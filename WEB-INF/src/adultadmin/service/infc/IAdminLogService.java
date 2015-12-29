/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;

import mmb.sale.order.stat.OrderAdminStatusLogBean;

import adultadmin.bean.AdminLogBean;
import adultadmin.bean.OrderImportLogBean;
import adultadmin.bean.log.OrderAdminLogBean;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-1-24
 * 
 * 说明：
 */
public interface IAdminLogService extends IBaseService {
    public boolean addAdminLog(AdminLogBean bean);

    public AdminLogBean getAdminLog(String condition);

    public int getAdminLogCount(String condition);

    public boolean updateAdminLog(String set, String condition);

    public boolean deleteAdminLog(String condition);

    public ArrayList getAdminLogList(String condition, int index, int count,
            String orderBy);

    // 订单的包裹单号的导入操作的日志
    public boolean addOrderImportLog(OrderImportLogBean bean);

    public OrderImportLogBean getOrderImportLog(String condition);

    public int getOrderImportLogCount(String condition);

    public boolean updateOrderImportLog(String set, String condition);

    public boolean deleteOrderImportLog(String condition);

    public ArrayList getOrderImportLogList(String condition, int index, int count,
            String orderBy);

    //  订单管理操作的日志
    public boolean addOrderAdminLog(OrderAdminLogBean bean);

    public OrderAdminLogBean getOrderAdminLog(String condition);

    public int getOrderAdminLogCount(String condition);

    public boolean updateOrderAdminLog(String set, String condition);

    public boolean deleteOrderAdminLog(String condition);

    public ArrayList getOrderAdminLogList(String condition, int index, int count,
            String orderBy);
    //  订单操作状态变化日志（为方便进行统计）
    public boolean addOrderAdminStatusLog(OrderAdminStatusLogBean bean);
    
    public void addOrderAdminStatusLog(int oriStatus,int status,int oriStockoutDeal,int stockoutDeal,OrderAdminLogBean log);

}
