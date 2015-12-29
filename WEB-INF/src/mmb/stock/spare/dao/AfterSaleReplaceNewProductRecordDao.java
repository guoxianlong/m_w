package mmb.stock.spare.dao;

import java.util.HashMap;
import java.util.List;

import mmb.stock.spare.model.AfterSaleReplaceNewProductRecord;

public interface AfterSaleReplaceNewProductRecordDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AfterSaleReplaceNewProductRecord record);

    int insertSelective(AfterSaleReplaceNewProductRecord record);

    AfterSaleReplaceNewProductRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AfterSaleReplaceNewProductRecord record);

    int updateByPrimaryKey(AfterSaleReplaceNewProductRecord record);
    
    AfterSaleReplaceNewProductRecord selectByCondition(String condition);
    
    int updateByCondition(String set, String condition);
    
    int getReplaceRecordCount(HashMap<String,String> conditionMap);
    
    List<AfterSaleReplaceNewProductRecord> getReplaceRecordList(HashMap<String,String> conditionMap);

    /**
     * 更新销售后台的处理单
     * @param sql
     * @return
     * 2014-10-30
     * lining
     */
    int updateAfterSaleWareHourceProductRecord(String sql);
    /**
     * 获取销售后台的处理单type值
     * @param id
     * @return
     * 2014-10-30
     * lining
     */
    int getAfterSaleWareHourceProductRecordType(String sql);

}