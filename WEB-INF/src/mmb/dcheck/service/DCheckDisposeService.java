package mmb.dcheck.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmb.common.dao.ProductDao;
import mmb.common.service.CommonService;
import mmb.common.service.MWareService;
import mmb.dcheck.dao.DynamicCheckCargoDifferenceBeanDao;
import mmb.dcheck.model.DynamicCheckCargoDifferenceBean;
import mmb.rec.oper.dao.ProductStockDao;
import mmb.ware.cargo.dao.CargoInfoAreaDao;
import mmb.ware.cargo.dao.CargoInfoDao;
import mmb.ware.cargo.dao.CargoInfoStorageDao;
import mmb.ware.cargo.dao.CargoOperLogDao;
import mmb.ware.cargo.dao.CargoOperationCargoDao;
import mmb.ware.cargo.dao.CargoOperationDao;
import mmb.ware.cargo.dao.CargoOperationLogDao;
import mmb.ware.cargo.dao.CargoOperationProcessDao;
import mmb.ware.cargo.dao.CargoProductStockDao;
import mmb.ware.cargo.dao.CargoStockCardDao;
import mmb.ware.cargo.model.CargoInfo;
import mmb.ware.cargo.model.CargoInfoArea;
import mmb.ware.cargo.model.CargoInfoStorage;
import mmb.ware.cargo.model.CargoOperLog;
import mmb.ware.cargo.model.CargoOperation;
import mmb.ware.cargo.model.CargoOperationCargo;
import mmb.ware.cargo.model.CargoOperationLog;
import mmb.ware.cargo.model.CargoOperationProcess;
import mmb.ware.cargo.model.CargoProductStock;
import mmb.ware.cargo.model.CargoStockCard;
import mmb.ware.stock.dao.BsbyOperationRecordDao;
import mmb.ware.stock.dao.BsbyOperationnoteDao;
import mmb.ware.stock.dao.BsbyProductCargoDao;
import mmb.ware.stock.dao.BsbyProductDao;
import mmb.ware.stock.model.BsbyOperationRecord;
import mmb.ware.stock.model.BsbyOperationnote;
import mmb.ware.stock.model.BsbyProduct;
import mmb.ware.stock.model.BsbyProductCargo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.MyRuntimeException;
import adultadmin.util.StringUtil;

@Service
public class DCheckDisposeService {
	
