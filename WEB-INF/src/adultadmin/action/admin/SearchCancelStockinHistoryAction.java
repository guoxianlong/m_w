/**
 * 
 */
package adultadmin.action.admin;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.stock.StockOperationBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICatalogService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 * 
 */
public class SearchCancelStockinHistoryAction extends BaseAction {

	/**
	 * 
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String name = StringUtil.dealParam(request.getParameter("name"));
		String username = StringUtil.dealParam(request.getParameter("username"));
		String code = StringUtil.dealParam(request.getParameter("code"));
		String orderCode = StringUtil.dealParam(request.getParameter("orderCode"));
		String oriname = StringUtil.dealParam(request.getParameter("oriname"));
		String price = StringUtil.dealParam(request.getParameter("price"));
		String stockCount = StringUtil.dealParam(request.getParameter("stockCount"));
		String startDate = StringUtil.dealParam(request.getParameter("startDate"));
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));
		String area = request.getParameter("area");
		int type = StringUtil.StringToId(request.getParameter("type"));
		if (name == null)
			name = "";
		if (username == null)
			username = "";
		if (code == null)
			code = "";
		if (oriname == null)
			oriname = "";
		if (startDate == null)
			startDate = "";
		if (endDate == null)
			endDate = "";
		if (price == null)
			price = "";
		if (area == null)
			area = "";
		if (stockCount == null)
			stockCount = "";
		if (orderCode == null)
			orderCode = "";

		if (username.length() == 0 && name.length() == 0 && code.length() == 0 && oriname.length() == 0
				&& startDate.length() == 0 && endDate.length() == 0
				&& price.length() == 0 && area.length() == 0)
			return mapping.findForward(IConstants.SUCCESS_KEY);

		Date date1 = null;
		Date date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (startDate.length() > 0) {
			try {
				date1 = sdf.parse(startDate);
			} catch (Exception e) {
				request.setAttribute("tip", "开始时间格式不对！");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		}
		if (endDate.length() > 0) {
			try {
				date2 = sdf.parse(endDate);
			} catch (Exception e) {
				request.setAttribute("tip", "结束时间格式不对！");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		}
		if (date1 != null && date2 != null) {
			if (date2.before(date1)) {
				request.setAttribute("tip", "结束时间必须在开始时间之后！");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		}

		StringBuffer buf = new StringBuffer(512);

		String sql = null;
		buf.append("select p.id,p.name,p.oriname,p.code,p.price,p.price3,p.parent_id1,p.parent_id2,sh.stock_bj,so.area from ((stock_operation so join stock_history sh on so.id=sh.oper_id ) join user_order uo on so.order_code=uo.code) join product p on sh.product_id=p.id where ");
		if(type == 2){ //烂货退换
			buf.append("so.type=");
			buf.append(StockOperationBean.BAD_EXCHANGE);
		} else { // 退/换货
			buf.append("(so.type=");
			buf.append(StockOperationBean.CANCEL_STOCKIN);
			buf.append(" or so.type=");
			buf.append(StockOperationBean.CANCEL_EXCHANGE);
			buf.append(") ");
		}
		if (code.length() > 0) {
			buf.append(" and sh.product_code='");
			buf.append(code);
			buf.append("' ");
		}
		if (orderCode.length() > 0) {
			buf.append(" and so.order_code='");
			buf.append(orderCode);
			buf.append("' ");
		}
		if (username.length() > 0) {
			buf.append(" and uo.name='");
			buf.append(username);
			buf.append("' ");
		}
		if (oriname.length() > 0) {
			buf.append(" and p.oriname like '%");
			buf.append(oriname);
			buf.append("%' ");
		}
		if (name.length() > 0) {
			buf.append(" and p.name like '%");
			buf.append(name);
			buf.append("%' ");
		}
		if (area.length() > 0) {
			buf.append(" and so.area=");
			buf.append(area);
		}
		if (price.length() > 0) {
			float fPrice = StringUtil.toFloat(price);
			buf.append(" and p.price>=");
			buf.append(fPrice - 0.5);
			buf.append(" and p.price<=");
			buf.append(fPrice + 0.5);
		}
		if(stockCount.length() > 0){
			buf.append(" and sh.stock_bj=");
			buf.append(stockCount);
		}
		if (startDate.length() > 0) {
			buf.append(" and so.create_datetime >= '");
			buf.append(startDate);
			buf.append("'");
		}
		if (endDate.length() > 0) {
			buf.append(" and so.create_datetime <= '");
			buf.append(endDate);
			buf.append("'");
		}

		buf.append(" order by p.id asc, so.create_datetime desc");
		sql = buf.toString();
		DbOperation dbOp = new DbOperation();
		try {
			dbOp.init("adult_slave");
			ResultSet rs = dbOp.executeQuery(sql);
			List list = new ArrayList();
			voProduct product = null;
			while (rs.next()) {
				product = new voProduct();
				product.setId(rs.getInt("p.id"));
				product.setName(rs.getString("p.name"));
				product.setOriname(rs.getString("p.oriname"));
				product.setBjBuyCount(rs.getInt("sh.stock_bj"));
				product.setGdBuyCount(rs.getInt("so.area"));
				product.setCode(rs.getString("p.code"));
				product.setPrice(rs.getFloat("p.price"));
				product.setPrice3(rs.getFloat("p.price3"));
				product.setParentId1(rs.getInt("p.parent_id1"));
				product.setParentId2(rs.getInt("p.parent_id2"));
				list.add(product);
			}
			rs.close();
			ICatalogService catalogService = ServiceFactory.createCatalogService(IBaseService.CONN_IN_SERVICE, dbOp);
			List catalogList = catalogService.getCatalogList(null, -1, -1, "id asc");
			HashMap catalogMap = new HashMap();
			Iterator iter = catalogList.listIterator();
			while (iter.hasNext()) {
				voCatalog catalog = (voCatalog) iter.next();
				catalogMap.put(Integer.valueOf(catalog.getId()), catalog);
			}
			request.setAttribute("productList", list);
			request.setAttribute("catalogMap", catalogMap);
		} finally {
			dbOp.release();
		}

		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}
