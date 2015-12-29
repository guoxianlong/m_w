package mmb.tms.dao;

import mmb.tms.model.DeliverBalanceType;

public interface DeliverBalanceTypeDao {
    int deleteByPrimaryKey(Integer id);
    
    /**
     * 增加快递公司与结算公司关系
     * @param record
     * @return 插入条数
     */
    int insert(DeliverBalanceType record);

    int insertSelective(DeliverBalanceType record);

    DeliverBalanceType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DeliverBalanceType record);

    int updateByPrimaryKey(DeliverBalanceType record);

    /**
     * 根据快递公司id获取快递公司与结算公司关系
     * @param deliverId
     * @return
     */
	DeliverBalanceType getDeliverBalanceTypeByDeliverId(Integer deliverId);
}