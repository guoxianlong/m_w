package mmb.stock.spare.dao;

import mmb.stock.spare.model.SpareStockinProductUpshelfBean;

public interface SpareStockinProductUpshelfBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareStockinProductUpshelfBean record);

    int insertSelective(SpareStockinProductUpshelfBean record);

    SpareStockinProductUpshelfBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpareStockinProductUpshelfBean record);

    int updateByPrimaryKey(SpareStockinProductUpshelfBean record);
}