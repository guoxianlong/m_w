package com.mmb.framework.utils;

/**
 * mmb 字符处理工具类
 * @author user
 *
 */
public class StringUtils {
	/**
	 * 方法说明
	 * 
	 * @param str 参数说明
	 * @return 返回类型说明 
	 */
	public static boolean isEmpty(String str) {
		return null == str || "".equals(str) ? false : true;
	}
}