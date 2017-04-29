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

	public String Str(double value)
	{
		return Double.toString(value);
	}

	public double Doub(String value)
	{
		return Double.parseDouble(value);
	}

	public int Int(String value)
	{
		return Integer.parseInt(value);
	}

	public String underscoreToSpace(String text)
	{
		return text.replaceAll("_"," ");
	}

	public void appendEntry(int ikey, int iid, int ivalue) throws IOException
	{
		appendEntry(ikey, iid, Str(ivalue));
	}

	public void appendEntry(int ikey, int iid, double dvalue) throws IOException
	{
		appendEntry(ikey, iid, Str(dvalue));
	}

	// Appends entry: Create or append
	public void appendEntry(int ikey, int iid, String value) throws IOException
	{
		String key = Str(ikey), id = Str(iid);
		// Get the key
		String content = (String)Hashtable.get(key);
		// Create a new entry if not found; Append to the entry if found
		if(content == null)
			content = Identifier + id + " " + value;
		else
			content += " " + Identifier + id + " " + value;

		Hashtable.put(key, content);
	}

	public void updateEntry(int ikey, int iid, int ivalue) throws IOException
	{
		updateEntry(ikey, iid, Str(ivalue));
	}

	public void updateEntry(int ikey, int iid, double dvalue) throws IOException
	{
		updateEntry(ikey, iid, Str(dvalue));
	}

	// Updates entry:
	public void updateEntry(int ikey, int iid, String value) throws IOException
	{
		String key = Str(ikey), id = Str(iid);
		// Search query for document id(int x)
		String column = Identifier + id;
		// Get the key
		String content = (String)Hashtable.get(key);
		// Append to the entry if not found
		if(content == null)
		{
			appendEntry(ikey, iid, value);
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
			appendEntry(ikey, iid, value);
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
	public Vector<Pair> getAllEntriesId(int ikey) throws IOException
	{
		String key = Str(ikey);
		String value = (String)Hashtable.get(key);

		if(value == null)
			return null;

		Vector<Pair> freq = new Vector<Pair>();

		String[] list = value.split("\\s+");
		for(int i = 0; i < list.length; i += 2)
			freq.add(new Pair(Int(list[i].replaceAll(Identifier, "")), Int(list[i + 1])));

		return freq;
	}

	public Vector<FPair> getAllEntriesVSM(int ikey) throws IOException
	{
		String key = Str(ikey);
		String value = (String)Hashtable.get(key);

		if(value == null)
			return null;

		Vector<FPair> weight = new Vector<FPair>();
		String[] list = value.split("\\s+");
		for(int i = 0; i < list.length; i += 2)
			weight.add(new FPair(Int(list[i].replaceAll(Identifier, "")), Doub(list[i + 1])));

		return weight;
	}

	// To be tested
	// Get the metas for one single document
	public Vector<String> getAllEntriesMeta(int ikey) throws IOException
	{
		String key = Str(ikey);
		String value = (String)Hashtable.get(key);

		if(value == null)
			return null;

		Vector<String> metas = new Vector<String>();
		String[] list = value.split("\\s+");
		// Recover the underscore save handler
		for(int i = 1; i < list.length; i += 2)
			metas.add(underscoreToSpace(list[i]));

		return metas;
	}


	public Vector<String> getAllEntriesChildLink(int ikey) throws IOException
	{
		String key = Str(ikey);
		String value = (String)Hashtable.get(key);

		if(value == null)
			return null;

		Vector<String> childLinks = new Vector<String>();
		String[] list = value.split("\\s+");

		for(int i = 1; i < list.length; i += 2)
			childLinks.add(list[i]);

		return childLinks;
	}
	

	public Vector<String> getAllKeys() throws IOException {
		Vector<String> allKeys = new Vector<String>();
		FastIterator iter = Hashtable.keys();

        String key;
        while( (key = (String)iter.next())!=null)
        {
            allKeys.add(key);
        }
		return allKeys;
	}



	// Removes entire row
	public void removeRow(int ikey) throws IOException
	{
		String key = Str(ikey);
		Hashtable.remove(key);
	}

	public void removeAll() throws IOException
	{
		FastIterator iter = Hashtable.keys();
		Vector<String> keys = new Vector<String>();
		String key;
		System.out.println("KEY");
		while( ( key = (String)iter.next() ) != null ){
			keys.add(key);
		}
		for(String deleteKey : keys){
			removeRow(Int(deleteKey));
		}
		System.out.println("finish");
	}

	public void printAll() throws IOException
	{
		FastIterator iter = Hashtable.keys();
		String key;
		System.out.println("KEY");
		while( ( key = (String)iter.next() ) != null )
			System.out.println(key + " = " + (String)Hashtable.get(key));
		System.out.println("finish");
	}
}
