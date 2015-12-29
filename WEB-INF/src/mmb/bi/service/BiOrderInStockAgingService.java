package mmb.bi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.bi.model.BiOrderInStockAging;
import mmb.common.dao.CommonDao;
import mmb.rec.sys.easyui.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.util.Arith;
import adultadmin.util.StringUtil;

@Service
public class BiOrderInStockAgingService {
	@Autowired
	public CommonDao commonMapper;

	public Json getInStockAging(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2123)) {
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
					"date_format(datet,'%Y-%m-%d') datex,concat(type,'') type,concat(orderCount,'') orderCount ");
			paramMap.put("table",
					" (select create_date datet,in_stock_aging_type type, "
							+ "sum(order_count) orderCount "
							+ " from bi_order_in_stock_aging where "
							+ condition.toString() + " create_date >= '"
							+ start + "' and create_date <= '" + end
							+ "' group by datet,in_stock_aging_type) tb ");
			paramMap.put("condition", "1=1");
			avgParamMap.put("column",
					" datex,type,concat(orderCount,'') orderCount ");
			avgParamMap
					.put("table",
							" (select '日均' datex,concat(type,'') type,round(avg(orderCount),2) orderCount from  "
									+ "(select create_date datet,in_stock_aging_type type,sum(order_count) orderCount "
									+ "from bi_order_in_stock_aging where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet,in_stock_aging_type) tb group by type) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
			paramMap.put("column",
					" datet datex,type,concat(orderCount,'') orderCount ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datex,'%X年-%m月') datet,concat(type,'') type, round(avg(orderCount),2) orderCount from "
							+ "(select create_date datex,in_stock_aging_type type,sum(order_count) orderCount from bi_order_in_stock_aging where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datex,in_stock_aging_type) tb group by datet,type) tb1 ");
			paramMap.put("condition", "1=1");
			avgParamMap
					.put("column",
							" datex,concat(type,'') type,concat(orderCount,'') orderCount ");
			avgParamMap
					.put("table",
							" (select '日均' datex,type,round(avg(orderCount),2) orderCount from  "
									+ "(select create_date datet,in_stock_aging_type type,sum(order_count) orderCount from bi_order_in_stock_aging where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet,in_stock_aging_type) tb group by type) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
			paramMap.put("column",
					" datex,type,concat(orderCount,'') orderCount ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datet, '%X年') datex,concat(type,'') type,round(avg(orderCount),2) orderCount from "
							+ "(select DATE_FORMAT(create_date,'%X-%m-01') datet,in_stock_aging_type type,sum(order_count) orderCount from bi_order_in_stock_aging where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet,in_stock_aging_type) tb group by datex,type) tb1 ");
			paramMap.put("condition", " 1=1");
			avgParamMap.put("column",
					" datex,type,concat(orderCount,'') orderCount ");
			avgParamMap
					.put("table",
							" (select '月均' datex,concat(type,'') type,round(avg(orderCount),2) orderCount from "
									+ "(select DATE_FORMAT(create_date,'%X年-%m月') datet,in_stock_aging_type type,sum(order_count) orderCount from bi_order_in_stock_aging where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet,in_stock_aging_type) tb group by type) tb1  ");
			avgParamMap.put("condition", " 1=1");
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		List<HashMap<String, String>> listFooter = commonMapper
				.getCommonInfo(avgParamMap);
		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);

		HashMap<String, String> dataMap = new HashMap<String, String>();
		List<String> returntimesList = new ArrayList<String>();
		HashMap<Integer, HashMap<String, String>> inStockAgingTypeMap = BiOrderInStockAging.inStockAgingTypeMap;
		List<HashMap<String, String>> returnList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> returnMap = null;
		if (listRows.size() != 0) {
			for (HashMap<String, String> map : listRows) {
				dataMap.put(map.get("datex") + "&&" + map.get("type"),
						map.get("orderCount"));
				if (returntimesList.contains(map.get("datex"))) {
					continue;
				}
				returntimesList.add(map.get("datex"));
			}
			for (String s : returntimesList) {
				returnMap = new HashMap<String, String>();
				returnMap.put("createDate", s);
				returnList.add(returnMap);
				for (int key : inStockAgingTypeMap.keySet()) {
					if (dataMap.get(s + "&&" + key) == null) {
						returnMap.put("inStockAging" + key, "0");
					} else {
						returnMap.put("inStockAging" + key,
								dataMap.get(s + "&&" + key));
					}
				}
			}
		}

		List<HashMap<String, String>> footerList = new ArrayList<HashMap<String, String>>();
		returnMap = new HashMap<String, String>();
		for (int key : inStockAgingTypeMap.keySet()) {
			for (HashMap<String, String> map : listFooter) {
				returnMap.put("createDate", map.get("datex"));
				if (key == Integer.valueOf(map.get("type"))) {
					returnMap.put("inStockAging" + key, map.get("orderCount"));
				}
			}
			if (returnMap.get("inStockAging" + key) == null) {
				returnMap.put("inStockAging" + key, "0");
			}
		}
		footerList.add(returnMap);

		List list = new ArrayList();
		list.add(inStockAgingTypeMap);
		list.add(returnList);
		list.add(footerList);

		j.setSuccess(true);
		j.setObj(list);
		return j;
	}

	public Json getInStockAgingChart(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2123)) {
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
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		paramMap.put("column", " concat(orderCount, '') orderCount, type ");
		paramMap.put(
				"table",
				"  (select sum(order_count) orderCount,concat(in_stock_aging_type,'') type from bi_order_in_stock_aging  where "
						+ condition.toString()
						+ " create_date >= '"
						+ start
						+ "' and create_date <= '" + end + "' group by in_stock_aging_type) tb ");
		paramMap.put("condition", " 1=1 ");

		HashMap<Integer, HashMap<String, String>> inStockAgingTypeMap = BiOrderInStockAging.inStockAgingTypeMap;
		int len = inStockAgingTypeMap.size();
		String[] types = new String[len];
		float[] orderCounts = new float[len];
		float[] percent = new float[len];

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);
		
		int t = 0;
		for (int key : inStockAgingTypeMap.keySet()) {
			types[t] = inStockAgingTypeMap.get(key).get("type");
			t++;
		}
		if (listRows.size() != 0) {
			int i = 0;
			for (int key : inStockAgingTypeMap.keySet()) {
				for (HashMap<String, String> map : listRows) {
					if (key == StringUtil.toInt(map == null ? null : map
							.get("type"))) {
						orderCounts[i] = StringUtil.toFloat(map
								.get("orderCount"));
					}
				}
				i++;
			}
		}

		float sum = Arith.add(orderCounts);

		if (sum != 0) {
			int i = 0;
			for (int key : inStockAgingTypeMap.keySet()) {
				percent[i] = Arith.mul(Arith.div(orderCounts[i], sum, 4), 100);
				i++;
			}
		}

		List returnList = new ArrayList();
		returnList.add(types);
		returnList.add(orderCounts);
		returnList.add(percent);

		j.setSuccess(true);
		j.setObj(returnList);
		return j;
	}
}
