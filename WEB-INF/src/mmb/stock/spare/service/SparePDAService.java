package mmb.stock.spare.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import mmb.common.dao.ProductDao;
import mmb.rec.oper.dao.ProductStockDao;
import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.JsonModelUtil;
import mmb.stock.spare.dao.AfterSaleReplaceNewProductRecordDao;
import mmb.stock.spare.dao.SpareBeanDao;
import mmb.stock.spare.dao.SpareStockinBeanDao;
import mmb.stock.spare.dao.SpareStockinProductUpshelfBeanDao;
import mmb.stock.spare.model.AfterSaleReplaceNewProductRecord;
import mmb.stock.spare.model.SpareBean;
import mmb.stock.spare.model.SpareStockinBean;
import mmb.stock.spare.model.SpareStockinProductUpshelfBean;
import mmb.ware.cargo.dao.CargoInfoDao;
import mmb.ware.cargo.dao.CargoOperationCargoDao;
import mmb.ware.cargo.dao.CargoOperationDao;
import mmb.ware.cargo.dao.CargoProductStockDao;
import mmb.ware.cargo.dao.CargoStockCardDao;
import mmb.ware.cargo.model.CargoInfo;
import mmb.ware.cargo.model.CargoOperation;
import mmb.ware.cargo.model.CargoOperationCargo;
import mmb.ware.cargo.model.CargoProductStock;
import mmb.ware.cargo.model.CargoStockCard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Service
public class SparePDAService {

	@Autowired
	private SpareService spareService;

	@Autowired
	private SpareBeanDao spareBeanDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ProductStockDao productStockDao;

	@Autowired
	private CargoInfoDao cargoDao;

	@Autowired
	private CargoProductStockDao cargoProductStockDao;

	@Autowired
	private SpareStockinBeanDao stockInDao;

	@Autowired
	private CargoOperationDao cargoOperationDao;

	@Autowired
	private CargoOperationCargoDao cargoOperationCargoDao;

	@Autowired
	private CargoStockCardDao cargoStockCardDao;

	@Autowired
	private SpareStockinProductUpshelfBeanDao sspuDao;

	@Autowired
	private AfterSaleReplaceNewProductRecordDao asrnprDao;
 
	/**
	 * 获取备用机信息
	 * @param code
	 * @return
	 */
	public JsonModel getSpareProductInfo(String code) {
		SpareBean spareBean = this.spareBeanDao.selectByCondition(" code ='" + code + "' ");
		if (spareBean == null) {
			return JsonModelUtil.error("备用机号不存在");
		}

		if (spareBean.getStatus() != 1) {
			return JsonModelUtil.error("备用机商品状态不正确");
		}

		SpareStockinBean stockInBean = this.stockInDao.selectByPrimaryKey(Integer.valueOf(spareBean.getSpareStockinId()));
		if (stockInBean == null) {
			return JsonModelUtil.error("备用机号入库单不存在");
		}

		// 0--待审核；1--已完成；2--审核不通过
		if (stockInBean.getStatus() != 1) {
			return JsonModelUtil.error("备用机号入库单未完成，不可上架");
		}

		voProduct producct = this.productDao.getProduct(" id = " + spareBean.getProductId());

		if (producct == null)
			return JsonModelUtil.error("商品信息不存在");

		JsonModel result = JsonModelUtil.success("code", producct.getCode());
		result.getData().put("name", producct.getOriname());
		return result;
	}

