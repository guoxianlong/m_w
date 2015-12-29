package mmb.stock.spare.dao;

import java.util.List;

import mmb.stock.spare.model.SpareCargoProductStock;
import mmb.stock.spare.model.SpareProductStock;

public interface SpareProductStockDao {
	
	public List<SpareProductStock> getList(String condition, int index, int count, String orderBy);
	
	public int getListCount(String condition);
	
	
	public List<SpareCargoProductStock> getCargoList(String condition, int index, int count, String orderBy);
	
	public int getCargoListCount(String condition);
	
}	
