package mmb.rec.oper.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.StockOperationAction;

import mmb.stock.stat.StatService;
import mmb.ware.WareService;
import net.sf.json.JSONArray;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInventoryBean;

import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;

import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockCardComparator;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 说明：报损报溢
 * 
 * @author 张晔
 *
 * 时间：2012.09.04
 */
@Controller
@RequestMapping("/ByBsController")
public class ByBsController {
	public static byte[] stockLock = new byte[0];
	public static byte[] bybsLock = new byte[0];
	public static Object lock = new Object();
	public static String noSession = "/admin/rec/oper/salesReturned/noSession.jsp";
	
	public static HashMap areaTypeStrings = new LinkedHashMap();
	static {
		HashMap map = new LinkedHashMap();
		map.put(0, "北库");
		map.put(1, "芳村");
		map.put(2, "广速");
		map.put(3, "增城");
		map.put(4, "无锡");
		areaTypeStrings.put("0", map);
		
		map = new LinkedHashMap();
		map.put(0, "北库");
		map.put(1, "芳村");
		map.put(3, "增城");
		map.put(4, "无锡");
		areaTypeStrings.put("1", map);
		
		map = new LinkedHashMap();
		map.put(0, "北库");
		map.put(1, "芳村");
		map.put(3, "增城");
		map.put(4, "无锡");
		areaTypeStrings.put("2", map);

		map = new LinkedHashMap();
		map.put(0, "北库");
		map.put(1, "芳村");
		map.put(3, "增城");
		map.put(4, "无锡");
		areaTypeStrings.put("3", map);

		
		map = new LinkedHashMap();
		map.put(0, "北库");
		map.put(1, "芳村");
		map.put(2, "广速");
		map.put(3, "增城");
		map.put(4, "无锡");
		areaTypeStrings.put("4", map);

		
		map = new LinkedHashMap();
		map.put(0, "北库");
		map.put(1, "芳村");
		map.put(2, "广速");
		map.put(3, "增城");
		map.put(4, "无锡");
		areaTypeStrings.put("5", map);

		map = new LinkedHashMap();
		map.put(0, "北库");
		map.put(1, "芳村");
		map.put(3, "增城");
		map.put(4, "无锡");
		areaTypeStrings.put("6", map);

		
		map = new LinkedHashMap();
		map.put(1, "芳村");
		map.put(4, "无锡");
		areaTypeStrings.put("9", map);
	}	
	/**
	 * 报损报溢页面的“库类型”
	 * 
	 */
	@RequestMapping("/getWarehouseTypeName")
	@ResponseBody
	public String getWarehouseTypeName(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		Map<Integer, String> map = ProductStockBean.stockTypeMap;
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("");
		bean.setText("全部");
		bean.setSelected(true);
		list.add(bean);
		for(Map.Entry<Integer, String> entry : map.entrySet()){
			if(entry.getKey() == ProductStockBean.STOCKTYPE_QUALITYTESTING || 
					entry.getKey() == ProductStockBean.STOCKTYPE_NIFFER
					||entry.getKey() == ProductStockBean.STOCKTYPE_AFTER_SALE
					||entry.getKey() == ProductStockBean.STOCKTYPE_CUSTOMER) {
				continue;
			} else {
				bean = new EasyuiComBoBoxBean();
				bean.setId("" + entry.getKey());
				bean.setText(entry.getValue());
				list.add(bean);
			}
		}
		return JSONArray.fromObject(list).toString();
	}
	
