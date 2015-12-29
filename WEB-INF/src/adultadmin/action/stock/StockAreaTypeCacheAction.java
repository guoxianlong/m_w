package adultadmin.action.stock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.util.db.DbOperation;
/**
 * 
 * 作者：朱爱林
 * 时间：2013-10-25
 * 说明：刷新库地区、库类型缓存
 */
public class StockAreaTypeCacheAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ProductStockBean.initStockAreaTypeCacheAll();
		response.getWriter().write("库类型、库区域刷新成功！");
		return null;
	}

}
