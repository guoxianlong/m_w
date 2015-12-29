package mmb.tms.dao;

import java.util.List;
import java.util.Map;

public interface EffectDao {
	/**
	 * 获取常规类列表
	* @Description: 
	* @author ahc
	 */
	public List<Map<String,String>> getRegularclazzList(Map<String,String> map);

	/**
	 * 获取常规类列表总数
	* @Description: 
	* @author ahc
	 */
	public int getRegularClazzCount(Map<String, String> map);
	
	/**
	 * 获取时效类列表
	* @Description: 
	* @author ahc
	 */
	public List<Map<String,String>> getPrescriptionList(Map<String,String> map);
	
	/**
	 * 获取时效类从属sql“该类型的总单量”(分开获取，优化sql)
	* @Description: 
	* @author ahc
	 */
	public List<Map<String, String>> getPrescriptionForThisCount(Map<String, String> map);
	
	/**
	 * 获取时效类从属sql“退货单总量”(分开获取，优化sql)
	* @Description: 
	* @author ahc
	 */
	public List<Map<String, String>> getPrescriptionForRenturnCount(Map<String, String> map);
	

	/**
	 * 获取时效类列表总数
	* @Description: 
	* @author ahc
	 */
	public int getPrescriptionCount(Map<String, String> map);
	
	/**
	 * 获取客诉类列表
	* @Description: 
	* @author ahc
	 */
	public List<Map<String,String>> getCustomerList(Map<String,String> map);

	/**
	 * 获取客诉类列表总数
	* @Description: 
	* @author ahc
	 */
	public int getCustomerCount(Map<String, String> map);
	/**
	 * 获取观察类列表
	* @Description: 
	* @author ahc
	 */
	public List<Map<String,String>> getObservationList(Map<String,String> map);

	/**
	 * 获取观察类列表总数
	* @Description: 
	* @author ahc
	 */
	public int getObservationCount(Map<String, String> map);

}