	/**
	 * 报损报溢页面的“库地区”
	 * 
	 */
	@RequestMapping("/getWareAreaName")
	@ResponseBody
	public String getWareAreaName(HttpServletRequest request, HttpServletResponse response)throws Exception{
		response.setContentType("text/html; charset=utf-8");
		String wareHouseType = StringUtil.convertNull(request.getParameter("warehouseType"));
		Map<Integer, String> map = (Map<Integer, String>)areaTypeStrings.get(wareHouseType);
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("");
		bean.setText("全部");
		bean.setSelected(true);
		list.add(bean);
		if (map != null) {
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId("" + entry.getKey());
				bean.setText(entry.getValue());
				list.add(bean);
			}
		}
		return JSONArray.fromObject(list).toString();
	}
	
	
	/**
	 * 查询报损报溢单
	 */
	@RequestMapping("/searchBybsList")
	@ResponseBody
	public EasyuiDataGridJson searchBybsList(
			HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid)throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		
		String firstTime = StringUtil.convertNull(request.getParameter("firstTime"));
		if (firstTime.equals("first")) {
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(230)) {
				request.setAttribute("msg", "当前没有权限，操作失败！");
				request.getRequestDispatcher(noSession).forward(request, response);
				return null;
			}
		}
		String code = StringUtil.convertNull(request.getParameter("code"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String[] statuss = request.getParameterValues("status");
		String type = StringUtil.convertNull(request.getParameter("type"));
		String warehouseArea = StringUtil.convertNull(request.getParameter("warehouseArea"));
		String warehouseType = StringUtil.convertNull(request.getParameter("warehouseType"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String sourceCode = StringUtil.convertNull(request.getParameter("sourceCode"));
		String status = "";
		if(statuss!=null){
			for(int i=0;i<statuss.length;i++){
				status = status + statuss[i] +",";
			}
			if(status.endsWith(",")){
				status = status.substring(0, status.length()-1);
			}
		}
		String ids = "";
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		try{
			StringBuilder buff = new StringBuilder();
			if(!code.equals("")){
				buff.append("receipts_number = '"+code+"'");
				buff.append(" and ");
			}
			if(!startTime.equals("")&&!endTime.equals("")){
				startTime = startTime + " 00:00:00";
				endTime = endTime + " 23:59:59";
				buff.append("add_time between '"+startTime+"' and '"+endTime+"'");
				buff.append(" and ");
			}
			if(!status.equals("")){
				buff.append("current_type in ("+status+")");
				buff.append(" and ");
			}
			if(!type.equals("")){
				buff.append("type in ("+type+")");
				buff.append(" and ");
			}
			if(!warehouseArea.equals("")&&!warehouseArea.equals("-1")){
				buff.append("warehouse_area in ("+warehouseArea+")");
				buff.append(" and ");
			}
			if(!warehouseType.equals("")){
				buff.append("warehouse_type in ("+warehouseType+")");
				buff.append(" and ");
			}
			if(!sourceCode.equals("")){
				CargoInventoryBean inventory = cargoService.getCargoInventory("code = '"+sourceCode+"'");
				if(inventory!=null){
					buff.append("source =");
					buff.append(inventory.getId());
					buff.append(" and ");
				}else{
					buff.append("source =");
					buff.append(-1);
					buff.append(" and ");
				}
			}
			if(!(productName.equals("")&&productCode.equals(""))&&!ids.equals("-1,")){
				List idList = null;
				if(!ids.equals("")){
					if(ids.endsWith(",")){
						ids = ids.substring(0, ids.length()-1);
					}
					idList = service.getFieldList(
								"operation_id", "bsby_product", "product_id in (select id from product where"+" operation_id in ("+ids+")"+(productName.equals("")?"":" and oriname like '%"+productName+"%'")+(productCode.equals("")?"":" and code='"+productCode+"'")+")", -1, -1, "operation_id", "operation_id", "int");
					ids = "";
				}else{
					idList = service.getFieldList(
								"operation_id", "bsby_product", "product_id in (select id from product where 1=1"+(productName.equals("")?"":" and oriname like '%"+productName+"%'")+(productCode.equals("")?"":" and code='"+productCode+"'")+")", -1, -1, "operation_id", "operation_id", "int");
				}
				
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids = ids + id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}

			if(!ids.equals("")){
				ids = ids.substring(0, ids.length()-1);
				buff.append("id in ("+ids+")");
			}
			String condition = "if_del=0 and (warehouse_type <> " + ProductStockBean.STOCKTYPE_AFTER_SALE + " and warehouse_type <> " + ProductStockBean.STOCKTYPE_CUSTOMER + ")";
			if(buff.length() > 0){
				condition = condition + " and " + buff.toString();
				if(condition.endsWith(" and ")){
					condition = condition.substring(0,condition.lastIndexOf(" and "));
				}
			}
//			String sql = "select bo.receipts_number,bo.warehouse_type,bo.warehouse_area,bo.id,bo.add_time,bo.operator_name,bo.type,bo.end_time,bo.end_oper_name,(GROUP_CONCAT)ps.supplier_name as name from bsby_operationnote bo " +
//			"join product pt on pt .code =bo.id join product_supplier ps on ps.product_id=bo.id where "+condition+
//			" group by ps.supplier_name order by add_time desc limit 0, 50";
//	ResultSet rs = service.getDbOp().executeQuery(sql);
//	List bsbyOperationnoteList = new ArrayList();
//    while (rs.next()) {
//		BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
//		voProductSupplier productSupplierBean = new voProductSupplier();
//		bsbyOperationnoteBean.setReceipts_number(rs.getString("bo.receipts_number"));
//		bsbyOperationnoteBean.setWarehouse_area(rs.getInt("bo.warehouse_area"));
//		bsbyOperationnoteBean.setWarehouse_type(rs.getInt("bo.warehouse_type"));
//		bsbyOperationnoteBean.setId(rs.getInt("bo.id"));
//		bsbyOperationnoteBean.setAdd_time(rs.getString("bo.add_time"));
//		bsbyOperationnoteBean.setOperator_name(rs.getString("bo.operator_name"));
//		bsbyOperationnoteBean.setType(rs.getInt("bo.type"));
//		bsbyOperationnoteBean.setEnd_time(rs.getString("bo.end_time"));
//		bsbyOperationnoteBean.setEnd_oper_name(rs.getString("bo.end_oper_name"));
//		productSupplierBean.setSupplier_name(rs.getString("name"));
//		bsbyOperationnoteBean.setProductSupplierBean(productSupplierBean);
//		bsbyOperationnoteList.add(bsbyOperationnoteBean);
//	}
//	////////////////////////////////////////////////////////////////////
			// 分页显示所有的报溢报损的单据 状态是if_del=0 就是没有被删除的单据
			try {
				int totalCount = service.getByBsOperationnoteCount(condition);
				// 页码
				List list=null;
				if(StringUtil.convertNull(request.getParameter("excel")).equals("0")||StringUtil.convertNull(request.getParameter("excel")).equals("")){
					 list= service.getByBsOperationnoteList(condition, (easyuiDataGrid.getPage()-1) * easyuiDataGrid.getRows(),
							 easyuiDataGrid.getRows(), "add_time desc");
				}
				if(StringUtil.convertNull(request.getParameter("excel")).equals("1")){
					list= service.getByBsOperationnoteList(condition,-1,
							-1, "add_time desc");
					List alist=new ArrayList();
					Map map=null;
					if(list!=null&&list.size()>0){
						ResultSet rs=null;
						for(int i=0;i<list.size();i++){
							map=new HashMap();
							
							BsbyOperationnoteBean bean=(BsbyOperationnoteBean)list.get(i);
							String sqlcargocode="select count,whole_code from bsby_product_cargo " +
									" join cargo_info on cargo_id=cargo_info.id " +
									"where bsby_oper_id="+bean.getId();
							map.put("billCode", bean.getReceipts_number());
							map.put("stockType", bean.getWarehouse_type()+"");
							map.put("stockArea", bean.getWarehouse_area()+"");
							map.put("id", bean.getId()+"");
							map.put("userName", bean.getOperator_name());
							map.put("currentType", bean.getCurrent_type()+"");
							map.put("type", bean.getType()+"");
							int count=0;
							String wholeCode="";
							service.getDbOp().prepareStatement(sqlcargocode);
							rs=service.getDbOp().getPStmt().executeQuery();
							if(rs.next()){
								count=rs.getInt("count");
								wholeCode=rs.getString("whole_code");
							}
							map.put("count", count+"");
							map.put("wholeCode", wholeCode);
							alist.add(map);
							
							
						}
						request.setAttribute("alist", alist);
						
					}
				}
				
				for(int i=0;i<list.size();i++){
					BsbyOperationnoteBean bean = (BsbyOperationnoteBean)list.get(i);
					CargoInventoryBean inventory = cargoService.getCargoInventory("id = "+bean.getSource());
					if(inventory != null){
						bean.setSourceCode(inventory.getCode());
					}
					
					bean.setWarehouse_type_name((String)ProductStockBean.stockTypeMap.get(Integer.valueOf(bean.getWarehouse_type())));
					bean.setWarehouse_area_name((String)ProductStockBean.areaMap.get(Integer.valueOf(bean.getWarehouse_area())));
					BsbyProductBean bsbyProductBean = ByBsController.getProductByOperationId(bean.getId());
					if (bsbyProductBean == null) {
						bean.setProductCode("");
						bean.setOriname("");
					} else {
						bean.setProductCode(bsbyProductBean.getProduct_code());
						bean.setOriname(bsbyProductBean.getOriname());
					}
					bean.setCurrent_type_name((String)BsbyOperationnoteBean.current_typeMap.get(Integer.valueOf(bean.getCurrent_type())));
				}
				request.setAttribute("list", list);
				
				easyuiDataGridJson.setTotal((long)totalCount);
				easyuiDataGridJson.setRows(list);
		
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
			
		}finally{
			service.releaseAll();
		}
		if(StringUtil.convertNull(request.getParameter("excel")).equals("1")){
			request.getRequestDispatcher("/admin/rec/oper/bsby/excelBsBylist.jsp").forward(request, response);
			return null;
		}else{
			return easyuiDataGridJson;
		}
	}
	
	public static BsbyProductBean getProductByOperationId(int opid)
	{	
		IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService();
		try {
			BsbyProductBean bsbyProductBean = bsbyservice.getBsbyProductBean("operation_id="+opid);
			if (bsbyProductBean!=null) {
				return bsbyProductBean;
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			bsbyservice.releaseAll();
		}
		return null;
	}
	
	/**
	 * 点击“添加报损报溢单”，直接进入编辑和操作页，同时列表页生成一条报损或报溢单据记录，单子的初始状态为“处理中” 
	 */
	@RequestMapping("/add")
	@ResponseBody
	public void add(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/html; charset=utf-8");
		synchronized(lock){
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前没有登录，操作失败！\"}");
				return;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(230)) {
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前没有权限，操作失败！\"}");
				return;
			}
			int operationnoteType = StringUtil.StringToId(request.getParameter("operationnoteType"));
			String warehouse_type = StringUtil.convertNull(request.getParameter("warehouse_type"));
			String warehouse_area = StringUtil.convertNull(request.getParameter("warehouse_area"));
			if(warehouse_type.equals(""))
			{
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"请选择库类型！\"}");
				return;
			}
			if(warehouse_area.equals(""))
			{
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"请选择库区域！\"}");
				return;
			}
			if(!CargoDeptAreaService. hasCargoDeptArea(request, Integer.valueOf(warehouse_area), Integer.valueOf(warehouse_type))){
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"用户只能添加自己所属库地区和库类型的报损报溢单！\"}");
				return;
			}
			String receipts_number = "";
			String title = "";// 日志的内容
			int typeString = 0;
			if (operationnoteType == 0) {
				// 报损
				String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
				receipts_number = createCode(code);// BS+年月日+3位自动增长数
				title = "创建新的报损表" + receipts_number;
				typeString = 0;
			} else {
				String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
				receipts_number = createCode(code);// BY+年月日+3位自动增长数
				title = "创建新的报溢表" + receipts_number;
				typeString = 1;
			}
			String nowTime = DateUtil.getNow();
			BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
			bsbyOperationnoteBean.setAdd_time(nowTime);
			bsbyOperationnoteBean.setCurrent_type(0);
			bsbyOperationnoteBean.setOperator_id(user.getId());
			bsbyOperationnoteBean.setOperator_name(user.getUsername());
			bsbyOperationnoteBean.setReceipts_number(receipts_number);
			bsbyOperationnoteBean.setWarehouse_area(Integer.valueOf(warehouse_area));
			bsbyOperationnoteBean.setWarehouse_type(Integer.valueOf(warehouse_type));
			bsbyOperationnoteBean.setType(typeString);
			bsbyOperationnoteBean.setIf_del(0);
			bsbyOperationnoteBean.setFinAuditId(0);
			bsbyOperationnoteBean.setFinAuditName("");
			bsbyOperationnoteBean.setFinAuditRemark("");
			IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(
					IBaseService.CONN_IN_SERVICE, null);
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
			bsbyOperationnoteBean.setId(maxid + 1);
			try {
				service.getDbOp().startTransaction();
				boolean falg = service.addBsbyOperationnoteBean(bsbyOperationnoteBean);
				if (falg) {
					// 添加操作日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(nowTime);
					bsbyOperationRecordBean.setInformation(title);
					bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
					service.addBsbyOperationRecord(bsbyOperationRecordBean);
	
				} else {
					service.getDbOp().rollbackTransaction();
					response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加失败！\"}");
					return;
				}
				service.getDbOp().commitTransaction();
				response.getWriter().write("{\"result\":\"success\",\"tip\":\"添加成功！\",\"opid\":\""+bsbyOperationnoteBean.getId()+"\"}");
				return;
			} catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"添加失败！\"}");
				return;
			} finally {
				service.releaseAll();
			}
		}
	}
	
	// 产生报损或者报溢的编号
	public static String createCode(String code) {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0 and receipts_number like '" + code + "%'");
			BsbyOperationnoteBean plan;
			plan = service.getBuycode("receipts_number like '" + code + "%'");
			if (plan == null) {
				// 当日第一份计划，编号最后三位 001
				code += "0001";
			} else {
				// 获取当日计划编号最大值
				plan = service.getBuycode("id =" + maxid);
				String _code = plan.getReceipts_number();
				int number = Integer.parseInt(_code.substring(_code.length() - 4));
				number++;
				code += String.format("%04d", new Object[] { new Integer(number) });
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

		return code;
	}
	
	/**
	 * 添加成功后跳转后添加页面 将具体的信息传到具体添加页面
	 */
	@RequestMapping("/getByOpid")
	public void getByOpid(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		int opid = StringUtil.StringToId(request.getParameter("opid"));
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(
				IBaseService.CONN_IN_SERVICE, null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try {
			BsbyOperationnoteBean bsbyOperationnoteBean = service.getBsbyOperationnoteBean("id=" + opid);
			if(bsbyOperationnoteBean == null){
				request.setAttribute("msg", "该单据不存在！");
				request.getRequestDispatcher(noSession).forward(request, response);
				return;
			}
			request.setAttribute("bsbyOperationnoteBean", bsbyOperationnoteBean);
			// 单子的初始状态为“处理中”，对应操作为“提交审核”，提交后状态改为“审核中”，“审核中”的单据对应操作“通过审核”和“未通过审核”，未通过审核状态改为“审核未通过”，对应的操作为“提交审核”，通过审核状态改为“已完成”
			int current_type = bsbyOperationnoteBean.getCurrent_type();
			String buttonString = "";
			String buttonString1 = "";
			if (current_type == 0 || current_type == 2 || current_type == 5) {
				// 处理中
				buttonString = "提交审核";
				request.setAttribute("buttonString", buttonString);
				request.setAttribute("type", "1");
			} else if (current_type == 6) {
				buttonString = "通过审核";
				buttonString1 = "未通过审核";
				request.setAttribute("buttonString", buttonString);
				request.setAttribute("type", "3");
				request.setAttribute("buttonString1", buttonString1);
				request.setAttribute("type1", "2");
			}else if(current_type==1){
				buttonString = "通过财务审核";
				buttonString1 = "未通过财务审核";
				request.setAttribute("buttonString", buttonString);
				request.setAttribute("type", "6");
				request.setAttribute("buttonString1", buttonString1);
				request.setAttribute("type1", "5");
			}
			if (bsbyOperationnoteBean.getType() == 0) {
				request.setAttribute("title", "报损");
			} else {
				request.setAttribute("title", "报溢");
			}

			// 查询这个单据的所有产品
			List list = service.getBsbyProductList("operation_id=" + opid, -1, -1, null);
			for(int i=0;i<list.size();i++){
				BsbyProductBean bsbyProduct = (BsbyProductBean)list.get(i);
				BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProduct.getId());
				if(bsbyCargo != null){
					CargoProductStockBean cps = cargoService.getCargoProductStock("id = "+bsbyCargo.getCargoProductStockId());
					CargoInfoBean ci = cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId());
					bsbyCargo.setCps(cps);
					bsbyCargo.setCargoInfo(ci);
					bsbyProduct.setBsbyCargo(bsbyCargo);
				}
				
				int x = getProductCount(bsbyProduct.getProduct_id(),bsbyOperationnoteBean.getWarehouse_area(),bsbyOperationnoteBean.getWarehouse_type());
				if(bsbyOperationnoteBean.getType() == 0 && bsbyOperationnoteBean.getCurrent_type() != 0 && bsbyOperationnoteBean.getCurrent_type() != 2) {
					x = x + bsbyProduct.getBsby_count();
				}
				int y = updateProductCount(x,bsbyOperationnoteBean.getType(),bsbyProduct.getBsby_count()); 
				bsbyProduct.setBsby_before_after_count(x + "(" + y + ")");
				bsbyProduct.setWhole_code(bsbyProduct.getBsbyCargo()==null?"":bsbyProduct.getBsbyCargo().getCargoInfo()==null?"":bsbyProduct.getBsbyCargo().getCargoInfo().getWholeCode());
			}
			
			CargoInventoryBean inventory = cargoService.getCargoInventory("id = "+bsbyOperationnoteBean.getSource());
			if(inventory != null){
				bsbyOperationnoteBean.setSourceCode(inventory.getCode());
			}
			
			CargoInfoAreaBean area = cargoService.getCargoInfoArea("old_id = "+bsbyOperationnoteBean.getWarehouse_area());
			request.setAttribute("bsbyProductList", list);
			request.setAttribute("listCount", Integer.valueOf(list.size()));
			request.setAttribute("stockArea", area);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		String lookup = StringUtil.dealParam(request.getParameter("lookup"));
		if (lookup != null) {
			request.getRequestDispatcher("/admin/rec/oper/bsby/lookupbsby.jsp").forward(request, response);
			return;
		} else {
			request.getRequestDispatcher("/admin/rec/oper/bsby/bsbyedit.jsp").forward(request, response);
			return;
		}

	}
	
	/**
	 * 根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 2010-02-22
	 * 
	 * @param productCode
	 * @param area
	 * @param type
	 * @return
	 */
	public static int getProductCount(int productid, int area, int type) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service
				.getDbOp());
		int x = 0;
		try {
			voProduct product = wareService.getProduct(productid);
			product.setPsList(psService.getProductStockList("product_id=" + productid, -1, -1, null));
//			x = product.getStock(area, type) + product.getLockCount(area, type);
			x = product.getStock(area, type);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return x;

	}
	
	/**
	 * 得到报损或者报溢后的产品的数量 2010-02-22
	 * 
	 * @param x
	 * @param Type
	 * @return
	 */
	public static int updateProductCount(int x, int type, int count) {
		int result = 0;
		if (type == 0) {
			// 报损
			result = x - count;
		} else {
			result = x + count;
		}
		return result;
	}
	
	/**
	 * 添加报损报溢的商品
	 * 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/addByBsProduct")
	public void addByBsProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int opid = StringUtil.StringToId(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(230);
		if (!viewAll) {
			request.setAttribute("tip", "你无权操作");
			request.setAttribute("result", "failure");
			  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		}
		synchronized (stockLock) {
			String productCode = StringUtil.dealParam(request.getParameter("productCode"));
			String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
			if (StringUtil.convertNull(productCode).equals("")) {
				request.setAttribute("tip", "请输入产品编号！");
				request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			String planCountGD = request.getParameter("planCountGD");
			if (StringUtil.convertNull(planCountGD).equals("")) {
				request.setAttribute("tip", "请输入产品数量！");
				request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			if(cargoCode.equals("")){
				request.setAttribute("tip", "货位号不能为空！");
				request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE);
			WareService wareService = new WareService(dbOp);
			voProduct product = wareService.getProduct(productCode);
			wareService.releaseAll();

			if (product == null) {
				request.setAttribute("tip", "不存在这个编号的产品！");
				request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			if (product.getParentId1() == 106) {
				request.setAttribute("tip", "该商品为新商品，请先修改该产品的分类");
				request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			if (product.getIsPackage() == 1) {
				request.setAttribute("tip", "该产品为套装产品，不能添加！");
				request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,null);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				service.getDbOp().startTransaction();
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
				if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail  && bean.getCurrent_type()!=BsbyOperationnoteBean.fin_audit_Fail){
					request.setAttribute("tip", "单据已提交审核，无法修改！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
					return;
				}
				
				if (service.getBsbyProductBean("operation_id = " + opid + " and product_code = " + productCode) != null) {
					request.setAttribute("tip", "该产品已经添加，直接修改即可，不用重复添加！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
					return;
				}
				/**
				 * 打开两个页面,先后提交,每次提交时都要检查是否已经添加过商品了;
				 */
				if (service.getBsbyProductBean("operation_id = " + opid ) != null) {
					request.setAttribute("tip", "每个单据只能添加一个商品！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
					return;
				}
				BsbyOperationnoteBean ben = service.getBsbyOperationnoteBean("id=" + opid);
				int x = getProductCount(product.getId(), ben.getWarehouse_area(), ben.getWarehouse_type());
				int result = updateProductCount(x, ben.getType(), StringUtil.toInt(planCountGD));
				if (result < 0 ) {
					request.setAttribute("tip", "您所添加商品的库存不足！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
					return;
				}
				//新货位管理判断
				CargoProductStockBean cps = null;
				if(ben.getType()==0){
			        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
			        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+ben.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	request.setAttribute("tip", "货位号"+cargoCode+"无效，请重新输入！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			            return;
			        }
			        cps = (CargoProductStockBean)cpsOutList.get(0);
			        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cps.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
			        	request.setAttribute("tip", "合格库缓存区暂时不能进行报损报溢操作！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			            return;
			        }
			        if(StringUtil.toInt(planCountGD) > cps.getStockCount()){
			        	request.setAttribute("tip", "该货位"+cargoCode+"库存为" + cps.getStockCount() + "，库存不足！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			            return;
			        }
				}else{
					CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
					CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ben.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+cargoCode+"' and status <> "+CargoInfoBean.STATUS3);
			        if(cargo == null){
			        	request.setAttribute("tip", "货位号"+cargoCode+"无效，请重新输入！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			            return;
			        }
			        if(cargo.getStatus() == CargoInfoBean.STATUS2){
			        	request.setAttribute("tip", "货位"+cargoCode+"未开通，请重新输入！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			            return;
			        }
			        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cargo.getStoreType() == CargoInfoBean.STORE_TYPE2){
			        	request.setAttribute("tip", "合格库缓存区暂时不能进行报损报溢操作！");
			            request.setAttribute("result", "failure");
			            service.getDbOp().rollbackTransaction();
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			            return;
			        }
			        List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
			        if(cpsOutList == null || cpsOutList.size()==0){
			        	if(cargo.getStatus() == CargoInfoBean.STATUS0 && (cargo.getStoreType() == CargoInfoBean.STORE_TYPE0||cargo.getStoreType() == CargoInfoBean.STORE_TYPE4)){
			        		request.setAttribute("tip", "货位"+cargoCode+"被其他商品使用中，添加失败！");
				            request.setAttribute("result", "failure");
				            service.getDbOp().rollbackTransaction();
							  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				            return;
			        	}
			        	cps = new CargoProductStockBean();
			        	cps.setCargoId(cargo.getId());
			        	cps.setProductId(product.getId());
			        	cps.setStockCount(0);
			        	cps.setStockLockCount(0);
			        	if(!cargoService.addCargoProductStock(cps))
			        	{
			        	  service.getDbOp().rollbackTransaction();
			        	  request.setAttribute("tip", "数据库操作失败");
			        	  request.setAttribute("result", "failure");
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			        	  return;
			        	}
			        	cps.setId(cargoService.getDbOp().getLastInsertId());
			        	
			        	if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId()))
			        	{
			        	  service.getDbOp().rollbackTransaction();
			        	  request.setAttribute("tip", "数据库操作失败");
			        	  request.setAttribute("result", "failure");
						  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			        	  return;
			        	}
			        }else{
			        	cps = (CargoProductStockBean)cpsOutList.get(0);
			        }
				}
		        

				BsbyProductBean bsbyProductBean = new BsbyProductBean();
				bsbyProductBean.setBsby_count(StringUtil.toInt(planCountGD));
				bsbyProductBean.setOperation_id(opid);
				bsbyProductBean.setProduct_code(productCode);
				bsbyProductBean.setProduct_id(product.getId());
				bsbyProductBean.setProduct_name(product.getName());
				bsbyProductBean.setOriname(product.getOriname());
				bsbyProductBean.setAfter_change(result);
				bsbyProductBean.setBefore_change(x);
				boolean falg = service.addBsbyProduct(bsbyProductBean);
				if (!falg) {
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
					return;
				}
				BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
				bsbyCargo.setBsbyOperId(ben.getId());
				bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
				bsbyCargo.setCount(StringUtil.toInt(planCountGD));
				bsbyCargo.setCargoProductStockId(cps.getId());
				bsbyCargo.setCargoId(cps.getCargoId());
				if(!service.addBsbyProductCargo(bsbyCargo))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				  return;
				}
				// 添加日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("给单据:" + StringUtil.dealParam(request.getParameter("opcode"))
						+ "添加商品:" + productCode + "数量：" + planCountGD);
				bsbyOperationRecordBean.setOperation_id(opid);
				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				  return;
				}
				service.getDbOp().commitTransaction();
				request.setAttribute("tip", "修改成功！");
				request.setAttribute("result", "success");
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "异常！");
				request.setAttribute("result", "failure");
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			} finally {
				service.releaseAll();
			}

		}
	}
	
	/**
	 * 修改备注 
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/editRemark")
	public void editRemark(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = StringUtil.dealParam(request.getParameter("id"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
			return;
		}
		String remark = StringUtil.dealParam(request.getParameter("remark"));
		int   biaodantype = StringUtil.StringToId(request.getParameter("biaodantype"));
		String title = null;
		if(biaodantype==0)
		{
			title = "报损";
		}else {
			title = "报溢";
		}
		if ("".equals(remark)) {
			request.setAttribute("tip", title+"原因不能为空！");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
			return;
		}		

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			boolean falg = service.updateBsbyOperationnoteBean("remark='" + remark + "'", "id=" + id);
			if (falg) {
				request.setAttribute("opid", id);
				// 添加日志
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);

				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的"+title+"原因为:" + remark);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);

			} else {
				request.setAttribute("tip", "修改失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
				return;
			}
			service.getDbOp().commitTransaction();
			request.setAttribute("tip", "修改成功！");
			request.setAttribute("result", "success");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
			return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}
	
	/**
	 * 修改财务审核意见
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/updateFinAuditRemark")
	public void updateFinAuditRemark(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String id = StringUtil.dealParam(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
			return;
		}
		String finAuditRemark = StringUtil.dealParam(request.getParameter("finAuditRemark"));

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			//examineSuggestion=(new String(examineSuggestion.getBytes("iso8859-1"),"utf-8"));
			boolean falg = service.updateBsbyOperationnoteBean("fin_audit_remark='" + finAuditRemark + "'", "id=" + id);
			if (falg) {
				request.setAttribute("opid", id);
				// 添加日志
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);

				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的财务审核意见为:" + finAuditRemark);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
				  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
				  return;
				}
			} else {
				request.setAttribute("tip", "修改失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
				return;
			}
			service.getDbOp().commitTransaction();
			request.setAttribute("tip", "修改成功！");
			request.setAttribute("result", "success");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
			return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}
	
	/**
	 * 修改审核意见
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/updateExamineSuggestion")
	public void updateExamineSuggestion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String id = StringUtil.dealParam(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
			return;
		}
		String examineSuggestion = StringUtil.dealParam(request.getParameter("examineSuggestion"));

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			//examineSuggestion=(new String(examineSuggestion.getBytes("iso8859-1"),"utf-8"));
			boolean falg = service.updateBsbyOperationnoteBean("examineSuggestion='" + examineSuggestion + "'", "id=" + id);
			if (falg) {
				request.setAttribute("opid", id);
				// 添加日志
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);

				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的审核意见为:" + examineSuggestion);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);

			} else {
				request.setAttribute("tip", "修改失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
				return;
			}
			service.getDbOp().commitTransaction();
			request.setAttribute("tip", "修改成功！");
			request.setAttribute("result", "success");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+id).forward(request, response);
			return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}
	
	/**
	 * 更改状态
	 * @throws IOException 
	 * @throws ServletException 
	 * 
	 */
	@RequestMapping("/updateCurrentType")
	public void updateCurrentType(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		synchronized (bybsLock) {
			
		String id = StringUtil.dealParam(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
			return;
		}
		int biaodantype = StringUtil.StringToId(request.getParameter("biaodantype"));
		int type = StringUtil.toInt(request.getParameter("type"));
		int type1 = StringUtil.toInt(request.getParameter("type1"));
		if (type == -1 && type1 == -1) {
			// 没有点击更改状态

		} else {

			/*取消页面判断 只进行数据库判断 (陈丽华意见)2010-04-14 李青
			 * String remark = StringUtil.dealParam(request.getParameter("remark"));
			remark  = new String(remark.getBytes("ISO-8859-1"),"utf-8");


			if ("".equals(remark)) {
				request.setAttribute("tip", title+"原因不能为空！");
				request.setAttribute("result", "failure");
				return;
			}*/
			IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, null);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				List list = service.getBsbyProductList("operation_id=" + id, -1, -1, null);
				if(list.size()==0)
				{
					request.setAttribute("tip", "您还没有添加商品！");
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				boolean falg = false;
				String alert = null;
				BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id=" + id);
				if (bean.getType()==0) {
					alert="报损原因不能为空";
				}else {
					alert="报溢原因不能为空";
				}
				if(bean.getRemark()==null)
				{
					request.setAttribute("tip", alert);
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				if(bean.getCurrent_type()==type)
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(type))+"!");
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				//当前状态为已完成
				else if(bean.getCurrent_type()==4)
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(bean.getCurrent_type()))+"!");
					request.setAttribute("result", "failure");
					request.setAttribute("look", "look");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				//审核通过或审核不通过（状态必须是财务审核通过）
				else if(bean.getCurrent_type()!=6 && (type == 3 || type1 ==2))
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(bean.getCurrent_type()))+"!");
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				//财务审核通过或财务审核不通过（状态必须是审核中）
				else if(bean.getCurrent_type()!=1 && (type1 == 5 || type ==6))
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(bean.getCurrent_type()))+"!");
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				//提交审核（状态必须是处理中或财务审核未通过或审核未通过）
				else if(bean.getCurrent_type()!=0 && bean.getCurrent_type()!=2 && bean.getCurrent_type()!=5  && type == 1)
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(bean.getCurrent_type()))+"!");
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				else if(bean.getCurrent_type()==type1)
				{
					request.setAttribute("tip", "单据已经是"+(String) bean.current_typeMap.get(Integer.valueOf(type1))+"!");
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					return;
				}
				service.getDbOp().startTransaction();
				String zhuangtai = "";
				if (type != -1) {
					if (type==3) {//审核通过
						
						//添加审核人信息 不是最后的完成人 审核
//						service.updateBsbyOperationnoteBean("current_type=3 , end_time='"
//						+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
//						+ user.getUsername() + "'", "id=" + id);
//
//						BsbyProductBean bpb = service.getBsbyProductBean("operation_id="+id);
//
//						list = service.getBsbyProductList("operation_id=" + id, -1, -1, null);
//						if (list.size() != 0 && bean.getType() == 0) {
//							Iterator it = list.iterator();
//							for (; it.hasNext();) {
//								BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
//								int productId = bsbyProductBean.getProduct_id();
//								// 每一个单据中的产品 依次进行修改库存操作
//								String titleString = "";
//
//								voProduct product = adminService.getProduct(productId);
//								// 得到这个产品的所有库存的列表
//								product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
//
//								// 出库 报损就是出库
//
//
//								// 如果出库的量大于 商品所在库的库存 就提示
//								if (bsbyProductBean.getBsby_count() > product.getStock(bean.getWarehouse_area(), bean
//										.getWarehouse_type())) {
//									request.setAttribute("tip", "可用库存不足,操作失败！");
//									request.setAttribute("result", "failure");
//									return;
//								}
//							}
//						}

						if (bean!=null) {
							if(bean.getCurrent_type()==4)
							{
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "此单据已经完成！");
								request.setAttribute("result", "failure");
								request.setAttribute("look", "look");
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
							else {
								if(!updateStock(bean, request, response, service.getDbOp())){
									service.getDbOp().rollbackTransaction();
									request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
									return;
								}
								
								// 如果是改为已完成 就要添加审核人的信息
								if(!service.updateBsbyOperationnoteBean("current_type=4 , end_time='"
										+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
										+ user.getUsername() + "'", "id=" + id))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
									request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								  return;
								}
								/**
								 * 单据状态改为完成 就要改变库存 如果是报损就要剪掉库存 如果是报溢就要添加批次 如果没有调整的产品
								 * 就不执行这个方法
								 */
								BsbyProductBean bsbyProductBean =  service.getBsbyProductBean("operation_id="+id);
								int beforeChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());


								/**
								 * 更改为完成后,要将最后的库存和改变后的库存的量记录
								 */
								int afterChangeProductCount = getProductCount(bsbyProductBean.getProduct_id(), bean.getWarehouse_area(), bean.getWarehouse_type());


								if(!service.updateBsbyProductBean("before_change="+beforeChangeProductCount+", after_change="+afterChangeProductCount, "id="+bsbyProductBean.getId()))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
								  request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								  return;
								}
								// 添加日志
								BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
								bsbyOperationRecordBean.setOperator_id(user.getId());
								bsbyOperationRecordBean.setOperator_name(user.getUsername());
								bsbyOperationRecordBean.setTime(DateUtil.getNow());
								bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:已完成");
								bsbyOperationRecordBean.setOperation_id(bean.getId());
								if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
									request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								  return;
								}

							}

						}
						request.setAttribute("look", "look");

					}else if (type==6) {//财务审核通过
						
						if (bean!=null) {
							if(bean.getCurrent_type()==4)
							{   
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "此单据已经完成！");
								request.setAttribute("result", "failure");
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
							else if(bean.getFinAuditRemark()==null||bean.getFinAuditRemark().equals("")){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "财务审核意见不能为空！");
								request.setAttribute("result", "failure");
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
							else {
								if(!service.updateBsbyOperationnoteBean("current_type=6,fin_audit_datetime='"+DateUtil.getNow()+"',fin_audit_id="+user.getId()+",fin_audit_name='"+user.getUsername()+"'", "id=" + id))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
									request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								  return;
								}
								// 添加日志
								BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
								bsbyOperationRecordBean.setOperator_id(user.getId());
								bsbyOperationRecordBean.setOperator_name(user.getUsername());
								bsbyOperationRecordBean.setTime(DateUtil.getNow());
								bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:财务审核通过");
								bsbyOperationRecordBean.setOperation_id(bean.getId());
								if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
								{
								  service.getDbOp().rollbackTransaction();
								  request.setAttribute("tip", "数据库操作失败");
								  request.setAttribute("result", "failure");
									request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								  return;
								}

							}

						}
					}else{
						if (type == 1) {//报损单提交审核，锁定库存量
							//报损单中的所有产品
							List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
							Iterator it = bsbyList.iterator();
							if(bean.getType() == 0){
								for (; it.hasNext();) {
									BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
									BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
									if(bsbyCargo == null){
										request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}
									String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
									+ "area = " + bean.getWarehouse_area() + " and type = "
									+ bean.getWarehouse_type();
									ProductStockBean psBean = psService.getProductStock(sql);
									//减少库存
									if(!psService.updateProductStockCount(psBean.getId(), -bsbyProductBean.getBsby_count())){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}
									//增加库存锁定量
									if (!psService.updateProductLockCount(psBean.getId(), bsbyProductBean.getBsby_count())) {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}

									//锁定货位库存
									//出库
									if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
										request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}
									if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
										request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}
									
									
								}
							}else if(bean.getType() == 1){
								for (; it.hasNext();) {
									BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
									BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
									if(bsbyCargo == null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}

									//锁定货位空间
									if(cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId()+" and status = 0")==null){
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "目的货位不存在或已被清空，操作失败，请与管理员联系！");
										request.setAttribute("result", "failure");
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}
									if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count + "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
										request.setAttribute("tip", "操作失败");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
										return;
									}
								}
							}
						}
						falg = service.updateBsbyOperationnoteBean("current_type=" + type, "id=" + id);
						if(!falg)
						{
						  service.getDbOp().rollbackTransaction();
						  request.setAttribute("tip", "数据库操作失败");
						  request.setAttribute("result", "failure");
							request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
						  return;
						}
						zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type));
					}
				}else if(type1==2){
					falg = service.updateBsbyOperationnoteBean("current_type=2 , end_time='"
							+ DateUtil.getNow() + "' , end_oper_id=" + user.getId() + " , end_oper_name='"
							+ user.getUsername() + "'", "id=" + id);
					if(!falg)
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
						request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					  return;
					}
					zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type1));
					if (bean.getType() == 0) {//报损单审核未通过，操作库存锁定量
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
	    	                    return;
							}
							String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
										+ "area = " + bean.getWarehouse_area() + " and type = "
										+ bean.getWarehouse_type();
							ProductStockBean psBean = psService.getProductStock(sql);
							//增加库存
							if(!psService.updateProductStockCount(psBean.getId(), bsbyProductBean.getBsby_count())){
								service.getDbOp().rollbackTransaction();
	                        	request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
	    	                    return;
	                        }
							//减去库存锁定量
							if (!psService.updateProductLockCount(psBean.getId(), -bsbyProductBean.getBsby_count())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
			                    return;
							}
							
							//解锁货位库存
							if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
							if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}

							if(bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED){
								//更新订单缺货状态
								this.updateLackOrder(bsbyProductBean.getProduct_id());
							}
						}
					}else if(bean.getType() == 1){//报溢单审核未通过，解锁空间锁定值
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
	    	                    return;
							}
							
							if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count - "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
								request.setAttribute("tip", "操作失败");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
						}
					}
				}else if(type1==5){
					falg = service.updateBsbyOperationnoteBean("current_type=5 , fin_audit_datetime='"
							+ DateUtil.getNow() + "' , fin_audit_id=" + user.getId() + " , fin_audit_name='"
							+ user.getUsername() + "'", "id=" + id);
					if(!falg)
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
						request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					  return;
					}
					zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type1));
					if (bean.getType() == 0) {//报损单财务审核未通过，操作库存锁定量
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
	    	                    return;
							}
							String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
										+ "area = " + bean.getWarehouse_area() + " and type = "
										+ bean.getWarehouse_type();
							ProductStockBean psBean = psService.getProductStock(sql);
							//增加库存
							if(!psService.updateProductStockCount(psBean.getId(), bsbyProductBean.getBsby_count())){
								service.getDbOp().rollbackTransaction();
	                        	request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
	    	                    return;
	                        }
							//减去库存锁定量
							if (!psService.updateProductLockCount(psBean.getId(), -bsbyProductBean.getBsby_count())) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
			                    request.setAttribute("result", "failure");
			        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
			                    return;
							}
							
							//解锁货位库存
							if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
							if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
							
							if(bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED){
								//更新订单缺货状态
								this.updateLackOrder(bsbyProductBean.getProduct_id());
							}
						}
					}else if(bean.getType() == 1){//报溢单财务审核未通过，解锁空间锁定值
						//报损单中的所有产品
						List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
						Iterator it = bsbyList.iterator();
						for (; it.hasNext();) {
							BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
							BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
							if(bsbyCargo == null){
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "货位信息异常，操作失败，请与管理员联系！");
	    	                    request.setAttribute("result", "failure");
	    	        			request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
	    	                    return;
							}
							
							if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count - "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
								request.setAttribute("tip", "操作失败");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
								return;
							}
						}
					}
				}else {
					falg = service.updateBsbyOperationnoteBean("current_type=" + type1, "id=" + id);
					if(!falg)
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
						request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					  return;
					}
					zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(type1));
				}
				if (falg) {

					/*// 如果备注也被修改了 那就连同备注一起更新 
					if (bean.getRemark() != null && bean.getRemark().equals(remark)) { } else {
					  service.updateBsbyOperationnoteBean("remark='" + remark + "'", "id=" + id); // 添加日志
					  int logId = service.getNumber("id",
					  "bsby_operation_record", "max", "id > 0") + 1;
					  BsbyOperationRecordBean bsbyOperationRecordBean = new
					  BsbyOperationRecordBean();
					  bsbyOperationRecordBean.setId(logId);
					  bsbyOperationRecordBean.setOperator_id(user.getId());
					  bsbyOperationRecordBean.setOperator_name(user.getUsername());
					  bsbyOperationRecordBean.setTime(DateUtil.getNow());
					  bsbyOperationRecordBean.setInformation("修改单据:" +
					  bean.getReceipts_number() + "的备注为:" + remark);
					  bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(id));
					  service.addBsbyOperationRecord(bsbyOperationRecordBean); }
					 */
					request.setAttribute("opid", id);
					// 添加日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:" + zhuangtai);
					bsbyOperationRecordBean.setOperation_id(bean.getId());
					if(!service.addBsbyOperationRecord(bsbyOperationRecordBean))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
						request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
					  return;
					}
				}
				service.getDbOp().commitTransaction();
				request.setAttribute("tip", "操作成功！");
				request.setAttribute("result", "success");
				request.getRequestDispatcher("/admin/rec/oper/bsby/editStatus.jsp?opid="+id).forward(request, response);
				return;
			} catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}
		}
	}
	
	public static void updateLackOrder(int productId){
    	DbOperation dbOp = new DbOperation();
    	dbOp.init("adult_slave");
    	DbOperation dbOp2 = new DbOperation();
    	dbOp2.init();
    	WareService wareService = new WareService(dbOp);
    	IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
    	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
    	try{
    		String productIds = productId+"";
    		//查询父商品
    		List ppList = ppService.getProductPackageList("product_id=" + productId, -1, -1, null);
			Iterator ppIter = ppList.listIterator();
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
    			productIds = productIds + "," + ppBean.getParentId();
			}
    		
    		List lackOrders = wareService.getOrdersByProducts("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null);
    		lackOrders.addAll(wareService.getOrdersByPresents("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null));
    		Iterator iter = lackOrders.listIterator();
    		while(iter.hasNext()){
    			voOrder order = (voOrder)iter.next();

				// 判断订单中商品的库存是否满足，根据库存状态，设置订单发货状态
				List orderProductList = wareService.getOrderProducts(order.getId());
				List orderPresentList = wareService.getOrderPresents(order.getId());
				orderProductList.addAll(orderPresentList);

				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(service.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							detailList.add(tempVOP);
						}
					} else {
						vop.setPsList(service.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						detailList.add(vop);
					}
				}
				orderProductList = detailList;

				if (checkStock(orderProductList,ProductStockBean.AREA_GF) || checkStock(orderProductList,ProductStockBean.AREA_ZC)) {
//					dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
//							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 3600 and uo.is_olduser=1 and uo.id = "+order.getId());
//					dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
//							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 7200 and uo.id = "+order.getId());
//					dbOp2.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 7,uold.stockout_deal = 7,uold.next_deal_datetime = null " +
//							"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') > 7200 and uo.id = "+order.getId());
					StockOperationAction.updateOrderLackStatu(dbOp2,order.getId());
				}
			
    		}
    	}catch (Exception e) {
    		e.printStackTrace();
		}finally{
			dbOp.release();
			dbOp2.release();
		}
    	
    }
	
	public static boolean checkStock(List orderProductList,int area) {
		if (orderProductList == null) {
			return false;
		}

		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.STOCKTYPE_QUALIFIED,area) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}
	
	/**
	 * 当审核完毕后 就要根据报损和报溢 变化库存
	 * 
	 * @param bean
	 */
	public static boolean updateStock(BsbyOperationnoteBean bean, HttpServletRequest request, HttpServletResponse response, DbOperation dbOp) {


			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return false;
			}
			WareService wareService = new WareService(dbOp);
			IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, bsbyservice.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,
					service.getDbOp());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			
			//财务基础数据
			List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
			try {

				if (bean.getType() == 3) {
					request.setAttribute("tip", "该操作已经完成，不能再更改！");
					request.setAttribute("result", "failure");
					return false;
				}


				int bybs_type = bean.getType();// 单据类型

				// 得到这个单据中的所有的要修改库存的商品
				List list = bsbyservice.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
				if (list.size() != 0) {
					Iterator it = list.iterator();
					for (; it.hasNext();) {
						BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
						BsbyProductCargoBean bsbyCargo = bsbyservice.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
						if (bsbyCargo == null) {
							request.setAttribute("tip", "货位信息异常，操作失败，请联系管理员！");
							request.setAttribute("result", "failure");
							return false;
						}
						int productId = bsbyProductBean.getProduct_id();
						// 每一个单据中的产品 依次进行修改库存操作
						String titleString = "";

						// 开始事务
						service.getDbOp().startTransaction();

						voProduct product = wareService.getProduct(productId);
						// 得到这个产品的所有库存的列表
						product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));

						// 出库 报损就是出库
						if (bybs_type == 0) {
							titleString = "报损";

							BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

							bsbyOperationRecordBean.setOperator_id(user.getId());
							bsbyOperationRecordBean.setOperator_name(user.getUsername());
							bsbyOperationRecordBean.setTime(DateUtil.getNow());
							bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存"
									+ product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
							bsbyOperationRecordBean.setOperation_id(bean.getId());
							bsbyOperationRecordBean.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}

							// 更新指定库的库存

							ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area="
									+ bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
							product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
									null));

							if (ps == null) {
								service.getDbOp().rollbackTransaction();
								request.setAttribute("tip", "没有找到产品库存，操作失败！");
								request.setAttribute("result", "failure");
								return false;
							}
							/*if (!psService.updateProductStockCount(ps.getId(), -bsbyProductBean.getBsby_count())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								return false;
							}*/
							//审核完成，清除库存锁定量
							if (!psService.updateProductLockCount(ps.getId(), -bsbyProductBean.getBsby_count())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							//审核完成，减货位库存锁定量
							if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							
							
							/*//对于从退货库报损的情况
							if( ps.getArea() == ProductStockBean.AREA_ZC && ps.getType() == ProductStockBean.STOCKTYPE_RETURN ){
								int totalCount = bsbyProductBean.getBsby_count();
								ReturnedProductBean rpbUnApp = statService.getReturnedProduct("product_id = " + ps.getProductId() + " and type = " + ReturnedProductBean.UNAPPRAISAL + " and count >= 0");
								//先减未质检量
								if( totalCount > 0 && rpbUnApp != null && rpbUnApp.getCount() != 0 ) {
									if( (totalCount - rpbUnApp.getCount()) <= 0 ) {
										if( !statService.updateReturnedProduct("count = count - " + totalCount , "id = " + rpbUnApp.getId())){
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库存操作失败");
						                    request.setAttribute("result", "failure");
						                    return false;
										}
										totalCount = 0;
									} else {
										if( !statService.updateReturnedProduct("count = count - " + rpbUnApp.getCount() , "id = " + rpbUnApp.getId())){
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库存操作失败");
						                    request.setAttribute("result", "failure");
						                    return false;
										}
										totalCount -= rpbUnApp.getCount();
									}
								}
								//再减质检不合格量
								ReturnedProductBean rpbUnq = statService.getReturnedProduct("product_id = " + ps.getProductId() + " and type = " + ReturnedProductBean.APPRAISAL_UNQUALIFY + " and count >= 0");
								if( totalCount > 0 && rpbUnq != null && rpbUnq.getCount() != 0 ) {
									if( (totalCount - rpbUnq.getCount()) <= 0 ) {
										if( !statService.updateReturnedProduct("count = count - " + totalCount , "id = " + rpbUnq.getId())){
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库存操作失败");
						                    request.setAttribute("result", "failure");
						                    return false;
										}
										totalCount = 0;
									} else {
										if( !statService.updateReturnedProduct("count = count - " + rpbUnq.getCount() , "id = " + rpbUnq.getId())){
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库存操作失败");
						                    request.setAttribute("result", "failure");
						                    return false;
										}
										totalCount -= rpbUnq.getCount();
									}
								}
								
								//再减合格量
								ReturnedProductBean rpbQualify = statService.getReturnedProduct("product_id = " + ps.getProductId() + " and type = " + ReturnedProductBean.APPRAISAL_QUALIFY + " and count >= 0");
								if( totalCount > 0 && rpbQualify != null && rpbQualify.getCount() != 0 ) {
									if( (totalCount - rpbQualify.getCount()) <= 0 ) {
										if(!psService.updateUnqualifyReturnedProduct(ps.getProductId(), ReturnedProductBean.APPRAISAL_QUALIFY, totalCount)) {
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "数据库存操作失败");
						                    request.setAttribute("result", "failure");
						                    return false;
										}
										
										List returnedProductCargoList = statService.getReturnedProductCargoList("product_id = " + product.getId(), -1, -1, "count desc");
										int leftCount = totalCount;
										if( returnedProductCargoList.size() > 0 ) {
											for( int k = 0; k < returnedProductCargoList.size(); k ++ ) {
												ReturnedProductCargoBean rpcb = (ReturnedProductCargoBean)returnedProductCargoList.get(k);
												if( (leftCount - rpcb.getCount()) <= 0 ) {
													if( !statService.updateReturnedProductCargo("count = count - " + leftCount, "id = " + rpcb.getId())) {
														service.getDbOp().rollbackTransaction();
														request.setAttribute("tip", "数据库存操作失败");
									                    request.setAttribute("result", "failure");
									                    return false;
													}
													leftCount = 0;
												} else {
													leftCount -= rpcb.getCount();
													if( !statService.updateReturnedProductCargo("count = count - " + rpcb.getCount(), "id = " + rpcb.getId())) {
														service.getDbOp().rollbackTransaction();
														request.setAttribute("tip", "数据库存操作失败");
									                    request.setAttribute("result", "failure");
									                    return false;
													}
												}
											}
											if( leftCount != 0 ) {
												service.getDbOp().rollbackTransaction();
												request.setAttribute("tip", "商品"+product.getCode()+"的绑定货位的合格量不足");
							                    request.setAttribute("result", "failure");
							                    return false;
											}
										} else {
											service.getDbOp().rollbackTransaction();
											request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，合格量在合格量货位关联表里没有记录");
						                    request.setAttribute("result", "failure");
						                    return false;
										}
									} else {
										service.getDbOp().rollbackTransaction();
										request.setAttribute("tip", "商品"+product.getCode()+"的数量不足报损的数量");
					                    request.setAttribute("result", "failure");
					                    return false;
									}
								} 
								
							}*/

							BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();

							bsbyOperationRecordBean1.setOperator_id(user.getId());
							bsbyOperationRecordBean1.setOperator_name(user.getUsername());
							bsbyOperationRecordBean1.setTime(DateUtil.getNow());
							bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品"
									+ bsbyProductBean.getProduct_code() + "出库" + bsbyProductBean.getBsby_count());
							bsbyOperationRecordBean1.setOperation_id(bean.getId());
							bsbyOperationRecordBean1.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}

							//财务基础数据列表
							BaseProductInfo base = new BaseProductInfo();
							base.setId(bsbyProductBean.getProduct_id());
							base.setProductStockId(ps.getId());
							base.setOutCount(bsbyProductBean.getBsby_count());
							base.setOutPrice(bsbyProductBean.getBsby_price());
							baseList.add(base);
							
