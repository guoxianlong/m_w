package mmb.rec.oper.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.common.dao.CommonDao;
import mmb.common.service.CommonService;
import mmb.rec.oper.dao.AuditPackageDao;
import mmb.rec.oper.dao.OrderStockDao;
import mmb.rec.oper.dao.OrderStockProductCargoDao;
import mmb.rec.oper.dao.OrderStockProductDao;
import mmb.rec.oper.dao.OuterAbnormalInfoDao;
import mmb.rec.oper.dao.ProductPackageDao;
import mmb.rec.oper.dao.ScanOrderStockDao;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.ware.WareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.StringUtil;

@Service
public class OrderStockService {
	
	@Autowired
	public OrderStockDao orderStockMapper;
	@Autowired
	public AuditPackageDao auditPackageMapper;
	@Autowired
	public OrderStockProductDao orderStockProductMapper;
	@Autowired
	public ProductPackageDao productPackageMapper;
	@Autowired
	public CommonDao commonMapper;
	@Autowired
	public OrderStockProductCargoDao orderStockProductCargoMapper;
	@Autowired
	public ScanOrderStockDao scanOrderStockMapper;
	@Autowired
	public OuterAbnormalInfoDao outerAbnormalInfoMapper;
	
	//orderStock
	public int addOrderStock(OrderStockBean orderStockBean) {
		return orderStockMapper.addOrderStock(orderStockBean);
	}

	public int deleteOrderStock(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("order_stock", condition);
		return commonMapper.deleteCommon(paramMap);
	}

