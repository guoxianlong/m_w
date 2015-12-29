/**
 * 
 */
package adultadmin.action.vo;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.ProductStockBean;

/**
 * @author Bomb
 *
 */
public class voOrderProduct implements Serializable {
	
	public static int BUY_PROMOTION_TYPE_PACKAGE = 1;//打包标识
	public static int BUY_PROMOTION_TYPE_ONLY = 2;//优惠价标识
	public static int BUY_PROMOTION_TYPE_REFENCE = 3;//满减标识
	public static int BUY_PROMOTION_TYPE_LIMIT = 4;//限时抢购标识
	public static int BUY_PROMOTION_BUYGIVE = 5;//买赠标识
	public static int BUY_PROMOTION_STEP = 6;//阶梯标识
	
	private int id;
	private int productId;
	private String name;
	private String oriname;
	private String lackRemark;
	private float price;
	private float price5;
	public float getPrice5() {
		return price5;
	}

	public void setPrice5(float price5) {
		this.price5 = price5;
	}

	private float groupBuyPrice;
	private int count;
	private int status;		// 0 未处理 1 已汇总
	private String code;
	private String proxyName;
	private int stock;
	private int stockGd;
	private int productStatus;
	private String productStatusName;
	private int parentId1;
	private int parentId2;
	private String parentId1Name;
	private String parentId2Name;
	private String bjStockin;
	private String gdStockin;
	private OrderStockProductBean orderStockProduct;
    
    /**
     * 该产品的库存列表，所有地区的，所有库的<br/>
     * 需要单独查询出来，放在这个voProduct实例中
     */
    private List psList;

    private List cargoPSList;
    
	/**
	 * user_order_product || user_order_present 中记录的 出库时的 商品库存价格（现在要保存商品的库存价格price5）
	 */
	private float price3;
	
	/**
	 * 拆分子订单时用于区分小于10元子订单中的商品是原品还是套装拆分后的
	 * 0:原品 1:套装拆过来的
	 */
	private int pacageType;
	
	public List getCargoPSList() {
		return cargoPSList;
	}

	public void setCargoPSList(List cargoPSList) {
		this.cargoPSList = cargoPSList;
	}
	
