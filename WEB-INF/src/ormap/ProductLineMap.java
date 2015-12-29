package ormap;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者：赵林
 *
 * 说明：警戒线——一级分类 映射
 */
public class ProductLineMap {
	private static Map productLineMap = new HashMap();
	
	/**
	 * 说明：北京成人保健
	 */
	public static final int SECURITYLINE0 = 0;
	
	/**
	 * 说明：广东成人保健
	 */
	public static final int SECURITYLINE1 = 1;

	/**
	 * 说明：手机数码
	 */
	public static final int SECURITYLINE2 = 2;
	
	/**
	 * 说明：手机数码配件
	 */
	public static final int SECURITYLINE3 = 3;
	
	/**
	 * 说明：电脑
	 */
	public static final int SECURITYLINE4 = 4;
	
	/**
	 * 说明：服装
	 */
	public static final int SECURITYLINE5 = 5;
	
	/**
	 * 说明：鞋子
	 */
	public static final int SECURITYLINE6 = 6;
	
	/**
	 * 说明：护肤品
	 */
	public static final int SECURITYLINE7 = 7;
	
	/**
	 * 说明：礼品
	 */
	public static final int SECURITYLINE8 = 8;
	
	/**
	 * 说明：新奇特
	 */
	public static final int SECURITYLINE9 = 9;
	
	/**
	 * 说明：包
	 */
	public static final int SECURITYLINE10 = 10;
	
	/**
	 * 说明：行货手机
	 */
	public static final int SECURITYLINE11 = 11;
	
	/**
	 * 说明：小家电
	 */
	public static final int SECURITYLINE12 = 12;
	
	/**
	 * 说明：饰品
	 */
	public static final int SECURITYLINE13 = 13;
	
	/**
	 * 说明：鞋配件
	 */
	public static final int SECURITYLINE14 = 14;
	
	/**
	 * 说明：手表
	 */
	public static final int SECURITYLINE15 = 15;
	
	/**
	 * 说明：成人日用
	 */
	public static final int SECURITYLINE16 = 16;
	
	/**
	 * 说明：日用百货
	 */
	public static final int SECURITYLINE17 = 17;
	
	/**
	 * 说明：食品
	 */
	public static final int SECURITYLINE18 = 18;
	
	static{
		productLineMap.put(Integer.valueOf(0), "成人保健");
		productLineMap.put(Integer.valueOf(1), "成人保健");
		productLineMap.put(Integer.valueOf(2), "成人保健");
		productLineMap.put(Integer.valueOf(3), "成人保健");
		productLineMap.put(Integer.valueOf(4), "成人保健");
		productLineMap.put(Integer.valueOf(5), "成人保健");
		productLineMap.put(Integer.valueOf(7), "成人保健");
		productLineMap.put(Integer.valueOf(8), "成人保健");
		productLineMap.put(Integer.valueOf(9), "成人保健");
		productLineMap.put(Integer.valueOf(17), "成人保健");
		productLineMap.put(Integer.valueOf(26), "成人保健");
		productLineMap.put(Integer.valueOf(28), "成人保健");
		productLineMap.put(Integer.valueOf(30), "成人保健");
		productLineMap.put(Integer.valueOf(36), "成人保健");
		productLineMap.put(Integer.valueOf(40), "成人保健");
		productLineMap.put(Integer.valueOf(43), "成人保健");
		productLineMap.put(Integer.valueOf(47), "成人保健");
		productLineMap.put(Integer.valueOf(51), "成人保健");
		productLineMap.put(Integer.valueOf(83), "成人保健");
		productLineMap.put(Integer.valueOf(89), "成人保健");
		productLineMap.put(Integer.valueOf(93), "成人保健");
		productLineMap.put(Integer.valueOf(94), "成人保健");
		productLineMap.put(Integer.valueOf(145), "成人保健");
		
		productLineMap.put(Integer.valueOf(690), "成人日用");
		
		productLineMap.put(Integer.valueOf(105), "手机数码");
		productLineMap.put(Integer.valueOf(114), "手机数码");
		productLineMap.put(Integer.valueOf(300), "手机数码");
		productLineMap.put(Integer.valueOf(118), "手机数码");
		productLineMap.put(Integer.valueOf(806), "手机数码");
		productLineMap.put(Integer.valueOf(807), "手机数码");
		productLineMap.put(Integer.valueOf(808), "手机数码");
		productLineMap.put(Integer.valueOf(809), "手机数码");
		productLineMap.put(Integer.valueOf(810), "手机数码");
		productLineMap.put(Integer.valueOf(811), "手机数码");
		
		
		productLineMap.put(Integer.valueOf(107), "手机数码配件");
		productLineMap.put(Integer.valueOf(117), "手机数码配件");
		
		productLineMap.put(Integer.valueOf(110), "行货手机");
		
		productLineMap.put(Integer.valueOf(130), "电脑");
		productLineMap.put(Integer.valueOf(131), "电脑");
		productLineMap.put(Integer.valueOf(132), "电脑");
		
		productLineMap.put(Integer.valueOf(119), "服装");
		productLineMap.put(Integer.valueOf(401), "服装");
		productLineMap.put(Integer.valueOf(340), "服装");
		
		productLineMap.put(Integer.valueOf(123), "鞋子");
		productLineMap.put(Integer.valueOf(143), "鞋配件");
		
		productLineMap.put(Integer.valueOf(6), "护肤品");
		productLineMap.put(Integer.valueOf(151), "护肤品");
		productLineMap.put(Integer.valueOf(183), "护肤品");
		productLineMap.put(Integer.valueOf(184), "护肤品");
		
		productLineMap.put(Integer.valueOf(102), "礼品");
		
		productLineMap.put(Integer.valueOf(163), "新奇特");
		productLineMap.put(Integer.valueOf(197), "小家电");
		productLineMap.put(Integer.valueOf(208), "饰品");
		
		productLineMap.put(Integer.valueOf(136), "包");
		productLineMap.put(Integer.valueOf(138), "包");
		
		productLineMap.put(Integer.valueOf(505), "手表");
		
		productLineMap.put(Integer.valueOf(752), "日用百货");
		
		productLineMap.put(Integer.valueOf(803), "食品");
	}
	
	public static Map getProductLineMap(){
		return productLineMap;
	}
}
