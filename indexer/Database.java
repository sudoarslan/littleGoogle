import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import java.io.IOException;

public class Database
{
	// Database filename
	private static final String DATABASE_NAME = "indexDB";
	// Hashtable filenames
	private static final String[] HASHTABLE_NAME = {"inverted", "forward", "link", "word", "url"};

	private RecordManager recman;

	//For direct access of functions
	public Index 	invertedIndex;
	public Index	forwardIndex;
	public Index	linkIndex;
	public MapTable	wordMapTable;
	public MapTable	urlMapTable;

	Database() throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(DATABASE_NAME);

		invertedIndex = new Index(LoadOrCreate(HASHTABLE_NAME[0]), "doc");
		forwardIndex  = new Index(LoadOrCreate(HASHTABLE_NAME[1]), "word");
		linkIndex	  = new Index(LoadOrCreate(HASHTABLE_NAME[2]), "link");
		wordMapTable  = new MapTable(LoadOrCreate(HASHTABLE_NAME[3]), LoadOrCreate("inverted_" + HASHTABLE_NAME[3]));
		urlMapTable	  = new MapTable(LoadOrCreate(HASHTABLE_NAME[4]), LoadOrCreate("inverted_" + HASHTABLE_NAME[4]));
	}

	// Load the database given the target table name, or create a new one when first try
	private HTree LoadOrCreate(String hashtable_name) throws IOException
	{
		// Load the target database
		long recid = recman.getNamedObject(hashtable_name);
		if(recid != 0)
		{
			// Return Hash Table if found
			System.out.println("Hashtable found, id: " + recid);
			return HTree.load(recman, recid);
		}
		else
		{
			// Create a new Hash Table if not found
			HTree hashtable = HTree.createInstance(recman);
			recid = hashtable.getRecid();
			recman.setNamedObject(hashtable_name, recid);
			System.out.println("Hashtable not found, new id: " + recid);
			return hashtable;
		}
	}

	// Save and confirm the changes of the database
	public void Finalize() throws IOException
	{
		recman.commit();
		recman.close();
		System.out.println("Finalized");
	}

	public static void main(String[] args)
	{
		try
		{
			Database db = new Database();

			// Read in the prompt input from user
			String hashtable_name 	= args[0];
			// Reverse the display order when the second input is "backward"
			boolean order 			= args.length > 1? !args[1].equals("backward"): true;

			// Print all the data in the Hash Table
			if(hashtable_name.equals("inverted"))
			{
				System.out.println("Inverted");
				db.invertedIndex.printAll();
			}
			else if(hashtable_name.equals("forward"))
			{
				System.out.println("Forward");
				db.forwardIndex.printAll();
			}
			else if(hashtable_name.equals("link"))
			{
				System.out.println("Links");
				db.linkIndex.printAll();
			}
			else if(hashtable_name.equals("word"))
			{
				System.out.println("Word");
				db.wordMapTable.printAll(order);
			}
			else if(hashtable_name.equals("url"))
			{
				System.out.println("Urls");
				db.urlMapTable.printAll(order);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

	}
}
