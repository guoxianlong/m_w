package mmb.stock.spare.dao.mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.spare.dao.SpareBeanDao;
import mmb.stock.spare.model.SpareBean;
import mmb.stock.spare.model.SpareProductDetailed;
import mmb.stock.spare.model.SpareUpShelves;
@Repository
public class SpareBeanMapper extends AbstractDaoSupport implements SpareBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareBean record) {
		getSession().insert(record);
		return record.getId();
	}


	@Override
	public SpareBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(SpareBean record) {
		return getSession().update(record);
	}

	@Override
	public int updateByPrimaryKey(SpareBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpareBean getSpareByCondition(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectOne(conditionMap);
	}
	
	@Override
	public List<SpareUpShelves> getSpareUpShelfList(Map<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public SpareProductDetailed getSpareProductDetailed(Map<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}
	
	@Override
	public SpareBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("set", set);
		map.put("condition", condition);
		
		return getSession().update(map);
	}

	@Override
	public int getSpareCargoStatus(Map<String, String> map) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public int updateSpareStatus(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().update(map)).intValue();
	}

	@Override
	public int batchAddSpareList(List<SpareBean> list) {
		return getSession().insert(list);
	}

	@Override
	public Map<String,String> getSupplierNameAndAddressBySpareCode(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

	@Override
	public int getSupplierIdBySpareCode(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public List<SpareBean> getSpareList(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public SpareBean getSpareJoinReplaceRecord(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectOne(conditionMap);
	}

	@Override
	public List<SpareBean> getSpareListJoinBackSupplierProduct(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

	@Override
	public List<SpareBean> getSpareListJoinUnqualifiedReplace(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

}
