package mmb.stock.spare.model;

import mmb.stock.aftersale.AfterSaleDetectProductBean;


public class AfterSaleReplaceNewProductRecord {
    private int id;

    private int afterSaleOrderId;

    private String afterSaleOrderCode;

    private int afterSaleDetectProductId;

    private String afterSaleDetectProductCode;

    private String spareCode;

    private int areaId;

    private int oriProductId;

    private int replaceNewProductId;

    private String createDatetime;

    private String lastOperateTime;

    private int lastOperateId;

    private String lastOperateUsername;

    private int type;

    private int status;
    
    private String productCode;
    private String productOriname;
    private int proParentId1;//商品一级分类id
    
    
    public int getProParentId1() {
		return proParentId1;
	}

	public void setProParentId1(int proParentId1) {
		this.proParentId1 = proParentId1;
	}

	/**
     * 备用机
     */
    public static int TYPE1 = 1;
    /**
     * 无商品可更换
     */
    public static int TYPE2 = 2;
    /**
     * 待报价
     */
    public static int STATUS1 = 1;
    /**
     * 待更换
     */
    public static int STATUS2 = 2;
    /**
     * 有费用单需要仓库指定更换商品
     */
    public static int STATUS3 = 3;
    /**
     * 等待客户确认
     */
    public static int STATUS4 = 4;
    /**
     * 已更换
     */
    public static int STATUS5 = 5;
    /**
     * 已作废
     */
    public static int STATUS6 = 6;

    public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductOriname() {
		return productOriname;
	}
	
	
	public void setProductOriname(String productOriname) {
		this.productOriname = productOriname;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAfterSaleOrderId() {
        return afterSaleOrderId;
    }

    public void setAfterSaleOrderId(int afterSaleOrderId) {
        this.afterSaleOrderId = afterSaleOrderId;
    }

    public String getAfterSaleOrderCode() {
        return afterSaleOrderCode;
    }

    public int getAfterSaleDetectProductId() {
        return afterSaleDetectProductId;
    }

    public void setAfterSaleDetectProductId(int afterSaleDetectProductId) {
        this.afterSaleDetectProductId = afterSaleDetectProductId;
    }

    public String getAfterSaleDetectProductCode() {
        return afterSaleDetectProductCode;
    }

    public String getSpareCode() {
        return spareCode;
    }
    
    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getOriProductId() {
        return oriProductId;
    }

    public void setOriProductId(int oriProductId) {
        this.oriProductId = oriProductId;
    }

    public int getReplaceNewProductId() {
        return replaceNewProductId;
    }

    public void setReplaceNewProductId(int replaceNewProductId) {
        this.replaceNewProductId = replaceNewProductId;
    }

    public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getLastOperateTime() {
		return lastOperateTime;
	}

	public void setLastOperateTime(String lastOperateTime) {
		this.lastOperateTime = lastOperateTime;
	}

	public int getLastOperateId() {
        return lastOperateId;
    }

    public void setLastOperateId(int lastOperateId) {
        this.lastOperateId = lastOperateId;
    }

    public String getLastOperateUsername() {
        return lastOperateUsername;
    }

    public void setAfterSaleOrderCode(String afterSaleOrderCode) {
		this.afterSaleOrderCode = afterSaleOrderCode;
	}

	public void setAfterSaleDetectProductCode(String afterSaleDetectProductCode) {
		this.afterSaleDetectProductCode = afterSaleDetectProductCode;
	}

	public void setSpareCode(String spareCode) {
		this.spareCode = spareCode;
	}

	public void setLastOperateUsername(String lastOperateUsername) {
		this.lastOperateUsername = lastOperateUsername;
	}

	public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}