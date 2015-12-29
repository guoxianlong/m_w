package mmb.rec.pda.util;

import javax.servlet.http.HttpServletRequest;

import mmb.system.admin.AdminService;
import mmb.util.Secure;
import adultadmin.action.vo.voUser;

/**
 * 说明：用于PDA登录验证
 * 
 * 时间：2013-07-30
 * 
 * 作者：石远飞
 */
public class CheckUser {
	public static boolean checkUser(HttpServletRequest req, String userName,String password){
		boolean flag = false;
		AdminService adminService = new AdminService();
		try {
			voUser user = adminService.getAdmin(userName);
			req.getSession().setAttribute("userView", user);
			if(user!=null && user.getPassword()!=null && user.getPassword().equals(Secure.encryptPwd(password))){
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			adminService.releaseAll();
		}
		return flag;
	}
}
