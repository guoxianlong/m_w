/*
 * Created on 2007-11-14
 *
 */
package adultadmin.service.infc;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import adultadmin.action.vo.voOrder;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;

import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PrintLogBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockPrintLogBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.GroupProductBean;
import adultadmin.bean.stock.MailingBatchBean;
import adultadmin.bean.stock.MailingBatchPackageBean;
import adultadmin.bean.stock.MailingBatchParcelBean;
import adultadmin.bean.stock.ProductGroupBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockHistoryBean;
import adultadmin.bean.stock.StockOperationBean;
import adultadmin.bean.stock.StockProductHistoryBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.util.db.DbOperation;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：
 */
public interface IStockService extends IBaseService {
	public ArrayList getSupplierStandarInfoList(String condition, int index,
			int count, String orderBy);
    //product_group
    public boolean addProductGroup(ProductGroupBean bean);

    public ProductGroupBean getProductGroup(String condition);
    
    public voUser getVoUser(String condition);
    
    public SupplierStandardInfoBean getSupplierStandardInfoBean(String condition);
    
    public int getProductGroupCount(String condition);

    public boolean updateProductGroup(String set, String condition);

    public boolean deleteProductGroup(String condition);

    public ArrayList getProductGroupList(String condition, int index,
            int count, String orderBy);
    
    //group_product
    public boolean addGroupProduct(GroupProductBean bean);

    public GroupProductBean getGroupProduct(String condition);

    public int getGroupProductCount(String condition);

    public boolean updateGroupProduct(String set, String condition);

    public boolean deleteGroupProduct(String condition);

    public ArrayList getGroupProductList(String condition, int index,
            int count, String orderBy);
    
    //stock_history
    public boolean addStockHistory(StockHistoryBean bean);

    public StockHistoryBean getStockHistory(String condition);

    public int getStockHistoryCount(String condition);

    public boolean updateStockHistory(String set, String condition);

    public boolean deleteStockHistory(String condition);

    public ArrayList getStockHistoryList(String condition, int index,
            int count, String orderBy);
    
    //stock_operation
    public boolean addStockOperation(StockOperationBean bean);

    public StockOperationBean getStockOperation(String condition);

    public int getStockOperationCount(String condition);

    public boolean updateStockOperation(String set, String condition);

    public boolean deleteStockOperation(String condition);

    public ArrayList getStockOperationList(String condition, int index,
            int count, String orderBy);
    
    //  stock_admin_history
    public boolean addStockAdminHistory(StockAdminHistoryBean bean);

    public StockAdminHistoryBean getStockAdminHistory(String condition);

    public int getStockAdminHistoryCount(String condition);

    public boolean updateStockAdminHistory(String set, String condition);

    public boolean deleteStockAdminHistory(String condition);

    public ArrayList getStockAdminHistoryList(String condition, int index,
            int count, String orderBy);

    // Print log
    public boolean addPrintLog(PrintLogBean bean);

    public PrintLogBean getPrintLog(String condition);

    public int getPrintLogCount(String condition);

    public boolean updatePrintLog(String set, String condition);

    public boolean deletePrintLog(String condition);

    public ArrayList getPrintLogList(String condition, int index,
            int count, String orderBy);
    
    public ArrayList getSupplierBankAccountList(String condition, int index,
            int count, String orderBy);

    // stock_product_history
    public boolean addStockProductHistory(StockProductHistoryBean bean);

    public StockProductHistoryBean getStockProductHistory(String condition);

    public int getStockProductHistoryCount(String condition);

    public boolean updateStockProductHistory(String set, String condition);

    public boolean deleteStockProductHistory(String condition);

    public ArrayList getStockProductHistoryList(String condition, int index,
            int count, String orderBy);
    // buy_stock
    public boolean addBuyStock(BuyStockBean bean);

    public BuyStockBean getBuyStock(String condition);

    public int getBuyStockCount(String condition);

    public boolean updateBuyStock(String set, String condition);

    public boolean deleteBuyStock(String condition);

    public ArrayList getBuyStockList(String condition, int index,
            int count, String orderBy);

    // buy_stock_product
    public boolean addBuyStockProduct(BuyStockProductBean bean);

    public BuyStockProductBean getBuyStockProduct(String condition);

    public int getBuyStockProductCount(String condition);

    public boolean updateBuyStockProduct(String set, String condition);

    public boolean deleteBuyStockProduct(String condition);

    public ArrayList getBuyStockProductList(String condition, int index,
            int count, String orderBy);
    
    //  buy_admin_history
    public boolean addBuyAdminHistory(BuyAdminHistoryBean bean);

    public BuyAdminHistoryBean getBuyAdminHistory(String condition);

    public int getBuyAdminHistoryCount(String condition);

    public boolean updateBuyAdminHistory(String set, String condition);

