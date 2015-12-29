package mmb.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CommonDao {
	
	public int deleteCommon(Map<String,String> paramMap);

	public int updateCommon(Map<String, String> paramMap);
	
	public int insertCommon(Map<String, String> paramMap);
	
	public int getCommonCount(Map<String,String> paramMap);
	
	public List<HashMap<String, String>> getCommonInfo(Map<String,String> paramMap);
	
	public List<HashMap<String, Object>> getCommonInfoCount(Map<String,String> paramMap);

	public List<HashMap<String, String>> getPOPCommonInfo(HashMap<String, String> paramMap);
}
