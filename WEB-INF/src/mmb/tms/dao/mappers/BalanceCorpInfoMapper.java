package mmb.tms.dao.mappers;

import java.util.List;

import mmb.tms.dao.BalanceCorpInfoDao;
import mmb.tms.model.BalanceCorpInfo;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class BalanceCorpInfoMapper extends AbstractDaoSupport implements BalanceCorpInfoDao{

	@Override
	public BalanceCorpInfo selectByPrimaryKey(Integer id) {
		return null;
	}

	@Override
	public List<BalanceCorpInfo> getBalanceCorpInfoList() {
		return getSession(DynamicDataSource.SLAVE).selectList();
	}

	

}
