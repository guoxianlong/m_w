package mmb.stock.fitting.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.stock.fitting.dao.BuyStockinDao;
import mmb.stock.fitting.model.BuyStockin;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class BuyStockinMapper extends AbstractDaoSupport implements
		BuyStockinDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(BuyStockin record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(BuyStockin record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BuyStockin selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(BuyStockin record) {
		// TODO Auto-generated method stub		
		return 0;
	}

	@Override
	public int updateByPrimaryKey(BuyStockin record) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int updateBuyStockin( Map<String,Object> map) {

		return getSession().update(map);
		//return 0;
	}

	@Override
	public List<Map<String,Object>> selectBuyStockinList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int selectBuyStockinListCount(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public List<Map<String,Object>> selectBuyStockinConfirmList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public List<Map<String,Object>> selectBuyStockinAuditList(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
