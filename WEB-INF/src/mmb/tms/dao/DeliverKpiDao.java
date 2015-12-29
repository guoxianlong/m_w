package mmb.tms.dao;

import java.util.HashMap;
import java.util.List;

import mmb.tms.model.DeliverKpi;

public interface DeliverKpiDao {
    int deleteByPrimaryKey(Integer id);

    /**
     * 增加快递公司kpi
     * @param record
     * @return id
     */
    int insert(DeliverKpi record);
    
    /**
     * 根据条件获取快递公司kpi指标列表
     * @param map
     * @return
     */
    List<DeliverKpi> getDeliverKpiList(HashMap<String,String> map);

    int insertSelective(DeliverKpi record);

    DeliverKpi selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverKpi record);

    int updateByPrimaryKey(DeliverKpi record);
}