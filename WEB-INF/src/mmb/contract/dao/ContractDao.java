/** 
* @Description: TODO
* @author: 叶二鹏  
* @date: 2014年8月22日 下午2:53:57 
* @version V1.0   
*/
package mmb.contract.dao;

import java.util.List;

import mmb.contract.dto.SupplierContractMoney;
import mmb.contract.dto.SupplierContractReturn;

/** 
 * @ClassName: ContractDao 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2014年8月22日 下午2:53:57  
 */
public interface ContractDao {
	
	/**
	 * @description:通过订单id获取合同返利规则
	 * @returnType:List<SupplierContractReturn>
	 * @create:2014年8月8日上午10:15:14
	 * @author:叶二鹏
	 */
	List<SupplierContractReturn> getreturnsByOrderId(int order);
	
	/**
	 * @description:获取合同下订单所有金额
	 * @returnType:double
	 * @create:2014年8月8日下午2:46:04
	 * @author:叶二鹏
	 */
	Double getContractTotal(int contractId);
	
	/**
	 * @description:获取订单退回总金额
	 * @returnType:double
	 * @create:2014年8月8日下午2:52:38
	 * @author:叶二鹏
	 */
	Double getContractReturnTotal(int contractId);

	/** 
	* @Description: 保存请款单分摊合同金额信息
	* @param @param monyInfo    设定文件 
	* @return void    返回类型 
	* @data: 2014年8月22日
	* @author: 叶二鹏
	* @throws 
	*/
	void insertContractMoney(SupplierContractMoney monyInfo);

	/** 
	*  @Description: 获得合同信息中的供应商id
	* @param @param monyInfo    设定文件 
	* @return void    返回类型 
	* @data: 2014年8月22日
	* @author: 叶二鹏
	* @throws 
	*/
	Integer getSupplierIdFromContractInfo(int contractId);
	/** 
	* @Description:获取合同一返送过的金额
	* @data: 2014年8月25日
	* @author: 叶二鹏
	* @throws 
	*/
	Double getSendedMoney(int contractId);
	/** 
	* @Description:更新合同返利规则
	* @data: 2014年8月25日
	* @author: 叶二鹏
	* @throws 
	*/
	void updateContractReturn(SupplierContractReturn rt);
	

}
