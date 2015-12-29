package mmb.stock.fitting.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import mmb.common.dao.ProductDao;
import mmb.rec.oper.bean.StockBatchBean;
import mmb.rec.oper.bean.StockBatchLogBean;
import mmb.rec.oper.bean.StockCardBean;
import mmb.rec.oper.dao.ProductStockDao;
import mmb.stock.fitting.dao.AfterSaleReceiveFittingDao;
import mmb.stock.fitting.dao.CargoInfoBeanDao;
import mmb.stock.fitting.dao.CargoProductStockBeanDao;
import mmb.stock.fitting.dao.FittingStockCardListDao;
import mmb.stock.fitting.model.AfterSaleReceiveFitting;
import mmb.stock.fitting.model.CargoInfoBean;
import mmb.stock.fitting.model.CargoOperationBean;
import mmb.stock.fitting.model.CargoOperationCargoBean;
import mmb.stock.fitting.model.CargoProductStockBean;
import mmb.stock.fitting.model.CargoStockCardBean;
import mmb.stock.fitting.model.FittingOutBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

/**
 * 配件 pda service
 */
@Service
public class FittingPDAService {

	@Autowired
	private CargoInfoBeanDao cargoInfoBeanDao;

	@Autowired
	private ProductDao productDao;

	@Autowired
	private ProductStockDao productStockDao;

	@Autowired
	private CargoProductStockBeanDao cargoProductStockBeanDao;

	@Autowired
	private FittingStockCardListDao stockCardDao;

	@Autowired
	private AfterSaleReceiveFittingDao receiveFittingDao;

