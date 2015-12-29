/*
 * Created on 2008-9-19
 *
 */
package adultadmin.framework;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import adultadmin.bean.OrderTypeBean;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CacheAdmin;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-9-19
 * 
 * 说明：用于判断订单的类型
 */
public class OrderTypeFrk {
    public static String GROUP = "order_type";

    public static int TIME = 24 * 3600;

    public static int OTHERS = 9;

    /**
     * 作者：李北金
     * 
     * 创建日期：2008-9-19
     * 
     * 说明：取得用户判断的列表
     * 
     * 参数及返回值说明：
     * 
     * @return
     */
    public static ArrayList getOrderTypeList() {
        String key = "getOrderTypeList";
        ArrayList list = (ArrayList) CacheAdmin.getFromCache(key, GROUP, TIME);
        if (list != null) {
            return list;
        }
        synchronized(GROUP) {
	        list = (ArrayList) CacheAdmin.getFromCache(key, GROUP, TIME);
	        if (list != null) {
	            return list;
	        }
	
	        list = new ArrayList();
	
	        try {
	            DbOperation dbOp = new DbOperation();
	            dbOp.init();
	            DbOperation dbOp1 = new DbOperation();
	            dbOp1.init();
	
	            String sql = "select * from user_order_type order by check_order";
	            String sql1 = null;
	            ResultSet rs = dbOp.executeQuery(sql);
	            ResultSet rs1 = null;
	            OrderTypeBean ot = null;
	            Hashtable pts = null;
	            while (rs.next()) {
	                ot = new OrderTypeBean();
	                ot.setCheckOrder(rs.getInt("check_order"));
	                ot.setId(rs.getInt("id"));
	                ot.setName(rs.getString("name"));
	                ot.setProductCatalogs(rs.getString("product_catalogs"));
	                ot.setProductIds(rs.getString("product_ids"));
	                ot.setTypeId(rs.getInt("type_id"));
	                sql1 = null;
	                if (!StringUtil.isNull(ot.getProductCatalogs())
	                        && !StringUtil.isNull(ot.getProductCatalogs().replace(
	                                " ", ""))) {
	                    sql1 = "select id from product where parent_id1 in ("
	                            + ot.getProductCatalogs() + ") or parent_id2 in ("
	                            + ot.getProductCatalogs() + ")";
	                } else if (!StringUtil.isNull(ot.getProductIds())
	                        && !StringUtil.isNull(ot.getProductIds().replace(" ",
	                                ""))) {
	                    sql1 = "select id from product where id in ("
	                            + ot.getProductIds() + ")";
	                }
	                pts = new Hashtable();
	                if (sql1 != null) {
	                    rs1 = dbOp1.executeQuery(sql1);
	                    while (rs1.next()) {
	                        pts.put("" + rs1.getInt("id"), "");
	                    }
	                    rs1.close();
	                }
	                ot.setProducts(pts);
	                list.add(ot);
	            }
	            dbOp1.release();
	            dbOp.release();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return null;	// 出错了的数据不能写入缓存 added by zhoujun
	        }
	
	        CacheAdmin.putInCache(key, list, GROUP, TIME);
	        return list;
        }
    }

