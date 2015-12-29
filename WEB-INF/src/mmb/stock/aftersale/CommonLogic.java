package mmb.stock.aftersale;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mmb.easyui.Json;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.msg.TemplateMarker;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.IMEIService;
import mmb.ware.WareService;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.impl.ProductStockServiceImpl;
import adultadmin.service.infc.IAfterSalesService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.chinasms.sms.SenderSMS3;
import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceSaleBaseDataService;

/**
 * PDA和web共用的业务逻辑功能
 * 
 */
public class CommonLogic {
	private static byte[] lock = new byte[0];

	/**
	 * 客户寄回签收包裹
	 * 
	 * @author mengqy
	 * 
	 * @param deliverId
	 *            快递公司id
	 * @param returnType
	 *            寄回类型 0,寄付 1,到付
	 * @param freight
	 *            运费金额
	 * @param pakcageCode
	 *            包裹单号
	 * @param user
	 *            当前操作用户
	 * @param areaId
	 * 			  地区id 
	 * @return 操作结果，操作成功返回null，操作失败返回原因
	 */
	public String addDetectPackageBean(int deliverId, int returnType, float freight, String packageCode, voUser user, int areaId) {
		if (deliverId <= 0)
			return "请选择快递公司!";
		if (packageCode == null || packageCode.length() == 0)
			return "请扫描包裹单号";

		packageCode = StringUtil.dealParam(packageCode);

		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			try {
				AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
				if(!service.checkAfterSaleUserGroup(user, areaId)){
					return "没有该地区售后仓内作业权限!";
				}
				// 校验是否已签收
				if (service.getAfterSaleDetectPackage(" package_code = '" + packageCode + "' ") != null)
					return "该包裹已签收!";

				dbOp.startTransaction();

				AfterSaleDetectPackageBean bean = new AfterSaleDetectPackageBean();
				bean.setCreateUserId(user.getId());
				bean.setCreateUserName(user.getUsername());
				bean.setCreateDatetime(DateUtil.getNow());
				bean.setDeliverId(deliverId);
				bean.setFreight(freight);
				bean.setPackageCode(packageCode);
				bean.setReturnType(returnType);
				bean.setStatus(AfterSaleDetectPackageBean.STATUS0);
				bean.setAreaId(areaId);

				if (!service.addAfterSaleDetectPackage(bean)) {
					dbOp.rollbackTransaction();
					return "数据库操作失败：addAfterSaleDetectPackage";
				}

				// 添加 销售后台用的包裹单列表after_sale_warehource_package_lis
				String sql = " INSERT INTO after_sale_warehource_package_list ( post_type, pay_type, freight, deliver_id, package_code, create_user_id, create_user_name, create_datetime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
				if (!dbOp.prepareStatement(sql)) {
					dbOp.rollbackTransaction();
					return "数据库操作失败";
				}
				PreparedStatement pstmt = dbOp.getPStmt();
				pstmt.setInt(1, 2);// post_type, 2 寄回
				pstmt.setInt(2, returnType + 1);// pay_type, 1寄付，2到付
				pstmt.setFloat(3, freight);
				pstmt.setInt(4, deliverId);
				pstmt.setString(5, packageCode);
				pstmt.setInt(6, user.getId());
				pstmt.setString(7, user.getUsername());
				pstmt.setString(8, DateUtil.getNow());
				if (pstmt.executeUpdate() <= 0) {
					dbOp.rollbackTransaction();
					return " 数据库操作失败：INSERT INTO after_sale_warehource_package_list ";
				}

				if (!service.writeAfterSaleLog(user, "PDA签收客户寄回包裹,包裹号：" + packageCode, 1, AfterSaleLogBean.TYPE1,packageCode,null)) {
					dbOp.rollbackTransaction();
					return "写售后日志失败";
				}
				
				dbOp.commitTransaction();
				return null;
			} catch (Exception e) {
				dbOp.rollbackTransaction();
				e.printStackTrace();
				return "发生异常!";
			} finally {
				dbOp.release();
			}
		}
	}

	/**
	 * 签收未妥投的包裹单号
	 * 
	 * @author mengqy
	 * @param packageCode
	 *            未妥投的包裹单号
	 * @param user
	 *            当前操作用户
	 * @param flag 0 PDA, 1 web
	 * @return 操作结果，操作成功返回null，操作失败返回原因
	 */
	public String receiveBackuserPackage(String packageCode, voUser user, int flag) {
		// 1，加库存，进销存卡片，批次，进销存卡片类型：售后原品退回，售后维修商品退回。（新增）。
		// 2，寄回包裹列表状态改为已退回。
		packageCode = StringUtil.dealParam(packageCode);
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			try {
				dbOp.startTransaction();
					
				String results = this.changeStatusForReceiveBackuserPackage(dbOp, packageCode, user);
				if (results != null) {
					dbOp.rollbackTransaction();
					return results;
				}

				results = this.addStockCardBatchForReceiveBackuserPackage(dbOp, packageCode, user,flag);
				if (results != null) {
					dbOp.rollbackTransaction();
					return results;
				}
				dbOp.commitTransaction();
				return null;
			} catch (Exception e) {
				dbOp.rollbackTransaction();
				e.printStackTrace();
				return "发生异常";
			} finally {
				dbOp.release();
			}
		}
	}
	/**
	 * 未妥投包裹签收 修改状态
	 * 
	 * @param dbOp
	 * @param packageCode
	 * @param user
	 * @return
	 */
	private String changeStatusForReceiveBackuserPackage(DbOperation dbOp, String packageCode, voUser user) {

		AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);

		String condition = " package_code = '" + packageCode + "'";
		AfterSaleBackUserPackage backPackage = service.getAfterSaleBackUserPackage(condition);
		if (backPackage == null) {
			return "包裹单[" + packageCode + "]不是寄出包裹，不可以签收!";
		}
		if(!service.checkAfterSaleUserGroup(user, backPackage.getAreaId())){
			return "没有该地区售后仓内作业权限!";
		}
		if (backPackage.status == AfterSaleBackUserPackage.STATUS2) {
			return "包裹单[" + packageCode + "]已签收完毕!";
		}

		String set = " status = " + AfterSaleBackUserPackage.STATUS2 + " , receive_datetime = '" + DateUtil.getNow() + "' ";
		set += " , receive_user_id = " + user.getId() + " , receive_user_name = '" + StringUtil.dealParam(user.getUsername()) + "' ";

		if (!service.updateAfterSaleBackUserPackage(set, " id = " + backPackage.getId())) {
			return "数据库操作失败:updateAfterSaleBackUserPackage";
		}

		return null;
	}

	/**
	 * 未妥投包裹签收 添加库存 进销存卡片
	 * 
	 * @param dbOp
	 * @param packageCode
	 * @return
	 * @throws SQLException
	 */
	private String addStockCardBatchForReceiveBackuserPackage(DbOperation dbOp, String packageCode, voUser user,int flag) throws SQLException {
		String queryProducts = " SELECT product.product_id, product.type, cargo.area_id, cargo.stock_type AS stockType ";
		queryProducts += " , detect.cargo_whole_code, detect.code, detect.id, detect.after_sale_order_id, detect.status ";
		queryProducts += " FROM after_sale_back_user_package AS package, after_sale_back_user_product AS product, after_sale_detect_product AS detect, cargo_info AS cargo ";
		queryProducts += " WHERE package.id = product.package_id AND product.after_sale_detect_product_id = detect.id ";
		queryProducts += " AND product.product_id = detect.product_id AND detect.cargo_whole_code = cargo.whole_code AND package.package_code = '" + packageCode + "'";

		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		ResultSet productsRs = dbOp.executeQuery(queryProducts);
		if (productsRs == null || !productsRs.next()) {
			if (productsRs != null)
				productsRs.close();
			return "未能查询到包裹单商品，签收失败!";
		}

		do {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("detectId", productsRs.getString("id"));
			map.put("orderId", productsRs.getString("after_sale_order_id"));
			map.put("detectStatus", productsRs.getString("status"));
			map.put("productId", productsRs.getString("product_id"));
			map.put("type", productsRs.getString("type"));
			map.put("area", productsRs.getString("area_id"));
			map.put("stockType", productsRs.getString("stockType"));
			map.put("cargoWholeCode", productsRs.getString("cargo_whole_code"));
			map.put("afCode", productsRs.getString("code"));
			list.add(map);
		} while (productsRs.next());
		productsRs.close();

		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		WareService wareService = new WareService(dbOp);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);

		for (HashMap<String, String> map : list) {
			int detectId = StringUtil.parstInt(map.get("detectId"));
			int orderId = StringUtil.parstInt(map.get("orderId"));
			int detectStatus = StringUtil.parstInt(map.get("detectStatus"));
			int productId = StringUtil.parstInt(map.get("productId"));
			int type = StringUtil.parstInt(map.get("type"));
			int area = StringUtil.parstInt(map.get("area"));
			int stockType = StringUtil.parstInt(map.get("stockType"));
			String cargoWholeCode = map.get("cargoWholeCode");
			String afCode = map.get("afCode");

			int cardType = StockCardBean.CARDTYPE_AFTERSALE_OLDPRODUCT_RETURN;
			int cargoCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_OLDPRODUCT_RETURN;

			// 0，原品返回
			// 1，维修寄回
			// 维修寄回
			if (type == 1) {
				cardType = StockCardBean.CARDTYPE_AFTERSALE_REPAIRPRODUCT_RETURN;
				cargoCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_REPAIRPRODUCT_RETURN;
			}else if(type==2){
				cardType = StockCardBean.CARDTYPE_AFTERSALE_REPLACE_RETURN;
				cargoCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_REPLACE_RETURN;
			}

			// 8, 付费维修已完成
			// 15，付费维修已退回

			// 9, 保修已完成
			// 16，保修维修已退回

			// 11，原品已返回
			// 17，原品已退回
			// 售后处理单状态, 保修已完成——》保修维修已退回
			// 售后处理单状态, 原品已返回——》原品已退回
			// 售后处理单状态，付费维修已完成——》付费维修已退回
			// 售后处理单状态，维修换新机已完成——》维修换新机已退回
			int status = 0;
			if (detectStatus == 8) {
				status = 15;
			} else if (detectStatus == 11) {
				status = 17;
			} else if (detectStatus == 9) {
				status = 16;
			}else if(detectStatus == 21){
				status = 22;
			}

			if (status == 0) {
				return "售后处理单状态不正确";
			}

			if (!afService.updateAfterSaleDetectProduct(" status = " + status, " id = " + detectId)) {
				return "数据库操作失败：updateAfterSaleDetectProduct";
			}

			// 修改售后单状态
			if (!afterSaleService.updateAfterSaleOrder(" status = " + AfterSaleOrderBean.STATUS_售后未妥投, " id = " + orderId)) {
				return "数据库操作失败：updateAfterSaleOrder";
			}

			// 销售后台处理单 after_sale_warehource_product_records,
			// after_sale_order_id
			// 状态未妥投 status = 8
			StringBuffer sb = new StringBuffer();
			sb.append(" UPDATE after_sale_warehource_product_records ");
			sb.append(" SET status = 8, ");
			sb.append(" modify_user_id = ");
			sb.append(user.getId());
			sb.append(" ,  modify_user_name = '");
			sb.append(StringUtil.dealParam(user.getUsername()));
			sb.append("' , modify_datetime = '");
			sb.append(DateUtil.getNow());

			sb.append("' WHERE code = '");
			sb.append(afCode);
			sb.append("' ");
			if (!dbOp.executeUpdate(sb.toString())) {
				return "数据库操作失败：更新销售后台处理单状态失败";
			}

			// 增加商品库存
			ProductStockBean inProductStock = psService.getProductStock("product_id = " + productId + " and area = " + area + " and type = " + stockType);
			if (!psService.updateProductStockCount(inProductStock.getId(), 1)) {
				return "数据库操作失败:updateProductStockCount!";
			}

			// 增加货位库存
			CargoInfoBean inCargoInfo = cargoService.getCargoInfo(" whole_code = '" + cargoWholeCode + "' ");
			CargoProductStockBean inCargoProductStock = cargoService.getCargoProductStock(" product_id = " + productId + " AND cargo_id = " + inCargoInfo.getId());
			if (!cargoService.updateCargoProductStockCount(inCargoProductStock.getId(), 1)) {
				return "数据库操作失败:updateCargoProductStockCount!";
			}

			voProduct vProduct = wareService.getProduct(productId);
			vProduct.setPsList(psService.getProductStockList("product_id = " + vProduct.getId(), -1, -1, "id asc"));

			// 商品进销存卡片
			StockCardBean insc = new StockCardBean();
			insc.setCardType(cardType);
			insc.setCode(afCode); // 售后处理单号
			insc.setCreateDatetime(DateUtil.getNow());
			insc.setStockType(stockType);
			insc.setStockArea(area);
			insc.setProductId(productId);
			insc.setStockId(inProductStock.getId());
			insc.setStockInCount(1);
			insc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			insc.setCurrentStock(vProduct.getStock(insc.getStockArea(), insc.getStockType()) + vProduct.getLockCount(insc.getStockArea(), insc.getStockType()));
			insc.setStockAllArea(vProduct.getStock(insc.getStockArea()) + vProduct.getLockCount(insc.getStockType()));
			insc.setStockAllType(vProduct.getStockAllType(insc.getStockType()) + vProduct.getLockCountAllType(insc.getStockType()));
			insc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			insc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
			insc.setAllStockPriceSum((new BigDecimal(insc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(insc.getStockPrice()))).doubleValue());
			if (!psService.addStockCard(insc)) {
				return "数据库操作失败：addStockCard";
			}

			// 货位进销存卡片
			CargoStockCardBean incsc = new CargoStockCardBean();
			incsc.setCardType(cargoCardType);
			incsc.setCode(afCode); // 售后处理单号
			incsc.setCreateDatetime(DateUtil.getNow());
			incsc.setStockType(stockType);
			incsc.setStockArea(area);
			incsc.setProductId(vProduct.getId());
			incsc.setStockId(inCargoProductStock.getId());
			incsc.setStockInCount(1);
			incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
			incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			incsc.setCurrentCargoStock(inCargoProductStock.getStockCount() + inCargoProductStock.getStockLockCount());
			incsc.setCargoStoreType(inCargoInfo.getStoreType());
			incsc.setCargoWholeCode(inCargoInfo.getWholeCode());
			incsc.setStockPrice(vProduct.getPrice5());
			incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
			if (!cargoService.addCargoStockCard(incsc)) {
				return "数据库操作失败：addCargoStockCard";
			}
			AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			if (!service.writeAfterSaleLog(user, (flag == 0 ? "PDA" : "Web") + "包裹单[" + packageCode + "]售后处理单[" + afCode +"]未妥投，已被售后库人员签收", 1, AfterSaleLogBean.TYPE3,afCode,null)) {
				dbOp.rollbackTransaction();
				return "写售后日志失败";
			}
			//相关配件寄回入库
			List<Map<String,Integer>> fittingList = afService.getAfterSaleDetectFitting(detectId);
			if(fittingList!=null && fittingList.size()>0){
				for(int i=0;i<fittingList.size();i++){
					Map<String,Integer> fittingMap = fittingList.get(i);
					int fittingId = fittingMap.get("fittingId");
					voProduct fitting = wareService.getProduct(fittingId);
					int intactCount = fittingMap.get("intactCount");
					int damageCount = fittingMap.get("damageCount");
					CargoInfoBean intactCargo = cargoService.getCargoInfo("area_id=" +  area +" and stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING + " and store_type=" 
							+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE3);
					if(intactCargo==null) {
						return "未找到完好配件货位！";
					}
					CargoInfoBean badCargo = cargoService.getCargoInfo("area_id=" +  area +" and stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING + " and store_type=" 
							+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE4);
					if(badCargo==null) {
						return "未找到损坏配件货位！";
					}
					int stockCardType = StockCardBean.CARDTYPE_FITTING_BACKUSER_RETURN;
					int cargoStockCardType = CargoStockCardBean.CARDTYPE_FITTING_BACKUSER_RETURN;
					if(intactCount>0){
						String tip =opearteFittingStockIn(psService, cargoService, area, afCode,fitting, intactCount, intactCargo,stockCardType,cargoStockCardType);
						if(tip!=null && tip.length()>0){
							return tip;
						}
					}
					if(damageCount>0){
						String tip =opearteFittingStockIn(psService, cargoService, area, afCode,fitting, damageCount, badCargo,stockCardType,cargoStockCardType);
						if(tip!=null && tip.length()>0){
							return tip;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * 对配件入库库存进行操作
	 * @param psService
	 * @param cargoService
	 * @param area
	 * @param stockType
	 * @param afCode
	 * @param fitting
	 * @param count
	 * @param cargo
	 * @return
	 * @author lining
	* @date 2014-7-8
	 */
	private String opearteFittingStockIn(IProductStockService psService, ICargoService cargoService, int area,  
			String afCode,voProduct fitting, int count, CargoInfoBean cargo,int stockCardType,int cargoStockCardType) {
		// 增加商品库存
		ProductStockBean productStock = psService.getProductStock("product_id = " + fitting.getId() + " and area = " + area + " and type = " + cargo.getStockType());
		if (!psService.updateProductStockCount(productStock.getId(), count)) {
			return "数据库操作失败:updateProductStockCount!";
		}
		
		// 增加货位库存
		CargoProductStockBean cargoProductStock = cargoService.getCargoProductStock(" product_id = " + fitting.getId() + " AND cargo_id = " + cargo.getId());
		if (!cargoService.updateCargoProductStockCount(cargoProductStock.getId(), count)) {
			return "数据库操作失败:updateCargoProductStockCount!";
		}

		fitting.setPsList(psService.getProductStockList("product_id = " + fitting.getId(), -1, -1, "id asc"));

		// 商品进销存卡片
		StockCardBean stockCard = new StockCardBean();
		stockCard.setCardType(stockCardType);
		stockCard.setCode(afCode); // 售后处理单号
		stockCard.setCreateDatetime(DateUtil.getNow());
		stockCard.setStockType(cargo.getStockType());
		stockCard.setStockArea(area);
		stockCard.setProductId(fitting.getId());
		stockCard.setStockId(productStock.getId());
		stockCard.setStockInCount(count);
		stockCard.setStockInPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(fitting.getPrice5()))).doubleValue());
		stockCard.setCurrentStock(fitting.getStock(stockCard.getStockArea(), stockCard.getStockType()) + fitting.getLockCount(stockCard.getStockArea(), stockCard.getStockType()));
		stockCard.setStockAllArea(fitting.getStock(stockCard.getStockArea()) + fitting.getLockCount(stockCard.getStockType()));
		stockCard.setStockAllType(fitting.getStockAllType(stockCard.getStockType()) + fitting.getLockCountAllType(stockCard.getStockType()));
		stockCard.setAllStock(fitting.getStockAll() + fitting.getLockCountAll());
		stockCard.setStockPrice(fitting.getPrice5());// 新的库存价格，每次入库都要计算
		stockCard.setAllStockPriceSum((new BigDecimal(stockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(stockCard.getStockPrice()))).doubleValue());
		if (!psService.addStockCard(stockCard)) {
			return "数据库操作失败：addStockCard";
		}

		// 货位进销存卡片
		CargoStockCardBean cargoStockCard = new CargoStockCardBean();
		cargoStockCard.setCardType(cargoStockCardType);
		cargoStockCard.setCode(afCode); // 售后处理单号
		cargoStockCard.setCreateDatetime(DateUtil.getNow());
		cargoStockCard.setStockType(cargo.getStockType());
		cargoStockCard.setStockArea(area);
		cargoStockCard.setProductId(fitting.getId());
		cargoStockCard.setStockId(cargoProductStock.getId());
		cargoStockCard.setStockInCount(count);
		cargoStockCard.setStockInPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(fitting.getPrice5()))).doubleValue());
		cargoStockCard.setCurrentStock(fitting.getStock(cargoStockCard.getStockArea(), cargoStockCard.getStockType()) + fitting.getLockCount(cargoStockCard.getStockArea(), cargoStockCard.getStockType()));
		cargoStockCard.setAllStock(fitting.getStockAll() + fitting.getLockCountAll());
		cargoStockCard.setCurrentCargoStock(cargoProductStock.getStockCount() + cargoProductStock.getStockLockCount());
		cargoStockCard.setCargoStoreType(cargo.getStoreType());
		cargoStockCard.setCargoWholeCode(cargo.getWholeCode());
		cargoStockCard.setStockPrice(fitting.getPrice5());
		cargoStockCard.setAllStockPriceSum((new BigDecimal(cargoStockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(cargoStockCard.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(cargoStockCard)) {
			return "数据库操作失败：addCargoStockCard";
		}
		return null;
	}

	
	
	/**
	 * 用户库 售后库 商品上架
	 * 
	 * @author mengqy
	 * @param cargoWholeCode
	 *            目的货位
	 * @param afCodes
	 *            售后处理单号列表(可能为多个)
	 * @param area
	 *            操作的库地区
	 * @param user
	 *            当前操作用户
	 * @param type
	 *            类别，1用户库商品上架 2售后库商品上架
	 * @return 操作结果，操作成功返回null，操作失败返回原因
	 */
	public String productUpShelf(String cargoWholeCode, List<String> afCodes, int area, voUser user, int type) {
		cargoWholeCode = StringUtil.dealParam(cargoWholeCode);
		if (afCodes == null || afCodes.size() == 0)
			return "请扫描售后处理单号";

		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			AfStockService stockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			WareService wareService = new WareService(dbOp);
			try {
				if(!stockService.checkAfterSaleUserGroup(user, area)){
					return "没有该地区售后仓内作业权限";
				}
				CargoInfoBean inCargoInfo = cargoService.getCargoInfo(" whole_code = '" + cargoWholeCode + "' AND status IN (0, 1) ");
				if (inCargoInfo == null) {
					return "目的货位不可用";
				}
				if (inCargoInfo.getAreaId() != area) {
					return "目的货位不在本地区，不可用";
				}

				if (type == 1) {
					if(inCargoInfo.getStockType() != CargoInfoBean.STOCKTYPE_CUSTOMER)
						return "用户库商品上架只能处理用户库商品";
				} else {
					if(inCargoInfo.getStockType() != CargoInfoBean.STOCKTYPE_AFTER_SALE)
						return "售后库商品上架只能处理售后库商品";
				}
				
				dbOp.startTransaction();

				if (inCargoInfo.getStatus() == CargoInfoBean.STATUS1) {
					if (!cargoService.updateCargoInfo(" status = " + CargoInfoBean.STATUS0, " id = " + inCargoInfo.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateCargoInfo";
					}
				}

				int[] arrStatus = {8, 9, 11, 21};
				for (String afterSaleCode : afCodes) {

					AfterSaleDetectProductBean product = stockService.getAfterSaleDetectProduct(" code = '" + afterSaleCode + "' ");
					if (product == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afterSaleCode + "]";
					}

					for (int n : arrStatus) {
						if (product.getStatus() == n) {
							dbOp.rollbackTransaction();
							return "售后处理单[" + afterSaleCode + "]已寄回用户，不可上架";
						}
					}
					
					CargoInfoBean outCargoInfo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' ");
					if (outCargoInfo == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afterSaleCode + "]所关联的货位";
					}

					if (inCargoInfo.getStockType() != outCargoInfo.getStockType()) {
						dbOp.rollbackTransaction();
						return "目的货位库类型和处理单关联货位库类型不一致";
					}
					
					if (inCargoInfo.getAreaId() != outCargoInfo.getAreaId()) {
						dbOp.rollbackTransaction();
						return "目的货位库地区和处理单关联货位库地区不一致";
					}

					if (outCargoInfo.getId() == inCargoInfo.getId()) {
						dbOp.rollbackTransaction();
						return "售后处理单[" + afterSaleCode + "]源货位和目的货位一致";
					}

					// 生成上架单
					CargoOperationBean cargoOper = new CargoOperationBean();
					cargoOper.setStockInType(inCargoInfo.getStoreType());
					cargoOper.setStockOutType(outCargoInfo.getStoreType());

					String results = addCargoOperForUpShelf(area, user, type, cargoService, afterSaleCode, cargoOper, stockService, product.getId());
					if (results != null) {
						dbOp.rollbackTransaction();
						return results;
					}

					results = changeParamForUpShelf(area, type, cargoService, stockService, psService, wareService, inCargoInfo, afterSaleCode, product, outCargoInfo, cargoOper);
					if (results != null) {
						dbOp.rollbackTransaction();
						return results;
					}
					
					if (!stockService.writeAfterSaleLog(user, (type == 1 ? "客户库" : "售后库") + "PDA商品上架,上架单号:" + cargoOper.getCode(), 1, AfterSaleLogBean.TYPE6,cargoOper.getCode(),null)) {
						dbOp.rollbackTransaction();
						return "写售后日志失败";
					}
				}
				dbOp.commitTransaction();
				return null;
			} catch (Exception e) {
				dbOp.rollbackTransaction();
				e.printStackTrace();
				return "发生异常";
			} finally {
				dbOp.release();
			}
		}
	}
	/**
	 * 待返厂商品下架
	 * @param wholeCode 目的货位
	 * @param afCodes 售后处理单号列表(可能为多个)
	 * @param areaId 操作的库地区
	 * @param user 当前操作用户
	 * @param stockType 库类型
	 * @return 操作结果，操作成功返回null，操作失败返回原因
	 * @author syuf
	 */
	public String productDownShelf(String wholeCode, List<String> afCodes, int areaId, voUser user, int stockType) {
		wholeCode = StringUtil.dealParam(wholeCode);
		if (afCodes == null || afCodes.size() == 0)
			return "请扫描售后处理单号";
		
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
			AfStockService stockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
			WareService wareService = new WareService(dbOp);
			try {
				if(!stockService.checkAfterSaleUserGroup(user, areaId)){
					return "没有该地区售后仓内作业权限";
				}
				CargoInfoBean inCargoInfo = cargoService.getCargoInfo(" store_type = " + CargoInfoBean.STORE_TYPE2 + " AND status IN (0, 1) AND stock_type = " + stockType + " AND area_id = " + areaId);
				if (inCargoInfo == null) {
					return "没有找到可用的目的货位";
				}
				dbOp.startTransaction();
				if (inCargoInfo.getStatus() == CargoInfoBean.STATUS1) {
					if (!cargoService.updateCargoInfo(" status = " + CargoInfoBean.STATUS0, " id = " + inCargoInfo.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateCargoInfo";
					}
				}
				for (String afterSaleCode : afCodes) {
					AfterSaleDetectProductBean detectProduct = stockService.getAfterSaleDetectProduct(" code = '" + afterSaleCode + "' ");
					if (detectProduct == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afterSaleCode + "]";
					}
					CargoInfoBean outCargoInfo = cargoService.getCargoInfo(" whole_code = '" + wholeCode + "' ");
					if (outCargoInfo == null) {
						dbOp.rollbackTransaction();
						return "找不到货位号[" + wholeCode + "]的相关信息";
					}
					if (inCargoInfo.getStockType() != outCargoInfo.getStockType()) {
						dbOp.rollbackTransaction();
						return "目的货位库类型和处理单关联货位库类型不一致";
					}
					// 生成下架单
					CargoOperationBean cargoOper = new CargoOperationBean();
					cargoOper.setStockInType(inCargoInfo.getStoreType());
					cargoOper.setStockOutType(outCargoInfo.getStoreType());
					
					String results = addCargoOperForDownShelf(areaId, user, stockType, cargoService, afterSaleCode, cargoOper, stockService, detectProduct.getId());
					if (results != null) {
						dbOp.rollbackTransaction();
						return results;
					}
					
					results = changeParamForDownShelf(areaId, stockType, cargoService, stockService, psService, wareService, inCargoInfo, afterSaleCode, detectProduct, outCargoInfo, cargoOper);
					if (results != null) {
						dbOp.rollbackTransaction();
						return results;
					}
				}
				dbOp.commitTransaction();
				return null;
			} catch (Exception e) {
				dbOp.rollbackTransaction();
				e.printStackTrace();
				return "发生异常";
			} finally {
				dbOp.release();
			}
		}
	}

	/**
	 * 
	 * @param type
	 *            类别，1用户库商品上架 2售后库商品上架
	 * @return
	 */
	private String changeParamForUpShelf(int area, int type, ICargoService cargoService, AfStockService stockService, IProductStockService psService, WareService wareService, CargoInfoBean inCargoInfo, String afterSaleCode, AfterSaleDetectProductBean product, CargoInfoBean outCargoInfo, CargoOperationBean cargoOper) {

		// 修改货位库存
		CargoProductStockBean outCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + outCargoInfo.getId() + " AND product_id=" + product.getProductId());
		if (outCargoProductStock == null || outCargoProductStock.getStockCount() <= 0) {
			return "售后处理单[" + afterSaleCode + "]源货位库存不足";
		}
		if (!cargoService.updateCargoProductStockCount(outCargoProductStock.getId(), -1)) {
			return "数据库操作失败：updateCargoProductStockLockCount";
		}
		
		CargoProductStockBean inCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + inCargoInfo.getId() + " AND product_id=" + product.getProductId());
		if (inCargoProductStock == null) {
			inCargoProductStock = new CargoProductStockBean();
			inCargoProductStock.setCargoId(inCargoInfo.getId());
			inCargoProductStock.setProductId(product.getProductId());
			inCargoProductStock.setStockCount(1);
			inCargoProductStock.setStockLockCount(0);
			if (!cargoService.addCargoProductStock(inCargoProductStock)) {
				return "数据库操作失败：addCargoProductStock";
			}
			inCargoProductStock.setId(cargoService.getDbOp().getLastInsertId());
		} else {
			if (!cargoService.updateCargoProductStockCount(inCargoProductStock.getId(), 1)) {
				return "数据库操作失败：updateCargoProductStockCount";
			}
		}

		// 上架单商品
		CargoOperationCargoBean bean = new CargoOperationCargoBean();
		bean.setOperId(cargoOper.getId());
		bean.setProductId(product.getProductId());
		bean.setInCargoProductStockId(inCargoProductStock.getId());
		bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
		bean.setOutCargoProductStockId(outCargoProductStock.getId());
		bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
		bean.setStockCount(1);
		bean.setType(1);
		bean.setUseStatus(0);
		if (!cargoService.addCargoOperationCargo(bean)) {
			return "数据库操作失败：addCargoOperationCargo";
		}

		bean.setType(0);
		bean.setUseStatus(1);
		if (!cargoService.addCargoOperationCargo(bean)) {
			return "数据库操作失败：addCargoOperationCargo";
		}

		// 修改售后处理单所关联的货位
		if (!stockService.updateAfterSaleDetectProduct(" cargo_whole_code = '" + inCargoInfo.getWholeCode() + "' ", " id = " + product.getId())) {
			return "数据库操作失败: updateAfterSaleDetectProduct";
		}

		// 添加 货位进销存卡片
		voProduct vProduct = wareService.getProduct(product.getProductId());
		vProduct.setPsList(psService.getProductStockList("product_id=" + product.getProductId(), -1, -1, null));

		CargoStockCardBean outcsc = new CargoStockCardBean();
		outcsc.setCardType(type == 1 ? CargoStockCardBean.CARDTYPE_UPSHELF_FOR_USER : CargoStockCardBean.CARDTYPE_UPSHELF_FOR_AFTERSALE);
		outcsc.setCode(cargoOper.getCode());
		outcsc.setCreateDatetime(DateUtil.getNow());
		outcsc.setStockType(type == 1 ? CargoInfoBean.STOCKTYPE_CUSTOMER : CargoInfoBean.STOCKTYPE_AFTER_SALE);
		outcsc.setStockArea(area);
		outcsc.setProductId(product.getProductId());
		outcsc.setStockId(outCargoProductStock.getId());
		outcsc.setStockOutCount(1);
		outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
		outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount() + outCargoProductStock.getStockLockCount());
		outcsc.setCargoStoreType(outCargoInfo.getStoreType());
		outcsc.setCargoWholeCode(outCargoInfo.getWholeCode());
		outcsc.setStockPrice(vProduct.getPrice5());
		outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(outcsc)) {
			return "数据库操作失败：addCargoStockCard";
		}
		// 货位入库卡片
		CargoStockCardBean incsc = new CargoStockCardBean();
		incsc.setCardType(type == 1 ? CargoStockCardBean.CARDTYPE_UPSHELF_FOR_USER : CargoStockCardBean.CARDTYPE_UPSHELF_FOR_AFTERSALE);
		incsc.setCode(cargoOper.getCode());
		incsc.setCreateDatetime(DateUtil.getNow());
		incsc.setStockType(type == 1 ? CargoInfoBean.STOCKTYPE_CUSTOMER : CargoInfoBean.STOCKTYPE_AFTER_SALE);
		incsc.setStockArea(area);
		incsc.setProductId(product.getProductId());
		incsc.setStockId(inCargoProductStock.getId());
		incsc.setStockInCount(1);
		incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
		incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		incsc.setCurrentCargoStock(inCargoProductStock.getStockCount() + inCargoProductStock.getStockLockCount());
		incsc.setCargoStoreType(inCargoInfo.getStoreType());
		incsc.setCargoWholeCode(inCargoInfo.getWholeCode());
		incsc.setStockPrice(vProduct.getPrice5());
		incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(incsc)) {
			return "数据库操作失败：addCargoStockCard";
		}
		return null;
	}
	/**
	 * 添加作业单(下架)商品及添加、修改库存、货位库存、进销存卡片等
	 * @param areaId 库地区
	 * @param stockType 库类型
	 * @author syuf
	 */
	private String changeParamForDownShelf(int areaId, int stockType, ICargoService cargoService, AfStockService stockService, IProductStockService psService, WareService wareService, CargoInfoBean inCargoInfo, String afterSaleCode, AfterSaleDetectProductBean product, CargoInfoBean outCargoInfo, CargoOperationBean cargoOper) {
		
		// 修改货位库存
		CargoProductStockBean outCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + outCargoInfo.getId() + " AND product_id=" + product.getProductId());
		if (outCargoProductStock == null || outCargoProductStock.getStockCount() <= 0) {
			return "售后处理单[" + afterSaleCode + "]源货位库存不足";
		}
		if (!cargoService.updateCargoProductStockCount(outCargoProductStock.getId(), -1)) {
			return "数据库操作失败：updateCargoProductStockLockCount";
		}
		
		CargoProductStockBean inCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + inCargoInfo.getId() + " AND product_id=" + product.getProductId());
		if (inCargoProductStock == null) {
			inCargoProductStock = new CargoProductStockBean();
			inCargoProductStock.setCargoId(inCargoInfo.getId());
			inCargoProductStock.setProductId(product.getProductId());
			inCargoProductStock.setStockCount(1);
			inCargoProductStock.setStockLockCount(0);
			if (!cargoService.addCargoProductStock(inCargoProductStock)) {
				return "数据库操作失败：addCargoProductStock";
			}
			inCargoProductStock.setId(cargoService.getDbOp().getLastInsertId());
		} else {
			if (!cargoService.updateCargoProductStockCount(inCargoProductStock.getId(), 1)) {
				return "数据库操作失败：updateCargoProductStockCount";
			}
		}
		
		// 添加下架单商品
		CargoOperationCargoBean bean = new CargoOperationCargoBean();
		bean.setOperId(cargoOper.getId());
		bean.setProductId(product.getProductId());
		bean.setInCargoProductStockId(inCargoProductStock.getId());
		bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
		bean.setOutCargoProductStockId(outCargoProductStock.getId());
		bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
		bean.setStockCount(1);
		bean.setType(1);
		bean.setUseStatus(0);
		if (!cargoService.addCargoOperationCargo(bean)) {
			return "数据库操作失败：addCargoOperationCargo";
		}
		
		bean.setType(0);
		bean.setUseStatus(1);
		if (!cargoService.addCargoOperationCargo(bean)) {
			return "数据库操作失败：addCargoOperationCargo";
		}
		// 修改售后处理单所关联的货位
		if (!stockService.updateAfterSaleDetectProduct(" cargo_whole_code = '" + inCargoInfo.getWholeCode() + "' ", " id = " + product.getId())) {
			return "数据库操作失败: updateAfterSaleDetectProduct";
		}
		
		// 添加 货位进销存卡片
		voProduct vProduct = wareService.getProduct(product.getProductId());
		vProduct.setPsList(psService.getProductStockList("product_id=" + product.getProductId(), -1, -1, null));
		
		CargoStockCardBean outcsc = new CargoStockCardBean();
		outcsc.setCardType(stockType == 10 ? CargoStockCardBean.CARDTYPE_DOWNSHELF_FOR_USER : CargoStockCardBean.CARDTYPE_DOWNSHELF_FOR_AFTERSALE);
		outcsc.setCode(cargoOper.getCode());
		outcsc.setCreateDatetime(DateUtil.getNow());
		outcsc.setStockType(stockType == 10 ? CargoInfoBean.STOCKTYPE_CUSTOMER : CargoInfoBean.STOCKTYPE_AFTER_SALE);
		outcsc.setStockArea(areaId);
		outcsc.setProductId(product.getProductId());
		outcsc.setStockId(outCargoProductStock.getId());
		outcsc.setStockOutCount(1);
		outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
		outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount() + outCargoProductStock.getStockLockCount());
		outcsc.setCargoStoreType(outCargoInfo.getStoreType());
		outcsc.setCargoWholeCode(outCargoInfo.getWholeCode());
		outcsc.setStockPrice(vProduct.getPrice5());
		outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(outcsc)) {
			return "数据库操作失败：addCargoStockCard";
		}
		// 货位入库卡片
		CargoStockCardBean incsc = new CargoStockCardBean();
		incsc.setCardType(stockType == 10 ? CargoStockCardBean.CARDTYPE_DOWNSHELF_FOR_USER : CargoStockCardBean.CARDTYPE_DOWNSHELF_FOR_AFTERSALE);
		incsc.setCode(cargoOper.getCode());
		incsc.setCreateDatetime(DateUtil.getNow());
		incsc.setStockType(stockType == 10 ? CargoInfoBean.STOCKTYPE_CUSTOMER : CargoInfoBean.STOCKTYPE_AFTER_SALE);
		incsc.setStockArea(areaId);
		incsc.setProductId(product.getProductId());
		incsc.setStockId(inCargoProductStock.getId());
		incsc.setStockInCount(1);
		incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
		incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		incsc.setCurrentCargoStock(inCargoProductStock.getStockCount() + inCargoProductStock.getStockLockCount());
		incsc.setCargoStoreType(inCargoInfo.getStoreType());
		incsc.setCargoWholeCode(inCargoInfo.getWholeCode());
		incsc.setStockPrice(vProduct.getPrice5());
		incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(incsc)) {
			return "数据库操作失败：addCargoStockCard";
		}
		return null;
	}

	/**
	 * 没有生成 CargoOperationCargoBean
	 * 
	 * @param type
	 *            类别，1用户库商品上架 2售后库商品上架
	 * @return
	 */
	private String addCargoOperForUpShelf(int area, voUser user, int type, ICargoService cargoService, String afterSaleCode, CargoOperationBean cargoOper, AfStockService stockService, int dpId) {
		String cargoOperCode = "HWS" + DateUtil.getNow().substring(2, 10).replace("-", "");
		CargoOperationBean oldCargoOper = cargoService.getCargoOperation("code like '" + cargoOperCode + "%' order by id desc limit 1");
		if (oldCargoOper == null) {
			cargoOperCode = cargoOperCode + "00001";
		} else {// 获取当日计划编号最大值
			String _code = oldCargoOper.getCode();
			int number = Integer.parseInt(_code.substring(_code.length() - 5));
			number++;
			cargoOperCode += String.format("%05d", new Object[] { new Integer(number) });
		}
		String storageCode = "";
		if (area == ProductStockBean.AREA_GF) {
			storageCode = "GZF";
		} else if (area == ProductStockBean.AREA_ZC) {
			storageCode = "GZZ";
		} else if (area == ProductStockBean.AREA_WX) {
			storageCode = "JSW";
		} else if (area == ProductStockBean.AREA_GS) {
			storageCode = "GZS";
		} else if (area == ProductStockBean.AREA_BJ) {
			storageCode = "BJA";
		}
		cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS8);
		cargoOper.setCreateDatetime(DateUtil.getNow());
		cargoOper.setRemark(type == 1 ? "用户库商品上架" : "售后库商品上架");
		cargoOper.setCreateUserId(user.getId());
		cargoOper.setAuditingDatetime(DateUtil.getNow());
		cargoOper.setAuditingUserId(user.getId());
		cargoOper.setCode(cargoOperCode);
		cargoOper.setSource(afterSaleCode);
		cargoOper.setStorageCode(storageCode);
		cargoOper.setCreateUserName(user.getUsername());
		cargoOper.setAuditingUserName(user.getUsername());
		cargoOper.setType(CargoOperationBean.TYPE0);
		cargoOper.setLastOperateDatetime(DateUtil.getNow());
		cargoOper.setConfirmDatetime(DateUtil.getNow());

		if (!cargoService.addCargoOperation(cargoOper)) {
			return "数据库操作失败:addCargoOperation";
		}
		// 查询上架单Id
		cargoOper.setId(cargoService.getDbOp().getLastInsertId());

		AfterSaleDetectProductUpshelf upShelf = new AfterSaleDetectProductUpshelf();
		upShelf.setOperId(cargoOper.getId());
		upShelf.setAfterSaleDetectProductId(dpId);
		upShelf.setOperStatus(AfterSaleDetectProductUpshelf.OPER_STATUS1);
		upShelf.setType(type == 1 ? AfterSaleDetectProductUpshelf.TYPE2 : AfterSaleDetectProductUpshelf.TYPE1);

		if (!stockService.addAfterSaleDetectProductUpshelf(upShelf)) {
			return "数据库操作失败:addAfterSaleDetectProductUpshelf";
		}

		return null;
	}
	/**
	 * 没有生成 CargoOperationCargoBean
	 * @param type
	 *            类别，1用户库商品上架 2售后库商品上架
	 * @return
	 */
	private String addCargoOperForDownShelf(int area, voUser user, int type, ICargoService cargoService, String afterSaleCode, CargoOperationBean cargoOper, AfStockService stockService, int dpId) {
		String cargoOperCode = "HWX" + DateUtil.getNow().substring(2, 10).replace("-", "");
		CargoOperationBean oldCargoOper = cargoService.getCargoOperation("code like '" + cargoOperCode + "%' order by id desc limit 1");
		if (oldCargoOper == null) {
			cargoOperCode = cargoOperCode + "00001";
		} else {// 获取当日计划编号最大值
			String _code = oldCargoOper.getCode();
			int number = Integer.parseInt(_code.substring(_code.length() - 5));
			number++;
			cargoOperCode += String.format("%05d", new Object[] { new Integer(number) });
		}
		String storageCode = "";
		if (area == ProductStockBean.AREA_GF) {
			storageCode = "GZF";
		} else if (area == ProductStockBean.AREA_ZC) {
			storageCode = "GZZ";
		} else if (area == ProductStockBean.AREA_WX) {
			storageCode = "JSW";
		} else if (area == ProductStockBean.AREA_GS) {
			storageCode = "GZS";
		} else if (area == ProductStockBean.AREA_BJ) {
			storageCode = "BJA";
		}
		cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS17);
		cargoOper.setCreateDatetime(DateUtil.getNow());
		cargoOper.setRemark(type == 10 ? "用户库商品下架" : "售后库商品下架");
		cargoOper.setCreateUserId(user.getId());
		cargoOper.setAuditingDatetime(DateUtil.getNow());
		cargoOper.setAuditingUserId(user.getId());
		cargoOper.setCode(cargoOperCode);
		cargoOper.setSource(afterSaleCode);
		cargoOper.setStorageCode(storageCode);
		cargoOper.setCreateUserName(user.getUsername());
		cargoOper.setAuditingUserName(user.getUsername());
		cargoOper.setType(CargoOperationBean.TYPE1);
		cargoOper.setLastOperateDatetime(DateUtil.getNow());
		cargoOper.setConfirmDatetime(DateUtil.getNow());
		
		if (!cargoService.addCargoOperation(cargoOper)) {
			return "数据库操作失败:addCargoOperation";
		}
		// 查询上架单Id
		cargoOper.setId(cargoService.getDbOp().getLastInsertId());
		
		AfterSaleDetectProductUpshelf upShelf = new AfterSaleDetectProductUpshelf();
		upShelf.setOperId(cargoOper.getId());
		upShelf.setAfterSaleDetectProductId(dpId);
		upShelf.setOperStatus(AfterSaleDetectProductUpshelf.OPER_STATUS1);
		upShelf.setType(type == 10 ? AfterSaleDetectProductUpshelf.TYPE3 : AfterSaleDetectProductUpshelf.TYPE4);
		
		if (!stockService.addAfterSaleDetectProductUpshelf(upShelf)) {
			return "数据库操作失败:addAfterSaleDetectProductUpshelf";
		}
		
		return null;
	}

	/**
	 * 查询快递公司列表
	 * 
	 * @author mengqy
	 * @return
	 */
	public List<HashMap<String, String>> getDeliverList() {
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		try {
			ResultSet rs = dbOp.executeQuery(" SELECT id, content FROM sys_dict WHERE pid = 7 ");
			if (rs != null) {
				while (rs.next()) {
					HashMap<String, String> map = new HashMap<String, String>();
					map.put("id", rs.getString("id"));
					map.put("content", rs.getString("content"));
					list.add(map);
				}
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return list;
	}

	/**
	 * 添加返厂商品
	 * 
	 * @author mengqy
	 * @param supplierId
	 *            返回厂商id
	 * @param afCodes
	 *            需要返厂的售后处理单号列表
	 * @param packageCode
	 *            包裹单号
	 * @param contract
	 *            联系人
	 * @param user
	 *            当前用户
	 * 
	 * @return 操作结果，操作成功返回null，操作失败返回原因
	 */
	public String backSupplierProduct(int supplierId, List<String> afCodes, String packageCode, String contract, voUser user) {

		packageCode = StringUtil.dealParam(packageCode);
		contract = StringUtil.dealParam(contract);

		if (afCodes == null || afCodes.size() == 0)
			return "请扫描售后处理单号";

		if ("".equals(packageCode))
			return "请扫描包裹单号";

		if ("".equals(contract))
			return "请请输入联系人";

		if (supplierId <= 0)
			return "请选择返厂厂商";

		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			try {
				dbOp.startTransaction();

				AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
				ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
				IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
				IAdminService adminService = ServiceFactory.createAdminService(dbOp);
				int stockType = -1;
				for (String afCode : afCodes) {
					AfterSaleDetectProductBean product = afStockService.getAfterSaleDetectProduct(" code = '" + StringUtil.dealParam(afCode) + "' ");
					if (product == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]";
					}
					CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' ");
					if (cargoInfo == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]所对应的货位信息";
					}
					if (stockType == -1) {
						stockType = cargoInfo.getStockType();
					} else {
						if (stockType != cargoInfo.getStockType()) {
							dbOp.rollbackTransaction();
							return "不同库类型中商品不可以添加到同一张返厂清单中";
						}
					}
					
					//权限判断
					if(!afStockService.checkAfterSaleUserGroup(user, product.getAreaId())){
						dbOp.rollbackTransaction();
						return "没有处理单[" + product.getCode() + "]售后仓内作业权限!";
					}
					
					AfterSaleOrderBean asoBean = afterSaleService.getAfterSaleOrder(" id = " + product.getAfterSaleOrderId());
					if (asoBean == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]所关联的售后单";
					}

					AfterSaleBackSupplierProduct backProducts = afStockService.getAfterSaleBackSupplierProduct(" after_sale_detect_product_id = " + product.getId());

					// 售后库商品返厂
					if (cargoInfo.getStockType() == CargoInfoBean.STOCKTYPE_AFTER_SALE) {
						if(backProducts != null && backProducts.getStatus() == AfterSaleBackSupplierProduct.STATUS0){
							dbOp.rollbackTransaction();
							return "售后处理单[" + afCode + "]已返厂";
						}
						
						// 返厂商品信息
						AfterSaleBackSupplierProduct backSupplierProduct = new AfterSaleBackSupplierProduct();
						backSupplierProduct.setAfterSaleDetectProductId(product.getId());
						backSupplierProduct.setProductId(product.getProductId());
						backSupplierProduct.setGuarantee(AfterSaleBackSupplierProduct.GUARANTEE0);
						backSupplierProduct.setUserId(user.getId());
						backSupplierProduct.setUserName(user.getName());
						backSupplierProduct.setCreateDatetime(DateUtil.getNow());
						// 状态 等待厂商寄回
						backSupplierProduct.setStatus(AfterSaleBackSupplierProduct.STATUS0);						
						backSupplierProduct.setSupplierId(supplierId);
						backSupplierProduct.setSenderId(user.getId());
						backSupplierProduct.setSenderName(user.getUsername());
						backSupplierProduct.setSendDatetime(DateUtil.getNow());
						backSupplierProduct.setContract(contract);
						backSupplierProduct.setPackageCode(packageCode);
						
						if (!afStockService.addAfterSaleBackSupplierProduct(backSupplierProduct)) {
							dbOp.rollbackTransaction();
							return "添加返厂商品信息失败！";
						}						
					} else {
						// 3，等待返厂
						if (backProducts == null || backProducts.getStatus() != 3) {
							dbOp.rollbackTransaction();
							return "售后处理单[" + afCode + "]为未非返厂商品";
						} else {
							String telNumber = asoBean.getCustomerPhone();
							if (telNumber != null && telNumber.length() > 0){
								voOrder order = adminService.getOrder(asoBean.getOrderId());
								if(order==null){
									dbOp.rollbackTransaction();
									return "未查询到售后处理单[" + afCode + "]所关联的订单";
								}
								Map<String, Object> paramMap = new HashMap<String, Object>();
								TemplateMarker tm = TemplateMarker.getMarker();
								String content = tm.getOutString(TemplateMarker.BACK_SUPPLIER_MESSAGE_NAME, paramMap);
								if(order.isDaqOrder()){
									if (SenderSMS3.send(user.getId(), telNumber, content,65) != "提醒短信发送成功") {
										dbOp.rollbackTransaction();
										return "发送提醒短信失败";
									}
								}else{
									if (SenderSMS3.send(user.getId(), telNumber, content) != "提醒短信发送成功") {
										dbOp.rollbackTransaction();
										return "发送提醒短信失败";
									}
								}
								
							}

							// 返厂商品：
							// 0，等待厂商寄回
							// 1，厂商已寄回
							// 3，等待返厂
							// 4，已返厂
							// 5，检测合格
							// 6，检测不合格
							// 保修状态 是否返修
							String set = " status = 4 , supplier_id = " + supplierId + " , sender_id = " + user.getId() + " ";
							set += " , sender_name = '" + StringUtil.dealParam(user.getUsername()) + "' , send_datetime = '" + StringUtil.dealParam(DateUtil.getNow()) + "' ";
							set += " , contract = '" + contract + "' , package_code = '" + packageCode + "' ";
							if (!afStockService.updateAfterSaleBackSupplierProduct(set, " id = " + backProducts.getId())) {
								dbOp.rollbackTransaction();
								return "数据库操作失败:updateAfterSaleBackSupplierProduct";
							}
						}
					}

					String result = updateIMEIForBackSupplier(user, dbOp, product);
					if (result != null) {
						dbOp.rollbackTransaction();
						return result;
					}
					
					if (!afStockService.writeAfterSaleLog(user,"PDA将售后处理单[" + afCode + "]的商品返厂", 1, AfterSaleLogBean.TYPE7,afCode,null)) {
						dbOp.rollbackTransaction();
						return "写售后日志失败";
					}
				}
				dbOp.commitTransaction();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				dbOp.rollbackTransaction();
				return "发生异常";
			} finally {
				dbOp.release();
			}
		}
	}

	public String updateIMEIForBackSupplier(voUser user, DbOperation dbOp, AfterSaleDetectProductBean product) {
		return this.updateIMEI(user, "添加返厂商品", IMEIBean.IMEISTATUS6, dbOp, product);
	}

	public String updateIMEIForStockIn(voUser user, DbOperation dbOp, AfterSaleDetectProductBean product) {
		return this.updateIMEI(user, "售后退货入库", IMEIBean.IMEISTATUS2, dbOp, product);
	}

	private String updateIMEI(voUser user, String oper, int status, DbOperation dbOp, AfterSaleDetectProductBean product) {
		// 处理IMEI码的状态
		if (product.getIMEI() != null && product.getIMEI().length() > 0) {
			IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
			if (!imeiService.updateIMEI(" status = " + status, " product_id = " + product.getProductId() + " AND code = '" + product.getIMEI() + "' ")) {
				return "更新IMEI码状态失败";
			}

			String content = oper + "：IMEI码[" + product.getIMEI() + "]状态变为[" + IMEIBean.IMEIStatusMap.get(status) + "]"+",地区："+ProductStockBean.areaMap.get(1);
			// 添加Imei码日志
			IMEILogBean log = new IMEILogBean();
			log.setContent(content);
			log.setCreateDatetime(DateUtil.getNow());
			log.setIMEI(product.getIMEI());
			log.setOperCode(product.getCode());
			log.setOperType(IMEILogBean.OPERTYPE5);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());

			if (!imeiService.addIMEILog(log)) {
				return "数据库操作失败: addIMEILog";
			}
		}
		return null;
	}

	/**
	 * 厂商寄回商品签收——查选IMEI
	 * 
	 * @param afCode
	 * @return 执行成功，返回{?IMEI} 错误时，返回错误信息
	 */
	public String searchIMIE(String afCode) {
		afCode = StringUtil.dealParam(afCode);

		AfStockService service = new AfStockService(IBaseService.CONN_IN_METHOD, null);

		AfterSaleDetectProductBean product = service.getAfterSaleDetectProduct(" code = '" + afCode + "' ");
		if (product == null)
			return "未查询到售后处理单";
		String imei = "?" + (product.getIMEI() == null ? "" : product.getIMEI());

		AfterSaleBackSupplierProduct backProduct = service.getAfterSaleBackSupplierProduct(" after_sale_detect_product_id = " + product.getId());
		if (backProduct == null)
			return "该商品为非厂商寄回商品";
		if (backProduct.status == AfterSaleBackSupplierProduct.STATUS1)
			return "该售后处理单已签收";

		return imei;
	}

	/**
	 * 厂商寄回商品签收
	 * 
	 * @param afCode
	 * @param imeiCode
	 * @param productCode 商品编号
	 * @param type 0 IMEI/售后处理单签收  1 商品编号签收
	 * @param user
	 *            当前操作用户
	 * @param flag 0 PDA, 1 Web           
	 * @return 操作结果，操作成功返回null，操作失败返回原因
	 */
	public Json receiveBackSupplierProduct(String afCode, String imeiCode, String productCode, int type, voUser user, int flag) {
		afCode = StringUtil.dealParam(afCode);
		imeiCode = StringUtil.convertNull(StringUtil.dealParam(imeiCode));
		productCode = StringUtil.convertNull(StringUtil.dealParam(productCode));

		Json j = new Json();
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);			
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				// 判断是否正在盘点
				AfterSaleInventory afterSaleInventory = service.getAfterSaleInventory(" status <>" + AfterSaleInventory.COMPLETE_CHECK);

				if (afterSaleInventory != null) {
					j.setMsg("存在尚未完成的盘点单，请先盘点!");
					return j;
				}
				AfterSaleDetectProductBean product = service.getAfterSaleDetectProduct(" code = '" + afCode + "' ");
				if (product == null) {
					j.setMsg("未查询到售后处理单");
					return j;
				}
				//权限判断
				if(!service.checkAfterSaleUserGroup(user, product.getAreaId())){
					j.setMsg("没有该地区售后仓内作业权限!");
					return j;
				}
				if (product.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
					j.setMsg("此售后处理单"+product.getCode()+"已锁定!");
					return j;
				}

