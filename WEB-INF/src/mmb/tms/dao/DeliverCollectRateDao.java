package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverCollectRate;

public interface DeliverCollectRateDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DeliverCollectRate record);

    int insertSelective(DeliverCollectRate record);

    DeliverCollectRate selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverCollectRate record);

    int updateByPrimaryKey(DeliverCollectRate record);

	List<DeliverCollectRate> getDeliverCollectRateList(Map<String, String> conditionMap);
}