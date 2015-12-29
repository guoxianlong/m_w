package adultadmin.action.stock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.stat.AreaStockExchangeService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.framework.BaseAction;
import adultadmin.service.infc.IBaseService;

public class StockManualAction extends BaseAction {
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AreaStockExchangeService service = new AreaStockExchangeService(IBaseService.CONN_IN_SERVICE, null);
		service.createExchangeProductList();
		return super.execute(mapping, form, request, response);
	}
}
