package cn.mmb.delivery.application;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.order.OrderStockBean;
import cn.mmb.delivery.domain.model.vo.DeliverSwitchBean;
import cn.mmb.delivery.domain.service.DeliverService;

@Component
public class DeliverApplication {

	@Resource
	private DeliverService deliverService;
	
	/**
	 * 获取切换快递公司列表总数
	* @Description: 
	* @author ahc
	 */
	public int getDeliverSwitchCount(Map<String,String> map ){
		
		return deliverService.getDeliverSwitchCount(map);
	}
	
	/**
	 * 获取切换快递公司列表
	* @Description: 
	* @author ahc
	 */
	public List<DeliverSwitchBean> getDeliverSwitchList(Map<String,String> map ){
		
		return deliverService.getDeliverSwitchList(map);
	}
	
	/**
	 * 获取切换快递公司列表
	* @Description: 
	* @author ahc
	 */
	public Map<String,Object> getDeliver(String OrderCode ){
		
		return deliverService.getDeliver(OrderCode);
	}
	
	/**
	 * 获取出库单
	* @Description: 
	* @author ahc
	 */
	public OrderStockBean getOrderStock(String OrderCode ){
		
		return deliverService.getOrderStock(OrderCode);
	}
	
	/**
	 * 切换快递公司
	* @Description: 
	* @author ahc
	* @param SourceDeliver:原快递公司
	* @param TargetDeliver：最终快递公司
	 * @throws Exception 
	 */ 
	public void switchDeliver(int sourceDeliver,int targetDeliver,String remark,voOrder order,voUser user) throws Exception{
		deliverService.switchDeliver(sourceDeliver,targetDeliver,remark,order,user);
	}
	
}
