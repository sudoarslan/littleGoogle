import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class Rank
{
	private RecordManager recman;
	public HTree rank;
	private Database database;

    private double d = 0.85;
    private int iterationNum = 50;

	Rank() throws Exception
    {
		database = new Database();
		recman = RecordManagerFactory.createRecordManager("indexDB");
		rank = LoadOrCreate("rank");
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

    public int Int(String value)
	{
		return Integer.parseInt(value);
	}

	public double Double(String value)
	{
		return Double.parseDouble(value);
	}

	public String Str(int value)
	{
		return Integer.toString(value);
	}

	public String Str(double value)
	{
		return Double.toString(value);
	}

	public void addEntry(String key, double value) throws IOException{
		Double content = (Double)rank.get(key);
        // if there is already an item, do nothing
		if(content != null);
		rank.put(key, value);
	}

	public void updateEntry(String key, double value) throws IOException{
		Double content = (Double)rank.get(key);

		if (content == null)
			addEntry(key, value);
		else{
			rank.remove(key);
			addEntry(key, value);
		}
	}

    public double pageRank(double d, Vector<Pair> allParents) throws IOException{
        double value = 1-d;

        for (Pair parent: allParents){
			int key = parent.getValue();
			String skey = Str(key);
            Vector<Pair> out = database.linkIndex.getAllEntriesId(key);
            int capcity = out.size();
			Double previous_rank = (Double)rank.get(skey);
            value += d * (previous_rank/capcity);
        }
        return value;
    }

    public void calculateAll() throws IOException{
		for (int i = 0; i < iterationNum; i++)
        {
			// loop through all documents(urls)
        	Vector<String> allKeys = database.urlMapTable.getAllKeys(true);
        	for (String key : allKeys)
        	{
            	// get its parents
            	Vector<Pair> allParents = database.parentIndex.getAllEntriesId(Int(key));
				double rankValue = pageRank(d, allParents);
				updateEntry(key, rankValue);
        	}
		}
    }

    // initialize all rank to 1
    public void initializeAll() throws IOException{
		Vector<String> allKeys = database.urlMapTable.getAllKeys(true);
        for (String key : allKeys){
			addEntry(key, 1);
		}
    }

	public void printAll() throws IOException{
		// iterate through all keys
        FastIterator iter = rank.keys();

		String key;
        while( (key = (String)iter.next())!=null)
        {
            // get and print the content of each key
            System.out.println(key + " : " + rank.get(key));
        }
	}

	// Save and confirm the changes of the database
	public void Finalize() throws IOException
	{
		recman.commit();
		recman.close();
		System.out.println("Closed");
	}
}
