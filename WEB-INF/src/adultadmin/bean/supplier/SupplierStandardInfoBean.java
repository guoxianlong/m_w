package adultadmin.bean.supplier;


/**
 * 说明：供货商基本信息bean
 * 
 * 创建时间：2011-03-15
 * 
 */
public class SupplierStandardInfoBean {

	public int id; // ID

	public String name; // 供货商名称

	public String nameAbbreviation; // 名称缩写

	public String full_name;// 供应商全名
	public String provice;// 所属省
	public String city;// 市
	public String towns;// 城镇（街道）
	public String postcode;// 邮政编码
	public String telephone_1;// 电话1
	public String telephone_2;// 电话2
	public String fax;// 传真
	public String name_abbreviation;//名称拼音缩写
	public int shouhou_type;//售后类型
	public String shui_code;//税号
	public float taxPoint;//供应商税率
	public String getName_abbreviation() {
		return name_abbreviation;
	}

	public void setName_abbreviation(String name_abbreviation) {
		this.name_abbreviation = name_abbreviation;
	}

	public int buyer_id;// 采购员ID
	public int status;// 供应商状态
	public int grade_id;// 供应商级别
	public String productLineNames;//供应商所属的产品线
	public float payOrderTotal;//订货金额
	public String lastTime;//末次交易时间


	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getProvice() {
		return provice;
	}

	public void setProvice(String provice) {
		this.provice = provice;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getTelephone_1() {
		return telephone_1;
	}

	public void setTelephone_1(String telephone_1) {
		this.telephone_1 = telephone_1;
	}

	public String getTelephone_2() {
		return telephone_2;
	}

	public void setTelephone_2(String telephone_2) {
		this.telephone_2 = telephone_2;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public int getBuyer_id() {
		return buyer_id;
	}

	public void setBuyer_id(int buyer_id) {
		this.buyer_id = buyer_id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getGrade_id() {
		return grade_id;
	}

	public void setGrade_id(int grade_id) {
		this.grade_id = grade_id;
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

	public String getNameAbbreviation() {
		return nameAbbreviation;
	}

	public void setNameAbbreviation(String nameAbbreviation) {
		this.nameAbbreviation = nameAbbreviation;
	}

	public String getTowns() {
		return towns;
	}

	public void setTowns(String towns) {
		this.towns = towns;
	}

	public int getShouhou_type() {
		return shouhou_type;
	}

	public void setShouhou_type(int shouhou_type) {
		this.shouhou_type = shouhou_type;
	}

	public String getShui_code() {
		return shui_code;
	}

	public void setShui_code(String shui_code) {
		this.shui_code = shui_code;
	}

	public String getProductLineNames() {
		return productLineNames;
	}

	public void setProductLineNames(String productLineNames) {
		this.productLineNames = productLineNames;
	}

	public float getPayOrderTotal() {
		return payOrderTotal;
	}

	public void setPayOrderTotal(float payOrderTotal) {
		this.payOrderTotal = payOrderTotal;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public float getTaxPoint() {
		return taxPoint;
	}

	public void setTaxPoint(float taxPoint) {
		this.taxPoint = taxPoint;
	}
	
}
