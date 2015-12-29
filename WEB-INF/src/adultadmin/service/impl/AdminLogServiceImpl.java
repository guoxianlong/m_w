/*
 * Created on 2007-2-8
 *
 */
package adultadmin.service.impl;

import java.util.ArrayList;

import mmb.sale.order.stat.OrderAdminStatusLogBean;
import adultadmin.bean.AdminLogBean;
import adultadmin.bean.OrderImportLogBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-2-8
 * 
 * 说明：
 */
public class AdminLogServiceImpl extends BaseServiceImpl implements IAdminLogService {
    public AdminLogServiceImpl(int useConnType, DbOperation dbOp) {
        this.useConnType = useConnType;
        if(dbOp == null) {	// 传一个null进来，表示使用默认的数据库：sms = log
        	this.dbOp = new DbOperation();
        	this.dbOp.init("sms");
        } else 
        	this.dbOp = dbOp;
    }

    public AdminLogServiceImpl() {
        this.useConnType = CONN_IN_SERVICE;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addAdminLog(AdminLogBean bean) {
        return addXXX(bean, "admin_log");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteAdminLog(String condition) {
        return deleteXXX(condition, "admin_log");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public AdminLogBean getAdminLog(String condition) {
        return (AdminLogBean) getXXX(condition, "admin_log", "adultadmin.bean.AdminLogBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getAdminLogCount(String condition) {
        return getXXXCount(condition, "admin_log", "id");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getAdminLogList(String condition, int index, int count,
            String orderBy) {
        return getXXXList(condition, index, count, orderBy, "admin_log",
                "adultadmin.bean.AdminLogBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateAdminLog(String set, String condition) {
        return updateXXX(set, condition, "admin_log");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addOrderImportLog(OrderImportLogBean bean) {
        return addXXX(bean, "order_import_log");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteOrderImportLog(String condition) {
        return deleteXXX(condition, "order_import_log");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public OrderImportLogBean getOrderImportLog(String condition) {
        return (OrderImportLogBean) getXXX(condition, "order_import_log", "adultadmin.bean.OrderImportLogBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getOrderImportLogCount(String condition) {
        return getXXXCount(condition, "order_import_log", "id");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getOrderImportLogList(String condition, int index, int count,
            String orderBy) {
        return getXXXList(condition, index, count, orderBy, "order_import_log",
                "adultadmin.bean.OrderImportLogBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateOrderImportLog(String set, String condition) {
        return updateXXX(set, condition, "order_import_log");
    }
    


    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addOrderAdminLog(OrderAdminLogBean bean) {
        return addXXX(bean, "order_admin_log");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteOrderAdminLog(String condition) {
        return deleteXXX(condition, "order_admin_log");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public OrderAdminLogBean getOrderAdminLog(String condition) {
        return (OrderAdminLogBean) getXXX(condition, "order_admin_log", "adultadmin.bean.log.OrderAdminLogBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getOrderAdminLogCount(String condition) {
        return getXXXCount(condition, "order_admin_log", "id");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getOrderAdminLogList(String condition, int index, int count,
            String orderBy) {
        return getXXXList(condition, index, count, orderBy, "order_admin_log",
                "adultadmin.bean.log.OrderAdminLogBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateOrderAdminLog(String set, String condition) {
        return updateXXX(set, condition, "order_admin_log");
    }

	public boolean addOrderAdminStatusLog(OrderAdminStatusLogBean bean) {
		return addXXX(bean, "order_admin_status_log");
	}
	
	/**
	 * 功能:根据订单状态及发货状态是否变化，添加订单状态日志中，用于日志统计
	 * @param oriStatus   
	 *	原订单状态
	 *@param status   
	 *	修改后订单状态
	 *@param oriStockoutDeal   
	 *	原发货状态
	 *@param stockoutDeal   
	 *	修改后发货状态
	 */
	public void addOrderAdminStatusLog(int oriStatus,int status,int oriStockoutDeal,int stockoutDeal,OrderAdminLogBean log){
		OrderAdminStatusLogBean statusLog  = new OrderAdminStatusLogBean();
		statusLog.setCreateDatetime(log.getCreateDatetime());
		statusLog.setOrderId(log.getOrderId());
		statusLog.setUsername(log.getUsername());
		if(oriStatus >=0 && status>=0 && oriStatus != status){
			if(status==1||status==2||status==3||status==6||status==7||status==8||status==11||status==14){
				//新增订单状态变化日志
				statusLog.setType(1);
				statusLog.setOriginStatus(oriStatus);
				statusLog.setNewStatus(status);
				this.addOrderAdminStatusLog(statusLog);
				if(status==7&&oriStatus==3){
					statusLog.setType(2);
					this.addOrderAdminStatusLog(statusLog);
				}
			}
		}
		if(oriStockoutDeal >=0 && stockoutDeal>=0 && oriStockoutDeal != stockoutDeal){
			if(stockoutDeal==1||stockoutDeal==2||stockoutDeal==9||stockoutDeal==5||stockoutDeal==6||stockoutDeal==8){
				//新增发货状态变化日志
				statusLog.setType(2);
				statusLog.setOriginStatus(oriStockoutDeal);
				statusLog.setNewStatus(stockoutDeal);
				this.addOrderAdminStatusLog(statusLog);
			}
		}
	}
}
