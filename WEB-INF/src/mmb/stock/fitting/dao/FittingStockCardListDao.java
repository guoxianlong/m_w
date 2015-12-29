package mmb.stock.fitting.dao;

import java.util.HashMap;
import java.util.List;

import mmb.stock.fitting.model.FittingOutBean;
import mmb.stock.fitting.model.FittingStockCard;

public interface FittingStockCardListDao {

	List<FittingStockCard> getOutStockCardList(HashMap<String, String> map);

	List<FittingStockCard> getInStockCardList(HashMap<String, String> map);

	int getOutStockCardCount(String condition);

	int getInStockCardCount(String condition);
	
	String getString(String select);
	
	List<FittingOutBean> getFittingOutList(String code);
	
	boolean updateAfterSaleDetectProductFittingByAsrfId(String set, int id);
}
