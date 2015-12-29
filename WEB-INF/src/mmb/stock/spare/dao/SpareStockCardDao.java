package mmb.stock.spare.dao;

import java.util.HashMap;
import java.util.List;

import mmb.stock.spare.model.SpareStockCard;

public interface SpareStockCardDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SpareStockCard record);

    SpareStockCard selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SpareStockCard record);
    
    /**
     * 批量插入备用机库存卡片
     * @param list
     * @return
     * 2014-11-4
     * lining
     */
    int batchInsertCard(List<SpareStockCard> list);
    
    int getHistoryStockCount(HashMap<String,String> condtionMap);
    /**
     * 获取备用机历史出入库列表
     * @param condtionMap
     * @return
     * 2014-11-3
     * lining
     */
    List<SpareStockCard> getHistoryStockList(HashMap<String,String> condtionMap);
}