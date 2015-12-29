package mmb.common.service;

import java.util.HashMap;
import java.util.Map;

public class CommonService {

	
	public static Map<String, String> constructDeleteMap(String table,
			String condition) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("table", table);
		map.put("condition", condition);
		return map;
	}
	
	public static Map<String,String> constructUpdateMap(String table, String set, String condition) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("table", table);
		map.put("set", set);
		map.put("condition", condition);
		return map;
	}
	
	public static Map<String,String> constructCountMap(String table,String condition ) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("table",table);
		map.put("condition", condition);
		return map;
	} 
	/**
	 * 拼装map的方法 传入 condition， index， count， orderBy
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public static Map<String, String> constructSelectMap(String condition, int index,
			int count, String orderBy) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("condition", condition);
		map.put("index", index+"");
		map.put("count", count+"");
		map.put("orderBy", orderBy);
		return map;
	}
}
