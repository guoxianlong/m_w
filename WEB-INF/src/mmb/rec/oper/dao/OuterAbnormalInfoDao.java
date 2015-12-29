package mmb.rec.oper.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface OuterAbnormalInfoDao {
	public List<HashMap<String, String>> getOuterAbnormalList(Map<String, String> paramMap);
	public int getOuterAbnormalCount(String condition);
}
