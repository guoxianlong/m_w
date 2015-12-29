package mmb.stock.area.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.stock.area.dao.StockAreaDao;
import mmb.stock.area.model.StockArea;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class StockAreaMapper extends AbstractDaoSupport implements StockAreaDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(StockArea record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public int insertSelective(StockArea record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public StockArea selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(StockArea record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(StockArea record) {
		getSession().update(record);
		return record.getId();
	}
	
	@Override
	public List<StockArea> getStockAreaList(Map<String,String> map){
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public List<Map<String,Object>> getStockAreaSubTempList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getStockAreaCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(map)).intValue();
	}

	@Override
	public List<Map<String, Object>> getStockTypeList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}
	
	@Override
	public List<Map<String, Object>> getStockTypeByCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

}
