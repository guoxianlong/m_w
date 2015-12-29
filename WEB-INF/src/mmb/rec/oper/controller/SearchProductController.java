package mmb.rec.oper.controller;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.ware.WareService;
import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;


/**
 * 说明：产品查询
 * 
 * @author 张晔
 *
 * 时间：2012.09.20
 */
@Controller
@RequestMapping("/SearchProductController")
public class SearchProductController {
	public static String noSession = "/admin/rec/oper/salesReturned/noSession.jsp";
	//查询产品信息
	@RequestMapping("/searchProductList")
	@ResponseBody
	public EasyuiDataGridJson searchProductList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser loginUser = (voUser)request.getSession().getAttribute("userView");
		UserGroupBean group = loginUser.getGroup();
		String proxyId = StringUtil.convertNull(request.getParameter("proxy"));
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		//增加条码查询
		String barcode= StringUtil.dealParam(request.getParameter("barcode"));
		//增加产品ID查询
		int productId= StringUtil.toInt(StringUtil.dealParam(request.getParameter("productId")));
		String price = request.getParameter("price");
		String minPrice = request.getParameter("minPrice");
		String maxPrice = request.getParameter("maxPrice");
		String minPrice5 = StringUtil.convertNull(request.getParameter("minPrice5"));
		String maxPrice5 = StringUtil.convertNull(request.getParameter("maxPrice5"));
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		int parentId2 = StringUtil.toInt(request.getParameter("parentId2"));
		int parentId3 = StringUtil.toInt(request.getParameter("parentId3"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		int brand = StringUtil.toInt(request.getParameter("brand")); //满折扣过来的有品牌值
		String catalogIds = StringUtil.convertNull(request.getParameter("catalogIds")); //满折扣过来的有一级分类多选
		//----cxq
		int minProductStock=StringUtil.StringToId(request.getParameter("minProductStock"));
		int maxProductStock=StringUtil.StringToId(request.getParameter("maxProductStock"));
		//主子商品，查询------flag=0或者2为主商品，1,3为子商品
		int productType=StringUtil.StringToId(request.getParameter("products"));

		if (name == null)
			name = "";
		if (code == null)
			code = "";
		if (barcode == null)
			barcode = "";
		if (price == null)
			price = "";
		if (minPrice == null)
			minPrice = "";
		if (maxPrice == null)
			maxPrice = "";


		String[] strProductIds = request.getParameterValues("id");
		String[] product_statuss = request.getParameterValues("product_status");
		String product_status = "";
		if(product_statuss!=null){
			for(int i=0;i<product_statuss.length;i++){
				product_status = product_status + product_statuss[i] +",";
			}
			if(product_status.endsWith(",")){
				product_status = product_status.substring(0, product_status.length()-1);
			}
		}
		int commodityStatus = StringUtil.parstBackMinus(request.getParameter("commodityStatus"));//售后过来的查询，需要查询订单状态少于100的 限制不要
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService service = new WareService(dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, psService.getDbOp());
		//        IPresentProductService presentService = ServiceFactory.createPresentProductService(IBaseService.CONN_IN_SERVICE, null);
		String a="0";
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		try {
			if (productId<0&&name.length() == 0 && code.length() == 0 && barcode.length() == 0 && price.length() == 0 && minPrice5.length() == 0 && maxPrice5.length() == 0 
					&& minPrice.length() == 0 && maxPrice.length() == 0 && parentId1 <= 0 && parentId2 <= 0 && parentId3 <= 0 && (strProductIds == null || strProductIds.length==0) 
					&& startTime.length() == 0 && endTime.length() == 0 && product_status.equals("") && brand<=0 && catalogIds.length()==0 &&  proxyId.equals("")){
				easyuiDataGridJson.setTotal((long) 0);
				easyuiDataGridJson.setRows(new ArrayList());
				return easyuiDataGridJson;
			}

			StringBuilder buf = new StringBuilder();
			buf.append(" (pb.barcode_status is null or pb.barcode_status=0) ");
			if(strProductIds == null || strProductIds.length == 0){
				if(!StringUtil.isNull(name)){
					name = StringUtil.toSqlLike(request.getParameter("name"));
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.name like '%");
					buf.append(name);
					buf.append("%' or a.oriname like '%");
					buf.append(name);
					buf.append("%') ");
				}
				if (!StringUtil.isNull(code)){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.code='");
					buf.append(code);
					buf.append("' ");
				}
				if (!StringUtil.isNull(barcode)){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" pb.barcode='");
					buf.append(barcode.trim());
					buf.append("'");
				}
				if (!StringUtil.isNull(price)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					price = StringUtil.toSqlLike(request.getParameter("price"));
					buf.append(" (round(a.price,2) like '%");
					buf.append(price);
					buf.append("%' or round(a.price2,2) like '%");
					buf.append(price);
					buf.append("%')");
				}
				if (!StringUtil.isNull(minPrice)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price >= ");
					buf.append(minPrice);
					buf.append(")");
				}
				if (!StringUtil.isNull(maxPrice)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price <= ");
					buf.append(maxPrice);
					buf.append(")");
				}
				if (!minPrice5.equals("")) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price5 >= ");
					buf.append(minPrice5);
					buf.append(")");
				}
				if (!maxPrice5.equals("")) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price5 <= ");
					buf.append(maxPrice5);
					buf.append(")");
				}
				if(productId >= 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.id=");
					buf.append(productId);
				}
				if(parentId1 > 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.parent_id1=");
					buf.append(parentId1);
				}
				if(parentId2 > 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.parent_id2=");
					buf.append(parentId2);
				}
				if(parentId3 > 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.parent_id3=");
					buf.append(parentId3);
				}

				if (!StringUtil.isNull(startTime) &&!StringUtil.isNull(endTime)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (left(a.create_datetime,10) between '");
					buf.append(startTime);
					buf.append("' and '");
					buf.append(endTime);
					buf.append("')");
				}
				if(!group.isFlag(89)){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.status <> 100");
				}

				if(!product_status.equals("")){
					buf.append(" and ");
					buf.append("b.id in (");
					buf.append(product_status);
					buf.append(")");
				}
				if(brand>0){
					buf.append(" and ");
					buf.append("a.brand=");
					buf.append(brand);
				}
				if(parentId1 < 0){
					if(!catalogIds.trim().equals("")&&catalogIds.length()>0){
						buf.append(" and ");
						buf.append("a.parent_id1 in(");
						buf.append(catalogIds);
						buf.append(")");
					}
				}
				if(!proxyId.equals("")){
					buf.append(" and d.id = ");
					buf.append(proxyId);
					//params = params + "&proxy="+proxyId;
				}
	            if(commodityStatus>0){//缺货和废弃状态的商品不能被添加和搜索到
	            	buf.append(" and a.status<").append(commodityStatus);
	            }
        	} else {
				if(buf.length() > 0){
					buf.append(" and ");
				}
				buf.append("a.id in (");
				for(int i=0; i<strProductIds.length; i++){
					if(i != 0){
						buf.append(",");
					}
					buf.append(strProductIds[i]);
				}
				buf.append(")");
			}
			String flagPresent = request.getParameter("flagPresent");
			List list = null;

			if(flagPresent!=null && flagPresent.equals("present")){
				list = service.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc",flagPresent);
			}else{
				list = service.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc");
			}
			if(list==null){
				easyuiDataGridJson.setTotal((long)0);
			}
			else{
				easyuiDataGridJson.setTotal((long)list.size());
			}
			Iterator iter = list.listIterator();
			List newList=new ArrayList();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
				product.setPsList(psList);
				String ProductLinePermissioncatalogIds = ProductLinePermissionCache.getCatalogIds(loginUser);
				HashMap map1 = (HashMap)CatalogCache.catalogLevelList.get(0);
				List list1 = (List)map1.get(Integer.valueOf(0));
				Iterator iter1 = list1.listIterator();
				//第一级老分类ID
				String oldCatalogFirstId=product.getParentId1()+"";
				boolean flag=false;
				while(iter1.hasNext()){
					voCatalog catalog = (voCatalog)iter1.next();
					if(StringUtil.hasStrArray(ProductLinePermissioncatalogIds.split(","),String.valueOf(catalog.getId()))){
						if(catalog.getId()==StringUtil.StringToId(oldCatalogFirstId)){
							flag=true;
							break;
						}
					}
				}
				product.setPrice5Flag(flag);
				//库存总数
				int allStock = product.getStock(0) + product.getStock(1) + product.getStock(2)+product.getStock(3)+product.getStock(4) + product.getLockCount(0) + product.getLockCount(1) + product.getLockCount(2) + product.getLockCount(3) + product.getLockCount(4);
				product.setAllStockCount(allStock);
				//--------------------------------
				//可发货总数
				int stock=product.getStock(0, 0) + product.getStock(1, 0) + product.getStock(2, 0)+ product.getStock(3, 0)+ product.getStock(4, 0);
				product.setCanBeShipStock(stock);
				if(maxProductStock>0){
					if(stock<=maxProductStock&&stock>=minProductStock){
						newList.add(product);
					}else{
						continue;
					}
				}
				//********************************
				product.setCargoPSList(cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId(), -1, -1, "ci.whole_code asc"));
				//如果供应商查询条件不为空，则查找该产品所有的供应商并显示出来
				ResultSet rs = null;
				String sqlSupplier = "select GROUP_CONCAT(d.name)sName from product p left outer join product_supplier c on p.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 " + " where  p.id=" + product.getId();
				rs = service.getDbOp().executeQuery(sqlSupplier);
				while (rs.next()) {
					String proxyName = StringUtil.convertNull(rs.getString("sName"));
					product.setProxyName(proxyName);
				}
				rs.close();
			}
			//cxq-----
			if(maxProductStock>0){
				list=newList;
				easyuiDataGridJson.setTotal((long)list.size());
			}
			easyuiDataGridJson.setRows(list);
			return easyuiDataGridJson;

		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("msg", "程序异常!");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}finally {
			psService.releaseAll();
		}
	}
	
	/**
	 * 一级分类
	 */
	@RequestMapping("/getparentId1")
	@ResponseBody
	public String getparentId1(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
		List list = (List)map.get(Integer.valueOf(0));
		Iterator iter = list.listIterator();
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("0");
		bean.setText("");
		bean.setSelected(true);
		easyuilist.add(bean);
		while(iter.hasNext()){
			voCatalog catalog = (voCatalog)iter.next();
			bean = new EasyuiComBoBoxBean();
			bean.setId("" + catalog.getId());
			bean.setText(catalog.getName());
			easyuilist.add(bean);
		}
		return JSONArray.fromObject(easyuilist).toString();
	}
	
	/**
	 * 二级分类
	 */
	@RequestMapping("/getparentId2")
	@ResponseBody
	public String getparentId2(HttpServletRequest request, HttpServletResponse response)throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		
		boolean permission = StringUtil.toBoolean(request.getParameter("permission"));
		
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		if (parentId1 == -1 ) {
			return JSONArray.fromObject(new ArrayList()).toString();
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
		
		response.setContentType("text/html; charset=utf-8");
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		HashMap secondMap = CatalogCache.getSecondMap();
		int level = 0;
			
		if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
			level = 2;
		}else{
			level = 1;
		}
		if(permission && !StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(parentId1))){
			bean = new EasyuiComBoBoxBean();
			bean.setId("0");
			bean.setText("不限");
			easyuilist.add(bean);
			return JSONArray.fromObject(easyuilist).toString();
		}
		
		//二级
		List secondList = (List)secondMap.get(Integer.valueOf(parentId1));
		if(secondList==null){
			bean = new EasyuiComBoBoxBean();
			bean.setId("0");
			bean.setText("不限");
			easyuilist.add(bean);
		}else{
			int indexSecond = 0;
			for(int j=0;j<secondList.size();j++){
				voCatalog catalog = (voCatalog)secondList.get(j);
				if(permission && level == 2 && !StringUtil.hasStrArray(catalogIds2.split(","),String.valueOf(catalog.getId()))){
					continue;
				}
				bean = new EasyuiComBoBoxBean();
				bean.setId("" + catalog.getId());
				bean.setText(catalog.getName());
				easyuilist.add(bean);
			}
		}
		return JSONArray.fromObject(easyuilist).toString();
	}
	
	
	
	/**
	 * 三级分类
	 */
	@RequestMapping("/getparentId3")
	@ResponseBody
	public String getparentId3(HttpServletRequest request, HttpServletResponse response)throws Exception{
		
		int parentId2 = StringUtil.toInt(request.getParameter("parentId2"));
		if (parentId2 == -1 ) {
			return JSONArray.fromObject(new ArrayList()).toString();
		}
		response.setContentType("text/html; charset=utf-8");
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		HashMap thirdMap = CatalogCache.getThirdMap();
		//三级
		List thirdList = (List)thirdMap.get(Integer.valueOf(parentId2));
		if(thirdList == null||thirdList.size()==0){
			bean = new EasyuiComBoBoxBean();
			bean.setId("0");
			bean.setText("不限");
			easyuilist.add(bean);
		}else{
			for(int k=0;k<thirdList.size();k++){
				voCatalog catalog = (voCatalog)thirdList.get(k);
				bean = new EasyuiComBoBoxBean();
				bean.setId("" + catalog.getId());
				bean.setText(catalog.getName());
				easyuilist.add(bean);
			}
		}
		return JSONArray.fromObject(easyuilist).toString();
	}

	
	/**
	 * 供应商
	 */
	@RequestMapping("/getProxyList")
	@ResponseBody
	public String getProxyList(HttpServletRequest request, HttpServletResponse response)throws Exception{
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService service = new WareService(dbOp);
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
		response.setContentType("text/html; charset=utf-8");
		List proxyList = service.getSelects("supplier_standard_info ", "where status=1 order by id");
		Iterator iter = proxyList.listIterator();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("0");
		bean.setText("");
		bean.setSelected(true);
		easyuilist.add(bean);
		while(iter.hasNext()){
			voSelect select = (voSelect)iter.next();
			bean = new EasyuiComBoBoxBean();
			bean.setId("" + select.getId());
			bean.setText(select.getName());
			easyuilist.add(bean);
		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return JSONArray.fromObject(easyuilist).toString();
	}
	
	/**
	 * 产品状态
	 */
	@RequestMapping("/getProductStatus")
	@ResponseBody
	public String getProductStatus(HttpServletRequest request, HttpServletResponse response)throws Exception{
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService service = new WareService(dbOp);
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
		response.setContentType("text/html; charset=utf-8");
		List statusList = service.getSelects("product_status","order by id");
		Iterator iter = statusList.listIterator();
		EasyuiComBoBoxBean bean = null;
		while(iter.hasNext()){
			voSelect select = (voSelect)iter.next();
			bean = new EasyuiComBoBoxBean();
			bean.setId("" + select.getId());
			bean.setText(select.getName());
			easyuilist.add(bean);
		}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return JSONArray.fromObject(easyuilist).toString();
	}
	
}
