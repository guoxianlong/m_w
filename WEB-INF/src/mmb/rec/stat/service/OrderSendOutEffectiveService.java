package mmb.rec.stat.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cache.ProductLinePermissionCache;

import mmb.rec.stat.bean.TempOrderEffectiveInfoBean;
import mmb.rec.stat.bean.WareSendOutDurationBean;
import mmb.rec.stat.bean.WareSendOutEffectiveBean;
import mmb.stock.stat.ClaimsVerificationBean;
import mmb.stock.stat.ProductLineCatalogBean;
import mmb.stock.stat.ReturnPackageCheckBean;
import mmb.stock.stat.StockinUnqualifiedService;
import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class OrderSendOutEffectiveService extends BaseServiceImpl {
	
	public OrderSendOutEffectiveService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

//	public OrderSendOutEffectiveService() {
//		this.useConnType = CONN_IN_SERVICE;
//	}
	
	//发货时效数据库操作
	public boolean addWareSendOutEffective(WareSendOutEffectiveBean bean) {
		return addXXX(bean, "ware_send_out_effective");
	}

	public List getWareSendOutEffectiveList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "ware_send_out_effective", "mmb.rec.stat.bean.WareSendOutEffectiveBean");
	}
	
	public int getWareSendOutEffectiveCount(String condition) {
		return getXXXCount(condition, "ware_send_out_effective", "id");
	}

	public WareSendOutEffectiveBean getWareSendOutEffective(String condition) {
		return (WareSendOutEffectiveBean) getXXX(condition, "ware_send_out_effective",
		"mmb.rec.stat.bean.WareSendOutEffectiveBean");
	}

	public boolean updateWareSendOutEffective(String set, String condition) {
		return updateXXX(set, condition, "ware_send_out_effective");
	}

	public boolean deleteWareSendOutEffective(String condition) {
		return deleteXXX(condition, "ware_send_out_effective");
	}
	
	
	//发货平均时间数据库操作
	public boolean addWareSendOutDuration(WareSendOutDurationBean bean) {
		return addXXX(bean, "ware_send_out_duration");
	}

	public List getWareSendOutDurationList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "ware_send_out_duration", "mmb.rec.stat.bean.WareSendOutDurationBean");
	}
	
	public int getWareSendOutDurationCount(String condition) {
		return getXXXCount(condition, "ware_send_out_duration", "id");
	}

	public WareSendOutDurationBean getWareSendOutDuration(String condition) {
		return (WareSendOutDurationBean) getXXX(condition, "ware_send_out_duration",
		"mmb.rec.stat.bean.WareSendOutDurationBean");
	}

	public boolean updateWareSendOutDuration(String set, String condition) {
		return updateXXX(set, condition, "ware_send_out_duration");
	}

	public boolean deleteWareSendOutDuration(String condition) {
		return deleteXXX(condition, "ware_send_out_duration");
	}

	public List<TempOrderEffectiveInfoBean> getOrderEffectiveInfo(
			String condition) {
		List<TempOrderEffectiveInfoBean> resultList = new ArrayList<TempOrderEffectiveInfoBean>();
		String sql = "select (p.price*osp.stockout_count) as 'per_product_total_price',  p.parent_id1, p.parent_id2, os.stock_area as 'area', os.id as 'order_stock_id', os.create_datetime as 'apply_datetime', mbp.create_datetime as 'mailing_datetime', sbg.receive_datetime as 'sorting_batch_datetime', sbg.receive_datetime2 as 'second_sort_datetime', ap.check_datetime as 'audit_datetime' from "
				+ "mailing_batch_package mbp,"
				+ " sorting_batch_group sbg,"
				+ " sorting_batch_order sbo,"
				+ " order_stock os,"
				+ " audit_package ap,"
				+ " order_stock_product osp,"
				+ " product p"
				+ " where sbo.sorting_group_id = sbg.id"
				+ " and sbo.order_id = mbp.order_id"
				+ " and mbp.order_id = os.order_id"
				+ " and os.id = osp.order_stock_id" 
				+" and osp.product_id = p.id"
				+ " and ap.order_id = mbp.order_id and os.status!= 3 and sbo.delete_status = 0 and ";
		if( condition == null || condition.equals("") ) {
			return resultList;
		}
		sql += condition;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return resultList;
		}
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				TempOrderEffectiveInfoBean toeiBean = new TempOrderEffectiveInfoBean();
				toeiBean.setArea(rs.getInt("area"));
				toeiBean.setOrderStockId(rs.getInt("order_stock_id"));
				toeiBean.setApplyDatetime(rs.getString("apply_datetime"));
				toeiBean.setSortingBatchDatetime(rs.getString("sorting_batch_datetime"));
				toeiBean.setSecondSortDatetime(rs.getString("second_sort_datetime"));
				toeiBean.setAuditDatetime(rs.getString("audit_datetime"));
				toeiBean.setMailingDatetime(rs.getString("mailing_datetime"));
				toeiBean.setParentId1(rs.getInt("parent_id1"));
				toeiBean.setParentId2(rs.getInt("parent_id2"));
				toeiBean.setPerProductTotalPrice(rs.getFloat("per_product_total_price"));
				resultList.add(toeiBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return resultList;
	}
	
	/**
	 * 计算，并添加 ware_send_out_effective, ware_send_out_duration
	 * @param orderEffectiveInfoList
	 */
	public void calculateOrderSendOutEffectiveInfo(
			List<TempOrderEffectiveInfoBean> orderEffectiveInfoList, String yesterdayString) {
		WareService wareService = new WareService(this.dbOp);
		StockinUnqualifiedService stockinUnqualifiedService = new StockinUnqualifiedService(IBaseService.CONN_IN_SERVICE,this.dbOp);
		Map<String, List<TempOrderEffectiveInfoBean>> map = new HashMap<String, List<TempOrderEffectiveInfoBean>>();
		List<ProductLineCatalogBean> productLineList = stockinUnqualifiedService.getProductLineCatalogList("id<>0", -1, -1, null);
		Map<String,Map<String,String>> productLineSplitMap = this.getProductLineSplitMap(productLineList);
		if( orderEffectiveInfoList != null ) {
			int x = orderEffectiveInfoList.size();
			for( int i = 0; i < x; i ++ ) {
				TempOrderEffectiveInfoBean toeiBean = orderEffectiveInfoList.get(i);
				//算得订单的各个时段的实际耗时
				calculateEveryDuration(toeiBean);
				//根据库区和产品线综合分类订单区间
				int productLineId = getPrdouctLineId(toeiBean.getParentId1(), toeiBean.getParentId2(), productLineSplitMap);
				if( productLineId == 0 ) {
					continue;
				}
				String areaPlusProductLineId = toeiBean.getArea() + "_" + productLineId;
				if( map.containsKey( areaPlusProductLineId)) {
					List<TempOrderEffectiveInfoBean> currentList = map.get(areaPlusProductLineId);
					currentList.add(toeiBean);
				} else {
					List<TempOrderEffectiveInfoBean> subList = new ArrayList<TempOrderEffectiveInfoBean>();
					subList.add(toeiBean);
					map.put(areaPlusProductLineId, subList);
				}
			}
		}
		//将分类完毕的各个信息进行处理
		statisticEveryWareProductLine(map, yesterdayString);
	}
	
	private int getPrdouctLineId(int parentId1, int parentId2,
			Map<String, Map<String, String>> productLineSplitMap) {
		int result = 0;
		Map<String,String> parentId1Map = productLineSplitMap.get("1");
		if( parentId1Map.containsKey(new Integer(parentId1).toString()) ) {
			result = Integer.valueOf(parentId1Map.get(new Integer(parentId1).toString()));
		} else {
			Map<String,String> parentId2Map = productLineSplitMap.get("2");
			if( parentId2Map.containsKey(new Integer(parentId2).toString()) ) {
				result = Integer.valueOf(parentId2Map.get(new Integer(parentId2).toString()));
			}
		}
		return result;
	}

	/**
	 * 根据全部产品线的区分来得出一个以type 和 parentId区分的Map来
	 * @param productLineList
	 * @return
	 */
	private Map<String, Map<String, String>> getProductLineSplitMap(
			List<ProductLineCatalogBean> productLineList) {
		Map<String,Map<String,String>> result = new HashMap<String,Map<String,String>>();
		int x = productLineList.size();
		for( int i = 0 ; i < x; i++ ) {
			ProductLineCatalogBean plcBean = productLineList.get(i);
			if( result.containsKey(new Integer(plcBean.getCatalogType()).toString()) ) {
				Map<String,String> parentIdMap = result.get(new Integer(plcBean.getCatalogType()).toString());
				if( parentIdMap.containsKey(new Integer(plcBean.getCatalogId()).toString()) ) {
					
				} else {
					parentIdMap.put(new Integer(plcBean.getCatalogId()).toString(), new Integer(plcBean.getProductLineId()).toString());
				}
			} else {
				Map<String,String> parentIdMap = new HashMap<String,String>();
				parentIdMap.put(new Integer(plcBean.getCatalogId()).toString(), new Integer(plcBean.getProductLineId()).toString());
				result.put(new Integer(plcBean.getCatalogType()).toString(), parentIdMap);
 			}
		}
		return result;
	}

	/**
	 * 找出所有地区和产品线分类 去map中取待统计列表，如果没有则填入默认值。
	 * @param map
	 */
	private void statisticEveryWareProductLine(
			Map<String, List<TempOrderEffectiveInfoBean>> map, String yesterdayString) {
		List lineList=ProductLinePermissionCache.getAllProductLineList(); 
		Iterator itr = ProductStockBean.areaMap.keySet().iterator();
		for ( ; itr.hasNext(); ) {
			Integer areaId = (Integer)itr.next();
			int x = lineList.size();
			for( int i = 0 ; i < x; i ++ ) {
				voProductLine line = (voProductLine)lineList.get(i);
				String areaPlusProductLineId = areaId +"_" + line.getId();
				if( map.containsKey(areaPlusProductLineId)) {
					//存在，计算结果
					List<TempOrderEffectiveInfoBean> list = map.get(areaPlusProductLineId);
					statisticPerWareProductLine(list, areaId, line.getId(), yesterdayString);
				} else {
					//不存在， 给予默认值
					statisticPerWareProductLineDefault(areaId, line.getId(), yesterdayString);
				}
			}
		}
		
	}

	/**
	 * 直接添加统计数据默认值
	 * @param areaId
	 * @param id
	 * @param yesterdayString
	 */
	private void statisticPerWareProductLineDefault(Integer areaId, int productLineId,
			String yesterdayString) {
		WareSendOutEffectiveBean wsoeBean = new WareSendOutEffectiveBean();
		WareSendOutDurationBean wsodBean = new WareSendOutDurationBean();
		wsoeBean.setArea(areaId);
		wsoeBean.setDate(yesterdayString);
		wsoeBean.setProductLineId(productLineId);
		wsoeBean.setCount1(0);
		wsoeBean.setCount2(0);
		wsoeBean.setCount3(0);
		if(!addWareSendOutEffective(wsoeBean)) {
			System.out.println(yesterdayString + "- 当天" + areaId + "地区" + productLineId + "产品线订单发货时效默认值记录添加失败！");
		}
		
		wsodBean.setArea(areaId);
		wsodBean.setDate(yesterdayString);
		wsodBean.setProductLineId(productLineId);
		wsodBean.setDuration1(0f);
		wsodBean.setDuration2(0f);
		wsodBean.setDuration3(0f);
		wsodBean.setDuration4(0f);
		if( !addWareSendOutDuration(wsodBean) ) {
			System.out.println(yesterdayString + "- 当天" + areaId + "地区" + productLineId + "产品线订单发货时效默认值记录添加失败！");
		}
	}

	/**
	 * 计算添加统计数据
	 * @param list
	 * @param areaId
	 * @param productLineId
	 * @param yesterdayString
	 */
	private void statisticPerWareProductLine(
			List<TempOrderEffectiveInfoBean> list, int areaId, int productLineId,String yesterdayString) {
		int x = list.size();
		//小于十二小时total
		int lessTotal = 0;
		//在十二到二十四小时 total
		int middleTotal = 0;
		//大于二十四小时total
		int moreTotal = 0;
		float duration1Total = 0f;
		float duration2Total = 0f;
		float duration3Total = 0f;
		float duration4Total = 0f;
		for( int i = 0 ; i < x; i++ ) {
			TempOrderEffectiveInfoBean toeiBean = (TempOrderEffectiveInfoBean)list.get(i);
			if( toeiBean.getDurationTotal() <= 12f ) {
				lessTotal ++;
			} else if( 12f < toeiBean.getDurationTotal() && toeiBean.getDurationTotal() <= 24f ) {
				middleTotal ++;
			} else {
				moreTotal ++;
			}
			duration1Total += toeiBean.getDuration1(); 
			duration2Total += toeiBean.getDuration2(); 
			duration3Total += toeiBean.getDuration3(); 
			duration4Total += toeiBean.getDuration4(); 
		}
		WareSendOutEffectiveBean wsoeBean = new WareSendOutEffectiveBean();
		WareSendOutDurationBean wsodBean = new WareSendOutDurationBean();
		wsoeBean.setArea(areaId);
		wsoeBean.setDate(yesterdayString);
		wsoeBean.setProductLineId(productLineId);
		wsoeBean.setCount1(lessTotal);
		wsoeBean.setCount2(middleTotal);
		wsoeBean.setCount3(moreTotal);
		if(!addWareSendOutEffective(wsoeBean)) {
			System.out.println(yesterdayString + "- 当天" + areaId + "地区" + productLineId + "产品线订单发货时效记录添加失败！");
		}
		float duration1 = getKeepTwoDecimal(duration1Total/(float)x);
		float duration2 = getKeepTwoDecimal(duration2Total/(float)x);
		float duration3 = getKeepTwoDecimal(duration3Total/(float)x);
		float duration4 = getKeepTwoDecimal(duration4Total/(float)x);
		
		wsodBean.setArea(areaId);
		wsodBean.setDate(yesterdayString);
		wsodBean.setProductLineId(productLineId);
		wsodBean.setDuration1(duration1);
		wsodBean.setDuration2(duration2);
		wsodBean.setDuration3(duration3);
		wsodBean.setDuration4(duration4);
		if( !addWareSendOutDuration(wsodBean) ) {
			System.out.println(yesterdayString + "- 当天" + areaId + "地区" + productLineId + "产品线订单发货时效记录添加失败！");
		}
	}

	/**
	 * 计算每个时间间隔,  对于分段时间间隔为0的 将按照整体时间赋值
	 * @param toeiBean
	 */
	private void calculateEveryDuration(TempOrderEffectiveInfoBean toeiBean) {
		
		float durationTotal = calculateDuration(toeiBean.getApplyDatetime(), toeiBean.getMailingDatetime());
		float duration1 = calculateDuration(toeiBean.getApplyDatetime(), toeiBean.getSortingBatchDatetime());
		float duration2 = calculateDuration(toeiBean.getSortingBatchDatetime(), toeiBean.getSecondSortDatetime());
		float duration3 = calculateDuration(toeiBean.getSecondSortDatetime(), toeiBean.getAuditDatetime());
		float duration4 = calculateDuration(toeiBean.getAuditDatetime(), toeiBean.getMailingDatetime());
		
		if( duration1 == 0f ) {
			toeiBean.setDuration1(0f);
		} else if (duration1 < 0f) {
			toeiBean.setDuration1(0f);
		} else {
			toeiBean.setDuration1(duration1);
		}
		if( duration2 == 0f  ) {
			toeiBean.setDuration2(0f);
		} else if (duration2 < 0f) {
			toeiBean.setDuration2(0f);
		} else {
			toeiBean.setDuration2(duration2);
		}
		if( duration3 == 0f ) {
			toeiBean.setDuration3(0f);
		}  else if (duration3 < 0f) {
			toeiBean.setDuration3(0f);
		} else {
			toeiBean.setDuration3(duration3);
		}
		if( duration4 == 0f ) {
			toeiBean.setDuration4(0f);
		}  else if (duration4 < 0f) {
			toeiBean.setDuration4(0f);
		} else {
			toeiBean.setDuration4(duration4);
		}
		toeiBean.setDurationTotal(durationTotal);
	}
	/**
	 * 计算单个时间间隔
	 * @param applyDatetime
	 * @param sortingBatchDatetime
	 * @return
	 */
	private float calculateDuration(String lessTime, String moreTime) {
		float result = 0f;
		if( lessTime == null || lessTime.equals("") || moreTime == null || moreTime.equals("") ) {
			
		} else {
			if( lessTime.length() > 19 ) {
				lessTime = StringUtil.convertNull(StringUtil.cutString(lessTime, 19));
			}
			if( moreTime.length() > 19 ) {
				moreTime = StringUtil.convertNull(StringUtil.cutString(moreTime, 19));
			}
			Date lessDate = DateUtil.parseDate(lessTime, "yyyy-MM-dd HH:mm:ss");
			Date moreDate = DateUtil.parseDate(moreTime, "yyyy-MM-dd HH:mm:ss");
			long durationMili = moreDate.getTime() - lessDate.getTime();
			result = ((float)durationMili/(float)(1000*60*60));
		}
		return result;
	}
	
	/**
	 * 计算float 保留两位小数
	 * @param f
	 * @return
	 */
	public float getKeepTwoDecimal( float f ) {
		DecimalFormat dcmFmt = new DecimalFormat("0.00");
		return Float.valueOf(dcmFmt.format(f));
	}
	
	/**
	 * 根据产品的parentId 得到产品的产品线id
	 * @param parentId1
	 * @param parentId2
	 * @return
	 */
	/*private int getPrdouctLineId(int parentId1, int parentId2) {
		System.out.println("p1"+parentId1);
		System.out.println("p2"+parentId2);
		WareService wareService = new WareService(this.dbOp);
		String productLineCondition = "  (product_line_catalog.catalog_id = " 
				+ parentId1 + " and product_line_catalog.catalog_type = 1) or (product_line_catalog.catalog_type = 2 and product_line_catalog.catalog_id = " + parentId2 + ")";
			voProductLine vpl = wareService.getProductLine(productLineCondition);
			if( vpl == null ) {
				return 0;
			}
			return vpl.getId();
	}*/

	/**
	 * 根据出库单id 得到代表订单商品线的 产品信息
	 * @param orderStockId
	 * @return
	 */
	/*private voProduct getRepresentProductOrder(int orderStockId) {
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		List<OrderStockProductBean> orderStockProductList = getOrderStockProductInfo(orderStockId);
		int x = orderStockProductList.size();
		float totalPrice = 0f;
		voProduct result = null;
		for( int i = 0; i < x; i ++ ) {
			OrderStockProductBean ospBean = orderStockProductList.get(i);
			float tempPrice = getTotalPriceOfOrderStockProduct(ospBean);
			if( tempPrice > totalPrice ) {
				totalPrice = tempPrice;
				result = ospBean.getProduct();
			}
		}
		return result;
	}*/
	
	/**
	 * 计算订单中商品的总价格
	 * @param ospBean
	 * @return
	 */
	private float getTotalPriceOfOrderStockProduct(OrderStockProductBean ospBean) {
		int productCount = ospBean.getStockoutCount();
		float result = productCount * ospBean.getProduct().getPrice();
		return result;
	}
	
	/**
	 * 得到出库单商品 以及商品信息 只有
	 * @param orderStockId
	 * @return
	 */
	private List<OrderStockProductBean> getOrderStockProductInfo(
			int orderStockId) {
		List<OrderStockProductBean> result = new ArrayList<OrderStockProductBean>();
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		String sql = "select osp.stockout_count, p.id, p.price, p.parent_id1, p.parent_id2 from order_stock_product osp, product p where osp.order_stock_id = "+orderStockId +" and osp.product_id = p.id;";
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				OrderStockProductBean ospBean = new OrderStockProductBean();
				ospBean.setStockoutCount(rs.getInt("stockout_count"));
				voProduct product = new voProduct();
				product.setId(rs.getInt("id"));
				product.setPrice(rs.getFloat("price"));
				product.setParentId1(rs.getInt("parent_id1"));
				product.setParentId2(rs.getInt("parent_id2"));
				ospBean.setProduct(product);
				result.add(ospBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	/**
	 * 比较日期,格式 yyyy-MM-dd;startDate > endDate 返回 -1;  startDate == endDate 返回0； startDate < endDate 返回 1；
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 */
	public int compareDate(String startDate, String endDate) throws ParseException {
		int result = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(startDate);
		Date date2 = sdf.parse(endDate);
		if( date1.getTime() > date2.getTime() ) {
			result = -1;
		} else if ( date1.getTime() == date2.getTime() ) {
			result = 0;
		} else {
			result = 1;
		}
		return result;
	}
	
	/**
	 * 得到天数的星期数，不足的按1 算
	 * @param during
	 * @return
	 */
	public int getWeekDuring(int during) {
		int result = 1;
		if( during == 0 ) {
			result = 0;
		} else {
			if( during%7 > 0) {
				result = (during/7) + 1;
			}
		}
		return result;
	}
	
	/**
	 * 一次计算各时段 分段平均值
	 * @param wsodList
	 * @return
	 */
	public Map<String, Float> calculateAverageDuration(int cycle, List<WareSendOutDurationBean> wsodList) {
		Map<String, Float> result = new HashMap<String,Float>();
		int x = wsodList.size();
		float duration1 = 0f;
		int count1 = 0;
		float duration2 = 0f;
		int count2 = 0;
		float duration3 = 0f;
		int count3 = 0;
		float duration4 = 0f;
		int count4 = 0;
		float everDuration1 = 0f;
		float everDuration2 = 0f;
		float everDuration3 = 0f;
		float everDuration4 = 0f;
		for( int i = 0 ; i < x; i ++ ) {
			WareSendOutDurationBean temp = wsodList.get(i);
			float durationF = temp.getDuration1();
			if( durationF == 0f ) {
				
			} else {
				duration1 += durationF;
				count1 +=1;
			}
			float durationS = temp.getDuration2();
			if( durationS == 0f ) {
				
			} else {
				duration2 += durationS;
				count2 +=1;
			}
			float durationT = temp.getDuration3();
			if( durationT == 0f ) {
				
			} else {
				duration3 += durationT;
				count3 +=1;
			}
			float durationFU = temp.getDuration4();
			if( durationFU == 0f ) {
				
			} else {
				duration4 += durationFU;
				count4 +=1;
			}
		}
		if( count1 != 0 ) {
			everDuration1 = duration1 / (float)count1;
		} else {
			everDuration1 = 0f;
		}
		if( count2 != 0 ) {
			everDuration2 = duration2 / (float)count2;
		} else {
			everDuration2 = 0f;
		}
		if( count3 != 0 ) {
			everDuration3 = duration3 / (float)count3;
		} else {
			everDuration3 = 0f;
		}
		if( count1 != 0 ) {
			everDuration4 = duration4 / (float)count4;
		} else {
			everDuration4 = 0f;
		}
		result.put("duration1", everDuration1);
		result.put("duration2", everDuration2);
		result.put("duration3", everDuration3);
		result.put("duration4", everDuration4);
		return result;
	}

	/**
	 * 计算所占百分比 int类型
	 * @param totalCount
	 * @param totalTCount
	 * @return
	 */
	public String calculatePercentage(int totalCount, int totalTCount) {
		if( totalTCount == 0 ) {
			return "0.00";
		}
		float percentage = ((float)totalCount * (float)100)/(float)totalTCount;
		return new Float(getKeepTwoDecimal(percentage)).toString();
	}
	/**
	 * 计算所占百分比 float类型
	 * @param totalCount
	 * @param totalTCount
	 * @return
	 */
	public String calculatePercentage(float totalCount, float totalTCount) {
		if( totalTCount == 0f ) {
			return "0.00";
		}
		float percentage = (totalCount * (float)100)/totalTCount;
		return new Float(getKeepTwoDecimal(percentage)).toString();
	}

	public List<TempOrderEffectiveInfoBean> getRepresentOrderProductEffectiveInfo(
			List<TempOrderEffectiveInfoBean> orderEffectiveInfoListAll) {
		List<TempOrderEffectiveInfoBean> result = new ArrayList<TempOrderEffectiveInfoBean>();
		Map<Integer, TempOrderEffectiveInfoBean> map = new HashMap<Integer, TempOrderEffectiveInfoBean>();
		int x = orderEffectiveInfoListAll.size();
		for( int i = 0 ; i < x; i ++ ) {
			TempOrderEffectiveInfoBean temp = orderEffectiveInfoListAll.get(i);
			if( map.containsKey(temp.getOrderStockId())) {
				TempOrderEffectiveInfoBean temp2 = map.get(temp.getOrderStockId());
				if( temp.getPerProductTotalPrice() > temp2.getPerProductTotalPrice() ) {
					map.put(temp.getOrderStockId(), temp);
				}
			} else {
				map.put(temp.getOrderStockId(), temp);
			}
		}
		result = new ArrayList<TempOrderEffectiveInfoBean>(map.values());
		return result;
	}
	
	
}
