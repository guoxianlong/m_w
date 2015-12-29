package mmb.stock.cargo;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.CommonDao;
import mmb.delivery.domain.PopBussiness;
import mmb.delivery.service.DeliveryService;
import mmb.hessian.ware.DeliverOrderInfoBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.tms.model.Provinces;
import mmb.util.ExcelUtil;
import mmb.util.excel.AbstractExcel;
import mmb.util.excel.ExportExcel;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


@RequestMapping("EffectDeliverController")
@Controller
public class EffectDeliverController {

	private static Logger logger = Logger.getLogger(EffectDeliverController.class);
	@Autowired
	public CommonDao commonMapper;
	@Autowired
	public DeliveryService deliveryService;
	/**
	 * 导入订单配送信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/upload")
	public String upload(HttpServletRequest request, HttpServletResponse response)throws Exception{

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "");
			request.setAttribute("msg", "您还没有登录");
			return "admin/orderStock/importEffectTime";
		}
		
		if(user.getGroup() == null || !user.getGroup().isFlag(3063)){
			request.setAttribute("result", "");
			request.setAttribute("msg", "您没有操作权限");
			return "admin/orderStock/importEffectTime";
		}
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		int popId = StringUtil.toInt(request.getParameter("popId"));
		
		if(popId == PopBussiness.POP_JD){
			areaId = ProductStockBean.AREA_JD;//如果是POP商家,areaId设置为5
		}
		if (areaId < 0) {
			request.setAttribute("result", "");
			request.setAttribute("msg", "选择仓错误");
			return "admin/orderStock/importEffectTime";
		}
		 // 转型为MultipartHttpRequest：   
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        // 获得文件：   
        MultipartFile file = multipartRequest.getFile("attendance");
        // 获得输入流：   
        InputStream input = file.getInputStream();   
        // 获得文件名：   
        String attendanceFileName = file.getOriginalFilename();
		int index = attendanceFileName.lastIndexOf(".");
		String suffix = attendanceFileName.substring(index + 1, attendanceFileName.length());

		StringBuilder msg = new StringBuilder();
		
		List<String[]> rows = ExcelUtil.rosolveFile(input, suffix, 1);
        
		String result = null;
		
		if (rows != null  && rows.size() > 0) {
			int province = StringUtil.toInt(rows.get(0)[0].trim());
			if(popId == PopBussiness.POP_MMB){
				if(province>Provinces.PROVINCE_FLAG){
					request.setAttribute("result", "您导入的信息不是买卖宝的，请重新导入");
					return "admin/orderStock/importEffectTime";
				}
			}else if(popId == PopBussiness.POP_JD){
				if(province<Provinces.PROVINCE_FLAG){
					request.setAttribute("result", "您导入的信息不是京东的，请重新导入");
					return "admin/orderStock/importEffectTime";
				}
			}else{
				request.setAttribute("result", "请选择POP商家后重新导入");
				return "admin/orderStock/importEffectTime";
			}
			int total = 0;
			int success = 0;
			boolean empty = true;
			DbOperation db = new DbOperation(DbOperation.DB);
			try {
				for (int i = 0; i < rows.size(); i++) {			
					String[] row = rows.get(i);
					if (row == null || row.length == 0) {
						continue;
					}
					empty = true;
					for (int j = 0; j < row.length; j++) {
						if(!"".equals(row[j]))
							empty = false;
					}
					// 全部列为空不处理
					if(empty){
						continue;
					}
					
					try {			
						total++;	
						if(row.length < 9){
							msg.append("第").append(i + 1).append("行, 数据列数不合法（至少9列）<br/>");
							continue;
						}
						int provinceId = StringUtil.toInt(row[0].trim());
						if (provinceId <= 0) {
							msg.append("第").append(i + 1).append("行, 省填写错误<br/>");
							continue;
						}
						int cityId = StringUtil.toInt(row[2].trim());
						if (cityId <= 0) {
							msg.append("第").append(i + 1).append("行, 市填写错误<br/>");
							continue;
						}
						int cityAreaId = StringUtil.toInt(row[4].trim());
						if (cityAreaId <= 0) {
							msg.append("第").append(i + 1).append("行, 区填写错误<br/>");
							continue;
						}
						int cityAreaTime = StringUtil.toInt(row[6].trim());
						if (cityAreaTime <= 0) {
							msg.append("第").append(i + 1).append("行, 区时效填写错误<br/>");
							continue;
						}
						int townTime = StringUtil.toInt(row[7].trim());
						if (townTime <= 0) {
							msg.append("第").append(i + 1).append("行, 乡镇时效填写错误<br/>");
							continue;
						}
						int villageTime = StringUtil.toInt(row[8].trim());
						if (villageTime <= 0) {
							msg.append("第").append(i + 1).append("行, 村时效填写错误<br/>");
							continue;
						}
						ResultSet rs = db.executeQuery("select id from effect_deliver_time where province_id=" + provinceId + " and province_city_id=" + cityId + " and city_area_id=" + cityAreaId + " and pop_id=" + popId + " and area_id=" + areaId);
						int id = 0; 
						if (rs.next()) {
							id = rs.getInt(1);
						}
						rs.close();
						if (id > 0) {
							if (!db.executeUpdate("update effect_deliver_time set city_area_time=" + cityAreaTime + ",town_time=" + townTime + ",village_time="+villageTime+",pop_id=" + popId + " where id=" + id)) {
								msg.append("第").append(i + 1).append("行, ").append("更新失败").append("<br/>");
							} else {
								success ++;
							}
						} else {
							
							if (!db.executeUpdate("insert into effect_deliver_time(province_id,province_city_id,city_area_id,area_id,city_area_time,town_time,village_time,pop_id) values (" +provinceId + "," +cityId + "," + cityAreaId+ "," +areaId + "," + cityAreaTime+ "," +townTime + "," +villageTime +","+popId+ ")")) {
								msg.append("第").append(i + 1).append("行, ").append("添加失败").append("<br/>");
							} else {
								success ++;
							}
						}
					} catch (NullPointerException e) {
						msg.append("第").append(i + 1).append("行, 数据为空<br/>");
						continue;
					} catch (Exception e) {
						msg.append("第").append(i + 1).append("行, ").append(e.getMessage()).append("<br/>");
						continue;
					}
									
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				db.release();
			}	
			result = "共" + total + "行,成功" + success + "行,失败" + (total - success) + "行.";
		} else {
			result = "Excel内容为空";
			msg.append("Excel内容为空");
		}
		
		request.setAttribute("result", result);
		request.setAttribute("msg", msg.toString());
		return "admin/orderStock/importEffectTime";
	}

	
	@RequestMapping("/download")
	public void download(HttpServletRequest request, HttpServletResponse response) {
		DbOperation slave2Dbop = new DbOperation(DbOperation.DB_SLAVE2);
		int popId = StringUtil.toInt(request.getParameter("popId"));
		String condition = "<"+Provinces.PROVINCE_FLAG;
		if(popId == PopBussiness.POP_JD){
			condition = ">"+Provinces.PROVINCE_FLAG;
		}
		try {
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			ResultSet rs = slave2Dbop.executeQuery("select p.id,p.`name`,pc.id,pc.city,ca.id,ca.area from city_area ca join province_city pc on ca.city_id=pc.id join provinces p on pc.province_id=p.id "
					+ " where p.id "+condition+" order by p.id,pc.id,ca.id");
			while(rs.next()) {
				ArrayList<String> body = new ArrayList<String>();
				body.add(rs.getInt(1) + "");
				body.add(rs.getString(2) + "");
				body.add(rs.getInt(3) + "");
				body.add(rs.getString(4) + "");
				body.add(rs.getInt(5) + "");
				body.add(rs.getString(6) + "");
				bodies.add(body);
			}
			rs.close();
			
			ExportExcel excel = new ExportExcel(AbstractExcel.HSSF);
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header = new ArrayList<String>();
			header.add("省id");
			header.add("省");
			header.add("市id");
			header.add("市");
			header.add("区id");
			header.add("区");
			header.add("区时效（小时）");
			header.add("乡镇时效（小时）");
			header.add("村时效（小时）");
			 
			headers.add(header);
			
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			excel.setMayMergeColumn(mayMergeColumn);
			
			List<Integer> mayMergeRow = new ArrayList<Integer>();
			excel.setMayMergeRow(mayMergeRow);
			
			excel.setColMergeCount(headers.get(0).size());			
			List<Integer> row  = new ArrayList<Integer>();
			List<Integer> col  = new ArrayList<Integer>();			
			excel.setRow(row);
			excel.setCol(col);			
			excel.buildListHeader(headers);
			
			excel.buildListBody(bodies);
			excel.getSheet().setColumnWidth(0, 0);
			excel.getSheet().setColumnWidth(2, 0);
			excel.getSheet().setColumnWidth(4, 0);
			String fileName = DateUtil.getNow().substring(0, 10);
			excel.exportToExcel(fileName, response, "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			slave2Dbop.release();
		}
	}
	/**
	 * 时效列表查询
	 */
	@RequestMapping("/getEffectDeliverTimeList")
	@ResponseBody
	public EasyuiDataGridJson getEffectDeliverTimeList(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid page) throws ServletException, IOException {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("msg", "您还没有登录");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			if(user.getGroup() == null || !user.getGroup().isFlag(3064)){
				request.setAttribute("msg", "您没有操作权限");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			int popId = StringUtil.toInt(request.getParameter("popId"));
			String stockArea = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("stockArea")));
			String sheng = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("sheng")));
			String shi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("shi")));
			String qu = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("qu")));
			StringBuffer condition = new StringBuffer();
			condition.append("1=1");
			if (popId != -1) {
				condition.append(" and a.pop_id=" + popId);
			}
			if (!"".equals(stockArea)) {
				if(popId==PopBussiness.POP_JD){
					condition.append(" and e.id="+ProductStockBean.AREA_JD);
				}else{
					condition.append(" and e.id=" + stockArea);
				}
			}
			if (!"".equals(sheng)) {
				condition.append(" and b.id=" + sheng);
			}
			if (!"".equals(shi)) {
				condition.append(" and c.id=" + shi);
			}
			if (!"".equals(qu)) {
				condition.append(" and d.id=" + qu);
			}
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "a.*,b.name sheng,c.city shi,d.area qu,e.name stockName");
			paramMap.put("table", " effect_deliver_time a "
					+ " LEFT JOIN provinces b ON a.province_id=b.id "
					+ " LEFT JOIN province_city c ON a.province_city_id=c.id "
					+ " LEFT JOIN city_area d ON a.city_area_id=d.id "
					+ " LEFT JOIN stock_area e ON a.area_id=e.id ");
			paramMap.put("condition", condition.toString()+" limit "+(page.getPage()-1) * page.getRows()+","+page.getRows());
			List<HashMap<String, String>> listRows = commonMapper.getCommonInfo(paramMap);
			HashMap<String, String> paramMapCount = new HashMap<String, String>();
			paramMapCount.put("column", " count(a.id) thecount ");
			paramMapCount.put("table", " effect_deliver_time a "
					+ " LEFT JOIN provinces b ON a.province_id=b.id "
					+ " LEFT JOIN province_city c ON a.province_city_id=c.id "
					+ " LEFT JOIN city_area d ON a.city_area_id=d.id "
					+ " LEFT JOIN stock_area e ON a.area_id=e.id ");
			paramMapCount.put("condition", condition.toString());
			List<HashMap<String, Object>> count = commonMapper.getCommonInfoCount(paramMapCount);
			int total = 0;
			if (count != null && count.size() > 0 && count.get(0) != null) {
				total = StringUtil.toInt(count.get(0).get("thecount") + "");
			}
			for (HashMap<String, String> hashMap : listRows) {
				Object t = hashMap.get("pop_id");
				int popID = StringUtil.toInt(t+"");
				hashMap.put("popName",PopBussiness.popMap.get(popID));
			}
			datagrid.setRows(listRows);
			datagrid.setTotal((long)total);// 设置总记录数
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return datagrid;
	}

	/**
	 * 配送时效订单信息
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/getEffectOrderInfoList")
	@ResponseBody
	public EasyuiDataGridJson getEffectOrderInfoList(EasyuiDataGrid page,HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		
		try {
			int popId = StringUtil.toInt(request.getParameter("popId"));
			String stockArea = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("stockArea")));
			String deliver = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("deliver")));
			String sheng = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("sheng")));
			String shi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("shi")));
			String qu = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("qu")));
			String date = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("date")));
			String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
			String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
			String shengName = StringUtil.convertNull(request.getParameter("shengName"));
			String shiName = StringUtil.convertNull(request.getParameter("shiName"));
			String quName = StringUtil.convertNull(request.getParameter("quName"));
			
			StringBuffer popParamSql = new StringBuffer();

			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("msg", "您还没有登录");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			if(user.getGroup() == null || !user.getGroup().isFlag(3065)){
				request.setAttribute("msg", "您没有操作权限");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			if (startTime.equals("") || endTime.equals("")) {
				request.setAttribute("msg", "请选择出库时间范围！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 15) {
				request.setAttribute("msg", "最多查询15天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			StringBuffer condition = new StringBuffer();
			condition.append("1=1");
			//配送状态字段剔除以下三个配送状态。已签收、未妥投已退回、未妥投开始退回    modify by 刘仁华 2015-3-4
			//condition.append(" (f.deliver_state is null or f.deliver_state not in (6, 7, 8)) ");
			if (!"".equals(startTime) && !"".equals(endTime)) {
				condition.append(" and a.receive_datetime between '"+ startTime + " 00:00:00' and '" + endTime + " 23:59:59'");
				popParamSql.append(" and dip3.time between '").append(startTime).append(" 00:00:00'")
			   		 	.append(" and '").append(endTime).append(" 23:59:59'").append(" and dip3.deliver_state=1 ");
			}
			if (popId != -1) {
				popParamSql.append(" and dip3.pop_type=").append(popId);
			}
			if (!"".equals(stockArea)) {
				condition.append(" and c.id=" + stockArea);
				popParamSql.append(" and dip3.storage_id=").append(stockArea);
			}
			if (!"".equals(sheng)) {
				condition.append(" and g.add_id1=" + sheng);
			}
			if (!"".equals(shi)) {
				condition.append(" and g.add_id2=" + shi);
			}
			if (!"".equals(qu)) {
				condition.append(" and g.add_id3=" + qu);
			}
			if(!shengName.equals("")&&!shengName.equals("全部省")){
				popParamSql.append(" and dip3.province like '%").append(shengName.trim()).append("%'");
			}
			if(!shiName.equals("")&&!shiName.equals("全部市")){
				popParamSql.append(" and dip3.city like '%").append(shiName.trim()).append("%'");
			}
			if(!quName.equals("")&&!quName.equals("全部区")){
				popParamSql.append(" and dip3.district like '%").append(quName.trim()).append("%'");
			}
			if (!"".equals(deliver) && !"-1".equals(deliver)) {
				condition.append(" and d.id=" + deliver);
				popParamSql.append(" and dip3.deliver_type=").append(deliver);
			}
			if ("1".equals(date)) {
				condition.append(" and a.limit_datetime <= now() ");
			} else if ("2".equals(date)) {
				condition.append(" and a.limit_datetime > date_add(now(),interval -1 day) and a.limit_datetime < now()");
			} else if ("3".equals(date)) {
				condition.append(" and a.limit_datetime <= date_add(now(),interval -1 day) and a.limit_datetime > date_add(now(),interval -2 day) ");
			} else if ("4".equals(date)) {
				condition.append(" and a.limit_datetime <= date_add(now(),interval -2 day) and a.limit_datetime > date_add(now(),interval -3 day) ");
			} else if ("5".equals(date)) {
				condition.append(" and a.limit_datetime <= date_add(now(),interval -3 day) ");
			} else if ("6".equals(date)) {
				condition.append(" and a.limit_yesterday = '"+ DateUtil.getNowDateStr() + "' ");
			} else {
				condition.append(" and 1=2 ");
			}
			HashMap orderTypeParamMap = new HashMap();
			orderTypeParamMap.put("column", "id,name");
			orderTypeParamMap.put("table", "user_order_type");
			orderTypeParamMap.put("condition", "id>0");
			List<HashMap<String, String>> orderTypeRows = commonMapper.getCommonInfo(orderTypeParamMap);
			List<HashMap<String, String>> listRows = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> paramMap = new HashMap<String, String>();
			HashMap<String, String> paramMapCount = new HashMap<String, String>();
			int total = 0;
			if(popId == PopBussiness.POP_MMB){
				paramMap.put("column", "b.package_code,j.name provinceName,k.city cityName,l.area areaName, a.order_type,a.order_code,c.name stockAreaName,d.name delivername,a.time_level,a.time,"
						+ " f.deliver_state,f.deliver_info,"
						+ " case when f.deliver_state in (7) then hour(timediff(f.receive_time,a.receive_datetime)) "
						+ " when f.deliver_state in (6,8,10) then hour(timediff(f.return_time,a.receive_datetime)) "
						+ "else hour(timediff(date_format(now(),'%Y-%m-%d %T' ),date_format(a.receive_datetime,'%Y-%m-%d %T' ))) end as hours, "
						+ " date_format(f.post_time,'%Y-%m-%d %T' ) post_time, "
						+ "a.address,h.username,date_format(h.datetime,'%Y-%m-%d %T' ) hdt,h.remark,i.code ");
				paramMap.put("table", " effect_order_info a "
						+ " LEFT JOIN audit_package b ON a.order_id=b.order_id "
						+ " LEFT JOIN stock_area c ON c.id=b.areano "
						+ " LEFT JOIN deliver_corp_info d ON d.id=b.deliver "
						+ " LEFT JOIN stock_area e ON b.areano=e.id "
						+ " LEFT JOIN deliver_order f ON f.order_id=a.order_id"
						+ " LEFT JOIN user_order_extend_info g ON a.order_code=g.order_code "
						+ " LEFT JOIN effect_post_info h ON a.order_id=h.order_id"
						+ " LEFT JOIN order_stock i ON a.order_id = i.order_id"
						+ " LEFT JOIN provinces j ON g.add_id1=j.id"
						+ " LEFT JOIN province_city k ON g.add_id2=k.id"
						+ " LEFT JOIN city_area l ON g.add_id3=l.id");
				paramMap.put("condition", condition.toString()+" limit "+(page.getPage()-1) * page.getRows()+","+page.getRows());
				listRows = commonMapper.getCommonInfo(paramMap);
				
				paramMapCount.put("column", " count(a.id) thecount ");
				paramMapCount.put("table", "  effect_order_info a "
						+ " LEFT JOIN audit_package b ON a.order_id=b.order_id "
						+ " LEFT JOIN stock_area c ON c.id=b.areano "
						+ " LEFT JOIN deliver_corp_info d ON d.id=b.deliver "
						+ " LEFT JOIN stock_area e ON b.areano=e.id "
						+ " LEFT JOIN deliver_order f ON f.order_id=a.order_id"
						+ " LEFT JOIN user_order_extend_info g ON a.order_code=g.order_code ");
				paramMapCount.put("condition", condition.toString());
				List<HashMap<String, Object>> count = commonMapper.getCommonInfoCount(paramMapCount);
				if (count != null && count.size() > 0 && count.get(0) != null) {
					total = StringUtil.toInt(count.get(0).get("thecount") + "");
				}
			}else if(popId == PopBussiness.POP_JD){
				paramMap.put("condition",popParamSql.toString());
				listRows = commonMapper.getPOPCommonInfo(paramMap);
				listRows = deliveryService.getPOPDeliverInfo(listRows,date);//组装数据
			}
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
					String timeLevel = map.get("time_level")+"";
					if("1".equals(timeLevel)){
						map.put("time_level", "区县");
					}else if("2".equals(timeLevel)){
						map.put("time_level", "乡镇");
					}else if("3".equals(timeLevel)){
						map.put("time_level", "村");
					}
					int popid = StringUtil.toInt(map.get("pop_type")+"");
					map.put("popName", PopBussiness.popMap.get(popId));
					if(popid == PopBussiness.POP_JD){
						int storageId = StringUtil.toInt(map.get("storage_id")+"");
						map.put("stockAreaName", "京东仓");
						int deliverType = StringUtil.toInt(map.get("deliver_type")+"");
						map.put("delivername", "京东快递");
					} 
					
					if(popid != PopBussiness.POP_JD){
						int deliverState = StringUtil.toInt(map.get("deliver_state")+"");
						String deliverStateInfo = DeliverOrderInfoBean.deliverStateMap.get(deliverState);
						if(orderTypeRows!=null && orderTypeRows.size()>0){
							for(int j=0;j<orderTypeRows.size();j++){
								HashMap orderTypeMap = (HashMap)orderTypeRows.get(j);
								String orderType = (map.get("order_type")==null?"":map.get("order_type").toString());
								if("9".equals(orderType)){
									map.put("order_type", "其他");
									break;
								}else if(orderType.equals(orderTypeMap.get("id").toString())){
									map.put("order_type", orderTypeMap.get("name").toString());
									break;
								}
							}
						}
						map.put("deliver_state", deliverStateInfo);
					}
					
				}
			}
			
			datagrid.setRows(listRows);
			datagrid.setTotal((long)total);// 设置总记录数
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		return datagrid;
	}

	@RequestMapping("/getEffectDeliverTimeExcel")
	public void getEffectDeliverTimeExcel (HttpServletRequest request,HttpServletResponse response) throws Exception{
		List listRows = null;
		try {
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null) {
				return;
			}
			UserGroupBean group= user.getGroup();
			if (!group.isFlag(3064)) {
				return;
			}
			int popId = StringUtil.toInt(request.getParameter("popId"));
			String stockArea = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("stockArea")));
			String sheng = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("sheng")));
			String shi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("shi")));
			String qu = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("qu")));
			StringBuffer condition = new StringBuffer();
			condition.append("1=1");
			if (popId != -1) {
				condition.append(" and a.pop_id=" + popId);
			}
			if (!"".equals(stockArea)) {
				if(popId == PopBussiness.POP_JD){
					condition.append(" and e.id="+ProductStockBean.AREA_JD);
				}else{
					condition.append(" and e.id=" + stockArea);
				}
			}
			if (!"".equals(sheng)) {
				condition.append(" and b.id=" + sheng);
			}
			if (!"".equals(shi)) {
				condition.append(" and c.id=" + shi);
			}
			if (!"".equals(qu)) {
				condition.append(" and d.id=" + qu);
			}
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "a.*,b.name sheng,c.city shi,d.area qu,e.name stockName");
			paramMap.put("table", " effect_deliver_time a "
					+ " LEFT JOIN provinces b ON a.province_id=b.id "
					+ " LEFT JOIN province_city c ON a.province_city_id=c.id "
					+ " LEFT JOIN city_area d ON a.city_area_id=d.id "
					+ " LEFT JOIN stock_area e ON a.area_id=e.id");
			paramMap.put("condition", condition.toString());
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		    listRows = commonMapper.getCommonInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		String fileName = DateUtil.getNow().substring(0, 10);
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		
		header.add("序号");
		header.add("省");
		header.add("市");
		header.add("区");
		header.add("仓");
		header.add("POP商家");
		header.add("区时效");
		header.add("乡镇时效");
		header.add("村时效");
			
		int size = header.size();
			
		if (listRows != null && listRows.size() > 0) {
			int x = listRows.size();
			for (int i = 0; i < x; i++) {
				HashMap map = (HashMap) listRows.get(i);
				ArrayList tmp = new ArrayList();
				tmp.add(i+1 +"");
				tmp.add(map.get("sheng")+"");
				tmp.add(map.get("shi")+"");
				tmp.add(map.get("qu")+"");
				tmp.add(map.get("stockName")+"");
				int popId = (Integer)map.get("pop_id");
				if(popId == PopBussiness.POP_JD){
					tmp.add("京东");
				}else if(popId == PopBussiness.POP_MMB){
					tmp.add("买卖宝");
				}
				tmp.add(map.get("city_area_time")+"");
				tmp.add(map.get("town_time")+"");
				tmp.add(map.get("village_time")+"");
				bodies.add(tmp);
			}
		}
		headers.add(header);
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
		excel.setColMergeCount(size);
        List<Integer> row  = new ArrayList<Integer>();
        List<Integer> col  = new ArrayList<Integer>();
        excel.setRow(row);
        excel.setCol(col);
        excel.buildListHeader(headers);
        excel.buildListBody(bodies);
        excel.exportToExcel(fileName, response, "");
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/EffectOrderInfoExcel")
	public void EffectOrderInfoExcel (HttpServletRequest request,HttpServletResponse response) throws Exception{
		List listRows = null;
		int popId = StringUtil.toInt(request.getParameter("popId"));
		
		try {
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null) {
				return;
			}
			UserGroupBean group= user.getGroup();
			if (!group.isFlag(3065)) {
				return;
			}
			String stockArea = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("stockArea")));
			String deliver = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("deliver")));
			String sheng = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("sheng")));
			String shi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("shi")));
			String qu = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("qu")));
			String date = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("date")));
			String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
			String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
			String shengName = StringUtil.convertNull(request.getParameter("shengName"));
			String shiName = StringUtil.convertNull(request.getParameter("shiName"));
			String quName = StringUtil.convertNull(request.getParameter("quName"));
			
			StringBuffer condition = new StringBuffer();
			StringBuffer popParamSql = new StringBuffer();
			condition.append("1=1");
			if (startTime.equals("") || endTime.equals("")) {
//				j.setMsg("请选择出库时间范围！");
				return;
			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 15) {
//				j.setMsg("最多查询15天的信息！");
				return;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				condition.append(" and a.receive_datetime between '"+ startTime + " 00:00:00' and '" + endTime+ " 23:59:59'");
				popParamSql.append(" and dip3.time between '").append(startTime).append(" 00:00:00'")
	   		 	.append(" and '").append(endTime).append(" 23:59:59'").append(" and dip3.deliver_state=1 ");
			}
			if (popId != -1) {
				popParamSql.append(" and dip3.pop_type=").append(popId);
			}
			if (!"".equals(stockArea)) {
				condition.append(" and c.id=" + stockArea);
				popParamSql.append(" and dip3.storage_id=").append(stockArea);
			}
			if (!"".equals(sheng)) {
				condition.append(" and g.add_id1=" + sheng);
			}
			if (!"".equals(shi)) {
				condition.append(" and g.add_id2=" + shi);
			}
			if (!"".equals(qu)) {
				condition.append(" and g.add_id3=" + qu);
			}
			if(!shengName.equals("")&&!shengName.equals("全部省")){
				popParamSql.append(" and dip3.province like '%").append(shengName.trim()).append("%'");
			}
			if(!shiName.equals("")&&!shiName.equals("全部市")){
				popParamSql.append(" and dip3.city like '%").append(shiName.trim()).append("%'");
			}
			if(!quName.equals("")&&!quName.equals("全部区")){
				popParamSql.append(" and dip3.district like '%").append(quName.trim()).append("%'");
			}
			if (!"".equals(deliver) && !"-1".equals(deliver)) {
				condition.append(" and d.id=" + deliver);
				popParamSql.append(" and dip3.deliver_type=").append(deliver);
			}
			if ("1".equals(date)) {
				condition.append(" and a.limit_datetime <= now() ");
			} else if ("2".equals(date)) {
				condition.append(" and a.limit_datetime > date_add(now(),interval -1 day) and a.limit_datetime < now()");
			} else if ("3".equals(date)) {
				condition.append(" and a.limit_datetime <= date_add(now(),interval -1 day) and a.limit_datetime > date_add(now(),interval -2 day) ");
			} else if ("4".equals(date)) {
				condition.append(" and a.limit_datetime <= date_add(now(),interval -2 day) and a.limit_datetime > date_add(now(),interval -3 day) ");
			} else if ("5".equals(date)) {
				condition.append(" and a.limit_datetime <= date_add(now(),interval -3 day) ");
			} else if ("6".equals(date)) {
				condition.append(" and a.limit_yesterday = '"+ DateUtil.getNowDateStr() + "' ");
			} else {
				condition.append(" and 1=2 ");
			}
			HashMap orderTypeParamMap = new HashMap();
			orderTypeParamMap.put("column", "id,name");
			orderTypeParamMap.put("table", "user_order_type");
			orderTypeParamMap.put("condition", "id>0");
			List<HashMap<String, String>> orderTypeRows = commonMapper.getCommonInfo(orderTypeParamMap);
			HashMap<String, String> paramMap = new HashMap<String, String>();
			if(popId == PopBussiness.POP_MMB){
				paramMap.put("column", "j.name provinceName,k.city cityName,l.area areaName, a.order_type,a.order_code,c.name stockAreaName,d.name delivername,a.time_level,a.time,"
						+ " f.deliver_state,f.deliver_info,"
						+ " case when f.deliver_state in (7) then hour(timediff(f.receive_time,a.receive_datetime)) "
						+ " when f.deliver_state in (6,8,10) then hour(timediff(f.return_time,a.receive_datetime)) "
						+ "else hour(timediff(date_format(now(),'%Y-%m-%d %T' ),date_format(a.receive_datetime,'%Y-%m-%d %T' ))) end as hours, "
						+ " date_format(f.post_time,'%Y-%m-%d %T' ) post_time, "
						+ "a.address,h.username,h.datetime,h.remark,i.code ");
				paramMap.put("table", " effect_order_info a "
						+ " LEFT JOIN audit_package b ON a.order_id=b.order_id "
						+ " LEFT JOIN stock_area c ON c.id=b.areano "
						+ " LEFT JOIN deliver_corp_info d ON d.id=b.deliver "
						+ " LEFT JOIN stock_area e ON b.areano=e.id "
						+ " LEFT JOIN deliver_order f ON f.order_id=a.order_id"
						+ " LEFT JOIN user_order_extend_info g ON a.order_code=g.order_code "
						+ " LEFT JOIN effect_post_info h ON a.order_id=h.order_id"
						+ " LEFT JOIN order_stock i ON a.order_id = i.order_id"
						+ " LEFT JOIN provinces j ON g.add_id1=j.id"
						+ " LEFT JOIN province_city k ON g.add_id2=k.id"
						+ " LEFT JOIN city_area l ON g.add_id3=l.id");
				paramMap.put("condition", condition.toString());
				listRows = commonMapper.getCommonInfo(paramMap);
			}else if(popId == PopBussiness.POP_JD){
				paramMap.put("condition",popParamSql.toString());
				listRows = commonMapper.getPOPCommonInfo(paramMap);
				listRows = deliveryService.getPOPDeliverInfo(listRows,date);//组装数据
			}
			
			if (listRows != null) {
				for (int i = 0; i < listRows.size(); i++) {
					HashMap map = (HashMap) listRows.get(i);
					String timeLevel = map.get("time_level") + "";
					if ("1".equals(timeLevel)) {
						map.put("time_level", "区县");
					} else if ("2".equals(timeLevel)) {
						map.put("time_level", "乡镇");
					} else if ("3".equals(timeLevel)) {
						map.put("time_level", "村");
					}
					int popid = StringUtil.toInt(map.get("pop_type") + "");
					map.put("popName", PopBussiness.popMap.get(popId));
					if (popid == PopBussiness.POP_JD) {
						map.put("stockAreaName", "京东仓");
						map.put("delivername", "京东快递");
					}

					if (popid != PopBussiness.POP_JD) {
						int deliverState = StringUtil.toInt(map.get("deliver_state") + "");
						String deliverStateInfo = DeliverOrderInfoBean.deliverStateMap.get(deliverState);
						if (orderTypeRows != null && orderTypeRows.size() > 0) {
							for (int j = 0; j < orderTypeRows.size(); j++) {
								HashMap orderTypeMap = (HashMap) orderTypeRows.get(j);
								String orderType = (map.get("order_type") == null ? "": map.get("order_type").toString());
								if ("9".equals(orderType)) {
									map.put("order_type", "其他");
									break;
								} else if (orderType.equals(orderTypeMap.get("id").toString())) {
									map.put("order_type",orderTypeMap.get("name").toString());
									break;
								}
							}
						}
						map.put("deliver_state", deliverStateInfo);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		String fileName = DateUtil.getNow().substring(0, 10);
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		
		header.add("序号");
		if (popId == PopBussiness.POP_MMB) {
			header.add("订单号");
		}else if (popId == PopBussiness.POP_JD) {
			header.add("mmb订单号");
			header.add("京东订单号");
			header.add("包裹单号");
			header.add("POP商家");
		}
		header.add("仓");
		header.add("快递公司");
		header.add("级别");
		header.add("时效H");
		header.add("已出库时长H");
		header.add("配送状态");
		header.add("订单类型");
		header.add("配送描述");
		header.add("最后反馈时间");
		header.add("订单地址");
		header.add("省");
		header.add("市");
		header.add("区");
		header.add("最后跟进人");
		header.add("最后跟进时间");
		header.add("跟进情况描述");
			
		int size = header.size();
			
		if (listRows != null && listRows.size() > 0) {
			int x = listRows.size();
			for (int i = 0; i < x; i++) {
				HashMap map = (HashMap) listRows.get(i);
				ArrayList tmp = new ArrayList();
				tmp.add(i+1 +"");
				tmp.add(map.get("order_code")+"");
				if (popId == PopBussiness.POP_JD) {
					tmp.add(map.get("pop_order_code")+"");
					tmp.add(map.get("package_code")+"");
					tmp.add(PopBussiness.popMap.get(popId));
				}
				tmp.add(map.get("stockAreaName")+"");
				tmp.add(map.get("delivername")+"");
				tmp.add(map.get("time_level")+"");
				tmp.add(map.get("time")+"");
				tmp.add(map.get("hours")+"");
				tmp.add(map.get("deliver_state")+"");
				tmp.add(map.get("order_type")+"");
				tmp.add(map.get("deliver_info")+"");
				tmp.add(map.get("post_time")+"");
				tmp.add(map.get("address")+"");
				tmp.add(map.get("provinceName")+"");
				tmp.add(map.get("cityName")+"");
				tmp.add(map.get("areaName")+"");
				tmp.add(map.get("username")+"");
				tmp.add(map.get("datetime")+"");
				tmp.add(map.get("remark")+"");
				bodies.add(tmp);
			}
		}
		headers.add(header);
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
		excel.setColMergeCount(size);
        List<Integer> row  = new ArrayList<Integer>();
        List<Integer> col  = new ArrayList<Integer>();
        excel.setRow(row);
        excel.setCol(col);
        excel.buildListHeader(headers);
        excel.buildListBody(bodies);
        excel.exportToExcel(fileName, response, "");
	}
	public void returnErrJsp(HttpServletRequest request, HttpServletResponse response, Json j) throws ServletException, IOException {
		request.setAttribute("msg", j.getMsg());
		request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	}
	/**
	 * 导入配送跟进信息
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/uploadEffectPostInfo")
	public String uploadEffectPostInfo(HttpServletRequest request, HttpServletResponse response)throws Exception{

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("result", "");
			request.setAttribute("msg", "您还没有登录");
			return "admin/orderStock/importEffectPostInfo";
		}
		
		if(user.getGroup() == null || !user.getGroup().isFlag(3063)){
			request.setAttribute("result", "");
			request.setAttribute("msg", "您没有操作权限");
			return "admin/orderStock/importEffectPostInfo";
		}
		 // 转型为MultipartHttpRequest：   
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        // 获得文件：   
        MultipartFile file = multipartRequest.getFile("attendance");
        // 获得输入流：   
        InputStream input = file.getInputStream();   
        // 获得文件名：   
        String attendanceFileName = file.getOriginalFilename();
		int index = attendanceFileName.lastIndexOf(".");
		String suffix = attendanceFileName.substring(index + 1, attendanceFileName.length());

		StringBuilder msg = new StringBuilder();
		
		List<String[]> rows = ExcelUtil.rosolveFile(input, suffix, 1);
        
		String result = null;
		
		if (rows != null  && rows.size() > 0) {
			int total = 0;
			int success = 0;
			boolean empty = true;
			DbOperation db = new DbOperation(DbOperation.DB);
			try {
				for (int i = 0; i < rows.size(); i++) {			
					String[] row = rows.get(i);
					if (row == null || row.length == 0) {
						continue;
					}
					empty = true;
					for (int j = 0; j < row.length; j++) {
						if(!"".equals(row[j]))
							empty = false;
					}
					// 全部列为空不处理
					if(empty){
						continue;
					}
					
					try {			
						total++;	
						if(row.length < 2){
							msg.append("第").append(i + 1).append("行, 跟进情描述填不能为空<br/>");
							continue;
						}
						String orderStockCode = StringUtil.convertNull(row[0].trim());
						if ("".equals(orderStockCode)) {
							msg.append("第").append(i + 1).append("行, 包裹单号填写错误<br/>");
							continue;
						}
						String remark = StringUtil.convertNull(row[1].trim());
						if ("".equals(remark)) {
							msg.append("第").append(i + 1).append("行, 跟进情描述填不能为空<br/>");
							continue;
						}
						ResultSet rs = db.executeQuery("select order_id,order_code from audit_package where package_code='" + orderStockCode + "'");
						int orderId = 0; 
						String orderCode = ""; 
						if (rs.next()) {
							orderId = rs.getInt(1);
							orderCode = rs.getString(2);
						}
						if (orderId == 0) {
							msg.append("第").append(i + 1).append("行, 包裹单号" + orderStockCode + "不存在<br/>");
							continue;
						}
						rs.close();
						rs = db.executeQuery("select id from effect_post_info where order_id=" + orderId );
						int id = 0; 
						if (rs.next()) {
							id = rs.getInt(1);
						}
						rs.close();
						if (id > 0) {
							if (!db.executeUpdate("update effect_post_info set username='" + user.getUsername() + "',datetime='" + DateUtil.getNow() + "',remark='"+remark+ "' where id=" + id)) {
								msg.append("第").append(i + 1).append("行, ").append("更新失败").append("<br/>");
							} else {
								success ++;
							}
						} else {
							if (!db.executeUpdate("insert into effect_post_info(order_id,order_code,username,datetime,remark) values ("+orderId+",'" +orderCode + "','" +user.getUsername() + "','" +DateUtil.getNow() + "','" + remark+ "')")) {
								msg.append("第").append(i + 1).append("行, ").append("添加失败").append("<br/>");
							} else {
								success ++;
							}
						}
					} catch (NullPointerException e) {
						msg.append("第").append(i + 1).append("行, 数据为空<br/>");
						continue;
					} catch (Exception e) {
						msg.append("第").append(i + 1).append("行, ").append(e.getMessage()).append("<br/>");
						continue;
					}
									
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				db.release();
			}	
			result = "共" + total + "行,成功" + success + "行,失败" + (total - success) + "行.";
		} else {
			result = "Excel内容为空";
			msg.append("Excel内容为空");
		}
		
		request.setAttribute("result", result);
		request.setAttribute("msg", msg.toString());
		return "admin/orderStock/importEffectPostInfo";
	}
	@RequestMapping("/downloadEffectPostInfo")
	public void downloadEffectPostInfo(HttpServletRequest request, HttpServletResponse response) {
		DbOperation slave2Dbop = new DbOperation(DbOperation.DB_SLAVE2);
		try {
			ExportExcel excel = new ExportExcel(AbstractExcel.HSSF);
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header = new ArrayList<String>();
			header.add("包裹单号");
			header.add("跟进情况描述");
			 
			headers.add(header);
			
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			excel.setMayMergeColumn(mayMergeColumn);
			
			List<Integer> mayMergeRow = new ArrayList<Integer>();
			excel.setMayMergeRow(mayMergeRow);
			
			excel.setColMergeCount(headers.get(0).size());			
			List<Integer> row  = new ArrayList<Integer>();
			List<Integer> col  = new ArrayList<Integer>();			
			excel.setRow(row);
			excel.setCol(col);			
			excel.buildListHeader(headers);
			
			String fileName = DateUtil.getNow().substring(0, 10);
			excel.exportToExcel(fileName, response, "");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			slave2Dbop.release();
		}
	}
	/**
	 * 常规类指标查询
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getCommonTypeList")
	@ResponseBody
	public EasyuiDataGridJson getCommonTypeList(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		try {
			String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
			String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
			String condi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("condition")));
			StringBuffer condition = new StringBuffer();
			StringBuffer groupBy = new StringBuffer();
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("msg", "您还没有登录");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
//			if(user.getGroup() == null || !user.getGroup().isFlag(3065)){
//				request.setAttribute("msg", "您没有操作权限");
//				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
//				return null;
//			}
			if (startTime.equals("") || endTime.equals("")) {
				request.setAttribute("msg", "请选择时间范围！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if(condi.equals("")){
				List list = new ArrayList();
				datagrid.setRows(list);
				return datagrid;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and a.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			}
			String select[] = condi.split(",");
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" a.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" d.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" d.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" d.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" x.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}else if("7".equals(select[i])){
						groupBy.append(" a.deliver,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "sum(if(l.deliver_state=7,1,0)) ttCount,"
								 + "sum(if(m.status=11,1,0)) thCount,"
								 + "sum(if(l.deliver_state=10,1,0)) nxCount,"
								 + "sum(if(l.deliver_state<>6 and l.deliver_state<>7 and l.deliver_state<>10,1,0)) zxCount,"
								 + "x.time_level timeLevel,l.deliver_state,j.name productTypeName,e.name provinceName,f.city cityName,g.area quName,h.street streetName,b.name deliverName,c.name areaName,count(a.id) fhCount");
			
			paramMap.put("table", " audit_package a "
					+ " LEFT JOIN deliver_corp_info b ON a.deliver=b.id "
					+ " LEFT JOIN stock_area c ON a.areano=c.id"
					+ " LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code"
					+ " LEFT JOIN provinces e ON d.add_id1=e.id"
					+ " LEFT JOIN province_city f ON d.add_id2=f.id"
					+ " LEFT JOIN city_area g ON d.add_id3=g.id"
					+ " LEFT JOIN area_street h ON d.add_id4=h.id"
					+ " LEFT JOIN deliver_order k ON k.order_id=a.order_id"
					+ " LEFT JOIN deliver_order_info l ON k.id=l.deliver_id"
					+ " LEFT JOIN user_order m ON m.id=a.order_id"
					+ " LEFT JOIN user_order_type j ON j.type_id=m.order_type"
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			
			paramMap.put("condition", "1=1 "+condition.toString()+"  limit "+(page.getPage()-1) * page.getRows()+","+page.getRows());
			
			List listRows = commonMapper.getCommonInfo(paramMap);
			
			
			
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
					int fhCount = StringUtil.toInt(map.get("fhCount")+"");//发货数量
					int ttCount = StringUtil.toInt(map.get("ttCount")+"");//订单妥投数量
					int thCount = StringUtil.toInt(map.get("thCount")+"");//订单退回数量
					int zxCount = StringUtil.toInt(map.get("zxCount")+"");//订单正向在途数量
					int nxCount = StringUtil.toInt(map.get("nxCount")+"");//订单逆向在途数量
					if(condi.contains("1")){
						map.put("cang", map.get("areaName"));
					}
					if(condi.contains("2")){
						map.put("sheng", map.get("provinceName"));
					}
					if(condi.contains("3")){
						map.put("shi", map.get("cityName"));
					}
					if(condi.contains("4")){
						map.put("qu", map.get("quName"));
					}
					if(condi.contains("5")){
						String timeLevel = map.get("timeLevel")+"";
						if("1".equals(timeLevel)){
							map.put("timeLevels", "区县");
						}else if("2".equals(timeLevel)){
							map.put("timeLevels", "乡镇");
						}else if("3".equals(timeLevel)){
							map.put("timeLevels", "村");
						}
					}
					if(condi.contains("6")){
						map.put("productline", map.get("productTypeName"));
					}
					if(condi.contains("7")){
						map.put("deliver", map.get("deliverName"));
					}
					if(fhCount!=0){
						map.put("ttper", Arith.div(ttCount*100,fhCount,2)+"%");//妥投率
						map.put("thper", Arith.div(thCount*100,fhCount,2)+"%");//退回率
						map.put("zxper", Arith.div(zxCount*100,fhCount,2)+"%");//正向在途率
						map.put("nxper", Arith.div(nxCount*100,fhCount,2)+"%");//逆向在途率
					}
					
				}
			}
			datagrid.setRows(listRows);
			HashMap<String, String> paramMapCount = new HashMap<String, String>();
			paramMapCount.put("column", "count(a.id) fhCount");
			paramMapCount.put("table", " audit_package a "
					+ " LEFT JOIN deliver_corp_info b ON a.deliver=b.id "
					+ " LEFT JOIN stock_area c ON a.areano=c.id"
					+ " LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code"
					+ " LEFT JOIN provinces e ON d.add_id1=e.id"
					+ " LEFT JOIN province_city f ON d.add_id2=f.id"
					+ " LEFT JOIN city_area g ON d.add_id3=g.id"
					+ " LEFT JOIN area_street h ON d.add_id4=h.id"
					+ " LEFT JOIN user_order m ON m.id=a.order_id"
					+ " LEFT JOIN user_order_type j ON j.type_id=m.order_type"
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			paramMapCount.put("condition", "1=1 "+condition.toString());
			
			List<HashMap<String, Object>> count = commonMapper.getCommonInfoCount(paramMapCount);
			int total = 0;
			if (count != null && count.size() > 0 && count.get(0) != null) {
				total = count.size();
			}
			datagrid.setTotal((long)total);// 设置总记录数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	@RequestMapping("/getCommonTypeListExcel")
	public void getCommonTypeListExcel (HttpServletRequest request,HttpServletResponse response) throws Exception{
		List listRows = null;
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String select[] = request.getParameterValues("condition");
		StringBuffer condition = new StringBuffer();
		StringBuffer groupBy = new StringBuffer();
		try {
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null) {
				return;
			}
			UserGroupBean group= user.getGroup();
			if (!group.isFlag(3069)) {
				request.setAttribute("msg", "无此权限！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (startTime.equals("") || endTime.equals("")) {
				return;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;

			}
			if(select==null || select.length==0){
				request.setAttribute("msg", "请选择查询条件");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and a.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			}
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" a.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" d.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" d.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" d.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" x.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}else if("7".equals(select[i])){
						groupBy.append(" a.deliver,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "sum(if(l.deliver_state=7,1,0)) ttCount,"
								 + "sum(if(m.status=11,1,0)) thCount,"
								 + "sum(if(l.deliver_state=10,1,0)) nxCount,"
								 + "sum(if(l.deliver_state<>6 and l.deliver_state<>7 and l.deliver_state<>10,1,0)) zxCount,"
								 + "x.time_level timeLevel,l.deliver_state,j.name productTypeName,e.name provinceName,f.city cityName,g.area quName,h.street streetName,b.name deliverName,c.name areaName,count(a.id) fhCount");
			
			paramMap.put("table", " audit_package a "
					+ " LEFT JOIN deliver_corp_info b ON a.deliver=b.id "
					+ " LEFT JOIN stock_area c ON a.areano=c.id"
					+ " LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code"
					+ " LEFT JOIN provinces e ON d.add_id1=e.id"
					+ " LEFT JOIN province_city f ON d.add_id2=f.id"
					+ " LEFT JOIN city_area g ON d.add_id3=g.id"
					+ " LEFT JOIN area_street h ON d.add_id4=h.id"
					+ " LEFT JOIN deliver_order k ON k.order_id=a.order_id"
					+ " LEFT JOIN deliver_order_info l ON k.id=l.deliver_id"
					+ " LEFT JOIN user_order m ON m.id=a.order_id"
					+ " LEFT JOIN user_order_type j ON j.type_id=m.order_type"
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			
			paramMap.put("condition", "1=1 "+condition.toString());
			
			listRows = commonMapper.getCommonInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = DateUtil.getNow().substring(0, 10);
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		header.add("序号");
		header.add("发货仓");
		header.add("快递公司");
		header.add("省");
		header.add("市");
		header.add("区");
		header.add("乡/镇/街");
		header.add("产品线");
		header.add("发货量");
		header.add("妥投率");
		header.add("退回率");
		header.add("正向在途率");
		header.add("逆向在途率");
			
		int size = header.size();
		if(listRows!=null && listRows.size()!=0){
			for(int i = 0;i<listRows.size();i++){
				HashMap map = (HashMap)listRows.get(i);
				ArrayList tmp = new ArrayList();
				tmp.add(i+1 +"");
				int fhCount = StringUtil.toInt(map.get("fhCount")+"");//发货数量
				int ttCount = StringUtil.toInt(map.get("ttCount")+"");//订单妥投数量
				int thCount = StringUtil.toInt(map.get("thCount")+"");//订单退回数量
				int zxCount = StringUtil.toInt(map.get("zxCount")+"");//订单正向在途数量
				int nxCount = StringUtil.toInt(map.get("nxCount")+"");//订单逆向在途数量
				if(Arrays.asList(select).contains("1")){
					tmp.add(map.get("areaName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("7")){
					tmp.add(map.get("deliverName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("2")){
					tmp.add( map.get("provinceName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("3")){
					tmp.add(map.get("cityName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("4")){
					tmp.add(map.get("quName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("5")){
					String timeLevel = map.get("timeLevel")+"";
					if("1".equals(timeLevel)){
						tmp.add("区县");
					}else if("2".equals(timeLevel)){
						tmp.add("乡镇");
					}else if("3".equals(timeLevel)){
						tmp.add("村");
					}else{
						tmp.add(map.get(""));
					}
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("6")){
					tmp.add(map.get("productTypeName"));
				}else{
					tmp.add(map.get(""));
				}
				if(fhCount!=0){
					tmp.add(fhCount+"");
					tmp.add(Arith.div(ttCount*100,fhCount,2)+"%");//妥投率
					tmp.add(Arith.div(thCount*100,fhCount,2)+"%");//退回率
					tmp.add(Arith.div(zxCount*100,fhCount,2)+"%");//正向在途率
					tmp.add(Arith.div(nxCount*100,fhCount,2)+"%");//逆向在途率
				}else{
					tmp.add(0+"");
					tmp.add(0+"%");//妥投率
					tmp.add(0+"%");//退回率
					tmp.add(0+"%");//正向在途率
					tmp.add(0+"%");//逆向在途率
				}
				bodies.add(tmp);
			}
		}	
		headers.add(header);
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
		excel.setColMergeCount(size);
        List<Integer> row  = new ArrayList<Integer>();
        List<Integer> col  = new ArrayList<Integer>();
        excel.setRow(row);
        excel.setCol(col);
        excel.buildListHeader(headers);
        excel.buildListBody(bodies);
        excel.exportToExcel(fileName, response, "");
	}
	/**
	 * 时效指标查询
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getEffectQueryList")
	@ResponseBody
	public EasyuiDataGridJson getEffectQueryList(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		try {
			String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
			String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
			String condi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("condition")));
			StringBuffer condition = new StringBuffer();
			StringBuffer groupBy = new StringBuffer();
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("msg", "您还没有登录");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
//			if(user.getGroup() == null || !user.getGroup().isFlag(3065)){
//				request.setAttribute("msg", "您没有操作权限");
//				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
//				return null;
//			}
			if (startTime.equals("") || endTime.equals("")) {
				request.setAttribute("msg", "请选择时间范围！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if(condi.equals("")){
				List list = new ArrayList();
				datagrid.setRows(list);
				return datagrid;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and a.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			}
			String select[] = condi.split(",");
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" a.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" d.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" d.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" d.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" x.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}else if("7".equals(select[i])){
						groupBy.append(" a.deliver,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			
			
			
			//fhCount 发货量
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "sum(if(l.deliver_state=7,1,0)) ttCount,"//ttCount 妥投 单量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7,1,0)) jsCount, "//jsCount 妥投及时
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7,TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),0)) ttztime, "//ttztime 妥投单量总时长
					+ "sum(if(l.deliver_state<>6 and l.deliver_state<>7 and l.deliver_state<>10 ,TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),0)) zxztime, "//zxztime 正向单量总时长
					+ "count(a.id) fhCount,"//发货量
					+ "sum(if(m.status=11,1,0)) thCount, "//thCount 退货量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7 and 24>TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) count24, "//count24 24小时妥投量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7 and 48>TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) count48, "//count48 48小时妥投量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7 and 72>TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) count72, "//count72 72小时妥投量
					+ "sum(if(l.deliver_state<>6 and l.deliver_state<>7 and l.deliver_state<>10 and 168<TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) zxztCount, "//zxztCount 正向在途超七天数量
					+ "sum(if(m.status=11,TIMESTAMPDIFF(DAY,o.storage_time,k.return_time),0)) thDays, "//退货天数
					+ "sum(if(m.status=11,TIMESTAMPDIFF(DAY,o.storage_time,k.receive_time),0)) thzzTimes, "//退货周转天数
					+ "x.time_level timeLevel,l.deliver_state,j.name productTypeName,e.name provinceName,f.city cityName,g.area quName,h.street streetName,b.name deliverName,c.name areaName");
			
			paramMap.put("table", " audit_package a "
					+ "LEFT JOIN deliver_corp_info b ON a.deliver=b.id "  
					+ "LEFT JOIN stock_area c ON a.areano=c.id  "
					+ "LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code  "
					+ "LEFT JOIN provinces e ON d.add_id1=e.id  "
					+ "LEFT JOIN province_city f ON d.add_id2=f.id  "
					+ "LEFT JOIN city_area g ON d.add_id3=g.id  "
					+ "LEFT JOIN area_street h ON d.add_id4=h.id  "
					+ "LEFT JOIN deliver_order k ON k.order_id=a.order_id  "
					+ "LEFT JOIN deliver_order_info l ON k.id=l.deliver_id  "
					+ "LEFT JOIN user_order m ON m.id=a.order_id  "
					+ "LEFT JOIN user_order_type j ON j.type_id=m.order_type "
					+ "LEFT JOIN effect_order_info n ON n.order_id=m.id "
					+ "LEFT JOIN returned_package o ON o.order_code=a.order_code "
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			
			paramMap.put("condition", "1=1 "+condition.toString()+" limit "+(page.getPage()-1) * page.getRows()+","+page.getRows());
			
			List listRows = commonMapper.getCommonInfo(paramMap);
			
			
			
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
					int jsCount = StringUtil.toInt(map.get("jsCount")+"");//订单妥投及时数量
					int ttCount = StringUtil.toInt(map.get("ttCount")+"");//订单妥投总数量
					float ttztime = StringUtil.toFloat(map.get("ttztime")+"");//妥投单量总时长
					float zxztime = StringUtil.toFloat(map.get("zxztime")+"");//正向单量总时长
					int fhCount = StringUtil.toInt(map.get("fhCount")+"");//发货总单量
					int count24 = StringUtil.toInt(map.get("count24")+"");//妥投时间为24小时的订单量
					int count48 = StringUtil.toInt(map.get("count48")+"");//妥投时间为48小时的订单量
					int count72 = StringUtil.toInt(map.get("count72")+"");//妥投时间为72小时的订单量
					int zxztCount = StringUtil.toInt(map.get("zxztCount")+"");//正向在途超过7天订单
					int thCount = StringUtil.toInt(map.get("thCount")+"");//退货单量
					float thTimes = StringUtil.toFloat(map.get("thDays")+"");//（退货入库时间-开始退回时间）总和
					float thzzTimes = StringUtil.toFloat(map.get("thzzTimes")+"");//（退货入库时间-发货完成时间）总和
					if(condi.contains("1")){
						map.put("cang", map.get("areaName"));
					}
					if(condi.contains("2")){
						map.put("sheng", map.get("provinceName"));
					}
					if(condi.contains("3")){
						map.put("shi", map.get("cityName"));
					}
					if(condi.contains("4")){
						map.put("qu", map.get("quName"));
					}
					if(condi.contains("5")){
						String timeLevel = map.get("timeLevel")+"";
						if("1".equals(timeLevel)){
							map.put("timeLevels", "区县");
						}else if("2".equals(timeLevel)){
							map.put("timeLevels", "乡镇");
						}else if("3".equals(timeLevel)){
							map.put("timeLevels", "村");
						}
					}
					if(condi.contains("6")){
						map.put("productline", map.get("productTypeName"));
					}
					if(condi.contains("7")){
						map.put("deliver", map.get("deliverName"));
					}
					if(ttCount!=0){
						//妥投及时率=妥投及时订单（在派送时效内达成的订单）/妥投总单量
						map.put("ttjsl", Arith.div(jsCount*100,ttCount,2)+"%");
						//妥投平均耗时=妥投单量总时长/妥投单量
						map.put("ttpjhs", Arith.div(ttztime,ttCount,2)+"");
					}else{
						map.put("ttjsl", 0+"%");
						map.put("ttpjhs",0+"");
					}
					if(fhCount!=0){
						//平均投递耗时=（订单正向状态终结-订单发货时间）的总耗时/该类型的总单量
						map.put("pjtdhs", Arith.div(zxztime,fhCount,2)+"");
						//24小时妥投率=妥投时间为24小时的订单/发货总单量
						map.put("ttl24", Arith.div(count24*100,fhCount,2)+"%");
						//48小时妥投率=妥投时间为48小时的订单/发货总单量
						map.put("ttl48", Arith.div(count48*100,fhCount,2)+"%");
						//72小时妥投率=妥投时间为72小时的订单/发货总单量
						map.put("ttl72", Arith.div(count72*100,fhCount,2)+"%");
						//正向在途超七天在途率=正向在途超过7天订单/总订单量
						map.put("zzztl7", Arith.div(zxztCount*100,fhCount,2)+"%");
						
					}else{
						map.put("pjtdhs",0+"");
						map.put("ttl24",  0+"%");
						map.put("ttl48",  0+"%");
						map.put("ttl72",  0+"%");
						map.put("zzztl7", 0+"%");
					}
					if(thCount!=0){
						//退货天数=（退货入库时间-开始退回时间）总和/退货单总量
						map.put("thTimes", Arith.div(thTimes,thCount,2)+"");
						//退货整体周转天数=（退货入库时间-发货完成时间）总和/退货单总量
						map.put("thzzTimes", Arith.div(thzzTimes,thCount,2)+"");
					}else{
						map.put("thTimes", 0+"");
						map.put("thzzTimes", 0+"");
					}
				}
			}
			datagrid.setRows(listRows);
			HashMap<String, String> paramMapCount = new HashMap<String, String>();
			paramMapCount.put("column", "count(a.id) fhCount");
			paramMapCount.put("table", " audit_package a "
					+ " LEFT JOIN deliver_corp_info b ON a.deliver=b.id "
					+ " LEFT JOIN stock_area c ON a.areano=c.id"
					+ " LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code"
					+ " LEFT JOIN provinces e ON d.add_id1=e.id"
					+ " LEFT JOIN province_city f ON d.add_id2=f.id"
					+ " LEFT JOIN city_area g ON d.add_id3=g.id"
					+ " LEFT JOIN area_street h ON d.add_id4=h.id"
					+ " LEFT JOIN order_stock i ON i.order_id=a.order_id"
					+ " LEFT JOIN user_order m ON m.id=a.order_id  "
					+ " LEFT JOIN user_order_type j ON j.type_id=m.order_type"
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			paramMapCount.put("condition", "1=1 "+condition.toString());
			
			List<HashMap<String, Object>> count = commonMapper.getCommonInfoCount(paramMapCount);
			int total = 0;
			if (count != null && count.size() > 0 && count.get(0) != null) {
				total = count.size();
			}
			datagrid.setTotal((long)total);// 设置总记录数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	@RequestMapping("/getEffectQueryListExcel")
	public void getEffectQueryListExcel (HttpServletRequest request,HttpServletResponse response) throws Exception{
		List listRows = null;
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String select[] = request.getParameterValues("condition");
		StringBuffer condition = new StringBuffer();
		StringBuffer groupBy = new StringBuffer();
		try {
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null) {
				return;
			}
			UserGroupBean group= user.getGroup();
			if (!group.isFlag(3069)) {
				request.setAttribute("msg", "无此权限！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (startTime.equals("") || endTime.equals("")) {
				return;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;

			}
			if(select==null || select.length==0){
				request.setAttribute("msg", "请选择查询条件");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and a.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			}
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" a.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" d.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" d.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" d.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" x.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}else if("7".equals(select[i])){
						groupBy.append(" a.deliver,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "sum(if(l.deliver_state=7,1,0)) ttCount,"//ttCount 妥投 单量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7,1,0)) jsCount, "//jsCount 妥投及时
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7,TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),0)) ttztime, "//ttztime 妥投单量总时长
					+ "sum(if(l.deliver_state<>6 and l.deliver_state<>7 and l.deliver_state<>10 ,TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),0)) zxztime, "//zxztime 正向单量总时长
					+ "count(a.id) fhCount,"//发货量
					+ "sum(if(m.status=11,1,0)) thCount, "//thCount 退货量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7 and 24>TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) count24, "//count24 24小时妥投量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7 and 48>TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) count48, "//count48 48小时妥投量
					+ "sum(if(n.limit_datetime >= k.post_time and l.deliver_state=7 and 72>TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) count72, "//count72 72小时妥投量
					+ "sum(if(l.deliver_state<>6 and l.deliver_state<>7 and l.deliver_state<>10 and 168<TIMESTAMPDIFF(HOUR,n.receive_datetime,k.receive_time),1,0)) zxztCount, "//zxztCount 正向在途超七天数量
					+ "sum(if(m.status=11,TIMESTAMPDIFF(DAY,o.storage_time,k.return_time),0)) thDays, "//退货天数
					+ "sum(if(m.status=11,TIMESTAMPDIFF(DAY,o.storage_time,k.receive_time),0)) thzzTimes, "//退货周转天数
					+ "x.time_level timeLevel,l.deliver_state,j.name productTypeName,e.name provinceName,f.city cityName,g.area quName,h.street streetName,b.name deliverName,c.name areaName");
			
			paramMap.put("table", " audit_package a "
					+ "LEFT JOIN deliver_corp_info b ON a.deliver=b.id "  
					+ "LEFT JOIN stock_area c ON a.areano=c.id  "
					+ "LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code  "
					+ "LEFT JOIN provinces e ON d.add_id1=e.id  "
					+ "LEFT JOIN province_city f ON d.add_id2=f.id  "
					+ "LEFT JOIN city_area g ON d.add_id3=g.id  "
					+ "LEFT JOIN area_street h ON d.add_id4=h.id  "
					+ "LEFT JOIN deliver_order k ON k.order_id=a.order_id  "
					+ "LEFT JOIN deliver_order_info l ON k.id=l.deliver_id  "
					+ "LEFT JOIN user_order m ON m.id=a.order_id  "
					+ "LEFT JOIN user_order_type j ON j.type_id=m.order_type "
					+ "LEFT JOIN effect_order_info n ON n.order_id=m.id "
					+ "LEFT JOIN returned_package o ON o.order_code=a.order_code "
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			
			paramMap.put("condition", "1=1 "+condition.toString());
			listRows = commonMapper.getCommonInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = DateUtil.getNow().substring(0, 10);
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		header.add("序号");
		header.add("发货仓");
		header.add("快递公司");
		header.add("省");
		header.add("市");
		header.add("区");
		header.add("乡/镇/街");
		header.add("产品线");
		header.add("妥投及时率");
		header.add("妥投平均耗时");
		header.add("平均投递耗时");
		header.add("24小时妥投率");
		header.add("48小时妥投率");
		header.add("72小时妥投率");
		header.add("正向在途超七天在途率");
		header.add("退货天数");
		header.add("退货整体周转天数");
			
		int size = header.size();
		if(listRows!=null && listRows.size()!=0){
			for(int i = 0;i<listRows.size();i++){
				HashMap map = (HashMap)listRows.get(i);
				ArrayList tmp = new ArrayList();
				tmp.add(i+1 +"");
				int jsCount = StringUtil.toInt(map.get("jsCount")+"");//订单妥投及时数量
				int ttCount = StringUtil.toInt(map.get("ttCount")+"");//订单妥投总数量
				float ttztime = StringUtil.toFloat(map.get("ttztime")+"");//妥投单量总时长
				float zxztime = StringUtil.toFloat(map.get("zxztime")+"");//正向单量总时长
				int fhCount = StringUtil.toInt(map.get("fhCount")+"");//发货总单量
				int count24 = StringUtil.toInt(map.get("count24")+"");//妥投时间为24小时的订单量
				int count48 = StringUtil.toInt(map.get("count48")+"");//妥投时间为48小时的订单量
				int count72 = StringUtil.toInt(map.get("count72")+"");//妥投时间为72小时的订单量
				int zxztCount = StringUtil.toInt(map.get("zxztCount")+"");//正向在途超过7天订单
				int thCount = StringUtil.toInt(map.get("thCount")+"");//退货单量
				float thTimes = StringUtil.toFloat(map.get("thDays")+"");//（退货入库时间-开始退回时间）总和
				float thzzTimes = StringUtil.toFloat(map.get("thzzTimes")+"");//（退货入库时间-发货完成时间）总和
				if(Arrays.asList(select).contains("1")){
					tmp.add(map.get("areaName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("7")){
					tmp.add(map.get("deliverName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("2")){
					tmp.add( map.get("provinceName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("3")){
					tmp.add(map.get("cityName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("4")){
					tmp.add(map.get("quName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("5")){
					String timeLevel = map.get("timeLevel")+"";
					if("1".equals(timeLevel)){
						tmp.add("区县");
					}else if("2".equals(timeLevel)){
						tmp.add("乡镇");
					}else if("3".equals(timeLevel)){
						tmp.add("村");
					}else{
						tmp.add(map.get(""));
					}
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("6")){
					tmp.add(map.get("productTypeName"));
				}else{
					tmp.add(map.get(""));
				}
				if(ttCount!=0){
					//妥投及时率=妥投及时订单（在派送时效内达成的订单）/妥投总单量
					tmp.add(Arith.div(jsCount*100,ttCount,2)+"%");
					//妥投平均耗时=妥投单量总时长/妥投单量
					tmp.add(Arith.div(ttztime,ttCount,2)+"");
				}else{
					tmp.add(0+"%");
					tmp.add(0+"");
				}
				if(fhCount!=0){
					//平均投递耗时=（订单正向状态终结-订单发货时间）的总耗时/该类型的总单量
					tmp.add(Arith.div(zxztime,fhCount,2)+"");
					//24小时妥投率=妥投时间为24小时的订单/发货总单量
					tmp.add(Arith.div(count24*100,fhCount,2)+"%");
					//48小时妥投率=妥投时间为48小时的订单/发货总单量
					tmp.add(Arith.div(count48*100,fhCount,2)+"%");
					//72小时妥投率=妥投时间为72小时的订单/发货总单量
					tmp.add(Arith.div(count72*100,fhCount,2)+"%");
					//正向在途超七天在途率=正向在途超过7天订单/总订单量
					tmp.add(Arith.div(zxztCount*100,fhCount,2)+"%");
					
				}else{
					tmp.add(0+"");
					tmp.add(0+"%");
					tmp.add(0+"%");
					tmp.add(0+"%");
					tmp.add(0+"%");
				}
				if(thCount!=0){
					//退货天数=（退货入库时间-开始退回时间）总和/退货单总量
					tmp.add(Arith.div(thTimes,thCount,2)+"");
					//退货整体周转天数=（退货入库时间-发货完成时间）总和/退货单总量
					tmp.add(Arith.div(thzzTimes,thCount,2)+"");
				}else{
					tmp.add(0+"");
					tmp.add(0+"");
				}
				bodies.add(tmp);
			}
		}	
		headers.add(header);
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
		excel.setColMergeCount(size);
        List<Integer> row  = new ArrayList<Integer>();
        List<Integer> col  = new ArrayList<Integer>();
        excel.setRow(row);
        excel.setCol(col);
        excel.buildListHeader(headers);
        excel.buildListBody(bodies);
        excel.exportToExcel(fileName, response, "");
	}
	/**
	 * 观察类指标查询
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getObservationEffectList")
	@ResponseBody
	public EasyuiDataGridJson getObservationEffectList(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		try {
			String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
			String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
			String condi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("condition")));
			StringBuffer condition = new StringBuffer();
			StringBuffer groupBy = new StringBuffer();
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("msg", "您还没有登录");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
//			if(user.getGroup() == null || !user.getGroup().isFlag(3065)){
//				request.setAttribute("msg", "您没有操作权限");
//				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
//				return null;
//			}
			if (startTime.equals("") || endTime.equals("")) {
				request.setAttribute("msg", "请选择时间范围！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if(condi.equals("")){
				List list = new ArrayList();
				datagrid.setRows(list);
				return datagrid;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and a.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			}
			String select[] = condi.split(",");
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" a.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" d.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" d.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" d.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" x.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "sum(if(m.buy_mode=0,1,0)) codCount,"//codCount cod单量
					+ "count(a.id) fhCount,"//发货量
					+ "sum(if(n.time_level in (2,3),1,0)) xcCount,"//乡村镇订单
					+ "sum(if(b.isems=1,1,0)) emsCount,"//ems订单
					+ "sum(if(b.isems=0,1,0)) noEmsCount,"//落地配订单
					+ "x.time_level timeLevel,j.name productTypeName,e.name provinceName,f.city cityName,g.area quName,h.street streetName,b.name deliverName,c.name areaName");
			
			paramMap.put("table", " audit_package a "
					+ "LEFT JOIN deliver_corp_info b ON a.deliver=b.id "  
					+ "LEFT JOIN stock_area c ON a.areano=c.id  "
					+ "LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code  "
					+ "LEFT JOIN provinces e ON d.add_id1=e.id  "
					+ "LEFT JOIN province_city f ON d.add_id2=f.id  "
					+ "LEFT JOIN city_area g ON d.add_id3=g.id  "
					+ "LEFT JOIN area_street h ON d.add_id4=h.id  "
					+ "LEFT JOIN user_order m ON m.id=a.order_id  "
					+ "LEFT JOIN effect_order_info n ON n.order_id=m.id "
					+ "LEFT JOIN user_order_type j ON j.type_id=m.order_type"
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			
			paramMap.put("condition", "1=1 "+condition.toString()+"  limit "+(page.getPage()-1) * page.getRows()+","+page.getRows());
			
			List listRows = commonMapper.getCommonInfo(paramMap);
			
			
			
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
					int codCount = StringUtil.toInt(map.get("codCount")+"");//cod单量
					int fhCount = StringUtil.toInt(map.get("fhCount")+"");//发货总单量
					int xcCount = StringUtil.toInt(map.get("xcCount")+"");//乡村镇订单
					int emsCount = StringUtil.toInt(map.get("emsCount")+"");//ems订单
					int noEmsCount = StringUtil.toInt(map.get("noEmsCount")+"");//落地配订单
					
					if(condi.contains("1")){
						map.put("cang", map.get("areaName"));
					}
					if(condi.contains("2")){
						map.put("sheng", map.get("provinceName"));
					}
					if(condi.contains("3")){
						map.put("shi", map.get("cityName"));
					}
					if(condi.contains("4")){
						map.put("qu", map.get("quName"));
					}
					if(condi.contains("5")){
						String timeLevel = map.get("timeLevel")+"";
						if("1".equals(timeLevel)){
							map.put("timeLevels", "区县");
						}else if("2".equals(timeLevel)){
							map.put("timeLevels", "乡镇");
						}else if("3".equals(timeLevel)){
							map.put("timeLevels", "村");
						}
					}
					if(condi.contains("6")){
						map.put("productline", map.get("productTypeName"));
					}
					if(fhCount!=0){
						//COD订单占比
						map.put("codper", Arith.div(codCount*100,fhCount,2)+"%");
						//乡村镇订单占比
						map.put("xcper", Arith.div(xcCount*100,fhCount,2)+"%");
						//邮政体系订单占比
						map.put("emsper", Arith.div(emsCount*100,fhCount,2)+"%");
						//落地配订单占比
						map.put("noEmsper", Arith.div(noEmsCount*100,fhCount,2)+"%");
					}else{
						map.put("codper",0+"%");
						map.put("xcper",0+"%");
						map.put("emsper",0+"%");
						map.put("noEmsper",0+"%");
					}
				}
			}
			datagrid.setRows(listRows);
			HashMap<String, String> paramMapCount = new HashMap<String, String>();
			paramMapCount.put("column", "count(a.id) fhCount");
			paramMapCount.put("table", " audit_package a "
					+ " LEFT JOIN deliver_corp_info b ON a.deliver=b.id "
					+ " LEFT JOIN stock_area c ON a.areano=c.id"
					+ " LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code"
					+ " LEFT JOIN provinces e ON d.add_id1=e.id"
					+ " LEFT JOIN province_city f ON d.add_id2=f.id"
					+ " LEFT JOIN city_area g ON d.add_id3=g.id"
					+ " LEFT JOIN area_street h ON d.add_id4=h.id"
					+ " LEFT JOIN user_order m ON m.id=a.order_id  "
					+ " LEFT JOIN effect_order_info n ON n.order_id=m.id "
					+ " LEFT JOIN user_order_type j ON j.type_id=m.order_type"
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"
					);
			paramMapCount.put("condition", "1=1 "+condition.toString());
			
			List<HashMap<String, Object>> count = commonMapper.getCommonInfoCount(paramMapCount);
			int total = 0;
			if (count != null && count.size() > 0 && count.get(0) != null) {
				total = count.size();
			}
			datagrid.setTotal((long)total);// 设置总记录数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	@RequestMapping("/getObservationEffectListExcel")
	public void getObservationEffectListExcel (HttpServletRequest request,HttpServletResponse response) throws Exception{
		List listRows = null;
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String select[] = request.getParameterValues("condition");
		StringBuffer condition = new StringBuffer();
		StringBuffer groupBy = new StringBuffer();
		try {
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null) {
				return;
			}
			UserGroupBean group= user.getGroup();
			if (!group.isFlag(3069)) {
				request.setAttribute("msg", "无此权限！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (startTime.equals("") || endTime.equals("")) {
				return;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;

			}
			if(select==null || select.length==0){
				request.setAttribute("msg", "请选择查询条件");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and a.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			}
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" a.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" d.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" d.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" d.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" x.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}else if("7".equals(select[i])){
						groupBy.append(" a.deliver,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "sum(if(m.buy_mode=0,1,0)) codCount,"//codCount cod单量
					+ "count(a.id) fhCount,"//发货量
					+ "sum(if(n.time_level in (2,3),1,0)) xcCount,"//乡村镇订单
					+ "sum(if(b.isems=1,1,0)) emsCount,"//ems订单
					+ "sum(if(b.isems=0,1,0)) noEmsCount,"//落地配订单
					+ "x.time_level timeLevel,j.name productTypeName,e.name provinceName,f.city cityName,g.area quName,h.street streetName,b.name deliverName,c.name areaName");
			
			paramMap.put("table", " audit_package a "
					+ "LEFT JOIN deliver_corp_info b ON a.deliver=b.id "  
					+ "LEFT JOIN stock_area c ON a.areano=c.id  "
					+ "LEFT JOIN user_order_extend_info d ON a.order_code=d.order_code  "
					+ "LEFT JOIN provinces e ON d.add_id1=e.id  "
					+ "LEFT JOIN province_city f ON d.add_id2=f.id  "
					+ "LEFT JOIN city_area g ON d.add_id3=g.id  "
					+ "LEFT JOIN area_street h ON d.add_id4=h.id  "
					+ "LEFT JOIN user_order m ON m.id=a.order_id  "
					+ "LEFT JOIN effect_order_info n ON n.order_id=m.id "
					+ "LEFT JOIN user_order_type j ON j.type_id=m.order_type"
					+ " LEFT JOIN effect_order_info x on x.order_id=m.id"

					);
			
			paramMap.put("condition", "1=1 "+condition.toString());
			
			listRows = commonMapper.getCommonInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = DateUtil.getNow().substring(0, 10);
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		header.add("序号");
		header.add("发货仓");
		header.add("快递公司");
		header.add("省");
		header.add("市");
		header.add("区");
		header.add("地址级别（乡，镇，村）");
		header.add("产品线");
		header.add("COD订单占比");
		header.add("乡村镇订单占比");
		header.add("邮政体系订单占比");
		header.add("落地配订单占比");
			
		int size = header.size();
		if(listRows!=null && listRows.size()!=0){
			for(int i = 0;i<listRows.size();i++){
				HashMap map = (HashMap)listRows.get(i);
				ArrayList tmp = new ArrayList();
				tmp.add(i+1 +"");
				int codCount = StringUtil.toInt(map.get("codCount")+"");//cod单量
				int fhCount = StringUtil.toInt(map.get("fhCount")+"");//发货总单量
				int xcCount = StringUtil.toInt(map.get("xcCount")+"");//乡村镇订单
				int emsCount = StringUtil.toInt(map.get("emsCount")+"");//ems订单
				int noEmsCount = StringUtil.toInt(map.get("noEmsCount")+"");//落地配订单
				if(Arrays.asList(select).contains("1")){
					tmp.add(map.get("areaName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("7")){
					tmp.add(map.get("deliverName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("2")){
					tmp.add( map.get("provinceName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("3")){
					tmp.add(map.get("cityName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("4")){
					tmp.add(map.get("quName"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("5")){
					String timeLevel = map.get("timeLevel")+"";
					if("1".equals(timeLevel)){
						tmp.add("区县");
					}else if("2".equals(timeLevel)){
						tmp.add("乡镇");
					}else if("3".equals(timeLevel)){
						tmp.add("村");
					}else{
						tmp.add(map.get(""));
					}
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("6")){
					tmp.add(map.get("productTypeName"));
				}else{
					tmp.add(map.get(""));
				}
				if(fhCount!=0){
					//COD订单占比
					tmp.add(Arith.div(codCount*100,fhCount,2)+"%");
					//乡村镇订单占比
					tmp.add(Arith.div(xcCount*100,fhCount,2)+"%");
					//邮政体系订单占比
					tmp.add(Arith.div(emsCount*100,fhCount,2)+"%");
					//落地配订单占比
					tmp.add(Arith.div(noEmsCount*100,fhCount,2)+"%");
				}else{
					tmp.add(0+"%");
					tmp.add(0+"%");
					tmp.add(0+"%");
					tmp.add(0+"%");
				}
				bodies.add(tmp);
			}
		}	
		headers.add(header);
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
		excel.setColMergeCount(size);
        List<Integer> row  = new ArrayList<Integer>();
        List<Integer> col  = new ArrayList<Integer>();
        excel.setRow(row);
        excel.setCol(col);
        excel.buildListHeader(headers);
        excel.buildListBody(bodies);
        excel.exportToExcel(fileName, response, "");
	}
	
	/**
	 * 客诉类指标查询
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getEffectCustomerComplaintRate")
	@ResponseBody
	public EasyuiDataGridJson getEffectCustomerComplaintRate(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		try {
			String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
			String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
			String condi = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("condition")));
			StringBuffer condition = new StringBuffer();
			StringBuffer groupBy = new StringBuffer();
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("msg", "您还没有登录");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			if (startTime.equals("") || endTime.equals("")) {
				request.setAttribute("msg", "请选择时间范围！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;

			}
			if(condi.equals("")){
				List list = new ArrayList();
				datagrid.setRows(list);
				return datagrid;
			}
			
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and ap.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59'");
			}
			String select[] = condi.split(",");
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" ap.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" uoei.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" uoei.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" uoei.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" eoi.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}else if("7".equals(select[i])){
						groupBy.append(" ap.deliver,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column","c.name cang,p.name provinces_name,pc.city city_name,ca.area city_area_name,eoi.time_level timeLevel,j.name catalog_name,dci.name deliver_name,count(DISTINCT(ap.order_id)) AS totalOrderCount,"
					+"count(DISTINCT(IF(cwo.first_type = 12 and cwo.second_type NOT IN (5, 7),cwuo.user_order_id,null))) AS zhengtiOrderCount,"
					+"count(DISTINCT(IF(cwo.second_type = 1, cwuo.user_order_id, NULL))) AS chaoqiOrderCount,"
					+"count(DISTINCT(IF(cwo.second_type = 2, cwuo.user_order_id, NULL))) AS yuanzeOrderCount,"
					+"count(DISTINCT(IF(cwo.second_type = 4, cwuo.user_order_id, NULL))) AS taiduOrderCount,"
					+"count(distinct(cwo.id)) as kesucount,"
					+"sum(TIMESTAMPDIFF(SECOND,ap.check_datetime,if(doi.add_time!=null,doi.add_time,now()))) kesutime");
					
					paramMap.put("table", " audit_package ap "
					+"LEFT JOIN stock_area c ON ap.areano=c.id "
					+" LEFT JOIN user_order_extend_info uoei ON ap.order_code = uoei.order_code "
					+" LEFT JOIN effect_order_info eoi on ap.order_id=eoi.order_id  "
					+"LEFT JOIN provinces p on uoei.add_id1=p.id "
					+"LEFT JOIN province_city pc on uoei.add_id2=pc.id "
					+"LEFT JOIN city_area ca on uoei.add_id3=ca.id "
					+"LEFT JOIN deliver_corp_info dci on ap.deliver=dci.id "
					+"LEFT JOIN call_work_user_order cwuo ON ap.order_id = cwuo.user_order_id "
					+"LEFT JOIN call_work_order cwo ON cwuo.call_work_id = cwo.id "
					+"AND cwo.first_type = 12 and cwo.second_type NOT IN (5, 7) "
					+"LEFT JOIN deliver_order dor on ap.order_id=dor.order_id "
					+"LEFT JOIN deliver_order_info doi on doi.deliver_id=dor.id and doi.deliver_state in (7,8) "
					+"LEFT JOIN user_order m ON m.id=ap.order_id "
					+"LEFT JOIN user_order_type j ON j.type_id=m.order_type "
					);
			
			paramMap.put("condition", "1=1 "+condition.toString()+"  limit "+(page.getPage()-1) * page.getRows()+","+page.getRows());
			List listRows = commonMapper.getCommonInfo(paramMap);
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
					int totalOrderCount = StringUtil.toInt(map.get("totalOrderCount")+"");
					int zhengtiOrderCount = StringUtil.toInt(map.get("zhengtiOrderCount")+"");
					int chaoqiOrderCount = StringUtil.toInt(map.get("chaoqiOrderCount")+"");
					int yuanzeOrderCount = StringUtil.toInt(map.get("yuanzeOrderCount")+"");
					int taiduOrderCount = StringUtil.toInt(map.get("taiduOrderCount")+"");
					int kesucount = StringUtil.toInt(map.get("kesucount")+"");
					int kesutime = StringUtil.toInt(map.get("kesutime")+"");//获取的是秒
					if(condi.contains("1")){
						map.put("cang", map.get("cang"));
					}
					if(condi.contains("2")){
						map.put("sheng", map.get("provinces_name"));
					}
					if(condi.contains("3")){
						map.put("shi", map.get("city_name"));
					}
					if(condi.contains("4")){
						map.put("qu", map.get("city_area_name"));
					}
					if(condi.contains("5")){
						String timeLevel = map.get("timeLevel")+"";
						if("1".equals(timeLevel)){
							map.put("timeLevels", "区县");
						}else if("2".equals(timeLevel)){
							map.put("timeLevels","乡镇");
						}else if("3".equals(timeLevel)){
							map.put("timeLevels", "村");
						}
					}
					if(condi.contains("6")){
						map.put("productline", map.get("catalog_name"));
					}
					if(condi.contains("7")){
						map.put("deliver", map.get("deliver_name"));
					}
					//if(StringUtil.checkNull(addressLevel).equals("6")){
					//}
					if(totalOrderCount!=0){
						map.put("zhengtiOrderCount", Arith.div(zhengtiOrderCount*100, totalOrderCount,2)+"%");
						map.put("chaoqiOrderCount" , Arith.div(chaoqiOrderCount*100, totalOrderCount,2)+"%");
						map.put("yuanzeOrderCount" , Arith.div(yuanzeOrderCount*100, totalOrderCount,2)+"%");
						map.put("taiduOrderCount"  , Arith.div(taiduOrderCount*100, totalOrderCount,2)+"%");
					}
					if(kesucount!=0){
						map.put("time"  , DateUtil.formatDiff(kesutime/kesucount));
					}
					
				}
			}
			datagrid.setRows(listRows);
			HashMap<String, String> paramMapCount = new HashMap<String, String>();
			paramMapCount.put("column","count(DISTINCT(ap.order_id)) AS totalOrderCount");
			paramMapCount.put("table", " audit_package ap "
					+" LEFT JOIN user_order_extend_info uoei ON ap.order_code = uoei.order_code "
					+" LEFT JOIN effect_order_info eoi on ap.order_id=eoi.order_id  "
					+"LEFT JOIN provinces p on uoei.add_id1=p.id "
					+"LEFT JOIN province_city pc on uoei.add_id2=pc.id "
					+"LEFT JOIN city_area ca on uoei.add_id3=ca.id "
					+"LEFT JOIN deliver_corp_info dci on ap.deliver=dci.id "
					+"LEFT JOIN call_work_user_order cwuo ON ap.order_id = cwuo.user_order_id "
					+"LEFT JOIN call_work_order cwo ON cwuo.call_work_id = cwo.id "
					+"AND cwo.first_type = 12 and cwo.second_type NOT IN (5, 7) "
					+"LEFT JOIN deliver_order dor on ap.order_id=dor.order_id "
					+"LEFT JOIN deliver_order_info doi on doi.deliver_id=dor.id and doi.deliver_state in (7,8) "
					+"LEFT JOIN user_order m ON m.id=ap.order_id "
					+"LEFT JOIN user_order_type j ON j.type_id=m.order_type "
					+"LEFT JOIN stock_area c ON ap.areano=c.id ");
			paramMapCount.put("condition", " 1=1 "+condition.toString());
			
			List<HashMap<String, Object>> count = commonMapper.getCommonInfoCount(paramMapCount);
			int total = 0;
			if (count != null && count.size() > 0 && count.get(0) != null) {
				total = count.size();
			}
			datagrid.setTotal((long)total);// 设置总记录数
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	@RequestMapping("/exportCustomerComplaintRateList")
	public void exportCustomerComplaintRateList (HttpServletRequest request,HttpServletResponse response) throws Exception{
		List listRows = null;
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String select[] = request.getParameterValues("condition");
		StringBuffer condition = new StringBuffer();
		StringBuffer groupBy = new StringBuffer();
		try {
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null) {
				return;
			}
			UserGroupBean group= user.getGroup();
			if (!group.isFlag(3069)) {
				request.setAttribute("msg", "无此权限！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (startTime.equals("") || endTime.equals("")) {
				return;

			}
			if (DateUtil.daysBetween(DateUtil.parseDate(endTime), DateUtil.parseDate(startTime)) > 30) {
				request.setAttribute("msg", "最多查询30天的信息！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;

			}
			if(select==null || select.length==0){
				request.setAttribute("msg", "请选择查询条件");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return;
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				 condition.append(" and ap.check_datetime between '" + startTime +" 00:00:00' and '"+endTime+" 23:59:59' ");
			}
			if(select!=null && select.length>0){
				groupBy.append(" group by");
				for(int i=0;i<select.length;i++){
					if("1".equals(select[i])){
						groupBy.append(" ap.areano,");
					}else if("2".equals(select[i])){
						groupBy.append(" uoei.add_id1,");
					}else if("3".equals(select[i])){
						groupBy.append(" uoei.add_id2,");
					}else if("4".equals(select[i])){
						groupBy.append(" uoei.add_id3,");
					}else if("5".equals(select[i])){
						groupBy.append(" eoi.time_level,");
					}else if("6".equals(select[i])){
						groupBy.append(" m.order_type,");
					}else if("7".equals(select[i])){
						groupBy.append(" ap.deliver,");
					}
				}
				if(groupBy.length()>9){
					condition.append(groupBy.toString().substring(0, groupBy.length()-1));
				}
			}
			
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column","c.name cang,p.name provinces_name,pc.city city_name,ca.area city_area_name,eoi.time_level timeLevel,j.name catalog_name,dci.name deliver_name,count(DISTINCT(ap.order_id)) AS totalOrderCount,"
					+"count(DISTINCT(IF(cwo.first_type = 12 and cwo.second_type NOT IN (5, 7),cwuo.user_order_id,null))) AS zhengtiOrderCount,"
					+"count(DISTINCT(IF(cwo.second_type = 1, cwuo.user_order_id, NULL))) AS chaoqiOrderCount,"
					+"count(DISTINCT(IF(cwo.second_type = 2, cwuo.user_order_id, NULL))) AS yuanzeOrderCount,"
					+"count(DISTINCT(IF(cwo.second_type = 4, cwuo.user_order_id, NULL))) AS taiduOrderCount,"
					+"count(distinct(cwo.id)) as kesucount,"
					+"sum(TIMESTAMPDIFF(SECOND,ap.check_datetime,if(doi.add_time!=null,doi.add_time,now()))) kesutime");
					
					paramMap.put("table", " audit_package ap "
					+" LEFT JOIN user_order_extend_info uoei ON ap.order_code = uoei.order_code "
					+" LEFT JOIN effect_order_info eoi on ap.order_id=eoi.order_id  "
					+"LEFT JOIN provinces p on uoei.add_id1=p.id "
					+"LEFT JOIN province_city pc on uoei.add_id2=pc.id "
					+"LEFT JOIN city_area ca on uoei.add_id3=ca.id "
					+"LEFT JOIN deliver_corp_info dci on ap.deliver=dci.id "
					+"LEFT JOIN call_work_user_order cwuo ON ap.order_id = cwuo.user_order_id "
					+"LEFT JOIN call_work_order cwo ON cwuo.call_work_id = cwo.id "
					+"AND cwo.first_type = 12 and cwo.second_type NOT IN (5, 7) "
					+"LEFT JOIN deliver_order dor on ap.order_id=dor.order_id "
					+"LEFT JOIN deliver_order_info doi on doi.deliver_id=dor.id and doi.deliver_state in (7,8) "
					+"LEFT JOIN user_order m ON m.id=ap.order_id "
					+"LEFT JOIN user_order_type j ON j.type_id=m.order_type "
					+"LEFT JOIN stock_area c ON ap.areano=c.id ");
			
			paramMap.put("condition", "1=1 "+condition.toString());
			
			listRows = commonMapper.getCommonInfo(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fileName = DateUtil.getNow().substring(0, 10);
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		header.add("序号");
		header.add("发货仓");
		header.add("快递公司");
		header.add("省");
		header.add("市");
		header.add("区");
		header.add("乡/镇/街");
		header.add("产品线");
		header.add("整体客诉率");
		header.add("超时客诉率");
		header.add("态度客诉率");
		header.add("原则客诉率");
		header.add("客诉平均处理时长");
			
		int size = header.size();
		if(listRows!=null && listRows.size()!=0){
			for(int i = 0;i<listRows.size();i++){
				HashMap map = (HashMap)listRows.get(i);
				ArrayList tmp = new ArrayList();
				tmp.add(i+1 +"");
				int totalOrderCount = StringUtil.toInt(map.get("totalOrderCount")+"");
				int zhengtiOrderCount = StringUtil.toInt(map.get("zhengtiOrderCount")+"");
				int chaoqiOrderCount = StringUtil.toInt(map.get("chaoqiOrderCount")+"");
				int yuanzeOrderCount = StringUtil.toInt(map.get("yuanzeOrderCount")+"");
				int taiduOrderCount = StringUtil.toInt(map.get("taiduOrderCount")+"");
				int kesucount = StringUtil.toInt(map.get("kesucount")+"");
				int kesutime = StringUtil.toInt(map.get("kesutime")+"");//获取的是秒
				if(Arrays.asList(select).contains("1")){
					tmp.add(map.get("cang"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("7")){
					tmp.add(map.get("deliver_name"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("2")){
					tmp.add( map.get("provinces_name"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("3")){
					tmp.add(map.get("city_name"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("4")){
					tmp.add(map.get("city_area_name"));
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("5")){
					String timeLevel = map.get("timeLevel")+"";
					if("1".equals(timeLevel)){
						tmp.add("区县");
					}else if("2".equals(timeLevel)){
						tmp.add("乡镇");
					}else if("3".equals(timeLevel)){
						tmp.add("村");
					}else{
						tmp.add(map.get(""));
					}
				}else{
					tmp.add(map.get(""));
				}
				if(Arrays.asList(select).contains("6")){
					tmp.add(map.get("catalog_name"));
				}else{
					tmp.add(map.get(""));
				}
				if(totalOrderCount!=0){
					tmp.add( Arith.div(zhengtiOrderCount*100, totalOrderCount,2)+"%");
					tmp.add( Arith.div(chaoqiOrderCount*100, totalOrderCount,2)+"%");
					tmp.add( Arith.div(yuanzeOrderCount*100, totalOrderCount,2)+"%");
					tmp.add( Arith.div(taiduOrderCount*100, totalOrderCount,2)+"%");
				}
				if(kesucount!=0){
					tmp.add( DateUtil.formatDiff(kesutime/kesucount)+"");
				}else{
					tmp.add(map.get(""));
				}
				bodies.add(tmp);
			}
		}	
		headers.add(header);
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
		excel.setColMergeCount(size);
        List<Integer> row  = new ArrayList<Integer>();
        List<Integer> col  = new ArrayList<Integer>();
        excel.setRow(row);
        excel.setCol(col);
        excel.buildListHeader(headers);
        excel.buildListBody(bodies);
        excel.exportToExcel(fileName, response, "");
	}
	
	@RequestMapping("/getProvicesByPOPId")
	@ResponseBody
	public List<Map<String,Object>> getProvicesByPOPId(HttpServletRequest request,HttpServletResponse response){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		int popId = StringUtil.toInt(request.getParameter("popId"));
		Map<String,Object> m1 = new HashMap<String,Object>();
		m1.put("id",0);
		m1.put("name","全部省");
		list.add(m1);
		list.addAll(deliveryService.getProvicesByPOPId(popId));
		return list;
	}
	
	@RequestMapping("/getCitysByProvinceId")
	@ResponseBody
	public List<Map<String,Object>> getCitysByProvinceId(HttpServletRequest request,HttpServletResponse response){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		int provinceId = StringUtil.toInt(request.getParameter("provinceId"));
		Map<String,Object> m1 = new HashMap<String,Object>();
		m1.put("id",0);
		m1.put("name","全部市");
		list.add(m1);
		list.addAll(deliveryService.getCitysByProvinceId(provinceId));
		return list;
	}
	
	@RequestMapping("/getDistrictsByCityId")
	@ResponseBody
	public List<Map<String,Object>> getDistrictsByCityId(HttpServletRequest request,HttpServletResponse response){
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		int cityId = StringUtil.toInt(request.getParameter("cityId"));
		Map<String,Object> m1 = new HashMap<String,Object>();
		m1.put("id",0);
		m1.put("name","全部区");
		list.add(m1);
		list.addAll(deliveryService.getDistrictsByCityId(cityId));
		return list;
	}
	
	
	
}
