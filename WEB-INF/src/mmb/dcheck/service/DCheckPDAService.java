package mmb.dcheck.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mmb.common.dao.ProductBarcodeDao;
import mmb.common.dao.ProductDao;
import mmb.dcheck.dao.DynamicCheckBeanDao;
import mmb.dcheck.dao.DynamicCheckCargoBeanDao;
import mmb.dcheck.dao.DynamicCheckCargoDifferenceBeanDao;
import mmb.dcheck.dao.DynamicCheckDataDao;
import mmb.dcheck.dao.DynamicCheckExceptionDataDao;
import mmb.dcheck.dao.DynamicCheckLogDao;
import mmb.dcheck.model.DynamicCheckBean;
import mmb.dcheck.model.DynamicCheckCargoBean;
import mmb.dcheck.model.DynamicCheckCargoDifferenceBean;
import mmb.dcheck.model.DynamicCheckData;
import mmb.dcheck.model.DynamicCheckExceptionData;
import mmb.rec.sys.easyui.Json;
import mmb.ware.cargo.dao.CargoInfoDao;
import mmb.ware.cargo.dao.CargoInfoPassageDao;
import mmb.ware.cargo.dao.CargoInfoStockAreaDao;
import mmb.ware.cargo.dao.CargoProductStockDao;
import mmb.ware.cargo.model.CargoInfo;
import mmb.ware.cargo.model.CargoInfoPassage;
import mmb.ware.cargo.model.CargoInfoStockArea;
import mmb.ware.cargo.model.CargoProductStock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Service
public class DCheckPDAService {

	@Autowired
	private DynamicCheckBeanDao dynamicCheckBeanDao;
	
	@Autowired
	private DynamicCheckExceptionDataDao dynamicCheckExceptionDao;

	@Autowired
	private CargoInfoStockAreaDao cargoInfoStockAreaDao;

	@Autowired
	private CargoInfoPassageDao cargoInfoPassageDao;

	@Autowired
	private CargoInfoDao cargoInfoDao;

	@Autowired
	private DynamicCheckCargoBeanDao dynamicCheckCargoBeanDao;

	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductBarcodeDao productBarcodeDao;

	@Autowired
	private CargoProductStockDao cargoProductStockDao;

	@Autowired
	private DynamicCheckCargoDifferenceBeanDao dccDifferenceBeanDao;

	@Autowired
	private DynamicCheckLogDao logDao;
	
	@Autowired
	private DynamicCheckDataDao dataDao;
	
	@Autowired
	private DynamicCheckExceptionDataDao exceptionDao;
	
	/**
	 * 
	 * @param type
	 *            1动碰盘， 2大盘
	 * @param areaId
	 * @return
	 */
	public Json getDCheckCode(int checkType, int areaId) {
		Json j = new Json();

		// 1 未盘点 2 盘点中  3盘点结束
		DynamicCheckBean bean = this.dynamicCheckBeanDao.selectByCondition(" area_id = " + areaId + " AND check_type = " + checkType + " AND status <> 3 ");

		if (bean == null) {
			j.setMsg("没有领取到盘点单");
			return j;
		}

		j.setSuccess(true);
		j.setObj(bean.getCode());
		return j;
	}
	
	public Json getDCheck(int checkType, int areaId) {
		Json j = new Json();

		// 1 未盘点 2 盘点中  3盘点结束
		DynamicCheckBean bean = this.dynamicCheckBeanDao.selectByCondition(" area_id = " + areaId + " AND check_type = " + checkType + " AND status <> 3 ");

		if (bean == null) {
			j.setMsg("没有领取到盘点单");
			return j;
		}

		j.setSuccess(true);
		j.setObj(bean);
		return j;
	}

