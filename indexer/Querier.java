import java.util.*;
import java.io.*;
import java.lang.Math;
import java.util.Scanner;
import java.util.regex.*;

public class Querier
{
	private Database database;
	private StopStem stopStem;

	private static final int TOP_K_RESULTS = 50;

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

	public boolean HasSequence(Vector<FPair> query, Vector<FPair> words) throws Exception
	{
		int index = 0;

		for(int i = 0; i < query.size(); i++)
		{
			//Get word id for the next query word
			int qword = query.get(i).Key;

			//Searche in the remaining document word list to see if the next query word is there
			//If it is, do the remaining searching from there
			int pos;
			for(pos = index + 1; pos < words.size(); pos++)
				if(qword == words.get(pos).Key)
				{
					index = pos;
					break;
				}
			//the next query word does not exist, so there is no exact phrase match
			if(pos == words.size())
				return false;
		}

		return true;
	}

	public double CosSim(Vector<FPair> s1, Vector<FPair> s2) throws Exception
	{
		if(s1.size() == 0.0 || s2.size() == 0.0)
			return 0.0;

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

	public double QCosSim(Vector<FPair> s1, Vector<FPair> s2) throws Exception
	{
		if(s1.size() == 0.0 || s2.size() == 0.0)
			return 0.0;
	
		double score = 0;
		if(HasSequence(s1, s2))
			for(int i = 0; i < s1.size(); i++)
				score += Math.pow(s1.get(i).Value, 2);

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

		//filter stopwords and stem for normal and qouted queries
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
			query_weight.add(new FPair(id, 1.0));//Collections.frequency(query_id, id) * idf(id))); <-- Can change to tf-idf anytime

		return query_weight;
	}

	public Vector<Vector<FPair>> QuoteWeight(String query) throws Exception
	{
		//Vector of Vector since there might be more than 1 quote
		Vector<Vector<FPair>> query_weight = new Vector<Vector<FPair>>();

		//Find substring inside " " and extract them
		Pattern pattern = Pattern.compile("\"(.*?)\"");
		Matcher matcher = pattern.matcher(query);
		Vector<String[]> quote = new Vector<String[]>();
		while(matcher.find())
			quote.add(matcher.group(1).replaceAll("[^\\w\\s]|_", "").trim().toLowerCase().split(" "));

		//Stem list of strings in each quote
		Vector<Vector<String>> q_query = new Vector<Vector<String>>();
		for(String[] q : quote)
			q_query.add(stopStem.stopAndStem(q));

		System.out.print("Quoted  query: ");
		for(Vector<String> q : q_query)
		{
			for(String s : q)
				System.out.print(s + " ");
			System.out.print("\t");
		}
		System.out.println("");

		//Convert list of strings to list of word_id for each quote
		Vector<Vector<Integer>> query_id = new Vector<Vector<Integer>>();
		for(Vector<String> q : q_query)
			query_id.add(database.wordMapTable.valueToKey(q));	

		//create weight vector for each quote
		for(Vector<Integer> id : query_id)
		{
			Vector<FPair> weight = new Vector<FPair>();
			HashSet<Integer> unique_id = new HashSet<Integer>(id);
			for(int u_id : unique_id)
				weight.add(new FPair(u_id, 1.0));//Collections.frequency(query_id, id) * idf(id))); <-- Can change to tf-idf anytime
			query_weight.add(weight);
		}

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
		Vector<FPair> n_query_weight = QueryWeight(query);
		Vector<Vector<FPair>> q_query_weight = QuoteWeight(query);

		//Iterate through all webpages
		int max_doc = database.urlMapTable.getMaxId();

		Vector<FPair> scores = new Vector<FPair>();
		for(int i = 0; i < max_doc; i++)
		{
			//Get tf-idf vector of a document
			Vector<FPair> doc_weight = DocWeight(i);
			//if it doesn't exist, then the document is not crawled
			if(doc_weight == null)
				continue;

			//Summation of normal query score and quoted query score
			double score = CosSim(n_query_weight, doc_weight);
			for(Vector<FPair> query_weight : q_query_weight)
				score += QCosSim(query_weight, doc_weight);

			scores.add(new FPair(i, score));
		}

		Vector<FPair> list = FPair.TopK(scores, topK);

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

			int top_k = TOP_K_RESULTS;

			if(args.length > 0)
				top_k = Integer.parseInt(args[0]);

			System.out.println("Displaying Top-" + top_k + " results");

			while(true)
			{
				System.out.print("\nSearch for: ");
				String query = scanner.nextLine();

				if(query.equals("quit"))
					break;

				for(String s : querier.NaiveSearch(query, top_k))
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