	/**
	 * @return Returns the stock.
	 */
	public int getStock() {
		return stock;
	}
	/**
	 * @param stock The stock to set.
	 */
	public void setStock(int stock) {
		this.stock = stock;
	}
	/**
	 * @return Returns the count.
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count The count to set.
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
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
	 * @return Returns the price.
	 */
	public float getPrice() {
		return price;
	}
	/**
	 * @param price The price to set.
	 */
	public void setPrice(float price) {
		this.price = price;
	}
	/**
	 * @return Returns the productId.
	 */
	public int getProductId() {
		return productId;
	}
	/**
	 * @param productId The productId to set.
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}
	/**
	 * @return Returns the status.
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * @param status The status to set.
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return Returns the code.
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code The code to set.
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return Returns the proxyName.
	 */
	public String getProxyName() {
		return proxyName;
	}
	/**
	 * @param proxyName The proxyName to set.
	 */
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}
	/**
	 * @return Returns the oriname.
	 */
	public String getOriname() {
		return oriname;
	}
	/**
	 * @param oriname The oriname to set.
	 */
	public void setOriname(String oriname) {
		this.oriname = oriname;
	}
	public int getStockGd() {
		return stockGd;
	}
	public void setStockGd(int stockGd) {
		this.stockGd = stockGd;
	}
	public int getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(int productStatus) {
		this.productStatus = productStatus;
	}
	public String getProductStatusName() {
		return productStatusName;
	}
	public void setProductStatusName(String productStatusName) {
		this.productStatusName = productStatusName;
	}

	public int getParentId1() {
		return parentId1;
	}

	public void setParentId1(int parentId1) {
		this.parentId1 = parentId1;
	}

	public String getParentId1Name() {
		return parentId1Name;
	}

	public void setParentId1Name(String parentId1Name) {
		this.parentId1Name = parentId1Name;
	}

	public int getParentId2() {
		return parentId2;
	}

	public void setParentId2(int parentId2) {
		this.parentId2 = parentId2;
	}

	public String getParentId2Name() {
		return parentId2Name;
	}

	public void setParentId2Name(String parentId2Name) {
		this.parentId2Name = parentId2Name;
	}

	public String getBjStockin() {
		return bjStockin;
	}

	public void setBjStockin(String bjStockin) {
		this.bjStockin = bjStockin;
	}

	public String getGdStockin() {
		return gdStockin;
	}

	public void setGdStockin(String gdStockin) {
		this.gdStockin = gdStockin;
	}

	public float getPrice3() {
		return price3;
	}

	public void setPrice3(float price3) {
		this.price3 = price3;
	}

	public List getPsList() {
		return psList;
	}

	public void setPsList(List psList) {
		this.psList = psList;
	}

	public int getStock(int area){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area){
				result += ps.getStock();
			}
		}
		return result;
	}

	public int getStock(int area, int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area && ps.getType() == type){
				result += ps.getStock();
			}
		}
		return result;
	}

	public int getStockAllType(int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getType() == type){
				result += ps.getStock();
			}
		}
		return result;
	}



	public int getLockCountAll(){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			result += ps.getLockCount();
		}
		return result;
	}

	public int getLockCount(int area){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area){
				result += ps.getLockCount();
			}
		}
		return result;
	}

	public int getLockCount(int area, int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area && ps.getType() == type){
				result += ps.getLockCount();
			}
		}
		return result;
	}

	public int getLockCountAllType(int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getType() == type){
				result += ps.getLockCount();
			}
		}
		return result;
	}

	public float getGroupBuyPrice() {
		return groupBuyPrice;
	}

	public void setGroupBuyPrice(float groupBuyPrice) {
		this.groupBuyPrice = groupBuyPrice;
	}

	public OrderStockProductBean getOrderStockProduct() {
		return orderStockProduct;
	}

	public void setOrderStockProduct(OrderStockProductBean orderStockProduct) {
		this.orderStockProduct = orderStockProduct;
	}

	private float productPrice;  // 订单中记录的商品mmb价格。
	private float discountPrice; // 订单商品单品成交价
	private int productDiscountId; // 优惠活动的id
	private int productPreferenceId; // 满减活动的id
	private int promotionId; // 活动的id
	private int flag; // 活动标识
	public float getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(float productPrice) {
		this.productPrice = productPrice;
	}

	public float getDiscountPrice() {
		return discountPrice;
	}

	public int getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(int promotionId) {
		this.promotionId = promotionId;
	}

	public int getFlag() {
		return flag;
	}
	public int getFlagForCache(){
		
		int temp = 0;
		//1--打包 2--优惠价 3--买赠 4--满减
		switch(flag){
		case 1:
			temp = BUY_PROMOTION_TYPE_PACKAGE;
			break;
		case 2:
			temp = BUY_PROMOTION_TYPE_ONLY;
			break;
		case 3:
			temp = BUY_PROMOTION_BUYGIVE;
			break;
		case 4:
			temp = BUY_PROMOTION_TYPE_REFENCE;
			break;
		case 5:
			temp = BUY_PROMOTION_TYPE_LIMIT;
			break;
		case 6:
			temp = BUY_PROMOTION_STEP;
			break;
		}

		return temp;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}

	public void setDiscountPrice(float discountPrice) {
		this.discountPrice = discountPrice;
	}

	public int getProductDiscountId() {
		return productDiscountId;
	}

	public void setProductDiscountId(int productDiscountId) {
		this.productDiscountId = productDiscountId;
	}

	public int getProductPreferenceId() {
		return productPreferenceId;
	}

	public void setProductPreferenceId(int productPreferenceId) {
		this.productPreferenceId = productPreferenceId;
	}

	public String getLackRemark() {
		return lackRemark;
	}

	public void setLackRemark(String lackRemark) {
		this.lackRemark = lackRemark;
	}
	
	/**
	 * @param stockType:库类型
	 * @param areaId:库区域
	 * @return
	 */
	public int getCargoStock(int areaId,int stockType){
		int result = 0;
		if(cargoPSList == null){
			return result;
		}
		Iterator iter = cargoPSList.listIterator();
		while(iter.hasNext()){
			CargoProductStockBean cps = (CargoProductStockBean) iter.next();
			if(cps.getCargoInfo().getStockType() == stockType && cps.getCargoInfo().getAreaId() == areaId){
				result += cps.getStockCount();
			}
		}
		return result;
	}
	
	public int getPacageType() {
		return pacageType;
	}
	
	public void setPacageType(int pacageType) {
		this.pacageType = pacageType;
	}
}
