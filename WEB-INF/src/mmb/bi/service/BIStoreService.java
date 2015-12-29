package mmb.bi.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.bi.dao.BIBaseCountBeanDao;
import mmb.bi.dao.BIChartBeanDao;
import mmb.bi.dao.BIInServiceCountBeanDao;
import mmb.bi.dao.BIOnGuradCountBeanDao;
import mmb.bi.dao.BISmsNumberBeanDao;
import mmb.bi.dao.BIStandardCapacityBeanDao;
import mmb.bi.dao.BITableDao;
import mmb.bi.model.BIBaseCountBean;
import mmb.bi.model.BIChartBean;
import mmb.bi.model.BIHichartJsonBean;
import mmb.bi.model.BIHichartPostBean;
import mmb.bi.model.BIInServiceCountBean;
import mmb.bi.model.BIOnGuradCountBean;
import mmb.bi.model.BISmsNumberBean;
import mmb.bi.model.BIStandardCapacityBean;
import mmb.bi.model.BITableBean;
import mmb.bi.model.EBIArea;
import mmb.bi.model.EBILayerType;
import mmb.bi.model.EBIOperType;
import mmb.common.service.CommonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

/**
 * BI仓储能效service
 * 
 * @author mengqy
 * 
 */
@Service
public class BIStoreService {

	@Autowired
	private BIInServiceCountBeanDao inServiceCountMapper;

	@Autowired
	private BIOnGuradCountBeanDao onGuradCountMapper;

	@Autowired
	private BIStandardCapacityBeanDao standardCapacityBeanMapper;

	@Autowired
	private BIBaseCountBeanDao baseCountBeanMapper;

	@Autowired
	private BIChartBeanDao chartBeanMapper;

	@Autowired
	private BITableDao tableMapper;
	
	@Autowired
	private BISmsNumberBeanDao smsNumberMapper;
	
	/**
	 * 保存在职人力
	 * 
	 * @param bean
	 */
	public void addBIInServiceCountBean(BIInServiceCountBean bean) {
		Date date = DateUtil.parseDate(bean.getDatetime(), DateUtil.normalDateFormat);
		if (date == null) {
			throw new RuntimeException("日期格式不合法");
		}

		bean.setDatetime(DateUtil.formatDate(date));
		updateInServiceCount(bean);
		bean.setStatusByEnum(BIInServiceCountBean.EStatus.Status0);

		String condition = "area_id = " + bean.getAreaId() + " AND  datetime = '" + bean.getDatetime() + "' ";
		if (inServiceCountMapper.getListCount(condition) > 0) {
			throw new RuntimeException("在职人力保存失败，在职人力已经录入");
		}

		if (inServiceCountMapper.insert(bean) <= 0) {
			throw new RuntimeException("在职人力保存失败");
		}
	}

	/**
	 * 查询在职人力记录数量
	 * 
	 * @param condition
	 * @return
	 */
	public int getBIInServiceCountListCount(String condition) {
		return inServiceCountMapper.getListCount(condition);
	}

	/**
	 * 查询在职人力列表
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<BIInServiceCountBean> getBIInServiceCountList(String condition, int index, int count, String orderBy) {
		Map<String, String> paramMap = CommonService.constructSelectMap(condition, index, count, orderBy);
		return inServiceCountMapper.selectList(paramMap);
	}

	/**
	 * 审核在职人力
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void checkBIInServiceCountBean(String id, String datetime, String areaId) {

		if (inServiceCountMapper.check(getAreaIdTimeMap(id, datetime, areaId)) <= 0)
			throw new RuntimeException("审核在职人力失败");

		this.setBaseCountBean(datetime, areaId);
	}

	/**
	 * 作废在职人力
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteBIInServiceCountBean(String id, String datetime, String areaId, String updateTime) {

		if( updateTime != null && updateTime.length() > 10) {
			String date31 = DateUtil.getForwardFromDate(updateTime.substring(0, 10), 31); 
			if (DateUtil.compareTime(DateUtil.getNow(), date31 + " 23:59:59") == 1) {
				throw new RuntimeException("生效日期已经超过31天，不可作废");	
			}
		}
		
		if (inServiceCountMapper.cancelDelete(getAreaIdTimeMap(id, datetime, areaId)) <= 0)
			throw new RuntimeException("作废在职人力失败");

		this.setBaseCountBean(datetime, areaId);
	}

	/**
	 * 修改在职人力
	 * 
	 * @param bean
	 * @return
	 */
	public void updateBIInServiceCountBean(BIInServiceCountBean bean) {
		updateInServiceCount(bean);

		if (inServiceCountMapper.update(bean) <= 0)
			throw new RuntimeException("保存在职人力失败");
	}

