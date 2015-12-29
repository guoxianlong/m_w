package mmb.stock.spare.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.spare.dao.SpareStockinProductBeanDao;
import mmb.stock.spare.model.SpareStockinProductBean;
@Repository
public class SpareStockinProductBeanMapper extends AbstractDaoSupport implements SpareStockinProductBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareStockinProductBean record) {
		return getSession().insert(record);
	}


	@Override
	public SpareStockinProductBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(SpareStockinProductBean record) {
		return getSession().update(record);
	}

	@Override
	public int updateByPrimaryKey(SpareStockinProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpareStockinProductBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public int batchAddSpareStockinProducts(List<SpareStockinProductBean> list) {
		return getSession().insert(list);
	}

	@Override
	public SpareStockinProductBean getSpareStockinProductByCondition(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectOne(conditionMap);
	}
	
	@Override
	public List<SpareStockinProductBean> getSpareStockinProductBeans(Map<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("set", set);
		map.put("condition", condition);
		return getSession().update(map);
	}
	
	@Override
	public int getSpareCodeStatus(Map<String, String> map) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public int updateSpareStockinProductByCondition(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession().update(map);
	}

}
