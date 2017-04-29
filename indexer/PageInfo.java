import java.util.Vector;

public class PageInfo
{
	public String Title;
	public String Url;
	public String LastModifiedDate;
	public int SizeOfPage;
	public Vector<String> KeywordVector;
	public Vector<String> ParentLinkVector;
	public Vector<String> ChildLinkVector;

	// Empty Constructor
	PageInfo()
	{
		Title = "";
		Url = "";
		LastModifiedDate = "";
		SizeOfPage = 0;
		KeywordVector = null;
		ParentLinkVector = null;
		ChildLinkVector = null;
	}

	PageInfo(String title, String url, String lastModifiedDate, int sizeOfPage, Vector<String> keywordVector, 
		Vector<String> parentLinkVector, Vector<String> childLinkVector)
	{
		Title = title;
		Url = url;
		LastModifiedDate = lastModifiedDate;
		SizeOfPage = sizeOfPage;
		KeywordVector = keywordVector;
		ParentLinkVector = parentLinkVector;
		ChildLinkVector = childLinkVector;
	}

}
