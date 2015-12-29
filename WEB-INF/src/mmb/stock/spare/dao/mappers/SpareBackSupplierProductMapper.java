package mmb.stock.spare.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.stock.spare.dao.SpareBackSupplierProductDao;
import mmb.stock.spare.model.SpareBackSupplierProduct;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class SpareBackSupplierProductMapper extends AbstractDaoSupport implements SpareBackSupplierProductDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareBackSupplierProduct record) {
		// TODO Auto-generated method stub
		return ((Integer)getSession().insert(record)).intValue();
	}

	@Override
	public int insertSelective(SpareBackSupplierProduct record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpareBackSupplierProduct selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(SpareBackSupplierProduct record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(SpareBackSupplierProduct record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List getSpareBackSupplierproductByCondition(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public List<SpareBackSupplierProduct> getSpareBackSupplierproductJoinProduct(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
