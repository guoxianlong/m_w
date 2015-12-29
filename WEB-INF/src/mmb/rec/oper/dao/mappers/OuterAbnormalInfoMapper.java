package mmb.rec.oper.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.rec.oper.dao.OuterAbnormalInfoDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class OuterAbnormalInfoMapper extends AbstractDaoSupport implements OuterAbnormalInfoDao {

	@Override
	public List<HashMap<String, String>> getOuterAbnormalList(Map<String, String> paramMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(paramMap);
	}

	@Override
	public int getOuterAbnormalCount(String condition) {
		// TODO Auto-generated method stub
		return 0;
	}


}

