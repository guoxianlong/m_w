package cn.mmb.delivery.domain.model.vo;

public class DeliverRelationInfoBean {
	
	private int id;
	private int deliverId;//快递公司
	private String orderCode;//订单编号
	private int type;//参数类型 1:districenter_code(集包地编码) 2:districenter_name(集包地名称) 3:bigpen_code(大笔编码) 4:position(大笔) 5:position_no(格口号)
	private String info;//参数信息
	private String infoType;//保留字段
	private String createDatetime;//生成时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getInfoType() {
		return infoType;
	}
	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	
}
