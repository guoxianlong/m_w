package mmb.stock.stat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import mmb.ware.WareService;
import java.util.Map;

import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import adultadmin.action.vo.voProduct;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class StatService  extends BaseServiceImpl{
	public StatService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public StatService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	// 发货及时率统计
	public boolean addOrderStockTimely(OrderStockTimelyBean bean) {
		return addXXX(bean, "order_stock_timely");
	}

	public ArrayList getOrderStockTimelyList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_stock_timely", "mmb.stock.stat.OrderStockTimelyBean");
	}

	public int getOrderStockTimelyCount(String condition) {
		return getXXXCount(condition, "order_stock_timely", "id");
	}

	public OrderStockTimelyBean getOrderStockTimely(String condition) {
		return (OrderStockTimelyBean) getXXX(condition, "order_stock_timely",
		"mmb.stock.stat.OrderStockTimelyBean");
	}

	public boolean updateOrderStockTimely(String set, String condition) {
		return updateXXX(set, condition, "order_stock_timely");
	}

	public boolean deleteOrderStockTimely(String condition) {
		return deleteXXX(condition, "order_stock_timely");
	}
	
	
	
	//售后调退货管理
	public ArrayList getStockExchangeList(String condition, int index, int count, String orderBy ) {
		return getXXXList(condition, index, count, orderBy, "stock_exchange", "adultadmin.bean.stock.StockExchangeBean");
	}
	
	public boolean updateStockExchange(String set, String condition) {
		return updateXXX(set, condition, "stock_exchange");
	}
	
	public ProductCodeInfoBean getProductExchangeByCode(String code)
			throws Exception {
		ProductCodeInfoBean pcib = null;
		// 数据库操作类
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return pcib;
		}
		ResultSet rs = null;

		// 构建查询语句
		String query = "select id, code, name from product";
		if (code != null) {
			query += " where code = '" + code + "'";
		} else {
			throw new Exception("错误的产品编号");
		}

		// 执行查询
		rs = dbOp.executeQuery(query);

		if (rs == null) {
			release(dbOp);
			return pcib;
		}

		try {
			if (rs.next()) {
				pcib = new ProductCodeInfoBean();
				pcib.setId(rs.getInt("id"));
				pcib.setCode(rs.getString("code"));
				pcib.setName(rs.getString("name"));
			} else {
				throw new Exception("错误的条码: " + code);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return pcib;
	}
	
	public ProductCodeInfoBean getProductExchangeById(int id) {
	ProductCodeInfoBean pcib = null;
	// 数据库操作类
	DbOperation dbOp = getDbOp();
	if (!dbOp.init()) {
		return pcib;
	}
	ResultSet rs = null;
	
	// 构建查询语句
	String query = "select id, code, name from product";
	if (id != -1 && id != 0) {
		query += " where id = " + id;
	}else {
		query += " where id < 0";
	}
	
	// 执行查询
	rs = dbOp.executeQuery(query);
	
	if (rs == null) {
		release(dbOp);
		return pcib;
	}
	
	try {
		if (rs.next()) {
			pcib = new ProductCodeInfoBean();
			pcib.setId(rs.getInt("id"));
			pcib.setCode(rs.getString("code"));
			pcib.setName(rs.getString("name"));
		} else {
		}
	
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		release(dbOp);
	}
	return pcib;
	}
	
	// 包裹退货管理
	public boolean addReturnedPackage(ReturnedPackageBean bean) {
		return addXXX(bean, "returned_package");
	}

	public List getReturnedPackageList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "returned_package", "mmb.stock.stat.ReturnedPackageBean");
	}

	public int getReturnedPackageCount(String condition) {
		return getXXXCount(condition, "returned_package", "id");
	}

	public ReturnedPackageBean getReturnedPackage(String condition) {
		return (ReturnedPackageBean) getXXX(condition, "returned_package",
		"mmb.stock.stat.ReturnedPackageBean");
	}

	public boolean updateReturnedPackage(String set, String condition) {
		return updateXXX(set, condition, "returned_package");
	}

	public boolean deleteReturnedPackage(String condition) {
		return deleteXXX(condition, "returned_package");
	}
	

	/*public boolean appraisalReturnedProductResult(String productCode,
			int number, int appraisalResult) throws Exception {
		boolean result = false;
		ReturnedProductBean rpb = getReturnedProductBean("product_code = "
				+ productCode + " and type = 0 and count > 0");
		// 是否还有未质检数量
		if (rpb != null) {
			// 是否有足够要修改的数量
			ReturnedProductBean rpb1 = getReturnedProductBean("product_code = "
				+ productCode + " and type = " + appraisalResult);
			if (number > 0 && (rpb.getCount() - number) >= 0) {
				int remainUnappraisal = rpb.getCount() - number;
				int totalAppraisalResultCount = rpb1.getCount() + number;
				result = updateReturnedProduct("count = " + remainUnappraisal,
						"product_code = " + productCode + " and type = 0")
						&& updateReturnedProduct("count = " + totalAppraisalResultCount,
								"product_code = " + productCode
										+ " and type = " + appraisalResult);
			} else {
				throw new Exception("商品未入退货库，或已全部质检完！");
			}
		} else {
			throw new Exception("商品未入退货库，或已全部质检完！");
		}
		return result;
	}
	
	public boolean appraisalUnqualifyToQualify(String productCode,
			int number, int appraisalResult) throws Exception {
		boolean result = false;
		ReturnedProductBean rpb = getReturnedProductBean("product_code = "
				+ productCode + " and type = 2 and count > 0");
		// 是否还有未质检数量
		if (rpb != null) {
			// 是否有足够要修改的数量
			ReturnedProductBean rpb1 = getReturnedProductBean("product_code = "
				+ productCode + " and type = " + appraisalResult);
			if (number > 0 && (rpb.getCount() - number) >= 0) {
				int remainUnappraisal = rpb.getCount() - number;
				int totalAppraisalResultCount = rpb1.getCount() + number;
				result = updateReturnedProduct("count = " + remainUnappraisal,
						"product_code = " + productCode + " and type = 2")
						&& updateReturnedProduct("count = " + totalAppraisalResultCount,
								"product_code = " + productCode
										+ " and type = " + appraisalResult);
			} else {
				throw new Exception("商品未入退货库，或已全部质检完！");
			}
		} else {
			throw new Exception("商品未入退货库，或已全部质检完！");
		}
		return result;
	}*/

	public CargoInfoBean getCargoInfoByProductCode(String productCode)
			throws Exception {
		ProductCodeInfoBean pcb = getProductExchangeByCode(productCode);
		CargoInfoBean ci = new CargoInfoBean();
		if (pcb != null) {
			DbOperation dbOp = getDbOp();
			if (!dbOp.init()) {
				return ci;
			}
			ResultSet rs = null;
			String sql = "select cps.product_id, ci.* from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = 3"
					+ " and ci.stock_type = 0 and ci.store_type in (0,4) and cps.product_id = "
					+ pcb.getId() + " order by cps.stock_count desc limit 1";
			rs = dbOp.executeQuery(sql);
			try {
				if (rs.next()) {
					ci = new CargoInfoBean();
					//赋值
					ci.setId(rs.getInt("ci.id"));
					ci.setCode(rs.getString("ci.code"));
					ci.setWholeCode(rs.getString("ci.whole_code"));
					ci.setStoreType(rs.getInt("ci.store_type"));
					ci.setWarnStockCount(rs.getInt("ci.warn_stock_count"));
					ci.setMaxStockCount(rs.getInt("ci.max_stock_count"));
					ci.setSpaceLockCount(rs.getInt("ci.space_lock_count"));
					ci.setProductLineId(rs.getInt("ci.product_line_id"));
					ci.setType(rs.getInt("ci.type"));
					ci.setLength(rs.getInt("ci.length"));
					ci.setWidth(rs.getInt("ci.width"));
					ci.setHigh(rs.getInt("ci.high"));
					ci.setFloorNum(rs.getInt("ci.floor_num"));
					ci.setStatus(rs.getInt("ci.status"));
					ci.setStockType(rs.getInt("ci.stock_type"));
					ci.setShelfId(rs.getInt("ci.shelf_id"));
					ci.setStockAreaId(rs.getInt("ci.stock_area_id"));
					ci.setStorageId(rs.getInt("ci.storage_id"));
					ci.setAreaId(rs.getInt("ci.area_id"));
					ci.setCityId(rs.getInt("ci.city_id"));
					ci.setRemark(rs.getString("ci.remark"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new Exception("在得到对应的货架号时发生了错误!");
			} finally {
				release(dbOp);
			}

		} else {
			throw new Exception("对应的产品code：" + productCode + "没有找到对应的商品信息!");
		}
		return ci;
	}
	
	//退货已下架商品列表
	
	/*public boolean dealReturnedSaleOutProduct( String productCode, int appraisalResult, int number )throws Exception {
		ReturnedProductBean rpb = null;
		ReturnedProductBean bean = getReturnedProductBean("product_code = "
				+ productCode + " and type = 0 and count > 0");
		if( bean != null ) {
			int finalUnappraisalCount = bean.getCount() - number;
				//退货库中的未质检量是否够
				if( number > 0 && finalUnappraisalCount >= 0 ) {
					rpb = getReturnedSaleOutProductBean("product_id = "
							+ bean.getProductId() + " and type = " + appraisalResult);
						if (rpb == null) {
							ReturnedProductBean rpb0 = new ReturnedProductBean();
							rpb0.setProductCode(bean.getProductCode());
							rpb0.setProductId(bean.getProductId());
							rpb0.setProductName(bean.getProductName());
							rpb0.setType(1);
							ReturnedProductBean rpb1 = new ReturnedProductBean();
							rpb1.setProductCode(bean.getProductCode());
							rpb1.setProductId(bean.getProductId());
							rpb1.setProductName(bean.getProductName());
							rpb1.setType(2);
							if( appraisalResult == 1 ) {
								rpb0.setCount(number);
								rpb1.setCount(0);
							} else {
								rpb1.setCount(number);
								rpb0.setCount(0);
							}
							boolean result = addReturnedSaleOutProduct(rpb0) && addReturnedSaleOutProduct(rpb1) && updateReturnedProduct("count = " + finalUnappraisalCount,
						"product_code = " + productCode + " and type = 0");
							return result;
						} else {
							int count = rpb.getCount() + number;
							return updateReturnedSaleOutProduct("count = " + count, "product_id = "
									+ rpb.getProductId() + " and type = " + appraisalResult) && updateReturnedProduct("count = " + finalUnappraisalCount,
											"product_code = " + productCode + " and type = 0") ;
						}
				} else {
					throw new Exception ("商品未入退货库，或已全部质检完！");
				}
				
		} else {
			throw new Exception ("商品未入退货库，或已全部质检完！");
		}
	}*/
	
	public String getReturnedProductNoCargoMatchedIds(String availAreaIds, String availCargoAreaIds, int wareArea, int cargoWareArea) {
		
		String result = "";
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		ResultSet rs = null;
		String sql = null; 
		if( wareArea == -1 ) {
			sql = "select distinct(cps.product_id) from cargo_info ci, cargo_product_stock cps, product p, product_stock ps where ci.id = cps.cargo_id and cps.product_id = ps.product_id and ps.product_id = p.id and ps.stock > 0 and  p.status <> 100 and ps.area in (" + availAreaIds + ") and ps.type = 4 and ci.area_id in (" + availCargoAreaIds + ") and ci.stock_type = 0 and ci.store_type in (0,4)";
		} else {
			sql = "select distinct(cps.product_id) from cargo_info ci, cargo_product_stock cps, product p, product_stock ps where ci.id = cps.cargo_id and cps.product_id = ps.product_id and ps.product_id = p.id and ps.stock > 0 and  p.status <> 100 and ps.area = " + wareArea + " and ps.type = 4 and ci.area_id = " + cargoWareArea + " and ci.stock_type = 0 and ci.store_type in (0,4)";
		}
		
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("cps.product_id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	
	public String getReturnedProductNoOffLine(String availAreaIds, int wareArea) {
		String result = "";
		
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		ResultSet rs = null;
		String sql = null;
		if( wareArea == -1 ) {
			sql = "select distinct(p.id) from product_stock ps, product p where ps.stock > 0 and  p.status <> 100 and ps.area in (" + availAreaIds + ") and ps.type = 4 and p.id = ps.product_id";
		} else {
			sql = "select distinct(p.id) from product_stock ps, product p where ps.stock > 0 and  p.status <> 100 and ps.area = " + wareArea + " and ps.type = 4 and p.id = ps.product_id";
		}
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		
		return result;
		
	}
	
	public String getReturnedSaleOutProductIds(String availAreaIds, int wareArea) throws Exception {
		String result = "";
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		ResultSet rs = null;
		String sql = null;
		if( wareArea == -1 ) {
			sql = "select ps.id from product_stock ps, product p where (ps.stock > 0 or ps.lock_count > 0) and  p.status = 100 and ps.area in("+availAreaIds+") and ps.type = 4 and p.id = ps.product_id";
		} else {
			sql = "select ps.id from product_stock ps, product p where (ps.stock > 0 or ps.lock_count > 0) and  p.status = 100 and ps.area ="+wareArea+" and ps.type = 4 and p.id = ps.product_id";
		}
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new Exception("在得到下架商品id时发生了错误");
		} finally {
			release(dbOp);
		}
		
		return result;
		
	}
	
	/*public List getReturnedSaleOutProductList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "returned_saleout_product", "mmb.stock.stat.ReturnedProductBean");
	}
	
	public boolean addReturnedSaleOutProduct(ReturnedProductBean bean) {
		return addXXX(bean, "returned_saleout_product");
	}
	
	public int getReturnedSaleOutProductCount(String condition) {
		return getXXXCount(condition, "returned_saleout_product", "id");
	}

	public ReturnedProductBean getReturnedSaleOutProductBean ( String condition ) {
		return (ReturnedProductBean) getXXX(condition, "returned_saleout_product",
		"mmb.stock.stat.ReturnedProductBean");
	}
	
	public boolean updateReturnedSaleOutProduct(String set, String condition) {
		return updateXXX(set, condition, "returned_saleout_product");
	}*/
	
	//不合格品接收明细表的操作
	
	public boolean addCheckStockinUnqualified(CheckStockinUnqualifiedBean bean) {
		return addXXX(bean, "check_stockin_unqualified");
	}

	public List getCheckStockinUnqualifiedList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "check_stockin_unqualified", "mmb.stock.stat.CheckStockinUnqualifiedBean");
	}
	
	public int getCheckStockinUnqualifiedCount(String condition) {
		return getXXXCount(condition, "check_stockin_unqualified", "id");
	}

	public CheckStockinUnqualifiedBean getCheckStockinUnqualified(String condition) {
		return (CheckStockinUnqualifiedBean) getXXX(condition, "check_stockin_unqualified",
		"mmb.stock.stat.CheckStockinUnqualifiedBean");
	}

	public boolean updateCheckStockinUnqualified(String set, String condition) {
		return updateXXX(set, condition, "check_stockin_unqualified");
	}

	public boolean deleteCheckStockinUnqualified(String condition) {
		return deleteXXX(condition, "check_stockin_unqualified");
	}
	
	//质检入库任务明细
	
	public boolean addCheckStockinMissionDetail(CheckStockinMissionDetailBean bean) {
		return addXXX(bean, "check_stockin_mission_detail");
	}

	public List getCheckStockinMissionDetailList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "check_stockin_mission_detail", "mmb.stock.stat.CheckStockinMissionDetailBean");
	}
	
	public int getCheckStockinMissionDetailCount(String condition) {
		return getXXXCount(condition, "check_stockin_mission_detail", "id");
	}

	public CheckStockinMissionDetailBean getCheckStockinMissionDetail(String condition) {
		return (CheckStockinMissionDetailBean) getXXX(condition, "check_stockin_mission_detail",
		"mmb.stock.stat.CheckStockinMissionDetailBean");
	}

	public boolean updateCheckStockinMissionDetail(String set, String condition) {
		return updateXXX(set, condition, "check_stockin_mission_detail");
	}

	public boolean deleteCheckStockinMissionDetail(String condition) {
		return deleteXXX(condition, "check_stockin_mission_detail");
	}
	
	//质检入库任务批次
	
	public boolean addCheckStockinMissionBatch(CheckStockinMissionBatchBean bean) {
		return addXXX(bean, "check_stockin_mission_batch");
	}

	public List getCheckStockinMissionBatchList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "check_stockin_mission_batch", "mmb.stock.stat.CheckStockinMissionBatchBean");
	}
	
	public int getCheckStockinMissionBatchCount(String condition) {
		return getXXXCount(condition, "check_stockin_mission_batch", "id");
	}

	public CheckStockinMissionBatchBean getCheckStockinMissionBatch(String condition) {
		return (CheckStockinMissionBatchBean) getXXX(condition, "check_stockin_mission_batch",
		"mmb.stock.stat.CheckStockinMissionBatchBean");
	}

	public boolean updateCheckStockinMissionBatch(String set, String condition) {
		return updateXXX(set, condition, "check_stockin_mission_batch");
	}

	public boolean deleteCheckStockinMissionBatch(String condition) {
		return deleteXXX(condition, "check_stockin_mission_batch");
	}
	
	//质检入库任务单
	
	public boolean addCheckStockinMission(CheckStockinMissionBean bean) {
		return addXXX(bean, "check_stockin_mission");
	}

	public List getCheckStockinMissionList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "check_stockin_mission", "mmb.stock.stat.CheckStockinMissionBean");
	}
	
	public int getCheckStockinMissionCount(String condition) {
		return getXXXCount(condition, "check_stockin_mission", "id");
	}

	public CheckStockinMissionBean getCheckStockinMission(String condition) {
		return (CheckStockinMissionBean) getXXX(condition, "check_stockin_mission",
		"mmb.stock.stat.CheckStockinMissionBean");
	}

	public boolean updateCheckStockinMission(String set, String condition) {
		return updateXXX(set, condition, "check_stockin_mission");
	}

	public boolean deleteCheckStockinMission(String condition) {
		return deleteXXX(condition, "check_stockin_mission");
	}
	
	//质检分类
	
	public boolean addCheckEffect(CheckEffectBean bean) {
		return addXXX(bean, "check_effect");
	}

	public List getCheckEffectList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "check_effect", "mmb.stock.stat.CheckEffectBean");
	}
	
	public int getCheckEffectCount(String condition) {
		return getXXXCount(condition, "check_effect", "id");
	}

	public CheckEffectBean getCheckEffect(String condition) {
		return (CheckEffectBean) getXXX(condition, "check_effect",
		"mmb.stock.stat.CheckEffectBean");
	}

	public boolean updateCheckEffect(String set, String condition) {
		return updateXXX(set, condition, "check_effect");
	}

	public boolean deleteCheckEffect(String condition) {
		return deleteXXX(condition, "check_effect");
	}
	
	//商品物流属性
	
	public boolean addProductWareProperty(ProductWarePropertyBean bean) {
		return addXXX(bean, "product_ware_property");
	}

	public List getProductWarePropertyList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_ware_property", "mmb.stock.stat.ProductWarePropertyBean");
	}
	
	public int getProductWarePropertyCount(String condition) {
		return getXXXCount(condition, "product_ware_property", "id");
	}

	public ProductWarePropertyBean getProductWareProperty(String condition) {
		return (ProductWarePropertyBean) getXXX(condition, "product_ware_property",
		"mmb.stock.stat.ProductWarePropertyBean");
	}

	public boolean updateProductWareProperty(String set, String condition) {
		return updateXXX(set, condition, "product_ware_property");
	}

	public boolean deleteProductWareProperty(String condition) {
		return deleteXXX(condition, "product_ware_property");
	}
	
	//商品物流分类
	public List getUserOrderPackageTypeList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "user_order_package_type", "adultadmin.bean.order.UserOrderPackageTypeBean");
	}
	
	
	//员工排班
	
	public boolean addCheckStaffBean(CheckStaffBean bean) {
		return addXXX(bean, "check_staff_count");
	}

	public List getCheckStaffList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "check_staff_count", "mmb.stock.stat.CheckStaffBean");
	}
	
	public int getCheckStaffCount(String condition) {
		return getXXXCount(condition, "check_staff_count", "id");
	}

	public CheckStaffBean getCheckStaff(String condition) {
		return (CheckStaffBean) getXXX(condition, "check_staff_count",
		"mmb.stock.stat.CheckStaffBean");
	}

	public boolean updateCheckStaff(String set, String condition) {
		return updateXXX(set, condition, "check_staff_count");
	}

	public boolean deleteCheckStaff(String condition) {
		return deleteXXX(condition, "check_staff_count");
	}
	
	//退货商品货位
	
	public boolean addReturnedProductCargo(ReturnedProductCargoBean bean) {
		return addXXX(bean, "returned_product_cargo");
	}

	public List getReturnedProductCargoList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "returned_product_cargo", "mmb.stock.stat.ReturnedProductCargoBean");
	}
	
	public int getReturnedProductCargoCount(String condition) {
		return getXXXCount(condition, "returned_product_cargo", "id");
	}

	public ReturnedProductCargoBean getReturnedProductCargo(String condition) {
		return (ReturnedProductCargoBean) getXXX(condition, "returned_product_cargo",
		"mmb.stock.stat.ReturnedProductCargoBean");
	}

	public boolean updateReturnedProductCargo(String set, String condition) {
		return updateXXX(set, condition, "returned_product_cargo");
	}

	public boolean deleteReturnedProductCargo(String condition) {
		return deleteXXX(condition, "returned_product_cargo");
	}
	
	
	//退货合格统计单
	public boolean addReturnedUpShelf(ReturnedUpShelfBean bean) {
		return addXXX(bean, "returned_up_shelf");
	}

	public List getReturnedUpShelfList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "returned_up_shelf", "mmb.stock.stat.ReturnedUpShelfBean");
	}
	
	public int getReturnedUpShelfCount(String condition) {
		return getXXXCount(condition, "returned_up_shelf", "id");
	}

	public ReturnedUpShelfBean getReturnedUpShelf(String condition) {
		return (ReturnedUpShelfBean) getXXX(condition, "returned_up_shelf",
		"mmb.stock.stat.ReturnedUpShelfBean");
	}

	public boolean updateReturnedUpShelf(String set, String condition) {
		return updateXXX(set, condition, "returned_up_shelf");
	}

	public boolean deleteReturnedUpShelf(String condition) {
		return deleteXXX(condition, "returned_up_shelf");
	}
	
	
	public boolean checkIsCargoFull( CargoInfoBean cib ) {
		 
		WareService wareService = new WareService(this.dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		List cargoProductStockList = cargoService.getCargoProductStockList("cargo_id = " + cib.getId(), -1, -1, "stock_count desc");
		int totalCount = 0;
		for( int i = 0; i < cargoProductStockList.size(); i ++ ) {
			CargoProductStockBean cpsb = (CargoProductStockBean) cargoProductStockList.get(i);
			totalCount += cpsb.getStockCount() + cpsb.getStockLockCount();
		}
		if( totalCount >= cib.getMaxStockCount() ) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getProductIds ( String condition ) {
		String result = "";
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		ResultSet rs = null;
		String sql = "select id from product where " + condition;
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		
		return result;
		
	}
	
	public HSSFWorkbook returnedProductNoCargoMatchedWebWork2(List list) {
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 在Excel 工作簿中建一工作表
		HSSFSheet sheet = workbook.createSheet("无散件区货位退货库商品列表");
		sheet.setColumnWidth(0, 5 * 256);
		sheet.setColumnWidth(1, 11 * 256);
		sheet.setColumnWidth(2, 25 * 256);
		sheet.setColumnWidth(3, 11 * 256);
		sheet.setColumnWidth(4, 11 * 256);
		sheet.setColumnWidth(5, 11 * 256);
		
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
		HSSFCell cell6 = row.createCell(5);
		// 定义单元格为字符串类型
		cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell5.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell6.setCellType(HSSFCell.CELL_TYPE_STRING);

		// 在单元格中输入数据
		cell1.setCellValue("序号");
		cell2.setCellValue("产品编号");
		cell3.setCellValue("产品名称");
		cell4.setCellValue("库存");
		cell5.setCellValue("冻结量");
		cell6.setCellValue("库地区");
		// cell11.setCellValue("查看详情");
		if( list.size() > 0 ) {
			for (int i = 0; i < list.size(); i++) {
				ProductStockBean rpb = (ProductStockBean) list.get(i);
				row = sheet.createRow((short) i + 1);
				// 1 序号 String
				HSSFCell cellc1 = row.createCell(0);
				cellc1.setCellStyle(cellStyle);
				cellc1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				// cell.setEncoding();
				cellc1.setCellValue(i + 1);
				// 2产品编号id String
				HSSFCell cellc2 = row.createCell(1);
				cellc2.setCellStyle(cellStyle);
				cellc2.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc2.setCellValue(rpb.getProduct().getCode());
				// 3 产品名称String
				HSSFCell cellc3 = row.createCell(2);
				cellc3.setCellStyle(cellStyle);
				cellc3.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc3.setCellValue(rpb.getProduct().getName());
				// 4 货位库存nt
				HSSFCell cellc4 = row.createCell(3);
				cellc4.setCellStyle(cellStyle);
				cellc4.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				// cell.setEncoding();
				cellc4.setCellValue(rpb.getStock());
				// 5 质检状态double
				HSSFCell cellc5 = row.createCell(4);
				cellc5.setCellStyle(cellStyle);
				cellc5.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc5.setCellValue(rpb.getLockCount());
				
				HSSFCell cellc6 = row.createCell(5);
				cellc6.setCellStyle(cellStyle);
				cellc6.setCellType(HSSFCell.CELL_TYPE_STRING);
				// cell.setEncoding();
				cellc6.setCellValue(rpb.getAreaName(rpb.getArea()));
			}
		}
		return workbook;
	}
	
	/*public String getNeedChangeCargoIds() {
		String result = "";
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		ResultSet rs = null;
		String sql = "select rpc.id from returned_product_cargo rpc LEFT JOIN cargo_product_stock cps on (rpc.product_id = cps.product_id and rpc.cargo_id = cps.cargo_id) where cps.id is NULL;";
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("rpc.id") + ",";
			}
		} catch (SQLException e) {
			
		} finally {
			release(dbOp);
		}
		
		return result;
	}*/
	
	public String getSearchIds(String sql) {
		String result = "";
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		ResultSet rs = null;
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("rpc.id") + ",";
			}
		} catch (SQLException e) {
		
		} finally {
			release(dbOp);
		}
		
		return result;
	}
	
	public List getSearchBeans(String condition) {
		List list = new ArrayList();
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return list;
		}
		ResultSet rs = null;
		String sql = "select rpc.* from returned_product_cargo rpc, cargo_info ci, product p where rpc.cargo_id = ci.id and rpc.product_id = p.id";
		if( !condition.equals("")) {
			sql += condition;
		}
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				ReturnedProductCargoBean rpcb = new ReturnedProductCargoBean();
				rpcb.setCargoId(rs.getInt("rpc.cargo_id"));
				rpcb.setCount(rs.getInt("rpc.count"));
				rpcb.setId(rs.getInt("rpc.id"));
				rpcb.setProductId(rs.getInt("rpc.product_id"));
				list.add(rpcb);
			}
		} catch (SQLException e) {
			
		} finally {
			release(dbOp);
		}
		
		return list;
	}
	
	public List getWholeAreaReturnedProductCargoList(String condition) {
		List list = new ArrayList();
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return list;
		}
		ResultSet rs = null;
		String sql = "select rpc.* from returned_product_cargo rpc, cargo_info ci where " + condition;
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				ReturnedProductCargoBean rpcb = new ReturnedProductCargoBean();
				rpcb.setCargoId(rs.getInt("rpc.cargo_id"));
				rpcb.setCount(rs.getInt("rpc.count"));
				rpcb.setProductId(rs.getInt("rpc.product_id"));
				rpcb.setId(rs.getInt("rpc.id"));
				list.add(rpcb);
			}
		} catch (SQLException e) {
			
		} finally {
			release(dbOp);
		}
		
		return list;
	}
	
	/**
	 * 验证标准装箱量
	 * @param productId
	 * @return
	 */
	public int valBinning(String productId){
		DbOperation dbop = getDbOp();
		
		ResultSet rs = null;
		String sql = "SELECT pwp.cartonning_standard_count FROM product_ware_property pwp  WHERE pwp.product_id="+productId;
		rs = dbop.executeQuery(sql);
		try {
			if(rs!=null && rs.next()){
				int temp = rs.getInt(1);
				rs.close();
				return temp;				
			}else{
				return -1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			release(dbop);
		}
		return -1;
	}
	
	/**
	 * 根据商品编号查询物流属性
	 * @param code
	 * @return
	 */
	public String getPwpIdByProductCode(String code){
		DbOperation dbop = getDbOp();
		
		ResultSet rs = null;
		String sql = "SELECT pwp.id FROM product_ware_property pwp  WHERE pwp.product_id="+code;
		rs = dbop.executeQuery(sql);
		try {
			if(rs!=null && rs.next()){
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		//return true;
		return "";
	}
	
	/**
	 * 根据商品条形码查询商品编号
	 * @param code
	 * @return
	 */
	public String getProductIdByProductBarcode(String code){
		DbOperation dbop = getDbOp();
		
		ResultSet rs = null;
		String sql = "select pb.product_id from product_barcode pb where pb.barcode='"+code+"'";
		rs = dbop.executeQuery(sql);
		try {
			if(rs!=null && rs.next()){
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return "";
	}
	
	/**
	 * 根据商品code查询商品编号
	 * @param code
	 * @return
	 */
	public String getProductIdbyProductCode(String code){
		DbOperation dbop = getDbOp();
		
		ResultSet rs = null;
		String sql = "select p.id from product p where p.code='"+code+"'";
		rs = dbop.executeQuery(sql);
		try {
			if(rs!=null && rs.next()){
				return rs.getString(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return "";
	}
	
	public voProduct getProduct(int id) {
		
		
		voProduct vo = null;
		DbOperation dbop = getDbOp();
		
		ResultSet rs = null;
		String sql = "select *, (select group_concat(supplier_name) from product_supplier where product_supplier.product_id=product.id) proxy_name, (select name from product_status where product_status.id=product.status) product_status_name from product where id="+id;
		try {
			

			rs = dbop.executeQuery(sql);
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setCreateTime(rs.getString("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setPrice(rs.getFloat("price"));
				vo.setPrice2(rs.getFloat("price2"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setPrice4(rs.getFloat("price4"));
				vo.setPrice5(rs.getFloat("price5"));
				vo.setDeputizePrice(rs.getFloat("deputize_price"));
				vo.setGroupBuyPrice(rs.getFloat("group_buy_price"));
				vo.setPic(rs.getString("pic"));
				vo.setPic2(rs.getString("pic2"));
				vo.setPic3(rs.getString("pic3"));
				vo.setIntro(rs.getString("intro"));
				vo.setIntro2(rs.getString("intro2"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id2"));
				vo.setParentId3(rs.getInt("parent_id3"));
				vo.setFlag(rs.getInt("flag"));
				vo.setCommentCount(rs.getInt("comment_count"));
				vo.setClickCount(rs.getInt("click_count"));
				vo.setBuyCount(rs.getInt("buy_count"));
				vo.setProxyId(rs.getInt("proxy_id"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setProxyName(rs.getString("proxy_name"));
				vo.setProxysName(rs.getString("proxys_name"));
				vo.setRemark(rs.getString("remark"));
				vo.setUnit(rs.getString("unit"));
				vo.setStock(rs.getInt("stock"));
				vo.setStockGd(rs.getInt("stock_gd"));
				vo.setImages(rs.getString("images"));
				vo.setGuanggao(rs.getString("guanggao"));
				vo.setGongxiao(rs.getString("gongxiao"));
				vo.setShiyongrenqun(rs.getString("shiyongrenqun"));
				vo.setShiyongfangfa(rs.getString("shiyongfangfa"));
				vo.setZhuyishixiang(rs.getString("zhuyishixiang"));
				vo.setTebietishi(rs.getString("tebietishi"));
				vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
				vo.setBaozhuangdaxiao(rs.getString("baozhuangdaxiao"));
				vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
				vo.setChanpinchicun(rs.getString("chanpinchicun"));
				vo.setChanpinchengfen(rs.getString("chanpinchengfen"));
				vo.setChangshang(rs.getString("changshang"));
				vo.setBaozhiqi(rs.getString("baozhiqi"));
				vo.setChucangfangfa(rs.getString("chucangfangfa"));
				vo.setPizhunwenhao(rs.getString("pizhunwenhao"));
				vo.setChangshangjieshao(rs.getString("changshangjieshao"));
				vo.setFuwuchengnuo(rs.getString("fuwuchengnuo"));
				vo.setRank(rs.getInt("rank"));
				vo.setDisplayOrder(rs.getInt("display_order"));
				vo.setTopOrder(rs.getInt("top_order"));
				vo.setStockLineBj(rs.getInt("stock_line_bj"));
				vo.setStockLineGd(rs.getInt("stock_line_gd"));
				vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
				vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
				vo.setStockStatus(rs.getInt("stock_status"));
				vo.setIsPackage(rs.getInt("is_package"));
				vo.setHasPresent(rs.getInt("has_present"));
				vo.setBrand(rs.getInt("brand"));
				vo.setShowPackage(rs.getInt("show_package"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setStockDayBj(rs.getInt("stock_day_bj"));
				vo.setStockDayGd(rs.getInt("stock_day_gd"));
				vo.setStockBjBad(rs.getInt("stock_bj_bad"));
				vo.setStockBjRepair(rs.getInt("stock_bj_repair"));
				vo.setStockGdBad(rs.getInt("stock_gd_bad"));
				vo.setStockGdRepair(rs.getInt("stock_gd_repair"));
				vo.setStatusName(rs.getString("product_status_name"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			release(dbop);
		}
		return vo;
	}
	
	/**
	 * 验证商品是否属于任务单
	 * @param productCode
	 * @param csmCode
	 * @return
	 */
	public boolean validProductByCode(String productCode,String csmCode){
		DbOperation dbop = getDbOp();
		ResultSet rs = null;
		
		String sql = "SELECT count(bsp.id) FROM buy_stock_product bsp JOIN buy_stock bs ON bsp.buy_stock_id = bs.id JOIN check_stockin_mission csm ON csm.buy_stockin_code=bs.code WHERE csm.buy_stockin_code='"+csmCode+"' AND bsp.product_id='"+productCode+"'";
		rs = dbop.executeQuery(sql);
		boolean flag = true;
		try {
			if(rs.next()){
				int temp = rs.getInt(1);
				if(temp==0)
					flag = false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		
		return flag;
	}
	
	/**
	 * 查询批次
	 * @param csmCode
	 * @return
	 */
	public int getCheckStockinMissionBatchInfo(String csmCode){
		DbOperation dbop = getDbOp();
		
		ResultSet rs = null;
		String sql = "SELECT csmb.id FROM check_stockin_mission_batch csmb JOIN check_stockin_mission csm ON csmb.mission_id=csm.id WHERE csm.buy_stockin_code='"+csmCode+"' ";
		rs = dbop.executeQuery(sql);
		//boolean flag = true;
		try {
			if(rs.next()){
				int temp = rs.getInt(1);
				return temp;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return -1;
		
	}
	
	/**
	 * 查询标准装箱量
	 * @param messionId
	 * @return
	 */
	public int queryBinning(String messionId){
		DbOperation dbop = getDbOp();
		
		ResultSet rs = null;
		String sql = "SELECT pwp.cartonning_standard_count FROM product_ware_property pwp JOIN check_stockin_mission_batch csmb on pwp.product_id=csmb.product_id WHERE csmb.mission_id='"+messionId+"' ";
		rs = dbop.executeQuery(sql);
		//boolean flag = true;
		try {
			if(rs.next()){
				int temp = rs.getInt(1);
				return temp;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return -1;
	}
	
	
	/**
	 * 查询采购单
	 * @param code
	 * @return
	 */
	public BuyStockBean getBuyStockBeanByCode(String code){
		BuyStockBean bs = null;
		DbOperation dbop = getDbOp();
		ResultSet rs = null;
		String sql = "SELECT bs.buy_order_id,bs.supplier_id,bs.id,bs.area,bs.code,bs.status FROM buy_stock bs WHERE bs.code='"+code+"'";
		rs = dbop.executeQuery(sql);
		try {
			if(rs.next()){
				bs = new BuyStockBean();
				bs.setId(rs.getInt(3));
				bs.setSupplierId(rs.getInt(2));
				bs.setBuyOrderId(rs.getInt(1));
				bs.setArea(rs.getInt(4));
				bs.setCode(rs.getString(5));
				bs.setStatus(rs.getInt(6));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return bs;
	}
	
	/**
	 * 验证是否已完成
	 * @param missionId
	 * @return
	 */
	public String validComplete(int missionId){
		boolean flag = false;
		DbOperation dbop = getDbOp();		
		ResultSet rs = null;
		String sql = "SELECT t.buy_count,t.current_check_count FROM check_stockin_mission_batch t WHERE t.mission_id="+missionId;
		rs = dbop.executeQuery(sql);
		try{
		if(rs.next()){
			int buycount = rs.getInt(1);
			int totalcount = rs.getInt(2);
			if(totalcount==buycount)//刚好=定购量
				return "1";
			else if(totalcount>buycount)//大于定购量
				return "2";
			else//小于定购量
				return "0";
		}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return "1";
	}
	
	//已入库数量
	public int getTotalCount(String condition){
		DbOperation dbop = getDbOp();		
		ResultSet rs = null;
		String sql = "SELECT SUM(t.stockin_count) FROM buy_stockin_product t WHERE t.buy_stockin_id in"+condition;
		rs = dbop.executeQuery(sql);
		try{
		if(rs.next()){
			int buycount = rs.getInt(1);
			return buycount;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return 0;
	}
	
	/**
	 * 取得buystockid
	 * @param id
	 * @return
	 */
	public String getBuyStockId(int id){
		DbOperation dbop = getDbOp();		
		ResultSet rs = null;
		String sql = "SELECT t.id FROM buy_stockin t WHERE t.buy_stock_id="+id;
		rs = dbop.executeQuery(sql);
		StringBuffer sb = new StringBuffer();
		sb.append(" (-100,");
		try{
		while(rs.next()){
			int buycount = rs.getInt(1);
			sb.append(buycount+",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return sb.toString();
	}
	
	/**
	 * 入库明细
	 * @param strMissionId
	 * @return
	 */
	public Map queryPackingDetailInfo(String strMissionId) {
		
		if(strMissionId == null){
			throw new RuntimeException("missionId cant be null");
		}
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		CartonningInfoService cartService = ServiceFactory.createCartonningInfoService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StringBuilder condition = new StringBuilder();
		condition.append("mission_id=");
		condition.append(Integer.parseInt(strMissionId));
		List packingDetailList = statService.getCheckStockinMissionDetailList(
				condition.toString(), 0, -1, null);
		Map resultMap = new HashMap();
		if(packingDetailList == null || packingDetailList.isEmpty()){
			return resultMap;
		}
		CheckStockinMissionDetailBean cmcBean = null;
		BuyStockinBean stockinBean = null;
		List cartonningList = null;
		List cartonningList1 = null;
		String cartonningName = null;
		int temp = 0;
		for(int i=0; i<packingDetailList.size(); i++){
			List tempList = new ArrayList();
			cmcBean = (CheckStockinMissionDetailBean) packingDetailList.get(i);
			if(cmcBean.getBuyStockinCreateDateTime()!=null && !cmcBean.getBuyStockinCreateDateTime().equals("")){
				cmcBean.setBuyStockinCreateDateTime(cmcBean.getBuyStockinCreateDateTime().substring(0,19));
			}
			stockinBean = stockService.getBuyStockin("id="+cmcBean.getBuyStockinId());
			if(stockinBean==null){
				continue;
			}
			cartonningList = cartService.getCartonningList("buy_stockin_id="+stockinBean.getId()+" and create_time='"+cmcBean.getBuyStockinCreateDateTime()+"'", -1, -1, null);
			if(cartonningList!=null&&cartonningList.size()>1){//同时生成的两个及以上装箱单
				if(temp==0){
					cartonningList1 = new ArrayList();
					cartonningList1.add(cartonningList.get(temp));
					temp ++;
				}else{
					cartonningList1 = new ArrayList();
					cartonningList1.add(cartonningList.get(temp));
					if(temp==cartonningList.size()-1)
						temp = 0;
					else
						temp ++;
					
				}
			}else{
				cartonningList1 = cartonningList;
			}
			cartonningName = getCartonningName(cartonningList1);//经过修改后，装箱单只能关联一张
			if(cartonningList1!=null&&cartonningList1.size()>0){
				CartonningInfoBean cb = (CartonningInfoBean)cartonningList1.get(0);
				cmcBean.setId(cb.getId());
			}else{
				cmcBean.setId(-1);
			}
			cmcBean.setCartonningName(cartonningName);
			cmcBean.setBuyStockinStatus(stockinBean.getStatusName());
			if(resultMap.isEmpty()){
				tempList.add(cmcBean);
				resultMap.put(cmcBean.getProductCode(), tempList);
			}else{
				if(resultMap.get(cmcBean.getProductCode()) != null){
					((List)resultMap.get(cmcBean.getProductCode())).add(cmcBean);
				}else{
					tempList.add(cmcBean);
					resultMap.put(cmcBean.getProductCode(), tempList);
				}
			}
		}
		return resultMap;
	}

	
	
	    /**  
	     * 此方法描述的是：  构造装箱单号，以逗号分割
	     * @author: liubo  
	     * @version: 2013-1-24 下午04:19:01  
	     */  
	    
	private String getCartonningName(List cartonningList) {
		
		if(cartonningList == null || cartonningList.isEmpty()){
			return "";
		}
		CartonningInfoBean coBean = null;
		StringBuilder strBuilder = new StringBuilder();
		for(int i=0; i<cartonningList.size(); i++){
			coBean = (CartonningInfoBean) cartonningList.get(i);
			if(strBuilder.length()>0){
				strBuilder.append(",");
			}
			strBuilder.append(coBean.getCode());
		}
		return strBuilder.toString();
	}
}
