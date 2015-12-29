package mmb.bi.dao;

import java.util.HashMap;
import java.util.List;

import mmb.bi.model.BiProductLineDeliverInfo;

public interface BiProductLineDeliverInfoDao {
	
	public List<HashMap<String, Object>> getProductLineDeliverInfo(HashMap<String, Object> paramMap);

}