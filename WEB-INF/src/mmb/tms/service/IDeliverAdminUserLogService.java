package mmb.tms.service;

import java.util.List;
import java.util.Map;

import mmb.tms.model.DeliverAdminUserLog;

public interface IDeliverAdminUserLogService {
	
	public int AddDeliverAdminUserLog(DeliverAdminUserLog daul);
	
	public List<Map<String,String>> getDeliverAdminUserLog(Map<String,String> map);
	
	public int getDeliverAdminUserCount(Map<String,String> map);
}
