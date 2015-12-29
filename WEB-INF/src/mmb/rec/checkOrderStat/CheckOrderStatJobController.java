package mmb.rec.checkOrderStat;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.orderTransmit.OrderTransmitJobBean;
import mmb.rec.orderTransmit.OrderTransmitJobService;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/CheckOrderStatJobController")
public class CheckOrderStatJobController {
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
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String today = DateUtil.getNow();// 得到今天的时间
		String ago30 = DateUtil.getBackFromDate(today, 30);// 得到30天前的日期
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
		try {
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
				String daySql = "select date,area,sum(order_count),sum(product_count),sum(sku_count),area from check_order_stat where id>0 "
						+ sql + " GROUP BY date;";
				ResultSet rs = wareService.getDbOp().executeQuery(daySql);
				List list = new ArrayList();
				while (rs.next()) {
					CheckOrderStatJobBean bean = new CheckOrderStatJobBean();
					bean.setOrderCount(rs.getInt("sum(order_count)"));
					bean.setProductCount(rs.getInt("sum(product_count)"));
					bean.setSkuCount(rs.getInt("sum(sku_count)"));
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
				String weekSql = "select DATE_FORMAT(date,'%X-%v') dd,sum(order_count),sum(product_count),sum(sku_count),area from check_order_stat where id>0 "
						+ sql + " GROUP BY dd;";
				ResultSet rs = wareService.getDbOp().executeQuery(weekSql);
				List list = new ArrayList();
				while (rs.next()) {
					CheckOrderStatJobBean bean = new CheckOrderStatJobBean();
					bean.setOrderCount(rs.getInt("sum(order_count)"));
					bean.setProductCount(rs.getInt("sum(product_count)"));
					bean.setSkuCount(rs.getInt("sum(sku_count)"));
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
				String weekSql = "select  DATE_FORMAT(date,'%X-%m') dd,sum(order_count),sum(product_count),sum(sku_count),area from check_order_stat where id>0 "
						+ sql + " GROUP BY dd;";
				ResultSet rs = wareService.getDbOp().executeQuery(weekSql);
				List list = new ArrayList();
				while (rs.next()) {
					CheckOrderStatJobBean bean = new CheckOrderStatJobBean();
					bean.setOrderCount(rs.getInt("sum(order_count)"));
					bean.setProductCount(rs.getInt("sum(product_count)"));
					bean.setSkuCount(rs.getInt("sum(sku_count)"));
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
			return "admin/rec/stat/checkOrderStat";
		} else if ("excel".equals(flag)) {
			return "admin/rec/stat/checkOrderStatExcel";
		} else {
			return "admin/rec/stat/checkOrderStat";
		}
	}

	/**
	 * 返回当前登陆用户所拥有的仓库列表
	 */
	@RequestMapping("/getStorageList")
	@ResponseBody
	public String getStorageList(ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) {
		StringBuffer sb = new StringBuffer();
		List<?> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		sb.append("[");
		sb.append("{\"id\":\"-1\",\"storageName\":\"全部仓\"},");
		if (areaList != null) {
			for (int i = 0; i < areaList.size(); i++) {
				sb.append("{\"id\":\"").append(areaList.get(i)).append("\",\"storageName\":\"")
						.append(ProductStockBean.areaMap.get(Integer.valueOf(areaList.get(i).toString())))
						.append("\"}");
				if (i == areaList.size() - 1) {
				} else {
					sb.append(",");
				}
			}
		}
		sb.append("]");
		return sb.toString();
	}

}
