import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.io.Serializable;

import java.util.Arrays;

public class MapTable
{
	private HTree Hashtable;

	MapTable(HTree hash_table) throws IOException
	{
		Hashtable = hash_table;
		if(Hashtable.get("max_id") == null)
			Hashtable.put("max_id", 0);
	}

	//Appends entry
	public int appendEntry(String key) throws IOException
	{
		Integer max_id = (Integer)Hashtable.get("max_id");
		Hashtable.put(key, max_id);
		Hashtable.put("max_id", max_id + 1);
		return max_id;
	}

	//Updates entry, for the sake of consistency this method exists
	public void updateEntry(String key, int value) throws IOException
	{
		Hashtable.put(key, value);
	}

	//Retrieve particular entry value
	public int getEntry(String key) throws IOException
	{
		Integer value = (Integer)Hashtable.get(key);
		return value == null? -1: value;
	}

	//Removes entire row
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
