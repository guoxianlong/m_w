package mmb.rec.oper.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.service.MWareService;
import mmb.delivery.domain.DeliverInfo;
import mmb.delivery.service.DeliveryService;
import mmb.hessian.ware.DeliverOrderInfoBean;
import mmb.rec.oper.service.OrderStockService;
import mmb.rec.oper.service.OuterOrderService;
import mmb.rec.oper.service.StockService;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.util.excel.ExportExcel;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.domain.order.ItemInfo;
import com.jd.open.api.sdk.domain.order.OrderInfo;
import com.jd.open.api.sdk.request.order.OrderFbpGetRequest;
import com.jd.open.api.sdk.response.order.OrderFbpGetResponse;

@Controller
@RequestMapping("/OrderStockController")
public class OrderStockController {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Autowired
	public OrderStockService orderStockService;
	@Autowired
	public StockService stockService;
	@Autowired
	public MWareService mWareService;
	@Resource
	public DeliveryService deliveryService;
	
	public static byte[] lock = new byte[0];
	/**
	 * @return 加载发货库地区comboBox
	 * @author syuf
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/getAreaComboBox")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAreaComboBox(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			ProductStockBean psBean = new ProductStockBean();
			@SuppressWarnings("unchecked")
			Map<Integer,String> areaMap = (HashMap<Integer, String>)psBean.stockoutAvailableAreaMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
			bean2.setId("");
			bean2.setText("请选择");
			comboBoxList.add(bean2);
			for(Map.Entry<Integer, String> entry : areaMap.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
			/**
			 * 以下为临时增加2个库地区，为查询需要，并非为发货仓
			 */
			EasyuiComBoBoxBean bean3 = new EasyuiComBoBoxBean();
			EasyuiComBoBoxBean bean4 = new EasyuiComBoBoxBean();
			bean3.setId("3");
			bean3.setText("增城");
			bean4.setId("0");
			bean4.setText("北京");
			comboBoxList.add(bean3);
			comboBoxList.add(bean4);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载权限遮罩库地区comboBox
	 * @author syuf
	 */
	@SuppressWarnings("static-access")
	@RequestMapping("/getDeptAreaComboBox")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeptAreaComboBox(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			if(areaList != null && areaList.size() > 0){
				ProductStockBean psBean = new ProductStockBean();
				comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
				for(String s : areaList){
					EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
					bean.setId(s);
					bean.setText(StringUtil.convertNull(psBean.getAreaName(StringUtil.toInt(s))));
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * 获得快递公司的Combox的方法 
	 * @return
	 */
	@RequestMapping("/getDeliverComboBox")
	@ResponseBody
	public String getDeliverJSON() {
		StringBuffer sb = new StringBuffer();
		Map map = voOrder.deliverMapAll;
		sb.append("[");
		Iterator itr = map.keySet().iterator();
		for (int i = 0 ; i < map.size(); i++) {
			String key = (String)itr.next();
			if (i == 0) {
				sb.append("{\"id\":\"").append(key).append("\",\"name\":\"").append((String)map.get(key)).append("\",\"selected\":true}");
			} else {
				sb.append("{\"id\":\"").append(key).append("\",\"name\":\"").append((String)map.get(key)).append("\"}");
			}
			if (i == map.size()-1) {
			} else {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
	/**
	 * @param easyuiPage 分页bean
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param area 库地区
	 * @return 生成datagrid
	 * @author syuf
	 */
	@RequestMapping("/getOrderStockQueryDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getOrderStockQueryDatagrid (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,String startTime,String endTime,String area) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			request.setAttribute("msg", "你没有登录！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(848)) {
			request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		try {
			if(startTime == null || "null".equals(startTime)){
				startTime = "";
			}
			if(endTime == null || "null".equals(endTime)){
				endTime = "";
			}
			if(area == null || "null".equals(area)){
				area = "";
			}
			StringBuffer condtion = new StringBuffer();
			if (!"".equals(area)) {
				condtion.append(" and a.stock_area=" + StringUtil.toSql(area));
			}else{
				if(areaList!=null && areaList.size()>0){
					condtion.append(" and a.stock_area=" + areaList.get(0));
				}else{
					condtion.append(" and a.stock_area=-1");
				}
			}
			if (!"".equals(startTime) && !"".equals(endTime)) {
				condtion.append(" and left(ap.check_datetime, 10) between '" + startTime + "' and '" + endTime + "'");
			}else{
				condtion.append(" and left(ap.check_datetime, 10) = '" + DateUtil.getNow().substring(0, 10) + "'");
			}
			int total = orderStockService.getOrderStockQueryCount(condtion.toString());
			datagridJson.setTotal((long)total );
			
			
			List<AuditPackageBean> auditPackageList = orderStockService.getOrderStockQueryList(condtion.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(), easyuiPage.getRows(), null);
			for(int i = 0; i < auditPackageList.size(); i++) {
				AuditPackageBean apBean = auditPackageList.get(i);
				apBean.setDeliverName((String)voOrder.deliverMapAll.get(apBean.getDeliver()));
			}
			datagridJson.setRows(auditPackageList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return datagridJson;
	}
	/**
	 * @param easyuiPage 分页bean
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param area	库地区
	 * @param orderCode	订单号
	 * @param status	状态
	 * @param flag 	标识是否查询全部处理中或红色处理中 暂未启用
	 * @return 订单出库 
	 * @author syuf
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getOrderStockDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getOrderStockDatagrid (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,String startTime,String endTime,String area,String baseCode,String status,String flag, String deliver) throws ServletException, IOException{
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		List<OrderStockBean> list = new ArrayList<OrderStockBean>();
		datagridJson.setRows(list);
		datagridJson.setTotal(0l);
		if(user == null) {
			datagridJson.setTip("你没有登录！");
			return datagridJson;
		}
		try {
			StringBuffer condition = new StringBuffer();
			condition.append(" status <> ");
			condition.append(OrderStockBean.STATUS4);
			if(!"".equals(StringUtil.convertNull(baseCode)) ) {
				if( baseCode.startsWith("CK") ) {
					condition.append(" and code='");
					condition.append(baseCode);
					condition.append("' ");
				} else {
					condition.append(" and order_code='");
					condition.append(baseCode);
					condition.append("' ");
				}
			} else {
				if( (!"".equals(StringUtil.checkNull(startTime)) && "".equals(StringUtil.checkNull(endTime)))||("".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime)))  ){
					datagridJson.setTip("请填写完整开始时间和结束时间！");
					return datagridJson;
				}
				if (!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))) {
					boolean moreThanThirtyDay = OrderStockService.isMoreThanThirtyDay(startTime, endTime);
					if( moreThanThirtyDay ) {
						datagridJson.setTip("查询时间段不能超过30天！");
						return datagridJson;
					}
					condition.append(" and create_datetime >='");
					condition.append(startTime);
					condition.append(" 00:00:00");
					condition.append("' and create_datetime <='");
					condition.append(endTime);
					condition.append(" 23:59:59");
					condition.append("' ");
				}
				if( !"-1".equals(StringUtil.checkNull(deliver)) && deliver != null) {
					condition.append(" and deliver = ");
					condition.append(deliver);
				}
				if (!"".equals(StringUtil.checkNull(status))) {
					condition.append(" and status = ");
					condition.append(status);
				}
			}
			if (!"".equals(StringUtil.checkNull(area))) {
				condition.append(" and stock_area = ");
				condition.append(area);
			}
			
			//总数
			int totalCount = orderStockService.getOrderStockCount(condition.toString());
			datagridJson.setTotal((long)totalCount);
			list = orderStockService.getOrderStockListSlave(condition.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(),easyuiPage.getRows(), "status, id desc");
		
			if( list != null && list.size() > 0 ) {
				for(OrderStockBean oper : list ) {
					oper.setStockAreaName(ProductStockBean.getAreaName(oper.getStockArea()));
					oper.setDeliverName(StringUtil.convertNull((String)voOrder.deliverMapAll.get(String.valueOf(oper.getDeliver()))));
					oper.setCreateDatetime(StringUtil.cutString(oper.getCreateDatetime(), 0, 19));
				}
			}
			datagridJson.setRows(list);
			datagridJson.setFooter(null);
		} finally {
		}
		return datagridJson;
	}
	/**
	 * 说明：判断订单出货地区的库存是否充足
	 * @param area 0:北库 1:广分  2:广速
	 */
	@SuppressWarnings("rawtypes")
	public boolean checkStockInArea(List orderProductList, int area) {
		if (orderProductList == null) {
			return false;
		}

		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(area, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}
	/**
	 * 说明：检查订单商品库存是否充足。
	 * 
	 * 参数及返回值说明：
	 * 
	 * 0表示两地库存都充足。/三地库存充足
	 * 
	 * 1表示北京库存充足。/北库库存充足/广分缺货/广速缺货
	 * 
	 * 2表示广东库存充足。/广分库存充足/北库缺货/广速缺货
	 * 
	 * 3表示两地库存都不足。/三地库存都不足
	 * 
	 * 4表示广速库存充足/北库缺货/广分缺货
	 * 
	 * 5广速缺货
	 * 
	 * 6广分缺货
	 * 
	 * 7北库缺货
	 */
	@SuppressWarnings("rawtypes")
	public int checkStock(List orderProductList) {
		if (orderProductList == null) {
			return 3;
		}

		Iterator itr = orderProductList.iterator();
		boolean zc = true;
		boolean gd = false; //2012-05-30  芳村停止发货，只增城发货
		boolean gs = false;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				zc = false;
			}
		}
		if (zc && gd && gs) {
			return 0;
		}
		if (zc && !gd && !gs) {
			return 1;
		}
		if (gd && !zc && !gs) {
			return 2;
		}
		if (gs && !zc && !gd) {
			return 4;
		}
		if (zc && gd && !gs){
			return 5;
		}
		if (zc && gs && !gd){
			return 6;
		}
		if (gd && gs && !zc){
			return 7;
		}
		return 3;
	}
	
	/**
	 * 配送状态查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDeliverOrderState")
	@ResponseBody
	public EasyuiDataGridJson getDeliverOrderState (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage) throws ServletException, IOException{
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		datagridJson.setRows(list);
		datagridJson.setTotal(0l);
		if(user == null) {
			datagridJson.setTip("你没有登录！");
			return datagridJson;
		}
		UserGroupBean group= user.getGroup();
		if (!group.isFlag(2161)) {
			datagridJson.setTip("你没有查询权限！");
			return datagridJson;
		}
		String orderCodes = StringUtil.convertNull(request.getParameter("orderCodes"));
		String startTime = StringUtil.convertNull(request.getParameter("startDate"));
		String endTime = StringUtil.convertNull(request.getParameter("endDate"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		int deliver = StringUtil.toInt(request.getParameter("deliver"));
		int deliverState = StringUtil.toInt(request.getParameter("deliverState"));
		int scanType = StringUtil.toInt(request.getParameter("scanType"));
		String[] orderCode = orderCodes.split("\n");
		DbOperation dbop = new DbOperation(DbOperation.DB_SLAVE);
		try {
			if (orderCodes.trim().equals("") && startTime.trim().equals("") && endTime.trim().equals("")) {
				return datagridJson;
			}
			StringBuffer condition = new StringBuffer();
			if (!orderCodes.trim().equals("")) {
				condition.append("(");
				if (scanType == 1) {
		        	for(int i = 0 ; i < orderCode.length ;i++ ){
		        		if(orderCode[i].trim().length()>0){
		        			condition.append(" ap.package_code='");
		        			condition.append(orderCode[i].trim().replaceAll("\n", ""));
		        			condition.append("' or ");
		        		}
		        	}
				} else {
	        		for(int i = 0 ; i < orderCode.length ;i++ ){
		        		if(orderCode[i].trim().length()>0){
		        			condition.append(" ap.order_code='");
		        			condition.append(orderCode[i].trim().replaceAll("\n", ""));
		        			condition.append("' or ");
		        		}
		        	}
	        	}
	        	if( condition.length() > 0 ){
	        		condition.replace(condition.length()-3, condition.length(), "");
	        	}
	        	condition.append(") ");
			}
			if( (!"".equals(StringUtil.checkNull(startTime)) && "".equals(StringUtil.checkNull(endTime)))||("".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime)))  ){
				datagridJson.setTip("请填写完整开始时间和结束时间！");
				return datagridJson;
			}
			if (!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))) {
				int days = DateUtil.getDaySub(startTime, endTime);
				if( days > 30 ) {
					datagridJson.setTip("查询时间段不能超过31天！");
					return datagridJson;
				}
				if (condition.length() > 0) {
					condition.append( " and ");
				}
				condition.append(" mb.transit_datetime >='");
				condition.append(startTime);
				condition.append(" 00:00:00");
				condition.append("' and mb.transit_datetime <='");
				condition.append(endTime);
				condition.append(" 23:59:59");
				condition.append("' ");
			}
			if(deliver != -1) {
				condition.append(" and dci.id = ");
				condition.append(deliver);
			}
			if (wareArea != -1) {
				condition.append(" and ap.areano = ");
				condition.append(wareArea);
			}
			if (deliverState != -1) {
				condition.append(" and do.deliver_state = ");
				condition.append(deliverState);
			}
			
			String countSql = "select count(ap.id) from " +
					"audit_package ap join deliver_order do on ap.order_id=do.order_id  " +
					"join deliver_corp_info dci on ap.deliver=dci.id " +
					"join mailing_batch_package mbp on ap.order_code=mbp.order_code  " +
					"join mailing_batch mb on mbp.mailing_batch_code=mb.code  " +
					"left join user_order_extend_info uoei on uoei.order_code=ap.order_code  where " 
					+ condition.toString();
			String sql = "select ap.areano area,"
					+"dci. NAME deliverName,"
					+"ap.package_code packageCode,"
					+"ap.order_code orderCode,"
					+"mb.transit_datetime transitDatetime,"
					+"do.deliver_state deliverState,"
					+"(select p.name from provinces p where p.Id=uoei.add_id1) province,"
					+"(select pc.city from province_city pc where pc.id=uoei.add_id2) city,"
					+"(select ca.area from city_area ca where ca.Id=uoei.add_id3) cityArea,"
					+"(select max(FROM_UNIXTIME(deliver_time/1000, '%Y-%m-%d %T')) deliverTime from deliver_order_info where deliver_id=do.id) deliverTime,"
					+"do.deliver_info deliverInfo from " +
					"audit_package ap join deliver_order do on ap.order_id=do.order_id  " +
					"join deliver_corp_info dci on ap.deliver=dci.id " +
					"join mailing_batch_package mbp on ap.order_code=mbp.order_code  " +
					"join mailing_batch mb on mbp.mailing_batch_code=mb.code  " +
					"left join user_order_extend_info uoei on uoei.order_code=ap.order_code  where " 
					+ condition.toString() 
					+ " limit " + (easyuiPage.getPage()-1)*easyuiPage.getRows() +"," + easyuiPage.getRows();
			int totalCount = 0;
			ResultSet rs = dbop.executeQuery(countSql);
			if (rs.next()) {
				totalCount = rs.getInt(1);
			}
			HashMap<String, String> map = new HashMap<String, String>();
			rs = dbop.executeQuery(sql);
			while (rs.next()) {
				map = new HashMap<String, String>();
				map.put("wareArea", ProductStockBean.areaMap.get(rs.getInt("area")));
				map.put("deliverName", rs.getString("deliverName"));
				map.put("packageCode", rs.getString("packageCode"));
				map.put("orderCode", rs.getString("orderCode"));
				map.put("transitDatetime", rs.getString("transitDatetime"));
				map.put("deliverStateName", DeliverOrderInfoBean.deliverStateMap.get(rs.getInt("deliverState")));
				map.put("deliverTime", rs.getString("deliverTime"));
				map.put("deliverInfo", rs.getString("deliverInfo"));
				map.put("province", rs.getString("province"));
				map.put("city", rs.getString("city"));
				map.put("cityArea", rs.getString("cityArea"));
				list.add(map);
			}
			rs.close();
		
			datagridJson.setTotal((long)totalCount);
			datagridJson.setRows(list);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			dbop.release();
		}
		return datagridJson;
	}
	
	/**
	 * POP配送状态查询,分页查询
	 * @return配送信息列表
	 * @author yaoliang 
	 * @create 2015年5月8日 上午11:06:50
	 */
	@RequestMapping("/getPOPDeliverInfoList")
	public ModelAndView getPOPDeliverInfoList (HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String, Object> model = new HashMap<String, Object>();
		List<DeliverInfo> deliverInfoList = new ArrayList<DeliverInfo>();
		try{
			voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			if(user == null){
				model.put("tip","你没有登录");
				return new ModelAndView("admin/orderStock/searchPOPDeliverInfoListData",model);
			}
			UserGroupBean group= user.getGroup();
			if (!group.isFlag(3614)) {
				model.put("tip","你没有查询权限");
				return new ModelAndView("admin/orderStock/searchPOPDeliverInfoListData",model);
			}
		
			String orderCodes = StringUtil.convertNull(request.getParameter("orderCodes"));
			int popId = StringUtil.toInt(request.getParameter("popId"));
			String startTime = StringUtil.convertNull(request.getParameter("startDate"));
			String endTime = StringUtil.convertNull(request.getParameter("endDate"));
			int storageId = StringUtil.toInt(request.getParameter("storageId"));
			int deliveryId = StringUtil.toInt(request.getParameter("deliveryId"));
			int deliverState = StringUtil.toInt(request.getParameter("deliverState"));
			int scanType = StringUtil.toInt(request.getParameter("scanType"));
			
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("orderCodes", orderCodes);
			paramMap.put("popId", popId);
			paramMap.put("startTime", startTime);
			paramMap.put("endTime", endTime);
			paramMap.put("storageId", storageId);
			paramMap.put("deliveryId", deliveryId);
			paramMap.put("deliverState", deliverState);
			paramMap.put("scanType", scanType);
			
			//导出
			int exportFlag = StringUtil.toInt(request.getParameter("exportFlag"));
			if(exportFlag ==1){
				deliveryService.exportPOPDeliverInfoList(request,response,paramMap);
				return null;
			}
			//过滤掉不是pop商家的单子
			if(!orderCodes.trim().equals("")){
				String[] orderCode = orderCodes.split("\r\n");
				String DeliverOrderCodes = deliveryService.getPOPDeliverOrderCode(StringUtils.join(orderCode,"','"),scanType);
				Set<String> set = new HashSet<String>(Arrays.asList(orderCode));
				Set<String> set2 = new HashSet<String>(Arrays.asList(DeliverOrderCodes.split(",")));
				set.removeAll(set2);
				model.put("notPOPCode", StringUtils.join(set, ","));
			}
			
			//列表数据
			deliverInfoList = deliveryService.getPOPDeliverInfoList(paramMap);
			
			model.put("deliverInfoList", deliverInfoList);
			model.put("dataLength", deliverInfoList.size());
		} catch(Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		}
		return new ModelAndView("admin/orderStock/searchPOPDeliverInfoListData",model);
	}
	
	/**
	 * 配送状态导出
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/exportDeliverOrderState")
	public void exportDeliverOrderState (HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			return;
		}
		UserGroupBean group= user.getGroup();
		if (!group.isFlag(2161)) {
			return;
		}
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		String orderCodes = StringUtil.convertNull(request.getParameter("orderCodes1"));
		String startTime = StringUtil.convertNull(request.getParameter("startDate1"));
		String endTime = StringUtil.convertNull(request.getParameter("endDate1"));
		int wareArea = StringUtil.toInt(request.getParameter("wareArea1"));
		int deliver = StringUtil.toInt(request.getParameter("deliver1"));
		int deliverState = StringUtil.toInt(request.getParameter("deliverState1"));
		int scanType = StringUtil.toInt(request.getParameter("scanType1"));
		String[] orderCode = orderCodes.split("\n");
		DbOperation dbop = new DbOperation(DbOperation.DB_SLAVE);
		try {
			StringBuffer condition = new StringBuffer();
			if (!orderCodes.trim().equals("")) {
				condition.append("(");
				if (scanType == 1) {
		        	for(int i = 0 ; i < orderCode.length ;i++ ){
		        		if(orderCode[i].trim().length()>0){
		        			condition.append(" ap.package_code='");
		        			condition.append(orderCode[i].trim().replaceAll("\n", ""));
		        			condition.append("' or ");
		        		}
		        	}
				} else {
	        		for(int i = 0 ; i < orderCode.length ;i++ ){
		        		if(orderCode[i].trim().length()>0){
		        			condition.append(" ap.order_code='");
		        			condition.append(orderCode[i].trim().replaceAll("\n", ""));
		        			condition.append("' or ");
		        		}
		        	}
	        	}
	        	if( condition.length() > 0 ){
	        		condition.replace(condition.length()-3, condition.length(), "");
	        	}
	        	condition.append(") ");
			}
			if (!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))) {
				if (condition.length() > 0) {
					condition.append( " and ");
				}
				condition.append(" mb.transit_datetime >='");
				condition.append(startTime);
				condition.append(" 00:00:00");
				condition.append("' and mb.transit_datetime <='");
				condition.append(endTime);
				condition.append(" 23:59:59");
				condition.append("' ");
			}
			if(deliver != -1) {
				condition.append(" and dci.id = ");
				condition.append(deliver);
			}
			if (wareArea != -1) {
				condition.append(" and ap.areano = ");
				condition.append(wareArea);
			}
			if (deliverState != -1) {
				condition.append(" and do.deliver_state = ");
				condition.append(deliverState);
			}
			if (condition.length() <= 0) {
				condition.append("1=2");
			}
			
			String sql = "select ap.areano area,"
					+"dci. NAME deliverName,"
					+"ap.package_code packageCode,"
					+"ap.order_code orderCode,"
					+"mb.transit_datetime transitDatetime,"
					+"(select p.name from provinces p where p.Id=uoei.add_id1) province,"
					+"(select pc.city from province_city pc where pc.id=uoei.add_id2) city,"
					+"(select ca.area from city_area ca where ca.Id=uoei.add_id3) cityArea,"					
					+"do.deliver_state deliverState,"
					+"(select max(FROM_UNIXTIME(deliver_time/1000, '%Y-%m-%d %T')) deliverTime from deliver_order_info where deliver_id=do.id) deliverTime,"
					+"do.deliver_info deliverInfo from " +
					"audit_package ap join deliver_order do on ap.order_id=do.order_id  " +
					"join deliver_corp_info dci on ap.deliver=dci.id " +
					"join mailing_batch_package mbp on ap.order_code=mbp.order_code  " +
					"join mailing_batch mb on mbp.mailing_batch_code=mb.code  " +
					"left join user_order_extend_info uoei on uoei.order_code=ap.order_code  where " 					
					+ condition.toString() ;
			HashMap<String, String> map = new HashMap<String, String>();
			ResultSet rs = dbop.executeQuery(sql);
			while (rs.next()) {
				map = new HashMap<String, String>();
				map.put("wareArea", ProductStockBean.areaMap.get(rs.getInt("area")));
				map.put("deliverName", rs.getString("deliverName"));
				map.put("packageCode", rs.getString("packageCode"));
				map.put("orderCode", rs.getString("orderCode"));
				map.put("transitDatetime", rs.getString("transitDatetime"));
				map.put("deliverStateName", DeliverOrderInfoBean.deliverStateMap.get(rs.getInt("deliverState")));
				map.put("deliverTime", rs.getString("deliverTime"));
				map.put("deliverInfo", rs.getString("deliverInfo"));
				map.put("province", rs.getString("province"));
				map.put("city", rs.getString("city"));
				map.put("cityArea", rs.getString("cityArea"));				
				list.add(map);
			}
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			dbop.release();
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
		header.add("包裹单号");
		header.add("订单号");
		header.add("发货时间");
		header.add("状态");
		header.add("节点时间");
		header.add("配送信息");
			
		int size = header.size();
			
		if (list != null && list.size() > 0) {
			int x = list.size();
			for (int i = 0; i < x; i++) {
				HashMap<String, String> map = list.get(i);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(i+1 +"");
				tmp.add(map.get("wareArea"));
				tmp.add(map.get("deliverName"));
				tmp.add(map.get("province"));
				tmp.add(map.get("city"));
				tmp.add(map.get("cityArea"));
				tmp.add(map.get("packageCode"));
				tmp.add(map.get("orderCode"));
				tmp.add(map.get("transitDatetime"));
				tmp.add(map.get("deliverStateName"));
				tmp.add(map.get("deliverTime"));
				tmp.add(map.get("deliverInfo"));
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
	
	/**
	 * 配送信息查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getDeliverOrderInfo")
	@ResponseBody
	public EasyuiDataGridJson getDeliverOrderInfo (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage) throws ServletException, IOException{
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		List<String> list = new ArrayList<String>();
		if(user == null) {
			datagridJson.setTip("你没有登录！");
			return datagridJson;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2162)) {
			datagridJson.setTip("你没有查询权限！");
			return datagridJson;
		}
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		String packageCode = StringUtil.convertNull(request.getParameter("packageCode"));
		DbOperation dbop = new DbOperation(DbOperation.DB_SLAVE);
		try {
			if (orderCode.trim().equals("") && packageCode.trim().equals("")) {
				datagridJson.setTip("你没有登录！");
				return datagridJson;
			}
			StringBuffer condition = new StringBuffer();
			condition.append(" 1=1 ");
			if (!orderCode.trim().equals("")) {
				condition.append(" and ap.order_code='").append(orderCode).append("' ");
			}
			if (!packageCode.trim().equals("")) {
				condition.append(" and ap.package_code='").append(packageCode).append("' ");
			}
			
			String titleSql = "select ap.areano,ap.package_code packageCode,"
					+"ROUND(IFNULL((select TIMESTAMPDIFF(MINUTE,"
					+ "(select mb.transit_datetime from mailing_batch_package mbp "
					+ "join mailing_batch mb on mbp.mailing_batch_code=mb.code where ap.order_code=mbp.order_code limit 1),"
					+"max(FROM_UNIXTIME(deliver_time/1000, '%Y-%m-%d %T'))) "
					+"from deliver_order_info where deliver_id=d.id)/60,0),2) currentTimeMinute," 
					+"ROUND(IFNULL((select e.time from effect_order_info e where e.order_id=ap.order_id limit 1),0),2) effectTime,"
					+"ap.order_code orderCode,d.deliver_state deliverState from "
					+"audit_package ap join deliver_order d on ap.order_id=d.order_id where "
					+ condition.toString();
			String sql = "select FROM_UNIXTIME(doi.deliver_time/1000, '%Y-%m-%d %T') deliverTime,doi.deliver_info deliverInfo from " +
					"audit_package ap join deliver_order do on ap.order_id=do.order_id join deliver_order_info doi on do.id=doi.deliver_id " +
					" where " 
					+ condition.toString() 
					+ " order by doi.id desc ";
			StringBuffer returnSB = new StringBuffer();
			ResultSet rs = dbop.executeQuery(titleSql);
			if (rs.next()) {
				returnSB.append("<b>");
				returnSB.append("包裹单号：");
				returnSB.append(rs.getString("packageCode"));
				returnSB.append("&nbsp;&nbsp;&nbsp;");
				returnSB.append("订单编号：");
				returnSB.append(rs.getString("orderCode"));
				returnSB.append("&nbsp;&nbsp;&nbsp;");
				returnSB.append("当前状态：");
				returnSB.append(DeliverOrderInfoBean.deliverStateMap.get(rs.getInt("deliverState")));
				returnSB.append("&nbsp;&nbsp;&nbsp;");
				returnSB.append("配送用时/标准时效：");
				BigDecimal currentTime = rs.getBigDecimal("currentTimeMinute");
				BigDecimal effectTime = rs.getBigDecimal("effectTime");
				BigDecimal oneDay = new BigDecimal("24");//将小时数转换成天数
				currentTime = currentTime.divide(oneDay,2,BigDecimal.ROUND_HALF_UP);
				effectTime = effectTime.divide(oneDay,2,BigDecimal.ROUND_HALF_UP);
				returnSB.append(currentTime.toString()+"/"+effectTime.toString()+" 天");
				returnSB.append("&nbsp;&nbsp;&nbsp;");
				returnSB.append("剩余时间：");
				returnSB.append(effectTime.subtract(currentTime).toString()+" 天");
				returnSB.append("&nbsp;&nbsp;&nbsp;");
				returnSB.append("是否超期：");
				if(effectTime.compareTo(new BigDecimal("0"))==1
						&&currentTime.compareTo(new BigDecimal("0"))==1
						&&currentTime.compareTo(effectTime)==1){
					returnSB.append("<font color=\"#ff0000\">是</font>");
				}else{
					returnSB.append("否");
				}
				returnSB.append("</b>");
			}
			list.add(returnSB.toString());
			rs = dbop.executeQuery(sql);
			while (rs.next()) {
				returnSB.setLength(0);
				returnSB.append(rs.getString("deliverTime"));
				returnSB.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				returnSB.append(rs.getString("deliverInfo"));
				list.add(returnSB.toString());
			}
			rs.close();
		
			datagridJson.setTotal((long) list.size());
			datagridJson.setRows(list);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			dbop.release();
		}
		return datagridJson;
	}
	
	/**
	 * 配送信息查询
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getPOPDeliverInfo")
	public ModelAndView getPOPDeliverInfo (HttpServletRequest request,HttpServletResponse response) throws IOException{
		Map<String, Object> model = new HashMap<String, Object>();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			model.put("tip","你没有登录");
			return new ModelAndView("admin/orderStock/searchPOPDeliverInfo",model);
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3615)) {
			model.put("tip","你没有查询权限");
			return new ModelAndView("admin/orderStock/searchPOPDeliverInfo",model);
		}
		
		String deliverCode = StringUtil.convertNull(request.getParameter("deliverCode"));
		String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
		String popOrderCode = StringUtil.convertNull(request.getParameter("popOrderCode"));
		
		model.put("deliverCode", deliverCode);
		model.put("orderCode", orderCode);
		model.put("popOrderCode", popOrderCode);
		try {
			if (deliverCode.trim().equals("")&&orderCode.trim().equals("")&&popOrderCode.trim().equals("")) {
				model.put("tip","请添加运单号或订单号");
				return new ModelAndView("admin/orderStock/searchPOPDeliverInfo",model);
			}
			String notPOPCode = "";
			String tempCode = "";
			//过滤掉不是pop商家的单子
			if(!deliverCode.equals("")){
				tempCode = deliveryService.getPOPDeliverOrderCode(deliverCode, DeliverInfo.SCAN_TYPE1);
				if(tempCode.equals("")){
					notPOPCode +=" "+ deliverCode;
				}
			}
			if(!orderCode.equals("")){
				tempCode = deliveryService.getPOPDeliverOrderCode(orderCode, DeliverInfo.SCAN_TYPE2);
				if(tempCode.equals("")){
					notPOPCode = orderCode;
				}
			}
			if(!popOrderCode.equals("")){
				tempCode = deliveryService.getPOPDeliverOrderCode(popOrderCode, DeliverInfo.SCAN_TYPE3);
				if(tempCode.equals("")){
					notPOPCode +=" "+ popOrderCode;
				}
			}
			if(!notPOPCode.equals("")){
				model.put("tip",notPOPCode+"不是POP商家的信息");
				return new ModelAndView("admin/orderStock/searchPOPDeliverInfo",model);
			}
			
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("deliverCode", deliverCode);
			paramMap.put("orderCode", orderCode);
			paramMap.put("popOrderCode", popOrderCode);
			
			List<DeliverInfo> deliverInfoList = deliveryService.getPOPDeliverInfo(paramMap);
			
			if(deliverInfoList.isEmpty()){
				model.put("tip","您输入的信息有误或非POP商家的信息");
				return new ModelAndView("admin/orderStock/searchPOPDeliverInfo",model);
			}else{
				DeliverInfo deliverInfo = deliverInfoList.get(0);
				model.put("deliverInfo",deliverInfo);
				model.put("list",deliverInfoList);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		}  
		return new ModelAndView("admin/orderStock/searchPOPDeliverInfo",model);
	}
	
	/**
	 * 地区发货优先级列表
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getAreaDeliveryPriorityList")
	@ResponseBody
	public EasyuiDataGridJson getAreaDeliveryPriorityList (HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		if(user == null) {
			datagridJson.setTip("你没有登录！");
			return datagridJson;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2182)) {
			datagridJson.setTip("你没有查询权限！");
			return datagridJson;
		}
		try {
			HashMap<String, String> areaDeliverPriorityMap = OrderStockBean.areaDeliverPriorityMap;
			List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = null;
			for (String key : areaDeliverPriorityMap.keySet()) {
				map = new HashMap<String, String>();
				String[] areaArray = areaDeliverPriorityMap.get(key).split(",");
				StringBuffer sb = new StringBuffer();
				int len = areaArray.length;
				for (int i = 0; i < len; i ++) {
					int areaInt = StringUtil.toInt(areaArray[i]);
					if (sb.length() > 0) {
						sb.append(",");
					}
					sb.append(ProductStockBean.areaMap.get(areaInt));
				}
				map.put("province", key);
				map.put("priority", sb.toString());
				list.add(map);
			}
		
			datagridJson.setTotal((long) list.size());
			datagridJson.setRows(list);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
		}
		return datagridJson;
	}
	
	/**
	 * 更新地区发货优先级
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/saveAreaDeliveryPriority")
	@ResponseBody
	public Json saveAreaDeliveryPriority (HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			j.setMsg("你没有登录！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2182)) {
			j.setMsg("你没有修改权限！");
			return j;
		}
		
		String provinces = StringUtil.convertNull(request.getParameter("provinces"));
		String areas = StringUtil.convertNull(request.getParameter("areas"));
		try {
			if (provinces.trim().equals("") || areas.trim().equals("")) {
				j.setMsg("参数错误！");
				return j;
			}
			j = orderStockService.saveAreaDeliveryPriority(provinces, areas);
		} catch(Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
		return j;
	}
	/**
	 * 第三方来源异常单列表
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/getOuterAbnormalInfo")
	@ResponseBody
	public EasyuiDataGridJson getOuterAbnormalInfo(HttpServletRequest request,HttpServletResponse response,EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		EasyuiDataGridJson easyuiDataGridJson = orderStockService.getOuterAbnormalList(request, easyuiDataGrid);
		return easyuiDataGridJson;
	}
	public void returnErrJsp(HttpServletRequest request,HttpServletResponse response, Json j) throws ServletException, IOException {
		request.setAttribute("msg", j.getMsg());
		request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	}
	
	/**
	 * 添加京东3C订单
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/addOuterOrder")
	@ResponseBody
	public Json addOuterOrder (HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			j.setMsg("你没有登录！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2201)) {
			j.setMsg("你没有给生成订单权限！");
			return j;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		DbOperation dbOpSlave = new DbOperation();
		dbOpSlave.init(DbOperation.DB_SLAVE);
		int id = StringUtil.StringToId(request.getParameter("id"));
		String outerOrderCode = StringUtil.convertNull(request.getParameter("outerOrderCode"));
		String remark = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("remark")));
		int before = StringUtil.toInt(StringUtil.convertNull(request.getParameter("before")));
		int after = StringUtil.toInt(StringUtil.convertNull(request.getParameter("after")));
		OuterOrderService osService = new OuterOrderService();
		try {
			synchronized (lock) {
				dbOp.startTransaction();
				if (outerOrderCode.trim().equals("")) {
					dbOp.rollbackTransaction();
					j.setMsg("单据编号为空！");
					return j;
				}
				int count = 0;
				ResultSet rs = dbOp.executeQuery("select count(*) from mmb_outer_relation where outer_swap_order_code='" + outerOrderCode + "'"); 
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count > 0) {
					dbOp.rollbackTransaction();
					j.setMsg("该单据已生成过订单！");
					return j;
				}
				JdClient client=new DefaultJdClient(OrderStockBean.SERVER_URL,OrderStockBean.accessToken,OrderStockBean.appKey,OrderStockBean.appSecret); 
				OrderFbpGetRequest fbprequest=new OrderFbpGetRequest();
				fbprequest.setOrderId( outerOrderCode );
				fbprequest.setOptionalFields( OuterOrderService.optionalFields );
				OrderFbpGetResponse fbpresponse=client.execute(fbprequest);
				OrderInfo oi = fbpresponse.getOrderDetailInfo().getOrderInfo();
				if (oi == null) {
					dbOp.rollbackTransaction();
					j.setMsg("该订单不存在！");
					return j;
				}
				String result = osService.addOuterOrderInfo(group, oi, dbOp, dbOpSlave, before, after);
				if (result != null) {
					dbOp.rollbackTransaction();
					j.setMsg(result);
					return j;
				}
				if (!dbOp.executeUpdate("update outer_abnormal_info set handle_time='" + DateUtil.getNow() + "',handle_user_id=" + user.getId() + ",handle_user_name='" + user.getUsername() + "',status=2,remark='" + remark + "' where id=" +id)) {
					dbOp.rollbackTransaction();
					j.setMsg("更新失败！");
					return j;
				}
				j.setSuccess(true);
				dbOp.commitTransaction();
			}
		} catch(Exception e) {
			e.printStackTrace();
			dbOp.rollbackTransaction();
			j.setMsg(e.getMessage());
			return j;
		} finally {
			dbOp.release();
			dbOpSlave.release();
		}
		return j;
	}
	
	/**
	 * 添加京东3C订单部分判断
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping("/ajaxJudgeAddOuterOrder")
	@ResponseBody
	public Json ajaxJudgeAddOuterOrder (HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			j.setMsg("你没有登录！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(2201)) {
			j.setMsg("你没有生成订单权限！");
			return j;
		}
		DbOperation dbOpSlave = new DbOperation();
		dbOpSlave.init(DbOperation.DB_SLAVE);
		String abnormalouterOrderCode = StringUtil.convertNull(request.getParameter("outerOrderCode"));
		OuterOrderService osService = new OuterOrderService();
		try {
			synchronized (lock) {
				if (abnormalouterOrderCode.trim().equals("")) {
					j.setMsg("单据编号为空！");
					return j;
				}
				int count = 0;
				ResultSet rs = dbOpSlave.executeQuery("select count(*) from mmb_outer_relation where outer_swap_order_code='" + abnormalouterOrderCode + "'"); 
				if (rs.next()) {
					count = rs.getInt(1);
				}
				if (count > 0) {
					j.setMsg("该单据已生成过订单！");
					return j;
				}
				JdClient client=new DefaultJdClient(OrderStockBean.SERVER_URL,OrderStockBean.accessToken,OrderStockBean.appKey,OrderStockBean.appSecret); 
				OrderFbpGetRequest fbprequest=new OrderFbpGetRequest();
				fbprequest.setOrderId( abnormalouterOrderCode );
				fbprequest.setOptionalFields( OuterOrderService.optionalFields );
				OrderFbpGetResponse fbpresponse=client.execute(fbprequest);
				OrderInfo osInfo = fbpresponse.getOrderDetailInfo().getOrderInfo();
				if (osInfo == null) {
					j.setMsg("该订单不存在！");
					return j;
				}
				//销售订单
	        	if (osInfo.getReturnOrder().equals("0")) {
	        		
	        	} else if (osInfo.getReturnOrder().equals("1") || osInfo.getReturnOrder().equals("2")) {
	        		//换货单
	        		String remark = osInfo.getOrderRemark();
	        		
	        		String result = "";  
	                Pattern pattern = Pattern.compile("原订单号:[0-9]{0,}");  
	                Matcher matcher = pattern.matcher(remark);  
	                if (matcher.find()) {  
	                    result = matcher.group(0);//只取第一组  
	                }
	                String outerOrderCode = "";
	        		rs = dbOpSlave.executeQuery("select outer_sale_order_code from mmb_outer_relation where outer_swap_order_code='" + result.split(":")[1] + "'"); 
					if (rs.next()) {
						outerOrderCode = rs.getString(1);
					}
					if (outerOrderCode.equals("")) {
						rs.close();
						j.setMsg("没有找到原销售单！");
						return j;
					}
					rs.close();
					fbprequest.setOrderId( outerOrderCode );
					fbprequest.setOptionalFields( OuterOrderService.optionalFields );
					fbpresponse=client.execute(fbprequest);
					if (fbpresponse == null) {
						j.setMsg("京东订单get为空！");
						return j;
					}
					
					if (fbpresponse.getOrderDetailInfo() == null) {
						j.setMsg("京东订单search的OrderDetailInfo为空");
						return j;
					}
					OrderInfo oi = fbpresponse.getOrderDetailInfo().getOrderInfo();
					if (oi == null) {
						j.setMsg("该订单不存在！");
						return j;
					}
					List<ItemInfo> swapitemList = osInfo.getItemInfoList();
					List<ItemInfo> outeritemList = oi.getItemInfoList();
					//换货单是否存在原销售单没有的商品
					boolean flag = osService.parentNoSku(swapitemList, outeritemList);
					if (flag) {
						if (!group.isFlag(2212)) {
							j.setMsg("订单中存在与原销售单不存在商品，你没有输入商品价格权限！");
							return j;
						} else {
							j.setSuccess(true);
							j.setObj("输入价格");
							return j;
						}
					}
	        	} else {
	        		j.setMsg("京东订单类型错误，return_order为" + osInfo.getReturnOrder());
					return j;
	        	}
				j.setSuccess(true);
			}
		} catch(Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		} finally {
			dbOpSlave.release();
		}
		return j;
	}
}
