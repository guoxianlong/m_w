package mmb.stock.spare.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.stock.spare.dao.SpareStockCardDao;
import mmb.stock.spare.model.SpareStockCard;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;
@Repository
public class SpareStockCardMapper extends AbstractDaoSupport implements SpareStockCardDao {

	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(SpareStockCard record) {
		getSession().insert(record);
		return record.getId();
	}

	@Override
	public SpareStockCard selectByPrimaryKey(Integer id) {
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(SpareStockCard record) {
		return 0;
	}

	@Override
	public int batchInsertCard(List<SpareStockCard> list) {
		return getSession().insert(list);
	}

	@Override
	public int getHistoryStockCount(HashMap<String,String> condtionMap) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condtionMap)).intValue();
	}

	@Override
	public List<SpareStockCard> getHistoryStockList(HashMap<String, String> condtionMap) {
		return getSession(DynamicDataSource.SLAVE).selectList(condtionMap);
	}

}
