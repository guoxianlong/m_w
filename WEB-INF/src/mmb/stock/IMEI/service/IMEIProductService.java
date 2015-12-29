package mmb.stock.IMEI.service;

import java.util.List;
import java.util.Map;

import mmb.stock.IMEI.model.ImeiProductLog;


public interface IMEIProductService {
	/**
	 * 保存数据到数据库
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	void saveImeiProductSet(List<ImeiProductLog> list,List<String> productCodeList);

	/**
	 * 查询IMEI产品属性设置表
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	List<ImeiProductLog> queryIMEIProductLog(int pageNo, int pageSize);

	/**
	 * 查询IMEI产品属性设置表总记录数
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	int queryIMEIProductLogCount(Map<String,String> map);

	/**
	 * 返回已经存在的productCodeList
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	List<String> queryIMEIProductCode(List<String> productCodeList);
}
