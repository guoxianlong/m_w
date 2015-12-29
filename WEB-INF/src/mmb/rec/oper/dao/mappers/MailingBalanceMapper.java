package mmb.rec.oper.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.MailingBalanceDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.bean.balance.MailingBalanceBean;
@Repository
public class MailingBalanceMapper extends AbstractDaoSupport implements MailingBalanceDao {

	@Override
	public int addMailingBalance(MailingBalanceBean mailingBalanceBean) {
		getSession().insert(mailingBalanceBean);
		return mailingBalanceBean.getId();
	}

	@Override
	public MailingBalanceBean getMailingBalance(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<MailingBalanceBean> getMailingBalanceList(
			Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

	@Override
	public List<MailingBalanceBean> getMailingBalanceListSlave(
			Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
