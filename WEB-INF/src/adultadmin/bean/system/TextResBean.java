/*
 * Created on 2009-5-17
 *
 */
package adultadmin.bean.system;

import java.util.HashMap;
import java.util.Map;

import adultadmin.util.ChineseToAZ;

public class TextResBean {

	public static int TYPE_EXCHANGE_REASON = 1;

	public int id;

	public int type;

	public String content;
	
	public boolean flag ;
	
	public int supplierId;
	public int product_line;
	public String proxyName ;
	public String full_name ;
	public String grade_name;
	
	public String contentPy;
	
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public String productType ;
	public int return_count;
	public double return_price;
	
	public int getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public int getProduct_line() {
		return product_line;
	}

	public void setProduct_line(int product_line) {
		this.product_line = product_line;
	}

	public int getReturn_count() {
		return return_count;
	}

	public void setReturn_count(int return_count) {
		this.return_count = return_count;
	}

	public double getReturn_price() {
		return return_price;
	}

	public void setReturn_price(double return_price) {
		this.return_price = return_price;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public String getFull_name() {
		return full_name;
	}

	public void setFull_name(String full_name) {
		this.full_name = full_name;
	}

	public String getGrade_name() {
		return grade_name;
	}

	public void setGrade_name(String grade_name) {
		this.grade_name = grade_name;
	}
	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public static final Map<Integer,String> typeMap = new HashMap<Integer,String>();
	
	static{
		typeMap.put(1, "调拨原因");// 2,3,4 6,7,8 售后 9 10 11 12
		typeMap.put(2, "处理建议");
		typeMap.put(3, "投诉分类");
		typeMap.put(4, "客户要求");
		typeMap.put(5, "采购退货原因");
		typeMap.put(6, "退换货原因");
		typeMap.put(7, "快递名称");
		typeMap.put(8, "银行名称");
		typeMap.put(9, "原品返回原因");
		typeMap.put(10, "差额用户支付方式");
		typeMap.put(11, "售后维修费用原因");
		typeMap.put(12, "厂商维修费用原因");
	}
	
	public String getTypeName(){
		return typeMap.get(type);
	}
	
	public String getContentPy() {
		return contentPy;
	}
	public void setContentPy(String contentPy) {
		this.contentPy = contentPy;
	}
	
	public void setContentPy(){
		this.contentPy= ChineseToAZ.converterToFirstSpell(content);
	}
}
