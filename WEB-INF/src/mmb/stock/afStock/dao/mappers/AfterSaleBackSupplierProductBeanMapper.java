package mmb.stock.afStock.dao.mappers;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.afStock.dao.AfterSaleBackSupplierProductBeanDao;
import mmb.stock.afStock.model.AfterSaleBackSupplierProductBean;

@Repository
public class AfterSaleBackSupplierProductBeanMapper extends AbstractDaoSupport implements AfterSaleBackSupplierProductBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleBackSupplierProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(AfterSaleBackSupplierProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleBackSupplierProductBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleBackSupplierProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AfterSaleBackSupplierProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleBackSupplierProductBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

}