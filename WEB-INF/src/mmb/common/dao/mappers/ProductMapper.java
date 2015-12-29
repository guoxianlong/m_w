package mmb.common.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.common.dao.ProductDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.action.vo.voProduct;
@Repository
public class ProductMapper extends AbstractDaoSupport implements ProductDao{

	@Override
	public voProduct getProduct(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}

	@Override
	public List<Map<String, String>> getProductNameAndCargo(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public List<Map<String, String>> getCargoProduct(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public List<Map<String, String>> getExceptProduct(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

}
