package mmb.rec.oper.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.common.dao.CommonDao;
import mmb.common.service.CommonService;
import mmb.rec.oper.dao.MailingBalanceDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.bean.balance.MailingBalanceBean;

@Service
public class BalanceService {
	@Autowired
	public MailingBalanceDao mailingBalanceMapper;
	@Autowired
	public CommonDao commonMapper;

	public int addMailingBalance(MailingBalanceBean mailingBalanceBean) {
		mailingBalanceMapper.addMailingBalance(mailingBalanceBean);
		return mailingBalanceBean.getId();
	}

	public MailingBalanceBean getMailingBalance(String condition) {
		return mailingBalanceMapper.getMailingBalance(condition);
	}

	public List<MailingBalanceBean> getMailingBalanceList(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return mailingBalanceMapper.getMailingBalanceList(paramMap);
	}
	public List<MailingBalanceBean> getMailingBalanceListSlave(String condition,int index, int count, String orderBy) {
		Map<String,String> paramMap = CommonService.constructSelectMap(condition,index,count,orderBy);
		return mailingBalanceMapper.getMailingBalanceListSlave(paramMap);
	}
	
	public int deleteMailingBalance(String condition) {
		Map<String,String> paramMap = CommonService.constructDeleteMap("mailing_balance", condition);
		return commonMapper.deleteCommon(paramMap);
	}
	
	public int updateMailingBalance(String set, String condition) {
		Map<String,String> paramMap = CommonService.constructUpdateMap("mailing_balance", set, condition);
		return commonMapper.updateCommon(paramMap);
	}
	
	public int getMailingBalanceCount(String condition ) {
		Map<String,String> paramMap = CommonService.constructCountMap("mailing_balance", condition);
		return commonMapper.getCommonCount(paramMap);
	}

	
	
	

}
