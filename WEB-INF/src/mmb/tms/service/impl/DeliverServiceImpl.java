package mmb.tms.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.common.dao.CommonDao;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.tms.dao.AuditOrderStatDao;
import mmb.tms.dao.BalanceCorpInfoDao;
import mmb.tms.dao.DeliverArriveRateDao;
import mmb.tms.dao.DeliverBalanceTypeDao;
import mmb.tms.dao.DeliverCollectRateDao;
import mmb.tms.dao.DeliverCorpInfoDao;
import mmb.tms.dao.DeliverKpiDao;
import mmb.tms.dao.DeliverLogDao;
import mmb.tms.dao.DeliverMailDao;
import mmb.tms.dao.DeliverMailingRateDao;
import mmb.tms.dao.DeliverSendDefaultDao;
import mmb.tms.dao.DeliverSendSpecialDao;
import mmb.tms.dao.DeliverTransitRateDao;
import mmb.tms.dao.ProvinceCityDao;
import mmb.tms.dao.ProvincesDao;
import mmb.tms.model.AuditOrderStat;
import mmb.tms.model.BalanceCorpInfo;
import mmb.tms.model.DeliverArriveRate;
import mmb.tms.model.DeliverBalanceType;
import mmb.tms.model.DeliverCollectRate;
import mmb.tms.model.DeliverKpi;
import mmb.tms.model.DeliverLog;
import mmb.tms.model.DeliverMail;
import mmb.tms.model.DeliverMailingRate;
import mmb.tms.model.DeliverSendDefault;
import mmb.tms.model.DeliverSendSpecial;
import mmb.tms.model.DeliverTransitRate;
import mmb.tms.model.ProvinceCity;
import mmb.tms.model.ProvinceCityDeliverTreeBean;
import mmb.tms.model.Provinces;
import mmb.tms.service.IDeliverService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.StringUtil;

/**
 * 快递公司 service层
 * @author 李宁
 * @date 2014-3-27
 */
@Service
public class DeliverServiceImpl implements IDeliverService{
	
	@Autowired
	private DeliverCorpInfoDao deliverCorpInfoDao;
	@Autowired
	private ProvincesDao provincesDao;
	@Autowired
	private ProvinceCityDao cityDao;
	@Autowired
	private DeliverSendSpecialDao specialDao;
	@Autowired
	private DeliverBalanceTypeDao deliverBalanceTypeDao;
	@Autowired
	private DeliverKpiDao deliverKpiDao;
	@Autowired
	private BalanceCorpInfoDao balanceCorpInfoDao;
	@Autowired
	private DeliverLogDao deliverLogDao;
	@Autowired
	private DeliverMailDao deliverMailDao;
	@Autowired
	private DeliverSendDefaultDao defaultDao;
	@Autowired
	private DeliverTransitRateDao transitDao;
	@Autowired
	private DeliverCollectRateDao collectDao;
	@Autowired
	private DeliverArriveRateDao arriveDao;
	@Autowired
	private DeliverMailingRateDao mailingDao;
	@Autowired
	private AuditOrderStatDao auditDao;
	@Autowired
	private CommonDao commonDao;
	
	@Override
	public int addDeliverCorpInfoBean(DeliverCorpInfoBean deliver) {
		return deliverCorpInfoDao.insert(deliver);
	}

	@Override
	public int addDeliverBalanceType(DeliverBalanceType bean) {
		return deliverBalanceTypeDao.insert(bean);
	}
	
