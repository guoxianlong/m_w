/**
 * 
 */
package adultadmin.action.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

/**
 * @author Bomb
 *
 */
public class SearchBatchAction  extends BaseAction{

	/**
	 *  
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		voUser adminUser = (voUser)request.getSession().getAttribute("userView");

		String code = StringUtil.dealParam(request.getParameter("code"));
		String name = StringUtil.dealParam(request.getParameter("name"));
		if(code == null)
			code = "";
		if(name == null)
			name = "";

		if(code.length() == 0 && name.length() == 0)
			return mapping.findForward(IConstants.SUCCESS_KEY);

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try{
			StringBuffer buf = new StringBuffer(128);
			if(code.length() > 0){
				if(buf.length() > 0){
					buf.append(" and ");
				}
				buf.append(" code = '");
				buf.append(code);
				buf.append("' ");
			}
			if(name.length() > 0){
				if(buf.length() > 0){
					buf.append(" and ");
				}
				buf.append(" oriname = '");
				buf.append(name);
				buf.append("' ");
			}


			voProduct product = wareService.getProduct2(buf.toString());

			if(product==null)
				return mapping.findForward(IConstants.SUCCESS_KEY);

			// 增加产品线权限的判断
			String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
			String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
			int parentId1 = product.getParentId1();
			int parentId2 = product.getParentId2();

			if(!(StringUtil.hasStrArray(catalogIds1.split(","),parentId1+"")||
					StringUtil.hasStrArray(catalogIds2.split(","),parentId2+""))){
				request.setAttribute("isError", "您无权查看该产品！");
			}

			List batchCodes = service.getFieldList("code", "stock_batch", "product_id="+product.getId(), -1, -1, "code", "id", "String");
			List psList = stockService.getProductStockList("product_id=" + product.getId(), -1, -1, "area asc, type asc");
			List showBatchList = new ArrayList();
			Iterator itr = batchCodes.iterator();
			while (itr.hasNext()) {
				String batchCode = (String)itr.next();
				List batchList = service.getStockBatchList("code='"+batchCode+"' and product_id="+product.getId(), -1, -1, "id");
				int batchCountTotal = 0;
				float batchPrice = 0;
				HashMap batchMap = new HashMap();
				for(int i=0;i<batchList.size();i++){
					StockBatchBean batch = (StockBatchBean)batchList.get(i);
					batchCountTotal = batchCountTotal + batch.getBatchCount();
					batchPrice = batch.getPrice();

					Iterator psIter = psList.listIterator();
					while(psIter.hasNext()){
						ProductStockBean ps = (ProductStockBean)psIter.next();

						if(batch.getProductStockId() == ps.getId()){

							batchMap.put(batch.getCode()+"_"+batch.getProductStockId(), Integer.valueOf(batch.getBatchCount()));
						}
					}

				}

				//查询批次创建时间
				String batchTime = "";
				StockCardBean bean = stockService.getStockCard("code = '"+batchCode+"'");
				if(bean != null){
					batchTime = bean.getCreateDatetime();
				}

				batchMap.put("code", batchCode);
				batchMap.put("batchCountTotal", Integer.valueOf(batchCountTotal));
				batchMap.put("batchPrice", Float.valueOf(batchPrice));
				batchMap.put("batchTime", batchTime);

				showBatchList.add(batchMap);
			}

			product.setPsList(psList);
			request.setAttribute("psList", psList);
			request.setAttribute("product", product);
			request.setAttribute("showBatchList", showBatchList);
		}finally{
			service.releaseAll();
		}

		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}
