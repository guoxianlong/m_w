package mmb.tms.service;

import java.util.Map;

import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGridJson;

public interface ITrunkOrderService {
	public EasyuiDataGridJson qryTrunkOrder(Map<String,Object> map);
	
	public EasyuiDataGridJson qryMailingBatchPackage(Map<String,Object> map);
	
	public Json modifyTrunkInfo(Map<String,Object> map);
	
	public EasyuiDataGridJson qryTrunkOrderInfo(Map<String,Object> map);
}
