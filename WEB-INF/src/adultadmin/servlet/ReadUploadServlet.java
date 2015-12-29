package adultadmin.servlet;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import adultadmin.action.vo.voUser;
import adultadmin.util.Constants;


public class ReadUploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		voUser user = null;
		if(session != null)
			user = (voUser)session.getAttribute("userView");
		if(user == null)	// 没登陆不让看
			return;
		
		String root = Constants.WARE_UPLOAD ;
		response.setContentType("application/octet-stream");
		String l = request.getPathInfo();
		String path = root + request.getPathInfo();
		
		FileInputStream is = new FileInputStream(path);
        
        int len;
        byte[] b = new byte[1024];
        while ((len = is.read(b)) > 0) {
            response.getOutputStream().write(b, 0, len);
        }
        is.close();
	}

	public void destroy() {
		super.destroy();
	}

}
