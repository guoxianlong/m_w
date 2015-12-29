package mmb.aftersale;


/**
 * 作者：曹续
 * 
 * 创建日期：2009-8-25
 * 
 * 说明：
 */
public class SecurityInfoBean {
	public int id ;
	public int userId;
	public String walletPwd;
	public String pwdFindQ1;
	public String pwdFindQ2;
	public String pwdFindQ3;
	
	public SecurityInfoBean(){
		userId = 0;
		walletPwd = "" ;
		pwdFindQ1 = "" ;
		pwdFindQ2 = "" ;
		pwdFindQ3 = "" ;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	/**
	 * @return the walletPwd
	 */
	public String getWalletPwd() {
		return walletPwd;
	}
	/**
	 * @param walletPwd the walletPwd to set
	 */
	public void setWalletPwd(String walletPwd) {
		this.walletPwd = walletPwd;
	}
	/**
	 * @return the pwdFindQ1
	 */
	public String getPwdFindQ1() {
		return pwdFindQ1;
	}
	/**
	 * @param pwdFindQ1 the pwdFindQ1 to set
	 */
	public void setPwdFindQ1(String pwdFindQ1) {
		this.pwdFindQ1 = pwdFindQ1;
	}
	/**
	 * @return the pwdFindQ2
	 */
	public String getPwdFindQ2() {
		return pwdFindQ2;
	}
	/**
	 * @param pwdFindQ2 the pwdFindQ2 to set
	 */
	public void setPwdFindQ2(String pwdFindQ2) {
		this.pwdFindQ2 = pwdFindQ2;
	}
	/**
	 * @return the pwdFindQ3
	 */
	public String getPwdFindQ3() {
		return pwdFindQ3;
	}
	/**
	 * @param pwdFindQ3 the pwdFindQ3 to set
	 */
	public void setPwdFindQ3(String pwdFindQ3) {
		this.pwdFindQ3 = pwdFindQ3;
	}
	
	
}

