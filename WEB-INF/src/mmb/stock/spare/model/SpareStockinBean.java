package mmb.stock.spare.model;

import adultadmin.bean.stock.ProductStockBean;


public class SpareStockinBean {
    private int id;

    private String code;

    private int supplierId;

    private int productId;

    private int count;

    private int areaId;

    private String createDatetime;

    private int createUserId;

    private String createUserName;

    private int status;

    private String auditDatetime;

    private int auditUserId;

    private String auditUserName;

    private String auditRemark;
    
    private String supplierName;
    
    private String productCode;
    
    private String productOriname;
    
 
	public String getSupplierName() {
		return supplierName;
	}

	public String getProductCode() {
		return productCode;
	}

	public String getProductOriname() {
		return productOriname;
	}

	public String getAreaName() {
		return ProductStockBean.areaMap.get(this.areaId);
	}

	public String getStatusName() {
		String name = "";
		switch (this.status) {
		case 0:
			name = "待审核";
			break;
		case 1:
			name = "已完成";
			break;
		case 2:
			name = "审核未通过";
			break;
		default:
			break;
		}
		return name;
	}

	/**
     * 待审核
     */
    public static int STATUS_WAIT_AUDIT = 0;
    /**
     * 已完成
     */
    public static int STATUS_COMPLETE = 1;
    /**
     * 审核未通过
     */
    public static int STATUS_NUN = 2;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getAuditDatetime() {
		return auditDatetime;
	}

	public void setAuditDatetime(String auditDatetime) {
		this.auditDatetime = auditDatetime;
	}

	public int getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(int auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

	public void setCode(String code) {
		this.code = code;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

	public void setAuditUserName(String auditUserName) {
		this.auditUserName = auditUserName;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}
}