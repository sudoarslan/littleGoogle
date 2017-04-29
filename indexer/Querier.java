import java.util.*;
import java.io.*;
import java.lang.Math;
import java.util.Scanner;
import java.util.regex.*;

public class Querier
{
	private Database database;
	private StopStem stopStem;
	private History history;

	private static final int TOP_K_RESULTS = 50;

	Querier() throws Exception
	{
		database = new Database();
		stopStem = new StopStem();
		history = new History();
	}

	public double idf(int word_id) throws Exception
	{
		int N = database.urlMapTable.getMaxId();
		int df = database.invertedIndex.getAllEntriesId(word_id).size();
		return (Math.log(N) - Math.log(df)) / Math.log(2.0);
	}

	public static void printlnWithLabel(String label, String text) throws Exception
	{
		System.out.println(label + ": " + text);
	}

	public static void printlnWithLabel(String label, int num) throws Exception
	{
		System.out.println(label + ": " + String.valueOf(num));
	}

	public static void printlnWithLabelWPair(String label, Vector<WPair> vec) throws Exception
	{
		System.out.print(label + ": ");
		for (WPair wp : vec){
			System.out.print(wp.Key + "(" + String.valueOf(wp.Value) + ") ");
		}
		System.out.println();
	}

	public static void printlnWithLabel(String label, Vector<String> vec) throws Exception
	{
		System.out.println(label + ": " + vec.toString());
	}

	/*
	public Vector<FPair> vecStringToVecFPair(Vector<String> vec_of_string) throws Exception
	{
		if(vec_of_string == null)
			return null;

		Vector<FPair> result = new Vector<FPair>();
		//String[] list = value.split("\\s+");
		for(int i = 0; i < vec_of_string.length; i += 2)
			result.add(new FPair(Int(list[i].replaceAll(Identifier, "")), Doub(list[i + 1]))); 

		return result;
	}
	*/

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

		System.out.print("Quoted query: ");
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

	// TODO: get the title of a document
	/*
	public Vector<FPair> TitleWeight(int doc_id) throws Exception
	{
		//Get title of a document
		return database.metaIndex.getAllEntriesMeta(doc_id)[0];
	}
	*/

	public Vector<PageInfo> NaiveSearch(String query, Integer topK) throws Exception
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
			
			//System.out.println(String.valueOf() + String.valueOf(score));

			// TODO: create the other required set-up
			/*
			Vector<FPair> title_weight = TitleWeight(i);

			score += CosSim(n_query_weight, title_weight);
			for(Vector<FPair> query_weight : q_query_weight)
				score += QCosSim(query_weight, title_weight);
			*/

			scores.add(new FPair(i, score));
		}


		// All search results in FPAir format
		Vector<FPair> list = FPair.TopK(scores, topK);

		// All search results
		Vector<PageInfo> results = new Vector<PageInfo>();


		for(FPair p : list){
			// Single search result
			PageInfo result = new PageInfo();
			// Get metadata
			Vector<String> resultMeta = database.metaIndex.getAllEntriesMeta(p.Key);
			// Get keyword pairs
			Vector<Pair> resultKeywordFreq = database.forwardIndex.getAllEntriesId(p.Key);
			for(int j = 0; j < 5; j++){
				WPair keywordPair = new WPair(database.wordMapTable.getEntry(resultKeywordFreq.get(j).Key), 
					resultKeywordFreq.get(j).Value);
				result.KeywordVector.add(keywordPair);
			}
			// Get title, url, date and size
			result.Title = resultMeta.get(0);
			result.Url = database.urlMapTable.getEntry(p.Key);
			result.LastModifiedDate = resultMeta.get(1);
			result.SizeOfPage = Integer.parseInt(resultMeta.get(2));

			// Get child links
			result.ChildLinkVector = database.linkIndex.getAllEntriesChildLink(p.Key);

			results.add(result);
		}
			
			//links.add(database.urlMapTable.getEntry(p.Key));

		System.out.println("\nSearch Result:");
		return results;
		//return links;
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
			History history = new History();

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


				// Print searching result by PageInfo
				for(PageInfo doc : querier.NaiveSearch(query, top_k)){
					printlnWithLabel("Title", doc.Title);
					printlnWithLabel("Url", doc.Url);
					printlnWithLabel("Last Modified Date", doc.LastModifiedDate);
					printlnWithLabel("Size of Page", doc.SizeOfPage);
					printlnWithLabelWPair("Keywords", doc.KeywordVector);
					printlnWithLabel("Child Links", doc.ChildLinkVector);
					System.out.println("---------------------");
				}

				// Add to query history
				history.addEntry(query);
				System.out.println("\nSearch history: ");
				history.printAll();

			}
		}
		catch (Exception e)
		{
			System.err.println("Error1");
			System.err.println(e.toString());
		}
	}
}
