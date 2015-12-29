package adultadmin.action.sms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.sms.OrderDealBean;
import adultadmin.bean.sms.OrderMessage2Bean;
import adultadmin.bean.sms.OrderMessage3Bean;
import adultadmin.bean.sms.OrderReceiveAnalyseBean;
import adultadmin.bean.sms.SendMessageAutoBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

/**
 * 
 * @author 张陶
 * 
 * 来电提醒短信相关操作
 * 
 */
public class SMSAction extends DispatchAction {

	public ActionForward savePreinstallTime(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		int orderId = StringUtil.StringToId(request.getParameter("orderId"));
		String nextProcessDate = StringUtil.dealParam(request.getParameter("nextProcessDate"));
		String nextProcessHour = StringUtil.dealParam(request.getParameter("nextProcessHour"));
		String nextProcessMinute = StringUtil.dealParam(request.getParameter("nextProcessMinute"));

		StringBuilder buf = new StringBuilder();
		buf.append(nextProcessDate).append(" ").append(nextProcessHour).append(":").append(nextProcessMinute).append(":00");
		String strNextProcessTime = buf.toString();
		Date nextProcessTime = DateUtil.parseDate(strNextProcessTime, "yyyy-MM-dd kk:mm:ss");
		if(nextProcessTime == null){
			request.setAttribute("tip", "时间设置错误，格式错误！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		if(nextProcessTime.getTime() <= System.currentTimeMillis()){
			request.setAttribute("tip", "时间设置错误，不能将预设时间设置在当前时间之前！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, null);
		WareService wareService = new WareService();
		try {
			voOrder order = wareService.getOrder(orderId);
			if(order == null){
				request.setAttribute("tip", "保存失败，订单信息不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward("failure");
			}
			if(order.getStatus() != 1){
				request.setAttribute("tip", "保存失败，订单状态不是电话失败！");
				request.setAttribute("result", "failure");
				return mapping.findForward("failure");
			}
			if(smsService.getOrderDealCount("order_id=" + orderId + " and type in (0,1)") > 0){
				buf.delete(0, buf.length());
				buf.append("next_process_time='").append(strNextProcessTime).append("', status=").append(OrderDealBean.STATUS_已经设置下次处理时间);
				smsService.updateOrderDeal(buf.toString(), "order_id=" + orderId + " and type in (0, 1)");
			} else {
				request.setAttribute("tip", "保存失败，未找到对应的短信回复信息！");
				request.setAttribute("result", "failure");
				return mapping.findForward("failure");
			}

			StringBuilder logContent = new StringBuilder();
			logContent.append("[订单处理时间设置:");
			logContent.append(strNextProcessTime);
			logContent.append("]");

			if(logContent.length() > 0){
				IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
				try{
					OrderAdminLogBean log = new OrderAdminLogBean();
					log.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
					log.setUserId(user.getId());
					log.setUsername(user.getUsername());
					log.setOrderId(order.getId());
					log.setOrderCode(order.getCode());
					log.setCreateDatetime(DateUtil.getNow());
					log.setContent(logContent.toString());

					logService.addOrderAdminLog(log);
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					logService.releaseAll();
				}
			}
		} finally {
			smsService.releaseAll();
			wareService.releaseAll();
		}

		return mapping.findForward("savePreinstallTime");
	}

	public ActionForward getSendMessageAuto(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, null);
		try {
			SendMessageAutoBean sma = smsService.getSendMessageAuto(null);
			request.setAttribute("sendMessageAuto", sma);
		} finally {
			smsService.releaseAll();
		}

		return mapping.findForward("editSendMessageAuto");
	}

	public ActionForward editSendMessageAuto(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(257);
		if (!viewAll) {
			request.setAttribute("tip", "你无权设置短信自动回复！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		int status = StringUtil.StringToId(request.getParameter("status"));
		int hourSet = StringUtil.StringToId(request.getParameter("hourSet"));
		int startHour = StringUtil.StringToId(request.getParameter("startHour"));
		int endHour = StringUtil.StringToId(request.getParameter("endHour"));
		String content = StringUtil.dealParam(request.getParameter("content"));

		StringBuilder buf = new StringBuilder();

		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, null);
		try {
			SendMessageAutoBean sma = smsService.getSendMessageAuto(null);
			if(status == SendMessageAutoBean.STATUS1){
				if(sma != null){
					buf.append("status=").append(status).append(", hour_set=").append(hourSet).append(", content='").append(content).append("'");
					if(hourSet == SendMessageAutoBean.HOURSET_STATUS1){
						if(content.trim().equals("")){
							request.setAttribute("tip", "输入内容不能为空！");
							request.setAttribute("result", "failure");
							return mapping.findForward("editSendMessageAutoResult");
						}
						if(content.length() > 200){
							request.setAttribute("tip", "最多输入200个字符！");
							request.setAttribute("result", "failure");
							return mapping.findForward("editSendMessageAutoResult");
						}

						buf.append(", start_hour=").append(startHour).append(", end_hour=").append(endHour);
						if(endHour <= startHour){
							request.setAttribute("tip", "时间段设置错误！");
							request.setAttribute("result", "failure");
							return mapping.findForward("editSendMessageAutoResult");
						}
					}
					smsService.updateSendMessageAuto(buf.toString(), "1=1");
				} else {
					if(content.trim().equals("")){
						request.setAttribute("tip", "输入内容不能为空！");
						request.setAttribute("result", "failure");
						return mapping.findForward("editSendMessageAutoResult");
					}
					if(content.length() > 200){
						request.setAttribute("tip", "最多输入200个字符！");
						request.setAttribute("result", "failure");
						return mapping.findForward("editSendMessageAutoResult");
					}

					buf.append(", start_hour=").append(startHour).append(", end_hour=").append(endHour);
					if(endHour <= startHour){
						request.setAttribute("tip", "时间段设置错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward("editSendMessageAutoResult");
					}

					sma = new SendMessageAutoBean();
					sma.setContent(content);
					sma.setEndHour(endHour);
					sma.setHourSet(SendMessageAutoBean.HOURSET_STATUS1);
					sma.setStartHour(startHour);
					sma.setStatus(status);
					smsService.addSendMessageAuto(sma);
				}
			} else {
				if(sma != null){
					smsService.updateSendMessageAuto("status=" + status, "1=1");
				} else {
					sma = new SendMessageAutoBean();
					sma.setContent("");
					sma.setEndHour(0);
					sma.setHourSet(SendMessageAutoBean.HOURSET_STATUS1);
					sma.setStartHour(0);
					sma.setStatus(status);
					smsService.addSendMessageAuto(sma);
				}
			}
			request.setAttribute("tip", "操作成功！");
		} finally {
			smsService.releaseAll();
		}

		return mapping.findForward("editSendMessageAutoResult");
	}

	public ActionForward orderReceiveAnalyse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		String mark = StringUtil.convertNull(request.getParameter("mark"));
		String date = StringUtil.convertNull(request.getParameter("date"));
		String params = "&mark="+mark+"&date="+date;
		String nextDate = "";
		if(mark.equals("1")&&!date.equals("")){
			nextDate = DateUtil.formatDate(DateUtil.rollDate(DateUtil.parseDate(date),1));
		}
		Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE);
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		try{

			int receiveOrderCount = 0;
			int receiveOrderDealedCount = 0;
			List orderReceiveAnalyseList = new ArrayList();

			StringBuilder buff = new StringBuilder();

			//分页
			buff.append("select count(distinct(t.content)) from (select content,count(distinct order_id) from sms.order_deal");
			if(mark.equals("1")&&!date.equals("")){
				buff.append(" where receive_time between '");
				buff.append(date);
				buff.append("' and '");
				buff.append(nextDate);
				buff.append("'");
			}
			buff.append(" group by order_id order by id asc) t");
			st = conn.createStatement();
			rs1 = st.executeQuery(buff.toString()); 
			//总数
			int totalCount = 0;
			if(rs1.next()){
				totalCount = rs1.getInt(1);
			}
			//页码
			int countPerPage = 50;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);

			buff.delete(0, buff.length());
			buff.append("select distinct(t.content) from (select content,count(distinct order_id) from sms.order_deal");
			if(mark.equals("1")&&!date.equals("")){
				buff.append(" where receive_time between '");
				buff.append(date);
				buff.append("' and '");
				buff.append(nextDate);
				buff.append("'");
			}
			buff.append(" group by order_id order by id asc) t limit ");
			buff.append(paging.getCurrentPageIndex() * countPerPage);
			buff.append(",");
			buff.append(countPerPage);
			st = conn.createStatement();
			rs1 = st.executeQuery(buff.toString()); 
			while(rs1.next()){
				String content = rs1.getString(1);

				//回复订单量
				buff.delete(0, buff.length());
				buff.append("select count(t.order_id) from (select content,order_id,count(distinct order_id) from sms.order_deal ");
				if(mark.equals("1")&&!date.equals("")){
					buff.append(" where receive_time between '");
					buff.append(date);
					buff.append("' and '");
					buff.append(nextDate);
					buff.append("'");
				}
				buff.append(" group by order_id) t");
				buff.append(" where t.content = '");
				buff.append(content);
				buff.append("'");
				pst = conn.prepareStatement(buff.toString());
				rs = pst.executeQuery();
				if(rs.next()){
					receiveOrderCount = rs.getInt(1);
				}

				//回复订单成交量
				buff.delete(0, buff.length());
				buff.append("select count(t.oid) from (select od.content ct,od.order_id oid,count(distinct od.order_id) from sms.order_deal od join shop.user_order uo on od.order_id = uo.id ");
				buff.append("where uo.status in (3,6,9,12,14)");
				if(mark.equals("1")&&!date.equals("")){
					buff.append(" and receive_time between '");
					buff.append(date);
					buff.append("' and '");
					buff.append(nextDate);
					buff.append("'");
				}
				buff.append(" group by od.order_id) t");
				buff.append(" where t.ct = '");
				buff.append(content);
				buff.append("'");
				pst = conn.prepareStatement(buff.toString());
				rs = pst.executeQuery();
				if(rs.next()){
					receiveOrderDealedCount = rs.getInt(1);
				}

				OrderReceiveAnalyseBean bean = new OrderReceiveAnalyseBean();
				bean.setFirstReceiveContent(content);
				bean.setOrderCount(receiveOrderCount);
				bean.setOrderDealedCount(receiveOrderDealedCount);
				bean.setOrderDealRate(Arith.div(receiveOrderDealedCount, receiveOrderCount));

				orderReceiveAnalyseList.add(bean);
			}

			Collections.sort(orderReceiveAnalyseList, new Comparator() {
				public int compare(Object o1, Object o2) {
					OrderReceiveAnalyseBean obj1 = (OrderReceiveAnalyseBean) o1;
					OrderReceiveAnalyseBean obj2 = (OrderReceiveAnalyseBean) o2;
					Integer orderCount1 = Integer.valueOf(obj1.getOrderCount());
					Integer orderCount2 = Integer.valueOf(obj2.getOrderCount());
					return (-orderCount1.compareTo(orderCount2));
				}
			});

			request.setAttribute("list", orderReceiveAnalyseList);
			paging.setPrefixUrl("SMSAction.do?method=orderReceiveAnalyse"+params);
			request.setAttribute("paging", paging);
		}finally{
			if(rs!=null){
				rs.close();
			}
			if(rs1!=null){
				rs1.close();
			}
			if(st!=null){
				st.close();
			}
			if(pst!=null){
				pst.close();
			}
			if(conn!=null){
				conn.close();
			}
		}

		return mapping.findForward("orderReceiveAnalyse");
	}

	//查货短信记录
	public ActionForward orderReceiveSMS3(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		String mobile = StringUtil.convertNull(request.getParameter("mobile"));
		int status = StringUtil.StringToId(request.getParameter("status"));
		String sendStartDate = StringUtil.convertNull(request.getParameter("sendStartDate"));
		String sendEndDate = StringUtil.convertNull(request.getParameter("sendEndDate"));
		String receiveStartDate = StringUtil.convertNull(request.getParameter("receiveStartDate"));
		String receiveEndDate = StringUtil.convertNull(request.getParameter("receiveEndDate"));
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		String packageNum = StringUtil.convertNull(request.getParameter("packageNum"));
		String operator = StringUtil.convertNull(request.getParameter("operator"));
		String consigner = StringUtil.convertNull(request.getParameter("consigner"));

		DbOperation dbOp = new DbOperation();
		dbOp.init("sms");
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
		boolean hasOrderCondition = false;
		try{
			StringBuilder buff = new StringBuilder(" 1=1 ");
			String params = "";

			if(!mobile.trim().equals("")){
				buff.append(" and o.mobile = '");
				buff.append(mobile);
				buff.append("'");

				params = params + "mobile="+mobile+"&";
			}
			if(status >= 0){
				buff.append(" and o.status = ");
				buff.append(status);
			}
			params = params + "status="+status+"&";
			if(!sendStartDate.trim().equals("")&&!sendEndDate.trim().equals("")){
				params = params + "sendStartDate="+sendStartDate+"&sendEndDate="+sendEndDate+"&";
				
				sendEndDate = sendEndDate+" 23:59:59";
				buff.append(" and o.send_datetime between '");
				buff.append(sendStartDate);
				buff.append("' and '");
				buff.append(sendEndDate);
				buff.append("'");
			}
			if(!receiveStartDate.trim().equals("")&&!receiveEndDate.trim().equals("")){
				params = params + "receiveStartDate="+receiveStartDate+"&receiveEndDate="+receiveEndDate+"&";
				
				receiveEndDate = receiveEndDate+" 23:59:59";
				buff.append(" and ");
				buff.append("o.receive_datetime between '");
				buff.append(receiveStartDate);
				buff.append("' and '");
				buff.append(receiveEndDate);
				buff.append("'");
			}
			if(!orderCode.trim().equals("")){
				buff.append(" and ");
				buff.append("o.order_code = '");
				buff.append(orderCode);
				buff.append("'");

				params = params + "orderCode="+orderCode+"&";
			}
			if(!packageNum.trim().equals("")){
				buff.append(" and ");
				buff.append("o.package_num = '");
				buff.append(packageNum);
				buff.append("'");

				params = params + "packageNum="+packageNum+"&";
			}
			if(operator.length()>0){
				buff.append(" and u.operator='").append(operator).append("'");
				params = params + "operator="+consigner+"&";
				hasOrderCondition = true;
			}
			if(consigner.length()>0){
				buff.append(" and u.consigner='").append(consigner).append("'");
				params = params + "consigner="+consigner+"&";
				hasOrderCondition = true;
			}
			
			if(params.endsWith("&")){
				params = "?method=orderReceiveSMS3&"+params.substring(0, params.length()-1);
			}else{
				params = "?method=orderReceiveSMS3";
			}
			
			String condition = null;
			if(buff.length()>0){
				condition = buff.toString();
			}

			//总数
			String sql = "select count(distinct o.id) from order_message55 o ";
			if(hasOrderCondition){
				sql = sql + "left join shop.user_order u on o.order_id = u.id";
			}
			sql = sql + " where " + (condition!=null?condition:"");
			
			int totalCount = smsService.getCascadedQueryCount(sql);
			int countPerPage = 30;
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = smsService.getOrderMessage3CascadedList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "receive_datetime desc");

			//发货人
			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				OrderMessage3Bean bean = (OrderMessage3Bean)iter.next();
				String consigner1= (String)smsService.getFieldValue("consigner", "shop.user_order", "id = "+bean.getOrderId(), null, null, "String");
				bean.setConsigner(StringUtil.convertNull(consigner1));
			}
			
			paging.setPrefixUrl("SMSAction.do"+params);
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			smsService.releaseAll();
		}

		return mapping.findForward("orderReceiveSMS3");
	}
	
