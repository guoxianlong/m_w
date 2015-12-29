/**   
* @Title: SupplierContractReturn.java 
* @Package com.mmb.supplychain.contract.dto 
* @Description: TODO
* @author 叶二鹏   
* @date 2014年7月23日 下午6:18:51 
* @version V1.0   
*/
package mmb.contract.dto;

import org.apache.ibatis.type.Alias;

/** 
 * @ClassName: SupplierContractReturn 
 * @Description: 返点返利规则实体类
 * @author:叶二鹏
 * @date 2014年7月23日 下午6:18:51 
 *  
 */
@Alias("supplierContractReturn")
public class SupplierContractReturn {
	
	/**
	 * 
	 */
	public static int NO_LATTER = 1;
	
	/**阶梯**/
	public static final int LATTER_YES = 3;
	
	/**不阶梯单一条件**/
	public static final int LATTER_NO_ONE = 1;
	
	/**不阶梯多条件**/
	public static final int LATTER_NO_MORE = 2;
	
	/**是否返送过：否**/
	public static final int IS_RETURNED_NO = 1;
	
	/**是否返送过：是**/
	public static final int IS_RETURNED_YES = 2;
	
	/**添加订单时添加的合同返利信息**/
	public static final int TYPE_ADD = 1;
	
	/** 
	* id
	*/
	private int id;
	
	/** 
	* 关联的合同主表ID
	*/
	private int contractId;
	
	/** 
	* 满多少钱返还
	*/
	private double triggerMoneyCash;
	
	/** 
	* 返还现金数
	*/
	private double returnCash;
	
	/** 
	* 返还百分比
	*/
	private float returnPercent;
	
	/** 
	* 是否阶梯(1不阶梯，2阶梯)
	*/
	private int type;
	
	/** 
	* 是否返送过：1，否 2，是
	*/
	private int isReturned;
	/**
	 * 饭送过
	 */
	public static Integer RETURNED_TRUE=2;
	/**
	 * 么饭送过
	 */
	public static Integer RETURNED_FALSE=1;
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the contractId
	 */
	public int getContractId() {
		return contractId;
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}

	/**
	 * @return the triggerMoneyCash
	 */
	public double getTriggerMoneyCash() {
		return triggerMoneyCash;
	}

	/**
	 * @param triggerMoneyCash the triggerMoneyCash to set
	 */
	public void setTriggerMoneyCash(double triggerMoneyCash) {
		this.triggerMoneyCash = triggerMoneyCash;
	}

	/**
	 * @return the returnCash
	 */
	public double getReturnCash() {
		return returnCash;
	}

	/**
	 * @param returnCash the returnCash to set
	 */
	public void setReturnCash(double returnCash) {
		this.returnCash = returnCash;
	}

	/**
	 * @return the returnPercent
	 */
	public float getReturnPercent() {
		return returnPercent;
	}

	/**
	 * @param returnPercent the returnPercent to set
	 */
	public void setReturnPercent(float returnPercent) {
		this.returnPercent = returnPercent;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the isReturned
	 */
	public int getIsReturned() {
		return isReturned;
	}

	/**
	 * @param isReturned the isReturned to set
	 */
	public void setIsReturned(int isReturned) {
		this.isReturned = isReturned;
	}

}
