/*
 * Created on 2009-2-19
 *
 */
package adultadmin.bean.stock;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.cargo.CargoProductStockBean;

public class StockExchangeProductBean {

	public static int STOCKOUT_UNDEAL = 0;
	public static int STOCKOUT_DEALED = 1;
	public static int STOCKIN_UNDEAL = 2;
	public static int STOCKIN_DEALED = 3;

	public int id;

	public int stockExchangeId;

	public int productId;

	public int stockOutCount;
	public int stockInCount;

	public int stockOutId;
	public int stockInId;

	public int status;
	
	public int noUpCargoCount; //已上架量
	public int upCargoLockCount;//冻结数量

	public String createDatetime;

	public String remark;

	public String confirmDatetime;

	public voProduct product;

	public StockExchangeBean exchange;

	public ProductStockBean psIn;
	public ProductStockBean psOut;
	
	public StockExchangeProductCargoBean sepcIn;
	public StockExchangeProductCargoBean sepcOut;

	public int reason;
	public String reasonText;

 
	
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

	public StockExchangeBean getExchange() {
		return exchange;
	}

	public void setExchange(StockExchangeBean exchange) {
		this.exchange = exchange;
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

	public int getStockOutCount() {
		return stockOutCount;
	}

	public void setStockOutCount(int stockOutCount) {
		this.stockOutCount = stockOutCount;
	}

	public int getStockExchangeId() {
		return stockExchangeId;
	}

	public void setStockExchangeId(int stockExchangeId) {
		this.stockExchangeId = stockExchangeId;
	}

	public int getStockInId() {
		return stockInId;
	}

	public void setStockInId(int stockInId) {
		this.stockInId = stockInId;
	}

	public int getStockOutId() {
		return stockOutId;
	}

	public void setStockOutId(int stockOutId) {
		this.stockOutId = stockOutId;
	}

	public ProductStockBean getPsIn() {
		return psIn;
	}

	public void setPsIn(ProductStockBean psIn) {
		this.psIn = psIn;
	}

	public ProductStockBean getPsOut() {
		return psOut;
	}

	public void setPsOut(ProductStockBean psOut) {
		this.psOut = psOut;
	}

	public int getReason() {
		return reason;
	}

	public void setReason(int reason) {
		this.reason = reason;
	}

	public String getReasonText() {
		return reasonText;
	}

	public void setReasonText(String reasonText) {
		this.reasonText = reasonText;
	}


	public StockExchangeProductCargoBean getSepcIn() {
		return sepcIn;
	}

	public void setSepcIn(StockExchangeProductCargoBean sepcIn) {
		this.sepcIn = sepcIn;
	}

	public StockExchangeProductCargoBean getSepcOut() {
		return sepcOut;
	}

	public void setSepcOut(StockExchangeProductCargoBean sepcOut) {
		this.sepcOut = sepcOut;
	}

	public int getNoUpCargoCount() {
		return noUpCargoCount;
	}

	public void setNoUpCargoCount(int noUpCargoCount) {
		this.noUpCargoCount = noUpCargoCount;
	}

	public int getUpCargoLockCount() {
		return upCargoLockCount;
	}

	public void setUpCargoLockCount(int upCargoLockCount) {
		this.upCargoLockCount = upCargoLockCount;
	}
	
}
