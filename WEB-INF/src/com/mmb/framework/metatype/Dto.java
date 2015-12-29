package com.mmb.framework.metatype;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface Dto extends Map<String, Object>{

	public Integer getAsInteger(String pStr);
	public Long getAsLong(String pStr);
	public String getAsString(String pStr);
	public BigDecimal getAsBigDecimal(String pStr);
	public Date getAsDate(String pStr);
	public List getAsList(String key);
	public Timestamp getAsTimestamp(String pStr);
	public Boolean getAsBoolean(String key);

	/**
	 * 将此Dto对象转换为Json格式字符串<br>
	 * 
	 * @return string 返回Json格式字符串
	 */
	public String toJson();

	/**
	 * 将此Dto对象转换为Json格式字符串(带日期时间型)<br>
	 * 
	 * @return string 返回Json格式字符串
	 */
	public String toJson(String pFormat);
	
	/**
	 * 设置交易状态
	 */
	public void setSuccess(Boolean pSuccess);
	
	/**
	 * 获取状态
	 */
	public Boolean getSuccess();
	
	/**
	 * 设置提示信息
	 */
	public void setMsg(String pMsg);
	
	/**
	 * 获取提示信息
	 */
	public String getMsg();
	
	
}
