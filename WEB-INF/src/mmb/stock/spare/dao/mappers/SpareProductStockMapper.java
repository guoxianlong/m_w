package mmb.stock.spare.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.stock.spare.dao.SpareProductStockDao;
import mmb.stock.spare.model.SpareCargoProductStock;
import mmb.stock.spare.model.SpareProductStock;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

@Repository
public class SpareProductStockMapper extends AbstractDaoSupport implements SpareProductStockDao {

	@Override
	public List<SpareProductStock> getList(String condition, int index, int count, String orderBy) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("condition", condition);
		map.put("index", index + "");
		map.put("count", count + "");
		map.put("orderBy", orderBy);		
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getListCount(String condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	@Override
	public List<SpareCargoProductStock> getCargoList(String condition, int index, int count, String orderBy) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("condition", condition);
		map.put("index", index + "");
		map.put("count", count + "");
		map.put("orderBy", orderBy);		
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getCargoListCount(String condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}

	
	
}
