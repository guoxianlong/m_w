package mmb.rec.sys.util;

public class BeanColumnToTableColumn {
	public static String Transform(String beanColumn){
		StringBuffer taleColumn = new StringBuffer();
		if(beanColumn != null && !"".equals(beanColumn)){
			StringBuffer sb=new StringBuffer(beanColumn);
	        char temp=0;
	        char large=32;
	        for(int i=0;i<sb.length();i++){
	            temp=sb.charAt(i);
	            if(temp>=65&&temp<=90){
	                temp+=large;
	                taleColumn.append("_");
	                taleColumn.append(temp);
	            }else{
	            	taleColumn.append(temp);
	            }
	        }
		}
		return taleColumn.toString();
	}
}
