package mmb.bi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bi.dao.BiProductLineDeliverInfoDao;
import mmb.common.dao.CommonDao;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.util.excel.ExportExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
@Service
public class BiProductLineDeliverService {
	@Autowired
	public BiProductLineDeliverInfoDao biProductLineDeliverInfoMapper;
	@Autowired
	public CommonDao commonMapper;
	/**
	 * @return 订单商品分析模块-产品线发货列表
	 * @author zhangxiaolei
	 */
	public Json getProductLineDeliverList(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2126)) {
			j.setMsg("没有权限！");
			return j;
		}
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String chinaArea = request.getParameter("chinaArea");
		String provinces = request.getParameter("provinces");
		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" a.stock_area = ").append(stockArea).append(" and");
		}
		if (!provinces.equals("")&&!provinces.equals("-1")) {
			if(!provinces.contains("-1")){
				condition.append(" a.provinces_id in (").append(provinces).append(")").append(" and");
			}
		}
		if (!chinaArea.equals("")&&!chinaArea.equals("-1")) {
			if(!chinaArea.contains("-1")){
				String ca[] = chinaArea.split(",");
				StringBuffer saSb = new StringBuffer();
				saSb.append(" a.provinces_id in (");
				for(int i = 0;i<ca.length;i++){
					if(ca[i].equals("-1")){//全国
						break;
					}
					else if(ca[i].equals("1")){//华北
						saSb.append("4,5,6,7,8");
					}
					else if(ca[i].equals("2")){//东北
						saSb.append("1,2,3");
					}
					else if(ca[i].equals("3")){//华东
						saSb.append("9,10,11,13,14");
					}
					else if(ca[i].equals("4")){//华中
						saSb.append("12,16,17,18");
					}
					else if(ca[i].equals("5")){//华南
						saSb.append("15,19,20,21");
					}
					else if(ca[i].equals("6")){//西南
						saSb.append("27,28,29,30,31");
					}
					else if(ca[i].equals("7")){//西北
						saSb.append("22,23,24,25,26");
					}
					saSb.append(",");
				}
				String str = saSb.substring(0, saSb.length()-1)+")";
				condition.append(str).append(" and");
			}
		}
		String start = "";
		String end = "";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		HashMap<String, Object> totalMap = new HashMap<String, Object>();
	    if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
			totalMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,sum(a.product_count) as product_count,a.product_line_id");
			totalMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			totalMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id order by a.stock_area,a.product_line_id");
		}
		else if (!startMonth.equals("")) {
			start = startMonth + "-01";
			end = startMonth + "-31";
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
			totalMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,sum(a.product_count) as product_count,a.product_line_id");
			totalMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			totalMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id order by a.stock_area,a.product_line_id");
		}
		else if (!startYear.equals("")) {
			start = startYear + "-01-01";
			end = startYear + "-12-31";
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
			totalMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,sum(a.product_count) as product_count,a.product_line_id");
			totalMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			totalMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id order by a.stock_area,a.product_line_id");
		}
		
		else{
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
		
		List<HashMap<String, Object>> listRows = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(paramMap);
		List<HashMap<String, Object>> listFooter = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(totalMap);
		HashMap<String, String> dataMap = new HashMap<String, String>();
		List<String> stockList = new ArrayList<String>();
		List<String> stockNameList = new ArrayList<String>();
		List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> returnMap = null;
		HashMap<String, String> productLineMap = new HashMap<String, String>();
		HashMap<String, Object> plineMap = new HashMap<String, Object>();
		plineMap.put("column", " id,name");
		plineMap.put("table", " product_line");
		plineMap.put("condition", " id>0");
		List<HashMap<String, Object>> productLineList = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(plineMap);
		if (listRows.size() != 0) {
			//遍历结果
			for (HashMap<String, Object> map : listRows ) {
				String s = map.get("stock_area").toString() + "&&" + map.get("product_line_id").toString();
				dataMap.put(s, map.get("product_count").toString());
				if (stockList.contains(map.get("stock_area").toString())) {
					continue;
				}
				//地区list
				stockList.add(map.get("stock_area").toString());
				stockNameList.add(map.get("name").toString());
			}
			//遍历地区List
			for (int index = 0;index<stockList.size();index++) {
				returnMap = new HashMap<String, Object>();
				returnMap.put("area", stockList.get(index));
				returnMap.put("stockArea", stockNameList.get(index));
				returnList.add(returnMap);
				//List<?> productLineList = service.getProductLineList("product_line.id>0");
				for(HashMap<String, Object> map : productLineList){
					productLineMap.put(map.get("id").toString(), map.get("name").toString());
					if (dataMap.get(stockList.get(index) + "&&" + map.get("id")) == null) {
						returnMap.put("productLineList" + map.get("id"), "0");
					} else {
						returnMap.put("productLineList" + map.get("id"), dataMap.get(stockList.get(index) + "&&" + map.get("id")));
					}
				}
			}
		}
		
		List<HashMap<String, Object>> footerList = new ArrayList<HashMap<String, Object>>();
		//求每条商品线的商品总数
		returnMap = new HashMap<String, Object>();
		returnMap.put("stockArea", "总量");
		for (String key : productLineMap.keySet()) {
			for (HashMap<String, Object> map : listFooter) {
				//returnMap.put("createDate", map.get("datex"));
				if (key.equals(map.get("product_line_id").toString())) {
					returnMap.put("productLineList" + key, map.get("product_count"));
				}
			}
			if (returnMap.get("productLineList" + key) == null) {
				returnMap.put("productLineList" + key, "0");
			}
		}
		footerList.add(returnMap);
		//求每条商品线的平均占比
		returnMap = new HashMap<String, Object>();
		returnMap.put("stockArea", "产品线发货占比");
		float sum = 0;
		for (HashMap<String, Object> map : listFooter) {
			sum+=StringUtil.StringToId(map.get("product_count").toString());
		}
		for (String key : productLineMap.keySet()) {
			for (HashMap<String, Object> map : listFooter) {
				if (key.equals(map.get("product_line_id").toString())) {
					if(sum!=0){
						float average = StringUtil.StringToId(map.get("product_count").toString())*100/sum;
						String averageString = StringUtil.formatDouble(average)+"";
						returnMap.put("productLineList" + key, averageString+"%");
					}else{
						returnMap.put("productLineList" + key, "0%");
					}
				}
			}
			if (returnMap.get("productLineList" + key) == null) {
				returnMap.put("productLineList" + key, "0%");
			}
		}
		footerList.add(returnMap);
		
		
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		easyuiDataGridJson.setRows(returnList);
		List<Object> list = new ArrayList<Object>();
		list.add(productLineMap);
		list.add(returnList);
		list.add(footerList);
		j.setSuccess(true);
		j.setObj(list);
		return j;
	}
	/**
	 * @return excel导出-订单商品分析模块-产品线发货列表
	 * @author zhangxiaolei
	 * @throws Exception 
	 */
	public Json exportProductLineDeliverList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2126)) {
			j.setMsg("没有权限！");
			return j;
		}
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String chinaArea = request.getParameter("chinaArea");
		String provinces = request.getParameter("provinces");
		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" a.stock_area = ").append(stockArea).append(" and");
		}
		if (!provinces.equals("")&&!provinces.equals("-1")) {
			if(!provinces.contains("-1")){
				condition.append(" a.provinces_id in (").append(provinces).append(")").append(" and");
			}
		}
		if (!chinaArea.equals("")&&!chinaArea.equals("-1")) {
			if(!chinaArea.contains("-1")){
				String ca[] = chinaArea.split(",");
				StringBuffer saSb = new StringBuffer();
				saSb.append(" a.provinces_id in (");
				for(int i = 0;i<ca.length;i++){
					if(ca[i].equals("-1")){//全国
						break;
					}
					else if(ca[i].equals("1")){//华北
						saSb.append("4,5,6,7,8");
					}
					else if(ca[i].equals("2")){//东北
						saSb.append("1,2,3");
					}
					else if(ca[i].equals("3")){//华东
						saSb.append("9,10,11,13,14");
					}
					else if(ca[i].equals("4")){//华中
						saSb.append("12,16,17,18");
					}
					else if(ca[i].equals("5")){//华南
						saSb.append("15,19,20,21");
					}
					else if(ca[i].equals("6")){//西南
						saSb.append("27,28,29,30,31");
					}
					else if(ca[i].equals("7")){//西北
						saSb.append("22,23,24,25,26");
					}
					saSb.append(",");
				}
				String str = saSb.substring(0, saSb.length()-1)+")";
				condition.append(str).append(" and");
			}
		}
		String start = "";
		String end = "";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		HashMap<String, Object> totalMap = new HashMap<String, Object>();
		if (!startYear.equals("")) {
			start = startYear + "-01-01";
			end = startYear + "-12-31";
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
			totalMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,sum(a.product_count) as product_count,a.product_line_id");
			totalMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			totalMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id order by a.stock_area,a.product_line_id");
		} else if (!startMonth.equals("")) {
			start = startMonth + "-01";
			end = startMonth + "-31";
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
			totalMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,sum(a.product_count) as product_count,a.product_line_id");
			totalMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			totalMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id order by a.stock_area,a.product_line_id");
		}else if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
			totalMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,sum(a.product_count) as product_count,a.product_line_id");
			totalMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			totalMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id order by a.stock_area,a.product_line_id");
		}else{
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
		
		List<HashMap<String, Object>> listRows = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(paramMap);
		List<HashMap<String, Object>> listFooter = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(totalMap);
		HashMap<String, String> dataMap = new HashMap<String, String>();
		List<String> stockList = new ArrayList<String>();
		List<String> stockNameList = new ArrayList<String>();
		List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> returnMap = null;
		HashMap<String, String> productLineMap = new HashMap<String, String>();
		HashMap<String, Object> plineMap = new HashMap<String, Object>();
		plineMap.put("column", " id,name");
		plineMap.put("table", " product_line");
		plineMap.put("condition", " id>0");
		List<HashMap<String, Object>> productLineList = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(plineMap);
		if (listRows.size() != 0) {
			//遍历结果
			for (HashMap<String, Object> map : listRows ) {
				String s = map.get("stock_area").toString() + "&&" + map.get("product_line_id").toString();
				dataMap.put(s, map.get("product_count").toString());
				if (stockList.contains(map.get("stock_area").toString())) {
					continue;
				}
				//地区list
				stockList.add(map.get("stock_area").toString());
				stockNameList.add(map.get("name").toString());
			}
			//遍历地区List
			for (int index = 0;index<stockList.size();index++) {
				returnMap = new HashMap<String, Object>();
				returnMap.put("stockArea", stockNameList.get(index));
				returnList.add(returnMap);
				//List<?> productLineList = service.getProductLineList("product_line.id>0");
				for(HashMap<String, Object> map : productLineList){
					productLineMap.put(map.get("id").toString(), map.get("name").toString());
					if (dataMap.get(stockList.get(index) + "&&" + map.get("id")) == null) {
						returnMap.put("productLineList" + map.get("id"), "0");
					} else {
						returnMap.put("productLineList" + map.get("id"), dataMap.get(stockList.get(index) + "&&" + map.get("id")));
					}
				}
			}
		}
		
		List<HashMap<String, Object>> footerList = new ArrayList<HashMap<String, Object>>();
		//求每条商品线的商品总数
		returnMap = new HashMap<String, Object>();
		returnMap.put("stockArea", "总量");
		for (String key : productLineMap.keySet()) {
			for (HashMap<String, Object> map : listFooter) {
				//returnMap.put("createDate", map.get("datex"));
				if (key.equals(map.get("product_line_id").toString())) {
					returnMap.put("productLineList" + key, map.get("product_count"));
				}
			}
			if (returnMap.get("productLineList" + key) == null) {
				returnMap.put("productLineList" + key, "0");
			}
		}
		footerList.add(returnMap);
		//求每条商品线的平均占比
		returnMap = new HashMap<String, Object>();
		returnMap.put("stockArea", "产品线发货占比");
		float sum = 0;
		for (HashMap<String, Object> map : listFooter) {
			sum+=StringUtil.StringToId(map.get("product_count").toString());
		}
		for (String key : productLineMap.keySet()) {
			for (HashMap<String, Object> map : listFooter) {
				if (key.equals(map.get("product_line_id").toString())) {
					if(sum!=0){
						float average = StringUtil.StringToId(map.get("product_count").toString())*100/sum;
						String averageString = StringUtil.formatDouble(average)+"";
						returnMap.put("productLineList" + key, averageString+"%");
					}else{
						returnMap.put("productLineList" + key, "0%");
					}
				}
			}
			if (returnMap.get("productLineList" + key) == null) {
				returnMap.put("productLineList" + key, "0%");
			}
		}
		footerList.add(returnMap);
		
		
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		easyuiDataGridJson.setRows(returnList);
		List<Object> list = new ArrayList<Object>();
		list.add(productLineMap);
		list.add(returnList);
		list.add(footerList);
		j.setSuccess(true);
		j.setObj(list);
		orderExportPrint(list, response);
		j.setSuccess(true);
		return j;
	}
	public void orderExportPrint (List<Object> orderList , HttpServletResponse response) throws Exception {
		
		int size = 0;
		
		String now = DateUtil.getNow().substring(0, 10);
		String fileName = now;
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		header.add("");
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		HashMap<String, String> productLineMap = (HashMap<String, String>) orderList.get(0);
		List<?> rowlist = (List<?>)orderList.get(1);
		for (String key : productLineMap.keySet()) {
			header.add(productLineMap.get(key).toString());
		}
		for(int i=0;i<rowlist.size();i++){
			HashMap<String, Object> rowMap = (HashMap<String, Object>)rowlist.get(i);
			ArrayList<String> tmp = new ArrayList<String>();
			tmp.add(rowMap.get("stockArea").toString());
			for (String key : productLineMap.keySet()) {
				tmp.add(rowMap.get("productLineList"+key).toString());
			}
			bodies.add(tmp);
		}
		rowlist = (List<?>)orderList.get(2);
		for(int i=0;i<rowlist.size();i++){
			@SuppressWarnings("unchecked")
			HashMap<String, Object> rowMap = (HashMap<String, Object>)rowlist.get(i);
			ArrayList<String> tmp = new ArrayList<String>();
			tmp.add(rowMap.get("stockArea").toString());
			for (String key : productLineMap.keySet()) {
				tmp.add(rowMap.get("productLineList"+key).toString());
			}
			bodies.add(tmp);
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
	/**
	 * @return 订单商品分析模块-所有产品线发货情况图表
	 * @author zhangxiaolei
	 */
	public Json getProductLineDeliverCharts(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2128)) {
			j.setMsg("没有权限！");
			return j;
		}
		
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		
		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" a.stock_area = ").append(stockArea);
		}
		String start = "";
		String end = "";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put("column", " sum(a.product_count) as product_count,product_line_id ");
			paramMap.put("table", " bi_product_line_deliver_info a  ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id ");
		}
		else if (!startMonth.equals("")) {
			start = startMonth + "-01";
			end = startMonth + "-31";
			paramMap.put("column", " sum(a.product_count) as product_count,product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a  ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id ");
		}
		else if (!startYear.equals("")) {
			start = startYear + "-01-01";
			end = startYear + "-12-31";
			paramMap.put("column", " sum(a.product_count) as product_count,product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.product_line_id ");
		}
		else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
		
		List<HashMap<String, Object>> listRows = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(paramMap);
		HashMap<String, String> dataMap = new HashMap<String, String>();
		HashMap<String, Object> plineMap = new HashMap<String, Object>();
		plineMap.put("column", " id,name");
		plineMap.put("table", " product_line");
		plineMap.put("condition", " id>0");
		List<HashMap<String, Object>> productLineList = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(plineMap);
		int len = productLineList.size();
		String[] types = new String[len];
		float[] orderCounts = new float[len];
		float[] percent = new float[len];
		if (listRows.size() != 0) {
			//遍历结果
			for (HashMap<String, Object> map : listRows ) {
				dataMap.put(map.get("product_line_id").toString(), map.get("product_count").toString());
			}
		}
		//遍历地区List
		int i =0;
		for(HashMap<String, Object> map : productLineList){
			types[i]=map.get("name").toString();
			if (dataMap.get(map.get("id") + "") == null) {
				orderCounts[i] = 0;
			} else {
				orderCounts[i] = StringUtil.toFloat(dataMap.get(map.get("id") + ""));
			}
			i++;
		}
		
		float sum = Arith.add(orderCounts);
        //计算平均值
		if (sum != 0) {
			for(int j1=0;j1<productLineList.size();j1++){
				percent[j1] = Arith.mul(Arith.div(orderCounts[j1], sum, 4), 100);
			}
		}
		List<Object> list = new ArrayList<Object>();
		list.add(types);//产品线
		list.add(orderCounts);//数量
		list.add(percent);//百分比
		j.setSuccess(true);
		j.setObj(list);
		return j;
	}
	/**
	 * @return 订单商品分析模块-单个产品线发货情况图表
	 * @author zhangxiaolei
	 */
	public Json getProductLineDeliverChart(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2127)) {
			j.setMsg("没有权限！");
			return j;
		}
			
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		int productLine = StringUtil.toInt(StringUtil.convertNull(request.getParameter("productLine")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
			
		StringBuffer condition = new StringBuffer();
		if (stockArea != -1) {
			condition.append(" a.stock_area = ").append(stockArea);
		}
		if (productLine != -1) {
			condition.append(" and a.product_line_id = ").append(productLine);
		}else{
			j.setMsg("请选择产品线！");
			return j;
		}
		String start = "";
		String end = "";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if (condition.length() > 0) {
			condition.append(" and ");
		}
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.create_date,a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
		}
		else if (!startMonth.equals("")) {
			start = startMonth + "-01";
			end = startMonth + "-31";
			paramMap.put("column", "date_format(a.create_date,'%Y-%m-%d')date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by a.create_date,a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
		}
		else if (!startYear.equals("")) {
			start = startYear + "-01-01";
			end = startYear + "-12-31";
			paramMap.put("column", "DATE_FORMAT(a.create_date,'%X年-%m月') date,a.stock_area,b.name,sum(a.product_count) as product_count,a.product_line_id");
			paramMap.put("table", " bi_product_line_deliver_info a left join stock_area b on a.stock_area=b.id ");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by left(a.create_date,7),a.stock_area,a.product_line_id order by a.stock_area,a.product_line_id");
		}
		else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
			
		List<HashMap<String, Object>> listRows = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(paramMap);
		List<HashMap<String, Object>> returnList = new ArrayList<HashMap<String, Object>>();
		int len =listRows.size() ;
		String[] date = new String[len];
		int[] orderCounts = new int[len];
		int i = 0;
		if (listRows.size() != 0) {
			for (HashMap<String, Object> map : listRows ) {
				date[i] = map.get("date").toString();
				orderCounts[i] = StringUtil.StringToId(map.get("product_count").toString());
				i++;
			}
		}
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		easyuiDataGridJson.setRows(returnList);
		List<Object> list = new ArrayList<Object>();
		list.add(date);//日期
		list.add(orderCounts);//数量
		j.setSuccess(true);
		j.setObj(list);
		return j;
	}
	/**
	 * @return 订单商品分析模块-产品线发货量明细
	 * @author zhangxiaolei
	 */
	public Json getProductLineDeliverDetail(HttpServletRequest request,EasyuiDataGrid easyuiDataGrid) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		int productLine = StringUtil.toInt(StringUtil.convertNull(request.getParameter("productLine")));
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String chinaArea = request.getParameter("chinaArea");
		String provinces = request.getParameter("provinces");
		HashMap footMap = new HashMap();
		StringBuffer condition = new StringBuffer();
		if (productLine == -1) {
			j.setMsg("产品线有误！");
			return j;
		}
		if (stockArea == -1) {
			j.setMsg("发货地区有误！");
			return j;
		}
		condition.append(" plc.product_line_id = ").append(productLine).append(" and");
		HashMap<String, Object> plineMap = new HashMap<String, Object>();
		plineMap.put("column", " id,name");
		plineMap.put("table", " product_line");
		plineMap.put("condition", " id="+productLine);
		List<HashMap<String, Object>> productLineList = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(plineMap);
		footMap.put("productLine", productLineList.get(0).get("name"));
		condition.append(" ap.areano = ").append(stockArea).append(" and");
		footMap.put("stockName", ProductStockBean.areaMap.get(stockArea));
		if (!provinces.equals("")&&!provinces.equals("-1")) {
			if(!provinces.contains("-1")){
				condition.append(" uoei.add_id1 in (").append(provinces).append(")").append(" and");
			}
		}
		if (!chinaArea.equals("")&&!chinaArea.equals("-1")) {
			if(!chinaArea.contains("-1")){
				String ca[] = chinaArea.split(",");
				StringBuffer saSb = new StringBuffer();
				saSb.append(" uoei.add_id1 in (");
				for(int i = 0;i<ca.length;i++){
					if(ca[i].equals("-1")){//全国
						break;
					}
					else if(ca[i].equals("1")){//华北
						saSb.append("4,5,6,7,8");
					}
					else if(ca[i].equals("2")){//东北
						saSb.append("1,2,3");
					}
					else if(ca[i].equals("3")){//华东
						saSb.append("9,10,11,13,14");
					}
					else if(ca[i].equals("4")){//华中
						saSb.append("12,16,17,18");
					}
					else if(ca[i].equals("5")){//华南
						saSb.append("15,19,20,21");
					}
					else if(ca[i].equals("6")){//西南
						saSb.append("27,28,29,30,31");
					}
					else if(ca[i].equals("7")){//西北
						saSb.append("22,23,24,25,26");
					}
					saSb.append(",");
				}
				String str = saSb.substring(0, saSb.length()-1)+")";
				condition.append(str).append(" and");
			}
		}
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("column", "p.code as pcode,p.name as pname,sum(osp.stockout_count)as count");
		paramMap.put("table",   " order_stock os LEFT JOIN user_order_extend_info uoei ON os.order_code=uoei.order_code"+
				" LEFT JOIN order_stock_product osp ON os.id=osp.order_stock_id"+
				" LEFT JOIN product p ON osp.product_id=p.id"+
				" LEFT JOIN product_line_catalog plc ON (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id)"+
				" LEFT JOIN audit_package ap ON os.order_code=ap.order_code ");
		
		if (!startTime.equals("") && !endTime.equals("")) {
			condition.append(" ap.check_datetime>='"+startTime+" 00:00:00' and ap.check_datetime<='"+endTime+" 23:59:59' and  os.status<>3  ");
			paramMap.put("condition" , condition+ " GROUP BY p.id limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
			footMap.put("time", startTime+"至"+endTime);
		}
		else if (!startMonth.equals("")) {
			condition.append(" ap.check_datetime>='"+startMonth+"-01 00:00:00' and ap.check_datetime<='"+startMonth+"-31 23:59:59' and os.status<>3  ");
			paramMap.put("condition" , condition+"  GROUP BY p.id limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
			footMap.put("time", startMonth);
		}
		else if (!startYear.equals("")) {
			condition.append(" ap.check_datetime>='"+startYear+"-01-01 00:00:00' and ap.check_datetime<='"+startYear+"-12-31 23:59:59' and os.status<>3 ");
			paramMap.put("condition" , condition+" GROUP BY p.id limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
			footMap.put("time", startYear);
		}
		else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
		List<HashMap<String, Object>> listRows = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(paramMap);
		paramMap.put("column", " count(distinct p.id) thecount");
		paramMap.put("condition", condition.toString());
		List<HashMap<String, Object>> countRow = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(paramMap);
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) StringUtil.toInt(countRow.get(0).get("thecount") + ""));
		List footList = new ArrayList();
		footList.add(footMap);
		easyuiDataGridJson.setFooter(footList);
		easyuiDataGridJson.setRows(listRows);
		j.setSuccess(true);
		j.setObj(easyuiDataGridJson);
		return j;
	}
	/**
	 * @return 导出excel订单商品分析模块-产品线发货量明细
	 * @author zhangxiaolei
	 */
	public Json exportProductLineDeliverDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		int productLine = StringUtil.toInt(StringUtil.convertNull(request.getParameter("productLine")));
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		String startYear = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startYear")));
		String startMonth = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startMonth")));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String chinaArea = request.getParameter("chinaArea");
		String provinces = request.getParameter("provinces");
		HashMap footMap = new HashMap();
		StringBuffer condition = new StringBuffer();
		if (productLine == -1) {
			j.setMsg("产品线有误！");
			return j;
		}
		if (stockArea == -1) {
			j.setMsg("发货地区有误！");
			return j;
		}
		condition.append(" plc.product_line_id = ").append(productLine).append(" and");
		HashMap<String, Object> plineMap = new HashMap<String, Object>();
		plineMap.put("column", " id,name");
		plineMap.put("table", " product_line");
		plineMap.put("condition", " id="+productLine);
		List<HashMap<String, Object>> productLineList = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(plineMap);
		footMap.put("productLine", productLineList.get(0).get("name"));
		condition.append(" ap.areano = ").append(stockArea).append(" and");
		footMap.put("stockName", ProductStockBean.areaMap.get(stockArea));
		if (!provinces.equals("")&&!provinces.equals("-1")) {
			if(!provinces.contains("-1")){
				condition.append(" uoei.add_id1 in (").append(provinces).append(")").append(" and");
			}
		}
		if (!chinaArea.equals("")&&!chinaArea.equals("-1")) {
			if(!chinaArea.contains("-1")){
				String ca[] = chinaArea.split(",");
				StringBuffer saSb = new StringBuffer();
				saSb.append(" uoei.add_id1 in (");
				for(int i = 0;i<ca.length;i++){
					if(ca[i].equals("-1")){//全国
						break;
					}
					else if(ca[i].equals("1")){//华北
						saSb.append("4,5,6,7,8");
					}
					else if(ca[i].equals("2")){//东北
						saSb.append("1,2,3");
					}
					else if(ca[i].equals("3")){//华东
						saSb.append("9,10,11,13,14");
					}
					else if(ca[i].equals("4")){//华中
						saSb.append("12,16,17,18");
					}
					else if(ca[i].equals("5")){//华南
						saSb.append("15,19,20,21");
					}
					else if(ca[i].equals("6")){//西南
						saSb.append("27,28,29,30,31");
					}
					else if(ca[i].equals("7")){//西北
						saSb.append("22,23,24,25,26");
					}
					saSb.append(",");
				}
				String str = saSb.substring(0, saSb.length()-1)+")";
				condition.append(str).append(" and");
			}
		}
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("column", "p.code as pcode,p.name as pname,sum(osp.stockout_count)as count");
		paramMap.put("table",   " order_stock os LEFT JOIN user_order_extend_info uoei ON os.order_code=uoei.order_code"+
				" LEFT JOIN order_stock_product osp ON os.id=osp.order_stock_id"+
				" LEFT JOIN product p ON osp.product_id=p.id"+
				" LEFT JOIN product_line_catalog plc ON (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id)"+
				" LEFT JOIN audit_package ap ON os.order_code=ap.order_code ");
		
		 if (!startTime.equals("") && !endTime.equals("")) {
			paramMap.put("condition" , condition+" ap.check_datetime>='"+startTime+" 00:00:00' and ap.check_datetime<='"+endTime+" 23:59:59' and  os.status<>3 GROUP BY p.id ");
			footMap.put("time", startTime+"至"+endTime);
		 }else if (!startMonth.equals("")) {
			 paramMap.put("condition" , condition+" ap.check_datetime>='"+startMonth+"-01 00:00:00' and ap.check_datetime<='"+startMonth+"-31 23:59:59' and os.status<>3 GROUP BY p.id ");
			 footMap.put("time", startMonth);
		 }else if (!startYear.equals("")) {
			paramMap.put("condition" , condition+" ap.check_datetime>='"+startYear+"-01-01 00:00:00' and ap.check_datetime<='"+startYear+"-12-31 23:59:59' and os.status<>3 GROUP BY p.id ");
			footMap.put("time", startYear);
		}else {
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
		List<HashMap<String, Object>> listRows = biProductLineDeliverInfoMapper.getProductLineDeliverInfo(paramMap);
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		easyuiDataGridJson.setRows(listRows);
		j.setSuccess(true);
		j.setObj(easyuiDataGridJson);
		exportOrderDetailExcel(listRows, response);
		j.setSuccess(true);
		return j;
	}
	public void exportOrderDetailExcel (List<HashMap<String, Object>> orderList , HttpServletResponse response) throws Exception {
		
		HashMap<String, Object> map = null;
		int size = 0;
		
		String now = DateUtil.getNow().substring(0, 10);
		String fileName = now;
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		
		header.add("序号");
		header.add("产品编号");
		header.add("产品名称");
		header.add("发货量");
		
		size = header.size();
		
		if (orderList != null && orderList.size() > 0) {
			int x = orderList.size();
			for (int i = 0; i < x; i++) {
				map =  orderList.get(i);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(i+1 + "");
				tmp.add(map.get("pcode").toString());
				tmp.add(map.get("pname").toString());
				tmp.add(map.get("count").toString());
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
