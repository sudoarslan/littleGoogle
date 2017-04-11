import java.util.*;
import java.io.*;
import java.lang.Math;
import java.util.Scanner;

public class Querier
{
	private Database database;
	private StopStem stopStem;

	private static final int TOP_K_RESULTS = 10;

	Querier() throws Exception
	{
		database = new Database();
		stopStem = new StopStem();
	}

	public double idf(int word_id) throws Exception
	{
		int N = database.urlMapTable.getMaxId();
		int df = database.invertedIndex.getAllEntriesId(word_id).size();
		return (Math.log(N) - Math.log(df)) / Math.log(2.0);
	}

	public double CosSim(Vector<FPair> s1, Vector<FPair> s2) throws Exception
	{
		double score = 0;
		for(int i = 0; i < s1.size(); i++)
			for(int j = 0; j < s2.size(); j++)
				if(s1.get(i).Key == s2.get(j).Key)
					score += s1.get(i).Value * s2.get(j).Value;

		double dist_s1 = 0.0;
		for(int i = 0; i < s1.size(); i++)
			dist_s1 += Math.pow(s1.get(i).Value, 2);

		double dist_s2 = 0.0;
		for(int j = 0; j < s2.size(); j++)
			dist_s2 += Math.pow(s2.get(j).Value, 2);

		dist_s1 = Math.sqrt(dist_s1);
		dist_s2 = Math.sqrt(dist_s2);

		return score / dist_s1 / dist_s2;
	}

	public Vector<FPair> QueryWeight(String query) throws Exception
	{
		Vector<FPair> query_weight = new Vector<FPair>();

		//split and filter query string to vector space
		String[] s_query = query.replaceAll("[^\\w\\s]|_", "").trim().toLowerCase().split(" ");

		//filter stopwords and stem
		Vector<String> p_query = stopStem.stopAndStem(s_query);

		System.out.print("Stemmed query: ");
		for(String s : p_query)
			System.out.print(s + " ");
		System.out.println("");

		//convert words to word_ids, skip if not found in database
		Vector<Integer> query_id = database.wordMapTable.valueToKey(p_query);

		//create weight for query
		HashSet<Integer> unique_id = new HashSet<Integer>(query_id);
		for(int id : unique_id)
			query_weight.add(new FPair(id, 1.0));//Collections.frequency(query_id, id) * idf(id)));

		return query_weight;
	}

	public Vector<FPair> DocWeight(int doc_id) throws Exception
	{
		//Get all words of a document
		return database.vsmIndex.getAllEntriesVSM(doc_id);
	}

	public Vector<String> NaiveSearch(String query, Integer topK) throws Exception
	{
		//Converts query into VSM of weights
		Vector<FPair> query_weight = QueryWeight(query);

		int max_doc = database.urlMapTable.getMaxId();

		Vector<FPair> score = new Vector<FPair>();
		for(int i = 0; i < max_doc; i++)
		{
			Vector<FPair> doc_weight = DocWeight(i);
			if(doc_weight == null)
				continue;

			score.add(new FPair(i, CosSim(query_weight, doc_weight)));
		}

		Vector<FPair> list = FPair.TopK(score, topK);

		Vector<String> links = new Vector<String>();
		for(FPair p : list)
			links.add(database.urlMapTable.getEntry(p.Key));

		return links;
	}

	//Prints all websites containing any of the query words
	public void WordInDoc(String query) throws Exception
	{
		//split and filter query string to vector space
		String[] s_query = query.replaceAll("[^\\w\\s]|_", "").trim().toLowerCase().split(" ");

		//filter stopwords and stem
		Vector<String> p_query = stopStem.stopAndStem(s_query);

		//convert words to word_ids, skip if not found in database
		Vector<Integer> query_id = database.wordMapTable.valueToKey(p_query);

		//distinct()
		HashSet<Integer> unique_id = new HashSet<Integer>(query_id);
		for(int id : unique_id)
		{
			System.out.println(database.wordMapTable.getEntry(id) + ": ");

			Vector<Pair> entries = database.invertedIndex.getAllEntriesId(id);
			for(Pair entry : entries)
				System.out.println(database.urlMapTable.getEntry(entry.Key));

			System.out.println("\n\n");
		}
	}

	public static void main(String[] args)
	{
		try
		{
			Querier querier = new Querier();
			Scanner scanner = new Scanner(System.in);

			while(true)
			{
				System.out.print("Search for: ");
				String query = scanner.nextLine();
				
				if(query.equals("quit"))
					break;

				for(String s : querier.NaiveSearch(query, TOP_K_RESULTS))
					System.out.println(s);
			}
		}
		catch (Exception e)
		{
			System.err.println("Error1");
			System.err.println(e.toString());
		}
	}
}
