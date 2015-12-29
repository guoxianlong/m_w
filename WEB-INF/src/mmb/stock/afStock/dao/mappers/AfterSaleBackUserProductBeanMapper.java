package mmb.stock.afStock.dao.mappers;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.afStock.dao.AfterSaleBackUserProductBeanDao;
import mmb.stock.afStock.model.AfterSaleBackUserProductBean;

@Repository
public class AfterSaleBackUserProductBeanMapper extends AbstractDaoSupport implements AfterSaleBackUserProductBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleBackUserProductBean record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSelective(AfterSaleBackUserProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleBackUserProductBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleBackUserProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AfterSaleBackUserProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}
    
}