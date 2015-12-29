package adultadmin.action.stock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.framework.BaseAction;
import adultadmin.util.db.DbOperation;
/**
 * 
 * 作者：郝亚斌
 * 时间：2014-07-08
 * 说明：刷新库存卡片类型缓存
 */
public class StockCardTypeCacheAction extends BaseAction {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		try{
			response.setContentType("text/html;charset=UTF-8");
			StockCardBean.setStockCardTypeMap(wareService);
			response.getWriter().write("进销存卡片类型刷新成功！");
		}catch(Exception e){
			e.printStackTrace();
			response.getWriter().write("进销存卡片类型刷新失败！");
		}finally{
			wareService.releaseAll();
		}
		return null;
	}

}
