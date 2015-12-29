/**
 * 
 */
package adultadmin.action.admin;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.system.admin.AdminService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.bean.OrderImportLogBean;
import adultadmin.bean.PagingBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author zhangtao
 * 
 */
public class OrderImportLogsAction extends BaseAction {

	static private int pagerow = 30;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int currentpage = StringUtil.StringToId(request.getParameter("currentPage"));
		int type = StringUtil.toInt(request.getParameter("type"));

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave2");
		IAdminLogService service = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, dbOp);
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			String condition = null;
			if (type >= 0) {
				condition = " type=" + type;
			}

			int totalCount = service.getOrderImportLogCount(condition);
			List list = service.getOrderImportLogList(condition, pagerow * currentpage, pagerow, "id desc");

			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				OrderImportLogBean log = (OrderImportLogBean)iter.next();
				log.setUser(adminService.getAdmin(log.getUserId()));
			}

			request.setAttribute("logList", list);
            PagingBean paging = new PagingBean(currentpage, totalCount, pagerow);
            String prefixUrl = "orderImportLogs.do?type=" + type;
            prefixUrl = StringUtil.dealLink(prefixUrl, request, response);
            paging.setPrefixUrl(prefixUrl);
            request.setAttribute("paging", paging);
		} finally {
			dbOp.release();
		}
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}
