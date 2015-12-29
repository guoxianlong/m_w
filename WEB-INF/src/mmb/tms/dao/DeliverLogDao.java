package mmb.tms.dao;

import java.util.List;

import mmb.tms.model.DeliverLog;

public interface DeliverLogDao {
    int deleteByPrimaryKey(Integer id);

    /**
     * 新增log
     * @param record
     * @return 新增条数
     */
    int insert(DeliverLog record);
    
    /**
     * 根据条件获取日志列表
     * @param condition
     * @return
     */
    List<DeliverLog> getDeliverLogList(String condition);

    int insertSelective(DeliverLog record);

    DeliverLog selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverLog record);

    int updateByPrimaryKey(DeliverLog record);
}