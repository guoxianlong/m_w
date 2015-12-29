package adultadmin.bean.afterSales;

import java.sql.ResultSet;

import mmb.ware.WareService;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IStockService;
import adultadmin.util.db.DbOperation;

public class AfterSaleNifferRecordBean {
	public int id;
	public String orderCode; // 订单号
	public String afterSaleOrderCode; // 售后单号
	public String afterSaleRefundOrderCode; // 售后退换货单号
	public String productCode; // 添加产品编号
	public int type;// 商品0 or 赠品 1
	private int productRealId;
	public int amount; // 添加数量
	public float realPrice; //商品价格
	private StockBatchBean sbb = null;
	private voProduct product = null;
	private ProductStockBean psb = null;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the orderCode
	 */
	public String getOrderCode() {
		return orderCode;
	}

	/**
	 * @param orderCode
	 *            the orderCode to set
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	/**
	 * @return the afterSaleOrderCode
	 */
	public String getAfterSaleOrderCode() {
		return afterSaleOrderCode;
	}

	/**
	 * @param afterSaleOrderCode
	 *            the afterSaleOrderCode to set
	 */
	public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}

	/**
	 * @return the afterSaleRefundOrderCode
	 */
	public String getAfterSaleRefundOrderCode() {
		return afterSaleRefundOrderCode;
	}

	/**
	 * @param afterSaleRefundOrderCode
	 *            the afterSaleRefundOrderCode to set
	 */
	public void setAfterSaleRefundOrderCode(String afterSaleRefundOrderCode) {
		this.afterSaleRefundOrderCode = afterSaleRefundOrderCode;
	}

	/**
	 * @return the amount
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode
	 *            the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public void setProduct(voProduct product){
		this.product=product;
	}
	
	public voProduct getProduct(){
		return product;
	}
	
	private void initProduct() {
		WareService service = new WareService();
		try {
			this.product = service.getProduct2("a.code="+this.productCode);
		} finally {
			service.releaseAll();
		}
	}

	private void initSBB() {
		if (product == null) {
			this.initProduct();
		}
		IStockService service = ServiceFactory.createStockService();
		try {
			String condition = "product_id=" + product.getId()
					+ " and stock_area=" + ProductStockBean.AREA_GS
					+ " and stock_type="
					+ ProductStockBean.STOCKTYPE_QUALITYTESTING;
			StockBatchBean _sbb = service.getStockBatch(condition);
			if (_sbb == null) {
				_sbb = new StockBatchBean();
				_sbb.setCode("");
			}
			this.sbb = _sbb;
		} finally {
			service.releaseAll();
		}
	}

	public String getStockBatchCode() {
		if (sbb == null) {
			this.initSBB();
		}
		return this.sbb.getCode();
	}

	public String getOriName() {
		if (product == null) {
			this.initProduct();
		}
		return product.getOriname();
	}

	public String getName() {
		if (product == null) {
			this.initProduct();
		}
		return product.getName();
	}

	public String getProductStatus(){
		if (product == null) {
			this.initProduct();
		}
		return product.getStatusName();
	}
	
	public float getProductPrice(){
		if (product == null) {
			this.initProduct();
		}
		return product.getPrice();
	}
	
	public int getGFStock() {
		if (product == null) {
			this.initProduct();
		}
		DbOperation dbOp = new DbOperation();
		try {
			String query = "select stock from product_stock where product_id="
					+ product.getId() + " and area=" + ProductStockBean.AREA_GF
					+ " and `type`=" + ProductStockBean.STOCKTYPE_NIFFER;
			ResultSet rs = dbOp.executeQuery(query);
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return 0;
	}

	public int getGSStock() {
		if (product == null) {
			this.initProduct();
		}
		DbOperation dbOp = new DbOperation();
		try {
			String query = "select stock from product_stock where product_id="
					+ product.getId() + " and area=" + ProductStockBean.AREA_GS
					+ " and `type`=" + ProductStockBean.STOCKTYPE_NIFFER;
			ResultSet rs = dbOp.executeQuery(query);
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return 0;
	}
	
	public int getGSHGStock() {
		if (product == null) {
			this.initProduct();
		}
		DbOperation dbOp = new DbOperation();
		try {
			String query = "select stock from product_stock where product_id="
					+ product.getId() + " and area=" + ProductStockBean.AREA_GS
					+ " and `type`=" + ProductStockBean.STOCKTYPE_QUALIFIED;
			ResultSet rs = dbOp.executeQuery(query);
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return 0;
	}
	
	public int getGFHGStock() {
		if (product == null) {
			this.initProduct();
		}
		DbOperation dbOp = new DbOperation();
		try {
			String query = "select stock from product_stock where product_id="
					+ product.getId() + " and area=" + ProductStockBean.AREA_GF
					+ " and `type`=" + ProductStockBean.STOCKTYPE_QUALIFIED;
			ResultSet rs = dbOp.executeQuery(query);
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return 0;
	}
	
	public int getZCHGStock() {
		if (product == null) {
			this.initProduct();
		}
		DbOperation dbOp = new DbOperation();
		try {
			String query = "select stock from product_stock where product_id="
					+ product.getId() + " and area=" + ProductStockBean.AREA_ZC
					+ " and `type`=" + ProductStockBean.STOCKTYPE_QUALIFIED;
			ResultSet rs = dbOp.executeQuery(query);
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return 0;
	}
	public int getProductId(){
		if (product == null) {
			this.initProduct();
		}
		return product.getId();
	}

	public float getRealPrice() {
		return realPrice;
	}

	public void setRealPrice(float realPrice) {
		this.realPrice = realPrice;
	}

	public int getProductRealId() {
		return productRealId;
	}

	public void setProductRealId(int productRealId) {
		this.productRealId = productRealId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
 
}
