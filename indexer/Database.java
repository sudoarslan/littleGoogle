import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import java.io.IOException;

public class Database
{
	// Database filename
	private static final String DATABASE_NAME = "indexDB";
	// Hashtable filenames
	private static final String[] HASHTABLE_NAME = {"inverted", "forward", "word", "link"};

	private RecordManager recman;

	//For direct access of functions
	public Index 	invertedIndex;
	public Index	forwardIndex;
	public MapTable	wordMapTable;
	public MapTable	linkMapTable;

	Database() throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(DATABASE_NAME);

		invertedIndex = new Index(LoadOrCreate(HASHTABLE_NAME[0]), "doc");
		forwardIndex  = new Index(LoadOrCreate(HASHTABLE_NAME[1]), "word");
		wordMapTable  = new MapTable(LoadOrCreate(HASHTABLE_NAME[2]));
		linkMapTable  = new MapTable(LoadOrCreate(HASHTABLE_NAME[3]));
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
			if(args[0].equals("inverted"))
			{
				System.out.println("Inverted");
				db.invertedIndex.printAll();
			}
			else if(args[0].equals("forward"))
			{
				System.out.println("Forward");
				db.forwardIndex.printAll();
			}
			else if(args[0].equals("word"))
			{
				System.out.println("Word");
				db.wordMapTable.printAll();
			}
			else if(args[0].equals("link"))
			{
				System.out.println("Link");
				db.linkMapTable.printAll();
			}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
		}

	}
}