	/**
	 * 保存在岗人力
	 * 
	 * @param bean
	 */
	public void addBIOnGuradCountBean(BIOnGuradCountBean bean) {

		Date date = DateUtil.parseDate(bean.getDatetime(), DateUtil.normalDateFormat);
		if (date == null) {
			throw new RuntimeException("日期格式不合法");
		}
		bean.setDatetime(DateUtil.formatDate(date));
		updateOnGuradCount(bean);
		bean.setStatusByEnum(BIOnGuradCountBean.EStatus.Status0);

		StringBuffer sb = new StringBuffer();
		sb.append(" datetime = '").append(bean.getDatetime()).append("' ");
		sb.append(" AND type = ").append(bean.getType());
		sb.append(" AND area_id = ").append(bean.getAreaId());
		sb.append(" AND department = ").append(bean.getDepartment());
		sb.append(" AND oper_type = ").append(bean.getOperType());

		if (onGuradCountMapper.getListCount(sb.toString()) > 0) {
			throw new RuntimeException("在岗人力保存失败，在岗人力已经录入");
		}

		if (onGuradCountMapper.insert(bean) <= 0) {
			throw new RuntimeException("在岗人力保存失败");
		}
	}

	/**
	 * 查询在岗人力记录数量
	 * 
	 * @param condition
	 * @return
	 */
	public int getBIOnGuradCountListCount(String condition) {
		return onGuradCountMapper.getListCount(condition);
	}

	/**
	 * 查询在岗人力列表
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @param type
	 *            人员类型
	 * @return
	 */
	public List<BIOnGuradCountBean> getBIOnGuradCountList(String condition, int index, int count, String orderBy, String type) {
		Map<String, String> paramMap = CommonService.constructSelectMap(condition, index, count, orderBy);
		List<BIOnGuradCountBean> rows = onGuradCountMapper.selectList(paramMap);

		if (rows != null && rows.size() > 0) {
			BIOnGuradCountBean bean = new BIOnGuradCountBean();
			bean.setBeSupportCount(0);
			bean.setBeSupportTimeLength(0.0F);
			bean.setOnGuradCount(0.0F);
			bean.setOnGuradTimeLength(0.0F);
			bean.setSupportCount(0);
			bean.setSupportTimeLength(0.0F);
			bean.setTempCount(0);
			bean.setTurnOut(0);
			bean.setType(Integer.valueOf(type));
			for (BIOnGuradCountBean temp : rows) {
				bean.setBeSupportCount(bean.getBeSupportCount() + temp.getBeSupportCount());
				bean.setBeSupportTimeLength(bean.getBeSupportTimeLength() + temp.getBeSupportTimeLength());
				bean.setOnGuradCount(bean.getOnGuradCount() + temp.getOnGuradCount());
				bean.setOnGuradTimeLength(bean.getOnGuradTimeLength() + temp.getOnGuradTimeLength());
				bean.setSupportCount(bean.getSupportCount() + temp.getSupportCount());
				bean.setSupportTimeLength(bean.getSupportTimeLength() + temp.getSupportTimeLength());
				bean.setTempCount(bean.getTempCount() + temp.getTempCount());
				bean.setTurnOut(bean.getTurnOut() + temp.getTurnOut());
			}
			rows.add(bean);
		}

		return rows;
	}

