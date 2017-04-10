import java.util.Vector;

public class FPair
{
	public int Key;
	public double Value;

	FPair(int key, double value)
	{
		Key = key;
		Value = value;
	}		
	
	public static Vector<FPair> TopK(Vector<FPair> list, int k)
	{
		Vector<FPair> topK = new Vector<FPair>();
		for(int i = 0; i < k; i++)
		{
			int top_index = 0;
			for(int j = 0; j < list.size(); j++)
				if(list.get(j).Value > list.get(top_index).Value)
					top_index = j;
			topK.add(list.remove(top_index));
		}

		return topK;
	}
}
