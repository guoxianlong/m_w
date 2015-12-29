package mmb.stock.fitting.dao;

import adultadmin.bean.buy.BuyAdminHistoryBean;
import mmb.stock.fitting.model.BuyAdminHistory;

public interface BuyAdminHistoryDao {
    int deleteByPrimaryKey(Integer id);

    int insert(BuyAdminHistoryBean log);

    int insertSelective(BuyAdminHistory record);

    BuyAdminHistory selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BuyAdminHistory record);

    int updateByPrimaryKey(BuyAdminHistory record);
}