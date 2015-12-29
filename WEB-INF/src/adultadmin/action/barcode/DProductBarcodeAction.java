/**
 * 
 */
package adultadmin.action.barcode;


import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.barcode.BarcodeLogBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

/**
 * 删除无效的条形码
 * @author szl
 *
 */
public class DProductBarcodeAction  extends BaseAction{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		WareService service = new WareService();
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		voUser admin = (voUser) request.getSession().getAttribute(
	              IConstants.USER_VIEW_KEY);
		   if(admin==null){
		    request.setAttribute("tip", "对不起，你还没有登录，请先登录。");
		    return mapping.findForward("failure");
		   }
		   UserGroupBean group = admin.getGroup();
		   if(!group.isFlag(292)){
			   request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
	           request.setAttribute("result", "failure");
		}
		// 条形吗id
		int id = StringUtil.StringToId(request.getParameter("id"));
		int pid = StringUtil.StringToId(request.getParameter("pid"));
		String oldBarcode = request.getParameter("oldBarcode");
		boolean flag = false;
		boolean addLog = false;
		PrintWriter out = response.getWriter();
		String condition = "";
		voProduct vo = null;
		vo = service.getProduct(pid);
		try {
			if(id>0){
				condition = "id="+id;
				flag = bcmService.deleteProductBarcode(condition);
				if(vo != null){
					int num = bcmService.getNumber("id", "barcode_log", "max", "id>0")+1;
					BarcodeLogBean barcodeLogBean = new BarcodeLogBean();
					barcodeLogBean.setId(num);
					barcodeLogBean.setAdminName(admin.getUsername());
					barcodeLogBean.setProductId(vo.getId());
					barcodeLogBean.setOriname(vo.getOriname());
					barcodeLogBean.setPname(vo.getName());
					barcodeLogBean.setOldBarcode(oldBarcode);
					barcodeLogBean.setBarcode("");
					barcodeLogBean.setOperDatetime(DateUtil.getNow());
					addLog = bcmService.addProductBarcodeLog(barcodeLogBean);
				}
			}
		} finally {
			bcmService.releaseAll();
		}
		if(flag && addLog){
			out.print("success");
		}else{
			out.print("failure");
		}
		return null;
	}
}
