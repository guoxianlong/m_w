package adultadmin.util;


public class SplitBuffer {

	private StringBuffer sb;
	private String split = null;
	
	public SplitBuffer()
	{
		sb = new StringBuffer();
		this.split = ",";
	}
	
	public SplitBuffer(String split)
	{
		sb = new StringBuffer();
		this.split = split;
	}
	
	public void add(int e)
	{
		sb.append(e);
		sb.append(split);
	}
	
	public void add(String e)
	{
		sb.append(e);
		sb.append(split);
	}
	
	public void add(String[] es)
	{
		if(es != null)
			for(int i = 0;i < es.length;i++)
			{
				sb.append(es[i]);
				sb.append(split);
			}
	}
	
	public String toString()
	{
		return sb.toString();
	}
}
