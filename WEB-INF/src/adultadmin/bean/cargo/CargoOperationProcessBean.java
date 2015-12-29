package adultadmin.bean.cargo;

/**
 * 货位作业单操作流程
 * @author Administrator
 *
 */
public class CargoOperationProcessBean {
	public int id;
	public int operationType;//作业单类型，1上架2下架3补货4调拨
	public int process;//阶段，1未处理，2待作业，3交接一，4交接二，5交接三，6交接四，7作业结束-待复核，8作业结束-作业成功，9作业结束-作业失败
	public int useStatus;//是否使用，0未使用，1使用
	public int handleType;//操作方式，0人工确认，1设备确认
	public int confirmType;//作业判断，0不做判断，1源货位，2目的货位，完成阶段为三数串联，0为否，1为是
	public String operName;//人员操作
	public String statusName;//作业状态
	public int effectTime;//时效（分钟）
	public int deptId1;//职能归属，一级部门
	public int deptId2;//职能归属，二级部门
	public int storageId;//所属仓库
	
	/**
	 * 上架单，生成作业
	 */
	public static final int OPERATION_STATUS1=1;
	
	/**
	 * 上架单，提交并确认
	 */
	public static final int OPERATION_STATUS2=2;
	
	/**
	 * 上架单，交接阶段一
	 */
	public static final int OPERATION_STATUS3=3;
	
	/**
	 * 上架单，交接阶段二
	 */
	public static final int OPERATION_STATUS4=4;
	
	/**
	 * 上架单，交接阶段三
	 */
	public static final int OPERATION_STATUS5=5;
	
	/**
	 * 上架单，交接阶段四
	 */
	public static final int OPERATION_STATUS6=6;
	
	/**
	 * 上架单，作业结束，待复核
	 */
	public static final int OPERATION_STATUS7=7;
	
	/**
	 * 上架单，作业结束，作业成功
	 */
	public static final int OPERATION_STATUS8=8;
	
	/**
	 * 上架单，作业结束，作业失败
	 */
	public static final int OPERATION_STATUS9=9;
	
	/**
	 * 下架单，生成作业
	 */
	public static final int OPERATION_STATUS10=10;
	
	/**
	 * 下架单，提交并确认
	 */
	public static final int OPERATION_STATUS11=11;
	
	/**
	 * 下架单，交接阶段一
	 */
	public static final int OPERATION_STATUS12=12;
	
	/**
	 * 下架单，交接阶段二
	 */
	public static final int OPERATION_STATUS13=13;
	
	/**
	 * 下架单，交接阶段三
	 */
	public static final int OPERATION_STATUS14=14;
	
	/**
	 * 下架单，交接阶段四
	 */
	public static final int OPERATION_STATUS15=15;
	
	/**
	 * 下架单，作业完成，待复核
	 */
	public static final int OPERATION_STATUS16=16;
	
	/**
	 * 下架单，作业完成，作业成功
	 */
	public static final int OPERATION_STATUS17=17;
	
	/**
	 * 下架单，作业完成，作业失败
	 */
	public static final int OPERATION_STATUS18=18;
	
	/**
	 * 补货单，生成作业
	 */
	public static final int OPERATION_STATUS19=19;
	
	/**
	 * 补货单，提交并确认
	 */
	public static final int OPERATION_STATUS20=20;
	
	/**
	 * 补货单，交接阶段一
	 */
	public static final int OPERATION_STATUS21=21;
	
	/**
	 * 补货单，交接阶段二
	 */
	public static final int OPERATION_STATUS22=22;
	
	/**
	 * 补货单，交接阶段三
	 */
	public static final int OPERATION_STATUS23=23;
	
	/**
	 * 补货单，交接阶段四
	 */
	public static final int OPERATION_STATUS24=24;
	
	/**
	 * 补货单，作业完成，待复核
	 */
	public static final int OPERATION_STATUS25=25;
	
	/**
	 * 补货单，作业完成，作业成功
	 */
	public static final int OPERATION_STATUS26=26;
	
	/**
	 * 补货单，作业完成，作业失败
	 */
	public static final int OPERATION_STATUS27=27;
	
	/**
	 * 货位间调拨单，生成作业
	 */
	public static final int OPERATION_STATUS28=28;
	
	/**
	 * 货位间调拨单，提交并确认
	 */
	public static final int OPERATION_STATUS29=29;
	
	/**
	 * 货位间调拨单，交接阶段一
	 */
	public static final int OPERATION_STATUS30=30;
	
	/**
	 * 货位间调拨单，交接阶段二
	 */
	public static final int OPERATION_STATUS31=31;
	
	/**
	 * 货位间调拨单，交接阶段三
	 */
	public static final int OPERATION_STATUS32=32;
	
	/**
	 * 货位间调拨单，交接阶段四
	 */
	public static final int OPERATION_STATUS33=33;
	
	/**
	 * 货位间调拨单，作业完成，待复核
	 */
	public static final int OPERATION_STATUS34=34;
	
	/**
	 * 货位间调拨单，作业完成，作业成功
	 */
	public static final int OPERATION_STATUS35=35;
	
	/**
	 * 货位间调拨单，作业完成，作业失败
	 */
	public static final int OPERATION_STATUS36=36;
	
	/**
	 * 退货上架单，生成作业
	 */
	public static final int OPERATION_STATUS37=37;
	
	/**
	 * 退货上架单，提交并确认，已提交
	 */
	public static final int OPERATION_STATUS38=38;
	
	/**
	 * 退货上架单，交接阶段一，已审核
	 */
	public static final int OPERATION_STATUS39=39;
	
	/**
	 * 退货上架单，交接阶段二
	 */
	public static final int OPERATION_STATUS40=40;
	
	/**
	 * 退货上架单，交接阶段三
	 */
	public static final int OPERATION_STATUS41=41;
	
	/**
	 * 退货上架单，交接阶段四
	 */
	public static final int OPERATION_STATUS42=42;
	
	/**
	 * 退货上架单，作业完成，待复核
	 */
	public static final int OPERATION_STATUS43=43;
	
	/**
	 * 退货上架单，作业完成，作业成功
	 */
	public static final int OPERATION_STATUS44=44;
	
	/**
	 * 退货上架单，作业完成，作业失败
	 */
	public static final int OPERATION_STATUS45=45;
	
	public int getUseStatus() {
		return useStatus;
	}
	public void setUseStatus(int useStatus) {
		this.useStatus = useStatus;
	}
	public int getStorageId() {
		return storageId;
	}
	public void setStorageId(int storageId) {
		this.storageId = storageId;
	}
	public int getDeptId1() {
		return deptId1;
	}
	public void setDeptId1(int deptId1) {
		this.deptId1 = deptId1;
	}
	public int getDeptId2() {
		return deptId2;
	}
	public void setDeptId2(int deptId2) {
		this.deptId2 = deptId2;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOperationType() {
		return operationType;
	}
	public void setOperationType(int operationType) {
		this.operationType = operationType;
	}
	public int getProcess() {
		return process;
	}
	public void setProcess(int process) {
		this.process = process;
	}
	public int getHandleType() {
		return handleType;
	}
	public void setHandleType(int handleType) {
		this.handleType = handleType;
	}
	public int getConfirmType() {
		return confirmType;
	}
	public void setConfirmType(int confirmType) {
		this.confirmType = confirmType;
	}
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public int getEffectTime() {
		return effectTime;
	}
	public void setEffectTime(int effectTime) {
		this.effectTime = effectTime;
	}
	
	
}
