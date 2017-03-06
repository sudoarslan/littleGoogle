/* --
   COMP4321 Lab1 Exercise
   Student Name: xjian
   Student ID:
Section:
Email: xjian@ust.hk
*/

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

import java.util.Arrays;

public class InvertedIndex
{
	private RecordManager recman;
	private HTree hashtable;

	InvertedIndex(String recordmanager, String objectname) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		long recid = recman.getNamedObject(objectname);

		if (recid != 0)
			hashtable = HTree.load(recman, recid);
		else
		{
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( objectname, hashtable.getRecid() );
		}
	}


	public void finalize() throws IOException
	{
		System.out.println("Finalizing..");
		printAll();
		recman.commit();
		recman.close();                
	} 

	//Appends entry
	public void appendEntry(String word, int x, int y) throws IOException
	{
		String content = (String)hashtable.get(word);
		if (content == null)
			content = "doc" + x + " " + y;
		else
			content += " doc" + x + " " + y;

		hashtable.put(word, content);
	}

	//Updates entry
	public void updateEntry(String word, int x, int y) throws IOException
	{
		//Search query for document id(int x)
		String column = "doc" + x;
		String content = (String)hashtable.get(word);
		if(content == null)
		{
			appendEntry(word, x, y);
			return;
		}

		//Split value into multiple entries with whitespace regex
		String[] entry = content.split("\\s+");
		for(int index = 0; index < entry.length; index++)
		{
			if(entry[index].equals(column))
				entry[index + 1] = y + "";
		}
		content = Arrays.toString(entry).replaceAll(",", "").replaceAll("\\[|\\]", "");
		hashtable.put(word, content);
	}

	public void removeEntry(String word, int x, int y) throws IOException
	{
		String column = "doc" + x;
		String content = (String)hashtable.get(word); 
	}

	//Removes entry
	public void deleteEntry(String word) throws IOException
	{
		hashtable.remove(word);
	} 

	public void printAll() throws IOException
	{
		FastIterator iter = hashtable.keys();
		String key;
		while( (key=(String)iter.next()) != null )
			System.out.println(key + " = " + hashtable.get(key));
	}    

	public static void main(String[] args)
	{
		try
		{
			InvertedIndex index = new InvertedIndex("indexDB","ht1");

			System.out.println("Reading JDBM");

			index.printAll();
		}
		catch(IOException ex)
		{
			System.err.println(ex.toString());
		}

	}
}
