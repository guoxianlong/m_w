package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stat.AbnormalRealProductBean;
import adultadmin.bean.stat.BsByAbnormalBean;
import adultadmin.bean.stat.WarehousingAbnormalBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 作者：石远飞
 * 
 * 日期：2013-4-3
 * 
 * 说明：异常入库单Action
 * */
public class WarehousingAbnormalAction extends DispatchAction {
	
	static byte[] lock = new byte[0]; 
	/**
	 *	说明：1.查询异常入库单列表2条件查询异常入库单3实现了分页4返回异常入库单列表页面 
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */ 
	public ActionForward selectAbnormalList(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(753)){
			request.setAttribute("tip", "对不起,你没有异常入库单访问权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		
		String bsbyResult = StringUtil.convertNull(request.getParameter("bsbyResult")).trim();
		String abnormalCode = StringUtil.convertNull(request.getParameter("abnormalCode")).trim();
		String startTime = StringUtil.convertNull(request.getParameter("startTime")).trim();
		String endTime = StringUtil.convertNull(request.getParameter("endTime")).trim();
		String operator = StringUtil.convertNull(request.getParameter("operator")).trim();
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode")).trim();
		String packageCode = StringUtil.convertNull(request.getParameter("packageCode")).trim();
		String deliver = StringUtil.convertNull(request.getParameter("deliver")).trim();
		String status = StringUtil.convertNull(request.getParameter("status")).trim();
		String area = StringUtil.convertNull(request.getParameter("wareArea")).trim();
		
		WarehousingAbnormalBean bean = null;
		ResultSet rs = null;
		try {
			StringBuilder url = new StringBuilder();
			url.append("warehousingAbnormalAction.do?method=selectAbnormalList");
			StringBuilder condition = new StringBuilder();
			if (!"".equals(abnormalCode)) {
				condition.append(" and wa.code='"+ StringUtil.toSql(abnormalCode) + "'");
				url.append("&abnormalCode=" + abnormalCode);
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				condition.append(" and left(wa.create_time,10) between '"+ StringUtil.toSql(startTime) + "' and '"+ StringUtil.toSql(endTime) + "'");
				url.append("&startTime=" + startTime + "&endTime=" + endTime);
			}
			if (!"".equals(operator)) {
				if(operator.matches("^[0-9]+$")){
					bean = service.getWarehousingAbnormal("operator_id=" + operator);
				}else{
					bean = service.getWarehousingAbnormal("operator_name='"+ operator + "'");
				}
				if(bean != null){
					condition.append(" and wa.operator_id=" + bean.getOperatorId());
					url.append("&operator=" + bean.getOperatorId());
				}else{
					condition.append(" and 1=2");
				}
			}
			if (!"".equals(orderCode)) {
				condition.append(" and ap.order_code='"+ StringUtil.toSql(orderCode) + "'");
				url.append("&orderCode=" + orderCode);
			}
			if (!"".equals(packageCode)) {
				condition.append(" and ap.package_code='"+ StringUtil.toSql(packageCode) + "'");
				url.append("&packageCode=" + packageCode);
			}
			if (!"".equals(deliver) && !"-1".equals(deliver)) {
				condition.append(" and ap.deliver="+ deliver);
				url.append("&deliver=" + deliver);
			}
			if (!"".equals(status) && !"-1".equals(status)) {
				condition.append(" and wa.status="+ status);
				url.append("&status=" + status);
			}
			if ("".equals(area) || "-1".equals(area)){
				area = "";
				List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
				condition.append(" and (");
				for(String arg : cdaList){
					condition.append("wa.ware_area="+ arg + " or ");
					area = area.concat(arg  + ",");
				}
				condition.append(" 1=2) ");
				url.append("&wareArea=" + area);
			}else if(area.contains(",")){
				String areaId[] = area.split(",");
				area = "";
				condition.append(" and (");
				for(String arg : areaId){
					condition.append("wa.ware_area="+ arg + " or ");
					area = area.concat(arg  + ",");
				}
				condition.append(" 1=2) ");
				url.append("&wareArea=" + area);
			}else if("-2".equals(area)){
				condition.append(" and 1=2");
			}else{
				condition.append(" and wa.ware_area=" + area);
				url.append("&wareArea=" + area);
			}
			String query = "select count(distinct wa.id) from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					" where wa.id>0 "+ condition ;
			int totalCount = 0;
			rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				totalCount = rs.getInt(1);
			}
			int countPerPage = 10;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean pageBean = new PagingBean(pageIndex, totalCount,countPerPage);
			query = "select distinct wa.code,wa.create_time,wa.status,ap.order_code,ap.package_code,ap.deliver,ap.sorting_datetime," +
					"wa.operator_name,wa.id from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id>0 "+ condition
					+ " order by wa.create_time DESC limit "  
					+ pageBean.getCurrentPageIndex()* countPerPage
					+ ","
					+ countPerPage;
			rs = service.getDbOp().executeQuery(query);
			List<WarehousingAbnormalBean> abnormalList = new ArrayList<WarehousingAbnormalBean>();
			while (rs.next()) {
				WarehousingAbnormalBean waBean = new WarehousingAbnormalBean();
				waBean.setCode(rs.getString(1));
				waBean.setCreateTime(rs.getString(2));
				waBean.setStatus(rs.getInt(3));
				waBean.setOrderCode(rs.getString(4));
				waBean.setPackageCode(rs.getString(5));
				waBean.setDeliver(rs.getInt(6));
				waBean.setSortingDatetime(rs.getString(7));
				waBean.setOperatorName(rs.getString(8));
				waBean.setId(rs.getInt(9));
				abnormalList.add(waBean);
			}
			List<WarehousingAbnormalBean> bsbyList = new ArrayList<WarehousingAbnormalBean>();
			for(WarehousingAbnormalBean abnormalBean : abnormalList){
				query ="select bba.abnormal_id,bbo.receipts_number,bbo.type,bbo.id,bbo.current_type,bbo.operator_id " +
						"from bsby_abnormal bba left join bsby_operationnote bbo on bba.bsby_id = bbo.id " +
						" where bba.abnormal_id=" + abnormalBean.getId();
				rs = service.getDbOp().executeQuery(query);
				while (rs.next()) {
					WarehousingAbnormalBean bsbyBean = new WarehousingAbnormalBean();
					bsbyBean.setId(rs.getInt(1));
					bsbyBean.setReceiptsNumber(rs.getString(2));
					bsbyBean.setBsbyType(rs.getInt(3));
					bsbyBean.setBsbyId(rs.getInt(4));
					bsbyBean.setBsbyStatus(rs.getInt(5));
					bsbyBean.setBsbyOperatorId(rs.getInt(6));
					bsbyList.add(bsbyBean);
				}
			}
			request.setAttribute("startTime", startTime);
			request.setAttribute("endTime", endTime);
			request.setAttribute("deliver", deliver);
			request.setAttribute("status", status);
			request.setAttribute("area", area);
			pageBean.setPrefixUrl(url.toString());
			request.setAttribute("abnormalList", abnormalList);
			request.setAttribute("bsbyList", bsbyList);
			request.setAttribute("pageBean", pageBean);
			request.setAttribute("recordNum", totalCount+"");
			request.setAttribute("bsbyResult", bsbyResult);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return mapping.findForward("abnormalList");
	}
	/**
	 *	说明：1.在此清空了session里所有内容 2.最终跳转到添加异常入库单页面
	 *	日期：2013-4-15
	 * 	作者：石远飞
	 */
	public ActionForward openAddWareAbnoraml(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(754)){
			request.setAttribute("tip", "对不起,你没有添加异常入库单权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		request.getSession().removeAttribute("wareArea");
		request.getSession().removeAttribute("vpList");
		request.getSession().removeAttribute("rpList");
		request.getSession().removeAttribute("apBean");
		return mapping.findForward("addWarehousingAbnormal");
	}
	/**
	 *	说明：1.查询商品相关信息 
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public ActionForward findProductInfoList(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String code = StringUtil.convertNull(request.getParameter("code").trim());
		String wareArea = StringUtil.convertNull(request.getParameter("wareArea").trim());
		ResultSet rs = null;
		try {
			String sql = null;
			String orderCode = null;
			String packageCode = null;
			sql = "select package_code from deliver_package_code where package_code='" + StringUtil.toSql(code) + "' and used=1";
			rs = service.getDbOp().executeQuery(sql);
			if(rs.next()){
				packageCode = rs.getString(1);
			}
			AuditPackageBean apBean = null;
			if(packageCode != null){
				apBean = service.getAuditPackage("package_code='" + packageCode + "'");
			}
			if(apBean != null){
				orderCode = apBean.getOrderCode();
				String result = checkOrder(orderCode,wareArea);
				if(!"ok".equals(result)){//调用checkOrder方法判断订单是否符合要求
					request.setAttribute("tip", result);
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}else{
				String result = checkOrder(code,wareArea);
				if(!"ok".equals(result)){//调用checkOrder方法判断订单是否符合要求
					request.setAttribute("tip", result);
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				apBean = service.getAuditPackage("order_code='" + StringUtil.toSql(code) + "'");
				if (apBean == null) {
					request.setAttribute("tip", "查不到包裹单信息!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			//获取商品列表
			List<voProduct> vpList = getVOProductList(apBean.getOrderId());
			request.getSession().setAttribute("vpList", vpList);
			request.getSession().setAttribute("apBean", apBean);
			request.getSession().setAttribute("wareArea", wareArea);
			request.getSession().removeAttribute("rpList");
			request.getSession().removeAttribute("flag");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return mapping.findForward("addWarehousingAbnormal");
	}
	/**
	 * 	说明：1.AJAX添加实际退回商品
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public ActionForward addRealProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		String realCode = StringUtil.convertNull(request.getParameter("realCode").trim());
		String realCount = StringUtil.convertNull(request.getParameter("realCount").trim());
		try {
			@SuppressWarnings("unchecked")
			List<voProduct> rpList = (ArrayList<voProduct>) request.getSession().getAttribute("rpList");
			if (rpList == null) {
				rpList = new ArrayList<voProduct>();
			}
			voProduct vpBean = wareService.getProduct(StringUtil.toSql(realCode));
			if (vpBean == null) {
				request.setAttribute("result", "商品编号不正确!");
				return mapping.findForward("selection");
			}
			if (rpList.size() > 0) {
				for (voProduct bean : rpList) {
					if (vpBean.getCode().equals(bean.getCode())) {
						request.setAttribute("result", "此商品已添加!");
						return mapping.findForward("selection");
					}
				}
			}
			vpBean.setCount(Integer.parseInt(realCount));
			rpList.add(vpBean);
			request.getSession().setAttribute("rpList", rpList);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			wareService.releaseAll();
		}
		return mapping.findForward("selection");
	}
	/**
     * 	说明：1.AJAX删除实际退回商品
	 *	日期：2013-4-15x
	 *	作者：石远飞
	 */
	public ActionForward delRealProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		String realCode = StringUtil.convertNull(request.getParameter("realCode").trim());
		try {
			@SuppressWarnings("unchecked")
			List<voProduct> rpList = (ArrayList<voProduct>) request.getSession().getAttribute("rpList");
			if (rpList == null) {
				request.setAttribute("tip", "此操作超时,删除失败 !");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			voProduct vpBean = wareService.getProduct(StringUtil.toSql(realCode));
			if (vpBean == null) {
				request.setAttribute("result", "商品编号不正确!");
				return mapping.findForward("selection");
			}
			if (rpList.size() > 0) {
				voProduct delBean = null;
				for (voProduct bean : rpList) {
					if (vpBean.getCode().equals(bean.getCode())) {
						delBean = bean;
					}
				}
				rpList.remove(delBean);
			}
			request.getSession().setAttribute("rpList", rpList);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			wareService.releaseAll();
		}
		return mapping.findForward("selection");
	}
	/**
	 *	说明：1.AJAX更新实际退回商品
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public ActionForward upRealProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(db);
		String realCode = StringUtil.convertNull(request.getParameter("realCode").trim());
		String realCount = StringUtil.convertNull(request.getParameter("realCount").trim());
		try {
			@SuppressWarnings("unchecked")
			List<voProduct> rpList = (ArrayList<voProduct>) request.getSession().getAttribute("rpList");
			if (rpList == null) {
				request.setAttribute("tip", "此操作超时,修改商品数量失败 !");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			voProduct vpBean = wareService.getProduct(StringUtil.toSql(realCode));
			if (vpBean == null) {
				request.setAttribute("result", "商品编号不正确!");
				return mapping.findForward("selection");
			}
			if (rpList.size() > 0) {
				voProduct delBean = null;
				for (voProduct bean : rpList) {
					if (vpBean.getCode().equals(bean.getCode())) {
						delBean = bean;
					}
				}
				rpList.remove(delBean);
			}
			vpBean.setCount(StringUtil.toInt(realCount));
			rpList.add(vpBean);
			request.getSession().setAttribute("rpList", rpList);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			wareService.releaseAll();
		}
		return mapping.findForward("selection");
	}
	/**
	 *	说明：1.生成异常入库单2保存实际退回商品列表
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public ActionForward addWarehousingAbnormal(ActionMapping mapping,
			ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(754)){
			request.setAttribute("tip", "对不起,你没有添加异常入库单权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, db);
		String abnormalId = null;
		AuditPackageBean apBean = (AuditPackageBean) request.getSession().getAttribute("apBean");
		String wareArea = (String) request.getSession().getAttribute("wareArea");
		List<voProduct> rpList = (ArrayList<voProduct>)request.getSession().getAttribute("rpList");
		List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
		try {
			String code = "YCR" + DateUtil.getNow().substring(2, 4)+ DateUtil.getNow().substring(5, 7)+ DateUtil.getNow().substring(8, 10);
			int maxid = service.getNumber("id", "warehousing_abnormal", "max","id > 0");
			WarehousingAbnormalBean lastCode = service.getWarehousingAbnormal("code like '" + code + "%'");
			if (lastCode == null) {
				// 当日第一份单据,编号最后5位 00001
				code += "00001";
			} else {
				// 获取当日计划编号最大值
				lastCode = service.getWarehousingAbnormal("id =" + maxid);
				String _code = lastCode.getCode();
				int number = Integer.parseInt(_code.substring(_code.length() - 5));
				if (number == 99999) {
					request.setAttribute("tip", "当天的异常入库单不能生成超过99999单!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				number++;
				code += String.format("%05d",new Object[] { new Integer(number) });
			}
			if(isTimeout(request)){ //校验session里的内容是否超时
				request.setAttribute("tip", "没有添加实际退回商品,或操作超时,请重新操作!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(rpList.size()==0){
				request.setAttribute("tip", "请输入实际退回商品!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!checkAbnormalOrCompensate(listTurnMap(vpList), listTurnMap(rpList))){
				request.setAttribute("tip", "不符合异常入库单条件 或请走理赔单!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			WarehousingAbnormalBean waBean = new WarehousingAbnormalBean();
			waBean.setCode(code);
			waBean.setStatus(0);
			waBean.setOperatorId(user.getId());
			waBean.setOperatorName(user.getUsername());
			waBean.setCreateTime(DateUtil.getNow());
			waBean.setOrderId(apBean.getOrderId());
			waBean.setWareArea(Integer.parseInt(wareArea));
			// service插入异常入库单
			service.getDbOp().startTransaction(); //开启事务
			if (!service.addWarehousingAbnormal(waBean)) {
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "添加异常入库单失败!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			maxid = service.getNumber("id", "warehousing_abnormal", "max","id > 0");
			WarehousingAbnormalBean anormalBean = service.getWarehousingAbnormal("id=" + maxid);
			abnormalId = anormalBean.getId() + "";
			for(voProduct bean : rpList){
				AbnormalRealProductBean arpBean = new AbnormalRealProductBean();
				arpBean.setProductCode(bean.getCode());
				arpBean.setProductCount(bean.getCount());
				arpBean.setProductId(bean.getId());
				arpBean.setProductName(bean.getName());
				arpBean.setProductOriname(bean.getOriname());
				arpBean.setAbnormalId(anormalBean.getId());
				if(!service.addAbnormalRealProduct(arpBean)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "保存实际退回商品失败!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id = " + anormalBean.getId() ;
			ResultSet rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				if (!packageLog.addReturnPackageLog("添加异常入库单", user, rs.getString(1))) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加退货日志失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			service.getDbOp().commitTransaction(); //提交事务
		} catch (Exception e) {
			service.getDbOp().rollbackTransaction();
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			service.releaseAll();
		}
		return new ActionForward("/admin/warehousingAbnormalAction.do?method=readyEditWarehousingAbnormal&abnormalId=" + abnormalId);
	}
	/**	
	 *  说明：1.删除异常入库单
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public ActionForward delWarehousingAbnormal(ActionMapping mapping,ActionForm form, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录,操作失败!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(755)){
				request.setAttribute("tip", "对不起,你没有删除异常入库单权限!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			DbOperation db = new DbOperation();
			db.init();
			WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
			ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			String abnormalId = StringUtil.convertNull((String)request.getParameter("abnormalId"));
			try {
				service.getDbOp().startTransaction();//开启事务
				String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
						"where wa.id = " + abnormalId ;
				ResultSet rs = service.getDbOp().executeQuery(query);
				if (rs.next()) {
					if (!packageLog.addReturnPackageLog("删除异常入库单", user, rs.getString(1))) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "添加退货日志失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				if(!service.delWarehousingAbnormal("id=" + abnormalId)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "删除异常入库单失败 !");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List<AbnormalRealProductBean> list = service.getAbnormalRealProductList("abnormal_id=" + abnormalId, -1, -1, null);
				if(list != null && list.size() > 0){
					if(!service.delAbnormalRealProduct("abnormal_id=" + abnormalId)){
						service.getDbOp().rollbackTransaction();
						request.setAttribute("tip", "删除实际退回商品失败 !");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				service.getDbOp().commitTransaction();//提交事务
			} catch (Exception e) {
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "程序异常 !");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			} finally {
				service.releaseAll();
			}
			return new ActionForward("/admin/warehousingAbnormalAction.do?method=selectAbnormalList");
		}
	/**
	 * 	说明：1.更新异常入库单
	 *	日期：2013-4-17
	 * 	作者：石远飞
	 **/
	@SuppressWarnings("unchecked")
	public ActionForward updateWarehousingAbnormal(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(755)){
			request.setAttribute("tip", "对不起,你没有更新异常入库单权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, db);
		String abnormalId = StringUtil.convertNull(request.getParameter("abnormalId"));
		List<voProduct> rpList = (ArrayList<voProduct>)request.getSession().getAttribute("rpList");
		List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
		try {
			if(rpList.size()==0){
				request.setAttribute("tip", "由于没有实际退回商品,请走理赔单!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			WarehousingAbnormalBean abnormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(abnormalBean == null){
				request.setAttribute("tip", "此异常入库单不存在!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(isTimeout(request)){ //校验session里的内容是否超时
				request.setAttribute("tip", "操作超时,请重新操作!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(rpList.size()==0){
				request.setAttribute("tip", "请输入实际退回商品!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(!checkAbnormalOrCompensate(listTurnMap(vpList), listTurnMap(rpList))){
				request.setAttribute("tip", "不符合异常入库单条件,或请走理赔单!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.getDbOp().startTransaction(); //开启事务
			if (!service.updateWarehousingAbnormal(" operator_id=" + user.getId() +",operator_name='" + user.getUsername()
					+"',create_time='" + DateUtil.getNow() + "' ", " id=" + abnormalBean.getId())) {
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "更新异常入库单失败!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			abnormalBean = service.getWarehousingAbnormal("id=" + abnormalBean.getId());
			List<AbnormalRealProductBean> list = service.getAbnormalRealProductList("abnormal_id=" + abnormalId, -1, -1, null);
			if(list != null && list.size() > 0){
				if(!service.delAbnormalRealProduct("abnormal_id=" + abnormalId)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "删除实际退回商品失败 !");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			for(voProduct bean : rpList){
				AbnormalRealProductBean arpBean = new AbnormalRealProductBean();
				arpBean.setProductCode(bean.getCode());
				arpBean.setProductCount(bean.getCount());
				arpBean.setProductId(bean.getId());
				arpBean.setProductName(bean.getName());
				arpBean.setProductOriname(bean.getOriname());
				arpBean.setAbnormalId(abnormalBean.getId());
				if(!service.addAbnormalRealProduct(arpBean)){
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "保存实际退回商品失败!");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}	
			String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id = " + abnormalBean.getId();
			ResultSet rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				if (!packageLog.addReturnPackageLog("修改异常入库单", user, rs.getString(1))) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加退货日志失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			service.getDbOp().commitTransaction(); //提交事务
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			service.releaseAll();
		}
		return new ActionForward("/admin/warehousingAbnormalAction.do?method=readyEditWarehousingAbnormal&abnormalId=" + abnormalId);
	}
	/**
	 *	说明：1.更新异常入库单状态为已提交
	 *	日期：2013-7-8
	 *	作者：石远飞
	 */
	public ActionForward statusToSubmitted(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(755)){
			request.setAttribute("tip", "对不起,你没有提交异常入库单权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull(request.getParameter("abnormalId"));
		try {
			if (!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS1, " id=" + abnormalId)) {
				request.setAttribute("tip", "更新异常入库单转状态失败!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			service.releaseAll();
		}
		return new ActionForward("/admin/warehousingAbnormalAction.do?method=readyEditWarehousingAbnormal&abnormalId=" + abnormalId);
	}
	/**
	 *	说明：1.更新异常入库单状态为未处理
	 *	日期：2013-7-8
	 *	作者：石远飞
	 */
	public ActionForward statusToUntreated(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(858)){
			request.setAttribute("tip", "对不起,你没有异常入库单审核权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull(request.getParameter("abnormalId"));
		try {
			if (!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS0, " id=" + abnormalId)) {
				request.setAttribute("tip", "更新异常入库单转状态失败!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			service.releaseAll();
		}
		return new ActionForward("/admin/warehousingAbnormalAction.do?method=readyEditWarehousingAbnormal&abnormalId=" + abnormalId);
	}
	/**
	 *	说明：1.为打开编辑异常入库单页面做准备
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public ActionForward readyEditWarehousingAbnormal(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		//审核请求
		String auditFlg = request.getParameter("auditFlg");
		if("1".equals(auditFlg)){
			if(!group.isFlag(858)){
				request.setAttribute("tip", "对不起,你没有异常入库单审核权限!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		}else if(!group.isFlag(755)){
			request.setAttribute("tip", "对不起,你没有编辑异常入库单权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull((String)request.getParameter("abnormalId"));
		ResultSet rs = null;
		try {
			
			WarehousingAbnormalBean anormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(anormalBean == null){
				request.setAttribute("tip", "异常入库单不存在!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			AuditPackageBean apBean = service.getAuditPackage(" order_id=" + anormalBean.getOrderId());
			List<voProduct> vpList = getVOProductList(anormalBean.getOrderId());
			List<voProduct> rpList = new ArrayList<voProduct>();
			String query = " select product_code,product_name,product_count,product_oriname,product_id from abnormal_real_products" +
					" where abnormal_id=" + anormalBean.getId();
			rs = service.getDbOp().executeQuery(query);
			while(rs.next()){
				voProduct bean = new voProduct();
				bean.setCode(rs.getString(1));
				bean.setName(rs.getString(2));
				bean.setCount(rs.getInt(3));
				bean.setOriname(rs.getString(4));
				bean.setId(rs.getInt(5));
				rpList.add(bean);
			}
			request.getSession().setAttribute("vpList", vpList);
			request.getSession().setAttribute("rpList", rpList);
			request.getSession().setAttribute("apBean", apBean);
			request.getSession().setAttribute("wareArea", anormalBean.getWareArea()+"");
			request.setAttribute("anormalBean", anormalBean);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return mapping.findForward("editWarehousingAbnormal");

	}/**
	 *	说明：1.查看异常入库单相关信息
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public ActionForward showWarehousingAbnormal(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(753)){
			request.setAttribute("tip", "对不起,你没有查看异常入库单权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull((String)request.getParameter("abnormalId"));
		ResultSet rs = null;
		try {
			WarehousingAbnormalBean anormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(anormalBean == null){
				request.setAttribute("tip", "异常入库单不存在!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List<voProduct> vpList = getVOProductList(anormalBean.getOrderId());
			List<voProduct>	rpList = new ArrayList<voProduct>();
			String query = " select product_code,product_name,product_count,product_oriname,product_id from abnormal_real_products" +
						" where abnormal_id=" + anormalBean.getId();
			rs = service.getDbOp().executeQuery(query);
			while(rs.next()){
				voProduct bean = new voProduct();
				bean.setCode(rs.getString(1));
				bean.setName(rs.getString(2));
				bean.setCount(rs.getInt(3));
				bean.setOriname(rs.getString(4));
				bean.setId(rs.getInt(5));
				rpList.add(bean);
			}
			AuditPackageBean apBean = service.getAuditPackage(" order_id=" + anormalBean.getOrderId());
			request.getSession().setAttribute("rpList", rpList);
			request.getSession().setAttribute("vpList", vpList);
			request.getSession().setAttribute("apBean", apBean);
			request.getSession().setAttribute("wareArea", anormalBean.getWareArea()+"");
			request.setAttribute("anormalBean", anormalBean);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return mapping.findForward("showWarehousingAbnormal");

	}
	/**
	 * 	说明：1.生成报损报溢单然后关联异常入库单
	 *	日期：2013-4-17
	 * 	作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public ActionForward submitWarehousingAbnormal(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(858)){
			request.setAttribute("tip", "对不起,你没有异常入库单审核权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, db);
		int wareArea = StringUtil.toInt((String)request.getSession().getAttribute("wareArea"));
		int abnormalId = StringUtil.toInt((String)request.getParameter("abnormalId"));
		Map<String,voProduct> vpMap = listTurnMap((ArrayList<voProduct>)request.getSession().getAttribute("vpList"));
		Map<String,voProduct> rpMap = listTurnMap((ArrayList<voProduct>)request.getSession().getAttribute("rpList"));
		int bsbyCount = 0;
		String bsbyResult = null;
		try {
			CargoInfoBean ciBean = cargoService.getCargoInfo(" area_id=" + wareArea + " and store_type=2 and stock_type=4");
			String cargoCode = null;
			if(ciBean != null){
				cargoCode = ciBean.getWholeCode();
			}else{
				request.setAttribute("tip", "找不到退货库货位!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//校验session里的内容是否超时
			if(isTimeout(request)){ 
				request.setAttribute("tip", "操作超时,请重新操作!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			 //校验是否复核异常入库单条件
			if(!checkAbnormalOrCompensate(vpMap, rpMap)){
				request.setAttribute("tip", "不符合异常入库条件,或请走理赔单!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//开启事务
			service.getDbOp().startTransaction();
			for(Map.Entry<String, voProduct> vpEntry : vpMap.entrySet()){
				if(rpMap.containsKey(vpEntry.getKey())){
					voProduct vpBean = vpEntry.getValue();
					voProduct rpBean = rpMap.get(vpEntry.getKey());
					if(vpBean.getCount()>rpBean.getCount()){
						int count = vpBean.getCount() - rpBean.getCount();
						//生成报损单
						int bsbyId = addBsByOperationnote(user, wareArea, 0, 4);
						if(bsbyId == -1){ 
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", "报溢单与异常入库单关联失败!");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//向报损单中添加商品
						String result = addByBsProduct(user, vpBean.getCode(), cargoCode, count, bsbyId);
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", result);
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//审核报损单
						result = auditBsByOperationnote(bsbyId);
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", result);
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//报损单与异常入库单关联
						BsByAbnormalBean bean = new BsByAbnormalBean();
						bean.setAbnormalId(abnormalId);
						bean.setBsbyId(bsbyId);
						if(service.addBsByAbnormal(bean)){
							if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
								service.getDbOp().rollbackTransaction(); 
								request.setAttribute("tip", "更新异常入库单状态失败!");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}else{
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", "报溢单与异常入库单关联失败!");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						bsbyCount ++;
					}else if(vpBean.getCount() < rpBean.getCount()){
						int count = rpBean.getCount() - vpBean.getCount();
						//生成报溢单
						int bsbyId = addBsByOperationnote(user, wareArea, 1, 4);
						if(bsbyId == -1){ 
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", "报溢单与异常入库单关联失败!");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//向报溢单中添加商品
						String result = addByBsProduct(user, vpBean.getCode(), cargoCode, count, bsbyId);	
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", result);
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//审核报溢单
						result = auditBsByOperationnote(bsbyId);
						if(!"success".equals(result)){
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", result);
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						//报溢单与异常入库单关联
						BsByAbnormalBean bean = new BsByAbnormalBean();
						bean.setAbnormalId(abnormalId);
						bean.setBsbyId(bsbyId);
						if(service.addBsByAbnormal(bean)){
							if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "更新异常入库单状态失败!");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}else{
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", "报溢单与异常入库单关联失败!");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						bsbyCount ++;
					}
				}else{//实际退回商品列表中没有这种商品 所以报损
					voProduct vpBean = vpEntry.getValue();
					//报损数量
					int count = vpBean.getCount();
					//1生成报损单
					int bsbyId = addBsByOperationnote(user, wareArea, 0, 4);
					if(bsbyId == -1){ 
						service.getDbOp().rollbackTransaction(); 
						request.setAttribute("tip", "报损单与异常入库单关联失败!");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//向报损单中添加商品
					String result = addByBsProduct(user, vpBean.getCode(), cargoCode, count, bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); 
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//审核报溢单
					result = auditBsByOperationnote(bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); 
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//报损单与异常入库单关联
					BsByAbnormalBean bean = new BsByAbnormalBean();
					bean.setAbnormalId(abnormalId);
					bean.setBsbyId(bsbyId);
					if(service.addBsByAbnormal(bean)){
						if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", "更新异常入库单状态失败!");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}else{
						service.getDbOp().rollbackTransaction(); 
						request.setAttribute("tip", "报溢单与异常入库单关联失败!");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					bsbyCount ++;
				}
			}
			for(Map.Entry<String, voProduct> rpEntry : rpMap.entrySet()){
				//总商品列表中没有实际退回商品列表中的这个商品 所以报溢
				if(!vpMap.containsKey(rpEntry.getKey())){
					//报溢数量
					voProduct rpBean = rpEntry.getValue();
					int count = rpBean.getCount();
					//1生成报溢单
					int bsbyId = addBsByOperationnote(user, wareArea, 1, 4);
					if(bsbyId == -1){ //bsbyId为-1说明生成报损报溢单失败!
						service.getDbOp().rollbackTransaction(); //事务回滚
						request.setAttribute("tip", "报溢单与异常入库单关联失败!");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//向报溢单中添加商品
					String result = addByBsProduct(user,rpBean.getCode(), cargoCode, count, bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); //事务回滚
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//审核报溢单
					result = auditBsByOperationnote(bsbyId);
					if(!"success".equals(result)){
						service.getDbOp().rollbackTransaction(); 
						request.setAttribute("tip", result);
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					//报溢单与异常入库单关联
					BsByAbnormalBean bean = new BsByAbnormalBean();
					bean.setAbnormalId(abnormalId);
					bean.setBsbyId(bsbyId);
					if(service.addBsByAbnormal(bean)){
						if(!service.updateWarehousingAbnormal(" status=" + WarehousingAbnormalBean.STATUS2, " id=" + abnormalId)){
							service.getDbOp().rollbackTransaction(); 
							request.setAttribute("tip", "更新异常入库单状态失败!");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}else{
						service.getDbOp().rollbackTransaction(); 
						request.setAttribute("tip", "报溢单与异常入库单关联失败!");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					bsbyCount ++;
				}
			}
			String query = "select distinct ap.order_code from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id = " + abnormalId + "";
			ResultSet rs = service.getDbOp().executeQuery(query);
			if (rs.next()) {
				if (!packageLog.addReturnPackageLog("提交异常入库单", user, rs.getString(1))) {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "添加退货日志失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			bsbyResult = "成功生成" + bsbyCount + "张报损报溢单！";
			service.getDbOp().commitTransaction(); //提交事务
		} catch (Exception e) {
			service.getDbOp().rollbackTransaction(); //事务回滚
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			service.releaseAll();
			cargoService.releaseAll();
		}
		return new ActionForward("/admin/warehousingAbnormalAction.do?method=selectAbnormalList&bsbyResult=" + bsbyResult );
	}
	/**
	 * 	说明：1.验证是异常单还是理赔单
	 *	日期：2013-4-17
	 * 	作者：石远飞
	 */
	public boolean checkAbnormalOrCompensate(Map<String,voProduct> vpMap,Map<String,voProduct> rpMap) throws Exception {
		synchronized (lock) {
			boolean flag = false;
			if(!rpMap.isEmpty() && rpMap!= null && !vpMap.isEmpty() && vpMap != null){
				if(vpMap.size()>=rpMap.size()){
					for(Map.Entry<String, voProduct> rpEntry : rpMap.entrySet()){
						voProduct vpBean = vpMap.get(rpEntry.getKey());
						if(vpBean != null){
							if(rpEntry.getValue().getCount()>vpBean.getCount()){
								flag = true;
								break;
							}
						}else flag = true;
					}
				}else flag = true;
			}
			return flag;
		}
	}
	/**
	 * 	说明：1.打印异常入库单详细信息
	 *	日期：2013-4-16
	 * 	作者：石远飞
	 **/
	public ActionForward printWarehousingAbnormal(ActionMapping mapping,ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录,操作失败!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(753) && !group.isFlag(754) && !group.isFlag(755)){
			request.setAttribute("tip", "对不起,你没有打印异常入库单权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		String abnormalId = StringUtil.convertNull((String)request.getParameter("abnormalId"));
		ResultSet rs = null;
		try {
			WarehousingAbnormalBean anormalBean = service.getWarehousingAbnormal("id=" + abnormalId);
			if(anormalBean == null){
				request.setAttribute("tip", "异常入库单不存在!");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List<voProduct> vpList = getVOProductList(anormalBean.getOrderId());
			List<voProduct>	rpList = new ArrayList<voProduct>();
			String query = " select product_code,product_name,product_count,product_oriname,product_id from abnormal_real_products" +
						" where abnormal_id=" + anormalBean.getId();
			rs = service.getDbOp().executeQuery(query);
			while(rs.next()){
				voProduct bean = new voProduct();
				bean.setCode(rs.getString(1));
				bean.setName(rs.getString(2));
				bean.setCount(rs.getInt(3));
				bean.setOriname(rs.getString(4));
				bean.setId(rs.getInt(5));
				rpList.add(bean);
			}
			AuditPackageBean apBean = service.getAuditPackage(" order_id=" + anormalBean.getOrderId());
			query = "select distinct wa.code,wa.create_time,wa.status,ap.order_code,ap.package_code,ap.deliver,ap.sorting_datetime," +
					"wa.operator_name,wa.id from warehousing_abnormal wa left join audit_package ap on wa.order_id=ap.order_id " +
					"where wa.id=" + abnormalId ;
			rs = service.getDbOp().executeQuery(query);
			WarehousingAbnormalBean abnormalBean = new WarehousingAbnormalBean();
			if (rs.next()) {
				abnormalBean.setCode(rs.getString(1));
				abnormalBean.setCreateTime(rs.getString(2));
				abnormalBean.setStatus(rs.getInt(3));
				abnormalBean.setOrderCode(rs.getString(4));
				abnormalBean.setPackageCode(rs.getString(5));
				abnormalBean.setDeliver(rs.getInt(6));
				abnormalBean.setSortingDatetime(rs.getString(7));
				abnormalBean.setOperatorName(rs.getString(8));
				abnormalBean.setId(rs.getInt(9));
			}
			List<WarehousingAbnormalBean> bsbyList = new ArrayList<WarehousingAbnormalBean>();
			query ="select bba.abnormal_id,bbo.receipts_number,bbo.type,bbo.id,bbo.current_type,bbo.operator_id " +
					"from bsby_abnormal bba left join bsby_operationnote bbo on bba.bsby_id = bbo.id " +
					" where bba.abnormal_id=" + abnormalBean.getId();
			rs = service.getDbOp().executeQuery(query);
			while (rs.next()) {
				WarehousingAbnormalBean bsbyBean = new WarehousingAbnormalBean();
				bsbyBean.setId(rs.getInt(1));
				bsbyBean.setReceiptsNumber(rs.getString(2));
				bsbyBean.setBsbyType(rs.getInt(3));
				bsbyBean.setBsbyId(rs.getInt(4));
				bsbyBean.setBsbyStatus(rs.getInt(5));
				bsbyBean.setBsbyOperatorId(rs.getInt(6));
				bsbyList.add(bsbyBean);
			}
			request.setAttribute("abnormalBean", abnormalBean);
			request.setAttribute("bsbyList", bsbyList);
			request.setAttribute("vpList", vpList);
			request.setAttribute("rpList", rpList);
			request.setAttribute("apBean", apBean);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("tip", "程序异常 !");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return mapping.findForward("printWarehousingAbnormal");
	}
	/**
	 * 	说明：1.检查session里的对象是超时
	 *	日期：2013-4-19
	 * 	作者：石远飞
	 **/
	@SuppressWarnings("unchecked")
	public boolean isTimeout(HttpServletRequest request ) throws Exception {
		boolean flag = false;
		AuditPackageBean apBean = (AuditPackageBean) request.getSession().getAttribute("apBean");
		if(apBean == null){
			flag = true;
		}
		String wareArea = (String) request.getSession().getAttribute("wareArea");
		if(wareArea == null){
			flag = true;
		}
		List<voProduct> rpList = (ArrayList<voProduct>)request.getSession().getAttribute("rpList");
		if(rpList == null){
			flag = true;
		}
		List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
		if(vpList == null){
			flag = true;
		}
		return flag;
	}
	/**
	 *	说明：1.生成报损报溢单 返回一个报损报溢单id
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public int addBsByOperationnote(voUser user,int wareArea,
			int bsbyType,int wareType)throws Exception {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, null);
		String receipts_number = "";
		String title = "";// 日志的内容
		int typeString = 0;
		int bsbyId = -1;
		if (bsbyType == 0) {
			// 报损
			String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code);// BS+年月日+3位自动增长数
			title = "创建新的报损表" + receipts_number;
			typeString = 0;
		} else {
			String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code);// BY+年月日+3位自动增长数
			title = "创建新的报溢表" + receipts_number;
			typeString = 1;
		}
		String nowTime = DateUtil.getNow();
		BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
		bsbyOperationnoteBean.setAdd_time(nowTime);
		bsbyOperationnoteBean.setCurrent_type(0);
		bsbyOperationnoteBean.setOperator_id(user.getId());
		bsbyOperationnoteBean.setOperator_name(user.getUsername());
		bsbyOperationnoteBean.setReceipts_number(receipts_number);
		bsbyOperationnoteBean.setWarehouse_area(wareArea);
		bsbyOperationnoteBean.setWarehouse_type(wareType);
		bsbyOperationnoteBean.setType(typeString);
		bsbyOperationnoteBean.setIf_del(0);
		bsbyOperationnoteBean.setFinAuditId(0);
		bsbyOperationnoteBean.setFinAuditName("");
		bsbyOperationnoteBean.setFinAuditRemark("");
		bsbyOperationnoteBean.setRemark("商品错漏发");
		try {
			service.getDbOp().startTransaction(); //开启事务
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
			bsbyOperationnoteBean.setId(maxid + 1);
			if (service.addBsbyOperationnoteBean(bsbyOperationnoteBean)) {
				bsbyId = Integer.valueOf(bsbyOperationnoteBean.getId());
				// 添加操作日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(nowTime);
				bsbyOperationRecordBean.setInformation(title);
				bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean)){
					service.getDbOp().rollbackTransaction();//事务回滚
				}
			}else{
				service.getDbOp().rollbackTransaction(); //事务回滚
			}
			service.getDbOp().commitTransaction(); //提交事务
		} catch (Exception e) {
			service.getDbOp().rollbackTransaction(); //事务回滚
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return bsbyId;
	}
	/**
	 *	说明：1.根据订单号获取商品列表
	 *	日期：2013-4-17
	 *	作者：石远飞
	 */
	public List<voProduct> getVOProductList(int orderId) throws Exception {
		DbOperation db = new DbOperation();
		db.init();
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);
		List<voProduct> vpList = new ArrayList<voProduct>();
		ResultSet rs = null;
		try {
			OrderStockBean osBean = service.getOrderStock("order_id="+ orderId+" and status<>3");
			if (osBean != null) {
				String sql = "select p.code,p.name,p.oriname,osp.stockout_count from product p left join order_stock_product osp " +
						  "on p.code=osp.product_code where osp.order_stock_id="+ osBean.getId();
				rs = service.getDbOp().executeQuery(sql);
				while(rs.next()){
					voProduct vpBean = new voProduct();
					vpBean.setCode(rs.getString(1));
					vpBean.setName(rs.getString(2));
					vpBean.setOriname(rs.getString(3));
					vpBean.setCount(rs.getInt(4));
					vpList.add(vpBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return vpList;
	}
	/**
	 *	说明：1.审核报损报溢单
	 *	日期：2013-4-23
	 *	作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	public String auditBsByOperationnote(int bsbyId) throws Exception {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, null);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String result = "success";
		try {
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + bsbyId);
			if(bean == null){
				result = "报损报溢单不存在!";
				return result;
			}
			//报损单中的所有产品
			List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
			Iterator it = bsbyList.iterator();
			service.getDbOp().startTransaction();
			if(bean.getType() == 0){
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						result = "货位信息异常,操作失败,请与管理员联系!";
						return result;
					}
					String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
					+ "area = " + bean.getWarehouse_area() + " and type = "
					+ bean.getWarehouse_type();
					ProductStockBean psBean = psService.getProductStock(sql);
					//减少库存
					if(!psService.updateProductStockCount(psBean.getId(), -bsbyProductBean.getBsby_count())){
						result = "库存操作失败,可能是库存不足,请与管理员联系!";
						return result;
					}
					//增加库存锁定量
					if (!psService.updateProductLockCount(psBean.getId(), bsbyProductBean.getBsby_count())) {
						result = "库存操作失败,可能是库存不足,请与管理员联系!";
						return result;
					}

					//锁定货位库存
					//出库
					if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
						service.getDbOp().rollbackTransaction();
						result = "货位库存操作失败,货位库存不足!";
						return result;
					}
					if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
						service.getDbOp().rollbackTransaction();
						result = "货位库存操作失败，货位库存不足!";
						return result;
					}
				}
			}else if(bean.getType() == 1){
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						result = "货位信息异常,操作失败,请与管理员联系!";
						return result;
					}

					//锁定货位空间
					if(cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId()+" and status = 0")==null){
						result = "目的货位不存在或已被清空，操作失败，请与管理员联系!";
						return result;
						
					}
					if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count + "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
						service.getDbOp().rollbackTransaction();
						result = "操作失败!";
						return result;
					}
				}
			}
			if(!service.updateBsbyOperationnoteBean(" current_type=1", " id=" + bsbyId)){
				result = "更新报损报溢单状态是失败!";
				return result;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			result = "程序异常!";
			return result;
		} finally {
			service.releaseAll();
		}
		return result;
	}
	/**
	 * 	说明：1.向报损报溢单中添加商品 返回一个字符串判断是否成功及记录错误信息
	 * 	日期：2013-4-15
	 * 	作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	public String addByBsProduct(voUser user, String productCode,String cargoCode,
			int count,int bsbyId)throws Exception {
		String result = null;
		WareService wareService = new WareService();
		voProduct product = wareService.getProduct(productCode);
		if (product == null) {
			result = "[" + productCode+ "]不存在这个编号的产品!";
			return result;
		}
		if (product.getParentId1() == 106) {
			result = "[" + productCode+ "]该商品为新商品,请先修改该产品的分类";
			return result;
		}
		if (product.getIsPackage() == 1) {
			result = "[" + productCode+ "]该产品为套装产品,不能添加!";
			return result;
		}
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try {
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + bsbyId);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail){
				result =  "单据已提交审核,无法修改!";
				return result;
			}
			
			if (service.getBsbyProductBean("operation_id = " + bsbyId + " and product_code = " + productCode) != null) {
				result = "[" + productCode+ "]该产品已经添加,直接修改即可,不用重复添加!";
				return result;
			}
			if (service.getBsbyProductBean("operation_id = " + bsbyId ) != null) {
				result = "只能添加一个商品 所以不能重复提交!";
				return result;
			}
			BsbyOperationnoteBean ben = service.getBsbyOperationnoteBean("id=" + bsbyId);
			int x = getProductCount(product.getId(), ben.getWarehouse_area(), ben.getWarehouse_type());
			int n = updateProductCount(x, ben.getType(), count);
			if (n < 0 ) {
				result = "您所添加商品的库存不足!";
				return result;
			}
			//新货位管理判断
			CargoProductStockBean cps = null;
			if(ben.getType()==0){
		        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
		        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+ben.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	result = "货位号"+cargoCode+"无效,请重新输入!";
		            return result;
		        }
		        cps = (CargoProductStockBean)cpsOutList.get(0);
		        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cps.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	result = "合格库缓存区暂时不能进行报损报溢操作!";
		            return result;
		        }
		        if(count > cps.getStockCount()){
		        	result = "该货位"+cargoCode+"库存为" + cps.getStockCount() + ",库存不足!";
		            return result;
		        }
			}else{
				CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
				CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ben.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+cargoCode+"' and status <> "+CargoInfoBean.STATUS3);
		        if(cargo == null){
		        	result = "货位号"+cargoCode+"无效,请重新输入!";
		            return result;
		        }
		        if(cargo.getStatus() == CargoInfoBean.STATUS2){
		        	result = "货位"+cargoCode+"未开通,请重新输入!";
		            return result;
		        }
		        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cargo.getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	result = "合格库缓存区暂时不能进行报损报溢操作!";
		            return result;
		        }
				List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	if(cargo.getStatus() == CargoInfoBean.STATUS0 && (cargo.getStoreType() == CargoInfoBean.STORE_TYPE0||cargo.getStoreType() == CargoInfoBean.STORE_TYPE4)){
		        		result = "货位"+cargoCode+"被其他商品使用中,添加失败!";
			            return result;
		        	}
		        	cps = new CargoProductStockBean();
		        	cps.setCargoId(cargo.getId());
		        	cps.setProductId(product.getId());
		        	cps.setStockCount(0);
		        	cps.setStockLockCount(0);
		        	service.getDbOp().startTransaction(); //开启事务 需要连接检查
		        	if(!cargoService.addCargoProductStock(cps)){
		        		service.getDbOp().rollbackTransaction(); //事务回滚
		        		result = "生成报损报溢单数据库异常!(cargoService.addCargoProductStock(cps))";
						return result;
		        	}
		        	cps.setId(cargoService.getDbOp().getLastInsertId());
		        	if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId())){
		        		service.getDbOp().rollbackTransaction(); //事务回滚
		        		result = "生成报损报溢单数据库异常!(cargoService.updateCargoInfo())";
						return result;
		        	}
		        }else{
		        	cps = (CargoProductStockBean)cpsOutList.get(0);
		        }
			}
	        

			BsbyProductBean bsbyProductBean = new BsbyProductBean();
			bsbyProductBean.setBsby_count(count);
			bsbyProductBean.setOperation_id(bsbyId);
			bsbyProductBean.setProduct_code(productCode);
			bsbyProductBean.setProduct_id(product.getId());
			bsbyProductBean.setProduct_name(product.getName());
			bsbyProductBean.setOriname(product.getOriname());
			bsbyProductBean.setAfter_change(n);
			bsbyProductBean.setBefore_change(x);
			float price5 = product.getPrice5();//含税金额
			//不含税金额
			float notaxProductPrice = service.returnFinanceProductPrice(bsbyProductBean.getProduct_id());
			bsbyProductBean.setPrice(price5);
			bsbyProductBean.setNotaxPrice(notaxProductPrice);
			if(service.addBsbyProduct(bsbyProductBean)) {
				result = "success";
			}else{
				service.getDbOp().rollbackTransaction(); //事务回滚
				result = "商品添加失败!";
				return result;
			}
			BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
			bsbyCargo.setBsbyOperId(ben.getId());
			bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
			bsbyCargo.setCount(count);
			bsbyCargo.setCargoProductStockId(cps.getId());
			bsbyCargo.setCargoId(cps.getCargoId());
			service.addBsbyProductCargo(bsbyCargo);
			// 添加日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(user.getId());
			bsbyOperationRecordBean.setOperator_name(user.getUsername());
			bsbyOperationRecordBean.setTime(DateUtil.getNow());
			bsbyOperationRecordBean.setInformation("给单据(id):" + bsbyId+ "添加商品:" + productCode + "数量：" + count);
			bsbyOperationRecordBean.setOperation_id(bsbyId);
			if(!service.addBsbyOperationRecord(bsbyOperationRecordBean)){
				service.getDbOp().rollbackTransaction(); //事务回滚
				result = "日志添加失败!";
				return result;
			}
			service.getDbOp().commitTransaction(); //提交事务
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
			wareService.releaseAll();
			cargoService.releaseAll();
		}
		return result;
	}
	/**
	 * 	说明：1.判断订单编号是否符合条件 返回boolean型 条件为：1订单已退回2退货库与选择的库地区是否匹配
	 *	日期：2013-4-17
	 * 	作者：石远飞
	 * @throws SQLException 
	 */
	public String checkOrder(String orderCode,String wareArea) throws SQLException{
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WarehousingAbnormalService service = ServiceFactory.createWarehousingAbnormalService(IBaseService.CONN_IN_SERVICE,db);	
		String result = "ok";
		ResultSet rs = null;
		String query = null;
		try {
			query = "select uo.id,uo.code from user_order uo left join user_order_status uos on uo.status=uos.id left join " +
					 "returned_package rp on uo.id=rp.order_id where uo.code='" + StringUtil.toSql(orderCode) + "'";
				rs = service.getDbOp().executeQuery(query);
				if(!rs.next()){
					result = "订单或包裹单不存在！";
					return result;
				}
			query = "select uo.id,uo.code from user_order uo left join user_order_status uos on uo.status=uos.id left join " +
				 "returned_package rp on uo.id=rp.order_id where uo.code='" + StringUtil.toSql(orderCode) + "' and uo.status=11";
			rs = service.getDbOp().executeQuery(query);
			if(!rs.next()){
				result = "该订单未入库  不能添加异常入库单！";
				return result;
			}
			query = "select uo.id,uo.code from user_order uo left join user_order_status uos on uo.status=uos.id left join " +
				"returned_package rp on uo.id=rp.order_id where uo.code='" + StringUtil.toSql(orderCode) + "' and rp.area=" + wareArea;
			rs = service.getDbOp().executeQuery(query);
			if(!rs.next()){
				result = "该订单退货库与选择库地区不符！";
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(rs != null){
				rs.close();
			}
			service.releaseAll();
		}
		return result;
	}
	/**
	 *	说明：1.生成报损报溢单号
	 *	日期：2013-4-15
	 *	作者：石远飞
	 */
	public static String createCode(String code) {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0 and receipts_number like '" + code + "%'");
			BsbyOperationnoteBean plan;
			plan = service.getBuycode("receipts_number like '" + code + "%'");
			if (plan == null) {
				// 当日第一份计划,编号最后三位 001
				code += "0001";
			} else {
				// 获取当日计划编号最大值
				plan = service.getBuycode("id =" + maxid);
				String _code = plan.getReceipts_number();
				int number = Integer.parseInt(_code.substring(_code.length() - 4));
				number++;
				code += String.format("%04d", new Object[] { new Integer(number) });
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

		return code;
	}
	/**
	 * 	说明：1.根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 
	 * 	日期：2013-04-17
	 * 	作者：石远飞
	 */
	public static int getProductCount(int productid, int area, int type) {
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		int x = 0;
		try {
			voProduct product = wareService.getProduct(productid);
			product.setPsList(psService.getProductStockList("product_id=" + productid, -1, -1, null));
			x = product.getStock(area, type);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return x;

	}
	/**
	 * 	说明：1.得到报损或者报溢后的产品的数量 
	 * 	日期：2013-4-15
	 * 	作者：石远飞
	 */
	public static int updateProductCount(int x, int type, int count) {
		int result = 0;
		if (type == 0) {
			// 报损
			result = x - count;
		} else {
			result = x + count;
		}
		return result;
	}
	/**
	 *	说明：1.将list里的内容封装到map中 返回一个map
	 *	日期：2013-4-16
	 * 	作者：石远飞
	 */
	public Map<String,voProduct> listTurnMap(List<voProduct> list) {
		synchronized (lock) {
			Map<String, voProduct> map = new HashMap<String, voProduct>();
			if(list!=null && list.size()>0){
				for(voProduct bean:list){
					map.put(bean.getCode(), bean);
				}
			}
			return map;
		}
	}
}
