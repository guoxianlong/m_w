/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.impl;

import java.util.ArrayList;

import adultadmin.bean.sms.ReceiveMessageHistoryBean;
import adultadmin.bean.sms.ShortmessageBean;
import adultadmin.service.infc.ISMSWithOneDBService;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者 姚兰
 * 
 * 创建日期：2012-02-17
 * 
 * 说明：短信收发相关操作
 */
public class SMSWithOneDBServiceImpl extends BaseServiceImpl implements
		ISMSWithOneDBService {

	public SMSWithOneDBServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public SMSWithOneDBServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	// dm下行短信(sms51_短信)
	public boolean addDmShortmessage(ShortmessageBean bean) {
		return addXXX(bean, "shortmessage51");
	}

	public boolean deleteDmReceiveMessage(String condition) {
		return deleteXXX(condition, "receive_message51");
	}

	public ArrayList getDmReceiveMessageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"receive_message51", "adultadmin.bean.sms.ReceiveMessageBean");
	}

	public boolean addDmReceiveMessageHistory(ReceiveMessageHistoryBean bean) {
		return addXXX(bean, "receive_message_history51");
	}

	// dm下行短信(sms52_短信)
	public boolean addPromotionShortmessage(ShortmessageBean bean) {
		return addXXX(bean, "shortmessage52");
	}

	public boolean deletePromotionReceiveMessage(String condition) {
		return deleteXXX(condition, "receive_message52");
	}

	public ArrayList getPromotionReceiveMessageList(String condition,
			int index, int count, String orderBy) {

		return getXXXList(condition, index, count, orderBy,
				"receive_message52", "adultadmin.bean.sms.ReceiveMessageBean");
	}

	public boolean addPromotionReceiveMessageHistory(
			ReceiveMessageHistoryBean bean) {
		return addXXX(bean, "receive_message_history52");
	}

}
