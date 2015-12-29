package com.mmb.ware.service.impl;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.framework.amqp.core.AmqpTemplate;
import org.framework.amqp.core.Message;
import org.framework.amqp.core.MessageDeliveryMode;
import org.framework.amqp.core.MessageProperties;
import org.framework.amqp.utils.SerializationUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import adultadmin.action.vo.voOrder;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.service.infc.IStockService;
import com.mmb.ware.service.MessageService;


@Service(value="logisticsCostMessage")
public class LogisticsCostMessage implements MessageService {
	
	
	private Log debugLog = LogFactory.getLog("debug.Log");

    @Resource
    private AmqpTemplate amqpTemplate;

    @Transactional
    public void sendMessage(Object msg) {
        MessageProperties msgProperties = new MessageProperties();
        msgProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        msgProperties.setContentType(MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT);
        try {
            Message message = new Message(SerializationUtils.serialize(msg),msgProperties);
            amqpTemplate.send("amq.direct", "finance_ware", message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    
    public void calLogisticsCost(IStockService stockService, voOrder order,
			AuditPackageBean apBean) {
    	
    	if(debugLog.isInfoEnabled()){
    		debugLog.info("ware开始发送消息");
    	}
		
		try{
			
			int deliver1 = order.getDeliver();
			int balanceType = 0;
			// 根据 快递公司，确定结算来源
			if(voOrder.deliverToBalanceTypeMap.containsKey(deliver1+"")){
				balanceType=Integer.parseInt(voOrder.deliverToBalanceTypeMap.get(deliver1+"").toString());
			}
			stockService.getDbOp().prepareStatement("SELECT add_id1,add_id2,add_id3 FROM user_order_extend_info WHERE order_code = '"+ order.getCode() + "'");
			PreparedStatement ps3 = stockService.getDbOp().getPStmt();
			ResultSet rs3 = ps3.executeQuery();
			String destProvince = null;
			String destCity = null;
			String destDistrict = null;
			while(rs3.next()){
				destProvince = rs3.getString("add_id1");
				destCity = rs3.getString("add_id2");
				destDistrict = rs3.getString("add_id3");
			}
			ps3.close();
			rs3.close();
			
			
			Map<String,String> argMap = new HashMap<String, String>();
			argMap.put("orderId", ""+order.getId());
			argMap.put("price", ""+order.getDprice());
			argMap.put("weight", ""+apBean.getWeight());
			argMap.put("express", ""+balanceType);
			argMap.put("balanceArea", ""+apBean.getAreano());
			argMap.put("destProvince", destProvince);
			argMap.put("destCity", destCity);
			argMap.put("buyMode", ""+order.getBuyMode());
			argMap.put("destArea", destDistrict);
			if(debugLog.isInfoEnabled()){
				debugLog.info("orderId:"+order.getId());
				debugLog.info("price:"+order.getDprice());
				debugLog.info("weight:"+apBean.getWeight());
				debugLog.info("express:"+balanceType);
				debugLog.info("balanceArea:"+apBean.getAreano());
				debugLog.info("destProvince:"+ destProvince);
				debugLog.info("destCity:"+destCity);
				debugLog.info("buyMode:"+order.getBuyMode());
				debugLog.info("destArea:"+destDistrict);
				debugLog.info("------------------------------");
			}
			
//			FinanceCache.addCharge(argMap, stockService.getDbOp());
			this.sendMessage(argMap);
			if(debugLog.isInfoEnabled()){
	    		debugLog.info("ware发送消息结束");
	    	}
		}catch(Exception e){
			System.out.println("系统生成物流成本失败");
			e.printStackTrace();
		}
	}
}
