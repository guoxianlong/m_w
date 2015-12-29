/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;

import adultadmin.bean.sms.EaseMessage2Bean;
import adultadmin.bean.sms.EaseMessageBean;
import adultadmin.bean.sms.OrderDealBean;
import adultadmin.bean.sms.OrderMessageBean;
import adultadmin.bean.sms.ReceiveMessageBean;
import adultadmin.bean.sms.ReceiveMessageHistoryBean;
import adultadmin.bean.sms.SendMessage3Bean;
import adultadmin.bean.sms.SendMessageAutoBean;
import adultadmin.bean.sms.SendMessageAutoHistoryBean;
import adultadmin.bean.sms.SendMessageOrderIdBean;
import adultadmin.bean.sms.ShortmessageBean;
import adultadmin.bean.sms.ShortmessageHistoryBean;
import adultadmin.bean.sms.OrderMessage2Bean;
import adultadmin.bean.sms.SendMessage2Bean;
import adultadmin.bean.sms.OrderMessage3Bean;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2010-06-17
 * 
 * 说明：信息机收发短信相关操作
 */
public interface ISMSService extends IBaseService {
	
	//下行短信(sms_订单失败短信)
	public boolean addShortmessage(ShortmessageBean bean);
	
	public ShortmessageBean getShortmessage(String condition);

	public int getShortmessageCount(String condition);

	public boolean updateShortmessage(String set, String condition);

	public boolean deleteShortmessage(String condition);

	public ArrayList getShortmessageList(String condition, int index, int count,
			String orderBy);
	
	//下行短信历史记录(sms_订单失败短信)
	public boolean addShortmessageHistory(ShortmessageHistoryBean bean);
	
	public ShortmessageHistoryBean getShortmessageHistory(String condition);

	public int getShortmessageHistoryCount(String condition);

	public boolean updateShortmessageHistory(String set, String condition);

	public boolean deleteShortmessageHistory(String condition);

	public ArrayList getShortmessageHistoryList(String condition, int index, int count,
			String orderBy);
	
	//上行短信(sms_订单失败短信)
	public boolean addReceiveMessage(ReceiveMessageBean bean);
	
	public ReceiveMessageBean getReceiveMessage(String condition);

	public int getReceiveMessageCount(String condition);

	public boolean updateReceiveMessage(String set, String condition);

	public boolean deleteReceiveMessage(String condition);

	public ArrayList getReceiveMessageList(String condition, int index, int count,
			String orderBy);
	
	//上行短信历史记录(sms_订单失败短信)
	public boolean addReceiveMessageHistory(ReceiveMessageHistoryBean bean);
	
	public ReceiveMessageHistoryBean getReceiveMessageHistory(String condition);

	public int getReceiveMessageHistoryCount(String condition);

	public boolean updateReceiveMessageHistory(String set, String condition);

	public boolean deleteReceiveMessageHistory(String condition);

	public ArrayList getReceiveMessageHistoryList(String condition, int index, int count,
			String orderBy);
	
	//短信信息(sms_订单失败短信)
	public boolean addOrderMessage(OrderMessageBean bean);
	
	public OrderMessageBean getOrderMessage(String condition);

	public int getOrderMessageCount(String condition);

	public boolean updateOrderMessage(String set, String condition);

	public boolean deleteOrderMessage(String condition);

	public ArrayList getOrderMessageList(String condition, int index, int count,
			String orderBy);
	
	//电话失败订单处理(sms_订单失败短信)
	public boolean addOrderDeal(OrderDealBean bean);
	
	public OrderDealBean getOrderDeal(String condition);

	/**
	 * 
	 * 功能:级联订单表 只获取订单id
	 * <p>作者 李双 Feb 26, 2013 10:07:11 AM
	 * @param condition
	 * @return
	 */
	public OrderDealBean getOrderDealCascaded(String condition);
	
	public int getOrderDealCount(String condition);

	public boolean updateOrderDeal(String set, String condition);

	public boolean deleteOrderDeal(String condition);

