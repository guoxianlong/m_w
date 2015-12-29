/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;

import adultadmin.bean.sms.ReceiveMessageHistoryBean;
import adultadmin.bean.sms.ShortmessageBean;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2010-06-17
 * 
 * 说明：信息机收发短信相关操作
 */
public interface ISMSWithOneDBService extends IBaseService {

	// dm下行短信(sms51_发短信)
	public boolean addDmShortmessage(ShortmessageBean bean);

	// dm上行短信(sms51_收短信)
	public boolean deleteDmReceiveMessage(String condition);

	public ArrayList getDmReceiveMessageList(String condition, int index,
			int count, String orderBy);

	// dm上行短信历史记录(sms51_短信)
	public boolean addDmReceiveMessageHistory(ReceiveMessageHistoryBean bean);

	// promo下行短信(sms52_发短信)
	public boolean addPromotionShortmessage(ShortmessageBean bean);

	// promo上行短信(sms52_收短信)
	public boolean deletePromotionReceiveMessage(String condition);

	public ArrayList getPromotionReceiveMessageList(String condition,
			int index, int count, String orderBy);

	// promo上行短信历史记录(sms52_短信)
	public boolean addPromotionReceiveMessageHistory(
			ReceiveMessageHistoryBean bean);

}