	/**
	 * 审核在岗人力
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void checkBIOnGuradCountBean(String id, String datetime, String areaId) {

		if (onGuradCountMapper.check(getAreaIdTimeMap(id, datetime, areaId)) <= 0)
			throw new RuntimeException("审核在岗人力失败");

		this.setBaseCountBean(datetime, areaId);
	}

	/**
	 * 作废在岗人力
	 * 
	 * @param id
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteBIOnGuradCountBean(String id, String datetime, String areaId, String updateTime) {
		
		if( updateTime != null && updateTime.length() > 10) {
			String date31 = DateUtil.getForwardFromDate(updateTime.substring(0, 10), 31); 
			if (DateUtil.compareTime(DateUtil.getNow(), date31 + " 23:59:59") == 1) {
				throw new RuntimeException("生效日期已经超过31天，不可作废");	
			}
		}
		
		if (onGuradCountMapper.cancelDelete(getAreaIdTimeMap(id, datetime, areaId)) <= 0)
			throw new RuntimeException("作废在岗人力失败");

		this.setBaseCountBean(datetime, areaId);
	}

	/**
	 * 修改在岗人力
	 * 
	 * @param bean
	 * @return
	 */
	public void updateBIOnGuradCountBean(BIOnGuradCountBean bean) {

		updateOnGuradCount(bean);

		if (onGuradCountMapper.update(bean) <= 0)
			throw new RuntimeException("保存在岗人力失败");
	}

	/**
	 * 查询人力基础数据数量
	 * 
	 * @param condition
	 * @return
	 */
	public int getBIBaseCountListCount(String condition) {
		return baseCountBeanMapper.getListCount(condition);
	}

	/**
	 * 查询人力基础数据列表
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<BIBaseCountBean> getBIBaseCountList(String condition, int index, int count, String orderBy) {
		Map<String, String> paramMap = CommonService.constructSelectMap(condition, index, count, orderBy);
		return baseCountBeanMapper.selectList(paramMap);
	}

	/**
	 * 保存标准产能
	 * 
	 * @param bean
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void addBIStandardCapacityBean(BIStandardCapacityBean bean) {

		Date startTime = DateUtil.parseDate(bean.getStartTime(), DateUtil.normalDateFormat);
		if (startTime == null) {
			throw new RuntimeException("生效日期格式不合法");
		}

		bean.setStartTime(DateUtil.formatDate(startTime));
		bean.setCreateTime(DateUtil.getNow());
		bean.setStatus(0);

		Date currentTime = DateUtil.getNowDate();
		if (startTime.getTime() < currentTime.getTime())
			throw new RuntimeException("生效日期不能小于当前日期");

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("areaId", bean.getAreaId().toString());
		map.put("operType", bean.getOperType().toString());
		// 停用日期等于当前的产能的生效日期
		map.put("stopTime", DateUtil.formatDate(startTime));
		map.put("updateTime", DateUtil.getNow());

		this.standardCapacityBeanMapper.updateStopTime(map);
		if (this.standardCapacityBeanMapper.insert(bean) <= 0)
			throw new RuntimeException("标准产能保存失败");
	}

	/**
	 * 查询标准产能记录数量
	 * 
	 * @param condition
	 * @return
	 */
	public int getBIStandardCapacityListCount(String condition) {
		return this.standardCapacityBeanMapper.getListCount(condition);
	}

