package lg;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import java.util.Date;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.File;
import java.io.InputStream;
import java.util.Scanner;

public class Test
{
	// Hashtable filenames
	private static final String[] HASHTABLE_NAME = {"inverted", "forward", "link", "word", "url"};

	private RecordManager recman;
	public Index 	invertedIndex;
	public Index	forwardIndex;
	public Index	linkIndex;
	public MapTable	wordMapTable;
	public MapTable	urlMapTable;

	Test(String recordmanager) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);

		invertedIndex = new Index(LoadOrCreate(HASHTABLE_NAME[0]), "doc");
		forwardIndex  = new Index(LoadOrCreate(HASHTABLE_NAME[1]), "word");
		linkIndex	  = new Index(LoadOrCreate(HASHTABLE_NAME[2]), "link");
		wordMapTable  = new MapTable(LoadOrCreate(HASHTABLE_NAME[3]), LoadOrCreate("inverted_" + HASHTABLE_NAME[3]));
		urlMapTable	  = new MapTable(LoadOrCreate(HASHTABLE_NAME[4]), LoadOrCreate("inverted_" + HASHTABLE_NAME[4]));
	}

	private HTree LoadOrCreate(String hashtable_name) throws IOException
	{
		long recid = recman.getNamedObject(hashtable_name);
		if(recid != 0)
		{
			System.out.println("Hashtable found, id: " + recid);
			return HTree.load(recman, recid);
		}
		else
		{
			HTree hashtable = HTree.createInstance(recman);
			recid = hashtable.getRecid();
			recman.setNamedObject(hashtable_name, recid);
			System.out.println("Hashtable not found, new id: " + recid);
			return hashtable;
		}
	}

	public void printAll() throws IOException
	{
		PrintWriter writer = new PrintWriter("spider_result.txt", "UTF-8");
		// writer.println("The first line");
 	// 	writer.println("The second line");

		for (int i = 0 ; i < 30; i++){
			String url_s = urlMapTable.getEntry(i);

			// page title
			InputStream response = new URL(url_s).openStream();
	   		Scanner scanner = new Scanner(response);
	   		String responseBody = scanner.useDelimiter("\\A").next();
	   		writer.println(responseBody.substring(responseBody.indexOf("<title>") + 7, responseBody.indexOf("</title>")));
			writer.println(url_s);

			// last modified date and size
			URL url = new URL(url_s);
			URLConnection conn = url.openConnection();
			Date lastModified = new Date(conn.getLastModified());
			int size = conn.getContentLength();
			writer.println(lastModified+ ", "+ size);

			// keywords and frequencyies
			Vector<Pair> word_freq = forwardIndex.getAllEntriesId(i);

			for(int j = 0; j < word_freq.size()	; j++)
				writer.format("%s %s;", wordMapTable.getEntry(word_freq.elementAt(j).Key), word_freq.elementAt(j).Value);

			/*if (word_freq != null){
				String[] pairs = word_freq.split("\\s+");
				for(int j = 0; j < pairs.length; j += 2){
					int word_id = Integer.parseInt(pairs[j].substring(4));
					String freq = pairs[j+1];
					String word = wordMapTable.getEntry(word_id);
					writer.format("%s %s;", word, freq);
				}
				writer.println("");
			}*/

			// child links
			Vector<Pair> child_links = linkIndex.getAllEntriesId(i);

			for(int j = 0; j < child_links.size(); j++)
				writer.println(urlMapTable.getEntry(child_links.elementAt(j).Value));

			/*if (child_links != null){
				String[] link_pairs = child_links.split("\\s+");
				for(int j = 0; j < link_pairs.length; j += 2){
					writer.println(link_pairs[j+1]);
				}
			}*/

			writer.println("-------------------------------------------------------------------------------------------");
			}
		writer.close();
	}

	public static void main(String[] args)
	{
		try
		{
			Test test = new Test("indexDB");
			System.out.println("Running test...");
			test.printAll();
			System.out.println("End of test");
		}
		catch(IOException ex)
		{
			System.err.println(ex.toString());
		}

	}
}
