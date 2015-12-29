package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverAdminUser;

public interface DeliverAdminUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(DeliverAdminUser record);

    int insertSelective(DeliverAdminUser record);

    DeliverAdminUser selectByPrimaryKey(Integer id);
    
    List<Map<String,String>> getDeliverAdminUser(Map<String,String> map);

    int updateByPrimaryKeySelective(DeliverAdminUser record);

    int updateByPrimaryKey(DeliverAdminUser record);
    
    int getDeliverAdminUserCount(Map<String,String> map);
    
    int updateDeliverAdminUser(Map<String,String> map);
    
}