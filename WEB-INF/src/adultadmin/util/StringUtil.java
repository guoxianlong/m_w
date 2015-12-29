/*
 * Created on 2005-5-27
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package adultadmin.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.bean.XMLTransferable;
 
/**
 * @author bomb
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class StringUtil {

	public static String NEW_PAGE = "NEWPAGE";

	public static String TAG_START = "TAGSTART";

	public static String TAG_END = "TAGEND";

	public static String context = "";

	public static float toFloat(String s) {
		if(s==null||s=="")return 0;
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			return 0;
		}
	}
	public static String checkNull(String s){
		if(s == null || "null".equals(s)){
			s = "";
		}
		return s;
	}
	public static double toDouble(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static int StringToId(String strId) {
		int id = 0;
		if(strId != null && !strId.trim().equals("")){
			try {
				id = Integer.parseInt(strId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	public static int toInt(String strId) {
		int id = -1;
		try {
			id = Integer.parseInt(strId);
		} catch (Exception e) {
		}
		return id;
	}

	public static long toLong(String strId) {
		long id = -1;
		try {
			id = Long.parseLong(strId);
		} catch (Exception e) {
		}
		return id;
	}

	public static String convertNull(String s) {
		if (s == null) {
			return "";
		} else {
			return s;
		}
	}

	public static int StringToId(HttpServletRequest request, String name) {
		int id = 0;
		try {
			id = Integer.parseInt(request.getParameter(name));
		} catch (Exception e) {
		}
		return id;
	}

	public static String getFileExt(String filePath) {
		if (filePath == null) {
			return null;
		}

		String path = filePath.trim();

		if (path.equals("")) {
			return null;
		}

		return path.substring(path.lastIndexOf('.') + 1, path.length());
	}

	public static String getUrlAction(String url) {
		if (url == null) {
			return null;
		}

		String action = url.trim();

		if (action.equals("")) {
			return null;
		}
		if (!action.substring(action.length() - 3).equals(".do"))
			return "";
		return action.substring(action.lastIndexOf('/') + 1,
				action.length() - 3);
	}

	public static String getShortAction(String fullname) {
		if (fullname == null) {
			return null;
		}

		String action = fullname.trim();

		if (action.equals("")) {
			return null;
		}

		return action.substring(action.lastIndexOf('/') + 1, action.length());
	}

	public static String getUrlPerm(String url) {
		if (url == null) {
			return null;
		}
		if (url.startsWith("/")) {
			int pos = url.indexOf("-");
			if (pos < 0)
				return null;

			return url.substring(1, pos);
		}
		return null;
	}

	public static String getStringId2(int id) {
		if (stringId2 == null) {
			stringId2 = new String[100];
			for (int i = 0; i < 10; i++) {
				stringId2[i] = "0" + String.valueOf(i);
			}
			for (int i = 10; i < 100; i++) {
				stringId2[i] = String.valueOf(i);
			}
		}
		return stringId2[id];
	}

	private static String[] stringId2 = null;

	public static String getCode(boolean needreply, int corpId, int userId) {
		String code = "02";
		if (needreply) {
			return code + getStringId2(corpId) + String.valueOf(userId);
		} else {
			return code + getStringId2(corpId);
		}
	}

	public static String dealParam(String param) {
		if (param == null) {
			return param;
		}

		//param = param.replaceAll("'", "\"");
		param = param.replace("\\", "\\\\");
		param = param.replace("'", "\\'");
		param = param.trim();
		return param;
	}

	public static String toSql(String src) {
		src = src.replace("\\", "\\\\");
		src = src.replace("'", "\\'");
		return src;
	}

	// 用于 like '??'
	public static String toSqlLike(String src) {
		src = src.replace("\\", "\\\\");
		src = src.replace("'", "\\'");
		src = src.replace("%", "\\%");
		src = src.replace("_", "\\_");
		return src;
	}

	public static String formatDouble(double d) {
		DecimalFormat df = new DecimalFormat("##########0.##");
		return df.format(d);
	}

	public static String formatDouble2(double d) {
		DecimalFormat df = new DecimalFormat("##########0.###");
		return df.format(d);
	}

	public static String formatFloat(float d) {
		DecimalFormat df = new DecimalFormat("##########0.##");
		return df.format(d);
	}

	public static String formatFloat2(float d) {
		DecimalFormat df = new DecimalFormat("##########0.###");
		return df.format(d);
	}

	public static String formatFloat3(float d) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(d);
	}
	
	public static String toWml(String src) {
		if (src == null)
			return "";

		src = src.replaceAll("&", "&amp;");
		src = src.replaceAll("\\$", "");
		src = src.replaceAll("¤", "O");
		src = src.replaceAll("<", "&lt;");
		src = src.replaceAll(">", "&gt;");
		src = src.replaceAll("\r\n", "<br/>");
		src = src.replaceAll("\n", "<br/>");
		src = src.replaceAll("", "");
		src = src.replace("\"", "&#34;");

		return src;
	}
	
	/**
	 * 将String中的'\r\n'和'\n' 都替换成""并返回
	 * @param src
	 * @return
	 */
	public static String removeChangeLine(String src) {
		if( src == null ) 
			return "";
		src = src.replaceAll("\r\n", " ");
		src = src.replaceAll("\n", " ");
		return src;
	}

	public static String dealTag(String str) {
		if (str == null) {
			return null;
		}

		str = str.replaceAll(TAG_START, "<");
		str = str.replaceAll(TAG_END, ">");

		return str;
	}

	public static String dealLink(String link, HttpServletRequest request,
			HttpServletResponse response) {
		if (link == null) {
			return link;
		}

		String domain = request.getServerName();
		if (link.startsWith("/")) {
			link = "http://" + domain + link;
		}
		//        link = link.replaceAll("&", "&amp;");

		return link;//response.encodeURL(link);
	}

	static Pattern p = Pattern.compile("\\[img\\]([^\\[]*)\\[img\\]");

	static String urlRegex = "[http|https]+[://]+[0-9A-Za-z:/[-]_#[?][=][.][%]&[)][(]]*";

	public static String toHtml(String src) {
		if (src == null)
			return "";


		src = src.replaceAll("&", "&amp;");
		src = src.replaceAll("\\$", "");
		src = src.replaceAll("¤", "O");
		src = src.replaceAll("<", "&lt;");
		src = src.replaceAll(">", "&gt;");
		src = src.replaceAll("\r\n", "<br/>");
		src = src.replaceAll("\n", "<br/>");
		src = src.replaceAll("", "");
		src = src.replace("\"", "&#34;");

		src = src.replaceAll("\r\n", "<br/>");
		src = src.replaceAll("\n", "<br/>");
		src = src.replaceAll(" ", "&nbsp;&nbsp;");
		src = src.replaceAll(urlRegex,
				"<a href=\"$0\" target=\"_blank\">$0</a>");
		Matcher m = p.matcher(src);
		while (m.find()) {
			String s = m.group(1);
			s = "<a href=\"" + s + "\" target=_blank>" + "<img src=\"" + s
					+ "\" width=\"400\" border=0 alt=\"点击查看大图\"/></a>";
			src = m.replaceFirst(s);
			m = p.matcher(src);
		}
		return src;
	}
	
	public static String toSecurityHtml(String src) {
		if (src == null)
			return "";

		src = src.replaceAll("&", "&amp;");
		src = src.replaceAll("\\$", "");
		src = src.replaceAll("¤", "O");
		src = src.replaceAll("<", "&lt;");
		src = src.replaceAll(">", "&gt;");
		src = src.replaceAll("\r\n", "<br/>");
		src = src.replaceAll("\n", "<br/>");
		src = src.replaceAll("", "");
		src = src.replace("\"", "&#34;");
		src = src.replace("\'", "&#39;");
		src = src.replace("=", "&#61");
		src = src.replace(":", "&#58");
		src = src.replace("(", "&#40");
		src = src.replace(")", "&#41");
		src = src.replace("$", "&#36");
		src = src.replace(".", "&#46");
		src = src.replaceAll("\r\n", "<br/>");
		src = src.replaceAll("\n", "<br/>");
		src = src.replaceAll(" ", "&nbsp;&nbsp;");
		src = src.replaceAll(urlRegex,"");
		return src;
	}

	public static String array2String(String[] strs, String split) {
		if (strs == null || strs.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder(strs.length * 10);
		for (int i = 0; i < strs.length; i++) {
			if (i != 0) {
				builder.append(split);
			}
			builder.append(strs[i]);
		}
		return builder.toString();
	}

	public static boolean isNull(String s) {
		if (s == null) {
			return true;
		}
		if ("".equals(s)) {
			return true;
		}
		return false;
	}
	
	public static boolean isEmpty(String s){
		boolean result = false;
		
		if(convertNull(s).trim().equals("")){
			result = true;
		}
		
		return result;
	}
	
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-12-25
	 * 
	 * 说明：将一个List内的所有对象都进行XML序列化
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param objList
	 * @param tag
	 * @return
	 */
	public static String list2XML(List objList, String tag) {
		StringBuilder buf = new StringBuilder();
		buf.append("<");
		buf.append(tag);
		buf.append(">");
		if (objList != null) {
			Iterator iter = objList.listIterator();
			while (iter.hasNext()) {
				XMLTransferable xmlObj = (XMLTransferable) iter.next();
				buf.append(xmlObj.toXML());
			}
		}
		buf.append("</");
		buf.append(tag);
		buf.append(">");
		return buf.toString();
	}

	
	 public static String toUtf8String(String s){ 
	     StringBuffer sb = new StringBuffer(); 
	       for (int i=0;i<s.length();i++){ 
	          char c = s.charAt(i); 
	          if (c >= 0 && c <= 255){sb.append(c);} 
	        else{ 
	        byte[] b; 
	         try { b = Character.toString(c).getBytes("utf-8");} 
	         catch (Exception ex) { 
	             System.out.println(ex); 
	                  b = new byte[0]; 
	         } 
	            for (int j = 0; j < b.length; j++) { 
	             int k = b[j]; 
	              if (k < 0) k += 256; 
	              sb.append("%" + Integer.toHexString(k).toUpperCase()); 
	              } 
	     } 
	  } 
	  return sb.toString(); 
	}
	 
	/**
	 * 自适应浏览器返回中文文件xls名称的方法
	 * 2013-08-14
	 * @author haoyabin
	 * @param s
	 * @param request
	 * @return
	 */
	public static String toUtf8String(String s, HttpServletRequest request) {
		String browserType = request.getHeader("user-agent");
		if (browserType.indexOf("Firefox") > -1) {
			String result = "";
			try {
				result = new String(s.getBytes("utf-8"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < s.length(); i++) {
				char c = s.charAt(i);
				if (c >= 0 && c <= 255) {
					sb.append(c);
				} else {
					byte[] b;
					try {
						b = Character.toString(c).getBytes("utf-8");
					} catch (Exception ex) {
						b = new byte[0];
					}
					for (int j = 0; j < b.length; j++) {
						int k = b[j];
						if (k < 0)
							k += 256;
						sb.append("%" + Integer.toHexString(k).toUpperCase());
					}
				}
			}
			return sb.toString();
		}
	}


	 
	public static boolean isNumeric(String str) {
		if (str.matches("\\d*")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isMobile(String s) {
		if (s == null) {
			return false;
		}

		if (!s.startsWith("13") && !s.startsWith("15") && !s.startsWith("14") && !s.startsWith("18")) {
			return false;
		}

		if (s.length() != 11) {
			return false;
		}

		try {
			Long.parseLong(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static String cutString(String s, int count) {
		if (s == null) {
			return s;
		}
		if (s.length() < count) {
			return s;
		}
		s = s.substring(0, count);
		return s;
	}

	public static String cutString(String s, int start, int end) {
		if (s == null) {
			return s;
		}
		if (s.length() < start || s.length() < end) {
			return s;
		}
		s = s.substring(start, end);
		return s;
	}

	//input 字符 
	//index 需要截取的长度 
	public static String getString(String input, int index) {
		int temp = 0; //长度
		StringBuffer sb = new StringBuffer(""); // 构造一个字符串缓冲区，并将其内容初始化为指定的字符串内容。
		for (int i = 0; i < input.length(); i++) {
			//获取每个字符 
			String slice = input.substring(i, i + 1);//循环分解字符串
			//substring()返回一个新的字符串，它是此字符串的一个子字符串。 
			byte[] strByte = slice.getBytes();
			//getBytes()使用平台的默认字符集将此 String 编码为 byte 序列，并将结果存储到一个新的 byte 数组中。
			if (strByte.length == 1) {//长度为1，则为英文字符 
				sb.append(slice);

				if (++temp == index) {
					return sb.toString();
				}
			} else {//长度为2，应为中文字符 
				if (temp + 2 > index) {//如果长度再加2，超过需要截取的长度 
					return sb.toString();
				}
				if (temp + 2 == index) {//如果长度再加2等于需要截取的长度,加上中文字符，返回 
					return sb.append(slice).toString();
				} else {//未超过截取字符，附加上中文字符 
					sb.append(slice);
					temp += 2;
				}
			}
		}
		return sb.toString();
	}
	
	public static String getGetMethodName(String name){
		return "get"+name.substring(0,1).toUpperCase()+name.substring(1);
	}
	
	public static String getSetMethodName(String name){
		return "set"+name.substring(0,1).toUpperCase()+name.substring(1);
	}
	
	public static String convertNull(String s,String def) {
		if (isNull(s)) {
			return def;
		} else {
			return s;
		}
	}

	/**
	 * 作者：曹续
	 * 
	 * 创建时间：2009-9-3
	 * 
	 * 补位左填充
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param input
	 * @param c
	 * @param length
	 * @return
	 */
	public static String padLeft(String input, char c, int length) {
		String output = input;
		while (output.length() < length) {
			output = c + output;
		}
		return output;
	}
	
	public static boolean toBoolean(String s){
		boolean b = false;
		try{
			b = Boolean.parseBoolean(s);
		}catch (Exception e) {
			
		}
		return b;
	}
	
	//异常信息输出转换
    public static String getExceptionInfo(Throwable t) {   
        StringWriter stringWriter= new StringWriter();   
        PrintWriter writer= new PrintWriter(stringWriter);   
        t.printStackTrace(writer);   
        StringBuffer buffer= stringWriter.getBuffer();   
        return buffer.toString();   
    }   

    /**
     * 说明：查询字符串数据中，是否包含某字符串
     */
    public static boolean hasStrArray(String[] array, String s){
    	boolean result = false;
    	for(int i=0;i<array.length;i++){
    		if(array[i].equals(s)){
    			result = true;
    			break;
    		}
    	}
    	return result;
    }
    
    /**
     *  &lt; < 小于号 
		&gt; > 大于号 
		&amp; & 和 
		&apos; ' 单引号 
		&quot; " 双引号
     * 功能:需要转化为xml 的特殊字符变化为 转义字符
     * <p>作者 李双 Dec 21, 2011 3:47:35 PM
     * @return
     */
    public static String changStrToXml(String str){
    	String xml="";
    	if(str==null || str.length()<1) return "";
    	
    	xml=str.replaceAll("<", "&lt;");
    	xml=xml.replaceAll(">", "&gt;");
    	xml=xml.replaceAll("&", "&&amp;");
    	xml=xml.replaceAll("'", "&apos;");
    	xml=xml.replaceAll("\"", "&quot;");
    	
    	return xml;
    }
    
    static Pattern pattern = Pattern.compile("\\d{0,9}");
    static Pattern pattern1 = Pattern.compile("\\[1-9]{1,9}");
    /**
     * 
     * 功能:转化成数字。 
     * <p>作者 李双 May 15, 2012 3:51:39 PM
     * @param id
     * @return
     */
    public static int parstInt(String id){
    	if(convertNull(id).equals("")) return 0;
    	Matcher m = pattern.matcher(id);
		if(m.matches()){
			return Integer.parseInt(id);
		}else{
			return 0;
		}
    }
    
    /**
     * 
     * 功能:转化成数字。 返回负数
     * <p>作者 李双 May 15, 2012 3:51:39 PM
     * @param id
     * @return
     */
    public static int parstBackMinus(String id){
    	if(convertNull(id).equals("")) return -1;
    	Matcher m = pattern.matcher(id);
		if(m.matches()){
			return Integer.parseInt(id);
		}else{
			return -1;
		}
    }
    /**
	 * 
	 * 功能:将字符 json化
	 * <p>作者 李双 Apr 12, 2012 5:04:58 PM
	 * @param str 满足简单的json 基本 但 不带双引号
	 * 缺陷： 值里面含有 ,:}{ 将无法封装
	 * @return
	 */
	public static String toJsonStr(String str){
		if(str==null|| str.length()<1) return null;
		StringBuilder sb = new StringBuilder();
		StringBuilder temp=new StringBuilder();
		for(int i=0;i<str.length();i++){
			char a=str.charAt(i);
			if(a=='{' || a=='}' || a==':' || a==','|| a=='['|| a==']' ){
				if(temp.length()>0){
					Matcher m = pattern1.matcher(temp.toString());
					if(m.matches()|| temp.toString().equals("null")){ //若是数字和 null 将不加双引号
						sb.append(temp);
					}else{
						sb.append("\"").append(temp).append("\"");
					}
				}
				temp.delete(0, temp.length());
				sb.append(a);
				continue;
			}
			temp.append(a);
		}
		return sb.toString();
	}

	/**
	 * 
	 * 功能:去重
	 * <p>作者 李双 Aug 1, 2012 4:00:15 PM
	 * @param array
	 * @return
	 */
	public static String[] unique(String[] array){
		Set set = new LinkedHashSet();
		set.addAll(Arrays.asList(array));
		String[] temp=new String[set.size()];
		int ind=0;
		for(Iterator i=set.iterator();i.hasNext();){
			temp[ind]=String.valueOf(i.next());
			ind++;
		}
		
		return temp;
	}
	
	private final static Locale locale = Locale.CHINA;
	/**
	 * 
	 * 功能:变成小写
	 * <p>作者 李双 Aug 30, 2012 1:44:22 PM
	 * @param str
	 * @return
	 */
	public static String toLowerCase(String str){
		return str!=null?str.toLowerCase(locale):"";
	}
	
	/**
	 * 
	 * 功能:去掉字符串前后的 符号
	 * <p>作者 李双 Sep 13, 2012 3:19:53 PM
	 * @param s
	 * @param symbol
	 * @return
	 */
	public static StringBuilder removeStartAndEndSymbol(StringBuilder s , char symbol){
		if(s==null)return null;
    	if (s.length()>0&&s.charAt(0) == symbol) {
    		s.deleteCharAt(0);
		}
		if (s.length() > 0 && s.charAt(s.length() - 1) == symbol) {
			s.deleteCharAt(s.length() - 1);
		}
		return s ;
	}

	public static boolean inArray(int[] array, int value) {
		if(array != null && array.length > 0) {
			Arrays.sort(array);
			return Arrays.binarySearch(array, value) >= 0;
		}
		return false;
	}
	
		
	/**
	 * 获取百分比，小数点后留1位
	 * @param num
	 * @return
	 */
	public static String getPersent(float num) {
		num = num*100;
		String result = String.valueOf(num);
		if(result.length()-result.indexOf(".")>3) {
			result = result.substring(0, result.indexOf(".")+3);
		}
		return result+"%";
	}
	
	static Pattern ptc = Pattern.compile("[\\u4e00-\\u9fa5]+");
	public static boolean isChainese(String value){
		Matcher m = ptc.matcher(value);
		if(m.matches()){
			return true;
		}
		return false;
	}
	
	/**
	 * 将数字转化为汉字数字
	 * 例如：1---一，11--十一
	 * @author 李宁
	 * @param n
	 * @return
	 */
	public static String transfer(int n){
		Stack<Integer> st = new Stack<Integer>();
		int division = 0; // 余数
		while (n >= 10) {
			division = n % 10;
			st.push(division);
			n = n / 10;
		}
		st.push(n); // 将最高位压栈
		
		//数字与汉字的映射表
		HashMap<Integer, String> numberMap = new HashMap<Integer, String>();
		numberMap.put(0, "零");
		numberMap.put(1, "一");
		numberMap.put(2, "二");
		numberMap.put(3, "三");
		numberMap.put(4, "四");
		numberMap.put(5, "五");
		numberMap.put(6, "六");
		numberMap.put(7, "七");
		numberMap.put(8, "八");
		numberMap.put(9, "九");
		
		//位数与其对应的单位的映射表
		HashMap<Integer, String> unitMap = new HashMap<Integer, String>();
		unitMap.put(2, "十");
		unitMap.put(3, "百");
		unitMap.put(4, "千");
		unitMap.put(5, "万");
		unitMap.put(6, "十万");
		unitMap.put(7, "百万");
		unitMap.put(8, "千万");
		unitMap.put(9, "亿");
		
		String out = "";
		int count = 0;
		while (!st.isEmpty()) {
			//移除堆栈顶部的对象,并获取顶部对象的值
			int temp = st.pop();
			if (st.size() == 0) {
				if (temp != 0) {
					out = out + numberMap.get(temp);
				}
			} else {
				if (temp == 0) {
					count++;
					//处理这种2005--二千零五，而不是二千零零五
					if(count<2){
						out = out + numberMap.get(temp);
					}
				} else {
					out = out + numberMap.get(temp) + unitMap.get(st.size() + 1);
					//处理特殊的读法，10--十,11,十一而不是一十一
					if("一十".equals(out)){
						out = "十";
					}
				}
			}
		}
		return out;
	}
	
	public static void main(String args[]){
		/*System.out.println(toFloat("5.4"));
		
		System.out.println(5.4>3);
		
		System.out.println(Math.ceil(5.0));*/
		System.out.println(StringUtil.addZeroLeft(3, 2));
		
	}
	
	/**
	 * 给数值的左边加0 以满足位数
	 * @param i   数值
	 * @param j   总位数值
	 * @return
	 */
	public static String addZeroLeft(int i, int j) {
		String formatRule = "%0"+j+"d";
		String b = String.format(formatRule, i);
		return b;
	}
	
	/**
	 * 将驼峰式命名的字符串转换为下划线小写方式。如果转换前的驼峰式命名的字符串为空，则返回空字符串。</br>
	 * 例如：HelloWorld->hello_world
	 * @param name 转换前的驼峰式命名的字符串
	 * @return 转换后下划线小写方式命名的字符串
	 */
	public static String underscoreName(String name) {
	    StringBuilder result = new StringBuilder();
	    if (name != null && name.length() > 0) {
	        // 将第一个字符处理成大写
	        result.append(name.substring(0, 1).toUpperCase());
	        // 循环处理其余字符
	        for (int i = 1; i < name.length(); i++) {
	            String s = name.substring(i, i + 1);
	            // 在大写字母前添加下划线
	            if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
	                result.append("_");
	            }
	            // 其他字符直接拼接
	            result.append(s);
	        }
	    }
	    return result.toString().toLowerCase();
	}

	
	/**
	 * 判断字符串中字符是否只在0-9a-zA-Z之间(无符号)
	 * return :
	 * 			true  无符号
	 * 			false 有符号
	 */
	public static boolean isNoSign(String str) {
		  Pattern pattern = Pattern.compile("[a-zA-Z0-9]");
		  //如果为空 返回false
		  if(str==null&&str.equals("")){
			  return false;
		  }
		  //对每一个字符进行判断
		  for(int i =0 ; i< str.length() ; i++){
			  Matcher matcher = pattern.matcher(str.charAt(i)+"");
			  if(!matcher.matches()){
				  return false;
			  }
		  }
		  return true;
	}
	
	/**
	 * 判断字符串中是否无空白  (空格 , tab , 回车)
	 * @param str
	 * @return  true 	:无空白
	 * 			false	:有空白
	 */
	public static boolean isNoBlank(String str) {
		  Pattern pattern = Pattern.compile("[^\\s]");
		  if(str==null&&str.equals("")){
			  return false;
		  }
		  for(int i =0 ; i< str.length() ; i++){
			  Matcher matcher = pattern.matcher(str.charAt(i)+"");
			  if(!matcher.matches()){
				  return false;
			  }
		  }
		  return true;
	}
	
	/**
	 * 删除字符串中特殊字符，
	* @Description: 
	* @author ahc
	 */
	
	public static String replaceStr(String s){
		if(s!=null && !"".equals(s)){
			s = s.replaceAll("&", "");
			s = s.replaceAll("<", "");
			s = s.replaceAll(">", "");
			s = s.replaceAll("'", "");
			s = s.replaceAll("\"\"", "");
		}
		return s;
	}
	
	/***
	 * jinpeng  20140612
	 * 功能：手机号码隐藏第四至第七位数字
	 * @param phone  超过11位 表明是非无锡本地的电话号码 ，前面加的有0 
	 * @return
	 */
	public static String concealPhone(String phone){
		if(phone == null || "".equals(phone)){
			return "" ;
		}
		if(phone.length() == 11){
			phone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length());
		}else if(phone.length() == 12){
			phone = phone.substring(0, 4) + "****" + phone.substring(8, phone.length());
		}else{
			return phone ;
		}
		return phone ;
	}
}
