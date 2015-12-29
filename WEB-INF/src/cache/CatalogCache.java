/*
 * Created on 2007-10-24
 *
 */
package cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import mmb.ware.WareService;
import adultadmin.action.vo.voCatalog;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-10-24
 * 
 * 说明：产品分类的缓存
 */
public class CatalogCache {
	public static String GROUP = "catalog";

    public static int TIME = 3600 * 24;
	
    public static HashMap catalogs = new HashMap();        //分类信息Map
	public static List catalogLevelList = new ArrayList(); //分类分级关系
	
	static{
		init();
	}
	
    public static void init() {
    	
    	catalogs = new HashMap();
    	catalogLevelList = new ArrayList();
    	
    	WareService service = new WareService();
    	try{
    		//一级分类
    		List firstList = service.getCatalogs("parent_id=0 and hide=0");
    		
    		HashMap firstMap = new HashMap();
    		firstMap.put(Integer.valueOf(0), firstList);
    		catalogLevelList.add(firstMap);
    		HashMap secondMap = new HashMap();
    		catalogLevelList.add(secondMap);
    		HashMap thirdMap = new HashMap();
    		catalogLevelList.add(thirdMap);
    		
    		Iterator firstIterator = firstList.listIterator();
    		voCatalog catalog = null;
    		while(firstIterator.hasNext()){
    			catalog = (voCatalog)firstIterator.next();
    			catalogs.put(Integer.valueOf(catalog.getId()), catalog);
    			List secondList = service.getCatalogs("parent_id="+catalog.getId()+" and hide=0"); //二级分类
    			if(secondList==null||secondList.size()==0){
    				voCatalog newCatalog = new voCatalog();
    				newCatalog.setId(0);
    				newCatalog.setName("默认二级分类");
    				secondList.add(newCatalog);
    			}
    			secondMap.put(Integer.valueOf(catalog.getId()), secondList);
    			
    			Iterator secondIterator = secondList.listIterator();
    			
    			while(secondIterator.hasNext()){
    				catalog = (voCatalog)secondIterator.next();
    				if(catalog.getId()==0){
    					continue;
    				}
        			catalogs.put(Integer.valueOf(catalog.getId()), catalog);
        			List thirdList = service.getCatalogs("parent_id="+catalog.getId()+" and hide=0"); //三级分类
        			thirdMap.put(Integer.valueOf(catalog.getId()), thirdList);
        			Iterator thirdIterator = thirdList.listIterator();
        			while(thirdIterator.hasNext()){
        				catalog = (voCatalog)thirdIterator.next();
            			catalogs.put(Integer.valueOf(catalog.getId()), catalog);
        			}
    			}
    			
    		}
    		System.out.println();
    	}finally{
    		service.releaseAll();
    	}
		
    }
    
    
public static void init2() {
    	
    	catalogs = new HashMap();
    	catalogLevelList = new ArrayList();

    	WareService service = new WareService();
    	try{
    		//一级分类
    		List firstList = service.getCatalogs("parent_id=0");
    		
    		HashMap firstMap = new HashMap();
    		firstMap.put(Integer.valueOf(0), firstList);
    		catalogLevelList.add(firstMap);
    		HashMap secondMap = new HashMap();
    		catalogLevelList.add(secondMap);
    		HashMap thirdMap = new HashMap();
    		catalogLevelList.add(thirdMap);
    		
    		Iterator firstIterator = firstList.listIterator();
    		voCatalog catalog = null;
    		while(firstIterator.hasNext()){
    			catalog = (voCatalog)firstIterator.next();
    			catalogs.put(Integer.valueOf(catalog.getId()), catalog);
    			List secondList = service.getCatalogs("parent_id="+catalog.getId()); //二级分类
    			if(secondList==null||secondList.size()==0){
    				voCatalog newCatalog = new voCatalog();
    				newCatalog.setId(0);
    				newCatalog.setName("默认二级分类");
    				secondList.add(newCatalog);
    			}
    			secondMap.put(Integer.valueOf(catalog.getId()), secondList);
    			
    			Iterator secondIterator = secondList.listIterator();
    			
    			while(secondIterator.hasNext()){
    				catalog = (voCatalog)secondIterator.next();
    				if(catalog.getId()==0){
    					continue;
    				}
        			catalogs.put(Integer.valueOf(catalog.getId()), catalog);
        			List thirdList = service.getCatalogs("parent_id="+catalog.getId()); //三级分类
        			thirdMap.put(Integer.valueOf(catalog.getId()), thirdList);
        			Iterator thirdIterator = thirdList.listIterator();
        			while(thirdIterator.hasNext()){
        				catalog = (voCatalog)thirdIterator.next();
            			catalogs.put(Integer.valueOf(catalog.getId()), catalog);
        			}
    			}
    			
    		}
    		System.out.println();
    	}finally{
    		service.releaseAll();
    	}
		
    }
    
    
    public static voCatalog getCatalog(int id){
    	voCatalog catalog = (voCatalog)catalogs.get(Integer.valueOf(id));
    	if(catalog==null){
    		catalog = new voCatalog();
    		catalog.setId(-1);
    		catalog.setName("错误分类");
    	}
    	return catalog;
    }
    
    public static voCatalog getParentCatalog(int id){
    	voCatalog catalog = (voCatalog)catalogs.get(Integer.valueOf(id));
    	if(catalog==null){
    		catalog = new voCatalog();
    		catalog.setId(-1);
    		catalog.setName("错误分类");
    	}else{
    		if(catalogs.get(Integer.valueOf(catalog.getParentId()))!=null){
    			catalog = (voCatalog)catalogs.get(Integer.valueOf(catalog.getParentId()));
    		}
    	}
    	return catalog;
    }
    
    public static HashMap getFirstMap(){
    	HashMap map = new HashMap();
    	if(catalogLevelList.get(0)!=null){
    		HashMap oriMap = (HashMap)catalogLevelList.get(0);
    		map = (HashMap)oriMap.clone();
    	}
    	
    	return map;
    }
    
    public static HashMap getSecondMap(){
    	HashMap map = new HashMap();
    	if(catalogLevelList.get(0)!=null){
    		HashMap oriMap = (HashMap)catalogLevelList.get(1);
    		map = (HashMap)oriMap.clone();
    	}
    	
    	return map;
    }
    
    public static HashMap getThirdMap(){
    	HashMap map = new HashMap();
    	if(catalogLevelList.get(0)!=null){
    		HashMap oriMap = (HashMap)catalogLevelList.get(2);
    		map = (HashMap)oriMap.clone();
    	}
    	
    	return map;
    }
}
