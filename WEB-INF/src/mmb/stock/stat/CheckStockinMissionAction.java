package mmb.stock.stat;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.contract.service.ContractService;
import mmb.finance.service.InitVerificationService;
import mmb.finance.service.impl.InitVerificationServiceImpl;
import mmb.finance.stat.FinanceBuyPayBean;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.stock.stat.formbean.CheckStockinMissionFormBean;
import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;
import com.mmb.framework.support.SpringHandler;
import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductLineCatalog;
import adultadmin.action.vo.voProductSupplier;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.PageUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

@Controller
@RequestMapping("/admin")
public class CheckStockinMissionAction extends DispatchAction {

	private static Object checkStockinLock = new Object();
	private static Object endCheckLock = new Object();//复录专用锁
	private final Log logger = LogFactory
			.getLog(CheckStockinMissionAction.class);
	private StatService statService = null;

	/**
	 * @name 获得不合格品接收明细
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getUnqualifiedStorageDetailInfo")
	public String getUnqualifiedStorageDetailInfo(HttpServletRequest request,
			HttpServletResponse response) {

		int pageIndex = -1;
		int countPerPage = 20;
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String sProductLine = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productLine")));
		String stockinTime = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("stockinTime")));
		String exchangeTime = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("exchangeTime")));
		String exchangeCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("exchangeCode")));
		String buyStockCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("buyStockCode")));
		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		String productName = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productName")));
		String checkStockMissionCode = StringUtil.toSql(StringUtil
				.convertNull(request.getParameter("checkStockMissionCode")));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
		List checkStockinUnqualifiedList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StockinUnqualifiedService sus = new StockinUnqualifiedService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StringBuilder param = new StringBuilder();
		StringBuilder parentId1 = new StringBuilder();
		StringBuilder parentId2 = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		// 根据产品线先找符合的id
		try {
			// 查看用户有权限的库地区， 只查询他有权限的库地区,或以前没有地区的商品信息
			List<String> cdaList = CargoDeptAreaService
					.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int x = cdaList.size();
			for (int i = 0; i < x; i++) {
				availAreaIds += "," + cdaList.get(i);
			}
			if (!productCode.equals("") || !productName.equals("")
					|| !sProductLine.equals("") || !sProductLine.equals("0")) {
				sql.append("select csu.* from product p, check_stockin_unqualified csu where p.id = csu.product_id");
				sqlCount.append("select count(csu.id) from product p, check_stockin_unqualified csu where p.id = csu.product_id");
			} else {
				sql.append("select csu.* from check_stockin_unqualified csu where csu.id<>0");
				sqlCount.append("select count(csu.id) from check_stockin_unqualified csu where csu.id<>0");
			}

			if (!productCode.equals("")) {
				sql.append(" and").append(" p.code='" + productCode + "'");
				sqlCount.append(" and").append(" p.code='" + productCode + "'");

				param.append("&").append("productCode=" + productCode);
			}

			if (!productName.equals("")) {
				sql.append(" and").append(
						" p.oriname like '" + productName + "%" + "'");
				sqlCount.append(" and").append(
						" p.oriname='" + productName + "%" + "'");

				param.append("&").append("productName=" + productName);
			}
			if (wareArea == -1) {
				sql.append(" and")
						.append(" csu.area in (" + availAreaIds + ")");
				sqlCount.append(" and").append(
						" csu.area in (" + availAreaIds + ")");

				param.append("&").append("wareArea=" + wareArea);
			} else {
				sql.append(" and").append(" csu.area=" + wareArea);
				sqlCount.append(" and").append(" csu.area=" + wareArea);

				param.append("&").append("wareArea=" + wareArea);
			}

			if (sProductLine != null && !sProductLine.equals("0")
					&& !sProductLine.equals("")) {
				List productLineList2 = sus.getProductLineCatalogList(
						"product_line_id = " + StringUtil.toSql(sProductLine),
						-1, -1, null);
				for (int i = 0; i < productLineList2.size(); i++) {
					ProductLineCatalogBean plcb = (ProductLineCatalogBean) productLineList2
							.get(i);
					if (plcb.getCatalogType() == 1) {
						parentId1.append(plcb.getCatalogId()).append(",");
					}
					if (plcb.getCatalogType() == 2) {
						parentId2.append(plcb.getCatalogId()).append(",");
					}
				}
				param.append("&");
				param.append("productLine=").append(sProductLine);
			}

			if (parentId1.length() > 0 || parentId2.length() > 0) {
				String sParentId1 = "";
				String sParentId2 = "";
				if (parentId1.length() > 0) {
					sParentId1 = parentId1.toString().substring(0,
							parentId1.length() - 1);
				}
				if (parentId2.length() > 0) {
					sParentId2 = parentId2.toString().substring(0,
							parentId2.length() - 1);
				}
				// checkUnqualifiedIds =
				// sus.getUnqualifiedByProductLine(sParentId1, sParentId2);
				String parentIdCondition = "";
				if (sParentId1.length() > 0) {
					parentIdCondition += " and (p.parent_id1 in (" + sParentId1
							+ ")";
				}
				if (sParentId2.length() > 0) {
					if (parentIdCondition.length() > 0) {
						parentIdCondition += " or p.parent_id2 in ("
								+ sParentId2 + ")";
					} else {
						parentIdCondition += " and (p.parent_id2 in ("
								+ sParentId2 + ")";
					}
				}
				if (parentIdCondition.length() > 0) {
					parentIdCondition += ")";
				}
				if (parentIdCondition.length() != 0) {
					sql.append(parentIdCondition);
					sqlCount.append(parentIdCondition);
				}

			}

			// 入库时间条件
			if (stockinTime != null && !stockinTime.equals("")) {
				String stockinTime1 = stockinTime + " 00:00:00";
				String stockinTime2 = stockinTime + " 23:59:59";
				sql.append(" and csu.stockin_date_time between '")
						.append(StringUtil.toSql(stockinTime1))
						.append("' and '")
						.append(StringUtil.toSql(stockinTime2)).append("'");
				param.append("&");
				sqlCount.append(" and csu.stockin_date_time between '")
						.append(StringUtil.toSql(stockinTime1))
						.append("' and '")
						.append(StringUtil.toSql(stockinTime2)).append("'");
				param.append("&");
				param.append("stockinTime=").append(stockinTime);
			}
			// 调拨单时间条件
			if (exchangeTime != null && !exchangeTime.equals("")) {
				String exchangeTime1 = exchangeTime + " 00:00:00";
				String exchangeTime2 = exchangeTime + " 23:59:59";
				sql.append(" and csu.exchange_date_time between '")
						.append(StringUtil.toSql(exchangeTime1))
						.append("' and '")
						.append(StringUtil.toSql(exchangeTime2)).append("'");
				param.append("&");
				sqlCount.append(" and csu.exchange_date_time between '")
						.append(StringUtil.toSql(exchangeTime1))
						.append("' and '")
						.append(StringUtil.toSql(exchangeTime2)).append("'");
				param.append("&");
				param.append("exchangeTime=").append(exchangeTime);
			}
			// 调拨单号条件
			if (exchangeCode != null && !exchangeCode.equals("")) {
				sql.append(" and csu.exchange_code='")
						.append(StringUtil.toSql(exchangeCode)).append("'");
				param.append("&");
				sqlCount.append(" and csu.exchange_code='")
						.append(StringUtil.toSql(exchangeCode)).append("'");
				param.append("&");
				param.append("exchangeCode=").append(exchangeCode);
			}
			// 预计单条件
			if (buyStockCode != null && !buyStockCode.equals("")) {
				sql.append(" and csu.buy_stock_code='")
						.append(StringUtil.toSql(buyStockCode)).append("'");
				param.append("&");
				sqlCount.append(" and csu.buy_stock_code='")
						.append(StringUtil.toSql(buyStockCode)).append("'");
				param.append("&");
				param.append("buyStockCode=").append(buyStockCode);
			}
			if (!checkStockMissionCode.equals("")) {
				CheckStockinMissionBean csmb = null;
				csmb = statService.getCheckStockinMission("code = '"
						+ checkStockMissionCode + "'");
				if (csmb != null) {
					sql.append(" and").append(" csu.mission_id =")
							.append(csmb.getId());
					sqlCount.append(" and").append(" csu.mission_id =")
							.append(csmb.getId());
				} else {
					sql.append(" and").append(" csu.mission_id = 0");
					sqlCount.append(" and").append(" csu.mission_id = 0");
				}
				param.append("&");
				param.append("checkStockinCode=").append(checkStockMissionCode);
			}

			PagingBean paging = null;
			int totalCount = sus.getCheckStockinUnqualifiedCount(sqlCount
					.toString());
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			// checkStockinUnqualifiedList =
			// statService.getCheckStockinUnqualifiedList(condition1,
			// paging.getCurrentPageIndex()*countPerPage, countPerPage,
			// "exchange_date_time desc, status asc");
			checkStockinUnqualifiedList = sus.getCheckStockinUnqualifiedList(
					sql.toString(),
					paging.getCurrentPageIndex() * countPerPage, countPerPage,
					"exchange_date_time desc, status asc");
			paging.setPrefixUrl("getUnqualifiedStorageDetailInfo.mmx?"
					+ param.toString());

			for (int i = 0; i < checkStockinUnqualifiedList.size(); i++) {
				CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) checkStockinUnqualifiedList
						.get(i);
				csub.setProduct(wareService.getProduct(csub.getProductId()));

			}

			request.setAttribute("list", checkStockinUnqualifiedList);
			request.setAttribute("paging", paging);
			// 得到产品线列表
			String productLinePermission = ProductLinePermissionCache
					.getProductLineIds(user);
			productLinePermission = "product_line.id in ("
					+ productLinePermission + ")";
			List productLineList = wareService
					.getProductLineList(productLinePermission);
			request.setAttribute("productLineList", productLineList);

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"method getUnqualifiedStorageDetailInfo exception", e);
			}
		} finally {
			statService.releaseAll();
		}
		return "/admin/cargo/unqualifiedStorageDetailInfo";
	}

	/**
	 * @name 导出不合格品接收明细
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 */
	public void exportUnqualifiedStorageDetailInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		Date date = new Date();
		String requestTime = DateUtil.formatDate(date, "yyyy-MM-dd");
		List list = new ArrayList();
		String condition = "";
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(629);
		if (!permission) {
			request.setAttribute("tip", "您没有不合格品接收明细表导出与打印的权限！");
			request.setAttribute("result", "failure");
		}
		String[] ids = request.getParameterValues("exportIds");
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			if (ids != null && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					condition += ids[i] + ",";
				}
				condition = condition.substring(0, (condition.length() - 1));
				list = statService.getCheckStockinUnqualifiedList("id in ("
						+ condition + ")", -1, -1, "exchange_date_time desc");
			} else {
				list = statService.getCheckStockinUnqualifiedList("status = 0",
						-1, -1, "exchange_date_time desc");
			}

			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) list
							.get(i);
					csub.setProduct(wareService.getProduct(csub.getProductId()));
				}
				// 修改导出状态
				statService.getDbOp().startTransaction();
				for (int i = 0; i < list.size(); i++) {
					CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) list
							.get(i);
					statService.updateCheckStockinUnqualified("status = "
							+ CheckStockinUnqualifiedBean.EXPORT, "id = "
							+ csub.getId());
				}
				statService.getDbOp().commitTransaction();
				StockinUnqualifiedService sus = new StockinUnqualifiedService(
						IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
				HSSFWorkbook hwb = null;
				hwb = sus.exportCheckStockinUnqualifiedInfo(list);
				response.reset();
				response.addHeader(
						"Content-Disposition",
						"attachment;filename="
								+ toUtf8String("不合格品接收明细" + requestTime,
										request) + ".xls");
				response.setContentType("application/msxls");
				response.setCharacterEncoding("utf-8");
				hwb.write(response.getOutputStream());

			} else {
				response.reset();
				response.setCharacterEncoding("utf-8");
				response.addHeader(
						"Content-Disposition",
						"attachment;filename="
								+ toUtf8String("不合格品接收明细" + requestTime,
										request) + ".xls");
				response.setContentType("application/msxls");
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"method exportUnqualifiedStorageDetailInfo exception",
						e);
			}
		} finally {
			statService.releaseAll();
		}
	}

	/**
	 * @name 打印不合格品明细
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward printUnqualifiedStorageDetailInfo(
			ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		String[] ids = request.getParameterValues("exportIds");
		List list = new ArrayList();
		String condition = "";
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(629);
		if (!permission) {
			request.setAttribute("tip", "您没有不合格品接收明细表导出与打印的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
			WareService wareService = new WareService();
			StatService statService = ServiceFactory.createStatServiceStat(
					IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try {
				if (ids != null && ids.length > 0) {
					for (int i = 0; i < ids.length; i++) {
						condition += ids[i] + ",";
					}
					condition = condition.substring(0, (condition.length() - 1));
					list = statService.getCheckStockinUnqualifiedList("id in ("
							+ condition + ")", -1, -1, "exchange_date_time desc");
				} else {
					list = statService.getCheckStockinUnqualifiedList("status = 0",
							-1, -1, "exchange_date_time desc");
				}
	
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) list
								.get(i);
						csub.setProduct(wareService.getProduct(csub.getProductId()));
					}
	
					// 修改导出状态
					statService.getDbOp().startTransaction();
					for (int i = 0; i < list.size(); i++) {
						CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) list
								.get(i);
						statService.updateCheckStockinUnqualified("status = "
								+ CheckStockinUnqualifiedBean.EXPORT, "id = "
								+ csub.getId());
					}
					statService.getDbOp().commitTransaction();
					request.setAttribute("list", list);
				} else {
					request.setAttribute("tip", "没有勾选要打印项且已没有还未导出的信息!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error(
							"method printUnqualifiedStorageDetailInfo exception", e);
				}
			} finally {
				statService.releaseAll();
			}

			return mapping.findForward("printUnqualifiedInfo");
		
	}

	/**
	 * 得到质检结果报表
	 */
	public ActionForward getCheckReportInfo(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		int pageIndex = -1;
		int countPerPage = 20;
		List checkBatchList = new ArrayList();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(628);
		if (!permission) {
			request.setAttribute("tip", "您没有质检报表的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String completeTime1 = StringUtil.convertNull(request
				.getParameter("completeTime1"));
		String completeTime2 = StringUtil.convertNull(request
				.getParameter("completeTime2"));
		String sProductLine = StringUtil.convertNull(request
				.getParameter("productLine"));
		String buyStockinCode = StringUtil.convertNull(request
				.getParameter("buyStockinCode"));
		String productCode = StringUtil.convertNull(request
				.getParameter("productCode"));
		String checkStockinMissionCode = StringUtil.convertNull(request
				.getParameter("checkStockinMissionCode"));
		String productName = StringUtil.convertNull(request
				.getParameter("productName"));
		String supplierName = StringUtil.convertNull(request
				.getParameter("supplierName"));
		int unqualifyNumber1 = ProductWarePropertyService.toInt(request
				.getParameter("unqualifyNumber1"));
		int unqualifyNumber2 = ProductWarePropertyService.toInt(request
				.getParameter("unqualifyNumber2"));
		float unqualifyRate1 = ProductWarePropertyService.toInt(request
				.getParameter("unqualifyRate1"));
		float unqualifyRate2 = ProductWarePropertyService.toInt(request
				.getParameter("unqualifyRate2"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		checkStockinMissionCode = StringUtil.convertNull(request
				.getParameter("checkStockinMissionCode"));
		pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
		// 接收参数完毕
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		StockinUnqualifiedService sus = new StockinUnqualifiedService(IBaseService.CONN_IN_SERVICE, dbOp);
		StringBuilder param = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder parentId1 = new StringBuilder();
		StringBuilder parentId2 = new StringBuilder();
		try {

			List<String> cdaList = CargoDeptAreaService
					.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int t = cdaList.size();
			for (int i = 0; i < t; i++) {
				availAreaIds += "," + cdaList.get(i);
			}
			/*
			 * condition.append("status = " +
			 * CheckStockinMissionBatchBean.STATUS3);
			 */
			if (!productCode.equals("") || !productName.equals("")
					|| (!sProductLine.equals("") && !sProductLine.equals("0"))) {
				sql.append("select csmb.* from check_stockin_mission_batch csmb, check_stockin_mission csm, product p where p.id = csmb.product_id and csmb.mission_id = csm.id and csmb.status="
						+ CheckStockinMissionBatchBean.STATUS3);
				sqlCount.append("select count(csmb.id) from check_stockin_mission_batch csmb, check_stockin_mission csm, product p where p.id = csmb.product_id and csmb.mission_id = csm.id and csmb.status="
						+ CheckStockinMissionBatchBean.STATUS3);
			} else {
				sql.append("select csmb.* from check_stockin_mission_batch csmb, check_stockin_mission csm where csmb.status="
						+ CheckStockinMissionBatchBean.STATUS3
						+ " and csmb.mission_id = csm.id");
				sqlCount.append("select count(csmb.id) from check_stockin_mission_batch csmb, check_stockin_mission csm where  csmb.status="
						+ CheckStockinMissionBatchBean.STATUS3
						+ " and csmb.mission_id = csm.id");
			}

			if (wareArea == -1) {
				sql.append(" and").append(
						" csm.ware_area in (" + availAreaIds + ")");
				sqlCount.append(" and").append(
						" csm.ware_area in (" + availAreaIds + ")");

				param.append("&").append("wareArea=" + wareArea);
			} else {
				sql.append(" and").append(" csm.ware_area=" + wareArea);
				sqlCount.append(" and").append(" csm.ware_area=" + wareArea);

				param.append("&").append("wareArea=" + wareArea);
			}

			// 产品编号
			if (!productCode.equals("")) {
				sql.append(" and").append(" p.code='" + productCode + "'");
				sqlCount.append(" and").append(" p.code='" + productCode + "'");

				param.append("&").append("productCode=").append(productCode);

			}
			// 产品原名称
			if (!productName.equals("")) {
				sql.append(" and").append(
						" p.oriname like '" + productName + "%" + "'");
				sqlCount.append(" and").append(
						" p.oriname like '" + productName + "%" + "'");

				param.append("&").append("productName=").append(productName);
			}
			// 产品线
			if (sProductLine != null && !sProductLine.equals("0")
					&& !sProductLine.equals("")) {
				// 在没有被其他条件限制到无结果的时候再查

				List productLineList2 = sus.getProductLineCatalogList(
						"product_line_id = " + sProductLine, -1, -1, null);
				for (int i = 0; i < productLineList2.size(); i++) {
					ProductLineCatalogBean plcb = (ProductLineCatalogBean) productLineList2
							.get(i);
					if (plcb.getCatalogType() == 1) {
						parentId1.append(plcb.getCatalogId()).append(",");
					}
					if (plcb.getCatalogType() == 2) {
						parentId2.append(plcb.getCatalogId()).append(",");
					}
				}
				param.append("&");
				param.append("productLine=").append(sProductLine);
			}
			// 根据产品线得parentId， 修饰parentId
			if (parentId1.length() > 0 || parentId2.length() > 0) {
				String sParentId1 = "";
				String sParentId2 = "";
				if (parentId1.length() > 0) {
					sParentId1 = parentId1.toString().substring(0,
							parentId1.length() - 1);
				}
				if (parentId2.length() > 0) {
					sParentId2 = parentId2.toString().substring(0,
							parentId2.length() - 1);
				}
				String parentIdCondition = "";
				if (sParentId1.length() > 0) {
					parentIdCondition += " and (p.parent_id1 in (" + sParentId1
							+ ")";
				}
				if (sParentId2.length() > 0) {
					if (parentIdCondition.length() > 0) {
						parentIdCondition += " or p.parent_id2 in ("
								+ sParentId2 + ")";
					} else {
						parentIdCondition += " and (p.parent_id2 in ("
								+ sParentId2 + ")";
					}
				}
				if (parentIdCondition.length() > 0) {
					parentIdCondition += ")";
				}
				if (parentIdCondition.length() != 0) {
					sql.append(parentIdCondition);
					sqlCount.append(parentIdCondition);
				}
			}

			// 结束时间
			if (completeTime1 != null && completeTime2 != null
					&& !completeTime1.equals("") && !completeTime2.equals("")) {
				String completeTimeStart = completeTime1 + " 00:00:00";
				String completeTimeEnd = completeTime2 + " 23:59:59";
				sql.append(" and").append(" csmb.complete_date_time between '")
						.append(completeTimeStart).append("' and '")
						.append(completeTimeEnd).append("'");
				sqlCount.append(" and")
						.append(" csmb.complete_date_time between '")
						.append(completeTimeStart).append("' and '")
						.append(completeTimeEnd).append("'");
				param.append("&");
				param.append("completeTime1=").append(completeTime1)
						.append("&").append("completeTime2=")
						.append(completeTime2);
			}
			// 预计单号
			if (buyStockinCode != null && !buyStockinCode.equals("")) {
				List<CheckStockinMissionBean> csmbList = null;
				csmbList = statService.getCheckStockinMissionList(
						"buy_stockin_code = '" + buyStockinCode + "'", -1, -1,
						"id asc");
				if (csmbList.size() != 0) {
					String ids = "-1,";
					int x = csmbList.size();
					for (int i = 0; i < x; i++) {
						CheckStockinMissionBean csmBean = csmbList.get(i);
						if (i == (x - 1)) {
							ids += csmBean.getId();
						} else {
							ids += csmBean.getId() + ",";
						}

					}
					sql.append(" and").append(" csmb.mission_id in (")
							.append(ids).append(")");
					sqlCount.append(" and").append(" csmb.mission_id in (")
							.append(ids).append(")");
				} else {
					sql.append(" and").append(" csmb.mission_id = 0");
					sqlCount.append(" and").append(" csmb.mission_id = 0");
				}
				param.append("&");
				param.append("buyStockinCode=").append(buyStockinCode);
			}

			// 任务单编号
			if (checkStockinMissionCode != null
					&& !checkStockinMissionCode.equals("")) {
				CheckStockinMissionBean csmb = null;
				csmb = statService.getCheckStockinMission("code = '"
						+ checkStockinMissionCode + "'");
				if (csmb != null) {
					sql.append(" and").append(" csmb.mission_id =")
							.append(csmb.getId());
					sqlCount.append(" and").append(" csmb.mission_id =")
							.append(csmb.getId());
				} else {
					sql.append(" and").append(" csmb.mission_id = 0");
					sqlCount.append(" and").append(" csmb.mission_id = 0");
				}
				param.append("&");
				param.append("checkStockinCode=").append(
						checkStockinMissionCode);
			}
			// 供应商名称
			if (!supplierName.equals("")) {
				sql.append(" and").append(" csmb.supplier_name like '")
						.append(supplierName).append("%'");
				sqlCount.append(" and").append(" csmb.supplier_name like '")
						.append(supplierName).append("%'");
				param.append("&");
				param.append("completeTime1=").append(completeTime1)
						.append("&").append("completeTime2=")
						.append(completeTime2);
			}
			// 不合格数
			if (unqualifyNumber1 != -1 && unqualifyNumber2 != -1) {
				sql.append(" and")
						.append(" (csmb.check_count - csmb.qualified_count) between ")
						.append(unqualifyNumber1).append(" and ")
						.append(unqualifyNumber2);
				sqlCount.append(" and")
						.append(" (csmb.check_count - csmb.qualified_count) between ")
						.append(unqualifyNumber1).append(" and ")
						.append(unqualifyNumber2);
				param.append("&");
				param.append("unqualifyNumber1=").append(unqualifyNumber1)
						.append("&").append("unqualifyNumber2=")
						.append(unqualifyNumber2);
			}
			// 不合格率
			if (unqualifyRate1 != -1 && unqualifyRate1 >= 0
					&& unqualifyRate1 <= 100 && unqualifyRate2 >= 0
					&& unqualifyRate2 <= 100 && unqualifyRate2 != -1) {
				sql.append(" and")
						.append(" csmb.check_count != 0 and (((csmb.check_count - csmb.qualified_count)/check_count) * 100) between ")
						.append(unqualifyRate1).append(" and ")
						.append(unqualifyRate2);
				sqlCount.append(" and")
						.append(" csmb.check_count != 0 and (((csmb.check_count - csmb.qualified_count)/check_count) * 100) between ")
						.append(unqualifyRate1).append(" and ")
						.append(unqualifyRate2);
				param.append("&");
				param.append("unqualifyRate1=").append(unqualifyRate1)
						.append("&").append("unqualifyRate2=")
						.append(unqualifyRate2);
			}

			PagingBean paging = null;
			int totalCount = sus.getCheckStockinMissionBatchCount(sqlCount
					.toString());
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			checkBatchList = sus.getCheckStockinMissionBatchList(
					sql.toString(),
					paging.getCurrentPageIndex() * countPerPage, countPerPage,
					"complete_date_time desc");
			paging.setPrefixUrl("checkStockinMissionAction.do?method=getCheckReportInfo"
					+ param.toString());

			if (checkBatchList != null) {
				for (int i = 0; i < checkBatchList.size(); i++) {
					CheckStockinMissionBatchBean csmbb = (CheckStockinMissionBatchBean) checkBatchList
							.get(i);
					csmbb.setProduct(wareService.getProduct(csmbb
							.getProductId()));
					// 加产品线名称
					if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ csmbb.getProduct().getParentId1()
									+ " and product_line_catalog.catalog_type=1") != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ csmbb.getProduct().getParentId1())
								.getName();
						csmbb.getProduct().setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ csmbb.getProduct().getParentId2()
									+ " and product_line_catalog.catalog_type=2") != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ csmbb.getProduct().getParentId2())
								.getName();
						csmbb.getProduct().setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ csmbb.getProduct().getParentId3()
									+ " and product_line_catalog.catalog_type=3") != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ csmbb.getProduct().getParentId3())
								.getName();
						csmbb.getProduct().setProductLineName(productLineName);
					} else {
						csmbb.getProduct().setProductLineName("");
					}
					// 预计到货量
					BuyStockProductBean sh = service
							.getBuyStockProduct("product_id = "
									+ csmbb.getProductId()
									+ " and buy_stock_id = "
									+ csmbb.getBuyStockinId());
					if (sh != null) {
						csmbb.setBuyStockProduct(sh);
					} else {
						sh = new BuyStockProductBean();
						sh.setBuyCount(0);
						csmbb.setBuyStockProduct(sh);
					}
					CheckStockinMissionBean csmBean = statService
							.getCheckStockinMission("id = "
									+ csmbb.getMissionId());
					csmbb.setCheckStockinMission(csmBean);

					List unqualifiedList = statService
							.getCheckStockinUnqualifiedList(
									"mission_batch_id = " + csmbb.getId(), -1,
									-1, "id asc");
					if (unqualifiedList != null && unqualifiedList.size() > 0) {
						String temp = "";
						int unqualifiedCount = 0;
						for (int j = 0; j < unqualifiedList.size(); j++) {
							CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) unqualifiedList
									.get(j);
							temp += csub.getRemark();
							if (j <= (unqualifiedList.size() - 1)) {
								temp += "<br>";
							}
							unqualifiedCount += csub.getCount();
						}
						csmbb.setUnqualifiedReasons(temp);
						csmbb.setUnqualifiedNumber(unqualifiedCount);
					} else {
						csmbb.setUnqualifiedReasons("");
						csmbb.setUnqualifiedNumber(0);
					}

				}
			}
			request.setAttribute("checkBatchList", checkBatchList);
			request.setAttribute("paging", paging);

			String productLinePermission = ProductLinePermissionCache
					.getProductLineIds(user);
			productLinePermission = "product_line.id in ("
					+ productLinePermission + ")";
			List productLineList = wareService
					.getProductLineList(productLinePermission);
			request.setAttribute("productLineList", productLineList);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method getCheckReportInfo exception", e);
			}
		} finally {
			statService.releaseAll();
			wareService.releaseAll();
		}

		return mapping.findForward("checkReportInfo");
	}

	/**
	 * @name 导出质检报表
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 */
	public void exportCheckReportInfo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		Date date = new Date();
		String fileDate = DateUtil.formatDate(date, "yyyyMMdd");
		String sProductLine = "";
		String completeTime = "";
		List checkBatchList = new ArrayList();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(628);
		if (!permission) {
			request.setAttribute("tip", "您没有质检报表的权限！");
			request.setAttribute("result", "failure");
		}
		sProductLine = StringUtil.convertNull(request
				.getParameter("productLine2"));
		completeTime = StringUtil.convertNull(request
				.getParameter("completeTime"));
		StringBuilder sql = new StringBuilder();
		StringBuilder parentId1 = new StringBuilder();
		StringBuilder parentId2 = new StringBuilder();
		String condition1 = null;
		String checkBatchIds = "";
		boolean hasProductLine = false;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StockinUnqualifiedService sus = new StockinUnqualifiedService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			// 产品线 s
			if (!sProductLine.equals("") && !sProductLine.equals("0")) {
				sql.append("select csmb.* from check_stockin_mission_batch csmb, product p where p.id = csmb.product_id and csmb.status="
						+ CheckStockinMissionBatchBean.STATUS3);
			} else {
				sql.append("select csmb.* from check_stockin_mission_batch csmb where csmb.status="
						+ CheckStockinMissionBatchBean.STATUS3);
			}
			if (sProductLine != null && !sProductLine.equals("0")
					&& !sProductLine.equals("")) {
				List productLineList2 = sus.getProductLineCatalogList(
						"product_line_id = " + sProductLine, -1, -1, null);
				for (int i = 0; i < productLineList2.size(); i++) {
					ProductLineCatalogBean plcb = (ProductLineCatalogBean) productLineList2
							.get(i);
					if (plcb.getCatalogType() == 1) {
						parentId1.append(plcb.getCatalogId()).append(",");
					}
					if (plcb.getCatalogType() == 2) {
						parentId2.append(plcb.getCatalogId()).append(",");
					}
				}
				hasProductLine = true;
			}
			if (parentId1.length() > 0 || parentId2.length() > 0) {
				String sParentId1 = "";
				String sParentId2 = "";
				if (parentId1.length() > 0) {
					sParentId1 = parentId1.toString().substring(0,
							parentId1.length() - 1);
				}
				if (parentId2.length() > 0) {
					sParentId2 = parentId2.toString().substring(0,
							parentId2.length() - 1);
				}
				checkBatchIds = sus.getCheckBatchByProductLine(sParentId1,
						sParentId2);
			}
			// 是否有符合产品线的商品
			// 产品线
			if (sProductLine != null && !sProductLine.equals("0")
					&& !sProductLine.equals("")) {
				// 在没有被其他条件限制到无结果的时候再查

				List productLineList2 = sus.getProductLineCatalogList(
						"product_line_id = " + sProductLine, -1, -1, null);
				for (int i = 0; i < productLineList2.size(); i++) {
					ProductLineCatalogBean plcb = (ProductLineCatalogBean) productLineList2
							.get(i);
					if (plcb.getCatalogType() == 1) {
						parentId1.append(plcb.getCatalogId()).append(",");
					}
					if (plcb.getCatalogType() == 2) {
						parentId2.append(plcb.getCatalogId()).append(",");
					}
				}
			}
			// 根据产品线得parentId， 修饰parentId
			if (parentId1.length() > 0 || parentId2.length() > 0) {
				String sParentId1 = "";
				String sParentId2 = "";
				if (parentId1.length() > 0) {
					sParentId1 = parentId1.toString().substring(0,
							parentId1.length() - 1);
				}
				if (parentId2.length() > 0) {
					sParentId2 = parentId2.toString().substring(0,
							parentId2.length() - 1);
				}
				String parentIdCondition = "";
				if (sParentId1.length() > 0) {
					parentIdCondition += " and (p.parent_id1 in (" + sParentId1
							+ ")";
				}
				if (sParentId2.length() > 0) {
					if (parentIdCondition.length() > 0) {
						parentIdCondition += " or p.parent_id2 in ("
								+ sParentId2 + ")";
					} else {
						parentIdCondition += " and (p.parent_id2 in ("
								+ sParentId2 + ")";
					}
				}
				if (parentIdCondition.length() > 0) {
					parentIdCondition += ")";
				}
				if (parentIdCondition.length() != 0) {
					sql.append(parentIdCondition);
				}
			}

			// 入库时间条件
			if (completeTime != null && !completeTime.equals("")) {
				String stockinTime1 = completeTime + " 00:00:00";
				String stockinTime2 = completeTime + " 23:59:59";
				sql.append(" and").append(" csmb.complete_date_time between '")
						.append(stockinTime1).append("' and '")
						.append(stockinTime2).append("'");
				fileDate = completeTime.replaceAll("-", "");
			}

			checkBatchList = sus.getCheckStockinMissionBatchList(
					sql.toString(), -1, -1, "complete_date_time desc");

			if (checkBatchList != null) {
				for (int i = 0; i < checkBatchList.size(); i++) {
					CheckStockinMissionBatchBean csmbb = (CheckStockinMissionBatchBean) checkBatchList
							.get(i);
					csmbb.setProduct(wareService.getProduct(csmbb
							.getProductId()));
					// 加产品线名称
					if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ csmbb.getProduct().getParentId1()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ csmbb.getProduct().getParentId1())
								.getName();
						csmbb.getProduct().setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ csmbb.getProduct().getParentId2()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ csmbb.getProduct().getParentId2())
								.getName();
						csmbb.getProduct().setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ csmbb.getProduct().getParentId3()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ csmbb.getProduct().getParentId3())
								.getName();
						csmbb.getProduct().setProductLineName(productLineName);
					} else {
						csmbb.getProduct().setProductLineName("");
					}
					// 预计到货量
					BuyStockProductBean sh = service
							.getBuyStockProduct("product_id = "
									+ csmbb.getProductId()
									+ " and buy_stock_id = "
									+ csmbb.getBuyStockinId());
					if (sh != null) {
						csmbb.setBuyStockProduct(sh);
					} else {
						sh = new BuyStockProductBean();
						sh.setBuyCount(0);
						csmbb.setBuyStockProduct(sh);
					}

					csmbb.setCheckStockinMission(statService
							.getCheckStockinMission("id = "
									+ csmbb.getMissionId()));
					List unqualifiedList = statService
							.getCheckStockinUnqualifiedList(
									"mission_batch_id = " + csmbb.getId(), -1,
									-1, "id asc");
					if (unqualifiedList != null && unqualifiedList.size() > 0) {
						String temp = "";
						int unqualifiedCount = 0;
						for (int j = 0; j < unqualifiedList.size(); j++) {
							CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) unqualifiedList
									.get(j);
							temp += csub.getRemark();
							if (j <= (unqualifiedList.size() - 1)) {
								temp += "\r\n";
							}
							unqualifiedCount += csub.getCount();
						}
						csmbb.setUnqualifiedReasons(temp);
						csmbb.setUnqualifiedNumber(unqualifiedCount);
					} else {
						csmbb.setUnqualifiedReasons("");
						csmbb.setUnqualifiedNumber(0);
					}

				}
			}

			HSSFWorkbook hwb = null;
			try {
				hwb = sus.exportCheckReportInfo(checkBatchList);
				response.reset();
				response.addHeader(
						"Content-Disposition",
						"attachment;filename="
								+ toUtf8String("质检报表" + fileDate, request)
								+ ".xls");
				response.setContentType("application/msxls");
				hwb.write(response.getOutputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method exportCheckReportInfo exception", e);
			}
		} finally {
			service.releaseAll();
		}

	}

	// 质检入库的不合格录入
	public ActionForward appraisalStorageUnqualiyInput(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(625);
		if (!permission) {
			request.setAttribute("tip", "您没有不合格品录入质检结果的权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		String buyStockCode = StringUtil.convertNull(request
				.getParameter("buyStockCode2")); // 预计单号
		int count1 = StringUtil.parstInt(request
				.getParameter("appraisalNumber2"));// 质检数量
		String productCode = StringUtil.convertNull(request
				.getParameter("productCode2")); // 商品编号
		String unqualifyReason = StringUtil.convertNull(request
				.getParameter("reasonForUnqualify")); // 不合格原因
		String completeString = StringUtil.convertNull(request
				.getParameter("complete"));
		boolean complete = false;
		CheckStockinMissionBean csmb = null;
		voProduct product = null;
		CheckStockinMissionBatchBean csmbb = null;
		if (count1 <= 0) {
			request.setAttribute("tip", "输入商品数量不得小于等于0");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		if (unqualifyReason == null || unqualifyReason.equals("")) {
			request.setAttribute("tip", "请填不合格原因");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if (completeString.equals("0")) {
			complete = true;
		}
		BuyStockBean bsBean = null;
		WareService wareService = new WareService();
		StatService statService = new StatService(
				BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ISupplierService supplierService = ServiceFactory
				.createSupplierService(IBaseService.CONN_IN_SERVICE,
						service.getDbOp());
		IProductStockService psService = ServiceFactory
				.createProductStockService(IBaseService.CONN_IN_SERVICE,
						service.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						statService.getDbOp());
		StockinUnqualifiedService sus = new StockinUnqualifiedService(
				BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		FinanceReportFormsService fService = new FinanceReportFormsService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (checkStockinLock) {
				// 开始事物
				statService.getDbOp().startTransaction();
				if (buyStockCode != null && !buyStockCode.equals("")) {
					bsBean = service.getBuyStock("code='"
							+ StringUtil.toSql(buyStockCode) + "'");
					if (bsBean == null) {
						request.setAttribute("tip", "预计单号有误！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					} else {
						// 验证 预计到货单的状态
						sus.checkBuyStockStatus(bsBean);
					}
				} else {
					request.setAttribute("tip", "未输入预计单号！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if (!CargoDeptAreaService.hasCargoDeptArea(request,
						bsBean.getArea(), ProductStockBean.STOCKTYPE_CHECK)) {
					request.setAttribute("tip", "您没有预计单到货地区待验库的操作权限！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				if (productCode != null && !productCode.equals("")) {
					ProductBarcodeVO bBean = bService
							.getProductBarcode("barcode=" + "'"
									+ StringUtil.toSql(productCode) + "'");
					if (bBean == null || bBean.getBarcode() == null) {
						product = wareService.getProduct(StringUtil
								.toSql(productCode));
					} else {
						product = wareService.getProduct(bBean.getProductId());
					}
				} else {
					request.setAttribute("tip", "未输入商品条码！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if (product == null) {
					request.setAttribute("tip", "没有找到对应的商品!");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				if (buyStockCode != null && !buyStockCode.equals("")
						&& productCode != null && !productCode.equals("")) {
					BuyStockProductBean bspBean = service
							.getBuyStockProduct("buy_stock_id="
									+ bsBean.getId() + " and product_id = "
									+ product.getId());
					if (bspBean == null) {
						request.setAttribute("tip", "预计到货单中 没有这个商品!");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}

				// 得到对应的质检入库任务 验证任务个数
				int unfinishCount = statService
						.getCheckStockinMissionBatchCount("buy_stockin_id = "
								+ bsBean.getId() + " and product_id = "
								+ product.getId() + " and status in ("
								+ CheckStockinMissionBatchBean.STATUS0 + ", "
								+ CheckStockinMissionBatchBean.STATUS1 + ", "
								+ CheckStockinMissionBatchBean.STATUS2 + ")");
				if (unfinishCount >= 2) {
					// 按照新的要求一个预计单的 一种商品只可以有一个未完成的 质检入库任务。
					request.setAttribute("tip", "发现预计单有多个质检入库任务，请排查问题！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				} else if (unfinishCount == 1) {
					csmbb = statService
							.getCheckStockinMissionBatch("buy_stockin_id = "
									+ bsBean.getId() + " and product_id = "
									+ product.getId() + " and status in ("
									+ CheckStockinMissionBatchBean.STATUS0 + ", "
									+ CheckStockinMissionBatchBean.STATUS1
									+ ", "
									+ CheckStockinMissionBatchBean.STATUS2
									+ ")");
				} else {
					request.setAttribute("tip", "没有找到未完成的质检入库任务！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				csmb = statService.getCheckStockinMission("id="
						+ csmbb.getMissionId());

				if (csmb == null) {
					request.setAttribute("tip", "没有对应预计单号的质检任务");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				BuyOrderBean bob = service.getBuyOrder("id = "
						+ bsBean.getBuyOrderId());
				// 验证采购订单的状态
				if (bob != null) {
					sus.checkBuyOrderStatus(bob);
				} else {
					request.setAttribute("tip", "没有找到质检任务所对应的采购订单信息！");
					request.setAttribute("result", "failure");
					statService.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				// int uncheckCount = csmbb.getStockinCount() -
				// csmbb.getCheckCount();
				if (csmbb.getStockinCount() > 0) {
					// 进行一系列的修改
					CheckStockinUnqualifiedBean csub = new CheckStockinUnqualifiedBean();
					csub.setBuyStockCode(csmb.getBuyStockinCode());
					csub.setStatus(CheckStockinUnqualifiedBean.UNEXPORT);
					csub.setCount(count1);
					csub.setProductId(product.getId());
					csub.setRemark(StringUtil.toSql(unqualifyReason));
					csub.setMissionBatchId(csmbb.getId());
					csub.setMissionId(csmb.getId());
					csub.setArea(bsBean.getArea());
					// 采购入库单
					// ---------添加采购入库单-----------

					if (!service.updateBuyStock(
							"transform_count = transform_count + 1", "id = "
									+ bsBean.getId())) {
						request.setAttribute("tip", "更新转换采购入库单次数失败！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}

					// ******注意生成入库单编号的 方式发生了改变， 注意改过来************。
					BuyStockinBean stockin = sus.transformStockinFromStock(
							count1, product, bsBean, user, csmb, service,
							wareService, psService, cargoService,
							supplierService, fService, statService, complete);

					// --------添加调拨单----------
					// 建调拨单目的库是返厂库，状态是出库审核中
					StockExchangeBean bean = sus.buildExchangeToBackWare(
							count1, product, user, stockin, service, psService,
							cargoService, statService);

					csub.setStockinDatetime(csmbb.getStockinDatetime());
					csub.setExchangeDatetime(bean.getCreateDatetime());
					csub.setExchangeCode(bean.getCode());
					csub.setBuyStorageCode(stockin.getCode());
					// 修改对应批次任务的质检数量
					if (!statService.updateCheckStockinMission("status="
							+ CheckStockinMissionBean.STATUS2,
							"id=" + csmb.getId())) {
						request.setAttribute("tip", "修改质检入库任务状态失败！");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}

					if (!statService.updateCheckStockinMissionBatch(
							"check_count = (check_count+" + count1
									+ "), status = "
									+ CheckStockinMissionBatchBean.STATUS2,
							"id = " + csmbb.getId())) {
						request.setAttribute("tip", "修改质检数失败");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					// 写不合格记录
					if (!statService.addCheckStockinUnqualified(csub)) {
						request.setAttribute("tip", "添加不合格记录失败");
						request.setAttribute("result", "failure");
						statService.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					statService.getDbOp().commitTransaction();
					statService.getDbOp().getConn().setAutoCommit(true);

					// 为打印准备数据
					stockin.setCreateUserName(user.getUsername());
					stockin.setBuyStock(bsBean);
					stockin.setBuyOrder(bob);
					request.setAttribute("stockExchangeBean", bean);
					request.setAttribute("buyStockinBean", stockin);
				} else {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "目前的确认到货量小于0，不可以质检");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
		} catch (Exception e) {
			service.getDbOp().rollbackTransaction();
			if (logger.isErrorEnabled()) {
				logger.error("method appraisalStorageUnqualiyInput exception",
						e);
			}
			request.setAttribute("tip", "操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			statService.releaseAll();
		}
		return mapping.findForward("printTwo");
	}

	// 弹出添加质检任务单页面
	public ActionForward showAddCheckStockin(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(689)) {
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		return mapping.findForward("showAddCheckStockin");
	}

	// 确认到货
	@RequestMapping("/confirmStockin")
	public String confirmStockin(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String missionId = request.getParameter("missionId");
		if (missionId == null || missionId.equals("")) {
			request.setAttribute("tip", "任务单不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String batchId = request.getParameter("batchId");

		if (batchId == null || batchId.equals("")) {
			request.setAttribute("tip", "任务详细信息不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String realCount = request.getParameter("realCount");
		if (realCount == null || realCount.equals("")) {
			request.setAttribute("tip", "请填写实际到货数量！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		WareService wareService = new WareService();
		CheckStockinMissionService service = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = new StatService(
				BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {

			CheckStockinMissionBatchBean batchBean = statService
					.getCheckStockinMissionBatch("id=" + batchId);

			if (batchBean.getStatus() != CheckStockinMissionBatchBean.STATUS0) {
				request.setAttribute(
						"tip",
						"不能确认到货，该任务状态为："
								+ CheckStockinMissionBatchBean
										.getStatusName(batchBean.getStatus())
								+ "！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			BuyStockBean stock = stockService.getBuyStock("id="
					+ batchBean.getBuyStockinId());
			BuyOrderBean buyOrderBean = stockService.getBuyOrder("id="
					+ stock.getBuyOrderId());
			// 判断采购订单是否已经完成
			if (buyOrderBean.getStatus() == BuyOrderBean.STATUS6) {
				request.setAttribute("tip", "不能确认到货，采购单已经完成！");
				request.setAttribute("result", "failure");
				statService.getDbOp().commitTransaction();
				return "/admin/error";
			}
			// 更新实际到货数量，将状态改为已确认数量
			service.getDbOp().startTransaction();
			service.updateRealStockinCount(Integer.parseInt(realCount),
					Integer.parseInt(batchId));
			// 获取编辑完成的信息
			CheckStockinMissionBean missionBean = service
					.getCheckStockinMissionBean(Integer.parseInt(missionId));
			service.getDbOp().commitTransaction();
			request.setAttribute("missionBean", missionBean);
			UserGroupBean group = user.getGroup();
			request.setAttribute("firstCheck", group.isFlag(786));
			request.setAttribute("secondCheck", group.isFlag(787));
			String type = request.getParameter("type");
			if (type != null && type.equals("1")) {
				return "forward:/admin/cargo/editCheckStockinMission.htm";
			}
			return "forward:/admin/cargo/addcheckStockinMission.htm";
		} catch (Exception e) {
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			service.releaseAll();
		}
	}

	/**
	 * 查询质检入库任务管理
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/queryCheckStockinMission")
	public String queryCheckStockinMission(CheckStockinMissionFormBean csmf,
			HttpServletRequest request, HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(621)) {
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		int countPerPage = 12;
		// System.out.println(csmf.getCreateUserName());
		request.setAttribute("csmf", csmf);
		int pageIndex = 0;
		if (request.getParameter("pageIndex") != null) {
			pageIndex = StringUtil
					.StringToId(request.getParameter("pageIndex"));
		}

		List checkStockinMissionList = new ArrayList();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		StockinUnqualifiedService sus = new StockinUnqualifiedService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CheckStockinMissionService missionService = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						statService.getDbOp());
		StringBuilder param = new StringBuilder();
		String missionIds = "";
		try {
			if(csmf.getProductCode() != null && !"".equals(csmf.getProductCode())){
				ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(csmf.getProductCode())+"'");
				if(bBean != null){
					voProduct product = wareService.getProduct(bBean.getProductId());
					csmf.setProductCode(product.getCode());
				}
			}
			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int x = cdaList.size();
			for (int i = 0; i < x; i++) {
				availAreaIds += "," + cdaList.get(i);
			}
			Map conditionMap = constructCondition(csmf, sus, missionService,
					param, availAreaIds);

			if (conditionMap.containsKey("0")) {
				request.setAttribute("tip", conditionMap.get("0"));
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			missionIds = (String) conditionMap.get("3");
			PagingBean paging = null;
			List missionIdList = null;
			if (((conditionMap.containsKey("2")
					|| !StringUtil.convertNull(csmf.getProductCode())
							.equals("") || (!StringUtil.convertNull(
					csmf.getSupplyId()).equals("") && !StringUtil.convertNull(
					csmf.getSupplyId()).equals("0"))) && missionIds.equals(""))) {
				int totalCount = 0;
				paging = new PagingBean(pageIndex, totalCount, countPerPage);
				checkStockinMissionList = new ArrayList();
				paging.setPrefixUrl("queryCheckStockinMission.mmx?"
						+ param.toString());
			} else {
				int totalCount = statService
						.getCheckStockinMissionCount((String) conditionMap
								.get("1"));
				if (totalCount == 0) {
					paging = new PagingBean(pageIndex, totalCount, countPerPage);
					checkStockinMissionList = new ArrayList();
					paging.setPrefixUrl("queryCheckStockinMission.mmx?"
							+ param.toString());
				} else {
					paging = new PagingBean(pageIndex, totalCount, countPerPage);
					missionIdList = missionService.getMissionIdsByCondition(
							(String) conditionMap.get("1"),
							paging.getCurrentPageIndex() * countPerPage,
							countPerPage, "create_date_time desc, status asc");
					paging.setPrefixUrl("queryCheckStockinMission.mmx?"
							+ param.toString());
					for (int i = 0; i < missionIdList.size(); i++) {
						int missionId = ((Integer) missionIdList.get(i))
								.intValue();
						checkStockinMissionList.add(missionService
								.getCheckStockinMissionBean(missionId));
					}
				}
			}

			// 供应商
			String supplierIds = ProductLinePermissionCache
					.getProductLineSupplierIds(user);
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List supplierList = wareService.getSelects(
					"supplier_standard_info", "where status = 1 and id in ("
							+ supplierIds + ") order by id");
			request.setAttribute("supplyList", supplierList);

			// 得到产品线列表
			String productLinePermission = ProductLinePermissionCache
					.getProductLineIds(user);
			productLinePermission = "product_line.id in ("
					+ productLinePermission + ")";
			List productLineList = wareService
					.getProductLineList(productLinePermission);
			request.setAttribute("productLineList", productLineList);

			request.setAttribute("checkStockinMissionlist",
					checkStockinMissionList);
			String pageLine = PageUtil.fenye(paging, true, "&nbsp;&nbsp;",
					"pageIndex", countPerPage);
			request.setAttribute("paging", pageLine);
			String wareAreaLable = ProductWarePropertyService
					.getWeraAreaOptions(request, csmf.getWareArea());
			request.setAttribute("wareAreaLable", wareAreaLable);

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method queryCheckStockinMission exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			wareService.releaseAll();
		}
		return "forward:/admin/cargo/queryCheckStockinMission.htm";
	}

	// 返回map，如果key有0，表示出错，有1表示正确，有2表示存在产品线,3表示产品先对应任务id
	private Map constructCondition(CheckStockinMissionFormBean csmf,
			StockinUnqualifiedService sus,
			CheckStockinMissionService missionService, StringBuilder param,
			String availAreaIds) {

		StringBuilder condition = new StringBuilder("status!=5");
		StringBuilder parentId1 = new StringBuilder();
		StringBuilder parentId2 = new StringBuilder();
		String beginStockinTime = null;
		String endStockinTime = null;
		String beginCompleteTime = null;
		String endCompleteTime = null;
		String missionIds = "";
		Map conditionMap = new HashMap();
		
		//入库时间区间，开始时间
		if(csmf.getBeginStockinTime() != null && !csmf.getBeginStockinTime().equals("")) {
			beginStockinTime = StringUtil.toSql(csmf.getBeginStockinTime()) + " 00:00:00";
			//入库时间区间，结束时间
			if(csmf.getEndStockinTime()!= null && !csmf.getEndStockinTime().equals("")) {
				endStockinTime = StringUtil.toSql(csmf.getEndStockinTime()) + " 23:59:59";
			}else{
				endStockinTime = DateUtil.getNowDateStr();
				csmf.setEndStockinTime(endStockinTime);
				endStockinTime = DateUtil.getNowDateStr() + " 23:59:59";
			}
			param.append("beginStockinTime=").append(beginStockinTime);
			param.append("&");
			param.append("endStockinTime=").append(endStockinTime);
			if(condition.length() > 0){
	    		condition.append(" and ");
	    	}
			condition.append("create_date_time between '").append(beginStockinTime).append("' and '").append(endStockinTime).append("'");
			if(DateUtil.compareTime(beginStockinTime, endStockinTime)==1){
				conditionMap.put("0", "入库开始时间不能大于结束时间！");
				return conditionMap;
			}
		}else{
			beginStockinTime = "";
		}

		// 入库时间区间，结束时间
		if (csmf.getEndStockinTime() != null
				&& !csmf.getEndStockinTime().equals("")) {
			endStockinTime = StringUtil.toSql(csmf.getEndStockinTime())
					+ " 23:59:59";
		} else {
			endStockinTime = "";
		}

		if (beginStockinTime.equals("") && !endStockinTime.equals("")) {
			conditionMap.put("0", "入库开始时间不能为空！");
			return conditionMap;
		}

		if (!beginStockinTime.equals("") && endStockinTime.equals("")) {
			conditionMap.put("0", "入库结束时间不能为空！");
			return conditionMap;
		}

		if (beginStockinTime.equals("") && endStockinTime.equals("")) {
			beginStockinTime = DateUtil.getNowDateStr();
			endStockinTime = DateUtil.getNowDateStr();
			csmf.setBeginStockinTime(beginStockinTime);
			csmf.setEndStockinTime(endStockinTime);
			beginStockinTime = DateUtil.getNowDateStr() + " 00:00:00";
			endStockinTime = DateUtil.getNowDateStr() + " 23:59:59";
		}

		if (DateUtil.compareTime(beginStockinTime, endStockinTime) == 1) {
			conditionMap.put("0", "入库开始时间不能大于结束时间！");
			return conditionMap;
		}
		param.append("beginStockinTime=").append(beginStockinTime);
		param.append("&");
		param.append("endStockinTime=").append(endStockinTime);

		// 完成时间区间，开始时间
		if (csmf.getBeginCompleteTime() != null
				&& !csmf.getBeginCompleteTime().equals("")) {
			beginCompleteTime = csmf.getBeginCompleteTime() + " 00:00:00";
		} else {
			beginCompleteTime = "";
		}

		// 完成时间区间，结束时间
		if (csmf.getEndCompleteTime() != null
				&& !csmf.getEndCompleteTime().equals("")) {
			endCompleteTime = csmf.getEndCompleteTime() + " 23:59:59";
		} else {
			endCompleteTime = "";
		}

		if (beginCompleteTime.equals("") && !endCompleteTime.equals("")) {
			conditionMap.put("0", "完成开始时间不能为空！");
			return conditionMap;
		}

		if (!beginCompleteTime.equals("") && endCompleteTime.equals("")) {
			conditionMap.put("0", "完成结束时间不能为空！");
			return conditionMap;
		}

		if (!beginCompleteTime.equals("") && !endCompleteTime.equals("")) {
			if (DateUtil.compareTime(beginCompleteTime, endCompleteTime) == 1) {
				conditionMap.put("0", "完成开始时间不能大于结束时间！");
				return conditionMap;
			}
		}
		if (beginCompleteTime != null && !beginCompleteTime.equals("")) {
			param.append("&");
			param.append("beginCompleteTime=").append(beginCompleteTime);
			param.append("&");
			param.append("endCompleteTime=").append(endCompleteTime);
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("complete_date_time between '").append(
					beginCompleteTime);
			condition.append("' and '").append(endCompleteTime).append("'");
		}

		if (condition.length() > 0) {
			condition.append(" and ");
		}

		condition.append("create_date_time between '").append(beginStockinTime)
				.append("' and '").append(endStockinTime).append("'");

		// 找出产品线信息
		if (csmf.getProductLine() != null && !csmf.getProductLine().equals("")
				&& !csmf.getProductLine().equals("0")) {
			List productLineList2 = sus.getProductLineCatalogList(
					"product_line_id = " + csmf.getProductLine(), -1, -1, null);
			for (int i = 0; i < productLineList2.size(); i++) {
				ProductLineCatalogBean plcb = (ProductLineCatalogBean) productLineList2
						.get(i);
				if (plcb.getCatalogType() == 1) {
					parentId1.append(plcb.getCatalogId()).append(",");
				}
				if (plcb.getCatalogType() == 2) {
					parentId2.append(plcb.getCatalogId()).append(",");
				}
			}
			param.append("&");
			param.append("productLine=").append(csmf.getProductLine());
			conditionMap.put("2", null);
		}

		// 找出满足产品线的任务id，以及供应商
		if (parentId1.length() > 0
				|| parentId2.length() > 0
				|| (csmf.getProductCode() != null && !csmf.getProductCode()
						.equals(""))
				|| (csmf.getSupplyId() != null
						&& !csmf.getSupplyId().equals("") && !csmf
						.getSupplyId().equals("0"))) {
			String sParentId1 = "";
			String sParentId2 = "";
			if (parentId1.length() > 0) {
				sParentId1 = parentId1.toString().substring(0,
						parentId1.length() - 1);
			}
			if (parentId2.length() > 0) {
				sParentId2 = parentId2.toString().substring(0,
						parentId2.length() - 1);
			}
			missionIds = missionService.getMissionIdsByProductLine(sParentId1,
					sParentId2, csmf.getSupplyId(), null, null,
					csmf.getProductCode());
			conditionMap.put("3", missionIds);
		}

		// 任务状态条件
		if (csmf.getMissionStatus() != null
				&& !csmf.getMissionStatus().equals("-1")) {
			if (csmf.getMissionStatus() != null
					&& !csmf.getMissionStatus().equals("")) {
				if (condition.length() > 0) {
					condition.append(" and ");
				}
				condition.append("status=").append(csmf.getMissionStatus());
				param.append("&");
				param.append("missionStatus=").append(csmf.getMissionStatus());
			} else {
				if (condition.length() > 0) {
					condition.append(" and ");
				}
				condition.append("status=").append(0);
				param.append("&");
				param.append("missionStatus=").append(0);
			}
		}

		// 到货地区条件
		if (csmf.getWareArea() != -1) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("ware_area=").append(csmf.getWareArea());
			param.append("&");
			param.append("wareArea=").append(csmf.getWareArea());
		} else {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("ware_area in (").append(availAreaIds).append(")");
			param.append("&");
			param.append("wareArea=").append(-1);
		}

		// 任务单号条件
		if (csmf.getMissionCode() != null && !csmf.getMissionCode().equals("")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("code='").append(csmf.getMissionCode())
					.append("'");
			param.append("&");
			param.append("missionCode=").append(csmf.getMissionCode());
		}

		// 预计单号条件
		if (csmf.getBuyStockCode() != null
				&& !csmf.getBuyStockCode().equals("")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("buy_stockin_code='")
					.append(csmf.getBuyStockCode()).append("'");
			param.append("&");
			param.append("buyStockCode=").append(csmf.getBuyStockCode());
		}

		// 任务优先程度条件
		if (csmf.getPriority() != null && !csmf.getPriority().equals("")
				&& !csmf.getPriority().equals("-1")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("prior_status='").append(csmf.getPriority())
					.append("'");
			param.append("&");
			param.append("priority=").append(csmf.getPriority());
		}

		// 产能负荷条件
		if (csmf.getProductLoad() != null && !csmf.getProductLoad().equals("")
				&& !csmf.getProductLoad().equals("-1")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("product_load='").append(csmf.getProductLoad())
					.append("'");
			param.append("&");
			param.append("productLoad=").append(csmf.getProductLoad());
		}

		// 实际耗时条件
		if ((csmf.getBeginConsumTime() == null || csmf.getBeginConsumTime()
				.equals(""))
				&& (csmf.getEndConsumTime() != null && !csmf.getEndConsumTime()
						.equals(""))) {
			conditionMap.put("0", "实际耗时开始时间不能为空！");
			return conditionMap;
		}

		if ((csmf.getBeginConsumTime() != null && !csmf.getBeginConsumTime()
				.equals(""))
				&& (csmf.getEndConsumTime() == null || csmf.getEndConsumTime()
						.equals(""))) {
			conditionMap.put("0", "实际耗时结束时间不能为空！");
			return conditionMap;
		}

		if ((csmf.getBeginConsumTime() != null && !csmf.getBeginConsumTime()
				.equals(""))
				&& (csmf.getEndConsumTime() != null && !csmf.getEndConsumTime()
						.equals(""))) {
			if (DateUtil.compareTime(csmf.getBeginConsumTime(),
					csmf.getEndConsumTime()) == 1) {
				conditionMap.put("0", "完成开始时间不能大于结束时间！");
				return conditionMap;
			}
		}
		if (csmf.getBeginConsumTime() != null
				&& !csmf.getBeginConsumTime().equals("")) {
			param.append("&");
			param.append("beginConsumTime=").append(beginCompleteTime);
			param.append("&");
			param.append("endConsumTime=").append(endCompleteTime);
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("real_consumtime between '").append(
					csmf.getBeginConsumTime());
			condition.append("' and '").append(csmf.getEndConsumTime())
					.append("'");
		}

		// 生成人条件
		if (csmf.getCreateUserName() != null
				&& !csmf.getCreateUserName().equals("")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("create_oper_name='")
					.append(csmf.getCreateUserName()).append("'");
			param.append("&");
			param.append("createUserName=").append(csmf.getProductLoad());
		}

		// 是否有符合产品线的商品
		if (!missionIds.equals("")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			missionIds = missionIds.substring(0, missionIds.length() - 1);
			condition.append("id").append(" in (").append(missionIds)
					.append(")");
		}

		conditionMap.put("1", condition.toString());
		return conditionMap;
	}

	// 获取入库明细信息
	@RequestMapping("/getPackingInfo")
	public String getPackingInfo(HttpServletRequest request,
			HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String strMissionId = request.getParameter("missionId");
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try {
			if (strMissionId == null || strMissionId.equals("")) {
				request.setAttribute("tip", "该任务单id不能为空！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			StatService service = new StatService(IBaseService.CONN_IN_SERVICE,
					wareService.getDbOp());
			CheckStockinMissionBatchBean bean = service
					.getCheckStockinMissionBatch("mission_id=" + strMissionId);
			request.setAttribute("missionId", strMissionId);
			request.setAttribute("batchId", bean.getId());
			Map map = service.queryPackingDetailInfo(strMissionId);
			request.setAttribute("cartonningMap", map);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			wareService.releaseAll();
		}
		return "forward:/admin/cargo/packingInfo.htm";
	}

	// 删除质检任务
	@RequestMapping("/deleteMission")
	public String deleteMission(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String strMissionId = request.getParameter("missionId");
		if (strMissionId == null || strMissionId.equals("")) {
			request.setAttribute("tip", "该任务单不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		WareService wareService = new WareService();
		CheckStockinMissionService service = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			service.deleteMission(StringUtil.parstInt(strMissionId));
			return "redirect:queryCheckStockinMission.mmx";
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method deleteMission exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			wareService.releaseAll();
		}
	}

	// 编辑质检入库任务
	@RequestMapping("/editCheckStockinMission")
	public String editCheckStockinMission(HttpServletRequest request,
			HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String strMissionId = request.getParameter("missionId");
		if (strMissionId == null || strMissionId.equals("")) {
			request.setAttribute("tip", "该任务单不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		WareService wareService = new WareService();
		UserGroupBean group = user.getGroup();
		request.setAttribute("firstCheck", group.isFlag(786));
		request.setAttribute("secondCheck", group.isFlag(787));
		CheckStockinMissionService service = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			CheckStockinMissionBean missionBean = service
					.getCheckStockinMissionBean(StringUtil
							.parstInt(strMissionId));
			String buyStockCode = missionBean.getBuyStockinCode();
			BuyStockBean stock = stockService.getBuyStock("code='"
					+ buyStockCode + "'");
			if (stock == null) {
				request.setAttribute("tip", "没有对应的预计到货表！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			if (!CargoDeptAreaService.hasCargoDeptArea(request,
					stock.getArea(), ProductStockBean.STOCKTYPE_CHECK)) {
				request.setAttribute("tip", "没有操作预计单到货地区待验库权限,不可以查看这个入库任务！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			List<CheckStockinMissionDetailBean> csmdBeanList = statService
					.getCheckStockinMissionDetailList("mission_id="
							+ missionBean.getId(), -1, -1, null);
			if (csmdBeanList != null && !csmdBeanList.isEmpty()) {
				StringBuilder stockinIdBuilder = new StringBuilder();
				for (CheckStockinMissionDetailBean csmd : csmdBeanList) {
					if (stockinIdBuilder.length() > 0) {
						stockinIdBuilder.append(",");
					}
					stockinIdBuilder.append(csmd.getBuyStockinId());
				}
				StringBuilder condition = new StringBuilder();
				condition.append("status!=" + BuyStockinBean.STATUS4);
				condition.append(" and status!=" + BuyStockinBean.STATUS5);
				condition.append(" and status!=" + BuyStockinBean.STATUS6);
				condition.append(" and status!=" + BuyStockinBean.STATUS7);
				condition.append(" and status!=" + BuyStockinBean.STATUS8);
				if (stockinIdBuilder.length() > 0) {
					condition.append(" and id in (");
					condition.append(stockinIdBuilder.toString());
					condition.append(")");
				}
				int count = stockService.getBuyStockinCount(condition
						.toString());
				if (count > 0) {
					request.setAttribute("showComp", 0);
				} else {
					request.setAttribute("showComp", 1);
				}
			}
			request.setAttribute("missionBean", missionBean);
			return "forward:/admin/cargo/editCheckStockinMission.htm";
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method editCheckStockinMission exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 
	 * 此方法描述的是： 根据预计单号判断是否可以自动完成订单
	 * 
	 * @author: liubo
	 * @version: 2013-1-23 下午04:51:02
	 */
	public ActionForward judgeComOrderByStockinId(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		PrintWriter pw = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			String buyStockinId = request.getParameter("buyStockinId");
			if (buyStockinId == null || buyStockinId.equals("")) {
				pw.write("参数错误，采购入库单id不能为空！");
				return null;
			}

			CheckStockinMissionService csms = new CheckStockinMissionService(
					IBaseService.CONN_IN_SERVICE,
					wareService.getDbOp());
			String result = csms.judgeComOrderByStockinId(Integer
					.parseInt(buyStockinId));
			pw.write(result);
			return null;
			
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method judgeComOrderByStockinId exception", e);
			}
			e.printStackTrace();
			pw.write("系统异常请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 
	 * 此方法描述的是： 判断是否可以自动完成订单
	 * 
	 * @author: liubo
	 * @version: 2013-1-23 下午04:49:02
	 */
	public ActionForward judgeAutoComplete(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter pw = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			String buyStockCode = request.getParameter("buyStockCode");
			if (buyStockCode == null || buyStockCode.equals("")) {
				pw.write("预计到货单号不能为空！");
				return null;
			}

			String appraisalNumber = request.getParameter("appraisalNumber");
			if (appraisalNumber == null || appraisalNumber.equals("")) {
				pw.write("扫描的商品数量不能为空！");
				return null;
			}

			if (appraisalNumber.length() > 10) {
				pw.write("质检数量长度不能大于10！");
				return null;
			}
			String productCode = request.getParameter("productCode");
			if (productCode == null || productCode.equals("")) {
				pw.write("扫描的商品编号不能为空！");
				return null;
			}
			synchronized (checkStockinLock) {
				CheckStockinMissionService csms = new CheckStockinMissionService(
						IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
				String result = csms.judgeAutoCompleteOrder(buyStockCode,
						appraisalNumber, productCode);
				pw.write(result);
				return null;
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method judgeAutoComplete exception", e);
			}
			pw.write("系统异常请联系管理员");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 执行质检合格装箱入库动作
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/appraisalStorageQualiyInput")
	public String appraisalStorageQualiyInput(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(624)) {
			request.setAttribute("tip", "您没有权限操作该功能！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String missionId = request.getParameter("missionId");
		if (missionId == null || missionId.equals("")) {
			request.setAttribute("tip", "该任务单号不能为空！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		request.setAttribute("missionId", missionId);

		String batchId = request.getParameter("batchId");
		if (batchId == null || batchId.equals("")) {
			request.setAttribute("tip", "该批次任务id不能为空！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		request.setAttribute("batchId", batchId);

		String buyStockCode = request.getParameter("buyStockCode");
		if (buyStockCode == null || buyStockCode.equals("")) {
			request.setAttribute("tip", "预计到货单号不能为空！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String appraisalNumber = request.getParameter("appraisalNumber");
		if (appraisalNumber == null || appraisalNumber.equals("")) {
			request.setAttribute("tip", "扫描的商品数量不能为空！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		if (appraisalNumber.length() > 10) {
			request.setAttribute("tip", "质检数量长度不能大于10！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String productCode = request.getParameter("productCode");
		if (productCode == null || productCode.equals("")) {
			request.setAttribute("tip", "扫描的商品编号不能为空！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		int checkNum = -1;
		try {
			checkNum = Integer.parseInt(appraisalNumber);
			if (checkNum <= 0) {
				request.setAttribute("tip", "采购入库量不能为0，操作失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
		} catch (Exception e) {
			request.setAttribute("tip", "产品个数有错误！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		CheckStockinMissionService csms = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE);
		try {
			// 质检合格入库
			Object result = csms
					.appraisalQualityInput(request, user, missionId,
							batchId, buyStockCode, checkNum, productCode);
			if (result != null && !BuyStockinBean.class.isInstance(result)) {
				request.setAttribute("tip", result);
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			BuyStockinBean bsBean = (BuyStockinBean) result;
			// BuyStockinProductBean bsProductBean = (BuyStockinProductBean)
			// bsBean.getBuyStockinProductList().get(0);
			request.setAttribute("buyStockin", bsBean);
			// request.setAttribute("buyStockinProduct", bsProductBean);
			return "forward:/admin/cargo/checkBuyStockinPrint.htm";
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"method appraisalStorageQualiyInput exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			csms.releaseAll();
		}
	
	}

	/**
	 * 
	 * 此方法描述的是： 打印入库明细
	 * 
	 * @author: liubo
	 * @version: 2013-1-23 上午10:38:07
	 */
	@RequestMapping("/printBuyStockin")
	public String printBuyStockin(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(624)) {
			request.setAttribute("tip", "您没有权限操作该功能！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String buyStockinId = request.getParameter("buyStockinId");
		if (buyStockinId == null || buyStockinId.equals("")) {
			request.setAttribute("tip", "参数传递错误，该入库单号不能为空！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		WareService wareService = new WareService();
		AdminService adminService = new AdminService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			IStockService service = ServiceFactory
					.createStockService(IBaseService.CONN_IN_SERVICE,
							adminService.getDbOp());

			BuyStockinBean bsBean = service.getBuyStockin("id=" + buyStockinId);
			BuyStockBean buyStock = service.getBuyStock("id="
					+ bsBean.getBuyStockId());
			BuyOrderBean buyOrder = service.getBuyOrder("id="
					+ buyStock.getBuyOrderId());
			buyStock.setBuyOrder(buyOrder);
			bsBean.setBuyStock(buyStock);
			voUser cuser = adminService.getAdmin(bsBean.getCreateUserId());
			bsBean.setCreateUserName(cuser.getUsername());
			List buyStockinProductList = service.getBuyStockinProductList(
					"buy_stockin_id=" + buyStockinId, -1, -1, null);
			if (buyStockinProductList == null
					|| buyStockinProductList.isEmpty()) {
				request.setAttribute("tip", "该入库单下没有商品信息，打印失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			BuyStockinProductBean bsip = (BuyStockinProductBean) buyStockinProductList
					.get(0);
			voProduct product = wareService.getProduct(bsip.getProductId());
			// 产品线名称
			voProductLine pl = null;
			voProductLineCatalog plc = null;
			List list2 = wareService.getProductLineListCatalog("catalog_id = "
					+ product.getParentId1());
			if (list2.size() > 0) {
				plc = (voProductLineCatalog) list2.get(0);
				pl = (voProductLine) wareService.getProductLineList(
						"product_line.id = " + plc.getProduct_line_id()).get(0);
				bsip.setProductLineName(pl.getName());
			}
			if (StringUtil.convertNull(bsip.getProductLineName()).equals("")) {
				list2 = wareService.getProductLineListCatalog("catalog_id = "
						+ product.getParentId2());
				if (list2.size() > 0) {
					plc = (voProductLineCatalog) list2.get(0);
					pl = (voProductLine) wareService.getProductLineList(
							"product_line.id = " + plc.getProduct_line_id())
							.get(0);
					bsip.setProductLineName(pl.getName());
				}
			}
			if (StringUtil.convertNull(bsip.getProductLineName()).equals("")) {
				bsip.setProductLineName("无");
			}
			bsBean.setBuyStockinProductList(buyStockinProductList);
			request.setAttribute("buyStockin", bsBean);
			return "forward:/admin/cargo/checkBuyStockinPrint.htm";
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method printBuyStockin exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 弹出提交质检结果
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/qualifyPacking")
	public String qualifyPacking(HttpServletRequest request,
			HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String missionId = request.getParameter("missionId");
		if (missionId == null || missionId.equals("")) {
			request.setAttribute("tip", "该任务单不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String batchId = request.getParameter("batchId");
		if (batchId == null || batchId.equals("")) {
			request.setAttribute("tip", "该批次任务id不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,
				wareService.getDbOp());
		IStockService stockService = new StockServiceImpl(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			CheckStockinMissionBean missionBean = statService
					.getCheckStockinMission("id=" + missionId);
			if (missionBean == null) {
				request.setAttribute("tip", "该任务不存在！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			CheckStockinMissionBatchBean batchBean = statService
					.getCheckStockinMissionBatch("id=" + batchId);
			if (batchBean == null) {
				request.setAttribute("tip", "该批次任务不存在！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			if (batchBean.getStatus() == CheckStockinMissionBatchBean.STATUS0) {
				request.setAttribute("tip", "该批次任务还没有处理！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			if (batchBean.getStatus() == CheckStockinMissionBatchBean.STATUS3) {
				request.setAttribute("tip", "该批次任务已经确认完成！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			BuyStockBean buyStock = stockService.getBuyStock("id="
					+ batchBean.getBuyStockinId());

			request.setAttribute("buyStockCode", buyStock.getCode());
			request.setAttribute("missionId", missionId);
			request.setAttribute("batchId", batchId);
			return "forward:/admin/cargo/qualifyPacking.htm";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			if (wareService != null) {
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 确认完成
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	//@RequestMapping("/confirmComCheckStockin")
	public String confirmComCheckStockin(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String missionId = request.getParameter("missionId");
		if (missionId == null || missionId.equals("")) {
			request.setAttribute("tip", "该任务单不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String batchId = request.getParameter("batchId");
		if (batchId == null || batchId.equals("")) {
			request.setAttribute("tip", "该批次任务id不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		WareService wareService = new WareService();
		CheckStockinMissionService service = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,
				wareService.getDbOp());
		try {
			CheckStockinMissionBatchBean batchBean = statService
					.getCheckStockinMissionBatch("id=" + batchId);
			if (batchBean == null) {
				request.setAttribute("tip", "该任务不存在！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			if (batchBean.getStatus() == CheckStockinMissionBatchBean.STATUS0
					|| batchBean.getStatus() == CheckStockinMissionBatchBean.STATUS1) {
				request.setAttribute("tip", "该任务还没有质检！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			if (batchBean.getStatus() == CheckStockinMissionBatchBean.STATUS3) {
				request.setAttribute("tip", "该任务已经确认完成！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			service.getDbOp().startTransaction();
			service.confirmComCheckStockin(Integer.parseInt(missionId),
					batchBean, user);
			service.getDbOp().commitTransaction();
			CheckStockinMissionBean missionBean = service
					.getCheckStockinMissionBean(Integer.parseInt(missionId));
			request.setAttribute("missionBean", missionBean);
			request.setAttribute("planBillNum", missionBean.getBuyStockinCode());
			request.setAttribute("missionLevel",
					Integer.valueOf(missionBean.getPriorStatus()));
			String type = request.getParameter("type");
			if (type != null && type.equals("1")) {
				return "forward:/admin/cargo/editCheckStockinMission.htm";
			}
			return "forward:/admin/cargo/addcheckStockinMission.htm";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			if (wareService!= null) {
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 分配暂存号
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/doAssignTempNum")
	public String doAssignTempNum(HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter pw = null;

		WareService wareService = new WareService();
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,
				wareService.getDbOp());
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				pw.write("当前没有登录，操作失败！");
				return null;
			}

			String batchId = StringUtil.convertNull(request
					.getParameter("batchId"));
			if (batchId == null || batchId.equals("")) {
				pw.write("该任务详细信息id不存在！");
				return null;
			}

			String parmFlag = StringUtil.convertNull(request
					.getParameter("parmFlag"));
			if (parmFlag == null || parmFlag.equals("")) {
				pw.write("参数parmFlag错误！");
				return null;
			}

			String[] tempNum = request.getParameterValues("tempNum");
			if (tempNum == null || tempNum.length == 0) {
				pw.write("没有选择暂存号！");
				return null;
			}

			StringBuilder tnum = new StringBuilder();
			for (int i = 0; i < tempNum.length; i++) {
				if (tnum.length() > 0) {
					tnum.append(",");
				}
				tnum.append(tempNum[i]);
			}

			if (tnum == null || tnum.equals("")) {
				pw.write("没有选择暂存号！");
				return null;
			}

			CheckStockinMissionBatchBean batchBean = statService
					.getCheckStockinMissionBatch("id=" + batchId);
			if (batchBean == null) {
				pw.write("该任务详细信息不存在！");
				return null;
			}

			if (batchBean.getStatus() == CheckStockinMissionBatchBean.STATUS3) {
				pw.write("该任务已经确认完成！");
				return null;
			}

			if (batchBean.getTempNum() != null
					&& !batchBean.getTempNum().equals("")) {
				if (parmFlag.equals("0")) {
					pw.write("0");
					return null;
				} else {
					wareService.getDbOp().startTransaction();
					if (!statService.updateCheckStockinMissionBatch(
							"temp_num='" + tnum + "'",
							"id=" + Integer.parseInt(batchId))) {
						wareService.getDbOp().rollbackTransaction();
						pw.write("分配暂存号失败！");
						return null;
					}
					wareService.getDbOp().commitTransaction();
				}
			} else {
				wareService.getDbOp().startTransaction();
				if (!statService.updateCheckStockinMissionBatch("temp_num='"
						+ tnum + "'", "id=" + Integer.parseInt(batchId))) {
					wareService.getDbOp().rollbackTransaction();
					pw.write("分配暂存号失败！");
					return null;
				}
				wareService.getDbOp().commitTransaction();
			}
			pw.write(batchBean.getMissionId() + "-0");
			return null;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method doAssignTempNum exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return null;
		} finally {
			if (wareService != null) {
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 弹出分配暂存号页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/assignTempNum")
	public String assignTempNum(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String batchId = request.getParameter("batchId");
		if (batchId == null || batchId.equals("")) {
			request.setAttribute("tip", "该批次任务id不存在！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		request.setAttribute("batchId", batchId);

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,
				wareService.getDbOp());
		try {
			CheckStockinMissionBatchBean batchBean = statService
					.getCheckStockinMissionBatch("id=" + batchId);
			if (batchBean == null) {
				request.setAttribute("tip", "该批次任务不存在！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			if (batchBean.getStatus() == CheckStockinMissionBatchBean.STATUS3) {
				request.setAttribute("tip", "该批次任务已经确认完成！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}

			List<String> areaIdList = CargoDeptAreaService
					.getCargoDeptAreaList(request);
			StringBuilder areaId = new StringBuilder();
			for (String str : areaIdList) {
				if (areaId.length() > 0) {
					areaId.append(",");
				}
				areaId.append(str);
			}
			if (areaId.length() == 0) {
				request.setAttribute("tip", "当前用户没有权限查询暂存号！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			// 构造页面信息
			constructPageInfo(request, wareService,
					"assignTempNum.mmb?batchId=" + batchId, areaId.toString(),
					-1);
			return "forward:/admin/cargo/checkAssignTemporaryNum.htm";
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			if (wareService != null) {
				wareService.releaseAll();
			}
		}
	}

	private void constructPageInfo(HttpServletRequest request,
			WareService wareService, String pageUrl, String areaIds,
			int areaId) {
		TempNumberService service = new TempNumberService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String tempNum = StringUtil
				.convertNull(request.getParameter("tempNum"));

		int countPerPage = 20;
		request.setAttribute("tempNum", tempNum);
		int pageIndex = 0;
		if (request.getParameter("pageIndex") != null) {
			pageIndex = StringUtil
					.StringToId(request.getParameter("pageIndex"));
		}
		List tempList = new ArrayList();
		if (areaId == -1) {
			if (tempNum != null && !tempNum.equals("")) {
				tempNum = "name='" + StringUtil.toSql(tempNum) + "'";
				tempNum = tempNum + " and area in(" + areaIds + ")";
			} else {
				tempNum = "area in(" + areaIds + ")";
			}
		} else {
			if (tempNum != null && !tempNum.equals("")) {
				tempNum = "name='" + StringUtil.toSql(tempNum) + "'";
				tempNum = tempNum + " and area =" + areaId;
			} else {
				tempNum = "area =" + areaId;
			}
		}
		int totalCount = service.getTemporaryNumCount(tempNum);
		PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
		if (totalCount != 0) {
			tempList = service.getTemporaryNumList(tempNum, pageIndex
					* countPerPage, countPerPage, "id desc");
		}
		if (tempNum != null && !tempNum.equals("")) {
			paging.setPrefixUrl(pageUrl + "&tempNum=" + tempNum);
		} else {
			paging.setPrefixUrl(pageUrl);
		}
		request.setAttribute("tempList", tempList);
		String pageLine = PageUtil.fenye(paging, true, "&nbsp;&nbsp;",
				"pageIndex", countPerPage);
		request.setAttribute("paging", pageLine);
	}

	/**
	 * 编辑任务优先级
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/modifyMissionPriority")
	public String modifyMissionPriority(HttpServletRequest request,
			HttpServletResponse response) {

		PrintWriter pw = null;
		WareService wareService = new WareService();
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				pw.write("当前没有登录，操作失败！");
				return null;
			}

			UserGroupBean group = user.getGroup();
			if (!group.isFlag(702)) {
				pw.write("您没有权限编辑任务优先级！");
				return null;
			}

			String[] missionId = request.getParameterValues("missionId");
			if (missionId == null || missionId.length == 0) {
				pw.write("请选择质检任务！");
				return null;
			}
			CheckStockinMissionService csms = new CheckStockinMissionService(
					IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			Integer mId = null;
			String priority = null;
			Map missionMap = new HashMap();
			for (int i = 0; i < missionId.length; i++) {
				mId = Integer.valueOf(missionId[i]);
				priority = request.getParameter("priority_" + mId);
				if (priority == null || priority.equals("-1")) {
					pw.write("请选择优先级！");
					return null;
				}
				missionMap.put(mId, priority);
			}
			// 修改任务优先级并重新计算产能
			String result = csms.modifyMissionPriority(missionMap);
			if (result != null) {
				pw.write(result);
				return null;
			}
			pw.write("success");
			return null;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method modifyMissionPriority exception", e);
			}
			pw.write("修改优先级失败，请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}

	}

	/**
	 * 导出质检任务
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/exportMission")
	public String exportMission(CheckStockinMissionFormBean csmf,
			HttpServletRequest request, HttpServletResponse response) {

		PrintWriter pw = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				pw.write("当前没有登录，操作失败！");
				return null;
			}

			List<String> cdaList = CargoDeptAreaService
					.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int x = cdaList.size();
			for (int i = 0; i < x; i++) {
				availAreaIds += "," + cdaList.get(i);
			}
			// UserGroupBean group = user.getGroup();
			// if(!group.isFlag(689)){
			// pw.write("您没有权限添加质检任务！");
			// return null;
			// }
			List checkStockinMissionList = new ArrayList();
			StockinUnqualifiedService sus = new StockinUnqualifiedService(
					IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			CheckStockinMissionService missionService = new CheckStockinMissionService(
					IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			missionService.getDbOp().getConn().setAutoCommit(true);
			StringBuilder param = new StringBuilder();
			String missionIds = "";

			Map conditionMap = constructCondition(csmf, sus, missionService,
					param, availAreaIds);

			if (conditionMap.containsKey("0")) {
				// request.setAttribute("tip", conditionMap.get("0"));
				// request.setAttribute("result", "failure");
				pw.write(conditionMap.get("0") + "");
				return null;
			}

			missionIds = (String) conditionMap.get("3");

			List missionIdList = null;
			if (((conditionMap.containsKey("2")
					|| !StringUtil.convertNull(csmf.getProductCode())
							.equals("") || (!StringUtil.convertNull(
					csmf.getSupplyId()).equals("") && !StringUtil.convertNull(
					csmf.getSupplyId()).equals("0"))) && missionIds.equals(""))) {
				checkStockinMissionList = new ArrayList();
			} else {
				missionIdList = missionService.getMissionIdsByCondition(
						(String) conditionMap.get("1"), 0, -1,
						"create_date_time desc, status asc");
				for (int i = 0; i < missionIdList.size(); i++) {
					int missionId = ((Integer) missionIdList.get(i)).intValue();
					checkStockinMissionList.add(missionService
							.getCheckStockinMissionBean(missionId));
				}
			}

			HSSFWorkbook hwb = missionService
					.exportMission(checkStockinMissionList);
			response.reset();
			response.addHeader(
					"Content-Disposition",
					"attachment;filename="
							+ StringUtil
									.toUtf8String("质检入库任务表"
											+ DateUtil.getNowDateStr().replace(
													"-", "")) + ".xls");
			response.setContentType("application/msxls");
			hwb.write(response.getOutputStream());
			return null;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method exportMission exception", e);
			}
			pw.write("添加任务失败，请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 判断质检数量是否大于预计到货数量
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/judgeStockinCount")
	public String judgeStockinCount(HttpServletRequest request,
			HttpServletResponse response) {
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService service = new StatService(IBaseService.CONN_IN_SERVICE,
				wareService.getDbOp());
		PrintWriter pw = null;
		try {
			synchronized (checkStockinLock) {
				response.setCharacterEncoding("utf-8");
				pw = response.getWriter();
				voUser user = (voUser) request.getSession().getAttribute(
						"userView");
				if (user == null) {
					pw.write("当前没有登录，操作失败！");
					return null;
				}

				String number = StringUtil.dealParam(request
						.getParameter("number"));
				String batchId = StringUtil.dealParam(request
						.getParameter("batchId"));
				if (number == null || number.equals("")) {
					pw.write("number参数传递错误！");
					return null;
				}
				if (batchId == null || batchId.equals("")) {
					pw.write("batchId参数传递错误！");
					return null;
				}

				CheckStockinMissionBatchBean csmb = service
						.getCheckStockinMissionBatch("id="
								+ Integer.parseInt(batchId));
				if (csmb.getBuyCount() < Integer.parseInt(number)) {
					pw.write("0");
					return null;
				}
				pw.write("");
				return null;
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method judgeStockinCount exception", e);
			}
			pw.write("判断数量失败，请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 添加质检入库任务单
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward addCheckStockin(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		synchronized (checkStockinLock) {

			WareService wareService = new WareService();
			CheckStockinMissionService service = new CheckStockinMissionService(
					IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			PrintWriter pw = null;
			try {
				response.setCharacterEncoding("utf-8");
				pw = response.getWriter();
				voUser user = (voUser) request.getSession().getAttribute(
						"userView");
				if (user == null) {
					pw.write("当前没有登录，操作失败！");
					return null;
				}

				UserGroupBean group = user.getGroup();
				if (!group.isFlag(689)) {
					pw.write("您没有权限添加质检任务！");
					return null;
				}
				String planBillNum = StringUtil.dealParam(request
						.getParameter("planBillNum"));
				String productCode = StringUtil.dealParam(request
						.getParameter("productCode"));
				if (planBillNum == null || planBillNum.equals("")) {
					pw.write("预计到货单号不能为空！");
					return null;
				}
				if (productCode == null || productCode.equals("")) {
					pw.write("产品编号不能为空！");
					return null;
				}

				String flag = request.getParameter("flag");
				String result = service.addCheckStockinMission(request,
						planBillNum, user, productCode, flag);
				if (result != null && result.equals("checkStockinTip")) {
					pw.write("checkStockinTip");
					return null;
				} else if (result != null && result.equals("请输入该sku的标准装箱量!")) {
					request.setAttribute("tip", result);
					statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
					String productId = statService
							.getProductIdbyProductCode(productCode);
					if (productId == null || "".equals(productId))
						productId = statService
								.getProductIdByProductBarcode(productCode);
					if (productId != null && !"".equals(productId)) {
						String propertyId = statService
								.getPwpIdByProductCode(productCode);
						List checkEffectList = new ArrayList();
						List productWareTypeList = new ArrayList();
						IBarcodeCreateManagerService bService = ServiceFactory
								.createBarcodeCMServcie(
										IBaseService.CONN_IN_SERVICE,
										wareService.getDbOp());
						ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
								IBaseService.CONN_IN_SERVICE,
								wareService.getDbOp());
						ProductWarePropertyBean pwpBean = statService
								.getProductWareProperty("id = "
										+ StringUtil.parstInt(propertyId));
						if (pwpBean == null) {
							request.setAttribute("tip", "没有找到对应的商品物流属性信息！");
							request.setAttribute("result", "failure");
							//return mapping.findForward(IConstants.FAILURE_KEY);
							pw.write("没有找到对应的商品物流属性信息！");
							return null;
						} else {
							voProduct temp = statService.getProduct(pwpBean
									.getProductId());
							if (temp == null) {
								temp = new voProduct();
							}
							pwpBean.setProduct(temp);
							ProductBarcodeVO bBean = bService
									.getProductBarcode("product_id="
											+ temp.getId());
							if (bBean == null) {
								request.setAttribute("barCode", "");
							} else {
								request.setAttribute("barCode",
										bBean.getBarcode());
							}
							request.setAttribute("productWareProperty", pwpBean);

							checkEffectList = statService.getCheckEffectList(
									"id<>0", -1, -1, "id asc");
							productWareTypeList = productWarePropertyService
									.getProductWareTypeList("id<>0", -1, -1,
											"id asc");
							/*
							 * productWareTypeList =
							 * statService.getUserOrderPackageTypeList("id<>0",
							 * -1, -1, "type_id asc"); productWareTypeList =
							 * productWarePropertyService
							 * .removeDuplicateInList(productWareTypeList);
							 */

							request.setAttribute("productWareTypeList",
									productWareTypeList);
							request.setAttribute("checkEffectList",
									checkEffectList);
							pw.write(result + "," + propertyId);
							// return
							// mapping.findForward("editproductwareproperty");
							return null;
						}

					} else {
						pw.write("商品不存在！");
					}

				} else if (result != null && !result.equals("checkStockinTip")
						&& !result.equals("请输入该sku的标准装箱量!")) {
					pw.write(result);
					return null;
				}
				pw.write("1");
				return null;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("method addCheckStockin exception", e);
				}
				pw.write("添加任务失败，请联系管理员！");
				return null;
			}finally{
				wareService.releaseAll();
			}
			
		}
	}

	/**
	 * 删除暂存号
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward deleteTemproraryNum(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		WareService wareService = new WareService();
		TempNumberService service = new TempNumberService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		PrintWriter pw = null;
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				pw.write("当前用户没有登录！");
				return null;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(700)) {
				pw.write("您没有权限更新暂存号！");
				return null;
			}

			String id = StringUtil.convertNull(request
					.getParameter("tempNumId"));
			if (id == null || id.equals("")) {
				pw.write("该暂存号不存在！");
				return null;
			}

			TemporaryNumberBean tempNumBean = service.getTemporaryNum("id="
					+ id);

			if (tempNumBean == null) {
				pw.write("该暂存号不存在！");
				return null;
			}
			List<String> areaIdList = CargoDeptAreaService
					.getCargoDeptAreaList(request);

			if (!areaIdList.contains(tempNumBean.getArea() + "")) {
				pw.write("您没有权限编辑该暂存号！");
				return null;
			}

			if (!service.deleteTemporaryNum("id=" + Integer.parseInt(id))) {
				pw.write("删除失败！");
				return null;
			}
			pw.write("1");
			return null;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method deleteTemproraryNum exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 更新暂存号
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward updateTemproraryNum(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		WareService wareService = new WareService();
		TempNumberService service = new TempNumberService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		PrintWriter pw = null;
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				pw.write("当前用户没有登录！");
				return null;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(701)) {
				pw.write("您没有权限更新暂存号！");
				return null;
			}

			String tempNum = StringUtil.convertNull(request
					.getParameter("tempNum"));
			request.setAttribute("tempNum", tempNum);
			if (tempNum == null || tempNum.equals("")) {
				pw.write("暂存号不能为空！");
				return null;
			}

			if (tempNum.length() > 20) {
				pw.write("暂存号长度不能超过20！");
				return null;
			}

			String id = StringUtil.convertNull(request.getParameter("id"));
			if (id == null || id.equals("")) {
				pw.write("该暂存号不存在！");
				return null;
			}

			TemporaryNumberBean tempNumBean = service.getTemporaryNum("id="
					+ id);

			if (tempNumBean == null) {
				pw.write("该暂存号不存在！");
				return null;
			}
			List<String> areaIdList = CargoDeptAreaService
					.getCargoDeptAreaList(request);

			if (!areaIdList.contains(tempNumBean.getArea() + "")) {
				pw.write("您没有权限编辑该暂存号！");
				return null;
			}

			TemporaryNumberBean tempBean = service.getTemporaryNum("name='"
					+ StringUtil.toSql(tempNum) + "' and id!="
					+ Integer.parseInt(id));
			if (tempBean != null) {
				pw.write("该暂存号已经存在，请重新填写！");
				return null;
			}

			if (!service.updateTemporaryNum(
					"name='" + StringUtil.toSql(tempNum) + "'",
					"id=" + Integer.parseInt(id))) {
				pw.write("更新失败！");
				return null;
			}
			pw.write("1");
			return null;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method updateTemproraryNum exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 弹出编辑页面
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward editTemproraryNum(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		TempNumberService service = new TempNumberService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "您还没有登录！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(701)) {
				request.setAttribute("tip", "您没有权限编辑暂存号！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			String tempNumId = StringUtil.convertNull(request
					.getParameter("tempNumId"));
			if (tempNumId == null || tempNumId.equals("")) {
				request.setAttribute("tip", "该暂存号不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			TemporaryNumberBean tempNumBean = service.getTemporaryNum("id="
					+ Integer.parseInt(tempNumId));

			if (tempNumBean == null) {
				request.setAttribute("tip", "该暂存号不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List<String> areaIdList = CargoDeptAreaService
					.getCargoDeptAreaList(request);

			if (!areaIdList.contains(tempNumBean.getArea() + "")) {
				request.setAttribute("tip", "您没有权限编辑该暂存号！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("tempNumBean", tempNumBean);
			return mapping.findForward("updateTempNum");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method editTemproraryNum exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 添加暂存号
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward addTemproraryNum(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		WareService wareService = new WareService();
		TempNumberService service = new TempNumberService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		PrintWriter pw = null;
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				pw.write("当前用户没有登录！");
				return null;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(699)) {
				pw.write("您没有权限添加暂存号！");
				return null;
			}

			String tempNum = StringUtil.convertNull(request
					.getParameter("tempNum"));
			request.setAttribute("tempNum", tempNum);
			if (tempNum == null || tempNum.equals("")) {
				pw.write("暂存号不能为空！");
				return null;
			}

			if (tempNum.length() > 20) {
				pw.write("暂存号长度不能超过20！");
				return null;
			}

			int wareArea = StringUtil.toInt(request.getParameter("wareArea"));

			if (wareArea == -1) {
				pw.write("您没有选择地区或者没有权限添加暂存号！");
				return null;
			}

			TemporaryNumberBean tempNumBean = service
					.getTemporaryNum("name='" + StringUtil.toSql(tempNum) + "'"
							+ " and area=" + wareArea);
			if (tempNumBean != null) {
				pw.write("该暂存号已经存在，请重新填写！");
				return null;
			}

			tempNumBean = new TemporaryNumberBean();
			tempNumBean.setName(tempNum);
			tempNumBean.setArea(wareArea);
			if (!service.addTemporaryNum(tempNumBean)) {
				pw.write("添加失败！");
				return null;
			}
			pw.write("添加成功！");
			return null;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method addTemproraryNum exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	@RequestMapping("/judgeExistUnqualifiedInfo")
	public String judgeExistUnqualifiedInfo(HttpServletRequest request,
			HttpServletResponse response) {
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService service = new StatService(IBaseService.CONN_IN_SERVICE,
				wareService.getDbOp());
		PrintWriter pw = null;
		try {
			response.setCharacterEncoding("utf-8");
			pw = response.getWriter();
			voUser user = (voUser) request.getSession()
					.getAttribute("userView");
			if (user == null) {
				pw.write("当前用户没有登录！");
				return null;
			}

			String missionId = StringUtil.convertNull(request
					.getParameter("missionId"));
			if (missionId == null || missionId.equals("")) {
				pw.write("参数传递错误！");
				return null;
			}
			int count = service.getCheckStockinUnqualifiedCount("mission_id="
					+ Integer.parseInt(missionId));
			if (count == 0) {
				pw.write("0");
				return null;
			}
			pw.write("1");
			return null;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method judgeExistUnqualifiedInfo exception", e);
			}
			pw.write("系统异常，请联系管理员！");
			return null;
		} finally {
			wareService.releaseAll();
		}
	}

	/**
	 * 查询暂存号
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward queryTemproraryNum(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try {

			List<String> areaIdList = CargoDeptAreaService
					.getCargoDeptAreaList(request);
			StringBuilder areaId = new StringBuilder();
			for (String str : areaIdList) {
				if (areaId.length() > 0) {
					areaId.append(",");
				}
				areaId.append(str);
			}
			if (areaId.length() == 0) {
				request.setAttribute("tip", "当前用户没有权限查询暂存号！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
			String url = null;
			if (wareArea != -1) {
				url = "checkStockinMissionAction.do?method=queryTemproraryNum&wareArea="
						+ wareArea;
			} else {
				url = "checkStockinMissionAction.do?method=queryTemproraryNum&wareArea";
			}
			// 构造页面信息
			constructPageInfo(request, wareService, url, areaId.toString(),
					wareArea);
			String wareAreaSelectLable = ProductWarePropertyService
					.getWeraAreaOptions(request);
			request.setAttribute("wareAreaSelectLable", wareAreaSelectLable);
			return mapping.findForward("queryTempNum");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("method queryTemproraryNum exception", e);
			}
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			wareService.releaseAll();
		}
	}

	public static String toUtf8String(String s, HttpServletRequest request) {
		String browserType = request.getHeader("user-agent");
		if (browserType.indexOf("Firefox") > -1) {
			String result = "";
			try {
				result = new String(s.getBytes("utf-8"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return result;
		} else {
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

	public StatService getStatService() {
		return statService;
	}

	public void setStatService(StatService statService) {
		this.statService = statService;
	}

	@RequestMapping("/beginCheck")
	public String beingCheck(HttpServletRequest request,
			HttpServletResponse response, ModelMap model,
			@RequestParam("buyStockinCode") String buyStockinCode,
			@RequestParam("batchId") int batchId,
			@RequestParam("missionId") int missionId,
			@RequestParam("productCode") String productCode) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		// groupvalidProduct
		UserGroupBean group = user.getGroup();
		boolean primission = group.isFlag(116);
		if( !primission ) {
			request.setAttribute("tip", "没有操作权限！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		model.addAttribute("buyStockinCode", buyStockinCode);
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				CheckStockinMissionBatchBean cs = statService
					.getCheckStockinMissionBatch("id=" + batchId);
				CheckStockinMissionBean cBean = statService.getCheckStockinMission("id=" + missionId);
		        if(cs==null){
		        	request.setAttribute("tip", "查询批次失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
		        }
		        if(cBean == null ) {
		        	request.setAttribute("tip", "查询任务失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
		        }
				// 根据missionId查询余量
				// int leftCount = statService.getLeftCountByMissionId(missionId);
				int leftCount = cs.getLeftCount();
				int secondStatus = cs.getSecondCheckStatus();
				int secondCount = cs.getSecondCheckCount();
				int firstStatus = cs.getFirstCheckStatus();
				
				String productId = statService
					.getProductIdbyProductCode(productCode);
				if (productId == null || "".equals(productId))
					productId = statService
						.getProductIdByProductBarcode(productCode);
				IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
				ProductBarcodeVO bBean= bService.getProductBarcode("product_id="+productId);
				if(bBean != null && bBean.getBarcode() != null ) {
					model.addAttribute("productBarCode", bBean.getBarcode());
				} else {
					model.addAttribute("productBarCode", "NOBARCODENONOENONE");
				}
				model.addAttribute("productId", productId);
				// 查询标准装箱量
				int binning = statService.queryBinning(missionId+"");
				model.addAttribute("binning", binning);
				model.addAttribute("firstStatus", firstStatus);
				model.addAttribute("secondStatus", secondStatus);
				model.addAttribute("secondCount", secondCount);
				model.addAttribute("leftCount", leftCount);
				model.addAttribute("batchId", batchId);
				model.addAttribute("missionId", missionId);
				model.addAttribute("productCode", productCode);
				model.addAttribute("firstCount", cs.getFirstCheckCount());
				model.addAttribute("firstStatus", cs.getFirstCheckStatus());
				model.addAttribute("primission", primission ? "1" : "0");
				model.addAttribute("buyStockCode", cBean.getCode());
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				wareService.releaseAll();
			}
		
		
		return "/admin/rec/oper/checkStockinMission/newBeginCheck2";
	}

	@RequestMapping("/validProduct")
	@ResponseBody
	/**
	 * csmcode:预计单号
	 * productcode:产品编号或条型码
	 */
	public String validProduct(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("productCode") String productCode,
			@RequestParam("csmCode") String csmCode) {
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

		String productId = statService.getProductIdbyProductCode(productCode);
		if (productId == null || "".equals(productId))
			productId = statService.getProductIdByProductBarcode(productCode);

		if (productId == null || "".equals(productId)) {
			return "-1";
		} else {
			// 验证商品是否属于该单号
//			boolean flag = statService.validProductByCode(productId, csmCode);
//			if (!flag)
//				return "0";
			return productId;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.getDbOp().release();
		}
		return "1";
	}

	@RequestMapping("updateFristCheck")
	@ResponseBody
	public String updateFristCheck(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("csmCode") String csmCode,
			@RequestParam("productCount") int productCount,
			@RequestParam("batchId") int batchId,
			@RequestParam("type") int type
			) {
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {


			// System.out.println("starting update------------>");
			wareService.getDbOp().startTransaction();
			
			BuyStockBean bs = statService
				.getBuyStockBeanByCode(csmCode);
				if(bs.getStatus()==BuyStockBean.STATUS6){
					return "-1,0";
				}
			// int batchid =
			// statService.getCheckStockinMissionBatchInfo(csmCode);
			CheckStockinMissionBatchBean ss = statService
					.getCheckStockinMissionBatch("id=" + batchId);
			request.getSession().setAttribute("currentCount", productCount);
			// 根据batchid更新数据库
			
			String set = "";
			if(type==2)
			set = " first_check_count=" + productCount
					+ " , first_check_status=1";
			else
				set = " second_check_count=" + productCount
				+ " , second_check_status=1";
			String condition = " id=" + batchId;
			if (statService.updateCheckStockinMissionBatch(set, condition)) {
				wareService.getDbOp().commitTransaction();

				// System.out.println("end update------------>");
				return "1," + (productCount);
			} else {
				// System.out.println("update fire-------------->");
				wareService.getDbOp().rollbackTransaction();
				return "0," + ss.getLeftCount();
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
		} finally {
			wareService.getDbOp().release();
		}

		return "";
	}

	/**
	 * 打开复录页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param buyStockinCode
	 * @param batchId
	 * @param missionId
	 * @return
	 */
	@RequestMapping("/endCheck")
	public String endCheck(HttpServletRequest request,
			HttpServletResponse response, ModelMap model,
			@RequestParam("buyStockinCode") String buyStockinCode,
			@RequestParam("batchId") String batchId,
			@RequestParam("missionId") String missionId,
			@RequestParam("productCode") String productCode) {
		model.addAttribute("buyStockinCode", buyStockinCode);
		model.addAttribute("batchId", batchId);
		model.addAttribute("missionId", missionId);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean primission = group.isFlag(116);
		if( !primission ) {
			request.setAttribute("tip", "没有操作权限！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		// group
		CheckStockinMissionBatchBean cs = null;

		try{
				// 更新任务单状态-->质检入库中
				if(!statService.updateCheckStockinMissionBatch("status=2", "mission_id="
						+ missionId)){
					request.setAttribute("tip", "更新批次质检状态失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				if(!statService.updateCheckStockinMission("status=2", "id="+missionId)){
					request.setAttribute("tip", "更新质检状态失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				// 查询标准装箱量
				int binning = statService.queryBinning(missionId);
				model.addAttribute("binning", binning);
				cs = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				CheckStockinMissionBean cBean = statService.getCheckStockinMission("id=" + missionId);
				if(cs==null){
					request.setAttribute("tip", "查询批次失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				if(cBean == null ) {
		        	request.setAttribute("tip", "查询任务失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
		        }
				// 查询初录数
				int firstCount = cs.getFirstCheckCount();
				// int currentCount = (Integer) request.getSession().getAttribute(
				// "currentCount");
				model.addAttribute("firstCount", firstCount);
				// 根据missionId查询余量
				// int leftCount =
				// statService.getLeftCountByMissionId(Integer.parseInt(missionId));
				int leftCount = cs.getLeftCount();
				//产品id
				String productId = statService
					.getProductIdbyProductCode(productCode);
				if (productId == null || "".equals(productId))
					productId = statService
						.getProductIdByProductBarcode(productCode);
				IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
				ProductBarcodeVO bBean= bService.getProductBarcode("product_id="+productId);
				if(bBean != null && bBean.getBarcode() != null ) {
					model.addAttribute("productBarCode", bBean.getBarcode());
				} else {
					model.addAttribute("productBarCode", "NOBARCODENONOENONE");
				}
				model.addAttribute("productId", productId);
				model.addAttribute("leftCount", leftCount);
				model.addAttribute("primission", primission ? "1" : "0");
				model.addAttribute("firstStatus", cs.getFirstCheckStatus());
				model.addAttribute("productCode", productCode);
				model.addAttribute("secondCount", cs.getSecondCheckCount());
				model.addAttribute("secondStatus", cs.getSecondCheckStatus());
				model.addAttribute("buyStockCode", cBean.getCode());
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.getDbOp().release();
		}
	
		
		return "/admin/rec/oper/checkStockinMission/newEndCheck2";
	}

	/**
	 * 完成复录提交功能
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param buyStockinCode
	 * @param batchId
	 * @param missionId
	 * @param binning
	 * @return
	 */
	@RequestMapping("/secondCheck")
	@ResponseBody
	public String seconkCheck(HttpServletRequest request,
			HttpServletResponse response, ModelMap model,
			@RequestParam("buyStockinCode") String buyStockinCode,
			@RequestParam("batchId") String batchId,
			@RequestParam("missionId") String missionId,
			@RequestParam("binning") int binning,
			@RequestParam("firstCount") int firstCount,
			@RequestParam("productCode") String productCode,
			@RequestParam("productCount") int productCount) {
		response.setContentType("text/html;charset=UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory
				.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		FinanceReportFormsService fService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ISupplierService supplierService = ServiceFactory
				.createSupplierService(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		CartonningInfoService cService = new CartonningInfoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int binnCount = 0;// 可以装多少箱
		int  count = firstCount % binning;
		BuyStockinBean stockin = null;
		String operationTime = "";
		PrintWriter out = null;
		CheckStockinMissionBatchBean bean1 = null;
	//	int temp = 0;
		List<String> siList = new ArrayList<String>();
		//List<String> caList = new ArrayList<String>();
		String codelist = "";
		try {
			synchronized(endCheckLock) {
				out = response.getWriter();
				wareService.getDbOp().startTransaction();
				BuyStockBean bs = statService
						.getBuyStockBeanByCode(buyStockinCode);
				if(bs.getStatus()==BuyStockBean.STATUS6){
					out.write("采购已完成，不允许执行入库操作!");
					return null;
				}
				String productId = statService
						.getProductIdbyProductCode(productCode);
				if (productId == null || "".equals(productId))
					productId = statService
							.getProductIdByProductBarcode(productCode);
				//解决重复提交
				CheckStockinMissionBatchBean batchBean = statService.getCheckStockinMissionBatch("id="+batchId);
				if(batchBean.getFirstCheckStatus()==0){
					out.write("请不要重复提交!");
					return null;
				}

				voProduct product = wareService.getProduct(Integer
						.parseInt(productId));
				
				//财务基础数据工厂
//				FinanceBaseDataService buyStockinBase = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_BUYSTOCKIN, wareService.getDbOp().getConn());
				// 判断扫描数量是否>标准装箱量
				if (firstCount >= binning) {
					binnCount = firstCount / binning;
					for (int i = 0; i < binnCount; i++) {// 循环生成采购入库单及装箱装单
//						product = wareService.getProduct(Integer
//								.parseInt(productId));
						operationTime = DateUtil.getNow();
						stockin = createBuyStockinBeanInfo(service, bs,
								operationTime, user);
						// 添加入库单
						if(!service.addBuyStockin(stockin)){
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库单失败!");
							return null;
						}
						if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库单失败!");
							return null;
						}
						stockin.setId(service.getDbOp().getLastInsertId());

						// log记录
						BuyAdminHistoryBean log = createBuyAdminHistoryBeanInfo(
								user, stockin, bs, 1, product);

						if (!service.addBuyAdminHistory(log)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("在添加生成入库单日志时，数据库操作出错！");
							return null;
						}

						// 添加对应的商品
						int buyStockProductId = product.getId();
						BuyStockProductBean bsp = service
								.getBuyStockProduct("buy_stock_id = "
										+ bs.getId() + " and product_id = "
										+ buyStockProductId);
						if (bsp == null) {
							wareService.getDbOp().rollbackTransaction();
							out.write("在对应的采购预计单中 没有找到对应的商品！");
							return null;
						}
						BuyStockinProductBean bsip = createBuyStockinProductBeanInfo(
								wareService, psService, service, stockin,
								operationTime, bsp, product, binning);
						if (!service.addBuyStockinProduct(bsip)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加添加采购入库单商品时 数据库操作失败！");
							return null;
						}

						List<Object> bsipList = new ArrayList<Object>();
						bsipList.add(bsip);
						//财务基础数据
						List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
						// log记录
						log = createBuyAdminHistoryBeanInfo(user, stockin, bs,
								2, product);

						if (!service.addBuyAdminHistory(log)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加日志时，数据库操作失败！");
							return null;
						}

						// 以下为审核流程及财务流程

						// 获取税点
						BuyOrderBean buyOrder = service
								.getBuyOrder("id=(select buy_order_id from buy_stock where id="
										+ bs.getId() + ")");
						double taxPoint = 0;
						if (buyOrder != null) {
							taxPoint = buyOrder.getTaxPoint();
						}
						// 审核采购入库单
						stockin.setStatus(BuyStockinBean.STATUS6);
						stockin.setAuditingUserId(user.getId());
						// 更新状态为已审核
						if(!service.updateBuyStockin("status=6", "id="
									+ stockin.getId())){
							wareService.getDbOp().rollbackTransaction();
							out.write("审核入库单失败，请检查后再试!");
							return null;
						}
						// log记录
						double totalMoney = 0;
					
						
						// 财务入库明细表添加数据
						// 计算此入库单，入库的总金额
//						if (bsipList != null && bsipList.size() > 0) {
//							BuyStockinProductBean bsipb = null;
//							for (int j = 0; j < bsipList.size(); j++) {
//								bsipb = (BuyStockinProductBean) bsipList.get(j);
//
//								//财务基本数据
//								BaseProductInfo base = new BaseProductInfo();
//								base.setId(bsipb.getProductId());
//								base.setInCount(bsipb.getStockInCount());
//								base.setInPrice(bsipb.getPrice3());
//								base.setProductStockId(bsip.getStockInId());
//								baseList.add(base);
//								
//							}
//						}
						
						
						BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(
								user, stockin, bs, 3, product);
						if (!service.addBuyAdminHistory(log1)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加日志时，数据库操作失败！");
							return null;
						}
//						String set = null;
						ProductStockBean ps = null;
						bsip.setPrice3(Double.valueOf(
								String.valueOf(Arith.mul(bsip.getPrice3(),
										Arith.add(1, taxPoint)))).floatValue());

						
						
						
						if (bsip.getStockInCount() <= 0) {
							wareService.getDbOp().rollbackTransaction();
							out.write("采购入库量不能为0，操作失败！");
							return null;
						}

						if (product.getIsPackage() == 1) {
							wareService.getDbOp().rollbackTransaction();
							out.write("入库单中包含有套装产品，不能入库！");
							return null;
						}
						product.setPsList(psService.getProductStockList(
								"product_id=" + product.getId(), -1, -1, null));
						ps = psService.getProductStock("id="
								+ bsip.getStockInId());
						
						float price5 = 0f;
						float price3 = 0f;
						price3 = bsip.getPrice3();
						int totalCount = product.getStockAll()
								+ product.getLockCountAll();
						price5 = (
									(float) Math.round(
											(product.getPrice5()* (totalCount) + 
													(bsip.getPrice3() * bsip.getStockInCount())
											)
											/ (totalCount + bsip.getStockInCount())
										* 1000
									)
								) / 1000;

						bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
						bsip.setRemark("操作前库存" + ps.getStock() + ",操作后库存"
								+ (ps.getStock() + bsip.getStockInCount()));
						bsip.setConfirmDatetime(operationTime);
						
						product.setPrice5(price5);//在下次循环中作为库存均价使用
						
						// --------------liuruilan----------------

						// 更新货位库存2011-04-19
						CargoInfoAreaBean inCargoArea = cargoService
								.getCargoInfoArea("old_id = " + ps.getArea());
						CargoProductStockBean cps = null;
						CargoInfoBean cargo = null;
						List cocList = cargoService
								.getCargoAndProductStockList(
										"ci.stock_type = " + ps.getType()
												+ " and ci.area_id = "
												+ inCargoArea.getId()
												+ " and ci.store_type = "
												+ CargoInfoBean.STORE_TYPE2
												+ " and cps.product_id = "
												+ bsip.getProductId(), -1, -1,
										"ci.id desc");

						// 更改待验库对应货位库存
						if (cocList == null || cocList.size() == 0) {// 产品首次入库，无暂存区绑定货位库存信息
							cargo = cargoService.getCargoInfo("stock_type = "
									+ ps.getType() + " and area_id = "
									+ inCargoArea.getId()
									+ " and store_type = "
									+ CargoInfoBean.STORE_TYPE2);
							if (cargo == null) {
								wareService.getDbOp().rollbackTransaction();
								out.write("目的待验库缓存区货位未设置，请先添加后再完成入库！");
								return null;
							}
							cps = new CargoProductStockBean();
							cps.setCargoId(cargo.getId());
							cps.setProductId(bsip.getProductId());
							// 注意这里不加货位库存了所以 stockCount 改为0直接加入到锁定中
							cps.setStockCount(0);
							cargoService.addCargoProductStock(cps);
							cps.setId(cargoService.getDbOp().getLastInsertId());

//							if (!cargoService.updateCargoInfo("status = "
//									+ CargoInfoBean.STATUS0,
//									"id = " + cps.getCargoId())) {
//								out.write("获取目的货位时，发生错误！");
//								return null;
//							}
						} else {
							cps = (CargoProductStockBean) cocList.get(0);
						}
						//更新库存
						cargoService.updateCargoProductStock("stock_count=stock_count+"+binning, "id="+cps.getId());
						psService.updateProductStock("stock=stock+"+binning, "id="+ps.getId());
						// 添加批次记录
						// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
						StockBatchBean batch = null;
						float batchPrice = 0;
						batch = service
								.getStockBatch("code='" + bs.getCode()
										+ "' and product_id = "
										+ bsip.getProductId()
										+ " and stock_type = "
										+ ProductStockBean.STOCKTYPE_CHECK
										+ " and stock_area = "
										+ stockin.getStockArea());
						if (batch == null) {
							batchPrice = bsip.getPrice3();
						} else {
							batchPrice = batch.getPrice();
						}

						// 判断并插入供货商关联信息
						voProductSupplier productSupplier = supplierService
								.getProductSupplierInfo("product_id = "
										+ bsip.getProductId()
										+ " and supplier_id = "
										+ bsip.getProductProxyId());
						if (productSupplier == null) {
							SupplierStandardInfoBean supplierStandardInfo = supplierService
									.getSupplierStandardInfo("id = "
											+ bsip.getProductProxyId());

							productSupplier = new voProductSupplier();
							productSupplier.setProduct_id(bsip.getProductId());
							productSupplier.setSupplier_id(bsip
									.getProductProxyId());
							if (supplierStandardInfo != null) {
								productSupplier
										.setSupplier_name(supplierStandardInfo
												.getName());
							}
							if (!supplierService
									.addProductSupplierInfo(productSupplier)) {
								wareService.getDbOp().rollbackTransaction();
								out.write("添加供货商信息时，数据库操作失败！");
								return null;
							}
						}
//						if (!service.completeBuyOrder(user, bs, buyOrder, true)) {
//							adminService.getDbOperation().rollbackTransaction();
//							out.write("自动完成订单失败！");
//							return null;
//						}

						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList(
								"product_id=" + bsip.getProductId(), -1, -1,
								null));
						cps = (CargoProductStockBean) cargoService
								.getCargoAndProductStockList(
										"cps.id = " + cps.getId(), 0, 1,
										"cps.id asc").get(0);

						// 计算入库金额
						double totalPrice = Arith.mul(bsip.getStockInCount(), batchPrice);

						// 入库卡片
						StockCardBean sc = createStockCardBeanInfo(product,
								stockin, bsip, totalPrice, price5,operationTime);
						if (!psService.addStockCard(sc)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库卡片时， 数据库操作失败！");
							return null;
						}


						// 货位入库卡片
						CargoStockCardBean csc = createCargoStockCardBeanInfo(
								stockin, bsip, product, price5, totalPrice, sc,
								cps,operationTime);
						if (!cargoService.addCargoStockCard(csc)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加货位入库卡片时，数据库操作失败！");
							return null;
						}

						// 拼装 bean 为打印时提供全部信息
						bsip.setTotalStockBeforeStockin(sc.getAllStock()
								- bsip.getStockInCount());
						if (wareService
								.getProductLine("product_line_catalog.catalog_id="
										+ product.getParentId1()) != null) {
							String productLineName = wareService
									.getProductLine(
											"product_line_catalog.catalog_id="
													+ product.getParentId1())
									.getName();
							product.setProductLineName(productLineName);
						} else if (wareService
								.getProductLine("product_line_catalog.catalog_id="
										+ product.getParentId2()) != null) {
							String productLineName = wareService
									.getProductLine(
											"product_line_catalog.catalog_id="
													+ product.getParentId2())
									.getName();
							product.setProductLineName(productLineName);
						} else if (wareService
								.getProductLine("product_line_catalog.catalog_id="
										+ product.getParentId3()) != null) {
							String productLineName = wareService
									.getProductLine(
											"product_line_catalog.catalog_id="
													+ product.getParentId3())
									.getName();
							product.setProductLineName(productLineName);
						} else {
							product.setProductLineName("");
						}

						// 生成装箱单
						String code = cService.getZXCodeForToday();
						CartonningInfoBean bean = new CartonningInfoBean();
						bean.setCode(code);
						bean.setCreateTime(operationTime);
						bean.setStatus(1);
						bean.setName(user.getUsername());
						bean.setCargoId(cps.getCargoId());
						bean.setBuyStockInId(stockin.getId());
						CartonningProductInfoBean productBean = new CartonningProductInfoBean();
						productBean.setProductCount(binning);
						productBean.setProductCode(product.getCode());
						productBean.setProductName(product.getOriname());
						productBean.setProductId(product.getId());
						bean.setProductBean(productBean);
						if (!cService.addCartonningInfo(bean)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("创建装箱单失败!");
							return null;
						}
						if( !cService.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
							wareService.getDbOp().rollbackTransaction();
							out.write("创建装箱单失败!");
							return null;
						}
						code = bean.getCode();
						
						
//						bean = cService.getCartonningInfo("code='"
//								+ bean.getCode() + "'");
						bean.setId(cService.getDbOp().getLastInsertId());
						//添加质检上架数据的地方
						BuyStockinUpshelfBean bsuBean = new BuyStockinUpshelfBean();
						bsuBean.setBuyStockinId(stockin.getId());
						bsuBean.setBuyStockinDatetime(stockin.getConfirmDatetime());
						bsuBean.setProductId(product.getId());
						bsuBean.setProductCode(product.getCode());
						bsuBean.setCartonningInfoId(bean.getId());
						bsuBean.setCartonningInfoName(bean.getName());
						bsuBean.setCargoOperationId(0);
						bsuBean.setWareArea(stockin.getStockArea());
						if( !checkStockinMissionService.addBuyStockinUpshelf(bsuBean)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("创建入库单和装箱单关联失败!");
							return null;
						}
						
						
						productBean.setCartonningId(bean.getId());
						cService.addCartonningProductInfo(productBean);
						
						siList.add(stockin.getCode()+"-"+code);

						// 采购入库明细
						CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
								stockin, product, missionId, bsip, bean,operationTime);

						if (!statService.addCheckStockinMissionDetail(csmdBean)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库明细失败！");
							return null;
						}
						//wareService.getDbOp().commitTransaction();
						//跟新财务数据
//						buyStockinBase.acquireFinanceBaseData(baseList, stockin.getCode(), user.getId(),1,stockin.getStockArea());
					}
					// 更新复录状态及余量信息
					if (!statService
							.updateCheckStockinMissionBatch(
									"second_check_count="+count+",first_check_count="+count+",current_check_count=current_check_count+"+binning*binnCount+",second_check_status=0,first_check_status=0,left_count="
											+ count, "id=" + batchId)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("更新复录状态及余量信息时出错，请联系管理员!");
						return null;
					}
					
					wareService.getDbOp().commitTransaction();
				} 
				bean1 = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				
				if(siList.size()>0){
					for(int s = 0;s<siList.size();s++){
						codelist += siList.get(s)+",";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			boolean isAuto = false;
			try {
				isAuto = service.getDbOp().getConn().getAutoCommit();
			} catch (SQLException e1) {}
			if( !isAuto ) {
				service.getDbOp().rollbackTransaction();
			}
		} finally {
			wareService.getDbOp().release();
		}
		//返回值为录入成功！,余量,入库号-装箱号
		out.write("入库成功!," + bean1.getLeftCount()+","+codelist);
		return null;
	}

	
	
	/**
	 * 生成采购入库单
	 * 
	 * @param service
	 * @param bs
	 * @param operationTime
	 * @param user
	 * @return
	 */
	public BuyStockinBean createBuyStockinBeanInfo(IStockService service,
			BuyStockBean bs, String operationTime, voUser user) {
		BuyStockinBean stockin = null;
			stockin = new BuyStockinBean();
			String buyStockCode = service.generateBuyStockinCodeBref();
			stockin.setCode(buyStockCode);
			stockin.setCreateDatetime(operationTime);
			stockin.setConfirmDatetime(operationTime);
			stockin.setStatus(BuyStockinBean.STATUS0);
			stockin.setBuyStockId(bs.getId());
			stockin.setBuyOrderId(bs.getBuyOrderId());
			stockin.setAuditingUserId(user.getId());
			//stockin.setId(service.getNumber("id", "buy_stockin", "max", "id > 0") + 1);
			stockin.setSupplierId(bs.getSupplierId());
			if (ProductStockBean.areaMap.get(bs.getArea()) != null) {
				stockin.setStockArea(bs.getArea());
			}
			stockin.setCreateUserId(user.getId());
			stockin.setCreateUserName(user.getName());
			stockin.setRemark(operationTime + "入库");
			stockin.setStockType(ProductStockBean.STOCKTYPE_CHECK);
		
		return stockin;
	}

	/**
	 * 生成日志
	 * 
	 * @param user
	 * @param stockin
	 * @param bs
	 * @return
	 */
	public BuyAdminHistoryBean createBuyAdminHistoryBeanInfo(voUser user,
			BuyStockinBean stockin, BuyStockBean bs, int flag, voProduct product) {
		BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(stockin.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			if (flag == 1)
				log.setRemark("转换成采购入库单，来源预计到货表：" + bs.getCode());
			else if (flag == 2)
				log.setRemark("添加采购入库单商品[" + product.getCode() + "]");
			else if (flag == 3)
				log.setRemark("审核通过");
			else if (flag == 4)
				log.setRemark("入库已完成");

			if (flag == 3 || flag == 4)
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			else
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
		
		return log;
	}

	/**
	 * 生成入库单包含产品实体
	 * 
	 * @param adminService
	 * @param psService
	 * @param service
	 * @param stockin
	 * @param operationTime
	 * @param bsp
	 * @param product
	 * @param binning
	 * @return
	 */

	public BuyStockinProductBean createBuyStockinProductBeanInfo(
			WareService wareService, IProductStockService psService,
			IStockService service, BuyStockinBean stockin,
			String operationTime, BuyStockProductBean bsp, voProduct product,
			int binning) {
		BuyStockinProductBean bsip = null;
			bsip = new BuyStockinProductBean();
//			voProduct product1 = wareService.getProduct(bsp.getProductId());
			ProductStockBean psb = psService.getProductStock("product_id="
					+ product.getId() + " and type=" + stockin.getStockType()
					+ " and area=" + stockin.getStockArea());
//			int bsipId = service.getNumber("id", "buy_stockin_product", "max",
//					"id > 0") + 1;
			bsip.setCreateDatetime(operationTime);
			bsip.setConfirmDatetime(operationTime);
			//bsip.setId(bsipId);
			bsip.setBuyStockinId(stockin.getId());
			bsip.setStockInId(psb.getId());
			bsip.setProductCode(product.getCode());
			bsip.setProductId(product.getId());
			bsip.setRemark("操作后库存增加:" + binning);
			bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
			bsip.setPrice3(bsp.getPurchasePrice());
			bsip.setProductProxyId(bsp.getProductProxyId());
			bsip.setOriname(product.getOriname());
			bsip.setStockInCount(binning);// 入库量，即为标准装箱量
		
		return bsip;
	}

	/**
	 * 生成FinanceBuyProductBean
	 * @param bsipb
	 * @param stockin
	 * @param buyOrder
	 * @param operationTime
	 * @return
	 */
//	public FinanceBuyProductBean createFinanceBuyProductBeanInfo(
//			BuyStockinProductBean bsipb, BuyStockinBean stockin,
//			BuyOrderBean buyOrder,String operationTime) {
//		FinanceBuyProductBean fbpb = null;
//			fbpb = new FinanceBuyProductBean();
//			fbpb.setBillsNumCode(stockin.getCode());
//			fbpb.setBuyOrderCode(buyOrder.getCode());
//			fbpb.setCreateDateTime(operationTime);
//			fbpb.setProductCount(bsipb.getStockInCount());
//			fbpb.setTaxPoint(Arith.round(buyOrder.getTaxPoint(), 4));
//			fbpb.setTicket(buyOrder.getTicket());
//			// 税后单价
//			fbpb.setProductPrice(Arith.round(
//					Arith.mul(bsipb.getPrice3(),
//							Arith.add(1, buyOrder.getTaxPoint())), 3));
//			fbpb.setProductId(bsipb.getProductId());
//			fbpb.setSupplierId(bsipb.getProductProxyId());
//			fbpb.setType(0);// 采购入库单用0表示，具体查看FinanceBuyPayBean类
//		
//		return fbpb;
//	}

	/**
	 * create financebuypaybean
	 * @param buyOrder
	 * @param stockin
	 * @param totalMoney
	 * @param operationTime
	 * @return
	 */
	public FinanceBuyPayBean createFinanceBuyPayBeanInfo(BuyOrderBean buyOrder,
			BuyStockinBean stockin, double totalMoney,String operationTime) {
		FinanceBuyPayBean fbp = null;
			fbp = new FinanceBuyPayBean();
			fbp.setTaxPoint(Arith.round(buyOrder.getTaxPoint(), 4));
			fbp.setTicket(buyOrder.getTicket());
			fbp.setBillsNumCode(stockin.getCode());
			fbp.setType(0);// 采购入库单用0表示，具体查看FinanceBuyPayBean类
			fbp.setSupplierId(buyOrder.getProxyId());
			// fbp.setBalanceMode(buyOrder.getBalanceMode());
			fbp.setBuyOrderCode(buyOrder.getCode());

			fbp.setCreateDateTime(operationTime);
			fbp.setMoney(totalMoney);
		
		return fbp;

	}

	/**
	 * 生成库存批次
	 * @param bs
	 * @param stockin
	 * @param bsip
	 * @param ps
	 * @param ticket
	 * @param operationTime
	 * @return
	 */
//	public StockBatchBean createStockBatchBeanInfo(BuyStockBean bs,
//			BuyStockinBean stockin, BuyStockinProductBean bsip,
//			ProductStockBean ps, int ticket,String operationTime) {
//		StockBatchBean batch = null;
//			batch = new StockBatchBean();
//			batch.setCode(bs.getCode());
//			batch.setProductId(bsip.getProductId());
//			batch.setPrice(bsip.getPrice3());
//			batch.setBatchCount(bsip.getStockInCount());
//			batch.setProductStockId(ps.getId());
//			batch.setStockArea(stockin.getStockArea());
//			batch.setStockType(ProductStockBean.STOCKTYPE_CHECK);
//			batch.setCreateDateTime(operationTime);
//			batch.setTicket(ticket);
//		
//		return batch;
//	}

	/**
	 * stockbatch日志
	 * @param batch
	 * @param user
	 * @param bsip
	 * @param operationTime
	 * @return
	 */
	public StockBatchLogBean createStockBatchLogBeanInfo(StockBatchBean batch,
			voUser user, BuyStockinProductBean bsip,String operationTime) {
		StockBatchLogBean batchLog = null;
			batchLog = new StockBatchLogBean();
			batchLog.setCode(batch.getCode());
			batchLog.setStockType(batch.getStockType());
			batchLog.setStockArea(batch.getStockArea());
			batchLog.setBatchCode(batch.getCode());
			batchLog.setBatchCount(bsip.getStockInCount());
			batchLog.setBatchPrice(batch.getPrice());
			batchLog.setProductId(batch.getProductId());
			batchLog.setRemark("采购入库");
			batchLog.setCreateDatetime(operationTime);
			batchLog.setUserId(user.getId());
		
		return batchLog;
	}

	/**
	 * 库存
	 * @param product
	 * @param stockin
	 * @param bsip
	 * @param totalPrice
	 * @param price5
	 * @param operationTime
	 * @return
	 */
	public StockCardBean createStockCardBeanInfo(voProduct product,
			BuyStockinBean stockin, BuyStockinProductBean bsip,
			double totalPrice, float price5,String operationTime) {
		StockCardBean sc = null;
			sc = new StockCardBean();
			sc.setCardType(StockCardBean.CARDTYPE_BUYSTOCKIN);
			sc.setCode(stockin.getCode());
			sc.setCreateDatetime(operationTime);
			sc.setStockType(stockin.getStockType());
			sc.setStockArea(stockin.getStockArea());
			sc.setProductId(bsip.getProductId());
			sc.setStockId(bsip.getStockInId());
			sc.setStockInCount(bsip.getStockInCount());
			sc.setStockInPriceSum(totalPrice);

			// 由于前面并没有把库存实际的加到 库存当中去 而是在后面 的调拨中直接锁在了 库存当中 所以这里的库存会存在出入
			// 要加上入库的存量
			//System.out.println(product.getStock(sc.getStockArea(),
					//sc.getStockType()));
			//System.out.println(product.getLockCount(sc.getStockArea(), sc.getStockType()));
			sc.setCurrentStock(product.getStock(sc.getStockArea(),
					sc.getStockType())
					+ product.getLockCount(sc.getStockArea(), sc.getStockType())
					);
			sc.setStockAllArea(product.getStock(stockin.getStockArea())
					+ product.getLockCount(stockin.getStockArea())
					);
			sc.setStockAllType(product.getStockAllType(stockin.getStockType())
					+ product.getLockCountAllType(stockin.getStockType())
					 );
			sc.setAllStock(product.getStockAll() + product.getLockCountAll()
					);
			sc.setStockPrice(price5);
			sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
					new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice())))
					.doubleValue());
		
		return sc;
	}

	/**
	 * 生成库存卡片
	 * @param service
	 * @param ticket
	 * @param sc
	 * @param price5
	 * @param bs
	 * @param bsip
	 * @param totalPrice
	 * @param priceSumHasticket
	 * @param priceSumNoticket
	 * @param priceHasticket
	 * @param priceNoticket
	 * @param _count
	 * @param operationTime
	 * @return
	 */

	public FinanceStockCardBean createFinanceStockCardBeanInfo(
			IStockService service, int ticket, StockCardBean sc, float price5,
			BuyStockBean bs, BuyStockinProductBean bsip, double totalPrice,
			float priceSumHasticket, float priceSumNoticket,
			float priceHasticket, float priceNoticket, int _count,String operationTime) {
		FinanceStockCardBean fsc = null;
			fsc = new FinanceStockCardBean();
			int currentStock = FinanceStockCardBean.getCurrentStockCount(
					service.getDbOp(), sc.getStockArea(), sc.getStockType(),
					ticket, sc.getProductId());
			int stockAllType = FinanceStockCardBean.getCurrentStockCount(
					service.getDbOp(), -1, sc.getStockType(), ticket,
					sc.getProductId());
			int stockAllArea = FinanceStockCardBean.getCurrentStockCount(
					service.getDbOp(), sc.getStockArea(), -1, ticket,
					sc.getProductId());
			fsc.setCardType(sc.getCardType());
			fsc.setCode(sc.getCode());
			fsc.setCreateDatetime(operationTime);
			fsc.setStockType(sc.getStockType());
			fsc.setStockArea(sc.getStockArea());
			fsc.setProductId(sc.getProductId());
			fsc.setStockId(sc.getStockId());
			fsc.setStockInCount(sc.getStockInCount());
			fsc.setCurrentStock(currentStock); // 只记录分库总库存
			fsc.setStockAllArea(stockAllArea);
			fsc.setStockAllType(stockAllType);
			fsc.setAllStock(sc.getAllStock());
			fsc.setStockPrice(price5);
			// fsc.setAllStockPriceSum((new
			// BigDecimal(sc.getAllStock())).multiply(new
			// BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());

			fsc.setType(fsc.getCardType());
			fsc.setIsTicket(ticket);
			fsc.setStockBatchCode(bs.getCode());
			fsc.setBalanceModeStockCount(bsip.getStockInCount() + _count);
			if (ticket == 0) {
				fsc.setStockInPriceSum(Arith.round(Arith.div(totalPrice, 1.17), 2));
				fsc.setBalanceModeStockPrice(Double.parseDouble(String
						.valueOf(priceHasticket)));
			}
			if (ticket == 1) {
				fsc.setStockInPriceSum(Arith.round(totalPrice, 2));
				fsc.setBalanceModeStockPrice(Double.parseDouble(String
						.valueOf(priceNoticket)));
			}
			double tmpPrice = Double.parseDouble(String.valueOf(Arith.add(
					priceSumHasticket, priceSumNoticket)));
			fsc.setAllStockPriceSum(tmpPrice);
		
		return fsc;
	}

	/**
	 * 生成货位进销存卡片
	 * @param stockin
	 * @param bsip
	 * @param product
	 * @param price5
	 * @param totalPrice
	 * @param sc
	 * @param cps
	 * @param operationTime
	 * @return
	 */
	public CargoStockCardBean createCargoStockCardBeanInfo(
			BuyStockinBean stockin, BuyStockinProductBean bsip,
			voProduct product, float price5, double totalPrice,
			StockCardBean sc, CargoProductStockBean cps,String operationTime) {
		CargoStockCardBean csc = null;
			csc = new CargoStockCardBean();
			csc.setCardType(CargoStockCardBean.CARDTYPE_BUYSTOCKIN);
			csc.setCode(stockin.getCode());
			csc.setCreateDatetime(operationTime);
			csc.setStockType(stockin.getStockType());
			csc.setStockArea(stockin.getStockArea());
			csc.setProductId(bsip.getProductId());
			csc.setStockId(cps.getId());
			csc.setStockInCount(bsip.getStockInCount());
			csc.setStockInPriceSum(totalPrice);
			csc.setCurrentStock(product.getStock(sc.getStockArea(),
					sc.getStockType())
					+ product.getLockCount(sc.getStockArea(), sc.getStockType())
					);
			csc.setAllStock(product.getStockAll() + product.getLockCountAll()
					);
			csc.setCurrentCargoStock(cps.getStockCount() + cps.getStockLockCount()
					);
			csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
			csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
			csc.setStockPrice(price5);
			csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
					new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice())))
					.doubleValue());
		
		return csc;
	}

	/**
	 * 生成入库明细
	 * 
	 * @param stockin
	 * @param product
	 * @param missionId
	 * @param bsip
	 * @param bean
	 * @return
	 */
	public CheckStockinMissionDetailBean createCheckStockinMissionDetailBeanInfo(
			BuyStockinBean stockin, voProduct product, String missionId,
			BuyStockinProductBean bsip, CartonningInfoBean bean,String operationTime) {
		CheckStockinMissionDetailBean csmdBean =  null;
			csmdBean = new CheckStockinMissionDetailBean();
			csmdBean.setBuyStockinCode(stockin.getCode());
			csmdBean.setBuyStockinCount(bsip.getStockInCount());
			csmdBean.setBuyStockinCreateDateTime(operationTime);
			csmdBean.setBuyStockinId(stockin.getId());
			csmdBean.setMissionId(Integer.parseInt(missionId));// 这里有可能有问题，待测试
			csmdBean.setProductCode(product.getCode());
			csmdBean.setProductId(product.getId());
			csmdBean.setCartonningName(bean.getCode());
		
		return csmdBean;
	}

	/**
	 * 手工提交
	 * 
	 * @return
	 */
//	@RequestMapping("noAutoCheck")
//	@ResponseBody
//	public String noAutoCheck(HttpServletRequest request,
//			HttpServletResponse response, ModelMap model,
//			@RequestParam("buyStockinCode") String buyStockinCode,
//			@RequestParam("batchId") String batchId,
//			@RequestParam("missionId") String missionId,
//			@RequestParam("binning") int binning,
//			@RequestParam("firstCount") int firstCount,
//			@RequestParam("productCode") String productCode,
//			@RequestParam("productCount") int productCount) {
//		response.setContentType("text/html;charset=UTF-8");
//
//		voUser user = (voUser) request.getSession().getAttribute("userView");
//		if (user == null) {
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return "/admin/error";
//		}
//		WareService wareService = new WareService();
//		StatService statService = ServiceFactory.createStatServiceStat(
//				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		IStockService service = ServiceFactory.createStockService(
//				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		IProductStockService psService = ServiceFactory
//				.createProductStockService(IBaseService.CONN_IN_SERVICE,
//						wareService.getDbOp());
//		FinanceReportFormsService fService = new FinanceReportFormsService(
//				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		ICargoService cargoService = ServiceFactory.createCargoService(
//				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		ISupplierService supplierService = ServiceFactory
//				.createSupplierService(IBaseService.CONN_IN_SERVICE,
//						wareService.getDbOp());
//		CartonningInfoService cService = new CartonningInfoService(
//				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
//		int binnCount = 0;// 可以装多少箱
//		int count = productCount % binning;
//
//		BuyStockinBean stockin = null;
//		String operationTime = "";
//		PrintWriter out = null;
//		CheckStockinMissionBatchBean bean1 = null;
//		List<String> siList = new ArrayList<String>();
//		String codelist = "";
//		try {
//			synchronized(endCheckLock) {
//				wareService.getDbOp().startTransaction();
//				out = response.getWriter();
//				BuyStockBean bs = statService
//						.getBuyStockBeanByCode(buyStockinCode);
//				if(bs.getStatus()==BuyStockBean.STATUS6){
//					out.write("采购已完成，不允许执行入库操作!");
//					return null;
//				}
//				String productId = statService
//						.getProductIdbyProductCode(productCode);
//				if (productId == null || "".equals(productId))
//					productId = statService
//							.getProductIdByProductBarcode(productCode);
//
//				//解决重复提交
//				CheckStockinMissionBatchBean batchBean = statService.getCheckStockinMissionBatch("id="+batchId);
//				if(batchBean.getFirstCheckStatus()==0){
//					out.write("请不要重复提交!");
//					return null;
//				}
//				voProduct product = wareService.getProduct(Integer
//						.parseInt(productId));
//				int temp =0;
//				// 判断扫描数量是否>标准装箱量
//				if (productCount >= binning) {
//					binnCount = productCount / binning;
//
//					for (int i = 0; i < binnCount; i++) {// 循环生成采购入库单及装箱装单
//						operationTime = DateUtil.getNow();
//						stockin = createBuyStockinBeanInfo(service, bs,
//								operationTime, user);
//						if (!service.addBuyStockin(stockin)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加入库单失败!");
//							return null;
//						}
//						if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加入库单失败!");
//							return null;
//						}
//	                    List stockinList = service.getBuyStockinList("buy_stock_id="
//								+ bs.getId() + " and buy_order_id="
//								+ bs.getBuyOrderId()+" and create_datetime='"+operationTime+"'", -1, -1, "id");
//	                    if(stockinList!=null && stockinList.size()>0)
//	                    	stockin = (BuyStockinBean)stockinList.get(stockinList.size()-1);
//
//						// log记录
//						BuyAdminHistoryBean log = createBuyAdminHistoryBeanInfo(
//								user, stockin, bs, 1, product);
//
//						if (!service.addBuyAdminHistory(log)) {
//							out.write("在添加生成入库单日志时，数据库操作出错！");
//							return null;
//						}
//
//						// 添加对应的商品
//						int buyStockProductId = product.getId();
//						BuyStockProductBean bsp = service
//								.getBuyStockProduct("buy_stock_id = "
//										+ bs.getId() + " and product_id = "
//										+ buyStockProductId);
//						if (bsp == null) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("在对应的采购预计单中 没有找到对应的商品！");
//							return null;
//						}
//						BuyStockinProductBean bsip = createBuyStockinProductBeanInfo(
//								wareService, psService, service, stockin,
//								operationTime, bsp, product, binning);
//
//						List<Object> bsipList = new ArrayList<Object>();
//						bsipList.add(bsip);
//
//						// log记录
//						log = createBuyAdminHistoryBeanInfo(user, stockin, bs,
//								2, product);
//
//						if (!service.addBuyAdminHistory(log)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加日志时，数据库操作失败！");
//							return null;
//						}
//
//						// 以下为审核流程及财务流程
//
//						// 获取税点
//						BuyOrderBean buyOrder = service
//								.getBuyOrder("id=(select buy_order_id from buy_stock where id="
//										+ bs.getId() + ")");
//						double taxPoint = 0;
//						if (buyOrder != null) {
//							taxPoint = buyOrder.getTaxPoint();
//						}
//						// 审核采购入库单
//						stockin.setStatus(BuyStockinBean.STATUS6);
//						stockin.setAuditingUserId(user.getId());
//						// 更新状态为已审核
//						if(!service.updateBuyStockin("status=6", "id="
//									+ stockin.getId())){
//							wareService.getDbOp().rollbackTransaction();
//							out.write("审核入库单失败，请检查后再试!");
//							return null;
//						}
//						// log记录
//						double totalMoney = 0;
//						// 财务入库明细表添加数据
//						// 计算此入库单，入库的总金额
//						if (bsipList != null && bsipList.size() > 0) {
//							BuyStockinProductBean bsipb = null;
//							FinanceBuyProductBean fbpb = null;
//							ResultSet rs = null;
//							String sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
//									+ "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
//							for (int j = 0; j < bsipList.size(); j++) {
//								bsipb = (BuyStockinProductBean) bsipList.get(j);
//
//								fbpb = createFinanceBuyProductBeanInfo(bsipb,
//										stockin, buyOrder,operationTime);
//								rs = fService.getDbOp().executeQuery(
//										sql + bsipb.getProductId());
//								while (rs.next()) {
//
//									fbpb.setProductLineId(rs.getInt(1));// 添加产品线
//								}
//								if (!fService.addFinanceBuyProductBean(fbpb)) {
//									wareService.getDbOp().rollbackTransaction();
//									out.write("添加财务数据时，数据库操作失败！");
//									return null;
//								}
//								totalMoney = Arith.add(totalMoney, Arith.mul(
//										bsipb.getStockInCount(),
//										Arith.mul(bsipb.getPrice3(),
//												Arith.add(1, taxPoint))));
//								totalMoney = Arith.round(totalMoney, 2);
//							}
//						}
//						// 入库单后，为财务表添加数据
//						FinanceBuyPayBean fbp = createFinanceBuyPayBeanInfo(
//								buyOrder, stockin, totalMoney,operationTime);
//						if (!fService.addFinanceBuyPayBean(fbp)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加财务数据，数据库操作失败！");
//							return null;
//						}// 财务表添加
//							// ---------------------------------
//						BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(
//								user, stockin, bs, 3, product);
//						if (!service.addBuyAdminHistory(log1)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加日志时，数据库操作失败！");
//							return null;
//						}
//						String set = null;
//						ProductStockBean ps = null;
//						float _price3 = bsip.getPrice3();
//						bsip.setPrice3(Double.valueOf(
//								String.valueOf(Arith.mul(bsip.getPrice3(),
//										Arith.add(1, taxPoint)))).floatValue());
//
//						if (bsip.getStockInCount() <= 0) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("采购入库量不能为0，操作失败！");
//							return null;
//						}
//
//						if (product.getIsPackage() == 1) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("入库单中包含有套装产品，不能入库！");
//							return null;
//						}
//						product.setPsList(psService.getProductStockList(
//								"product_id=" + product.getId(), -1, -1, null));
//						ps = psService.getProductStock("id="
//								+ bsip.getStockInId());
//						float price5 = 0f;
//						float price3 = 0f;
//						price3 = bsip.getPrice3();
//						int totalCount = product.getStockAll()
//								+ product.getLockCountAll();
//						price5 = ((float) Math.round((product.getPrice5()
//								* (totalCount) + (bsip.getPrice3() * bsip
//								.getStockInCount()))
//								/ (totalCount + bsip.getStockInCount()) * 1000)) / 1000;
//
//						bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
//						bsip.setRemark("操作前库存" + ps.getStock() + ",操作后库存"
//								+ (ps.getStock() + bsip.getStockInCount()));
//						bsip.setConfirmDatetime(operationTime);
//						if(!service.getDbOp().executeUpdate(
//									"update product set price5=" + price5
//											+ " where id = " + product.getId())){
//							wareService.getDbOp().rollbackTransaction();
//							out.write("更新商品状态失败!");
//							return null;
//						}
//						product.setPrice5(price5);//在下次循环中作为库存均价使用
//						
//						// 财务数据填充：finance_product表--------liuruilan---------
//						FinanceProductBean fProduct = fService
//								.getFinanceProductBean("product_id = "
//										+ product.getId());
//						if (fProduct == null) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("查询异常，请与管理员联系！");
//							return null;
//						}
//						float priceSum = Arith.mul(price5,
//								totalCount + bsip.getStockInCount());
//						float priceHasticket = 0;
//						float priceNoticket = 0;
//						float priceSumHasticket = fProduct
//								.getPriceSumHasticket();
//						float priceSumNoticket = fProduct.getPriceSumNoticket();
//						int ticket = FinanceSellProductBean.queryTicket(
//								service.getDbOp(), bs.getCode()); // 是否含票
//						if (ticket == -1) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("查询异常，请与管理员联系！");
//							return null;
//						}
//						int _count = FinanceProductBean.queryCountIfTicket(
//								service.getDbOp(), bsip.getProductId(), ticket);
//						set = "price =" + price5 + ", price_sum =" + priceSum;
//						if (ticket == 0) { // 0-有票
//							_price3 = (float) Arith.div(
//									Arith.mul(_price3, Arith.add(1, taxPoint)),
//									1.17);
//							
//							priceHasticket = Arith.round(Arith.div(
//									Arith.add(
//											fProduct.getPriceSumHasticket(),
//											Arith.mul(_price3,
//													bsip.getStockInCount())),
//									Arith.add(_count, bsip.getStockInCount())),
//									2);
//							priceSumHasticket = Arith.mul(priceHasticket,
//									bsip.getStockInCount() + _count);
//							set += ", price_hasticket =" + priceHasticket
//									+ ", price_sum_hasticket ="
//									+ priceSumHasticket;
//						}
//						if (ticket == 1) { // 1-无票
//							_price3 = (float) Arith.mul(_price3,
//									Arith.add(1, taxPoint));
//							priceNoticket = Arith.round(Arith.div(
//									Arith.add(
//											fProduct.getPriceSumNoticket(),
//											Arith.mul(_price3,
//													bsip.getStockInCount())),
//									Arith.add(_count, bsip.getStockInCount())),
//									2);
//							priceSumNoticket = Arith.mul(priceNoticket,
//									bsip.getStockInCount() + _count);
//							set += ", price_noticket =" + priceNoticket
//									+ ", price_sum_noticket ="
//									+ priceSumNoticket;
//						}
//						if (!fService.updateFinanceProductBean(set,
//								"product_id = " + product.getId())) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("修改操作数据时，数据库操作失败！");
//							return null;
//						}
//						// --------------liuruilan----------------
//
//						// 更新货位库存2011-04-19
//						CargoInfoAreaBean inCargoArea = cargoService
//								.getCargoInfoArea("old_id = " + ps.getArea());
//						CargoProductStockBean cps = null;
//						CargoInfoBean cargo = null;
//						List cocList = cargoService
//								.getCargoAndProductStockList(
//										"ci.stock_type = " + ps.getType()
//												+ " and ci.area_id = "
//												+ inCargoArea.getId()
//												+ " and ci.store_type = "
//												+ CargoInfoBean.STORE_TYPE2
//												+ " and cps.product_id = "
//												+ bsip.getProductId(), -1, -1,
//										"ci.id desc");
//
//						// 更改待验库对应货位库存
//						if (cocList == null || cocList.size() == 0) {// 产品首次入库，无暂存区绑定货位库存信息
//							cargo = cargoService.getCargoInfo("stock_type = "
//									+ ps.getType() + " and area_id = "
//									+ inCargoArea.getId()
//									+ " and store_type = "
//									+ CargoInfoBean.STORE_TYPE2);
//							if (cargo == null) {
//								wareService.getDbOp().rollbackTransaction();
//								out.write("目的待验库缓存区货位未设置，请先添加后再完成入库！");
//								return null;
//							}
//							cps = new CargoProductStockBean();
//							cps.setCargoId(cargo.getId());
//							cps.setProductId(bsip.getProductId());
//							// 注意这里不加货位库存了所以 stockCount 改为0直接加入到锁定中
//							cps.setStockCount(0);
//							cargoService.addCargoProductStock(cps);
//							cps.setId(cargoService.getDbOp().getLastInsertId());
//						} else {
//							cps = (CargoProductStockBean) cocList.get(0);
//						}
//						//更新库存
//						cargoService.updateCargoProductStock("stock_count=stock_count+"+binning, "id="+cps.getId());
//						psService.updateProductStock("stock=stock+"+binning, "id="+ps.getId());
//						// 更新订单已入库量和已入库总金额
//						String area = "";
//						if (stockin.getStockArea() == ProductStockBean.AREA_BJ) {
//							area = "bj";
//						} else if (stockin.getStockArea() == ProductStockBean.AREA_GF) {
//							area = "gd";
//						} else if (stockin.getStockArea() == ProductStockBean.AREA_ZC) {// 增城用北京的字段
//							area = "bj";
//						} else if (stockin.getStockArea() == ProductStockBean.AREA_WX) {
//							area = "gd";
//						}
//						set = "stockin_count_" + area + "=(stockin_count_"
//								+ area + " + " + bsip.getStockInCount() + "),"
//								+ "stockin_total_price =(stockin_total_price+"
//								+ bsip.getStockInCount() * bsip.getPrice3()
//								+ ")";
//						String condition2 = "buy_order_id="
//								+ bs.getBuyOrderId() + " and product_id="
//								+ product.getId();
//						if (!service.updateBuyOrderProduct(set, condition2)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("修改采购订单数据时，数据库操作失败！");
//							return null;
//						}
//
//						// 添加批次记录
//						// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
//						StockBatchBean batch = null;
//						batch = service
//								.getStockBatch("code='" + bs.getCode()
//										+ "' and product_id = "
//										+ bsip.getProductId()
//										+ " and stock_type = "
//										+ ProductStockBean.STOCKTYPE_CHECK
//										+ " and stock_area = "
//										+ stockin.getStockArea());
//						if (batch == null) {
//							batch = createStockBatchBeanInfo(bs, stockin, bsip,
//									ps, ticket,operationTime);
//							service.addStockBatch(batch);
//						} else {
//							if (!service.updateStockBatch(
//									"batch_count = "
//											+ (batch.getBatchCount() + bsip
//													.getStockInCount()),
//									"code='" + bs.getCode()
//											+ "' and product_id = "
//											+ bsip.getProductId()
//											+ " and stock_type = "
//											+ ProductStockBean.STOCKTYPE_CHECK
//											+ " and stock_area = "
//											+ stockin.getStockArea())) {
//								wareService.getDbOp().rollbackTransaction();
//								out.write("修改批次 信息时， 数据库操作失败！");
//								return null;
//							}
//						}
//
//						// 添加批次操作记录
//						StockBatchLogBean batchLog = createStockBatchLogBeanInfo(
//								batch, user, bsip,operationTime);
//						if (!service.addStockBatchLog(batchLog)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加批次信息时，数据库操作失败！");
//							return null;
//						}
//
//						// 判断并插入供货商关联信息
//						voProductSupplier productSupplier = supplierService
//								.getProductSupplierInfo("product_id = "
//										+ bsip.getProductId()
//										+ " and supplier_id = "
//										+ bsip.getProductProxyId());
//						if (productSupplier == null) {
//							SupplierStandardInfoBean supplierStandardInfo = supplierService
//									.getSupplierStandardInfo("id = "
//											+ bsip.getProductProxyId());
//
//							productSupplier = new voProductSupplier();
//							productSupplier.setProduct_id(bsip.getProductId());
//							productSupplier.setSupplier_id(bsip
//									.getProductProxyId());
//							if (supplierStandardInfo != null) {
//								productSupplier
//										.setSupplier_name(supplierStandardInfo
//												.getName());
//							}
//							if (!supplierService
//									.addProductSupplierInfo(productSupplier)) {
//								wareService.getDbOp().rollbackTransaction();
//								out.write("添加供货商信息时，数据库操作失败！");
//								return null;
//							}
//						}
//
//						if (!service.addBuyStockinProduct(bsip)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加添加采购入库单商品时 数据库操作失败！");
//							return null;
//						}
//
//						// 审核通过，就加 进销存卡片
//						product.setPsList(psService.getProductStockList(
//								"product_id=" + bsip.getProductId(), -1, -1,
//								null));
//						cps = (CargoProductStockBean) cargoService
//								.getCargoAndProductStockList(
//										"cps.id = " + cps.getId(), 0, 1,
//										"cps.id asc").get(0);
//
//						// 计算入库金额
//						double totalPrice = bsip.getStockInCount()
//								* batchLog.getBatchPrice();
//
//						// 入库卡片
//						StockCardBean sc = createStockCardBeanInfo(product,
//								stockin, bsip, totalPrice, price5,operationTime);
//						if (!psService.addStockCard(sc)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加入库卡片时， 数据库操作失败！");
//							return null;
//						}
//
//						// 财务进销存卡片---liuruilan-----
//						FinanceStockCardBean fsc = createFinanceStockCardBeanInfo(
//								service, ticket, sc, price5, bs, bsip,
//								totalPrice, priceSumHasticket,
//								priceSumNoticket, priceHasticket,
//								priceNoticket, _count,operationTime);
//						if (!fService.addFinanceStockCardBean(fsc)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加财务卡片时， 数据库操作失败！");
//							return null;
//						}
//
//						// 货位入库卡片
//						CargoStockCardBean csc = createCargoStockCardBeanInfo(
//								stockin, bsip, product, price5, totalPrice, sc,
//								cps,operationTime);
//						if (!cargoService.addCargoStockCard(csc)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加货位入库卡片时，数据库操作失败！");
//							return null;
//						}
//
//						// 拼装 bean 为打印时提供全部信息
//						bsip.setTotalStockBeforeStockin(sc.getAllStock()
//								- bsip.getStockInCount());
//						if (wareService
//								.getProductLine("product_line_catalog.catalog_id="
//										+ product.getParentId1()) != null) {
//							String productLineName = wareService
//									.getProductLine(
//											"product_line_catalog.catalog_id="
//													+ product.getParentId1())
//									.getName();
//							product.setProductLineName(productLineName);
//						} else if (wareService
//								.getProductLine("product_line_catalog.catalog_id="
//										+ product.getParentId2()) != null) {
//							String productLineName = wareService
//									.getProductLine(
//											"product_line_catalog.catalog_id="
//													+ product.getParentId2())
//									.getName();
//							product.setProductLineName(productLineName);
//						} else if (wareService
//								.getProductLine("product_line_catalog.catalog_id="
//										+ product.getParentId3()) != null) {
//							String productLineName = wareService
//									.getProductLine(
//											"product_line_catalog.catalog_id="
//													+ product.getParentId3())
//									.getName();
//							product.setProductLineName(productLineName);
//						} else {
//							product.setProductLineName("");
//						}
//
//						// 生成装箱单
//						String code = cService.getZXCodeForToday();
//						CartonningInfoBean bean = new CartonningInfoBean();
//						bean.setCode(code);
//						bean.setCreateTime(operationTime);
//						bean.setStatus(1);
//						bean.setName(user.getUsername());
//						bean.setCargoId(cps.getCargoId());
//						bean.setBuyStockInId(stockin.getId());
//						CartonningProductInfoBean productBean = new CartonningProductInfoBean();
//						productBean.setProductCount(binning);
//						productBean.setProductCode(product.getCode());
//						productBean.setProductName(product.getOriname());
//						productBean.setProductId(product.getId());
//						bean.setProductBean(productBean);
//						if (!cService.addCartonningInfo(bean)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("创建装箱单失败!");
//							return null;
//						}
//						if( !cService.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("创建装箱单失败!");
//							return null;
//						}
//						code = bean.getCode();
//						bean = cService.getCartonningInfo("code='"
//								+ bean.getCode() + "'");
//						//添加质检上架关联的地方
//						BuyStockinUpshelfBean bsuBean = new BuyStockinUpshelfBean();
//						bsuBean.setBuyStockinId(stockin.getId());
//						bsuBean.setBuyStockinDatetime(stockin.getConfirmDatetime());
//						bsuBean.setProductId(product.getId());
//						bsuBean.setProductCode(product.getCode());
//						bsuBean.setCartonningInfoId(bean.getId());
//						bsuBean.setCartonningInfoName(bean.getName());
//						bsuBean.setCargoOperationId(0);
//						bsuBean.setWareArea(stockin.getStockArea());
//						if( !checkStockinMissionService.addBuyStockinUpshelf(bsuBean)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("创建入库单和装箱单关联失败!");
//							return null;
//						}
//						productBean.setCartonningId(bean.getId());
//						cService.addCartonningProductInfo(productBean);
//
//						// 采购入库明细
//						CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
//								stockin, product, missionId, bsip, bean,operationTime);
//
//						if (!statService.addCheckStockinMissionDetail(csmdBean)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加入库明细失败！");
//							return null;
//						}
//						siList.add(stockin.getCode()+"-"+code);
//						//wareService.getDbOp().commitTransaction();
//
//					}
//					// 更新复录状态及余量信息
//					if (!statService
//							.updateCheckStockinMissionBatch(
//									"second_check_count="+count+",first_check_count="+count+",current_check_count=current_check_count+"+binning*binnCount+",second_check_status=0,first_check_status=0,left_count="
//											+ count, "id=" + batchId)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("更新复录状态及余量信息时出错，请联系管理员!");
//						return null;
//					}
//					wareService.getDbOp().commitTransaction();
//
//				} else {// 不足标准装箱量
//					operationTime = DateUtil.getNow();
//					stockin = createBuyStockinBeanInfo(service, bs,
//							operationTime, user);
//					// 添加入库单
//					if (!service.addBuyStockin(stockin)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加入库单失败!");
//						return null;
//					}
//					if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加入库单失败!");
//						return null;
//					}
//					List stockinList = service.getBuyStockinList("buy_stock_id="
//							+ bs.getId() + " and buy_order_id="
//							+ bs.getBuyOrderId()+" and create_datetime='"+operationTime+"'", -1, -1, "id");
//	                if(stockinList!=null && stockinList.size()>0)
//	                	stockin = (BuyStockinBean)stockinList.get(stockinList.size()-1);
//
//					// log记录
//					BuyAdminHistoryBean log = createBuyAdminHistoryBeanInfo(
//							user, stockin, bs, 1, product);
//
//					if (!service.addBuyAdminHistory(log)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("在添加生成入库单日志时，数据库操作出错！");
//						return null;
//					}
//
//					// 添加对应的商品
//					int buyStockProductId = product.getId();
//					BuyStockProductBean bsp = service
//							.getBuyStockProduct("buy_stock_id = " + bs.getId()
//									+ " and product_id = " + buyStockProductId);
//					if (bsp == null) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("在对应的采购预计单中 没有找到对应的商品！");
//						return null;
//					}
//					BuyStockinProductBean bsip = createBuyStockinProductBeanInfo(
//							wareService, psService, service, stockin,
//							operationTime, bsp, product, count);// 这里传的参数是余量
//
//					List<Object> bsipList = new ArrayList<Object>();
//					bsipList.add(bsip);
//
//					// log记录
//					log = createBuyAdminHistoryBeanInfo(user, stockin, bs, 2,
//							product);
//
//					if (!service.addBuyAdminHistory(log)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加日志时，数据库操作失败！");
//						return null;
//					}
//
//					// 以下为审核流程及财务流程
//
//					// 获取税点
//					BuyOrderBean buyOrder = service
//							.getBuyOrder("id=(select buy_order_id from buy_stock where id="
//									+ bs.getId() + ")");
//					double taxPoint = 0;
//					if (buyOrder != null) {
//						taxPoint = buyOrder.getTaxPoint();
//					}
//					// 审核采购入库单
//					stockin.setStatus(BuyStockinBean.STATUS6);
//					stockin.setAuditingUserId(user.getId());
//					// 更新状态为已审核
//					if(!service.updateBuyStockin("status=6",
//							"id=" + stockin.getId()))				
//					 {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("审核入库单失败，请检查后再试!");
//						return null;
//					}
//					// log记录
//					double totalMoney = 0;
//					// 财务入库明细表添加数据
//					// 计算此入库单，入库的总金额
//					if (bsipList != null && bsipList.size() > 0) {
//						BuyStockinProductBean bsipb = null;
//						FinanceBuyProductBean fbpb = null;
//						ResultSet rs = null;
//						String sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
//								+ "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
//						for (int j = 0; j < bsipList.size(); j++) {
//							bsipb = (BuyStockinProductBean) bsipList.get(j);
//
//							fbpb = createFinanceBuyProductBeanInfo(bsipb,
//									stockin, buyOrder,operationTime);
//							rs = fService.getDbOp().executeQuery(
//									sql + bsipb.getProductId());
//							while (rs.next()) {
//
//								fbpb.setProductLineId(rs.getInt(1));// 添加产品线
//							}
//							if (!fService.addFinanceBuyProductBean(fbpb)) {
//								wareService.getDbOp().rollbackTransaction();
//								out.write("添加财务数据时，数据库操作失败！");
//								return null;
//							}
//							totalMoney = Arith.add(
//									totalMoney,
//									Arith.mul(
//											bsipb.getStockInCount(),
//											Arith.mul(bsipb.getPrice3(),
//													Arith.add(1, taxPoint))));
//							totalMoney = Arith.round(totalMoney, 2);
//						}
//					}
//					// 入库单后，为财务表添加数据
//					FinanceBuyPayBean fbp = createFinanceBuyPayBeanInfo(
//							buyOrder, stockin, totalMoney,operationTime);
//					if (!fService.addFinanceBuyPayBean(fbp)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加财务数据，数据库操作失败！");
//						return null;
//					}// 财务表添加
//						// ---------------------------------
//					BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(
//							user, stockin, bs, 3, product);
//					if (!service.addBuyAdminHistory(log1)) {
//						out.write("添加日志时，数据库操作失败！");
//						return null;
//					}
//					String set = null;
//					ProductStockBean ps = null;
//					float _price3 = bsip.getPrice3();
//					bsip.setPrice3(Double.valueOf(
//							String.valueOf(Arith.mul(bsip.getPrice3(),
//									Arith.add(1, taxPoint)))).floatValue());
//
//					if (bsip.getStockInCount() <= 0) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("采购入库量不能为0，操作失败！");
//						return null;
//					}
//
//					if (product.getIsPackage() == 1) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("入库单中包含有套装产品，不能入库！");
//						return null;
//					}
//					product.setPsList(psService.getProductStockList(
//							"product_id=" + product.getId(), -1, -1, null));
//					ps = psService.getProductStock("id=" + bsip.getStockInId());
//					float price5 = 0f;
//					float price3 = 0f;
//					price3 = bsip.getPrice3();
//					int totalCount = product.getStockAll()
//							+ product.getLockCountAll();
//					price5 = ((float) Math.round((product.getPrice5()
//							* (totalCount) + (bsip.getPrice3() * bsip
//							.getStockInCount()))
//							/ (totalCount + bsip.getStockInCount()) * 1000)) / 1000;
//
//					bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
//					bsip.setRemark("操作前库存" + ps.getStock() + ",操作后库存"
//							+ (ps.getStock() + bsip.getStockInCount()));
//					bsip.setConfirmDatetime(operationTime);
//					try {
//						service.getDbOp().executeUpdate(
//								"update product set price5=" + price5
//										+ " where id = " + product.getId());
//					} catch (Exception e) {
//						e.printStackTrace();
//						wareService.getDbOp().rollbackTransaction();
//						out.write("更新商品状态失败!");
//						return null;
//					}
//
//					// 财务数据填充：finance_product表--------liuruilan---------
//					FinanceProductBean fProduct = fService
//							.getFinanceProductBean("product_id = "
//									+ product.getId());
//					if (fProduct == null) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("查询异常，请与管理员联系！");
//						return null;
//					}
//					float priceSum = Arith.mul(price5,
//							totalCount + bsip.getStockInCount());
//					float priceHasticket = 0;
//					float priceNoticket = 0;
//					float priceSumHasticket = fProduct.getPriceSumHasticket();
//					float priceSumNoticket = fProduct.getPriceSumNoticket();
//					int ticket = FinanceSellProductBean.queryTicket(
//							service.getDbOp(), bs.getCode()); // 是否含票
//					if (ticket == -1) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("查询异常，请与管理员联系！");
//						return null;
//					}
//					int _count = FinanceProductBean.queryCountIfTicket(
//							service.getDbOp(), bsip.getProductId(), ticket);
//					set = "price =" + price5 + ", price_sum =" + priceSum;
//					if (ticket == 0) { // 0-有票
//						_price3 = (float) Arith.div(
//								Arith.mul(_price3, Arith.add(1, taxPoint)),
//								1.17);
//						// 计算公式：(fProduct.getPriceSumHasticket() + (_price3 *
//						// _count)) / (totalCount + _count)
//						priceHasticket = Arith.round(Arith.div(Arith.add(
//								fProduct.getPriceSumHasticket(),
//								Arith.mul(_price3, bsip.getStockInCount())),
//								Arith.add(_count, bsip.getStockInCount())), 2);
//						priceSumHasticket = Arith.mul(priceHasticket,
//								bsip.getStockInCount() + _count);
//						set += ", price_hasticket =" + priceHasticket
//								+ ", price_sum_hasticket =" + priceSumHasticket;
//					}
//					if (ticket == 1) { // 1-无票
//						_price3 = (float) Arith.mul(_price3,
//								Arith.add(1, taxPoint));
//						priceNoticket = Arith.round(Arith.div(
//								Arith.add(
//										fProduct.getPriceSumNoticket(),
//										Arith.mul(_price3,
//												bsip.getStockInCount())),
//								Arith.add(_count, bsip.getStockInCount())), 2);
//						priceSumNoticket = Arith.mul(priceNoticket,
//								bsip.getStockInCount() + _count);
//						set += ", price_noticket =" + priceNoticket
//								+ ", price_sum_noticket =" + priceSumNoticket;
//					}
//					if (!fService.updateFinanceProductBean(set, "product_id = "
//							+ product.getId())) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("修改操作数据时，数据库操作失败！");
//						return null;
//					}
//					// --------------liuruilan----------------
//
//					// 更新货位库存2011-04-19
//					CargoInfoAreaBean inCargoArea = cargoService
//							.getCargoInfoArea("old_id = " + ps.getArea());
//					CargoProductStockBean cps = null;
//					CargoInfoBean cargo = null;
//					List cocList = cargoService
//							.getCargoAndProductStockList(
//									"ci.stock_type = " + ps.getType()
//											+ " and ci.area_id = "
//											+ inCargoArea.getId()
//											+ " and ci.store_type = "
//											+ CargoInfoBean.STORE_TYPE2
//											+ " and cps.product_id = "
//											+ bsip.getProductId(), -1, -1,
//									"ci.id desc");
//
//					// 更改待验库对应货位库存
//					if (cocList == null || cocList.size() == 0) {// 产品首次入库，无暂存区绑定货位库存信息
//						cargo = cargoService.getCargoInfo("stock_type = "
//								+ ps.getType() + " and area_id = "
//								+ inCargoArea.getId() + " and store_type = "
//								+ CargoInfoBean.STORE_TYPE2);
//						if (cargo == null) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("目的待验库缓存区货位未设置，请先添加后再完成入库！");
//							return null;
//						}
//						cps = new CargoProductStockBean();
//						cps.setCargoId(cargo.getId());
//						cps.setProductId(bsip.getProductId());
//						// 注意这里不加货位库存了所以 stockCount 改为0直接加入到锁定中
//						cps.setStockCount(0);
//						cargoService.addCargoProductStock(cps);
//						cps.setId(cargoService.getDbOp().getLastInsertId());
//					} else {
//						cps = (CargoProductStockBean) cocList.get(0);
//					}
//					//更新库存
//					cargoService.updateCargoProductStock("stock_count=stock_count+"+count, "id="+cps.getId());
//					psService.updateProductStock("stock=stock+"+count, "id="+ps.getId());
//					// 更新订单已入库量和已入库总金额
//					String area = "";
//					if (stockin.getStockArea() == ProductStockBean.AREA_BJ) {
//						area = "bj";
//					} else if (stockin.getStockArea() == ProductStockBean.AREA_GF) {
//						area = "gd";
//					} else if (stockin.getStockArea() == ProductStockBean.AREA_ZC) {// 增城用北京的字段
//						area = "bj";
//					} else if (stockin.getStockArea() == ProductStockBean.AREA_WX) {
//						area = "gd";
//					}
//					set = "stockin_count_" + area + "=(stockin_count_" + area
//							+ " + " + bsip.getStockInCount() + "),"
//							+ "stockin_total_price =(stockin_total_price+"
//							+ bsip.getStockInCount() * bsip.getPrice3() + ")";
//					String condition2 = "buy_order_id=" + bs.getBuyOrderId()
//							+ " and product_id=" + product.getId();
//					if (!service.updateBuyOrderProduct(set, condition2)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("修改采购订单数据时，数据库操作失败！");
//						return null;
//					}
//
//					// 添加批次记录
//					// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
//					StockBatchBean batch = null;
//					batch = service.getStockBatch("code='" + bs.getCode()
//							+ "' and product_id = " + bsip.getProductId()
//							+ " and stock_type = "
//							+ ProductStockBean.STOCKTYPE_CHECK
//							+ " and stock_area = " + stockin.getStockArea());
//					if (batch == null) {
//						batch = createStockBatchBeanInfo(bs, stockin, bsip, ps,
//								ticket,operationTime);
//						service.addStockBatch(batch);
//					} else {
//						if (!service.updateStockBatch(
//								"batch_count = "
//										+ (batch.getBatchCount() + bsip
//												.getStockInCount()),
//								"code='" + bs.getCode() + "' and product_id = "
//										+ bsip.getProductId()
//										+ " and stock_type = "
//										+ ProductStockBean.STOCKTYPE_CHECK
//										+ " and stock_area = "
//										+ stockin.getStockArea())) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("修改批次 信息时， 数据库操作失败！");
//							return null;
//						}
//					}
//
//					// 添加批次操作记录
//					StockBatchLogBean batchLog = createStockBatchLogBeanInfo(
//							batch, user, bsip,operationTime);
//					if (!service.addStockBatchLog(batchLog)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加批次信息时，数据库操作失败！");
//						return null;
//					}
//
//					// 判断并插入供货商关联信息
//					voProductSupplier productSupplier = supplierService
//							.getProductSupplierInfo("product_id = "
//									+ bsip.getProductId()
//									+ " and supplier_id = "
//									+ bsip.getProductProxyId());
//					if (productSupplier == null) {
//						SupplierStandardInfoBean supplierStandardInfo = supplierService
//								.getSupplierStandardInfo("id = "
//										+ bsip.getProductProxyId());
//
//						productSupplier = new voProductSupplier();
//						productSupplier.setProduct_id(bsip.getProductId());
//						productSupplier
//								.setSupplier_id(bsip.getProductProxyId());
//						if (supplierStandardInfo != null) {
//							productSupplier
//									.setSupplier_name(supplierStandardInfo
//											.getName());
//						}
//						if (!supplierService
//								.addProductSupplierInfo(productSupplier)) {
//							wareService.getDbOp().rollbackTransaction();
//							out.write("添加供货商信息时，数据库操作失败！");
//							return null;
//						}
//					}
//
//					if (!service.addBuyStockinProduct(bsip)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加添加采购入库单商品时 数据库操作失败！");
//						return null;
//					}
//
//					// 审核通过，就加 进销存卡片
//					product.setPsList(psService.getProductStockList(
//							"product_id=" + bsip.getProductId(), -1, -1, null));
//					cps = (CargoProductStockBean) cargoService
//							.getCargoAndProductStockList(
//									"cps.id = " + cps.getId(), 0, 1,
//									"cps.id asc").get(0);
//
//					// 计算入库金额
//					double totalPrice = bsip.getStockInCount()
//							* batchLog.getBatchPrice();
//
//					// 入库卡片
//					StockCardBean sc = createStockCardBeanInfo(product,
//							stockin, bsip, totalPrice, price5,operationTime);
//					if (!psService.addStockCard(sc)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加入库卡片时， 数据库操作失败！");
//						return null;
//					}
//
//					// 财务进销存卡片---liuruilan-----
//					FinanceStockCardBean fsc = createFinanceStockCardBeanInfo(
//							service, ticket, sc, price5, bs, bsip, totalPrice,
//							priceSumHasticket, priceSumNoticket,
//							priceHasticket, priceNoticket, _count,operationTime);
//					if (!fService.addFinanceStockCardBean(fsc)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加财务卡片时， 数据库操作失败！");
//						return null;
//					}
//
//					// 货位入库卡片
//					CargoStockCardBean csc = createCargoStockCardBeanInfo(
//							stockin, bsip, product, price5, totalPrice, sc, cps,operationTime);
//					if (!cargoService.addCargoStockCard(csc)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加货位入库卡片时，数据库操作失败！");
//						return null;
//					}
//
//					// 拼装 bean 为打印时提供全部信息
//					bsip.setTotalStockBeforeStockin(sc.getAllStock()
//							- bsip.getStockInCount());
//					if (wareService
//							.getProductLine("product_line_catalog.catalog_id="
//									+ product.getParentId1()) != null) {
//						String productLineName = wareService.getProductLine(
//								"product_line_catalog.catalog_id="
//										+ product.getParentId1()).getName();
//						product.setProductLineName(productLineName);
//					} else if (wareService
//							.getProductLine("product_line_catalog.catalog_id="
//									+ product.getParentId2()) != null) {
//						String productLineName = wareService.getProductLine(
//								"product_line_catalog.catalog_id="
//										+ product.getParentId2()).getName();
//						product.setProductLineName(productLineName);
//					} else if (wareService
//							.getProductLine("product_line_catalog.catalog_id="
//									+ product.getParentId3()) != null) {
//						String productLineName = wareService.getProductLine(
//								"product_line_catalog.catalog_id="
//										+ product.getParentId3()).getName();
//						product.setProductLineName(productLineName);
//					} else {
//						product.setProductLineName("");
//					}
//
//					// 生成装箱单
//					String code = cService.getZXCodeForToday();					
//					CartonningInfoBean bean = new CartonningInfoBean();
//					bean.setCode(code);
//					bean.setCreateTime(operationTime);
//					bean.setStatus(1);
//					bean.setName(user.getUsername());
//					bean.setCargoId(cps.getCargoId());
//					bean.setBuyStockInId(stockin.getId());
//					CartonningProductInfoBean productBean = new CartonningProductInfoBean();
//					productBean.setProductCount(count);
//					productBean.setProductCode(product.getCode());
//					productBean.setProductName(product.getOriname());
//					productBean.setProductId(product.getId());
//					bean.setProductBean(productBean);
//					if (!cService.addCartonningInfo(bean)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("创建装箱单失败!");
//						return null;
//					}
//					if( !cService.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("创建装箱单失败!");
//						return null;
//					}
//					code = bean.getCode();
//
//					bean = cService.getCartonningInfo("code='" + bean.getCode()
//							+ "'");
//					//添加质检上架关联的地方
//					BuyStockinUpshelfBean bsuBean = new BuyStockinUpshelfBean();
//					bsuBean.setBuyStockinId(stockin.getId());
//					bsuBean.setBuyStockinDatetime(stockin.getConfirmDatetime());
//					bsuBean.setProductId(product.getId());
//					bsuBean.setProductCode(product.getCode());
//					bsuBean.setCartonningInfoId(bean.getId());
//					bsuBean.setCartonningInfoName(bean.getName());
//					bsuBean.setCargoOperationId(0);
//					bsuBean.setWareArea(stockin.getStockArea());
//					if( !checkStockinMissionService.addBuyStockinUpshelf(bsuBean)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("创建入库单和装箱单关联失败!");
//						return null;
//					}
//					productBean.setCartonningId(bean.getId());
//					cService.addCartonningProductInfo(productBean);
//
//					// 采购入库明细
//					CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
//							stockin, product, missionId, bsip, bean,operationTime);
//					
//					siList.add(stockin.getCode()+"-"+code);
//
//					if (!statService.addCheckStockinMissionDetail(csmdBean)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("添加入库明细失败！");
//						return null;
//					}
//					// 更新复录状态及余量信息
//					if (!statService
//							.updateCheckStockinMissionBatch(
//									"second_check_count=0,first_check_count=0,current_check_count=current_check_count+"+count+",second_check_status=0,first_check_status=0,left_count=0", "id=" + batchId)) {
//						wareService.getDbOp().rollbackTransaction();
//						out.write("更新复录状态及余量信息时出错，请联系管理员!");
//						return null;
//					}
//					wareService.getDbOp().commitTransaction();
//				}
//				
//				bean1 = statService
//						.getCheckStockinMissionBatch("id=" + batchId);
//				if(siList.size()>0){
//					for(int s=0;s<siList.size();s++){
//						codelist += siList.get(s)+",";
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			boolean isAuto = false;
//			try {
//				isAuto = service.getDbOp().getConn().getAutoCommit();
//			} catch (SQLException e1) {}
//			if( !isAuto ) {
//				service.getDbOp().rollbackTransaction();
//			}
//		} finally {
//			wareService.getDbOp().release();
//		}
//		out.write("入库成功!," + bean1.getLeftCount()+","+codelist);
//		return null;
//	}


	/**
	 * 取累计录入数与定购量进行比对
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("validComplete")
	@ResponseBody
	public String validComplete(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("missionId") int missionId) {
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String flag = statService.validComplete(missionId);
		wareService.getDbOp().release();
		return flag;
	}

	/**
	 * 
	 * 打印装箱记录
	 */
	@RequestMapping("printCartonningInfo")
	public String PrintCartonningInfo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		int id = StringUtil.StringToId(request.getParameter("id"));
		WareService pService = new WareService();
		CartonningInfoService service = new CartonningInfoService(
				IBaseService.CONN_IN_SERVICE, pService.getDbOp());
		// ICargoService cargoService =
		// ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,
		// pService.getDbOperation());
		
		try {

			List<CartonningInfoBean> list = new ArrayList<CartonningInfoBean>();
			CartonningInfoBean bean = service.getCartonningInfo("id=" + id
					+ " and create_time='"
					+ request.getParameter("createTime") + "'");
			if (bean == null) {
				request.setAttribute("tip", "没有关联上装箱单！");
				request.setAttribute("result", "failure");
				return "admin/error";
			}
			CartonningProductInfoBean pBean = service
					.getCartonningProductInfo("cartonning_id="
							+ bean.getId());
			voProduct product = pService.getProduct(pBean.getProductId());
			voProductLine productLine = pService
					.getProductLine("product_line_catalog.catalog_id="
							+ product.getParentId1()
							+ " or product_line_catalog.catalog_id="
							+ product.getParentId2());
			if (productLine == null) {
				request.setAttribute("tip", "获取产品线时出错！");
				request.setAttribute("result", "failure");
				return "admin/error";
			}
			service.updateCartonningInfo("status=1", "id=" + id);
			bean.setProductBean(pBean);
			request.setAttribute("bean", bean);
			list.add(bean);
			request.setAttribute("beanList", list);
			request.setAttribute("missionId",request.getParameter("missionId"));
			request.setAttribute("batchId", request.getParameter("batchId"));
			request.setAttribute("productLine", productLine.getName());
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		
		return "forward:/admin/rec/oper/checkStockinMission/qualifyCartonningInfoPrint2.jsp";
	}
	
	/**
	 * 初始化余量
	 * @param re
	 * @param res
	 * @param batchId
	 * @return
	 */
	@RequestMapping("initLeftCount")
	@ResponseBody
	public String initLeftCount(HttpServletRequest re,HttpServletResponse res,@RequestParam("batchId") int batchId){
		WareService pService = new WareService();
		int result = 0;

		try{
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, pService.getDbOp());
		CheckStockinMissionBatchBean batchBean = statService
		.getCheckStockinMissionBatch("id=" + batchId);
		if(batchBean==null)
			return "-1";
		else
			return ""+batchBean.getLeftCount();
		}catch (Exception e) {
			e.printStackTrace();
			return "error";
		}finally{
			pService.releaseAll();
		}
	}
	
	/**
	 * 动态检测是初/复录状态
	 * @param re
	 * @param res
	 * @param batchId
	 * @param type
	 * @return
	 */
	@RequestMapping("checkStatus")
	@ResponseBody
	public String checkStatus(HttpServletRequest re,HttpServletResponse res,@RequestParam("batchId") int batchId,
			@RequestParam("type") int type){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService pService = new WareService(dbOp);
		try{
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, pService.getDbOp());
			CheckStockinMissionBatchBean batchBean = statService
					.getCheckStockinMissionBatch("id=" + batchId);
			if(type==2)
				return batchBean.getSecondCheckStatus()+","+batchBean.getSecondCheckCount();
			else
				return batchBean.getFirstCheckStatus()+","+batchBean.getFirstCheckCount();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			pService.releaseAll();
		}
		return "";
	}
	
	/**
	 * 
	 * 打印装箱记录
	 */
	@RequestMapping("printCartonningInfo1")
	public String PrintCartonningInfo(HttpServletRequest request,
			HttpServletResponse response,@RequestParam("code")String code) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		//int id = StringUtil.StringToId(request.getParameter("id"));
		WareService pService = new WareService();
		CartonningInfoService service = new CartonningInfoService(
				IBaseService.CONN_IN_SERVICE, pService.getDbOp());
		// ICargoService cargoService =
		// ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,
		// pService.getDbOperation());
		try {
			List<CartonningInfoBean> list = new ArrayList<CartonningInfoBean>();
			String[] codes = code.split(",");
			if(codes.length==0){
				request.setAttribute("tip", "装箱单编号不能为空！");
				request.setAttribute("result", "failure");
				return "admin/error";
			}
			for(int i=0;i<codes.length;i++){
				CartonningInfoBean bean = service
						.getCartonningInfo("code='" + codes[i]
								+ "'");
				if(bean==null){
					request.setAttribute("tip", "没有关联上装箱单！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				CartonningProductInfoBean pBean = service
						.getCartonningProductInfo("cartonning_id="
								+ bean.getId());
				service.updateCartonningInfo("status=1", "code='" + code+"'");
				bean.setProductBean(pBean);
				voProduct product = pService.getProduct(pBean.getProductId());
				voProductLine productLine = pService.getProductLine("product_line_catalog.catalog_id=" + product.getParentId1()+" or product_line_catalog.catalog_id=" + product.getParentId2());
				request.setAttribute("bean", bean);
				list.add(bean);
				request.setAttribute("productLine", productLine.getName());
				request.setAttribute("beanList", list);
			}
			request.setAttribute("missionId",
					request.getParameter("missionId"));
			request.setAttribute("batchId", request.getParameter("batchId"));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return "admin/cargo/qualifyCartonningInfoPrint2";
	}
	
	/**
	 * 自动完成订单
	 * @param request
	 * @param response
	 * @param model
	 * @param buyStockinCode
	 * @return
	 */
	@RequestMapping("/autocompleteorder")
	@ResponseBody
	public String autoCompleteOrder(HttpServletRequest request,
			HttpServletResponse response, ModelMap model,
			@RequestParam("buyStockinCode") String buyStockinCode){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		WareService service = new WareService();
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IStockService service1 = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, service.getDbOp());
		
		try {
			synchronized (checkStockinLock) {
				statService.getDbOp().startTransaction();
				BuyStockBean bs = statService
						.getBuyStockBeanByCode(buyStockinCode);
				if (bs == null) {
					return "-1";
				}
				BuyOrderBean buyOrder = service1
						.getBuyOrder("id=(select buy_order_id from buy_stock where id="
								+ bs.getId() + ")");
				if (!service1.completeBuyOrder(user, bs, buyOrder, true)) {
					statService.getDbOp().rollbackTransaction();
					return "0";
				}
				statService.getDbOp().commitTransaction();
			}
		} catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		
		return "1";
	}
	
	/**
	 * 修改标准装箱量
	 */
	@RequestMapping("changeBinning")
	@ResponseBody
	public String changeBinning(HttpServletRequest request,HttpServletResponse req,@RequestParam("binning") int binning,@RequestParam("productId") String productId){
		WareService service = new WareService();
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ProductWarePropertyService ps = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			return "-1";
		}

		try{
			service.getDbOp().startTransaction();
			ProductWarePropertyBean pb = statService.getProductWareProperty("product_id="+productId);
			boolean flag = statService.updateProductWareProperty("cartonning_standard_count="+binning, "product_id="+productId);
			int propertyId = pb.getId();
			ProductWarePropertyLogBean bean = new ProductWarePropertyLogBean();
			bean.setTime(DateUtil.getNow());
			bean.setProductWarePropertyId(propertyId);
			bean.setOperId(user.getId());
			bean.setOperName(user.getUsername());
			bean.setOperDetail("标准装箱量:"+pb.getCartonningStandardCount()+"->"+binning);
			boolean flag1 = ps.addProductWarePropertyLog(bean);
			service.getDbOp().commitTransaction();
			return flag&&flag1?"1":"0";
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			return "0";
		}finally{
			service.releaseAll();
		}
	
	}

	/**
	 * @name 获得质检入库上架单信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getBuyStockinUpShelfList")
	@ResponseBody
	public List<Map<String,String>> getBuyStockinUpShelfList(HttpServletRequest request,HttpServletResponse response) {
		
		List<Map<String,String>> result = new ArrayList<Map<String, String>>();
		int pageIndex = -1;
		int countPerPage = 20;
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return result;
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(883);
		if (!permission) {
			request.setAttribute("tip", "您没有采购上架列表权限！");
			request.setAttribute("result", "failure");
			return result;
		}

		String checkinDatetime1 = StringUtil.convertNull(request.getParameter("checkinDatetime1"));
		String checkinDatetime2 = StringUtil.convertNull(request.getParameter("checkinDatetime2"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String buyStockinCode = StringUtil.convertNull(request.getParameter("buyStockinCode"));
		String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode"));
		String upShelfCode = StringUtil.convertNull(request.getParameter("upShelfCode"));
		int upShelfStatus = StringUtil.toInt(request.getParameter("upShelfStatus"));
		String cartonningUsername = StringUtil.convertNull(request.getParameter("cartonningUsername"));
		String upShelfUsername = StringUtil.convertNull(request.getParameter("upShelfUsername"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
		List<BuyStockinUpshelfBean> buyStockinUpshelfList = new ArrayList<BuyStockinUpshelfBean>();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StockinUnqualifiedService sus = new StockinUnqualifiedService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StringBuilder param = new StringBuilder();
		StringBuilder parentId1 = new StringBuilder();
		StringBuilder parentId2 = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		// 根据产品线先找符合的id
		try {
			if(upShelfStatus == 1 && ((upShelfCode != null && !upShelfCode.equals("")) || (upShelfUsername != null && !upShelfUsername.equals("")) ) ) {
				return result;
			}
			// 查看用户有权限的库地区， 只查询他有权限的库地区,或以前没有地区的商品信息
			List<String> cdaList = CargoDeptAreaService
					.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int x = cdaList.size();
			for (int i = 0; i < x; i++) {
				availAreaIds += "," + cdaList.get(i);
			}
			if( upShelfStatus == -1 ) {
				if( (upShelfCode != null && !upShelfCode.equals("")) || (upShelfUsername != null && !upShelfUsername.equals("")) ) {
					sql.append("select bsu.* from buy_stockin_upshelf bsu, cargo_operation co where bsu.cargo_operation_id = co.id");
					sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu, cargo_operation co where bsu.cargo_operation_id = co.id");
				} else {
					sql.append("select bsu.* from buy_stockin_upshelf bsu where bsu.id <> 0");
					sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu where bsu.id <> 0");
				}
			} else if( upShelfStatus == 1 ) {
				sql.append("select bsu.* from buy_stockin_upshelf bsu where bsu.cargo_operation_id = 0");
				sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu where bsu.cargo_operation_id = 0");
			} else {
				sql.append("select bsu.* from buy_stockin_upshelf bsu, cargo_operation co where bsu.cargo_operation_id = co.id");
				sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu, cargo_operation co where bsu.cargo_operation_id = co.id");
			}
			//商品编号
			if (!productCode.equals("")) {
				sql.append(" and").append(" bsu.product_code='" + productCode + "'");
				sqlCount.append(" and").append(" bsu.product_code='" + productCode + "'");
			}
			//地区
			if (wareArea == -1) {
				sql.append(" and")
						.append(" bsu.ware_area in (" + availAreaIds + ")");
				sqlCount.append(" and").append(
						" bsu.ware_area in (" + availAreaIds + ")");
			} else {
				sql.append(" and").append(" bsu.ware_area=" + wareArea);
				sqlCount.append(" and").append(" bsu.ware_area=" + wareArea);
			}

			// 入库时间条件
			if ((checkinDatetime1 != null && !checkinDatetime1.equals("")) || (checkinDatetime2 != null && !checkinDatetime2.equals(""))) {
				String checkinDatetimeStart = checkinDatetime1 + " 00:00:00";
				String checkinDatetimeEnd = checkinDatetime2 + " 23:59:59";
				sql.append(" and bsu.buy_stockin_datetime between '")
						.append(StringUtil.toSql(checkinDatetimeStart))
						.append("' and '")
						.append(StringUtil.toSql(checkinDatetimeEnd)).append("'");
				sqlCount.append(" and bsu.buy_stockin_datetime between '")
						.append(StringUtil.toSql(checkinDatetimeStart))
						.append("' and '")
						.append(StringUtil.toSql(checkinDatetimeEnd)).append("'");
			}
			
			//入库单号
			if ( buyStockinCode != null && !buyStockinCode.equals("")) {
				BuyStockinBean bsBean = service.getBuyStockin("code='" + StringUtil.toSql(buyStockinCode) + "'"); 
				if( bsBean != null ) {
					sql.append(" and bsu.buy_stockin_id=").append(bsBean.getId());
					sqlCount.append(" and bsu.buy_stockin_id=").append(bsBean.getId());
				} else {
					sql.append(" and bsu.buy_stockin_id=").append(0);
					sqlCount.append(" and bsu.buy_stockin_id=").append(0);
				}
			}
			//装箱单号
			if ( cartonningCode != null && !cartonningCode.equals("")) {
				CartonningInfoBean ciBean = cService.getCartonningInfo("code='" + StringUtil.toSql(cartonningCode) + "'");
				if( ciBean != null ) {
					sql.append(" and bsu.cartonning_info_id=").append(ciBean.getId());
					sqlCount.append(" and bsu.cartonning_info_id=").append(ciBean.getId());
				} else {
					sql.append(" and bsu.cartonning_info_id=").append(0);
					sqlCount.append(" and bsu.cartonning_info_id=").append(0);
				}
			}
			//上架单号
			if( upShelfStatus != 1 && upShelfCode != null && !upShelfCode.equals("") ) {
				sql.append(" and co.code='").append(StringUtil.toSql(upShelfCode)).append("'");
				sqlCount.append(" and co.code='").append(StringUtil.toSql(upShelfCode)).append("'");
			}
			//上架员
			if( upShelfStatus != 1 && upShelfUsername != null && !upShelfUsername.equals("") ) {
				sql.append(" and co.complete_user_name='").append(StringUtil.toSql(upShelfUsername)).append("'");
				sqlCount.append(" and co.complete_user_name='").append(StringUtil.toSql(upShelfUsername)).append("'");
			}
			//装箱员
			if( cartonningUsername != null && !cartonningUsername.equals("") ) {
				sql.append(" and bsu.cartonning_info_name='").append(StringUtil.toSql(cartonningUsername)).append("'");
				sqlCount.append(" and bsu.cartonning_info_name='").append(StringUtil.toSql(cartonningUsername)).append("'");
			}
			
			if( upShelfStatus == 2 ) {
				sql.append(" and co.status in (").append("1,2,3,4,5,6").append(")");
				sql.append(" and co.effect_status in (").append("0,1").append(")");
				sqlCount.append(" and co.status in (").append("1,2,3,4,5,6").append(")");
				sqlCount.append(" and co.effect_status in (").append("0,1").append(")");
			}
			
			if( upShelfStatus == 3 ) {
				sql.append(" and co.status in (").append("7,8").append(")");
				sqlCount.append(" and co.status in (").append("7,8").append(")");
			}
			PagingBean paging = null;
			int totalCount = checkStockinMissionService.getBuyStockinUpshelfCount2(sqlCount.toString());
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			buyStockinUpshelfList = checkStockinMissionService.getBuyStockinUpshelfList2(sql.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "bsu.buy_stockin_datetime desc");

			for (int i = 0; i < buyStockinUpshelfList.size(); i++) {
				BuyStockinUpshelfBean bsuBean = (BuyStockinUpshelfBean) buyStockinUpshelfList.get(i);
				Map<String,String> map = new HashMap<String,String>();
				BuyStockinBean bsBean = service.getBuyStockin("id=" + bsuBean.getBuyStockinId());
				CartonningInfoBean ciBean = cService.getCartonningInfo("id=" + bsuBean.getCartonningInfoId());
				CartonningProductInfoBean cpiBean = cService.getCartonningProductInfo("cartonning_id=" + ciBean.getId() + " and product_id =" + bsuBean.getProductId());
				map.put("buy_stockin_code", "<a href='"+request.getContextPath()+"/admin/stock2/buyStockin.jsp?id=" + bsBean.getId() + "' target='_blank'>"+bsBean.getCode()+"</a>");
				map.put("product_code", "<a href='"+ request.getContextPath()+"/admin/fproduct.do?id=" + bsuBean.getProductId() + "' target='_blank'>" + bsuBean.getProductCode() + "</a>");
				map.put("buy_stockin_datetime", bsuBean.getBuyStockinDatetime());
				map.put("cartonning_code", "<a href='"+request.getContextPath()+"/admin/cartonningInfoAction.do?method=cartonningInfo&select="+ciBean.getCode()+"&status=0&status=1' target='_blank'>"+ciBean.getCode()+"</a>");
				map.put("cartonning_sum", new Integer(cpiBean.getProductCount()).toString());
				map.put("cartonning_username", bsuBean.getCartonningInfoName());
				bsuBean.setBuyStockinBean(bsBean);
				bsuBean.setCartonningInfoBean(ciBean);
				if( bsuBean.getCargoOperationId() != 0 ) {
					CargoOperationBean coBean = cargoService.getCargoOperation("id = " + bsuBean.getCargoOperationId());
					bsuBean.setCargoOperationBean(coBean);
					if( coBean != null ) {
						CargoOperationCargoBean cocBean = cargoService.getCargoOperationCargo("oper_id=" + coBean.getId() + " and type=0");
						map.put("cargo_operation_code", "<a href='" + request.getContextPath() + "/admin/cargoOper.do?method=showEditCargoOperation&operationId=" + coBean.getId() + "' target='_blank'>" + coBean.getCode() + "</a>");
						map.put("cargo_operation_username", StringUtil.convertNull(coBean.getCompleteUsername()));
						map.put("cargo_operation_datetime", StringUtil.convertNull(coBean.getCompleteDatetime()));
						map.put("cargo_operation_status", (coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS3 || coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS2) ? "已完成" : "上架中");
						if( cocBean != null ) {
							map.put("target_cargo_code", cocBean.getInCargoWholeCode());
						} else {
							map.put("target_cargo_code", "-");
						}
					} else {
						map.put("cargo_operation_code", "-");
						map.put("cargo_operation_username", "-");
						map.put("cargo_operation_datetime", "-");
						map.put("cargo_operation_status", "-");
						map.put("target_cargo_code", "-");
					}
				} else {
					map.put("cargo_operation_code", "-");
					map.put("cargo_operation_username", "-");
					map.put("cargo_operation_datetime", "-");
					map.put("cargo_operation_status", "待上架");
					map.put("target_cargo_code", "-");
				}
				result.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error(
						"method getUnqualifiedStorageDetailInfo exception", e);
			}
		} finally {
			statService.releaseAll();
		}
		return result;
	}

	
	/**
	 * @name 获得质检入库上架单信息
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/getBuyStockinUpShelfOtherInfo")
	public void getBuyStockinUpShelfOtherInfo(HttpServletRequest request,HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding( "UTF-8");
		int pageIndex = -1;
		int countPerPage = 20;
		voUser user = (voUser) request.getSession().getAttribute("userView");
		try{
		if (user == null) {
			response.getWriter().write("{status:'fail', tip:'没有登录不可查询!'}");
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean permission = group.isFlag(883);
		if (!permission) {
			response.getWriter().write("{status:'fail', tip:'您没有采购上架列表权限！'}");
			return;
		}
		} catch(Exception e){
			e.printStackTrace();
		}
		String checkinDatetime1 = StringUtil.convertNull(request.getParameter("checkinDatetime1"));
		String checkinDatetime2 = StringUtil.convertNull(request.getParameter("checkinDatetime2"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String buyStockinCode = StringUtil.convertNull(request.getParameter("buyStockinCode"));
		String cartonningCode = StringUtil.convertNull(request.getParameter("cartonningCode"));
		String upShelfCode = StringUtil.convertNull(request.getParameter("upShelfCode"));
		int upShelfStatus = StringUtil.toInt(request.getParameter("upShelfStatus"));
		String cartonningUsername = StringUtil.convertNull(request.getParameter("cartonningUsername"));
		String upShelfUsername = StringUtil.convertNull(request.getParameter("upShelfUsername"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		StringBuilder sqlCount = new StringBuilder();
		// 根据产品线先找符合的id
		try {
			if(upShelfStatus == 1 && ((upShelfCode != null && !upShelfCode.equals("")) || (upShelfUsername != null && !upShelfUsername.equals("")) ) ) {
				String result = "{status:'success', pageLine:'', totalCount:'0' }";
				response.getWriter().write(result);
				return;
			}
			// 查看用户有权限的库地区， 只查询他有权限的库地区,或以前没有地区的商品信息
			List<String> cdaList = CargoDeptAreaService
					.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int x = cdaList.size();
			for (int i = 0; i < x; i++) {
				availAreaIds += "," + cdaList.get(i);
			}
			if( upShelfStatus == -1 ) {
				if( (upShelfCode != null && !upShelfCode.equals("")) || (upShelfUsername != null && !upShelfUsername.equals("")) ) {
					sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu, cargo_operation co where bsu.cargo_operation_id = co.id");
				} else {
					sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu where bsu.id <> 0");
				}
			} else if( upShelfStatus == 1 ) {
				sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu where bsu.cargo_operation_id = 0");
			} else {
				sqlCount.append("select count(bsu.id) from buy_stockin_upshelf bsu, cargo_operation co where bsu.cargo_operation_id = co.id");
			}
			//商品编号
			if (!productCode.equals("")) {
				sqlCount.append(" and").append(" bsu.product_code='" + productCode + "'");
			}
			//地区
			if (wareArea == -1) {
				sqlCount.append(" and").append(
						" bsu.ware_area in (" + availAreaIds + ")");
			} else {
				sqlCount.append(" and").append(" bsu.ware_area=" + wareArea);
			}

			// 入库时间条件
			if ((checkinDatetime1 != null && !checkinDatetime1.equals("")) || (checkinDatetime2 != null && !checkinDatetime2.equals(""))) {
				String checkinDatetimeStart = checkinDatetime1 + " 00:00:00";
				String checkinDatetimeEnd = checkinDatetime2 + " 23:59:59";
				sqlCount.append(" and bsu.buy_stockin_datetime between '")
						.append(StringUtil.toSql(checkinDatetimeStart))
						.append("' and '")
						.append(StringUtil.toSql(checkinDatetimeEnd)).append("'");
			}
			
			//入库单号
			if ( buyStockinCode != null && !buyStockinCode.equals("")) {
				BuyStockinBean bsBean = service.getBuyStockin("code='" + StringUtil.toSql(buyStockinCode) + "'"); 
				if( bsBean != null ) {
					sqlCount.append(" and bsu.buy_stockin_id=").append(bsBean.getId());
				} else {
					sqlCount.append(" and bsu.buy_stockin_id=").append(0);
				}
			}
			//装箱单号
			if ( cartonningCode != null && !cartonningCode.equals("")) {
				CartonningInfoBean ciBean = cService.getCartonningInfo("code='" + StringUtil.toSql(cartonningCode) + "'");
				if( ciBean != null ) {
					sqlCount.append(" and bsu.cartonning_info_id=").append(ciBean.getId());
				} else {
					sqlCount.append(" and bsu.cartonning_info_id=").append(0);
				}
			}
			//上架单号
			if( upShelfStatus != 1 && upShelfCode != null && !upShelfCode.equals("") ) {
				sqlCount.append(" and co.code='").append(StringUtil.toSql(upShelfCode)).append("'");
			}
			//上架员
			if( upShelfStatus != 1 && upShelfUsername != null && !upShelfUsername.equals("") ) {
				sqlCount.append(" and co.complete_user_name='").append(StringUtil.toSql(upShelfUsername)).append("'");
			}
			//装箱员
			if( cartonningUsername != null && !cartonningUsername.equals("") ) {
				sqlCount.append(" and bsu.cartonning_info_name='").append(StringUtil.toSql(cartonningUsername)).append("'");
			}
			
			if( upShelfStatus == 2 ) {
				sqlCount.append(" and co.status in (").append("1,2,3,4,5,6").append(")");
				sqlCount.append(" and co.effect_status in (").append("0,1").append(")");
			}
			
			if( upShelfStatus == 3 ) {
				sqlCount.append(" and co.status in (").append("7,8").append(")");
			}

			PagingBean paging = null;
			int totalCount = checkStockinMissionService.getBuyStockinUpshelfCount2(sqlCount
					.toString());
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			paging.setPrefixUrl("javascript:loadPage(");
			String pageLine = PageUtil.fenyeAjax(paging, true, "&nbsp;&nbsp;", 20);
			String result = "{status:'success', pageLine:'" + pageLine + "', totalCount:'" + totalCount + "' }";
			response.getWriter().write(result);
			return;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(
						"method getUnqualifiedStorageDetailInfo exception", e);
			}
		} finally {
			statService.releaseAll();
		}
	}

	/**
	 * 采购入库统计
	 * @param request
	 * @param response
	 * @param inTime
	 * @param username
	 * @param stockCode
	 * @param productCode
	 * @param area
	 * @return
	 */
	@RequestMapping("/stockInStoreCount")
	@ResponseBody
	public String stockInStoreCount(HttpServletRequest request,HttpServletResponse response,
			@RequestParam("inTime") String inTime,
			@RequestParam("username") String username,
			@RequestParam("stockCode") String stockCode,//预计到货单
			@RequestParam("productCode") String productCode,//产品编号
			@RequestParam("area") String area){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		int isBTwoC = StringUtil.StringToId(request.getParameter("isBTwoC"));
		try{
			response.setContentType("text/html;charset=UTF-8");
			if(user==null){
				response.getWriter().write("{status:'needLogin',result:'当前没有登录，操作失败！'}");
				return null;
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(885)){
				response.getWriter().write("{status:'failure',result:'您没有采购入库统计权限！'}");
				return null;
			}
			
			wareService = new WareService(dbSlave);
			CheckStockinMissionService service = new CheckStockinMissionService(
					IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			int userId = -1;
			StringBuilder main = new StringBuilder();
			StringBuilder sub = new StringBuilder();
			main.append("select count(distinct bs.id),count(distinct bsp.product_id) 'sku个数',sum(bsp.stockin_count) '商品个数' from ");
			stockCode = StringUtil.convertNull(stockCode);
			if(stockCode!=null&&!"".equals(stockCode)){
				sub.append(" select bs.id from buy_stockin bs ,buy_stock bsk where bs.buy_stock_id = bsk.id and bsk.code = '"+stockCode+"'")
					.append(" and bs.status="+BuyStockinBean.STATUS6 + " and bs.type=" + isBTwoC);//审核已通过
			}else{
				sub.append(" select bs.id from buy_stockin bs where bs.status="+BuyStockinBean.STATUS6 + " and bs.type=" + isBTwoC);//审核已通过
			}
			sub.append(" and DATE_FORMAT(bs.confirm_datetime,'%Y-%m-%d') = '"+inTime+"'");
			username = StringUtil.convertNull(username);
			if(username!=null&&!"".equals(username)){
				userId = service.findUserByUsername(username);
				sub.append(userId==-1?"":" and bs.auditing_user_id = "+userId);
			}
			productCode = StringUtil.convertNull(productCode);
			int areaId = StringUtil.toInt(area);
			if(areaId!=-1){
				sub.append(" and bs.stock_area = "+areaId);
			}
			main.append("("+sub.toString()+") bs, buy_stockin_product bsp where bs.id = bsp.buy_stockin_id ");
			if(productCode!=null&&!"".equals(productCode)){
				main.append(" and bsp.product_code = '"+productCode+"'");
			}
			ResultSet rs = wareService.getDbOp().executeQuery(main.toString());
			main = new StringBuilder();
			main.append(inTime+"共审核");
			while(rs.next()){
				main.append(rs.getObject(1))
					.append("张入库单，商品总计：")
					.append(rs.getObject(2))
					.append("个sku，")
					.append(rs.getObject(3)==null?0:rs.getObject(3))
					.append("件商品");
			}
			response.getWriter().write("{status:'success',result:'"+main.toString()+"'}");
			return null;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(wareService!=null){
				wareService.releaseAll();
			}
		}
		return null;
	}
	/**
	 * 采购上架统计
	 * @param request
	 * @param response
	 * @param completeDatetime
	 * @param completeUsename
	 * @param productCode
	 * @return
	 */
	@RequestMapping("/stockShelfCount")
	public void stockShelfCount(HttpServletRequest request,HttpServletResponse response,
			@RequestParam String completeDatetime,
			@RequestParam String completeUsename,
			@RequestParam String productCodes,
			@RequestParam String area){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try{
			response.setContentType("text/html;charset=UTF-8");
			if(user==null){
				response.getWriter().write("{status:'needLogin',result:'当前没有登录，操作失败！'}");
				return;
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(885)){
				response.getWriter().write("{status:'failure',result:'您没有采购上架统计权限！'}");
				return;
			}
			StringBuilder main = new StringBuilder();
			productCodes = StringUtil.convertNull(productCodes);
			main.append("select count(distinct co.id) ,count(DISTINCT coc.product_id) ,sum(coc.stock_count)");
			if(productCodes!=null&&!"".equals(productCodes)){
				main.append(" from cargo_operation co,cargo_operation_cargo coc,cargo_info ci where co.id = coc.oper_id ")
				.append(" and ci.whole_code = coc.out_cargo_whole_code and ci.stock_type=1 and coc.product_id in (");
				String sql = "select distinct id from product p where p.code='"+productCodes+"'";
				ResultSet rs = wareService.getDbOp().executeQuery(sql);
				while(rs.next()){
					main.append("'"+rs.getObject(1).toString()+"',");
				}
				main.deleteCharAt(main.length()-1);
				main.append(")");
//				main.append(" from cargo_operation co,cargo_operation_cargo coc,cargo_info ci,product p where co.id = coc.oper_id and p.id = coc.product_id")
//					.append(" and ci.whole_code = coc.out_cargo_whole_code and ci.stock_type=1 and p.code = '"+productCodes+"'");
			}else{
				main.append(" from cargo_operation co,cargo_operation_cargo coc,cargo_info ci where co.id = coc.oper_id and ci.whole_code = coc.out_cargo_whole_code and ci.stock_type=1");
			}
			completeDatetime = StringUtil.convertNull(completeDatetime);
			if(completeDatetime!=null&&!"".equals(completeDatetime)){
				//用了函数就不能起到索引的作用了
//				main.append(" and DATE_FORMAT(co.complete_datetime,'%Y-%m-%d')='"+completeDatetime+"'");
				main.append(" and co.complete_datetime>='"+completeDatetime+" 00:00:00' ");
				main.append(" and co.complete_datetime<='"+completeDatetime+" 23:59:59' ");
			}
			completeUsename = StringUtil.convertNull(completeUsename);
			if(completeUsename!=null&&!"".equals(completeUsename)){
				main.append(" and co.complete_user_name = '"+completeUsename+"'");
			}
			int areaId = StringUtil.toInt(StringUtil.convertNull(area));
			if(areaId>=0){
				main.append(" and ci.area_id = "+areaId);
			}
			main.append(" and co.status in(7,8) and coc.use_status=1");//作业完成
			ResultSet rs = wareService.getDbOp().executeQuery(main.toString());
			main = new StringBuilder();
			main.append(completeDatetime+"共完成");
			while(rs.next()){
				main.append(rs.getObject(1))
				.append("张上架单，商品总计：")
				.append(rs.getObject(2))
				.append("个sku，")
				.append(rs.getObject(3)==null?0:rs.getObject(3))
				.append("件商品");
			}
			response.getWriter().write("{status:'success',result:'"+main.toString()+"'}");
			return;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(wareService!=null){
				wareService.releaseAll();
			}
		}
	}
	
	/**
	 * 验证传入的预计单号对应的预计单的状态是否是已完成
	 * @param request
	 * @param response
	 * @param buyStockCode
	 * @return
	 */
	@RequestMapping("check/checkBuyStock")
	@ResponseBody
	public Map<String,String> checkBuyStockStatus(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("buyStockCode") String buyStockCode
	) {
		
		Map<String,String > map = new HashMap<String, String>();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, dbOp);
		CheckStockinMissionService csmsService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			BuyStockBean bsBean = service.getBuyStock("code='" + StringUtil.toSql(buyStockCode) + "'");
			if( bsBean == null ) {
				map.put("status", "fail");
				map.put("tip", "未根据提供预计单号找到预计单！");
				return map;
			} else {
				if( bsBean.getStatus() == BuyStockBean.STATUS6 ) {
					map.put("status", "fail");
					map.put("tip", "对应的预计单采购已完成，不可以继续操作了！");
					return map;
				} else if (bsBean.getStatus() == BuyStockBean.STATUS0 ) {
					map.put("status", "fail");
					map.put("tip", "对应的预计单状态为未处理，不可以继续操作了！");
					return map;
				}else if (bsBean.getStatus() == BuyStockBean.STATUS1 ) {
					map.put("status", "fail");
					map.put("tip", "对应的预计单状态为处理中，不可以继续操作了！");
					return map;
				}else if (bsBean.getStatus() == BuyStockBean.STATUS8 ) {
					map.put("status", "fail");
					map.put("tip", "对应的预计单状态为已删除，不可以继续操作了！");
					return map;
				} else {
					map.put("status", "success");
					map.put("code", bsBean.getCode());
					map.put("id", new Integer(bsBean.getId()).toString());
					return map;
				}
			}
		} catch (Exception e ) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return map;
	}
	/**
	 * 验证传入的预计单号和产品编号，查找商品，以及对应的未初录的质检任务单信息
	 * @param request
	 * @param response
	 * @param buyStockCode
	 * @return
	 */
	@RequestMapping("check/checkProductCodeAndGetCheckStockinInfo")
	@ResponseBody
	public Map<String,String> checkProductCodeAndGetCheckStockinInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("buyStockCode") String buyStockCode,
			@RequestParam("productCode") String productCode
			) {
		Map<String, String> map = new HashMap<String, String>();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			map.put("status", "fail");
			map.put("tip", "当前没有登录，操作失败！");
			return map;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(689)) {
			map.put("status", "fail");
			map.put("tip", "您没有权限添加质检任务！");
			return map;
		}
		if (!group.isFlag(786)) {
			map.put("status", "fail");
			map.put("tip", "您没有初录权限！");
			return map;
		}
		if (buyStockCode == null || buyStockCode.equals("")) {
			map.put("status", "fail");
			map.put("tip", "预计到货单号不能为空！");
			return map;
		}
		if (productCode == null || productCode.equals("")) {
			map.put("status", "fail");
			map.put("tip", "产品编号不能为空！");
			return map;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, dbOp);
		CheckStockinMissionService csmsService = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			synchronized (checkStockinLock) {
				voProduct product = csmsService.getProductByCode(productCode);
				BuyStockBean bsBean = service.getBuyStock("code='"
						+ StringUtil.toSql(buyStockCode) + "'");
				if (product == null) {
					map.put("status", "fail");
					map.put("tip", productCode + "该商品不存在！");
					return map;
				}
				if (bsBean == null) {
					map.put("status", "fail");
					map.put("tip", "未根据提供预计单号找到预计单！");
					return map;
				} else {
					if(bsBean.getStatus()!=BuyStockBean.STATUS2
							&& bsBean.getStatus()!=BuyStockBean.STATUS3
							&& bsBean.getStatus()!=BuyStockBean.STATUS5){
						map.put("status", "fail");
						map.put("tip", "该预计单状态为"+bsBean.getStatusName()+"，生成任务失败！");
						return map;
					}
					boolean areaPermission = CargoDeptAreaService.hasCargoDeptArea(request, bsBean.getArea(), ProductStockBean.STOCKTYPE_CHECK);
					if( ! areaPermission ) {
						map.put("status", "fail");
						map.put("tip", "你没有预计单入库地区的操作权限，不可以操作这个任务！");
						return map;
					}
					//判断采购订单是否已经完成
					BuyOrderBean buyOrderBean = service.getBuyOrder("id="+bsBean.getBuyOrderId());
					if(buyOrderBean.getStatus()!=BuyOrderBean.STATUS3 
							&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS5
							&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS7){
						map.put("status", "fail");
						map.put("tip", "该预计单对应的采购订单状态为"+buyOrderBean.getStatusName()+"，生成任务失败！");
						return map;
					}
					
					//判断商品是否属于预计单
					int count = service.getBuyStockProductCount("product_id="+product.getId()+" and buy_stock_id="+bsBean.getId());
					if(count<=0){
						map.put("status", "fail");
						map.put("tip", "产品（编号）不属于该预计单！");
						return map;
					}
				}
				List<CheckStockinMissionBatchBean> checkMissionBatches = csmsService
						.getNotBeginCheckMisssions(bsBean.getCode(),
								product.getId());
				if (checkMissionBatches.size() == 0) {
					// 对于为零的 要添加 注意判断的条件
					// 创建质检入库单的流程
					Object result = csmsService.addCheckStockinMission2(request,
							buyStockCode, user, productCode);
					if ( result instanceof String ) {
						if (result != null ) {
							String result2 = (String) result;
							statService = new StatService(
									IBaseService.CONN_IN_SERVICE,
									dbOp);
							
								String propertyId = statService
										.getPwpIdByProductCode(new Integer(product.getId()).toString());
								ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
										IBaseService.CONN_IN_SERVICE,
										dbOp);
								ProductWarePropertyBean pwpBean = statService
										.getProductWareProperty("id = "
												+ StringUtil.parstInt(propertyId));
								if (pwpBean == null) {
									map.put("status", "fail");
									map.put("tip", "没有找到这个商品的商品物流属性，请添加！");
									return map;
								} else if (pwpBean.getIdentityInfo() == null || pwpBean.getIdentityInfo().equals("")) {
									map.put("status", "fail");
									map.put("tip", "对应商品的商品物流属性没有可辨识信息，请添加！");
									return map;
								} else if (pwpBean.getCartonningStandardCount() == 0 ) {
									map.put("status", "fail");
									map.put("tip", "对应商品的商品物流属性没有标准装箱量，请添加！");
									return map;
								} else {
									map.put("status", "fail");
									map.put("tip", result2);
									return map;
								}
						}
					}  else if (result instanceof CheckStockinMissionBatchBean )  {
						//再查出这个质检入库任务
						CheckStockinMissionBatchBean csmbBean = (CheckStockinMissionBatchBean) result;
						map.put("status", "created");
						map.put("check_stockin_mission_id", new Integer(csmbBean.getCheckStockinMission().getId()).toString());
						map.put("check_stockin_mission_batch_id", new Integer(csmbBean.getId()).toString());
						map.put("check_stockin_code",csmbBean.getCheckStockinMission().getCode());
						map.put("product_code", productCode);
						map.put("product_id", new Integer( product.getId() ).toString());
						return map;
					}
					
				} else if (checkMissionBatches.size() == 1) {
					// 对于只有一个的可以返回去，到就当做这次初录的对应的质检任务
					CheckStockinMissionBatchBean csmsBean = checkMissionBatches
							.get(0);
					map.put("status", "success");
					map.put("check_stockin_mission_id", new Integer(csmsBean
							.getCheckStockinMission().getId()).toString());
					map.put("check_stockin_mission_batch_id", new Integer(csmsBean.getId()).toString());
					map.put("check_stockin_code", csmsBean
							.getCheckStockinMission().getCode());
					map.put("product_code", productCode);
					map.put("product_id", new Integer( product.getId() ).toString());
					return map;
				} else {
					// 对于初录量比较多的应该属于错误 要报错
					map.put("status", "fail");
					map.put("tip", "当前存在多个未初录的任务，有错误，请查证！");
					return map;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", "fail");
			map.put("tip", "程序出错！");
		} finally {
			service.releaseAll();
		}
		// 首先是查询质检入库任务的信息 要求没有初录过的。。
		// 这里还有一个问题，如果已经有未初录的。。 首先将判断加上限制，
		// 没有的情况

		// 没有为初录的话 要创建新的 但是在创建新的的时候要考虑，不能有未完成的。 因为 如果有未复录的 可以重新初录，可能会出问题。
		// 有多个未初录的 报错
		return map;
	}
	
	
	/**
	 * 验证传入的预计单号和产品编号，查找商品，以及对应的已初录未复录的质检任务单信息
	 * @param request
	 * @param response
	 * @param buyStockCode
	 * @return
	 */
	@RequestMapping("check/checkProductCodeAndGetFirstCheckStockinInfo")
	@ResponseBody
	public Map<String,String> checkProductCodeAndGetFirstCheckStockinInfo(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam("buyStockCode") String buyStockCode,
			@RequestParam("productCode") String productCode
			) {
		Map<String, String> map = new HashMap<String, String>();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			map.put("status", "fail");
			map.put("tip", "当前没有登录，操作失败！");
			return map;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(787)) {
			map.put("status", "fail");
			map.put("tip", "您没有复录权限！");
			return map;
		}
		if (buyStockCode == null || buyStockCode.equals("")) {
			map.put("status", "fail");
			map.put("tip", "预计到货单号不能为空！");
			return map;
		}
		if (productCode == null || productCode.equals("")) {
			map.put("status", "fail");
			map.put("tip", "产品编号不能为空！");
			return map;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, dbOp);
		CheckStockinMissionService csmsService = new CheckStockinMissionService(
				IBaseService.CONN_IN_SERVICE, dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			synchronized (checkStockinLock) {
				voProduct product = csmsService.getProductByCode(productCode);
				BuyStockBean bsBean = service.getBuyStock("code='"
						+ StringUtil.toSql(buyStockCode) + "'");
				if (product == null) {
					map.put("status", "fail");
					map.put("tip", productCode + "该商品不存在！");
					return map;
				}
				if (bsBean == null) {
					map.put("status", "fail");
					map.put("tip", "未根据提供预计单号找到预计单！");
					return map;
				} else {
					if(bsBean.getStatus()!=BuyStockBean.STATUS2
							&& bsBean.getStatus()!=BuyStockBean.STATUS3
							&& bsBean.getStatus()!=BuyStockBean.STATUS5){
						map.put("status", "fail");
						map.put("tip", "该预计单状态为"+bsBean.getStatusName()+"，不可以继续操作！");
						return map;
					}
					boolean areaPermission = CargoDeptAreaService.hasCargoDeptArea(request, bsBean.getArea(), ProductStockBean.STOCKTYPE_CHECK);
					if( ! areaPermission ) {
						map.put("status", "fail");
						map.put("tip", "你没有预计单入库地区的操作权限，不可以操作这个任务！");
						return map;
					}
					
					//判断采购订单是否已经完成
					BuyOrderBean buyOrderBean = service.getBuyOrder("id="+bsBean.getBuyOrderId());
					if(buyOrderBean.getStatus()!=BuyOrderBean.STATUS3 
							&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS5
							&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS7){
						map.put("status", "fail");
						map.put("tip", "该预计单对应的采购订单状态为"+buyOrderBean.getStatusName()+"，不可以继续操作！");
						return map;
					}
					
					//判断商品是否属于预计单
					int count = service.getBuyStockProductCount("product_id="+product.getId()+" and buy_stock_id="+bsBean.getId());
					if(count<=0){
						map.put("status", "fail");
						map.put("tip", "产品（编号）不属于该预计单！");
						return map;
					}
				}
				List<CheckStockinMissionBatchBean> checkMissionBatches = csmsService
						.getAlreadyBeginCheckMisssions(bsBean.getCode(),
								product.getId());
				if (checkMissionBatches.size() == 0) {
					//如果未找到任何已经初录过的质检任务，报错提示
					map.put("status", "fail");
					map.put("tip", "当前没有对应的已初录未复录的质检任务！");
					return map;
				} else if (checkMissionBatches.size() == 1) {
					// 对于只有一个的可以返回去，到就当做这次复录的对应的质检任务
					CheckStockinMissionBatchBean csmsBean = checkMissionBatches
							.get(0);
					String propertyId = statService
					.getPwpIdByProductCode(new Integer(product.getId()).toString());
					ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
					IBaseService.CONN_IN_SERVICE,
					dbOp);
					ProductWarePropertyBean pwpBean = statService
					.getProductWareProperty("id = "
							+ StringUtil.parstInt(propertyId));
					if( pwpBean == null ) {
						map.put("status", "fail");
						map.put("tip", "当前商品没有商品物流属性！");
						return map;
					}
					map.put("status", "success");
					map.put("check_stockin_mission_id", new Integer(csmsBean
							.getCheckStockinMission().getId()).toString());
					map.put("check_stockin_mission_batch_id", new Integer(csmsBean.getId()).toString());
					map.put("check_stockin_code", csmsBean
							.getCheckStockinMission().getCode());
					map.put("product_code", productCode);
					map.put("product_id", new Integer( product.getId() ).toString());
					map.put("binning", new Integer(pwpBean.getCartonningStandardCount()).toString());
					return map;
				} else {
					// 对于未复录的数量大于一个的 提示错误，
					map.put("status", "fail");
					map.put("tip", "当前存在多个未复录的任务，有错误，请查证！");
					return map;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			map.put("status", "fail");
			map.put("tip", "程序出错！");
		} finally {
			service.releaseAll();
		}
		// 首先是查询质检入库任务的信息 要求没有初录过的。。
		// 这里还有一个问题，如果已经有未初录的。。 首先将判断加上限制，
		// 没有的情况

		// 没有为初录的话 要创建新的 但是在创建新的的时候要考虑，不能有未完成的。 因为 如果有未复录的 可以重新初录，可能会出问题。
		// 有多个未初录的 报错
		return map;
	}
	
	
	/**
	 * 质检初录新的录入接口处理
	 * @param request
	 * @param response
	 * @param checkStockinMissionId
	 * @param productId
	 * @return
	 */
	@RequestMapping("check/newFirstCheckAutomatic")
	@ResponseBody
	public Map<String, String> firstCheckAutomatic(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("checkStockinMissionId") String checkStockinMissionId,
			@RequestParam("checkStockinMissionBatchId") String checkStockinMissionBatchId,
			@RequestParam("productId") String productId,
			@RequestParam("firstCheckCount") String checkCount,
			@RequestParam("buyStockCode") String buyStockCode
	) {
		Map<String, String> map = new HashMap<String,String>();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			map.put("status", "fail");
			map.put("tip", "当前没有登录，操作失败！");
			return map;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(786)) {
			map.put("status", "fail");
			map.put("tip", "您没有初录权限！");
			return map;
		}
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			wareService.getDbOp().startTransaction();
			BuyStockBean bs = statService
				.getBuyStockBeanByCode(buyStockCode);
				if(bs.getStatus()==BuyStockBean.STATUS6){
					map.put("status", "fail");
					map.put("tip", "预计单的状态为采购已完成，不可以再进行初录了！");
					return map;
				}
			// int batchid =
			// statService.getCheckStockinMissionBatchInfo(csmCode);
			CheckStockinMissionBatchBean ss = statService
					.getCheckStockinMissionBatch("id=" + checkStockinMissionBatchId);
			// 根据batchid更新数据库
			CheckStockinMissionBean csmBean = statService.getCheckStockinMission("id=" + checkStockinMissionId);
			if( ss == null || csmBean == null  )  {
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "没有找到对应的质检任务单！");
				return map;
			}
			if( csmBean.getStatus() == CheckStockinMissionBean.STATUS3) {
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "质检任务状态为已完成，不可以再进行初录了！");
				return map;
			} else if (csmBean.getStatus()  == CheckStockinMissionBean.STATUS4 ) {
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "质检任务状态为已删除，不可以再进行初录了！");
				return map;
			}
			
			BuyStockProductBean bsp = service.getBuyStockProduct("buy_stock_id = " + bs.getId() + " and product_id=" + productId);
			if (bsp == null) {
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "没有找到商品采购信息！");
				return map;
				
			}
			int count = StringUtil.toInt(checkCount);
			if (count == -1) {
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "初录数量有错！");
				return map;
			}
			if(!psService.checkBuyStockinProductCount(service.getDbOp(), -1, count, bsp.getBuyCount(), bsp.getProductId(), bs.getId())) {
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "采购入库单商品过多！");
				return map;
			}
			
			String set = "";
			set = " first_check_count=" + checkCount
					+ " , first_check_status=1, status = " + CheckStockinMissionBean.STATUS2;
			String condition = " id=" + checkStockinMissionBatchId;
			if (statService.updateCheckStockinMissionBatch(set, condition)) {
				wareService.getDbOp().commitTransaction();
				map.put("status", "success");
				map.put("tip", "质检初录成功！");
				return map;
			} else {
				// System.out.println("update fire-------------->");
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "质检初录结果录入失败！");
				return map;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
		} finally {
			wareService.getDbOp().release();
		}
		map.put("status", "fail");
		map.put("tip", "发生了异常！");
		return map;
	}
	
	
	/**
	 * 质检复录新的录入接口处理
	 * 
	 * @param request
	 * @param response
	 * @param checkStockinMissionId
	 * @param productId
	 * @return
	 */
	@RequestMapping("check/newSecondCheckAutomatic")
	@ResponseBody
	public Map<String, String> newSecondCheckAutomatic(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("checkStockinMissionId") String missionId,
			@RequestParam("checkStockinMissionBatchId") String batchId,
			@RequestParam("productId") String productId,
			@RequestParam("productCode") String productCode,
			@RequestParam("secondCheckCount") int productCount,
			@RequestParam("buyStockCode") String buyStockinCode
	) {
		Map<String,String> map = new HashMap<String,String>();
		List<BuyStockinProductBean> beanList = new ArrayList<BuyStockinProductBean>();
		//需要注意的复录时的 一些问题  比如提示结束 采购订单 和预计单的提示， 等等等等
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			map.put("status", "fail");
			map.put("tip", "当前没有登录，操作失败！");
			return map;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(787)) {
			map.put("status", "fail");
			map.put("tip", "您没有复录权限！");
			return map;
		}
		
		//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点1：开始");///////////////////////////////////////////////////////////////////////////////
		
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
//		FinanceReportFormsService fService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ISupplierService supplierService = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		CartonningInfoService cService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		InitVerificationService initVerificationService = new InitVerificationServiceImpl();
		
		//财务基础数据接口
		FinanceBaseDataService fService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(
						FinanceStockCardBean.CARDTYPE_BUYSTOCKIN, wareService.getDbOp().getConn());
		List<BaseProductInfo> baseProductList = null;
		BaseProductInfo baseProduct = null;
		
		int binnCount = 0;// 可以装多少箱

		BuyStockinBean stockin = null;
		String operationTime = "";
		CheckStockinMissionBatchBean bean1 = null;
		List<String> siList = new ArrayList<String>();
		String codelist = "";
		try {
			synchronized (endCheckLock) {

				BuyStockBean bs = statService
						.getBuyStockBeanByCode(buyStockinCode);
				if (bs.getStatus() == BuyStockBean.STATUS6) {
					map.put("status", "fail");
					map.put("tip", "预计单的状态为采购已完成，不可以再进行初录了！");
					return map;
				}
				CheckStockinMissionBatchBean ss = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				// 根据batchid更新数据库
				CheckStockinMissionBean csmBean = statService
						.getCheckStockinMission("id=" + missionId);
				if (ss == null || csmBean == null) {
					map.put("status", "fail");
					map.put("tip", "没有找到对应的质检任务单！");
					return map;
				}
				if (csmBean.getStatus() == CheckStockinMissionBean.STATUS3) {
					map.put("status", "fail");
					map.put("tip", "质检任务状态为已完成，不可以再进行初录了！");
					return map;
				} else if (csmBean.getStatus() == CheckStockinMissionBean.STATUS4) {
					map.put("status", "fail");
					map.put("tip", "质检任务状态为已删除，不可以再进行初录了！");
					return map;
				}
				// 解决重复提交
				CheckStockinMissionBatchBean batchBean = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				if (batchBean.getFirstCheckStatus() == 0) {
					map.put("status", "fail");
					map.put("tip", "这个质检任务 还没有初录完成！");
					return map;
				}
				int firstCheckCount = batchBean.getFirstCheckCount();
				if( firstCheckCount != productCount ) {
					map.put("status", "fail");
					map.put("tip", "初录复录结果不一致，操作取消！");
					return map;
				}
				
				voProduct product = wareService.getProduct(Integer
						.parseInt(productId));
				ProductWarePropertyBean pwpBean = statService
						.getProductWareProperty("product_id=" + productId);
				if (pwpBean == null) {
					map.put("status", "fail");
					map.put("tip", "当前商品没有商品物流属性！");
					return map;
				}

				int binning = pwpBean.getCartonningStandardCount();
				if (binning == 0) {
					map.put("status", "fail");
					map.put("tip", "当前商品的标准装箱量为0，请修改！");
					return map;
				}
				
				BuyStockProductBean bsp = service.getBuyStockProduct("buy_stock_id = " + bs.getId() + " and product_id=" + productId);
				if (bsp == null) {
					map.put("status", "fail");
					map.put("tip", "没有找到商品采购信息！");
					return map;
					
				}
				if (productCount == -1) {
					map.put("status", "fail");
					map.put("tip", "复录数量有错！");
					return map;
				}
				if(!psService.checkBuyStockinProductCount(service.getDbOp(), -1, productCount, bsp.getBuyCount(), bsp.getProductId(), bs.getId())) {
					map.put("status", "fail");
					map.put("tip", "采购入库单商品过多！");
					return map;
				}
				int count = productCount % binning;
				int temp = 0;
				//对于满了一个装箱量的，装一箱，最后剩下的不足一箱的 单独装一箱。
				binnCount = productCount / binning;
				wareService.getDbOp().startTransaction();
				
				//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点2：整箱开始");////////////////////////////////////////////////////////////////////////
				
				for (int i = 0; i < binnCount; i++) {// 循环生成采购入库单及装箱装单
					operationTime = DateUtil.getNow();
					stockin = createBuyStockinBeanInfo(service, bs,
							operationTime, user);
					if (!service.addBuyStockin(stockin)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库单失败!");
						return map;
					}
					if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库单失败!");
						return map;
					}
					List stockinList = service.getBuyStockinList(
							"buy_stock_id=" + bs.getId() + " and buy_order_id="
									+ bs.getBuyOrderId()
									+ " and create_datetime='" + operationTime
									+ "'", -1, -1, "id");
					if (stockinList != null && stockinList.size() > 0)
						stockin = (BuyStockinBean) stockinList.get(stockinList
								.size() - 1);

					// log记录
					BuyAdminHistoryBean log = createBuyAdminHistoryBeanInfo(
							user, stockin, bs, 1, product);

					if (!service.addBuyAdminHistory(log)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "在添加生成入库单日志时，数据库操作出错！");
						return map;
					}

					// 添加对应的商品
					int buyStockProductId = product.getId();
					bsp = service
							.getBuyStockProduct("buy_stock_id = " + bs.getId()
									+ " and product_id = " + buyStockProductId);
					if (bsp == null) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "在对应的采购预计单中 没有找到对应的商品！");
						return map;
					}
					BuyStockinProductBean bsip = createBuyStockinProductBeanInfo(
							wareService, psService, service, stockin,
							operationTime, bsp, product, binning);

//					List<Object> bsipList = new ArrayList<Object>();
//					bsipList.add(bsip);
//					beanList.add(bsip);

					// log记录
					log = createBuyAdminHistoryBeanInfo(user, stockin, bs, 2,
							product);

					if (!service.addBuyAdminHistory(log)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加日志时，数据库操作失败！");
						return map;
					}

					// 以下为审核流程及财务流程

					// 获取税点
//					BuyOrderBean buyOrder = service
//							.getBuyOrder("id=(select buy_order_id from buy_stock where id="
//									+ bs.getId() + ")");
					BuyOrderBean buyOrder = service.getBuyOrder("id=" + bs.getBuyOrderId());
					double taxPoint = 0;
					if (buyOrder != null) {
						taxPoint = buyOrder.getTaxPoint();
					}
					// 审核采购入库单
					stockin.setStatus(BuyStockinBean.STATUS4);
					stockin.setAuditingUserId(user.getId());
					// 更新状态为已审核
					if (!service.updateBuyStockin("status="+BuyStockinBean.STATUS4,
							"id=" + stockin.getId())) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "审核入库单失败，请检查后再试!");
						return map;
					}
					// log记录
//					double totalMoney = 0;
					// 财务入库明细表添加数据
					// 计算此入库单，入库的总金额
//					if (bsipList != null && bsipList.size() > 0) {
//						BuyStockinProductBean bsipb = null;
//						FinanceBuyProductBean fbpb = null;
//						ResultSet rs = null;
//						String sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
//								+ "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
//						for (int j = 0; j < bsipList.size(); j++) {
//							bsipb = (BuyStockinProductBean) bsipList.get(j);
//
//							fbpb = createFinanceBuyProductBeanInfo(bsipb,
//									stockin, buyOrder, operationTime);
//							rs = fService.getDbOp().executeQuery(
//									sql + bsipb.getProductId());
//							while (rs.next()) {
//
//								fbpb.setProductLineId(rs.getInt(1));// 添加产品线
//							}
//							if (!fService.addFinanceBuyProductBean(fbpb)) {
//								wareService.getDbOp().rollbackTransaction();
//								map.put("status", "fail");
//								map.put("tip", "添加财务数据时，数据库操作失败！");
//								return map;
//							}
//							totalMoney = Arith.add(
//									totalMoney,
//									Arith.mul(
//											bsipb.getStockInCount(),
//											Arith.mul(bsipb.getPrice3(),
//													Arith.add(1, taxPoint))));
//							totalMoney = Arith.round(totalMoney, 2);
//						}
//					}
//					// 入库单后，为财务表添加数据
//					FinanceBuyPayBean fbp = createFinanceBuyPayBeanInfo(
//							buyOrder, stockin, totalMoney, operationTime);
//					if (!fService.addFinanceBuyPayBean(fbp)) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "添加财务数据，数据库操作失败！");
//						return map;
//					}// 财务表添加
						// ---------------------------------
					BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(user, stockin, bs, 3, product);
					if (!service.addBuyAdminHistory(log1)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加日志时，数据库操作失败！");
						return map;
					}
					log1 = createBuyAdminHistoryBeanInfo(user, stockin, bs, 4, product);
					if (!service.addBuyAdminHistory(log1)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加日志时，数据库操作失败！");
						return map;
					}
					
					String set = null;
					ProductStockBean ps = null;
//					bsip.setPrice3(Double.valueOf(
//							String.valueOf(Arith.mul(bsip.getPrice3(),
//									Arith.add(1, taxPoint)))).floatValue());

					if (bsip.getStockInCount() <= 0) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "采购入库量不能为0，操作失败！");
						return map;
					}

					if (product.getIsPackage() == 1) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "入库单中包含有套装产品，不能入库！");
						return map;
					}
					product.setPsList(psService.getProductStockList(
							"product_id=" + product.getId(), -1, -1, null));
					ps = psService.getProductStock("id=" + bsip.getStockInId());
					bsip.setPrice3(Double.valueOf(
							String.valueOf(Arith.mul(bsip.getPrice3(),
									Arith.add(1, taxPoint)))).floatValue());
					float price5 = 0f;
					int totalCount = product.getStockAll()
							+ product.getLockCountAll();
					price5 = ((float) Math.round((product.getPrice5()
							* (totalCount) + (bsip.getPrice3() * bsip
							.getStockInCount()))
							/ (totalCount + bsip.getStockInCount()) * 1000)) / 1000;

					bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
					bsip.setRemark("操作前库存" + ps.getStock() + ",操作后库存"
							+ (ps.getStock() + bsip.getStockInCount()));
					bsip.setConfirmDatetime(operationTime);
//					if (!service.getDbOp().executeUpdate(
//							"update product set price5=" + price5
//									+ " where id = " + product.getId())) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "更新商品状态失败!");
//						return map;
//					}
					product.setPrice5(price5);// 在下次循环中作为库存均价使用

					// 财务数据填充：finance_product表--------liuruilan---------
//					FinanceProductBean fProduct = fService
//							.getFinanceProductBean("product_id = "
//									+ product.getId());
//					if (fProduct == null) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "查询异常，请与管理员联系！");
//						return map;
//					}
//					float priceSum = Arith.mul(price5,
//							totalCount + bsip.getStockInCount());
//					float priceHasticket = 0;
//					float priceNoticket = 0;
//					float priceSumHasticket = fProduct.getPriceSumHasticket();
//					float priceSumNoticket = fProduct.getPriceSumNoticket();
//					int ticket = FinanceSellProductBean.queryTicket(
//							service.getDbOp(), bs.getCode()); // 是否含票
//					if (ticket == -1) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "查询异常，请与管理员联系！");
//						return map;
//					}
//					int _count = FinanceProductBean.queryCountIfTicket(
//							service.getDbOp(), bsip.getProductId(), ticket);
//					set = "price =" + price5 + ", price_sum =" + priceSum;
//					if (ticket == 0) { // 0-有票
//						_price3 = (float) Arith.div(
//								Arith.mul(_price3, Arith.add(1, taxPoint)),
//								1.17);
//
//						priceHasticket = Arith.round(Arith.div(Arith.add(
//								fProduct.getPriceSumHasticket(),
//								Arith.mul(_price3, bsip.getStockInCount())),
//								Arith.add(_count, bsip.getStockInCount())), 2);
//						priceSumHasticket = Arith.mul(priceHasticket,
//								bsip.getStockInCount() + _count);
//						set += ", price_hasticket =" + priceHasticket
//								+ ", price_sum_hasticket =" + priceSumHasticket;
//					}
//					if (ticket == 1) { // 1-无票
//						_price3 = (float) Arith.mul(_price3,
//								Arith.add(1, taxPoint));
//						priceNoticket = Arith.round(Arith.div(
//								Arith.add(
//										fProduct.getPriceSumNoticket(),
//										Arith.mul(_price3,
//												bsip.getStockInCount())),
//								Arith.add(_count, bsip.getStockInCount())), 2);
//						priceSumNoticket = Arith.mul(priceNoticket,
//								bsip.getStockInCount() + _count);
//						set += ", price_noticket =" + priceNoticket
//								+ ", price_sum_noticket =" + priceSumNoticket;
//					}
//					if (!fService.updateFinanceProductBean(set, "product_id = "
//							+ product.getId())) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "修改操作数据时，数据库操作失败！");
//						return map;
//					}
					// --------------liuruilan----------------

					// 更新货位库存2011-04-19
					CargoInfoAreaBean inCargoArea = cargoService
							.getCargoInfoArea("old_id = " + ps.getArea());
					CargoProductStockBean cps = null;
					CargoInfoBean cargo = null;
					List cocList = cargoService
							.getCargoAndProductStockList(
									"ci.stock_type = " + ps.getType()
											+ " and ci.area_id = "
											+ inCargoArea.getId()
											+ " and ci.store_type = "
											+ CargoInfoBean.STORE_TYPE2
											+ " and cps.product_id = "
											+ bsip.getProductId(), -1, -1,
									"ci.id desc");

					// 更改待验库对应货位库存
					if (cocList == null || cocList.size() == 0) {// 产品首次入库，无暂存区绑定货位库存信息
						cargo = cargoService.getCargoInfo("stock_type = "
								+ ps.getType() + " and area_id = "
								+ inCargoArea.getId() + " and store_type = "
								+ CargoInfoBean.STORE_TYPE2);
						if (cargo == null) {
							wareService.getDbOp().rollbackTransaction();
							map.put("status", "fail");
							map.put("tip", "目的待验库缓存区货位未设置，请先添加后再完成入库！");
							return map;
						}
						cps = new CargoProductStockBean();
						cps.setCargoId(cargo.getId());
						cps.setProductId(bsip.getProductId());
						// 注意这里不加货位库存了所以 stockCount 改为0直接加入到锁定中
						cps.setStockCount(0);
						cargoService.addCargoProductStock(cps);
						cps.setId(cargoService.getDbOp().getLastInsertId());
					} else {
						cps = (CargoProductStockBean) cocList.get(0);
					}
					// 更新库存
					cargoService.updateCargoProductStock(
							"stock_count=stock_count+" + binning,
							"id=" + cps.getId());
					psService.updateProductStock("stock=stock+" + binning,
							"id=" + ps.getId());
					
					
					// 更新订单已入库量和已入库总金额
					String area = "";
					if (stockin.getStockArea() == ProductStockBean.AREA_BJ) {
						area = "bj";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_GF) {
						area = "gd";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_ZC) {// 增城用北京的字段
						area = "bj";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_WX) {
						area = "gd";
					}else if(stockin.getStockArea() == ProductStockBean.AREA_JD){//京东记到广东
						area = "gd";
					}else if(stockin.getStockArea()==ProductStockBean.AREA_XA){//西安用北京的字段
						area = "bj";
					} else {
						area = "gd";
					}
					set = "stockin_count_" + area + "=(stockin_count_" + area
							+ " + " + bsip.getStockInCount() + ")";
					String condition2 = "buy_order_id=" + bs.getBuyOrderId()
							+ " and product_id=" + product.getId();
					if (!service.updateBuyOrderProduct(set, condition2)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "修改采购订单数据时，数据库操作失败！");
						return map;
					}

					// 添加批次记录
					// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
//					StockBatchBean batch = null;
//					batch = service.getStockBatch("code='" + bs.getCode()
//							+ "' and product_id = " + bsip.getProductId()
//							+ " and stock_type = "
//							+ ProductStockBean.STOCKTYPE_CHECK
//							+ " and stock_area = " + stockin.getStockArea());
//					if (batch == null) {
//						batch = createStockBatchBeanInfo(bs, stockin, bsip, ps,
//								ticket, operationTime);
//						service.addStockBatch(batch);
//					} else {
//						if (!service.updateStockBatch(
//								"batch_count = "
//										+ (batch.getBatchCount() + bsip
//												.getStockInCount()),
//								"code='" + bs.getCode() + "' and product_id = "
//										+ bsip.getProductId()
//										+ " and stock_type = "
//										+ ProductStockBean.STOCKTYPE_CHECK
//										+ " and stock_area = "
//										+ stockin.getStockArea())) {
//							wareService.getDbOp().rollbackTransaction();
//							map.put("status", "fail");
//							map.put("tip", "修改批次 信息时， 数据库操作失败！");
//							return map;
//						}
//					}
//
//					// 添加批次操作记录
//					StockBatchLogBean batchLog = createStockBatchLogBeanInfo(
//							batch, user, bsip, operationTime);
//					if (!service.addStockBatchLog(batchLog)) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "添加批次信息时，数据库操作失败！");
//						return map;
//					}

					// 判断并插入供货商关联信息
					voProductSupplier productSupplier = supplierService
							.getProductSupplierInfo("product_id = "
									+ bsip.getProductId()
									+ " and supplier_id = "
									+ bsip.getProductProxyId());
					if (productSupplier == null) {
						SupplierStandardInfoBean supplierStandardInfo = supplierService
								.getSupplierStandardInfo("id = "
										+ bsip.getProductProxyId());

						productSupplier = new voProductSupplier();
						productSupplier.setProduct_id(bsip.getProductId());
						productSupplier
								.setSupplier_id(bsip.getProductProxyId());
						if (supplierStandardInfo != null) {
							productSupplier
									.setSupplier_name(supplierStandardInfo
											.getName());
						}
						if (!supplierService
								.addProductSupplierInfo(productSupplier)) {
							wareService.getDbOp().rollbackTransaction();
							map.put("status", "fail");
							map.put("tip", "添加供货商信息时，数据库操作失败！");
							return map;
						}
					}

					if (!service.addBuyStockinProduct(bsip)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加添加采购入库单商品时 数据库操作失败！");
						return map;
					}

					// 审核通过，就加 进销存卡片
					product.setPsList(psService.getProductStockList(
							"product_id=" + bsip.getProductId(), -1, -1, null));
					cps = (CargoProductStockBean) cargoService
							.getCargoAndProductStockList(
									"cps.id = " + cps.getId(), 0, 1,
									"cps.id asc").get(0);

					// 计算入库金额
					double totalPrice = bsip.getStockInCount()
							* bsip.getPrice3();

					// 入库卡片
					StockCardBean sc = createStockCardBeanInfo(product,
							stockin, bsip, totalPrice, price5, operationTime);
					if (!psService.addStockCard(sc)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库卡片时， 数据库操作失败！");
						return map;
					}

					// 财务进销存卡片---liuruilan-----
//					FinanceStockCardBean fsc = createFinanceStockCardBeanInfo(
//							service, ticket, sc, price5, bs, bsip, totalPrice,
//							priceSumHasticket, priceSumNoticket,
//							priceHasticket, priceNoticket, _count,
//							operationTime);
//					if (!fService.addFinanceStockCardBean(fsc)) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "添加财务卡片时， 数据库操作失败！");
//						return map;
//					}

					// 货位入库卡片
					CargoStockCardBean csc = createCargoStockCardBeanInfo(
							stockin, bsip, product, price5, totalPrice, sc,
							cps, operationTime);
					if (!cargoService.addCargoStockCard(csc)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加货位入库卡片时，数据库操作失败！");
						return map;
					}

					// 拼装 bean 为打印时提供全部信息
					bsip.setTotalStockBeforeStockin(sc.getAllStock()
							- bsip.getStockInCount());
					if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ product.getParentId1()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ product.getParentId1()).getName();
						product.setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ product.getParentId2()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ product.getParentId2()).getName();
						product.setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ product.getParentId3()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ product.getParentId3()).getName();
						product.setProductLineName(productLineName);
					} else {
						product.setProductLineName("");
					}

					// 生成装箱单
					// String code = createZXCode();
					// 生成装箱单
					String code = cService.getZXCodeForToday();
					CartonningInfoBean bean = new CartonningInfoBean();
					bean.setCode(code);
					bean.setCreateTime(operationTime);
					bean.setStatus(1);
					bean.setName(user.getUsername());
					bean.setCargoId(cps.getCargoId());
					bean.setBuyStockInId(stockin.getId());
					CartonningProductInfoBean productBean = new CartonningProductInfoBean();
					productBean.setProductCount(binning);
					productBean.setProductCode(product.getCode());
					productBean.setProductName(product.getOriname());
					productBean.setProductId(product.getId());
					bean.setProductBean(productBean);
					if (!cService.addCartonningInfo(bean)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建装箱单失败!");
						return map;
					}
					if( !cService.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建装箱单失败!");
						return map;
					}
					code = bean.getCode();
					bean = cService.getCartonningInfo("code='" + bean.getCode()
							+ "'");
					// 添加质检上架关联的地方
					BuyStockinUpshelfBean bsuBean = new BuyStockinUpshelfBean();
					bsuBean.setBuyStockinId(stockin.getId());
					bsuBean.setBuyStockinDatetime(stockin.getConfirmDatetime());
					bsuBean.setProductId(product.getId());
					bsuBean.setProductCode(product.getCode());
					bsuBean.setCartonningInfoId(bean.getId());
					bsuBean.setCartonningInfoName(bean.getName());
					bsuBean.setCargoOperationId(0);
					bsuBean.setWareArea(stockin.getStockArea());
					if (!checkStockinMissionService
							.addBuyStockinUpshelf(bsuBean)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建入库单和装箱单关联失败!");
						return map;
					}
					productBean.setCartonningId(bean.getId());
					if( !cService.addCartonningProductInfo(productBean) ) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建装箱单失败!");
						return map;
					}

					// 采购入库明细
					CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
							stockin, product, missionId, bsip, bean,
							operationTime);

					if (!statService.addCheckStockinMissionDetail(csmdBean)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库明细失败！");
						return map;
					}
					siList.add(stockin.getCode() + "-" + code);
					
					//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点2.1：整箱财务开始 i="+i);////////////////////////////////////////////////////////////////////////
					
					//财务基础数据
					baseProductList = new ArrayList<BaseProductInfo>();
					baseProduct = new BaseProductInfo();
					baseProduct.setId(bsip.getProductId());
					baseProduct.setInCount(bsip.getStockInCount());
					baseProduct.setInPrice(bsip.getPrice3());
					baseProduct.setProductStockId(bsip.getStockInId());
					baseProductList.add(baseProduct);
					fService.acquireFinanceBaseData(baseProductList, stockin.getCode(), 
							user.getId(), stockin.getStockType(), stockin.getStockArea());
					
					//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点2.2：整箱财务结束 i="+i);////////////////////////////////////////////////////////////////////////
					
				}
				
				//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点3：整箱结束，单箱开始");////////////////////////////////////////////////////////////////////////
				
				if (count > 0) {// 不足标准装箱量
					operationTime = DateUtil.getNow();
					stockin = createBuyStockinBeanInfo(service, bs,
							operationTime, user);
					// 添加入库单
					if (!service.addBuyStockin(stockin)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库单失败!");
						return map;
					}
					if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库单失败!");
						return map;
					}
					List stockinList = service.getBuyStockinList(
							"buy_stock_id=" + bs.getId() + " and buy_order_id="
									+ bs.getBuyOrderId()
									+ " and create_datetime='" + operationTime
									+ "'", -1, -1, "id");
					if (stockinList != null && stockinList.size() > 0)
						stockin = (BuyStockinBean) stockinList.get(stockinList
								.size() - 1);

					// log记录
					BuyAdminHistoryBean log = createBuyAdminHistoryBeanInfo(
							user, stockin, bs, 1, product);

					if (!service.addBuyAdminHistory(log)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "在添加生成入库单日志时，数据库操作出错！");
						return map;
					}

					// 添加对应的商品
					int buyStockProductId = product.getId();
					bsp = service
							.getBuyStockProduct("buy_stock_id = " + bs.getId()
									+ " and product_id = " + buyStockProductId);
					if (bsp == null) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "在对应的采购预计单中 没有找到对应的商品！");
						return map;
					}
					BuyStockinProductBean bsip = createBuyStockinProductBeanInfo(
							wareService, psService, service, stockin,
							operationTime, bsp, product, count);// 这里传的参数是余量

					List<Object> bsipList = new ArrayList<Object>();
					bsipList.add(bsip);
					beanList.add(bsip);

					// log记录
					log = createBuyAdminHistoryBeanInfo(user, stockin, bs, 2,
							product);

					if (!service.addBuyAdminHistory(log)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加日志时，数据库操作失败！");
						return map;
					}

					// 以下为审核流程及财务流程

					// 获取税点
					BuyOrderBean buyOrder = service
							.getBuyOrder("id=(select buy_order_id from buy_stock where id="
									+ bs.getId() + ")");
					double taxPoint = 0;
					if (buyOrder != null) {
						taxPoint = buyOrder.getTaxPoint();
					}
					// 审核采购入库单
					stockin.setStatus(BuyStockinBean.STATUS4);
					stockin.setAuditingUserId(user.getId());
					// 更新状态为已审核
					if (!service.updateBuyStockin("status="+BuyStockinBean.STATUS4,
							"id=" + stockin.getId())) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "审核入库单失败，请检查后再试!");
						return map;
					}
					// log记录
//					double totalMoney = 0;
					// 财务入库明细表添加数据
					// 计算此入库单，入库的总金额
//					if (bsipList != null && bsipList.size() > 0) {
//						BuyStockinProductBean bsipb = null;
//						FinanceBuyProductBean fbpb = null;
//						ResultSet rs = null;
//						String sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
//								+ "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
//						for (int j = 0; j < bsipList.size(); j++) {
//							bsipb = (BuyStockinProductBean) bsipList.get(j);
//
//							fbpb = createFinanceBuyProductBeanInfo(bsipb,
//									stockin, buyOrder, operationTime);
//							rs = fService.getDbOp().executeQuery(
//									sql + bsipb.getProductId());
//							while (rs.next()) {
//
//								fbpb.setProductLineId(rs.getInt(1));// 添加产品线
//							}
//							if (!fService.addFinanceBuyProductBean(fbpb)) {
//								wareService.getDbOp().rollbackTransaction();
//								map.put("status", "fail");
//								map.put("tip", "添加财务数据时，数据库操作失败！");
//								return map;
//							}
//							totalMoney = Arith.add(
//									totalMoney,
//									Arith.mul(
//											bsipb.getStockInCount(),
//											Arith.mul(bsipb.getPrice3(),
//													Arith.add(1, taxPoint))));
//							totalMoney = Arith.round(totalMoney, 2);
//						}
//					}
//					// 入库单后，为财务表添加数据
//					FinanceBuyPayBean fbp = createFinanceBuyPayBeanInfo(
//							buyOrder, stockin, totalMoney, operationTime);
//					if (!fService.addFinanceBuyPayBean(fbp)) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "添加财务数据，数据库操作失败！");
//						return map;
//					}// 财务表添加
						// ---------------------------------
					BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(user, stockin, bs, 3, product);
					if (!service.addBuyAdminHistory(log1)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加日志时，数据库操作失败！");
						return map;
					}
					log1 = createBuyAdminHistoryBeanInfo(user, stockin, bs, 4, product);
					if (!service.addBuyAdminHistory(log1)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加日志时，数据库操作失败！");
						return map;
					}
					
					String set = null;
					ProductStockBean ps = null;
					bsip.setPrice3(Double.valueOf(
							String.valueOf(Arith.mul(bsip.getPrice3(),
									Arith.add(1, taxPoint)))).floatValue());

					if (bsip.getStockInCount() <= 0) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "采购入库量不能为0，操作失败！");
						return map;
					}

					if (product.getIsPackage() == 1) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "入库单中包含有套装产品，不能入库！");
						return map;
					}
					product.setPsList(psService.getProductStockList(
							"product_id=" + product.getId(), -1, -1, null));
					ps = psService.getProductStock("id=" + bsip.getStockInId());
					float price5 = 0f;
					int totalCount = product.getStockAll()
							+ product.getLockCountAll();
					price5 = ((float) Math.round((product.getPrice5()
							* (totalCount) + (bsip.getPrice3() * bsip
							.getStockInCount()))
							/ (totalCount + bsip.getStockInCount()) * 1000)) / 1000;

					bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
					bsip.setRemark("操作前库存" + ps.getStock() + ",操作后库存"
							+ (ps.getStock() + bsip.getStockInCount()));
					bsip.setConfirmDatetime(operationTime);
//					try {
//						service.getDbOp().executeUpdate(
//								"update product set price5=" + price5
//										+ " where id = " + product.getId());
//					} catch (Exception e) {
//						e.printStackTrace();
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "更新商品状态失败!");
//						return map;
//					}

					// 财务数据填充：finance_product表--------liuruilan---------
////					FinanceProductBean fProduct = fService
////							.getFinanceProductBean("product_id = "
////									+ product.getId());
////					if (fProduct == null) {
////						wareService.getDbOp().rollbackTransaction();
////						map.put("status", "fail");
////						map.put("tip", "查询异常，请与管理员联系！");
////						return map;
////					}
//					float priceSum = Arith.mul(price5,
//							totalCount + bsip.getStockInCount());
//					float priceHasticket = 0;
//					float priceNoticket = 0;
//					float priceSumHasticket = fProduct.getPriceSumHasticket();
//					float priceSumNoticket = fProduct.getPriceSumNoticket();
//					int ticket = FinanceSellProductBean.queryTicket(
//							service.getDbOp(), bs.getCode()); // 是否含票
//					if (ticket == -1) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "查询异常，请与管理员联系！");
//						return map;
//					}
//					int _count = FinanceProductBean.queryCountIfTicket(
//							service.getDbOp(), bsip.getProductId(), ticket);
//					set = "price =" + price5 + ", price_sum =" + priceSum;
//					if (ticket == 0) { // 0-有票
//						_price3 = (float) Arith.div(
//								Arith.mul(_price3, Arith.add(1, taxPoint)),
//								1.17);
//						// 计算公式：(fProduct.getPriceSumHasticket() + (_price3 *
//						// _count)) / (totalCount + _count)
//						priceHasticket = Arith.round(Arith.div(Arith.add(
//								fProduct.getPriceSumHasticket(),
//								Arith.mul(_price3, bsip.getStockInCount())),
//								Arith.add(_count, bsip.getStockInCount())), 2);
//						priceSumHasticket = Arith.mul(priceHasticket,
//								bsip.getStockInCount() + _count);
//						set += ", price_hasticket =" + priceHasticket
//								+ ", price_sum_hasticket =" + priceSumHasticket;
//					}
//					if (ticket == 1) { // 1-无票
//						_price3 = (float) Arith.mul(_price3,
//								Arith.add(1, taxPoint));
//						priceNoticket = Arith.round(Arith.div(
//								Arith.add(
//										fProduct.getPriceSumNoticket(),
//										Arith.mul(_price3,
//												bsip.getStockInCount())),
//								Arith.add(_count, bsip.getStockInCount())), 2);
//						priceSumNoticket = Arith.mul(priceNoticket,
//								bsip.getStockInCount() + _count);
//						set += ", price_noticket =" + priceNoticket
//								+ ", price_sum_noticket =" + priceSumNoticket;
//					}
//					if (!fService.updateFinanceProductBean(set, "product_id = "
//							+ product.getId())) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "修改操作数据时，数据库操作失败！");
//						return map;
//					}
					// --------------liuruilan----------------

					// 更新货位库存2011-04-19
					CargoInfoAreaBean inCargoArea = cargoService
							.getCargoInfoArea("old_id = " + ps.getArea());
					CargoProductStockBean cps = null;
					CargoInfoBean cargo = null;
					List cocList = cargoService
							.getCargoAndProductStockList(
									"ci.stock_type = " + ps.getType()
											+ " and ci.area_id = "
											+ inCargoArea.getId()
											+ " and ci.store_type = "
											+ CargoInfoBean.STORE_TYPE2
											+ " and cps.product_id = "
											+ bsip.getProductId(), -1, -1,
									"ci.id desc");

					// 更改待验库对应货位库存
					if (cocList == null || cocList.size() == 0) {// 产品首次入库，无暂存区绑定货位库存信息
						cargo = cargoService.getCargoInfo("stock_type = "
								+ ps.getType() + " and area_id = "
								+ inCargoArea.getId() + " and store_type = "
								+ CargoInfoBean.STORE_TYPE2);
						if (cargo == null) {
							wareService.getDbOp().rollbackTransaction();
							map.put("status", "fail");
							map.put("tip", "目的待验库缓存区货位未设置，请先添加后再完成入库！");
							return map;
						}
						cps = new CargoProductStockBean();
						cps.setCargoId(cargo.getId());
						cps.setProductId(bsip.getProductId());
						// 注意这里不加货位库存了所以 stockCount 改为0直接加入到锁定中
						cps.setStockCount(0);
						cargoService.addCargoProductStock(cps);
						cps.setId(cargoService.getDbOp().getLastInsertId());
					} else {
						cps = (CargoProductStockBean) cocList.get(0);
					}
					// 更新库存
					cargoService.updateCargoProductStock(
							"stock_count=stock_count+" + count,
							"id=" + cps.getId());
					psService.updateProductStock("stock=stock+" + count, "id="
							+ ps.getId());
					// 更新订单已入库量和已入库总金额
					// 更新订单已入库量和已入库总金额
					String area = "";
					if (stockin.getStockArea() == ProductStockBean.AREA_BJ) {
						area = "bj";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_GF) {
						area = "gd";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_ZC) {// 增城用北京的字段
						area = "bj";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_WX) {
						area = "gd";
					}else if(stockin.getStockArea() == ProductStockBean.AREA_JD){//京东记到广东
						area = "gd";
					}else if(stockin.getStockArea()==ProductStockBean.AREA_XA){//西安用北京的字段
						area = "bj";
					} else {
						area = "gd";
					}
					set = "stockin_count_" + area + "=(stockin_count_" + area
							+ " + " + bsip.getStockInCount() + ")";
					String condition2 = "buy_order_id=" + bs.getBuyOrderId()
							+ " and product_id=" + product.getId();
					if (!service.updateBuyOrderProduct(set, condition2)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "修改采购订单数据时，数据库操作失败！");
						return map;
					}
					// 添加批次记录
					// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
//					StockBatchBean batch = null;
//					batch = service.getStockBatch("code='" + bs.getCode()
//							+ "' and product_id = " + bsip.getProductId()
//							+ " and stock_type = "
//							+ ProductStockBean.STOCKTYPE_CHECK
//							+ " and stock_area = " + stockin.getStockArea());
//					if (batch == null) {
//						batch = createStockBatchBeanInfo(bs, stockin, bsip, ps,
//								ticket, operationTime);
//						service.addStockBatch(batch);
//					} else {
//						if (!service.updateStockBatch(
//								"batch_count = "
//										+ (batch.getBatchCount() + bsip
//												.getStockInCount()),
//								"code='" + bs.getCode() + "' and product_id = "
//										+ bsip.getProductId()
//										+ " and stock_type = "
//										+ ProductStockBean.STOCKTYPE_CHECK
//										+ " and stock_area = "
//										+ stockin.getStockArea())) {
//							wareService.getDbOp().rollbackTransaction();
//							map.put("status", "fail");
//							map.put("tip", "修改批次 信息时， 数据库操作失败！");
//							return map;
//						}
//					}
//
//					// 添加批次操作记录
//					StockBatchLogBean batchLog = createStockBatchLogBeanInfo(
//							batch, user, bsip, operationTime);
//					if (!service.addStockBatchLog(batchLog)) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "添加批次信息时，数据库操作失败！");
//						return map;
//					}

					// 判断并插入供货商关联信息
					voProductSupplier productSupplier = supplierService
							.getProductSupplierInfo("product_id = "
									+ bsip.getProductId()
									+ " and supplier_id = "
									+ bsip.getProductProxyId());
					if (productSupplier == null) {
						SupplierStandardInfoBean supplierStandardInfo = supplierService
								.getSupplierStandardInfo("id = "
										+ bsip.getProductProxyId());

						productSupplier = new voProductSupplier();
						productSupplier.setProduct_id(bsip.getProductId());
						productSupplier
								.setSupplier_id(bsip.getProductProxyId());
						if (supplierStandardInfo != null) {
							productSupplier
									.setSupplier_name(supplierStandardInfo
											.getName());
						}
						if (!supplierService
								.addProductSupplierInfo(productSupplier)) {
							wareService.getDbOp().rollbackTransaction();
							map.put("status", "fail");
							map.put("tip", "添加供货商信息时，数据库操作失败！");
							return map;
						}
					}

					if (!service.addBuyStockinProduct(bsip)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加添加采购入库单商品时 数据库操作失败！");
						return map;
					}

					// 审核通过，就加 进销存卡片
					product.setPsList(psService.getProductStockList(
							"product_id=" + bsip.getProductId(), -1, -1, null));
					cps = (CargoProductStockBean) cargoService
							.getCargoAndProductStockList(
									"cps.id = " + cps.getId(), 0, 1,
									"cps.id asc").get(0);

					// 计算入库金额
					double totalPrice = bsip.getStockInCount()
							* bsip.getPrice3();

					// 入库卡片
					StockCardBean sc = createStockCardBeanInfo(product,
							stockin, bsip, totalPrice, price5, operationTime);
					if (!psService.addStockCard(sc)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库卡片时， 数据库操作失败！");
						return map;
					}

//					// 财务进销存卡片---liuruilan-----
//					FinanceStockCardBean fsc = createFinanceStockCardBeanInfo(
//							service, ticket, sc, price5, bs, bsip, totalPrice,
//							priceSumHasticket, priceSumNoticket,
//							priceHasticket, priceNoticket, _count,
//							operationTime);
//					if (!fService.addFinanceStockCardBean(fsc)) {
//						wareService.getDbOp().rollbackTransaction();
//						map.put("status", "fail");
//						map.put("tip", "添加财务卡片时， 数据库操作失败！");
//						return map;
//					}

					// 货位入库卡片
					CargoStockCardBean csc = createCargoStockCardBeanInfo(
							stockin, bsip, product, price5, totalPrice, sc,
							cps, operationTime);
					if (!cargoService.addCargoStockCard(csc)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加货位入库卡片时，数据库操作失败！");
						return map;
					}

					// 拼装 bean 为打印时提供全部信息
					bsip.setTotalStockBeforeStockin(sc.getAllStock()
							- bsip.getStockInCount());
					if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ product.getParentId1()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ product.getParentId1()).getName();
						product.setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ product.getParentId2()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ product.getParentId2()).getName();
						product.setProductLineName(productLineName);
					} else if (wareService
							.getProductLine("product_line_catalog.catalog_id="
									+ product.getParentId3()) != null) {
						String productLineName = wareService.getProductLine(
								"product_line_catalog.catalog_id="
										+ product.getParentId3()).getName();
						product.setProductLineName(productLineName);
					} else {
						product.setProductLineName("");
					}

					// 生成装箱单
					String code = cService.getZXCodeForToday();
					CartonningInfoBean bean = new CartonningInfoBean();
					bean.setCode(code);
					bean.setCreateTime(operationTime);
					bean.setStatus(1);
					bean.setName(user.getUsername());
					bean.setCargoId(cps.getCargoId());
					bean.setBuyStockInId(stockin.getId());
					CartonningProductInfoBean productBean = new CartonningProductInfoBean();
					productBean.setProductCount(count);
					productBean.setProductCode(product.getCode());
					productBean.setProductName(product.getOriname());
					productBean.setProductId(product.getId());
					bean.setProductBean(productBean);
					if (!cService.addCartonningInfo(bean)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建装箱单失败!");
						return map;
					}
					if( !cService.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建装箱单失败!");
						return map;
					}
					code = bean.getCode();
					int id  = wareService.getDbOp().getLastInsertId();
					bean = cService.getCartonningInfo("code='" + bean.getCode()
							+ "'");
					// 添加质检上架关联的地方
					BuyStockinUpshelfBean bsuBean = new BuyStockinUpshelfBean();
					bsuBean.setBuyStockinId(stockin.getId());
					bsuBean.setBuyStockinDatetime(stockin.getConfirmDatetime());
					bsuBean.setProductId(product.getId());
					bsuBean.setProductCode(product.getCode());
					bsuBean.setCartonningInfoId(bean.getId());
					bsuBean.setCartonningInfoName(bean.getName());
					bsuBean.setCargoOperationId(0);
					bsuBean.setWareArea(stockin.getStockArea());
					if (!checkStockinMissionService
							.addBuyStockinUpshelf(bsuBean)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建入库单和装箱单关联失败!");
						return map;
					}
					productBean.setCartonningId(bean.getId());
					if( !cService.addCartonningProductInfo(productBean) ) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "创建装箱单失败!");
						return map;
					}

					// 采购入库明细
					CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
							stockin, product, missionId, bsip, bean,
							operationTime);

					siList.add(stockin.getCode() + "-" + code);

					if (!statService.addCheckStockinMissionDetail(csmdBean)) {
						wareService.getDbOp().rollbackTransaction();
						map.put("status", "fail");
						map.put("tip", "添加入库明细失败！");
						return map;
					}
					
					//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点3.1：单箱财务开始");////////////////////////////////////////////////////////////////////////
					
					baseProductList = new ArrayList<BaseProductInfo>();
					baseProduct = new BaseProductInfo();
					baseProduct.setId(bsip.getProductId());
					baseProduct.setInCount(bsip.getStockInCount());
					baseProduct.setInPrice(bsip.getPrice3());
					baseProduct.setProductStockId(bsip.getStockInId());
					baseProductList.add(baseProduct);
					fService.acquireFinanceBaseData(baseProductList, stockin.getCode(), 
							user.getId(), stockin.getStockType(), stockin.getStockArea());
					
					//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点3.2：单箱财务结束");////////////////////////////////////////////////////////////////////////
					
				}
				
				//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点4：单箱结束");////////////////////////////////////////////////////////////////////////
				
				// 更新复录状态及余量信息
				if (!statService.updateCheckStockinMissionBatch(
						"second_check_count=" + productCount
								+ ",current_check_count=current_check_count+"
								+ productCount
								+ ",second_check_status=1,left_count=0", "id="
								+ batchId)) {
					wareService.getDbOp().rollbackTransaction();
					map.put("status", "fail");
					map.put("tip", "更新复录状态及余量信息时出错，请联系管理员!");
					return map;
				}

				bean1 = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				if (siList.size() > 0) {
					for (int s = 0; s < siList.size(); s++) {
						codelist += siList.get(s) + ",";
					}
				}
				
				
				//完成任务单的一系列操作
				CheckStockinMissionBatchBean batchBean2 = statService.getCheckStockinMissionBatch("id="+batchId);
				if (batchBean2.getStatus() == CheckStockinMissionBatchBean.STATUS3) {
					wareService.getDbOp().rollbackTransaction();
					map.put("status", "fail");
					map.put("tip", "该任务已经确认完成！");
					return map;
				}
				checkStockinMissionService.confirmComCheckStockin(Integer.parseInt(missionId),
						batchBean2, user);
				initVerificationService.insertRInitData(beanList, user.getId(), wareService.getDbOp());
				wareService.getDbOp().commitTransaction();
				
				//计算返利金额
				ContractService contractService = SpringHandler.getBean(ContractService.class);
				if(!contractService.dealContractMony(bs.getBuyOrderId())) {
					map.put("tip", "复录成功后计算返利金额失败");
					return map;
				}
			}
			
			//System.out.println(DateUtil.getNow()+"-"+buyStockinCode+"-"+productId+"-复录节点5：结束");////////////////////////////////////////////////////////////////////////
			
			map.put("status", "success");
			map.put("tip", "复录成功！");
			map.put("codelist", codelist);
			return map;
		} catch (Exception e) {
			System.out.print(DateUtil.getNow()+" 复录:");e.printStackTrace();
			boolean isAuto = false;
			try {
				isAuto = wareService.getDbOp().getConn().getAutoCommit();
			} catch (SQLException e1) {
				System.out.println(DateUtil.getNow());e1.printStackTrace();
			}
			if( !isAuto ) {
				service.getDbOp().rollbackTransaction();
			}
			map.put("status", "fail");
			map.put("tip", "操作发生异常，请联系管理员！");
		} finally {
			statService.releaseAll();
		}
		return map;
	}
	
	/**
	 * 对初录数据进行还原，一边重新初录
	 * @param request
	 * @param response
	 * @param batchId
	 * @return
	 */
	@RequestMapping("check/resetFirstCheck")
	@ResponseBody
	public Map<String,String> reFirstCheck(HttpServletRequest request, HttpServletResponse response,
			@RequestParam("checkStockinMissionId") String checkStockinMissionId,
			@RequestParam("checkStockinMissionBatchId") String checkStockinMissionBatchId,
			@RequestParam("buyStockCode") String buyStockCode
	) {
		Map<String,String> map = new HashMap<String,String>();
		
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			map.put("status", "fail");
			map.put("tip", "当前没有登录，操作失败！");
			return map;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(786)) {
			map.put("status", "fail");
			map.put("tip", "您没有初录权限！");
			return map;
		}
		/*UserGroupBean group = user.getGroup();
		if (!group.isFlag(689)) {
			map.put("status", "fail");
			map.put("tip", "您没有权限添加质检任务！");
			return map;
		}*/
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			wareService.getDbOp().startTransaction();
			BuyStockBean bs = statService
				.getBuyStockBeanByCode(buyStockCode);
				if(bs.getStatus()==BuyStockBean.STATUS6){
					map.put("status", "fail");
					map.put("tip", "预计单的状态为采购已完成，不可以再进行操作了！");
					return map;
				}
				
			// int batchid =
			// statService.getCheckStockinMissionBatchInfo(csmCode);
			CheckStockinMissionBatchBean ss = statService
					.getCheckStockinMissionBatch("id=" + checkStockinMissionBatchId);
			// 根据batchid更新数据库
			CheckStockinMissionBean csmBean = statService.getCheckStockinMission("id=" + checkStockinMissionId);
			if( ss == null || csmBean == null  )  {
				map.put("status", "fail");
				map.put("tip", "没有找到对应的质检任务单！");
				return map;
			}
			if( ss.getSecondCheckStatus() == 1 ) {
				map.put("status", "fail");
				map.put("tip", "质检任务已经进行了复录，不可以再重新初录了！");
				return map;
			}
			if( csmBean.getStatus() == CheckStockinMissionBean.STATUS3) {
				map.put("status", "fail");
				map.put("tip", "质检任务状态为已完成，不可以再进行初录了！");
				return map;
			} else if (csmBean.getStatus()  == CheckStockinMissionBean.STATUS4 ) {
				map.put("status", "fail");
				map.put("tip", "质检任务状态为已删除，不可以再进行初录了！");
				return map;
			}
			
			String set = "";
			set = " first_check_count=0, first_check_status=0";
			String condition = " id=" + checkStockinMissionBatchId;
			if (statService.updateCheckStockinMissionBatch(set, condition)) {
				wareService.getDbOp().commitTransaction();
				map.put("status", "success");
				map.put("tip", "数据修改成功，可以重新录入了！");
				return map;
			} else {
				// System.out.println("update fire-------------->");
				wareService.getDbOp().rollbackTransaction();
				map.put("status", "fail");
				map.put("tip", "操作失败！");
				return map;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
		} finally {
			wareService.getDbOp().release();
		}
		map.put("status", "fail");
		map.put("tip", "发生了异常！");
		return map;
	}
	
}

