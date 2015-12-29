package mmb.bsby.dao;

import java.util.List;
import java.util.Map;

import mmb.bsby.model.BsbyReasonLog;




public interface BsbyReasonLogDao {
	   int deleteByPrimaryKey(Integer id);

	    int insert(BsbyReasonLog bsbyReasonLog);
	 
	    BsbyReasonLog selectByPrimaryKey(Integer id);
	    int getBsbyReasonLogCount(String condition);
	    List<BsbyReasonLog>  getBsbyReasonLogList(Map<String,String> map);
	    public BsbyReasonLog queryBsbyReasonLogByCondition(String condition);
	    
	    /**
	     * 编辑
	     */
	    int updateByCondition(String set, String condition);
	    
}
