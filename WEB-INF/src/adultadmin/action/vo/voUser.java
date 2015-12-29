/**
 * 
 */
package adultadmin.action.vo;

import java.io.Serializable;
import java.util.HashMap;

import mmb.util.BinaryFlag;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.framework.PermissionFrk;

/**
 * @author Bomb
 *  
 */
public class voUser implements Serializable {
	
    private static final long serialVersionUID = 6964477808073011363L;
    
    public int id;

    public String username;

    public String password;

    public int flag; // 管理员则为1

    public String createDatetime;

    public String name;

    public String phone;

    public String address;

    public String postcode;

    public String cp;

    public String ua;

    public String nick;

    public int vip;

    public String vipPhone;

    public int agent;

    public float discount;

    public float orderReimburse;//退货款

    public float reimburse;//应返款

    private int gender;//性别
    
    public String username2;//OA名称
    
    public boolean isDisable;//后台隐性删除账号 是否可以登陆 true表示已经被隐性删除 不可登陆
	
	public int alone;

    
    /**
     * 安全等级： <br/>10: 超级管理员 <br/>9: 高级管理员 <br/>5: 普通管理员 <br/>
     */
    public int securityLevel;

    /**
     * 权限： <br/>10: 超级管理员 <br/>9: 高级管理员 <br/>8: 平台运维部 <br/>7: 销售部 <br/>6：
     * 商品采购部 <br/>5: 推广部 <br/>4: 运营中心 <br/>3：客服部 <br/>2：物流库存部
     */
    public int permission;

    public int groupId;
    public int groupId2;
    public int groupId3;
    UserGroupBean group;		// 三个组的组合权限，权限以外的参数为groupId对应的主组

    public UserInfoBean userInfo;
    public  CargoStaffBean cargoStaffBean;

    /**
     * @return Returns the userInfo.
     */
    public UserInfoBean getUserInfo() {
        return userInfo;
    }

    /**
     * @param userInfo
     *            The userInfo to set.
     */
    public void setUserInfo(UserInfoBean userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * @return Returns the orderReimburse.
     */
    public float getOrderReimburse() {
        return orderReimburse;
    }

    /**
     * @param orderReimburse
     *            The orderReimburse to set.
     */
    public void setOrderReimburse(float orderReimburse) {
        this.orderReimburse = orderReimburse;
    }

    /**
     * @return Returns the reimburse.
     */
    public float getReimburse() {
        return reimburse;
    }

    /**
     * @param reimburse
     *            The reimburse to set.
     */
    public void setReimburse(float reimburse) {
        this.reimburse = reimburse;
    }

    /**
     * @return Returns the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address
     *            The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return Returns the cp.
     */
    public String getCp() {
        return cp;
    }

    /**
     * @param cp
     *            The cp to set.
     */
    public void setCp(String cp) {
        if (cp == null) {
            this.cp = "";
        } else {
            this.cp = cp;
        }
    }

    /**
     * @return Returns the createDatetime.
     */
    public String getCreateDatetime() {
        return createDatetime;
    }

    /**
     * @param createDatetime
     *            The createDatetime to set.
     */
    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        if (name == null) {
            this.name = "";
        } else {
            this.name = name;
        }
    }

    /**
     * @return Returns the nick.
     */
    public String getNick() {
        return nick;
    }

    /**
     * @param nick
     *            The nick to set.
     */
    public void setNick(String nick) {
        if (nick == null) {
            this.nick = "";
        } else {
            this.nick = nick;
        }
    }

    /**
     * @return Returns the phone.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            The phone to set.
     */
    public void setPhone(String phone) {
        if (phone == null) {
            this.phone = "";
        } else {
            this.phone = phone;
        }
    }

    /**
     * @return Returns the postcode.
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @param postcode
     *            The postcode to set.
     */
    public void setPostcode(String postcode) {
        if (postcode == null) {
            this.postcode = "";
        } else {
            this.postcode = postcode;
        }
    }

    /**
     * @return Returns the ua.
     */
    public String getUa() {
        return ua;
    }

    /**
     * @param ua
     *            The ua to set.
     */
    public void setUa(String ua) {
        if (ua == null) {
            this.ua = "";
        } else {
            this.ua = ua;
        }
    }

    /**
     * @return Returns the flag.
     */
    public int getFlag() {
        return flag;
    }

