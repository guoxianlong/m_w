package mmb.stock.aftersale.dao.mappers;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.aftersale.AfterSaleBackUserProduct;
import mmb.stock.aftersale.dao.AfterSaleBackUserProductDao;

@Repository
public class AfterSaleBackUserProductMapper extends AbstractDaoSupport implements AfterSaleBackUserProductDao {

	@Override
	public int insert(AfterSaleBackUserProduct record) {
		return getSession().insert(record);
	}

}
