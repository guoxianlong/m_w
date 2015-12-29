package mmb.stock.stat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.Assert;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;
import adultadmin.action.bybs.ByBsAction;
import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.ReturnsReasonBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.MyException;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ClaimsVerificationService  extends BaseServiceImpl {
	
	public ClaimsVerificationService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ClaimsVerificationService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	//包裹核查信息
	public boolean addReturnPackageCheck(ReturnPackageCheckBean bean) {
		return addXXX(bean, "return_package_check");
	}

	public List getReturnPackageCheckList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "return_package_check", "mmb.stock.stat.ReturnPackageCheckBean");
	}
	
	public int getReturnPackageCheckCount(String condition) {
		return getXXXCount(condition, "return_package_check", "id");
	}

	public ReturnPackageCheckBean getReturnPackageCheck(String condition) {
		return (ReturnPackageCheckBean) getXXX(condition, "return_package_check",
		"mmb.stock.stat.ReturnPackageCheckBean");
	}

	public boolean updateReturnPackageCheck(String set, String condition) {
		return updateXXX(set, condition, "return_package_check");
	}

	public boolean deleteReturnPackageCheck(String condition) {
		return deleteXXX(condition, "return_package_check");
	}
	
	//包裹核查商品信息
	public boolean addReturnPackageCheckProduct(ReturnPackageCheckProductBean bean) {
		return addXXX(bean, "return_package_check_product");
	}

	public List getReturnPackageCheckProductList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "return_package_check_product", "mmb.stock.stat.ReturnPackageCheckProductBean");
	}
	
	public int getReturnPackageCheckProductCount(String condition) {
		return getXXXCount(condition, "return_package_check_product", "id");
	}

	public ReturnPackageCheckProductBean getReturnPackageCheckProduct(String condition) {
		return (ReturnPackageCheckProductBean) getXXX(condition, "return_package_check_product",
		"mmb.stock.stat.ReturnPackageCheckProductBean");
	}

	public boolean updateReturnPackageCheckProduct(String set, String condition) {
		return updateXXX(set, condition, "return_package_check_product");
	}

	public boolean deleteReturnPackageCheckProduct(String condition) {
		return deleteXXX(condition, "return_package_check_product");
	}
	
	//理赔核销单
	public boolean addClaimsVerification(ClaimsVerificationBean bean) {
		return addXXX(bean, "claims_verification");
	}

	public List getClaimsVerificationList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "claims_verification", "mmb.stock.stat.ClaimsVerificationBean");
	}
	
	public int getClaimsVerificationCount(String condition) {
		return getXXXCount(condition, "claims_verification", "id");
	}

	public ClaimsVerificationBean getClaimsVerification(String condition) {
		return (ClaimsVerificationBean) getXXX(condition, "claims_verification",
		"mmb.stock.stat.ClaimsVerificationBean");
	}

	public boolean updateClaimsVerification(String set, String condition) {
		return updateXXX(set, condition, "claims_verification");
	}

	public boolean deleteClaimsVerification(String condition) {
		return deleteXXX(condition, "claims_verification");
	}
	
	//理赔核销商品
	public boolean addClaimsVerificationProduct(ClaimsVerificationProductBean bean) {
		return addXXX(bean, "claims_verification_product");
	}

	public List getClaimsVerificationProductList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "claims_verification_product", "mmb.stock.stat.ClaimsVerificationProductBean");
	}
	
	public int getClaimsVerificationProductCount(String condition) {
		return getXXXCount(condition, "claims_verification_product", "id");
	}

	public ClaimsVerificationProductBean getClaimsVerificationProduct(String condition) {
		return (ClaimsVerificationProductBean) getXXX(condition, "claims_verification_product",
		"mmb.stock.stat.ClaimsVerificationProductBean");
	}

	public boolean updateClaimsVerificationProduct(String set, String condition) {
		return updateXXX(set, condition, "claims_verification_product");
	}

	public boolean deleteClaimsVerificationProduct(String condition) {
		return deleteXXX(condition, "claims_verification_product");
	}
	
	public int getClaimsVerificationCount2(String sqlCount) {
		int result = 0;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		
		rs = dbOp.executeQuery(sqlCount);
		try {
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	public List getClaimsVerificationList2(String sql, int index,
			int count, String orderBy) {
		List result = new ArrayList();
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		if( orderBy != null && !orderBy.equals("")) {
			sql += " order by " + orderBy;
		}
		sql = DbOperation.getPagingQuery(sql, index, count);
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				ClaimsVerificationBean cvBean = new ClaimsVerificationBean();
				cvBean.setId(rs.getInt("id"));
				cvBean.setCode(rs.getString("code"));
				cvBean.setOrderCode(rs.getString("order_code"));
				cvBean.setPackageCode(rs.getString("package_code"));
				cvBean.setCreateUserName(rs.getString("create_user_name"));
				cvBean.setCreateUserId(rs.getInt("create_user_id"));
				cvBean.setAuditUserName(rs.getString("audit_user_name"));
				cvBean.setAuditUserId(rs.getInt("audit_user_id"));
				cvBean.setConfirmUserName(rs.getString("confirm_user_name"));
				cvBean.setConfirmUserId(rs.getInt("confirm_user_id"));
				cvBean.setCreateTime(rs.getString("create_time"));
				cvBean.setAuditTime(rs.getString("audit_time"));
				cvBean.setConfirmTime(rs.getString("confirm_time"));
				cvBean.setDeliver(rs.getInt("deliver"));
				cvBean.setStatus(rs.getInt("status"));
				cvBean.setWareArea(rs.getInt("ware_area"));
				cvBean.setBsbyCodes(rs.getString("bsby_codes"));
				cvBean.setPrice(rs.getFloat("price"));
				cvBean.setType(rs.getInt("type"));
				cvBean.setHasGift(rs.getInt("has_gift"));
				result.add(cvBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	/**
	 * 检查库存可用量是否充足
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public boolean checkProductStock(int area, int stockType,
			voProduct product, int count, IProductStockService service) {
		
		ProductStockBean psb = service.getProductStock("stock > 0 and area = " + area + " and type = " + stockType + " and product_id = " + product.getId());
		if( psb == null ) {
			return false;
		} else {
			if( psb.getStock() < count) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 减少库存可用量，增加相应的冻结量
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public String updateLockProductStock(int area, int stockType,
			voProduct product, int count, IProductStockService service) {
		
		ProductStockBean psb = service.getProductStock("stock > 0 and area = " + area + " and type = " + stockType + " and product_id = " + product.getId());
		if( psb == null ) {
			return "未找到对应的库存信息！";
		} else {
			if( psb.getStock() < count) {
				return "当前库存不足";
			} else {
				if( !service.updateProductStockCount(psb.getId(), -count) ) {
					return "减少库存时，数据库操作失败！";
				}
				if( !service.updateProductLockCount(psb.getId(), count)) {
					return "增加库存冻结量时， 数据库操作失败！";
				}
			}
		}
		return "success";
	}
	
	/**
	 * 减少库存冻结量，增加相应的可用量
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public String updateUnLockProductStock(int area, int stockType,
			voProduct product, int count, IProductStockService service) {
		
		ProductStockBean psb = service.getProductStock("stock > 0 and area = " + area + " and type = " + stockType + " and product_id = " + product.getId());
		if( psb == null ) {
			return "未找到对应的库存信息！";
		} else {
			if( psb.getLockCount() < count) {
				return "当前库存冻结量不足";
			} else {
				if( !service.updateProductStockCount(psb.getId(), count) ) {
					return "增加库存时，数据库操作失败！";
				}
				if( !service.updateProductLockCount(psb.getId(), -count)) {
					return "减少库存冻结量时， 数据库操作失败！";
				}
			}
		}
		return "success";
	}

	/**
	 * 单纯的减少冻结量
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public String updateSubtractLockProductStock(int area, int stockType,
			voProduct product, int count, IProductStockService service) {
		
		ProductStockBean psb = service.getProductStock("stock > 0 and area = " + area + " and type = " + stockType + " and product_id = " + product.getId());
		if( psb == null ) {
			return "未找到对应的库存信息！";
		} else {
			if( psb.getLockCount() < count) {
				return "当前库存冻结量不足";
			} else {
				if( !service.updateProductLockCount(psb.getId(), -count)) {
					return "减少库存冻结量时， 数据库操作失败！";
				}
			}
		}
		return "success";
	}
	
	/**
	 * 检查货位库存可用量是否充足
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public boolean checkCargoStock(int area, int stockType, int storeType,
			voProduct product, int count, ICargoService cargoService) {
		
		CargoInfoAreaBean cargoArea = cargoService.getCargoInfoArea("old_id = "+area);
		CargoInfoBean cargoInfo = cargoService.getCargoInfo("area_id = "+ cargoArea.getId() + " and stock_type = " + stockType + " and store_type=" + storeType);
		if( cargoInfo == null ) {
			return false;
		}
		CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id = " + cargoInfo.getId() + " and product_id = " + product.getId());
		if( cpsb == null ) {
			return false;
		} else {
			if( cpsb.getStockCount() < count) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 减少货位库存可用量，增加相应的货位冻结量
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public String updateLockCargoStock(int area, int stockType, int storeType,
			voProduct product, int count, ICargoService cargoService) {
		CargoInfoAreaBean cargoArea = cargoService.getCargoInfoArea("old_id = "+area);
		CargoInfoBean cargoInfo = cargoService.getCargoInfo("area_id = "+ cargoArea.getId() + " and stock_type = " + stockType + " and store_type=" + storeType);
		if( cargoInfo == null ) {
			return "未找到对应的货位信息！";
		}
		CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id = " + cargoInfo.getId() + " and product_id = " + product.getId());
		if( cpsb == null ) {
			return "未找到对应的货位库存信息！";
		} else {
			if( cpsb.getStockCount() < count) {
				return "货位可用库存不足！";
			} else {
				if( !cargoService.updateCargoProductStockCount(cpsb.getId(), -count)) {
					return "在减少货位库存时，数据库操作失败！";
				}
				if( !cargoService.updateCargoProductStockLockCount(cpsb.getId(), count)) {
					return "在增加货位冻结量时， 数据库操作失败！";
				}
			}
		}
		return "success";
	}
	
	/**
	 * 减少货位库存冻结量，增加相应的货位可用量
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public String updateUnLockCargoStock(int area, int stockType, int storeType,
			voProduct product, int count, ICargoService cargoService) {
		CargoInfoAreaBean cargoArea = cargoService.getCargoInfoArea("old_id = "+area);
		CargoInfoBean cargoInfo = cargoService.getCargoInfo("area_id = "+ cargoArea.getId() + " and stock_type = " + stockType + " and store_type=" + storeType);
		if( cargoInfo == null ) {
			return "未找到对应的货位信息！";
		}
		CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id = " + cargoInfo.getId() + " and product_id = " + product.getId());
		if( cpsb == null ) {
			return "未找到对应的货位库存信息！";
		} else {
			if( cpsb.getStockLockCount() < count) {
				return "货位库存冻结量不足！";
			} else {
				if( !cargoService.updateCargoProductStockCount(cpsb.getId(), count)) {
					return "在增加货位库存时，数据库操作失败！";
				}
				if( !cargoService.updateCargoProductStockLockCount(cpsb.getId(), -count)) {
					return "在减少货位冻结量时， 数据库操作失败！";
				}
			}
		}
		return "success";
	}
	
	/**
	 * 单纯的减少货位冻结量
	 * @param area
	 * @param stockType
	 * @param product
	 * @param count
	 * @param service
	 * @return
	 */
	public String updateSubtractLockCargoStock(int area, int stockType, int storeType,
			voProduct product, int count, ICargoService cargoService) {
		CargoInfoAreaBean cargoArea = cargoService.getCargoInfoArea("old_id = "+area);
		CargoInfoBean cargoInfo = cargoService.getCargoInfo("area_id = "+ cargoArea.getId() + " and stock_type = " + stockType + " and store_type=" + storeType);
		if( cargoInfo == null ) {
			return "未找到对应的货位信息！";
		}
		CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id = " + cargoInfo.getId() + " and product_id = " + product.getId());
		if( cpsb == null ) {
			return "未找到对应的货位库存信息！";
		} else {
			if( cpsb.getStockLockCount() < count) {
				return "货位库存冻结量不足！";
			} else {
				if( !cargoService.updateCargoProductStockLockCount(cpsb.getId(), -count)) {
					return "在减少货位冻结量时， 数据库操作失败！";
				}
			}
		}
		return "success";
	}

	/**
	 * 根据商品编号 或商品条码 获得 商品信息的方法
	 * @param code
	 * @param bService
	 * @param wareService
	 */
	public voProduct getProuctByAnyCode(String code,
			IBarcodeCreateManagerService bService, WareService wareService) {
		voProduct product = null;
		ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(code)+"'");
		if( bBean == null || bBean.getBarcode() == null ) {
			product = wareService.getProduct(StringUtil.toSql(code));
		} else {
			product = wareService.getProduct(bBean.getProductId());
		}
		return product;
	}

	/**
	 * 扫描和出库单里的商品种类 以及数量是否对的上去
	 * @param orderStockBean
	 * @param list
	 * @param scanMap
	 * @param map
	 * @return
	 */
	public String checkPackageProductIntegrity(
			OrderStockBean orderStockBean, List<OrderStockProductBean> list, Map<Integer, Integer> scanMap, Map<Integer, String> map) {
		WareService wareService = new WareService(this.dbOp);
		String temp = "";
		int x = list.size();
		boolean integrity = true;
		for( int i = 0; i < x; i ++ ) {
			OrderStockProductBean ospBean = list.get(i);
			//先看有没有 扫描到该商品
			if( scanMap.containsKey(ospBean.getProductId())) {
				//在看数量是否正确
				int count = scanMap.get(ospBean.getProductId());
				String countCompare = getCountCompareResult(count, ospBean);
				if( countCompare != null ) {
					temp += "r-n" + countCompare;
					integrity = false;
				}
			} else {
				temp += "r-n" + "缺少商品" + ospBean.getProductCode();
				integrity = false;
			}
		}
		
		Iterator<Integer> itr = scanMap.keySet().iterator();
		for( ; itr.hasNext(); ) {
			Integer productId = itr.next();
			if( !map.containsKey(productId)) {
				voProduct product = wareService.getProduct(productId.intValue());
				if( product != null ) {
					temp += "r-n" + "多出商品" + product.getCode() + ",多了" + scanMap.get(productId) + "件";
					integrity = false;
				}
			} 
		}
		
		if( integrity ) {
			temp += "r-n"+"订单"+ orderStockBean.getOrderCode() +"检查完毕,包裹完整！";
		} else {
			temp += "r-n" + "订单" + orderStockBean.getOrderCode() + "检查完毕，有商品不正确！";
		}
		return temp;
	}
	/**
	 * 扫描和出库单里的商品种类 以及数量是否对的上去
	 * @param orderStockBean
	 * @param list
	 * @param scanMap
	 * @param map
	 * @return
	 */
	public boolean checkPackageProductIntegrityBoolean(
			OrderStockBean orderStockBean, List<OrderStockProductBean> list, Map<Integer, Integer> scanMap, Map<Integer, String> map) {
		WareService wareService = new WareService(this.dbOp);
		int x = list.size();
		boolean integrity = true;
		for( int i = 0; i < x; i ++ ) {
			OrderStockProductBean ospBean = list.get(i);
			//先看有没有 扫描到该商品
			if( scanMap.containsKey(ospBean.getProductId())) {
				//在看数量是否正确
				int count = scanMap.get(ospBean.getProductId());
				String countCompare = getCountCompareResult(count, ospBean);
				if( countCompare != null ) {
					integrity = false;
				}
			} else {
				integrity = false;
			}
		}
		
		Iterator<Integer> itr = scanMap.keySet().iterator();
		for( ; itr.hasNext(); ) {
			Integer productId = itr.next();
			if( !map.containsKey(productId)) {
				voProduct product = wareService.getProduct(productId.intValue());
				if( product != null ) {
					integrity = false;
				}
			} 
		}
		return integrity;
	}
	
	/**
	 * 检查 如果是 扫描商品  和 出库单商品中都存在的商品，看数量是否对的上
	 * @param count
	 * @param ospBean
	 * @return
	 */
	private String getCountCompareResult(int count,
			OrderStockProductBean ospBean) {
		String result = null;
		int difference = count - ospBean.getStockoutCount();
		if( difference > 0 ) {
			result = "商品" + ospBean.getProductCode() + "扫描数量多了" + difference;
		} else if( difference == 0 ) {
			result = null;
		} else if ( difference < 0 ) {
			result = "商品" + ospBean.getProductCode() + "扫描数量少了" + Math.abs(difference);
		}
		return result;
	}

	/**
	 * 根据 订单号  或者 包裹单号 得到订单信息 或返回错误提示
	 * @param string
	 * @param wareService
	 * @param istockService
	 * @return
	 * @throws Exception
	 */
	public Object getOrderByPcodeOrOcode(String string, WareService wareService, IStockService istockService) throws Exception  {
		voOrder result = null;
		
		AuditPackageBean auditPackageBean = istockService
		.getAuditPackage("package_code='" + StringUtil.toSql(string) + "'");

		if( auditPackageBean != null ) {
			result = wareService.getOrder("code='" + auditPackageBean.getOrderCode()+"'");
			if( result == null ) {
				return "没有根据包裹单号找到订单信息！";
			}
		} else {
			result = wareService.getOrder("code='"+StringUtil.toSql(string)+"'");
			if(result == null){
				return "没有找到订单信息！";
			}
		}
		
		return result;
	}
	
	/**
	 * 根据 订单号  或者 包裹单号 得到订单信息 或返回错误提示
	 * @param string
	 * @param wareService
	 * @param istockService
	 * @return
	 * @throws Exception
	 */
	public Object getOrderByPcodeOrOcodeConsiderArea(String string, WareService wareService, IStockService istockService, int wareArea) throws Exception  {
		voOrder result = null;
		
		AuditPackageBean auditPackageBean = istockService
		.getAuditPackage("package_code='" + StringUtil.toSql(string) + "'");

		if( auditPackageBean != null ) {
			result = wareService.getOrder("code='" + auditPackageBean.getOrderCode()+"'");
			if( result == null ) {
				return "没有根据包裹单号找到订单信息！";
			} else {
				OrderStockBean osBean = istockService.getOrderStock("order_id=" + result.getId() + " and status != 3");
				if( osBean == null ) {
					return "没有找到对应的出库单信息！";
				} 
			}
		} else {
			result = wareService.getOrder("code='"+StringUtil.toSql(string)+"'");
			if(result == null){
				return "没有找到订单信息！";
			} else {
				OrderStockBean osBean = istockService.getOrderStock("order_id=" + result.getId() + " and status != 3");
				if( osBean == null ) {
					return "没有找到对应的出库单信息！";
				} 
			}
		}
		
		return result;
	}
	
	/**
	 * 添加报损单，如果添加成功就会返回 报损单Bean 如果失败返回提示错误的String
	 * @param warehouse_type
	 * @param warehouse_area
	 * @param user
	 * @param i 
	 */
	public Object createBsOperation (int warehouse_type, int warehouse_area, voUser user) {
		String receipts_number = "";
		String title = "";// 日志的内容
		int typeString = 0;
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(
				IBaseService.CONN_IN_SERVICE, this.dbOp);
		// 报损
		String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
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
		receipts_number = code;
		title = "创建新的报损表" + receipts_number;
		typeString = 0;
		
		String nowTime = DateUtil.getNow();
		BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
		bsbyOperationnoteBean.setAdd_time(nowTime);
		bsbyOperationnoteBean.setCurrent_type(BsbyOperationnoteBean.dispose);
		bsbyOperationnoteBean.setOperator_id(user.getId());
		bsbyOperationnoteBean.setOperator_name(user.getUsername());
		bsbyOperationnoteBean.setReceipts_number(receipts_number);
		bsbyOperationnoteBean.setWarehouse_area(warehouse_area);
		bsbyOperationnoteBean.setWarehouse_type(warehouse_type);
		bsbyOperationnoteBean.setType(typeString);
		bsbyOperationnoteBean.setIf_del(0);
		bsbyOperationnoteBean.setFinAuditId(0);
		bsbyOperationnoteBean.setFinAuditName("");
		bsbyOperationnoteBean.setFinAuditRemark("");
		int maxid2 = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
		bsbyOperationnoteBean.setId(maxid2 + 1);
		if( !service.addBsbyOperationnoteBean(bsbyOperationnoteBean)) {
			return "数据库操作失败！";
		}
	
		// 添加操作日志
		BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
		bsbyOperationRecordBean.setOperator_id(user.getId());
		bsbyOperationRecordBean.setOperator_name(user.getUsername());
		bsbyOperationRecordBean.setTime(nowTime);
		bsbyOperationRecordBean.setInformation(title);
		bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
		if( !service.addBsbyOperationRecord(bsbyOperationRecordBean) ) {
			return "数据库操作失败！";
		}
		
		return bsbyOperationnoteBean;
	}
	
	/**
	 * 的到确定 有这种商品的 货位信息 或者 返回错误提示
	 * @param wareArea
	 * @param stockType
	 * @param productId
	 * @param storeType
	 * @param cargoService
	 * @return
	 */
	public Object getCargoCodeForSure(int wareArea, int stockType, int productId, int storeType, ICargoService cargoService) {
		
		CargoInfoAreaBean cargoArea = cargoService.getCargoInfoArea("old_id = "+wareArea);
		CargoInfoBean cargoInfo = cargoService.getCargoInfo("area_id = "+ cargoArea.getId() + " and stock_type = " + stockType + " and store_type=" + storeType);
		if( cargoInfo == null ) {
			return "未找到对应的货位信息！";
		}
		CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id = " + cargoInfo.getId() + " and product_id = " + productId);
		if( cpsb == null ) {
			return "未找到对应的货位库存信息！";
		}
		return  cargoInfo;
	}
	
	/**
	 * 为报损单添加商品
	 */
	public Object addProductToBsOperation(voProduct product, int count, BsbyOperationnoteBean bsbyOperationnoteBean, int wareArea, CargoInfoBean ciBean, voUser user) {
		
		//cargoCode 就是 退货库的那么一个货位
		if (count <= 0) {
			return "商品" + product.getCode() + "报损数量有误！";
		}
		if(ciBean == null ){
			return "商品" + product.getCode() + "没有找到有库存的退货库货位！";
		}
		if (product.getParentId1() == 106) {
			return "商品" + product.getCode() + "为新商品，请先修改该产品的分类";
		}
		if (product.getIsPackage() == 1) {
			return "商品" + product.getCode() + "为套装产品，不能添加！";
		}
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,this.dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		BsbyOperationnoteBean bean = bsbyOperationnoteBean;
		int x = getProductCount(product, bean.getWarehouse_area(), bean.getWarehouse_type());
		int result = ByBsAction.updateProductCount(x, bean.getType(), count);
		if (result < 0 ) {
			return "商品" + product.getCode() + "的库存不足！";
		}
		//新货位管理判断
		CargoProductStockBean cps = null;
		if(bean.getType()==0){
			//是报损单
	        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getWarehouse_area());
	        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+ciBean.getWholeCode()+"'", -1, -1, "ci.id asc");
	        if(cpsOutList == null || cpsOutList.size()==0){
	            return "商品" + product.getCode() + "货位号"+ ciBean.getWholeCode() + "无效！";
	        }
	        cps = (CargoProductStockBean)cpsOutList.get(0);
	        if(count > cps.getStockCount()){
	            return "商品" + product.getCode() + "货位号"+ ciBean.getWholeCode() + "货位库存为" + cps.getStockCount()+"，库存不足！";
	        }
		}
        
		BsbyProductBean bsbyProductBean = new BsbyProductBean();
		bsbyProductBean.setBsby_count(count);
		bsbyProductBean.setOperation_id(bean.getId());
		bsbyProductBean.setProduct_code(product.getCode());
		bsbyProductBean.setProduct_id(product.getId());
		bsbyProductBean.setProduct_name(product.getName());
		bsbyProductBean.setOriname(product.getOriname());
		bsbyProductBean.setAfter_change(result);
		bsbyProductBean.setBefore_change(x);
		float price5 = product.getPrice5();//含税金额
		//不含税金额
		float notaxProductPrice = service.returnFinanceProductPrice(bsbyProductBean.getProduct_id());
		bsbyProductBean.setPrice(price5);
		bsbyProductBean.setNotaxPrice(notaxProductPrice);
		boolean falg = service.addBsbyProduct(bsbyProductBean);
		if (!falg) {
			return "数据库操作失败!";
		}
		BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
		bsbyCargo.setBsbyOperId(bean.getId());
		bsbyCargo.setBsbyProductId(service.getDbOp().getLastInsertId());
		bsbyCargo.setCount(count);
		bsbyCargo.setCargoProductStockId(cps.getId());
		bsbyCargo.setCargoId(cps.getCargoId());
		service.addBsbyProductCargo(bsbyCargo);
		// 添加日志
		BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
		bsbyOperationRecordBean.setOperator_id(user.getId());
		bsbyOperationRecordBean.setOperator_name(user.getUsername());
		bsbyOperationRecordBean.setTime(DateUtil.getNow());
		bsbyOperationRecordBean.setInformation("给单据:" + bean.getReceipts_number()
				+ "添加商品:" + product.getCode() + "数量：" + count);
		bsbyOperationRecordBean.setOperation_id(bean.getId());
		if( !service.addBsbyOperationRecord(bsbyOperationRecordBean) ) {
			return "数据库操作失败！";
		}
		return bsbyProductBean;
	}
	
	/**
	 * 根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 2010-02-22
	 * 
	 * @param productCode
	 * @param area
	 * @param type
	 * @return
	 */
	public int getProductCount(voProduct product, int area, int type) {
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		int x = 0;
		product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
		x = product.getStock(area, type);
		return x;
	}
	
	
	/**
	 * 得到所有订单商品 从出库单来
	 * @param id
	 * @return
	 */
	public List<OrderStockProductBean> getAllProductsByOrder(int id) {
		//首先要找到 对应订单的所有出库单 
		List<OrderStockProductBean> result = new ArrayList<OrderStockProductBean>();
		List<OrderStockBean> orderStocks = new ArrayList<OrderStockBean>();
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,this.dbOp);
		orderStocks = (List<OrderStockBean>)istockService.getOrderStockList("order_id=" + id + " and status != " + OrderStockBean.STATUS4 , -1, -1, null);
		if( orderStocks == null || orderStocks.size() == 0 ) {
			return new ArrayList<OrderStockProductBean>();
		} else {
			int x = orderStocks.size();
			for( int i = 0 ; i < x; i ++ ) {
				OrderStockBean osBean = orderStocks.get(i);
				List<OrderStockProductBean> orderStockProducts = getAllProductsByOrderStock(osBean.getId(), istockService);
				int y = orderStockProducts.size();
				for( int j = 0 ; j < y ; j ++ ) {
					result.add(orderStockProducts.get(j));
				}
			}
			return result;
		}
	}

	/**
	 *  将map中的所有 出库单商品信息放倒list中
	 * @param map
	 * @return
	 */
	public List<OrderStockProductBean> mergeOrderStockProducts(
			Map<Integer, OrderStockProductBean> map) {
		List<OrderStockProductBean> res = new ArrayList<OrderStockProductBean>();
		Iterator<OrderStockProductBean> itr = map.values().iterator();
		for( ; itr.hasNext(); ) {
			OrderStockProductBean ospb = itr.next();
			res.add(ospb);
		}
		return res;
	}
	
	/**
	 * 将传入的 可能包含多个 同样出库商品的list 用map来合并 不出现重复商品
	 * @param result
	 * @return
	 */
	public Map<Integer, OrderStockProductBean> getMergMap(
			List<OrderStockProductBean> result) {
		Map<Integer,OrderStockProductBean> map = new HashMap<Integer, OrderStockProductBean>();
		int x = result.size();
		for( int i = 0 ; i < x; i ++ ) {
			OrderStockProductBean ospBean = result.get(i);
			if( map.containsKey(ospBean.getProductId())) {
				OrderStockProductBean temp = map.get(ospBean.getProductId());
				temp.setStockoutCount(temp.getStockoutCount() + ospBean.getStockoutCount());
			} else {
				map.put(ospBean.getProductId(), ospBean);
			}
		}
		return map;
	}

	/**
	 * 得到根据出库单获得出库商品信息
	 * @param id
	 * @param istockService
	 * @return
	 */
	private List<OrderStockProductBean> getAllProductsByOrderStock(int id, IStockService istockService) {
		
		List<OrderStockProductBean> result = (List<OrderStockProductBean>)istockService.getOrderStockProductList("order_stock_id=" + id, -1, -1, null);
		if( result == null ) {
			return new ArrayList<OrderStockProductBean>();
		} else {
			return result;
		}
	}

	/**
	 * 对报损单进行一次审核   锁定库存
	 * @param user 
	 * @param bsbyOperationnoteBean
	 * @return
	 */
	public String auditBsbyOperationnote(
			BsbyOperationnoteBean bean, voUser user) {
		String result = "SUCCESS";
		boolean flag = false;
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,this.dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		//报损单提交审核，锁定库存量
		//报损单中的所有产品
		List bsbyList = service.getBsbyProductList("operation_id=" + bean.getId(), -1, -1, null);
		Iterator it = bsbyList.iterator();
		if(bean.getType() == 0){
			for (; it.hasNext();) {
				BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
				BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
				if(bsbyCargo == null){
					result = "货位信息异常，操作失败，请与管理员联系！";
					return result;
				}
				String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
				+ "area = " + bean.getWarehouse_area() + " and type = "
				+ bean.getWarehouse_type();
				ProductStockBean psBean = psService.getProductStock(sql);
				//减少库存
				if(!psService.updateProductStockCount(psBean.getId(), -bsbyProductBean.getBsby_count())){
					result = "库存操作失败，可能是库存不足，请与管理员联系！";
					return result;
				}
				//增加库存锁定量
				if (!psService.updateProductLockCount(psBean.getId(), bsbyProductBean.getBsby_count())) {
					result = "库存操作失败，可能是库存不足，请与管理员联系！";
					return result;
				}

				//锁定货位库存
				//出库
				if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
					result = "货位库存操作失败，货位库存不足！";
					return result;
				}
				if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
					result = "货位库存操作失败，货位库存不足！";
					return result;
				}
			}
		}
		String remark = "理赔核销";
		flag = service.updateBsbyOperationnoteBean("current_type=" + 1 + ", remark='" + remark +"'", "id=" + bean.getId());
		String zhuangtai = (String) bean.current_typeMap.get(Integer.valueOf(1));
		if (flag) {
			// 添加日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(user.getId());
			bsbyOperationRecordBean.setOperator_name(user.getUsername());
			bsbyOperationRecordBean.setTime(DateUtil.getNow());
			bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "的状态为:" + zhuangtai);
			bsbyOperationRecordBean.setOperation_id(bean.getId());
			service.addBsbyOperationRecord(bsbyOperationRecordBean);
		}
		
		return result;
	}
	
	public OrderBillInfoBean getOrderStockOutInfo( int id ) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		OrderBillInfoBean obiBean = null;
		DbOperation dbOp2 = this.dbOp;
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, this.dbOp);// 分拣相关的service
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		siService.getDbOp().startTransaction();//需要连接检查
		boolean transactionFlag = false;
		try {
			// 根据分拣波次号，取得该波次下的订单列表
			StringBuffer sqlSb = new StringBuffer();
			//sqlSb.append("select os.*,uo.*,sbo.group_num,sbg.code,d.serial_number,sbo.group_code,d.batch from user_order uo,order_stock os,sorting_batch_order sbo,sorting_batch_group sbg,order_customer d  where sbo.delete_status<>1 and os.status<>3 and d.order_code = uo.code and ");
			sqlSb.append("select os.*,uo.*,d.serial_number,d.batch from user_order uo,order_stock os,order_customer d where os.status<>3 and d.order_code = uo.code and ");
			if (id != -1 && id != 0) {
				sqlSb.append("uo.id =").append(id).append(" and");
			}
			//sqlSb.append(" sbo.order_id =os.order_id  and uo.id = os.order_id and sbo.sorting_group_id=sbg.id order by sbo.group_num asc");
			sqlSb.append(" uo.id = os.order_id");
			List orderList = new ArrayList();
			pst = siService.getDbOp().getConn().prepareStatement(sqlSb.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				voOrder vo = new voOrder();
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
//				vo.setRemark(rs.getString("sbg.code"));// 借用，放分拣波次号
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
//				vo.setGroup_num(rs.getString("sbo.group_num"));
				vo.setSerialNumber(rs.getInt("d.serial_number"));
				vo.setBatchNum(rs.getInt("d.batch"));
//				vo.setGroupCode(rs.getString("sbo.group_code"));
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
				String dmSql = "select type_id from user_order_package_type where name ='印刷品'";
				ResultSet rsDm = siService.getDbOp().executeQuery(dmSql);
				String isDm = "";
				if (rsDm.next()) { 
					isDm = " AND (e.product_type_id<>" + rsDm.getInt("type_id") + " or e.product_type_id is null)";
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
					orderProduct.setOriname(rs2.getString("oriname"));// 商品名称
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
						productNameMap.put(huizongKey, orderProduct.getOriname());
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
						productNameMap.put(huizongKey, orderProduct.getOriname());
					}
				}
				productMap.put(order.getOrderStock().getId() + "", orderProductList);// 以申请出库的订单ID为key把该订单里的产品放到Map中
				if (rs2 != null) {
					rs2.close();
				}
				if (pst2 != null) {
					pst2.close();
				}
				// 查询订单下的印刷品产品
				String nodmSql = "select type_id from user_order_package_type where name ='印刷品'";
				ResultSet norsDm = siService.getDbOp().executeQuery(nodmSql);
				String noDm = "";
				if (norsDm.next()) {
					noDm = " AND e.product_type_id=" + norsDm.getInt("type_id");
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
					orderProduct.setOriname(rs3.getString("oriname"));// 商品名称
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
						productNameMap1.put(huizongKey, orderProduct.getOriname());
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
						productNameMap1.put(huizongKey, orderProduct.getOriname());
					}
				}
				productMap1.put(order.getOrderStock().getId() + "", orderProductList1);// 以申请出库的订单ID为key把该订单里的产品放到Map中
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
			 obiBean = new OrderBillInfoBean();
			 obiBean.setOrderList(orderList);
			 obiBean.setProductMap( productMap);
			 obiBean.setHuizongList( huizongList);
			 obiBean.setHuizongNum( huizongNum);
			 obiBean.setProductCodeMap( productCodeMap);
			 obiBean.setProductMap1( productMap1);
			 obiBean.setHuizongList1( huizongList1);
			 obiBean.setHuizongNum1( huizongNum1);
			 obiBean.setProductCodeMap1( productCodeMap1);
			 obiBean.setProductNameMap( productNameMap);
			 obiBean.setProductNameMap1( productNameMap1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obiBean;
	}
	
	/**
	 * 获取电子出货单信息html
	 * @param obiBean
	 * @param request
	 * @return
	 */
	public String getOrderBillInfoHTML(OrderBillInfoBean obiBean, HttpServletRequest request) {
		String result = "";
		int line =0;//当前的字符串行数
		int SKUIndex=0;//SKU循环到的行号
		int SKUIndex1 = 0;//印刷品SKU的行号
		int pageNum=0;//当前页数
		int MAXLINE=37;//每页最大37行字符串
		int charNumPerLine = 32;//每行最多35个字符串，超出会换行
		if (obiBean != null) {
			List huizongList1 = new ArrayList();
			huizongList1 = (List) obiBean.getHuizongList1();
			List orderList = obiBean.getOrderList();// 订单列表
			Map productMap = obiBean.getProductMap();// productMap.put(order.getOrderStock().getId()+"",
														// orderProductList);//以申请出库的订单ID为key把该订单里的产品放到Map中
			List huizongList = obiBean.getHuizongList();
			Map productMap1 = obiBean.getProductMap1();
			Map productNameMap = obiBean.getProductNameMap();
			Map productNameMap1 = obiBean.getProductNameMap1();
			int bianjie = 0;
			Map stockMap = new HashMap();
			int orderNum = 0;// 标记是第几个订单
			Iterator orderListIter = orderList.listIterator();
			while (orderListIter.hasNext()) {// 循环订单
				voOrder order = (voOrder) orderListIter.next();

				int index = 0;
				int totalCount = 0;// 本单商品数
				int totalSum = 0;// 商品总数
				float totalPrice = 0;// 总金额

				List productList = (List) productMap.get(order.getOrderStock()
						.getId() + "");
				List productList1 = (List) productMap1.get(order
						.getOrderStock().getId() + "");
				productList.addAll(productList1);
				if (productList != null) {
					orderNum++;
					Iterator iter = productList.listIterator();
					while (iter.hasNext()) {// 循环订单中的产品
						voProduct orderProduct = (voProduct) iter.next();

						totalCount += orderProduct.getBuyCount();
						totalPrice += orderProduct.getBuyCount()
								* orderProduct.getPrice();

						if (index % 6 == 0) {// 每个发货清单的开头
							totalCount = 0;

							result += "<div id=\"tableDiv"+bianjie+"\">";
							result += "<table cellpadding=\"0\" cellspacing=\"0\" width=\"700\" height=\"450\" border=\"1\" style=\"border: 1px solid; border-collapse: collapse; font-size: 12px;\">";
							result += "<tr height=\"60px\">";
							result += "<td colspan=\"6\">";
							result += "<table width=\"730\" cellpadding=\"1\" style=\"border: none; border-collapse: collapse;\">";
							result += "<tr>";
							result += "<td rowspan=\"2\" style=\"width: 180px; height: 90px; text-align: center; vertical-align: middle;\">";
							result += "<img src=\""
									+ request.getContextPath()
									+ "/image/logo_fhd.bmp\" width=\"150px\" height=\"63px\"/>";
							result += "</td>";
							result += "<td style=\"width: 27%; height: 30px; text-align: center; vertical-align: middle;\">";
							result += "<font style=\"font-size: 28px;\"><strong>发&nbsp;货&nbsp;清&nbsp;单&nbsp;</strong></font>";
							result += "<br><font style=\"font-size: 24px;\">"
									+ order.getCode() + "</font>";
							result += "</td>";
							result += "<td id=\"barcodeID"
									+ bianjie
									+ "\" width=170px rowspan=\"2\" align=\"right\" style=\"height: 60px; text-align: center;\">";
							result += "<div id=\"barcodeImage" + bianjie + "\">"
									+ order.getCode() + "</div>";
							result += "</td>";
							result += "<td> ";
							/*if (productList.size() > 1) {
								result += "<font style=\"font-size: 30px;\"><b>"
										+ order.getGroupCode() + "</b></font>";
							} else {
								result += "<font style=\"font-size: 30px;\"><b>"
										+ order.getGroup_num() + "</b></font>";
							}*/
							String temp = (index / 6) > 0 ? "续" : "";
							result += "<span style=\"font-size:15px;font-weight:bold;\">"
									+ temp + "</span>";
							result += "</td>";
							result += "</tr>";
							result += "<tr>";
							result += "<td style=\"width: 40%; height: 30px; text-align: center; vertical-align: middle; font-size: 15px;\">";
							result += "<i>欢迎使用<strong>买卖宝</strong>&nbsp;&nbsp;手机购物<strong>我最好</strong></i>";
							result += "</td>";
							result += "</tr>";
							result += "</table>";
							result += "</td>";
							result += "</tr>";
							result += "<tr>";
							result += "<td align=\"left\" style=\"font-size: 13px;\">";
							result += "序号：<strong>" + order.getBatchNum() + "-"
									+ order.getSerialNumber() + "</strong>";
							result += "</td>";
							result += "<td align=\"left\" colspan=\"2\" width=\"300px\">订单时间："
									+ order.getCreateDatetime().toString()
											.substring(0, 16) + "</td>";
							result += "<td align=\"left\" width=\"15%\">姓名：<strong";
							String temp2 = "&nbsp;";
							if (order.getName() != null) {
								temp2 = StringUtil
										.getString(order.getName(), 8);
							}
							result += "style=\"font-size: 13px;\">" + temp2
									+ "</strong></td>";
							result += "<td align=\"left\" colspan=\"2\">快递公司："
									+ order.getDeliverName() + "</td>";
							result += "</tr>";
							result += "<tr>";
							result += "<td align=\"left\">商品序号</td>";
							result += "<td align=\"center\">&nbsp;&nbsp;</td>";
							result += "<td align=\"left\">买卖宝编号</td>";
							result += "<td align=\"center\">数量</td>";
							result += "<td align=\"center\">单价</td>";
							result += "<td align=\"center\">金额</td>";
							result += "</tr>";
							result += "<tr>";
							int count2 = index + 1;
							result += "<td align=\"left\">" + count2 + "</td>";
							result += "<td align=\"left\"><strong style=\"font-size: 13px;\">"
									+ orderProduct.getCargoPSList().get(0)
											.toString() + "</strong></td>";
							result += "<td align=\"left\">"
									+ orderProduct.getCode() + "</td>";
							result += "<td align=\"center\"><strong style=\"font-size: 13px;\">"
									+ orderProduct.getBuyCount()
									+ "</strong></td>";
							result += "<td style=\"text-align: center;\">"
									+ NumberUtil.price(orderProduct.getPrice())
									+ "</td>";
							result += "<td style=\"text-align: center;\">"
									+ NumberUtil.price(orderProduct
											.getBuyCount()
											* orderProduct.getPrice())
									+ "</td>";

							totalSum += orderProduct.getBuyCount();
							result += "</tr>";
							result += "<tr>";
							result += "<td></td>";
							result += "<td align=\"left\" style=\"font-size: 13px;\" colspan=\"5\">"
									+ orderProduct.getOriname() + "</td>";
							result += "</tr>";

						} else { // 非一个发货单的开头
							int count = index + 1;
							result += "<tr>";
							result += "<td align=\"left\">" + count + "</td>";
							result += "<td align=\"left\"><strong style=\"font-size: 13px;\">"
									+ orderProduct.getCargoPSList().get(0)
											.toString() + "</strong></td>";
							result += "<td align=\"left\">"
									+ orderProduct.getCode() + "</td>";
							result += "<td align=\"center\"><strong style=\"font-size: 13px;\">"
									+ orderProduct.getBuyCount()
									+ "</strong></td>";
							result += "<td style=\"text-align: center;\">"
									+ NumberUtil.price(orderProduct.getPrice())
									+ "</td>";
							result += "<td style=\"text-align: center;\">"
									+ NumberUtil.price(orderProduct
											.getBuyCount()
											* orderProduct.getPrice())
									+ "</td>";

							totalSum += orderProduct.getBuyCount();
							result += "</tr>";
							result += "<tr>";
							result += "<td></td>";
							result += "<td align=\"left\" style=\"font-size: 13px;\" colspan=\"5\">"
									+ orderProduct.getOriname() + "</td>";
							result += "</tr>";
						}
						index++;
						if (index % 6 == 0) {
							bianjie++;
							stockMap.put(bianjie + "", order.getCode());
							result += "<tr>";
							String temp3 = "";
							if (productList.size() > 6) {
								temp3 = "小计：" + totalCount + "&nbsp;";
							}
							result += "<td colspan=\"2\" align=\"left\">" + temp3
									+ "商品总数：" + totalSum + "</td>";
							result += "<td align=\"left\">运费："
									+ (int) order.getPostage() + "元</td>";
							String temp4 = "";

							switch (order.getBuyMode()) {
							case 0:
								temp4 = "货到付款";
								break;
							case 1:
								temp4 = "邮购";
								break;
							case 2:
								temp4 = "上门自取";
								break;
							}
							result += "<td colspan=\"2\" align=\"left\">付款方式："
									+ temp4 + "</td>";
							result += "<td align=\"left\">总金额："
									+ NumberUtil.price(totalPrice) + "元</td>";
							result += "</tr>";
							result += "<tr height=\"25px\">";
							result += "<td colspan=\"6\" align=\"center\" style=\"border: none;\"><strong";
							result += "style=\"font-size: 13px;\">全场保真！全国范围货到付款！30天包退换！无风险网购第一站：买卖宝</strong>";
							result += "</td>";
							result += "</tr>";
							result += "<tr height=\"25px\">";
							result += "<td colspan=\"6\" align=\"center\" style=\"border: none;\">";
							result += "<strong style=\"font-size: 13px;\">全国售后服务专线（免长话费）：40088-43211&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手机访问&nbsp;mmb.cn</strong>";
							result += "</td>";
							result += "</tr>";
							result += "</table>";
							result += "</div>";
							result += "<br />";

						}
						if (index % 6 != 0 && index == productList.size()) {// 产品列表结束但未到一页结尾
							for (; index % 6 != 0; index++) {
								result += "<tr>";
								result += "<td>&nbsp;</td>";
								result += "<td>&nbsp;</td>";
								result += "<td>&nbsp;</td>";
								result += "<td>&nbsp;</td>";
								result += "<td>&nbsp;</td>";
								result += "<td>&nbsp;</td>";
								result += "</tr>";
								result += "<tr>";
								result += "<td>&nbsp;</td>";
								result += "<td colspan=\"5\">&nbsp;</td>";
								result += "</tr>";
							}// 此时index%6应为0，需要添加发货清单结尾
							if (index % 6 == 0 && productList.size() != 0) {
								bianjie++;
								stockMap.put(bianjie + "", order.getCode());
								result += "<tr>";
								result += "<td colspan=\"2\" align=\"left\">商品总数："
										+ totalSum + "</td>";
								result += "<td align=\"left\">运费："
										+ (int) order.getPostage() + "元</td>";
								String temp5 = "";

								switch (order.getBuyMode()) {
								case 0:
									temp5 = "货到付款";
									break;
								case 1:
									temp5 = "邮购";
									break;
								case 2:
									temp5 = "上门自取";
									break;
								}
								result += "<td colspan=\"2\" align=\"left\">付款方式："
										+ temp5 + "</td>";
								result += "<td align=\"left\">总金额："
										+ NumberUtil.price(totalPrice)
										+ "元</td>";
								result += "</tr>";
								result += "<tr height=\"25px\">";
								result += "<td colspan=\"6\" align=\"center\" style=\"border: none;\"><strong";
								result += "style=\"font-size: 13px;\">全场保真！全国范围货到付款！30天包退换！无风险网购第一站：买卖宝</strong>";
								result += "</td>";
								result += "</tr>";
								result += "<tr height=\"25px\">";
								result += "<td colspan=\"6\" align=\"center\" style=\"border: none;\">";
								result += "<strong style=\"font-size: 13px;\">全国售后服务专线（免长话费）：40088-43211&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;手机访问&nbsp;mmb.cn</strong>";
								result += "</td>";
								result += "</tr>";
								result += "</table>";
								result += "</div>";
								result += "<br />";
							}
						}
					}
				}
			}

		}
		return result;
	}
	/**
	 * 添加退货包裹信息， 为待退货的订单状态的。
	 * @param user
	 * @param vorder
	 * @param status
	 * @param wareArea
	 * @param wareArea 
	 * @param wareService
	 * @return
	 */
	public String addReturnedPackageInfoBref(voUser user, voOrder vorder, int reasonId, int wareArea, WareService wareService) {
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		
		AuditPackageBean auditPackageBean = service.getAuditPackage("order_code='" + vorder.getCode() + "'"); 
		ReturnedPackageBean packageBean = new ReturnedPackageBean();
		packageBean.setDeliver(vorder.getDeliver());
		//packageBean.setOperatorId(user.getId());
		//packageBean.setOperatorName(user.getUsername());
		packageBean.setOrderCode(vorder.getCode());
		packageBean.setOrderId(vorder.getId());
		if( auditPackageBean == null ) {
			packageBean.setPackageCode("");
		} else {
			packageBean.setPackageCode(auditPackageBean.getPackageCode());
			packageBean.setCheckDatetime(auditPackageBean.getCheckDatetime());
		}
		packageBean.setStatus(ReturnedPackageBean.STATUS_TO_RETURN);
		packageBean.setStorageStatus(-1);
		packageBean.setImportTime(DateUtil.getNow());
		packageBean.setImportUserId(user.getId());
		packageBean.setImportUserName(user.getUsername());
		if( reasonId == -1 ) {
			packageBean.setReasonId(0);
		} else {
			packageBean.setReasonId(reasonId);
		}
		//packageBean.setStorageTime(DateUtil.getNow());
		packageBean.setArea(wareArea);
		if(!statService.addReturnedPackage(packageBean)){
			return "为订单" + vorder.getCode() + "添加退货包裹失败";
		}
		return "SUCCESS";
	}

	/**
	 * 根据id获取退货原因信息
	 * @param rId
	 * @return
	 */
	public ReturnsReasonBean getReturnsReasonById(int rId) {
		ResultSet rs = null;
		PreparedStatement prod = null;
		ReturnsReasonBean result = null;
		String sql = "select id, reason, code from returns_reason where id = ?";
		try {
			prod = this.dbOp.getConn().prepareStatement(sql);
			prod.setInt(1, rId);
			rs = prod.executeQuery();
			if( rs.next()) {
				result = new ReturnsReasonBean();
				result.setId(rs.getInt("id"));
				result.setReason(rs.getString("reason"));
				result.setCode(rs.getString("code"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if( rs != null ) {
				try {rs.close();} catch (SQLException e) {}
				rs = null;
			}
			if( prod != null ) {
				try {prod.close();} catch (SQLException e) {}
				prod = null;
			}
		}
		return result;
	}
	
	/**
	 * 根据code获取退货原因信息
	 * @param rId
	 * @return
	 */
	public ReturnsReasonBean getReturnsReasonByCode(String code) {
		ResultSet rs = null;
		PreparedStatement prod = null;
		ReturnsReasonBean result = null;
		String sql = "select id, reason, code from returns_reason where code = ?";
		try {
			prod = this.dbOp.getConn().prepareStatement(sql);
			prod.setString(1, code);
			rs = prod.executeQuery();
			if( rs.next()) {
				result = new ReturnsReasonBean();
				result.setId(rs.getInt("id"));
				result.setReason(rs.getString("reason"));
				result.setCode(rs.getString("code"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if( rs != null ) {
				try {rs.close();} catch (SQLException e) {}
				rs = null;
			}
			if( prod != null ) {
				try {prod.close();} catch (SQLException e) {}
				prod = null;
			}
		}
		return result;
	}

	/**
	 * 获得  三种理赔方式的金额
	 * @param cvBean
	 * @param vorder
	 * @param mbBean
	 * @return
	 */
	public Map<Integer, String> calculateAvailablePrice(
			ClaimsVerificationBean cvBean, voOrder vorder, MailingBalanceBean mbBean) {
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(0, Float.toString(vorder.getDprice()));
		
		float calculatedSKUPrice = calculateSKUPrice(cvBean.getClaimsVerificationProductList());
		map.put(1, Float.toString(roundKeepTwo(calculatedSKUPrice)));
		
		if( mbBean == null ) {
			map.put(2, Float.toString(0));
		} else {
			map.put(2, Float.toString(roundKeepTwo(mbBean.getCarriage()*3)));
		}
		return map;
	}
	/**
	 * 计算按sku理赔的整个订单的金额
	 * @param claimsVerificationProductList
	 * @return
	 */
	private float calculateSKUPrice(List claimsVerificationProductList) {
		float result = 0;
		int x = claimsVerificationProductList.size();
		for( int i = 0 ; i < x ; i ++ ) {
			ClaimsVerificationProductBean cbpBean = (ClaimsVerificationProductBean) claimsVerificationProductList.get(i);
			float skuPrice = calculateSingleSKUPrice(cbpBean);
			result += skuPrice;
		}
		return result;
	}
	
	/**
	 * 计算每个sku的理赔金额
	 * @param cvpBean
	 * @return
	 */
	private float calculateSingleSKUPrice(ClaimsVerificationProductBean cvpBean) {
		float result = 0;
		if( cvpBean.getExist() == ClaimsVerificationProductBean.NOT_EXIST ) {
			if( cvpBean.getCount() > 0 ) {
				result += cvpBean.getProduct().getPrice2() * cvpBean.getCount();
			}
		}
		return result;
	}
	
	/**
	 * 将float类型保留两位小数
	 * @param target
	 * @return
	 */
	private float roundKeepTwo(float target) {
		return (float)(Math.round(target*100))/100;
	}

	/**
	 * 去掉字符串中的换行等等符号，不使json调用报错
	 * @return
	 */
	public String changeStringForJson(String target) {
		String result = target;
		result = result.replaceAll("'", "\"");
		result = StringUtil.toWml(result);
		return result;
	}

	public Object addReturnPackageCheckInfo(Map<Integer, Integer> scanMap,
			Map<Integer, String> map, OrderStockBean orderStockBean, List<OrderStockProductBean> list,
			AuditPackageBean apBean, ReturnedPackageBean rpBean, voOrder order, voUser user) {
		
		WareService wareService = new WareService(this.dbOp);
		this.dbOp.startTransaction();
		try {
			//首先要验证订单是否已经  存在于包裹核查列表中了****
			ReturnPackageCheckBean rpcBean1 = this.getReturnPackageCheck("order_code='" + order.getCode() + "'");
			if( rpcBean1 != null ) {
				
				if( rpcBean1.getStatus() != ReturnPackageCheckBean.STATUS_UNDEAL  && rpcBean1.getStatus() != ReturnPackageCheckBean.STATUS_AUDIT_FAIL) {
					return "当前包裹核查记录 已经在进行处理，原有记录将不会被替代！";
				}
				//删除原有记录的地方了
				if( ! this.deleteReturnPackageCheck("id=" + rpcBean1.getId()))  {
					this.dbOp.rollbackTransaction();
					return "删除原有包裹核查信息时,数据库操作失败！";
				}
				List tempList = this.getReturnPackageCheckProductList("return_package_check_id=" + rpcBean1.getId(), -1, -1, null);
				if( tempList.size() != 0 ) {
					if( ! this.deleteReturnPackageCheckProduct("return_package_check_id=" + rpcBean1.getId()))  {
						this.dbOp.rollbackTransaction();
						return "删除原有包裹核查信息时,数据库操作失败！";
					}
				}
			}
			
			
			int checkTypeResult = this.checkPackageTypeResult(orderStockBean, list, scanMap, map);
			ReturnPackageCheckBean rpcBean = new ReturnPackageCheckBean();
			rpcBean.setArea(rpBean.getArea());
			rpcBean.setCheckTime(DateUtil.getNow());
			rpcBean.setCheckUserId(user.getId());
			rpcBean.setCheckUserName(user.getUsername());
			if( checkTypeResult == 0 ) {
				rpcBean.setCheckResult(ReturnPackageCheckBean.RESULT_NORMAL);
			} else {
				rpcBean.setCheckResult(ReturnPackageCheckBean.RESULT_ABNORMAL);
			}
			rpcBean.setOrderCode(order.getCode());
			rpcBean.setPackageCode(apBean.getPackageCode());
			rpcBean.setStatus(ReturnPackageCheckBean.STATUS_UNDEAL);
			rpcBean.setType(checkTypeResult);
			
			
			if( !this.addReturnPackageCheck(rpcBean) ) {
				this.dbOp.rollbackTransaction();
				return "添加包裹核查信息时,数据库操作失败！";
			}
			int id = this.dbOp.getLastInsertId();
			Iterator itr = scanMap.keySet().iterator();
			List<ReturnPackageCheckProductBean> rpcpList = new ArrayList<ReturnPackageCheckProductBean> ();
			for( ; itr.hasNext(); ) {
				Integer productId = (Integer) itr.next();
				voProduct product = wareService.getProduct(productId.intValue());
				ReturnPackageCheckProductBean  rpcpBean = new ReturnPackageCheckProductBean();
				rpcpBean.setCount(scanMap.get(productId));
				rpcpBean.setProductId(productId);
				rpcpBean.setProductCode(product.getCode());
				rpcpBean.setReturnPackageCheckId(id);
				rpcpList.add(rpcpBean);
				if( !this.addReturnPackageCheckProduct(rpcpBean) ) {
					this.dbOp.rollbackTransaction();
					return "添加包裹核查商品信息时,数据库操作失败！";
				}
			}
			rpcBean.setReturnPackageCheckProductList(rpcpList);
			this.dbOp.commitTransaction();
			return rpcBean;
		} catch (Exception e ) {
			this.dbOp.rollbackTransaction();
			e.printStackTrace();
		}
		return "记录包裹核查记录时，数据库操作发生了错误！";
	}
	
	/**
	 * 返回各类 算
	 * @param id
	 * @return
	 */
	public Map<String, Float> calculateClaimsPrice(int id,ClaimsVerificationStatService claimsVerificationStatService) {
		Map<String, Float> result = new HashMap<String, Float>();
		WareService wareService = new WareService(this.dbOp);
		IBalanceService baService = ServiceFactory.createBalanceService(
				IBaseService.CONN_IN_SERVICE, this.dbOp);
		// 订单总额，key理赔id，value订单总额
		voOrder vorder = wareService.getOrder(id);
		MailingBalanceBean mbBean = baService.getMailingBalance("order_id="
				+ vorder.getId());
		List<voOrderProduct> goods = getAllProductsAvgPrice(vorder.getId(),
				mbBean.getStockoutDatetime().substring(0, 10), claimsVerificationStatService);
		// 计算折扣后SKU总金额
		double totalDprice = 0;
		for (voOrderProduct vp : goods) {
			totalDprice += (vp.getDiscountPrice() * vp.getCount());
		}
		// 计算折扣后SKU总金额
		double totalPrice5 = 0;
		for (voOrderProduct vp : goods) {
			totalPrice5 += (vp.getPrice5() * vp.getCount());
		}
		float all = 0;
		// 订单金额
		for (voOrderProduct vop : goods) {
			// 每个订单的 单个sku价
			//在计算最终价格的时候 会 根据是否是 成人产品线的商品来 决定 是否除税 1.17
			taxDeal(vop);
			result.put("SKU_" + vop.getProductId(),
					Arith.round((float) (vop.getPrice() * 1.25), 2));
			all += (float)(vop.getPrice())*vop.getCount()*1.25;
		}
		// 整单计算
		// 三倍运费理赔
		float threeMailingPrice = mbBean.getCarriage() * 3;
		result.put("MAIL", Arith.round(threeMailingPrice, 2));
		//result.put("ALL", Arith.round(all, 2));
		return result;
	}
	
	/**
	 * 根据产品id 找到产品 是否是 成人产品线， 如果不是 要把价格除以 1.17
	 * @param vop
	 */
	private void taxDeal(voOrderProduct vop) {
		WareService wareService = new WareService(this.dbOp);
		voProduct product = wareService.getProduct(vop.getProductId());
		String productLineNameCondition = "  (product_line_catalog.catalog_id = (" 
				+ product.getParentId1() + ") and product_line_catalog.catalog_type = 1) or (product_line_catalog.catalog_type = 2 and product_line_catalog.catalog_id "
				+ " = (" + product.getParentId2() + "))";
			voProductLine vpl = wareService.getProductLine(productLineNameCondition);
			if( vpl == null || vpl.getId() != 12 ) {
				if( Float.compare(vop.getPrice(), 0f) != 0 ) {
					vop.setPrice(Arith.div(vop.getPrice(), 1.17f, 2));
				}
			} 
	}

	/**
	 * 根据产品id 获取对应商品的 包裹理赔金额
	 * @param productId
	 * @return
	 * @throws MyException 
	 */
	public Float getPackageClaimsPrice(int productId) throws MyException {
		float result = 0;
		WareService wareService = new WareService(this.dbOp);
		voProduct product = wareService.getProduct(productId);
		String productLineNameCondition = "  (product_line_catalog.catalog_id = (" 
				+ product.getParentId1() + ") and product_line_catalog.catalog_type = 1) or (product_line_catalog.catalog_type = 2 and product_line_catalog.catalog_id "
				+ " = (" + product.getParentId2() + "))";
			voProductLine vpl = wareService.getProductLine(productLineNameCondition);
		if( vpl == null ) {
			throw new MyException("存在商品"+product.getCode()+"没有找到对应的产品线！");
		}
		try {
			String sql = "select price from claims_package_price where product_line_id = " + vpl.getId();	
			ResultSet rs = this.dbOp.executeQuery(sql);
			if( rs.next() ) {
				result = rs.getFloat(1);
			} else {
				//throw new MyException("没有找到商品"+ product.getCode() +"的包裹理赔金额，它的产品线是"+ vpl.getName()+"!");
				return 0f;
			}
		} catch(SQLException se) {
			se.printStackTrace();
		}
		return Arith.round(result, 2);
	}
	
	
	public List<voOrderProduct> getAllProductsAvgPrice(int orderId, String stockOutTime, ClaimsVerificationStatService claimsVerificationStatService){
		// 获取所有订单商品
		List<voOrderProduct> mapOrderGoods = getOrderProductByClaim(orderId,"user_order_product_split_history", stockOutTime,claimsVerificationStatService);
		List<voOrderProduct> viceMapOrderGoods = getOrderProductByClaim(orderId, "user_order_present_split_history", stockOutTime, claimsVerificationStatService);
		//合并订单商品和赠品
			List<voOrderProduct> list = mapOrderGoods;
			if(list!=null){
				list.addAll(viceMapOrderGoods);
			}
		return mapOrderGoods;
	}
	
	public List<voOrderProduct> getOrderProductByClaim(int orderId,String tableName, String stockOutTime, ClaimsVerificationStatService claimsVerificationStatService) {
		 List<voOrderProduct> orderGoods = new ArrayList<voOrderProduct>();
		if ( orderId > 0) {
			StringBuilder sqlBuilder = new StringBuilder("select p.name,p.id product_id,uopsh.count,uopsh.dprice,uopsh.price,uopsh.order_id order_id");
			sqlBuilder.append(" from product p join ").append(tableName).append(" uopsh on p.id = uopsh.product_id").append(" where p.parent_id2 not in(835,836) ")
					  .append(" and uopsh.order_id = ").append(orderId);
			Map<Integer,Integer> orderMap = new HashMap<Integer,Integer>();
			//查询参数和类型
			List<Object[]> paramAndTypeMap = new LinkedList<Object[]>();
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				rs = this.dbOp.executeQuery(sqlBuilder.toString());
				while (rs.next()) {
					voOrderProduct orp = new voOrderProduct();
					orp.setId(rs.getInt("product_id"));
					orp.setProductId(rs.getInt("product_id"));
					orp.setName(rs.getString("name"));
					orp.setCount(rs.getInt("count"));
					orp.setDiscountPrice(rs.getFloat("dprice"));
					orp.setPrice5(rs.getFloat("price"));
					orderGoods.add(orp);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			List<voOrderProduct> products = new ArrayList<voOrderProduct>();
			//设置商品出库时间
				List<voOrderProduct> proList = orderGoods;
				for (voOrderProduct vp : proList) {
					// 仓库时间
						vp.setBjStockin(stockOutTime);
				}
				products.addAll(proList);
			// 计算采购平均价
				// 计算采购平均价
				Map<Integer, List<voOrderProduct>> avgPrice = claimsVerificationStatService.getProAvgePrice(products);
				outer:for (voOrderProduct vp : products) {
						List<voOrderProduct> vpList = avgPrice.get(vp.getId());
						inner:for (voOrderProduct vp1 : vpList) {
							if (Float.compare(vp1.getPrice(), 0f) != 0 ) {
								vp.setPrice(vp1.getPrice());
								break inner;
							}
						}
					}
		}
		return orderGoods;
	}
	
	/**
	 * 检查包裹的 异常类型 
	 * @param orderStockBean
	 * @param list
	 * @param scanMap
	 * @param map
	 * @return  异常类型    0,是正常， 1，是缺失了商品， 2， 是个多出多了商品， 3， 是有错件
	 */
	private int checkPackageTypeResult(OrderStockBean orderStockBean,
			List<OrderStockProductBean> list, Map<Integer, Integer> scanMap,
			Map<Integer, String> map) {
		int result = 0;
		int x = list.size();
		boolean more = false;
		boolean less = false;
		for( int i = 0; i < x; i ++ ) {
			OrderStockProductBean ospBean = list.get(i);
			//先看有没有 扫描到该商品
			if( scanMap.containsKey(ospBean.getProductId())) {
				//在看数量是否正确
				int count = scanMap.get(ospBean.getProductId());
				int countCompare = getCountResult(count, ospBean);
				if( countCompare == 1 ) {
					more = true;
				} else if( countCompare == 2 ) {
					less = true;
				}
			} else {
				less = true;
			}
		}
		//看扫描商品是否多于出库单商品
		Iterator<Integer> itr = scanMap.keySet().iterator();
		for( ; itr.hasNext(); ) {
			Integer productId = itr.next();
			if( !map.containsKey(productId)) {
				more = true;
			} 
		}
		
		if( !more && !less ) {
			result = 0;
		} 
		if( less ) {
			result = 1;
		} 
		if( more ) {
			result = 2;
		} 
		if( less && more ) {
			result = 3;
		}
		return result;
	}
	
	/**
	 * 检查 如果是 扫描商品  和 出库单商品中都存在的商品，看数量是否对的上
	 * @param count
	 * @param ospBean
	 * @return  如果 一样 返回0， 扫描数比出库单多 1，  扫描数比出库单少 2
	 */
	private int getCountResult(int count,
			OrderStockProductBean ospBean) {
		int result = 0;
		int difference = count - ospBean.getStockoutCount();
		if( difference > 0 ) {
			result = 1;
		} else if( difference == 0 ) {
			result = 0;
		} else if ( difference < 0 ) {
			result = 2;
		}
		return result;
	}

	public voProduct getProductWithOriName(int productId) {
		
		ResultSet rs = null;
		PreparedStatement prod = null;
		voProduct result = null;
		String sql = "select oriname from product where id = ?";
		try {
			prod = this.dbOp.getConn().prepareStatement(sql);
			prod.setInt(1, productId);
			rs = prod.executeQuery();
			if( rs.next()) {
				result = new voProduct();
				result.setId(productId);
				result.setOriname(rs.getString("oriname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if( rs != null ) {
				try {rs.close();} catch (SQLException e) {}
				rs = null;
			}
			if( prod != null ) {
				try {prod.close();} catch (SQLException e) {}
				prod = null;
			}
		}
		return result;
	}

	/**
	 * 根据 理赔核销单内部的商品和数量以及理赔方式 来计算 总理赔价
	 * @param cvpList
	 * @param priceMap
	 * @return
	 * @throws MyException 
	 */
	public float calculateTotalClaimsPrice(List<ClaimsVerificationProductBean> cvpList,
			Map<String, Float> priceMap, voOrder order) throws MyException {
		float result = 0f;
		boolean carrige3 = false;
		for(ClaimsVerificationProductBean cvp : cvpList ) {
			if( cvp.getClaimsType() == ClaimsVerificationProductBean.CLAIMS_TYPE2 ) {
				result = priceMap.get("MAIL");
				carrige3 = true;
				break;
			}
			//在理赔方式 为 sku理赔或者 整单理赔是  因为两者计算逻辑是一致的  用统一个算法
			if( cvp.getClaimsType() == ClaimsVerificationProductBean.CLAIMS_TYPE1 || cvp.getClaimsType() == ClaimsVerificationProductBean.CLAIMS_TYPE0) {
				if( priceMap.containsKey("SKU_"+cvp.getProduct().getId() ) ) {
					float singlePrice = priceMap.get("SKU_"+cvp.getProduct().getId());
					float skuTotalPrice = Arith.mul(singlePrice, (float)cvp.getCount());
					result = Arith.add( result, skuTotalPrice);
				}
			}
			//在理赔方式为 按包装理赔的时候
			if( cvp.getClaimsType() == ClaimsVerificationProductBean.CLAIMS_TYPE3 ) {
				float singlePackPrice = this.getPackageClaimsPrice(cvp.getProduct().getId());
				float packTotalPrice = singlePackPrice*cvp.getCount();
				result = Arith.add( result, packTotalPrice);
			}
		}
		if( !carrige3 && !order.getCode().startsWith("S") ) {
			if( Float.compare(result, order.getDprice()) > 0  ) {
				result = order.getDprice();
			} 
		}
		return result;
	}

	public void auditClaimsVerification(int id, int yesno, voUser user) throws MyException {
		WareService wareService = new WareService(this.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		ReturnPackageLogService packageLog = new ReturnPackageLogService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		//检验 要提交的 理赔单是否存在
		ClaimsVerificationBean cvBean = this.getClaimsVerification("id=" + id);
		if( cvBean == null ) {
			throw new MyException("没有这个理赔单！");
		}
		//验证状态， 只有为 未处理的可以继续进行编辑的。
		if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_CONFIRM) {
			throw new MyException("理赔单:"+cvBean.getCode()+"状态不是已提交，不可以审核！");
		}
		// 看订单是否存在
		voOrder vorder = wareService.getOrder("code='"+ cvBean.getOrderCode() +"'");
		if( vorder == null ) {
			throw new MyException("没有找到理赔单:"+cvBean.getCode()+"对应的订单信息！");
		}
		if( vorder.getStatus() != 11) {
			throw new MyException("理赔单:"+cvBean.getCode()+"对应的订单不是已退回状态，不能操作！");
		}
		//看订单包裹是否可以查到
		List auditPackageList = istockService
		.getAuditPackageList("order_code='" + vorder.getCode() + "'", -1, -1, "id asc"); 
		if( auditPackageList == null || auditPackageList.size() == 0 ) {
			throw new MyException("没有找到理赔单:"+cvBean.getCode()+"对应的包裹单信息！");
		}
		//看订单是否入过 退货库
		ReturnedPackageBean rpBean = (ReturnedPackageBean) statService.getReturnedPackage("order_code='" + vorder.getCode()+"' and claims_verification_id=" + cvBean.getId() + " and area = " + cvBean.getWareArea());
		if( rpBean == null ) {
			throw new MyException("未找到理赔单:"+cvBean.getCode()+"关联的退货包裹，不可以审核！");
		}
		if( yesno == 1 ) {
			if( Float.compare(cvBean.getPrice(), 0f) == 0 ) {
				throw new MyException("理赔单:"+cvBean.getCode()+"理赔金额为0,请修改后再进行审核！");
			}
		}
		// 获取理赔商品列表  如果列表大小为0 就不允许提交
		List cvpList = this.getClaimsVerificationProductList("claims_verification_id = " + cvBean.getId(), -1, -1, "id asc");
		cvBean.setClaimsVerificationProductList(cvpList);
		if( cvpList == null || cvpList.size() == 0 ) {
			throw new MyException("理赔单:"+cvBean.getCode()+"中并没有添加任何商品，不可以审核!");
		} else {
			if( yesno == 0 ) {
				//审核不通过 ，
				String set = "status="+ClaimsVerificationBean.CLAIMS_AUDIT_FAIL +", audit_user_name='" + user.getUsername() + "', audit_user_id="+user.getId() + ", audit_time='" + DateUtil.getNow()+"'";
				if( !this.updateClaimsVerification(set, "id=" + cvBean.getId())) {
					throw new MyException("修改理赔单:"+cvBean.getCode()+"状态时，数据库操作失败！");
				}
				if( !statService.updateReturnedPackage("claims_verification_id = 0", "id=" + rpBean.getId())) {
					throw new MyException("修改理赔单:"+cvBean.getCode()+"状态时，数据库操作失败！");
				}
			} else if ( yesno == 1) {
				//审核通过， 把理赔单加入到 退货包裹列表
				String bsbyCodes = "";
				for( int i = 0; i < cvpList.size(); i ++ ) {
					ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) cvpList.get(i);
					voProduct product = wareService.getProduct(cvpBean.getProductCode());
					if( cvpBean.getExist() == 0 ) {
						if( cvpBean.getCount() > 0 ){
							//在这一部分 需要加入 添加报损单的 地方.....
							Object result = this.createBsOperation(CargoInfoBean.STOCKTYPE_RETURN, cvBean.getWareArea(), user);
							if( result instanceof String ) {
								throw new MyException((String)result);
							} else {
								//添加商品
								Object ciBean = this.getCargoCodeForSure(cvBean.getWareArea(), ProductStockBean.STOCKTYPE_RETURN,product.getId(), CargoInfoBean.STORE_TYPE2, cargoService);
								if( ciBean instanceof String) {
									throw new MyException((String)ciBean);
								}
								BsbyOperationnoteBean bsbyOperationnoteBean = (BsbyOperationnoteBean)result;
								CargoInfoBean cargoInfo = (CargoInfoBean) ciBean;
								Object bsbyProductBean = this.addProductToBsOperation(product, cvpBean.getCount(), bsbyOperationnoteBean, cvBean.getWareArea(),cargoInfo, user);
								if( bsbyProductBean instanceof String ) {
									throw new MyException((String) bsbyProductBean);
								}
								//一次审核 锁库存操作
								String res = this.auditBsbyOperationnote(bsbyOperationnoteBean,user);
								if( !res.equals("SUCCESS")) {
									throw new MyException(res);
								}
								bsbyCodes += bsbyOperationnoteBean.getReceipts_number()+",";
							}
						} else {
							throw new MyException("理赔单：:"+cvBean.getCode()+"中商品"+ cvpBean.getProductCode() +"数量有误，不可以提交!");
						}
					}
				}
				
				if( !statService.updateReturnedPackage("claims_verification_id=" + cvBean.getId(), "id=" + rpBean.getId())) {
					throw new MyException("理赔单:"+cvBean.getCode()+"取消退货包裹关联时，数据库操作失败！");
				}
				//修改理赔单状态
				String set = "status="+ClaimsVerificationBean.CLAIMS_AUDIT +", audit_user_name='" + user.getUsername() + "', audit_user_id="+user.getId() + ", audit_time='" + DateUtil.getNow()+"'" + ", bsby_codes='" + bsbyCodes+"'";
				if( !this.updateClaimsVerification(set, "id=" + cvBean.getId())) {
					throw new MyException("修改理赔单:"+cvBean.getCode()+"状态时，数据库操作失败！");
				}
				if (!packageLog.addReturnPackageLog("审核理赔单", user, vorder.getCode())) {
					throw new MyException("添加退货日志失败！");
				}
			}
		}
	}
	
}