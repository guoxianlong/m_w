package cn.mmb.delivery.domain.model.vo;

/**
 * 
* @Description: 发件人信息（买卖宝公司相关信息）
* @author ahc
*
 */
public class SenderBean {
	
	private String name;//发件公司
	private int postCode;//邮编
	private String phone;//座机
	private String mobile;//手机
	private String prov;//省
	private String city;//市，区
	private String address;//详细地址
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPostCode() {
		return postCode;
	}
	public void setPostCode(int postCode) {
		this.postCode = postCode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getProv() {
		return prov;
	}
	public void setProv(String prov) {
		this.prov = prov;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
