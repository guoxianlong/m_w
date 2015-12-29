package adultadmin.bean.balance;

public class LogisticBean {
	
	public static int PACKAGE_TYPE_ORDER = 0;
	
	public static int PACKAGE_TYPE_AFTERSALE_USER = 1;
	
	public static int PACKAGE_TYPE_AFTERSALE_FACTORY = 4;
	
	public static int PACKAGE_TYPE_OTHER = 2;
	
	public int id;            //ID
	
	/**
	 * 编号
	 */
	public String code;
	
	/**
	 * 包裹单号
	 */
	public String packageNum;
	
	/**
	 * 包裹单类型
	 */
	public int packageType;
	
	/**
	 * 单据号
	 */
	public String documentCode;
	
	/**
	 * 物流成本<br/>
	 * 物流成本=运费+妥投费+退回费+结算费+保险费+单册费
	 */
	public float mailingCost;
	
	/**
	 * 运费
	 */
	public float carriage;

	/**
	 * 妥投费
	 */
	public float mailingCharge;

	/**
	 * 退回费
	 */
	public float untreadCharge;

	/**
	 * 结算费
	 */
	public float balanceCharge;

	/**
	 * 保险费
	 */
	public float insureCharge;
	
	/**
	 * 保价费
	 */
	public float insurePriceCharge;

	/**
	 * 单册费
	 */
	public float billsCharge;
	
	/**
	 * 创建时间
	 */
	public String createDatetime;
	
	/**
	 * 创建人ID
	 */
	public int createUserid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPackageNum() {
		return packageNum;
	}

	public void setPackageNum(String packageNum) {
		this.packageNum = packageNum;
	}

	public int getPackageType() {
		return packageType;
	}

	public void setPackageType(int packageType) {
		this.packageType = packageType;
	}

	public float getMailingCost() {
		return mailingCost;
	}

	public void setMailingCost(float mailingCost) {
		this.mailingCost = mailingCost;
	}

	public float getCarriage() {
		return carriage;
	}

	public void setCarriage(float carriage) {
		this.carriage = carriage;
	}

	public float getMailingCharge() {
		return mailingCharge;
	}

	public void setMailingCharge(float mailingCharge) {
		this.mailingCharge = mailingCharge;
	}

	public float getUntreadCharge() {
		return untreadCharge;
	}

	public void setUntreadCharge(float untreadCharge) {
		this.untreadCharge = untreadCharge;
	}

	public float getBalanceCharge() {
		return balanceCharge;
	}

	public void setBalanceCharge(float balanceCharge) {
		this.balanceCharge = balanceCharge;
	}

	public float getInsureCharge() {
		return insureCharge;
	}

	public void setInsureCharge(float insureCharge) {
		this.insureCharge = insureCharge;
	}

	public float getBillsCharge() {
		return billsCharge;
	}

	public void setBillsCharge(float billsCharge) {
		this.billsCharge = billsCharge;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getCreateUserid() {
		return createUserid;
	}

	public void setCreateUserid(int createUserid) {
		this.createUserid = createUserid;
	}

	public String getDocumentCode() {
		return documentCode;
	}

	public void setDocumentCode(String documentCode) {
		this.documentCode = documentCode;
	}

	public float getInsurePriceCharge() {
		return insurePriceCharge;
	}

	public void setInsurePriceCharge(float insurePriceCharge) {
		this.insurePriceCharge = insurePriceCharge;
	}
	
}
