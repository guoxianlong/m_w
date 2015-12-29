package mmb.bi.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.bi.dao.BiOrderFinishRateDao;
import mmb.bi.dao.BiProductLineDeliverInfoDao;
import mmb.bi.model.BiProductLineDeliverInfo;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class BiProductLineDeliverInfoMapper extends AbstractDaoSupport implements BiProductLineDeliverInfoDao {

	@Override
	public List<HashMap<String, Object>> getProductLineDeliverInfo(
			HashMap<String, Object> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}


}