	public ArrayList getOrderDealList(String condition, int index, int count,
			String orderBy);
	
	//自动发送短信设置(sms_订单失败短信)
	public boolean addSendMessageAuto(SendMessageAutoBean bean);
	
	public SendMessageAutoBean getSendMessageAuto(String condition);

	public int getSendMessageAutoCount(String condition);

	public boolean updateSendMessageAuto(String set, String condition);

	public boolean deleteSendMessageAuto(String condition);

	public ArrayList getSendMessageAutoList(String condition, int index, int count,
			String orderBy);
	
	//自动发送短信历史记录(sms_订单失败短信)
	public boolean addSendMessageAutoHistory(SendMessageAutoHistoryBean bean);
	
	public SendMessageAutoHistoryBean getSendMessageAutoHistory(String condition);

	public int getSendMessageAutoHistoryCount(String condition);

	public boolean updateSendMessageAutoHistory(String set, String condition);

	public boolean deleteSendMessageAutoHistory(String condition);

	public ArrayList getSendMessageAutoHistoryList(String condition, int index, int count,
			String orderBy);
	
	
	//下行短信(sms2_群发短信)
	public boolean addShortmessage2(ShortmessageBean bean);
	
	public ShortmessageBean getShortmessage2(String condition);

	public int getShortmessage2Count(String condition);

	public boolean updateShortmessage2(String set, String condition);

	public boolean deleteShortmessage2(String condition);

	public ArrayList getShortmessage2List(String condition, int index, int count,
			String orderBy);
	
	//下行短信历史记录(sms2_群发短信)
	public boolean addShortmessageHistory2(ShortmessageHistoryBean bean);
	
	public ShortmessageHistoryBean getShortmessageHistory2(String condition);

	public int getShortmessageHistory2Count(String condition);

	public boolean updateShortmessageHistory2(String set, String condition);

	public boolean deleteShortmessageHistory2(String condition);

	public ArrayList getShortmessageHistory2List(String condition, int index, int count,
			String orderBy);
	
	//上行短信(sms2_群发短信)
	public boolean addReceiveMessage2(ReceiveMessageBean bean);
	
	public ReceiveMessageBean getReceiveMessage2(String condition);

	public int getReceiveMessage2Count(String condition);

	public boolean updateReceiveMessage2(String set, String condition);

	public boolean deleteReceiveMessage2(String condition);

	public ArrayList getReceiveMessage2List(String condition, int index, int count,
			String orderBy);
	
	//上行短信历史记录(sms2_群发短信)
	public boolean addReceiveMessageHistory2(ReceiveMessageHistoryBean bean);
	
	public ReceiveMessageHistoryBean getReceiveMessageHistory2(String condition);

	public int getReceiveMessageHistory2Count(String condition);

	public boolean updateReceiveMessageHistory2(String set, String condition);

	public boolean deleteReceiveMessageHistory2(String condition);

	public ArrayList getReceiveMessageHistory2List(String condition, int index, int count,
			String orderBy);
	
	//下发短信信息(sms2_群发短信)
	public boolean addSendMessage2(SendMessage2Bean bean);
	
	public SendMessage2Bean getSendMessage2(String condition);

	public int getSendMessage2Count(String condition);

	public boolean updateSendMessage2(String set, String condition);

	public boolean deleteSendMessage2(String condition);

	public ArrayList getSendMessage2List(String condition, int index, int count,
			String orderBy);
	
	//短信信息(sms2_群发短信)
	public boolean addOrderMessage2(OrderMessage2Bean bean);
	
	public OrderMessage2Bean getOrderMessage2(String condition);

	public int getOrderMessage2Count(String condition);

	public boolean updateOrderMessage2(String set, String condition);

	public boolean deleteOrderMessage2(String condition);

	public ArrayList getOrderMessage2List(String condition, int index, int count,
			String orderBy);
	
	
	//下行短信(sms3_发货短信)
	public boolean addShortmessage3(ShortmessageBean bean);
	
	public ShortmessageBean getShortmessage3(String condition);

