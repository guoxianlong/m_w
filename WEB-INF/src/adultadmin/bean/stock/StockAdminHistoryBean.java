/*
 * Created on 2007-11-22
 *
 */
package adultadmin.bean.stock;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-22
 * 
 * 说明：
 */
public class StockAdminHistoryBean {
    public static int STOCK_OPERATION = 0;

    public static int STOCK_HISTORY = 1;

    /**
     * <pre>
     * logType 类型：表示该日志是 退换货入库 的操作日志
     * </pre>
     */
    public static int LOG_TYPE_STOCK_CANCEL_STOCKIN = 5;

    /**
     * 订单申请出货
     */
    public static int ORDER_STOCK_STATUS1 = 2;

    /**
     * 订单确认申请出货
     */
    public static int ORDER_STOCK_STATUS2 = 3;

    /**
     * 订单确认出库
     */
    public static int ORDER_STOCK_STATUS3 = 4;

    /**
     * 商品检测操作
     */
    public static int STOCK_CHECK = 5;

    /**
     * 商品返厂操作
     */
    public static int STOCK_BACK = 6;

    /**
     * 商品维修操作
     */
    public static int STOCK_REPAIR = 7;

    /**
     * 商品报废操作
     */
    public static int STOCK_BAD = 8;

    /**
     * 商品库存调配 操作
     */
    public static int STOCK_EXCHANGE = 9;

    /**
     * 订单出货操作
     */
    public static int ORDER_STOCK = 10;

    /**
     * 退换货入库操作
     */
    public static int CANCEL_STOCKIN = 11;

    /**
     * 其他出入库操作
     */
    public static int OTHERS_STOCK = 12;

    /**
     * 修改订单快递公司
     */
    public static int ORDER_DELIVER=13;
    
    public static int CREATE = 0;

    public static int CHANGE = 1;

    public static int DELETE = 2;

    public int id;

    public int type;

    public int logType;

    public String remark;

    public String operDatetime;

    public int logId;

    public int adminId;

    public String adminName;

    public int deleted;

    /**
     * @return Returns the deleted.
     */
    public int getDeleted() {
        return deleted;
    }

    /**
     * @param deleted
     *            The deleted to set.
     */
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    /**
     * @return Returns the adminId.
     */
    public int getAdminId() {
        return adminId;
    }

    /**
     * @param adminId
     *            The adminId to set.
     */
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    /**
     * @return Returns the adminName.
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     * @param adminName
     *            The adminName to set.
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
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
     * @return Returns the logId.
     */
    public int getLogId() {
        return logId;
    }

    /**
     * @param logId
     *            The logId to set.
     */
    public void setLogId(int logId) {
        this.logId = logId;
    }

    /**
     * @return Returns the logType.
     */
    public int getLogType() {
        return logType;
    }

    /**
     * @param logType
     *            The logType to set.
     */
    public void setLogType(int logType) {
        this.logType = logType;
    }

    /**
     * @return Returns the operDatetime.
     */
    public String getOperDatetime() {
        return operDatetime;
    }

    /**
     * @param operDatetime
     *            The operDatetime to set.
     */
    public void setOperDatetime(String operDatetime) {
        this.operDatetime = operDatetime;
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
     * @return Returns the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     *            The type to set.
     */
    public void setType(int type) {
        this.type = type;
    }
}
