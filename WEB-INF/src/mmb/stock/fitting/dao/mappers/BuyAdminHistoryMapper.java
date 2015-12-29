package mmb.stock.fitting.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.stock.fitting.dao.BuyAdminHistoryDao;
import mmb.stock.fitting.dao.BuyStockinDao;
import mmb.stock.fitting.model.BuyAdminHistory;
import mmb.stock.fitting.model.BuyStockin;
import mmb.stock.fitting.model.FittingStockinBean;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.bean.buy.BuyAdminHistoryBean;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class BuyAdminHistoryMapper extends AbstractDaoSupport implements
		BuyAdminHistoryDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(BuyAdminHistory record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BuyAdminHistory selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(BuyAdminHistory record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(BuyAdminHistory record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(BuyAdminHistoryBean log) {
		// TODO Auto-generated method stub
		return getSession().insert(log);
	}


}
