package mmb.dcheck.dao;

import mmb.dcheck.model.DynamicCheckData;

/**
 * @description 盘点日志
 * @create 2015-7-2 下午03:58:36
 * @author gel
 */
public interface DynamicCheckDataDao {

	/** 
	 * @description 
	 * @param data
	 * @return
	 * @returnType DynamicCheckLogBean
	 * @create 2015-7-2 下午04:03:40
	 * @author gel
	 */
	public DynamicCheckData getDynamicCheckData(DynamicCheckData data);
	
	/** 
	 * @description 
	 * @param bean
	 * @return
	 * @returnType int
	 * @create 2015-7-2 下午04:56:38
	 * @author gel
	 */
	public int saveDynamicCheckData(DynamicCheckData bean);
	
	/** 
	 * @description 
	 * @param id
	 * @return
	 * @returnType int
	 * @create 2015-7-2 下午09:23:41
	 * @author gel
	 */
	public int deleteDynamicCheckData(int id);
}
