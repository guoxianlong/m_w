package mmb.bsby.service;

import java.util.List;
import java.util.Map;

import mmb.bsby.dao.mappers.BsbyReasonLogMapper;

import mmb.bsby.model.BsbyReasonLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BsbyReasonLogService {
	@Autowired
   private  BsbyReasonLogMapper bsbyReasonLogMapper;
	
	/**
	 * 添加
	 */
	public int addBsbyReasonLog(BsbyReasonLog bsbyReasonLog){
		return this.bsbyReasonLogMapper.insert(bsbyReasonLog);
	}
	
	/**
	 * 删除
	 */
	public int deleteBsbyReasonLogById(int id){
		return this.bsbyReasonLogMapper.deleteByPrimaryKey(id);
	}
	
	/**
	 * 根据id查询
	 */
	public BsbyReasonLog selectBsbyReasonLogById(int id){
		return this.bsbyReasonLogMapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 获取总条数
	 */
	public int getBsbyReasonLogCount(String condition){
		return this.bsbyReasonLogMapper.getBsbyReasonLogCount(condition);
	}
	
	/**
	 * 获取集合
	 */
	public List<BsbyReasonLog> queryBsbyReasonLogs(Map<String,String> map){
		return this.bsbyReasonLogMapper.getBsbyReasonLogList(map);
	}
	

	public BsbyReasonLog queryBsbyReasonLogByCondition(String condition){
		return bsbyReasonLogMapper.queryBsbyReasonLogByCondition(condition);
	}
	public int updateBsbyReasonLog(String set,String condition){
		return bsbyReasonLogMapper.updateByCondition(set, condition);
	}
}
