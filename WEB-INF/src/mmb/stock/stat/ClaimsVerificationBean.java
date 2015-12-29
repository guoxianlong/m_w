package mmb.stock.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.order.AuditPackageBean;

public class ClaimsVerificationBean {
	
	public String code; //理赔单号
	public int id;	//id
	public String orderCode; //订单编号
	public String packageCode; // 包裹单号
	public String createUserName;	
	public String auditUserName;
	public String confirmUserName;
	public int createUserId;
	public int auditUserId;
	public int confirmUserId;
	public String createTime;
	public String auditTime;
	public String confirmTime;
	public String completeTime;
	public int completeUserId;
	public String completeUserName;
	public int status;
	public int deliver; //快递公司
	public String deliverCompanyName; //快递公司名称
	public List claimsVerificationProductList = new ArrayList(); //理赔单商品列
	public int wareArea;	//库地区
	public String bsbyCodes;	//报损单号 逗号隔开
	public List<BsbyOperationnoteBean> bsbyList = new ArrayList<BsbyOperationnoteBean>(); //对应的 报损单列表
	public AuditPackageBean auditPackageBean;  //对应的包裹单
	public String deliverDate;  //发货日期
	public int type;  //理赔类型
	public float price; //理赔金额
	public int isTicket; //是否需要开发票
	
	public int hasGift;  // 是否有赠品  0 没有 1， 有  --这里的赠品是 采购赠品
	public String reasonRemark; //理赔原因备注内容
	public int reasonType;  //理赔原因选项key
	
	public static final int CLAIMS_UNDEAL = 0;
	public static final int CLAIMS_CONFIRM = 1;
	public static final int CLAIMS_AUDIT_FAIL = 2;
	public static final int CLAIMS_AUDIT = 3;
	public static final int CLAIMS_COMPLETE = 4;
	
	//理赔类型值
	public static final int TYPE_WHOLE = 0;  	//整单理赔
	public static final int TYPE_BASE_ON_SKU = 1; //按照sku理赔
	public static final int TYPE_TRIPLE_MAILING_PRIZE = 2; //按照三倍邮费理赔
	public static final int TYPE_PACKAGING_CLAIMS = 3; //包装理赔
	public static final int TYPE_MIX = 4; //按商品和包装理赔
	
	
	//理赔时是否开具发票 
	public static final int TICKET_HAS = 1;  	//开具发票
	public static final int TICKET_NO = 0;   //不用开票
	public static final int HAS_GIFT_YES = 1;  	//有赠品  --这里的赠品是 采购赠品
	public static final int HAS_GIFT_NO = 0;   //无赠品 --这里的赠品是 采购赠品
	
	public static Map<Integer,String> reasonTypeMap = new HashMap<Integer,String>();
	
	static {
		reasonTypeMap = new HashMap<Integer,String>();
		reasonTypeMap.put(0, "包装破损，可换");
		reasonTypeMap.put(1, "包装破损，不可换");
		reasonTypeMap.put(2, "内件物品溢漏");
		reasonTypeMap.put(3, "被溢漏商品污染");
		reasonTypeMap.put(4, "内件物品破损");
		reasonTypeMap.put(5, "内件物品丢失");
		reasonTypeMap.put(6, "透明外膜被撕");
		reasonTypeMap.put(7, "内存卡丢失");
		reasonTypeMap.put(8, "3C礼包丢失耳塞");
		reasonTypeMap.put(9, "丢失手机");
		reasonTypeMap.put(10, "商品被掉包");
		reasonTypeMap.put(11, "其他");
	}
	 
	//用来显示理赔单（张晔）
	public String receiptsNumberString;
	public String bsbyIdString;
	public String lookupString;
	public String ifDelString;
	
	public String isAdd;
	public String wareAreaName;
	
	public String getStatusName() {
		
		String result = "";
		if( this.status == ClaimsVerificationBean.CLAIMS_UNDEAL) {
			result = "未处理";
		} else if(this.status == ClaimsVerificationBean.CLAIMS_CONFIRM) {
			result = "已提交";
		} else if(this.status == ClaimsVerificationBean.CLAIMS_AUDIT_FAIL) {
			result = "审核不通过";
		} else if(this.status == ClaimsVerificationBean.CLAIMS_AUDIT) {
			result = "审核通过";
		} else if(this.status == ClaimsVerificationBean.CLAIMS_COMPLETE) {
			result = "已完成";
		}
		return result;
		
	}
	
	public static Map getAllStatusName() {
		Map map = new HashMap();
		map.put(CLAIMS_UNDEAL, "未处理");
		map.put(CLAIMS_CONFIRM, "已提交");
		map.put(CLAIMS_AUDIT_FAIL, "审核不通过");
		map.put(CLAIMS_AUDIT, "审核通过");
		map.put(CLAIMS_COMPLETE, "已完成");
		return map;
	}
	
