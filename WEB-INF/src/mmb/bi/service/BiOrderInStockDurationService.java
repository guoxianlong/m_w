package mmb.bi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bi.model.BiOrderInStockAging;
import mmb.bi.model.BiOrderInStockDuration;
import mmb.common.dao.CommonDao;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.util.excel.ExportExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Service
public class BiOrderInStockDurationService {
	@Autowired
	public CommonDao commonMapper;

	public Json getInStockDuration(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2119)) {
			j.setMsg("没有权限！");
			return j;
		}

		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String start = "";
		String end = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		HashMap<String, String> avgParamMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put(
					"column",
					"datet datex,"
							+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			paramMap.put(
					"table",
					" (select create_date datet,sum(intraday_real_deliver_order_count) realCount,sum(intraday_deliver_order_count) deliverCount,sum(in_stock_duration) inStock,sum(sorting_duration) sorting,sum(allocate_duration) allocate,sum(review_duration) review,sum(associate_duration) associate from bi_order_in_stock_duration where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet) tb ");
			paramMap.put("condition", "1=1");
			avgParamMap
					.put("column",
							" datex,inStockDuration,sortingDuration,allocateDuration,reviewDuration,associateDuration ");
			avgParamMap
					.put("table",
							" (select '日均' datex,"
									+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration from  ("
									+ "select sum(intraday_real_deliver_order_count) realCount,sum(intraday_deliver_order_count) deliverCount,sum(in_stock_duration) inStock,sum(sorting_duration) sorting,sum(allocate_duration) allocate,sum(review_duration) review,sum(associate_duration) associate from bi_order_in_stock_duration where "
									+ condition.toString()
									+ " create_date >= '" + start
									+ "' and create_date <= '" + end
									+ "' ) tb) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
			paramMap.put(
					"column",
					" datet datex,"
							+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datex,'%X年-%m月') datet, "
							+ "round(avg(realCount),4) realCount,"
							+ "round(avg(deliverCount),4) deliverCount,"
							+ "round(avg(inStock),4) inStock,"
							+ "round(avg(sorting),4) sorting,"
							+ "round(avg(allocate),4) allocate,"
							+ "round(avg(review),4) review,"
							+ "round(avg(associate),4) associate from ("
							+ "select create_date datex,sum(intraday_real_deliver_order_count) realCount,"
							+ "sum(intraday_deliver_order_count) deliverCount,"
							+ "sum(in_stock_duration) inStock,"
							+ "sum(sorting_duration) sorting,"
							+ "sum(allocate_duration) allocate,"
							+ "sum(review_duration) review,"
							+ "sum(associate_duration) associate from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datex) tb group by datet) tb1 ");
			paramMap.put("condition", "1=1");
			avgParamMap
					.put("column",
							" datex,"
									+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			avgParamMap
					.put("table",
							" (select '日均' datex,"
									+ "round(avg(realCount),4) realCount,"
									+ "round(avg(deliverCount),4) deliverCount,"
									+ "round(avg(inStock),4) inStock,"
									+ "round(avg(sorting),4) sorting,"
									+ "round(avg(allocate),4) allocate,"
									+ "round(avg(review),4) review,"
									+ "round(avg(associate),4) associate from ("
									+ "select create_date datex,sum(intraday_real_deliver_order_count) realCount,"
									+ "sum(intraday_deliver_order_count) deliverCount,"
									+ "sum(in_stock_duration) inStock,"
									+ "sum(sorting_duration) sorting,"
									+ "sum(allocate_duration) allocate,"
									+ "sum(review_duration) review,"
									+ "sum(associate_duration) associate from bi_order_in_stock_duration where "
									+ condition.toString()
									+ " create_date >= '" + start
									+ "' and create_date <= '" + end
									+ "' group by datex) tb ) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
			paramMap.put(
					"column",
					" datex,"
							+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datet, '%X年') datex,"
							+ "round(avg(realCount),4) realCount,"
							+ "round(avg(deliverCount),4) deliverCount,"
							+ "round(avg(inStock),4) inStock,"
							+ "round(avg(sorting),4) sorting,"
							+ "round(avg(allocate),4) allocate,"
							+ "round(avg(review),4) review,"
							+ "round(avg(associate),4) associate from ("
							+ "select DATE_FORMAT(create_date,'%X-%m-01') datet,"
							+ "sum(intraday_real_deliver_order_count) realCount,"
							+ "sum(intraday_deliver_order_count) deliverCount,"
							+ "sum(in_stock_duration) inStock,"
							+ "sum(sorting_duration) sorting,"
							+ "sum(allocate_duration) allocate,"
							+ "sum(review_duration) review,"
							+ "sum(associate_duration) associate from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datet) tb group by datex) tb1 ");
			paramMap.put("condition", " 1=1");
			avgParamMap
					.put("column",
							" datex,"
									+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
									+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			avgParamMap
					.put("table",
							" (select '月均' datex,"
									+ "round(avg(realCount),4) realCount,"
									+ "round(avg(deliverCount),4) deliverCount,"
									+ "round(avg(inStock),4) inStock,"
									+ "round(avg(sorting),4) sorting,"
									+ "round(avg(allocate),4) allocate,"
									+ "round(avg(review),4) review,"
									+ "round(avg(associate),4) associate from ("
									+ "select DATE_FORMAT(create_date,'%X年-%m月') datet,"
									+ "sum(intraday_real_deliver_order_count) realCount,"
									+ "sum(intraday_deliver_order_count) deliverCount,"
									+ "sum(in_stock_duration) inStock,"
									+ "sum(sorting_duration) sorting,"
									+ "sum(allocate_duration) allocate,"
									+ "sum(review_duration) review,"
									+ "sum(associate_duration) associate from bi_order_in_stock_duration where "
									+ condition.toString()
									+ " create_date >= '" + start
									+ "' and create_date <= '" + end
									+ "' group by datet) tb ) tb1  ");
			avgParamMap.put("condition", " 1=1");
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		List<HashMap<String, String>> listFooter = commonMapper
				.getCommonInfo(avgParamMap);
		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);

		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		easyuiDataGridJson.setRows(listRows);
		easyuiDataGridJson.setFooter(listFooter);
		j.setSuccess(true);
		j.setObj(easyuiDataGridJson);
		return j;
	}

	public Json getInStockDurationChart(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2119)) {
			j.setMsg("没有权限！");
			return j;
		}

		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String start = "";
		String end = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put(
					"column",
					"date_format(datet,'%Y-%m-%d') datex,"
							+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration ");
			paramMap.put(
					"table",
					" (select create_date datet,"
							+ "sum(intraday_real_deliver_order_count) realCount,"
							+ "sum(in_stock_duration) inStock "
							+ "from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datet) tb ");
			paramMap.put("condition", "1=1");
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
			paramMap.put(
					"column",
					" datet datex,"
							+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datex,'%X年-%m月') datet, "
							+ "round(avg(realCount),4) realCount,"
							+ "round(avg(inStock),4) inStock from ("
							+ "select create_date datex,sum(intraday_real_deliver_order_count) realCount,"
							+ "sum(in_stock_duration) inStock from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datex) tb group by datet) tb1 ");
			paramMap.put("condition", "1=1");
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
			paramMap.put(
					"column",
					" datex,"
							+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datet, '%X年') datex,"
							+ "round(avg(realCount),4) realCount,"
							+ "round(avg(inStock),4) inStock from ("
							+ "select DATE_FORMAT(create_date,'%X-%m-01') datet,"
							+ "sum(intraday_real_deliver_order_count) realCount,"
							+ "sum(in_stock_duration) inStock from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datet) tb group by datex) tb1 ");
			paramMap.put("condition", " 1=1");
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);

		String[] returntimes = null;
		float[] inStockDurations = null;

		if (listRows.size() != 0) {
			int x = listRows.size();
			returntimes = new String[x];
			inStockDurations = new float[x];
		}
		int i = 0;
		for (HashMap<String, String> map : listRows) {
			returntimes[i] = map.get("datex");
			inStockDurations[i] = StringUtil
					.toFloat(map.get("inStockDuration"));
			i++;
		}
		List returnStrings = new ArrayList();
		returnStrings.add(returntimes);
		returnStrings.add(inStockDurations);

		j.setSuccess(true);
		j.setObj(returnStrings);
		return j;
	}

	public Json getInStockDurationHourChart(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2121)) {
			j.setMsg("没有权限！");
			return j;
		}

		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String start = "";
		String end = "";
		String startHour = "";
		String endHour = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		String[] returntimes = null;
		float[] inStockDurations = null;

		Map<Integer, HashMap<String, String>> timeMap = BiOrderInStockDuration.timeMap;
		Map<Integer, String> map = BiOrderInStockDuration.timeTypeMap;
		returntimes = new String[map.size()];
		inStockDurations = new float[map.size()];
		int i = 0;
		for (int key : map.keySet()) {
			returntimes[i] = map.get(key);
			if (!startTime.equals("") && !endTime.equals("")) {
				start = startTime;
				startHour = timeMap.get(key).get("start") + ":00";
				end = endTime;
				endHour = timeMap.get(key).get("end")
						+ (timeMap.get(key).get("end").equals("23:59") ? ":59"
								: ":00");
			} else if (!startMonth.equals("") && !endMonth.equals("")) {
				start = startMonth + "-01";
				startHour = timeMap.get(key).get("start") + ":00";
				end = endMonth + "-31";
				endHour = timeMap.get(key).get("end")
						+ (timeMap.get(key).get("end").equals("23:59") ? ":59"
								: ":00");
			} else if (!startYear.equals("") && !endYear.equals("")) {
				start = startYear + "-01-01";
				startHour = timeMap.get(key).get("start") + ":00";
				end = endYear + "-12-31";
				endHour = timeMap.get(key).get("end")
						+ (timeMap.get(key).get("end").equals("23:59") ? ":59"
								: ":00");
			} else {
				j.setMsg("请输入时间作为条件查询！");
				return j;
			}
			paramMap.put(
					"column",
					"datet datex,"
							+ "case realCount when 0 then '0' else concat(round(inStock/realCount  ,3),'') end inStockDuration ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(begin_date_time, '%T') datet,"
							+ "sum(intraday_real_deliver_order_count) realCount,"
							+ "sum(in_stock_duration) inStock "
							+ "from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end + "' "
							+ "and date_format(begin_date_time, '%T') >='"
							+ startHour + "' "
							+ "and date_format(end_date_time, '%T') <='"
							+ endHour + "' ) tb ");
			paramMap.put("condition", "1=1");

			List<HashMap<String, String>> listRows = commonMapper
					.getCommonInfo(paramMap);
			if (listRows != null) {
				HashMap<String, String> mapx = listRows.get(0);
				if (mapx != null) {
					inStockDurations[i] = StringUtil.toFloat(mapx
							.get("inStockDuration"));
				}
			}
			i++;
		}

		List returnStrings = new ArrayList();
		returnStrings.add(returntimes);
		returnStrings.add(inStockDurations);

		j.setSuccess(true);
		j.setObj(returnStrings);
		return j;
	}

	public Json getStoragePartInStockDurationChart(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2120)) {
			j.setMsg("没有权限！");
			return j;
		}

		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String start = "";
		String end = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put(
					"column",
					"date_format(datet,'%Y-%m-%d') datex,"
							+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			paramMap.put(
					"table",
					" (select create_date datet,sum(intraday_deliver_order_count) deliverCount,sum(sorting_duration) sorting,sum(allocate_duration) allocate,sum(review_duration) review,sum(associate_duration) associate from bi_order_in_stock_duration where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet) tb ");
			paramMap.put("condition", "1=1");
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
			paramMap.put(
					"column",
					" datet datex,"
							+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datex,'%X年-%m月') datet, "
							+ "round(avg(deliverCount),4) deliverCount,"
							+ "round(avg(sorting),4) sorting,"
							+ "round(avg(allocate),4) allocate,"
							+ "round(avg(review),4) review,"
							+ "round(avg(associate),4) associate from ("
							+ "select create_date datex,"
							+ "sum(intraday_deliver_order_count) deliverCount,"
							+ "sum(sorting_duration) sorting,"
							+ "sum(allocate_duration) allocate,"
							+ "sum(review_duration) review,"
							+ "sum(associate_duration) associate from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datex) tb group by datet) tb1 ");
			paramMap.put("condition", "1=1");
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
			paramMap.put(
					"column",
					" datex,"
							+ "case deliverCount when 0 then '0' else concat(round(sorting/deliverCount  ,3),'') end sortingDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(allocate/deliverCount  ,3),'') end allocateDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(review/deliverCount  ,3),'') end reviewDuration,"
							+ "case deliverCount when 0 then '0' else concat(round(associate/deliverCount  ,3),'') end associateDuration ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datet, '%X年') datex,"
							+ "round(avg(deliverCount),4) deliverCount,"
							+ "round(avg(sorting),4) sorting,"
							+ "round(avg(allocate),4) allocate,"
							+ "round(avg(review),4) review,"
							+ "round(avg(associate),4) associate from ("
							+ "select DATE_FORMAT(create_date,'%X-%m-01') datet,"
							+ "sum(intraday_deliver_order_count) deliverCount,"
							+ "sum(sorting_duration) sorting,"
							+ "sum(allocate_duration) allocate,"
							+ "sum(review_duration) review,"
							+ "sum(associate_duration) associate from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datet) tb group by datex) tb1 ");
			paramMap.put("condition", " 1=1");
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);

		String[] returntimes = null;
		float[] sortingDurations = null;
		float[] allocateDurations = null;
		float[] reviewDurations = null;
		float[] associateDurations = null;

		if (listRows.size() != 0) {
			int x = listRows.size();
			returntimes = new String[x];
			sortingDurations = new float[x];
			allocateDurations = new float[x];
			reviewDurations = new float[x];
			associateDurations = new float[x];
		}
		int i = 0;
		for (HashMap<String, String> map : listRows) {
			returntimes[i] = map.get("datex");
			sortingDurations[i] = StringUtil
					.toFloat(map.get("sortingDuration"));
			allocateDurations[i] = StringUtil.toFloat(map
					.get("allocateDuration"));
			reviewDurations[i] = StringUtil.toFloat(map.get("reviewDuration"));
			associateDurations[i] = StringUtil.toFloat(map
					.get("associateDuration"));
			i++;
		}
		List returnStrings = new ArrayList();
		returnStrings.add(returntimes);
		returnStrings.add(sortingDurations);
		returnStrings.add(allocateDurations);
		returnStrings.add(reviewDurations);
		returnStrings.add(associateDurations);

		j.setSuccess(true);
		j.setObj(returnStrings);
		return j;
	}

	public Json getDividePartInStockDurationChart(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2120)) {
			j.setMsg("没有权限！");
			return j;
		}
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));
		int type = StringUtil.toInt(request.getParameter("type"));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String start = "";
		String end = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}

		String searchColumn = "";

		switch (type) {
		case -1:
			searchColumn = "sorting_duration + allocate_duration + review_duration + associate_duration";
			break;
		case 1:
			searchColumn = "sorting_duration";
			break;
		case 2:
			searchColumn = "allocate_duration";
			break;
		case 3:
			searchColumn = "review_duration";
			break;
		case 4:
			searchColumn = "associate_duration";
			break;
		default:
			j.setMsg("所选环节错误！");
			return j;
		}

		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put(
					"column",
					"date_format(datet,'%Y-%m-%d') datex,"
							+ "case deliverCount when 0 then '0' else concat(round(duration/deliverCount  ,3),'') end durationAll ");
			paramMap.put(
					"table",
					" (select create_date datet,sum(intraday_deliver_order_count) deliverCount,sum("
							+ searchColumn
							+ ") duration from bi_order_in_stock_duration where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet) tb ");
			paramMap.put("condition", "1=1");
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
			paramMap.put(
					"column",
					" datet datex,"
							+ "case deliverCount when 0 then '0' else concat(round(duration/deliverCount  ,3),'') end durationAll ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datex,'%X年-%m月') datet, "
							+ "round(avg(deliverCount),4) deliverCount,"
							+ "round(avg(duration),4) duration from ("
							+ "select create_date datex,"
							+ "sum(intraday_deliver_order_count) deliverCount,"
							+ "sum("
							+ searchColumn
							+ ") duration from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datex) tb group by datet) tb1 ");
			paramMap.put("condition", "1=1");
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
			paramMap.put(
					"column",
					" datex,"
							+ "case deliverCount when 0 then '0' else concat(round(duration/deliverCount  ,3),'') end durationAll ");
			paramMap.put("table", " (select DATE_FORMAT(datet, '%X年') datex,"
					+ "round(avg(deliverCount),4) deliverCount,"
					+ "round(avg(duration),4) duration from ("
					+ "select DATE_FORMAT(create_date,'%X-%m-01') datet,"
					+ "sum(intraday_deliver_order_count) deliverCount,"
					+ "sum(" + searchColumn
					+ ") duration  from bi_order_in_stock_duration where "
					+ condition.toString() + " create_date >= '" + start
					+ "' and create_date <= '" + end
					+ "' group by datet) tb group by datex) tb1 ");
			paramMap.put("condition", " 1=1");
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);

		String[] returntimes = null;
		float[] durationAlls = null;

		if (listRows.size() != 0) {
			int x = listRows.size();
			returntimes = new String[x];
			durationAlls = new float[x];
		}
		int i = 0;
		for (HashMap<String, String> map : listRows) {
			returntimes[i] = map.get("datex");
			durationAlls[i] = StringUtil.toFloat(map.get("durationAll"));
			i++;
		}
		List returnStrings = new ArrayList();
		returnStrings.add(returntimes);
		returnStrings.add(durationAlls);
		List list = new ArrayList();
		list.add(BiOrderInStockDuration.partMap.get(type) == null ? "订单"
				: BiOrderInStockDuration.partMap.get(type) + "环节");
		list.add(returnStrings);

		j.setSuccess(true);
		j.setObj(list);
		return j;
	}

	public Json getDividePartInStockDurationHourChart(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2122)) {
			j.setMsg("没有权限！");
			return j;
		}

		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));
		int type = StringUtil.toInt(request.getParameter("type"));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String searchColumn = "";
		switch (type) {
		case -1:
			searchColumn = "sorting_duration + allocate_duration + review_duration + associate_duration";
			break;
		case 1:
			searchColumn = "sorting_duration";
			break;
		case 2:
			searchColumn = "allocate_duration";
			break;
		case 3:
			searchColumn = "review_duration";
			break;
		case 4:
			searchColumn = "associate_duration";
			break;
		default:
			j.setMsg("所选环节错误！");
			return j;
		}

		String start = "";
		String end = "";
		String startHour = "";
		String endHour = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		String[] returntimes = null;
		float[] inStockDurations = null;

		Map<Integer, HashMap<String, String>> timeMap = BiOrderInStockDuration.timeMap;
		Map<Integer, String> map = BiOrderInStockDuration.timeTypeMap;
		returntimes = new String[map.size()];
		inStockDurations = new float[map.size()];
		int i = 0;
		for (int key : map.keySet()) {
			returntimes[i] = map.get(key);
			if (!startTime.equals("") && !endTime.equals("")) {
				start = startTime;
				startHour = timeMap.get(key).get("start") + ":00";
				end = endTime;
				endHour = timeMap.get(key).get("end")
						+ (timeMap.get(key).get("end").equals("23:59") ? ":59"
								: ":00");
			} else if (!startMonth.equals("") && !endMonth.equals("")) {
				start = startMonth + "-01";
				startHour = timeMap.get(key).get("start") + ":00";
				end = endMonth + "-31";
				endHour = timeMap.get(key).get("end")
						+ (timeMap.get(key).get("end").equals("23:59") ? ":59"
								: ":00");
			} else if (!startYear.equals("") && !endYear.equals("")) {
				start = startYear + "-01-01";
				startHour = timeMap.get(key).get("start") + ":00";
				end = endYear + "-12-31";
				endHour = timeMap.get(key).get("end")
						+ (timeMap.get(key).get("end").equals("23:59") ? ":59"
								: ":00");
			} else {
				j.setMsg("请输入时间作为条件查询！");
				return j;
			}
			paramMap.put(
					"column",
					"datet datex,"
							+ "case deliverCount when 0 then '0' else concat(round(duration/deliverCount  ,3),'') end durationAll ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(begin_date_time, '%T') datet,"
							+ "sum(intraday_deliver_order_count) deliverCount,"
							+ "sum(" + searchColumn + ") duration "
							+ "from bi_order_in_stock_duration where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end + "' "
							+ "and date_format(begin_date_time, '%T') >='"
							+ startHour + "' "
							+ "and date_format(end_date_time, '%T') <='"
							+ endHour + "' ) tb ");
			paramMap.put("condition", "1=1");

			List<HashMap<String, String>> listRows = commonMapper
					.getCommonInfo(paramMap);
			if (listRows != null) {
				HashMap<String, String> mapx = listRows.get(0);
				if (mapx != null) {
					inStockDurations[i] = StringUtil.toFloat(mapx
							.get("durationAll"));
				}
			}
			i++;
		}

		List returnStrings = new ArrayList();
		returnStrings.add(returntimes);
		returnStrings.add(inStockDurations);

		List list = new ArrayList();
		list.add(BiOrderInStockDuration.partMap.get(type) == null ? "订单"
				: BiOrderInStockDuration.partMap.get(type) + "环节");
		list.add(returnStrings);

		j.setSuccess(true);
		j.setObj(list);
		return j;
	}

	public Json getOrderInStockDetail(HttpServletRequest request,
			EasyuiDataGrid easyuiDataGrid) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2124)) {
			j.setMsg("没有权限！");
			return j;
		}

		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));
		int type = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("type")));

		if (stockArea == -1 && startYear.equals("") && endYear.equals("")
				&& startMonth.equals("") && endMonth.equals("")
				&& startTime.equals("") && endTime.equals("") && type == -1) {
			j.setSuccess(true);
			return j;
		}

		HashMap<Integer, HashMap<String, String>> inStockAgingTypeMap = BiOrderInStockAging.inStockAgingTypeMap;

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String start = "";
		String end = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
		} else {
			j.setMsg("时间参数错误！");
			return j;
		}

		paramMap.put(
				"column",
				" os.order_code orderCode,"
						+ "os. CODE ckCode,"
						+ "date_format(sb.create_datetime, '%Y-%m-%d %T' ) sortingStart,"
						+ "date_format(sbg.sorting_complete_datetime, '%Y-%m-%d %T' ) sortingEnd,"
						+ "date_format(sbg.receive_datetime2, '%Y-%m-%d %T' ) allocateStart,"
						+ "date_format(sbg.complete_datetime2, '%Y-%m-%d %T' ) allocateEnd,"
						+ "date_format(ap.first_print_package_datetime, '%Y-%m-%d %T' ) reviewTime,"
						+ "date_format(mb.transit_datetime, '%Y-%m-%d %T' ) associateTime ");
		paramMap.put(
				"table",
				" order_stock os "
						+ "JOIN mailing_batch_package mbp ON os.order_id = mbp.order_id "
						+ "JOIN mailing_batch mb ON mb.code = mbp.mailing_batch_code "
						+ "JOIN sorting_batch_order sbo ON sbo.order_id = os.order_id  and sbo.delete_status=0 "
						+ "JOIN sorting_batch sb ON sbo.sorting_batch_id = sb.id "
						+ "JOIN sorting_batch_group sbg ON sbg.id = sbo.sorting_group_id "
						+ "JOIN audit_package ap ON ap.order_id = os.order_id ");
		if (type == -1) {
			condition.append(" mb.transit_datetime >= '" + start
					+ " 00:00:00' and mb.transit_datetime <= '" + end
					+ " 23:59:59' and os.status<>"
					+ OrderStockBean.STATUS4);
			paramMap.put("condition", condition.toString() + " limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
		} else if (inStockAgingTypeMap.get(type) != null) {
			condition
					.append(" mb.transit_datetime >= '"
							+ start
							+ " 00:00:00' and mb.transit_datetime <= '"
							+ end
							+ " 23:59:59'  and os.status<>"
							+ OrderStockBean.STATUS4
							+ " and timestampdiff(hour,sb.create_datetime, mb.transit_datetime) >= "
							+ inStockAgingTypeMap.get(type).get("start")
							+ " and timestampdiff(hour,sb.create_datetime, mb.transit_datetime) < "
							+ inStockAgingTypeMap.get(type).get("end"));
			paramMap.put("condition", condition.toString() + " limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
		} else {
			j.setMsg("类型参数错误！");
			return j;
		}

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);
		paramMap.put("column",
				" concat(thecount,'') thecount from (select count(os.id) thecount ");
		paramMap.put("condition", condition.toString() + ") tb");
		List<HashMap<String, String>> countRow = commonMapper
				.getCommonInfo(paramMap);

		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) StringUtil.toInt(countRow.get(0)
				.get("thecount")));
		easyuiDataGridJson.setRows(listRows);
		j.setSuccess(true);
		j.setObj(easyuiDataGridJson);
		return j;
	}
	

	public Json exportOrderInStockDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2125)) {
			j.setMsg("没有权限！");
			return j;
		}

		int stockArea = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startYear")));
		String endYear = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startMonth")));
		String endMonth = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("endTime")));
		int type = StringUtil.toInt(StringUtil.convertNull(request
				.getParameter("type")));

		if (stockArea == -1 && startYear.equals("") && endYear.equals("")
				&& startMonth.equals("") && endMonth.equals("")
				&& startTime.equals("") && endTime.equals("") && type == -1) {
			j.setSuccess(true);
			return j;
		}
		
		HashMap<Integer, HashMap<String, String>> inStockAgingTypeMap = BiOrderInStockAging.inStockAgingTypeMap;

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" stock_area in (").append(stockArea).append(") ");
		}
		String start = "";
		String end = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
		} else {
			j.setMsg("时间参数错误！");
			return j;
		}

		paramMap.put(
				"column",
				" os.order_code orderCode,"
						+ "os. CODE ckCode,"
						+ "date_format(sb.create_datetime, '%Y-%m-%d %T' ) sortingStart,"
						+ "date_format(sbg.sorting_complete_datetime, '%Y-%m-%d %T' ) sortingEnd,"
						+ "date_format(sbg.receive_datetime2, '%Y-%m-%d %T' ) allocateStart,"
						+ "date_format(sbg.complete_datetime2, '%Y-%m-%d %T' ) allocateEnd,"
						+ "date_format(ap.first_print_package_datetime, '%Y-%m-%d %T' ) reviewTime,"
						+ "date_format(mb.transit_datetime, '%Y-%m-%d %T' ) associateTime ");
		paramMap.put(
				"table",
				" order_stock os "
						+ "JOIN mailing_batch_package mbp ON os.order_id = mbp.order_id "
						+ "JOIN mailing_batch mb ON mb.code = mbp.mailing_batch_code "
						+ "JOIN sorting_batch_order sbo ON sbo.order_id = os.order_id  and sbo.delete_status=0 "
						+ "JOIN sorting_batch sb ON sbo.sorting_batch_id = sb.id "
						+ "JOIN sorting_batch_group sbg ON sbg.id = sbo.sorting_group_id "
						+ "JOIN audit_package ap ON ap.order_id = os.order_id ");
		if (type == -1) {
			condition.append(" mb.transit_datetime >= '" + start
					+ " 00:00:00' and mb.transit_datetime <= '" + end
					+ " 23:59:59'  and os.status<>"
							+ OrderStockBean.STATUS4
							+ " ");
			paramMap.put("condition", condition.toString());
		} else if (inStockAgingTypeMap.get(type) != null) {
			condition
					.append(" mb.transit_datetime >= '"
							+ start
							+ " 00:00:00' and mb.transit_datetime <= '"
							+ end
							+ " 23:59:59'  and os.status<>"
							+ OrderStockBean.STATUS4
							+ "   and timestampdiff(hour,sb.create_datetime, mb.transit_datetime) >= "
							+ inStockAgingTypeMap.get(type).get("start")
							+ " and timestampdiff(hour,sb.create_datetime, mb.transit_datetime) < "
							+ inStockAgingTypeMap.get(type).get("end"));
			paramMap.put("condition", condition.toString());
		} else {
			j.setMsg("类型参数错误！");
			return j;
		}

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);
		
		orderExportPrint(listRows, response);
		j.setSuccess(true);
		return j;
	}
	
	public void orderExportPrint (List<HashMap<String, String>> orderList , HttpServletResponse response) throws Exception {
		
		HashMap<String, String> map = null;
		int size = 0;
		
		String now = DateUtil.getNow().substring(0, 10);
		String fileName = now;
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		
		header.add("序号");
		header.add("订单编号");
		header.add("CK单号");
		header.add("分拣开始时间");
		header.add("分拣完成时间");
		header.add("分播开始时间");
		header.add("分播结束时间");
		header.add("复核完成时间");
		header.add("交接出库时间");
		
		size = header.size();
		
		if (orderList != null && orderList.size() > 0) {
			int x = orderList.size();
			for (int i = 0; i < x; i++) {
				map =  orderList.get(i);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(i+1 + "");
				tmp.add(map.get("orderCode"));
				tmp.add(map.get("ckCode"));
				tmp.add(map.get("sortingStart"));
				tmp.add(map.get("sortingEnd"));
				tmp.add(map.get("allocateStart"));
				tmp.add(map.get("allocateEnd"));
				tmp.add(map.get("reviewTime"));
				tmp.add(map.get("associateTime"));
				bodies.add(tmp);
			}
		}
		headers.add(header);

		/*允许合并列,下标从0开始，即0代表第一列*/
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		
		/*允许合并行,下标从0开始，即0代表第一行*/
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
        
		/*
		 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
		 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
		 * 
		 * */
		excel.setColMergeCount(size);
        
		
		/*
		 * 设置需要自己设置样式的行，以每个bodies为参照
		 * 具体的样式设置参考 DemoExcel.java中的setStyle方法
		 * 具体可以参照执行后导出的excel样式及DemoExcel中的setStyle方法
		 */
        List<Integer> row  = new ArrayList<Integer>();
        
        /*设置需要自己设置样式的列，以每个bodies为参照*/
        List<Integer> col  = new ArrayList<Integer>();
        
        excel.setRow(row);
        excel.setCol(col);
        
        //调用填充表头方法
        excel.buildListHeader(headers);
        
        //调用填充数据区方法
        excel.buildListBody(bodies);
        //文件输出
        excel.exportToExcel(fileName, response, "");
	}
}
