package mmb.stock.area.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.area.model.StockArea;
import mmb.stock.area.model.StockAreaSubTemp;

public interface StockAreaDao {
    int deleteByPrimaryKey(Integer id);

    int insert(StockArea record);

    int insertSelective(StockArea record);

    StockArea selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StockArea record);

    int updateByPrimaryKey(StockArea record);

	List<StockArea> getStockAreaList(Map<String, String> map);
	
	List<Map<String,Object>> getStockAreaSubTempList(Map<String, String> map);
	
	int getStockAreaCount(Map<String,String> map);
	
	List<Map<String,Object>> getStockTypeList(Map<String,String> map);

	List<Map<String, Object>> getStockTypeByCount(Map<String, String> map);
}