    public boolean deleteBuyAdminHistory(String condition);

    public ArrayList getBuyAdminHistoryList(String condition, int index,
            int count, String orderBy);

    // buy_order
    public boolean addBuyOrder(BuyOrderBean bean);

    public BuyOrderBean getBuyOrder(String condition);

    public int getBuyOrderCount(String condition);

    public boolean updateBuyOrder(String set, String condition);

    public boolean deleteBuyOrder(String condition);

    public ArrayList getBuyOrderList(String condition, int index,
            int count, String orderBy);

    // buy_order_product
    public boolean addBuyOrderProduct(BuyOrderProductBean bean);

    public BuyOrderProductBean getBuyOrderProduct(String condition);

    public int getBuyOrderProductCount(String condition);

    public boolean updateBuyOrderProduct(String set, String condition);

    public boolean deleteBuyOrderProduct(String condition);

    public ArrayList getBuyOrderProductList(String condition, int index,
            int count, String orderBy);

    // buy_stockin
    public boolean addBuyStockin(BuyStockinBean bean);

    public BuyStockinBean getBuyStockin(String condition);

    public int getBuyStockinCount(String condition);

    public boolean updateBuyStockin(String set, String condition);

    public boolean deleteBuyStockin(String condition);

    public ArrayList getBuyStockinList(String condition, int index,
            int count, String orderBy);

    // buy_stockin_product
    public boolean addBuyStockinProduct(BuyStockinProductBean bean);

    public BuyStockinProductBean getBuyStockinProduct(String condition);

    public int getBuyStockinProductCount(String condition);

    public boolean updateBuyStockinProduct(String set, String condition);

    public boolean deleteBuyStockinProduct(String condition);

    public ArrayList getBuyStockinProductList(String condition, int index,
            int count, String orderBy);

    // order_stock
    public boolean addOrderStock(OrderStockBean bean);

    public OrderStockBean getOrderStock(String condition);

    public int getOrderStockCount(String condition);

    public boolean updateOrderStock(String set, String condition);

    public boolean deleteOrderStock(String condition);

    public ArrayList getOrderStockList(String condition, int index, int count, String orderBy);

    // order_stock_product
    public boolean addOrderStockProduct(OrderStockProductBean bean);

    public OrderStockProductBean getOrderStockProduct(String condition);

    public int getOrderStockProductCount(String condition);

    public boolean updateOrderStockProduct(String set, String condition);

    public boolean deleteOrderStockProduct(String condition);

    public ArrayList getOrderStockProductList(String condition, int index,
            int count, String orderBy);
    
    //order_stock_product_cargo
    public boolean addOrderStockProductCargo(OrderStockProductCargoBean bean);

    public OrderStockProductCargoBean getOrderStockProductCargo(String condition);

    public int getOrderStockProductCargoCount(String condition);

    public boolean updateOrderStockProductCargo(String set, String condition);

    public boolean deleteOrderStockProductCargo(String condition);

    public ArrayList getOrderStockProductCargoList(String condition, int index,
            int count, String orderBy);
    
    public boolean addStockBatch(StockBatchBean bean);
    
    public boolean updateStockBatch(String set, String condition);
    
    public boolean deleteStockBatch(String condition);
    
    public ArrayList getStockBatchList(String condition, int index,
            int count, String orderBy);
    
    public StockBatchBean getStockBatch(String condition);
    
    public int getStockBatchCount(String condition);

    public boolean addStockBatchLog(StockBatchLogBean bean);
    
    public ArrayList getStockBatchLogList(String condition, int index,
            int count, String orderBy);
    
    public int getStockBatchLogCount(String condition);
    
    public StockBatchLogBean getStockBatchLog(String condition);
    
    public boolean updateStockBatchLog(String set, String condition);
    
    public boolean deleteStockBatchLog(String condition);
    //核对包裹
    public boolean addAuditPackage(AuditPackageBean bean);
    
    public ArrayList getAuditPackageList(String condition, int index,
            int count, String orderBy);
    
    public int getAuditPackageCount(String condition);
    
    public AuditPackageBean getAuditPackage(String condition);
    
    public boolean updateAuditPackage(String set, String condition);
    
    public boolean deleteAuditPackage(String condition);
    
    //发货清单打印日志
    public boolean addOrderStockPrintLog(OrderStockPrintLogBean bean);
    
    public ArrayList getOrderStockPrintLogList(String condition, int index,
            int count, String orderBy);
    
    public int getOrderStockPrintLogCount(String condition);
    
    public OrderStockPrintLogBean getOrderStockPrintLog(String condition);
    
    public boolean updateOrderStockPrintLog(String set, String condition);
    
    public boolean deleteOrderStockPrintLog(String condition);
    
    //物流自配送——发货波次
    public boolean addMailingBatch(MailingBatchBean bean);
    
