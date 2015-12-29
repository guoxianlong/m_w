package adultadmin.bean.afterSales;

import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IStockService;

public class AfterSaleRefundOrderBean {
	public int id;
	public String code;
	public int type;
	public int status;
	public String orderCode; // 原销售单
	public String newOrderCode; // 新生成换货单
	public int productId;
	public int afterSaleOrderId;
	public String afterSaleOrderCode;
	public int creatorId;
	public String creatorName;
	public String createTime;
	public int amount;

	private StockBatchLogBean sblb = null;
	private voProduct voP = null;

	public static int STATUS_PROCESSING = 1;
	public static int STATUS_DONE = 2;//状态使用
	public static int STATUS_DELETED = 3;

	public static int TYPE_REFUND = 1;//退货	类型ls 未使用2012-6-13 
	public static int TYPE_NIFFER = 2;//换货 
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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
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
	 * @return the creatorId
	 */
	public int getCreatorId() {
		return creatorId;
	}

	/**
	 * @param creatorId
	 *            the creatorId to set
	 */
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}

	/**
	 * @return the creatorName
	 */
	public String getCreatorName() {
		return creatorName;
	}

	/**
	 * @param creatorName
	 *            the creatorName to set
	 */
	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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
	 * @return the productId
	 */
	public int getProductId() {
		return productId;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}

	/**
	 * @return the newOrderCode
	 */
	public String getNewOrderCode() {
		return newOrderCode;
	}

	/**
	 * @param newOrderCode
	 *            the newOrderCode to set
	 */
	public void setNewOrderCode(String newOrderCode) {
		this.newOrderCode = newOrderCode;
	}

	private void initSBLB() {
		IStockService service = ServiceFactory.createStockService();
		try {
			StockBatchLogBean _sblb = service
					.getStockBatchLog("code='" + this.orderCode
							+ "' and product_id=" + this.productId
							+ " and stock_type="
							+ ProductStockBean.STOCKTYPE_QUALIFIED);
			if (_sblb == null) {
				_sblb = new StockBatchLogBean();
				_sblb.setBatchCode("");
				_sblb.setBatchPrice(0.00f);
				_sblb.setStockArea(0);
			}
			this.sblb = _sblb;
		} finally {
			service.releaseAll();
		}
	}

	public String getStockAreaName() {
		if (this.sblb == null) {
			this.initSBLB();
		}
		return ProductStockBean.getAreaName(ProductStockBean.AREA_GF);
	}

	public String getBatchInfo() {
		if (this.sblb == null) {
			this.initSBLB();
		}
		return sblb.getBatchCode() + "(" + this.amount + ")/"
				+ sblb.getBatchPrice();
	}

	private void initVoP() {
		WareService service = new WareService();
		try {
			voProduct _voP = service.getProduct2("a.id=" + this.productId);
			this.voP = _voP;
		} finally {
			service.releaseAll();
		}
	}

	public String getProductCode() {
		if (this.voP == null) {
			this.initVoP();
		}
		return this.voP.getCode();
	}

	public String getProductOriName() {
		if (this.voP == null) {
			this.initVoP();
		}
		return this.voP.getOriname();
	}

	public String getProductName() {
		if (this.voP == null) {
			this.initVoP();
		}
		return this.voP.getName();
	}

	public String getProductParent1Name() {
		if (this.voP == null) {
			this.initVoP();
		}
		return this.voP.getParent1().getName();
	}

	public String getProductParent2Name() {
		if (this.voP == null) {
			this.initVoP();
		}
		return this.voP.getParent2().getName();
	}

	public String getProductStatus() {
		if (this.voP == null) {
			this.initVoP();
		}
		return voP.getStatusName();
	}

	public float getProductPrice() {
		if (this.voP == null) {
			this.initVoP();
		}
		return voP.getPrice();
	}

	/**
	 * @return the afterSaleOrderId
	 */
	public int getAfterSaleOrderId() {
		return afterSaleOrderId;
	}

	/**
	 * @param afterSaleOrderId
	 *            the afterSaleOrderId to set
	 */
	public void setAfterSaleOrderId(int afterSaleOrderId) {
		this.afterSaleOrderId = afterSaleOrderId;
	}

}
