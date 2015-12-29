package mmb.stock.fitting.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.fitting.model.AfterSaleReceiveFitting;

public interface AfterSaleReceiveFittingDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AfterSaleReceiveFitting record);

    int insertSelective(AfterSaleReceiveFitting record);

    AfterSaleReceiveFitting selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AfterSaleReceiveFitting record);

    int updateByPrimaryKey(AfterSaleReceiveFitting record);

	AfterSaleReceiveFitting getAfterSaleReceiveFitting(Map<String, Object> condition);

	int getDetectIdByCode(String code);
	
	boolean updateAfterSaleReceiveFitting(String set, String condition);

	int getAfterSaleReceiveFittingCount(Map<String, Object> condition);

	List<Map<String, Object>> getAfterSaleReceiveFittingList(Map<String, Object> condition);

}