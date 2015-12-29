package mmb.tms.dao;

import mmb.tms.model.SortingBatchOrder;

public interface SortingBatchOrderDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SortingBatchOrder record);

    int insertSelective(SortingBatchOrder record);

    SortingBatchOrder selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SortingBatchOrder record);

    int updateByPrimaryKey(SortingBatchOrder record);
}