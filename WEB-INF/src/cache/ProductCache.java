package cache;

import java.sql.ResultSet;
import java.util.HashMap;

import adultadmin.action.vo.voSelect;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 说明：产品相关信息缓存
 *
 */
public class ProductCache {
	public static HashMap productStatusMap = new HashMap();
	
	static{
		init();
	}
	
	public static void init(){
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		dbOp.init("adult_slave");
		try{
			ResultSet rs = dbOp.executeQuery("select * from product_status");
			while(rs.next()){
				voSelect vo = new voSelect();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				productStatusMap.put(Integer.valueOf(rs.getInt("id")), vo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}

	public static String getProductStatusName(int status){
		String name = ""; 
        voSelect vo = (voSelect)productStatusMap.get(Integer.valueOf(status));
        if(vo != null){
        	name = vo.getName();
        }
        
		return name;
	}
}