	/**
	 * 查询标准产能列表
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<BIStandardCapacityBean> getBIStandardCapacityList(String condition, int index, int count, String orderBy) {
		Map<String, String> paramMap = CommonService.constructSelectMap(condition, index, count, orderBy);
		return this.standardCapacityBeanMapper.selectList(paramMap);
	}

	/**
	 * 整体效能 单仓效能
	 * 
	 * @param bean
	 * @return
	 */
	public BIHichartJsonBean getSingleOrderCountChart(BIHichartPostBean bean) {
		
		HashMap<String, String> map = getSelectType(bean);

		if (bean.getLayer() == EBILayerType.All.getIndex().intValue()) {
			map.put("field", "total");
		} else {
			map.put("field", "ware");
		}
		map.put("areaId", bean.getAreaId() + "");
		
		List<BIChartBean> chartList = null;		
		int selectType = Integer.valueOf(map.get("selectType")).intValue();
		
		switch (selectType) {
		case 0:
			chartList = chartBeanMapper.singleOrderCountByDay(map);
			break;

		case 1:
			chartList = chartBeanMapper.singleOrderCountByMonth(map);
			break;

		case 2:
			chartList = chartBeanMapper.singleOrderCountByYear(map);
			break;
		}

		BIHichartJsonBean json = getChartJsonWithTime(chartList, selectType);
		this.setChartTitle(json, "日人均产能走势", selectType);
		return json;
	}

	
	/**
	 * 整体效能 分仓对比
	 * @param bean
	 * @return
	 */
	public BIHichartJsonBean getMultiOrderCountChart(BIHichartPostBean bean) {
		
		HashMap<String, String> map = getSelectType(bean);

		if (bean.getLayer() == EBILayerType.All.getIndex().intValue()) {
			map.put("field", "total");
		} else {
			map.put("field", "ware");
		}
		
		List<BIChartBean> chartList = null;		
		int selectType = Integer.valueOf(map.get("selectType")).intValue();
		
		switch (selectType) {
		case 0:
			chartList = chartBeanMapper.multiOrderCountByDay(map);
			break;

		case 1:
			chartList = chartBeanMapper.multiOrderCountByMonth(map);
			break;

		case 2:
			chartList = chartBeanMapper.multiOrderCountByYear(map);
			break;
		}

		BIHichartJsonBean json = getChartJsonWithArea(chartList, selectType);
		this.setChartTitle(json, "日人均产能分仓对比", selectType);
		return json;
	}
	
	
	/**
	 * 整体效能 在岗率
	 * @param bean
	 * @return
	 */
	public BIHichartJsonBean getOnGuradPerChart(BIHichartPostBean bean) {
		HashMap<String, String> map = getSelectType(bean);

		if (bean.getLayer() == EBILayerType.All.getIndex().intValue()) {
			map.put("field", "total");
		} else {
			map.put("field", "ware");
		}
		map.put("areaId", bean.getAreaId() + "");
		
		List<BIChartBean> chartList = null;		
		int selectType = Integer.valueOf(map.get("selectType")).intValue();
		
		switch (selectType) {
		case 0:
			chartList = chartBeanMapper.onGuradPerByDay(map);
			break;

		case 1:
			chartList = chartBeanMapper.onGuradPerByMonth(map);
			break;

		case 2:
			chartList = chartBeanMapper.onGuradPerByYear(map);
			break;
		}

		BIHichartJsonBean resultJson = getChartJsonWithTime(chartList, selectType);
		this.setChartTitle(resultJson, "人员安排走势", selectType);
		return resultJson;
	}
	
	
	/**
	 * 整体效能 
	 * @param map
	 * @return
	 */
	public List<HashMap<String, String>> getOrderCountTableList(Map<String, String> map){
		return this.tableMapper.getOrderCountTable(map);		
	}
	
	
	/**
	 * 作业环节 单仓效能
	 * 
	 * @param bean
	 * @return
	 */
	public BIHichartJsonBean getSingleOperTypeChart(BIHichartPostBean bean) {
		
		HashMap<String, String> map = getSelectType(bean);
		map.put("areaId", bean.getAreaId() + "");
		map.put("operType", bean.getOperType() + "");		
		
		List<BIChartBean> chartList = null;		
		int selectType = Integer.valueOf(map.get("selectType")).intValue();
		
		switch (selectType) {
		case 0:
			chartList = chartBeanMapper.singleOperTypeByDay(map);
			break;

		case 1:
			chartList = chartBeanMapper.singleOperTypeByMonth(map);
			break;

		case 2:
			chartList = chartBeanMapper.singleOperTypeByYear(map);
			break;
		}

		BIHichartJsonBean json = getChartJsonWithTime(chartList, selectType);
		EBIOperType operType = EBIOperType.getEnum(Integer.valueOf(bean.getOperType()));		
		this.setChartTitle(json, (operType == null ? "" : operType.getName() + "日人均产能"), selectType);
		return json;
	}

	
	/**
	 * 作业环节 分仓对比
	 * @param bean
	 * @return
	 */
	public BIHichartJsonBean getMultiOperTypeChart(BIHichartPostBean bean) {
		
		HashMap<String, String> map = getSelectType(bean);
		map.put("operType", bean.getOperType() + "");
		
		List<BIChartBean> chartList = null;		
		int selectType = Integer.valueOf(map.get("selectType")).intValue();
		
		switch (selectType) {
		case 0:
			chartList = chartBeanMapper.multiOperTypeByDay(map);
			break;

		case 1:
			chartList = chartBeanMapper.multiOperTypeByMonth(map);
			break;

		case 2:
			chartList = chartBeanMapper.multiOperTypeByYear(map);
			break;
		}

		BIHichartJsonBean json = getChartJsonWithArea(chartList, selectType);
		EBIOperType operType = EBIOperType.getEnum(Integer.valueOf(bean.getOperType()));		
		this.setChartTitle(json, (operType == null ? "" : operType.getName() + "日人均产能分仓对比"), selectType);		
		return json;
	}
	