	/**
	 * 更换货位
	 * 
	 * @param outWholeCode
	 *            原货位
	 * @param inWholeCode
	 *            目的货位
	 * @param code
	 *            配件编号
	 * @param count
	 *            数量
	 * @param type
	 *            0客户配件库 1售后配件库
	 * @param user
	 *            当前用户
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void changeCargo(String outWholeCode, String inWholeCode, String code, int count, int type, voUser user) {
		voProduct vProduct = this.productDao.getProduct(" code = '" + code + "' ");
		if (vProduct == null) {
			throw new RuntimeException("配件不存在");
		}

		if (vProduct.getParentId1() != 1536) {
			throw new RuntimeException("该商品不是配件");
		}

		CargoInfoBean cargoInfoOut = cargoInfoBeanDao.selectByCondition(" whole_code = '" + outWholeCode + "' ");
		if (cargoInfoOut == null) {
			throw new RuntimeException("原货位不存在");
		}

		CargoInfoBean cargoInfoIn = cargoInfoBeanDao.selectByCondition(" whole_code = '" + inWholeCode + "' AND status IN (0, 1) ");
		if (cargoInfoIn == null) {
			throw new RuntimeException("目的货位不存在");
		}

		if (cargoInfoIn.getStatus() == CargoInfoBean.STATUS1) {
			if (!this.cargoInfoBeanDao.updateCargoInfoBean(" status = " + CargoInfoBean.STATUS0, " id = " + cargoInfoIn.getId())) {
				throw new RuntimeException("操作数据库失败");
			}
		}

		if (cargoInfoIn.getStockType() != cargoInfoOut.getStockType()) {
			throw new RuntimeException("原货位和目的货位库类型不一致");
		}
		if (cargoInfoIn.getAreaId() != cargoInfoOut.getAreaId()) {
			throw new RuntimeException("原货位和目的货位库地区不一致");
		}
		if (cargoInfoIn.getType() != cargoInfoOut.getType()) {
			throw new RuntimeException("原货位和目的货位类型不一致");
		}
		if (type == 0) {
			if (cargoInfoOut.getStockType() != CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING) {
				throw new RuntimeException("客户配件更换功能不可以操作非配件客户库货位");
			}
		} else {
			if (cargoInfoOut.getStockType() != CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING) {
				throw new RuntimeException("售后配件更换功能不可以操作非配件售后库货位");
			}
		}
		
		CargoOperationBean cargoOper = new CargoOperationBean();
		cargoOper.setStockInType(cargoInfoIn.getStoreType());
		cargoOper.setStockOutType(cargoInfoOut.getStoreType());
		String cargoOperCode = "HWS" + DateUtil.getNow().substring(2, 10).replace("-", "");
		CargoOperationBean oldCargoOper = cargoInfoBeanDao.selectCargoOperationByCondition(" code like '" + cargoOperCode + "%' order by id desc ");
		if (oldCargoOper == null) {
			cargoOperCode = cargoOperCode + "00001";
		} else {// 获取当日计划编号最大值
			String _code = oldCargoOper.getCode();
			int number = Integer.parseInt(_code.substring(_code.length() - 5));
			number++;
			cargoOperCode += String.format("%05d", new Object[] { new Integer(number) });
		}
		String storageCode = "";
		int area = cargoInfoIn.getAreaId();
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
		cargoOper.setRemark(type == 0 ? "客户配件货位更换" : "售后配件货位更换");
		cargoOper.setCreateUserId(user.getId());
		cargoOper.setAuditingDatetime(DateUtil.getNow());
		cargoOper.setAuditingUserId(user.getId());
		cargoOper.setCode(cargoOperCode);
		cargoOper.setSource("");
		cargoOper.setStorageCode(storageCode);
		cargoOper.setCreateUserName(user.getUsername());
		cargoOper.setAuditingUserName(user.getUsername());
		cargoOper.setType(0);
		cargoOper.setLastOperateDatetime(DateUtil.getNow());
		cargoOper.setConfirmDatetime(DateUtil.getNow());
		cargoOper.setConfirmUserName(user.getUsername());
		cargoOper.setCompleteDatetime(DateUtil.getNow());
		cargoOper.setCompleteUserId(user.getId());
		cargoOper.setCompleteUserName(user.getUsername());

		if (this.cargoInfoBeanDao.insertCargoOperationBean(cargoOper) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}

		// 修改货位库存
		CargoProductStockBean outCargoProductStock = this.cargoProductStockBeanDao.selectByCondition("cargo_id=" + cargoInfoOut.getId() + " AND product_id=" + vProduct.getId());
		if (outCargoProductStock == null || outCargoProductStock.getStockCount() < count) {
			throw new RuntimeException("源货位库存不足");
		}
		if (!this.cargoProductStockBeanDao.updateStockCount(outCargoProductStock.getId(), -count)) {
			throw new RuntimeException("数据库操作失败");
		}

		CargoProductStockBean inCargoProductStock = this.cargoProductStockBeanDao.selectByCondition("cargo_id=" + cargoInfoIn.getId() + " AND product_id=" + vProduct.getId());
		if (inCargoProductStock == null) {
			inCargoProductStock = new CargoProductStockBean();
			inCargoProductStock.setCargoId(cargoInfoIn.getId());
			inCargoProductStock.setProductId(vProduct.getId());
			inCargoProductStock.setStockCount(count);
			inCargoProductStock.setStockLockCount(0);
			if (this.cargoProductStockBeanDao.insert(inCargoProductStock) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
		} else {
			if (!this.cargoProductStockBeanDao.updateStockCount(inCargoProductStock.getId(), count)) {
				throw new RuntimeException("数据库操作失败");
			}
		}

		// 上架单商品
		CargoOperationCargoBean bean = new CargoOperationCargoBean();
		bean.setOperId(cargoOper.getId());
		bean.setProductId(vProduct.getId());
		bean.setInCargoProductStockId(inCargoProductStock.getId());
		bean.setInCargoWholeCode(cargoInfoIn.getWholeCode());
		bean.setOutCargoProductStockId(outCargoProductStock.getId());
		bean.setOutCargoWholeCode(cargoInfoOut.getWholeCode());
		bean.setStockCount(count);
		bean.setType(1);
		bean.setUseStatus(0);
		if (this.cargoInfoBeanDao.insertCargoOperationCargoBean(bean) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}

		bean.setId(0);
		bean.setType(0);
		bean.setUseStatus(1);
		if (this.cargoInfoBeanDao.insertCargoOperationCargoBean(bean) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("condition", "product_id=" + vProduct.getId());
		map.put("index", "-1");
		map.put("count", "-1");
		map.put("orderBy", null);

		vProduct.setPsList(this.productStockDao.getProductStockList(map));

		CargoStockCardBean outcsc = new CargoStockCardBean();
		outcsc.setCardType(type == 0 ? CargoStockCardBean.CARDTYPE_AFTERSALE_FITTING_CHANGE_CARGO : CargoStockCardBean.CARDTYPE_CUSTOM_FITTING_CHANGE_CARGO);
		outcsc.setCode(cargoOper.getCode());
		outcsc.setCreateDatetime(DateUtil.getNow());
		outcsc.setStockType(cargoInfoOut.getStockType());
		outcsc.setStockArea(cargoInfoOut.getAreaId());
		outcsc.setProductId(vProduct.getId());
		outcsc.setStockId(outCargoProductStock.getId());
		outcsc.setStockOutCount(count);
		outcsc.setStockOutPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
		outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount() + outCargoProductStock.getStockLockCount());
		outcsc.setCargoStoreType(cargoInfoOut.getStoreType());
		outcsc.setCargoWholeCode(cargoInfoOut.getWholeCode());
		outcsc.setStockPrice(vProduct.getPrice5());
		outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
		if (this.cargoProductStockBeanDao.insertCargoStockCardBean(outcsc) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}

		// 货位入库卡片
		CargoStockCardBean incsc = new CargoStockCardBean();
		incsc.setCardType(type == 0 ? CargoStockCardBean.CARDTYPE_AFTERSALE_FITTING_CHANGE_CARGO : CargoStockCardBean.CARDTYPE_CUSTOM_FITTING_CHANGE_CARGO);
		incsc.setCode(cargoOper.getCode());
		incsc.setCreateDatetime(DateUtil.getNow());
		incsc.setStockType(cargoInfoIn.getStockType());
		incsc.setStockArea(cargoInfoIn.getAreaId());
		incsc.setProductId(vProduct.getId());
		incsc.setStockId(inCargoProductStock.getId());
		incsc.setStockInCount(count);
		incsc.setStockInPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
		incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
		incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
		incsc.setCurrentCargoStock(inCargoProductStock.getStockCount() + inCargoProductStock.getStockLockCount());
		incsc.setCargoStoreType(cargoInfoIn.getStoreType());
		incsc.setCargoWholeCode(cargoInfoIn.getWholeCode());
		incsc.setStockPrice(vProduct.getPrice5());
		incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
		if (this.cargoProductStockBeanDao.insertCargoStockCardBean(incsc) <= 0) {
			throw new RuntimeException("数据库操作失败");
		}

	}

	/**
	 * PDA配件出库领单
	 * 
	 * @param code
	 * @return
	 */
	public List<FittingOutBean> getFittingOutList(String code) {
		return this.stockCardDao.getFittingOutList(code);
	}

