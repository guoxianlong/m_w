package mmb.stock.stat;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

public class SortingBatchBean   {
//	id,int(11)
//	批次号 code,varchar(45)////
//	生成时间 create_datetime,datetime
//	完成时间 complete_datetime,datetime
//	作业仓 storage,int(11)
//	分拣状态 status,int(11)////
//  分拣波次列表 sortingBatchGroupList
//  分拣批次里波次的各个状态值(key)以及对应的波次数量(value) sortingBatchGroupStatusCountMap
	/**无需处理
	 */
	public static int STATUS = 5;
	/**
	 * 未处理
	 */
	public static int STATUS0 = 0;
	/**
	 * 处理中 
	 */
	public static int STATUS1 = 1;
	/**
	 * 未分拣
	 */
	public static int STATUS2 = 2;
	/**
	 * 分拣中 
	 */
	public static int STATUS3 = 3;
	/**
	 * 已完成
	 */
	public static int STATUS4 = 4;
	/**批次生成中：为了防止批次生成之后，批次下面的订单还未生成之前就被操作的一种状态
	 */
	public static int STATUS5 = 6;
	
	/**
	 * 按照批次里所有SKU的总数由大到小排序，取前（3.5*20）个SUK
	 */
	public static final int COUNT = 5;
	/**
     *SKU数不大于30个
	 */
	public static final int PRODUCT_COUNT = 30;
	/**
	 * 订单数不大于20个
	 */
	public static final int ORDER_COUNT = 20;
	public int id;
	public String code;
	public String createDatetime;
	public String completeDatetime;
	public int storage;
	public String storageName;
	public int status;
	public List sortingBatchGroupList;
	public Map sortingBatchGroupStatusCountMap;
	public int sortingBatchGroupCount;
	public int orderCount;
	public int skuCount;
	public List groupList;
	public String parm;
	public String statusName;
	//该批次下没有分配快递公司的订单数量
	public int noAssignDoCount;
	//该批次下没有分配分配的订单数量
	public int noAssignOtCount;
	//该批次下分配给EMS省内的订单数量
	public int emsSnOrderCount;
	//该批次下分配给EMS省外的订单数量
	public int emsSwOrderCount;
	//该批次下分配给sf的订单数量
	public int sfOrderCount;
	//该批次下的ems省内打过单的订单数量
	public int emsSnDdCount;
	//该批次下的ems省外打过单的订单数量
	public int emsSwDdCount;
	//该批次下的顺丰打过单的订单数量
	public int sfDdCount;
	//该批次中可发给顺丰的订单数量
	public int sfCount;
	//判断该批次是多sku批次还是单sku批次,0:单，1：多
	public int type1;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getCompleteDatetime() {
		return completeDatetime;
	}
	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
	}
	public int getStorage() {
		return storage;
	}
	public void setStorage(int storage) {
		this.storage = storage;
	}
	public String getStorageName() {
		return storageName;
	}
	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}
	public int getStatus() {
		return this.status;
	}
	public String getStatusName(int status) {
		if (status == STATUS) {
			return "无需处理";
		}else if (status == STATUS0) {
			return "未处理";
		}else if(status == STATUS1){
			return "处理中";
		}else if(status == STATUS2){
			return "未分拣";
		}else if(status == STATUS3){
			return "分拣中";
		}else if(status == STATUS4){
			return "已完成";
		}else if(status == STATUS5){
			return "生成中...";
		}
		else{
			return "";
		}
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public List getSortingBatchGroupList() {
		return sortingBatchGroupList;
	}
	public void setSortingBatchGroupList(List sortingBatchGroupList) {
		this.sortingBatchGroupList = sortingBatchGroupList;
	}
	public Map getSortingBatchGroupStatusCountMap() {
		return sortingBatchGroupStatusCountMap;
	}
	public void setSortingBatchGroupStatusCountMap(
			Map sortingBatchGroupStatusCountMap) {
		this.sortingBatchGroupStatusCountMap = sortingBatchGroupStatusCountMap;
	}
	public int getSortingBatchGroupCount() {
		return sortingBatchGroupCount;
	}
	public void setSortingBatchGroupCount(int sortingBatchGroupCount) {
		this.sortingBatchGroupCount = sortingBatchGroupCount;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public int getSkuCount() {
		return skuCount;
	}
	public void setSkuCount(int skuCount) {
		this.skuCount = skuCount;
	}
	public List getGroupList() {
		return groupList;
	}
	public void setGroupList(List groupList) {
		this.groupList = groupList;
	}
	public String getParm() {
		return parm;
	}
	public void setParm(String parm) {
		this.parm = parm;
	}
	public static String getCircleNumber(int number){
		switch(number){
			case 1: return"①";
			case 2: return"②";
			case 3: return"③";
			case 4: return"④";
			case 5: return"⑤";
			case 6: return"⑥";
			case 7: return"⑦";
			case 8: return"⑧";
			case 9: return"⑨";
			case 10: return"⑩";
			case 11: return"⑪";
			case 12: return"⑫";
			case 13: return"⑬";
			case 14: return"⑭";
			case 15: return"⑮";
			case 16: return"⑯";
			case 17: return"⑰";
			case 18: return"⑱";
			case 19: return"⑲";
			case 20: return"⑳";
			default:return"error";
		}
	}
	public int getSfOrderCount() {
		return sfOrderCount;
	}
	public void setSfOrderCount(int sfOrderCount) {
		this.sfOrderCount = sfOrderCount;
	}
	public int getNoAssignDoCount() {
		return noAssignDoCount;
	}
	public void setNoAssignDoCount(int noAssignDoCount) {
		this.noAssignDoCount = noAssignDoCount;
	}
	public int getNoAssignOtCount() {
		return noAssignOtCount;
	}
	public void setNoAssignOtCount(int noAssignOtCount) {
		this.noAssignOtCount = noAssignOtCount;
	}
	public int getEmsSnOrderCount() {
		return emsSnOrderCount;
	}
	public void setEmsSnOrderCount(int emsSnOrderCount) {
		this.emsSnOrderCount = emsSnOrderCount;
	}
	public int getEmsSwOrderCount() {
		return emsSwOrderCount;
	}
	public void setEmsSwOrderCount(int emsSwOrderCount) {
		this.emsSwOrderCount = emsSwOrderCount;
	}
	public int getEmsSnDdCount() {
		return emsSnDdCount;
	}
	public void setEmsSnDdCount(int emsSnDdCount) {
		this.emsSnDdCount = emsSnDdCount;
	}
	public int getEmsSwDdCount() {
		return emsSwDdCount;
	}
	public void setEmsSwDdCount(int emsSwDdCount) {
		this.emsSwDdCount = emsSwDdCount;
	}
	public int getSfDdCount() {
		return sfDdCount;
	}
	public void setSfDdCount(int sfDdCount) {
		this.sfDdCount = sfDdCount;
	}
	public int getSfCount() {
		return sfCount;
	}
	public void setSfCount(int sfCount) {
		this.sfCount = sfCount;
	}
	public int getType1() {
		return type1;
	}
	public void setType1(int type1) {
		this.type1 = type1;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	
}