	/**
	 * 作业环节 日在岗人均产能
	 * @param paramMap
	 * @return
	 */
	public List<BITableBean> getOperTypeTableList(HashMap<String,String> paramMap) {
		return this.tableMapper.getOperTypeTableList(paramMap);
	}
	
	/**
	 * 保存定时短信手机号
	 * 
	 * @param bean
	 */
	public void saveBISmsNumberBean(BISmsNumberBean bean, voUser user) {		
		String condition = " number = '" + bean.getNumber() + "' AND id <> " + bean.getId();		
		if (smsNumberMapper.getListCount(condition) > 0) {
			throw new RuntimeException("定时短信手机号保存失败，手机号已经录入");
		}

		if (bean.getId() > 0) {
			bean.setUpdateUserId(user.getId());
			bean.setUpdateUsername(user.getUsername());
			bean.setUpdateTime(DateUtil.getNow());
			if (smsNumberMapper.update(bean) <= 0) {
				throw new RuntimeException("定时短信手机号保存失败");
			}	
		} else {
			bean.setCreateUserId(user.getId());
			bean.setCreateUsername(user.getUsername());
			bean.setCreateTime(DateUtil.getNow());
			bean.setStatus(0);
			if (smsNumberMapper.insert(bean) <= 0) {
				throw new RuntimeException("定时短信手机号保存失败");
			}	
		}
	}

	/**
	 * 查询定时短信手机号记录数量
	 * 
	 * @param condition
	 * @return
	 */
	public int getBISmsNumberListCount(String condition) {
		return smsNumberMapper.getListCount(condition);
	}

	/**
	 * 查询定时短信手机号列表
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<BISmsNumberBean> getBISmsNumberList(String condition, int index, int count, String orderBy) {		
		return smsNumberMapper.selectList(condition, index, count, orderBy);
	}

	/**
	 * 审核定时短信手机号
	 * 
	 * @param id
	 * @return
	 */
	public void checkBISmsNumberBean(String id, String status, voUser user) {

		String condition = convertIdToCondition(id);
		HashMap<String, String> map= new HashMap<String, String>();
		map.put("checkTime", DateUtil.getNow());
		map.put("userId", user.getId()+"");
		map.put("username", user.getUsername());
		map.put("status", status);
		map.put("condition", condition);
				
		if (!smsNumberMapper.checkAll(map))
			throw new RuntimeException("审核定时短信手机号失败");
	}

	/**
	 * 作废定时短信手机号
	 * 
	 * @param id
	 * @return
	 */
	public void deleteBISmsNumberBean(String id) {
		String condition = convertIdToCondition(id);
		if (!smsNumberMapper.cancelDeleteAll(condition))
			throw new RuntimeException("删除定时短信手机号失败");
	}