    public ArrayList getMailingBatchList(String condition, int index,
            int count, String orderBy);
    
    public int getMailingBatchCount(String condition);
    
    public MailingBatchBean getMailingBatch(String condition);
    
    public boolean updateMailingBatch(String set, String condition);
    
    public boolean deleteMailingBatch(String condition);
    
    //物流自配送——发货发货邮包
    public boolean addMailingBatchParcel(MailingBatchParcelBean bean);
    
    public ArrayList getMailingBatchParcelList(String condition, int index,
            int count, String orderBy);
    
    public int getMailingBatchParcelCount(String condition);
    
    public MailingBatchParcelBean getMailingBatchParcel(String condition);
    
    public boolean updateMailingBatchParcel(String set, String condition);
    
    public boolean deleteMailingBatchParcel(String condition);
    
    //物流自配送——邮包中的包裹
    public boolean addMailingBatchPackage(MailingBatchPackageBean bean);
    
    public ArrayList getMailingBatchPackageList(String condition, int index,
            int count, String orderBy);
    
    public int getMailingBatchPackageCount(String condition);
    
    public MailingBatchPackageBean getMailingBatchPackage(String condition);
    
    public boolean updateMailingBatchPackage(String set, String condition);
    
    public boolean deleteMailingBatchPackage(String condition);
    
    public int getGroupCount(String condition);
    
    /**
     * 获得批次创建时间
     * @param batchCode
     * @return
     */
    public String getStockBatchCreateDatetime(String batchCode,int productId);
    
    
    /**
     * 产生采购入库单条码
     * @return
     */
	public String generateBuyStockinCodeBref();
	
	
	/**
	 * 自动完成采购订单，采购订单下预计到货单，预计到货单下采购入库单
	 * @param user
	 * @param stock
	 * @param buyOrder
	 * @param autoComplete
	 * @return
	 */
	public boolean completeBuyOrder(voUser user, BuyStockBean stock,
			BuyOrderBean buyOrder, boolean autoComplete);
	
    /**
	 * 
	 * 作者：张小磊
	 * 
	 * 创建日期：2012-12-10
	 * 
	 * 说明：客户系统可以通过此接口向企业服务平台发出自动判断或自动无法判断，由人工判断的指令，
	 * 以判断客户订单是否在顺丰的收派范围内。顺丰系统首先会根据收派双方的地址自动判断订单是否
	 * 在顺丰的收派范围内。如果系统无法判断，顺丰提供人工判断服务，人工判断后，再反馈结果给客
	 * 户或客户通过人工筛单查询接口查询订单是否在收派范围内。
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param address 订单派送地址
	 * @return true 可以配送 false 无法配送
	 */
    public boolean shunfengInterface1(String province,String city,String county,String address,String tel,float dprice);
    /**
	 * 
	 * 作者：张小磊
	 * 
	 * 创建日期：2012-12-14
	 * 
	 * 下单接口提供以下三个功能：
     * 1.客户系统向顺丰下发订单。
     * 2.生成电子运单图片，并回传给客户系统。客户可根据运单图片，自主打印电子运单，然后贴到投寄包裹上。
     * 3.为订单分配运单号。
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param custid 客户id
	 * @param orderid 订单id
	 * @return true 可以配送 false 无法配送
	 */
    public String shunfengInterface2(String orderid,String province,String city,String county,String address,String tel,String d_cityid,String contact,String cargo,float dprice) ;
    
    /**  
     * 此方法描述的是： 获取订单扩展信息 
     * @author: liubo  
     * @version: 2013-3-22 上午11:54:16  
     * @throws SQLException 
     */  
    
    public voOrderExtendInfo getOrderExtendInfo(String condition) throws SQLException;
    
    /**
     * 生成订单评价码
     * @param order
     * @param dbOp
     * @return
     */
    public boolean createUserOrderCommentCode(voOrder order,DbOperation dbOp);
    
    /**
     * 在事物中修改刚添加的入库单的code 将其后几位改为id 的后几位
     * @param brefCode
     * @param dbOp
     * @param bean
     * @return
     */
    public boolean fixBuyStockinCode(String brefCode, DbOperation dbOp, BuyStockinBean bean);
    
    /**
	 * 根据dbOp的上一个插入的id来修改code的后五位后缀为id 的后五位不满五位的补零。
	 * @param dbOp
	 * @param brefCode
	 * @return
	 */
	public String getFixedBuyStockinCode(String brefCode, int id);
	
	
    
    /**
     * 这个方法在取批次的时候 会将code的首字符截取为 codeType 并会按照给定的顺序排序 最好利用FEILD函数
     * @param condition
     * @param index
     * @param count
     * @param orderBy
     * @return
     */
	public List getStockBatchListWithSomeoneComeFirst(String condition, int index,
			int count, String orderBy);
}
