package mmb.dcheck.dao;

import java.util.HashMap;
import java.util.List;

import mmb.dcheck.model.DynamicCheckLogBean;

/**
 * @description 盘点日志
 * @create 2015-7-2 下午03:58:36
 * @author gel
 */
public interface DynamicCheckLogDao {

	/** 
	 * @description 
	 * @param log
	 * @return
	 * @returnType DynamicCheckLogBean
	 * @create 2015-7-2 下午04:03:40
	 * @author gel
	 */
	public DynamicCheckLogBean getDynamicCheckLog(DynamicCheckLogBean log);
	
	/** 
	 * @description 
	 * @param bean
	 * @return
	 * @returnType int
	 * @create 2015-7-2 下午04:56:38
	 * @author gel
	 */
	public int saveDynamicCheckLog(DynamicCheckLogBean bean);
	
	/**
	 * 
	 * @descripion 获取盘点日志总数
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	public Long getDynamicCheckLogCount(HashMap<String,Object> condition);
	
	/**
	 * 
	 * @descripion 获取盘点日志列表
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	public List<HashMap<String,Object>> getDynamicCheckLogLst(HashMap<String,Object> condition);
	
	/**
	 * 
	 * @descripion 删除盘点日志
	 * @author 刘仁华
	 * @time  2015年7月4日
	 */
	public int delDynamicCheckLog(Long id);
	
	/**
	 * 
	 * @descripion 根据盘点日志获取盘点记录数
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	public Long getCheckDataCountByLogId(Long id);
	
}
