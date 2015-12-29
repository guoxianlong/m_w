package adultadmin.action.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

public class SearchTransformBuyOrderListAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(75);
		boolean search = group.isFlag(194);

		String code = StringUtil.convertNull(request.getParameter("code"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String status = StringUtil.convertNull(request.getParameter("status"));
		String payStatus = StringUtil.convertNull(request.getParameter("payStatus"));
		int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		String transformCount = StringUtil.convertNull(request.getParameter("transformCount"));
		int productLine = StringUtil.StringToId(request.getParameter("productLine"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String from = StringUtil.convertNull(request.getParameter("from"));
		productName = Encoder.decrypt(productName);//解码为中文
		if(productName == null){//解码失败,表示已经为中文,则返回默认
			productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));//名称
		}
		String ids = "-1,";
		String params = "";

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		try{

			StringBuilder buff = new StringBuilder();
			if(from.equals("stockBatch")){
				buff.append("status in ("+BuyOrderBean.STATUS3+","+BuyOrderBean.STATUS5+","+BuyOrderBean.STATUS6+") and ");
			}else{
				buff.append("status in ("+BuyOrderBean.STATUS3+","+BuyOrderBean.STATUS5+") and ");
			}
			
			if(!code.equals("")){
				params = params + "code="+code+"&";
				buff.append("code like '"+code+"%'");
				buff.append(" and ");
			}
			if(!startTime.equals("")&&!endTime.equals("")){
				params = params + "startTime="+startTime+"&endTime="+endTime+"&";
				startTime = startTime + " 00:00:00";
				endTime = endTime + " 23:59:59";
				buff.append("create_datetime between '"+startTime+"' and '"+endTime+"'");
				buff.append(" and ");
			}
			if(!status.equals("")){
				params = params + "status="+status+"&";
				buff.append("status = "+status+"");
				buff.append(" and ");
			}
			if(!payStatus.equals("")){
				params = params + "payStatus="+payStatus+"&";
				buff.append("pay_status in ("+payStatus+")");
				buff.append(" and ");
			}
			String parent1Id = ProductLinePermissionCache.getCatalogIds1(user);
			String parent2Id = ProductLinePermissionCache.getCatalogIds2(user);
			parent1Id = (StringUtil.isNull(parent1Id) ? "-1" : parent1Id);
			parent2Id = (StringUtil.isNull(parent2Id) ? "-1" : parent2Id);
			String sql = "bop.product_id = p.id and (p.parent_id1 in (" + parent1Id + ") or p.parent_id2 in (" + parent2Id + "))";
			List idsLists = service.getFieldList("bop.buy_order_id", "buy_order_product bop, product p", 
					sql, -1, -1, "bop.buy_order_id", "bop.buy_order_id", "int");
			for (int i=0;idsLists!=null&&i<idsLists.size();i++) {
				int id = ((Integer) idsLists.get(i)).intValue();
				if(ids.indexOf(id+",")==-1){
					ids += id + ",";
				}
			}
			if(!StringUtil.isNull(productCode)){
				params += "productCode="+productCode+"&";
				List idList = service.getFieldList(
						"buy_order_id", "buy_order_product", "product_id in (select id from product where code = '"+productCode+
						"') and buy_order_id in (" + ids.substring(0, ids.length() - 1 ) + ")", -1, -1, "buy_order_id", "buy_order_id", "int");
				ids = "";
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids = ids + id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if(productLine > 0){
				params += "productLine="+productLine+"&";
				String condition = "product_id in (select p.id from product p, product_line_catalog plc "
					+ "where case when plc.catalog_type = 1 then plc.catalog_id = p.parent_id1 "
					+ "when plc.catalog_type = 2 then plc.catalog_id = p.parent_id2 end and plc."
					+ "product_line_id = " + productLine + ")";
				List idList = null;
				if(!ids.equals("")){
					if(ids.endsWith(",")){
						ids = ids.substring(0, ids.length()-1);
					}
					idList = service.getFieldList("buy_order_id", "buy_order_product",
							condition+" and buy_order_id in ("+ids+")", -1, -1, "buy_order_id", "buy_order_id", "int");
					ids = "";
				} else {
					idList = service.getFieldList("buy_order_id", "buy_order_product",
							condition, -1, -1, "buy_order_id", "buy_order_id", "int");
				}
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids = ids + id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if(supplierId > 0){
				params = params + "supplierId="+supplierId+"&";
				List idList = null;
				if(!ids.equals("")){
					if(ids.endsWith(",")){
						ids = ids.substring(0, ids.length()-1);
					}
					idList = service.getFieldList(
							"buy_order_id", "buy_order_product", "product_proxy_id = "+supplierId+" and buy_order_id in ("+ids+")", -1, -1, "buy_order_id", "buy_order_id", "int");
					ids = "";
				} else {
					idList = service.getFieldList(
							"buy_order_id", "buy_order_product", "product_proxy_id = "+supplierId, -1, -1, "buy_order_id", "buy_order_id", "int");
				}
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids = ids + id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if(!productName.equals("")&&!ids.equals("-1,")){
				params = params + "productName="+Encoder.encrypt(productName)+"&";
				List idList = null;
				if(!ids.equals("")){
					if(ids.endsWith(",")){
						ids = ids.substring(0, ids.length()-1);
					}
					idList = service.getFieldList(
							"buy_order_id", "buy_order_product", "product_id in (select id from product where oriname like '%"+productName+"%') and buy_order_id in ("+ids+")", -1, -1, "buy_order_id", "buy_order_id", "int");
					ids = "";
				}else{
					idList = service.getFieldList(
							"buy_order_id", "buy_order_product", "product_id in (select id from product where oriname like '%"+productName+"%')", -1, -1, "buy_order_id", "buy_order_id", "int");
				}

				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids = ids + id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}
			if(!transformCount.equals("")&&!ids.equals("-1,")){
				buff.append("transform_count = "+transformCount);
				buff.append(" and ");
				params = params + "transformCount="+transformCount+"&";
			}
			if(!from.equals("")){
				params = params + "from="+from+"&";
			}
			if(!ids.equals("")){
				ids = ids.substring(0, ids.length()-1);
				buff.append("id in ("+ids+")");
			}
			String condition = null;
			if(buff.length() > 0){
				condition = buff.toString();
				if(condition.endsWith(" and ")){
					condition = condition.substring(0,condition.lastIndexOf(" and "));
				}
				if(params.endsWith("&")){
					params = "?"+params.substring(0, params.length()-1);
				}
			}

			int orderId = StringUtil.StringToId(request.getParameter("orderId"));

			//总数
			int totalCount = service.getBuyOrderCount(condition);
			int countPerPage = 10;
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List buyOrderList = service.getBuyOrderList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = buyOrderList.listIterator();
			while(iter.hasNext()){
				BuyOrderBean orderBean = (BuyOrderBean)iter.next();

				//产品类别
				String productType = "";
				String parentId1 = "";
				String parentId2 = "";
				ArrayList typeKeyList = service.getFieldList(
						"p.parent_id1", 
						"buy_order_product bop, product p" , 
						"bop.product_id = p.id and bop.buy_order_id ="+orderBean.getId(),
						-1, -1, "p.parent_id1", null, "String");	
				Iterator typeKeyIter = typeKeyList.listIterator();
				while(typeKeyIter.hasNext()){
					String key = (String) typeKeyIter.next();
					parentId1 += (key == null ? "-1" : key) + ", ";
				}
				typeKeyList = service.getFieldList(
						"p.parent_id2", 
						"buy_order_product bop, product p" , 
						"bop.product_id = p.id and bop.buy_order_id ="+orderBean.getId(),
						-1, -1, "p.parent_id1", null, "String");
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
				orderBean.setProductType(productType);

				//订单预计总金额
				List buyOrderProductList = service.getBuyOrderProductList("buy_order_id="+orderBean.getId(), -1, -1, "id desc");
				Iterator bopIter = buyOrderProductList.listIterator();
				double totalPurchasePrice = 0;
				while(bopIter.hasNext()){
					BuyOrderProductBean bop = (BuyOrderProductBean)bopIter.next();
					totalPurchasePrice += (bop.getOrderCountBJ()+bop.getOrderCountGD())*bop.getPurchasePrice();
				}
				orderBean.setTotalPurchasePrice(totalPurchasePrice);

				//操作人
				voUser creatUser = adminService.getAdmin(orderBean.getCreateUserId());
				voUser auditingUser = adminService.getAdmin(orderBean.getAuditingUserId());
				orderBean.setCreatUser(creatUser);
				orderBean.setAuditingUser(auditingUser);

				BuyOrderProductBean bop = service.getBuyOrderProduct("buy_order_id="+orderBean.getId());
				if(bop!=null){
					orderBean.setProxyId(bop.getProductProxyId());
				}
			}

			if(orderId!=0){
				BuyOrderBean order = service.getBuyOrder("id="+orderId);

				condition = "buy_order_id = "+orderId;
				List buyOrderProductList = service.getBuyOrderProductList(condition, -1, -1, "id desc");
				iter = buyOrderProductList.listIterator();
				int planCount = 0;
				while (iter.hasNext()) {
					BuyOrderProductBean bpp = (BuyOrderProductBean) iter.next();
					//					bpp.setPlanCount(bpp.getPlanCountBJ()+bpp.getPlanCountGD());         //****
					planCount += bpp.getOrderCountBJ()+bpp.getOrderCountGD();
					voProduct product = wareService.getProduct(bpp.getProductId());
					if(product == null){

					} else {
						condition = "pl.id = plc.product_line_id and ("
							+ "(plc.catalog_type = 1 and plc.catalog_id = " + product.getParentId1() + ") or "
							+ "(plc.catalog_type = 2 and plc.catalog_id = " + product.getParentId2() + "))";
						List productLineList = service.getFieldList("pl.name", "product_line pl,product_line_catalog plc", 
								condition, -1, -1, "pl.id", "pl.id", "String");
						Iterator proLineIter = productLineList.listIterator();
						String proLineName = "";
						while (proLineIter.hasNext()) {
							String key = (String) proLineIter.next();
							proLineName += (key == null ? "" : key + ", ");
						}
						if (proLineName != null && proLineName.length() > 0) {
							proLineName = proLineName.substring(0, proLineName.length() - 2);
						} else {
							proLineName = "无";
						}
						bpp.setProductLineName(proLineName);
						bpp.setProduct(product);
					}

					//已进货量
					int stockCountBJ = 0;
					int stockCountGD = 0;
					condition = "buy_stock_id in (select id from buy_stock where buy_order_id = "+orderId+" and status in ("+BuyStockBean.STATUS2 +
					","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+","+BuyStockBean.STATUS6+") and area = "+ProductStockBean.AREA_BJ+") and product_id = "+product.getId();
					List bspList = service.getBuyStockProductList(condition, -1, -1, null);
					Iterator bspIterator = bspList.listIterator();
					while(bspIterator.hasNext()){
						BuyStockProductBean bsp = (BuyStockProductBean)bspIterator.next();
						stockCountBJ += bsp.getBuyCount();
					}
					condition = "buy_stock_id in (select id from buy_stock where buy_order_id = "+orderId+" and status in ("+BuyStockBean.STATUS2 +
					","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+","+BuyStockBean.STATUS6+") and area = "+ProductStockBean.AREA_GF+") and product_id = "+product.getId();
					bspList = service.getBuyStockProductList(condition, -1, -1, null);
					bspIterator = bspList.listIterator();
					while(bspIterator.hasNext()){
						BuyStockProductBean bsp = (BuyStockProductBean)bspIterator.next();
						stockCountGD += bsp.getBuyCount();
					}
					bpp.setStockCountBJ(stockCountBJ);
					bpp.setStockCountGD(stockCountGD);
				}

				//代理商
				//				proxyId = String.valueOf(service.getNumber("id", "product_proxy", "max", 
				//						"id in (select bop.product_proxy_id from buy_order_product bop, product_proxy pp where bop.product_proxy_id = pp.id and bop.buy_order_id = "+orderId+")"));
				supplierId = service.getNumber("id", "supplier_standard_info", "max", 
						"id in (select bop.product_proxy_id from buy_order_product bop, product_proxy pp where bop.product_proxy_id = pp.id and bop.buy_order_id = "+orderId+")");
				request.setAttribute("buyOrderProductList", buyOrderProductList);
				request.setAttribute("supplier_id", Integer.valueOf(supplierId));
				request.setAttribute("order", order);
			}


			paging.setPrefixUrl("searchTransformBuyOrderList.do"+params);
			request.setAttribute("params", params);
			request.setAttribute("paging", paging);
			request.setAttribute("buyOrderList", buyOrderList);

			//List proxyList = adminService.getSelects("product_proxy", "order by id");
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("+supplierIds+") order by id");
			request.setAttribute("supplierList", supplierList);
			HashMap supplierMap = new HashMap();
			Iterator iterator = supplierList.listIterator();
			while(iterator.hasNext()){
				voSelect supplier = (voSelect)iterator.next();
				supplierMap.put(Integer.valueOf(supplier.getId()), supplier);
			}
			List productLineList = wareService.getProductLineList("product_line.id in ("+productLines+")");
			request.setAttribute("supplierMap", supplierMap);
			request.setAttribute("productLineList", productLineList);
		}finally{
			service.releaseAll();
			wareService.releaseAll();
		}

		String forward = IConstants.SUCCESS_KEY;
		if(!StringUtil.convertNull(request.getParameter("from")).equals("")){
			forward = StringUtil.convertNull(request.getParameter("from"));
		}

		return mapping.findForward(forward);
	}
}
