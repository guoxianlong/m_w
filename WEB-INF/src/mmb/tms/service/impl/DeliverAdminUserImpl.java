package mmb.tms.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mmb.tms.dao.DeliverAdminUserDao;
import mmb.tms.model.DeliverAdminUser;
import mmb.tms.service.IDeliverAdminUserService;

@Service
public class DeliverAdminUserImpl implements IDeliverAdminUserService {
	
	@Autowired
	public DeliverAdminUserDao deliverAdminUserDao;

	@Override
	public int addDeliverAdminUser(DeliverAdminUser deliverAdminUser) {
		// TODO Auto-generated method stub
		return deliverAdminUserDao.insert(deliverAdminUser);
	}

	@Override
	public List<Map<String,String>> getDeliverAdminUser(Map<String, String> map) {
		// TODO Auto-generated method stub
		return deliverAdminUserDao.getDeliverAdminUser(map);
	}

	@Override
	public int getDeliverAdminUserCount(Map<String, String> map) {
		// TODO Auto-generated method stub
		return deliverAdminUserDao.getDeliverAdminUserCount(map);
	}

	@Override
	public int updateDeliverAdminUser(Map<String, String> map) {
		// TODO Auto-generated method stub
		return deliverAdminUserDao.updateDeliverAdminUser(map);
	}


}
