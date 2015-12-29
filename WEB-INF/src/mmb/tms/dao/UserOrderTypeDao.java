package mmb.tms.dao;

import mmb.tms.model.UserOrderType;

public interface UserOrderTypeDao {
    int deleteByPrimaryKey(Integer id);

    int insert(UserOrderType record);

    int insertSelective(UserOrderType record);

    UserOrderType selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserOrderType record);

    int updateByPrimaryKey(UserOrderType record);
}