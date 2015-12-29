package cn.mmb.delivery.domain.model.vo;
/**
 * 
* @Description: 用于访问接口时设置的参数
* @author ahc
*
 */
public class BasicParamBean {
	
	private String url;//发送订单url
	private String urlForCancel;//取消订单url
	private String partnerid;
	private String password;
	private String version;
	private String request;
	private String requestForCancel;
	private String clientId;
	private String type;
	//成都密钥串
	private String partneridcd;
	//成都商家编号
	private String clientIdcd;
	
	private String indentity;//如风达商家唯一标识
	private String key;//如风达key
	private String merchantcode;//如风达商家编号
	
	/**
	 * 调用圆通接口通用私钥
	 */
	private String ytCommonPrivateKey;
	
	/**
	 * 圆通走件流程查询接口Url
	 */
	private String ytWaybillTraceUrl;
	
	/**
	 * 圆通走件流程查询接口名称
	 */
	private String ytWaybillTraceMethod;
	
	/**
	 * 调用圆通接口通用的user_id
	 */
	private String ytCommonUserId;
	
	/**
	 * 调用圆通接口通用的app_key
	 */
	private String ytCommonAppKey;
	
	/**
	 * @return the ytWaybillTraceUrl
	 */
	public String getYtWaybillTraceUrl() {
		return ytWaybillTraceUrl;
	}
	/**
	 * @param ytWaybillTraceUrl the ytWaybillTraceUrl to set
	 */
	public void setYtWaybillTraceUrl(String ytWaybillTraceUrl) {
		this.ytWaybillTraceUrl = ytWaybillTraceUrl;
	}
	/**
	 * @return the ytWaybillTraceMethod
	 */
	public String getYtWaybillTraceMethod() {
		return ytWaybillTraceMethod;
	}
	/**
	 * @param ytWaybillTraceMethod the ytWaybillTraceMethod to set
	 */
	public void setYtWaybillTraceMethod(String ytWaybillTraceMethod) {
		this.ytWaybillTraceMethod = ytWaybillTraceMethod;
	}
	/**
	 * @return the ytCommonUserId
	 */
	public String getYtCommonUserId() {
		return ytCommonUserId;
	}
	/**
	 * @param ytCommonUserId the ytCommonUserId to set
	 */
	public void setYtCommonUserId(String ytCommonUserId) {
		this.ytCommonUserId = ytCommonUserId;
	}
	/**
	 * @return the ytCommonAppKey
	 */
	public String getYtCommonAppKey() {
		return ytCommonAppKey;
	}
	/**
	 * @param ytCommonAppKey the ytCommonAppKey to set
	 */
	public void setYtCommonAppKey(String ytCommonAppKey) {
		this.ytCommonAppKey = ytCommonAppKey;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlForCancel() {
		return urlForCancel;
	}
	public void setUrlForCancel(String urlForCancel) {
		this.urlForCancel = urlForCancel;
	}
	public String getPartnerid() {
		return partnerid;
	}
	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	
	public String getRequestForCancel() {
		return requestForCancel;
	}
	public void setRequestForCancel(String requestForCancel) {
		this.requestForCancel = requestForCancel;
	}
	/**
	 * @return the ytCommonPrivateKey
	 */
	public String getYtCommonPrivateKey() {
		return ytCommonPrivateKey;
	}
	/**
	 * @param ytCommonPrivateKey the ytCommonPrivateKey to set
	 */
	public void setYtCommonPrivateKey(String ytCommonPrivateKey) {
		this.ytCommonPrivateKey = ytCommonPrivateKey;
	}
	public String getIndentity() {
		return indentity;
	}
	public void setIndentity(String indentity) {
		this.indentity = indentity;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getMerchantcode() {
		return merchantcode;
	}
	public void setMerchantcode(String merchantcode) {
		this.merchantcode = merchantcode;
	}
	public String getPartneridcd() {
		return partneridcd;
	}
	public void setPartneridcd(String partneridcd) {
		this.partneridcd = partneridcd;
	}
	public String getClientIdcd() {
		return clientIdcd;
	}
	public void setClientIdcd(String clientIdcd) {
		this.clientIdcd = clientIdcd;
	}
	

}
