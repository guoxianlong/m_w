/*
 * Created on 2007-11-14
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;

import mmb.rec.stat.bean.QualifiedStockVolumeShareBean;
import mmb.rec.stat.bean.StockShareBean;
import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.ProductStockTuneLogBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-4-20
 * 
 * 说明：产品库存 
 */
public interface IProductStockService extends IBaseService {
    //product_stock
    public boolean addProductStock(ProductStockBean bean);

    public ProductStockBean getProductStock(String condition);

    public int getProductStockCount(String condition);

    public boolean updateProductStock(String set, String condition);

    public boolean deleteProductStock(String condition);

    public ArrayList getProductStockList(String condition, int index,
            int count, String orderBy);

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-6-23
     * 
     * 说明：修改产品库存数量，增加时，mcount为整数，减少时，mcount为负数
     * 
     * 参数及返回值说明：
     * 
     * @param id
     * @param mcount
     * @return
     */
    public boolean updateProductStockCount(int id, int mcount);

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-6-23
     * 
     * 说明：修改产品库存锁定数量，增加时，mcount为整数，减少时，mcount为负数
     * 
     * 参数及返回值说明：
     * 
     * @param id
     * @param mcount
     * @return
     */
    public boolean updateProductLockCount(int id, int mcount);


    //stock_exchange
    public boolean addStockExchange(StockExchangeBean bean);

    public StockExchangeBean getStockExchange(String condition);

    public int getStockExchangeCount(String condition);

    public boolean updateStockExchange(String set, String condition);

    public boolean deleteStockExchange(String condition);

    public ArrayList getStockExchangeList(String condition, int index,
            int count, String orderBy);


    //stock_exchange_product
    public boolean addStockExchangeProduct(StockExchangeProductBean bean);

    public StockExchangeProductBean getStockExchangeProduct(String condition);

    public int getStockExchangeProductCount(String condition);

    public boolean updateStockExchangeProduct(String set, String condition);

    public boolean deleteStockExchangeProduct(String condition);

    public ArrayList getStockExchangeProductList(String condition, int index,
            int count, String orderBy);
    
    //stock_exchange_product_cargo
    public boolean addStockExchangeProductCargo(StockExchangeProductCargoBean bean);

    public StockExchangeProductCargoBean getStockExchangeProductCargo(String condition);

    public int getStockExchangeProductCargoCount(String condition);

    public boolean updateStockExchangeProductCargo(String set, String condition);

    public boolean deleteStockExchangeProductCargo(String condition);

    public ArrayList getStockExchangeProductCargoList(String condition, int index,
            int count, String orderBy);    

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-5-27
     * 
     * 说明：检测产品的合格库存，如果为0，则自动调整为 下架(100)
     * 
     * 参数及返回值说明：
     * 
     * @param productId
     */
	public void checkProductStatus(int productId);
	
	/**
	 * 检测产品的合格库存，如果为0，则自动调整为 缺货130，并记录产品操作记录
	 *@author 李宁
	 *@date 2013-5-6 下午1:50:18
	 * @param productId
	 * @param user
	 */
	public void checkProductStatus(int productId,voUser user);

	//product_stock_tune_log
    public boolean addProductStockTuneLog(ProductStockTuneLogBean bean);

    public ProductStockTuneLogBean getProductStockTuneLog(String condition);

    public int getProductStockTuneLogCount(String condition);

    public boolean updateProductStockTuneLog(String set, String condition);

    public boolean deleteProductStockTuneLog(String condition);

    public ArrayList getProductStockTuneLogList(String condition, int index,
            int count, String orderBy);


    //stock_card
    public boolean addStockCard(StockCardBean bean);

    public StockCardBean getStockCard(String condition);

    public int getStockCardCount(String condition);

    public boolean updateStockCard(String set, String condition);

    public boolean deleteStockCard(String condition);

    public ArrayList getStockCardList(String condition, int index,
            int count, String orderBy);

	public boolean addStockShare(StockShareBean bean);

	public boolean addQualifiedStockVolumeShareBean(
			QualifiedStockVolumeShareBean bean);

	public boolean checkBuyStockinProductCount(DbOperation dbop, int stockinId,
			int editCount, int buyStockCount, int productId, int buyStockId);
	
	public StockAreaBean getStockArea(String condition);
}