	//短信群发记录
	public ActionForward orderReceiveSMS2(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		String mobile = StringUtil.convertNull(request.getParameter("mobile"));
		int status = StringUtil.StringToId(request.getParameter("status"));
		String receiveStartDate = StringUtil.convertNull(request.getParameter("receiveStartDate"));
		String receiveEndDate = StringUtil.convertNull(request.getParameter("receiveEndDate"));
		
		String startHour = StringUtil.dealParam(request.getParameter("startHour"));
		String endHour = StringUtil.dealParam(request.getParameter("endHour"));
		String startMinute = StringUtil.dealParam(request.getParameter("startMinute"));
		String endMinute = StringUtil.dealParam(request.getParameter("endMinute"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("sms");
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			StringBuilder buff = new StringBuilder();
			buff.append("receive_datetime is not null");
			String params = "";

			if(!mobile.trim().equals("")){
				if(buff.length()>0){
					buff.append(" and ");
				}
				buff.append("mobile = '");
				buff.append(mobile);
				buff.append("'");

				params = params + "mobile="+mobile+"&";
			}
			if(status >= 0){
				if(buff.length()>0){
					buff.append(" and ");
				}
				buff.append("status = ");
				buff.append(status);
			}
			params = params + "status="+status+"&";
			if(!receiveStartDate.trim().equals("")&&!receiveEndDate.trim().equals("")){
				String start = receiveStartDate + " " + startHour+":"+startMinute;
				String end = receiveEndDate + " " + endHour+":"+endMinute;
				
				params = params + "receiveStartDate="+receiveStartDate+"&receiveEndDate="+receiveEndDate;
				params+="&startHour="+startHour+"&startMinute="+startMinute+"&endHour="+endHour+"&endMinute="+endMinute+"&";
				if(buff.length()>0){
					buff.append(" and ");
				}
				buff.append("receive_datetime between '");
				buff.append(start);
				buff.append("' and '");
				buff.append(end);
				buff.append("'");
			}

			if(params.endsWith("&")){
				params = "?method=orderReceiveSMS2&"+params.substring(0, params.length()-1);
			}else{
				params = "?method=orderReceiveSMS2";
			}
			
			String condition = null;
			if(buff.length()>0){
				condition = buff.toString();
			}

			//总数
//			int totalCount = smsService.getOrderMessage2Count(condition);
			int totalCount = 3000;
			int countPerPage = 30;
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = smsService.getOrderMessage2List(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "receive_datetime desc");

			paging.setPrefixUrl("SMSAction.do"+params);
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			smsService.releaseAll();
		}

		return mapping.findForward("orderReceiveSMS2");
	}
	
	//发货短信记录，查看订单
	public ActionForward fetchReceiveSMS3(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		
		int orderId = StringUtil.StringToId(request.getParameter("orderId"));
		int orderMessageId = StringUtil.StringToId(request.getParameter("orderMessageId"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("sms");
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			smsService.updateOrderMessage3("status = "+OrderMessage3Bean.STATUS1, "id = "+orderMessageId);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			smsService.releaseAll();
		}

		response.sendRedirect(request.getContextPath()+"/admin/forder.do?id="+orderId);
		
		return null;
	}
	
	//群发短信回复记录，匹配订单
	public ActionForward fetchReceiveSMS2(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		
		String phone = StringUtil.convertNull(request.getParameter("phone"));
		int orderMessageId = StringUtil.StringToId(request.getParameter("orderMessageId"));
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("sms");
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			smsService.updateOrderMessage2("status = "+OrderMessage2Bean.STATUS1, "id = "+orderMessageId);
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			smsService.releaseAll();
		}

		response.sendRedirect(request.getContextPath()+"/admin/searchorder.do?phone="+phone);
		
		return null;
	}
	
	//短信群发记录，查看内容
	public ActionForward fetchReceiveDetailsSMS2(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}

		String mobile = StringUtil.convertNull(request.getParameter("mobile"));

		DbOperation dbOp = new DbOperation();
		dbOp.init("sms");
		ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			StringBuilder buff = new StringBuilder();
			String params = "";

			if(!mobile.trim().equals("")){
				buff.append("mobile = '");
				buff.append(mobile);
				buff.append("'");

				params = params + "mobile="+mobile+"&";
			}

			if(params.endsWith("&")){
				params = "?method=fetchReceiveDetailsSMS2&"+params.substring(0, params.length()-1);
			}else{
				params = "?method=fetchReceiveDetailsSMS2";
			}
			
			String condition = null;
			if(buff.length()>0){
				condition = buff.toString();
			}

			//总数
			int totalCount = smsService.getOrderMessage2Count(condition);
			int countPerPage = 30;
			//页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
			List list = smsService.getOrderMessage2List(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id asc");

			paging.setPrefixUrl("SMSAction.do"+params);
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			smsService.releaseAll();
		}

		return mapping.findForward("fetchReceiveDetailsSMS2");
	}

}
