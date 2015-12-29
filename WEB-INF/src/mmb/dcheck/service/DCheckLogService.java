package mmb.dcheck.service;

import java.util.Map;

import mmb.dcheck.model.DynamicCheckLogBean;

public interface DCheckLogService {
	
	/** 
	 * @description 
	 * @param condition
	 * @return
	 * @returnType DynamicCheckLogBean
	 * @create 2015-7-2 下午04:23:08
	 * @author gel
	 */
	public DynamicCheckLogBean getDynamicCheckLog(Map<String, Object> condition);
	
	/** 
	 * @description 
	 * @param bean
	 * @return
	 * @returnType int
	 * @create 2015-7-2 下午04:55:17
	 * @author gel
	 */
	public int saveDynamicCheckLog(Map<String, Object> map);
	
	/**
	 * 
	 * @descripion 删除盘点日志
	 * @author 刘仁华
	 * @time  2015年7月4日
	 */
	public int delDynamicCheckLog(Long id);
}
