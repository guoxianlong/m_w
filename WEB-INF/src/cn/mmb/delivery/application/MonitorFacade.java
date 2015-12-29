package cn.mmb.delivery.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.delivery.domain.PopOrderInfo;

import org.springframework.stereotype.Component;

import adultadmin.action.vo.voSelect;
import cn.mmb.delivery.domain.model.vo.MonitorQueryParamBean;
import cn.mmb.delivery.domain.service.MonitorService;
import cn.mmb.delivery.util.Page;

@Component
public class MonitorFacade {

	@Resource
	private MonitorService monitorService;
	
	/**
	 * @description 获取面单信息反馈列表
	 * @param queryParam
	 * @param offset
	 * @param pageSize
	 * @return
	 */
	public Page<PopOrderInfo> getPopOrderList(MonitorQueryParamBean queryParam,int pageNum){
		if(queryParam.getSendStatus()==2&&queryParam.getFailedReason()!=0){
			Page<PopOrderInfo> page = new Page<PopOrderInfo>(0,1,20);
			List<PopOrderInfo> list = new ArrayList<PopOrderInfo>();
			page.setList(list);
			return page;
		}
		int totalRecord = monitorService.countPopOrder(queryParam);
		if(pageNum <= 0){
			pageNum = 1;
		}
		
		Page<PopOrderInfo> page = new Page<PopOrderInfo>(totalRecord,pageNum,20);
		List<PopOrderInfo> list = monitorService.getPopOrderList(queryParam,page.getOffset(),page.getPageSize());
		page.setList(list);
		
		return page;
	}
	
	/**
	 * 手动处理发送失败面单，将发送状态改为未发送
	 * @param ids
	 * @return
	 * @throws Exception 
	 */
	public int updateSendStatus(String ids,String failedReasons) throws Exception{
		return monitorService.updateSendStatus(ids,failedReasons);
	}

	/**
	 * @return pop参数map
	 * @throws Exception 
	 * @author yaoliang
	 * @time 2015-09-21
	 */
	public Map<Integer, String> getParamPOP() {
		return monitorService.getParamPOP();
	}

	/**
	 * @return delivery参数map
	 * @throws Exception 
	 * @author yaoliang
	 * @param pop 
	 * @time 2015-09-21
	 */
	public List<voSelect> getParamDelivery(int pop) {
		return monitorService.getParamDelivery(pop);
	}
}
