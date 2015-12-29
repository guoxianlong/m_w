package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import mmb.rec.oper.bean.StockBatchBean;
import mmb.rec.oper.bean.StockBatchLogBean;
import mmb.rec.oper.bean.StockCardBean;
import adultadmin.bean.stock.ProductStockBean;

public interface ProductStockDao {

	/**
	 * 添加ProductStock
	 * @param ProductStockBean
	 * @return
	 */
	public int addProductStock(ProductStockBean productStockBean);
	/**
	 * 查ProductStock
	 * @param condition
	 * @return
	 */
	public ProductStockBean getProductStock(String condition);
	
	/**
	 * 查询符合条件的ProductStock的List
	 * @param paramMap
	 * @return
	 */
	public List<ProductStockBean> getProductStockList(Map<String,String> paramMap);
	
	/**
	 * 查询符合条件的ProductStock的List
	 * @param paramMap
	 * @return
	 */
	public List<ProductStockBean> getProductStockListSlave(Map<String,String> paramMap);


	/**
	 * 更新商品可用量库存
	 * @param id
	 * @param count
	 * @return
	 */
	boolean updateStockCount(int id, int count);

	/**
	 * 更新商品
	 * @param id
	 * @param count
	 * @return
	 */
	boolean updateStockLockCount(int id, int count);
	
	/**
	 * 增加商品进销存卡片
	 * @param bean
	 * @return
	 */
	int insertStockCardBean(StockCardBean bean);
	
	/**
	 * 查询库存批次列表
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	List<StockBatchBean> getStockBatchBeanList(String condition, int index, int count, String orderBy);
	
	/**
	 * 删除批次
	 * @param id
	 * @return
	 */
	boolean deleteStockBatchBean(Integer id);
	
	/**
	 * 更新批次
	 * @param set
	 * @param condition
	 * @return
	 */
	boolean udpateStockBatchBean(String set, String condition);

	/**
	 * 增加库存批次日志
	 * @param bean
	 * @return
	 */
	boolean insertStockBatchLogBean(StockBatchLogBean bean) ;
	
}
