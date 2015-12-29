/*
 * Created on 2005-5-31
 *
 */
package adultadmin.util.filter;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.system.admin.AdminService;
import mmb.user.secure.LoginUtil;
import mmb.util.Secure;
import adultadmin.action.vo.voUser;
import adultadmin.framework.AccessController;
import adultadmin.framework.IConstants;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.Constants;
import adultadmin.util.CookieUtil;
import adultadmin.util.LogUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.UserControlUtil;
import adultadmin.util.db.DbOperation;




/**
 * @author Bomb

 *
 */
public class UserControlFilter implements Filter {
	
	private static Map<Integer, String> userSessionIdMap = new ConcurrentHashMap<Integer, String>();

	public static void setUserSessionId(int userid, String seesionId) {
		userSessionIdMap.put(userid, seesionId);
	}

    public static void main(String[] args){
        // System.out.println(accessAllowedIp.pattern());
    	System.out.println(accessAllowedIp.matcher("183.62.25.193").matches());	// 错误的地址
    	System.out.println(accessAllowedIp.matcher("183.62.25.194").matches());	// 正确的地址
    	System.out.println(accessAllowedIp.matcher("183.62.25.195").matches());	// 正确的地址
    	System.out.println(accessAllowedIp.matcher("183.62.25.196").matches());	// 正确的地址
    	System.out.println(accessAllowedIp.matcher("183.62.25.197").matches());	// 正确的地址
    	System.out.println(accessAllowedIp.matcher("183.62.25.198").matches());	// 正确的地址
    	System.out.println(accessAllowedIp.matcher("183.62.25.199").matches());	// 错误的地址
    	System.out.println(accessAllowedIp.matcher("218.240.58.22").matches());	// 正确的地址
    	System.out.println(accessAllowedIp.matcher("120.195.108.136").matches());	// 正确的地址
    	System.out.println(accessAllowedIp.matcher("124.207.34.159").matches());	// 正确的地址
    }

	public static Pattern accessAllowedIp = Pattern.compile("219\\.238\\.200\\.(3[2-9]|[45][0-9])|192\\.168\\..*|127\\.0\\..*|221\\.179\\.215\\..*|218\\.205\\.223\\.4[0-3]|58\\.215\\.221\\.4[2-6]|58\\.214\\.14\\.74|183\\.62\\.25\\.19[4-8]|218\\.240\\.58\\..*|120\\.195\\.108\\.136|124\\.207\\.34\\.1[3-5][0-9]");

	public static Set<String> urlIgnoreMC = new HashSet<String>();

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
//		loginURL = config.getInitParameter("LoginURL");
		String pmask = config.getServletContext().getInitParameter("pmask");
		voUser.pmaskId = StringUtil.toInt(pmask);
		String outmask = config.getServletContext().getInitParameter("outmask");
		voUser.outMaskId = StringUtil.toInt(outmask);
		//新IP地址过滤初始化
		UserControlUtil.initAllowedIp();
		UserControlUtil.initAllowedHessianIp();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest hsr = (HttpServletRequest)request;
		HttpSession session = hsr.getSession(false);
		
		String url = hsr.getServletPath();
		String ipFrom = hsr.getRemoteAddr();
		boolean accessAllow = UserControlUtil.isAllowedIp(ipFrom); // 是否属于内部ip
		if (!accessAllow && Constants.OUT_HTTPS && hsr.getScheme().equalsIgnoreCase("HTTP")) {
			String basePath = request.getServerName();
			((HttpServletResponse) response).sendRedirect("https://" + basePath + hsr.getContextPath() + url);
			return;
		}

		// 防止用户拼命刷新后台页面
//    	if(!AccessController.isCanAccess() || !AccessController.isNormalAccess(hsr)){
//    		return;
//    	}
       	if (url.equalsIgnoreCase("/login.do") || url.equalsIgnoreCase("/admin/login.mmx") || url.indexOf("logout.jsp")!=-1 || url.indexOf("waiburukou.jsp")!=-1 || url.indexOf("/wap/") != -1 || url.indexOf("soLogin") != -1 || url.indexOf("stockOperation/login") != -1||url.indexOf("/enter/")!=-1){
			chain.doFilter(request,response);
			return;
		}
		voUser user = null;
		if(session != null)
			user = (voUser)session.getAttribute("userView");
		
