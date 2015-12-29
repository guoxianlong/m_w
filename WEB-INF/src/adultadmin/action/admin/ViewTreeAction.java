/**
 * 
 */
package adultadmin.action.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;

/**
 * @author Bomb
 *
 */
public class ViewTreeAction  extends BaseAction{

	/**
	 *  
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

//		WAdminServiceImpl service = getWAdminService();
//		IArticleCatalogService aservice = new ArticleCatalogServiceImpl();
//		IProductCatalogService pcs = ServiceFactory.createProductCatalogService(IBaseService.CONN_IN_SERVICE, null);
//		try {
//			List list = service.getCatalogs();
//			request.setAttribute("catalogList", list);

//			List articleCatalogList = aservice.getArticleCatalogList(null, -1, -1, " parent_id asc,code asc ");
//			request.setAttribute("articleCatalogList", articleCatalogList);

//			List orderStatusList = service.getSelects("user_order_status", "where visible=1 order by sec");
//			request.setAttribute("orderStatusList", orderStatusList);
			
//			List poolInfoList = service.getPools(0);
//			request.setAttribute("poolInfoList", poolInfoList);
//			
//			List poolProductList = service.getPools(1);
//			request.setAttribute("poolProductList", poolProductList);
//
//			List poolArticleList = service.getPools(2);
//			request.setAttribute("poolArticleList", poolArticleList);
//
//			List parsePoolInfoList = service.getPools(3);
//			request.setAttribute("parsePoolInfoList", parsePoolInfoList);
//			
//			List ClothingPoolInfoList = service.getPools(4);
//			request.setAttribute("ClothingPoolInfoList", ClothingPoolInfoList);
//			
//			List parseClothingInfoList = service.getPools(5);
//			request.setAttribute("parseClothingInfoList", parseClothingInfoList);
			// web
//			List list2 = service.getWCatalogs();
//			request.setAttribute("wcatalogList", list2);
			
//			List list3 = service.getWPools();
//			request.setAttribute("wpoolList", list3);

//			request.setAttribute("orderStockStatusMap", OrderStockStatusBean.orderStockStatusMap);
//			request.setAttribute("orderBalanceStatusMap", OrderBalanceStatusBean.orderBalanceStatusMap);

//			List spiCatalogList = pcs.getProductCatalogList(null, -1, -1, "level asc, display_order asc, id asc");
//			request.setAttribute("spiCatalogList", spiCatalogList);
//		} finally {
//			service.close();
//			pcs.releaseAll();
//		}
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}

}
