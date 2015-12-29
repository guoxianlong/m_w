/**
 * 
 */
package adultadmin.action.admin;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.framework.BaseAction;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

/**
 * @author Bomb
 * 
 */
public class FProductAction extends BaseAction {

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("add");
		}
		UserGroupBean group = user.getGroup();

		int id = StringUtil.StringToId(request.getParameter("id"));
		int catalogId = StringUtil
		.StringToId(request.getParameter("catalogId"));
		WareService service = new WareService();
		ISupplierService supplierService = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IProductStockService psService = ServiceFactory
		.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		IProductPackageService ppService = ServiceFactory
		.createProductPackageService(IBaseService.CONN_IN_SERVICE,
				psService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, psService.getDbOp());
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, psService.getDbOp());
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		try {
			List statusList = service.getSelects("product_status",
			"order by id");
			request.setAttribute("statusList", statusList);
			//			List proxyList = service.getSelects("product_proxy",
			//					"order by seq asc");
			String supplierIds = ProductLinePermissionCache.getProductLineSupplierIds(user);
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List proxyList = supplierService.getSupplierStandardInfoList("status = 1 and id in (" + supplierIds + ")", -1, -1, "name_abbreviation asc");
			request.setAttribute("proxyList", proxyList);
			List brandList = service.getSelects("product_brand", "order by id");
			request.setAttribute("brandList", brandList);

			if (id == 0) {
				return mapping.findForward("add");
			}
			
			voProduct vo = service.getProduct(id);
			if (vo == null) {
				return mapping.findForward("add");
			}
			vo.setOriname(StringUtil.removeChangeLine(vo.getOriname()));
			List psList = psService.getProductStockList(
					"product_id=" + vo.getId(), -1, -1, "area asc, type asc");
			request.setAttribute("psList", psList);

			boolean inPackage = false;
			if (ppService.getProductPackageCount("product_id=" + id) > 0) {
				inPackage = true;
			}
			request.setAttribute("inPackage", Boolean.valueOf(inPackage));

			// 查询产品条码信息
			ProductBarcodeVO barcodeVO = bcmService.getProductBarcode("product_id="+id+" and barcode_status=0");
			request.setAttribute("product", vo);
			request.setAttribute("catalogId", Integer.valueOf(catalogId));
			request.setAttribute("barcodeVO", barcodeVO);

			//计算广东采购在途量
			int buyCountGD = 0;
			String condition = "product_id=" + vo.getId()
			+ " and buy_order_id in (select id from buy_order where complete_status=0 and "
			+ "status = " + BuyOrderBean.STATUS3 + " or status ="
			+ BuyOrderBean.STATUS5 + ")";
			ArrayList bopList = stockService.getBuyOrderProductList(condition,
					-1, -1, null);
			Iterator bopIterator = bopList.listIterator();
			while (bopIterator.hasNext()) {
				BuyOrderProductBean bop = (BuyOrderProductBean) bopIterator
				.next();
				buyCountGD += (bop.getOrderCountGD() - bop.getStockinCountGD() - bop.getStockinCountBJ()) > 0 ? (bop
						.getOrderCountGD() - bop.getStockinCountGD() - bop.getStockinCountBJ()) : 0;
			}
			request.setAttribute("buyCountGD", Integer.valueOf(buyCountGD));

			// xiao lei add可发货量
			int qcbs = 0;
			String selSql = "select qcbs from product_stock_property where product_id="
				+ id;
			ResultSet rsQcbs = stockService.getDbOp().executeQuery(
					selSql.toString());
			if (rsQcbs.next()) {
				qcbs = rsQcbs.getInt(1);
			}
			request.setAttribute("qcbs", String.valueOf(qcbs));

			//代理商信息
			SupplierStandardInfoBean supplier = supplierService.getSupplierStandardInfo("id = "+vo.getProxyId());

			request.setAttribute("supplier",supplier);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
			service.releaseAll();
			psService.releaseAll();
		}
		return mapping.findForward("modify");
	}
}
