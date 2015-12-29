package mmb.stock.spare.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.List;

import mmb.stock.spare.dao.SpareCodeBeanDao;
import mmb.stock.spare.model.SpareCodeBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class SpareCodeBeanMapper extends AbstractDaoSupport implements SpareCodeBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareCodeBean record) {
		// TODO Auto-generated method stub
		return getSession().insert(record);
	}

	@Override
	public int insertSelective(SpareCodeBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpareCodeBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(SpareCodeBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(SpareCodeBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<SpareCodeBean> getSpareCodeBean(Map<String,String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public SpareCodeBean getSpareCodeByCondition(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectOne(conditionMap);
	}

	@Override
	public int batchUpdateSpareCodeStatus(List<SpareCodeBean> list) {
		return getSession().update(list);
	}

	@Override
	public int updateStatusByCode(SpareCodeBean record) {
		return getSession().update(record);
	}

	@Override
	public int updateSpareCodeBeanByCondition(Map<String, String> map) {
		return getSession().update(map);
	}

}
