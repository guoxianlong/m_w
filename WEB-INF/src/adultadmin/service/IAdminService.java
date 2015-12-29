/**
 * 
 */
package adultadmin.service;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductLineCatalog;
import adultadmin.action.vo.voProductProperty;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IProductDiscountService;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 * 
 */

public interface IAdminService {

	public void close();

	public boolean startTransaction();
	public boolean commitTransaction();
	public boolean rollbackTransaction();
	public int executeUpdate(String sql);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-11-6
	 * 
	 * 说明：使用当前的 service 中的 Connection对象，构造一个 DbOperation 对象，用来在Service之间共享 数据库链接， 处理数据库事务
	 * 
	 * 参数及返回值说明：
	 * 
	 * @return
	 */
	public DbOperation getDbOperation();

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-10-15
	 * 
	 * 说明：根据id,获取number表里的数字
	 * 
	 * id=1 是前台生成订单编号时使用
	 * id=2,4,5,6 都是生成订单编号时使用
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 * @return
	 */
	public int getNumber(int id);
    public void setNumber(int id, int number);
	
	public List getOrders(int status, int buymode, int start, int limit);
	public List getOrders2(int status, int buymode, int start, int limit, int minId);
	public List getOrders(int status, int buymode, int start, int limit, String orderBy);
	public List getOrders2(int status, int buymode, int start, int limit,int minId, String orderBy);
	public List getOrders(int status, int buymode, int flat, int start, int limit);
	public List getOrders(int status, int buymode, int flat, int start, int limit, int minId);
	public List getOrders(int status, int buymode, int start, int flat, int limit, String orderBy);
	public List getOrders(int status, int buymode, int start, int flat, int limit, int minId, String orderBy);
	public int getOrderCount(String condition);
	public int getLackOrderCount(String condition);
	public int getOrderCount(int status, int buymode);
	public int getOrderCount(int status, int buymode, int flat);
	public voOrder getOrder(int id); 
	public voOrder getOrder(String condition); 
	public voOrder getMajorOrderByFlag(String condition, long flag);
	public List getOrders(String condition, int index, int count, String orderBy);
	public List getOrdersWithProName(String condition, int index, int count, String orderBy);
	public List getOrdersByProduct(String condition, int index, int count, String orderBy);
	public voProductProperty searchProductPropertyByProductId(int id);

