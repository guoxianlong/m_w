/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.impl;

import java.util.ArrayList;

import adultadmin.bean.balance.BalanceComputeBean;
import adultadmin.bean.balance.BalanceCycleBean;
import adultadmin.bean.balance.BalanceTimepointBean;
import adultadmin.bean.balance.LogisticBean;
import adultadmin.bean.balance.MailingBalanceAuditingBean;
import adultadmin.bean.balance.MailingBalanceAuditingLogBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.service.infc.IBalanceService;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-9-25
 * 
 * 说明：
 */
public class BalanceServiceImpl extends BaseServiceImpl implements
		IBalanceService {
	public BalanceServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public BalanceServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	//MailingBalance
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addMailingBalance(MailingBalanceBean mailingBalance) {
		return addXXX(mailingBalance, "mailing_balance");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteMailingBalance(String condition) {
		return deleteXXX(condition, "mailing_balance");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public MailingBalanceBean getMailingBalance(String condition) {
		return (MailingBalanceBean) getXXX(condition, "mailing_balance",
				"adultadmin.bean.balance.MailingBalanceBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getMailingBalanceCount(String condition) {
		return getXXXCount(condition, "mailing_balance", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getMailingBalanceList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "mailing_balance",
				"adultadmin.bean.balance.MailingBalanceBean");
	}

	public ArrayList getMailingBalanceList2(String table,String condition, int index,
			int count, String orderBy) {
		return getJoinXXXList(table,condition, index, count, orderBy, "mailing_balance",
				"adultadmin.bean.balance.MailingBalanceBean");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateMailingBalance(String set, String condition) {
		return updateXXX(set, condition, "mailing_balance");
	}
	//MailingBalanceAuditing
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addMailingBalanceAuditing(MailingBalanceAuditingBean mailingBalance) {
		return addXXX(mailingBalance, "mailing_balance_auditing");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteMailingBalanceAuditing(String condition) {
		return deleteXXX(condition, "mailing_balance_auditing");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public MailingBalanceAuditingBean getMailingBalanceAuditing(String condition) {
		return (MailingBalanceAuditingBean) getXXX(condition, "mailing_balance_auditing",
				"adultadmin.bean.balance.MailingBalanceAuditingBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getMailingBalanceAuditingCount(String condition) {
		return getXXXCount(condition, "mailing_balance_auditing", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getMailingBalanceAuditingList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "mailing_balance_auditing",
				"adultadmin.bean.balance.MailingBalanceAuditingBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateMailingBalanceAuditing(String set, String condition) {
		return updateXXX(set, condition, "mailing_balance_auditing");
	}

	//BalanceCompute
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBalanceCompute(BalanceComputeBean balanceCompute) {
		return addXXX(balanceCompute, "balance_compute");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBalanceCompute(String condition) {
		return deleteXXX(condition, "balance_compute");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BalanceComputeBean getBalanceCompute(String condition) {
		return (BalanceComputeBean) getXXX(condition, "balance_compute",
				"adultadmin.bean.balance.BalanceComputeBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBalanceComputeCount(String condition) {
		return getXXXCount(condition, "balance_compute", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBalanceComputeList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "balance_compute",
				"adultadmin.bean.balance.BalanceComputeBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBalanceCompute(String set, String condition) {
		return updateXXX(set, condition, "balance_compute");
	}


	//balance_cycle
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBalanceCycle(BalanceCycleBean balanceCycle) {
		return addXXX(balanceCycle, "balance_cycle");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBalanceCycle(String condition) {
		return deleteXXX(condition, "balance_cycle");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BalanceCycleBean getBalanceCycle(String condition) {
		return (BalanceCycleBean) getXXX(condition, "balance_cycle",
				"adultadmin.bean.balance.BalanceCycleBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBalanceCycleCount(String condition) {
		return getXXXCount(condition, "balance_cycle", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBalanceCycleList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "balance_cycle",
				"adultadmin.bean.balance.BalanceCycleBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBalanceCycle(String set, String condition) {
		return updateXXX(set, condition, "balance_cycle");
	}

	//BalanceTimepoint
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBalanceTimepoint(BalanceTimepointBean balanceTimepoint) {
		return addXXX(balanceTimepoint, "balance_timepoint");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBalanceTimepoint(String condition) {
		return deleteXXX(condition, "balance_timepoint");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BalanceTimepointBean getBalanceTimepoint(String condition) {
		return (BalanceTimepointBean) getXXX(condition, "balance_timepoint",
				"adultadmin.bean.balance.BalanceTimepointBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBalanceTimepointCount(String condition) {
		return getXXXCount(condition, "balance_timepoint", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBalanceTimepointList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "balance_timepoint",
				"adultadmin.bean.balance.BalanceTimepointBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBalanceTimepoint(String set, String condition) {
		return updateXXX(set, condition, "balance_timepoint");
	}
	
	//Logistic
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addLogistic(LogisticBean logistic) {
		return addXXX(logistic, "logistic");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteLogistic(String condition) {
		return deleteXXX(condition, "logistic");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public LogisticBean getLogistic(String condition) {
		return (LogisticBean) getXXX(condition, "logistic",
				"adultadmin.bean.balance.LogisticBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getLogisticCount(String condition) {
		return getXXXCount(condition, "logistic", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getLogisticList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "logistic",
				"adultadmin.bean.balance.LogisticBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateLogistic(String set, String condition) {
		return updateXXX(set, condition, "logistic");
	}
	
	//MailingBalanceAuditingLog
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addMailingBalanceAuditingLog(MailingBalanceAuditingLogBean mailingBalanceAuditingLog) {
		return addXXX(mailingBalanceAuditingLog, "mailing_balance_auditing_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteMailingBalanceAuditingLog(String condition) {
		return deleteXXX(condition, "mailing_balance_auditing_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public MailingBalanceAuditingLogBean getMailingBalanceAuditingLog(String condition) {
		return (MailingBalanceAuditingLogBean) getXXX(condition, "mailing_balance_auditing_log",
				"adultadmin.bean.balance.MailingBalanceAuditingLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getMailingBalanceAuditingLogCount(String condition) {
		return getXXXCount(condition, "mailing_balance_auditing_log", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getMailingBalanceAuditingLogList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "mailing_balance_auditing_log",
				"adultadmin.bean.balance.MailingBalanceAuditingLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateMailingBalanceAuditingLog(String set, String condition) {
		return updateXXX(set, condition, "mailing_balance_auditing_log");
	}
}
