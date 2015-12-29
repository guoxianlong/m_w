package adultadmin.bean.bybs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import adultadmin.action.vo.voProductSupplier;

public class BsbyOperationnoteBean {
	public static int dispose = 0;// 处理中
	public static int audit_ing = 1;// 审核中
	public static int audit_Fail = 2;// 审核未通过
	public static int audit_sus = 3; // 审核通过
	public static int audit_end = 4;//结束
	public static int fin_audit_Fail = 5;//财务审核未通过
	public static int fin_audit_sus = 6;//财务审核通过
	public static HashMap<Integer, String> current_typeMap = new LinkedHashMap<Integer, String>();
	/** 报损 **/
	public static int TYPE0 = 0;
	/** 报溢 **/
	public static int TYPE1 = 1;
	
	public static HashMap<Integer, String> typeMap = new HashMap<Integer, String>();
	
	static {
		current_typeMap.put(Integer.valueOf(dispose), "处理中");
		current_typeMap.put(Integer.valueOf(audit_ing), "审核中");
		current_typeMap.put(Integer.valueOf(audit_Fail), "财务审核未通过");
		current_typeMap.put(Integer.valueOf(audit_end), "已完成");
		current_typeMap.put(Integer.valueOf(fin_audit_Fail), "运营审核未通过");
		current_typeMap.put(Integer.valueOf(fin_audit_sus), "运营审核通过");
		
		typeMap.put(TYPE0, "报损");
		typeMap.put(TYPE1, "报溢");
	}
	public int id;
	public String receipts_number; //编号
	public int warehouse_type; //库类型
	public int warehouse_area;	//库地区
	public String add_time;	//添加时间
	public String operator_name;	//操作人
	public int operator_id;	//操作人id
	public int current_type;	//状态
	public String end_time;	//完成时间
	public String remark;	//注释
	public int type; //报损报溢类型
	public String end_oper_name;//最后修改人
	public int end_oper_id; //完成操作人
	public int print_sum;	//打印次数
	public int if_del; //删除状态
	public String examineSuggestion;//审核意见
	public String finAuditDatetime;//财务审核时间
	public int finAuditId;//财务审核人Id
	public String finAuditName;//财务审核人姓名
	public String finAuditRemark;//财务审核意见
	public int source;   //来源(目前仅用于货位盘点陈升的库存调整)
	public String sourceCode;  //来源单据号
	
	private int productCount;
	public String lookup;
	public String current_type_name;
	public String warehouse_type_name;
	public String warehouse_area_name;
	public String productCode;
	public String oriname;
	private String typeName;
	
	public String bsbyCount;//jsp页面用来拼接显示报损报溢数量
	public String cargoCode;//jsp用来拼接货位
	private String price;//报损单价含税
	private String priceNotOfTax;//报损单价不含税
	private String allPrice; //报损总额含税
	private String allPriceNotOfTax; //报损总额不含税
	public String counts;//单品数量

	public String parentName1;//商品一级分类名称
	public String parentName2;//商品二级分类名称
	public String parentName3;//商品三级分类名称
	public String productLine;//产品线名称
	public String getPriceNotOfTax() {
		return priceNotOfTax;
	}

	public void setPriceNotOfTax(String priceNotOfTax) {
		this.priceNotOfTax = priceNotOfTax;
	}

	public String getAllPriceNotOfTax() {
		return allPriceNotOfTax;
	}

	public void setAllPriceNotOfTax(String allPriceNotOfTax) {
		this.allPriceNotOfTax = allPriceNotOfTax;
	}

	public String getCounts() {
		return counts;
	}

