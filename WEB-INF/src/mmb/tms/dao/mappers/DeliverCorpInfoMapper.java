package mmb.tms.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.tms.dao.DeliverCorpInfoDao;

@Repository
public class DeliverCorpInfoMapper extends AbstractDaoSupport implements DeliverCorpInfoDao{

	@Override
	public int insert(DeliverCorpInfoBean deliverInfo) {
		getSession().insert(deliverInfo);
		return deliverInfo.getId();
	}

	@Override
	public int update(DeliverCorpInfoBean deliverInfo) {
		return getSession().update(deliverInfo);
	}

	@Override
	public DeliverCorpInfoBean getDeliverCorpInfoById(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public List<DeliverCorpInfoBean> getDeliverCorpInfoList(Map<String, Integer> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public DeliverCorpInfoBean getDeliverCorpInfoByName(String name) {
		
		return getSession(DynamicDataSource.SLAVE).selectOne(name);
	}

}
