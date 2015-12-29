package mmb.rec.oper;

import java.io.BufferedReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.SortingBatchBean;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.mmb.utils.JBBCodeHandle;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockPrintLogBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
@Controller
@RequestMapping("/SortingController")
public class SortingController extends DispatchAction {
	public static byte[] cargoLock = new byte[0];
	private String date = DateUtil.formatDate(new Date());
	public static final String normalTimeFormat = "yyyy-MM-dd HH:mm:ss";

	/*
	 * 批次列表
	 */
	public static int sfMaxfhCount = 300;// 顺丰当天发货最大数量
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingBatchList")
	@ResponseBody
	public EasyuiDataGridJson sortingBatchList(HttpServletRequest request, 
			HttpServletResponse response) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService icService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		List<SortingBatchBean> batchList = new ArrayList<SortingBatchBean>();
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		try {
			String page = StringUtil.convertNull(request.getParameter("page"));
			String rows = StringUtil.convertNull(request.getParameter("rows"));
			String sort = StringUtil.convertNull(request.getParameter("sort"));
			String order = StringUtil.convertNull(request.getParameter("order"));
			if (!group.isFlag(595)) {
				request.setAttribute("msg", "您没有此权限!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			String storage = StringUtil.convertNull(request.getParameter("storage"));
			String status = StringUtil.convertNull(request.getParameter("status"));
			String select = StringUtil.convertNull(request.getParameter("select"));
			String text = StringUtil.convertNull(request.getParameter("text"));
			String startDay = StringUtil.convertNull(request.getParameter("startDay"));
			String startHour = StringUtil.convertNull(request.getParameter("startHour"));
			String startM = StringUtil.convertNull(request.getParameter("startM"));
			String endHour = StringUtil.convertNull(request.getParameter("endHour"));
			String endM = StringUtil.convertNull(request.getParameter("endM"));
			String flag = StringUtil.convertNull(request.getParameter("flag"));
			String type1 = StringUtil.convertNull(request.getParameter("type1"));
			// 该员工可操作的区域列表
			List<?> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			String areaString = new String();
			if (areaList != null) {
				for (int i = 0; i < areaList.size(); i++) {
					areaString += (String) areaList.get(i);
					areaString += ",";
				}
			}
			String sql = "id>0 and storage in (" + areaString + "-1)";
			if (flag.equals("0")) {// 0:今日未完成
				sql = sql + " and status<>4 and status<>5 and left(create_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'";
			} else if (flag.equals("1")) {// 1:历史未完成
				sql = sql + " and status<>4 and status<>5";
			} else if (flag.equals("2")) {// 2:今日已完成
				sql = sql + " and status=4 and left(complete_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'";
			}
			StringBuilder url = new StringBuilder();
			url.append("sortingAction.do?method=sortingBatchList");
			if (!storage.equals("")) {
				sql = sql + " and storage=" + storage;
				url.append("&storage=" + storage);
			}
			if (!status.equals("")) {
				sql = sql + " and status=" + status;
				url.append("&status=" + status);
			}
			if (!type1.equals("")) {
				sql = sql + " and type1=" + type1;
				url.append("&type1=" + type1);
			}
			if (select.equals("0") && !text.equals("")) {
				if (text.substring(0, 2).equals("FJ") && text.length() == 12) {// 说明输入的是批次编号
					sql = sql + " and code='" + text + "'";
					url.append("&text=" + text);
				} else if (text.substring(0, 2).equals("FJ") && text.length() >= 15) {
					sql = sql + " and id = (select sorting_batch_id from sorting_batch_group where code ='" + text + "')";
					url.append("&text=" + text);
				} else if (text.substring(0, 2).equals("CK")) {
					sql = sql + " and id = (select sorting_batch_id from sorting_batch_order where  status<>4 and order_code=(select order_code from order_stock where code='" + text + "'))";
					url.append("&text=" + text);
				} else if (!text.equals("订单号/批次号/波次号")) {
					sql = sql + " and id = (select sorting_batch_id from sorting_batch_order where  status<>4 and delete_status<>1 and order_code='" + text + "')";
					url.append("&text=" + text);
				}
			}
			if (startHour.length() == 1) {
				startHour = "0" + startHour;
			}
			if (endHour.length() == 1) {
				endHour = "0" + endHour;
			}
			String startTime = startDay + " " + startHour + ":" + startM + ":" + "00";// 2012-08-06010
			String endTime = startDay + " " + endHour + ":" + endM + ":" + "00";
			if (select.equals("1")) {
				sql = sql + " and create_datetime between '" + startTime + "' and '" + endTime + "'";
				url.append("&startDay=" + startDay);
				url.append("&startHour=" + startHour);
				url.append("&startM=" + startM);
				url.append("&endHour=" + endHour);
				url.append("&endM=" + endM);
			}
			SortingBatchBean sbBean = siService.getSortingBatchInfo(" 1=1 order by id desc limit 1");
			EasyuiDataGrid easyUiPage = new EasyuiDataGrid();
			easyUiPage.setOrder(order);
			easyUiPage.setPage(StringUtil.toInt(page));
			easyUiPage.setRows(StringUtil.toInt(rows));
			easyUiPage.setSort(sort);
			String orderby = null;
			if (easyUiPage.getSort() != null && !"".equals(easyUiPage.getSort().trim())) {// 设置排序
				orderby+=easyUiPage.getSort() + " " + easyUiPage.getOrder();
			}else{
				orderby="id desc";
			}
			batchList = siService.getSortingBatchList(sql.toString(), (easyUiPage.getPage()-1)*easyUiPage.getRows(), easyUiPage.getRows(), orderby);
			int  count =siService.getSortingBatchCount(sql.toString());
			j.setTotal(Long.valueOf(count));// 设置总记录数
			int todaySfOrderCount = 0;
			if (batchList != null) {
				for (int i = 0; i < batchList.size(); i++) {
					String parm = "";
					SortingBatchBean bean = batchList.get(i);
					bean.setStatusName(bean.getStatusName(bean.getStatus()));
					// 求批次下的SKU数
					String sqlString = "select count(distinct d.product_code) from sorting_batch as a " +
							"left join sorting_batch_order as b on a.id=b.sorting_batch_id " +
							"left join order_stock as c" + " on b.order_id =c.order_id " +
							"left join order_stock_product as d on c.id=d.order_stock_id where b.delete_status<>1 and c.status<>3 and a.id=" + bean.getId();
					ResultSet rs = icService.getDbOp().executeQuery(sqlString);
					int skuCount = 0;
					if (rs.next()) {
						skuCount = rs.getInt(1);
					}
					rs.close();
					bean.setParm(parm);
					CargoInfoAreaBean bean1 = icService.getCargoInfoArea("old_id=" + bean.getStorage());
					if (bean1 == null) {
						continue;
					}
					int groupCount = siService.getSortingBatchGroupCount("sorting_batch_id=" + bean.getId());
					int orderCount = siService.getSortingBatchOrderCount("delete_status<>1 and sorting_batch_id=" + bean.getId());
					bean.setStorageName(bean1.getName());
					bean.setSortingBatchGroupCount(groupCount);
					bean.setOrderCount(orderCount);
					bean.setSkuCount(skuCount);
				}
			}
			if (DateUtil.compareTime(DateUtil.getNow(), StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00") == 1) {
				todaySfOrderCount = stockService.getOrderStockCount("create_datetime between '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00' and '" + DateUtil.getNow() + "' and deliver=12 and status<>3");
			} else {
				todaySfOrderCount = stockService.getOrderStockCount("create_datetime between '" + DateUtil.getLastDay()[0] + " 19:30:00' and '" + StringUtil.cutString(DateUtil.getNow(), 10) + " 19:30:00' and deliver=12 and status<>3");
			}
			// 今日未完成的分拣批次() 历史未完成的分拣批次() 今日已完成的分拣批次() 至今未处理的订单()
			int noCompleteBatchCount = siService.getSortingBatchCount("status<>4 and status<>5 and left(create_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'");
			int historyNoCompleteCount = siService.getSortingBatchCount("status<>4 and status<>5");
			int completeBatchOrderCount = siService.getSortingBatchCount("status=4 and left(complete_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'");
			int ordercount = 0;
			String orderCountSql = "select count(*) from user_order a join order_stock b on a.id=b.order_id where  b.status in(0,1)";
			ResultSet rs = stockService.getDbOp().executeQuery(orderCountSql);
			if (rs.next()) {
				ordercount = rs.getInt("count(*)");
			}
			rs.close();
			if (sbBean != null) {
				request.setAttribute("lastTime", sbBean.getCreateDatetime());// 上次生成批次时间
			}
			// 计算下次批次生成时间 半个小时间隔
			String currentTime = DateUtil.getNow();// 当前时间
			String nextTime = new String();// 下次批次生成时间
			int currentMinute = StringUtil.toInt(currentTime.substring(14, 16).toString());// 计算当前分钟数
			int compareTime = DateUtil.compareTime(DateUtil.getNow(), DateUtil.getNow().substring(0, 11) + "19:01:00");// 比较当前时间是否已经大于19:01分
			if (compareTime == 1) {// 如果大于19点01
				nextTime = DateUtil.getTimeBeforeMinutes(1440).substring(5, 11) + "08:01:00";// 则下次生成批次时间是在明天的八点零一分
			} else {// 如果当前时间小于19点01分
				if (currentMinute < 31) {// 判断是否大于31分
					nextTime = currentTime.substring(5, 13) + ":31:00";
				} else {
					nextTime = DateUtil.getTimeBeforeMinutes(60).substring(5, 13) + ":01:00";
				}
			}
			
			HashMap map =new HashMap();
            j.setRows(batchList);// 设置返回的行
            List list = new ArrayList();
            map.put("nextTime", nextTime);
            map.put("noCompleteBatchCount", noCompleteBatchCount+"");
            map.put("historyNoCompleteCount", historyNoCompleteCount+"");
            map.put("completeBatchOrderCount", completeBatchOrderCount+"");
            map.put("ordercount", ordercount);
            map.put("todaySfOrderCount", todaySfOrderCount);
            map.put("lastTime", sbBean.getCreateDatetime().substring(5, 19));
            list.add(map);
    		j.setFooter(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return j;
	}
	/**
	 *返回当前登陆用户所拥有的仓库列表
	 */
	@RequestMapping("/getStorageList")
	@ResponseBody
	public String getStorageList(ActionMapping mapping,
			HttpServletRequest request, HttpServletResponse response) {
		StringBuffer sb = new StringBuffer();
		List<?> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		sb.append("[");
		sb.append("{\"id\":\"-1\",\"storageName\":\"请选择库地区\",\"selected\":true},");
		if (areaList != null) {
			for (int i = 0; i < areaList.size(); i++) {
				sb.append("{\"id\":\"").append(areaList.get(i)).append("\",\"storageName\":\"").append(ProductStockBean.areaMap.get
		                (Integer.valueOf(areaList.get(i).toString()))).append("\"}");
				if (i == areaList.size()-1) {
				} else {
					sb.append(",");
				}
			}
		}
		sb.append("]");
		return sb.toString();
	}
	@RequestMapping("/createSortingBatch")
	@ResponseBody
	//生成分拣批次
	public String createSortingBatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		WareService wareService = new WareService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBatchBarcodeService batchBarcodeService = ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		String sortingType = request.getParameter("sortingType");
		synchronized (cargoLock) {
			try {
				if (!group.isFlag(596)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				int maxBatch = 0;// 今天已打印的最大批次
				String maxBatchSql = "select batch from order_stock_print_log " + "where time>'" + DateUtil.getNowDateStr() + " 00:00:00' " + "and time<'" + DateUtil.getNowDateStr() + " 23:59:59' and type=1 " + "and batch>0 order by id desc limit 1";
				ResultSet maxBatchRs = wareService.getDbOp().executeQuery(maxBatchSql);
				if (maxBatchRs.next()) {
					maxBatch = maxBatchRs.getInt(1);
				}
				maxBatchRs.close();
				// 发货地区只加增城和无锡
				int storage = 0;
				List areaList = new ArrayList();
				if (sortingType != null && "zengcheng".equals(sortingType)) {
					CargoInfoAreaBean cg = new CargoInfoAreaBean();
					cg.setId(3);
					cg.setOldId(3);
					areaList.add(cg);
					storage=3;
				} else if (sortingType != null && "wuxi".equals(sortingType)) {
					CargoInfoAreaBean cg1 = new CargoInfoAreaBean();
					cg1.setId(4);
					cg1.setOldId(4);
					areaList.add(cg1);
					storage=4;
				}
				//计算十分钟之内不允许点击生成批次按钮的逻辑
				SortingBatchBean maxSortingBatch=siService.getSortingBatchInfo("id>0 and storage="+storage+" order by id desc limit 1");
			    if(maxSortingBatch!=null){
			    	SimpleDateFormat sdf=new SimpleDateFormat(normalTimeFormat);
					String maxCreateDatetime=maxSortingBatch.getCreateDatetime();
					String nowDate=DateUtil.getNow();
					Date d1=sdf.parse(maxCreateDatetime);
					Date d2=sdf.parse(nowDate);
					if(d2.getTime()-d1.getTime()<10*60*1000){
						request.setAttribute("msg", "10分钟内不能再次生成分拣批次");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}
				}
				List batchList = new ArrayList();
				SortingBatchBean sb1 = null;
				try {
					// 循环多品订单生成批次
					for (int i = 0; i < areaList.size(); i++) {
						CargoInfoAreaBean ciaBean = (CargoInfoAreaBean) areaList.get(i);
						if (ciaBean == null) {
							continue;
						}
						// 添加批次
						String code = "FJ" + DateUtil.getNow().substring(2, 4) + DateUtil.getNow().substring(5, 7) + DateUtil.getNow().substring(8, 10) + ciaBean.getOldId();
						int batchCount = siService.getSortingBatchCount("code like'" + code + "%' and storage=" + ciaBean.getOldId());
						batchCount += 1;
						String bcount = Integer.toString(batchCount);
						if (bcount.length() == 1) {
							bcount = "00" + bcount;
						}
						if (bcount.length() == 2) {
							bcount = "0" + bcount;
						}
						code = code + bcount;
						SortingBatchBean sbBean = new SortingBatchBean();
						sbBean.setCode(code);
						batchList.add(code);
						sbBean.setCreateDatetime(DateUtil.getNow());
						sbBean.setStatus(SortingBatchBean.STATUS5);
						sbBean.setType1(1);
						sbBean.setStorage(ciaBean.getOldId());
						siService.addSortingBatchInfo(sbBean);// 添加数据到批次表中
						// 循环订单
						String sql = "select a.id,a.deliver,b.product_type,a.name,a.code,b.id,b.code,b.stock_area,b.deliver from user_order a join order_stock b on b.order_id=a.id where '"+DateUtil.getLastHalfHour()+"'>b.create_datetime and b.status in(0,1) and b.stock_area=" + ciaBean.getOldId() + " order by b.create_datetime";
						ResultSet rs = stockService.getDbOp().executeQuery(sql);
						List osList = new ArrayList();
						while (rs.next()) {
							OrderStockBean osBean = new OrderStockBean();
							voOrder vo = new voOrder();
							osBean.setOrderId(rs.getInt("a.id"));
							osBean.setId(rs.getInt("b.id"));
							osBean.setCode(rs.getString("b.code"));
							osBean.setOrderCode(rs.getString("a.code"));
							osBean.setStockArea(rs.getInt("b.stock_area"));
							vo.setId(rs.getInt("a.id"));
							vo.setCode(rs.getString("a.code"));
							vo.setName(rs.getString("a.name"));
							vo.setDeliver(rs.getInt("a.deliver"));
							osBean.setProductType(rs.getInt("b.product_type"));
							vo.setOrderStock(osBean);
							osList.add(vo);
						}
						rs.close();
						sb1 = siService.getSortingBatchInfo("code='" + code + "'");
						int serialNum = 1;// 批次中的序号
						for (int j = 0; j < osList.size(); j++) {
							psService.getDbOp().startTransaction();
							try {
								voOrder vo = (voOrder) osList.get(j);
								if (vo != null) {
									OrderStockBean osBean = vo.getOrderStock();
									List outOrderProductList = stockService.getOrderStockProductList("order_stock_id=" + osBean.getId(), -1, -1, null);
									if (outOrderProductList == null || outOrderProductList.size() == 0) {
										psService.getDbOp().rollbackTransaction();
										continue;
									}
									boolean stockCheck = true;
									// 查找区域下最小库存货位
									HashMap osMap = new HashMap();
									int flag=0;
									for (int k = 0; k < outOrderProductList.size(); k++) {
										OrderStockProductBean ospBean = (OrderStockProductBean) outOrderProductList.get(k);
										String sql1 = "select cps.id,cps.cargo_id,ci.whole_code,ci.stock_area_id,cps.stock_count "+
												" from cargo_product_stock cps "+
												" join cargo_info ci on ci.id=cps.cargo_id "+
												" where cps.product_id=" + ospBean.getProductId() + " and ci.area_id = " + ciaBean.getId() + 
												" and (ci.store_type=0 or ci.store_type=4) and cps.stock_count >= " + ospBean.getStockoutCount() +
												" and cps.stock_count =( "+
												" select max(cps2.stock_count) "+
												" from cargo_product_stock cps2 "+
												" join cargo_info ci2 on ci2.id=cps2.cargo_id and (ci2.store_type=0 or ci2.store_type=4) and cps2.stock_count >= " + ospBean.getStockoutCount() +
												" where ci2.stock_area_id=ci.stock_area_id "+
												" and cps2.product_id=cps.product_id)";
										List cargoProductCargoList = new ArrayList();
										ResultSet rs1 = siService.getDbOp().executeQuery(sql1);
										while (rs1.next()) {
											CargoProductStockBean ospcBean = new CargoProductStockBean();
											ospcBean.setId(rs1.getInt("cps.id"));
											ospcBean.setCargoId(rs1.getInt("cps.cargo_id"));
											CargoInfoBean ciBean = new CargoInfoBean();
											ciBean.setWholeCode(rs1.getString("ci.whole_code"));
											ciBean.setAreaId(rs1.getInt("ci.stock_area_id"));
											ospcBean.setCargoInfo(ciBean);
											ospcBean.setStockCount(rs1.getInt("cps.stock_count"));
											cargoProductCargoList.add(ospcBean);
										}
										rs1.close();
										if (cargoProductCargoList == null || cargoProductCargoList.size() == 0) {
											psService.getDbOp().rollbackTransaction();
											flag=1;
											break;
										}
										osMap.put(ospBean, cargoProductCargoList);
									}
									if(flag==1){
										continue;
									}
									HashMap map = confirmCargo(osMap);
									Iterator outIter = map.entrySet().iterator();
									// 遍历每一个出库的产品
									while (outIter.hasNext()) {
										Map.Entry entry = (Map.Entry) outIter.next();
										OrderStockProductBean outOrderProduct = (OrderStockProductBean) entry.getKey();
										List cpsOutList = (List) entry.getValue();
										voProduct product = wareService.getProduct(outOrderProduct.getProductId());
										product.setCargoPSList(cargoService.getCargoAndProductStockList("cps.product_id = " + product.getId(), -1, -1, "cps.id asc"));
										if (cpsOutList == null || cpsOutList.size() == 0) {
											stockCheck = false;
											break;
										}
										// 更新货位库存锁定记录
										int totalCount = outOrderProduct.getStockoutCount();
										int index = 0;
										int stockOutCount = 0;
										// 遍历每一个货位
										do {
											CargoProductStockBean cps = (CargoProductStockBean) cpsOutList.get(index);
											// 如果该商品要出库的数量大于等于该货位库存的可发货数量
											if (totalCount >= cps.getStockCount()) {
												stockOutCount = cps.getStockCount();
											} else {
												stockOutCount = totalCount;
											}
											totalCount -= cps.getStockCount();
											index++;
											// 添加订单出库货位信息记录
											OrderStockProductCargoBean ospc = new OrderStockProductCargoBean();
											ospc.setOrderStockId(osBean.getId());
											ospc.setOrderStockProductId(outOrderProduct.getId());
											ospc.setCount(stockOutCount);
											ospc.setCargoProductStockId(cps.getId());
											ospc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
											if (!stockService.addOrderStockProductCargo(ospc)) {
												psService.getDbOp().rollbackTransaction();
												stockCheck = false;
												break;
											}

											// 更新货位库存
											if (!cargoService.updateCargoProductStockCount(cps.getId(), -stockOutCount)) {
												psService.getDbOp().rollbackTransaction();
												stockCheck = false;
												break;
											}
											StockAdminHistoryBean log = new StockAdminHistoryBean();
											log.setAdminId(user.getId());
											log.setAdminName(user.getUsername());
											log.setLogId(osBean.getId());
											log.setLogType(StockAdminHistoryBean.ORDER_STOCK_STATUS2);
											log.setOperDatetime(DateUtil.getNow());
											log.setRemark("手动分拣：商品编号" + outOrderProduct.getProductId() + "，锁定数量" + stockOutCount + "，锁定前库存" + cps.getStockCount() + "， 锁定后库存" + (cps.getStockCount() - stockOutCount));
											log.setType(StockAdminHistoryBean.CHANGE);
											stockService.addStockAdminHistory(log);
											if (!cargoService.updateCargoProductStockLockCount(cps.getId(), stockOutCount)) {
												psService.getDbOp().rollbackTransaction();
												stockCheck = false;
												break;
											}

										} while (totalCount > 0 && index < cpsOutList.size());

										if (!stockCheck) {
											break;
										}

									}
									if (!stockCheck) {
										if (psService.getDbOp().getConn().getAutoCommit() == false) {
											psService.getDbOp().rollbackTransaction();
										}
										continue;
									}

									if (!stockService.updateOrderStock("status=" + OrderStockBean.STATUS6, "id=" + osBean.getId() + " and status=" + OrderStockBean.STATUS2)) {
										psService.getDbOp().rollbackTransaction();
										stockCheck = false;
										break;
									}
									if (vo.getDeliver() > 0 && vo.getOrderStock().getDeliver() == 0) {

										if (!stockService.updateOrderStock("deliver = " + vo.getDeliver(), "id=" + osBean.getId())) {
											psService.getDbOp().rollbackTransaction();
											stockCheck = false;
											break;
										}
									}

									// 添加核对包裹记录
									AuditPackageBean apBean = new AuditPackageBean();
									apBean.setOrderId(vo.getId());
									apBean.setOrderCode(vo.getCode());
									apBean.setSortingDatetime(DateUtil.getNow());
									apBean.setSortingUserName(user.getUsername());
									apBean.setAreano(osBean.getStockArea());
									apBean.setDeliver(vo.getDeliver());
									apBean.setStatus(AuditPackageBean.STATUS2);// 已分拣
									apBean.setCheckUserName("");
									apBean.setAuditPackageUserName("");
									apBean.setPackageCode("");
									if (!stockService.addAuditPackage(apBean)) {
										psService.getDbOp().rollbackTransaction();
										stockCheck = false;
										break;
									}

									// 添加客户信息
									OrderCustomerBean orderCustomerBean = new OrderCustomerBean();
									orderCustomerBean.setOrderCode(vo.getCode());
									orderCustomerBean.setSerialNumber(serialNum++);
									orderCustomerBean.setStatus(OrderStockBean.STATUS6);
									String name=vo.getName();
									if(name.length()>15){
										name=name.substring(0,15);
									}
									orderCustomerBean.setName(name);
									orderCustomerBean.setBatch(maxBatch + 1);
									orderCustomerBean.setOrderDate(adultadmin.util.DateUtil.getNow());
									if (batchBarcodeService.getOrderCustomerBean("order_code='" + vo.getCode() + "'") != null) {
										batchBarcodeService.deleteOrderCustomer("order_code='" + vo.getCode() + "'");
									}
									if (!batchBarcodeService.addOrderCustomer(orderCustomerBean)) {
										psService.getDbOp().rollbackTransaction();
										stockCheck = false;
										break;
									}

									 // 打印日志
									OrderStockPrintLogBean osplBean = new OrderStockPrintLogBean();
									osplBean.setBatch(maxBatch + 1);
									osplBean.setType(1);
									osplBean.setUserId(user.getId());
									osplBean.setUserName(user.getUsername());
									osplBean.setTime(DateUtil.getNow());
									osplBean.setRemark(vo.getCode());
									if (!stockService.addOrderStockPrintLog(osplBean)) {
										psService.getDbOp().rollbackTransaction();
										stockCheck = false;
										break;
									}
									SortingBatchOrderBean sboBean = new SortingBatchOrderBean();
									sboBean.setOrderId(vo.getId());
									sboBean.setOrderType(osBean.getProductType());
									sboBean.setDeliver(vo.getDeliver());
									sboBean.setOrderCode(osBean.getOrderCode());
									sboBean.setSortingBatchId(sb1.getId());
									sboBean.setSortingBatchCode(sb1.getCode());
									sboBean.setStatus(0);
									sboBean.setDeleteStatus(0);
									sboBean.setGroupCode("");
									sboBean.setOrderStockId(vo.getOrderStock().getId());
									sboBean.setOrderStockCode(vo.getOrderStock().getCode());
									siService.addSortingBatchOrderInfo(sboBean);// 添加波次下的订单
									stockService.updateOrderStock("status=" + OrderStockBean.STATUS6, "status<>3 and order_id=" + sboBean.getOrderId());
								}
								psService.getDbOp().commitTransaction();
								psService.getDbOp().getConn().setAutoCommit(true);
							} catch (Exception e) {
								e.printStackTrace();
								psService.getDbOp().rollbackTransaction();
							}

						}
						siService.updateSortingBatchInfo("status=" + SortingBatchBean.STATUS0, "id=" + sb1.getId());
					}
				} catch (Exception e) {
					if (sb1 != null) {
						siService.updateSortingBatchInfo("status=" + SortingBatchBean.STATUS0, "id=" + sb1.getId());
					}
					e.printStackTrace();
				}
				if (psService.getDbOp().getConn().getAutoCommit() == false) {
					psService.getDbOp().getConn().setAutoCommit(true);
				}
				for (int i = 0; i < batchList.size(); i++) {
					String batchCode = (String) batchList.get(i);
					int count = siService.getSortingBatchOrderCount("delete_status<>1 and sorting_batch_code='" + batchCode + "'");
					if (count == 0) {
						siService.updateSortingBatchInfo("status=" + SortingBatchBean.STATUS, "code='" + batchCode + "'");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
		}
		request.getRequestDispatcher("/admin/rec/oper/sorting/sortingBatchList.jsp").forward(request, response);
		return null;
	}

	/*
	 * 确定订单发货的货位列表 参数 osMap key:orderStockProductBean
	 * value:cargoProductStockList 商品对应的货位库存信息每个区域只保留库存最小的那个货位 返回值：cargoMap
	 * key:orderStockProductBean value:cargoProductStockList
	 * 包含:productId,cargoId,cargoWholeCode,id
	 */
	public HashMap confirmCargo(HashMap osMap) {
		// 根据cargoListMap中的数据求出该订单中可发货数量最多的区，并锁定该区中满足可发货量的库存最少的货位
		HashMap areaMap = new HashMap();// 区域发货map key:货位编号前八位 value 该区域的可发货数量
		HashMap cargoMap = new HashMap();// 函数返回值 key:orderStockProductBean
											// value:cargoProductStockList
		Iterator iter = osMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			OrderStockProductBean orderStockProductBean = (OrderStockProductBean) entry.getKey();
			//System.out.println("输入产品Id" + orderStockProductBean.getProductId());
			List cargoProductStockList = (List) entry.getValue();
			if (cargoProductStockList != null && cargoProductStockList.size() > 0) {
				for (int i = 0; i < cargoProductStockList.size(); i++) {
					CargoProductStockBean cargoProductStockBean = (CargoProductStockBean) cargoProductStockList.get(i);
				//	System.out.println("输入产品货位列表" + cargoProductStockBean.getCargoInfo().getWholeCode());
					String areaCode = cargoProductStockBean.getCargoInfo().getWholeCode().substring(0, 7);
					// 计算出每个区域可发货的数量存入areaMap中，key:区域编号 value:区域可发货数量
					if (areaMap.size() == 0) {
						areaMap.put(areaCode, 1);
					} else if (areaMap.containsKey(areaCode)) {
						areaMap.put(areaCode, StringUtil.toInt(areaMap.get(areaCode).toString()) + 1);
					} else {
						areaMap.put(areaCode, 1);
					}
				}
			}
			// 将areaMap按照可发货数量排序存入areaSortList中
		}
		List areaSortList = new ArrayList(areaMap.entrySet());
		Collections.sort(areaSortList, new Comparator() {
			public int compare(Object o1, Object o2) {
				return (((Map.Entry) o2).getValue()).toString().compareTo(((Map.Entry) o1).getValue().toString());
			}
		});
		//System.out.println("区域排序列表" + areaSortList);
		// 获取每个产品发货的货位
		Iterator iterOsMap = osMap.entrySet().iterator();
		while (iterOsMap.hasNext()) {// 遍历每个商品
			Map.Entry entry = (Map.Entry) iterOsMap.next();// key：orderStockProductBean,value:cpsList
			OrderStockProductBean orderStockProductBean = (OrderStockProductBean) entry.getKey();// 发货产品BEAN
			List cargoProductStockList = (List) entry.getValue();// 发货产品货位列表
			for (int i = 0; i < areaSortList.size(); i++) {// 遍历最高发货量区域列表
				Map.Entry areaSortMap = (Map.Entry) areaSortList.get(i);// areaSortMap
				for (int j = 0; j < cargoProductStockList.size(); j++) {// 遍历每个商品所有的可发货货位
					CargoProductStockBean cargoProductStockBean = (CargoProductStockBean) cargoProductStockList.get(j);
					List list = new ArrayList();
					list.add(cargoProductStockBean);
					String areaCode = cargoProductStockBean.getCargoInfo().getWholeCode().substring(0, 7);
					// key
					// 货位编号前七位
					// value：每个区可发SKU种类值
					if (areaCode.equals(areaSortMap.getKey().toString())) {
						if (!cargoMap.containsKey(orderStockProductBean)) {
							cargoMap.put(orderStockProductBean, list);

							//System.out.println("SKU-" + orderStockProductBean.getProductId());
							for (int k = 0; k < list.size(); k++) {
								CargoProductStockBean cargoProductStockBean1 = (CargoProductStockBean) list.get(k);
								//System.out.println("货位-" + cargoProductStockBean1.getCargoInfo().getWholeCode());
							}
						}
						break;
					}
				}
			}
		}
		return cargoMap;
	}
	/**
	 * 订单列表
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingBatchOrderList")
	@ResponseBody
	public EasyuiDataGridJson sortingBatchOrderList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService icService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String batchId = StringUtil.convertNull(request.getParameter("batchId"));
		String code = StringUtil.convertNull(request.getParameter("code"));
		String orderType = StringUtil.convertNull(request.getParameter("searchProductType"));// 商品分类
		String deliver = StringUtil.convertNull(request.getParameter("deliver"));// 快递公司
		StringBuffer sql = new StringBuffer();
		StringBuffer url = new StringBuffer();
		String result = StringUtil.convertNull(request.getParameter("result"));
		request.setAttribute("result", result);
		String failure = StringUtil.convertNull(request.getParameter("failure"));
		request.setAttribute("failure", failure);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(593)) {
			request.setAttribute("msg", "您没有此权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		String page = StringUtil.convertNull(request.getParameter("page"));
		String rows = StringUtil.convertNull(request.getParameter("rows"));
		String sort = StringUtil.convertNull(request.getParameter("sort"));
		String order = StringUtil.convertNull(request.getParameter("order"));
		url.append("sortingAction.do?method=sortingBatchOrderList&batchId=" + batchId);
		if (!code.equals("") && code.length() > 0 && !code.equals("订单编号/分拣波次号/分拣批次号") && !code.substring(0, 2).equals("CK")) {
			if (code.substring(0, 2).equals("FJ") && code.length() == 12) {// 说明输入的是批次编号
				sql.append(" and sorting_batch_code='" + code + "'");
				url.append("&text=" + code);
			} else if (code.substring(0, 2).equals("FJ") && code.length() >= 15) {// 说明输入的是波次编号
				sql.append(" and sorting_group_code='" + code + "'");
				url.append("&text=" + code);
			} else if (code.substring(0, 2).equals("CK")) {
				sql.append(" and id = (select sorting_batch_id from sorting_batch_order where delete_status<>1 and order_code=(select order_code from order_stock where code='" + code + "'))");
				url.append("&text=" + code);
			} else if (!code.equals("订单号/批次号/波次号")) {
				sql.append(" and order_code='" + code + "'");
				url.append("&text=" + code);
			}
		}
		if (!orderType.equals("") && orderType.length() != 0 && !orderType.equals("-1") && !orderType.equals("null")) {
			sql.append(" and order_type=" + orderType);
			url.append("&parentId1=" + orderType);
		}
		if (!deliver.equals("") && deliver.length() != 0 && !deliver.equals("-1") && !deliver.equals("null")) {
			sql.append(" and deliver=" + deliver);
			url.append("&deliver=" + deliver);
		}
		try {
			List orderList = new ArrayList();

			int totalCount = siService.getSortingBatchOrderCount("delete_status<>1 and sorting_batch_id=" + batchId + sql);
			json.setTotal(Long.valueOf(totalCount));// 设置总记录数
			int countPerPage = 50;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			int SKUcount = 0;
			int productCount = 0;
			String sqlString = "select count(distinct d.product_code),sum(stockout_count) from sorting_batch as a left join sorting_batch_order as b on a.id=b.sorting_batch_id left join order_stock as c" + " on b.order_id =c.order_id left join order_stock_product as d on c.id=d.order_stock_id where b.delete_status<>1 and c.status<>3 and a.id=" + batchId;
			ResultSet rs = icService.getDbOp().executeQuery(sqlString);
			if (rs.next()) {
				SKUcount = rs.getInt(1);
				productCount = rs.getInt(2);
			}
			rs.close();
			EasyuiDataGrid easyUiPage = new EasyuiDataGrid();
			easyUiPage.setOrder(order);
			easyUiPage.setPage(StringUtil.toInt(page));
			easyUiPage.setRows(StringUtil.toInt(rows));
			easyUiPage.setSort(sort);
			List list = siService.getSortingBatchOrderList("delete_status<>1 and sorting_batch_id=" + batchId + sql, (easyUiPage.getPage()-1)*easyUiPage.getRows(), easyUiPage.getRows(), "id desc");
			for (int i = 0; i < list.size(); i++) {

				SortingBatchOrderBean sboBean = (SortingBatchOrderBean) list.get(i);
				List productList = wareService.getOrderProducts(sboBean.getOrderId());
				String productNames = new String();
				if(productList!=null){
					for(int j=0;j<productList.size();j++){
						voOrderProduct p= (voOrderProduct)productList.get(j);
						productNames=productNames+p.getName()+"<br>";
					}
				}
				voOrder vOrder = wareService.getOrder(sboBean.getOrderId());
				sboBean.setProductNames(productNames);
				sboBean.setDprice(vOrder.getDprice());
				sboBean.setName(vOrder.getName());
				sboBean.setPhone(vOrder.getPhone());
				if(sboBean.getDeliver()==0){
					sboBean.setDeliverName("未分配");
				}else{
					sboBean.setDeliverName(voOrder.deliverMapAll.get(sboBean.getDeliver()+"")+"");
				}
				OrderStockBean osBean = stockService.getOrderStock("order_id=" + sboBean.getOrderId());
				sboBean.setCkTime(osBean.getCreateDatetime());
				sboBean.setAddress(vOrder.getAddress());
				sboBean.setOrderStockCode(osBean.getCode());
				orderList.add(sboBean);
			}
			int noAssignTypeOrderCount = siService.getSortingBatchOrderCount("delete_status<>1 and order_type=0 and sorting_batch_id=" + batchId);
			int noAssignDeliverOrderCount = siService.getSortingBatchOrderCount("delete_status<>1 and deliver=0 and sorting_batch_id=" + batchId);
			int orderCount = siService.getSortingBatchOrderCount(" delete_status<>1 and sorting_batch_id=" + batchId);
			SortingBatchBean bean = siService.getSortingBatchInfo("id=" + batchId);
			int groupCount = siService.getSortingBatchGroupCount("sorting_batch_id=" + bean.getId());
			Map deliverMap = voOrder.deliverMapAll;
			if (bean != null) {
				request.setAttribute("batchCode", bean.getCode());// 批次编号
			}
			request.setAttribute("noAssignTypeOrderCount", Integer.toString(noAssignTypeOrderCount));// 未定义产品分类的订单数量
			request.setAttribute("noAssignDeliverOrderCount", Integer.toString(noAssignDeliverOrderCount));// 未定义快递公司的订单数量
			request.setAttribute("orderCount", Integer.toString(orderCount));// 订单数量
			request.setAttribute("orderList", orderList);// 订单列表
			request.setAttribute("deliverMap", deliverMap);// 快递公司列表
			request.setAttribute("productCount", productCount + "");// 产品数量
			request.setAttribute("SKUCount", SKUcount + "");// SKU数量
			request.setAttribute("batchId", batchId + "");
			request.setAttribute("groupCount", groupCount + "");
			Map productTypeMap = new HashMap();
			String sql2 = "select distinct type_id as id,name from user_order_package_type";
			ResultSet rs2 = icService.getDbOp().executeQuery(sql2);
			while (rs2.next()) {
				productTypeMap.put(rs2.getInt("id") + "", rs2.getString("name"));
			}
			rs2.close();
			request.setAttribute("productTypeMap", productTypeMap);
			List footerList = new ArrayList();
			HashMap footerMap = new HashMap();
			footerMap.put("batchCode", bean.getCode());
			footerMap.put("noAssignTypeOrderCount", Integer.toString(noAssignTypeOrderCount));
			footerMap.put("noAssignDeliverOrderCount", Integer.toString(noAssignDeliverOrderCount));
			footerMap.put("orderCount", Integer.toString(orderCount)+"");
			footerMap.put("productCount", productCount);
			footerMap.put("SKUcount", SKUcount);
			
			footerList.add(footerMap);
			json.setFooter(footerList);
			json.setRows(orderList);// 设置返回的行
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return json;
	}
	/**
	 * 
	 * 批次导单
	 */
	@RequestMapping("/sortingOrderStockPrint")
	public String  sortingOrderStockPrint( HttpServletRequest request, HttpServletResponse response) throws Exception {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String batchId = StringUtil.convertNull(request.getParameter("batchId"));
		String deliver = StringUtil.convertNull(request.getParameter("deliver"));
		voUser user = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group = user.getGroup();
		synchronized (cargoLock) {
			try {
				if (!group.isFlag(594)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				SortingBatchBean batch = siService.getSortingBatchInfo("id=" + batchId);
				if (batch == null) {
					request.setAttribute("msg", "没有找到该批次!");
					request.getRequestDispatcher("/admin/err.jsp").forward(request, response);
					return null;
				}
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT b.buy_mode ,b.code,d.product_type,b.dprice,b.address,b.postcode,b.deliver,b.name,b.phone,c.batch,c.serial_number FROM sorting_batch_order a " + "JOIN user_order b ON a.order_id=b.id join order_customer c on b.code=c.order_code join order_stock d on b.id=d.order_id and d.status!=3 where a.delete_status<>1");
				if (batchId != null) {
					sql.append(" and a.sorting_batch_id=" + batchId);
				}
				sql.append(" order by c.serial_number");
				List orderList = new ArrayList();
				PreparedStatement pst = null;
				ResultSet rs = null;
				pst = siService.getDbOp().getConn().prepareStatement(sql.toString());
				rs = pst.executeQuery();
				while (rs.next()) {
					if ("1".equals(deliver) && (rs.getInt("b.deliver") == 12 || (rs.getInt("b.deliver") == 0 && rs.getInt("b.buy_mode") == 0 && (rs.getInt("b.product_type") == 10 || rs.getInt("b.product_type") == 11 || rs.getInt("b.product_type") == 1 || rs.getInt("b.product_type") == 7)))) {
						voOrder orderBean = new voOrder();
						orderBean.setCode(rs.getString("b.code"));
						orderBean.setProductType(rs.getInt("d.product_type"));
						orderBean.setDprice(rs.getFloat("b.dprice"));
						orderBean.setAddress(rs.getString("b.address"));
						orderBean.setPostcode(rs.getString("b.postcode"));
						orderBean.setDeliver(rs.getInt("b.deliver"));
						orderBean.setName(rs.getString("b.name"));
						orderBean.setPhone(rs.getString("b.phone"));
						orderBean.setSerialNumber(rs.getInt("c.serial_number"));
						orderBean.setBatchNum(rs.getInt("c.batch"));
						orderList.add(orderBean);
						// 全部
					} else if ("all".equals(deliver)) {
						voOrder orderBean = new voOrder();
						orderBean.setCode(rs.getString("b.code"));
						orderBean.setProductType(rs.getInt("d.product_type"));
						orderBean.setDprice(rs.getFloat("b.dprice"));
						orderBean.setAddress(rs.getString("b.address"));
						orderBean.setPostcode(rs.getString("b.postcode"));
						orderBean.setDeliver(rs.getInt("b.deliver"));
						orderBean.setSerialNumber(rs.getInt("c.serial_number"));
						orderBean.setBatchNum(rs.getInt("c.batch"));
						orderBean.setName(rs.getString("b.name"));
						if (rs.getInt("b.deliver") == 12) {
							orderBean.setPhone(rs.getString("b.phone"));
						}
						orderList.add(orderBean);
					}
				}
				rs.close();
				// wareService.getDbOp().commitTransaction();
				request.setAttribute("orderList", orderList);
				SortingBatchBean bean = siService.getSortingBatchInfo("id=" + batchId);
				if (bean.getStatus() == 0) {
					siService.updateSortingBatchInfo("status=1", "id=" + batchId);
				}
				request.setAttribute("batchCode", bean.getCode());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
		}
		
			return "admin/rec/oper/sorting/sortingOrderStockPrint";
	}
	/**
	 * 分拣超时订单列表
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingOvertimeOrderList")
	@ResponseBody
	public EasyuiDataGridJson sortingOvertimeOrderList(HttpServletRequest request, HttpServletResponse response) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		String code = StringUtil.convertNull(request.getParameter("staffCode"));
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		try {
			String sql = "select c.name,c.code,a.code,a.receive_datetime,b.order_code from sorting_batch_group a " + " join sorting_batch_order b on a.id=b.sorting_group_id " + " join cargo_staff c on a.staff_id=c.id " + " where a.status=1 and b.status=2 " + " and b.delete_status<>1 and date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "' order by a.receive_datetime, c.name,a.code";
			ResultSet rs = wareService.getDbOp().executeQuery(sql);
			// System.out.println(DateUtil.getNowDateStr());
			List orderList = new ArrayList();
			while (rs.next()) {
				SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
				groupBean.setStaffName(rs.getString(1));
				groupBean.setStaffCode(rs.getString(2));
				groupBean.setCode(rs.getString(3));
				groupBean.setGroupCount(1);
				groupBean.setReceiveDatetime(rs.getString(4));
				groupBean.setOrderCode(rs.getString(5));
				orderList.add(groupBean);
			}
			rs.close();
			 j.setRows(orderList);// 设置返回的行
			request.setAttribute("orderList", orderList);
			request.setAttribute("code", code);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return j;
	}
	/**
	 * 分拣量统计
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingStatisticsList")
	@ResponseBody
	public EasyuiDataGridJson sortingStatisticsList(HttpServletRequest request, HttpServletResponse response) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);

		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String staffName =  StringUtil.convertNull(request.getParameter("staffName"));
		String staffCode = StringUtil.convertNull(request.getParameter("staffCode"));
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(644)) {
				request.setAttribute("msg", "您没有此权限!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			StringBuffer conditon = new StringBuffer();
			if ("".equals(startTime) || "".equals(endTime)) {
				startTime = DateUtil.formatDate(new Date());
				endTime = DateUtil.formatDate(new Date());
			}

			// DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			// Calendar calendar = Calendar.getInstance();
			// calendar.setTime(df.parse(endTime));
			// calendar.add(Calendar.DATE, +1); //加一天
			// endTime = df.format(calendar.getTime());

			conditon.append(" and left(a.receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'");
			if (!"".equals(staffCode)&&!" ".equals(staffCode)) {
				conditon.append(" and cs.code='" + staffCode + "'");
				String staffSQL = " select name from cargo_staff where code='" + StringUtil.toSql(staffCode) + "'";
				ResultSet staffRS = wareService.getDbOp().executeQuery(staffSQL);
				if (staffRS.next()) {
					staffName = staffRS.getString("name");
				} else {
					request.setAttribute("msg", "员工编号不正确！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				staffRS.close();
			}
			String sql = " select count(distinct d.product_code),sum(d.stockout_count),count(distinct c.order_id),count(distinct ci.passage_id)," + " count( distinct a.id),count(distinct left(a.receive_datetime,10)),cs.name,cs.code,cs.id from sorting_batch_group as a" + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where  a.staff_name is not null " + conditon + " group by a.id";
			ResultSet rs = wareService.getDbOp().executeQuery(sql);
			//System.out.println(sql);
			List staffList = new ArrayList();
			int flag = 0;
			while (rs.next()) {
				SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
				groupBean.setSkuCount(rs.getInt(1));
				groupBean.setProductCount(rs.getInt(2));
				groupBean.setOrderCount(rs.getInt(3));
				groupBean.setPassageCount(rs.getInt(4));
				// groupBean.setGroupCount(rs.getInt(3));
				// groupBean.setAttendanceCount(rs.getInt(6));
				groupBean.setStaffName(rs.getString(7));
				groupBean.setStaffCode(rs.getString(8));
				groupBean.setStaffId(rs.getInt(9));				
				if (staffList != null && staffList.size() > 0) {
					for (Iterator i = staffList.iterator(); i.hasNext();) {
						SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
						if (tempBean.getStaffCode().equals(groupBean.getStaffCode())) {
							tempBean.setSkuCount(tempBean.getSkuCount() + groupBean.getSkuCount());
							tempBean.setProductCount(tempBean.getProductCount() + groupBean.getProductCount());
							tempBean.setOrderCount(tempBean.getOrderCount() + groupBean.getOrderCount());
							tempBean.setPassageCount(tempBean.getPassageCount() + groupBean.getPassageCount());
							tempBean.setGroupCount(tempBean.getGroupCount() + groupBean.getGroupCount());
							tempBean.setAttendanceCount(tempBean.getAttendanceCount() + groupBean.getAttendanceCount());
							tempBean.setCancelOrderCount(tempBean.getCancelOrderCount()+groupBean.getCancelOrderCount());
							flag = 1;
							break;
						}
					}
					if (flag == 0) {
						staffList.add(groupBean);
					}
					flag = 0;
				} else {
					staffList.add(groupBean);
				}
			}
			rs.close();
			// 计算每个员工的出勤天数
			String sql1 = "select count(distinct left(receive_datetime,10)),cs.id from sorting_batch_group a  " + "join cargo_staff as cs on cs.id=a.staff_id where a.id>0 " + conditon + " group by cs.id";
			ResultSet rs1 = wareService.getDbOp().executeQuery(sql1);
			List staffAttendanceList = new ArrayList();
			while (rs1.next()) {
				SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
				groupBean.setAttendanceCount(rs1.getInt(1));
				groupBean.setStaffId(rs1.getInt(2));
				staffAttendanceList.add(groupBean);
			}
			rs1.close();
			if (staffList != null) {
				for (Iterator i = staffList.iterator(); i.hasNext();) {
					SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
					if (staffAttendanceList != null) {
						for (int j = 0; j < staffAttendanceList.size(); j++) {
							SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffAttendanceList.get(j);
							if (AttendanceBean.getStaffId() == groupBean.getStaffId()) {
								groupBean.setAttendanceCount(AttendanceBean.getAttendanceCount());
							}
						}
					}
				}

			}

			// 计算每个员工的完成波次数
			String sql2 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where 1=1 " + conditon + " group by cs.id";
			ResultSet rs2 = wareService.getDbOp().executeQuery(sql2);	
			//System.out.println(sql2);
			List staffGroupList = new ArrayList();
			while (rs2.next()) {
				SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
				groupBean.setGroupCount(rs2.getInt(1));
				groupBean.setStaffId(rs2.getInt(2));
				staffGroupList.add(groupBean);
			}
			rs2.close();
			if (staffList != null) {
				for (Iterator i = staffList.iterator(); i.hasNext();) {
					SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
					if (staffGroupList != null) {
						for (int j = 0; j < staffGroupList.size(); j++) {
							SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffGroupList.get(j);
							if (AttendanceBean.getStaffId() == groupBean.getStaffId()) {
								groupBean.setGroupCount(AttendanceBean.getGroupCount());
							}
						}
					}
				}

			}
			//计算每个员工的撤单量
			String sql3 = "SELECT count(s.order_id) ,t.staff_id FROM sorting_batch_group t left join sorting_batch_order s on t.id=s.sorting_group_id where s.delete_status=1 and s.status<>0 and left(t.receive_datetime,10) between '"+startTime+"' and '"+endTime+"' group by t.staff_id";
			ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
			while(rs3.next()){
				if(staffList != null){
					for(Iterator i = staffList.iterator(); i.hasNext();){
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
						if(rs3.getInt(2) == groupBean.getStaffId()){
							groupBean.setCancelOrderCount(rs3.getInt(1));
						}
					}
				}
			}
			rs3.close();
			SortingBatchGroupBean totalBean = new SortingBatchGroupBean();
			int totalAttendanceCount = 0;
			int totalGroupCount = 0;
			int totalOrderCount = 0;
			int totalSkuCount = 0;
			int totalProductCount = 0;
			int totalPassageCount = 0;
			int totalCancelOrderCount = 0;
			if (staffList != null && staffList.size() > 0) {
				for (Iterator i = staffList.iterator(); i.hasNext();) {
					SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
					totalSkuCount = tempBean.getSkuCount() + totalSkuCount;
					totalProductCount = tempBean.getProductCount() + totalProductCount;
					totalOrderCount = tempBean.getOrderCount() + totalOrderCount;
					totalPassageCount = tempBean.getPassageCount() + totalPassageCount;
					totalGroupCount = tempBean.getGroupCount() + totalGroupCount;
					totalAttendanceCount = tempBean.getAttendanceCount() + totalAttendanceCount;
					totalCancelOrderCount = tempBean.getCancelOrderCount() + totalCancelOrderCount;
				}
			}
			totalBean.setSkuCount(totalSkuCount);
			totalBean.setProductCount(totalProductCount);
			totalBean.setOrderCount(totalOrderCount);
			totalBean.setPassageCount(totalPassageCount);
			totalBean.setGroupCount(totalGroupCount);
			totalBean.setAttendanceCount(totalAttendanceCount);
			totalBean.setStaffCount(staffList.size());
			totalBean.setCancelOrderCount(totalCancelOrderCount);

			
//				field : 'attendanceCount',
//				title : '出勤天数',
//				align :'center',
//				width:'80'
//			},{
//			},{
//				field : 'cancelOrderCount',
//				title : '撤单数',
//				align :'center',
//				width:'90'
//				
//			},
			HashMap map =new HashMap();
			map.put("staffName", "总数：");
			map.put("skuCount", totalSkuCount);
			map.put("productCount", totalProductCount);
			map.put("orderCount", totalOrderCount);
			map.put("passageCount", totalPassageCount);
			map.put("groupCount", totalGroupCount);
			map.put("attendanceCount", totalAttendanceCount);
			map.put("staffCode", staffList.size());
			map.put("cancelOrderCount", totalCancelOrderCount);
			List list = new ArrayList();
			list.add(map);
			json.setFooter(list);
			request.setAttribute("totalBean", totalBean);
			request.setAttribute("staffList", staffList);
			request.setAttribute("startTime", startTime);
			request.setAttribute("endTime", endTime);
			request.setAttribute("staffName", staffName);
			request.setAttribute("staffCode", staffCode);
			json.setRows(staffList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return json;
	}
	/**
	 * 分拣监控员工列表
	 */
	@RequestMapping("/getStaffList")
	@ResponseBody
	public String getStaffList(ActionMapping mapping,
			HttpServletRequest request, HttpServletResponse response) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);

		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String staffName =  StringUtil.convertNull(request.getParameter("staffName"));
		String staffCode = StringUtil.convertNull(request.getParameter("staffCode"));
		StringBuffer sb = new StringBuffer();
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			UserGroupBean group = user.getGroup();
			StringBuffer conditon = new StringBuffer();
			conditon.append(" and left(a.receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'");
			if (!"".equals(staffCode)) {
				conditon.append(" and cs.code='" + staffCode + "'");
				String staffSQL = " select name from cargo_staff where code='" + StringUtil.toSql(staffCode) + "'";
				ResultSet staffRS = wareService.getDbOp().executeQuery(staffSQL);
				if (staffRS.next()) {
					staffName = staffRS.getString("name");
				} else {
					request.setAttribute("msg", "员工编号不正确！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				staffRS.close();
			}
			String sql = " select distinct cs.name,cs.code,cs.id from sorting_batch_group as a" + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where  a.staff_name is not null " + conditon + " group by a.id";
			ResultSet rs = wareService.getDbOp().executeQuery(sql);
			List staffList = new ArrayList();
			int flag = 0;
			while (rs.next()) {
				SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
				groupBean.setStaffName(rs.getString(1));
				groupBean.setStaffCode(rs.getString(2));
				groupBean.setStaffId(rs.getInt(3));				
			    staffList.add(groupBean);
			}
			rs.close();
			
			sb.append("[");
			if(staffList.size()==0){
				sb.append("{\"staffCode\":\" \",\"staffName\":\"全部\",\"selected\":true}");
			}else{
				sb.append("{\"staffCode\":\" \",\"staffName\":\"全部\",\"selected\":true},");
			}
			if(staffList.size()!=0){
				for (int i = 0; i < staffList.size(); i++) {
					SortingBatchGroupBean bean = (SortingBatchGroupBean)staffList.get(i);
					sb.append("{\"staffCode\":\"").append(bean.getStaffCode()).append("\",\"staffName\":\"").append(bean.getStaffName()).append("\"}");
					if (i == staffList.size()-1) {
					} else {
						sb.append(",");
					}
				}
			}
			sb.append("]");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return sb.toString();
	}

	/**
	 * 波次领取显示
	 */
	@RequestMapping("/sortingBatchOrderReceiveInfo")
	public String sortingBatchOrderReceiveInfo( HttpServletRequest request, HttpServletResponse response) {
		SortingInfoService siService = ServiceFactory.createSortingInfoService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, siService.getDbOp());

		synchronized (cargoLock) {
			try {
				voUser user = (voUser) request.getSession().getAttribute("userView");
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(592)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				String success = StringUtil.convertNull(request.getParameter("success"));
				String staffCode = StringUtil.convertNull(request.getParameter("staffCode"));
				String sortingBatchGroupId = StringUtil.convertNull(request.getParameter("sortingBatchGroupId"));
				// 该员工可操作的区域列表
				List areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
				if (sortingBatchGroupId != null && sortingBatchGroupId.length() > 0) {
					SortingBatchGroupBean groupBean = siService.getSortingBatchGroupInfo("id=" + sortingBatchGroupId);
					request.setAttribute("groupBean", groupBean);
				}
				CargoStaffBean staffBean = cargoService.getCargoStaff("code='" + staffCode + "'");
				request.setAttribute("success", success);
				request.setAttribute("staffCode", staffCode);
				request.setAttribute("staffBean", staffBean);
				// 今日单SKU已分配分拣波次数
				int assignedCount1 = siService.getSortingBatchGroupCount("staff_name is not null and status<>3 and left(receive_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'");
				// 今日多SKU已分配分拣波次数
				//int assignedCount2 = siService.getSortingBatchGroupCount("staff_name is not null and status<>3 and type1=1 and left(receive_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'");
				// 单SKU待分配分拣波次列表
				int notAssignedCount1 = siService.getSortingBatchGroupCount("staff_name is null and status=0 and status<>2");
				// 多SKU待分配分拣波次列表
				//int notAssignedCount2 = siService.getSortingBatchGroupCount("staff_name is null and status=0 and type1=1 and status<>2");

				request.setAttribute("assignedCount1", assignedCount1 + "");
				//request.setAttribute("assignedCount2", assignedCount2 + "");
				request.setAttribute("notAssignedCount1", notAssignedCount1 + "");
				//request.setAttribute("notAssignedCount2", notAssignedCount2 + "");
				request.setAttribute("areaList", areaList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				siService.releaseAll();
			}
		}
		 return "admin/rec/oper/sorting/sortingBatchOrderReceive";
	}

	/**
	 * 波次领取
	 */
	@RequestMapping("/sortingBatchOrderReceive")
	public String sortingBatchOrderReceive(HttpServletRequest request, HttpServletResponse response) {
		String staffCode = StringUtil.convertNull(request.getParameter("staffCode"));
		String ems = StringUtil.convertNull(request.getParameter("ems"));
		SortingInfoService siService = ServiceFactory.createSortingInfoService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, siService.getDbOp());
		String printStatus = StringUtil.convertNull(request.getParameter("printStatus"));// 判断打印状态
		String areaId = StringUtil.convertNull(request.getParameter("areaId"));//地区
		synchronized (cargoLock) {
			try {
				voUser user = (voUser) request.getSession().getAttribute("userView");
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(592)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				int success = 0;
				// 今日EMS已分配分拣波次数
				int assignedCount1 = siService.getSortingBatchGroupCount("staff_name is not null and status<>3 and type1=0 and type2=0 and left(receive_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'");
				// 今日非EMS已分配分拣波次数
				int assignedCount2 = siService.getSortingBatchGroupCount("staff_name is not null and status<>3 and type1=0 and type2=1 and left(receive_datetime,10)='" + StringUtil.cutString(DateUtil.getNow(), 10) + "'");
				// ems待分配分拣波次数
				int notAssignedCount1 = siService.getSortingBatchGroupCount("staff_name is null and status=0 and type1=0 and type2=0");
				// 非ems待分配分拣波次数
				int notAssignedCount2 = siService.getSortingBatchGroupCount("staff_name is null and status=0 and type1=0 and type2=1");
				List notAssignedList = new ArrayList();// 未分配波次列表
//				if (ems != null && ems.length() > 0 && "0".equals(ems)) {
//					notAssignedList = siService.getSortingBatchGroupList("staff_name is null and status=0 and type2=0", -1, -1, "create_datetime");
//				} else {
//					notAssignedList = siService.getSortingBatchGroupList("staff_name is null and status=0 and type2=1", -1, -1, "create_datetime");
//				}
				notAssignedList = siService.getSortingBatchGroupList("staff_name is null and status=0 and storage="+areaId, -1, -1, "create_datetime");
				// 员工领单
				CargoStaffBean staffBean = cargoService.getCargoStaff("code='" + staffCode + "'");
				if (staffBean == null || "".equals(staffBean)) {
					success = -1;
					request.setAttribute("msg", staffCode + "的员工不存在!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				} else {
					if ((notAssignedList != null && notAssignedList.size() > 0)) {
						Random r = new Random();
						int a = r.nextInt(notAssignedList.size());
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) notAssignedList.get(a);
						if (groupBean.getStaffId() > 0) {
							success = -1;
							request.setAttribute("msg", "此波次已经分配!");
							request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
							return null;
						}
						// siService.updateSortingBatchInfo("status=3", "id=" +
						// groupBean.getSortingBatchId());
						// siService.updateSortingBatchGroupInfo("status=1,receive_datetime='"
						// + DateUtil.getNow() + "',staff_id=" +
						// staffBean.getId() + ",staff_name='" +
						// staffBean.getName() + "'", "id=" +
						// groupBean.getId());
						// siService.updateSortingBatchOrderInfo("status=2",
						// "delete_status<>1 and status<>3 and sorting_group_id ="
						// + groupBean.getId());
						request.setAttribute("groupBean", groupBean);
						// 打印开始
						if ("".equals(printStatus)) {
							String userCode = staffBean.getCode(); // 员工号：userCode
							String sortingBatchGroupId = groupBean.getId() + "";// 分拣波次id：sortingBatchGroupId
							String printType = "all"; // 打印类型：printType（value--all/huizong/fahuoqingdan/selected）
							// String selectedOrder =
							// StringUtil.convertNull(request.getParameter("selectedOrder"));
							// // 选择要打印的selected数组：selectedOrder
							PreparedStatement pst = null;
							ResultSet rs = null;
							// DbOperation dbOp2 = new DbOperation();
							// dbOp2.init("adult");// 主数据库是adult
							siService.getDbOp().startTransaction();
							boolean transactionFlag = false;
							if ("".equals(sortingBatchGroupId)) {
								request.setAttribute("msg", "分拣波次号不能为空！");
								request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
								return null;
							}
							CargoStaffBean cargoStaff = null;
							if ("all".equals(printType)) {
								if ("".equals(userCode)) {
									request.setAttribute("msg", "员工号不能为空！");
									request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
									return null;
								}
								cargoStaff = (CargoStaffBean) siService.getXXX("code='" + userCode + "'", "cargo_staff", "adultadmin.bean.cargo.CargoStaffBean");
								if (cargoStaff == null || "".equals(cargoStaff)) {
									request.setAttribute("msg", userCode + "的员工不存在！");
									request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
									return null;
								} else {
									SortingBatchGroupBean sbb = siService.getSortingBatchGroupInfo("id=" + sortingBatchGroupId);
									if (sbb.getStaffId() > 0) {
										request.setAttribute("msg", "此波次已经分配！");
										request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
										return null;
									}
									// 分拣人id staff_id 分拣人姓名 staff_name
									siService.updateSortingBatchGroupInfo("receive_datetime='" + DateUtil.getNow() + "',staff_id=" + cargoStaff.getId() + ",staff_name='" + cargoStaff.getName() + "'", "id=" + sortingBatchGroupId);
									transactionFlag = true;
								}
							}
							// 加载分拣波次信息--传回页面
							String sql = "select cs.*,sbg.deliver,sbg.code,sbg.create_datetime,cia.name from sorting_batch_group sbg,cargo_staff cs,cargo_info_area cia  where sbg.id=? and cs.id = sbg.staff_id and cia.old_id = sbg.storage ";
							pst = siService.getDbOp().getConn().prepareStatement(sql);
							pst.setInt(1, Integer.parseInt(sortingBatchGroupId));
							rs = pst.executeQuery();
							SortingBatchGroupBean sortingBatchGroupInfo = siService.getSortingBatchGroupInfo("id=" + sortingBatchGroupId);
							while (rs.next()) {
								sortingBatchGroupInfo.setCode(rs.getString("sbg.code"));
								sortingBatchGroupInfo.setReceiveDatetime(rs.getTimestamp("sbg.create_datetime").toString());
								CargoStaffBean cargoStaff2 = new CargoStaffBean();
								cargoStaff2.setCode(rs.getString("cs.code"));
								cargoStaff2.setName(rs.getString("cs.name"));
								sortingBatchGroupInfo.setCargoStaff(cargoStaff2);
								sortingBatchGroupInfo.setStorageName(rs.getString("cia.name"));
								// 快递公司
								// sortingBatchGroupInfo.setDeliverName(voOrder.deliverGdMap.get(rs.getString("sbg.deliver")).toString());
							}
							if (rs != null) {
								rs.close();
							}
							if (pst != null) {
								pst.close();
							}
							// 把参数返回到打印页面
							request.setAttribute("sortingBatchGroupInfo", sortingBatchGroupInfo);
							request.setAttribute("printType", printType);

							// 根据分拣波次号，取得该波次下的订单列表
							// 根据分拣波次号，取得该波次下的订单列表
							StringBuffer sqlSb = new StringBuffer();
							sqlSb.append("select os.*,uo.*,sbo.group_num,sbo.group_code,sbg.code,d.serial_number,d.batch from user_order uo,order_stock os,sorting_batch_order sbo,sorting_batch_group sbg,order_customer d  where sbo.delete_status<>1 and os.status<>3 and d.order_code = uo.code and ");
							// if (!"".equals(selectedOrder)) {
							// sqlSb.append("uo.id in(").append(selectedOrder).append(") and");
							// }
							sqlSb.append(" sbo.sorting_group_id=? and sbo.order_id =os.order_id  and uo.id = os.order_id and sbo.sorting_group_id=sbg.id order by sbo.group_num asc");
							List orderList = new ArrayList();

							pst = siService.getDbOp().getConn().prepareStatement(sqlSb.toString());
							pst.setInt(1, Integer.parseInt(sortingBatchGroupId));
							rs = pst.executeQuery();

							while (rs.next()) {
								voOrder vo = new voOrder();
								// int serialNumber =
								// rs.getInt("sbo.group_num");
								// if (serialNumber != 0) {
								// vo.setSerialNumber(serialNumber);// 序号
								// }
								vo.setId(rs.getInt("uo.id"));
								vo.setName(rs.getString("uo.name"));
								vo.setPhone(rs.getString("phone"));
								vo.setAddress(rs.getString("address"));
								vo.setPostcode(rs.getString("postcode"));
								vo.setBuyMode(rs.getInt("buy_mode"));
								vo.setOperator(rs.getString("operator"));
								vo.setCreateDatetime(rs.getTimestamp("os.create_datetime"));
								vo.setUserId(rs.getInt("user_id"));
								vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
								vo.setStatus(rs.getInt("os.status"));
								vo.setCode(rs.getString("uo.code"));
								vo.setPrice(rs.getFloat("price"));
								vo.setDprice(rs.getFloat("dprice"));
								vo.setDiscount(rs.getFloat("uo.discount"));
								vo.setDeliverType(rs.getInt("deliver_type"));
								vo.setRemitType(rs.getInt("remit_type"));
								vo.setStockout(rs.getInt("stockout"));
								vo.setPhone2(rs.getString("phone2"));
								vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
								vo.setFr(rs.getInt("fr"));
								vo.setAgent(rs.getInt("agent"));
								vo.setAgentMark(rs.getString("agent_mark"));
								vo.setAgentRemark(rs.getString("agent_remark"));
								vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
								vo.setIsReimburse(rs.getInt("is_reimburse"));
								vo.setRealPay(rs.getFloat("real_pay"));
								vo.setPostage(rs.getInt("postage"));
								vo.setIsOrder(rs.getInt("is_order"));
								vo.setImages(rs.getString("images"));
								vo.setAreano(rs.getInt("areano"));
								vo.setPrePayType(rs.getInt("pre_pay_type"));
								vo.setIsOlduser(rs.getInt("is_olduser"));
								vo.setSuffix(rs.getFloat("suffix"));
								vo.setContactTime(rs.getInt("contact_time"));
								vo.setUnitedOrders(rs.getString("united_orders"));
								vo.setRemark(rs.getString("sbg.code"));// 借用，放分拣波次号
								vo.setFlat(rs.getInt("flat"));
								vo.setHasAddPoint(rs.getInt("has_add_point"));
								vo.setGender(rs.getInt("gender"));
								vo.setWebRemark(rs.getString("web_remark"));
								vo.setEmail(rs.getString("email"));
								vo.setOriginOrderId(rs.getInt("origin_order_id"));
								vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
								vo.setNewOrderId(rs.getInt("new_order_id"));
								vo.setStockoutRemark(rs.getString("stockout_remark"));
								vo.setDeliver(rs.getInt("os.deliver"));
								vo.setGroup_num(rs.getString("sbo.group_num"));
								vo.setSerialNumber(rs.getInt("d.serial_number"));
								vo.setBatchNum(rs.getInt("d.batch"));
								vo.setGroupCode(rs.getString("sbo.group_code"));
								OrderStockBean os = new OrderStockBean();
								os.setId(rs.getInt("os.id"));
								os.setStatus(rs.getInt("os.status"));
								os.setStockArea(rs.getInt("os.stock_area"));
								os.setCode(rs.getString("os.code"));

								vo.setOrderStock(os);

								orderList.add(vo);
							}
							if (rs != null) {
								rs.close();
							}
							if (pst != null) {
								pst.close();
							}

							Map productMap = new HashMap();
							List huizongList = null;
							LinkedHashMap huizong = new LinkedHashMap();
							Map huizongNum = new HashMap();
							Map productCodeMap = new HashMap();
							int pruductSum = 0;
							Map productNameMap = new HashMap();

							Map productMap1 = new HashMap();
							List huizongList1 = null;
							LinkedHashMap huizong1 = new LinkedHashMap();
							Map huizongNum1 = new HashMap();
							Map productCodeMap1 = new HashMap();
							Map productNameMap1 = new HashMap();
							int pruductSum1 = 0;
							// 循环订单，查询订单中的商品及其货位
							for (int i = 0; i < orderList.size(); i++) {// 循环订单
								voOrder order = (voOrder) orderList.get(i);
								// if (order.getSerialNumber() == 0) {
								// // 把订单在分拣波次中的序号update到sorting_batch_order
								// siService.updateSortingBatchOrderInfo("group_num="
								// + (i + 1), "order_id=" + order.getId());
								// order.setSerialNumber(i + 1);
								// transactionFlag = true;
								// }

								// 查询订单下的非印刷品产品
								// sql="select * from order_stock_product_cargo ospc "+
								// "join order_stock_product osp on osp.id=ospc.order_stock_product_id "+
								// "join product p on p.id=osp.product_id "+
								// "left join catalog e on e.id=p.parent_id1 "+
								// "where (e.name <>'印刷品' or e.id is null) "+
								// "and osp.order_stock_id=? ";
								String dmSql = "select type_id as id from user_order_package_type where name ='印刷品'";
								ResultSet rsDm = siService.getDbOp().executeQuery(dmSql);
								String isDm = "";
								if (rsDm.next()) {
									isDm = " AND (e.product_type_id<>" + rsDm.getInt("id") + " or e.product_type_id is null)";
								}
								rsDm.close();
								String skuCountSql = "SELECT * FROM order_stock_product_cargo a " + "JOIN order_stock_product b ON b.id=a.order_stock_product_id " + "JOIN product c ON c.id=b.product_id " + "LEFT JOIN product_ware_property e ON c.id=e.product_id " + "WHERE b.order_stock_id=?" + isDm;
								PreparedStatement pst2 = siService.getDbOp().getConn().prepareStatement(skuCountSql);
								pst2.setInt(1, order.getOrderStock().getId());
								// pst2.setInt(2,
								// order.getOrderStock().getId());
								ResultSet rs2 = pst2.executeQuery();
								List orderProductList = new ArrayList();

								while (rs2.next()) {// 把查询出来的产品放到orderProductList中
									voProduct orderProduct = new voProduct();
									List cargo_whole_code = new ArrayList();
									String cargoWholeCode = rs2.getString("cargo_whole_code");
									cargo_whole_code.add(cargoWholeCode);
									orderProduct.setCargoPSList(cargo_whole_code);
									orderProduct.setCode(rs2.getString("code"));// 商品编号
									orderProduct.setBuyCount(rs2.getInt("count"));// 数量
									pruductSum += rs2.getInt("count");
									orderProduct.setPrice(rs2.getInt("price"));// 单价
									orderProduct.setName(rs2.getString("c.name"));// 商品名称
									orderProductList.add(orderProduct);
									// int serialNumber =
									// order.getSerialNumber() == 0 ? (i + 1) :
									// order.getSerialNumber();
									int serialNumber = order.getSerialNumber();
									// 制作汇总单里的数据
									String huizongKey = cargoWholeCode+"_"+orderProduct.getCode();
									if (huizong.keySet().contains(huizongKey)) {// 如果Map中已经有了这个货位，则把订单序号和分拣数量拼进去，例如1(2)
										StringBuffer sb = new StringBuffer();
										sb.append(huizong.get(huizongKey)).append(",").append(order.getGroup_num());
										if (orderProduct.getBuyCount() > 1) {
											sb.append("(" + orderProduct.getBuyCount() + ")");
										}
										String text = sb.toString();
										huizong.put(huizongKey, text);
										huizongNum.put(huizongKey, new Integer(((Integer) huizongNum.get(huizongKey)).intValue() + orderProduct.getBuyCount()));// 保存该SKU商品订购数量
										productCodeMap.put(huizongKey, rs2.getString("c.code"));
										productNameMap.put(huizongKey, orderProduct.getName());
									} else {
										StringBuffer sb = new StringBuffer();
										sb.append(order.getGroup_num());
										if (orderProduct.getBuyCount() > 1) {
											sb.append("(" + orderProduct.getBuyCount() + ")");
										}
										String text = sb.toString();
										huizong.put(huizongKey, text);
										huizongNum.put(huizongKey, new Integer(orderProduct.getBuyCount()));// 保存该SKU商品订购数量
										productCodeMap.put(huizongKey, rs2.getString("c.code"));
										productNameMap.put(huizongKey, orderProduct.getName());
									}
								}
								productMap.put(order.getOrderStock().getId() + "", orderProductList);// 以申请出库的订单ID为key把该订单里的产品放到Map中
								sortingBatchGroupInfo.setProductCount(pruductSum);
								if (rs2 != null) {
									rs2.close();
								}
								if (pst2 != null) {
									pst2.close();
								}
								// 查询订单下的印刷品产品
								String nodmSql = "select type_id as id from user_order_package_type where name ='印刷品'";
								ResultSet norsDm = siService.getDbOp().executeQuery(nodmSql);
								String noDm = "";
								if (norsDm.next()) {
									noDm = " AND e.product_type_id=" + norsDm.getInt("id");
								} else {
									noDm = " AND 1=2 ";
								}
								norsDm.close();
								String skuCountSql1 = "SELECT * FROM order_stock_product_cargo a " + "JOIN order_stock_product b ON b.id=a.order_stock_product_id " + "JOIN product c ON c.id=b.product_id " + "LEFT JOIN product_ware_property e ON c.id=e.product_id " + "WHERE b.order_stock_id=?" + noDm;
								PreparedStatement pst3 = siService.getDbOp().getConn().prepareStatement(skuCountSql1);
								pst3.setInt(1, order.getOrderStock().getId());
								ResultSet rs3 = pst3.executeQuery();
								List orderProductList1 = new ArrayList();

								while (rs3.next()) {// 把查询出来的产品放到orderProductList中
									voProduct orderProduct = new voProduct();
									List cargo_whole_code = new ArrayList();
									String cargoWholeCode = rs3.getString("cargo_whole_code");
									cargo_whole_code.add(cargoWholeCode);
									orderProduct.setCargoPSList(cargo_whole_code);
									orderProduct.setCode(rs3.getString("code"));// 商品编号
									orderProduct.setBuyCount(rs3.getInt("count"));// 数量
									pruductSum1 += rs3.getInt("count");
									orderProduct.setPrice(rs3.getInt("price"));// 单价
									orderProduct.setName(rs3.getString("c.name"));// 商品名称
									orderProductList1.add(orderProduct);
									// int serialNumber =
									// order.getSerialNumber() == 0 ? (i + 1) :
									// order.getSerialNumber();
									int serialNumber = order.getSerialNumber();
									// 制作汇总单里的数据
									String huizongKey = cargoWholeCode+"_"+orderProduct.getCode();
									if (huizong1.keySet().contains(huizongKey)) {// 如果Map中已经有了这个货位，则把订单序号和分拣数量拼进去，例如1(2)
										StringBuffer sb = new StringBuffer();
										sb.append(huizong1.get(huizongKey)).append(",").append(order.getGroup_num());
										if (orderProduct.getBuyCount() > 1) {
											sb.append("(" + orderProduct.getBuyCount() + ")");
										}
										String text = sb.toString();
										huizong1.put(huizongKey, text);
										huizongNum1.put(huizongKey, new Integer(((Integer) huizongNum1.get(huizongKey)).intValue() + orderProduct.getBuyCount()));// 保存该SKU商品订购数量
										productCodeMap1.put(huizongKey, rs3.getString("c.code"));
										productNameMap1.put(huizongKey, orderProduct.getName());
									} else {
										StringBuffer sb = new StringBuffer();
										sb.append(order.getGroup_num());
										if (orderProduct.getBuyCount() > 1) {
											sb.append("(" + orderProduct.getBuyCount() + ")");
										}
										String text = sb.toString();
										huizong1.put(huizongKey, text);
										huizongNum1.put(huizongKey, new Integer(orderProduct.getBuyCount()));// 保存该SKU商品订购数量
										productCodeMap1.put(huizongKey, rs3.getString("c.code"));
										productNameMap1.put(huizongKey, orderProduct.getName());
									}
								}
								productMap1.put(order.getOrderStock().getId() + "", orderProductList1);// 以申请出库的订单ID为key把该订单里的产品放到Map中
								sortingBatchGroupInfo.setProductCount(pruductSum1);
								if (rs3 != null) {
									rs3.close();
								}
								if (pst3 != null) {
									pst3.close();
								}
							}
							// 按照货位号由小到大排序，List里装的是Map.Entry
							huizongList = new ArrayList(huizong.entrySet());
							 Collections.sort(huizongList, new Comparator() {
							 public int compare(Object o1, Object o2) {
							 return (((Map.Entry)
							 o1).getKey()).toString().compareTo(((Map.Entry)
							 o2).getKey().toString());
							 }
							 });
							// 按照货位号由小到大排序，List里装的是Map.Entry
							huizongList1 = new ArrayList(huizong1.entrySet());
							 Collections.sort(huizongList1, new Comparator() {
							 public int compare(Object o1, Object o2) {
							 return (((Map.Entry)
							 o1).getKey()).toString().compareTo(((Map.Entry)
							 o2).getKey().toString());
							 }
							 });
							request.setAttribute("orderList", orderList);
							request.setAttribute("productMap", productMap);
							request.setAttribute("huizongList", huizongList);
							request.setAttribute("huizongNum", huizongNum);
							request.setAttribute("productCodeMap", productCodeMap);
							request.setAttribute("productMap1", productMap1);
							request.setAttribute("huizongList1", huizongList1);
							request.setAttribute("huizongNum1", huizongNum1);
							request.setAttribute("productCodeMap1", productCodeMap1);
							request.setAttribute("userCode", userCode);
							request.setAttribute("sortingBatchGroupId", sortingBatchGroupId);
							request.setAttribute("printType", printType);
							// request.setAttribute("selectedOrder",
							// selectedOrder);
							request.setAttribute("productNameMap", productNameMap);
							request.setAttribute("productNameMap1", productNameMap1);

							if ("all".equals(printType)) {// 打单时，如果是初次打印，则：
								// 1、分拣批次状态变为分拣中---分拣批次状态为未处理（分拣批次的全部状态：0未处理、1处理中、2未分拣、3分拣中、4分拣完成）
								siService.updateSortingBatchInfo("status=3", "id=" + sortingBatchGroupInfo.getSortingBatchId());
								// 2、分拣波次状态变为分拣中。分拣人id、分拣人姓名已经在该方法的开始就写入了
								siService.updateSortingBatchGroupInfo("status=1", "id=" + sortingBatchGroupId);
								// 3、批次订单表中，订单状态为分拣中;
								siService.updateSortingBatchOrderInfo("status=2", "delete_status<>1 and status<>3 and sorting_group_id=" + sortingBatchGroupId);
								transactionFlag = true;
							}
							if (transactionFlag) {
								siService.getDbOp().commitTransaction();
							}

							request.setAttribute("pageFrom", "sortingBatchOrderReceive");
							request.setAttribute("staffCode", staffCode);
							// siService.release(dbOp2);
							// wareService.releaseAll();
						}
					} else {
						success = -1;
						request.setAttribute("msg", "没有待分拣的波次!");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}
				}
				success = 1;
				request.setAttribute("assignedCount1", assignedCount1 + "");
				request.setAttribute("assignedCount2", assignedCount2 + "");
				request.setAttribute("notAssignedCount1", notAssignedCount1 + "");
				request.setAttribute("notAssignedCount2", notAssignedCount2 + "");
				// 员工bean
				request.setAttribute("staffBean", staffBean);
				request.setAttribute("success", success + "");

				// 物流员工绩效考核表操作
				CargoStaffBean csBean = cargoService.getCargoStaff(" code='" + staffCode + "'");
				if (csBean == null) {
					siService.getDbOp().rollbackTransaction();
					request.setAttribute("msg", "此账号不是物流员工 !");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				String type = "0"; // 目前只统计单sku
				CargoStaffPerformanceBean cspBean = null;
				if ("0".equals(type)) {
					cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=0");
				} else if ("1".equals(type)) {
					cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=7");
				}
				int operCount = 1;
				if (cspBean != null) {
					operCount = cspBean.getOperCount() + operCount;
					boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount, " id=" + cspBean.getId());
					if (!flag) {
						siService.getDbOp().rollbackTransaction();
						request.setAttribute("msg", "物流员工绩效考核更新操作失败 !");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}
				} else {
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean.setDate(date);
					bean.setOperCount(1);
					bean.setProductCount(0);
					bean.setStaffId(csBean.getId());
					if ("0".equals(type)) {
						bean.setType(0); // 0代表单SKU分拣作业
					} else if ("1".equals(type)) {
						bean.setType(7); // 7代表多SKU分拣作业
					}
					boolean flag = cargoService.addCargoStaffPerformance(bean);
					if (!flag) {
						siService.getDbOp().rollbackTransaction();
						request.setAttribute("msg", "物流员工绩效考核添加操作失败 !");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}
				}
				// 物流员工作业效率排名
				String firstCount = "";
				String oneselfCount = "";
				String ranking = "";
				String photoUrl = null;
				int n = 1;
				if ("0".equals(type)) {
					cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=0");
					List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=0", -1, -1, " oper_count DESC");
					if (cspBean != null) {
						for (int i = 0; i < cspList.size(); i++) {
							CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
							bean = cspList.get(i);
							if (i == 0) {
								firstCount = bean.getOperCount() + "";
								CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
								photoUrl = winBean.getPhotoUrl();// 获取冠军头像的URl
								if (cspBean.getOperCount() >= bean.getOperCount()) {
									ranking = "排名第" + n;
									oneselfCount = "单SKU波次" + cspBean.getOperCount();
									break;
								} else {
									n++;
								}
							} else {
								if (cspBean.getOperCount() >= bean.getOperCount()) {
									ranking = "排名第" + n;
									oneselfCount = "单SKU波次" + cspBean.getOperCount();
									break;
								} else {
									n++;
								}
							}
						}
					} else {
						if (cspList != null && cspList.size() > 0) {
							CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
							bean = cspList.get(0);
							CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
							photoUrl = winBean.getPhotoUrl();// 获取冠军头像的URl
							firstCount = bean.getOperCount() + "";
							ranking = "尚无名次";
							oneselfCount = "单SKU波次0";
						} else {
							firstCount = "0";
							ranking = "尚无名次";
							oneselfCount = "单SKU波次0";
						}
					}
				} else if ("1".equals(type)) {
					cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + staffBean.getUserId() + " and type=7");
					List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=7", -1, -1, " oper_count DESC");
					if (cspBean != null) {
						for (int i = 0; i < cspList.size(); i++) {
							CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
							bean = cspList.get(i);
							if (i == 0) {
								firstCount = bean.getOperCount() + "";
								CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
								photoUrl = winBean.getPhotoUrl();// 获取冠军头像的URl
								if (cspBean.getOperCount() >= bean.getOperCount()) {
									ranking = "排名第" + n;
									oneselfCount = "多SKU波次" + cspBean.getOperCount();
									break;
								} else {
									n++;
								}
							} else {
								if (cspBean.getOperCount() >= bean.getOperCount()) {
									ranking = "排名第" + n;
									oneselfCount = "多SKU波次" + cspBean.getOperCount();
									break;
								} else {
									n++;
								}
							}
						}
					} else {
						if (cspList != null && cspList.size() > 0) {
							CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
							bean = cspList.get(0);
							CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
							photoUrl = winBean.getPhotoUrl();// 获取冠军头像的URl
							firstCount = bean.getOperCount() + "";
							ranking = "尚无名次";
							oneselfCount = "多SKU波次0";
						} else {
							firstCount = "0";
							ranking = "尚无名次";
							oneselfCount = "多SKU波次0";
						}
					}
				}
				request.getSession().setAttribute("photoUrl", photoUrl);
				request.getSession().setAttribute("firstCount", firstCount);
				request.getSession().setAttribute("oneselfCount", oneselfCount);
				request.getSession().setAttribute("ranking", ranking);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				siService.releaseAll();
				cargoService.releaseAll();
			}
		}
		if ("".equals(printStatus)) {
			return "admin/rec/oper/sorting/sortingBatchGroupPrintLine";
		} else {
			 return "admin/rec/oper/sorting/sortingBatchOrderReceive";
		}
	}
	/**
	 * 分拣波次补打列表
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingBatchGroupPrintList")
	@ResponseBody
	public EasyuiDataGridJson sortingBatchGroupPrintList(HttpServletRequest request, HttpServletResponse response) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		String code = StringUtil.convertNull(request.getParameter("code"));
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		try {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(643)) {
				request.setAttribute("msg", "您没有此权限!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			StringBuffer conditon = new StringBuffer();
			Pattern pattern = Pattern.compile("^[a-zA-Z]+.*$");
			Matcher matcher = pattern.matcher(code);
			boolean b = matcher.matches();
			if (code != null && code.length() > 0 && !code.equals("员工号/分拣波次号/订单号")) {
				if (code.substring(0, 2).equals("FJ")) {// 输入的是分拣波次
					conditon.append(" and a.code='" + code + "'");
				} else if (b == true && !code.substring(0, 2).equals("FJ")) {// 输入的是订单
					conditon.append(" and a.id = (select sorting_group_id from sorting_batch_order where  delete_status<>1 and order_code='" + code + "')");
				} else {// 输入的是员工号
					conditon.append(" and b.code='" + code + "'");
				}
			}
			String sql = " select b.code,b.name,a.code,a.receive_datetime,a.status ,a.id from sorting_batch_group a join cargo_staff b on a.staff_id=b.id where a.status<>3 and a.status<>2" + conditon + " order by a.create_datetime desc";
			ResultSet rs = wareService.getDbOp().executeQuery(sql);
			List staffList = new ArrayList();
			while (rs.next()) {
				SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
				groupBean.setStaffCode(rs.getString(1));
				groupBean.setStaffName(rs.getString(2));
				groupBean.setCode(rs.getString(3));
				groupBean.setReceiveDatetime(rs.getString(4));
				groupBean.setStatus(rs.getInt(5));
				groupBean.setId(rs.getInt(6));
				staffList.add(groupBean);
			}
			rs.close();
			 j.setRows(staffList);
			request.setAttribute("staffList", staffList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return j;
	}
	/**
	 * 波次明细打单
	 */
	@RequestMapping("/sortingBatchGroupPrintLine")
	public String sortingBatchGroupPrintLine( HttpServletRequest request, HttpServletResponse response) throws Exception {

		String userCode = StringUtil.convertNull(request.getParameter("userCode")); // 员工号：userCode
		String sortingBatchGroupId = StringUtil.convertNull(request.getParameter("sortingBatchGroupId")); // 分拣波次id：sortingBatchGroupId
		String printType = StringUtil.convertNull(request.getParameter("printType")); // 打印类型：printType（value--all/huizong/fahuoqingdan/selected）
		String selectedOrder = StringUtil.convertNull(request.getParameter("selectedOrder")); // 选择要打印的selected数组：selectedOrder
		String pageFrom = StringUtil.convertNull(request.getParameter("pageFrom"));

		String success = StringUtil.convertNull(request.getParameter("success"));
		String staffCode = StringUtil.convertNull(request.getParameter("staffCode"));
		// DbOperation dbOp = new DbOperation();
		// dbOp.init("adult_slave");//读取使用从数据库，主数据库是adult
		// WareService wareService = new WareService(dbOp);
		// Connection con = wareService.getDbOp().getConn();
		PreparedStatement pst = null;
		ResultSet rs = null;

		DbOperation dbOp2 = new DbOperation();
		dbOp2.init("adult");// 主数据库是adult
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp2);// 分拣相关的service
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, siService.getDbOp());
		synchronized (cargoLock) {
			siService.getDbOp().startTransaction();
			boolean transactionFlag = false;
			try {
				if ("".equals(sortingBatchGroupId)) {
					request.setAttribute("msg","分拣波次号不能为空！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				CargoStaffBean cargoStaff = null;
				if ("all".equals(printType)) {
					if ("".equals(userCode)) {
						request.setAttribute("msg", "员工号不能为空！");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}
					cargoStaff = (CargoStaffBean) siService.getXXX("code='" + userCode + "'", "cargo_staff", "adultadmin.bean.cargo.CargoStaffBean");
					if (cargoStaff == null || "".equals(cargoStaff)) {
						request.setAttribute("msg",  userCode + "的员工不存在！");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					} else {
						SortingBatchGroupBean sbb = siService.getSortingBatchGroupInfo("id=" + sortingBatchGroupId);
						if (sbb.getStaffId() > 0) {
							request.setAttribute("msg", "此波次已经分配！");
							request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
							return null;
						}
						// 分拣人id staff_id 分拣人姓名 staff_name
						siService.updateSortingBatchGroupInfo("receive_datetime='" + DateUtil.getNow() + "',staff_id=" + cargoStaff.getId() + ",staff_name='" + cargoStaff.getName() + "'", "id=" + sortingBatchGroupId);
						transactionFlag = true;
					}
				}
				if (sortingBatchGroupId != null && sortingBatchGroupId.length() > 0) {
					SortingBatchGroupBean groupBean = siService.getSortingBatchGroupInfo("id=" + sortingBatchGroupId);
					request.setAttribute("groupBean", groupBean);
				}
				CargoStaffBean staffBean = cargoService.getCargoStaff("code='" + staffCode + "'");
				request.setAttribute("success", success);
				request.setAttribute("staffCode", staffCode);
				request.setAttribute("staffBean", staffBean);
				// 加载分拣波次信息--传回页面
				String sql = "select cs.*,sbg.deliver,sbg.code,sbg.create_datetime,cia.name from sorting_batch_group sbg left join cargo_staff cs on cs.id = sbg.staff_id left join cargo_info_area cia on cia.old_id = sbg.storage where sbg.id=?";
				pst = siService.getDbOp().getConn().prepareStatement(sql);
				pst.setInt(1, Integer.parseInt(sortingBatchGroupId));
				rs = pst.executeQuery();
				SortingBatchGroupBean sortingBatchGroupInfo = siService.getSortingBatchGroupInfo("id=" + sortingBatchGroupId);
				while (rs.next()) {
					sortingBatchGroupInfo.setCode(rs.getString("sbg.code"));
					sortingBatchGroupInfo.setReceiveDatetime(rs.getTimestamp("sbg.create_datetime").toString());
					CargoStaffBean cargoStaff2 = new CargoStaffBean();
					cargoStaff2.setCode(rs.getString("cs.code"));
					cargoStaff2.setName(rs.getString("cs.name"));
					sortingBatchGroupInfo.setCargoStaff(cargoStaff2);
					sortingBatchGroupInfo.setStorageName(rs.getString("cia.name"));
					// 快递公司
					// sortingBatchGroupInfo.setDeliverName(voOrder.deliverGdMap.get(rs.getString("sbg.deliver")).toString());

				}

				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}
				// 把参数返回到打印页面
				request.setAttribute("sortingBatchGroupInfo", sortingBatchGroupInfo);
				request.setAttribute("printType", printType);

				// 根据分拣波次号，取得该波次下的订单列表
				StringBuffer sqlSb = new StringBuffer();
				sqlSb.append("select os.*,uo.*,sbo.group_num,sbg.code,d.serial_number,sbo.group_code,d.batch from user_order uo,order_stock os,sorting_batch_order sbo,sorting_batch_group sbg,order_customer d  where sbo.delete_status<>1 and os.status<>3 and d.order_code = uo.code and ");
				if (!"".equals(selectedOrder)) {
					sqlSb.append("uo.id in(").append(selectedOrder).append(") and");
				}
				sqlSb.append(" sbo.sorting_group_id=? and sbo.order_id =os.order_id  and uo.id = os.order_id and sbo.sorting_group_id=sbg.id order by sbo.group_num asc");
				List orderList = new ArrayList();

				pst = siService.getDbOp().getConn().prepareStatement(sqlSb.toString());
				pst.setInt(1, Integer.parseInt(sortingBatchGroupId));
				rs = pst.executeQuery();

				while (rs.next()) {
					voOrder vo = new voOrder();
					// int serialNumber = rs.getInt("sbo.group_num");
					// if (serialNumber != 0) {
					// vo.setSerialNumber(serialNumber);// 序号
					// }
					vo.setId(rs.getInt("uo.id"));
					vo.setName(rs.getString("uo.name"));
					vo.setPhone(rs.getString("phone"));
					vo.setAddress(rs.getString("address"));
					vo.setPostcode(rs.getString("postcode"));
					vo.setBuyMode(rs.getInt("buy_mode"));
					vo.setOperator(rs.getString("operator"));
					vo.setCreateDatetime(rs.getTimestamp("os.create_datetime"));
					vo.setUserId(rs.getInt("user_id"));
					vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
					vo.setStatus(rs.getInt("os.status"));
					vo.setCode(rs.getString("uo.code"));
					vo.setPrice(rs.getFloat("price"));
					vo.setDprice(rs.getFloat("dprice"));
					vo.setDiscount(rs.getFloat("uo.discount"));
					vo.setDeliverType(rs.getInt("deliver_type"));
					vo.setRemitType(rs.getInt("remit_type"));
					vo.setStockout(rs.getInt("stockout"));
					vo.setPhone2(rs.getString("phone2"));
					vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
					vo.setFr(rs.getInt("fr"));
					vo.setAgent(rs.getInt("agent"));
					vo.setAgentMark(rs.getString("agent_mark"));
					vo.setAgentRemark(rs.getString("agent_remark"));
					vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
					vo.setIsReimburse(rs.getInt("is_reimburse"));
					vo.setRealPay(rs.getFloat("real_pay"));
					vo.setPostage(rs.getInt("postage"));
					vo.setIsOrder(rs.getInt("is_order"));
					vo.setImages(rs.getString("images"));
					vo.setAreano(rs.getInt("areano"));
					vo.setPrePayType(rs.getInt("pre_pay_type"));
					vo.setIsOlduser(rs.getInt("is_olduser"));
					vo.setSuffix(rs.getFloat("suffix"));
					vo.setContactTime(rs.getInt("contact_time"));
					vo.setUnitedOrders(rs.getString("united_orders"));
					vo.setRemark(rs.getString("sbg.code"));// 借用，放分拣波次号
					vo.setFlat(rs.getInt("flat"));
					vo.setHasAddPoint(rs.getInt("has_add_point"));
					vo.setGender(rs.getInt("gender"));
					vo.setWebRemark(rs.getString("web_remark"));
					vo.setEmail(rs.getString("email"));
					vo.setOriginOrderId(rs.getInt("origin_order_id"));
					vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
					vo.setNewOrderId(rs.getInt("new_order_id"));
					vo.setStockoutRemark(rs.getString("stockout_remark"));
					vo.setDeliver(rs.getInt("os.deliver"));
					vo.setGroup_num(rs.getString("sbo.group_num"));
					vo.setSerialNumber(rs.getInt("d.serial_number"));
					vo.setBatchNum(rs.getInt("d.batch"));
					vo.setGroupCode(rs.getString("sbo.group_code"));
					OrderStockBean os = new OrderStockBean();
					os.setId(rs.getInt("os.id"));
					os.setStatus(rs.getInt("os.status"));
					os.setStockArea(rs.getInt("os.stock_area"));
					os.setCode(rs.getString("os.code"));

					vo.setOrderStock(os);

					orderList.add(vo);
				}
				if (rs != null) {
					rs.close();
				}
				if (pst != null) {
					pst.close();
				}

				Map productMap = new HashMap();
				List huizongList = null;
				LinkedHashMap huizong = new LinkedHashMap();
				Map huizongNum = new HashMap();
				Map productCodeMap = new HashMap();
				int pruductSum = 0;
				Map productNameMap = new HashMap();

				Map productMap1 = new HashMap();
				List huizongList1 = null;
				LinkedHashMap huizong1 = new LinkedHashMap();
				Map huizongNum1 = new HashMap();
				Map productCodeMap1 = new HashMap();
				Map productNameMap1 = new HashMap();
				int pruductSum1 = 0;
				// 循环订单，查询订单中的商品及其货位
				for (int i = 0; i < orderList.size(); i++) {// 循环订单
					voOrder order = (voOrder) orderList.get(i);
					// if (order.getSerialNumber() == 0) {
					// // 把订单在分拣波次中的序号update到sorting_batch_order
					// siService.updateSortingBatchOrderInfo("group_num=" + (i +
					// 1), "order_id=" + order.getId());
					// order.setSerialNumber(i + 1);
					// transactionFlag = true;
					// }

					// 查询订单下的非印刷品产品
					// sql="select * from order_stock_product_cargo ospc "+
					// "join order_stock_product osp on osp.id=ospc.order_stock_product_id "+
					// "join product p on p.id=osp.product_id "+
					// "left join catalog e on e.id=p.parent_id1 "+
					// "where (e.name <>'印刷品' or e.id is null) "+
					// "and osp.order_stock_id=? ";
					String dmSql = "select type_id as id from user_order_package_type where name ='印刷品'";
					ResultSet rsDm = siService.getDbOp().executeQuery(dmSql);
					String isDm = "";
					if (rsDm.next()) {
						isDm = " AND (e.product_type_id<>" + rsDm.getInt("id") + " or e.product_type_id is null)";
					}
					rsDm.close();
					String skuCountSql = "SELECT * FROM order_stock_product_cargo a " + "JOIN order_stock_product b ON b.id=a.order_stock_product_id " + "JOIN product c ON c.id=b.product_id " + "LEFT JOIN product_ware_property e ON c.id=e.product_id " + "WHERE b.order_stock_id=?" + isDm;
					PreparedStatement pst2 = siService.getDbOp().getConn().prepareStatement(skuCountSql);
					pst2.setInt(1, order.getOrderStock().getId());
					// pst2.setInt(2, order.getOrderStock().getId());
					ResultSet rs2 = pst2.executeQuery();
					List orderProductList = new ArrayList();

					while (rs2.next()) {// 把查询出来的产品放到orderProductList中
						voProduct orderProduct = new voProduct();
						List cargo_whole_code = new ArrayList();
						String cargoWholeCode = rs2.getString("cargo_whole_code");
						cargo_whole_code.add(cargoWholeCode);
						orderProduct.setCargoPSList(cargo_whole_code);
						orderProduct.setCode(rs2.getString("code"));// 商品编号
						orderProduct.setBuyCount(rs2.getInt("count"));// 数量
						pruductSum += rs2.getInt("count");
						orderProduct.setPrice(rs2.getInt("price"));// 单价
						orderProduct.setName(rs2.getString("c.name"));// 商品名称
						orderProductList.add(orderProduct);
						// int serialNumber = order.getSerialNumber() == 0 ? (i
						// + 1) : order.getSerialNumber();
						int serialNumber = order.getSerialNumber();
						// 制作汇总单里的数据
						String huizongKey = cargoWholeCode+"_"+orderProduct.getCode();
						if (huizong.keySet().contains(huizongKey)) {// 如果Map中已经有了这个货位，则把订单序号和分拣数量拼进去，例如1(2)
							StringBuffer sb = new StringBuffer();
							sb.append(huizong.get(huizongKey)).append(",").append(order.getGroup_num());
							if (orderProduct.getBuyCount() > 1) {
								sb.append("(" + orderProduct.getBuyCount() + ")");
							}
							String text = sb.toString();
							huizong.put(huizongKey, text);
							huizongNum.put(huizongKey, new Integer(((Integer) huizongNum.get(huizongKey)).intValue() + orderProduct.getBuyCount()));// 保存该SKU商品订购数量
							productCodeMap.put(huizongKey, rs2.getString("c.code"));
							productNameMap.put(huizongKey, orderProduct.getName());
						} else {
							StringBuffer sb = new StringBuffer();
							sb.append(order.getGroup_num());
							if (orderProduct.getBuyCount() > 1) {
								sb.append("(" + orderProduct.getBuyCount() + ")");
							}
							String text = sb.toString();
							huizong.put(huizongKey, text);
							huizongNum.put(huizongKey, new Integer(orderProduct.getBuyCount()));// 保存该SKU商品订购数量
							productCodeMap.put(huizongKey, rs2.getString("c.code"));
							productNameMap.put(huizongKey, orderProduct.getName());
						}
					}
					productMap.put(order.getOrderStock().getId() + "", orderProductList);// 以申请出库的订单ID为key把该订单里的产品放到Map中
					sortingBatchGroupInfo.setProductCount(pruductSum);
					if (rs2 != null) {
						rs2.close();
					}
					if (pst2 != null) {
						pst2.close();
					}
					// 查询订单下的印刷品产品
					String nodmSql = "select type_id as id from user_order_package_type where name ='印刷品'";
					ResultSet norsDm = siService.getDbOp().executeQuery(nodmSql);
					String noDm = "";
					if (norsDm.next()) {
						noDm = " AND e.product_type_id=" + norsDm.getInt("id");
					} else {
						noDm = " AND 1=2 ";
					}
					norsDm.close();
					String skuCountSql1 = "SELECT * FROM order_stock_product_cargo a " + "JOIN order_stock_product b ON b.id=a.order_stock_product_id " + "JOIN product c ON c.id=b.product_id " + "LEFT JOIN product_ware_property e ON c.id=e.product_id " + "WHERE b.order_stock_id=?" + noDm;
					PreparedStatement pst3 = siService.getDbOp().getConn().prepareStatement(skuCountSql1);
					pst3.setInt(1, order.getOrderStock().getId());
					ResultSet rs3 = pst3.executeQuery();
					List orderProductList1 = new ArrayList();

					while (rs3.next()) {// 把查询出来的产品放到orderProductList中
						voProduct orderProduct = new voProduct();
						List cargo_whole_code = new ArrayList();
						String cargoWholeCode = rs3.getString("cargo_whole_code");
						cargo_whole_code.add(cargoWholeCode);
						orderProduct.setCargoPSList(cargo_whole_code);
						orderProduct.setCode(rs3.getString("code"));// 商品编号
						orderProduct.setBuyCount(rs3.getInt("count"));// 数量
						pruductSum1 += rs3.getInt("count");
						orderProduct.setPrice(rs3.getInt("price"));// 单价
						orderProduct.setName(rs3.getString("c.name"));// 商品名称
						orderProductList1.add(orderProduct);
						// int serialNumber = order.getSerialNumber() == 0 ? (i
						// + 1) : order.getSerialNumber();
						int serialNumber = order.getSerialNumber();
						// 制作汇总单里的数据
						String huizongKey = cargoWholeCode+"_"+orderProduct.getCode();
						if (huizong1.keySet().contains(huizongKey)) {// 如果Map中已经有了这个货位，则把订单序号和分拣数量拼进去，例如1(2)
							StringBuffer sb = new StringBuffer();
							sb.append(huizong1.get(huizongKey)).append(",").append(order.getGroup_num());
							if (orderProduct.getBuyCount() > 1) {
								sb.append("(" + orderProduct.getBuyCount() + ")");
							}
							String text = sb.toString();
							huizong1.put(huizongKey, text);
							huizongNum1.put(huizongKey, new Integer(((Integer) huizongNum1.get(huizongKey)).intValue() + orderProduct.getBuyCount()));// 保存该SKU商品订购数量
							productCodeMap1.put(huizongKey, rs3.getString("c.code"));
							productNameMap1.put(huizongKey, orderProduct.getName());
						} else {
							StringBuffer sb = new StringBuffer();
							sb.append(order.getGroup_num());
							if (orderProduct.getBuyCount() > 1) {
								sb.append("(" + orderProduct.getBuyCount() + ")");
							}
							String text = sb.toString();
							huizong1.put(huizongKey, text);
							huizongNum1.put(huizongKey, new Integer(orderProduct.getBuyCount()));// 保存该SKU商品订购数量
							productCodeMap1.put(huizongKey, rs3.getString("c.code"));
							productNameMap1.put(huizongKey, orderProduct.getName());
						}
					}
					productMap1.put(order.getOrderStock().getId() + "", orderProductList1);// 以申请出库的订单ID为key把该订单里的产品放到Map中
					sortingBatchGroupInfo.setProductCount(pruductSum1);
					if (rs3 != null) {
						rs3.close();
					}
					if (pst3 != null) {
						pst3.close();
					}
				}
				// 按照货位号由小到大排序，List里装的是Map.Entry
				huizongList = new ArrayList(huizong.entrySet());
				 Collections.sort(huizongList, new Comparator() {
				 public int compare(Object o1, Object o2) {
				 return (((Map.Entry)
				 o1).getKey()).toString().compareTo(((Map.Entry)
				 o2).getKey().toString());
				 }
				 });
				// 按照货位号由小到大排序，List里装的是Map.Entry
				huizongList1 = new ArrayList(huizong1.entrySet());
				 Collections.sort(huizongList1, new Comparator() {
				 public int compare(Object o1, Object o2) {
				 return (((Map.Entry)
				 o1).getKey()).toString().compareTo(((Map.Entry)
				 o2).getKey().toString());
				 }
				 });
				request.setAttribute("orderList", orderList);
				request.setAttribute("productMap", productMap);
				request.setAttribute("huizongList", huizongList);
				request.setAttribute("huizongNum", huizongNum);
				request.setAttribute("productCodeMap", productCodeMap);
				request.setAttribute("productMap1", productMap1);
				request.setAttribute("huizongList1", huizongList1);
				request.setAttribute("huizongNum1", huizongNum1);
				request.setAttribute("productCodeMap1", productCodeMap1);
				request.setAttribute("userCode", userCode);
				request.setAttribute("sortingBatchGroupId", sortingBatchGroupId);
				request.setAttribute("printType", printType);
				request.setAttribute("selectedOrder", selectedOrder);
				request.setAttribute("productNameMap", productNameMap);
				request.setAttribute("productNameMap1", productNameMap1);
				if ("all".equals(printType)) {// 打单时，如果是初次打印，则：
					// 1、分拣批次状态变为分拣中---分拣批次状态为未处理（分拣批次的全部状态：0未处理、1处理中、2未分拣、3分拣中、4分拣完成）
					siService.updateSortingBatchInfo("status=3", "id=" + sortingBatchGroupInfo.getSortingBatchId());
					// 2、分拣波次状态变为分拣中。分拣人id、分拣人姓名已经在该方法的开始就写入了
					siService.updateSortingBatchGroupInfo("status=1", "id=" + sortingBatchGroupId);
					// 3、批次订单表中，订单状态为分拣中
					siService.updateSortingBatchOrderInfo("status=2", "delete_status<>1 and status<>3 and sorting_group_id=" + sortingBatchGroupId);
					transactionFlag = true;
				}
				if (transactionFlag) {
					siService.getDbOp().commitTransaction();
				}

				siService.release(dbOp2);
				// wareService.releaseAll();
			} catch (Exception e) {
				e.printStackTrace();
				if (transactionFlag) {
					siService.getDbOp().rollbackTransaction();
				}
				siService.release(dbOp2);
				// wareService.releaseAll();c
			}finally{
				dbOp2.release();
			}

			// 把波次列表的当前页传回去.
			request.setAttribute("pageIndex", StringUtil.StringToId(request.getParameter("pageIndex")));
			request.setAttribute("pageFrom", pageFrom);
			
				return "admin/rec/oper/sorting/sortingBatchGroupPrintLine";
			
		}
	}

	/**
	 * 
	 * 分拣波次列表（订单列表）
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingBatchGroupDetail")
	@ResponseBody
	public EasyuiDataGridJson sortingBatchGroupDetail(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService isService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService icService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String group_id = StringUtil.convertNull(request.getParameter("groupId"));
		String deliver = StringUtil.convertNull(request.getParameter("deliver"));
		String code = StringUtil.convertNull(request.getParameter("text"));
		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		String page = StringUtil.convertNull(request.getParameter("page"));
		String rows = StringUtil.convertNull(request.getParameter("rows"));
		String sort = StringUtil.convertNull(request.getParameter("sort"));
		String order = StringUtil.convertNull(request.getParameter("order"));
		UserGroupBean group = admin.getGroup();
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		try {
			if (!group.isFlag(591)) {
				request.setAttribute("msg", "您没有此权限!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
				SortingBatchGroupBean sbgBean = (SortingBatchGroupBean)siService.getSortingBatchGroupInfo("id="+group_id);
				int SKUcount = 0;
				int productCount = 0;
				String sqlString = "select count(distinct d.product_code),sum(stockout_count) from sorting_batch_group as a " + "" + "left join sorting_batch_order as b on a.id=b.sorting_group_id " + "" + "left join order_stock as c" + " on b.order_id =c.order_id " + "" + "left join order_stock_product as d on c.id=d.order_stock_id " + "where b.delete_status<>1 and c.status<>3 and a.id=" + sbgBean.getId();
				ResultSet rs = icService.getDbOp().executeQuery(sqlString);
				if (rs.next()) {
					SKUcount = rs.getInt(1);
					productCount = rs.getInt(2);
				}
				rs.close();
				List orderList = siService.getSortingBatchOrderList("delete_status<>1 and sorting_group_id=" + sbgBean.getId(), -1, -1, null);
				List orderList1 = new ArrayList();
				for (int j = 0; j < orderList.size(); j++) {
					SortingBatchOrderBean sboBean = (SortingBatchOrderBean) orderList.get(j);
					sboBean.setStatusName(sboBean.getStatusName(sboBean.getStatus()));
					if(sboBean.getDeliver()==0){
						sboBean.setDeliverName("未分配");
					}else{
						sboBean.setDeliverName(voOrder.deliverMapAll.get(sboBean.getDeliver()+"")+"");
					}
					sboBean.setOrderTypeName(voOrder.productTypeMap.get(sboBean.getOrderType()+"")+"");
					OrderStockBean osBean = isService.getOrderStock("order_id=" + sboBean.getOrderId() + " and status!=3");
					if (osBean == null) {
						continue;
					}
					List ospList = isService.getOrderStockProductList("order_stock_id=" + osBean.getId(), -1, -1, null);
					List ospcList = new ArrayList();
					if (ospList.size() != 0) {
						sboBean.setProductCount(ospList.size());
						String productCargo = new String();
						for (int k = 0; k < ospList.size(); k++) {
							OrderStockProductBean ospBean = (OrderStockProductBean) ospList.get(k);
							List cargoList = isService.getOrderStockProductCargoList("order_stock_product_id=" + ospBean.getId(), -1, -1, null);
							for (int c = 0; c < cargoList.size(); c++) {
								OrderStockProductCargoBean tempOspc = (OrderStockProductCargoBean) cargoList.get(c);
								productCargo = productCargo + ospBean.getProductCode()+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+tempOspc.getCargoWholeCode()+"<br>";
								//sboBean.setCargoCode(tempOspc.getCargoWholeCode());
							}
						}
						sboBean.setProductCode(productCargo);
					}
					orderList1.add(sboBean);
				}
			json.setRows(orderList1);// 设置返回的行
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return json;
	}
	/**
	 * 
	 * 分拣波次列表（波次信息）
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingBatchGroupInfo")
	@ResponseBody
	public EasyuiDataGridJson sortingBatchGroupInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService isService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService icService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String batchId = StringUtil.convertNull(request.getParameter("batchId"));
		String deliver = StringUtil.convertNull(request.getParameter("deliver"));
		String code = StringUtil.convertNull(request.getParameter("text"));
		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		String page = StringUtil.convertNull(request.getParameter("page"));
		String rows = StringUtil.convertNull(request.getParameter("rows"));
		String sort = StringUtil.convertNull(request.getParameter("sort"));
		String order = StringUtil.convertNull(request.getParameter("order"));
		UserGroupBean group = admin.getGroup();
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		try {
			if (!group.isFlag(591)) {
				request.setAttribute("msg", "您没有此权限!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			SortingBatchBean bean = siService.getSortingBatchInfo("id=" + batchId);
			StringBuffer sql = new StringBuffer();
			StringBuilder url = new StringBuilder();
			url.append("sortingAction.do?method=sortingBatchGroupDetail&batchId=" + batchId);
			if (deliver != null && deliver.length() != 0 && !deliver.equals("-1")) {
				sql.append(" and type2=" + deliver);
				url.append("&deliver=" + deliver);
			}
			if (code != null && code.length() != 0 && !code.equals("请输入分拣波次号/订单号")) {
				if (code.substring(0, 2).equals("FJ") && code.length() >= 15) {// 说明输入的是波次编号
					sql.append(" and code='" + code + "'");
					url.append("&query=" + code);
				}else if(code.substring(0, 2).equals("CK")){
					sql.append(" and id in(select sorting_group_id from sorting_batch_order a join order_stock b on a.order_id =b.order_id where b.code='" + code + "' and a.sorting_batch_id=" + batchId + ")");
					url.append("&query=" + code);
				}else {
					sql.append(" and id in(select sorting_group_id from sorting_batch_order where order_code='" + code + "' and sorting_batch_id=" + batchId + ")");
					url.append("&query=" + code);
				}
			}

			int totalCount = siService.getSortingBatchGroupCount("sorting_batch_id=" + batchId + sql);
			json.setTotal(Long.valueOf(totalCount));// 设置返回的行
			EasyuiDataGrid easyUiPage = new EasyuiDataGrid();
			easyUiPage.setOrder(order);
			easyUiPage.setPage(StringUtil.toInt(page));
			easyUiPage.setRows(StringUtil.toInt(rows));
			easyUiPage.setSort(sort);
			List groupList = siService.getSortingBatchGroupList("sorting_batch_id=" + batchId + sql, (easyUiPage.getPage()-1)*easyUiPage.getRows(), easyUiPage.getRows(), null);
			List list = new ArrayList();
			int count = 0;
			int count1 = 0;
			int count2 = 0;
			for (int i = 0; i < groupList.size(); i++) {
				SortingBatchGroupBean sbgBean = (SortingBatchGroupBean) groupList.get(i);
				int SKUcount = 0;
				int productCount = 0;
				String sqlString = "select count(distinct d.product_code),sum(stockout_count) from sorting_batch_group as a " + "" + "left join sorting_batch_order as b on a.id=b.sorting_group_id " + "" + "left join order_stock as c" + " on b.order_id =c.order_id " + "" + "left join order_stock_product as d on c.id=d.order_stock_id " + "where b.delete_status<>1 and c.status<>3 and a.id=" + sbgBean.getId();
				ResultSet rs = icService.getDbOp().executeQuery(sqlString);
				if (rs.next()) {
					SKUcount = rs.getInt(1);
					productCount = rs.getInt(2);
				}
				rs.close();
				int orderCount = siService.getSortingBatchOrderCount("delete_status<>1 and sorting_group_id="+sbgBean.getId());
				count = siService.getSortingBatchGroupCount(" status =0 and sorting_batch_id=" + batchId);
				count1 = siService.getSortingBatchGroupCount("  status =1 and sorting_batch_id=" + batchId);
				count2 = siService.getSortingBatchGroupCount(" status =2 and sorting_batch_id=" + batchId);
				CargoStaffBean staffBean = icService.getCargoStaff("id=" + sbgBean.getStaffId());
				CargoInfoAreaBean cargoBean = icService.getCargoInfoArea("old_id=" + sbgBean.getStorage());
				sbgBean.setStorageName(cargoBean.getName());
				    sbgBean.setOrderCount(orderCount);
				    sbgBean.setSkuCount(SKUcount);
				    sbgBean.setProductCount(productCount);
				if (staffBean != null) {
					sbgBean.setStaffName(staffBean.getName());
				}
			}
			request.setAttribute("bean", bean);
			request.setAttribute("batchId", batchId);
			request.setAttribute("count", count + "");// 未打单订单数量
			request.setAttribute("count1", count1 + "");// 分拣中订单数量
			request.setAttribute("count2", count2 + "");// 已完成订单数量
			Map productTypeMap = new HashMap();
			String sql2 = "select distinct type_id as id,name from user_order_package_type";
			ResultSet rs2 = icService.getDbOp().executeQuery(sql2);
			while (rs2.next()) {
				productTypeMap.put(rs2.getInt("id") + "", rs2.getString("name"));
			}
			rs2.close();
			request.setAttribute("productTypeMap", productTypeMap);
			List footer = new ArrayList();
			HashMap map =new HashMap();
			map.put("batch", bean.getCode());
			map.put("createTime", bean.getCreateDatetime());
			map.put("statusName", bean.getStatusName(bean.getStatus()));
			map.put("completeTime", bean.getCompleteDatetime());
			map.put("completeCount", count2);
			map.put("sortingCount", count1);
			map.put("noPrintCount", count);
			footer.add(map);
			json.setFooter(footer);
			json.setRows(groupList);// 设置返回的行
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return json;
	}
	/**
	 * 
	 *  分拣波次列表（批次信息）
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/sortingBatchInfo")
	@ResponseBody
	public EasyuiDataGridJson sortingBatchInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService isService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService icService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String batchId = StringUtil.convertNull(request.getParameter("batchId"));
		String deliver = StringUtil.convertNull(request.getParameter("deliver"));
		String code = StringUtil.convertNull(request.getParameter("text"));
		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		String page = StringUtil.convertNull(request.getParameter("page"));
		String rows = StringUtil.convertNull(request.getParameter("rows"));
		String sort = StringUtil.convertNull(request.getParameter("sort"));
		String order = StringUtil.convertNull(request.getParameter("order"));
		UserGroupBean group = admin.getGroup();
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		try {
			if (!group.isFlag(591)) {
				request.setAttribute("msg", "您没有此权限!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			SortingBatchBean bean = siService.getSortingBatchInfo("id=" + batchId);
			StringBuffer sql = new StringBuffer();
			StringBuilder url = new StringBuilder();
			url.append("sortingAction.do?method=sortingBatchGroupDetail&batchId=" + batchId);
			if (deliver != null && deliver.length() != 0 && !deliver.equals("-1")) {
				sql.append(" and type2=" + deliver);
				url.append("&deliver=" + deliver);
			}
			if (code != null && code.length() != 0 && !code.equals("请输入分拣波次号/订单号")) {
				if (code.substring(0, 2).equals("FJ") && code.length() >= 15) {// 说明输入的是波次编号
					sql.append(" and code='" + code + "'");
					url.append("&query=" + code);
				}else if(code.substring(0, 2).equals("CK")){
					sql.append(" and id in(select sorting_group_id from sorting_batch_order a join order_stock b on a.order_id =b.order_id where b.code='" + code + "' and a.sorting_batch_id=" + batchId + ")");
					url.append("&query=" + code);
				}else {
					sql.append(" and id in(select sorting_group_id from sorting_batch_order where order_code='" + code + "' and sorting_batch_id=" + batchId + ")");
					url.append("&query=" + code);
				}
			}

			int totalCount = siService.getSortingBatchGroupCount("sorting_batch_id=" + batchId + sql);
			json.setTotal(Long.valueOf(totalCount));// 设置返回的行
			EasyuiDataGrid easyUiPage = new EasyuiDataGrid();
			easyUiPage.setOrder(order);
			easyUiPage.setPage(StringUtil.toInt(page));
			easyUiPage.setRows(StringUtil.toInt(rows));
			easyUiPage.setSort(sort);
			List groupList = siService.getSortingBatchGroupList("sorting_batch_id=" + batchId + sql, -1,-1, null);
			List list = new ArrayList();
			int count = 0;
			int count1 = 0;
			int count2 = 0;
			for (int i = 0; i < groupList.size(); i++) {
				SortingBatchGroupBean sbgBean = (SortingBatchGroupBean) groupList.get(i);
				int SKUcount = 0;
				int productCount = 0;
				String sqlString = "select count(distinct d.product_code),sum(stockout_count) from sorting_batch_group as a " + "" + "left join sorting_batch_order as b on a.id=b.sorting_group_id " + "" + "left join order_stock as c" + " on b.order_id =c.order_id " + "" + "left join order_stock_product as d on c.id=d.order_stock_id " + "where b.delete_status<>1 and c.status<>3 and a.id=" + sbgBean.getId();
				ResultSet rs = icService.getDbOp().executeQuery(sqlString);
				if (rs.next()) {
					SKUcount = rs.getInt(1);
					productCount = rs.getInt(2);
				}
				rs.close();
				List orderList = siService.getSortingBatchOrderList("delete_status<>1 and sorting_group_id=" + sbgBean.getId(), -1, -1, null);
				List orderList1 = new ArrayList();
				for (int j = 0; j < orderList.size(); j++) {
					SortingBatchOrderBean sboBean = (SortingBatchOrderBean) orderList.get(j);
					OrderStockBean osBean = isService.getOrderStock("order_id=" + sboBean.getOrderId() + " and status!=3");
					if (osBean == null) {
						continue;
					}
					List ospList = isService.getOrderStockProductList("order_stock_id=" + osBean.getId(), -1, -1, null);
					List ospcList = new ArrayList();
					if (ospList.size() != 0) {
						for (int k = 0; k < ospList.size(); k++) {
							OrderStockProductBean ospBean = (OrderStockProductBean) ospList.get(k);
							List cargoList = isService.getOrderStockProductCargoList("order_stock_product_id=" + ospBean.getId(), -1, -1, null);
							for (int c = 0; c < cargoList.size(); c++) {
								OrderStockProductCargoBean tempOspc = (OrderStockProductCargoBean) cargoList.get(c);
								String tempCode = "";
								if (ospBean.getCargoCode() != null) {
									tempCode = ospBean.getCargoCode();
								}
								if (c != 0) {
									tempCode += "<br/>";
								}
								tempCode += tempOspc.getCargoWholeCode();
								ospBean.setCargoCode(tempCode);
							}
							ospcList.add(ospBean);
						}
						sboBean.setProductList(ospcList);

					}
					orderList1.add(sboBean);
				}
				count = siService.getSortingBatchGroupCount(" status =0 and sorting_batch_id=" + batchId);
				count1 = siService.getSortingBatchGroupCount("  status =1 and sorting_batch_id=" + batchId);
				count2 = siService.getSortingBatchGroupCount(" status =2 and sorting_batch_id=" + batchId);
				CargoStaffBean staffBean = icService.getCargoStaff("id=" + sbgBean.getStaffId());
				if (staffBean != null) {
					sbgBean.setStaffName(staffBean.getName());
				}
				sbgBean.setOrderList(orderList1);
				sbgBean.setOrderCount(orderList.size());
				sbgBean.setSkuCount(SKUcount);
				sbgBean.setProductCount(productCount);
				list.add(sbgBean);
			}
			if (bean != null) {
				bean.setGroupList(list);
				CargoInfoAreaBean cargoBean = icService.getCargoInfoArea("old_id=" + bean.getStorage());
				bean.setStorageName(cargoBean.getName());
			}
			request.setAttribute("bean", bean);
			request.setAttribute("batchId", batchId);
			request.setAttribute("count", count + "");// 未打单订单数量
			request.setAttribute("count1", count1 + "");// 分拣中订单数量
			request.setAttribute("count2", count2 + "");// 已完成订单数量
			Map productTypeMap = new HashMap();
			String sql2 = "select distinct type_id as id,name from user_order_package_type";
			ResultSet rs2 = icService.getDbOp().executeQuery(sql2);
			while (rs2.next()) {
				productTypeMap.put(rs2.getInt("id") + "", rs2.getString("name"));
			}
			rs2.close();
			request.setAttribute("productTypeMap", productTypeMap);
			List footer = new ArrayList();
			HashMap map =new HashMap();
			map.put("batch", bean.getCode());
			map.put("createTime", bean.getCreateDatetime());
			map.put("statusName", bean.getStatusName(bean.getStatus()));
			map.put("completeTime", bean.getCompleteDatetime());
			map.put("completeCount", count2);
			map.put("sortingCount", count1);
			map.put("noPrintCount", count);
			footer.add(map);
			json.setFooter(footer);
			json.setRows(list);// 设置返回的行
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.releaseAll();
		}
		return json;
	}
	/**
	 *返回产品分类列表
	 * @throws SQLException 
	 */
	@RequestMapping("/getProductTypeList")
	@ResponseBody
	public String getProductTypeList(ActionMapping mapping,HttpServletRequest request, HttpServletResponse response) throws SQLException {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		String sql2 = "select distinct type_id as id,name from user_order_package_type";
		ResultSet rs2 = wareService.getDbOp().executeQuery(sql2);
		List listId =new ArrayList();
		List listName =new ArrayList();
		while (rs2.next()) {
			listId.add(rs2.getInt("id"));
			listName.add(rs2.getString("name"));
		}
		rs2.close();
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (listId != null) {
			for (int i = 0; i < listId.size(); i++) {
				sb.append("{\"id\":\"").append(listId.get(i)).append("\",\"productTypeName\":\"").append(listName.get(i)).append("\"}");
				if (i == listId.size()-1) {
				} else {
					sb.append(",");
				}
			}
		}
		sb.append("]");
		wareService.releaseAll();
		return sb.toString();
	}
	/**
	 *返回产品分类列表
	 * @throws SQLException 
	 */
	@RequestMapping("/getProductTypeList1")
	@ResponseBody
	public String getProductTypeList1(ActionMapping mapping,HttpServletRequest request, HttpServletResponse response) throws SQLException {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		String sql2 = "select distinct type_id as id,name from user_order_package_type";
		ResultSet rs2 = wareService.getDbOp().executeQuery(sql2);
		List listId =new ArrayList();
		List listName =new ArrayList();
		while (rs2.next()) {
			listId.add(rs2.getInt("id"));
			listName.add(rs2.getString("name"));
		}
		rs2.close();
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("{\"id\":\"0\",\"productTypeName\":\"未选择\"},");
		if (listId != null) {
			for (int i = 0; i < listId.size(); i++) {
				sb.append("{\"id\":\"").append(listId.get(i)).append("\",\"productTypeName\":\"").append(listName.get(i)).append("\"}");
				if (i == listId.size()-1) {
				} else {
					sb.append(",");
				}
			}
		}
		sb.append("]");
		wareService.releaseAll();
		return sb.toString();
	}
	/**
	 * 批次订单页批量修改产品分类
	 */
	@RequestMapping("/modifyOrder")
	public String modifyOrder( HttpServletRequest request, HttpServletResponse response) throws Exception {
		IAdminService iService = ServiceFactory.createAdminServiceLBJ();
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, iService.getDbOperation());
		IStockService isService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, iService.getDbOperation());
		String orderIds = request.getParameter("ids");
		String orderTypes = request.getParameter("orderTypes");
        String orderid[] = orderIds.split("-");
        String productType[] = orderTypes.split("-");
		String batchId = StringUtil.convertNull(request.getParameter("batchId"));// 批次ID
		synchronized (cargoLock) {
			try {
				voUser user = (voUser) request.getSession().getAttribute("userView");
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(594)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				SortingBatchBean batch = new SortingBatchBean();
				batch = siService.getSortingBatchInfo("id=" + batchId);
				if (batch == null) {
					request.setAttribute("msg", "未找到对应批次！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				if (orderid == null) {
					request.setAttribute("msg", "请选择最少一个订单");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
					
				}
				if (orderid != null && !orderid.equals("0")) {
					for(int i=0;i<orderid.length;i++){
						StringBuffer sql = new StringBuffer();
						StringBuffer sql1 = new StringBuffer();
						if (sql.length() != 0) {
							sql.append(",order_type=" + productType[i]);
							sql1.append(",product_type=" + productType[i]);
						} else {
							sql.append("order_type=" + productType[i]);
							sql1.append("product_type=" + productType[i]);
							
						}
						SortingBatchOrderBean orderBean = siService.getSortingBatchOrderInfo("id=" + orderid[i]);
						if (orderBean.getStatus() == 3 || orderBean.getDeleteStatus() == 1) {
							continue;
						} else {
							siService.updateSortingBatchOrderInfo(sql.toString(), "id=" + orderid[i]) ;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				iService.close();
			}
		}
		request.setAttribute("batchId", batchId);
		return  "admin/rec/oper/sorting/sortingOrderList";

	}
	/*
	 * 批量修改快递公司
	 */
	@RequestMapping("/modifyOrderDeliver")
	public String modifyOrderDeliver( HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser loginUser = (voUser) request.getSession().getAttribute("userView");
		if (loginUser == null) {
			request.setAttribute("msg", "当前没有登录，添加失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		// 根据订单号，批量修改快递公司
		synchronized (cargoLock) {
			String batchId = StringUtil.convertNull(request.getParameter("batchId"));
			String temp = StringUtil.convertNull(request.getParameter("temp"));
			WareService wareService = new WareService();
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			int result = 0, failure = 0;
			Statement st = null;
			voUser user = (voUser) request.getSession().getAttribute("userView");
			UserGroupBean group = user.getGroup();
			try {
				if (!group.isFlag(594)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				StringBuffer update = new StringBuffer();
				StringBuffer error = new StringBuffer();
				String data = request.getParameter("ordersDeliver");
				if (data == null || data.length() == 0) {
					request.setAttribute("msg", "单号和快递信息不能为空！");
					return  "admin/rec/err";
				} else {
					Connection conn = wareService.getDbOp().getConn();
					st = conn.createStatement();
					BufferedReader read = new BufferedReader(new StringReader(data));
					String tmp = null;
					int i = 1;
					while ((tmp = read.readLine()) != null) {
						wareService.getDbOp().startTransaction();
						String[] str = tmp.trim().split("\\s");
						if (str.length < 2) {
							str = tmp.trim().split("\\t");
						}
						if (str.length >= 2) {
							String orderCode = str[0];
							String deliverName = str[1];
							SortingBatchOrderBean sboBean = siService.getSortingBatchOrderInfo("order_code='" + orderCode + "' and delete_status<>1");
							//if(sboBean!=null)
							if (orderCode != null && deliverName != null && sboBean!=null) {
							SortingBatchBean batchBean = siService.getSortingBatchInfo("id=" + sboBean.getSortingBatchId());
								int deliver = 0;
								// 得到快递公司
								if (voOrder.deliverChangeMap.containsValue(deliverName)) {
									Set set = voOrder.deliverChangeMap.entrySet();
									Iterator it = set.iterator();
									while (it.hasNext()) {
										Map.Entry entry = (Map.Entry) it.next();
										if (entry.getValue().equals(deliverName)) {
											deliver = Integer.parseInt(entry.getKey().toString());
										}
									}
								}
								voOrder userOrder = wareService.getOrder(" code='" + orderCode + "'");

								if (userOrder == null) {
									error.append("<font color=\"red\">").append(orderCode).append("</font> ").append(deliverName).append("<br/>");
									wareService.getDbOp().rollbackTransaction();
									failure++;
									continue;
								} else if (deliver == 0) {
									error.append(orderCode).append(" <font color=\"red\">").append(deliverName).append("</font><br/>");
									wareService.getDbOp().rollbackTransaction();
									failure++;
									continue;
								} else {
									//if (temp.equals("0")) {
										if (sboBean != null) {
											if (sboBean.getDeleteStatus() == 1) {
												error.append("<font color=\"red\">").append(orderCode).append("</font> ").append(deliverName).append("<br/>");
												wareService.getDbOp().rollbackTransaction();
												failure++;
												continue;
											} else {
												// 该订单所处的批次的状态是分拣中并且，并且该批次中存在分拣中之前的订单，则该订单不能被修改快递公司
													int orderCount = siService.getSortingBatchOrderCount("sorting_batch_id=" + sboBean.getSortingBatchId() + " and delete_status<>1 and status in(0,1)");// 该批次未打单的订单数
													int orderCount2 = siService.getSortingBatchOrderCount("sorting_batch_id=" + sboBean.getSortingBatchId() + " and delete_status<>1 and status in(2,3)");// 该批次已打单的订单数

													if (orderCount != 0 && orderCount2 != 0) {// 该批次下有未打单的也有已打单的，则不能修改快递公司
														error.append("<font color=\"red\">").append(orderCode).append("</font> ").append(deliverName).append("<br/>");
														wareService.getDbOp().rollbackTransaction();
														failure++;
														continue;
													}

												siService.updateSortingBatchOrderInfo("deliver=" + deliver, "order_id='" + sboBean.getOrderId() + "' and delete_status<>1");
												SortingBatchBean sbBean = siService.getSortingBatchInfo("id=" + sboBean.getSortingBatchId());
												if (sbBean != null) {
													if (sbBean.getStatus() == SortingBatchBean.STATUS0) {
														if (!siService.updateSortingBatchInfo("status=1", "id=" + batchId)) {
															request.setAttribute("msg", "操作失败!");
															request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
															wareService.getDbOp().rollbackTransaction();
															return null;
															//return mapping.findForward(IConstants.FAILURE_KEY);
														}
													}
												}

											}
										}
									//}
									OrderStockBean orderstockBean = service.getOrderStock("status<>3 and order_code = '" + orderCode + "'");
									String orderStockCode = "";
									if (orderstockBean == null) {
										orderStockCode = "出库单已删除!";
									} else {
										orderStockCode = orderstockBean.getCode();
										update.append(" update order_stock set deliver=");
										update.append(deliver);
										update.append(" where id=").append(orderstockBean.getId());
										st.addBatch(update.toString());
										update.delete(0, update.length());
									}
									update.append(" update user_order set deliver=");
									update.append(deliver);
									update.append(" where code='").append(orderCode).append("'");
									st.addBatch(update.toString());
									update.delete(0, update.length());


									// 修改核对包裹记录
									update.append(" update audit_package set deliver=");
									update.append(deliver);
									update.append(" where order_code='").append(orderCode).append("'");
									st.addBatch(update.toString());
									update.delete(0, update.length());
									i++;
									// 添加快递公司修改日志

									String firstDeilver = "";
									if (userOrder.getDeliver() == 0) {
										firstDeilver = "空";
									} else {
										firstDeilver = "" + userOrder.getDeliverMapAll().get(String.valueOf(userOrder.getDeliver()));
									}
									StockAdminHistoryBean log = new StockAdminHistoryBean();
									log.setAdminId(loginUser.getId());
									log.setAdminName(loginUser.getUsername());
									log.setLogId(orderstockBean.getId());
									log.setLogType(StockAdminHistoryBean.ORDER_DELIVER);
									log.setOperDatetime(DateUtil.getNow());
									log.setRemark("订单号(" + userOrder.getCode() + ")出库单编号 :(" + orderStockCode + ")快递公司(" + firstDeilver + "-" + voOrder.deliverMapAll.get(String.valueOf(deliver)) + ")");
									log.setType(StockAdminHistoryBean.CHANGE);
									service.addStockAdminHistory(log);
								}
								if (i % 100 == 0) {
									st.executeBatch();
									result += i - 1;
									i = 1;
								}
							}
						} else if (str.length == 1 && str[0].length() > 0) {
							wareService.getDbOp().rollbackTransaction();
							failure++;
							continue;
						} else {
							wareService.getDbOp().rollbackTransaction();
							failure++;
							continue;
						}
						wareService.getDbOp().commitTransaction();
					}
					if (i != 1) {
						st.executeBatch();
						result += i - 1;
					}
					String str = "<font color='red'>快递公司修改成功" + result + "个，失败" + failure + "个！</font><br/>";
					request.setAttribute("str", str);
					error.insert(0, str);
				}
			} catch (Exception e) {
				e.printStackTrace();
				wareService.getDbOp().rollbackTransaction();
				request.setAttribute("result", e.getMessage());
			} finally {
				if (st != null)
					st.close();
				wareService.releaseAll();
			}
			if (temp != null && temp.equals("1")) {
//				ActionForward actionForward = new ActionForward("/admin/sortingAction.do?method=noSortingBatchOrderList&temp=1&result=" + result + "&failure=" + failure);
//				actionForward.setRedirect(true);
				request.setAttribute("batchId", batchId);
				return  "admin/rec/oper/sorting/sortingOrderList";
			} else {
//				ActionForward actionForward = new ActionForward("/admin/sortingAction.do?method=sortingBatchOrderList&flag=1&batchId=" + batchId + "&result=" + result + "&failure=" + failure);
//				actionForward.setRedirect(true);
				request.setAttribute("batchId", batchId);
				return  "admin/rec/oper/sorting/sortingOrderList";
			}
		}
	}
	/*
	 * 把剩余未分配订单分给EMS（省内、省外） 广东省速递局-广东省内 广东省外-广东省外
	 */
	@RequestMapping("/modifyOrderDeliverToEMS")
	public String modifyOrderDeliverToEMS(HttpServletRequest request, HttpServletResponse response) throws Exception {
		SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, null);
		WareService wareService = new WareService();
		SortingInfoService sortingService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		String batchId = StringUtil.convertNull(request.getParameter("batchId"));// 批次ID
		voUser loginUser = (voUser) request.getSession().getAttribute("userView");
		int shengwaiCount = 0;
		int shengneiCount = 0;
		synchronized (cargoLock) {
			try {
				if ("".equals(batchId) || batchId.length() == 0) {
					request.setAttribute("msg", "批次号不能为空");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				SortingBatchBean batch = sortingService.getSortingBatchInfo("id=" + batchId);
				if (batch == null) {
					request.setAttribute("msg", "没有找到该批次！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				if (batch.getStatus() == SortingBatchBean.STATUS4) {
					request.setAttribute("msg", "该批次已完成，不能修改！");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;				}
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT c.id,b.code,c.code,b.id,b.address,b.deliver FROM sorting_batch_order a " + "JOIN user_order b ON a.order_id=b.id join order_stock c on b.id=c.order_id where a.deliver=0 and c.status<>3 and a.delete_status<>1");
				if (batchId != null) {
					sql.append(" and a.sorting_batch_id=" + batchId);
				}
				wareService.getDbOp().startTransaction();
				ResultSet rs = null;
				rs = service.getDbOp().executeQuery(sql.toString());
				while (rs.next()) {
					StockAdminHistoryBean log = new StockAdminHistoryBean();
					if ("广东省".equals(rs.getString("b.address").substring(0, 3))) {
						sortingService.updateSortingBatchOrderInfo("deliver=11", "delete_status<>1 and order_id=" + rs.getInt("b.id"));
						if (batch.getStatus() == 0) {
							sortingService.updateSortingBatchInfo("status=" + SortingBatchBean.STATUS1, "id=" + batchId);
						}
						wareService.modifyOrder("deliver=11", "id=" + rs.getInt("b.id"));
						stockService.updateOrderStock("deliver=11", "status<>3 and order_id=" + rs.getInt("b.id"));
						stockService.updateAuditPackage("deliver=11", "order_id=" + rs.getInt("b.id"));
						shengneiCount++;
						log.setRemark("订单号(" + rs.getString("b.code") + ")出库单编号 :(" + rs.getString("c.code") + ")快递公司(" + rs.getInt("b.deliver") + "-" + voOrder.deliverMapAll.get(String.valueOf(11)) + ")");

					} else {
						sortingService.updateSortingBatchOrderInfo("deliver=9", "delete_status<>1 and order_id=" + rs.getInt("b.id"));
						if (batch.getStatus() == 0) {
							sortingService.updateSortingBatchInfo("status=" + SortingBatchBean.STATUS1, "id=" + batchId);
						}
						wareService.modifyOrder("deliver=9", "id=" + rs.getInt("b.id"));
						stockService.updateOrderStock("deliver=9", "order_id=" + rs.getInt("b.id") + " and status<>3");
						stockService.updateAuditPackage("deliver=9", "order_id=" + rs.getInt("b.id"));
						shengwaiCount++;
						log.setRemark("订单号(" + rs.getString("b.code") + ")出库单编号 :(" + rs.getString("c.code") + ")快递公司(" + rs.getInt("b.deliver") + "-" + voOrder.deliverMapAll.get(String.valueOf(9)) + ")");
					}
					log.setAdminId(loginUser.getId());
					log.setAdminName(loginUser.getUsername());
					log.setLogId(rs.getInt("c.id"));
					log.setLogType(StockAdminHistoryBean.ORDER_DELIVER);
					log.setOperDatetime(DateUtil.getNow());
					log.setType(StockAdminHistoryBean.CHANGE);
					stockService.addStockAdminHistory(log);
				}

				rs.close();
				wareService.getDbOp().commitTransaction();
			} catch (Exception e) {
				if (wareService.getDbOp().getConn().getAutoCommit() == false) {
					wareService.getDbOp().rollbackTransaction();
				}
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
				service.releaseAll();
			}
		}
		request.setAttribute("batchId", batchId);
		return  "admin/rec/oper/sorting/sortingOrderList";
	}
	/**
	 * 分拣批次1.4，多SKU批次生成分拣波次
	 */
	@RequestMapping("/makeSortingBatchGroup3")
	public String makeSortingBatchGroup3(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		UserGroupBean group = admin.getGroup();

		WareService wareService = new WareService();
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService iService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		String batchId = StringUtil.convertNull(request.getParameter("batchId"));// 批次id
		String toPage = StringUtil.convertNull(request.getParameter("toPage"));// 跳转目标

		boolean isStartTransaction = false;
		synchronized (cargoLock) {
			try {
				wareService.getDbOp().startTransaction();
				if (!group.isFlag(590)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				SortingBatchBean sbBean = siService.getSortingBatchInfo("id=" + batchId);// 分拣批次
				int groupCount = siService.getSortingBatchGroupCount("sorting_batch_id=" + batchId);
				if (groupCount > 0) {
					request.setAttribute("msg", "该批次已经生成过波次");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;				
				}
				int noAssignDoCount = siService.getSortingBatchOrderCount("delete_status<>1 and deliver=0 and sorting_batch_id=" + batchId);
				int noAssignOtCount = siService.getSortingBatchOrderCount("delete_status<>1 and order_type=0 and sorting_batch_id=" + batchId);
				if (noAssignDoCount > 0 || noAssignOtCount > 0) {
					request.setAttribute("msg", "有" + noAssignDoCount + "个订单未分配归属物流," + noAssignOtCount + "个订单未指定分类，请在完成后再生成分拣波次");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;	
				}

				String typeSql = "select type_id as id from user_order_package_type where name ='印刷品'";
				ResultSet typeRs = wareService.getDbOp().executeQuery(typeSql);
				int type = 0;// 属于印刷品的分类
				if (typeRs.next()) {
					type = typeRs.getInt(1);
				}

				/*
				 * 得到所有订单及其货位列表 在一位多品的情况下，每个订单的货位列表中货位可能重复
				 */
				List orderListAll = new ArrayList();// 批次中所有订单列表，其中元素：SortingBatchOrderBean
				String orderListSql = "select sbo.id,sbo.order_code,sbo.deliver,ospc.cargo_whole_code " + "from sorting_batch_order sbo " + "join order_stock os on os.order_code=sbo.order_code " + "join order_stock_product osp on osp.order_stock_id=os.id " + "join order_stock_product_cargo ospc on ospc.order_stock_product_id=osp.id " + "join product p on p.id=osp.product_id " + "left join product_ware_property pwp on pwp.product_id=p.id " + "where sbo.delete_status!=1 and os.status!=3 and (pwp.product_type_id!=" + type + " or pwp.product_type_id is null) " + " and sbo.sorting_batch_id=" + batchId + " order by ospc.cargo_whole_code asc";
				ResultSet rs = wareService.getDbOp().executeQuery(orderListSql);
				while (rs.next()) {
					int id = rs.getInt("sbo.id");
					String cargoCode = rs.getString("ospc.cargo_whole_code");
					String orderCode = rs.getString("sbo.order_code");
					int deliver = rs.getInt("sbo.deliver");

					OrderStockProductCargoBean ospcBean = new OrderStockProductCargoBean();// 用于记录货位
					ospcBean.setCargoWholeCode(cargoCode);

					boolean contains = false;// 现有订单列表中是否包含该订单
					for (int i = 0; i < orderListAll.size(); i++) {
						SortingBatchOrderBean sbo = (SortingBatchOrderBean) orderListAll.get(i);
						if (sbo.getId() == id) {
							contains = true;
							List<OrderStockProductCargoBean> ospcList = sbo.getOrderStockProductCargoList();
							ospcList.add(ospcBean);
						}
					}
					if (contains == false) {// 现有订单列表中不包含该订单
						SortingBatchOrderBean sboBean = new SortingBatchOrderBean();
						sboBean.setId(id);
						sboBean.setOrderCode(orderCode);
						sboBean.setDeliver(deliver);

						orderListAll.add(sboBean);
						List<OrderStockProductCargoBean> ospcList = new ArrayList<OrderStockProductCargoBean>();
						ospcList.add(ospcBean);
						sboBean.setOrderStockProductCargoList(ospcList);
					}
				}
				rs.close();

				/*
				 * 区分跨区和不跨区的订单
				 */
				List<SortingBatchOrderBean> sAreaOrderList = new ArrayList<SortingBatchOrderBean>();// 不跨区订单
				List<SortingBatchOrderBean> mAreaOrderList = new ArrayList<SortingBatchOrderBean>();// 跨区订单
				for (int i = 0; i < orderListAll.size(); i++) {
					List<String> stockAreaList = new ArrayList<String>();// 订单相关地区列表
					SortingBatchOrderBean sboBean = (SortingBatchOrderBean) orderListAll.get(i);
					List<OrderStockProductCargoBean> ospcList = sboBean.getOrderStockProductCargoList();
					for (int j = 0; j < ospcList.size(); j++) {
						OrderStockProductCargoBean ospcBean = (OrderStockProductCargoBean) ospcList.get(j);
						String cargoCode = ospcBean.getCargoWholeCode();// 货位号
						String stockAreaCode = cargoCode.substring(0, 7);// 地区号
						if (!stockAreaList.contains(stockAreaCode)) {
							stockAreaList.add(stockAreaCode);
						}
					}
					if (stockAreaList.size() > 1) {// 订单属于跨区
						mAreaOrderList.add(sboBean);
					} else {
						sAreaOrderList.add(sboBean);
					}
				}

				/*
				 * 处理不跨区订单
				 */
				// 区域订单对应表，key：区域GZZ01-A，value：orderList，其中元素：SortingBatchOrderBean
				Map<String, List> areaOrderMap = new HashMap<String, List>();
				for (int i = 0; i < sAreaOrderList.size(); i++) {
					SortingBatchOrderBean sboBean = (SortingBatchOrderBean) sAreaOrderList.get(i);
					List ospcList = sboBean.getOrderStockProductCargoList();
					if (ospcList.size() > 0) {
						OrderStockProductCargoBean ospcBean = (OrderStockProductCargoBean) ospcList.get(0);
						String stockAreaCode = ospcBean.getCargoWholeCode().substring(0, 7);
						if (areaOrderMap.containsKey(stockAreaCode)) {// 已包含该区域
							List orderList = areaOrderMap.get(stockAreaCode);
							orderList.add(sboBean);
						} else {
							List<SortingBatchOrderBean> orderList = new ArrayList<SortingBatchOrderBean>();
							orderList.add(sboBean);
							areaOrderMap.put(stockAreaCode, orderList);
						}
					} else {// 有订单没货位，暂时还不知道怎么处理
						System.out.println("多SKU分拣批次，有订单无货位，批次订单id：" + sboBean.getId());
					}
				}
				Iterator sAreaOrderIter = areaOrderMap.keySet().iterator();
				while (sAreaOrderIter.hasNext()) {
					List<SortingBatchOrderBean> groupList = new ArrayList<SortingBatchOrderBean>();// 可以用来生成波次的订单列表（未拆成30一波）
					String stockAreaCode = sAreaOrderIter.next().toString();
					List<SortingBatchOrderBean> sboList = areaOrderMap.get(stockAreaCode);
					groupList = getGroupList(sboList);
					// groupList生成波次
					addSortingBatchGroupByList2(siService, sbBean, groupList);

				}

				// 处理跨区订单
				List<SortingBatchOrderBean> groupList = new ArrayList<SortingBatchOrderBean>();// 可以用来生成波次的订单列表（未拆成30一波）
				groupList = getGroupList(mAreaOrderList);
				// groupList生成波次
				addSortingBatchGroupByList2(siService, sbBean, groupList);
				wareService.getDbOp().commitTransaction();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			} finally {
				// siService.getDbOp().commitTransaction();
				siService.releaseAll();
			}
			request.setAttribute("batchId", batchId);
			return  "admin/rec/oper/sorting/sortingBatchGroupList";
		}
	}
	/**
	 * 根据订单列表获得可以生成波次的订单列表，相当于排序
	 * 
	 * @param orderList
	 * @return
	 */
	public List getGroupList(List orderList) {
		if (orderList == null) {
			return new ArrayList();
		}

		// 可以用来生成波次的订单列表（未拆成30一波）
		List<SortingBatchOrderBean> groupList = new ArrayList<SortingBatchOrderBean>();

		// 订单涉及巷道对应表，List中元素：订单涉及的巷道:GZZ01-A01
		Map<SortingBatchOrderBean, List> orderPassageMap = new HashMap<SortingBatchOrderBean, List>();

		// 所有订单涉及的巷道列表，key：巷道号:01，value：该巷道号对应的巷道列表:GZZ01-A01,GZZ01-B01
		TreeMap<String, List> passageMap = new TreeMap<String, List>();

		for (int i = 0; i < orderList.size(); i++) {
			SortingBatchOrderBean sboBean = (SortingBatchOrderBean) orderList.get(i);
			List<OrderStockProductCargoBean> ospcList = sboBean.getOrderStockProductCargoList();
			for (int j = 0; j < ospcList.size(); j++) {
				OrderStockProductCargoBean ospcBean = (OrderStockProductCargoBean) ospcList.get(j);
				String cargoCode = ospcBean.getCargoWholeCode();// 货位号
				String passageCode = cargoCode.substring(7, 9);// 巷道号01
				String passageWholeCode = cargoCode.substring(0, 9);// 巷道完整编号GZZ01-A01

				// passageMap中添加相关巷道
				if (passageMap.containsKey(passageCode)) {
					List<String> passageList = passageMap.get(passageCode);
					if (!passageList.contains(passageWholeCode)) {
						passageList.add(passageWholeCode);
					}
				} else {
					List<String> passageList = new ArrayList<String>();
					passageList.add(passageWholeCode);
					passageMap.put(passageCode, passageList);
				}

				// orderPassageMap中添加相关巷道
				if (orderPassageMap.containsKey(sboBean)) {
					List<String> passageList = orderPassageMap.get(sboBean);
					if (!passageList.contains(passageWholeCode)) {
						passageList.add(passageWholeCode);
					}
				} else {
					List<String> passageList = new ArrayList<String>();
					passageList.add(passageWholeCode);
					orderPassageMap.put(sboBean, passageList);
				}

			}
		}

		// 开始分配
		/*
		 * 同时参与匹配的巷道列表 其中元素：巷道GZZ01-A01
		 */
		List<String> tempPassageList = new ArrayList<String>();
		Iterator passageIter = passageMap.keySet().iterator();
		while (passageIter.hasNext()) {
			String passageCode = passageIter.next().toString();// 某一个巷道01
			List<String> passageList = passageMap.get(passageCode);// 与巷道有关的巷道号GZZ01-A01,GZZ01-B01
			// 得到所有相关巷道列表
			tempPassageList.addAll(passageList);
			for (int i = 0; i < orderList.size(); i++) {
				SortingBatchOrderBean sboBean = (SortingBatchOrderBean) orderList.get(i);
				List<String> orderPassageList = orderPassageMap.get(sboBean);// 订单涉及巷道列表
				boolean enoughPassage = true;// 巷道列表是否满足该订单
				for (int j = 0; j < orderPassageList.size(); j++) {
					String passageWholeCode = orderPassageList.get(j);
					if (!tempPassageList.contains(passageWholeCode)) {// 巷道列表不能满足该订单
						enoughPassage = false;
						break;
					}
				}
				if (enoughPassage == true) {
					groupList.add(sboBean);
					orderList.remove(sboBean);
					i--;
				}
			}
		}
		return groupList;
	}

	// 添加波次，按照批次id和订单列表

	public static Map groupCodeMap = new HashMap();
	static {
		groupCodeMap.put("1", "A-1");
		groupCodeMap.put("2", "A-2");
		groupCodeMap.put("3", "A-3");
		groupCodeMap.put("4", "A-4");
		groupCodeMap.put("5", "A-5");
		groupCodeMap.put("6", "A-6");
		groupCodeMap.put("7", "B-1");
		groupCodeMap.put("8", "B-2");
		groupCodeMap.put("9", "B-3");
		groupCodeMap.put("10", "B-4");
		groupCodeMap.put("11", "B-5");
		groupCodeMap.put("12", "B-6");
		groupCodeMap.put("13", "C-1");
		groupCodeMap.put("14", "C-2");
		groupCodeMap.put("15", "C-3");
		groupCodeMap.put("16", "C-4");
		groupCodeMap.put("17", "C-5");
		groupCodeMap.put("18", "C-6");
		groupCodeMap.put("19", "D-1");
		groupCodeMap.put("20", "D-2");
		groupCodeMap.put("21", "D-3");
		groupCodeMap.put("22", "D-4");
		groupCodeMap.put("23", "D-5");
		groupCodeMap.put("24", "D-6");
		groupCodeMap.put("25", "E-1");
		groupCodeMap.put("26", "E-2");
		groupCodeMap.put("27", "E-3");
		groupCodeMap.put("28", "E-4");
		groupCodeMap.put("29", "E-5");
		groupCodeMap.put("30", "E-6");
	}
	// 添加多SKu波次，按照批次id和订单列表
		public int addSortingBatchGroupByList2(SortingInfoService siService, SortingBatchBean sbBean, List orderList) {
			int orderCountPerGroup = 30;// 每个波次中订单数

			List tempOrderList = new ArrayList();// 参与生成波次的所有EMS订单列表
			for (int j = 0; j < orderList.size(); j++) {
				SortingBatchOrderBean sboBean = (SortingBatchOrderBean) orderList.get(j);
					tempOrderList.add(sboBean);
			}
			List groupList = new ArrayList();// 一个波次中的订单列表
			for (int j = 0; j < tempOrderList.size(); j++) {
				SortingBatchOrderBean sboBean = (SortingBatchOrderBean) tempOrderList.get(j);
				groupList.add(sboBean);
				// 序号是30的倍数或者末尾，生成波次
				if ((j + 1) % orderCountPerGroup == 0 || j == tempOrderList.size() - 1) {
					String code = sbBean.getCode();
					int num = siService.getSortingBatchGroupCount("sorting_batch_id=" + sbBean.getId());
					String bcount = Integer.toString(num + 1);
					if (bcount.length() == 1) {
						bcount = "00" + bcount;
					}
					if (bcount.length() == 2) {
						bcount = "0" + bcount;
					}
					code = code + bcount;
					SortingBatchGroupBean dbgBean = new SortingBatchGroupBean();
					dbgBean.setCreateDatetime(DateUtil.getNow());
					dbgBean.setCode(code);
					dbgBean.setDeliver(0);
					dbgBean.setStatus(0);
					dbgBean.setStorage(sbBean.getStorage());
					dbgBean.setSortingBatchId(sbBean.getId());
					dbgBean.setType1(sbBean.getType1());
					dbgBean.setType2(0);
					siService.addSortingBatchGroupInfo(dbgBean);
					int groupId = siService.getDbOp().getLastInsertId();
					for (int k = 0; k < groupList.size(); k++) {
						SortingBatchOrderBean tempSboBean = (SortingBatchOrderBean) groupList.get(k);
						int id = tempSboBean.getId();
						String groupCode = groupCodeMap.get((k + 1) + "").toString();
						siService.updateSortingBatchOrderInfo("sorting_group_id=" + groupId + ",sorting_group_code='" + code + "',status =1,group_num=" + (k + 1) + ",group_code='" + groupCode + "'", "id=" + id);
					}
					siService.updateSortingBatchInfo("status=2", "id=" + sbBean.getId());
					groupList = new ArrayList();
				}
			}

			return 0;
		}
		/**
		 * 分拣波次查询列表(波次信息)
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("/sortingBatchGroupQueryList")
		@ResponseBody
		public EasyuiDataGridJson sortingBatchGroupQueryList( HttpServletRequest request, HttpServletResponse response) throws Exception {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);
			IStockService isService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ICargoService icService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			String startTime = StringUtil.convertNull(request.getParameter("startTime"));
			String endTime = StringUtil.convertNull(request.getParameter("endTime"));
			int queryType = StringUtil.StringToId(request.getParameter("queryType"));
			String code = StringUtil.convertNull(request.getParameter("code"));
			String storage = StringUtil.convertNull(request.getParameter("storage"));
			String from = StringUtil.convertNull(request.getParameter("from"));
			voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
			String page = StringUtil.convertNull(request.getParameter("page"));
			String rows = StringUtil.convertNull(request.getParameter("rows"));
			String sort = StringUtil.convertNull(request.getParameter("sort"));
			String order = StringUtil.convertNull(request.getParameter("order"));
			UserGroupBean group = admin.getGroup();
			EasyuiDataGridJson json = new EasyuiDataGridJson();
			EasyuiDataGrid easyUiPage = new EasyuiDataGrid();
			easyUiPage.setOrder(order);
			easyUiPage.setPage(StringUtil.toInt(page));
			easyUiPage.setRows(StringUtil.toInt(rows));
			easyUiPage.setSort(sort);
			try {
					 if (!group.isFlag(591)) {
						 request.setAttribute("msg", "您没有此权限!");
						 request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						 return null;	
					 }
				if(!"1".equals(from)){//from=1说明是从左侧树打开的链接
					StringBuffer sql = new StringBuffer();
					StringBuilder url = new StringBuilder();
					url.append("sortingAction.do?method=sortingBatchGroupQueryList");
					if (storage != null &&storage.length() > 0) {
						sql.append(" and storage=" + storage);
						url.append("&storage=" + storage);
					}
					if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
						sql.append(" and left(receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'");
						url.append("&startTime=" + startTime);
						url.append("&endTime=" + endTime);
					}
					if (code != null && code.length() != 0 && !code.equals("请输入...")) {
						if (queryType == 1) {// 说明输入的是订单编号
							sql.append(" and id in(select sorting_group_id from sorting_batch_order where order_code='" + code + "')");
							url.append("&code=" + code);
							url.append("&queryType="+queryType);
						} else if (queryType == 2) {// 说明输入的是波次编号
							sql.append(" and code='" + code + "'");
							url.append("&code=" + code);
							url.append("&queryType="+queryType);
						} else if (queryType == 3) {// 说明输入的是分播员工编号
							sql.append(" and staff_id=(select id from cargo_staff where code='" + code + "')");
							url.append("&code=" + code);
							url.append("&queryType="+queryType);
						} else if (queryType == 4) {// 说明输入的是分拣员工编号
							sql.append(" and staff_id2=(select id from cargo_staff where code='" + code + "')");
							url.append("&code=" + code);
							url.append("&queryType="+queryType);
						}
					}

					int totalCount = siService.getSortingBatchGroupCount("id>0" + sql);
					List groupList = siService.getSortingBatchGroupList("id>0" + sql, (easyUiPage.getPage()-1)*easyUiPage.getRows(), easyUiPage.getRows(), null);
					List list = new ArrayList();
					int count = 0;
					int count1 = 0;
					int count2 = 0;
					for (int i = 0; i < groupList.size(); i++) {
						SortingBatchGroupBean sbgBean = (SortingBatchGroupBean) groupList.get(i);
						int SKUcount = 0;
						int productCount = 0;
						String sqlString = "select count(distinct d.product_code),sum(stockout_count) from sorting_batch_group as a " + "" + "left join sorting_batch_order as b on a.id=b.sorting_group_id " + "" + "left join order_stock as c" + " on b.order_id =c.order_id " + "" + "left join order_stock_product as d on c.id=d.order_stock_id " + "where b.delete_status<>1 and c.status<>3 and a.id=" + sbgBean.getId();
						ResultSet rs = icService.getDbOp().executeQuery(sqlString);
						if (rs.next()) {
							SKUcount = rs.getInt(1);
							productCount = rs.getInt(2);
						}
						rs.close();
						List orderList = siService.getSortingBatchOrderList("delete_status<>1 and sorting_group_id=" + sbgBean.getId(), -1, -1, null);
						List orderList1 = new ArrayList();
						for (int j = 0; j < orderList.size(); j++) {
							SortingBatchOrderBean sboBean = (SortingBatchOrderBean) orderList.get(j);
							OrderStockBean osBean = isService.getOrderStock("order_id=" + sboBean.getOrderId() + " and status!=3");
							if (osBean == null) {
								continue;
							}
							List ospList = isService.getOrderStockProductList("order_stock_id=" + osBean.getId(), -1, -1, null);
							List ospcList = new ArrayList();
							if (ospList.size() != 0) {
								for (int k = 0; k < ospList.size(); k++) {
									OrderStockProductBean ospBean = (OrderStockProductBean) ospList.get(k);
									List cargoList = isService.getOrderStockProductCargoList("order_stock_product_id=" + ospBean.getId(), -1, -1, null);
									for (int c = 0; c < cargoList.size(); c++) {
										OrderStockProductCargoBean tempOspc = (OrderStockProductCargoBean) cargoList.get(c);
										String tempCode = "";
										if (ospBean.getCargoCode() != null) {
											tempCode = ospBean.getCargoCode();
										}
										if (c != 0) {
											tempCode += "<br/>";
										}
										tempCode += tempOspc.getCargoWholeCode();
										ospBean.setCargoCode(tempCode);
									}
									ospcList.add(ospBean);
								}
								sboBean.setProductList(ospcList);

							}
							orderList1.add(sboBean);
						}
						CargoStaffBean staffBean = icService.getCargoStaff("id=" + sbgBean.getStaffId());
						if (staffBean != null) {
							sbgBean.setStaffName(staffBean.getName());
						}
						CargoStaffBean staffBean2 = icService.getCargoStaff("id=" + sbgBean.getStaffId2());
						if (staffBean2 != null) {
							sbgBean.setStaffName2(staffBean2.getName());
						}
						if (sbgBean.getReceiveDatetime2() != null && sbgBean.getReceiveDatetime2().length() > 0 && sbgBean.getCompleteDatetime2() != null && sbgBean.getCompleteDatetime2().length() > 0) {
							int secondeSortingTime = DateUtil.getMinuteSub(sbgBean.getReceiveDatetime2(), sbgBean.getCompleteDatetime2());
							if(secondeSortingTime==0){
								sbgBean.setSecondeSortingTime(1);
							}else{
								sbgBean.setSecondeSortingTime(secondeSortingTime);
							}
						}
						CargoInfoAreaBean cargoBean = icService.getCargoInfoArea("old_id=" + sbgBean.getStorage());
						sbgBean.setStorageName(cargoBean.getName());
						sbgBean.setOrderList(orderList1);
						sbgBean.setOrderCount(orderList.size());
						sbgBean.setSkuCount(SKUcount);
						sbgBean.setProductCount(productCount);
						list.add(sbgBean);
					}
					request.setAttribute("list", list);// 仓库列表
					request.setAttribute("count", count + "");// 未打单订单数量
					request.setAttribute("count1", count1 + "");// 分拣中订单数量
					request.setAttribute("count2", count2 + "");// 已完成订单数量
					Map productTypeMap = new HashMap();
					String sql2 = "select distinct type_id as id,name from user_order_package_type";
					ResultSet rs2 = icService.getDbOp().executeQuery(sql2);
					while (rs2.next()) {
						productTypeMap.put(rs2.getInt("id") + "", rs2.getString("name"));
					}
					rs2.close();
					request.setAttribute("productTypeMap", productTypeMap);
					json.setRows(groupList);// 设置返回的行
					json.setTotal(Long.valueOf(totalCount));// 设置总记录数
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
			return json;
		}
		/**
		 * 分拣量统计明细
		 */
		@RequestMapping("/sortingStatisticalDetailed")
		public String sortingStatisticalDetailed(HttpServletRequest request, HttpServletResponse response) {
			voUser user = (voUser) request.getSession().getAttribute("userView");
			if (user == null) {
				request.setAttribute("msg", "当前没有登录，操作失败！");
				return  "admin/rec/err";
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(645)) {
				request.setAttribute("msg", "您没有此权限!");
				return  "admin/rec/err";
			}
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);
			String startTime = StringUtil.convertNull(request.getParameter("startTime"));
			String endTime = StringUtil.convertNull(request.getParameter("endTime"));
			String staffName = "";
			String staffCode = StringUtil.convertNull(request.getParameter("staffCode"));
			try {
				StringBuffer conditon = new StringBuffer();
				if ("".equals(startTime) || "".equals(endTime)) {
					startTime = DateUtil.formatDate(new Date());
					endTime = DateUtil.formatDate(new Date());
				}

				conditon.append(" and left(a.receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'");

				String sql_L = " select distinct cs.code,cs.name from cargo_staff as cs  " + "left join sorting_batch_group as a on cs.id=a.staff_id where " + " left(a.receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'";  
				ResultSet rs_L = wareService.getDbOp().executeQuery(sql_L);
				List sibList = new ArrayList();
				while (rs_L.next()) {
					CargoStaffBean sib = new CargoStaffBean();
					sib.setCode(rs_L.getString(1));
					sib.setName(rs_L.getString(2));
					sibList.add(sib);
				}
				rs_L.close();
				if (!"".equals(staffCode)) {
					conditon.append(" and cs.code='" + staffCode + "'");
					String staffSQL = " select name from cargo_staff where code='" + StringUtil.toSql(staffCode) + "'";
					ResultSet staffRS = wareService.getDbOp().executeQuery(staffSQL);
					if (staffRS.next()) {
						staffName = staffRS.getString("name");
					} else {
						request.setAttribute("msg", "员工编号不正确！");
						return  "admin/rec/err";
					}
					staffRS.close();
				}
				String sql = " select count(distinct d.product_code),sum(d.stockout_count),count(distinct c.order_id),count(distinct ci.passage_id)," + " count( distinct a.id),count(distinct left(a.receive_datetime,10)),cs.name,cs.code,left(a.receive_datetime,10) ,a.staff_id from sorting_batch_group as a" + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where 1=1 ";
				if(staffName!=null && !"".equals(staffName))
					sql += "and a.staff_name='"+staffName+"'";
				sql +=" " + conditon + " group by a.id";
				ResultSet rs = wareService.getDbOp().executeQuery(sql);
				List list = new ArrayList();
				int n = 0;
				while (rs.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setSkuCount(rs.getInt(1));
					groupBean.setProductCount(rs.getInt(2));
					groupBean.setOrderCount(rs.getInt(3));
					groupBean.setPassageCount(rs.getInt(4));
					groupBean.setStaffName(rs.getString(7));
					groupBean.setStaffCode(rs.getString(8));
					groupBean.setSortingTime(rs.getString(9));
					groupBean.setStaffId(rs.getInt(10));
					if (list != null && list.size() > 0) {
						for (Iterator i = list.iterator(); i.hasNext();) {
							SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
							if (tempBean.getSortingTime().equals(groupBean.getSortingTime())) {
								tempBean.setSkuCount(tempBean.getSkuCount() + groupBean.getSkuCount());
								tempBean.setProductCount(tempBean.getProductCount() + groupBean.getProductCount());
								tempBean.setOrderCount(tempBean.getOrderCount() + groupBean.getOrderCount());
								tempBean.setPassageCount(tempBean.getPassageCount() + groupBean.getPassageCount());
								n = 1;
								break;
							}
						}
						if (n == 0) {
							list.add(groupBean);
						}
						n = 0;
					} else {
						list.add(groupBean);
					}
				}
				rs.close();

				// 计算每个员工的出勤天数
				String sql1 = "select count(distinct left(receive_datetime,10)),cs.id from  cargo_staff as cs  " + "join sorting_batch_group a on cs.id=a.staff_id where a.id>0 " + conditon + " group by cs.id";
				ResultSet rs1 = wareService.getDbOp().executeQuery(sql1);
				List staffAttendanceList = new ArrayList();
				while (rs1.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setAttendanceCount(rs1.getInt(1));
					groupBean.setStaffId(rs1.getInt(2));
					staffAttendanceList.add(groupBean);
				}
				rs1.close();
				if (list != null) {
					for (Iterator i = list.iterator(); i.hasNext();) {
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
						if (staffAttendanceList != null) {
							for (int j = 0; j < staffAttendanceList.size(); j++) {
								SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffAttendanceList.get(j);
								if (AttendanceBean.getStaffId() == groupBean.getStaffId()) {
									groupBean.setAttendanceCount(AttendanceBean.getAttendanceCount());
								}
							}
						}
					}

				}

				// 计算每个员工的完成波次数
				String sql2 = "select count(distinct a.id),left(a.receive_datetime,10) from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where 1=1 " + conditon ; 
				if(staffName!=null && !"".equals(staffName))
					sql += " and a.staff_name='"+staffName+"' ";
						sql2= sql2 + " group by left(a.receive_datetime,10)";
				ResultSet rs2 = wareService.getDbOp().executeQuery(sql2);
				//System.out.println(sql2);
				List staffGroupList = new ArrayList();
				while (rs2.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setGroupCount(rs2.getInt(1));
					//groupBean.setStaffId(rs2.getInt(2));
					groupBean.setSortingTime(rs2.getString(2));
					staffGroupList.add(groupBean);
				}
				rs2.close();
				if (list != null) {
					for (Iterator i = list.iterator(); i.hasNext();) {
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
						if (staffGroupList != null) {
							for (int j = 0; j < staffGroupList.size(); j++) {
								SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffGroupList.get(j);
								if (AttendanceBean.getSortingTime().equals(groupBean.getSortingTime())) {
									groupBean.setGroupCount(AttendanceBean.getGroupCount());
								}
							}
						}
					}

				}
				// 根据日期补充没有作业天的数据
				List detailedList = new ArrayList();
				StringBuffer dateTime = new StringBuffer();

				int stDay = StringUtil.toInt(startTime.substring(8, 10));
				int etDay = StringUtil.toInt(endTime.substring(8, 10));
				int stMoon = StringUtil.toInt(startTime.substring(5, 7));
				int etMoon = StringUtil.toInt(endTime.substring(5, 7));
				if (stDay > etDay) {
					int end = 0;
					if (stMoon == 2) {
						if ((StringUtil.toInt(startTime.substring(0, 5))) % 4 == 0) {
							end = 29;
						} else {
							end = 28;
						}
					} else if (stMoon == 1 || stMoon == 3 || stMoon == 5 || stMoon == 7 || stMoon == 8 || stMoon == 10 || stMoon == 12) {
						end = 31;
					} else {
						end = 30;
					}
					for (int i = stDay; i <= end; i++) {
						boolean flag = true;
						dateTime.append(startTime.substring(0, 8));
						if (i < 10) {
							dateTime.append("0");
						}
						dateTime.append(i);
						SortingBatchGroupBean sbgb = new SortingBatchGroupBean();
						for (int j = 0; j < list.size(); j++) {
							sbgb = (SortingBatchGroupBean) list.get(j);
							String DBDateTime = sbgb.getSortingTime();
							// 计算每个员工的完成波次数
							String sql3 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where left(receive_datetime,10)='" + DBDateTime + "'";
							if(staffName!=null && !"".equals(staffName))
								sql3 += " and a.staff_name='"+staffName+"' ";
							sql3 += " group by left(receive_datetime,10)";
							ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
							int groupCount = 0;
							if (rs3.next()) {
								groupCount = rs3.getInt(1);
							}
							rs3.close();
							if (dateTime.toString().equals(DBDateTime)) {
								SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
								groupBean.setSkuCount(sbgb.getSkuCount());
								groupBean.setProductCount(sbgb.getProductCount());
								groupBean.setOrderCount(sbgb.getOrderCount());
								groupBean.setPassageCount(sbgb.getPassageCount());
								groupBean.setGroupCount(groupCount);
								groupBean.setStaffName(sbgb.getStaffName());
								groupBean.setStaffCode(sbgb.getStaffCode());
								groupBean.setSortingTime(sbgb.getSortingTime());
								detailedList.add(groupBean);
								flag = false;
								break;
							}
						}
						if (flag) {
							SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
							groupBean.setSkuCount(0);
							groupBean.setProductCount(0);
							groupBean.setOrderCount(0);
							groupBean.setPassageCount(0);
							groupBean.setGroupCount(0);
							groupBean.setStaffName(sbgb.getStaffName());
							groupBean.setStaffCode(sbgb.getStaffCode());
							groupBean.setSortingTime(dateTime.toString());
							detailedList.add(groupBean);
						}
						dateTime.setLength(0);
					}
					for (int i = 1; i <= etDay; i++) {
						boolean flag = true;
						dateTime.append(endTime.substring(0, 8));
						if (i < 10) {
							dateTime.append("0");
						}
						dateTime.append(i);
						SortingBatchGroupBean sbgb = new SortingBatchGroupBean();
						for (int j = 0; j < list.size(); j++) {
							sbgb = (SortingBatchGroupBean) list.get(j);
							String DBDateTime = sbgb.getSortingTime();
							// 计算每个员工的完成波次数
							String sql3 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where left(receive_datetime,10)='" + DBDateTime + "'";
							if(staffName!=null && !"".equals(staffName))
								sql3 += " and a.staff_name='"+staffName+"' ";
							sql3+=" group by left(receive_datetime,10)";
						
							ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
							int groupCount = 0;
							if (rs3.next()) {
								groupCount = rs3.getInt(1);
							}
							rs3.close();
							if (dateTime.toString().equals(DBDateTime)) {
								SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
								groupBean.setSkuCount(sbgb.getSkuCount());
								groupBean.setProductCount(sbgb.getProductCount());
								groupBean.setOrderCount(sbgb.getOrderCount());
								groupBean.setPassageCount(sbgb.getPassageCount());
								groupBean.setGroupCount(groupCount);
								groupBean.setStaffName(sbgb.getStaffName());
								groupBean.setStaffCode(sbgb.getStaffCode());
								groupBean.setSortingTime(sbgb.getSortingTime());
								detailedList.add(groupBean);
								flag = false;
								break;
							}
						}
						if (flag) {
							SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
							groupBean.setSkuCount(0);
							groupBean.setProductCount(0);
							groupBean.setOrderCount(0);
							groupBean.setPassageCount(0);
							groupBean.setGroupCount(0);
							groupBean.setStaffName(sbgb.getStaffName());
							groupBean.setStaffCode(sbgb.getStaffCode());
							groupBean.setSortingTime(dateTime.toString());
							detailedList.add(groupBean);
						}
						dateTime.setLength(0);
					}
				} else {
					for (int i = stDay; i <= etDay; i++) {
						boolean flag = true;
						dateTime.append(startTime.substring(0, 8));
						if (i < 10) {
							dateTime.append("0");
						}
						dateTime.append(i);
						SortingBatchGroupBean sbgb = new SortingBatchGroupBean();
						for (int j = 0; j < list.size(); j++) {
							sbgb = (SortingBatchGroupBean) list.get(j);
							String DBDateTime = sbgb.getSortingTime();
							// 计算每个员工的完成波次数
							String sql4 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where left(receive_datetime,10)='" + DBDateTime + "'";
							if(staffName!=null && !"".equals(staffName))
								sql4 += " and a.staff_name='"+staffName+"' ";
							sql4 += " group by left(receive_datetime,10)";
							ResultSet rs4 = wareService.getDbOp().executeQuery(sql4);
							//System.out.println("-->"+sql4);
							int groupCount = 0;
							if (rs4.next()) {
								groupCount = rs4.getInt(1);
							}
							rs4.close();
							if (dateTime.toString().equals(DBDateTime)) {
								SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
								groupBean.setSkuCount(sbgb.getSkuCount());
								groupBean.setProductCount(sbgb.getProductCount());
								groupBean.setOrderCount(sbgb.getOrderCount());
								groupBean.setPassageCount(sbgb.getPassageCount());
								groupBean.setGroupCount(groupCount);
								groupBean.setStaffName(sbgb.getStaffName());
								groupBean.setStaffCode(sbgb.getStaffCode());
								groupBean.setSortingTime(sbgb.getSortingTime());
								detailedList.add(groupBean);
								flag = false;
								break;
							}
						}
						if (flag) {
							SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
							groupBean.setSkuCount(0);
							groupBean.setProductCount(0);
							groupBean.setOrderCount(0);
							groupBean.setPassageCount(0);
							groupBean.setGroupCount(0);
							groupBean.setStaffName(sbgb.getStaffName());
							groupBean.setStaffCode(sbgb.getStaffCode());
							groupBean.setSortingTime(dateTime.toString());
							detailedList.add(groupBean);
						}
						dateTime.setLength(0);
					}
				}
				
				//计算每个员工的撤单量
				String sql3 = "SELECT count(s.order_id) ,left(t.receive_datetime,10) FROM sorting_batch_group t left join sorting_batch_order s on t.id=s.sorting_group_id where s.delete_status=1 ";
				if(staffName!=null && !"".equals(staffName))
					sql3 += "and t.staff_name='"+staffName+"' ";
				sql3 +="and s.status<>0 and left(t.receive_datetime,10) between '"+startTime+"' and '"+endTime+"' group by left(t.receive_datetime,10)";
				ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
				while(rs3.next()){
					if(list != null){
						for(Iterator i = list.iterator(); i.hasNext();){
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
							if(rs3.getString(2) .equals( groupBean.getSortingTime())){
								groupBean.setCancelOrderCount(rs3.getInt(1));
							}
						}				
						
					}
					if(detailedList!=null){
						for(Iterator i = detailedList.iterator(); i.hasNext();){
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
							if(rs3.getString(2) .equals( groupBean.getSortingTime())){
								groupBean.setCancelOrderCount(rs3.getInt(1));
							}
						}
					}
				}
				rs3.close();
				SortingBatchGroupBean totalBean = new SortingBatchGroupBean();
				int totalAttendanceCount = 0;
				int totalGroupCount = 0;
				int totalOrderCount = 0;
				int totalSkuCount = 0;
				int totalProductCount = 0;
				int totalPassageCount = 0;
				int totalCalcelOrderCount = 0;
				if (list != null && list.size() > 0) {
					for (Iterator i = list.iterator(); i.hasNext();) {
						SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
						totalSkuCount = tempBean.getSkuCount() + totalSkuCount;
						totalProductCount = tempBean.getProductCount() + totalProductCount;
						totalOrderCount = tempBean.getOrderCount() + totalOrderCount;
						totalPassageCount = tempBean.getPassageCount() + totalPassageCount;
						totalGroupCount = tempBean.getGroupCount() + totalGroupCount;
						totalAttendanceCount = tempBean.getAttendanceCount() + totalAttendanceCount;
						totalCalcelOrderCount = tempBean.getCancelOrderCount() + totalCalcelOrderCount;
					}
				}
				totalBean.setSkuCount(totalSkuCount);
				totalBean.setProductCount(totalProductCount);
				totalBean.setOrderCount(totalOrderCount);
				totalBean.setPassageCount(totalPassageCount);
				totalBean.setGroupCount(totalGroupCount);
				totalBean.setAttendanceCount(totalAttendanceCount);
				totalBean.setStaffCount(list.size());
				totalBean.setCancelOrderCount(totalCalcelOrderCount);
				request.setAttribute("endTime", endTime);
				request.setAttribute("startTime", startTime);
				request.setAttribute("staffName", staffName);
				request.setAttribute("staffCode", staffCode);
				request.setAttribute("totalBean", totalBean);
				request.setAttribute("sibList", sibList);
				request.setAttribute("detailedList", detailedList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
			return  "admin/rec/oper/sorting/sortingStatisticalDetailed";
		}
		/**
		 * 分拣量统计明细
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("/sortingStatisticalDetailedJson")
		@ResponseBody
		public EasyuiDataGridJson sortingStatisticalDetailedJson( HttpServletRequest request, HttpServletResponse response) {
			EasyuiDataGridJson json = new EasyuiDataGridJson();
			voUser user = (voUser) request.getSession().getAttribute("userView");
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);
			String startTime = StringUtil.convertNull(request.getParameter("startTime"));
			String endTime = StringUtil.convertNull(request.getParameter("endTime"));
			String staffName = "";
			String staffCode = StringUtil.convertNull(request.getParameter("staffCode"));
			try {
				if (user == null) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(645)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
					
				}
				StringBuffer conditon = new StringBuffer();
				if ("".equals(startTime) || "".equals(endTime)) {
					startTime = DateUtil.formatDate(new Date());
					endTime = DateUtil.formatDate(new Date());
				}

				conditon.append(" and left(a.receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'");

				String sql_L = " select distinct cs.code,cs.name from cargo_staff as cs  " + "left join sorting_batch_group as a on cs.id=a.staff_id where " + " left(a.receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'";  
				ResultSet rs_L = wareService.getDbOp().executeQuery(sql_L);
				List sibList = new ArrayList();
				while (rs_L.next()) {
					CargoStaffBean sib = new CargoStaffBean();
					sib.setCode(rs_L.getString(1));
					sib.setName(rs_L.getString(2));
					sibList.add(sib);
				}
				rs_L.close();
				if (!"".equals(staffCode)) {
					conditon.append(" and cs.code='" + staffCode + "'");
					String staffSQL = " select name from cargo_staff where code='" + StringUtil.toSql(staffCode) + "'";
					ResultSet staffRS = wareService.getDbOp().executeQuery(staffSQL);
					if (staffRS.next()) {
						staffName = staffRS.getString("name");
					} else {
						request.setAttribute("msg", "员工编号不正确！");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}
					staffRS.close();
				}
				String sql = " select count(distinct d.product_code),sum(d.stockout_count),count(distinct c.order_id),count(distinct ci.passage_id)," + " count( distinct a.id),count(distinct left(a.receive_datetime,10)),cs.name,cs.code,left(a.receive_datetime,10) ,a.staff_id from sorting_batch_group as a" + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where 1=1 ";
				if(staffName!=null && !"".equals(staffName))
					sql += "and a.staff_name='"+staffName+"'";
				sql +=" " + conditon + " group by a.id";
				ResultSet rs = wareService.getDbOp().executeQuery(sql);
				List list = new ArrayList();
				int n = 0;
				while (rs.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setSkuCount(rs.getInt(1));
					groupBean.setProductCount(rs.getInt(2));
					groupBean.setOrderCount(rs.getInt(3));
					groupBean.setPassageCount(rs.getInt(4));
					groupBean.setStaffName(rs.getString(7));
					groupBean.setStaffCode(rs.getString(8));
					groupBean.setSortingTime(rs.getString(9));
					groupBean.setStaffId(rs.getInt(10));
					if (list != null && list.size() > 0) {
						for (Iterator i = list.iterator(); i.hasNext();) {
							SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
							if (tempBean.getSortingTime().equals(groupBean.getSortingTime())) {
								tempBean.setSkuCount(tempBean.getSkuCount() + groupBean.getSkuCount());
								tempBean.setProductCount(tempBean.getProductCount() + groupBean.getProductCount());
								tempBean.setOrderCount(tempBean.getOrderCount() + groupBean.getOrderCount());
								tempBean.setPassageCount(tempBean.getPassageCount() + groupBean.getPassageCount());
								n = 1;
								break;
							}
						}
						if (n == 0) {
							list.add(groupBean);
						}
						n = 0;
					} else {
						list.add(groupBean);
					}
				}
				rs.close();

				// 计算每个员工的出勤天数
				String sql1 = "select count(distinct left(receive_datetime,10)),cs.id from  cargo_staff as cs  " + "join sorting_batch_group a on cs.id=a.staff_id where a.id>0 " + conditon + " group by cs.id";
				ResultSet rs1 = wareService.getDbOp().executeQuery(sql1);
				List staffAttendanceList = new ArrayList();
				while (rs1.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setAttendanceCount(rs1.getInt(1));
					groupBean.setStaffId(rs1.getInt(2));
					staffAttendanceList.add(groupBean);
				}
				rs1.close();
				if (list != null) {
					for (Iterator i = list.iterator(); i.hasNext();) {
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
						if (staffAttendanceList != null) {
							for (int j = 0; j < staffAttendanceList.size(); j++) {
								SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffAttendanceList.get(j);
								if (AttendanceBean.getStaffId() == groupBean.getStaffId()) {
									groupBean.setAttendanceCount(AttendanceBean.getAttendanceCount());
								}
							}
						}
					}

				}

				// 计算每个员工的完成波次数
				String sql2 = "select count(distinct a.id),left(a.receive_datetime,10) from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where 1=1 " + conditon ; 
				if(staffName!=null && !"".equals(staffName))
					sql += " and a.staff_name='"+staffName+"' ";
						sql2= sql2 + " group by left(a.receive_datetime,10)";
				ResultSet rs2 = wareService.getDbOp().executeQuery(sql2);
				//System.out.println(sql2);
				List staffGroupList = new ArrayList();
				while (rs2.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setGroupCount(rs2.getInt(1));
					//groupBean.setStaffId(rs2.getInt(2));
					groupBean.setSortingTime(rs2.getString(2));
					staffGroupList.add(groupBean);
				}
				rs2.close();
				if (list != null) {
					for (Iterator i = list.iterator(); i.hasNext();) {
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
						if (staffGroupList != null) {
							for (int j = 0; j < staffGroupList.size(); j++) {
								SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffGroupList.get(j);
								if (AttendanceBean.getSortingTime().equals(groupBean.getSortingTime())) {
									groupBean.setGroupCount(AttendanceBean.getGroupCount());
								}
							}
						}
					}

				}
				// 根据日期补充没有作业天的数据
				List detailedList = new ArrayList();
				StringBuffer dateTime = new StringBuffer();

				int stDay = StringUtil.toInt(startTime.substring(8, 10));
				int etDay = StringUtil.toInt(endTime.substring(8, 10));
				int stMoon = StringUtil.toInt(startTime.substring(5, 7));
				int etMoon = StringUtil.toInt(endTime.substring(5, 7));
				if (stDay > etDay) {
					int end = 0;
					if (stMoon == 2) {
						if ((StringUtil.toInt(startTime.substring(0, 5))) % 4 == 0) {
							end = 29;
						} else {
							end = 28;
						}
					} else if (stMoon == 1 || stMoon == 3 || stMoon == 5 || stMoon == 7 || stMoon == 8 || stMoon == 10 || stMoon == 12) {
						end = 31;
					} else {
						end = 30;
					}
					for (int i = stDay; i <= end; i++) {
						boolean flag = true;
						dateTime.append(startTime.substring(0, 8));
						if (i < 10) {
							dateTime.append("0");
						}
						dateTime.append(i);
						SortingBatchGroupBean sbgb = new SortingBatchGroupBean();
						for (int j = 0; j < list.size(); j++) {
							sbgb = (SortingBatchGroupBean) list.get(j);
							String DBDateTime = sbgb.getSortingTime();
							// 计算每个员工的完成波次数
							String sql3 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where left(receive_datetime,10)='" + DBDateTime + "'";
							if(staffName!=null && !"".equals(staffName))
								sql3 += " and a.staff_name='"+staffName+"' ";
							sql3 += " group by left(receive_datetime,10)";
							ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
							int groupCount = 0;
							if (rs3.next()) {
								groupCount = rs3.getInt(1);
							}
							rs3.close();
							if (dateTime.toString().equals(DBDateTime)) {
								SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
								groupBean.setSkuCount(sbgb.getSkuCount());
								groupBean.setProductCount(sbgb.getProductCount());
								groupBean.setOrderCount(sbgb.getOrderCount());
								groupBean.setPassageCount(sbgb.getPassageCount());
								groupBean.setGroupCount(groupCount);
								groupBean.setStaffName(sbgb.getStaffName());
								groupBean.setStaffCode(sbgb.getStaffCode());
								groupBean.setSortingTime(sbgb.getSortingTime());
								detailedList.add(groupBean);
								flag = false;
								break;
							}
						}
						if (flag) {
							SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
							groupBean.setSkuCount(0);
							groupBean.setProductCount(0);
							groupBean.setOrderCount(0);
							groupBean.setPassageCount(0);
							groupBean.setGroupCount(0);
							groupBean.setStaffName(sbgb.getStaffName());
							groupBean.setStaffCode(sbgb.getStaffCode());
							groupBean.setSortingTime(dateTime.toString());
							detailedList.add(groupBean);
						}
						dateTime.setLength(0);
					}
					for (int i = 1; i <= etDay; i++) {
						boolean flag = true;
						dateTime.append(endTime.substring(0, 8));
						if (i < 10) {
							dateTime.append("0");
						}
						dateTime.append(i);
						SortingBatchGroupBean sbgb = new SortingBatchGroupBean();
						for (int j = 0; j < list.size(); j++) {
							sbgb = (SortingBatchGroupBean) list.get(j);
							String DBDateTime = sbgb.getSortingTime();
							// 计算每个员工的完成波次数
							String sql3 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where left(receive_datetime,10)='" + DBDateTime + "'";
							if(staffName!=null && !"".equals(staffName))
								sql3 += " and a.staff_name='"+staffName+"' ";
							sql3+=" group by left(receive_datetime,10)";
						
							ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
							int groupCount = 0;
							if (rs3.next()) {
								groupCount = rs3.getInt(1);
							}
							rs3.close();
							if (dateTime.toString().equals(DBDateTime)) {
								SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
								groupBean.setSkuCount(sbgb.getSkuCount());
								groupBean.setProductCount(sbgb.getProductCount());
								groupBean.setOrderCount(sbgb.getOrderCount());
								groupBean.setPassageCount(sbgb.getPassageCount());
								groupBean.setGroupCount(groupCount);
								groupBean.setStaffName(sbgb.getStaffName());
								groupBean.setStaffCode(sbgb.getStaffCode());
								groupBean.setSortingTime(sbgb.getSortingTime());
								detailedList.add(groupBean);
								flag = false;
								break;
							}
						}
						if (flag) {
							SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
							groupBean.setSkuCount(0);
							groupBean.setProductCount(0);
							groupBean.setOrderCount(0);
							groupBean.setPassageCount(0);
							groupBean.setGroupCount(0);
							groupBean.setStaffName(sbgb.getStaffName());
							groupBean.setStaffCode(sbgb.getStaffCode());
							groupBean.setSortingTime(dateTime.toString());
							detailedList.add(groupBean);
						}
						dateTime.setLength(0);
					}
				} else {
					for (int i = stDay; i <= etDay; i++) {
						boolean flag = true;
						dateTime.append(startTime.substring(0, 8));
						if (i < 10) {
							dateTime.append("0");
						}
						dateTime.append(i);
						SortingBatchGroupBean sbgb = new SortingBatchGroupBean();
						for (int j = 0; j < list.size(); j++) {
							sbgb = (SortingBatchGroupBean) list.get(j);
							String DBDateTime = sbgb.getSortingTime();
							// 计算每个员工的完成波次数
							String sql4 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where left(receive_datetime,10)='" + DBDateTime + "'";
							if(staffName!=null && !"".equals(staffName))
								sql4 += " and a.staff_name='"+staffName+"' ";
							sql4 += " group by left(receive_datetime,10)";
							ResultSet rs4 = wareService.getDbOp().executeQuery(sql4);
							//System.out.println("-->"+sql4);
							int groupCount = 0;
							if (rs4.next()) {
								groupCount = rs4.getInt(1);
							}
							rs4.close();
							if (dateTime.toString().equals(DBDateTime)) {
								SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
								groupBean.setSkuCount(sbgb.getSkuCount());
								groupBean.setProductCount(sbgb.getProductCount());
								groupBean.setOrderCount(sbgb.getOrderCount());
								groupBean.setPassageCount(sbgb.getPassageCount());
								groupBean.setGroupCount(groupCount);
								groupBean.setStaffName(sbgb.getStaffName());
								groupBean.setStaffCode(sbgb.getStaffCode());
								groupBean.setSortingTime(sbgb.getSortingTime());
								detailedList.add(groupBean);
								flag = false;
								break;
							}
						}
						if (flag) {
							SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
							groupBean.setSkuCount(0);
							groupBean.setProductCount(0);
							groupBean.setOrderCount(0);
							groupBean.setPassageCount(0);
							groupBean.setGroupCount(0);
							groupBean.setStaffName(sbgb.getStaffName());
							groupBean.setStaffCode(sbgb.getStaffCode());
							groupBean.setSortingTime(dateTime.toString());
							detailedList.add(groupBean);
						}
						dateTime.setLength(0);
					}
				}
				
				//计算每个员工的撤单量
				String sql3 = "SELECT count(s.order_id) ,left(t.receive_datetime,10) FROM sorting_batch_group t left join sorting_batch_order s on t.id=s.sorting_group_id where s.delete_status=1 ";
				if(staffName!=null && !"".equals(staffName))
					sql3 += "and t.staff_name='"+staffName+"' ";
				sql3 +="and s.status<>0 and left(t.receive_datetime,10) between '"+startTime+"' and '"+endTime+"' group by left(t.receive_datetime,10)";
				ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
				while(rs3.next()){
					if(list != null){
						for(Iterator i = list.iterator(); i.hasNext();){
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
							if(rs3.getString(2) .equals( groupBean.getSortingTime())){
								groupBean.setCancelOrderCount(rs3.getInt(1));
							}
						}				
						
					}
					if(detailedList!=null){
						for(Iterator i = detailedList.iterator(); i.hasNext();){
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
							if(rs3.getString(2) .equals( groupBean.getSortingTime())){
								groupBean.setCancelOrderCount(rs3.getInt(1));
							}
						}
					}
				}
				rs3.close();
				SortingBatchGroupBean totalBean = new SortingBatchGroupBean();
				int totalAttendanceCount = 0;
				int totalGroupCount = 0;
				int totalOrderCount = 0;
				int totalSkuCount = 0;
				int totalProductCount = 0;
				int totalPassageCount = 0;
				int totalCalcelOrderCount = 0;
				if (list != null && list.size() > 0) {
					for (Iterator i = list.iterator(); i.hasNext();) {
						SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
						totalSkuCount = tempBean.getSkuCount() + totalSkuCount;
						totalProductCount = tempBean.getProductCount() + totalProductCount;
						totalOrderCount = tempBean.getOrderCount() + totalOrderCount;
						totalPassageCount = tempBean.getPassageCount() + totalPassageCount;
						totalGroupCount = tempBean.getGroupCount() + totalGroupCount;
						totalAttendanceCount = tempBean.getAttendanceCount() + totalAttendanceCount;
						totalCalcelOrderCount = tempBean.getCancelOrderCount() + totalCalcelOrderCount;
					}
				}
				totalBean.setSkuCount(totalSkuCount);
				totalBean.setProductCount(totalProductCount);
				totalBean.setOrderCount(totalOrderCount);
				totalBean.setPassageCount(totalPassageCount);
				totalBean.setGroupCount(totalGroupCount);
				totalBean.setAttendanceCount(totalAttendanceCount);
				totalBean.setStaffCount(list.size());
				totalBean.setCancelOrderCount(totalCalcelOrderCount);
				request.setAttribute("endTime", endTime);
				request.setAttribute("startTime", startTime);
				request.setAttribute("staffName", staffName);
				request.setAttribute("staffCode", staffCode);
				request.setAttribute("totalBean", totalBean);
				request.setAttribute("sibList", sibList);
				request.setAttribute("detailedList", detailedList);
				json.setRows(detailedList);
				HashMap map =new HashMap();
//				{field : 'sortingTime',title : ' 日期',align : 'center',width : '120'},
//				{field : 'groupCount',align : 'center',title : '波次数',sortable : true,width : '180'},
//				{field : 'orderCount',title : '订单数',align : 'center',width : '80'},
//				{field : 'skuCount',title : 'SKU数',align : 'center',width : '280'},
//				{field : 'productCount',title : '商品个数',align : 'center',width : '100'},
//				{field : 'passageCount',title : '巷道数',align : 'center',width : '100'},
//				{field : 'cancelOrderCount',title : '撤单数',align : 'center',width : '90'}
				map.put("sortingTime", "总数：");
				map.put("groupCount", totalGroupCount);
				map.put("orderCount", totalOrderCount);
				map.put("skuCount", totalSkuCount);
				map.put("productCount", totalProductCount);
				map.put("passageCount", totalPassageCount);
				map.put("cancelOrderCount", totalCalcelOrderCount);

				List list1 = new ArrayList();
				list1.add(map);
				json.setFooter(list1);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
			return  json;
			//return mapping.findForward("sortingStatisticalDetailed");
		}
		
		/**
		 * 分拣量统计excel导出
		 */
		@RequestMapping("/sortingStatisticsListExcel")
		public String sortingStatisticsListExcel(HttpServletRequest request, HttpServletResponse response) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			WareService wareService = new WareService(dbOp);

			String startTime = StringUtil.convertNull(request.getParameter("startTime"));
			String endTime = StringUtil.convertNull(request.getParameter("endTime"));
			String staffName = "";
			String staffCode = StringUtil.convertNull(request.getParameter("staffCode").trim());
			try {
				StringBuffer conditon = new StringBuffer();
				if ("".equals(startTime) || "".equals(endTime)) {
					startTime = DateUtil.formatDate(new Date());
					endTime = DateUtil.formatDate(new Date());
				}
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(df.parse(endTime));
				calendar.add(Calendar.DATE, +1); // 加一天
				endTime = df.format(calendar.getTime());

				conditon.append(" and left(a.receive_datetime, 10) between '" + startTime + "' and '" + endTime + "'");
				if (!"".equals(staffCode)) {
					conditon.append(" and cs.code='" + staffCode + "'");
					String staffSQL = " select name from cargo_staff where code='" + StringUtil.toSql(staffCode) + "'";
					ResultSet staffRS = wareService.getDbOp().executeQuery(staffSQL);
					if (staffRS.next()) {
						staffName = staffRS.getString("name");
					} else {
						request.setAttribute("msg", "员工编号不正确！");
						request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
						return null;
					}
					staffRS.close();
				}
				String sql = " select count(distinct d.product_code),sum(d.stockout_count),count(distinct c.order_id),count(distinct ci.passage_id)," + " count( distinct a.id),count(distinct left(a.receive_datetime,10)),cs.name,cs.code,cs.id from sorting_batch_group as a" + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where 1=1 " ;
				
				if(staffName!=null&&!"".equals(staffName))
					sql += " and a.staff_name='"+staffName+"' ";
				sql +=  conditon + " group by a.id";
				ResultSet rs = wareService.getDbOp().executeQuery(sql);
				List staffList = new ArrayList();
				int flag = 0;
				while (rs.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setSkuCount(rs.getInt(1));
					groupBean.setProductCount(rs.getInt(2));
					groupBean.setOrderCount(rs.getInt(3));
					groupBean.setPassageCount(rs.getInt(4));
					groupBean.setGroupCount(rs.getInt(5));
					groupBean.setAttendanceCount(rs.getInt(6));
					groupBean.setStaffName(rs.getString(7));
					groupBean.setStaffCode(rs.getString(8));
					groupBean.setStaffId(rs.getInt(9));
					if (staffList != null && staffList.size() > 0) {
						for (Iterator i = staffList.iterator(); i.hasNext();) {
							SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
							if (tempBean.getStaffCode().equals(groupBean.getStaffCode())) {
								tempBean.setSkuCount(tempBean.getSkuCount() + groupBean.getSkuCount());
								tempBean.setProductCount(tempBean.getProductCount() + groupBean.getProductCount());
								tempBean.setOrderCount(tempBean.getOrderCount() + groupBean.getOrderCount());
								tempBean.setPassageCount(tempBean.getPassageCount() + groupBean.getPassageCount());
								tempBean.setGroupCount(tempBean.getGroupCount() + groupBean.getGroupCount());
								tempBean.setAttendanceCount(tempBean.getAttendanceCount() + groupBean.getAttendanceCount());
								flag = 1;
								break;
							}
						}
						if (flag == 0) {
							staffList.add(groupBean);
						}
						flag = 0;
					} else {
						staffList.add(groupBean);
					}
				}
				rs.close();
				// 计算每个员工的出勤天数
				String sql1 = "select count(distinct left(receive_datetime,10)),cs.id from sorting_batch_group a  " + "join cargo_staff as cs on cs.id=a.staff_id where a.id>0 " + conditon + " group by cs.id";
				ResultSet rs1 = wareService.getDbOp().executeQuery(sql1);
				List staffAttendanceList = new ArrayList();
				while (rs1.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setAttendanceCount(rs1.getInt(1));
					groupBean.setStaffId(rs1.getInt(2));
					staffAttendanceList.add(groupBean);
				}
				rs1.close();
				if (staffList != null) {
					for (Iterator i = staffList.iterator(); i.hasNext();) {
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
						if (staffAttendanceList != null) {
							for (int j = 0; j < staffAttendanceList.size(); j++) {
								SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffAttendanceList.get(j);
								if (AttendanceBean.getStaffId() == groupBean.getStaffId()) {
									groupBean.setAttendanceCount(AttendanceBean.getAttendanceCount());
								}
							}
						}
					}

				}

				// 计算每个员工的完成波次数
				String sql2 = "select count(distinct a.id),a.staff_id from sorting_batch_group a " + "" + "join cargo_staff as cs on cs.id=a.staff_id where 1=1";
				if(staffName!=null&&!"".equals(staffName))
					sql2 += " and a.staff_name='"+staffName+"' ";
				sql2 +=  conditon + " group by cs.id";
				ResultSet rs2 = wareService.getDbOp().executeQuery(sql2);
				List staffGroupList = new ArrayList();
				while (rs2.next()) {
					SortingBatchGroupBean groupBean = new SortingBatchGroupBean();
					groupBean.setGroupCount(rs2.getInt(1));
					groupBean.setStaffId(rs2.getInt(2));
					staffGroupList.add(groupBean);
				}
				rs2.close();
				if (staffList != null) {
					for (Iterator i = staffList.iterator(); i.hasNext();) {
						SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
						if (staffGroupList != null) {
							for (int j = 0; j < staffGroupList.size(); j++) {
								SortingBatchGroupBean AttendanceBean = (SortingBatchGroupBean) staffGroupList.get(j);
								if (AttendanceBean.getStaffId() == groupBean.getStaffId()) {
									groupBean.setGroupCount(AttendanceBean.getGroupCount());
								}
							}
						}
					}

				}
				
				//计算每个员工的撤单量
				String sql3 = "SELECT count(s.order_id) ,t.staff_id FROM sorting_batch_group t left join sorting_batch_order s on t.id=s.sorting_group_id where s.delete_status=1 and s.status<>0 and left(t.receive_datetime,10) between '"+startTime+"' and '"+endTime+"' group by t.staff_id";
				ResultSet rs3 = wareService.getDbOp().executeQuery(sql3);
				while(rs3.next()){
					if(staffList != null){
						for(Iterator i = staffList.iterator(); i.hasNext();){
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) i.next();
							if(rs3.getInt(2) == groupBean.getStaffId()){
								groupBean.setCancelOrderCount(rs3.getInt(1));
							}
						}
					}
				}
				rs3.close();
				SortingBatchGroupBean totalBean = new SortingBatchGroupBean();
				int totalAttendanceCount = 0;
				int totalGroupCount = 0;
				int totalOrderCount = 0;
				int totalSkuCount = 0;
				int totalProductCount = 0;
				int totalPassageCount = 0;
				int totalCancelOrderCount = 0;
				if (staffList != null && staffList.size() > 0) {
					for (Iterator i = staffList.iterator(); i.hasNext();) {
						SortingBatchGroupBean tempBean = (SortingBatchGroupBean) i.next();
						totalSkuCount = tempBean.getSkuCount() + totalSkuCount;
						totalProductCount = tempBean.getProductCount() + totalProductCount;
						totalOrderCount = tempBean.getOrderCount() + totalOrderCount;
						totalPassageCount = tempBean.getPassageCount() + totalPassageCount;
						totalGroupCount = tempBean.getGroupCount() + totalGroupCount;
						totalAttendanceCount = tempBean.getAttendanceCount() + totalAttendanceCount;
						totalCancelOrderCount = tempBean.getCancelOrderCount() + totalCancelOrderCount;
					}
				}
				totalBean.setSkuCount(totalSkuCount);
				totalBean.setProductCount(totalProductCount);
				totalBean.setOrderCount(totalOrderCount);
				totalBean.setPassageCount(totalPassageCount);
				totalBean.setGroupCount(totalGroupCount);
				totalBean.setAttendanceCount(totalAttendanceCount);
				totalBean.setStaffCount(staffList.size());
				totalBean.setCancelOrderCount(totalCancelOrderCount);

				calendar.setTime(df.parse(endTime));
				calendar.add(Calendar.DATE, -1); // 减一天
				endTime = df.format(calendar.getTime());

				request.setAttribute("totalBean", totalBean);
				request.setAttribute("staffList", staffList);
				request.setAttribute("startTime", startTime);
				request.setAttribute("endTime", endTime);
				request.setAttribute("staffName", staffName);
				request.setAttribute("staffCode", staffCode);

				request.setAttribute("totalBean", totalBean);
				request.setAttribute("staffList", staffList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				wareService.releaseAll();
			}
			return  "admin/rec/oper/sorting/sortingStatisticsExcel";
		}
		/**
		 * 分拣监控列表
		 */
		public EasyuiDataGridJson sortingMonitorList(HttpServletRequest request, HttpServletResponse response) {
			EasyuiDataGridJson json = new EasyuiDataGridJson();
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				voUser user = (voUser) request.getSession().getAttribute("userView");
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(646)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				List teamList = new ArrayList();// teamList里保存着多个分好组的StaffList
				// 1.求当天第一个波次ID号，和领单时间
				String firstDatetime = new String();
				String laterDatetime = new String();
				String sql = "select id,receive_datetime from sorting_batch_group where staff_name is not null and receive_datetime between '" + DateUtil.getNowDateStr() + " 00:00:00' and '" + DateUtil.getNowDateStr() + " 23:59:59' order by receive_datetime limit 1 ";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				if (rs.next()) {
					firstDatetime = rs.getString(2);
				}
				rs.close();
				// 2.求出半个小时之后的时间
				if (!"".equals(firstDatetime) && firstDatetime.length() > 0) {
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					c.setTime(sdf.parse(firstDatetime));
					c.add(Calendar.MINUTE, 30);
					laterDatetime = sdf.format(c.getTime());
				} else {
					request.setAttribute("msg", "今天还没有员工领单");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				// 3.求出第一班小组时间段之内所有进行过领单的员工列表
				String sql1 = "select a.staff_id,b.code,a.staff_name ,a.receive_datetime  from sorting_batch_group a " + "join cargo_staff b on a.staff_id=b.id where " + "a.receive_datetime between '" + StringUtil.cutString(firstDatetime, 0, 19) + "' and '" + laterDatetime + "' and a.staff_name is not null group by b.id";
				List staffList = new ArrayList();
				ResultSet rs1 = service.getDbOp().executeQuery(sql1);
				int maxId = 0;
				while (rs1.next()) {
					SortingBatchGroupBean bean = new SortingBatchGroupBean();
					bean.setStaffId(rs1.getInt(1));
					bean.setStaffCode(rs1.getString(2));
					bean.setStaffName(rs1.getString(3));
					bean.setReceiveDatetime(rs1.getString(4));
					staffList.add(bean);
				}
				rs1.close();
				teamList.add(staffList);
				// 5.分组
				teamList = fenban(teamList, laterDatetime);
				List xiaojiList = new ArrayList();
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
					for (Iterator i = teamList.iterator(); i.hasNext();) {
						List tempList = (List) i.next();
						SortingBatchGroupBean xiaojiBean = new SortingBatchGroupBean();
						int xiaojiOvertimeOrderCount = 0;// 小计分拣超时订单数
						int xiaojiGroupCount = 0;// 小计完成波次数
						int xiaojiCompleteOrderCount = 0;// 小计完成订单数
						int xiaojiNoCompleteOrderCount = 0;// 小计未完成订单数
						int xiaojiSkuCount = 0;// 小计完成SKU数
						int xiaojiProductCount = 0;// 小计完成商品数
						int xiaojiPassageCount = 0;// 小计完成巷道数
						for (Iterator j = tempList.iterator(); j.hasNext();) {
							staffCount++;
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) j.next();
							// 姓名，员工号，完成SKU，完成商品，巷道数
							String sql0 = " select cs.name,cs.code,count( distinct d.product_code),sum(d.stockout_count),count(distinct ci.passage_id)" + " from sorting_batch_group as a " + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where (b.status=3 or (b.status=2 and b.delete_status=1)) and cs.id=" + groupBean.getStaffId() + " and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' group by a.id";
							ResultSet rs0 = service.getDbOp().executeQuery(sql0);
							int groupCount = 0;
							int skuCount = 0;
							int productCount = 0;
							int passageCount = 0;
							while (rs0.next()) {
								groupBean.setStaffName(rs0.getString(1));
								groupBean.setStaffCode(rs0.getString(2));
								skuCount += rs0.getInt(3);
								productCount += rs0.getInt(4);
								passageCount += rs0.getInt(5);
							}
							rs0.close();
							groupBean.setGroupCount(groupCount);
							groupBean.setSkuCount(skuCount);
							groupBean.setProductCount(productCount);
							groupBean.setPassageCount(passageCount);
							// 计算每个员工的完成波次数
							String sql11 = "select count(distinct a.id)from sorting_batch_group a " + "join cargo_staff as cs on cs.id=a.staff_id where (a.status=2 or (a.status=3 and a.staff_name is not null)) and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and cs.id=" + groupBean.getStaffId();
							ResultSet rs11 = service.getDbOp().executeQuery(sql11);
							if (rs11.next()) {
								groupBean.setGroupCount(rs11.getInt(1));
							}
							rs11.close();
							// 分拣超时订单数量
							Calendar c = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							c.setTime(sdf.parse(groupBean.getReceiveDatetime()));
							c.add(Calendar.MINUTE, 30);
							String sql7 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where" + " b.status=2 and b.delete_status<>1 and  date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "' and a.staff_id=" + groupBean.getStaffId() + " and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
							ResultSet rs7 = service.getDbOp().executeQuery(sql7);
							if (rs7.next()) {
								groupBean.setOverTimeOrderCount(rs7.getInt(1));
							}
							rs7.close();
							// 员工姓名，员工编号
							String sql8 = "select code,name from cargo_staff where id=" + groupBean.getStaffId();
							ResultSet rs8 = service.getDbOp().executeQuery(sql8);
							if (rs8.next()) {
								groupBean.setStaffCode(rs8.getString(1));
								groupBean.setStaffName(rs8.getString(2));
							}
							rs8.close();
							// 作业开始时间
							String sql3 = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " limit 1 ";
							ResultSet rs3 = service.getDbOp().executeQuery(sql3);
							if (rs3.next()) {
								groupBean.setBegindatetime(rs3.getString(1));
							}
							rs3.close();
							// 最后领单时间
							String sql4 = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " order by receive_datetime desc limit 1 ";
							ResultSet rs4 = service.getDbOp().executeQuery(sql4);
							if (rs4.next()) {
								groupBean.setFinallReceiveOrderTime(rs4.getString(1));
							}
							rs4.close();
							// 完成的订单
							String sql5 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where (b.status=3 or (b.status=2 and b.delete_status=1)) and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
							ResultSet rs5 = service.getDbOp().executeQuery(sql5);
							if (rs5.next()) {
								groupBean.setCompleteOrderCount(rs5.getInt(1));
							}
							rs5.close();
							// 未完成的订单
							String sql6 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where b.status<>3  and b.delete_status<>1 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
							ResultSet rs6 = service.getDbOp().executeQuery(sql6);
							if (rs6.next()) {
								groupBean.setNoCompleteOrderCount(rs6.getInt(1));
							}
							rs6.close();

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
						String sql9 = "select min(receive_datetime) from sorting_batch_group where staff_id in(" + staffId + ") and  left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
						ResultSet rs9 = service.getDbOp().executeQuery(sql9);
						if (rs9.next()) {
							xiaojiBean.setTeamBeginTime(rs9.getString(1));
							Calendar c = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							c.setTime(sdf.parse(rs9.getString(1)));
							c.add(Calendar.MINUTE, 30);
							xiaojiBean.setTeamEndTime(sdf.format(c.getTime()));
						}
						rs9.close();
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

				// 已完成波次数
				int completeGroupCount = 0;
				String sql10 = "select count(distinct a.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where a.status=2 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
				ResultSet rs10 = service.getDbOp().executeQuery(sql10);
				if (rs10.next()) {
					completeGroupCount = rs10.getInt(1);
				}
				rs10.close();
				// 已完成订单数
				int completeOrderCount = 0;
				String sql16 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where  left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and  ( b.status=3 or (b.status=2 and b.delete_status=1))";
				ResultSet rs16 = service.getDbOp().executeQuery(sql16);
				if (rs16.next()) {
					completeOrderCount = rs16.getInt(1);
				}
				rs16.close();
				// 未领取波次数
				int noReceiveGroupCount = 0;
				String sql11 = "select count(id) from sorting_batch_group where status=0";
				ResultSet rs11 = service.getDbOp().executeQuery(sql11);
				if (rs11.next()) {
					noReceiveGroupCount = rs11.getInt(1);
				}
				rs11.close();
				// 未领取订单数
				int noReceiveOrderCount = 0;
				String sql14 = "select count( b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where a.status=0 and b.status in(0,1) and b.delete_status<>1";
				ResultSet rs14 = service.getDbOp().executeQuery(sql14);
				if (rs14.next()) {
					noReceiveOrderCount = rs14.getInt(1);
				}
				rs14.close();
				// 未处理订单数
				int noDisposeOrderCount = 0;
				String sql12 = "select count(*) from user_order a join order_stock b on a.id=b.order_id where b.status in(0,1)";
				ResultSet rs12 = service.getDbOp().executeQuery(sql12);
				if (rs12.next()) {
					noDisposeOrderCount = rs12.getInt(1);
				}
				rs12.close();
				// 分拣超时订单数
				int overTimeOrderCount = 0;
				String sql13 = "select count(b.id) from  sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id " + "where a.status=1 and b.status=2 and b.delete_status<>1 and date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "'";
				ResultSet rs13 = service.getDbOp().executeQuery(sql13);
				if (rs13.next()) {
					overTimeOrderCount = rs13.getInt(1);
				}
				rs13.close();
				request.setAttribute("firstDatetime", firstDatetime);
				request.setAttribute("laterDatetime", laterDatetime);
				request.setAttribute("completeGroupCount", completeGroupCount + "");
				request.setAttribute("completeOrderCount", completeOrderCount + "");
				request.setAttribute("noReceiveGroupCount", noReceiveGroupCount + "");
				request.setAttribute("noReceiveOrderCount", noReceiveOrderCount + "");
				request.setAttribute("noDisposeOrderCount", noDisposeOrderCount + "");
				request.setAttribute("overTimeOrderCount", overTimeOrderCount + "");
				request.setAttribute("teamList", teamList);
				request.setAttribute("xiaojiList", xiaojiList);
				request.setAttribute("zongjiBean", zongjiBean);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
			return json;
		}
		/**
		 * 分拣监控时间列表
		 */
		@SuppressWarnings("unchecked")
		@RequestMapping("/sortingMonitorTimeList")
		@ResponseBody
		public EasyuiDataGridJson sortingMonitorTimeList(HttpServletRequest request, HttpServletResponse response) {
			EasyuiDataGridJson json = new EasyuiDataGridJson();
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				voUser user = (voUser) request.getSession().getAttribute("userView");
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(646)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				List teamList = new ArrayList();// teamList里保存着多个分好组的StaffList
				// 1.求当天第一个波次ID号，和领单时间
				String firstDatetime = new String();
				String laterDatetime = new String();
				String sql = "select id,receive_datetime from sorting_batch_group where staff_name is not null and receive_datetime between '" + DateUtil.getNowDateStr() + " 00:00:00' and '" + DateUtil.getNowDateStr() + " 23:59:59' order by receive_datetime limit 1 ";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				if (rs.next()) {
					firstDatetime = rs.getString(2);
				}
				rs.close();
				// 2.求出半个小时之后的时间
				if (!"".equals(firstDatetime) && firstDatetime.length() > 0) {
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					c.setTime(sdf.parse(firstDatetime));
					c.add(Calendar.MINUTE, 30);
					laterDatetime = sdf.format(c.getTime());
				} else {
					request.setAttribute("msg", "今天还没有员工领单");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				// 3.求出第一班小组时间段之内所有进行过领单的员工列表
				String sql1 = "select a.staff_id,b.code,a.staff_name ,a.receive_datetime  from sorting_batch_group a " + "join cargo_staff b on a.staff_id=b.id where " + "a.receive_datetime between '" + StringUtil.cutString(firstDatetime, 0, 19) + "' and '" + laterDatetime + "' and a.staff_name is not null group by b.id";
				List staffList = new ArrayList();
				ResultSet rs1 = service.getDbOp().executeQuery(sql1);
				int maxId = 0;
				while (rs1.next()) {
					SortingBatchGroupBean bean = new SortingBatchGroupBean();
					bean.setStaffId(rs1.getInt(1));
					bean.setStaffCode(rs1.getString(2));
					bean.setStaffName(rs1.getString(3));
					bean.setReceiveDatetime(rs1.getString(4));
					staffList.add(bean);
				}
				rs1.close();
				teamList.add(staffList);
				// 5.分组
				teamList = fenban(teamList, laterDatetime);
				List xiaojiList = new ArrayList();
				SortingBatchGroupBean xiaojiBean1 = new SortingBatchGroupBean();
				xiaojiBean1.setTeamBeginTime(firstDatetime+"--"+laterDatetime);
				xiaojiList.add(xiaojiBean1);
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
					for (Iterator i = teamList.iterator(); i.hasNext();) {
						List tempList = (List) i.next();
						SortingBatchGroupBean xiaojiBean = new SortingBatchGroupBean();
						int xiaojiOvertimeOrderCount = 0;// 小计分拣超时订单数
						int xiaojiGroupCount = 0;// 小计完成波次数
						int xiaojiCompleteOrderCount = 0;// 小计完成订单数
						int xiaojiNoCompleteOrderCount = 0;// 小计未完成订单数
						int xiaojiSkuCount = 0;// 小计完成SKU数
						int xiaojiProductCount = 0;// 小计完成商品数
						int xiaojiPassageCount = 0;// 小计完成巷道数
						for (Iterator j = tempList.iterator(); j.hasNext();) {
							staffCount++;
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) j.next();
							// 姓名，员工号，完成SKU，完成商品，巷道数
							String sql0 = " select cs.name,cs.code,count( distinct d.product_code),sum(d.stockout_count),count(distinct ci.passage_id)" + " from sorting_batch_group as a " + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where (b.status=3 or (b.status=2 and b.delete_status=1)) and cs.id=" + groupBean.getStaffId() + " and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' group by a.id";
							ResultSet rs0 = service.getDbOp().executeQuery(sql0);
							int groupCount = 0;
							int skuCount = 0;
							int productCount = 0;
							int passageCount = 0;
							while (rs0.next()) {
								groupBean.setStaffName(rs0.getString(1));
								groupBean.setStaffCode(rs0.getString(2));
								skuCount += rs0.getInt(3);
								productCount += rs0.getInt(4);
								passageCount += rs0.getInt(5);
							}
							rs0.close();
							groupBean.setGroupCount(groupCount);
							groupBean.setSkuCount(skuCount);
							groupBean.setProductCount(productCount);
							groupBean.setPassageCount(passageCount);
							// 计算每个员工的完成波次数
							String sql11 = "select count(distinct a.id)from sorting_batch_group a " + "join cargo_staff as cs on cs.id=a.staff_id where (a.status=2 or (a.status=3 and a.staff_name is not null)) and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and cs.id=" + groupBean.getStaffId();
							ResultSet rs11 = service.getDbOp().executeQuery(sql11);
							if (rs11.next()) {
								groupBean.setGroupCount(rs11.getInt(1));
							}
							rs11.close();
							// 分拣超时订单数量
							Calendar c = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							c.setTime(sdf.parse(groupBean.getReceiveDatetime()));
							c.add(Calendar.MINUTE, 30);
							String sql7 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where" + " b.status=2 and b.delete_status<>1 and  date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "' and a.staff_id=" + groupBean.getStaffId() + " and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
							ResultSet rs7 = service.getDbOp().executeQuery(sql7);
							if (rs7.next()) {
								groupBean.setOverTimeOrderCount(rs7.getInt(1));
							}
							rs7.close();
							// 员工姓名，员工编号
							String sql8 = "select code,name from cargo_staff where id=" + groupBean.getStaffId();
							ResultSet rs8 = service.getDbOp().executeQuery(sql8);
							if (rs8.next()) {
								groupBean.setStaffCode(rs8.getString(1));
								groupBean.setStaffName(rs8.getString(2));
							}
							rs8.close();
							// 作业开始时间
							String sql3 = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " limit 1 ";
							ResultSet rs3 = service.getDbOp().executeQuery(sql3);
							if (rs3.next()) {
								groupBean.setBegindatetime(rs3.getString(1));
							}
							rs3.close();
							// 最后领单时间
							String sql4 = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " order by receive_datetime desc limit 1 ";
							ResultSet rs4 = service.getDbOp().executeQuery(sql4);
							if (rs4.next()) {
								groupBean.setFinallReceiveOrderTime(rs4.getString(1));
							}
							rs4.close();
							// 完成的订单
							String sql5 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where (b.status=3 or (b.status=2 and b.delete_status=1)) and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
							ResultSet rs5 = service.getDbOp().executeQuery(sql5);
							if (rs5.next()) {
								groupBean.setCompleteOrderCount(rs5.getInt(1));
							}
							rs5.close();
							// 未完成的订单
							String sql6 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where b.status<>3  and b.delete_status<>1 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
							ResultSet rs6 = service.getDbOp().executeQuery(sql6);
							if (rs6.next()) {
								groupBean.setNoCompleteOrderCount(rs6.getInt(1));
							}
							rs6.close();

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
						String sql9 = "select min(receive_datetime) from sorting_batch_group where staff_id in(" + staffId + ") and  left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
						ResultSet rs9 = service.getDbOp().executeQuery(sql9);
						if (rs9.next()) {
							Calendar c = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							c.setTime(sdf.parse(rs9.getString(1)));
							c.add(Calendar.MINUTE, 30);
							xiaojiBean.setTeamBeginTime(rs9.getString(1)+"--"+sdf.format(c.getTime()));
							xiaojiBean.setTeamEndTime(sdf.format(c.getTime()));
						}
						rs9.close();
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

				// 已完成波次数
				int completeGroupCount = 0;
				String sql10 = "select count(distinct a.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where a.status=2 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
				ResultSet rs10 = service.getDbOp().executeQuery(sql10);
				if (rs10.next()) {
					completeGroupCount = rs10.getInt(1);
				}
				rs10.close();
				// 已完成订单数
				int completeOrderCount = 0;
				String sql16 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where  left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and  ( b.status=3 or (b.status=2 and b.delete_status=1))";
				ResultSet rs16 = service.getDbOp().executeQuery(sql16);
				if (rs16.next()) {
					completeOrderCount = rs16.getInt(1);
				}
				rs16.close();
				// 未领取波次数
				int noReceiveGroupCount = 0;
				String sql11 = "select count(id) from sorting_batch_group where status=0";
				ResultSet rs11 = service.getDbOp().executeQuery(sql11);
				if (rs11.next()) {
					noReceiveGroupCount = rs11.getInt(1);
				}
				rs11.close();
				// 未领取订单数
				int noReceiveOrderCount = 0;
				String sql14 = "select count( b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where a.status=0 and b.status in(0,1) and b.delete_status<>1";
				ResultSet rs14 = service.getDbOp().executeQuery(sql14);
				if (rs14.next()) {
					noReceiveOrderCount = rs14.getInt(1);
				}
				rs14.close();
				// 未处理订单数
				int noDisposeOrderCount = 0;
				String sql12 = "select count(*) from user_order a join order_stock b on a.id=b.order_id where b.status in(0,1)";
				ResultSet rs12 = service.getDbOp().executeQuery(sql12);
				if (rs12.next()) {
					noDisposeOrderCount = rs12.getInt(1);
				}
				rs12.close();
				// 分拣超时订单数
				int overTimeOrderCount = 0;
				String sql13 = "select count(b.id) from  sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id " + "where a.status=1 and b.status=2 and b.delete_status<>1 and date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "'";
				ResultSet rs13 = service.getDbOp().executeQuery(sql13);
				if (rs13.next()) {
					overTimeOrderCount = rs13.getInt(1);
				}
				rs13.close();
				request.setAttribute("firstDatetime", firstDatetime);
				request.setAttribute("laterDatetime", laterDatetime);
				request.setAttribute("completeGroupCount", completeGroupCount + "");
				request.setAttribute("completeOrderCount", completeOrderCount + "");
				request.setAttribute("noReceiveGroupCount", noReceiveGroupCount + "");
				request.setAttribute("noReceiveOrderCount", noReceiveOrderCount + "");
				request.setAttribute("noDisposeOrderCount", noDisposeOrderCount + "");
				request.setAttribute("overTimeOrderCount", overTimeOrderCount + "");
				request.setAttribute("teamList", teamList);
				request.setAttribute("xiaojiList", xiaojiList);
				request.setAttribute("zongjiBean", zongjiBean);
				json.setRows(xiaojiList);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
			return json;
		}
		/**
		 * 分拣监控列表导出excel
		 */
		public ActionForward sortingMonitorExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				voUser user = (voUser) request.getSession().getAttribute("userView");
				UserGroupBean group = user.getGroup();
				if (!group.isFlag(646)) {
					request.setAttribute("msg", "您没有此权限!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				List teamList = new ArrayList();// teamList里保存着多个分好组的StaffList
				// 1.求当天第一个波次ID号，和领单时间
				String firstDatetime = new String();
				String laterDatetime = new String();
				String sql = "select id,receive_datetime from sorting_batch_group where staff_name is not null and receive_datetime between '" + DateUtil.getNowDateStr() + " 00:00:00' and '" + DateUtil.getNowDateStr() + " 23:59:59' order by receive_datetime limit 1 ";
				ResultSet rs = service.getDbOp().executeQuery(sql);
				if (rs.next()) {
					firstDatetime = rs.getString(2);
				}
				rs.close();
				// 2.求出半个小时之后的时间
				if (!"".equals(firstDatetime) && firstDatetime.length() > 0) {
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					c.setTime(sdf.parse(firstDatetime));
					c.add(Calendar.MINUTE, 30);
					laterDatetime = sdf.format(c.getTime());
				} else {
					request.setAttribute("msg", "今天还没有员工领单");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				// 3.求出第一班小组时间段之内所有进行过领单的员工列表
				String sql1 = "select a.staff_id,b.code,a.staff_name ,a.receive_datetime  from sorting_batch_group a " + "join cargo_staff b on a.staff_id=b.id where " + "a.receive_datetime between '" + StringUtil.cutString(firstDatetime, 0, 19) + "' and '" + laterDatetime + "' and a.staff_name is not null group by b.id";
				List staffList = new ArrayList();
				ResultSet rs1 = service.getDbOp().executeQuery(sql1);
				while (rs1.next()) {
					SortingBatchGroupBean bean = new SortingBatchGroupBean();
					bean.setStaffId(rs1.getInt(1));
					bean.setStaffCode(rs1.getString(2));
					bean.setStaffName(rs1.getString(3));
					bean.setReceiveDatetime(rs1.getString(4));
					staffList.add(bean);
				}
				rs1.close();
				// 4.求该时间段中最后波次的ID
				// String sql2 =
				// "select max(id) from sorting_batch_group where receive_datetime between '"
				// + StringUtil.cutString(firstDatetime, 0, 19) + "' and '" +
				// laterDatetime + "'";
				// ResultSet rs2 = service.getDbOp().executeQuery(sql2);
				// if (rs2.next()) {
				// maxId = rs2.getInt(1);
				// }
				// rs2.close();
				teamList.add(staffList);
				// 5.分组
				teamList = fenban(teamList, laterDatetime);
				List xiaojiList = new ArrayList();
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
					for (Iterator i = teamList.iterator(); i.hasNext();) {
						List tempList = (List) i.next();
						SortingBatchGroupBean xiaojiBean = new SortingBatchGroupBean();
						int xiaojiOvertimeOrderCount = 0;// 小计分拣超时订单数
						int xiaojiGroupCount = 0;// 小计完成波次数
						int xiaojiCompleteOrderCount = 0;// 小计完成订单数
						int xiaojiNoCompleteOrderCount = 0;// 小计未完成订单数
						int xiaojiSkuCount = 0;// 小计完成SKU数
						int xiaojiProductCount = 0;// 小计完成商品数
						int xiaojiPassageCount = 0;// 小计完成巷道数
						for (Iterator j = tempList.iterator(); j.hasNext();) {
							staffCount++;
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) j.next();
							// 姓名，员工号，完成SKU，完成商品，巷道数
							String sql0 = " select cs.name,cs.code,count( distinct d.product_code),sum(d.stockout_count),count(distinct ci.passage_id)" + " from sorting_batch_group as a " + " join cargo_staff as cs on cs.id=a.staff_id " + " join sorting_batch_order as b on a.id = b.sorting_group_id " + " join order_stock as c on b.order_id = c.order_id " + " join order_stock_product as d on c.id = d.order_stock_id " + " join order_stock_product_cargo as e on d.id = e.order_stock_product_id " + " join cargo_info as ci on e.cargo_whole_code=ci.whole_code" + " where (b.status=3 or (b.status=2 and b.delete_status=1)) and cs.id=" + groupBean.getStaffId() + " and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' group by a.id";
							ResultSet rs0 = service.getDbOp().executeQuery(sql0);
							int groupCount = 0;
							int skuCount = 0;
							int productCount = 0;
							int passageCount = 0;
							while (rs0.next()) {
								groupBean.setStaffName(rs0.getString(1));
								groupBean.setStaffCode(rs0.getString(2));
								skuCount += rs0.getInt(3);
								productCount += rs0.getInt(4);
								passageCount += rs0.getInt(5);
							}
							rs0.close();
							groupBean.setGroupCount(groupCount);
							groupBean.setSkuCount(skuCount);
							groupBean.setProductCount(productCount);
							groupBean.setPassageCount(passageCount);
							// 计算每个员工的完成波次数
							String sql11 = "select count(distinct a.id)from sorting_batch_group a " + "join cargo_staff as cs on cs.id=a.staff_id where (a.status=2 or (a.status=3 and a.staff_name is not null)) and left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and cs.id=" + groupBean.getStaffId();
							ResultSet rs11 = service.getDbOp().executeQuery(sql11);
							if (rs11.next()) {
								groupBean.setGroupCount(rs11.getInt(1));
							}
							rs11.close();
							// 分拣超时订单数量
							Calendar c = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							c.setTime(sdf.parse(groupBean.getReceiveDatetime()));
							c.add(Calendar.MINUTE, 30);
							String sql7 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where" + " b.status=2 and a.status=1 and b.delete_status<>1 and  date_add(a.receive_datetime, interval 2 hour)<'" + DateUtil.getNow() + "' and a.staff_id=" + groupBean.getStaffId() + " and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
							ResultSet rs7 = service.getDbOp().executeQuery(sql7);
							if (rs7.next()) {
								groupBean.setOverTimeOrderCount(rs7.getInt(1));
							}
							rs7.close();
							// 员工姓名，员工编号
							String sql8 = "select code,name from cargo_staff where id=" + groupBean.getStaffId();
							ResultSet rs8 = service.getDbOp().executeQuery(sql8);
							if (rs8.next()) {
								groupBean.setStaffCode(rs8.getString(1));
								groupBean.setStaffName(rs8.getString(2));
							}
							rs8.close();
							// 作业开始时间
							String sql3 = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " limit 1 ";
							ResultSet rs3 = service.getDbOp().executeQuery(sql3);
							if (rs3.next()) {
								groupBean.setBegindatetime(rs3.getString(1));
							}
							rs3.close();
							// 最后领单时间
							String sql4 = "select b.receive_datetime from cargo_staff a join sorting_batch_group b on a.id=b.staff_id  where left(b.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.id=" + groupBean.getStaffId() + " order by receive_datetime desc limit 1 ";
							ResultSet rs4 = service.getDbOp().executeQuery(sql4);
							if (rs4.next()) {
								groupBean.setFinallReceiveOrderTime(rs4.getString(1));
							}
							rs4.close();
							// 完成的订单
							String sql5 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where (b.status=3 or (b.status=2 and b.delete_status=1)) and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
							ResultSet rs5 = service.getDbOp().executeQuery(sql5);
							if (rs5.next()) {
								groupBean.setCompleteOrderCount(rs5.getInt(1));
							}
							rs5.close();
							// 未完成的订单
							String sql6 = "select count(b.id) from sorting_batch_group a join sorting_batch_order b on a.id=b.sorting_group_id where b.status<>3  and b.delete_status<>1 and left(a.receive_datetime,10)='" + DateUtil.getNowDateStr() + "' and a.staff_id=" + groupBean.getStaffId();
							ResultSet rs6 = service.getDbOp().executeQuery(sql6);
							if (rs6.next()) {
								groupBean.setNoCompleteOrderCount(rs6.getInt(1));
							}
							rs6.close();

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
						String sql9 = "select min(receive_datetime) from sorting_batch_group where staff_id in(" + staffId + ") and  left(receive_datetime,10)='" + DateUtil.getNowDateStr() + "'";
						ResultSet rs9 = service.getDbOp().executeQuery(sql9);
						if (rs9.next()) {
							xiaojiBean.setTeamBeginTime(rs9.getString(1));
							Calendar c = Calendar.getInstance();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							c.setTime(sdf.parse(rs9.getString(1)));
							c.add(Calendar.MINUTE, 30);
							xiaojiBean.setTeamEndTime(sdf.format(c.getTime()));
						}
						rs9.close();
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
			}
			return mapping.findForward("sortingMonitorExcel");
		}

		/*
		 * staffList已经分配到组别里的员工列表 teamList小组列表，元素为每个小组员工的列表
		 */
		public List fenban(List teamList, String firstDatetime) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			SortingInfoService service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
			synchronized (cargoLock) {
				try {
					StringBuffer staffId = new StringBuffer();
					for (int i = 0; i < teamList.size(); i++) {
						List groupList = (List) teamList.get(i);
						for (int j = 0; j < groupList.size(); j++) {
							SortingBatchGroupBean groupBean = (SortingBatchGroupBean) groupList.get(j);
							if (staffId.toString().equals("") || staffId.toString().length() == 0) {
								staffId.append(groupBean.getStaffId());
							} else {
								staffId.append("," + groupBean.getStaffId());
							}
						}
					}
					SortingBatchGroupBean groupBean = service.getSortingBatchGroupInfo(" receive_datetime>'" + firstDatetime + "' and staff_name is not null  and staff_id not in (" + staffId + ") limit 1");
					if (groupBean != null) {// 说明找到了下一个波次的ID
						Calendar c = Calendar.getInstance();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						c.setTime(sdf.parse(groupBean.getReceiveDatetime()));
						c.add(Calendar.MINUTE, 30);
						String laterDatetime = sdf.format(c.getTime());// 求出了半个小时之后的时间
						String sql1 = "select a.staff_id,b.code,a.staff_name ,a.receive_datetime  from sorting_batch_group a " + "join cargo_staff b on a.staff_id=b.id where " + "a.receive_datetime between '" + StringUtil.cutString(groupBean.getReceiveDatetime(), 19) + "' and '" + laterDatetime + "' and a.staff_name is not null and a.staff_id not in (" + staffId + ") group by b.id";
						List staffList = new ArrayList();
						ResultSet rs1 = service.getDbOp().executeQuery(sql1);
						while (rs1.next()) {
							SortingBatchGroupBean bean = new SortingBatchGroupBean();
							bean.setStaffId(rs1.getInt(1));
							bean.setStaffCode(rs1.getString(2));
							bean.setStaffName(rs1.getString(3));
							bean.setReceiveDatetime(rs1.getString(4));
							staffList.add(bean);
						}
						rs1.close();
						teamList.add(staffList);
						String sql = "select max(id) from sorting_batch_group where receive_datetime between '" + StringUtil.cutString(groupBean.getReceiveDatetime(), 19) + "' and '" + laterDatetime + "' ";
						ResultSet rs = service.getDbOp().executeQuery(sql);
						// if (rs.next()) {
						// maxId = rs.getInt(1);
						// }
						// rs.close();
						fenban(teamList, laterDatetime);
					} else {
						return teamList;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					service.releaseAll();
				}
			}
			return teamList;
		}
		@RequestMapping("/generateBarCode")
		public void generateBarCode(HttpServletRequest request,HttpServletResponse response){
			String content = request.getParameter("code");
			JBBCodeHandle jbbcode = new JBBCodeHandle();
			if(content != null){
				jbbcode.createJBBCode(content, response);
			}
		}

}