	public void setCounts(String counts) {
		this.counts = counts;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getAllPrice() {
		return allPrice;
	}

	public void setAllPrice(String allPrice) {
		this.allPrice = allPrice;
	}

	public String getOriname() {
		return oriname;
	}

	public void setOriname(String oriname) {
		this.oriname = oriname;
	}

	public String getWarehouse_type_name() {
		return warehouse_type_name;
	}

	public void setWarehouse_type_name(String warehouse_type_name) {
		this.warehouse_type_name = warehouse_type_name;
	}

	public String getWarehouse_area_name() {
		return warehouse_area_name;
	}

	public void setWarehouse_area_name(String warehouse_area_name) {
		this.warehouse_area_name = warehouse_area_name;
	}

	public String getCurrent_type_name() {
		return current_type_name;
	}

	public void setCurrent_type_name(String current_type_name) {
		this.current_type_name = current_type_name;
	}

	public String getLookup() {
		return lookup;
	}

	public void setLookup(String lookup) {
		this.lookup = lookup;
	}

	public List<BsbyProductBean> bsbyProductBeans = new ArrayList<BsbyProductBean>();
    
	private voProductSupplier ProductSupplierBean;//表product_supplier
	
	public voProductSupplier getProductSupplierBean() {
		return ProductSupplierBean;
	}

	public void setProductSupplierBean(voProductSupplier productSupplierBean) {
		ProductSupplierBean = productSupplierBean;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReceipts_number() {
		return receipts_number;
	}

	public void setReceipts_number(String receipts_number) {
		this.receipts_number = receipts_number;
	}

	public int getWarehouse_type() {
		return warehouse_type;
	}

	public void setWarehouse_type(int warehouse_type) {
		this.warehouse_type = warehouse_type;
	}

	public int getWarehouse_area() {
		return warehouse_area;
	}

	public void setWarehouse_area(int warehouse_area) {
		this.warehouse_area = warehouse_area;
	}

	public String getAdd_time() {
		return add_time.replace(".0", "");
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public String getOperator_name() {
		return operator_name;
	}

	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}

	public int getOperator_id() {
		return operator_id;
	}

	public void setOperator_id(int operator_id) {
		this.operator_id = operator_id;
	}

	public int getCurrent_type() {
		return current_type;
	}

	public void setCurrent_type(int current_type) {
		this.current_type = current_type;
	}

	public String getEnd_time() {
		if(end_time==null)
		{
			return end_time;
		}else {
			return end_time.replace(".0", "");
		}
		
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getEnd_oper_name() {
		return end_oper_name;
	}

	public void setEnd_oper_name(String end_oper_name) {
		this.end_oper_name = end_oper_name;
	}

	public int getEnd_oper_id() {
		return end_oper_id;
	}

	public void setEnd_oper_id(int end_oper_id) {
		this.end_oper_id = end_oper_id;
	}

	public int getPrint_sum() {
		return print_sum;
	}

	public void setPrint_sum(int print_sum) {
		this.print_sum = print_sum;
	}

	public int getIf_del() {
		return if_del;
	}

	public void setIf_del(int if_del) {
		this.if_del = if_del;
	}

	public String getExamineSuggestion() {
		return examineSuggestion;
	}

	public String getFinAuditDatetime() {
		return finAuditDatetime;
	}

	public void setFinAuditDatetime(String finAuditDatetime) {
		this.finAuditDatetime = finAuditDatetime;
	}

	public int getFinAuditId() {
		return finAuditId;
	}

	public void setFinAuditId(int finAuditId) {
		this.finAuditId = finAuditId;
	}

	public String getFinAuditName() {
		return finAuditName;
	}

	public void setFinAuditName(String finAuditName) {
		this.finAuditName = finAuditName;
	}

	public String getFinAuditRemark() {
		return finAuditRemark;
	}

	public void setFinAuditRemark(String finAuditRemark) {
		this.finAuditRemark = finAuditRemark;
	}

	public void setExamineSuggestion(String examineSuggestion) {
		this.examineSuggestion = examineSuggestion;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public String getSourceCode() {
		return sourceCode;
	}

	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public List<BsbyProductBean> getBsbyProductBeans() {
		return bsbyProductBeans;
	}

	public void setBsbyProductBeans(List<BsbyProductBean> bsbyProductBeans) {
		this.bsbyProductBeans = bsbyProductBeans;
	}

	public int getProductCount() {
		return productCount;
	}

	public void setProductCount(int productCount) {
		this.productCount = productCount;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getBsbyCount() {
		return bsbyCount;
	}

	public void setBsbyCount(String bsbyCount) {
		this.bsbyCount = bsbyCount;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCargoCode() {
		return cargoCode;
	}

	public void setCargoCode(String cargoCode) {
		this.cargoCode = cargoCode;
	}

	public String getParentName1() {
		return parentName1;
	}

	public void setParentName1(String parentName1) {
		this.parentName1 = parentName1;
	}

	public String getParentName2() {
		return parentName2;
	}

	public void setParentName2(String parentName2) {
		this.parentName2 = parentName2;
	}

	public String getParentName3() {
		return parentName3;
	}

	public void setParentName3(String parentName3) {
		this.parentName3 = parentName3;
	}

	public String getProductLine() {
		return productLine;
	}

	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}


	
}