//							// 更新批次记录
//							List sbList = service.getStockBatchList("product_id=" + productId + " and stock_type="
//									+ bean.getWarehouse_type() + " and stock_area=" + bean.getWarehouse_area(), -1, -1,
//							"id asc");
//							float stockOutPrice = 0;
//							if (sbList != null && sbList.size() != 0) {
//								int stockExchangeCount = bsbyProductBean.getBsby_count();//要报损的产品数量
//								int index = 0;
//								int batchCount = 0;//当次要报损的数量
//
//								do {
//									StockBatchBean batch = (StockBatchBean) sbList.get(index);
//									if (stockExchangeCount >= batch.getBatchCount()) {
//										//如果报损的数量大于当前批次的数量 就删除这个批次. 那这次要报损的数量就是这个批次的数量
//										if(!service.deleteStockBatch("id=" + batch.getId()))
//										{
//										  service.getDbOp().rollbackTransaction();
//										  request.setAttribute("tip", "数据库操作失败");
//										  request.setAttribute("result", "failure");
//										  return false;
//										}
//										batchCount = batch.getBatchCount();
//									} else {
//										//如果报损数量小于当前批次数量, 那就改变这个批次中的存货厕数量,那这次出货的数量就是剩下的数量
//										
//										if(!service.updateStockBatch("batch_count = batch_count-" + stockExchangeCount,
//												"id=" + batch.getId()))
//										{
//										  service.getDbOp().rollbackTransaction();
//										  request.setAttribute("tip", "数据库操作失败");
//										  request.setAttribute("result", "failure");
//										  return false;
//										}
//										batchCount = stockExchangeCount;
//									}
//
//									// 添加批次操作记录
//									StockBatchLogBean batchLog = new StockBatchLogBean();
//									batchLog.setCode(bean.getReceipts_number());
//									batchLog.setStockType(batch.getStockType());
//									batchLog.setStockArea(batch.getStockArea());
//									batchLog.setBatchCode(batch.getCode());
//									batchLog.setBatchCount(bsbyProductBean.getBsby_count());
//									batchLog.setBatchPrice(batch.getPrice());
//									batchLog.setProductId(batch.getProductId());
//									batchLog.setRemark("报损报溢");
//									batchLog.setCreateDatetime(DateUtil.getNow());
//									batchLog.setUserId(user.getId());
//									if(!service.addStockBatchLog(batchLog)){
//										request.setAttribute("tip", "批次记录添加失败，请重新尝试操作！");
//										request.setAttribute("result", "failure");
//										service.getDbOp().rollbackTransaction();
//										return false;
//									}
//									
//									//财务产品信息表---liuruilan-----
//									int ticket = FinanceSellProductBean.queryTicket(dbOp, batch.getCode());	//是否含票 
//									if(ticket == -1){
//										request.setAttribute("tip", "查询异常，请与管理员联系！");
//										request.setAttribute("result", "failure");
//										return false;
//									}
//									FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//									if(fProduct == null){
//										request.setAttribute("tip", "查询异常，请与管理员联系！");
//										request.setAttribute("result", "failure");
//										return false;
//									}
//									int _count = FinanceProductBean.queryCountIfTicket(dbOp, batch.getProductId(), ticket);
//									float price5 = product.getPrice5();
//			    					int totalCount = product.getStockAll() + product.getLockCountAll();
//									float priceSum = Arith.mul(price5, totalCount);
//									float priceHasticket = fProduct.getPriceHasticket();
//									float priceNoticket = fProduct.getPriceNoticket();
//									float priceSumHasticket = 0;
//									float priceSumNoticket = 0;
//									String set = "price_sum =" + priceSum;
//									if(ticket == 0){	//0-有票
//										priceSumHasticket = Arith.mul(priceHasticket,  _count);
//										set += ", price_sum_hasticket =" + priceSumHasticket;
//									}
//									if(ticket == 1){	//1-无票
//										priceSumNoticket = Arith.mul(priceNoticket,  _count);
//										set += ", price_sum_noticket =" + priceSumNoticket;
//									}
//									if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId()))
//									{
//									  service.getDbOp().rollbackTransaction();
//									  request.setAttribute("tip", "数据库操作失败");
//									  request.setAttribute("result", "failure");
//									  return false;
//									}
//									
//									//财务进销存卡片
//									product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
//											null));
//									int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), bean.getWarehouse_area(), bean.getWarehouse_type(), ticket, batch.getProductId());
//									int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, bean.getWarehouse_type(), ticket, batch.getProductId());
//									int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(),  bean.getWarehouse_area(), -1,ticket, batch.getProductId());
//									FinanceStockCardBean fsc = new FinanceStockCardBean();
//									fsc.setCardType(StockCardBean.CARDTYPE_LOSE);
//									fsc.setCode(bean.getReceipts_number());
//									fsc.setCreateDatetime(DateUtil.getNow());
//									fsc.setStockType(bean.getWarehouse_type());
//									fsc.setStockArea(bean.getWarehouse_area());
//									fsc.setProductId(batch.getProductId());
//									fsc.setStockId(ps.getId());
//									fsc.setStockInCount(batchCount);
//									fsc.setStockAllArea(stockAllArea);
//									fsc.setStockAllType(stockAllType);
//									fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//									fsc.setStockPrice(product.getPrice5());
//									
//									fsc.setCurrentStock(currentStock);
//									fsc.setType(fsc.getCardType());
//									fsc.setIsTicket(ticket);
//									fsc.setStockBatchCode(batch.getCode());
//									fsc.setBalanceModeStockCount(_count);
//									if(ticket == 0){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), batchCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//									}
//									if(ticket == 1){
//										fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), batchCount))));
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//									}
//									double tmpPrice = Arith.add(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//									fsc.setAllStockPriceSum(tmpPrice);
//									if(!frfService.addFinanceStockCardBean(fsc))
//									{
//									  service.getDbOp().rollbackTransaction();
//									  request.setAttribute("tip", "数据库操作失败");
//									  request.setAttribute("result", "failure");
//									  return false;
//									}
//									//---------------liuruilan-----------
//									
//									//出货的总金额
//									stockOutPrice = stockOutPrice + batch.getPrice() * batchCount;
//
//									stockExchangeCount -= batch.getBatchCount();
//									index++;
//								} while (stockExchangeCount > 0 && index < sbList.size());
//
//							}
							// 更新库存价格
