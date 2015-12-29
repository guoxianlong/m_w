/*
 * Created on 2007-10-24
 *
 */
package cache;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mmb.ware.WareService;
import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductLineCatalog;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.supplier.supplierProductLineBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-10-24
 * 
 * 说明：产品分类的缓存
 */
public class ProductLinePermissionCache {

	private static HashMap catalogIdsMap1 = new HashMap();
	private static HashMap catalogIdsMap2 = new HashMap();
	private static HashMap productLinePermissionMap = new HashMap();
	private static List permissionList = new ArrayList();
	
	private static HashMap productLineMap = new HashMap();
	private static HashMap productLineIdCarglogIdsMap = new HashMap();
	
	private static HashMap productBrandMap = new HashMap();
	static{
		init();
	}
	
	static void clearAll(){
		permissionList.clear();
		productBrandMap.clear();
		catalogIdsMap1.clear();
		catalogIdsMap2.clear();
		productLineMap.clear();
		productLineIdCarglogIdsMap.clear();
		productLinePermissionMap.clear();
	}
	
	public static void init() {
		clearAll();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService service = new WareService(dbOp);
		try{
			List productLineList = null;
			List productLineCatalogList = null;

			productLineList = service.getProductLineList("product_line.permission_id > 0 order by product_line.permission_id asc");
			productLineMap.put("productLine", productLineList);
			Iterator iter = productLineList.listIterator();
			while(iter.hasNext()){
				voProductLine productLine = (voProductLine)iter.next();
				int permissionId = productLine.getPermissionId();
				int productLineId = productLine.getId();
				String ids1 = "";
				String ids2 = "";
				StringBuffer bs=new StringBuffer("");
				permissionList.add(String.valueOf(permissionId));

				productLineCatalogList = service.getProductLineListCatalog("product_line_id = "+productLineId);
				for(int i=0;i<productLineCatalogList.size();i++){
					voProductLineCatalog bean = (voProductLineCatalog)productLineCatalogList.get(i);
					if(bean.getCatalog_type()==1){
						ids1 = ids1 + bean.getCatalog_id() + ",";
					}else if(bean.getCatalog_type()==2){
						ids2 = ids2 + bean.getCatalog_id() + ",";
					}
				}
				if(ids1.endsWith(",")){
					ids1 = ids1.substring(0, ids1.length()-1);
				}
				if(ids2.endsWith(",")){
					ids2 = ids2.substring(0, ids2.length()-1);
				}

				if(!ids1.equals("")){
					catalogIdsMap1.put(String.valueOf(permissionId), ids1);
					if(!ids2.equals("")){
						catalogIdsMap2.put(String.valueOf(permissionId), ids2);
						bs.append(" and (p.parent_id1 in(");
						bs.append(ids1);
						bs.append(")");
						bs.append(" or p.parent_id2 in(");
						bs.append(ids2);
						bs.append("))");
					}else{
						bs.append(" and p.parent_id1 in(");
						bs.append(ids1);
						bs.append(")");
					}
				}else{
					if(!ids2.equals("")){
						catalogIdsMap2.put(String.valueOf(permissionId), ids2);
						bs.append(" and ");
						bs.append(" p.parent_id2 in(");
						bs.append(ids2);
						bs.append(")");
					}
				}
				productLineIdCarglogIdsMap.put(String.valueOf(productLineId),bs.toString());
				productLinePermissionMap.put(String.valueOf(permissionId), String.valueOf(productLineId));
				
				//品牌
				String productBrandIds = "";
				ResultSet rs = service.getDbOp().executeQuery("select brand from product p where 1=1 "+bs.toString()+" group by brand");
				while(rs.next()){
					productBrandIds = productBrandIds + rs.getInt("brand") +",";
				}
				if(productBrandIds.endsWith(",")){
					productBrandIds = productBrandIds.substring(0,productBrandIds.length()-1);
				}
				if(!productBrandIds.equals("")){
					productBrandMap.put(String.valueOf(permissionId), productBrandIds);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}finally{	
			service.releaseAll();
		}

	}

	public static String getCatalogIds1(voUser user){

		UserGroupBean group = user.getGroup();
		String ids = "-1,";

		Iterator iter = permissionList.listIterator();
		while(iter.hasNext()){
			int permissionId = StringUtil.StringToId((String)iter.next());
			if(permissionId > 0){
				if(group.isFlag(permissionId)&&catalogIdsMap1.get(String.valueOf(permissionId))!=null){
					ids = ids + (String)catalogIdsMap1.get(String.valueOf(permissionId)) + ",";
				}
			}
		}

		if(ids.endsWith(",")){
			ids = ids.substring(0, ids.length()-1);
		}

		return ids;
	}

	public static String getCatalogIds2(voUser user){

		UserGroupBean group = user.getGroup();
		String ids = "-1,";

		Iterator iter = permissionList.listIterator();
		while(iter.hasNext()){
			int permissionId = StringUtil.StringToId((String)iter.next());
			if(permissionId > 0){
				if(group.isFlag(permissionId)&&catalogIdsMap2.get(String.valueOf(permissionId))!=null){
					ids = ids + (String)catalogIdsMap2.get(String.valueOf(permissionId)) + ",";
				}
			}
		}

		if(ids.endsWith(",")){
			ids = ids.substring(0, ids.length()-1);
		}

		return ids;
	}
	
	/**
	 * 
	 * 功能:获取当前用户 产品线权限 下 商品目录
	 * @param user
	 * @return
	 */
	public static String getCatalogIds(voUser user){
		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(user);
		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(user);
		if(!catalogIds2.equals("")){
			String[] splits = catalogIds2.split(",");
			for(int i=0;i<splits.length;i++){
				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					catalogIds1 = catalog.getId() + "," + catalogIds1;
				}
			}
			if(catalogIds1.endsWith(",")){
				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
			}
		}
		 
		return catalogIds1;
	} 
	
	public static String getProductLineIds(voUser user){
		UserGroupBean group = user.getGroup();
		String ids = "-1,";

		Iterator iter = permissionList.listIterator();
		while(iter.hasNext()){
			int permissionId = StringUtil.StringToId((String)iter.next());
			if(permissionId > 0){
				if(group.isFlag(permissionId)&&productLinePermissionMap.get(String.valueOf(permissionId))!=null){
					ids = ids + (String)productLinePermissionMap.get(String.valueOf(permissionId)) + ",";
				}
			}
		}

		if(ids.endsWith(",")){
			ids = ids.substring(0, ids.length()-1);
		}

		return ids;
	}

	public static String getProductLineSupplierIds(voUser user) {
		String ids = "";
		ISupplierService service = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE, null);
		try {
			String productLineIds = getProductLineIds(user);
			String condition = "product_line_id in (" + productLineIds + ") and supplier_id in ("
					+ "select id from supplier_standard_info where status = 1) group by supplier_id";
			List productLineSupplierList = service.getSupplierProductLineList(condition, -1, -1, "supplier_id");
			for (int i=0;productLineSupplierList!=null&&i<productLineSupplierList.size();i++) {
				supplierProductLineBean bean = (supplierProductLineBean) productLineSupplierList.get(i);
				ids += (bean.getSupplier_id() + ",");
			}
			if (ids.endsWith(",")) {
				ids = ids.substring(0, ids.length() - 1);
			}
		} finally {
			service.releaseAll();
		}
		return ids;
	}
	public static String getProductLineSupplierIds1(voUser user) {
		String ids = "";
		ISupplierService service = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE, null);
		try {
			String productLineIds = getProductLineIds(user);
			String condition = "product_line_id in (" + productLineIds + ") and supplier_id in ("
					+ "select id from supplier_standard_info ) group by supplier_id";
			List productLineSupplierList = service.getSupplierProductLineList(condition, -1, -1, "supplier_id");
			for (int i=0;productLineSupplierList!=null&&i<productLineSupplierList.size();i++) {
				supplierProductLineBean bean = (supplierProductLineBean) productLineSupplierList.get(i);
				ids += (bean.getSupplier_id() + ",");
			}
			if (ids.endsWith(",")) {
				ids = ids.substring(0, ids.length() - 1);
			}
		} finally {
			service.releaseAll();
		}
		return ids;
	}
	
	public static boolean hasProductPermission(voUser user, voProduct product){
		
		boolean result = false;
		
		UserGroupBean group = user.getGroup();
		String ids = "";
		Iterator iter = permissionList.listIterator();
		while(iter.hasNext()){
			int permissionId = StringUtil.StringToId((String)iter.next());
			if(permissionId > 0){
				if(group.isFlag(permissionId)&&catalogIdsMap1.get(String.valueOf(permissionId))!=null){
					ids = ids + (String)catalogIdsMap1.get(String.valueOf(permissionId)) + ",";
				}
				if(group.isFlag(permissionId)&&catalogIdsMap2.get(String.valueOf(permissionId))!=null){
					ids = ids + (String)catalogIdsMap2.get(String.valueOf(permissionId)) + ",";
				}
			}
		}

		if(ids.endsWith(",")){
			ids = ids.substring(0, ids.length()-1);
		}
		
		String[] idArray = ids.split(",");
		for(int i=0; i<idArray.length ;i++){
			int id = StringUtil.StringToId(idArray[i]);
			if(id > 0){
				if(id == product.getParentId1()){
					result = true;
					break;
				}else if(id == product.getParentId2()){
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * 功能:获取当天产品线下的 carglog id  并作好条件是返回 product表 必须以p 为别名
	 * <p>作者 李双 Sep 7, 2011 11:22:17 AM
	 * @param proudctLineIds
	 * @return
	 */
	public static String getCarglogIdsByProudctId(String proudctLineId){
		 
		String query = (String)productLineIdCarglogIdsMap.get(proudctLineId);
		if(query!=null){
			return query ;
		}
		WareService service = new WareService();
		try{
			List productLineCatalogList = service.getProductLineListCatalog("product_line_catalog.product_line_id = "+proudctLineId);
			String ids1="",ids2="";
			StringBuffer bs = new StringBuffer("");
			for(int i=0;i<productLineCatalogList.size();i++){
				voProductLineCatalog bean = (voProductLineCatalog)productLineCatalogList.get(i);
				if(bean.getCatalog_type()==1){
					ids1 = ids1 + bean.getCatalog_id() + ",";
				}else if(bean.getCatalog_type()==2){
					ids2 = ids2 + bean.getCatalog_id() + ",";
				}
			}
			if(ids1.endsWith(",")){
				ids1 = ids1.substring(0, ids1.length()-1);
			}
			if(ids2.endsWith(",")){
				ids2 = ids2.substring(0, ids2.length()-1);
			}

			if(!ids1.equals("")){
 				if(!ids2.equals("")){
					bs.append(" and (p.parent_id1 in(");
					bs.append(ids1);
					bs.append(")");
					bs.append(" or p.parent_id2 in(");
					bs.append(ids2);
					bs.append("))");
				}else{
					bs.append(" and p.parent_id1 in(");
					bs.append(ids1);
					bs.append(")");
				}
			}
			if(!ids2.equals("")){
 				bs.append(" and ");
				bs.append(" p.parent_id2 in(");
				bs.append(ids2);
				bs.append(")");
			}
			productLineIdCarglogIdsMap.put(proudctLineId, bs.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return (String)productLineIdCarglogIdsMap.get(proudctLineId);
	}
	
	/**
	 * 
	 * 功能:获取当前用户下产品线 List voProductLine
	 * <p>作者 李双 Sep 7, 2011 2:25:35 PM
	 * @param user
	 * @return
	 */
	public static List getProductList(voUser user){
		List productLineList = new ArrayList();
		List userProductLineList = new ArrayList();
		UserGroupBean group = user.getGroup();
		
		if(productLineMap.get("productLine")!=null){
			productLineList=(List)productLineMap.get("productLine") ;
			if(productLineList!=null && productLineList.size()>0)
				for(int i=0;i<productLineList.size();i++){
					voProductLine vo = (voProductLine)productLineList.get(i);
					int permissionId = vo.getPermissionId();
					if(permissionId > 0){
						if(group.isFlag(permissionId)&&productLinePermissionMap.get(String.valueOf(permissionId))!=null){
							userProductLineList.add(vo);
						}
					}
				}
				
				return userProductLineList;
		}
		initProductLine();
		for(int i=0;i<productLineList.size();i++){
			voProductLine vo = (voProductLine)productLineList.get(i);
			int permissionId = vo.getPermissionId();
			if(permissionId > 0){
				if(group.isFlag(permissionId)&&productLinePermissionMap.get(String.valueOf(permissionId))!=null){
					userProductLineList.add(vo);
				}
			}
		}
		
		return userProductLineList;
	}
	
	/**
	 * 
	 * 功能:获取 所有产品线
	 * <p>作者 李双 Dec 14, 2011 10:02:47 AM
	 * @param user
	 * @return
	 */
	public static List getAllProductLineList(){
		List productLineList = new ArrayList();
		if(productLineMap.get("productLine")!=null){
			productLineList=(List)productLineMap.get("productLine") ;
		}else{
			initProductLine();
			productLineList=(List)productLineMap.get("productLine");
		}
		return productLineList;
	}
	
	/**
	 * 
	 * 功能:初始化 产品线!
	 * <p>作者 李双 Dec 14, 2011 10:07:37 AM
	 */
	public static void initProductLine(){
		WareService service = new WareService();
		List productLineList = new ArrayList();
		try{
			productLineList = service.getProductLineList("product_line.permission_id > 0 order by product_line.permission_id asc");
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		productLineMap.put("productLine", productLineList);
	}
	
	/**
	 * 说明：获取产品线权限下品牌
	 * 
	 */
	public static String getProductBrandIds(voUser user,int productLineId){
		UserGroupBean group = user.getGroup();
		String ids = "-1,";

		Iterator iter = permissionList.listIterator();
		while(iter.hasNext()){
			int permissionId = StringUtil.StringToId((String)iter.next());
			if(permissionId > 0){
				if(group.isFlag(permissionId)&&productBrandMap.get(String.valueOf(permissionId))!=null){
					if(productLineId == 0 || productLineId == StringUtil.toInt((String)productLinePermissionMap.get(String.valueOf(permissionId)))){
						ids = ids + (String)productBrandMap.get(String.valueOf(permissionId)) + ",";
					}
				}
			}
		}

		if(ids.endsWith(",")){
			ids = ids.substring(0, ids.length()-1);
		}

		return ids;
	}
}
