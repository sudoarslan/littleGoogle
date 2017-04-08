import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

import java.util.Arrays;

public class Index
{
	private HTree Hashtable;

	private String Identifier;

	Index(HTree hash_table, String identifier)
	{
		Hashtable = hash_table;
		Identifier = identifier;
	}

	public String Str(int value)
	{
		return Integer.toString(value);
	}

	public int Int(String value)
	{
		return Integer.parseInt(value);
	}

	// Appends entry: Create or append
	public void appendEntry(int ikey, int iid, int ivalue) throws IOException
	{
		String key = Str(ikey), id = Str(iid), value = Str(ivalue);
		// Get the key
		String content = (String)Hashtable.get(key);
		// Create a new entry if not found; Append to the entry if found
		if(content == null)
			content = Identifier + id + " " + value;
		else
			content += " " + Identifier + id + " " + value;

		Hashtable.put(key, content);
	}

	// Updates entry: 
	public void updateEntry(int ikey, int iid, int ivalue) throws IOException
	{
		String key = Str(ikey), id = Str(iid), value = Str(ivalue);
		// Search query for document id(int x)
		String column = Identifier + id;
		// Get the key
		String content = (String)Hashtable.get(key);
		// Append to the entry if not found
		if(content == null)
		{
			appendEntry(ikey, iid, ivalue);
			return;
		}

		// Split value into multiple entries with whitespace regex
		String[] entry = content.split("\\s+");
		int index;
		for(index = 0; index < entry.length; index++)
		{
			if(entry[index].equals(column))
			{
				entry[index + 1] = value;
				break;
			}
		}
		if(index == entry.length)
		{
			appendEntry(ikey, iid, ivalue);
			return;
		}

		content = Arrays.toString(entry).replaceAll(",", "").replaceAll("\\[|\\]", "");
		Hashtable.put(key, content);
	}

	// Retrieve particular entry value
	public int getEntry(int ikey, int iid) throws IOException
	{
		String key = Str(ikey), id = Str(iid);
		String column = Identifier + id;
		String content = (String)Hashtable.get(key);
		String[] entry = content.split("\\s+");
		for(int index = 0; index < entry.length; index++)
			if(entry[index].equals(column))
				return Int(entry[index + 1]);

		//Entry not found
		return -1;
	}

	// Retrieve all entries
	public Vector<Pair> getAllEntries(int ikey) throws IOException
	{
		String key = Str(ikey);
		String value = (String)Hashtable.get(key);

		if(value == null)
			return new Vector<Pair>();

		Vector<Pair> freq = new Vector<Pair>();

		String[] list = value.split("\\s+");
		for(int i = 0; i < list.length; i += 2)
			freq.add(new Pair(Int(list[i].replaceAll(Identifier, "")), Int(list[i + 1])));

		return freq;
	}

	// Removes entire row
	public void removeRow(int ikey) throws IOException
	{
		String key = Str(ikey);
		Hashtable.remove(key);
	}

	public void printAll() throws IOException
	{
		FastIterator iter = Hashtable.keys();
		String key;
		while( (key=(String)iter.next()) != null )
			System.out.println(key + " = " + Hashtable.get(key));
	}
}
