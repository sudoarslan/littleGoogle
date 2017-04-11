import IRUtilities.*;
import java.io.*;
import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class StopStem
{
	private Porter porter;
	private java.util.HashSet stopWords;

	private static final String STOPWORD_SOURCE_DIRECTORY = "stopwords.txt";

	public boolean isStopWord(String str)
	{
		return stopWords.contains(str);	
	}

	public StopStem()
	{
		super();

		try
		{
			porter = new Porter();
			stopWords = new java.util.HashSet();

			BufferedReader br = new BufferedReader(new FileReader(STOPWORD_SOURCE_DIRECTORY));

			String currentLine;

			while((currentLine = br.readLine()) != null)
				stopWords.add(currentLine);
		}
		catch(Exception ex)
		{
			System.out.println(ex.toString());
		}
	}

	public String stem(String str)
	{
		return porter.stripAffixes(str);
	}

	public Vector<String> stopAndStem(String[] str)
	{
		Vector<String> words = new Vector<String>();
		if(str.length == 0)
			return words;

		for(String s : str)
			if(!s.equals("") && !isStopWord(s))
				words.add(stem(s));

		return words;
	}

	public static void main(String[] arg)
	{
		StopStem stopStem = new StopStem();
		String input="";
		try
		{
			do
			{
				System.out.print("Please enter a single English word: ");
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				input = in.readLine();
				if(input.length()>0)
				{	
					if (stopStem.isStopWord(input))
						System.out.println("It should be stopped");
					else
						System.out.println("The stem of it is \"" + stopStem.stem(input)+"\"");
				}
			}
			while(input.length()>0);
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
		}
	}
}
