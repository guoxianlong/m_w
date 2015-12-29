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

import mmb.ware.WareService;

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
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;

/**
 * @author Bomb
 * 
 */
public class SearchStockinHistoryAction extends BaseAction {

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");

		String name = StringUtil.dealParam(request.getParameter("name"));
		String code = StringUtil.dealParam(request.getParameter("code"));
		String oriname = StringUtil.dealParam(request.getParameter("oriname"));
		String price = StringUtil.dealParam(request.getParameter("price"));
		String stockCount = StringUtil.dealParam(request.getParameter("stockCount"));
		String startDate = StringUtil.dealParam(request.getParameter("startDate"));
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));
		String area = StringUtil.convertNull(request.getParameter("area"));
		int productProxyId = StringUtil.toInt(request.getParameter("productProxyId"));
		String forward = StringUtil.convertNull(request.getParameter("forward"));
		String operationName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("operationName")));
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
		if (price == null)
			price = "";
		if (area == null)
			area = "";
		if (stockCount == null)
			stockCount = "";

		WareService service = new WareService();
		List proxyList = service.getSelects("supplier_standard_info", "where status=1 order by id");
		request.setAttribute("proxyList", proxyList);
		service.releaseAll();

		if (name.length() == 0 && code.length() == 0 && oriname.length() == 0
				&& startDate.length() == 0 && endDate.length() == 0
				&& price.length() == 0 && area.length() == 0 && productProxyId <= 0 && operationName.length() == 0)
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

		StringBuffer buf = new StringBuffer(512);
		
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

		String sql = "select *, (case when stock_area=0 then stockin_count else 0 end) stockin_bj, (case when stock_area=1 then stockin_count when stock_area=3 then stockin_count else 0 end) stockin_gd from " +
				     "(buy_stockin so join buy_stockin_product sh on so.id=sh.buy_stockin_id ) join product p on sh.product_id=p.id ";
		buf.append(" where so.status in (4,6) ");
		if (code.length() > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" sh.product_code='");
			buf.append(code);
			buf.append("' ");
		}
		if (area.length() > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" so.stock_area=");
			buf.append(area);
		}
		if (productProxyId > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" so.supplier_id=");
			buf.append(productProxyId);
		}
		if (price.length() > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			float fPrice = StringUtil.toFloat(price);
			buf.append(" p.price>=");
			buf.append(fPrice - 0.5);
			buf.append(" and p.price<=");
			buf.append(fPrice + 0.5);
		}
		if (startDate.length() > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" left(so.confirm_datetime, 10) >= '");
			buf.append(startDate);
			buf.append("'");
		}
		if (endDate.length() > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" left(so.confirm_datetime, 10) <= '");
			buf.append(endDate);
			buf.append("'");
		}
		if (oriname.length() > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" p.oriname like '%");
			buf.append(oriname);
			buf.append("%' ");
		}
		if (name.length() > 0) {
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" p.name like '%");
			buf.append(name);
			buf.append("%' ");
		}
		if (operationName.length() > 0){
			if(buf.length() > 0){
				buf.append(" and ");
			} else {
				buf.append(" where ");
			}
			buf.append(" so.name like '%");
			buf.append(operationName);
			buf.append("%' ");
		}

		if(stockCount.length() > 0){
			buf.append(" having ");
			if(area.trim().length() > 0){
				int intArea = StringUtil.StringToId(area);
				if(intArea == 1){
					buf.append(" stockin_gd=");
				}else {
					buf.append(" stockin_bj=");
				}
				buf.append(stockCount);
			} else {
				buf.append(" (stockin_bj=");
				buf.append(stockCount);
				buf.append(" or stockin_gd=");	
				buf.append(stockCount);
				buf.append(") ");
			}
		}
		
		if(!catalogIds1.equals("")||!catalogIds2.equals("")){
			if(buf.length() > 0){
				buf.append(" and ");
			}

			buf.append(" (");
			if(catalogIds1.equals("")){
				buf.append("p.parent_id2 in ("+catalogIds2+")");
			}else{
				if(catalogIds2.equals("")){
					buf.append("p.parent_id1 in ("+catalogIds1+")");
				}else{
					buf.append("p.parent_id1 in ("+catalogIds1+") or p.parent_id2 in ("+catalogIds2+")");
				}
			}
			buf.append(") ");

		}

		buf.append("  order by so.id desc");

		sql += buf.toString();
		DbOperation dbOp = new DbOperation();
		try {
			dbOp.init("adult_slave2");
			ResultSet rs = dbOp.executeQuery(sql);
			List list = new ArrayList();
			voProduct product = null;
			while (rs.next()) {
				product = new voProduct();
				product.setId(rs.getInt("p.id"));
				product.setName(rs.getString("p.name"));
				product.setOriname(rs.getString("p.oriname"));
				// product.setBuyCount(rs.getInt("bc"));
				product.setBjBuyCount(rs.getInt("stockin_bj"));
				product.setGdBuyCount(rs.getInt("stockin_gd"));
				product.setCode(rs.getString("p.code"));
				product.setPrice(rs.getFloat("p.price"));
				product.setPrice3(rs.getFloat("p.price3"));
				product.setPrice5(rs.getFloat("sh.price3"));
				product.setParentId1(rs.getInt("p.parent_id1"));
				product.setParentId2(rs.getInt("p.parent_id2"));
				product.setCreateDatetime(rs.getTimestamp("so.create_datetime"));
				product.setRemark(String.valueOf(rs.getInt("so.id"))+","+rs.getString("so.code"));
				list.add(product);
			}
			ICatalogService catalogService = ServiceFactory.createCatalogService(IBaseService.CONN_IN_SERVICE, dbOp);
			List catalogList = catalogService.getCatalogList(null, -1, -1, "id asc");
			HashMap catalogMap = new HashMap();
			Iterator iter = catalogList.listIterator();
			while (iter.hasNext()) {
				voCatalog catalog = (voCatalog) iter.next();
				catalogMap.put(Integer.valueOf(catalog.getId()), catalog);
			}
			request.setAttribute("productList", list);
			request.setAttribute("catalogMap", catalogMap);

            String query = "select p.parent_id1, sum(sh.price3 * (sh.stockin_count)), sum(sh.price3 * (case when stock_area=0 then stockin_count else 0 end)), sum(sh.price3 * (case when stock_area=1 then stockin_count when stock_area=3 then stockin_count else 0 end)) from product p join buy_stockin_product sh on p.id=sh.product_id join buy_stockin so on sh.buy_stockin_id=so.id where so.status in (4,6) ";

            if (startDate.length() > 0) {
            	query += " and left(so.confirm_datetime, 10) >= '"
                        + startDate + "'";
            }
            if (endDate.length() > 0) {
            	query += " and left(so.confirm_datetime, 10) <= '"
                        + endDate + "'";
            }
            if(area.length() > 0){
            	query += " and so.stock_area=" + area;
            }

            query += " group by p.parent_id1 order by p.parent_id1 ";
            rs = dbOp.executeQuery(query);
            HashMap map = new HashMap();
            HashMap gd = new HashMap();
            HashMap bj = new HashMap();
            float total = 0;
            while(rs.next()){
            	int parentId = rs.getInt(1);
            	float price3 = rs.getFloat(2);
            	float priceBj = rs.getFloat(3);
            	float priceGd = rs.getFloat(4);
            	total += price3;
            	map.put(Integer.valueOf(parentId), Float.valueOf(price3));
            	bj.put(Integer.valueOf(parentId), Float.valueOf(priceBj));
            	gd.put(Integer.valueOf(parentId), Float.valueOf(priceGd));
            }
            map.put(Integer.valueOf(0), Float.valueOf(total));
            request.setAttribute("map", map);
            request.setAttribute("mapBj", bj);
            request.setAttribute("mapGd", gd);
		} finally {
			dbOp.release();
		}

		if(forward.equals("export")){
			return mapping.findForward("exportSearchStockinHistory");
		}
		
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}
