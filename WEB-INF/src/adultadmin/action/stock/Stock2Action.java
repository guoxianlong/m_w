/*
 * Created on 2007-11-14
 *
 */
package adultadmin.action.stock;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductLineCatalog;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.PrintLogBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockHistoryBean;
import adultadmin.bean.stock.StockOperationBean;
import adultadmin.bean.stock.StockProductHistoryBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;
/**
 * 
 * 作者：赵林
 * 
 * 创建日期：2009-2-20
 * 
 * 说明：采购相关操作
 */
public class Stock2Action {

	public static byte[] buyPlanLock = new byte[0];
	public static byte[] buyOrderLock = new byte[0];
	public static byte[] buyStockLock = new byte[0];
	public static byte[] buyReturnLock = new byte[0];
	public static byte[] stockBatchPriceLock = new byte[0];
	private static DecimalFormat df = new DecimalFormat("0.##");
	public Log stockLog = LogFactory.getLog("stock.Log");

	/**
	 * 作者：李宁
	 * 创建时间：2011-04-28
	 * 说明：产品采购中供应商推荐
	 * 参数及返回值说明：
	 * @param productId	要采购的产品id
	 * @return	推荐的供应商id
	 */
	public int supplierRecommend(int productId, IBaseService service, String supplierIds) {
		int supplierId = 0;
		ResultSet rs = null;
		String query = "select * from product_supplier where product_id = "+productId+" order by id desc limit 1";
		try {
			rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				supplierId = rs.getInt("supplier_id");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return supplierId;
	}
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：打印日志列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void printLogList(HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		int countPerPage = 50;
		int operId = StringUtil.StringToId(request.getParameter("operId"));
		int type = StringUtil.StringToId(request.getParameter("type"));
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
        AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbOp_slave);
		try {
			StockOperationBean bean = service.getStockOperation("id = " + operId);
			String condition = "oper_id=" + bean.getId();
			//总数
			int totalCount = service.getPrintLogCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			paging.setPrefixUrl("printLog.jsp?operId=" + operId + "&type=" + type);
			List printLogList = service.getPrintLogList("oper_id=" + bean.getId() + " and type=" + type, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc" );
			Iterator iter = printLogList.listIterator();
			while(iter.hasNext()){
				PrintLogBean plBean = (PrintLogBean)iter.next();
				plBean.setUser(admiService.getAdmin(plBean.getUserId()));
			}

			request.setAttribute("paging", paging);
			request.setAttribute("bean", bean);
			request.setAttribute("printLogList", printLogList);
		} finally {
			admiService.releaseAll();
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：预计到货表列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void buyStockList(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(55);

		int countPerPage = 50;
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
        AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbOp_slave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		WareService wareService = new WareService(dbOp_slave);
		try {
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			
			//产品线权限
//			String ids = "-1,";
//			String parent1Id = ProductLinePermissionCache.getCatalogIds1(user);
//			String parent2Id = ProductLinePermissionCache.getCatalogIds2(user);
//			parent1Id = (StringUtil.isNull(parent1Id) ? "-1" : parent1Id);
//			parent2Id = (StringUtil.isNull(parent2Id) ? "-1" : parent2Id);
//			String sql = "bsp.product_id = p.id and (p.parent_id1 in (" + parent1Id + ") or p.parent_id2 in (" + parent2Id + "))";
//			List idsLists = service.getFieldList("bsp.buy_stock_id", "buy_stock_product bsp, product p", 
//					sql, -1, -1, "bsp.buy_stock_id", "bsp.buy_stock_id", "int");
//			for (int i=0;idsLists!=null&&i<idsLists.size();i++) {
//				int id = ((Integer) idsLists.get(i)).intValue();
//				if(ids.indexOf(id+",")==-1){
//					ids += id + ",";
//				}
//			}
//			ids = ids.substring(0, ids.length() - 1);
			
			String condition = "status != "+BuyStockBean.STATUS8;
			StringBuilder buf = new StringBuilder();
			if(!viewAll){ //没有查看所有进货单的权限
				buf.append(" and create_user_id=");
				buf.append(user.getId());
			}
			if(buf.length() > 0){
				condition = condition + buf.toString();
			}
//			condition += " and id in (" + ids + ")";

			//总数
			int totalCount = service.getBuyStockCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = service.getBuyStockList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			if(list!=null){
				Iterator iter = list.listIterator();
				while(iter.hasNext()){
					BuyStockBean stockBean = (BuyStockBean)iter.next();
					stockBean.setBuyOrder(service.getBuyOrder("id=" + stockBean.getBuyOrderId()));

					//产品类别
					String productType = "";
					String parentId1 = "";
					String parentId2 = "";
					ArrayList typeKeyList = service.getFieldList(
							"p.parent_id1", 
							"buy_stock_product bsp, product p" , 
							"bsp.product_id = p.id and bsp.buy_stock_id ="+stockBean.getId(),
							-1, -1, "p.parent_id1", null, "String");
					Iterator typeKeyIter = typeKeyList.listIterator();
					while(typeKeyIter.hasNext()){
						String key = (String) typeKeyIter.next();
						parentId1 += (key == null ? "-1" : key) + ", ";
					}
					typeKeyList = service.getFieldList(
							"p.parent_id2", 
							"buy_stock_product bsp, product p" , 
							"bsp.product_id = p.id and bsp.buy_stock_id ="+stockBean.getId(),
							-1, -1, "p.parent_id2", null, "String");
					typeKeyIter = typeKeyList.listIterator();
					while(typeKeyIter.hasNext()){
						String key = (String) typeKeyIter.next();
						parentId2 += (key == null ? "-1" : key) + ", ";
					}
					if (parentId1 != null && parentId1.length() > 0) {
						parentId1 = parentId1.substring(0, parentId1.length() -2);
					} else {
						parentId1 = "-1";
					}
					if (parentId2 != null && parentId2.length() > 0) {
						parentId2 = parentId2.substring(0, parentId2.length() -2);
					} else {
						parentId2 = "-1";
					}
					String productLineSql = "pl.id = plc.product_line_id and ((plc.catalog_id in (" + parentId1 
					+ ") and plc.catalog_type = 1) or (plc.catalog_type = 2 and plc.catalog_id "
					+ " in (" + parentId2 + ")))";
					typeKeyList = service.getFieldList(
							"pl.name", 
							"product_line pl, product_line_catalog plc", 
							productLineSql, -1, -1, "pl.id", "pl.id", "String");
					typeKeyIter = typeKeyList.listIterator();
					while(typeKeyIter.hasNext()){
						String key = (String) typeKeyIter.next();
						productType += (key == null ? "" : key + ", ");
					}
					if (productType != null && productType.length() > 0) {
						productType = productType.substring(0, productType.length() - 2);
					} else {
						productType = "无";
					}
					stockBean.setProductType(productType);

					String proxyName = wareService.getString("ssi.name", "buy_stock_product bsp, supplier_standard_info ssi", 
							"ssi.status = 1 and bsp.product_proxy_id = ssi.id and bsp.buy_stock_id = "+stockBean.getId());
					if (proxyName == null || proxyName.length() == 0) {
						proxyName = "无";
					}
					stockBean.setProxyName(proxyName);


					//操作人
					voUser creatUser = admiService.getAdmin(stockBean.getCreateUserId());
					voUser auditingUser = admiService.getAdmin(stockBean.getAuditingUserId());
					stockBean.setCreatUser(creatUser);
					stockBean.setAuditingUser(auditingUser);
				}
			}
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 order by id");
//			List productLineList = adminService.getProductLineList("product_line.id in (" + productLines + ")");
			List productLineList = wareService.getProductLineList("1=1");
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			paging.setPrefixUrl("buyStockList.jsp");
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		} finally {
			service.releaseAll();
		}
	}

	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-06-15
	 * 
	 * 说明：预计到货表待转换列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void transformBuyStockList(HttpServletRequest request, HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(55);
		//判断是否是非我司仓 1：非我司仓
		int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
		int stockId = StringUtil.StringToId(request.getParameter("stockId"));
		String stockCode = StringUtil.convertNull(request.getParameter("stockCode"));
		String startDate = StringUtil.convertNull(request.getParameter("startDate"));
		String endDate = StringUtil.convertNull(request.getParameter("endDate"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String oriName = StringUtil.convertNull(request.getParameter("oriName"));
		String createUser = StringUtil.convertNull(request.getParameter("createUser"));
		String affirmUser = StringUtil.convertNull(request.getParameter("affirmUser"));
		int productLine = StringUtil.StringToId(request.getParameter("productLine"));
		int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));
		oriName = Encoder.decrypt(oriName);//解码为中文
		if(oriName == null){//解码失败,表示已经为中文,则返回默认
			oriName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("oriName")));
		}
		
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbOp_slave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		WareService wareService = new WareService(dbOp_slave);
		try{
			String condition = "status in ("+BuyStockBean.STATUS2+","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+") and type=" + isBTwoC;
			if(!viewAll){
				condition += " and create_user_id="+user.getId();
			}
			StringBuilder search = new StringBuilder();
			StringBuilder param = new StringBuilder();
			if(!StringUtil.isNull(stockCode)) {
				param.append("stockCode="+stockCode+"&");
				search.append(" and code = '" + stockCode +"'");
			}
			if(!StringUtil.isNull(startDate) && !StringUtil.isNull(endDate)) {
				param.append("startDate="+startDate+"&");
				param.append("endDate="+endDate+"&");
				search.append(" and left(expect_arrival_datetime, 10) between '");
				search.append(startDate + "' and '" + endDate + "'");
			}
			if(!StringUtil.isNull(createUser)) {
				param.append("createUser="+createUser+"&");
				search.append(" and create_user_id in (select u.id from user u");
				search.append(" where u.username like '%"+createUser+"%')");
			}
			if(!StringUtil.isNull(affirmUser)) {
				param.append("affirmUser="+affirmUser+"&");
				search.append(" and auditing_user_id in (select u.id from user u");
				search.append(" where u.username like '%"+affirmUser+"%')");
			}
			String ids = "-1,";
			String parent1Id = ProductLinePermissionCache.getCatalogIds1(user);
			String parent2Id = ProductLinePermissionCache.getCatalogIds2(user);
			parent1Id = (StringUtil.isNull(parent1Id) ? "-1" : parent1Id);
			parent2Id = (StringUtil.isNull(parent2Id) ? "-1" : parent2Id);
			String sql = "bsp.product_id = p.id and (p.parent_id1 in (" + parent1Id + ") or p.parent_id2 in (" + parent2Id + "))";
			List idsLists = service.getFieldList("bsp.buy_stock_id", "buy_stock_product bsp, product p", 
					sql, -1, -1, "bsp.buy_stock_id", "bsp.buy_stock_id", "int");
			for (int i=0;idsLists!=null&&i<idsLists.size();i++) {
				int id = ((Integer) idsLists.get(i)).intValue();
				if(ids.indexOf(id+",")==-1){
					ids += id + ",";
				}
			}
			if(!StringUtil.isNull(productCode)) {
				param.append("productCode="+productCode+"&");
				sql = "product_id in (select id from product where code = '"+productCode+"') and buy_stock_id in (" + ids.substring(0, ids.length() - 1) + ")" 
						+ (ids.length() > 0 ? " and buy_stock_id in ("+ids.substring(0, ids.length()-1)+")" : "");
				List idList = service.getFieldList(
								"buy_stock_id", "buy_stock_product", sql, -1, -1, "buy_stock_id", "buy_stock_id", "int");
				ids = "";
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids += id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if(!StringUtil.isNull(oriName)) {
				param.append("oriName="+Encoder.encrypt(oriName)+"&");
				sql = "product_id in (select id from product where oriname like '%"+oriName+"%')" 
						+ (ids.length() > 0 ? " and buy_stock_id in ("+ids.substring(0, ids.length()-1)+")" : "");
				List idList = service.getFieldList(
						"buy_stock_id", "buy_stock_product", sql, -1, -1, "buy_stock_id", "buy_stock_id", "int");
				ids = "";
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids += id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if (productLine > 0) {
				param.append("productLine="+productLine+"&");
				sql = "product_id in (select p.id from product p, product_line_catalog plc "
								+ "where case when plc.catalog_type = 1 then plc.catalog_id = p.parent_id1 "
								+ "when plc.catalog_type = 2 then plc.catalog_id = p.parent_id2 end and plc."
								+ "product_line_id = " + productLine + ")" + (ids.length() > 0 ? 
								" and buy_stock_id in ("+ids.substring(0, ids.length()-1)+")" : "");
				List idList = service.getFieldList("buy_stock_id", "buy_stock_product",
						sql, -1, -1, "buy_stock_id", "buy_stock_id", "int");
				ids = "";
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids += id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if(supplierId > 0){
				param.append("supplierId="+supplierId+"&");
				sql = "product_proxy_id = " + supplierId + (ids.length() > 0 ? 
						" and buy_stock_id in ("+ids.substring(0, ids.length()-1)+")" : "");
				List idList = service.getFieldList(
						"buy_stock_id", "buy_stock_product", sql, -1, -1, "buy_stock_id", "buy_stock_id", "int");
				ids = "";
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids += id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if(ids.length() > 0) {
				ids = ids.substring(0, ids.length() - 1);
				search.append(" and id in ("+ids+")");
			}
			if(search.length() > 0) {
				condition += search.toString();
			}
			//总数
			int totalCount = service.getBuyStockCount(condition);
			int countPerPage = 10;
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List buyStockList = service.getBuyStockList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = buyStockList.listIterator();
			while(iter.hasNext()){
				BuyStockBean stockBean = (BuyStockBean)iter.next();

				//产品类别
				String productType = "";
				/*
				String parentId1 = "";
				String parentId2 = "";
				ArrayList typeKeyList = service.getFieldList(
						"p.parent_id1", 
						"buy_stock_product bsp, product p" , 
						"bsp.product_id = p.id and bsp.buy_stock_id ="+stockBean.getId(),
						-1, -1, "p.parent_id1", null, "String");
				Iterator typeKeyIter = typeKeyList.listIterator();
				while(typeKeyIter.hasNext()){
					String key = (String) typeKeyIter.next();
					parentId1 += (key == null ? "" : key + ",");
				}
				typeKeyList = service.getFieldList(
						"p.parent_id2", 
						"buy_stock_product bsp, product p" , 
						"bsp.product_id = p.id and bsp.buy_stock_id ="+stockBean.getId(),
						-1, -1, "p.parent_id2", null, "String");
				typeKeyIter = typeKeyList.listIterator();
				while(typeKeyIter.hasNext()){
					String key = (String) typeKeyIter.next();
					parentId2 += (key == null ? "" : key + ",");
				}
				if (parentId1 != null && parentId1.length() > 0) {
					parentId1 = parentId1.substring(0, parentId1.length() -1);
				} else {
					parentId1 = "-1";
				}
				if (parentId2 != null && parentId2.length() > 0) {
					parentId2 = parentId2.substring(0, parentId2.length() -1);
				} else {
					parentId2 = "-1";
				}
				String productLineSql = "pl.id = plc.product_line_id and ((plc.catalog_id in (" + parentId1 
				+ ") and plc.catalog_type = 1) or (plc.catalog_type = 2 and plc.catalog_id "
				+ " in (" + parentId2 + ")))";
				typeKeyList = service.getFieldList(
						"pl.name", 
						"product_line pl, product_line_catalog plc", 
						productLineSql, -1, -1, "pl.id", "pl.id", "String");
				typeKeyIter = typeKeyList.listIterator();
				while(typeKeyIter.hasNext()){
					String key = (String) typeKeyIter.next();
					productType += (key == null ? "" : key + ", ");
				}
				if (productType != null && productType.length() > 0) {
					productType = productType.substring(0, productType.length() - 2);
				} else {
					productType = "无";
				}
				*/
				stockBean.setProductType(productType);

				String proxyName = wareService.getString("ssi.name", "buy_stock_product bsp, supplier_standard_info ssi", 
						"ssi.status = 1 and bsp.product_proxy_id = ssi.id and bsp.buy_stock_id = "+stockBean.getId());
				if (proxyName == null || proxyName.length() == 0) {
					proxyName = "无";
				}
				stockBean.setProxyName(proxyName);

				//操作人
				voUser creatUser = admiService.getAdmin(stockBean.getCreateUserId());
				voUser auditingUser =admiService.getAdmin(stockBean.getAuditingUserId());
				stockBean.setCreatUser(creatUser);
				stockBean.setAuditingUser(auditingUser);
			}

			if(stockId!=0){
				if(service.getBuyStock("id="+stockId)==null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				BuyStockBean stock = service.getBuyStock("id="+stockId);
				condition = "buy_stock_id = "+stockId;
				List buyStockProductList = service.getBuyStockProductList(condition, -1, -1, "id desc");
				iter = buyStockProductList.listIterator();
				while (iter.hasNext()) {
					BuyStockProductBean bpp = (BuyStockProductBean) iter.next();
					voProduct product = wareService.getProduct(bpp.getProductId());
					if(product == null){
						
					} else {
						product.setPsList(stockService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
						//已入库量
						int stockinCount = 0;
						List bispList = service.getBuyStockinProductList("buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+stock.getId()+
								" and (status = "+BuyStockinBean.STATUS4+" or status = "+BuyStockinBean.STATUS6+"))" +
								" and product_id = "+product.getId(), -1, -1, null);
						Iterator bispIterator = bispList.listIterator();
						while(bispIterator.hasNext()){
							BuyStockinProductBean bisp = (BuyStockinProductBean)bispIterator.next();
							stockinCount += bisp.getStockInCount();
						}
						String proLineName = "";
						
						condition = "pl.id = plc.product_line_id and ("
							+ "(plc.catalog_type = 1 and plc.catalog_id = " + product.getParentId1() + ") or "
							+ "(plc.catalog_type = 2 and plc.catalog_id = " + product.getParentId2() + "))";
						List productLineList = service.getFieldList("pl.name", "product_line pl,product_line_catalog plc", 
								condition, -1, -1, "pl.id", "pl.id", "String");
						Iterator proLineIter = productLineList.listIterator();
						while (proLineIter.hasNext()) {
							String key = (String) proLineIter.next();
							proLineName += (key == null ? "" : key + ", ");
						}
						if (proLineName != null && proLineName.length() > 0) {
							proLineName = proLineName.substring(0, proLineName.length() - 2);
						} else {
							proLineName = "无";
						}
						bpp.setStockinCount(stockinCount);
						bpp.setProduct(product);
						bpp.setProductLineName(proLineName);
					}
				}
				String proxyName = wareService.getString("ssi.name", "buy_stock_product bsp, supplier_standard_info ssi", 
						"ssi.status = 1 and bsp.product_proxy_id = ssi.id and bsp.buy_stock_id = "+stockId);
				if (proxyName == null || proxyName.length() == 0) {
					proxyName = "无";
				}
				request.setAttribute("buyStockProductList", buyStockProductList);
				request.setAttribute("proxyName", proxyName);
				request.setAttribute("stock", stock);

			}
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			param.append("isBTwoC=" + isBTwoC + "&");
			paging.setPrefixUrl("transformBuyStockList.jsp" + (param.length() > 0 ? "?" + param.substring(0, param.length()-1) : ""));
			request.setAttribute("buyStockList", buyStockList);
			request.setAttribute("paging", paging);
			request.setAttribute("isBTwoC", isBTwoC);
		}finally{
			dbOp_slave.release();
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：添加预计到货表（旧）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void addBuyStock(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		synchronized(buyStockLock){
			int orderId = StringUtil.StringToId(request.getParameter("orderId"));
			int area = StringUtil.StringToId(request.getParameter("area"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			WareService wareService = new WareService(service.getDbOp());
			try {
				BuyOrderBean order = service.getBuyOrder("id=" + orderId);
				if(order == null){
					request.setAttribute("tip", "没有这个采购订单，添加失败！");
					request.setAttribute("result", "failure");
					return;
				}
				
				if (!service.updateBuyOrder("transform_count = transform_count + 1" , "id = " + orderId)) {
					request.setAttribute("tip", "更新转换预计到货表次数失败！");
					request.setAttribute("result", "failure");
					return;
				}
				
				service.getDbOp().startTransaction();
				int id = service.getNumber("id", "buy_stock", "max", "id > 0") + 1;
				BuyStockBean stock = new BuyStockBean();
				stock.setCreateDatetime(DateUtil.getNow());
				stock.setName(order.getName() + "进货");
				stock.setArea(area);
				stock.setId(id);
				stock.setRemark("");
				stock.setDeadline(DateUtil.getNow());
				stock.setConfirmDatetime(DateUtil.getNow());
				stock.setCreateUserId(user.getId());
				stock.setBuyOrderId(orderId);
				if (!service.addBuyStock(stock)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					return;
				}

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(id);
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("新建预计到货表操作：" + stock.getCode());
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
				service.addBuyAdminHistory(log);

				request.setAttribute("stockId", String.valueOf(id));
				request.setAttribute("orderId", String.valueOf(orderId));
				List buyOrderProductList = service.getBuyOrderProductList("buy_order_id=" + orderId, -1, -1, "id desc");
				Iterator iter = buyOrderProductList.listIterator();
				while(iter.hasNext()){
					BuyOrderProductBean bpp = (BuyOrderProductBean)iter.next();
					BuyStockProductBean bsp = new BuyStockProductBean();
					bsp.setBuyCount(bpp.getOrderCountBJ());
					bsp.setBuyStockId(id);
					bsp.setCreateDatetime(stock.getCreateDatetime());
					bsp.setProductId(bpp.getProductId());
					bsp.setProductProxyId(bpp.getProductProxyId());
					bsp.setPurchasePrice(Double.valueOf(String.valueOf(bpp.getPurchasePrice())).floatValue());
					bsp.setRemark("");
					bsp.setStatus(BuyOrderProductBean.UNDEAL);
					service.addBuyStockProduct(bsp);

					//log记录
					log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(id);
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("预计到货表操作：" + stock.getCode() + ",添加了进货商品");
					log.setType(BuyAdminHistoryBean.TYPE_ADD);
					service.addBuyAdminHistory(log);
				}

				service.getDbOp().commitTransaction();
			} finally {
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：查看预计到货表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void buyStock(HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		int countPerPage = 50;
		int stockId = StringUtil.StringToId(request.getParameter("stockId"));
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		WareService wareService = new WareService(dbOp_slave);
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		try {
			BuyStockBean bean = service.getBuyStock("id = " + stockId);
			if(bean == null){
				request.setAttribute("tip", "没有这个预计到货表！");
				request.setAttribute("result", "failure");
				return;
			}

			//相关的入库记录
			String condition = "buy_stock_id = " + bean.getId();
			//总数
			int totalCount = service.getBuyStockProductCount(condition);
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			paging.setPrefixUrl("buyStock.jsp?stockId=" + stockId);
			ArrayList buyStockProductList = service.getBuyStockProductList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = buyStockProductList.listIterator();
			int proxyId = 0;
			float totalPurchasePrice = 0;
			ArrayList errorProductList = new ArrayList();
			while (iter.hasNext()) {
				BuyStockProductBean bpp = (BuyStockProductBean) iter.next();
				voProduct product = wareService.getProduct(bpp.getProductId());
				if(product == null){

				} else {
					bpp.setProduct(product);
					product.setPsList(stockService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
				}
				totalPurchasePrice += bpp.getBuyCount() * bpp.getPurchasePrice();

				//已入库量
				int stockinCount = 0;
				List bispList = service.getBuyStockinProductList("buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+bean.getId()+
						" and (status = "+BuyStockinBean.STATUS4+" or status = "+BuyStockinBean.STATUS6+") and  auditing_user_id > 0)" +
						" and product_id = "+product.getId(), -1, -1, null);
				Iterator bispIterator = bispList.listIterator();
				while(bispIterator.hasNext()){
					BuyStockinProductBean bisp = (BuyStockinProductBean)bispIterator.next();
					stockinCount += bisp.getStockInCount();
				}
				bpp.setStockinCount(stockinCount);

				proxyId = bpp.getProductProxyId();
				
				if(product.getIsPackage()==1){
                	errorProductList.add(Integer.valueOf(product.getId()));
                }
				//产品线名称
				voProductLine pl = null;
				voProductLineCatalog plc = null;
				List list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId1());
				if(list2.size()>0){
					plc = (voProductLineCatalog)list2.get(0);
					pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
					bpp.setProductLineName(pl.getName());
				}
				if(StringUtil.convertNull(bpp.getProductLineName()).equals("")){
					list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId2());
					if(list2.size()>0){
						plc = (voProductLineCatalog)list2.get(0);
						pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
						bpp.setProductLineName(pl.getName());
					}
				}
				if(StringUtil.convertNull(bpp.getProductLineName()).equals("")){
					bpp.setProductLineName("无");
				}
			}

			List proxyList = wareService.getSelects("supplier_standard_info", "where status = 1 order by id");

			request.setAttribute("totalPurchasePrice", String.valueOf(StringUtil.formatFloat(totalPurchasePrice)));
			request.setAttribute("paging", paging);
			request.setAttribute("bean", bean);
			request.setAttribute("buyStockProductList", buyStockProductList);
			request.setAttribute("proxyList", proxyList);
			request.setAttribute("proxyId", Integer.valueOf(proxyId));
			request.setAttribute("errorProductList", errorProductList);

			//来源采购订单、采购计划编号、ID
			BuyOrderBean order = service.getBuyOrder("id="+bean.getBuyOrderId());
			double taxPoint = 0;
			if(order != null){
				taxPoint = order.getTaxPoint();
				request.setAttribute("buyOrderCode", order.getCode());
				request.setAttribute("orderId", Integer.valueOf(order.getId()));

			}
			
			List list = service.getBuyStockinList("buy_stock_id="+bean.getId()+" and status="+BuyStockinBean.STATUS6, -1, -1, null);
			if(list.size() != 0){
				request.setAttribute("editPriceCheck", Boolean.valueOf(true));
			}

			List buyStockinList = service.getBuyStockinList("buy_stock_id="+bean.getId()+" and status != "+BuyStockinBean.STATUS8, -1, -1, "id desc");
			request.setAttribute("buyStockinList", buyStockinList);
			request.setAttribute("taxPoint", Double.valueOf(taxPoint));
		} finally {
			dbOp_slave.release();
		}
	}

	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-6-16
	 * 
	 * 说明：转换 采购订单->预计到货表
	 * 
	 * @param request
	 * @param response
	 */
	public void transformToBuyStock(HttpServletRequest request,
			HttpServletResponse response){


		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(112);
		if(!viewAll){ 
			request.setAttribute("tip", "你无权转换预计到货表！");
			request.setAttribute("result", "failure");
			return;
		}

		synchronized(buyStockLock){
			int orderId = StringUtil.toInt(request.getParameter("orderId"));
			String[] buyOrderProductIds = request.getParameterValues("buyOrderProductId");
			int area = StringUtil.toInt(request.getParameter("area"));
			if(area != 1 && area != 3 && area != 4){
				request.setAttribute("tip", "地区选择不正确");
				request.setAttribute("result", "failure");
				return;
			}

			WareService wareService = new WareService();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try {
				wareService.getDbOp().startTransaction();
				BuyOrderBean order = service.getBuyOrder("id=" + orderId);
				if(order == null){    
					request.setAttribute("tip", "没有这个采购订单");
					request.setAttribute("result", "failure");
					wareService.getDbOp().rollbackTransaction();
					return;
				}
				
				if (!service.updateBuyOrder("transform_count = transform_count + 1" , "id = " + orderId)) {
					request.setAttribute("tip", "更新转换预计到货表次数失败！");
					request.setAttribute("result", "failure");
					wareService.getDbOp().rollbackTransaction();
					return;
				}
				
				//新预计到货表编号：CGJH20090601001
				String code = "J"+DateUtil.getNow().substring(0,10).replace("-", "");   
				service.getDbOp().startTransaction();
				int maxid = service.getNumber("id", "buy_stock", "max", "id > 0");
				BuyStockBean stock;
				stock = service.getBuyStock("code like '" + code + "%'");
				if(stock == null){
					//当日第一份进货单，编号最后三位 001
					code += "001";
				}else {
					//获取当日进货单编号最大值
					stock = service.getBuyStock("id =" + maxid); 
					String _code = stock.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-3));
					number++;
					code += String.format("%03d",new Object[]{new Integer(number)});
				}
				stock = new BuyStockBean();
				stock.setBuyOrderId(orderId);
				stock.setCreateDatetime(DateUtil.getNow());
				stock.setConfirmDatetime(DateUtil.getNow());
				stock.setStatus(BuyStockBean.STATUS0);
				stock.setCode(code);
				stock.setId(maxid+1);
				stock.setArea(area);
				stock.setRemark("");
				stock.setPortage(0);
				stock.setDeadline(DateUtil.formatDate(DateUtil.rollDate(3)));
				stock.setCreateUserId(user.getId());
				stock.setExpectArrivalDatetime(DateUtil.formatDate(DateUtil.rollDate(1)));
				stock.setExpressCode("");
				stock.setExpressCompany("");
				stock.setSupplierId(order.getProxyId());
				if (!service.addBuyStock(stock)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					wareService.getDbOp().rollbackTransaction();
					return;
				}

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stock.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("转换成预计到货表，来源采购订单：" + order.getCode());
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
				service.addBuyAdminHistory(log);

				for(int i=0;i<buyOrderProductIds.length;i++){
					String buyOrderProductId = buyOrderProductIds[i];
					BuyOrderProductBean bop = service.getBuyOrderProduct("id="+buyOrderProductId);
					BuyStockProductBean bsp = new BuyStockProductBean();
					voProduct product = wareService.getProduct(bop.getProductId());

					//预计进货量
					int stockCount = 0;
					String condition = "buy_stock_id in (select id from buy_stock where buy_order_id = "+orderId+" and status in ("+BuyStockBean.STATUS2 +
					","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+","+BuyStockBean.STATUS6+") and area = "+area+") and product_id = "+product.getId();
					List bspList = service.getBuyStockProductList(condition, -1, -1, null);
					Iterator bspIterator = bspList.listIterator();
					while(bspIterator.hasNext()){
						BuyStockProductBean bspBean = (BuyStockProductBean)bspIterator.next();
						stockCount += bspBean.getBuyCount();
					}
					if(area == 0){
						if (bop.getOrderCountBJ()==0 || bop.getOrderCountBJ()-stockCount <= 0) {
							request.setAttribute("tip", "所选地区存在不需进货的产品");
							request.setAttribute("result", "failure");
							wareService.getDbOp().rollbackTransaction();
							return;
						}
						bsp.setBuyCount(bop.getOrderCountBJ()-stockCount); 
					}else{
						if (bop.getOrderCountGD()==0) {
							request.setAttribute("tip", "选择的对应地区的产品中存在进货数为0的产品");
							request.setAttribute("result", "failure");
							wareService.getDbOp().rollbackTransaction();
							return;
						}
						if(bop.getOrderCountGD()-stockCount <= 0){
							request.setAttribute("tip", "选择的对应地区的产品预计采购量已超过已入库量");
							request.setAttribute("result", "failure");
							wareService.getDbOp().rollbackTransaction();
							return;
						}
						bsp.setBuyCount(bop.getOrderCountGD()-stockCount); 
					}

					bsp.setId(service.getNumber("id", "buy_stock_product", "max", "id > 0")+1);
					bsp.setBuyStockId(stock.getId());
					bsp.setCreateDatetime(stock.getCreateDatetime());
					bsp.setProductId(bop.getProductId());
					bsp.setProductProxyId(bop.getProductProxyId());
					bsp.setPurchasePrice(Double.valueOf(String.valueOf(bop.getPurchasePrice())).floatValue());
					bsp.setRemark("");
					bsp.setStatus(BuyOrderProductBean.UNDEAL);

					service.addBuyStockProduct(bsp);

					//log记录
					log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(stock.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("添加预计到货表商品["+product.getCode()+"]");
					log.setType(BuyAdminHistoryBean.TYPE_ADD);
					service.addBuyAdminHistory(log);
				}

				wareService.getDbOp().commitTransaction();

				request.setAttribute("stockId", Integer.valueOf(stock.getId()));
			} finally {
				wareService.releaseAll();
			}
		}


	}
	
	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-6-16
	 * 
	 * 说明：转换 采购订单->预计到货表 (对勾选转换成预计到货表)
	 * 
	 * @param request
	 * @param response
	 */
	public void transformToBuyStock2(HttpServletRequest request,
			HttpServletResponse response){


		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(112);
		if(!viewAll){ 
			request.setAttribute("tip", "你无权转换预计到货表！");
			request.setAttribute("result", "failure");
			return;
		}

		synchronized(buyStockLock){
			int orderId = StringUtil.toInt(request.getParameter("orderId"));
			String[] buyOrderProductIds = StringUtil.convertNull(request.getParameter("ids")).split(",");

			WareService wareService = new WareService();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try {
				wareService.getDbOp().startTransaction();
				BuyOrderBean order = service.getBuyOrder("id=" + orderId);
				if(order == null){    
					request.setAttribute("tip", "没有这个采购订单");
					request.setAttribute("result", "failure");
					wareService.getDbOp().rollbackTransaction();
					return;
				}
				
				if (!service.updateBuyOrder("transform_count = transform_count + 1" , "id = " + orderId)) {
					request.setAttribute("tip", "更新转换预计到货表次数失败！");
					request.setAttribute("result", "failure");
					wareService.getDbOp().rollbackTransaction();
					return;
				}

				//新预计到货表编号：CGJH20090601001
				String code = "J"+DateUtil.getNow().substring(0,10).replace("-", "");   
				service.getDbOp().startTransaction();
				int maxid = service.getNumber("id", "buy_stock", "max", "id > 0");
				BuyStockBean stock;
				stock = service.getBuyStock("code like '" + code + "%'");
				if(stock == null){
					//当日第一份进货单，编号最后三位 001
					code += "001";
				}else {
					//获取当日进货单编号最大值
					stock = service.getBuyStock("id =" + maxid); 
					String _code = stock.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-3));
					number++;
					code += String.format("%03d",new Object[]{new Integer(number)});
				}
				stock = new BuyStockBean();
				stock.setBuyOrderId(orderId);
				stock.setCreateDatetime(DateUtil.getNow());
				stock.setConfirmDatetime(DateUtil.getNow());
				stock.setStatus(BuyStockBean.STATUS0);
				stock.setCode(code);
				stock.setId(maxid+1);
				stock.setArea(0);
				stock.setRemark("");
				stock.setPortage(0);
				stock.setDeadline(DateUtil.formatDate(DateUtil.rollDate(3)));
				stock.setCreateUserId(user.getId());
				stock.setExpectArrivalDatetime(DateUtil.formatDate(DateUtil.rollDate(1)));
				stock.setExpressCode("");
				stock.setExpressCompany("");
				stock.setSupplierId(order.getProxyId());//供应商id
				if (!service.addBuyStock(stock)) {	
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					wareService.getDbOp().rollbackTransaction();
					return;
				}

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stock.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("转换成预计到货表，来源采购订单：" + order.getCode());
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
				service.addBuyAdminHistory(log);

				for(int i=0;i<buyOrderProductIds.length;i++){
					String buyOrderProductId = buyOrderProductIds[i];
					BuyOrderProductBean bop = service.getBuyOrderProduct("id="+buyOrderProductId);
					BuyStockProductBean bsp = new BuyStockProductBean();
					voProduct product = wareService.getProduct(bop.getProductId());

					//预计进货量
					int stockCount = 0;
					String condition = "buy_stock_id in (select id from buy_stock where buy_order_id = "+orderId+" and status in ("+BuyStockBean.STATUS2 +
					","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+","+BuyStockBean.STATUS6+") and area = 3) and product_id = "+product.getId();
					List bspList = service.getBuyStockProductList(condition, -1, -1, null);
					Iterator bspIterator = bspList.listIterator();
					while(bspIterator.hasNext()){
						BuyStockProductBean bspBean = (BuyStockProductBean)bspIterator.next();
						stockCount += bspBean.getBuyCount();
					}

					if (bop.getOrderCountGD()==0) {
						request.setAttribute("tip", "选择的对应地区的产品中存在进货数为0的产品");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return;
					}
					if(bop.getOrderCountGD()-stockCount <= 0){
						request.setAttribute("tip", "选择的对应地区的产品预计采购量已超过已入库量");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return;
					}
					bsp.setBuyCount(bop.getOrderCountGD()-stockCount); 


					bsp.setId(service.getNumber("id", "buy_stock_product", "max", "id > 0")+1);
					bsp.setBuyStockId(stock.getId());
					bsp.setCreateDatetime(stock.getCreateDatetime());
					bsp.setProductId(bop.getProductId());
					bsp.setProductProxyId(bop.getProductProxyId());
					bsp.setPurchasePrice(Double.valueOf(String.valueOf(bop.getPurchasePrice())).floatValue());
					bsp.setRemark("");
					bsp.setStatus(BuyOrderProductBean.UNDEAL);

					service.addBuyStockProduct(bsp);

					//log记录
					log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(stock.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("添加预计到货表商品["+product.getCode()+"]");
					log.setType(BuyAdminHistoryBean.TYPE_ADD);
					service.addBuyAdminHistory(log);
				}

				wareService.getDbOp().commitTransaction();

				request.setAttribute("stockId", Integer.valueOf(stock.getId()));
			} finally {
				service.releaseAll();
			}
		}


	}


	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：添加预计到货表产品（旧）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void addBuyStockProduct(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean bianji = group.isFlag(168);
		
		synchronized(buyStockLock){
			String productCode = StringUtil.dealParam(request.getParameter("productCode"));
			if (StringUtil.convertNull(productCode).equals("")) {
				request.setAttribute("tip", "请输入产品编号！");
				request.setAttribute("result", "failure");
				return;
			}
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			int buyCount = StringUtil.StringToId(request.getParameter("buyCount"));
			if (buyCount == 0) {
				request.setAttribute("tip", "请输入入库量！");
				request.setAttribute("result", "failure");
				return;
			}

			WareService wareService = new WareService();
			voProduct product = wareService.getProduct(productCode);
			wareService.releaseAll();

			if (product == null) {
				request.setAttribute("tip", "不存在这个编号的产品！");
				request.setAttribute("result", "failure");
				return;
			}

			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			try {
				BuyStockBean bean = service.getBuyStock("id = " + stockId);
				if(!bianji&&bean.getCreateUserId()!=user.getId()){
					request.setAttribute("tip", "你无权添加产品！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS6){
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}
				if (service.getBuyStockProduct("buy_stock_id = " + stockId + " and product_id = " + product.getId()) != null) {
					request.setAttribute("tip", "该产品已经添加，直接修改即可，不用重复添加！");
					request.setAttribute("result", "failure");
					return;
				}

				//开始事务
				service.getDbOp().startTransaction();
				//添加计划商品
				int bppId = service.getNumber("id", "buy_stock_product", "max", "id > 0") + 1;
				BuyStockProductBean bpp = new BuyStockProductBean();
				bpp.setCreateDatetime(DateUtil.getNow());
				bpp.setConfirmDatetime(null);
				bpp.setId(bppId);
				bpp.setBuyStockId(stockId);
				bpp.setProductId(product.getId());
				bpp.setRemark("");
				bpp.setStatus(BuyStockProductBean.UNDEAL);
				bpp.setBuyCount(buyCount);
				if (!service.addBuyStockProduct(bpp)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stockId);
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("预计到货表操作：" + bean.getCode() + ",添加了进货商品[" + product.getCode() + "]");
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
				service.addBuyAdminHistory(log);

				service.getDbOp().commitTransaction();
			} finally {
				service.releaseAll();
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：修改预计到货表信息
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void editBuyStock(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean bianji = group.isFlag(168);

		synchronized(buyStockLock){
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			String remark = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("remark")));
			int stockArea = StringUtil.StringToId(request.getParameter("stockArea"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			try {
				BuyStockBean bean = service.getBuyStock("id = " + stockId);
				if(bean == null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(!bianji&&bean.getCreateUserId()!=user.getId()){
					request.setAttribute("tip", "你无权修改这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS6){
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}
				if(stockArea == 0){
					request.setAttribute("tip", "地区不能为空！");
					request.setAttribute("result", "failure");
					return;
				}

				//开始事务
				service.getDbOp().startTransaction();

				StringBuilder buf = new StringBuilder();
				buf.append("remark='");
				buf.append(remark);
				buf.append(DateUtil.getNow().substring(10, 19));
				if(bean.status == BuyStockBean.STATUS0){
					buf.append("',status=");
					buf.append(BuyStockBean.STATUS1);
				}else{
					buf.append("'");
				}
				buf.append(",area = "+stockArea);
				service.updateBuyStock(buf.toString(), "id = " + bean.getId());

				service.getDbOp().commitTransaction();
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：修改预计到货表商品信息
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void editBuyStockProduct(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean bianji = group.isFlag(168);

		synchronized(buyStockLock){
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			String expectArrivalDatetime = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("expectArrivalDatetime")));
			String expressCompany = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("expressCompany")));
			String expressCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("expressCode")));
			int stockArea = StringUtil.StringToId(request.getParameter("stockArea"));
			IStockService service = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, null);
			WareService wareService = new WareService(service.getDbOp());
			try {
				BuyStockBean bean = service.getBuyStock("id = " + stockId);
				if(bean == null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(!bianji&&bean.getCreateUserId()!=user.getId()){
					request.setAttribute("tip", "你无权修改这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS6){
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}
				if(stockArea == 0){
					request.setAttribute("tip", "地区	不能为空！");
					request.setAttribute("result", "failure");
					return;
				}
				
				//相关的产品
				int buyCount = 0;
				// add 2011-05-10 lining 修改信息，不允许修改供应商id
				//int productProxyId = 0;
//				float purchasePrice = 0;
				float portage = StringUtil.toFloat(request.getParameter("portage"));
				StringBuilder buf = new StringBuilder();
				//相关的产品
				String condition = "buy_stock_id = " + bean.getId();
				int totalCount = service.getBuyStockProductCount(condition);
				int countPerPage = 50;
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				paging.setPrefixUrl("buyStock.jsp?stockId=" + stockId);
				ArrayList buyStockProductList = service.getBuyStockProductList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
				Iterator itr = buyStockProductList.listIterator();
				//开始事务
				service.getDbOp().startTransaction();
				voProduct product = null;
				BuyStockProductBean bpp = null;
				while (itr.hasNext()) {
					bpp = (BuyStockProductBean) itr.next();
					product = wareService.getProduct(bpp.getProductId());
					if (product == null) {
						continue;
					}

					buyCount = StringUtil.StringToId(request.getParameter("buyCount" + product.getId()));
//					purchasePrice = StringUtil.toFloat(request.getParameter("purchasePrice" + product.getId()));
					//没做改变
					if (bpp.getBuyCount() == buyCount) {// && bpp.getProductProxyId() == productProxyId) {
						continue;
					}
					if (buyCount == 0) {
						request.setAttribute("tip", "请输入正确的计划量！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
					//预计进货量默认值
					int stockCount = 0;
					condition = "buy_stock_id in (select id from buy_stock where buy_order_id = "+bean.getBuyOrderId()+" and status in ("+BuyStockBean.STATUS2 +
					","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+","+BuyStockBean.STATUS6+") and area = "+bean.getArea()+") and product_id = "+product.getId();
					List bspList = service.getBuyStockProductList(condition, -1, -1, null);
					Iterator bspIterator = bspList.listIterator();
					while(bspIterator.hasNext()){
						BuyStockProductBean bspBean = (BuyStockProductBean)bspIterator.next();
						stockCount += bspBean.getBuyCount();
					}
					BuyOrderProductBean buyOrderProductBean = service.getBuyOrderProduct("buy_order_id="+bean.getBuyOrderId()+" and product_id="+product.getId());
					if(buyCount>buyOrderProductBean.getOrderCountGD()-stockCount && (bean.getStatus() != BuyStockBean.STATUS3&&bean.getStatus() != BuyStockBean.STATUS2)){
						request.setAttribute("tip", "商品"+product.getCode()+"输入的预计进货量不能大于默认值");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
//					if(bean.getArea()==3){
//						if(buyCount>buyOrderProductBean.getOrderCountBJ()-stockCount && (bean.getStatus() != BuyStockBean.STATUS3&&bean.getStatus() != BuyStockBean.STATUS2)){
//							request.setAttribute("tip", "商品"+product.getCode()+"输入的预计进货量不能大于默认值");
//							request.setAttribute("result", "failure");
//							service.getDbOp().rollbackTransaction();
//							return;
//						}
//					}else if(bean.getArea()==1){
//						if(buyCount>buyOrderProductBean.getOrderCountGD()-stockCount && (bean.getStatus() != BuyStockBean.STATUS3&&bean.getStatus() != BuyStockBean.STATUS2)){
//							request.setAttribute("tip", "商品"+product.getCode()+"输入的预计进货量不能大于默认值");
//							request.setAttribute("result", "failure");
//							service.getDbOp().rollbackTransaction();
//							return;
//						}
//					}
					//add 2011-05-10 lining
					/*if (productProxyId == 0){
						request.setAttribute("tip", "请选择正确的代理商！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}*/
//					if (purchasePrice < 0){
//						request.setAttribute("tip", "请输入正确的价格！");
//						request.setAttribute("result", "failure");
//						service.getDbOp().rollbackTransaction();
//						return;
//					}
					
					buf.delete(0, buf.length());
					buf.append("buy_count=");
					buf.append(buyCount);
//					buf.append(",purchase_price=");
//					buf.append(purchasePrice);

					//入库记录
					service.updateBuyStockProduct(buf.toString(), "id = " + bpp.getId());
					
					//更新入库单本次进货价
//					List bsipList = service.getBuyStockinProductList("buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+bean.getId()+") and product_id ="+bpp.getProductId(),-1,-1,null);
//					if( bsipList != null){
//						service.updateBuyStockinProduct("price3="+purchasePrice, "buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+bean.getId()+") and product_id ="+bpp.getProductId());
//					}
					
					String proxyNameOld = wareService.getString("name", "supplier_standard_info", "id="+bpp.getProductProxyId());
					if(proxyNameOld==null){
						proxyNameOld = "无";
					}
					String proxyNameNew = proxyNameOld;
					String remark = "修改了预计到货表商品["+product.getCode()+"]的预计进货量("+bpp.getBuyCount()+"-"+buyCount+")  "+
									"  代理商("+proxyNameOld+"-"+proxyNameNew+")";
					int logId = service.getNumber("id", "buy_admin_history", "max", "id > 0") + 1;
					BuyAdminHistoryBean log = new BuyAdminHistoryBean();
					log.setLogId(logId);
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(stockId);
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark(remark);
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					service.addBuyAdminHistory(log);
				}
				if (portage < 0){
					request.setAttribute("tip", "请输入正确的运费！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				if (expressCode.length() > 15){
					request.setAttribute("tip", "物流单号长度不能超过15位！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				String remark = "修改了预计到货表的";
				StringBuilder buffer = new StringBuilder();

				buffer.append("area=" + stockArea+",");
				if(bean.getPortage()!=portage){
					buffer.append("portage=" + portage+",");
					remark = remark+"预计运费("+bean.getPortage()+"-"+portage+")  ";
				}
				if(!bean.getExpectArrivalDatetime().substring(0, 10).equals(expectArrivalDatetime)){
					buffer.append("expect_arrival_datetime='" + expectArrivalDatetime+"',");
					remark = remark+"预计到货时间("+bean.getExpectArrivalDatetime().substring(0, 10)+"-"+expectArrivalDatetime+")  ";
				}
				if(!bean.getExpressCompany().equals(expressCompany)){
					buffer.append("express_company='" + expressCompany+"',");
					if(bean.getExpressCompany().equals("")){
						bean.setExpressCompany("无");
					}
					if(expressCompany.equals("")){
						expressCompany = "无";
					}
					remark = remark+"物流公司("+bean.getExpressCompany()+"-"+expressCompany+")  ";
				}
				if(!bean.getExpressCode().equals(expressCode)){
					buffer.append("express_code='" + expressCode+"',");
					if(bean.getExpressCode().equals("")){
						bean.setExpressCode("无");
					}
					if(expressCode.equals("")){
						expressCode = "无";
					}
					remark = remark+"物流单号("+bean.getExpressCode()+"-"+expressCode+")  ";
				}
				if(buffer.length()>0){
					String set = buffer.toString();
					service.updateBuyStock(set.endsWith(",")?set.substring(0, set.length()-1):set, "id="+stockId);
					
					BuyAdminHistoryBean log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(stockId);
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark(remark);
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					service.addBuyAdminHistory(log);
				}
				if(bean.getStatus() == BuyStockBean.STATUS0){
					service.updateBuyStock("status="+BuyStockBean.STATUS1, "id="+bean.getId());
				}

				//提交事务
				service.getDbOp().commitTransaction();
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 说明：修改采购计划的商品的批发价(旧)
	 */
	public void editBuyStockPrice(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		synchronized(buyStockLock){
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			IStockService service = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, null);
			WareService wareService = new WareService(service.getDbOp());
			try {
				StockOperationBean bean = service.getStockOperation("id = "
						+ stockId + " and type = " + StockOperationBean.BUY_STOCKIN);
				if (bean.getStatus() == StockOperationBean.STATUS1) {
					request.setAttribute("tip", "该操作还未完成，不能修改价格！");
					request.setAttribute("result", "failure");
					return;
				}

				String[] productIds = request.getParameterValues("productId");
				String[] prices = request.getParameterValues("price3");
				String[] orinames = request.getParameterValues("oriname");
				String[] proxyIds = request.getParameterValues("proxyId");
				boolean editPrice = false;
				if(productIds != null && prices != null){
					service.getDbOp().startTransaction();
					for(int i=0; i<productIds.length && i<prices.length; i++){
						int productId = StringUtil.StringToId(productIds[i]);
						float price3 = StringUtil.toFloat(prices[i]);
						String oriname = StringUtil.dealParam(orinames[i]);
						int proxyId = StringUtil.StringToId(proxyIds[i]);
						if(productId <= 0){
							request.setAttribute("tip", "产品ID不正确！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						voProduct product = wareService.getProduct(productId);
						if(!editPrice){
							if(price3 != product.getPrice3()){
								editPrice = true;
							}
						}
						if(product == null){
							request.setAttribute("tip", "产品不存在！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						} 
						if(price3 < 0){
							request.setAttribute("tip", "产品批发价不正确！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						if(proxyId <= 0){
							request.setAttribute("tip", "产品代理商设置不正确！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						if(oriname == null || oriname.length() == 0){
							request.setAttribute("tip", "产品原名称设置不正确！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return;
						}
						StockProductHistoryBean stockProduct = service.getStockProductHistory("oper_id=" + bean.getId() + " and product_id=" + productId);
						if(stockProduct == null){
							stockProduct = new StockProductHistoryBean();
							stockProduct.setOperId(bean.getId());
							stockProduct.setProductId(productId);
							stockProduct.setProductCode(product.getCode());
							stockProduct.setOriname(oriname);
							stockProduct.setPrice3(price3);
							stockProduct.setProxyId(proxyId);
							service.addStockProductHistory(stockProduct);
						} else {
							String set = "oriname='" + oriname + "', proxy_id=" + proxyId + ", price3=" + price3;
							service.updateStockProductHistory(set, "id=" + stockProduct.getId());
						}
					}
					//提交事务
					service.getDbOp().commitTransaction();
				}
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-2-20
	 * 
	 * 说明：确认预计到货表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void completeBuyStock(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(113);
		if(!viewAll){ 
			request.setAttribute("tip", "你无权确认这个预计到货表！");
			request.setAttribute("result", "failure");
			return;
		}

		synchronized(buyStockLock){
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			WareService wareService = new WareService(service.getDbOp());
			try {
				BuyStockBean bean = service.getBuyStock("id = " + stockId);
				if(bean == null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS6){
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getArea() == 0){
					request.setAttribute("tip", "地区不能为空！");
					request.setAttribute("result", "failure");
					return;
				}
				String condition = "buy_stock_id = " + bean.getId();
				ArrayList buyStockProductList = service.getBuyStockProductList(condition, 0, -1, "id");
				Iterator itr = buyStockProductList.iterator();
				if(buyStockProductList.size() == 0){
					request.setAttribute("tip", "没有需要执行的数据");
					request.setAttribute("result", "failure");
					return;
				}
				
				//开始事务
				service.getDbOp().startTransaction();
				BuyStockProductBean bpp = null;
				voProduct product = null;
				int count = 0;
				int stockCount = 0;
				ArrayList errorProductList = new ArrayList();
				while (itr.hasNext()) {
					count++;
					bpp = (BuyStockProductBean) itr.next();
					product = wareService.getProduct(bpp.getProductId());
					if(product.getIsPackage()==1){
			        	request.setAttribute("tip", "订单中不能包含套装产品！");
			            request.setAttribute("result", "failure");
			            return;
			        }
					
					condition = "buy_stock_id in (select id from buy_stock where buy_order_id = "+bean.getBuyOrderId()+" and status in ("+BuyStockBean.STATUS3 +
					","+BuyStockBean.STATUS6+","+BuyStockBean.STATUS2+") and area = "+bean.getArea()+") and product_id = "+product.getId();
					List bspList = service.getBuyStockProductList(condition, -1, -1, null);
					Iterator bspIterator = bspList.listIterator();
					while(bspIterator.hasNext()){
						BuyStockProductBean bspBean = (BuyStockProductBean)bspIterator.next();
						stockCount += bspBean.getBuyCount();
					}
					BuyOrderProductBean buyOrderProductBean = service.getBuyOrderProduct("buy_order_id="+bean.getBuyOrderId()+" and product_id="+product.getId());
					if(bean.area == 0){
						if(bpp.getBuyCount()>buyOrderProductBean.getOrderCountBJ()-stockCount){
							errorProductList.add(Integer.valueOf(product.getId()));
						}
					}else if(bean.getArea()==1){
						if(bpp.getBuyCount()>buyOrderProductBean.getOrderCountGD()-stockCount){
							errorProductList.add(Integer.valueOf(product.getId()));
						}
					}

				}
				if(errorProductList.size() != 0){
					request.setAttribute("errorProductList", errorProductList);
					request.setAttribute("tip", "存在不需进货的产品（超出需进货量或已完成进货），请参考与之关联的采购订单数据");
					request.setAttribute("result", "return");
					service.getDbOp().rollbackTransaction();
					return;
				}
				if (count == 0) {
					request.setAttribute("tip", "该操作没有任何变动，不能执行！");
					request.setAttribute("result", "failure");
					return;
				}

				service.updateBuyStock("status = " + BuyStockBean.STATUS2 + ", confirm_datetime = now(), auditing_user_id=" + user.getId(), "id = " + stockId);

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("确认预计到货表");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
				service.addBuyAdminHistory(log);


				//提交事务
				service.getDbOp().commitTransaction();
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2009-06-09
	 * 
	 * 说明：完成预计到货表（旧）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void finishBuyStock(HttpServletRequest request,
			HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		synchronized(buyStockLock){
			int userID = user.getId();
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			try{
				BuyStockBean stock = service.getBuyStock("id="+stockId);
				if(stock == null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(userID != stock.getCreateUserId() && userID != stock.getAuditingUserId()){
					request.setAttribute("tip", "对不起，您无权完成这个预计到货表");
					request.setAttribute("result", "failure");
					return;
				}

				service.updateBuyStock("status="+BuyStockBean.STATUS6, "id="+stockId);

				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stockId);
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("采购已完成");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
				service.addBuyAdminHistory(log);
			}finally{
				service.releaseAll();
			}
		}
	}

	/**
	 * 说明：删除预计到货表产品
	 */
	public void deleteBuyStockProduct(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean bianji = group.isFlag(168);
		
		synchronized(buyStockLock){
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			int productId = StringUtil.StringToId(request.getParameter("productId"));
			IStockService service = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, null);
			WareService wareService = new WareService(service.getDbOp());
			try {
				BuyStockBean bean = service.getBuyStock("id = " + stockId);
				if(bean == null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(!bianji&&bean.getCreateUserId()!=user.getId()){
					request.setAttribute("tip", "你无权删除这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockBean.STATUS2 || bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS6){
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}
				
				voProduct product = wareService.getProduct(productId);

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stockId);
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("修改预计到货表商品[" + product.getCode() + "]");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
				service.addBuyAdminHistory(log);

				service.deleteBuyStockProduct("buy_stock_id = " + stockId + " and product_id = " + productId);

				if(bean.getStatus() == BuyStockBean.STATUS0){
					service.updateBuyStock("status="+BuyStockBean.STATUS1, "id="+bean.getId());
				}
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 说明：删除预计到货表
	 */
	public void deleteBuyStock(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean bianji = group.isFlag(168);

		synchronized(buyStockLock){
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			try {
				BuyStockBean bean = service.getBuyStock("id = " + stockId);
				if(bean == null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(!bianji&&bean.getCreateUserId()!=user.getId()){
					request.setAttribute("tip", "你无权删除这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockBean.STATUS3 || bean.getStatus() == BuyStockBean.STATUS6){
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return;
				}
				if(bean.getStatus() == BuyStockBean.STATUS1 && service.getBuyStockProductCount("buy_stock_id="+bean.getId())>0){
					request.setAttribute("tip", "预计到货表中存在商品，不能删除！");
					request.setAttribute("result", "failure");
					return;
				}
				service.getDbOp().startTransaction();
				service.updateBuyStock("status = "+BuyStockBean.STATUS8, "id="+stockId);
				
				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stockId);
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("删除预计到货表");
				log.setType(BuyAdminHistoryBean.TYPE_DELETE);
				service.addBuyAdminHistory(log);

				service.getDbOp().commitTransaction();
			} finally {
				service.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-2-24
	 * 
	 * 说明：审核预计到货表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void confirmBuyStock(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean auditing = group.isFlag(58);

		if(!auditing){ 
			request.setAttribute("tip", "你无权审核这个预计到货表！");
			request.setAttribute("result", "failure");
			return;
		}
		
		synchronized(buyStockLock){
			int stockId = StringUtil.StringToId(request.getParameter("stockId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			WareService wareService = new WareService(service.getDbOp());
			try {
				BuyStockBean bean = service.getBuyStock("id = " + stockId);
				if(bean == null){
					request.setAttribute("tip", "没有这个预计到货表！");
					request.setAttribute("result", "failure");
					return;
				}
				if (bean.getStatus() != BuyStockBean.STATUS2 && bean.getStatus() != BuyStockBean.STATUS5) {
					request.setAttribute("tip", "该操作还未完成进货，不能审核！");
					request.setAttribute("result", "failure");
					return;
				}
				int mark = StringUtil.StringToId(request.getParameter("mark"));
				service.getDbOp().startTransaction();
				//都已经处理
				if (mark == 0) { // 审核未通过
					service.updateBuyStock("status = " + BuyStockBean.STATUS4, "id = " + stockId);
					service.updateBuyStockProduct("status=" + BuyStockProductBean.UNDEAL, "buy_stock_id=" + stockId);
					
					//log记录
					BuyAdminHistoryBean log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("审核未通过");
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					service.addBuyAdminHistory(log);
				} else {
					
					ArrayList buyStockProductList = service.getBuyStockProductList("buy_stock_id="+bean.getId(), -1, -1, null);
					Iterator iter = buyStockProductList.listIterator();
					if(buyStockProductList.size() == 0){
						request.setAttribute("tip", "没有需要执行的数据");
						request.setAttribute("result", "failure");
						return;
					}
					//预计进货量默认值
					int stockCount = 0;
					ArrayList errorProductList = new ArrayList();
					while(iter.hasNext()){
						BuyStockProductBean bop = (BuyStockProductBean)iter.next();
						voProduct product = wareService.getProduct(bop.getProductId());
						if(product.getIsPackage()==1){
				        	request.setAttribute("tip", "订单中不能包含套装产品！");
				            request.setAttribute("result", "failure");
				            return;
				        }
						String condition = "buy_stock_id in (select id from buy_stock where buy_order_id = "+bean.getBuyOrderId()+" and status in ("+BuyStockBean.STATUS2 +
					","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+","+BuyStockBean.STATUS6+") and area = "+bean.getArea()+") and product_id = "+product.getId();
						List bspList = service.getBuyStockProductList(condition, -1, -1, null);
						Iterator bspIterator = bspList.listIterator();
						while(bspIterator.hasNext()){
							BuyStockProductBean bspBean = (BuyStockProductBean)bspIterator.next();
							stockCount += bspBean.getBuyCount();
						}
						BuyOrderProductBean buyOrderProductBean = service.getBuyOrderProduct("buy_order_id="+bean.getBuyOrderId()+" and product_id="+product.getId());
						if(bean.area == 0){
							if(bop.getBuyCount()>buyOrderProductBean.getOrderCountBJ()-stockCount){
								errorProductList.add(Integer.valueOf(product.getId()));
							}
						}else{
							if(bop.getBuyCount()>buyOrderProductBean.getOrderCountGD()-stockCount){
								errorProductList.add(Integer.valueOf(product.getId()));
							}
						}
					}
					if(errorProductList.size() != 0){
						request.setAttribute("errorProductList", errorProductList);
						request.setAttribute("tip", "存在不需进货的产品（超出需进货量或已完成进货），请参考与之关联的采购订单数据");
						request.setAttribute("result", "return");
						service.getDbOp().rollbackTransaction();
						return;
					}
					service.updateBuyStock("status = " + BuyStockBean.STATUS3 + ", confirm_datetime = now(), auditing_user_id=" + user.getId(), "id = " + stockId);
					
					//log记录
					BuyAdminHistoryBean log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("审核通过");
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					service.addBuyAdminHistory(log);
					
				}
				service.getDbOp().commitTransaction();
			} finally {
				service.releaseAll();
			}
		}
	}


	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-2-26
	 * 
	 * 说明：打印预计到货表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void printBuyStock(HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(55);

		int stockId = StringUtil.StringToId(request.getParameter("stockId"));
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbOp_slave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		IStockService serviceSlave = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		WareService wareService = new WareService(dbOp_slave);
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, serviceSlave.getDbOp());
		try {

			BuyStockBean bean = service.getBuyStock("id = " + stockId);
			if(bean == null){
				request.setAttribute("tip", "没有这个预计到货表！");
				request.setAttribute("result", "failure");
				return;
			}
			
			if(!viewAll && bean.getCreateUserId() != user.getId()){ // 如果当前用户没有权限查看所有的的预计到货表
				//只能查看自己的进货信息
				request.setAttribute("tip", "你无权查看这个预计到货表！");
				request.setAttribute("result", "failure");
				return;
			}
			if(bean.getStatus()==BuyStockBean.STATUS0 || bean.getStatus()==BuyStockBean.STATUS1 || bean.getStatus()==BuyStockBean.STATUS4){
				request.setAttribute("tip", "预计到货表还未通过确认，不能够导出");
				request.setAttribute("result", "failure");
				return;
			}
			int logCountOld = serviceSlave.getNumber("id", "buy_admin_history", "count", "remark='预计到货表操作：" + bean.getCode() + "已确认进货' and log_id = "+bean.getId());
			int logCountNew = serviceSlave.getNumber("id", "buy_admin_history", "count", "log_type="+BuyAdminHistoryBean.LOGTYPE_BUY_STOCK+
					" and type="+BuyAdminHistoryBean.TYPE_MODIFY+" and log_id = "+bean.getId()+" and remark = '确认预计到货表'");
			if(logCountOld == 0 && logCountNew == 0){
				request.setAttribute("tip", "预计到货表未进行过确认，不能够导出");
				request.setAttribute("result", "failure");
				return;
			}

			//相关的入库记录
			String condition = "buy_stock_id = " + bean.getId();
			ArrayList buyStockProductList = serviceSlave.getBuyStockProductList(condition, -1, -1, "id desc");
			Iterator iter = buyStockProductList.listIterator();
			int proxyId = 0;
			while (iter.hasNext()) {
				BuyStockProductBean bpp = (BuyStockProductBean) iter.next();
				voProduct product = wareService.getProduct(bpp.getProductId());
				if(product == null){

				} else {
					bpp.setProduct(product);
					product.setPsList(stockService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
				}

				//已入库量
				int stockinCount = 0;
				List bispList = serviceSlave.getBuyStockinProductList("buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+bean.getId()+
						" and (status = "+BuyStockinBean.STATUS4+" or status = "+BuyStockinBean.STATUS6+") and  auditing_user_id > 0)" +
						" and product_id = "+product.getId(), -1, -1, null);
				Iterator bispIterator = bispList.listIterator();
				while(bispIterator.hasNext()){
					BuyStockinProductBean bisp = (BuyStockinProductBean)bispIterator.next();
					stockinCount += bisp.getStockInCount();
				}
				bpp.setStockinCount(stockinCount);

				proxyId = bpp.getProductProxyId();
				//产品线名称
				voProductLine pl = null;
				voProductLineCatalog plc = null;
				List list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId1());
				if(list2.size()>0){
					plc = (voProductLineCatalog)list2.get(0);
					pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
					bpp.setProductLineName(pl.getName());
				}
				if(StringUtil.convertNull(bpp.getProductLineName()).equals("")){
					list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId2());
					if(list2.size()>0){
						plc = (voProductLineCatalog)list2.get(0);
						pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
						bpp.setProductLineName(pl.getName());
					}
				}
				if(StringUtil.convertNull(bpp.getProductLineName()).equals("")){
					bpp.setProductLineName("无");
				}
			}
			//获取税点
			BuyOrderBean order = serviceSlave.getBuyOrder("id="+bean.getBuyOrderId());
			double taxPoint = 0;
			if(order != null){
				taxPoint = order.getTaxPoint();
			}
			
			//log记录
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(bean.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("打印/导出列表");
			log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			service.addBuyAdminHistory(log);
			
			//代理商
			String proxyName = wareService.getString("pp.name", "buy_stock_product bsp, supplier_standard_info pp", 
					"bsp.product_proxy_id = pp.id and bsp.buy_stock_id = "+bean.getId());
			bean.setProxyName(proxyName);

			//操作人
			voUser creatUser = admiService.getAdmin(bean.getCreateUserId());
			voUser auditingUser = admiService.getAdmin(bean.getAuditingUserId());
			bean.setCreatUser(creatUser);
			bean.setAuditingUser(auditingUser);

			request.setAttribute("bean", bean);
			request.setAttribute("buyStockProductList", buyStockProductList);
			request.setAttribute("proxyId", Integer.valueOf(proxyId));
			request.setAttribute("taxPoint", Double.valueOf(taxPoint));

			PrintLogBean plBean = new PrintLogBean();
			plBean.setOperId(bean.getId());
			plBean.setUserId(user.getId());
			plBean.setCreateDatetime(DateUtil.getNow());
			plBean.setType(PrintLogBean.PRINT_LOG_TYPE_BUYSTOCK);
			service.addPrintLog(plBean);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			service.releaseAll();
		}
	}


	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-3-3
	 * 
	 * 说明：采购 人员操作记录     
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void buyAdminHistory(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		int logId = StringUtil.StringToId(request.getParameter("logId"));
		int logType = StringUtil.toInt(request.getParameter("logType"));
		if(logType == -1){
			logType = BuyAdminHistoryBean.LOGTYPE_BUY_PLAN;
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		try {
			String condition = "id = " + logId;
			if(logType == BuyAdminHistoryBean.LOGTYPE_BUY_PLAN){
				
			} else if(logType == BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN){
				BuyStockinBean stockin = service.getBuyStockin("id=" + logId);
				request.setAttribute("bean", stockin);
			}
			condition = "log_id = " + logId + " and log_type=" + logType;
			List list = service.getBuyAdminHistoryList(condition, 0, -1, "id");

			request.setAttribute("list", list);
			request.setAttribute("logType", String.valueOf(logType));
		} finally {
			dbOperation.release();
		}
	}



	
	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-8-6
	 * 
	 * 说明：批次记录列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void stockBatchList(HttpServletRequest request,
			HttpServletResponse response){
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}

		int countPerPage = 50;

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		WareService wareService = new WareService(dbOp);
		try {
			String condition = null;
			//总数
			int totalCount = service.getStockBatchCount(condition);         //
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = service.getStockBatchList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				StockBatchBean batch = (StockBatchBean)iter.next();
				voProduct product = wareService.getProduct(batch.getProductId());
				batch.setProduct(product);
			}
			paging.setPrefixUrl("stockBatchList.jsp");
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		} finally {
			service.releaseAll();
		}
		
	}
	
	
	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2009-8-18
	 * 
	 * 说明：查询批次操作记录
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	public void stockBatchLog(HttpServletRequest request,
			HttpServletResponse response){
		
		voUser admin = (voUser)request.getSession().getAttribute("userView");
		if(admin == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		
		int productId = StringUtil.StringToId(request.getParameter("productId"));
		String batchCode = StringUtil.convertNull(request.getParameter("batchCode"));

		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
        AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbOp_slave);
		WareService wareService = new WareService(dbOp_slave);
		try{
			int totalCount = service.getStockBatchLogCount("product_id="+productId+" and batch_code='"+batchCode+"'");
			int countPerPage = 50;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			paging.setPrefixUrl("stockBatchLog.jsp?productId="+productId+"&batchCode="+batchCode);
			List batchLogList = service.getStockBatchLogList("product_id="+productId+" and batch_code='"+batchCode+"'", paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			voProduct product = wareService.getProduct2("a.id="+productId);
			
			Iterator itr = batchLogList.listIterator();
			while(itr.hasNext()){
				StockBatchLogBean batchLog = (StockBatchLogBean)itr.next();
				voUser user = admiService.getAdmin(batchLog.getUserId());
				batchLog.setUser(user);
			}
			
			request.setAttribute("batchLogList", batchLogList);
			request.setAttribute("product", product);
			request.setAttribute("paging", paging);
			
		}finally{
			service.releaseAll();	
		}
		
	}
	
}
