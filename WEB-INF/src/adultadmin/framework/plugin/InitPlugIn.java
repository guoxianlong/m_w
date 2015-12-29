/*
 * Created on 2005-5-17
 *
 */
package adultadmin.framework.plugin;

import javax.servlet.ServletException;

import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.PlugIn;
import org.apache.struts.config.ModuleConfig;

import adultadmin.framework.IConstants;
import adultadmin.service.IServiceFactory;
import adultadmin.service.ServiceFactory;

/**
 * @author Bomb
 *  
 */
public class InitPlugIn implements PlugIn {

	private IServiceFactory serviceFactory=null;

	public void destroy() {
		if (serviceFactory != null) {
			serviceFactory = null;
		}
	}

	public void init(ActionServlet servlet, ModuleConfig config)
			throws ServletException {

		try{
			serviceFactory = ServiceFactory.getInstance();
			servlet.getServletContext().setAttribute(
					IConstants.SERVICE_FACTORY_KEY, serviceFactory);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
