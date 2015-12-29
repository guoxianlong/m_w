package mmb.stock.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ReturnedUpShelfBean {
	public int id;
	public int status=0;
	public String createDatetime;
	public String remark="";
	public String confirmDatetime;
	public int createUserId=0;
	public String auditingDatetime;
	public int auditingUserId=0;
	public String code="";
	public String createUserName="";
	public String auditingUserName="";
	public String confirmUserName="";
	public String completeDatetime;
	public int completeUserId=0;
	public String completeUsername="";
	public String statusName;//当前状态名称
	public String passageWholeCode;//巷道全码
	private int productCount;//上架量
	
	/**
	 * 退货上架汇总单，生成作业
	 */
	public static final int OPERATION_STATUS37=37;
	
	/**
	 * 退货上架汇总单，提交并确认，已提交
	 */
	public static final int OPERATION_STATUS38=38;
	
	/**
	 * 退货上架汇总单，交接阶段一，已审核
	 */
	public static final int OPERATION_STATUS39=39;
	
	/**
	 * 退货上架汇总单，交接阶段二
	 */
	public static final int OPERATION_STATUS40=40;
	
	/**
	 * 退货上架汇总单，交接阶段三
	 */
	public static final int OPERATION_STATUS41=41;
	
	/**
	 * 退货上架汇总单，交接阶段四
	 */
	public static final int OPERATION_STATUS42=42;
	
	/**
	 * 退货上架单，作业完成，作业成功
	 */
	public static final int OPERATION_STATUS43=43;
	
	/**
	 * 退货上架汇总单，作业完成，待复核
	 */
//	public static final int OPERATION_STATUS44=44;
	
	/**
	 * 退货上架汇总单，作业完成，作业失败
	 */
	public static final int OPERATION_STATUS45=45;
	
	/**
	 * 退货上架汇总单，作业结束
	 */
	public static final int OPERATION_STATUS46=46;
	
	public static HashMap statusMap = new LinkedHashMap();
	
	static {
		statusMap.put(Integer.valueOf(OPERATION_STATUS37), "未处理");
		statusMap.put(Integer.valueOf(OPERATION_STATUS38), "已提交");
		statusMap.put(Integer.valueOf(OPERATION_STATUS39), "已审核");
		statusMap.put(Integer.valueOf(OPERATION_STATUS40), "交接阶段二");
		statusMap.put(Integer.valueOf(OPERATION_STATUS41), "交接阶段三");
		statusMap.put(Integer.valueOf(OPERATION_STATUS42), "交接阶段四");
		statusMap.put(Integer.valueOf(OPERATION_STATUS43), "作业成功");
//		statusMap.put(Integer.valueOf(OPERATION_STATUS44), "作业完成");
		statusMap.put(Integer.valueOf(OPERATION_STATUS45), "作业失败");
		statusMap.put(Integer.valueOf(OPERATION_STATUS46), "作业结束");
	}
	
	
	private ArrayList cargoInfoList = new ArrayList();//下架单列表显示用的
	
	
	
	
	

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}


	
	public int getId() {
		return id;
	}
	public String getStatusName() {
		return statusName;
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getConfirmDatetime() {
		return confirmDatetime;
	}
	public void setConfirmDatetime(String confirmDatetime) {
		this.confirmDatetime = confirmDatetime;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getAuditingDatetime() {
		return auditingDatetime;
	}
	public void setAuditingDatetime(String auditingDatetime) {
		this.auditingDatetime = auditingDatetime;
	}
	public int getAuditingUserId() {
		return auditingUserId;
	}
	public void setAuditingUserId(int auditingUserId) {
		this.auditingUserId = auditingUserId;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getAuditingUserName() {
		return auditingUserName;
	}
	public void setAuditingUserName(String auditingUserName) {
		this.auditingUserName = auditingUserName;
	}
	public String getConfirmUserName() {
		return confirmUserName;
	}
	public void setConfirmUserName(String confirmUserName) {
		this.confirmUserName = confirmUserName;
	}
	public ArrayList getCargoInfoList() {
		return cargoInfoList;
	}
	public void setCargoInfoList(ArrayList cargoInfoList) {
		this.cargoInfoList = cargoInfoList;
	}

	public String getCompleteDatetime() {
		return completeDatetime;
	}

	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
	}

	public int getCompleteUserId() {
		return completeUserId;
	}

	public void setCompleteUserId(int completeUserId) {
		this.completeUserId = completeUserId;
	}

	public String getCompleteUsername() {
		return completeUsername;
	}

	public void setCompleteUsername(String completeUsername) {
		this.completeUsername = completeUsername;
	}



	public String getPassageWholeCode() {
		return passageWholeCode;
	}



	public void setPassageWholeCode(String passageWholeCode) {
		this.passageWholeCode = passageWholeCode;
	}



	public int getProductCount() {
		return productCount;
	}



	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}
	
}
