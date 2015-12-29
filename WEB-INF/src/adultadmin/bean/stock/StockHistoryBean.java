/*
 * Created on 2007-11-14
 *
 */
package adultadmin.bean.stock;

import adultadmin.action.vo.voProduct;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：商品出入库历史记录
 */
public class StockHistoryBean {
	/**
	 * stockType 入库
	 */
    public static int IN = 1; 
    /**
     * stockType 出库
     */
    public static int OUT = 0; 
    
    /**
     * status 未真正出入库
     */
    public static int UNDEAL = 0; 
    /**
     * status 已经出入库
     */
    public static int DEALED = 1; 
    /**
     * status 已经撤销出入库
     */
    public static int DELETED = 2; 

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 出库操作
     * 商品维修: 出库操作
     * 商品返厂: 
     * </pre>
     */
    public static int STOCK_TYPE_CHECK_OUT = 0;

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 入库操作
     * 商品维修: 入库操作
     * 商品返厂: 
     * </pre>
     */
    public static int STOCK_TYPE_CHECK_IN = 1;

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 第一步“出库”操作，处理中
     * 商品维修: 第一步“出库”操作，处理中
     * 商品返厂: 
     * </pre>
     */
    public static int STEP1_DEALING = 0;

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 第一步“出库”操作，完成；审核时需要出库
     * 商品维修: 第一步“出库”操作，完成；审核时需要出库
     * 商品返厂: 
     * </pre>
     */
    public static int STEP1_DONE = 1;

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 第一步操作，完成；审核时不需要出库操作
     * 商品维修: 第一步操作，完成；审核时不需要出库操作
     * 商品返厂: 
     * </pre>
     */
    public static int STEP1_DONE_NOSTOCK = 4;

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 第二步{“入库”操作/分支处理(转换为“返厂”、“维修”)}，正在处理中
     * 商品维修: 第二步{“入库”操作/分支处理(转换为“返厂”、“检测”)}，正在处理中
     * 商品返厂: 
     * </pre>
     */
    public static int STEP2_DEALING = 2;

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 第二步{“入库”操作/分支处理(转换为“返厂”、“维修”)}，完成
     * 商品维修: 第二步{“入库”操作/分支处理(转换为“返厂”、“检测”)}，完成
     * 商品返厂: 
     * </pre>
     */
    public static int STEP2_DONE = 3;

    /**
     * <pre>
     * 库存调整部分使用这个状态
     * 商品检测: 第二步操作，完成；审核时不需要入库操作
     * 商品维修: 第二步操作，完成；审核时不需要入库操作
     * 商品返厂: 
     * </pre>
     */
    public static int STEP2_DONE_NOSTOCK = 5;

    public int id;

    public int productId;//产品id

    public String productCode;//产品编码

    public int stockBj;//北京仓库存量

    public int stockGd;//广东仓库存量

    public int status;//入库状态

    public int stockType;//库存类型

    public int operType;//?????

    public int operId;//操作人id

    public String createDatetime;//创建时间

    public String dealDatetime;//处理时间

    /**
     * 改操作完成时间<br/>
     * eg: 订单确认出货时间
     */
    public String overDatetime;

    public String remark;//备注

    public voProduct product;

    public StockProductHistoryBean stockProduct;

    public StockOperationBean stockOperation;
    /**
     * @return Returns the product.
     */
    public voProduct getProduct() {
        return product;
    }

    /**
     * @param product
     *            The product to set.
     */
    public void setProduct(voProduct product) {
        this.product = product;
    }

    /**
     * @return Returns the createDatetime.
     */
    public String getCreateDatetime() {
        return createDatetime;
    }

    /**
     * @param createDatetime
     *            The createDatetime to set.
     */
    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    /**
     * @return Returns the dealDatetime.
     */
    public String getDealDatetime() {
        return dealDatetime;
    }

    /**
     * @param dealDatetime
     *            The dealDatetime to set.
     */
    public void setDealDatetime(String dealDatetime) {
        this.dealDatetime = dealDatetime;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the operId.
     */
    public int getOperId() {
        return operId;
    }

    /**
     * @param operId
     *            The operId to set.
     */
    public void setOperId(int operId) {
        this.operId = operId;
    }

    /**
     * @return Returns the operType.
     */
    public int getOperType() {
        return operType;
    }

    /**
     * @param operType
     *            The operType to set.
     */
    public void setOperType(int operType) {
        this.operType = operType;
    }

    /**
     * @return Returns the product_code.
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param product_code
     *            The product_code to set.
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * @return Returns the productId.
     */
    public int getProductId() {
        return productId;
    }

    /**
     * @param productId
     *            The productId to set.
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * @return Returns the remark.
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     *            The remark to set.
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return Returns the status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * @param status
     *            The status to set.
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * @return Returns the stockBj.
     */
    public int getStockBj() {
        return stockBj;
    }

    /**
     * @param stockBj
     *            The stockBj to set.
     */
    public void setStockBj(int stockBj) {
        this.stockBj = stockBj;
    }

    /**
     * @return Returns the stockGd.
     */
    public int getStockGd() {
        return stockGd;
    }

    /**
     * @param stockGd
     *            The stockGd to set.
     */
    public void setStockGd(int stockGd) {
        this.stockGd = stockGd;
    }

    /**
     * @return Returns the stockType.
     */
    public int getStockType() {
        return stockType;
    }

    /**
     * @param stockType
     *            The stockType to set.
     */
    public void setStockType(int stockType) {
        this.stockType = stockType;
    }

	public StockProductHistoryBean getStockProduct() {
		return stockProduct;
	}

	public void setStockProduct(StockProductHistoryBean stockProduct) {
		this.stockProduct = stockProduct;
	}

	public String getOverDatetime() {
		return overDatetime;
	}

	public void setOverDatetime(String overDatetime) {
		this.overDatetime = overDatetime;
	}

	public StockOperationBean getStockOperation() {
		return stockOperation;
	}

	public void setStockOperation(StockOperationBean stockOperation) {
		this.stockOperation = stockOperation;
	}

    
}
