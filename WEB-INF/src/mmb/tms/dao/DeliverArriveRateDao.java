package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverArriveRate;

public interface DeliverArriveRateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DeliverArriveRate record);

    int insertSelective(DeliverArriveRate record);

    DeliverArriveRate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverArriveRate record);

    int updateByPrimaryKey(DeliverArriveRate record);

	List<DeliverArriveRate> getDeliverArriveRateList(Map<String, String> conditionMap);
}