package mmb.bi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bi.dao.BiOrderFinishRateDao;
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
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Service
public class BiOrderFinishRateService {
	@Autowired
	public BiOrderFinishRateDao biOrderFinishRateMapper;
	@Autowired
	public CommonDao commonMapper;

	public Json getIntradayOrderComplete(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2117)) {
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
					"datet datex,outOrderCount,deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'%') end completePercent ");
			paramMap.put(
					"table",
					" (select create_date datet,sum(intraday_out_order_count) outOrderCount, sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet) tb ");
			paramMap.put("condition", "1=1");
			avgParamMap
					.put("column",
							" datex,outOrderCount,deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'%') end completePercent ");
			avgParamMap
					.put("table",
							" (select '日均' datex,round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount from  (select create_date datet,sum(intraday_out_order_count) outOrderCount, sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet) tb) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
			paramMap.put(
					"column",
					" datet datex,outOrderCount,deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'%') end completePercent ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datex,'%X年-%m月') datet, round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount from (select create_date datex,sum(intraday_out_order_count) outOrderCount,sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datex) tb group by datet) tb1 ");
			paramMap.put("condition", "1=1");
			avgParamMap
					.put("column",
							" datex,outOrderCount,deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'%') end completePercent ");
			avgParamMap
					.put("table",
							" (select '日均' datex,round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount from  (select create_date datet,sum(intraday_out_order_count) outOrderCount, sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet) tb) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
			paramMap.put(
					"column",
					" datex,outOrderCount,deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'%') end completePercent ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datet, '%X年') datex,round(avg(outOrderCount),2) outOrderCount, round(avg(deliverOrderCount),2) deliverOrderCount from (select DATE_FORMAT(create_date,'%X-%m-01') datet,sum(intraday_out_order_count) outOrderCount,sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet) tb group by datex) tb1 ");
			paramMap.put("condition", " 1=1");
			avgParamMap
					.put("column",
							" datex,outOrderCount,deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'%') end completePercent ");
			avgParamMap
					.put("table",
							" (select '月均' datex,round(avg(outOrderCount),2) outOrderCount, round(avg(deliverOrderCount),2) deliverOrderCount from (select DATE_FORMAT(create_date,'%X年-%m月') datet,sum(intraday_out_order_count) outOrderCount,sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet) tb ) tb1  ");
			avgParamMap.put("condition", " 1=1");
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		List<HashMap<String, String>> listFooter = biOrderFinishRateMapper
				.getIntradayOrderCompleteInfo(avgParamMap);
		List<HashMap<String, String>> listRows = biOrderFinishRateMapper
				.getIntradayOrderCompleteInfo(paramMap);

		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		easyuiDataGridJson.setRows(listRows);
		easyuiDataGridJson.setFooter(listFooter);
		j.setSuccess(true);
		j.setObj(easyuiDataGridJson);
		return j;
	}

	public Json getCutOffOrderComplete(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2117)) {
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
					"datet datex,realcutOffOutOrderCount,cutOffOutOrderCount,case cutOffOutOrderCount when 0 then '0' else concat(round(realcutOffOutOrderCount/cutOffOutOrderCount * 100 ,2),'%') end completePercent ");
			paramMap.put(
					"table",
					" (select create_date datet,sum(intraday_real_deliver_order_count) realcutOffOutOrderCount, sum(cut_off_out_order_count) cutOffOutOrderCount from bi_order_finish_rate where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet) tb ");
			paramMap.put("condition", "1=1");
			avgParamMap
					.put("column",
							" datex,realcutOffOutOrderCount,cutOffOutOrderCount,case cutOffOutOrderCount when 0 then '0' else concat(round(realcutOffOutOrderCount/cutOffOutOrderCount * 100 ,2),'%') end completePercent ");
			avgParamMap
					.put("table",
							" (select '日均' datex,round(avg(realcutOffOutOrderCount),2) realcutOffOutOrderCount,round(avg(cutOffOutOrderCount),2) cutOffOutOrderCount from  (select create_date datet,sum(intraday_real_deliver_order_count) realcutOffOutOrderCount, sum(cut_off_out_order_count) cutOffOutOrderCount from bi_order_finish_rate where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet) tb) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startMonth.equals("") && !endMonth.equals("")) {
			start = startMonth + "-01";
			end = endMonth + "-31";
			paramMap.put(
					"column",
					" datet datex,realcutOffOutOrderCount,cutOffOutOrderCount,case cutOffOutOrderCount when 0 then '0' else concat(round(realcutOffOutOrderCount/cutOffOutOrderCount * 100 ,2),'%') end completePercent ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datex,'%X年-%m月') datet, round(avg(realcutOffOutOrderCount),2) realcutOffOutOrderCount,round(avg(cutOffOutOrderCount),2) cutOffOutOrderCount from (select create_date datex,sum(intraday_real_deliver_order_count) realcutOffOutOrderCount,sum(cut_off_out_order_count) cutOffOutOrderCount from bi_order_finish_rate where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datex) tb group by datet) tb1 ");
			paramMap.put("condition", "1=1");
			avgParamMap
					.put("column",
							" datex,realcutOffOutOrderCount,cutOffOutOrderCount,case cutOffOutOrderCount when 0 then '0' else concat(round(realcutOffOutOrderCount/cutOffOutOrderCount * 100 ,2),'%') end completePercent ");
			avgParamMap
					.put("table",
							" (select '日均' datex,round(avg(realcutOffOutOrderCount),2) realcutOffOutOrderCount,round(avg(cutOffOutOrderCount),2) cutOffOutOrderCount from  (select create_date datet,sum(intraday_real_deliver_order_count) realcutOffOutOrderCount, sum(cut_off_out_order_count) cutOffOutOrderCount from bi_order_finish_rate where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet) tb) tb1 ");
			avgParamMap.put("condition", "1=1");
		} else if (!startYear.equals("") && !endYear.equals("")) {
			start = startYear + "-01-01";
			end = endYear + "-12-31";
			paramMap.put(
					"column",
					" datex,realcutOffOutOrderCount,cutOffOutOrderCount,case cutOffOutOrderCount when 0 then '0' else concat(round(realcutOffOutOrderCount/cutOffOutOrderCount * 100 ,2),'%') end completePercent ");
			paramMap.put(
					"table",
					" (select DATE_FORMAT(datet, '%X年') datex,round(avg(realcutOffOutOrderCount),2) realcutOffOutOrderCount, round(avg(cutOffOutOrderCount),2) cutOffOutOrderCount from (select DATE_FORMAT(create_date,'%X-%m-01') datet,sum(intraday_real_deliver_order_count) realcutOffOutOrderCount,sum(cut_off_out_order_count) cutOffOutOrderCount from bi_order_finish_rate where "
							+ condition.toString()
							+ " create_date >= '"
							+ start
							+ "' and create_date <= '"
							+ end
							+ "' group by datet) tb group by datex) tb1 ");
			paramMap.put("condition", " 1=1");
			avgParamMap
					.put("column",
							" datex,realcutOffOutOrderCount,cutOffOutOrderCount,case cutOffOutOrderCount when 0 then '0' else concat(round(realcutOffOutOrderCount/cutOffOutOrderCount * 100 ,2),'%') end completePercent ");
			avgParamMap
					.put("table",
							" (select '月均' datex,round(avg(realcutOffOutOrderCount),2) realcutOffOutOrderCount, round(avg(cutOffOutOrderCount),2) cutOffOutOrderCount from (select DATE_FORMAT(create_date,'%X年-%m月') datet,sum(intraday_real_deliver_order_count) realcutOffOutOrderCount,sum(cut_off_out_order_count) cutOffOutOrderCount from bi_order_finish_rate where "
									+ condition.toString()
									+ " create_date >= '"
									+ start
									+ "' and create_date <= '"
									+ end
									+ "' group by datet) tb ) tb1  ");
			avgParamMap.put("condition", " 1=1");
		} else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}

		List<HashMap<String, String>> listFooter = biOrderFinishRateMapper
				.getIntradayOrderCompleteInfo(avgParamMap);
		List<HashMap<String, String>> listRows = biOrderFinishRateMapper
				.getIntradayOrderCompleteInfo(paramMap);

		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		easyuiDataGridJson.setRows(listRows);
		easyuiDataGridJson.setFooter(listFooter);
		j.setSuccess(true);
		j.setObj(easyuiDataGridJson);
		return j;
	}

	public Json getOrderComplete(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("尚未登录！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2117)) {
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
		String type = StringUtil.convertNull(request.getParameter("type"));

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

		if (type.equals("intradayComplete")) {
			if (!startTime.equals("") && !endTime.equals("")) {
				start = startTime;
				end = endTime;
				paramMap.put(
						"column",
						"date_format(datet,'%Y-%m-%d') datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent,case outOrderCount when 0 then '0' else concat(round(intradayCutOffOrderCount/outOrderCount * 100 ,2),'') end cutOffPercent ");
				paramMap.put(
						"table",
						" (select create_date datet,sum(intraday_out_order_count) outOrderCount, sum(intraday_deliver_order_count) deliverOrderCount, sum(intraday_cut_off_order_count) intradayCutOffOrderCount from bi_order_finish_rate where "
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
						" datet datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent,case outOrderCount when 0 then '0' else concat(round(intradayCutOffOrderCount/outOrderCount * 100 ,2),'') end cutOffPercent ");
				paramMap.put(
						"table",
						" (select DATE_FORMAT(datex,'%X年-%m月') datet, round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount,round(avg(intradayCutOffOrderCount),2) intradayCutOffOrderCount from (select create_date datex,sum(intraday_out_order_count) outOrderCount,sum(intraday_deliver_order_count) deliverOrderCount, sum(intraday_cut_off_order_count) intradayCutOffOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datex) tb group by datet) tb1 ");
				paramMap.put("condition", "1=1");
			} else if (!startYear.equals("") && !endYear.equals("")) {
				start = startYear + "-01-01";
				end = endYear + "-12-31";
				paramMap.put(
						"column",
						" datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent,case outOrderCount when 0 then '0' else concat(round(intradayCutOffOrderCount/outOrderCount * 100 ,2),'') end cutOffPercent ");
				paramMap.put(
						"table",
						" (select DATE_FORMAT(datet, '%X年') datex,round(avg(outOrderCount),2) outOrderCount, round(avg(deliverOrderCount),2) deliverOrderCount, round(avg(intradayCutOffOrderCount),2) intradayCutOffOrderCount from (select DATE_FORMAT(create_date,'%X-%m-01') datet,sum(intraday_out_order_count) outOrderCount,sum(intraday_deliver_order_count) deliverOrderCount,sum(intraday_cut_off_order_count) intradayCutOffOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datet) tb group by datex) tb1 ");
				paramMap.put("condition", " 1=1");
			} else {
				j.setMsg("请输入时间作为条件查询！");
				return j;
			}
//		} else if (type.equals("intradayCutOff")) {
//			if (!startTime.equals("") && !endTime.equals("")) {
//				start = startTime;
//				end = endTime;
//				paramMap.put(
//						"column",
//						"date_format(datet,'%Y-%m-%d') datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
//				paramMap.put(
//						"table",
//						" (select create_date datet,sum(intraday_out_order_count) outOrderCount, sum(intraday_cut_off_order_count) deliverOrderCount from bi_order_finish_rate where "
//								+ condition.toString()
//								+ " create_date >= '"
//								+ start
//								+ "' and create_date <= '"
//								+ end
//								+ "' group by datet) tb ");
//				paramMap.put("condition", "1=1");
//			} else if (!startMonth.equals("") && !endMonth.equals("")) {
//				start = startMonth + "-01";
//				end = endMonth + "-31";
//				paramMap.put(
//						"column",
//						" datet datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
//				paramMap.put(
//						"table",
//						" (select DATE_FORMAT(datex,'%X年-%m月') datet, round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount from (select create_date datex,sum(intraday_out_order_count) outOrderCount,sum(intraday_cut_off_order_count) deliverOrderCount from bi_order_finish_rate where "
//								+ condition.toString()
//								+ " create_date >= '"
//								+ start
//								+ "' and create_date <= '"
//								+ end
//								+ "' group by datex) tb group by datet) tb1 ");
//				paramMap.put("condition", "1=1");
//			} else if (!startYear.equals("") && !endYear.equals("")) {
//				start = startYear + "-01-01";
//				end = endYear + "-12-31";
//				paramMap.put(
//						"column",
//						" datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case deliverOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
//				paramMap.put(
//						"table",
//						" (select DATE_FORMAT(datet, '%X年') datex,round(avg(outOrderCount),2) outOrderCount, round(avg(deliverOrderCount),2) deliverOrderCount from (select DATE_FORMAT(create_date,'%X-%m-01') datet,sum(intraday_out_order_count) outOrderCount,sum(intraday_cut_off_order_count) deliverOrderCount from bi_order_finish_rate where "
//								+ condition.toString()
//								+ " create_date >= '"
//								+ start
//								+ "' and create_date <= '"
//								+ end
//								+ "' group by datet) tb group by datex) tb1 ");
//				paramMap.put("condition", " 1=1");
//			} else {
//				j.setMsg("请输入时间作为条件查询！");
//				return j;
//			}
		} else if (type.equals("cutOff")) {
			if (!startTime.equals("") && !endTime.equals("")) {
				start = startTime;
				end = endTime;
				paramMap.put(
						"column",
						"date_format(datet,'%Y-%m-%d') datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent,case thecount when 0 then '0' else concat(round(intradayCutOffOrderCount/thecount * 100 ,2),'') end cutOffPercent  ");
				paramMap.put(
						"table",
						" (select create_date datet,sum(cut_off_out_order_count) outOrderCount, sum(intraday_real_deliver_order_count) deliverOrderCount,sum(intraday_out_order_count) thecount, sum(intraday_cut_off_order_count) intradayCutOffOrderCount from bi_order_finish_rate where "
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
						" datet datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent,case thecount when 0 then '0' else concat(round(intradayCutOffOrderCount/thecount * 100 ,2),'') end cutOffPercent  ");
				paramMap.put(
						"table",
						" (select DATE_FORMAT(datex,'%X年-%m月') datet, round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount,round(avg(thecount),2) thecount,round(avg(intradayCutOffOrderCount),2) intradayCutOffOrderCount from (select create_date datex,sum(cut_off_out_order_count) outOrderCount,sum(intraday_real_deliver_order_count) deliverOrderCount,sum(intraday_out_order_count) thecount, sum(intraday_cut_off_order_count) intradayCutOffOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datex) tb group by datet) tb1 ");
				paramMap.put("condition", "1=1");
			} else if (!startYear.equals("") && !endYear.equals("")) {
				start = startYear + "-01-01";
				end = endYear + "-12-31";
				paramMap.put(
						"column",
						" datex,concat(outOrderCount,'') outOrderCount,concat(deliverOrderCount,'') deliverOrderCount,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent,case thecount when 0 then '0' else concat(round(intradayCutOffOrderCount/thecount * 100 ,2),'') end cutOffPercent  ");
				paramMap.put(
						"table",
						" (select DATE_FORMAT(datet, '%X年') datex,round(avg(outOrderCount),2) outOrderCount, round(avg(deliverOrderCount),2) deliverOrderCount,round(avg(thecount),2) thecount,round(avg(intradayCutOffOrderCount),2) intradayCutOffOrderCount from (select DATE_FORMAT(create_date,'%X-%m-01') datet,sum(cut_off_out_order_count) outOrderCount,sum(intraday_real_deliver_order_count) deliverOrderCount,sum(intraday_out_order_count) thecount, sum(intraday_cut_off_order_count) intradayCutOffOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datet) tb group by datex) tb1 ");
				paramMap.put("condition", " 1=1");
			} else {
				j.setMsg("请输入时间作为条件查询！");
				return j;
			}
		} else {
			j.setMsg("类型错误！");
			return j;
		}

		List<HashMap<String, String>> listRows = biOrderFinishRateMapper
				.getIntradayOrderCompleteInfo(paramMap);

		List returnList = getReturnStrings(listRows);

		j.setSuccess(true);
		j.setObj(returnList);
		return j;
	}

	public List getReturnStrings(List<HashMap<String, String>> listRows) {
		String[] returntimes = null;
		float[] outOrderCounts = null;
		float[] deliverOrderCounts = null;
		float[] completePercent = null;
		float[] cutOffPercent = null;

		if (listRows.size() != 0) {
			int x = listRows.size();
			returntimes = new String[x];
			outOrderCounts = new float[x];
			deliverOrderCounts = new float[x];
			completePercent = new float[x];
			cutOffPercent = new float[x];
		}
		int i = 0;
		for (HashMap<String, String> map : listRows) {
			returntimes[i] = map.get("datex");
			outOrderCounts[i] = StringUtil.toFloat(map.get("outOrderCount"));
			deliverOrderCounts[i] = StringUtil.toFloat(map
					.get("deliverOrderCount"));
			completePercent[i] = StringUtil.toFloat(map.get("completePercent"));
			cutOffPercent[i] = StringUtil.toFloat(map.get("cutOffPercent"));
			i++;
		}
		List returnStrings = new ArrayList();
		returnStrings.add(returntimes);
		returnStrings.add(outOrderCounts);
		returnStrings.add(deliverOrderCounts);
		returnStrings.add(completePercent);
		returnStrings.add(cutOffPercent);

		return returnStrings;
	}

	public Json getOrderCompletePercent(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("尚未登录！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2118)) {
			j.setMsg("没有权限！");
			return j;
		}

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
		String type = StringUtil.convertNull(request.getParameter("type"));

		StringBuffer condition = new StringBuffer();
		String start = "";
		String end = "";
		HashMap<String, String> paramMap = new HashMap<String, String>();

		if (type.equals("intradayComplete")) {
			if (!startTime.equals("") && !endTime.equals("")) {
				start = startTime;
				end = endTime;
				paramMap.put(
						"column",
						"stockArea,date_format(datet,'%Y-%m-%d') datex,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
				paramMap.put(
						"table",
						" (select concat(stock_area,'') stockArea,create_date datet,sum(intraday_out_order_count) outOrderCount, sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datet,stock_area) tb ");
				paramMap.put("condition", "1=1");
			} else if (!startMonth.equals("") && !endMonth.equals("")) {
				start = startMonth + "-01";
				end = endMonth + "-31";
				paramMap.put(
						"column",
						" stockArea,datet datex,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
				paramMap.put(
						"table",
						" (select concat(stockArea,'') stockArea,DATE_FORMAT(datex,'%X年-%m月') datet, round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount from (select stock_area stockArea,create_date datex,sum(intraday_out_order_count) outOrderCount,sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datex,stock_area) tb group by datet,stockArea) tb1 ");
				paramMap.put("condition", "1=1");
			} else if (!startYear.equals("") && !endYear.equals("")) {
				start = startYear + "-01-01";
				end = endYear + "-12-31";
				paramMap.put(
						"column",
						" stockArea,datex,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
				paramMap.put(
						"table",
						" (select concat(stockArea,'') stockArea,DATE_FORMAT(datet, '%X年') datex,round(avg(outOrderCount),2) outOrderCount, round(avg(deliverOrderCount),2) deliverOrderCount from (select stock_area stockArea,DATE_FORMAT(create_date,'%X-%m-01') datet,sum(intraday_out_order_count) outOrderCount,sum(intraday_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datet,stock_area) tb group by datet,stockArea) tb1 ");
				paramMap.put("condition", " 1=1");
			} else {
				j.setMsg("请输入时间作为条件查询！");
				return j;
			}
		} else if (type.equals("cutOff")) {
			if (!startTime.equals("") && !endTime.equals("")) {
				start = startTime;
				end = endTime;
				paramMap.put(
						"column",
						"stockArea,date_format(datet,'%Y-%m-%d') datex,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
				paramMap.put(
						"table",
						" (select concat(stock_area,'') stockArea,create_date datet,sum(cut_off_out_order_count) outOrderCount, sum(intraday_real_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datet,stock_area) tb ");
				paramMap.put("condition", "1=1");
			} else if (!startMonth.equals("") && !endMonth.equals("")) {
				start = startMonth + "-01";
				end = endMonth + "-31";
				paramMap.put(
						"column",
						" stockArea,datet datex,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
				paramMap.put(
						"table",
						" (select concat(stockArea,'') stockArea,DATE_FORMAT(datex,'%X年-%m月') datet, round(avg(outOrderCount),2) outOrderCount,round(avg(deliverOrderCount),2) deliverOrderCount from (select stock_area stockArea,create_date datex,sum(cut_off_out_order_count) outOrderCount,sum(intraday_real_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datex,stock_area) tb group by datet,stockArea) tb1 ");
				paramMap.put("condition", "1=1");
			} else if (!startYear.equals("") && !endYear.equals("")) {
				start = startYear + "-01-01";
				end = endYear + "-12-31";
				paramMap.put(
						"column",
						" stockArea,datex,case outOrderCount when 0 then '0' else concat(round(deliverOrderCount/outOrderCount * 100 ,2),'') end completePercent ");
				paramMap.put(
						"table",
						" (select concat(stockArea,'') stockArea,DATE_FORMAT(datet, '%X年') datex,round(avg(outOrderCount),2) outOrderCount, round(avg(deliverOrderCount),2) deliverOrderCount from (select stock_area stockArea,DATE_FORMAT(create_date,'%X-%m-01') datet,sum(cut_off_out_order_count) outOrderCount,sum(intraday_real_deliver_order_count) deliverOrderCount from bi_order_finish_rate where "
								+ condition.toString()
								+ " create_date >= '"
								+ start
								+ "' and create_date <= '"
								+ end
								+ "' group by datet,stock_area) tb group by datet,stockArea) tb1 ");
				paramMap.put("condition", " 1=1");
			} else {
				j.setMsg("请输入时间作为条件查询！");
				return j;
			}
		} else {
			j.setMsg("类型错误！");
			return j;
		}

		List<HashMap<String, String>> listRows = biOrderFinishRateMapper
				.getIntradayOrderCompleteInfo(paramMap);

		HashMap<String, String> dataMap = new HashMap<String, String>();

		String[] returntimes = null;
		List<String> returntimesList = new ArrayList<String>();
		String[] returnAreas = null;
		List returnStrings = new ArrayList();
		HashMap<Integer, String> availableAreaMap = ProductStockBean.stockoutAvailableAreaMap;
		returnAreas = new String[availableAreaMap.size()];
		returnStrings.add(returnAreas);
		if (listRows.size() != 0) {
			for (HashMap<String, String> map : listRows) {
				dataMap.put(map.get("datex") + "&&" + map.get("stockArea"),
						map.get("completePercent"));
				if (returntimesList.contains(map.get("datex"))) {
					continue;
				}
				returntimesList.add(map.get("datex"));
			}
			int t = returntimesList.size();
			returntimes = new String[t];
			for (int i = 0; i < t; i++) {
				returntimes[i] = returntimesList.get(i);
			}
			returnStrings.add(returntimes);
			for (int key : availableAreaMap.keySet()) {

				float[] completePercent = new float[t];
				int i = 0;
				for (String s : returntimes) {
					if (dataMap.get(s + "&&" + key) == null) {
						completePercent[i] = 0;
					} else {
						completePercent[i] = StringUtil.toFloat(dataMap.get(s
								+ "&&" + key));
					}
					i++;
				}
				returnStrings.add(completePercent);
			}
		}
		j.setSuccess(true);
		j.setObj(returnStrings);
		return j;
	}

	public Json getOrderDetail(HttpServletRequest request,
			EasyuiDataGrid easyuiDataGrid) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
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
		String type = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("type")));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" os.stock_area in (").append(stockArea).append(") ");
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
		String startBefore = DateUtil.getBackFromDate(start, 1);

		paramMap.put("column", "uo.code orderCode,p.code productCode,"
				+ "p.name productName,pl.name productLineName,"
				+ "uo.address address,uos.name statusName,"
				+ "date_format( uo.create_datetime, '%Y-%m-%d %T' )  createDatetime,"
				+ "concat(os.stock_area,'') stockArea,"
				+ "date_format(mb.transit_datetime,'%Y-%m-%d %T') transitDatetime,"
				+ "dci.name deliverName,uo.remark remark ");
		paramMap.put(
				"table",
				"user_order uo "
						+ "join user_order_status uos on uo.status=uos.id "
						+ "JOIN order_stock os force INDEX (Index_5) ON uo.id = os.order_id "
						+ "JOIN order_stock_product osp ON os.id = osp.order_stock_id "
						+ "JOIN product p ON osp.product_id = p.id "
						+ "JOIN product_line_catalog plc ON ( "
						+ "	plc.catalog_id = p.parent_id1 "
						+ "	OR plc.catalog_id = p.parent_id2 "
						+ ") "
						+ "JOIN product_line pl ON plc.product_line_id = pl.id "
						+ "LEFT JOIN deliver_corp_info dci ON uo.deliver = dci.id "
						+ "LEFT JOIN mailing_batch_package mbp ON uo.code = mbp.order_code "
						+ "LEFT JOIN mailing_batch mb ON mbp.mailing_batch_code = mb.code ");
		if (type.equals("outOrder")) {
			condition.append(" os.create_datetime >= '" + start
					+ " 00:00:00' and os.create_datetime <= '" + end
					+ " 23:59:59' and os.status<>" + OrderStockBean.STATUS4);
			paramMap.put("condition", condition.toString() + " limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
		} else if (type.equals("deliverOrder")) {
			condition.append(" os.create_datetime >= '" + start
					+ " 00:00:00' and os.create_datetime <= '" + end
					+ " 23:59:59' and os.status<>" + OrderStockBean.STATUS4
					+ " and mb.transit_datetime >= '" + start
					+ " 00:00:00' and mb.transit_datetime <= '" + end
					+ " 23:59:59'");
			paramMap.put("condition", condition.toString() + " limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
		} else if (type.equals("cutOffOutOrder")) {
			condition.append(" os.create_datetime > '" + startBefore
					+ " 17:00:00' and os.create_datetime <= '" + end
					+ " 17:00:00' and os.status<>" + OrderStockBean.STATUS4);
			paramMap.put("condition", condition.toString() + " limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
		} else if (type.equals("realcutOffOutOrder")) {
			condition.append(" os.create_datetime > '" + startBefore
					+ " 17:00:00' and os.create_datetime <= '" + end
					+ " 17:00:00' and os.status<>" + OrderStockBean.STATUS4
					+ " " + "and mb.transit_datetime >= '" + start
					+ " 00:00:00' and mb.transit_datetime <= '" + end
					+ " 23:59:59'");
			paramMap.put("condition", condition.toString() + " limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
		} else {
			j.setMsg("类型参数错误！");
			return j;
		}

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);
		for (HashMap<String, String> map : listRows) {
			if(map != null) {
				map.put("stockAreaName", ProductStockBean.stockoutAvailableAreaMap.get(StringUtil.toInt(map.get("stockArea") + "")));
			}
		}
		paramMap.put("column", " concat(thecount,'') thecount from (select count(osp.id) thecount ");
		paramMap.put("condition", condition.toString() + ") tb");
		List<HashMap<String, String>> countRow = commonMapper
				.getCommonInfo(paramMap);

		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) StringUtil.toInt(countRow.get(0).get("thecount")));
		easyuiDataGridJson.setRows(listRows);
		j.setSuccess(true);
		j.setObj(easyuiDataGridJson);
		return j;
	}
	
	public Json exportOrderDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
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
		String type = StringUtil.dealParam(StringUtil.convertNull(request
				.getParameter("type")));

		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" os.stock_area in (").append(stockArea).append(") ");
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
		String startBefore = DateUtil.getBackFromDate(start, 1);

		paramMap.put("column", "uo.code orderCode,p.code productCode,"
				+ "p.name productName,pl.name productLineName,"
				+ "uo.address address,uos.name statusName,"
				+ "date_format( uo.create_datetime, '%Y-%m-%d %T' )  createDatetime,"
				+ "concat(os.stock_area,'') stockArea,"
				+ "date_format(mb.transit_datetime,'%Y-%m-%d %T') transitDatetime,"
				+ "dci.name deliverName,uo.remark remark ");
		paramMap.put(
				"table",
				"user_order uo "
						+ "join user_order_status uos on uo.status=uos.id "
						+ "JOIN order_stock os force INDEX (Index_5) ON uo.id = os.order_id "
						+ "JOIN order_stock_product osp ON os.id = osp.order_stock_id "
						+ "JOIN product p ON osp.product_id = p.id "
						+ "JOIN product_line_catalog plc ON ( "
						+ "	plc.catalog_id = p.parent_id1 "
						+ "	OR plc.catalog_id = p.parent_id2 "
						+ ") "
						+ "JOIN product_line pl ON plc.product_line_id = pl.id "
						+ "LEFT JOIN deliver_corp_info dci ON uo.deliver = dci.id "
						+ "LEFT JOIN mailing_batch_package mbp ON uo.code = mbp.order_code "
						+ "LEFT JOIN mailing_batch mb ON mbp.mailing_batch_code = mb.code ");
		if (type.equals("outOrder")) {
			condition.append(" os.create_datetime >= '" + start
					+ " 00:00:00' and os.create_datetime <= '" + end
					+ " 23:59:59' and os.status<>" + OrderStockBean.STATUS4);
			paramMap.put("condition", condition.toString());
		} else if (type.equals("deliverOrder")) {
			condition.append(" os.create_datetime >= '" + start
					+ " 00:00:00' and os.create_datetime <= '" + end
					+ " 23:59:59' and os.status<>" + OrderStockBean.STATUS4
					+ " and mb.transit_datetime >= '" + start
					+ " 00:00:00' and mb.transit_datetime <= '" + end
					+ " 23:59:59'");
			paramMap.put("condition", condition.toString());
		} else if (type.equals("cutOffOutOrder")) {
			condition.append(" os.create_datetime > '" + startBefore
					+ " 17:00:00' and os.create_datetime <= '" + end
					+ " 17:00:00' and os.status<>" + OrderStockBean.STATUS4);
			paramMap.put("condition", condition.toString());
		} else if (type.equals("realcutOffOutOrder")) {
			condition.append(" os.create_datetime > '" + startBefore
					+ " 17:00:00' and os.create_datetime <= '" + end
					+ " 17:00:00' and os.status<>" + OrderStockBean.STATUS4
					+ " " + "and mb.transit_datetime >= '" + start
					+ " 00:00:00' and mb.transit_datetime <= '" + end
					+ " 23:59:59'");
			paramMap.put("condition", condition.toString());
		} else {
			j.setMsg("类型参数错误！");
			return j;
		}

		List<HashMap<String, String>> listRows = commonMapper
				.getCommonInfo(paramMap);
		for (HashMap<String, String> map : listRows) {
			if(map != null) {
				map.put("stockAreaName", ProductStockBean.stockoutAvailableAreaMap.get(StringUtil.toInt(map.get("stockArea") + "")));
			}
		}
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
		header.add("产品编号");
		header.add("产品名称");
		header.add("产品线");
		header.add("地址");
		header.add("订单状态");
		header.add("生成时间");
		header.add("出货地点");
		header.add("出货时间");
		header.add("快递公司");
		header.add("备注");
		
		size = header.size();
		
		if (orderList != null && orderList.size() > 0) {
			int x = orderList.size();
			for (int i = 0; i < x; i++) {
				map =  orderList.get(i);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(i+1 + "");
				tmp.add(map.get("orderCode"));
				tmp.add(map.get("productCode"));
				tmp.add(map.get("productName"));
				tmp.add(map.get("productLineName"));
				tmp.add(map.get("address"));
				tmp.add(map.get("statusName"));
				tmp.add(map.get("createDatetime"));
				tmp.add(map.get("stockAreaName"));
				tmp.add(map.get("transitDatetime"));
				tmp.add(map.get("deliverName"));
				tmp.add(map.get("remark"));
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
