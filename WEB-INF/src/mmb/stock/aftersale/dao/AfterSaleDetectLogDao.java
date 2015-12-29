package mmb.stock.aftersale.dao;

import mmb.stock.aftersale.AfterSaleDetectLogBean;


public interface AfterSaleDetectLogDao {
	/**
	 * 
	 * @param record
	 * @return 返回id
	 * 2014年11月24日
	 * user
	 */
	int insert(AfterSaleDetectLogBean record);
	
	int insertSaleDetectLog(String sql);
}