	/**
	 * 获取合格库的区和巷道列表
	 * 
	 * @param areaId
	 * @return
	 */
	public Map<String, Object> getAreaAndPassage(int areaId) {

		HashMap<String, String> param = new HashMap<String, String>();
		param.put("condition", " area_id = " + areaId + " AND stock_type = " + CargoInfoBean.STOCKTYPE_QUALIFIED);
		param.put("orderBy", null);
		param.put("count", "-1");
		param.put("index", "-1");

		List<CargoInfoStockArea> areaList = cargoInfoStockAreaDao.selectCargoInfoStockAreaListSlave(param);
		if (areaList == null || areaList.size() == 0)
			return null;

		List<CargoInfoPassage> passageList = cargoInfoPassageDao.selectCargoInfoPassageListSlave(param);
		if (passageList == null || passageList.size() == 0)
			return null;

		Map<Integer, String> areaMap = new LinkedHashMap<Integer, String>();
		Map<Integer, String> passageMap = new LinkedHashMap<Integer, String>();
		HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();

		for (CargoInfoStockArea stockArea : areaList) {
			areaMap.put(stockArea.getId(), stockArea.getCode());
			map.put(stockArea.getId(), new ArrayList<Integer>());
		}

		for (CargoInfoPassage cargoInfoPassage : passageList) {
			passageMap.put(cargoInfoPassage.getId(), cargoInfoPassage.getCode());
			if (map.containsKey(cargoInfoPassage.getStockAreaId())) {
				map.get(cargoInfoPassage.getStockAreaId()).add(cargoInfoPassage.getId());
			}
		}

		HashMap<String, Object> result = new HashMap<String, Object>();

		result.put("areaMap", areaMap);
		result.put("passageMap", passageMap);
		result.put("map", map);

		return result;
	}

	/**
	 * 获取需要盘点的货位列表
	 * 
	 * @param map
	 * @return
	 */
	public Json getCheckCargoList(HashMap<String, Object> map) {
		String code = map.get("code").toString();
		int areaId = StringUtil.toInt(map.get("areaId").toString());
		int stockArea = StringUtil.toInt(map.get("stockArea").toString());
		int passage = StringUtil.toInt(map.get("passage").toString());
		// 1 动碰盘 2 大盘
		int checkType = StringUtil.toInt(map.get("type").toString());
		// 1、2、3盘
		int flag = StringUtil.toInt(map.get("flag").toString());

		Json j = new Json();

		DynamicCheckBean dcBean = this.dynamicCheckBeanDao.selectByCondition(" code = '" + StringUtil.dealParam(code) + "' AND area_id = " + areaId + " AND check_type = " + checkType + " AND status <> 3 ");

		if (dcBean == null) {
			j.setMsg("盘点单不存在或盘点任务已结束");
			return j;
		}

		List<String> list = new ArrayList<String>();
		// 大盘 一盘
		// 货位列表 = 应盘(合格库非缓存区货位) - 已盘
		if (checkType == 2 && flag == 1) {
			// 合格库 使用中的 非缓存区货位
			StringBuilder sb = new StringBuilder();
			sb.append(" area_id = ").append(areaId);
			sb.append(" AND  stock_type = ").append(CargoInfoBean.STOCKTYPE_QUALIFIED);
			sb.append(" AND store_type <> ").append(CargoInfoBean.STORE_TYPE2);
			sb.append(" AND status = 0 ");
			if (stockArea > 0) {
				sb.append(" AND stock_area_id = ").append(stockArea);
			}
			if (passage > 0) {
				sb.append(" AND passage_id = ").append(passage);
			}
			System.err.println(DateUtil.getNow() + " [PDA盘点,getCheckCargoList] SQL: " + sb.toString());
			List<CargoInfo> cargoList = this.cargoInfoDao.selectListSlave(sb.toString(), -1, -1, null);
			if (cargoList == null || cargoList.size() == 0) {
				j.setMsg("没有查询到需要盘点的货位");
				return j;
			}

			//如果改货位盘点产生了异常数据,不过滤货位.否则过滤货物.
			HashMap<String, String> mapFinished = new HashMap<String, String>();
			sb.setLength(0);
			sb.append(" dynamic_check_id = ").append(dcBean.getId());
			sb.append(" AND check_user_id1 > 0  GROUP BY cargo_id ");
			List<DynamicCheckCargoBean> dccList = this.dynamicCheckCargoBeanDao.selectList(sb.toString(), -1, -1, null);
			if (dccList != null && dccList.size() != 0) {
				for (DynamicCheckCargoBean dynamicCheckCargoBean : dccList) {
					mapFinished.put(dynamicCheckCargoBean.getCargoWholeCode(), "");
				}
			}
			//从完成货位中剔除异常数据.异常货位需要复盘
			if(!mapFinished.isEmpty()){
				this.excludeExceptionCargo(mapFinished,dcBean.getId());
			}
			
			// 过滤已经盘点完成的货位
			for (CargoInfo cargo : cargoList) {
				if (!mapFinished.containsKey(cargo.getWholeCode())) {
					list.add(cargo.getWholeCode());
				}
			}

		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(" dynamic_check_id = ").append(dcBean.getId());
			// 0 待一盘, 1 待二盘, 3待三盘
			sb.append(" AND status = ").append(flag - 1);
			if (stockArea > 0) {
				sb.append(" AND cargo_info_stock_area_id = ").append(stockArea);
			}
			if (passage > 0) {
				sb.append(" AND cargo_info_passage_id = ").append(passage);
			}
			sb.append(" GROUP BY cargo_id ");

			System.err.println(DateUtil.getNow() +  " [PDA盘点,getCheckCargoList] SQL: " + sb.toString());
			List<DynamicCheckCargoBean> dccList = this.dynamicCheckCargoBeanDao.selectList(sb.toString(), -1, -1, null);
			if (dccList == null || dccList.size() == 0) {
				j.setMsg("没有查询到需要盘点的货位");
				return j;
			}
			for (DynamicCheckCargoBean dynamicCheckCargoBean : dccList) {
				list.add(dynamicCheckCargoBean.getCargoWholeCode());
			}
		}

		j.setObj(list);
		j.setSuccess(true);
		return j;
	}

