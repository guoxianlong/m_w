package mmb.bi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.bi.model.BITableBean;

public interface BITableDao {
	
	List<HashMap<String, String>> getOrderCountTable(Map<String, String> param);
	
	List<BITableBean> getOperTypeTableList(Map<String, String> paramMap);
	
}