	public int updateOrderStock(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("order_stock", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	public int getOrderStockCount(String condition) {
		Map<String,String> paramMap = CommonService.constructCountMap("order_stock", condition);
		return commonMapper.getCommonCount(paramMap);
	}

	public OrderStockBean getOrderStock(String condition) {
		return orderStockMapper.getOrderStock(condition);
	}


	public List<OrderStockBean> getOrderStockList(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return orderStockMapper.getOrderStockList(paramMap);
	}
	public List<OrderStockBean> getOrderStockListSlave(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return orderStockMapper.getOrderStockListSlave(paramMap);
	}
	
	//orderStockProduct
	public int addOrderStockProduct(OrderStockProductBean orderStockProductBean) {
		return orderStockProductMapper.addOrderStockProduct(orderStockProductBean);
	}

	public int deleteOrderStockProduct(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("order_stock_product", condition);
		return commonMapper.deleteCommon(paramMap);
	}

	public int updateOrderStockProduct(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("order_stock_product", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	
	public int getOrderStockProductCount(String condition) {
		Map<String,String> paramMap = CommonService.constructCountMap("order_stock_product", condition);
		return commonMapper.getCommonCount(paramMap);
	}

	public OrderStockProductBean getOrderStockProduct(String condition) {
		return orderStockProductMapper.getOrderStockProduct(condition);
	}

	public List<OrderStockProductBean> getOrderStockProductList(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return orderStockProductMapper.getOrderStockProductList(paramMap);
	}
	public List<OrderStockProductBean> getOrderStockProductListSlave(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return orderStockProductMapper.getOrderStockProductListSlave(paramMap);
	}

	//auditPackage
	public int addAuditPackage(AuditPackageBean auditPackageBean) {
		return auditPackageMapper.addAuditPackage(auditPackageBean);
	}

	public int deleteAuditPackage(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("audit_package", condition);
		return commonMapper.deleteCommon(paramMap);
	}

	public int updateAuditPackage(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("audit_package", set, condition);
		return commonMapper.updateCommon(paramMap);
	}


	public int getAuditPackageCount(String condition) {
		Map<String,String> paramMap = CommonService.constructCountMap("audit_package", condition);
		return commonMapper.getCommonCount(paramMap);
	}

	public AuditPackageBean getAuditPackage(String condition) {
		return auditPackageMapper.getAuditPackage(condition);
	}
	public List<AuditPackageBean> getAuditPackageList(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return auditPackageMapper.getAuditPackageList(paramMap);
	}
	public List<AuditPackageBean> getAuditPackageListSlave(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return auditPackageMapper.getAuditPackageListSlave(paramMap);
	}
	
	//ProductPackage
	public int addProductPackage(ProductPackageBean productPackageBean) {
		return productPackageMapper.addProductPackage(productPackageBean);
	}

	public int deleteProductPackage(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("product_package", condition);
		return commonMapper.deleteCommon(paramMap);
	}
	
	public int updateProductPackage(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("product_package", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	
	public int getProductPackageCount(String condition ) {
		Map<String,String> paramMap = CommonService.constructCountMap("product_package", condition);
		return commonMapper.getCommonCount(paramMap);
	}

	public ProductPackageBean getProductPackage(String condition) {
		return productPackageMapper.getProductPackage(condition);
	}

	public List<ProductPackageBean> getProductPackageList(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return productPackageMapper.getProductPackageList(paramMap);
	}
	public List<ProductPackageBean> getProductPackageListSlave(String condition, int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return productPackageMapper.getProductPackageListSlave(paramMap);
	}
	
	public int addOrderStockProductCargo(
			OrderStockProductCargoBean orderStockProductCargo) {
		return orderStockProductCargoMapper.addOrderStockProductCargo(orderStockProductCargo);
	}
	
	public int deleteOrderStockProductCargo(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("order_stock_product_cargo", condition);
		return commonMapper.deleteCommon(paramMap);
	}

	public int updateOrderStockProductCargo(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("order_stock_product_cargo", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	
	public int getOrderStockProductCargoCount(String condition) {
		Map<String,String> paramMap = CommonService.constructCountMap("order_stock_product_cargo", condition);
		return commonMapper.getCommonCount(paramMap);
	}

	public OrderStockProductCargoBean getOrderStockProductCargo(String condition) {
		return orderStockProductCargoMapper.getOrderStockProductCargo(condition);
	}

	public List<OrderStockProductCargoBean> getOrderStockProductCargoList(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return orderStockProductCargoMapper.getOrderStockProductCargoList(paramMap);
	}
	public List<OrderStockProductCargoBean> getOrderStockProductCargoListSlave(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return orderStockProductCargoMapper.getOrderStockProductCargoListSlave(paramMap);
	}
	/**
	 * 在OrderStockController里getOrderStockQueryDatagrid调用的查询list的联查
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AuditPackageBean> getOrderStockQueryList(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return scanOrderStockMapper.getOrderStockQueryList(paramMap);
	}
	/**
	 * 
	 * @param condition
	 * @return
	 */
	public int getOrderStockQueryCount(String condition) {
		return scanOrderStockMapper.getOrderStockQueryCount(condition);
	}
	
	/**
	 * 计算两个yyyy-MM-dd格式的时间差 是否在30天以上
	 * @param startTime
	 * @param endTime
	 * @return  true代表大于 30天了
	 */
	public static boolean isMoreThanThirtyDay(String startTime, String endTime) {
		boolean result = false;
		
		long thirtyOneDayOfMiliis = (long)30*24*60*60*1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = sdf.parse(startTime);
			Date date2 = sdf.parse(endTime);
			long start = date1.getTime();
			long end = date2.getTime();
			long period = end - start;
			if( period > thirtyOneDayOfMiliis ) {
				result = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	@Transactional(readOnly = false,propagation = Propagation.REQUIRED, rollbackFor=RuntimeException.class)
	public Json saveAreaDeliveryPriority(String provinces, String areas) {
		Json j = new Json();
		HashMap<String, String> paramMap = new HashMap<String, String>();
		if (provinces.equals("-1")) {
			HashMap<String, String> areaDeliverPriorityMap = OrderStockBean.areaDeliverPriorityMap;
			for (String key : areaDeliverPriorityMap.keySet()) {
				paramMap.put("table", " area_delivery_priority ");
				paramMap.put("set", " priority = '" + areas + "' ");
				paramMap.put("condition", " id= " + key);
				if (commonMapper.updateCommon(paramMap) <= 0) {
					throw new RuntimeException("更新失败");
				}
			}
		} else {
			paramMap.put("table", " area_delivery_priority ");
			paramMap.put("set", " priority = '" + areas + "' ");
			paramMap.put("condition", " id in (" + provinces + ") ");
			if (commonMapper.updateCommon(paramMap) <= 0) {
				throw new RuntimeException("更新失败");
			}
		}
		j.setSuccess(true);
		return j;
	}
	public int getOuterAbnormalCount(String condition) {
		return outerAbnormalInfoMapper.getOuterAbnormalCount(condition);
	}
	public List<HashMap<String, String>> getOuterAbnormalList(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return outerAbnormalInfoMapper.getOuterAbnormalList(paramMap);
	}

	public EasyuiDataGridJson getOuterAbnormalList(HttpServletRequest request, EasyuiDataGrid easyuiDataGrid) {
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			easyuiDataGridJson.setTip("当前没有登录,操作失败！");
			return easyuiDataGridJson;
		}
		UserGroupBean group = user.getGroup();
		String outerOrderCode = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("outerOrderCode")));
		int sourceId = StringUtil.StringToId(request.getParameter("sourceId"));
		String startTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endTime")));
		String status = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("status")));
		StringBuffer condition = new StringBuffer(" id > 0 ");
		if (!startTime.equals("") && !endTime.equals("")) {
			condition.append(" and keep_time >= '").append(startTime).append(" 00:00:00' and keep_time <= '").append(endTime).append(" 23:59:59' ");
		}
		if (sourceId != 0) {
			condition.append(" and source_id=").append(sourceId);
		}
		if (!status.equals("") ) {
			condition.append(" and status in ( ").append(status).append(") ");
		}
		if (!outerOrderCode.equals("")) {
			condition.append(" and outer_order_code = '").append(outerOrderCode).append("' ");
		}
		HashMap<String, String> paramMap = new HashMap<String, String>();
		HashMap<String, String> totalMap = new HashMap<String, String>();
		paramMap.put("column", "id,outer_order_code,source_id,'京东' sourceName,date_format( keep_time, '%Y-%m-%d %T' ) keep_time,date_format( handle_time, '%Y-%m-%d %T' ) handle_time,handle_user_id,handle_user_name,status,remark,reason ");
		paramMap.put("table", " outer_abnormal_info");
		paramMap.put("condition", condition.toString() + " limit "
				+ (easyuiDataGrid.getPage() - 1) * easyuiDataGrid.getRows()
				+ "," + easyuiDataGrid.getRows());
		totalMap.put("column", "count(id)");
		totalMap.put("table", " outer_abnormal_info");
		totalMap.put("condition", condition.toString());
		List<HashMap<String, String>> listRows = outerAbnormalInfoMapper.getOuterAbnormalList(paramMap);
		int count = commonMapper.getCommonCount(totalMap);
		easyuiDataGridJson.setTotal((long) count);
		easyuiDataGridJson.setRows(listRows);
		List l = new ArrayList();
		l.add(group.isFlag(2201));
		easyuiDataGridJson.setFooter(l);
		return easyuiDataGridJson;
	}
	public void orderSendOutCount(String yesterday, String mdcAreas, HashMap<String, Integer> mdcDeliverySkuMap, HashMap<Integer, String> unMdcAreaMap, DbOperation slave2,WareService wareService, String sb) throws SQLException {
		String beforeYesterday = DateUtil.getBackFromDate(yesterday, 89);
		StringBuffer sql = new StringBuffer();
		sql.append("select os.stock_area area, osp.product_id productId ")
			.append("from order_stock os,audit_package ap,order_stock_product osp ")
			.append(" where os.id=osp.order_stock_id and os.order_id=ap.order_id  "
					+ " and ap.check_datetime >= '"
					+ beforeYesterday
					+ " 00:00:00' and ap.check_datetime <= '"
					+ yesterday
					+ " 23:59:59' and os.status<>"
					+ OrderStockBean.STATUS4 
					+ " and os.stock_area in (" + mdcAreas + ") "
					+ " group by os.stock_area,osp.product_id");
		ResultSet rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			mdcDeliverySkuMap.put(rs.getInt("area") + "," + rs.getInt("productId"), null);
		}

		sql.setLength(0);
		sql.append("select a.area area, osp.product_id productId ")
			.append("from order_stock os,order_stock_product osp,audit_package ap,user_order_sub_list uosl, ")
			.append("					(SELECT                                             ")
			.append("	uo.id parentId,os.stock_area area                                  ")
			.append("FROM                                                                   ")
			.append(" audit_package ap    ")
			.append("JOIN order_stock os ON ap.order_id = os.order_id                  ")
			.append("JOIN user_order_sub_list uosl ON os.order_id = uosl.child_id          ")
			.append("JOIN user_order uo ON uosl.parent_id = uo.id                           ")
			.append("JOIN user_order_extend_info uoei ON uo. CODE = uoei.order_code         ")
			.append("join provinces p on uoei.add_id1 = p.id                                ")
			.append("join area_delivery_priority adp on p.name like concat(adp.province,'%') ")
			.append("WHERE                                                                  "
					+ "  ap.check_datetime >= '"
					+ beforeYesterday
					+ " 00:00:00' and ap.check_datetime <= '"
					+ yesterday
					+ " 23:59:59' and os.status<>"
					+ OrderStockBean.STATUS4 
					+ " and os.stock_area in (" + mdcAreas + ") " )
			.append(" and substr(adp.priority,1,1)=os.stock_area ")
			.append(" group by uo.id) a ")
			.append(" where os.id=osp.order_stock_id and os.order_id=ap.order_id and os.order_id=uosl.child_id and a.parentId=uosl.parent_id and os.stock_area<>a.area "
					+ " and ap.check_datetime >= '"
					+ beforeYesterday
					+ " 00:00:00' and ap.check_datetime <= '"
					+ yesterday
					+ " 23:59:59' and os.status<>"
					+ OrderStockBean.STATUS4 
					+ " and os.order_id=uosl.child_id group by a.area,osp.product_id");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			if (!mdcDeliverySkuMap.containsKey(rs.getInt("area") + "," + rs.getInt("productId"))) {
				mdcDeliverySkuMap.put(rs.getInt("area") + "," + rs.getInt("productId"), null);
			}
		}
		
		HashMap<String, HashMap<String, Integer>> map = new HashMap<String, HashMap<String,Integer>>();
		HashMap<String, Integer> tempMap = null;
		
		sql.setLength(0);
		sql.append("SELECT                                                                                                    ")
			.append("	substr(adp.priority,1,1) area,substr(adp.priority,3,1) area2,osp.product_id productId,sum(osp.stockout_count) thecount                                   ")
			.append("FROM                                                                                                                         ")
			.append(" audit_package ap                                                        ")
			.append("JOIN order_stock os ON ap.order_id = os.order_id                                                                       ")
			.append("join order_stock_product osp on os.id=osp.order_stock_id                                                                     ")
			.append("JOIN user_order_extend_info uoei ON os.order_code = uoei.order_code                                                          ")
			.append("join provinces p on uoei.add_id1 = p.id                                                                                      ")
			.append("join area_delivery_priority adp on p.name like concat(adp.province,'%')                                                      ")
			.append("WHERE   ap.check_datetime >= '"
					+ yesterday
					+ " 00:00:00' and ap.check_datetime <= '"
					+ yesterday
					+ " 23:59:59' and os.status<>"
					+ OrderStockBean.STATUS4 
					+ " and substr(adp.priority,1,1) in (" + mdcAreas + ") " )
			.append(" group by substr(adp.priority,1,1),substr(adp.priority,3,1),osp.product_id");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			if (mdcDeliverySkuMap.containsKey(rs.getInt("area") + "," + rs.getInt("productId"))) {
				tempMap = map.get(rs.getInt("area") + "," + rs.getInt("productId"));
				if (tempMap == null) {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("deliverCount", rs.getInt("thecount"));
					map.put(rs.getInt("area") + "," + rs.getInt("productId"), tempMap);
				} else {
					if (tempMap.get("deliverCount") != null) {
						tempMap.put("deliverCount", rs.getInt("thecount") + tempMap.get("deliverCount"));
					} else {
						tempMap.put("deliverCount", rs.getInt("thecount"));
					}
				}
			} else {
				tempMap = map.get(rs.getInt("area2") + "," + rs.getInt("productId"));
				if (tempMap == null) {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("deliverCount", rs.getInt("thecount"));
					map.put(rs.getInt("area2") + "," + rs.getInt("productId"), tempMap);
				} else {
					if (tempMap.get("deliverCount") != null) {
						tempMap.put("deliverCount", rs.getInt("thecount") + tempMap.get("deliverCount"));
					} else {
						tempMap.put("deliverCount", rs.getInt("thecount"));
					}
				}
			}
		}
		
		sql.setLength(0);
		sql.append("SELECT                                                                                                    ")
			.append("	substr(adp.priority,1,1) area,osp.product_id productId,sum(osp.stockout_count) thecount                                   ")
			.append("FROM      audit_package ap                                                                                                                   ")
			.append("JOIN order_stock os ON ap.order_id = os.order_id                                                                        ")
			.append("join order_stock_product osp on os.id=osp.order_stock_id                                                                     ")
			.append("JOIN user_order_extend_info uoei ON os.order_code = uoei.order_code                                                          ")
			.append("join provinces p on uoei.add_id1 = p.id                                                                                      ")
			.append("join area_delivery_priority adp on p.name like concat(adp.province,'%')                                                      ")
			.append("WHERE   ap.check_datetime >= '"
					+ yesterday
					+ " 00:00:00' and ap.check_datetime <= '"
					+ yesterday
					+ " 23:59:59' and os.status<>"
					+ OrderStockBean.STATUS4 
					+ " and substr(adp.priority,1,1) not in (" + mdcAreas + ") " )
			.append(" group by substr(adp.priority,1,1),osp.product_id");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			tempMap = map.get(rs.getInt("area") + "," + rs.getInt("productId"));
			if (tempMap == null) {
				tempMap = new HashMap<String, Integer>();
				tempMap.put("deliverCount", rs.getInt("thecount"));
				map.put(rs.getInt("area") + "," + rs.getInt("productId"), tempMap);
			} else {
				if (tempMap.get("deliverCount") != null) {
					tempMap.put("deliverCount", rs.getInt("thecount") + tempMap.get("deliverCount"));
				} else {
					tempMap.put("deliverCount", rs.getInt("thecount"));
				}
			}
		}

		sql.setLength(0);
		sql.append("SELECT                                                                                                    ")
			.append("	os.stock_area area,osp.product_id productId,sum(osp.stockout_count) thecount                                   ")
			.append("FROM                    audit_package ap                                                                                                     ")
			.append("JOIN order_stock os ON ap.order_id = os.order_id                                                                        ")
			.append("join order_stock_product osp on os.id=osp.order_stock_id                                                                     ")
			.append("WHERE   ap.check_datetime >= '"
					+ yesterday
					+ " 00:00:00' and ap.check_datetime <= '"
					+ yesterday
					+ " 23:59:59' and os.status<>"
					+ OrderStockBean.STATUS4 )
			.append(" group by os.stock_area,osp.product_id");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			tempMap = map.get(rs.getInt("area") + "," + rs.getInt("productId"));
			if (tempMap == null) {
				tempMap = new HashMap<String, Integer>();
				tempMap.put("realDeliverCount", rs.getInt("thecount"));
				map.put(rs.getInt("area") + "," + rs.getInt("productId"), tempMap);
			} else {
				if (tempMap.get("realDeliverCount") != null) {
					tempMap.put("realDeliverCount", rs.getInt("thecount") + tempMap.get("realDeliverCount"));
				} else {
					tempMap.put("realDeliverCount", rs.getInt("thecount"));
				}
			}
		}
		
		sql.setLength(0);
		sql.append("SELECT ")
			.append("	ps.area area,           ")
			.append("	ps.product_id productId,")
			.append("	sum(ps.stock) thecount  ")
			.append("FROM                       ")
			.append("	product_stock ps        ")
			.append("WHERE                      ")
			.append("	ps.type = 0             ")
			.append("GROUP BY                   ")
			.append("	ps.area,                ")
			.append("	ps.product_id");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			tempMap = map.get(rs.getInt("area") + "," + rs.getInt("productId"));
			if (tempMap == null) {
				if (!sb.contains(yesterday)) {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("quafiliedStockCount", rs.getInt("thecount"));
					map.put(rs.getInt("area") + "," + rs.getInt("productId"), tempMap);
				} else {
					continue;
				}
			} else {
				if (tempMap.get("quafiliedStockCount") != null) {
					tempMap.put("quafiliedStockCount", rs.getInt("thecount") + tempMap.get("quafiliedStockCount"));
				} else {
					tempMap.put("quafiliedStockCount", rs.getInt("thecount"));
				}
			}
		}
		