	/**
	 * 备用机上架
	 * @param spareCode
	 * @param cargoCode
	 * @param user
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void spareProductUpshelf(String spareCode, String cargoCode, voUser user) {

		SpareBean spareBean = this.spareBeanDao.selectByCondition(" code ='" + spareCode + "'");
		if (spareBean == null) {
			throw new RuntimeException("备用机号不存在");
		}
		if (spareBean.getStatus() != 1) {
			throw new RuntimeException("备用机商品状态不正确");
		}
		SpareStockinBean stockInBean = this.stockInDao.selectByPrimaryKey(Integer.valueOf(spareBean.getSpareStockinId()));
		if (stockInBean == null) {
			throw new RuntimeException("备用机号入库单不存在");
		}

		// 0--待审核；1--已完成；2--审核不通过
		if (stockInBean.getStatus() != 1) {
			throw new RuntimeException("备用机号入库单未完成，不可上架");
		}

		CargoInfo outCargo = this.cargoDao.selectByCondition(" whole_code = '" + spareBean.getCargoWholeCode() + "' ");
		if (outCargo == null) {
			throw new RuntimeException("备用机商品不存在");
		}

		if (!outCargo.getStoreType().equals(CargoInfoBean.STORE_TYPE2)) {
			throw new RuntimeException("此备用机单号不是上架状态");
		}

		CargoInfo inCargo = this.cargoDao.selectByCondition(" whole_code = '" + cargoCode + "' AND status IN (0, 1) ");
		if (inCargo == null) {
			throw new RuntimeException("目的货位货位不存在");
		}

		if (inCargo.getAreaId() != outCargo.getAreaId()) {
			throw new RuntimeException("目的货位 地区不正确");
		}

		if (inCargo.getStockType() != outCargo.getStockType()) {
			throw new RuntimeException("目的货位 库类型不正确");
		}

		if (inCargo.getStatus() == CargoInfoBean.STATUS1) {
			if (cargoDao.updateByCondition(" status = " + CargoInfoBean.STATUS0, " id = " + inCargo.getId()) == 0) {
				throw new RuntimeException("数据库操作失败:updateCargoInfo");
			}
		}

		CargoOperation cargoOper = new CargoOperation();
		cargoOper.setStockInType(inCargo.getStoreType());
		cargoOper.setStockOutType(outCargo.getStoreType());

		String cargoOperCode = "HWS" + DateUtil.getNow().substring(2, 10).replace("-", "");
		CargoOperation oldCargoOper = cargoOperationDao.selectByCondition("code like '" + cargoOperCode + "%' order by id desc ");
		if (oldCargoOper == null) {
			cargoOperCode = cargoOperCode + "00001";
		} else {// 获取当日计划编号最大值
			String _code = oldCargoOper.getCode();
			int number = Integer.parseInt(_code.substring(_code.length() - 5));
			number++;
			cargoOperCode += String.format("%05d", new Object[] { new Integer(number) });
		}

		int area = stockInBean.getAreaId();
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
		cargoOper.setRemark("备用机商品上架");
		cargoOper.setCreateUserId(user.getId());
		cargoOper.setAuditingDatetime(DateUtil.getNow());
		cargoOper.setAuditingUserId(user.getId());
		cargoOper.setCode(cargoOperCode);
		cargoOper.setSource(spareCode);
		cargoOper.setStorageCode(storageCode);
		cargoOper.setCreateUserName(user.getUsername());
		cargoOper.setAuditingUserName(user.getUsername());
		cargoOper.setType(CargoOperationBean.TYPE0);
		cargoOper.setLastOperateDatetime(DateUtil.getNow());
		cargoOper.setConfirmDatetime(DateUtil.getNow());
		cargoOper.setConfirmUserName(user.getUsername());
		cargoOper.setCompleteUserId(user.getId());
		cargoOper.setCompleteUserName(user.getUsername());
		cargoOper.setCompleteDatetime(DateUtil.getNow());
		cargoOper.setPrintCount(0);
		cargoOper.setStockInArea(inCargo.getAreaId());
		cargoOper.setStockOutArea(outCargo.getAreaId());
		cargoOper.setStockInType(inCargo.getStoreType());
		cargoOper.setStockOutType(outCargo.getStoreType());
		cargoOper.setEffectStatus(0);
		
		if (cargoOperationDao.insert(cargoOper) <= 0) {
			throw new RuntimeException("数据操作失败");
		}

		// 备用机 和上架单的对应关系
		SpareStockinProductUpshelfBean sspuBean = new SpareStockinProductUpshelfBean();
		sspuBean.setOperId(cargoOper.getId());
		sspuBean.setSpareStockinProductId(spareBean.getId());
		sspuBean.setOperStatus((byte) 1);
		sspuBean.setType((byte) 1);

		if (this.sspuDao.insert(sspuBean) <= 0) {
			throw new RuntimeException("数据操作失败");
		}

		// 修改货位库存
		CargoProductStock outCargoProductStock = this.cargoProductStockDao.selectByCondition("cargo_id=" + outCargo.getId() + " AND product_id=" + spareBean.getProductId());
		if (outCargoProductStock == null || outCargoProductStock.getStockCount() <= 0) {
			throw new RuntimeException("源货位库存不足");
		}

		if (!this.cargoProductStockDao.updateCargoProductStockCount(outCargoProductStock.getId(), -1)) {
			throw new RuntimeException("数据库操作失败：updateCargoProductStockLockCount");
		}

		CargoProductStock inCargoProductStock = this.cargoProductStockDao.selectByCondition("cargo_id=" + inCargo.getId() + " AND product_id=" + spareBean.getProductId());
		if (inCargoProductStock == null) {
			inCargoProductStock = new CargoProductStock();
			inCargoProductStock.setCargoId(inCargo.getId());
			inCargoProductStock.setProductId(spareBean.getProductId());
			inCargoProductStock.setStockCount(1);
			inCargoProductStock.setStockLockCount(0);
			if (cargoProductStockDao.insert(inCargoProductStock) <= 0) {
				throw new RuntimeException("数据库操作失败：addCargoProductStock");
			}
			// 为货位进销存卡片数值计算做准备
			inCargoProductStock.setStockCount(0);
		} else {
			if (!cargoProductStockDao.updateCargoProductStockCount(inCargoProductStock.getId(), 1)) {
				throw new RuntimeException("数据库操作失败：addCargoProductStock");
			}
		}

		// 上架单商品
		CargoOperationCargo cocBean = new CargoOperationCargo();
		cocBean.setOperId(cargoOper.getId());
		cocBean.setProductId(spareBean.getProductId());
		cocBean.setInCargoProductStockId(inCargoProductStock.getId());
		cocBean.setInCargoWholeCode(inCargo.getWholeCode());
		cocBean.setOutCargoProductStockId(outCargoProductStock.getId());
		cocBean.setOutCargoWholeCode(outCargo.getWholeCode());
		cocBean.setStockCount(1);
		cocBean.setType(1);
		cocBean.setUseStatus(0);
		if (this.cargoOperationCargoDao.insert(cocBean) <= 0) {
			throw new RuntimeException("数据库操作失败：addCargoOperationCargo");
		}

		cocBean.setId(0);
		cocBean.setType(0);
		cocBean.setUseStatus(1);
		if (this.cargoOperationCargoDao.insert(cocBean) <= 0) {
			throw new RuntimeException("数据库操作失败：addCargoOperationCargo");
		}

		// 修改备用机商品所关联的货位
		if (this.spareBeanDao.updateByCondition(" cargo_whole_code = '" + cargoCode + "' ", " id = " + spareBean.getId()) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}

		// 添加 货位进销存卡片
		voProduct vProduct = this.productDao.getProduct(" id = " + spareBean.getProductId());
		Map<String, String> psMap = new HashMap<String, String>();
		psMap.put("condition", " product_id=" + spareBean.getProductId() + " ");
		psMap.put("index", "-1");
		psMap.put("count", "-1");
		psMap.put("order", null);

		vProduct.setPsList(this.productStockDao.getProductStockList(psMap));

		CargoStockCard outcsc = new CargoStockCard();
		outcsc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELF_FOR_SPARE);
		outcsc.setCode(cargoOper.getCode());
		outcsc.setCreateDatetime(DateUtil.getNow());
		outcsc.setStockType(ProductStockBean.STOCKTYPE_SPARE);
		outcsc.setStockArea(area);
		outcsc.setProductId(spareBean.getProductId());
		outcsc.setStockId(outCargoProductStock.getId());
		outcsc.setStockInCount(0);
		outcsc.setStockInPriceSum(0.0);
		outcsc.setStockOutCount(1);
		outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
		outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount() + outCargoProductStock.getStockLockCount());
		outcsc.setCargoStoreType(outCargo.getStoreType());
		outcsc.setCargoWholeCode(outCargo.getWholeCode());
		outcsc.setStockPrice(vProduct.getPrice5());
		outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
		if (cargoStockCardDao.insert(outcsc) <= 0) {
			throw new RuntimeException("数据库操作失败：addCargoStockCard");
		}
		// 货位入库卡片
		CargoStockCard incsc = new CargoStockCard();
		incsc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELF_FOR_SPARE);
		incsc.setCode(cargoOper.getCode());
		incsc.setCreateDatetime(DateUtil.getNow());
		incsc.setStockType(ProductStockBean.STOCKTYPE_SPARE);
		incsc.setStockArea(area);
		incsc.setProductId(spareBean.getProductId());
		incsc.setStockId(inCargoProductStock.getId());
		incsc.setStockOutCount(0);
		incsc.setStockOutPriceSum(0.0);
		incsc.setStockInCount(1);
		incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
		incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		incsc.setCurrentCargoStock(inCargoProductStock.getStockCount() + inCargoProductStock.getStockLockCount());
		incsc.setCargoStoreType(inCargo.getStoreType());
		incsc.setCargoWholeCode(inCargo.getWholeCode());
		incsc.setStockPrice(vProduct.getPrice5());
		incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
		if (cargoStockCardDao.insert(incsc) <= 0) {
			throw new RuntimeException("数据库操作失败：addCargoStockCard");
		}
	}

	/**
	 * 备用机换新机
	 * 
	 * @param afCode
	 *            售后处理单号
	 * @param spareCode
	 *            备用机号
	 * @param user
	 *            当前操作用户
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void replaceReserve(String afCode, String spareCode, voUser user) {

		AfterSaleReplaceNewProductRecord asrnprBean = asrnprDao.selectByCondition(" after_sale_detect_product_code = '" + afCode + "' AND status = 2 ");
		if (asrnprBean == null) {
			throw new RuntimeException("该售后处理单不需要换新机.");
		}

		this.spareService.replaceNewCode(asrnprBean.getId(), spareCode, user);

	}

}
