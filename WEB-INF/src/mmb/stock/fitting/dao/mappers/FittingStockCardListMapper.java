package mmb.stock.fitting.dao.mappers;

import java.util.HashMap;
import java.util.List;

import mmb.stock.fitting.dao.FittingStockCardListDao;
import mmb.stock.fitting.model.FittingOutBean;
import mmb.stock.fitting.model.FittingStockCard;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

/**
 * 配件商品进销存卡片 mapper
 * @author mengqy
 *
 */
@Repository
public class FittingStockCardListMapper extends AbstractDaoSupport implements FittingStockCardListDao {

	@Override
	public List<FittingStockCard> getOutStockCardList(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public List<FittingStockCard> getInStockCardList(HashMap<String, String> map) {
		return getSession(DynamicDataSource.SLAVE).selectList(map);
	}

	@Override
	public int getOutStockCardCount(String condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();	
	}

	@Override
	public int getInStockCardCount(String condition) {
		return ((Integer)getSession(DynamicDataSource.SLAVE).selectOne(condition)).intValue();
	}
	
	@Override
	public String getString(String select) {
		return getSession(DynamicDataSource.SLAVE).selectOne(select);
	}
	/**
	 * PDA配件确认出库(领单)
	 */
	@Override
	public List<FittingOutBean> getFittingOutList(String code) {
		return getSession(DynamicDataSource.SLAVE).selectList(code);
	}

	@Override
	public boolean updateAfterSaleDetectProductFittingByAsrfId(String set, int id) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("set", set);
		map.put("id", id);
		return getSession().update(map) > 0;
	}
	
}
