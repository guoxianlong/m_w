package mmb.bi.dao;

import java.util.HashMap;
import java.util.List;


public interface BiOrderFinishRateDao {

	public List<HashMap<String, String>> getIntradayOrderCompleteInfo(HashMap<String, String> paramMap);
	
}
