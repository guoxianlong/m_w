package mmb.tms.service.impl;

import java.util.List;
import java.util.Map;

import mmb.tms.dao.DeliverAdminUserLogDao;
import mmb.tms.model.DeliverAdminUserLog;
import mmb.tms.service.IDeliverAdminUserLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliverAdminUserLogServiceImpl implements IDeliverAdminUserLogService {
	@Autowired
	private DeliverAdminUserLogDao DeliverAdminUserLogDao;
	
	@Override
	public int AddDeliverAdminUserLog(DeliverAdminUserLog daul) {
		// TODO Auto-generated method stub
		return DeliverAdminUserLogDao.addDeliverAdminUserLog(daul);
	}

	@Override
	public List<Map<String, String>> getDeliverAdminUserLog(
			Map<String, String> map) {
		// TODO Auto-generated method stub
		return DeliverAdminUserLogDao.getDeliverAdminUserLog(map);
	}

	@Override
	public int getDeliverAdminUserCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return DeliverAdminUserLogDao.getDeliverAdminUserCount(map);
	}

}
