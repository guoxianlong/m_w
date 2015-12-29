package mmb.stock.spare.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.spare.dao.SpareBackSupplierDao;
import mmb.stock.spare.model.SpareBackSupplier;
@Repository
public class SpareBackSupplierMapper extends AbstractDaoSupport implements SpareBackSupplierDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareBackSupplier record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSelective(SpareBackSupplier record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpareBackSupplier selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

	@Override
	public int updateByPrimaryKeySelective(SpareBackSupplier record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(SpareBackSupplier record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String,String>> getSpareBackSupplierByCondition(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getSpareBackSupplierByConditionForCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

}
