package mmb.tms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface IEffectService {
	
	/**
	 * 获取常规类
	* @Description: 
	* @author ahc
	 */
	public List getRegularClazz(Map<String,String> map);
	
	/**
	 * 获取常规类总数
	* @Description: 
	* @author ahc
	 */
	public int getRegularClazzCount(Map<String,String> map);
	
	/**
	 * 获取时效类
	* @Description: 
	* @author ahc
	 */
	public List getPrescriptionList(Map<String,String> map);
	
	/**
	 * 获取时效类从属sql“该类型的总单量”(分开获取，优化sql)
	* @Description: 
	* @author ahc
	 */
	public List getPrescriptionForThisCount(Map<String,String> map);
	
	/**
	 * 获取时效类从属sql“退后单总量”(分开获取，优化sql)
	* @Description: 
	* @author ahc
	 */
	public List getPrescriptionForRenturnCount(Map<String,String> map);
	
	/**
	 * 获取时效类总数
	* @Description: 
	* @author ahc
	 */
	public int getPrescriptionCount(Map<String,String> map);
	
	/**
	 * 获取客诉类
	* @Description: 
	* @author ahc
	 */
	public List getCustomerList(Map<String,String> map);
	
	/**
	 * 获取客诉类总数
	* @Description: 
	* @author ahc
	 */
	public int getCustomerCount(Map<String,String> map);
	
	/**
	 * 获取观察类
	* @Description: 
	* @author ahc
	 */
	public List getObservationList(Map<String,String> map);
	
	/**
	 * 获取观察类总数
	* @Description: 
	* @author ahc
	 */
	public int getObservationCount(Map<String,String> map);
	
	/**
	 * 导出excel
	* @Description: 
	* @author ahc
	 */
	public void portExcel(String type,List<Map<String, String>> list , HttpServletResponse response) throws Exception;
}
