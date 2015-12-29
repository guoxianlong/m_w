/*
 * Created on 2005-11-15
 *
 */
package adultadmin.bean;


/**
 * @author bomb
 *  
 */
public class UserBean {
	public int id;
	public String username;
	public String password;
	public String nick;
	public String name;
	public String phone;
	public String address;
	public String postcode;
	public String cp;
	public String ua;
	public int flag;
	public boolean mobile;
	public int fr;			// 从哪里链接过来
	public int onlineLogId;	// online log id
	public int vip;
	public int agent;	//	是否为代理商 (1 是 0 否)
	public float discount;	//	折扣，默认为1
	
	public String createDatetime; //创建时间
	
	public String vipPhone;
	
	public float orderReimburse;
	
	public float reimburse;

    /**
	 * @return Returns the onlineLogId.
	 */
	public int getOnlineLogId() {
		return onlineLogId;
	}

	/**
	 * @param onlineLogId The onlineLogId to set.
	 */
	public void setOnlineLogId(int onlineLogId) {
		this.onlineLogId = onlineLogId;
	}

	public UserBean() {
    	id = 0;
    }

    public boolean isLogin() {		// 是否登陆
    	return id > 0;
    }
    
    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the nick.
     */
    public String getNick() {
    	if(id == 0) {
    		return "匿名顾客";
    	}
        if (nick == null || "".equals(nick)) {
            nick = "顾客" + id;
        }
        return nick;
    }
    
    public String getRealNick(){
        return nick;
    }
    
    /**
     * @param nick
     *            The nick to set.
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

  
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	/**
	 * @return Returns the address.
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address The address to set.
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the phone.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone The phone to set.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return Returns the postcode.
	 */
	public String getPostcode() {
		return postcode;
	}

	/**
	 * @param postcode The postcode to set.
	 */
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	/**
	 * @return Returns the cp.
	 */
	public String getCp() {
		return cp;
	}

	/**
	 * @param cp The cp to set.
	 */
	public void setCp(String cp) {
		this.cp = cp;
	}

	/**
	 * @return Returns the log.
	 */
	public boolean isLog() {
		return cp != null || ua != null;
	}

	/**
	 * @return Returns the ua.
	 */
	public String getUa() {
		return ua;
	}

	/**
	 * @param ua The ua to set.
	 */
	public void setUa(String ua) {
		this.ua = ua;
	}

	/**
	 * @return Returns the flag.
	 */
	public int getFlag() {
		return flag;
	}

	/**
	 * @param flag The flag to set.
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}
    
	public boolean isMobile() {
		return mobile;
	}
	
	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}
	
	public boolean isAdmin() {
		return flag > 5;
	}

	/**
	 * @return Returns the fr.
	 */
	public int getFr() {
		return fr;
	}

	/**
	 * @param fr The fr to set.
	 */
	public void setFr(int fr) {
		this.fr = fr;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getAgent() {
		return agent;
	}

	public void setAgent(int agent) {
		this.agent = agent;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public String getVipPhone() {
		return vipPhone;
	}

	public void setVipPhone(String vipPhone) {
		this.vipPhone = vipPhone;
	}

	public float getOrderReimburse() {
		return orderReimburse;
	}

	public void setOrderReimburse(float orderReimburse) {
		this.orderReimburse = orderReimburse;
	}

	public float getReimburse() {
		return reimburse;
	}

	public void setReimburse(float reimburse) {
		this.reimburse = reimburse;
	}

	
}
