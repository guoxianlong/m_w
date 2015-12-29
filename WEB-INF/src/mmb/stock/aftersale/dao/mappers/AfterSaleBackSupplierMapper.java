package mmb.stock.aftersale.dao.mappers;


import mmb.stock.aftersale.AfterSaleBackSupplier;
import mmb.stock.aftersale.dao.AfterSaleBackSupplierDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class AfterSaleBackSupplierMapper extends AbstractDaoSupport implements AfterSaleBackSupplierDao{

	@Override
	public AfterSaleBackSupplier getAfterSaleBackSupplierById(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectOne(id);
	}

}
