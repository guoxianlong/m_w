package mmb.stock.fitting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.easyui.EasyuiDataGridBean;
import mmb.easyui.EasyuiPageBean;
import mmb.easyui.Json;
import mmb.stock.fitting.dao.AfterSaleDetectProductFittingDao;
import mmb.stock.fitting.dao.AfterSaleFittingsDao;
import mmb.stock.fitting.dao.AfterSaleReceiveFittingDao;
import mmb.stock.fitting.dao.AfterSaleReceiveFittingDetailDao;
import mmb.stock.fitting.dao.BuyAdminHistoryDao;
import mmb.stock.fitting.dao.BuyStockinDao;
import mmb.stock.fitting.model.AfterSaleReceiveFitting;
import mmb.stock.fitting.model.AfterSaleReceiveFittingDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voUser;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;


/**
 * 配件service
 */
@Service
public class FittingService {

	@Autowired
	private AfterSaleFittingsDao fittingMapper;
	@Autowired
	private AfterSaleReceiveFittingDao receiveMapper;
	@Autowired
	private AfterSaleReceiveFittingDetailDao receiveDetailMapper;
	@Autowired
	private BuyStockinDao buyStockin;
	@Autowired
	private AfterSaleDetectProductFittingDao detectFittingMapper;
	@Autowired
	private BuyAdminHistoryDao buyAdminHistroyDao;

