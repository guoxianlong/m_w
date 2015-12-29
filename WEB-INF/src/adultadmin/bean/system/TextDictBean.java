package adultadmin.bean.system;

import java.util.HashMap;


/**
 * 
 *  <code>TextDictBean.java</code>
 *  <p>功能:加备注的 字典表
 *  
 *  <p>Copyright 商机无限 2012 All right reserved.
 *  @author 李双 lishuang@ebinf.com 时间 Apr 23, 2012 4:05:58 PM	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class TextDictBean {

	public int id;

	public int type;//  订单状态  设置： 在订单状态值+8  （避免以后再加入订单状态重复值） 发货状态 值+1 理由同上

	public String content;
	
	public String mark;
	 
	public static HashMap TYPEMAP = new HashMap();
	
	public static HashMap orderStatusMap = new HashMap();
	public static HashMap stockOutStatusMap = new HashMap();
	
	static{
		TYPEMAP.put("1", "岗位职别");
		TYPEMAP.put("9", "电话失败");
		TYPEMAP.put("10", "电话成功");
		TYPEMAP.put("11", "待发货");//新加状态
		TYPEMAP.put("15", "已取消");
		TYPEMAP.put("16", "废弃");
		TYPEMAP.put("2", "发货失败");
		TYPEMAP.put("6", "缺货电话失败");
		TYPEMAP.put("7", "缺货电话成功");
		
		orderStatusMap.put("9", "电话失败");
		orderStatusMap.put("10", "电话成功");
		orderStatusMap.put("11", "待发货");//新加状态
		orderStatusMap.put("15", "已取消");
		orderStatusMap.put("16", "废弃");
		
		stockOutStatusMap.put("2", "发货失败");
		stockOutStatusMap.put("6", "缺货电话失败");
		stockOutStatusMap.put("7", "缺货电话成功");
	}
	
	public String getTypeName(){
		if(TYPEMAP.containsKey(String.valueOf(type)))
			return String.valueOf(TYPEMAP.get(String.valueOf(type)));
		return "错误类别";
	}

	public static String getTypeName(int type){
		if(TYPEMAP.containsKey(String.valueOf(type)))
			return String.valueOf(TYPEMAP.get(String.valueOf(type)));
		return "错误类别";
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public String getMark() {
		return mark;
	}


	public void setMark(String mark) {
		this.mark = mark;
	}
}
