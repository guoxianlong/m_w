package cn.mmb.delivery.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import mmb.delivery.domain.PopOrderInfo;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import adultadmin.action.vo.voSelect;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.application.MonitorFacade;
import cn.mmb.delivery.domain.model.vo.MonitorQueryParamBean;
import cn.mmb.delivery.util.Page;

@Controller
@RequestMapping("/monitoringController")
public class MonitoringController {

	@Resource
	private MonitorFacade monitorFacade;
	
	/**
	 * 面单信息反馈监控列表
	 * @param queryParam
	 * @param map
	 * @param pageNum
	 * @return
	 */
	@RequestMapping("/popWaybillFeedback")
	public ModelAndView popWaybillFeedback(int flag,MonitorQueryParamBean queryParam,ModelMap map,String pageNum){
		ModelAndView model = new ModelAndView();
		model.addObject("flag",flag);
		if(flag == 1){
			queryParam.setPop(MonitorQueryParamBean.POP5);
			model.setViewName("monitor/page/popWaybillFeedback");
		}else{
			Map<Integer,String> popMap = monitorFacade.getParamPOP();
			map.put("popMap", popMap);
			
			List<voSelect> deliveryList = this.getParamDelivery(queryParam.getPop());
			map.put("deliveryList",deliveryList);
			model.setViewName("monitor/page/mmbWaybillFeedback");
		}
		
		int nextPage = 0;
		if(StringUtils.isNotBlank(pageNum)){
			nextPage = Integer.parseInt(pageNum);
		}
		Page<PopOrderInfo> page = monitorFacade.getPopOrderList(queryParam, nextPage);
		
		map.put("page", page);
		map.put("queryParam", queryParam);
		model.addAllObjects(map);
		
		return model;
	}
	
	/**
	 * @return delivery集合
	 * @throws Exception 
	 * @author yaoliang
	 * @param pop 
	 * @time 2015-09-21
	 */
	@RequestMapping("/getParamDelivery")
	@ResponseBody
	public List<voSelect> getParamDelivery(int pop){
		List<voSelect> deliveryList = monitorFacade.getParamDelivery(pop);
		return deliveryList;
	}
	
	
	
	/**
	 * 手动处理失败面单
	 * @param ids
	 * @return
	 */
	@RequestMapping("/manualHandleOrder")
	@ResponseBody
	public String manualHandleOrder(String ids,String failedReasons){
		if(StringUtils.isBlank(ids)){
			return "dataError";
		}else if(ids.endsWith(",")){
			ids = ids.substring(0, ids.length()-1);
		}
		
		//更新状态
		int result = 0;
		try {
			result = monitorFacade.updateSendStatus(ids,failedReasons);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result > 0){
			return "success";
		}else {
			return "fail";
		}
	}
}
