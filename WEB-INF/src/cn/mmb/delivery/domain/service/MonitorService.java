package cn.mmb.delivery.domain.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.delivery.domain.PopOrderInfo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voSelect;
import cn.mmb.delivery.domain.model.vo.MonitorQueryParamBean;
import cn.mmb.delivery.infrastructrue.persistence.MonitorDao;

@Service
public class MonitorService {
	
	@Resource
	private MonitorDao monitorDao;
	
	/**
	 * 获取总数量
	 * @param queryParam
	 * @return
	 */
	public int countPopOrder(MonitorQueryParamBean queryParam){
		
		return monitorDao.countPopOrder(queryParam);
	}
	
	/**
	 * @description 获取面单信息反馈列表
	 * @param queryParam
	 * @param offset
	 * @param pageSize
	 * @return
	 */
	public List<PopOrderInfo> getPopOrderList(MonitorQueryParamBean queryParam,int offset,int pageSize){
		
		return monitorDao.getPopOrderList(queryParam, offset, pageSize);
	}
	
	/**
	 * 手动处理发送失败面单，将发送状态改为未发送
	 * @param ids
	 * @return
	 */
	@Transactional(rollbackFor=Exception.class)
	public int updateSendStatus(String ids,String failedReasons) throws Exception{
		return monitorDao.updateSendStatus(ids,failedReasons);
	}

	/**
	 * @return pop参数map
	 * @throws Exception 
	 * @author yaoliang
	 * @time 2015-09-21
	 */
	public Map<Integer, String> getParamPOP() {
		Map<Integer,String> popMap = new HashMap<Integer,String>();
		popMap.put(0, "请选择");
		popMap.put(9, "买卖宝-成都");
		popMap.put(4, "买卖宝-无锡");
		popMap.put(5, "京东");
		return popMap;
	}

	/**
	 * @return delivery参数map
	 * @throws Exception 
	 * @author yaoliang
	 * @param pop 
	 * @time 2015-09-21
	 */
	public List<voSelect> getParamDelivery(int pop) {
		return monitorDao.getParamDelivery(pop);
	}
}
