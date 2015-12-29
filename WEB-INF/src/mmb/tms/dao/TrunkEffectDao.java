package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.TrunkEffect;

public interface TrunkEffectDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TrunkEffect record);

    int insertSelective(TrunkEffect record);

    TrunkEffect selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TrunkEffect record);

    int updateByPrimaryKey(TrunkEffect record);
    
    List<Map<String,String>> getTrunkEffectForLineList(Map<String,String> map);
    
    int getTrunkEffectForLineCount(Map<String,String> map);
    
    List<Map<String,String>> getTrunkEffectByAreaAndDeliver(Map<String,String> map);
    
    List<Map<String,String>> getTrunkEffectByTrunkAndDeliverAdmin(Map<String,String> map);
    
    int updateTrunkEffect(Map<String,String> map);
    
    List<Map<String,String>> getTrunkEffect(Map<String,String> map);
    
    List<Map<String,String>>  getTrunkEffectList(Map<String,String> map);
    
    int getTrunkEffectCount(Map<String,String> map);
    
}