//							int totalCount = product.getStock(ProductStockBean.AREA_BJ)
//							+ product.getStock(ProductStockBean.AREA_GF)
//							+ product.getStock(ProductStockBean.AREA_GS)
//							+ product.getLockCount(ProductStockBean.AREA_BJ)
//							+ product.getLockCount(ProductStockBean.AREA_GF)
//							+ product.getLockCount(ProductStockBean.AREA_GS);
//							float price5 = ((float) Math.round((product.getPrice5() * totalCount - stockOutPrice)
//									/ (totalCount - bsbyProductBean.getBsby_count()) * 1000)) / 1000;
//							if (totalCount - bsbyProductBean.getBsby_count() == 0) {
//								price5 = 0;
//							}
//							service.getDbOp().executeUpdate(
//									"update product set price5=" + price5 + " where id = " + product.getId());

							//**
							if(!bsbyservice.updateBsbyProductBean("bsby_price = "+product.getPrice5(), "id = "+bsbyProductBean.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}
							// 审核通过，就加 进销存卡片
							product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
									null));
							CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = "+bsbyCargo.getCargoProductStockId());

							// 出库卡片
							StockCardBean sc = new StockCardBean();
//							int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
//							sc.setId(scId);

							sc.setCardType(StockCardBean.CARDTYPE_LOSE);// 出库就是报损
							sc.setCode(bean.getReceipts_number());

							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getWarehouse_type());
							sc.setStockArea(bean.getWarehouse_area());
							sc.setProductId(productId);
							sc.setStockId(ps.getId());
							sc.setStockOutCount(bsbyProductBean.getBsby_count());
