package mmb.tms.service;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverAdminUser;

public interface IDeliverAdminUserService {
	
	public int addDeliverAdminUser(DeliverAdminUser deliverAdminUser);
	
	public List<Map<String,String>> getDeliverAdminUser(Map<String,String> map);
	
	public int getDeliverAdminUserCount(Map<String,String> map);
	
	public int updateDeliverAdminUser(Map<String,String> map);
	
	
}
