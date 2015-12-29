/**
 * 
 */
package adultadmin.framework;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;

/**
 * @ Bomb
 */
public class BaseAction extends Action {

	protected Object getSessionObject(HttpServletRequest request, String attName) {
		HttpSession session = request.getSession(false);
		Object sessionObject = null;
		if (session != null)
			sessionObject = session.getAttribute(attName);
		return sessionObject;
	}

	/**
	 * 根据attrName，得到Application中存放的对象。
	 * 
	 * 
	 * 
	 * 
	 * @param attrName
	 * @return 返回得到的Application中存放的对象，如果没有得到返回null
	 */
	protected Object getApplicationObject(String attrName) {
		return servlet.getServletContext().getAttribute(attrName);
	}

	/**
	 * 
	 * @param attrName
	 * @param object
	 */
	protected void setApplicationObject(String attrName, Object object) {
		servlet.getServletContext().setAttribute(attrName, object);
	}

	/***************************************************************************
	 * @param request
	 * @return 登录了返回true，否则返回false
	 */
	public boolean isLoggedIn(HttpServletRequest request) {
		if (getSessionObject(request, IConstants.USER_LOGIN_KEY) != null) {
			return true;
		} else {
			return false;
		}
	}

}