	@Autowired
	private CargoOperationDao cargoOperationMapper;
	@Autowired
	private DynamicCheckCargoDifferenceBeanDao dynamicCheckCargoDifferenceBeanMapper;
	@Autowired
	private CargoInfoDao cargoInfoMapper;
	@Autowired
	private CargoProductStockDao cargoProductStockMapper;
	@Autowired
	private CargoInfoStorageDao cargoInfoStorageMapper;
	@Autowired
	private MWareService mWareService;
	@Autowired
	private CargoOperationCargoDao cargoOperationCargoMapper;
	@Autowired
	private CargoOperationLogDao cargoOperationLogMapper;
	@Autowired
	private ProductStockDao productStockMapper;
	@Autowired
	private CargoOperLogDao cargoOperLogMapper;
	@Autowired
	private CargoOperationProcessDao cargoOperationProcessMapper;
	@Autowired
	private CargoStockCardDao cargoStockCardMapper;
	@Autowired
	private BsbyOperationnoteDao bsbyOperationnoteMapper;
	@Autowired
	private BsbyProductCargoDao bsbyProductCargoMapper;
	@Autowired
	private ProductDao productMapper;
	@Autowired
	private BsbyOperationRecordDao bsbyOperationRecordMapper;
	@Autowired
	private BsbyProductDao bsbyProductMapper;
	@Autowired
	private CargoInfoAreaDao cargoInfoAreaMapper;
	/**
	 * 返回当前 库存中 因为报损报溢单据有锁定的货位-商品
	 * Map Key: cargoProductStockId
	 * 
	 * @return
	 */
	public Map<Integer, String> getHasBsbyLockInfo() {
		Map<Integer,String > result = new HashMap<Integer,String>();
		List<BsbyOperationnote> bsbyList = bsbyOperationnoteMapper.selectList("type="+BsbyOperationnote.TYPE0+" and current_type="+BsbyOperationnote.audit_sus, -1, -1, null);
		if( bsbyList != null ) {
			for( BsbyOperationnote bo : bsbyList ) {
				List<BsbyProductCargo> bpcList = bsbyProductCargoMapper.selectList("bsby_oper_id="+ bo.getId(), -1, -1, null);
				if( bpcList != null ) {
					for (BsbyProductCargo bpc : bpcList ) {
						result.put(bpc.getCargoProductStockId(), "");
					}
				}
			}
		}
		return result;
	}
	
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=RuntimeException.class)
	public void disposeCurrentDifferenceWithExchange(String area, voUser user) throws ParseException {
		
		int areaId = StringUtil.parstInt(area);
		if( areaId < 0 ) {
			throw new MyRuntimeException("地区信息错误!");
		}
		//首先， 查出所有的 dynamic_check_cargo_difference
		List<DynamicCheckCargoDifferenceBean> moreDifferenceList = dynamicCheckCargoDifferenceBeanMapper.selectList("difference > 0 and  area_id = "+ area +" and status="+ DynamicCheckCargoDifferenceBean.STATUS1, -1, -1, "difference desc");
		List<DynamicCheckCargoDifferenceBean> lessDifferenceList = dynamicCheckCargoDifferenceBeanMapper.selectList("difference < 0 and area_id = "+area+" and status="+ DynamicCheckCargoDifferenceBean.STATUS1, -1, -1, "difference asc");
		List<DynamicCheckCargoDifferenceBean> totalDifferenceList = dynamicCheckCargoDifferenceBeanMapper.selectList("difference <> 0 and area_id = "+area+" and status="+ DynamicCheckCargoDifferenceBean.STATUS1, -1, -1, "difference asc");
		
		Map<Integer, List<DynamicCheckCargoDifferenceBean>> lessMap = new HashMap<Integer, List<DynamicCheckCargoDifferenceBean>>();
		Map<Integer, List<DynamicCheckCargoDifferenceBean>> moreMap = new HashMap<Integer, List<DynamicCheckCargoDifferenceBean>>();
		List<DynamicCheckCargoDifferenceBean> dealList = new ArrayList<DynamicCheckCargoDifferenceBean>();
		//然后根据多少 分成两拨
		Map<Integer,String> unavailMap  = this.getHasBsbyLockInfo();
		if( moreDifferenceList != null && moreDifferenceList.size() != 0 && lessDifferenceList != null && lessDifferenceList.size() != 0) {
			for( DynamicCheckCargoDifferenceBean dccdBean1 : moreDifferenceList ) {
				dccdBean1.setAbsCount(Math.abs(dccdBean1.getDifference()));
				CargoInfo ci = cargoInfoMapper.selectByPrimaryKey(dccdBean1.getCargoId());
				if( ci == null ) {
					throw new MyRuntimeException("有数据找不到对应的货位！");
				}
				//只能是合格库货位
				if( ci.getStockType() != 0 ) {
					continue;
				}
				dccdBean1.setCargoInfo(ci);
				CargoProductStock cps = cargoProductStockMapper.selectByCondition("cargo_id="+dccdBean1.getCargoId() +" and product_id="+ dccdBean1.getProductId());
				if( cps == null ) {
					continue;
				}
				//货位库存上不能有 报损锁定量
				if( unavailMap.containsKey(cps.getId()) ) {
					continue;
				}
				dccdBean1.setCargoProductStock(cps);
				if( cps.getStockCount() < dccdBean1.getDifference() ) {
					 continue;
				}
				if( moreMap.containsKey(dccdBean1.getProductId()) ) {
					List<DynamicCheckCargoDifferenceBean> tempList = moreMap.get(dccdBean1.getProductId() );
					tempList.add(dccdBean1);
				} else {
					List<DynamicCheckCargoDifferenceBean> tempList = new ArrayList<DynamicCheckCargoDifferenceBean>();
					tempList.add(dccdBean1);
					moreMap.put(dccdBean1.getProductId(), tempList);
				}
			}
			for( DynamicCheckCargoDifferenceBean dccdBean2 : lessDifferenceList ) {
				dccdBean2.setAbsCount(Math.abs(dccdBean2.getDifference()));
				CargoInfo ci = cargoInfoMapper.selectByPrimaryKey(dccdBean2.getCargoId());
				if( ci == null ) {
					throw new MyRuntimeException("有数据找不到对应的货位！");
				}
				//货位只能是合格库的
				if( ci.getStockType() != 0 ) {
					continue;
				}
				dccdBean2.setCargoInfo(ci);
				CargoProductStock cps = cargoProductStockMapper.selectByCondition("cargo_id="+dccdBean2.getCargoId() +" and product_id="+ dccdBean2.getProductId());
				if( cps == null ) {
					continue;
				}
				//货位库存上不能有 报损锁定量
				if( unavailMap.containsKey(cps.getId()) ) {
					continue;
				}
				dccdBean2.setCargoProductStock(cps);
				if( lessMap.containsKey(dccdBean2.getProductId()) ) {
					List<DynamicCheckCargoDifferenceBean> tempList = lessMap.get(dccdBean2.getProductId() );
					tempList.add(dccdBean2);
				} else {
					List<DynamicCheckCargoDifferenceBean> tempList = new ArrayList<DynamicCheckCargoDifferenceBean>();
					tempList.add(dccdBean2);
					lessMap.put(dccdBean2.getProductId(), tempList);
				}
			}
		}
		
		//按照是多是少 已经分组完成
		for( DynamicCheckCargoDifferenceBean dccdBean : totalDifferenceList ) {
			Integer productId = dccdBean.getProductId();
			//根据多出的Map来进行遍历
			if( lessMap.get(productId) == null && moreMap.get(productId) == null ) {
				continue;
			} else if (lessMap.get(productId) != null && moreMap.get(productId) == null ) {
				List<DynamicCheckCargoDifferenceBean> lessList = lessMap.get(productId);
				lessMap.put(productId, null);
			} else if (lessMap.get(productId) == null && moreMap.get(productId) != null ) {
				List<DynamicCheckCargoDifferenceBean> moreList = moreMap.get(productId);
				moreMap.put(productId, null);
			} else {
				while (lessMap.get(productId).size() > 0 && moreMap.get(productId).size() > 0) {
					//开始对冲
					//在这时排序吧...
					List<DynamicCheckCargoDifferenceBean> lessList = lessMap.get(productId);
					List<DynamicCheckCargoDifferenceBean> moreList = moreMap.get(productId);
					DynamicCheckCargoDifferenceBean[] moreArray = this.sortMoreOrLessList(moreList);
					DynamicCheckCargoDifferenceBean[] lessArray = this.sortMoreOrLessList(lessList);
					DynamicCheckCargoDifferenceBean lessBean = lessArray[0];
					DynamicCheckCargoDifferenceBean moreBean = moreArray[0];
					// 如果 被调方 大于 需调方， 调入 需调量
					//如果被调方 和 需调方 一样， 刚好
					// 如果被调方，小于需调方量， 全部调走
					int count = this.getHowManyShouldExchange(lessBean, moreBean);
					//注意调拨方向 ，  是需要  是盘点量多了的  需要调来， 盘点量少的  需要调出
					this.generateDeploy3( areaId, user,lessBean.getCargoInfo(), moreBean.getCargoInfo(), lessBean.getCargoProductStock(), moreBean.getCargoProductStock(), productId, count);
					lessBean.setDifference(lessBean.getDifference() + count);
					lessBean.setAbsCount(lessBean.getAbsCount() - count);
					moreBean.setDifference(moreBean.getDifference() - count);
					moreBean.setAbsCount(moreBean.getAbsCount() - count);
					if( lessBean.getAbsCount() == 0) {
						//清除掉 lessBean
						dealList.add(lessBean);
						lessList.remove(lessBean);
						lessMap.put(productId, lessList);
						
					}
					if( moreBean.getAbsCount() == 0 ) {
						dealList.add(moreBean);
						moreList.remove(moreBean);
						moreMap.put(productId, moreList);
					}
				}
				if ( lessMap.get(productId).size() > 0 && moreMap.get(productId).size() == 0 ) {
					//对于还在mapList 之中的  要对 他进行更新数量处理
					List<DynamicCheckCargoDifferenceBean> tempLessList = lessMap.get(productId);
					for( DynamicCheckCargoDifferenceBean temp : tempLessList ) {
						if( dynamicCheckCargoDifferenceBeanMapper.updateByCondition("difference="+temp.getDifference(), "id="+temp.getId()) <= 0 ) {
							throw new MyRuntimeException("修改差异单状态失败！");
						}
					}
					lessMap.put(productId, null);
					continue;
				}
				if ( lessMap.get(productId).size() == 0 && moreMap.get(productId).size() > 0 ) {
					//对于还在mapList 之中的  要对 他进行更新数量处理
					List<DynamicCheckCargoDifferenceBean> tempMoreList = moreMap.get(productId);
					for( DynamicCheckCargoDifferenceBean temp : tempMoreList ) {
						if( dynamicCheckCargoDifferenceBeanMapper.updateByCondition("difference="+temp.getDifference(), "id="+temp.getId()) <= 0 ) {
							throw new MyRuntimeException("修改差异单状态失败！");
						}
					}	
					moreMap.put(productId, null);
					continue;
				}
			}
		}
		//最后 对dealList 里的进行已处理状态修改
		for(  DynamicCheckCargoDifferenceBean temp : dealList ) {
			if( dynamicCheckCargoDifferenceBeanMapper.updateByCondition("status="+DynamicCheckCargoDifferenceBean.STATUS2, "id="+temp.getId()) <= 0 ) {
				throw new MyRuntimeException("修改差异单状态失败！");
			}
		}

	}
	
	/**
	 * 按照差值的绝对值 由大到小排列
	 * @param list
	 * @return
	 */
	private DynamicCheckCargoDifferenceBean[] sortMoreOrLessList(List<DynamicCheckCargoDifferenceBean> list) {
		
		DynamicCheckCargoDifferenceBean[] accps = new DynamicCheckCargoDifferenceBean[list.size()];
		for( int i = 0 ; i < accps.length; i ++ ) {
			accps[i] = list.get(i);
		}
		int x = accps.length;
		for( int i = 0; i < x; i++ ) {
			for( int j = x - 1; j > i; j --) {
				if( accps[j].getAbsCount() > accps[j - 1 ].getAbsCount() ) {
					DynamicCheckCargoDifferenceBean temp = accps[j];
					accps[j] = accps[j-1];
					accps[j-1] = temp;
				}
			}
		}
		return accps;
	}
	
	/**
	 * 得到可以调拨量
	 * @param lessBean
	 * @param moreBean
	 * @return
	 */
	private int getHowManyShouldExchange(
			DynamicCheckCargoDifferenceBean lessBean,
			DynamicCheckCargoDifferenceBean moreBean) {
		int exchangeCount = 0;
		int result = lessBean.getAbsCount() - moreBean.getAbsCount();
		if( result == 0 ) {
			exchangeCount = moreBean.getAbsCount();
		} else if( result > 0 ) {
			exchangeCount = moreBean.getAbsCount();
		} else if( result < 0 ) {
			exchangeCount = lessBean.getAbsCount();
		}
		return exchangeCount;
	}
	
	/**
	 * 生成货位间调拨单的完整流程  除了交接处  有省略
	 * @param dbOp
	 * @param areaId
	 * @param user
	 * @param inCargoInfoBean
	 * @param outCargoInfoBean
	 * @param inCargoProductStockBean
	 * @param outCargoProductStockBean
	 * @param productId
	 * @param count
	 * @return
	 * @throws ParseException 
	 */
	public void generateDeploy3( int areaId, voUser user,
			CargoInfo inCargoInfoBean, CargoInfo outCargoInfoBean, CargoProductStock inCargoProductStockBean, CargoProductStock outCargoProductStockBean, int productId, int count) throws ParseException {
		String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");   
			//String storageCode = wareService.getString("whole_code", "cargo_info", "id = (select cargo_id from cargo_product_stock where id = "+outCargoProductStockBean.getId()+")");
			String storageCode  = outCargoInfoBean.getWholeCode();
			
			storageCode = storageCode.substring(0,storageCode.indexOf("-"));
			CargoInfoStorage storage=cargoInfoStorageMapper.selectCargoInfoStorage("whole_code='"+storageCode+"'");
			if(storage==null){
				throw new MyRuntimeException("地区错误 ！");
			}
			//生成编号
			CargoOperation cargoOper = cargoOperationMapper.selectByCondition("code like '"+code+"%' order by id desc");
			if(cargoOper == null){
				code = code + "00001";
			}else{
				//获取当日计划编号最大值
				String _code = cargoOper.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-5));
				number++;
				code += String.format("%05d",new Object[]{new Integer(number)});
			}
			cargoOper = new CargoOperation();
			cargoOper.setCode(code);
			cargoOper.setCreateDatetime(DateUtil.getNow());
			cargoOper.setCreateUserId(user.getId());
			cargoOper.setCreateUserName(user.getUsername());
			cargoOper.setRemark("货位盘点异常调拨平仓");
			cargoOper.setSource("");
			cargoOper.setStockInType(inCargoInfoBean.getStoreType());
			cargoOper.setStockOutType(outCargoInfoBean.getStoreType());
			cargoOper.setStorageCode(storageCode);
			cargoOper.setType(CargoOperationBean.TYPE3);
			cargoOper.setStockInArea(areaId);
			cargoOper.setStockOutArea(areaId);
			cargoOper.setAuditingUserId(user.getId());
			cargoOper.setAuditingUserName(user.getUsername());
			cargoOper.setAuditingDatetime(DateUtil.getNow());
			cargoOper.setConfirmUserName(user.getUsername());
			cargoOper.setConfirmDatetime(DateUtil.getNow());
			cargoOper.setCompleteUserId(user.getId());
			cargoOper.setCompleteUserName(user.getUsername());
			cargoOper.setCompleteDatetime(DateUtil.getNow());
			cargoOper.setPrintCount(new Integer(0));
			cargoOper.setEffectStatus(new Integer(0));
			cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS28);
			cargoOper.setLastOperateDatetime(DateUtil.getNow());
			if( cargoOperationMapper.insert(cargoOper) <= 0){
				throw new MyRuntimeException("添加补货单失败！");
			}//添加cargo_operation

			int cargoOperId = cargoOper.getId();
			
			//-----
			CargoProductStock bean = outCargoProductStockBean;
			if(bean==null){
				throw new MyRuntimeException("库存错误，无法添加调拨单！");
			}

			CargoInfo ci=outCargoInfoBean;
			CargoOperationCargo coc = new CargoOperationCargo();
			voProduct product1=mWareService.getProduct("id="+bean.getProductId());
			coc.setOperId(cargoOperId);
			coc.setInCargoProductStockId(0);
			coc.setProductId(bean.getProductId());
			coc.setType(1);
			coc.setOutCargoProductStockId(outCargoProductStockBean.getId());
			coc.setOutCargoWholeCode(ci.getWholeCode());
			coc.setStockCount(count);
			coc.setUseStatus(0);
			if( cargoOperationCargoMapper.insert(coc) <= 0 ){
				throw new MyRuntimeException("添加补货单详细信息失败！");
			}
			
			CargoOperationLog logBean1=new CargoOperationLog();
			logBean1.setOperId(cargoOperId);
			logBean1.setOperDatetime(DateUtil.getNow());
			logBean1.setOperAdminId(user.getId());
			logBean1.setOperAdminName(user.getUsername());
			StringBuilder logRemark=new StringBuilder("制单：");
			
			logRemark.append("商品");
			logRemark.append(product1.getCode());
			logRemark.append("，");
			logRemark.append("源货位（");
			logRemark.append(ci.getWholeCode());
			logRemark.append("）");
			
			//分配目的货位
			/*CargoInfoBean inCi = null;
			if(ci.getStoreType() == 0||ci.getStoreType() == 4){
				List inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(), -1, -1, null);
				if(inCpsList.size()>0){
					CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(0);
					CargoInfoBean cargoInfo=cps.getCargoInfo();
					inCi=cargoInfo;
				}
				if(inCi==null){//查询一个未使用的货位
					CargoInfoBean tempInCi=service.getCargoInfo("area_id="+ci.getAreaId()+" and stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and store_type="+ci.getStoreType()+" and status=1");
					if(tempInCi!=null){
						if(!service.updateCargoInfo("status=0", "id="+tempInCi.getId())){
							result = "数据库操作失败！";
							return result;
						}
						CargoProductStockBean newCps=new CargoProductStockBean();
						newCps.setCargoId(tempInCi.getId());
						newCps.setProductId(coc.getProductId());
						newCps.setStockCount(0);
						newCps.setStockLockCount(0);
						if(!service.addCargoProductStock(newCps)){
							result = "数据库操作失败！";
							return result;
						}
						inCi=tempInCi;
					}
				}
			}else if(ci.getStoreType() == 1){
				List inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(),-1,-1,null);
				if(inCpsList.size()>0){
					CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(i);
					CargoInfoBean cargoInfo=cps.getCargoInfo();
					inCi=cargoInfo;
				}
				if(inCi==null){ //查询非此SKU整件区货位
					inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0",-1,-1,null);
					if(inCpsList.size()>0){
						CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(i);
						CargoInfoBean cargoInfo=cps.getCargoInfo();
						inCi=cargoInfo;
						if(cargoInfo.getStatus()!=0){
							if(!service.updateCargoInfo("status=0", "id="+cargoInfo.getId())){
								result = "数据库操作失败！";
								return result;
							}
						}
					}
				}
			}*/
			CargoInfo inCi1 = inCargoInfoBean;
			CargoOperationCargo inCoc1=new CargoOperationCargo();
			inCoc1.setOperId(cargoOperId);
			inCoc1.setProductId(coc.getProductId());
			inCoc1.setOutCargoProductStockId(bean.getId());
			inCoc1.setOutCargoWholeCode(ci.getWholeCode());
			inCoc1.setStockCount(count);
			inCoc1.setType(0);
			inCoc1.setUseStatus(1);
			//CargoProductStockBean inCps=service.getCargoProductStock("cargo_id="+inCi.getId()+" and product_id="+coc.getProductId());
			/*if(inCps==null){//如果该货位没有库存记录，则添加新的库存记录
				inCps=new CargoProductStockBean();
				inCps.setCargoId(inCi.getId());
				inCps.setProductId(coc.getProductId());
				inCps.setStockCount(0);
				inCps.setStockLockCount(0);
				service.addCargoProductStock(inCps);
				inCps.setId(service.getDbOp().getLastInsertId());
			}*/
			inCoc1.setInCargoProductStockId(inCargoProductStockBean.getId());
			inCoc1.setInCargoWholeCode(inCi1.getWholeCode());
			if(cargoOperationCargoMapper.insert(inCoc1) <= 0 ){
				throw new MyRuntimeException("数据库操作失败！");
			}//添加目的货位记录
			
			logRemark.append("，");
			logRemark.append("目的货位（");
			logRemark.append(inCi1.getWholeCode());
			logRemark.append("）");
			

			logBean1.setRemark(logRemark.toString());
			if( cargoOperationLogMapper.insert(logBean1) <= 0 ) {
				throw new MyRuntimeException("数据库操作失败！");
			}
