package mmb.stock.fitting.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.fitting.model.BuyStockin;

public interface BuyStockinDao {
    int deleteByPrimaryKey(Integer id);

    int insert(BuyStockin record);

    int insertSelective(BuyStockin record);

    BuyStockin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BuyStockin record);

    int updateByPrimaryKey(BuyStockin record);
    
    List<Map<String,Object>> selectBuyStockinList(Map<String,Object> map);//入库配件列表
    
    int selectBuyStockinListCount(Map<String,Object> map);//查询记录数

	List<Map<String,Object>> selectBuyStockinConfirmList(Map<String, Object> map);//入库确认列表
	
	List<Map<String,Object>> selectBuyStockinAuditList(Map<String, Object> map);//入库审核列表
	
	int updateBuyStockin( Map<String,Object> map);
	
}