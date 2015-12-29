package mmb.stock.spare.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.spare.dao.SpareStockinBeanDao;
import mmb.stock.spare.model.SpareStockinBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class SpareStockinBeanMapper extends AbstractDaoSupport  implements SpareStockinBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareStockinBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSelective(SpareStockinBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpareStockinBean selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(SpareStockinBean record) {
		return getSession().update(record);
	}

	@Override
	public int updateByPrimaryKey(SpareStockinBean record) {
		return 0;
	}

	@Override
	public List<SpareStockinBean> getSpareStockinList(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

	@Override
	public List<SpareStockinBean> getSpareStockinListJoinProduct(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

	@Override
	public int getSpareStockInCount(HashMap<String,String> conditionMap) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(conditionMap)).intValue();
	}

	@Override
	public Map<String,String> getSupplierId(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

	@Override
	public Map<String, String> getSpareStockinAreaId(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("set", set);
		map.put("condition", condition);
		return getSession().update(map);
	}
}
