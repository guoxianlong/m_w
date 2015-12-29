package mmb.hessian;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import adultadmin.util.UserControlUtil;

import com.caucho.services.server.ServiceContext;

public class MMBHessianServlet extends
		com.caucho.hessian.server.HessianServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8846684123470577456L;

	/**
	 * 通过className 和 method 要能找到具体那个类的那个方法. className为类名，要求为全类名，如:
	 * mmb.service.order.OrderService 可以省略： mmb.service，相当于只在mmb.service下查找指定的类。
	 * method为方法名。 params为调用方法的函数。
	 */
	public Object service(String className, String method, Object... params) {
		HttpServletRequest req = (HttpServletRequest) ServiceContext.getContextRequest();
        String ipFrom = req.getRemoteAddr();
//        System.out.println("hessian:"+ipFrom);
        if(UserControlUtil.isAllowedHessianIp(ipFrom)){
        	try {
    			Class c = getClass(className);
    			Method m = null;
    			if (params == null || params.length < 1) {
    				m = c.getMethod(method, null);
    			} else {
    				Class[] paramClass = new Class[params.length];
    				int i = 0;
    				for (final Object obj : params) {
    					paramClass[i++] = obj.getClass();
    				}
    				m = c.getMethod(method, paramClass);
    			}
    			return m.invoke(c.newInstance(), params);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
        
		
		return null;
	}

	private final Class getClass(String className) {
		Class c = null;
		try {
			c = Class.forName(className, true, classLoader);
		} catch (ClassNotFoundException e) {
			try {
				c = Class.forName("mmb.hessian" + className, true, classLoader);
			} catch (ClassNotFoundException e1) {
			}
		}
		return c;
	}

	private static ClassLoader classLoader = MMBHessianServlet.class
			.getClassLoader();

}
