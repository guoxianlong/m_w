package mmb.stock.fitting.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.fitting.dao.AfterSaleReceiveFittingDao;
import mmb.stock.fitting.model.AfterSaleReceiveFitting;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class AfterSaleReceiveFittingMapper extends AbstractDaoSupport implements AfterSaleReceiveFittingDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleReceiveFitting record) {
		return getSession().insert(record);
	}

	@Override
	public int insertSelective(AfterSaleReceiveFitting record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public AfterSaleReceiveFitting selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleReceiveFitting record) {
		return getSession().update(record);
	}

	@Override
	public int updateByPrimaryKey(AfterSaleReceiveFitting record) {
		return 0;
	}

	@Override
	public AfterSaleReceiveFitting getAfterSaleReceiveFitting(Map<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public int getDetectIdByCode(String code) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(code)).intValue();
	}


	@Override
	public boolean updateAfterSaleReceiveFitting(String set, String condition) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("set", set);
		map.put("condition", condition);
		return getSession().update(map) > 0;
	}

	@Override
	public int getAfterSaleReceiveFittingCount(Map<String, Object> condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	@Override
	public List<Map<String, Object>> getAfterSaleReceiveFittingList(Map<String, Object> condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

}
