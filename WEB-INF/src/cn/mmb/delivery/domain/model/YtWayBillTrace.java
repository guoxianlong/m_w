package cn.mmb.delivery.domain.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.type.Alias;

@Alias("ytWayBillTrace")
public class YtWayBillTrace extends WayBillTrace {
	
	/**
	 * 圆通物流状态对照关系
	 */
	public static Map<String, Integer> ytDeliverStatus = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
	{
		put("揽收", 1);
		put("派件中", 5);
		put("签收", 7);
		put("失败签收录入", 8);
	}};
}
