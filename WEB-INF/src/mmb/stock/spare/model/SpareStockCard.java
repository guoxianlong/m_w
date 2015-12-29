package mmb.stock.spare.model;

import java.util.HashMap;

import adultadmin.bean.stock.ProductStockBean;


public class SpareStockCard {
    private int id;

    private String spareCode;//备用机号

    private int supplierId;

    private int productId;

    private int count;

    private int areaId;

    private String createDatetime;

    private int operateId;

    private String operateUsername;
    
    private int type;
    
    private int operateItemId;
    
    private String operateItemCode;
    
    private String productCode;//商品编号
    private String productName;//商品小店名称
    private String productOriname;//商品原名称
    private String supplierName;//供应商名称
    
    /**
     * 入库单入库
     */
    public static int TYPE_STOCKIN = 1;
    /**
     * 换新机入库
     */
    public static int TYPE_REPLACE_STOCKIN = 2;
    /**
     * 返还供应商出库
     */
    public static int TYPE_BACK_SUPPLIER = 3;
    /**
     * 换新机出库
     */
    public static int TYPE_REPLACE_STOCKOUT = 4;
    /**
     * 检测不合格更换入库
     */
    public static int TYPE_UNQUALIFIED_REPLACE_STOUCTIN = 5;
    /**
     * 检测不合格更换出库
     */
    public static int TYPE_UNQUALIFIED_REPLACE_STOUCTOUT = 6;
    
    public static HashMap<Integer,String> typeMap = new HashMap<Integer,String>();
    
    static{
    	typeMap.put(TYPE_STOCKIN, "入库单入库");
    	typeMap.put(TYPE_REPLACE_STOCKIN, "换新机入库");
    	typeMap.put(TYPE_BACK_SUPPLIER, "返还供应商出库");
    	typeMap.put(TYPE_REPLACE_STOCKOUT, "换新机出库");
    	typeMap.put(TYPE_UNQUALIFIED_REPLACE_STOUCTIN, "检测不合格更换入库");
    	typeMap.put(TYPE_UNQUALIFIED_REPLACE_STOUCTOUT, "检测不合格更换出库");
    }
    

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public int getOperateId() {
        return operateId;
    }

    public void setOperateId(int operateId) {
        this.operateId = operateId;
    }

    public String getOperateUsername() {
        return operateUsername;
    }

    public void setOperateUsername(String operateUsername) {
        this.operateUsername = operateUsername == null ? null : operateUsername.trim();
    }
    
    public String getAreaName(){
    	return ProductStockBean.areaMap.get(this.areaId);
    }
    
    public String getTypeName(){
    	return typeMap.get(this.type);
    }

	public String getSpareCode() {
		return spareCode;
	}

	public void setSpareCode(String spareCode) {
		this.spareCode = spareCode;
	}

	public int getOperateItemId() {
		return operateItemId;
	}

	public void setOperateItemId(int operateItemId) {
		this.operateItemId = operateItemId;
	}

	public String getOperateItemCode() {
		return operateItemCode;
	}

	public void setOperateItemCode(String operateItemCode) {
		this.operateItemCode = operateItemCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductOriname() {
		return productOriname;
	}

	public void setProductOriname(String productOriname) {
		this.productOriname = productOriname;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}
    
}