	private void excludeExceptionCargo(HashMap<String, String> mapFinished, int checkId) {
		List<String> cargoCode = this.dynamicCheckExceptionDao.getExceptionCargoCode(checkId);
		for(String code : cargoCode){
			if(mapFinished.containsKey(code)){
				mapFinished.remove(code);
			}
		}
	}

	/**
	 * 获取需要盘点的商品列表
	 * 
	 * @param map
	 * @return
	 */
	public Json getCheckProductList(HashMap<String, Object> map) {
		String code = map.get("code").toString();
		String cargo = map.get("cargo").toString();
		int areaId = StringUtil.toInt(map.get("areaId").toString());
		// 1 动碰盘 2 大盘
		int checkType = StringUtil.toInt(map.get("type").toString());
		// 1、2、3盘
		int flag = StringUtil.toInt(map.get("flag").toString());

		Json j = new Json();

		DynamicCheckBean dcBean = this.dynamicCheckBeanDao.selectByCondition(" code = '" + StringUtil.dealParam(code) + "' AND area_id = " + areaId + " AND check_type = " + checkType + " AND status <> 3 ");

		if (dcBean == null) {
			j.setMsg("盘点单不存在或盘点任务已结束");
			return j;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" whole_code = '").append(StringUtil.dealParam(cargo)).append("' ");
		sb.append(" AND store_type <> ").append(CargoInfoBean.STORE_TYPE2);
		sb.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" AND area_id = ").append(areaId);
		sb.append(" AND status = 0 ");
		
		CargoInfo cargoInfo = this.cargoInfoDao.selectByCondition(sb.toString());

		if (cargoInfo == null) {
			throw new RuntimeException("货位不存在");
		}

		List<Integer> products = new ArrayList<Integer>();

		// 大盘 一盘
		// 货位列表 = 应盘(合格库非缓存区货位) - 已盘
		if (checkType == 2 && flag == 1) {
			StringBuilder sbCondition = new StringBuilder();
			sbCondition.append(" dynamic_check_id = ").append(dcBean.getId());
			sbCondition.append(" AND cargo_id = ").append(cargoInfo.getId());
			if (this.dynamicCheckCargoBeanDao.selectCount(sbCondition.toString()) > 0) {
				j.setMsg("该货位上的商品已盘点完毕");
				return j;
			}
			List<CargoProductStock> cpsList = this.cargoProductStockDao.selectListSlave(" cargo_id = " + cargoInfo.getId() + " AND (( stock_count + stock_lock_count) > 0 ) ", -1, -1, null);
			if (cpsList != null && cpsList.size() != 0) {
				for (CargoProductStock cargoProductStock : cpsList) {
					products.add(cargoProductStock.getProductId());
				}
			}
		} else {
			StringBuilder sbCondition = new StringBuilder();
			sbCondition.append(" dynamic_check_id = ").append(dcBean.getId());
			sbCondition.append(" AND cargo_id = ").append(cargoInfo.getId());
			// 0 待一盘, 1 待二盘, 3待三盘
			sbCondition.append(" AND status = ").append(flag - 1);

			List<DynamicCheckCargoBean> dccList = this.dynamicCheckCargoBeanDao.selectList(sbCondition.toString(), -1, -1, null);
			if (dccList == null || dccList.size() == 0) {
				j.setMsg("没有查询到需要盘点的商品");
				return j;
			}
			for (DynamicCheckCargoBean dynamicCheckCargoBean : dccList) {
				products.add(dynamicCheckCargoBean.getProductId());
			}
		}

		if (products.size() == 0) {
			j.setMsg("没有查询到可以盘点的商品");
			return j;
		}

		// key code value productName
		HashMap<String, String> resultMap = new HashMap<String, String>();

