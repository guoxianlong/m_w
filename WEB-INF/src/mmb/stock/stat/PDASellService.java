package mmb.stock.stat;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ezmorph.bean.MorphDynaBean;
import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * PDA 快速调拨、下架返厂
 * 
 * @author mengqy
 * 
 */
public class PDASellService extends BaseServiceImpl {

	public PDASellService(DbOperation dbOp) {
		this.useConnType = IBaseService.CONN_IN_SERVICE;
		this.dbOp = dbOp;
	}

	/**
	 * 查询货位上的商品库存
	 * 
	 * @param cargoId
	 *            货位id
	 * @return
	 */
	public List<HashMap<String, Object>> getCargoProductCount(int cargoId) {
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT p.id, p.code, pb.barcode, cps.stock_count, cps.stock_lock_count  ");
		sb.append(" FROM cargo_product_stock AS cps LEFT JOIN product as p ON cps.product_id = p.id ");
		sb.append(" LEFT JOIN product_barcode AS pb ON cps.product_id = pb.product_id ");
		sb.append(" WHERE cps.cargo_id = " + cargoId);
		sb.append(" AND cps.stock_count > 0 ");

		try {
			ResultSet rs = this.dbOp.executeQuery(sb.toString());
			if (rs == null)
				return list;

			while (rs.next()) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("id", rs.getString("id"));
				map.put("code", rs.getString("code"));
				map.put("barcode", rs.getString("barcode"));
				map.put("stockCount", rs.getInt("stock_count"));
				map.put("stockLockCount", rs.getInt("stock_lock_count"));
				list.add(map);
			}

			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 快速调拨
	 * 
	 * @param outCargo
	 *            源货位
	 * @param inCargo
	 *            目的货位
	 * @param list
	 *            调拨的商品id及数量
	 * @param user
	 *            当前操作用户
	 * @return
	 */
	public String quickAllot(CargoInfoBean outCargo, CargoInfoBean inCargo, List<MorphDynaBean> list, voUser user) {

		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		WareService wareService = new WareService(this.dbOp);

		// 生成调拨单
		String code = "HWD" + DateUtil.getNow().substring(2, 10).replace("-", "");
		String storageCode = outCargo.getWholeCode().substring(0, 5);
		// 生成编号
		CargoOperationBean cargoOper = service.getCargoOperation("code like '" + code + "%' order by id desc limit 1");
		if (cargoOper == null) {
			code = code + "00001";
		} else {
			// 获取当日计划编号最大值
			String _code = cargoOper.getCode();
			int number = Integer.parseInt(_code.substring(_code.length() - 5));
			number++;
			code += String.format("%05d", new Object[] { new Integer(number) });
		}

		cargoOper = new CargoOperationBean();
		cargoOper.setCode(code);
		cargoOper.setCreateDatetime(DateUtil.getNow());
		cargoOper.setCreateUserId(user.getId());
		cargoOper.setCreateUserName(user.getUsername());
		cargoOper.setRemark("");
		cargoOper.setSource("");
		cargoOper.setStockInType(inCargo.getStoreType());
		cargoOper.setStockOutType(outCargo.getStoreType());
		cargoOper.setStorageCode(storageCode);
		cargoOper.setType(CargoOperationBean.TYPE3);
		cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS35);
		cargoOper.setLastOperateDatetime(DateUtil.getNow());
		cargoOper.setStockOutArea(outCargo.getAreaId());
		cargoOper.setStockInArea(inCargo.getAreaId());
		if (!service.addCargoOperation(cargoOper)) {
			return "数据库操作失败：addCargoOperation";
		}

		cargoOper.setId(service.getDbOp().getLastInsertId());

		for (MorphDynaBean map : list) {
			int productId = StringUtil.toInt(map.get("id").toString()); // 商品id
			int count = StringUtil.toInt(map.get("count").toString()); // 调拨数量

			voProduct vProduct = wareService.getProduct(productId);
			if (vProduct == null) {
				return "未查询到商品[" + productId + "]";
			}

			// 货位库存
			CargoProductStockBean outCps = service.getCargoProductStock(" cargo_id = " + outCargo.getId() + " and product_id = " + productId);
			if (outCps == null || outCps.getStockCount() < count) {
				return "源货位库存不足，商品编号[" + vProduct.getCode() + "]";
			}

			// 减库存、可用量
			if (!service.updateCargoProductStockCount(outCps.getId(), -count)) {
				return "数据库操作失败：updateCargoProductStockCount";
			}

			// 加库存、可用量
			CargoProductStockBean inCps = service.getCargoAndProductStock(" cargo_id = " + inCargo.getId() + " and product_id = " + productId);
			if (inCps == null) {
				inCps = new CargoProductStockBean();
				inCps.setCargoId(inCargo.getId());
				inCps.setProductId(productId);
				inCps.setStockCount(count);
				inCps.setStockLockCount(0);
				if (!service.addCargoProductStock(inCps)) {
					return "数据库操作失败：addCargoProductStock";
				}
				inCps.setId(service.getDbOp().getLastInsertId());
			} else {
				if (!service.updateCargoProductStockCount(inCps.getId(), count)) {
					return "数据库操作失败：updateCargoProductStockCount";
				}
			}

			// 货位进销存卡片
			vProduct.setPsList(psService.getProductStockList("product_id = " + vProduct.getId(), -1, -1, "id asc"));

			// 货位进销存卡片
			CargoStockCardBean outcsc = new CargoStockCardBean();
			outcsc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
			outcsc.setCode(cargoOper.getCode()); // 调拨单号
			outcsc.setCreateDatetime(DateUtil.getNow());
			outcsc.setStockType(outCargo.getStockType());
			outcsc.setStockArea(outCargo.getAreaId());
			outcsc.setProductId(vProduct.getId());
			outcsc.setStockId(outCps.getId());
			outcsc.setStockOutCount(count);
			outcsc.setStockOutPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
			outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			outcsc.setCurrentCargoStock(outCps.getStockCount() + outCps.getStockLockCount());
			outcsc.setCargoStoreType(outCargo.getStoreType());
			outcsc.setCargoWholeCode(outCargo.getWholeCode());
			outcsc.setStockPrice(vProduct.getPrice5());
			outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
			if (!service.addCargoStockCard(outcsc)) {
				return "数据库操作失败：addCargoStockCard";
			}

			// 货位进销存卡片
			CargoStockCardBean incsc = new CargoStockCardBean();
			incsc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
			incsc.setCode(cargoOper.getCode()); // 调拨单号
			incsc.setCreateDatetime(DateUtil.getNow());
			incsc.setStockType(inCargo.getStockType());
			incsc.setStockArea(inCargo.getAreaId());
			incsc.setProductId(vProduct.getId());
			incsc.setStockId(inCps.getId());
			incsc.setStockInCount(count);
			incsc.setStockInPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
			incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			incsc.setCurrentCargoStock(inCps.getStockCount() + inCps.getStockLockCount());
			incsc.setCargoStoreType(inCargo.getStoreType());
			incsc.setCargoWholeCode(inCargo.getWholeCode());
			incsc.setStockPrice(vProduct.getPrice5());
			incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
			if (!service.addCargoStockCard(incsc)) {
				return "数据库操作失败：addCargoStockCard";
			}

			CargoOperationCargoBean outCoc = new CargoOperationCargoBean();
			outCoc.setOperId(cargoOper.getId());
			outCoc.setInCargoProductStockId(0);
			outCoc.setProductId(vProduct.getId());
			outCoc.setType(1);
			outCoc.setUseStatus(0);
			outCoc.setOutCargoProductStockId(outCps.getId());
			outCoc.setOutCargoWholeCode(outCargo.getWholeCode());
			outCoc.setStockCount(count);
			if (!service.addCargoOperationCargo(outCoc)) {
				return "数据库操作失败 : addCargoOperationCargo";
			}

			CargoOperationLogBean logBean = new CargoOperationLogBean();
			logBean.setOperId(cargoOper.getId());
			logBean.setOperDatetime(DateUtil.getNow());
			logBean.setOperAdminId(user.getId());
			logBean.setOperAdminName(user.getUsername());
			StringBuilder logRemark = new StringBuilder("制单：");

			logRemark.append("商品");
			logRemark.append(vProduct.getCode());
			logRemark.append("，");
			logRemark.append("源货位（");
			logRemark.append(outCargo.getWholeCode());
			logRemark.append("）");

			CargoOperationCargoBean inCoc = new CargoOperationCargoBean();
			inCoc.setOperId(cargoOper.getId());
			inCoc.setProductId(vProduct.getId());
			inCoc.setOutCargoProductStockId(outCps.getId());
			inCoc.setOutCargoWholeCode(outCargo.getWholeCode());
			inCoc.setStockCount(count);
			inCoc.setType(0);
			inCoc.setUseStatus(1);
			inCoc.setInCargoProductStockId(inCps.getId());
			inCoc.setInCargoWholeCode(inCargo.getWholeCode());

			if (!service.addCargoOperationCargo(inCoc)) {
				return "数据库操作失败 : addCargoOperationCargo";
			}// 添加目的货位记录

			logRemark.append("，");
			logRemark.append("目的货位（");
			logRemark.append(inCargo.getWholeCode());
			logRemark.append("）");
			logBean.setRemark(logRemark.toString());

			if (!service.addCargoOperationLog(logBean)) {
				return "数据库操作失败 : addCargoOperationLog";
			}
		}

		return null;
	}

