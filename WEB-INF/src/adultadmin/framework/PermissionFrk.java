/*
 * Created on 2008-2-1
 *
 */
package adultadmin.framework;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.util.BinaryFlag;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PermissionBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.UserPermissionBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-2-1
 * 
 * 说明：用户后台操作权限
 */
public class PermissionFrk {
    public static int ORDER_ADMIN = 1; //权限：订单管理

    public static int USER_ADMIN = 2; //权限：用户管理

    /**
     * 作者：李北金
     * 
     * 创建日期：2008-2-1
     * 
     * 说明：
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param function
     *            功能ID
     * @return
     */
    public static boolean hasPermission(HttpServletRequest request, int function) {
        voUser user = (voUser) request.getSession().getAttribute("userView");
        if (user == null) {
            return false;
        }

        boolean isSystem = (user.getSecurityLevel() == 10); //系统管理员
        boolean isGaojiAdmin = (user.getSecurityLevel() == 9); //高级管理员
        boolean isAdmin = (user.getSecurityLevel() == 5); //普通管理员

        boolean isPingtaiyunwei = (user.getPermission() == 8); //平台运维部
        boolean isXiaoshou = (user.getPermission() == 7); //销售部
        boolean isShangpin = (user.getPermission() == 6); //商品部
//        boolean isTuiguang = (user.getPermission() == 5); //推广部
        boolean isYunyingzhongxin = (user.getPermission() == 4); //运营中心
        boolean isKefu = (user.getPermission() == 3); //客服部
        boolean isKucunwuliu = (user.getPermission() == 2); //库存物流部

        if (isSystem) { //超级管理员
            return true;
        }
        if (!isSystem && !isGaojiAdmin && !isAdmin) {
            return false;
        }

        //订单管理权限
        if (function == ORDER_ADMIN) {
            if ((isXiaoshou || isKefu || isShangpin || isKucunwuliu) //销售部、客服部、商品部、库存物流部
                    || (isYunyingzhongxin) //运营中心的管理员
                    || (isPingtaiyunwei && isGaojiAdmin) //平台运维部的高级管理员
            ) {
                return true;
            } else {
                return false;
            }
        }

        //用户管理权限
        if (function == USER_ADMIN) {
            if ((isXiaoshou || isKefu) //销售部、客服部
                    || (isYunyingzhongxin && isGaojiAdmin) //运营中心的高级管理员
                    || (isPingtaiyunwei && isGaojiAdmin) //平台运维部的高级管理员
            ) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
    
    
    public static HashMap groupMap = null;
    public static UserGroupBean nullGroup = new UserGroupBean();
    static {
    	nullGroup.setName("无");
    	nullGroup.setBak("无");
    }
    public static void clearGroup() {
    	groupMap = null;
    }
    static int[] lock = new int[0];
    public static HashMap getGroupMap() {
    	if(groupMap == null) {
    		synchronized(lock) {
    			if(groupMap == null) {
    				groupMap = getGroupMapDB();
    			}
    		}
    	}
    	return groupMap;
    }
    public static UserGroupBean getUserGroup(int groupId) {
    	if(groupId <= 0)
    		return nullGroup;
    	HashMap map = getGroupMap();
    	if(map == null)
    		return nullGroup;
    	
		UserGroupBean g = (UserGroupBean)map.get(new Integer(groupId));
		if(g == null)
			return nullGroup;
		
		return g;
    }
    
    public static HashMap getGroupMapDB() {
    	HashMap map = new LinkedHashMap();
    	
        DbOperation dbOp = new DbOperation();
        dbOp.init();
        ResultSet rs = null;

        rs = dbOp.executeQuery("select * from user_group order by seq");

        try {
            while (rs.next()) {
            	UserGroupBean g = new UserGroupBean();
            	g.setId(rs.getInt("id"));
            	g.setFlag(rs.getLong("flag"));
            	g.setFlag2(rs.getLong("flag2"));
            	g.setFlag3(rs.getLong("flag3"));
            	g.setFlag4(rs.getLong("flag4"));
            	g.setFlag5(rs.getLong("flag5"));
            	g.setFlag6(rs.getLong("flag6"));
            	g.setFlag7(rs.getLong("flag7"));
            	g.setFlag8(rs.getLong("flag8"));
            	g.setFlag9(rs.getLong("flag9"));
            	g.setFlag10(rs.getLong("flag10"));
            	g.setFlags(new BinaryFlag(rs.getBytes("flags")));
            	g.setBak(rs.getString("bak"));
            	g.setName(rs.getString("name"));
            	g.setCatalog(rs.getInt("catalog"));
            	g.setSeq(rs.getInt("seq"));
            	g.setDept(rs.getInt("dept"));
            	map.put(new Integer(g.getId()), g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dbOp.release();
        
    	groupMap = map;
    	return map;
    }
    public static boolean updateUserGroup(UserGroupBean g, boolean add) {
    	
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		String query = DbOperation.modifySql(add, 
				"user_group set name=?,bak=?,flag=?,flag2=?,flag3=?,flag4=?,flag5=?,flag6=?,flag7=?,flag8=?,flag9=?,flag10=?,flags=?", g.getId());
		
		if (!dbOp.prepareStatement(query)) {
			dbOp.release();
			return false;
		}
		PreparedStatement pstmt = dbOp.getPStmt();
		try {
			pstmt.setString(1, g.getName());
			pstmt.setString(2, g.getBak());
			pstmt.setLong(3, g.getFlag());
			pstmt.setLong(4, g.getFlag2());
			pstmt.setLong(5, g.getFlag3());
			pstmt.setLong(6, g.getFlag4());
			pstmt.setLong(7, g.getFlag5());
			pstmt.setLong(8, g.getFlag6());
			pstmt.setLong(9, g.getFlag7());
			pstmt.setLong(10, g.getFlag8());
			pstmt.setLong(11, g.getFlag9());
			pstmt.setLong(12, g.getFlag10());
			pstmt.setBytes(13, g.getFlags().getBytes());
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			dbOp.release();
			return false;
		}
		
		if(add && g.getId() == 0)
			g.setId(dbOp.getLastInsertId());
		dbOp.release();
		return true;
    }
    public static boolean updatePermission(PermissionBean g, boolean add) {
    	
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		String query = DbOperation.modifySql(add, 
				"permission set name=?,bak=?,parent=?", g.getId());
		
		if (!dbOp.prepareStatement(query)) {
			dbOp.release();
			return false;
		}
		PreparedStatement pstmt = dbOp.getPStmt();
		try {
			pstmt.setString(1, g.getName());
			pstmt.setString(2, g.getBak());
			pstmt.setLong(3, g.getParent());
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			dbOp.release();
			return false;
		}
		
		if(add && g.getId() == 0)
			g.setId(dbOp.getLastInsertId());
		dbOp.release();
		return true;
    }
    
    public static PermissionBean getPermission(int id) {
    	return getPermissionDB(id);
    }
    public static List getPermissionList(String cond) {
    	return getPermissionListDB(cond);
    }
    
    public static void addUserGroup(UserGroupBean g) {
    	updateUserGroup(g, true);
    	HashMap map = getGroupMap();
    	synchronized(lock) {
    		map.put(new Integer(g.getId()), g);
    	}
    }
    
    public static List getPermissionListDB(String cond) {
    	List list = new ArrayList();
    	
        DbOperation dbOp = new DbOperation();
        dbOp.init();
        ResultSet rs = null;

        rs = dbOp.executeQuery("select * from permission where " + cond);

        try {
            while (rs.next()) {
            	list.add(getPermission(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dbOp.release();
        
    	return list;
    }
    public static PermissionBean getPermissionDB(int id) {
        DbOperation dbOp = new DbOperation();
        dbOp.init();
        ResultSet rs = null;
        PermissionBean g = null;
        rs = dbOp.executeQuery("select * from permission where id=" + id);

        try {
            if (rs.next()) {
            	g = getPermission(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dbOp.release();
        return g;
    }
    public static PermissionBean getPermission(ResultSet rs) throws SQLException {
    	PermissionBean g = new PermissionBean();
    	g.setId(rs.getInt("id"));
    	g.setParent(rs.getInt("parent"));
    	g.setSeq(rs.getInt("seq"));
    	g.setBak(rs.getString("bak"));
    	g.setName(rs.getString("name"));
    	return g;
    }
    
    public static List getUserPermissionList(String cond) {
        DbOperation dbOp = new DbOperation();
        dbOp.init();
        ResultSet rs = null;
        List list = new ArrayList();
        rs = dbOp.executeQuery("select * from user_permission where " + cond);

        try {
            while (rs.next()) {
            	list.add(getUserPermission(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        dbOp.release();
        return list;
    }
    public static UserPermissionBean getUserPermission(ResultSet rs) throws SQLException {
    	UserPermissionBean g = new UserPermissionBean();
    	g.setId(rs.getInt("id"));
    	g.setUserId(rs.getInt("user_id"));
    	g.setGroupId(rs.getInt("group_id"));
    	g.setGroupId2(rs.getInt("group_id2"));
    	g.setGroupId3(rs.getInt("group_id3"));
    	g.setSecurityLevel(rs.getInt("security_level"));
    	g.setPermission(rs.getInt("permission"));
    	g.setGroups(string2ints(rs.getString("groups")));
    	g.setFlag(new BinaryFlag(rs.getBytes("flags")));
    	return g;
    }
    
    public static boolean updateUserPermission(UserPermissionBean g, boolean add) {
    	
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		String query = DbOperation.modifySql(add, 
				"user_permission set user_id=?,`permission`=?,security_level=?,groups=?,flags=?", g.getId());
		
		if (!dbOp.prepareStatement(query)) {
			dbOp.release();
			return false;
		}
		PreparedStatement pstmt = dbOp.getPStmt();
		try {
			pstmt.setInt(1, g.getUserId());
			pstmt.setInt(2, g.getPermission());
			pstmt.setInt(3, g.getSecurityLevel());
			pstmt.setString(4, array2String(g.getGroups(), ","));
			pstmt.setBytes(5, g.getFlag().getBytes());
			pstmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			dbOp.release();
			return false;
		}
		
		if(add && g.getId() == 0)
			g.setId(dbOp.getLastInsertId());
		dbOp.release();
		return true;
    }
    
    public static String array2String(int[] strs, String split) {
		if (strs == null || strs.length == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder(strs.length * 10);
		for (int i = 0; i < strs.length; i++) {
			builder.append(split);
			builder.append(strs[i]);
		}
		builder.append(split);
		return builder.toString();
	}
    
    public static voUser getUser(String cond) {
		IUserService service = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, null);
		try {
			return service.getUser(cond);
		} finally {
		    service.releaseAll();
		}
    }
    
    public static voUser getAdminUser(String cond) {
    	IUserService service = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, null);
    	try {
    		return service.getAdminUser(cond);
    	} finally {
    		service.releaseAll();
    	}
    }
    
	public static int[] string2ints(String str) {
		if(str == null || str.length() == 0)
			return new int[0];
		String[] strs = str.split("[,;]");
		return string2ints(strs);
	}
	public static int[] string2ints(String[] strs) {
		if(strs == null)
			return new int[0];
		List list = new ArrayList(strs.length);
		for(int i = 0;i < strs.length;i++) {
			int id = StringUtil.toInt(strs[i]);
			if(id > 0)
				list.add(new Integer(id));
		}
		if(list.size() == 0)
			return new int[0];
		int[] ids = new int[list.size()];
		for(int i = 0;i < list.size();i++) {
			ids[i] = ((Integer)list.get(i)).intValue();
		}
		return ids;
	}
}