//----
			CargoOperLog operLog1=new CargoOperLog();//员工操作日志
			operLog1.setOperId(cargoOperId);
			operLog1.setOperCode(cargoOper.getCode());
			CargoOperationProcess process1=cargoOperationProcessMapper.selectByCondition("id="+CargoOperationProcessBean.OPERATION_STATUS28);
			operLog1.setOperName(process1.getOperName());
			operLog1.setOperDatetime(DateUtil.getNow());
			operLog1.setOperAdminId(user.getId());
			operLog1.setOperAdminName(user.getUsername());
			operLog1.setHandlerCode("");
			operLog1.setEffectTime(CargoOperLogBean.EFFECT_TIME0);
			operLog1.setRemark("");
			operLog1.setPreStatusName("无");
			operLog1.setNextStatusName(process1.getStatusName());
			if( cargoOperLogMapper.insert(operLog1) <= 0 ){
				throw new MyRuntimeException("添加日志数据时发生异常！");
			}
			//---添加结束   该加 确认的地方了
			cargoOper.setId(cargoOperId);
			
			if(cargoOperationMapper.updateByCondition(
					"status = "+CargoOperationProcessBean.OPERATION_STATUS29+"," +
							"effect_status=0,last_operate_datetime='"+DateUtil.getNow()+"'," +
									"confirm_datetime = '"+DateUtil.getNow()+"'," +
											"confirm_user_name = '"+user.getUsername()+"'", "id = "+cargoOper.getId()) <= 0){
				throw new MyRuntimeException("更新调拨单状态失败！");
			}
			
			//----------------这里把锁货位库存的地方全给省了
			
			//修改上一操作日志的时效
			CargoOperLog lastLog1=cargoOperLogMapper.selectByCondition("oper_id="+cargoOper.getId()+" order by id desc");//当前作业单的最后一条日志
			if(lastLog1!=null&&lastLog1.getEffectTime()==0){//如果不是进行中，不需要再改时效
				CargoOperationProcess tempProcess=cargoOperationProcessMapper.selectByCondition("id="+cargoOper.getStatus());//生成作业单
				int effectTime=tempProcess.getEffectTime();//生成阶段时效
				String lastOperateTime=lastLog1.getOperDatetime();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long date1=sdf.parse(lastOperateTime).getTime();
				long date2=sdf.parse(DateUtil.getNow()).getTime();
				if(date1+effectTime*60*1000<date2){//已超时
					cargoOperLogMapper.updateByCondition("effect_time=1", "id="+lastLog1.getId());
				}
			}

			CargoOperLog operLog2=new CargoOperLog();
			operLog2.setOperId(cargoOper.getId());
			operLog2.setOperCode(cargoOper.getCode());
			CargoOperationProcess process3=cargoOperationProcessMapper.selectByCondition("id="+CargoOperationProcessBean.OPERATION_STATUS28);
			CargoOperationProcess process4=cargoOperationProcessMapper.selectByCondition("id="+CargoOperationProcessBean.OPERATION_STATUS29);
			operLog2.setOperName(process4.getOperName());
			operLog2.setOperDatetime(DateUtil.getNow());
			operLog2.setOperAdminId(user.getId());
			operLog2.setOperAdminName(user.getUsername());
			operLog2.setHandlerCode("");
			operLog2.setEffectTime(0);
			operLog2.setRemark("");
			operLog2.setPreStatusName(process3.getStatusName());
			operLog2.setNextStatusName(process4.getStatusName());
			cargoOperLogMapper.insert(operLog2);

			
		   // 确认阶段完成 
			
			// 交接阶段
			//		int status = StringUtil.toInt(request.getParameter("status"));
			//		String remark = StringUtil.convertNull(request.getParameter("remark")).trim();
			int nextStatus=CargoOperationProcessBean.OPERATION_STATUS30;//下一个状态
				CargoOperation cargoOperation1 = cargoOperationMapper.selectByCondition("id = "+cargoOper.getId());
				if(cargoOperation1 == null){
					throw new MyRuntimeException("该作业单不存在！");
				}
				if(cargoOperation1.getStatus()>=nextStatus){
					throw new MyRuntimeException("该作业单状态已被更新，操作失败！");
				}
				CargoOperationLog logBean = new CargoOperationLog();
				logBean.setOperId(cargoOper.getId());
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());

				CargoOperationProcess process5=cargoOperationProcessMapper.selectByCondition("id="+cargoOperation1.getStatus());//当前阶段
				CargoOperationProcess process6=cargoOperationProcessMapper.selectByCondition("id="+nextStatus);//下个阶段
				if(process5==null){
					throw new MyRuntimeException("作业单流程信息错误！");
				}
				if(process6==null){
					throw new MyRuntimeException("作业单流程信息错误！");
				}
				int handleType=process6.getHandleType();//操作方式，0人工确认，1设备确认
				if(handleType!=0){
					throw new MyRuntimeException("当前操作方式为设备确认！");
				}
