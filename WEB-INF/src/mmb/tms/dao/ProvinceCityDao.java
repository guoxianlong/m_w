package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.ProvinceCity;

public interface ProvinceCityDao {
    int deleteByPrimaryKey(Integer id);

    int insert(ProvinceCity record);

    int insertSelective(ProvinceCity record);

    ProvinceCity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ProvinceCity record);

    int updateByPrimaryKey(ProvinceCity record);

	List<ProvinceCity> getProvinceCityList(Map<String, String> conditionMap);
}