	/**
	 * 配件出库
	 * 
	 * @param code
	 * @param mapList
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void fittingOut(String code, List<HashMap<String, String>> mapList, List<FittingOutBean> beanList, voUser user) {
		HashMap<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("condition", " code = '" + code + "' ");

		AfterSaleReceiveFitting asrfBean = this.receiveFittingDao.getAfterSaleReceiveFitting(conditionMap);
		if (asrfBean == null) {
			throw new RuntimeException("没有查询到领用单");
		}

		if (asrfBean.getStatus() == AfterSaleReceiveFitting.STATUS4) {
			throw new RuntimeException("领用单已完成");
		}
		
		afterSaleGoodOut(mapList, beanList, user, asrfBean);

		StringBuffer sbSet = new StringBuffer();
		sbSet.append(" status = ").append(AfterSaleReceiveFitting.STATUS4);
		sbSet.append(" , complete_user_id = ").append(user.getId());
		sbSet.append(" , complete_user_name = '").append(user.getUsername()).append("' ");
		sbSet.append(" , complete_datetime = '").append(DateUtil.getNow()).append("' ");
				
		// 修改领用单状态 出库完成
		if(!this.receiveFittingDao.updateAfterSaleReceiveFitting(sbSet.toString(), " id = " + asrfBean.getId())){
			throw new RuntimeException("数据库操作失败");
		}

		// 更换、补齐客户库商品
		if (!(asrfBean.getTarget() == AfterSaleReceiveFitting.TARGET3 || asrfBean.getTarget() == AfterSaleReceiveFitting.TARGET4)) {
			return;
		}

		String setAsdpf = "";
		if (asrfBean.getTarget() == AfterSaleReceiveFitting.TARGET3) {
			// 更换商品
			setAsdpf = " asdpf.intact_count = asdpf.intact_count + asrfd.count, asdpf.damage_count = asdpf.damage_count - asrfd.count ";
		} else {
			// 补齐商品
			setAsdpf = " asdpf.intact_count = asdpf.intact_count + asrfd.count ";
		}
		
		// 修改处理单所关联的配件数量
		if (!this.stockCardDao.updateAfterSaleDetectProductFittingByAsrfId(setAsdpf, asrfBean.getId())) {
			throw new RuntimeException("数据库操作失败");
		}
		
		if(asrfBean.getTarget() != AfterSaleReceiveFitting.TARGET3){
			return;
		}

		// 配件客户库 完好品入库
		customerGoodIn(beanList, asrfBean);
		
		// 售后配件库 残次品入库
		afterSaleBadIn(beanList, asrfBean);
		
		// 客户配件库  残次品 出库
		customerBadOut(beanList, asrfBean);
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void afterSaleGoodOut(List<HashMap<String, String>> mapList, List<FittingOutBean> beanList, voUser user, AfterSaleReceiveFitting asrfBean) {
		// 减货位库存
		// 货位进销存卡片
		for (HashMap<String, String> map : mapList) {
			CargoInfoBean ciOut = this.cargoInfoBeanDao.selectByCondition(" whole_code = '" + map.get("wholeCode") + "' ");
			if (ciOut == null) {
				throw new RuntimeException(map.get("wholeCode") + "货位不存在");
			}
			if(ciOut.getType() != CargoInfoBean.TYPE3){
				throw new RuntimeException(map.get("wholeCode") + "货位为非完好配件货位");
			}
			if(ciOut.getStockType() != CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING){
				throw new RuntimeException(map.get("wholeCode") + "货位为非售后配件货位");
			}
			voProduct vProduct = this.productDao.getProduct(" code = '" + map.get("code") + "' AND parent_id1 = 1536 ");
			if (vProduct == null) {
				throw new RuntimeException(map.get("code") + "配件不存在");
			}
			if(ciOut.getAreaId() != asrfBean.getAreaId()){
				throw new RuntimeException("货位地区与领用单地区不一致");
			}
			
			CargoProductStockBean cpsOut = this.cargoProductStockBeanDao.selectByCondition(" cargo_id = " + ciOut.getId() + " AND product_id = " + vProduct.getId());
			int count = Integer.valueOf(map.get("count")).intValue();
			if (cpsOut == null || cpsOut.getStockCount() < count) {
				throw new RuntimeException("货位" + map.get("wholeCode") + ",配件" + map.get("code") + "库存不足");
			}

			if (!this.cargoProductStockBeanDao.updateStockCount(cpsOut.getId(), -count)) {
				throw new RuntimeException("货位" + map.get("wholeCode") + ",配件" + map.get("code") + "库存不足");
			}

			HashMap<String, String> psMap = new HashMap<String, String>();
			psMap.put("condition", "product_id=" + vProduct.getId());
			psMap.put("index", "-1");
			psMap.put("count", "-1");
			psMap.put("orderBy", null);
			vProduct.setPsList(this.productStockDao.getProductStockList(psMap));

			CargoStockCardBean cscOut = new CargoStockCardBean();
			cscOut.setCardType(CargoStockCardBean.CARDTYPE_RECEIVE_AFTERSALE_OUT_FITTING);
			cscOut.setCode(asrfBean.getCode());
			cscOut.setCreateDatetime(DateUtil.getNow());
			cscOut.setStockType(ciOut.getStockType());
			cscOut.setStockArea(ciOut.getAreaId());
			cscOut.setProductId(vProduct.getId());
			cscOut.setStockId(cpsOut.getId());
			cscOut.setStockOutCount(count);
			cscOut.setStockOutPriceSum((new BigDecimal(count)).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			cscOut.setCurrentStock(vProduct.getStock(cscOut.getStockArea(), cscOut.getStockType()) + vProduct.getLockCount(cscOut.getStockArea(), cscOut.getStockType()));
			cscOut.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			cscOut.setCurrentCargoStock(cpsOut.getStockCount() + cpsOut.getStockLockCount());
			cscOut.setCargoStoreType(ciOut.getStoreType());
			cscOut.setCargoWholeCode(ciOut.getWholeCode());
			cscOut.setStockPrice(vProduct.getPrice5());
			cscOut.setAllStockPriceSum((new BigDecimal(cscOut.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(cscOut.getStockPrice()))).doubleValue());
			if (this.cargoProductStockBeanDao.insertCargoStockCardBean(cscOut) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
		}

		// 减商品库存
		// 商品进销存卡片
		// 批次
		for (FittingOutBean bean : beanList) {
			voProduct vProduct = this.productDao.getProduct(" code = '" + bean.getCode() + "' AND parent_id1 = 1536 ");
			if (vProduct == null) {
				throw new RuntimeException(bean.getCode() + "配件不存在");
			}

			StringBuffer sbCondition = new StringBuffer();
			sbCondition.append(" product_id = ").append(vProduct.getId());
			sbCondition.append(" AND area = ").append(asrfBean.getAreaId());
			sbCondition.append(" AND type = ").append(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);

			ProductStockBean psOut = this.productStockDao.getProductStock(sbCondition.toString());
			if (psOut == null || psOut.getStock() < bean.getCount()) {
				throw new RuntimeException(bean.getCode() + "商品库存不足");
			}

			if (!this.productStockDao.updateStockCount(psOut.getId(), -bean.getCount())) {
				throw new RuntimeException(bean.getCode() + "商品库存不足");
			}
			HashMap<String, String> psMap = new HashMap<String, String>();
			psMap.put("condition", "product_id=" + vProduct.getId());
			psMap.put("index", "-1");
			psMap.put("count", "-1");
			psMap.put("orderBy", null);
			vProduct.setPsList(this.productStockDao.getProductStockList(psMap));

			// 商品进销存卡片
			StockCardBean outsc = new StockCardBean();
			outsc.setCardType(StockCardBean.CARDTYPE_RECEIVE_AFTERSALE_OUT_FITTING);
			outsc.setCode(asrfBean.getCode());
			outsc.setCreateDatetime(DateUtil.getNow());
			outsc.setStockType(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);
			outsc.setStockArea(asrfBean.getAreaId());
			outsc.setProductId(vProduct.getId());
			outsc.setStockId(psOut.getId());
			outsc.setStockOutCount(bean.getCount());
			outsc.setStockOutPriceSum((new BigDecimal(bean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			outsc.setCurrentStock(vProduct.getStock(outsc.getStockArea(), outsc.getStockType()) + vProduct.getLockCount(outsc.getStockArea(), outsc.getStockType()));
			outsc.setStockAllArea(vProduct.getStock(outsc.getStockArea()) + vProduct.getLockCount(outsc.getStockType()));
			outsc.setStockAllType(vProduct.getStockAllType(outsc.getStockType()) + vProduct.getLockCountAllType(outsc.getStockType()));
			outsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			outsc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
			outsc.setAllStockPriceSum((new BigDecimal(outsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outsc.getStockPrice()))).doubleValue());
			if (this.productStockDao.insertStockCardBean(outsc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}

			if (asrfBean.getTarget() != AfterSaleReceiveFitting.TARGET3) {
				sbCondition.setLength(0);
				sbCondition.append(" product_id = ").append(vProduct.getId());
				sbCondition.append(" AND stock_area = ").append(asrfBean.getAreaId());
				sbCondition.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);
				sbCondition.append(" AND batch_count > 0 ");
				List<StockBatchBean> batchList = this.productStockDao.getStockBatchBeanList(sbCondition.toString(), -1, -1, null);
				if (batchList == null || batchList.size() == 0) {
					continue;
				}
				int totalCount = bean.getCount();
				for (StockBatchBean batchBean : batchList) {
					int tempCount = 0;
					if (totalCount >= batchBean.getBatchCount()){
						if (!this.productStockDao.deleteStockBatchBean(batchBean.getId())) {
							throw new RuntimeException("数据库操作失败");
						}
						totalCount -= batchBean.getBatchCount();
						tempCount = batchBean.getBatchCount();
					} else {
						if (!this.productStockDao.udpateStockBatchBean(" batch_count = batch_count - " + totalCount, "id = " + batchBean.getId())) {
							throw new RuntimeException("数据库操作失败");
						}
						tempCount = totalCount;
						totalCount = 0;
					}
					StockBatchLogBean sblBean = new StockBatchLogBean();
					sblBean.setCode(asrfBean.getCode());
					sblBean.setStockType(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);
					sblBean.setStockArea(asrfBean.getAreaId());
					sblBean.setBatchCode(batchBean.getCode());
					sblBean.setBatchCount(tempCount);
					sblBean.setBatchPrice(batchBean.getPrice());
					sblBean.setProductId(batchBean.getProductId());
					sblBean.setRemark("配件领用出库");
					sblBean.setCreateDatetime(DateUtil.getNow());
					sblBean.setUserId(user.getId());
					if (!this.productStockDao.insertStockBatchLogBean(sblBean)){
						throw new RuntimeException("数据库操作失败");
					}
					if (totalCount <= 0) {
						break;
					}				
				}			
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void customerGoodIn(List<FittingOutBean> beanList, AfterSaleReceiveFitting asrfBean) {
		for (FittingOutBean bean : beanList) {
			voProduct vProduct = this.productDao.getProduct(" code = '" + bean.getCode() + "' AND parent_id1 = 1536 ");
			if (vProduct == null) {
				throw new RuntimeException(bean.getCode() + "配件不存在");
			}

			StringBuffer sbCondition = new StringBuffer();
			sbCondition.append(" area_id = ").append(asrfBean.getAreaId());
			sbCondition.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);
			sbCondition.append(" AND store_type = ").append(CargoInfoBean.STORE_TYPE2);
			sbCondition.append(" AND type = ").append(CargoInfoBean.TYPE3);
			
			CargoInfoBean ciIn = this.cargoInfoBeanDao.selectByCondition(sbCondition.toString());
			if(ciIn == null){
				throw new RuntimeException("未找到 客户库 完好配件缓存区货位");
			}
			
			sbCondition.setLength(0);
			sbCondition.append(" cargo_id = ").append(ciIn.getId());
			sbCondition.append(" AND product_id = ").append(vProduct.getId());
			CargoProductStockBean cpsIn = this.cargoProductStockBeanDao.selectByCondition(sbCondition.toString());
			if (cpsIn == null) {
				cpsIn = new CargoProductStockBean();
				cpsIn.setCargoId(ciIn.getId());
				cpsIn.setProductId(vProduct.getId());
				cpsIn.setStockCount(bean.getCount());
				if (this.cargoProductStockBeanDao.insert(cpsIn) <= 0) {
					throw new RuntimeException("数据库操作失败");
				}					
			} else {
				if (!this.cargoProductStockBeanDao.updateStockCount(cpsIn.getId(), bean.getCount())) {
					throw new RuntimeException("数据库操作失败");
				}
			}

			sbCondition.setLength(0);
			sbCondition.append(" product_id = ").append(vProduct.getId());
			sbCondition.append(" AND area = ").append(asrfBean.getAreaId());
			sbCondition.append(" AND type = ").append(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);

			ProductStockBean psBean = this.productStockDao.getProductStock(sbCondition.toString());
			if (!this.productStockDao.updateStockCount(psBean.getId(), bean.getCount())) {
				throw new RuntimeException("数据库操作失败");
			}
			
			HashMap<String, String> psMap = new HashMap<String, String>();
			psMap.put("condition", "product_id=" + vProduct.getId());
			psMap.put("index", "-1");
			psMap.put("count", "-1");
			psMap.put("orderBy", null);
			vProduct.setPsList(this.productStockDao.getProductStockList(psMap));

			CargoStockCardBean incsc = new CargoStockCardBean();
			incsc.setCardType(CargoStockCardBean.CARDTYPE_RECEIVE_CUSTOMER_IN_FITTING);
			incsc.setCode(asrfBean.getCode());
			incsc.setCreateDatetime(DateUtil.getNow());
			incsc.setStockType(ciIn.getStockType());
			incsc.setStockArea(ciIn.getAreaId());
			incsc.setProductId(vProduct.getId());
			incsc.setStockId(cpsIn.getId());
			incsc.setStockInCount(bean.getCount());
			incsc.setStockInPriceSum((new BigDecimal(bean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
			incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			incsc.setCurrentCargoStock(cpsIn.getStockCount() + cpsIn.getStockLockCount());
			incsc.setCargoStoreType(ciIn.getStoreType());
			incsc.setCargoWholeCode(ciIn.getWholeCode());
			incsc.setStockPrice(vProduct.getPrice5());
			incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
			if (this.cargoProductStockBeanDao.insertCargoStockCardBean(incsc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
						
			// 商品进销存卡片
			StockCardBean insc = new StockCardBean();
			insc.setCardType(StockCardBean.CARDTYPE_RECEIVE_CUSTOMER_IN_FITTING);
			insc.setCode(asrfBean.getCode());
			insc.setCreateDatetime(DateUtil.getNow());
			insc.setStockType(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);
			insc.setStockArea(asrfBean.getAreaId());
			insc.setProductId(vProduct.getId());
			insc.setStockId(psBean.getId());
			insc.setStockInCount(bean.getCount());
			insc.setStockInPriceSum((new BigDecimal(bean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			insc.setCurrentStock(vProduct.getStock(insc.getStockArea(), insc.getStockType()) + vProduct.getLockCount(insc.getStockArea(), insc.getStockType()));
			insc.setStockAllArea(vProduct.getStock(insc.getStockArea()) + vProduct.getLockCount(insc.getStockType()));
			insc.setStockAllType(vProduct.getStockAllType(insc.getStockType()) + vProduct.getLockCountAllType(insc.getStockType()));
			insc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			insc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
			insc.setAllStockPriceSum((new BigDecimal(insc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(insc.getStockPrice()))).doubleValue());
			if (this.productStockDao.insertStockCardBean(insc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void afterSaleBadIn(List<FittingOutBean> beanList, AfterSaleReceiveFitting asrfBean) {
		for (FittingOutBean bean : beanList) {
			voProduct vProduct = this.productDao.getProduct(" code = '" + bean.getCode() + "' AND parent_id1 = 1536 ");
			if (vProduct == null) {
				throw new RuntimeException(bean.getCode() + "配件不存在");
			}

			StringBuffer sbCondition = new StringBuffer();
			sbCondition.append(" area_id = ").append(asrfBean.getAreaId());
			sbCondition.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);
			sbCondition.append(" AND store_type = ").append(CargoInfoBean.STORE_TYPE2);
			sbCondition.append(" AND type = ").append(CargoInfoBean.TYPE4);
			
			CargoInfoBean ciIn = this.cargoInfoBeanDao.selectByCondition(sbCondition.toString());
			if(ciIn == null){
				throw new RuntimeException("未找到 残次售后配件库 缓存区货位");
			}
			
			sbCondition.setLength(0);
			sbCondition.append(" cargo_id = ").append(ciIn.getId());
			sbCondition.append(" AND product_id = ").append(vProduct.getId());
			CargoProductStockBean cpsIn = this.cargoProductStockBeanDao.selectByCondition(sbCondition.toString());
			if (cpsIn == null) {
				cpsIn = new CargoProductStockBean();
				cpsIn.setCargoId(ciIn.getId());
				cpsIn.setProductId(vProduct.getId());
				cpsIn.setStockCount(bean.getCount());
				if(this.cargoProductStockBeanDao.insert(cpsIn) <= 0){
					throw new RuntimeException("数据库操作失败");
				}					
			} else {
				if(!this.cargoProductStockBeanDao.updateStockCount(cpsIn.getId(), bean.getCount())){
					throw new RuntimeException("数据库操作失败");
				}
			}

			sbCondition.setLength(0);
			sbCondition.append(" product_id = ").append(vProduct.getId());
			sbCondition.append(" AND area = ").append(asrfBean.getAreaId());
			sbCondition.append(" AND type = ").append(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);

			ProductStockBean psBean = this.productStockDao.getProductStock(sbCondition.toString());
			if (!this.productStockDao.updateStockCount(psBean.getId(), bean.getCount())) {
				throw new RuntimeException("数据库操作失败");
			}
			
			HashMap<String, String> psMap = new HashMap<String, String>();
			psMap.put("condition", "product_id=" + vProduct.getId());
			psMap.put("index", "-1");
			psMap.put("count", "-1");
			psMap.put("orderBy", null);
			vProduct.setPsList(this.productStockDao.getProductStockList(psMap));

			CargoStockCardBean incsc = new CargoStockCardBean();
			incsc.setCardType(CargoStockCardBean.CARDTYPE_RECEIVE_AFTERSALE_IN_FITTING);
			incsc.setCode(asrfBean.getCode());
			incsc.setCreateDatetime(DateUtil.getNow());
			incsc.setStockType(ciIn.getStockType());
			incsc.setStockArea(ciIn.getAreaId());
			incsc.setProductId(vProduct.getId());
			incsc.setStockId(cpsIn.getId());
			incsc.setStockInCount(bean.getCount());
			incsc.setStockInPriceSum((new BigDecimal(bean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
			incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			incsc.setCurrentCargoStock(cpsIn.getStockCount() + cpsIn.getStockLockCount());
			incsc.setCargoStoreType(ciIn.getStoreType());
			incsc.setCargoWholeCode(ciIn.getWholeCode());
			incsc.setStockPrice(vProduct.getPrice5());
			incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
			if (this.cargoProductStockBeanDao.insertCargoStockCardBean(incsc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
						
			// 商品进销存卡片
			StockCardBean insc = new StockCardBean();
			insc.setCardType(StockCardBean.CARDTYPE_RECEIVE_AFTERSALE_IN_FITTING);
			insc.setCode(asrfBean.getCode());
			insc.setCreateDatetime(DateUtil.getNow());
			insc.setStockType(CargoInfoBean.STOCKTYPE_AFTER_SALE_FIITING);
			insc.setStockArea(asrfBean.getAreaId());
			insc.setProductId(vProduct.getId());
			insc.setStockId(psBean.getId());
			insc.setStockInCount(bean.getCount());
			insc.setStockInPriceSum((new BigDecimal(bean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			insc.setCurrentStock(vProduct.getStock(insc.getStockArea(), insc.getStockType()) + vProduct.getLockCount(insc.getStockArea(), insc.getStockType()));
			insc.setStockAllArea(vProduct.getStock(insc.getStockArea()) + vProduct.getLockCount(insc.getStockType()));
			insc.setStockAllType(vProduct.getStockAllType(insc.getStockType()) + vProduct.getLockCountAllType(insc.getStockType()));
			insc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			insc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
			insc.setAllStockPriceSum((new BigDecimal(insc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(insc.getStockPrice()))).doubleValue());
			if (this.productStockDao.insertStockCardBean(insc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void customerBadOut(List<FittingOutBean> beanList, AfterSaleReceiveFitting asrfBean) {
		for (FittingOutBean bean : beanList) {
			voProduct vProduct = this.productDao.getProduct(" code = '" + bean.getCode() + "' AND parent_id1 = 1536 ");
			if (vProduct == null) {
				throw new RuntimeException(bean.getCode() + "配件不存在");
			}
			StringBuffer sbCondition = new StringBuffer();			
			sbCondition.append(" area_id = ").append(asrfBean.getAreaId());
			sbCondition.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);
			sbCondition.append(" AND store_type = ").append(CargoInfoBean.STORE_TYPE2);
			sbCondition.append(" AND type = ").append(CargoInfoBean.TYPE4);
			
			CargoInfoBean ciOut = this.cargoInfoBeanDao.selectByCondition(sbCondition.toString());
			if(ciOut == null){
				throw new RuntimeException("未找到 完好配件客户库 缓存区货位");
			}
			
			sbCondition.setLength(0);
			sbCondition.append(" cargo_id = ").append(ciOut.getId());
			sbCondition.append(" AND product_id = ").append(vProduct.getId());
			CargoProductStockBean cpsOut = this.cargoProductStockBeanDao.selectByCondition(sbCondition.toString());
			if (cpsOut == null || cpsOut.getStockCount() < bean.getCount()) {
				throw new RuntimeException("配件客户库缓存区货位商品库存不足");
			}
			
			if (!this.cargoProductStockBeanDao.updateStockCount(cpsOut.getId(), -bean.getCount())) {
				throw new RuntimeException("配件客户库缓存区货位商品库存不足");
			}

			sbCondition.setLength(0);
			sbCondition.append(" product_id = ").append(vProduct.getId());
			sbCondition.append(" AND area = ").append(asrfBean.getAreaId());
			sbCondition.append(" AND type = ").append(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);

			ProductStockBean psOut = this.productStockDao.getProductStock(sbCondition.toString());
			if (psOut == null || psOut.getStock() < bean.getCount()) {
				throw new RuntimeException("配件客户库商品库存不足");
			}
			if (!this.productStockDao.updateStockCount(psOut.getId(), -bean.getCount())) {
				throw new RuntimeException("配件客户库商品库存不足");
			}
			
			HashMap<String, String> psMap = new HashMap<String, String>();
			psMap.put("condition", "product_id=" + vProduct.getId());
			psMap.put("index", "-1");
			psMap.put("count", "-1");
			psMap.put("orderBy", null);
			vProduct.setPsList(this.productStockDao.getProductStockList(psMap));

			CargoStockCardBean outcsc = new CargoStockCardBean();
			outcsc.setCardType(CargoStockCardBean.CARDTYPE_RECEIVE_CUSTOMER_OUT_FITTING);
			outcsc.setCode(asrfBean.getCode());
			outcsc.setCreateDatetime(DateUtil.getNow());
			outcsc.setStockType(ciOut.getStockType());
			outcsc.setStockArea(ciOut.getAreaId());
			outcsc.setProductId(vProduct.getId());
			outcsc.setStockId(cpsOut.getId());
			outcsc.setStockOutCount(bean.getCount());
			outcsc.setStockOutPriceSum((new BigDecimal(bean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
			outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			outcsc.setCurrentCargoStock(cpsOut.getStockCount() + cpsOut.getStockLockCount());
			outcsc.setCargoStoreType(ciOut.getStoreType());
			outcsc.setCargoWholeCode(ciOut.getWholeCode());
			outcsc.setStockPrice(vProduct.getPrice5());
			outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
			if (this.cargoProductStockBeanDao.insertCargoStockCardBean(outcsc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
						
			// 商品进销存卡片
			StockCardBean outsc = new StockCardBean();
			outsc.setCardType(StockCardBean.CARDTYPE_RECEIVE_CUSTOMER_OUT_FITTING);
			outsc.setCode(asrfBean.getCode());
			outsc.setCreateDatetime(DateUtil.getNow());
			outsc.setStockType(CargoInfoBean.STOCKTYPE_CUSTOMER_FITTING);
			outsc.setStockArea(asrfBean.getAreaId());
			outsc.setProductId(vProduct.getId());
			outsc.setStockId(psOut.getId());
			outsc.setStockOutCount(bean.getCount());
			outsc.setStockOutPriceSum((new BigDecimal(bean.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
			outsc.setCurrentStock(vProduct.getStock(outsc.getStockArea(), outsc.getStockType()) + vProduct.getLockCount(outsc.getStockArea(), outsc.getStockType()));
			outsc.setStockAllArea(vProduct.getStock(outsc.getStockArea()) + vProduct.getLockCount(outsc.getStockType()));
			outsc.setStockAllType(vProduct.getStockAllType(outsc.getStockType()) + vProduct.getLockCountAllType(outsc.getStockType()));
			outsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
			outsc.setStockPrice(vProduct.getPrice5());// 新的库存价格，每次入库都要计算
			outsc.setAllStockPriceSum((new BigDecimal(outsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outsc.getStockPrice()))).doubleValue());
			if (this.productStockDao.insertStockCardBean(outsc) <= 0) {
				throw new RuntimeException("数据库操作失败");
			}
		}
	}
}
