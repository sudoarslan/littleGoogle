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
		int doc_id = database.urlMapTable.getEntry(url);
		if(doc_id == -1)
			throw new Exception("Link not found, cannot insert word to index");

		HashSet<String> unique = new HashSet<String>(words);

		for(String word: unique)
		{
			int freq = Collections.frequency(words, word);
			database.invertedIndex.updateEntry(word, str(doc_id), str(freq));

			int word_id = database.wordMapTable.appendEntry(word);
			database.forwardIndex.updateEntry(str(doc_id), str(word_id), str(freq));
		}
	}

	public void updateLinkIndex(String url, Vector<String> links) throws Exception
	{
		int url_id = database.urlMapTable.appendEntry(url);

		database.linkIndex.removeRow(str(url_id));

		int link_id = 0;
		for(String link: links)
			database.linkIndex.appendEntry(str(url_id), str(link_id), links.elementAt(link_id++));
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
			//Initialize
			System.out.println("Initializing..");
			Crawler crawler = new Crawler();

			for(int i = 0; i < MAX_CRAWLED_PAGES && !crawler.isEmpty(); i ++)
			{
				//Pops first element, i.e. BFS
				System.out.print("Website " + i + ": ");
				String url = crawler.getURL();
				System.out.println(url);

				//Extract links
				crawler.updateLinkIndex(url, crawler.extractLinks());
				//Extract words
				crawler.updateWordIndex(url, crawler.extractWords());	
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

