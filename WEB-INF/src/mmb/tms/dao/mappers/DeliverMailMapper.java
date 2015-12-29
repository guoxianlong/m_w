package mmb.tms.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.tms.dao.DeliverMailDao;
import mmb.tms.model.DeliverMail;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class DeliverMailMapper extends AbstractDaoSupport implements DeliverMailDao
{
	@Override
	public List getDeliverMailList(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}
	@Override
	public List getDeliverMailList1(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}
	@Override
	public List getDeliverPackageCodeList(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}
	@Override
	public int getDeliverMailCount(HashMap<String, String> map) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}
	@Override
	public DeliverMail getDeliverMailInfo(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}
	@Override
	public int updateDeliverMailStatus(DeliverMail bean){
		return getSession().update(bean);
	}
	@Override
	public int addDeliverMail(DeliverMail bean){
		getSession().insert(bean);
		return bean.getId();
	}
	
}
