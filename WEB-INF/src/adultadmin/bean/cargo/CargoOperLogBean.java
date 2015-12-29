package adultadmin.bean.cargo;

/**
 * 货位作业单操作日志
 * @author Administrator
 *
 */
public class CargoOperLogBean {
	public int id;
	public int operId;//作业单id
	public String operCode;//作业单编号
	public String operName;//作业操作
	public String operDatetime;//操作时间
	public int operAdminId;//操作人Id
	public String operAdminName;//操作人姓名
	public String handlerCode;//执行人，若是人工确认则为空，机械确认则记录员工条码的员工编码
	public int effectTime;//是否超时，1进行中，2超出时效，3待复核，4作业成功，5，作业失败
	public String remark;//备注
	public String preStatusName;//操作前作业状态
	public String nextStatusName;//操作后作业状态
	
	public String getPreStatusName() {
		return preStatusName;
	}
	public void setPreStatusName(String preStatusName) {
		this.preStatusName = preStatusName;
	}
	public String getNextStatusName() {
		return nextStatusName;
	}
	public void setNextStatusName(String nextStatusName) {
		this.nextStatusName = nextStatusName;
	}
	/**
	 * 进行中
	 */
	public static final int EFFECT_TIME0=0;
	/**
	 * 超出时效
	 */
	public static final int EFFECT_TIME1=1;
	/**
	 * 待复核
	 */
	public static final int EFFECT_TIME2=2;
	/**
	 * 作业成功
	 */
	public static final int EFFECT_TIME3=3;
	/**
	 * 作业失败
	 */
	public static final int EFFECT_TIME4=4;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOperId() {
		return operId;
	}
	public void setOperId(int operId) {
		this.operId = operId;
	}
	public String getOperCode() {
		return operCode;
	}
	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
	public String getOperDatetime() {
		return operDatetime;
	}
	public void setOperDatetime(String operDatetime) {
		this.operDatetime = operDatetime;
	}
	public int getOperAdminId() {
		return operAdminId;
	}
	public void setOperAdminId(int operAdminId) {
		this.operAdminId = operAdminId;
	}
	public String getOperAdminName() {
		return operAdminName;
	}
	public void setOperAdminName(String operAdminName) {
		this.operAdminName = operAdminName;
	}
	public String getHandlerCode() {
		return handlerCode;
	}
	public void setHandlerCode(String handlerCode) {
		this.handlerCode = handlerCode;
	}
	public int getEffectTime() {
		return effectTime;
	}
	public void setEffectTime(int effectTime) {
		this.effectTime = effectTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
