/*
 * Created on 2009-2-26
 *
 */
package adultadmin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import adultadmin.bean.Postage;

public class PostageUtil {

	/*
	 * 
	 * postage.setAreaName("广东省广州市"); 
	 * postage.setQizhong(500);
	 * postage.setXuzhong(500); 
	 * postage.setQizhongfeiyong(8.4f);
	 * postage.setXuzhongfeiyong(1.4f); 
	 * postage.setTuotoufeiyong(0);
	 * postage.setDshkfeiyong(0.01f);
	 */

	private static String gdPostageConfigFile = "GDPostageConfigFile.txt";
	private static String bjPostageConfigFile = "BJPostageConfigFile.txt";

	/*
	private static String gdPostageConfigFile = "D:/eclipse/workspace/adult-admin/WEB-INF/config/GDPostageConfigFile.txt";
	private static String bjPostageConfigFile = "D:/eclipse/workspace/adult-admin/WEB-INF/config/BJPostageConfigFile.txt";
	*/
	

	private static Map gdPostageMap = new HashMap();
	private static Map bjPostageMap = new HashMap();
	static {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Constants.CONFIG_PATH + gdPostageConfigFile)), "UTF-8"));
			String str = br.readLine();
			while (str != null) {
				if (!"".equals(str)) {
					String[] fields = str.split("\t");
					Postage postage = new Postage();
					postage.setAreaName(fields[0]);
					postage.setQizhong(StringUtil.toInt(fields[1]));
					postage.setXuzhong(StringUtil.toInt(fields[2]));
					postage.setQizhongfeiyong(StringUtil.toFloat(fields[3]));
					postage.setXuzhongfeiyong(StringUtil.toFloat(fields[4]));
					postage.setTuotoufeiyong(StringUtil.toFloat(fields[5]));
					postage.setDshkfeiyong(StringUtil.toFloat(fields[6]));
					gdPostageMap.put(postage.getAreaName(), postage);
				}
				str = br.readLine();
			}
			br.close();
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Constants.CONFIG_PATH + bjPostageConfigFile)), "UTF-8"));
			str = br.readLine();
			while (str != null) {
				if (!"".equals(str)) {
					String[] fields = str.split("\t");
					Postage postage = new Postage();
					postage.setAreaName(fields[0]);
					postage.setQizhong(StringUtil.toInt(fields[1]));
					postage.setXuzhong(StringUtil.toInt(fields[2]));
					postage.setQizhongfeiyong(StringUtil.toFloat(fields[3]));
					postage.setXuzhongfeiyong(StringUtil.toFloat(fields[4]));
					postage.setTuotoufeiyong(StringUtil.toFloat(fields[5]));
					postage.setDshkfeiyong(StringUtil.toFloat(fields[6]));
					bjPostageMap.put(postage.getAreaName(), postage);
				}
				str = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Postage getPostage(int area, String areaName){
		Postage postage = null;
		if(area == 0){
			postage = (Postage)bjPostageMap.get(areaName);
		} else {
			postage = (Postage)gdPostageMap.get(areaName);
		}
		return postage;
	}
}