	private String convertIdToCondition(String id){
		StringBuilder sb = new StringBuilder();		
		if (id != null) {
			if (id.indexOf(",") > -1) {
				sb.append(" id IN ( ").append(id).append(" )");	
			} else {
				sb.append(" id = ").append(id);
			}
		}
		return sb.toString();
	}
	 
	
	/**
	 * 设置人力基础数据
	 */
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private void setBaseCountBean(String datetime, String areaId) {

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("datetime", datetime);
		map.put("areaId", areaId);
		BIBaseCountBean baseCount = baseCountBeanMapper.searchOne(map);

		StringBuffer sbCondtion = new StringBuffer();
		sbCondtion.append(" datetime = '").append(datetime).append("' ");
		sbCondtion.append(" AND area_id = ").append(areaId).append(" ");
		BIBaseCountBean saveCount = baseCountBeanMapper.selectByCondition(sbCondtion.toString());

		if (baseCount != null) {
			baseCount.setInTotal(baseCount.getInAdmin() + baseCount.getInDelivery() + baseCount.getInWare());
			baseCount.setOnTotal(baseCount.getOnAdmin() + baseCount.getOnDelivery() + baseCount.getOnWare());
			if (saveCount == null) {
				baseCount.setAreaId(Integer.valueOf(areaId));
				baseCount.setDatetime(datetime);
				if (baseCountBeanMapper.insert(baseCount) <= 0)
					throw new RuntimeException("更新人力基础数据失败");
			} else {
				baseCount.setId(saveCount.getId());
				if (baseCountBeanMapper.updateByPrimaryKey(baseCount) <= 0)
					throw new RuntimeException("更新人力基础数据失败");
			}
		} else {
			if (saveCount != null) {
				if (baseCountBeanMapper.deleteByPrimaryKey(saveCount.getId()) <= 0)
					throw new RuntimeException("更新人力基础数据失败");
			}
		}
	}

	/**
	 * 更新在职bean
	 * 
	 * @param bean
	 */
	private void updateInServiceCount(BIInServiceCountBean bean) {
		bean.setCreateTime(DateUtil.formatTime(new Date()));
		bean.setUpdateTime(DateUtil.formatTime(new Date()));		
		int total = 0;
		total += bean.getAdministration() + bean.getDelivery() + bean.getHr() + bean.getOperation() + bean.getProduct();
		total += bean.getQualityChecking() + bean.getRefundGoods() + bean.getSendGoods() + bean.getWarehouse();
		bean.setTotal(total);
	}

	/**
	 * 更新在岗bean
	 * 
	 * @param bean
	 */
	private void updateOnGuradCount(BIOnGuradCountBean bean) {
		bean.setCreateTime(DateUtil.formatTime(new Date()));
		bean.setUpdateTime(DateUtil.formatTime(new Date()));
		// 作业人员
		if (bean.getType() == BIOnGuradCountBean.EType.Type0.getIndex()) {
			bean.setDepartment(-1);

			float len = bean.getOnGuradTimeLength() + bean.getBeSupportTimeLength() - bean.getSupportTimeLength();
			bean.setOnGuradCount(Float.valueOf(getKeepTwoDecimal((float) (len / 8.0))));
		} else {
			// 职能管理人员
			bean.setOperType(-1);
			bean.setBeSupportCount(0);
			bean.setBeSupportTimeLength(0.0F);
			bean.setSupportCount(0);
			bean.setSupportTimeLength(0.0F);
			bean.setTempCount(0);

			bean.setOnGuradTimeLength(Float.valueOf(bean.getTurnOut() * 8));
			bean.setOnGuradCount(Float.valueOf(bean.getTurnOut()));
		}
	}

	/**
	 * 计算float 保留两位小数
	 * 
	 * @param f
	 * @return
	 */
	private float getKeepTwoDecimal(float f) {
		java.math.BigDecimal b = new java.math.BigDecimal(f);  
		return (float)b.setScale(2, java.math.BigDecimal.ROUND_HALF_UP).doubleValue();  
	}