	public String getIsAdd() {
		return isAdd;
	}

	public void setIsAdd(String isAdd) {
		this.isAdd = isAdd;
	}

	public String getWareAreaName() {
		return wareAreaName;
	}

	public void setWareAreaName(String wareAreaName) {
		this.wareAreaName = wareAreaName;
	}

	public String getReceiptsNumberString() {
		return receiptsNumberString;
	}

	public void setReceiptsNumberString(String receiptsNumberString) {
		this.receiptsNumberString = receiptsNumberString;
	}

	public String getBsbyIdString() {
		return bsbyIdString;
	}

	public void setBsbyIdString(String bsbyIdString) {
		this.bsbyIdString = bsbyIdString;
	}

	public String getLookupString() {
		return lookupString;
	}

	public void setLookupString(String lookupString) {
		this.lookupString = lookupString;
	}

	public String getIfDelString() {
		return ifDelString;
	}

	public void setIfDelString(String ifDelString) {
		this.ifDelString = ifDelString;
	}

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getAuditUserName() {
		return auditUserName;
	}
	public void setAuditUserName(String auditUserName) {
		this.auditUserName = auditUserName;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public int getAuditUserId() {
		return auditUserId;
	}
	public void setAuditUserId(int auditUserId) {
		this.auditUserId = auditUserId;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(String auditTime) {
		this.auditTime = auditTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public String getDeliverCompanyName() {
		return deliverCompanyName;
	}

	public void setDeliverCompanyName(String deliverCompanyName) {
		this.deliverCompanyName = deliverCompanyName;
	}

	public List getClaimsVerificationProductList() {
		return claimsVerificationProductList;
	}

	public void setClaimsVerificationProductList(List claimsVerificationProductList) {
		this.claimsVerificationProductList = claimsVerificationProductList;
	}

	public int getWareArea() {
		return wareArea;
	}

	public void setWareArea(int wareArea) {
		this.wareArea = wareArea;
	}

	public String getBsbyCodes() {
		return bsbyCodes;
	}

	public void setBsbyCodes(String bsbyCodes) {
		this.bsbyCodes = bsbyCodes;
	}

	public List<BsbyOperationnoteBean> getBsbyList() {
		return bsbyList;
	}

	public void setBsbyList(List<BsbyOperationnoteBean> bsbyList) {
		this.bsbyList = bsbyList;
	}
	
	/**
	 * 返回 最后一个逗号被去掉的
	 * @return
	 */
	public String getBsbyCodesFixed() {
		if( this.bsbyCodes == null || this.bsbyCodes.equals("") ) {
			return this.bsbyCodes;
		} else {
			return this.bsbyCodes.substring(0, (this.bsbyCodes.length() - 1 ));
		}
	}
	
	public String getDeliverCompanyNameDirectly() {
		return (String)voOrder.deliverMapAll.get(""+this.deliver);
	}

	public AuditPackageBean getAuditPackageBean() {
		return auditPackageBean;
	}

	public void setAuditPackageBean(AuditPackageBean auditPackageBean) {
		this.auditPackageBean = auditPackageBean;
	}

	public String getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(String deliverDate) {
		this.deliverDate = deliverDate;
	}

	public String getConfirmUserName() {
		return confirmUserName;
	}

	public void setConfirmUserName(String confirmUserName) {
		this.confirmUserName = confirmUserName;
	}

	public int getConfirmUserId() {
		return confirmUserId;
	}

	public void setConfirmUserId(int confirmUserId) {
		this.confirmUserId = confirmUserId;
	}

	public String getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getIsTicket() {
		return isTicket;
	}

	public void setIsTicket(int isTicket) {
		this.isTicket = isTicket;
	}

	public String getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(String completeTime) {
		this.completeTime = completeTime;
	}

	public int getCompleteUserId() {
		return completeUserId;
	}

	public void setCompleteUserId(int completeUserId) {
		this.completeUserId = completeUserId;
	}

	public String getCompleteUserName() {
		return completeUserName;
	}

	public void setCompleteUserName(String completeUserName) {
		this.completeUserName = completeUserName;
	}

	public int getHasGift() {
		return hasGift;
	}

	public void setHasGift(int hasGift) {
		this.hasGift = hasGift;
	}

	public int getReasonType() {
		return reasonType;
	}

	public void setReasonType(int reasonType) {
		this.reasonType = reasonType;
	}

	public String getReasonRemark() {
		return reasonRemark;
	}

	public void setReasonRemark(String reasonRemark) {
		this.reasonRemark = reasonRemark;
	}
	public String getReasonTypeName() {
		return ClaimsVerificationBean.reasonTypeMap.get(this.reasonType);
	}

}
