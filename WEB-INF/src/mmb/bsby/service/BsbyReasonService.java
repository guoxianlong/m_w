package mmb.bsby.service;

import java.util.List;
import java.util.Map;

import mmb.bsby.dao.mappers.BsbyReasonMapper;
import mmb.bsby.model.BsbyReason;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BsbyReasonService {
	@Autowired
   private  BsbyReasonMapper bsbyReasonMapper;
	
	/**
	 * 添加
	 */
	public int addBsbyReason(BsbyReason bsbyReason){
		return this.bsbyReasonMapper.insert(bsbyReason);
	}
	
	/**
	 * 删除
	 */
	public int deleteBsbyReasonById(int id){
		return this.bsbyReasonMapper.deleteByPrimaryKey(id);
	}
	
	/**
	 * 根据id查询
	 */
	public BsbyReason selectBsbyReasonById(int id){
		return this.bsbyReasonMapper.selectByPrimaryKey(id);
	}
	
	/**
	 * 获取总条数
	 */
	public int getBsbyReasonCount(String condition){
		return this.bsbyReasonMapper.selectCount(condition);
	}
	
	/**
	 * 获取集合
	 */
	public List<BsbyReason> queryBsbyReasons(Map<String,String> map){
		return this.bsbyReasonMapper.getBsbyReasonList(map);
	}
	
	/**
	 * 根据报损报溢原因查询是否存在
	 * type 类型  reason 原因
	 */
	public BsbyReason queryBsbyReasonByCondition(String condition){
		return bsbyReasonMapper.queryBsbyReasonByCondition(condition);
	}
	public int updateBsbyReason(String set,String condition){
		return bsbyReasonMapper.updateByCondition(set, condition);
	}
}
