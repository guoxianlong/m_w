package mmb.tms.dao;

import java.util.List;

import mmb.tms.model.BalanceCorpInfo;

public interface BalanceCorpInfoDao {
    

    BalanceCorpInfo selectByPrimaryKey(Integer id);

    /**
     * 获取结算公司列表
     * @return
     */
    List<BalanceCorpInfo> getBalanceCorpInfoList();
}