package cn.mmb.delivery.application;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.mmb.delivery.domain.service.DeliveryOrderService;
import cn.mmb.delivery.infrastructrue.persistence.WayBillMapper;

@Component("DeliveryOrderFacade")
public class DeliveryOrderFacade {
	
	@Resource
	private DeliveryOrderService deliveryOrderService;
	
	@Resource
	private WayBillMapper wayBillMapper;
	
	/**
	 * 如果运单状态里有[妥投]、[拒收]，两种状态时，需要将对应订单的状态更新为：妥投或者拒收。
	 * @author yaoliang 
	 * @create 2015年5月7日 上午8:40:22
	 * @param orderId 要修改的订单id
	 * @param deliverStatus 配送状态
	 */
	public int updateOrderStatus(int orderId, int deliverStatus) throws Exception {
		return deliveryOrderService.updateOrderStatus(orderId, deliverStatus);
	}
	
	/**
	 * 如果运单状态里有[妥投]、[拒收]，两种状态时，需要将对应订单的时间更新为当前时间
	 * @author yaoliang 
	 * @create 2015年5月15日 上午8:40:22
	 * @param orderCodePOP 订单号
	 * @param status 状态
	 */
	public int updatePOPOrderInfo(String orderCodePOP, int status) throws Exception {
		return deliveryOrderService.updatePOPOrderInfo(orderCodePOP, status);
	}
	/**
	* @Description: 关联某个时间段之间 快递公司和订单编号
	* @author ahc
	 */
	public void relationDeliver(int deliverIds[],String startTime,String endTime){
		deliveryOrderService.addDeliverRelation(deliverIds,startTime,endTime);
	}
	
}
