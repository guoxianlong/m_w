/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年8月11日 下午6:26:29 
 * @version V1.0   
 */
package cn.mmb.delivery.application;

import java.util.List;

import javax.annotation.Resource;

import mmb.stock.stat.DeliverCorpInfoBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import cn.mmb.delivery.domain.model.WayBillTrace;
import cn.mmb.delivery.domain.service.WayBillService;
import cn.mmb.delivery.domain.service.WayBillServiceFactory;
/** 
 * @ClassName: WayBillApplication 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2015年8月11日 下午6:26:29  
 */
@Component("wayBillApplication")
public class WayBillApplication {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	@Resource
	private WayBillServiceFactory wayBillServiceFactory;
	
	/** 
	 * @Description: 获取圆通面单配送信息
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月10日 下午3:07:38 
	 */
	public void getWayBillDispatching() throws Exception {
		WayBillService wayBillService = wayBillServiceFactory.create(DeliverCorpInfoBean.DELIVER_ID_YT_WX);
		WayBillTrace wayBillTrace = wayBillService.getWayBillTrace();
		
		if (!wayBillTrace.getTraceInfo().isEmpty()) {
			List<WayBillTrace> rs = wayBillService.getNeedAddWayBillInfo(wayBillTrace);
			String result = wayBillService.updateWayBillInfo(rs);
			log.info("getWayBillDispatchingJob result:"+result);
		}
	}
	
	/**
	* @Description: 取消订单信息
	* @author ahc
	 * @throws Exception 
	 */
	public String cancelWayBills(List<Integer> deliverIds, List<String> orderCodes){
		for(int i = 0 ; i < deliverIds.size() ; i++){
			int deliverId = deliverIds.get(i);
			String orderCode = orderCodes.get(i);
			
			WayBillService wayBillService = wayBillServiceFactory.create(deliverId);
			if(deliverId==DeliverCorpInfoBean.DELIVER_ID_RFD_SD || deliverId==DeliverCorpInfoBean.DELIVER_ID_YD || 
				deliverId==DeliverCorpInfoBean.DELIVER_ID_JD_CD || deliverId==DeliverCorpInfoBean.DELIVER_ID_JD_WX ||
				deliverId==DeliverCorpInfoBean.DELIVER_ID_YT_CD || deliverId==DeliverCorpInfoBean.DELIVER_ID_YT_WX ){
				try {
					/**
					 * 发送"取消订单"接口
					 */
					int result =wayBillService.cancelWayBill(deliverId,orderCode);
					return "success";
				} catch (Exception e) {
					e.printStackTrace();
					return e.getMessage();
				}
			}
		}
		
		return null;
	}
}