	@Transactional(rollbackFor=Exception.class)
	public void editReceiveFitting(voUser user, String receiveId,String fittingIds,String detectCodes, String fittingCounts, String target,String areaId) {
		AfterSaleReceiveFitting receive = new AfterSaleReceiveFitting();
		receive.setId(StringUtil.toInt(receiveId));
		receive.setAreaId(Short.parseShort(areaId));
		receive.setStatus((byte) AfterSaleReceiveFitting.STATUS1);
		receive.setTarget(Byte.parseByte(target));
		int number = receiveMapper.updateByPrimaryKeySelective(receive);
		if(number == 0){
			throw new RuntimeException("更新领用单失败!");
		}
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("condition", "asrfd.receive_fitting_id=" + receiveId);
		List<Map<String,Object>> rows = receiveDetailMapper.getReceiveFittingDetailList(condition);
		if(rows != null && rows.size() > 0){
			number = receiveDetailMapper.deleteByReceiveID(StringUtil.toInt(receiveId));
			if(number == 0){
				throw new RuntimeException("程序异常_删除领用单商品失败!");
			}
		}
		if(!"".equals(fittingIds)){
			for(int i = 0;i < fittingIds.split(",").length; i++){
				AfterSaleReceiveFittingDetail receiveDetail = new AfterSaleReceiveFittingDetail();
				receiveDetail.setFittingId(StringUtil.toInt(fittingIds.split(",")[i]));
				receiveDetail.setReceiveFittingId(StringUtil.toInt(receiveId));
				receiveDetail.setCount(StringUtil.toInt(fittingCounts.split(",")[i]));
				if(!"".equals(detectCodes)){
					condition.put("condition", "asdp.code='" + detectCodes.split(",")[i] + "' and       fitting.id ='" + fittingIds.split(",")[i] + "'");
					Map<String,Object> detectFitting = detectFittingMapper.getAfterSaleDetectProductFitting(condition);
					if(detectFitting == null){
						throw new RuntimeException("处理单号[" + detectCodes.split(",")[i] + "]不正确!");
					}
					receiveDetail.setDetectProductId(Integer.parseInt(detectFitting.get("detectId").toString()));
				}
				number = receiveDetailMapper.insertSelective(receiveDetail);
				if(number == 0){
					throw new RuntimeException("更新领用单明细失败!");
				}
			}
		}
		
	}
	public Json getReceiveFittingEdit(HttpServletRequest request, String receiveId) {
		Json j = new Json();
		Map<String,Object> dataMap = new HashMap<String, Object>();
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("condition", "id=" + receiveId);
		AfterSaleReceiveFitting receive = receiveMapper.getAfterSaleReceiveFitting(condition);
		if(receive == null){
			j.setMsg("该领用单已不存在!");
			return j;
		}
		if(receive.getStatus() > AfterSaleReceiveFitting.STATUS2){
			j.setMsg("状态正确,不允许修改!");
			return j;
		}
		dataMap.put("receiveCode", receive.getCode());
		dataMap.put("receiveId", receive.getId());
		dataMap.put("areaId", receive.getAreaId());
		dataMap.put("target", receive.getTarget());
		j.setObj(dataMap);
		condition.put("condition", "asrfd.receive_fitting_id=" + receiveId);
		List<Map<String,Object>> rows = receiveDetailMapper.getReceiveFittingDetailList(condition);
		if(rows != null){
			Map<String,Map<String,String>> cacheMap = new HashMap<String, Map<String,String>>();
			Map<String,String> fittingMap = new HashMap<String, String>();
			for(Map<String,Object> row : rows){
				if(!"".equals(StringUtil.checkNull((String) row.get("detectCode")))){
					if(cacheMap.containsKey(row.get("detectCode"))){
						fittingMap = cacheMap.get(row.get("detectCode"));
						fittingMap.put((String) row.get("fittingCode"), row.get("fittingCount").toString());
						cacheMap.put((String) row.get("detectCode"), fittingMap);
					} else {
						fittingMap.put((String) row.get("fittingCode"), row.get("fittingCount").toString());
						cacheMap.put((String) row.get("detectCode"), fittingMap);
					}
				} else {
					if(cacheMap.containsKey("")){
						fittingMap = cacheMap.get("");
						fittingMap.put((String) row.get("fittingCode"), row.get("fittingCount").toString());
						cacheMap.put("", fittingMap);
					} else {
						fittingMap.put((String) row.get("fittingCode"), row.get("fittingCount").toString());
						cacheMap.put("", fittingMap);
					}
				}
			}
			request.getSession().setAttribute("cacheMap", cacheMap);
			j.setSuccess(true);
		}
		return j;
	}
	@SuppressWarnings("unchecked")
	public Json addCacheAddFitting(HttpServletRequest request, String detectCode,String fittingCodes, String fittingCounts,String target) {
		Json j = new Json();
		Map<String,Map<String,String>> cacheMap = (Map<String, Map<String,String>>) request.getSession().getAttribute("cacheMap");
		Map<String,String> tempMap = new HashMap<String, String>();
		Map<String,String> fittingMap = new HashMap<String, String>();
		Map<String,Object> condition = new HashMap<String, Object>();
		if(cacheMap == null){
			cacheMap = new HashMap<String, Map<String,String>>();
		}
		//去重
		if(!"".equals(fittingCodes)){
			int index = 0;
			for(String fittingCode : fittingCodes.split(",")){
				tempMap.put(fittingCode, fittingCounts.split(",")[index]);
				index++;
			}
		}
		if(!"".equals(detectCode)){//带有处理单号的校验
			if(cacheMap.containsKey(detectCode)){
				fittingMap = cacheMap.get(detectCode);
			}
			for(Map.Entry<String, String> m : tempMap.entrySet()){
				if(!fittingMap.containsKey(m.getKey())){
					condition.put("condition", "asdp.code='" + detectCode + "' AND fitting.code ='" + m.getKey() + "'");
					//校验当前的处理单号与配件编号 是否有记录
					Map<String,Object> detectFitting = detectFittingMapper.getAfterSaleDetectProductFitting(condition);
					//过滤掉不符合要求的记录
					if(detectFitting != null){
						condition.put("condition", "asdp.code='" + detectCode + "'");
						Map<String,Object> backUser = detectFittingMapper.getAfterSalebackUserDetect(condition);
						if(backUser != null){
							j.setMsg("该处理单[" + detectCode + "]已寄回用户,不能领取配件!");
							return j;
						}
						//更换用户商品情况下领用数不能大于损坏数
						if(StringUtil.toInt(target) == AfterSaleReceiveFitting.TARGET3){
							if(StringUtil.toInt(m.getValue()) > (Integer)detectFitting.get("damageCount")){
								j.setMsg("领取的配件[" + m.getKey() + "]数量大于该处理单[" + detectCode + "]中的损坏配件数!");
								return j;
							} else {
								fittingMap.put(m.getKey(), m.getValue());
							}
						} else {
							fittingMap.put(m.getKey(), m.getValue());
						}
					}
				}
			}
			cacheMap.put(detectCode, fittingMap);
		} else {
			if(cacheMap.containsKey("")){
				fittingMap = cacheMap.get("");
			}
			for(Map.Entry<String, String> m : tempMap.entrySet()){
				if(!fittingMap.containsKey(m.getKey())){
					condition.put("condition", "fitting.code='" + m.getKey() + "'");
					Map<String,Object> map = fittingMapper.getFittingName(condition);
					if(map != null){
						fittingMap.put(m.getKey(), m.getValue());
					}
				}
			}
			cacheMap.put("", fittingMap);
		}
		request.getSession().setAttribute("cacheMap", cacheMap);
		j.setSuccess(true);
		return j;
	}
	@SuppressWarnings("unchecked")
	public EasyuiDataGridBean getCacheAddFitting(HttpServletRequest request,String flag) {
		Map<String,Map<String,String>> cacheMap = (Map<String, Map<String,String>>) request.getSession().getAttribute("cacheMap");
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		List<Map<String,Object>> gridList = new ArrayList<Map<String,Object>>();
		if(cacheMap != null){
			if("empty".equals(flag)){
				 request.getSession().removeAttribute("cacheMap");
				 datagrid.setRows(gridList);
				 return datagrid;
			}
			gridList = getMapData(cacheMap);
			datagrid.setRows(gridList);
		}
		return datagrid;
	}
	private List<Map<String,Object>> getMapData(Map<String,Map<String,String>> cacheMap){
		List<Map<String,Object>> gridList = new ArrayList<Map<String,Object>>();
		Map<String,Object> condition = new HashMap<String, Object>();
		if(cacheMap != null){
			for(Map.Entry<String, Map<String,String>> cache : cacheMap.entrySet()){
				Map<String,String> childMap = cache.getValue();
				if(!"".equals(cache.getKey())){
					for(Map.Entry<String, String> child : childMap.entrySet()){
						Map<String,Object> gridMap = new HashMap<String, Object>();
						condition.put("condition", "asdp.code='" + cache.getKey() + "' and       fitting.code ='" + child.getKey() + "'");
						//校验当前的处理单号与配件编号 是否有记录
						Map<String,Object> detectFitting = detectFittingMapper.getAfterSaleDetectProductFitting(condition);
						if(detectFitting != null){
							gridMap.put("detectCode", detectFitting.get("detectCode"));
							gridMap.put("productOriName", detectFitting.get("productOriName"));
							gridMap.put("fittingName", detectFitting.get("fittingName"));
							gridMap.put("fittingId", detectFitting.get("fittingId"));
							gridMap.put("fittingCode", detectFitting.get("fittingCode"));
							gridMap.put("fittingCount", child.getValue());
							gridList.add(gridMap);
						}
					}
				} else {
					for(Map.Entry<String, String> child : childMap.entrySet()){
						Map<String,Object> gridMap = new HashMap<String, Object>();
						condition.put("condition", "fitting.code='" + child.getKey() + "'");
						Map<String,Object> map = fittingMapper.getFittingName(condition);
						if(map != null){
							gridMap.put("fittingName", map.get("fittingName"));
							gridMap.put("fittingId", map.get("fittingId"));
							gridMap.put("fittingCode", map.get("fittingCode"));
							gridMap.put("fittingCount", child.getValue());
							gridList.add(gridMap);
						}
					}
				}
			}
		}
		return gridList;
	}
	@SuppressWarnings("unchecked")
	public void delCacheFitting(HttpServletRequest request,String detectCode, String fittingCode) throws Exception {
		Map<String,Map<String,String>> cacheMap = (Map<String, Map<String,String>>) request.getSession().getAttribute("cacheMap");
		Map<String,String> fittingMap = new HashMap<String, String>();
		if(cacheMap != null){
			if(!"".equals(detectCode)){
				fittingMap = cacheMap.get(detectCode);
				fittingMap.remove(fittingCode);
				if(!fittingMap.isEmpty()){
					cacheMap.put(detectCode, fittingMap);
				} else {
					cacheMap.remove(detectCode);
				}
			} else {
				fittingMap = cacheMap.get("");
				fittingMap.remove(fittingCode);
				cacheMap.put("", fittingMap);
				if(!fittingMap.isEmpty()){
					cacheMap.put("", fittingMap);
				} else {
					cacheMap.remove("");
				}
			}
		}
	}
	public Json auditReceiveFitting(voUser user, String receiveId, String type,String remark) {
		Json j = new Json();
		try {
			AfterSaleReceiveFitting record = new AfterSaleReceiveFitting();
			if("1".equals(type)){
				record.setStatus((byte) AfterSaleReceiveFitting.STATUS2);
			} else if("2".equals(type)) {
				record.setStatus((byte) AfterSaleReceiveFitting.STATUS3);
			}
			record.setRemark(remark);
			record.setAuditUserId(user.getId());
			record.setAuditUserName(user.getUsername());
			record.setId(StringUtil.toInt(receiveId));
			int number = receiveMapper.updateByPrimaryKeySelective(record);
			if(number == 0){
				j.setMsg("审核失败!");
				return j;
			}
			j.setMsg("审核成功!");
			j.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	public List<Map<String,Object>> getReceiveFittingDetailDatagrid(String receiveId) {
		List<Map<String,Object>> detailList = null;
		try {
			Map<String,Object> condition = new HashMap<String, Object>();
			condition.put("condition", "id=" + receiveId);
			AfterSaleReceiveFitting receive = receiveMapper.getAfterSaleReceiveFitting(condition);
			if(receive == null){
				return detailList;
			}
			condition.put("condition", "asrfd.receive_fitting_id = " + receiveId);
			detailList = receiveDetailMapper.getReceiveFittingDetailList(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detailList;
	}
	public Json getReceiveFittingInfo(String receiveId) {
		Json j = new Json();
		try {
			Map<String,Object> condition = new HashMap<String, Object>();
			condition.put("condition", "id=" + receiveId);
			AfterSaleReceiveFitting receive = receiveMapper.getAfterSaleReceiveFitting(condition);
			if(receive == null){
				j.setMsg("领用单已不存在!");
				return j;
			}
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("createDatetime", receive.getCreateDatetime());
			data.put("createUserName", receive.getCreateUserName());
			data.put("remark", receive.getRemark());
			data.put("target", AfterSaleReceiveFitting.targetMap.get(receive.getTarget()));
			j.setObj(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	public EasyuiDataGridBean getReceiveFittingDatagrid(EasyuiPageBean page,String fittingName, String areaId, String status,String createUserName) {
		EasyuiDataGridBean grid = new EasyuiDataGridBean();
		StringBuffer buff = new StringBuffer();
		buff.append(" 1= 1");
		if(!"".equals(StringUtil.checkNull(fittingName))){
			buff.append(" AND fitting.name = '" + fittingName + "'");
		}
		if(!"".equals(StringUtil.checkNull(areaId)) && !"-1".equals(StringUtil.checkNull(areaId))){
			buff.append(" AND asrf.area_id = " + areaId);
		}
		if(!"".equals(StringUtil.checkNull(status))){
			buff.append(" AND asrf.status = " + status);
		}
		if(!"".equals(StringUtil.checkNull(createUserName))){
			buff.append(" AND asrf.create_user_name = '" + createUserName + "'");
		}
		Map<String,Object> condition = new HashMap<String, Object>();
		try {
			condition.put("condition", buff.toString());
			int total = receiveMapper.getAfterSaleReceiveFittingCount(condition);
			grid.setTotal((long) total);
			condition.put("orderBy", " asrf.create_datetime desc");
			condition.put("index", (page.getPage()-1)*page.getRows());
			condition.put("count", page.getRows());
			List<Map<String, Object>> rows = receiveMapper.getAfterSaleReceiveFittingList(condition);
			if(rows != null){
				for(Map<String, Object> row : rows){
					row.put("createDatetime", row.get("createDatetime").toString());
				}
			}
			grid.setRows(rows);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return grid;
	}
	/**
	 * 说明：生成配件领用单编号
	 * 日期：2014-07-03
	 * @author syuf
	 */
	public String newReceiveFittingCode(){
		
		String code = "LY"+DateUtil.getNow().substring(2,10).replace("-", "");
		Map<String,Object> condition = new HashMap<String, Object>();
		condition.put("condition", "code like '"+code+"%' order by id desc limit 1");
		AfterSaleReceiveFitting receive = receiveMapper.getAfterSaleReceiveFitting(condition);
		if(receive != null){
			String _code = receive.getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-4));
			number++;
			code += String.format("%04d",new Object[]{new Integer(number)});
		}else{
			code = code + "0001";
		}
		return code;
	}
	@Transactional(rollbackFor=Exception.class)
	public void addReceiveFitting(voUser user, String fittingIds,String detectCodes, String fittingCounts, String target, String areaId) {
		AfterSaleReceiveFitting receive = new AfterSaleReceiveFitting();
		receive.setAreaId(Short.parseShort(areaId));
		receive.setCode(this.newReceiveFittingCode());
		receive.setCreateDatetime(DateUtil.getNow());
		receive.setCreateUserId(user.getId());
		receive.setCreateUserName(user.getUsername());
		receive.setStatus((byte) AfterSaleReceiveFitting.STATUS1);
		receive.setTarget(Byte.parseByte(target));
		int receiveId = receiveMapper.insertSelective(receive);
		if(receiveId == 0){
			throw new RuntimeException("添加领用单失败!");
		}
		if(!"".equals(fittingIds)){
			for(int i = 0;i < fittingIds.split(",").length; i++){
				AfterSaleReceiveFittingDetail receiveDetail = new AfterSaleReceiveFittingDetail();
				receiveDetail.setFittingId(StringUtil.toInt(fittingIds.split(",")[i]));
				receiveDetail.setReceiveFittingId(receiveId);
				receiveDetail.setCount(StringUtil.toInt(fittingCounts.split(",")[i]));
				if(!"".equals(detectCodes)){
					Map<String,Object> condition = new HashMap<String, Object>();
					condition.put("condition", "asdp.code='" + detectCodes.split(",")[i] + "' and       fitting.id ='" + fittingIds.split(",")[i] + "'");
					Map<String,Object> detectFitting = detectFittingMapper.getAfterSaleDetectProductFitting(condition);
					if(detectFitting == null){
						throw new RuntimeException("处理单号[" + detectCodes.split(",")[i] + "]不正确!");
					}
					condition.put("condition", "asdp.code='" + detectCodes.split(",")[i] + "'");
					Map<String,Object> backUser = detectFittingMapper.getAfterSalebackUserDetect(condition);
					if(backUser != null){
						throw new RuntimeException("该处理单[" + detectCodes.split(",")[i] + "]已寄回用户,不能领取配件!");
					}
					receiveDetail.setDetectProductId(Integer.parseInt(detectFitting.get("detectId").toString()));
				}
				int number = receiveDetailMapper.insertSelective(receiveDetail);
				if(number == 0){
					throw new RuntimeException("添加领用单明细失败!");
				}
			}
		}
	}
	/**
	 * 获取配件列表页
	 * @auth aohaichen
	 */
	public List<Map<String,Object>> getBuyStockin(Map<String,Object> map){		
		return buyStockin.selectBuyStockinList(map);		
	}
	
	/**
	 * 获取配件列表总数
	 * @auth aohaichen
	 */
	public int getSelectBuyStockinListCount(Map<String,Object> map){
		return buyStockin.selectBuyStockinListCount(map);
	}
	/**
	 * 获取配件“入库确认”列表
	 * @auth aohaichen
	 */
	public List<Map<String,Object>> getBuyStockinConfirm(Map<String,Object> map){		
		return buyStockin.selectBuyStockinConfirmList(map);		
	}
	
	/**
	 * 获取配件“入库审核”列表
	 * @auth aohaichen
	 */
	public List<Map<String,Object>> getBuyStockinAudit(Map<String,Object> map){		
		return buyStockin.selectBuyStockinAuditList(map);		
	}
	
	
	/**
	 * 更新配件“入库确认”列表
	 * @auth aohaichen
	 */
	public int updateBuyStockinConfirm(Map<String,Object> map){		
		return buyStockin.updateBuyStockin(map);		
	}
	
	public String getStatusStr(String index){
		if("0".equals(index)){
			return "待确认";
		}
		if("1".equals(index)){
			return "确认未通过";
		}
		if("3".equals(index)){
			return "待审核";
		}
		if("5".equals(index)){
			return "审核未通过";
		}
		if("4".equals(index)){
			return "已完成";
		}
		return index;
		
	}

	/**
	 * 添加日志
	 * @auth aohaichen
	 */
	public int addBuyAdminHistory(BuyAdminHistoryBean log){
		return buyAdminHistroyDao.insert(log);
		
	}

	
	/**
	 * 获取领用单信息
	 * @param id
	 * @return
	 * @author lining
	* @date 2014-7-4
	 */
	public AfterSaleReceiveFitting getReceiveFitting(int id){
		return receiveMapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 根据领用单id获取配件信息
	 * @param receiveFittingId
	 * @return
	 * @author lining
	* @date 2014-7-4
	 */
	public List<Map<String,Object>> getReceiveFittingDetails(int receiveFittingId){
		return receiveDetailMapper.getReceiveFittingDetails(receiveFittingId);
	}
}
