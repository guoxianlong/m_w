package com.mmb.framework.metatype;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mmb.framework.mapper.JaxbMapper;
import com.mmb.framework.mapper.JsonMapper;

public class BaseDto extends HashMap<String, Object> implements Dto,Serializable {

public BaseDto(){}
	
	public BaseDto(String key, Object value){
		put(key, value);
	}
	
	public BaseDto(Boolean success){
		setSuccess(success);
	}
	
	public BaseDto(Boolean success, String msg){
		setSuccess(success);
		setMsg(msg);
	}

	/**
	 * 以BigDecimal类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return BigDecimal 键值
	 */
	public BigDecimal getAsBigDecimal(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "BigDecimal", null);
		if (obj != null)
			return (BigDecimal) obj;
		else
			return null;
	}

	/**
	 * 以Date类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Date 键值
	 */
	public Date getAsDate(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Date", "yyyy-MM-dd");
		if (obj != null)
			return (Date) obj;
		else
			return null;
	}

	/**
	 * 以Integer类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Integer 键值
	 */
	public Integer getAsInteger(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Integer", null);
		if (obj != null)
			return (Integer) obj;
		else
			return null;
	}

	/**
	 * 以Long类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Long 键值
	 */
	public Long getAsLong(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Long", null);
		if (obj != null)
			return (Long) obj;
		else
			return null;
	}

	/**
	 * 以String类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return String 键值
	 */
	public String getAsString(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "String", null);
		if (obj != null)
			return (String) obj;
		else
			return "";
	}
	
	/**
	 * 以List类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return List 键值
	 */
	public List getAsList(String key){
		return (List)get(key);
	}

	/**
	 * 以Timestamp类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Timestamp 键值
	 */
	public Timestamp getAsTimestamp(String key) {
		Object obj = TypeCaseHelper.convert(get(key), "Timestamp", "yyyy-MM-dd HH:mm:ss");
		if (obj != null)
			return (Timestamp) obj;
		else
			return null;
	}
	
	/**
	 * 以Boolean类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Timestamp 键值
	 */
	public Boolean getAsBoolean(String key){
		Object obj = TypeCaseHelper.convert(get(key), "Boolean", null);
		if (obj != null)
			return (Boolean) obj;
		else
			return false;
	}

	public String toXml() {
		JaxbMapper jaxbContext = new JaxbMapper();
		return jaxbContext.toXml(this,"UTF-8");
	}
	
	/**
	 * 将此Dto对象转换为Json格式字符串<br>
	 * 
	 * @return string 返回Json格式字符串
	 */
	public String toJson() {
		return JsonMapper.buildNormalMapper().toJson(this);
	}
	
	/**
	 * 将此Dto对象转换为Json格式字符串(带日期时间型)<br>
	 * 
	 * @return string 返回Json格式字符串
	 */
	public String toJson(String pFormat){
		return JsonMapper.buildNormalMapper(pFormat).toJson(this);
	}
	
	/**
	 * 设置交易状态
	 * 
	 * @param pSuccess
	 */
	public void setSuccess(Boolean pSuccess){
		put("success", pSuccess);
		if (pSuccess) {
			//put("bflag", "1");
		}else {
			//put("bflag", "0");
		}
		
	}
	
	/**
	 * 获取交易状态
	 * 
	 * @param pSuccess
	 */
	public Boolean getSuccess(){
		return getAsBoolean("success");
	}
	
	public void setMsg(String pMsg){
		put("msg", pMsg);
	}
	
	public String getMsg(){
		return getAsString("msg");
	}

}
