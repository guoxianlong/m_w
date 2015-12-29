/**
 * 
 */
package adultadmin.action.admin;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cache.CatalogCache;
import cache.ProductLinePermissionCache;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICatalogService;
import adultadmin.util.DateUtil;
import adultadmin.util.DbLock;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

/**
 * @author Bomb
 *  
 */
public class SearchOrderHistory2Action extends BaseAction {

    /**
     *  
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	
    	voUser adminUser = (voUser)request.getSession().getAttribute("userView");

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String oriname = request.getParameter("oriname");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        if (name == null)
            name = "";
        if (code == null)
            code = "";
        if (oriname == null)
            oriname = "";
        if (startDate == null)
            startDate = "";
        if (endDate == null)
            endDate = "";
        int status = StringUtil.StringToId(request.getParameter("status"));
        int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
        int area = StringUtil.toInt(request.getParameter("area"));

        if (name.length() == 0 && code.length() == 0 && oriname.length() == 0
                && startDate.length() == 0 && endDate.length() == 0)
            return mapping.findForward(IConstants.SUCCESS_KEY);

        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate.length() > 0) {
            try {
                date1 = sdf.parse(startDate);
            } catch (Exception e) {
                request.setAttribute("tip", "开始时间格式不对！");
                return mapping.findForward(IConstants.FAILURE_KEY);
            }
        }
        if (endDate.length() > 0) {
            try {
                date2 = sdf.parse(endDate);
            } catch (Exception e) {
                request.setAttribute("tip", "结束时间格式不对！");
                return mapping.findForward(IConstants.FAILURE_KEY);
            }
        }
        if (date1 != null && date2 != null) {
            if (date2.before(date1)) {
                request.setAttribute("tip", "结束时间必须在开始时间之后！");
                return mapping.findForward(IConstants.FAILURE_KEY);
            }
        }
      //产品线权限限制
		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
		String catalogIdsTemp = "";
		if(!catalogIds2.equals("")){
			String[] splits = catalogIds2.split(",");
			for(int i=0;i<splits.length;i++){
				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
				}
			}
			if(catalogIds1.endsWith(",")){
				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
			}
		}
        //产品的condition
        String productCondition = " where id > 0";
        if (code.length() > 0) {
            productCondition += " and code = '" + code + "'";
        }
        if (oriname.length() > 0) {
            productCondition += " and oriname like '%" + oriname + "%'";
        }
        if (name.length() > 0) {
            productCondition += " and name like '%" + name + "%'";
        }
        if (status == 1) {
            productCondition += " and status >= 100";
        } else if(status == 2) {
            productCondition += " and status < 100";
        }
        if(parentId1 > 0){
        	if(!StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
        		productCondition += " and parent_id1=" + parentId1;
        	}else{
        		String[] catalogIds2Array = catalogIds2.split(",");
    			String ids2 = "-1";
    			for(int j = 0; j < catalogIds2Array.length; j++){
					if(CatalogCache.getParentCatalog(Integer.parseInt(catalogIds2Array[j])).getId() == parentId1){
						ids2 = ids2+","+catalogIds2Array[j];
					}
				}
        		productCondition += " and parent_id2 in (" + ids2+") ";
        	}
        	
        }else{
			productCondition += " and (parent_id1 in (" + catalogIds1 + ") or parent_id2 in (" + catalogIds2 + "))";
		} 
        //订单的condition
        String orderCondition = ""; //"status in (3, 6)";
        String stockOutCondition = "";
        if (startDate.length() > 0) {
            orderCondition += " and left(so.create_datetime, 10) >= '"
                    + startDate + "'";
            //stockOutCondition += " and left(so.last_oper_time, 10) >= '" + startDate + "'";
            stockOutCondition += " and left(uo.create_datetime, 10) >= '" + startDate + "'";
        }
        if (endDate.length() > 0) {
            orderCondition += " and left(so.create_datetime, 10) <= '"
                    + endDate + "'";
            //stockOutCondition += " and left(so.last_oper_time, 10) <= '" + endDate + "'";
            stockOutCondition += " and left(uo.create_datetime, 10) <= '" + endDate + "'";
        }
        if(area >= 0){
        	stockOutCondition += " and so.area=" + area;
        }
        /*
        String table1 = "select id from user_order where " + orderCondition;
        String table2 = "select id, code, name, oriname, price, price3, parent_id1, parent_id2 from product where "
                + productCondition;
        String query = "select * from (select product_id, sum(count) bc from user_order_product join ("
                + table1
                + ") temp on user_order_product.order_id=temp.id group by product_id) temp1 join ("
                + table2
                + ") temp2 on temp1.product_id = temp2.id order by bc desc";
        */
        // 构造查询条件
        String bjCount="0",gdCount="0";
//        if(area==0||area<0){//北京销量=产品出库量+产品出货量+最新表中的统计产品出货量(合格库类型)
//	        bjCount= "ifnull((select sum(so.count) from stockout_product so where so.product_id = product.id and so.type = 1 "
//	                + orderCondition
//	                + "),0) + ifnull((select sum(so.count) from stockout_product so where so.product_id = product.id and so.type = 0 "
//	                + orderCondition + "), 0) + ifnull((select sum(so.stockout_count) from order_stock_product so , order_stock where so.order_stock_id=order_stock.id and order_stock.status = 2 and so.product_id = product.id and so.stock_area = 0 and so.stock_type=0 "
//			        + orderCondition
//		            + "),0)";
//        }
        if(area==1||area<0){
	         gdCount = "ifnull((select sum(so.count) from stockout_product so where so.product_id = product.id and so.type = 2 "
	                + orderCondition
	                + "),0) + ifnull((select sum(so.stockout_count) from order_stock_product so, order_stock  where so.order_stock_id=order_stock.id and order_stock.status = 2 and so.product_id = product.id and so.stock_area in (1,2) and so.stock_type=0 "
	         		+ orderCondition
	         		+ "),0)";
        }
        String allOrderProductCount = "ifnull((select sum(uop.count) from user_order so join user_order_product uop on so.id=uop.order_id where so.status in (3,6,9,12,13,14) and uop.product_id=product.id "
                + orderCondition + "),0)";
        String allOrderPresentCount = "ifnull((select sum(uop.count) from user_order so join user_order_present uop on so.id=uop.order_id where so.status in (3,6,9,12,13,14) and uop.product_id=product.id "
            + orderCondition + "),0)";
        String query = "select * from (select id,name,oriname,code,price,price3,parent_id1,parent_id2, ("
                + bjCount
                + ") bj_count, ("
                + gdCount
                + ") gd_count, ("
                + allOrderProductCount
                + ") all_order_product_count, ("
                + allOrderPresentCount
                + ") all_order_present_count from product "
                + productCondition
                + ") temp where (temp.gd_count > 0 or temp.bj_count > 0 or temp.all_order_product_count > 0) order by (temp.all_order_product_count) desc";

