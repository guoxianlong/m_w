package mmb.rec.sys.interceptors;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import adultadmin.action.vo.voUser;
import adultadmin.framework.IConstants;

/**
 * @author 石远飞
 */
public class AuthInterceptor implements HandlerInterceptor {

	@Override
	public void afterCompletion(HttpServletRequest arg0,
			HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1,
			Object arg2, ModelAndView arg3) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
			Object obj) throws Exception {
//		voUser user = (voUser) request.getSession().getAttribute("userView");
//		if (user == null) {// 没有登录系统，或登录超时
//			forward("您没有登录或登录超时，请重新登录！", request, response);
//			return false;
//		}
		return true;
	}
	private void forward(String msg, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setAttribute("tip", msg);
		request.setAttribute("result", "failure");
		request.getRequestDispatcher(IConstants.FAILURE_KEY).forward(request, response);
	}


}
