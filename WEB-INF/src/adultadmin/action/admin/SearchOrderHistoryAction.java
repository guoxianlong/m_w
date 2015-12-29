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
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICatalogService;
import adultadmin.util.DateUtil;
import adultadmin.util.DbLock;
import adultadmin.util.StatUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;

/**
 * @author Bomb
 *  
 */
public class SearchOrderHistoryAction extends BaseAction {

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
		int status = StringUtil.StringToId(request.getParameter("status"));
		//int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
		String[] parentId1s = new String[]{}; 
		if(request.getParameterValues("parentId1")!=null){
			parentId1s=request.getParameterValues("parentId1");
			request.setAttribute("parentId1s", parentId1s);
		} 
		int area = StringUtil.toInt(request.getParameter("area"));
		
		List productLineList =ProductLinePermissionCache.getProductList(adminUser); //获取当前用户下 产品线 
		request.setAttribute("productLineList", productLineList);
		List productBrandList = new ArrayList();
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave2");
		WareService service = new WareService(dbOperation);
		try{
			String productBrandIds = "-1";
			for (int i = 0; i < productLineList.size(); i++) {
				voProductLine bean = (voProductLine) productLineList.get(i);
				productBrandIds =  productBrandIds + "," + ProductLinePermissionCache.getProductBrandIds(adminUser, bean.getId());	//仅列出用户有权限看到的品牌
			}
			if(productBrandIds.endsWith(",")){
				productBrandIds = productBrandIds.substring(0, productBrandIds.length()-1);
			}
			productBrandList = service.getProductBrandList("id in (" + productBrandIds + ")", -1, -1, "id asc");
			request.setAttribute("productBrandIds", productBrandIds);
			request.setAttribute("productBrandList", productBrandList);
		}finally{
			service.releaseAll();
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
		String condition = " where os.status = 2";
		String orderCondition = " where p.id>0";
		String productCondition = "";
		
		int productBrandId = 0;	//品牌id
		String brandtext = StringUtil.convertNull(request.getParameter("brandtext"));	//品牌名称
		for (int i = 0; i < productBrandList.size(); i++) {
			voSelect vo = (voSelect) productBrandList.get(i);
			if(vo != null && brandtext.equals(vo.getName())){
				productBrandId = vo.getId();
				break;
			}
		}
		request.setAttribute("productBrandId", String.valueOf(productBrandId));
		
		
		 //判断是否为无锡单
        if(areaGroup!=null && areaGroup.equals("1")){
        	orderCondition = orderCondition +" and uo.areano<>9";
        	condition = condition + " and os.areano<>9";
        }
        if(areaGroup!=null && areaGroup.equals("2")){
        	orderCondition = orderCondition +" and uo.areano=9";
        	condition = condition + " and os.areano=9";
        }
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
		} else if(status == 2) {
			productCondition += " and p.status < 100";
		}
		if(!"".equals(brandtext)){	//指定品牌查询
			productCondition+=" and spi.brand_id= "+productBrandId;
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
			productCondition += " and (p.parent_id1 in (" + parentId1 + ") or p.parent_id2 in (" + ids2 + "))";
		}else{
			productCondition += " and (p.parent_id1 in (" + catalogIds1 + ") or p.parent_id2 in (" + catalogIds2 + "))";
		}   
		
		if(productLine.length()>0){
			productCondition+=" "+ProductLinePermissionCache.getCarglogIdsByProudctId(productLine);
		}
		
		if (startDate.length() > 0) {
			condition = condition + " and os.last_oper_time >= '"+startDate+"'";
			orderCondition += " and uo.create_datetime >= '"+ startDate + "' and uo.id >= "+StatUtil.getDayFirstOrderId(startDate);
		}
		if (endDate.length() > 0) {
			condition = condition + " and os.last_oper_time <= '"+endDate+" 23:59:59'";
			orderCondition += " and uo.create_datetime <= '"+ startDate + " 23:59:59'";
		}
       
        
		// 构造查询条件
		if(area==1||area<0){
			condition = condition + " and os.stock_area in (1,2,3)";
		}
		condition = condition + productCondition;
		orderCondition = orderCondition + productCondition;
		
		String query = "";

		//数据库大查询锁，等待3秒
		if (!DbLock.statServerQueryLocked(100)) {
			ActionForward af = new ActionForward("/tip.jsp");
			return af;
		}

		voUser loginUser = (voUser) request.getSession().getAttribute("userView");

		DbOperation dbOp = null;
		try {
			dbOp = new DbOperation();
			DbLock.statServerOperator = loginUser.getUsername() + "_成交订单销量查询_" + DateUtil.getNow();
			dbOp.init("adult_slave2");
			
			//出库量
			query = "select p.id,p.name,p.oriname,p.code,p.price,p.price3,p.parent_id1,p.parent_id2,sum(osp.stockout_count) gd_count from order_stock os join order_stock_product osp on os.id = osp.order_stock_id join product p on osp.product_id = p.id ";
			query +=" left join spi_sub_product_info ssp on p.id=ssp.sub_product_id left join spi_product_info spi on ssp.product_info_id=spi.id ";
			query = query + condition;
			query = query + " group by p.id order by gd_count desc";
			ResultSet rs = dbOp.executeQuery(query);
			List list = new ArrayList();
			voProduct product = null;
			LinkedHashMap productMap = new LinkedHashMap();
			while (rs.next()) {
				product = new voProduct();
				product.setId(rs.getInt("id"));
				product.setName(rs.getString("name"));
				product.setOriname(rs.getString("oriname"));
				product.setGdBuyCount(rs.getInt("gd_count"));
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
			// 查询现在的订单出货数据
			query = "select p.parent_id1, sum(p.price3 * (sh.stockout_count)) from product p join order_stock_product sh on p.id=sh.product_id join order_stock so on sh.order_stock_id=so.id " +
					" where so.status=2";

			if (startDate.length() > 0) {
				query += " and so.last_oper_time >= '"
					+ startDate + "'";
			}
			if (endDate.length() > 0) {
				query += " and so.last_oper_time <= '"
					+ endDate + " 23:59:59'";
			}
			if(area >= 0){
				query += " and so.stock_area in (1,2,3)";
			}
			if (status == 1) {
				query += " and p.status >= 100 ";
			} else if(status == 2) {
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
