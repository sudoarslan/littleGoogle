import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.io.Serializable;

import java.util.*;

public class MapTable
{
	private HTree ForwardHashtable;
	private HTree BackwardHashtable;

	MapTable(HTree forward_hash_table, HTree backward_hash_table) throws IOException
	{
		ForwardHashtable = forward_hash_table;
		BackwardHashtable = backward_hash_table;

		// max_id is default as 0
		if(BackwardHashtable.get("max_id") == null)
			BackwardHashtable.put("max_id", 0);
	}

	public int getMaxId() throws IOException
	{
		return (Integer)BackwardHashtable.get("max_id");
	}

	// Appends entry, assume only crawler uses this, so no foward insertion
	public int appendEntry(String value) throws IOException
	{
		Integer id = (Integer)BackwardHashtable.get(value);
		if(id != null)
			return id;

		// max_id is the NEXT ID to be inserted
		Integer max_id = (Integer)BackwardHashtable.get("max_id");
		ForwardHashtable.put(max_id, value);
		BackwardHashtable.put(value, max_id);
		BackwardHashtable.put("max_id", max_id + 1);
		return max_id;
	}

	// Updates entry, for the sake of consistency this method exists
	public void updateEntry(String value) throws IOException
	{
		appendEntry(value);
	}

	// Retrieve particular entry value
	public String getEntry(int key) throws IOException
	{
		return (String)ForwardHashtable.get(key);
	}

	public int getKey(String value) throws IOException
	{
		Integer key = (Integer)BackwardHashtable.get(value);
		return key == null? -1: key;
	}

	// Removes row given key
	public void removeRow(int key) throws IOException
	{
		String value = (String)ForwardHashtable.get(key);
		ForwardHashtable.remove(key);
		BackwardHashtable.remove(value);
	} 

	// Removes row given value
	public void removeRow(String value) throws IOException
	{
		Integer key = (Integer)BackwardHashtable.get(value);
		ForwardHashtable.remove(key);
		BackwardHashtable.remove(value);
	}

	public Vector<Integer> valueToKey(Vector<String> values) throws IOException
	{
		Vector<Integer> keys = new Vector<Integer>();
		for(String v : values)
		{
			int key = getKey(v);
			if(key != -1)
				keys.add(key);
		}

		return keys;
	}

	public Vector<String> keyToValue(Vector<Integer> keys) throws IOException
	{
		Vector<String> values = new Vector<String>();
		for(int k : keys)
		{
			String value = getEntry(k);
			if(value != null)
				values.add(value);
		}

		return values;
	}

	public void printAll(boolean forward) throws IOException
	{
		if(forward)
		{
			FastIterator iter = ForwardHashtable.keys();
			Integer key;
			while( (key=(Integer)iter.next()) != null )
				System.out.println(key + " = " + ForwardHashtable.get(key));
		}
		else
		{
			FastIterator iter = BackwardHashtable.keys();
			String value;
			while( (value=(String)iter.next()) != null )
				System.out.println(value + " = " + BackwardHashtable.get(value));

		}
	}    
}
