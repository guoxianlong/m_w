package mmb.bsby.dao;

import java.util.List;
import java.util.Map;

import mmb.bsby.model.BsbyReason;


public interface BsbyReasonDao {
	   int deleteByPrimaryKey(Integer id);

	    int insert(BsbyReason bsbyReason);
	 
	    BsbyReason selectByPrimaryKey(Integer id);
	    int selectCount(String condition);
	    List<BsbyReason>  getBsbyReasonList(Map<String,String> map);
	    public BsbyReason queryBsbyReasonByCondition(String condition);
	    
	    /**
	     * 编辑
	     */
	    int updateByCondition(String set, String condition);
	    
}
