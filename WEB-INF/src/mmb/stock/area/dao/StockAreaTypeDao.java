package mmb.stock.area.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.area.model.StockAreaType;

public interface StockAreaTypeDao {
    int deleteByCondition(Map<String,String> map);

    int insert(StockAreaType record);

    int insertSelective(StockAreaType record);

    int updateByPrimaryKeySelective(StockAreaType record);

    int updateByPrimaryKey(StockAreaType record);

	List getStockAreaTypes(Map<String, String> map);
}