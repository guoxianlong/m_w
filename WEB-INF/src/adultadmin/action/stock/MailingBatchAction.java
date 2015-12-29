package adultadmin.action.stock;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.finance.balance.BalanceService;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.balance.MailingBalanceAuditingBean;
import adultadmin.bean.balance.MailingBalanceAuditingLogBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.cargo.CargoDeptBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.MailingBatchBean;
import adultadmin.bean.stock.MailingBatchPackageBean;
import adultadmin.bean.stock.MailingBatchParcelBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class MailingBatchAction extends DispatchAction {

	public static byte[] MailingBatchLock = new byte[0];
	
	/**
	 * 石远飞（修改）添加权限遮罩
	 * 
	 * 2013-3-29
	 * 
	 * 发货波次管理
	 */
	public ActionForward mailingBatchList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(429)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String select = StringUtil.convertNull(request.getParameter("select"));
		String condition = StringUtil.convertNull(request.getParameter("condition1")).trim();
		String total = "";
		String outCount = "";
		String inCount = "";
		try {
			String startTime = DateUtil.formatDate(new Date());
			String endTime = DateUtil.formatDate(new Date());
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(df.parse(endTime));
			calendar.add(Calendar.DATE, +1);    //得到后一天
			endTime = df.format(calendar.getTime());
			String checkSql = "select count(distinct ap.order_code) from audit_package ap left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "'";
			ResultSet checkRs = service.getDbOp().executeQuery(checkSql);
			while(checkRs.next()){
				total = checkRs.getInt(1) + "";
			}
			String outSql = "select count(distinct mbp.order_code) from mailing_batch_package mbp left join audit_package ap on mbp.order_code=ap.order_code " +
					" left join mailing_batch mb on mb.id=mbp.mailing_batch_id "+
					" left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "'";
			ResultSet outRs = service.getDbOp().executeQuery(outSql);
			while (outRs.next()) {
				outCount = outRs.getInt(1) + "";
			}

			inCount=(Integer.parseInt(total)-Integer.parseInt(outCount))+"";
			String flag="false";
			CargoStaffBean csBean = cargoService.getCargoStaff("status=0 and user_id=" + user.getId());
			if (csBean != null) {
				flag="true";
			}
			if (request.getMethod().equalsIgnoreCase("get")){
				if(Encoder.decrypt(condition)!=null){
					condition=Encoder.decrypt(condition);
				}
			}
			StringBuilder url = new StringBuilder();
			url.append("mailingBatch.do?method=mailingBatchList&select="+select);
			StringBuilder sql = new StringBuilder();
			sql.append("id>0");
			if(select.equals("0")){//"0"为默认查询条件
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(select.equals("1")){//"1"发货波次号
				sql.append(" and code='"+condition+"'");
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(select.equals("2")){//"2"订单编号
				String query = "select a.code from mailing_batch a left join mailing_batch_parcel b on a.code=b.mailing_batch_code " +
						       "left join mailing_batch_package c on b.code=c.mailing_batch_parcel_code where c.order_code='"+condition+"';";
				ResultSet rs = service.getDbOp().executeQuery(query);	
				String flag1 ="false";
					while (rs.next()) {
						sql.append(" and code='" + rs.getString("a.code") + "'");
						url.append("&condition1=" + Encoder.encrypt(condition));
						flag1="true";
					}
					if(flag1.equals("false")){
						sql.append(" and id=0");
						url.append("&condition1=" + Encoder.encrypt(condition));
					}
			}
			if(select.equals("3")){//"3"配送渠道
				Iterator deliverIter = voOrder.deliverMapAll.entrySet().iterator();
				while (deliverIter.hasNext()) {
					Map.Entry entry = (Map.Entry) deliverIter.next();
					if(entry.getValue().equals(condition)){
						sql.append(" and deliver="+entry.getKey());
						url.append("&condition1=" + Encoder.encrypt(condition));
					}
				}
			}
			if(select.equals("4")){//"4"发货仓库
				sql.append(" and store='"+condition+"'");
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(select.equals("5")){//"5"创建日期
				sql.append(" and left(create_datetime,10)='"+condition+"'");
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			List areaList = CargoDeptAreaService.getCargoDeptAreaList(request); 
			StringBuffer buffer = new StringBuffer();
			if(areaList!=null && areaList.size()>0){
				buffer.append(" and (");
				for(int i=0;i<areaList.size();i++){
					buffer.append( " area=" + areaList.get(i) + " or ");
				}
				buffer.append(" 1=2)");
			}else{
				buffer.append(" and 1=2");
			}
			sql.append(buffer);
			// 分页
			int totalCount = service.getMailingBatchCount(sql.toString());
			int countPerPage = 20;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List mbList = service.getMailingBatchList(sql.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			request.setAttribute("mbList", mbList);
			request.setAttribute("total", total);
			request.setAttribute("outCount", outCount);
			request.setAttribute("inCount", inCount);
			request.setAttribute("flag", flag);
			request.setAttribute("url", url.toString());
			request.setAttribute("paging", paging);
			paging.setPrefixUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("mailingBatchList");
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-3-4 （修改）
	 * 
	 * TODO：快递公司交接首页
	 */
	public ActionForward courierHandover(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		DbOperation dbop = new DbOperation(DbOperation.DB_SLAVE);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop);
		String areaId = StringUtil.convertNull(request.getParameter("area"));
		try {
			StringBuilder url = new StringBuilder();
			StringBuilder Condition = new StringBuilder();
			if(group.isFlag(654)){
				Condition.append(" mb.deliver=9 or ");
			}
			if(group.isFlag(655)){
				Condition.append(" mb.deliver=10 or ");
			}
			if(group.isFlag(656)){
				Condition.append(" mb.deliver=11 or ");
			}
			if(group.isFlag(657)){
				Condition.append(" mb.deliver=12 or ");
			}
			if(group.isFlag(658)){
				Condition.append(" mb.deliver=13 or ");
			}
			if(group.isFlag(659)){
				Condition.append(" mb.deliver=14 or ");
			}
			if(group.isFlag(660)){
				Condition.append(" mb.deliver=16 or ");
			}
			if(group.isFlag(661)){
				Condition.append(" mb.deliver=17 or ");
			}
			if(group.isFlag(662)){
				Condition.append(" mb.deliver=18 or ");
			}
			if(group.isFlag(684)){
				Condition.append(" mb.deliver=19 or ");
			}
			if(group.isFlag(685)){
				Condition.append(" mb.deliver=20 or ");
			}
			if(group.isFlag(686)){
				Condition.append(" mb.deliver=21 or ");
			}
			if(group.isFlag(687)){
				Condition.append(" mb.deliver=22 or ");
			}
			if(group.isFlag(710)){
				Condition.append(" mb.deliver=23 or ");
			}
			if(group.isFlag(711)){
				Condition.append(" mb.deliver=24 or ");
			}
			if(group.isFlag(737)){
				Condition.append(" mb.deliver=25 or ");
			}
			if(group.isFlag(738)){
				Condition.append(" mb.deliver=26 or ");
			}
			if(group.isFlag(739)){
				Condition.append(" mb.deliver=27 or ");
			}
			if(group.isFlag(740)){
				Condition.append(" mb.deliver=28 or ");
			}
			if(group.isFlag(743)){
				Condition.append(" mb.deliver=29 or ");
			}
			if(group.isFlag(744)){
				Condition.append(" mb.deliver=30 or ");
			}
			if(group.isFlag(751)){
				Condition.append(" mb.deliver=31 or ");
			}
			if(group.isFlag(752)){
				Condition.append(" mb.deliver=32 or ");
			}
			if(group.isFlag(763)){
				Condition.append(" mb.deliver=33 or ");
			}
			if(group.isFlag(764)){
				Condition.append(" mb.deliver=34 or ");
			}
			if(group.isFlag(782)){
				Condition.append(" mb.deliver=35 or ");
			}
			if(group.isFlag(783)){
				Condition.append(" mb.deliver=36 or ");
			}
			if(group.isFlag(793)){
				Condition.append(" mb.deliver=37 or ");
			}
			Condition.append(" 1=2 ");
			String totalSql = null;
			if(!"".equals(areaId) && !"-1".equals(areaId)){
				totalSql="select count(distinct mb.code) from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join user_order uo on mbp.order_code=uo.code where  mb.status<>0 and mb.area=" + StringUtil.toSql(areaId) +" and (" + Condition +")";
				url.append("mailingBatch.do?method=courierHandover&area=" + areaId);
				request.setAttribute("areaId", areaId);
			}else{
				totalSql="select count(distinct mb.code) from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join user_order uo on mbp.order_code=uo.code where  mb.status<>0 and (" + Condition +")";
				url.append("mailingBatch.do?method=courierHandover");
			}
			// 分页
			int totalCount = 0;
			ResultSet totalRs = service.getDbOp().executeQuery(totalSql);
			while (totalRs.next()) {
				totalCount = totalRs.getInt(1);
			}
			int countPerPage = 20;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			
			String sql = null;
			if(!"".equals(areaId) && !"-1".equals(areaId)){
				sql="select mb.code,mb.deliver,mb.store,mb.create_admin_name,mb.status,count(distinct uo.code),sum(uo.price),mb.recipient,left(mb.receiver_datetime,19),mb.id from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join audit_package ap on mbp.order_code=ap.order_code left join user_order uo on mbp.order_code=uo.code where mb.status<>0 and mb.area=" + StringUtil.toSql(areaId) + " and ("+ Condition + ") group by mb.code order by mb.status,mb.receiver_datetime desc,ap.check_datetime desc limit "+ paging.getCurrentPageIndex() * countPerPage +"," +countPerPage;
			}else{
				sql="select mb.code,mb.deliver,mb.store,mb.create_admin_name,mb.status,count(distinct uo.code),sum(uo.price),mb.recipient,left(mb.receiver_datetime,19),mb.id from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join audit_package ap on mbp.order_code=ap.order_code left join user_order uo on mbp.order_code=uo.code where mb.status<>0 and ("+ Condition + ") group by mb.code order by mb.status,mb.receiver_datetime desc,ap.check_datetime desc limit "+ paging.getCurrentPageIndex() * countPerPage +"," +countPerPage;
			}
			ResultSet rs = service.getDbOp().executeQuery(sql);
			List<MailingBatchBean> mbList = new ArrayList<MailingBatchBean>();
			while (rs.next()) {
				MailingBatchBean mbBean = new MailingBatchBean();
				mbBean.setCode(rs.getString(1));
				mbBean.setDeliver(rs.getInt(2));
				mbBean.setStore(rs.getString(3));
				mbBean.setCreateAdminName(rs.getString(4));
				mbBean.setStatus(rs.getInt(5));
				mbBean.setOrderCount(rs.getInt(6));
				mbBean.setTotalPrice(rs.getDouble(7));
				mbBean.setRecipient(rs.getString(8));
				mbBean.setReceiverTime(rs.getString(9));
				mbBean.setId(rs.getInt(10));
				mbList.add(mbBean);
			}
			if(rs!=null){
				rs.close();
			}
			request.setAttribute("mbList", mbList);
			request.setAttribute("url", url.toString());
			request.setAttribute("paging", paging);
			paging.setPrefixUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("courierHandoverList");
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-3-4（备注）
	 * 
	 * TODO：快递公司交接确认
	 * 
	 */
	public ActionForward courierHandoverConfirm(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String batchId = StringUtil.convertNull(request.getParameter("batchId"));
		try {
			Date nowTime = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Timestamp receiverTime = Timestamp.valueOf(df.format(nowTime));
		    String set = " recipient= '" + user.getUsername() + "', receiver_datetime='" + receiverTime + "' , status=2";
		    if(!service.updateMailingBatch(set, "id=" + StringUtil.toSql(batchId))){
		    	request.setAttribute("tip", "操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);	
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return  new ActionForward("/admin/mailingBatch.do?method=courierHandover");
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-3-4 （备注）
	 * 
	 * 说明：导出交接清单
	 */
	public ActionForward exportCourierHandover(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String batchCode = StringUtil.convertNull(request.getParameter("batchCode"));
		try {
			String sql = "SELECT distinct mbp.create_datetime,mbp.package_code,mbp.total_price,oc.name,mbp.address,mb.store,pc.city,mbp.order_code,uopt.name, mbp.weight,uo.phone " +
					" from mailing_batch_package mbp left join order_customer oc on mbp.order_code=oc.order_code  left join mailing_batch mb on mbp.mailing_batch_code=mb.code " +
					"left join user_order_extend_info uoei on  uoei.order_code=mbp.order_code  left join province_city pc on uoei.add_id2=pc.id left join user_order uo on mbp.order_code=uo.code " +
					"left join user_order_package_type uopt on uo.product_type=uopt.type_id where mbp.mailing_batch_code='" + StringUtil.toSql(batchCode) +"'";
			List mbpList = new ArrayList();
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				MailingBatchPackageBean mbpBean = new MailingBatchPackageBean();
				mbpBean.setCreateDatetime(rs.getDate(1).toString());
				mbpBean.setPackageCode(rs.getString(2));
				mbpBean.setTotalPrice(rs.getFloat(3));
				mbpBean.setCustomerName(rs.getString(4));
				mbpBean.setAddress(rs.getString(5));
				mbpBean.setStore(rs.getString(6));
				mbpBean.setAddressTo(rs.getString(7));
				mbpBean.setOrderCode(rs.getString(8));
				mbpBean.setOrderType(rs.getString(9));
				mbpBean.setWeight(rs.getFloat(10));
				mbpBean.setPhone(rs.getString(11));
				mbpList.add(mbpBean);
			}
			if(rs!=null){
				rs.close();
			}
			request.setAttribute("mbpList", mbpList);
			request.setAttribute("batchCode", batchCode);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		 return mapping.findForward("exportCourierHandover");
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-3-4 （备注）
	 * 
	 * 说明：快递公司交接确认 ——按波次号查询
	 */
	public ActionForward findCourierHandoverByBatchCode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String batchCode = StringUtil.convertNull(request.getParameter("batchCode")).trim();
		try {
			StringBuilder Condition = new StringBuilder();
			if(group.isFlag(654)){
				Condition.append(" mb.deliver=9 or ");
			}
			if(group.isFlag(655)){
				Condition.append(" mb.deliver=10 or ");
			}
			if(group.isFlag(656)){
				Condition.append(" mb.deliver=11 or ");
			}
			if(group.isFlag(657)){
				Condition.append(" mb.deliver=12 or ");
			}
			if(group.isFlag(658)){
				Condition.append(" mb.deliver=13 or ");
			}
			if(group.isFlag(659)){
				Condition.append(" mb.deliver=14 or ");
			}
			if(group.isFlag(660)){
				Condition.append(" mb.deliver=16 or ");
			}
			if(group.isFlag(661)){
				Condition.append(" mb.deliver=17 or ");
			}
			if(group.isFlag(662)){
				Condition.append(" mb.deliver=18 or ");
			}
			if(group.isFlag(684)){
				Condition.append(" mb.deliver=19 or ");
			}
			if(group.isFlag(685)){
				Condition.append(" mb.deliver=20 or ");
			}
			if(group.isFlag(686)){
				Condition.append(" mb.deliver=21 or ");
			}
			if(group.isFlag(687)){
				Condition.append(" mb.deliver=22 or ");
			}
			if(group.isFlag(710)){
				Condition.append(" mb.deliver=23 or ");
			}
			if(group.isFlag(711)){
				Condition.append(" mb.deliver=24 or ");
			}
			if(group.isFlag(737)){
				Condition.append(" mb.deliver=25 or ");
			}
			if(group.isFlag(738)){
				Condition.append(" mb.deliver=26 or ");
			}
			if(group.isFlag(739)){
				Condition.append(" mb.deliver=27 or ");
			}
			if(group.isFlag(740)){
				Condition.append(" mb.deliver=28 or ");
			}
			if(group.isFlag(743)){
				Condition.append(" mb.deliver=29 or ");
			}
			if(group.isFlag(744)){
				Condition.append(" mb.deliver=30 or ");
			}
			if(group.isFlag(751)){
				Condition.append(" mb.deliver=31 or ");
			}
			if(group.isFlag(752)){
				Condition.append(" mb.deliver=32 or ");
			}
			if(group.isFlag(763)){
				Condition.append(" mb.deliver=33 or ");
			}
			if(group.isFlag(764)){
				Condition.append(" mb.deliver=34 or ");
			}
			if(group.isFlag(782)){
				Condition.append(" mb.deliver=35 or ");
			}
			if(group.isFlag(783)){
				Condition.append(" mb.deliver=36 or ");
			}
			if(group.isFlag(793)){
				Condition.append(" mb.deliver=37 or ");
			}
			Condition.append(" 1=2 ");
			String sql="select mb.code,mb.deliver,mb.store,mb.create_admin_name,mb.status,count(distinct uo.code),sum(uo.price),mb.recipient,left(mb.receiver_datetime,19),mb.id from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
			" left join user_order uo on mbp.order_code=uo.code where  mb.code='" +StringUtil.toSql(batchCode) + "' and mb.status<>0 and (" + Condition + " )";
			ResultSet rs = service.getDbOp().executeQuery(sql);
			List mbList = new ArrayList();
			while (rs.next()) {
				MailingBatchBean mbBean = new MailingBatchBean();
				mbBean.setCode(rs.getString(1));
				mbBean.setDeliver(rs.getInt(2));
				mbBean.setStore(rs.getString(3));
				mbBean.setCreateAdminName(rs.getString(4));
				mbBean.setStatus(rs.getInt(5));
				mbBean.setOrderCount(rs.getInt(6));
				mbBean.setTotalPrice(rs.getDouble(7));
				mbBean.setRecipient(rs.getString(8));
				mbBean.setReceiverTime(rs.getString(9));
				mbBean.setId(rs.getInt(10));
				mbList.add(mbBean);
			}
			if(rs!=null){
				rs.close();
			}
			if(((MailingBatchBean)mbList.get(0)).getCode() == null){
				request.setAttribute("tip", "波次号不存在或你没有查看此波次信息的权限！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("mbList", mbList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("courierHandoverList");
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-3-4（修改）
	 * 
	 * TODO：快递公司交接确认——按快递公司查询
	 */
	public ActionForward findCourierHandoverByDeliver(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbop = new DbOperation(DbOperation.DB_SLAVE);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop);
		String deliver = StringUtil.convertNull(request.getParameter("deliver"));
		String areaId = StringUtil.convertNull(request.getParameter("area"));
		try {
			StringBuilder url = new StringBuilder();
			if(!"".equals(areaId) && !"-1".equals(areaId)){
				url.append("mailingBatch.do?method=findCourierHandoverByDeliver&deliver="+ deliver + "&area=" + areaId);
				request.setAttribute("areaId", areaId);
			}else{
				url.append("mailingBatch.do?method=findCourierHandoverByDeliver&deliver="+ deliver);
			}
			// 分页
			int totalCount = 0;
			String totalSql = null;
			if(!"".equals(areaId) && !"-1".equals(areaId)){
				 totalSql="select count(distinct mb.code) from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join user_order uo on mbp.order_code=uo.code where mb.deliver=" + StringUtil.toSql(deliver) + " and mb.area="+  StringUtil.toSql(areaId) +" and mb.status<>0 ";
			}else{
				 totalSql="select count(distinct mb.code) from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join user_order uo on mbp.order_code=uo.code where mb.deliver=" + StringUtil.toSql(deliver) + " and mb.status<>0 ";
			}
			ResultSet totalRs = service.getDbOp().executeQuery(totalSql);
			while (totalRs.next()) {
				totalCount = totalRs.getInt(1);
			}
			int countPerPage = 20;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			String sql = null;
			if(!"".equals(areaId) && !"-1".equals(areaId)){
				 sql="select mb.code,mb.deliver,mb.store,mb.create_admin_name,mb.status,count(distinct uo.code),sum(uo.price),mb.recipient,left(mb.receiver_datetime,19),mb.id from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join audit_package ap on mbp.order_code=ap.order_code left join user_order uo on mbp.order_code=uo.code where mb.deliver=" + StringUtil.toSql(deliver) + " and mb.area=" + StringUtil.toSql(areaId) +" and mb.status<>0  group by mb.code order by mb.status,mb.receiver_datetime desc,ap.check_datetime desc limit "+ paging.getCurrentPageIndex() * countPerPage +"," +countPerPage;
			}else{
				 sql="select mb.code,mb.deliver,mb.store,mb.create_admin_name,mb.status,count(distinct uo.code),sum(uo.price),mb.recipient,left(mb.receiver_datetime,19),mb.id from mailing_batch mb left join mailing_batch_package mbp on mb.code=mbp.mailing_batch_code " +
						" left join audit_package ap on mbp.order_code=ap.order_code left join user_order uo on mbp.order_code=uo.code where mb.deliver=" + StringUtil.toSql(deliver) + " and mb.status<>0  group by mb.code order by mb.status,mb.receiver_datetime desc,ap.check_datetime desc limit "+ paging.getCurrentPageIndex() * countPerPage +"," +countPerPage;
			}
			ResultSet rs = service.getDbOp().executeQuery(sql);
			List mbList = new ArrayList();
			while (rs.next()) {
				MailingBatchBean mbBean = new MailingBatchBean();
				mbBean.setCode(rs.getString(1));
				mbBean.setDeliver(rs.getInt(2));
				mbBean.setStore(rs.getString(3));
				mbBean.setCreateAdminName(rs.getString(4));
				mbBean.setStatus(rs.getInt(5));
				mbBean.setOrderCount(rs.getInt(6));
				mbBean.setTotalPrice(rs.getDouble(7));
				mbBean.setRecipient(rs.getString(8));
				mbBean.setReceiverTime(rs.getString(9));
				mbBean.setId(rs.getInt(10));
				mbList.add(mbBean);
			}
			if(rs!=null){
				rs.close();
			}
			request.setAttribute("mbList", mbList);
			request.setAttribute("url", url.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("deliver", deliver);
			paging.setPrefixUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("courierHandoverList");
	}
	/**
	 * 
	 * 出库作业明细
	 */
	public ActionForward outWarehouseDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(429)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String	endTime = startTime;
		String total = "";
		String outCount = "";
		String inCount = "";
		try {
			if(startTime.equals("")){
				startTime = DateUtil.formatDate(new Date());
				endTime = DateUtil.formatDate(new Date());
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(df.parse(endTime));
			calendar.add(Calendar.DATE, +1);    //得到后一天
			endTime = df.format(calendar.getTime());
			
			String checkSql = "select count(distinct ap.order_code) from audit_package ap left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "'";
			ResultSet checkRs = service.getDbOp().executeQuery(checkSql);
			while(checkRs.next()){
				total = checkRs.getInt(1) + "";
			}
			String outSql = "select count(distinct mbp.order_code) from mailing_batch_package mbp left join audit_package ap on mbp.order_code=ap.order_code " +
			" left join mailing_batch mb on mb.id=mbp.mailing_batch_id "+
			" left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "'";
			ResultSet outRs = service.getDbOp().executeQuery(outSql);
			while (outRs.next()) {
				outCount = outRs.getInt(1) + "";
			}
//			String inSql = "select count(ap.order_code) from audit_package ap left join user_order uo on ap.order_code=uo.code where ap.order_code not in " +
//					"( SELECT mbp.order_code from mailing_batch_package mbp left join audit_package ap on mbp.order_code=ap.order_code " +
//			" left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "') " +
//					"  and uo.deliver<>0 and  ap.check_datetime  between '" + startTime + "' and '" + endTime + "' ";
//			ResultSet inRs = service.getDbOp().executeQuery(inSql);
//			while (inRs.next()) {
//				inCount = inRs.getInt(1) + "";
//			}
			inCount=(Integer.parseInt(total)-Integer.parseInt(outCount))+"";
			request.setAttribute("total", total);
			request.setAttribute("outCount", outCount);
			request.setAttribute("inCount", inCount);
			request.setAttribute("startTime", startTime);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("outWarehouseDetail");
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-3-4 （备注）
	 * 
	 * 说明：快递公司及订单数量列表 内嵌滑动门里的页面
	 */
	public ActionForward deliverOrderDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(429)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String	endTime = startTime;
		try {
			if(startTime.equals("")){
				startTime = DateUtil.formatDate(new Date());
				endTime = DateUtil.formatDate(new Date());
			}
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(df.parse(endTime));
			calendar.add(Calendar.DATE, +1);    //得到后一天
			endTime = df.format(calendar.getTime());
			List list = new ArrayList();
			if(flag.equals("check")){
				String sql = "select left(ap.check_datetime,10),uo.deliver,count(distinct ap.order_code) from audit_package ap left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "' group by uo.deliver";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while(rs.next()){
					AuditPackageBean apBean = new AuditPackageBean();
					apBean.setCheckDatetime(rs.getString(1));
					apBean.setDeliver(rs.getInt(2));
					apBean.setOrderCount(rs.getInt(3));
					list.add(apBean);
				}
				if(rs!=null){
					rs.close();
				}
			}else if(flag.equals("out")){
				String sql = "select left(ap.check_datetime,10),uo.deliver,count(distinct mbp.order_code) from mailing_batch_package mbp left join audit_package ap on mbp.order_code=ap.order_code " +
				" left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "' group by uo.deliver";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while(rs.next()){
					AuditPackageBean apBean = new AuditPackageBean();
					apBean.setCheckDatetime(rs.getString(1));
					apBean.setDeliver(rs.getInt(2));
					apBean.setOrderCount(rs.getInt(3));
					list.add(apBean);
				}
				if(rs!=null){
					rs.close();
				}
			}else if(flag.equals("in")){
				String sql = "select left(ap.check_datetime,10),uo.deliver,count(distinct ap.order_code) from audit_package ap left join user_order uo on ap.order_code=uo.code where ap.order_code not in " +
				"( SELECT mbp.order_code  from mailing_batch_package mbp left join audit_package ap on mbp.order_code=ap.order_code " +
				" left join user_order uo on ap.order_code=uo.code where uo.deliver<>0 and ap.check_datetime between '" + startTime + "' and '" + endTime + "') " +
				"and uo.deliver<>0 and ap.check_datetime  between '" + startTime + "' and '" + endTime + "'  group by uo.deliver ";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while(rs.next()){
					AuditPackageBean apBean = new AuditPackageBean();
					apBean.setCheckDatetime(rs.getString(1));
					apBean.setDeliver(rs.getInt(2));
					apBean.setOrderCount(rs.getInt(3));
					list.add(apBean);
				}
				if(rs!=null){
					rs.close();
				}
			}
			request.setAttribute("list", list);
			request.setAttribute("flag", flag);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("deliverOrderDetail");
	}
	/**
	 * 修改：石远飞
	 * 
	 * 日期：2013-3-4 
	 * 
	 * 说明：添加发货波次
	 * 
	 */
	public ActionForward addMailingBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		int deliver = StringUtil.StringToId(request.getParameter("deliver"));
		int area = StringUtil.StringToId(request.getParameter("area"));
		if(area == -1){
			request.setAttribute("tip", "请选择归属仓库！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if(deliver == -1){
			request.setAttribute("tip", "请选择配送渠道！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String carrier = StringUtil.convertNull(request.getParameter("carrier1"));
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		CargoDeptBean cdBean = null;
		try {
			// 仓库代码
			CargoStaffBean csBean = cargoService.getCargoStaff("status=0 and user_id=" + user.getId());
			if (csBean != null) {
				cdBean = cargoService.getCargoDept("id=" + csBean.getDeptId());
			}
			if (cdBean != null) {
				CargoDeptBean deptBean = cargoService.getCargoDept("id="+cdBean.getParentId0());
				String storageCode = CargoDeptBean.storeMap.get(deptBean.getCode()).toString();
				// 发货波次号
				String code = "SW" + DateUtil.getNow().substring(2, 4)+DateUtil.getNow().substring(5, 7)+DateUtil.getNow().substring(8, 10)+storageCode;
				// 计算位序号
				int maxid = service.getNumber("id", "mailing_batch", "max", "id > 0 and code like '" + code +"%'");
				MailingBatchBean lastMailingBatch = service.getMailingBatch("code like '" + code + "%'");
				if(lastMailingBatch == null){
					//当日第一份单据，编号最后三位 001
					code += "001";
				}else {
					//获取当日计划编号最大值
					lastMailingBatch = service.getMailingBatch("id =" + maxid); 
					String _code = lastMailingBatch.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-3));
						if(number==999){
							request.setAttribute("tip", "当天波次最大数不能超过999！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					number++;
					code+= String.format("%03d",new Object[]{new Integer(number)});
				}
				MailingBatchBean bean = new MailingBatchBean();
				bean.setCode(code);
				bean.setCreateDatetime(DateUtil.getNow());
				bean.setDeliver(deliver);
				bean.setCreateAdminId(user.getId());
				bean.setCreateAdminName(user.getUsername());
				if(carrier!=null && !carrier.equals("选填")){
					bean.setCarrier(carrier);
				}
				bean.setStatus(0);
				bean.setStore(cdBean.getName());
				bean.setArea(area);
				if(!service.addMailingBatch(bean)){
					request.setAttribute("tip", "创建波次失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				request.setAttribute("load", "load");
			} else {
				request.setAttribute("tip", "您不能进行添加操作！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("addMailingBatch");
	}
	/**
	 * 
	 * 删除发货波次
	 */
	public ActionForward deleteMailBatch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(432)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String condition1 = StringUtil.dealParam(request.getParameter("condition1"));
		if(Encoder.decrypt(condition1)!=null){
			condition1=Encoder.decrypt(condition1);
		}
		String select = StringUtil.dealParam(request.getParameter("select"));
		String id = StringUtil.dealParam(request.getParameter("id"));
		try {
			
			service.getDbOp().startTransaction();
			//删除表 发货波次：mailing_batch数据
			service.deleteMailingBatch("id="+id);
			//删除表 发货邮包：mailing_batch_parcel数据
			service.deleteMailingBatchParcel("mailing_batch_id="+id);
			//删除表 邮包中的包裹：mailing_batch_package 的数据
			service.deleteMailingBatchPackage("mailing_batch_id="+id);
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}
		return new ActionForward("/admin/mailingBatch.do?method=mailingBatchList&condition1="+condition1+"&select="+select);
	}
	/**
	 * 
	 *  货物运输交接单打印
	 */
	public ActionForward cargoTransportDeliveryReceiptPrint(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(435)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String code = StringUtil.dealParam(request.getParameter("id"));
		try {
			List mbpList = new ArrayList();
				String sql = "SELECT a.code,b.code,a.store,count(c.id)counts,sum(c.weight)weight FROM mailing_batch a left join mailing_batch_parcel " +
						" b on a.code=b.mailing_batch_code  left join mailing_batch_package c on b.code=c.mailing_batch_parcel_code where a.code='"+code+"' group by b.id";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					MailingBatchParcelBean batchParcelBean = new MailingBatchParcelBean();
					MailingBatchBean mbb = new MailingBatchBean();
					batchParcelBean.setMailingBatchCode(rs.getString("a.code"));
					batchParcelBean.setCode(rs.getString("b.code"));
					mbb.setStore(rs.getString("a.store"));
					batchParcelBean.setPackageCount(rs.getInt("counts"));
					batchParcelBean.setTotalWeight(rs.getFloat("weight"));
					batchParcelBean.setMbb(mbb);
					mbpList.add(batchParcelBean);
				}
		    MailingBatchBean bean = service.getMailingBatch("code='"+code+"'");
		    if(bean!=null){
		    	request.setAttribute("carrier", bean.getCarrier());
		    }
			request.setAttribute("list", mbpList);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("cargoTransportDeliveryReceiptPrint");
	}
	/**
	 * 
	 *  邮包装袋明细单打印
	 */
	public ActionForward mailingParcelDetailPrint(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(440)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String code = StringUtil.dealParam(request.getParameter("id"));
		try {
			StringBuilder url = new StringBuilder();
		    url.append("mailingBatch.do?method=mailingParcelDetailPrint");
			    List mbpList = new ArrayList();
				String sql = "SELECT distinct pwt.name,a.store,uo.buy_mode,c.create_datetime,c.weight,c.order_code,c.package_code,c.address,c.total_price FROM mailing_batch a "+
						"left join mailing_batch_parcel  b on a.code=b.mailing_batch_code "+
						"left join mailing_batch_package c on b.mailing_batch_code=c.mailing_batch_code "+
						"left join user_order uo on c.order_id=uo.id "+
						"left join order_stock os on os.order_id = uo.id AND os.status!=3 "+
						"LEFT JOIN user_order_package_type pwt ON pwt.type_id = os.product_type "+
						" where c.mailing_batch_parcel_code='"+code+"'";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					MailingBatchPackageBean batchPackageBean = new MailingBatchPackageBean();
					MailingBatchBean mbb = new MailingBatchBean();
					batchPackageBean.setCreateDatetime(rs.getString("c.create_datetime").substring(0,11));
					batchPackageBean.setOrderCode(rs.getString("c.order_code"));
					batchPackageBean.setAddress(rs.getString("c.address"));
					batchPackageBean.setWeight(rs.getFloat("c.weight"));
					batchPackageBean.setTotalPrice(rs.getFloat("c.total_price"));
					batchPackageBean.setBuyMode(rs.getInt("uo.buy_mode"));
					batchPackageBean.setPackageCode(rs.getString("c.package_code"));
					batchPackageBean.setOrderType(rs.getString("pwt.name"));					
					mbb.setStore(rs.getString("a.store"));
					batchPackageBean.setMailingBatchBean(mbb);
					mbpList.add(batchPackageBean);
				}
			request.setAttribute("list", mbpList);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("mailingParcelDetailPrint");
	}
	/**
	 * 
	 *  邮包装袋明细单导出excel
	 */
	public ActionForward mailingParcelDetailExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(440)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		String code = StringUtil.dealParam(request.getParameter("id"));
		try {
			StringBuilder url = new StringBuilder();
		    url.append("mailingBatch.do?method=mailingParcelDetailPrint");
			    List mbpList = new ArrayList();
				String sql = "SELECT distinct f.name,pwt.name,a.store,uo.buy_mode,c.create_datetime,c.weight,c.order_code,c.package_code,c.address,c.total_price,uo.postcode FROM mailing_batch AS a "+
						"JOIN mailing_batch_parcel AS b ON a.code = b.mailing_batch_code "+
						"JOIN mailing_batch_package AS c ON b.id = c.mailing_batch_parcel_id "+
						"JOIN user_order AS uo ON c.order_id = uo.id "+
						"join order_stock os on os.order_id = uo.id AND os.status!=3 "+
						"LEFT JOIN user_order_package_type pwt ON pwt.type_id = os.product_type "+
						"left join deliver_corp_info f ON c.deliver=f.id "+						
						"where b.code='"+code+"'";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				while (rs.next()) {
					MailingBatchPackageBean batchPackageBean = new MailingBatchPackageBean();
					MailingBatchBean mbb = new MailingBatchBean();
					batchPackageBean.setCreateDatetime(rs.getString("c.create_datetime").substring(0,11));
					batchPackageBean.setOrderCode(rs.getString("c.order_code"));
					batchPackageBean.setDeliverName(rs.getString("f.name"));
					batchPackageBean.setAddress(rs.getString("c.address"));
					batchPackageBean.setOrderPostCode(rs.getString("uo.postcode"));
					batchPackageBean.setOrderType(rs.getString("pwt.name"));
					batchPackageBean.setWeight(rs.getFloat("c.weight"));
					batchPackageBean.setTotalPrice(rs.getFloat("c.total_price"));
					batchPackageBean.setPackageCode(rs.getString("c.package_code"));
					batchPackageBean.setBuyMode(rs.getInt("uo.buy_mode"));
					mbb.setStore(rs.getString("a.store"));
					batchPackageBean.setMailingBatchBean(mbb);
					mbpList.add(batchPackageBean);
				}
			request.setAttribute("list", mbpList);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("mailingParcelDetailExcel");
	}
	/**
	 * 更新付款方式
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	public ActionForward changePayType(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(468)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String payType = StringUtil.convertNull(request.getParameter("payType")) ;
		String bachId = StringUtil.convertNull(request.getParameter("bachId"));
		String select = StringUtil.convertNull(request.getParameter("select"));
		String select1 = StringUtil.convertNull(request.getParameter("select1"));
		PagingBean paging = (PagingBean)request.getSession().getAttribute("paging");
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			service.updateMailingBatchPackage("pay_type = "+ payType ," id=" + bachId);
			request.setAttribute("select", select);
			request.setAttribute("select1", select1);
			request.setAttribute("paging", paging);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		
		return mapping.findForward("flashOrderDeliverList");
	}
	/**
	 * 
	 *  订单配送管理
	 */
	public ActionForward orderDeliverList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(468)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		String query = StringUtil.convertNull(request.getParameter("select"));//查询下拉框
		String orderStatus = StringUtil.convertNull(request.getParameter("select1"));//订单状态
		String condition = StringUtil.convertNull(request.getParameter("condition1"));//查询条件文本框
		try {
			if (request.getMethod().equalsIgnoreCase("get")){
				if(Encoder.decrypt(condition)!=null){
					condition=Encoder.decrypt(condition);
				}
			}
			StringBuilder url = new StringBuilder();//分页URL
			StringBuilder sql = new StringBuilder();//查询条件
			url.append("mailingBatch.do?method=orderDeliverList&select="+query);
			sql.append(" mailing_status<>0 and mailing_status<>6 and mailing_status<>8 and balance_status<>3 ");//已返库6和已结算8在此页面不显示
			if(!"".equals(orderStatus)&& !orderStatus.equals("0")){
				sql.append(" and mailing_status="+orderStatus);
				url.append("&orderStatus=" + orderStatus);
			}
			if(query.equals("0")){//"0"为默认查询条件
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(query.equals("1")){//"1"发货波次号
				sql.append(" and mailing_batch_code='"+condition+"'");
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(query.equals("2")){//"2"订单编号
						sql.append(" and order_code='" + condition + "'");
						url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(query.equals("3")){//"3"入库日期
						sql.append(" and left(stock_in_datetime,10)='"+condition+"'");
						url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(query.equals("4")){//"4"投递员姓名
				sql.append(" and post_staff_name='"+condition+"'");
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(query.equals("5")){//"5"接货人账号
				sql.append(" and stock_in_admin_name='"+condition+"'");
				url.append("&condition1=" + Encoder.encrypt(condition));
			}
			if(query.equals("6")){//"6"发货仓
				List mailingBatchList=service.getMailingBatchList("store='"+condition+"'", -1, -1, null);
				String ids="0";
				for(int i=0;i<mailingBatchList.size();i++){
					MailingBatchBean batch=(MailingBatchBean)mailingBatchList.get(i);
					ids+=",";
					ids+=batch.getId()+"";
				}
				sql.append(" and mailing_batch_id in("+ids+")");
		        url.append("&condition1=" + Encoder.encrypt(condition));
			}
			// 分页
			int totalCount = service.getMailingBatchPackageCount(sql.toString());
			int countPerPage = 20;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List mbList = service.getMailingBatchPackageList(sql.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			request.setAttribute("mbList", mbList);
			request.setAttribute("url", url.toString());
			request.setAttribute("paging", paging);
			paging.setPrefixUrl(url.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	    return mapping.findForward("orderDeliverList");
	}
	/**
	 * 
	 * 修改包裹配送状态
	 */
	public ActionForward modifyMailingPackageDeliverStauts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String status = StringUtil.convertNull(request.getParameter("status"));//将要被改成的包裹配送状态
		String packageId =  StringUtil.convertNull(request.getParameter("packageId"));//包裹id
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			
			MailingBatchPackageBean parcel=service.getMailingBatchPackage("id="+packageId);
			if(parcel==null){
				request.setAttribute("tip", "该邮包不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.getDbOp().startTransaction();
			service.updateMailingBatchPackage("mailing_status="+status, "id="+packageId);
			if(status.equals("4")){//如果邮包配送状态由"返库中"变为"(未返库)投递超时"，那么邮包的返库状态变为"未返库"
				service.updateMailingBatchPackage("return_status=0", "id="+packageId);
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		String url="/admin/mailingBatch.do?method=orderDeliverList";
		return new ActionForward(url);
	}
	/**
	 * 
	 * 新到波次配送入库
	 */
	public ActionForward mailingBatchStockIn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(460)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if (request.getMethod().equalsIgnoreCase("get")){
			return mapping.findForward("mailingBatchStockIn");
		}
		String mailingBatchCode = StringUtil.convertNull(request.getParameter("mailingBatchCode"));//波次编号
		String parcelCode =  StringUtil.convertNull(request.getParameter("parcelCode"));//邮包编号 
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			MailingBatchBean batch=service.getMailingBatch("code='"+mailingBatchCode+"'");
			if(batch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(batch.getStatus()!=1){
				request.setAttribute("tip", "该波次未出库！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(service.getMailingBatchParcel("mailing_batch_code='"+mailingBatchCode+"' and code='"+parcelCode+"'")==null){
				request.setAttribute("tip", "发货波次编号与发货邮包编号不匹配！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		String url="/admin/mailingBatch.do?method=packageStockIn&mailingBatchCode="+mailingBatchCode+"&parcelCode="+parcelCode;
		return new ActionForward(url);
	}
	/**
	 * 
	 * 新到包裹入库
	 */
	public ActionForward packageStockIn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(460)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String mailingBatchCode = StringUtil.convertNull(request.getParameter("mailingBatchCode"));// 波次编号
		String parcelCode = StringUtil.convertNull(request.getParameter("parcelCode"));// 邮包编号
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));// 包裹编号
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());

		try {
			if (orderCode.length()!=0) {
				MailingBatchBean batch = service.getMailingBatch("code='" + mailingBatchCode + "'");
				if (batch == null) {
					request.setAttribute("tip", "该发货波次不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				MailingBatchParcelBean parcel = service.getMailingBatchParcel("code='" + parcelCode + "'");
			    if (parcel == null) {
					request.setAttribute("tip", "该邮包不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				voOrder order = wareService.getOrder("code='" + orderCode + "'");
				if (order == null) {
					request.setAttribute("tip", "无此订单！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				MailingBatchPackageBean packageBean = service.getMailingBatchPackage("order_code='" + orderCode + "' and mailing_batch_parcel_code='" + parcelCode + "'");
				if (packageBean == null || packageBean.getBalanceStatus()==3) {
					request.setAttribute("tip", "该包裹不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				MailingBalanceBean mb = baService.getMailingBalance("order_code='" + orderCode + "' and packagenum='" + packageBean.getPackageCode() + "'");
				if (mb == null) {
					request.setAttribute("tip", "订单号与包裹单编号不匹配，请核实！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if (order.getDeliver() != batch.getDeliver()) {
					request.setAttribute("tip", "该订单快递公司与波次物流不匹配！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(batch.getStatus()!=1){
					request.setAttribute("tip", "该波次未入库！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if (packageBean != null && packageBean.getMailingStatus()!=0) {
					request.setAttribute("tip", "该包裹已入库！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				else{
					String upSql="mailing_status=1,stock_in_datetime='"+DateUtil.getNow()+"',stock_in_admin_id="+user.getId()+",stock_in_admin_name='"+user.getUsername()+"'";
					service.getDbOp().startTransaction();
					service.updateMailingBatchPackage(upSql, "order_code='" + orderCode + "'");
					service.getDbOp().commitTransaction();
				}
			}
			request.setAttribute("mailingBatchCode", mailingBatchCode);
			request.setAttribute("parcelCode", parcelCode);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return mapping.findForward("packageStockIn");
	}
	/**
	 * 
	 * 未签收包裹列表
	 */
	public ActionForward notSignInPackageList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(469)){
				request.setAttribute("tip", "您没有权限进行此操作！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			String query = StringUtil.convertNull(request.getParameter("select"));//查询
			String orderStatus = StringUtil.convertNull(request.getParameter("select1"));//订单状态
			String condition = StringUtil.convertNull(request.getParameter("condition1"));
			try {
				
				if (request.getMethod().equalsIgnoreCase("get")){
					if(Encoder.decrypt(condition)!=null){
						condition=Encoder.decrypt(condition);
					}
				}
				StringBuilder url = new StringBuilder();
				StringBuilder sql = new StringBuilder();
				url.append("mailingBatch.do?method=notSignInPackageList&select="+query);
				//未签收包裹列表显示的数据是未入库，且在同一波次中存在不是未入库的包裹
			    //select a.* from mailing_batch_package a,(select mailing_batch_code,sum(case when mailing_status=0 then 0 else 1 end) s ,count(id) i from mailing_batch_package
				//group by mailing_batch_code having s<>0 and s<>i)tt where a.mailing_batch_code=tt.mailing_batch_code and a.mailing_status =0;
				sql.append("mailing_status=0 and balance_status<>3 and deliver in (13,14,16,17,18,19,20,21,22,23,24)");
				if(orderStatus!="" && !orderStatus.equals("0")){
					sql.append(" and mailing_status="+orderStatus);
					url.append("&orderStatus=" + orderStatus);
				}
				if(query.equals("0")){//"0"为默认查询条件
					url.append("&condition1=" + Encoder.encrypt(condition));
				}
				if(query.equals("1")){//"1"订单编号
					sql.append(" and order_code='" + condition + "'");
					url.append("&condition1=" + Encoder.encrypt(condition));
		        }
				if(query.equals("2")){//"2"配送渠道
					String flag = "false";
					Iterator deliverIter = voOrder.deliverMapAll.entrySet().iterator();
					while (deliverIter.hasNext()) {
						Map.Entry entry = (Map.Entry) deliverIter.next();
						if(entry.getValue().equals(condition)){
							sql.append(" and deliver="+entry.getKey());
							url.append("&condition1=" + Encoder.encrypt(condition));
							flag="true";
						}
					}
					if(flag.equals("false")){//如果flag=false说明按照配送渠道查找没有找到相关数据
						sql.append(" and id=0");
						url.append("&condition1=" + Encoder.encrypt(condition));
					}
				}
				if(query.equals("3")){//"3"发货仓
					List mailingBatchList=service.getMailingBatchList("store='"+condition+"'", -1, -1, null);
					String ids="0";
					for(int i=0;i<mailingBatchList.size();i++){
						MailingBatchBean batch=(MailingBatchBean)mailingBatchList.get(i);
						ids+=",";
						ids+=batch.getId()+"";
					}
					sql.append(" and mailing_batch_id in("+ids+")");
			        url.append("&condition1=" + Encoder.encrypt(condition));
				}
				// 分页
				int totalCount = service.getMailingBatchPackageCount(sql.toString());
				int countPerPage = 20;
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				List mbList = service.getMailingBatchPackageList(sql.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
				request.setAttribute("mbList", mbList);
				request.setAttribute("url", url.toString());
				request.setAttribute("paging", paging);
				paging.setPrefixUrl(url.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		    return mapping.findForward("notSignInPackageList");
		}
	/**
	 * 
	 * 未签收包裹导出Excel
	 */
	public ActionForward notSignInPackageExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(469)){
				request.setAttribute("tip", "您没有权限进行此操作！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			String[] packageId = request.getParameterValues("packageId");//查询
			try {
				List mbList=new ArrayList();
				if (packageId != null) {
					for (int i = 0; i < packageId.length; i++) {
						MailingBatchPackageBean bean=service.getMailingBatchPackage("id=" + packageId[i]);
						mbList.add(bean);
					}
				}else{
					request.setAttribute("tip", "请至少选择一个您要导出的包裹序号！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				request.setAttribute("mbList", mbList);
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		    return mapping.findForward("notSignInPackageExcel");
		}
	/**
	 * 
	 * 订单返库管理
	 */
	public ActionForward mailingBalanceOrderReturn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(464)){
				request.setAttribute("tip", "您没有权限进行此操作！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			String query = StringUtil.convertNull(request.getParameter("select"));//查询
			String returnStatus = StringUtil.convertNull(request.getParameter("returnStatus"));//返库状态
			String orderStatus = StringUtil.convertNull(request.getParameter("select1"));//订单状态
			String condition = StringUtil.convertNull(request.getParameter("condition1"));
			try {
				if (request.getMethod().equalsIgnoreCase("get")){
					if(Encoder.decrypt(condition)!=null){
						condition=Encoder.decrypt(condition);
					}
				}
				StringBuilder url = new StringBuilder();
				url.append("mailingBatch.do?method=mailingBalanceOrderReturn&select="+query+"&returnStatus="+returnStatus);
				StringBuilder sql = new StringBuilder();
				sql.append("mailing_status=4 and balance_status<>3 ");//在此页面中显示的是投递超时的数据
				if(orderStatus!="" && !orderStatus.equals("0")){
					sql.append(" and mailing_status="+orderStatus);
					url.append("&orderStatus=" + orderStatus);
				}
				if(query.equals("0")){//"0"为默认查询条件
					url.append("&condition1=" + Encoder.encrypt(condition));
				}
				if(query.equals("1")){//"1"发货波次号
					sql.append(" and mailing_batch_code='"+condition+"'");
					url.append("&condition1=" + Encoder.encrypt(condition));
				}
				if(query.equals("2")){//"2"订单编号
					sql.append(" and order_code='" + condition + "'");
					url.append("&condition1=" + Encoder.encrypt(condition));
		        }
				if(query.equals("3")){//"3"配送渠道
					String flag = "false";
					Iterator deliverIter = voOrder.deliverMapAll.entrySet().iterator();
					while (deliverIter.hasNext()) {
						Map.Entry entry = (Map.Entry) deliverIter.next();
						if(entry.getValue().equals(condition)){
							sql.append(" and deliver="+entry.getKey());
							url.append("&condition1=" + Encoder.encrypt(condition));
							flag="true";
						}
					}
					if(flag.equals("false")){//如果flag=false说明按照配送渠道查找没有找到相关数据
						sql.append(" and id=0");
						url.append("&condition1=" + Encoder.encrypt(condition));
					}
				}
				if(query.equals("4")){//"4"发货仓
					List mailingBatchList=service.getMailingBatchList("store='"+condition+"'", -1, -1, null);
					String ids="0";
					for(int i=0;i<mailingBatchList.size();i++){
						MailingBatchBean batch=(MailingBatchBean)mailingBatchList.get(i);
						ids+=",";
						ids+=batch.getId()+"";
					}
					sql.append(" and mailing_batch_id in("+ids+")");
			        url.append("&condition1=" + Encoder.encrypt(condition));
				}
				if(query.equals("5")){//"5"创建时间
					sql.append(" and left(create_datetime,10)='"+condition+"'");
					url.append("&condition1=" + Encoder.encrypt(condition));
		        }
				if(returnStatus.length()>0&&(!returnStatus.equals("-1"))){
					sql.append(" and return_status="+returnStatus );
					url.append("&condition1=" + Encoder.encrypt(condition));
				}
				// 分页
				int totalCount = service.getMailingBatchPackageCount(sql.toString());
				int countPerPage = 20;
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				List mbList = service.getMailingBatchPackageList(sql.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
				request.setAttribute("mbList", mbList);
				request.setAttribute("url", url.toString());
				request.setAttribute("paging", paging);
				paging.setPrefixUrl(url.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		    return mapping.findForward("mailingBalanceOrderReturn");
		}
	/**
	 * 
	 * 订单返库申请操作
	 */
	public ActionForward mailingBalanceOrderReturnOperate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(464)){
				request.setAttribute("tip", "您没有权限进行此操作！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				String[] check = request.getParameterValues("checkbox");
				service.getDbOp().startTransaction();
				if (check != null) {//订单反库申请操作,配送状态和返库状态均变成返库中
					for (int i = 0; i < check.length; i++) {
						service.updateMailingBatchPackage("return_status=1", "id=" + check[i]);
						service.updateMailingBatchPackage("mailing_status=5", "id=" + check[i]);
					}
				}
				else{
					request.setAttribute("tip", "请至少选择一个订单进行申请反库！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().commitTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
			String url="/admin/mailingBatch.do?method=mailingBalanceOrderReturn";
			return new ActionForward(url);
		}
	/**
	 * 
	 * 投递任务分配
	 */
	public ActionForward packageDeliverDistribute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(462)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if (request.getMethod().equalsIgnoreCase("get")){
			return mapping.findForward("packageDeliverDistribute");
		}
		String orderCode = StringUtil.convertNull(request.getParameter("mailingBatchCode"));//订单编号
		String staffCode =  StringUtil.convertNull(request.getParameter("parcelCode"));//员工编号 
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			MailingBatchPackageBean packageBean=service.getMailingBatchPackage("order_code='"+orderCode+"'");
			if(packageBean==null || packageBean.getBalanceStatus()==3){
				request.setAttribute("tip", "该包裹不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//当包裹的配送状态为未分配，投递中，和投递超时时，可以对包裹进行投递任务分配
			if(packageBean!=null&&packageBean.getMailingStatus()!=1&&packageBean.getMailingStatus()!=2&&packageBean.getMailingStatus()!=4){
				request.setAttribute("tip", "该包裹不能分配！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			CargoStaffBean staffBean=cargoService.getCargoStaff("status=0 and code='"+staffCode+"'");
			if(staffBean==null){
				request.setAttribute("tip", "该员工不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//用来存储该包裹最近一次分派的时间(包裹被分配给上一个投递员的一个小时之内不允许再分配给其他投递员)
			//DateUtil.parseDate(DateUtil.getNow(),"yyyy-mm-dd HH:mm:ss").getTime()/(1000*60)-DateUtil.parseDate(sStartTime,"yyyy-mm-dd HH:mm:ss").getTime()/(1000*60)
			if(DateUtil.parseDate(DateUtil.getNow(),"yyyy-mm-dd HH:mm:ss").getTime()/(1000*60)-DateUtil.parseDate(packageBean.getAssignTime(),"yyyy-mm-dd HH:mm:ss").getTime()/(1000*60)<60){//得到的是分
				request.setAttribute("tip", "该订单1小时内无法再次分配投递任务！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			service.getDbOp().startTransaction();
			service.updateMailingBatchPackage("assign_time='"+DateUtil.getNow()+"', mailing_status=2,post_staff_id="+staffBean.getId()+",post_staff_name='"+staffBean.getUserName()+"'", "order_code='"+orderCode+"'");
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		 return mapping.findForward("packageDeliverDistribute");
	}
	/**
	 * 
	 * 订单包裹退回确认
	 */
	public ActionForward confirmMailingBalanceOrderReturn(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(473)){
				request.setAttribute("tip", "您没有权限进行此操作！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			String query = StringUtil.convertNull(request.getParameter("select"));//查询
			String orderStatus = StringUtil.convertNull(request.getParameter("select1"));//订单状态
			String condition = StringUtil.convertNull(request.getParameter("condition1"));
			try {
				if (request.getMethod().equalsIgnoreCase("get")){
					if(Encoder.decrypt(condition)!=null){
						condition=Encoder.decrypt(condition);
					}
				}
				StringBuilder url = new StringBuilder();
				url.append("mailingBatch.do?method=confirmMailingBalanceOrderReturn&select="+query);
				StringBuilder sql = new StringBuilder();
				sql.append("return_status=1 and balance_status<>3 ");
				if(orderStatus!="" && !orderStatus.equals("0")){
					sql.append(" and mailing_status="+orderStatus);
					url.append("&orderStatus=" + orderStatus);
				}
				if(query.equals("0")){//"0"为默认查询条件
					url.append("&condition1=" + Encoder.encrypt(condition));
				}
				if(query.equals("1")){//"1"发货波次号
					sql.append(" and mailing_batch_code='"+condition+"'");
					url.append("&condition1=" + Encoder.encrypt(condition));
				}
				if(query.equals("2")){//"2"订单编号
					sql.append(" and order_code='" + condition + "'");
					url.append("&condition1=" + Encoder.encrypt(condition));
		        }
				if(query.equals("3")){//"3"配送渠道
					String flag = "false";
					Iterator deliverIter = voOrder.deliverMapAll.entrySet().iterator();
					while (deliverIter.hasNext()) {
						Map.Entry entry = (Map.Entry) deliverIter.next();
						if(entry.getValue().equals(condition)){
							sql.append(" and deliver="+entry.getKey());
							url.append("&condition1=" + Encoder.encrypt(condition));
							flag="true";
						}
					}
					if(flag.equals("false")){//如果flag=false说明按照配送渠道查找没有找到相关数据
						sql.append(" and id=0");
						url.append("&condition1=" + Encoder.encrypt(condition));
					}
				}
				if(query.equals("6")){//"6"发货仓
					List mailingBatchList=service.getMailingBatchList("store='"+condition+"'", -1, -1, null);
					String ids="0";
					for(int i=0;i<mailingBatchList.size();i++){
						MailingBatchBean batch=(MailingBatchBean)mailingBatchList.get(i);
						ids+=",";
						ids+=batch.getId()+"";
					}
					sql.append(" and mailing_batch_id in("+ids+")");
			        url.append("&condition1=" + Encoder.encrypt(condition));
				}
				if(query.equals("5")){//"5"创建时间
					sql.append(" and left(create_datetime,10)='"+condition+"'");
					url.append("&condition1=" + Encoder.encrypt(condition));
		        }
				// 分页
				int totalCount = service.getMailingBatchPackageCount(sql.toString());
				int countPerPage = 20;
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				List mbList = service.getMailingBatchPackageList(sql.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
				request.setAttribute("mbList", mbList);
				request.setAttribute("url", url.toString());
				request.setAttribute("paging", paging);
				paging.setPrefixUrl(url.toString());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		    return mapping.findForward("confirmMailingBalanceOrderReturn");
		}
	/**
	 * 
	 * 订单包裹退回确认操作
	 */
	public ActionForward confirmMailingBalanceOrderReturnOperate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			UserGroupBean group = user.getGroup();
			if(!group.isFlag(473)){
				request.setAttribute("tip", "您没有权限进行此操作！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				String[] check = request.getParameterValues("checkbox");
				service.getDbOp().startTransaction();
				if (check != null) {//订单反库状态改为已返库
					for (int i = 0; i < check.length; i++) {
						service.updateMailingBatchPackage("return_status=2", "id=" + check[i]);
						service.updateMailingBatchPackage("mailing_status=6", "id=" + check[i]);
					}
				}else{
					request.setAttribute("tip", "请至少选择一个订单进行退货入库！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().commitTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
			String url="/admin/mailingBatch.do?method=confirmMailingBalanceOrderReturn";
			return new ActionForward(url);
		}
	/**
	 * 
	 * 发货波次明细
	 */
	public ActionForward mailingBatchDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(430)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		dbOp_slave.init("adult_slave");
		WareService wareService = new WareService(dbOp_slave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		int mailingBatchId=Integer.parseInt(request.getParameter("mailingBatchId"));//发货波次Id
		
		try{
			MailingBatchBean batch=stockService.getMailingBatch("id="+mailingBatchId);
			if(batch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			String params = "mailingBatch.do?method=mailingBatchDetail&mailingBatchId="+mailingBatchId;
			int countPerPage = 20;
			int totalCount = stockService.getMailingBatchParcelCount("mailing_batch_id="+batch.getId());
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			paging.setCurrentPageIndex(pageIndex);
			paging.setPrefixUrl(params);
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1));
			//邮包列表
			List parcelList=stockService.getMailingBatchParcelList("mailing_batch_id="+batch.getId(), pageIndex*countPerPage, countPerPage, "id desc");
			while(parcelList.size()==0&&pageIndex!=0){
				pageIndex=pageIndex-1;
				paging.setCurrentPageIndex(pageIndex);
				parcelList=stockService.getMailingBatchParcelList("mailing_batch_id="+batch.getId(), pageIndex*countPerPage, countPerPage, "id desc");
			}
			for(int i=0;i<parcelList.size();i++){
				MailingBatchParcelBean parcel=(MailingBatchParcelBean)parcelList.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcel.getId()+" and balance_status<>3", -1, -1, null);//包裹列表
				parcel.setMailingBatchPackageList(packageList);
				for(int j=0;j<packageList.size();j++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j);
					float weight=packageBean.getWeight();
					BigDecimal weightB=new BigDecimal(weight);
					BigDecimal parcelTotalWeight=new BigDecimal(parcel.getTotalWeight());
					
					float totalPrice=packageBean.getTotalPrice();
					BigDecimal totalPriceB=new BigDecimal(totalPrice);
					BigDecimal parcelTotalPrice=new BigDecimal(parcel.getTotalPrice());
					
					String orderCode=packageBean.getOrderCode();
					voOrder order=wareService.getOrder("code='"+orderCode+"'");
					if(order!=null){
						packageBean.setOrderId(order.getId());
						packageBean.setBuyMode(order.getBuyMode());
					}
					parcel.setPackageCount(parcel.getPackageCount()+1);
					parcel.setTotalWeight(parcelTotalWeight.add(weightB).floatValue());
					parcel.setTotalPrice(parcelTotalPrice.add(totalPriceB).floatValue());
				}
			}
			List parcelListAll=stockService.getMailingBatchParcelList("mailing_batch_id="+batch.getId(), -1, -1, "id desc");
			batch.setMailingBatchParcelList(parcelList);
			for(int i=0;i<parcelListAll.size();i++){
				MailingBatchParcelBean parcel=(MailingBatchParcelBean)parcelListAll.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcel.getId()+" and balance_status<>3", -1, -1, null);//包裹列表
				parcel.setMailingBatchPackageList(packageList);
				for(int j=0;j<packageList.size();j++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j);
					float weight=packageBean.getWeight();
					BigDecimal weightB=new BigDecimal(weight);
					BigDecimal parcelTotalWeight=new BigDecimal(parcel.getTotalWeight());
					
					float totalPrice=packageBean.getTotalPrice();
					BigDecimal totalPriceB=new BigDecimal(totalPrice);
					BigDecimal parcelTotalPrice=new BigDecimal(parcel.getTotalPrice());
					
					String orderCode=packageBean.getOrderCode();
					voOrder order=wareService.getOrder("code='"+orderCode+"'");
					if(order!=null){
						packageBean.setOrderId(order.getId());
					}
					parcel.setPackageCount(parcel.getPackageCount()+1);
					parcel.setTotalWeight(parcelTotalWeight.add(weightB).floatValue());
					parcel.setTotalPrice(parcelTotalPrice.add(totalPriceB).floatValue());
					batch.setOrderCount(batch.getOrderCount()+1);
					batch.setTotalWeight(new BigDecimal(batch.getTotalWeight()).add(weightB).floatValue());
				}
			}
			request.setAttribute("batch", batch);
			request.setAttribute("paging", paging);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("mailingBatchDetail");
	}
	
	/**
	 * 
	 * 交接完成确认
	 */
	public ActionForward transitMailingBatch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(433)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		String date = DateUtil.getNow();
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		int mailingBatchId=Integer.parseInt(request.getParameter("mailingBatchId"));//发货波次Id
		synchronized (MailingBatchLock) {
		try{
			dbOp.startTransaction();
			MailingBatchBean batch=stockService.getMailingBatch("id="+mailingBatchId);
			if(batch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				dbOp.rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(batch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接！");
				request.setAttribute("result", "failure");
				dbOp.rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//出库后所有邮包状态改为已复核
			List parcelList=stockService.getMailingBatchParcelList("mailing_batch_id="+batch.getId(), -1, -1, null);
			for(int i=0;i<parcelList.size();i++){
				MailingBatchParcelBean parcel=(MailingBatchParcelBean)parcelList.get(i);
				stockService.updateMailingBatchParcel("status=1", "id="+parcel.getId());
			}
			stockService.updateMailingBatch("status=1,transit_datetime='"+date+"',transit_admin_id="+user.getId()+",transit_admin_name='"+user.getUsername()+"'", "id="+mailingBatchId);
			
//			Pattern p1 = Pattern.compile("\\S{0,}(市|区|县)\\S{0,}");
			Pattern p2 = Pattern.compile("\\S{0,}(市|区|县)\\S{0,}(乡|镇)\\S{0,}");
			Pattern p3 = Pattern.compile("\\S{0,}(市|区|县)\\S{0,}(乡|镇)\\S{0,}(村|屯|邮)\\S{0,}");
			List packageList = stockService.getMailingBatchPackageList("mailing_batch_id="+batch.getId(), -1, -1, null);
			for(int i=0;i<packageList.size();i++){
				MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
				voOrder order = wareService.getOrder(packageBean.getOrderId());
				if (order == null) {
					request.setAttribute("tip", "订单不存在");
					request.setAttribute("result", "failure");
					dbOp.rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				String deliverName = (String) voOrder.deliverMapAll.get(packageBean.getDeliver() + "");
				String content = "订单已经出库，正在发往【"+deliverName+"】快递公司途中";
				//加入配送信息
				int id = dbOp.getInt("select id from deliver_order where order_id="+ packageBean.getOrderId() + " order by id desc limit 1");
				if (id == 0) {
					if (!dbOp.executeUpdate("insert into deliver_order(order_id,deliver_no,deliver_info,add_time,post_time) "+
							 "values("+packageBean.getOrderId()+",'"+packageBean.getPackageCode()+"','"+content+"','"+date+"','"+date+"')")) {
						request.setAttribute("tip", "配送信息插入失败");
						request.setAttribute("result", "failure");
						dbOp.rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					id = dbOp.getLastInsertId();
				} else {
					if (!dbOp.executeUpdate("update deliver_order set deliver_info='"+content+"',post_time='"+date+"' where id=" + id)) {
						request.setAttribute("tip", "配送信息更新失败");
						request.setAttribute("result", "failure");
						dbOp.rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				String sqlAddress="select b.add_id1,b.add_id2,b.add_id3,b.add_id4 from user_order a join user_order_extend_info  b on a.code=b.order_code where a.id="+order.getId();
				ResultSet rs = wareService.getDbOp().executeQuery(sqlAddress);
				String province ="";
				String city ="";
				String county ="";
				while (rs.next()) {
					province = rs.getInt("b.add_id1")+"";
					city = rs.getInt("b.add_id2")+"";
					county = rs.getInt("b.add_id3")+"";
				}
				rs.close();
				//查找省
				if(!"".equals(province) && province.length()>0){
					String sqlProvince = "select name from provinces where id="+province;
					ResultSet rsProvince = wareService.getDbOp().executeQuery(sqlProvince);
					while (rsProvince.next()) {
						province = rsProvince.getString("name");
					}
					rsProvince.close();
				}
				//查找市
				if(!"".equals(city) && city.length()>0){
					String sqlProvince = "select city from province_city where id="+city;
					ResultSet rsCity = wareService.getDbOp().executeQuery(sqlProvince);
					while (rsCity.next()) {
						city = rsCity.getString("city");
					}
					rsCity.close();
				}
				//查找区县
				if(!"".equals(county) && county.length()>0){
					String sqlProvince = "select area from city_area where id="+county;
					ResultSet rsCounty = wareService.getDbOp().executeQuery(sqlProvince);
					while (rsCounty.next()) {
						county = rsCounty.getString("area");
					}
					rsCounty.close();
				}
				//配送信息明细
				if (!dbOp.executeUpdate("INSERT INTO `deliver_order_info` (`deliver_id`, `deliver_info`, `deliver_time`, `deliver_state`, `add_time`, `province`, `city`, `district`, `post`) " +
						"VALUES (" + id + ", '" + content + "', unix_timestamp('"+date+"')*1000, '0', unix_timestamp('"+date+"')*1000, '" + province + "', '" + city + "', '" + county + "', '" + order.getPostcode() +"')")) {
					request.setAttribute("tip", "配送信息明细插入失败");
					request.setAttribute("result", "failure");
					dbOp.rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("orderId", order.getId() + "");
				map.put("orderCode", order.getCode());
				map.put("address", order.getAddress());
				String sql = "select ap.areano,uo.order_type from audit_package ap join user_order uo on ap.order_id=uo.id where ap.order_id=" + order.getId();
				ResultSet rs1 = dbOp.executeQuery(sql);
				if (rs1.next()) {
					map.put("areaId", rs1.getInt(1) + "");
					map.put("orderType", rs1.getInt(2) + "");
				} else {
					rs1.close();
					request.setAttribute("tip", "未找到出库仓");
					request.setAttribute("result", "failure");
					dbOp.rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				rs1.close();
				
				Matcher m3 = p3.matcher(map.get("address"));
				if (m3.find()) {
					map.put("timeLevel", "3");
				} else {
					Matcher m2 = p2.matcher(map.get("address"));
					if (m2.find()) {
						map.put("timeLevel", "2");
					} else {
						map.put("timeLevel", "1");
					}
				}
				//时效取值逻辑修改 by liurenhua 2015-06-17
				int time=this.getEffectDeliverTime(dbOp, 1, map.get("orderCode"), map.get("areaId"), map.get("timeLevel"));
				if(time==0){
					time=this.getEffectDeliverTime(dbOp, 2, map.get("orderCode"), map.get("areaId"), map.get("timeLevel"));
					if(time==0){
						time=this.getEffectDeliverTime(dbOp, 3, map.get("orderCode"), map.get("areaId"), map.get("timeLevel"));
						if(time==0){
							time=this.getEffectDeliverTime(dbOp, 4, map.get("orderCode"), map.get("areaId"), map.get("timeLevel"));
						}
					}
				}
				map.put("time",time + "");
				map.put("limitDateTime", DateUtil.getBackHourFromDate(date, -time));
				map.put("limitYesterday", DateUtil.getBackFromDate(map.get("limitDateTime"), 1).substring(0,10));
				if (!dbOp.executeUpdate("insert into effect_order_info(order_id,order_code,address,time_level,time,limit_yesterday,limit_datetime,receive_datetime,order_type) values (" + map.get("orderId") + ",'" + map.get("orderCode") + "','" + StringUtil.dealParam(map.get("address")) + "'," + map.get("timeLevel") + "," + map.get("time") + ",'" + map.get("limitYesterday") + "','" + map.get("limitDateTime") + "','" + date +"'," + map.get("orderType") + ");")) {
					dbOp.rollbackTransaction();
					request.setAttribute("tip", "配送时效订单信息插入失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);	
				}
			}
			dbOp.commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		}
		return new ActionForward("/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId="+mailingBatchId);
	}
	
	/**
	 * 
	 * @descripion 取得订单对应配送时效
	 * @author 刘仁华
	 * @time  2015年6月17日
	 */
	private int getEffectDeliverTime(DbOperation dbOp,int type,String orderCode,String areaId,String timeLevel){
		String sql="";
		//1:省市区  2:省区   3:省市   4:省
		if(type==1){
			sql="select edt.city_area_time,edt.town_time,edt.village_time from user_order_extend_info uoei join effect_deliver_time edt on uoei.add_id1=edt.province_id and uoei.add_id2=edt.province_city_id and uoei.add_id3=edt.city_area_id  where uoei.order_code='" + orderCode + "'  and edt.area_id=" + areaId + "";
		}else if(type==2){
			sql="select edt.city_area_time,edt.town_time,edt.village_time from user_order_extend_info uoei join effect_deliver_time edt on uoei.add_id1=edt.province_id and uoei.add_id3=edt.city_area_id  where uoei.order_code='" + orderCode + "'  and edt.area_id=" + areaId + "";
		}else if(type==3){
			sql="select min(edt.city_area_time),min(edt.town_time),min(edt.village_time) from user_order_extend_info uoei join effect_deliver_time edt on uoei.add_id1=edt.province_id and uoei.add_id2=edt.province_city_id where uoei.order_code='" + orderCode + "'  and edt.area_id=" + areaId + "";
		}else if(type==4){
			sql="select edt.city_area_time,edt.town_time,edt.village_time from user_order_extend_info uoei join effect_deliver_time edt on uoei.add_id1=edt.province_id  where uoei.order_code='" + orderCode + "'  and edt.area_id=" + areaId + "";
		}
		ResultSet result = dbOp.executeQuery(sql);
		int time = 0;
		try {
			if (result.next()) {
				if ("3".equals(timeLevel)) {
					time = result.getInt(3);
				} else if ("2".equals(timeLevel)) {
					time = result.getInt(2);
				} else if ("1".equals(timeLevel)) {
					time = result.getInt(1);
				}
			} else {
				System.out.println("配送时效"+type+"："+DateUtil.getNow()+"-"+orderCode+"-"+timeLevel);
				System.out.println(sql);
			}
			result.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return time;
	}
	
	/**
	 * 
	 * 删除邮包
	 */
	public ActionForward deleteParcel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(437)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		int parcelId=Integer.parseInt(request.getParameter("parcelId"));//邮包Id
		int pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
		int mailingBatchId=0;
		synchronized (MailingBatchLock) {
		try{
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+parcelId);
			if(parcel==null){
				request.setAttribute("tip", "该邮包不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			mailingBatchId=parcel.getMailingBatchId();
			MailingBatchBean mailingBatch=stockService.getMailingBatch("id="+mailingBatchId);
			if(mailingBatch==null){
				request.setAttribute("tip", "发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(mailingBatch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接完成！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			dbOp.startTransaction();
			stockService.deleteMailingBatchParcel("id="+parcelId);
			stockService.deleteMailingBatchPackage("mailing_batch_parcel_id="+parcelId);
			dbOp.commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		}
		return new ActionForward("/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId="+mailingBatchId+"&pageIndex="+pageIndex);
	}
	
	/**
	 * 
	 * 添加发货邮包
	 */
	public ActionForward addMailingBatchParcel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(434)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		int mailingBatchId=Integer.parseInt(request.getParameter("mailingBatchId"));//发货波次Id
		try{
			MailingBatchBean batch=stockService.getMailingBatch("id="+mailingBatchId);
			if(batch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(batch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			MailingBatchParcelBean parcel=new MailingBatchParcelBean();
			parcel.setMailingBatchId(batch.getId());
			parcel.setMailingBatchCode(batch.getCode());
			
			List parcelList=stockService.getMailingBatchParcelList("mailing_batch_id="+mailingBatchId, -1, -1, "id desc");
			if(parcelList.size()>0){
				MailingBatchParcelBean parcelBean=(MailingBatchParcelBean)parcelList.get(0);
				if(parcelBean.getCode().substring(14,16).equals("99")){
					request.setAttribute("tip", "邮包最多添加99个！");
					return new ActionForward("/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId="+mailingBatchId);
				}
			}
			if(parcelList.size()==0){
				parcel.setCode(batch.getCode()+"01");
			}else{
				MailingBatchParcelBean firstParcel=(MailingBatchParcelBean)parcelList.get(0);
				String parcelCode=firstParcel.getCode();
				int parcelNum=Integer.parseInt(parcelCode.substring(parcelCode.length()-2,parcelCode.length()))+1;
				parcel.setCode(batch.getCode()+(parcelNum<10?"0":"")+parcelNum);
			}
			stockService.addMailingBatchParcel(parcel);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return new ActionForward("/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId="+mailingBatchId);
	}
	
	/**
	 * 
	 * 添加邮包中包裹页面
	 */
	public ActionForward toAddPackage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(439)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		int parcelId=Integer.parseInt(request.getParameter("parcelId"));//发货邮包Id
		try{
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+parcelId);
			if(parcel==null){
				request.setAttribute("tip", "该发货邮包不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			MailingBatchBean mailingBatch=stockService.getMailingBatch("id="+parcel.getMailingBatchId());
			if(mailingBatch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(mailingBatch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接完成！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("mailingBatch", mailingBatch);
			request.setAttribute("parcel", parcel);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("toAddPackage");
	}
	
	/**
	 * 
	 * 添加邮包中包裹
	 */
	public ActionForward addMailingBatchPackage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		int mailingBatchPackage=Integer.parseInt(request.getParameter("mailingBatchId"));//发货波次Id
		int parcelId=Integer.parseInt(request.getParameter("parcelId"));//发货邮包Id
		String orderCode=request.getParameter("orderCode");//订单编号
		String packageCode=StringUtil.toSql(request.getParameter("packageCode"));//包裹单编号
		try{
			MailingBatchBean mailingBatch=stockService.getMailingBatch("id="+mailingBatchPackage);
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+parcelId);
			if(mailingBatch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(mailingBatch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接完成！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(parcel==null){
				request.setAttribute("tip", "该发货邮包不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			OrderStockBean orderStockBean = stockService.getOrderStock("order_code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
			voOrder order = null;
			if(orderStockBean != null){
				if(mailingBatch.getArea()!=orderStockBean.getStockArea()){
					request.setAttribute("tip", "订单与发货波次不属于同一地区");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				order = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
			}else{
				request.setAttribute("tip", "出库信息错误！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(order==null){
				request.setAttribute("tip", "无此订单！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			MailingBalanceBean mb=baService.getMailingBalance("order_code='"+order.getCode()+"' and packagenum='"+packageCode+"'");
			if(mb==null){
				request.setAttribute("tip", "订单号与包裹单编号不匹配，请核实！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(order.getDeliver()!=mailingBatch.getDeliver()){
				request.setAttribute("tip", "该订单快递公司与波次物流不匹配！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int packageCount=stockService.getMailingBatchPackageCount("order_code='"+order.getCode()+"' and package_code='"+packageCode+"' and balance_status<>3");
			if(packageCount!=0){
				request.setAttribute("tip", "该包裹已被添加！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			AuditPackageBean apBean=stockService.getAuditPackage("order_code='"+order.getCode()+"'");//用于提取包裹重量
			if(apBean==null){
				request.setAttribute("tip", "包裹信息错误！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			
			MailingBatchPackageBean packageBean=new MailingBatchPackageBean();
			packageBean.setMailingBatchId(mailingBatch.getId());
			packageBean.setMailingBatchCode(mailingBatch.getCode());
			packageBean.setMailingBatchParcelId(parcel.getId());
			packageBean.setMailingBatchParcelCode(parcel.getCode());
			packageBean.setOrderCode(order.getCode());
			packageBean.setPackageCode(packageCode);
			packageBean.setCreateDatetime(DateUtil.getNow());
			packageBean.setAddress(order.getAddress());
			packageBean.setWeight(apBean.getWeight());
			packageBean.setTotalPrice(order.getDprice());
			packageBean.setDeliver(order.getDeliver());
			packageBean.setOrderId(order.getId());
			packageBean.setStockInDatetime("1111-11-11 11:11:11");
			packageBean.setAssignTime("1111-11-11 11:11:11");
			packageBean.setPostStaffId(0);
			packageBean.setPostStaffName("");
			packageBean.setStockInAdminId(0);
			packageBean.setStockInAdminName("");
			packageBean.setMailingStatus(0);
			packageBean.setReturnStatus(0);
			packageBean.setBalanceStatus(0);
			stockService.addMailingBatchPackage(packageBean);
			
			request.setAttribute("mailingBatch", mailingBatch);
			request.setAttribute("parcel", parcel);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("toAddPackage");
	}
	
	/**
	 * 
	 * 删除邮包中包裹
	 */
	public ActionForward deletePackage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(442)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		int packageId=Integer.parseInt(request.getParameter("packageId"));//邮包中包裹Id
		MailingBatchBean mailingBatch=null;
		try{
			MailingBatchPackageBean packageBean=stockService.getMailingBatchPackage("id="+packageId);
			if(packageBean==null){
				request.setAttribute("tip", "该包裹不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+packageBean.getMailingBatchParcelId());
			if(parcel==null){
				request.setAttribute("tip", "该发货邮包不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			mailingBatch=stockService.getMailingBatch("id="+packageBean.getMailingBatchId());
			if(mailingBatch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(mailingBatch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接完成！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			stockService.deleteMailingBatchPackage("id="+packageId);
			
			request.setAttribute("mailingBatch", mailingBatch);
			request.setAttribute("parcel", parcel);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return new ActionForward("/admin/mailingBatch.do?method=mailingBatchDetail&mailingBatchId="+mailingBatch.getId());
	}
	
	/**
	 * 
	 * 包裹重量修正页面
	 */
	public ActionForward toChangePackageWeight(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp_slave = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp_slave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp_slave);
		
		int packageId=Integer.parseInt(request.getParameter("packageId"));//包裹Id
		try{
			MailingBatchPackageBean packageBean=stockService.getMailingBatchPackage("id="+packageId);
			if(packageBean==null){
				request.setAttribute("tip", "该包裹不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+packageBean.getMailingBatchParcelId());
			if(parcel==null){
				request.setAttribute("tip", "该发货邮包不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			MailingBatchBean mailingBatch=stockService.getMailingBatch("id="+packageBean.getMailingBatchId());
			if(mailingBatch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(mailingBatch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接完成！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("mailingBatch", mailingBatch);
			request.setAttribute("parcel", parcel);
			request.setAttribute("package", packageBean);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("toChangePackageWeight");
	}
	
	/**
	 * 
	 * 包裹重量修正
	 */
	public ActionForward changePackageWeight(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		int packageId=Integer.parseInt(request.getParameter("packageId"));//邮包中包裹Id
		
		float weight=0;//修正后的重量
		if(!request.getParameter("weight").equals("输入包裹重量...")){
			weight=Float.parseFloat(request.getParameter("weight"));
		}
		MailingBatchBean mailingBatch=null;
		synchronized (MailingBatchLock) {
		try{
			dbOp.startTransaction();
			MailingBatchPackageBean packageBean=stockService.getMailingBatchPackage("id="+packageId);
			if(packageBean==null){
				request.setAttribute("tip", "该包裹不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+packageBean.getMailingBatchParcelId());
			if(parcel==null){
				request.setAttribute("tip", "该发货邮包不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			mailingBatch=stockService.getMailingBatch("id="+packageBean.getMailingBatchId());
			if(mailingBatch==null){
				request.setAttribute("tip", "该发货波次不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			if(mailingBatch.getStatus()!=0){
				request.setAttribute("tip", "该发货波次已交接完成！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			AuditPackageBean apBean=stockService.getAuditPackage("order_id="+packageBean.getOrderId());
			if(apBean==null){
				request.setAttribute("tip", "数据错误！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			stockService.updateMailingBatchPackage("weight="+weight*1000, "id="+packageId);
			stockService.updateAuditPackage("weight="+weight*1000, "order_id="+packageBean.getOrderId());
			
			request.setAttribute("mailingBatch", mailingBatch);
			request.setAttribute("parcel", parcel);
			request.setAttribute("package", packageBean);
			dbOp.commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		}
		return mapping.findForward("toChangePackageWeight");
	}
	
	/**
	 * 
	 * Ajax,发货波次订单总数更新
	 */
	public ActionForward updateMailingBatchOrderCount(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		int mailingBatchId=Integer.parseInt(request.getParameter("mailingBatchId"));
		
		try{
			int orderCount=stockService.getMailingBatchPackageCount("mailing_batch_id="+mailingBatchId);
			PrintWriter out = response.getWriter();
			out.write(orderCount+"");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 
	 * Ajax,发货波次总重量更新
	 */
	public ActionForward updateMailingBatchTotalWeight(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		int mailingBatchId=Integer.parseInt(request.getParameter("mailingBatchId"));
		
		try{
			List packageList=stockService.getMailingBatchPackageList("mailing_batch_id="+mailingBatchId+" and balance_status<>3",-1,-1,null);
			BigDecimal totalWeight=new BigDecimal(0);
			for(int i=0;i<packageList.size();i++){
				MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
				BigDecimal packageWeight=new BigDecimal(packageBean.getWeight());
				totalWeight=totalWeight.add(packageWeight);
			}
			PrintWriter out = response.getWriter();
			out.write(totalWeight.divide(new BigDecimal(1000)).toString());
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 
	 * Ajax,发货邮包订单总数更新
	 */
	public ActionForward updateMailingBatchParcelOrderCount(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);;
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		int parcelId=Integer.parseInt(request.getParameter("parcelId"));
		
		try{
			int orderCount=stockService.getMailingBatchPackageCount("mailing_batch_parcel_id="+parcelId);
			PrintWriter out = response.getWriter();
			out.write(orderCount+"");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 
	 * Ajax,发货邮包总重量更新
	 */
	public ActionForward updateMailingBatchParcelTotalWeight(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		int parcelId=Integer.parseInt(request.getParameter("parcelId"));
		
		try{
			List packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcelId+" and balance_status<>3",-1,-1,null);
			BigDecimal totalWeight=new BigDecimal(0);
			for(int i=0;i<packageList.size();i++){
				MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
				BigDecimal packageWeight=new BigDecimal(packageBean.getWeight());
				totalWeight=totalWeight.add(packageWeight);
			}
			PrintWriter out = response.getWriter();
			out.write(totalWeight.divide(new BigDecimal(1000)).toString());
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 
	 * Ajax,发货邮包代收货款总额更新
	 */
	public ActionForward updateMailingBatchParcelTotalPrice(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		int parcelId=Integer.parseInt(request.getParameter("parcelId"));
		
		try{
			List packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcelId+" and balance_status<>3",-1,-1,null);
			BigDecimal totalPrice=new BigDecimal(0);
			for(int i=0;i<packageList.size();i++){
				MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
				BigDecimal packageTotalPrice=new BigDecimal(packageBean.getTotalPrice());
				totalPrice=totalPrice.add(packageTotalPrice);
			}
			PrintWriter out = response.getWriter();
			DecimalFormat dcmFmt = new DecimalFormat("0.00");
			out.write(dcmFmt.format(totalPrice.floatValue()));
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 
	 * Ajax,发货邮包包裹列表更新
	 */
	public ActionForward updateMailingBatchPackageList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		int parcelId=Integer.parseInt(request.getParameter("parcelId"));
		
		try{
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+parcelId);
			MailingBatchBean batch=stockService.getMailingBatch("id="+parcel.getMailingBatchId());
			List packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcelId+" and balance_status<>3", -1, -1, null);
			parcel.setMailingBatchPackageList(packageList);
			request.setAttribute("parcel", parcel);
			request.setAttribute("batch", batch);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("mailingBatchPackageList");
	}
	
	/**
	 * 
	 * 订单应收款结算管理
	 */
	public ActionForward mailingBalanceAuditingList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		int balanceType=Integer.parseInt(request.getParameter("balanceType"));
		String keyword=StringUtil.convertNull(request.getParameter("keyword"));
		String condition=StringUtil.convertNull(request.getParameter("condition"));
		
		try{
			String sql="";
			if(condition.equals("1")){//发货波次号
				sql+=" and id in(0";
				List packageList=stockService.getMailingBatchPackageList("mailing_batch_code='"+keyword+"'", -1, -1, "id desc");
				for(int i=0;i<packageList.size();i++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
					MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
					if(mailingBalance!=null&&mailingBalance.getMailingBalanceAuditingId()!=0){
						sql+=",";
						sql+=mailingBalance.getMailingBalanceAuditingId();
					}
				}
				sql+=")";
			}else if(condition.equals("2")){//订单编号
				sql+=" and id in(0";
				MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+keyword+"'");
				if(mailingBalance!=null&&mailingBalance.getMailingBalanceAuditingId()!=0){
					sql+=",";
					sql+=mailingBalance.getMailingBalanceAuditingId();
				}
				sql+=")";
			}else if(condition.equals("3")){//入库日期
				sql+=" and id in(0";
				List packageList=stockService.getMailingBatchPackageList("stock_in_datetime>'"+keyword+" 00:00:00' and stock_in_datetime<'"+keyword+" 23:59:59'", -1, -1, null);
				for(int i=0;i<packageList.size();i++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
					MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
					if(mailingBalance!=null&&mailingBalance.getMailingBalanceAuditingId()!=0){
						sql+=",";
						sql+=mailingBalance.getMailingBalanceAuditingId();
					}
				}
				sql+=")";
			}else if(condition.equals("4")){//投递员姓名
				sql+=" and id in(0";
				List packageList=stockService.getMailingBatchPackageList("post_staff_name='"+keyword+"'", -1, -1, null);
				for(int i=0;i<packageList.size();i++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
					MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
					if(mailingBalance!=null&&mailingBalance.getMailingBalanceAuditingId()!=0){
						sql+=",";
						sql+=mailingBalance.getMailingBalanceAuditingId();
					}
				}
				sql+=")";
			}else if(condition.equals("5")){//接货人账号
				sql+=" and id in(0";
				List packageList=stockService.getMailingBatchPackageList("stock_in_admin_name='"+keyword+"'", -1, -1, null);
				for(int i=0;i<packageList.size();i++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
					MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
					if(mailingBalance!=null&&mailingBalance.getMailingBalanceAuditingId()!=0){
						sql+=",";
						sql+=mailingBalance.getMailingBalanceAuditingId();
					}
				}
				sql+=")";
			}else if(condition.equals("6")){//发货仓库
				sql+=" and id in(0";
				List mailingBatchList=stockService.getMailingBatchList("store='"+keyword+"'",-1,-1,null);
				for(int i=0;i<mailingBatchList.size();i++){
					MailingBatchBean mailingBatch=(MailingBatchBean)mailingBatchList.get(i);
					List packageList=stockService.getMailingBatchPackageList("mailing_batch_id='"+mailingBatch.getId()+"'", -1, -1, null);
					for(int j=0;j<packageList.size();j++){
						MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j);
						MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
						if(mailingBalance!=null&&mailingBalance.getMailingBalanceAuditingId()!=0){
							sql+=",";
							sql+=mailingBalance.getMailingBalanceAuditingId();
						}
					}
				}
				sql+=")";
			}
			
			//全部状态
			int countPerPage = 20;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			int select=StringUtil.StringToId(request.getParameter("select"));//分页中的批次状态参数
			
			int totalCount1 = baService.getMailingBalanceAuditingCount("balance_type="+balanceType+sql);
			PagingBean paging1 = new PagingBean(pageIndex, totalCount1,countPerPage);
			if(select!=1){
				paging1.setCurrentPageIndex(0);
			}
			paging1.setTotalPageCount(totalCount1%countPerPage==0?totalCount1/countPerPage:((totalCount1-totalCount1%countPerPage)/countPerPage+1));
			paging1.setPrefixUrl("/adult-admin/admin/mailingBatch.do?method=mailingBalanceAuditingList&balanceType="+balanceType+"&keyword="+keyword+"&condition="+condition+"&select=1");
			
			
			List  mbaList=null;
			if(select==1){
				mbaList=baService.getMailingBalanceAuditingList("balance_type="+balanceType+sql, countPerPage*pageIndex, countPerPage, "id desc");
			}else{
				mbaList=baService.getMailingBalanceAuditingList("balance_type="+balanceType+sql, 0, countPerPage, "id desc");
			}
			
			for(int i=0;i<mbaList.size();i++){
				MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_balance_auditing_id="+mba.getId(), -1, -1, "id desc");
				mba.setPackageList(packageList);
				for(int j=0;j<packageList.size();j++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j);
					mba.setShouldPay(mba.getShouldPay()+(double)packageBean.getTotalPrice());
				}
			}
			
			//未提交财务
			int totalCount2 = baService.getMailingBalanceAuditingCount("balance_type="+balanceType+" and status=0"+sql);
			PagingBean paging2 = new PagingBean(pageIndex, totalCount2,countPerPage);
			if(select!=2){
				paging2.setCurrentPageIndex(0);
			}
			paging2.setTotalPageCount(totalCount2%countPerPage==0?totalCount2/countPerPage:((totalCount2-totalCount2%countPerPage)/countPerPage+1));
			paging2.setPrefixUrl("/adult-admin/admin/mailingBatch.do?method=mailingBalanceAuditingList&balanceType="+balanceType+"&status=0"+"&keyword="+keyword+"&condition="+condition+"&select=2");
			
			List  mbaList2=null;
			if(select==2){
				mbaList2=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=0"+sql, countPerPage*paging2.getCurrentPageIndex(), countPerPage, "id desc");
			}else{
				mbaList2=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=0"+sql, 0, countPerPage, "id desc");
			}
			
			for(int i=0;i<mbaList2.size();i++){
				MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList2.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_balance_auditing_id="+mba.getId(), -1, -1, "id desc");
				mba.setPackageList(packageList);
			}
			//已提交财务
			int totalCount3 = baService.getMailingBalanceAuditingCount("balance_type="+balanceType+" and status=1"+sql);
			PagingBean paging3 = new PagingBean(pageIndex, totalCount3,countPerPage);
			if(select!=3){
				paging3.setCurrentPageIndex(0);
			}
			paging3.setTotalPageCount(totalCount3%countPerPage==0?totalCount3/countPerPage:((totalCount3-totalCount3%countPerPage)/countPerPage+1));
			paging3.setPrefixUrl("/adult-admin/admin/mailingBatch.do?method=mailingBalanceAuditingList&balanceType="+balanceType+"&status=1"+"&keyword="+keyword+"&condition="+condition+"&select=3");
			
			List  mbaList3=null;
			if(select==3){
				mbaList3=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=1"+sql, countPerPage*paging3.getCurrentPageIndex(), countPerPage, "id desc");
			}else{
				mbaList3=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=1"+sql, 0, countPerPage, "id desc");
			}
			
			for(int i=0;i<mbaList3.size();i++){
				MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList3.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_balance_auditing_id="+mba.getId(), -1, -1, "id desc");
				mba.setPackageList(packageList);
			}
			//审核未通过
			int totalCount4 = baService.getMailingBalanceAuditingCount("balance_type="+balanceType+" and status=2"+sql);
			PagingBean paging4 = new PagingBean(pageIndex, totalCount4,countPerPage);
			if(select!=4){
				paging4.setCurrentPageIndex(0);
			}
			paging4.setTotalPageCount(totalCount4%countPerPage==0?totalCount4/countPerPage:((totalCount4-totalCount4%countPerPage)/countPerPage+1));
			paging4.setPrefixUrl("/adult-admin/admin/mailingBatch.do?method=mailingBalanceAuditingList&balanceType="+balanceType+"&status=2"+"&keyword="+keyword+"&condition="+condition+"&select=4");
			
			List  mbaList4=null;
			if(select==4){
				mbaList4=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=2"+sql, countPerPage*paging4.getCurrentPageIndex(), countPerPage, "id desc");
			}else{
				mbaList4=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=2"+sql, 0, countPerPage, "id desc");
			}
			
			for(int i=0;i<mbaList4.size();i++){
				MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList4.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_balance_auditing_id="+mba.getId(), -1, -1, "id desc");
				mba.setPackageList(packageList);
			}
			//已结算
			int totalCount5 = baService.getMailingBalanceAuditingCount("balance_type="+balanceType+" and status=3"+sql);
			PagingBean paging5 = new PagingBean(pageIndex, totalCount5,countPerPage);
			if(select!=5){
				paging5.setCurrentPageIndex(0);
			}
			paging5.setTotalPageCount(totalCount5%countPerPage==0?totalCount5/countPerPage:((totalCount5-totalCount5%countPerPage)/countPerPage+1));
			paging5.setPrefixUrl("/adult-admin/admin/mailingBatch.do?method=mailingBalanceAuditingList&balanceType="+balanceType+"&status=3"+"&keyword="+keyword+"&condition="+condition+"&select=5");
			
			List  mbaList5=null;
			if(select==5){
				mbaList5=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=3"+sql, countPerPage*paging5.getCurrentPageIndex(), countPerPage, "id desc");
			}else{
				mbaList5=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=3"+sql, 0, countPerPage, "id desc");
			}
			
			for(int i=0;i<mbaList5.size();i++){
				MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList5.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_balance_auditing_id="+mba.getId(), -1, -1, "id desc");
				mba.setPackageList(packageList);
				
			}
			//已作废
			int totalCount6 = baService.getMailingBalanceAuditingCount("balance_type="+balanceType+" and status=4"+sql);
			PagingBean paging6 = new PagingBean(pageIndex, totalCount6,countPerPage);
			if(select!=6){
				paging6.setCurrentPageIndex(0);
			}
			paging6.setTotalPageCount(totalCount6%countPerPage==0?totalCount6/countPerPage:((totalCount6-totalCount6%countPerPage)/countPerPage+1));
			paging6.setPrefixUrl("/adult-admin/admin/mailingBatch.do?method=mailingBalanceAuditingList&balanceType="+balanceType+"&status=4"+"&keyword="+keyword+"&condition="+condition+"&select=6");
			
			List  mbaList6=null;
			if(select==6){
				mbaList6=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=4"+sql, countPerPage*paging6.getCurrentPageIndex(), countPerPage, "id desc");
			}else{
				mbaList6=baService.getMailingBalanceAuditingList("balance_type="+balanceType+" and status=4"+sql, 0, countPerPage, "id desc");
			}
			
			for(int i=0;i<mbaList6.size();i++){
				MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList6.get(i);
				List packageList=stockService.getMailingBatchPackageList("mailing_balance_auditing_id="+mba.getId(), -1, -1, "id desc");
				mba.setPackageList(packageList);
			}
			
			int deliver=0;
			switch(balanceType){
				case 1://北速
					deliver=7;break;
				case 2://广速省内
					deliver=8;break;
				case 3://广州宅急送
					deliver=10;break;
				case 4://广速省外
					deliver=9;break;
				case 5://广东省速递局
					deliver=11;break;
				case 6://广州顺丰
					deliver=12;break;
				case 7://深圳自建
					deliver=13;break;
				case 8://通路速递
					deliver=14;break;
				case 10://如风达
					deliver=16;break;
				case 11://通路速递广东
					deliver=19;break;
				case 12://通路速递浙江
					deliver=20;break;
				case 13://银捷速递
					deliver=21;break;
				case 14://重庆华宇
					deliver=23;break;
				case 15://四川立即送
					deliver=24;break;
				case 16://广西邮政
					deliver=25;break;
				case 17://宅急送四川
					deliver=26;break;
				case 18://宅急送重庆
					deliver=27;break;
				case 19://江西邮政
					deliver=28;break;
				case 20://上海无疆
					deliver=29;break;
				case 21://上海宅急送
					deliver=30;break;
					
			}
			if(deliver==0){
				request.setAttribute("tip", "不能识别快递公司！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			//未申请结算订单数
			int orderCount=stockService.getMailingBatchPackageCount("deliver="+deliver+" and mailing_status=3 and balance_status=0");
			
			request.setAttribute("mbaList", mbaList);
			request.setAttribute("mbaList2", mbaList2);
			request.setAttribute("mbaList3", mbaList3);
			request.setAttribute("mbaList4", mbaList4);
			request.setAttribute("mbaList5", mbaList5);
			request.setAttribute("mbaList6", mbaList6);
			request.setAttribute("orderCount", orderCount+"");
			request.setAttribute("paging1", paging1);
			request.setAttribute("paging2", paging2);
			request.setAttribute("paging3", paging3);
			request.setAttribute("paging4", paging4);
			request.setAttribute("paging5", paging5);
			request.setAttribute("paging6", paging6);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("mailingBalanceAuditingList");
	}
	
	/**
	 * 
	 * 未申请结算订单列表
	 */
	public ActionForward unBalanceOrderList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(478)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		String keyword=StringUtil.convertNull(request.getParameter("keyword")).trim();
		String select=StringUtil.convertNull(request.getParameter("select")).trim();
		if(keyword.length()>0&&(!group.isFlag(479))){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		try{
			if(Encoder.decrypt(keyword)!=null){
				keyword=Encoder.decrypt(keyword);
			}
			
			String condition="";
			if(select.equals("1")){//发货波次号
				condition="mailing_batch_code='"+keyword+"'";
			}else if(select.equals("2")){//订单编号
				condition="order_code='"+keyword+"'";
			}else if(select.equals("3")){//入库日期
				condition="stock_in_datetime>'"+keyword+" 00:00:00' and stock_in_datetime<'"+keyword+"23:59:59'";
			}else if(select.equals("4")){//投递员姓名
				condition="post_staff_name='"+keyword+"'";
			}else if(select.equals("5")){//接货人账号
				condition="stock_admin_name='"+keyword+"'";
			}else if(select.equals("6")){//发货仓库
				List mailingBatchList=stockService.getMailingBatchList("store='"+keyword+"'", -1, -1, null);
				String ids="0";
				for(int i=0;i<mailingBatchList.size();i++){
					MailingBatchBean batch=(MailingBatchBean)mailingBatchList.get(i);
					ids+=",";
					ids+=batch.getId()+"";
				}
				condition="mailing_batch_id in("+ids+")";
			}
			if(condition.length()>0){
				condition+=" and ";
			}
			condition+="mailing_status=3 and balance_status=0 and deliver=13";
			
			int countPerPage = 20;
			int totalCount = stockService.getMailingBatchPackageCount(condition);
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage)	;
			
			List packageList=stockService.getMailingBatchPackageList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
			for(int i=0;i<packageList.size();i++){
				MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
				MailingBatchBean batch=stockService.getMailingBatch("id="+packageBean.getMailingBatchId());
				if(batch!=null){
					packageBean.setMailingBatchBean(batch);
				}
			}
			
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:((totalCount-totalCount%countPerPage)/countPerPage+1));
			paging.setPrefixUrl("mailingBatch.do?method=unBalanceOrderList&keyword="+Encoder.encrypt(keyword)+"&select="+select+"&balanceType=7");
			request.setAttribute("paging", paging);
			request.setAttribute("packageList", packageList);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("unBalanceOrderList");
	}
	
	/**
	 * 
	 * 提交选中订单至结算申请
	 */
	public ActionForward addMailingBalanceAudit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String[] ids = request.getParameterValues("packageId");//包裹单id列表
		if(ids == null){
			request.setAttribute("tip", "请选择要提交的发货波次！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		for(int i=0;i<ids.length;i++){
			if(StringUtil.convertNull(request.getParameter(ids[i])).equals("0")){
				request.setAttribute("tip", "包含未选择的付款方式！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
		}
		if(ids.length > 1){
			for(int i=1;i<ids.length;i++){//比较付款方式是否相同
				if(!StringUtil.convertNull(request.getParameter(ids[i-1])).equals(StringUtil.convertNull(request.getParameter(ids[i])))){
					request.setAttribute("tip", "不同付款方式的订单不能在同一个批次中提交！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		BalanceService balanceService = new  BalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			int balanceType=0;
			if(ids.length>0){//获得balanceType
				MailingBatchPackageBean packageBean=stockService.getMailingBatchPackage("id="+ids[0]);
				MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
				balanceType=mailingBalance.getBalanceType();
			}
			MailingBalanceAuditingBean mba=new MailingBalanceAuditingBean();
			String code="JS"+DateUtil.getNowDateStr().replace(":", "").replace(" ", "").replace("-", "");
			List mbaList=baService.getMailingBalanceAuditingList("code like '"+code+"%'", -1, -1, "id desc");
			if(mbaList.size()>0){
				MailingBalanceAuditingBean tempMba=(MailingBalanceAuditingBean)mbaList.get(0);
				int num=Integer.parseInt(tempMba.getCode().substring(10,13));
				num++;
				code=code+(num<10?("00"+num):(num<100?"0"+num:""+num));
			}else{
				code=code+"001";
			}
			mba.setCode(code);
			mba.setBalanceType(balanceType);
			mba.setBalanceDatetime(DateUtil.getNow().substring(0,10));
			mba.setPayType(Integer.parseInt(StringUtil.convertNull(request.getParameter(ids[0]))));
			baService.addMailingBalanceAuditing(mba);
			
			MailingBalanceAuditingBean newMba=baService.getMailingBalanceAuditing("code='"+code+"'");
			for(int i=0;i<ids.length;i++){
				String payType = StringUtil.convertNull(request.getParameter(ids[i]));
				String id=ids[i];
				MailingBatchPackageBean packageBean=stockService.getMailingBatchPackage("id="+id);
				MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
				stockService.updateMailingBatchPackage("mailing_balance_auditing_id="+newMba.getId()+",mailing_status=7,balance_status=1,pay_type=" + payType, "id="+id);
				baService.updateMailingBalance("mailing_balance_auditing_id="+newMba.getId()+",import_type="+MailingBalanceBean.IMPORT_TYPE_MAILING+",balance_date='"+DateUtil.getNowDateStr()+"',balance_realtime='"+DateUtil.getNow()+"'", "id="+mailingBalance.getId());
				balanceService.updateFinanceMailingBalanceBean("mailing_balance_auditing_id = " + newMba.getId() + ", import_type = " + MailingBalanceBean.IMPORT_TYPE_MAILING + ", balance_date ='" + DateUtil.getNowDateStr() + "', agency_price=" + mailingBalance.getPrice(), "data_type = 0 AND order_code='"+packageBean.getOrderCode()+"'");
			}
			
			//log记录
			int maxid = baService.getNumber("id", "mailing_balance_auditing", "max", "id > 0");
    		MailingBalanceAuditingLogBean log = new MailingBalanceAuditingLogBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(maxid+1);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("添加结算数据确认单");
			log.setType(MailingBalanceAuditingLogBean.TYPE_ADD);
			baService.addMailingBalanceAuditingLog(log);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return new ActionForward("/admin/mailingBatch.do?method=unBalanceOrderList");
	}
	
	/**
	 * 
	 * 删除结算批次中的包裹
	 */
	public ActionForward deleteMailingBalancePackage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(483)){
			request.setAttribute("tip", "您没有权限进行此操作！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		BalanceService balanceService = new  BalanceService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String id=StringUtil.convertNull(request.getParameter("id"));
		synchronized (MailingBatchLock) {
		try{
			MailingBatchPackageBean packageBean=stockService.getMailingBatchPackage("id="+id);
			if(packageBean==null){
				request.setAttribute("tip", "无此包裹，请刷新页面！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			dbOp.startTransaction();
			String set="mailing_status="+MailingBatchPackageBean.MAILING_STATUS3+",balance_status="+MailingBatchPackageBean.BALANCE_STATUS0+",mailing_balance_auditing_id=0";
			stockService.updateMailingBatchPackage(set, "id="+id);
			MailingBalanceBean mailingBalance=baService.getMailingBalance("order_code='"+packageBean.getOrderCode()+"'");
			if(mailingBalance==null){
				request.setAttribute("tip", "未找到包裹单，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			String set2="mailing_balance_auditing_id=0,balance_status="+MailingBalanceBean.BALANCE_STATUS_UNDEAL+",import_type=0";
			baService.updateMailingBalance(set2, "id="+mailingBalance.getId());
			balanceService.updateFinanceMailingBalanceBean("mailing_balance_auditing_id=0, balance_status="+MailingBalanceBean.BALANCE_STATUS_UNDEAL+", import_type=0, agency_price=0 ", "data_type = 0 AND order_code='"+packageBean.getOrderCode()+"'");
			dbOp.commitTransaction();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		}
		return new ActionForward("/admin/mailingBatch.do?method=mailingBalanceAuditingList");
	}
	
	/**
	 * 
	 * 打印财务对账单
	 */
	public ActionForward printBalanceCount(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbSlave);
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, dbSlave);
		
		String id=StringUtil.convertNull(request.getParameter("mbaId"));
		try{
			MailingBalanceAuditingBean mba=baService.getMailingBalanceAuditing("id="+id);
			if(mba==null){
				request.setAttribute("tip", "无此结算批次，操作失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List packageList=stockService.getMailingBatchPackageList("mailing_balance_auditing_id="+id, -1, -1, null);
			for(int i=0;i<packageList.size();i++){
				MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
				mba.setShouldPay(mba.getShouldPay()+packageBean.getTotalPrice());
			}
			request.setAttribute("mba", mba);
			request.setAttribute("packageList", packageList);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("printBalanceCount");
	}
	
	/**
	 * 
	 * 发货邮包重量审核
	 */
	public ActionForward checkParcelWeight(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IStockService stockService=ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String parcelId=StringUtil.convertNull(request.getParameter("parcelId"));
		String weight=StringUtil.convertNull(request.getParameter("weight"));
		try{
			MailingBatchParcelBean parcel=stockService.getMailingBatchParcel("id="+parcelId);
			if(parcel==null){
				request.setAttribute("tip", "没有找到发货邮包！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			request.setAttribute("parcelBean", parcel);
			List packageList=stockService.getMailingBatchPackageList("mailing_batch_parcel_id="+parcelId, -1, -1, null);
			for(int i=0;i<packageList.size();i++){
				MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
				if(packageBean.getWeight()==0){
					request.setAttribute("tip", "该邮包内有未称重的包裹！请通过重量修正功能对未称重的包裹进行称重，再进行邮包重量复核！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
			}
			if(!weight.equals("")){//复核
				float newWeight=StringUtil.toFloat(weight)*1000;//单位:g
				float oldWeight=0;
				for(int i=0;i<packageList.size();i++){
					MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i);
					if(packageBean.getWeight()==0){
						request.setAttribute("tip", "该邮包内有未称重的包裹！请通过重量修正功能对未称重的包裹进行称重，再进行邮包重量复核！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					oldWeight+=packageBean.getWeight();
				}
				if(newWeight-oldWeight>220||oldWeight-newWeight>220){//复核不通过
					stockService.updateMailingBatchParcel("status="+MailingBatchParcelBean.status2, "id="+parcelId);
					request.setAttribute("tip", "1");
				}else{//复核通过
					stockService.updateMailingBatchParcel("status="+MailingBatchParcelBean.status1, "id="+parcelId);
					request.setAttribute("tip", "0");
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("checkParcelWeight");
	}
	
	
	/**
	 * 
	 * 物流在途数据导出
	 */
	public ActionForward deliverOrderMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE2);
		WareService wareService = new WareService(dbOp);
		
		String startDate=StringUtil.convertNull(request.getParameter("startDate"));
		String endDate=StringUtil.convertNull(request.getParameter("endDate"));
		String buyMode=StringUtil.convertNull(request.getParameter("buyMode"));
		String status=StringUtil.convertNull(request.getParameter("status"));
		String deliver=StringUtil.convertNull(request.getParameter("deliver"));
		try{
			ExportExcel excel = new ExportExcel();
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header = new ArrayList<String>();
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			
			String sql="select mb.stockout_datetime, mb.order_code,uo.buy_mode,uo.status," +
					"sa.name,d.receive_time, d.deliver_info, dci.name, uo.address " +
					"from mailing_balance mb join deliver_order d on mb.order_id = d.order_id and d.deliver_state=7 " +
					//"join deliver_order_info d2 on d.id = d2.deliver_id " +
					"join order_stock os on mb.order_id = os.order_id " +
					"join deliver_corp_info dci on os.deliver = dci.id " +
					"join stock_area sa on os.stock_area = sa.id " +
					"join user_order uo on mb.order_id = uo.id " +
					(("-1".equals(buyMode))?"":(" and uo.buy_mode="+buyMode+" ")) +
					(("-1".equals(status))?(" and uo.status in (6,14,11) "):(" and uo.status="+status+" "))+
					(("-1".equals(deliver))?"":(" and uo.deliver="+deliver+" "))+
					"where mb.stockout_datetime between '"+startDate+" 00:00:00' and '"+endDate+" 23:59:59' and os.status <> 3 " +
					"order by mb.order_id asc, d.id asc";
			
			ResultSet rs=dbOp.executeQuery(sql);
			while(rs.next()){
				String stockOutDate=rs.getString("mb.stockout_datetime");
				if(stockOutDate!=null&&stockOutDate.length()>19){
					stockOutDate=stockOutDate.substring(0,19);
				}
				String orderCode=rs.getString("mb.order_code");
				String orderBuyMode=rs.getString("uo.buy_mode");
				if(orderBuyMode.equals("0")){
					orderBuyMode="货到付款";
				}else if(orderBuyMode.equals("1")){
					orderBuyMode="在线支付";
				}else if(orderBuyMode.equals("2")){
					orderBuyMode="银行汇款";
				}else if(orderBuyMode.equals("3")){
					orderBuyMode="售后换货";
				}
				String orderStatus=rs.getString("uo.status");
				if(orderStatus.equals("6")){
					orderStatus="已发货";
				}else if(orderStatus.equals("14")){
					orderStatus="已妥投";
				}else if(orderStatus.equals("11")){
					orderStatus="已退回";
				}
				String orderArea=rs.getString("sa.name");
				String date=rs.getString("d.receive_time");
				if(date!=null&&date.length()>19){
					date=date.substring(0,19);
				}
				String msg=rs.getString("d.deliver_info");
				if("2013-08-27 00:00:00".equals(date)){
					if(msg.length()>19){
						date=msg.substring(0,19);
					}
				}
				String orderDeliver=rs.getString("dci.name");
				String orderAdd=rs.getString("uo.address");
				
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(stockOutDate);
				tmp.add(orderCode);
				tmp.add(orderBuyMode);
				tmp.add(orderStatus);
				tmp.add(orderArea);
				tmp.add(date);
				tmp.add(msg);
				tmp.add(orderDeliver);
				tmp.add(orderAdd);
				bodies.add(tmp);
			}
			rs.close();
			if(bodies.size()>300000){
				request.setAttribute("tip", "本次导出数据过大，已超过30万条，建议重新分批导出！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			header.add("发货时间");
			header.add("订单编号");
			header.add("支付方式");
			header.add("订单状态");
			header.add("发货地区");
			header.add("时间");
			header.add("物流信息");
			header.add("物流公司");
			header.add("收货地址");
			headers.add(header);
			
			excel.setColMergeCount(header.size());

			List<Integer> row = new ArrayList<Integer>();

			List<Integer> col = new ArrayList<Integer>();

			excel.setRow(row);
			excel.setCol(col);

			// 调用填充表头方法
			excel.buildListHeader(headers);
			// 调用填充数据区方法
			excel.buildListBody(bodies);
			// 文件输出
			excel.exportToExcel("物流在途信息导出", response, "");
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return null;
		
	}
}
