package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.TrunkEffectLog;

public interface TrunkEffectLogDao {
	
	int addTrunkEffectLog(TrunkEffectLog irunkEffectLog);
	
	List<Map<String,String>> getTrunkEffectLog(Map<String,String> map);
	
	int getTrunkEffectLogCount(Map<String,String> map);
}
