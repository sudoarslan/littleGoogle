import java.util.Vector;

public class PageInfo
{
	public String Title;
	public String Url;
	public String LastModifiedDate;
	public int SizeOfPage;
	public Vector<WPair> KeywordVector;
	public Vector<String> ParentLinkVector;
	public Vector<String> ChildLinkVector;
	public double Score;

	// Empty Constructor
	PageInfo()
	{
		Title = "";
		Url = "";
		LastModifiedDate = "";
		SizeOfPage = 0;
		KeywordVector = new Vector<WPair>();
		ParentLinkVector = null;
		ChildLinkVector = null;
		Score = 0.0;
	}

	PageInfo(String title, String url, String lastModifiedDate, int sizeOfPage, Vector<WPair> keywordVector, 
		Vector<String> parentLinkVector, Vector<String> childLinkVector, double score)
	{
		Title = title;
		Url = url;
		LastModifiedDate = lastModifiedDate;
		SizeOfPage = sizeOfPage;
		KeywordVector = keywordVector;
		ParentLinkVector = parentLinkVector;
		ChildLinkVector = childLinkVector;
		Score = score;
	}

}
