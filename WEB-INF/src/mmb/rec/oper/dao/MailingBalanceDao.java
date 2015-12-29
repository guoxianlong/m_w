package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.balance.MailingBalanceBean;

public interface MailingBalanceDao {
	
	public int addMailingBalance(MailingBalanceBean mailingBalanceBean);
	
	public MailingBalanceBean getMailingBalance(String condition);
	
	public List<MailingBalanceBean> getMailingBalanceList(Map<String,String> paramMap);
	
	public List<MailingBalanceBean> getMailingBalanceListSlave(Map<String,String> paramMap);

}
