package mmb.rec.stat.controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;

@Controller
@RequestMapping("/searchProductStockController")
public class SearchProductStockController {
	
	/**
	 * 一级分类
	 * 库存查询（有权限对应）
	 * 2013-9-23
	 * 朱爱林	
	 */
	@RequestMapping("/querySelectParentId1Permission")
	@ResponseBody
	public Object querySelectParentId1Permission(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		//产品线权限
		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(user);
		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(user);
		String catalogIdsTemp = "";
		if(!catalogIds2.equals("")){
			String[] splits = catalogIds2.split(",");
			for(int i=0;i<splits.length;i++){
				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
				}
			}
			catalogIds1 = catalogIdsTemp + catalogIds1;
			if(catalogIds1.endsWith(",")){
				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
			}
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> maps = new HashMap<String, String>();
		maps.put("id", "0");
		maps.put("text", "全部");
		maps.put("selected", "true");
		list.add(maps);
		try{
			HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
			List list2 = (List)map.get(Integer.valueOf(0));
			Iterator iter = list2.listIterator();
			while(iter.hasNext()){
				voCatalog catalog = (voCatalog)iter.next();
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					continue;
				}
				maps = new HashMap<String, String>();
				maps.put("id", catalog.getId()+"");
				maps.put("text", catalog.getName());
				list.add(maps);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return list;
	}
	/**
	 * 一级分类
	 * 分库库存查询（没权限对应）
	 * 2013-9-23
	 * 朱爱林	
	 */
	@RequestMapping("/querySelectParentId1NoPermission")
	@ResponseBody
	public Object querySelectParentId1NoPermission(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		//产品线权限
		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(user);
		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(user);
		String catalogIdsTemp = "";
		if(!catalogIds2.equals("")){
			String[] splits = catalogIds2.split(",");
			for(int i=0;i<splits.length;i++){
				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
				}
			}
			catalogIds1 = catalogIdsTemp + catalogIds1;
			if(catalogIds1.endsWith(",")){
				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
			}
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> maps = new HashMap<String, String>();
		maps.put("id", "0");
		maps.put("text", "全部");
		maps.put("selected", "true");
		list.add(maps);
		try{
			HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
			List list2 = (List)map.get(Integer.valueOf(0));
			Iterator iter = list2.listIterator();
			while(iter.hasNext()){
				voCatalog catalog = (voCatalog)iter.next();
				maps = new HashMap<String, String>();
				maps.put("id", catalog.getId()+"");
				maps.put("text", catalog.getName());
				list.add(maps);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return list;
	}
	/**
	 * 二级分类
	 * 2013-9-23
	 * 朱爱林	
	 */
	@RequestMapping("/querySelectParentId2")
	@ResponseBody
	public Object querySelectParentId2(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		int parentId1 = StringUtil.parstInt(StringUtil.convertNull(request.getParameter("parentId1")));
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> maps = new HashMap<String, String>();
		maps.put("id", "0");
		maps.put("text", "全部");
		maps.put("selected", "true");
		list.add(maps);
		
		List<Map<String,String>> list2 = new ArrayList<Map<String,String>>();
		list2.add(maps);
		//异常则返回空内容
		resultMap.put("parentId2", list);
		resultMap.put("parentId3", list2);
		try{
			
			HashMap secondMap = CatalogCache.getSecondMap();
			HashMap thirdMap = CatalogCache.getThirdMap();
			//二级
			List secondList = (List)secondMap.get(parentId1);
			voCatalog catalog = null;
			if(secondList==null){
//					response.getWriter().println("spts["+indexFirst+"][0] = new Option(\"不限\", \"0\")");
				maps = new HashMap<String, String>();
				maps.put("id", "0");
				maps.put("text", "不限");
				list.add(maps);
			}else{
				for(int j=0;j<secondList.size();j++){
					catalog = (voCatalog)secondList.get(j);
//						response.getWriter().println("spts["+indexFirst+"]["+indexSecond+"] = new Option(\""+catalog.getName()+"\", \""+catalog.getId()+"\")");
					maps = new HashMap<String, String>();
					maps.put("id", catalog.getId()+"");
					maps.put("text", catalog.getName());
					list.add(maps);
					//三级
					if(j==0){
						List thirdList = (List)thirdMap.get(Integer.valueOf(catalog.getId()));
						if(thirdList == null||thirdList.size()==0){
//							response.getWriter().println("tpts["+indexFirst+"]["+indexSecond+"][0] = new Option(\"不限\", \"0\")");
							maps = new HashMap<String, String>();
							maps.put("id", "0");
							maps.put("text", "不限");
							list2.add(maps);
						}else{
							for(int k=0;k<thirdList.size();k++){
								catalog = (voCatalog)thirdList.get(k);
//								response.getWriter().println("tpts["+indexFirst+"]["+indexSecond+"]["+k+"] = new Option(\""+catalog.getName()+"\", \""+catalog.getId()+"\")");
								maps = new HashMap<String, String>();
								maps.put("id", catalog.getId()+"");
								maps.put("text", catalog.getName());
								list2.add(maps);
							}
						}
					}
				}
			}
			resultMap.put("parentId2", list);
			resultMap.put("parentId3", list2);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 三级分类
	 * 2013-9-23
	 * 朱爱林	
	 */
	@RequestMapping("/querySelectParentId3")
	@ResponseBody
	public Object querySelectParentId3(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		int parentId2 = StringUtil.parstInt(StringUtil.convertNull(request.getParameter("parentId2")));
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> maps = new HashMap<String, String>();
		maps.put("id", "0");
		maps.put("text", "全部");
		maps.put("selected", "true");
		list.add(maps);
		try{
			
			HashMap thirdMap = CatalogCache.getThirdMap();
			voCatalog catalog = null;
			//三级
			List thirdList = (List)thirdMap.get(parentId2);
			if(thirdList == null||thirdList.size()==0){
//							response.getWriter().println("tpts["+indexFirst+"]["+indexSecond+"][0] = new Option(\"不限\", \"0\")");
				maps = new HashMap<String, String>();
				maps.put("id", "0");
				maps.put("text", "不限");
				list.add(maps);
			}else{
				for(int k=0;k<thirdList.size();k++){
					catalog = (voCatalog)thirdList.get(k);
//								response.getWriter().println("tpts["+indexFirst+"]["+indexSecond+"]["+k+"] = new Option(\""+catalog.getName()+"\", \""+catalog.getId()+"\")");
					maps = new HashMap<String, String>();
					maps.put("id", catalog.getId()+"");
					maps.put("text", catalog.getName());
					list.add(maps);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return list;
	}
	/**
	 * 代理商下拉选
	 * 2013-9-23
	 * 朱爱林	
	 */
	@RequestMapping("/querySelectProxy")
	@ResponseBody
	public Object querySelectProxy(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> maps = new HashMap<String, String>();
		maps.put("id", "0");
		maps.put("text", "所有");
		maps.put("selected", "true");
		list.add(maps);
		try{
			ResultSet rs = service.getDbOp().executeQuery("select id, name from supplier_standard_info where status=1 order by id");
			while(rs.next()){
				maps = new HashMap<String, String>();
				maps.put("id", rs.getInt(1)+"");
				maps.put("text", rs.getString(2));
				list.add(maps);
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return list;
	}
	/**
	 * 以下是旧版的库存查询，分库库存查询
	 */
	/**
	 * 查询货位库存
	 * @param request
	 * @param response
	 * @return
	 * 2013/10/15
	 * 朱爱林
	 * @throws Exception 
	 */
	@RequestMapping("/searchProductStock2")
	@ResponseBody
	public Object searchProductStock2(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		if(adminUser == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = adminUser.getGroup();
		request.setCharacterEncoding("utf-8");
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商	
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
	
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类2
		String toExcel =StringUtil.dealParam(request.getParameter("toExcel"));//编号
		
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
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and a.code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(proxy>0){
			condition.append(" and d.id="+proxy);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}else{
			condition.append(" and (a.parent_id1 in ("+catalogIds1+")");
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}else{
			if(parentId1 > 0){
				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
					condition.append(" and a.parent_id2 in ("+catalogIds2+")");
				}
			}else{
				condition.append(" or a.parent_id2 in ("+catalogIds2+"))");
			}
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
//		else{
//			if(parentId1 > 0){
//				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
//					condition.append(" and a.parent_id3 in ("+catalogIds2+")");
//				}
//			}else{
//				condition.append(" or a.parent_id3 in ("+catalogIds2+"))");
//			}
//		}	 
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService service = new WareService(dbOperation);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try {
			BigDecimal totalPrice = new BigDecimal(0.0d);
			List totalList = service.getProductList2(condition.toString(), -1, -1, "a.id asc");
	        Iterator it = totalList.listIterator();
	        BigDecimal stockAll = null;
	        BigDecimal lockCountAll = null;
	        BigDecimal price5 = null;
	        //float share = (pp.divide(ss, 4, BigDecimal.ROUND_HALF_DOWN).floatValue())*100;
			while(it.hasNext()){
				voProduct product = (voProduct)it.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
				stockAll = new BigDecimal(product.getStockAll());
				lockCountAll = new BigDecimal(product.getLockCountAll());
				price5 = new BigDecimal(product.getPrice5());
				totalPrice = totalPrice.add((stockAll.add(lockCountAll)).multiply(price5));
				//totalPrice += (product.getStockAll() + product.getLockCountAll())*product.getPrice5();
			}
			request.setAttribute("totalList", totalList);
//			resultMap.put("total","0");
			resultMap.put("rows",lists);

			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;
			
			request.setAttribute("totalList", totalList);//计算出要显示的总数
			 //总数
	        int totalCount = totalList.size();
	        resultMap.put("total", totalCount);
	        //页码
	        //为了分页显示
	        List list = service.getProductList2(condition.toString(), start , rows, "a.id asc");
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
			}
			voProduct item = null;
			String path = request.getContextPath();
			for(int i=0;i<list.size();i++){
				map = new HashMap<String, String>();
				item = (voProduct) list.get(i);
				map.put("code", "<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">"+item.getCode()+"</a>");//编号fproduct.do
				map.put("name", "<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">"+item.getOriname()+"</a>");//名称
				map.put("stock_count", item.getStockAll() + item.getLockCountAll()+"");//库存总数
				//待验库
				map.put("dyk_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK)+"");//
				map.put("dyk_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK)+"");//
				map.put("dyk_wx", item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_CHECK)+"");//
				//合格库
				map.put("hg_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				map.put("hg_gs", item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				map.put("hg_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				map.put("hg_wx", item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				//退货库
				map.put("th_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN)+"");//
				map.put("th_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN)+"");//
				map.put("th_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)+"");//
				map.put("th_wx", item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_RETURN)+"");//
				//返厂库
				map.put("fc_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK)+"");//
				map.put("fc_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_BACK)+"");//
				map.put("fc_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK)+"");//
				//维修库
				map.put("wx_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR)+"");//
				map.put("wx_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_REPAIR)+"");//
				map.put("wx_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR)+"");//
				//残次品库
				map.put("ccp_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE)+"");//
				map.put("ccp_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE)+"");//
				map.put("ccp_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE)+"");//
				//样品库
				map.put("yp_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE)+"");//
				map.put("yp_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE)+"");//
				map.put("yp_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE)+"");//
				//售后库
				map.put("sh_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_AFTER_SALE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_AFTER_SALE)+"");//
				//标准库存
				map.put("stockstandard_bj", item.getStockStandardBj()+"");//
				map.put("stockstandard_fc", item.getStockStandardGd()+"");//
				
				map.put("statusName", item.getStatusName());//
				map.put("parent1_name", item.getParentId1()==0? "无" : item.getParent1().getName());//
				map.put("parent2_name", item.getParentId2()==0? "无" : item.getParent2().getName());//
				map.put("parent3_name", item.getParentId3()==0? "无" : item.getParent3().getName());//
				map.put("rank", item.getRank()+"");//
				map.put("query", "<a href=\""+path+"/admin/productStock/stockCardList.jsp?productCode="+StringUtil.dealParam(item.getCode())+"\" target=\"_blank\">查</a>");//
				lists.add(map);
			}
			resultMap.put("rows", lists);
			if(!group.isFlag(86)){
				resultMap.put("boo","false");
			}else{
				resultMap.put("boo","true");
				if(totalPrice.toString().split("\\.").length>=2&&totalPrice.toString().split("\\.")[1].length()>2){
					resultMap.put("totalPrice", totalPrice.toString().substring(0, totalPrice.toString().indexOf(".")+3));
				}else{
					resultMap.put("totalPrice", totalPrice.toString());
				}
			}
		} finally {
			psService.releaseAll();
		}
		//导出数据
		if(toExcel!=null&&toExcel.equals("to")){
//			return mapping.findForward("toExcel");
		} 
		long end = System.currentTimeMillis();
		return resultMap;
		//显示数据
//		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
	/**
	 * 查询货位库存导出
	 * @param request
	 * @param response
	 * @return
	 * 2013/10/15
	 * 朱爱林
	 * @throws Exception 
	 */
	@RequestMapping("/searchProductStockExport2")
	public String searchProductStockExport2(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		if(adminUser == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		request.setCharacterEncoding("utf-8");
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商	
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
		
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类2
		
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
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and a.code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(proxy>0){
			condition.append(" and d.id="+proxy);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}else{
			condition.append(" and (a.parent_id1 in ("+catalogIds1+")");
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}else{
			if(parentId1 > 0){
				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
					condition.append(" and a.parent_id2 in ("+catalogIds2+")");
				}
			}else{
				condition.append(" or a.parent_id2 in ("+catalogIds2+"))");
			}
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
//		else{
//			if(parentId1 > 0){
//				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
//					condition.append(" and a.parent_id3 in ("+catalogIds2+")");
//				}
//			}else{
//				condition.append(" or a.parent_id3 in ("+catalogIds2+"))");
//			}
//		}	 
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService service = new WareService(dbOperation);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		
		try {
			List totalList = service.getProductList2(condition.toString(), -1, -1, "a.id asc");
			Iterator it = totalList.listIterator();
			//float share = (pp.divide(ss, 4, BigDecimal.ROUND_HALF_DOWN).floatValue())*100;
			while(it.hasNext()){
				voProduct product = (voProduct)it.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
				//totalPrice += (product.getStockAll() + product.getLockCountAll())*product.getPrice5();
			}
			request.setAttribute("totalList", totalList);
//			resultMap.put("total","0");
		} finally {
			psService.releaseAll();
		}
		return "forward:/admin/rec/stat/productStock/allProductsStockSubExcel.jsp";
	}
	
	@RequestMapping("/searchProductStockAmount2")
	public void searchProductStockAmount2(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(adminUser == null){
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return;
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService wareService = new WareService(dbOperation);
		UserGroupBean group = adminUser.getGroup();
		String statusIds = "-1";
		if(!"".equals(ProductLinePermissionCache.getProductLineSupplierIds(adminUser))){
			statusIds = ProductLinePermissionCache.getProductLineSupplierIds(adminUser);
		}
		List supplierList = wareService.getSelects("supplier_standard_info ", "where status = 1 and id in(" + statusIds + ")");
		request.setAttribute("supplierList", supplierList);
		String path = request.getContextPath();
		request.setCharacterEncoding("utf-8");
		
		int proxy = 0;	// 供应商id
		int suppliertext = StringUtil.StringToId(request.getParameter("suppliertext"));//供应商名称
		request.setAttribute("proxy", String.valueOf(proxy));
		
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
		int stockType =StringUtil.toInt(request.getParameter("stockType"));//库类型
		//System.out.println("---->"+stockType);
		int stockArea =StringUtil.toInt(request.getParameter("stockArea"));//库区域
		request.setAttribute("stockType", stockType);
		request.setAttribute("stockArea", stockArea);
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类3
		String toExcel =StringUtil.dealParam(request.getParameter("toExcel"));//编号
		
		//产品线权限限制
//		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
//		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
//		String catalogIdsTemp = "";
//		if(!catalogIds2.equals("")){
//			String[] splits = catalogIds2.split(",");
//			for(int i=0;i<splits.length;i++){
//				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
//				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
//					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
//				}
//			}
//			if(catalogIds1.endsWith(",")){
//				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
//			}
//		}
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(suppliertext>0){
			condition.append(" and d.id="+suppliertext);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
		if(stockType>=0){
			condition.append(" and ps.type="+stockType);
		}
		if(stockArea>=0){
			condition.append(" and ps.area="+stockArea);
		}

		//得出已处理中和待处理状态的产品集合
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		StringBuilder columns = new StringBuilder();//表头
		StringBuilder rows = new StringBuilder();//表数据
		try {
			List list = wareService.getProductList2(condition.toString(), -1,-1, "a.id asc");
		    //Hashtable ht=service.getOrderStockProductList(list);	
		    //request.setAttribute("ht", ht);
			
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));	
			}
			
			request.setAttribute("productList", list);
			request.setAttribute("status",""+status);
			request.setAttribute("count", ""+list.size());
			request.setAttribute("date","status="+status+"&proxy="+proxy+"&code="+code+"&name="+name+"&parentId1="+parentId1+"&parentId2="+parentId2+"&parentId3="+parentId3);
			columns.append("[[");
			
			columns.append("{field:'code',title:'产品编号',align:'center'},{field:'name',title:'小店名称',align:'center'},{field:'oriname',title:'产品原名称',align:'center'},");
			//field:  city_count,city_amount
			//待验库
			if(stockType==1){
				if(stockArea==0||stockArea==-1){
					columns.append("{field:'dy_bj',title:'北京',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'dy_bj_count',title:'金额(北库)',align:'center'},");
					}
				}
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'dy_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'dy_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
				if(stockArea==3||stockArea==-1){
					columns.append("{field:'dy_zc',title:'增城',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'dy_zc_count',title:'金额(增城)',align:'center'},");
					}
				}
				if(stockArea==4||stockArea==-1){
					columns.append("{field:'dy_wx',title:'无锡',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'dy_wx_count',title:'金额(无锡)',align:'center'},");
					}
				}
			}else if(stockType==0){//合格库
				if(stockArea==0||stockArea==-1){
					columns.append("{field:'hg_bj',title:'北京',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_bj_count',title:'金额(北库)',align:'center'},");
					}
				}
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'hg_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
				if(stockArea==2||stockArea==-1){
					columns.append("{field:'hg_gs',title:'广速',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_gs_count',title:'金额(广速)',align:'center'},");
					}
				}
				if(stockArea==3||stockArea==-1){
					columns.append("{field:'hg_zc',title:'增城',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_zc_count',title:'金额(增城)',align:'center'},");
					}
				}
				if(stockArea==4||stockArea==-1){
					columns.append("{field:'hg_wx',title:'无锡',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_wx_count',title:'金额(无锡)',align:'center'},");
					}
				}
			}else if(stockType==6){//样品库
				if(stockArea==0||stockArea==-1){
					columns.append("{field:'hg_bj',title:'北京',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_bj_count',title:'金额(北库)',align:'center'},");
					}
				}
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'hg_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
				if(stockArea==3||stockArea==-1){
					columns.append("{field:'hg_zc',title:'增城',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'hg_zc_count',title:'金额(增城)',align:'center'},");
					}
				}
			}else if(stockType==4){//退货库
				if(stockArea==0||stockArea==-1){
					columns.append("{field:'th_bj',title:'北京',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'th_bj_count',title:'金额(北库)',align:'center'},");
					}
				}
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'th_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'th_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
				if(stockArea==2||stockArea==-1){
					columns.append("{field:'th_gs',title:'广速',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'th_gs_count',title:'金额(广速)',align:'center'},");
					}
				}
				if(stockArea==3||stockArea==-1){
					columns.append("{field:'th_zc',title:'增城',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'th_zc_count',title:'金额(增城)',align:'center'},");
					}
				}
				if(stockArea==4||stockArea==-1){
					columns.append("{field:'th_wx',title:'无锡',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'th_wx_count',title:'金额(无锡)',align:'center'},");
					}
				}
			}else if(stockType==3){//返厂库
				if(stockArea==0||stockArea==-1){
					columns.append("{field:'fc_bj',title:'北京',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'fc_bj_count',title:'金额(北库)',align:'center'},");
					}
				}
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'fc_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'fc_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
				if(stockArea==3||stockArea==-1){
					columns.append("{field:'fc_zc',title:'增城',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'fc_zc_count',title:'金额(增城)',align:'center'},");
					}
				}
				if(stockArea==4||stockArea==-1){
					columns.append("{field:'fc_wx',title:'无锡',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'fc_wx_count',title:'金额(无锡)',align:'center'},");
					}
				}
			}else if(stockType==2){//维修库
				if(stockArea==0||stockArea==-1){
					columns.append("{field:'wx_bj',title:'北京',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'wx_bj_count',title:'金额(北库)',align:'center'},");
					}
				}
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'wx_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'wx_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
				if(stockArea==3||stockArea==-1){
					columns.append("{field:'wx_zc',title:'增城',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'wx_zc_count',title:'金额(增城)',align:'center'},");
					}
				}
			}else if(stockType==5){//残次品库
				if(stockArea==0||stockArea==-1){
					columns.append("{field:'cc_bj',title:'北京',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'cc_bj_count',title:'金额(北库)',align:'center'},");
					}
				}
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'cc_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'cc_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
				if(stockArea==2||stockArea==-1){
					columns.append("{field:'cc_gs',title:'广速',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'cc_gs_count',title:'金额(广速)',align:'center'},");
					}
				}
				if(stockArea==3||stockArea==-1){
					columns.append("{field:'cc_zc',title:'增城',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'cc_zc_count',title:'金额(增城)',align:'center'},");
					}
				}
			}else if(stockType==9){//售后库
				if(stockArea==1||stockArea==-1){
					columns.append("{field:'sh_fc',title:'芳村',align:'center'},");
					if(group.isFlag(95)){
						columns.append("{field:'sh_fc_count',title:'金额(芳村)',align:'center'},");
					}
				}
			}
			columns.append("{field:'stock_count',title:'库存总数',align:'center'},")
				.append(group.isFlag(95)==true?"{field:'stock_amount',title:'总金额',align:'center'},":"")
				.append("{field:'status_name',title:'状态',align:'center'},{field:'parent_id1',title:'一级分类',align:'center'},{field:'parent_id2',title:'二级分类',align:'center'},")
				.append("{field:'parent_id3',title:'三级分类',align:'center'},{field:'query',title:'库存记录',align:'center'}");
			columns.append("]]");
			double total = 0.0d;//记录总金额
			int city_count = 0;
			double city_amount = 0.0d;
			int stock_count = 0;//库存总数
			double stock_amount = 0.0d;//总金额
			//city_count city_amount
			//计算总金额的中间变量  stockcounts  price5
			BigDecimal stockcounts = null;
			BigDecimal price5 = null;
			rows.append("[");//[{:,:},]
			for(int i=0;i<list.size();i++){
				voProduct item = (voProduct) list.get(i);
				rows.append("{'code':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getCode())+"</a>',")
					.append("'name':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getName())+"</a>',")
					.append("'oriname':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getOriname())+"</a>',");
				//待验库
				if(stockType==1){
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
							stockcounts = new BigDecimal(item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1) + item.getStock(3,1) + item.getLockCount(3,1) + item.getStock(4,1) + item.getLockCount(4,1));
							price5 = new BigDecimal(item.getPrice5());
//							total=new BigDecimal(total).add(new BigDecimal((item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1) + item.getStock(3,1) + item.getLockCount(3,1) + item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
						}else{
//							total=new BigDecimal(total).add((item.getStock(stockArea,1) + item.getLockCount(stockArea,1))*item.getPrice5());
							stockcounts = new BigDecimal(item.getStock(stockArea,1) + item.getLockCount(stockArea,1));
							price5 = new BigDecimal(item.getPrice5());
							
						}
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(1,1) + item.getLockCount(1,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,1) + item.getLockCount(0,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'dy_bj':'").append(city_count+"',")
						.append("'dy_bj_count':'").append(city_amount+"',");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(0,1) + item.getLockCount(0,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,1) + item.getLockCount(1,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'dy_fc':'").append(city_count+"',")
						.append("'dy_fc_count':'").append(city_amount+"',");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,1) + item.getLockCount(3,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,1) + item.getLockCount(3,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'dy_zc':'").append(city_count+"',")
						.append("'dy_zc_count':'").append(city_amount+"',");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,1) + item.getLockCount(4,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'dy_wx':'").append(city_count+"',")
						.append("'dy_wx_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//						.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1)+ item.getStock(3,1) + item.getLockCount(3,1)+ item.getStock(4,1) + item.getLockCount(4,1);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new BigDecimal((item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1)+ item.getStock(3,1) + item.getLockCount(3,1)+ item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");
				}else if(stockType==0){//合格库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=new BigDecimal(total).add(new BigDecimal((item.getStock(0,0) + item.getLockCount(0,0) + item.getStock(1,0) + item.getLockCount(1,0) + item.getStock(2,0) + item.getLockCount(2,0) + item.getStock(3,0) + item.getLockCount(3,0) + item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
							stockcounts = new BigDecimal(item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0));
						}else{
//							total=total+(item.getStock(stockArea,0) + item.getLockCount(stockArea,0))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,0) + item.getLockCount(stockArea,0));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,0) + item.getLockCount(0,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,0) + item.getLockCount(0,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_bj':'").append(city_count+"',")
						.append("'hg_bj_count':'").append(city_amount+"',");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,0) + item.getLockCount(1,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,0) + item.getLockCount(1,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_fc':'").append(city_count+"',")
						.append("'hg_fc_count':'").append(city_amount+"',");
					}
					if(stockArea==2||stockArea==-1){
						city_count = item.getStock(2,0) + item.getLockCount(2,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(2,0) + item.getLockCount(2,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_gs':'").append(city_count+"',")
						.append("'hg_gs_count':'").append(city_amount+"',");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,0) + item.getLockCount(3,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,0) + item.getLockCount(3,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_zc':'").append(city_count+"',")
						.append("'hg_zc_count':'").append(city_amount+"',");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,0) + item.getLockCount(4,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_wx':'").append(city_count+"',")
						.append("'hg_wx_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");
				}else if(stockType==6){//样品库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,1))*item.getPrice5();
							stockcounts = new BigDecimal((item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6)));
						}else{
//							total=total+(item.getStock(stockArea,6) + item.getLockCount(stockArea,6))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,6) + item.getLockCount(stockArea,6));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,6) + item.getLockCount(0,6);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,6) + item.getLockCount(0,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_bj':'").append(city_count+"',")
						.append("'hg_bj_count':'").append(city_amount+"',");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,6) + item.getLockCount(1,6);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,6) + item.getLockCount(1,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_fc':'").append(city_count+"',")
						.append("'hg_fc_count':'").append(city_amount+"',");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,6) + item.getLockCount(3,6);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,6) + item.getLockCount(3,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'hg_zc':'").append(city_count+"',")
						.append("'hg_zc_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");
				}else if(stockType==4){//退货库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4) + item.getStock(3,4) + item.getLockCount(3,4) + item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5();
							stockcounts = new BigDecimal((item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4)));
						}else{
//							total=total+(item.getStock(stockArea,4) + item.getLockCount(stockArea,4))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,4) + item.getLockCount(stockArea,4));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			        }
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,4) + item.getLockCount(0,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,4) + item.getLockCount(0,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'th_bj':'").append(city_count+"',")
						.append("'th_bj_count':'").append(city_amount+"',");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,4) + item.getLockCount(1,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,4) + item.getLockCount(1,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'th_fc':'").append(city_count+"',")
						.append("'th_fc_count':'").append(city_amount+"',");
					}
					if(stockArea==2||stockArea==-1){
						city_count = item.getStock(2,4) + item.getLockCount(2,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(2,4) + item.getLockCount(2,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'th_gs':'").append(city_count+"',")
						.append("'th_gs_count':'").append(city_amount+"',");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,4) + item.getLockCount(3,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,4) + item.getLockCount(3,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'th_zc':'").append(city_count+"',")
						.append("'th_zc_count':'").append(city_amount+"',");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,4) + item.getLockCount(4,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'th_wx':'").append(city_count+"',")
						.append("'th_wx_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");
				}else if(stockType==3){//返厂库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3) + item.getStock(3,3) + item.getLockCount(3,3) + item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3)+ item.getStock(4,3) + item.getLockCount(4,3));
						}else{
//							total=total+(item.getStock(stockArea,3) + item.getLockCount(stockArea,3))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,3) + item.getLockCount(stockArea,3));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,3) + item.getLockCount(0,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,3) + item.getLockCount(0,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'fc_bj':'").append(city_count+"',")
						.append("'fc_bj_count':'").append(city_amount+"',");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,3) + item.getLockCount(1,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,3) + item.getLockCount(1,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'fc_fc':'").append(city_count+"',")
						.append("'fc_fc_count':'").append(city_amount+"',");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,3) + item.getLockCount(3,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,3) + item.getLockCount(3,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'fc_zc':'").append(city_count+"',")
						.append("'fc_zc_count':'").append(city_amount+"',");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,3) + item.getLockCount(4,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'fc_wx':'").append(city_count+"',")
						.append("'fc_wx_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3)+ item.getStock(4,3) + item.getLockCount(4,3);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3)+ item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");
				}else if(stockType==2){//维修库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2));
						}else{
//							total=total+(item.getStock(stockArea,2) + item.getLockCount(stockArea,2))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,2) + item.getLockCount(stockArea,2));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,2) + item.getLockCount(0,2);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,2) + item.getLockCount(0,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'wx_bj':'").append(city_count+"',")
						.append("'wx_bj_count':'").append(city_amount+"',");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,2) + item.getLockCount(1,2);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,2) + item.getLockCount(1,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'wx_fc':'").append(city_count+"',")
						.append("'wx_fc_count':'").append(city_amount+"',");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,2) + item.getLockCount(3,2);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,3) + item.getLockCount(3,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'wx_zc':'").append(city_count+"',")
						.append("'wx_zc_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");	
				}else if(stockType==5){//残次品库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5) + item.getStock(3,5) + item.getLockCount(3,5))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5));
						}else{
//							total=total+(item.getStock(stockArea,5) + item.getLockCount(stockArea,5))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,5) + item.getLockCount(stockArea,5));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			        }
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,5) + item.getLockCount(0,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,5) + item.getLockCount(0,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'cc_bj':'").append(city_count+"',")
						.append("'cc_bj_count':'").append(city_amount+"',");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,5) + item.getLockCount(1,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,5) + item.getLockCount(1,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'cc_fc':'").append(city_count+"',")
						.append("'cc_fc_count':'").append(city_amount+"',");
					}
					if(stockArea==2||stockArea==-1){
						city_count = item.getStock(2,5) + item.getLockCount(2,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(2,5) + item.getLockCount(2,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'cc_gs':'").append(city_count+"',")
						.append("'cc_gs_count':'").append(city_amount+"',");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,5) + item.getLockCount(3,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,5) + item.getLockCount(3,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'cc_zc':'").append(city_count+"',")
						.append("'cc_zc_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");
				}else if(stockType==9){//售后库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,9) + item.getLockCount(0,9) + item.getStock(1,9) + item.getLockCount(1,9) + item.getStock(3,9) + item.getLockCount(3,9))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(1,9) + item.getLockCount(1,9));
						}else{
//							total=total+(item.getStock(stockArea,9) + item.getLockCount(stockArea,9))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,9) + item.getLockCount(stockArea,9));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,9) + item.getLockCount(1,9);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,9) + item.getLockCount(1,9))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows.append("'sh_fc':'").append(city_count+"',")
						.append("'sh_fc_count':'").append(city_amount+"',");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(1,9) + item.getLockCount(1,9);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(1,9) + item.getLockCount(1,9))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows.append("'stock_count':'").append(stock_count+"',")
						.append("'stock_amount':'").append(stock_amount+"',");
				}
				rows.append("'status_name':'").append(item.getStatusName()+"',")
					.append("'parent_id1':'").append((item.getParentId1()==0  ? "无" : item.getParent1().getName())+"',")
					.append("'parent_id2':'").append((item.getParentId2()==0  ? "无" : item.getParent2().getName())+"',")
					.append("'parent_id3':'").append((item.getParentId3()==0  ? "无" : item.getParent3().getName())+"',")
					.append("'query':'").append("<a href=\""+path+"/admin/productStock/stockCardList.jsp?productCode="+item.getCode()+"\" target=\"_blank\">查</a>'},");
			}
			if(list.size()!=0){
				rows.deleteCharAt(rows.length()-1);
			}
			rows.append("]");
			boolean boo = group.isFlag(95);
			String re = "{\'rows\':"+rows.toString()+",\'columns\':"+columns.toString()+",\'totalPrice\':\'"+new  BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"\',\'size\':\'"+list.size()+"\',\'boo\':\'"+boo+"\'}";
			response.getWriter().write(re);
		} finally {
			wareService.releaseAll();
			psService.releaseAll();
		}
		
	}
	/**
	 * 查询货位库存导出
	 * @param request
	 * @param response
	 * @return
	 * 2013/10/16
	 * 朱爱林
	 * @throws Exception 
	 */
	@RequestMapping("/searchProductStockAmountExport2")
	public String searchProductStockAmountExport2(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(adminUser == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService wareService = new WareService(dbOperation);
		UserGroupBean group = adminUser.getGroup();
		String statusIds = "-1";
		if(!"".equals(ProductLinePermissionCache.getProductLineSupplierIds(adminUser))){
			statusIds = ProductLinePermissionCache.getProductLineSupplierIds(adminUser);
		}
		List supplierList = wareService.getSelects("supplier_standard_info ", "where status = 1 and id in(" + statusIds + ")");
		request.setAttribute("supplierList", supplierList);
		request.setCharacterEncoding("utf-8");
		
		int proxy = 0;	// 供应商id
		int suppliertext = StringUtil.StringToId(request.getParameter("suppliertext"));//供应商名称
		request.setAttribute("proxy", String.valueOf(proxy));
		
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
		int stockType =StringUtil.toInt(request.getParameter("stockType"));//库类型
		//System.out.println("---->"+stockType);
		int stockArea =StringUtil.toInt(request.getParameter("stockArea"));//库区域
		request.setAttribute("stockType", stockType);
		request.setAttribute("stockArea", stockArea);
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类3
		String toExcel =StringUtil.dealParam(request.getParameter("toExcel"));//编号
		
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(suppliertext>0){
			condition.append(" and d.id="+suppliertext);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
		if(stockType>=0){
			condition.append(" and ps.type="+stockType);
		}
		if(stockArea>=0){
			condition.append(" and ps.area="+stockArea);
		}

		//得出已处理中和待处理状态的产品集合
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			List list = wareService.getProductList2(condition.toString(), -1,-1, "a.id asc");
		    //Hashtable ht=service.getOrderStockProductList(list);	
		    //request.setAttribute("ht", ht);
			
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));	
			}
			SearchProductStockExcel excel = new SearchProductStockExcel(SearchProductStockExcel.XSSF);
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header0 = new ArrayList<String>();//表头
			ArrayList<String> header = new ArrayList<String>();//表头
			//产品编号 	小店名称 	产品原名称
			header.add("产品编号");
			header.add("小店名称");
			header.add("产品原名称");
			
			//待验库
			if(stockType==1){
				if(stockArea==0||stockArea==-1){
					header.add("北京");
					if(group.isFlag(95)){
						header.add("金额(北库)");
					}
				}
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
				if(stockArea==3||stockArea==-1){
					header.add("增城");
					if(group.isFlag(95)){
						header.add("金额(增城)");
					}
				}
				if(stockArea==4||stockArea==-1){
					header.add("无锡");
					if(group.isFlag(95)){
						header.add("金额(无锡)");
					}
				}
			}else if(stockType==0){//合格库
				if(stockArea==0||stockArea==-1){
					header.add("北京");
					if(group.isFlag(95)){
						header.add("金额(北库)");
					}
				}
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
				if(stockArea==2||stockArea==-1){
					header.add("广速");
					if(group.isFlag(95)){
						header.add("金额(广速)");
					}
				}
				if(stockArea==3||stockArea==-1){
					header.add("增城");
					if(group.isFlag(95)){
						header.add("金额(增城)");
					}
				}
				if(stockArea==4||stockArea==-1){
					header.add("无锡");
					if(group.isFlag(95)){
						header.add("金额(无锡)");
					}
				}
			}else if(stockType==6){//样品库
				if(stockArea==0||stockArea==-1){
					header.add("北京");
					if(group.isFlag(95)){
						header.add("金额(北库)");
					}
				}
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
				if(stockArea==3||stockArea==-1){
					header.add("增城");
					if(group.isFlag(95)){
						header.add("金额(增城)");
					}
				}
			}else if(stockType==4){//退货库
				if(stockArea==0||stockArea==-1){
					header.add("北京");
					if(group.isFlag(95)){
						header.add("金额(北库)");
					}
				}
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
				if(stockArea==2||stockArea==-1){
					header.add("广速");
					if(group.isFlag(95)){
						header.add("金额(广速)");
					}
				}
				if(stockArea==3||stockArea==-1){
					header.add("增城");
					if(group.isFlag(95)){
						header.add("金额(增城)");
					}
				}
				if(stockArea==4||stockArea==-1){
					header.add("无锡");
					if(group.isFlag(95)){
						header.add("金额(无锡)");
					}
				}
			}else if(stockType==3){//返厂库
				if(stockArea==0||stockArea==-1){
					header.add("北京");
					if(group.isFlag(95)){
						header.add("金额(北库)");
					}
				}
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
				if(stockArea==3||stockArea==-1){
					header.add("增城");
					if(group.isFlag(95)){
						header.add("金额(增城)");
					}
				}
				if(stockArea==4||stockArea==-1){
					header.add("无锡");
					if(group.isFlag(95)){
						header.add("金额(无锡)");
					}
				}
			}else if(stockType==2){//维修库
				if(stockArea==0||stockArea==-1){
					header.add("北京");
					if(group.isFlag(95)){
						header.add("金额(北库)");
					}
				}
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
				if(stockArea==3||stockArea==-1){
					header.add("增城");
					if(group.isFlag(95)){
						header.add("金额(增城)");
					}
				}
			}else if(stockType==5){//残次品库
				if(stockArea==0||stockArea==-1){
					header.add("北京");
					if(group.isFlag(95)){
						header.add("金额(北库)");
					}
				}
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
				if(stockArea==2||stockArea==-1){
					header.add("广速");
					if(group.isFlag(95)){
						header.add("金额(广速)");
					}
				}
				if(stockArea==3||stockArea==-1){
					header.add("增城");
					if(group.isFlag(95)){
						header.add("金额(增城)");
					}
				}
			}else if(stockType==9){//售后库
				if(stockArea==1||stockArea==-1){
					header.add("芳村");
					if(group.isFlag(95)){
						header.add("金额(芳村)");
					}
				}
			}
			
			header.add("库存总数");
			header.add("总金额");
			header.add("状态");
			header.add("一级分类");
			header.add("二级分类");
			header.add("三级分类");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			ArrayList<String> body = new ArrayList<String>();//数据
			
			double total = 0.0d;//记录总金额
			int city_count = 0;
			double city_amount = 0.0d;
			int stock_count = 0;//库存总数
			double stock_amount = 0.0d;//总金额
			//city_count city_amount
			//计算总金额的中间变量  stockcounts  price5
			BigDecimal stockcounts = null;
			BigDecimal price5 = null;
			for(int i=0;i<list.size();i++){
				body = new ArrayList<String>();//数据
				voProduct item = (voProduct) list.get(i);
				body.add(item.getCode());
				body.add(item.getName());
				body.add(item.getOriname());
				//待验库
				if(stockType==1){
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
							stockcounts = new BigDecimal(item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1) + item.getStock(3,1) + item.getLockCount(3,1) + item.getStock(4,1) + item.getLockCount(4,1));
							price5 = new BigDecimal(item.getPrice5());
//							total=new BigDecimal(total).add(new BigDecimal((item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1) + item.getStock(3,1) + item.getLockCount(3,1) + item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
						}else{
//							total=new BigDecimal(total).add((item.getStock(stockArea,1) + item.getLockCount(stockArea,1))*item.getPrice5());
							stockcounts = new BigDecimal(item.getStock(stockArea,1) + item.getLockCount(stockArea,1));
							price5 = new BigDecimal(item.getPrice5());
							
						}
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(1,1) + item.getLockCount(1,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,1) + item.getLockCount(0,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(0,1) + item.getLockCount(0,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,1) + item.getLockCount(1,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,1) + item.getLockCount(3,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,1) + item.getLockCount(3,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,1) + item.getLockCount(4,1);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//						.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1)+ item.getStock(3,1) + item.getLockCount(3,1)+ item.getStock(4,1) + item.getLockCount(4,1);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new BigDecimal((item.getStock(0,1) + item.getLockCount(0,1) + item.getStock(1,1) + item.getLockCount(1,1)+ item.getStock(3,1) + item.getLockCount(3,1)+ item.getStock(4,1) + item.getLockCount(4,1))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}else if(stockType==0){//合格库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=new BigDecimal(total).add(new BigDecimal((item.getStock(0,0) + item.getLockCount(0,0) + item.getStock(1,0) + item.getLockCount(1,0) + item.getStock(2,0) + item.getLockCount(2,0) + item.getStock(3,0) + item.getLockCount(3,0) + item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
							stockcounts = new BigDecimal(item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0));
						}else{
//							total=total+(item.getStock(stockArea,0) + item.getLockCount(stockArea,0))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,0) + item.getLockCount(stockArea,0));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,0) + item.getLockCount(0,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,0) + item.getLockCount(0,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,0) + item.getLockCount(1,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,0) + item.getLockCount(1,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==2||stockArea==-1){
						city_count = item.getStock(2,0) + item.getLockCount(2,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(2,0) + item.getLockCount(2,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,0) + item.getLockCount(3,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,0) + item.getLockCount(3,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,0) + item.getLockCount(4,0);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,0) + item.getLockCount(0,0) +item.getStock(1,0) + item.getLockCount(1,0)+item.getStock(2,0) + item.getLockCount(2,0)+item.getStock(3,0) + item.getLockCount(3,0)+item.getStock(4,0) + item.getLockCount(4,0))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}else if(stockType==6){//样品库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,1))*item.getPrice5();
							stockcounts = new BigDecimal((item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6)));
						}else{
//							total=total+(item.getStock(stockArea,6) + item.getLockCount(stockArea,6))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,6) + item.getLockCount(stockArea,6));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,6) + item.getLockCount(0,6);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,6) + item.getLockCount(0,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,6) + item.getLockCount(1,6);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,6) + item.getLockCount(1,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,6) + item.getLockCount(3,6);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,6) + item.getLockCount(3,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,6) + item.getLockCount(0,6) + item.getStock(1,6) + item.getLockCount(1,6) + item.getStock(3,6) + item.getLockCount(3,6))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}else if(stockType==4){//退货库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4) + item.getStock(3,4) + item.getLockCount(3,4) + item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5();
							stockcounts = new BigDecimal((item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4)));
						}else{
//							total=total+(item.getStock(stockArea,4) + item.getLockCount(stockArea,4))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,4) + item.getLockCount(stockArea,4));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			        }
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,4) + item.getLockCount(0,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,4) + item.getLockCount(0,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,4) + item.getLockCount(1,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,4) + item.getLockCount(1,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==2||stockArea==-1){
						city_count = item.getStock(2,4) + item.getLockCount(2,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(2,4) + item.getLockCount(2,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,4) + item.getLockCount(3,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,4) + item.getLockCount(3,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,4) + item.getLockCount(4,4);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,4) + item.getLockCount(0,4) + item.getStock(1,4) + item.getLockCount(1,4)+item.getStock(2,4) + item.getLockCount(2,4)+item.getStock(3,4) + item.getLockCount(3,4)+item.getStock(4,4) + item.getLockCount(4,4))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}else if(stockType==3){//返厂库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3) + item.getStock(3,3) + item.getLockCount(3,3) + item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3)+ item.getStock(4,3) + item.getLockCount(4,3));
						}else{
//							total=total+(item.getStock(stockArea,3) + item.getLockCount(stockArea,3))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,3) + item.getLockCount(stockArea,3));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,3) + item.getLockCount(0,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,3) + item.getLockCount(0,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,3) + item.getLockCount(1,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,3) + item.getLockCount(1,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,3) + item.getLockCount(3,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,3) + item.getLockCount(3,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==4||stockArea==-1){
						city_count = item.getStock(4,3) + item.getLockCount(4,3);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3)+ item.getStock(4,3) + item.getLockCount(4,3);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,3) + item.getLockCount(0,3) + item.getStock(1,3) + item.getLockCount(1,3)+ item.getStock(3,3) + item.getLockCount(3,3)+ item.getStock(4,3) + item.getLockCount(4,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}else if(stockType==2){//维修库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2));
						}else{
//							total=total+(item.getStock(stockArea,2) + item.getLockCount(stockArea,2))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,2) + item.getLockCount(stockArea,2));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,2) + item.getLockCount(0,2);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,2) + item.getLockCount(0,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,2) + item.getLockCount(1,2);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,2) + item.getLockCount(1,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,2) + item.getLockCount(3,2);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,3) + item.getLockCount(3,3))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,2) + item.getLockCount(0,2) + item.getStock(1,2) + item.getLockCount(1,2) + item.getStock(3,2) + item.getLockCount(3,2))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}else if(stockType==5){//残次品库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5) + item.getStock(3,5) + item.getLockCount(3,5))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5));
						}else{
//							total=total+(item.getStock(stockArea,5) + item.getLockCount(stockArea,5))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,5) + item.getLockCount(stockArea,5));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			        }
					if(stockArea==0||stockArea==-1){
						city_count = item.getStock(0,5) + item.getLockCount(0,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal((item.getStock(0,5) + item.getLockCount(0,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,5) + item.getLockCount(1,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,5) + item.getLockCount(1,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==2||stockArea==-1){
						city_count = item.getStock(2,5) + item.getLockCount(2,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(2,5) + item.getLockCount(2,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
					if(stockArea==3||stockArea==-1){
						city_count = item.getStock(3,5) + item.getLockCount(3,5);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(3,5) + item.getLockCount(3,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(0,5) + item.getLockCount(0,5) + item.getStock(1,5) + item.getLockCount(1,5)+item.getStock(2,5) + item.getLockCount(2,5)+item.getStock(3,5) + item.getLockCount(3,5))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}else if(stockType==9){//售后库
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
						if(stockArea == -1){
//							total=total+(item.getStock(0,9) + item.getLockCount(0,9) + item.getStock(1,9) + item.getLockCount(1,9) + item.getStock(3,9) + item.getLockCount(3,9))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(1,9) + item.getLockCount(1,9));
						}else{
//							total=total+(item.getStock(stockArea,9) + item.getLockCount(stockArea,9))*item.getPrice5();
							stockcounts = new BigDecimal(item.getStock(stockArea,9) + item.getLockCount(stockArea,9));
						}
						price5 = new BigDecimal(item.getPrice5());
						total = new BigDecimal(total).add(stockcounts.multiply(price5)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					if(stockArea==1||stockArea==-1){
						city_count = item.getStock(1,9) + item.getLockCount(1,9);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal( (item.getStock(1,9) + item.getLockCount(1,9))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body.add(city_count+"");
						body.add(city_amount+"");
					}
//					rows.append("'city_count':'").append(city_count+"',")
//					.append("'city_amount':'").append(city_amount+"',");
					stock_count=item.getStock(1,9) + item.getLockCount(1,9);
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						stock_amount = new  BigDecimal((item.getStock(1,9) + item.getLockCount(1,9))*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body.add(stock_count+"");
					body.add(stock_amount+"");
				}
				
				body.add(item.getStatusName());
				body.add(item.getParentId1()==0  ? "无" : item.getParent1().getName());
				body.add(item.getParentId2()==0  ? "无" : item.getParent2().getName());
				body.add(item.getParentId3()==0  ? "无" : item.getParent3().getName());
				bodies.add(body);
			}
			if(group.isFlag(95)){
				header0.add("共有:"+list.size()+" 种产品 总金额: "+new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				for(int i=0;i<header.size()-1;i++){
					header0.add("");
				}
				headers.add(header0);
				/*允许合并列,下标从0开始，即0代表第一列*/
				List<Integer> mayMergeColumn = new ArrayList<Integer>();
				mayMergeColumn.add(0);
				mayMergeColumn.add(header0.size());
				excel.setMayMergeColumn(mayMergeColumn);
				
				/*允许合并行,下标从0开始，即0代表第一行*/
				List<Integer> mayMergeRow = new ArrayList<Integer>();
				mayMergeRow.add(0);
	            excel.setMayMergeRow(mayMergeRow);
				/*
				 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
				 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
				 * 
				 * */
				excel.setColMergeCount(header0.size());
			}
			headers.add(header);
			//调用填充表头方法
            excel.buildListHeader(headers);
            
            //用于标记是否是表头数据,一般设置样式时是否表头会用到
            excel.setHeader(false);
            
            //调用填充数据区方法
            excel.buildListBody(bodies);
            
            //文件输出
            excel.exportToExcel("分库库存查询导出_"+DateUtil.getNowDateStr(), response, "");
            
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			wareService.releaseAll();
			psService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 以下是改进版
	 */
	/**
	 * 查询货位库存
	 * @param request
	 * @param response
	 * @return
	 * 2013/10/15
	 * 朱爱林
	 * @throws Exception 
	 */
	@RequestMapping("/searchProductStock")
	@ResponseBody
	public Object searchProductStock(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		if(adminUser == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = adminUser.getGroup();
		request.setCharacterEncoding("utf-8");
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商	
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
	
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类2
		String toExcel =StringUtil.dealParam(request.getParameter("toExcel"));//编号
		
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
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and a.code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(proxy>0){
			condition.append(" and d.id="+proxy);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}else{
			condition.append(" and (a.parent_id1 in ("+catalogIds1+")");
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}else{
			if(parentId1 > 0){
				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
					condition.append(" and a.parent_id2 in ("+catalogIds2+")");
				}
			}else{
				condition.append(" or a.parent_id2 in ("+catalogIds2+"))");
			}
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
//		else{
//			if(parentId1 > 0){
//				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
//					condition.append(" and a.parent_id3 in ("+catalogIds2+")");
//				}
//			}else{
//				condition.append(" or a.parent_id3 in ("+catalogIds2+"))");
//			}
//		}	 
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService service = new WareService(dbOperation);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		try {
			BigDecimal totalPrice = new BigDecimal(0.0d);
			List totalList = service.getProductList2(condition.toString(), -1, -1, "a.id asc");
	        Iterator it = totalList.listIterator();
	        BigDecimal stockAll = null;
	        BigDecimal lockCountAll = null;
	        BigDecimal price5 = null;
	        //float share = (pp.divide(ss, 4, BigDecimal.ROUND_HALF_DOWN).floatValue())*100;
			while(it.hasNext()){
				voProduct product = (voProduct)it.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
				stockAll = new BigDecimal(product.getStockAll());
				lockCountAll = new BigDecimal(product.getLockCountAll());
				price5 = new BigDecimal(product.getPrice5());
				totalPrice = totalPrice.add((stockAll.add(lockCountAll)).multiply(price5));
				//totalPrice += (product.getStockAll() + product.getLockCountAll())*product.getPrice5();
			}
			request.setAttribute("totalList", totalList);
//			resultMap.put("total","0");
			resultMap.put("rows",lists);

			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;
			
			request.setAttribute("totalList", totalList);//计算出要显示的总数
			 //总数
	        int totalCount = totalList.size();
	        resultMap.put("total", totalCount);
	        //页码
	        //为了分页显示
	        List list = service.getProductList2(condition.toString(), start , rows, "a.id asc");
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
			}
			voProduct item = null;
			String path = request.getContextPath();
			for(int i=0;i<list.size();i++){
				map = new HashMap<String, String>();
				item = (voProduct) list.get(i);
				map.put("code", "<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">"+item.getCode()+"</a>");//编号fproduct.do
				map.put("name", "<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">"+item.getOriname()+"</a>");//名称
				map.put("stock_count", item.getStockAll() + item.getLockCountAll()+"");//库存总数
				//增加判断条件，如果库存总和为0则不显示
				if(item.getStockAll() + item.getLockCountAll()<=0){
					continue;
				}
				//待验库
				map.put("dyk_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_CHECK)+"");//
				map.put("dyk_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_CHECK)+"");//
				map.put("dyk_wx", item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_CHECK) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_CHECK)+"");//
				//合格库
				map.put("hg_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				map.put("hg_gs", item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				map.put("hg_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				map.put("hg_wx", item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_QUALIFIED)+"");//
				//退货库
				map.put("th_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_RETURN)+"");//
				map.put("th_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_RETURN) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_RETURN)+"");//
				map.put("th_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_RETURN)+"");//
				map.put("th_wx", item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_RETURN) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_RETURN)+"");//
				//返厂库
				map.put("fc_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_BACK)+"");//
				map.put("fc_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_BACK) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_BACK)+"");//
				map.put("fc_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_BACK)+"");//
				//维修库
				map.put("wx_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_REPAIR)+"");//
				map.put("wx_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_REPAIR) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_REPAIR)+"");//
				map.put("wx_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_REPAIR)+"");//
				//残次品库
				map.put("ccp_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_DEFECTIVE)+"");//
				map.put("ccp_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_DEFECTIVE)+"");//
				map.put("ccp_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_DEFECTIVE)+"");//
				//样品库
				map.put("yp_bj", item.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_SAMPLE)+"");//
				map.put("yp_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_SAMPLE)+"");//
				map.put("yp_zc", item.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_SAMPLE)+"");//
				map.put("yp_wx", item.getStock(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_SAMPLE) + item.getLockCount(ProductStockBean.AREA_WX, ProductStockBean.STOCKTYPE_SAMPLE)+"");//
				//售后库
				map.put("sh_fc", item.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_AFTER_SALE) + item.getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_AFTER_SALE)+"");//
				//标准库存
				map.put("stockstandard_bj", item.getStockStandardBj()+"");//
				map.put("stockstandard_fc", item.getStockStandardGd()+"");//
				
				map.put("statusName", item.getStatusName());//
				map.put("parent1_name", item.getParentId1()==0? "无" : item.getParent1().getName());//
				map.put("parent2_name", item.getParentId2()==0? "无" : item.getParent2().getName());//
				map.put("parent3_name", item.getParentId3()==0? "无" : item.getParent3().getName());//
				map.put("rank", item.getRank()+"");//
				map.put("query", "<a href=\""+path+"/admin/productStock/stockCardList.jsp?productCode="+StringUtil.dealParam(item.getCode())+"\" target=\"_blank\">查</a>");//
				lists.add(map);
			}
			resultMap.put("rows", lists);
			if(!group.isFlag(86)){
				resultMap.put("boo","false");
			}else{
				resultMap.put("boo","true");
				if(totalPrice.toString().split("\\.").length>=2&&totalPrice.toString().split("\\.")[1].length()>2){
					resultMap.put("totalPrice", totalPrice.toString().substring(0, totalPrice.toString().indexOf(".")+3));
				}else{
					resultMap.put("totalPrice", totalPrice.toString());
				}
			}
		} finally {
			psService.releaseAll();
		}
		//导出数据
		if(toExcel!=null&&toExcel.equals("to")){
//			return mapping.findForward("toExcel");
		} 
		return resultMap;
		//显示数据
	}
	/**
	 * 库存查询--改成动态加载库地区
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/getProductStockSubColumns")
	public void getProductStockSubColumns(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(adminUser == null){
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return;
		}
		StringBuilder columns = new StringBuilder();
		//表头有三行，需要分别定义
		StringBuilder firstRow = new StringBuilder();
		StringBuilder secondRow = new StringBuilder();
		StringBuilder thirdRow = new StringBuilder();
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService service = new WareService(dbOperation);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		try{
			secondRow.append("[");
			
			secondRow.append("{field:'stock_count',title:'库存总数', align:'center',rowspan:2, colspan:1},");
			//获取所有的库类型
			Map<Integer,String> stockTypeMap = ProductStockBean.getStockTypeMap();
			Set<Entry<Integer, String>> set = stockTypeMap.entrySet();
			Iterator<Entry<Integer,String>> ite = set.iterator();
			int size = 0;
			//记录所有的库类型id,能保证columns与rows的field顺序一致
			List<Integer> stockTypeIdList = new ArrayList<Integer>();
			while(ite.hasNext()){
				Entry<Integer,String> entry = ite.next();
//				entry.getKey();//库类型的id
//				entry.getValue();//库类型的名字
				stockTypeIdList.add(entry.getKey());
				size = ProductStockBean.getTypeToAreaMap().get(entry.getKey()).size();//有多少个库区域
				secondRow.append("{field:'',title:'")
					.append(entry.getValue())
					.append("', align:'center',rowspan:1, colspan:")
					.append(size)
					.append("},");
			}
			secondRow.deleteCharAt(secondRow.length()-1);
			secondRow.append("]");
			List<StockAreaBean> stockAreaList = null;
			thirdRow.append("[");
			int colspan = 1;//记录所有的需要显示仓库的列数 初始值为1，包含了库存总数列
			for(Integer typeId:stockTypeIdList){
				stockAreaList = ProductStockBean.getStockAreaByType(typeId);
				int temp = 0;
				for(StockAreaBean bean:stockAreaList){
					colspan++;
					thirdRow.append("{field:'").append(typeId+"_"+temp++).append("',title:'")//field字段的名字待定，需与rows对应
						.append(bean.getName())
						.append("', align:'center',rowspan:1, colspan:1},");
				}
			}
			thirdRow.append("{field:'stockstandard_bj',title:'北京',align:'center',rowspan:1, colspan:1},{field:'stockstandard_fc',title:'芳村',align:'center',rowspan:1, colspan:1}");
			thirdRow.append("]");
			
			firstRow.append("[");
			firstRow.append("{field:'code',title:'编号',rowspan:3,colspan:1},{field:'name',title:'名称', align:'center',rowspan:3, colspan:1},")
				.append("{field:'',title:'库存数量', align:'center',rowspan:1, colspan:").append(colspan).append("},")
				.append("{field:'',title:'库存标准', align:'center',rowspan:2, colspan:2},{field:'statusName',title:'状态', align:'center',rowspan:3, colspan:1},")
				.append("{field:'parent1_name',title:'一级分类', align:'center',rowspan:3, colspan:1},{field:'parent2_name',title:'二级分类', align:'center',rowspan:3, colspan:1},")
				.append(" {field:'parent3_name',title:'三级分类', align:'center',rowspan:3, colspan:1},{field:'rank',title:'等级', align:'center',rowspan:3, colspan:1},")
				.append("{field:'query',title:'库存记录', align:'center',rowspan:3, colspan:1}");
			firstRow.append("]");
			
			columns.append("[");
			columns.append(firstRow.toString()).append(",")
					.append(secondRow.toString()).append(",")
					.append(thirdRow.toString());
			columns.append("]");
			
			//存储所有的数据
			StringBuilder rows2 = new StringBuilder();
			
			UserGroupBean group = adminUser.getGroup();
			request.setCharacterEncoding("utf-8");
			int status = StringUtil.StringToId(request.getParameter("status"));//状态
			int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商	
			String code =StringUtil.dealParam(request.getParameter("code"));//编号
			if (code==null) code="";
			String name =StringUtil.dealParam(request.getParameter("name"));//名称
		
			name = Encoder.decrypt(name);//解码为中文
			if(name==null){//解码失败,表示已经为中文,则返回默认
				 name =StringUtil.dealParam(request.getParameter("name"));//名称
			}
			if (name==null) name="";
			int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
			int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
			int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类2
			
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
			
			StringBuffer condition = new StringBuffer();
			if(code!=null&&!code.trim().equals("")){
				condition.append(" and a.code='"+code+"'");
			}
			//名称匹配
			if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
				condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
			}
			if(proxy>0){
				condition.append(" and d.id="+proxy);
			}
			if(status > 0){
				condition.append( " and a.status ="+status);
			}
			if(parentId1>0){
				condition.append(" and a.parent_id1="+parentId1);
			}else{
				condition.append(" and (a.parent_id1 in ("+catalogIds1+")");
			}
			if(parentId2>0){
				condition.append(" and a.parent_id2="+parentId2);
			}else{
				if(parentId1 > 0){
					if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
						condition.append(" and a.parent_id2 in ("+catalogIds2+")");
					}
				}else{
					condition.append(" or a.parent_id2 in ("+catalogIds2+"))");
				}
			}
			if(parentId3>0){
				condition.append(" and a.parent_id3="+parentId3);
			}
			
			
			BigDecimal totalPrice = new BigDecimal(0.0d);
			List totalList = service.getProductList2(condition.toString(), -1, -1, "a.id asc");
	        Iterator it = totalList.listIterator();
	        BigDecimal stockAll = null;
	        BigDecimal lockCountAll = null;
	        BigDecimal price5 = null;
	        //float share = (pp.divide(ss, 4, BigDecimal.ROUND_HALF_DOWN).floatValue())*100;
			while(it.hasNext()){
				voProduct product = (voProduct)it.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
				stockAll = new BigDecimal(product.getStockAll());
				lockCountAll = new BigDecimal(product.getLockCountAll());
				price5 = new BigDecimal(product.getPrice5());
				totalPrice = totalPrice.add((stockAll.add(lockCountAll)).multiply(price5));
				//totalPrice += (product.getStockAll() + product.getLockCountAll())*product.getPrice5();
			}
			request.setAttribute("totalList", totalList);
//			resultMap.put("total","0");

			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;
			
			request.setAttribute("totalList", totalList);//计算出要显示的总数
			 //总数
	        int totalCount = totalList.size();
	        //页码
	        //为了分页显示
	        List list = service.getProductList2(condition.toString(), start , rows, "a.id asc");
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
			}
			voProduct item = null;
			String path = request.getContextPath();
			rows2.append("[");
			int stockCount = 0;//临时变量，记录每个库的库存数量
			for(int i=0;i<list.size();i++){
				item = (voProduct) list.get(i);
				//增加判断条件，如果库存总和为0则不显示
				if(item.getStockAll() + item.getLockCountAll()<=0){
					continue;
				}
				rows2.append("{'code':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getCode())+"</a>',")
					.append("'name':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getName())+"</a>',")
					.append("'stock_count':'").append(item.getStockAll() + item.getLockCountAll()+"',");
				
				for(Integer typeId:stockTypeIdList){
					stockAreaList = ProductStockBean.getStockAreaByType(typeId);
					int temp = 0;
					for(StockAreaBean bean:stockAreaList){
						stockCount = item.getStock(bean.getId(), typeId)+item.getLockCount(bean.getId(), typeId);
						rows2.append("'").append(typeId+"_"+temp++).append("':'").append(stockCount).append("',");
					}
				}
				rows2.append("'stockstandard_bj':'").append(item.getStockStandardBj()).append("',")
					.append("'stockstandard_fc':'").append(item.getStockStandardGd()).append("',")
					.append("'statusName':'").append(item.getStatusName()).append("',")
					.append("'parent1_name':'").append(item.getParentId1()==0? "无" : item.getParent1().getName()).append("',")
					.append("'parent2_name':'").append(item.getParentId2()==0? "无" : item.getParent2().getName()).append("',")
					.append("'parent3_name':'").append(item.getParentId3()==0? "无" : item.getParent3().getName()).append("',")
					.append("'rank':'").append(item.getRank()).append("',")
					.append("'query':'").append("<a href=\""+path+"/admin/productStock/stockCardList.jsp?productCode="+StringUtil.dealParam(item.getCode())+"\" target=\"_blank\">查</a>").append("'")
					.append("},");
				
			}
			if(list.size()>0){
				rows2.deleteCharAt(rows2.length()-1);
			}
			rows2.append("]");
//			resultMap.put("rows", lists);
			
			StringBuilder result = new StringBuilder();
			result.append("{\'rows\':"+rows2.toString()+",\'columns\':"+columns.toString()+",\'size\':'"+totalCount+"',");
			if(!group.isFlag(86)){
				result.append("\'boo\':\'false\'}");
			}else{
				result.append("\'boo\':\'true\',");
				if(totalPrice.toString().split("\\.").length>=2&&totalPrice.toString().split("\\.")[1].length()>2){
					result.append("\'totalPrice\':\'").append(totalPrice.toString().substring(0, totalPrice.toString().indexOf(".")+3)).append("\'}");
				}else{
					result.append("\'totalPrice\':\'").append(totalPrice).append("\'}");
				}
			}
			response.getWriter().write(result.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			psService.releaseAll();
		}
	}
	/**
	 * 查询货位库存导出
	 * @param request
	 * @param response
	 * @return
	 * 2013/10/15
	 * 朱爱林
	 * @throws Exception 
	 */
	@RequestMapping("/searchProductStockExport")
	public String searchProductStockExport(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		if(adminUser == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		request.setCharacterEncoding("utf-8");
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商	
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
		
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类2
		
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
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and a.code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(proxy>0){
			condition.append(" and d.id="+proxy);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}else{
			condition.append(" and (a.parent_id1 in ("+catalogIds1+")");
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}else{
			if(parentId1 > 0){
				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
					condition.append(" and a.parent_id2 in ("+catalogIds2+")");
				}
			}else{
				condition.append(" or a.parent_id2 in ("+catalogIds2+"))");
			}
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
		
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService service = new WareService(dbOperation);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		
		ExportExcel excel = new ExportExcel(ExportExcel.XSSF);
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header0 = new ArrayList<String>();//表头
		ArrayList<String> header1 = new ArrayList<String>();//表头
		ArrayList<String> header2 = new ArrayList<String>();//表头
		try {
			
/**************************************************/			
			header1.add("编号");
			header1.add("名称");
			header1.add("库存总数");
			
			header2.add("编号");
			header2.add("名称");
			header2.add("库存总数");
			
			//获取所有的库类型
			Map<Integer,String> stockTypeMap = ProductStockBean.getStockTypeMap();
			Set<Entry<Integer,String>> set = stockTypeMap.entrySet();
			Iterator<Entry<Integer,String>> ite = set.iterator();
			//记录所有的库类型id,能保证columns与rows的field顺序一致
			List<Integer> stockTypeIdList = new ArrayList<Integer>();
			int columnSize = 1;
			while(ite.hasNext()){
				Entry<Integer,String> entry = ite.next();
				entry.getKey();//库类型id
				entry.getValue();//库类型名称
				stockTypeIdList.add(entry.getKey());
				//库类型对应的库地区
				List<StockAreaBean> list = ProductStockBean.getStockAreaByType(entry.getKey());
				for(StockAreaBean bean:list){
					header1.add(entry.getValue());
					header2.add(bean.getName());
					columnSize++;
				}
			}
			header1.add("库存标准");
			header1.add("库存标准");
			header1.add("状态");
			header1.add("一级分类");
			header1.add("二级分类");
			header1.add("三级分类");
			header1.add("等级");
			header1.add("库存记录");
/**************************************************/			
			
			header2.add("北京");
			header2.add("芳村");
			header2.add("状态");
			header2.add("一级分类");
			header2.add("二级分类");
			header2.add("三级分类");
			header2.add("等级");
			header2.add("库存记录");
			
			header0.add("编号");
			header0.add("名称");
			for(int i=0;i<columnSize;i++){
				header0.add("库存数量");
			}
			header0.add("库存标准");
			header0.add("库存标准");
			header0.add("状态");
			header0.add("一级分类");
			header0.add("二级分类");
			header0.add("三级分类");
			header0.add("等级");
			header0.add("库存记录");
			
			headers.add(header0);
			headers.add(header1);
			headers.add(header2);
			
			/*允许合并列,下标从0开始，即0代表第一列*/
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			mayMergeColumn.add(0);
			mayMergeColumn.add(1);
			mayMergeColumn.add(2);
			mayMergeColumn.add(3+columnSize);
			mayMergeColumn.add(4+columnSize);
			mayMergeColumn.add(5+columnSize);
			mayMergeColumn.add(6+columnSize);
			mayMergeColumn.add(7+columnSize);
			mayMergeColumn.add(8+columnSize);
			mayMergeColumn.add(9+columnSize);
			mayMergeColumn.add(10+columnSize);
			
			/**
			 * 一下设置bodies
			 */
			voProduct item = null;
			List list = service.getProductList2(condition.toString(), -1, -1, "a.id asc");
			Iterator it = list.listIterator();
			//float share = (pp.divide(ss, 4, BigDecimal.ROUND_HALF_DOWN).floatValue())*100;
			while(it.hasNext()){
				voProduct product = (voProduct)it.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
				//totalPrice += (product.getStockAll() + product.getLockCountAll())*product.getPrice5();
			}
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			ArrayList<String> body = new ArrayList<String>();//数据
			
			List<StockAreaBean> stockAreaList = null;
			int stockCount = 0;//临时变量，记录每个库的库存数量
			for(int i=0;i<list.size();i++){
				body = new ArrayList<String>();
				item = (voProduct) list.get(i);
				//增加判断条件，如果库存总和为0则不显示
				if(item.getStockAll() + item.getLockCountAll()<=0){
					continue;
				}
				body.add(StringUtil.dealParam(item.getCode()));
				body.add(StringUtil.dealParam(item.getName()));
				body.add((item.getStockAll() + item.getLockCountAll())+"");
				
				for(Integer typeId:stockTypeIdList){
					stockAreaList = ProductStockBean.getStockAreaByType(typeId);
					for(StockAreaBean bean:stockAreaList){
						stockCount = item.getStock(bean.getId(), typeId)+item.getLockCount(bean.getId(), typeId);
//						rows2.append("'").append(typeId+"_"+temp++).append("':'").append(stockCount).append("',");
						body.add(stockCount+"");
					}
				}
				body.add(item.getStockStandardBj()+"");
				body.add(item.getStockStandardGd()+"");
				body.add(item.getStatusName());
				body.add(item.getParentId1()==0? "无" : item.getParent1().getName());
				body.add(item.getParentId2()==0? "无" : item.getParent2().getName());
				body.add(item.getParentId3()==0? "无" : item.getParent3().getName());
				body.add(item.getRank()+"");
				body.add("查");
				bodies.add(body);
			}
			
			excel.setMayMergeColumn(mayMergeColumn);
			
			/*允许合并行,下标从0开始，即0代表第一行*/
			List<Integer> mayMergeRow = new ArrayList<Integer>();
            mayMergeRow.add(0);
            mayMergeRow.add(1);
            mayMergeRow.add(2);
            excel.setMayMergeRow(mayMergeRow);
            
            
            /*
			 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
			 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
			 * 
			 * */
			excel.setColMergeCount(headers.get(0).size());
			//调用填充表头方法
            excel.buildListHeader(headers);
            //用于标记是否是表头数据,一般设置样式时是否表头会用到
            excel.setHeader(false);
          //调用填充数据区方法
            excel.buildListBody(bodies);
			excel.exportToExcel("库存查询导出_"+DateUtil.getNowDateStr(), response, "");
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			psService.releaseAll();
		}
		return null;
	}
	/**
	 * 获取供应商列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/querySelectSupplier")
	@ResponseBody
	public Object querySelectSupplier(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
//		StringBuilder result = new StringBuilder();
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService wareService = new WareService(dbOperation);
		
		String statusIds = "-1";
		if(!"".equals(ProductLinePermissionCache.getProductLineSupplierIds(user))){
			statusIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
		}
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> maps = new HashMap<String, String>();
		maps.put("id", "0");
		maps.put("text", "所有");
		maps.put("selected", "true");
		list.add(maps);
		try{
			List supplierList = wareService.getSelects("supplier_standard_info ", "where status = 1 and id in(" + statusIds + ")");
			for(int i=0;i<supplierList.size();i++){
				voSelect ssInfoBean = (voSelect) supplierList.get(i);
				if(ssInfoBean != null){
					maps = new HashMap<String, String>();
					maps.put("id", ssInfoBean.getId()+"");
					maps.put("text", ssInfoBean.getName());
					list.add(maps);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return list;
	}
	
	@RequestMapping("/searchProductStockAmount")
	public void searchProductStockAmount(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(adminUser == null){
			response.getWriter().write("{\'result\':\'failure\',\'tip\':\'当前没有登录，操作失败！\'}");
			return;
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService wareService = new WareService(dbOperation);
		UserGroupBean group = adminUser.getGroup();
		String statusIds = "-1";
		if(!"".equals(ProductLinePermissionCache.getProductLineSupplierIds(adminUser))){
			statusIds = ProductLinePermissionCache.getProductLineSupplierIds(adminUser);
		}
		List supplierList = wareService.getSelects("supplier_standard_info ", "where status = 1 and id in(" + statusIds + ")");
		request.setAttribute("supplierList", supplierList);
		String path = request.getContextPath();
		request.setCharacterEncoding("utf-8");
		
		int proxy = 0;	// 供应商id
		int suppliertext = StringUtil.StringToId(request.getParameter("suppliertext"));//供应商名称
		request.setAttribute("proxy", String.valueOf(proxy));
		
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
		int stockType =StringUtil.toInt(request.getParameter("stockType"));//库类型
		//System.out.println("---->"+stockType);
		int stockArea =StringUtil.toInt(request.getParameter("stockArea"));//库区域
		request.setAttribute("stockType", stockType);
		request.setAttribute("stockArea", stockArea);
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类3
		
		//产品线权限限制
//		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
//		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
//		String catalogIdsTemp = "";
//		if(!catalogIds2.equals("")){
//			String[] splits = catalogIds2.split(",");
//			for(int i=0;i<splits.length;i++){
//				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
//				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
//					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
//				}
//			}
//			if(catalogIds1.endsWith(",")){
//				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
//			}
//		}
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(suppliertext>0){
			condition.append(" and d.id="+suppliertext);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
		if(stockType>=0){
			condition.append(" and ps.type="+stockType);
		}
		if(stockArea>=0){
			condition.append(" and ps.area="+stockArea);
		}

		//得出已处理中和待处理状态的产品集合
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		StringBuilder columns2 = new StringBuilder();//表头
		StringBuilder rows2 = new StringBuilder();//表数据
		try {
			List list = wareService.getProductList2(condition.toString(), -1,-1, "a.id asc");
		    //Hashtable ht=service.getOrderStockProductList(list);	
		    //request.setAttribute("ht", ht);
			
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));	
			}
			
			request.setAttribute("productList", list);
			request.setAttribute("status",""+status);
			request.setAttribute("count", ""+list.size());
			request.setAttribute("date","status="+status+"&proxy="+proxy+"&code="+code+"&name="+name+"&parentId1="+parentId1+"&parentId2="+parentId2+"&parentId3="+parentId3);
			columns2.append("[[");
			
			columns2.append("{field:'code',title:'产品编号',align:'center'},{field:'name',title:'小店名称',align:'center'},{field:'oriname',title:'产品原名称',align:'center'},");
			
			List<StockAreaBean> areaList = ProductStockBean.getStockAreaByType(stockType);
			boolean boo2 = group.isFlag(95);
			//field规则：库存数量用地区id，库存金额用地区id加_count
			if(stockArea==-1){
				for(StockAreaBean bean : areaList){
					//先放库存数量
					columns2.append("{field:'").append(bean.getId()).append("',title:'").append(bean.getName()).append("',align:'center'},");
					//判断是否有95的权限，然后显示库存对应的金额数
					if(boo2){
						columns2.append("{field:'").append(bean.getId()+"_count").append("',title:'金额(").append(bean.getName()).append(")',align:'center'},");
					}
				}
			}else{
				//先放库存数量
				columns2.append("{field:'").append(stockArea).append("',title:'").append(ProductStockBean.getAreaName(stockArea)).append("',align:'center'},");
				//判断是否有95的权限，然后显示库存对应的金额数
				if(boo2){
					columns2.append("{field:'").append(stockArea+"_count").append("',title:'金额(").append(ProductStockBean.getAreaName(stockArea)).append(")',align:'center'},");
				}
			}
			//field:  city_count,city_amount
			/*Map<Integer,Map<Integer,Map<String,String>>> stockMap = voProduct.stockMap;
			Map<Integer,Map<String,String>> areaMap = stockMap.get(stockType);
			boolean boo2 = group.isFlag(95);
			Integer areaKey = 0;
			String key = "";
			int ii=0;
			if(stockArea==-1){
				//所有的仓库
				Iterator iteArea = areaMap.keySet().iterator();
				while(iteArea.hasNext()){
					areaKey = (Integer) iteArea.next();//仓库
					Map<String,String> areaSub = areaMap.get(areaKey);//仓库下需要显示的列
					Iterator iteAreaSub = areaSub.keySet().iterator();
					while(iteAreaSub.hasNext()){
						key = iteAreaSub.next().toString();
						if(ii++==0){
							columns2.append("{field:'").append(key).append("',title:'").append(areaSub.get(key).toString()).append("',align:'center'},");
							continue;
						}
						if(boo2){
							columns2.append("{field:'").append(key).append("',title:'").append(areaSub.get(key).toString()).append("',align:'center'},");
						}else{
							return;
						}
					}
				}
			}else{
				Map<String,String> areaSub = areaMap.get(stockArea);
				Iterator iteAreaSub = areaSub.keySet().iterator();
				while(iteAreaSub.hasNext()){
					key = iteAreaSub.next().toString();
					if(ii++ ==0){
						columns2.append("{field:'").append(key).append("',title:'").append(areaSub.get(key).toString()).append("',align:'center'},");
						continue;
					}
					if(boo2){
						columns2.append("{field:'").append(key).append("',title:'").append(areaSub.get(key).toString()).append("',align:'center'},");
					}else{
						return;
					}
				}
			}*/
			columns2.append("{field:'stock_count',title:'库存总数',align:'center'},")
				.append(group.isFlag(95)==true?"{field:'stock_amount',title:'总金额',align:'center'},":"")
				.append("{field:'status_name',title:'状态',align:'center'},{field:'parent_id1',title:'一级分类',align:'center'},{field:'parent_id2',title:'二级分类',align:'center'},")
				.append("{field:'parent_id3',title:'三级分类',align:'center'},{field:'query',title:'库存记录',align:'center'}");
			columns2.append("]]");
			BigDecimal total2 = new BigDecimal(0);
			int city_count = 0;
			double city_amount = 0.0d;
			int stock_count = 0;//库存总数
			BigDecimal stock_amount2 = new BigDecimal(0);//总金额
			//city_count city_amount
			//计算总金额的中间变量  stockcounts  price5
			BigDecimal price5 = null;
			rows2.append("[");//[{:,:},]
			for(int i=0;i<list.size();i++){
				voProduct item = (voProduct) list.get(i);
				rows2.append("{'code':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getCode())+"</a>',")
				.append("'name':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getName())+"</a>',")
				.append("'oriname':'<a href=\""+path+"/admin/fproduct.do?id="+item.getId()+"\">").append(StringUtil.dealParam(item.getOriname())+"</a>',");
//				Map<Integer,Map<Integer,Map<String,String>>> stockMap = voProduct.stockMap;
//				Map<Integer,Map<String,String>> areaMap = stockMap.get(stockType);
				stock_count = 0;
				//1、先计算所选库类型的总数量 stock_count
				for(StockAreaBean bean:areaList){
					stock_count = stock_count+(item.getStock(bean.getId(), stockType)+item.getLockCount(bean.getId(), stockType));
				}
				if(stock_count<=0){
					continue;
				}
				price5 = new BigDecimal(item.getPrice5());
				//2 、 计算库存总金额 total2
				stock_amount2 = new BigDecimal(stock_count).multiply(price5).setScale(2, BigDecimal.ROUND_HALF_UP);
				if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
					total2 = total2.add(stock_amount2);
				}
				
				if(stockArea==-1){
					for(StockAreaBean bean:areaList){
						
						city_count = item.getStock(bean.getId(),stockType) + item.getLockCount(bean.getId(),stockType);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						rows2.append("'"+bean.getId()+"':'").append(city_count+"',");
						rows2.append("'"+bean.getId()+"_count':'").append(city_amount+"',");
					}
				}else{
					city_count = item.getStock(stockArea,stockType) + item.getLockCount(stockArea,stockType);
					if(city_count<=0){
						continue;
					}
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					rows2.append("'"+stockArea+"':'").append(city_count+"',");
					rows2.append("'"+stockArea+"_count':'").append(city_amount+"',");
				}
				
				rows2.append("'stock_count':'").append(stock_count+"',");
				if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
					rows2.append("'stock_amount':'").append(stock_amount2.doubleValue()+"',");
				}
				rows2.append("'status_name':'").append(item.getStatusName()+"',")
					.append("'parent_id1':'").append((item.getParentId1()==0  ? "无" : item.getParent1().getName())+"',")
					.append("'parent_id2':'").append((item.getParentId2()==0  ? "无" : item.getParent2().getName())+"',")
					.append("'parent_id3':'").append((item.getParentId3()==0  ? "无" : item.getParent3().getName())+"',")
					.append("'query':'").append("<a href=\""+path+"/admin/productStock/stockCardList.jsp?productCode="+item.getCode()+"\" target=\"_blank\">查</a>'},");
				
			}
			if(list.size()!=0){
				rows2.deleteCharAt(rows2.length()-1);
			}
			rows2.append("]");
			boolean boo = group.isFlag(95);
			String re = "{\'rows\':"+rows2.toString()+",\'columns\':"+columns2.toString()+",\'totalPrice\':\'"+total2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"\',\'size\':\'"+list.size()+"\',\'boo\':\'"+boo+"\'}";
			response.getWriter().write(re);
		} finally {
			wareService.releaseAll();
			psService.releaseAll();
		}
		
	}
	/**
	 * 查询货位库存导出
	 * @param request
	 * @param response
	 * @return
	 * 2013/10/16
	 * 朱爱林
	 * @throws Exception 
	 */
	@RequestMapping("/searchProductStockAmountExport")
	public String searchProductStockAmountExport(HttpServletRequest request,HttpServletResponse response)
		throws Exception{
		
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		response.setContentType("text/html;charset=UTF-8");
		if(adminUser == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService wareService = new WareService(dbOperation);
		UserGroupBean group = adminUser.getGroup();
		String statusIds = "-1";
		if(!"".equals(ProductLinePermissionCache.getProductLineSupplierIds(adminUser))){
			statusIds = ProductLinePermissionCache.getProductLineSupplierIds(adminUser);
		}
		List supplierList = wareService.getSelects("supplier_standard_info ", "where status = 1 and id in(" + statusIds + ")");
		request.setAttribute("supplierList", supplierList);
		request.setCharacterEncoding("utf-8");
		
		int proxy = 0;	// 供应商id
		int suppliertext = StringUtil.StringToId(request.getParameter("suppliertext"));//供应商名称
		request.setAttribute("proxy", String.valueOf(proxy));
		
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
		int stockType =StringUtil.toInt(request.getParameter("stockType"));//库类型
		//System.out.println("---->"+stockType);
		int stockArea =StringUtil.toInt(request.getParameter("stockArea"));//库区域
		request.setAttribute("stockType", stockType);
		request.setAttribute("stockArea", stockArea);
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类3
		
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(suppliertext>0){
			condition.append(" and d.id="+suppliertext);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
		if(stockType>=0){
			condition.append(" and ps.type="+stockType);
		}
		if(stockArea>=0){
			condition.append(" and ps.area="+stockArea);
		}

		//得出已处理中和待处理状态的产品集合
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		try {
			List list = wareService.getProductList2(condition.toString(), -1,-1, "a.id asc");
		    //Hashtable ht=service.getOrderStockProductList(list);	
		    //request.setAttribute("ht", ht);
			
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));	
			}
			SearchProductStockExcel excel = new SearchProductStockExcel(SearchProductStockExcel.XSSF);
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header0 = new ArrayList<String>();//表头
			ArrayList<String> header2 = new ArrayList<String>();//表头
			//产品编号 	小店名称 	产品原名称
			
			header2.add("产品编号");
			header2.add("小店名称");
			header2.add("产品原名称");
			
			List<StockAreaBean> areaList = ProductStockBean.getStockAreaByType(stockType);
			boolean boo2 = group.isFlag(95);
			//field规则：库存数量用地区id，库存金额用地区id加_count
			if(stockArea==-1){
				for(StockAreaBean bean : areaList){
					//先放库存数量
					header2.add(bean.getName());
					//判断是否有95的权限，然后显示库存对应的金额数
					if(boo2){
						header2.add("金额("+bean.getName()+")");
					}
				}
			}else{
				//先放库存数量
				header2.add(ProductStockBean.getAreaName(stockArea));
				//判断是否有95的权限，然后显示库存对应的金额数
				if(boo2){
					header2.add("金额("+ProductStockBean.getAreaName(stockArea)+")");
				}
			}
			header2.add("库存总数");
			header2.add("总金额");
			header2.add("状态");
			header2.add("一级分类");
			header2.add("二级分类");
			header2.add("三级分类");
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			ArrayList<String> body2 = new ArrayList<String>();//数据
			
			BigDecimal total2 = new BigDecimal(0);//记录总金额
			int city_count = 0;
			double city_amount = 0.0d;
			int stock_count = 0;//库存总数
			BigDecimal stock_amount2 = new BigDecimal(0);//总金额
			//city_count city_amount
			//计算总金额的中间变量  stockcounts  price5
			BigDecimal price5 = null;
			for(int i=0;i<list.size();i++){
				body2 = new ArrayList<String>();//数据
				voProduct item = (voProduct) list.get(i);
				
				body2.add(item.getCode());
				body2.add(item.getName());
				body2.add(item.getOriname());
				
				stock_count = 0;
				//1、先计算所选库类型的总数量 stock_count
				for(StockAreaBean bean:areaList){
					stock_count = stock_count+(item.getStock(bean.getId(), stockType)+item.getLockCount(bean.getId(), stockType));
				}
				if(stock_count<=0){
					continue;
				}
				price5 = new BigDecimal(item.getPrice5());
				//2 、 计算库存总金额 total2
				stock_amount2 = new BigDecimal(stock_count).multiply(price5).setScale(2, BigDecimal.ROUND_HALF_UP);
				if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)) {
					total2 = total2.add(stock_amount2);
				}
				
				if(stockArea==-1){
					for(StockAreaBean bean:areaList){
						
						city_count = item.getStock(bean.getId(),stockType) + item.getLockCount(bean.getId(),stockType);
						if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
							city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
						}
						body2.add(city_count+"");
						body2.add(city_amount+"");
					}
				}else{
					city_count = item.getStock(stockArea,stockType) + item.getLockCount(stockArea,stockType);
					if(city_count<=0){
						continue;
					}
					if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
						city_amount = new  BigDecimal(city_count*item.getPrice5()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
					}
					body2.add(city_count+"");
					body2.add(city_amount+"");
				}
				body2.add(stock_count+"");
				if(group.isFlag(95)&&ProductLinePermissionCache.hasProductPermission(adminUser,item)){
					body2.add(stock_amount2.doubleValue()+"");
				}
				
				body2.add(item.getStatusName());
				body2.add(item.getParentId1()==0  ? "无" : item.getParent1().getName());
				body2.add(item.getParentId2()==0  ? "无" : item.getParent2().getName());
				body2.add(item.getParentId3()==0  ? "无" : item.getParent3().getName());
				bodies.add(body2);
			}
			if(group.isFlag(95)){
				header0.add("共有:"+list.size()+" 种产品 总金额: "+total2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
				for(int i=0;i<header2.size()-1;i++){
					header0.add("");
				}
				headers.add(header0);
				/*允许合并列,下标从0开始，即0代表第一列*/
				List<Integer> mayMergeColumn = new ArrayList<Integer>();
				mayMergeColumn.add(0);
				mayMergeColumn.add(header0.size());
				excel.setMayMergeColumn(mayMergeColumn);
				
				/*允许合并行,下标从0开始，即0代表第一行*/
				List<Integer> mayMergeRow = new ArrayList<Integer>();
				mayMergeRow.add(0);
	            excel.setMayMergeRow(mayMergeRow);
				/*
				 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
				 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
				 * 
				 * */
				excel.setColMergeCount(header0.size());
			}
//			headers.add(header);
			headers.add(header2);
			//调用填充表头方法
            excel.buildListHeader(headers);
            
            //用于标记是否是表头数据,一般设置样式时是否表头会用到
            excel.setHeader(false);
            
            //调用填充数据区方法
            excel.buildListBody(bodies);
            
            //文件输出
            excel.exportToExcel("分库库存查询导出_"+DateUtil.getNowDateStr(), response, "");
            
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			wareService.releaseAll();
			psService.releaseAll();
		}
		return null;
	}
}
