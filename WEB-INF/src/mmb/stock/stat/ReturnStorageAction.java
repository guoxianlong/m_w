package mmb.stock.stat;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.cargo.model.ReturnedProductVirtual;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.aftersale.AfStockService;
import mmb.stock.aftersale.AfterSaleDetectProductBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.formbean.ReturnedPackageFBean;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoModelBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.cargo.ReturnsReasonBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Constants;
import adultadmin.util.CookieUtil;
import adultadmin.util.DateUtil;
import adultadmin.util.PageUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;
  
public class ReturnStorageAction extends DispatchAction {
	
	private static Object lock = new Object();//锁对象
	private static Object AppraisalReturnedProductLock = new Object();//单个商品质检专用锁
	
	private static final String RETUPSHELFCODE = "retUpShelfCode";
	private final Log logger = LogFactory.getLog("stock.Log");
	
	private String date = DateUtil.formatDate(new Date());
	
	public ActionForward storagePackage(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	ReturnedPackageFBean formBean = (ReturnedPackageFBean) form;
	ReturnedPackageService retPService = null;
	response.setCharacterEncoding("utf-8");
	PrintWriter pw = response.getWriter(); 
	voUser user = (voUser) request.getSession().getAttribute("userView");
	if(user == null){
		pw.write("用户没有登录！");
		return null;
	}
	UserGroupBean group = user.getGroup();
	if(!group.isFlag(613)){
		pw.write("您没有权限进行此操作！");
		return null;
	}	
	int wareArea = formBean.getWareArea();
	if( !CargoDeptAreaService.hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN) ) {
		pw.write("您没有操作该地区退货库的权限！");
        return null;
	}
	try{
		if(formBean.getType() == null || formBean.getType().equals("")){
			pw.write("type cant be null");
		}
		retPService = new ReturnedPackageServiceImpl();
		pw.write(retPService.storagePackage(formBean.getType(), 
				formBean.getOrderCode(), formBean.getProductBarCode(), 
				formBean.getPackageCode(), formBean.getExceptionPCode(), 
				formBean.getPayFlag(),
				(voUser)request.getSession().getAttribute(IConstants.USER_VIEW_KEY),formBean.getWareArea()));
		CookieUtil cu = new CookieUtil(request, response);
		cu.setCookie("RETURN_REGULAR_AREA_MARK_" + user.getId(), ""+wareArea, 60*60*24*30);
		return null;
	}catch(Exception e){
		e.printStackTrace();
		if(e.getMessage().equals("java.lang.RuntimeException: unexistProductStock")){
			pw.write("退货库库存信息异常！");
		}else{
			pw.write("系统异常，请联系管理员");
		}
		return null;
	}finally{
		if(pw != null){
			pw.close();
		}
	}
}
	/**
	 * 退货原因统计主页
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward returnsReasonStatistic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(672) ) {
			request.setAttribute("tip", "您没有销售退货原因统计查看权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		try {
			String sql = "select id,reason from returns_reason";
			List list = new ArrayList();
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				ReturnsReasonBean bean = new ReturnsReasonBean();
				bean.setId(rs.getInt(1));
				bean.setReason(rs.getString(2));
				list.add(bean);
			}
			if(rs != null){
				rs.close();
			}
			request.setAttribute("list", list);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		if("ajax".equals(flag)){
			return mapping.findForward("selection");
		}else if("detail".equals(flag)){
			return mapping.findForward("returnsReasonStatisticDetailed");
		}else{
			return mapping.findForward("returnsReasonStatistic");
		}
	}
	/**
	 * 退货原因统计子页
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward returnsReasonStatisticChild(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(672) ) {
			request.setAttribute("tip", "您没有销售退货原因统计查看权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		try {
			List reasonList = new ArrayList();
			int x = 0;
			int y = 0;
			String sql_y = "select id,reason from returns_reason";
			ResultSet rs = service.getDbOp().executeQuery(sql_y);
			while (rs.next()) {
				ReturnsReasonBean bean = new ReturnsReasonBean();
				bean.setId(rs.getInt(1));
				bean.setReason(rs.getString(2));
				reasonList.add(bean);
			}
			if(rs != null){
				rs.close();
			}
			y = reasonList.size();
			if("day".equals(flag)){
				String endTime = DateUtil.formatDate(new Date());
				String startTime = endTime.substring(0,8)+"01";
				String sql_x = "SELECT  count( distinct left(storage_time,10)) FROM returned_package where left(storage_time,10)  between '" + startTime + "' and '" + endTime + "'";
				rs = service.getDbOp().executeQuery(sql_x);
				while (rs.next()) {
					x = rs.getInt(1);
				}
				if(rs != null){
					rs.close();
				}
				String statistics[][]=new String[x+1][y+2];
				statistics[0][0]= "日期";
				statistics[0][y+1]= "总退货量";
				for(int m=0;m<y;m++){
					statistics[0][m+1] = ((ReturnsReasonBean)reasonList.get(m)).getReason();
				}
				String sql = "SELECT  left(storage_time,10),count(order_id),rr.reason  FROM returned_package rp left join returns_reason rr on rp.reason_id=rr.id where left(storage_time,10) between  '"+ startTime + "' and '" + endTime + "' group by  left(storage_time,10),rr.reason";
				
				rs = service.getDbOp().executeQuery(sql);
				int n = 1;
				while (rs.next()) {
					if(rs.getString(1).equals(statistics[n-1][0])){
						n--;
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(rs.getString(3).equals(statistics[0][j])){
									statistics[n][j]= rs.getString(2).toString();
									if(statistics[n][y+1]!=null){
										statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(statistics[n][j])) + "";
									}else{
										statistics[n][y+1] = (0 + Integer.parseInt(statistics[n][j])) + "";
									}
									break;
								}
							}
						}else{
							statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(rs.getString(2))) +"";
						}
					}else{
						statistics[n][0]= rs.getString(1);
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(statistics[0][j].equals(rs.getString(3))){
									statistics[n][j]= rs.getString(2).toString();
									statistics[n][y+1] = statistics[n][j];
									break;
								}
							}
						}else{
							statistics[n][y+1] = rs.getString(2).toString();
						}
					}
					n++;
				}
				if(rs != null){
					rs.close();
				}
				request.setAttribute("statistics", statistics);
			}else if("moon".equals(flag)){
				String endTime = DateUtil.formatDate(new Date());
				endTime = endTime.substring(0,7);
				String startTime = endTime.substring(0,5)+"01";
				String sql_x = "SELECT  count( distinct left(storage_time,7)) FROM returned_package where left(storage_time,7) between '" + startTime + "' and '" + endTime + "'";
				rs = service.getDbOp().executeQuery(sql_x);
				while (rs.next()) {
					x = rs.getInt(1);
				}
				if(rs != null){
					rs.close();
				}
				String statistics[][]=new String[x+1][y+2];
				statistics[0][0]= "日期";
				statistics[0][y+1]= "总退货量";
				for(int m=0;m<y;m++){
					statistics[0][m+1] = ((ReturnsReasonBean)reasonList.get(m)).getReason();
				}
				String sql = "SELECT  left(storage_time,7),count(order_id),rr.reason  FROM returned_package rp left join returns_reason rr on rp.reason_id=rr.id where left(storage_time,7) between '"+ startTime + "' and '" + endTime + "' group by  left(storage_time,7),rr.reason";
				rs = service.getDbOp().executeQuery(sql);
				int n = 1;
				while (rs.next()) {
					if(rs.getString(1).equals(statistics[n-1][0])){
						n--;
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(rs.getString(3).equals(statistics[0][j])){
									statistics[n][j]= rs.getString(2).toString();
									if(statistics[n][y+1]!=null){
										statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(statistics[n][j])) + "";
									}else{
										statistics[n][y+1] = (0 + Integer.parseInt(statistics[n][j])) + "";
									}
									break;
								}
							}
						}else{
							statistics[n][y+1] = (Integer.parseInt(statistics[n][y+1]) + Integer.parseInt(rs.getString(2))) +"";
						}
					}else{
						statistics[n][0]= rs.getString(1);
						if(rs.getString(3) != null && !"".equals(rs.getString(3))){
							for(int j=1;j<y+1;j++){
								if(statistics[0][j].equals(rs.getString(3))){
									statistics[n][j]= rs.getString(2).toString();
									statistics[n][y+1] = statistics[n][j];
									break;
								}
							}
						}else{
							statistics[n][y+1] = rs.getString(2).toString();
						}
					}
					n++;
				}
				if(rs != null){
					rs.close();
				}
				request.setAttribute("statistics", statistics);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("returnsReasonStatisticChild");
	}
	/**
	 * 退货原因统计查询页
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward returnsReasonStatisticDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(672) ) {
			request.setAttribute("tip", "您没有销售退货原因统计查看权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		String packageCode = StringUtil.convertNull(request.getParameter("packageCode"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String reasonId = StringUtil.convertNull(request.getParameter("reasonId"));
		try{
			if("query".equals(flag)){
				StringBuilder url = new StringBuilder();
				url.append("returnStorageAction.do?method=returnsReasonStatisticDetail&flag=query");
				StringBuffer totalSql = new StringBuffer();
				StringBuffer sql = new StringBuffer();
				totalSql.append(" select count(distinct order_code) from returned_package rp left join returns_reason rr on rp.reason_id=rr.id where rp.id>0 ");
				sql.append(" select order_code,package_code,deliver,storage_time,storage_status,rr.reason from returned_package rp left join returns_reason rr on rp.reason_id=rr.id where rp.id>0 ");
				if(!"".equals(orderCode)){
					sql.append(" and order_code='" + StringUtil.toSql(orderCode) + "'");
					totalSql.append(" and order_code='" + StringUtil.toSql(orderCode) + "'");
					url.append("&orderCode=" + orderCode);
				}
				if(!"".equals(packageCode)){
					sql.append(" and package_code='" + StringUtil.toSql(packageCode) + "'");
					totalSql.append(" and package_code='" + StringUtil.toSql(packageCode) + "'");
					url.append("&packageCode=" + packageCode);
				}
				if(!"".equals(startTime) && !"".equals(endTime)){
					sql.append(" and left(storage_time,10) between '" + StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
					totalSql.append(" and left(storage_time,10) between '" + StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
					url.append("&startTime=" + startTime + "&endTime=" + endTime);
				}
				if(!"".equals(reasonId)){
					sql.append(" and rr.id='" + StringUtil.toSql(reasonId) + "'");
					totalSql.append(" and rr.id='" + StringUtil.toSql(reasonId) + "'");
					url.append("&reasonId=" + reasonId);
				}
				sql.append(" order by left(storage_time,10)");
				int totalCount = 0;
				ResultSet rs = service.getDbOp().executeQuery(totalSql.toString());
				if(rs.next()){
					totalCount = rs.getInt(1);
				}
				int countPerPage = 12;
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				sql.append(" limit "+ paging.getCurrentPageIndex() * countPerPage +"," +countPerPage);
				List rpList = new ArrayList();
				rs = service.getDbOp().executeQuery(sql.toString());
				while (rs.next()){
					ReturnedPackageBean rpBean = new ReturnedPackageBean();
					rpBean.setOrderCode(rs.getString(1));
					rpBean.setPackageCode(rs.getString(2));
					rpBean.setDeliver(rs.getInt(3));
					rpBean.setStorageTime(rs.getString(4));
					rpBean.setStorageStatus(rs.getInt(5));
					rpBean.setReasonName(rs.getString(6));
					rpList.add(rpBean);
				}
				if(rs != null){
					rs.close();
				}
				String sql_r = "select id,reason from returns_reason";
				List list = new ArrayList();
				rs = service.getDbOp().executeQuery(sql_r);
				while (rs.next()) {
					ReturnsReasonBean bean = new ReturnsReasonBean();
					bean.setId(rs.getInt(1));
					bean.setReason(rs.getString(2));
					list.add(bean);
				}
				if(rs != null){
					rs.close();
				}
				request.setAttribute("list", list);
				request.setAttribute("rpList", rpList);
				request.setAttribute("orderCode", orderCode);
				request.setAttribute("packageCode", packageCode);
				request.setAttribute("startTime", startTime);
				request.setAttribute("endTime", endTime);
				request.setAttribute("reasonId", reasonId);
				request.setAttribute("url", url.toString());
				request.setAttribute("paging", paging);
				paging.setPrefixUrl(url.toString());
				return mapping.findForward("returnsReasonStatisticDetailed");
			}else{
				StringBuffer sql = new StringBuffer();
				sql.append(" select order_code,package_code,deliver,storage_time,storage_status,rr.reason from returned_package rp left join returns_reason rr on rp.reason_id=rr.id where rp.id>0 ");
				if(!"".equals(orderCode)){
					sql.append(" and order_code='" + StringUtil.toSql(orderCode) + "'");
				}
				if(!"".equals(packageCode)){
					sql.append(" and package_code='" + StringUtil.toSql(packageCode) + "'");
				}
				if(!"".equals(startTime)){
					sql.append(" and left(storage_time,10) between '" + StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
				}
				if(!"".equals(reasonId)){
					sql.append(" and rr.id='" + StringUtil.toSql(reasonId) + "'");
				}
				sql.append(" order by left(storage_time,10)");
				ResultSet rs = service.getDbOp().executeQuery(sql.toString());
				List rpList = new ArrayList();
				while (rs.next()){
					ReturnedPackageBean rpBean = new ReturnedPackageBean();
					rpBean.setOrderCode(rs.getString(1));
					rpBean.setPackageCode(rs.getString(2));
					rpBean.setDeliver(rs.getInt(3));
					rpBean.setStorageTime(rs.getString(4));
					rpBean.setStorageStatus(rs.getInt(5));
					rpBean.setReasonName(rs.getString(6));
					rpList.add(rpBean);
				}
				if(rs != null){
					rs.close();
				}
				request.setAttribute("rpList", rpList);
				return mapping.findForward("exportReturnsReasonStatisticDetail");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("returnsReasonStatisticDetailed");
	}
	/**
	 * 退货原因列表
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward returnsReasonList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
//		UserGroupBean group = user.getGroup();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
//		String code = StringUtil.convertNull(request.getParameter("code"));
		try {
			if("ajax".equals(flag)){
				String sql = "select id,reason from returns_reason";
				List list = new ArrayList();
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					ReturnsReasonBean bean = new ReturnsReasonBean();
					bean.setId(rs.getInt(1));
					bean.setReason(rs.getString(2));
					list.add(bean);
				}
				request.setAttribute("list", list);
				return mapping.findForward("selection");
			}else{
				String sql = "select id,code,reason from returns_reason";
				List list = new ArrayList();
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					ReturnsReasonBean bean = new ReturnsReasonBean();
					bean.setId(rs.getInt(1));
					bean.setCode(rs.getString(2));
					bean.setReason(rs.getString(3));
					list.add(bean);
				}
				request.setAttribute("list", list);
				return mapping.findForward("returnsReasonList");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("returnsReasonList");
	}
	/**
	 * 退货原因添加
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward returnsReasonAdd(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(673) ) {
			request.setAttribute("tip", "您没有销售退货原因编辑权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String reason = StringUtil.convertNull(request.getParameter("reason")).trim();
		try {
			if(reason.length()>20){
				request.setAttribute("tip", "退货原因不能超过10个汉字！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			String sql = "select id,code,reason from returns_reason order by code DESC";
			List list = new ArrayList();
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				ReturnsReasonBean bean = new ReturnsReasonBean();
				bean.setId(rs.getInt(1));
				bean.setCode(rs.getString(2));
				bean.setReason(rs.getString(3));
				list.add(bean);
			}
			String newCode = "";
			if(list.size()>0){
				String code = ((ReturnsReasonBean)list.get(0)).getCode();
				if(code != null && !"".equals(code)){
					int count = Integer.parseInt(code.substring(2,5));
					count++;
					if(count <10){
						newCode = "TH00" + count;
					}else if(count<100){
						newCode = "TH0" + count;
					}else if(count < 1000){
						newCode = "TH" + count;
					}else{
						request.setAttribute("tip", "条码不能再长了！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					for(int i=0;i<list.size();i++){
						ReturnsReasonBean bean =(ReturnsReasonBean)list.get(i);
						if(reason.equals(bean.getReason())){
							request.setAttribute("tip", "退货原因不能重复！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
				}else{
					newCode = "TH001";
				}
			}
			sql = "insert into returns_reason (code,reason) values('" + newCode + "','"+ StringUtil.toSql(reason) +"')";
			if(service.getDbOp().executeUpdate(sql)){
				return new ActionForward("/admin/returnStorageAction.do?method=returnsReasonList");
			}else{
				request.setAttribute("tip", "添加失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("returnsReasonAdd");
	}
	/**
	 * 退货原因编辑
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward returnsReasonEdit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(673) ) {
			request.setAttribute("tip", "您没有销售退货原因编辑权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String id = StringUtil.convertNull(request.getParameter("id"));
		String code = StringUtil.convertNull(request.getParameter("code")).trim();
		String reason = StringUtil.convertNull(request.getParameter("reason")).trim();
		try {
			if(!"".equals(reason) && !"".equals(id) && !"".equals(code)){
				if(code.length()>5){
					request.setAttribute("tip", "条码不能超过5位！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(reason.length()>20){
					request.setAttribute("tip", "退货原因不能超过10个汉字！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(code.matches("[TH]{2}[0-9]{3}|[th]{2}[0-9]{3}")==false){
					request.setAttribute("tip", "原因条码格式不正确！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				String sql = "select id,code,reason from returns_reason";
				List list = new ArrayList();
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					ReturnsReasonBean bean = new ReturnsReasonBean();
					bean.setId(rs.getInt(1));
					bean.setCode(rs.getString(2));
					bean.setReason(rs.getString(3));
					list.add(bean);
				}
				for(int i=0;i<list.size();i++){
					ReturnsReasonBean bean =(ReturnsReasonBean)list.get(i);
					if(Integer.parseInt(id) != bean.getId()){
						if(reason.equals(bean.getReason())){
							request.setAttribute("tip", "原因名不能重复");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(bean.getCode()!=null && (bean.getCode().equals(code))){
							request.setAttribute("tip", "原因条码不能重复！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					if(reason.equals(bean.getReason())&& bean.getCode()!=null && bean.getCode().equals(code)){
						request.setAttribute("tip", "数据未修改无需提交！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				sql = "update returns_reason set reason='"+ StringUtil.toSql(reason) +"',code='"+ code +"' where id=" + StringUtil.toInt(id);
				if(service.getDbOp().executeUpdate(sql)){
					return new ActionForward("/admin/returnStorageAction.do?method=returnsReasonList");
				}
			}else if(!"".equals(id)){
				String sql = "select id,code,reason from returns_reason where id=" + StringUtil.toInt(id);
				ResultSet rs = service.getDbOp().executeQuery(sql);
				if(rs.next()) {
					id = rs.getInt(1)+"";
					code = rs.getString(2);
					reason = rs.getString(3);
				}
				request.setAttribute("id", id);
				request.setAttribute("code", code);
				request.setAttribute("reason", reason);
				return mapping.findForward("returnsReasonEdit");
			}else{
				request.setAttribute("tip", "提交信息异常！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("returnsReasonList");
	}
	/**
	 * 退货原因删除
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward returnsReasonDel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(674) ) {
			request.setAttribute("tip", "您没有销售退货原因删除权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String id = StringUtil.convertNull(request.getParameter("id"));
		try {
			String sql = "Delete from returns_reason where id="+ StringUtil.toInt(id);
			if(service.getDbOp().executeUpdate(sql)){
				return new ActionForward("/admin/returnStorageAction.do?method=returnsReasonList");
			}else{
				request.setAttribute("tip", "删除失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("returnsReasonList");
	}
	/**
	 * 退货原因打印
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward returnsReasonPrint(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String id = StringUtil.convertNull(request.getParameter("id"));
		String code = "";
		String reason = "";
		try {
			String sql = "select id,code,reason from returns_reason where id=" + StringUtil.toInt(id);
			ResultSet rs = service.getDbOp().executeQuery(sql);
			if(rs.next()) {
				id = rs.getInt(1)+"";
				code = rs.getString(2);
				reason = rs.getString(3);
			}
			if(rs != null){
				rs.close();
			}
			request.setAttribute("id", id);
			request.setAttribute("code", code);
			request.setAttribute("reason", reason);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("returnsReasonCodePrint");
	}
	@SuppressWarnings("unchecked")
	public ActionForward getReturnExList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		StatService ss = new StatService();
		
		List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
		String availAreaIds = "100";
		int x = cdaList.size();
		for(int i = 0; i < x; i++ ) {
			availAreaIds += "," + cdaList.get(i);
		}
		try {
			List list = ss.getStockExchangeList("stock_in_type = 4 and stock_out_type = 9 and stock_in_area in("+ availAreaIds + ") and status in (3,4,5,6,8)",
					-1, -1, "create_datetime asc");
			request.setAttribute("exchangeToRecieveList", list);
			return mapping.findForward("exchangeReturnInfo");
		} catch ( Exception e ) {
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.getReturnExList exception", e);
			}
			request.setAttribute("tip", "读取未完成调拨单发生异常");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			if(ss != null){
				ss.releaseAll();
			}

		}
	}
	
	
	@SuppressWarnings("unchecked")
	public ActionForward returnExchangeCheckIn(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean stockIn = group.isFlag(83);
//		boolean stockOut = group.isFlag(82);
		boolean auditingIn = group.isFlag(81);
//		boolean auditingOut = group.isFlag(80);
		boolean returnExchangeCheck = group.isFlag(614);
		if( !returnExchangeCheck ) {
			request.setAttribute("tip", "您没有售后退货商品入库的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int confirm = 0;

		WareService wareService = new WareService();
		IProductStockService service = ServiceFactory
				.createProductStockService(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService ss = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String dbCode = StringUtil.convertNull(request
				.getParameter("exchangeCode2"));
		String productCode = StringUtil.convertNull(request
				.getParameter("productCode"));
		IStockService stockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,stockService.getDbOp());
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,stockService.getDbOp());
		String[] productCodes = null;
		List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
		String availAreaIds = "100";
		int x = cdaList.size();
		for(int i = 0; i < x; i++ ) {
			availAreaIds += "," + cdaList.get(i);
		}
		List sepList = null;
		int totalCount = 0;
		int stockExchangeId = 0;
		/*List tempProductList = new ArrayList();*/
		List scanProductList = new ArrayList();
		try {
			synchronized (Constants.LOCK) {
				List dbList = ss
						.getStockExchangeList(
								"stock_in_type = 4 and stock_out_type = 9 and stock_in_area in ("+ availAreaIds + ")  and status in (3,5,6,8)",
								-1, -1, null);
				boolean onWait = false;
				List exchangeTargetList = ss.getStockExchangeList("code = '" + dbCode + "'" , -1, -1, null);
				if( exchangeTargetList.size() == 0 ) {
					request.setAttribute("tip","调拨单不存在");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				} else {
					StockExchangeBean sebTester = (StockExchangeBean)exchangeTargetList.get(0);
					
					if( !CargoDeptAreaService.hasCargoDeptArea(request, sebTester.getStockInArea(), sebTester.getStockInType())) {
						request.setAttribute("tip","你没有对应目的地区或对应目的库的权限，不能对调拨单入库！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if( sebTester.getStatus() < 3) {
						request.setAttribute("tip","调拨单状态未到出库");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					} else if ( sebTester.getStatus() == 7 ) {
						request.setAttribute("tip","调拨单已经完成");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if( sebTester.getStockInType() != 4 ) {
						request.setAttribute("tip","调拨单不是调往退货库的");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					} 
					
					if( sebTester.getStockOutType() != 9 ) {
						request.setAttribute("tip","调拨单不是来自售后库的");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
				}
				
				for (int i = 0; i < dbList.size(); i++) {
					StockExchangeBean seb = (StockExchangeBean) dbList.get(i);
					// 调拨单 在要等待的队列中
					if (dbCode.equals(seb.getCode())) {
						stockExchangeId = seb.getId();
						String condition = "stock_exchange_id = " + seb.getId();
						sepList = service.getStockExchangeProductList(condition, 0, -1,
								"id asc");
						for (int j = 0; j < sepList.size(); j++) {
							StockExchangeProductBean sepb = (StockExchangeProductBean) sepList
									.get(j);
							totalCount += sepb.getStockOutCount();
						}
						onWait = true;
					}
				}
		
				if (onWait == true) {
					// 获得多个商品的编号的数组
					if (!productCode.equals("")) {
						try {
							productCodes = productCode.split("\r\n");
						} catch (Exception e) {
							e.printStackTrace();
							request.setAttribute("tip", "商品条码输入格式有误!");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						try {
							for (int i = 0; i < productCodes.length; i++) {
								ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCodes[i]+"'");
								if( bBean == null || bBean.getBarcode() == null ) {
									ProductCodeInfoBean tempCodeBean = ss
									.getProductExchangeByCode(productCodes[i]);
									if( tempCodeBean != null ) {
										scanProductList.add(tempCodeBean);
									}
								} else {
									scanProductList.add(ss.getProductExchangeById(bBean.getProductId()));
								}
							}
						} catch (Exception e) {
							request.setAttribute("tip", e.getMessage());
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					} else {
						request.setAttribute("tip", "未输入商品条码");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
					if( productCodes.length > scanProductList.size() ) {
						request.setAttribute("tip", "商品条码和编码中有错误的");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
		
					// 检查商品的数量是否是对的。
					if (scanProductList.size() == totalCount) {
						// 总数符合，是正常情况
						Map tempProductIdMap = new HashMap();
						for( int i = 0;  i < sepList.size(); i++ ) {
							tempProductIdMap.put(new Integer(((StockExchangeProductBean)sepList.get(i)).getProductId()), "0");
						}
						for(int i = 0; i < scanProductList.size(); i++ ) {
							ProductCodeInfoBean pcib = (ProductCodeInfoBean) scanProductList
							.get(i);
							int scanProductId = 0;
							try {
								scanProductId = pcib.getId();
							} catch (NullPointerException e) {
								request.setAttribute("tip", "商品条码不存在");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							Integer iScanProductId = new Integer(scanProductId);
							if(!tempProductIdMap.containsKey(iScanProductId)) {
								request.setAttribute("tip",
										"商品条码" + pcib.getCode() + "不在调拨单内");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
						
						
						for (int i = 0; i < sepList.size(); i++) {
							/*ReturnedProductBean eptb = new ReturnedProductBean();*/
							StockExchangeProductBean sepb = (StockExchangeProductBean) sepList
									.get(i);
							/*eptb.setProductId(sepb.getProductId());*/
							int singleProductCount = 0;
							for (int j = 0; j < scanProductList.size(); j++) {
								ProductCodeInfoBean pcib = (ProductCodeInfoBean) scanProductList
										.get(j);
								int scanProductId = 0;
								try {
									scanProductId = pcib.getId();
								} catch (NullPointerException e) {
									request.setAttribute("tip", "商品条码不存在");
									request.setAttribute("result", "failure");
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if (scanProductId == sepb.getProductId()) {
									singleProductCount += 1;
									/*eptb.setProductCode(pcib.getCode());
									eptb.setProductName(pcib.getName());*/
								}  
							}
							
							if (singleProductCount != sepb.getStockOutCount()) {
								ProductCodeInfoBean pcib2 = ss.getProductExchangeById(sepb.getProductId());
								request.setAttribute("tip",
										"商品条码" + pcib2.getCode() + "的数量不正确");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							/*eptb.setCount(sepb.getStockOutCount());*/
							/*tempProductList.add(eptb);*/
						}
						confirm = 1;
		
					} else if (scanProductList.size() > totalCount) {
						request.setAttribute("tip", "扫描的商品总数 大于调拨单内商品的数量！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					} else {
						request.setAttribute("tip", "扫描的商品总数小于调拨单内商品的数量！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
		
					
					//之下都是走调拨单的过程
					// ---------------------------------------------------
					// 从审核通过待入库 到入库未审核的过程改调拨单状态， 和商品确认状态
					
					// 确认商品入库
					StockExchangeBean bean = service.getStockExchange("id = "
							+ stockExchangeId);
		
					StringBuilder buf = new StringBuilder();
					String condition = null;
					buf.append("stock_exchange_id = ");
					buf.append(bean.getId());
					buf.append(" and status = ");
					buf.append(StockExchangeProductBean.STOCKIN_UNDEAL);
					condition = buf.toString();
					int hisStatus = StockExchangeProductBean.STOCKIN_DEALED;
					ArrayList confirmProductList = service.getStockExchangeProductList(
							condition, 0, -1, "id");
					Iterator itr = confirmProductList.iterator();
					// 开始事务
					service.getDbOp().startTransaction();//需要连接检查
					StockExchangeProductBean sep = null;
					voProduct product = null;
					String set = null;
					int count = 0;
					while (itr.hasNext()) {
						count++;
						sep = (StockExchangeProductBean) itr.next();
						product = wareService.getProduct(sep.getProductId());
						set = "status = " + hisStatus + ", confirm_datetime = now()";
						service.updateStockExchangeProduct(set, "id = " + sep.getId());
		
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品检测操作：" + bean.getName() + ": 确认商品["
								+ product.getCode() + "]入库");
						log.setType(StockAdminHistoryBean.CHANGE);
						stockService.addStockAdminHistory(log);
					}
					if (bean.getStatus() == StockExchangeBean.STATUS3
							|| bean.getStatus() == StockExchangeBean.STATUS8) {
						service.updateStockExchange("status=" + StockExchangeBean.STATUS5,
								"id=" + bean.getId());
					}
		
					// 从入库中 到 已入库未审核。。该调拨但状态
					if (!stockIn) {
						request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if (confirm == 1) {
						int undealCount = service.getStockExchangeProductCount(condition);
						if (undealCount > 0) {
							request.setAttribute("tip", "还有没确认的商品，不能完成入库操作！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						String auditSet = "status = " + StockExchangeBean.STATUS6
								+ ", confirm_datetime = now(), stock_in_oper="
								+ user.getId() + ", stock_in_oper_name='"
								+ user.getUsername() + "'";
						service.updateStockExchange(auditSet, "id = " + bean.getId());
		
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + ": 确认完成入库操作");
						log.setType(StockAdminHistoryBean.CHANGE);
						stockService.addStockAdminHistory(log);
					} else {
		
					}
					// --------------------------------------------------------------------------------------------------------
					// 从入库未审核到入库审核完成,只改一个调拨单状态
		
					DbOperation dbOp = new DbOperation();
					dbOp.init("adult_slave");
					try {
						List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
						
						if (!auditingIn) {
							request.setAttribute("tip", "你没有权限进行该操作，请与管理员联系！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						String stockInCondition = "stock_exchange_id = " + bean.getId()
								+ " and status = "
								+ StockExchangeProductBean.STOCKIN_DEALED;
		
						// 审核通过，要入库改库存
						ArrayList stockInAuditList = service.getStockExchangeProductList(
								stockInCondition, 0, -1, "id");
						Iterator itrAudit = stockInAuditList.iterator();
						StockExchangeProductBean sep2 = null;
						ProductStockBean psOut = null;
						ProductStockBean psIn = null;
						while (itrAudit.hasNext()) {
							sep2 = (StockExchangeProductBean) itrAudit.next();
							product = wareService.getProduct(sep2.getProductId());
							psIn = service.getProductStock("id=" + sep2.getStockInId());
							psOut = service.getProductStock("id=" + sep2.getStockOutId());
							// 入库
							set = "status = " + StockExchangeProductBean.STOCKIN_DEALED
									+ ", remark = '操作前库存" + psIn.getStock() + ",操作后库存"
									+ (psIn.getStock() + sep2.getStockInCount())
									+ "', confirm_datetime = now()";
							if (!service.updateStockExchangeProduct(set,
									"id = " + sep2.getId())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							// service.updateProductStock("lock_count=(lock_count - " +
							// sep.getStockOutCount() + ")", "id=" + sep.getStockOutId());
							if (!service.updateProductLockCount(sep2.getStockOutId(),
									-sep2.getStockOutCount())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "商品" + product.getCode()
										+ "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							// service.updateProductStock("stock=(stock + " +
							// sep.getStockInCount() + ")", "id=" + sep.getStockInId());
							if (!service.updateProductStockCount(sep2.getStockInId(),
									sep2.getStockInCount())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "商品" + product.getCode()
										+ "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
		
							// log记录
							StockAdminHistoryBean log = new StockAdminHistoryBean();
							log.setAdminId(user.getId());
							log.setAdminName(user.getUsername());
							log.setLogId(bean.getId());
							log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark("商品调配操作：" + bean.getName() + ": 将商品["
									+ product.getCode() + "]入库");
							log.setType(StockAdminHistoryBean.CHANGE);
							if (!stockService.addStockAdminHistory(log)) {
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
		
							// 入库货位库存
							StockExchangeProductCargoBean sepcIn = service
									.getStockExchangeProductCargo("stock_exchange_id = "
											+ bean.getId()
											+ " and stock_exchange_product_id = "
											+ sep2.getId() + " and type = 1");
							StockExchangeProductCargoBean sepcOut = service
									.getStockExchangeProductCargo("stock_exchange_id = "
											+ bean.getId()
											+ " and stock_exchange_product_id = "
											+ sep2.getId() + " and type = 0");
							if (!cargoService
									.updateCargoProductStockCount(
											sepcIn.getCargoProductStockId(),
											sepcIn.getStockCount())) {
								request.setAttribute("tip", "商品" + product.getCode()
										+ "货位库存操作失败，货位锁定库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!cargoService.updateCargoProductStockLockCount(
									sepcOut.getCargoProductStockId(),
									-sepcOut.getStockCount())) {
								request.setAttribute("tip", "商品" + product.getCode()
										+ "货位库存操作失败，货位锁定库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
		
							// 更新调拨单未上架数量
							if (!service.updateStockExchangeProduct("no_up_cargo_count = "
									+ sep2.getStockOutCount(), "id = " + sep2.getId())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							
							//组装财务接口需要的数据
							BaseProductInfo baseProductInfo = new BaseProductInfo();
							baseProductInfo.setId(sep2.getProductId());
							baseProductInfo.setProductStockOutId(psOut.getId());
							baseProductInfo.setProductStockId(psIn.getId());
							baseProductInfo.setOutCount(sep2.getStockOutCount());
							baseList.add(baseProductInfo);
		
							// 更新批次记录、添加调拨出、入库批次记录
							/**
							List sbList = stockService.getStockBatchList(
									"product_id=" + psOut.getProductId()
											+ " and stock_type=" + psOut.getType()
											+ " and stock_area=" + psOut.getArea(), -1, -1,
									"id asc");
							double stockinPrice = 0;
							double stockoutPrice = 0;
							if (sbList != null && sbList.size() != 0) {
								int stockExchangeCount = sep2.getStockOutCount();
								int index = 0;
								int stockBatchCount = 0;
		
								do {
									// 出库
									StockBatchBean batch = (StockBatchBean) sbList
											.get(index);
									if (stockExchangeCount >= batch.getBatchCount()) {
										if (!stockService.deleteStockBatch("id="
												+ batch.getId())) {
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库操作失败！");
											request.setAttribute("result", "failure");
											return mapping
													.findForward(IConstants.FAILURE_KEY);
										}
										stockBatchCount = batch.getBatchCount();
									} else {
										if (!stockService.updateStockBatch(
												"batch_count = batch_count-"
														+ stockExchangeCount,
												"id=" + batch.getId())) {
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库操作失败！");
											request.setAttribute("result", "failure");
											return mapping
													.findForward(IConstants.FAILURE_KEY);
										}
										stockBatchCount = stockExchangeCount;
									}
		
									// 添加批次操作记录
									StockBatchLogBean batchLog = new StockBatchLogBean();
									batchLog.setCode(bean.getCode());
									batchLog.setStockType(batch.getStockType());
									batchLog.setStockArea(batch.getStockArea());
									batchLog.setBatchCode(batch.getCode());
									batchLog.setBatchCount(stockBatchCount);
									batchLog.setBatchPrice(batch.getPrice());
									batchLog.setProductId(batch.getProductId());
									batchLog.setRemark("调拨出库");
									batchLog.setCreateDatetime(DateUtil.getNow());
									batchLog.setUserId(user.getId());
									batchLog.setSupplierId(batch.getSupplierId());
									batchLog.setTax(batch.getTax());
									if (!stockService.addStockBatchLog(batchLog)) {
										request.setAttribute("tip", "添加失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
		
									stockoutPrice = stockoutPrice
											+ batchLog.getBatchCount()
											* batchLog.getBatchPrice();
		
									// 入库
									StockBatchBean batchBean = stockService
											.getStockBatch("code='" + batch.getCode()
													+ "' and product_id="
													+ batch.getProductId()
													+ " and stock_type=" + psIn.getType()
													+ " and stock_area=" + psIn.getArea());
									if (batchBean != null) {
										if (!stockService.updateStockBatch(
												"batch_count = batch_count+"
														+ stockBatchCount, "id="
														+ batchBean.getId())) {
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库操作失败！");
											request.setAttribute("result", "failure");
											return mapping
													.findForward(IConstants.FAILURE_KEY);
										}
									} else {
										StockBatchBean newBatch = new StockBatchBean();
										newBatch.setCode(batch.getCode());
										newBatch.setProductId(batch.getProductId());
										newBatch.setPrice(batch.getPrice());
										newBatch.setBatchCount(stockBatchCount);
										newBatch.setProductStockId(psIn.getId());
										newBatch.setStockArea(bean.getStockInArea());
										newBatch.setStockType(psIn.getType());
										newBatch.setSupplierId(batch.getSupplierId());
										newBatch.setTax(batch.getTax());
										newBatch.setNotaxPrice(batch.getNotaxPrice());
										newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
										if (!stockService.addStockBatch(newBatch)) {
											request.setAttribute("tip", "添加失败！");
											request.setAttribute("result", "failure");
											service.getDbOp().rollbackTransaction();
											return mapping
													.findForward(IConstants.FAILURE_KEY);
										}
									}
		
									// 添加批次操作记录
									batchLog = new StockBatchLogBean();
									batchLog.setCode(bean.getCode());
									batchLog.setStockType(psIn.getType());
									batchLog.setStockArea(bean.getStockInArea());
									batchLog.setBatchCode(batch.getCode());
									batchLog.setBatchCount(stockBatchCount);
									batchLog.setBatchPrice(batch.getPrice());
									batchLog.setProductId(batch.getProductId());
									batchLog.setRemark("调拨入库");
									batchLog.setCreateDatetime(DateUtil.getNow());
									batchLog.setUserId(user.getId());
									batchLog.setSupplierId(batch.getSupplierId());
									batchLog.setTax(batch.getTax());
									if (!stockService.addStockBatchLog(batchLog)) {
										request.setAttribute("tip", "添加失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
		
									stockExchangeCount -= batch.getBatchCount();
									index++;
		
									stockinPrice = stockinPrice + batchLog.getBatchCount()
											* batchLog.getBatchPrice();
								} while (stockExchangeCount > 0 && index < sbList.size());
							}
							*/
		
							// 审核通过，就加 进销存卡片
							product.setPsList(service.getProductStockList("product_id="
									+ sep2.getProductId(), -1, -1, null));
							CargoProductStockBean cpsIn = cargoService
									.getCargoAndProductStock("cps.id = "
											+ sepcIn.getCargoProductStockId());
							CargoProductStockBean cpsOut = cargoService
									.getCargoAndProductStock("cps.id = "
											+ sepcOut.getCargoProductStockId());
		
							// 出库卡片
							StockCardBean sc = new StockCardBean();
							sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
							sc.setCode(bean.getCode());
							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getStockOutType());
							sc.setStockArea(bean.getStockOutArea());
							sc.setProductId(sep2.getProductId());
							sc.setStockId(sep2.getStockOutId());
							sc.setStockOutCount(sep2.getStockOutCount());
							// sc.setStockOutPriceSum(stockoutPrice);
							sc.setStockOutPriceSum((new BigDecimal(sep2.getStockOutCount()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(product
													.getPrice5()))).doubleValue());
							sc.setCurrentStock(product.getStock(bean.getStockOutArea(),
									bean.getStockOutType())
									+ product.getLockCount(bean.getStockOutArea(),
											bean.getStockOutType()));
							sc.setStockAllArea(product.getStock(bean.getStockOutArea())
									+ product.getLockCount(bean.getStockOutArea()));
							sc.setStockAllType(product.getStockAllType(bean
									.getStockOutType())
									+ product.getLockCountAllType(bean.getStockOutType()));
							sc.setAllStock(product.getStockAll()
									+ product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(sc
													.getStockPrice()))).doubleValue());
							if (!service.addStockCard(sc)) {
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
		
							// 货位出库卡片
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
							csc.setCode(bean.getCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(bean.getStockOutType());
							csc.setStockArea(bean.getStockOutArea());
							csc.setProductId(sep2.getProductId());
							csc.setStockId(cpsOut.getId());
							csc.setStockOutCount(sep2.getStockOutCount());
							csc.setStockOutPriceSum((new BigDecimal(sep2.getStockOutCount()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(product
													.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(sc.getStockArea(),
									sc.getStockType())
									+ product.getLockCount(sc.getStockArea(),
											sc.getStockType()));
							csc.setAllStock(product.getStockAll()
									+ product.getLockCountAll());
							csc.setCurrentCargoStock(cpsOut.getStockCount()
									+ cpsOut.getStockLockCount());
							csc.setCargoStoreType(cpsOut.getCargoInfo().getStoreType());
							csc.setCargoWholeCode(cpsOut.getCargoInfo().getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(sc
													.getStockPrice()))).doubleValue());
							if (!cargoService.addCargoStockCard(csc)) {
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
		
							// 入库卡片
							sc = new StockCardBean();
							sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
							sc.setCode(bean.getCode());
							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getStockInType());
							sc.setStockArea(bean.getStockInArea());
							sc.setProductId(sep2.getProductId());
							sc.setStockId(sep2.getStockInId());
							sc.setStockInCount(sep2.getStockInCount());
							// sc.setStockInPriceSum(stockinPrice);
							sc.setStockInPriceSum((new BigDecimal(sep2.getStockInCount()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(product
													.getPrice5()))).doubleValue());
							sc.setCurrentStock(product.getStock(bean.getStockInArea(),
									bean.getStockInType())
									+ product.getLockCount(bean.getStockInArea(),
											bean.getStockInType()));
							sc.setStockAllArea(product.getStock(bean.getStockInArea())
									+ product.getLockCount(bean.getStockInArea()));
							sc.setStockAllType(product.getStockAllType(bean
									.getStockInType())
									+ product.getLockCountAllType(bean.getStockInType()));
							sc.setAllStock(product.getStockAll()
									+ product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(sc
													.getStockPrice()))).doubleValue());
							if (!service.addStockCard(sc)) {
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
		
							// 货位入库卡片
							csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
							csc.setCode(bean.getCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(bean.getStockInType());
							csc.setStockArea(bean.getStockInArea());
							csc.setProductId(sep2.getProductId());
							csc.setStockId(cpsIn.getId());
							csc.setStockInCount(sep2.getStockInCount());
							csc.setStockInPriceSum((new BigDecimal(sep2.getStockInCount()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(product
													.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(sc.getStockArea(),
									sc.getStockType())
									+ product.getLockCount(sc.getStockArea(),
											sc.getStockType()));
							csc.setAllStock(product.getStockAll()
									+ product.getLockCountAll());
							csc.setCurrentCargoStock(cpsIn.getStockCount()
									+ cpsIn.getStockLockCount());
							csc.setCargoStoreType(cpsIn.getCargoInfo().getStoreType());
							csc.setCargoWholeCode(cpsIn.getCargoInfo().getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
									.multiply(
											new BigDecimal(StringUtil.formatDouble2(sc
													.getStockPrice()))).doubleValue());
							if (!cargoService.addCargoStockCard(csc)) {
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
						
						//调用财务接口
						if(!baseList.isEmpty()){
							FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
							baseService.acquireFinanceBaseData(baseList, bean.getCode(), user.getId(), 0, 0);
						}
						
						if (!service.updateStockExchange(
								"status=" + StockExchangeBean.STATUS7
										+ ", auditing_user_id2=" + user.getId()
										+ ", auditing_user_name2='" + user.getUsername()
										+ "'", "id=" + bean.getId())) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} /*else {
							// 在这个时候把商品放倒退货库商品列表中
							// 对退货库的数量在 uncheck_count 加上对应的量
								for (int i = 0; i < tempProductList.size(); i++) {
									ReturnedProductBean rpb = (ReturnedProductBean) tempProductList
											.get(i);
									if (!ss.dealReturnedProduct(rpb)) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "更改退货商品列表时数据库操作失败！");
										request.setAttribute("result", "failure");
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
						}*/
						// log记录
						StockAdminHistoryBean log = new StockAdminHistoryBean();
						log.setAdminId(user.getId());
						log.setAdminName(user.getUsername());
						log.setLogId(bean.getId());
						log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark("商品调配操作：" + bean.getName() + "：入库审核通过");
						log.setType(StockAdminHistoryBean.CHANGE);
						if (!stockService.addStockAdminHistory(log)) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "数据库操作失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						
						int assepCount = afStockService.getAfterSaleStockExchangeProductCount("stock_exchange_id=" + bean.getId());
						if (assepCount > 0 ) {
							if (!wareService.executeUpdate("update after_sale_detect_product asdp,after_sale_stock_exchange_product assep,cargo_info ci set asdp.cargo_whole_code=ci.whole_code ,asdp.lock_status= "+ AfterSaleDetectProductBean.LOCK_STATUS0 + " where asdp.id=assep.after_sale_detect_product_id and assep.in_cargo_id=ci.id and assep.stock_exchange_id=" + bean.getId())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "更新处理单锁定状态失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
		
						// 提交事务
						service.getDbOp().commitTransaction();
						service.getDbOp().getConn().setAutoCommit(true);
					
					} catch (Exception e) {
						if(logger.isErrorEnabled()){
							logger.error("method:ReturnStorageAction.returnExchangeCheckIn exception", e);
						}
						e.printStackTrace();
						service.getDbOp().rollbackTransaction();
					} finally {
						dbOp.release();
					}
		
					request.setAttribute("result", "success");
					return mapping.findForward("exchangeReturnSuccess");
				} else {
					request.setAttribute("tip",
							"该调拨单不在待收调拨单列表中!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
		}finally {
			service.releaseAll();
		}
	
	}
	
	//退货库商品列表
	@SuppressWarnings("unchecked")
	public ActionForward getReturnedProductList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		String strWareArea = request.getParameter("wareArea");
		
		List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
		String availAreaIds = "100";
		int x = cdaList.size();
		for(int i = 0; i < x; i++ ) {
			availAreaIds += "," + cdaList.get(i);
		}
		
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		
		String wareAreaLable = ProductWarePropertyService.getWeraAreaOptions(request, wareArea);
		request.setAttribute("wareAreaLable", wareAreaLable);
		
		StringBuilder params = new StringBuilder();
		StringBuilder condition = new StringBuilder();
		StringBuilder searchCondition = new StringBuilder();
		condition.append("(stock > 0 or lock_count > 0) and type = " + ProductStockBean.STOCKTYPE_RETURN );
		boolean searched = false;
		boolean hasResult = false;
		if( productCode != null && !productCode.equals("")) {
			params.append("&");
			params.append("productCode=" + productCode);
			
			searchCondition.append("code = '" + StringUtil.toSql(productCode) + "'");
		} 
		
		if( productName != null && !productName.equals("")) {
			params.append("&");
			params.append("productName=" + productName);
			
			if( searchCondition.length() > 0 ) {
				searchCondition.append(" and name like '" + StringUtil.toSql(productName) + "%" + "'" );
			} else {
				searchCondition.append("name like '" + StringUtil.toSql(productName) + "%" + "'" );
			}
		}
		
		if(strWareArea == null && cdaList != null && !cdaList.isEmpty()){
			condition.append(" and area in (" + availAreaIds + ")");
		}else if(strWareArea != null && wareArea == -1){
			return mapping.findForward("returnedProductTable");
		}else if(wareArea != -1){
			condition.append(" and area = " + wareArea);
			params.append("&");
			params.append("wareArea=" + wareArea);
		}
		
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		try {
			String ids = "";
			if( searchCondition.length() > 0 ) {
				String searchedString = searchCondition.toString();
				searched = true;
				ids = statService.getProductIds(searchedString);
				if( ids.length() > 0 ) {
					hasResult = true;
					ids = ids.substring(0, ids.length() - 1 );
				}
			}
			
			if( searched && hasResult ) {
				condition.append(" and product_id in (" + ids + ")");
			}
			
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
			String condition1 = null;
			if(!condition.toString().equals("")){
				condition1 = condition.toString();
			}
			List returnedProductList = new ArrayList();
			int totalCount = 0;
			if ( searched && !hasResult ) {
				totalCount = 0;
			} else {
				totalCount = service.getProductStockCount(condition1);
			}
	        PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
	        paging.setCurrentPageIndex(pageIndex);
	        paging.setPrefixUrl("returnStorageAction.do?method=getReturnedProductList" + params.toString());
	        if(searched && !hasResult ) {
	        	returnedProductList = new ArrayList();
	        } else {
	        	returnedProductList = service.getProductStockList(condition1,paging.getCurrentPageIndex() * countPerPage,countPerPage,"id asc");
	        	if( returnedProductList == null ) {
	        		returnedProductList = new ArrayList();
	        	}
	        }
			
			for( int i = 0; i < returnedProductList.size(); i ++ ) {
				ProductStockBean psb = (ProductStockBean) returnedProductList.get(i);
				voProduct product = wareService.getProduct( psb.getProductId() );
				if( product!= null ) {
					psb.setProduct(product);
				} else {
					product = new voProduct();
					product.setName("");
					product.setCode("");
					psb.setProduct(product);
				}
			}
			
			//添加冻结量部分
			/*for( int i = 0; i < returnedProductList.size(); i ++ ) {
				ReturnedProductBean rpb = (ReturnedProductBean) returnedProductList.get(i);
				if( rpb.getType() == 1 ) {
					ProductStockBean psb = service.getProductStock("product_id = " + rpb.getProductId() + " and area = " + ProductStockBean.AREA_ZC + " and type = " + ProductStockBean.STOCKTYPE_RETURN );
					//考虑下架可能找不到锁定库存
					if( psb != null ) {
						rpb.setLockCount(psb.getLockCount());
					}
				} 
			}*/
			
			 //根据条件得到 总数量
			
			//paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-(totalCount%countPerPage))/countPerPage)+1);
			request.setAttribute("paging", paging);
			request.setAttribute("returnedProductList", returnedProductList);
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.getReturnedProductList exception", e);
			}
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("returnedProductTable");
	}
	
	//退货商品列表找不到对应合格库散件区货位号的商品列表
	@SuppressWarnings("unchecked")
	public ActionForward getReturnedProductNoCargoMatched(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		
		
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		StringBuilder params = new StringBuilder();
		StringBuilder condition = new StringBuilder();
		condition.append("(stock > 0 or lock_count > 0) and type = " + ProductStockBean.STOCKTYPE_RETURN );
		StringBuilder searchCondition = new StringBuilder();
		boolean searched = false;
		boolean hasResult = false;
		if( productCode != null && !productCode.equals("")) {
			params.append("&");
			params.append("productCode=" + productCode);
			
			searchCondition.append("code = '" + StringUtil.toSql(productCode) + "'");
		} 
		
		if( productName != null && !productName.equals("")) {
			params.append("&");
			params.append("productName=" + productName);
			
			if( searchCondition.length() > 0 ) {
				searchCondition.append(" and name like '" + StringUtil.toSql(productName) + "%" + "'" );
			} else {
				searchCondition.append("name like '" + StringUtil.toSql(productName) + "%" + "'" );
			}
		}
		
		
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			String availCargoAreaIds = "0";
			int x = cdaList.size();
			for(int i = 0; i < x; i++ ) {
				availAreaIds += "," + cdaList.get(i);
				CargoInfoAreaBean ciaBean = cargoService.getCargoInfoArea("old_id=" + cdaList.get(i));
				if( ciaBean != null ) {
					availCargoAreaIds += "," + ciaBean.getId();
				}
			}
			int wareCargoArea = 0;
			CargoInfoAreaBean wareCargoAreaB = cargoService.getCargoInfoArea("old_id=" + wareArea);
			if( wareCargoAreaB != null ) {
				wareCargoArea = wareCargoAreaB.getId();
			}
			if( wareArea == -1 ) {
				condition.append(" and area in (" + availAreaIds + ")");
			} else {
				condition.append(" and area = " + wareArea);
			}
			params.append("&");
			params.append("wareArea=" + wareArea);
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
	        
			String condition1 = null;
			String canIds = statService.getReturnedProductNoOffLine(availAreaIds, wareArea);
			String noIds = statService.getReturnedProductNoCargoMatchedIds(availAreaIds, availCargoAreaIds,wareArea, wareCargoArea);
			if( canIds != null && canIds.length() > 0 ) {
				canIds = canIds.substring(0, (canIds.length() - 1));
				condition.append(" and product_id in (" + canIds + ")");
				if( noIds != null && noIds.length() > 0 ) {
					noIds = noIds.substring(0,(noIds.length() - 1));
					condition.append(" and product_id not in (" + noIds + ")");
				}
			}
			String ids = "";
			if( searchCondition.length() > 0 ) {
				String searchedString = searchCondition.toString();
				searched = true;
				ids = statService.getProductIds(searchedString);
				if( ids.length() > 0 ) {
					hasResult = true;
					ids = ids.substring(0, ids.length() - 1 );
				}
			}
			
			if( searched && hasResult ) {
				condition.append(" and product_id in (" + ids + ")");
			}
			if(!condition.toString().equals("")){
				condition1 = condition.toString();
			}
			List returnedProductList = new ArrayList();
			int totalCount = 0;
			PagingBean paging = null;
			if ( searched && !hasResult ) {
				totalCount = 0;
				paging = new PagingBean(pageIndex, totalCount,countPerPage);
			} else {
				totalCount = service.getProductStockCount(condition1);
				paging = new PagingBean(pageIndex, totalCount,countPerPage);
				returnedProductList = service.getProductStockList(condition1,paging.getCurrentPageIndex() * countPerPage,countPerPage,"id asc");
				if( returnedProductList == null ) {
					returnedProductList = new ArrayList();
				}
			}
	        paging.setPrefixUrl("returnStorageAction.do?method=getReturnedProductNoCargoMatched" + params.toString());
			
			for( int i = 0; i < returnedProductList.size(); i ++ ) {
				ProductStockBean psb = (ProductStockBean) returnedProductList.get(i);
				voProduct product = wareService.getProduct( psb.getProductId() );
				if( product!= null ) {
					psb.setProduct(product);
				} else {
					product = new voProduct();
					product.setName("");
					product.setCode("");
					psb.setProduct(product);
				}
			}
			//添加冻结量部分
			/*for( int i = 0; i < returnedProductList.size(); i ++ ) {
				ReturnedProductBean rpb = (ReturnedProductBean) returnedProductList.get(i);
				if( rpb.getType() == 1 ) {
					ProductStockBean psb = service.getProductStock("product_id = " + rpb.getProductId() + " and area = " + ProductStockBean.AREA_ZC + " and type = " + ProductStockBean.STOCKTYPE_RETURN );
					//考虑下架可能找不到锁定库存
					if( psb != null ) {
						rpb.setLockCount(psb.getLockCount());
					}
				} 
			}*/
			//paging.setTotalCount(totalCount);
			//paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-(totalCount%countPerPage))/countPerPage)+1);
			request.setAttribute("paging", paging);
			request.setAttribute("returnedNoCargoList", returnedProductList);
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.getReturnedProductNoCargoMatched exception", e);
			}
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("returnedProductNoCargoMatched");
	}
	
	//导出商品列表找不到对应合格库散件区货位号的商品列表
	@SuppressWarnings("unchecked")
	public void exportReturnedProductNoCargoMatched(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return ;
		}
		
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		StringBuilder params = new StringBuilder();
		StringBuilder condition = new StringBuilder();
		condition.append("(stock > 0 or lock_count > 0) and type = " + ProductStockBean.STOCKTYPE_RETURN );
		StringBuilder searchCondition = new StringBuilder();
		boolean searched = false;
		boolean hasResult = false;
		if( productCode != null && !productCode.equals("")) {
			params.append("&");
			params.append("productCode=" + productCode);
			
			searchCondition.append("code = '" + StringUtil.toSql(productCode) + "'");
		} 
		
		if( productName != null && !productName.equals("")) {
			params.append("&");
			params.append("productName=" + productName);
			
			if( searchCondition.length() > 0 ) {
				searchCondition.append(" and name like '" + StringUtil.toSql(productName) + "%" + "'" );
			} else {
				searchCondition.append("name like '" + StringUtil.toSql(productName) + "%" + "'" );
			}
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			
			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			String availCargoAreaIds = "0";
			int x = cdaList.size();
			for(int i = 0; i < x; i++ ) {
				availAreaIds += "," + cdaList.get(i);
				CargoInfoAreaBean ciaBean = cargoService.getCargoInfoArea("old_id=" + cdaList.get(i));
				if( ciaBean != null ) {
					availCargoAreaIds += "," + ciaBean.getId();
				}
			}
			int wareCargoArea = 0;
			CargoInfoAreaBean wareCargoAreaB = cargoService.getCargoInfoArea("old_id=" + wareArea);
			if( wareCargoAreaB != null ) {
				wareCargoArea = wareCargoAreaB.getId();
			}
			
	        List returnedProductList = new ArrayList();
			String condition1 = null;
			String canIds = statService.getReturnedProductNoOffLine(availAreaIds, wareArea);
			String noIds = statService.getReturnedProductNoCargoMatchedIds(availAreaIds, availCargoAreaIds,wareArea, wareCargoArea);
			if( canIds != null && canIds.length() > 0 ) {
				canIds = canIds.substring(0, (canIds.length() - 1));
				condition.append(" and product_id in (" + canIds + ")");
				if( noIds != null && noIds.length() > 0 ) {
					noIds = noIds.substring(0,(noIds.length() - 1));
					condition.append(" and product_id not in (" + noIds + ")");
				}
			}
			String ids = "";
			if( searchCondition.length() > 0 ) {
				String searchedString = searchCondition.toString();
				searched = true;
				ids = statService.getProductIds(searchedString);
				if( ids.length() > 0 ) {
					hasResult = true;
					ids = ids.substring(0, ids.length() - 1 );
				}
			}
			if( wareArea == -1 ) {
				condition.append(" and area in (" + availAreaIds + ")");
			} else {
				condition.append(" and area = " + wareArea);
			}
			if( searched && hasResult ) {
				condition.append(" and product_id in (" + ids + ")");
			}
			if(!condition.toString().equals("")){
				condition1 = condition.toString();
			}
			if ( searched && !hasResult ) {
				returnedProductList = new ArrayList();
			} else {
				returnedProductList = service.getProductStockList(condition1,-1,-1,"id asc");
			}
			
			for( int i = 0; i < returnedProductList.size(); i ++ ) {
				ProductStockBean psb = (ProductStockBean) returnedProductList.get(i);
				voProduct product = wareService.getProduct( psb.getProductId() );
				if( product!= null ) {
					psb.setProduct(product);
				} else {
					product = new voProduct();
					product.setName("");
					product.setCode("");
					psb.setProduct(product);
				}
			}
			Date date = new Date();
			String fileDate = DateUtil.formatDate(date, "yyyyMMdd");
			HSSFWorkbook hwb = null;
			hwb = statService.returnedProductNoCargoMatchedWebWork2(returnedProductList);
			response.reset();
			response.addHeader("Content-Disposition", "attachment;filename="+ toUtf8String("无散件区货位商品"+fileDate,request)+ ".xls");
			response.setContentType("application/msxls");
		    hwb.write(response.getOutputStream());
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.exportReturnedProductNoCargoMatched exception", e);
			}
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
	}

	/**
	 * 说明：物流员工作业效率排名
	 * 
	 * 日期：2013-2-22
	 * 
	 * 作者：石远飞
	 * 
	 */
	public ActionForward returnedProductAppraisal(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			String firstCount = "";
			String oneselfCount = "";
			String ranking ="";
			String productCount="";
			String firstProductCount="";
			int n = 1;
			CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
			if(csBean == null){
				request.setAttribute("tip", "此账号不是物流员工 !");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=4");
			List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=4", -1, -1, " oper_count DESC");
			if(cspBean != null){
				for(int i = 0;i < cspList.size();i++){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(i);
					if(i==0){
						firstCount = bean.getOperCount() + "";
						firstProductCount = bean.getProductCount()+"";
						if(cspBean.getOperCount() >= bean.getOperCount()){
							productCount = cspBean.getProductCount()+"";
							ranking = "排名第" + n ;
							oneselfCount = cspBean.getOperCount()+"";
							break;
						}else{
							n++;
						}
					}else{
						if(cspBean.getOperCount() >= bean.getOperCount()){
							ranking = "排名第" + n ;
							productCount = cspBean.getProductCount()+"";
							oneselfCount = cspBean.getOperCount()+"";
							break;
						}else{
							n++;
						}
					}
				}
			}else{
				if(cspList != null && cspList.size() > 0){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(0);
					firstCount = bean.getOperCount() + "";
					firstProductCount = bean.getProductCount()+"";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}else{
					firstCount = "0";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}
			}
			request.setAttribute("firstCount", firstCount);
			request.setAttribute("productCount", productCount);
			request.setAttribute("oneselfCount", oneselfCount);
			request.setAttribute("firstProductCount", firstProductCount);
			request.setAttribute("ranking", ranking);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		
		return mapping.findForward("returnedProductAppraisal");
	}
	//单个商品质检
	@SuppressWarnings("unchecked")
	public ActionForward appraisalReturnedProduct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		CookieUtil cu = new CookieUtil(request, response);
		String returnedProductCode = StringUtil.convertNull(request.getParameter("returnedProductCode"));
		String appraisalNumber = StringUtil.convertNull(request.getParameter("appraisalNumber"));
		String appraisalResult = StringUtil.convertNull(request.getParameter("appraisalResult"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		int appraisalNumberInt = -1;
		int appraisalResultInt = -1;
		List cargoInfoList = new ArrayList();
		String forward = "";
		try {
			if(!CargoDeptAreaService.hasCargoDeptArea(request, wareArea, ProductStockBean.STOCKTYPE_RETURN)) {
				request.setAttribute("tip", "你没有对应地区的退货库的操作权限！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if( !appraisalNumber.equals("")) {
				appraisalNumberInt = Integer.parseInt(appraisalNumber);
			} else {
				request.setAttribute("tip", "未传入质检个数！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if( appraisalNumberInt <= 0 ) {
				request.setAttribute("tip", "传入的质检个数有误!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if( wareArea < 0 ) {
				request.setAttribute("tip", "未选择操作的库地区！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			if( !appraisalResult.equals("")) {
				appraisalResultInt = Integer.parseInt(appraisalResult);
			}
		} catch(Exception e ) {
			request.setAttribute("tip", "所传质检结果或商品个数格式有错误!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnedPackageService rpService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,statService.getDbOp());
		try {
			synchronized(AppraisalReturnedProductLock){
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(returnedProductCode)+"'");
				voProduct product = null;
				if( bBean == null || bBean.getBarcode() == null ) {
					product = wareService.getProduct(returnedProductCode);
				} else {
					product = wareService.getProduct(bBean.getProductId());
				}
				if( product == null ) {
					request.setAttribute("tip", "条码有误");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//验证是否已有退货库库存
				ProductStockBean psb = service.getProductStock("stock > 0 and area = " + wareArea + " and type = " + ProductStockBean.STOCKTYPE_RETURN + " and product_id = " + product.getId());
				if( psb == null ) {
					request.setAttribute("tip", "该商品还没有入退货库库存");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if( appraisalResultInt == 0 ) {
					String tip = "";
					if(product.getStatus() == 100 ) {
						tip += "该商品已下架";
					}
					CargoInfoBean cargoInfo = cargoService.getTargetCargoInfo(product,appraisalNumberInt,wareArea);
					if( wareArea == ProductStockBean.AREA_ZC || wareArea == ProductStockBean.AREA_CD ) {
						if( (cargoInfo == null || cargoInfo.getWholeCode() == null) && product.getStatus() == 100 ) {
							request.setAttribute("tip", "该产品已下架且没有符合条件货位可选！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}else if( (cargoInfo == null || cargoInfo.getWholeCode() == null) && product.getStatus() != 100 ) {
							request.setAttribute("tip", "没有符合条件货位可选！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					} else {
						if( (cargoInfo == null || cargoInfo.getWholeCode() == null) && product.getStatus() == 100 ) {
							request.setAttribute("tip", "该产品已下架且没有整件区滞销货位可选了");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}else if( (cargoInfo == null || cargoInfo.getWholeCode() == null) && product.getStatus() != 100 ) {
							request.setAttribute("tip", "没有散件区货位可选了");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					service.getDbOp().startTransaction();
					boolean targetCargoAvail = rpService.dealTargetCargoAndProduct(cargoInfo, product, cargoService, service);
					
					CargoOperationCargoBean cargoOperationCargo = null;
					if( targetCargoAvail == false ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "分配的目的货位在与商品关联时失败");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					} else {
						//生成上架单
						cargoOperationCargo = rpService.createUpShelfBill(user, product, appraisalNumberInt, cargoInfo, wareService, service, statService, cargoService,wareArea);
						//添加退货上架临时表数据
						ReturnedProductVirtual returnedProductVirtual = new ReturnedProductVirtual();
						returnedProductVirtual.setOperId(cargoOperationCargo.getOperId());
						returnedProductVirtual.setCargoId(cargoInfo.getId());
						returnedProductVirtual.setProductId(product.getId());
						cargoService.addReturnedProductVirtual(returnedProductVirtual);
					}
					//临时用这个remark来放一下上架单编号
					service.getDbOp().commitTransaction();
					service.getDbOp().getConn().setAutoCommit(true);
					request.setAttribute("tip", tip);
					//需要传过去的信息有， cargoWholeCode， 上架单编号.
					cargoInfoList.add(cargoOperationCargo);
					request.setAttribute("cargoInfoList", cargoInfoList);
					request.setAttribute("url", "returnStorageAction.do?method=returnedProductAppraisal");
					forward = "printCargoCodeJump";
				} else if ( appraisalResultInt == 1 ) {
					forward = "appraisalReturnedProductSuccess";
				} else {
					request.setAttribute("tip", "所传质检结果参数有错误");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				//报错提是 下架的东西已经放到了对应的商品列表中
				cu.setCookie("Appraisal_result_mark_" + user.getId(), appraisalResult, 60*60*24*30);
				cu.setCookie("Appraisal_area_mark_" + user.getId(), ""+wareArea, 60*60*24*30);
				//物流员工绩效考核操作
				CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
				if(csBean == null){
					request.setAttribute("tip", "此账号不是物流员工 !");
					request.setAttribute("result", "failure");
				}
        		CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=4 and staff_id=" + csBean.getId() );
        		int operCount = 1;
        		int productCount = appraisalNumberInt;
				if(cspBean != null){
					operCount = operCount + cspBean.getOperCount();
					productCount = productCount + cspBean.getProductCount();
					boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + productCount, " id=" + cspBean.getId());
					if(!flag){
						request.setAttribute("tip", "物流员工绩效考核更新操作失败 !");
						request.setAttribute("result", "failure");
					}
				}else{
					CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
					newBean.setDate(date);
					newBean.setProductCount(productCount);
					newBean.setOperCount(operCount);
					newBean.setStaffId(csBean.getId());
					newBean.setType(4);  //4代表退货上架作业
					boolean flag = cargoService.addCargoStaffPerformance(newBean);
					if(!flag){
						request.setAttribute("tip", "物流员工绩效考核添加操作失败 !");
						request.setAttribute("result", "failure");
					}
				}
				service.getDbOp().commitTransaction();
			}
		} catch(Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.appraisalReturnedProduct exception", e);
			}
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", e.getMessage());
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally {
			statService.releaseAll();
		}
		return mapping.findForward(forward);
	}
	
	//检验入合格库上架汇总单的资格
	/*public void checkWholeAreaQualify(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		WareService wareService = new WareService();
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			voProduct product = null;
			ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode)+"'");
			if( bBean == null || bBean.getBarcode() == null ) {
				product = adminService.getProduct(productCode);
			} else {
				product = adminService.getProduct(bBean.getProductId());
			}
			if( product == null ) {
				//商品条码是否存在
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("1");
				return;
			}
			
			//-------------------这里要加上对于库存量的查询---------------
			ProductStockBean psb = service.getProductStock("stock > 0 and area = " + ProductStockBean.AREA_ZC + " and type = " + ProductStockBean.STOCKTYPE_RETURN + " and product_id = " + product.getId());
			CargoProductStockBean cpsb = cargoService.getCargoAndProductStock("ci.stock_type = " + CargoInfoBean.STOCKTYPE_RETURN + " and ci.area_id = " + ProductStockBean.AREA_ZC + " and cps.product_id = " + product.getId() + " and cps.stock_count > 0");
			ReturnedProductCargoBean rpcbTemp = statService.getReturnedProductCargo("product_id = " + product.getId());
			List returnedProductCargoList = statService.getWholeAreaReturnedProductCargoList("rpc.cargo_id = ci.id and rpc.product_id = " + product.getId() + " and ci.stock_type = " + CargoInfoBean.STOCKTYPE_QUALIFIED + " and ci.store_type = " + CargoInfoBean.STORE_TYPE1 + " and ci.area_id = " + ProductStockBean.AREA_ZC + " order by rpc.count desc");
			List returnedCargoList = statService.getReturnedProductCargoList("product_id = " + product.getId(), -1, -1, "id asc");
			// 这个问题 有点儿问题 关于如何确认 当前的这个情况
			if( psb == null ) {
				//是否有可用库存
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("2");
				return;
			}
			
			if( returnedCargoList.size() > 1 ) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("8");
				return;
			}
			
			if( rpcbTemp == null ) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("9");
				return;
			}
			
			if( cpsb == null ) {
				//是否有货位库存
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("3");
				return;
			} else {
				
			}
			
			if( returnedProductCargoList.size() == 0 ) {
				//是否是整件区货位
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("4");
				return;
			}
			
			if(product.getStatus() != 100 ) {
				//是否已经是已下架商品
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("5");
				return;
			}
			CargoInfoBean cargoInfo =  statService.getCargoInfoByProductCode(product.getCode());
			if( cargoInfo != null && cargoInfo.getWholeCode() != null ) {
				//是否有对应的散件区货位号
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("6");
				return;
			}
			
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			response.getWriter().write("7");
			return;
		} catch( Exception e ) {
			if(logger.isErrorEnabled()){
							logger.error("method:ReturnStorageAction.checkWholeAreaQuailify exception", e);
			}
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		
	}*/
	
	public void getCodeAndStock(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try {
			voProduct product = null;
			ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode)+"'");
			if( bBean == null || bBean.getBarcode() == null ) {
				product = wareService.getProduct(productCode);
			} else {
				product = wareService.getProduct(bBean.getProductId());
			}
			if( product == null ) {
			}
			//-------------------这里要加上对于库存量的查询---------------
			ProductStockBean psb = service.getProductStock("stock > 0 and area = " + ProductStockBean.AREA_ZC + " and type = " + ProductStockBean.STOCKTYPE_RETURN + " and product_id = " + product.getId());
			if( psb == null ) {
			}
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			response.getWriter().write("'" + product.getCode() + "," + psb.getStock()+"'");
			return;
		} catch( Exception e ) {
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.getCodeAndStock exception", e);
			}
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	
	
	//生成整件区上架单
	/*public ActionForward generateWholeAreaCargoOperation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		String[] productCodes = null;
		try{
			if(user == null){
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("{status:'fail',tip:'当前没有登录，操作失败！'}");
				return null;
			}
			UserGroupBean group = user.getGroup();
			boolean batchUpdate = group.isFlag(616);
			if( !batchUpdate ) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("{status:'fail',tip:'您没有生成上架单的权限！'}");
				return null;
			}
			boolean changeCargo = group.isFlag(651);
			if( !changeCargo ) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("{status:'fail',tip:'您没有生成整件区上架单的权限！'}");
				return null;
			}
			String codes = StringUtil.convertNull(request.getParameter("codes"));
			String source = StringUtil.convertNull(request.getParameter("source"));
			if( codes != null && !codes.equals("")) {
				productCodes = codes.split(",");
			}
			if( productCodes == null ) {
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write("{status:'fail',tip:'没有扫描成功的整件区上架商品'}");
				return null;
			}
		} catch(Exception e ) {
			e.printStackTrace();
		}
		WareService wareService = new WareService();
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ReturnedPackageService rpService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List allCargoOperationCargoList = new ArrayList();
		String code = "";
		String tip = "";
		try {
			synchronized (lock) {
				
				if( productCodes.length > 0) {
					//添加汇总单
					code = rpService.saveReturnedUpShelf(user, statService, "");
					if(code == null || code.equals("")){
						response.setContentType("text/html;charset=UTF-8");
						response.setCharacterEncoding( "UTF-8");
						response.getWriter().write("{status:'fail',tip:'生成汇总单失败！'}");
						return null;
					}
					service.getDbOp().getConn().setAutoCommit(true);
					
					int productCargoCount = 0;
					for( int i =0; i < productCodes.length; i++ ) {
						String tempCode = productCodes[i];
						int scanNumber = StringUtil.parstInt(request.getParameter("number_" + tempCode));
						if( scanNumber <= 0 ) {
							tip += "商品" + tempCode + "个数有误,";
							continue;
						}
						service.getDbOp().startTransaction();
						voProduct product = null;
						ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(tempCode)+"'");
						if( bBean == null || bBean.getBarcode() == null ) {
							product = adminService.getProduct(tempCode);
						} else {
							product = adminService.getProduct(bBean.getProductId());
						}
						if( product == null ) {
							//商品条码是否存在
							tip += "有商品条码" + tempCode + "不存在,";
							continue;
						}
						if(product.getStatus() != 100 ) {
							tip += "有商品"+product.getCode()+"不是下架商品,";
							continue;
						}
						CargoInfoBean cargoInfo =  statService.getCargoInfoByProductCode(product.getCode());
						if( cargoInfo != null && cargoInfo.getWholeCode() != null ) {
							tip += "有商品" + product.getCode() + "已经有散件区目的货位了,";
							continue;
						}
						//-------------------这里要加上对于库存量的查询---------------
						ProductStockBean psb = service.getProductStock("stock > 0 and area = " + ProductStockBean.AREA_ZC + " and type = " + ProductStockBean.STOCKTYPE_RETURN + " and product_id = " + product.getId());
						CargoProductStockBean cpsb = cargoService.getCargoAndProductStock("ci.stock_type = " + CargoInfoBean.STOCKTYPE_RETURN + " and ci.store_type = " + CargoInfoBean.STORE_TYPE2 + " and ci.area_id = " + ProductStockBean.AREA_ZC + " and cps.product_id = " + product.getId() + " and cps.stock_count > 0");
						List returnedProductCargoList = statService.getWholeAreaReturnedProductCargoList("rpc.cargo_id = ci.id and rpc.product_id = " + product.getId() + " and ci.stock_type = " + CargoInfoBean.STOCKTYPE_QUALIFIED + " and ci.store_type = " + CargoInfoBean.STORE_TYPE1 + " and ci.area_id = " + ProductStockBean.AREA_ZC + " order by rpc.count desc");
						List returnedCargoList = statService.getReturnedProductCargoList("product_id = " + product.getId(), -1, -1, "id asc");
						if( psb == null || cpsb == null || psb.getStock() < scanNumber || cpsb.getStockCount() < scanNumber ) {
							tip += "商品" + product.getCode() + "没有足够的库存可用,";
							continue;
						}
						if( returnedCargoList.size() > 1 ) {
							tip += "商品" + product.getCode() + "这种商品需要修正目的货位,";
							continue;
						}
						if ( returnedProductCargoList.size() == 0 ) {
							tip += "商品" + product.getCode() + "没有找到对应的货位绑定信息,";
							continue;
						} else {
							ReturnedProductCargoBean rpcb = (ReturnedProductCargoBean) returnedProductCargoList.get(0);
							CargoInfoBean cargoInfo2 = cargoService.getTargetCargoInfo(product);
							if( cargoInfo2 == null || cargoInfo2.getWholeCode() == null) {
								tip += "商品" + product.getCode() + "当前没有可使用的使用中整件区滞销货位";
								continue;
							}
							if( cargoInfo2.getId() == rpcb.getCargoId() ) {
								
								//添加上架单
								String operCode = "HWTS"+DateUtil.getNow().substring(2,10).replace("-", "");   
								//生成编号
								CargoOperationBean cargoOper = cargoService.getCargoOperation("code like '"+operCode+"%' order by id desc limit 1");
								if(cargoOper == null){
									operCode = operCode + "00001";
								}else{//获取当日计划编号最大值
									String _code = cargoOper.getCode();
									int number = Integer.parseInt(_code.substring(_code.length()-5));
									number++;
									operCode += String.format("%05d",new Object[]{new Integer(number)});
								}
								CargoOperationBean cob = new CargoOperationBean();
								cob.setStatus(CargoOperationProcessBean.OPERATION_STATUS3);
								cob.setCreateDatetime(DateUtil.getNow());
								cob.setCreateUserId(user.getId());
								cob.setCreateUserName(user.getUsername());
								cob.setCode(operCode);
								cob.setSource(code);
								cob.setStorageCode("GZZ01");
								cob.setAuditingDatetime(DateUtil.getNow());
								cob.setAuditingUserId(user.getId());
								cob.setAuditingUserName(user.getUsername());
								cob.setConfirmDatetime(DateUtil.getNow());
								cob.setConfirmUserName(user.getUsername());
								cob.setStockInType(CargoInfoBean.STORE_TYPE1);
								cob.setStockOutType(CargoInfoBean.STORE_TYPE2);
								cob.setType(CargoOperationBean.TYPE0);
								cob.setPrintCount(0);
								cob.setLastOperateDatetime(DateUtil.getNow());
								cob.setEffectStatus(CargoOperationBean.EFFECT_STATUS0);
								if(!cargoService.addCargoOperation(cob)) {
									tip += "商品"+ product.getCode()+"数据库操作失败没有添加上架单,";
									continue;
								}
								int id = service.getDbOp().getLastInsertId();
								
								CargoProductStockBean cpsbIn = cargoService.getCargoProductStock("product_id = " + product.getId() + " and cargo_id = " + rpcb.getCargoId());
								List outList = new ArrayList();
								rpService.constructSourceInfo(rpService.getDbOp(),outList);
								CargoProductStockBean cpsbOut = null;
								for( int s = 0; s < outList.size(); s++ ) {
									CargoInfoModelBean tempcps = (CargoInfoModelBean) outList.get(s);
									if( tempcps.getProductId() == product.getId() ) {
										cpsbOut = new CargoProductStockBean();
										cpsbOut.setCargoId(tempcps.getId());
										cpsbOut.setId(tempcps.getCargoStockId());
									}
								}
								if( cpsbOut == null ) {
									service.getDbOp().rollbackTransaction();
									tip += "商品"+ product.getCode()+"没有找到源货位库存的信息,";
									continue;
								}
								CargoInfoBean cargoInfoOut = cargoService.getCargoInfo("id = " + cpsbOut.getCargoId());
								CargoInfoBean cargoInfoIn = cargoService.getCargoInfo("id = " + rpcb.getCargoId());
								if(cargoInfoIn == null ) {
									service.getDbOp().rollbackTransaction();
									tip += "商品"+ product.getCode()+"没有找到目的货位库存的信息,";
									continue;
								}
								CargoOperationCargoBean cocb = new CargoOperationCargoBean();
								cocb.setProductId(product.getId());
								cocb.setOperId(id);
								cocb.setInCargoProductStockId(cpsbIn.getId());
								cocb.setInCargoWholeCode(cargoInfoIn.getWholeCode());
								cocb.setOutCargoProductStockId(cpsbOut.getId());
								cocb.setOutCargoWholeCode(cargoInfoOut.getWholeCode());
								cocb.setStockCount(scanNumber);
								cocb.setType(CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE);
								cocb.setUseStatus(CargoOperationCargoBean.COC_WITH_INCARGOINFO_STATUS);
								cocb.setCompleteCount(0);
								//为寻找库区号做准备。
								allCargoOperationCargoList.add(cocb);
								if( !cargoService.addCargoOperationCargo(cocb)) {
									service.getDbOp().rollbackTransaction();
									tip += "商品"+ product.getCode()+"添加入单失败,";
									continue;
								}
								
								CargoOperationCargoBean cocb2 = new CargoOperationCargoBean();
								cocb2.setProductId(product.getId());
								cocb2.setOperId(id);
								cocb2.setOutCargoProductStockId(cpsbOut.getId());
								cocb2.setOutCargoWholeCode(cargoInfoOut.getWholeCode());
								cocb2.setStockCount(scanNumber);
								cocb2.setType(CargoOperationCargoBean.COC_UNWITH_INCARGOINFO_TYPE);
								cocb2.setUseStatus(CargoOperationCargoBean.COC_UNWITH_INCARGOINFO_STATUS);
								cocb2.setCompleteCount(0);
								if( !cargoService.addCargoOperationCargo(cocb2)) {
									service.getDbOp().rollbackTransaction();
									tip += "商品"+ product.getCode()+"添加入单失败,";
									continue;
								}
								
								//锁定库存， 源货位库存
								//锁定目的货位空间冻结量
								if(!cargoService.updateCargoProductStockCount(cpsbOut.getId(), -scanNumber)){
									service.getDbOp().rollbackTransaction();
									tip += "商品" + product.getCode() + "操作失败，货位库存不足,";
									continue;
								}
								if(!cargoService.updateCargoProductStockLockCount(cpsbOut.getId(), scanNumber)){
									service.getDbOp().rollbackTransaction();
									tip += "有商品" + product.getCode() + "操作失败，货位库存不足,";
									continue;
								}
//								}
							if(!cargoService.updateCargoSpaceLockCount(cargoInfoIn.getId(), scanNumber)){
								service.getDbOp().rollbackTransaction();
								tip += "有商品" + product.getCode() + "操作失败，货位库存不足,";
								continue;
							}
							CargoInfoAreaBean cargoInfoArea = cargoService.getCargoInfoArea("id="+cargoInfoOut.getAreaId());
							ProductStockBean outProductStock = service.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_RETURN+" and product_id="+product.getId());
							if(outProductStock==null){
								service.getDbOp().rollbackTransaction();
								tip += "有商品" + product.getCode() + "合格库库存数据错误,";
								continue;
							}
							if (!service.updateProductStockCount(outProductStock.getId(),-scanNumber)) {
								service.getDbOp().rollbackTransaction();
								tip += "有商品" + product.getCode() + "库存操作失败，可能是库存不足，请与管理员联系,";
								continue;
							}
							if (!service.updateProductLockCount(outProductStock.getId(),scanNumber)) {
								service.getDbOp().rollbackTransaction();
								tip += "有商品" + product.getCode() + "库存操作失败，可能是库存不足，请与管理员联系,";
								continue;
							}
							if( scanNumber == outProductStock.getStock() ) {
								if( !statService.deleteReturnedProductCargo("product_id = " + product.getId() + " and cargo_id = " + cargoInfoIn.getId())){
									service.getDbOp().rollbackTransaction();
									tip += "有商品" + product.getCode() + "删除退货库商品货位绑定时 数据库操作失败,";
									continue;
								}
							}
								
								CargoOperationLogBean logBean=new CargoOperationLogBean();
								logBean.setOperId(id);
								logBean.setOperDatetime(DateUtil.getNow());
								logBean.setOperAdminId(user.getId());
								logBean.setOperAdminName(user.getUsername());
								StringBuilder logRemark = new StringBuilder("保存编辑：商品");
								logRemark.append(adminService.getProductCode(product.getId()+""));
								logRemark.append("，");
								logRemark.append("源货位（");
								logRemark.append(cargoInfoOut.getWholeCode());
								logRemark.append("）");
								logRemark.append("，目的货位（");
								logRemark.append(cargoInfoIn.getWholeCode());
								logRemark.append("），");
								logRemark.append("上架量（");
								logRemark.append(scanNumber);
								logRemark.append("）");
								logBean.setRemark(logRemark.toString());
								if(!cargoService.addCargoOperationLog(logBean)){
									service.getDbOp().rollbackTransaction();
									tip += "商品"+ product.getCode()+"添加入单日志失败,";
									continue;
								}
								
								CargoOperationLogBean logBean2=new CargoOperationLogBean();
								logBean2.setOperId(id);
								logBean2.setOperDatetime(DateUtil.getNow());
								logBean2.setOperAdminId(user.getId());
								logBean2.setOperAdminName(user.getUsername());
								StringBuilder logRemark2 = new StringBuilder("退货上架单确认提交，审核通过");
								logRemark.append(adminService.getProductCode(product.getId()+""));
								logRemark2.append("，");
								logRemark2.append("源货位（");
								logRemark2.append(cargoInfoOut.getWholeCode());
								logRemark2.append("）");
								logRemark2.append("，目的货位（");
								logRemark2.append(cargoInfoIn.getWholeCode());
								logRemark2.append("），");
								logRemark2.append("上架量（");
								logRemark2.append(scanNumber);
								logRemark2.append("）");
								logBean2.setRemark(logRemark2.toString());
								if(!cargoService.addCargoOperationLog(logBean2)){
									service.getDbOp().rollbackTransaction();
									tip += "商品"+ product.getCode()+"添加确认，审核日志失败,";
									continue;
								}
								productCargoCount ++;
							} else {
								tip +="商品" + product.getCode() + "需要修正货位";
								continue;
							}
						}
						service.getDbOp().commitTransaction();
						service.getDbOp().getConn().setAutoCommit(true);
					}
					
					String passageCode = rpService.getPassageCodeByMount(allCargoOperationCargoList);
					service.getDbOp().startTransaction();
					if( !rpService.updateXXX("passage_whole_code = '" + passageCode + "'", "code='" + code + "'", "returned_up_shelf") ) {
						tip += "添加库区号失败,";
					}
					service.getDbOp().commitTransaction();
					service.getDbOp().getConn().setAutoCommit(true);
					if( productCargoCount == 0 ) {
						if(!statService.deleteReturnedUpShelf("code='" + code + "'")){
							tip += "没有商品成功生成上架单，汇总单删除失败";
							response.setContentType("text/html;charset=UTF-8");
							response.setCharacterEncoding( "UTF-8");
							response.getWriter().write("{status:'fail',tip:'"+tip+"'}");
							return null;
						} else {
							tip += "没有商品能够添加， 整个汇总单上架单生成失败";
							response.setContentType("text/html;charset=UTF-8");
							response.setCharacterEncoding( "UTF-8");
							response.getWriter().write("{status:'fail',tip:'" + tip + "'}");
							return null;
						}
						
					} else {
						tip +=" 正确商品已生成到汇总单中";
					}
				}
			}
			 
		} catch( Exception e ) {
			if(logger.isErrorEnabled()){
							logger.error("method:ReturnStorageAction.generateWholeAreaCargoOperation exception", e);
			}
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding( "UTF-8");
		try {
			response.getWriter().write("{status:'success',tip:'" + tip + "',code:'" + code + "'}");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	public ActionForward checkOrderCode(
			ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		PrintWriter pw  = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			pw = response.getWriter(); 
			String orderCode = request.getParameter("orderCode");
			if(orderCode==null || orderCode.equals("")){
				pw.write("4");
				return null;
			}
			// 判断订单编号是否存在
			
			OrderStockBean orderStockBean = istockService.getOrderStock("code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
			voOrder vorder = null;
			if(orderStockBean != null){
				vorder = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
			}else{
				vorder = wareService.getOrder("code='"+orderCode+"'");
			}
			
			if (vorder == null) {
				pw.write("2");
				return null;
			}
			
			ReturnedPackageBean packageBean  = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
			if(packageBean != null 
					&& (packageBean.getStorageStatus() == 0 || packageBean.getStorageStatus()==2)){
				pw.write("0");
				return null;
			}
			pw.write("1");
			return null;
		}catch(Exception e){
			pw.write("3");
			return null;
		}finally{
			wareService.releaseAll();
		}
	}
	
	//新已下架商品列表
	@SuppressWarnings("unchecked")
	public ActionForward getReturnedSaleOutProductList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		try {
			//获得用户的权限库地区，只查询他有权限的地区
			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int x = cdaList.size();
			for(int i = 0; i < x; i++ ) {
				availAreaIds += "," + cdaList.get(i);
			}
			
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
	        PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
	        List returnedSaleOutProductList = new ArrayList();
	        paging.setCurrentPageIndex(pageIndex);
	        paging.setPrefixUrl("returnStorageAction.do?method=getReturnedSaleOutProductList");
	        
	        String condition = "";
	        String ids = statService.getReturnedSaleOutProductIds(availAreaIds, wareArea);
	        if( ids != "") {
	        	ids = ids.substring(0,(ids.length() - 1));
	        	condition = "id in (" + ids + ")";
	        } else {
	        	condition = "id in (" + -1 + ")";
	        }
			returnedSaleOutProductList = service.getProductStockList(condition,paging.getCurrentPageIndex() * countPerPage,countPerPage,"id asc");
			if(returnedSaleOutProductList.size()==0&&paging.getCurrentPageIndex()!=0){
				returnedSaleOutProductList= service.getProductStockList(condition,(paging.getCurrentPageIndex()-1) * countPerPage,countPerPage,"id asc");
			}
			for( int i = 0; i < returnedSaleOutProductList.size(); i ++ ) {
				ProductStockBean psb = (ProductStockBean) returnedSaleOutProductList.get(i);
				voProduct product = wareService.getProduct(psb.getProductId());
				if(product != null) {
					psb.setProduct(product);
				} else {
					product = new voProduct();
					product.setName("");
					product.setCode("");
					psb.setProduct(product);
				}
			}
			
			int totalCount = service.getProductStockCount(condition); //根据条件得到 总数量
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-(totalCount%countPerPage))/countPerPage)+1);
			request.setAttribute("paging", paging);
			request.setAttribute("returnedSaleOutProductList", returnedSaleOutProductList);
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.getReturnedSaleOutProductList exception", e);
			}
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("returnedSaleOutProductTable");
	}
	
   //6-4-hp
	//查询包裹列表
	public ActionForward queryPackage(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
		StatService service = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		ReturnedPackageService retPService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, dbOp);
		synchronized ( request.getSession().getId()) {
			try{
				String tempSql = "";
				String tempSqlCount = "";
				ReturnedPackageFBean formBean = (ReturnedPackageFBean) form;
				if( formBean.getCvStatus() == -1 && formBean.getOrderStatus() == -1 ) {
					tempSql = "select rp.id,rp.order_id,rp.order_code,rp.package_code,rp.deliver,rp.operator_id,rp.operator_name,rp.storage_time,rp.storage_status,rp.remark,rp.reason_id,rp.claims_verification_id,rp.area,rp.`status`,rp.import_time,rp.import_user_name,import_user_id ,rp.check_datetime from returned_package rp where rp.id <> 0  ";
					tempSqlCount = "select count(rp.id) from returned_package rp  where rp.id <> 0  "; 
				}
				if( formBean.getCvStatus() != -1 ) {
					tempSql = "select rp.id,rp.order_id,rp.order_code,rp.package_code,rp.deliver,rp.operator_id,rp.operator_name,rp.storage_time,rp.storage_status,rp.remark,rp.reason_id,rp.claims_verification_id,rp.area,rp.`status`,rp.import_time,rp.import_user_name,import_user_id ,rp.check_datetime  from returned_package rp, claims_verification cv  where rp.claims_verification_id = cv.id  ";
					tempSqlCount = "select count(rp.id) from returned_package rp, claims_verification cv where rp.claims_verification_id = cv.id  ";
				} 
				sql.append(tempSql);
				sqlCount.append(tempSqlCount);
				int countPerPage=20;
				int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
				if(formBean.getOrderCode()!=null && !formBean.getOrderCode().equals("")){
					sql.append(" and rp.order_code='");
					sql.append(formBean.getOrderCode());
					sql.append("'");
					sqlCount.append(" and rp.order_code='");
					sqlCount.append(formBean.getOrderCode());
					sqlCount.append("'");
					params.append("&orderCode="+formBean.getOrderCode());
					
				}
				if(formBean.getPackageCode() != null && !formBean.getPackageCode().equals("")){
					sql.append(" and rp.package_code='");
					sql.append(formBean.getPackageCode());
					sql.append("'");
					sqlCount.append(" and rp.package_code='");
					sqlCount.append(formBean.getPackageCode());
					sqlCount.append("'");
					params.append("&packageCode=" + formBean.getPackageCode());
				}
				if((formBean.getStorageStartTime() != null && !formBean.getStorageStartTime().equals("")) && ( formBean.getStorageEndTime() != null && !"".equals(formBean.getStorageEndTime()) ) ){
					String beginTime = formBean.getStorageStartTime() + " 00:00:00";
					String endTime = formBean.getStorageEndTime() + " 23:59:59";
					sql.append(" and rp.storage_time between '");
					sql.append(beginTime);
					sql.append("' and '");
					sql.append(endTime);
					sql.append("'");
					sqlCount.append(" and rp.storage_time between '");
					sqlCount.append(beginTime);
					sqlCount.append("' and '");
					sqlCount.append(endTime);
					sqlCount.append("'");
					params.append("&storageStartTime=" + formBean.getStorageStartTime());
					params.append("&storageEndTime="+formBean.getStorageEndTime());
				}
				//6-13  //
				//System.out.println("query-hp-Time-"+formBean.getCheckStartTime());
				if(formBean.getCheckStartTime() != null && !formBean.getCheckStartTime().equals("")&&formBean.getCheckEndTime() != null && !formBean.getCheckEndTime().equals("")){
					String beginTime = formBean.getCheckStartTime() + " 00:00:00";
					String endTime = formBean.getCheckEndTime() + " 23:59:59";
					sql.append(" and rp.check_datetime between '");
					sql.append(beginTime);
					sql.append("' and '");
					sql.append(endTime);
					sql.append("'");
					sqlCount.append(" and rp.check_datetime between '");
					sqlCount.append(beginTime);
					sqlCount.append("' and '");
					sqlCount.append(endTime);
					sqlCount.append("'");
					params.append("&checkStartTime=" + formBean.getCheckStartTime());
					params.append("&checkEndTime=" + formBean.getCheckEndTime());
				}
				
				if(formBean.getDeliver() != -1){
					sql.append(" and rp.deliver=");
					sql.append(formBean.getDeliver());
					sqlCount.append(" and rp.deliver=");
					sqlCount.append(formBean.getDeliver());
					params.append("&deliver=" + formBean.getDeliver());
				}
				if( formBean.getWareArea() != -1 ) {
					sql.append(" and rp.area = " + formBean.getWareArea());
					sqlCount.append(" and rp.area = " + formBean.getWareArea());
					params.append("&wareArea="+formBean.getWareArea());
				}
				
				if( formBean.getCvStatus() != -1 ) {
					sql.append(" and cv.status=" + formBean.getCvStatus());
					sqlCount.append(" and cv.status=" + formBean.getCvStatus());
					params.append("&cvStatus=" + formBean.getCvStatus());
				}
				if( formBean.getReturnedPackageStatus() != -1 ) {
					sql.append(" and rp.status=" + formBean.getReturnedPackageStatus() );
					sqlCount.append(" and rp.status=" + formBean.getReturnedPackageStatus());
					params.append("&returnedPackageStatus=" + formBean.getReturnedPackageStatus());
				}
				request.setAttribute("deliverMap", voOrder.deliverMapAll);
				request.setAttribute("formBean", formBean);
				
				int totalCount = retPService.getReturnedPackageCountDirectly(sqlCount.toString());
				PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
				
				List<ReturnedPackageBean> packageList = retPService.getReturnedPackageListDirectly(sql.toString(), paging.getCurrentPageIndex()*countPerPage, countPerPage, "storage_time desc");
				int x = packageList.size();
				for( int i = 0 ; i < x; i ++ ) {
					ReturnedPackageBean rpBean = packageList.get(i);
					if( rpBean.getClaimsVerificationId() != 0 ) {
						ClaimsVerificationBean claimsVerificationBean = claimsVerificationService.getClaimsVerification("id=" + rpBean.getClaimsVerificationId());
						rpBean.setClaimsVerificationBean(claimsVerificationBean);
					}
					if( rpBean.getReasonId() != 0 ) {
						String sql2 = "select id,reason from returns_reason where id = " + rpBean.getReasonId();
						ResultSet rs = service.getDbOp().executeQuery(sql2);
						if(rs.next()) {
							ReturnsReasonBean bean = new ReturnsReasonBean();
							bean.setId(rs.getInt(1));
							bean.setReason(rs.getString(2));
							rpBean.setReturnsReasonBean(bean);
						}
					}
					voOrder vorder = wareService.getOrder(rpBean.getOrderId());
					if( vorder == null ) {
						rpBean.setOrderStatusName("");
					} else {
						rpBean.setOrderStatusName(vorder.getStatusName());
					}
				}
				StringBuilder strBuilder = new StringBuilder();
				strBuilder.append("returnedPackageAction.do?method=queryPackage");
				strBuilder.append(params.toString());
				paging.setPrefixUrl(strBuilder.toString());
				request.setAttribute("paging", paging);
				request.setAttribute("packageList", packageList);
				request.setAttribute("recordNum", totalCount+"");
				return mapping.findForward("success");
			}finally{
				service.releaseAll();
				wareService.releaseAll();
			}
		}
	}
	
	
	//导出包裹列表
	//hp6-4增加复核时间
	public void exportPackage(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp); 
		StatService service = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		ReturnedPackageService retPService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			String tempSql = "";
			String tempSqlCount = "";
			ReturnedPackageFBean formBean = (ReturnedPackageFBean) form;
			if (formBean.getCvStatus() == -1 && formBean.getOrderStatus() == -1) {
				tempSql = "select rp.id,rp.order_id,rp.order_code,rp.package_code,rp.deliver,rp.operator_id,rp.operator_name,rp.storage_time,rp.storage_status,rp.remark,rp.reason_id,rp.claims_verification_id,rp.area,rp.`status`,rp.import_time,rp.import_user_name,import_user_id ,rp.check_datetime from returned_package rp where rp.id <> 0  ";
				tempSqlCount = "select count(rp.id) from returned_package rp  where rp.id <> 0  ";
			}
			if (formBean.getCvStatus() != -1) {
				tempSql = "select rp.id,rp.order_id,rp.order_code,rp.package_code,rp.deliver,rp.operator_id,rp.operator_name,rp.storage_time,rp.storage_status,rp.remark,rp.reason_id,rp.claims_verification_id,rp.area,rp.`status`,rp.import_time,rp.import_user_name,import_user_id ,rp.check_datetime  from returned_package rp, claims_verification cv  where rp.claims_verification_id = cv.id  ";
				tempSqlCount = "select count(rp.id) from returned_package rp, claims_verification cv where rp.claims_verification_id = cv.id  ";
			}
			sql.append(tempSql);
			sqlCount.append(tempSqlCount);
			if (formBean.getOrderCode() != null && !formBean.getOrderCode().equals("")) {
				sql.append(" and rp.order_code='");
				sql.append(formBean.getOrderCode());
				sql.append("'");
				sqlCount.append(" and rp.order_code='");
				sqlCount.append(formBean.getOrderCode());
				sqlCount.append("'");
				params.append("&orderCode=" + formBean.getOrderCode());

			}
			if (formBean.getPackageCode() != null && !formBean.getPackageCode().equals("")) {
				sql.append(" and rp.package_code='");
				sql.append(formBean.getPackageCode());
				sql.append("'");
				sqlCount.append(" and rp.package_code='");
				sqlCount.append(formBean.getPackageCode());
				sqlCount.append("'");
				params.append("&packageCode=" + formBean.getPackageCode());
			}
			if ((formBean.getStorageStartTime() != null && !formBean.getStorageStartTime().equals("")) && (formBean.getStorageEndTime() != null && !"".equals(formBean.getStorageEndTime()))) {
				String beginTime = formBean.getStorageStartTime() + " 00:00:00";
				String endTime = formBean.getStorageEndTime() + " 23:59:59";
				sql.append(" and rp.storage_time between '");
				sql.append(beginTime);
				sql.append("' and '");
				sql.append(endTime);
				sql.append("'");
				sqlCount.append(" and rp.storage_time between '");
				sqlCount.append(beginTime);
				sqlCount.append("' and '");
				sqlCount.append(endTime);
				sqlCount.append("'");
				params.append("&storageStartTime=" + formBean.getStorageStartTime());
				params.append("&storageEndTime=" + formBean.getStorageEndTime());
			}else{
				String beginTime = DateUtil.getNowDateStr() + " 00:00:00";
				String endTime = DateUtil.getNowDateStr() + " 23:59:59";
				sql.append(" and rp.storage_time between '");
				sql.append( beginTime + "' and  '");
				sql.append(endTime + "'");
			}
			// 6-13 //
			// System.out.println("query-hp-Time-"+formBean.getCheckStartTime());
			if (formBean.getCheckStartTime() != null && !formBean.getCheckStartTime().equals("") && formBean.getCheckEndTime() != null && !formBean.getCheckEndTime().equals("")) {
				String beginTime = formBean.getCheckStartTime() + " 00:00:00";
				String endTime = formBean.getCheckEndTime() + " 23:59:59";
				sql.append(" and rp.check_datetime between '");
				sql.append(beginTime);
				sql.append("' and '");
				sql.append(endTime);
				sql.append("'");
				sqlCount.append(" and rp.check_datetime between '");
				sqlCount.append(beginTime);
				sqlCount.append("' and '");
				sqlCount.append(endTime);
				sqlCount.append("'");
				params.append("&checkStartTime=" + formBean.getCheckStartTime());
				params.append("&checkEndTime=" + formBean.getCheckEndTime());
			}
			if(formBean.getDeliver() != -1){
				sql.append(" and rp.deliver=");
				sql.append(formBean.getDeliver());
				sqlCount.append(" and rp.deliver=");
				sqlCount.append(formBean.getDeliver());
				params.append("&deliver=" + formBean.getDeliver());
			}
			if (formBean.getWareArea() != -1) {
				sql.append(" and rp.area = " + formBean.getWareArea());
				sqlCount.append(" and rp.area = " + formBean.getWareArea());
				params.append("&wareArea=" + formBean.getWareArea());
			}

			if (formBean.getCvStatus() != -1) {
				sql.append(" and cv.status=" + formBean.getCvStatus());
				sqlCount.append(" and cv.status=" + formBean.getCvStatus());
				params.append("&cvStatus=" + formBean.getCvStatus());
			}
			if (formBean.getReturnedPackageStatus() != -1) {
				sql.append(" and rp.status=" + formBean.getReturnedPackageStatus());
				sqlCount.append(" and rp.status=" + formBean.getReturnedPackageStatus());
				params.append("&returnedPackageStatus=" + formBean.getReturnedPackageStatus());
			}
			request.setAttribute("deliverMap", voOrder.deliverMapAll);
			request.setAttribute("formBean", formBean);


			List<ReturnedPackageBean> packageList = retPService.getReturnedPackageListDirectly(sql.toString(), -1,-1,"storage_time desc");
			int x = packageList.size();
			for (int i = 0; i < x; i++) {
				ReturnedPackageBean rpBean = packageList.get(i);
				if (rpBean.getClaimsVerificationId() != 0) {
					ClaimsVerificationBean claimsVerificationBean = claimsVerificationService.getClaimsVerification("id=" + rpBean.getClaimsVerificationId());
					rpBean.setClaimsVerificationBean(claimsVerificationBean);
				}
				if (rpBean.getReasonId() != 0) {
					String sql2 = "select id,reason from returns_reason where id = " + rpBean.getReasonId();
					ResultSet rs = service.getDbOp().executeQuery(sql2);
					if (rs.next()) {
						ReturnsReasonBean bean = new ReturnsReasonBean();
						bean.setId(rs.getInt(1));
						bean.setReason(rs.getString(2));
						rpBean.setReturnsReasonBean(bean);
					}
				}
				voOrder vorder = wareService.getOrder(rpBean.getOrderId());
				if (vorder == null) {
					rpBean.setOrderStatusName("");
				} else {
					rpBean.setOrderStatusName(vorder.getStatusName());
				}
			}
			request.setAttribute("packageList", packageList);
			XSSFWorkbook hwb = retPService.exportPackage(packageList);
			response.reset();
			response.addHeader("Content-Disposition", "attachment;filename="
					+ DateUtil.getNowDateStr() + StringUtil.toUtf8String("退货包裹列表") + ".xls");
			response.setContentType("application/msxls");
			hwb.write(response.getOutputStream());
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	
	
	
	//作业失败任务
	public ActionForward rollbackRetShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		WareService wareService = new WareService();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		ReturnedPackageService service = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			if(user == null){
				pw.write("用户没有登录！");
				return null;
			}
			
//			String cargoOpId = request.getParameter("cargoOpId");
//			if(cargoOpId == null || cargoOpId.equals("")){
//				pw.write("作业单id不能为空！");
//				return null;
//			}
			
			String upShelfCode = request.getParameter("upShelfCode");
			if(upShelfCode == null || upShelfCode.equals("")){
				pw.write("该退货上架汇总单编号不能为空！");
				return null;
			}
			
			String flag = service.rollbackRetShelf(upShelfCode, user, wareService);
			pw.write(flag);
			return null;
		}catch(Exception e){
			e.printStackTrace();
			pw.write("系统异常，请联系管理员！");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	
	
	/**
	 *为已下架商品生成退货上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward constructUpShelfOfunProduct(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		try{
//			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
//			ReturnedPackageService service = new ReturnedPackageServiceImpl();
//			int cargoOperationId = service.constructUpShelfOfunProduct(user);
			int cargoOperationId = -1;
			if(cargoOperationId == -2){
				request.setAttribute("tip", "操作失败，退货合格品商品不足！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(cargoOperationId == -3){
				request.setAttribute("tip", "操作失败，目的货位中没有退货的商品！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			if(cargoOperationId == -5){
				request.setAttribute("tip", "没有可上架的商品！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			if(cargoOperationId != 0 && cargoOperationId != -1){
				ActionForward actionForward = new ActionForward(); 
				actionForward.setPath("/admin/showRetShelfAction.do?method=showRetShelf&cargoOperationId="+cargoOperationId); 
				return actionForward; 
			}else if(cargoOperationId==0){
				request.setAttribute("tip", "操作失败，源货位库存不足！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}else if(cargoOperationId==-4){
				request.setAttribute("tip", "操作失败，目的货位空间锁定量不足，或者货位已经删除！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}else{
				request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("tip", "生成退货上架单失败");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
	}
	/**
	 * 生成退货上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward constructRetShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		try{
//			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
//			ReturnedPackageService service = new ReturnedPackageServiceImpl();
//			int cargoOperationId = service.constructRetShelf(user);
//			if(cargoOperationId == -2){
//				request.setAttribute("tip", "操作失败，退货合格品商品不足！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
//			if(cargoOperationId == -3){
//				request.setAttribute("tip", "操作失败，目的货位中没有退货的商品！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
//			
//			if(cargoOperationId == -5){
//				request.setAttribute("tip", "没有可上架的商品！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
//			
//			if(cargoOperationId != 0 && cargoOperationId != -1){
//				ActionForward actionForward = new ActionForward(); 
//				actionForward.setPath("/admin/showRetShelfAction.do?method=showRetShelf&cargoOperationId="+cargoOperationId); 
//				return actionForward; 
//			}else{
//				request.setAttribute("tip", "生成退货上架单失败，请联系系统管理员！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
//			else if(cargoOperationId==0){
//				request.setAttribute("tip", "操作失败，源货位库存不足！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}else if(cargoOperationId==-4){
//				request.setAttribute("tip", "操作失败，目的货位空间锁定量不足，或者货位已经删除！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}else{
//				request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
			return null;
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("tip", "生成退货上架单失败");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
	}
	
	
	/**
	 * 确认提交退货上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward confirmSubmitShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		WareService wareService = new WareService();
		try{
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ReturnedPackageService retService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			StatService statSevice = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			if(user == null){
				request.setAttribute("tip", "用户没有登录");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
//			int cargoOperationId = StringUtil.parstInt(request.getParameter("cargoOperationId"));
			String upShelfCode = request.getParameter("upShelfCode");
			if(upShelfCode == null || upShelfCode.equals("")){
				request.setAttribute("tip", "作业单号不能为空！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			ReturnedUpShelfBean rufBean = statSevice.getReturnedUpShelf(
					"code='"+StringUtil.toSql(upShelfCode)+"'");
			if(rufBean == null){
				request.setAttribute("tip", "该汇总单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List cargoOperationList = cargoService.getCargoOperationList(
					"source='"+StringUtil.toSql(upShelfCode)+"' and status="+CargoOperationProcessBean.OPERATION_STATUS1, 0, -1, null);
		
			wareService.getDbOp().startTransaction();
			CargoOperationBean coBean = null;
			for(int i=0; i<cargoOperationList.size(); i++){
				coBean = (CargoOperationBean) cargoOperationList.get(i);
				List cocList = cargoService.getCargoOperationCargoList(
						"oper_id="+coBean.getId()+" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, 0, -1, null);
				if(cocList ==null || cocList.isEmpty()){
					request.setAttribute("tip", "该任务下没有任务详细信息");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoOperationCargoBean  cocBean =  null; 
				CargoInfoModelBean comBean = null;
				CargoInfoBean ciBean = null;
				int tempValue = 0;
				int tempCount = 0;
				voProduct product = null;
				for(int j=0; j<cocList.size(); j++){
					cocBean = (CargoOperationCargoBean) cocList.get(j);
					tempCount = StringUtil.parstInt(request.getParameter("pcount_"+cocBean.getId()));
					//获取合格量，退货库库存，货位库存最小值
					tempValue = getMinStockCount(cargoService, statSevice, psService, cocBean);
					if(tempCount>tempValue){
						product = wareService.getProduct(cocBean.getProductId());
						request.setAttribute("tip", "产品："+product.getCode()+"，编辑数量不能大于货位库存量或库存量！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!cargoService.updateCargoOperationCargo("stock_count="+tempCount, "oper_id="+cocBean.getOperId())){
						request.setAttribute("tip", "更新退货任务库存失败！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					ciBean = cargoService.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
					comBean = new CargoInfoModelBean();
					comBean.setCount(tempCount);
					comBean.setProductId(cocBean.getProductId());
					comBean.setId(ciBean.getId());
					comBean.setCargoStockId(cocBean.getOutCargoProductStockId());
					List preList = new ArrayList();
					preList.add(comBean);
					
//					//更新退货商品表合格商品数
//					retService.updateRetProductCount(retService.getDbOp(), preList, null, null);
//					System.out.println("更新退货表成功");
					
					//更新退货库库存
					if(!retService.lockProductStock(retService.getDbOp(), preList, null, null)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "更新退货商品表失败，库存锁定量不足！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					};
		
					//更新退货(源货位)货位库存
					if(!retService.lockCargoProductStock(preList, null, null, cargoService)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "更新源货位库存失败，库存锁定量不足！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
					//更新目标货位空间锁定量
//					if(!retService.lockCargoProductSpaceStock(preList, null, null, cargoService)){
//						adminService.rollbackTransaction();
//						request.setAttribute("tip", "更新目标货位空间锁定量失败，空间锁定量不足！");
//						request.setAttribute("result", "failure");
//						return mapping.findForward(IConstants.FAILURE_KEY);
//					}
					
					CargoOperationLogBean logBean=new CargoOperationLogBean();
					logBean.setOperId(coBean.getId());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark = new StringBuilder("编辑商品："+cocBean.getProductId());
					logRemark.append(wareService.getProductCode(cocBean.getProductId()+""));
					logRemark.append("，");
					logRemark.append("源货位（");
					logRemark.append(cocBean.getOutCargoWholeCode());
					logRemark.append("）");
					logRemark.append("，目的货位（");
					logRemark.append(cocBean.getInCargoWholeCode());
					logRemark.append("），");
					logRemark.append("上架量（");
					logRemark.append(tempCount);
					logRemark.append("）");
					logBean.setRemark(logRemark.toString());
					if(!cargoService.addCargoOperationLog(logBean)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "添加人员操作记录失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				//更新退货任务状态为已提交
				if(!cargoService.updateCargoOperation(
						"status="+CargoOperationProcessBean.OPERATION_STATUS2
						+",confirm_datetime='"+DateUtil.getNow()+"'"
						+",confirm_user_name='"+user.getUsername()+"'"
						+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+coBean.getId())){
					wareService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "更新退货上架单状态失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			if(!statSevice.updateReturnedUpShelf(
					"status="+ReturnedUpShelfBean.OPERATION_STATUS38
					+",confirm_datetime='"+DateUtil.getNow()+"'"
					+",confirm_user_name='"+user.getUsername()+"'", "id="+rufBean.getId())){
				wareService.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "更新汇总单状态失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			wareService.getDbOp().commitTransaction();
			ActionForward actionForward = new ActionForward(); 
			actionForward.setPath("/admin/showRetShelfAction.do?method=showRetShelf&upShelfCode="+rufBean.getCode()); 
			return actionForward; 
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			wareService.releaseAll();
		}
	}
	
	/**
	 * 确认编辑退货上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward confirmEditShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		WareService wareService = new WareService();
		try{
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			StatService statSevice = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null){
				request.setAttribute("tip", "用户没有登录");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
//			int cargoOperationId = StringUtil.parstInt(request.getParameter("cargoOperationId"));
			String upShelfCode = request.getParameter("upShelfCode");
			if(upShelfCode == null || upShelfCode.equals("")){
				request.setAttribute("tip", "作业单号不能为空！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			ReturnedUpShelfBean rufBean = statSevice.getReturnedUpShelf(
					"code='"+StringUtil.toSql(upShelfCode)+"'");
			if(rufBean == null){
				request.setAttribute("tip", "该汇总单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			
			List cargoOperationList = cargoService.getCargoOperationList(
					"source='"+StringUtil.toSql(upShelfCode)+"' and status="+CargoOperationProcessBean.OPERATION_STATUS1, 0, -1, null);
		
			wareService.getDbOp().startTransaction();
			CargoOperationBean coBean = null;
			for(int i=0; i<cargoOperationList.size(); i++){
				coBean = (CargoOperationBean) cargoOperationList.get(i);
				List cocList = cargoService.getCargoOperationCargoList(
									"oper_id="+coBean.getId()+" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, 0, -1, null);
				if(cocList ==null || cocList.isEmpty()){
					request.setAttribute("tip", "该任务下没有任务详细信息");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoOperationCargoBean  cocBean =  null; 
				int tempValue = -1;
				voProduct product = null;
				int tempCount = 0;
				for(int j=0; j<cocList.size(); j++){
					cocBean = (CargoOperationCargoBean) cocList.get(j);
					tempCount = StringUtil.parstInt(request.getParameter("pcount_"+cocBean.getId()));
					tempValue = getMinStockCount(cargoService, statSevice,
							psService, cocBean);
					product = wareService.getProduct(cocBean.getProductId());
					if(tempCount>tempValue){
						request.setAttribute("tip", "产品："+product.getCode()+"，编辑数量不能大于货位库存量或库存量！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!cargoService.updateCargoOperationCargo("stock_count="+tempCount, "oper_id="+cocBean.getOperId())){
						request.setAttribute("tip", "更新退货任务库存失败！");
						request.setAttribute("result", "failure");
						wareService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
					CargoOperationLogBean logBean=new CargoOperationLogBean();
					logBean.setOperId(coBean.getId());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark = new StringBuilder("编辑商品："+cocBean.getProductId());
					logRemark.append(wareService.getProductCode(cocBean.getProductId()+""));
					logRemark.append("，");
					logRemark.append("源货位（");
					logRemark.append(cocBean.getOutCargoWholeCode());
					logRemark.append("）");
					logRemark.append("，目的货位（");
					logRemark.append(cocBean.getInCargoWholeCode());
					logRemark.append("），");
					logRemark.append("上架量（");
					logRemark.append(tempCount);
					logRemark.append("）");
					logBean.setRemark(logRemark.toString());
					if(!cargoService.addCargoOperationLog(logBean)){
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "添加人员操作记录失败！");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
			}
			wareService.getDbOp().commitTransaction();
			ActionForward actionForward = new ActionForward(); 
			actionForward.setPath("/admin/showRetShelfAction.do?method=showRetShelf&upShelfCode="+rufBean.getCode()+"&editFlag=1"); 
			return actionForward; 
//			request.setAttribute("tip", "编辑成功！");
//			return mapping.findForward(IConstants.FAILURE_KEY);
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			wareService.releaseAll();
		}
	}

	private int getMinStockCount(ICargoService cargoService,
			StatService statSevice, IProductStockService psService,
			CargoOperationCargoBean cocBean) {
//		ReturnedProductBean rpBean;
		CargoProductStockBean cpsBean;
		ProductStockBean psBean;
		int tempValue;
//		rpBean = statSevice.getReturnedProduct("product_id="+cocBean.getProductId()+" and type=1");
		cpsBean = cargoService.getCargoAndProductStock(
				"ci.stock_type=4 and ci.area_id=3 and cps.product_id="+cocBean.getProductId());
		psBean = psService.getProductStock("product_id="+cocBean.getProductId()+" and area=3 and type=4");
		tempValue = cpsBean.getStockCount();
//		if(tempValue>cpsBean.getStockCount()){
//			tempValue = cpsBean.getStockCount();
//		}
		if(tempValue>psBean.getStock()){
			tempValue=psBean.getStock();
		}
		return tempValue;
	}
	
	/**
	 * 展示退货汇总上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward showRetShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		try{
			
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null){
				request.setAttribute("tip", "用户没有登录");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			String filteredProductCode = request.getParameter("filteredProductCode");
			if(filteredProductCode == null || filteredProductCode.equals("")){
				request.setAttribute("filteredProductCode", "");
			}else{
				request.setAttribute("filteredProductCode", filteredProductCode);
			}
			
			String editFlag = request.getParameter("editFlag");
			if(editFlag == null || editFlag.equals("")){
				request.setAttribute("editFlag", "");
			}else{
				request.setAttribute("editFlag", editFlag);
			}
			
			UserGroupBean group = user.getGroup();
			
			
//			int cargoOperationId = 0;
//			if(request.getParameter("cargoOperationId")!=null 
//					&& !request.getParameter("cargoOperationId").equals("")){
//				cargoOperationId = Integer.valueOf(request.getParameter("cargoOperationId")).intValue();
//			}
//			if(cargoOperationId <= 0){
//				request.setAttribute("tip", "生成退货上架单Id不能为空");
//				request.setAttribute("result", "failure");
//				return mapping.findForward(IConstants.FAILURE_KEY);
//			}
			if(request.getParameter("upShelfCode")==null 
					|| request.getParameter("upShelfCode").equals("")){
				request.setAttribute("tip", "生成退货汇总上架单编号不能为空");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			ReturnedPackageService retPService = new ReturnedPackageServiceImpl();
			CargoInfoModelBean cargoModelBean = retPService.getCargInfoModelByUpShelfCode(request.getParameter("upShelfCode"));
//			CargoInfoModelBean cargoModelBean = retPService.getCargInfoModelById(cargoOperationId);
			if(cargoModelBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS38){
				if(!group.isFlag(617)){
					request.setAttribute("showAuditButton", "0");
				}else{
					request.setAttribute("showAuditButton", "1");
				}
			}else if(cargoModelBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS39){
				if(!group.isFlag(620)){
					request.setAttribute("showAuditButton", "0");
				}else{
					request.setAttribute("showAuditButton", "1");
				}
			}else if(cargoModelBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS37){
				request.setAttribute("showAuditButton", "1");
			}else{
				request.setAttribute("showAuditButton", "0");
			}
			request.setAttribute("cargoModelBean", cargoModelBean);
			List operationCodeList = cargoModelBean.getPassageCode();
			if(operationCodeList == null || operationCodeList.isEmpty()){
				request.setAttribute("tip", "该汇总单下没有上架单，请联系管理员");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
//			initRetShelfPageInfo(request, cargoModelBean, operationCodeList);
			request.setAttribute("cargoModelBean", cargoModelBean);
			return mapping.findForward("success");
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			request.setAttribute("result", "failure");
			e.printStackTrace();
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
	}
	
	
	
	/**
	 * 删除退货上架任务
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward deleteRetShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		WareService wareService = new WareService();
		ICargoService service = null;
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			String upShelfCode = request.getParameter("upShelfCode");
			if(upShelfCode == null || upShelfCode.equals("")){
				request.setAttribute("tip", "汇总单编号不能为空！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			
			service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			String cargoOpCode = request.getParameter("cargoOperationCode");
			String[] cargoOpStatus = request.getParameterValues("cargoOpStatus");
			String cargoCode = request.getParameter("cargoCode");
			String productCode = request.getParameter("productCode");
			request.setAttribute("cargoOperationCode", cargoOpCode);
			request.setAttribute("cargoOpStatus", cargoOpStatus);
			request.setAttribute("productCode", productCode);
			request.setAttribute("cargoCode", cargoCode);
			
			wareService.getDbOp().startTransaction();
			List cargoOperationList = service.getCargoOperationList("source='"+upShelfCode+"' and status=="+CargoOperationProcessBean.OPERATION_STATUS1, 0, -1, null);
			CargoOperationBean coBean = null;
			for(int j=0; j<cargoOperationList.size(); j++){
				coBean = (CargoOperationBean) cargoOperationList.get(j);
				service.deleteCargoOperation("id="+coBean.getId());
				List cocBeanList = service.getCargoOperationCargoList(
						"oper_id="+coBean.getId(), 0, -1, null);
				
				CargoOperationCargoBean cocBean = null;
				for(int i=0; i<cocBeanList.size(); i++){
					cocBean = (CargoOperationCargoBean) cocBeanList.get(i);
					service.deleteCargoOperationCargo("id="+cocBean.getId());
				}
			}
			statService.deleteReturnedUpShelf("code='"+upShelfCode+"'");
			wareService.getDbOp().commitTransaction();
			ActionForward actionForward = new ActionForward(); 
			actionForward.setPath("/admin/retShelfListAction.do?method=retShelfList"); 
			return actionForward;
		}catch(Exception e){
			wareService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", "系统异常，请联系管理员");
			request.setAttribute("result", "failure");
			e.printStackTrace();
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			wareService.releaseAll();
		}
	}
	
	
	/**
	 * 查询统计汇总单信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward queryRetShelfStaticInfo(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter();  
		String createDate = request.getParameter("createDateTime");
		if(createDate == null || createDate.equals("")){
			createDate = DateUtil.getNowDateStr();
		}
		ReturnedPackageService packageSerivce = new ReturnedPackageServiceImpl();
		StringBuilder str = new StringBuilder("<div style=margin-top:10px; text-align:left;>");
		try{
			str.append(packageSerivce.queryRetShelfStaticInfo(createDate));
			str.append("</div>");
			str.append("<div><input type=button onclick=hiddenSelf(); value=隐藏></div>");
			pw.write(str.toString());
		}catch(Exception e){
			e.printStackTrace();
			pw.write("获取统计信息错误，请联系管理员！");
		}
		return null;
	}
	/**
	 * 展示退货上架汇总单列表
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward retShelfList(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ReturnedPackageService retService = new ReturnedPackageServiceImpl(BaseServiceImpl.CONN_IN_SERVICE,wareService.getDbOp());
		//ICargoService service = service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String cargoOpCode = request.getParameter("cargoOperationCode");//汇总单号
			String[] cargoOpStatus = request.getParameterValues("cargoOpStatus");
			String cargoCode = request.getParameter("cargoCode");
			String productCode = request.getParameter("productCode");
			String createUser = request.getParameter("createUser");
			int countPerPage=10;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
			
			int count = 0;
			count = retService.getRetShelfCount(cargoOpStatus,cargoOpCode,cargoCode,productCode,createUser);
			
			PagingBean paging = new PagingBean(pageIndex, count, countPerPage);
			if(count == 0){
				request.setAttribute("cargoOperationList", new ArrayList());
				request.setAttribute("cargoOperationCode", cargoOpCode);
				request.setAttribute("cargoOpStatus", cargoOpStatus);
				request.setAttribute("productCode", productCode);
				request.setAttribute("cargoCode", cargoCode);
				request.setAttribute("createUser", createUser);
				return mapping.findForward("success");
			}
			
			List retUpShelfList = retService.getRetShelfList(cargoOpStatus,cargoOpCode,cargoCode,productCode, pageIndex*countPerPage, countPerPage, createUser, "create_datetime desc");
			assignStatusName(retUpShelfList, null);
			
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("../admin/retShelfListAction.do?method=retShelfList");
			if(cargoOpCode != null && !cargoOpCode.equals("")){
				queryBuilder.append("&cargoOperationCode=" + cargoOpCode);
			}
			
			if(cargoOpStatus != null && !cargoOpStatus.equals("")){
				for(int i=0; i<cargoOpStatus.length; i++){
					queryBuilder.append("&cargoOpStatus=" + cargoOpStatus[i]);
				}
			}
			
			if(cargoCode !=null && !cargoCode.equals("")){
				queryBuilder.append("&cargoCode=" + cargoCode);
			}
			
			if(productCode !=null && !productCode.equals("")){
				queryBuilder.append("&productCode="+productCode);
			}
			
			if(createUser != null && !createUser.equals("")){
				queryBuilder.append("&createUser="+createUser);
			}
			paging.setPrefixUrl(queryBuilder.toString());
			paging.setTotalPageCount(count%countPerPage==0?count/countPerPage:count/countPerPage+1);
			String pageLine = PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", countPerPage);
			request.setAttribute("pageLine", pageLine);
			request.setAttribute("cargoOpList", retUpShelfList);
			request.setAttribute("cargoOperationCode", cargoOpCode);
			request.setAttribute("cargoOpStatus", cargoOpStatus);
			request.setAttribute("productCode", productCode);
			request.setAttribute("cargoCode", cargoCode);
			request.setAttribute("createUser", createUser);
			return mapping.findForward("success");
			
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			request.setAttribute("result", "failure");
			e.printStackTrace();
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			wareService.releaseAll();
		}
	}
	
	
	private void assignStatusName(List upShelfList, ICargoService service) {
		
		ReturnedUpShelfBean rufBean = null;
		for(int i=0; i<upShelfList.size(); i++){
			rufBean = (ReturnedUpShelfBean) upShelfList.get(i);
			rufBean.setStatusName(ReturnedUpShelfBean.statusMap.get(Integer.valueOf(rufBean.getStatus()))+"");
			if(rufBean.getCreateDatetime() != null){
				rufBean.setCreateDatetime(rufBean.getCreateDatetime().substring(0,19));
			}
			if(rufBean.getCompleteDatetime() != null){
				rufBean.setCompleteDatetime(rufBean.getCompleteDatetime().substring(0,19));
			}
			if(rufBean.getAuditingDatetime() != null){
				rufBean.setAuditingDatetime(rufBean.getAuditingDatetime().substring(0,19));
			}
		}
	}

	
	//强制作业完成，将未完成上架单修改为作业失败，失效状态为失败
	public ActionForward completeRetShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter();  
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			pw.write("用户没有登录");
			return null;
		}
		
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(618)){
			pw.write("您没有权限进行此操作！");
			return null;
		}
		
		
		String upShelfCode = request.getParameter("upShelfCode");
		if(upShelfCode == null || upShelfCode.equals("")){
			pw.write("该退货上架汇总单编号不能为空！");
			return null;
		}
		
		
		WareService wareService = new WareService();
		
		StatService statService = null;
		try{
			
			
			statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ReturnedPackageService retService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			
			wareService.getDbOp().startTransaction();
			ReturnedUpShelfBean rufBean = statService.getReturnedUpShelf("code='"+StringUtil.toSql(upShelfCode)+"'");
			if(rufBean == null){
				pw.write("该汇总单不存在！");
				return null;
			}
			
			//完成退货上架单
			String result = retService.completeRetShelf(rufBean, user, wareService);
			if(result != null){
				wareService.getDbOp().rollbackTransaction();
				pw.write(result);
				return null;
			}
			wareService.getDbOp().commitTransaction();
			pw.write("8");//作业单完成
		}catch(Exception e){
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
			pw.write("系统异常，请联系管理员");
			return null;
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
		return null;
	}
	
	/**
	 * 审核退货上架汇总单，状态变为已审核
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward confirmRetShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter();  
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			pw.write("用户没有登录");
			return null;
		}
		
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(617)){
			pw.write("您没有权限进行此操作！");
			return null;
		}
		String retShelfCode = request.getParameter("retshelfCode");
		if(retShelfCode == null || retShelfCode.equals("")){
			pw.write("2");
			return null;
		}
		ReturnedPackageService service = new ReturnedPackageServiceImpl();
//		WareService wareService = null;
		try{
//			adminService = new WareService();
//			ICargoService cargoService = ServiceFactory.createCargoService(
//								BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
//			CargoDeptAreaService daService = new CargoDeptAreaService(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
//			
//			List<CargoOperationBean> coBeanList = cargoService.getCargoOperationList(
//													"source='"+retShelfCode+"'", -1, -1, null);
//			if(coBeanList != null && !coBeanList.isEmpty()){
//				StringBuilder strBuilder = new StringBuilder();
//				for(CargoOperationBean coBean : coBeanList){
//					if(strBuilder.length()>0){
//						strBuilder.append(",");
//					}
//					strBuilder.append(coBean.getId());
//				}
//				List<CargoOperationCargoBean> cocBeanList = cargoService.getCargoOperationCargoList(
//						"oper_id in("+strBuilder.toString()+")"+" and type=0", -1, -1, null);
//				CargoInfoBean ci = null;
//				if(cocBeanList != null && !cocBeanList.isEmpty()){
//					for(CargoOperationCargoBean cocBean : cocBeanList){
//						ci = cargoService.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
//						if(ci != null){
//							if(!daService.hasCargoDeptArea(request, ci.getAreaId(), ci.getStockType())){
//								pw.write("您没有权限操作不属于自己部门的汇总单！");
//								return null;
//							}
//						}
//					}
//				}
//			}
			
			pw.write(service.confirmRetShelf(retShelfCode,user));
			return null;
		}catch(Exception e){
			pw.write("系统异常，请联系管理员");
			return null;
		}
	}
	
	
	/**
	 * 打印退货上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward printRetShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String upShelfCode = request.getParameter("upShelfCode");
		if(upShelfCode == null || upShelfCode.equals("")){
			request.setAttribute("tip", "退货上架汇总单编号不能为空！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnedPackageService retPService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			CargoInfoModelBean cargoModelBean = null;
			synchronized(lock){
				cargoModelBean = retPService.getCargInfoModelByUpShelfCode(upShelfCode);
				if(cargoModelBean == null){
					request.setAttribute("tip", "退货上架汇总单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			List passageCodeList = cargoModelBean.getPassageCode();
			String formatPassCode = formatPassCode(passageCodeList);
			cargoModelBean.setPstrCode(formatPassCode);
			
			List operationCodeList = cargoModelBean.getPassageCode();
			if(operationCodeList == null || operationCodeList.isEmpty()){
				return mapping.findForward("success");
			}
			
//			initRetShelfPageInfo(request, cargoModelBean, operationCodeList);
			request.setAttribute("cargoModelBean", cargoModelBean);
			
			return mapping.findForward("success");
		}catch(Exception e){
			request.setAttribute("tip", "系统异常，请联系管理员");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			service.releaseAll();
		}
	}

	private void initRetShelfPageInfo(HttpServletRequest request,
			CargoInfoModelBean cargoModelBean, List operationCodeList) {
		if(operationCodeList.size()==3){
			request.setAttribute("firstPassage", 
					cargoModelBean.getPassageProduct().get(operationCodeList.get(0)));
			request.setAttribute("secondPassage", 
					cargoModelBean.getPassageProduct().get(operationCodeList.get(1)));
			request.setAttribute("thirdPassage",
					cargoModelBean.getPassageProduct().get(operationCodeList.get(2)));
			request.setAttribute("hasFirst","1");
			request.setAttribute("hasSecond","1");
			request.setAttribute("hasThird","1");
		}else if(operationCodeList.size()==2){
			request.setAttribute("firstPassage", 
					cargoModelBean.getPassageProduct().get(operationCodeList.get(0)));
			request.setAttribute("secondPassage", 
					cargoModelBean.getPassageProduct().get(operationCodeList.get(1)));
			request.setAttribute("hasFirst","1");
			request.setAttribute("hasSecond","1");
			request.setAttribute("hasThird","0");
		}else{
			request.setAttribute("firstPassage",
					cargoModelBean.getPassageProduct().get(operationCodeList.get(0)));
			request.setAttribute("hasFirst","1");
			request.setAttribute("hasSecond","0");
			request.setAttribute("hasThird","0");
		}
	}
	
	
	
	private String formatPassCode(List passageCodeList) {
		StringBuilder strBuilder = new StringBuilder();
		for(int i=0; i<passageCodeList.size();i++){
			if(strBuilder.length()>0){
				strBuilder.append("-");
			}
			strBuilder.append(passageCodeList.get(i));
		}
		return strBuilder.toString();
	}

	//检测是否有退货上架单作业确认权限
	public ActionForward checkConfirmOpAuthority(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		PrintWriter pw = response.getWriter();  
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(618)){
			pw.write("0");
			return null;
		}
		return null;
		
	}
	
	//检测是否有退货包裹入库权限
	public ActionForward checkStorageAuthority(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		UserGroupBean group = user.getGroup();
		PrintWriter pw = response.getWriter();  
		if(!group.isFlag(613)){
			pw.write("0");
			return null;
		}
		return null;
	}
	
	//检测退货上架单确认是否有权限
	public ActionForward checkRetShelfAuthority(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		UserGroupBean group = user.getGroup();
		PrintWriter pw = response.getWriter();  
		if(!group.isFlag(617)){
			pw.write("0");
			return null;
		}
		return null;
	}
	
	
	public ActionForward clearOpSession(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute(Constants.CACHECAROINO);
		return null;
	}
	
	
	/**
	 * 退货上架作业确认,状态改为已完成
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ActionForward confirmOp(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();  
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			pw.write("9");
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(618)){
			pw.write("您没有权限进行此操作！");
			return null;
		}
		
		String barCode = request.getParameter("barCode");
		if(barCode == null || barCode.equals("")){
			pw.write("0");//条码不能为空
		}
		
		CargoInfoModelBean cargoInfoModel = (CargoInfoModelBean) request.getSession()
									.getAttribute(Constants.CACHECAROINO);
		
		WareService wareService = new WareService();
		wareService.getDbOp().startTransaction();//需要连接检查
		StatService statService = null;
		ICargoService service = null;
		IBarcodeCreateManagerService bService = 
			ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService productservice = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ReturnedPackageService repService = new ReturnedPackageServiceImpl(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			
			service = ServiceFactory.createCargoService(
						IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			
			statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			
			
			//判断作业单是否存在
			if(cargoInfoModel == null){
				
				//校验权限，判断汇总单下上架单是否属于用户所在部门
				boolean result = repService.checkCollectBill(request, barCode);
				if(!result){
					pw.write("您没有权限完成该退货上架汇总单！");//汇总单不存在
					return null;
				}
				
				ReturnedUpShelfBean rufBean = statService.getReturnedUpShelf(
						"code='"+StringUtil.toSql(barCode)+"'");
				if(rufBean == null){
					pw.write("该汇总单不存在！");//汇总单不存在
					return null;
				}
				
//				cargoOperation = service.getCargoOperation(
//						"code='" + barCode + "' and type="+CargoOperationBean.TYPE4);
//				if(cargoOperation == null){
//					pw.write("1");//作业单不存在
//					return null;
//				}
				if(rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS38){
					pw.write("-2");//作业单没有被审核
					return null;
				}
				if(rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS43
						|| rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS45
						|| rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS46){
					pw.write("-1");//作业单已经完成
					return null;
				}
				cargoInfoModel = new CargoInfoModelBean();
				cargoInfoModel.setRetUpShelfCode(barCode);
				List cargoOperationList = service.getCargoOperationList(
						"source='"+StringUtil.toSql(barCode)+"'"
						+" and effect_status!="+CargoOperationBean.EFFECT_STATUS4
						+" and status!="+CargoOperationProcessBean.OPERATION_STATUS7
						+" and status!="+CargoOperationProcessBean.OPERATION_STATUS9, 0, -1, null);
				StringBuilder operIds = new StringBuilder();
				CargoOperationBean coBean = null;
				if(cargoOperationList == null || cargoOperationList.isEmpty()){
					pw.write("汇总单下所有上架单已经作业结束！");
					return null;
				}
				for(int i=0; i<cargoOperationList.size(); i++){
					coBean = (CargoOperationBean) cargoOperationList.get(i);
					if(operIds.length()>0){
						operIds.append(",");
					}
					operIds.append(coBean.getId());
				}
				cargoInfoModel.setCargoOperationIds(operIds.toString());
				request.getSession().setAttribute(Constants.CACHECAROINO, cargoInfoModel);
				pw.write("2");//作业单正常
				return null;
			}
			
			//判断商品是否存在，并判断商品是否属于作业单,如果不是商品需要看是不是货位编号
			CargoInfoBean cargoInfo = null;
			voProduct product = null;
			ProductBarcodeVO bBean = bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(barCode)+"'");
			if(bBean == null){
				product = wareService.getProduct(StringUtil.toSql(barCode));
			}else{
				product = wareService.getProduct(bBean.getProductId());
			}
			
//			ReturnedProductBean retProduct = null;
//			
//			if(product != null){
//				retProduct = statService.getReturnedProduct(
//						"product_code='" + product.getCode() + "' and type=1");
//			}
			
			if(product == null ){
				if(cargoInfoModel.getProductBeanList().isEmpty()){
					pw.write("16");//商品条码不存在
					return null;
				}else{
					cargoInfo = service.getCargoInfo(
							"whole_code='" + barCode + "' and stock_type=" + CargoInfoBean.STOCKTYPE_QUALIFIED 
							+" and status=" + CargoInfoBean.STATUS0);
					if(cargoInfo == null){
						if(StringUtil.isNumeric(barCode)){
							pw.write("16");//商品条码不存在
						}else{
							pw.write("4");//货位条码不存在
						}
						return null;
					}else{
						
						wareService.getDbOp().startTransaction();
						StringBuilder strPId = new StringBuilder();
						strPId.append("(");
						List retProductList = cargoInfoModel.getProductBeanList();
						voProduct rp = null;
						for(int j=0; j<retProductList.size(); j++){
							rp = (voProduct) retProductList.get(j);
							if(j>0){
								strPId.append(",");
							}
							strPId.append(rp.getId());
						}
						strPId.append(")");
						List cargoOpList = new ArrayList();
						if(strPId.length()>0){
							cargoOpList = service.getCargoOperationCargoList(
								"oper_id in("+cargoInfoModel.getCargoOperationIds() + 
								") and product_id in " + strPId.toString()+
								" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
						}
						
						rp = (voProduct) retProductList.get(0);
						CargoOperationCargoBean cocBean = null;
//						boolean cargoCodeFlag = true;//判断输入的是否是扫描商品所属货位
//						for(int i=0; i<cargoOpList.size(); i++){
//							cocBean = (CargoOperationCargoBean) cargoOpList.get(i);
//							if(!cocBean.getInCargoWholeCode().equals(cargoInfo.getWholeCode())){
//								cargoCodeFlag = false;
//								break;
//							}
//						}
//						
//						if(!cargoCodeFlag){
//							pw.write("21");
//							return null;
//						}
						
						if(rp.getInCargoWholeCode()==null 
								|| !rp.getInCargoWholeCode().equals(cargoInfo.getWholeCode())){
							pw.write("21");
							return null;
						}
						
						
						//更新货位库存完成商品数量，减少源货位锁定量，目的货位空间锁定量，目的货位库存，减少库存锁定量
						ReturnedPackageService rpService = new ReturnedPackageServiceImpl();
						List productList = cargoInfoModel.getProductBeanList();
						List newProductList = new ArrayList();
						voProduct vproduct = null;
						voProduct temProduct = null;
						for(int i=0; i<productList.size(); i++){
							vproduct = (voProduct)productList.get(i);
							if(newProductList.isEmpty()){
								vproduct.setCount(1); 
								newProductList.add(vproduct);
							}else if(newProductList.contains(vproduct)){
								temProduct = (voProduct) newProductList.get(newProductList.indexOf(vproduct));
								temProduct.setCount(temProduct.getCount()+1);
							}else{
								newProductList.add(vproduct);
							}
						}
						cargoInfoModel.setProductBeanList(newProductList);
						//作业完成更新退货库，合格库库存信息
						String strFlag = rpService.updateStockInfo(
								cargoInfoModel, service, wareService, productservice, cargoOpList, user);
						if(strFlag != null){
							wareService.getDbOp().rollbackTransaction();
							request.getSession().removeAttribute(Constants.CACHECAROINO);
							pw.write(strFlag);
							return null;
						}
						cargoOpList = service.getCargoOperationCargoList(
								"oper_id in("+cargoInfoModel.getCargoOperationIds() + 
								") and product_id in " + strPId.toString()+
								" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
					
						for(int k=0; k<cargoOpList.size(); k++){
							cocBean = (CargoOperationCargoBean) cargoOpList.get(k);
							//检测商品完成数量和库存数量是否一致，如果一致那么修改上架单状态为已完成
							if(cocBean.getCompleteCount()==cocBean.getStockCount()){
								if(!service.updateCargoOperation(
										"status=" + CargoOperationProcessBean.OPERATION_STATUS7
										+ ",complete_datetime='" + DateUtil.getNow() + "'"
										+ ",complete_user_name='" + user.getUsername() + "'"
										+ ",complete_user_id=" + user.getId(), 
										"id=" + cocBean.getOperId())){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("更新作业完成数量失败！");
									return null;
								}
								CargoOperationLogBean logBean = new CargoOperationLogBean();
								logBean.setOperId(cocBean.getOperId());
								logBean.setOperDatetime(DateUtil.getNow());
								logBean.setOperAdminId(user.getId());
								logBean.setOperAdminName(user.getUsername());
								logBean.setRemark("作业单操作确认完成");
								if(!service.addCargoOperationLog(logBean)){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("添加操作记录失败！");
									return null;
								}
							}else{
								CargoOperationLogBean logBean = new CargoOperationLogBean();
								logBean.setOperId(cocBean.getOperId());
								logBean.setOperDatetime(DateUtil.getNow());
								logBean.setOperAdminId(user.getId());
								logBean.setOperAdminName(user.getUsername());
								logBean.setRemark("作业单操作确认进行中");
								if(!service.addCargoOperationLog(logBean)){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("添加操作记录失败！");
									return null;
								}
							}
						}
						
						//查询汇总单下是否存在时效状态不等于作业失败，并且状态为已审核的上架单
						int count = service.getCargoOperationCount(
								"source='"+cargoInfoModel.getRetUpShelfCode()+"'" +
										" and status="+CargoOperationProcessBean.OPERATION_STATUS3 +
										" and effect_status!="+CargoOperationBean.EFFECT_STATUS4);
						
						if(count==0){
//							count = service.getCargoOperationCount(
//								"source='"+cargoInfoModel.getRetUpShelfCode()
//								+"' and (status="+CargoOperationProcessBean.OPERATION_STATUS9
//								+" or effect_status="+CargoOperationBean.EFFECT_STATUS4+")");
//							if(count>0){
								if(!statService.updateReturnedUpShelf(
										"status="+ReturnedUpShelfBean.OPERATION_STATUS46+
										",complete_datetime='"+DateUtil.getNow()+"'"+
										",complete_user_name='"+user.getUsername()+"'"+
										",complete_user_id="+user.getId(), "code='"+cargoInfoModel.getRetUpShelfCode()+"'")){
									wareService.getDbOp().rollbackTransaction();
									request.getSession().removeAttribute(Constants.CACHECAROINO);
									pw.write("更新汇总单失败！");
									return null;
								}
//							}else{
//								statService.updateReturnedUpShelf("status="+ReturnedUpShelfBean.OPERATION_STATUS43+
//										",complete_datetime='"+DateUtil.getNow()+"'"+
//										",complete_user_name='"+user.getUsername()+"'"+
//										",complete_user_id="+user.getId(), "code='"+cargoInfoModel.getRetUpShelfCode()+"'");
//							}
						}
						request.getSession().removeAttribute(Constants.CACHECAROINO);
						pw.write("8");//作业单完成
						wareService.getDbOp().commitTransaction();
						return null;
					}
				}
			}else{
				
				//查看商品是否属于上架单
				CargoOperationCargoBean cargoOper = service.getCargoOperationCargo(
						"oper_id in(" + cargoInfoModel.getCargoOperationIds() +
						") and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE +
						" and use_status="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_STATUS +
						" and product_id=" + product.getId());
				CargoOperationBean coBean = null;
				if(cargoOper == null){
					pw.write("6");//商品不属于上架单
					return null;
				}else{
					coBean = service.getCargoOperation("id="+cargoOper.getOperId());
					if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS7){
						if(cargoOper.getStockCount()==cargoOper.getCompleteCount()){
							if(cargoOper.getStockCount()==0){
								
							}else{
								pw.write("24");//该商品已经作业完成
								request.getSession().removeAttribute(Constants.CACHECAROINO);
								return null;
							}
						}
						if(cargoInfoModel.getProductBeanList().isEmpty()){
							product.setInCargoWholeCode(cargoOper.getInCargoWholeCode());
							cargoInfoModel.getProductBeanList().add(product);
							pw.write("11");
							return null;
						}else{
							voProduct retProductBean = 
									(voProduct) cargoInfoModel.getProductBeanList().get(0);
							if(retProductBean.getInCargoWholeCode().equals(cargoOper.getInCargoWholeCode())){
								if(cargoInfoModel.getProductBeanList().size()>=(cargoOper.getStockCount()-cargoOper.getCompleteCount())){
									pw.write("22");//未上架量不足
									return null;
								}
								cargoInfoModel.getProductBeanList().add(product);
								pw.write("11");
								return null;
							}else{
								pw.write("7");//商品不属于货位
								return null;
							}
						}
					}else{
						pw.write("24");//该商品已经作业完成
						request.getSession().removeAttribute(Constants.CACHECAROINO);
						return null;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			request.getSession().removeAttribute(Constants.CACHECAROINO);
			wareService.getDbOp().rollbackTransaction();
			pw.write("系统异常，请联系管理员");
			return null;
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	

	
	
//	private boolean checkComplete(List cocList) {
//		
//		CargoOperationCargoBean cocBean = null;
//		for(int i=0; i<cocList.size(); i++){
//			cocBean = (CargoOperationCargoBean) cocList.get(i);
//			if(cocBean.getCompleteCount()!=cocBean.getStockCount()){
//				return false;
//			}
//		}
//		return true;
//	}
//
//	private String updateCompleteCount(
//				CargoInfoModelBean cargoInfoModel,
//				ICargoService service, 
//				WareService wareService,
//				IProductStockService productservice) throws Exception {
//		
//		List retProductList = cargoInfoModel.getProductBeanList();
//		ReturnedProductBean productBean = null;
//		CargoOperationCargoBean cocBean = null;
//		CargoInfoBean sourceCargoInfo = null;//源货位信息
//		CargoInfoBean targetCargoInfo = null;//目的货位信息
//		CargoProductStockBean cargoProductStock = null;//货位库存
//		voProduct product = null;
//		for(int i=0; i<retProductList.size(); i++){
//			productBean = (ReturnedProductBean) retProductList.get(i);
//			cocBean = service.getCargoOperationCargo(
//					"oper_id=" + cargoInfoModel.getId() + " and product_id=" + productBean.getProductId());
//			if(cocBean.getStockCount()!=cocBean.getCompleteCount()){
//				if(!service.updateCargoOperationCargo(
//						"complete_count=complete_count+1", 
//						"oper_id=" + cargoInfoModel.getId() + 
//						" and product_id=" + productBean.getProductId() + 
//						" and complete_count<stock_count")){
//					return "23";//更新作业单库存量
//				}
//				
//				product = adminService.getProduct(productBean.getProductId());
//				product.setPsList(productservice.getProductStockList("product_id="+ productBean.getProductId(), -1, -1, null));
//				
//				//减去源货位库存锁定量
//				if(!service.updateCargoProductStockLockCount(
//						cocBean.getOutCargoProductStockId(), -1)){
//					return "12";//操作失败，货位库存不足！
//				}
//				
//				ReturnedPackageService retService = new ReturnedPackageServiceImpl();
//				//减少退货库库存锁定量
//				if(!retService.updateProductStock(
//						productBean.getProductId(), 1,service.getDbOp())){
//					return "20";////库存操作失败，可能是库存不足，请与管理员联系！
//				}
//				
//				//减去目的货位空间锁定量
//				CargoProductStockBean cpsBean = service.getCargoProductStock("id=" + cocBean.getInCargoProductStockId());
//				if(!service.updateCargoSpaceLockCount(
//						cpsBean.getCargoId(), -1)){
//					return "17";
//				}
//				
//				//增加目的货位库存量
//				if(!service.updateCargoProductStockCount(
//						cocBean.getInCargoProductStockId(), 1)){
//					return "18";
//				}
//				
//				IProductStockService productStockService = new ProductStockServiceImpl(
//										IBaseService.CONN_IN_SERVICE,service.getDbOp());
//				//增加合格库散件区库存量
//				if(!productStockService.updateProductStock(
//						"stock=(stock+1)", 
//						"product_id=" + productBean.getProductId() + 
//						" and area="+ProductStockBean.AREA_ZC + 
//						" and type="+ProductStockBean.STOCKTYPE_QUALIFIED + 
//						" and stock>-1" + 
//						" and status=0")){
//					return "19";
//				}
//				
//				targetCargoInfo = service.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
//				sourceCargoInfo = service.getCargoInfo("whole_code='"+cocBean.getOutCargoWholeCode()+"'");
//				
//				// 出库卡片
//				StockCardBean sc = new StockCardBean();
//				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//				sc.setCode(cargoInfoModel.getOperationCode());
//				sc.setCreateDatetime(DateUtil.getNow());
//				sc.setStockType(sourceCargoInfo.getStockType());
//				CargoInfoAreaBean cia = service.getCargoInfoArea("id="+sourceCargoInfo.getAreaId());
//				if(cia == null){
//					continue;
//				}
//				int stockArea = cia.getOldId();
//				sc.setStockArea(stockArea);
//				sc.setProductId(productBean.getProductId());
//				sc.setStockId(sourceCargoInfo.getStorageId());
//				sc.setStockOutCount(1);
//				// sc.setStockOutPriceSum(stockoutPrice);
//				sc.setStockOutPriceSum((new BigDecimal(1)).multiply(
//								new BigDecimal(StringUtil.formatDouble2(product
//										.getPrice5()))).doubleValue());
//				sc.setCurrentStock(product.getStock(sourceCargoInfo.getStockAreaId(),
//						sourceCargoInfo.getStockType()) + product.getLockCount(sourceCargoInfo.getStockAreaId(),sourceCargoInfo.getStockType()));
//				sc.setStockAllArea(product.getStock(
//						sourceCargoInfo.getStockAreaId()) + product.getLockCount(sourceCargoInfo.getStockAreaId()));
//				sc.setStockAllType(product.getStockAllType(
//						sourceCargoInfo.getStockType()) + product.getLockCountAllType(sourceCargoInfo.getStockType()));
//				sc.setAllStock(product.getStockAll()
//						+ product.getLockCountAll());
//				sc.setStockPrice(product.getPrice5());
//				sc.setAllStockPriceSum((new BigDecimal(
//						sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
//				if (!productservice.addStockCard(sc)) {
//					throw new RuntimeException("出库添加进销存库存卡片失败！");
//				}
//	
//				cargoProductStock = service.getCargoAndProductStock("cargo_id="+sourceCargoInfo.getId() + " and product_id="+ productBean.getProductId());
//				// 货位出库卡片
//				CargoStockCardBean csc = new CargoStockCardBean();
//				csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//				csc.setCode(cargoInfoModel.getOperationCode());
//				csc.setCreateDatetime(DateUtil.getNow());
//				csc.setStockType(sourceCargoInfo.getStockType());
//				csc.setStockArea(stockArea);
//				csc.setProductId(productBean.getProductId());
//				csc.setStockId(sourceCargoInfo.getStorageId());
//				csc.setStockOutCount(1);
//				csc.setStockOutPriceSum((new BigDecimal(1))
//						.multiply(
//								new BigDecimal(StringUtil.formatDouble2(product
//										.getPrice5()))).doubleValue());
//				csc.setCurrentStock(product.getStock(sc.getStockArea(),
//						sc.getStockType())
//						+ product.getLockCount(sc.getStockArea(),
//								sc.getStockType()));
//				csc.setAllStock(product.getStockAll()
//						+ product.getLockCountAll());
//				csc.setCurrentCargoStock(cargoProductStock.getStockCount()
//						+ cargoProductStock.getStockLockCount());
//				csc.setCargoStoreType(sourceCargoInfo.getStoreType());
//				csc.setCargoWholeCode(sourceCargoInfo.getWholeCode());
//				csc.setStockPrice(product.getPrice5());
//				csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
//						.multiply(
//								new BigDecimal(StringUtil.formatDouble2(sc
//										.getStockPrice()))).doubleValue());
//				if (!service.addCargoStockCard(csc)) {
//					throw new RuntimeException("出库进销存货位卡片添加失败！");
//				}
//	
//				CargoInfoAreaBean tcib = service.getCargoInfoArea("id="+targetCargoInfo.getAreaId());
//				if(tcib == null){
//					continue;
//				}
//				int tarStockArea = tcib.getOldId();
//				// 入库卡片
//				sc = new StockCardBean();
//				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//				sc.setCode(cargoInfoModel.getOperationCode());
//				sc.setCreateDatetime(DateUtil.getNow());
//				sc.setStockType(targetCargoInfo.getStockType());
//				sc.setStockArea(tarStockArea);
//				sc.setProductId(productBean.getProductId());
//				sc.setStockId(targetCargoInfo.getStorageId());
//				sc.setStockInCount(1);
//				// sc.setStockInPriceSum(stockinPrice);
//				sc.setStockInPriceSum((new BigDecimal(1)).multiply(
//								new BigDecimal(StringUtil.formatDouble2(product
//										.getPrice5()))).doubleValue());
//				sc.setCurrentStock(product.getStock(tcib.getOldId(),
//						targetCargoInfo.getStockType()) + product.getLockCount(tcib.getOldId(),targetCargoInfo.getStockType()));
//				sc.setStockAllArea(product.getStock(
//						tcib.getOldId()) + product.getLockCount(tcib.getOldId()));
//				sc.setStockAllType(product.getStockAllType(
//						targetCargoInfo.getStockType()) + product.getLockCountAllType(targetCargoInfo.getStockType()));
//				sc.setAllStock(product.getStockAll()
//						+ product.getLockCountAll());
//				sc.setStockPrice(product.getPrice5());
//				sc.setAllStockPriceSum((new BigDecimal(
//						sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
//	
//				if (!productservice.addStockCard(sc)) {
//					throw new RuntimeException("出库添加进销存失败！");
//				}
//				
//				cargoProductStock = service.getCargoAndProductStock("cargo_id="+targetCargoInfo.getId() + " and product_id="+ productBean.getProductId());
//				// 货位入库卡片
//				csc = new CargoStockCardBean();
//				csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//				csc.setCode(cargoInfoModel.getOperationCode());
//				csc.setCreateDatetime(DateUtil.getNow());
//				csc.setStockType(targetCargoInfo.getStockType());
//				csc.setStockArea(tarStockArea);
//				csc.setProductId(productBean.getProductId());
//				csc.setStockId(targetCargoInfo.getStorageId());
//				csc.setStockInCount(1);
//				csc.setStockInPriceSum((new BigDecimal(1))
//						.multiply(
//								new BigDecimal(StringUtil.formatDouble2(product
//										.getPrice5()))).doubleValue());
//				csc.setCurrentStock(product.getStock(sc.getStockArea(),
//						sc.getStockType())
//						+ product.getLockCount(sc.getStockArea(),
//								sc.getStockType()));
//				csc.setAllStock(product.getStockAll()
//						+ product.getLockCountAll());
//				csc.setCurrentCargoStock(cargoProductStock.getStockCount()
//						+ cargoProductStock.getStockLockCount());
//				csc.setCargoStoreType(targetCargoInfo.getStoreType());
//				csc.setCargoWholeCode(targetCargoInfo.getWholeCode());
//				csc.setStockPrice(product.getPrice5());
//				csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
//						.multiply(
//								new BigDecimal(StringUtil.formatDouble2(sc
//										.getStockPrice()))).doubleValue());
//				if (!service.addCargoStockCard(csc)) {
//					throw new RuntimeException("出库进销存添加失败！");
//				}
//			}
//			
//		}
//		return null;
//		
//	}
	
	/**
	 * 退货上架作业确认页面的取消，重新扫描
	 */
	public ActionForward resetConfirm(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute(Constants.CACHECAROINO);
		return mapping.findForward("success");
	}
	
	public static String toUtf8String(String s, HttpServletRequest request) {
		String browserType = request.getHeader("user-agent");
		 if (browserType.indexOf("Firefox") > -1) 
        { 
			 String result = "";
           try {
        	 result = new String(s.getBytes("utf-8"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
        } 
        else
        {
        	StringBuffer sb = new StringBuffer();
    		for (int i = 0; i < s.length(); i++) {
    			char c = s.charAt(i);
    			if (c >= 0 && c <= 255) {
    				sb.append(c);
    			} else {
    				byte[] b;
    				try {
    					b = Character.toString(c).getBytes("utf-8");
    				} catch (Exception ex) {
    					b = new byte[0];
    				}
    				for (int j = 0; j < b.length; j++) {
    					int k = b[j];
    					if (k < 0)
    						k += 256;
    					sb.append("%" + Integer.toHexString(k).toUpperCase());
    				}
    			}
    		}
    		return sb.toString();
        }
	}
	
	
	/**
	 * 清空缓存中所有上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward clearCachedProductCode(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		try{
			request.getSession().removeAttribute(RETUPSHELFCODE);
			pw.write("0");
			return null;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.clearCachedProductCode exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return null;
		}
	}
	
	/**
	 * 获取缓存的上架单数量
	 * @return
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月23日 下午2:41:43
	 */
	@SuppressWarnings("rawtypes")
	public ActionForward getCachedProductCodeCount(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter();
		int count = 0;
		try{
			Map retUpShelfMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
			if(retUpShelfMap != null){
				count = retUpShelfMap.size();
			}
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.getCachedProductCodeCount exception", e);
			}
		}
		pw.write(String.valueOf(count));
		return null;
	}
	
	/**
	 * 删除缓存中指定商品条码
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward deleteCacheUpShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		String retUpShelfCode = request.getParameter("retUpShelfCode");
		if(retUpShelfCode == null){
			pw.write("上架单条码不能为空！");
			return null;
		}
		try{
			
			Map retUpShelfMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
			if(retUpShelfMap == null || retUpShelfMap.isEmpty()){
				pw.write("目前缓存中没有任何上架单！");
				return null;
			}
			
			if(!retUpShelfMap.containsKey(retUpShelfCode)){
				pw.write("该上架单不存在或者已经删除！");
				return null;
			}
			
			retUpShelfMap.remove(retUpShelfCode);
			
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:deleteCacheUpShelf exception:", e);
			}
			pw.write("系统异常请联系管理员！");
			return null;
		}
		pw.write("该上架单删除成功！");
		return null;
	}
	
	
	/**
	 * 取消刚刚扫描的上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cancelCacheProduct(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		String retUpShelfCode = request.getParameter("preProductCode");
		try{
			
			Map productCodeMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
			if(productCodeMap == null || productCodeMap.isEmpty()){
				pw.write("当前没有扫描任何上架单！");
				return null;
			}
			
			if(!productCodeMap.containsKey(retUpShelfCode)){
				pw.write("该上架单已经被清除！");
				return null;
			}
			productCodeMap.remove(retUpShelfCode);
			pw.write("0");
			return null;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.cancelCacheProduct exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return null;
		}
	}
	
	
	/**
	 * 缓存扫描的上架单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward cacheProductCode(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		
		//获取扫描的上架单条码
		String retUpShelfCode = request.getParameter("productCode");
		if(retUpShelfCode == null){
			pw.write("上架单条码不能为空！");
			return null;
		}
		if(!retUpShelfCode.contains("HWTS")){
			pw.write("该条码不是退货上架单条码！");
			return null;
		}
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		if(wareArea == -1){
			pw.write("您没有选择地区或者您没有权限生成退货上架汇总单！");
			return null;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		Map retUpShelfMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
		
		try{
			
			CargoOperationBean cocBean = cargoService.getCargoOperation("code='"+StringUtil.toSql(retUpShelfCode)+"'");
			if(cocBean == null){
				pw.write("该退货上架单不存在！");
				return null;
			}
			
			CargoOperationCargoBean cargoOpBean = cargoService.getCargoOperationCargo("oper_id="+cocBean.getId()+" and type=0");
			CargoInfoBean inCi = cargoService.getCargoInfo("whole_code='"+cargoOpBean.getInCargoWholeCode()+"'");
			CargoInfoAreaBean ciaBean = cargoService.getCargoInfoArea("old_id="+wareArea);
			if(inCi.getAreaId()!=ciaBean.getId()){
				pw.write("您汇总的退货上架单(目的)不属于该地区！");
				return null;
			}
			
			CargoInfoBean outCi = cargoService.getCargoInfo("whole_code='"+cargoOpBean.getOutCargoWholeCode()+"'");
			if(outCi.getAreaId()!=ciaBean.getId()){
				pw.write("您汇总的退货上架单(源)不属于该地区！");
				return null;
			}
			
			if(cocBean.getEffectStatus()==CargoOperationBean.EFFECT_STATUS4){
				pw.write("该退货上架单已作业失败！");
				return null;
			}
			
			if(cocBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS2){
				
				if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1){
					pw.write("该退货上架单还没有确认提交！");
					return null;
				}
				
				if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS3
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS4
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS5
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS6){
					pw.write("该退货上架单已汇总！");
					return null;
				}
				
				if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS7
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS8
						|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS9){
					pw.write("该退货上架单已完成！");
					return null;
				}
			}
			
			
			if(retUpShelfMap == null){
				retUpShelfMap = new HashMap();
				retUpShelfMap.put(retUpShelfCode, retUpShelfCode);
				request.getSession().setAttribute(RETUPSHELFCODE, retUpShelfMap);
			}else{
				if(!retUpShelfMap.isEmpty() && retUpShelfMap.size()==30){
					pw.write("汇总单上最多允许30个上架单，目前已经30个，请生成汇总单后再扫描！");
					return null;
				}
				
				if(!retUpShelfMap.containsKey(retUpShelfCode)){
					retUpShelfMap.put(retUpShelfCode, retUpShelfCode);
				}else{
					pw.write("该退货上架单："+retUpShelfCode+"，已经扫描过！");
					return null;
				}
			}
			
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("扫描成功！\r\n");
			strBuilder.append("当前扫描上架单号");
			strBuilder.append(retUpShelfCode);
			strBuilder.append(";0");
			pw.write(strBuilder.toString());
			return null;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:cacheProductCode exception:", e);
			}
			pw.write("扫描失败！当前扫描上架单号:"+retUpShelfCode+"，请联系管理员！");
			return null;
		}finally{
			wareService.releaseAll();
		}
		
	}
	
	
	/**
	 * 生成退货上架汇总单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */						
	public ActionForward generateRetUpShelf(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		response.setCharacterEncoding("utf-8");
		PrintWriter pw = response.getWriter(); 
		WareService wareService = new WareService();
		String upShelfCode = null;
		try{
			synchronized(lock){
				ReturnedPackageService pService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
				voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
				if(user == null){
					pw.write("用户没有登录！");
					return null;
				}
				
				UserGroupBean group = user.getGroup();
				if( !group.isFlag(616)) {
					pw.write("您没有生成退货上架汇总单的权限！");
					return null;
				}
				
				Map retUpShelfMap = (Map) request.getSession().getAttribute(RETUPSHELFCODE);
				if(retUpShelfMap == null || retUpShelfMap.isEmpty()){
					pw.write("没有可以汇总的上架单！");
					return null;
				}
				if(retUpShelfMap.size()<5){
					pw.write("汇总单中至少要包含5个退货上架单！");
					return null;
				}
				if(retUpShelfMap.size()>30){
					pw.write("汇总单中最多包含30个退货上架单！");
					return null;
				}
				upShelfCode = pService.generateUpShelf(retUpShelfMap, user, date);
				if(upShelfCode != null && !upShelfCode.equals("") && !upShelfCode.contains("HWTS")){
					pw.write(upShelfCode);
					return null;
				}
				request.getSession().removeAttribute(RETUPSHELFCODE);
				pw.write(upShelfCode);
				return null;
			}
			
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStoreageAction.generateRetUpShelf exception", e);
			}
//			adminService.rollbackTransaction();
			pw.write("系统异常，请联系管理员！");
			return null;
		}finally{
			wareService.releaseAll();
		}
		
	}
	
	
	
	/**
	 * 扫描生成退货上架汇总单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public ActionForward statisticQualifiedRetProduct(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			request.setAttribute("tip", "用户没有登录！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		UserGroupBean group = user.getGroup();
		if( !group.isFlag(616)) {
			request.setAttribute("tip", "您没有生成退货上架单的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if(request.getSession().getAttribute("productCodeMap")!=null){
			request.getSession().removeAttribute("productCodeMap");
		}
		return mapping.findForward("statisticQualifiedRetProduct");
		
	}
	/**
	 * 	作者： 石远飞
	 * 
	 *	日期：2013-2-26
	 *	
	 *	说明：退货上架汇总单页面的物流员工作业效率排名_ajax请求
	 *
	 */
	public ActionForward ajaxCargoStaffPerformance(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String firstCount = "";
			String oneselfCount = "";
			String ranking ="";
			String productCount="";
			String firstProductCount="";
			int n = 1;
			CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
			if(csBean == null){
				request.setAttribute("tip", "此账号不是物流员工 !");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=6");
			List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=6", -1, -1, " oper_count DESC");
			if(cspBean != null){
				for(int i = 0;i < cspList.size();i++){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(i);
					if(i==0){
						firstCount = bean.getOperCount() + "";
						firstProductCount = bean.getProductCount() + "";
						if(cspBean.getOperCount() >= bean.getOperCount()){
							productCount = cspBean.getProductCount()+"";
							ranking = "排名第" + n ;
							oneselfCount = cspBean.getOperCount()+"";
							break;
						}else{
							n++;
						}
					}else{
						if(cspBean.getOperCount() >= bean.getOperCount()){
							ranking = "排名第" + n ;
							productCount = cspBean.getProductCount()+"";
							oneselfCount = cspBean.getOperCount()+"";
							break;
						}else{
							n++;
						}
					}
				}
			}else{
				if(cspList != null && cspList.size() > 0){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(0);
					firstCount = bean.getOperCount() + "";
					firstProductCount = bean.getProductCount()+"";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}else{
					firstCount = "0";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}
			}
			request.setAttribute("firstCount", firstCount);
			request.setAttribute("productCount", productCount);
			request.setAttribute("oneselfCount", oneselfCount);
			request.setAttribute("firstProductCount", firstProductCount);
			request.setAttribute("ranking", ranking);
		}catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("selection");
	}
	
	/**
	 * 修改退货包裹原因
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ActionForward returnsReasonInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(758)){
			request.setAttribute("tip", "您没有退货原因录入权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		int wareArea = StringUtil.toInt(request.getParameter("wareArea1"));
		String returnsReasonCode = StringUtil.convertNull(request.getParameter("returnsReasonCode"));
		String code = StringUtil.convertNull(request.getParameter("code"));
		WareService wareService = new WareService();
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnedPackageService rpService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized(Constants.LOCK){
				service.getDbOp().startTransaction();
				ReturnsReasonBean rrBean  = claimsVerificationService.getReturnsReasonByCode(StringUtil.toSql(returnsReasonCode));
				if( rrBean == null ) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "退货原因条码有误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				voOrder order = null;
				if( code.equals("") ) {
				} else {
					Object result = claimsVerificationService.getOrderByPcodeOrOcode(code, wareService, service);
					if( result instanceof String ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					} else if ( result instanceof voOrder ) {
						order = (voOrder)result;
						ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + order.getCode() + "'");
						if( rpBean == null ) {
							service.getDbOp().rollbackTransaction();
							request.setAttribute("tip", "该订单不在三方物流交接单中！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						} else {
							if( rpBean.getArea() != wareArea ) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "所选地区不是退货包裹的入库地区！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							} 
							if( !statService.updateReturnedPackage("reason_id = " + rrBean.getId(), "id=" + rpBean.getId())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "更新数据库时操作失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!packageLog.addReturnPackageLog(rpBean.getReasonId() == 0 ? "添加退货原因" : "修改退货原因", user, order.getCode())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "添加退货日志失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							request.setAttribute("tip", "操作成功!");
						}
					}
				}
				service.getDbOp().commitTransaction();
				service.getDbOp().getConn().setAutoCommit(true);
			}
		} catch(Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.returnsReasonInput exception", e);
			}
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", e.getMessage());
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally {
			statService.releaseAll();
		}
		request.setAttribute("url", request.getContextPath()+"/admin/cargo/returnsReasonInput.jsp");
		return mapping.findForward("tip");
	}
	
	/**
	 * 预查退货包裹 是否已经有原因了
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void getDoReturnedPackageHasReason(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		String code = StringUtil.convertNull(request.getParameter("code"));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ReturnedPackageService rpService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ClaimsVerificationService claimsVerificationService = new ClaimsVerificationService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String json = "{ ";
		try {
			synchronized(Constants.LOCK){
				voOrder order = null;
				if( code.equals("") ) {
					json += "status:'failure', tip:'" + "请填写订单编号或包裹单号!" + "'";
				} else {
					Object result = claimsVerificationService.getOrderByPcodeOrOcode(code, wareService, service);
					if( result instanceof String ) {
						json += "status:'failure', tip:'" + result + "'";
					} else if ( result instanceof voOrder ) {
						order = (voOrder)result;
						ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + order.getCode() + "'");
						if( rpBean == null ) {
							json += "status:'failure', tip:'" + "该订单不在三方物流交接单中！" + "'";
						} else {
							if( rpBean.getReasonId() != 0 ) {
								json += "status:'success', hasReason:'1'";
							} else {
								json += "status:'success', hasReason:'0'";
							}
						}
					}
				}
				json += " }";
				response.setContentType("text/html;charset=UTF-8");
				response.setCharacterEncoding( "UTF-8");
				response.getWriter().write(json);
				return;
			}
		} catch(Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.returnsReasonInput exception", e);
			}
		}finally {
			statService.releaseAll();
		}
	}
	
	public ActionForward ReturnPackageLog(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		StatService service = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, db);
		ReturnPackageLogService packLogService = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String condition = "";
		String packageCode = "";
		try {
			ResultSet rs = service.getDbOp().executeQuery("select order_code,package_code from returned_package where order_code='"+orderCode+"' or package_code='"+orderCode+ "'");
			if(rs.next()) {
				condition = "order_code='"+StringUtil.toSql(rs.getString(1))+"'";
				packageCode = rs.getString(2);
			} else {
				condition = "1=2";
			}
			List list = packLogService.getReturnPackageLogList(condition,
					-1, -1, "oper_time asc");
			request.setAttribute("ReturnPackageLogList", list);
			request.setAttribute("packageCode", packageCode);
			return mapping.findForward("ReturnPackageLog");
		} catch ( Exception e ) {
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnStorageAction.returnPackageLog exception", e);
			}
			request.setAttribute("tip", "查询退货日志异常");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			if(packLogService != null){
				packLogService.releaseAll();
			}

		}
	}
	
}
