package mmb.stock.aftersale.dao.mappers;

import java.util.HashMap;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.aftersale.AfterSaleDetectProductBean;
import mmb.stock.aftersale.dao.AfterSaleDetectProductDao;

@Repository
public class AfterSaleDetectProductMapper extends AbstractDaoSupport implements AfterSaleDetectProductDao {

	@Override
	public int getAfterSaleBackSupplierProductCount(HashMap<String,String> map) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public AfterSaleDetectProductBean getDetectProduct(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectOne(map);
	}

	@Override
	public int updateDetectProduct(AfterSaleDetectProductBean record) {
		return getSession().update(record);
	}

	@Override
	public AfterSaleDetectProductBean getDetectProductById(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

}