//				int confirmType=process2.getConfirmType();//作业判断，0不做判断，1源货位，2目的货位，人工确认不需要判断该条件
//				int deptId1=process2.getDeptId1();//职能归属，一级部门，人工确认不需要判断该条件
//				int deptId2=process2.getDeptId2();//职能归属，二级部门，人工确认不需要判断该条件
//				int storageId=process2.getStorageId();//所属仓库，人工确认不需要判断该条件

				//修改上一操作日志的时效
				CargoOperLog lastLog2=cargoOperLogMapper.selectByCondition("oper_id="+cargoOper.getId()+" order by id desc");//当前作业单的最后一条日志
				if(lastLog2!=null&&lastLog2.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process5.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog2.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						cargoOperLogMapper.updateByCondition("effect_time=1", "id="+lastLog2.getId());
					}
				}

				if(cargoOperationMapper.updateByCondition(
						"status="+nextStatus+",effect_status = 0," +
								"last_operate_datetime='"+DateUtil.getNow()+"'"+(
										cargoOperation1.getStatus()==CargoOperationProcessBean.OPERATION_STATUS29?("," +
												"auditing_datetime='"+DateUtil.getNow()+"'," +
														"auditing_user_id="+user.getId()+"," +
																"auditing_user_name='"+user.getUsername()+"'"):""), "id="+cargoOper.getId()) <= 0 ){
					throw new MyRuntimeException("更新调拨单状态失败！");
				}

				CargoOperLog operLog3=new CargoOperLog();
				operLog3.setOperId(cargoOper.getId());
				operLog3.setOperCode(cargoOperation1.getCode());
				operLog3.setOperName(process6.getOperName());
				operLog3.setOperDatetime(DateUtil.getNow());
				operLog3.setOperAdminId(user.getId());
				operLog3.setOperAdminName(user.getUsername());
				operLog3.setHandlerCode("");
				operLog3.setEffectTime(0);
				operLog3.setRemark("");
				operLog3.setPreStatusName(process5.getStatusName());
				operLog3.setNextStatusName(process6.getStatusName());
				cargoOperLogMapper.insert(operLog3);
			//交接阶段完成
			
			//完成阶段

				CargoOperation cargoOperation = cargoOperationMapper.selectByCondition("id = "+cargoOper.getId());
				if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS34){
					throw new MyRuntimeException("该作业单状态已被更新，操作失败！");
				}
				//完成货位库存量操作
				List<CargoOperationCargo> outCocList = cargoOperationCargoMapper.selectList("oper_id = "+cargoOperation.getId()+" and type = 1", -1, -1, "id asc");
				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargo outCoc = (CargoOperationCargo)outCocList.get(i);
					CargoProductStock outCps = cargoProductStockMapper.selectByCondition("id = "+outCoc.getOutCargoProductStockId());
					CargoInfo outCi = cargoInfoMapper.selectByCondition("id = "+outCps.getCargoId());
					voProduct product = mWareService.getProduct("id="+outCoc.getProductId());
					Map<String,String> paramMap = CommonService.constructSelectMap("product_id = "+product.getId(), -1, -1, "id asc");
					product.setPsList(productStockMapper.getProductStockList(paramMap));
					int stockOutCount = 0;
					List inCocList = cargoOperationCargoMapper.selectList("oper_id = "+cargoOperation.getId()+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<inCocList.size();j++){
						CargoOperationCargo inCoc = (CargoOperationCargo)inCocList.get(j);
						CargoProductStock inCps = cargoProductStockMapper.selectByCondition("id = "+inCoc.getInCargoProductStockId());
						CargoInfo inCi = cargoInfoMapper.selectByCondition("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
						//因为冻结的步骤 被省略了 所以这里不是从锁定量来加了
						if(inCps!=null&&outCps!=null){
							if(!cargoProductStockMapper.updateCargoProductStockCount(inCps.getId(), inCoc.getStockCount())){
								throw new MyRuntimeException("操作失败，货位库存不足！");
							}
							if(!cargoProductStockMapper.updateCargoProductStockCount(outCps.getId(), -inCoc.getStockCount())){
								throw new MyRuntimeException("操作失败，货位库存不足！");
							}

							//调整合格库库存，修改批次，添加进销存卡片
							/*if(inCi.getAreaId()!=outCi.getAreaId()){
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
								ProductStockBean inProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
								if(inProductStock==null){
									result = "合格库库存数据错误！";
									return result;
								}
								if (!psService.updateProductStockCount(inProductStock.getId(),inCoc.getStockCount())) {
									result = "库存操作失败，可能是库存不足，请与管理员联系！";
									return result;
								}
								productStockCount+=inCoc.getStockCount();
								
								ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
								ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
								//批次修改开始
								//更新批次记录、添加调拨出、入库批次记录
								List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId(), -1, -1, "id asc");
								double stockinPrice = 0;
								double stockoutPrice = 0;
								if(sbList!=null&&sbList.size()!=0){
									int stockExchangeCount = inCoc.getStockCount();
									int index = 0;
									int stockBatchCount = 0;
									
									do {
										//出库
										StockBatchBean batch = (StockBatchBean)sbList.get(index);
										if(stockExchangeCount>=batch.getBatchCount()){
											if(!stockService.deleteStockBatch("id="+batch.getId())){
												result = "数据库操作失败！";
								               return result;
											}
											stockBatchCount = batch.getBatchCount();
										}else{
											if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
												result =  "数据库操作失败！";
								                return result;
											}
											stockBatchCount = stockExchangeCount;
										}
										
										//添加批次操作记录
										StockBatchLogBean batchLog = new StockBatchLogBean();
										batchLog.setCode(cargoOperation.getCode());
										batchLog.setStockType(batch.getStockType());
										batchLog.setStockArea(batch.getStockArea());
										batchLog.setBatchCode(batch.getCode());
										batchLog.setBatchCount(stockBatchCount);
										batchLog.setBatchPrice(batch.getPrice());
										batchLog.setProductId(batch.getProductId());
										batchLog.setRemark("调拨出库");
										batchLog.setCreateDatetime(DateUtil.getNow());
										batchLog.setUserId(user.getId());
										if(!stockService.addStockBatchLog(batchLog)){
											 result = "添加失败！";
								             return result;
										}
										
										stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
										
										//入库
										StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
										if(batchBean!=null){
											if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
												result = "数据库操作失败！";
								                return result;
											}
										}else{
											int ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
											StockBatchBean newBatch = new StockBatchBean();
											newBatch.setCode(batch.getCode());
											newBatch.setProductId(batch.getProductId());
											newBatch.setPrice(batch.getPrice());
											newBatch.setBatchCount(stockBatchCount);
											newBatch.setProductStockId(psIn.getId());
											newBatch.setStockArea(inCi.getAreaId());
											newBatch.setStockType(psIn.getType());
											newBatch.setTicket(ticket);
											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
											if(!stockService.addStockBatch(newBatch)){
												result = "添加失败！";
												return result;
											}
										}
										
										//添加批次操作记录
										batchLog = new StockBatchLogBean();
										batchLog.setCode(cargoOperation.getCode());
										batchLog.setStockType(psIn.getType());
										batchLog.setStockArea(inCi.getAreaId());
										batchLog.setBatchCode(batch.getCode());
										batchLog.setBatchCount(stockBatchCount);
										batchLog.setBatchPrice(batch.getPrice());
										batchLog.setProductId(batch.getProductId());
										batchLog.setRemark("调拨入库");
										batchLog.setCreateDatetime(DateUtil.getNow());
										batchLog.setUserId(user.getId());
										if(!stockService.addStockBatchLog(batchLog)){
											result =  "添加失败！";
											return result;
										}
										
										stockExchangeCount -= batch.getBatchCount();
										index++;
										
										stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
									} while (stockExchangeCount>0&&index<sbList.size());
								}
								//批次修改结束
								
								//添加进销存卡片开始
								// 入库卡片
								StockCardBean sc = new StockCardBean();
								sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
								sc.setCode(cargoOperation.getCode());

								sc.setCreateDatetime(DateUtil.getNow());
								sc.setStockType(inCi.getStockType());
								sc.setStockArea(inCi.getAreaId());
								sc.setProductId(inCps.getProductId());
								sc.setStockId(psIn.getId());
								sc.setStockInCount(inCoc.getStockCount());
								sc.setStockInPriceSum(0);

								sc.setCurrentStock(product.getStock(inCi.getAreaId(), sc.getStockType())
										+ product.getLockCount(inCi.getStockAreaId(), sc.getStockType()));
								sc.setStockAllArea(product.getStock(inCi.getAreaId())
										+ product.getLockCount(inCi.getAreaId()));
								sc.setStockAllType(product.getStockAllType(sc.getStockType())
										+ product.getLockCountAllType(sc.getStockType()));
								sc.setAllStock(product.getStockAll() + product.getLockCountAll());
								sc.setStockPrice(product.getPrice5());// 新的库存价格
								sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
										new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
								psService.addStockCard(sc);
								
								// 出库卡片
								StockCardBean sc2 = new StockCardBean();
								int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
								sc2.setId(scId);

								sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
								sc2.setCode(cargoOperation.getCode());

								sc2.setCreateDatetime(DateUtil.getNow());
								sc2.setStockType(outCi.getStockType());
								sc2.setStockArea(outCi.getAreaId());
								sc2.setProductId(product.getId());
								sc2.setStockId(psOut.getId());
								sc2.setStockOutCount(inCoc.getStockCount());
//								sc2.setStockOutPriceSum(stockOutPrice);
								sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								sc2.setCurrentStock(product.getStock(outCi.getAreaId(), sc2.getStockType())
										+ product.getLockCount(outCi.getAreaId(), sc2.getStockType()));
								sc2.setStockAllArea(product.getStock(outCi.getAreaId())
										+ product.getLockCount(outCi.getAreaId()));
								sc2.setStockAllType(product.getStockAllType(sc2.getStockType())
										+ product.getLockCountAllType(sc2.getStockType()));
								sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
								sc2.setStockPrice(product.getPrice5());
								sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
										new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
								psService.addStockCard(sc2);
								//添加进销存卡片结束
								
							}*/

							//货位入库卡片
							inCps = cargoProductStockMapper.selectByCondition("id = "+inCps.getId());
							CargoStockCard csc = new CargoStockCard();
							csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKIN);
							csc.setCode(cargoOperation.getCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(inCi.getStockType());
							csc.setStockArea(inCi.getAreaId());
							csc.setProductId(product.getId());
							csc.setStockId(inCps.getId());
							csc.setStockInCount(inCoc.getStockCount());
							csc.setStockOutCount(0);
							csc.setStockOutPriceSum(0d);
							csc.setStockInPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(inCps.getStockCount());
							csc.setCargoStoreType(inCi.getStoreType());
							csc.setCargoWholeCode(inCi.getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							if( cargoStockCardMapper.insert(csc) <= 0 ) {
								throw new MyRuntimeException("添加货位入库库存卡片失败！");
							}

							stockOutCount = stockOutCount + inCoc.getStockCount();
						}else{
							throw new MyRuntimeException("库存错误，无法提交！");
						}

						/*if(outCi.getAreaId()!=inCi.getAreaId()){
							//更新订单缺货状态
							this.updateLackOrder(outCoc.getProductId());
						}*/
					}

					//调整合格库库存
					/*CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
					ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
					if(outProductStock==null){
						result = "合格库库存数据错误！";
						return result;
					}
					if (!psService.updateProductLockCount(outProductStock.getId(),-productStockCount)) {
						result = "库存操作失败，可能是库存不足，请与管理员联系！";
						return result;
					}*/

					//货位出库卡片
					outCps = cargoProductStockMapper.selectByCondition("id = "+outCps.getId());
					CargoStockCard csc = new CargoStockCard();
					csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKOUT);
					csc.setCode(cargoOperation.getCode());
					csc.setCreateDatetime(DateUtil.getNow());
					csc.setStockType(outCi.getStockType());
					csc.setStockArea(outCi.getAreaId());
					csc.setProductId(product.getId());
					csc.setStockId(outCps.getId());
					csc.setStockOutCount(stockOutCount);
					csc.setStockInCount(0);
					csc.setStockInPriceSum(0d);
					csc.setStockOutPriceSum((new BigDecimal(stockOutCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
					csc.setAllStock(product.getStockAll() + product.getLockCountAll());
					csc.setCurrentCargoStock(outCps.getStockCount());
					csc.setCargoStoreType(outCi.getStoreType());
					csc.setCargoWholeCode(outCi.getWholeCode());
					csc.setStockPrice(product.getPrice5());
					csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
					if( cargoStockCardMapper.insert(csc) <= 0 ) {
						throw new MyRuntimeException("添加货位出库库存卡片失败！");
					}
				}	
				if(cargoOperationMapper.updateByCondition(
						"status="+CargoOperationProcessBean.OPERATION_STATUS34+"," +
								"effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"," +
										"complete_datetime='"+DateUtil.getNow()+"'," +
												"complete_user_id="+user.getId()+"," +
														"complete_user_name='"+user.getUsername()+"'", "id="+cargoOperation.getId()) <= 0){
					throw new MyRuntimeException("更新调拨单状态失败！");
				}

				CargoOperationProcess process=cargoOperationProcessMapper.selectByCondition("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcess process2=cargoOperationProcessMapper.selectByCondition("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
				if(process==null){
					throw new MyRuntimeException("作业单流程信息错误！");
				}
				if(process2==null){
					throw new MyRuntimeException("作业单流程信息错误！");
				}

				//修改上一操作日志的时效
				CargoOperLog lastLog5=cargoOperLogMapper.selectByCondition("oper_id="+cargoOperation.getId()+" order by id desc");//当前作业单的最后一条日志
				if(lastLog5!=null&&lastLog5.getEffectTime()==1){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog5.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						cargoOperLogMapper.updateByCondition("effect_time=2", "id="+lastLog5.getId());
					}
				}

				CargoOperLog operLog5=new CargoOperLog();
				operLog5.setOperId(cargoOperation.getId());
				operLog5.setOperCode(cargoOperation.getCode());
				operLog5.setOperName(process2.getOperName());
				operLog5.setOperDatetime(DateUtil.getNow());
				operLog5.setOperAdminId(user.getId());
				operLog5.setOperAdminName(user.getUsername());
				operLog5.setHandlerCode("");
				operLog5.setEffectTime(2);
				operLog5.setRemark("");
				operLog5.setPreStatusName(process.getStatusName());
				operLog5.setNextStatusName(process2.getStatusName());
				cargoOperLogMapper.insert(operLog5);
				
				//复核s
				//作业成功
				
				if( cargoOperationMapper.updateByCondition(
						"effect_status="+CargoOperationBean.EFFECT_STATUS3+"," +
								"last_operate_datetime='"+DateUtil.getNow()+"'", "id="+cargoOper.getId()) <= 0 ){
					throw new MyRuntimeException("更新调拨单状态失败！");
				}
				
				CargoOperationProcess process7=cargoOperationProcessMapper.selectByCondition("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcess process8=cargoOperationProcessMapper.selectByCondition("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
				if(process7==null){
					throw new MyRuntimeException("作业单流程信息错误！");
				}
				if(process8==null){
					throw new MyRuntimeException("作业单流程信息错误！");
				}

				//修改上一操作日志的时效
				CargoOperLog lastLog=cargoOperLogMapper.selectByCondition("oper_id="+cargoOper.getId()+" order by id desc");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process7.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						cargoOperLogMapper.updateByCondition("effect_time=1", "id="+lastLog.getId());
					}
				}

				CargoOperLog operLog=new CargoOperLog();
				operLog.setOperId(cargoOper.getId());
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName("作业复核");
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(3);
				operLog.setRemark("货位盘点调拨单");
				operLog.setPreStatusName(process7.getStatusName());
				operLog.setNextStatusName(process8.getStatusName());
				cargoOperLogMapper.insert(operLog);

	}

	/**
	 * 生成报损报溢功能
	 * aohaichen
	 * 2014-8-1
	 * @throws ParseException 
	 */
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=RuntimeException.class)
	public void disposeCurrentDifferenceWithBsby(String area, voUser user) throws ParseException {
		//this.disposeCurrentDifferenceWithExchange(area, user);
		List<DynamicCheckCargoDifferenceBean> differenceList = dynamicCheckCargoDifferenceBeanMapper.selectList("status="+ DynamicCheckCargoDifferenceBean.STATUS1, -1, -1, "difference desc");
		Map<Integer,String> unavailMap = this.getHasBsbyLockInfo();
		for(int i = 0 ; i < differenceList.size(); i++){
			DynamicCheckCargoDifferenceBean dcdb =differenceList.get(i);
			int productId = dcdb.getProductId();
			String cargoCode = dcdb.getCargoWholeCode();
			int count = dcdb.getDifference();
			int cargoId = dcdb.getCargoId();
			int wareType = 0;
			CargoInfo cib =cargoInfoMapper.selectByCondition("id= "+cargoId);
			CargoProductStock cps = cargoProductStockMapper.selectByCondition("cargo_id="+dcdb.getCargoId() + " and product_id="+dcdb.getProductId());
			if( cps == null ) {
				continue;
			}
			if( unavailMap.containsKey(cps.getId()) ) {
				continue;
			} else {
				if( cps.getStockCount() + count < 0 ) {
					continue;
				}
				if(cib.getType()== ProductStockBean.STOCKTYPE_QUALIFIED && cib.getStoreType()!= CargoInfoBean.STORE_TYPE2 ){//获取库类型是合格库
					this.addBsByOperationnote(user, cargoId,area, wareType, productId, cargoCode, count);
					dynamicCheckCargoDifferenceBeanMapper.updateByCondition("status ="+DynamicCheckCargoDifferenceBean.STATUS2, dcdb.getId()+"");
				}else{
					continue;
				}
			}
		}
	}
	@Transactional(propagation=Propagation.REQUIRED,rollbackFor=RuntimeException.class)
	public void addBsByOperationnote(voUser user,int cargoId,String wareArea,int wareType, int productId, String cargoCode, int count ) {
				
		voProduct product = productMapper.getProduct("id= "+productId);			
		if (product == null) {
			throw new MyRuntimeException("有商品不存在!");
		}
		if (product.getParentId1() == 106) {
			throw new MyRuntimeException("[" + product.getCode()+ "]该商品为新商品,请先修改该产品的分类");
		}
		if (product.getIsPackage() == 1) {
			throw new MyRuntimeException("[" + product.getCode()+ "]该产品为套装产品,不能添加!");
		}
		
		String receipts_number = "";
		String title = "";// 日志的内容
		int typeString = 0;
		int bsbyId = -1;
		if (count < 0) {
			// 报损
			String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code, bsbyOperationnoteMapper);// BS+年月日+3位自动增长数
			title = "创建新的报损表" + receipts_number;
			typeString = 0;
		} else if(count > 0) {
			String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code,bsbyOperationnoteMapper);// BY+年月日+3位自动增长数
			title = "创建新的报溢表" + receipts_number;
			typeString = 1;
		}
		String nowTime = DateUtil.getNow();

			BsbyOperationnote bsbyOperationnote = new BsbyOperationnote();
			bsbyOperationnote.setAddTime(nowTime);
			bsbyOperationnote.setCurrentType(0);
			bsbyOperationnote.setOperatorId(user.getId());
			bsbyOperationnote.setOperatorName(user.getUsername());
			bsbyOperationnote.setReceiptsNumber(receipts_number);
			bsbyOperationnote.setWarehouseArea(Integer.parseInt(wareArea));
			bsbyOperationnote.setWarehouseType(wareType);
			bsbyOperationnote.setType(typeString);
			bsbyOperationnote.setIfDel(0);
			bsbyOperationnote.setFinAuditId(0);
			bsbyOperationnote.setFinAuditName("");
			bsbyOperationnote.setFinAuditRemark("");
			bsbyOperationnote.setRemark("动态货位盘点功能");
			
			if (bsbyOperationnoteMapper.insert(bsbyOperationnote)>0) {
				bsbyId = Integer.valueOf(bsbyOperationnote.getId());
				// 添加操作日志
				BsbyOperationRecord bsbyOperationRecord = new BsbyOperationRecord();
				bsbyOperationRecord.setOperatorId(user.getId());
				bsbyOperationRecord.setOperatorName(user.getUsername());
				bsbyOperationRecord.setTime(nowTime);
				bsbyOperationRecord.setInformation(title);
				bsbyOperationRecord.setOperationId(bsbyOperationnote.getId());
				if(bsbyOperationRecordMapper.insert(bsbyOperationRecord)<=0){
					throw new MyRuntimeException("添加报损报溢日志失败!");
				}
			}else{
				throw new MyRuntimeException("添加报损报溢表失败！");
			}
			
			//-----------------加商品
			
			BsbyOperationnote bean = bsbyOperationnoteMapper.selectByCondition("id = " + bsbyId);
			if(bean.getCurrentType()!=BsbyOperationnoteBean.dispose && bean.getCurrentType()!=BsbyOperationnoteBean.audit_Fail){			

		
				throw new MyRuntimeException("单据已提交审核,无法修改!");
			}
			
			if (bsbyProductMapper.selectByCondition("operation_id = " + bsbyId + " and product_code = " + product.getCode()) != null) {
				throw new MyRuntimeException("[" + product.getCode()+ "]该产品已经添加,直接修改即可,不用重复添加!");
			}
			if (bsbyProductMapper.selectByCondition("operation_id = " + bsbyId ) != null) {
				throw new MyRuntimeException("只能添加一个商品 所以不能重复提交!");
			}
			BsbyOperationnote ben = bsbyOperationnoteMapper.selectByCondition("id=" + bsbyId);
			int x = getProductCount(product.getId(), ben.getWarehouseArea(), ben.getWarehouseType());
			int n = updateProductCount(x, ben.getType(), count);
			if (n < 0 ) {
				throw new MyRuntimeException("您所添加商品的库存不足!");
			}
			
			//新货位管理判断
			CargoProductStock cps = null;
			if(ben.getType()==0){
				CargoInfoArea outCargoArea = cargoInfoAreaMapper.selectCargoInfoArea("old_id = "+ben.getWarehouseArea());
				
		       // List cpsOutList = cargoInfoMapper.getCargoAndProductStockList("ci.stock_type = "+ben.getWarehouseType()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
				List< CargoProductStock> cpsOutList = new ArrayList< CargoProductStock>();
				CargoInfo ci = cargoInfoMapper.selectByCondition("stock_type = "+ben.getWarehouseType()+" and area_id = "+outCargoArea.getId()+" and whole_code = '"+cargoCode+"'");
		        cpsOutList = cargoProductStockMapper.selectList("cargo_id="+ci.getId(), -1, -1, "id asc");
				/*for(int i = 0 ; i < ciList.size() ; i++){
		        	CargoInfo ci =ciList.get(i);
		        	cps =cargoProductStockMapper.selectByCondition("cargo_id = "+ci.getId()+" and product_id = "+product.getId());
		        	cpsOutList.add(cps);
		        }*/
		        
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	throw new MyRuntimeException("货位号"+cargoCode+"无效,请重新输入!");
		        }
		        cps = (CargoProductStock)cpsOutList.get(0);
		        if(ben.getWarehouseType() == ProductStockBean.STOCKTYPE_QUALIFIED && ci.getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	throw new MyRuntimeException("合格库缓存区暂时不能进行报损报溢操作!");
		        }
		        if(count > cps.getStockCount()){
		        	throw new MyRuntimeException("该货位"+cargoCode+"库存为" + cps.getStockCount() + ",库存不足!");
		        }
			}else{
				CargoInfoArea inCargoArea = cargoInfoAreaMapper.selectCargoInfoArea("old_id = "+ben.getWarehouseArea());
				CargoInfo cargo = cargoInfoMapper.selectByCondition("stock_type = "+ben.getWarehouseType()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+cargoCode+"' and status <> "+CargoInfoBean.STATUS3);
		        if(cargo == null){
		        	throw new MyRuntimeException("货位号"+cargoCode+"无效,请重新输入!");
		        }
		        if(cargo.getStatus() == CargoInfoBean.STATUS2){
		        	throw new MyRuntimeException("货位"+cargoCode+"未开通,请重新输入!");
		        }
		        if(ben.getWarehouseType() == ProductStockBean.STOCKTYPE_QUALIFIED && cargo.getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	throw new MyRuntimeException("合格库缓存区暂时不能进行报损报溢操作!");
		        }
				List cpsOutList = cargoProductStockMapper.selectList("product_id = "+product.getId()+" and cargo_id = "+cargo.getId(), -1, -1, null);
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	if(cargo.getStatus() == CargoInfoBean.STATUS0 && (cargo.getStoreType() == CargoInfoBean.STORE_TYPE0||cargo.getStoreType() == CargoInfoBean.STORE_TYPE4)){
		        		throw new MyRuntimeException("货位"+cargoCode+"被其他商品使用中,添加失败!");
		        	}
		        	cps = new CargoProductStock();
		        	cps.setCargoId(cargo.getId());
		        	cps.setProductId(product.getId());
		        	cps.setStockCount(0);
		        	cps.setStockLockCount(0);
		        	if(cargoProductStockMapper.insert(cps)<=0){
		        		throw new MyRuntimeException("生成报损报溢单数据库异常!(cargoService.addCargoProductStock(cps))");
		        	}
		        	
		        	if(cargoInfoMapper.updateByCondition("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId())<=0){
		        		throw new MyRuntimeException("生成报损报溢单数据库异常!(cargoService.updateCargoInfo())");
		        	}
		        }else{
		        	cps = (CargoProductStock)cpsOutList.get(0);
		        }
			}
			BsbyProduct bsbyProduct1 = new BsbyProduct();
			bsbyProduct1.setBsbyCount(Math.abs(count));
			bsbyProduct1.setBsbyPrice(0f);
			bsbyProduct1.setOperationId(bsbyId);
			bsbyProduct1.setProductCode(product.getCode());
			bsbyProduct1.setProductId(product.getId());
			bsbyProduct1.setProductName(product.getName());
			bsbyProduct1.setOriname(product.getOriname());
			bsbyProduct1.setAfterChange(n);
			bsbyProduct1.setBeforeChange(x);
			if(bsbyProductMapper.insert(bsbyProduct1)>0) {
			}else{
				throw new MyRuntimeException("商品添加失败!");
			}
			BsbyProductCargo bsbyCargo1 = new BsbyProductCargo();
			bsbyCargo1.setBsbyOperId(ben.getId());
			bsbyCargo1.setBsbyProductId(bsbyProduct1.getId());
			bsbyCargo1.setCount(Math.abs(count));
			bsbyCargo1.setCargoProductStockId(cps.getId());
			bsbyCargo1.setCargoId(cps.getCargoId());
			bsbyProductCargoMapper.insert(bsbyCargo1);
			// 添加日志
			BsbyOperationRecord bsbyOperationRecord = new BsbyOperationRecord();
			bsbyOperationRecord.setOperatorId(user.getId());
			bsbyOperationRecord.setOperatorName(user.getUsername());
			bsbyOperationRecord.setTime(DateUtil.getNow());
			bsbyOperationRecord.setInformation("给单据(id):" + bsbyId+ "添加商品:" + product.getCode() + "数量：" + Math.abs(count));
			bsbyOperationRecord.setOperationId(bsbyId);
			if(bsbyOperationRecordMapper.insert(bsbyOperationRecord)<=0){
				throw new MyRuntimeException("日志添加失败!");
			}
			//-------------------审核
			BsbyOperationnote bean2 = bsbyOperationnoteMapper.selectByCondition("id=" + bsbyId);
			if(bean2 == null){
				throw new MyRuntimeException("报损报溢单不存在!");
			}
			//报损单中的所有产品
			List bsbyList = bsbyProductMapper.selectList("operation_id=" + bean2.getId(), -1, -1, "id asc");
			Iterator it = bsbyList.iterator();
			if(bean2.getType() == 0){
				for (; it.hasNext();) {
					BsbyProduct bsbyProduct = (BsbyProduct) it.next();
					BsbyProductCargo bsbyCargo = bsbyProductCargoMapper.selectByCondition("bsby_product_id = "+bsbyProduct.getId());
					if(bsbyCargo == null){
						throw new MyRuntimeException("货位信息异常,操作失败,请与管理员联系!");
					}
					String sql = "product_id = " + bsbyProduct.getProductId() + " and "
					+ "area = " + bean2.getWarehouseArea() + " and type = "
					+ bean2.getWarehouseType();
					ProductStockBean psBean = productStockMapper.getProductStock(sql);
					//减少库存
					if(!productStockMapper.updateStockCount(psBean.getId(), -bsbyProduct.getBsbyCount())){
						throw new MyRuntimeException("库存操作失败,可能是库存不足,请与管理员联系!");
					}
					//增加库存锁定量
					if (!productStockMapper.updateStockLockCount(psBean.getId(), bsbyProduct.getBsbyCount())) {
						throw new MyRuntimeException("库存操作失败,可能是库存不足,请与管理员联系!");
					}

					//锁定货位库存
					//出库
					if(!cargoProductStockMapper.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
						throw new MyRuntimeException("货位库存操作失败,货位库存不足!");
					}
					if(!cargoProductStockMapper.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
						throw new MyRuntimeException("货位库存操作失败，货位库存不足!");
					}
				}
			}else if(bean2.getType() == 1){
				for (; it.hasNext();) {
					BsbyProduct bsbyProductBean = (BsbyProduct) it.next();
					BsbyProductCargo bsbyCargo = bsbyProductCargoMapper.selectByCondition("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						throw new MyRuntimeException("货位信息异常,操作失败,请与管理员联系!");
					}

					//锁定货位空间
					if(cargoInfoMapper.selectByCondition("id = "+bsbyCargo.getCargoId()+" and status = 0")==null){
						throw new MyRuntimeException("目的货位不存在或已被清空，操作失败，请与管理员联系!");
					}
					if(cargoInfoMapper.updateByCondition("space_lock_count = space_lock_count + "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())<=0){
						throw new MyRuntimeException("操作失败!");
					}
				}
			}
			if(bsbyOperationnoteMapper.updateByCondition(" current_type=1", " id=" + bsbyId)<=0){
				throw new MyRuntimeException("更新报损报溢单状态时失败!");
			}
	}
	
	public String createCode(String code, BsbyOperationnoteDao bsbyOperationnoteMapper) {
		BsbyOperationnote plan;
		plan = bsbyOperationnoteMapper.selectByCondition("receipts_number like '" + code + "%'");
		if (plan == null) {
			// 当日第一份计划,编号最后三位 001
			code += "0001";
		} else {
			int maxid = bsbyOperationnoteMapper.selectMaxCount("id > 0 and receipts_number like '" + code + "%'");
			// 获取当日计划编号最大值
			plan = bsbyOperationnoteMapper.selectByCondition("id =" + maxid);
			String _code = plan.getReceiptsNumber();
			int number = Integer.parseInt(_code.substring(_code.length() - 4));
			number++;
			code += String.format("%04d", new Object[] { new Integer(number) });
		}
		return code;
	}
	/**
	 * 	说明：1.根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 
	 * 
	 * 	日期：2013-04-17
	 * 
	 * 	作者：石远飞
	 * 
	 * 	修改：aohaichen 2014-8-4
	 */
	public int getProductCount(int productid, int area, int type) {

		int x = 0;
		voProduct product = productMapper.getProduct(productid+"");	
		Map<String,String> map = new HashMap<String,String>();
		map.put("condition", "product_id=" + productid);
		map.put("index", "-1");
		map.put("count", "-1");
		product.setPsList(productStockMapper.getProductStockList(map));
		x = product.getStock(area, type);
		return x;

	}
	
	/**
	 * 	说明：1.得到报损或者报溢后的产品的数量 
	 * 
	 * 	日期：2013-4-15
	 * 
	 * 	作者：石远飞
	 */
	public static int updateProductCount(int x, int type, int count) {
		int result = 0;
		if (type == 0) {
			// 报损
			result = x - count;
		} else {
			result = x + count;
		}
		return result;
	}
	
	
	

}
