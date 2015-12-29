package mmb.tms.service;

import java.util.List;
import java.util.Map;

import mmb.tms.model.TrunkEffectLog;

public interface ITrunkEffectLogService {
	
	public int addTrunkEffectLog(TrunkEffectLog trunkEffectLog);
	
	public List<Map<String,String>> getTrunkEffectLog(Map<String,String> map);
	
	public int getTrunkEffectLogCount(Map<String,String> map);
}
