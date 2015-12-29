package mmb.stock.stat;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.order.UserOrderPackageTypeBean;

/**
 * @name 商品物流属性
 * @author HYB
 *
 */
public class ProductWarePropertyBean {

	public int id; //id
	
	public int checkEffectId;	//质检分类id
	public int productId; //产品id
	public int length; //包装长度
	public int width; //包装宽度
	public int height; //保证高度
	public int weight; //重量
	public int productTypeId;//商品物流分类
	public String identityInfo; //可辨识信息
	
	public voProduct product;
	public CheckEffectBean checkeEffect;
	public UserOrderPackageTypeBean userOrderPackageType;
	public ProductBarcodeVO productBarcodeBean;
	public ProductWareTypeBean productWareType;
	
	public int cartonningStandardCount;//标准装箱量
	
	
	public ProductWareTypeBean getProductWareType() {
		return productWareType;
	}
	public void setProductWareType(ProductWareTypeBean productWareType) {
		this.productWareType = productWareType;
	}
	public ProductBarcodeVO getProductBarcodeBean() {
		return productBarcodeBean;
	}
	public void setProductBarcodeBean(ProductBarcodeVO productBarcodeBean) {
		this.productBarcodeBean = productBarcodeBean;
	}
	public UserOrderPackageTypeBean getUserOrderPackageType() {
		return userOrderPackageType;
	}
	public void setUserOrderPackageType(
			UserOrderPackageTypeBean userOrderPackageType) {
		this.userOrderPackageType = userOrderPackageType;
	}
	public CheckEffectBean getCheckeEffect() {
		return checkeEffect;
	}
	public void setCheckeEffect(CheckEffectBean checkeEffect) {
		this.checkeEffect = checkeEffect;
	}
	public voProduct getProduct() {
		return product;
	}
	public void setProduct(voProduct product) {
		this.product = product;
	}
	public int getProductTypeId() {
		return productTypeId;
	}
	public void setProductTypeId(int productTypeId) {
		this.productTypeId = productTypeId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCheckEffectId() {
		return checkEffectId;
	}
	public void setCheckEffectId(int checkEffectId) {
		this.checkEffectId = checkEffectId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public long calculateVolume() {
		return this.length * this.width * this.height;
	}
	public String getIdentityInfo() {
		return identityInfo;
	}
	public void setIdentityInfo(String identityInfo) {
		this.identityInfo = identityInfo;
	}
	public int getCartonningStandardCount() {
		return cartonningStandardCount;
	}
	public void setCartonningStandardCount(int cartonningStandardCount) {
		this.cartonningStandardCount = cartonningStandardCount;
	}
	
}
