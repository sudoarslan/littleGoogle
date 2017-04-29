import java.util.*;

public class Pair
{
	public int Key;
	public int Value;

	Pair(int key, int value)
	{
		Key = key;
		Value = value;
	}

	public void printPair(){
		System.out.printf("%d, %d  ", Key, Value);
	}

	public int getKey(){
		return Key;
	}

	public int getValue(){
		return Value;
	}

}
