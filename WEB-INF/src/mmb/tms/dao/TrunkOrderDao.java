package mmb.tms.dao;

import java.util.List;
import java.util.Map;

import mmb.tms.model.TrunkOrder;
import mmb.tms.model.TrunkOrderInfo;

public interface TrunkOrderDao {
	public List<TrunkOrder> qryTrunkOrderLs(Map<String,Object> map);
	
	public Long qryTrunkOrderLsTotal(Map<String,Object> map);
	
	public List<Map<String,Object>> qryMailingBatchPackage(Map<String,Object> map);
	
	public Long qryMailingBatchPackageTotal(Map<String,Object> map);
	
	public int insertTrunkOrderInfo(TrunkOrderInfo record);
	
	public int updateByPrimaryKeySelective(TrunkOrder record);
	
	public TrunkOrder qryTrunkOrderByPK(Integer trunkOrderId);
	
	public List<Map<String,Object>> qryTrunkOrderInfoLs(Map<String,Object> map);
	
	public Long qryTrunkOrderInfoLsTotal(Map<String,Object> map);
}
