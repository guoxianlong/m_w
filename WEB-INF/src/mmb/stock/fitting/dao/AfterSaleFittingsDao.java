package mmb.stock.fitting.dao;

import java.util.Map;

import mmb.stock.fitting.model.AfterSaleFittings;

public interface AfterSaleFittingsDao {
    int deleteByPrimaryKey(Long id);

    int insert(AfterSaleFittings record);

    int insertSelective(AfterSaleFittings record);

    AfterSaleFittings selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AfterSaleFittings record);

    int updateByPrimaryKey(AfterSaleFittings record);

	Map<String, Object> getFittingName(Map<String, Object> condition);
}