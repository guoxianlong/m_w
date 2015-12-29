/*
 * Created on 2008-6-2
 *
 */
package stat;

import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-6-2
 * 
 * 说明：
 */
public class Cleaner {

    public static void main(String[] args) {
        cleanOnlineLog();
    }
    
    public static void cleanAccessLog(){
        int minId = 53761101;
        int maxId = 70599829;
        DbOperation dbOp = new DbOperation();
        dbOp.init();
        int id = minId;
        String sql = null;
        while(true){            
            id += 10;
            if(id > maxId){
                id = maxId;
            }
            sql = "delete from access_log where id <= " + id;
            System.out.println(sql);
            dbOp.executeUpdate(sql);            
            if(id == maxId){
                break;
            }
        }        
        dbOp.release();
    }
    
    public static void cleanOnlineLog(){
        int minId = 47265769;
        int maxId = 64210098;
        DbOperation dbOp = new DbOperation();
        dbOp.init();
        int id = minId;
        String sql = null;
        while(true){            
            id += 10;
            if(id > maxId){
                id = maxId;
            }
            sql = "delete from online_log where id <= " + id;
            System.out.println(sql);
            dbOp.executeUpdate(sql);            
            if(id == maxId){
                break;
            }
        }        
        dbOp.release();
    }
}
