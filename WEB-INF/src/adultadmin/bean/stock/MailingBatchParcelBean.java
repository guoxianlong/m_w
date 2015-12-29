package adultadmin.bean.stock;

import java.util.List;

/**
 * 物流自配送，发货邮包
 * @author Administrator
 *
 */
public class MailingBatchParcelBean {
	public int id;
	public int mailingBatchId;//发货波次id
	public String mailingBatchCode;//发货波次编号
	public String code;//发货邮包编号
	
	public int packageCount;//订单包裹数量
	public float totalWeight;//总重量
	public float totalPrice;//货款总额
	public List MailingBatchPackageList;//邮包中的包裹列表
	public MailingBatchBean mbb;
	public int status;//邮包状态，0是未复核，1是已复核，2是复核失败
	
	/**
	 * 状态：未复核
	 */
	public static final int status0 = 0;
	
	/**
	 * 状态：已复核
	 */
	public static final int status1 = 1;
	
	/**
	 * 状态：复核失败
	 */
	public static final int status2 = 2;
	
	public MailingBatchBean getMbb() {
		return mbb;
	}
	public void setMbb(MailingBatchBean mbb) {
		this.mbb = mbb;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMailingBatchId() {
		return mailingBatchId;
	}
	public void setMailingBatchId(int mailingBatchId) {
		this.mailingBatchId = mailingBatchId;
	}
	public String getMailingBatchCode() {
		return mailingBatchCode;
	}
	public void setMailingBatchCode(String mailingBatchCode) {
		this.mailingBatchCode = mailingBatchCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public List getMailingBatchPackageList() {
		return MailingBatchPackageList;
	}
	public void setMailingBatchPackageList(List mailingBatchPackageList) {
		MailingBatchPackageList = mailingBatchPackageList;
	}
	public int getPackageCount() {
		return packageCount;
	}
	public void setPackageCount(int packageCount) {
		this.packageCount = packageCount;
	}
	public float getTotalWeight() {
		return totalWeight;
	}
	public void setTotalWeight(float totalWeight) {
		this.totalWeight = totalWeight;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getStatusName(){
		if(this.getStatus()==0){
			return "未复核";
		}else if(this.getStatus()==1){
			return "已复核";
		}else if(this.getStatus()==2){
			return "复核失败";
		}
		return "";
	}
}
