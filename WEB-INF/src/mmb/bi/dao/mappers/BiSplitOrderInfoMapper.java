package mmb.bi.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.bi.dao.BiSplitOrderInfoDao;
import mmb.bi.model.BiSplitOrderInfo;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class BiSplitOrderInfoMapper extends AbstractDaoSupport implements
		BiSplitOrderInfoDao {
	@Override
	public List<HashMap<String, Object>> getSplitOrderList(
			HashMap<String, Object> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

}
