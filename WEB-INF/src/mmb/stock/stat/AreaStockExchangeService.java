/**  
 * 文件名：AreaStockExchangeService.java  
 *  
 * 版本信息：  
 * 日期：2013-2-20  
 * Copyright 买卖宝 Corporation 2013   
 * 版权所有  
 *  
 */

package mmb.stock.stat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

/**
 * 此类描述的是： 库区调拨服务
 * 
 * @author: liubo
 * @version: 2013-2-20 下午02:55:21
 */

public class AreaStockExchangeService extends BaseServiceImpl {

	private final Log logger = LogFactory
			.getLog(AreaStockExchangeService.class);
	private static final int INTERVAL = 6;// 作为获取前6天条件
	private static final int PREDAY_INTERVAL = 2;// 作为获取前天条件
	private static final int NEXTDAY_INTERVAL = 4;// 作为获取后四天条件
	private static final int DAYINTERVAL = 20;// 作为统计20天发货量
	private static final float SALDAY = 7L;// 作为统计7日发货量

	public AreaStockExchangeService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public AreaStockExchangeService() {
		this.useConnType = CONN_IN_SERVICE;
	}

	/**
	 * 
	 * 此方法描述的是： 创建地区待调拨商品列表
	 * 
	 * @author: liubo
	 * @version: 2013-2-21 上午11:03:46
	 */
	public void createExchangeProductList2() {

		System.out.println("开始产生待调拨商品列表");
		long beginTime = System.currentTimeMillis();
		WareService wareService = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		DbOperation mainDBOP = null;
		DbOperation dbOp = null;
		try {
			// 获取调度区域列表
			List<Integer> exchangeAreaList = getExchangeAreaList();
			if (exchangeAreaList == null || exchangeAreaList.isEmpty()) {
				if (logger.isInfoEnabled()) {
					logger.info("地区不存在，无法统计待调度列表！");
				}
				return;
			}

			mainDBOP = new DbOperation();
			mainDBOP.init(DbOperation.DB);
			this.dbOp = mainDBOP;
			this.useConnType = BaseServiceImpl.CONN_IN_SERVICE;
			// 清空表
			this.deleteAll(mainDBOP);
			mainDBOP.startTransaction();

			// 获取20天内地区销售出库的商品和数量
			// String nowDate = "2012-10-31 23:59:59";
			// String beforeDate = "2012-10-11 00:00:00";
			String nowDate = DateUtil.getNow();
			String beforeDate = DateUtil.getBackFromDate(nowDate, DAYINTERVAL)
					+ " 00:00:00";
			StringBuilder querySql = new StringBuilder(
					"select product_id, sum(stock_out_count) as count, stock_area");
			querySql.append(" from stock_card where stock_area in (");

			dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE2);
			wareService = new WareService(dbOp);

			int productId = -1;
			int count = -1;
			int stockArea = -1;
			Map<String, StockCardBean> cachMap = new HashMap<String, StockCardBean>();

			StringBuilder areaIdBuilder = new StringBuilder();
			// 遍历地区,创建地区待调拨商品列表
			for (Integer areaId : exchangeAreaList) {
				if (areaIdBuilder.length() > 0) {
					areaIdBuilder.append(",");
				}
				areaIdBuilder.append(areaId);
			}

			querySql.append(areaIdBuilder.toString() + ")");
			querySql.append(" and create_datetime between '");
			querySql.append(beforeDate);
			querySql.append("' and '");
			querySql.append(nowDate);
			querySql.append("' and card_type=");
			querySql.append(StockCardBean.CARDTYPE_ORDERSTOCK);
			long onetime = System.currentTimeMillis();

			wareService.getDbOp().prepareStatement(
					querySql.toString() + " group by product_id, stock_area");
			ps = wareService.getDbOp().getPStmt();
			rs = ps.executeQuery();
			StockCardBean scBean = null;
			System.out.println("第一次查询sql--：" + querySql.toString()
					+ " group by product_id, stock_area");
			System.out.println("第一次查询共用时间--："
					+ (System.currentTimeMillis() - onetime) + "ms");
			while (rs.next()) {
				productId = rs.getInt("product_id");
				count = rs.getInt("count");
				stockArea = rs.getInt("stock_area");
				scBean = new StockCardBean();
				scBean.setProductId(productId);
				scBean.setStockArea(stockArea);
				scBean.setAllStockOutCount(count);
				cachMap.put(stockArea + "_" + productId, scBean);
			}
			// 开始构造调拨记录

			createExchangeInfo(wareService.getDbOp(), cachMap, mainDBOP);
			mainDBOP.commitTransaction();
		} catch (Exception e) {
			mainDBOP.rollbackTransaction();
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("method createExchangeProductList", e);
			}
		} finally {
			System.out.println("待调拨商品列表产生完毕，耗时："
					+ (System.currentTimeMillis() - beginTime) + "ms");
			if (wareService != null) {
				wareService.releaseAll();
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
					ps = null;
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					ps = null;
				}
			}
			if (mainDBOP != null) {
				mainDBOP.release();
			}
		}
	}
	
	/****
	 * @author: hepeng
	 * 待调拨商品列表
	 */
	public void createExchangeProductList() {
		
		System.out.println("开始产生待调拨商品列表");
		long beginTime = System.currentTimeMillis();
		WareService wareService = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		DbOperation mainDBOP = null;
		DbOperation dbOp = null;
		try {
			// 获取调度区域列表
			List<Integer> exchangeAreaList = getExchangeAreaList();
			if (exchangeAreaList == null || exchangeAreaList.isEmpty()) {
				if (logger.isInfoEnabled()) {
					logger.info("地区不存在，无法统计待调度列表！");
				}
				return;
			}

			mainDBOP = new DbOperation();
			mainDBOP.init(DbOperation.DB);
			this.dbOp = mainDBOP;
			this.useConnType = BaseServiceImpl.CONN_IN_SERVICE;
			// 清空表
			this.deleteAll(mainDBOP);
			
			mainDBOP.startTransaction();

			// 获取20天内地区销售出库的商品和数量
			// String nowDate = "2012-10-31 23:59:59";
			// String beforeDate = "2012-10-11 00:00:00";
			String nowDate = DateUtil.getNow();
			String beforeDate = DateUtil.getBackFromDate(nowDate, DAYINTERVAL)
					+ " 00:00:00";
			StringBuilder areaIdBuilder = new StringBuilder();
			// 遍历地区,创建地区待调拨商品列表
			for (Integer areaId : exchangeAreaList) {
				if (areaIdBuilder.length() > 0) {
					areaIdBuilder.append(",");
				}
				areaIdBuilder.append(areaId);
			}
			StringBuilder querySql = new StringBuilder(
					"SELECT t.proid as product_id  ,t.area as stock_area ,sum(t.currentstockcount) as currentstockcount ,sum(t.stockoutcount) as stockoutcount FROM (SELECT sc.product_id AS proid,sc.stock_area AS area, 0 AS currentstockcount,sum(sc.stock_out_count) AS stockoutcount FROM stock_card sc WHERE     sc.create_datetime BETWEEN '"+beforeDate+"' AND '"+nowDate+"' AND sc.card_type = 2 AND sc.stock_area IN ("+areaIdBuilder.toString()+")  GROUP BY sc.product_id, sc.stock_area  UNION ALL SELECT ps.product_id AS proid, ps.area, sum(ps.stock) AS currentstockcount, 0 AS stockoutcount  FROM product_stock ps WHERE ps.area IN ("+areaIdBuilder.toString()+") AND ps.type = 0 GROUP BY ps.product_id, ps.area) t GROUP by t.proid,t.area ");
			

			dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE2);
			wareService = new WareService(dbOp);

			int productId = -1;
			int count = -1;
			int stockArea = -1;
			Map<String, StockCardBean> cachMap = new HashMap<String, StockCardBean>();
            wareService.getDbOp().prepareStatement(
					querySql.toString());
			ps = wareService.getDbOp().getPStmt();
			rs = ps.executeQuery();
			StockCardBean scBean = null;
		
		
			
			//System.out.println("sql--：" + querySql.toString());
			//System.out.println("查询共用时间--："
			//		+ (System.currentTimeMillis() - beginTime) + "ms");
			while (rs.next()) {
				productId = rs.getInt("product_id");
				count = rs.getInt("stockoutcount");
				stockArea = rs.getInt("stock_area");
				scBean = new StockCardBean();
				scBean.setProductId(productId);
				scBean.setStockArea(stockArea);
				scBean.setAllStockOutCount(count);
				scBean.setCurrentStock(rs.getInt("currentstockcount"));
				cachMap.put(stockArea + "_" + productId, scBean);
			}
			
          if(cachMap.entrySet().size()>0){
			for (String key : cachMap.keySet()) {
				scBean = cachMap.get(key);
				float inAvgCount = 0l;
				String curDate = DateUtil.getNowDateStr();
				String preDate = DateUtil.getBackFromDate(curDate,
						PREDAY_INTERVAL) + " 00:00:00";
				String nextDate = DateUtil.getForwardFromDate(curDate,
						NEXTDAY_INTERVAL) + " 23:59:59";
				String befDate = DateUtil
						.getBackFromDate(curDate, INTERVAL) + " 00:00:00";
				AreaStockExchangeBean asBean = null;
				inAvgCount = Arith.div(scBean.getAllStockOutCount(),
						DAYINTERVAL, 2);
				if (inAvgCount < 1L) {
					inAvgCount = 1L;
				}
				// 当前结存小于7日发货量
				if (scBean.getCurrentStock() < Arith.mul(
						inAvgCount, SALDAY)) {
					// 是否存在7日内未完成预计到货单
					boolean flag = judgeExistBuyStockin(preDate, nextDate,
							scBean.getStockArea(), scBean.getProductId(),
							wareService.getDbOp());
					if (!flag) {
						// 是否存在7日内未完成调拨单
						boolean exflag = judgeExistExchange(curDate,
								befDate, scBean.getStockArea(),
								scBean.getProductId(), wareService.getDbOp());
						if (!exflag) {
							// 寻找其它地区
							for (String otherKey : cachMap.keySet()) {
								if (!key.split("_")[0].equals(otherKey
										.split("_")[0])
										&& key.split("_")[1]
												.equals(otherKey.split("_")[1])) {
									// 获取其它地区20天日均发货量
									float outAvgCount = Arith.div(cachMap
											.get(otherKey)
											.getAllStockOutCount(),
											DAYINTERVAL, 2);
									if (outAvgCount < 1L) {
										outAvgCount = 1L;
									}
									// 20天日均发货量小于当前结存
									if (Arith.mul(outAvgCount, 20) < scBean.getCurrentStock()) {
										asBean = new AreaStockExchangeBean();
										asBean.setOutArea(cachMap.get(
												otherKey).getStockArea());
										asBean.setOutSaleCount(outAvgCount);
										asBean.setProductId(cachMap.get(
												otherKey).getProductId());
										asBean.setSaleCount(inAvgCount);
										asBean.setArea(scBean
												.getStockArea());
										if (!this
												.addAreaStockExchange(asBean)) {
											throw new RuntimeException(
													"添加失败，数据库异常！");
										}
										break;
									}

								}
							}
						}
					}
				}
			}
          }
		
			mainDBOP.commitTransaction();
		} catch (Exception e) {
			mainDBOP.rollbackTransaction();
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("method createExchangeProductList", e);
			}
		} finally {
			System.out.println("待调拨商品列表产生完毕，耗时："
					+ (System.currentTimeMillis() - beginTime) + "ms");
			if (wareService != null) {
				wareService.releaseAll();
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					rs = null;
					ps = null;
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					ps = null;
				}
			}
			if (mainDBOP != null) {
				mainDBOP.release();
			}
		}
		
		
		
	}
	
	
	
	

	/**
	 * 此方法描述的是：
	 * 
	 * @author: liubo
	 * @version: 2013-2-21 下午06:42:03
	 */

	private void deleteAll(DbOperation mainDBOP) {
		String sql = "truncate area_stock_exchange";
		mainDBOP.executeUpdate(sql);
	}

	/**
	 * 此方法描述的是： 创建需要调拨商品记录
	 * 
	 * @count: 目标库20天发货量
	 * @author: liubo
	 * @version: 2013-2-21 下午01:33:38
	 * @param mainDBOP
	 * @throws SQLException
	 */
	private void createExchangeInfo(DbOperation dbOperation,
			Map<String, StockCardBean> cacheMap, DbOperation mainDBOP)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		PreparedStatement ps1 = null;
		Map<String, ProductStockBean> productStockCache = new HashMap<String, ProductStockBean>();
		try {
			StockCardBean scBean = null;
			StringBuilder queryProductStock = new StringBuilder();
			StringBuilder queryStockAera = new StringBuilder();
			if (cacheMap.entrySet().size() > 0) {
				for (String key : cacheMap.keySet()) {
					if (queryProductStock.length() > 0) {
						queryProductStock.append(" or ");
					}
					queryProductStock.append("product_id=" + key.split("_")[1]);

					if (queryStockAera.indexOf("area=" + key.split("_")[0]) == -1) {
						if (queryStockAera.length() > 0) {
							queryStockAera.append(" or ");
						}
						queryStockAera.append("area=" + key.split("_")[0]);
					}
				}

				// 获取当前结存
				String qs = "select sum(stock) as count, area, product_id "
						+ "from product_stock where ("
						+ queryStockAera.toString() + ") and type="
						+ ProductStockBean.STOCKTYPE_QUALIFIED + " and ("
						+ queryProductStock.toString()
						+ ") group by product_id,area";

				long onetime = System.currentTimeMillis();
				dbOperation.prepareStatement(qs);
				ps = dbOperation.getPStmt();
				rs = ps.executeQuery();
				ProductStockBean psBean = null;
				System.out.println("第二次查询sql--：" + qs);
				System.out.println("第二次查询共用时间--："
						+ (System.currentTimeMillis() - onetime) + "ms");
				while (rs.next()) {
					psBean = new ProductStockBean();
					psBean.setProductId(rs.getInt("product_id"));
					psBean.setAllStock(rs.getInt("count"));
					productStockCache.put(
							rs.getInt("area") + "_" + rs.getInt("product_id"),
							psBean);
				}

				for (String key : cacheMap.keySet()) {
					scBean = cacheMap.get(key);
					float inAvgCount = 0l;
					String curDate = DateUtil.getNowDateStr();
					String preDate = DateUtil.getBackFromDate(curDate,
							PREDAY_INTERVAL) + " 00:00:00";
					String nextDate = DateUtil.getForwardFromDate(curDate,
							NEXTDAY_INTERVAL) + " 23:59:59";
					String befDate = DateUtil
							.getBackFromDate(curDate, INTERVAL) + " 00:00:00";
					AreaStockExchangeBean asBean = null;
					inAvgCount = Arith.div(scBean.getAllStockOutCount(),
							DAYINTERVAL, 2);
					if (inAvgCount < 1L) {
						inAvgCount = 1L;
					}
					// 当前结存小于7日发货量
					if (productStockCache.get(key).getAllStock() < Arith.mul(
							inAvgCount, SALDAY)) {
						// 是否存在7日内未完成预计到货单
						boolean flag = judgeExistBuyStockin(preDate, nextDate,
								scBean.getStockArea(), scBean.getProductId(),
								dbOperation);
						if (!flag) {
							// 是否存在7日内未完成调拨单
							boolean exflag = judgeExistExchange(curDate,
									befDate, scBean.getStockArea(),
									scBean.getProductId(), dbOperation);
							if (!exflag) {
								// 寻找其它地区
								for (String otherKey : cacheMap.keySet()) {
									if (!key.split("_")[0].equals(otherKey
											.split("_")[0])
											&& key.split("_")[1]
													.equals(otherKey.split("_")[1])) {
										// 获取其它地区20天日均发货量
										float outAvgCount = Arith.div(cacheMap
												.get(otherKey)
												.getAllStockOutCount(),
												DAYINTERVAL, 2);
										if (outAvgCount < 1L) {
											outAvgCount = 1L;
										}
										// 20天日均发货量小于当前结存
										if (Arith.mul(outAvgCount, 20) < productStockCache
												.get(otherKey).getAllStock()) {
											asBean = new AreaStockExchangeBean();
											asBean.setOutArea(cacheMap.get(
													otherKey).getStockArea());
											asBean.setOutSaleCount(outAvgCount);
											asBean.setProductId(cacheMap.get(
													otherKey).getProductId());
											asBean.setSaleCount(inAvgCount);
											asBean.setArea(scBean
													.getStockArea());
											if (!this
													.addAreaStockExchange(asBean)) {
												throw new RuntimeException(
														"添加失败，数据库异常！");
											}
											break;
										}

									}
								}
							}
						}
					}
				}
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}

	}

	/**
	 * 此方法描述的是： 判断是否存在从今天开始7日前的未完成跨区库存调拨单和货位调拨单
	 * 
	 * @author: liubo
	 * @version: 2013-2-21 下午03:56:11
	 * @throws SQLException
	 */

	private boolean judgeExistExchange(String curDate, String befDate,
			Integer areaId, int productId, DbOperation dbOperation)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		ResultSet rs1 = null;
		PreparedStatement ps1 = null;
		boolean existArea = false;
		boolean existCargo = false;
		try {
			StringBuilder sqlBuilder = new StringBuilder(
					"select count(se.id) as count from stock_exchange se, stock_exchange_product sep where ");
			sqlBuilder
					.append("se.id=sep.stock_exchange_id and sep.product_id=");
			sqlBuilder.append(productId);
			sqlBuilder.append(" and se.create_datetime between '");
			sqlBuilder.append(befDate);
			sqlBuilder.append("' and '");
			sqlBuilder.append(curDate);
			sqlBuilder.append("' and ");
			sqlBuilder.append("se.stock_in_area=");
			sqlBuilder.append(areaId);
			sqlBuilder.append(" and ");
			sqlBuilder.append("se.stock_out_area!=");
			sqlBuilder.append(areaId);
			sqlBuilder.append(" and se.status!=");
			sqlBuilder.append(StockExchangeBean.STATUS7);
			sqlBuilder.append(" and se.status!=");
			sqlBuilder.append(StockExchangeBean.STATUS0);
			sqlBuilder.append(" and se.status!=");
			sqlBuilder.append(StockExchangeBean.STATUS8);
			dbOperation.prepareStatement(sqlBuilder.toString());
			ps = dbOperation.getPStmt();
			rs = ps.executeQuery();
			if (rs != null) {
				rs.next();
				int count = rs.getInt("count");
				if (count > 0) {
					existArea = true;
				}
			}
			sqlBuilder = new StringBuilder(
					"select count(co.id) count from cargo_operation co, cargo_operation_cargo coc");
			sqlBuilder.append(" where coc.oper_id=co.id and coc.product_id=");
			sqlBuilder.append(productId);
			sqlBuilder.append(" and co.create_datetime between '");
			sqlBuilder.append(befDate);
			sqlBuilder.append("' and '");
			sqlBuilder.append(curDate);
			sqlBuilder.append("' and ");
			sqlBuilder.append("co.stock_in_area=");
			sqlBuilder.append(areaId);
			sqlBuilder.append(" and ");
			sqlBuilder.append("co.stock_out_area!=");
			sqlBuilder.append(areaId);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS36);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS35);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS34);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS28);
			sqlBuilder.append(" and co.effect_status!=");
			sqlBuilder.append(CargoOperationBean.EFFECT_STATUS4);
			sqlBuilder.append(" and co.type=");
			sqlBuilder.append(CargoOperationBean.TYPE3);
			dbOperation.prepareStatement(sqlBuilder.toString());
			ps1 = dbOperation.getPStmt();
			rs1 = ps1.executeQuery();
			if (rs1 != null) {
				rs1.next();
				int count = rs1.getInt("count");
				if (count > 0) {
					existCargo = true;
				}
			}
			if (existCargo || existArea) {
				return true;
			}
			return false;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (rs1 != null) {
				rs1.close();
			}
			if (ps1 != null) {
				ps1.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 判断是否存在7日内未完成预计到货单
	 * 
	 * @author: liubo
	 * @version: 2013-2-21 下午03:28:26
	 * @param productId
	 * @throws SQLException
	 */

	private boolean judgeExistBuyStockin(String curDate, String nextDate,
			Integer areaId, int productId, DbOperation dbOperation)
			throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			StringBuilder sqlBuilder = new StringBuilder(
					"select count(bs.id) count from buy_stock bs, buy_stock_product bsp where ");
			sqlBuilder.append("bs.id=bsp.buy_stock_id and bsp.product_id=");
			sqlBuilder.append(productId);
			sqlBuilder.append(" and bs.expect_arrival_datetime between '");
			sqlBuilder.append(curDate);
			sqlBuilder.append("' and '");
			sqlBuilder.append(nextDate);
			sqlBuilder.append("' and ");
			sqlBuilder.append("bs.area=");
			sqlBuilder.append(areaId);
			sqlBuilder.append(" and bs.status!=");
			sqlBuilder.append(BuyStockBean.STATUS6);
			sqlBuilder.append(" and bs.status!=");
			sqlBuilder.append(BuyStockBean.STATUS8);
			sqlBuilder.append(" and bs.status!=");
			sqlBuilder.append(BuyStockBean.STATUS0);
			dbOperation.prepareStatement(sqlBuilder.toString());
			ps = dbOperation.getPStmt();
			rs = ps.executeQuery();
			if (rs != null) {
				rs.next();
				int count = rs.getInt("count");
				if (count <= 0) {
					return false;
				} else {
					return true;
				}
			}
			return false;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 获取地区列表
	 * 
	 * @author: liubo
	 * @version: 2013-2-21 上午11:52:07
	 */

	private List<Integer> getExchangeAreaList() {

		List<Integer> areaIdList = new ArrayList<Integer>();
		areaIdList.add(ProductStockBean.AREA_ZC);
		areaIdList.add(ProductStockBean.AREA_WX);
		return areaIdList;
	}

	/**
	 * 
	 * 此方法描述的是： 添加库区调拨
	 * 
	 * @author: liubo
	 * @version: 2013-2-20 下午02:56:17
	 */
	public boolean addAreaStockExchange(AreaStockExchangeBean bean) {
		return addXXX(bean, "area_stock_exchange");
	}

	@SuppressWarnings("unchecked")
	public List<AreaStockExchangeBean> getAreaStockExchangeBeanList(
			String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"area_stock_exchange", "mmb.stock.stat.AreaStockExchangeBean");
	}

	@SuppressWarnings("unchecked")
	public List<AreaStockExchangeBean> getAreaStockExchangeBeanList() {
		return getXXXList(null, -1, -1, null, "area_stock_exchange",
				"mmb.stock.stat.AreaStockExchangeBean");
	}

	public int getAreaStockExchangeBeanCount(String condition) {
		return getXXXCount(condition, "area_stock_exchange", "id");
	}

	public AreaStockExchangeBean getAreaStockExchangeBean(String condition) {
		return (AreaStockExchangeBean) getXXX(condition, "area_stock_exchange",
				"mmb.stock.stat.AreaStockExchangeBean");
	}

	public boolean updateAreaStockExchangeBean(String set, String condition) {
		return updateXXX(set, condition, "area_stock_exchange");
	}

	public boolean deleteAreaStockExchangeBean(String condition) {
		return deleteXXX(condition, "area_stock_exchange");
	}

	/**
	 * 此方法描述的是： 根据地区类型获取调拨数据
	 * 
	 * @author: liubo
	 * @version: 2013-2-22 下午02:45:37
	 * @param countPerPage
	 * @param index
	 * @throws SQLException
	 */

	public List<AreaStockExchangeBean> getExchangeList(String type, int index,
			int countPerPage) throws SQLException {

		if (type == null) {
			throw new RuntimeException("参数错误！");
		}

		ResultSet rs = null;
		PreparedStatement ps = null;
		WareService wareService = null;
		voProduct product = null;
		try {
			wareService = new WareService();
			StringBuilder strBuilder = new StringBuilder();
			strBuilder
					.append("select ase.id aid, ase.product_id, ase.product_id apid, ase.sale_count acount, ase.out_area aoarea, ase.out_sale_count aocount, ase.area aarea from area_stock_exchange ase, product_stock ps");
			strBuilder
					.append(" where ase.product_id=ps.product_id and ps.area=ase.area");
			strBuilder.append(" and ase.area=");
			if (type.equals("0")) {
				strBuilder.append(ProductStockBean.AREA_WX);
			} else {
				strBuilder.append(ProductStockBean.AREA_ZC);
			}
			strBuilder.append(" and ps.type=");
			strBuilder.append(ProductStockBean.STOCKTYPE_QUALIFIED);
			strBuilder.append(" and ps.stock<");
			strBuilder.append("ase.sale_count*7");
			String query = DbOperation.getPagingQuery(strBuilder.toString(),
					index, countPerPage);
			dbOp.prepareStatement(query);
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			if (rs == null) {
				return new ArrayList<AreaStockExchangeBean>();
			}
			List<AreaStockExchangeBean> resultList = new ArrayList<AreaStockExchangeBean>();
			AreaStockExchangeBean bean = null;
			while (rs.next()) {
				bean = new AreaStockExchangeBean();
				bean.setId(rs.getInt("aid"));
				bean.setArea(rs.getInt("aarea"));
				bean.setOutArea(rs.getInt("aoarea"));
				bean.setProductId(rs.getInt("product_id"));
				bean.setSaleCount(rs.getFloat("acount"));
				bean.setOutSaleCount(rs.getFloat("aocount"));
				product = wareService.getProduct(bean.getProductId());
				bean.setProductCode(product.getCode());
				bean.setProductOriName(product.getOriname());
				bean.setInAreaStockCount(getStockCount(bean.getProductId(),
						bean.getArea()));// 目的地区结存
				bean.setOutAreaStockCount(getStockCount(bean.getProductId(),
						bean.getOutArea()));// 源地区结存
				bean.setStockinCount(getBuyStockCount(bean.getProductId(),
						bean.getOutArea()));// 预计到货数量
				bean.setBuyStockCode(getBuyStockCode(getBuyStockList(
						bean.getProductId(), bean.getOutArea())));// 7日内预计到货单
				bean.setExchangeInCount(getExchangeInCount(bean.getProductId(),
						bean.getOutArea()));// 跨区调入量
				bean.setExchangeOutCount(getExchangeOutCount(
						bean.getProductId(), bean.getOutArea()));// 跨区调出量
				bean.setNeedExchangeCount(getNeedExchangeCount(bean, type, null));// 需调拨量
				resultList.add(bean);
			}
			return resultList;
		} finally {
			if (wareService != null) {
				wareService.releaseAll();
			}
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}

	}

	/**
	 * 此方法描述的是： 抽取出7日内预计到货单号，并封装url
	 * 
	 * @author: liubo
	 * @version: 2013-2-25 下午05:33:54
	 */

	private String getBuyStockCode(List<BuyStockBean> buyStockCodeList) {

		StringBuilder strBuilder = new StringBuilder();
		for (BuyStockBean buyStock : buyStockCodeList) {
			if (strBuilder.length() > 0) {
				strBuilder.append("<br>");
			}
			strBuilder
					.append("<a target=_blank href=../admin/stock2/buyStock.jsp?stockId=");
			strBuilder.append(buyStock.getId());
			strBuilder.append(">");
			strBuilder.append(buyStock.getCode());
			strBuilder.append("</a>");
		}
		return strBuilder.toString();
	}

	/**
	 * 此方法描述的是： 需要调拨数量
	 * 
	 * @author: liubo
	 * @version: 2013-2-22 下午05:26:38
	 * @param flag
	 *            调拨类型,0调入，1调出
	 * @param type
	 *            地区类型,0无锡，1增城
	 * @throws SQLException
	 */

	private int getNeedExchangeCount(AreaStockExchangeBean bean, String type,
			String flag) throws SQLException {

		int result1 = bean.getOutAreaStockCount()
				- Math.round(Arith.mul(bean.getOutSaleCount(), 10));
		if (result1 < 0) {
			result1 = 0;
		}
		if (flag == null) {
			if (type.equals("1")) {
				int result = Math.round(Arith.mul(bean.getOutSaleCount(), 20));
				if (result1 > result) {
					return result;
				}
				return result1;
			} else {
				return executeQuery(bean, result1);
			}
		} else {
			if (type.equals("1")) {
				if ("0".equals(flag)) {
					int result = Math.round(Arith.mul(bean.getOutSaleCount(),
							20));
					if (result1 > result) {
						return result;
					}
					return result1;
				} else {
					return executeQuery(bean, result1);
				}
			} else {
				if ("0".equals(flag)) {
					return executeQuery(bean, result1);
				} else {
					int result = Math.round(Arith.mul(bean.getOutSaleCount(),
							20));
					if (result1 > result) {
						return result;
					}
					return result1;
				}
			}
		}

	}

	private int executeQuery(AreaStockExchangeBean bean, int result1)
			throws SQLException {
		int result2 = -1;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			StringBuilder sql = new StringBuilder(
					"select sum(ps.stock) count from product_stock ps");
			sql.append(" where ps.product_id=");
			sql.append(bean.getProductId());
			sql.append(" and ps.area in");
			sql.append("(");
			sql.append(ProductStockBean.AREA_BJ);
			sql.append(",");
			sql.append(ProductStockBean.AREA_GF);
			sql.append(",");
			sql.append(ProductStockBean.AREA_GS);
			sql.append(",");
			sql.append(ProductStockBean.AREA_WX);
			sql.append(",");
			sql.append(ProductStockBean.AREA_ZC);
			sql.append(")");
			sql.append(" and ps.type=");
			sql.append(ProductStockBean.STOCKTYPE_QUALIFIED);
			dbOp.prepareStatement(sql.toString());
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			rs.next();
			result2 = rs.getInt("count");
			int result3 = (int) (result2 * 0.3 - bean.getInAreaStockCount());
			if (result1 > result3) {
				if (result3 < 0) {
					result3 = 0;
				}
				return result3;
			} else {
				return result1;
			}

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 源库为area的合格库跨区调拨单中sku作业量
	 * 
	 * @author: liubo
	 * @version: 2013-2-22 下午05:23:47
	 * @throws SQLException
	 */

	private int getExchangeOutCount(int productId, int area)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(coc.stock_count) count from cargo_operation as co");
			sqlBuilder
					.append(", cargo_operation_cargo as coc where coc.oper_id=co.id");
			sqlBuilder.append(" and coc.product_id=");
			sqlBuilder.append(productId);
			sqlBuilder.append(" and co.stock_out_area=");
			sqlBuilder.append(area);
			// sqlBuilder.append(" and co.stock_out_type=");
			// sqlBuilder.append(ProductStockBean.STOCKTYPE_QUALIFIED);
			sqlBuilder.append(" and co.stock_in_area!=");
			sqlBuilder.append(area);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS36);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS35);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS34);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS28);
			sqlBuilder.append(" and co.effect_status!=");
			sqlBuilder.append(CargoOperationBean.EFFECT_STATUS4);
			sqlBuilder.append(" and co.type=");
			sqlBuilder.append(CargoOperationBean.TYPE3);
			sqlBuilder.append(" and coc.type=0");
			dbOp.prepareStatement(sqlBuilder.toString());
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt("count");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 目的库为area地区的合格库跨区调拨单中sku作业量
	 * 
	 * @author: liubo
	 * @version: 2013-2-22 下午04:55:10
	 * @throws SQLException
	 */
	private int getExchangeInCount(int productId, int area) throws SQLException {

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder
					.append("select sum(coc.stock_count) count from cargo_operation as co");
			sqlBuilder
					.append(", cargo_operation_cargo as coc where coc.oper_id=co.id");
			sqlBuilder.append(" and coc.product_id=");
			sqlBuilder.append(productId);
			sqlBuilder.append(" and co.stock_in_area=");
			sqlBuilder.append(area);
			// sqlBuilder.append(" and co.stock_in_type=");
			// sqlBuilder.append(ProductStockBean.STOCKTYPE_QUALIFIED);
			sqlBuilder.append(" and co.stock_out_area!=");
			sqlBuilder.append(area);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS36);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS35);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS34);
			sqlBuilder.append(" and co.status!=");
			sqlBuilder.append(CargoOperationProcessBean.OPERATION_STATUS28);
			sqlBuilder.append(" and co.effect_status!=");
			sqlBuilder.append(CargoOperationBean.EFFECT_STATUS4);
			sqlBuilder.append(" and co.type=");
			sqlBuilder.append(CargoOperationBean.TYPE3);
			sqlBuilder.append(" and coc.type=0");
			dbOp.prepareStatement(sqlBuilder.toString());
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt("count");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 获取7日内预计到货总数
	 * 
	 * @author: liubo
	 * @version: 2013-2-22 下午04:37:21
	 * @throws SQLException
	 */

	private int getBuyStockCount(int productId, int area) throws SQLException {

		String curDate = DateUtil.getNow();
		String preDate = DateUtil.getBackFromDate(curDate, 2) + " 00:00:00";
		String nextDate = DateUtil.getForwardFromDate(curDate, 4) + " 23:59:59";
		// area = 3;
		// String curDate = "2012-09-28 23:59:59";
		// String befDate = "2012-09-09 00:00:00";
		String sql = "select sum(bsp.buy_count) count from buy_stock bs, buy_stock_product bsp"
				+ " where bs.id=bsp.buy_stock_id and bsp.product_id="
				+ productId
				+ " and bs.expect_arrival_datetime between '"
				+ preDate
				+ "' and '"
				+ nextDate
				+ "' and bs.status!="
				+ BuyStockBean.STATUS8
				+ " and bs.status!="
				+ BuyStockBean.STATUS0
				+ " and bs.status!="
				+ BuyStockBean.STATUS6
				+ " and bs.status!="
				+ BuyStockBean.STATUS4 + " and bs.area=" + area;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			dbOp.prepareStatement(sql);
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt("count");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 获取7日内预计到货单
	 * 
	 * @author: liubo
	 * @version: 2013-2-22 下午04:21:33
	 * @throws SQLException
	 */

	private List<BuyStockBean> getBuyStockList(int productId, int area)
			throws SQLException {

		String curDate = DateUtil.getNow();
		String preDate = DateUtil.getBackFromDate(curDate, 2) + " 00:00:00";
		String nextDate = DateUtil.getForwardFromDate(curDate, 4) + " 23:59:59";
		// area = 3;
		// String nextDate = "2012-09-28 23:59:59";
		// String befDate = "2012-09-09 00:00:00";
		String sql = "select bs.id, bs.code from buy_stock bs, buy_stock_product bsp"
				+ " where bs.id=bsp.buy_stock_id and bsp.product_id="
				+ productId
				+ " and bs.expect_arrival_datetime between '"
				+ preDate
				+ "' and '"
				+ nextDate
				+ "' and bs.status!="
				+ BuyStockBean.STATUS8
				+ " and bs.status!="
				+ BuyStockBean.STATUS0
				+ " and bs.status!="
				+ BuyStockBean.STATUS6
				+ " and bs.status!="
				+ BuyStockBean.STATUS4 + " and bs.area=" + area;

		ResultSet rs = null;
		PreparedStatement ps = null;
		BuyStockBean bs = null;
		List<BuyStockBean> resultList = new ArrayList<BuyStockBean>();
		try {
			dbOp.prepareStatement(sql);
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			if (rs == null) {
				return resultList;
			}
			while (rs.next()) {
				bs = new BuyStockBean();
				bs.setId(rs.getInt("id"));
				bs.setCode(rs.getString("code"));
				resultList.add(bs);
			}
			return resultList;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}

	}

	/**
	 * 此方法描述的是： 获取地区合格库结存
	 * 
	 * @author: liubo
	 * @version: 2013-2-22 下午04:09:13
	 * @throws SQLException
	 */

	private int getStockCount(int productId, int area) throws SQLException {

		String sql = "select sum(stock) as count from product_stock where area="
				+ area + " and type=0 and product_id=" + productId;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			dbOp.prepareStatement(sql);
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt("count");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 获取待调入或待调出记录数量
	 * 
	 * @author: liubo
	 * @version: 2013-2-25 下午08:03:51
	 * @throws SQLException
	 */

	public int getWaitExchangeProductCount(String type, String flag)
			throws SQLException {

		int area = -1;
		StringBuilder strBuilder = new StringBuilder(
				"select count(co.id) as count from ");
		if (type.equals("0")) {
			area = ProductStockBean.AREA_WX;
		} else {
			area = ProductStockBean.AREA_ZC;
		}

		if (flag.equals("0")) {
			strBuilder
					.append("cargo_operation co, cargo_operation_cargo coc where co.stock_in_area=");
		} else {
			strBuilder
					.append("cargo_operation co, cargo_operation_cargo coc where co.stock_out_area=");
		}
		strBuilder.append(area);
		strBuilder.append(" and co.stock_out_area!=co.stock_in_area");
		strBuilder.append(" and coc.oper_id=co.id");
		strBuilder.append(" and co.status!=");
		strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS36);
		strBuilder.append(" and co.status!=");
		strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS35);
		strBuilder.append(" and co.status!=");
		strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS34);
		strBuilder.append(" and co.effect_status!=");
		strBuilder.append(CargoOperationBean.EFFECT_STATUS4);
		strBuilder.append(" and co.type=");
		strBuilder.append(CargoOperationBean.TYPE3);
		strBuilder.append(" and coc.type=0");

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			dbOp.prepareStatement(strBuilder.toString());
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			rs.next();
			return rs.getInt("count");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 获取待调入或待调出商品列表
	 * 
	 * @author: liubo
	 * @version: 2013-2-25 下午08:21:21
	 * @throws SQLException
	 */

	public List<CargoOperationBean> getWaitExchangeProductList(String type,
			String flag, int index, int countPerPage) throws SQLException {
		// 构造待调入或调出查询语句
		StringBuilder strBuilder = constructQuerySql(null, type, flag);

		String query = DbOperation.getPagingQuery(strBuilder.toString(), index,
				countPerPage);
		ResultSet rs = null;
		PreparedStatement ps = null;
		List<CargoOperationBean> asList = new ArrayList<CargoOperationBean>();
		CargoOperationBean coBean = null;
		WareService wareService = null;
		ICargoService cargoService = null;
		voProduct product = null;
		try {
			wareService = new WareService();
			cargoService = ServiceFactory.createCargoService(
					BaseServiceImpl.CONN_IN_SERVICE, dbOp);
			dbOp.prepareStatement(query);
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			while (rs.next()) {
				product = wareService.getProduct(rs.getInt("product_id"));
				coBean = new CargoOperationBean();
				coBean.setProductCode(product.getCode());
				coBean.setProductOriName(product.getOriname());
				coBean.setInAreaStockCount(getStockCount(product.getId(),
						rs.getInt("area")));
				coBean.setOutAreaStockCount(getStockCount(product.getId(),
						rs.getInt("outarea")));
				coBean.setId(rs.getInt("coid"));
				coBean.setCode(rs.getString("code"));
				coBean.setStatus(rs.getInt("status"));
				coBean.setExchangeCount(rs.getInt("excount"));
				CargoOperationProcessBean process = cargoService
						.getCargoOperationProcess("id=" + coBean.getStatus());// 当前阶段
				if (process != null) {
					coBean.setStatusName(process.getStatusName());
				} else {
					coBean.setStatusName("");
				}
				asList.add(coBean);
			}
			return asList;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (wareService != null) {
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 导出待调入或调出商品列表
	 * 
	 * @param aseBeanId
	 * @param type
	 * @param flag
	 * @return
	 * @throws SQLException
	 */
	public HSSFWorkbook exportExchangeProduct(String[] aseBeanId, String type,
			String flag) throws SQLException {

		String fileName = null;
		if (type.equals("0")) {
			if (flag.equals("0")) {
				fileName = "无锡待调入商品列表";
			} else {
				fileName = "无锡待调出商品列表";
			}
		} else {
			if (flag.equals("1")) {
				fileName = "增城待调入商品列表";
			} else {
				fileName = "增城待调出商品列表";
			}
		}
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 在Excel 工作簿中建一工作表
		HSSFSheet sheet = workbook.createSheet(fileName);

		sheet.setColumnWidth(0, 15 * 256);
		sheet.setColumnWidth(1, 15 * 256);
		sheet.setColumnWidth(2, 15 * 256);
		sheet.setColumnWidth(3, 15 * 256);
		sheet.setColumnWidth(4, 15 * 256);
		sheet.setColumnWidth(5, 15 * 256);
		sheet.setColumnWidth(6, 15 * 256);
		sheet.setColumnWidth(7, 15 * 256);
		// 设置单元格格式(文本)
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));

		// 在索引0的位置创建行（第一行）
		HSSFRow row = sheet.createRow((short) 0);

		HSSFCell cell1 = row.createCell(0);// 第一列
		HSSFCell cell2 = row.createCell(1);
		HSSFCell cell3 = row.createCell(2);
		HSSFCell cell4 = row.createCell(3);
		HSSFCell cell5 = row.createCell(4);
		HSSFCell cell7 = row.createCell(5);
		HSSFCell cell8 = row.createCell(6);
		HSSFCell cell9 = row.createCell(7);
		// 定义单元格为字符串类型
		cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell5.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell7.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell8.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell9.setCellType(HSSFCell.CELL_TYPE_STRING);

		/*
		 * cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
		 * cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
		 * cell3.setEncoding(HSSFCell.ENCODING_UTF_16);
		 */
		// 在单元格中输入数据
		cell1.setCellValue("序号");
		cell2.setCellValue("产品编号");
		cell3.setCellValue("原名称");
		cell4.setCellValue("无锡结存");
		cell5.setCellValue("增城结存");
		cell7.setCellValue("调拨单");
		cell8.setCellValue("状态");
		cell9.setCellValue("调拨量");
		StringBuilder strBuilder = constructQuerySql(aseBeanId, type, flag);
		String query = DbOperation
				.getPagingQuery(strBuilder.toString(), -1, -1);
		ResultSet rs = null;
		PreparedStatement ps = null;
		WareService wareService = null;
		ICargoService cargoService = null;
		voProduct product = null;
		try {
			wareService = new WareService();
			cargoService = ServiceFactory.createCargoService(
					BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
			dbOp.prepareStatement(query);
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			int i = 0;
			int sequence = 1;
			while (rs.next()) {
				row = sheet.createRow((short) i + 1);
				// 1 序号
				HSSFCell cellc1 = row.createCell(0);
				cellc1.setCellStyle(cellStyle);
				cellc1.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc1.setCellValue(sequence++ + "");
				product = wareService.getProduct(rs.getInt("product_id"));
				// 2产品编号
				HSSFCell cellc2 = row.createCell(1);
				cellc2.setCellStyle(cellStyle);
				cellc2.setCellType(HSSFCell.CELL_TYPE_STRING);
				cellc2.setCellValue(product.getCode());
				// 3 产品原名称
				HSSFCell cellc3 = row.createCell(2);
				cellc3.setCellStyle(cellStyle);
				cellc3.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc3.setCellValue(product.getOriname());
				// 4无锡结存
				HSSFCell cellc4 = row.createCell(3);
				cellc4.setCellStyle(cellStyle);
				cellc4.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				if (type.equals("0")) {
					if ("0".equals(flag)) {
						cellc4.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("area")));
					} else {
						cellc4.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("outarea")));
					}
				} else {
					if ("0".equals(flag)) {
						cellc4.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("outarea")));
					} else {
						cellc4.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("area")));
					}
				}
				// 5 增城结存
				HSSFCell cellc5 = row.createCell(4);
				cellc5.setCellStyle(cellStyle);
				cellc5.setCellType(HSSFCell.CELL_TYPE_STRING);
				if (type.equals("0")) {
					if ("0".equals(flag)) {
						cellc5.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("outarea")));
					} else {
						cellc5.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("area")));
					}
				} else {
					if ("0".equals(flag)) {
						cellc5.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("area")));
					} else {
						cellc5.setCellValue(getStockCount(
								rs.getInt("product_id"), rs.getInt("outarea")));
					}
				}

				CargoOperationProcessBean process = cargoService
						.getCargoOperationProcess("id=" + rs.getInt("status"));// 当前阶段

				// 7 调拨单
				HSSFCell cellc7 = row.createCell(5);
				cellc7.setCellStyle(cellStyle);
				cellc7.setCellType(HSSFCell.CELL_TYPE_STRING);
				cellc7.setCellValue(rs.getString("code"));

				// 8 调拨单状态
				HSSFCell cellc8 = row.createCell(6);
				cellc8.setCellStyle(cellStyle);
				cellc8.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				if (process != null) {
					cellc8.setCellValue(process.getStatusName());
				} else {
					cellc8.setCellValue("");
				}

				// 9 调拨量
				HSSFCell cellc9 = row.createCell(7);
				cellc9.setCellStyle(cellStyle);
				cellc9.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc9.setCellValue(rs.getInt("excount"));
				i++;
			}
			return workbook;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			if (wareService != null) {
				wareService.releaseAll();
			}
		}
	}

	/**
	 * 
	 * 此方法描述的是： 构造查询语句
	 * 
	 * @author: liubo
	 * @version: 2013-3-5 上午11:15:57
	 */
	private StringBuilder constructQuerySql(String[] exchangeId, String type,
			String flag) {

		int area = -1;
		StringBuilder strBuilder = new StringBuilder("select co.id as coid,");
		strBuilder.append("co.stock_in_area area,co.stock_out_area outarea,");
		if (type.equals("0")) {
			area = ProductStockBean.AREA_WX;
		} else {
			area = ProductStockBean.AREA_ZC;
		}
		strBuilder
				.append("coc.product_id,co.code, coc.stock_count as excount, co.status from ");
		if (flag.equals("0")) {
			strBuilder
					.append("cargo_operation co, cargo_operation_cargo coc where co.stock_in_area=");
		} else {
			strBuilder
					.append("cargo_operation co, cargo_operation_cargo coc where co.stock_out_area=");
		}
		strBuilder.append(area);
		strBuilder.append(" and co.stock_out_area!=co.stock_in_area");
		strBuilder.append(" and coc.oper_id=co.id");
		strBuilder.append(" and co.status!=");
		strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS36);
		strBuilder.append(" and co.status!=");
		strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS35);
		strBuilder.append(" and co.status!=");
		strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS34);
		strBuilder.append(" and co.effect_status!=");
		strBuilder.append(CargoOperationBean.EFFECT_STATUS4);
		strBuilder.append(" and co.type=");
		strBuilder.append(CargoOperationBean.TYPE3);
		strBuilder.append(" and coc.type=0");
		if (exchangeId != null && exchangeId.length > 0) {
			StringBuilder cargoOperationId = new StringBuilder();
			for (int i = 0; i < exchangeId.length; i++) {
				if (cargoOperationId.length() > 0) {
					cargoOperationId.append(",");
				}
				cargoOperationId.append(exchangeId[i]);

			}
			strBuilder.append(" and co.id in (");
			strBuilder.append(cargoOperationId.toString());
			strBuilder.append(")");
		}
		return strBuilder;
	}

	/**
	 * 此方法描述的是： 判断是否存在对应sku(productId)未完成的跨区货位间调拨单
	 * 
	 * @author: liubo
	 * @version: 2013-2-26 下午03:01:18
	 * @throws SQLException
	 */

	public String checkExchange(String area, int productId) throws SQLException {

		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder
					.append("select count(co.id) count from cargo_operation co, cargo_operation_cargo coc");
			strBuilder.append(" where co.id=coc.oper_id and coc.product_id=");
			strBuilder.append(productId);
			strBuilder.append(" and co.stock_in_area=");
			strBuilder.append(area);
			strBuilder.append(" and co.stock_in_area!=co.stock_out_area");
			strBuilder.append(" and co.status!=");
			strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS36);
			strBuilder.append(" and co.status!=");
			strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS35);
			strBuilder.append(" and co.status!=");
			strBuilder.append(CargoOperationProcessBean.OPERATION_STATUS34);
			strBuilder.append(" and co.effect_status!=");
			strBuilder.append(CargoOperationBean.EFFECT_STATUS4);
			strBuilder.append(" and co.type=");
			strBuilder.append(CargoOperationBean.TYPE3);
			dbOp.prepareStatement(strBuilder.toString());
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			if (rs == null) {
				return "0";
			}
			rs.next();
			int count = rs.getInt("count");
			if (count > 0) {
				return "1";
			} else {
				return "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "3";
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}

	/**
	 * 此方法描述的是： 获取需调拨商品数量
	 * 
	 * @author: liubo
	 * @version: 2013-3-5 下午04:26:15
	 * @throws SQLException
	 */

	public int getAreaStockExchangeBeanCount(int areaId) throws SQLException {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder
					.append("select count(ase.id) count from area_stock_exchange ase, product_stock ps");
			strBuilder
					.append(" where ase.product_id=ps.product_id and ps.area=ase.area");
			strBuilder.append(" and ase.area=");
			strBuilder.append(areaId);
			strBuilder.append(" and ps.type=");
			strBuilder.append(ProductStockBean.STOCKTYPE_QUALIFIED);
			strBuilder.append(" and ps.stock<");
			strBuilder.append("ase.sale_count*7");
			dbOp.prepareStatement(strBuilder.toString());
			ps = dbOp.getPStmt();
			rs = ps.executeQuery();
			if (rs == null) {
				return 0;
			}
			rs.next();
			int count = rs.getInt("count");
			if (count > 0) {
				return count;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (ps != null) {
				ps.close();
			}
			if (rs != null) {
				rs.close();
			}
		}
	}
}
