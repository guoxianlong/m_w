/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.impl;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import adultadmin.bean.system.TextDictBean;
import adultadmin.bean.system.TextResBean;
import adultadmin.service.infc.ISystemService;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2008-12-31
 * 
 * 说明：公告、全站消息
 */
public class SystemServiceImpl extends BaseServiceImpl implements
		ISystemService {
	public SystemServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public SystemServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addTextRes(TextResBean bulletin) {
		return addXXX(bulletin, "text_res");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteTextRes(String condition) {
		return deleteXXX(condition, "text_res");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public TextResBean getTextRes(String condition) {
		Object queryObject = null;
		queryObject = (TextResBean) getXXX(condition, "text_res",
				"adultadmin.bean.system.TextResBean");

		return (TextResBean) queryObject;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getTextResCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "text_res", "id");
		return count;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getTextResList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "text_res",
				"adultadmin.bean.system.TextResBean");
		return (ArrayList) queryList;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateTextRes(String set, String condition) {
		return updateXXX(set, condition, "text_res");
	}

	
	public boolean addTextDict(TextDictBean bulletin) {
		return addXXX(bulletin, "text_dict");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteTextDict(String condition) {
		return deleteXXX(condition, "text_dict");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public TextDictBean getTextDict(String condition) {
		Object queryObject = null;
		queryObject = (TextDictBean) getXXX(condition, "text_dict",
				"adultadmin.bean.system.TextDictBean");

		return (TextDictBean) queryObject;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getTextDictCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "text_dict", "id");
		return count;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getTextDictList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "text_dict",
				"adultadmin.bean.system.TextDictBean");
		return (ArrayList) queryList;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateTextDict(String set, String condition) {
		return updateXXX(set, condition, "text_dict");
	}
	
	public Map getTextDictNumMap(){
		Map map = new HashMap();
		DbOperation db = getDbOp();
		ResultSet rs = db.executeQuery("select type,count(id) from text_dict group by type");
		try{
			while(rs.next()){
				map.put(rs.getString(1), rs.getString(2));
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			this.release(db);
		}
		return map ;
	}
}
