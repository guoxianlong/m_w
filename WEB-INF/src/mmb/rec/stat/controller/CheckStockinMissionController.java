package mmb.rec.stat.controller;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.finance.stat.FinanceBuyPayBean;
import mmb.finance.stat.FinanceBuyProductBean;
import mmb.finance.stat.FinanceProductBean;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.rec.stat.bean.CheckStockinMissionFormBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.stock.stat.CheckStockinMissionAction;
import mmb.stock.stat.CheckStockinMissionBatchBean;
import mmb.stock.stat.CheckStockinMissionBean;
import mmb.stock.stat.CheckStockinMissionDetailBean;
import mmb.stock.stat.CheckStockinMissionService;
import mmb.stock.stat.ProductLineCatalogBean;
import mmb.stock.stat.StatService;
import mmb.stock.stat.StockinUnqualifiedService;
import mmb.ware.WareService;
import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cache.ProductLinePermissionCache;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductSupplier;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("admin")
public class CheckStockinMissionController {

	private static Object checkStockinLock = new Object();
	private final Log logger = LogFactory
			.getLog(CheckStockinMissionAction.class);

	/**
	 * 判断状态
	 * 
	 * @param request
	 * @param resp
	 * @return
	 */
	@RequestMapping("checkStatus1")
	@ResponseBody
	public String checkStatus(HttpServletRequest request,
			HttpServletResponse resp) {

		List<Object> list = new ArrayList<Object>();
		Map<String, String> map2 = null;
		for (int i = 0; i < CheckStockinMissionBean.STATUS4; i++) {
			map2 = new HashMap<String, String>();
			map2.put("id", i + "");
			map2.put("statusName", CheckStockinMissionBean.getStatusName(i));
			list.add(map2);
		}
		String status = JSONArray.fromObject(list).toString();
		return status;
	}

