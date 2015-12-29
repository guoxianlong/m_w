/*
 * Created on 2006-8-19
 *
 */
package adultadmin.service.impl;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ormap.MapField;
import ormap.Mapping;
import ormap.OrMap;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2006-8-19
 * 
 * 说明：基础服务的实现。
 */
public class BaseServiceImpl implements IBaseService {
	
	public Log stockLog = LogFactory.getLog("stock.Log");
	
    /**
     * 作者：
     * 
     * 创建日期：2006-8-19
     * 
     * 说明：使用数据库连接的类型，可以是CONN_IN_METHOD，CONN_IN_SERVICE，CONN_FROM_OUTSIDE。
     */
    protected int useConnType = CONN_IN_SERVICE;

    /**
     * 作者：
     * 
     * 创建日期：2006-8-19
     * 
     * 说明：数据库操作类。
     */
    protected DbOperation dbOp;

    /**
     * @param useConnType
     * @param dbOp
     */
    public BaseServiceImpl(int useConnType, DbOperation dbOp) {
        this.useConnType = useConnType;
        this.dbOp = dbOp;
    }

    public BaseServiceImpl() {
        this.useConnType = CONN_IN_METHOD;
    }
    // 传入一个连接池名，作为默认连接池，并直接连接
	public BaseServiceImpl(String db) {
		dbOp = new DbOperation(db);
		this.useConnType = CONN_IN_SERVICE;
	}
	// 传入一个连接，作为默认连接
    public BaseServiceImpl(DbOperation dbOp) {
        this.useConnType = CONN_IN_SERVICE;
        this.dbOp = dbOp;
    }
    
    public DbOperation getDbOp() {
        if (dbOp != null) {
            return dbOp;
        }
        //在方法中建立、释放数据库连接
        if (useConnType == CONN_IN_METHOD) {
            return new DbOperation();
        }
        //服务中建立、释放数据库连接
        else if (useConnType == CONN_IN_SERVICE) {
            dbOp = new DbOperation();
            return dbOp;
        }
        return dbOp;
    }
    // 获得对应的连接池并立刻初始化
    public DbOperation getDbOp(String db) {
        if (dbOp != null) {
            return dbOp;
        }
        //在方法中建立、释放数据库连接
        if (useConnType == CONN_IN_METHOD) {
            return new DbOperation(db);
        }
        //服务中建立、释放数据库连接
        else if (useConnType == CONN_IN_SERVICE) {
            dbOp = new DbOperation(db);
            return dbOp;
        }
        return dbOp;
    }

    /**
     * 
     * 作者：
     * 
     * 创建日期：2006-8-19
     * 
     * 说明：释放数据库操作类中的资源。
     * 
     * 参数及返回值说明：
     * 
     * @param dbOp
     */
    public void release(DbOperation dbOp) {
        if (dbOp == null) {
            return;
        }
        //在方法中建立、释放数据库连接
        if (useConnType == CONN_IN_METHOD) {
            //全部释放
            dbOp.release();
        }
        //服务中建立、释放数据库连接
        else if (useConnType == CONN_IN_SERVICE) {
            //不释放数据库连接
            dbOp.releaseWithoutConn();
        }
    }

