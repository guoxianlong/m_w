package mmb.stock.spare.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import mmb.stock.spare.dao.ImeiSpareStockinBeanDao;
import mmb.stock.spare.model.ImeiSpareStockinBean;
@Repository
public class ImeiSpareStockinBeanMapper extends AbstractDaoSupport implements ImeiSpareStockinBeanDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(ImeiSpareStockinBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(ImeiSpareStockinBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ImeiSpareStockinBean selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(ImeiSpareStockinBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(ImeiSpareStockinBean record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int batchInsertBean(List<ImeiSpareStockinBean> list) {
		return getSession().insert(list);
	}

	@Override
	public int updateIMEISpareStockinByCondition(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession().update(map);
	}

}
