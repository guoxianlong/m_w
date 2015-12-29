package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voUser;

import mmb.tms.model.TrunkCorpInfo;
import mmb.tms.model.TrunkEffectForExcel;

public interface TrunkLineDao {
	
	public int addTrunk(TrunkCorpInfo t);
	
	public List<TrunkCorpInfo> getTrunkCorpInfo(Map<String,String> map);
	
	public int getTrunkCorpInfoCount(Map<String,String> map);
	
	public int upDateTrunkCorpInfo(Map<String,String> map);
	
}
