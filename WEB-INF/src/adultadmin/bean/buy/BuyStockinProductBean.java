/*
 	* Created on 2009-5-6
 *
 */
package adultadmin.bean.buy;

import mmb.stock.fitting.model.FittingBuyStockInBean;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.stock.ProductStockBean;

/**
 * @author Administrator
 *
 * 说明：入库单所含产品Bean
 */
public class BuyStockinProductBean {

	public static int BUYSTOCKIN_UNDEAL = 0;        //状态：未处理

	public static int BUYSTOCKIN_DEALED = 1;        //状态：已处理

	public int id;                      //ID

	public int buyStockinId;            //所属入库单ID

	public int productId;               //产品ID

	public int stockInCount;            //入库量

	public int stockInId;              //入库ID

	public int status;                  //状态

	public String createDatetime;       //添加时间

	public String remark;               //备注

	public String confirmDatetime;      //确认时间

	public float price3;                
	public int productProxyId;          //代理商ID
	public String productCode;          //产品编号
	public String oriname;              //原名称

	public String proxyName;            //代理商名称

	public String productLineName;		//产品对应的产品线
	
	public voProduct product;           //对应产品

	public BuyStockinBean buyStockin;   //对应入库单

	public ProductStockBean psIn;       //对应产品库存
	
	private int totalStockBeforeStockin;  //暂时存放在入库前的总量
	
	private int fittingType;//配件类别

	public int getFittingType() {
		return fittingType;
	}

	public void setFittingType(int fittingType) {
		this.fittingType = fittingType;
	}

	public String getFittingTypeName() {
		return FittingBuyStockInBean.fittingTypeMap.get((byte)this.fittingType);
	}
	
	public BuyStockinBean getBuyStockin() {
		return buyStockin;
	}

	public void setBuyStockin(BuyStockinBean buyStockin) {
		this.buyStockin = buyStockin;
	}

	public int getBuyStockinId() {
		return buyStockinId;
	}

	public void setBuyStockinId(int buyStockinId) {
		this.buyStockinId = buyStockinId;
	}

	public String getConfirmDatetime() {
		return confirmDatetime;
	}

	public void setConfirmDatetime(String confirmDatetime) {
		this.confirmDatetime = confirmDatetime;
	}

	public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public ProductStockBean getPsIn() {
		return psIn;
	}

	public void setPsIn(ProductStockBean psIn) {
		this.psIn = psIn;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStockInCount() {
		return stockInCount;
	}

	public void setStockInCount(int stockInCount) {
		this.stockInCount = stockInCount;
	}

	public int getStockInId() {
		return stockInId;
	}

	public void setStockInId(int stockInId) {
		this.stockInId = stockInId;
	}

	public String getOriname() {
		return oriname;
	}

	public void setOriname(String oriname) {
		this.oriname = oriname;
	}

	public float getPrice3() {
		return price3;
	}

	public void setPrice3(float price3) {
		this.price3 = price3;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public int getProductProxyId() {
		return productProxyId;
	}

	public void setProductProxyId(int productProxyId) {
		this.productProxyId = productProxyId;
	}

	public String getProxyName() {
		return proxyName;
	}

	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	public String getProductLineName() {
		return productLineName;
	}

	public void setProductLineName(String productLineName) {
		this.productLineName = productLineName;
	}

	public void setTotalStockBeforeStockin(int totalStockBeforeStockin) {
		this.totalStockBeforeStockin = totalStockBeforeStockin;
	}

	public int getTotalStockBeforeStockin() {
		return totalStockBeforeStockin;
	}

}
