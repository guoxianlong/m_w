package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.Provinces;

public interface ProvincesDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Provinces record);

    int insertSelective(Provinces record);

    Provinces selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Provinces record);

    int updateByPrimaryKey(Provinces record);
    
    List<Provinces> getProvincesList(Map<String,String> map);
}