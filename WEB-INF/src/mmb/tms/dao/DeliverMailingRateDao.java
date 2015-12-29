package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.AuditOrderStat;
import mmb.tms.model.DeliverMailingRate;

public interface DeliverMailingRateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DeliverMailingRate record);

    int insertSelective(DeliverMailingRate record);

    DeliverMailingRate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverMailingRate record);

    int updateByPrimaryKey(DeliverMailingRate record);

	List<DeliverMailingRate> getDeliverMailingRateList(Map<String, String> conditionMap);
}