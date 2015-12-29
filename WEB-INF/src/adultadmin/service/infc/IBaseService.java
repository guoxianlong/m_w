/*
 * Created on 2006-8-19
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;

import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2006-8-19
 * 
 * 说明：基础接口。
 */
public interface IBaseService {
    /**
     * 作者：
     * 
     * 创建日期：2006-8-19
     * 
     * 说明：每个Service操作数据库的方法之一，在每个方法中建立、释放数据库连接。
     */
    public static final int CONN_IN_METHOD = 1;

    /**
     * 作者：
     * 
     * 创建日期：2006-8-19
     * 
     * 说明：每个Service操作数据库的方法之一，在每个服务中建立、释放数据库连接。
     */
    public static final int CONN_IN_SERVICE = 2;

    /**
     * 作者：
     * 
     * 创建日期：2006-8-19
     * 
     * 说明：每个Service操作数据库的方法之一，从外部传入一个数据库连接，在Service内不释放。
     */
    public static final int CONN_FROM_OUTSIDE = 3;

    /**
     * 作者：
     * 
     * 创建日期：2006-8-29
     * 
     * 说明：往数据库增加一个对象。
     * 
     * 参数及返回值说明：
     * 
     * @param xxx：对象。
     * @param tableName：对象字段与数据库字段的对应表。
     * @return
     */
    public boolean addXXX(Object xxx, String tableName);

    /**
     * 作者：
     * 
     * 创建日期：2006-8-29
     * 
     * 说明：更新表。
     * 
     * 参数及返回值说明：
     * 
     * @param set
     * @param condition
     * @param tableName：表名。
     * @return
     */
    public boolean updateXXX(String set, String condition, String tableName);

    /**
     * 作者：
     * 
     * 创建日期：2006-8-29
     * 
     * 说明：删除。
     * 
     * 参数及返回值说明：
     * 
     * @param condition
     * @param tableName：表名。
     * @return
     */
    public boolean deleteXXX(String condition, String tableName);

    /**
     * 作者：
     * 
     * 创建日期：2006-8-29
     * 
     * 说明：取得数目。
     * 
     * 参数及返回值说明：
     * 
     * @param condition
     * @param tableName：表名。
     * @param countFieldName：取数目的字段名。
     * @return
     */
    public int getXXXCount(String condition, String tableName,
            String countFieldName);

    /**
     * 作者：
     * 
     * 创建日期：2006-8-29
     * 
     * 说明：取得一个XXX。
     * 
     * 参数及返回值说明：
     * 
     * @param condition
     * @param tableName：表名。
     * @param className：类名，注意，必须是全名（包括包名）。
     * @return
     */
    public Object getXXX(String condition, String tableName, String className);

    /**
     * 作者：李北金
     * 
     * 创建日期：2006-10-20
     * 
     * 说明：取得上一条或下一条。
     * 
     * 参数及返回值说明：
     * 
     * @param listCondition
     * @param currentCondition
     * @param orderBy
     * @param npType
     * @return
     */
    public Object[] getNPXXX(String listCondition, int currentId,
            String orderBy, int[] npType, String tableName, String className);

    /**
     * 作者：
     * 
     * 创建日期：2006-8-29
     * 
     * 说明：
     * 
     * 参数及返回值说明：
     * 
     * @param condition
     * @param index
     * @param count
     * @param orderBy
     * @param tableName
     * @param className：类名，注意，必须是全名（包括包名）。
     * @return
     */
    public ArrayList getXXXList(String condition, int index, int count,
            String orderBy, String tableName, String className);

    /**
     * 作者：李北金
     * 修改：赵林
     * 
     * 创建日期：2006-10-14
     * 
     * 说明：
     * 
     * 参数及返回值说明：
     * 
     * @param fieldName
     * @param tableName
     * @param condition
     * @param index
     * @param count
     * @param groupBy
     * @param orderBy
     * @param objType
     * @return
     */
    public ArrayList getFieldList(String fieldName, String tableName,
            String condition, int index, int count, String groupBy, String orderBy, String objType);
    
    
    /**
     * 作者：赵林
     * 
     * 创建日期：2010-12-03
     * 
     * 说明：
     * 
     * 参数及返回值说明：
     * 
     * @param fieldName
     * @param tableName
     * @param condition
     * @param index
     * @param count
     * @param groupBy
     * @param orderBy
     * @param objType
     * @return
     */
    public Object getFieldValue(String fieldName, String tableName,
            String condition, String groupBy, String orderBy, String objType);

    
    /**
     * 作者：李北金
     * 
     * 创建日期：2006-11-11
     * 
     * 说明：取得一个数字
     * 
     * 参数及返回值说明：
     * 
     * @param fieldName
     * @param tableName
     * @param function
     * @param condition
     * @return
     */
    public int getNumber(String fieldName, String tableName, String function,
            String condition);

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
    public void releaseAll();

    public DbOperation getDbOp();
    
    /**
     * 
     * 功能:级联查询 统计数据数量
     * @param query :select count(a.id) from  x a join b ....
     * @return
     */
    public int getCascadedQueryCount(String query);
    
    /**
     * 
     * 功能:当需要级联表 作为条的时候。 自己构建好sql  例如
     * @param condition
     * @param index
     * @param count
     * @param orderBy
     * @param query  已经构建好的sql select o.* from order_message55 o left join user_order u on o.order_id = u.id 
     * @param tableName
     * @param className
     * @return
     */
    public ArrayList getCascadedXXXList(String condition, int index, int count,
            String orderBy, String query ,String tableName, String className);
}
