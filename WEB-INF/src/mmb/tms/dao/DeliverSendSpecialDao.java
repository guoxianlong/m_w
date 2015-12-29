package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverSendSpecial;

public interface DeliverSendSpecialDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DeliverSendSpecial record);

    int insertSelective(DeliverSendSpecial record);

    DeliverSendSpecial selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverSendSpecial record);

    int updateByPrimaryKey(DeliverSendSpecial record);

	List<DeliverSendSpecial> getDeliverSendSpecialList(Map<String, String> deliverMap);

	List<Map<String, Object>> getDeliverSendSpecialMap(Map<String, String> deliverMap);
	
	String getDeliverAreaProvinces(String condition);
	
	String getDeliverAreaCities(String condition);
}