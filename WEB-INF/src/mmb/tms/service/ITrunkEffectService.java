package mmb.tms.service;

import java.util.List;
import java.util.Map;

import mmb.tms.model.TrunkEffect;

public interface ITrunkEffectService {
	
	public int addTrunkEffect(TrunkEffect trunkEffect);
	
	public List<Map<String,String>> getTrunkEffectForLineList(Map<String,String> map);
	
	public int getTrunkEffectForLineCount(Map<String,String> map);
	
	public List<Map<String,String>> getTrunkEffectByAreaAndDeliver(Map<String,String> map);
	
	public List<Map<String,String>> getTrunkEffectByTrunkAndDeliverAdmin(Map<String,String> map);
	
	public int updateTrunkEffect(Map<String,String> map);
	
	public List<Map<String,String>> getTrunkEffectList(Map<String,String> map);
	
	public int getTrunkEffectCount(Map<String,String> map);
	
}
