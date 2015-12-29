package adultadmin.bean.cargo;

public class ReturnsReasonBean {
	private int id;             //id
	private String reason;   //退货原因
	private String code;//条码
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
