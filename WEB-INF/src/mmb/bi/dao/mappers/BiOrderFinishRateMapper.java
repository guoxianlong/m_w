package mmb.bi.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.bi.dao.BiOrderFinishRateDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class BiOrderFinishRateMapper extends AbstractDaoSupport implements BiOrderFinishRateDao {

	@Override
	public List<HashMap<String, String>> getIntradayOrderCompleteInfo(HashMap<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
