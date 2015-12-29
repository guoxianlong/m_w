package mmb.stock.fitting.dao;

import java.util.List;
import java.util.Map;

import mmb.stock.fitting.model.AfterSaleReceiveFittingDetail;

public interface AfterSaleReceiveFittingDetailDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AfterSaleReceiveFittingDetail record);

    int insertSelective(AfterSaleReceiveFittingDetail record);

    AfterSaleReceiveFittingDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AfterSaleReceiveFittingDetail record);

    int updateByPrimaryKey(AfterSaleReceiveFittingDetail record);
    
    /**
     * 根据领用单id获取领用配件信息
     * @param id
     * @return
     * @author lining
    * @date 2014-7-4
     */
    List<Map<String,Object>> getReceiveFittingDetails(Integer id);

	List<Map<String, Object>> getReceiveFittingDetailList(Map<String, Object> condition);

	int deleteByReceiveID(Integer receiveId);
}