//				if (oldImei != null && !"".equals(oldImei) && (newImei == null || "".equals(newImei))) {
//					return "请扫描IMEI码";
//				}

				CargoInfoBean cargo = cargoService.getCargoInfo("whole_code='" + product.getCargoWholeCode() + "'");
				if(cargo==null){
					j.setMsg("此处理单对应的货位不存在!");
					return j;
				}
				//获取最近一条返厂商品记录
				List<AfterSaleBackSupplierProduct> backSupplierProductList = service.getAfterSaleBackSupplierProductList("after_sale_detect_product_id=" + product.getId()+" and status in("+AfterSaleBackSupplierProduct.STATUS4+","+AfterSaleBackSupplierProduct.STATUS0+")",-1,1,"id desc");
				AfterSaleBackSupplierProduct backProduct = null;
				if(backSupplierProductList!=null &&backSupplierProductList.size()==1){
					backProduct = backSupplierProductList.get(0);
				}
				if (backProduct == null){
					j.setMsg("该商品为非厂商寄回商品");
					return j;
				}
				
				if(cargo.getStockType() == ProductStockBean.STOCKTYPE_CUSTOMER){
					if (product.getStatus() == AfterSaleDetectProductBean.STATUS4) {
						if (!(backProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS4 || backProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS0)){
							j.setMsg("该售后处理单不是【等待厂商寄回】或【已返厂】状态,不能签收!");
							return j;
						}
					} else {
						if (backProduct.getStatus() != AfterSaleBackSupplierProduct.STATUS0){
							j.setMsg("该售后处理单不是【等待厂商寄回】状态,不能签收!");
							return j;
						}
					}
				}else if(cargo.getStockType() == ProductStockBean.STOCKTYPE_AFTER_SALE){
					if (!(backProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS4 || backProduct.getStatus() == AfterSaleBackSupplierProduct.STATUS0)){
						j.setMsg("该售后处理单不是【等待厂商寄回】或【已返厂】状态,不能签收!");
						return j;
					}
				}else{
					j.setMsg( "返厂商品不是售后库也不是客户库!");
					return j;
				}
				
				// 匹配IMEI或商品编号
				if (type == 0) {
					if (!imeiCode.equalsIgnoreCase(StringUtil.convertNull(backProduct.getIMEI()))) {
						j.setMsg("售后处理单号和IMEI码不匹配");
						return j;
					}
				} else {
					WareService wareService = new WareService(dbOp);
					voProduct vProduct = wareService.getProduct(productCode);
					if(vProduct == null){
						j.setMsg("商品编号不存在");
						return j;
					}
					if (product.getProductId() != vProduct.getId()) {
						j.setMsg("售后处理单号和商品编号不匹配");
						return j;
					}
				}
				
				StringBuilder sbCount = new StringBuilder();
				sbCount.append(" SELECT COUNT(*) FROM after_sale_back_supplier_product_replace WHERE detect_id = ").append(product.getId());
				sbCount.append(" AND `audit_status` = ").append(AfterSaleBackSupplierProductReplace.AUDIT_STATUS1); // 待审核
				int count = dbOp.getInt(sbCount.toString());
				if (count > 0) {
					j.setMsg("该售后处理单更换商品操作,还未审核,不能签收");
					return j;
				}
				
				boolean isSpare = dbOp.getInt(" SELECT COUNT(*) FROM after_sale_replace_new_product_record WHERE after_sale_detect_product_id = " + product.getId()) > 0;
				
				dbOp.startTransaction();

				String set = " status = " + AfterSaleBackSupplierProduct.STATUS1;
				set += " , return_user_id = " + user.getId();
				set += " , return_user_name = '" + StringUtil.dealParam(user.getUsername());
				set += "' , return_datetime = '" + DateUtil.getNow() + "' ";

				if (!service.updateAfterSaleBackSupplierProduct(set, " id = " + backProduct.getId())) {
					dbOp.rollbackTransaction();
					j.setMsg("数据库操作失败：updateAfterSaleBackSupplierProduct");
					return j;
				}


				if (product.getIMEI() != null && product.getIMEI().length() > 0) {
					if (isSpare) {
						// 如果是备用机 IMEI状态为 可出库
						String result = this.updateIMEI(user, "备用机厂商寄回商品签收", IMEIBean.IMEISTATUS2, dbOp, product);
						if (result != null) {
							dbOp.rollbackTransaction();
							j.setMsg(result);
							return j;
						}
					} else {
						// 撤销IMEI码替换功能
						String result = this.revertIMEI(product.getIMEI(), product.getProductId(), product.getCargoWholeCode(), afCode, null, user, dbOp);
						if (result != null) {
							dbOp.rollbackTransaction();
							j.setMsg(result);
							return j;
						}
					}
				}			
				
				if (!service.writeAfterSaleLog(user, (flag == 0 ? "PDA" : "Web") + "签收厂商寄回商品,处理单号:" + afCode, 1, AfterSaleLogBean.TYPE2,afCode,null)) {
					dbOp.rollbackTransaction();
					j.setMsg("写售后日志失败");
					return j;
				}
				dbOp.commitTransaction();
				
				if(isSpare){
					j.setObj("此机器已做维修换新机更更换，请入备用机库！");
				}
				j.setSuccess(true);				
				j.setMsg("操作成功");				
				return j;
			} catch (Exception e) {
				e.printStackTrace();
				dbOp.rollbackTransaction();
				j.setMsg("发生异常");
				return j;
			} finally {
				dbOp.release();
			}
		}
	}

	/**
	 * 恢复原有IMEI状态
	 * 
	 * @param imei
	 * @param cargoWholeCode
	 * @param afCode
	 * @param user
	 * @param dbOp
	 * @return
	 */
	private String revertIMEI(String imei, int productId, String cargoWholeCode, String afCode, String logContent, voUser user, DbOperation dbOp) {
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);

		CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + cargoWholeCode + "' ");
		if (cargoInfo == null)
			return "未查询到货位";

		int newStatus = cargoInfo.getStockType() == CargoInfoBean.STOCKTYPE_AFTER_SALE ? IMEIBean.IMEISTATUS2 : IMEIBean.IMEISTATUS3;
		// 售后库：可出库；用户库：已出库
		if (!imeiService.updateIMEI(" status = " + newStatus, " product_id = " + productId + " AND  code = '" + imei + "' ")) {
			return "更新IMEI码状态失败";
		}

		String content = "厂商寄回商品签收：IMEI码[" + imei + "]状态由[维修中]变为[" + IMEIBean.IMEIStatusMap.get(newStatus) + "]"+",地区："+ProductStockBean.areaMap.get(1);
		// 添加Imei码日志
		IMEILogBean log = new IMEILogBean();
		log.setContent(logContent == null ? content : logContent);
		log.setCreateDatetime(DateUtil.getNow());
		log.setIMEI(imei);
		log.setOperCode(afCode);
		log.setOperType(IMEILogBean.OPERTYPE5);
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());

		if (!imeiService.addIMEILog(log)) {
			return "数据库操作失败: addIMEILog";
		}

		return null;
	}

	/**
	 * 添加一个新的IMEI码
	 * 
	 * @param imei
	 * @param productId
	 * @param cargoWholeCode
	 * @param afCode
	 * @param logContent
	 * @param user
	 * @param dbOp
	 * @return
	 */
	private String addNewIMEI(String imei, int productId, String cargoWholeCode, String afCode, String logContent, voUser user, DbOperation dbOp) {
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);

		CargoInfoBean cargoInfo = cargoService.getCargoInfo(" whole_code = '" + cargoWholeCode + "' ");
		if (cargoInfo == null)
			return "未查询到货位";

		int newStatus = cargoInfo.getStockType() == CargoInfoBean.STOCKTYPE_AFTER_SALE ? IMEIBean.IMEISTATUS2 : IMEIBean.IMEISTATUS3;

		IMEIBean imeiBean = new IMEIBean();
		imeiBean.setCode(imei);
		imeiBean.setStatus(newStatus);
		imeiBean.setCreateDatetime(DateUtil.getNow());
		imeiBean.setProductId(productId);
		if (!imeiService.addIMEI(imeiBean)) {
			dbOp.rollbackTransaction();
			return "数据库操作失败: addIMEI";
		}

		IMEILogBean log = new IMEILogBean();
		log.setContent(logContent);
		log.setCreateDatetime(DateUtil.getNow());
		log.setIMEI(imei);
		log.setOperCode(afCode);
		log.setOperType(IMEILogBean.OPERTYPE5);
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());

		if (!imeiService.addIMEILog(log)) {
			dbOp.rollbackTransaction();
			return "数据库操作失败: addIMEILog";
		}

		return null;
	}

	/**
	 * 寄回用户
	 * 
	 * @param afCodes
	 *            售出处理单号列表
	 * @param packageCode
	 *            包裹单号
	 * @param freight
	 *            运费金额
	 * @param deliverId
	 *            快递公司id
	 * @param user
	 *            当前操作用户
	 * @param areaId
	 * 			  库地区id
	 * 
	 * @param flagPDA 0 PDA, 1 Web
	 * 
	 * @return 操作结果，操作成功返回null，操作失败返回原因
	 */
	public String backUser(List<String> afCodes, String packageCode, Float freight, int deliverId, String tel,float weight, String address, String username, voUser user, String remark, int flagPDA) {
		packageCode = StringUtil.dealParam(packageCode);
		tel = StringUtil.dealParam(tel);
		address = StringUtil.dealParam(address);

		if ("".equals(username)) {
			return "请输入用户姓名";
		}
		if ("".equals(tel)) {
			return "请输入手机号";
		}
		if ("".equals(address)) {
			return "请输入地址";
		}

		if (afCodes == null || afCodes.size() == 0)
			return "请扫描售后处理单号";


		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			WareService wareService = new WareService(dbOp);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IAdminService adminService = ServiceFactory.createAdminService(dbOp);
			IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				// 判断是否正在盘点
				AfterSaleInventory afterSaleInventory = afService.getAfterSaleInventory(" status <>" + AfterSaleInventory.COMPLETE_CHECK);

				if (afterSaleInventory != null) {
					return "存在尚未完成的盘点单，请先盘点!";
				}
				if (packageCode == null) {
					packageCode = "";
				}
				packageCode = packageCode.trim();
				if (packageCode != "") {
					if (afService.getAfterSaleBackUserPackage(" package_code = '" + packageCode + "'") != null) {
						return "该包裹单号已被使用, 请选用其他包裹单号";
					}
				}

				dbOp.startTransaction();

				AfterSaleBackUserPackage backUserPackage = new AfterSaleBackUserPackage();
				backUserPackage.setCustomerName(username);
				backUserPackage.setUserPhone(tel);
				backUserPackage.setUserAddress(address);
				backUserPackage.setDeliverId(deliverId);
				backUserPackage.setPrice(freight);
				backUserPackage.setPackageCode(packageCode);
				backUserPackage.setCreateDatetime(DateUtil.getNow());
				backUserPackage.setUserId(user.getId());
				backUserPackage.setUserName(user.getUsername());
				backUserPackage.setRemark(remark);
				backUserPackage.setWeight(weight);
				for (String afCode : afCodes) {
					AfterSaleDetectProductBean product = afService.getAfterSaleDetectProduct(" code = '" + afCode + "' ");
					if(product != null){
						backUserPackage.setAreaId(product.getAreaId());
						break;
					}else{
						//备用机号--需要联查换新机记录
						product = afService.getDetectProductBySpareCode(afCode);
						if(product!=null){
							backUserPackage.setAreaId(product.getAreaId());
							break;
						}
					}
				}
				// 状态已寄出
				backUserPackage.setStatus(AfterSaleBackUserPackage.STATUS0);

				// 添加寄回包裹
				if (!afService.addAfterSaleBackUserPackage(backUserPackage)) {
					dbOp.rollbackTransaction();
					return "数据库操作失败：addAfterSaleBackUserPackage";
				}

				backUserPackage.setId(dbOp.getLastInsertId());

				// 添加 销售后台用的包裹单列表after_sale_warehource_package_list
				String sql = " INSERT INTO after_sale_warehource_package_list ( post_type, pay_type, freight, deliver_id, package_code, create_user_id, create_user_name, create_datetime ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ? ) ";
				if (!dbOp.prepareStatement(sql)) {
					dbOp.rollbackTransaction();
					return "数据库操作失败";
				}
				PreparedStatement pstmt = dbOp.getPStmt();
				pstmt.setInt(1, 1);// post_type, 1 寄出
				pstmt.setInt(2, 1);// pay_type, 1寄付 2到付
				pstmt.setFloat(3, freight);
				pstmt.setInt(4, deliverId);
				pstmt.setString(5, packageCode);
				pstmt.setInt(6, user.getId());
				pstmt.setString(7, user.getUsername());
				pstmt.setString(8, DateUtil.getNow());

				if (pstmt.executeUpdate() <= 0) {
					dbOp.rollbackTransaction();
					return " 数据库操作失败：INSERT INTO after_sale_warehource_package_list ";
				}

				int warePackageId = dbOp.getLastInsertId();

				// 验证所有的售后处理单所关联的电话号码是否一致
				String orderTelNumber = null;
				String orderAddress = null;
				String productsName = "";
				int areaId = -1;
				int afterOrderId = -1;
				//决定是否用65通道发短信
				boolean flag = true;
				// 添加寄回商品
				for (String afCode : afCodes) {
					AfterSaleDetectProductBean product = afService.getAfterSaleDetectProduct(" code = '" + afCode + "' ");
					if (product == null) {
						product = afService.getDetectProductBySpareCode(afCode);
						if(product == null){
							dbOp.rollbackTransaction();
							return "未查询到售后处理单[" + afCode + "]";
						}
					}
					//权限判断
					if(!afService.checkAfterSaleUserGroup(user, product.getAreaId())){
						dbOp.rollbackTransaction();
						return "没有处理单[" + product.getCode() + "]所属地区售后仓内作业权限!";
					}
					if(areaId == -1){
						areaId = product.getAreaId();
					} else {
						if(product.getAreaId() != areaId){
							dbOp.rollbackTransaction();
							return "同一包裹中的售后处理单地区必须一致!";
						}
					}
					if (product.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
						dbOp.rollbackTransaction();
						return "此售后处理单["+afCode+"]已锁定!";
					}
					AfterSaleOrderBean asoBean = afterSaleService.getAfterSaleOrder(" id = " + product.getAfterSaleOrderId());
					if (asoBean == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]所关联的售后单";
					}
					if(afterOrderId == -1){
						afterOrderId = asoBean.getId(); 
					} else {
						if(asoBean.getId() != afterOrderId){
							dbOp.rollbackTransaction();
							return "同一包裹中的售后处理单必须是同一售后单!";
						}
					}

					if (orderTelNumber == null) {
						orderTelNumber = asoBean.getCustomerPhone();
					} else {
						if (!orderTelNumber.equals(asoBean.getCustomerPhone())) {
							dbOp.rollbackTransaction();
							return "同一包裹中的商品，相关联的电话号码必须一致,售后处理单[" + afCode + "]";
						}
					}
					if (orderAddress == null) {
						orderAddress = asoBean.getCustomerAddress();
					} else {
						if (!orderAddress.equals(asoBean.getCustomerAddress())) {
							dbOp.rollbackTransaction();
							return "同一包裹中的商品，相关联的地址必须一致,售后处理单[" + afCode + "]";
						}
					}
					
					voOrder order = adminService.getOrder(asoBean.getOrderId());
					if(order == null){
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]所关联的订单";
					}
					if(!order.isDaqOrder()){
						flag = false;
					}
					AfterSaleBackUserProduct oldBackProduct = afService.getAfterSaleBackUserProduct(" package_id = 0 AND after_sale_detect_product_id = " + product.getId());
					if (oldBackProduct == null) {
						dbOp.rollbackTransaction();
						return "该商品为非寄回用户商品，售后处理单[" + afCode + "]";
					}

					// 售后处理单状态： 10，原品返回
					// 0，原品返回
					// 1，维修寄回
					StringBuilder set = new StringBuilder(" package_id = " + backUserPackage.getId());
					if(oldBackProduct.getType()!=2){
						set.append(" , product_id = ").append(product.getProductId()).append(",type=").append(((product.getStatus() == 10) ? 0 : 1));
					}
					

					if (!afService.updateAfterSaleBackUserProduct(set.toString(), " id = " + oldBackProduct.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：updateAfterSaleBackUserProduct";
					}

					// 短信，生成短信内容
					voProduct pro = wareService.getProduct(product.getProductId());
					if (productsName.length() > 0)
						productsName += ",";
					productsName += pro.getName();
					
					// 新的售后处理单状态
					int newAFStatus = 0;
					switch (product.getStatus()) {
					case 10:
						// 原品返回 ——》原品已返回
						newAFStatus = 11;
						break;
					case 4:
						// 保修——》保修已完成
						newAFStatus = 9;
						break;
					case 7:
						// 付费维修——》付费维修已完成
						newAFStatus = 8;
						break;
					case 20:
						//维修换新机中-----》维修换新机已完成
						newAFStatus = 21;
						break;
					default:
						dbOp.rollbackTransaction();
						return "售后处理单[" + afCode + "]状态不正确";
					}

					// 修改 售后处理单 状态
					if (!afService.updateAfterSaleDetectProduct(" status = " + newAFStatus, " id = " + product.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：updateAfterSaleDetectProduct";
					}
					
					//如果是imei码状态不是已出库--更新为已出库
					IMEIBean imeiBean = imeiService.getIMEI("code='"+product.getIMEI()+"'");
					if(imeiBean!=null && imeiBean.getStatus()!=IMEIBean.IMEISTATUS3){
						int preStatus = imeiBean.getStatus();
						if(!imeiService.updateIMEI("status=3", "id="+imeiBean.getId())){
							dbOp.rollbackTransaction();
							return "更新售后处理单[" + afCode + "]所关联的IMEI码状态失败";
						}
						IMEILogBean log = new IMEILogBean();
						log.setContent("寄回用户操作,由【"+IMEIBean.IMEIStatusMap.get(preStatus)+"】变为【已出库】");
						log.setCreateDatetime(DateUtil.getNow());
						log.setIMEI(imeiBean.getCode());
						log.setOperCode("");
						log.setOperType(IMEILogBean.OPERTYPE16);
						log.setUserId(user.getId());
						log.setUserName(user.getUsername());
						if(!imeiService.addIMEILog(log)){
							dbOp.rollbackTransaction();
							return "新增售后处理单[" + afCode + "]所关联的IMEI码日志失败";
						}
					}
					
					// 关联销售处理单
					String query = " SELECT id FROM after_sale_warehource_product_records WHERE code = '" + afCode + "' ";
					//换新机寄回--输入的是备用机号
					if(newAFStatus == 21){
						query = "select aswpr.id from after_sale_warehource_product_records aswpr "
								+ "join after_sale_replace_new_product_record asrnpr on aswpr.id=asrnpr.after_sale_detect_product_id "
								+ "where asrnpr.spare_code='"+ afCode + "' ";
					}
					int tmpId = dbOp.getInt(query);
					if (tmpId == 0) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]所关联的销售处理单";
					}

					String insert = " INSERT INTO after_sale_whpackage_post_detail (after_sale_warehource_package_id, after_sale_wsproduct_record_id) VALUES ( " + warePackageId + "," + tmpId + ") ";
					if (!dbOp.executeUpdate(insert)) {
						dbOp.rollbackTransaction();
						return " 数据库操作失败：INSERT INTO after_sale_whpackage_post_detail ";
					}

					// 销售后台处理单 after_sale_warehource_product_records
					// status = 7 处理已完成
					StringBuffer sb = new StringBuffer();
					sb.append(" UPDATE after_sale_warehource_product_records ");
					sb.append(" SET status = 7, ");
					sb.append(" modify_user_id = ");
					sb.append(user.getId());
					sb.append(" ,  modify_user_name = '");
					sb.append(StringUtil.dealParam(user.getUsername()));
					sb.append("' , modify_datetime = '");
					sb.append(DateUtil.getNow());

					sb.append("' WHERE id = ");
					sb.append(tmpId);
					if (!dbOp.executeUpdate(sb.toString())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：更新销售后台处理单状态失败";
					}

					CargoInfoBean outCargoInfo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' ");
					if (outCargoInfo == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]所关联货位";
					}

					// 减货位库存
					CargoProductStockBean outCargoProductStock = cargoService.getCargoProductStock(" product_id = " + product.getProductId() + " AND cargo_id = " + outCargoInfo.getId());
					if (outCargoProductStock == null || outCargoProductStock.getStockCount() <= 0) {
						dbOp.rollbackTransaction();
						return "货位库存不足";
					}
					if (!cargoService.updateCargoProductStockCount(outCargoProductStock.getId(), -1)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateCargoProductStockCount!";
					}

					// 减商品库存
					ProductStockBean outProductStock = psService.getProductStock("product_id=" + product.getProductId() + " AND area=" + outCargoInfo.getAreaId() + " and type=" + outCargoInfo.getStockType());
					if (outProductStock.getStock() <= 0) {
						dbOp.rollbackTransaction();
						return "商品库存不足";
					}
					if (!psService.updateProductStockCount(outProductStock.getId(), -1)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateProductStockCount!";
					}

					voProduct vProduct = wareService.getProduct(product.getProductId());
					vProduct.setPsList(psService.getProductStockList("product_id = " + vProduct.getId(), -1, -1, "id asc"));

					// 售后处理单状态： 10，原品返回
					// 原品返回、 维修寄回
					int cardType = StockCardBean.CARDTYPE_AFTERSALE_OLDPRODUCT_BACKUSER;
					int cargoCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_OLDPRODUCT_BACKUSER;
					if (oldBackProduct.getType()==AfterSaleBackUserProduct.TYPE1) {
						cardType = StockCardBean.CARDTYPE_AFTERSALE_REPAIRPRODUCT_BACKUSER;
						cargoCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_REPAIRPRODUCT_BACKUSER;
					}else if(oldBackProduct.getType()==AfterSaleBackUserProduct.TYPE2){
						cardType = StockCardBean.CARDTYPE_ASFTERSALE_REPLACE_BACKUSER;
						cargoCardType = CargoStockCardBean.CARDTYPE_ASFTERSALE_REPLACE_BACKUSER;
					}

					// 商品进销存卡片
					StockCardBean sc = new StockCardBean();
					sc.setCardType(cardType);
					sc.setCode(afCode); // 售后处理单号
					sc.setCreateDatetime(DateUtil.getNow());
					sc.setStockType(outCargoInfo.getStockType());
					sc.setStockArea(outCargoInfo.getAreaId());
					sc.setProductId(product.getProductId());
					sc.setStockId(outProductStock.getId());
					sc.setStockOutCount(1);
					sc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
					sc.setCurrentStock(vProduct.getStock(sc.getStockArea(), sc.getStockType()) + vProduct.getLockCount(sc.getStockArea(), sc.getStockType()));
					sc.setStockAllArea(vProduct.getStock(sc.getStockArea()) + vProduct.getLockCount(sc.getStockType()));
					sc.setStockAllType(vProduct.getStockAllType(sc.getStockType()) + vProduct.getLockCountAllType(sc.getStockType()));
					sc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					sc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
					sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
					if (!psService.addStockCard(sc)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：addStockCard";
					}

					// 货位进销存卡片
					CargoStockCardBean incsc = new CargoStockCardBean();
					incsc.setCardType(cargoCardType);
					incsc.setCode(afCode); // 售后处理单号
					incsc.setCreateDatetime(DateUtil.getNow());
					incsc.setStockType(outCargoInfo.getStockType());
					incsc.setStockArea(outCargoInfo.getAreaId());
					incsc.setProductId(product.getProductId());
					incsc.setStockId(outCargoProductStock.getId());
					incsc.setStockOutCount(1);
					incsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
					incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
					incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					incsc.setCurrentCargoStock(outCargoProductStock.getStockCount() + outCargoProductStock.getStockLockCount());
					incsc.setCargoStoreType(outCargoInfo.getStoreType());
					incsc.setCargoWholeCode(outCargoInfo.getWholeCode());
					incsc.setStockPrice(vProduct.getPrice5());
					incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
					if (!cargoService.addCargoStockCard(incsc)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：addCargoStockCard";
					}
					
					//相关配件寄回用户
					List<Map<String,Integer>> fittingList = afService.getAfterSaleDetectFitting(product.getId());
					int area = product.getAreaId();
					int stockType = ProductStockBean.STOCKTYPE_CUSTOMER_FITTING;
					if(fittingList!=null && fittingList.size()>0){
						for(int i=0;i<fittingList.size();i++){
							Map<String,Integer> fittingMap = fittingList.get(i);
							int fittingId = fittingMap.get("fittingId");
							voProduct fitting = wareService.getProduct(fittingId);
							int intactCount = fittingMap.get("intactCount");
							int damageCount = fittingMap.get("damageCount");
							CargoInfoBean intactCargo = cargoService.getCargoInfo("area_id=" +  area +" and stock_type="+ stockType + " and store_type=" 
									+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE3);
							if(intactCargo==null) {
								dbOp.rollbackTransaction();
								return "未找到完好配件货位！";
							}
							CargoInfoBean badCargo = cargoService.getCargoInfo("area_id=" +  area +" and stock_type="+ stockType + " and store_type=" 
									+ CargoInfoBean.STORE_TYPE2  + " and type=" + CargoInfoBean.TYPE4);
							if(badCargo==null) {
								dbOp.rollbackTransaction();
								return "未找到损坏配件货位！";
							}
							int stockCardType = StockCardBean.CARDTYPE_FITTING_BACKUSER;
							int cargoStockCardType = CargoStockCardBean.CARDTYPE_FITTING_BACKUSER;
							if(intactCount>0){
								String tip =opearteFittingStockOut(psService, cargoService, area, afCode,fitting, intactCount, intactCargo,stockCardType,cargoStockCardType);
								if(tip!=null && tip.length()>0){
									dbOp.rollbackTransaction();
									return tip;
								}
							}
							if(damageCount>0){
								String tip =opearteFittingStockOut(psService, cargoService, area, afCode,fitting, damageCount, badCargo,stockCardType,cargoStockCardType);
								if(tip!=null && tip.length()>0){
									dbOp.rollbackTransaction();
									return tip;
								}
							}
						}
					}

					if (!afService.writeAfterSaleLog(user, (flagPDA == 0 ? "PDA" : "Web") + "寄回用户,处理单号:" + afCode, 1, AfterSaleLogBean.TYPE8,afCode,null)) {
						dbOp.rollbackTransaction();
						return "写售后日志失败";
					}
					
					// 客户库 不处理存库批次 和 财务相关的表
					
				}

				// 需要发送短信
				/*if (productsName.length() > 0) {
					if (orderTelNumber == null || orderTelNumber.length() == 0) {
						dbOp.rollbackTransaction();
						return "未查询到手机号，不能发送短信";
					}

					Map<String, Object> paramMap = new HashMap<String, Object>();
					paramMap.put("productName", productsName);
					paramMap.put("deliver", SysDict.deliverMap.get(deliverId));
					paramMap.put("packageNum", packageCode);

					TemplateMarker tm = TemplateMarker.getMarker();
					String content = tm.getOutString(TemplateMarker.BACK_USER_PRODUCT_MESSAGE_NAME, paramMap);
					if(flag){
						SenderSMS3.send(user.getId(), orderTelNumber, content,65);
					}else{
						SenderSMS3.send(user.getId(), orderTelNumber, content);
					}
				}*/

				dbOp.commitTransaction();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				dbOp.rollbackTransaction();
				return "发生异常";
			} finally {
				dbOp.release();
			}
		}
	}
	
	/**
	 * 对配件出库库存进行操作
	 * @param psService
	 * @param cargoService
	 * @param area
	 * @param stockType
	 * @param afCode
	 * @param fitting
	 * @param count
	 * @param cargo
	 * @return
	 * @author lining
	* @date 2014-7-8
	 */
	private String opearteFittingStockOut(IProductStockService psService, ICargoService cargoService, int area,
			String afCode,voProduct fitting, int count, CargoInfoBean cargo,int stockCardType,int cargoStockCardType) {
		// 增加商品库存
		ProductStockBean productStock = psService.getProductStock("product_id = " + fitting.getId() + " and area = " + area + " and type = " + cargo.getStockType());
		if(productStock==null || productStock.getStock()<=0){
			return "配件库存不足!";
		}
		if (!psService.updateProductStockCount(productStock.getId(), -count)) {
			return "数据库操作失败:updateProductStockCount!";
		}
		
		// 增加货位库存
		CargoProductStockBean cargoProductStock = cargoService.getCargoProductStock(" product_id = " + fitting.getId() + " AND cargo_id = " + cargo.getId());
		if(cargoProductStock==null || cargoProductStock.getStockCount()<=0){
			return "配件的货位库存不足!";
		}
		if (!cargoService.updateCargoProductStockCount(cargoProductStock.getId(), -count)) {
			return "数据库操作失败:updateCargoProductStockCount!";
		}

		fitting.setPsList(psService.getProductStockList("product_id = " + fitting.getId(), -1, -1, "id asc"));

		// 商品进销存卡片
		StockCardBean stockCard = new StockCardBean();
		stockCard.setCardType(stockCardType);
		stockCard.setCode(afCode); // 售后处理单号
		stockCard.setCreateDatetime(DateUtil.getNow());
		stockCard.setStockType(cargo.getStockType());
		stockCard.setStockArea(area);
		stockCard.setProductId(fitting.getId());
		stockCard.setStockId(productStock.getId());
		stockCard.setStockOutCount(count);
		stockCard.setStockOutPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(fitting.getPrice5()))).doubleValue());
		stockCard.setCurrentStock(fitting.getStock(stockCard.getStockArea(), stockCard.getStockType()) + fitting.getLockCount(stockCard.getStockArea(), stockCard.getStockType()));
		stockCard.setStockAllArea(fitting.getStock(stockCard.getStockArea()) + fitting.getLockCount(stockCard.getStockType()));
		stockCard.setStockAllType(fitting.getStockAllType(stockCard.getStockType()) + fitting.getLockCountAllType(stockCard.getStockType()));
		stockCard.setAllStock(fitting.getStockAll() + fitting.getLockCountAll());
		stockCard.setStockPrice(fitting.getPrice5());// 新的库存价格，每次入库都要计算
		stockCard.setAllStockPriceSum((new BigDecimal(stockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(stockCard.getStockPrice()))).doubleValue());
		if (!psService.addStockCard(stockCard)) {
			return "数据库操作失败：addStockCard";
		}

		// 货位进销存卡片
		CargoStockCardBean cargoStockCard = new CargoStockCardBean();
		cargoStockCard.setCardType(cargoStockCardType);
		cargoStockCard.setCode(afCode); // 售后处理单号
		cargoStockCard.setCreateDatetime(DateUtil.getNow());
		cargoStockCard.setStockType(cargo.getStockType());
		cargoStockCard.setStockArea(area);
		cargoStockCard.setProductId(fitting.getId());
		cargoStockCard.setStockId(cargoProductStock.getId());
		cargoStockCard.setStockOutCount(count);
		cargoStockCard.setStockOutPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(fitting.getPrice5()))).doubleValue());
		cargoStockCard.setCurrentStock(fitting.getStock(cargoStockCard.getStockArea(), cargoStockCard.getStockType()) + fitting.getLockCount(cargoStockCard.getStockArea(), cargoStockCard.getStockType()));
		cargoStockCard.setAllStock(fitting.getStockAll() + fitting.getLockCountAll());
		cargoStockCard.setCurrentCargoStock(cargoProductStock.getStockCount() + cargoProductStock.getStockLockCount());
		cargoStockCard.setCargoStoreType(cargo.getStoreType());
		cargoStockCard.setCargoWholeCode(cargo.getWholeCode());
		cargoStockCard.setStockPrice(fitting.getPrice5());
		cargoStockCard.setAllStockPriceSum((new BigDecimal(cargoStockCard.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(cargoStockCard.getStockPrice()))).doubleValue());
		if (!cargoService.addCargoStockCard(cargoStockCard)) {
			return "数据库操作失败：addCargoStockCard";
		}
		return null;
	}
 	
	/**
	 * 封箱
	 * 
	 * @param afCodes
	 * @param user
	 * @return 执行成功，返回{?封箱单号} 错误时，返回错误信息
	 */
	public String sealBox(List<String> afCodes, voUser user) {
		if (afCodes == null || afCodes.size() == 0)
			return "";

		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);

			try {

				dbOp.startTransaction();

				// 生成新的封箱单号
				String code = "";
				String date = "F" + DateUtil.getNow().substring(0, 10).replace("-", "");
				int number = 0;
				AfterSaleSeal oldSeal = afService.getAfterSaleSeal("code like '" + date + "%'  order by id desc limit 1");
				if (oldSeal != null) {
					String oldCode = oldSeal.getCode();
					number = Integer.parseInt(oldCode.substring(oldCode.length() - 4));
				}
				number++;
				code = date + String.format("%04d", new Object[] { new Integer(number) });

				AfterSaleSeal seal = new AfterSaleSeal();
				seal.setCode(code);
				seal.setCreateDatetime(DateUtil.getNow());
				seal.setSealProductCount(afCodes.size());
				seal.setUserId(user.getId());
				seal.setUserName(user.getUsername());
				seal.setStatus(0); // 0已封箱

				if (!afService.addAfterSaleSeal(seal)) {
					dbOp.rollbackTransaction();
					return "数据库操作失败:addAfterSaleSeal";
				}
				seal.setId(dbOp.getLastInsertId());

				for (String afCode : afCodes) {

					AfterSaleDetectProductBean product = afService.getAfterSaleDetectProduct(" code = '" + afCode + "' ");
					if (product == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]";
					}
					AfterSaleOrderBean order = afterSaleService.getAfterSaleOrder(" id = " + product.getAfterSaleOrderId());
					if (order == null) {
						dbOp.rollbackTransaction();
						return "未查询到售后处理单[" + afCode + "]所关联的售后单";
					}

					// 12，待封箱
					if (product.status != 12) {
						dbOp.rollbackTransaction();
						return "售后处理单[" + afCode + "]状态不正确，不能封箱";
					}
					// 13，封箱已完成
					if (!afService.updateAfterSaleDetectProduct(" status = " + 13, " id = " + product.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateAfterSaleDetectProduct";
					}

					// 不修改售后单状态
					AfterSaleSealProduct sealProduct = afService.getAfterSaleSealProduct(" after_sale_seal_id = 0 AND status = 0 AND after_sale_detect_product_id = " + product.getId());
					if (sealProduct == null) {
						dbOp.rollbackTransaction();
						return "该商品为非待封箱商品，售后处理单[" + afCode + "]";
					}

					// 1, 已封箱
					String set = " after_sale_seal_id = " + seal.getId() + " , after_sale_order_detect_product_status = " + product.getStatus();
					set += " , after_sale_order_status = " + order.getStatus() + " , product_id = " + product.getProductId() + " , status = 1 ";
					if (!afService.updateAfterSaleSealProduct(set, " id = " + sealProduct.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateAfterSaleSealProduct";
					}

					// 销售后台处理单 after_sale_warehource_product_records
					// is_vanning = 2 已装箱
					StringBuffer sb = new StringBuffer();
					sb.append(" UPDATE after_sale_warehource_product_records ");
					sb.append(" SET is_vanning = 2 , ");
					sb.append(" modify_user_id = ");
					sb.append(user.getId());
					sb.append(" ,  modify_user_name = '");
					sb.append(StringUtil.dealParam(user.getUsername()));
					sb.append("' , modify_datetime = '");
					sb.append(DateUtil.getNow());

					sb.append("' WHERE code = '");
					sb.append(product.getCode());
					sb.append("' ");
					if (!dbOp.executeUpdate(sb.toString())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：更新销售后台处理单状态失败";
					}
					
					if (!afService.writeAfterSaleLog(user,  "PDA封箱,处理单号:" + afCode + ",封箱单号:"+seal.getCode(), 1, AfterSaleLogBean.TYPE9,afCode,null)) {
						dbOp.rollbackTransaction();
						return "写售后日志失败";
					}
				}
				dbOp.commitTransaction();
				return "?" + code;
			} catch (Exception e) {
				e.printStackTrace();
				dbOp.rollbackTransaction();
				return "发生异常";
			} finally {
				dbOp.release();
			}
		}
	}

	/**
	 * 完成售后退货入库任务
	 * <p>
	 * 本质为 售后库入库任务
	 * 
	 * @param ids
	 * @param inCargoWholeCode
	 * @return
	 */
	public String stockinForReturn(List<String> ids, voUser user) {

		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			WareService wareService = new WareService(dbOp);
			IAfterSalesService afterSaleService = ServiceFactory.createAfterSalesService(IBaseService.CONN_IN_SERVICE, dbOp);
			try {
				dbOp.startTransaction();
				// 判断是否正在盘点
				AfterSaleInventory afterSaleInventory = afService.getAfterSaleInventory(" status <>" + AfterSaleInventory.COMPLETE_CHECK);

				if (afterSaleInventory != null) {
					dbOp.rollbackTransaction();
					return "存在尚未完成的盘点单，请先盘点!";
				}
				for (String id : ids) {
					AfterSaleStockin stockin = afService.getAfterSaleStockin(" status = 0 AND after_sale_detect_product_id = " + Integer.parseInt(id));
					if (stockin == null) {
						dbOp.rollbackTransaction();
						return "售后退货入库任务[" + id + "]不存在";
					}

					AfterSaleDetectProductBean product = afService.getAfterSaleDetectProduct(" id = " + Integer.parseInt(id));
					if (product == null) {
						dbOp.rollbackTransaction();
						return "售后处理单[" + id + "]不存在";
					}
					//权限判断
					if(!afService.checkAfterSaleUserGroup(user, product.getAreaId())){
						dbOp.rollbackTransaction();
						return "没有处理单[" + product.getCode() + "]售后仓内作业权限!";
					}
					if (product.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
						dbOp.rollbackTransaction();
						return "此售后处理单["+product.getCode()+"]已锁定!";
					}

					AfterSaleOrderBean order = afterSaleService.getAfterSaleOrder(" id = " + product.getAfterSaleOrderId());
					if (order == null) {
						dbOp.rollbackTransaction();
						return "售后处理单[" + id + "]所关联的售后单不存在";
					}

					// 减少货位库存
					CargoInfoBean outCargoInfo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' ");
					CargoProductStockBean outCargoProductStock = cargoService.getCargoProductStock(" product_id = " + stockin.getProductId() + " AND cargo_id = " + outCargoInfo.getId());
					if (outCargoProductStock == null || outCargoProductStock.getStockCount() <= 0) {
						dbOp.rollbackTransaction();
						return "原货位库存不足";
					}
					if (!cargoService.updateCargoProductStockCount(outCargoProductStock.getId(), -1)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateCargoProductStockCount!";
					}

					// 减少商品库存
					ProductStockBean outProductStock = psService.getProductStock("product_id=" + stockin.getProductId() + " and area=" + outCargoInfo.getAreaId() + " and type=" + outCargoInfo.getStockType());
					if (outProductStock.getStock() <= 0) {
						dbOp.rollbackTransaction();
						return "原商品库存不足";
					}
					if (!psService.updateProductStockCount(outProductStock.getId(), -1)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateProductStockCount!";
					}

					// 增加货位库存
					CargoInfoBean inCargoInfo = cargoService.getCargoInfo(" area_id = " + outCargoInfo.getAreaId() + " AND stock_type = " + CargoInfoBean.STOCKTYPE_AFTER_SALE + " AND store_type = " + CargoInfoBean.STORE_TYPE2 + " AND status IN (0, 1) ");
					if (inCargoInfo == null) {
						dbOp.rollbackTransaction();
						return "目的货位不可用";
					}

					if (inCargoInfo.getStatus() == CargoInfoBean.STATUS1) {
						if (!cargoService.updateCargoInfo(" status = " + CargoInfoBean.STATUS0, " id = " + inCargoInfo.getId())) {
							dbOp.rollbackTransaction();
							return "数据库操作失败:updateCargoInfo";
						}
					}

					CargoProductStockBean inCargoProductStock = cargoService.getCargoProductStock(" product_id = " + stockin.getProductId() + " AND cargo_id = " + inCargoInfo.getId());
					if (inCargoProductStock == null) {
						inCargoProductStock = new CargoProductStockBean();
						inCargoProductStock.setCargoId(inCargoInfo.getId());
						inCargoProductStock.setProductId(product.getProductId());
						inCargoProductStock.setStockCount(1);
						inCargoProductStock.setStockLockCount(0);
						if (!cargoService.addCargoProductStock(inCargoProductStock)) {
							return "数据库操作失败：addCargoProductStock";
						}
						inCargoProductStock.setId(cargoService.getDbOp().getLastInsertId());
					} else {
						if (!cargoService.updateCargoProductStockCount(inCargoProductStock.getId(), 1)) {
							dbOp.rollbackTransaction();
							return "数据库操作失败:updateCargoProductStockCount!";
						}
					}

					// 增加商品库存
					ProductStockBean inProductStock = psService.getProductStock("product_id=" + stockin.getProductId() + " AND area=" + inCargoInfo.getAreaId() + " and type=" + inCargoInfo.getStockType());
					if (!psService.updateProductStockCount(inProductStock.getId(), 1)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateProductStockCount!";
					}
					
					StringBuffer sbSetStockin = new StringBuffer();
					sbSetStockin.append(" status = 1 , complete_datetime = '").append(DateUtil.getNow()).append("' ");
					sbSetStockin.append(" , in_cargo_whole_code = '").append(inCargoInfo.getWholeCode()).append("' ");
					sbSetStockin.append(" , out_cargo_whole_code = '").append(outCargoInfo.getWholeCode()).append("' ");
					sbSetStockin.append(" , complete_user_id = ").append(user.getId()).append(" ");
					sbSetStockin.append(" , complete_user_name = '").append(StringUtil.dealParam(user.getUsername())).append("' ");
					// 修改入库单状态、关联目的货位、源货位、完成人、完成时间
					if (!afService.updateAfterSaleStockin(sbSetStockin.toString(), " id = " + stockin.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败:updateAfterSaleStockin";
					}
					
					//修改订单信息
					String s = afService.updateOrderAfterSale(order.getOrderId(), dbOp, user);
					if ( s != null) {
						dbOp.rollbackTransaction();
						return s;
					}

					// 更改IMEI状态
					String result = this.updateIMEIForStockIn(user, dbOp, product);
					if (result != null) {
						dbOp.rollbackTransaction();
						return result;
					}

					// 查询销售订单
					voOrder vOrder = wareService.getOrder(order.getOrderId());
					if (vOrder == null) {
						dbOp.rollbackTransaction();
						return "售后处理单[" + id + "]所关联的订单不存在";
					}
					// 修改库存批次
					result = this.updateStockBatchForAfterSaleStockin(product.getAreaId(),dbOp, product.getProductId(), order.getAfterSaleOrderCode(), vOrder, user);
					if (result != null) {
						dbOp.rollbackTransaction();
						return result;
					}

					voProduct vProduct = wareService.getProduct(stockin.getProductId());
					vProduct.setPsList(psService.getProductStockList("product_id = " + vProduct.getId(), -1, -1, "id asc"));

					// 商品进销存卡片
					StockCardBean outsc = new StockCardBean();
					outsc.setCardType(StockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN);
					outsc.setCode(product.getCode()); // 售后处理单号
					outsc.setCreateDatetime(DateUtil.getNow());
					outsc.setStockType(outCargoInfo.getStockType());
					outsc.setStockArea(outCargoInfo.getAreaId());
					outsc.setProductId(stockin.getProductId());
					outsc.setStockId(outProductStock.getId());
					outsc.setStockOutCount(1);
					outsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
					outsc.setCurrentStock(vProduct.getStock(outsc.getStockArea(), outsc.getStockType()) + vProduct.getLockCount(outsc.getStockArea(), outsc.getStockType()));
					outsc.setStockAllArea(vProduct.getStock(outsc.getStockArea()) + vProduct.getLockCount(outsc.getStockType()));
					outsc.setStockAllType(vProduct.getStockAllType(outsc.getStockType()) + vProduct.getLockCountAllType(outsc.getStockType()));
					outsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					outsc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
					outsc.setAllStockPriceSum((new BigDecimal(outsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outsc.getStockPrice()))).doubleValue());
					if (!psService.addStockCard(outsc)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：addStockCard";
					}

					// 货位进销存卡片
					CargoStockCardBean outcsc = new CargoStockCardBean();
					outcsc.setCardType(CargoStockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN);
					outcsc.setCode(product.getCode()); // 售后处理单号
					outcsc.setCreateDatetime(DateUtil.getNow());
					outcsc.setStockType(outCargoInfo.getStockType());
					outcsc.setStockArea(outCargoInfo.getAreaId());
					outcsc.setProductId(vProduct.getId());
					outcsc.setStockId(outCargoProductStock.getId());
					outcsc.setStockOutCount(1);
					outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
					outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
					outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount() + outCargoProductStock.getStockLockCount());
					outcsc.setCargoStoreType(outCargoInfo.getStoreType());
					outcsc.setCargoWholeCode(outCargoInfo.getWholeCode());
					outcsc.setStockPrice(vProduct.getPrice5());
					outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
					if (!cargoService.addCargoStockCard(outcsc)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：addCargoStockCard";
					}
					AfterSaleOrderBean afterSaleOrder = afService.getAfterSaleOrder("id=" + product.getAfterSaleOrderId());
					if(afterSaleOrder == null){
						dbOp.rollbackTransaction();
						return "售后单不存在!";
					}
					float afterSalePrice = product.getPrice5(); 
					float price5 = product.getPrice5();
					voOrderProduct orderProduct = wareService.getOrderProductSplit(afterSaleOrder.getOrderId(), product.getCode());
					if(orderProduct == null){
						orderProduct = wareService.getOrderPresentSplit(afterSaleOrder.getId(), product.getCode());
					}
					//出库库存价丢失补救****
					if(orderProduct != null&&orderProduct.getPrice3() == 0){
						//获取出货前最后一条进销存记录
						int outId = service.getNumber("id", "stock_card", null, "code = '"+ afterSaleOrder.getOrderCode() +"' and card_type = "+StockCardBean.CARDTYPE_ORDERSTOCK+" and product_id = " + product.getId());
						int scId = service.getNumber("id", "stock_card", "max", "id < "+outId+" and product_id = "+ product.getId());
						StockCardBean stockCard = psService.getStockCard("id = "+scId);
						orderProduct.setPrice3(stockCard.getStockPrice());
					}
					if(orderProduct != null){
						int totalCount = vProduct.getStockAll() + vProduct.getLockCountAll();
						//StockBatchLogBean batchLog = service.getStockBatchLog("code='"+ afterSaleOrder.getOrderCode() +"' and product_id="+orderProduct.getProductId());
						price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * 1)) / (totalCount + 1) * 1000))/1000;
						afterSalePrice = orderProduct.getPrice3();
					} 
					// 商品进销存卡片
					StockCardBean insc = new StockCardBean();
					insc.setCardType(StockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN);
					insc.setCode(product.getCode()); // 售后处理单号
					insc.setCreateDatetime(DateUtil.getNow());
					insc.setStockType(inCargoInfo.getStockType());
					insc.setStockArea(inCargoInfo.getAreaId());
					insc.setProductId(stockin.getProductId());
					insc.setStockId(inProductStock.getId());
					insc.setStockInCount(1);
					insc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(afterSalePrice))).doubleValue());
					insc.setCurrentStock(vProduct.getStock(insc.getStockArea(), insc.getStockType()) + vProduct.getLockCount(insc.getStockArea(), insc.getStockType()));
					insc.setStockAllArea(vProduct.getStock(insc.getStockArea()) + vProduct.getLockCount(insc.getStockType()));
					insc.setStockAllType(vProduct.getStockAllType(insc.getStockType()) + vProduct.getLockCountAllType(insc.getStockType()));
					insc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					insc.setStockPrice(price5);// 新的库存价格，每次入库都要计算
					insc.setAllStockPriceSum((new BigDecimal(insc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(insc.getStockPrice()))).doubleValue());
					if (!psService.addStockCard(insc)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：addStockCard";
					}

					// 货位进销存卡片
					CargoStockCardBean incsc = new CargoStockCardBean();
					incsc.setCardType(CargoStockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN);
					incsc.setCode(product.getCode()); // 售后处理单号
					incsc.setCreateDatetime(DateUtil.getNow());
					incsc.setStockType(inCargoInfo.getStockType());
					incsc.setStockArea(inCargoInfo.getAreaId());
					incsc.setProductId(vProduct.getId());
					incsc.setStockId(inCargoProductStock.getId());
					incsc.setStockInCount(1);
					incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
					incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
					incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					incsc.setCurrentCargoStock(inCargoProductStock.getStockCount() + inCargoProductStock.getStockLockCount());
					incsc.setCargoStoreType(inCargoInfo.getStoreType());
					incsc.setCargoWholeCode(inCargoInfo.getWholeCode());
					incsc.setStockPrice(vProduct.getPrice5());
					incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
					if (!cargoService.addCargoStockCard(incsc)) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：addCargoStockCard";
					}

					// 如果是退货流程 则修改售后处理单状态 为 退货已完成[status = 14]
					String queryType = " SELECT type FROM after_sale_warehource_product_records WHERE code = '" + product.getCode() + "' ";
					int type = dbOp.getInt(queryType);
					// 1直接退货 2 扣费退货
					if (type == 1 || type == 2) {
						if (!afService.updateAfterSaleDetectProduct(" status = " + AfterSaleDetectProductBean.STATUS14, " id = " + product.getId())) {
							dbOp.rollbackTransaction();
							return "数据库操作失败：updateAfterSaleDetectProduct";
						}
					}

					// 修改售后处理单所关联的货位
					if (!afService.updateAfterSaleDetectProduct(" cargo_whole_code = '" + inCargoInfo.getWholeCode() + "' ", " id = " + product.getId())) {
						dbOp.rollbackTransaction();
						return "数据库操作失败：updateAfterSaleDetectProduct";
					}

					boolean changed = false;
					// 不修改售后单状态
					// 查询相关S单是否已发货，如果已发货则销售处理单after_sale_warehource_product_records状态改为处理已完成status=7，如果没发货则不改状态。
					AfterSaleDetectProductOrder pOrder = afService.getAfterSaleDetectProductOrder(" detect_code = '" + product.getCode() + "' ");
					if (pOrder != null) {
						vOrder = wareService.getOrder(pOrder.getOrderid());
						// 2 - 发货成功
						if (vOrder != null && vOrder.getStatus() == 2) {
							// 销售后台处理单 after_sale_warehource_product_records
							// status = 7 处理已完成
							StringBuffer sb = new StringBuffer();
							sb.append(" UPDATE after_sale_warehource_product_records ");
							sb.append(" SET status = 7, ");
							sb.append(" modify_user_id = ");
							sb.append(user.getId());
							sb.append(" ,  modify_user_name = '");
							sb.append(StringUtil.dealParam(user.getUsername()));
							sb.append("' , modify_datetime = '");
							sb.append(DateUtil.getNow());

							sb.append("' WHERE code = '");
							sb.append(product.getCode());
							sb.append("' ");
							if (!dbOp.executeUpdate(sb.toString())) {
								dbOp.rollbackTransaction();
								return "数据库操作失败：更新销售后台处理单状态失败";
							}
							changed = true;
						}
					}

					if (!changed) {
						StringBuffer sb = new StringBuffer();
						sb.append(" UPDATE after_sale_warehource_product_records ");
						sb.append(" SET modify_user_id = ");
						sb.append(user.getId());
						sb.append(" ,  modify_user_name = '");
						sb.append(StringUtil.dealParam(user.getUsername()));
						sb.append("' , modify_datetime = '");
						sb.append(DateUtil.getNow());

						sb.append("' WHERE code = '");
						sb.append(product.getCode());
						sb.append("' ");
						if (!dbOp.executeUpdate(sb.toString())) {
							dbOp.rollbackTransaction();
							return "数据库操作失败：更新销售后台处理单状态失败";
						}
					}

					if (!afService.writeAfterSaleLog(user,  "售后处理单[" + product.getCode() + "]的商品入售后库", 1, AfterSaleLogBean.TYPE10, product.getCode(),null)) {
						dbOp.rollbackTransaction();
						return "写售后日志失败";
					}
				}
				dbOp.commitTransaction();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				dbOp.rollbackTransaction();
				return "发生异常!";
			} finally {
				dbOp.release();
			}
		}
	}

	/**
	 * 完成售后库调拨，新建调拨单
	 * 
	 * @param inStockArea
	 *            目的库库地区
	 * @param inStockType
	 *            目的库库类型
	 * @param codes
	 *            售后处理单号列表
	 * @param area
	 *            当前库地区，当前应该为1
	 * @param user
	 *            当前操作用户
	 * @param flag
	 *            标记，PDA：1，其他情况待定
	 * @return
	 * @author mengqy
	 */
	public Json createAfterSaleAllot(int inStockArea, int inStockType, List<String> codes, voUser user, int flag, int stockExchangeId) {
		Json json = new Json();
		if (inStockArea < 0) {
			json.setMsg("请选择库地区");
			return json;
		}
		if (inStockType < 0) {
			json.setMsg("请选择库类型");
			return json;
		}
		
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(1474)) {
			json.setMsg( "您没有新建调拨单的权限!");			
			return json;
		}
		
		if (inStockType != ProductStockBean.STOCKTYPE_BACK 
				&& inStockType != ProductStockBean.STOCKTYPE_DEFECTIVE
				&& inStockType != ProductStockBean.STOCKTYPE_QUALIFIED
				&& inStockType != ProductStockBean.STOCKTYPE_RETURN
				&& inStockType != ProductStockBean.STOCKTYPE_CHECK 
				&& inStockType != ProductStockBean.STOCKTYPE_AFTER_SALE) {
			json.setMsg("目的库类型错误！");
			return json;
		}
		if (codes == null || codes.size() == 0) {
			json.setMsg("请输入售后处理单号");
			return json;
		}
		synchronized (lock) {

			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB);
			WareService wareService = new WareService(dbOp);
			AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			try {
				List<AfterSaleAllotData> allotDataList = new ArrayList<CommonLogic.AfterSaleAllotData>();
				// 排重
				Map<String, String> map = new HashMap<String, String>();
				
				for (String tempCode : codes) {
					map.put(tempCode, tempCode);
				}
				Set<String> set_one = map.keySet();
				//校验所选择处理单是否是同一地区
				List<Integer> tempList = new ArrayList<Integer>();
				for (String code : set_one) {
					AfterSaleDetectProductBean product = service.getAfterSaleDetectProduct(" code = '" + code + "' ");
					if(product != null){
						if(tempList.size() > 0){
							if(!tempList.contains(product.getAreaId())){
								json.setMsg("所选处理单必须是同一地区的！");
								return json;
							}
						} else {
							tempList.add(product.getAreaId());
						}
					}
				}
				int area = tempList.get(0);
				//权限判断
				if(!service.checkAfterSaleUserGroup(user, area)){
					json.setMsg("没有选中的处理单所属地区售后舱内作业权限！");
					return json;
				}
				dbOp.startTransaction();
				
				StockExchangeBean bean = null;
				if (stockExchangeId > 0) {
					bean = psService.getStockExchange("id=" + stockExchangeId);
					if (bean == null) {
						dbOp.rollbackTransaction();
						json.setMsg("没有找到调拨单！");
						return json;
					}
					
					if(bean.getCreateUserId() != user.getId()){
						dbOp.rollbackTransaction();
						json.setMsg("对不起，你没有权限编辑其他人的调拨单！");
						return json;
					}
					
					if (!psService.deleteStockExchangeProduct("stock_exchange_id=" + bean.getId())) {
						dbOp.rollbackTransaction();
						json.setMsg("删除调拨单商品失败！");
						return json;
					}
					
					if (!psService.deleteStockExchangeProductCargo("stock_exchange_id=" + bean.getId())) {
						dbOp.rollbackTransaction();
						json.setMsg("删除调拨单商品货位信息失败！");
						return json;
					}
					
					if (!service.getDbOp().executeUpdate("update after_sale_detect_product asdp, after_sale_stock_exchange_product assep set asdp.lock_status=" + AfterSaleDetectProductBean.LOCK_STATUS0 + " where asdp.id=assep.after_sale_detect_product_id and assep.stock_exchange_id=" + bean.getId() )) {
						dbOp.rollbackTransaction();
						json.setMsg("解除锁定失败！");
						return json;
					}
					
					if (!service.deleteAfterSaleStockExchangeProduct("stock_exchange_id=" + bean.getId())) {
						dbOp.rollbackTransaction();
						json.setMsg("删除调拨单与处理单关联信息失败！");
						return json;
					}
					
					String set = "status = " + StockExchangeBean.STATUS2 + ", confirm_datetime = now(), stock_out_oper=" + user.getId()+", stock_out_oper_name='"+user.getUsername()+"'";
					if(!psService.updateStockExchange(set, "id = " + bean.getId())) {
						service.getDbOp().rollbackTransaction();
						json.setMsg("数据库操作失败");
						return json;
					}
					
					// log记录
					StockAdminHistoryBean log = new StockAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("（售后）商品调配操作：" + bean.getName() + ": 确认完成出库操作");
					log.setType(StockAdminHistoryBean.CHANGE);
					if(!stockService.addStockAdminHistory(log)) {
						service.getDbOp().rollbackTransaction();
						json.setMsg("数据库操作失败");
						return json;
					}
				} else {
					// 添加调拨单
					bean = new StockExchangeBean();
					bean.setCreateDatetime(DateUtil.getNow());
					bean.setName(DateUtil.getNowDateStr() + "售后库调拨");
					bean.setRemark("");
					bean.setStatus(StockExchangeBean.STATUS2);
					bean.setConfirmDatetime(DateUtil.getNow());
					Calendar cal = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
					String brefCode = "DB" + sdf.format(cal.getTime());
					bean.setCode(brefCode);
					bean.setCreateUserId(user.getId());
					bean.setCreateUserName(user.getUsername());
					bean.setStockOutOper(user.getId());
					bean.setStockOutOperName(user.getUsername());
					bean.setAuditingUserName("");
					bean.setStockInOperName("");
					bean.setAuditingUserName2("");
					bean.setStockInArea(inStockArea);
					bean.setStockOutArea(area);
					bean.setStockInType(inStockType);
					bean.setStockOutType(CargoInfoBean.STOCKTYPE_AFTER_SALE);
					bean.setPriorStatus(StockExchangeBean.PRIOR_STATUS0);
					if (!psService.addStockExchange(bean)) {
						dbOp.rollbackTransaction();
						json.setMsg("数据库操作失败:addStockExchange");
						return json;
					}
					bean.setId(psService.getDbOp().getLastInsertId());
					
					// 此处修改调拨单Code
					String newCode = null;
					if (bean.getId() > 9999) {
						String strId = String.valueOf(bean.getId());
						newCode = strId.substring(strId.length() - 4, strId.length());
					} else {
						DecimalFormat df2 = new DecimalFormat("0000");
						newCode = df2.format(bean.getId());
					}
					String totalCode = brefCode + newCode;
					StringBuilder updateBuf = new StringBuilder();
					updateBuf.append("update stock_exchange set code = '" + totalCode + "' where id = ").append(bean.getId());
					if (!psService.getDbOp().executeUpdate(updateBuf.toString())) {
						dbOp.rollbackTransaction();
						json.setMsg("数据库操作失败:添加调拨单失败！");
						return json;
					}
					bean.setCode(totalCode);
					
					// log记录
					StockAdminHistoryBean log = new StockAdminHistoryBean();
					log.setAdminId(user.getId());
					log.setAdminName(user.getUsername());
					log.setLogId(bean.getId());
					log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("（售后）商品调配操作：" + bean.getName() + ": 确认完成出库操作");
					log.setType(StockAdminHistoryBean.CREATE);
					if(!stockService.addStockAdminHistory(log)) {
						service.getDbOp().rollbackTransaction();
						json.setMsg("数据库操作失败");
						return json;
					}
				}
				if (!CheckAndBuildList(inStockArea, inStockType, codes, area, json, dbOp, allotDataList)){
					service.getDbOp().rollbackTransaction();
					return json;
				}

				for (AfterSaleAllotData temp : allotDataList) {

					int productId = temp.getProductId();
					int productCount = temp.getCount();
					ProductStockBean outPs = temp.getOutPs();
					ProductStockBean inPs = temp.getInPs();

					StockExchangeProductBean sep = new StockExchangeProductBean();
					sep.setCreateDatetime(DateUtil.getNow());
					sep.setConfirmDatetime(null);
					sep.setStockExchangeId(bean.getId());
					sep.setProductId(productId);
					sep.setRemark("");
					sep.setStatus(StockExchangeProductBean.STOCKOUT_DEALED);
					sep.setStockOutCount(productCount);
					sep.setStockInCount(productCount);
					sep.setStockOutId(outPs.getId());
					sep.setStockInId(inPs.getId());
					sep.setReason(1);
					sep.setReasonText("");
					// 添加调拨单商品
					if (!psService.addStockExchangeProduct(sep)) {
						dbOp.rollbackTransaction();
						json.setMsg("数据库操作失败:调拨单商品添加失败！");
						return json;
					}
					sep.setId(psService.getDbOp().getLastInsertId());

					voProduct product = wareService.getProduct(productId);
					if (product == null) {
						dbOp.rollbackTransaction();
						json.setMsg("调拨单商品不存在！");
						return json;
					}
					// 锁商品库存
					if (!psService.updateProductStockCount(outPs.getId(), -productCount)) {
						dbOp.rollbackTransaction();
						json.setMsg("商品编号为[" + product.getCode() + "]源库存不足");
						return json;
					}
					if (!psService.updateProductLockCount(outPs.getId(), productCount)) {
						dbOp.rollbackTransaction();
						json.setMsg("数据库操作失败:锁库存失败");
						return json;
					}

					// 调拨单改为已确认
					String setConfirm = "remark = '操作前库存" + outPs.getStock() + ",操作后库存" + (outPs.getStock() - sep.getStockOutCount()) + "', confirm_datetime = now()";
					if (!psService.updateStockExchangeProduct(setConfirm, "id = " + sep.getId())) {
						dbOp.rollbackTransaction();
						json.setMsg("数据库操作失败:确认调拨单失败");
						return json;
					}

					for (AfterSaleAllotCargoData cargoData : temp.getCargoDataList()) {

						CargoProductStockBean inCps = cargoData.getInCps();
						CargoProductStockBean outCps = cargoData.getOutCps();
						int cargoCount = cargoData.getAsdpList().size();

						StockExchangeProductCargoBean sepcOut = new StockExchangeProductCargoBean();
						sepcOut.setStockExchangeProductId(sep.getId());
						sepcOut.setStockExchangeId(bean.getId());
						sepcOut.setStockCount(cargoCount);
						sepcOut.setCargoProductStockId(outCps.getId());
						sepcOut.setCargoInfoId(outCps.getCargoId());
						sepcOut.setType(0);
						if (!psService.addStockExchangeProductCargo(sepcOut)) {
							dbOp.rollbackTransaction();
							json.setMsg("数据库操作失败:addStockExchangeProductCargo");
							return json;
						}
						StockExchangeProductCargoBean sepcIn = new StockExchangeProductCargoBean();
						sepcIn.setStockExchangeProductId(sep.getId());
						sepcIn.setStockExchangeId(bean.getId());
						sepcIn.setStockCount(cargoCount);
						sepcIn.setCargoProductStockId(inCps.getId());
						sepcIn.setCargoInfoId(inCps.getCargoId());
						sepcIn.setType(1);
						if (!psService.addStockExchangeProductCargo(sepcIn)) {
							dbOp.rollbackTransaction();
							json.setMsg("数据库操作失败:addStockExchangeProductCargo");
							return json;
						}

						// 锁货位库存
						if (!cargoService.updateCargoProductStockCount(outCps.getId(), -cargoCount)) {
							dbOp.rollbackTransaction();
							json.setMsg("商品编号为[" + product.getCode() + "],货位为[" + cargoData.getOutCargoWholeCode() + "]的源货位库存不足");
							return json;
						}
						if (!cargoService.updateCargoProductStockLockCount(outCps.getId(), cargoCount)) {
							dbOp.rollbackTransaction();
							json.setMsg("数据库操作失败:锁库存失败");
							return json;
						}

						for (AfterSaleDetectProductBean asdp : cargoData.getAsdpList()) {
							AfterSaleStockExchangeProduct asep = new AfterSaleStockExchangeProduct();
							asep.setAfterSaleDetectProductId(asdp.getId());
							asep.setStockExchangeId(bean.getId());
							asep.setInCargoId(sepcIn.getCargoInfoId());
							if (!service.addAfterSaleStockExchangeProduct(asep)) {
								dbOp.rollbackTransaction();
								json.setMsg("数据库操作失败:addAfterSaleStockExchangeProduct");
								return json;
							}
							
							if (!service.updateAfterSaleDetectProduct("lock_status=" + AfterSaleDetectProductBean.LOCK_STATUS1, "id=" + asdp.getId())) {
								dbOp.rollbackTransaction();
								json.setMsg("锁定售后处理单失败！");
								return json;
							}
							
							if (!service.writeAfterSaleLog(user,  (flag == 1 ? "PDA":"Web") + "添加售后处理单[" + asdp.getCode() + "]到调拨单[" + bean.getCode() + "]", 1, AfterSaleLogBean.TYPE11,asdp.getCode(),null)) {
								dbOp.rollbackTransaction();
								json.setMsg("写售后日志失败");
								return json;
							}
						}						
					}					
				}

				dbOp.commitTransaction();
				json.setSuccess(true);
				json.setMsg(stockExchangeId > 0 ? "编辑调拨作业成功" : bean.getCode() + "调拨单添加成功");
				return json;
			} catch (Exception e) {
				e.printStackTrace();
				dbOp.rollbackTransaction();
				json.setMsg("发生异常");
				return json;
			} finally {
				dbOp.release();
			}
		}
	}

	private boolean CheckAndBuildList(int inStockArea, int inStockType, List<String> codes, int area, Json json, DbOperation dbOp, List<AfterSaleAllotData> allotDataList) throws SQLException {
		AfStockService service = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CargoInfoBean inCargoInfo = cargoService.getCargoInfo(" area_id = " + inStockArea + " AND stock_type = " + inStockType + " AND store_type = " + CargoInfoBean.STORE_TYPE2);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		if (inCargoInfo == null) {
			json.setMsg("没有找到目的货位");
			return false;
		}

		Map<String, String> map = new HashMap<String, String>();
		// 排重
		for (String tempCode : codes) {
			map.put(tempCode, tempCode);
		}
		Set<String> set = map.keySet();
		for (String code : set) {
			AfterSaleDetectProductBean product = service.getAfterSaleDetectProduct(" code = '" + code + "' ");
			if (product == null) {
				json.setMsg("售后处理单[" + code + "]不存在");
				return false;
			}
			if (product.getLockStatus() != AfterSaleDetectProductBean.LOCK_STATUS0) {
				json.setMsg("售后处理单[" + code + "]已锁定，不可以调拨");
				return false;
			}
			
//			List<AfterSaleBackSupplierProduct> list =service.getAfterSaleBackSupplierProductList(" after_sale_detect_product_id= "+product.getId(), -1, -1, "create_datetime desc");
//			if(list!=null && list.size()>0 ){
//				AfterSaleBackSupplierProduct afterSaleBackSupplierProduct = list.get(0);
//				if(!(afterSaleBackSupplierProduct.status==AfterSaleBackSupplierProduct.STATUS5 || afterSaleBackSupplierProduct.status==AfterSaleBackSupplierProduct.STATUS6 )){
//					json.setMsg("返厂维修中处理单[" + code + "]不能调拨");
//					return false;
//				}
//			}
			if(StringUtil.checkNull(product.getIMEI()).trim().length()>0){
				IMEIBean imeiBean =imeiService.getIMEI(" code = '"+product.getIMEI()+"'");
				if(imeiBean.getStatus()!=IMEIBean.IMEISTATUS2){
					json.setMsg("处理单"+code+"关联的IMEI码不是可出库状态不能调拨");
					return false;
				}
			}
			
			
			// @mengqy 没有必要判断修理单状态
			
			CargoInfoBean outCargoInfo = cargoService.getCargoInfo(" whole_code = '" + product.getCargoWholeCode() + "' ");
			if (outCargoInfo == null) {
				json.setMsg("售后处理单[" + code + "]关联货位不存在");
				return false;
			}

			if (outCargoInfo.getAreaId() != area || outCargoInfo.getStockType() != CargoInfoBean.STOCKTYPE_AFTER_SALE) {
				json.setMsg("售后处理单[" + code + "]已经不在售后库不可以调拨");
				return false;
			}
			
			// 两级检索 检索所属商品
			AfterSaleAllotData data = null;
			for (AfterSaleAllotData temp : allotDataList) {
				if (temp.getProductId() == product.getProductId()) {
					data = temp;
					break;
				}
			}
			if (data == null) {
				data = new AfterSaleAllotData();
				data.setCargoDataList(new ArrayList<CommonLogic.AfterSaleAllotCargoData>());
				data.setProductId(product.getProductId());

				ProductStockBean outPs = psService.getProductStock("product_id = " + product.getProductId() + " AND type = " + ProductStockBean.STOCKTYPE_AFTER_SALE + " AND area = " + area);
				if (outPs == null) {
					json.setMsg("源商品库存不足");
					return false;
				}
				data.setOutPs(outPs);
				ProductStockBean inPs = psService.getProductStock("product_id = " + product.getProductId() + " AND type = " + inStockType + " AND area = " + inStockArea);
				if (inPs == null) {
					json.setMsg("没有找到目的库商品库存");
					return false;
				}
				data.setInPs(inPs);
				allotDataList.add(data);
			}

			// 检索所属货位
			AfterSaleAllotCargoData cargoData = null;
			for (AfterSaleAllotCargoData temp : data.getCargoDataList()) {
				if (temp.getOutCargoWholeCode().equalsIgnoreCase(product.getCargoWholeCode())) {
					cargoData = temp;
					break;
				}
			}
			if (cargoData == null) {
				cargoData = new AfterSaleAllotCargoData();
				cargoData.setOutCargoWholeCode(product.getCargoWholeCode());

				CargoProductStockBean inCps = cargoService.getCargoProductStock(" product_id = " + product.getProductId() + " AND cargo_id = " + inCargoInfo.getId());
				if (inCps == null) {
					inCps = new CargoProductStockBean();
					inCps.setCargoId(inCargoInfo.getId());
					inCps.setProductId(product.getProductId());
					inCps.setStockCount(0);
					inCps.setStockLockCount(0);
					if (!cargoService.addCargoProductStock(inCps)) {
						json.setMsg("数据库操作失败：addCargoProductStock");
						return false;
					}
					inCps.setId(cargoService.getDbOp().getLastInsertId());
				}
				cargoData.setInCps(inCps);

				CargoProductStockBean outCps = cargoService.getCargoAndProductStock("cargo_id = " + outCargoInfo.getId() + " AND product_id = " + product.getProductId());
				if (outCps == null) {
					json.setMsg("源货位商品库存不足");
					return false;
				}
				cargoData.setOutCps(outCps);
				cargoData.setAsdpList(new ArrayList<AfterSaleDetectProductBean>());

				data.getCargoDataList().add(cargoData);
			}

			cargoData.getAsdpList().add(product);
		}

		return true;
	}

	class AfterSaleAllotData {
		private int productId;
		private ProductStockBean outPs;
		private ProductStockBean inPs;
		private List<AfterSaleAllotCargoData> cargoDataList;

		public int getProductId() {
			return productId;
		}

		public void setProductId(int productId) {
			this.productId = productId;
		}

		public ProductStockBean getOutPs() {
			return outPs;
		}

		public void setOutPs(ProductStockBean outPs) {
			this.outPs = outPs;
		}

		public ProductStockBean getInPs() {
			return inPs;
		}

		public void setInPs(ProductStockBean inPs) {
			this.inPs = inPs;
		}

		public List<AfterSaleAllotCargoData> getCargoDataList() {
			return cargoDataList;
		}

		public void setCargoDataList(List<AfterSaleAllotCargoData> cargoDataList) {
			this.cargoDataList = cargoDataList;
		}

		public int getCount() {
			int count = 0;
			for (AfterSaleAllotCargoData cargo : this.cargoDataList) {
				count += cargo.getAsdpList().size();
			}
			return count;
		}
	}

	class AfterSaleAllotCargoData {
		private List<AfterSaleDetectProductBean> asdpList;
		private String outCargoWholeCode;
		private CargoProductStockBean outCps;
		private CargoProductStockBean inCps;

		public List<AfterSaleDetectProductBean> getAsdpList() {
			return asdpList;
		}

		public void setAsdpList(List<AfterSaleDetectProductBean> asdpList) {
			this.asdpList = asdpList;
		}

		public String getOutCargoWholeCode() {
			return outCargoWholeCode;
		}

		public void setOutCargoWholeCode(String outCargoWholeCode) {
			this.outCargoWholeCode = outCargoWholeCode;
		}

		public CargoProductStockBean getOutCps() {
			return outCps;
		}

		public void setOutCps(CargoProductStockBean outCps) {
			this.outCps = outCps;
		}

		public CargoProductStockBean getInCps() {
			return inCps;
		}

		public void setInCps(CargoProductStockBean inCps) {
			this.inCps = inCps;
		}
	}

	/**
	 * 售后退货入库修改批次、外部控制事务
	 * 
	 * @param db
	 * @param productId
	 *            商品id
	 * @param afterSaleOrderCode
	 *            售后单号
	 * @param order
	 *            销售订单
	 * @param user
	 *            当前操作用户
	 * @return 执行成功返回 null，执行错误返回错误原因
	 */
	public String updateStockBatchForAfterSaleStockin(int areaId,DbOperation db, int productId, String afterSaleOrderCode, voOrder order, voUser user) {
		try {
			IProductStockService ipsServ = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, db);
			String sql = "product_id="+ productId +" and area="+ areaId +" and type="+ ProductStockBean.STOCKTYPE_AFTER_SALE;
			ProductStockBean productStock = ipsServ.getProductStock(sql);
			
			//根据业务类型采集财务基础数据
			BaseProductInfo baseProductInfo = new BaseProductInfo();
			baseProductInfo.setId(productId);
			baseProductInfo.setInCount(1);
			baseProductInfo.setProductStockId(productStock.getId());
			FinanceSaleBaseDataService financeService = FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(
					FinanceStockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN, db.getConn());
			financeService.acquireFinanceAfterSaleBaseData(order.getCode(), afterSaleOrderCode, 
					user.getId(),DateUtil.getNow(), ProductStockBean.STOCKTYPE_AFTER_SALE, areaId, 
					FinanceStockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN, Arrays.asList(baseProductInfo));
		} catch (Exception e) {
			e.printStackTrace();
			return "发生异常";
		}
		return null;
		
		
		/**
		WareService adminServ = new WareService(db);
		IStockService stockServ = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminServ.getDbOp());
		IProductStockService psServ = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, adminServ.getDbOp());
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, adminServ.getDbOp());
		PreparedStatement pstmt = null;
		PreparedStatement reps = null;
		ResultSet rs = null;
		ResultSet rers = null;
		ResultSet rs0 = null;
		try {
			// 将订单数据写入发货信息表----liuruilan---2012-11-22-挪至此处------
			float price = 0.0f; // 退货产品在销售时的实际金额
			int fsId = 0;
			int deliverType = 0;
			if (voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()) != null) {
				deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()).toString());
			}
			FinanceSellBean fsBean = new FinanceSellBean();
			fsBean.setOrderId(order.getId());
			fsBean.setCode(afterSaleOrderCode); // 单据号为售后单号
			fsBean.setPrice(price); // 退货单金额为负，计算时已经是负，此处不再加负号
			fsBean.setCarriage(0); // 此处并未对运费退回进行计算
			fsBean.setCharge(0);
			fsBean.setBuyMode(order.getBuyMode());
			fsBean.setPayMode(order.getBuyMode()); // 备用字段，取值赞同buyMode
			fsBean.setDeliverType(deliverType);
			fsBean.setCreateDatetime(DateUtil.getNow());
			fsBean.setPackageNum(order.getPackageNum());
			fsBean.setDataType(2); // 2-售后退回
			fsBean.setCount(-1);
			if (!frfService.addFinanceSellBean(fsBean))
				return "数据库操作失败：addFinanceSellBean";

			fsId = db.getLastInsertId();
			// ------------------liuruilan------------------

			voProduct product = adminServ.getProduct(productId); // 产品
			product.setPsList(psServ.getProductStockList("product_id=" + product.getId(), -1, -1, null));
			voOrderProduct orderProduct = null;
			orderProduct = adminServ.getOrderProductSplit(order.getId(), product.getCode());
			if (orderProduct == null) {
				orderProduct = adminServ.getOrderPresentSplit(order.getId(), product.getCode());
			}

			String condition = "code='" + order.getCode() + "' and product_id=" + product.getId() + " and remark = '订单出货'";
			List batchLogList = stockServ.getStockBatchLogList(condition, -1, -1, "id desc"); // 批次信息

			condition = "product_id=" + product.getId() +" and area=" + areaId + " and type=" + ProductStockBean.STOCKTYPE_AFTER_SALE;
			ProductStockBean inPs = psServ.getProductStock(condition); // 退货改为入售后库

			int count = 1;
			float price5 = product.getPrice5(); // 库存价格
			double stockinPrice = 0.00d;
			if (orderProduct != null) {
				int totalCount = product.getStockAll() + product.getLockCountAll();
				StockBatchLogBean batchLog = stockServ.getStockBatchLog("code='" + order.getCode() + "' and product_id=" + product.getId() + " and remark = '订单出货'");
				if (batchLog == null) {
					price5 = ((float) Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * count)) / (totalCount + count) * 1000)) / 1000;
				}
			}

			if (batchLogList == null || batchLogList.size() == 0) {
				String code = "S" + DateUtil.getNow().substring(0, 10).replace("-", "");
				StockBatchBean newBatch;
				newBatch = stockServ.getStockBatch("code like '" + code + "%' and product_id=" + product.getId());
				int ticket = 0;
				int _count = FinanceProductBean.queryCountIfTicket(db, product.getId(), ticket);
				if (newBatch == null) {
					// 当日第一份批次记录，编号最后三位 001
					code += "001";
				} else {
					// 获取当日计划编号最大值
					newBatch = stockServ.getStockBatch("code like '" + code + "%' and product_id=" + product.getId() + " order by id desc limit 1");
					String _code = newBatch.getCode();
					int number = Integer.parseInt(_code.substring(_code.length() - 3));
					number++;
					code += String.format("%03d", new Object[] { new Integer(number) });
				}
				// int batchId = stockServ.getNumber("id", "stock_batch","max",
				// "id > 0") + 1;
				newBatch = new StockBatchBean();
				newBatch.setCode(code);
				newBatch.setProductId(product.getId());
				newBatch.setProductStockId(inPs.getId());
				newBatch.setStockArea(inPs.getArea());
				newBatch.setStockType(ProductStockBean.STOCKTYPE_AFTER_SALE);
				newBatch.setCreateDateTime(DateUtil.getNow());
				newBatch.setPrice((orderProduct == null) ? product.getPrice5() : orderProduct.getPrice3());
				newBatch.setBatchCount(count);
				newBatch.setTicket(ticket);
				if (!stockServ.addStockBatch(newBatch))
					return "数据库操作失败:addStockBatch";

				// 添加批次操作记录
				// id = stockServ.getNumber("id", "stock_batch_log","max",
				// "id > 0") + 1;
				StockBatchLogBean batchLog = new StockBatchLogBean();
				batchLog.setCode(order.getCode());
				batchLog.setStockType(ProductStockBean.STOCKTYPE_AFTER_SALE);
				batchLog.setStockArea(inPs.getArea());
				batchLog.setBatchCode(newBatch.getCode());
				batchLog.setBatchCount(count);
				batchLog.setBatchPrice(newBatch.getPrice());
				batchLog.setProductId(newBatch.getProductId());
				batchLog.setRemark("售后退货入库");
				batchLog.setCreateDatetime(DateUtil.getNow());
				batchLog.setUserId(user.getId());
				if (!stockServ.addStockBatchLog(batchLog))
					return "数据库操作失败:addStockBatchLog";

				stockinPrice = batchLog.getBatchCount() * batchLog.getBatchPrice();

				// 财务产品信息表---liuruilan-----2012-11-02-----
				FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + product.getId());

				int totalCount = product.getStockAll() + product.getLockCountAll();
				int stockinCount = count;
				float priceSum = Arith.mul(price5, totalCount + stockinCount);

				// 计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
				float priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(batchLog.getBatchPrice(), stockinCount)), Arith.add(_count, stockinCount)), 2);
				float priceSumHasticket = Arith.mul(priceHasticket, stockinCount + _count);
				String set = "price =" + price5 + ", price_sum =" + priceSum + ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
				if (!frfService.updateFinanceProductBean(set, "product_id = " + product.getId()))
					return "数据库操作失败：updateFinanceProductBean";
				if (reps != null) {
					reps.close();
				}
				// 财务进销存卡片
				int currentStock = FinanceStockCardBean.getCurrentStockCount(db, batchLog.getStockArea(), batchLog.getStockType(), ticket, product.getId());
				int stockAllType = FinanceStockCardBean.getCurrentStockCount(db, -1, batchLog.getStockType(), ticket, product.getId());
				int stockAllArea = FinanceStockCardBean.getCurrentStockCount(db, batchLog.getStockArea(), -1, ticket, product.getId());
				product.setPsList(psServ.getProductStockList("product_id=" + product.getId(), -1, -1, null));
				FinanceStockCardBean fsc = new FinanceStockCardBean();
				fsc.setCardType(StockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN);
				fsc.setCode(order.getCode());
				fsc.setCreateDatetime(DateUtil.getNow());
				fsc.setStockType(batchLog.getStockType());
				fsc.setStockArea(batchLog.getStockArea());
				fsc.setProductId(product.getId());
				fsc.setStockId(inPs.getId());
				fsc.setStockInCount(-count);
				fsc.setCurrentStock(currentStock); // 只记录分库总库存
				fsc.setStockAllArea(stockAllArea);
				fsc.setStockAllType(stockAllType);
				fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
				fsc.setStockPrice(price5);

				fsc.setType(fsc.getCardType());
				fsc.setIsTicket(ticket);
				fsc.setStockBatchCode(batchLog.getBatchCode());
				fsc.setBalanceModeStockCount(stockinCount + _count);
				fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(batchLog.getBatchPrice(), stockinCount))));
				fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
				double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
				fsc.setAllStockPriceSum(tmpPrice);
				if (!frfService.addFinanceStockCardBean(fsc))
					return "数据库操作失败：addFinanceStockCardBean";

				// 将订单商品写入销售商品信息表--商品
				int returnCount = count; // 本次退回数量
				int supplierId = FinanceSellProductBean.querySupplier(db, batchLog.getBatchCode());
				String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 " + "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " + "WHERE h.order_id = ? ";
				db.prepareStatement(sql);
				pstmt = db.getPStmt();
				pstmt.setInt(1, order.getId());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int pId = rs.getInt("id");
					int buyCount = rs.getInt("count");
					if (product.getId() == rs.getInt("id")) {
						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " + "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
						db.prepareStatement(sql);
						pstmt = db.getPStmt();
						pstmt.setInt(1, pId);
						rs0 = pstmt.executeQuery();
						int productLine = 0;
						if (rs0.next()) {
							productLine = rs0.getInt("product_line_id");
						}

						sql = "SELECT SUM(buy_count) returnCount FROM finance_sell_product WHERE data_type = 5 " + " AND order_id = ? AND product_id = ? GROUP BY order_id";
						db.prepareStatement(sql);
						pstmt = db.getPStmt();
						pstmt.setInt(1, order.getId());
						pstmt.setInt(2, pId);
						rs0 = pstmt.executeQuery();
						int returnedCount = 0; // 已退回商品数
						if (rs0.next()) {
							returnedCount = Math.abs(rs0.getInt("returnCount"));
						}
						int allowCount = buyCount - returnedCount; // 最大允许退回数
						int _returnCount = 0; // 本次最终退回数
						if (returnCount > allowCount) {
							_returnCount = allowCount;
							returnCount -= allowCount;
						} else {
							_returnCount = returnCount;
							returnCount = 0;
						}
						FinanceSellProductBean fspBean = new FinanceSellProductBean();
						fspBean.setOrderId(order.getId());
						fspBean.setProductId(rs.getInt("id"));
						fspBean.setBuyCount(-_returnCount);
						fspBean.setPrice(rs.getFloat("price"));
						fspBean.setDprice(rs.getFloat("dprice"));
						fspBean.setPrice5(rs.getFloat("price5"));
						fspBean.setProductLine(productLine); // 财务用产品线
						fspBean.setParentId1(rs.getInt("parent_id1"));
						fspBean.setParentId2(rs.getInt("parent_id2"));
						fspBean.setParentId3(rs.getInt("parent_id3"));
						fspBean.setCreateDatetime(DateUtil.getNow());
						fspBean.setDataType(4); // 4-商品售后退回
						fspBean.setBalanceMode(ticket);
						fspBean.setSupplierId(supplierId);
						fspBean.setFinanceSellId(fsId);
						if (!frfService.addFinanceSellProductBean(fspBean))
							return "数据库操作失败:addFinanceSellProductBean";

						price = Arith.add(price, Arith.mul(fspBean.getDprice(), fspBean.getBuyCount()));
					}
				}

				// 赠品
				sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 " + "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " + "WHERE h.order_id = ? ";
				db.prepareStatement(sql);
				pstmt = db.getPStmt();
				pstmt.setInt(1, order.getId());
				rs = pstmt.executeQuery();
				while (rs.next()) {
					int pId = rs.getInt("id");
					int buyCount = rs.getInt("count");
					if (product.getId() == rs.getInt("id")) {
						sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " + "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
						db.prepareStatement(sql);
						pstmt = db.getPStmt();
						pstmt.setInt(1, pId);
						rs0 = pstmt.executeQuery();
						int productLine = 0;
						if (rs0.next()) {
							productLine = rs0.getInt("product_line_id");
						}
						sql = "SELECT SUM(buy_count) returnCount FROM finance_sell_product WHERE data_type = 5 " + " AND order_id = ? AND product_id = ? GROUP BY order_id";
						db.prepareStatement(sql);
						pstmt = db.getPStmt();
						pstmt.setInt(1, order.getId());
						pstmt.setInt(2, pId);
						rs0 = pstmt.executeQuery();
						int returnedCount = 0; // 已退回商品数
						if (rs0.next()) {
							returnedCount = Math.abs(rs0.getInt("returnCount"));
						}
						int allowCount = buyCount - returnedCount; // 最大允许退回数
						int _returnCount = 0; // 本次最终退回数
						if (returnCount > allowCount) {
							_returnCount = allowCount;
							returnCount -= allowCount;
						} else {
							_returnCount = returnCount;
							returnCount = 0;
						}
						FinanceSellProductBean fspBean = new FinanceSellProductBean();
						fspBean.setOrderId(order.getId());
						fspBean.setProductId(rs.getInt("id"));
						fspBean.setBuyCount(-_returnCount);
						fspBean.setPrice(rs.getFloat("price"));
						fspBean.setDprice(rs.getFloat("dprice"));
						fspBean.setPrice5(rs.getFloat("price5"));
						fspBean.setProductLine(productLine); // 财务用产品线
						fspBean.setParentId1(rs.getInt("parent_id1"));
						fspBean.setParentId2(rs.getInt("parent_id2"));
						fspBean.setParentId3(rs.getInt("parent_id3"));
						fspBean.setCreateDatetime(DateUtil.getNow());
						fspBean.setDataType(5); // 5-赠品售后退回
						fspBean.setBalanceMode(ticket);
						fspBean.setSupplierId(supplierId);
						fspBean.setFinanceSellId(fsId);
						if (!frfService.addFinanceSellProductBean(fspBean))
							return "数据库操作失败：addFinanceSellProductBean";
					}
				}
				// -------------liuruilan-------------

			} else {
				Iterator batchIter = batchLogList.listIterator();
				int batchCount = 0;
				int stockinCount = count;
				while (batchIter.hasNext() && count > 0) {
					StockBatchLogBean batchLog = (StockBatchLogBean) batchIter.next();
					StockBatchBean batch = stockServ.getStockBatch("code = '" + batchLog.getBatchCode() + "' and product_id=" + batchLog.getProductId() + " and stock_type=" + ProductStockBean.STOCKTYPE_AFTER_SALE + " and stock_area=" + inPs.getArea());
					int ticket = FinanceSellProductBean.queryTicket(db, batchLog.getBatchCode()); // 是否含票
					int _count = FinanceProductBean.queryCountIfTicket(db, product.getId(), ticket);
					if (batch != null) {
						if (count <= batchLog.getBatchCount()) {
							if (!stockServ.updateStockBatch("batch_count = batch_count+" + count, "id=" + batch.getId()))
								return "数据库操作失败：updateStockBatch";
							batchCount = count;
							stockinCount = 0;
						} else {
							if (!stockServ.updateStockBatch("batch_count = batch_count+" + batchLog.getBatchCount(), "id=" + batch.getId()))
								return "数据库操作失败：updateStockBatch";
							stockinCount -= batchLog.getBatchCount();
							batchCount = batchLog.getBatchCount();
						}
					} else {
						StockBatchBean newBatch = new StockBatchBean();
						newBatch.setCode(batchLog.getBatchCode());
						newBatch.setProductId(product.getId());
						newBatch.setProductStockId(inPs.getId());
						newBatch.setStockArea(inPs.getArea());
						newBatch.setStockType(ProductStockBean.STOCKTYPE_AFTER_SALE);
						newBatch.setCreateDateTime(stockServ.getStockBatchCreateDatetime(batchLog.getBatchCode(), product.getId()));
						newBatch.setPrice(batchLog.getBatchPrice());
						newBatch.setTicket(ticket);
						if (stockinCount <= batchLog.getBatchCount()) {
							newBatch.setBatchCount(stockinCount);
							batchCount = stockinCount;
							stockinCount = 0;
						} else {
							newBatch.setBatchCount(batchLog.getBatchCount());
							stockinCount -= batchLog.getBatchCount();
							batchCount = batchLog.getBatchCount();
						}
						if (!stockServ.addStockBatch(newBatch))
							return "数据库操作失败：addStockBatch";
					}
					stockinPrice = stockinPrice + batchLog.getBatchPrice() * batchCount;

					// 添加批次操作记录
					StockBatchLogBean newBatchLog = new StockBatchLogBean();
					newBatchLog.setCode(order.getCode());
					newBatchLog.setStockType(ProductStockBean.STOCKTYPE_AFTER_SALE);
					newBatchLog.setStockArea(inPs.getArea());
					newBatchLog.setBatchCode(batchLog.getBatchCode());
					newBatchLog.setBatchCount(batchCount);
					newBatchLog.setBatchPrice(batchLog.getBatchPrice());
					newBatchLog.setProductId(batchLog.getProductId());
					newBatchLog.setRemark("售后退货入库");
					newBatchLog.setCreateDatetime(DateUtil.getNow());
					newBatchLog.setUserId(user.getId());
					if (!stockServ.addStockBatchLog(newBatchLog))
						return "数据库操作失败：addStockBatchLog";

					// 财务产品信息表---liuruilan-----2012-11-01-----
					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + product.getId());
					int totalCount = product.getStockAll() + product.getLockCountAll();
					float priceSum = Arith.mul(price5, totalCount + batchCount);
					float priceHasticket = 0;
					float priceNoticket = 0;
					float priceSumHasticket = 0;
					float priceSumNoticket = 0;
					String set = "price =" + price5 + ", price_sum =" + priceSum;

					float sqlPrice5 = 0;// 获得销售出库价
					if (ticket == 0) { // 0-有票
						// 计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
						String sqlPrice = "select price5 from finance_sell_product where product_id=" + batchLog.getProductId() + " and data_type=0 and balance_mode=" + ticket + " and order_id=" + order.getId();
						reps = frfService.getDbOp().getConn().prepareStatement(sqlPrice);
						rers = reps.executeQuery();

						boolean flag = false;// 为false,finance_sell_product
												// 没有记录，为true,finance_sell_product有记录
						while (rers.next()) {
							flag = true;
							sqlPrice5 = rers.getFloat(1);
						}
						if (!flag) {

							// 如果finance_sell_product
							// 没有记录要在user_order_product_split_history里面找
							sqlPrice = "select price5 from user_order_product_split_history psplit where product_id=" + product.getId() + " and order_id=" + order.getId();
							reps = frfService.getDbOp().getConn().prepareStatement(sqlPrice);
							rers = reps.executeQuery();
							while (rers.next()) {
								flag = true;
								sqlPrice5 = rers.getFloat("price5");
							}
							if (!flag) {// 如果user_order_product_split_history
										// 没有记录要在user_order_present_split_history里面找
								sqlPrice = "select price5 from user_order_present_split_history psplit where product_id=" + product.getId() + " and order_id=" + order.getId();
								reps = frfService.getDbOp().getConn().prepareStatement(sqlPrice);
								rers = reps.executeQuery();
								while (rers.next()) {
									flag = true;
									sqlPrice5 = rers.getFloat("price5");
								}
							}
						}
						// -------------------------------获得出库价end
						// --------------------------------------------

						priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(sqlPrice5, batchCount)), Arith.add(_count, batchCount)), 2);
						priceSumHasticket = Arith.mul(priceHasticket, batchCount + _count);
						set += ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
					}
					if (ticket == 1) { // 1-无票
						boolean flag = false;
						String sqlPrice = "select price5 from finance_sell_product where product_id=" + batchLog.getProductId() + " and data_type=0 and balance_mode=" + ticket + " and order_id=" + order.getId();
						reps = frfService.getDbOp().getConn().prepareStatement(sqlPrice);
						rers = reps.executeQuery();

						while (rers.next()) {
							flag = true;
							sqlPrice5 = rers.getFloat(1);
						}
						if (!flag) {// 如果user_order_product_split_history
									// 没有记录要在user_order_present_split_history里面找
							sqlPrice = "select price5 from user_order_present_split_history psplit where product_id=" + product.getId() + " and order_id=" + order.getId();
							reps = frfService.getDbOp().getConn().prepareStatement(sqlPrice);
							rers = reps.executeQuery();
							while (rers.next()) {
								flag = true;
								sqlPrice5 = rers.getFloat("price5");
							}
						}
						priceNoticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumNoticket(), Arith.mul(sqlPrice5, batchCount)), Arith.add(_count, batchCount)), 2);
						priceSumNoticket = Arith.mul(priceNoticket, batchCount + _count);
						set += ", price_noticket =" + priceNoticket + ", price_sum_noticket =" + priceSumNoticket;
					}
					if (!frfService.updateFinanceProductBean(set, "product_id = " + product.getId()))
						return "数据库操作失败:updateFinanceProductBean";

					// 财务进销存卡片
					int currentStock = FinanceStockCardBean.getCurrentStockCount(db, batchLog.getStockArea(), batchLog.getStockType(), ticket, product.getId());
					int stockAllType = FinanceStockCardBean.getCurrentStockCount(db, -1, batchLog.getStockType(), ticket, product.getId());
					int stockAllArea = FinanceStockCardBean.getCurrentStockCount(db, batchLog.getStockArea(), -1, ticket, product.getId());
					product.setPsList(psServ.getProductStockList("product_id=" + product.getId(), -1, -1, null));
					FinanceStockCardBean fsc = new FinanceStockCardBean();
					fsc.setCardType(StockCardBean.CARDTYPE_AFTERSALESCANCELSTOCKIN);
					fsc.setCode(order.getCode());
					fsc.setCreateDatetime(DateUtil.getNow());
					fsc.setStockType(batchLog.getStockType());
					fsc.setStockArea(batchLog.getStockArea());
					fsc.setProductId(product.getId());
					fsc.setStockId(inPs.getId());
					fsc.setStockInCount(-batchCount);
					fsc.setCurrentStock(currentStock); // 只记录分库总库存
					fsc.setStockAllArea(stockAllArea);
					fsc.setStockAllType(stockAllType);
					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
					fsc.setStockPrice(price5);

					fsc.setType(fsc.getCardType());
					fsc.setIsTicket(ticket);
					fsc.setStockBatchCode(batchLog.getBatchCode());
					fsc.setBalanceModeStockCount(batchCount + _count);
					fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(sqlPrice5, batchCount))));
					if (ticket == 0) {
						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
					}
					if (ticket == 1) {
						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceNoticket)));
					}
					double tmpPrice = Arith.add(Double.parseDouble(String.valueOf(fProduct.getPriceSumNoticket())), Double.parseDouble(String.valueOf(fProduct.getPriceSumHasticket())));
					fsc.setAllStockPriceSum(tmpPrice);
					if (!frfService.addFinanceStockCardBean(fsc))
						return "数据库操作失败：addFinanceStockCardBean";

					// 将订单商品写入销售商品信息表--商品
					int returnCount = batchCount; // 本批总退回数量
					int supplierId = FinanceSellProductBean.querySupplier(db, batchLog.getBatchCode());
					String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 " + "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " + "WHERE h.order_id = ? ";
					db.prepareStatement(sql);
					pstmt = db.getPStmt();
					pstmt.setInt(1, order.getId());
					rs = pstmt.executeQuery();
					while (rs.next()) {
						if (product.getId() == rs.getInt("id")) {
							int pId = rs.getInt("id");
							int buyCount = rs.getInt("count");
							sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " + "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
							db.prepareStatement(sql);
							pstmt = db.getPStmt();
							pstmt.setInt(1, pId);
							rs0 = pstmt.executeQuery();
							int productLine = 0;
							if (rs0.next()) {
								productLine = rs0.getInt("product_line_id");
							}

							sql = "SELECT SUM(buy_count) returnCount FROM finance_sell_product WHERE data_type = 4 " + " AND order_id = ? AND product_id = ? GROUP BY order_id";
							db.prepareStatement(sql);
							pstmt = db.getPStmt();
							pstmt.setInt(1, order.getId());
							pstmt.setInt(2, pId);
							rs0 = pstmt.executeQuery();
							int returnedCount = 0; // 已退回商品数
							if (rs0.next()) {
								returnedCount = Math.abs(rs0.getInt("returnCount"));
							}
							int allowCount = buyCount - returnedCount; // 最大允许退回数
							int _returnCount = 0; // 本次最终退回数
							if (returnCount > allowCount) {
								_returnCount = allowCount;
								returnCount -= allowCount;
							} else {
								_returnCount = returnCount;
								returnCount = 0;
							}

							FinanceSellProductBean fspBean = new FinanceSellProductBean();
							fspBean.setOrderId(order.getId());
							fspBean.setProductId(rs.getInt("id"));
							fspBean.setBuyCount(-_returnCount);
							fspBean.setPrice(rs.getFloat("price"));
							fspBean.setDprice(rs.getFloat("dprice"));
							fspBean.setPrice5(sqlPrice5);
							fspBean.setProductLine(productLine); // 财务用产品线
							fspBean.setParentId1(rs.getInt("parent_id1"));
							fspBean.setParentId2(rs.getInt("parent_id2"));
							fspBean.setParentId3(rs.getInt("parent_id3"));
							fspBean.setCreateDatetime(DateUtil.getNow());
							fspBean.setDataType(4); // 4-商品售后退回
							fspBean.setBalanceMode(ticket);
							fspBean.setSupplierId(supplierId);
							fspBean.setFinanceSellId(fsId);
							if (!frfService.addFinanceSellProductBean(fspBean))
								return "数据库操作失败：addFinanceSellProductBean";

							price = Arith.add(price, Arith.mul(fspBean.getDprice(), fspBean.getBuyCount()));
						}
					}

					// 赠品
					sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 " + "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " + "WHERE h.order_id = ? ";
					db.prepareStatement(sql);
					pstmt = db.getPStmt();
					pstmt.setInt(1, order.getId());
					rs = pstmt.executeQuery();
					while (rs.next()) {
						int pId = rs.getInt("id");
						int buyCount = rs.getInt("count");
						if (product.getId() == rs.getInt("id")) {
							sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " + "ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
							db.prepareStatement(sql);
							pstmt = db.getPStmt();
							pstmt.setInt(1, pId);
							rs0 = pstmt.executeQuery();
							int productLine = 0;
							if (rs0.next()) {
								productLine = rs0.getInt("product_line_id");
							}
							sql = "SELECT SUM(buy_count) returnCount FROM finance_sell_product WHERE data_type = 5 " + " AND order_id = ? AND product_id = ? GROUP BY order_id";
							db.prepareStatement(sql);
							pstmt = db.getPStmt();
							pstmt.setInt(1, order.getId());
							pstmt.setInt(2, pId);
							rs0 = pstmt.executeQuery();
							int returnedCount = 0; // 已退回商品数
							if (rs0.next()) {
								returnedCount = Math.abs(rs0.getInt("returnCount"));
							}
							int allowCount = buyCount - returnedCount; // 最大允许退回数
							int _returnCount = 0; // 本次最终退回数
							if (returnCount > allowCount) {
								_returnCount = allowCount;
								returnCount -= allowCount;
							} else {
								_returnCount = returnCount;
								returnCount = 0;
							}
							FinanceSellProductBean fspBean = new FinanceSellProductBean();
							fspBean.setOrderId(order.getId());
							fspBean.setProductId(rs.getInt("id"));
							fspBean.setBuyCount(-_returnCount);
							fspBean.setPrice(rs.getFloat("price"));
							fspBean.setDprice(rs.getFloat("dprice"));
							fspBean.setPrice5(sqlPrice5);
							fspBean.setProductLine(productLine); // 财务用产品线
							fspBean.setParentId1(rs.getInt("parent_id1"));
							fspBean.setParentId2(rs.getInt("parent_id2"));
							fspBean.setParentId3(rs.getInt("parent_id3"));
							fspBean.setCreateDatetime(DateUtil.getNow());
							fspBean.setDataType(5); // 5-赠品售后退回
							fspBean.setBalanceMode(ticket);
							fspBean.setSupplierId(supplierId);
							fspBean.setFinanceSellId(fsId);
							if (!frfService.addFinanceSellProductBean(fspBean))
								return "数据库操作失败：addFinanceSellProductBean";
						}
					}
					// -------------liuruilan-------------
				}
			}

			if (orderProduct != null) {
				int totalCount = product.getStockAll() + product.getLockCountAll();
				price5 = ((float) Math.round((product.getPrice5() * (totalCount) + (orderProduct.getPrice3() * count)) / (totalCount + count) * 1000)) / 1000;
				if (price5 > 0) {
					if (!stockServ.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId()))
						return "数据库操作失败：update product set price5";
				}
			}

			if (!frfService.updateFinanceSellBean(" price =" + price, "id =" + fsId)) // 追加修改累加后的订单价---liuruilan----
				return "数据库操作失败：updateFinanceSellBean";
		} catch (Exception e) {
			e.printStackTrace();
			return "发生异常";
		}
		return null;
		*/
	}

	public Map<String,String> loadWaitBackSupplierProduct(String cargoId, int area) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService stockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		Map<String, String> tempMap = null;
		try {
			String condition = " ci.stock_type = " + ProductStockBean.STOCKTYPE_CUSTOMER + "  and asbsp.status = " + AfterSaleBackSupplierProduct.STATUS3 + " AND ci.store_type = " + CargoInfoBean.STORE_TYPE6 + " AND ci.area_id = " + area;
			if(!"".equals(cargoId)){
				condition += " and ci.id > " + cargoId;
			}
			
			List<Map<String,String>> list = stockService.getWaitBackSupplierProductList(condition);
			tempMap = new HashMap<String, String>();
			for(Map<String,String> map : list){
				if(tempMap.isEmpty()){
					tempMap.putAll(map);
				} else {
					if (tempMap.get("wholeCode") != null && tempMap.get("wholeCode").equalsIgnoreCase(map.get("wholeCode"))) {
						tempMap.put("detectCode", tempMap.get("detectCode") + "," + map.get("detectCode"));
						tempMap.put("productName", tempMap.get("productName") + "," + map.get("productName"));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return tempMap;
	}

	/**
	 * 返厂签收获取code
	 * @param imeiCode
	 * @param afterSaleCode
	 * @param productCode
	 * @return
	 */
	public Json getCodeForBackSupplier(String imeiCode, String afterSaleCode, String productCode){
		Json j = new Json();
		
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB_SLAVE);		
		try {
			if (!"".equals(productCode)) {
				WareService wareService = new WareService(db);
				voProduct product = wareService.getProduct(productCode);
				if (product == null) {
					j.setMsg("商品编号不存在");
					return j;
				}
				
				String resultCode = null;
				// 客户库：处理单状态为“保修”：返厂状态为“已返厂”或“等待厂商寄回” 可签收
				// 			处理单状态为 其他状态： 返厂状态为：“等待厂商寄回” 可签收
				//售后库：返厂状态为“已返厂”或“等待厂商寄回” 可签收
				StringBuilder sb = new StringBuilder();
				sb.append(" SELECT asdp.`code` FROM "); 
				sb.append(" after_sale_back_supplier_product AS asbsp, after_sale_detect_product AS asdp, cargo_info AS ci ");
				sb.append(" WHERE asbsp.after_sale_detect_product_id = asdp.id AND  ");
				sb.append(" asdp.cargo_whole_code = ci.whole_code AND asbsp.product_id = ").append(product.getId());
				sb.append(" AND (( asbsp.`status` = 0 ) OR ( asdp.`status` = 4 AND asbsp.`status` = 4)) or (ci.stock_type=9 and asbsp.`status` in (0,4)) ");
				sb.append("AND asdp.lock_status = 0 AND asbsp.after_sale_detect_product_id NOT IN ");
				sb.append(" (  SELECT detect_id " );
				sb.append(" 	FROM after_sale_back_supplier_product_replace AS asbspr ");
				sb.append(" 	WHERE asbspr.new_product_id = ").append(product.getId());
				sb.append(" 	AND asbspr.`audit_status` = ").append(AfterSaleBackSupplierProductReplace.AUDIT_STATUS1);// 待审核 
				sb.append(" ) ");
				sb.append(" ORDER BY ci.stock_type DESC, asdp.id ASC   LIMIT 1 ");
				
				ResultSet rs = db.executeQuery(sb.toString());
				if (rs != null) {
					if (rs.next()) {
						resultCode = rs.getString("code");	
					}
					rs.close();
				}
				
				if(resultCode == null){
					j.setMsg("没有查询到可以签收的处理单");
					return j;
				}
				afterSaleCode = resultCode;
			} else {
				String condition = "".equals(imeiCode) ? (" asdp.`code` = '" + StringUtil.toSql(afterSaleCode) + "' ") : ( " asbsp.IMEI = '" + StringUtil.toSql(imeiCode) + "' ");
				// 客户库：处理单状态为“保修”：返厂状态为“已返厂”或“等待厂商寄回” 可签收
				// 			处理单状态为 其他状态： 返厂状态为：“等待厂商寄回” 可签收
				//售后库：返厂状态为“已返厂”或“等待厂商寄回” 可签收
				StringBuilder sb = new StringBuilder();
				sb.append(" SELECT asdp.`code`, asbsp.IMEI FROM ");
				sb.append(" after_sale_back_supplier_product AS asbsp, after_sale_detect_product AS asdp, cargo_info AS ci  ");
				sb.append(" WHERE asbsp.after_sale_detect_product_id = asdp.id AND asdp.lock_status = 0 AND asdp.cargo_whole_code=ci.whole_code and ");
				sb.append(condition);				
				sb.append(" AND (( asbsp.`status` = 0 ) OR ( asdp.`status` = 4 AND asbsp.`status` = 4) or (ci.stock_type=9 and asbsp.`status` in (0,4)))");				
				sb.append(" AND asbsp.after_sale_detect_product_id NOT IN ");
				sb.append(" (  SELECT detect_id " );
				sb.append(" 	FROM after_sale_back_supplier_product_replace AS asbspr ");				
				sb.append(" 	WHERE asbspr.`audit_status` = ").append(AfterSaleBackSupplierProductReplace.AUDIT_STATUS1); // 待审核
				sb.append(" ) ");
				
				String imei = null;
				String code = null;
				ResultSet rs = db.executeQuery(sb.toString());
				if (rs != null) {
					if (rs.next()) {
						code = rs.getString("code");
						imei = rs.getString("imei");
						if (imei == null)
							imei = "";
					}
					rs.close();
				}
				
				if (imei == null || code == null) {
					j.setMsg("没有查询到可以签收的处理单");
					return j;
				}

				imeiCode = imei;
				afterSaleCode = code;
 			}
			
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("afterSaleCode", afterSaleCode);
			map.put("imeiCode", imeiCode);
			map.put("productCode", productCode);
			
			j.setSuccess(true);
			j.setMsg("操作成功");
			j.setObj(map);
			return j;
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
		finally{
			db.release();
		}
	}
}
