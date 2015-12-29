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

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICatalogService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.DbLock;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;

/**
 * @author yaolan
 *  
 */
public class SearchNodealProductAction extends BaseAction {
 
	/**
	 *  无销量产品查询
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
		// 加载品牌
		String productBrandIds = "-1";
		for (int i = 0; i < productLineList.size(); i++) {
			voProductLine bean = (voProductLine) productLineList.get(i);
			productBrandIds =  productBrandIds + "," + ProductLinePermissionCache.getProductBrandIds(adminUser, bean.getId());	//仅列出用户有权限看到的品牌
		}
		if(productBrandIds.endsWith(",")){
			productBrandIds = productBrandIds.substring(0, productBrandIds.length()-1);
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService service = null;
		List productBrandList = new ArrayList();
		try{
			service = new WareService(dbOperation);
			productBrandList = service.getProductBrandList("id in (" + productBrandIds + ")", -1, -1, "id asc");
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOperation!=null){
				dbOperation.release();
			}
		}
    	request.setAttribute("productBrandIds", productBrandIds);
		request.setAttribute("productBrandList", productBrandList);
		int productBrandId = 0;	//品牌id
		String brandtext = StringUtil.convertNull(request.getParameter("brandtext"));	//品牌名称
		for (int i = 0; i < productBrandList.size(); i++) {
			voSelect vo = (voSelect) productBrandList.get(i);
			if(vo != null && brandtext.equals(vo.getName())){
				productBrandId = vo.getId();
				break;
			}
		}
		Map  productBrandMap = new HashMap();
		for (int i = 0; i < productBrandList.size(); i++) {
			voSelect vo = (voSelect) productBrandList.get(i);
			productBrandMap.put(new Integer(vo.getId()), vo.getName());
		}
		request.setAttribute("productBrandId", String.valueOf(productBrandId));
		
		int stockcount = StringUtil.StringToId(request.getParameter("stockcount"));
		
		if (name.length() == 0 && code.length() == 0 && oriname.length() == 0
				&& startDate.length() == 0 && endDate.length() == 0 && productBrandId==0 && stockcount == 0 )
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
		String productCondition = " p.id > 0";
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
		if(productBrandId>0){
			productCondition += " and p.brand = " + productBrandId;
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
		
		if (startDate.length() > 0) {
			orderCondition += " and uo.create_datetime >='"+startDate+"'";
			
		}
		if (endDate.length() > 0) {
			orderCondition += " and uo.create_datetime <='"+endDate+"'";
		}
		//发货量查询sql
		StringBuffer query2 = new StringBuffer();
		query2.append("select uop.product_id,sum(count) totalcount1 from user_order uo,user_order_product uop")
			  .append(" where uo.status in(3,6,9,12,13,14)")
			  .append(orderCondition)
			  .append(" and uo.id=uop.order_id group by uop.product_id");
//		System.out.println(query2);
		// 构造查询条件
		
	     
		String query = "select p.id,p.code,p.name,p.oriname,p.proxy_" +
				"id,p.price,p.price3,p.price5,p.parent_id1,p.parent_id2,p.parent_id3,p.brand from product p where "
			+productCondition +"  order by p.id desc ";
	   //数据库大查询锁，等待3秒
        if (!DbLock.statServerQueryLocked(100)) {
            ActionForward af = new ActionForward("/tip.jsp");
            return af;
        }
        voUser loginUser = (voUser) request.getSession().getAttribute("userView");

        DbOperation dbOp = null;
		dbOp = new DbOperation();
        DbLock.statServerOperator = loginUser.getUsername() + "_无销量产品查询_" + DateUtil.getNow();

        dbOp.init("adult_slave2");
      	IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			String sqlSupplier = "select c.product_id pid,GROUP_CONCAT(d.name)sName from product p left outer join product_supplier c on p.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 group by c.product_id";
			ResultSet supplierRs = service.getDbOp().executeQuery(sqlSupplier);
			Map proxyMap = new HashMap();
			while (supplierRs.next()) {
				if(supplierRs.getString("sName")!=null&&!supplierRs.getString("sName").equals("")){
					String proxyName = StringUtil.convertNull(supplierRs.getString("sName"));
					proxyMap.put(new Integer(supplierRs.getInt("pid")),proxyName);
				}
			}
			supplierRs.close();
//			System.out.println("start proxyMap="+proxyMap.size());
			ResultSet rs1 = dbOp.executeQuery(query2.toString());
			Map fahuoMap = new HashMap();
			while(rs1.next()){
				fahuoMap.put(new Integer(rs1.getInt(1)), new Integer(rs1.getInt(2)));
			}
//			System.out.println( "start  fahuoMap="+fahuoMap.size());
			List list = new ArrayList();
			ResultSet rs = dbOp.executeQuery(query);
			voProduct product = null;
			
			while (rs.next()) {
				product = new voProduct();
				product.setId(rs.getInt("id"));
				product.setCode(rs.getString("code"));
				product.setName(rs.getString("name"));
				product.setOriname(rs.getString("oriname"));
				product.setProxyId(rs.getInt("proxy_id"));
				product.setPrice(rs.getFloat("price"));
				product.setPrice3(rs.getFloat("price3"));
				product.setPrice5(rs.getFloat("price5"));
				product.setParentId1(rs.getInt("parent_id1"));
				product.setParentId2(rs.getInt("parent_id2"));
				product.setParentId3(rs.getInt("parent_id3"));
				product.setBrand(rs.getInt("brand"));
				list.add(product);
			}
			if(rs!=null){rs.close();}
			// 加载库存
//			System.out.println( "start list ="+list.size());
			for(int l=0;l<list.size();l++){
				voProduct  vo = (voProduct) list.get(l);
				if(fahuoMap.get(new Integer(vo.getId()))!=null){
					list.remove(l);
					l--;
			    	continue;
				}
				vo.setPsList(psService.getProductStockList("product_id=" + vo.getId(), -1, -1, null));
				int realstockcount = vo.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED)+ vo.getStockAllType(ProductStockBean.STOCKTYPE_CHECK) + vo.getLockCountAllType(ProductStockBean.STOCKTYPE_CHECK);
				if(realstockcount==0 || realstockcount<stockcount){ // 判断是否符合合格库数量内容。
				    list.remove(l);
				    l--;
				    continue;
				}
				if(productBrandId == 0){ //0设置品牌名称
					vo.setIntro((String)productBrandMap.get(new Integer(vo.getBrand())));
				}else{
					vo.setIntro(brandtext);
				}
				// 这只供应商名称
				vo.setProxyName((String)proxyMap.get(new Integer(vo.getId())));
				
			}
//			System.out.println( "end list ="+list.size());
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