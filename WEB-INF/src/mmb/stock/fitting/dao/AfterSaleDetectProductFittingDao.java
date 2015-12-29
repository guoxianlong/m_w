package mmb.stock.fitting.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

import mmb.stock.fitting.model.AfterSaleDetectProductFitting;


public interface AfterSaleDetectProductFittingDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AfterSaleDetectProductFitting record);

    int insertSelective(AfterSaleDetectProductFitting record);

    AfterSaleDetectProductFitting selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AfterSaleDetectProductFitting record);

    int updateByPrimaryKey(AfterSaleDetectProductFitting record);

	Map<String, Object> getAfterSaleDetectProductFitting(Map<String, Object> condition);

	Map<String, Object> getAfterSalebackUserDetect(Map<String, Object> condition);
}