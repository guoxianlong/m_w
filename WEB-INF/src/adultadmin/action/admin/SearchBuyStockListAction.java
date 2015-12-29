package adultadmin.action.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

public class SearchBuyStockListAction extends BaseAction {

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
		boolean viewAll = group.isFlag(55);
		boolean search = group.isFlag(195);
		if(!search){
			request.setAttribute("tip", "您无权查询预计到货表！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		
		String code = StringUtil.convertNull(request.getParameter("code"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String[] statuss = request.getParameterValues("status");
		int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		String arrivalStartTime = StringUtil.convertNull(request.getParameter("arrivalStartTime"));
		String arrivalEndTime = StringUtil.convertNull(request.getParameter("arrivalEndTime"));
		int productLine = StringUtil.StringToId(request.getParameter("productLine"));
		productName = Encoder.decrypt(productName);//解码为中文
		if(productName == null){//解码失败,表示已经为中文,则返回默认
			productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));//名称
		}
		String status = "";
		if(statuss!=null){
			for(int i=0;i<statuss.length;i++){
				status = status + statuss[i] +",";
			}
			if(status.endsWith(",")){
				status = status.substring(0, status.length()-1);
			}
		}
		String ids = "";
		String params = "";
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		WareService wareService = new WareService(dbOperation);
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, dbOperation);
		try{
			StringBuilder buff = new StringBuilder();
			buff.append("status != "+BuyStockBean.STATUS8+" and ");
			if(!viewAll){ // 如果当前用户没有权限查看所有的的采购计划
				//只能查看自己的计划信息
				buff.append(" (create_user_id=");
				buff.append(user.getId());
				buff.append(" or assign_user_id=");
				buff.append(user.getId());
				buff.append(") and ");
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
				if(status.indexOf("2")!=-1){
					status = status + ",3";
				}
				buff.append("status in ("+status+")");
				buff.append(" and ");
			}
			if(!arrivalStartTime.equals("")&&!arrivalEndTime.equals("")){
				params = params + "arrivalStartTime="+arrivalStartTime+"&arrivalEndTime="+arrivalEndTime+"&";
				arrivalStartTime = arrivalStartTime + " 00:00:00";
				arrivalEndTime = arrivalEndTime + " 23:59:59";
				buff.append("expect_arrival_datetime between '"+arrivalStartTime+"' and '"+arrivalEndTime+"'");
				buff.append(" and ");
			}
			
			//产品线权限
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
			String sql = "";
			if(productLine > 0){
				params += "productLine="+productLine+"&";
				String condition = "product_id in (select p.id from product p, product_line_catalog plc "
					+ "where case when plc.catalog_type = 1 then plc.catalog_id = p.parent_id1 "
					+ "when plc.catalog_type = 2 then plc.catalog_id = p.parent_id2 end and plc."
					+ "product_line_id = " + productLine + ")";
//					+ "product_line_id = " + productLine + ") and buy_stock_id in (" + ids.substring(0, ids.length() - 1) + ")";
				List idList = service.getFieldList("buy_stock_id", "buy_stock_product",
						condition, -1, -1, "buy_stock_id", "buy_stock_id", "int");
				ids = "";
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids += id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids += "-1,";
				}
			}
			if(supplierId > 0){
				params = params + "supplierId="+supplierId+"&";
				 sql = "product_proxy_id = "+supplierId+(ids.length() > 0 ? " and buy_stock_id in ("+ids.substring(0, ids.length() -1)+")" : "");
				ids = "";
				List idList = service.getFieldList(
									"buy_stock_id", "buy_stock_product", sql, -1, -1, "buy_stock_id", "buy_stock_id", "int");
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
								"buy_stock_id", "buy_stock_product", "product_id in (select id from product where oriname like '%"+productName+"%') and buy_stock_id in ("+ids+")", -1, -1, "buy_stock_id", "buy_stock_id", "int");
					ids = "";
				}else{
					idList = service.getFieldList(
								"buy_stock_id", "buy_stock_product", "product_id in (select id from product where oriname like '%"+productName+"%')", -1, -1, "buy_stock_id", "buy_stock_id", "int");
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


			//总数
			int totalCount = service.getBuyStockCount(condition);
			int countPerPage = 50;
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

					//代理商
					//String proxyName = adminService.getString("pp.name", "buy_stock_product bsp, product_proxy pp", 
					//		"bsp.product_proxy_id = pp.id and bsp.buy_stock_id = "+stockBean.getId());
					String proxyName = wareService.getString("ssi.name", "buy_stock_product bsp, supplier_standard_info ssi", 
							"bsp.product_proxy_id = ssi.id and bsp.buy_stock_id = "+stockBean.getId());
					if (proxyName == null || proxyName.length() == 0) {
						proxyName = "无";
					}
					stockBean.setProxyName(proxyName);

					//操作人
					voUser creatUser = adminService.getAdmin(stockBean.getCreateUserId());
					voUser auditingUser = adminService.getAdmin(stockBean.getAuditingUserId());
					stockBean.setCreatUser(creatUser);
					stockBean.setAuditingUser(auditingUser);
				}
			}
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 order by id");
			List productLineList = wareService.getProductLineList("1=1");
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			paging.setPrefixUrl("searchBuyStockList.do"+params);
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		}finally{
			dbOperation.release();
		}

		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}
