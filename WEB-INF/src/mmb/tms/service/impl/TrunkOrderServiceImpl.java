package mmb.tms.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.tms.dao.TrunkOrderDao;
import mmb.tms.model.TrunkOrder;
import mmb.tms.model.TrunkOrderInfo;
import mmb.tms.service.ITrunkOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrunkOrderServiceImpl implements ITrunkOrderService{

	@Autowired
	private TrunkOrderDao trunkOrderDao;
	
	/**
	 * 查询干线单信息
	 */
	@Override
	public EasyuiDataGridJson qryTrunkOrder(Map<String, Object> map) {
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		List<TrunkOrder> rsList = trunkOrderDao.qryTrunkOrderLs(map);
		if(rsList!=null&&rsList.size()>0){
			//当前时间
			Date currentDate = Calendar.getInstance().getTime();
			for(TrunkOrder trunkOrder:rsList){
				if("5".equals(trunkOrder.getStatus())){
					trunkOrder.setRemainTime("0");
					if(trunkOrder.getNodeTime().getTime()>trunkOrder.getExpectTime().getTime()){
						trunkOrder.setOverTime("1");
					}else{
						trunkOrder.setOverTime("0");
					}
				}else{
					trunkOrder.setRemainTime(this.converRemainTime(currentDate, trunkOrder.getExpectTime()));
					if(currentDate.getTime()>trunkOrder.getExpectTime().getTime()){
						trunkOrder.setOverTime("1");
					}else{
						trunkOrder.setOverTime("0");
					}
				}
			}
		}
		json.setRows(rsList);
		json.setTotal(trunkOrderDao.qryTrunkOrderLsTotal(map));
		return json;
	}

	/**
	 * 查询干线单详细信息
	 */
	@Override
	public EasyuiDataGridJson qryMailingBatchPackage(Map<String, Object> map) {
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		json.setRows(trunkOrderDao.qryMailingBatchPackage(map));
		json.setTotal(trunkOrderDao.qryMailingBatchPackageTotal(map));
		return json;
	}
	
	/**
	 * 修改干线单信息
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Json modifyTrunkInfo(Map<String, Object> map) {
		Json json = new Json();
		//当前时间
		Date currentDate = Calendar.getInstance().getTime();
		//干线单信息
		TrunkOrder trunkOrder=new TrunkOrder();
		trunkOrder.setId((Integer)map.get("trunkOrderId"));
		trunkOrder.setStatus((String)map.get("status"));
		trunkOrder.setNodeTime(currentDate);
		trunkOrder.setUpdTime(currentDate);
		trunkOrder.setSize((Float)map.get("size"));
		trunkOrder.setWeight((Float)map.get("weight"));
		trunkOrder.setMode((Integer)map.get("mode"));
		trunkOrder.setSysOpUser((Integer)map.get("sysOpUser"));
		trunkOrderDao.updateByPrimaryKeySelective(trunkOrder);
		
		TrunkOrder newTrunkOrder = trunkOrderDao.qryTrunkOrderByPK(trunkOrder.getId());
		//干线单日志
		TrunkOrderInfo trunkOrderInfo = new TrunkOrderInfo();
		trunkOrderInfo.setTrunkOrderId(trunkOrder.getId());
		trunkOrderInfo.setStatus(trunkOrder.getStatus());
		trunkOrderInfo.setWeight(newTrunkOrder.getWeight());
		trunkOrderInfo.setSize(trunkOrder.getSize());
		trunkOrderInfo.setWeight(trunkOrder.getWeight());
		trunkOrderInfo.setMode(trunkOrder.getMode());
		trunkOrderInfo.setNodeTime(currentDate);
		trunkOrderInfo.setOpUser(newTrunkOrder.getOpUser());
		trunkOrderInfo.setSysOpUser((Integer)map.get("sysOpUser"));
		trunkOrderDao.insertTrunkOrderInfo(trunkOrderInfo);
		
		json.setSuccess(true);
		json.setMsg("修改成功");
		return json;
	}


	@Override
	public EasyuiDataGridJson qryTrunkOrderInfo(Map<String, Object> map) {
		EasyuiDataGridJson json = new EasyuiDataGridJson();
		json.setRows(trunkOrderDao.qryTrunkOrderInfoLs(map));
		json.setTotal(trunkOrderDao.qryTrunkOrderInfoLsTotal(map));
		return json;
	}
	
	/*
	 * 计算剩余时间
	 */
	private String converRemainTime(Date currentDate,Date expectDate){
		if(expectDate.getTime()-currentDate.getTime()>0){
			long l=expectDate.getTime()-currentDate.getTime();
			long day=l/(24*60*60*1000);
			long hour=(l/(60*60*1000)-day*24); 
			long min=((l/(60*1000))-day*24*60-hour*60); 
			long sec=(l/1000-day*24*60*60-hour*60*60-min*60);
			StringBuilder sb = new StringBuilder("");
			if(day>0){
				sb.append(day+"天");
			}
			if(hour>0){
				sb.append(hour+"小时");
			}
			if(min>0){
				sb.append(min+"分");
			}
			if(sec>0){
				sb.append(sec+"秒");
			}
			return sb.toString();
		}else{
			return "0";
		}
	}
}
