package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.AuditOrderStat;

public interface AuditOrderStatDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AuditOrderStat record);

    int insertSelective(AuditOrderStat record);

    AuditOrderStat selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AuditOrderStat record);

    int updateByPrimaryKey(AuditOrderStat record);

	List<AuditOrderStat> getAuditOrderStatList(Map<String, String> conditionMap);
}