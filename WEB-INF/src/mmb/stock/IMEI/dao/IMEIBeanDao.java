package mmb.stock.IMEI.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.model.ImeiProductLog;


public interface IMEIBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(IMEIBean record);

    IMEIBean selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IMEIBean record);

    int updateByPrimaryKey(IMEIBean record);
    
    IMEIBean getIMEIByCondition(HashMap<String,String> conditionMap);
    
    int updateIMEIByCondition(Map<String,String> map);
    /**
     * 批量增加
     * @param list
     * @return
     */
    int batchInsertIMEI(List<IMEIBean> list);
    
    IMEIBean getIMEIBean(Map<String,String> map);

    /**
     * 批量更新imei码状态为2--可出库
     * @param list
     * @return
     */
    int batchUpdateIMEIStatus(List<IMEIBean> list);
    
    /**
     * 获取imei_product中相应商品id的数量（判断是否是imei码商品）
     * @param productId
     * @return
     * 2014年12月5日
     * user
     */
    int getImeiProductId(int productId);

    /**
	 * 保存数据到数据库
	 * @create yaoliang
	 * @time  2015-10-13 9:20:25
	 * @param request
	 * @param Response
	 */
	void saveImeiProductLog(List<ImeiProductLog> list);

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
	Integer queryIMEIProductLogCount(Map<String,String> map);

	/**
	 * 返回已经存在的productCodeList
	 * @create yaoliang
	 * @time  2015-10-13 09:10:25
	 * @param request
	 * @param Response
	 */
	List<String> queryIMEIProductCode(List<String> productCodeList);

	List<Integer> queryProductId(List<String> productCodeList);

	void saveImeiProduct(List<Integer> productIdList);
}