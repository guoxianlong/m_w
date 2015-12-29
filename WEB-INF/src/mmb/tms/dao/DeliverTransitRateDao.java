package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverTransitRate;

public interface DeliverTransitRateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DeliverTransitRate record);

    int insertSelective(DeliverTransitRate record);

    DeliverTransitRate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverTransitRate record);

    int updateByPrimaryKey(DeliverTransitRate record);

	List<DeliverTransitRate> getDeliverTransiteRateList(Map<String, String> conditionMap);
}