    /**
     * 作者：
     * 
     * 创建日期：2006-8-19
     * 
     * 说明：释放数据库操作类的所有资源。
     * 
     * 参数及返回值说明：
     * 
     *  
     */
    public void releaseAll() {
        if (dbOp == null) {
            return;
        }
        dbOp.release();
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addXXX(Object xxx, String tableName) {
        if (xxx == null || tableName == null) {
            return false;
        }

        //取得or mapping
        Mapping mapping = OrMap.getMapping(tableName);
        if (mapping == null) {
            return false;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return false;
        }

        //构建sql语句
        StringBuffer querySb = new StringBuffer("insert into " + tableName
                + "(");
        int fieldsCount = mapping.getFields().size();
        int i;
        String objField = null;
        String tableField = null;
        ArrayList fields = mapping.getFields();
        for (i = 0; i < fieldsCount; i++) {
            tableField = ((MapField) fields.get(i)).getTableField();
            if (i != 0) {
                querySb.append(", ");
            }
            querySb.append("`").append(tableField).append("`");
        }
        querySb.append(") values(");
        for (i = 0; i < fieldsCount; i++) {
            if (i != 0) {
                querySb.append(", ");
            }
            querySb.append("?");
        }
        querySb.append(")");

        String query = querySb.toString();
        //准备执行
        if (!dbOp.prepareStatement(query)) {
            release(dbOp);
            return false;
        }

        Class c = xxx.getClass();
        PreparedStatement pstmt = dbOp.getPStmt();
        try {
            Field field = null;
            MapField mapField = null;
            //设置参数
            for (i = 0; i < fieldsCount; i++) {
                mapField = (MapField) fields.get(i);
                objField = mapField.getObjField();
                field = c.getField(objField);
                if (field == null) {
                    release(dbOp);
                    return false;
                }
                //整数
                if ("int".equals(mapField.getObjType())) {
                    pstmt.setInt(i + 1, field.getInt(xxx));
                }
                else if ("float".equals(mapField.getObjType())) {
                    pstmt.setFloat(i + 1, field.getFloat(xxx));
                }
                //double
                else if ("double".equals(mapField.getObjType())) {
                    pstmt.setDouble(i + 1, field.getDouble(xxx));
                }
                //字符串
                else if ("String".equals(mapField.getObjType())) {
                    pstmt.setString(i + 1, (String) field.get(xxx));
                }
                //Timestamp
                else if ("Timestamp".equals(mapField.getObjType())) {
                    pstmt.setTimestamp(i + 1, (Timestamp) field.get(xxx));
                }
                //long
                else if ("long".equals(mapField.getObjType())) {
                    pstmt.setLong(i + 1, field.getLong(xxx));
                }
                //其他
                else {
                    pstmt.setObject(i + 1, field.get(xxx));
                }
            }
            //执行
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            stockLog.error(StringUtil.getExceptionInfo(e));
            release(dbOp);
            return false;
        } finally {
            //释放数据库连接
            release(dbOp);
        }

        return true;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteXXX(String condition, String tableName) {
        if (condition == null || tableName == null) {
            return false;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return false;
        }

        String query = "delete from " + tableName + " where " + condition;

        boolean result = dbOp.executeUpdate(query);

        //释放数据库连接
        release(dbOp);
        return result;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public Object getXXX(String condition, String tableName, String className) {
        Object result = null;

        if (tableName == null || className == null) {
            return result;
        }

        //取得or mapping
        Mapping mapping = OrMap.getMapping(tableName);
        if (mapping == null) {
            return result;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init() || !dbOp.setFetchSize(1)) {
            return result;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select * from " + tableName;
        if (condition != null) {
            query += " where " + condition;
        }

        //执行查询
        rs = dbOp.executeQuery(query);

        if (rs == null) {
            release(dbOp);
            return null;
        }

        try {
            //把结果集封装
            if (rs.next()) {
                Class c = Class.forName(className);
                result = c.newInstance();
                int fieldsCount = mapping.getFields().size();
                ArrayList fields = mapping.getFields();
                MapField mapField = null;
                Field field = null;
                int i;
                String objField;
                String tableField;
                for (i = 0; i < fieldsCount; i++) {
                    mapField = (MapField) fields.get(i);
                    objField = mapField.getObjField();
                    tableField = mapField.getTableField();
                    field = c.getField(objField);
                    if (field == null) {
                        release(dbOp);
                        return null;
                    }
                    //整数
                    if ("int".equals(mapField.getObjType())) {
                        field.setInt(result, rs.getInt(tableField));
                    }
                    else if ("float".equals(mapField.getObjType())) {
                        field.setFloat(result, rs.getFloat(tableField));
                    }
                    //double
                    else if ("double".equals(mapField.getObjType())) {
                        field.setDouble(result, rs.getDouble(tableField));
                    }
                    //字符串
                    else if ("String".equals(mapField.getObjType())) {
                        field.set(result, rs.getString(tableField));
                    }
                    //Timestamp
                    else if ("Timestamp".equals(mapField.getObjType())) {
                        field.set(result, rs.getTimestamp(tableField));
                    }
                    //long
                    else if ("long".equals(mapField.getObjType())) {
                        field.setLong(result, rs.getLong(tableField));
                    }
                    //其他
                    else {
                        field.set(result, rs.getObject(tableField));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            release(dbOp);
            return null;
        }

        //释放数据库连接
        release(dbOp);

        return result;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public Object[] getNPXXX(String listCondition, int currentId,
            String orderBy, int[] npType, String tableName, String className) {
        Object[] result = null;

        if (tableName == null || className == null || npType == null) {
            return result;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init() || !dbOp.setFetchSize(1)) {
            return result;
        }
//        ResultSet rs = null;

        //构建查询语句
        String sql = tableName;
        if (listCondition != null) {
            sql += " where " + listCondition;
        }
        if (orderBy != null) {
            sql += " order by " + orderBy;
        }

        createRownum(dbOp, sql);
        int rownum = getRownumById(dbOp, currentId);

        result = new Object[npType.length];
        int id = -1;
        Object o = null;
        for (int i = 0; i < npType.length; i++) {
            id = getIdByRownum(dbOp, rownum + npType[i]);
            if (id != 0) {
                o = getXXX("id = " + id, tableName, className);
            } else {
                o = null;
            }
            result[i] = o;
        }

        //释放数据库连接
        release(dbOp);

        return result;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getXXXCount(String condition, String tableName,
            String countFieldName) {
        if (tableName == null || countFieldName == null) {
            return 0;
        }

        int count = 0;

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return count;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select count(" + countFieldName + ") as c_id from "
                + tableName;
        if (condition != null) {
            query += " where " + condition;
        }
        
        //执行查询
        rs = dbOp.executeQuery(query);

        if (rs == null) {
            release(dbOp);
            return count;
        }

        try {
            //把结果集封装
            if (rs.next()) {
                count = rs.getInt("c_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //释放数据库连接
        release(dbOp);

        return count;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getXXXList(String condition, int index, int count,
            String orderBy, String tableName, String className) {
        ArrayList resultList = new ArrayList();
        Object result = null;

        if (tableName == null || className == null) {
            return resultList;
        }

        //取得or mapping
        Mapping mapping = OrMap.getMapping(tableName);
        if (mapping == null) {
            return resultList;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return resultList;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select * from " + tableName;
        if (condition != null) {
            query += " where " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }

        try {
            //把结果集封装
            Class c = Class.forName(className);
            int fieldsCount = mapping.getFields().size();
            ArrayList fields = mapping.getFields();
            MapField mapField = null;
            Field field = null;
            int i;
            String objField;
            String tableField;

            while (rs.next()) {
                result = c.newInstance();
                for (i = 0; i < fieldsCount; i++) {
                    mapField = (MapField) fields.get(i);
                    objField = mapField.getObjField();
                    tableField = mapField.getTableField();
                    field = c.getField(objField);
                    if (field == null) {
                        release(dbOp);
                        return resultList;
                    }
                    //整数
                    if ("int".equals(mapField.getObjType())) {
                        field.setInt(result, rs.getInt(tableField));
                    }
                    else if ("float".equals(mapField.getObjType())) {
                        field.setFloat(result, rs.getFloat(tableField));
                    }
                    //double
                    else if ("double".equals(mapField.getObjType())) {
                        field.setDouble(result, rs.getDouble(tableField));
                    }
                    //字符串
                    else if ("String".equals(mapField.getObjType())) {
                        field.set(result, rs.getString(tableField));
                    }
                    //Timestamp
                    else if ("Timestamp".equals(mapField.getObjType())) {
                        field.set(result, rs.getTimestamp(tableField));
                    }
                    //long
                    else if ("long".equals(mapField.getObjType())) {
                        field.setLong(result, rs.getLong(tableField));
                    }
                    //其他
                    else {
                        field.set(result, rs.getObject(tableField));
                    }
                }
                resultList.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            release(dbOp);
            return resultList;
        }finally
        {//释放数据库连接
        	release(dbOp);
        }
        
        

        return resultList;
    }
    
    /**
     * 多个表连接只显示一个表的数据
     * @param tablename 要显示的表名
     * @param condition --join on where 
     * @param index
     * @param count
     * @param orderBy
     * @param tableName
     * @param className
     * @return
     */
    public ArrayList getJoinXXXList(String table,String joinwhere, int index, int count,
            String orderBy, String tableName, String className) {
        ArrayList resultList = new ArrayList();
        Object result = null;

        if (tableName == null || className == null) {
            return resultList;
        }

        //取得or mapping
        Mapping mapping = OrMap.getMapping(tableName);
        if (mapping == null) {
            return resultList;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return resultList;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select "+table+".* from " + tableName;
        if (joinwhere != null) {
            query += " " + joinwhere;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }

        try {
            //把结果集封装
            Class c = Class.forName(className);
            int fieldsCount = mapping.getFields().size();
            ArrayList fields = mapping.getFields();
            MapField mapField = null;
            Field field = null;
            int i;
            String objField;
            String tableField;

            while (rs.next()) {
                result = c.newInstance();
                for (i = 0; i < fieldsCount; i++) {
                    mapField = (MapField) fields.get(i);
                    objField = mapField.getObjField();
                    tableField = mapField.getTableField();
                    field = c.getField(objField);
                    if (field == null) {
                        release(dbOp);
                        return resultList;
                    }
                    //整数
                    if ("int".equals(mapField.getObjType())) {
                        field.setInt(result, rs.getInt(tableField));
                    }
                    else if ("float".equals(mapField.getObjType())) {
                        field.setFloat(result, rs.getFloat(tableField));
                    }
                    //double
                    else if ("double".equals(mapField.getObjType())) {
                        field.setDouble(result, rs.getDouble(tableField));
                    }
                    //字符串
                    else if ("String".equals(mapField.getObjType())) {
                        field.set(result, rs.getString(tableField));
                    }
                    //Timestamp
                    else if ("Timestamp".equals(mapField.getObjType())) {
                        field.set(result, rs.getTimestamp(tableField));
                    }
                    //long
                    else if ("long".equals(mapField.getObjType())) {
                        field.setLong(result, rs.getLong(tableField));
                    }
                    //其他
                    else {
                        field.set(result, rs.getObject(tableField));
                    }
                }
                resultList.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            release(dbOp);
            return resultList;
        }finally
        {//释放数据库连接
        	release(dbOp);
        }
        
        

        return resultList;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateXXX(String set, String condition, String tableName) {
        if (set == null || condition == null || tableName == null) {
            return false;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return false;
        }

        String query = "update " + tableName + " set " + set + " where "
                + condition;

        boolean result = false;
        try{
        	result = dbOp.executeUpdate(query);
        } catch (Exception e) {
        	e.printStackTrace();
            stockLog.error(StringUtil.getExceptionInfo(e));
		} finally {
	        //释放数据库连接
	        release(dbOp);
		}

        return result;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getFieldList(String fieldName, String tableName,
            String condition, int index, int count, String groupBy, String orderBy, String objType) {
        ArrayList resultList = new ArrayList();

        if (fieldName == null || tableName == null) {
            return resultList;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return resultList;
        }
        ResultSet rs = null;

        String query = "select " + fieldName + " from " + tableName;
        if (condition != null) {
            query += " where " + condition;
        }
        if (groupBy != null) {
			query += " group by " + groupBy;
		}
        if (orderBy != null) {
            query += " order by " + orderBy;
        }

        query = DbOperation.getPagingQuery(query, index, count);

        rs = dbOp.executeQuery(query);
        if (rs == null) {
            release(dbOp);
            return resultList;
        }

        try {
            while (rs.next()) {
            	if("int".equals(objType)){
            		resultList.add(new Integer(rs.getInt(fieldName)));
            	}
            	else if("String".equals(objType)){
            		resultList.add(rs.getString(fieldName));
            	}
            	else if("float".equals(objType)){
            		resultList.add(new Float(rs.getFloat(fieldName)));
            	}
            	else if("double".equals(objType)){
            		resultList.add(new Double(rs.getDouble(fieldName)));
            	}
            	else if("long".equals(objType)){
            		resultList.add(new Long(rs.getLong(fieldName)));
            	}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            release(dbOp);
            return resultList;
        }

        release(dbOp);
        return resultList;
    }
    
    /*
     * 请查看父类或接口对应的注释。
     */
    public Object getFieldValue(String fieldName, String tableName,
            String condition, String groupBy, String orderBy, String objType) {
        Object obj = null;

        if (fieldName == null || tableName == null) {
            return obj;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return obj;
        }
        ResultSet rs = null;

        String query = "select " + fieldName + " from " + tableName;
        if (condition != null) {
            query += " where " + condition;
        }
        if (groupBy != null) {
			query += " group by " + groupBy;
		}
        if (orderBy != null) {
            query += " order by " + orderBy;
        }

        rs = dbOp.executeQuery(query);
        if (rs == null) {
            release(dbOp);
            return obj;
        }

        try {
            while (rs.next()) {
            	if("int".equals(objType)){
            		obj = new Integer(rs.getInt(fieldName));
            	}
            	else if("String".equals(objType)){
            		obj = rs.getString(fieldName);
            	}
            	else if("float".equals(objType)){
            		obj = new Float(rs.getFloat(fieldName));
            	}
            	else if("double".equals(objType)){
            		obj = new Double(rs.getDouble(fieldName));
            	}
            	else if("long".equals(objType)){
            		obj = new Long(rs.getLong(fieldName));
            	}
            }
        } catch (SQLException e) {
            e.printStackTrace();
            release(dbOp);
            return obj;
        }

        release(dbOp);
        return obj;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getNumber(String fieldName, String tableName, String function,
            String condition) {
        if (fieldName == null || tableName == null) {
            return -1;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return -1;
        }
        ResultSet rs = null;

        String query = function == null ? fieldName + " as n from " + tableName : (function + "("
                + fieldName + ")")
                + " as n from " + tableName;
        query = "select " + query;
        if (condition != null) {
            query += " where " + condition;
        }

        rs = dbOp.executeQuery(query);
        if (rs == null) {
            release(dbOp);
            return -1;
        }

        int number = -1;

        try {
            if (rs.next()) {
                number = rs.getInt("n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            release(dbOp);
            return -1;
        }

        release(dbOp);
        return number;
    }

    final public static String ROWNUM_TABLE = "tmp_rownum";

    public void createRownum(DbOperation dbOp, String sql) {

        removeRownum(dbOp);

        dbOp
                .executeUpdate("create temporary table "
                        + ROWNUM_TABLE
                        + " (rownum int AUTO_INCREMENT, id int, index using hash(rownum), index using hash(id))engine=memory select id from "
                        + sql);

    }

    public void removeRownum(DbOperation dbOp) {

        dbOp.executeUpdate("drop temporary table if exists " + ROWNUM_TABLE);

    }

    public int getRownumById(DbOperation dbOp, int id) {
        ResultSet rs = dbOp.executeQuery("select rownum from " + ROWNUM_TABLE
                + " where id=" + id);

        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getIdByRownum(DbOperation dbOp, int rownum) {
        ResultSet rs = dbOp.executeQuery("select id from " + ROWNUM_TABLE
                + " where rownum=" + rownum);

        try {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    
    public boolean executeUpdate(String sql) {
		if (sql == null) {
			return false;
		}
		dbOp = new DbOperation();
		if (!dbOp.init()) {
			return false;
		}
		try {
			return dbOp.executeUpdate(sql);

		} finally {
			release(dbOp);
		}		
    }
    
    /**
     * 
     * 功能:级联查询 统计数据使用
     * @param query
     * @return
     */
    public int getCascadedQueryCount(String query){
    	int row = 0;
    	DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return row;
        }
    	ResultSet rs = dbOp.executeQuery(query);
    	try {
			if(rs.next()){
				row=rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			 release(dbOp);
		}
    	return row;
    }
    
    /**
     * 
     * 功能:当需要级联表 作为条的时候。 自己构建好sql  例如 select o.* from order_message55 o left join user_order u on o.order_id = u.id 查询
     * @param condition
     * @param index
     * @param count
     * @param orderBy
     * @param query  已经构建好的sql
     * @param tableName
     * @param className
     * @return
     */
    public ArrayList getCascadedXXXList(String condition, int index, int count,
            String orderBy, String query ,String tableName, String className) {
        ArrayList resultList = new ArrayList();
        Object result = null;

        if (tableName == null || className == null) {
            return resultList;
        }

        //取得or mapping
        Mapping mapping = OrMap.getMapping(tableName);
        if (mapping == null) {
            return resultList;
        }

        //数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return resultList;
        }
        ResultSet rs = null;

        //构建查询语句
        if (condition != null) {
            query += " where " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }
        query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);
        
        if (rs == null) {
            release(dbOp);
            return null;
        }

        try {
            //把结果集封装
            Class c = Class.forName(className);
            int fieldsCount = mapping.getFields().size();
            ArrayList fields = mapping.getFields();
            MapField mapField = null;
            Field field = null;
            int i;
            String objField;
            String tableField;

            while (rs.next()) {
                result = c.newInstance();
                for (i = 0; i < fieldsCount; i++) {
                    mapField = (MapField) fields.get(i);
                    objField = mapField.getObjField();
                    tableField = mapField.getTableField();
                    field = c.getField(objField);
                    if (field == null) {
                        release(dbOp);
                        return resultList;
                    }
                    //整数
                    if ("int".equals(mapField.getObjType())) {
                        field.setInt(result, rs.getInt(tableField));
                    }
                    else if ("float".equals(mapField.getObjType())) {
                        field.setFloat(result, rs.getFloat(tableField));
                    }
                    //double
                    else if ("double".equals(mapField.getObjType())) {
                        field.setDouble(result, rs.getDouble(tableField));
                    }
                    //字符串
                    else if ("String".equals(mapField.getObjType())) {
                        field.set(result, rs.getString(tableField));
                    }
                    //Timestamp
                    else if ("Timestamp".equals(mapField.getObjType())) {
                        field.set(result, rs.getTimestamp(tableField));
                    }
                    //long
                    else if ("long".equals(mapField.getObjType())) {
                        field.setLong(result, rs.getLong(tableField));
                    }
                    //其他
                    else {
                        field.set(result, rs.getObject(tableField));
                    }
                }
                resultList.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            release(dbOp);
            return resultList;
        }finally
        {//释放数据库连接
        	release(dbOp);
        }
        
        

        return resultList;
    }
} 
