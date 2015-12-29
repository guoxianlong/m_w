package mmb.tms.service.impl;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.TrunkEffectLogDao;
import mmb.tms.model.TrunkEffectLog;
import mmb.tms.service.ITrunkEffectLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TrunkEffectLogServiceImpl implements ITrunkEffectLogService {
	
	@Autowired
	private TrunkEffectLogDao trunkEffectLogDao;
	
	@Override
	public int addTrunkEffectLog(TrunkEffectLog trunkEffectLog) {
		// TODO Auto-generated method stub
		return trunkEffectLogDao.addTrunkEffectLog(trunkEffectLog);
	}

	@Override
	public List<Map<String, String>> getTrunkEffectLog(Map<String, String> map) {
		// TODO Auto-generated method stub
		return trunkEffectLogDao.getTrunkEffectLog(map);
	}

	@Override
	public int getTrunkEffectLogCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return trunkEffectLogDao.getTrunkEffectLogCount(map);
	}

}