		// 判断用户的IP地址是否是 公司内部的
		//如果是公司内部的就不做限制
        //如果不是公司内部的
        //再判断session里面是否有 waiburukou 这个对象
        //如果有，则放行
        //如果没有，则跳转到外部入口登录页面
        // 如果没有外部访问权限，使用的又不是内部ip，直接跳去登陆
		if(user != null&&!accessAllow && !user.getGroup().isFlag(26) && session.getAttribute("waiburukou") == null){
			session.removeAttribute("userView");
			((HttpServletResponse)response).sendRedirect(hsr.getContextPath() + "/login.do");
			return;
		}
	

        // 把重复登录帐号踢下线
  		voUser myUser = null;
  		if (session != null) {
  			myUser = (voUser) session.getAttribute("myUser");
  		}
  		if (myUser == null) {
  			myUser = user;
  		}
  		if (myUser != null && myUser.getAlone() == 0) {
 			String seesionId = userSessionIdMap.get(user.getId());
 			String invalidSeesion = session.getId();
 			if (seesionId == null) {
 				userSessionIdMap.put(user.getId(), invalidSeesion);
 			} else if (!invalidSeesion.equals(seesionId)) {
 				session.invalidate();
 				user = null;
 			}
 		}
        
		if (user != null) {
			if (urlIgnoreMC.contains(url)) {
				chain.doFilter(request, response);
			}else{
				if (AccessController.isLimitTimeAccessStart(hsr, "fixation_url")) {
					try {
						chain.doFilter(request, response);
					} finally {
						AccessController.isLimitTimeAccessEnd(hsr, "fixation_url");
					}
				}else{
					request.getRequestDispatcher("/admin/busyTip.jsp").forward(request, response);
				}
			}
			return;
		} else 	{	// 试图从cookie登陆
			AdminService adminService = null;
			try{
				adminService = new AdminService();
				CookieUtil ck = new CookieUtil(hsr, (HttpServletResponse)response);		// 根据cookie登陆
				String username = ck.getCookieValue("opau");
				String ccode = ck.getCookieValue("opap");

				if(username != null && ccode != null) {

					voUser vo = adminService.getAdmin(username);

					if(vo != null) {
						long cookieTime = System.currentTimeMillis() / 86400000l / 7;	// 每个cookie最多有7天有效期
						String cookieHash = Secure.encryptCookie(username, vo.getPassword(), adultadmin.util.IP.getRemoteAddr(hsr), ccode, cookieTime);
						if(cookieHash.equals(vo.getCookieHash())) {
							if(!vo.getGroup().isFlag(26)) {
								if(!accessAllow){
									((HttpServletResponse)response).sendRedirect(hsr.getContextPath() + "/login.do");
									return ;
								}
							}
							session = hsr.getSession(true);

							session.setAttribute(IConstants.USER_VIEW_KEY, vo);
							setUserSessionId(vo.getId(),session.getId());
							session.setAttribute(IConstants.USER_LOGIN_USERNAME, vo.getUsername());
							// cookie登陆之前有日志但是没有记录username
							String str = vo.getUsername() + "\t" + vo.getId() + "\t" + url + "\t" + hsr.getRemoteAddr();
							LogUtil.logAccess(str);
							// 登录log记录～
							LoginUtil.log2Db((HttpServletRequest) request, username, "", 0);
							LoginUtil.recordUserNameSuccess(username, (HttpServletRequest) request);
							/**************************************************/
							//不同分支和账号切换自动登录授权账号
							List<Object[]> ret = new ArrayList<Object[]>();
							ResultSet rs = null;
							try {
								rs = adminService.getDbOp().executeQuery("select * from authorize where in_use=1 and suser_id="+vo.getId()+" and now()>start_time and now()<end_time");
								if (rs != null) {
									while (rs.next()) {
										int n = rs.getMetaData().getColumnCount();
										Object[] objs = new Object[n];
										for(int i = 0;i < n;i++)
											objs[i] = rs.getObject(i + 1);
										ret.add(objs);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							//自动授权
							if(ret!=null&&ret.size()!=0){
								 Object[] objArray=(Object[]) ret.get(0);
								 voUser author = adminService.getAdmin(objArray[6].toString());
								 session.setAttribute("userView", author);
								 session.setAttribute("myUser", vo);
							}
							/************************************************/
							chain.doFilter(request,response);
							return;
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				adminService.releaseAll();
			}
		}
		if(!url.equals("/welcome.jsp"))	// 子窗口重登陆需要调用logout.jsp，整页面刷新只需要重定向
			((HttpServletResponse)response).sendRedirect(hsr.getContextPath() + "/admin/logout.jsp");
		else
			((HttpServletResponse)response).sendRedirect(hsr.getContextPath() + "/login.do");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
