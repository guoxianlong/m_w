package mmb.dcheck.service;

import java.util.List;
import java.util.Map;

import mmb.dcheck.model.DynamicCheckBean;
import mmb.dcheck.model.DynamicCheckCargoBean;
import mmb.ware.cargo.model.CargoInfo;


/**
 * @author hp
 *动态盘点计划Service
 */

public interface DCheckService {
	/**
	 * 获取动态盘点计划列表
	 * @param condition
	 * @return 动态盘点计划集合
	 */
	public List<DynamicCheckBean> getDynamicCheckBeans(Map<String,String> condition);
	/**
	 * 获取动态盘点计划总条数
	 * @param condition
	 * @return  总条数
	 */
	public int getDynamicCheckBeanCount(Map<String, String> condition);
	/**
	 * 添加动态盘点计划
	 * @param dynamicCheck
	 * @return  
	 */
	public int addDynamicCheckBean(DynamicCheckBean dynamicCheck) throws Exception;
	
	/***
	 * 结束盘点
	 * @param id
	 */
	public int endDCheck(DynamicCheckBean  dynamicCheckBean);
	
	
	/**
	 * 获取动态盘点明细列表
	 * @param condition
	 * @return 动态盘点计划集合
	 */
	public List<DynamicCheckCargoBean> getDynamicCheckCargoBeans(Map<String,String> condition);
	/**
	 * 获取动态盘点明细总条数
	 * @param condition
	 * @return  总条数
	 */
	public int getDynamicCheckCargoBeanCount(Map<String, String> condition);
	
	/***
	 * 重新终盘
	 * @param id
	 */
	public int afreshDCheck(Integer  id);
	
	/**
	 * 获取动态盘点计划
	 * @param condition
	 * @return 动态盘点计划
	 */
	public DynamicCheckBean getDynamicCheckBean(String condition);
	
	public CargoInfo getCargoByCondition(String condition);
}
