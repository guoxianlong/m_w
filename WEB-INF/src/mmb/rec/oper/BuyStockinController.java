package mmb.rec.oper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.PrintLogBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyPlanBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.stock.ProductStockBean;
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

@Controller
@RequestMapping("/admin")
public class BuyStockinController {
	
	private static byte[] buyStockinLock = new byte[0];
	
	
	/**
	 * 
	 * 作者：郝亚斌
	 * 
	 * 创建日期：2013-08-19
	 * 
	 * 说明：进入采购入库单列表
	 * 
	 * 参数及返回值说明：
	 * @param request
	 * @param response
	 */
	@RequestMapping("toBuyStockinList")
	public String toBuyStockinList(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(56);
		DbOperation dbop_slave2 = new DbOperation(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbop_slave2);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		try {
			//得到供应商 集合   和  产品线集合的方式。。
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			
		} finally {
			dbop_slave2.release();
		}
		return "forward:/admin/rec/oper/buyStockin/buyStockinList.jsp";
	}
	
	/**
	 * 
	 * 作者：郝亚斌
	 * 
	 * 创建日期：2013-08-19
	 * 
	 * 说明：进入采购入库单列表
	 * 
	 * 参数及返回值说明：
	 * @param request
	 * @param response
	 */
	@RequestMapping("toTransformBuyStockList")
	public String toTransformBuyStockList(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(56);
		DbOperation dbop_slave2 = new DbOperation(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbop_slave2);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		try {
			//得到供应商 集合   和  产品线集合的方式。。
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			
		} finally {
			dbop_slave2.release();
		}
		return "forward:/admin/rec/oper/buyStockin/transformBuyStockList.jsp";
	}
	