	public float getOrderPrice3(int orderId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-10-20
	 * 
	 * 说明：获取所有订单状态的列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @return
	 */
	public List getOrderStatusList();

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-1
	 * 
	 * 说明：根据条件返回订单列表，其中a为user_order表、b为user_order_status表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @param orderBy
	 * @return
	 */
	public List getOrdersWithProductName(String condition, int start, int limit, String orderBy);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-11-21
	 * 
	 * 说明：根据订单中的产品ID和其他条件查询订单。<br/>
	 * 		其中 a为Order表、b为user_order_product表<br/>
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param productId
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getOrders(String condition, int productId, int index, int count, String orderBy);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：根据订单中的赠品的产品ID和其他条件查询订单。<br/>
	 * 		其中 a为Order表、b为user_order_present表<br/>
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param productId
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getOrdersByPresent(String condition, int productId, int index, int count, String orderBy);
	
	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2012-04-12
	 * 
	 * 说明：根据订单中的产品ID和其他条件查询订单。<br/>
	 * 		其中 a为Order表、b为user_order_product表<br/>
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param productId
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getOrdersByProducts(String condition, int index, int count, String orderBy);

	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2012-04-12
	 * 
	 * 说明：根据订单中的赠品的产品ID和其他条件查询订单。<br/>
	 * 		其中 a为Order表、b为user_order_present表<br/>
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param productId
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getOrdersByPresents(String condition, int index, int count, String orderBy);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：获取一个订单中的商品列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public List getOrderProducts(int orderId);
	
	/**
	 * 
	 * 作者：szl
	 * 
	 * 创建日期：2012-11-09
	 * 
	 * 说明：获取一个订单中的商品列表(更改后用的是user_order_promotion_product)
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public List getOrderProducts1(int orderId);
	
	/**
	 * 
	 * 作者：赵林
	 * 
	 * 创建日期：2011-08-25
	 * 
	 * 说明：根据条件，获取一个订单中的商品列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public List getOrderProducts(String condition);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：获取一个订单中的商品列表（把套装拆分以后的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public List getOrderProductsSplit(int orderId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-4-24
	 * 
	 * 说明：获取订单中的某个商品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public voOrderProduct getOrderProduct(int orderId, String productCode);
	
	public voOrderProduct getOrderPromotionProduct(int orderId, String productCode);
	
	public int getOrderIdByPromotionProduct(int id); 
	
	public voOrderProduct getOrderPromotionProduct(int orderProductId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-11-11
	 * 
	 * 说明：获取订单中的某个商品，根据 user_order_product 表中的ID进行查询
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderProductId
	 * @return
	 */
	public voOrderProduct getOrderProduct(int orderProductId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：获取订单中的某个商品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @param productCode
	 * @return
	 */
	public voOrderProduct getOrderProduct(String condition);

	/**
	 * 
	 * 作者：张小磊
	 * 
	 * 创建日期：2012-5-4
	 * 
	 * 说明：获取订单中的某个商品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @param productCode
	 * @return
	 */
	public voOrderProduct getOrderProductSplit(int orderId, String productCode);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：获取一个订单中的赠品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public List getOrderPresents(int orderId);

	/**
	 * 
	 * 功能:根据条件获取订单赠品中的商品
	 * <p>作者 李双 Dec 2, 2011 2:06:10 PM
	 * @param condition
	 * @return
	 */
	public List getOrderPresents(String condition);
	
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：过去一个订单中的赠品列表（套装产品拆分过的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public List getOrderPresentsSplit(int orderId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-4-24
	 * 
	 * 说明：获取一个订单中的某个赠品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @return
	 */
	public voOrderProduct getOrderPresent(int orderId, String productCode);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-11-11
	 * 
	 * 说明：获取一个订单中的某个赠品，根据user_order_present表中的ID进行检索
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderPresentId
	 * @return
	 */
	public voOrderProduct getOrderPresent(int orderPresentId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：获取一个订单中的某个赠品(套装产品拆分过的)
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @param productCode
	 * @return
	 */
	public voOrderProduct getOrderPresentSplit(int orderId, String productCode);

	public List getProducts(String condition);
	public List getProductsForPresent(String condition);
	public List getProducts(String condition, int start, int limit);
	public List getProducts(String condition, int start, int limit, String orderBy);
	public List getProductList(String condition, int start, int limit, String orderBy);
	public List getProductList1(String condition, int start, int limit, String orderBy);
	public List getProductList2(String condition, int start, int limit, String orderBy);
	public Hashtable getOrderStockProductList(String sql);
	public Hashtable getOrderStockProductList(List list);
	public Hashtable getBuyStockinProductList(String sql);
	public int getProductCount(String condition);
	public void addProduct(voProduct vo);
	public void modifyProduct(int id, voProduct vo);
	public void modifyProductMark(int id, voProduct vo);
	public void deleteProduct(int id);
	public voProduct getProduct(int id);
	/**
	 * 李宁
	 * 功能：获取产品（product表）信息
	 * @param id
	 * @return
	 */
	public voProduct getProduct2(int id);
	public voProduct getProductSimple(int id);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-6-2
	 * 
	 * 说明：获取产品 及其 库存信息（待出货的库存也要计算在内）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @param orderBy
	 * @return
	 */
	public List getProductStockList(String condition, int start, int limit, String orderBy);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-12
	 * 
	 * 说明：新的 产品库存信息列表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param start
	 * @param limit
	 * @param orderBy
	 * @return
	 */
	public List getProductStockList2(String condition, int start, int limit, String orderBy);
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-4-24
	 * 
	 * 说明：与别的Service使用共同的数据库连接，方便事务处理
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 * @param dbOp
	 * @return
	 */
	public voProduct getProduct(int id, DbOperation dbOp);
	public voProduct getProduct(String code);
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-2-16
	 * 
	 * 说明：根据不同的条件查询 product 表
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @return
	 */
	public voProduct getProduct2(String condition);
	public void updateProductCode(int id);
	
	public List getCatalogs();
	public List getCatalogs(String condition);
	public voCatalog getCatalog(String condition);
	public voUser getAdmin(String username, String password);
	public voUser getUser(int userId);
	public voUser getUser(String username);
	
	public voUser getAdminUser(int userId);
	public voUser getAdminUser(String username);
	
	public List getAdminList(String query);
	
	public List getSelects(String table, String condition);
	public voSelect getSelect(String table, String condition);
	public String getString(String field, String table, String condition);
	
	// 搜索功能
	public List searchProduct(String code, String name, String price);
	public List searchProduct(String code, String name, String price, String minPrice, String maxPrice);
	public List searchProduct(String condition, int startIndex, int limit, String orderBy);
	public List searchProduct(String condition, int startIndex, int limit, String orderBy,String present);
	public List searchOrder(String condition);
	public List searchOrder2(String condition);
	public List searchOrder(String condition, String useOrderIndex);
	public List searchOrder2(String condition, String useOrderIndex);
	public List searchOrderByUserId(int userId);
	public List searchOrderByProductId(int productId, int status, int buymode);
	
	public boolean setOrderStatus(int id, int status);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：修改订单中商品的数量
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderProductId
	 * @param productCode
	 * @param orderProductCount
	 */
	public void modifyOrderProduct(int orderProductId, String productCode, int orderProductCount);	
	// 修改订单
	public void modifyOrderPromotionProduct(int orderProductId, String productCode, int orderProductCount);		// 修改订单

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：删除订单中的商品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 */
	public void deleteOrderProduct(int id);
	
	public void deleteOrderProduct(int orderId, int productId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：删除订单中的所有商品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 */
	public void deleteOrderProducts(int orderId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：删除订单中的商品（套装拆分后的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 */
	public void deleteOrderProductSplit(int id);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：删除订单中的所有商品（套装拆分后的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 */
	public void deleteOrderProductsSplit(int orderId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：修改订单中赠品的数量
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderProductId
	 * @param productCode
	 * @param orderProductCount
	 */
	public void modifyOrderPresent(int orderProductId, String productCode, int orderProductCount);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：删除订单中的赠品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 */
	public void deleteOrderPresent(int id);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：删除订单中的所有赠品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 */
	public void deleteOrderPresents(int orderId);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：删除订单中的赠品（套装拆分后的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 */
	public void deleteOrderPresentSplit(int id);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：删除订单中的所有商品（套装拆分后的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 */
	public void deleteOrderPresentsSplit(int orderId);

	public void updateOrderImage(voOrder vo);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-1
	 * 
	 * 说明：向订单中添加一个商品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId 订单order的id
	 * @param code 商品product的code
	 * @param count orderProduct的count
	 */
	public void addOrderProduct(int orderId, String productCode, int orderPresentCount);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：向订单中添加一个商品， 套装拆分后的
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @param productCode
	 * @param orderPresentCount
	 */
	public boolean addOrderProductSplit(int orderId, String productCode, int orderPresentCount);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-10-28
	 * 
	 * 说明：更新订单产品表中的一条记录（套装拆分过的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param set
	 * @param id
	 */
	public boolean updateOrderProductSplit(String set, int id);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：向订单中添加一个赠品
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @param productCode
	 * @param orderProductCount
	 */
	public void addOrderPresent(int orderId, String productCode, int orderProductCount);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-13
	 * 
	 * 说明：向订单中添加一个赠品（套装拆分过的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @param productCode
	 * @param orderProductCount
	 */
	public boolean addOrderPresentSplit(int orderId, String productCode, int orderProductCount);

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-10-28
	 * 
	 * 说明：更新订单赠品表中的一条记录（套装拆分过的）
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param set
	 * @param id
	 */
	public boolean updateOrderPresentSplit(String set, int id);

	public void modifyOrder(int id, int status);
	public void modifyOrder(String set, String condition);
	public void modifyOrder(voOrder vo);
	public int addOrder(voOrder vo);
	public int getOrderStatus(int id);
	public void replaceOrderStatus(int oldstatus, int status, int buymode);
	
	// 汇总功能喝管理
	public List getCollectProductList(int status, int buymode);
	public List getCollectPresentList(int status, int buymode);
	
	public List getCollectProductList(String selects);
	public List getCollectPresentList(String selects);
	public List getOrders(String selects);
	
	public int getNextOrderAndLackId(String orderType);
	public int getNextOrderAndOnLine(String lastDealTime, String operator);
	
	public void modifyOrderPrice(int id, float price);
	public void modifyOrderPrice(int id, float price, float dprice);
	public void modifyOrderPrice(int id, float price, float dprice, float postage);	//把邮费也更新 lbj_20071222
	public float calcOrderPrice(int id);
	public float calcOrderPrice(int id, int flat);	//flat表示是WEB的订单或WAP订单，WEB为1，WAP为0 lbj_20071222
	public float calcOrderPrice(int id, int flat, boolean isGroupRate);	//flat表示是WEB的订单或WAP订单，WEB为1，WAP为0 lbj_20071222
	public float calcOrderPrice(int id, int flat, boolean isGroupRate, boolean isFrequentUser); // 团购订单，老用户订单打折
	public int calCpaBonus(int orderId, int siteId, float voPrice);	//计算可分成费用
	public void modifyCpaBonus(int orderId, int bonus);	//把邮费也更新 lbj_20071222
	public int getOrderIdByProduct(int id);	// 由订单的一个产品获得订单id0
	
	public float calcOrderByGroupRateProductPrice(int id, int flat);
	
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2007-12-6
	 * 
	 * 说明：通过一个订单中的赠品的ID来获取该订单的ID
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param id
	 * @return
	 */
	public int getOrderIdByPresent(int id);
	
	/**
     * 通过产品id查询产品编号 从而查询是否缺货
     */
    public String getProductCode(String pid);
  //检查这个添加的id是否存在
    public boolean checkProductId(String id);
	
	/**
	 * 查询产品线信息
	 * @param sql	查询条件(where语句之后的条件,字段之前需要加表名 例:product_line.id)
	 * @return
	 */
	public voProductLine getProductLine(String sql);
	
	/**
	 * 查询产品线信息
	 * @param sql	查询条件(where语句之后的条件,字段之前需要加表名 例:product_line.id)
	 * @return
	 */
	public List getProductLineList(String sql);
	
	/**
	 * 查询产品线关联分类信息
	 * @param sql	查询条件(where语句之后的条件,字段之前需要加表名 例:product_line_catalog.catalog_id)
	 * @return
	 */
	public List getProductLineListCatalog(String sql);

	/**
	 * 添加产品线信息
	 * @param vpl
	 * @return
	 */
	public void addProductLine(voProductLine vpl);
	
	/**
	 * 添加产品线关联分类
	 * @param vpl
	 * @return
	 */
	public void addProductLineCatalog(voProductLineCatalog vplc);
	
	
	/**
	 * 更新产品线信息
	 * @param vpl
	 * @return
	 */
	public void updateProductLine(voProductLine vpl);
	
	/**
	 * 更新产品线关联分类
	 * @param vpl
	 * @return
	 */
	public void updateProductLineCatalog(voProductLineCatalog vplc);
	
	/**
	 * 删除产品线信息
	 * @param sql 删除条件(where语句之后的条件)
	 * @return
	 */
	public void deleteProductLine(String sql);
	
	/**
	 * 删除产品线关联信息
	 * @param sql 删除条件(where语句之后的条件)
	 * @return
	 */
	public void deleteProductLineCatalog(String sql);

	/**
	 * 功能:获取最后添加的id
	 * <p>作者文齐辉 2010-12-28 下午03:45:46
	 * @param table
	 * @return
	 */
	public int getLastInsertId(String table);

	public void addOrderProduct(int orderId, voOrderProduct orderProduct);
	
	/**
	 * 
	 * 功能:获取全部的品牌
	 * <p>作者 李双 Oct 13, 2011 9:41:37 AM
	 * @return
	 */
	public List getAllBrandList();
	/**
	 * 
	 * 功能:获取 发货人的订单类型
	 * <p>作者 李双 Oct 19, 2011 6:35:17 PM
	 * @param condition
	 * @return
	 */
	public List getOrdersAndOrderStock(String condition);
	
	/**
	 * 
	 * 功能:获取 销售人的订单类型 
	 * <p>作者 李双 Oct 19, 2011 6:35:17 PM
	 * @param condition
	 * @return
	 */
	public List getOrdersTypesOlny(String condition);
	//获取产品的历史销量
	public int getLiShiXiaoLiang(List productids);
	
	/**
	 * 
	 * 功能:查看订单电话索引表中是否存在该记录
	 * <p>作者 李双 Mar 8, 2012 2:57:40 PM
	 * @param orderId
	 * @param phone
	 * @return
	 */
	public boolean isContianUserOrderPhoneIndex(int orderId,String phone);
	
	/**
	 * 
	 * 功能:往订单电话索引表中添加记录
	 * <p>作者 李双 Mar 8, 2012 2:58:23 PM
	 * @param orderId
	 * @param orderCode
	 * @param phone
	 */
	public void addUserOrderPhoneIndex(int orderId,String name,String phone);
	
	/*
	 * 删除订单的电话索引表中记录
	 */
	public boolean delUserOrderPhoneIndex(int orderId,String phone);
	
	
	public boolean addOrderExtendInfo(voOrderExtendInfo vo);
	
	public boolean updateOrderExtendInfo(voOrderExtendInfo vo);
	
	public voOrderExtendInfo getOrderExtendInfo(int id);
	

	/**
	 * 根据条件获得对应产品品牌列表
	 * @param condition 查询条件
	 * @param start 起始索引
	 * @param limit 结束索引
	 * @param orderBy 排序
	 */
	public List getProductBrandList(String condition,int start,int limit,String orderBy);
	

	public voProductProperty getProductProperty(String condition);
	

	/**
	 * 根据条件，得到产品附加属性基本信息列表
	 * @param condition 条件
	 * @param start 起始索引
	 * @param end 结束索引
	 * @param orderby 排序
	 */
	public List getProductPropertyInfoList(String condition, int start, int limit,
			String orderBy);
	
	public void addOrderAdminStatusLog(IAdminLogService logService,int oriStatus,int status,int oriStockoutDeal,int stockoutDeal,OrderAdminLogBean log);
	
	public Map getCashTickets(IProductDiscountService ipds);
	
	public void deleteSendMsgAndAutolibrary(voOrder order);
	
	/**
	 * 增加子订单
	 *@author 李宁
	 *@date 2013-5-10 上午10:10:27
	 * @param vo
	 * @return
	 */
	public boolean addSubOrder(voOrder vo);
	
	/**
	 * 
	 * 功能:复制订单商品活动信息
	 * @param newId
	 * @param oldId
	 * @param productId
	 * @return
	 */
	public boolean copyUserOrderPromotionToSubOrder( int newId ,int oldId, int productId);
	
	/**
	 * 
	 * 功能:复制订单商品
	 * @param newId
	 * @param oldId
	 * @param productId
	 * @return
	 */
	public boolean copyUserOrderProductToSubOrder(int newId ,int oldId,  int productId);
}