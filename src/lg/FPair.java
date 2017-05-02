package lg;

import java.util.Vector;
import java.util.Collections;

public class FPair implements Comparable<FPair>
{
	public int Key;
	public double Value;

	FPair(int key, double value)
	{
		Key = key;
		Value = value;
	}

	public void changeValue(double new_value){
		Value = new_value;
	}

	@Override
	public int compareTo(FPair other) {
		if (Value > other.Value)
			return -1;
		else if (Value == other.Value)
			return 0;
		else
			return 1;
   }

	public static Vector<FPair> TopK(Vector<FPair> list, int k, Vector<FPair> PR, double w)
	{
		// filter
		Vector<FPair> filtered = new Vector<FPair>();
		for(int i = 0; list.size() > 0 && i< list.size(); i++)
		{
			double score = list.get(i).Value;
			if(score > 0.0){
				FPair new_pair = list.remove(i);
				double PR_score = PR.get(i).Value;
				FPair pagerank_pair = PR.remove(i);
				double weighted_score = w * score + (1-w) * PR_score;
				new_pair.changeValue(weighted_score);
				filtered.add(new_pair);
			}
		}
		// rank
		Collections.sort(filtered);
		Vector<FPair> topK = new Vector<FPair>();
		for(int i = 0; i < k && filtered.size() > 0; i++)
		{
			topK.add(filtered.remove(0));
		}

		return topK;
	}
}
