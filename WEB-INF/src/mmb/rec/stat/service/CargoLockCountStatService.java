package mmb.rec.stat.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmb.rec.sys.bean.TempCargoLockCountInfoBean;
import mmb.rec.sys.bean.TempOrderStockLockCountInfoBean;
import mmb.rec.sys.bean.WareCargoStockLockCheckBean;
import mmb.stock.stat.BuyStockinUpshelfBean;
import mmb.ware.WareService;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

public class CargoLockCountStatService  extends BaseServiceImpl {
	
	public CargoLockCountStatService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public CargoLockCountStatService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	//记录错误的货位锁定库存 信息的Bean
	public boolean addWareCargoStockLockCheck(WareCargoStockLockCheckBean bean) {
		return addXXX(bean, "ware_cargo_stock_lock_check");
	}

	public List getWareCargoStockLockCheckList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "ware_cargo_stock_lock_check", "mmb.rec.sys.bean.WareCargoStockLockCheckBean");
	}

	public int getWareCargoStockLockCheckCount(String condition) {
		return getXXXCount(condition, "ware_cargo_stock_lock_check", "id");
	}

	public WareCargoStockLockCheckBean getWareCargoStockLockCheck(String condition) {
		return (WareCargoStockLockCheckBean) getXXX(condition, "ware_cargo_stock_lock_check",
		"mmb.rec.sys.bean.WareCargoStockLockCheckBean");
	}

	public boolean updateWareCargoStockLockCheck(String set, String condition) {
		return updateXXX(set, condition, "ware_cargo_stock_lock_check");
	}

	public boolean deleteWareCargoStockLockCheck(String condition) {
		return deleteXXX(condition, "ware_cargo_stock_lock_check");
	}
	/*public List getAllCargoLockCouldBe(String cargoWholeCode, String productCode) {
		
		DbOperation dbOpSlave = this.getDbOp();
		WareService wareService = new WareService(dbOpSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService stockService=ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+cargoWholeCode+"'");
		voProduct product=wareService.getProduct(productCode);
		//货位库存锁定量
		
		*//****************调拨冻结**************************//*
		//开启数据库连接
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		String query = "select se.code,sepc.stock_count "
				+" from stock_exchange se "
				+" join stock_exchange_product sep on se.id=sep.stock_exchange_id and sep.product_id="+product.getId()
				+" join stock_exchange_product_cargo sepc on sepc.stock_exchange_product_id=sep.id and sepc.cargo_info_id="+ciBean.getId()
				+" where (se.status in(2,3,5,6,8) or (se.status=1 and sep.status=1))";
		//System.out.println("1-->"+query);
		ResultSet rs = dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				orderList.add(rs.getString(1));
				countList.add(rs.getInt(2));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();//释放所有资源
		}
		
		*//****************销售出库冻结**************************//*
		//开启数据库连接
		DbOperation dbOp1 = new DbOperation();
		dbOp1.init("adult_slave");
		String query1 = "select os.code,ospc.count "
				+" from order_stock os"
				+" join order_stock_product osp on osp.order_stock_id=os.id and osp.product_id= "+product.getId()
				+" join order_stock_product_cargo ospc on ospc.order_stock_product_id=osp.id and ospc.cargo_whole_code= '"+ciBean.getWholeCode()+"'"
				+" where os.status=5";

		ResultSet rs1 = dbOp1.executeQuery(query1);
		try {
			while (rs1.next()) {
				orderList.add(rs1.getString(1));
				countList.add(rs1.getInt(2));
			}
			rs1.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally { 
			dbOp1.release();//释放所有资源 
		}
		
		
		*//****************报损单冻结**************************//*
		//开启数据库连接
		DbOperation dbOp2 = new DbOperation();
		dbOp2.init("adult_slave");
		String query2="select bo.receipts_number,bpc.count "
			+" from bsby_operationnote bo"
			+" join bsby_product bp on bp.operation_id=bo.id and bp.product_id="+product.getId()
			+" join bsby_product_cargo bpc on bpc.bsby_product_id=bp.id and bpc.cargo_id="+ciBean.getId()
			+" where (bo.current_type = 1 or bo.current_type=6) and bo.type = 0";
		ResultSet rs2 = dbOp2.executeQuery(query2); 
		try {
			while (rs2.next()) {
				orderList.add(rs2.getString(1));
				countList.add(rs2.getInt(2));
			}
			rs2.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally { 
			dbOp2.release();//释放所有资源 
		}
		
		*//****************仓内作业单冻结**************************//*
		//开启数据库连接
		DbOperation dbOp3 = new DbOperation();
		dbOp3.init("adult_slave");
		String query3 = "select distinct co.code,coc.stock_count "
				+" from cargo_operation co "
				+" join cargo_operation_cargo coc on coc.oper_id=co.id and coc.use_status=1 and coc.product_id= "
				+product.getId()+" and coc.out_cargo_whole_code= '"+ciBean.getWholeCode()+"'"
				+" where co.status in(2,3,11,12,20,21,29,30) "
				+" and co.effect_status in (0,1)" ;
		ResultSet rs3 = dbOp3.executeQuery(query3);
		try {
			while (rs3.next()) {
				orderList.add(rs3.getString(1));
				countList.add(rs3.getInt(2));
			}
			rs3.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp3.release();//释放所有资源
		}
		*//****************货位异常单冻结**************************//*
		//开启数据库连接
		DbOperation dbOp4 = new DbOperation();
		dbOp4.init("adult_slave");
		String query4 = "select sa.code,sap.lock_count "
				+" from sorting_abnormal_product sap "
				+" join cargo_info ci on ci.whole_code=sap.cargo_whole_code and ci.whole_code= '"+ciBean.getWholeCode()+"'"
				+" join sorting_abnormal sa on sa.id=sap.sorting_abnormal_id"
				+" where sap.product_id= "+product.getId()
				+" and sap.lock_count>0 and sap.status in (0,1,2,3)";
		ResultSet rs4 = dbOp4.executeQuery(query4);
		try {
			while (rs4.next()) {
				orderList.add(rs4.getString(1));
				countList.add(rs4.getInt(2));
			}
			rs4.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp4.release();//释放所有资源
		}
	}*/
	
	/**
	 * 根据参数的list，将其中的可能锁定了货位库存的数量加到Map对应的货位库存id键的值上去
	 * 最终返回的map是加成了所给list数据的map
	 * 注意计算结束后会将list变为null
	 * @param oriMap
	 * @param resourceList
	 * @return
	 */
	public Map<String, Integer> getSumLockCargoStockCountMap(Map<String,Integer> oriMap, List<TempCargoLockCountInfoBean> resourceList) {
		if( resourceList != null && resourceList.size() != 0 ) {
			int x = resourceList.size();
			for( int i = 0 ; i < x; i++ ) {
				TempCargoLockCountInfoBean tclciBean = resourceList.get(i);
				String cargoProductStockId = new Integer(tclciBean.getCargoProductStockId()).toString();
				if( oriMap.containsKey( cargoProductStockId ) ) {
					int currentTotalCount = oriMap.get(cargoProductStockId);
					/*if( cargoProductStockId.equals("326")) {
						System.out.println("326@"+currentTotalCount);
					}*/
					currentTotalCount += tclciBean.getLockCount();
					oriMap.put(cargoProductStockId, currentTotalCount);
				} else {
					/*if( cargoProductStockId.equals("326")) {
						System.out.println("326@"+tclciBean.getLockCount());
					}*/
					oriMap.put(cargoProductStockId, tclciBean.getLockCount() );
				}
			}
			resourceList = null;
		}
		return oriMap;
	}

	/**
	 *  得到调拨单的锁定List
	 *  2014.3.18 添加了退货库的仓库统计
	 * @return
	 */
	public List<TempCargoLockCountInfoBean> getStockExchangeLockList() {
		List<TempCargoLockCountInfoBean> result = new ArrayList<TempCargoLockCountInfoBean>();
		String query = "select sepc.stock_count, cps.id from stock_exchange se join stock_exchange_product sep on se.id=sep.stock_exchange_id "
						+" join stock_exchange_product_cargo sepc on sepc.stock_exchange_product_id=sep.id join cargo_product_stock cps on (cps.product_id = sep.product_id and cps.cargo_id = sepc.cargo_info_id)"
						+" where (se.status in(2,3,5,6,8) or (se.status=1 and sep.status=1)) and se.stock_out_type not in (7,8,10) and sepc.type not in (7,8,10);";
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				TempCargoLockCountInfoBean tclciBean = new TempCargoLockCountInfoBean();
				tclciBean.setCargoProductStockId(rs.getInt("cps.id"));
				tclciBean.setLockCount(rs.getInt("sepc.stock_count"));
				result.add(tclciBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			release(dbOp);
		}
		return result;
	}
	
	/**
	 * 得到订单发货可能锁定List
	 * 
	 * 这里加上了 计算pda分拣导致的锁定的货位库存变更的逻辑
	 * @return
	 */
	public List<TempCargoLockCountInfoBean> getOrderStockOutLockList() {
		ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, this.dbOp);
		List<TempCargoLockCountInfoBean> result = new ArrayList<TempCargoLockCountInfoBean>();
		String query = "select distinct( ospc.id), os.stock_area, os.id,ospc.count,ospc.cargo_product_stock_id,osp.product_id,ospc.cargo_whole_code from order_stock os join order_stock_product osp on osp.order_stock_id=os.id "
						+" join order_stock_product_cargo ospc on ospc.order_stock_product_id=osp.id "
						+" where os.status=5;";
		ResultSet rs = this.dbOp.executeQuery(query);
		Map<String, TempOrderStockLockCountInfoBean> map = new HashMap<String,TempOrderStockLockCountInfoBean>();
		int zcOperationCargoId = 0;
		int wxOperationCargoId = 0;
		try {
			while (rs.next()) {
				TempOrderStockLockCountInfoBean toslciBean = new TempOrderStockLockCountInfoBean();
				String key = rs.getInt("os.stock_area") + "_" + rs.getInt("os.id") + "_" + rs.getInt("osp.product_id");
				toslciBean.setArea(rs.getInt("os.stock_area"));
				toslciBean.setCargoProductStockId(rs.getInt("ospc.cargo_product_stock_id"));
				toslciBean.setCargoWholeCode(rs.getString("ospc.cargo_whole_code"));
				toslciBean.setLockCount(rs.getInt("ospc.count"));
				toslciBean.setOrderStockId(rs.getInt("os.id"));
				toslciBean.setProductId(rs.getInt("osp.product_id"));
				map.put(key, toslciBean);
			}
			List<TempOrderStockLockCountInfoBean> pdaLockCountList = new ArrayList<TempOrderStockLockCountInfoBean>();
			String query2 = "select sbo.order_stock_id, sbop.product_id, sbop.sorting_count, cps.id, ci.area_id from sorting_batch_group sbg, sorting_batch_order sbo, sorting_batch_order_product sbop, cargo_info ci, cargo_product_stock cps, order_stock os where " 
							+" ci.id = sbop.cargo_id and ci.id = cps.cargo_id and cps.product_id = sbop.product_id and sbo.order_stock_id = os.id and " 
							+" sbg.id = sbo.sorting_group_id and sbo.id = sbop.sorting_batch_order_id and os.status = 5 and sbg.sorting_type = 1;";
			ResultSet rs2 = this.dbOp.executeQuery(query2);
			while ( rs2.next() ) {
				TempOrderStockLockCountInfoBean toslciBean2 = new TempOrderStockLockCountInfoBean();
				toslciBean2.setArea(rs2.getInt("ci.area_id"));
				toslciBean2.setProductId(rs2.getInt("sbop.product_id"));
				toslciBean2.setOrderStockId(rs2.getInt("sbo.order_stock_id"));
				toslciBean2.setCount(rs2.getInt("sbop.sorting_count"));
				toslciBean2.setCargoProductStockId(rs2.getInt("cps.id"));
				pdaLockCountList.add(toslciBean2);
			}
			//查找作业货位的信息
			List operationCargoInfoList = cargoService.getCargoInfoBeanist("store_type = 5 and stock_type = 0", -1, -1, null);
			if( operationCargoInfoList == null ) {
				
			} else {
				for( int i = 0; i < operationCargoInfoList.size(); i ++ ) {
					CargoInfoBean ciBean = (CargoInfoBean)operationCargoInfoList.get(i);
					if( ciBean.getAreaId() == ProductStockBean.AREA_ZC ){
						zcOperationCargoId = ciBean.getId();
					}
					if( ciBean.getAreaId() == ProductStockBean.AREA_WX ) {
						wxOperationCargoId = ciBean.getId();
					}
				}
			}
			//重新计算订单出库导致的锁定
			result = recoculateLockCount(map, pdaLockCountList, zcOperationCargoId, wxOperationCargoId);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			release(dbOp);
		}
		
		return result;
	}
	
	/**
	 * 计算订单出库时在货位上的锁定量， 首先要从原来的货位上的锁定量扣除pda分拣了的数量
	 * 并且添加对应的pda分拣量的锁定量 到对应库的作业货位货位库存锁定量上去
	 * @param map
	 * @param pdaLockCountList
	 * @param zcOperationCargoId
	 * @param wxOperationCargoId
	 * @return
	 */
	private List<TempCargoLockCountInfoBean> recoculateLockCount(
			Map<String, TempOrderStockLockCountInfoBean> map,
			List<TempOrderStockLockCountInfoBean> pdaLockCountList,
			int zcOperationCargoId, int wxOperationCargoId) {
		ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, this.dbOp);
		List<TempCargoLockCountInfoBean> result = new ArrayList<TempCargoLockCountInfoBean>();
		for( int i = 0 ; i < pdaLockCountList.size(); i ++ ) {
			TempOrderStockLockCountInfoBean toslciBean = pdaLockCountList.get(i);
			if( toslciBean.getCount() > 0 ) {
				String targetKey = toslciBean.getArea() + "_" + toslciBean.getOrderStockId() + "_" + toslciBean.getProductId();
				if( map.containsKey(targetKey) ) {
					TempOrderStockLockCountInfoBean toslciBean2 = map.get(targetKey);
					int newCount = toslciBean2.getLockCount() - toslciBean.getCount();
					toslciBean2.setLockCount(newCount);
					//在扣掉了 pda分拣锁到别处的数量后 就可以算作正常锁的了
					/*TempCargoLockCountInfoBean tclciBean = new TempCargoLockCountInfoBean();
					tclciBean.setCargoProductStockId(toslciBean2.getCargoProductStockId());
					tclciBean.setLockCount(newCount);
					result.add(tclciBean);*/
					CargoProductStockBean cpsBean = null;
					if( toslciBean.getArea() == ProductStockBean.AREA_ZC ) {
						cpsBean = cargoService.getCargoProductStock("cargo_id = " + zcOperationCargoId + " and product_id =" + toslciBean.getProductId());
					} else if( toslciBean.getArea() == ProductStockBean.AREA_WX ) {
						cpsBean = cargoService.getCargoProductStock("cargo_id = " + wxOperationCargoId + " and product_id =" + toslciBean.getProductId());
					}
					//从出库单订单中计算过后，对应的锁定量应该放在了作业货位上， 也给计算到其中
					if( cpsBean != null ) {
						TempCargoLockCountInfoBean tclciBean2 = new TempCargoLockCountInfoBean();
						tclciBean2.setCargoProductStockId(cpsBean.getId());
						tclciBean2.setLockCount(toslciBean.getCount());
						//System.out.println("***作业货位" + tclciBean2.getCargoProductStockId()+ "--" + tclciBean2.getLockCount());
						result.add(tclciBean2);
					}
				}
			}
		}
		Iterator<String> itr = map.keySet().iterator();
		for(; itr.hasNext(); ) {
			String targetKey = itr.next();
			TempOrderStockLockCountInfoBean toslciBean3 = map.get(targetKey);
			TempCargoLockCountInfoBean tclciBean = new TempCargoLockCountInfoBean();
			tclciBean.setCargoProductStockId(toslciBean3.getCargoProductStockId());
			tclciBean.setLockCount(toslciBean3.getLockCount());
			result.add(tclciBean);
		}
		return result;
	}

	/**
	 *  得到报损单可能锁定List
	 *  添加了对退货库的锁定的统计
	 * @return
	 */
	public List<TempCargoLockCountInfoBean> getBsOperationLockList() {
		List<TempCargoLockCountInfoBean> result = new ArrayList<TempCargoLockCountInfoBean>();
		String query = "select bpc.count, bpc.cargo_product_stock_id from bsby_operationnote bo join bsby_product bp on bp.operation_id=bo.id "
						+" join bsby_product_cargo bpc on bpc.bsby_product_id=bp.id "
						+" where (bo.current_type = 1 or bo.current_type=6) and bo.type = 0 and bo.warehouse_type not in (7,8,10);";
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				TempCargoLockCountInfoBean tclciBean = new TempCargoLockCountInfoBean();
				tclciBean.setCargoProductStockId(rs.getInt("bpc.cargo_product_stock_id"));
				tclciBean.setLockCount(rs.getInt("bpc.count"));
				result.add(tclciBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			release(dbOp);
		}
		return result;
	}
	
	/**
	 * 得到仓内作业可能锁定List
	 * @return
	 */
	public List<TempCargoLockCountInfoBean> getCargoOperationLockList() {
		List<TempCargoLockCountInfoBean> result = new ArrayList<TempCargoLockCountInfoBean>();
		String query = "select coc.stock_count,coc.out_cargo_product_stock_id from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id and coc.use_status=1 "
						+" where co.status in(2,3,11,12,20,21,29,30) and co.effect_status in (0,1);";
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				TempCargoLockCountInfoBean tclciBean = new TempCargoLockCountInfoBean();
				tclciBean.setCargoProductStockId(rs.getInt("coc.out_cargo_product_stock_id"));
				tclciBean.setLockCount(rs.getInt("coc.stock_count"));
				result.add(tclciBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			release(dbOp);
		}
		return result;
	}
	
	/**
	 * 得到异常单可能的锁定List
	 * @return
	 */
	public List<TempCargoLockCountInfoBean> getAbnormalCargoStockLockList() {
		List<TempCargoLockCountInfoBean> result = new ArrayList<TempCargoLockCountInfoBean>();
		String query = "select sap.lock_count, cps.id from sorting_abnormal_product sap join cargo_info ci on ci.whole_code=sap.cargo_whole_code "
						+" join sorting_abnormal sa on sa.id=sap.sorting_abnormal_id "
						+" join cargo_product_stock cps on cps.cargo_id = ci.id and cps.product_id = sap.product_id where sap.lock_count>0 and sap.status in (0,1,2,3);";
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				TempCargoLockCountInfoBean tclciBean = new TempCargoLockCountInfoBean();
				tclciBean.setCargoProductStockId(rs.getInt("cps.id"));
				tclciBean.setLockCount(rs.getInt("sap.lock_count"));
				result.add(tclciBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			release(dbOp);
		}
		return result;
	}

	/**
	 * 得到需要验证的所有货位库存的列表
	 * 添加了对退货库相关锁定库存的统计
	 * @return
	 */
	public List<TempCargoLockCountInfoBean> getCargoProductStockLockCountInfo() {
		List<TempCargoLockCountInfoBean> result = new ArrayList<TempCargoLockCountInfoBean>();
		String query = "select distinct( cps.id), ps.product_id,ps.area, cps.stock_lock_count, ci.whole_code, ci.id, ci.area_id from product_stock ps, cargo_product_stock cps, cargo_info ci "
						+" where ps.product_id = cps.product_id and cps.cargo_id = ci.id and ci.area_id = ps.area "
						+" and ( ps.lock_count + ps.stock ) > 0 and cps.stock_lock_count >=0 and ps.type not in (7,8,10) and ps.area in (3,4) and ci.stock_type not in (7,8,10);";
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			while (rs.next()) {
				TempCargoLockCountInfoBean tclciBean = new TempCargoLockCountInfoBean();
				tclciBean.setCargoInfoId(rs.getInt("ci.id"));
				tclciBean.setCargoWholeCode(rs.getString("ci.whole_code"));
				tclciBean.setCargoProductStockId(rs.getInt("cps.id"));
				tclciBean.setLockCount(rs.getInt("cps.stock_lock_count"));
				tclciBean.setProductId(rs.getInt("ps.product_id"));
				tclciBean.setArea(rs.getInt("ps.area"));
				tclciBean.setCargoArea(rs.getInt("ci.area_id"));
				result.add(tclciBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			release(dbOp);
		}
		return result;
	}

	/**
	 * 根据需要检查锁定量的货位库存列表和，计算得的实际单据在货位上锁定的总量，排查锁定量异常情况。
	 * @param list
	 * @param mayLockCargoStockMap
	 */
	public boolean dealProblemCargoProductStockLockInfo(
			List<TempCargoLockCountInfoBean> list,
			Map<String, Integer> mayLockCargoStockMap) {
		boolean result = true;
		int x = list.size();
		//System.out.println("------------");
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(now);
		for( int i = 0 ; i < x; i ++ ) {
			TempCargoLockCountInfoBean tclciBean = list.get(i);
			String cargoProductStockId = new Integer(tclciBean.getCargoProductStockId()).toString();
			WareCargoStockLockCheckBean wcpsBean = new WareCargoStockLockCheckBean();
			wcpsBean.setCargoId(tclciBean.getCargoInfoId());
			wcpsBean.setCargoProductStockId(tclciBean.getCargoProductStockId());
			wcpsBean.setCargoLockCount(tclciBean.getLockCount());
			wcpsBean.setCargoWholeCode(tclciBean.getCargoWholeCode());
			wcpsBean.setProductId(tclciBean.getProductId());
			wcpsBean.setWareArea(tclciBean.getArea());
			wcpsBean.setCreateDatetime(today);
			if( mayLockCargoStockMap.containsKey(cargoProductStockId) ) {
				int needLock = mayLockCargoStockMap.get(cargoProductStockId);
				if( needLock == tclciBean.getLockCount() ) {
					//System.out.println("货位库存:" + tclciBean.getCargoProductStockId() + "--没有问题 货位上锁定了 **"+tclciBean.getLockCount()+"** 单据上锁定了**"+ needLock +"**- 地区：" + tclciBean.getArea() + " 货位地区：" + tclciBean.getCargoArea() );
				} else if( needLock > tclciBean.getLockCount() ) {
					//System.out.println("货位库存:" + tclciBean.getCargoProductStockId() + "--比需要锁的多 " + (needLock - tclciBean.getLockCount()) + " 地区：" + tclciBean.getArea()+ " 货位地区：" + tclciBean.getCargoArea());
					wcpsBean.setOrderLockCount(needLock);
					if( !addWareCargoStockLockCheck(wcpsBean) ) {
						result = false;
					}
				} else {
					//System.out.println("货位库存:" + tclciBean.getCargoProductStockId() + "--比需要锁的少 " + (tclciBean.getLockCount() - needLock ) + " 地区：" + tclciBean.getArea()+ " 货位地区：" + tclciBean.getCargoArea());
					wcpsBean.setOrderLockCount(needLock);
					if( !addWareCargoStockLockCheck(wcpsBean) ) {
						result = false;
					}
				}
			} else {
				//货位上本来也没有锁定量，也没有单据锁定，正常
				if( tclciBean.getLockCount() == 0 ) {
					//System.out.println("货位库存:" + tclciBean.getCargoProductStockId() + "--没有问题 货位上没锁定量，单据上也没有锁定量， 地区：" + tclciBean.getArea()+ " 货位地区：" + tclciBean.getCargoArea());
				} else {
					//货位上有锁定量，但没有单据锁定， 多锁了
					//System.out.println("货位库存:" + tclciBean.getCargoProductStockId() + "--货位上有锁定量 " + tclciBean.getLockCount() + "单据没有锁 地区：" + tclciBean.getArea()+ " 货位地区：" + tclciBean.getCargoArea());
					wcpsBean.setOrderLockCount(0);
					if( !addWareCargoStockLockCheck(wcpsBean) ) {
						result = false;
					}
				}
			}
		}
		return result;
	}

}
