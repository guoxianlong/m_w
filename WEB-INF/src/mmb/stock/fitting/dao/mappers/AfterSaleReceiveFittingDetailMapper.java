package mmb.stock.fitting.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import mmb.stock.fitting.dao.AfterSaleReceiveFittingDetailDao;
import mmb.stock.fitting.model.AfterSaleReceiveFittingDetail;

@Repository
public class AfterSaleReceiveFittingDetailMapper extends AbstractDaoSupport implements AfterSaleReceiveFittingDetailDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(AfterSaleReceiveFittingDetail record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insertSelective(AfterSaleReceiveFittingDetail record) {
		return getSession().insert(record);
	}

	@Override
	public AfterSaleReceiveFittingDetail selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(AfterSaleReceiveFittingDetail record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(AfterSaleReceiveFittingDetail record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String, Object>> getReceiveFittingDetails(Integer id) {
		return getSession(DynamicDataSource.SLAVE).selectList(id);
	}

	@Override
	public List<Map<String, Object>> getReceiveFittingDetailList(Map<String, Object> condition) {
		return  getSession(DynamicDataSource.SLAVE).selectList(condition);
	}

	@Override
	public int deleteByReceiveID(Integer receiveId) {
		return getSession().delete(receiveId);
	}

}
