package mmb.rec.pda.util;

import net.sf.ezmorph.bean.MorphDynaBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.util.StringUtil;
import mmb.rec.pda.bean.JsonModel;
import mmb.rec.sys.easyui.Json;

/**
 * JsonModel工具类
 * 
 * @author mengqy
 * 
 */
public class JsonModelUtil {


	/**
	 * 获取字符串
	 * @param json
	 * @param key
	 * @return
	 */
	public static String getString(JsonModel json, String key) {
		if (json.getData() == null || !json.getData().containsKey(key) || json.getData().get(key) == null) {
			return null;
		}
		return json.getData().get(key).toString();
	}

	/**
	 * 获取int
	 * @param json
	 * @param key
	 * @return
	 */
	public static int getInt(JsonModel json, String key) {
		String temp = getString(json, key);
		if(temp != null)
			return StringUtil.toInt(temp);

		return -1;
	}
	
	/**
	 * 获取float
	 * @param json
	 * @param key
	 * @return
	 */
	public static float getFloat(JsonModel json, String key) {
		String temp = getString(json, key);
		if(temp != null)
			return StringUtil.toFloat(temp);

		return -1;
	}
	
	/**
	 * 获取double
	 * @param json
	 * @param key
	 * @return
	 */
	public static double getDouble(JsonModel json, String key) {
		String temp = getString(json, key);
		if(temp != null)
			return StringUtil.toDouble(temp);

		return -1;
	}
	

	
	/**
	 * 获取字符串列表
	 * 
	 * @param json
	 * @param key
	 * @return
	 */
	public static List<String> getListString(JsonModel json, String key) {
		if (json.getData() == null || !json.getData().containsKey(key)) {
			return null;
		}

		List<String> list = (ArrayList<String>) json.getData().get(key);
		if (list == null || list.size() == 0)
			return null;
		return list;
	}

	/**
	 * 获取键值对列表
	 * 
	 * @param json
	 * @param key
	 * @param items
	 * @return
	 */
	public static List<HashMap<String, String>> getListMap(JsonModel json, String key, String[] items) {
		if (json.getData() == null || !json.getData().containsKey(key) || json.getData().get(key) == null) {
			return null;
		}

		List<MorphDynaBean> list = (ArrayList<MorphDynaBean>) json.getData().get(key);
		if (list == null || list.size() == 0)
			return null;

		List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		for (MorphDynaBean bean : list) {
			HashMap<String, String> map = new HashMap<String, String>();
			for (String item : items) {
				map.put(item, bean.get(item).toString());
			}
			result.add(map);
		}

		return result;
	}

	/**
	 * 根据json判断返回结果
	 * @param json
	 * @return
	 */
	public static JsonModel json(Json json) {
		if (json.isSuccess()) {
			return JsonModelUtil.success();
		}
		return JsonModelUtil.error(json.getMsg());
	}

	/**
	 * 返回错误信息
	 * 
	 * @param msg
	 * @return
	 */
	public static JsonModel error(String msg) {
		return error(msg, 0);
	}

	/**
	 * 返回错误信息
	 * 
	 * @param msg
	 * @param flag
	 * @return
	 */
	public static JsonModel error(String msg, int flag) {
		JsonModel json = new JsonModel();
		json.setFlag(flag);
		json.setData(null);
		json.setMessage(msg);
		return json;
	}

	/**
	 * 返回操作成功提示信息
	 * 
	 * @return
	 */
	public static JsonModel success() {
		JsonModel json = new JsonModel();
		json.setFlag(1);
		json.setData(null);
		json.setMessage("操作成功");
		return json;
	}

	/**
	 * 返回操作成功提示信息
	 * 
	 * @return
	 */
	public static JsonModel success(String key, Object value) {
		JsonModel json = new JsonModel();
		json.setFlag(1);
		json.setMessage("操作成功");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(key, value);
		json.setData(map);
		return json;
	}

}
