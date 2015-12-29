package mmb.stock.spare.dao.mappers;


import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.spare.dao.AfterSaleReplaceNewProductRecordDao;
import mmb.stock.spare.model.AfterSaleReplaceNewProductRecord;
@Repository
public class AfterSaleReplaceNewProductRecordMapper extends AbstractDaoSupport implements AfterSaleReplaceNewProductRecordDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleReplaceNewProductRecord record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(AfterSaleReplaceNewProductRecord record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleReplaceNewProductRecord selectByPrimaryKey(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleReplaceNewProductRecord record) {
		return getSession().update(record);
	}

	@Override
	public int updateByPrimaryKey(AfterSaleReplaceNewProductRecord record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleReplaceNewProductRecord selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("set", set);
		hashMap.put("condition", condition);
		
		return getSession().update(hashMap);
	}

	@Override
	public int getReplaceRecordCount(HashMap<String, String> conditionMap) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(conditionMap)).intValue();
	}

	@Override
	public List<AfterSaleReplaceNewProductRecord> getReplaceRecordList(HashMap<String, String> conditionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(conditionMap);
	}

	@Override
	public int updateAfterSaleWareHourceProductRecord(String sql) {
		return getSession().update(sql);
	}

	@Override
	public int getAfterSaleWareHourceProductRecordType(String sql) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(sql)).intValue();
	}

}
