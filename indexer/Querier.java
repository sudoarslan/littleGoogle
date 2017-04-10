import java.util.*;
import java.io.*;
import java.lang.Math;

public class Querier
{
	private Database database;
	private StopStem stopStem;

	Querier() throws Exception
	{
		database = new Database();
		stopStem = new StopStem();
	}

	public double idf(int word_id) throws Exception
	{
		int N = database.urlMapTable.getMaxId();
		int df = database.invertedIndex.getAllEntries(word_id).size();
		return (Math.log(N) - Math.log(df)) / Math.log(2.0);
	}

	public double CosSim(Vector<FPair> s1, Vector<FPair> s2)
	{
		double score = 0;
		for(int i = 0; i < s1.size(); i++)
			for(int j = 0; j < s2.size(); j++)
				if(s1.get(i).Key == s2.get(i).Key)
					score += s1.get(i).Value * s2.get(j).Value;

		double dist_s1 = 0.0;
		for(int i = 0; i < s1.size(); i++)
			dist_s1 += Math.pow(s1.get(i).Value, 2);

		double dist_s2 = 0.0;
		for(int j = 0; j < s2.size(); j++)
			dist_s2 += Math.pow(s2.get(j).Value, 2);

		System.out.println(score / dist_s1 / dist_s2);

		return score / dist_s1 / dist_s2;
	}

	public Vector<FPair> QueryWeight(String query) throws Exception
	{
		Vector<FPair> query_weight = new Vector<FPair>();

		//split and filter query string to vector space
		String[] s_query = query.replaceAll("[^\\w\\s]|_", "").trim().toLowerCase().split(" ");

		//filter stopwords and stem
		Vector<String> p_query = stopStem.stopAndStem(s_query);

		//convert words to word_ids, skip if not found in database
		Vector<Integer> query_id = database.wordMapTable.valueToKey(p_query);

		//create tf for query
		HashSet<Integer> unique_id = new HashSet<Integer>(query_id);
		for(int id : unique_id)
			query_weight.add(new FPair(id, Collections.frequency(query_id, id) * idf(id)));

		return query_weight;
	}

	public Vector<FPair> DocWeight(int doc_id) throws Exception
	{
		//Get all words of a document
		Vector<Pair> doc = database.forwardIndex.getAllEntries(doc_id);
		Vector<FPair> doc_weight = new Vector<FPair>();
		for(int j = 0; j < doc.size(); j++)
		{
			Pair word = doc.get(j);
			//length of entries per word = df of the word
			doc_weight.add(new FPair(word.Key, word.Value * idf(word.Key)));
		}

		return doc_weight;
	}

	public Vector<String> NaiveSearch(String query, Integer topK) throws Exception
	{
		//Converts query into VSM of weights
		Vector<FPair> query_weight = QueryWeight(query);

		//Converts each documents into VSM of weightsf
		Vector<Vector<FPair>> doc_weight = new Vector<Vector<FPair>>();

		int max_doc = database.urlMapTable.getMaxId();
		for(int i = 0; i < max_doc; i++)
			doc_weight.add(DocWeight(i));

		Vector<FPair> score = new Vector<FPair>();
		for(int i = 0; i < max_doc; i++)
			score.add(new FPair(i, CosSim(query_weight, doc_weight.get(i))));

		Vector<FPair> list = FPair.TopK(score, topK);

		Vector<String> links = new Vector<String>();
		for(FPair p : list)
			links.add(database.urlMapTable.getEntry(p.Key));

		return links;
	}

	public static void main(String[] args)
	{
		if(args.length == 0)
			return;

		try
		{
			Querier querier = new Querier();

			for(String s : querier.NaiveSearch(args[0], 10))
				System.out.println(s);
		}
		catch (Exception e)
		{
			System.err.println("Error");
			System.err.println(e.toString());
		}
	}
}
