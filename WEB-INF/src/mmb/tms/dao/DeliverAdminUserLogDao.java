package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverAdminUserLog;

public interface DeliverAdminUserLogDao {
	
	public int addDeliverAdminUserLog(DeliverAdminUserLog deliverAdminUserLog);
	
	public List<Map<String,String>> getDeliverAdminUserLog(Map<String,String> map);
	
	public int getDeliverAdminUserCount(Map<String,String> map);
}
