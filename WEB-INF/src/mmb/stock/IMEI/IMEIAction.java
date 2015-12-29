package mmb.stock.IMEI;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.framework.IConstants;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class IMEIAction extends DispatchAction {
	public ActionForward IMEIlogList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IMEIService IMEISericve = new IMEIService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String code = StringUtil.convertNull(request.getParameter("code"));//EMEI码编号
		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		try {
			    if(code!=null && code.length()>0){
					StringBuffer sql = new StringBuffer();
					StringBuilder url = new StringBuilder();
					url.append("IMEIAction.do?method=IMEIlogList");
					if (code != null && code.length() != 0) {
							sql.append(" and IMEI='"+StringUtil.dealParam(code)+"'");
							url.append("&code=" + code);
					}
					int totalCount = IMEISericve.getIMEILogCount("id>0 " + sql);
					int countPerPage = 20;
					int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
					PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
					List<IMEILogBean> IMEIlogList = IMEISericve.getIMEILogList("id>0" + sql, paging.getCurrentPageIndex() * countPerPage, countPerPage, null);
					request.setAttribute("IMEIlogList", IMEIlogList);
					paging.setPrefixUrl(url.toString());
					request.setAttribute("paging", paging);
			    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return mapping.findForward("IMEIlogList");
	}
}