	/**
	 * 下架返厂，生成从合格库到返厂库的调拨单
	 * 
	 * @param outCargo
	 *            源货位
	 * @param outPs
	 *            合格库商品库存
	 * @param outCps
	 *            源货位商品库存
	 * @param inCargo
	 *            目的货位 返厂库 缓存区
	 * @param inPs
	 *            返厂库商品库存
	 * @param inCps
	 *            目的货位 商品库存
	 * @param vProduct
	 *            调拨的商品
	 * @param count
	 *            调拨数量
	 * @param user
	 *            当前操作用户
	 * @return
	 */

	public String backSupplier(CargoInfoBean outCargo, ProductStockBean outPs, CargoProductStockBean outCps, CargoInfoBean inCargo, ProductStockBean inPs, CargoProductStockBean inCps, voProduct vProduct, int count, voUser user) {

		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);

		// 添加调拨单
		StockExchangeBean bean = new StockExchangeBean();
		bean.setCreateDatetime(DateUtil.getNow());
		bean.setName(DateUtil.getNowDateStr() + "下架返厂调拨");
		bean.setRemark("");
		bean.setStatus(StockExchangeBean.STATUS3);
		bean.setConfirmDatetime(DateUtil.getNow());
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String brefCode = "DB" + sdf.format(cal.getTime());
		bean.setCode(brefCode);
		bean.setCreateUserId(user.getId());
		bean.setCreateUserName(user.getUsername());
		bean.setStockOutOperName("");
		bean.setAuditingUserName("");
		bean.setStockInOperName("");
		bean.setAuditingUserName2("");
		bean.setStockInArea(inCargo.getAreaId());
		bean.setStockOutArea(outCargo.getAreaId());
		bean.setStockInType(inCargo.getStockType());
		bean.setStockOutType(outCargo.getStockType());
		bean.setPriorStatus(StockExchangeBean.PRIOR_STATUS0);
		if (!psService.addStockExchange(bean)) {
			return "数据库操作失败:addStockExchange";
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
		updateBuf.append("update stock_exchange set code='" + totalCode + "' where id=").append(bean.getId());
		if (!psService.getDbOp().executeUpdate(updateBuf.toString())) {
			return "数据库操作失败:添加调拨单失败！";
		}
		bean.setCode(totalCode);

		StockExchangeProductBean sep = new StockExchangeProductBean();
		sep.setCreateDatetime(DateUtil.getNow());
		sep.setConfirmDatetime(null);
		sep.setStockExchangeId(bean.getId());
		sep.setProductId(vProduct.getId());
		sep.setRemark("");
		sep.setStatus(StockExchangeProductBean.STOCKIN_DEALED);
		sep.setStockOutCount(count);
		sep.setStockInCount(count);
		sep.setStockOutId(outPs.getId());
		sep.setStockInId(inPs.getId());
		sep.setReason(1);
		sep.setReasonText("");
		// 添加调拨单商品
		if (!psService.addStockExchangeProduct(sep)) {
			return "数据库操作失败:调拨单商品添加失败！";
		}
		sep.setId(psService.getDbOp().getLastInsertId());

		StockExchangeProductCargoBean sepcOut = new StockExchangeProductCargoBean();
		sepcOut.setStockExchangeProductId(sep.getId());
		sepcOut.setStockExchangeId(bean.getId());
		sepcOut.setStockCount(count);
		sepcOut.setCargoProductStockId(outCps.getId());
		sepcOut.setCargoInfoId(outCargo.getId());
		sepcOut.setType(0);
		if (!psService.addStockExchangeProductCargo(sepcOut)) {
			return "数据库操作失败:addStockExchangeProductCargo";
		}
		StockExchangeProductCargoBean sepcIn = new StockExchangeProductCargoBean();
		sepcIn.setStockExchangeProductId(sep.getId());
		sepcIn.setStockExchangeId(bean.getId());
		sepcIn.setStockCount(count);
		sepcIn.setCargoProductStockId(inCps.getId());
		sepcIn.setCargoInfoId(inCargo.getId());
		sepcIn.setType(1);
		if (!psService.addStockExchangeProductCargo(sepcIn)) {
			return "数据库操作失败:addStockExchangeProductCargo";
		}

		// 调拨单改为已确认
		String set = "remark = '操作前库存" + outPs.getStock() + ",操作后库存" + (outPs.getStock() - sep.getStockOutCount()) + "', confirm_datetime = now()";
		if (!psService.updateStockExchangeProduct(set, "id = " + sep.getId())) {
			return "数据库操作失败:确认调拨单失败";
		}

		// 锁库存
		// 商品库存
		if (!psService.updateProductStockCount(outPs.getId(), -count)) {
			return "数据库操作失败:锁库存失败";
		}
		if (!psService.updateProductLockCount(outPs.getId(), count)) {
			return "数据库操作失败:锁库存失败";
		}
		// 货位库存
		if (!cargoService.updateCargoProductStockCount(outCps.getId(), -count)) {
			return "数据库操作失败:锁库存失败";
		}
		if (!cargoService.updateCargoProductStockLockCount(outCps.getId(), count)) {
			return "数据库操作失败:锁库存失败";
		}

		return "?" + bean.getCode();
	}
}
