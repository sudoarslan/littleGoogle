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

	// Appends entry: Create or append
	public void appendEntry(String key, String id, String value) throws IOException
	{
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
	public void updateEntry(String key, String id, String value) throws IOException
	{
		// Search query for document id(int x)
		String column = Identifier + id;
		// Get the key
		String content = (String)Hashtable.get(key);
		// Append to the entry if not found
		if(content == null)
		{
			appendEntry(key, id, value);
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
			appendEntry(key, id, value);
			return;
		}

		content = Arrays.toString(entry).replaceAll(",", "").replaceAll("\\[|\\]", "");
		Hashtable.put(key, content);
	}

	// Retrieve particular entry value
	public String getEntry(String key, String id) throws IOException
	{
		String column = Identifier + id;
		String content = (String)Hashtable.get(key);
		String[] entry = content.split("\\s+");
		for(int index = 0; index < entry.length; index++)
			if(entry[index].equals(column))
				return entry[index + 1];

		//Entry not found
		return null;
	}

	// Retrieve all entries
	public String getAllEntries(String key) throws IOException
	{
		String content = (String)Hashtable.get(key);

		return content;
	}

	// Removes entire row
	public void removeRow(String key) throws IOException
	{
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
