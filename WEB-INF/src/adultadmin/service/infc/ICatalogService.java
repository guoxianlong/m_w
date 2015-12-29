/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;
import java.util.Map;

import adultadmin.action.vo.voCatalog;

/**
 * 作者：张陶
 * 
 * 创建日期：2007-5-12
 * 
 * 说明：
 */
public interface ICatalogService extends IBaseService {
	public ArrayList getCatalogList(String condition, int index, int count,
			String orderBy);

	public voCatalog getCatalog(String condition);

	public int getCatalogCount(String condition);

	public boolean addCatalog(voCatalog catalog);

	public boolean updateCatalog(String set, String condition);

	public boolean deleteCatalog(String condition);

	public Object[] getNPCatalog(String listCondition, int currentId,
			String orderBy, int[] npType);

	public Map getCatalogMap(String condition, int index, int count);
	
	//获取类似产品分类名称的ID
	public String getCatalogIdsLikeName(String catalogname,String catalogids);
}
