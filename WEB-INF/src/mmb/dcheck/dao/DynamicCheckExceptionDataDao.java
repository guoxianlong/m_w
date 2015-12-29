package mmb.dcheck.dao;

import java.util.HashMap;
import java.util.List;

import mmb.dcheck.model.DynamicCheckExceptionData;

public interface DynamicCheckExceptionDataDao {
	
	/**
	 * 
	 * @description:是否存在异常货位
	 * @param wholeCode
	 * @return
	 * @returnType: int
	 * @create:2015年7月4日 上午11:07:50
	 */
	public Integer existExceptionCargo(String wholeCode);
	
	/**
	 * 
	 * @descripion 获取异常数据总数
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	public Long getExceptionDataCount(HashMap<String,Object> condition);
	
	/**
	 * 
	 * @descripion 获取异常数据列表
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	public List<HashMap<String,Object>> getExceptionDataLst(HashMap<String,Object> condition);
	
	/** 
	 * @description 
	 * @param bean
	 * @return
	 * @returnType int
	 * @create 2015-7-2 下午06:46:38
	 * @author gel
	 */
	public int saveDynamicCheckExceptionData(DynamicCheckExceptionData bean);
	
	/** 
	 * @description 
	 * @param bean
	 * @return
	 * @returnType int
	 * @create 2015-7-2 下午07:13:31
	 * @author gel
	 */
	public int deleteDynamciCheckExceptionData(DynamicCheckExceptionData bean);

	/**
	 * 
	 * @description:获取盘单号对应的异常货位号列表
	 * @param checkId
	 * @return
	 * @returnType: List<String>
	 * @create:2015年7月5日 下午12:56:30
	 */
	public List<String> getExceptionCargoCode(int checkId);
}
