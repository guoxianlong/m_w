/* ==================================================================
 * $Id: TypeConvertUtil.java,v 1.1 2010/07/19 10:37:31 lhy Exp $ 
 * Created [Jul 9, 2010 4:14:00 PM] by louhy
 * ==================================================================
 * telek_oa
 * ==================================================================
 * telek_oa License v1.0
 * Copyright (c) Telek S&T Co.ltd HangZhou, 2009-2010
 * ==================================================================
 * 杭州天丽科技有限公司拥有该文件的使用、复制、修改和分发的许可权
 * 如果你想得到更多信息，请访问 <http://www.telek.com.cn>
 * Telek S&T Co.ltd HangZhou owns permission to use, copy, modify and
 * distribute this documentation.
 * For more information on telek_oa, please
 * see <http://www.telek.com.cn>
 * ==================================================================
 */
 package com.mmb.framework.utils;

/**
 * TypeConvertUtil.java 
 *
 * @author $Author: lhy $
 * @version $Revision: 1.1 $ $Date: 2010/07/19 10:37:31 $
 */
public abstract class TypeConvertUtil
{

	/**
	 * 将null的字符串转换为""
	 * @param str
	 * @return
	 */
	public static String getNullStr(Object obj) 
	{
		if (obj == null)
			return "";

		if (obj.toString() != null && obj.toString().equals("null")
				|| obj.toString().equals("NULL")) {
			return "";
		} else {
			return obj.toString();
		}
	}

	public static int getIntOfObj(Object obj)
	{
		try
		{
			return Integer.parseInt(obj.toString());
		} 
		catch (Exception ingore)
		{
			return 0;
		}
	}

	public static double getDoubleOfObj(Object dou)
	{
		try
		{
			return Double.parseDouble(dou.toString());
		}
		catch (Exception ingore)
		{
			return 0;
		}
	}

}
