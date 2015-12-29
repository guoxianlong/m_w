package mmb.stock.area.dao.mappers;
import java.util.List;
import java.util.Map;

import mmb.stock.area.dao.StockAreaTypeDao;
import mmb.stock.area.model.StockAreaType;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;
@Repository
public class StockAreaTypeMapper extends AbstractDaoSupport implements StockAreaTypeDao {

	@Override
	public int deleteByCondition(Map<String,String> map) {
		// TODO Auto-generated method stub
		return getSession().delete(map);
	}

	@Override
	public int insert(StockAreaType record) {
		// TODO Auto-generated method stub
		return getSession().insert(record);
	}

	@Override
	public int insertSelective(StockAreaType record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<StockAreaType> getStockAreaTypes(Map<String,String> map) {
		// TODO Auto-generated method stub
		return getSession().selectList(map);
	}

	@Override
	public int updateByPrimaryKeySelective(StockAreaType record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(StockAreaType record) {
		// TODO Auto-generated method stub
		return 0;
	}

}
