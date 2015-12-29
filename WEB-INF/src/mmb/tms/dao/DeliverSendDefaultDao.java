package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverSendDefault;

public interface DeliverSendDefaultDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DeliverSendDefault record);

    int insertSelective(DeliverSendDefault record);

    DeliverSendDefault selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverSendDefault record);

    int updateByPrimaryKey(DeliverSendDefault record);

	List<DeliverSendDefault> getDeliverSendDefaultList(Map<String, String> defaultMap);

	List<Map<String, Object>> getDeliverSendDefaultMap(Map<String, String> defaultMap);
}