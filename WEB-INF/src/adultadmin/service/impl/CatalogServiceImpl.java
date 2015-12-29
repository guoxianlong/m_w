/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voCatalog;
import adultadmin.service.infc.ICatalogService;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-1-24
 * 
 * 说明：
 */
public class CatalogServiceImpl extends BaseServiceImpl implements
		ICatalogService {
	public CatalogServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public CatalogServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addCatalog(voCatalog catalog) {
		return addXXX(catalog, "catalog");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteCatalog(String condition) {
		return deleteXXX(condition, "catalog");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public voCatalog getCatalog(String condition) {
		Object queryObject = null;
		queryObject = (voCatalog) getXXX(condition, "catalog",
				"adultadmin.action.vo.voCatalog");

		return (voCatalog) queryObject;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getCatalogCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "catalog", "id");
		return count;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getCatalogList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "catalog",
				"adultadmin.action.vo.voCatalog");
		return (ArrayList) queryList;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateCatalog(String set, String condition) {
		return updateXXX(set, condition, "catalog");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public Object[] getNPCatalog(String listCondition, int currentId,
			String orderBy, int[] npType) {
		Object queryList = getNPXXX(listCondition, currentId, orderBy, npType,
				"catalog", "adultadmin.action.vo.voCatalog");
		return (Object[]) queryList;
	}

	public Map getCatalogMap(String condition, int index, int count) {
		List catalogList = this.getCatalogList(condition, index, count, null);
		HashMap catalogMap = new HashMap();
		Iterator iter = catalogList.listIterator();
		while (iter.hasNext()) {
			voCatalog catalog = (voCatalog) iter.next();
			catalogMap.put(Integer.valueOf(catalog.getId()), catalog);
		}
		return catalogMap;
	}
	public String getCatalogIdsLikeName(String catalogname,String catalogids){
        String sql="select id from catalog where  name like '%"+catalogname+"%' and id in "+catalogids;
        DbOperation dbOp = this.getDbOp();
        ResultSet rs=dbOp.executeQuery(sql);
        StringBuffer sb=new StringBuffer();
        sb.append("(");
        try {
        while(rs.next()){
       	 sb.append(rs.getInt("id")).append(",");       	 
        }       
        } catch (Exception e) {
    		e.printStackTrace();
    	}finally{
			dbOp.releaseWithoutConn();
		}
    	String sbstr="";
    	if(sb.length()>2){
    		sbstr=sb.toString().substring(0, sb.length()-1)+")";
    	}else{
    		sbstr="";
    	}
		return sbstr;
	}
}
