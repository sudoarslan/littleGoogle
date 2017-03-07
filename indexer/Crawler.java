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

	//Imported packages for pipeline process
	private InvertedIndex index;
	private StopStem stopStem;

	//Crawl for num_pages pages, default is 30
	private static final int MAX_CRAWLED_PAGES = 30;
	// Set the crawling target domain
	private static final String TAGRET_CRAWLED_DOMAIN = "http://www.cs.ust.hk";
	// Set stopword resource address
	private static final String STOPWORD_SOURCE_DIRCTORY = "stopwords.txt";
	

	Crawler(String _url, InvertedIndex _index, StopStem _stopStem)
	{
		url = new Vector<String>();
		url.add(_url);

		index = _index;
		stopStem = _stopStem;
	}

	public boolean isEmpty()
	{
		return url.size() == 0;
	}

	public String popURL()
	{
		return url.remove(0);
	}

	public void finalize() throws Exception
	{
		index.finalize();
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
	public void insertIndex(int i, Vector<String> words) throws Exception
	{
		HashSet<String> unique = new HashSet<String>(words);

		for(String word: unique)
			index.updateEntry(word, i, Collections.frequency(words, word));
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
			Crawler crawler = new Crawler(TAGRET_CRAWLED_DOMAIN, 
							  new InvertedIndex("indexDB", "htl"), 
							  new StopStem(STOPWORD_SOURCE_DIRCTORY));

			for(int i = 0; i < MAX_CRAWLED_PAGES; i ++)
			{
				System.out.println("Website" + i);
				//Extract words
				System.out.println("Extracting words..");
				crawler.insertIndex(i, crawler.extractWords());

				//Extract links
				System.out.println("Extracting links..");
				crawler.extractLinks();

				//Pop the first url, ie BFS
				System.out.println(crawler.popURL());
				System.out.println("\n");
			}

			//Save the database
			crawler.finalize();
		}
		catch (Exception e)
		{
			System.err.println("Error");
			System.err.println(e.toString());
		}
	}
}

