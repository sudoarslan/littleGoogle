import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.htree.HTree;
import jdbm.helper.FastIterator;
import java.util.Vector;
import java.io.IOException;
import java.io.Serializable;

import java.util.Arrays;

public class Test
{
	private RecordManager recman;
	private HTree hashtable;

	Test(String recordmanager, String objectname) throws IOException
	{
		recman = RecordManagerFactory.createRecordManager(recordmanager);
		System.out.println(objectname);
		long recid = recman.getNamedObject(objectname);

		if (recid != 0){
			hashtable = HTree.load(recman, recid);
			System.out.println("Hash table loaded!");
		}
		else
		{
			System.out.println("Hash table is empty");
			hashtable = HTree.createInstance(recman);
			recman.setNamedObject( objectname, hashtable.getRecid() );
		}
	}

	public void printAll() throws IOException
	{
		FastIterator iter = hashtable.keys();
		String key;

		while( (key=(String)iter.next()) != null )
			System.out.println(key);
	}

	public static void main(String[] args)
	{
		try
		{
			Test test = new Test("indexDB","ht1");
			System.out.println("Running test...");
			test.printAll();
		}
		catch(IOException ex)
		{
			System.err.println(ex.toString());
		}

	}
}
