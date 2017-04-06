import org.htmlparser.beans.StringBean;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.beans.LinkBean;
import java.util.*;
import java.net.URL;
import java.io.*;

public class Crawler
{
	//URL queue
	private Vector<String> url;

	// Crawl for num_pages pages, default is 30
	private static final int MAX_CRAWLED_PAGES = 1000;
	// Set the crawling target domain
	private static final String TARGET_CRAWLED_DOMAIN = "http://www.cse.ust.hk";
	// Set the stopword resource path
	private static final String STOPWORD_SOURCE_DIRECTORY = "stopwords.txt";

	//Imported packages for pipeline process
	private Database database;
	private StopStem stopStem;

	Crawler() throws Exception
	{
		url = new Vector<String>();
		url.add(TARGET_CRAWLED_DOMAIN);

		database = new Database();

		stopStem = new StopStem(STOPWORD_SOURCE_DIRECTORY);
	}
	
	public boolean isEmpty()
	{
		return url.size() == 0;
	}

	public String getURL()
	{
		return url.firstElement();
	}

	public void Finalize() throws Exception
	{
		database.Finalize();
	}

	//Extract words from the first url in the vector
	public Vector<String> extractWords() throws Exception
	{
		Vector<String> words = new Vector<String>();
		StringBean bean = new StringBean();

		if(url.size() == 0)
			return words;

		bean.setURL(url.firstElement());
		bean.setLinks(false);
		String contents = bean.getStrings();
		StringTokenizer st = new StringTokenizer(contents);

		while (st.hasMoreTokens())
			words.add(st.nextToken());

		//All the text processing
		//Stop word removal, stemming, invalid word removal, to lower case
		Vector<String> stemmed = new Vector<String>();
		for(String word : words)
		{
			String lword = word.toLowerCase();
			if(!stopStem.isStopWord(lword) && lword.matches("[a-z]+"))
				stemmed.add(lword);
		}

		return stemmed;
	}

	//Find the occurence of each word in the string vector, save the frequency to the database
	public void updateWordIndex(String url, Vector<String> words) throws Exception
	{
		// Get the Document ID
		int doc_id = database.urlMapTable.getKey(url);
		if(doc_id == -1)
			throw new Exception("Link not found, cannot insert word to index");

		// Collect all the words in a Hash Set
		HashSet<String> unique = new HashSet<String>(words);

		// Iterate through all the words in the document
		for(String word: unique)
		{
			// Get the term frequency(tf) of the word
			int freq = Collections.frequency(words, word);

			// Insert the word into wordMapTable and get the word ID
			int word_id = database.wordMapTable.appendEntry(word);

			// Insert the word into Inverted File: [word, document ID, term frequency]
			database.invertedIndex.updateEntry(word_id, doc_id, freq);

			// Insert the word into the Forward Index: [document ID, word ID, term frequency]
			database.forwardIndex.updateEntry(doc_id, word_id, freq);
		}
	}

	// 
	public void updateLinkIndex(String url, Vector<String> links) throws Exception
	{
		// Insert the url into urlMapTable and get the url ID
		int url_id = database.urlMapTable.appendEntry(url);

		// Remove the out-dated url's child links data
		database.linkIndex.removeRow(url_id);

		// Iterate through all the child links of the url
		int index = 0;
		for(String link: links)
		{
			int link_id = database.urlMapTable.appendEntry(link);
			// Insert the child links into the Link Index: [url ID, link index, child link id]
			database.linkIndex.appendEntry(url_id, index++, link_id);
		}
	}

	//Extract all links in the website
	public Vector<String> extractLinks() throws Exception
	{
		Vector<String> link = new Vector<String>();
		LinkBean bean = new LinkBean();

		if(url.size() == 0)
			return link;

		bean.setURL(url.remove(0));
		URL[] urls = bean.getLinks();

		for (URL s : urls)
			link.add(s.toString());
	
		url.addAll(link);

		return link;
	}

	public static void main (String[] args)
	{
		try
		{
			// Initialization
			System.out.println("Initializing..");
			Crawler crawler = new Crawler();

			for(int i = 0; i < MAX_CRAWLED_PAGES && !crawler.isEmpty(); i ++)
			{
				// Pops first element, i.e. BFS
				System.out.print("Website " + i + ": ");
				String url = crawler.getURL();
				System.out.println(url);

				// Extract links: create Link Index
				crawler.updateLinkIndex(url, crawler.extractLinks());
				// Extract words: create Inverted Index & Forward Index
				crawler.updateWordIndex(url, crawler.extractWords());	
			}

			// Save the database
			crawler.Finalize();
		}
		catch (Exception e)
		{
			System.err.println("Error");
			System.err.println(e.toString());
		}
	}
}

