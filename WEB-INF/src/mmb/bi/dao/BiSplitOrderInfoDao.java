package mmb.bi.dao;

import java.util.HashMap;
import java.util.List;

import mmb.bi.model.BiSplitOrderInfo;

public interface BiSplitOrderInfoDao {
    public List<HashMap<String, Object>> getSplitOrderList(HashMap<String, Object> paramMap);
}