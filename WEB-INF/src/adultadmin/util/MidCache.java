/*
 * Created on 2007-6-8
 *
 */
package adultadmin.util;

import java.sql.ResultSet;
import java.sql.SQLException;

import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-6-8
 * 
 * 说明：
 */
public class MidCache {
    public static String CACHE_GROUP = "mid_mobile";
    
    public static int FLUSH_PERIOD = 3600;
    
    public static String getMobileByMid(String mid){
        if(mid == null){
            return null;
        }
        mid = mid.replaceAll("'", "");
        
        //从缓存中取
        String mobile = (String) CacheAdmin.getFromCache(mid, CACHE_GROUP, FLUSH_PERIOD);
        if(mobile != null){
            return mobile;
        }
        
        //从数据库中取
        DbOperation dbOp = new DbOperation();
        dbOp.init();
        ResultSet rs = dbOp.executeQuery("select real_mobile from user_status where phone = '" + mid + "'");
        try {
            if(rs.next()){
                mobile = rs.getString("real_mobile");
            }            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dbOp.release();
        
        if(mobile == null){
            mobile = "";
        }
        
        //放入缓存
        CacheAdmin.putInCache(mid, mobile, CACHE_GROUP, FLUSH_PERIOD);
        return mobile;
    }
}
