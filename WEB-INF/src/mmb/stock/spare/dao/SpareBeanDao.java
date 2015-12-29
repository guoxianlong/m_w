package mmb.stock.spare.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.spare.model.SpareBean;
import mmb.stock.spare.model.SpareProductDetailed;
import mmb.stock.spare.model.SpareUpShelves;

public interface SpareBeanDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareBean record);

    SpareBean selectByPrimaryKey(Integer id);
    
    SpareBean getSpareByCondition(HashMap<String,String> conditionMap);

    int updateByPrimaryKeySelective(SpareBean record);

    int updateByPrimaryKey(SpareBean record);
    
    SpareBean selectByCondition(String condition);
    
    int updateByCondition(String set, String condition);

	SpareProductDetailed getSpareProductDetailed(Map<String, String> map);

	int getSpareCargoStatus(Map<String, String> map);
	
	int updateSpareStatus(Map<String,String> map);
	int batchAddSpareList(List<SpareBean> list);

	List<SpareUpShelves> getSpareUpShelfList(Map<String, String> map);
	
	Map<String,String> getSupplierNameAndAddressBySpareCode(Map<String,String> map);
	int getSupplierIdBySpareCode(Map<String,String> map);
	
	List<SpareBean> getSpareList(HashMap<String,String> map);
	
	/**
	 * 换新机记录与备用机表联查
	 * @param conditionMap
	 * @return
	 * 2014-11-4
	 * lining
	 */
	SpareBean getSpareJoinReplaceRecord(HashMap<String,String> conditionMap);
	
	/**
	 * 查询备用机列表
	 * 联查出库备用机表
	 * @param conditionMap
	 * @return
	 * 2014-11-4
	 * lining
	 */
	List<SpareBean> getSpareListJoinBackSupplierProduct(HashMap<String,String> conditionMap);
	/**
	 * 查询备用机列表
	 * 联查备用机检测不合格更换单
	 * @param conditionMap
	 * @return
	 * 2014-11-7
	 * lining
	 */
	List<SpareBean> getSpareListJoinUnqualifiedReplace(String condition);
}