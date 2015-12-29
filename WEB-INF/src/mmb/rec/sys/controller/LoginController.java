package mmb.rec.sys.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.system.admin.AdminService;
import mmb.user.secure.LoginUtil;
import mmb.util.Secure;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import adultadmin.action.vo.voUser;
import adultadmin.framework.IConstants;
import adultadmin.util.CookieUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.UserControlUtil;

@Controller
@RequestMapping("/admin")
public class LoginController {

	@RequestMapping("/login")
	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String username = StringUtil.dealParam(request.getParameter("username"));
		String password = request.getParameter("password"); 
		
		///增加用户名登陆失败次数限制以及同一个IP登陆失败次数限制
		if(username!=null){
	    	int loginCheck = LoginUtil.loginCheck(username, request);
	        if(loginCheck != 0){
	        	LoginUtil.log2Db(request, username, password,loginCheck);
	        	String msg = "";
	        	if(loginCheck==1){
	        		msg = "你的账号登陆失败次数过多，请十分钟后再试。";
	        	}
	        	if(loginCheck==2){
	        		msg = "你的IP地址不允许访问，请联系技术人员。";
	        	}
	        	if(loginCheck==3){
	        		msg = "你的IP："+adultadmin.util.IP.getRemoteAddr(request)+" 登陆失败次数过多，请一分钟后再试。";
	        	}
	        	
	        	request.setAttribute("errorMsg", msg);
	        	
	        	return "/admin/rec/login";
	        }  
		}
        
		CookieUtil ck = new CookieUtil(request, response);		// 写入cookie
		if(username != null) {		// post提交才有效
			boolean ru = request.getParameter("ru") != null;
	    	if(ru) {		// 记住用户名
	    		if(username.length() > 0)
	    			ck.setCookie("u", username, 2000000000);
	    		ck.setCookie("ru", "1", 2000000000);
	    	} else {
	    		ck.removeCookie("u");
	    		ck.setCookie("ru", "0", 2000000000);
	    	}
		}
		AdminService adminService = null;
		try{
			if(username == null)
				return "/admin/rec/login";
			boolean accessAllow = UserControlUtil.isAllowedIp(adultadmin.util.IP.getRemoteAddr(request));
			adminService=new AdminService();
			voUser vo =adminService.getAdmin(username);  
			if(vo==null){
				return "/admin/rec/login";
			}
			
			// clear otp passcode
			String pass1 = vo.getOtpPassword();	// 短信密钥
			if(pass1.length() > 5) {
				if(vo.getOtpTime() + 5 * 60 * 1000 < System.currentTimeMillis()) {
					vo.setOtpPassword("");
					pass1 = "";
					adminService.getDbOp().executeUpdate(
							"update admin_user set otp_password='',otp_time=0 where id=" + vo.getId());
				}
			}
			String key2 = vo.getOtpKey();
			String pass2 = null, pass3 = null, pass4 = null, pass5 = null;
			if (key2.length() > 0) {
				pass2 = adultadmin.util.TOTP.getTOTPLsat(key2, -55); // oath密钥 前60秒密钥
				pass3 = adultadmin.util.TOTP.getTOTPLsat(key2, -25); // oath密钥 前60秒密钥
				pass4 = adultadmin.util.TOTP.getTOTPLsat(key2, 5); // oath密钥
				pass5 = adultadmin.util.TOTP.getTOTPLsat(key2, 35); // oath密钥 后30秒密钥
			}
			if (!accessAllow) {
				if (!vo.getGroup().isFlag(26)) {
					LoginUtil.log2Db(request, username, password, 4);
					return "/admin/rec/login";
				}

				if (pass1.length() > 5 && password.endsWith(pass1)) {
					adminService.getDbOp().executeUpdate(
							"update admin_user set otp_password='',otp_time=0 where id=" + vo.getId());
					password = password.substring(0, password.length() - pass1.length());
				} else if (pass2 != null && (password.endsWith(pass2) || password.endsWith(pass3) || password.endsWith(pass4) || password.endsWith(pass5))) {
					password = password.substring(0, password.length() - pass2.length());
				} else {
					int isok = adminService.getDbOp().getInt(
							"select id from admin_user_mobile where user_id=" + vo.getId() + " limit 1");
					if (isok != 0) {
						return "/admin/rec/login";
					}
				}
			} else {
				long time = System.currentTimeMillis() - 5 * 60 * 1000;
				if (pass1.length() > 5 && password.endsWith(pass1) && vo.getOtpTime() > time) {
					adminService.getDbOp().executeUpdate(
							"update admin_user set otp_password='',otp_time=0 where id=" + vo.getId());
					password = password.substring(0, password.length() - pass1.length());
				} else if (pass2 != null && (password.endsWith(pass2) || password.endsWith(pass3) || password.endsWith(pass4) || password.endsWith(pass5))) {
					password = password.substring(0, password.length() - pass2.length());
				}
			}
			if (!vo.getPassword().equals(Secure.encryptPwd(password))) {
				LoginUtil.recordError(username, request);
				LoginUtil.log2Db(request, username, password, -1); // /-1表示
																	// 密码或者用户名错误
				String msg = "用户名或密码错误，请重新输入。";
				request.setAttribute("errorMsg", msg);
				return "/admin/rec/login";
			}
						
			LoginUtil.log2Db(request, username, password,0);
			LoginUtil.recordUserNameSuccess(username,request);

			ck.removeCookie("opau");
			ck.removeCookie("opap");	// 确保旧版本cookie删除
			
	    	ck.setCookieSafe("opau", ".ebinf.com", username, -1);
	    	String ccode = request.getSession().getId();	// 用sessionid来替代ccode，安全性足够
	    	long cookieTime = System.currentTimeMillis() / 86400000l / 7;	// 每个cookie最多有7天有效期
	    	String cookieHash = Secure.encryptCookie(username, vo.getPassword(), adultadmin.util.IP.getRemoteAddr(request), ccode, cookieTime);
	    	// 保存hash到数据库用于cookie登陆
	    	adminService.getDbOp().executeUpdate("update admin_user set cookie_hash='" + cookieHash + "' where id=" + vo.getId());
	    	
	    	ck.setCookieSafe("opap", ".ebinf.com", ccode, -1);
	    	
	    	boolean rp = request.getParameter("rp") != null;

	    	if(rp) {		// 记住密码
//	    		ck.setCookie("p", password, 2000000000);
	    		ck.setCookie("rp", "1", 2000000000);
	    		request.getSession().setAttribute("simplifyWeb", "1");// 简化版
	    	} else {
	    		ck.removeCookie("p");
	    		ck.setCookie("rp", "0", 2000000000);
	    	}
			request.getSession().setAttribute(IConstants.USER_VIEW_KEY, vo);
			
		} catch (Exception e) {
			e.printStackTrace();
			return "/admin/rec/login";
		}finally{
			if(adminService != null){
				adminService.releaseAll();
			}
		}
		
		return "forward:/admin/default.mmx";
	}
	
	@RequestMapping("default")
	public String toDefault (HttpServletRequest request, HttpServletResponse response) {
		return "forward:/admin/rec/index.jsp";
	}

}
