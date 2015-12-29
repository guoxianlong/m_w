/**
 * 
 */
package adultadmin.action.admin;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voProduct;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 *
 */
public class AllProductsStockAction  extends BaseAction{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		int status = StringUtil.StringToId(request.getParameter("status"));
		String condition = null;
		if(status > 0){
			condition = "status in (" + status + ")";
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService service = new WareService(dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try {
			List list = service.getProductList1(condition, -1, -1, "rank asc, display_order asc, code asc");
			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
			}

			request.setAttribute("productList", list);
			request.setAttribute("count", list.size() + "");
		} finally {
			psService.releaseAll();
		}
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}
