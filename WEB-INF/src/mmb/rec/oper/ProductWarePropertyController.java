package mmb.rec.oper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.CheckEffectBean;
import mmb.stock.stat.CheckStaffBean;
import mmb.stock.stat.ProductWarePropertyBean;
import mmb.stock.stat.ProductWarePropertyLogBean;
import mmb.stock.stat.ProductWarePropertyService;
import mmb.stock.stat.ProductWareTypeBean;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.MyRuntimeException;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/admin")
public class ProductWarePropertyController {

	private static byte[] lock = {};

	/**
	 * 
	 * 得到质检排班计划列表
	 * 
	 * @return
	 */
	@RequestMapping("getCheckStaffWorkPlanInfo")
	public String getCheckStaffWorkPlanInfo(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String monthDate = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("monthDate")));
		String areaId = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("areaId")));
		if (areaId == null || "".equals(areaId)) {
			List areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			if (areaList != null && areaList.size() > 0) {
				areaId = (String) areaList.get(0);
			} else {
				request.setAttribute("tip", "权限遮罩功能显示您当前没有任何地区的权限！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
		}
		List list = new ArrayList();
		if (monthDate == null || monthDate.equals("")) {
			Date now = new Date();
			monthDate = DateUtil.formatDate(now, "yyyy-MM");
		}
		try {
			int pointCut = monthDate.indexOf("-");
			int year = Integer.parseInt(monthDate.substring(0, pointCut));
			int month = Integer.parseInt(monthDate.substring(pointCut + 1));
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);// Java月份才0开始算
			int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
			for (int i = 1; i < dateOfMonth + 1; i++) {
				String temp = year
						+ "-"
						+ String.format("%02d", new Object[] { new Integer(
								month) })
						+ "-"
						+ String.format("%02d", new Object[] { new Integer(i) });
				list.add(temp);
			}
		} catch (Exception e) {
			request.setAttribute("tip", "所传日期参数有误");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		List dayList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			// 根据list 遍历拿出对应的记录 如果没有就给哥空的
			for (int i = 0; i < list.size(); i++) {
				String temp = (String) list.get(i);
				String time = temp + " 00:00:00";
				CheckStaffBean checkStaffBean = (CheckStaffBean) statService
						.getCheckStaff("date='" + time + "' and area_id="
								+ Integer.parseInt(areaId));
				// 把值赋给 bean
				if (checkStaffBean == null) {
					checkStaffBean = new CheckStaffBean();
					checkStaffBean.setDate(time);
					checkStaffBean.setMonthCount(0);
					checkStaffBean.setDayCount(0);
				}
				checkStaffBean.setNikeName(temp);
				dayList.add(checkStaffBean);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("dateList", dayList);
		return "forward:/admin/rec/oper/checkStaffWorkPlan/checkStaffWorkPlanInfo.jsp?monthDate="
				+ monthDate + "&areaId=" + areaId;
	}

	/**
	 * 
	 * 根据填写的月在编人数修改记录
	 * 
	 * @return
	 */
	@RequestMapping("addMonthStaffCount")
	public String addMonthStaffCount(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		UserGroupBean group = user.getGroup();
		boolean addMonthRight = group.isFlag(691);
		if (!addMonthRight) {
			request.setAttribute("tip", "您没有编辑月在编人数的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String monthDate = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("monthDate2")));
		int monthCount = StringUtil.parstInt(request
				.getParameter("mStaffCount"));
		int areaId = StringUtil.parstInt(request.getParameter("areaIdHidden"));
		List list = new ArrayList();
		if (monthDate == null || monthDate.equals("")) {
			Date now = new Date();
			monthDate = DateUtil.formatDate(now, "yyyy-MM");
		}
		try {
			int pointCut = monthDate.indexOf("-");
			int year = Integer.parseInt(monthDate.substring(0, pointCut));
			int month = Integer.parseInt(monthDate.substring(pointCut + 1));
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);// Java月份才0开始算
			int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
			for (int i = 1; i < dateOfMonth + 1; i++) {
				String temp = year
						+ "-"
						+ String.format("%02d", new Object[] { new Integer(
								month) })
						+ "-"
						+ String.format("%02d", new Object[] { new Integer(i) });
				list.add(temp);
			}
		} catch (Exception e) {
			request.setAttribute("tip", "所传日期参数有误");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String tip = "";
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				// 加上时间验证的 地方 不可以修改 这个月以前的 时间的记录
				if (productWarePropertyService.isChangeAvailForMonth(monthDate)) {
					productWarePropertyService.addCheckStaffMonth(monthCount,
							list, statService, areaId);
					tip = "可以修改的已修改";
				} else {
					tip = "不可以更改过去月份的月在编人数";
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch(MyRuntimeException mre ) {
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		} catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "系统异常!");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", tip);
		request.setAttribute("url", request.getContextPath()+"/admin/getCheckStaffWorkPlanInfo.mmx?monthDate="
				+ monthDate + "&areaId=" + areaId);
		return "/admin/tip";
	}

	/**
	 * 
	 * 根据修改的日在编人数 修改记录
	 * 
	 * @return
	 */
	@RequestMapping("editDayStaffCount")
	public String editDayStaffCount(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		UserGroupBean group = user.getGroup();
		boolean addDayRight = group.isFlag(692);
		if (!addDayRight) {
			request.setAttribute("tip", "您没有编辑日在岗人数的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		String monthDate = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("monthDate3")));
		int areaId = StringUtil.toInt((StringUtil.convertNull(request
				.getParameter("area"))));
		List list = new ArrayList();
		Map map = new HashMap();
		if (monthDate == null || monthDate.equals("")) {
			Date now = new Date();
			monthDate = DateUtil.formatDate(now, "yyyy-MM");
		}
		try {
			int pointCut = monthDate.indexOf("-");
			int year = Integer.parseInt(monthDate.substring(0, pointCut));
			int month = Integer.parseInt(monthDate.substring(pointCut + 1));
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, month - 1);// Java月份才0开始算
			int dateOfMonth = cal.getActualMaximum(Calendar.DATE);
			for (int i = 1; i < dateOfMonth + 1; i++) {
				String temp = year
						+ "-"
						+ String.format("%02d", new Object[] { new Integer(
								month) })
						+ "-"
						+ String.format("%02d", new Object[] { new Integer(i) });
				list.add(temp);
				int tempInt = ProductWarePropertyService.toInt(request
						.getParameter("dStaffCount_" + temp));
				if (tempInt != -1) {
					map.put(temp, new Integer(tempInt));
				}
			}
		} catch (Exception e) {
			request.setAttribute("tip", "所传日期参数有误");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String tip = "";
		boolean change = false;
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				int x = list.size();
				for (int i = 0; i < x; i++) {
					String temp = (String) list.get(i);
					String time = temp + " 00:00:00";
					if (map.containsKey(temp)) {
						int dayCount = ((Integer) map.get(temp)).intValue();
						if (productWarePropertyService
								.isChangeAvailForDay(temp)) {
							if (!productWarePropertyService.addCheckStaffDay(
									dayCount, temp, areaId, statService)) {
								tip += temp + "数据库操作出错";
							} else {
								change = true;
							}
						} else {
							tip += temp + "已过期不可修改,";
						}
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
				if (change) {
					if (tip.equals("")) {
						tip = "修改成功!";
					} else {
						tip += "其余修改已成功！";
					}
				} else {
					if (tip.equals("")) {
						tip = "未修改数值";
					}
				}
			}
		} catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", e.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", tip);
		request.setAttribute("url", request.getContextPath() + "/admin/getCheckStaffWorkPlanInfo.mmx?monthDate="
				+ monthDate + "&areaId=" + areaId);
		return "/admin/tip";
	}

	/**
	 * 
	 * 查询商品质检分类效率
	 * 
	 * @return
	 */
	@RequestMapping("getCheckEffectInfo")
	@ResponseBody
	public Map<String, Object> getCheckEffectInfo(HttpServletRequest request,
			HttpServletResponse response) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("total", "0");
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		resultMap.put("rows", resultList);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			resultMap.put("tip", "当前没有登陆，操作失败！");
			return resultMap;
		}

		int pageIndex = StringUtil.parstInt(request.getParameter("page"));
		pageIndex = pageIndex - 1;
		int countPerPage = StringUtil.parstInt(request.getParameter("rows"));
		List checkEffectList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		// ProductWarePropertyService productWarePropertyService = new
		// ProductWarePropertyService(IBaseService.CONN_IN_SERVICE,
		// wareService.getDbOp());
		try {
			int totalCount = statService.getCheckEffectCount("id<>0");
			checkEffectList = statService.getCheckEffectList("id<>0",
					pageIndex * countPerPage, countPerPage,
					"id asc");
			int x = checkEffectList.size();
			for (int i = 0; i < x; i++) {
				CheckEffectBean cfBean = (CheckEffectBean) checkEffectList
						.get(i);
				List productWarePropertyList = statService
						.getProductWarePropertyList("check_effect_id = "
								+ cfBean.getId(), -1, -1, "id asc");
				String productLines = "";
				Map tempMap = new HashMap();
				int y = productWarePropertyList.size();
				for (int j = 0; j < y; j++) {
					ProductWarePropertyBean pwpBean = (ProductWarePropertyBean) productWarePropertyList
							.get(j);
					voProduct product = wareService.getProduct(pwpBean
							.getProductId());
					// 确定产品线
					String productLine = "";// 产品线
					String productLineNameCondition = "  (product_line_catalog.catalog_id = ("
							+ product.getParentId1()
							+ ") and product_line_catalog.catalog_type = 1) or (product_line_catalog.catalog_type = 2 and product_line_catalog.catalog_id "
							+ " = (" + product.getParentId2() + "))";
					voProductLine vpl = wareService
							.getProductLine(productLineNameCondition);
					if (vpl != null) {
						productLine = vpl.getName();
					}
					if (!productLine.equals("")
							&& !tempMap.containsKey(productLine)) {
						productLines += productLine + ",";
						tempMap.put(productLine, "");
					}

				}
				if (productLines.length() > 0) {
					productLines = productLines.substring(0,
							(productLines.length() - 1));
				}
				cfBean.setProductLines(productLines);
			}

			request.setAttribute("list", checkEffectList);
			for (int i = 0; i < checkEffectList.size(); i++) {
				CheckEffectBean ceBean = (CheckEffectBean) checkEffectList
						.get(i);
				Map<String, String> map = new HashMap<String, String>();
				map.put("check_effect_name", ceBean.getName());
				map.put("product_lines", ceBean.getProductLines());
				map.put("check_effect_effect",
						new Integer(ceBean.getEffect()).toString());
				String url = "<a href='" + request.getContextPath()
						+ "/admin/rec/oper/checkEffect/editCheckEffect.jsp?id="
						+ ceBean.getId() + "&name=" + ceBean.getName()
						+ "&effect=" + ceBean.getEffect()
						+ "'>编辑</a>&nbsp;|&nbsp;";
				url += "<a href='javascript:deleteColum( " + ceBean.getId()
						+ ", \"" + ceBean.getName() + "\");' >删除</a>";
				map.put("management", url);
				map.put("check_effect_id", new Integer(ceBean.getId()).toString());
				resultList.add(map);
			}
			resultMap.put("total", totalCount);
			resultMap.put("rows", resultList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return resultMap;
	}

	/**
	 * 
	 * 查询商品质检分类效率
	 * 
	 * @return
	 */
	@RequestMapping("toCheckEffectInfo")
	public String toCheckEffectInfo(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		return "forward:/admin/rec/oper/checkEffect/checkEffectInfo.jsp";
	}

	/**
	 * 
	 * 添加商品质检分类效率
	 * 
	 * @return
	 */
	@RequestMapping("addCheckEffect")
	public void addCheckEffect(HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		String name = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("name")));
		int effect = StringUtil.parstInt(request.getParameter("effect"));
		try {
			if (user == null) {
				response.getWriter().write("当前没有登录，操作失败！");
				return;
			}

			UserGroupBean group = user.getGroup();
			boolean addCheckEffectRight = group.isFlag(693);
			if (!addCheckEffectRight) {
				response.getWriter().write("您没有添加商品质检分类与效率的权限！");
				return;
			}

			name = name.trim();
			if (name.equals("")) {
				response.getWriter().write("请填写分类效率名称！");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				CheckEffectBean cfb = statService.getCheckEffect("name='"
						+ name + "'");
				if (cfb != null) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("已经有以这个名字命名的质检分类了");
					return;
				} else {
					cfb = new CheckEffectBean();
					cfb.setName(name);
					cfb.setEffect(effect);
					if (!statService.addCheckEffect(cfb)) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("操作数据库失败");
						return;
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
			response.getWriter().write("添加成功！");
		} catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return;
	}

	/**
	 * 
	 * 编辑商品质检分类效率
	 * 
	 * @return
	 */
	@RequestMapping("editCheckEffect")
	public void editCheckEffect(HttpServletRequest request,
			HttpServletResponse response) {
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		voUser user = (voUser) request.getSession().getAttribute("userView");
		try {
			if (user == null) {
				response.getWriter().write("当前没有登录，操作失败！");
				return;
			}

			UserGroupBean group = user.getGroup();
			boolean editCheckEffectRight = group.isFlag(694);
			if (!editCheckEffectRight) {
				response.getWriter().write("您没有编辑商品质检分类与效率的权限！");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String name = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("name")));
		int effect = StringUtil.parstInt(request.getParameter("effect"));
		int id = StringUtil.parstInt(request.getParameter("id"));
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				CheckEffectBean cfb = statService.getCheckEffect("id=" + id);
				if (cfb == null) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("没有找到这个要编辑的质检分类条目!");
					return;
				}
				if (cfb.getName().equals(name) && cfb.getEffect() == effect) {
					statService.getDbOp().rollbackTransaction();
					response.getWriter().write("并没有做修改!");
					return;
				}
				if (cfb.getName().equals(name)) {
					if (!statService.updateCheckEffect("effect=" + effect,
							"id=" + id)) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("操作数据库失败!");
						return;
					}
				} else {
					CheckEffectBean cfb2 = statService.getCheckEffect("name='"
							+ name + "'");
					if (cfb2 != null) {
						statService.getDbOp().rollbackTransaction();
						response.getWriter().write("已经有以这个名字命名的质检分类了!");
						return;
					} else {
						if (!statService.updateCheckEffect("name='" + name
								+ "'," + "effect=" + effect, "id=" + id)) {
							statService.getDbOp().rollbackTransaction();
							response.getWriter().write("操作数据库失败!");
							return;
						}
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
			response.getWriter().write("修改成功!");
		} catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return;
	}

	/**
	 * 
	 * 删除商品质检分类效率
	 * 
	 * @return
	 */
	@RequestMapping("deleteCheckEffect")
	public String deleteCheckEffect(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		int id = StringUtil.parstInt(request.getParameter("id"));
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				CheckEffectBean cfb = statService.getCheckEffect("id=" + id);
				if (cfb == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "没有找到这个要删除的质检分类条目");
					request.setAttribute("result", "failure");
					return "/admin/error";
				} else {
					// ******************注意加上对于 商品物流属性的 相关的验证，如果 有关联 是让还是不让编辑
					if (statService
							.getProductWarePropertyCount("check_effect_id = "
									+ id) > 0) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "该质检效率已经有商品物流属性关联了，不可以删除！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
					if (!statService.deleteCheckEffect("id=" + id)) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "在删除质检效率时，操作数据库失败！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);

			}
		} catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "删除成功");
		request.setAttribute("url", "toCheckEffectInfo.mmx");
		return "/admin/tip";
	}

	/**
	 * 
	 * 进入添加商品物流属性页面前要准备的数据
	 * 
	 * @return
	 */
	@RequestMapping("toAddProductWareProperty")
	public String toAddProductWareProperty(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean addProductWarePropertyRight = group.isFlag(696);
		if (!addProductWarePropertyRight) {
			request.setAttribute("tip", "您没有添加商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		List checkEffectList = new ArrayList();
		List productWareTypeList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {

			checkEffectList = statService.getCheckEffectList("id<>0", -1, -1,
					"id asc");
			productWareTypeList = productWarePropertyService
					.getProductWareTypeList("id<>0", -1, -1, "id asc");
			/*
			 * productWareTypeList =
			 * statService.getUserOrderPackageTypeList("id<>0", -1, -1,
			 * "type_id asc"); productWareTypeList =
			 * productWarePropertyService.removeDuplicateInList
			 * (productWareTypeList);
			 */

			request.setAttribute("productWareTypeList", productWareTypeList);
			request.setAttribute("checkEffectList", checkEffectList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return "forward:/admin/rec/oper/productWareProperty/addProductWareProperty.jsp";
	}

	/**
	 * 验证商品信息
	 * 
	 */
	@RequestMapping("getProductInfoByCode")
	public void getProductInfoByCode(HttpServletRequest request,
			HttpServletResponse response) {

		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		voProduct product = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			if (!productCode.equals("")) {
				product = wareService.getProduct(productCode);
				if (product == null) {
					response.getWriter().write(
							"{status:'fail', tip:'没有找到对应该编号的商品!'}");
				} else {
					List productWareList = statService
							.getProductWarePropertyList("product_id = "
									+ product.getId(), -1, -1, "id asc");
					if (productWareList.size() == 0) {

						ProductBarcodeVO pbcb = bService
								.getProductBarcode("product_id = "
										+ product.getId());
						if (pbcb == null) {
							response.getWriter().write(
									"{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success'";
							// 条码
							ProductBarcodeVO bBean = bService
									.getProductBarcode("product_id="
											+ product.getId());
							if (bBean == null) {
								js += ",productBarCode:''";
							} else {
								js += ",productBarCode:'" + bBean.getBarcode()
										+ "'";
							}
							// 物流分类

							js += ",wareType:'-1'";

							// 标准装箱量
							/*
							 * CartonningStandardCountBean cscount =
							 * cartonningInfoservice
							 * .getCartonningStandardCount("product_id="
							 * +product.getId()); if(cscount != null){ js +=
							 * ",standardCount:'" + cscount.getStandard() + "'";
							 * }else{ js += ",standardCount:'0'"; }
							 */

							js += "}";
							response.getWriter().write(js);
						}

					} else {
						response.getWriter().write(
								"{status:'fail', tip:'该商品已经有商品物流属性了，不要重复添加！'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品编号!'}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}

	/**
	 * 验证商品信息
	 * 
	 */
	@RequestMapping("getProductInfoByBarCode")
	public void getProductInfoByBarCode(HttpServletRequest request,
			HttpServletResponse response) {

		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productBarCode")));
		voProduct product = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			if (!productBarCode.equals("")) {
				ProductBarcodeVO bBean = bService.getProductBarcode("barcode="
						+ "'" + StringUtil.toSql(productBarCode) + "'");

				if (bBean == null) {
					response.getWriter().write(
							"{status:'fail', tip:'没有找到对应该条码的商品!'}");
				} else {
					product = wareService.getProduct(bBean.getProductId());
					List productWareList = statService
							.getProductWarePropertyList("product_id = "
									+ product.getId(), -1, -1, "id asc");
					if (productWareList.size() == 0) {
						ProductBarcodeVO pbcb = bService
								.getProductBarcode("product_id = "
										+ product.getId());
						if (pbcb == null) {
							response.getWriter().write(
									"{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success',productCode:'"
									+ product.getCode() + "'";
							// 物流分类
							js += ",wareType:'-1'";

							// 标准装箱量
							/*
							 * CartonningStandardCountBean cscount =
							 * cartonningInfoservice
							 * .getCartonningStandardCount("product_id="
							 * +product.getId()); if(cscount != null){ js +=
							 * ",standardCount:'" + cscount.getStandard() + "'";
							 * }else{ js += ",standardCount:'0'"; }
							 */

							js += "}";
							response.getWriter().write(js);
						}

					} else {
						response.getWriter().write(
								"{status:'fail', tip:'该商品已经有商品物流属性了，不要重复添加!'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品条码!'}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}

	/**
	 * 验证商品信息
	 * 
	 */
	@RequestMapping("getProductInfoByCode2")
	public void getProductInfoByCode2(HttpServletRequest request,
			HttpServletResponse response) {

		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		voProduct product = null;
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			if (!productCode.equals("")) {
				product = wareService.getProduct(productCode);
				if (product == null) {
					response.getWriter().write(
							"{status:'fail', tip:'没有找到对应该编号的商品!'}");
				} else {
					List productWareList = statService
							.getProductWarePropertyList("product_id = "
									+ product.getId(), -1, -1, "id asc");
					if (productWareList.size() == 1) {

						ProductBarcodeVO pbcb = bService
								.getProductBarcode("product_id = "
										+ product.getId());
						if (pbcb == null) {
							response.getWriter().write(
									"{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success'";
							// 条码
							ProductBarcodeVO bBean = bService
									.getProductBarcode("product_id="
											+ product.getId());
							if (bBean == null) {
								js += ",productBarCode:''";
							} else {
								js += ",productBarCode:'" + bBean.getBarcode()
										+ "'";
							}
							// 物流分类
							js += ",wareType:'-1'";

							// 标准装箱量
							/*
							 * CartonningStandardCountBean cscount =
							 * cartonningInfoservice
							 * .getCartonningStandardCount("product_id="
							 * +product.getId()); if(cscount != null){ js +=
							 * ",standardCount:'" + cscount.getStandard() + "'";
							 * }else{ js += ",standardCount:'0'"; }
							 */

							js += "}";
							response.getWriter().write(js);
						}

					} else {
						response.getWriter().write(
								"{status:'fail', tip:'自动获取数据出错！'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品编号!'}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}

	/**
	 * 验证商品信息
	 * 
	 */
	@RequestMapping("getProductInfoByBarCode2")
	public void getProductInfoByBarCode2(HttpServletRequest request,
			HttpServletResponse response) {

		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productBarCode")));
		voProduct product = null;
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			if (!productBarCode.equals("")) {
				ProductBarcodeVO bBean = bService.getProductBarcode("barcode="
						+ "'" + StringUtil.toSql(productBarCode) + "'");

				if (bBean == null) {
					response.getWriter().write(
							"{status:'fail', tip:'没有找到对应该条码的商品!'}");
				} else {
					product = wareService.getProduct(bBean.getProductId());
					List productWareList = statService
							.getProductWarePropertyList("product_id = "
									+ product.getId(), -1, -1, "id asc");
					if (productWareList.size() == 1) {
						ProductBarcodeVO pbcb = bService
								.getProductBarcode("product_id = "
										+ product.getId());
						if (pbcb == null) {
							response.getWriter().write(
									"{status:'fail', tip:'没有找到对应的商品条码!'}");
						} else {
							String js = "{status:'success',productCode:'"
									+ product.getCode() + "'";
							// 物流分类
							js += ",wareType:'-1'";

							// 标准装箱量
							/*
							 * CartonningStandardCountBean cscount =
							 * cartonningInfoservice
							 * .getCartonningStandardCount("product_id="
							 * +product.getId()); if(cscount != null){ js +=
							 * ",standardCount:'" + cscount.getStandard() + "'";
							 * }else{ js += ",standardCount:'0'"; }
							 */

							js += "}";
							response.getWriter().write(js);
						}

					} else {
						response.getWriter().write(
								"{status:'fail', tip:'自动获取数据出错！'}");
					}
				}
			} else {
				response.getWriter().write("{status:'fail', tip:'未传入产品条码!'}");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
	}

	/**
	 * 
	 * 添加商品物流属性
	 * 
	 * @return
	 */
	@RequestMapping("addProductWareProperty")
	public String addProductWareProperty(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean addProductWarePropertyRight = group.isFlag(696);
		if (!addProductWarePropertyRight) {
			request.setAttribute("tip", "您没有添加商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productBarCode")));
		int checkEffectId = StringUtil.parstInt(request
				.getParameter("checkEffect"));
		int length = StringUtil.parstInt(request.getParameter("length"));
		int width = StringUtil.parstInt(request.getParameter("width"));
		int height = StringUtil.parstInt(request.getParameter("height"));
		// int standardCount =
		// StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));
		int weight = StringUtil.parstInt(request.getParameter("weight"));
		String identityInfo = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("identityInfo")));
		int binning = StringUtil.parstInt(request.getParameter("binning"));// 标准装箱量
		voProduct product = null;
		ProductBarcodeVO bBean = null;
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();

				if (productCode.equals("") && productBarCode.equals("")) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "请至少输入商品编号和商品条码中的一个！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				} else if (!productCode.equals("") && productBarCode.equals("")) {
					product = wareService.getProduct(productCode);
					if (product == null) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应编号的商品！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
				} else if (productCode.equals("") && !productBarCode.equals("")) {
					bBean = bService.getProductBarcode("barcode=" + "'"
							+ productBarCode + "'");
					if (bBean == null) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应的条码记录！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					} else {
						product = wareService.getProduct(bBean.getProductId());
						if (product == null) {
							request.setAttribute("tip", "没有找到对应的条码的商品！");
							request.setAttribute("result", "failure");
							return "/admin/error";
						}
					}
				} else {
					// 两个都填了
					// 先要验证产品，productCode productBarCode 是否存在 是否对应于一个
					product = productWarePropertyService
							.isCodeAndBarCodeMatched(productCode,
									productBarCode, bService, statService,
									wareService);
				}

				// 要验证是否已经存在该商品的 商品物流属性
				ProductWarePropertyBean pwpBean = statService
						.getProductWareProperty("product_id = "
								+ product.getId());
				if (pwpBean != null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "该商品已经有商品物流属性了！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				// 然后验证 质检分类是否存在
				CheckEffectBean cfBean = statService.getCheckEffect("id = "
						+ checkEffectId);
				if (cfBean == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的质检分类！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				// 对应的 物流分类是否存在
				ProductWareTypeBean pwtBean = productWarePropertyService
						.getProductWareType("id=" + wareType);
				if (pwtBean == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的商品物流分类！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}

				/*
				 * UserOrderPackageTypeBean uoptBean2 =
				 * userOrderService.getUserOrderPackageType("type_id=" +
				 * wareType + " and product_catalog=" + product.getParentId1());
				 * if( uoptBean2 == null ) {
				 * statService.getDbOp().rollbackTransaction();
				 * request.setAttribute("tip", "商品不属于所选的物流分类！ ");
				 * request.setAttribute("result", "failure"); return
				 * mapping.findForward(IConstants.FAILURE_KEY); }
				 */
				// 寻找或修改装箱数量
				// productWarePropertyService.manageCartonningStandardCount(user,product,standardCount,
				// cartonningInfoService);

				// 首先要在 数据库 product_ware_property 之中 添加一条记录
				// productWarePropertyService.saveProductWareProperty(product,user,cfBean,pwtBean,length,width,height,weight,statService);
				productWarePropertyService.saveProductWareProperty(product,
						user, cfBean, pwtBean, length, width, height, weight,
						identityInfo, statService, binning);

				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		}  catch(MyRuntimeException mre ) {
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		}  catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "系统异常!");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "添加成功!");
		request.setAttribute("url", request.getContextPath()
				+ "/admin/toAddProductWareProperty.mmx");
		return "/admin/tip";
	}

	/**
	 * 
	 * 查询商品物流属性
	 * 
	 * @return
	 */
	@RequestMapping("toProductWarePropertyInfo")
	public String toProductWarePropertyInfo(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		UserGroupBean group = user.getGroup();
		boolean seeProductWarePropertyRight = group.isFlag(695);
		if (!seeProductWarePropertyRight) {
			request.setAttribute("tip", "您没有查看商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		List productWarePropertyList = new ArrayList();
		List checkEffectList = new ArrayList();
		List productWareTypeList = new ArrayList();
		StringBuilder condition = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			checkEffectList = statService.getCheckEffectList("id<>0", -1, -1,
					"id asc");
			productWareTypeList = productWarePropertyService
					.getProductWareTypeList("id<>0", -1, -1, "id asc");
			/*
			 * productWareTypeList =
			 * statService.getUserOrderPackageTypeList("id<>0", -1, -1,
			 * "type_id asc"); productWareTypeList =
			 * productWarePropertyService.removeDuplicateInList
			 * (productWareTypeList);
			 */

			request.setAttribute("productWareTypeList", productWareTypeList);
			request.setAttribute("checkEffectList", checkEffectList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return "forward:/admin/rec/oper/productWareProperty/productWarePropertyInfo.jsp";
	}

	/**
	 * 
	 * 查询商品物流属性
	 * 
	 * @return
	 */
	@RequestMapping("getProductWarePropertyInfo")
	@ResponseBody
	public Map<String, Object> getProductWarePropertyInfo(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			resultMap.put("tip", "当前没有登录，操作失败！");
			return resultMap;
		}

		UserGroupBean group = user.getGroup();
		boolean seeProductWarePropertyRight = group.isFlag(695);
		if (!seeProductWarePropertyRight) {
			resultMap.put("tip", "您没有查看商品物流属性的权限！");
			return resultMap;
		}

		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productBarCode")));
		int checkEffectId = StringUtil.parstInt(request
				.getParameter("checkEffect"));
		// int standardCount =
		// StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));

		int pageIndex = StringUtil.parstInt(request.getParameter("page"));
		pageIndex = pageIndex - 1;
		int countPerPage = StringUtil.parstInt(request.getParameter("rows"));
		List productWarePropertyList = new ArrayList();
		StringBuilder condition = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			if (!productCode.equals("")) {
				params.append("&");
				params.append("productCode=" + productCode);

				voProduct product = wareService.getProduct(productCode);
				if (product == null) {
					if (condition.length() > 0) {
						condition.append(" and");
						condition.append(" id = -1");
					} else {
						condition.append(" id = -1");
					}
				} else {
					if (condition.length() > 0) {
						condition.append(" and");
						condition.append(" product_id = " + product.getId());
					} else {
						condition.append(" product_id = " + product.getId());
					}
				}
			}

			if (!productBarCode.equals("")) {
				params.append("&");
				params.append("productBarCode=" + productBarCode);

				ProductBarcodeVO bBean = bService.getProductBarcode("barcode="
						+ "'" + StringUtil.toSql(productBarCode) + "'");
				if (bBean == null) {
					if (condition.length() > 0) {
						condition.append(" and");
						condition.append(" id = -1");
					} else {
						condition.append(" id = -1");
					}
				} else {
					voProduct product = wareService.getProduct(bBean
							.getProductId());
					if (product == null) {
						if (condition.length() > 0) {
							condition.append(" and");
							condition.append(" id = -1");
						} else {
							condition.append(" id = -1");
						}
					} else {
						if (condition.length() > 0) {
							condition.append(" and");
							condition
									.append(" product_id = " + product.getId());
						} else {
							condition
									.append(" product_id = " + product.getId());
						}
					}
				}
			}

			if (checkEffectId != 0 && checkEffectId != -1) {
				params.append("&");
				params.append("checkEffect=" + checkEffectId);

				if (condition.length() > 0) {
					condition.append(" and");
					condition.append(" check_effect_id = " + checkEffectId);
				} else {
					condition.append(" check_effect_id = " + checkEffectId);
				}

			}

			if (wareType != 0 && wareType != -1) {
				params.append("&");
				params.append("wareType=" + wareType);

				if (condition.length() > 0) {
					condition.append(" and");
					condition.append(" product_type_id = " + wareType);
				} else {
					condition.append(" product_type_id = " + wareType);
				}

			}
			String conditionS = "id<>0";
			if (condition.length() > 0) {
				conditionS = condition.toString();
			}

			int totalCount = statService
					.getProductWarePropertyCount(conditionS);
			productWarePropertyList = statService.getProductWarePropertyList(
					conditionS, pageIndex * countPerPage,
					countPerPage, "id asc");
			int x = productWarePropertyList.size();
			for (int i = 0; i < x; i++) {
				ProductWarePropertyBean pwpBean = (ProductWarePropertyBean) productWarePropertyList
						.get(i);
				voProduct temp = wareService.getProduct(pwpBean.getProductId());
				if (temp == null) {
					temp = new voProduct();
				}
				pwpBean.setProduct(temp);
				CheckEffectBean cfBean = statService.getCheckEffect("id="
						+ pwpBean.getCheckEffectId());
				if (cfBean == null) {
					cfBean = new CheckEffectBean();
				}
				pwpBean.setCheckeEffect(cfBean);
				ProductWareTypeBean pwtBean = productWarePropertyService
						.getProductWareType("id=" + pwpBean.getProductTypeId());
				if (pwtBean == null) {
					pwtBean = new ProductWareTypeBean();
					pwtBean.setName("");
				}
				pwpBean.setProductWareType(pwtBean);
				Map<String, String> map = new HashMap<String, String>();
				map.put("product_code", pwpBean.getProduct().getCode());
				map.put("product_name", pwpBean.getProduct().getOriname());
				map.put("check_effect_name", pwpBean.getCheckeEffect()
						.getName());
				map.put("cartonning_standard_count",
						new Integer(pwpBean.getCartonningStandardCount())
								.toString());
				map.put("package_size",
						pwpBean.getLength() + "cm*" + pwpBean.getWidth()
								+ "cm*" + pwpBean.getHeight() + "cm");
				map.put("weight", new Integer(pwpBean.getWeight()).toString());
				map.put("identity_info", pwpBean.getIdentityInfo());
				map.put("product_ware_type_name", pwpBean.getProductWareType()
						.getName());
				/*String operation = "<a href='preEditProductWareProperty.mmx?productWarePropertyId="
						+ pwpBean.getId() + "'>修改</a>";
				operation += "&nbsp;|&nbsp;<a href='javascript:deleteColum("
						+ pwpBean.getId() + ", " + pwpBean.getProductId()
						+ ");'>删除</a>";*/
				/*operation += "&nbsp;|&nbsp;<a href='javascript:showDialog(\"toProductWarePropertyLogInfo.mmx?productWarePropertyId="
						+ pwpBean.getId()
						+ "&productCode="
						+ pwpBean.getProduct().getCode() + "\");'>操作日志</a>";
				map.put("operation", operation);*/
				map.put("product_ware_property_id", new Integer(pwpBean.getId()).toString());
				map.put("product_ware_property_product_id", new Integer(pwpBean.getProductId()).toString());
				resultList.add(map);
			}
			resultMap.put("total", totalCount);
			resultMap.put("rows", resultList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return resultMap;
	}

	/**
	 * 
	 * before 修改商品物流属性
	 * 
	 * @return
	 */
	@RequestMapping("preEditProductWareProperty")
	public String preEditProductWareProperty(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean editProductWarePropertyRight = group.isFlag(697);
		if (!editProductWarePropertyRight) {
			request.setAttribute("tip", "您没有修改商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		List checkEffectList = new ArrayList();
		List productWareTypeList = new ArrayList();
		int productWarePropertyId = StringUtil.parstInt(request
				.getParameter("productWarePropertyId"));
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			ProductWarePropertyBean pwpBean = statService
					.getProductWareProperty("id = " + productWarePropertyId);
			if (pwpBean == null) {
				request.setAttribute("tip", "没有找到对应的商品物流属性信息！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			} else {
				voProduct temp = wareService.getProduct(pwpBean.getProductId());
				if (temp == null) {
					temp = new voProduct();
				}
				pwpBean.setProduct(temp);
				ProductBarcodeVO bBean = bService
						.getProductBarcode("product_id=" + temp.getId());
				if (bBean == null) {
					request.setAttribute("barCode", "");
				} else {
					request.setAttribute("barCode", bBean.getBarcode());
				}
				request.setAttribute("productWareProperty", pwpBean);

				checkEffectList = statService.getCheckEffectList("id<>0", -1,
						-1, "id asc");
				productWareTypeList = productWarePropertyService
						.getProductWareTypeList("id<>0", -1, -1, "id asc");
				/*
				 * productWareTypeList =
				 * statService.getUserOrderPackageTypeList("id<>0", -1, -1,
				 * "type_id asc"); productWareTypeList =
				 * productWarePropertyService
				 * .removeDuplicateInList(productWareTypeList);
				 */

				request.setAttribute("productWareTypeList", productWareTypeList);
				request.setAttribute("checkEffectList", checkEffectList);
			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			statService.releaseAll();
		}
		return "forward:/admin/rec/oper/productWareProperty/editProductWareProperty.jsp";
	}

	/**
	 * 
	 * 修改商品物流属性
	 * 
	 * @return
	 */
	@RequestMapping("editProductWareProperty")
	public String editProductWareProperty(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean editProductWarePropertyRight = group.isFlag(697);
		if (!editProductWarePropertyRight) {
			request.setAttribute("tip", "您没有修改商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		int productWarePropertyId = StringUtil.parstInt(request
				.getParameter("productWarePropertyId"));
		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productBarCode")));
		int checkEffectId = StringUtil.parstInt(request
				.getParameter("checkEffect"));
		int length = StringUtil.parstInt(request.getParameter("length"));
		int width = StringUtil.parstInt(request.getParameter("width"));
		int height = StringUtil.parstInt(request.getParameter("height"));
		// int standardCount =
		// StringUtil.parstInt(request.getParameter("standardCount"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));
		int weight = ProductWarePropertyService.toInt(request
				.getParameter("weight"));
		String identityInfo = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("identityInfo")));
		int binning = StringUtil.parstInt(request.getParameter("binning"));
		voProduct product = null;
		ProductBarcodeVO bBean = null;
		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();

				if (productCode.equals("") && productBarCode.equals("")) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "请至少输入商品编号和商品条码中的一个！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				} else if (!productCode.equals("") && productBarCode.equals("")) {
					product = wareService.getProduct(productCode);
					if (product == null) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应编号的商品！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					}
				} else if (productCode.equals("") && !productBarCode.equals("")) {
					bBean = bService.getProductBarcode("barcode=" + "'"
							+ productBarCode + "'");
					if (bBean == null) {
						statService.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "没有找到对应的条码记录！");
						request.setAttribute("result", "failure");
						return "/admin/error";
					} else {
						product = wareService.getProduct(bBean.getProductId());
						if (product == null) {
							request.setAttribute("tip", "没有找到对应的条码的商品！");
							request.setAttribute("result", "failure");
							return "/admin/error";
						}
					}
				} else {
					// 两个都填了
					// 先要验证产品，productCode productBarCode 是否存在 是否对应于一个
					product = productWarePropertyService
							.isCodeAndBarCodeMatched(productCode,
									productBarCode, bService, statService,
									wareService);
				}

				// 要验证是否已经存在该商品的 商品物流属性
				ProductWarePropertyBean pwpBean = statService
						.getProductWareProperty("id=" + productWarePropertyId);
				if (pwpBean == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "没有找到要修改的商品物流属性的信息！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				} else {
					CheckEffectBean cfBean2 = statService.getCheckEffect("id="
							+ pwpBean.getCheckEffectId());
					if (cfBean2 == null) {
						cfBean2 = new CheckEffectBean();
						cfBean2.setName("");
					}
					pwpBean.setCheckeEffect(cfBean2);

					ProductWareTypeBean pwtBean2 = productWarePropertyService
							.getProductWareType("id="
									+ pwpBean.getProductTypeId());
					if (pwtBean2 == null) {
						pwtBean2 = new ProductWareTypeBean();
						pwtBean2.setName("");
					}
					pwpBean.setProductWareType(pwtBean2);
				}
				ProductWarePropertyBean pwpBeanOther = statService
						.getProductWareProperty("product_id = "
								+ product.getId());
				if (pwpBeanOther != null
						&& pwpBeanOther.getId() != productWarePropertyId) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip",
							"该商品已经有别的商品物流属性了，请去修改对应的商品物流属性！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}

				// 然后验证 质检分类是否存在
				CheckEffectBean cfBean = statService.getCheckEffect("id = "
						+ checkEffectId);
				if (cfBean == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的质检分类！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				// 对应的 物流分类是否存在
				ProductWareTypeBean pwtBean = productWarePropertyService
						.getProductWareType("id=" + wareType);
				if (pwtBean == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "未找到所选的商品物流分类！");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}

				/*
				 * UserOrderPackageTypeBean uoptBean2 =
				 * userOrderService.getUserOrderPackageType("type_id=" +
				 * wareType + " and product_catalog=" + product.getParentId1());
				 * if( uoptBean2 == null ) {
				 * statService.getDbOp().rollbackTransaction();
				 * request.setAttribute("tip", "商品不属于所选的物流分类！ ");
				 * request.setAttribute("result", "failure"); return
				 * mapping.findForward(IConstants.FAILURE_KEY); }
				 */
				// 寻找或修改装箱数量
				// productWarePropertyService.manageCartonningStandardCount(user,product,standardCount,
				// cartonningInfoService);

				// 修改 对应的商品物流属性
				// productWarePropertyService.updateProductWareProperty(pwpBean,
				// user,
				// product,cfBean,pwtBean,length,width,height,weight,statService);
				productWarePropertyService.updateProductWareProperty(pwpBean,
						user, product, cfBean, pwtBean, binning, length, width,
						height, weight, identityInfo, statService, binning);

				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		}  catch(MyRuntimeException mre ) {
			statService.getDbOp().rollbackTransaction();
			request.setAttribute("tip", mre.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		}  catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "系统异常！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "修改成功!");
		request.setAttribute("url", request.getContextPath()
				+ "/admin/toProductWarePropertyInfo.mmx");
		return "/admin/tip";
	}

	/**
	 * 
	 * 删除商品物流属性
	 * 
	 * @return
	 */
	@RequestMapping("deleteProductWareProperty")
	public String deleteProductWareProperty(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		UserGroupBean group = user.getGroup();
		boolean deleteProductWarePropertyRight = group.isFlag(698);
		if (!deleteProductWarePropertyRight) {
			request.setAttribute("tip", "您没有删除商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

		int productWarePropertyId = StringUtil.parstInt(request
				.getParameter("productWarePropertyId"));
		int productId = StringUtil.parstInt(request.getParameter("productId"));

		WareService wareService = new WareService();
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				statService.getDbOp().startTransaction();
				// 要验证是否已经存在该商品的 商品物流属性
				ProductWarePropertyBean pwpBean = statService
						.getProductWareProperty("id = " + productWarePropertyId);
				if (pwpBean == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "没有找到要删除的商品物流属性!");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				ProductWarePropertyBean pwpBean2 = statService
						.getProductWareProperty("id = " + productWarePropertyId
								+ " and product_id = " + productId);
				if (pwpBean2 == null) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "要删除的商品物流属性的商品关联已经发生变更，暂时不能删除!");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				if (!statService.deleteProductWareProperty("id="
						+ productWarePropertyId)) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "在删除商品物流属性时，数据库操作出错!");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				voProduct product = wareService.getProduct(pwpBean
						.getProductId());
				if (product == null) {
					product = new voProduct();
					product.setCode("");
				}
				pwpBean.setProduct(product);
				// 添加日志
				ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
				pwplBean.setProductWarePropertyId(pwpBean.getId());
				pwplBean.setOperDetail("删除了商品" + product.getCode() + "的物流属性");
				pwplBean.setOperId(user.getId());
				pwplBean.setOperName(user.getUsername());
				pwplBean.setTime(DateUtil.getNow());
				if (!productWarePropertyService
						.addProductWarePropertyLog(pwplBean)) {
					statService.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "在删除商品物流属性时，数据库操作出错!");
					request.setAttribute("result", "failure");
					return "/admin/error";
				}
				statService.getDbOp().commitTransaction();
				statService.getDbOp().getConn().setAutoCommit(true);
			}
		} catch (Exception e) {
			statService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", e.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		} finally {
			statService.releaseAll();
		}
		request.setAttribute("tip", "删除成功");
		request.setAttribute("url", request.getContextPath()
				+ "/admin/toProductWarePropertyInfo.mmx");
		return "/admin/tip";
	}

	/**
	 * 
	 * 查询商品物流属性日志
	 * 
	 * @return
	 */
	@RequestMapping("toProductWarePropertyLogInfo")
	public String toProductWarePropertyLogInfo(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean seeProductWarePropertyRight = group.isFlag(695);
		if (!seeProductWarePropertyRight) {
			request.setAttribute("tip", "您没有查看商品物流属性的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		String productWarePropertyId = request
				.getParameter("productWarePropertyId");
		request.setAttribute("productCode", productCode);
		request.setAttribute("productWarePropertyId", productWarePropertyId);
		return "forward:/admin/rec/oper/productWareProperty/productWarePropertyLogInfo.jsp";
	}

	/**
	 * 
	 * 查询商品物流属性日志
	 * 
	 * @return
	 */
	@RequestMapping("getProductWarePropertyLogInfo")
	@ResponseBody
	public Map<String, Object> getProductWarePropertyLogInfo(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			resultMap.put("tip", "当前没有登录，操作失败！");
			return resultMap;
		}

		UserGroupBean group = user.getGroup();
		boolean seeProductWarePropertyRight = group.isFlag(695);
		if (!seeProductWarePropertyRight) {
			resultMap.put("tip", "您没有查看商品物流属性的权限！");
			return resultMap;
		}

		int productWarePropertyId = StringUtil.toInt(request
				.getParameter("productWarePropertyId"));
		int pageIndex = StringUtil.parstInt(request.getParameter("page"));
		pageIndex = pageIndex - 1;
		int countPerPage = StringUtil.parstInt(request.getParameter("rows"));
		List productWarePropertyLogList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			PagingBean paging = null;

			int totalCount = productWarePropertyService
					.getProductWarePropertyLogCount("product_ware_property_id="
							+ productWarePropertyId);
			productWarePropertyLogList = productWarePropertyService
					.getProductWarePropertyLogList("product_ware_property_id="
							+ productWarePropertyId, pageIndex * countPerPage,
							countPerPage, "time desc");
			for (int i = 0; i < productWarePropertyLogList.size(); i++) {
				ProductWarePropertyLogBean pwplBean = (ProductWarePropertyLogBean) productWarePropertyLogList
						.get(i);
				Map<String, String> map = new HashMap<String, String>();
				map.put("time", pwplBean.getTime().substring(0, 19));
				map.put("oper_name", pwplBean.getOperName());
				map.put("oper_info", pwplBean.getOperDetail());
				/*
				 * <td align="left"> <%= pwplBean.getTime().subSequence(0,19)%>
				 * </td> <td align="left"> <%= pwplBean.getOperName()%> </td>
				 * <td align="left"> <%= pwplBean.getOperDetail()%> </td>
				 */
				resultList.add(map);
			}
			resultMap.put("total", totalCount);
			resultMap.put("rows", resultList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return resultMap;
	}

	/**
	 * 
	 * 添加商品物流分类
	 * 
	 * @return
	 */
	@RequestMapping("addProductWareType")
	public String addProductWareType(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean addProductWarePropertyRight = group.isFlag(725);
		if (!addProductWarePropertyRight) {
			request.setAttribute("tip", "您没有添加商品物流分类的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String name = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("name")));
		WareService wareService = new WareService();
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				productWarePropertyService.getDbOp().startTransaction();
				int has = productWarePropertyService
						.getProductWareTypeCount("name='" + name + "'");
				if (has > 0) {
					request.setAttribute("tip", "该商品物流分类已存在！");
					request.setAttribute("result", "failure");
					productWarePropertyService.getDbOp().rollbackTransaction();
					return "/admin/error";
				} else {
					ProductWareTypeBean pwtBean = new ProductWareTypeBean();
					pwtBean.setName(name);
					pwtBean.setSequence(1);
					if (!productWarePropertyService.addProductWareType(pwtBean)) {
						request.setAttribute("tip", "在添加商品物流分类时，数据库操作失败！");
						request.setAttribute("result", "failure");
						productWarePropertyService.getDbOp()
								.rollbackTransaction();
						return "/admin/error";
					}
				}
				productWarePropertyService.getDbOp().commitTransaction();
				productWarePropertyService.getDbOp().getConn()
						.setAutoCommit(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "添加商品物流分类时发生了错误！");
			request.setAttribute("result", "failure");
			productWarePropertyService.getDbOp().rollbackTransaction();
			return "/admin/error";
		} finally {
			productWarePropertyService.releaseAll();
		}
		request.setAttribute("tip", "添加成功!");
		request.setAttribute("url", request.getContextPath()
				+ "/admin/rec/oper/productWareType/addProductWareType.jsp");
		return "/admin/tip";
	}

	/**
	 * 
	 * 通往查询商品物流分类
	 * 
	 * @return
	 */
	@RequestMapping("toProductWareTypeInfo")
	public String toProductWareTypeInfo(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		return "forward:/admin/rec/oper/productWareType/productWareTypeInfo.jsp";
	}

	/**
	 * 
	 * 查询商品物流分类
	 * 
	 * @return
	 */
	@RequestMapping("getProductWareTypeInfo")
	@ResponseBody
	public Map<String, Object> getProductWareTypeInfo(
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			resultMap.put("tip", "当前没有登陆，操作失败！");
			return resultMap;
		}
		int pageIndex = StringUtil.parstInt(request.getParameter("page"));
		pageIndex = pageIndex - 1;
		int countPerPage = StringUtil.parstInt(request.getParameter("rows"));
		List productWareTypeList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			PagingBean paging = null;
			int totalCount = productWarePropertyService
					.getProductWareTypeCount("id<>0");
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			productWareTypeList = productWarePropertyService
					.getProductWareTypeList("id<>0",
							paging.getCurrentPageIndex() * countPerPage,
							countPerPage, "id asc");
			for (int i = 0; i < productWareTypeList.size(); i++) {
				ProductWareTypeBean pwtBean = (ProductWareTypeBean) productWareTypeList
						.get(i);
				Map<String, String> map = new HashMap<String, String>();

				map.put("a", pwtBean.getName());
				String inp = "<input type='text' size='6' name='sequence_"
						+ pwtBean.getId()
						+ "' id='sequence_"
						+ pwtBean.getId()
						+ "' value='"
						+ pwtBean.getSequence()
						+ "' onblur='checkNumber(this);' onchange='addToChange("
						+ pwtBean.getId() + ");'/>";
				inp += "<input type='hidden' name='change' id='change_"
						+ pwtBean.getId() + "' value='" + pwtBean.getId()
						+ "' disabled='true'/>";
				map.put("b", inp);
				/*
				 * <tr bgcolor="#FFFFFF" > <td align="left"> <%=
				 * pwtBean.getName()%> </td> <td align="center" width="60px">
				 * <input type="text" size="6"
				 * name="sequence_<%= pwtBean.getId()%>"
				 * id="sequence_<%= pwtBean.getId()%>"
				 * value="<%= pwtBean.getSequence()%>"
				 * onblur="checkNumber(this);"
				 * onchange="addToChange(<%= pwtBean.getId()%>);" /> <input
				 * type="hidden" name="change" id="change_<%= pwtBean.getId()%>"
				 * value="<%= pwtBean.getId()%>" disabled="true"/> </td> </tr>
				 */
				resultList.add(map);
			}
			resultMap.put("total", totalCount);
			resultMap.put("rows", resultList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return resultMap;
	}

	/**
	 * 
	 * 修改商品物流分类优先级
	 * 
	 * @return
	 */
	@RequestMapping("editProductWareTypeSequence")
	public String editProductWareTypeSequence(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		UserGroupBean group = user.getGroup();
		boolean editProductWarePropertyRight = group.isFlag(708);
		if (!editProductWarePropertyRight) {
			request.setAttribute("tip", "您没有修改商品物流分类优先级的权限！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		String[] change = request.getParameterValues("change");
		boolean hasChange = false;
		WareService wareService = new WareService();
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				if (change != null) {
					productWarePropertyService.getDbOp().startTransaction();
					int changeCount = change.length;
					for (int i = 0; i < changeCount; i++) {
						String temp = change[i];
						int id = StringUtil.parstInt(temp);
						int sequence = StringUtil.parstInt(request
								.getParameter("sequence_" + id));
						if (id == 0 || sequence <= 0) {
							request.setAttribute("tip", "所传参数有误!");
							request.setAttribute("result", "failure");
							productWarePropertyService.getDbOp()
									.rollbackTransaction();
							return "/admin/error";
						}
						// 查找是否还存在要修改的项目
						ProductWareTypeBean pwtBean = productWarePropertyService
								.getProductWareType("id=" + id);
						if (pwtBean == null) {
							request.setAttribute("tip", "未找到要修改的项目!");
							request.setAttribute("result", "failure");
							productWarePropertyService.getDbOp()
									.rollbackTransaction();
							return "/admin/error";
						}
						if (pwtBean.getSequence() != sequence) {
							// 执行修改
							if (!productWarePropertyService
									.updateProductWareType("sequence="
											+ sequence, "id=" + id)) {
								request.setAttribute("tip", "修改时，数据库操作失败!");
								request.setAttribute("result", "failure");
								productWarePropertyService.getDbOp()
										.rollbackTransaction();
								return "/admin/error";
							}
							hasChange = true;
						}
					}
					productWarePropertyService.getDbOp().commitTransaction();
					productWarePropertyService.getDbOp().getConn()
							.setAutoCommit(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "修改商品物流分类优先级时发生了错误！");
			request.setAttribute("result", "failure");
			productWarePropertyService.getDbOp().rollbackTransaction();
			return "/admin/error";
		} finally {
			productWarePropertyService.releaseAll();
		}
		if (hasChange) {
			request.setAttribute("tip", "修改优先级成功!");
			request.setAttribute("url", request.getContextPath()
					+ "/admin/toProductWareTypeInfo.mmx");
			return "/admin/tip";
		} else {
			request.setAttribute("tip", "并没有任何修改!");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}

	}

}
