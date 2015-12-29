/**
 * 
 */
package adultadmin.servlet;

// import smscenter.util.EsmsThread;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author Bomb
 * 
 */
public class AppStopServlet extends HttpServlet {

	ServletContext servlet = null;

	public void init(ServletConfig config) throws ServletException {
		servlet = config.getServletContext();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		if (servlet != null) {

		}

		super.destroy();
	}

}