		sql.setLength(0);
		sql.append("SELECT ")
			.append("	ps.area area,           ")
			.append("	ps.product_id productId,")
			.append("	sum(ps.stock) thecount  ")
			.append("FROM                       ")
			.append("	product_stock ps        ")
			.append("WHERE                      ")
			.append("	ps.type = 1             ")
			.append("GROUP BY                   ")
			.append("	ps.area,                ")
			.append("	ps.product_id");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			tempMap = map.get(rs.getInt("area") + "," + rs.getInt("productId"));
			if (tempMap == null) {
				if (!sb.contains(yesterday)) {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("checkStockCount", rs.getInt("thecount"));
					map.put(rs.getInt("area") + "," + rs.getInt("productId"), tempMap);
				} else {
					continue;
				}
			} else {
				if (tempMap.get("checkStockCount") != null) {
					tempMap.put("checkStockCount", rs.getInt("thecount") + tempMap.get("checkStockCount"));
				} else {
					tempMap.put("checkStockCount", rs.getInt("thecount"));
				}
			}
		}
		
		sql.setLength(0);
		sql.append("	SELECT                                                       ")
			.append("	se.stock_in_area area,                                       ")
			.append("	sep.product_id productId,                                    ")
			.append("	sum(sep.stock_in_count) thecount                             ")
			.append("FROM                                                            ")
			.append("	stock_exchange se                                            ")
			.append("JOIN stock_exchange_product sep ON se.id = sep.stock_exchange_id ")
			.append("WHERE                                                           ")
			.append("	se.stock_in_type = 0                                         ")
			.append("AND (                                                           ")
			.append("	se.stock_in_area != se.stock_out_area                        ")
			.append("	OR se.stock_in_type != se.stock_out_type                     ")
			.append(")                                                               ")
			.append("AND se. STATUS = 7                                              ")
			.append("GROUP BY                                                        ")
			.append("	se.stock_in_area,                                            ")
			.append("	sep.product_id                                               ");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			tempMap = map.get(rs.getInt("area") + "," + rs.getInt("productId"));
			if (tempMap == null) {
				if (!sb.contains(yesterday)) {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("exchangeStockCount", rs.getInt("thecount"));
					map.put(rs.getInt("area") + "," + rs.getInt("productId"), tempMap);
				} else {
					continue;
				}
			} else {
				if (tempMap.get("exchangeStockCount") != null) {
					tempMap.put("exchangeStockCount", rs.getInt("thecount") + tempMap.get("exchangeStockCount"));
				} else {
					tempMap.put("exchangeStockCount", rs.getInt("thecount"));
				}
			}
		}
		
		sql.setLength(0);
		sql.append("	SELECT                                                       ")
			.append("	se.stock_out_area area,                                       ")
			.append("	sep.product_id productId,                                    ")
			.append("	sum(sep.stock_in_count) thecount                             ")
			.append("FROM                                                            ")
			.append("	stock_exchange se                                            ")
			.append("JOIN stock_exchange_product sep ON se.id = sep.stock_exchange_id ")
			.append("WHERE                                                           ")
			.append("	se.stock_out_type = 0                                         ")
			.append("AND (                                                           ")
			.append("	se.stock_in_area != se.stock_out_area                        ")
			.append("	OR se.stock_in_type != se.stock_out_type                     ")
			.append(")                                                               ")
			.append("AND se. STATUS = 7                                              ")
			.append("GROUP BY                                                        ")
			.append("	se.stock_out_area,                                            ")
			.append("	sep.product_id                                               ");
		rs = slave2.executeQuery(sql.toString());
		while (rs.next()) {
			tempMap = map.get(rs.getInt("area") + "," + rs.getInt("productId"));
			if (tempMap == null) {
				if (!sb.contains(yesterday)) {
					tempMap = new HashMap<String, Integer>();
					tempMap.put("exchangeStockCount", -rs.getInt("thecount"));
					map.put(rs.getInt("area") + "," + rs.getInt("productId"), tempMap);
				} else {
					continue;
				}
			} else {
				if (tempMap.get("exchangeStockCount") != null) {
					tempMap.put("exchangeStockCount",tempMap.get("exchangeStockCount") - rs.getInt("thecount") );
				} else {
					tempMap.put("exchangeStockCount", -rs.getInt("thecount"));
				}
			}
		}
		if (!sb.contains(yesterday)) {
			//mdc90天发过货的sku全部记下来
			for (String mdcKey : mdcDeliverySkuMap.keySet()) {
				tempMap = new HashMap<String, Integer>();
				map.put(mdcKey, tempMap);
			}
			
			sql.setLength(0);
			sql.append(" select id from product ");
			rs = slave2.executeQuery(sql.toString());
			//非mdc仓全部sku全部记下来
			while (rs.next()) {
				for (int key : unMdcAreaMap.keySet()) {
					tempMap = map.get(key + "," + rs.getInt("id"));
					if (tempMap == null) {
						tempMap = new HashMap<String, Integer>();
						map.put(key + "," + rs.getInt("id"), tempMap);
					}
				}
			}
		}
		rs.close();
		
		for (String key : map.keySet()) {
			String area = key.split(",")[0];
			String productId = key.split(",")[1];
			HashMap<String, Integer> temp = map.get(key);
			int deliverCount = temp.get("deliverCount") == null ? 0 : temp.get("deliverCount");
			int realDeliverCount = temp.get("realDeliverCount") == null ? 0 : temp.get("realDeliverCount");
			int quafiliedStockCount = temp.get("quafiliedStockCount") == null ? 0 : temp.get("quafiliedStockCount");
			int checkStockCount = temp.get("checkStockCount") == null ? 0 : temp.get("checkStockCount");
			int exchangeStockCount = temp.get("exchangeStockCount") == null ? 0 : temp.get("exchangeStockCount");
			
			sql.setLength(0);
			sql.append("insert into order_send_out_count(`create_date`, `stock_area`, `product_id`, `deliver_count`, `real_deliver_count`, `quafilied_stock_count`, `check_stock_count`, `exchange_stock_count`, type) "
					+ "values ('"
					+ yesterday
					+ "',"
					+ area
					+ ","
					+ productId
					+ ","
					+ deliverCount
					+ ","
					+ realDeliverCount
					+ ","
					+ quafiliedStockCount
					+ ","
					+ checkStockCount
					+ ","
					+ exchangeStockCount
					+ ","
					+ ((deliverCount == 0 && realDeliverCount == 0) ? 1 : 0)
					+ ")");
			if (!wareService.getDbOp().executeUpdate(sql.toString())) {
				System.out.println(DateUtil.getNow() + "应发货量定时任务添加失败");
				System.out.println(DateUtil.getNow() + "应发货量定时任务结束");
				return;
			}
		}
	}
	
}