	/**
	 * 
	 * 作者：郝亚斌
	 * 
	 * 创建日期：2013-08-19
	 * 
	 * 说明：采购入库单列表
	 * 
	 * 参数及返回值说明：
	 * @param request
	 * @param response
	 */
	@RequestMapping("buyStockinList")
	@ResponseBody
	public Map<String,Object> buyStockinList(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			resultMap.put("tip", "当前没有登录，操作失败！");
			return resultMap;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(56);
		int id = StringUtil.StringToId(request.getParameter("id"));//这个是在点击。。。具体的采购入库单时，，，传过来的ID编号。。
		int countPerPage = 15;
		if( request.getParameter("rows") != null ) {
			countPerPage = StringUtil.toInt(request.getParameter("rows"));
		}
		int totalCount = 0;
		DbOperation dbop_slave2 = new DbOperation(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbop_slave2);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop_slave2);
		try {
			//得到查询   条件值
			String action = StringUtil.convertNull(request.getParameter("search"));
			String stockinCode = StringUtil.convertNull(request.getParameter("stockinCode"));
			String sCreateDate = StringUtil.convertNull(request.getParameter("sCreateDate"));
			String eCreateDate = StringUtil.convertNull(request.getParameter("eCreateDate"));
			String createUser = StringUtil.convertNull(request.getParameter("createUser"));
			String auditUser = StringUtil.convertNull(request.getParameter("auditUser"));
			String sBuyDate = StringUtil.convertNull(request.getParameter("sBuyDate"));
			String eBuyDate = StringUtil.convertNull(request.getParameter("eBuyDate"));
			String buyStockCode = StringUtil.convertNull(request.getParameter("buyStockCode"));
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));
			String productName = StringUtil.convertNull(request.getParameter("productName"));
			productName = Encoder.decrypt(productName);//解码为中文
			if(productName == null){//解码失败,表示已经为中文,则返回默认
				productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
			}
			String[] statuss = request.getParameterValues("status");
			int productLine = StringUtil.StringToId(request.getParameter("productLine"));
			int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));// 供应商id
			//结束
			
			
			if(action.equals("search")){//判断当前的操作是不是模糊查询。。。
				String condition = "status != "+BuyStockinBean.STATUS8;//设置状态不是被删除状态。。。
				StringBuilder buf = new StringBuilder();
				StringBuilder search = new StringBuilder();
				StringBuilder param = new StringBuilder();//是接在   url后的参数条件。。。是为了在分页当中起到作用。。
				buf.append(" and code not like 'RK%'");
				if(id>0){
					buf.append(" and id=");
					buf.append(id);
				}
				if(!viewAll){ //没有查看所有进货单的权限
					buf.append(" and create_user_id=");
					buf.append(user.getId());
				}
				if(buf.length() > 0){
					condition = condition + buf.toString();
				}
				if(!StringUtil.isNull(stockinCode)) {//判断模糊查询中的   采购入库编号是否为空、、
					param.append("stockinCode="+stockinCode+"&");
					search.append(" and code = '" + stockinCode + "'");//开始设置模糊查询的条件。。(采购入库编号条件)
				}
				if(!StringUtil.isNull(buyStockCode)) {// 判断采购预计到货单编号   是否为空。。
					param.append("buyStockCode="+buyStockCode+"&");
					int buyStockId = 0;
					BuyStockBean buyStock = service.getBuyStock("code = '"+buyStockCode+"'");//根据输入的采购预计到货编号  得到  采购预计到货对象。。从而得到采购预计到货ID
					if(buyStock != null){
						buyStockId = buyStock.getId();
					}
					search.append(" and buy_stock_id = "+buyStockId);//开始设置模糊查询的条件。。（采购预计到货编号条件）
				}
				if(!StringUtil.isNull(sCreateDate) && !StringUtil.isNull(eCreateDate)){//开始判断查询条件的添加日期  是否为空
					param.append("sCreateDate="+sCreateDate+"&");
					param.append("eCreateDate="+eCreateDate+"&");
					search.append(" and left(create_datetime, 10) between '" + sCreateDate +"'");//开始设置条件（  时间条件。。）
					search.append(" and '" + eCreateDate + "'");
				}
				if(!StringUtil.isNull(createUser)){//开始判断生产人  是否为空。。
					param.append("createUser="+createUser+"&");
					search.append(" and create_user_id in (select u.id from user u"); //根据生产人姓名得到生成人的ID
					search.append(" where u.username like '%" + createUser +"%')");
				}
				if(!StringUtil.isNull(auditUser)){//开始判断  审核人  是否为空
					param.append("auditUser="+auditUser+"&");
					search.append(" and auditing_user_id in (select u.id from user u");
					search.append(" where u.username like '%" + auditUser +"%')");// 根据审核人  姓名 得到审核人ID
				}
				if(!StringUtil.isNull(sBuyDate) && !StringUtil.isNull(eBuyDate)){//开始判断  采购完成时间  是否为空
					param.append("sBuyDate="+sBuyDate+"&");
					param.append("eBuyDate="+eBuyDate+"&");
					search.append(" and left(confirm_datetime, 10) between '" + sBuyDate +"'");
					search.append(" and '" + eBuyDate + "'"); //设置采购完成时间的条件。。
				}
				String status = "";
				if(statuss != null && statuss.length > 0){//开始判断采购入库单状态   是否为空  （状态可能为多个值）
					for (int i=0;i<statuss.length;i++) {
						status += (statuss[i] + ",");
					}
					status = status.substring(0, status.length() - 1);
					param.append("status="+status+"&");
					search.append(" and status in ("+status+")"); //开始设定条件。。
				}
				
				//*****
				String parent1Id = ProductLinePermissionCache.getCatalogIds1(user);//根据用户名  得到  一级分类编号
				String parent2Id = ProductLinePermissionCache.getCatalogIds2(user); //根据用户  。。得到二级分类编号。。
				parent1Id = (StringUtil.isNull(parent1Id) ? "-1" : parent1Id);//因为分类编号。。是从那-1 开始写的。。
				parent2Id = (StringUtil.isNull(parent2Id) ? "-1" : parent2Id);
				//根据入库的产品。。得到入库编号。。
				String idsSql = "select bsip.buy_stockin_id from buy_stockin_product bsip join product p on bsip.product_id = p.id";
				
				//开始对   产品表product的  一级分类编号。。跟二级分类编号。。开始赋值。。
				String productCondition = " (p.parent_id1 in (" + parent1Id + ") or p.parent_id2 in (" + parent2Id + ")) and ";
				
				if(!StringUtil.isNull(productName)||!StringUtil.isNull(productCode)){//开始判断 产品编号。跟产品原名称  是否为空。。
					if(!StringUtil.isNull(productName)){
						param.append("productName="+Encoder.encrypt(productName)+"&");
//						Encoder.encrypt(productName) 设置编码。。
						productCondition = productCondition + " p.oriname like '%"+productName+"%' and ";
					}
					if(!StringUtil.isNull(productCode)){
						param.append("productCode="+productCode+"&");
						productCondition = productCondition + " p.code = '"+productCode+"' and ";
					}
				}
				
				
				if(productLine > 0){//如果产品线  不为空。。
					param.append("productLine="+productLine+"&");
					productCondition = productCondition + "p.id in (select p.id from product p, product_line_catalog plc "
						+ "where case when plc.catalog_type = 1 then plc.catalog_id = p.parent_id1 "
						+ "when plc.catalog_type = 2 then plc.catalog_id = p.parent_id2 end and plc."
						+ "product_line_id = " + productLine + ") and ";
				}
				if(supplierId > 0){//对 buy_stockin_product表的代理商编号  赋值。。
					param.append("supplierId="+supplierId+"&");
					productCondition = productCondition + "bsip.product_proxy_id = "+supplierId+" and ";
				}
				//***
				if(productCondition.length() > 0){
					productCondition = productCondition.substring(0,productCondition.length()-5);
					idsSql = idsSql + " where " + productCondition;
					search.append(" and id in (" + idsSql + ")");//根据多中条件得到    采购入库的编号。。
				}
				// 如果当前的操作是  查询。。。则把条件更改为   多条件的模糊查询、、、、如果不是在进行查询。。。则  直接使用  最先前的   不被删除的条件。。。得到   采购入库信息
				if(search.length() > 0){
					condition += search.toString();
				}
				//总数
				totalCount = service.getBuyStockinCount(condition);
				//页码
				int pageIndex = 0;
				if( request.getParameter("page") != null ) {
					pageIndex = StringUtil.parstInt(request.getParameter("page"));
					pageIndex = pageIndex - 1;
				}
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				List list = service.getBuyStockinList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
				if(list!=null){
					Iterator iter = list.listIterator();
					int i = 0; 
					while(iter.hasNext()){
						i++;
						BuyStockinBean shBean = (BuyStockinBean)iter.next();
						//得到打印的次数。。
						shBean.setPrintCount(service.getPrintLogCount("oper_id=" + shBean.getId() + " and type=" + PrintLogBean.PRINT_LOG_TYPE_BUYSTOCKIN));
						
						//得到采购进货单。。
						BuyStockBean stock = service.getBuyStock("id="+shBean.getBuyStockId());
						shBean.setBuyStock(stock);

						//产品类别
						String productType = "";
						String parentId1 = "";
						String parentId2 = "";
						ArrayList typeKeyList = service.getFieldList(//得到当前入库了的产品的   一级分类编号
								"p.parent_id1", 
								"buy_stockin_product bsp, product p" , 
								"bsp.product_id = p.id and bsp.buy_stockin_id ="+shBean.getId(),
								-1, -1, "p.parent_id1", null, "String");
						
						Iterator typeKeyIter = typeKeyList.listIterator();
						while(typeKeyIter.hasNext()){
							String key = (String) typeKeyIter.next();
							parentId1 += (key == null ? "-1" : key) + ", ";
						}
						typeKeyList = service.getFieldList(//得到  当前入库了的产品的二级分类编号。。（根据入库编号。。。。buy_stockin_product表使入库表跟产品表连接。。）
								"p.parent_id2", 
								"buy_stockin_product bsp, product p" , 
								"bsp.product_id = p.id and bsp.buy_stockin_id ="+shBean.getId(),
								-1, -1, "p.parent_id2", null, "String");
						typeKeyIter = typeKeyList.listIterator();
						while(typeKeyIter.hasNext()){
							String key = (String) typeKeyIter.next();
							parentId2 += (key == null ? "-1" : key) + ", ";
						}
						if (parentId1 != null && parentId1.length() > 0) {
							parentId1 = parentId1.substring(0, parentId1.length() -2);//得到一级分类编号。。每一个编号用 ,号隔开
						} else {
							parentId1 = "-1";
						}
						if (parentId2 != null && parentId2.length() > 0) {//得到二级分类编号。。每一个编号用 ,号隔开
							parentId2 = parentId2.substring(0, parentId2.length() -2);
						} else {
							parentId2 = "-1";
						}
						
						//生成    可以得到产品线  类型  的sql语句。。。
						String productLineSql = "pl.id = plc.product_line_id and ((plc.catalog_id in (" + parentId1 
						+ ") and plc.catalog_type = 1) or (plc.catalog_type = 2 and plc.catalog_id "
						+ " in (" + parentId2 + ")))";
						typeKeyList = service.getFieldList(//得到产品线集合、、。、
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
						
						shBean.setProductType(productType);
						//代理商
						//String proxyName = adminService.getString("pp.name", "buy_stockin_product bsp, product_proxy pp", 
						//		"bsp.product_proxy_id = pp.id and bsp.buy_stockin_id = "+shBean.getId());
						String proxyName = wareService.getString("ssi.name", "buy_stockin_product bsp, supplier_standard_info ssi", 
								"bsp.product_proxy_id = ssi.id and bsp.buy_stockin_id = "+shBean.getId());
						if (proxyName == null || proxyName.length() == 0) {
							proxyName = "无";
						}
						shBean.setProxyName(proxyName);

						//操作人
						voUser creatUser = admiService.getAdmin(shBean.getCreateUserId());
						voUser auditingUser = admiService.getAdmin(shBean.getAuditingUserId());
						shBean.setCreatUser(creatUser);
						shBean.setAuditingUser(auditingUser);
						Map<String, String> map = new HashMap<String,String>();
						map.put("count", new Integer(i).toString());
						map.put("buy_stockin_code", "<a href='"+request.getContextPath()+"/admin/stock2/buyStockin.jsp?id="+shBean.getId()+"'>"+shBean.getCode()+"</a>");
						map.put("create_datetime", shBean.getCreateDatetime().substring(11, 16));
						map.put("product_line", shBean.getProductType());
						String supplierName = shBean.getProxyName() == null ? "无" : shBean.getProxyName();
						map.put("supplier_name", supplierName);
						map.put("buy_stock_code", shBean.getBuyStock().getCode());
						String wareA = shBean.getStockArea() == ProductStockBean.AREA_BJ ? "北京" : shBean.getStockArea() == ProductStockBean.AREA_GF ? "芳村" : shBean.getStockArea() == ProductStockBean.AREA_ZC ? "增城" : shBean.getStockArea() == ProductStockBean.AREA_WX ? "无锡" : "";
						map.put("ware_area", wareA);
						String statusS = "";
						if(shBean.getStatus() == BuyStockinBean.STATUS0 || shBean.getStatus() == BuyStockinBean.STATUS1 || shBean.getStatus() == BuyStockinBean.STATUS2){
								statusS = "<font color='red'>"+ shBean.getStatusName()+"</font>";
							}else{
								statusS = shBean.getStatusName();
							}
						map.put("status", statusS);
						String createUserName = "";
						if(shBean.getCreatUser()!=null){
							createUserName += shBean.getCreatUser().getUsername();
						} else {
							createUserName += "-";
						}
						if(shBean.getAuditingUser()!=null){
							createUserName += "/"+shBean.getAuditingUser().getUsername();
						} else {
							createUserName += "/-";
						}
						map.put("create_username",createUserName);
						String management = "";
						boolean transform = group.isFlag(114);
						boolean bianji = group.isFlag(169);
						management += "<a href='"+request.getContextPath()+"/admin/stock2/buyStockin.jsp?id="+shBean.getId()+"'>编辑</a>";
						int isDeleteAvailable = 0;
						int isPrintAvailable = 0;
						int isPriceAvailable = 0;
						if((shBean.getStatus() == BuyStockinBean.STATUS0 || shBean.getStatus() == BuyStockinBean.STATUS3 || shBean.getProxyName() == null)&&(bianji||shBean.getCreateUserId()==user.getId())){
							management += "|<a href='"+request.getContextPath()+"/admin/deleteBuyStockin.mmx?buyStockinId="+shBean.getId()+"' onclick='return confirm(\"确认删除？\")'>删除</a>";
							isDeleteAvailable = 1;
						}
						if(group.isFlag(181) && (shBean.getStatus() == BuyStockinBean.STATUS6 || shBean.getStatus() == BuyStockinBean.STATUS4)){
							management += "<a href='"+request.getContextPath()+"/admin/stock2/batchPrice.jsp?id="+shBean.getId()+"'>|查看入库价</a>";
							isPriceAvailable = 1;
						}
						if(group.isFlag(31)){ 
						if(shBean.getStatus()==BuyStockinBean.STATUS6||shBean.getStatus()==BuyStockinBean.STATUS4){ 
							management += "|<a href='"+request.getContextPath()+"/admin/rec/oper/buyStockin/buyStockinPrint.jsp?stockinId=" + shBean.getId() + "' target='_blank' style='color: green;'>打印</a>";
							isPrintAvailable = 1;
							if(shBean.getPrintCount()>0){ 
								management += "<a href='"+request.getContextPath()+"/admin/stock2/printLog.jsp?operId="+shBean.getId()+"&type="+ PrintLogBean.PRINT_LOG_TYPE_BUYSTOCKIN +"'>|打印"+shBean.getPrintCount()+ "次";
										}else{ 
											management += "|打印次数";
										}
								management +="</a>";
						} 
						}
						map.put("is_delete_available", new Integer(isDeleteAvailable).toString());  //是否可删除
						map.put("is_price_available", new Integer(isPriceAvailable).toString());	//是否可看入库价
						map.put("is_print_available", new Integer(isPrintAvailable).toString()); //是否可打印
						map.put("buy_stockin_id", new Integer(shBean.getId()).toString());
						map.put("management", management);
						map.put("print_count", new Integer(shBean.getPrintCount()).toString() + "次");
						resultList.add(map);
					}
				}
				param.append("search=search&");
				paging.setPrefixUrl("buyStockinList.jsp" + (param.length() > 0 ? "?" + param.substring(0, param.length() -1) : ""));
				request.setAttribute("paging", paging);
				request.setAttribute("list", list);
			}
			resultMap.put("total", totalCount);
			resultMap.put("rows", resultList);
			//得到供应商 集合   和  产品线集合的方式。。
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			
		} finally {
			dbop_slave2.release();
		}
		return resultMap;
	}
	
	/**
	 * 作者：郝亚斌
	 * 
	 * 创建日期：2013-08-21
	 * 
	 * 说明：预计到货表待转换列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("transformBuyStockList")
	@ResponseBody
	public Map<String,Object> transformBuyStockList(HttpServletRequest request, HttpServletResponse response){
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		resultMap.put("total","0");
		resultMap.put("rows",resultList);
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			resultMap.put("tip", "未登录，操作失败！");
			return resultMap;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(55);

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
			String condition = "status in ("+BuyStockBean.STATUS2+","+BuyStockBean.STATUS3+","+BuyStockBean.STATUS5+")";
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
			int countPerPage = StringUtil.toInt(request.getParameter("rows"));
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("page"));
			pageIndex = pageIndex - 1;
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List buyStockList = service.getBuyStockList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			Iterator iter = buyStockList.listIterator();
			int i = 0 ;
			while(iter.hasNext()){
				BuyStockBean stockBean = (BuyStockBean)iter.next();
				i++;
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
				
				Map<String,String> map = new HashMap<String,String>();
				map.put("count", new Integer(i).toString());
				map.put("buy_stock_code", "<a href='javascript:getInfo("+stockBean.getId()+");testDialog();'>"+stockBean.getCode()+"</a>");
				map.put("create_datetime", stockBean.getCreateDatetime().substring(11,16));
				map.put("product_line", stockBean.getProductType());
				map.put("supplier_name", stockBean.getProxyName());
				String wareA = stockBean.getArea() == ProductStockBean.AREA_BJ ? "北京" : stockBean.getArea() == ProductStockBean.AREA_GF ? "芳村" : stockBean.getArea() == ProductStockBean.AREA_ZC ? "增城" : stockBean.getArea() == ProductStockBean.AREA_WX ? "无锡" : "";
				map.put("ware_area", wareA);
				String statusS = "";
				if(stockBean.getStatus() == BuyPlanBean.STATUS0 || stockBean.getStatus() == BuyPlanBean.STATUS1 || stockBean.getStatus() == BuyPlanBean.STATUS4){
					statusS = "<font color='red'>"+stockBean.getStatusName()+"</font>";
				}else{
					statusS = stockBean.getStatusName();
				}
				map.put("status", statusS);
				String createUsers = "";
				if(stockBean.getCreatUser()!=null){
					createUsers += stockBean.getCreatUser().getUsername();
				}else {
					createUsers += "-";
				}
				if(stockBean.getAuditingUser()!=null){
					createUsers += "/"+stockBean.getAuditingUser().getUsername();
				} else {
					createUsers +="/-";
				}
				map.put("create_username", createUsers);
				/*<tr<%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
				  <td><%=(i + 1)%></td>
				  <td><a href="transformBuyStockList.jsp?stockId=<%=bean.getId() %>&pageIndex=<%=StringUtil.StringToId(request.getParameter("pageIndex")) %>"><%=bean.getCode()%></a></td>
				  <td><%=bean.getCreateDatetime().substring(11, 16)%></td>
				  <td><%=bean.getProductType() %></td>
				  <td><%=bean.getProxyName() %></td>
				  <td><%if(bean.getArea() == 0){%>北京<%}else if(bean.getArea()==1){%>芳村<%}else if(bean.getArea()==3){%>增城<%}else if(bean.getArea()==4){%>无锡<%}%></td>
				  <td>
				<%if(bean.getStatus() == BuyPlanBean.STATUS0 || bean.getStatus() == BuyPlanBean.STATUS1 || bean.getStatus() == BuyPlanBean.STATUS4){%>
					<font color="red"><%=bean.getStatusName()%></font>
				<%}else{%><%=bean.getStatusName()%><%}%>
				  </td>
				  <td><%if(bean.getCreatUser()!=null){%><%=bean.getCreatUser().getUsername() %><%}%>/<%if(bean.getAuditingUser()!=null){%><%=bean.getAuditingUser().getUsername()%><%}%></td>
				</tr>*/
				resultList.add(map);
			}
			resultMap.put("total", totalCount);
			resultMap.put("rows", resultList);
			String productLines = ProductLinePermissionCache.getProductLineIds(user);
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			productLines = StringUtil.isNull(productLines) ? "-1" : productLines;
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects("supplier_standard_info", "where status = 1 and id in ("
					+ supplierIds + ") order by id");
			List productLineList = wareService.getProductLineList("product_line.id in (" + productLines + ")");
			request.setAttribute("supplierList", supplierList);
			request.setAttribute("productLineList", productLineList);
			paging.setPrefixUrl("transformBuyStockList.jsp" + (param.length() > 0 ? "?" + param.substring(0, param.length()-1) : ""));
			request.setAttribute("buyStockList", buyStockList);
			request.setAttribute("paging", paging);
		}finally{
			dbOp_slave.release();
		}
		return resultMap;
	}
	
	/**
	 * 作者： 郝亚斌
	 * 
	 *  创建日期：2013-08-21
	 *  
	 *  说明： 单独提出来的用来获取创建采购入库单的预计单的信息
	 * @param request
	 * @param response
	 */
	@RequestMapping("getBuyStockForTransform")
	@ResponseBody
	public Map<String, Object> getBuyStockForTransform(HttpServletRequest request, HttpServletResponse response ) {
		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("status", "failure");
		resultMap.put("dWareArea", "");
		resultMap.put("dStockCode", "");
		resultMap.put("dProxyName", "");
		resultMap.put("hiddenInfo", "");
		List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
		resultMap.put("products", resultList);
		int stockId = StringUtil.toInt(request.getParameter("stockId"));
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		AdminService admiService = new AdminService(IBaseService.CONN_IN_SERVICE,dbOp_slave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		WareService wareService = new WareService(dbOp_slave);
		try  {
			String condition = "";
			if(stockId!=0){
				if(service.getBuyStock("id="+stockId)==null){
					return resultMap;
				}
				BuyStockBean stock = service.getBuyStock("id="+stockId);
				String proxyName = wareService.getString("ssi.name", "buy_stock_product bsp, supplier_standard_info ssi", 
						"ssi.status = 1 and bsp.product_proxy_id = ssi.id and bsp.buy_stock_id = "+stockId);
				if (proxyName == null || proxyName.length() == 0) {
					proxyName = "无";
				}
				String tableString = "编号："+stock.getCode()+"<br/>";
				String wareA = stock.getArea() == ProductStockBean.AREA_BJ ? "北京" : stock.getArea() == ProductStockBean.AREA_GF ? "芳村" : stock.getArea() == ProductStockBean.AREA_ZC ? "增城" : stock.getArea() == ProductStockBean.AREA_WX ? "无锡" : "";
				tableString += "代理商：" + proxyName + "&nbsp;&nbsp;&nbsp;地区：" + wareA ;
				tableString += "</td>";
				tableString += "</tr>";
				tableString += "<table width='95%' border='1' style='border-collapse:collapse;' bordercolor='#D8D8D5'>";
				tableString += "<tr>";
				tableString += "<td>选择</td>";
				tableString += "<td>序号</td>";
				tableString += "<td>产品线</td>";
				tableString += "<td>产品编号</td>";
				tableString += "<td>产品名称</td>";
				tableString += "<td>原名称</td>";
				tableString += "<td>预计进货量(已入库量)</td>";
				tableString += "<td>进货前库存</td>";
				tableString += "</tr>";
				condition = "buy_stock_id = "+stockId;
				List buyStockProductList = service.getBuyStockProductList(condition, -1, -1, "id desc");
				Iterator iter = buyStockProductList.listIterator();
				int i = 0;
				while (iter.hasNext()) {
					BuyStockProductBean bpp = (BuyStockProductBean) iter.next();
					voProduct product = wareService.getProduct(bpp.getProductId());
					if(product == null){
						
					} else {
						i++;
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
						bpp.setStockinCount(stockinCount);
						bpp.setProduct(product);
						bpp.setProductLineName(proLineName);
						Map<String, String> map = new HashMap<String,String>();
						
						tableString+= "<tr";
						if(i%2==0){
						tableString+= " bgcolor='#EEE9D9'";
						}
						tableString +=">";
						
						tableString+= "<td><input type='checkbox' checked name='buyStockProductId' value='"+bpp.getId()+"'/></td>";
						map.put("check_option", "<input type='checkbox' checked name='buyStockProductId' value='"+bpp.getId()+"'/>");
						tableString+= "<td>" + i + "</td>";
						map.put("count", new Integer(i).toString());
						tableString+= "<td>"+bpp.getProductLineName() +"</td>";
						map.put("product_line", bpp.getProductLineName());
						tableString+= "<td><a href='../admin/fproduct.do?id="+bpp.getProduct().getId()+"' target='_blank'>"+bpp.getProduct().getCode()+"</a></td>";
						map.put("product_code", "<a href='../admin/fproduct.do?id="+bpp.getProduct().getId()+"' target='_blank'>"+bpp.getProduct().getCode()+"</a>");
						tableString+= "<td><a href='../admin/fproduct.do?id="+bpp.getProduct().getId()+"' target='_blank'>"+bpp.getProduct().getName()+"</a></td>";
						map.put("product_name", "<a href='../admin/fproduct.do?id="+bpp.getProduct().getId()+"' target='_blank'>"+bpp.getProduct().getName()+"</a>");
						tableString+= "<td><a href='../admin/fproduct.do?id="+bpp.getProduct().getId()+"' target='_blank'>"+bpp.getProduct().getOriname()+"</a></td>";
						map.put("ori_name", "<a href='../admin/fproduct.do?id="+bpp.getProduct().getId()+"' target='_blank'>"+bpp.getProduct().getOriname()+"</a>");
						tableString+= "<td>"+bpp.getBuyCount() +"("+bpp.getStockinCount() +")</td>";
						map.put("buy_stock_sum", bpp.getBuyCount() +"("+bpp.getStockinCount() +")");
						tableString+= "<td>";
						String wareAr = "";
						if(stock.getArea()==0){
							wareAr = new Integer( bpp.getProduct().getStock(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)).toString()+ "(" + new Integer(bpp.getProduct().getLockCount(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)).toString() + ")";
						}else if(stock.getArea()==1){
							wareAr = new Integer( bpp.getProduct().getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED)).toString()+ "(" + new Integer(bpp.getProduct().getLockCount(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED)).toString() + ")";
						}
						tableString += wareAr;
						map.put("stockin_stock", wareAr);
						tableString+= "</td>";
						tableString+= "	</tr>";
						resultList.add(map);
					}
				}
				
				request.setAttribute("buyStockProductList", buyStockProductList);
				request.setAttribute("proxyName", proxyName);
				request.setAttribute("stock", stock);
				tableString += "</table>";
				tableString += "<input type='hidden' name='stockId' value='"+stockId+"'/>";
				tableString += "<input type='hidden' name='buyOrderId' value='"+stock.getBuyOrderId()+"'/>";
				String hiddenInfo = "<input type='hidden' name='stockId' value='"+stockId+"'/>";
				hiddenInfo += "<input type='hidden' name='buyOrderId' value='"+stock.getBuyOrderId()+"'/>";
				resultMap.put("hiddenInfo", hiddenInfo);
				resultMap.put("status", "success");
				resultMap.put("dWareArea", wareA);
				resultMap.put("dStockCode", stock.getCode());
				resultMap.put("dProxyName", proxyName);
				resultMap.put("products", resultList);
				//map.put("tableString", tableString);
			}
		}finally{
			dbOp_slave.release();
		}
		return resultMap;
	}
	
	
	/**
	 * 作者：赵林
	 * 
	 * 创建日期：2013-08-21
	 * 
	 * 说明：转换 预计到货表->采购入库单
	 * @param request
	 * @param response
	 */
	@RequestMapping("transformToBuyStockin")
	public void transformToBuyStockin(HttpServletRequest request,
			HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(114);
		if(!viewAll){ 
			request.setAttribute("tip", "你无权转换采购入库！");
			request.setAttribute("result", "failure");
			return;
		}
		synchronized(buyStockinLock){
			int stockId = StringUtil.toInt(request.getParameter("stockId"));
			int buyOrderId = StringUtil.toInt(request.getParameter("buyOrderId"));
			String[] buyStockProductIds = request.getParameterValues("buyStockProductId");
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				service.getDbOp().startTransaction();
				BuyStockBean stock = service.getBuyStock("id=" + stockId);
				if(stock == null){    
					request.setAttribute("tip", "没有这个预计到货表");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				if (!service.updateBuyStock("transform_count = transform_count + 1" , "id = " + stockId)) {
					request.setAttribute("tip", "更新转换采购入库单次数失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				//采购入库单编号
				String code = service.generateBuyStockinCodeBref();
				
				BuyStockinBean stockin = new BuyStockinBean();
				stockin.setBuyStockId(stockId);
				stockin.setBuyOrderId(buyOrderId);
				stockin.setCreateDatetime(DateUtil.getNow());
				stockin.setConfirmDatetime(DateUtil.getNow());
				stockin.setStatus(BuyStockinBean.STATUS0);
				stockin.setCode(code);
				if(stock.getArea() == 0){
					stockin.setStockArea(ProductStockBean.AREA_BJ);
				}else if(stock.getArea()==1){
					stockin.setStockArea(ProductStockBean.AREA_GF);
				}else if(stock.getArea()==3){
					stockin.setStockArea(ProductStockBean.AREA_ZC);
				} else if(stock.getArea() == ProductStockBean.AREA_WX ) {
					stockin.setStockArea(ProductStockBean.AREA_WX);
				} 
				stockin.setStockType(ProductStockBean.STOCKTYPE_CHECK);
				stockin.setRemark("");
				stockin.setCreateUserId(user.getId());
				stockin.setSupplierId(stock.getSupplierId());//供应商id
				if (!service.addBuyStockin(stockin)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				
				if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}
				code = stockin.getCode();

				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(stockin.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("转换成采购入库单，来源预计到货表：" + stock.getCode());
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
				if( !service.addBuyAdminHistory(log) ) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return;
				}

				for(int i=0;i<buyStockProductIds.length;i++){
					String buyStockProductId = buyStockProductIds[i];
					BuyStockProductBean bsp = service.getBuyStockProduct("id="+buyStockProductId);
					BuyStockinProductBean bsip = new BuyStockinProductBean();
					voProduct product = wareService.getProduct(bsp.getProductId());
					ProductStockBean ps = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockin.getStockType() + " and area=" + stockin.getStockArea());
					int bsipId = service.getNumber("id", "buy_stockin_product", "max", "id > 0") + 1;
					bsip.setCreateDatetime(DateUtil.getNow());
					bsip.setConfirmDatetime(DateUtil.getNow());
					bsip.setId(bsipId);
					bsip.setBuyStockinId(stockin.getId());
					bsip.setStockInId(ps.getId());
					bsip.setProductCode(product.getCode());
					bsip.setProductId(product.getId());
					bsip.setRemark("");
					bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
					bsip.setPrice3(bsp.getPurchasePrice());
					bsip.setProductProxyId(bsp.getProductProxyId());
					bsip.setOriname(product.getOriname());

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
					bsip.setStockInCount((bsp.getBuyCount()-stockinCount)>0?(bsp.getBuyCount()-stockinCount):0);

					if (!service.addBuyStockinProduct(bsip)) {
						request.setAttribute("tip", "添加失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}

					//log记录
					log = new BuyAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(stockin.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("添加采购入库单商品["+product.getCode()+"]");
					log.setType(BuyAdminHistoryBean.TYPE_ADD);
					if( !service.addBuyAdminHistory(log) ) {
						request.setAttribute("tip", "添加失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return;
					}
				}

				service.getDbOp().commitTransaction();

				request.setAttribute("stockinId", Integer.valueOf(stockin.getId()));
			} finally {
				service.releaseAll();
			}
		}
	}
	
	/**
	 * 
	 * 作者：郝亚斌
	 * 
	 * 创建日期：2013-9-11
	 * 
	 * 说明：删除采购入库单
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping("deleteBuyStockin")
	public String deleteBuyStockin(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean auditing = group.isFlag(116);
		boolean bianji = group.isFlag(169);
		
		synchronized(buyStockinLock){
			int buyStockinId = StringUtil.StringToId(request.getParameter("buyStockinId"));
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			try {
				BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
				if(bean == null){
					request.setAttribute("tip", "没有这个采购入库单！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				if(bean.getStatus()==BuyStockinBean.STATUS3&&!(auditing&&bianji)){
					request.setAttribute("tip", "你没有权限修改这个采购入库单");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				if (bean.getStatus() != BuyStockinBean.STATUS0 && bean.getStatus() != BuyStockinBean.STATUS1) {
					request.setAttribute("tip", "该操作已处理，不能再删除！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				if(bean.getStatus() == BuyStockinBean.STATUS1 && service.getBuyStockinProductCount("buy_stockin_id="+bean.getId())>0){
					request.setAttribute("tip", "采购入库单中存在商品，不能删除！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				service.getDbOp().startTransaction();
				service.updateBuyStockin("status = "+BuyStockinBean.STATUS8, "id = "+buyStockinId);
				
				//log记录
				voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
				if (admin == null) {
					request.setAttribute("tip", "当前没有登录，添加失败！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(admin.getId());
				log.setAdminName(admin.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("删除采购入库");
				log.setType(BuyAdminHistoryBean.TYPE_DELETE);
				service.addBuyAdminHistory(log);
				service.updateBuyAdminHistory("deleted = 1", "log_type = " + BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN + " and log_id = " + bean.getId());
				service.getDbOp().commitTransaction();
			} finally {
				service.releaseAll();
			}
			return "forward:/admin/toBuyStockinList.mmx";
		}
	}


}
