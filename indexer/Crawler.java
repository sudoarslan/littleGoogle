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
	private static final int MAX_CRAWLED_PAGES = 30;
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

	private String str(int v)
	{
		return Integer.toString(v);
	}

	public boolean isEmpty()
	{
		return url.size() == 0;
	}

	public String popURL()
	{
		return url.remove(0);
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
	public void updateWordIndex(int doc_id, Vector<String> words) throws Exception
	{
		HashSet<String> unique = new HashSet<String>(words);

		for(String word: unique)
		{
			int freq = Collections.frequency(words, word);
			database.invertedIndex.updateEntry(word, str(doc_id), str(freq));

			int word_id = database.wordMapTable.getEntry(word);
			if(word_id == -1)
				word_id = database.wordMapTable.appendEntry(word);
			database.forwardIndex.updateEntry(str(doc_id), str(word_id), str(freq));
		}
	}

	public void updateLinkIndex(String url, int doc_id) throws Exception
	{
		database.linkMapTable.appendEntry(url);
	}

	//Extract all links in the website
	public void extractLinks() throws Exception
	{
		Vector<String> result = new Vector<String>();
		LinkBean bean = new LinkBean();

		if(url.size() == 0)
			return;

		bean.setURL(url.remove(0));
		URL[] urls = bean.getLinks();

		for (URL s : urls)
			url.add(s.toString());
	}

	public static void main (String[] args)
	{
		try
		{
			//Initialize
			System.out.println("Initializing..");
			Crawler crawler = new Crawler();

			for(int i = 0; i < MAX_CRAWLED_PAGES; i ++)
			{
				System.out.print("Website " + i + ": ");
				//Extract words
				crawler.updateWordIndex(i, crawler.extractWords());

				//Extract links
				crawler.extractLinks();
				String url = crawler.popURL();
				crawler.updateLinkIndex(url, i);

				//Pop the first url, i.e. BFS
				System.out.println(url);
			}

			//Save the database
			crawler.Finalize();
		}
		catch (Exception e)
		{
			System.err.println("Error");
			System.err.println(e.toString());
		}
	}
}

