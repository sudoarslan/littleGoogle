import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class History
{
	// save query history
	private RecordManager recman;
	public HTree history;

	private static final int TOP_K_RESULTS = 50;

	History() throws Exception
    {
		recman = RecordManagerFactory.createRecordManager("queryDB");
		history = LoadOrCreate("history");
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

	public void addEntry(String value) throws IOException{
		Integer id = (Integer)history.get(value);
        // if there is already an item, do nothing
		if(id != null);

		// current time as ID
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = sdf.format(date);
		history.put(formattedDate, value);
	}

	public void printAll() throws IOException{
		// iterate through all keys
        FastIterator iter = history.keys();

		String key;
        while( (key = (String)iter.next())!=null)
        {
            // get and print the content of each key
            System.out.println(key + " : " + history.get(key));
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
