package mmb.stock.afStock.dao.mappers;

import java.util.HashMap;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.afStock.dao.AfterSaleDetectProductBeanDao;
import mmb.stock.afStock.model.AfterSaleDetectProductBean;

@Repository
public class AfterSaleDetectProductBeanMapper extends AbstractDaoSupport implements AfterSaleDetectProductBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleDetectProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(AfterSaleDetectProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleDetectProductBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleDetectProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AfterSaleDetectProductBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public AfterSaleDetectProductBean selectByCondition(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public int updateByCondition(String set, String condition) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("set", set);
		map.put("condition", condition);
		return getSession().update(map);
	}

}