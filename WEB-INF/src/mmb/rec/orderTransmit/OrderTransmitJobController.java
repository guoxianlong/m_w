package mmb.rec.orderTransmit;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/OrderTransmitJobController")
public class OrderTransmitJobController {
	/**
	 * 复核统计
	 */
	@RequestMapping("/queryList")
	public String queryList(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
			return "admin/rec/err";
		}
		UserGroupBean group = user.getGroup();
		// if (!group.isFlag(645)) {
		// request.setAttribute("msg", "您没有此权限!");
		// return "admin/rec/err";
		// }
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String today = DateUtil.getNow();// 得到今天的时间
		String ago30 = DateUtil.getBackFromDate(today, 30);	// 得到30天前的日期
		if(startTime==""){
			request.setAttribute("endTime", today.substring(0,10));
			request.setAttribute("startTime", ago30);
		}
		else{
			request.setAttribute("endTime", endTime);
			request.setAttribute("startTime", startTime);
		}
		
		String storage = StringUtil.convertNull(request.getParameter("storage"));
		String time = StringUtil.convertNull(request.getParameter("time"));
		String flag = StringUtil.convertNull(request.getParameter("flag"));// flag用来判断是查询还是导出excel
		
		DbOperation dbOp = new DbOperation();
		try {
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);
			StringBuffer sql = new StringBuffer();
			if ("".equals(startTime) && "".equals(endTime) && ("".equals(storage) || "-1".equals(storage))
					&& "".equals(time)) {
				sql.append(" and date>='" + ago30 + " 00:00:00' and date<='" + today + "'");
			}
			if (!"".equals(storage) && !"-1".equals(storage)) {
				sql.append(" and area=" + storage);
			}
			if (("day".equals(time)||"".equals(time)) && !"".equals(startTime) && !"".equals(endTime)) {
				sql.append(" and date>='" + startTime + "' and date<='" + endTime + "'");
			}
			if ("week".equals(time) && !"".equals(startTime) && !"".equals(endTime)) {
				String[] times = DateUtil.getDateTimes("week", startTime, endTime);
				startTime = times[0];
				endTime = times[1];
				sql.append(" and date>='" + startTime + "' and date<='" + endTime + "'");
			}
			if ("month".equals(time) && !"".equals(startTime) && !"".equals(endTime)) {
				String[] times = DateUtil.getDateTimes("month", startTime, endTime);
				startTime = times[0];
				endTime = times[1];
				sql.append(" and date>='" + startTime + "' and date<='" + endTime + "'");
			}
			// 查询天数据
			if ("".equals(time) || "day".equals(time)) {
				String daySql = "select date,sum(order_count),area from order_transmit_stat where id>0 "
						+ sql + " GROUP BY date;";
				ResultSet rs = wareService.getDbOp().executeQuery(daySql);
				List list = new ArrayList();
				while (rs.next()) {
					OrderTransmitJobBean bean = new OrderTransmitJobBean();
					bean.setOrderCount(rs.getInt("sum(order_count)"));
					bean.setDate(rs.getString("date"));
					if("-1".equals(storage)){
						bean.setArea(-1);
					}else{
						bean.setArea(rs.getInt("area"));
					}
					list.add(bean);
				}
				request.setAttribute("list", list);
				rs.close();
			}
			// 查询周数据
			if (!"".equals(time) && "week".equals(time)) {
				String weekSql = "select DATE_FORMAT(date,'%X-%v') dd,sum(order_count),area from order_transmit_stat where id>0 "
						+ sql + " GROUP BY dd;";
				ResultSet rs = wareService.getDbOp().executeQuery(weekSql);
				List list = new ArrayList();
				while (rs.next()) {
					OrderTransmitJobBean bean = new OrderTransmitJobBean();
					bean.setOrderCount(rs.getInt("sum(order_count)"));
					bean.setDate(rs.getString("dd") + "  周");
					if("-1".equals(storage)){
						bean.setArea(-1);
					}else{
						bean.setArea(rs.getInt("area"));
					}
					list.add(bean);
				}
				request.setAttribute("list", list);
				rs.close();
			}
			// 查询月数据
			if (!"".equals(time) && "month".equals(time)) {
				String weekSql = "select  DATE_FORMAT(date,'%X-%m') dd,sum(order_count),area from order_transmit_stat where id>0 "
						+ sql + " GROUP BY dd;";
				ResultSet rs = wareService.getDbOp().executeQuery(weekSql);
				List list = new ArrayList();
				while (rs.next()) {
					OrderTransmitJobBean bean = new OrderTransmitJobBean();
					bean.setOrderCount(rs.getInt("sum(order_count)"));
					bean.setDate(rs.getString("dd") + "  月");
					if("-1".equals(storage)){
						bean.setArea(-1);
					}else{
						bean.setArea(rs.getInt("area"));
					}
					list.add(bean);
				}
				request.setAttribute("list", list);
				rs.close();
			}
			request.setAttribute("storage", storage);
			request.setAttribute("time", time);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}

		if ("query".equals(flag)) {
			return "admin/rec/stat/orderTransmit";
		} else if ("excel".equals(flag)) {
			return "admin/rec/stat/orderTransmitExcel";
		} else {
			return "admin/rec/stat/orderTransmit";
		}
	}
}