    /**
     * 作者：李北金
     * 
     * 创建日期：2008-9-19
     * 
     * 说明：判别一个订单的类别
     * 
     * 参数及返回值说明：
     * 
     * @param orderId
     * @return
     */
    public static int getOrderType(int orderId, DbOperation dbOp) {
        try {
            boolean dbOpIsNull = false;
            if (dbOp == null) {
                dbOp = new DbOperation();
                dbOp.init();
                dbOpIsNull = true;
            }

            String sql = "select product_id from user_order_product where order_id = "
                    + orderId;
            ResultSet rs = dbOp.executeQuery(sql);
            int productId = 0;
            ArrayList pids = new ArrayList();
            while (rs.next()) {
                productId = rs.getInt(1);
                pids.add(String.valueOf(productId));
            }
            rs.close();

            if(pids.isEmpty()){// 如果没有商品，则查询赠品
            	sql = "select product_id from user_order_present where order_id = " + orderId;
            	rs = dbOp.executeQuery(sql);
            	productId = 0;
            	pids.clear();
            	while (rs.next()) {
                    productId = rs.getInt(1);
                    pids.add(String.valueOf(productId));
                }
            }
            rs.close();
            if (dbOpIsNull) {
                dbOp.release();
            }

            ArrayList orderTypeList = getOrderTypeList();
            OrderTypeBean ot = null;
            Iterator itr = orderTypeList.iterator();
            int i, count;
            count = pids.size();
            while (itr.hasNext()) {
                ot = (OrderTypeBean) itr.next();
                for (i = 0; i < count; i++) {
                    if (ot.getProducts().get((String) pids.get(i)) != null) {
                        return ot.getTypeId();                        
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return OTHERS;
    }

    /**
     * 作者：李北金
     * 
     * 创建日期：2008-9-19
     * 
     * 说明：更新订单类型
     * 
     * 参数及返回值说明：
     * 
     * @param orderId
     * @param dbOp
     */
    public static void updateOrderType(int orderId, DbOperation dbOp) {
        int typeId = getOrderType(orderId, dbOp);
        try {
            boolean dbOpIsNull = false;
            if (dbOp == null) {
                dbOp = new DbOperation();
                dbOp.init();
                dbOpIsNull = true;
            }
            
            dbOp.executeUpdate("update user_order set order_type = " + typeId + " where id = " + orderId);
            
            if (dbOpIsNull) {
                dbOp.release();
            }
        } catch (Exception e) {
        }
    }
    
    public static void updateOrderType(int orderId, DbOperation dbOp, int orderType) {
        int typeId = getOrderType(orderId, dbOp);
        if(typeId == orderType){
            return;
        }
        try {
            boolean dbOpIsNull = false;
            if (dbOp == null) {
                dbOp = new DbOperation();
                dbOp.init();
                dbOpIsNull = true;
            }
            
            dbOp.executeUpdate("update user_order set order_type = " + typeId + " where id = " + orderId);
            
            System.out.println(orderId + ": " + typeId + ": " + orderType);
            
            if (dbOpIsNull) {
                dbOp.release();
            }
        } catch (Exception e) {
        }
    }
    
    /**
     * 作者：李北金
     * 
     * 创建日期：2008-9-19
     * 
     * 说明：设置历史所有订单的类型
     * 
     * 参数及返回值说明：
     * 
     * 
     */
    public static void checkHistory(){
        try {
            DbOperation dbOp = new DbOperation();
            dbOp.init();
            DbOperation dbOp1 = new DbOperation();
            dbOp1.init();
            
            String sql = "select id, order_type from user_order where order_type=9 and create_datetime > '2009-11-24' order by id";
            ResultSet rs1 = dbOp1.executeQuery(sql);
            while(rs1.next()){
                System.out.println(rs1.getInt(1));
                updateOrderType(rs1.getInt(1), dbOp);
            }
            rs1.close();
            dbOp.release();
            dbOp1.release();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        checkHistory();
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-11-2
     * 
     * 说明：获取这个订单产品类型的名称
     * 
     * 参数及返回值说明：
     * 
     * @param orderType
     * @return
     */
    public static String getOrderTypeName(int orderType){
    	List orderTypeList = getOrderTypeList();
    	Iterator iter = orderTypeList.listIterator();
    	while(iter.hasNext()){
    		OrderTypeBean ot = (OrderTypeBean)iter.next();
    		if(ot.getTypeId() == orderType){
    			return ot.getName();
    		}
    	}
    	if(orderType == 9){
    		return "其他订单";
    	}
    	return "";
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-11-2
     * 
     * 说明：查找一个产品在订单商品类型中的排序位置
     * 
     * 参数及返回值说明：如果没找到这个商品的排序位置，则返回99， 表示排在最后
     * 
     * @param productId
     * @return
     */
    public static int getProductCheckOrder(int productId){
    	ArrayList orderTypeList = getOrderTypeList();
        OrderTypeBean ot = null;
        Iterator itr = orderTypeList.iterator();
        while (itr.hasNext()) {
            ot = (OrderTypeBean) itr.next();
            if (ot.getProducts().get(String.valueOf(productId)) != null) {
                return ot.getCheckOrder();                        
            }
        }
        return 99;
    }
}