	private HashMap<String, String> getAreaIdTimeMap(String id, String datetime, String areaId) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", id);
		map.put("datetime", datetime);
		map.put("areaId", areaId);
		map.put("updateTime", DateUtil.getNow());
		return map;
	}

	private HashMap<String, String> getSelectType(BIHichartPostBean bean) {
		HashMap<String, String> map = new HashMap<String, String>();
		String beginDate = null;
		String endDate = null;
		// 0 按日查询
		// 1 按月查询
		// 2 按年查询
		String selectType = null;

		if (bean.getBeginYear() > 0 && bean.getBeginMonth() > 0 && bean.getBeginDay() > 0 
				&& bean.getEndYear() > 0 && bean.getEndMonth() > 0 && bean.getEndDay() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(Integer.toString(bean.getBeginYear())).append("-");
			sb.append(StringUtil.padLeft(Integer.toString(bean.getBeginMonth()), '0', 2)).append("-");
			sb.append(StringUtil.padLeft(Integer.toString(bean.getBeginDay()), '0', 2)).append(" 00:00:00");
			beginDate = sb.toString();

			sb = new StringBuffer();
			sb.append(Integer.toString(bean.getEndYear())).append("-");
			sb.append(StringUtil.padLeft(Integer.toString(bean.getEndMonth()), '0', 2)).append("-");
			sb.append(StringUtil.padLeft(Integer.toString(bean.getEndDay()), '0', 2)).append(" 23:59:59");
			endDate = sb.toString();

			Date begin = DateUtil.parseDate(beginDate, DateUtil.normalTimeFormat);
			Date end = DateUtil.parseDate(endDate, DateUtil.normalTimeFormat);
			
			if(begin == null || end == null)
				throw new RuntimeException("日期格式不合法");
			
			if (((end.getTime() - begin.getTime())/(1000 * 24 * 3600)) > 31)
				throw new RuntimeException("日期范围不可以大于31天");

			if (((end.getTime() - begin.getTime())/(1000 * 24 * 3600)) < 1)
				throw new RuntimeException("日期范围不可以小于2天");
			selectType = "0";
		}

		if (bean.getBeginYear() > 0 && bean.getBeginMonth() > 0 && bean.getBeginDay() <= 0 
				&& bean.getEndYear() <= 0 && bean.getEndMonth() <= 0 && bean.getEndDay() <= 0) {

			StringBuffer sb = new StringBuffer();
			sb.append(Integer.toString(bean.getBeginYear())).append("-");
			sb.append(StringUtil.padLeft(Integer.toString(bean.getBeginMonth()), '0', 2)).append("-01 00:00:00");
			beginDate = sb.toString();

			endDate = DateUtil.getMonthLastDay(bean.getBeginYear() + "", bean.getBeginMonth() + "");			
			selectType = "0";
		}

		if (bean.getBeginYear() > 0 && bean.getBeginMonth() > 0 && bean.getBeginDay() <= 0 
				&& bean.getEndYear() > 0 && bean.getEndMonth() > 0 && bean.getEndDay() <= 0) {
			StringBuffer sb = new StringBuffer();
			sb.append(Integer.toString(bean.getBeginYear())).append("-");
			sb.append(StringUtil.padLeft(Integer.toString(bean.getBeginMonth()), '0', 2)).append("-01 00:00:00");
			beginDate = sb.toString();

			endDate = DateUtil.getMonthLastDay(bean.getEndYear() + "", bean.getEndMonth() + "");
						
			if (bean.getEndYear() - bean.getBeginYear() > 1) {
				throw new RuntimeException("日期范围不可以大于12个月");
			}
			if ((bean.getEndYear() - bean.getBeginYear() == 1) && bean.getEndMonth() >= bean.getBeginMonth()) {
				throw new RuntimeException("日期范围不可以大于12个月");
			}
			if ((bean.getEndYear() == bean.getBeginYear()) && bean.getEndMonth() - bean.getBeginMonth() < 1) {
				throw new RuntimeException("日期范围不可以小于2个月");
			}
			selectType = "1";
		}

		if (bean.getBeginYear() > 0 && bean.getBeginMonth() <= 0 && bean.getBeginDay() <= 0 && bean.getEndYear() <= 0 && bean.getEndMonth() <= 0 && bean.getEndDay() <= 0) {

			beginDate = Integer.toString(bean.getBeginYear()) + "-01-01 00:00:00";
			endDate = Integer.toString(bean.getBeginYear()) + "-12-31 23:59:59";

			selectType = "1";
		}

		if (bean.getBeginYear() > 0 && bean.getBeginMonth() <= 0 && bean.getBeginDay() <= 0 && bean.getEndYear() > 0 && bean.getEndMonth() <= 0 && bean.getEndDay() <= 0) {

			beginDate = Integer.toString(bean.getBeginYear()) + "-01-01 00:00:00";
			endDate = Integer.toString(bean.getEndYear()) + "-12-31 23:59:59";

			if (bean.getEndYear() - bean.getBeginYear() >= 5)
				throw new RuntimeException("日期范围不可以大于5年");

			if (bean.getEndYear() - bean.getBeginYear() < 1)
				throw new RuntimeException("日期范围不可以小于1年");
			selectType = "2";
		}

		if (selectType == null) {
			throw new RuntimeException("日期格式不合法");
		} else {
			Date begin = DateUtil.parseDate(beginDate, DateUtil.normalTimeFormat);
			Date end = DateUtil.parseDate(endDate, DateUtil.normalTimeFormat);
			
			if(begin == null || end == null)
				throw new RuntimeException("日期格式不合法");
			
			if (end.getTime() - begin.getTime() <= 0)
				throw new RuntimeException("结束日期必须大于起始日期");
		}

		map.put("beginDate", beginDate);
		map.put("endDate", endDate);
		map.put("selectType", selectType);
		return map;
	}

	private BIHichartJsonBean getChartJsonWithTime(List<BIChartBean> chartList, int selectType) {
		BIHichartJsonBean resultJson = new BIHichartJsonBean();
		resultJson.setSelectType(selectType);
		
		int begin = 0;
		int end = 0;
		switch (selectType) {
		case 0:
			begin = 5;
			end = 10;
			break;

		case 1:
			begin = 2;
			end = 7;
			break;

		case 2:
			begin = 0;
			end = 4;
			break;
		}

		if (chartList != null) {
			for (BIChartBean chart : chartList) {
				resultJson.getCatList().add(chart.getDatetime().substring(begin, end));
				resultJson.getData1().add(getKeepTwoDecimal(chart.getData1()));
				resultJson.getData2().add(getKeepTwoDecimal(chart.getData2()));
				resultJson.getData3().add(getKeepTwoDecimal(chart.getData3()));
			}
		}
		return resultJson;
	}

	private BIHichartJsonBean getChartJsonWithArea(List<BIChartBean> chartList, int selectType) {
		BIHichartJsonBean resultJson = new BIHichartJsonBean();
		resultJson.setSelectType(selectType);
		if (chartList != null) {
			for (BIChartBean chart : chartList) {
				if(!ProductStockBean.stockoutAvailableAreaMap.containsKey(Integer.valueOf(chart.getAreaId()))){
					continue;
				}
				resultJson.getCatList().add(ProductStockBean.stockoutAvailableAreaMap.get(Integer.valueOf(chart.getAreaId())));
				resultJson.getData1().add(getKeepTwoDecimal(chart.getData1()));
				resultJson.getData2().add(getKeepTwoDecimal(chart.getData2()));
				resultJson.getData3().add(getKeepTwoDecimal(chart.getData3()));
			}
		}
		return resultJson;
	}

	private void setChartTitle(BIHichartJsonBean json, String title, int selectType){
		String type = "(按天统计)";
		if (selectType == 1) {
			type = "(按月统计)";
		} else if (selectType == 2) {
			type = "(按年统计)";
		}
		if (json != null) {
			json.setTitle(title + type);
		}
	}
	
}