	@Override
	public List<ProvinceCityDeliverTreeBean> getProvincesCitys(String id) {
		List<ProvinceCityDeliverTreeBean> treeList = new ArrayList<ProvinceCityDeliverTreeBean>();
		try {
			Map<String,String> conditionMap = new HashMap<String, String>();
			Map<String,Object> tempMap = new HashMap<String, Object>();
			ProvinceCityDeliverTreeBean tree = null;
			//如果id为空说明是初始化
			if (!"".equals(StringUtil.checkNull(id))) {
				String[] ids = id.split("_");
				//根据id中是否含有下划线区分是第一级节点还是第二级节点
				if(StringUtil.toInt(ids[1]) == 2){
					//顶级节点为-1说明是点击的是全国
					if(StringUtil.toInt(ids[2]) == -1){
						conditionMap.put("condition", "1=1");
						conditionMap.put("order", "dss.index");
						List<Map<String,Object>> defaults = defaultDao.getDeliverSendDefaultMap(conditionMap);
						if(defaults != null && defaults.size() > 0){
							for(Map<String,Object> map : defaults){
								if(tempMap.containsKey(map.get("deliver_corp_id").toString())){
									tree = (ProvinceCityDeliverTreeBean) tempMap.get(map.get("deliver_corp_id").toString());
									if("3".equals(map.get("stock_area_id").toString())){
										tree.setId(tree.getId() + "_3#" + map.get("id").toString()); //快递公司id_级别标识_表id_表id
										tree.setZc(map.get("send_count_limit").toString());
									} else if("4".equals(map.get("stock_area_id").toString())){
										tree.setId(tree.getId() + "_4#" + map.get("id").toString()); //快递公司id_级别标识_表id_表id
										tree.setWx(map.get("send_count_limit").toString());
									}
									tempMap.put(map.get("deliver_corp_id").toString(), tree);
								} else {
									tree = new ProvinceCityDeliverTreeBean();
									tree.setDeliver(map.get("deliver_corp_id").toString());
									tree.setText(map.get("name").toString());
									if("3".equals(map.get("stock_area_id").toString())){
										tree.setId(map.get("deliver_corp_id").toString() + "_3_3#" + map.get("id").toString());
										tree.setZc(map.get("send_count_limit").toString());
									} else if("4".equals(map.get("stock_area_id").toString())){
										tree.setId(map.get("deliver_corp_id").toString() + "_3_4#" + map.get("id").toString());
										tree.setWx(map.get("send_count_limit").toString());
									}
									tree.setIndex(map.get("index").toString());
									tree.setParentId(id);
									tree.setParentText("剩余区域");
									tempMap.put(map.get("deliver_corp_id").toString(), tree);
								}
							}
							for(Map.Entry<String, Object> entry : tempMap.entrySet()){
								treeList.add((ProvinceCityDeliverTreeBean)entry.getValue());
							}
						}
					} else {
						//全境
						if(StringUtil.toInt(ids[0]) == -1){
							conditionMap.put("condition", "dss.target_id=" + ids[2] + " and dss.source=1");
						} else {
							conditionMap.put("condition", "dss.target_id=" + ids[0] + " and dss.source=2");
						}
						conditionMap.put("order", "dss.index");
						List<Map<String,Object>> specials = specialDao.getDeliverSendSpecialMap(conditionMap);
						if(specials != null && specials.size() > 0){
							for(Map<String,Object> map : specials){
								if(tempMap.containsKey(map.get("deliver_corp_id").toString())){
									tree = (ProvinceCityDeliverTreeBean) tempMap.get(map.get("deliver_corp_id").toString());
									if("3".equals(map.get("stock_area_id").toString())){
										tree.setId(tree.getId() + "_3#" + map.get("id").toString()); //快递公司id_级别标识_表id_表id
										tree.setZc(map.get("send_count_limit").toString());
									} else if("4".equals(map.get("stock_area_id").toString())){
										tree.setId(tree.getId() + "_4#" + map.get("id").toString()); //快递公司id_级别标识_表id_表id
										tree.setWx(map.get("send_count_limit").toString());
									}
									tempMap.put(map.get("deliver_corp_id").toString(), tree);
								} else {
									tree = new ProvinceCityDeliverTreeBean();
									tree.setDeliver(map.get("deliver_corp_id").toString());
									tree.setText(map.get("name").toString());
									if("3".equals(map.get("stock_area_id").toString())){
										tree.setId(map.get("deliver_corp_id").toString() + "_3_3#" + map.get("id").toString()); //快递公司id_级别标识_表id
										tree.setZc(map.get("send_count_limit").toString());
									} else if("4".equals(map.get("stock_area_id").toString())){
										tree.setId(map.get("deliver_corp_id").toString() + "_3_4#" + map.get("id").toString()); //快递公司id_级别标识_表id
										tree.setWx(map.get("send_count_limit").toString());
									}
									tree.setIndex(map.get("index").toString());
									tree.setParentId(id);
									tree.setParentText("全境");
									tempMap.put(map.get("deliver_corp_id").toString(), tree);
								}
							}
							for(Map.Entry<String, Object> entry : tempMap.entrySet()){
								treeList.add((ProvinceCityDeliverTreeBean)entry.getValue());
							}
						}
					}
				} else if(StringUtil.toInt(ids[1]) == 1){
					if(StringUtil.toInt(ids[0]) == -1){
						conditionMap.put("condition", "1=1");
						List<DeliverSendDefault> defaults = defaultDao.getDeliverSendDefaultList(conditionMap);
						tree = new ProvinceCityDeliverTreeBean();
						tree.setId(-1 + "_2_-1");  //当前节点id_节点级别标识_上级节点id
						tree.setText("剩余区域");
						tree.setParentId(id);
						tree.setParentText("全国");
						if(defaults != null && defaults.size() > 0){
							tree.setState("closed");
						}
						treeList.add(tree);
					} else {
						//如果选择二级节点且id等于0 那么说明是全省
						conditionMap.put("condition", "province_id=" +ids[0]);
						List<ProvinceCity> citys = cityDao.getProvinceCityList(conditionMap);
						//手动加入全境bean
						ProvinceCity bean = new ProvinceCity();
						bean.setId(-1);
						bean.setCity("全境");
						citys.add(bean);
						if(citys != null && citys.size() > 0){
							Provinces provinces = provincesDao.selectByPrimaryKey(StringUtil.toInt(ids[0]));
							for(ProvinceCity city : citys){
								if(city.getId() == -1){
									conditionMap.put("condition", "target_id=" + ids[0] + " and source=1");
								} else {
									conditionMap.put("condition", "target_id=" + city.getId() + " and source=2");
								}
								List<DeliverSendSpecial> specials = specialDao.getDeliverSendSpecialList(conditionMap);
								if(specials != null && specials.size() > 0){
									city.setSpecials(specials);
								}
								if(provinces != null){
									city.setProvinces(provinces);
								}
							}
							for(int i = citys.size() - 1; i >= 0;i--){
								ProvinceCity city = citys.get(i);
								tree = new ProvinceCityDeliverTreeBean();
								tree.setId(city.getId()+ "_2_" + StringUtil.toInt(ids[0]) ); //当前节点id_节点级别标识_上级节点id
								tree.setText(city.getCity());
								if(city.getProvinces() != null){
									tree.setParentId(id);
									tree.setParentText(city.getProvinces().getName());
								}
								if (city.getSpecials() != null && city.getSpecials().size() > 0) {
									tree.setState("closed");
								}
								treeList.add(tree);
							}
						}
					}
				}
			}else{
				Map<String,String> provincesMap = new HashMap<String, String>();
				provincesMap.put("condition", "1=1");
				provincesMap.put("order", "id");
				List<Provinces> provincesList =provincesDao.getProvincesList(provincesMap);
				if(provincesList != null && provincesList.size() > 0){
					for(Provinces provinces : provincesList){
						conditionMap.put("condition", "province_id=" + provinces.getId());
						List<ProvinceCity> citys = cityDao.getProvinceCityList(conditionMap);
						if(citys != null && citys.size() > 0){
							provinces.setCitys(citys);
						}
					}
				}
				//手动添加一个全国
				tree = new ProvinceCityDeliverTreeBean();
				tree.setId(-1 + "_1_0");
				tree.setText("全国");
				tree.setState("closed");
				treeList.add(tree);
				for (Provinces provinces : provincesList) {
					tree = new ProvinceCityDeliverTreeBean();
					tree.setId(provinces.getId() + "_1");
					tree.setText(provinces.getName());
					if (provinces.getCitys() != null && provinces.getCitys().size() > 0) {
						tree.setState("closed");
					}
					treeList.add(tree);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeList;
	}

	@Override
	public int addDeliverKPI(DeliverKpi bean) {
		return deliverKpiDao.insert(bean);
	}

	@Override
	public List<BalanceCorpInfo> getBalanceCorpInfoList() {
		return balanceCorpInfoDao.getBalanceCorpInfoList();
	}

	@Override
	public DeliverCorpInfoBean getDeliverCorpInfo(Integer id) {
		return deliverCorpInfoDao.getDeliverCorpInfoById(id);
	}

	@Override
	public List<DeliverKpi> getDeliverKpiList(HashMap<String,String> map) {
		return deliverKpiDao.getDeliverKpiList(map);
	}

	@Override
	public DeliverBalanceType getDeliverBalanceTypeByDeliverId(Integer deliverId) {
		return deliverBalanceTypeDao.getDeliverBalanceTypeByDeliverId(deliverId);
	}

	@Override
	public int updateDeliverCorpInfo(DeliverCorpInfoBean deliver) {
		return deliverCorpInfoDao.update(deliver);
	}

	@Override
	public int updateDeliverBalanceType(DeliverBalanceType bean) {
		return deliverBalanceTypeDao.updateByPrimaryKey(bean);
	}

	@Override
	public int updateDeliverKpi(DeliverKpi bean) {
		return deliverKpiDao.updateByPrimaryKey(bean);
	}

	@Override
	public int addDeliverLog(DeliverLog log) {
		return deliverLogDao.insert(log);
	}

	@Override
	public List<DeliverCorpInfoBean> getDelvierList(HashMap<String,Integer> map) {
		return deliverCorpInfoDao.getDeliverCorpInfoList(map);
	}

	@Override
	public List<DeliverLog> getDeliverLogList(String condition) {
		return deliverLogDao.getDeliverLogList(condition);
	}

	@Override
	public int getDeliverMailCount(HashMap<String, String> map) {
		return deliverMailDao.getDeliverMailCount(map);
	}

	@Override
	public List<DeliverMail> getDeliverMailList(HashMap<String, String> map) {
		return deliverMailDao.getDeliverMailList(map);
	}	
	
	@Override
	public List<DeliverCorpInfoBean> getDeliverMailList1(HashMap<String, String> map) {
		return deliverMailDao.getDeliverMailList1(map);
	}	
	
	@Override
	public DeliverMail getDeliverMailInfo(HashMap<String, String> map) {
		return deliverMailDao.getDeliverMailInfo(map);
	}
	
	@Override
	public List<DeliverMail> getDeliverPackageCodeList(HashMap<String, String> map) {
		return deliverMailDao.getDeliverPackageCodeList(map);
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void configDeliver(List<ProvinceCityDeliverTreeBean> addList,List<ProvinceCityDeliverTreeBean> editList) {
		if(addList != null && addList.size() > 0){
			for(ProvinceCityDeliverTreeBean bean :addList){
				int provinceId = StringUtil.toInt(bean.getParentId().split("_")[2]);
				int cityId = StringUtil.toInt(bean.getParentId().split("_")[0]);
				int deliverId = StringUtil.toInt(bean.getDeliver());
				//根据上一级父id判断是不是配置的全国
				if("-1".equals(bean.getParentId().split("_")[2])){
					if(!this.checkConfigDefault(deliverId,ProductStockBean.AREA_ZC,-1)){
						throw new RuntimeException("当前快递公司已配置(增城区域)!");
					}
					if(!this.checkConfigDefault(deliverId,ProductStockBean.AREA_WX,-1)){
						throw new RuntimeException("当前快递公司已配置(无锡区域)!");
					}
					try {
						this.addDeliverConfigDefault(bean);
					} catch (RuntimeException e) {
						throw new RuntimeException(e.getMessage());
					}
					
				} else {
					if(cityId == -1){
						//如果添加全境配置时 有其他市快递公司配置 提示先删除再添加
						if(!this.checkConfigOther(provinceId)){
							throw new RuntimeException("请先删除其他市快递公司配置,再配置全境!");
						}
						this.addDeliverConfig(bean,(byte)1,provinceId);
					} else {
						if(!this.checkConfigAll(provinceId)){
							throw new RuntimeException("请先删除全境快递公司配置,再配置其他市!");
						}
						if(!this.checkConfig(cityId,deliverId,ProductStockBean.AREA_ZC,-1)){
							throw new RuntimeException("当前快递公司已配置(增城区域)!");
						}
						if(!this.checkConfig(cityId,deliverId,ProductStockBean.AREA_WX,-1)){
							throw new RuntimeException("当前快递公司已配置(无锡区域)!");
						}
						try {
							this.addDeliverConfig(bean,(byte)2,cityId);
						} catch (RuntimeException e) {
							throw new RuntimeException(e.getMessage());
						}
					}
				}
			}
		}
		if(editList != null && editList.size() > 0){
			for(ProvinceCityDeliverTreeBean bean : editList){
				int provinceId = StringUtil.toInt(bean.getParentId().split("_")[2]);
				int cityId = StringUtil.toInt(bean.getParentId().split("_")[0]);
				int deliverId = StringUtil.toInt(bean.getDeliver());
				String[] ids = bean.getId().split("_");
				//根据上一级父id判断是不是配置的全国
				if("-1".equals(bean.getParentId().split("_")[2])){//全国
					DeliverSendDefault def = null;
					for(int i = 2;i <ids.length;i++ ){
						int areaId = StringUtil.toInt(ids[i].split("\\#")[0]);
						int tableId = StringUtil.toInt(ids[i].split("\\#")[1]);
						def = new DeliverSendDefault();
						def.setId(tableId);
						def.setDeliverCorpId(deliverId);
						def.setStockAreaId(areaId);
						def.setIndex(StringUtil.toInt(bean.getIndex()));
						if(areaId == ProductStockBean.AREA_ZC){
							if(!this.checkConfigDefault(deliverId,areaId,tableId)){
								throw new RuntimeException("当前快递公司已配置(增城区域)!");
							}
							def.setSendCountLimit(StringUtil.toInt(bean.getZc()));
							def.setSendCountCurrent(StringUtil.toInt(bean.getZc()));
						} else if(areaId == ProductStockBean.AREA_WX){
							if(!this.checkConfigDefault(deliverId,areaId,tableId)){
								throw new RuntimeException("当前快递公司已配置(无锡区域)!");
							}
							def.setSendCountLimit(StringUtil.toInt(bean.getWx()));
							def.setSendCountCurrent(StringUtil.toInt(bean.getWx()));
						}
						try {
							this.updateDeliverConfigDefault(def);
						} catch (RuntimeException e) {
							throw new RuntimeException(e.getMessage());
						}
					}
					
				} else {
					DeliverSendSpecial special = null;
					if(cityId == -1){
						//如果添加全境配置时 有其他市快递公司配置 提示先删除再添加
						if(!this.checkConfigOther(provinceId)){
							throw new RuntimeException("请先删除其他市快递公司配置,再配置全境!");
						}
						for(int i = 2;i <ids.length;i++ ){
							int areaId = StringUtil.toInt(ids[i].split("\\#")[0]);
							int tableId = StringUtil.toInt(ids[i].split("\\#")[1]);
							special = new DeliverSendSpecial();
							special.setId(tableId);
							special.setDeliverCorpId(deliverId);
							special.setSource((byte)1);
							special.setStockAreaId(areaId);
							special.setTargetId(provinceId);
							special.setIndex(StringUtil.toInt(bean.getIndex()));
							if(areaId == ProductStockBean.AREA_ZC){
								special.setSendCountLimit(StringUtil.toInt(bean.getZc()));
								special.setSendCountCurrent(StringUtil.toInt(bean.getZc()));
							} else if(areaId == ProductStockBean.AREA_WX){
								special.setSendCountLimit(StringUtil.toInt(bean.getWx()));
								special.setSendCountCurrent(StringUtil.toInt(bean.getWx()));
							}
							try {
								this.updateDeliverConfig(special);
							} catch (RuntimeException e) {
								throw new RuntimeException(e.getMessage());
							}
						}
						
					} else {
						if(!this.checkConfigAll(provinceId)){
							throw new RuntimeException("请先删除全境快递公司配置,再配置其他市!");
						}
						for(int i = 2;i <ids.length;i++ ){
							int areaId = StringUtil.toInt(ids[i].split("\\#")[0]);
							int tableId = StringUtil.toInt(ids[i].split("\\#")[1]);
							special = new DeliverSendSpecial();
							special.setId(tableId);
							special.setDeliverCorpId(deliverId);
							special.setSource((byte)2);
							special.setStockAreaId(areaId);
							special.setTargetId(cityId);
							special.setIndex(StringUtil.toInt(bean.getIndex()));
							if(areaId == ProductStockBean.AREA_ZC){
								if(!this.checkConfig(cityId,deliverId,3,tableId)){
									throw new RuntimeException("当前快递公司已配置(增城区域)!");
								}
								special.setSendCountLimit(StringUtil.toInt(bean.getZc()));
								special.setSendCountCurrent(StringUtil.toInt(bean.getZc()));
							} else if(areaId == ProductStockBean.AREA_WX){
								if(!this.checkConfig(cityId,deliverId,4,tableId)){
									throw new RuntimeException("当前快递公司已配置(无锡区域)!");
								}
								special.setSendCountLimit(StringUtil.toInt(bean.getWx()));
								special.setSendCountCurrent(StringUtil.toInt(bean.getWx()));
							}
							try {
								this.updateDeliverConfig(special);
							} catch (RuntimeException e) {
								throw new RuntimeException(e.getMessage());
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void addDeliverConfig(ProvinceCityDeliverTreeBean bean,byte source,int targetId) {
		DeliverSendSpecial special = new DeliverSendSpecial();
		special.setDeliverCorpId(StringUtil.toInt(bean.getDeliver()));
		special.setSendCountLimit(StringUtil.toInt(bean.getWx()));
		special.setSendCountCurrent(StringUtil.toInt(bean.getWx()));
		special.setSource(source);
		special.setStockAreaId(ProductStockBean.AREA_WX);
		special.setTargetId(targetId);
		special.setIndex(StringUtil.toInt(bean.getIndex()));
		int count = specialDao.insertSelective(special);
		if(count == 0){
			throw new RuntimeException("无锡库未添加成功!");
		}
		special = new DeliverSendSpecial();
		special.setDeliverCorpId(StringUtil.toInt(bean.getDeliver()));
		special.setSendCountLimit(StringUtil.toInt(bean.getZc()));
		special.setSendCountCurrent(StringUtil.toInt(bean.getZc()));
		special.setSource(source);
		special.setStockAreaId(ProductStockBean.AREA_ZC);
		special.setTargetId(targetId);
		special.setIndex(StringUtil.toInt(bean.getIndex()));
		count = specialDao.insertSelective(special);
		if(count == 0){
			throw new RuntimeException("增城库未添加成功!");
		}
	}

	@Override
	public boolean checkConfig(int id,int deliverId,int areaId,int tableId) {
		Map<String,String> conditionMap = new HashMap<String, String>();
		if(tableId == -1){
			conditionMap.put("condition", "target_id=" + id + " and source=2 and deliver_corp_id=" + deliverId + " and stock_area_id=" + areaId);
		} else {
			conditionMap.put("condition", "target_id=" + id + " and source=2 and deliver_corp_id=" + deliverId + " and stock_area_id=" + areaId + " and id <>" + tableId);
		}
		List<DeliverSendSpecial> specials = specialDao.getDeliverSendSpecialList(conditionMap);
		if(specials != null && specials.size() > 0){
			return false;
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void addDeliverConfigDefault(ProvinceCityDeliverTreeBean bean) {
		try {
			DeliverSendDefault def = new DeliverSendDefault();
			def.setDeliverCorpId(StringUtil.toInt(bean.getDeliver()));
			def.setStockAreaId(ProductStockBean.AREA_ZC);
			def.setSendCountCurrent(StringUtil.toInt(bean.getZc()));
			def.setSendCountLimit(StringUtil.toInt(bean.getZc()));
			def.setIndex(StringUtil.toInt(bean.getIndex()));
			int count = defaultDao.insertSelective(def);
			if(count == 0){
				throw new RuntimeException("增城库未添加成功!");
			}
			def = new DeliverSendDefault();
			def.setDeliverCorpId(StringUtil.toInt(bean.getDeliver()));
			def.setStockAreaId(ProductStockBean.AREA_WX);
			def.setIndex(StringUtil.toInt(bean.getIndex()));
			def.setSendCountCurrent(StringUtil.toInt(bean.getWx()));
			def.setSendCountLimit(StringUtil.toInt(bean.getWx()));
			count = defaultDao.insertSelective(def);
			if(count == 0){
				throw new RuntimeException("无锡库未添加成功!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean checkConfigDefault(int deliverId, int areaId, int tableId) {
		Map<String,String> conditionMap = new HashMap<String, String>();
		if(tableId == -1){
			conditionMap.put("condition", "deliver_corp_id=" + deliverId + " and stock_area_id=" + areaId);
		} else {
			conditionMap.put("condition", "deliver_corp_id=" + deliverId + " and stock_area_id=" + areaId + " and id <>" + tableId);
		}
		List<DeliverSendDefault> defaluts = defaultDao.getDeliverSendDefaultList(conditionMap);
		if(defaluts != null && defaluts.size() > 0){
			return false;
		}
		return true;
	}

	@Override
	public boolean checkConfigOther(int provinceId) {
		Map<String,String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", "province_id=" + provinceId);
		List<ProvinceCity> citys = cityDao.getProvinceCityList(conditionMap);
		if(citys != null && citys.size() > 0){
			StringBuffer buff = new StringBuffer();
			buff.append("(");
			int i = 1;
			for(ProvinceCity city : citys){
				buff.append(city.getId());
				if(i != citys.size()){
					buff.append(",");
				}
				i++;
			}
			buff.append(")");
			conditionMap.put("condition", "target_id in " + buff.toString() + " and source=2");
			List<DeliverSendSpecial> specials = specialDao.getDeliverSendSpecialList(conditionMap);
			if(specials != null && specials.size() > 0){
				return false;
			}
		} else {
			return false;//数据异常
		}
		return true;
	}

	@Override
	public boolean checkConfigAll(int provinceId) {
		Map<String,String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", "target_id = " + provinceId + " and source = 1");
		List<DeliverSendSpecial> specials = specialDao.getDeliverSendSpecialList(conditionMap);
		if(specials != null && specials.size() > 0){
			return false;
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateDeliverConfigDefault(DeliverSendDefault bean) {
		int count = defaultDao.updateByPrimaryKeySelective(bean);
		if(count == 0){
			throw new RuntimeException("更新失败!");
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateDeliverConfig(DeliverSendSpecial bean) {
		int count = specialDao.updateByPrimaryKeySelective(bean);
		if(count == 0){
			throw new RuntimeException("更新失败!");
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void delDeliverConfigDefault(String ids) {
		if(!"".equals(ids)){
			for(String id :ids.split("\\*")){
				if(defaultDao.deleteByPrimaryKey(StringUtil.toInt(id.split("_")[2].split("\\#")[1])) == 0){
					throw new RuntimeException("删除失败");
				}
				if(id.split("_").length > 3){
					if(defaultDao.deleteByPrimaryKey(StringUtil.toInt(id.split("_")[3].split("\\#")[1])) == 0){
						throw new RuntimeException("删除失败");
					}
				}
			}
		}
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void delDeliverConfig(String ids) {
		if(!"".equals(ids)){
			for(String id : ids.split("\\*")){
				if(specialDao.deleteByPrimaryKey(StringUtil.toInt(id.split("_")[2].split("\\#")[1])) == 0){
					throw new RuntimeException("删除失败");
				}
				if(id.split("_").length > 3){
					if(specialDao.deleteByPrimaryKey(StringUtil.toInt(id.split("_")[3].split("\\#")[1])) == 0){
						throw new RuntimeException("删除失败");
					}
				}
			}
		}
	}
	
	@Override
	public long[][] getDeliverTransitIntimeRate(int deliverId,String startDate,String endDate,int areaId) {
		long[][] data = null;
		Map<String,String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", "deliver_id=" + deliverId + " and date BETWEEN '" + startDate + "' and '" + endDate + "' and area_id=" + areaId);
		conditionMap.put("order", "date");
		List<DeliverTransitRate> transites = transitDao.getDeliverTransiteRateList(conditionMap);
		if(transites != null && transites.size() > 0){
			data = new long[transites.size()][2];
			int i = 0;
			for(DeliverTransitRate transite : transites){
				long[] array = new long[2];
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(transite.getDate());
				calendar.add(Calendar.HOUR_OF_DAY, 8); //目的是适应前段stockchart 如果不改成8点 那么默认就是12点 那么它就认为是前一天 擦
				array[0] = calendar.getTime().getTime();
				array[1] = (long)(transite.getIntimeTransitRate()*100);
				data[i] = array;
				i++;
			}
		} else {
			data = new long[0][0];
		}
		return data;
	}

	@Override
	public long[][] getDeliverTransitRate(int deliverId,String startDate,String endDate,int areaId) {
		long[][] data = null;
		try {
			Map<String,String> conditionMap = new HashMap<String, String>();
			conditionMap.put("condition", "deliver_id=" + deliverId + " and date BETWEEN '" + startDate + "' and '" + endDate + "' and area_id=" + areaId);
			conditionMap.put("order", "date");
			List<DeliverTransitRate> transites = transitDao.getDeliverTransiteRateList(conditionMap);
			if(transites != null && transites.size() > 0){
				data = new long[transites.size()][2];
				int i = 0;
				for(DeliverTransitRate transite : transites){
					long[] array = new long[2];
					Calendar calendar = Calendar.getInstance();
					calendar.setTime(transite.getDate());
					calendar.add(Calendar.HOUR_OF_DAY, 8); //目的是适应前段stockchart 如果不改成8点 那么默认就是12点 那么它就认为是前一天 擦
					array[0] = calendar.getTime().getTime();
					array[1] = (long)(transite.getTransitRate()*100);
					data[i] = array;
					i++;
				}
			} else {
				data = new long[0][0];
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	@Override
	public long[][] getDeliverCollectRate(int deliverId,String startDate,String endDate,int areaId) {
		long[][] data = null;
		Map<String,String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", "deliver_id=" + deliverId + " and date BETWEEN '" + startDate + "' and '" + endDate + "' and area_id=" + areaId);
		conditionMap.put("order", "date");
		List<DeliverCollectRate> collects = collectDao.getDeliverCollectRateList(conditionMap);
		if(collects != null && collects.size() > 0){
			data = new long[collects.size()][2];
			int i = 0;
			for(DeliverCollectRate collect : collects){
				long[] array = new long[2];
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(collect.getDate());
				calendar.add(Calendar.HOUR_OF_DAY, 8); //目的是适应前段stockchart 如果不改成8点 那么默认就是12点 那么它就认为是前一天 擦
				array[0] = calendar.getTime().getTime();
				array[1] = (long)(collect.getIntimeCollectRate()*100);
				data[i] = array;
				i++;
			}
		} else {
			data = new long[0][0];
		}
		return data;
	}

	@Override
	public long[][] getDeliverArriveRate(int deliverId,String startDate,String endDate,int areaId) {
		long[][] data = null;
		Map<String,String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", "deliver_id=" + deliverId + " and date BETWEEN '" + startDate + "' and '" + endDate + "' and area_id=" + areaId);
		conditionMap.put("order", "date");
		List<DeliverArriveRate> arrives = arriveDao.getDeliverArriveRateList(conditionMap);
		if(arrives != null && arrives.size() > 0){
			data = new long[arrives.size()][2];
			int i = 0;
			for(DeliverArriveRate arrive : arrives){
				long[] array = new long[2];
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(arrive.getDate());
				calendar.add(Calendar.HOUR_OF_DAY, 8); //目的是适应前段stockchart 如果不改成8点 那么默认就是12点 那么它就认为是前一天 擦
				array[0] = calendar.getTime().getTime();
				array[1] = (long)(arrive.getIntimeArriveRate()*100);
				data[i] = array;
				i++;
			}
		}else {
			data = new long[0][0];
		}
		return data;
	}

	@Override
	public long[][] getDeliverMailingRate(int deliverId,String startDate,String endDate,int areaId) {
		long[][] data = null;
		Map<String,String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", "deliver_id=" + deliverId + " and date BETWEEN '" + startDate + "' and '" + endDate + "' and area_id=" + areaId);
		conditionMap.put("order", "date");
		List<DeliverMailingRate> mailings = mailingDao.getDeliverMailingRateList(conditionMap);
		if(mailings != null && mailings.size() > 0){
			data = new long[mailings.size()][2];
			int i = 0;
			for(DeliverMailingRate mailing : mailings){
				long[] array = new long[2];
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(mailing.getDate());
				calendar.add(Calendar.HOUR_OF_DAY, 8); //目的是适应前段stockchart 如果不改成8点 那么默认就是12点 那么它就认为是前一天 擦
				array[0] = calendar.getTime().getTime();
				array[1] = (long)(mailing.getIntimeMailingRate()*100);
				data[i] = array;
				i++;
			}
		} else {
			data = new long[0][0];
		}
		return data;
	}
	
	@Override
	public long[][] getAuditOrderCount(int deliverId, String startDate,String endDate,int areaId) {
		long[][] data = null;
		Map<String,String> conditionMap = new HashMap<String, String>();
		conditionMap.put("condition", "deliver_id=" + deliverId + " and date BETWEEN '" + startDate + "' and '" + endDate + "' and area_id=" + areaId);
		conditionMap.put("order", "date");
		List<AuditOrderStat> audits = auditDao.getAuditOrderStatList(conditionMap);
		if(audits != null && audits.size() > 0){
			data = new long[audits.size()][2];
			int i = 0;
			for(AuditOrderStat audit : audits){
				long[] array = new long[2];
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(audit.getDate());
				calendar.add(Calendar.HOUR_OF_DAY, 8); //目的是适应前段stockchart 如果不改成8点 那么默认就是12点 那么它就认为是前一天 擦
				array[0] = calendar.getTime().getTime();
				array[1] = (long)(audit.getAuditCount());
				data[i] = array;
				i++;
			}
		} else {
			data = new long[0][0];
		}
		return data;
	}
	
	@Override
	public String getDeliverProviences(String condition) {
		return specialDao.getDeliverAreaProvinces(condition);
	}

	@Override
	public String getDeliverCities(String condition) {
		return specialDao.getDeliverAreaCities(condition);
	}

	@Override
	public int updateDeliverMailStatus(DeliverMail bean) {
		return deliverMailDao.updateDeliverMailStatus(bean);
	}

	@Override
	public int getDeliverIdByName(String deliverName) {
		DeliverCorpInfoBean corpInfo = deliverCorpInfoDao.getDeliverCorpInfoByName(deliverName);
		if(corpInfo != null){
			return corpInfo.getId();
		}
		return -1;
	}
	
	@Override
	public int addDeliverMail(DeliverMail bean) {
		return deliverMailDao.addDeliverMail(bean);
	}

	@Override
	@Transactional(rollbackFor=RuntimeException.class)
	public void editDeliverSendConf(HttpServletRequest request) {
		int area = StringUtil.toInt(request.getParameter("area"));
		int provinceId = StringUtil.toInt(request.getParameter("provinceId"));
		String deliverIds = StringUtil.convertNull(request.getParameter("deliverIds"));
		String countLimits = StringUtil.convertNull(request.getParameter("countLimits"));
		String prioritys = StringUtil.convertNull(request.getParameter("prioritys"));
		String wholeAreas = StringUtil.convertNull(request.getParameter("wholeAreas"));
		
		if (area == -1 || provinceId == -1) {
			throw new RuntimeException("地区或省份有误");
		}
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("table", " deliver_send_conf ");
		paramMap.put("condition", " area =" + area + " and province_id=" + provinceId);
		commonDao.deleteCommon(paramMap);
		if (!deliverIds.equals("")) {
			String delivers = ",";
			String prios = ",";
			int maxPriority = -1;
			int key = -1;
			String[] deliverIdArray = deliverIds.split(",");
			String[] countLimitArray = countLimits.split(",");
			String[] priorityArray = prioritys.split(",");
			String[] wholeAreaArray = wholeAreas.split(",");
			int len = deliverIdArray.length;
			for (int i = 0 ; i < len ; i ++) {
				if (delivers.contains(deliverIdArray[i])) {
					throw new RuntimeException("快递公司不能重复");
				}
				delivers = delivers + deliverIdArray[i] +",";
				if (prios.contains(priorityArray[i])) {
					throw new RuntimeException("优先级不能重复");
				}
				prios = prios + priorityArray[i] +",";
				if (StringUtil.toInt(priorityArray[i]) > maxPriority) {
					maxPriority = StringUtil.toInt(priorityArray[i]);
					key = i;
				}
			}
			if (StringUtil.toInt(countLimitArray[key]) != 0) {
				throw new RuntimeException("最大优先级的单量限制必须为0");
			}
			for (int i = 0 ; i < len ; i ++) {
				paramMap.clear();
				paramMap.put("table", " deliver_send_conf(area,province_id,deliver_id,count_limit,priority,whole_area) ");
				paramMap.put("set", "(" + area + "," + provinceId + "," + deliverIdArray[i] + "," + countLimitArray[i] + "," + priorityArray[i] + "," + wholeAreaArray[i] + ")");
				commonDao.insertCommon(paramMap);
			}
		}
		OrderStockBean.initAreaDeliverPriorityMap();
	}
}
