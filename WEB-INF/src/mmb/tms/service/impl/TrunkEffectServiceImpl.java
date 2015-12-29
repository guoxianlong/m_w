package mmb.tms.service.impl;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.TrunkEffectDao;
import mmb.tms.model.TrunkEffect;
import mmb.tms.service.ITrunkEffectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrunkEffectServiceImpl implements ITrunkEffectService {
	
	@Autowired
	private TrunkEffectDao TrunkEffectDao;
	
	@Override
	public int addTrunkEffect(TrunkEffect trunkEffect) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.insert(trunkEffect);
	}

	@Override
	public List<Map<String, String>> getTrunkEffectForLineList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.getTrunkEffectForLineList(map);
	}

	@Override
	public int getTrunkEffectForLineCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.getTrunkEffectForLineCount(map);
	}

	@Override
	public List<Map<String, String>> getTrunkEffectByAreaAndDeliver(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.getTrunkEffectByAreaAndDeliver(map);
	}

	@Override
	public List<Map<String, String>> getTrunkEffectByTrunkAndDeliverAdmin(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.getTrunkEffectByTrunkAndDeliverAdmin(map);
	}

	@Override
	public int updateTrunkEffect(Map<String, String> map) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.updateTrunkEffect(map);
	}

	@Override
	public List<Map<String, String>> getTrunkEffectList(Map<String, String> map) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.getTrunkEffectList(map);
	}

	@Override
	public int getTrunkEffectCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return TrunkEffectDao.getTrunkEffectCount(map);
	}

}
