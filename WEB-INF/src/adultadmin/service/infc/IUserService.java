/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;

import adultadmin.action.vo.UserInfoBean;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserPermissionBean;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-1-24
 * 
 * 说明：
 */
public interface IUserService extends IBaseService {
    public boolean addUser(voUser user);
    
    public boolean addAdminUser(voUser user);

    public voUser getUser(String condition);
    
    public voUser getAdminUser(String condition);

    public int getUserCount(String condition);
    
    public int getAdminUserCount(String condition);

    public boolean updateUser(String set, String condition);
    
    public boolean updateAdminUser(String set, String condition);

    public boolean deleteUser(String condition);
    
    public boolean deleteAdminUser(String condition);

    public ArrayList getUserList(String condition, int index, int count,
            String orderBy);
    
    public ArrayList getAdminUserList(String condition, int index, int count,
    		String orderBy);

    public ArrayList queryUserListByPermission(String condition, int index, int count, String orderBy);
    
    public ArrayList queryAdminUserListByPermission(String condition, int index, int count, String orderBy);

    public String getLastLoginTime(String phone);
    
    public boolean addUserPermission(UserPermissionBean userPermission);

    public UserPermissionBean getUserPermission(String condition);

    public int getUserPermissionCount(String condition);

    public boolean updateUserPermission(String set, String condition);

    public boolean deleteUserPermission(String condition);

    public ArrayList getUserPermissionList(String condition, int index, int count,
            String orderBy);
    
    //  user_info
    public boolean addUserInfo(UserInfoBean user);

    public UserInfoBean getUserInfo(String condition);

    public int getUserInfoCount(String condition);

    public boolean updateUserInfo(String set, String condition);

    public boolean deleteUserInfo(String condition);

    public ArrayList getUserInfoList(String condition, int index, int count,
            String orderBy);
    
    /**
     * 
     * 功能:获取uservo 跟性别
     * <p>作者 李双 Dec 19, 2011 11:42:36 AM
     * @param condition
     * @return
     */
    public voUser getVoUserAndGender(String condition) ;
    
   /**
    * 
    * 功能:获取uservo list 跟性别
    * <p>作者 李双 Dec 19, 2011 11:42:36 AM
    * @param condition
    * @return
    */
    public ArrayList getVoUserAndGenderList(String condition);
    
}
