/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;

import adultadmin.bean.balance.BalanceComputeBean;
import adultadmin.bean.balance.BalanceCycleBean;
import adultadmin.bean.balance.BalanceTimepointBean;
import adultadmin.bean.balance.LogisticBean;
import adultadmin.bean.balance.MailingBalanceAuditingBean;
import adultadmin.bean.balance.MailingBalanceAuditingLogBean;
import adultadmin.bean.balance.MailingBalanceBean;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-9-25
 * 
 * 说明：结算功能 数据库操作
 */
public interface IBalanceService extends IBaseService {
	
	//MailingBalance
	public ArrayList getMailingBalanceList(String condition, int index,
			int count, String orderBy);
	public ArrayList getMailingBalanceList2(String table,String condition, int index,
			int count, String orderBy);

	public MailingBalanceBean getMailingBalance(String condition);

	public int getMailingBalanceCount(String condition);

	public boolean addMailingBalance(MailingBalanceBean mailingBalance);

	public boolean updateMailingBalance(String set, String condition);

	public boolean deleteMailingBalance(String condition);
	
	//MailingBalanceAuditing
	public ArrayList getMailingBalanceAuditingList(String condition, int index,
			int count, String orderBy);
	
	public MailingBalanceAuditingBean getMailingBalanceAuditing(String condition);

	public int getMailingBalanceAuditingCount(String condition);

	public boolean addMailingBalanceAuditing(MailingBalanceAuditingBean mailingBalance);

	public boolean updateMailingBalanceAuditing(String set, String condition);

	public boolean deleteMailingBalanceAuditing(String condition);

	//BalanceCompute
	public ArrayList getBalanceComputeList(String condition, int index,
			int count, String orderBy);

	public BalanceComputeBean getBalanceCompute(String condition);

	public int getBalanceComputeCount(String condition);

	public boolean addBalanceCompute(BalanceComputeBean balanceCompute);

	public boolean updateBalanceCompute(String set, String condition);

	public boolean deleteBalanceCompute(String condition);

	//BalanceCycle
	public ArrayList getBalanceCycleList(String condition, int index,
			int count, String orderBy);

	public BalanceCycleBean getBalanceCycle(String condition);

	public int getBalanceCycleCount(String condition);

	public boolean addBalanceCycle(BalanceCycleBean balanceCycle);

	public boolean updateBalanceCycle(String set, String condition);

	public boolean deleteBalanceCycle(String condition);

	//BalanceTimepoint
	public ArrayList getBalanceTimepointList(String condition, int index,
			int count, String orderBy);

	public BalanceTimepointBean getBalanceTimepoint(String condition);

	public int getBalanceTimepointCount(String condition);

	public boolean addBalanceTimepoint(BalanceTimepointBean balanceTimepoint);

	public boolean updateBalanceTimepoint(String set, String condition);

	public boolean deleteBalanceTimepoint(String condition);
	
	//Logistic
	public ArrayList getLogisticList(String condition, int index,
			int count, String orderBy);

	public LogisticBean getLogistic(String condition);

	public int getLogisticCount(String condition);

	public boolean addLogistic(LogisticBean logistic);

	public boolean updateLogistic(String set, String condition);

	public boolean deleteLogistic(String condition);
	
	//MailingBalanceAuditingLog
	public ArrayList getMailingBalanceAuditingLogList(String condition, int index,
			int count, String orderBy);

	public MailingBalanceAuditingLogBean getMailingBalanceAuditingLog(String condition);

	public int getMailingBalanceAuditingLogCount(String condition);

	public boolean addMailingBalanceAuditingLog(MailingBalanceAuditingLogBean mailingBalanceAuditingLog);

	public boolean updateMailingBalanceAuditingLog(String set, String condition);

	public boolean deleteMailingBalanceAuditingLog(String condition);
}
