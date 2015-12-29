/*
 * Created on 2007-2-8
 *
 */
package adultadmin.service.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import adultadmin.action.vo.UserInfoBean;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserPermissionBean;
import adultadmin.service.infc.IUserService;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-2-8
 * 
 * 说明：
 */
public class UserServiceImpl extends BaseServiceImpl implements IUserService {
    public UserServiceImpl(int useConnType, DbOperation dbOp) {
        this.useConnType = useConnType;
        this.dbOp = dbOp;
    }

//    public UserServiceImpl() {
//        this.useConnType = CONN_IN_SERVICE;
//    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addUser(voUser user) {
        return addXXX(user, "user");
    }
    
    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addAdminUser(voUser user) {
    	return addXXX(user, "admin_user");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteUser(String condition) {
        return deleteXXX(condition, "user");
    }
    
    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteAdminUser(String condition) {
    	return deleteXXX(condition, "admin_user");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public voUser getUser(String condition) {
        return (voUser) getXXX(condition, "user", "adultadmin.action.vo.voUser");
    }
    
    /*
     * 请查看父类或接口对应的注释。
     */
    public voUser getAdminUser(String condition) {
    	return (voUser) getXXX(condition, "admin_user", "adultadmin.action.vo.voUser");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getUserCount(String condition) {
        return getXXXCount(condition, "user", "id");
    }
    
    /*
     * 请查看父类或接口对应的注释。
     */
    public int getAdminUserCount(String condition) {
    	return getXXXCount(condition, "admin_user", "id");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getUserList(String condition, int index, int count,
            String orderBy) {
        return getXXXList(condition, index, count, orderBy, "user",
                "adultadmin.action.vo.voUser");
    }
    
    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getAdminUserList(String condition, int index, int count,
    		String orderBy) {
    	return getXXXList(condition, index, count, orderBy, "admin_user",
    	"adultadmin.action.vo.voUser");
    }

    public ArrayList queryUserListByPermission(String condition, int index, int count, String orderBy) {
    	ArrayList resultList = new ArrayList();
    	//数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return resultList;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = "select * from user ";
        if (condition != null) {
            query += " where " + condition;
        }
        if (orderBy != null) {
            query += " order by " + orderBy;
        }

        if (index < 0) {
			index = 0;
		}
		if (count == -1) {
			query += " limit " + index + ", 200";
		} else {
			query += " limit " + index + ", " + count;
		}
        //query = DbOperation.getPagingQuery(query, index, count);

        //执行查询
        rs = dbOp.executeQuery(query);

        if (rs == null) {
            release(dbOp);
            return null;
        }
        try {
    		while(rs.next()){
    			voUser vo = new voUser();
    			vo.setId(rs.getInt("u.id"));
    			vo.setUsername(rs.getString("username"));
    			vo.setPassword(rs.getString("password"));
    			vo.setCreateDatetime(rs.getString("create_datetime"));
    			vo.setName(rs.getString("name"));
    			vo.setNick(rs.getString("nick"));
    			vo.setSecurityLevel(rs.getInt("security_level"));
    			vo.setPermission(rs.getInt("permission"));
    			vo.setUsername2(rs.getString("username2"));
    			vo.setIsDisable(rs.getBoolean("u.is_disable"));
    			resultList.add(vo);
    		}
    	} catch (Exception e) {
            e.printStackTrace();
            release(dbOp);
            return resultList;
        } finally {
	        //释放数据库连接
	        release(dbOp);
        }

        return resultList;
    }
    
    public ArrayList queryAdminUserListByPermission(String condition, int index, int count, String orderBy) {
    	ArrayList resultList = new ArrayList();
    	//数据库操作类
    	DbOperation dbOp = getDbOp();
    	if (!dbOp.init()) {
    		return resultList;
    	}
    	ResultSet rs = null;
    	
    	//构建查询语句
    	String query = "select * from admin_user u left outer join user_permission up on u.id=up.user_id ";
    	if (condition != null) {
    		query += " where " + condition;
    	}
    	if (orderBy != null) {
    		query += " order by " + orderBy;
    	}
    	
    	if (index < 0) {
    		index = 0;
    	}
    	if (count == -1) {
    		//query += " limit " + index + ", 200";
    	} else {
    		query += " limit " + index + ", " + count;
    	}
    	//query = DbOperation.getPagingQuery(query, index, count);
    	
    	//执行查询
    	rs = dbOp.executeQuery(query);
    	
    	if (rs == null) {
    		release(dbOp);
    		return null;
    	}
    	try {
    		while(rs.next()){
    			voUser vo = new voUser();
    			vo.setId(rs.getInt("u.id"));
    			vo.setUsername(rs.getString("username"));
    			vo.setPassword(rs.getString("password"));
    			vo.setCreateDatetime(rs.getString("create_datetime"));
    			vo.setName(rs.getString("name"));
    			vo.setNick(rs.getString("nick"));
    			vo.setSecurityLevel(rs.getInt("security_level"));
    			vo.setPermission(rs.getInt("permission"));
    			vo.setUsername2(rs.getString("username2"));
    			resultList.add(vo);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    		release(dbOp);
    		return resultList;
    	} finally {
    		//释放数据库连接
    		release(dbOp);
    	}
    	
    	return resultList;
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateUser(String set, String condition) {
        return updateXXX(set, condition, "user");
    }
    
    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateAdminUser(String set, String condition) {
    	return updateXXX(set, condition, "admin_user");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addUserPermission(UserPermissionBean userPermission) {
        return addXXX(userPermission, "user_permission");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteUserPermission(String condition) {
        return deleteXXX(condition, "user_permission");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public UserPermissionBean getUserPermission(String condition) {
        return (UserPermissionBean) getXXX(condition, "user_permission", "adultadmin.bean.UserPermissionBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getUserPermissionCount(String condition) {
        return getXXXCount(condition, "user_permission", "id");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getUserPermissionList(String condition, int index, int count,
            String orderBy) {
        return getXXXList(condition, index, count, orderBy, "user_permission",
                "adultadmin.bean.UserPermissionBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateUserPermission(String set, String condition) {
        return updateXXX(set, condition, "user_permission");
    }
    
    
    /*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addUserInfo(UserInfoBean bean) {
		return addXXX(bean, "user_info");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteUserInfo(String condition) {
		return deleteXXX(condition, "user_info");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public UserInfoBean getUserInfo(String condition) {
		return (UserInfoBean) getXXX(condition, "user_info",
				"adultadmin.action.vo.UserInfoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getUserInfoCount(String condition) {
		return getXXXCount(condition, "user_info", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getUserInfoList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "user_info",
				"adultadmin.action.vo.UserInfoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateUserInfo(String set, String condition) {
		return updateXXX(set, condition, "user_info");
	}
	

	public String getLastLoginTime(String phone) {
		if(phone == null){
			return null;
		}
		StringBuffer buf = new StringBuffer(64);
		String sql = null;
		buf.append("select last_login_time from user_status where phone='");
		buf.append(phone);
		buf.append("'");
		sql = buf.toString();
		//数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init() || !dbOp.setFetchSize(1)) {
            return null;
        }
        ResultSet rs = dbOp.executeQuery(sql);
        try{
	        if(rs != null){
	        	if(rs.next()){
	        		return rs.getString(1);
	        	}
	        }
        }catch(Exception e){
        	e.printStackTrace();
        } finally {
	        //释放数据库连接
	        release(dbOp);
        }
		return null;
	}    


	
	
	public voUser getVoUserAndGender(String condition){
		StringBuffer buf = new StringBuffer(64);
		String sql = null;
		buf.append("select u.*,ui.gender gender from user u left join user_info ui on u.id=ui.id where ");
		buf.append(condition);
		voUser vo = new voUser();
		sql = buf.toString();
		//数据库操作类
        DbOperation dbOp = getDbOp();
        if (!dbOp.init() || !dbOp.setFetchSize(1)) {
            return null;
        }
        ResultSet rs = dbOp.executeQuery(sql);
        try{
        
        	if(rs.next()){
        		vo.setId(rs.getInt("u.id"));
                vo.setUsername(rs.getString("username"));
                vo.setPassword(rs.getString("password"));
                vo.setCreateDatetime(rs.getString("create_datetime"));
                vo.setFlag(rs.getInt("flag"));
                vo.setName(rs.getString("name"));
                vo.setPhone(rs.getString("phone"));
                vo.setAddress(rs.getString("address"));
                vo.setPostcode(rs.getString("postcode"));
                vo.setCp(rs.getString("cp"));
                vo.setUa(rs.getString("ua"));
                vo.setNick(rs.getString("nick"));
                vo.setVip(rs.getInt("vip"));
                vo.setVipPhone(rs.getString("vip_phone"));
                vo.setAgent(rs.getInt("agent"));
                vo.setDiscount(rs.getFloat("discount"));
                vo.setOrderReimburse(rs.getFloat("order_reimburse"));
                vo.setReimburse(rs.getFloat("reimburse"));
                 
                vo.setGender(rs.getInt("gender"));
        	}
	        rs.close();
        }catch(Exception e){
        	e.printStackTrace();
        } finally {
	        //释放数据库连接
	        release(dbOp);
        }
        return vo;
	}
	
	public ArrayList getVoUserAndGenderList(String condition){
		StringBuffer buf = new StringBuffer(64);
		String sql = null;
		buf.append("select u.*,ui.gender gender from user u left join user_info ui on u.id=ui.id where ");
		buf.append(condition);
		sql = buf.toString();
		//数据库操作类
        DbOperation dbOp = getDbOp();
        ArrayList list = new ArrayList();
        ResultSet rs = dbOp.executeQuery(sql);
        try{
        
        	while(rs.next()){
        		voUser vo = new voUser();
        		vo.setId(rs.getInt("u.id"));
                vo.setUsername(rs.getString("username"));
                vo.setPassword(rs.getString("password"));
                vo.setCreateDatetime(rs.getString("create_datetime"));
                vo.setFlag(rs.getInt("flag"));
                vo.setName(rs.getString("name"));
                vo.setPhone(rs.getString("phone"));
                vo.setAddress(rs.getString("address"));
                vo.setPostcode(rs.getString("postcode"));
                vo.setCp(rs.getString("cp"));
                vo.setUa(rs.getString("ua"));
                vo.setNick(rs.getString("nick"));
                vo.setVip(rs.getInt("vip"));
                vo.setVipPhone(rs.getString("vip_phone"));
                vo.setAgent(rs.getInt("agent"));
                vo.setDiscount(rs.getFloat("discount"));
                vo.setOrderReimburse(rs.getFloat("order_reimburse"));
                vo.setReimburse(rs.getFloat("reimburse"));
                 
                vo.setGender(rs.getInt("gender"));
                list.add(vo);
        	}
	        rs.close();
        }catch(Exception e){
        	e.printStackTrace();
        } finally {
	        //释放数据库连接
	        release(dbOp);
        }
        return list;
	}
	
}
