package adultadmin.util.timedtask;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmb.stock.stat.SortingBatchBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockPrintLogBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

public class SortingBatchJob implements Job {
	public static byte[] sortingBatchLock = new byte[0];

	@SuppressWarnings("unchecked")
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "分拣批次定时任务开始");
		synchronized (sortingBatchLock) {
			WareService wareService = new WareService();
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			//IStockService stockService2 = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IBatchBarcodeService batchBarcodeService = ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try {
				int maxBatch = 0;// 今天已打印的最大批次
				String maxBatchSql="select batch from order_stock_print_log "+
					"where time>'"+DateUtil.getNowDateStr()+" 00:00:00' "+
					"and time<'"+DateUtil.getNowDateStr()+" 23:59:59' and type=1 "+
					"and batch>0 order by id desc limit 1";
				ResultSet maxBatchRs=wareService.getDbOp().executeQuery(maxBatchSql);
				if(maxBatchRs.next()){
					maxBatch=maxBatchRs.getInt(1);
				}
				maxBatchRs.close();
				// 发货地区只加增城和无锡
				CargoInfoAreaBean cg = new CargoInfoAreaBean();
				cg.setId(3);
				cg.setOldId(3);
				List<CargoInfoAreaBean> areaList = new ArrayList<CargoInfoAreaBean>();
				areaList.add(cg);
				CargoInfoAreaBean cg1 = new CargoInfoAreaBean();
				cg1.setId(4);
				cg1.setOldId(4);
				areaList.add(cg1);
				//List areaList = cargoService.getCargoInfoAreaList("id>0", -1, -1, null);
				List<String> batchList = new ArrayList<String>();
				//循环多品订单生成批次
				SortingBatchBean sb1= null;
				try {
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
						//17点之后生成的分拣批次，取消申请发货半个小时的限制
						String date1=DateUtil.getNow();//现在时间
						String date2=DateUtil.getNowDateStr()+" 17:00:00";//当天17点
						String sql=null;
						if(date1.compareTo(date2)>=0){//17点之后，取消申请发货半个小时的限制
							sql = "select a.id,a.deliver,b.product_type,a.name,a.code,b.id,b.code,b.stock_area,b.deliver from user_order a join order_stock b on b.order_id=a.id where b.status in(0,1) and b.stock_area=" + ciaBean.getOldId() + " and a.flat <> 2 and b.deliver!=0 order by b.create_datetime";
						}else{//17点之前，有申请发货半个小时的限制
							sql = "select a.id,a.deliver,b.product_type,a.name,a.code,b.id,b.code,b.stock_area,b.deliver from user_order a join order_stock b on b.order_id=a.id where '"+DateUtil.getLastHalfHour()+"'>b.create_datetime and b.status in(0,1) and b.stock_area=" + ciaBean.getOldId() + " and a.flat <> 2 and b.deliver!=0 order by b.create_datetime";
						}
						// String sql =
						// "select a.id,a.deliver,a.product_type,a.name,a.code,b.id,b.stock_area,b.deliver from user_order a join order_stock b on b.order_id=a.id where b.status in(0,1) and a.buy_mode=0 and (a.deliver=0 or a.deliver=12 or a.deliver=9 or a.deliver=11) and b.stock_area="
						// + ciaBean.getOldId() + " order by b.create_datetime";
						ResultSet rs = stockService.getDbOp().executeQuery(sql);
						List<voOrder> osList = new ArrayList<voOrder>();
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
									// vo.setOrderStock(osBean);
									List<OrderStockProductBean> outOrderProductList = stockService.getOrderStockProductList("order_stock_id=" + osBean.getId(), -1, -1, null);
									// if (outOrderProductList.size() == 0) {
									// psService.getDbOp().rollbackTransaction();
									// continue;
									// }
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
										//String sql1 = "select cps.id,cps.cargo_id,min(cps.stock_count),ci.whole_code,ci.stock_area_id" + " from cargo_product_stock cps" + " join cargo_info ci on ci.id=cps.cargo_id" + " where product_id=" + ospBean.getProductId() + " and ci.area_id = " + ciaBean.getId() + " and (ci.store_type=0 or ci.store_type=4) and cps.stock_count >= " + ospBean.getStockoutCount() + " group by ci.stock_area_id";
										String sql1 = "select cps.id,cps.cargo_id,ci.whole_code,ci.stock_area_id,cps.stock_count "+
												" from cargo_product_stock cps "+
												" join cargo_info ci on ci.id=cps.cargo_id "+
												" where cps.product_id=" + ospBean.getProductId() + " and ci.area_id = " + ciaBean.getId() + 
												" and (ci.store_type=0 or ci.store_type=4) and cps.stock_count >= " + ospBean.getStockoutCount() +
												" and cps.stock_count =( "+
												" select min(cps2.stock_count) "+
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
											log.setAdminId(0);
											log.setAdminName("");
											log.setLogId(osBean.getId());
											log.setLogType(StockAdminHistoryBean.ORDER_STOCK_STATUS2);
											log.setOperDatetime(DateUtil.getNow());
											// int stockcount =
											// cargoService.getCargoProductStockCount(cps.getId()
											// + "") + stockOutCount;
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
									apBean.setSortingUserName("");
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
									//orderCustomerBean.setSerialNumber(0);
									orderCustomerBean.setStatus(OrderStockBean.STATUS6);
									String name=vo.getName();
									if(name.length()>15){
										name=name.substring(0,15);
									}
									orderCustomerBean.setName(name);
									orderCustomerBean.setBatch(maxBatch + 1);
									//orderCustomerBean.setBatch(0);
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
									 OrderStockPrintLogBean osplBean = new
									 OrderStockPrintLogBean();
									 osplBean.setBatch(maxBatch + 1);
									 osplBean.setType(1);
									 osplBean.setUserId(0);
									 osplBean.setUserName("");
									 osplBean.setTime(DateUtil.getNow());
									 osplBean.setRemark(vo.getCode());
									 if(!stockService.addOrderStockPrintLog(osplBean)){
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
									if(!siService.addSortingBatchOrderInfo(sboBean)){// 添加波次下的订单
										psService.getDbOp().rollbackTransaction();
									}
									if(!stockService.updateOrderStock("status=" + OrderStockBean.STATUS6, "status<>3 and order_id=" + sboBean.getOrderId())){
										psService.getDbOp().rollbackTransaction();
									}
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
				}catch (Exception e) {
					if(sb1!=null){
						siService.updateSortingBatchInfo("status=" + SortingBatchBean.STATUS0, "id=" + sb1.getId());
					}
						e.printStackTrace();
				}
				if(psService.getDbOp().getConn().getAutoCommit()==false){
					psService.getDbOp().getConn().setAutoCommit(true);
				}
				for (int i = 0; i < batchList.size(); i++) {
					String batchCode = (String) batchList.get(i);
					int count = siService.getSortingBatchOrderCount("delete_status<>1 and sorting_batch_code='" + batchCode + "'");
					if (count == 0) {
						siService.updateSortingBatchInfo("status="+SortingBatchBean.STATUS, "code='" + batchCode + "'");
					}
				}
				System.out.println(DateUtil.getNow() + "存储完毕");
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				System.out.println(DateUtil.getNow() + "数据库添加完毕");
				wareService.releaseAll();
				//stockService2.releaseAll();
			}
		}
		System.out.println("分拣批次定时任务结束");
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
}
