package mmb.stock.stat;

/**
 *	快递公司
 */
public class DeliverCorpInfoBean {
	
	/** ‘无锡京东快递公司’对应的记录id */
	public static final int DELIVER_ID_JD_WX = 62;
	/** ‘成都京东快递公司’对应的记录id */
	public static final int DELIVER_ID_JD_CD = 63;
	/** ‘韵达快递公司’对应的记录id */
	public static final int DELIVER_ID_YD = 59;
	/** ‘无锡圆通’对应的记录id */
	public static final int DELIVER_ID_YT_WX = 61;
	/** ‘成都圆通’对应的记录id */
	public static final int DELIVER_ID_YT_CD = 64;
	/** ‘如风达’对应的记录id */
	public static final int DELIVER_ID_RFD = 16;
	/** ‘如风达浙江’对应的记录id */
	public static final int DELIVER_ID_RFD_ZJ = 22;
	/** ‘如风达苏沪’对应的记录id */
	public static final int DELIVER_ID_RFD_SH = 58;
	/** ‘如风达山东’对应的记录id */
	public static final int DELIVER_ID_RFD_SD = 42;
	
	public int id;
	public String name;//快递公司名称
	public int changeable;//是否可修改为该快递公司，0代表不可修改，1代表可修改
	public String pinyin; //快递100对应缩写
	public String nameWap;  //前台显示名称
	public String phone;    //热线电话
	public String token;    //识别码
	public String days;     //快递公司发货所需要的大概时间
	public String address;  //快递公司地址
	public String sendsms;
	public int isems;  //是否是ems 0表示非ems，1是ems
	public int status;//状态,0未启用(停用)，1启用
	public String createDatetime;//生成时间
	public String webAddress;//查询订单网址
	public int buyModeType;//订单付款类型，0不限，1仅货到付款，2仅非货到付款
	public int channel;//渠道，0不限，1普通，2特殊
	public int formType;//快递类型，0EMS，1宅急送，2落地配
	public int packageType;//面单类型，0MMB面单，1EMS国内面单
	public int codPackageType;//代收货款订单类型，0订单号，1EMS经济快递单号，2EMS货到付款单号，3EMS标准快递单号，4快递自己提供的单号
	public String codAccount;//代收货款接口账号
	public String codPassword;//代收货款接口密码
	public int paidPackageType;//已付款订单类型，0订单号，1EMS经济快递单号，2EMS货到付款单号，3EMS标准快递单号，4快递自己提供的单号
	public String paidAccount;//已付款接口账号
	public String paidPassword;//已付款接口密码
	public String mail;//发货明细接收邮箱
	public int deliveryRate;//妥投率
	public int overtimeRate;//超时率
	public String lastOperDatetime;//最后操作时间
	public String addPackageCodeDatetime;//包裹单号最后注入时间
	public int addPackageCodeCount;//包裹单号最后注入数量
	
	private String statusName;//状态名称
	private String deliverArea;//快递公司配送区域
	
	public String sendTime;//邮件发送时间
	public int TransitCount;//交接数量
	
	public String getDeliverArea() {
		return deliverArea;
	}
	public void setDeliverArea(String deliverArea) {
		this.deliverArea = deliverArea;
	}
	public String getStatusName() {
		return this.status==0?"停用":this.status==1?"启用中":"";
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getChangeable() {
		return changeable;
	}
	public void setChangeable(int changeable) {
		this.changeable = changeable;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public String getNameWap() {
		return nameWap;
	}
	public void setNameWap(String nameWap) {
		this.nameWap = nameWap;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getDays() {
		return days;
	}
	public void setDays(String days) {
		this.days = days;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getIsems() {
		return isems;
	}
	public void setIsems(int isems) {
		this.isems = isems;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreateDatetime() {
		if(this.createDatetime!=null && this.createDatetime.length()>0){
			return createDatetime.substring(0,19);
		}else{
			return "";
		}
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public String getWebAddress() {
		return webAddress;
	}
	public void setWebAddress(String webAddress) {
		this.webAddress = webAddress;
	}
	public int getBuyModeType() {
		return buyModeType;
	}
	public void setBuyModeType(int buyModeType) {
		this.buyModeType = buyModeType;
	}
	public int getChannel() {
		return channel;
	}
	public void setChannel(int channel) {
		this.channel = channel;
	}
	public int getFormType() {
		return formType;
	}
	public void setFormType(int formType) {
		this.formType = formType;
	}
	public int getPackageType() {
		return packageType;
	}
	public void setPackageType(int packageType) {
		this.packageType = packageType;
	}
	public int getCodPackageType() {
		return codPackageType;
	}
	public void setCodPackageType(int codPackageType) {
		this.codPackageType = codPackageType;
	}
	public String getCodAccount() {
		return codAccount;
	}
	public void setCodAccount(String codAccount) {
		this.codAccount = codAccount;
	}
	public String getCodPassword() {
		return codPassword;
	}
	public void setCodPassword(String codPassword) {
		this.codPassword = codPassword;
	}
	public int getPaidPackageType() {
		return paidPackageType;
	}
	public void setPaidPackageType(int paidPackageType) {
		this.paidPackageType = paidPackageType;
	}
	public String getPaidAccount() {
		return paidAccount;
	}
	public void setPaidAccount(String paidAccount) {
		this.paidAccount = paidAccount;
	}
	public String getPaidPassword() {
		return paidPassword;
	}
	public void setPaidPassword(String paidPassword) {
		this.paidPassword = paidPassword;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}

	public int getDeliveryRate() {
		return deliveryRate;
	}
	public void setDeliveryRate(int deliveryRate) {
		this.deliveryRate = deliveryRate;
	}
	public int getOvertimeRate() {
		return overtimeRate;
	}
	public void setOvertimeRate(int overtimeRate) {
		this.overtimeRate = overtimeRate;
	}
	public String getLastOperDatetime() {
		if(this.lastOperDatetime!=null && this.lastOperDatetime.length()>0){
			return lastOperDatetime.substring(0,19);
		}else{
			return "";
		}
	}
	public void setLastOperDatetime(String lastOperDatetime) {
		this.lastOperDatetime = lastOperDatetime;
	}
	public String getAddPackageCodeDatetime() {
		if(this.addPackageCodeDatetime!=null && this.addPackageCodeDatetime.length()>0){
			return addPackageCodeDatetime.substring(0,19);
		}else{
			return "";
		}
	}
	public void setAddPackageCodeDatetime(String addPackageCodeDatetime) {
		this.addPackageCodeDatetime = addPackageCodeDatetime;
	}
	public int getAddPackageCodeCount() {
		return addPackageCodeCount;
	}
	public void setAddPackageCodeCount(int addPackageCodeCount) {
		this.addPackageCodeCount = addPackageCodeCount;
	}
	public String getSendsms() {
		return sendsms;
	}
	public void setSendsms(String sendsms) {
		this.sendsms = sendsms;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public int getTransitCount() {
		return TransitCount;
	}
	public void setTransitCount(int transitCount) {
		TransitCount = transitCount;
	}

	
	
}
