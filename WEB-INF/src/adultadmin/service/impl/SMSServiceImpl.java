/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import adultadmin.bean.sms.EaseMessage2Bean;
import adultadmin.bean.sms.EaseMessageBean;
import adultadmin.bean.sms.OrderDealBean;
import adultadmin.bean.sms.OrderMessage2Bean;
import adultadmin.bean.sms.OrderMessage3Bean;
import adultadmin.bean.sms.OrderMessageBean;
import adultadmin.bean.sms.ReceiveMessageBean;
import adultadmin.bean.sms.ReceiveMessageHistoryBean;
import adultadmin.bean.sms.SendMessage2Bean;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.bean.sms.SendMessageAutoBean;
import adultadmin.bean.sms.SendMessageAutoHistoryBean;
import adultadmin.bean.sms.SendMessageOrderIdBean;
import adultadmin.bean.sms.ShortmessageBean;
import adultadmin.bean.sms.ShortmessageHistoryBean;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2010-06-17
 * 
 * 说明：短信收发相关操作
 */
public class SMSServiceImpl extends BaseServiceImpl implements ISMSService {
	
	public SMSServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public SMSServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	//下行短信(sms_订单失败短信)
	public boolean addShortmessage(ShortmessageBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"shortmessage");
	}

	public boolean deleteShortmessage(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"shortmessage");
	}

	public ShortmessageBean getShortmessage(String condition) {
		return (ShortmessageBean) getXXX(condition, DbOperation.SCHEMA_SMS+"shortmessage",
				"adultadmin.bean.sms.ShortmessageBean");
	}

	public int getShortmessageCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"shortmessage", "id");
	}

	public ArrayList getShortmessageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"shortmessage",
				"adultadmin.bean.sms.ShortmessageBean");
	}

	public boolean updateShortmessage(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"shortmessage");
	}

	//下行短信历史记录(sms_订单失败短信)
	public boolean addShortmessageHistory(ShortmessageHistoryBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"shortmessage_history");
	}

	public boolean deleteShortmessageHistory(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"shortmessage_history");
	}

	public ShortmessageHistoryBean getShortmessageHistory(String condition) {
		return (ShortmessageHistoryBean) getXXX(condition, DbOperation.SCHEMA_SMS+"shortmessage_history",
				"adultadmin.bean.sms.ShortmessageHistoryBean");
	}

	public int getShortmessageHistoryCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"shortmessage_history", "id");
	}

	public ArrayList getShortmessageHistoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"shortmessage_history",
				"adultadmin.bean.sms.ShortmessageHistoryBean");
	}

	public boolean updateShortmessageHistory(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"shortmessage_history");
	}
	
	//上行短信(sms_订单失败短信)
	public boolean addReceiveMessage(ReceiveMessageBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"receive_message");
	}

	public boolean deleteReceiveMessage(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"receive_message");
	}

	public ReceiveMessageBean getReceiveMessage(String condition) {
		return (ReceiveMessageBean) getXXX(condition, DbOperation.SCHEMA_SMS+"receive_message",
				"adultadmin.bean.sms.ReceiveMessageBean");
	}

	public int getReceiveMessageCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"receive_message", "id");
	}

	public ArrayList getReceiveMessageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"receive_message",
				"adultadmin.bean.sms.ReceiveMessageBean");
	}

	public boolean updateReceiveMessage(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"receive_message");
	}

	//上行短信历史记录(sms_订单失败短信)
	public boolean addReceiveMessageHistory(ReceiveMessageHistoryBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"receive_message_history");
	}

	public boolean deleteReceiveMessageHistory(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"receive_message_history");
	}

	public ReceiveMessageHistoryBean getReceiveMessageHistory(String condition) {
		return (ReceiveMessageHistoryBean) getXXX(condition, DbOperation.SCHEMA_SMS+"receive_message_history",
				"adultadmin.bean.sms.ReceiveMessageHistoryBean");
	}

	public int getReceiveMessageHistoryCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"receive_message_history", "id");
	}

	public ArrayList getReceiveMessageHistoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"receive_message_history",
				"adultadmin.bean.sms.ReceiveMessageHistoryBean");
	}

	public boolean updateReceiveMessageHistory(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"receive_message_history");
	}
	
	//订单下行短信(sms_订单失败短信)
	public boolean addOrderMessage(OrderMessageBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"order_message");
	}

	public boolean deleteOrderMessage(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"order_message");
	}

	public OrderMessageBean getOrderMessage(String condition) {
		return (OrderMessageBean) getXXX(condition, DbOperation.SCHEMA_SMS+"order_message",
				"adultadmin.bean.sms.OrderMessageBean");
	}

	public int getOrderMessageCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"order_message", "id");
	}

	public ArrayList getOrderMessageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"order_message",
				"adultadmin.bean.sms.OrderMessageBean");
	}
	
	public boolean updateOrderMessage(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"order_message");
	}
	
	//电话失败订单处理(sms_订单失败短信)
	public boolean addOrderDeal(OrderDealBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"order_deal");
	}

	public boolean deleteOrderDeal(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"order_deal");
	}

	public OrderDealBean getOrderDeal(String condition) {
		return (OrderDealBean) getXXX(condition, DbOperation.SCHEMA_SMS+"order_deal",
				"adultadmin.bean.sms.OrderDealBean");
	}

	public OrderDealBean getOrderDealCascaded(String condition){
		OrderDealBean bean = null;
		
		DbOperation dbOp = getDbOp();
        if (!dbOp.init() || !dbOp.setFetchSize(1)) {
            return bean;
        }
        ResultSet rs = null;

        StringBuilder query = new StringBuilder(150);
        query.append("select d.id,d.order_id from sms.order_deal d join shop.user_order uo on d.order_id = uo.id where ");
        query.append(condition);
        rs = dbOp.executeQuery(query.toString());
        if (rs == null) {
            release(dbOp);
            return null;
        }
        try{
        	if(rs.next()){
        		bean = new OrderDealBean();
        		bean.setId(rs.getInt("id"));
        		bean.setOrderId(rs.getInt("order_id"));
        	}
        	
        }catch (Exception e) {
            e.printStackTrace();
            release(dbOp);
            return null;
        }
        //释放数据库连接
        release(dbOp);
		
		return bean; 
	}
	
	public int getOrderDealCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"order_deal", "id");
	}

	public ArrayList getOrderDealList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"order_deal",
				"adultadmin.bean.sms.OrderDealBean");
	}
	
	public boolean updateOrderDeal(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"order_deal");
	}
	
	//自动发送短信设置(sms_订单失败短信)
	public boolean addSendMessageAuto(SendMessageAutoBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"send_message_auto");
	}

	public boolean deleteSendMessageAuto(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"send_message_auto");
	}

	public SendMessageAutoBean getSendMessageAuto(String condition) {
		return (SendMessageAutoBean) getXXX(condition, DbOperation.SCHEMA_SMS+"send_message_auto",
				"adultadmin.bean.sms.SendMessageAutoBean");
	}

	public int getSendMessageAutoCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"send_message_auto", "id");
	}

	public ArrayList getSendMessageAutoList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"send_message_auto",
				"adultadmin.bean.sms.SendMessageAutoBean");
	}
	
	public boolean updateSendMessageAuto(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"send_message_auto");
	}
	
	//自动发送短信设置(sms_订单失败短信)
	public boolean addSendMessageAutoHistory(SendMessageAutoHistoryBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"send_message_auto_history");
	}

	public boolean deleteSendMessageAutoHistory(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"send_message_auto_history");
	}

	public SendMessageAutoHistoryBean getSendMessageAutoHistory(String condition) {
		return (SendMessageAutoHistoryBean) getXXX(condition, DbOperation.SCHEMA_SMS+"send_message_auto_history",
				"adultadmin.bean.sms.SendMessageAutoHistoryBean");
	}

	public int getSendMessageAutoHistoryCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"send_message_auto_history", "id");
	}

	public ArrayList getSendMessageAutoHistoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"send_message_auto_history",
				"adultadmin.bean.sms.SendMessageAutoHistoryBean");
	}
	
	public boolean updateSendMessageAutoHistory(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"send_message_auto_history");
	}
	
	
	//下行短信(sms2_群发短信)
	public boolean addShortmessage2(ShortmessageBean bean) {
		return addXXX(bean, "shortmessage54");
	}

	public boolean deleteShortmessage2(String condition) {
		return deleteXXX(condition, "shortmessage54");
	}

	public ShortmessageBean getShortmessage2(String condition) {
		return (ShortmessageBean) getXXX(condition, "shortmessage54",
				"adultadmin.bean.sms.ShortmessageBean");
	}

	public int getShortmessage2Count(String condition) {
		return getXXXCount(condition, "shortmessage54", "id");
	}

	public ArrayList getShortmessage2List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "shortmessage54",
				"adultadmin.bean.sms.ShortmessageBean");
	}

	public boolean updateShortmessage2(String set, String condition) {
		return updateXXX(set, condition, "shortmessage54");
	}

	//下行短信历史记录(sms2_群发短信)
	public boolean addShortmessageHistory2(ShortmessageHistoryBean bean) {
		return addXXX(bean, "shortmessage_history54");
	}

	public boolean deleteShortmessageHistory2(String condition) {
		return deleteXXX(condition, "shortmessage_history54");
	}

	public ShortmessageHistoryBean getShortmessageHistory2(String condition) {
		return (ShortmessageHistoryBean) getXXX(condition, "shortmessage_history54",
				"adultadmin.bean.sms.ShortmessageHistoryBean");
	}

	public int getShortmessageHistory2Count(String condition) {
		return getXXXCount(condition, "shortmessage_history54", "id");
	}

	public ArrayList getShortmessageHistory2List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "shortmessage_history54",
				"adultadmin.bean.sms.ShortmessageHistoryBean");
	}

	public boolean updateShortmessageHistory2(String set, String condition) {
		return updateXXX(set, condition, "shortmessage_history54");
	}
	
	//上行短信(sms2_群发短信)
	public boolean addReceiveMessage2(ReceiveMessageBean bean) {
		return addXXX(bean, "receive_message54");
	}

	public boolean deleteReceiveMessage2(String condition) {
		return deleteXXX(condition, "receive_message54");
	}

	public ReceiveMessageBean getReceiveMessage2(String condition) {
		return (ReceiveMessageBean) getXXX(condition, "receive_message54",
				"adultadmin.bean.sms.ReceiveMessageBean");
	}

	public int getReceiveMessage2Count(String condition) {
		return getXXXCount(condition, "receive_message54", "id");
	}

	public ArrayList getReceiveMessage2List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "receive_message54",
				"adultadmin.bean.sms.ReceiveMessageBean");
	}

	public boolean updateReceiveMessage2(String set, String condition) {
		return updateXXX(set, condition, "receive_message54");
	}

	//上行短信历史记录(sms2_群发短信)
	public boolean addReceiveMessageHistory2(ReceiveMessageHistoryBean bean) {
		return addXXX(bean, "receive_message_history54");
	}

	public boolean deleteReceiveMessageHistory2(String condition) {
		return deleteXXX(condition, "receive_message_history54");
	}

	public ReceiveMessageHistoryBean getReceiveMessageHistory2(String condition) {
		return (ReceiveMessageHistoryBean) getXXX(condition, "receive_message_history54",
				"adultadmin.bean.sms.ReceiveMessageHistoryBean");
	}

	public int getReceiveMessageHistory2Count(String condition) {
		return getXXXCount(condition, "receive_message_history54", "id");
	}

	public ArrayList getReceiveMessageHistory2List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "receive_message_history54",
				"adultadmin.bean.sms.ReceiveMessageHistoryBean");
	}

	public boolean updateReceiveMessageHistory2(String set, String condition) {
		return updateXXX(set, condition, "receive_message_history54");
	}
	
	//下行短信信息(sms2_群发短信)
	public boolean addSendMessage2(SendMessage2Bean bean) {
		return addXXX(bean, "send_message54");
	}

	public boolean deleteSendMessage2(String condition) {
		return deleteXXX(condition, "send_message54");
	}

	public SendMessage2Bean getSendMessage2(String condition) {
		return (SendMessage2Bean) getXXX(condition, "send_message54",
				"adultadmin.bean.sms.SendMessage2Bean");
	}

	public int getSendMessage2Count(String condition) {
		return getXXXCount(condition, "send_message54", "id");
	}

	public ArrayList getSendMessage2List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "send_message54",
				"adultadmin.bean.sms.SendMessage2Bean");
	}
	
	public boolean updateSendMessage2(String set, String condition) {
		return updateXXX(set, condition, "send_message54");
	}
	
	//短信信息(sms2_群发短信)
	public boolean addOrderMessage2(OrderMessage2Bean bean) {
		return addXXX(bean, "order_message54");
	}

	public boolean deleteOrderMessage2(String condition) {
		return deleteXXX(condition, "order_message54");
	}

	public OrderMessage2Bean getOrderMessage2(String condition) {
		return (OrderMessage2Bean) getXXX(condition, "order_message54",
				"adultadmin.bean.sms.OrderMessage2Bean");
	}

	public int getOrderMessage2Count(String condition) {
		return getXXXCount(condition, "order_message54", "id");
	}

	public ArrayList getOrderMessage2List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_message54",
				"adultadmin.bean.sms.OrderMessage2Bean");
	}
	
	public boolean updateOrderMessage2(String set, String condition) {
		return updateXXX(set, condition, "order_message54");
	}
	
	
	//下行短信(sms3_发货短信)
	public boolean addShortmessage3(ShortmessageBean bean) {
		return addXXX(bean, "shortmessage55");
	}

	public boolean deleteShortmessage3(String condition) {
		return deleteXXX(condition, "shortmessage55");
	}

	public ShortmessageBean getShortmessage3(String condition) {
		return (ShortmessageBean) getXXX(condition, "shortmessage55",
				"adultadmin.bean.sms.ShortmessageBean");
	}

	public int getShortmessage3Count(String condition) {
		return getXXXCount(condition, "shortmessage55", "id");
	}

	public ArrayList getShortmessage3List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "shortmessage55",
				"adultadmin.bean.sms.ShortmessageBean");
	}

	public boolean updateShortmessage3(String set, String condition) {
		return updateXXX(set, condition, "shortmessage55");
	}

	//下行短信历史记录(sms3_发货短信)
	public boolean addShortmessageHistory3(ShortmessageHistoryBean bean) {
		return addXXX(bean, "shortmessage_history55");
	}

	public boolean deleteShortmessageHistory3(String condition) {
		return deleteXXX(condition, "shortmessage_history55");
	}

	public ShortmessageHistoryBean getShortmessageHistory3(String condition) {
		return (ShortmessageHistoryBean) getXXX(condition, "shortmessage_history55",
				"adultadmin.bean.sms.ShortmessageHistoryBean");
	}

	public int getShortmessageHistory3Count(String condition) {
		return getXXXCount(condition, "shortmessage_history55", "id");
	}

	public ArrayList getShortmessageHistory3List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "shortmessage_history55",
				"adultadmin.bean.sms.ShortmessageHistoryBean");
	}

	public boolean updateShortmessageHistory3(String set, String condition) {
		return updateXXX(set, condition, "shortmessage_history55");
	}
	
	//上行短信(sms3_发货短信)
	public boolean addReceiveMessage3(ReceiveMessageBean bean) {
		return addXXX(bean, "receive_message55");
	}

	public boolean deleteReceiveMessage3(String condition) {
		return deleteXXX(condition, "receive_message55");
	}

	public ReceiveMessageBean getReceiveMessage3(String condition) {
		return (ReceiveMessageBean) getXXX(condition, "receive_message55",
				"adultadmin.bean.sms.ReceiveMessageBean");
	}

	public int getReceiveMessage3Count(String condition) {
		return getXXXCount(condition, "receive_message55", "id");
	}

	public ArrayList getReceiveMessage3List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "receive_message55",
				"adultadmin.bean.sms.ReceiveMessageBean");
	}

	public boolean updateReceiveMessage3(String set, String condition) {
		return updateXXX(set, condition, "receive_message55");
	}

	//上行短信历史记录(sms3_发货短信)
	public boolean addReceiveMessageHistory3(ReceiveMessageHistoryBean bean) {
		return addXXX(bean, "receive_message_history55");
	}

	public boolean deleteReceiveMessageHistory3(String condition) {
		return deleteXXX(condition, "receive_message_history55");
	}

	public ReceiveMessageHistoryBean getReceiveMessageHistory3(String condition) {
		return (ReceiveMessageHistoryBean) getXXX(condition, "receive_message_history55",
				"adultadmin.bean.sms.ReceiveMessageHistoryBean");
	}

	public int getReceiveMessageHistory3Count(String condition) {
		return getXXXCount(condition, "receive_message_history55", "id");
	}

	public ArrayList getReceiveMessageHistory3List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "receive_message_history55",
				"adultadmin.bean.sms.ReceiveMessageHistoryBean");
	}

	public boolean updateReceiveMessageHistory3(String set, String condition) {
		return updateXXX(set, condition, "receive_message_history55");
	}
	
	//下行短信信息(sms3_发货短信)
	public boolean addSendMessage3(SendMessage3Bean bean) {
		return addXXX(bean, "send_message55");
	}

	public boolean deleteSendMessage3(String condition) {
		return deleteXXX(condition, "send_message55");
	}

	public SendMessage3Bean getSendMessage3(String condition) {
		return (SendMessage3Bean) getXXX(condition, "send_message55",
				"adultadmin.bean.sms.SendMessage3Bean");
	}

	public int getSendMessage3Count(String condition) {
		return getXXXCount(condition, "send_message55", "id");
	}

	public ArrayList getSendMessage3List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "send_message55",
				"adultadmin.bean.sms.SendMessage3Bean");
	}
	
	public boolean updateSendMessage3(String set, String condition) {
		return updateXXX(set, condition, "send_message55");
	}
	
	//短信信息(sms3_发货短信)
	public boolean addOrderMessage3(OrderMessage3Bean bean) {
		return addXXX(bean, "order_message55");
	}

	public boolean deleteOrderMessage3(String condition) {
		return deleteXXX(condition, "order_message55");
	}

	public OrderMessage3Bean getOrderMessage3(String condition) {
		return (OrderMessage3Bean) getXXX(condition, "order_message55",
				"adultadmin.bean.sms.OrderMessage3Bean");
	}

	public int getOrderMessage3Count(String condition) {
		return getXXXCount(condition, "order_message55", "id");
	}

	public ArrayList getOrderMessage3List(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_message55",
				"adultadmin.bean.sms.OrderMessage3Bean");
	}
	
	public ArrayList getOrderMessage3CascadedList(String condition, int index, int count,String orderBy){
		return getCascadedXXXList(condition, index, count, orderBy, "select o.* from order_message55 o left join shop.user_order u on o.order_id = u.id" ,"order_message55",
		"adultadmin.bean.sms.OrderMessage3Bean") ;
	}
	
	public boolean updateOrderMessage3(String set, String condition) {
		return updateXXX(set, condition, "order_message55");
	}
	
	//安心短信(sms3)
	public boolean addEaseMessage(EaseMessageBean bean) {
		return addXXX(bean, "ease_message55");
	}

	public boolean deleteEaseMessage(String condition) {
		return deleteXXX(condition, "ease_message55");
	}

	public EaseMessageBean getEaseMessage(String condition) {
		return (EaseMessageBean) getXXX(condition, "ease_message55",
				"adultadmin.bean.sms.EaseMessageBean");
	}

	public int getEaseMessageCount(String condition) {
		return getXXXCount(condition, "ease_message55", "id");
	}

	public ArrayList getEaseMessageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "ease_message55",
				"adultadmin.bean.sms.EaseMessageBean");
	}
	
	public boolean updateEaseMessage(String set, String condition) {
		return updateXXX(set, condition, "ease_message55");
	}

	public boolean addEaseMessage2(EaseMessage2Bean bean) {
		return addXXX(bean, "ease_message2");
	}

	public boolean deleteEaseMessage2(String condition) {
		return deleteXXX(condition, "ease_message2");
	}

	public EaseMessageBean getEaseMessage2(String condition) {
		return (EaseMessageBean) getXXX(condition, "ease_message2",
				"adultadmin.bean.sms.EaseMessage2Bean");
	}

	public int getEaseMessageCount2(String condition) {
		return getXXXCount(condition, "ease_message2", "id");
	}

	public ArrayList getEaseMessageList2(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "ease_message2",
				"adultadmin.bean.sms.EaseMessage2Bean");
	}

	public boolean updateEaseMessage2(String set, String condition) {
		return updateXXX(set, condition, "ease_message2");
	}
	//老用户订单。待发货  取消时候 需要发送短信表
	public boolean addSendMessageOrderId(SendMessageOrderIdBean bean) {
		return addXXX(bean, DbOperation.SCHEMA_SMS+"send_message_order_id");
	}

	public boolean deleteSendMessageOrderId(String condition) {
		return deleteXXX(condition, DbOperation.SCHEMA_SMS+"send_message_order_id");
	}

	public SendMessageOrderIdBean getSendMessageOrderId(String condition) {
		return (SendMessageOrderIdBean) getXXX(condition, DbOperation.SCHEMA_SMS+"send_message_order_id",
				"adultadmin.bean.sms.SendMessageOrderIdBean");
	}

	public int getSendMessageOrderIdCount(String condition) {
		return getXXXCount(condition, DbOperation.SCHEMA_SMS+"send_message_order_id", "id");
	}

	public ArrayList getSendMessageOrderIdList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, DbOperation.SCHEMA_SMS+"send_message_order_id",
				"adultadmin.bean.sms.SendMessageOrderIdBean");
	}
	
	public boolean updateSendMessageOrderId(String set, String condition) {
		return updateXXX(set, condition, DbOperation.SCHEMA_SMS+"send_message_order_id");
	}
	
}