        //数据库大查询锁，等待3秒
        if (!DbLock.statServerQueryLocked(100)) {
            ActionForward af = new ActionForward("/tip.jsp");
            return af;
        }

        voUser loginUser = (voUser) request.getSession().getAttribute("userView");
        DbOperation dbOp = null;
        try {
        	dbOp = new DbOperation();
            DbLock.statServerOperator = loginUser.getUsername() + "_历史销量查询_" + DateUtil.getNow();

            dbOp.init("adult_slave2");
            ResultSet rs = dbOp.executeQuery(query);
            List list = new ArrayList();
            voProduct product = null;
            while (rs.next()) {
                product = new voProduct();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setOriname(rs.getString("oriname"));
                product.setBjBuyCount(rs.getInt("bj_count"));
                product.setGdBuyCount(rs.getInt("gd_count"));
                if(area >= 0){
                	product.setBuyCount(product.getBjBuyCount() + product.getGdBuyCount());
                } else {
                	product.setBuyCount(rs.getInt("all_order_product_count") + rs.getInt("all_order_present_count"));
                }
                product.setCode(rs.getString("code"));
                product.setPrice(rs.getFloat("price"));
                product.setPrice3(rs.getFloat("price3"));
                product.setParentId1(rs.getInt("parent_id1"));
                product.setParentId2(rs.getInt("parent_id2"));
                list.add(product);
            }
            ICatalogService catalogService = ServiceFactory
                    .createCatalogService(IBaseService.CONN_IN_SERVICE, dbOp);
            List catalogList = catalogService.getCatalogList(null, -1, -1,
                    "id asc");
            HashMap catalogMap = new HashMap();
            Iterator iter = catalogList.listIterator();
            while (iter.hasNext()) {
                voCatalog catalog = (voCatalog) iter.next();
                catalogMap.put(Integer.valueOf(catalog.getId()), catalog);
            }
            request.setAttribute("productList", list);
            request.setAttribute("catalogMap", catalogMap);

            //query = "select p.parent_id1, sum(p.price3 * (sh.stock_bj + sh.stock_gd)) from product p join stock_history sh on p.id=sh.product_id join stock_operation so on sh.oper_id=so.id where so.status=2 and sh.stock_type=0 and sh.oper_type=0 ";
            query = "select p.parent_id1, sum(p.price3 * (uop.count)) from product p join user_order_product uop on p.id=uop.product_id join user_order uo on uop.order_id=uo.id ";
            if(area >= 0){
            	query += " join stock_operation so on uo.code=so.order_code ";
            }
            query += " where uo.status in (3,6,9,12,13,14) ";

            if (startDate.length() > 0) {
            	query += " and left(uo.create_datetime, 10) >= '"
                        + startDate + "'";
            }
            if (endDate.length() > 0) {
            	query += " and left(uo.create_datetime, 10) <= '"
                        + endDate + "'";
            }
            if(area >= 0){
            	query += " and so.area=" + area;
            }
            if (status == 1) {
                query += " and p.status >= 100 ";
            } else if(status == 2){
                query += " and p.status < 100 ";
            }

            query += " group by p.parent_id1 order by p.parent_id1 ";
            rs = dbOp.executeQuery(query);
            HashMap map = new HashMap();
            float total = 0;
            while(rs.next()){
            	int parentId = rs.getInt(1);
            	float price3 = rs.getFloat(2);
            	total += price3;
            	map.put(Integer.valueOf(parentId), Float.valueOf(price3));
            }
            map.put(Integer.valueOf(0), Float.valueOf(total));
            request.setAttribute("map", map);
        } finally {
            DbLock.statServerQueryLock.unlock();
            if(dbOp != null)
            	dbOp.release();
        }

        return mapping.findForward(IConstants.SUCCESS_KEY);
    }
}