	public int getShortmessage3Count(String condition);

	public boolean updateShortmessage3(String set, String condition);

	public boolean deleteShortmessage3(String condition);

	public ArrayList getShortmessage3List(String condition, int index, int count,
			String orderBy);
	
	//下行短信历史记录(sms3_发货短信)
	public boolean addShortmessageHistory3(ShortmessageHistoryBean bean);
	
	public ShortmessageHistoryBean getShortmessageHistory3(String condition);

	public int getShortmessageHistory3Count(String condition);

	public boolean updateShortmessageHistory3(String set, String condition);

	public boolean deleteShortmessageHistory3(String condition);

	public ArrayList getShortmessageHistory3List(String condition, int index, int count,
			String orderBy);
	
	//上行短信(sms3_发货短信)
	public boolean addReceiveMessage3(ReceiveMessageBean bean);
	
	public ReceiveMessageBean getReceiveMessage3(String condition);

	public int getReceiveMessage3Count(String condition);

	public boolean updateReceiveMessage3(String set, String condition);

	public boolean deleteReceiveMessage3(String condition);

	public ArrayList getReceiveMessage3List(String condition, int index, int count,
			String orderBy);
	
	//上行短信历史记录(sms3_发货短信)
	public boolean addReceiveMessageHistory3(ReceiveMessageHistoryBean bean);
	
	public ReceiveMessageHistoryBean getReceiveMessageHistory3(String condition);

	public int getReceiveMessageHistory3Count(String condition);

	public boolean updateReceiveMessageHistory3(String set, String condition);

	public boolean deleteReceiveMessageHistory3(String condition);

	public ArrayList getReceiveMessageHistory3List(String condition, int index, int count,
			String orderBy);
	
	//下发短信信息(sms3_发货短信)
	public boolean addSendMessage3(SendMessage3Bean bean);
	
	public SendMessage3Bean getSendMessage3(String condition);

	public int getSendMessage3Count(String condition);

	public boolean updateSendMessage3(String set, String condition);

	public boolean deleteSendMessage3(String condition);

	public ArrayList getSendMessage3List(String condition, int index, int count,
			String orderBy);
	
	//短信信息(sms3_发货短信)
	public boolean addOrderMessage3(OrderMessage3Bean bean);
	
	public OrderMessage3Bean getOrderMessage3(String condition);

	public int getOrderMessage3Count(String condition);

	public boolean updateOrderMessage3(String set, String condition);

	public boolean deleteOrderMessage3(String condition);

	public ArrayList getOrderMessage3List(String condition, int index, int count,
			String orderBy);
	
	public ArrayList getOrderMessage3CascadedList(String condition, int index, int count,String orderBy);
	
	//安心短信（sms3）
	public boolean addEaseMessage(EaseMessageBean bean);
	
	public EaseMessageBean getEaseMessage(String condition);

	public int getEaseMessageCount(String condition);

	public boolean updateEaseMessage(String set, String condition);

	public boolean deleteEaseMessage(String condition);

	public ArrayList getEaseMessageList(String condition, int index, int count,
			String orderBy);
	
	public boolean addEaseMessage2(EaseMessage2Bean bean);
		
	public EaseMessageBean getEaseMessage2(String condition);

	public int getEaseMessageCount2(String condition);

	public boolean updateEaseMessage2(String set, String condition);

	public boolean deleteEaseMessage2(String condition);

	public ArrayList getEaseMessageList2(String condition, int index, int count,
				String orderBy);
	
	//老用户订单。待发货  取消时候 需要发送短信表
	public boolean addSendMessageOrderId(SendMessageOrderIdBean bean);
	
	public SendMessageOrderIdBean getSendMessageOrderId(String condition);

	public int getSendMessageOrderIdCount(String condition);

	public boolean updateSendMessageOrderId(String set, String condition);

	public boolean deleteSendMessageOrderId(String condition);

	public ArrayList getSendMessageOrderIdList(String condition, int index, int count,
			String orderBy);
	
}