		for (Integer integer : products) {		
			// 使用条码
			ProductBarcodeVO pBarcode = this.productBarcodeDao.getProductBarcode(" product_id = " + integer.intValue());			
			voProduct product = this.productDao.getProduct(" id = " + integer.intValue());
			if (pBarcode != null && product != null) {
				resultMap.put(pBarcode.getBarcode(), product.getName());
			}
		}
		j.setObj(resultMap);
		j.setSuccess(true);
		return j;
	}

	/**
	 * 验证商品编号，并获取商品名称
	 * 
	 * @param code
	 * @return
	 */
	public Json getProductName(String code) {
		Json j = new Json();

		ProductBarcodeVO pBarcode = this.productBarcodeDao.getProductBarcode(" barcode = '" + StringUtil.dealParam(code) + "' ");
		if (pBarcode == null) {
			j.setMsg("条码不存在");
			return j;			
		}
		
		voProduct product = this.productDao.getProduct(" id = " + pBarcode.getProductId());
		if (product == null) {
			j.setMsg("商品不存在");
			return j;
		}
		
		j.setObj(product.getName());
		j.setSuccess(true);
		return j;
	}

	/**
	 * 完成动态盘点功能
	 * 
	 * @param map
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void finishDynamicCheck(HashMap<String, Object> map, voUser user) {
		String code = map.get("code").toString();
		int areaId = StringUtil.toInt(map.get("areaId").toString());
		// 1 动碰盘 2 大盘
		int checkType = StringUtil.toInt(map.get("type").toString());
		// 1、2、3盘
		int flag = StringUtil.toInt(map.get("flag").toString());
		// 盘点的货位
		String cargo = map.get("cargo").toString();
		List<HashMap<String, String>> products = (List<HashMap<String, String>>) map.get("products");

		DynamicCheckBean dcBean = this.dynamicCheckBeanDao.selectByCondition(" code = '" + StringUtil.dealParam(code) + "' AND area_id = " + areaId + " AND check_type = " + checkType + " AND status <> 3 ");

		if (dcBean == null) {
			throw new RuntimeException("盘点单不存在或盘点任务已结束");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" whole_code = '").append(StringUtil.dealParam(cargo)).append("' ");
		sb.append(" AND store_type <> ").append(CargoInfoBean.STORE_TYPE2);
		sb.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" AND area_id = ").append(areaId);

		CargoInfo cargoInfo = this.cargoInfoDao.selectByCondition(sb.toString());

		if (cargoInfo == null) {
			throw new RuntimeException("货位不存在");
		}

		if (products == null || products.size() == 0) {
			throw new RuntimeException("请至少盘点一个商品");
		}

		for (HashMap<String, String> hashMap : products) {
			int checkCount = StringUtil.toInt(hashMap.get("count").toString());
			ProductBarcodeVO pBarcode = this.productBarcodeDao.getProductBarcode(" barcode = '" + StringUtil.dealParam(hashMap.get("code")) + "' ");
			if (pBarcode == null) {
				throw new RuntimeException("条码不存在");
			}
			
			voProduct product = this.productDao.getProduct(" id = " + pBarcode.getProductId());
			if (product == null) {
				throw new RuntimeException("商品不存在");
			}

			int stockCount = 0;
			CargoProductStock cps = this.cargoProductStockDao.selectByCondition(" cargo_id = " + cargoInfo.getId() + " AND product_id = " + product.getId());
			if (cps != null) {
				stockCount = cps.getStockCount() + cps.getStockLockCount();
			}

			// 大盘 一盘
			if (checkType == 2 && flag == 1) {
				// insert
				int countTemp = this.dynamicCheckCargoBeanDao.selectCount(" dynamic_check_id = " + dcBean.getId() + " AND cargo_id = " + cargoInfo.getId() + " AND  product_id = " + product.getId());
				if (countTemp > 0) {
					throw new RuntimeException("该货位上的商品已盘点完毕");
				}

				int differenct = checkCount - stockCount;
				// 默认值 0    1无差异，2盈， 3亏				
				int checkResult = 0;			
				int status = 1;
				if(differenct == 0){
					checkResult = 1;
				}
				// 0 待一盘，1 待二盘，2 待三盘， 3 盘点完成
				if(differenct == 0){
					status = 3;
				}
				
				DynamicCheckCargoBean dccBean = new DynamicCheckCargoBean();
				dccBean.setCargoId(cargoInfo.getId());
				dccBean.setCargoInfoPassageId(cargoInfo.getPassageId());
				dccBean.setCargoInfoStockAreaId(cargoInfo.getStockAreaId());
				dccBean.setCargoWholeCode(cargoInfo.getWholeCode());
				dccBean.setCheckResult(checkResult);
				dccBean.setCheckUserId1(user.getId());
				dccBean.setCheckUsername1(user.getUsername());
				dccBean.setDifference1(differenct);
				dccBean.setDynamicCheckId(dcBean.getId());
				dccBean.setProductCode(product.getCode());
				dccBean.setProductId(product.getId());
				dccBean.setProductName(product.getName());
				dccBean.setStatus(status); 

				if (this.dynamicCheckCargoBeanDao.insert(dccBean) <= 0) {
					throw new RuntimeException("保存盘点结果失败");
				}
			} else {
				// update
				DynamicCheckCargoBean dccBean = this.dynamicCheckCargoBeanDao.selectByCondition("  dynamic_check_id = " + dcBean.getId() + " AND cargo_id = " + cargoInfo.getId() + " AND  product_id = " + product.getId() + " AND status = " + (flag - 1));
				if (dccBean == null) {
					throw new RuntimeException("该盘点任务不存在或已盘点完毕");
				}

				int endCheckTiems = dccBean.getEndCheckTimes();
				int difference = checkCount - stockCount;
				int status = -1;
				int checkResult = 0;// 0默认值 1无差异， 2盈 , 3亏

				if (flag == 1) {
					// 0 待一盘，1 待二盘，2 待三盘， 3 盘点完成
					status = 1;					
					if(difference == 0){
						status = 3; // 盘点完成
					}
				} else if (flag == 2) {
					status = 2; // 待三盘
					// 如果盘点结果无差异或  二盘结果和一盘结果一致,盘点完成
					if (difference == 0 || dccBean.getDifference1() == difference) {
						status = 3; // 盘点完成
					}
				} else if (flag == 3) {
					status = 3; // 盘点完成
				}

				if (status == 3) {
					if (checkCount == stockCount) {
						checkResult = 1; // 1 无差异
					} else if (checkCount > stockCount) {
						checkResult = 2; // 2 盈
					} else if (checkCount < stockCount) {
						checkResult = 3; // 3 亏
					}
					endCheckTiems++;
				}

				StringBuilder sbSet = new StringBuilder();
				sbSet.append(" difference").append(flag).append(" = ").append(difference);
				sbSet.append(" , check_user_id").append(flag).append(" = ").append(user.getId());
				sbSet.append(" , check_username").append(flag).append(" = '").append(user.getUsername()).append("' ");
				sbSet.append(" , end_check_times = ").append(endCheckTiems);
				sbSet.append(" , check_result = ").append(checkResult);
				sbSet.append(" , status =  ").append(status);

				if (this.dynamicCheckCargoBeanDao.updateByCondition(sbSet.toString(), " id = " + dccBean.getId()) <= 0) {
					throw new RuntimeException("保存盘点结果失败");
				}

				// 更新差异明细
				if (status == 3) {
					String setTemp = " difference =  " + difference;
					StringBuilder sbCondition = new StringBuilder();
					sbCondition.append(" cargo_id = ").append(cargoInfo.getId());
					sbCondition.append(" AND product_id = ").append(product.getId());
					sbCondition.append(" AND status = 1 ");

					if (this.dccDifferenceBeanDao.updateByCondition(setTemp, sbCondition.toString()) <= 0) {
						if (difference != 0) {
							DynamicCheckCargoDifferenceBean dccdBean = new DynamicCheckCargoDifferenceBean();
							dccdBean.setAreaId(cargoInfo.getAreaId());
							dccdBean.setCargoId(cargoInfo.getId());
							dccdBean.setCargoWholeCode(cargoInfo.getWholeCode());
							dccdBean.setCargoInfoStockAreaId(cargoInfo.getStockAreaId());
							dccdBean.setProductId(product.getId());
							dccdBean.setProductCode(product.getCode());
							dccdBean.setProductName(product.getName());
							dccdBean.setDifference(difference);
							dccdBean.setStatus(1);

							if (this.dccDifferenceBeanDao.insert(dccdBean) <= 0) {
								throw new RuntimeException("保存差异明细失败");
							}
						}						
					}
				}
			}
		}

		// 盘点中
		if(this.dynamicCheckBeanDao.updateByCondition(" status = 2 " , " id = " + dcBean.getId())<=0){
			throw new RuntimeException("修改盘点单状态失败");
		}
		
	}
	
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void finishDynamicCheck2(HashMap<String, Object> map, voUser user) {
		String code = map.get("code").toString();
		int stockArea = StringUtil.toInt("" + map.get("stockArea"));
		int areaId = StringUtil.toInt(map.get("areaId").toString());
		// 1 动碰盘 2 大盘
		int checkType = StringUtil.toInt(map.get("type").toString());
		// 1、2、3盘
		int flag = StringUtil.toInt(map.get("flag").toString());
		// 盘点的货位
		String cargo = map.get("cargo").toString();
		
		// 查询当前用户是否在本组盘点过，盘点过则不能再次提交
		int passageId = StringUtil.toInt("" + map.get("passage"));
//		int areaId = StringUtil.toInt("" + map.get("areaId"));
		int group = StringUtil.toInt("" + map.get("group"));
		int operator = StringUtil.toInt("" + map.get("operator"));
		
//		DynamicCheckLogBean param = new DynamicCheckLogBean();
//		param.setCargoInfoAreaId(areaId);
//		param.setCargoInfoPassageId(passageId);
//		param.setOperator(operator);
//		param.setGroupId(group);
//		param.setDynamicCheckCode(dynamicCheckCode);
//		
//		DynamicCheckLogBean log = logDao.getDynamicCheckLog(param);
//		if (log != null) {
//			if (log.getOperator() == user.getId()) {
//				throw new RuntimeException("已使用过此分组盘点过！");
//			} 
//		}
		
		
		List<HashMap<String, String>> products = (List<HashMap<String, String>>) map.get("products");

		DynamicCheckBean dcBean = this.dynamicCheckBeanDao.selectByCondition(" code = '" + StringUtil.dealParam(code) + "' AND area_id = " + areaId + " AND check_type = " + checkType + " AND status <> 3 ");

		if (dcBean == null) {
			throw new RuntimeException("盘点单不存在或盘点任务已结束");
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" whole_code = '").append(StringUtil.dealParam(cargo)).append("' ");
		sb.append(" AND store_type <> ").append(CargoInfoBean.STORE_TYPE2);
		sb.append(" AND stock_type = ").append(CargoInfoBean.STOCKTYPE_QUALIFIED);
		sb.append(" AND area_id = ").append(areaId);
		sb.append(" AND passage_id = ").append(passageId);
		sb.append(" AND stock_area_id = ").append(stockArea);

		CargoInfo cargoInfo = this.cargoInfoDao.selectByCondition(sb.toString());

		if (cargoInfo == null) {
			throw new RuntimeException("此区域中，此货位不存在");
		}
//
//		if (products == null || products.size() == 0) {
//			throw new RuntimeException("请至少盘点一个商品");
//		}

		int dynamicCheckId = StringUtil.toInt("" + map.get("dynamicCheckId"));
		
		for (HashMap<String, String> hashMap : products) {
			String productCode = "" + hashMap.get("code");
			int checkCount = StringUtil.toInt(hashMap.get("count").toString());
			ProductBarcodeVO pBarcode = this.productBarcodeDao.getProductBarcode(" barcode = '" + StringUtil.dealParam(hashMap.get("code")) + "' ");
			if (pBarcode == null) {
				throw new RuntimeException("条码不存在");
			}
			
			voProduct product = this.productDao.getProduct(" id = " + pBarcode.getProductId());
			if (product == null) {
				throw new RuntimeException("商品不存在");
			}

			//同一个人同一个组不能重复盘点相同货位相同商品
			DynamicCheckData dataParam = new DynamicCheckData();
			dataParam.setCargoId(cargoInfo.getId());
			dataParam.setProductCode(productCode);
			dataParam.setDynamicCheckId(dynamicCheckId);
			dataParam.setCheckGroup(group);
			dataParam.setOperator(operator);
			DynamicCheckData data = dataDao.getDynamicCheckData(dataParam);
			if(data != null){
				continue;
//				throw new RuntimeException("该货位商品:"+productCode+",您已盘点,不能重复盘点!");
			}
			
			int stockCount = 0;
			CargoProductStock cps = this.cargoProductStockDao.selectByCondition(" cargo_id = " + cargoInfo.getId() + " AND product_id = " + product.getId());
			if (cps != null) {
				stockCount = cps.getStockCount() + cps.getStockLockCount();
			}

			// 大盘 一盘
			if (checkType == 2 && flag == 1) {
				int countTemp = this.dynamicCheckCargoBeanDao.selectCount(" dynamic_check_id = " + dcBean.getId() + " AND cargo_id = " + cargoInfo.getId() + " AND  product_id = " + product.getId());
				if (countTemp > 0) {
					continue;
//					throw new RuntimeException("该货位上的商品(" + productCode + ")已盘点完毕");
				}
				
				// 查询`dynamic_check_data`表，查询到记录说明此商品被盘点过一次，下一步要进行比较,没查询到则写入
				dataParam = new DynamicCheckData();
				dataParam.setCargoId(cargoInfo.getId());
				dataParam.setProductCode(productCode);
				dataParam.setDynamicCheckId(dynamicCheckId);
				data = dataDao.getDynamicCheckData(dataParam);
				
				if (data == null) {
					// 写入
					DynamicCheckData bean = new DynamicCheckData();
					bean.setCargoId(cargoInfo.getId());
					bean.setDynamicCheckId(dynamicCheckId);
					bean.setCargoInfoPassageId(passageId);
					bean.setCargoInfoStockAreaId(stockArea);
					bean.setCargoWholeCode(cargoInfo.getWholeCode());
					bean.setCheckCount(checkCount);
					bean.setCheckGroup(group);
					bean.setProductCode(productCode);
					bean.setProductName(product.getOriname());
					bean.setProductId(product.getId());
					bean.setOperator(operator);
					dataDao.saveDynamicCheckData(bean);
					continue;
				} else {
					// 比对两次盘点的数量是否一致，一致往下走，否则写入异常表
					if (data.getCheckCount() == checkCount) {
						// 一致删除`dynamic_check_data`
						dataDao.deleteDynamicCheckData(data.getId());
						// 删除异常数据
						DynamicCheckExceptionData exceptionData = new DynamicCheckExceptionData();
						exceptionData.setCargoId(cargoInfo.getId());
						exceptionData.setDynamicCheckId(dynamicCheckId);
						exceptionData.setProductId(product.getId());
						exceptionDao.deleteDynamciCheckExceptionData(exceptionData);
					} else {
						// 保证同一商品对应一条异常数据
						DynamicCheckExceptionData exceptionData = new DynamicCheckExceptionData();
						exceptionData.setCargoId(cargoInfo.getId());
						exceptionData.setDynamicCheckId(dynamicCheckId);
						exceptionData.setProductId(product.getId());
						exceptionDao.deleteDynamciCheckExceptionData(exceptionData);
						
						DynamicCheckExceptionData exceptionBean = new DynamicCheckExceptionData();
						exceptionBean.setCargoId(cargoInfo.getId());
						exceptionBean.setCargoInfoAreaId(areaId);
						exceptionBean.setCargoInfoPassageId(passageId);
						exceptionBean.setCargoInfoStockAreaId(stockArea);
						exceptionBean.setCargoWholeCode(cargoInfo.getWholeCode());
						exceptionBean.setDynamicCheckId(dynamicCheckId);
						exceptionBean.setProductCode(productCode);
						exceptionBean.setProductId(product.getId());
						exceptionBean.setProductName(product.getName());
						exceptionDao.saveDynamicCheckExceptionData(exceptionBean);
						// 一致删除`dynamic_check_data`
						dataDao.deleteDynamicCheckData(data.getId());
						continue;
					}
				}

				int differenct = checkCount - stockCount;
				// 默认值 0    1无差异，2盈， 3亏				
				int checkResult = 0;			
				int status = 1;
				if(differenct == 0){
					checkResult = 1;
				}
				// 0 待一盘，1 待二盘，2 待三盘， 3 盘点完成
				if(differenct == 0){
					status = 3;
				}
				
				DynamicCheckCargoBean dccBean = new DynamicCheckCargoBean();
				dccBean.setCargoId(cargoInfo.getId());
				dccBean.setCargoInfoPassageId(cargoInfo.getPassageId());
				dccBean.setCargoInfoStockAreaId(cargoInfo.getStockAreaId());
				dccBean.setCargoWholeCode(cargoInfo.getWholeCode());
				dccBean.setCheckResult(checkResult);
				dccBean.setCheckUserId1(user.getId());
				dccBean.setCheckUsername1(user.getUsername());
				dccBean.setDifference1(differenct);
				dccBean.setDynamicCheckId(dcBean.getId());
				dccBean.setProductCode(product.getCode());
				dccBean.setProductId(product.getId());
				dccBean.setProductName(product.getName());
				dccBean.setStatus(status); 

				if (this.dynamicCheckCargoBeanDao.insert(dccBean) <= 0) {
					throw new RuntimeException("保存盘点结果失败");
				}
			} else {
				// update
				DynamicCheckCargoBean dccBean = this.dynamicCheckCargoBeanDao.selectByCondition("  dynamic_check_id = " + dcBean.getId() + " AND cargo_id = " + cargoInfo.getId() + " AND  product_id = " + product.getId() + " AND status = " + (flag - 1));
				if (dccBean == null) {
					throw new RuntimeException("该盘点任务不存在或已盘点完毕");
				}

				int endCheckTiems = dccBean.getEndCheckTimes();
				int difference = checkCount - stockCount;
				int status = -1;
				int checkResult = 0;// 0默认值 1无差异， 2盈 , 3亏

				if (flag == 1) {
					// 0 待一盘，1 待二盘，2 待三盘， 3 盘点完成
					status = 1;					
					if(difference == 0){
						status = 3; // 盘点完成
					}
				} else if (flag == 2) {
					status = 2; // 待三盘
					// 如果盘点结果无差异或  二盘结果和一盘结果一致,盘点完成
					if (difference == 0 || dccBean.getDifference1() == difference) {
						status = 3; // 盘点完成
					}
				} else if (flag == 3) {
					status = 3; // 盘点完成
				}

				if (status == 3) {
					if (checkCount == stockCount) {
						checkResult = 1; // 1 无差异
					} else if (checkCount > stockCount) {
						checkResult = 2; // 2 盈
					} else if (checkCount < stockCount) {
						checkResult = 3; // 3 亏
					}
					endCheckTiems++;
				}

				StringBuilder sbSet = new StringBuilder();
				sbSet.append(" difference").append(flag).append(" = ").append(difference);
				sbSet.append(" , check_user_id").append(flag).append(" = ").append(user.getId());
				sbSet.append(" , check_username").append(flag).append(" = '").append(user.getUsername()).append("' ");
				sbSet.append(" , end_check_times = ").append(endCheckTiems);
				sbSet.append(" , check_result = ").append(checkResult);
				sbSet.append(" , status =  ").append(status);

				if (this.dynamicCheckCargoBeanDao.updateByCondition(sbSet.toString(), " id = " + dccBean.getId()) <= 0) {
					throw new RuntimeException("保存盘点结果失败");
				}

				// 更新差异明细
				if (status == 3) {
					String setTemp = " difference =  " + difference;
					StringBuilder sbCondition = new StringBuilder();
					sbCondition.append(" cargo_id = ").append(cargoInfo.getId());
					sbCondition.append(" AND product_id = ").append(product.getId());
					sbCondition.append(" AND status = 1 ");

					if (this.dccDifferenceBeanDao.updateByCondition(setTemp, sbCondition.toString()) <= 0) {
						if (difference != 0) {
							DynamicCheckCargoDifferenceBean dccdBean = new DynamicCheckCargoDifferenceBean();
							dccdBean.setAreaId(cargoInfo.getAreaId());
							dccdBean.setCargoId(cargoInfo.getId());
							dccdBean.setCargoWholeCode(cargoInfo.getWholeCode());
							dccdBean.setCargoInfoStockAreaId(cargoInfo.getStockAreaId());
							dccdBean.setProductId(product.getId());
							dccdBean.setProductCode(product.getCode());
							dccdBean.setProductName(product.getName());
							dccdBean.setDifference(difference);
							dccdBean.setStatus(1);

							if (this.dccDifferenceBeanDao.insert(dccdBean) <= 0) {
								throw new RuntimeException("保存差异明细失败");
							}
						}						
					}
				}
			}
		}

		// 盘点中
		if(this.dynamicCheckBeanDao.updateByCondition(" status = 2 " , " id = " + dcBean.getId())<=0){
			throw new RuntimeException("修改盘点单状态失败");
		}
		
	}

	public Json getProductNameAndCargo(String code, String cargo) {
		Json j = new Json();
		ProductBarcodeVO pBarcode = this.productBarcodeDao.getProductBarcode(" barcode = '" + StringUtil.dealParam(code) + "' ");
		if (pBarcode == null) {
			j.setMsg("条码不存在");
			return j;
		}
		List<Map<String, String>> mp = this.productDao.getProductNameAndCargo(" p.id = " + pBarcode.getProductId());
		if (mp == null || mp.isEmpty()) {
			j.setMsg("商品不存在");
			return j;
		}
		Map<String, String> rsp = null;
		for (Map<String, String> p : mp) {
			if (p.get("whole_code").equals(cargo)) {
				rsp = p;
				break;
			}
		}
		if (rsp == null) {
			rsp = mp.get(0);
		}
		j.setObj(rsp);
		j.setSuccess(true);
		return j;
	}

	public List<Map<String, String>> getCargoProduct(String cargo) {
		if (exceptionDao.existExceptionCargo(cargo).intValue() > 0) {
			return this.productDao.getExceptProduct(" dce.cargo_whole_code='"+cargo+"'");
		} else {
			return this.productDao.getCargoProduct(" ci.whole_code='"+cargo+"'");
		}
	}

}
