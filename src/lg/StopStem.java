package lg;

import java.io.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;

public class StopStem
{
	private Porter porter;
	private HashSet stopWords;
	private String stopWordsStr = "a about above across after again against all almost alone along already also although always among an and another any anybody anyone anything anywhere are area areas around as ask asked asking asks at away b back backed backing backs be became because become becomes been before began behind being beings best better between big both but by c came can cannot case cases certain certainly clear clearly come could d did differ different differently do does done down down downed downing downs during e each early either end ended ending ends enough even evenly ever every everybody everyone everything everywhere f face faces fact facts far felt few find finds first for four from full fully further furthered furthering furthers g gave general generally get gets give given gives go going good goods got great greater greatest group grouped grouping groups h had has have having he her here herself high high high higher highest him himself his how however i if important in interest interested interesting interests into is it its itself j just k keep keeps kind knew know known knows l large largely last later latest least less let lets like likely long longer longest m made make making man many may me member members men might more most mostly mr mrs much must my myself n necessary need needed needing needs never new new newer newest next no nobody non noone not nothing now nowhere number numbers o of off often old older oldest on once one only open opened opening opens or order ordered ordering orders other others our out over p part parted parting parts per perhaps place places point pointed pointing points possible present presented presenting presents problem problems put puts q quite r rather really right right room rooms s said same saw say says second seconds see seem seemed seeming seems sees several shall she should show showed showing shows side sides since small smaller smallest so some somebody someone something somewhere state states still still such sure t take taken than that the their them then there therefore these they thing things think thinks this those though thought thoughts three through thus to today together too took toward turn turned turning turns two u under until up upon us use used uses v very w want wanted wanting wants was way ways we well wells went were what when where whether which while who whole whose why will with within without work worked working works would x y year years yet you young younger youngest your yours z";

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
			stopWords = new HashSet();

			for(String sw: stopWordsStr.split("\\s+"))
				stopWords.add(sw);
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
