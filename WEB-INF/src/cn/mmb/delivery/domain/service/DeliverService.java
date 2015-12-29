package cn.mmb.delivery.domain.service;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.util.DateUtil;
import cn.mmb.delivery.domain.model.WayBill;
import cn.mmb.delivery.domain.model.vo.DeliverSwitchBean;
import cn.mmb.delivery.infrastructrue.persistence.DeliverDao;

@Service
public class DeliverService {
	
	@Resource
	private WayBillServiceFactory wayBillServiceFactory;
	
	@Resource
	private DeliverDao deliverDao;
	
	public int getDeliverSwitchCount(Map<String,String> map){
		
		return deliverDao.deliverSwitchCount(map);
	}
	
	public List<DeliverSwitchBean> getDeliverSwitchList(Map<String,String> map){
		
		return deliverDao.deliverSwitchList(map);
	}
	
	public Map<String,Object> getDeliver(String orderCode){
		
		return deliverDao.getDeliver(orderCode);
	}
	
	public OrderStockBean getOrderStock(String orderCode){
		
		return deliverDao.getOrderStock(orderCode);
	}
	
	@Transactional
	public void switchDeliver(int sourceDeliver,int targetDeliver,String remark,voOrder order,voUser user) throws Exception{
		
		int result = 0;
		
		/**
		 * 记录切换前 快递公司和包裹单号
		 */
		DeliverSwitchBean dsb = new DeliverSwitchBean();
		OrderStockBean os = deliverDao.getOrderStock(order.getCode());
		Map<String,Object> map =deliverDao.getDeliver(os.getOrderCode());
		String stockName =deliverDao.getStockAreaByName(os.getStockArea());
		dsb.setStockArea(stockName);
		dsb.setOrderCode(order.getCode());
		dsb.setOriginDeliverName(String.valueOf(map.get("name")));
		dsb.setOriginPackageCode(String.valueOf(map.get("package_code")));
		
		/**
		 * 调用取消接口
		 */
		WayBillService wayBillService = wayBillServiceFactory.create(sourceDeliver);
		result =wayBillService.cancelWayBill(sourceDeliver,order.getCode());
		if(result<=0){
			throw new RuntimeException( "调用取消接口失败！");
			
		}
		
		/**
		 * 调用下单接口
		 */
		wayBillService = wayBillServiceFactory.create(targetDeliver);
		List<WayBill> sendData = wayBillService.getNeedWayBillInfo(targetDeliver,order.getCode());
		if(sendData!=null ){
			//发送并接收报文
			List<String> resultList = wayBillService.sendWayBillInfo(sendData);
			
			//封装报文
			List<WayBill> list = wayBillService.parseToWayBill(sendData, resultList);
			
			//添加关联信息
			wayBillService.addDeliverRelation(targetDeliver, list);
		}
		
		/**
		 * 更新订单
		 */
		result = deliverDao.updateUserOrder(targetDeliver,order.getCode());
		if(result<=0){
			throw new RuntimeException( "修改订单表快递失败！");
		}
		/**
		 * 更新出库单
		 */
		result = deliverDao.updateOrderStock(targetDeliver,order.getCode());
		if(result<=0){
			throw new RuntimeException ("修改出库单快递失败！");
		}
		
		/**
		 * 更新复核列表
		 */
		deliverDao.updateAuditPackage(targetDeliver,order.getCode(),order.getBuyMode());
		
		/**
		 * 记录切换后 快递公司和裹单号
		 */
		OrderStockBean os2 = deliverDao.getOrderStock(order.getCode());
		Map<String,Object> map2 =deliverDao.getDeliver(os2.getOrderCode());
		dsb.setDeliverName(String.valueOf(map2.get("name")));
		dsb.setPackageCode(String.valueOf(map2.get("package_code")));
		dsb.setCreateDateTime(order.getCreateDatetime()+"");
		dsb.setModifyDatetime(DateUtil.getNow());
		dsb.setRemark(remark);
		dsb.setOperUserId(user.getId()+"");
		dsb.setOperUserName(user.getUsername());
		
		/**
		 * 保存切换记录
		 */
		result =deliverDao.addDeliverSwitch(dsb);
		if(result<=0){
			throw new RuntimeException( "添加切换记录失败！");
		}
	}
	
	
}
