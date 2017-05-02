package lg;

import java.util.Vector;

public class SearchResult
{
	public String SuggestedQuery;
	public Vector<PageInfo> PageInfoVector;

	// Empty Constructor
	SearchResult()
	{
		SuggestedQuery = "";
		PageInfoVector = new Vector<PageInfo>();
	}

	SearchResult(String suggestedQuery, Vector<PageInfo> pageInfoVector)
	{
		SuggestedQuery = suggestedQuery;
		PageInfoVector = pageInfoVector;
	}

}