//							sc.setStockOutPriceSum(stockOutPrice);
							sc.setStockOutPriceSum((new BigDecimal(bsbyProductBean.getBsby_count())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType())
									+ product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
							sc.setStockAllArea(product.getStock(bean.getWarehouse_area())
									+ product.getLockCount(bean.getWarehouse_area()));
							sc.setStockAllType(product.getStockAllType(sc.getStockType())
									+ product.getLockCountAllType(sc.getStockType()));
							sc.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
									new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!psService.addStockCard(sc)){
								request.setAttribute("tip", "进销存记录添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							
							//货位出库卡片
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_LOSE);
							csc.setCode(bean.getReceipts_number());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(bean.getWarehouse_type());
							csc.setStockArea(bean.getWarehouse_area());
							csc.setProductId(productId);
							csc.setStockId(cps.getId());
							csc.setStockOutCount(bsbyProductBean.getBsby_count());
							csc.setStockOutPriceSum((new BigDecimal(bsbyProductBean.getBsby_count())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
							csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
							csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							if(!cargoService.addCargoStockCard(csc)){
								request.setAttribute("tip", "货位进销存记录添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}

						}
						// 入库
						else {
							titleString = "报溢";

							BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

							bsbyOperationRecordBean.setOperator_id(user.getId());
							bsbyOperationRecordBean.setOperator_name(user.getUsername());
							bsbyOperationRecordBean.setTime(DateUtil.getNow());
							bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存"
									+ product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
							bsbyOperationRecordBean.setOperation_id(bean.getId());
							bsbyOperationRecordBean.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}
							product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
//							int totalCount = product.getStock(ProductStockBean.AREA_BJ)
//							+ product.getStock(ProductStockBean.AREA_GF)
//							+ product.getStock(ProductStockBean.AREA_GS)
//							+ product.getLockCount(ProductStockBean.AREA_BJ)
//							+ product.getLockCount(ProductStockBean.AREA_GF)
//							+ product.getLockCount(ProductStockBean.AREA_GS);

							ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area="
									+ bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
							if (ps == null) {
								request.setAttribute("tip", "没有找到产品库存，操作失败！");
								request.setAttribute("result", "failure");
								return false;
							}
							if (!psService.updateProductStockCount(ps.getId(), bsbyProductBean.getBsby_count())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								return false;
							}
							
							//对于针对退货库的报溢， 要加未质检量进去
							/*if( bean.getWarehouse_area() == ProductStockBean.AREA_ZC && bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_RETURN ) {
								if( !psService.addUnappraisalNumberOrReturnedProduct(product.getId(), product.getCode(), product.getName(), bsbyProductBean.getBsby_count())) {
									request.setAttribute("tip", "商品"+product.getCode()+"库存操作失败，在加退货库未质检量时出了问题");
				                    request.setAttribute("result", "failure");
				                    service.getDbOp().rollbackTransaction();
				                    return false;
								}
							}*/

							//审核完成，增加货位库存量
							if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
								request.setAttribute("tip", "货位库存操作失败，货位库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count-"+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
								request.setAttribute("tip", "货位库存操作失败，货位冻结空间不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}

//							// log记录
//							if (user == null) {
//								request.setAttribute("tip", "当前没有登录，添加失败！");
//								request.setAttribute("result", "failure");
//								return false;
//							}

							BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();
							bsbyOperationRecordBean1.setOperator_id(user.getId());
							bsbyOperationRecordBean1.setOperator_name(user.getUsername());
							bsbyOperationRecordBean1.setTime(DateUtil.getNow());
							bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品"
									+ bsbyProductBean.getProduct_code() + "入库"+bsbyProductBean.getBsby_count());
							bsbyOperationRecordBean1.setOperation_id(bean.getId());
							bsbyOperationRecordBean1.setLog_type(0);
							if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1))
							{
							  service.getDbOp().rollbackTransaction();
							  request.setAttribute("tip", "数据库操作失败");
							  request.setAttribute("result", "failure");
							  return false;
							}
							
							
							//财务基础数据
							BaseProductInfo base = new BaseProductInfo();
							base.setId(productId);
							base.setInCount(bsbyProductBean.getBsby_count());
							base.setInPrice(product.getPrice5());
							base.setProductStockId(ps.getId());
							baseList.add(base);
							
//							// 添加批次记录
//							String code = "Q" + DateUtil.getNow().substring(0, 10).replace("-", "");
//							StockBatchBean batch;
//							batch = service.getStockBatch("code like '" + code + "%'");
//							int ticket = 0;
//							if (batch == null) {
//								// 当日第一份批次记录，编号最后三位 001
//								code += "001";
//							} else {
//								// 获取当日计划编号最大值
//								int maxid = service.getNumber("id", "stock_batch", "max", "id > 0 and code like '"
//										+ code + "%'");
//								batch = service.getStockBatch("id =" + maxid);
//								String _code = batch.getCode();
//								int number = Integer.parseInt(_code.substring(_code.length() - 3));
//								number++;
//								code += String.format("%03d", new Object[] { new Integer(number) });
//							}
//							batch = new StockBatchBean();
//							batch.setCode(code);
//							batch.setProductId(productId);
//							batch.setPrice(product.getPrice5());// 报溢的产品的价格是现有价格
//							batch.setBatchCount(bsbyProductBean.getBsby_count());
//							batch.setProductStockId(ps.getId());
//							batch.setStockArea(bean.getWarehouse_area());
//							batch.setStockType(bean.getWarehouse_type());
//							batch.setCreateDateTime(DateUtil.getNow());
//							batch.setTicket(ticket);
//							if(!service.addStockBatch(batch)){
//								request.setAttribute("tip", "批次添加失败，请重新尝试操作！");
//								request.setAttribute("result", "failure");
//								service.getDbOp().rollbackTransaction();
//								return false;
//							}
//
//							// 添加批次操作记录
//							StockBatchLogBean batchLog = new StockBatchLogBean();
//							batchLog.setCode(bean.getReceipts_number());
//							batchLog.setStockType(batch.getStockType());
//							batchLog.setStockArea(batch.getStockArea());
//							batchLog.setBatchCode(batch.getCode());
//							batchLog.setBatchCount(batch.getBatchCount());
//							batchLog.setBatchPrice(batch.getPrice());
//							batchLog.setProductId(batch.getProductId());
//							batchLog.setRemark("报损报溢");
//							batchLog.setCreateDatetime(DateUtil.getNow());
//							batchLog.setUserId(user.getId());
//							if(!service.addStockBatchLog(batchLog)){
//								request.setAttribute("tip", "批次记录添加失败，请重新尝试操作！");
//								request.setAttribute("result", "failure");
//								service.getDbOp().rollbackTransaction();
//								return false;
//							}

							// 审核通过，就加 进销存卡片

//							float price5 = ((float) Math.round((product.getPrice5() * totalCount)
//									/ (totalCount + bsbyProductBean.getBsby_count()) * 1000)) / 1000;
//							service.getDbOp().executeUpdate(
//									"update product set price5=" + price5 + " where id = " + product.getId());

							product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
							CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = "+bsbyCargo.getCargoProductStockId());

							// 入库卡片
							StockCardBean sc = new StockCardBean();
							sc.setCardType(StockCardBean.CARDTYPE_GET);
							sc.setCode(bean.getReceipts_number());

							sc.setCreateDatetime(DateUtil.getNow());
							sc.setStockType(bean.getWarehouse_type());
							sc.setStockArea(bean.getWarehouse_area());
							sc.setProductId(productId);
							sc.setStockId(ps.getId());
							sc.setStockInCount(bsbyProductBean.getBsby_count());
							sc.setStockInPriceSum(product.getPrice5()*bsbyProductBean.getBsby_count());

							sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType())
									+ product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
							sc.setStockAllArea(product.getStock(bean.getWarehouse_area())
									+ product.getLockCount(bean.getWarehouse_area()));
							sc.setStockAllType(product.getStockAllType(sc.getStockType())
									+ product.getLockCountAllType(sc.getStockType()));
							sc.setAllStock(product.getStockAll() + product.getLockCountAll());
							sc.setStockPrice(product.getPrice5());// 新的库存价格
							sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
									new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!psService.addStockCard(sc)){
								request.setAttribute("tip", "进销存记录添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							
							//货位入库卡片
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_GET);
							csc.setCode(bean.getReceipts_number());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(bean.getWarehouse_type());
							csc.setStockArea(bean.getWarehouse_area());
							csc.setProductId(productId);
							csc.setStockId(cps.getId());
							csc.setStockInCount(bsbyProductBean.getBsby_count());
							csc.setStockInPriceSum((new BigDecimal(bsbyProductBean.getBsby_count())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
							csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
							csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
							if(!cargoService.addCargoStockCard(csc)){
								request.setAttribute("tip", "货位进销存添加失败，请重新尝试操作！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return false;
							}
							
//							//财务产品信息表---liuruilan-----2012-11-02-----
//	    					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + productId);
//	    					if(fProduct == null){
//								request.setAttribute("tip", "查询异常，请与管理员联系！");
//								request.setAttribute("result", "failure");
//								return false;
//							}
//	    					float price5 = product.getPrice5();
//	    					int totalCount = product.getStockAll() + product.getLockCountAll();
//							float priceSum = Arith.mul(price5, totalCount);
//							int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), productId, ticket);
//							int stockinCount = bsbyProductBean.getBsby_count();
//							float priceHasticket = fProduct.getPriceHasticket();
//							float priceSumHasticket = Arith.mul(priceHasticket,  _count);
//							String set = "price =" + price5 + ", price_sum =" + priceSum + ", price_sum_hasticket =" + priceSumHasticket;
//							if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId()))
//							{
//							  service.getDbOp().rollbackTransaction();
//							  request.setAttribute("tip", "数据库操作失败");
//							  request.setAttribute("result", "failure");
//							  return false;
//							}
//							//财务进销存卡片
//	    					int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), batchLog.getStockType(), ticket, productId);
//	    					int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, batchLog.getStockType(), ticket,productId);
//							int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), -1,ticket, productId);
//	    					FinanceStockCardBean fsc = new FinanceStockCardBean();
//	    					fsc.setCardType(StockCardBean.CARDTYPE_GET);//StockCardBean.CARDTYPE_CANCELORDERSTOCKIN-->StockCardBean.CARDTYPE_GET
//	    					fsc.setCode(bean.getReceipts_number());
//	    					fsc.setCreateDatetime(DateUtil.getNow());
//	    					fsc.setStockType(batchLog.getStockType());
//	    					fsc.setStockArea(batchLog.getStockArea());
//	    					fsc.setProductId(productId);
//	    					fsc.setStockId(ps.getId());
//	    					fsc.setStockInCount(batchLog.getBatchCount());	
//	    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//	    					fsc.setStockAllArea(stockAllArea);
//	    					fsc.setStockAllType(stockAllType);
//	    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//	    					fsc.setStockPrice(price5);
//	    					
//	    					fsc.setType(fsc.getCardType());
//	    					fsc.setIsTicket(ticket);
//	    					fsc.setStockBatchCode(batchLog.getBatchCode());
//	    					fsc.setBalanceModeStockCount(_count);
//	    					fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(priceHasticket, stockinCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//	    					double tmpPrice = Arith.add(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//	    					fsc.setAllStockPriceSum(tmpPrice);
//	    					if(!frfService.addFinanceStockCardBean(fsc))
//	    					{
//	    					  service.getDbOp().rollbackTransaction();
//	    					  request.setAttribute("tip", "数据库操作失败");
//	    					  request.setAttribute("result", "failure");
//	    					  return false;
//	    					}
//	    					//-----------liuruilan-------------
							

							if(bean.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED){
								//更新订单缺货状态
								updateLackOrder(bsbyProductBean.getProduct_id());
							}
						}
					}

					
					//报损报溢出调用财务接口
					if(baseList.size() > 0){
						if(bybs_type == 0){
							//报损
							FinanceBaseDataService bsBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_LOSE, service.getDbOp().getConn());
							bsBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
						}else{
							//报溢
							FinanceBaseDataService byBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_GET, service.getDbOp().getConn());
							byBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
						}
					}
					
					
					// 操作完成记录 bsby
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "完成更改库存操作");
					bsbyOperationRecordBean.setOperation_id(bean.getId());
					bsbyOperationRecordBean.setLog_type(0);
					if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean))
					{
					  service.getDbOp().rollbackTransaction();
					  request.setAttribute("tip", "数据库操作失败");
					  request.setAttribute("result", "failure");
					  return false;
					}
					// 提交事务
					service.getDbOp().commitTransaction();
					
				}
			} catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				return false;
			} finally {
				service.releaseAll();
				bsbyservice.releaseAll();
			}

			return true;
		

	}
	
	/**
	 * 操作人员日志记录 
	 * 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("getOperationRecord")
	@ResponseBody
	public EasyuiDataGridJson getOperationRecord(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			String opid = StringUtil.dealParam(request.getParameter("opid"));
			List list = service.getBsbyOperationRecordList("operation_id=" + opid + " and log_type=0", -1, -1,
			"time asc");
			request.setAttribute("list", list);
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long)list.size());
			easyuiDataGridJson.setRows(list);
			return easyuiDataGridJson;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 导出列表 2010-02-22
	 * 
	 * @param request
	 * @param response
	 */
	public void printBsBy(HttpServletRequest request, HttpServletResponse response) {
		int opid = StringUtil.StringToId(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return;
		}
		UserGroupBean group = user.getGroup();

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(
				IBaseService.CONN_IN_SERVICE, null);
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bsbyOperationnoteBean = service.getBsbyOperationnoteBean("id=" + opid);
			request.setAttribute("bsbyOperationnoteBean", bsbyOperationnoteBean);
			if (bsbyOperationnoteBean.getCurrent_type() != 4) {
				request.setAttribute("tip", "表单还未确认，不能够导出");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return;
			}
			if (bsbyOperationnoteBean.getType() == 0) {
				request.setAttribute("title", "报损");
			} else {
				request.setAttribute("title", "报溢");
			}
			// 查询这个单据的所有产品
			List list = service.getBsbyProductList("operation_id=" + opid, -1, -1, null);
			request.setAttribute("bsbyProductList", list);
			// 添加日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(user.getId());
			bsbyOperationRecordBean.setOperator_name(user.getUsername());
			bsbyOperationRecordBean.setTime(DateUtil.getNow());
			bsbyOperationRecordBean.setInformation("打印单据:" + StringUtil.dealParam(request.getParameter("opcode")));
			bsbyOperationRecordBean.setOperation_id(opid);
			bsbyOperationRecordBean.setLog_type(1);
			service.addBsbyOperationRecord(bsbyOperationRecordBean);
			int printSum = bsbyOperationnoteBean.getPrint_sum() + 1;
			if(!service.updateBsbyOperationnoteBean("print_sum=" + printSum, "id=" + opid))
			{
			  service.getDbOp().rollbackTransaction();
			  request.setAttribute("tip", "数据库操作失败");
			  request.setAttribute("result", "failure");
			  return;
			}
			service.getDbOp().commitTransaction();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
		} finally {
			service.releaseAll();
		}

	}
	
	/**
	 * 根据产品id查看进销存卡片
	 * 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/findStockCard")
	@ResponseBody
	public EasyuiDataGridJson findStockCard(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiDataGrid) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");

		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		String pid = StringUtil.dealParam(request.getParameter("pid"));
		if (pid != null) {
			request.setAttribute("pid", pid);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			// 总数
			int totalCount = service.getStockCardCount("product_id=" + pid);
			List list = service.getStockCardList("product_id=" + pid, (easyuiDataGrid.getPage()-1) * easyuiDataGrid.getRows(),
					easyuiDataGrid.getRows(), "create_datetime desc");
			Collections.sort(list, new StockCardComparator());
			for(int i = 0; i < list.size(); i ++){
				StockCardBean bean = (StockCardBean) list.get(i);
				bean.setStockTypeName(ProductStockBean.getStockTypeName(bean.getStockType()));
				bean.setStockAreaName(ProductStockBean.getAreaName(bean.getStockArea()));
				bean.setStockInPriceSumString(bean.getStockInPriceSum() > 0 ? StringUtil.formatDouble2(bean.getStockInPriceSum()):"-");
				bean.setStockOutPriceSumString(bean.getStockOutPriceSum() > 0 ? StringUtil.formatDouble2(bean.getStockOutPriceSum()):"-");
				bean.setAllStockPriceSumString(StringUtil.formatDouble2(bean.getAllStockPriceSum()));
			}
			
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long)totalCount);
			easyuiDataGridJson.setRows(list);
			return easyuiDataGridJson;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		} finally {
			service.releaseAll();
		}

	}
    
	
	/**
	 * 删除单据
	 * @throws IOException 
	 */
	@RequestMapping("/delBybsOpre")
	@ResponseBody
	public void delBybsOpre(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=utf-8");
		String opid = StringUtil.dealParam(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"当前没有登录，操作失败！\"}");
			return;
		}

		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail&&bean.getCurrent_type()!=BsbyOperationnoteBean.fin_audit_Fail){
				service.getDbOp().rollbackTransaction();
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"单据已提交审核，无法删除！\"}");
				return;
			}
			// boolean flag = service.deleteBsbyOperationnote("id=" + opid);
			// 删除单据只是修改单据的status=1
			boolean flag = service.updateBsbyOperationnoteBean("if_del=1", "id=" + opid);
			if (flag) {
				// 删除单据 日志 对于的产品
				// service.deleteBsbyProduct("operation_id=" + opid);
				// service.deleteBsbyOperationRecord("operation_id=" + opid);
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("删除" + StringUtil.dealParam(request.getParameter("code")));
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(opid));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);
			} else {
				service.getDbOp().rollbackTransaction();
				response.getWriter().write("{\"result\":\"failure\",\"tip\":\"删除失败！\"}");
				return;
			}
			service.getDbOp().commitTransaction();
			response.getWriter().write("{\"result\":\"success\",\"tip\":\"删除成功！\"}");
			return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			response.getWriter().write("{\"result\":\"failure\",\"tip\":\"异常！\"}");
			return;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 查看打印记录 
	 * @throws IOException 
	 * @throws ServletException 
	 * 
	 */
	@RequestMapping("/getOperationPrintRecord")
	@ResponseBody
	public EasyuiDataGridJson getOperationPrintRecord(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		/*
		 * boolean viewAll = group.isFlag(230); if (!viewAll) {
		 * request.setAttribute("tip", "你无权操作"); request.setAttribute("result",
		 * "failure"); return; }
		 */
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			String opid = StringUtil.dealParam(request.getParameter("opid"));
			List list = service.getBsbyOperationRecordList("operation_id=" + opid + " and log_type=1", -1, -1,
			"time desc");
			request.setAttribute("list", list);
			EasyuiDataGridJson  easyuiDataGridJson = new EasyuiDataGridJson();
			easyuiDataGridJson.setTotal((long)list.size());
			easyuiDataGridJson.setRows(list);
			return easyuiDataGridJson;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			request.setAttribute("msg", "异常！");
			request.getRequestDispatcher(noSession).forward(request, response);
			return null;
		} finally {
			service.releaseAll();
		}

	}
	
	@RequestMapping("/updateBsbyProductCount")
	public void updateBsbyProductCount(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String opid = StringUtil.dealParam(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		}
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail && bean.getCurrent_type()!=BsbyOperationnoteBean.fin_audit_Fail){
				request.setAttribute("tip", "单据已提交审核，无法修改！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			List list = service.getBsbyProductList("operation_id=" + opid, -1, -1, null);
			Iterator it = list.iterator();
			for (; it.hasNext();) {
				BsbyProductBean bpb = (BsbyProductBean) it.next();
				BsbyProductCargoBean pcb = service.getBsbyProductCargo("bsby_product_id = "+bpb.getId());
				int id = bpb.getId();
				String count = StringUtil.dealParam(request.getParameter("editCount"));
				BsbyOperationnoteBean bsbyOperationnoteBean = service.getBsbyOperationnoteBean("id=" + opid);
				int x = getProductCount(bpb.getProduct_id(), bsbyOperationnoteBean.getWarehouse_area(),
						bsbyOperationnoteBean.getWarehouse_type());
				int result = updateProductCount(x, bsbyOperationnoteBean.getType(), StringUtil.toInt(count));
				if (result < 0 ) {
					request.setAttribute("tip", "您所添加商品的库存不足！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
					return;
				}
				boolean flag = service.updateBsbyProductBean("bsby_count=" + count + " , before_change=" + x
						+ " , after_change=" + result, "id=" + id);
				if(!service.updateBsbyProductCargo("count=" + count , "id=" + pcb.getId()))
				{
				  service.getDbOp().rollbackTransaction();
				  request.setAttribute("tip", "数据库操作失败");
				  request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				  return;
				}
				if (flag) {
					request.setAttribute("opid", opid);
					// 添加日志
					BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
					bsbyOperationRecordBean.setOperator_id(user.getId());
					bsbyOperationRecordBean.setOperator_name(user.getUsername());
					bsbyOperationRecordBean.setTime(DateUtil.getNow());
					bsbyOperationRecordBean.setInformation("修改单据:"
							+ StringUtil.dealParam(request.getParameter("opcode")) + "的商品" + bpb.getProduct_code()
							+ "的数量为：" + count);
					bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(opid));
					bsbyOperationRecordBean.setLog_type(0);
					service.addBsbyOperationRecord(bsbyOperationRecordBean);

				} else {
					service.getDbOp().rollbackTransaction();
					request.setAttribute("tip", "修改失败！");
					request.setAttribute("result", "failure");
					request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
					return;
				}

			}
			service.getDbOp().commitTransaction();
			request.setAttribute("tip", "修改成功！");
			request.setAttribute("result", "success");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			request.setAttribute("tip", "异常");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		} finally {
			service.releaseAll();
		}
	}
	
	/**
	 * 删除报损报溢的商品
	 * 
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("/delByBsProduct")
	public void delByBsProduct(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String opid = StringUtil.dealParam(request.getParameter("opid"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		}

		String bsbypid = StringUtil.dealParam(request.getParameter("bsbypid"));
		String pid = StringUtil.dealParam(request.getParameter("pid"));
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService();
		try {
			service.getDbOp().startTransaction();
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + opid);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail&& bean.getCurrent_type()!=BsbyOperationnoteBean.fin_audit_Fail){
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "单据已提交审核，无法删除！");
				request.setAttribute("result", "failure");
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			boolean flag = service.deleteBsbyProduct("id=" + bsbypid);
			if(!service.deleteBsbyProductCargo("bsby_product_id=" + bsbypid))
			{
			  service.getDbOp().rollbackTransaction();
			  request.setAttribute("tip", "数据库操作失败");
			  request.setAttribute("result", "failure");
			  request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			  return;
			}
			if (flag) {
				// 添加日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "删除产品:" + pid);
				bsbyOperationRecordBean.setOperation_id(StringUtil.toInt(opid));
				service.addBsbyOperationRecord(bsbyOperationRecordBean);
			} else {
				request.setAttribute("tip", "删除失败！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
				return;
			}
			service.getDbOp().commitTransaction();
			request.setAttribute("tip", "删除成功！");
			request.setAttribute("result", "success");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			request.setAttribute("tip", "异常");
			request.setAttribute("result", "failure");
			request.getRequestDispatcher("/admin/rec/oper/bsby/addProductAndEditRemark.jsp?opid="+opid).forward(request, response);
			return;
		} finally {
			service.releaseAll();
		}
	}
	
	//查询产品信息
	@RequestMapping("/searchProductList")
	@ResponseBody
	public EasyuiDataGridJson searchProductList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String name = request.getParameter("name");
		int stockType = StringUtil.toInt(request.getParameter("stockType"));
		int stockAreaId = StringUtil.toInt(request.getParameter("stockAreaId"));
		int operType = StringUtil.toInt(request.getParameter("operType"));

		if (name == null)
			name = "";

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService service = new WareService(dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, psService.getDbOp());
		try {
			StringBuilder buf = new StringBuilder();
			buf.append(" (pb.barcode_status is null or pb.barcode_status=0) ");
			if(!StringUtil.isNull(name)){
				name = StringUtil.toSqlLike(request.getParameter("name"));
				if(buf.length() > 0){
					buf.append(" and ");
				}
				buf.append(" (a.name like '%");
				buf.append(name);
				buf.append("%' or a.oriname like '%");
				buf.append(name);
				buf.append("%') ");
			}
			List list = null;

			list = service.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc");
			Iterator iter = list.listIterator();
			List newList=new ArrayList();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
				product.setPsList(psList);
				product.setProductCargoCode(getProductCargoCode(product.getId(), stockType,stockAreaId,operType,cargoService));
				product.setFcStock(product.getStock(1));
				product.setZcStock(product.getStock(3));
				ResultSet rs = null;
				String sqlSupplier = "select GROUP_CONCAT(d.name)sName from product p left outer join product_supplier c on p.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 " + " where  p.id=" + product.getId();
				rs = service.getDbOp().executeQuery(sqlSupplier);
				while (rs.next()) {
					String proxyName = StringUtil.convertNull(rs.getString("sName"));
					product.setProxyName(proxyName);
				}
				rs.close();
			}
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			if(list==null){
				easyuiDataGridJson.setTotal((long)0);
				easyuiDataGridJson.setRows(new ArrayList());
			} else {
				easyuiDataGridJson.setTotal((long)list.size());
				easyuiDataGridJson.setRows(list);
			}
			return easyuiDataGridJson;
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			psService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 源货位
	 * 
	 */
	public String getProductCargoCode(int productId, int stockType, int stockAreaId, int operType,ICargoService cargoService)throws Exception{
		List<EasyuiComBoBoxBean> list = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = null;
		bean = new EasyuiComBoBoxBean();
		bean.setId("");
		bean.setText("");
		bean.setSelected(true);
		list.add(bean);
		List<CargoProductStockBean> cpsList = cargoService.getCargoAndProductStockList("cps.product_id = "+productId, -1, -1, "ci.whole_code asc");
		if(cpsList != null){
    		for(int i=0;i<cpsList.size();i++){
    			CargoProductStockBean cps = (CargoProductStockBean)cpsList.get(i);
    			if(cps.getCargoInfo().getStockType() != stockType || cps.getCargoInfo().getAreaId() != stockAreaId){
    				continue;
    			}
    			if(operType == 0 && cps.getStockCount() == 0){
    				continue;
    			}
    			
				bean = new EasyuiComBoBoxBean();
				bean.setId(cps.getCargoInfo().getWholeCode());
				bean.setText(cps.getCargoInfo().getWholeCode()+"("+cps.getStockCount()+")");
				list.add(bean);
			}
		}
		return JSONArray.fromObject(list).toString();
			
	}
}
