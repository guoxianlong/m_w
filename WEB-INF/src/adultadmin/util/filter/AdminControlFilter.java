/*
 * Created on 2005-5-31
 *
 */
package adultadmin.util.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * @author Bomb
 *
 */
public class AdminControlFilter implements Filter {
	
	private String loginURL = "/esms/admin/login.do";

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException {
		// TODO Auto-generated method stub
//		loginURL = config.getInitParameter("LoginURL");
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpSession session = ((HttpServletRequest)request).getSession(false);
		
		String url = ((HttpServletRequest)request).getRequestURI();
//		String action = StringUtil.getUrlAction(url);
//		if (url.equalsIgnoreCase(loginURL) || action.equalsIgnoreCase("login")){
//			chain.doFilter(request,response);
//			return;
//		}
//
//		
//		if (session != null){
//			if (session.getAttribute("adminView") != null){
//				chain.doFilter(request,response);
//				return;
//			}
//		}
//		((HttpServletResponse)response).sendRedirect(loginURL);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
