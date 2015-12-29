package mmb.stock.spare.dao.mappers;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.spare.dao.SpareStockinProductUpshelfBeanDao;
import mmb.stock.spare.model.SpareStockinProductUpshelfBean;

@Repository
public class SpareStockinProductUpshelfBeanMapper extends AbstractDaoSupport implements SpareStockinProductUpshelfBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareStockinProductUpshelfBean record) {
		getSession().insert(record);
		return record.getId();		
	}

	@Override
	public int insertSelective(SpareStockinProductUpshelfBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SpareStockinProductUpshelfBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(SpareStockinProductUpshelfBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(SpareStockinProductUpshelfBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}