	/**
	 * 查询产品线
	 * 
	 * @param request
	 * @param resp
	 * @return
	 */
	@RequestMapping("queryProudctLine")
	@ResponseBody
	public String queryProudctLine(HttpServletRequest request,
			HttpServletResponse resp) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		try {
			// 得到产品线列表
			String productLinePermission = ProductLinePermissionCache
					.getProductLineIds(user);
			productLinePermission = "product_line.id in ("
					+ productLinePermission + ")";
			List<?> productLineList = wareService
					.getProductLineList(productLinePermission);
			return JSONArray.fromObject(productLineList).toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.getDbOp().release();
		}
		return "";

	}

	/**
	 * 查询供应商
	 * 
	 * @param request
	 * @param resp
	 * @return
	 */
	@RequestMapping("querysupplyId")
	@ResponseBody
	public String querysupplyId(HttpServletRequest request,
			HttpServletResponse resp) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		try {
			// 供应商
			String supplierIds = ProductLinePermissionCache
					.getProductLineSupplierIds(user);
			supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;
			List<?> supplierList = wareService.getSelects(
					"supplier_standard_info", "where status = 1 and id in ("
							+ supplierIds + ") order by id");
			return JSONArray.fromObject(supplierList).toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.getDbOp().release();
		}
		return "";
	}

	/**
	 * 查询产能
	 * 
	 * @param request
	 * @param resp
	 * @return
	 */
	@RequestMapping("queryproductLoad")
	@ResponseBody
	public String queryproductLoad(HttpServletRequest request,
			HttpServletResponse resp) {
		return "";
	}

	/**
	 * 查询优先级
	 * 
	 * @param request
	 * @param resp
	 * @return
	 */
	@RequestMapping("querypriority")
	@ResponseBody
	public String querypriority(HttpServletRequest request,
			HttpServletResponse resp) {
		return "";
	}

	/**
	 * 查询库地区
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("queryWareLabel")
	@ResponseBody
	public String queryWareLabel(HttpServletRequest request) {
		Map<String, String> map = null;
		List<String> cdaList = CargoDeptAreaService
				.getCargoDeptAreaList(request);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (cdaList.size() == 0) {
			map = new HashMap<String, String>();
			map.put("id", "-1");
			map.put("name", "无地区权限");
			list.add(map);
		} else {
			for (int i = 0; i < cdaList.size(); i++) {
				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
				map = new HashMap<String, String>();
				map.put("id", cdaList.get(i));
				map.put("name", (String) ProductStockBean.areaMap.get(areaId));
				list.add(map);
			}
		}
		return JSONArray.fromObject(list).toString();
	}

	//@RequestMapping("editCheckStockinMission2")
	/*public String editCheckStockinMission2(HttpServletRequest request,
			HttpServletResponse req, ModelMap model,
			@RequestParam("missionId") int missionId) {
		model.addAttribute("missionId", missionId);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		model.addAttribute("firstCheck", group.isFlag(786));
		model.addAttribute("secondCheck", group.isFlag(787));
		return "/admin/rec/stat/checkstockinmission/editCheckStockinMission";
	}

	// 编辑质检入库任务
	//@RequestMapping("/editCheckStockinMission1")
	//@ResponseBody
	public String editCheckStockinMission1(HttpServletRequest request,
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

			@SuppressWarnings("unchecked")
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
			return JSONArray.fromObject(missionBean).toString();
		} catch (Exception e) {
			e.printStackTrace();
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

	

	// 返回map，如果key有0，表示出错，有1表示正确，有2表示存在产品线,3表示产品先对应任务id
	private Map<String, Object> constructCondition(HttpServletRequest request,
			 StockinUnqualifiedService sus,
			CheckStockinMissionService missionService, StringBuilder param,
			String availAreaIds) {

		StringBuilder condition = new StringBuilder("status!="
				+ CheckStockinMissionBean.STATUS4);
		StringBuilder parentId1 = new StringBuilder();
		StringBuilder parentId2 = new StringBuilder();
		String beginStockinTime = request.getParameter("beginStockinTime");
		String endStockinTime = request.getParameter("endStockinTime");
		String beginCompleteTime = request.getParameter("beginCompleteTime");;
		String endCompleteTime = request.getParameter("endCompleteTime");
		String missionIds = "";
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		if(beginCompleteTime != null && !"".equals(beginCompleteTime)){
			beginStockinTime = beginStockinTime + " 00:00:00";
			if(endStockinTime != null && !"".equals(endStockinTime)){
				endStockinTime = endStockinTime + " 23:59:59";
			}else{
				endStockinTime = DateUtil.getNowDateStr() + " 23:59:59";
			}
			param.append("beginStockinTime=").append(beginStockinTime);
			param.append("&");
			param.append("endStockinTime=").append(endStockinTime);
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("create_date_time between '")
					.append(beginStockinTime).append("' and '")
					.append(endStockinTime).append("'");
			if (DateUtil.compareTime(beginStockinTime, endStockinTime) == 1) {
				conditionMap.put("0", "入库开始时间不能大于结束时间！");
				return conditionMap;
			}
		}


		// 完成时间区间，开始时间
		if (beginCompleteTime != null
				&& !beginCompleteTime.equals("")) {
			beginCompleteTime = beginCompleteTime + " 00:00:00";
		} else {
			beginCompleteTime = "";
		}

		// 完成时间区间，结束时间
		if (endCompleteTime != null
				&& !endCompleteTime.equals("")) {
			endCompleteTime = endCompleteTime + " 23:59:59";
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
		
		String productLine = request.getParameter("productLine");//产品线
		String productCode = request.getParameter("productCode");//商品编号
		String supplyId = request.getParameter("supplyId");//供应商id

		// 找出产品线信息
		if (productLine != null && !productLine.equals("")
				&& !productLine.equals("0")) {
			@SuppressWarnings("unchecked")
			List<Object> productLineList2 = sus.getProductLineCatalogList(
					"product_line_id = " + productLine, -1, -1, null);
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
			param.append("productLine=").append(productLine);
			conditionMap.put("2", null);
		}

		// 找出满足产品线的任务id，以及供应商
		if (parentId1.length() > 0
				|| parentId2.length() > 0
				|| (productCode != null && !productCode.equals(""))
				|| (supplyId != null && !supplyId.equals("") && !supplyId.equals("0"))) {
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
					sParentId2, supplyId, null, null,
					productCode);
			conditionMap.put("3", missionIds);
		}

		String missionStatus = request.getParameter("missionStatus");
		// 任务状态条件
		if (missionStatus != null && !missionStatus.equals("")) {
			if (missionStatus != null
					&& !missionStatus.equals("")) {
				if (condition.length() > 0) {
					condition.append(" and ");
				}
				condition.append("status=").append(missionStatus);
				param.append("&");
				param.append("missionStatus=").append(missionStatus);
			} else {
				if (condition.length() > 0) {
					condition.append(" and ");
				}
				condition.append("status=").append(0);
				param.append("&");
				param.append("missionStatus=").append(0);
			}
		}

		String wareArea = request.getParameter("wareArea");
		// 到货地区条件
		if (null != wareArea && !"".equals(wareArea)) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("ware_area=").append(wareArea);
			param.append("&");
			param.append("wareArea=").append(wareArea);
		} else {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("ware_area in (").append(availAreaIds).append(")");
			param.append("&");
			param.append("wareArea=").append(-1);
		}

		String missionCode = request.getParameter("missionCode");
		// 任务单号条件
		if (missionCode != null && !missionCode.equals("")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("code='").append(missionCode)
					.append("'");
			param.append("&");
			param.append("missionCode=").append(missionCode);
		}

		String buyStockCode = request.getParameter("buyStockCode");
		// 预计单号条件
		if (buyStockCode != null
				&& !buyStockCode.equals("")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("buy_stockin_code='")
					.append(buyStockCode).append("'");
			param.append("&");
			param.append("buyStockCode=").append(buyStockCode);
		}

		String priority = request.getParameter("priority");
		// 任务优先程度条件
		if (priority != null && !priority.equals("")
				&& !priority.equals("-1")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("prior_status='").append(priority)
					.append("'");
			param.append("&");
			param.append("priority=").append(priority);
		}

		String productLoad = request.getParameter("productLoad");
		// 产能负荷条件
		if (productLoad != null && !productLoad.equals("")
				&& !productLoad.equals("-1")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("product_load='").append(productLoad)
					.append("'");
			param.append("&");
			param.append("productLoad=").append(productLoad);
		}

		String beginConsumTime = request.getParameter("beginConsumTime");
		String endConsumTime = request.getParameter("endConsumTime");
		// 实际耗时条件
		if ((beginConsumTime == null || beginConsumTime.equals(""))
				&& (endConsumTime != null && !endConsumTime.equals(""))) {
			conditionMap.put("0", "实际耗时开始时间不能为空！");
			return conditionMap;
		}

		if ((beginConsumTime != null && !beginConsumTime.equals(""))
				&& (endConsumTime == null || endConsumTime.equals(""))) {
			conditionMap.put("0", "实际耗时结束时间不能为空！");
			return conditionMap;
		}

		if ((beginConsumTime != null && !beginConsumTime.equals(""))
				&& (endConsumTime != null && !endConsumTime.equals(""))) {
			if (DateUtil.compareTime(beginConsumTime,
					endConsumTime) == 1) {
				conditionMap.put("0", "完成开始时间不能大于结束时间！");
				return conditionMap;
			}
		}
		if (beginConsumTime != null
				&& !beginConsumTime.equals("")) {
			param.append("&");
			param.append("beginConsumTime=").append(beginCompleteTime);
			param.append("&");
			param.append("endConsumTime=").append(endCompleteTime);
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("real_consumtime between '").append(
					beginConsumTime);
			condition.append("' and '").append(endCompleteTime)
					.append("'");
		}

		String createUserName = request.getParameter("createUserName");
		// 生成人条件
		if (createUserName != null
				&& !createUserName.equals("")) {
			if (condition.length() > 0) {
				condition.append(" and ");
			}
			condition.append("create_oper_name='")
					.append(createUserName).append("'");
			param.append("&");
			param.append("createUserName=").append(createUserName);
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

	*//**
	 * 打开初录页面
	 * @param request
	 * @param response
	 * @param model
	 * @param buyStockinCode
	 * @param batchId
	 * @param missionId
	 * @param productCode
	 * @return
	 *//*
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
		model.addAttribute("buyStockinCode", buyStockinCode);
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		synchronized (checkStockinLock) {
			try {
				CheckStockinMissionBatchBean cs = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				if (cs == null) {
					request.setAttribute("tip", "查询批次失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				// 根据missionId查询余量
				// int leftCount =
				// statService.getLeftCountByMissionId(missionId);
				int leftCount = cs.getLeftCount();
				int secondStatus = cs.getSecondCheckStatus();
				int secondCount = cs.getSecondCheckCount();

				String productId = statService
						.getProductIdbyProductCode(productCode);
				if (productId == null || "".equals(productId))
					productId = statService
							.getProductIdByProductBarcode(productCode);
				model.addAttribute("productId", productId);
				// 查询标准装箱量
				int binning = statService.queryBinning(missionId + "");
				model.addAttribute("binning", binning);
				model.addAttribute("secondStatus", secondStatus);
				model.addAttribute("secondCount", secondCount);
				model.addAttribute("leftCount", leftCount);
				model.addAttribute("batchId", batchId);
				model.addAttribute("missionId", missionId);
				model.addAttribute("productCode", productCode);
				model.addAttribute("firstCount", cs.getFirstCheckCount());
				model.addAttribute("firstStatus", cs.getFirstCheckStatus());
				model.addAttribute("primission", primission ? "1" : "0");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
		}

		return "/admin/cargo/beginCheck";
	}

	@RequestMapping("/validProduct")
	@ResponseBody
	*//**
	 * 验证商品是否属于该任务单
	 * csmcode:预计单号
	 * productcode:产品编号或条型码
	 *//*
	public String validProduct(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("productCode") String productCode,
			@RequestParam("csmCode") String csmCode) {
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {

			String productId = statService
					.getProductIdbyProductCode(productCode);
			if (productId == null || "".equals(productId))
				productId = statService
						.getProductIdByProductBarcode(productCode);

			if (productId == null || "".equals(productId)) {
				return "-1";
			} else {
				// 验证商品是否属于该单号
				// boolean flag = statService.validProductByCode(productId,
				// csmCode);
				// if (!flag)
				// return "0";
				return productId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.getDbOp().release();
		}
		return "1";
	}

	@RequestMapping("updateFristCheck")
	@ResponseBody
	*//**
	 * 初录
	 *//*
	public String updateFristCheck(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("csmCode") String csmCode,
			@RequestParam("productCount") int productCount,
			@RequestParam("batchId") int batchId, @RequestParam("type") int type) {
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (checkStockinLock) {

				// System.out.println("starting update------------>");
				wareService.getDbOp().startTransaction();

				BuyStockBean bs = statService.getBuyStockBeanByCode(csmCode);
				if (bs.getStatus() == BuyStockBean.STATUS6) {
					return "-1,0";
				}
				// int batchid =
				// statService.getCheckStockinMissionBatchInfo(csmCode);
				CheckStockinMissionBatchBean ss = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				String condition1 = statService.getBuyStockId(ss
						.getBuyStockinId());
				int count = statService.getTotalCount(condition1);
				request.getSession().setAttribute("currentCount", productCount);
				// 根据batchid更新数据库

				String set = "";
				if (type == 2)
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
		} finally {
			wareService.getDbOp().release();
		}

		return "";
	}

	*//**
	 * 打开复录页面
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param buyStockinCode
	 * @param batchId
	 * @param missionId
	 * @return
	 *//*
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
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		// group
		UserGroupBean group = user.getGroup();
		boolean primission = group.isFlag(116);
		CheckStockinMissionBatchBean cs = null;
		synchronized (checkStockinLock) {
			try {
				// 更新任务单状态-->质检入库中
				if (!statService.updateCheckStockinMissionBatch("status=2",
						"mission_id=" + missionId)) {
					request.setAttribute("tip", "更新批次质检状态失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				if (!statService.updateCheckStockinMission("status=2", "id="
						+ missionId)) {
					request.setAttribute("tip", "更新质检状态失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				// 查询标准装箱量
				int binning = statService.queryBinning(missionId);
				model.addAttribute("binning", binning);
				cs = statService.getCheckStockinMissionBatch("id=" + batchId);
				if (cs == null) {
					request.setAttribute("tip", "查询批次失败！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				// 查询初录数
				int firstCount = cs.getFirstCheckCount();
				model.addAttribute("firstCount", firstCount);
				// 根据missionId查询余量
				int leftCount = cs.getLeftCount();
				// 产品id
				String productId = statService
						.getProductIdbyProductCode(productCode);
				if (productId == null || "".equals(productId))
					productId = statService
							.getProductIdByProductBarcode(productCode);
				model.addAttribute("productId", productId);

				model.addAttribute("leftCount", leftCount);
				model.addAttribute("primission", primission ? "1" : "0");
				model.addAttribute("productCode", productCode);
				model.addAttribute("firstStatus", cs.getFirstCheckStatus());
				model.addAttribute("productCode", productCode);
				model.addAttribute("secondCount", cs.getSecondCheckCount());
				model.addAttribute("secondStatus", cs.getSecondCheckStatus());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.getDbOp().release();
			}
		}

		return "/admin/cargo/endCheck";
	}

	*//**
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
	 *//*
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
				.createProductStockService(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		FinanceReportFormsService fService = new FinanceReportFormsService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ISupplierService supplierService = ServiceFactory
				.createSupplierService(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		CartonningInfoService cService = new CartonningInfoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int binnCount = 0;// 可以装多少箱
		int count = firstCount % binning;
		BuyStockinBean stockin = null;
		String operationTime = "";
		PrintWriter out = null;
		CheckStockinMissionBatchBean bean1 = null;
		// int temp = 0;
		List<String> siList = new ArrayList<String>();
		// List<String> caList = new ArrayList<String>();
		String codelist = "";
		try {
			synchronized (checkStockinLock) {
				out = response.getWriter();
				wareService.getDbOp().startTransaction();
				BuyStockBean bs = statService
						.getBuyStockBeanByCode(buyStockinCode);
				if (bs.getStatus() == BuyStockBean.STATUS6) {
					out.write("采购已完成，不允许执行入库操作!");
					return null;
				}
				String productId = statService
						.getProductIdbyProductCode(productCode);
				if (productId == null || "".equals(productId))
					productId = statService
							.getProductIdByProductBarcode(productCode);

				voProduct product = wareService.getProduct(Integer
						.parseInt(productId));
				// 判断扫描数量是否>标准装箱量
				if (firstCount >= binning) {
					binnCount = firstCount / binning;

					for (int i = 0; i < binnCount; i++) {// 循环生成采购入库单及装箱装单
						operationTime = DateUtil.getNow();
						stockin = createBuyStockinBeanInfo(service, bs,
								operationTime, user);
						// 添加入库单
						try {
							service.addBuyStockin(stockin);
						} catch (Exception e) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库单失败!");
							return null;
						}
						List stockinList = service.getBuyStockinList(
								"buy_stock_id=" + bs.getId()
										+ " and buy_order_id="
										+ bs.getBuyOrderId()
										+ " and create_datetime='"
										+ operationTime + "'", -1, -1, "id");
						if (stockinList != null && stockinList.size() > 0)
							stockin = (BuyStockinBean) stockinList
									.get(stockinList.size() - 1);
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

						List<Object> bsipList = new ArrayList<Object>();
						bsipList.add(bsip);

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
						try {
							service.updateBuyStockin("status=6", "id="
									+ stockin.getId());
						} catch (Exception e) {
							wareService.getDbOp().rollbackTransaction();
							out.write("审核入库单失败，请检查后再试!");
							return null;
						}
						// log记录
						double totalMoney = 0;
						// 财务入库明细表添加数据
						// 计算此入库单，入库的总金额
						if (bsipList != null && bsipList.size() > 0) {
							BuyStockinProductBean bsipb = null;
							FinanceBuyProductBean fbpb = null;
							ResultSet rs = null;
							String sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
									+ "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
							for (int j = 0; j < bsipList.size(); j++) {
								bsipb = (BuyStockinProductBean) bsipList.get(j);

								fbpb = createFinanceBuyProductBeanInfo(bsipb,
										stockin, buyOrder, operationTime);
								rs = fService.getDbOp().executeQuery(
										sql + bsipb.getProductId());
								while (rs.next()) {

									fbpb.setProductLineId(rs.getInt(1));// 添加产品线
								}
								if (!fService.addFinanceBuyProductBean(fbpb)) {
									wareService.getDbOp().rollbackTransaction();
									out.write("添加财务数据时，数据库操作失败！");
									return null;
								}
								totalMoney = Arith.add(totalMoney, Arith.mul(
										bsipb.getStockInCount(),
										Arith.mul(bsipb.getPrice3(),
												Arith.add(1, taxPoint))));
								totalMoney = Arith.round(totalMoney, 2);
							}
						}
						// 入库单后，为财务表添加数据
						FinanceBuyPayBean fbp = createFinanceBuyPayBeanInfo(
								buyOrder, stockin, totalMoney, operationTime);
						if (!fService.addFinanceBuyPayBean(fbp)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加财务数据，数据库操作失败！");
							return null;
						}// 财务表添加
							// ---------------------------------
						BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(
								user, stockin, bs, 3, product);
						if (!service.addBuyAdminHistory(log1)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加日志时，数据库操作失败！");
							return null;
						}
						String set = null;
						ProductStockBean ps = null;
						float _price3 = bsip.getPrice3();
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
						price5 = ((float) Math.round((product.getPrice5()
								* (totalCount) + (bsip.getPrice3() * bsip
								.getStockInCount()))
								/ (totalCount + bsip.getStockInCount()) * 1000)) / 1000;

						bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
						bsip.setRemark("操作前库存" + ps.getStock() + ",操作后库存"
								+ (ps.getStock() + bsip.getStockInCount()));
						bsip.setConfirmDatetime(operationTime);
						try {
							service.getDbOp().executeUpdate(
									"update product set price5=" + price5
											+ " where id = " + product.getId());
						} catch (Exception e) {
							wareService.getDbOp().rollbackTransaction();
							e.printStackTrace();
							out.write("更新商品状态失败!");
							return null;
						}

						// 财务数据填充：finance_product表--------liuruilan---------
						FinanceProductBean fProduct = fService
								.getFinanceProductBean("product_id = "
										+ product.getId());
						if (fProduct == null) {
							wareService.getDbOp().rollbackTransaction();
							out.write("查询异常，请与管理员联系！");
							return null;
						}
						float priceSum = Arith.mul(price5,
								totalCount + bsip.getStockInCount());
						float priceHasticket = 0;
						float priceNoticket = 0;
						float priceSumHasticket = fProduct
								.getPriceSumHasticket();
						float priceSumNoticket = fProduct.getPriceSumNoticket();
						int ticket = FinanceSellProductBean.queryTicket(
								service.getDbOp(), bs.getCode()); // 是否含票
						if (ticket == -1) {
							wareService.getDbOp().rollbackTransaction();
							out.write("查询异常，请与管理员联系！");
							return null;
						}
						int _count = FinanceProductBean.queryCountIfTicket(
								service.getDbOp(), bsip.getProductId(), ticket);
						set = "price =" + price5 + ", price_sum =" + priceSum;
						if (ticket == 0) { // 0-有票
							_price3 = (float) Arith.div(
									Arith.mul(_price3, Arith.add(1, taxPoint)),
									1.17);
							// 计算公式：(fProduct.getPriceSumHasticket() + (_price3
							// *
							// _count)) / (totalCount + _count)
							priceHasticket = Arith.round(Arith.div(
									Arith.add(
											fProduct.getPriceSumHasticket(),
											Arith.mul(_price3,
													bsip.getStockInCount())),
									Arith.add(_count, bsip.getStockInCount())),
									2);
							priceSumHasticket = Arith.mul(priceHasticket,
									bsip.getStockInCount() + _count);
							set += ", price_hasticket =" + priceHasticket
									+ ", price_sum_hasticket ="
									+ priceSumHasticket;
						}
						if (ticket == 1) { // 1-无票
							_price3 = (float) Arith.mul(_price3,
									Arith.add(1, taxPoint));
							priceNoticket = Arith.round(Arith.div(
									Arith.add(
											fProduct.getPriceSumNoticket(),
											Arith.mul(_price3,
													bsip.getStockInCount())),
									Arith.add(_count, bsip.getStockInCount())),
									2);
							priceSumNoticket = Arith.mul(priceNoticket,
									bsip.getStockInCount() + _count);
							set += ", price_noticket =" + priceNoticket
									+ ", price_sum_noticket ="
									+ priceSumNoticket;
						}
						if (!fService.updateFinanceProductBean(set,
								"product_id = " + product.getId())) {
							wareService.getDbOp().rollbackTransaction();
							out.write("修改操作数据时，数据库操作失败！");
							return null;
						}
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

						} else {
							cps = (CargoProductStockBean) cocList.get(0);
						}
						// 更新库存
						cargoService.updateCargoProductStock(
								"stock_count=stock_count+" + binning, "id="
										+ cps.getId());
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
						}
						set = "stockin_count_" + area + "=(stockin_count_"
								+ area + " + " + bsip.getStockInCount() + "),"
								+ "stockin_total_price =(stockin_total_price+"
								+ bsip.getStockInCount() * bsip.getPrice3()
								+ ")";
						String condition2 = "buy_order_id="
								+ bs.getBuyOrderId() + " and product_id="
								+ product.getId();
						if (!service.updateBuyOrderProduct(set, condition2)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("修改采购订单数据时，数据库操作失败！");
							return null;
						}

						// 添加批次记录
						// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
						StockBatchBean batch = null;
						batch = service
								.getStockBatch("code='" + bs.getCode()
										+ "' and product_id = "
										+ bsip.getProductId()
										+ " and stock_type = "
										+ ProductStockBean.STOCKTYPE_CHECK
										+ " and stock_area = "
										+ stockin.getStockArea());
						if (batch == null) {
							batch = createStockBatchBeanInfo(bs, stockin, bsip,
									ps, ticket, operationTime);
							service.addStockBatch(batch);
						} else {
							if (!service.updateStockBatch(
									"batch_count = "
											+ (batch.getBatchCount() + bsip
													.getStockInCount()),
									"code='" + bs.getCode()
											+ "' and product_id = "
											+ bsip.getProductId()
											+ " and stock_type = "
											+ ProductStockBean.STOCKTYPE_CHECK
											+ " and stock_area = "
											+ stockin.getStockArea())) {
								wareService.getDbOp().rollbackTransaction();
								out.write("修改批次 信息时， 数据库操作失败！");
								return null;
							}
						}

						// 添加批次操作记录
						StockBatchLogBean batchLog = createStockBatchLogBeanInfo(
								batch, user, bsip, operationTime);
						if (!service.addStockBatchLog(batchLog)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加批次信息时，数据库操作失败！");
							return null;
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
						if (!service.addBuyStockinProduct(bsip)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加添加采购入库单商品时 数据库操作失败！");
							return null;
						}

						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList(
								"product_id=" + bsip.getProductId(), -1, -1,
								null));
						cps = (CargoProductStockBean) cargoService
								.getCargoAndProductStockList(
										"cps.id = " + cps.getId(), 0, 1,
										"cps.id asc").get(0);

						// 计算入库金额
						double totalPrice = bsip.getStockInCount()
								* batchLog.getBatchPrice();

						// 入库卡片
						StockCardBean sc = createStockCardBeanInfo(product,
								stockin, bsip, totalPrice, price5,
								operationTime);
						if (!psService.addStockCard(sc)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库卡片时， 数据库操作失败！");
							return null;
						}

						// 财务进销存卡片---liuruilan-----
						FinanceStockCardBean fsc = createFinanceStockCardBeanInfo(
								service, ticket, sc, price5, bs, bsip,
								totalPrice, priceSumHasticket,
								priceSumNoticket, priceHasticket,
								priceNoticket, _count, operationTime);
						if (!fService.addFinanceStockCardBean(fsc)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加财务卡片时， 数据库操作失败！");
							return null;
						}

						// 货位入库卡片
						CargoStockCardBean csc = createCargoStockCardBeanInfo(
								stockin, bsip, product, price5, totalPrice, sc,
								cps, operationTime);
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
						String code = "";
						if (i == 0)
							code = createZXCode();
						else {
							code = createZXCode();
							String code1 = code.substring(code.length() - 4);
							code = code.substring(0, code.length() - 4)
									+ String.format("%04d",
											new Object[] { new Integer(code1)
													+ i });
						}
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
						bean = cService.getCartonningInfo("code='"
								+ bean.getCode() + "'");
						productBean.setCartonningId(bean.getId());
						cService.addCartonningProductInfo(productBean);

						siList.add(stockin.getCode() + "-" + code);

						// 采购入库明细
						CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
								stockin, product, missionId, bsip, bean,
								operationTime);

						if (!statService.addCheckStockinMissionDetail(csmdBean)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库明细失败！");
							return null;
						}
						// wareService.getDbOp().commitTransaction();
					}
					// 更新复录状态及余量信息
					if (!statService
							.updateCheckStockinMissionBatch(
									"second_check_count="
											+ count
											+ ",first_check_count="
											+ count
											+ ",current_check_count=current_check_count+"
											+ binning
											* binnCount
											+ ",second_check_status=0,first_check_status=0,left_count="
											+ count, "id=" + batchId)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("更新复录状态及余量信息时出错，请联系管理员!");
						return null;
					}
					wareService.getDbOp().commitTransaction();
				}
				bean1 = statService
						.getCheckStockinMissionBatch("id=" + batchId);

				if (siList.size() > 0) {
					for (int s = 0; s < siList.size(); s++) {
						codelist += siList.get(s) + ",";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
		} finally {
			wareService.getDbOp().release();
		}
		// 返回值为录入成功！,余量,入库号-装箱号
		out.write("入库成功!," + bean1.getLeftCount() + "," + codelist);
		return null;
	}

	*//**
	 * 生成装箱单code
	 * 
	 * @author zhuguofu
	 * @return
	 *//*
	public String createZXCode() {
		CartonningInfoService service = new CartonningInfoService(
				IBaseService.CONN_IN_SERVICE, null);
		String code = "";
		synchronized (checkStockinLock) {
			code = "ZX" + DateUtil.getNow().substring(2, 4)
					+ DateUtil.getNow().substring(5, 7)
					+ DateUtil.getNow().substring(8, 10);
			CartonningInfoBean lastCode = service
					.getCartonningInfo("code like '" + code + "%'");
			if (lastCode == null) {
				// 当日第一份单据，编号最后四位 0001
				code += "0001";
			} else {
				String code1 = service.getLastInsertCode(code);
				code1 = code1.substring(code1.length() - 4);
				code += String.format("%04d",
						new Object[] { new Integer(code1) + 1 });
			}
		}

		service.releaseAll();
		return code;
	}

	*//**
	 * 生成采购入库单
	 * 
	 * @param service
	 * @param bs
	 * @param operationTime
	 * @param user
	 * @return
	 *//*
	public BuyStockinBean createBuyStockinBeanInfo(IStockService service,
			BuyStockBean bs, String operationTime, voUser user) {
		BuyStockinBean stockin = null;
		synchronized (checkStockinLock) {
			stockin = new BuyStockinBean();
			String buyStockCode = service.generateBuyStockinCode();
			stockin.setCode(buyStockCode);
			stockin.setCreateDatetime(operationTime);
			stockin.setConfirmDatetime(operationTime);
			stockin.setStatus(BuyStockinBean.STATUS0);
			stockin.setBuyStockId(bs.getId());
			stockin.setBuyOrderId(bs.getBuyOrderId());
			// stockin.setId(service.getNumber("id", "buy_stockin", "max",
			// "id > 0") + 1);
			stockin.setSupplierId(bs.getSupplierId());
			if (bs.getArea() == 0) {
				stockin.setStockArea(ProductStockBean.AREA_BJ);
			} else if (bs.getArea() == 1) {
				stockin.setStockArea(ProductStockBean.AREA_GF);
			} else if (bs.getArea() == 3) {
				stockin.setStockArea(ProductStockBean.AREA_ZC);
			} else if (bs.getArea() == 4) {
				stockin.setStockArea(ProductStockBean.AREA_WX);
			}
			stockin.setCreateUserId(user.getId());
			stockin.setCreateUserName(user.getName());
			stockin.setRemark(operationTime + "入库");
			stockin.setStockType(ProductStockBean.STOCKTYPE_CHECK);
		}

		return stockin;
	}

	*//**
	 * 生成日志
	 * 
	 * @param user
	 * @param stockin
	 * @param bs
	 * @return
	 *//*
	public BuyAdminHistoryBean createBuyAdminHistoryBeanInfo(voUser user,
			BuyStockinBean stockin, BuyStockBean bs, int flag, voProduct product) {
		BuyAdminHistoryBean log = new BuyAdminHistoryBean();
		synchronized (checkStockinLock) {
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

			if (flag == 3)
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			else
				log.setType(BuyAdminHistoryBean.TYPE_ADD);
		}

		return log;
	}

	*//**
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
	 *//*

	public BuyStockinProductBean createBuyStockinProductBeanInfo(
			WareService wareService, IProductStockService psService,
			IStockService service, BuyStockinBean stockin,
			String operationTime, BuyStockProductBean bsp, voProduct product,
			int binning) {
		BuyStockinProductBean bsip = null;
		synchronized (checkStockinLock) {
			bsip = new BuyStockinProductBean();
			voProduct product1 = wareService.getProduct(bsp.getProductId());
			ProductStockBean psb = psService.getProductStock("product_id="
					+ product1.getId() + " and type=" + stockin.getStockType()
					+ " and area=" + stockin.getStockArea());
			// int bsipId = service.getNumber("id", "buy_stockin_product",
			// "max",
			// "id > 0") + 1;
			bsip.setCreateDatetime(operationTime);
			bsip.setConfirmDatetime(operationTime);
			// bsip.setId(bsipId);
			bsip.setBuyStockinId(stockin.getId());
			bsip.setStockInId(psb.getId());
			bsip.setProductCode(product1.getCode());
			bsip.setProductId(product1.getId());
			bsip.setRemark("操作后库存增加:" + binning);
			bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
			bsip.setPrice3(bsp.getPurchasePrice());
			bsip.setProductProxyId(bsp.getProductProxyId());
			bsip.setOriname(product1.getOriname());
			bsip.setStockInCount(binning);// 入库量，即为标准装箱量
		}

		return bsip;
	}

	*//**
	 * 生成FinanceBuyProductBean
	 * 
	 * @param bsipb
	 * @param stockin
	 * @param buyOrder
	 * @param operationTime
	 * @return
	 *//*
	public FinanceBuyProductBean createFinanceBuyProductBeanInfo(
			BuyStockinProductBean bsipb, BuyStockinBean stockin,
			BuyOrderBean buyOrder, String operationTime) {
		FinanceBuyProductBean fbpb = null;
		synchronized (checkStockinLock) {
			fbpb = new FinanceBuyProductBean();
			fbpb.setBillsNumCode(stockin.getCode());
			fbpb.setBuyOrderCode(buyOrder.getCode());
			fbpb.setCreateDateTime(operationTime);
			fbpb.setProductCount(bsipb.getStockInCount());
			fbpb.setTaxPoint(Arith.round(buyOrder.getTaxPoint(), 4));
			fbpb.setTicket(buyOrder.getTicket());
			// 税后单价
			fbpb.setProductPrice(Arith.round(
					Arith.mul(bsipb.getPrice3(),
							Arith.add(1, buyOrder.getTaxPoint())), 3));
			fbpb.setProductId(bsipb.getProductId());
			fbpb.setSupplierId(bsipb.getProductProxyId());
			fbpb.setType(0);// 采购入库单用0表示，具体查看FinanceBuyPayBean类
		}

		return fbpb;
	}

	*//**
	 * create financebuypaybean
	 * 
	 * @param buyOrder
	 * @param stockin
	 * @param totalMoney
	 * @param operationTime
	 * @return
	 *//*
	public FinanceBuyPayBean createFinanceBuyPayBeanInfo(BuyOrderBean buyOrder,
			BuyStockinBean stockin, double totalMoney, String operationTime) {
		FinanceBuyPayBean fbp = null;
		synchronized (checkStockinLock) {
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
		}

		return fbp;

	}

	*//**
	 * 生成库存批次
	 * 
	 * @param bs
	 * @param stockin
	 * @param bsip
	 * @param ps
	 * @param ticket
	 * @param operationTime
	 * @return
	 *//*
	public StockBatchBean createStockBatchBeanInfo(BuyStockBean bs,
			BuyStockinBean stockin, BuyStockinProductBean bsip,
			ProductStockBean ps, int ticket, String operationTime) {
		StockBatchBean batch = null;
		synchronized (checkStockinLock) {
			batch = new StockBatchBean();
			batch.setCode(bs.getCode());
			batch.setProductId(bsip.getProductId());
			batch.setPrice(bsip.getPrice3());
			batch.setBatchCount(bsip.getStockInCount());
			batch.setProductStockId(ps.getId());
			batch.setStockArea(stockin.getStockArea());
			batch.setStockType(ProductStockBean.STOCKTYPE_CHECK);
			batch.setCreateDateTime(operationTime);
			batch.setTicket(ticket);
		}

		return batch;
	}

	*//**
	 * stockbatch日志
	 * 
	 * @param batch
	 * @param user
	 * @param bsip
	 * @param operationTime
	 * @return
	 *//*
	public StockBatchLogBean createStockBatchLogBeanInfo(StockBatchBean batch,
			voUser user, BuyStockinProductBean bsip, String operationTime) {
		StockBatchLogBean batchLog = null;
		synchronized (checkStockinLock) {
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
		}

		return batchLog;
	}

	*//**
	 * 库存
	 * 
	 * @param product
	 * @param stockin
	 * @param bsip
	 * @param totalPrice
	 * @param price5
	 * @param operationTime
	 * @return
	 *//*
	public StockCardBean createStockCardBeanInfo(voProduct product,
			BuyStockinBean stockin, BuyStockinProductBean bsip,
			double totalPrice, float price5, String operationTime) {
		StockCardBean sc = null;
		synchronized (checkStockinLock) {
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
			sc.setCurrentStock(product.getStock(sc.getStockArea(),
					sc.getStockType())
					+ product.getLockCount(sc.getStockArea(), sc.getStockType()));
			sc.setStockAllArea(product.getStock(stockin.getStockArea())
					+ product.getLockCount(stockin.getStockArea()));
			sc.setStockAllType(product.getStockAllType(stockin.getStockType())
					+ product.getLockCountAllType(stockin.getStockType()));
			sc.setAllStock(product.getStockAll() + product.getLockCountAll());
			sc.setStockPrice(price5);
			sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
					.multiply(
							new BigDecimal(StringUtil.formatDouble2(sc
									.getStockPrice()))).doubleValue());
		}

		return sc;
	}

	*//**
	 * 生成库存卡片
	 * 
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
	 *//*

	public FinanceStockCardBean createFinanceStockCardBeanInfo(
			IStockService service, int ticket, StockCardBean sc, float price5,
			BuyStockBean bs, BuyStockinProductBean bsip, double totalPrice,
			float priceSumHasticket, float priceSumNoticket,
			float priceHasticket, float priceNoticket, int _count,
			String operationTime) {
		FinanceStockCardBean fsc = null;
		synchronized (checkStockinLock) {
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

			fsc.setType(fsc.getCardType());
			fsc.setIsTicket(ticket);
			fsc.setStockBatchCode(bs.getCode());
			fsc.setBalanceModeStockCount(bsip.getStockInCount() + _count);
			if (ticket == 0) {
				fsc.setStockInPriceSum(Arith.round(Arith.div(totalPrice, 1.17),
						2));
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
		}

		return fsc;
	}

	*//**
	 * 生成货位进销存卡片
	 * 
	 * @param stockin
	 * @param bsip
	 * @param product
	 * @param price5
	 * @param totalPrice
	 * @param sc
	 * @param cps
	 * @param operationTime
	 * @return
	 *//*
	public CargoStockCardBean createCargoStockCardBeanInfo(
			BuyStockinBean stockin, BuyStockinProductBean bsip,
			voProduct product, float price5, double totalPrice,
			StockCardBean sc, CargoProductStockBean cps, String operationTime) {
		CargoStockCardBean csc = null;
		synchronized (checkStockinLock) {
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
					+ product.getLockCount(sc.getStockArea(), sc.getStockType()));
			csc.setAllStock(product.getStockAll() + product.getLockCountAll());
			csc.setCurrentCargoStock(cps.getStockCount()
					+ cps.getStockLockCount());
			csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
			csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
			csc.setStockPrice(price5);
			csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
					.multiply(
							new BigDecimal(StringUtil.formatDouble2(sc
									.getStockPrice()))).doubleValue());
		}

		return csc;
	}

	*//**
	 * 生成入库明细
	 * 
	 * @param stockin
	 * @param product
	 * @param missionId
	 * @param bsip
	 * @param bean
	 * @return
	 *//*
	public CheckStockinMissionDetailBean createCheckStockinMissionDetailBeanInfo(
			BuyStockinBean stockin, voProduct product, String missionId,
			BuyStockinProductBean bsip, CartonningInfoBean bean,
			String operationTime) {
		CheckStockinMissionDetailBean csmdBean = null;
		synchronized (checkStockinLock) {
			csmdBean = new CheckStockinMissionDetailBean();
			csmdBean.setBuyStockinCode(stockin.getCode());
			csmdBean.setBuyStockinCount(bsip.getStockInCount());
			csmdBean.setBuyStockinCreateDateTime(operationTime);
			csmdBean.setBuyStockinId(stockin.getId());
			csmdBean.setMissionId(Integer.parseInt(missionId));// 这里有可能有问题，待测试
			csmdBean.setProductCode(product.getCode());
			csmdBean.setProductId(product.getId());
			csmdBean.setCartonningName(bean.getCode());
		}

		return csmdBean;
	}

	*//**
	 * 手工提交
	 * 
	 * @return
	 *//*
	@RequestMapping("noAutoCheck")
	@ResponseBody
	public String noAutoCheck(HttpServletRequest request,
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
				.createProductStockService(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		FinanceReportFormsService fService = new FinanceReportFormsService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ISupplierService supplierService = ServiceFactory
				.createSupplierService(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		CartonningInfoService cService = new CartonningInfoService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		int binnCount = 0;// 可以装多少箱
		int count = productCount % binning;

		BuyStockinBean stockin = null;
		String operationTime = "";
		PrintWriter out = null;
		CheckStockinMissionBatchBean bean1 = null;
		List<String> siList = new ArrayList<String>();
		String codelist = "";
		try {
			synchronized (checkStockinLock) {
				wareService.getDbOp().startTransaction();
				out = response.getWriter();
				BuyStockBean bs = statService
						.getBuyStockBeanByCode(buyStockinCode);
				if (bs.getStatus() == BuyStockBean.STATUS6) {
					out.write("采购已完成，不允许执行入库操作!");
					return null;
				}
				String productId = statService
						.getProductIdbyProductCode(productCode);
				if (productId == null || "".equals(productId))
					productId = statService
							.getProductIdByProductBarcode(productCode);

				voProduct product = wareService.getProduct(Integer
						.parseInt(productId));
				int temp = 0;
				// 判断扫描数量是否>标准装箱量
				if (productCount >= binning) {
					binnCount = productCount / binning;

					for (int i = 0; i < binnCount; i++) {// 循环生成采购入库单及装箱装单
						operationTime = DateUtil.getNow();
						stockin = createBuyStockinBeanInfo(service, bs,
								operationTime, user);
						if (!service.addBuyStockin(stockin)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库单失败!");
							return null;
						}
						List stockinList = service.getBuyStockinList(
								"buy_stock_id=" + bs.getId()
										+ " and buy_order_id="
										+ bs.getBuyOrderId()
										+ " and create_datetime='"
										+ operationTime + "'", -1, -1, "id");
						if (stockinList != null && stockinList.size() > 0)
							stockin = (BuyStockinBean) stockinList
									.get(stockinList.size() - 1);
						// log记录
						BuyAdminHistoryBean log = createBuyAdminHistoryBeanInfo(
								user, stockin, bs, 1, product);

						if (!service.addBuyAdminHistory(log)) {
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

						List<Object> bsipList = new ArrayList<Object>();
						bsipList.add(bsip);

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
						try {
							service.updateBuyStockin("status=6", "id="
									+ stockin.getId());
						} catch (Exception e) {
							wareService.getDbOp().rollbackTransaction();
							out.write("审核入库单失败，请检查后再试!");
							return null;
						}
						// log记录
						double totalMoney = 0;
						// 财务入库明细表添加数据
						// 计算此入库单，入库的总金额
						if (bsipList != null && bsipList.size() > 0) {
							BuyStockinProductBean bsipb = null;
							FinanceBuyProductBean fbpb = null;
							ResultSet rs = null;
							String sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
									+ "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
							for (int j = 0; j < bsipList.size(); j++) {
								bsipb = (BuyStockinProductBean) bsipList.get(j);

								fbpb = createFinanceBuyProductBeanInfo(bsipb,
										stockin, buyOrder, operationTime);
								rs = fService.getDbOp().executeQuery(
										sql + bsipb.getProductId());
								while (rs.next()) {

									fbpb.setProductLineId(rs.getInt(1));// 添加产品线
								}
								if (!fService.addFinanceBuyProductBean(fbpb)) {
									wareService.getDbOp().rollbackTransaction();
									out.write("添加财务数据时，数据库操作失败！");
									return null;
								}
								totalMoney = Arith.add(totalMoney, Arith.mul(
										bsipb.getStockInCount(),
										Arith.mul(bsipb.getPrice3(),
												Arith.add(1, taxPoint))));
								totalMoney = Arith.round(totalMoney, 2);
							}
						}
						// 入库单后，为财务表添加数据
						FinanceBuyPayBean fbp = createFinanceBuyPayBeanInfo(
								buyOrder, stockin, totalMoney, operationTime);
						if (!fService.addFinanceBuyPayBean(fbp)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加财务数据，数据库操作失败！");
							return null;
						}// 财务表添加
							// ---------------------------------
						BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(
								user, stockin, bs, 3, product);
						if (!service.addBuyAdminHistory(log1)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加日志时，数据库操作失败！");
							return null;
						}
						String set = null;
						ProductStockBean ps = null;
						float _price3 = bsip.getPrice3();
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
						price5 = ((float) Math.round((product.getPrice5()
								* (totalCount) + (bsip.getPrice3() * bsip
								.getStockInCount()))
								/ (totalCount + bsip.getStockInCount()) * 1000)) / 1000;

						bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_DEALED);
						bsip.setRemark("操作前库存" + ps.getStock() + ",操作后库存"
								+ (ps.getStock() + bsip.getStockInCount()));
						bsip.setConfirmDatetime(operationTime);
						try {
							service.getDbOp().executeUpdate(
									"update product set price5=" + price5
											+ " where id = " + product.getId());
						} catch (Exception e) {
							wareService.getDbOp().rollbackTransaction();
							out.write("更新商品状态失败!");
							return null;
						}

						// 财务数据填充：finance_product表--------liuruilan---------
						FinanceProductBean fProduct = fService
								.getFinanceProductBean("product_id = "
										+ product.getId());
						if (fProduct == null) {
							wareService.getDbOp().rollbackTransaction();
							out.write("查询异常，请与管理员联系！");
							return null;
						}
						float priceSum = Arith.mul(price5,
								totalCount + bsip.getStockInCount());
						float priceHasticket = 0;
						float priceNoticket = 0;
						float priceSumHasticket = fProduct
								.getPriceSumHasticket();
						float priceSumNoticket = fProduct.getPriceSumNoticket();
						int ticket = FinanceSellProductBean.queryTicket(
								service.getDbOp(), bs.getCode()); // 是否含票
						if (ticket == -1) {
							wareService.getDbOp().rollbackTransaction();
							out.write("查询异常，请与管理员联系！");
							return null;
						}
						int _count = FinanceProductBean.queryCountIfTicket(
								service.getDbOp(), bsip.getProductId(), ticket);
						set = "price =" + price5 + ", price_sum =" + priceSum;
						if (ticket == 0) { // 0-有票
							_price3 = (float) Arith.div(
									Arith.mul(_price3, Arith.add(1, taxPoint)),
									1.17);
							// 计算公式：(fProduct.getPriceSumHasticket() + (_price3
							// *
							// _count)) / (totalCount + _count)
							priceHasticket = Arith.round(Arith.div(
									Arith.add(
											fProduct.getPriceSumHasticket(),
											Arith.mul(_price3,
													bsip.getStockInCount())),
									Arith.add(_count, bsip.getStockInCount())),
									2);
							priceSumHasticket = Arith.mul(priceHasticket,
									bsip.getStockInCount() + _count);
							set += ", price_hasticket =" + priceHasticket
									+ ", price_sum_hasticket ="
									+ priceSumHasticket;
						}
						if (ticket == 1) { // 1-无票
							_price3 = (float) Arith.mul(_price3,
									Arith.add(1, taxPoint));
							priceNoticket = Arith.round(Arith.div(
									Arith.add(
											fProduct.getPriceSumNoticket(),
											Arith.mul(_price3,
													bsip.getStockInCount())),
									Arith.add(_count, bsip.getStockInCount())),
									2);
							priceSumNoticket = Arith.mul(priceNoticket,
									bsip.getStockInCount() + _count);
							set += ", price_noticket =" + priceNoticket
									+ ", price_sum_noticket ="
									+ priceSumNoticket;
						}
						if (!fService.updateFinanceProductBean(set,
								"product_id = " + product.getId())) {
							wareService.getDbOp().rollbackTransaction();
							out.write("修改操作数据时，数据库操作失败！");
							return null;
						}
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
							cps.setCargoId(cps.getCargoId());
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
								"stock_count=stock_count+" + binning, "id="
										+ cps.getId());
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
						}
						set = "stockin_count_" + area + "=(stockin_count_"
								+ area + " + " + bsip.getStockInCount() + "),"
								+ "stockin_total_price =(stockin_total_price+"
								+ bsip.getStockInCount() * bsip.getPrice3()
								+ ")";
						String condition2 = "buy_order_id="
								+ bs.getBuyOrderId() + " and product_id="
								+ product.getId();
						if (!service.updateBuyOrderProduct(set, condition2)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("修改采购订单数据时，数据库操作失败！");
							return null;
						}

						// 添加批次记录
						// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
						StockBatchBean batch = null;
						batch = service
								.getStockBatch("code='" + bs.getCode()
										+ "' and product_id = "
										+ bsip.getProductId()
										+ " and stock_type = "
										+ ProductStockBean.STOCKTYPE_CHECK
										+ " and stock_area = "
										+ stockin.getStockArea());
						if (batch == null) {
							batch = createStockBatchBeanInfo(bs, stockin, bsip,
									ps, ticket, operationTime);
							service.addStockBatch(batch);
						} else {
							if (!service.updateStockBatch(
									"batch_count = "
											+ (batch.getBatchCount() + bsip
													.getStockInCount()),
									"code='" + bs.getCode()
											+ "' and product_id = "
											+ bsip.getProductId()
											+ " and stock_type = "
											+ ProductStockBean.STOCKTYPE_CHECK
											+ " and stock_area = "
											+ stockin.getStockArea())) {
								wareService.getDbOp().rollbackTransaction();
								out.write("修改批次 信息时， 数据库操作失败！");
								return null;
							}
						}

						// 添加批次操作记录
						StockBatchLogBean batchLog = createStockBatchLogBeanInfo(
								batch, user, bsip, operationTime);
						if (!service.addStockBatchLog(batchLog)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加批次信息时，数据库操作失败！");
							return null;
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

						if (!service.addBuyStockinProduct(bsip)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加添加采购入库单商品时 数据库操作失败！");
							return null;
						}

						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList(
								"product_id=" + bsip.getProductId(), -1, -1,
								null));
						cps = (CargoProductStockBean) cargoService
								.getCargoAndProductStockList(
										"cps.id = " + cps.getId(), 0, 1,
										"cps.id asc").get(0);

						// 计算入库金额
						double totalPrice = bsip.getStockInCount()
								* batchLog.getBatchPrice();

						// 入库卡片
						StockCardBean sc = createStockCardBeanInfo(product,
								stockin, bsip, totalPrice, price5,
								operationTime);
						if (!psService.addStockCard(sc)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库卡片时， 数据库操作失败！");
							return null;
						}

						// 财务进销存卡片---liuruilan-----
						FinanceStockCardBean fsc = createFinanceStockCardBeanInfo(
								service, ticket, sc, price5, bs, bsip,
								totalPrice, priceSumHasticket,
								priceSumNoticket, priceHasticket,
								priceNoticket, _count, operationTime);
						if (!fService.addFinanceStockCardBean(fsc)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加财务卡片时， 数据库操作失败！");
							return null;
						}

						// 货位入库卡片
						CargoStockCardBean csc = createCargoStockCardBeanInfo(
								stockin, bsip, product, price5, totalPrice, sc,
								cps, operationTime);
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
						// String code = createZXCode();
						// 生成装箱单
						String code = "";
						if (i == 0)
							code = createZXCode();
						else {
							code = createZXCode();
							String code1 = code.substring(code.length() - 4);
							code = code.substring(0, code.length() - 4)
									+ String.format("%04d",
											new Object[] { new Integer(code1)
													+ i });
						}
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
						bean = cService.getCartonningInfo("code='"
								+ bean.getCode() + "'");
						productBean.setCartonningId(bean.getId());
						cService.addCartonningProductInfo(productBean);

						// 采购入库明细
						CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
								stockin, product, missionId, bsip, bean,
								operationTime);

						if (!statService.addCheckStockinMissionDetail(csmdBean)) {
							wareService.getDbOp().rollbackTransaction();
							out.write("添加入库明细失败！");
							return null;
						}
						siList.add(stockin.getCode() + "-" + code);
						// wareService.getDbOp().commitTransaction();

					}
					// 更新复录状态及余量信息
					if (!statService
							.updateCheckStockinMissionBatch(
									"second_check_count="
											+ count
											+ ",first_check_count="
											+ count
											+ ",current_check_count=current_check_count+"
											+ binning
											* binnCount
											+ ",second_check_status=0,first_check_status=0,left_count="
											+ count, "id=" + batchId)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("更新复录状态及余量信息时出错，请联系管理员!");
						return null;
					}
					wareService.getDbOp().commitTransaction();

				} else {// 不足标准装箱量
					operationTime = DateUtil.getNow();
					stockin = createBuyStockinBeanInfo(service, bs,
							operationTime, user);
					// 添加入库单
					if (!service.addBuyStockin(stockin)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("添加入库单失败!");
						return null;
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
						out.write("在添加生成入库单日志时，数据库操作出错！");
						return null;
					}

					// 添加对应的商品
					int buyStockProductId = product.getId();
					BuyStockProductBean bsp = service
							.getBuyStockProduct("buy_stock_id = " + bs.getId()
									+ " and product_id = " + buyStockProductId);
					if (bsp == null) {
						wareService.getDbOp().rollbackTransaction();
						out.write("在对应的采购预计单中 没有找到对应的商品！");
						return null;
					}
					BuyStockinProductBean bsip = createBuyStockinProductBeanInfo(
							wareService, psService, service, stockin,
							operationTime, bsp, product, count);// 这里传的参数是余量

					List<Object> bsipList = new ArrayList<Object>();
					bsipList.add(bsip);

					// log记录
					log = createBuyAdminHistoryBeanInfo(user, stockin, bs, 2,
							product);

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
					if (!service.updateBuyStockin("status=6",
							"id=" + stockin.getId())) {
						wareService.getDbOp().rollbackTransaction();
						out.write("审核入库单失败，请检查后再试!");
						return null;
					}
					// log记录
					double totalMoney = 0;
					// 财务入库明细表添加数据
					// 计算此入库单，入库的总金额
					if (bsipList != null && bsipList.size() > 0) {
						BuyStockinProductBean bsipb = null;
						FinanceBuyProductBean fbpb = null;
						ResultSet rs = null;
						String sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc "
								+ "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id =";
						for (int j = 0; j < bsipList.size(); j++) {
							bsipb = (BuyStockinProductBean) bsipList.get(j);

							fbpb = createFinanceBuyProductBeanInfo(bsipb,
									stockin, buyOrder, operationTime);
							rs = fService.getDbOp().executeQuery(
									sql + bsipb.getProductId());
							while (rs.next()) {

								fbpb.setProductLineId(rs.getInt(1));// 添加产品线
							}
							if (!fService.addFinanceBuyProductBean(fbpb)) {
								wareService.getDbOp().rollbackTransaction();
								out.write("添加财务数据时，数据库操作失败！");
								return null;
							}
							totalMoney = Arith.add(
									totalMoney,
									Arith.mul(
											bsipb.getStockInCount(),
											Arith.mul(bsipb.getPrice3(),
													Arith.add(1, taxPoint))));
							totalMoney = Arith.round(totalMoney, 2);
						}
					}
					// 入库单后，为财务表添加数据
					FinanceBuyPayBean fbp = createFinanceBuyPayBeanInfo(
							buyOrder, stockin, totalMoney, operationTime);
					if (!fService.addFinanceBuyPayBean(fbp)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("添加财务数据，数据库操作失败！");
						return null;
					}// 财务表添加
						// ---------------------------------
					BuyAdminHistoryBean log1 = createBuyAdminHistoryBeanInfo(
							user, stockin, bs, 3, product);
					if (!service.addBuyAdminHistory(log1)) {
						out.write("添加日志时，数据库操作失败！");
						return null;
					}
					String set = null;
					ProductStockBean ps = null;
					float _price3 = bsip.getPrice3();
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
					ps = psService.getProductStock("id=" + bsip.getStockInId());
					float price5 = 0f;
					float price3 = 0f;
					price3 = bsip.getPrice3();
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
					try {
						service.getDbOp().executeUpdate(
								"update product set price5=" + price5
										+ " where id = " + product.getId());
					} catch (Exception e) {
						e.printStackTrace();
						wareService.getDbOp().rollbackTransaction();
						out.write("更新商品状态失败!");
						return null;
					}

					// 财务数据填充：finance_product表--------liuruilan---------
					FinanceProductBean fProduct = fService
							.getFinanceProductBean("product_id = "
									+ product.getId());
					if (fProduct == null) {
						wareService.getDbOp().rollbackTransaction();
						out.write("查询异常，请与管理员联系！");
						return null;
					}
					float priceSum = Arith.mul(price5,
							totalCount + bsip.getStockInCount());
					float priceHasticket = 0;
					float priceNoticket = 0;
					float priceSumHasticket = fProduct.getPriceSumHasticket();
					float priceSumNoticket = fProduct.getPriceSumNoticket();
					int ticket = FinanceSellProductBean.queryTicket(
							service.getDbOp(), bs.getCode()); // 是否含票
					if (ticket == -1) {
						wareService.getDbOp().rollbackTransaction();
						out.write("查询异常，请与管理员联系！");
						return null;
					}
					int _count = FinanceProductBean.queryCountIfTicket(
							service.getDbOp(), bsip.getProductId(), ticket);
					set = "price =" + price5 + ", price_sum =" + priceSum;
					if (ticket == 0) { // 0-有票
						_price3 = (float) Arith.div(
								Arith.mul(_price3, Arith.add(1, taxPoint)),
								1.17);
						// 计算公式：(fProduct.getPriceSumHasticket() + (_price3 *
						// _count)) / (totalCount + _count)
						priceHasticket = Arith.round(Arith.div(Arith.add(
								fProduct.getPriceSumHasticket(),
								Arith.mul(_price3, bsip.getStockInCount())),
								Arith.add(_count, bsip.getStockInCount())), 2);
						priceSumHasticket = Arith.mul(priceHasticket,
								bsip.getStockInCount() + _count);
						set += ", price_hasticket =" + priceHasticket
								+ ", price_sum_hasticket =" + priceSumHasticket;
					}
					if (ticket == 1) { // 1-无票
						_price3 = (float) Arith.mul(_price3,
								Arith.add(1, taxPoint));
						priceNoticket = Arith.round(Arith.div(
								Arith.add(
										fProduct.getPriceSumNoticket(),
										Arith.mul(_price3,
												bsip.getStockInCount())),
								Arith.add(_count, bsip.getStockInCount())), 2);
						priceSumNoticket = Arith.mul(priceNoticket,
								bsip.getStockInCount() + _count);
						set += ", price_noticket =" + priceNoticket
								+ ", price_sum_noticket =" + priceSumNoticket;
					}
					if (!fService.updateFinanceProductBean(set, "product_id = "
							+ product.getId())) {
						wareService.getDbOp().rollbackTransaction();
						out.write("修改操作数据时，数据库操作失败！");
						return null;
					}
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
							out.write("目的待验库缓存区货位未设置，请先添加后再完成入库！");
							return null;
						}
						cps = new CargoProductStockBean();
						cps.setCargoId(cps.getCargoId());
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
					String area = "";
					if (stockin.getStockArea() == ProductStockBean.AREA_BJ) {
						area = "bj";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_GF) {
						area = "gd";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_ZC) {// 增城用北京的字段
						area = "bj";
					} else if (stockin.getStockArea() == ProductStockBean.AREA_WX) {
						area = "gd";
					}
					set = "stockin_count_" + area + "=(stockin_count_" + area
							+ " + " + bsip.getStockInCount() + "),"
							+ "stockin_total_price =(stockin_total_price+"
							+ bsip.getStockInCount() * bsip.getPrice3() + ")";
					String condition2 = "buy_order_id=" + bs.getBuyOrderId()
							+ " and product_id=" + product.getId();
					if (!service.updateBuyOrderProduct(set, condition2)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("修改采购订单数据时，数据库操作失败！");
						return null;
					}

					// 添加批次记录
					// 添加批次 成为如果有对应的预计单和对应的产品id的条目 就更新 而不是插入新的一条记录
					StockBatchBean batch = null;
					batch = service.getStockBatch("code='" + bs.getCode()
							+ "' and product_id = " + bsip.getProductId()
							+ " and stock_type = "
							+ ProductStockBean.STOCKTYPE_CHECK
							+ " and stock_area = " + stockin.getStockArea());
					if (batch == null) {
						batch = createStockBatchBeanInfo(bs, stockin, bsip, ps,
								ticket, operationTime);
						service.addStockBatch(batch);
					} else {
						if (!service.updateStockBatch(
								"batch_count = "
										+ (batch.getBatchCount() + bsip
												.getStockInCount()),
								"code='" + bs.getCode() + "' and product_id = "
										+ bsip.getProductId()
										+ " and stock_type = "
										+ ProductStockBean.STOCKTYPE_CHECK
										+ " and stock_area = "
										+ stockin.getStockArea())) {
							wareService.getDbOp().rollbackTransaction();
							out.write("修改批次 信息时， 数据库操作失败！");
							return null;
						}
					}

					// 添加批次操作记录
					StockBatchLogBean batchLog = createStockBatchLogBeanInfo(
							batch, user, bsip, operationTime);
					if (!service.addStockBatchLog(batchLog)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("添加批次信息时，数据库操作失败！");
						return null;
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
							out.write("添加供货商信息时，数据库操作失败！");
							return null;
						}
					}

					if (!service.addBuyStockinProduct(bsip)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("添加添加采购入库单商品时 数据库操作失败！");
						return null;
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
							* batchLog.getBatchPrice();

					// 入库卡片
					StockCardBean sc = createStockCardBeanInfo(product,
							stockin, bsip, totalPrice, price5, operationTime);
					if (!psService.addStockCard(sc)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("添加入库卡片时， 数据库操作失败！");
						return null;
					}

					// 财务进销存卡片---liuruilan-----
					FinanceStockCardBean fsc = createFinanceStockCardBeanInfo(
							service, ticket, sc, price5, bs, bsip, totalPrice,
							priceSumHasticket, priceSumNoticket,
							priceHasticket, priceNoticket, _count,
							operationTime);
					if (!fService.addFinanceStockCardBean(fsc)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("添加财务卡片时， 数据库操作失败！");
						return null;
					}

					// 货位入库卡片
					CargoStockCardBean csc = createCargoStockCardBeanInfo(
							stockin, bsip, product, price5, totalPrice, sc,
							cps, operationTime);
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
					String code = createZXCode();
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
						out.write("创建装箱单失败!");
						return null;
					}

					bean = cService.getCartonningInfo("code='" + bean.getCode()
							+ "'");
					productBean.setCartonningId(bean.getId());
					cService.addCartonningProductInfo(productBean);

					// 采购入库明细
					CheckStockinMissionDetailBean csmdBean = createCheckStockinMissionDetailBeanInfo(
							stockin, product, missionId, bsip, bean,
							operationTime);

					siList.add(stockin.getCode() + "-" + code);

					if (!statService.addCheckStockinMissionDetail(csmdBean)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("添加入库明细失败！");
						return null;
					}
					// 更新复录状态及余量信息
					if (!statService
							.updateCheckStockinMissionBatch(
									"second_check_count=0,first_check_count=0,current_check_count=current_check_count+"
											+ count
											+ ",second_check_status=0,first_check_status=0,left_count=0",
									"id=" + batchId)) {
						wareService.getDbOp().rollbackTransaction();
						out.write("更新复录状态及余量信息时出错，请联系管理员!");
						return null;
					}
					wareService.getDbOp().commitTransaction();
				}

				bean1 = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				if (siList.size() > 0) {
					for (int s = 0; s < siList.size(); s++) {
						codelist += siList.get(s) + ",";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
		} finally {
			wareService.getDbOp().release();
		}
		out.write("入库成功!," + bean1.getLeftCount() + "," + codelist);
		return null;
	}

	*//**
	 * 取累计录入数与定购量进行比对
	 * 
	 * @param request
	 * @param response
	 * @return
	 *//*
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

	*//**
	 * 
	 * 打印装箱记录
	 *//*
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
		synchronized (checkStockinLock) {
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
				request.setAttribute("missionId",
						request.getParameter("missionId"));
				request.setAttribute("batchId", request.getParameter("batchId"));
				request.setAttribute("productLine", productLine.getName());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		return "admin/cargo/qualifyCartonningInfoPrint2";
	}

	*//**
	 * 初始化余量
	 * 
	 * @param re
	 * @param res
	 * @param batchId
	 * @return
	 *//*
	@RequestMapping("initLeftCount")
	@ResponseBody
	public String initLeftCount(HttpServletRequest re, HttpServletResponse res,
			@RequestParam("batchId") int batchId) {
		WareService pService = new WareService();
		int result = 0;
		synchronized (checkStockinLock) {
			try {
				StatService statService = new StatService(
						IBaseService.CONN_IN_SERVICE, pService.getDbOp());
				CheckStockinMissionBatchBean batchBean = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				if (batchBean == null)
					return "-1";
				else
					return "" + batchBean.getLeftCount();
			} catch (Exception e) {
				e.printStackTrace();
				return "error";
			} finally {
				pService.releaseAll();
			}
		}
	}

	*//**
	 * 动态检测是初/复录状态
	 * 
	 * @param re
	 * @param res
	 * @param batchId
	 * @param type
	 * @return
	 *//*
	@RequestMapping("checkStatus")
	@ResponseBody
	public String checkStatus(HttpServletRequest re, HttpServletResponse res,
			@RequestParam("batchId") int batchId, @RequestParam("type") int type) {
		WareService pService = new WareService();
		synchronized (checkStockinLock) {
			try {
				StatService statService = new StatService(
						IBaseService.CONN_IN_SERVICE, pService.getDbOp());
				CheckStockinMissionBatchBean batchBean = statService
						.getCheckStockinMissionBatch("id=" + batchId);
				if (type == 2)
					return batchBean.getSecondCheckStatus() + ","
							+ batchBean.getSecondCheckCount();
				else
					return batchBean.getFirstCheckStatus() + ","
							+ batchBean.getFirstCheckCount();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				pService.releaseAll();
			}
		}
		return "";
	}

	*//**
	 * 
	 * 打印装箱记录
	 *//*
	@RequestMapping("printCartonningInfo1")
	public String PrintCartonningInfo(HttpServletRequest request,
			HttpServletResponse response, @RequestParam("code") String code)
			throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		WareService pService = new WareService();
		CartonningInfoService service = new CartonningInfoService(
				IBaseService.CONN_IN_SERVICE, pService.getDbOp());
		synchronized (checkStockinLock) {
			try {
				List<CartonningInfoBean> list = new ArrayList<CartonningInfoBean>();
				String[] codes = code.split(",");
				if (codes.length == 0) {
					request.setAttribute("tip", "装箱单编号不能为空！");
					request.setAttribute("result", "failure");
					return "admin/error";
				}
				for (int i = 0; i < codes.length; i++) {
					CartonningInfoBean bean = service
							.getCartonningInfo("code='" + codes[i] + "'");
					if (bean == null) {
						request.setAttribute("tip", "没有关联上装箱单！");
						request.setAttribute("result", "failure");
						return "admin/error";
					}
					CartonningProductInfoBean pBean = service
							.getCartonningProductInfo("cartonning_id="
									+ bean.getId());
					service.updateCartonningInfo("status=1", "code='" + code
							+ "'");
					bean.setProductBean(pBean);
					voProduct product = pService.getProduct(pBean
							.getProductId());
					voProductLine productLine = pService
							.getProductLine("product_line_catalog.catalog_id="
									+ product.getParentId1()
									+ " or product_line_catalog.catalog_id="
									+ product.getParentId2());
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
		}
		return "admin/cargo/qualifyCartonningInfoPrint2";
	}

	*//**
	 * 自动完成订单
	 * 
	 * @param request
	 * @param response
	 * @param model
	 * @param buyStockinCode
	 * @return
	 *//*
	@RequestMapping("/autocompleteorder")
	@ResponseBody
	public String autoCompleteOrder(HttpServletRequest request,
			HttpServletResponse response, ModelMap model,
			@RequestParam("buyStockinCode") String buyStockinCode) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "admin/error";
		}
		WareService service = new WareService();
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,
				service.getDbOp());
		IStockService service1 = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, service.getDbOp());
		synchronized (checkStockinLock) {
			try {
				BuyStockBean bs = statService
						.getBuyStockBeanByCode(buyStockinCode);
				if (bs == null) {
					return "-1";
				}
				BuyOrderBean buyOrder = service1
						.getBuyOrder("id=(select buy_order_id from buy_stock where id="
								+ bs.getId() + ")");
				if (!service1.completeBuyOrder(user, bs, buyOrder, true)) {
					return "0";
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}

		return "1";
	}*/
	

}
