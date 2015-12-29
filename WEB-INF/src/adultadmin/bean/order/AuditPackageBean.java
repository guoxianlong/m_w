package adultadmin.bean.order;

/**
 * 
 * 核对包裹
 *
 */
public class AuditPackageBean {
	public int id;
	
	public int orderId;//订单Id
	
	public String orderCode;//订单编号
	
	public String sortingDatetime;//分拣时间
	
	public String sortingUserName;//分拣人姓名
	
	public String checkDatetime;//复核出库时间
	
	public String checkUserName;//复核出库人姓名
	
	public String auditPackageDatetime;//核对包裹时间
	
	public String auditPackageUserName;//核对包裹人姓名
	
	public String packageCode;//包裹单号
	
	public int deliver;//快递公司编号
	
	public int orderCount;//订单数量
	
	public String receiveDatetime;//订单签收时间
	public String firstCompleteOrderStockDatetime;//第一次扫描CK单号时间
	public String lastCompleteOrderStockDatetime;//最后一次复核扫描CK单号时间
	
	public String firstPrintPackageDatetime;//第一次打单时间
	
	
	/**
	 * 发货地
	 * 
	 */
	public int areano;
	
	public float weight;//包裹重量，单位g
	
	/**
	 * 状态
	 * 
	 * 0,未分拣
	 * 1,已分拣
	 * 2,已复核出库
	 * 3,已核对包裹
	 * 4,已导入包裹单
	 */
	public int status;//状态

	/**
	 * 未分拣
	 */
	public static int STATUS1 = 0;
	
	/**
	 * 已分拣
	 */
	public static int STATUS2 = 1;
	
	/**
	 * 已复核出库
	 */
	public static int STATUS3 = 2;
	
	/**
	 * 已核对包裹
	 */
	public static int STATUS4 = 3;
	
	/**
	 * 已导入包裹单
	 */
	public static int STATUS5 = 4;
	/**
	 * 为方便easyui添加一些属性
	 */
	public String orderStockCode; //出库单号
	
	public String deliverName;
	
	public float dDprice;
	
	public String getOrderStockCode() {
		return orderStockCode;
	}

	public void setOrderStockCode(String orderStockCode) {
		this.orderStockCode = orderStockCode;
	}

	public float getdDprice() {
		return dDprice;
	}

	public void setdDprice(float dDprice) {
		this.dDprice = dDprice;
	}

	public void setDeliverName(String deliverName) {
		this.deliverName = deliverName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getSortingDatetime() {
		return sortingDatetime;
	}

	public void setSortingDatetime(String sortingDatetime) {
		this.sortingDatetime = sortingDatetime;
	}

	public String getSortingUserName() {
		return sortingUserName;
	}

	public void setSortingUserName(String sortingUserName) {
		this.sortingUserName = sortingUserName;
	}

	public String getCheckDatetime() {
		return checkDatetime;
	}

	public void setCheckDatetime(String checkDatetime) {
		this.checkDatetime = checkDatetime;
	}

	public String getCheckUserName() {
		return checkUserName;
	}

	public void setCheckUserName(String checkUserName) {
		this.checkUserName = checkUserName;
	}

	public String getAuditPackageDatetime() {
		return auditPackageDatetime;
	}

	public void setAuditPackageDatetime(String auditPackageDatetime) {
		this.auditPackageDatetime = auditPackageDatetime;
	}

	public String getAuditPackageUserName() {
		return auditPackageUserName;
	}

	public void setAuditPackageUserName(String auditPackageUserName) {
		this.auditPackageUserName = auditPackageUserName;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public int getAreano() {
		return areano;
	}

	public void setAreano(int areano) {
		this.areano = areano;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getDeliverName() {
        if (deliver == 9) {
			return "广东省外";
		}
        if (deliver == 10) {
			return "广州宅急送";
		}
        if (deliver == 11) {
			return "广东省速递局";
		}
        if (deliver == 12) {
        	return "广州顺丰";
        }
        if (deliver == 13) {
        	return "深圳自建";
        }
        return "";
    }

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}
	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public String getReceiveDatetime() {
		return receiveDatetime;
	}

	public void setReceiveDatetime(String receiveDatetime) {
		this.receiveDatetime = receiveDatetime;
	}


	public String getFirstCompleteOrderStockDatetime() {
		return firstCompleteOrderStockDatetime;
	}

	public void setFirstCompleteOrderStockDatetime(
			String firstCompleteOrderStockDatetime) {
		this.firstCompleteOrderStockDatetime = firstCompleteOrderStockDatetime;
	}

	public String getLastCompleteOrderStockDatetime() {
		return lastCompleteOrderStockDatetime;
	}

	public void setLastCompleteOrderStockDatetime(
			String lastCompleteOrderStockDatetime) {
		this.lastCompleteOrderStockDatetime = lastCompleteOrderStockDatetime;
	}

	public String getFirstPrintPackageDatetime() {
		return firstPrintPackageDatetime;
	}

	public void setFirstPrintPackageDatetime(String firstPrintPackageDatetime) {
		this.firstPrintPackageDatetime = firstPrintPackageDatetime;
	}
	
}