    /**
     * @param flag
     *            The flag to set.
     */
    public void setFlag(int flag) {
        this.flag = flag;
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

    /**
     * @return Returns the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getVipPhone() {
        return vipPhone;
    }

    public void setVipPhone(String vipPhone) {
        this.vipPhone = vipPhone;
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

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public String getSecurityLevelName(){
    	String securityLevelName = null;
    	switch(this.securityLevel){
    		case 5:
    			securityLevelName = "普通管理员";
    			break;
    		case 9:
    			securityLevelName = "高级管理员";
    			break;
    		case 10:
    			securityLevelName = "超级管理员";
    			break;
    		default:
    			securityLevelName = "普通用户";
    	}
    	return securityLevelName;
    }

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public static int pmaskId = -1;	// 遮盖权限组，如<=0则不遮盖
	public static int outMaskId = -1;	// 遮盖权限组，如<=0则不遮盖
	
	/**
	 * 是否外部登录
	 */
	public boolean isWithout=false;
	
	public UserGroupBean getGroup() {
		if (group == null) {
//			group = PermissionFrk.getUserGroup(groupId).copy();
//			if (groupId2 > 0)
//				group.mergeFlags(PermissionFrk.getUserGroup(groupId2));
//			if (groupId3 > 0)
//				group.mergeFlags(PermissionFrk.getUserGroup(groupId3));
			group = new UserGroupBean();
			if (groups.length > 0) {
				for (int i = 0; i < groups.length; i++) {
					group.mergeFlags(PermissionFrk.getUserGroup(groups[i]));
				}
			}
			group.binaryFlagOr(mBinaryFlag);
			if (pmaskId > 0) {
				group.maskFlags(PermissionFrk.getUserGroup(pmaskId));
			}
			if (isWithout && outMaskId > 0) {// 处理外部登录后可以访问的权限
				group.maskFlags(PermissionFrk.getUserGroup(outMaskId));
			}
		}
		return group;
	}

	public int getGroupId2() {
		return groupId2;
	}

	public void setGroupId2(int groupId2) {
		this.groupId2 = groupId2;
	}

	public int getGroupId3() {
		return groupId3;
	}

	public void setGroupId3(int groupId3) {
		this.groupId3 = groupId3;
	}

	private int accessCount;
	private long lastCheckTime;
	private long nextAccessTime;

	public int getAccessCount() {
		return accessCount;
	}

	public void setAccessCount(int accessCount) {
		this.accessCount = accessCount;
	}

	public long getLastCheckTime() {
		return lastCheckTime;
	}

	public void setLastCheckTime(long lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	public void addAccessCount() {
		this.accessCount ++ ;
	}

	public long getNextAccessTime() {
		return nextAccessTime;
	}

	public void setNextAccessTime(long nextAccessTime) {
		this.nextAccessTime = nextAccessTime;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}
	
	public String getGenderName(){
		return gender==1?"男":gender==2?"女":"未填写";
	}
	
	public int[] groups;
	public BinaryFlag mBinaryFlag;

	public int[] getGroups() {
		return groups;
	}

	public void setGroups(int[] groups) {
		this.groups = groups;
	}

	public BinaryFlag getBinaryFlag() {
		return mBinaryFlag;
	}

	public void setBinaryFlag(BinaryFlag flag) {
		this.mBinaryFlag = flag;
	}

	public String getUsername2() {
		return username2;
	}

	public void setUsername2(String username2) {
		this.username2 = username2;
	}

	public CargoStaffBean getCargoStaffBean() {
		return cargoStaffBean;
	}

	public void setCargoStaffBean(CargoStaffBean cargoStaffBean) {
		this.cargoStaffBean = cargoStaffBean;
	}

	public long otpTime;
	public String otpPassword;	// 动态密钥（短信下发）
	public String otpKey;	// 动态密钥私钥（用于客户端）
	public String cookieHash;	// 用于校验cookie是否正确，md5(username+ipc+ccode)

	public long getOtpTime() {
		return otpTime;
	}

	public void setOtpTime(long otpTime) {
		this.otpTime = otpTime;
	}

	public String getOtpPassword() {
		return otpPassword;
	}

	public void setOtpPassword(String otpPassword) {
		this.otpPassword = otpPassword;
	}

	public String getOtpKey() {
		return otpKey;
	}

	public void setOtpKey(String otpKey) {
		this.otpKey = otpKey;
	}

	public String getCookieHash() {
		return cookieHash;
	}

	public void setCookieHash(String cookieHash) {
		this.cookieHash = cookieHash;
	}
	
	public String rootIds="";
	private HashMap<Integer, Integer> parentIds = new HashMap<Integer, Integer>();

	public String getRootIds() {
		return rootIds;
	}

	public void setRootIds(String rootIds2) {
		this.rootIds = rootIds2;
	}

	public void PutIsParent(int parentId) {
		parentIds.put(Integer.valueOf(parentId), Integer.valueOf(parentId));
	}

	public boolean getIsParent(int parentId) {
		return parentIds.get(Integer.valueOf(parentId)) != null;
	}

	public boolean getIsDisable() {
		return isDisable;
	}

	public void setIsDisable(boolean isDisable) {
		this.isDisable = isDisable;
	}

	public int getAlone() {
		return alone;
	}

	public void setAlone(int alone) {
		this.alone = alone;
	}
}