/*
 * Created on 2009-10-13
 *
 */
package adultadmin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 作者：李北金
 * 
 * 创建日期：2009-10-13
 * 
 * 说明：
 */
public class HttpUtil {

    public static void main(String[] args) {
        String str = getContent("http://qudao.ebinf.com/qshop_admin/check.jsp?c=c03879773157d6f6feb959b7d4642fcb", "UTF-8");
        System.out.println(str);
        if("1".equals(str)){
            System.out.println(true);
        }
    }

    public static String getContent(String address, String charset) {
        try {
            URL url = new URL(address);
            BufferedReader br = new BufferedReader(new InputStreamReader(url
                    .openStream(), charset));
            StringBuffer sb = new StringBuffer();
            int line = br.read();
            while (line != -1) {
                if (line == 0) {
                    line = br.read();
                    continue;
                }
                char c = (char) line;
                //System.out.println(c);
                sb.append(c);
                line = br.read();
            }
            String s = sb.toString();
            return s;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     */
    public static String getOrderDetailsHref(Object codeObject,Object idObject){
    	String codeStr=String.valueOf(codeObject==null?"":codeObject);
    	String idStr = String.valueOf(idObject==null?"":idObject);
    	
    	StringBuffer bs=new StringBuffer();//  
    	String[] codes = codeStr.split("<br/>");
    	String[] ids = idStr.split(",");
    	if(ids.length<1||codes.length<1){
    		return "";
    	}
    	
    	for(int i=0;i<codes.length;i++){
    		bs.append("<a href='../order.do?id=");
    		bs.append(ids[i]);
    		bs.append("' target='_blank'>");
    	    bs.append(codes[i]);
    	    bs.append("</a>");
    		bs.append("<br/>");
    	}
    	
    	return bs.toString();
    }
}