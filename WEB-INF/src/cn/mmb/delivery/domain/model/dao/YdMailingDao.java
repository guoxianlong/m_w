package cn.mmb.delivery.domain.model.dao;


public class YdMailingDao{

	private String id;
	private String status;//返回状态 1：表示调用接口成功，0：表示未调用接口
	private String reach; //是否到达 1：表示可以到达，0：表示不到达
	private String districenterCode; //集包分拨中心编码
	private String districenterName; //集包分拨中心名称
	private String bigpenCode;//大笔编码
	private String position; //大笔
	private String positionNo; //格口号
	private String oneCode; //暂时没启用
	private String twoCode;//暂时没启用
	private String threeCode; //暂时没启用
	private String padMailno;//集包分拨中心编码
	private String msg; //提示信息
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReach() {
		return reach;
	}
	public void setReach(String reach) {
		this.reach = reach;
	}
	public String getDistricenterCode() {
		return districenterCode;
	}
	public void setDistricenterCode(String districenterCode) {
		this.districenterCode = districenterCode;
	}
	public String getDistricenterName() {
		return districenterName;
	}
	public void setDistricenterName(String districenterName) {
		this.districenterName = districenterName;
	}
	public String getBigpenCode() {
		return bigpenCode;
	}
	public void setBigpenCode(String bigpenCode) {
		this.bigpenCode = bigpenCode;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public String getPositionNo() {
		return positionNo;
	}
	public void setPositionNo(String positionNo) {
		this.positionNo = positionNo;
	}
	public String getOneCode() {
		return oneCode;
	}
	public void setOneCode(String oneCode) {
		this.oneCode = oneCode;
	}
	public String getTwoCode() {
		return twoCode;
	}
	public void setTwoCode(String twoCode) {
		this.twoCode = twoCode;
	}
	public String getThreeCode() {
		return threeCode;
	}
	public void setThreeCode(String threeCode) {
		this.threeCode = threeCode;
	}
	public String getPadMailno() {
		return padMailno;
	}
	public void setPadMailno(String padMailno) {
		this.padMailno = padMailno;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
