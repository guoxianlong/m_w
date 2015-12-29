package mmb.rec.oper.controller;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingInfoService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/SortingMonitorController")
public class SortingMonitorController {
	public static byte[] cargoLock = new byte[0];
	/**
	 *@return 分拣统计信息
	 *@author 石远飞
	 */
	@RequestMapping("/getSortingCountInfo")
	@ResponseBody
	public Map<String,Integer> getSortingCountInfo(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		Map<String,Integer> countMap = new HashMap<String, Integer>();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "请先登录!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			// 已完成波次数
			int completeGroupCount = 0;
			String sql = "select count(distinct a.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where a.status=2 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
			ResultSet rs = service.getDbOp().executeQuery(sql);
			if (rs.next()) {
				completeGroupCount = rs.getInt(1);
			}
			// 已完成订单数
			int completeOrderCount = 0;
			sql = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where  left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and  ( b.status=3 or (b.status=2 and b.delete_status=1))";
			rs = service.getDbOp().executeQuery(sql);
			if (rs.next()) {
				completeOrderCount = rs.getInt(1);
			}
			// 未领取波次数
			int noReceiveGroupCount = 0;
			sql = "select count(id) from sorting_batch_group where status=0";
			rs = service.getDbOp().executeQuery(sql);
			if (rs.next()) {
				noReceiveGroupCount = rs.getInt(1);
			}
			// 未领取订单数
			int noReceiveOrderCount = 0;
			sql = "select count( b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where a.status=0 and b.status in(0,1) and b.delete_status<>1";
			rs = service.getDbOp().executeQuery(sql);
			if (rs.next()) {
				noReceiveOrderCount = rs.getInt(1);
			}
			// 未处理订单数
			/**
			int noDisposeOrderCount = 0;
			sql = "select count(*) from user_order a join order_stock b on a.id=b.order_id where b.status in(0,1)";
			rs = service.getDbOp().executeQuery(sql);
			if (rs.next()) {
				noDisposeOrderCount = rs.getInt(1);
			}*/
			// 分拣超时订单数
			int overTimeOrderCount = 0;
			sql = "select count(b.id) from  sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id " + "where a.status=1 and b.status=2 and b.delete_status<>1 and date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "'";
			rs = service.getDbOp().executeQuery(sql);
			if (rs.next()) {
				overTimeOrderCount = rs.getInt(1);
			}
			if(rs != null){
				rs.close();
			}
			countMap.put("completeGroupCount", completeGroupCount);
			countMap.put("completeOrderCount", completeOrderCount);
			countMap.put("noReceiveGroupCount", noReceiveGroupCount);
			countMap.put("noReceiveOrderCount", noReceiveOrderCount);
			//countMap.put("noDisposeOrderCount", noDisposeOrderCount);
			countMap.put("overTimeOrderCount", overTimeOrderCount);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return countMap;
	}
	/**
	 *@return 加载highchart数据
	 *@author 石远飞
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getHighchartData")
	@ResponseBody
	public Map<String,List<Object>> getHighchartData(HttpServletRequest request,HttpServletResponse response,
			String index) throws ServletException, IOException {
		Map<String,List<Object>> map = new HashMap<String, List<Object>>();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "请先登录!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			index = StringUtil.checkNull(index);
			List<SortingBatchGroupBean> groupCountBeans = (List<SortingBatchGroupBean>) request.getSession().getAttribute("groupCountBeans");
			List<List<SortingBatchGroupBean>> groupBeanss = (List<List<SortingBatchGroupBean>>) request.getSession().getAttribute("groupBeanss");
			if(groupCountBeans == null || groupBeanss == null){
				request.setAttribute("msg", "操作超时 请新操作!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			int i = 0;
			List<Object> indexList = new ArrayList<Object>();
			List<Object> titleList = new ArrayList<Object>();
			
			for(SortingBatchGroupBean groupCount : groupCountBeans){
				indexList.add(i);
				titleList.add("第" + (i + 1) +"班（开始时间" + groupCount.getTeamBeginTime().substring(10, 16) + " --" + groupCount.getTeamEndTime().substring(10, 16) + "）");
				i++;
			}
			map.put("indexs", indexList);
			map.put("titles", titleList);
			i = 0;
			List<Object> staffNameList = new ArrayList<Object>();
			List<Object> staffCodeList = new ArrayList<Object>();
			List<Object> completeOrderCountList = new ArrayList<Object>();
			List<Object> noCompleteOrderCountList = new ArrayList<Object>();
			List<Object> overTimeOrderCountList = new ArrayList<Object>();
			for(List<SortingBatchGroupBean> groups : groupBeanss){
				if(groups != null && groups.size() > 0){
					String[] staffNames = new String[groups.size()];
					String[] staffCodes = new String[groups.size()];
					int[] completeOrderCounts = new int[groups.size()];
					int[] noCompleteOrderCounts = new int[groups.size()];
					int[] overTimeOrderCounts = new int[groups.size()];
					for(SortingBatchGroupBean group : groups){
						staffNames[i] = group.getStaffName();
						staffCodes[i] = group.getStaffCode();
						completeOrderCounts[i] = group.getCompleteOrderCount();
						noCompleteOrderCounts[i] = group.getNoCompleteOrderCount();
						overTimeOrderCounts[i] = group.getOverTimeOrderCount();
						i++;
					}
					staffNameList.add(staffNames);
					staffCodeList.add(staffCodes);
					completeOrderCountList.add(completeOrderCounts);
					noCompleteOrderCountList.add(noCompleteOrderCounts);
					overTimeOrderCountList.add(overTimeOrderCounts);
				}
			}
			map.put("staffNames", staffNameList);
			map.put("staffCodes", staffCodeList);
			map.put("completeOrderCounts", completeOrderCountList);
			map.put("noCompleteOrderCounts", noCompleteOrderCountList);
			map.put("overTimeOrderCounts", overTimeOrderCountList);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return map;
	}
	/**
	 *说明：分拣统计子表格
	 *@author 石远飞
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getSortingCountChildDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getSortingCountChildDatagrid(HttpServletRequest request,HttpServletResponse response,
			String index) throws ServletException, IOException {
		EasyuiDataGridJson dategridJson = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "请先登录!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			index = StringUtil.checkNull(index);
			List<SortingBatchGroupBean> groupCountBeans = (List<SortingBatchGroupBean>) request.getSession().getAttribute("groupCountBeans");
			List<List<SortingBatchGroupBean>> groupBeanss = (List<List<SortingBatchGroupBean>>) request.getSession().getAttribute("groupBeanss");
			if(groupCountBeans == null || groupBeanss == null){
				request.setAttribute("msg", "操作超时 请新操作!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			List<SortingBatchGroupBean> groupBeans = groupBeanss.get(StringUtil.toInt(index)-1);
			SortingBatchGroupBean gropCountBean = groupCountBeans.get(StringUtil.toInt(index)-1);
			groupCountBeans = new ArrayList<SortingBatchGroupBean>();
			groupCountBeans.add(gropCountBean);
			dategridJson.setRows(groupBeans);
			dategridJson.setFooter(groupCountBeans);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return dategridJson;
	}
	/**
	 * @return 分拣监控列表导出excel
	 * @author syuf
	 * @throws SQLException 
	 */
	@RequestMapping("/sortingMonitorExcel")
	public String sortingMonitorExcel( HttpServletRequest request, HttpServletResponse response) throws SQLException {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		ResultSet rs = null;
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(646)) {
				request.setAttribute("msg", "你没有这个权限!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			List<List<SortingBatchGroupBean>> teamList = new ArrayList<List<SortingBatchGroupBean>>();// teamList里保存着多个分好组的StaffList
			// 1.求当天第一个波次ID号，和领单时间
			String firstDatetime = new String();
			String laterDatetime = new String();
			String sql = "select id,receive_datetime from sorting_batch_group where staff_name is not null and receive_datetime between '" + DateUtil.getNowDateStr() + " 00:00:00' and '" + DateUtil.getNowDateStr() + " 23:59:59' order by receive_datetime limit 1 ";
			rs = service.getDbOp().executeQuery(sql);
			if (rs.next()) {
				firstDatetime = rs.getString(2);
			}
			// 2.求出半个小时之后的时间
			if (!"".equals(firstDatetime) && firstDatetime.length() > 0) {
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				c.setTime(sdf.parse(firstDatetime));
				c.add(Calendar.MINUTE, 30);
				laterDatetime = sdf.format(c.getTime());
			} else {
				request.setAttribute("msg", "今天还没有员工领单!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			// 3.求出第一班小组时间段之内所有进行过领单的员工列表
			sql = "select a.staff_id,b.code,a.staff_name ,a.receive_datetime  from sorting_batch_group a " + "join cargo_staff b on a.staff_id=b.id where " + "a.receive_datetime between '" + StringUtil.cutString(firstDatetime, 0, 19) + "' and '" + laterDatetime + "' and a.staff_name is not null group by b.id";
			List<SortingBatchGroupBean> staffList = new ArrayList<SortingBatchGroupBean>();
			rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				SortingBatchGroupBean bean = new SortingBatchGroupBean();
				bean.setStaffId(rs.getInt(1));
				bean.setStaffCode(rs.getString(2));
				bean.setStaffName(rs.getString(3));
				bean.setReceiveDatetime(rs.getString(4));
				staffList.add(bean);
			}
			teamList.add(staffList);
			// 5.分组
			teamList = divideGroup(teamList, laterDatetime);
			List<SortingBatchGroupBean> xiaojiList = new ArrayList<SortingBatchGroupBean>();
			SortingBatchGroupBean zongjiBean = new SortingBatchGroupBean();
			int staffCount = 0;
			if (teamList != null && teamList.size() > 0) {
				int totalOvertimeOrderCount = 0;// 总计分拣超时订单数
				int totalGroupCount = 0;// 总计完成波次数
				int totalCompleteOrderCount = 0;// 总计完成订单数
				int totalNoCompleteOrderCount = 0;// 总计未完成订单数
				int totalSkuCount = 0;// 总计完成SKU数
				int totalProductCount = 0;// 总计完成商品数
				int totalPassageCount = 0;// 总计完成巷道数数
				for(Iterator<List<SortingBatchGroupBean>> i = teamList.iterator();i.hasNext();){
					List<SortingBatchGroupBean> tempList = i.next();
					SortingBatchGroupBean xiaojiBean = new SortingBatchGroupBean();
					int xiaojiOvertimeOrderCount = 0;// 小计分拣超时订单数
					int xiaojiGroupCount = 0;// 小计完成波次数
					int xiaojiCompleteOrderCount = 0;// 小计完成订单数
					int xiaojiNoCompleteOrderCount = 0;// 小计未完成订单数
					int xiaojiSkuCount = 0;// 小计完成SKU数
					int xiaojiProductCount = 0;// 小计完成商品数
					int xiaojiPassageCount = 0;// 小计完成巷道数
					for (Iterator<SortingBatchGroupBean> j = tempList.iterator(); j.hasNext();) {
						staffCount++;
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) j.next();
						// 姓名，员工号，完成SKU，完成商品，巷道数
						sql = " select cs.name,cs.code,count( distinct d.product_code),sum(d.stockout_count),count(distinct ci.passage_id)" + " from sorting_batch_group as a " + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where (b.status=3 or (b.status=2 and b.delete_status=1)) and cs.id=" + groupBean.getStaffId() + " and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' group by a.id";
						rs = service.getDbOp().executeQuery(sql);
						int groupCount = 0;
						int skuCount = 0;
						int productCount = 0;
						int passageCount = 0;
						while (rs.next()) {
							groupBean.setStaffName(rs.getString(1));
							groupBean.setStaffCode(rs.getString(2));
							skuCount += rs.getInt(3);
							productCount += rs.getInt(4);
							passageCount += rs.getInt(5);
						}
						groupBean.setGroupCount(groupCount);
						groupBean.setSkuCount(skuCount);
						groupBean.setProductCount(productCount);
						groupBean.setPassageCount(passageCount);
						// 计算每个员工的完成波次数
						sql = "select count(distinct a.id)from sorting_batch_group a " + "join cargo_staff as cs on cs.id=a.staff_id where (a.status=2 or (a.status=3 and a.staff_name is not null)) and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and cs.id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setGroupCount(rs.getInt(1));
						}
						// 分拣超时订单数量
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						c.setTime(sdf.parse(groupBean.getReceiveDatetime()));
						c.add(Calendar.MINUTE, 30);
						sql = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where" + " b.status=2 and a.status=1 and b.delete_status<>1 and  date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "' and a.staff_id=" + groupBean.getStaffId() + " and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setOverTimeOrderCount(rs.getInt(1));
						}
						// 员工姓名，员工编号
						sql = "select code,name from cargo_staff where id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setStaffCode(rs.getString(1));
							groupBean.setStaffName(rs.getString(2));
						}
						// 作业开始时间
						sql = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " limit 1 ";
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setBegindatetime(rs.getString(1));
						}
						// 最后领单时间
						sql = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " order by receive_datetime desc limit 1 ";
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setFinallReceiveOrderTime(rs.getString(1));
						}
						// 完成的订单
						sql = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where (b.status=3 or (b.status=2 and b.delete_status=1)) and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setCompleteOrderCount(rs.getInt(1));
						}
						// 未完成的订单
						sql = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where b.status<>3  and b.delete_status<>1 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setNoCompleteOrderCount(rs.getInt(1));
						}
						xiaojiOvertimeOrderCount += groupBean.getOverTimeOrderCount();
						xiaojiProductCount += groupBean.getProductCount();
						xiaojiCompleteOrderCount += groupBean.getCompleteOrderCount();
						xiaojiNoCompleteOrderCount += groupBean.getNoCompleteOrderCount();
						xiaojiSkuCount += groupBean.getSkuCount();
						xiaojiPassageCount += groupBean.getPassageCount();
						xiaojiGroupCount += groupBean.getGroupCount();

						totalOvertimeOrderCount += groupBean.getOverTimeOrderCount();
						totalProductCount += groupBean.getProductCount();
						totalCompleteOrderCount += groupBean.getCompleteOrderCount();
						totalNoCompleteOrderCount += groupBean.getNoCompleteOrderCount();
						totalSkuCount += groupBean.getSkuCount();
						totalPassageCount += groupBean.getPassageCount();
						totalGroupCount += groupBean.getGroupCount();
					}
					StringBuffer staffId = new StringBuffer();
					for (int l = 0; l < tempList.size(); l++) {
						SortingBatchGroupBean tempBean = (SortingBatchGroupBean) tempList.get(l);
						if (staffId.toString().equals("") || staffId.toString().length() == 0) {
							staffId.append(tempBean.getStaffId());
						} else {
							staffId.append("," + tempBean.getStaffId());
						}
					}
					sql = "select min(receive_datetime) from sorting_batch_group where staff_id in(" + staffId + ") and  left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
					rs = service.getDbOp().executeQuery(sql);
					if (rs.next()) {
						xiaojiBean.setTeamBeginTime(rs.getString(1));
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						c.setTime(sdf.parse(rs.getString(1)));
						c.add(Calendar.MINUTE, 30);
						xiaojiBean.setTeamEndTime(sdf.format(c.getTime()));
					}
					xiaojiBean.setStaffCount(tempList.size());
					xiaojiBean.setOverTimeOrderCount(xiaojiOvertimeOrderCount);
					xiaojiBean.setGroupCount(xiaojiGroupCount);
					xiaojiBean.setCompleteOrderCount(xiaojiCompleteOrderCount);
					xiaojiBean.setNoCompleteOrderCount(xiaojiNoCompleteOrderCount);
					xiaojiBean.setSkuCount(xiaojiSkuCount);
					xiaojiBean.setProductCount(xiaojiProductCount);
					xiaojiBean.setPassageCount(xiaojiPassageCount);
					xiaojiList.add(xiaojiBean);
				}
				zongjiBean.setStaffCount(staffCount);
				zongjiBean.setOverTimeOrderCount(totalOvertimeOrderCount);
				zongjiBean.setGroupCount(totalGroupCount);
				zongjiBean.setCompleteOrderCount(totalCompleteOrderCount);
				zongjiBean.setNoCompleteOrderCount(totalNoCompleteOrderCount);
				zongjiBean.setSkuCount(totalSkuCount);
				zongjiBean.setProductCount(totalProductCount);
				zongjiBean.setPassageCount(totalPassageCount);
			}

			request.setAttribute("firstDatetime", firstDatetime);
			request.setAttribute("laterDatetime", laterDatetime);
			request.setAttribute("teamList", teamList);
			request.setAttribute("xiaojiList", xiaojiList);
			request.setAttribute("zongjiBean", zongjiBean);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
			if(rs!= null){
				rs.close();
			}
		}
		return "admin/rec/oper/sortingMonitor/sortingMonitorExcel";
	}
	/**
	 *@return 加载分拣数据表格
	 *@author 石远飞
	 *@throws SQLException 
	 */
	@RequestMapping("/getSortingCountDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getSortingCountDatagrid(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException, SQLException {
		EasyuiDataGridJson dategridJson = new EasyuiDataGridJson();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "请先登录!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(646)) {
			request.setAttribute("msg", "你没有这个权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		ResultSet rs = null;
		try {
			List<List<SortingBatchGroupBean>> groupBeanss = divideGroup(null, null);// 分组
			List<SortingBatchGroupBean> groupCountBeans = new ArrayList<SortingBatchGroupBean>();
			SortingBatchGroupBean totalCountBean = new SortingBatchGroupBean();
			int staffCount = 0;
			String sql = null;
			if (groupBeanss != null && groupBeanss.size() > 0) {
				int totalOvertimeOrderCount = 0;// 总计分拣超时订单数
				int totalGroupCount = 0;// 总计完成波次数
				int totalCompleteOrderCount = 0;// 总计完成订单数
				int totalNoCompleteOrderCount = 0;// 总计未完成订单数
				int totalSkuCount = 0;// 总计完成SKU数
				int totalProductCount = 0;// 总计完成商品数
				int totalPassageCount = 0;// 总计完成巷道数数
				for (List<SortingBatchGroupBean> groupBeans : groupBeanss) {
					SortingBatchGroupBean groupCountBean = new SortingBatchGroupBean();
					int groupOvertimeOrderCount = 0;// 小计分拣超时订单数
					int groupCount = 0;// 小计完成波次数
					int groupCompleteOrderCount = 0;// 小计完成订单数
					int groupNoCompleteOrderCount = 0;// 小计未完成订单数
					int groupSkuCount = 0;// 小计完成SKU数
					int groupProductCount = 0;// 小计完成商品数
					int groupPassageCount = 0;// 小计完成巷道数
					for (SortingBatchGroupBean groupBean : groupBeans) {
						staffCount++;
						// 姓名，员工号，完成SKU，完成商品，巷道数
						sql = " select cs.name,cs.code,count( distinct d.product_code),sum(d.stockout_count),count(distinct ci.passage_id)" + " from sorting_batch_group as a " + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where (b.status=3 or (b.status=2 and b.delete_status=1)) and cs.id=" + groupBean.getStaffId() + " and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' group by a.id";
						rs = service.getDbOp().executeQuery(sql);
						int count = 0;
						int skuCount = 0;
						int productCount = 0;
						int passageCount = 0;
						while (rs.next()) {
							groupBean.setStaffName(rs.getString(1));
							groupBean.setStaffCode(rs.getString(2));
							skuCount += rs.getInt(3);
							productCount += rs.getInt(4);
							passageCount += rs.getInt(5);
						}
						groupBean.setGroupCount(count);
						groupBean.setSkuCount(skuCount);
						groupBean.setProductCount(productCount);
						groupBean.setPassageCount(passageCount);
						// 计算每个员工的完成波次数
						sql = "select count(distinct a.id)from sorting_batch_group a " + "join cargo_staff as cs on cs.id=a.staff_id where (a.status=2 or (a.status=3 and a.staff_name is not null)) and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and cs.id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setGroupCount(rs.getInt(1));
						}
						// 分拣超时订单数量
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						c.setTime(sdf.parse(groupBean.getReceiveDatetime()));
						c.add(Calendar.MINUTE, 30);
						sql = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where" + " b.status=2 and b.delete_status<>1 and  date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "' and a.staff_id=" + groupBean.getStaffId() + " and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setOverTimeOrderCount(rs.getInt(1));
						}
						// 员工姓名，员工编号
						sql = "select code,name from cargo_staff where id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setStaffCode(rs.getString(1));
							groupBean.setStaffName(rs.getString(2));
						}
						// 作业开始时间
						sql = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " limit 1 ";
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setBegindatetime(rs.getString(1));
						}
						// 最后领单时间
						sql = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " order by receive_datetime desc limit 1 ";
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setFinallReceiveOrderTime(rs.getString(1));
						}
						// 完成的订单
						sql = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where (b.status=3 or (b.status=2 and b.delete_status=1)) and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setCompleteOrderCount(rs.getInt(1));
						}
						// 未完成的订单
						sql = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where b.status<>3  and b.delete_status<>1 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
						rs = service.getDbOp().executeQuery(sql);
						if (rs.next()) {
							groupBean.setNoCompleteOrderCount(rs.getInt(1));
						}
						if(rs != null){
							rs.close();
						}
						groupOvertimeOrderCount += groupBean.getOverTimeOrderCount();
						groupProductCount += groupBean.getProductCount();
						groupCompleteOrderCount += groupBean.getCompleteOrderCount();
						groupNoCompleteOrderCount += groupBean.getNoCompleteOrderCount();
						groupSkuCount += groupBean.getSkuCount();
						groupPassageCount += groupBean.getPassageCount();
						groupCount += groupBean.getGroupCount();

						totalOvertimeOrderCount += groupBean.getOverTimeOrderCount();
						totalProductCount += groupBean.getProductCount();
						totalCompleteOrderCount += groupBean.getCompleteOrderCount();
						totalNoCompleteOrderCount += groupBean.getNoCompleteOrderCount();
						totalSkuCount += groupBean.getSkuCount();
						totalPassageCount += groupBean.getPassageCount();
						totalGroupCount += groupBean.getGroupCount();
					}
					StringBuffer staffId = new StringBuffer();
					for (int l = 0; l < groupBeans.size(); l++) {
						SortingBatchGroupBean tempBean = (SortingBatchGroupBean) groupBeans.get(l);
						if (staffId.toString().equals("") || staffId.toString().length() == 0) {
							staffId.append(tempBean.getStaffId());
						} else {
							staffId.append("," + tempBean.getStaffId());
						}
					}
					sql = "select min(receive_datetime) from sorting_batch_group where staff_id in(" + staffId + ") and  left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
					rs = service.getDbOp().executeQuery(sql);
					if (rs.next()) {
						groupCountBean.setTeamBeginTime(rs.getString(1));
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						c.setTime(sdf.parse(rs.getString(1)));
						c.add(Calendar.MINUTE, 30);
						groupCountBean.setTeamEndTime(sdf.format(c.getTime()));
					}
					if(rs != null){
						rs.close();
					}
					groupCountBean.setStaffName("小计");
					groupCountBean.setStaffCode("-");
					groupCountBean.setBegindatetime("-");
					groupCountBean.setFinallReceiveOrderTime("-");
					groupCountBean.setStaffCount(groupBeans.size());
					groupCountBean.setOverTimeOrderCount(groupOvertimeOrderCount);
					groupCountBean.setGroupCount(groupCount);
					groupCountBean.setCompleteOrderCount(groupCompleteOrderCount);
					groupCountBean.setNoCompleteOrderCount(groupNoCompleteOrderCount);
					groupCountBean.setSkuCount(groupSkuCount);
					groupCountBean.setProductCount(groupProductCount);
					groupCountBean.setPassageCount(groupPassageCount);
					groupCountBeans.add(groupCountBean);
				}
				totalCountBean.setStaffCount(staffCount);
				totalCountBean.setOverTimeOrderCount(totalOvertimeOrderCount);
				totalCountBean.setGroupCount(totalGroupCount);
				totalCountBean.setCompleteOrderCount(totalCompleteOrderCount);
				totalCountBean.setNoCompleteOrderCount(totalNoCompleteOrderCount);
				totalCountBean.setSkuCount(totalSkuCount);
				totalCountBean.setProductCount(totalProductCount);
				totalCountBean.setPassageCount(totalPassageCount);
			}
			List<Map<String, String>> groupCountMaps = new ArrayList<Map<String,String>>();
			if(groupCountBeans != null && groupCountBeans.size() > 0){
				int i = 0;
				for(SortingBatchGroupBean bean : groupCountBeans){
					i++;
					Map<String, String> groupCountMap = new HashMap<String, String>();
					groupCountMap.put("batchNumber", i+"");
					groupCountMap.put("sortingTimes", bean.getTeamBeginTime().substring(10, 16) + " --" + bean.getTeamEndTime().substring(10, 16));
					groupCountMaps.add(groupCountMap);
				}
			}else{
				request.setAttribute("msg", "今天还没有员工领单!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			List<SortingBatchGroupBean> totalCountBeans = new ArrayList<SortingBatchGroupBean>();
			totalCountBeans.add(totalCountBean);
			dategridJson.setRows(groupCountMaps);
			dategridJson.setFooter(totalCountBeans);
			request.getSession().setAttribute("groupCountBeans", groupCountBeans);
			request.getSession().setAttribute("groupBeanss", groupBeanss);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
			if(rs != null){
				rs.close();
			}
		}
		return dategridJson;
	}
	/**
	 * @param teamList
	 * @param startTime
	 * @return staffList已经分配到组别里的员工列表 teamList小组列表，元素为每个小组员工的列表
	 * @author syuf
	 */
	public List<List<SortingBatchGroupBean>> divideGroup(List<List<SortingBatchGroupBean>> teamList, String startTime) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		synchronized (cargoLock) {
			try {
				StringBuffer staffId = new StringBuffer();
				if(teamList != null && teamList.size() > 0){
					for(List<SortingBatchGroupBean> groupList : teamList){
						if(groupList != null && groupList.size() > 0){
							for(SortingBatchGroupBean group : groupList){
								if (staffId.toString().equals("") || staffId.toString().length() == 0) {
									staffId.append(group.getStaffId());
								} else {
									staffId.append("," + group.getStaffId());
								}
							}
						}
					}
					SortingBatchGroupBean groupBean = service.getSortingBatchGroupInfo(" receive_datetime>'" + startTime + "' and staff_name is not null  and staff_id not in (" + staffId + ") limit 1");
					if (groupBean != null) {// 说明找到了下一个波次的ID
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						c.setTime(sdf.parse(groupBean.getReceiveDatetime()));
						c.add(Calendar.MINUTE, 30);
						String endTime = sdf.format(c.getTime());// 求出了半个小时之后的时间
						String sql = "select a.staff_id,b.code,a.staff_name ,a.receive_datetime  from sorting_batch_group a " + "join cargo_staff b on a.staff_id=b.id where " + "a.receive_datetime between '" + StringUtil.cutString(groupBean.getReceiveDatetime(), 19) + "' and '" + endTime + "' and a.staff_name is not null and a.staff_id not in (" + staffId + ") group by b.id";
						List<SortingBatchGroupBean> staffList = new ArrayList<SortingBatchGroupBean>();
						ResultSet rs = service.getDbOp().executeQuery(sql);
						while (rs.next()) {
							SortingBatchGroupBean bean = new SortingBatchGroupBean();
							bean.setStaffId(rs.getInt(1));
							bean.setStaffCode(rs.getString(2));
							bean.setStaffName(rs.getString(3));
							bean.setReceiveDatetime(rs.getString(4));
							staffList.add(bean);
						}
						rs.close();
						teamList.add(staffList);
						divideGroup(teamList, endTime);
					} else {
						return teamList;
					}
				}else{
					teamList = new ArrayList<List<SortingBatchGroupBean>>();
					// 1.求当天第一个波次ID号，和领单时间
					String firstDatetime = new String();
					String laterDatetime = new String();
					String sql = "select id,receive_datetime from sorting_batch_group where staff_name is not null and receive_datetime between '" + DateUtil.getNowDateStr() + " 00:00:00' and '" + DateUtil.getNowDateStr() + " 23:59:59' order by receive_datetime limit 1 ";
					ResultSet rs = service.getDbOp().executeQuery(sql);
					if (rs.next()) {
						firstDatetime = rs.getString(2);
					}
					// 2.求出半个小时之后的时间
					if (!"".equals(firstDatetime) && firstDatetime.length() > 0) {
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						c.setTime(sdf.parse(firstDatetime));
						c.add(Calendar.MINUTE, 30);
						laterDatetime = sdf.format(c.getTime());
						// 3.求出第一班小组时间段之内所有进行过领单的员工列表
						String sql1 = "select a.staff_id,b.code,a.staff_name ,a.receive_datetime  from sorting_batch_group a " + "join cargo_staff b on a.staff_id=b.id where " + "a.receive_datetime between '" + StringUtil.cutString(firstDatetime, 0, 19) + "' and '" + laterDatetime + "' and a.staff_name is not null group by b.id";
						List<SortingBatchGroupBean> staffList = new ArrayList<SortingBatchGroupBean>();
						rs = service.getDbOp().executeQuery(sql1);
						while (rs.next()) {
							SortingBatchGroupBean bean = new SortingBatchGroupBean();
							bean.setStaffId(rs.getInt(1));
							bean.setStaffCode(rs.getString(2));
							bean.setStaffName(rs.getString(3));
							bean.setReceiveDatetime(rs.getString(4));
							staffList.add(bean);
						}
						teamList.add(staffList);
					}
					if(rs != null){
						rs.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		return teamList;
	}
}
