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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICatalogService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.DbLock;
import adultadmin.util.StatUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;

/**
 * @author shimingsong
 *  
 */
public class SearchCjPresentAction extends BaseAction {
 
	/**
	 *  成交订单产品数量查询
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
		String areaGroup = request.getParameter("areaGroup");
		String productLine = StringUtil.convertNull(request.getParameter("productLine"));
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
		List productLineList =ProductLinePermissionCache.getProductList(adminUser); //获取当前用户下 产品线 
		request.setAttribute("productLineList", productLineList);
		
		int status = StringUtil.StringToId(request.getParameter("status"));
		String[] parentId1s = new String[] {};
		if (request.getParameterValues("parentId1") != null) {
			parentId1s = request.getParameterValues("parentId1");
			request.setAttribute("parentId1s", parentId1s);
		}
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
		String productCondition = " and p.id > 0";
		if (code.length() > 0) {
			productCondition += " and p.code = '" + code + "'";
		}
		if (oriname.length() > 0) {
			productCondition += " and p.oriname like '%" + oriname + "%'";
		}
		if (name.length() > 0) {
			productCondition += " and p.name like '%" + name + "%'";
		}
		if (status == 1) {
			productCondition += " and p.status >= 100";
		} else if (status == 2) {
			productCondition += " and p.status < 100";
		}
		//当产品分类数不为0且没有选中全部时查询
		if (parentId1s.length != 0 && !parentId1s[0].equals("0")) {
			String[] catalogIds2Array = catalogIds2.split(",");
			String ids2 = "-1";
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < parentId1s.length; i++) {
				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1s[i]))){
					for(int j = 0; j < catalogIds2Array.length; j++){
						if(CatalogCache.getParentCatalog(Integer.parseInt(catalogIds2Array[j])).getId()==Integer.parseInt(parentId1s[i])){
							ids2 = ids2+","+catalogIds2Array[j];
						}
					}
					continue;
				}
				sb.append(parentId1s[i] + ",");
			}
			if(sb.length()>0){
				sb.deleteCharAt(sb.length() - 1);
			}else{
				sb.append("-1");
			}
			String parentId1 = sb.toString();
			productCondition += " and (parent_id1 in (" + parentId1 + ") or parent_id2 in (" + ids2 + "))";
		}else{
			productCondition += " and (p.parent_id1 in (" + catalogIds1 + ") or p.parent_id2 in (" + catalogIds2 + "))";
		}
		
		if(productLine.length()>0){//产品线判断
			productCondition+=" "+ProductLinePermissionCache.getCarglogIdsByProudctId(productLine);
		}
		//订单的condition
		String orderCondition = ""; //"status in (3, 6)";
		//判断是否为无锡单
        String wuxiCondition="";
		
		if(areaGroup!=null && areaGroup.equals("0")){
			
		}
        if(areaGroup!=null && areaGroup.equals("1")){
        	wuxiCondition+=" and uo.areano<>9 ";
		}
        if(areaGroup!=null && areaGroup.equals("2")){
        	wuxiCondition+=" and uo.areano=9 ";
        }
		if (startDate.length() > 0) {
			int minId = StatUtil.getDateTimeFirstOrderId(startDate);
			orderCondition += " and uo.id >= "+minId+wuxiCondition+" and uo.create_datetime>= '"
					+ startDate + "'";
		}
		if (endDate.length() > 0) {
			orderCondition += " and uo.create_datetime <= '"
					+ endDate + "'";
		}
		// 构造查询条件
		String query = "select p.id,p.code,p.name,p.oriname,p.price,p.price3,p.price5,p1.totalcount,p.parent_id1,p.parent_id2,p.parent_id3 from (" 
					  +" select uop.product_id, sum(count) totalcount from user_order uo,user_order_present uop"
					  +" where uo.status in(3,6,9,12,13,14)"
					  +orderCondition
					  +" and uo.id=uop.order_id"
					  +" group by uop.product_id) p1,product p"
					  +" where p1.product_id=p.id "
					  +productCondition +" order by p1.totalcount";
		//发货量查询sql
		StringBuffer query2 = new StringBuffer();
		query2.append("select uop.product_id,sum(count) totalcount1 from user_order uo,user_order_present uop")
			  .append(" where uo.status in(6,11,12,13,14)")
			  .append(orderCondition)
			  .append(" and uo.id=uop.order_id group by uop.product_id");
		
        //数据库大查询锁，等待3秒
        if (!DbLock.statServerQueryLocked(100)) {
            ActionForward af = new ActionForward("/tip.jsp");
            return af;
        }
        voUser loginUser = (voUser) request.getSession().getAttribute("userView");

        DbOperation dbOp = null;
		dbOp = new DbOperation();
        DbLock.statServerOperator = loginUser.getUsername() + "_成交订单赠品查询_" + DateUtil.getNow();

        dbOp.init("adult_slave2");
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			ResultSet rs1 = dbOp.executeQuery(query2.toString());
			
			Map fahuoMap = new HashMap();
			while(rs1.next()){
				fahuoMap.put(new Integer(rs1.getInt(1)), new Integer(rs1.getInt(2)));
			}
			
			int fhuoCount = 0;
			List list = new ArrayList();
			ResultSet rs = dbOp.executeQuery(query);
			voProduct product = null;
			
			while (rs.next()) {
				product = new voProduct();
				product.setId(rs.getInt("id"));
				product.setCode(rs.getString("code"));
				product.setName(rs.getString("name"));
				product.setOriname(rs.getString("oriname"));
				product.setPrice(rs.getFloat("price"));
				product.setPrice3(rs.getFloat("price3"));
				product.setPrice5(rs.getFloat("price5"));
					product.setBuyCount(rs.getInt("totalcount"));
				if(fahuoMap.get(new Integer(rs.getInt("id"))) != null){
					fhuoCount = ((Integer)fahuoMap.get(new Integer(rs.getInt("id")))).intValue();
				}
				product.setFhuoCount(fhuoCount);
				product.setParentId1(rs.getInt("parent_id1"));
				product.setParentId2(rs.getInt("parent_id2"));
				product.setParentId3(rs.getInt("parent_id3"));
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
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
		} finally {
            DbLock.statServerQueryLock.unlock();
            psService.releaseAll();
			if(dbOp != null)
				dbOp.release();
		}

		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}