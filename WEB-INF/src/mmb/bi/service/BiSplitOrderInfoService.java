package mmb.bi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bi.dao.BiSplitOrderInfoDao;
import mmb.common.dao.CommonDao;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.util.combobox.ComboboxController;
import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.timedtask.BiSplitOrderInfoJob;

@Service
public class BiSplitOrderInfoService {
	@Autowired
	public BiSplitOrderInfoDao biSplitOrderInfoMapper;
	@Autowired
	public CommonDao commonMapper;
	public Json getBiSplitOrderList(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}

		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2129)) {
			j.setMsg("没有权限！");
			return j;
		}
		
		String dataType = StringUtil.convertNull(request.getParameter("dataType"));
		String startYear =StringUtil.convertNull(request.getParameter("startYear"));
		String startMonth = StringUtil.convertNull(request.getParameter("startMonth"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		
		StringBuffer condition = new StringBuffer();
		StringBuffer column = new StringBuffer();
		condition.append(" group by left(a.create_date,10)");
		String start = "";
		String end = "";
		HashMap<Integer, String> areaMap = ProductStockBean.stockoutAvailableAreaMap;
		for (int key : areaMap.keySet()) {
			if("cd".equals(dataType)){
				column.append("sum(a.split_order_count) totalcount,sum(CASE a.stock_area WHEN "+key+" THEN a.split_order_count ELSE 0 END) as count"+key+",");
			}else{
				column.append("sum(a.span_order_count) totalcount,sum(CASE a.stock_area WHEN "+key+" THEN a.span_order_count ELSE 0 END) as count"+key+",");
			}
		}
		String columns = column.toString().substring(0, column.length()-1);
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if (!startTime.equals("") && !endTime.equals("")) {
			start = startTime;
			end = endTime;
			paramMap.put("column", " a.create_date as date,"+columns);
			paramMap.put("table",  " bi_split_order_info a LEFT JOIN stock_area b on a.stock_area = b.id");
			paramMap.put("condition" ," a.create_date >= '" + start + "' and a.create_date <= '" + end + "'"+condition);
		}
		else if (!startMonth.equals("")) {
			start = startMonth + "-01";
			end = startMonth + "-31";
			paramMap.put("column", " a.create_date as date,"+columns);
			paramMap.put("table",  " bi_split_order_info a LEFT JOIN stock_area b on a.stock_area = b.id");
			paramMap.put("condition" ," a.create_date >= '" + start + "' and a.create_date <= '" + end + "'"+condition);
		}
		else if (!startYear.equals("")) {
			start = startYear + "-01-01";
			end = startYear + "-12-31";
			paramMap.put("column", " a.create_date as date,"+columns);
			paramMap.put("table",  " bi_split_order_info a LEFT JOIN stock_area b on a.stock_area = b.id");
			paramMap.put("condition" ," a.create_date >= '" + start + "' and a.create_date <= '" + end + "'"+condition);
		}
		else{
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
		
		List<HashMap<String, Object>> listRows = biSplitOrderInfoMapper.getSplitOrderList(paramMap);
		List<Object> list = new ArrayList<Object>();
		list.add(areaMap);
		list.add(listRows);
		j.setSuccess(true);
		j.setObj(list);
		return j;
	}
	/**
	 * @return 订单商品分析模块-订单越仓发货图表
	 * @author zhangxiaolei
	 */
	public Json getBiSplitOrderCharts(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2130)) {
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
			paramMap.put("column", " sum(a.base_order_count) count,date_format(a.create_date,'%Y-%m-%d')as date,sum(a.split_order_count) as splitCount,sum(a.span_order_count) spanCount,sum(a.sub_order_count) subCount,b.name");
			paramMap.put("table",  " bi_split_order_info a LEFT JOIN stock_area b on a.stock_area = b.id");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by date");
		}
		else if (!startMonth.equals("")) {
			start = startMonth + "-01";
			end = startMonth + "-31";
			paramMap.put("column", " sum(a.base_order_count) count,date_format(a.create_date,'%Y-%m-%d')as date,sum(a.split_order_count) as splitCount,sum(a.span_order_count) spanCount,sum(a.sub_order_count) subCount,b.name");
			paramMap.put("table",  " bi_split_order_info a LEFT JOIN stock_area b on a.stock_area = b.id");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by date");
		}
		else if (!startYear.equals("")) {
			start = startYear + "-01-01";
			end = startYear + "-12-31";
			paramMap.put("column", " sum(a.base_order_count) count,DATE_FORMAT(a.create_date,'%X年-%m月')as date,sum(a.split_order_count) as splitCount,sum(a.span_order_count) spanCount,sum(a.sub_order_count) subCount,b.name");
			paramMap.put("table",  " bi_split_order_info a LEFT JOIN stock_area b on a.stock_area = b.id");
			paramMap.put("condition" , condition+ " a.create_date >= '" + start + "' and a.create_date <= '" + end + "' group by date");
		} 
		else{
			j.setMsg("请输入时间作为条件查询！");
			return j;
		}
		List<HashMap<String, Object>> listRows = biSplitOrderInfoMapper.getSplitOrderList(paramMap);
		String[] date = new String[listRows.size()];
		float[] spanCount = new float[listRows.size()];
		float[] splitCount = new float[listRows.size()];
		float[] count = new float[listRows.size()];
		float[] subCount = new float[listRows.size()];
		float[] spanPercent = new float[listRows.size()];
		float[] splitPercent = new float[listRows.size()];
		int index = 0;
		if (listRows!=null && listRows.size() != 0) {
			for (HashMap<String, Object> map : listRows ) {
				if(map!=null){
					date[index] = map.get("date").toString();
					spanCount[index] = StringUtil.toFloat(map.get("spanCount").toString());
					splitCount[index] = StringUtil.toFloat(map.get("splitCount").toString());
					count[index] = StringUtil.toFloat(map.get("count").toString());
					subCount[index] = StringUtil.toFloat(map.get("subCount").toString());
					index++;
				}
			}
		}
		float spanSum= Arith.add(spanCount);
		float splitSum= Arith.add(splitCount);
        //计算平均值
		if (spanSum != 0) {
			for(int i=0;i<listRows.size();i++){
				spanPercent[i] = Arith.round(Arith.div(spanCount[i],subCount[i])*100, 2) ;
			}
		}
        //计算平均值
		if (splitSum != 0) {
			for(int i=0;i<listRows.size();i++){
				splitPercent[i] =  Arith.round(Arith.div(splitCount[i],count[i])*100,2);
			}
		}
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		easyuiDataGridJson.setTotal((long) listRows.size());
		//easyuiDataGridJson.setRows(listRows);
		List<Object> list = new ArrayList<Object>();
		list.add(date);//日期
		list.add(spanCount);//数量
		list.add(spanPercent);//数量
		list.add(splitCount);//数量
		list.add(splitPercent);//数量
		j.setSuccess(true);
		j.setObj(list);
		return j;
	}
	/**
	 * @return 拆单、越仓发货订单明细
	 * @author zhangxiaolei
	 */
	public Json getSpiltOrderInfoDetail(HttpServletRequest request,EasyuiDataGrid easyuiDataGrid) {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		String date = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("date")));
		String type = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("type")));
		StringBuffer condition = new StringBuffer();
		HashMap<String, Object> footMap = new HashMap<String, Object>();
		DbOperation dbop = new DbOperation(DbOperation.DB_SLAVE);
		WareService service = new WareService(dbop);
		try {
			if (!date.equals("")) {
				condition.append("ap.check_datetime<='" + date + " 23:59:59'").append(" and ").append("ap.check_datetime>='" + date + " 00:00:00'").append(" and ");
				;
				footMap.put("date", date);
			}
			if (!type.equals("1") && !type.equals("2")) {
				j.setMsg("类型错误！");
				return j;
			}
			if (type.equals("2")) {
				condition.append(" SUBSTR(adp.priority,1,1)!=ap.areano and ");
			}
			if (stockArea != -1) {
				if (type.equals("1")) {// 拆单
					condition.append(" SUBSTR(adp.priority,1,1)= ").append(stockArea).append(" and ");
				} else if (type.equals("2")) {
					condition.append(" SUBSTR(adp.priority,1,1) = ").append(stockArea).append(" and ");
				}
				footMap.put("stockName", ProductStockBean.areaMap.get(stockArea));
			}
			condition.append(" os.status <> 3 and ");
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("column", "osp.stockout_count pcount,"
					+"ap.order_id orderId,     "
					+"sa. NAME stockname,      "
					+"ap.order_code ordercode, "
					+"p. CODE productcode,     "
					+"p. NAME productname,     "
					+"pl. NAME plname");       
			if (type.equals("1")) {
				paramMap.put("table", " audit_package ap "
										+"JOIN user_order_sub_list uosl ON ap.order_id = uosl.child_id  join user_order uo on uosl.parent_id=uo.id join user_order_extend_info uoei on uo.code = uoei.order_code                               "
										+"join provinces po on po.id=uoei.add_id1 join area_delivery_priority adp on po.name like concat(adp.province,'%') JOIN stock_area sa ON sa.id = ap.areano                                                       "
										+"JOIN order_stock os ON os.order_id = ap.order_id                                                   "
										+"JOIN order_stock_product osp ON os.id = osp.order_stock_id                                   "
										+"JOIN product p on osp.product_id = p.id                                                        "
										+"JOIN product_line_catalog plc ON (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id)"
										+"JOIN product_line pl on plc.product_line_id = pl.id ");     
			} else if (type.equals("2")) {
				paramMap.put("table", " audit_package ap "
										+"JOIN stock_area sa ON sa.id = ap.areano                                                      "
										+"JOIN order_stock os ON os.order_id = ap.order_id                                                   "
										+"JOIN order_stock_product osp ON os.id = osp.order_stock_id                                   "
										+"JOIN product p on osp.product_id = p.id                                                      "
										+"JOIN product_line_catalog plc ON (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id)"
										+"JOIN product_line pl on plc.product_line_id = pl.id                                          "
										+"join user_order_extend_info uoei on uoei.order_code=ap.order_code                            "
										+"join provinces po on po.id=uoei.add_id1                                                       "
										+"join area_delivery_priority adp on po.name like concat(adp.province,'%') ");     
			}
			paramMap.put("condition", condition + " ap.id>0"+ " limit "
					+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
					+ "," + easyuiDataGrid.getRows());
			List<HashMap<String, Object>> listRows = biSplitOrderInfoMapper.getSplitOrderList(paramMap);
			paramMap.put("condition", condition + " ap.id>0");
			List<HashMap<String, Object>> totalRows = biSplitOrderInfoMapper.getSplitOrderList(paramMap);
			
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long) totalRows.size());
			easyuiDataGridJson.setRows(listRows);
			List<HashMap<String, Object>> footList = new ArrayList<HashMap<String, Object>>();
			footList.add(footMap);
			easyuiDataGridJson.setFooter(footList);
			j.setSuccess(true);
			j.setObj(easyuiDataGridJson);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return j;
	}
	/**
	 * @return excel导出拆单、越仓发货订单明细
	 * @author zhangxiaolei
	 */
	public Json excelSpiltOrderInfoDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		int stockArea = StringUtil.toInt(StringUtil.convertNull(request.getParameter("stockArea")));
		String date = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("date")));
		String type = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("type")));
		StringBuffer condition = new StringBuffer();
		HashMap<String, Object> footMap = new HashMap<String, Object>();
		WareService service = new WareService();
		try {
			if (!date.equals("")) {
				condition.append("ap.check_datetime<='" + date + " 23:59:59'").append(" and ").append("ap.check_datetime>='" + date + " 00:00:00'").append(" and ");
				;
				footMap.put("date", date);
			}
			if (!type.equals("1") && !type.equals("2")) {
				j.setMsg("类型错误！");
				return j;
			}
			if (stockArea != -1) {
				if (type.equals("1")) {// 拆单
					condition.append(" SUBSTR(adp.priority,1,1)= ").append(stockArea).append(" and ");
				} else if (type.equals("2")) {
					condition.append(" SUBSTR(adp.priority,1,1)= ").append(stockArea).append(" and ");
					condition.append(" ap.areano != ").append(stockArea).append(" and ");
				}
				footMap.put("stockName", ProductStockBean.areaMap.get(stockArea));
			}
			condition.append(" os.status <> 3 and ");
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("column", "osp.stockout_count pcount,"
					+"ap.order_id orderId,     "
					+"sa. NAME stockname,      "
					+"ap.order_code ordercode, "
					+"p. CODE productcode,     "
					+"p. NAME productname,     "
					+"pl. NAME plname");       
			if (type.equals("1")) {
				paramMap.put("table", " audit_package ap "
										+"JOIN user_order_sub_list uosl ON ap.order_id = uosl.child_id  join user_order uo on uosl.parent_id=uo.id join user_order_extend_info uoei on uo.code = uoei.order_code                               "
										+"join provinces po on po.id=uoei.add_id1 join area_delivery_priority adp on po.name like concat(adp.province,'%') JOIN stock_area sa ON sa.id = ap.areano                                                       "
										+"JOIN order_stock os ON os.order_id = ap.order_id                                                   "
										+"JOIN order_stock_product osp ON os.id = osp.order_stock_id                                   "
										+"JOIN product p on osp.product_id = p.id                                                        "
										+"JOIN product_line_catalog plc ON (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id)"
										+"JOIN product_line pl on plc.product_line_id = pl.id ");     
			} else if (type.equals("2")) {
				paramMap.put("table", " audit_package ap "
										+"JOIN stock_area sa ON sa.id = ap.areano                                                      "
										+"JOIN order_stock os ON os.order_id = ap.order_id                                                   "
										+"JOIN order_stock_product osp ON os.id = osp.order_stock_id                                   "
										+"JOIN product p on osp.product_id = p.id                                                      "
										+"JOIN product_line_catalog plc ON (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id)"
										+"JOIN product_line pl on plc.product_line_id = pl.id                                          "
										+"join user_order_extend_info uoei on uoei.order_code=ap.order_code                            "
										+"join provinces po on po.id=uoei.add_id1                                                       "
										+"join area_delivery_priority adp on po.name like concat(adp.province,'%') ");     
			}
			paramMap.put("condition", condition + " ap.id>0");
			List<HashMap<String, Object>> listRows = biSplitOrderInfoMapper.getSplitOrderList(paramMap);
			exportExcel(listRows, response);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return j;
	}
	public void exportExcel(List<HashMap<String, Object>> orderList,
			HttpServletResponse response) throws Exception {

		HashMap<String, Object> map = null;
		int size = 0;

		String now = DateUtil.getNow().substring(0, 10);
		String fileName = now;
		// 设置表头
		ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();

		header.add("订单号");
		header.add("发货仓量");
		header.add("产品编号");
		header.add("产品名称");
		header.add("产品数量");
		header.add("产品线");

		size = header.size();
		if (orderList != null && orderList.size() > 0) {
			int x = orderList.size();
			for (int i = 0; i < x; i++) {
				map = orderList.get(i);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(map.get("ordercode").toString());
				tmp.add(map.get("stockname").toString());
				tmp.add(map.get("productcode").toString());
				tmp.add(map.get("productname").toString());
				tmp.add(map.get("pcount").toString());
				tmp.add(map.get("plname").toString());
				bodies.add(tmp);
			}
		}
		headers.add(header);

		/* 允许合并列,下标从0开始，即0代表第一列 */
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);

		/* 允许合并行,下标从0开始，即0代表第一行 */
		List<Integer> mayMergeRow = new ArrayList<Integer>();
		excel.setMayMergeRow(mayMergeRow);

		/*
		 * 该行为固定写法 （设置该值为导出excel最大列宽 ,下标从1开始）
		 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
		 */
		excel.setColMergeCount(size);

		/*
		 * 设置需要自己设置样式的行，以每个bodies为参照 具体的样式设置参考 DemoExcel.java中的setStyle方法
		 * 具体可以参照执行后导出的excel样式及DemoExcel中的setStyle方法
		 */
		List<Integer> row = new ArrayList<Integer>();

		/* 设置需要自己设置样式的列，以每个bodies为参照 */
		List<Integer> col = new ArrayList<Integer>();

		excel.setRow(row);
		excel.setCol(col);

		// 调用填充表头方法
		excel.buildListHeader(headers);

		// 调用填充数据区方法
		excel.buildListBody(bodies);
		// 文件输出
		excel.exportToExcel(fileName, response, "");
	}
}
