/*
 * Created on 2007-11-14
 *
 */
package adultadmin.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;

import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.stat.ProductWarePropertyBean;
import net.sf.json.JSONObject;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transport.http.HTTPException;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PrintLogBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.buy.BuyPlanBean;
import adultadmin.bean.buy.BuyPlanProductBean;
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
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockHistoryBean;
import adultadmin.bean.stock.StockOperationBean;
import adultadmin.bean.stock.StockProductHistoryBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.test.InsertAction;
import adultadmin.util.Arith;
import adultadmin.util.Base64;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

import com.amazonaws.mws.MarketplaceWebService;
import com.amazonaws.mws.MarketplaceWebServiceClient;
import com.amazonaws.mws.MarketplaceWebServiceConfig;
import com.amazonaws.mws.MarketplaceWebServiceException;
import com.amazonaws.mws.model.FeedSubmissionInfo;
import com.amazonaws.mws.model.IdList;
import com.amazonaws.mws.model.ResponseMetadata;
import com.amazonaws.mws.model.SubmitFeedRequest;
import com.amazonaws.mws.model.SubmitFeedResponse;
import com.amazonaws.mws.model.SubmitFeedResult;
import com.amazonaws.mws.samples.SubmitFeedSample;
import com.hollycrm.bigaccount.webservice.printservice.GetPrintDatas;
import com.hollycrm.bigaccount.webservice.printservice.GetPrintDatasPortType;
import com.routdata.cussrvems.webservice.web.IOException;
import com.routdata.cussrvems.webservice.web.Uniservice;
import com.routdata.cussrvems.webservice.web.UniservicePortType;
import com.sf.module.serviceprovide.service.CustomerServiceService;
import com.sf.module.serviceprovide.service.FilterOrderServiceService;
import com.sf.module.serviceprovide.service.ICustomerService;
import com.sf.module.serviceprovide.service.IFilterOrderService;
/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：
 */
public class StockServiceImpl extends BaseServiceImpl implements IStockService {
	public StockServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public StockServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addProductGroup(ProductGroupBean bean) {
		return addXXX(bean, "product_group");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteProductGroup(String condition) {
		return deleteXXX(condition, "product_group");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ProductGroupBean getProductGroup(String condition) {
		return (ProductGroupBean) getXXX(condition, "product_group",
				"adultadmin.bean.stock.ProductGroupBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getProductGroupCount(String condition) {
		return getXXXCount(condition, "product_group", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getProductGroupList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_group",
				"adultadmin.bean.stock.ProductGroupBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateProductGroup(String set, String condition) {
		return updateXXX(set, condition, "product_group");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addGroupProduct(GroupProductBean bean) {
		return addXXX(bean, "group_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteGroupProduct(String condition) {
		return deleteXXX(condition, "group_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public GroupProductBean getGroupProduct(String condition) {
		return (GroupProductBean) getXXX(condition, "group_product",
				"adultadmin.bean.stock.GroupProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getGroupProductCount(String condition) {
		return getXXXCount(condition, "group_product", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getGroupProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "group_product",
				"adultadmin.bean.stock.GroupProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateGroupProduct(String set, String condition) {
		return updateXXX(set, condition, "group_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockHistory(StockHistoryBean bean) {
		return addXXX(bean, "stock_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockHistory(String condition) {
		return deleteXXX(condition, "stock_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockHistoryBean getStockHistory(String condition) {
		return (StockHistoryBean) getXXX(condition, "stock_history",
				"adultadmin.bean.stock.StockHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockHistoryCount(String condition) {
		return getXXXCount(condition, "stock_history", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockHistoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_history",
				"adultadmin.bean.stock.StockHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockHistory(String set, String condition) {
		return updateXXX(set, condition, "stock_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockOperation(StockOperationBean bean) {
		return addXXX(bean, "stock_operation");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockOperation(String condition) {
		return deleteXXX(condition, "stock_operation");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockOperationBean getStockOperation(String condition) {
		return (StockOperationBean) getXXX(condition, "stock_operation",
				"adultadmin.bean.stock.StockOperationBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockOperationCount(String condition) {
		return getXXXCount(condition, "stock_operation", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockOperationList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_operation",
				"adultadmin.bean.stock.StockOperationBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockOperation(String set, String condition) {
		return updateXXX(set, condition, "stock_operation");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockAdminHistory(StockAdminHistoryBean bean) {
		return addXXX(bean, "stock_admin_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockAdminHistory(String condition) {
		return deleteXXX(condition, "stock_admin_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockAdminHistoryBean getStockAdminHistory(String condition) {
		return (StockAdminHistoryBean) getXXX(condition, "stock_admin_history",
				"adultadmin.bean.stock.StockAdminHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockAdminHistoryCount(String condition) {
		return getXXXCount(condition, "stock_admin_history", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockAdminHistoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"stock_admin_history",
				"adultadmin.bean.stock.StockAdminHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockAdminHistory(String set, String condition) {
		return updateXXX(set, condition, "stock_admin_history");
	}

	/*

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addPrintLog(PrintLogBean bean) {
		return addXXX(bean, "print_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deletePrintLog(String condition) {
		return deleteXXX(condition, "print_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public PrintLogBean getPrintLog(String condition) {
		return (PrintLogBean) getXXX(condition, "print_log",
				"adultadmin.bean.PrintLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getPrintLogCount(String condition) {
		return getXXXCount(condition, "print_log", "id");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getPrintLogList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "print_log",
				"adultadmin.bean.PrintLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updatePrintLog(String set, String condition) {
		return updateXXX(set, condition, "print_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockProductHistory(StockProductHistoryBean bean) {
		return addXXX(bean, "stock_product_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockProductHistory(String condition) {
		return deleteXXX(condition, "stock_product_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockProductHistoryBean getStockProductHistory(String condition) {
		return (StockProductHistoryBean) getXXX(condition,
				"stock_product_history",
				"adultadmin.bean.stock.StockProductHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockProductHistoryCount(String condition) {
		return getXXXCount(condition, "stock_product_history", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockProductHistoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"stock_product_history",
				"adultadmin.bean.stock.StockProductHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockProductHistory(String set, String condition) {
		return updateXXX(set, condition, "stock_product_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyPlan(BuyPlanBean bean) {
		return addXXX(bean, "buy_plan");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyPlan(String condition) {
		return deleteXXX(condition, "buy_plan");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyPlanBean getBuyPlan(String condition) {
		return (BuyPlanBean) getXXX(condition, "buy_plan",
				"adultadmin.bean.buy.BuyPlanBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyPlanCount(String condition) {
		return getXXXCount(condition, "buy_plan", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyPlanList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_plan",
				"adultadmin.bean.buy.BuyPlanBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyPlan(String set, String condition) {
		return updateXXX(set, condition, "buy_plan");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyPlanProduct(BuyPlanProductBean bean) {
		return addXXX(bean, "buy_plan_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyPlanProduct(String condition) {
		return deleteXXX(condition, "buy_plan_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyPlanProductBean getBuyPlanProduct(String condition) {
		return (BuyPlanProductBean) getXXX(condition, "buy_plan_product",
				"adultadmin.bean.buy.BuyPlanProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyPlanProductCount(String condition) {
		return getXXXCount(condition, "buy_plan_product", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyPlanProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_plan_product",
				"adultadmin.bean.buy.BuyPlanProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyPlanProduct(String set, String condition) {
		return updateXXX(set, condition, "buy_plan_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyStock(BuyStockBean bean) {
		return addXXX(bean, "buy_stock");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyStock(String condition) {
		return deleteXXX(condition, "buy_stock");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyStockBean getBuyStock(String condition) {
		return (BuyStockBean) getXXX(condition, "buy_stock",
				"adultadmin.bean.buy.BuyStockBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyStockCount(String condition) {
		return getXXXCount(condition, "buy_stock", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyStockList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_stock",
				"adultadmin.bean.buy.BuyStockBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyStock(String set, String condition) {
		return updateXXX(set, condition, "buy_stock");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyStockProduct(BuyStockProductBean bean) {
		return addXXX(bean, "buy_stock_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyStockProduct(String condition) {
		return deleteXXX(condition, "buy_stock_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyStockProductBean getBuyStockProduct(String condition) {
		return (BuyStockProductBean) getXXX(condition, "buy_stock_product",
				"adultadmin.bean.buy.BuyStockProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyStockProductCount(String condition) {
		return getXXXCount(condition, "buy_stock_product", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyStockProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"buy_stock_product", "adultadmin.bean.buy.BuyStockProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyStockProduct(String set, String condition) {
		return updateXXX(set, condition, "buy_stock_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyAdminHistory(BuyAdminHistoryBean bean) {
		return addXXX(bean, "buy_admin_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyAdminHistory(String condition) {
		return deleteXXX(condition, "buy_admin_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyAdminHistoryBean getBuyAdminHistory(String condition) {
		return (BuyAdminHistoryBean) getXXX(condition, "buy_admin_history",
				"adultadmin.bean.buy.BuyAdminHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyAdminHistoryCount(String condition) {
		return getXXXCount(condition, "buy_admin_history", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyAdminHistoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"buy_admin_history",
				"adultadmin.bean.buy.BuyAdminHistoryBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyAdminHistory(String set, String condition) {
		return updateXXX(set, condition, "buy_admin_history");
	}

	




	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyOrder(BuyOrderBean bean) {
		return addXXX(bean, "buy_order");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyOrder(String condition) {
		return deleteXXX(condition, "buy_order");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyOrderBean getBuyOrder(String condition) {
		return (BuyOrderBean) getXXX(condition, "buy_order",
				"adultadmin.bean.buy.BuyOrderBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyOrderCount(String condition) {
		return getXXXCount(condition, "buy_order", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyOrderList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_order",
				"adultadmin.bean.buy.BuyOrderBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyOrder(String set, String condition) {
		return updateXXX(set, condition, "buy_order");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyOrderProduct(BuyOrderProductBean bean) {
		return addXXX(bean, "buy_order_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyOrderProduct(String condition) {
		return deleteXXX(condition, "buy_order_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyOrderProductBean getBuyOrderProduct(String condition) {
		return (BuyOrderProductBean) getXXX(condition, "buy_order_product",
				"adultadmin.bean.buy.BuyOrderProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyOrderProductCount(String condition) {
		return getXXXCount(condition, "buy_order_product", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyOrderProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_order_product",
				"adultadmin.bean.buy.BuyOrderProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyOrderProduct(String set, String condition) {
		return updateXXX(set, condition, "buy_order_product");
	}

	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyStockin(BuyStockinBean bean) {
		return addXXX(bean, "buy_stockin");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyStockin(String condition) {
		return deleteXXX(condition, "buy_stockin");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyStockinBean getBuyStockin(String condition) {
		return (BuyStockinBean) getXXX(condition, "buy_stockin",
				"adultadmin.bean.buy.BuyStockinBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyStockinCount(String condition) {
		return getXXXCount(condition, "buy_stockin", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyStockinList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_stockin",
				"adultadmin.bean.buy.BuyStockinBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyStockin(String set, String condition) {
		return updateXXX(set, condition, "buy_stockin");
	}


	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBuyStockinProduct(BuyStockinProductBean bean) {
		return addXXX(bean, "buy_stockin_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBuyStockinProduct(String condition) {
		return deleteXXX(condition, "buy_stockin_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BuyStockinProductBean getBuyStockinProduct(String condition) {
		return (BuyStockinProductBean) getXXX(condition, "buy_stockin_product",
				"adultadmin.bean.buy.BuyStockinProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBuyStockinProductCount(String condition) {
		return getXXXCount(condition, "buy_stockin_product", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBuyStockinProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_stockin_product",
				"adultadmin.bean.buy.BuyStockinProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBuyStockinProduct(String set, String condition) {
		return updateXXX(set, condition, "buy_stockin_product");
	}

	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addOrderStock(OrderStockBean bean) {
		return addXXX(bean, "order_stock");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteOrderStock(String condition) {
		return deleteXXX(condition, "order_stock");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public OrderStockBean getOrderStock(String condition) {
		return (OrderStockBean) getXXX(condition, "order_stock",
				"adultadmin.bean.order.OrderStockBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getOrderStockCount(String condition) {
		return getXXXCount(condition, "order_stock", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getOrderStockList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_stock",
				"adultadmin.bean.order.OrderStockBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateOrderStock(String set, String condition) {
		return updateXXX(set, condition, "order_stock");
	}




	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addOrderStockProduct(OrderStockProductBean bean) {
		return addXXX(bean, "order_stock_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteOrderStockProduct(String condition) {
		return deleteXXX(condition, "order_stock_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public OrderStockProductBean getOrderStockProduct(String condition) {
		return (OrderStockProductBean) getXXX(condition, "order_stock_product",
				"adultadmin.bean.order.OrderStockProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getOrderStockProductCount(String condition) {
		return getXXXCount(condition, "order_stock_product", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getOrderStockProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_stock_product",
				"adultadmin.bean.order.OrderStockProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateOrderStockProduct(String set, String condition) {
		return updateXXX(set, condition, "order_stock_product");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addOrderStockProductCargo(OrderStockProductCargoBean bean) {
		return addXXX(bean, "order_stock_product_cargo");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteOrderStockProductCargo(String condition) {
		return deleteXXX(condition, "order_stock_product_cargo");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public OrderStockProductCargoBean getOrderStockProductCargo(String condition) {
		return (OrderStockProductCargoBean) getXXX(condition, "order_stock_product_cargo",
				"adultadmin.bean.order.OrderStockProductCargoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getOrderStockProductCargoCount(String condition) {
		return getXXXCount(condition, "order_stock_product_cargo", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getOrderStockProductCargoList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_stock_product_cargo",
				"adultadmin.bean.order.OrderStockProductCargoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateOrderStockProductCargo(String set, String condition) {
		return updateXXX(set, condition, "order_stock_product_cargo");
	}
	
	public boolean addStockBatch(StockBatchBean bean) {
		return addXXX(bean, "stock_batch");
	}
	
	public boolean updateStockBatch(String set, String condition) {
		return updateXXX(set, condition, "stock_batch");
	}
	
	public boolean deleteStockBatch(String condition) {
		return deleteXXX(condition, "stock_batch");
	}
	
	public ArrayList getStockBatchList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_batch", "adultadmin.bean.stock.StockBatchBean");
	}
	
	public StockBatchBean getStockBatch(String condition) {
		return (StockBatchBean) getXXX(condition, "stock_batch",
					"adultadmin.bean.stock.StockBatchBean");
	}
	
	public int getStockBatchCount(String condition) {
		return getXXXCount(condition, "stock_batch", "id");
	}
	
	public boolean addStockBatchLog(StockBatchLogBean bean) {
		return addXXX(bean, "stock_batch_log");
	}
	
	public ArrayList getStockBatchLogList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_batch_log", "adultadmin.bean.stock.StockBatchLogBean");
	}
	
	public int getStockBatchLogCount(String condition) {
		return getXXXCount(condition, "stock_batch_log", "id");
	}
	
	public StockBatchLogBean getStockBatchLog(String condition) {
		return (StockBatchLogBean) getXXX(condition, "stock_batch_log",
					"adultadmin.bean.stock.StockBatchLogBean");
	}
	
	public boolean updateStockBatchLog(String set, String condition) {
		return updateXXX(set, condition, "stock_batch_log");
	}
	
	public boolean deleteStockBatchLog(String condition) {
		return deleteXXX(condition, "stock_batch_log");
	}
	
	
	
	public boolean addAuditPackage(AuditPackageBean bean) {
		return addXXX(bean, "audit_package");
	}
	
	public ArrayList getAuditPackageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "audit_package", "adultadmin.bean.order.AuditPackageBean");
	}
	
	public int getAuditPackageCount(String condition) {
		return getXXXCount(condition, "audit_package", "id");
	}
	
	public AuditPackageBean getAuditPackage(String condition) {
		return (AuditPackageBean) getXXX(condition, "audit_package",
					"adultadmin.bean.order.AuditPackageBean");
	}
	
	public boolean updateAuditPackage(String set, String condition) {
		return updateXXX(set, condition, "audit_package");
	}
	
	public boolean deleteAuditPackage(String condition) {
		return deleteXXX(condition, "audit_package");
	}

	public boolean addOrderStockPrintLog(OrderStockPrintLogBean bean) {
		return addXXX(bean, "order_stock_print_log");
	}

	public boolean deleteOrderStockPrintLog(String condition) {
		return deleteXXX(condition, "order_stock_print_log");
	}

	public OrderStockPrintLogBean getOrderStockPrintLog(String condition) {
		return (OrderStockPrintLogBean) getXXX(condition, "order_stock_print_log",
		"adultadmin.bean.order.OrderStockPrintLogBean");
	}

	public int getOrderStockPrintLogCount(String condition) {
		return getXXXCount(condition, "order_stock_print_log", "id");
	}

	public ArrayList getOrderStockPrintLogList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "order_stock_print_log", "adultadmin.bean.order.OrderStockPrintLogBean");
	}

	public boolean updateOrderStockPrintLog(String set, String condition) {
		return updateXXX(set, condition, "order_stock_print_log");
	}

	public ArrayList getPrintFinanceList(String condition, int index,
			int count, String orderBy) {
		
		return getXXXList(condition,index,count,orderBy,"supplier_pay_application","adultadmin.bean.buy.SupplierPayApplicationBean");
	}


	public SupplierStandardInfoBean getSupplierStandardInfoBean(String condition) {
		
		return (SupplierStandardInfoBean) getXXX(condition, "supplier_standard_info",
		"adultadmin.bean.supplier.SupplierStandardInfoBean");
	}
	
	public ArrayList getSupplierStandarInfoList(String condition, int index,
			int count, String orderBy) {
	
		return getXXXList(condition,index,count,orderBy,"supplier_standard_info","adultadmin.bean.supplier.SupplierStandardInfoBean");
	}


	public ArrayList getSupplierBankAccountList(String condition, int index,
			int count, String orderBy) {
	
		return getXXXList(condition,index,count,orderBy,"supplier_bank_account","adultadmin.bean.supplier.SupplierBankAccountBean");
	}


	public voUser getVoUser(String condition) {
		
		return (voUser) getXXX(condition, "user",
		"adultadmin.action.vo.voUser");
	}
	
	public boolean addMailingBatch(MailingBatchBean bean) {
		return addXXX(bean, "mailing_batch");
	}

	public boolean deleteMailingBatch(String condition) {
		return deleteXXX(condition, "mailing_batch");
	}

	public MailingBatchBean getMailingBatch(String condition) {
		return (MailingBatchBean) getXXX(condition, "mailing_batch",
		"adultadmin.bean.stock.MailingBatchBean");
	}

	public int getMailingBatchCount(String condition) {
		return getXXXCount(condition, "mailing_batch", "id");
	}

	public ArrayList getMailingBatchList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "mailing_batch", "adultadmin.bean.stock.MailingBatchBean");
	}

	public boolean updateMailingBatch(String set, String condition) {
		return updateXXX(set, condition, "mailing_batch");
	}
	
	public boolean addMailingBatchParcel(MailingBatchParcelBean bean) {
		return addXXX(bean, "mailing_batch_parcel");
	}

	public boolean deleteMailingBatchParcel(String condition) {
		return deleteXXX(condition, "mailing_batch_parcel");
	}

	public MailingBatchParcelBean getMailingBatchParcel(String condition) {
		return (MailingBatchParcelBean) getXXX(condition, "mailing_batch_parcel",
		"adultadmin.bean.stock.MailingBatchParcelBean");
	}

	public int getMailingBatchParcelCount(String condition) {
		return getXXXCount(condition, "mailing_batch_parcel", "id");
	}

	public ArrayList getMailingBatchParcelList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "mailing_batch_parcel", "adultadmin.bean.stock.MailingBatchParcelBean");
	}

	public boolean updateMailingBatchParcel(String set, String condition) {
		return updateXXX(set, condition, "mailing_batch_parcel");
	}
	
	public boolean addMailingBatchPackage(MailingBatchPackageBean bean) {
		return addXXX(bean, "mailing_batch_package");
	}

	public boolean deleteMailingBatchPackage(String condition) {
		return deleteXXX(condition, "mailing_batch_package");
	}

	public MailingBatchPackageBean getMailingBatchPackage(String condition) {
		return (MailingBatchPackageBean) getXXX(condition, "mailing_batch_package",
		"adultadmin.bean.stock.MailingBatchPackageBean");
	}

	public int getMailingBatchPackageCount(String condition) {
		return getXXXCount(condition, "mailing_batch_package", "id");
	}

	public ArrayList getMailingBatchPackageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "mailing_batch_package", "adultadmin.bean.stock.MailingBatchPackageBean");
	}

	public boolean updateMailingBatchPackage(String set, String condition) {
		return updateXXX(set, condition, "mailing_batch_package");
	}
	
	public int getGroupCount(String condition) {
		int result = -1;
		
		DbOperation dbOp = getDbOp();
        if (!dbOp.init()) {
            return result;
        }
        ResultSet rs = null;

        //构建查询语句
        String query = null;
        if( condition != null ) {
        	query = "select count(*) from ( select count(*) num,code from stock_batch where " + condition + ") as total";
        } else {
        	release(dbOp);
        	return result;
        }
        try{
        	//执行查询
        	rs = dbOp.executeQuery(query);

        	if (rs == null) {
        		release(dbOp);
        		return result;
        	}
        	if( rs.next() ) {
        		result = rs.getInt(1);
        	}

        }catch(Exception e){
        	e.printStackTrace();
        }finally{
        	release(dbOp);
        }
        
        return result;
	}
	
	public String getStockBatchCreateDatetime(String batchCode,int productId){
		
		String batchTime = DateUtil.getNow();
		
		StockBatchLogBean old = null;
		
		try{
			old = this.getStockBatchLog("batch_code = '"+batchCode+"' and product_id = "+productId+" and remark='采购入库' limit 1");
			if(old == null){
				System.out.println("查不到批次日志，条件为：batch_code=" + batchCode + " and  product_id = " + productId ) ;
			}else{
				batchTime = old.getCreateDatetime();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return batchTime;
	}

	public String generateBuyStockinCodeBref() {
		String code = "R"+DateUtil.getNow().substring(2,10).replace("-", "");   
		return code;
	}

	public boolean completeBuyOrder(voUser user, BuyStockBean stock,
			BuyOrderBean buyOrder, boolean autoComplete) {
		
		BuyAdminHistoryBean log;
		//判断订单完成情况***
		ArrayList bopList = this.getBuyOrderProductList("buy_order_id="+stock.getBuyOrderId(), -1, -1, null);
		Iterator bopIterator = bopList.listIterator();
		boolean check = true;
		while(bopIterator.hasNext()){
			BuyOrderProductBean bopBean = (BuyOrderProductBean)bopIterator.next();
			if((bopBean.getStockinCountBJ()+bopBean.getStockinCountGD())<(bopBean.getOrderCountBJ()+bopBean.getOrderCountGD())){
				check = false;
				break;
			}
		}
		if(check && autoComplete){
			//自动完成订单
			BuyOrderBean buyOrderBean = this.getBuyOrder("id="+stock.getBuyOrderId());

			String orderSet = "status = " + BuyOrderBean.STATUS6
					+ ",confirm_datetime='" + DateUtil.getNow() + "'";
			if(!this.updateBuyOrder(orderSet, "id="+buyOrderBean.getId())){
				return false;
			}
			log = new BuyAdminHistoryBean();
			log.setAdminId(0);
			log.setAdminName("");
			log.setLogId(buyOrderBean.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_ORDER);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("采购已完成");
			log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			if(!this.addBuyAdminHistory(log)){
				return false;
			}

			List stockList = this.getBuyStockList("buy_order_id="+stock.getBuyOrderId()+" and status != "+BuyStockBean.STATUS8, -1, -1, null);
			Iterator stockIterator = stockList.listIterator();
			while(stockIterator.hasNext()){
				BuyStockBean buyStockBean = (BuyStockBean)stockIterator.next();

				//自动完成进货单
				if(!this.updateBuyStock("status="+BuyStockBean.STATUS6, "id="+buyStockBean.getId())){
					return false;
				}
				log = new BuyAdminHistoryBean();
				log.setAdminId(0);
				log.setAdminName("");
				log.setLogId(buyStockBean.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCK);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("采购已完成");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
				if(!this.addBuyAdminHistory(log)){
					return false;
				}

				List stockinList = this.getBuyStockinList("buy_stock_id="+buyStockBean.getId()+" and status != "+BuyStockinBean.STATUS8, -1, -1, null);
				Iterator stockinIterator = stockinList.listIterator();
				while(stockinIterator.hasNext()){
					BuyStockinBean buyStockinBean = (BuyStockinBean)stockinIterator.next();

					/*
					//自动完成入库单
					if(!this.updateBuyStockin("status="+BuyStockinBean.STATUS4, "id="+buyStockinBean.getId())){
						return false;
					}
					log = new BuyAdminHistoryBean();
					log.setAdminId(0);
					log.setAdminName("");
					log.setLogId(buyStockinBean.getId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("采购已完成");
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					if(!this.addBuyAdminHistory(log)){
						return false;
					}
					*/

					/* add lining 修改打款状态显示红色的条件 */
					double planMoney = 0;//已入库总金额
					double buyOrderPayMoney = 0;//已打款总金额
					List buyOrderProductList = this.getBuyOrderProductList("buy_order_id = " + buyOrder.getId(), 0, -1, "id");
					Iterator bopIter = buyOrderProductList.iterator();
					while (bopIter.hasNext()) {
						BuyOrderProductBean bopBean = (BuyOrderProductBean) bopIter
								.next();
						planMoney = Arith.add(planMoney, Arith.mul(
								bopBean.getOrderCountBJ()
										+ bopBean.getOrderCountGD(),
								bopBean.getPurchasePrice()));
					}
					planMoney = Arith.add(Arith.mul(planMoney, Arith
							.add(1, buyOrder.getTaxPoint())), buyOrder
							.getPortage());
					double[] money = { buyOrder.getMoney(),
							buyOrder.getLockMoney(), buyOrder.getDeposit(),
							buyOrder.getLock_deposit() };
					buyOrderPayMoney = Arith.add(money);
					String bSet = "pay_status = ";
					if (buyOrderPayMoney == 0) {
						bSet += BuyOrderBean.PAY_STATUS0;
					} else if (buyOrderPayMoney > 0 && buyOrderPayMoney < planMoney) {
						bSet += BuyOrderBean.PAY_STATUS1;
					} else if (buyOrderPayMoney > 0 && buyOrderPayMoney >= planMoney) {
						bSet += BuyOrderBean.PAY_STATUS2;
					}
					if(!this.updateBuyOrder(bSet, "id = " + buyOrder.getId())){
						return false;
					}
				}
			}

			//自动完成计划单*********
//				ArrayList buyPlanProductList = service.getBuyPlanProductList("buy_plan_id="+buyOrderBean.getBuyPlanId()+" and status = "+BuyPlanProductBean.NOTRANSFORM, 
//						-1, -1, null);
			ArrayList buyOrderList = this.getBuyOrderList("buy_plan_id="+buyOrderBean.getBuyPlanId()+" and status not in ("+BuyOrderBean.STATUS6+","+BuyOrderBean.STATUS8+")",
					-1, -1, null);
			if(buyOrderList.size()==0){
				BuyPlanBean plan = this.getBuyPlan("id="+buyOrderBean.getBuyPlanId());
				if(plan != null){
					if(!this.updateBuyPlan("status = "+BuyPlanBean.STATUS6, "id="+buyOrderBean.getBuyPlanId())){
						return false;
					}

					log = new BuyAdminHistoryBean();
					log.setAdminId(0);
					log.setAdminName(user.getUsername());
					log.setLogId(buyOrderBean.getBuyPlanId());
					log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_PLAN);
					log.setOperDatetime(DateUtil.getNow());
					log.setRemark("采购已完成");
					log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
					if(!this.addBuyAdminHistory(log)){
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static String checkword = "376eeccf22cd40d78fdf8fb1a07768ab";//顺丰校验码
	private static final QName SERVICE_NAME = new QName("http://service.serviceprovide.module.sf.com/", "FilterOrderServiceService");
	public static String custId="0203725062";//mmb在顺风的客户id
	/**
	 * 
	 * 作者：张小磊
	 * 
	 * 创建日期：2012-12-10
	 * 
	 * 订单筛选接口：客户系统可以通过此接口向企业服务平台发出自动判断或自动无法判断，由人工判断的指令，
	 * 以判断客户订单是否在顺丰的收派范围内。顺丰系统首先会根据收派双方的地址自动判断订单是否
	 * 在顺丰的收派范围内。如果系统无法判断，顺丰提供人工判断服务，人工判断后，再反馈结果给客
	 * 户或客户通过人工筛单查询接口查询订单是否在收派范围内。
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param address 订单派送地址
	 * @return true 可以配送 false 无法配送
	 */
	public boolean shunfengInterface1(String province,String city,String county,String address,String tel,float dprice) {
		try {
			java.lang.String xml = "";
			xml = "<orderFilterService>" + 
			            "<filter_type>1</filter_type>" + 
						"<orderid></orderid>" + 
						"<j_custid>"+custId+"</j_custid>" + 
						"<j_custtag></j_custtag>" + 
						"<j_company>无锡买卖宝信息技术有限公司</j_company>" + 
						"<j_contact></j_contact>" + 
						"<j_postcode></j_postcode>" + 
						"<j_tel>4008843211</j_tel>" + 
						"<j_mobile></j_mobile>" + 
						"<j_province>广东省</j_province>" + 
						"<j_city>广州市</j_city>" + 
						"<j_county>越秀区</j_county>" + 
						"<j_address>东风路29号</j_address>" + 
						"<d_company></d_company>" + 
						"<d_contact></d_contact>" + 
						"<d_postcode></d_postcode>" + 
						"<d_tel>"+tel+"</d_tel>" + 
						"<d_mobile></d_mobile>" + 
						"<d_province>"+province+"</d_province>" + 
						"<d_city>"+city+"</d_city>" + 
						"<d_county>"+county+"</d_county>" + 
						"<d_address>"+address+"</d_address>" + 
						"<cargo></cargo>"+ 
						"<cargo_total_weight></cargo_total_weight>" + 
						"<is_cod>0</is_cod>" + 
						"<cod_amount>"+dprice+"</cod_amount>" + 
						"<is_insurance></is_insurance>" + 
						"<insurance_amount></insurance_amount>" + 
						"<checkword>" + checkword + "</checkword>" + 
			      "</orderFilterService>";
			URL wsdlURL = FilterOrderServiceService.WSDL_LOCATION;
			FilterOrderServiceService ss = new FilterOrderServiceService(wsdlURL, SERVICE_NAME);
			IFilterOrderService port = ss.getFilterOrderServicePort();
			//设置超时
			Client client = ClientProxy.getClient(port);   
			HTTPConduit http = (HTTPConduit) client.getConduit();   
			HTTPClientPolicy httpClientPolicy =  new  HTTPClientPolicy();   
			httpClientPolicy.setConnectionTimeout(300);   
			httpClientPolicy.setAllowChunking( false );   
			httpClientPolicy.setReceiveTimeout(300);   
			http.setClient(httpClientPolicy);  
			Document doc = null;
			java.lang.String OrderFilterResult = null;
			try{
				OrderFilterResult = port.orderFilterService(xml);
				doc = DocumentHelper.parseText(OrderFilterResult);
			}catch(Exception e){
				Throwable ta = e.getCause();    
				if(ta instanceof SocketTimeoutException){     
					System.out.println("=========顺丰订单筛选接口--响应超时=========");    
				}else if(ta instanceof HTTPException){ 
					System.out.println("============顺丰订单筛选接口--服务端地址无效404==========");    
				}else if(ta instanceof XMLStreamException){ 
					System.out.println("========顺丰订单筛选接口--连接超时===========");    
				}else{    
					System.out.println("============顺丰订单筛选接口--其他exception==========");    
				}
				return false;
			}
			//System.out.println(OrderFilterResult);
			Element rootElt = doc.getRootElement(); // 获取根节点
			if ("responseFail".equals(rootElt.getName().toString())) {// rootElt.getName()拿到根节点的名称
				return false;
			} else if ("orderFilterResponse".equals(rootElt.getName().toString())) {
				Element isAccept = rootElt.element("isAccept");
				if ("2".equals(isAccept.getData())) {
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}
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
	 * */
	public String shunfengInterface2(String orderid,String province,String city,String county,String address,String tel,String d_cityid,String contact,String cargo,float dprice) {
		String result="false";
		try {
			java.lang.String xml = "";
			xml ="<tporder>"+
					"<orderid>mmb"+orderid+"</orderid>"+
					"<tradeno></tradeno>"+
					"<logistic_provider></logistic_provider>"+
					"<apply_type>010</apply_type>"+
					"<ewaybill_size></ewaybill_size>"+
					"<j_custid>"+custId+"</j_custid>"+
					"<j_custtag></j_custtag>"+
					"<j_company>无锡买卖宝信息技术有限公司</j_company>"+
					"<j_contact>唐祥</j_contact>"+
					"<j_postcode></j_postcode>"+
					"<j_tel>4008843211</j_tel>" + 
					"<j_mobile></j_mobile>" + 
					"<j_province>广东省</j_province>" + 
					"<j_city>广州市</j_city>" + 
					"<j_county>越秀区</j_county>" + 
					"<j_address>东风路29号</j_address>" + 
					"<d_company></d_company>"+
					"<d_contact>"+contact+"</d_contact>"+
					"<d_postcode></d_postcode>"+
					"<d_tel>"+tel+"</d_tel>"+
					"<d_mobile></d_mobile>"+
					"<d_province>"+province+"</d_province>" + 
					"<d_city>"+city+"</d_city>" + 
					"<d_county>"+county+"</d_county>" + 
					"<d_address>"+address+"</d_address>" + 
					"<cargo>"+cargo+"</cargo>"+
					"<parcel_quantity></parcel_quantity>"+
					"<cargo_total_weight></cargo_total_weight>"+
					"<cargo_net_weight></cargo_net_weight>"+
					"<express_type></express_type>"+
					"<is_cod>0</is_cod>"+
					"<cod_amount>"+dprice+"</cod_amount>"+
					"<pay_method>1</pay_method>"+
					"<pay_custid>"+custId+"</pay_custid>"+
					"<is_insurance></is_insurance>"+
					"<insurance_amount></insurance_amount>"+
					"<remark></remark>"+
					"<sendstarttime></sendstarttime>"+
					"<sendendtime></sendendtime>"+
					"<checkword>" + checkword + "</checkword>" + 
					"<j_cityid>020</j_cityid>"+
					"<d_cityid>"+d_cityid+"</d_cityid>"+
					"<cargo_length></cargo_length>"+
					"<cargo_width></cargo_width>"+
					"<cargo_height></cargo_height>"+
					"<filter1></filter1>"+
					"<filter2></filter2>"+
					"<filter3></filter3>"+
					"<filter4></filter4>"+
					"<filter5></filter5>"+
					"<filter6></filter6>"+
					"<filter7></filter7>"+
					"<filter8></filter8>"+
					"<filter9></filter9>"+
					"<filter10></filter10>"+
					"<filter11></filter11>"+
					"<filter12></filter12>"+
					"<filter13></filter13>"+
					"<filter14></filter14>"+
					"<filter15></filter15>"+
					"<filter16></filter16>"+
					"<filter17></filter17>"+
					"<filter18></filter18>"+
					"<filter19></filter19>"+
					"<filter20></filter20>"+
			     "</tporder>";
	        CustomerServiceService ss = new CustomerServiceService();
	        ICustomerService port = ss.getCustomerServicePort(); 
	        //设置超时
			Client client = ClientProxy.getClient(port);   
			HTTPConduit http = (HTTPConduit) client.getConduit();   
			HTTPClientPolicy httpClientPolicy =  new  HTTPClientPolicy();   
			httpClientPolicy.setConnectionTimeout(300);   
			httpClientPolicy.setAllowChunking( false );   
			httpClientPolicy.setReceiveTimeout(300);   
			http.setClient(httpClientPolicy);  
			java.lang.String OrderFilterResult = null;
			try{
				OrderFilterResult = port.orderService(xml);
			}catch(Exception e){
				//e.printStackTrace();    
			//上面的账号还是测试的，所有的接口都需要加入这个输出，用来区分是哪个接口的哪类错误！
				Throwable ta = e.getCause();    
				if(ta instanceof SocketTimeoutException){     
					System.out.println("=========顺丰下单接口--响应超时=========");    
					}else if(ta instanceof HTTPException){ 
						System.out.println("============顺丰下单接口--服务端地址无效404==========");    
						}else if(ta instanceof XMLStreamException){ 
							System.out.println("========顺丰下单接口--连接超时===========");    
							}else{    
								System.out.println("============顺丰下单接口--其他exception==========");    
				}
			}
			Document doc = null;
			doc = DocumentHelper.parseText(OrderFilterResult);
			//System.out.println(OrderFilterResult);
			Element rootElt = doc.getRootElement(); // 获取根节点
			if (!"responseFail".equals(rootElt.getName().toString())) {// rootElt.getName()拿到根节点的名称
				if ("orderResponse".equals(rootElt.getName().toString())) {
					Element isAccept = rootElt.element("result");
					if ("1".equals(isAccept.getData())) {
						Element mailno = rootElt.element("mailno");
						if(mailno!=null){
							result = mailno.getData().toString();
							return result;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 * 作者：张小磊
	 * 
	 * 创建日期：2013-3-21
	 * 
	 * 将省外详情单打印信息更新到EMS自助服务系统接口
	 * 功能:将详情单打印信息更新到自助服务系统
	 * 
	 * 参数及返回值说明：
	 * voOrder     订单BEAN
	 * packageCode 包裹单号
	 * area        发货区域
	 * orderType   订单类型
	 * province    省
	 * city        市
	 * county      区
	 * address     街道
	 * */
	public static final String SW_USERNAME ="44011109057000";//省外用户名             					 
    public static final String SW_PASSWORD ="123456";//省外密码          
    
    public static final String JX_USERNAME ="36010200000628";//江西邮政用户名             			            
    public static final String JX_PASSWORD ="mD=86201841=";//江西邮政密码            
   
    public static final String WX_USERNAME ="32020101094000";//无锡邮政用户名             			          
    public static final String WX_PASSWORD ="A1234567vc";//无锡邮政密码A1234567CV           
   
    public static final String HN_USERNAME ="43010093801000";//湖南EMS用户名             					
    public static final String HN_PASSWORD ="123456";//湖南EMS密码            
   
    public static final String SH_USERNAME ="32120220048270";//上海EMS用户名             					
    public static final String SH_PASSWORD ="123456";//上海EMS密码           
    
    public static final String SN_USERNAME ="MMB_ITCO";//省内用户名                                                                    
    public static final String SN_PASSWORD ="59082F7065BCB149E45BBE98D708F70B";//省内密码        
    
    public static final String GX_USERNAME ="45000000195000";//广西EMS用户名             					
    public static final String GX_PASSWORD ="123456";//广西EMS密码   
    
    public static final String SXWXDS_USERNAME ="61010210494000";//陕西邮政无锡仓代收货款用户名                  					
    public static final String SXWXDS_PASSWORD ="MMB20130726";//陕西邮政无锡仓代收货款密码   
    
    public static final String SXWXBZ_USERNAME ="61010210493000";//陕西邮政无锡仓标准快递用户名                  					
    public static final String SXWXBZ_PASSWORD ="MMB20130726";//陕西邮政无锡仓标准快递密码 
    
    public static final String SXGZDS_USERNAME ="61011362041900";//陕西邮政广州仓代收货款用户名                  					
    public static final String SXGZDS_PASSWORD ="MMB20130726";//陕西邮政广州仓代收货款密码   
    
    public static final String SXGZBZ_USERNAME ="61010210465000";//陕西邮政广州仓标准快递用户名                  					
    public static final String SXGZBZ_PASSWORD ="MMB20130726";//陕西邮政广州仓标准快递密码  
    
    public static final String SXCDBZ_USERNAME ="61010210914000";//陕西邮政成都迈世商务服务有限公司（省内标准件）用户名          					
    public static final String SXCDBZ_PASSWORD ="ems183";//密码
    
    public static final String SXCDDS_USERNAME ="61010210916000";//陕西邮政成都迈世商务服务有限公司（省内代收件）             					
    public static final String SXCDDS_PASSWORD ="ems183";//密码   
    
    public static final String HB_USERNAME ="42011210627000";//湖北邮政用户名                  					
    public static final String HB_PASSWORD ="654321";//湖北邮政密码  
    
    public static final String GDSSDJ_USERNAME ="44011109057001";//广东省速递局 用户名            					
    public static final String GDSSDJ_PASSWORD ="123456";//广东省速递局 密码  
    
    public static final String GZ_USERNAME ="52010241248300";//贵州邮政用户名            					
    public static final String GZ_PASSWORD ="112233";//贵州邮政 密码  
    
    public static final String LNFDS_USERNAME ="21010081404000";//辽宁非代收用户名            					
    public static final String LNFDS_PASSWORD ="123456";//辽宁非代收用户名 密码  
    
    public static final String LNGNDS_USERNAME ="21010081405000";//辽宁国内代收用户名            					
    public static final String LNGNDS_PASSWORD ="123456";//辽宁国内代收用户名 密码  
    
    public static final String LNSNDS_USERNAME ="21010081406000";//辽宁省内代收用户名            					
    public static final String LNSNDS_PASSWORD ="123456";//辽宁省内代收 密码  
    
    public static final String SCCD_USERNAME ="51010606224024";//四川成都代收用户名            					
    public static final String SCCD_PASSWORD ="123456";//四川成都代收 密码   
    
    public static final String YN_USERNAME ="53010220774000";//云南邮政用户名            					
    public static final String YN_PASSWORD ="123456";//云南邮政密码   
    
    public static final String FJ_USERNAME ="35050296357000";//福建邮政      					
    public static final String FJ_PASSWORD ="Mmb123456";//福建邮政
    
    private static final String APPKEY="S9a9C8c73476e3AC5";
    
	private static final QName EMS_SN_SERVICE_NAME = new QName("http://printService.webservice.bigaccount.hollycrm.com", "getPrintDatas");
	public static boolean emsSwInterface(voOrder orderBean,String packageCode,float weight,String province,String city,String county,String address,int area,int deliver) {
		try {
			int orderType = 0;//快递类型
			String fahuoInfo="";//发货人信息
			//如果是增城发货，则发货人地址如下
			String addressInfo = "";
			if(voOrder.deliverInfoMapAll.get(deliver).getAddress()!=null){
				if(deliver==43||deliver==44){
					addressInfo="沈阳市苏家屯区机场路1010号";
				}else{
					addressInfo="买卖宝"+ProductStockBean.areaMap.get(area)+"仓"+"("+voOrder.deliverInfoMapAll.get(deliver).getAddress()+")";
				}
			}
			fahuoInfo="<scontactor>无锡买卖宝</scontactor>"+
					"<scustMobile>4008843211</scustMobile>"+
					"<scustTelplus></scustTelplus>"+
					"<scustPost>214000</scustPost>"+
					"<scustAddr>"+addressInfo+"</scustAddr>"+
					"<scustComp>无锡买卖宝</scustComp>";
			if("".equals(county)){
				county=city;
			}
			String productCode = "";// 产品代码
			String fee = new String();
			String passWord = new String();
			if (orderBean.getBuyMode() == 0) {//代收货款
				orderType = 2;// 货到付款
				//productCode = "4310101911";//代收货款传4310101911，非代收的传4310101991
				if(deliver==11){//广东省速递局
					productCode = "3310202411";
				}else if(deliver==43){//辽宁省
					if("辽宁".equals(addressInfo.substring(0, 2))){
						productCode = "3310112911";//辽宁省内代收
					}else{
						productCode = "4310208611";//辽宁省外代收
					}
				}else{
					productCode = "4310101911";
				}
				fee+="<fee>"+(int)orderBean.getDprice()+"</fee>" + 
				"<feeUppercase>"+StockServiceImpl.toUpperNumber((int)orderBean.getDprice()+"")+"</feeUppercase>";
			} else {//非代收货款
				orderType = 1;// 标准快递
				if(deliver==11){
					productCode = "3310202491";
				}else if(deliver==43){//辽宁省
					if("辽宁".equals(addressInfo.substring(0, 2))){
						productCode = "4310210991";//辽宁省内非代收
					}else{
						productCode = "3310210991";//辽宁省外非代收
					}
				}else{
					productCode = "4310101991";
				}
				//productCode = "4310101991";//代收货款传4310101911，非代收的传4310101991
				fee+="<fee></fee><feeUppercase></feeUppercase>";
			}
			//广速省速递局
			if(deliver==11){
				if (orderBean.getBuyMode() == 0) {
					passWord = "<sysAccount>"+GDSSDJ_USERNAME+"</sysAccount>"+
					"<passWord>"+GDSSDJ_PASSWORD+"</passWord>";
				}else if(orderBean.getBuyMode() != 0){//省内非代收大客户号跟全国一样
					passWord = "<sysAccount>"+SW_USERNAME+"</sysAccount>"+
					"<passWord>"+SW_PASSWORD+"</passWord>";
				}
			}
			//辽宁
			if(deliver==43||deliver==44){
				if (orderBean.getBuyMode() == 0) {//辽宁代收
					if("辽宁".equals(addressInfo.substring(0, 2))){//辽宁省内代收
						passWord = "<sysAccount>"+LNSNDS_USERNAME+"</sysAccount>"+
								   "<passWord>"+LNSNDS_PASSWORD+"</passWord>";
					}else{//辽宁省外代收
						passWord = "<sysAccount>"+LNGNDS_USERNAME+"</sysAccount>"+
								   "<passWord>"+LNGNDS_PASSWORD+"</passWord>";					
					}
				}else if(orderBean.getBuyMode() != 0){//辽宁非代收
					passWord = "<sysAccount>"+LNFDS_USERNAME+"</sysAccount>"+
							   "<passWord>"+LNFDS_PASSWORD+"</passWord>";
				}
			}
			//广速省外
			if(deliver==9){
				passWord = "<sysAccount>"+SW_USERNAME+"</sysAccount>"+
			    "<passWord>"+SW_PASSWORD+"</passWord>";
			}
			//江西
			if(deliver==28){
				passWord = "<sysAccount>"+JX_USERNAME+"</sysAccount>"+
			    "<passWord>"+JX_PASSWORD+"</passWord>";
			}
			//无锡
			if(deliver==37){
				passWord = "<sysAccount>"+WX_USERNAME+"</sysAccount>"+
			    "<passWord>"+WX_PASSWORD+"</passWord>";
			}
			//湖南
			if(deliver==31){
				passWord = "<sysAccount>"+HN_USERNAME+"</sysAccount>"+
			    "<passWord>"+HN_PASSWORD+"</passWord>";
			}
			//上海
			if(deliver==29){
				passWord = "<sysAccount>"+SH_USERNAME+"</sysAccount>"+
			    "<passWord>"+SH_PASSWORD+"</passWord>";
			}
			//广西
			if(deliver==25){
				passWord = "<sysAccount>"+GX_USERNAME+"</sysAccount>"+
			    "<passWord>"+GX_PASSWORD+"</passWord>";
			}
			//陕西邮政无锡仓代收货款
			if(deliver==34 && orderBean.getBuyMode() == 0 && area==4){
				passWord = "<sysAccount>"+SXWXDS_USERNAME+"</sysAccount>"+
			    "<passWord>"+SXWXDS_PASSWORD+"</passWord>";
			}
			//陕西邮政无锡仓标准快递
			if(deliver==34 && orderBean.getBuyMode() != 0 && area==4){
				passWord = "<sysAccount>"+SXWXBZ_USERNAME+"</sysAccount>"+
			    "<passWord>"+SXWXBZ_PASSWORD+"</passWord>";
			}
			//陕西邮政广州仓代收货款
			if(deliver==34 && orderBean.getBuyMode() == 0 && area==3){
				passWord = "<sysAccount>"+SXGZDS_USERNAME+"</sysAccount>"+
			    "<passWord>"+SXGZDS_PASSWORD+"</passWord>";
			}
			//陕西邮政广州仓标准快递
			if(deliver==34 && orderBean.getBuyMode() != 0 && area==3){
				passWord = "<sysAccount>"+SXGZBZ_USERNAME+"</sysAccount>"+
			    "<passWord>"+SXGZBZ_PASSWORD+"</passWord>";
			}
			//陕西邮政成都仓代收货款
			if(deliver==34 && orderBean.getBuyMode() == 0 && area==9){
				passWord = "<sysAccount>"+SXCDDS_USERNAME+"</sysAccount>"+
			    "<passWord>"+SXCDDS_PASSWORD+"</passWord>";
			}
			//陕西邮政成都仓标准快递
			if(deliver==34 && orderBean.getBuyMode() != 0 && area==9){
				passWord = "<sysAccount>"+SXCDBZ_USERNAME+"</sysAccount>"+
			    "<passWord>"+SXCDBZ_PASSWORD+"</passWord>";
			}
			//湖北邮政
			if(deliver==32){
				passWord = "<sysAccount>"+HB_USERNAME+"</sysAccount>"+
			    "<passWord>"+HB_PASSWORD+"</passWord>";
			}
			//贵州邮政
			if(deliver==35){
				passWord = "<sysAccount>"+GZ_USERNAME+"</sysAccount>"+
			    "<passWord>"+GZ_PASSWORD+"</passWord>";
			}
			//四川成都
			if(deliver==52){
				passWord = "<sysAccount>"+SCCD_USERNAME+"</sysAccount>"+
			    "<passWord>"+SCCD_PASSWORD+"</passWord>";
			}
			//云南
			if(deliver==51){
				passWord = "<sysAccount>"+YN_USERNAME+"</sysAccount>"+
			    "<passWord>"+YN_PASSWORD+"</passWord>";
			}
			//福建
			if(deliver==54){
				passWord = "<sysAccount>"+FJ_USERNAME+"</sysAccount>"+
			    "<passWord>"+FJ_PASSWORD+"</passWord>";
			}
			String phone = "";
			if(orderBean.getPhone()!=null && orderBean.getPhone().length()>=8){
				phone = orderBean.getPhone().substring(0, 2) +"******"+orderBean.getPhone().substring(8, orderBean.getPhone().length());
			}else if(orderBean.getPhone()!=null && orderBean.getPhone().length()>=2){
				phone = orderBean.getPhone().substring(0, 2) +"******";
			}
			java.lang.String xml = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<XMLInfo>"+passWord+
				"<printKind>2</printKind>"+
				"<appKey>"+APPKEY+"</appKey>"+
				"<printDatas>"+
					"<printData>"+
				        "<bigAccountDataId>"+orderBean.getCode()+"</bigAccountDataId>"+
				        "<billno>"+packageCode+"</billno>"+
				        fahuoInfo+
						"<tcontactor>"+orderBean.getName()+"</tcontactor>"+
						"<tcustMobile>"+phone+"</tcustMobile>"+
						"<tcustTelplus></tcustTelplus>"+
						"<tcustPost>"+orderBean.getPostcode()+"</tcustPost>"+
						"<tcustAddr>"+address+"</tcustAddr>"+
				        "<tcustComp></tcustComp>"+
				        "<tcustProvince>"+province+"</tcustProvince>"+
				        "<tcustCity>"+city+"</tcustCity>"+
				        "<tcustCounty>"+county+"</tcustCounty>"+
				        "<weight>"+weight+"</weight>"+
				        "<length>01</length>"+
				        "<insure>0</insure>"+
				        "<insurance></insurance>"+
				        fee+
				        "<businessType>"+orderType+"</businessType>"+
				        "<cargoDesc></cargoDesc>"+
				        "<cargoType></cargoType>"+
				        "<remark></remark>"+
				        "<deliveryclaim></deliveryclaim>"+
				        "<customerDn></customerDn>"+
				        "<productCode>"+ productCode +"</productCode>" + 
				        "<blank1></blank1>"+
				        "<blank2></blank2>"+
				        "<blank3></blank3>"+
				        "<blank4></blank4>"+
				        "<blank5></blank5>"+
			        "</printData>"+
		    	"</printDatas>"+
	    	"</XMLInfo>";
		
			URL wsdlURL = GetPrintDatas.WSDL_LOCATION;
			GetPrintDatas ss = new GetPrintDatas(wsdlURL, EMS_SN_SERVICE_NAME);
	        GetPrintDatasPortType port = ss.getGetPrintDatasHttpSoap12Endpoint();  
	        //设置超时
			Client client = ClientProxy.getClient(port);   
			HTTPConduit http = (HTTPConduit) client.getConduit();   
			HTTPClientPolicy httpClientPolicy =  new  HTTPClientPolicy();   
			httpClientPolicy.setConnectionTimeout(300);   
			httpClientPolicy.setAllowChunking( false );   
			httpClientPolicy.setReceiveTimeout(300);   
			http.setClient(httpClientPolicy); 
			java.lang.String OrderFilterResult = null;
			try{
				OrderFilterResult = port.updatePrintEMSDatas(new String(Base64.encode(xml.getBytes())));
				//System.out.println("OrderFilterResult++++"+OrderFilterResult);
			}catch(Exception e){
				//e.printStackTrace();    
			//上面的账号还是测试的，所有的接口都需要加入这个输出，用来区分是哪个接口的哪类错误！
				Throwable ta = e.getCause();    
				if(ta instanceof SocketTimeoutException){     
					System.out.println("=========EMS省外回传接口--响应超时=========");    
					}else if(ta instanceof HTTPException){ 
						System.out.println("============EMS省外回传接口--服务端地址无效404==========");    
						}else if(ta instanceof XMLStreamException){ 
							System.out.println("========EMS省外回传接口--连接超时===========");    
							}else{
								//e.printStackTrace();
								System.out.println("============EMS省外回传接口--其他exception=========="); 
				}
				return false;
				
			}
			//System.out.println("加密之后的参数"+new String(Base64.encode(xml.getBytes())));
			//System.out.println(Base64.decode(new String(Base64.encode(xml.getBytes())));
			Document doc = null;
			//System.out.println("解码之后的结果"+new String(Base64.decode(OrderFilterResult.getBytes())));
			doc = DocumentHelper.parseText(new String(Base64.decode(OrderFilterResult.getBytes())));
			Element rootElt = doc.getRootElement(); // 获取根节点
		    // rootElt.getName()拿到根节点的名称
			if ("response".equals(rootElt.getName().toString())) {
				Element isAccept = rootElt.element("result");
				if ("1".equals(isAccept.getData())) {
						return true;
				}
				else if ("0".equals(isAccept.getData())) {
					Element reason = rootElt.element("errorDesc");
					System.out.println("订单号"+orderBean.getCode()+"，包裹单号"+packageCode+"调用EMS省外接口错误：原因是"+reason.getData());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 
	 * 作者：张小磊
	 * 
	 * 创建日期：2013-3-22
	 * 
	 * 根据大客户唯一标识获取详情单号接口
	 * 功能:通过大客户唯一标识，如某电商公司的配货单号获取详情单号
	 * 
	 * */
	public void emsInterface2() {
		//IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		try {
			java.lang.String string = 	"<orderId>" + 
										"<bigAccountDataId>1234567890</bigAccountDataId>" + 
										"</orderId>";
			for (int i = 0; i < 2; i++) {
				string += "<orderId>" + 
						  "<bigAccountDataId>1234567890</bigAccountDataId>" + 
				          "</orderId>";
			}
			//System.out.println("Invoking getBillNo...");
			java.lang.String _getBillNo_xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
			"<XMLInfo>" + 
				"<sysAccount>"+SW_USERNAME+"</sysAccount>" + 
				"<passWord>"+SW_PASSWORD+"</passWord>" + 
				"<businessType>2</businessType>" + 
				"<orderIds>" + string + "</orderIds>" + 
			"</XMLInfo>";
			//System.out.println(_getBillNo_xmlStr);
			// 加密
			URL wsdlURL = GetPrintDatas.WSDL_LOCATION;
			GetPrintDatas ss = new GetPrintDatas(wsdlURL, EMS_SN_SERVICE_NAME);
			GetPrintDatasPortType port = ss.getGetPrintDatasHttpSoap12Endpoint();
			// 设置超时
			Client client = ClientProxy.getClient(port);
			HTTPConduit http = (HTTPConduit) client.getConduit();
			HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
			httpClientPolicy.setConnectionTimeout(300);
			httpClientPolicy.setAllowChunking(false);
			httpClientPolicy.setReceiveTimeout(300);
			http.setClient(httpClientPolicy);
			java.lang.String _getBillNo__return = null;
			try{
				_getBillNo__return = port.getBillNo(new String(Base64.encode(_getBillNo_xmlStr.getBytes())));
			}catch(Exception e){
				//e.printStackTrace();    
			//上面的账号还是测试的，所有的接口都需要加入这个输出，用来区分是哪个接口的哪类错误！
				Throwable ta = e.getCause();    
				if(ta instanceof SocketTimeoutException){     
					System.out.println("=========EMS获取详情单接口--响应超时=========");    
					}else if(ta instanceof HTTPException){ 
						System.out.println("============EMS获取详情单接口--服务端地址无效404==========");    
						}else if(ta instanceof XMLStreamException){ 
							System.out.println("========EMS获取详情单接口--连接超时===========");    
							}else{    
								System.out.println("============EMS获取详情单接口--其他exception==========");    
				}
			}
			// 解密
			java.lang.String returnXml = new String(Base64.decode(_getBillNo__return.getBytes()));
			Document doc = null;
			doc = DocumentHelper.parseText(returnXml);
			Element rootElt = doc.getRootElement(); // 获取根节点
			Element a = rootElt.element("assignIds");
			List assignIdList = a.elements("assignId");
			for (int i = 0; i < assignIdList.size(); i++) {
				Element assignId = (Element) assignIdList.get(i);
				Element billno = assignId.element("billno");
				//System.out.println(billno.getData());
				String sqlEms = "INSERT INTO deliver_package_code (deliver,package_code,used)VALUES" + "(9," + "'" + billno.getData() + "',0)";
				//adminService.getDbOperation().executeUpdate(sqlEms);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	public static void main(String args[]) {
//		voOrder order=new voOrder();
//		order.setCode("B13040834095");
//		order.setName("邓杍文");
//		order.setPostcode("514000");
//		order.setPhone("13723614813");
//		order.setDprice(550);
//		order.setBuyMode(0);
//		//省内的：B13040834095    货到付款 邓杍文 13723614813  550.00 广东省梅州市梅县丙村镇邮政局(电话通知) 邮编514000 包裹单号码EE427065624GD
//		System.out.println(JXemsInterface(order,"88888881",10,"广东省","梅州市","梅县","丙村镇邮政局",1));
//		//JXemsInterface(voOrder orderBean,String packageCode,float weight,String province,String city,String county,String address,int area)
//	}
	 /**
		 * 
		 * 作者：张小磊
		 * 
		 * 创建日期：2013-4-7
		 * 
		 * 将省内详情单打印信息更新到EMS自助服务系统接口
		 * 功能:将详情单打印信息更新到自助服务系统
		 * 
		 * 参数及返回值说明：
		 * voOrder     订单BEAN
		 * packageCode 包裹单号
		 * province    省
		 * city        市
		 * county      区
		 * address     街道
		 * */
		public static boolean emsInterface3(voOrder orderBean,String packageCode,float weight,String province,String city,String county,String address) {
		        try {
		        	Uniservice ss = new Uniservice();
		        	UniservicePortType port = ss.getUniserviceHttpPort();  
		        	// 设置超时
		        	Client client = ClientProxy.getClient(port);
		        	HTTPConduit http = (HTTPConduit) client.getConduit();
		        	HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		        	httpClientPolicy.setConnectionTimeout(300);
		        	httpClientPolicy.setAllowChunking(false);
		        	httpClientPolicy.setReceiveTimeout(700);
		        	http.setClient(httpClientPolicy);
				String price = "";
				String buyMode ="001";
				if (orderBean.getBuyMode() == 0) {
					price += orderBean.getDprice();
					buyMode="006";
				}
		        String headWihtoutSign ="{\"SYS_CODE\":\"888888\"," +
		               	"\"USERNAME\":\""+StockServiceImpl.SN_USERNAME+"\"," +
		               	"\"PASSWORD\":\""+StockServiceImpl.SN_PASSWORD+"\"," +
		               	"\"FUNC_CODE\":\"PMS001\"}";
		        //44023200316000
		        String body = 
		        	"{\"DS_COUNT\":\"1\","+
				    "\"DATASET\":[{"+
				    "\"DS_ID\":\"101\","+
				    "\"COL_COUNT\":\"29\","+
				    "\"ROW\":[{"+
				    "\"V_CUSCODE\":\"44011109057001\","+
				    "\"V_SENDER_NAME\":\"赵艳\","+ 
				    "\"V_SENDER_COMPANY\":\"无锡买卖宝\","+ 
				    "\"V_SENDER_PROV\":\"广东省\","+ 
				    "\"V_SENDER_CITY\":\"广州市\","+ 
				    "\"V_SENDER_DIST\":\"越秀区\","+ 
				    "\"V_SENDER_ADDRESS\":\"东风路29号\","+
				    "\"V_SENDER_TEL\":\"4008843211\","+ 
				    "\"V_MAIL_CODE\":\"" +packageCode+"\","+
				    "\"V_RECIPIENT_NAME\":\"" +orderBean.getName()+
				    "\","+
				    "\"V_RECIPIENT_COMPANY\":\"" +
				    "\","+ 
				    "\"V_RECIPIENT_PROV\":\"" +province+
				    "\","+
				    "\"V_RECIPIENT_CITY\":\"" +city+
				    "\","+ 
				    "\"V_RECIPIENT_DIST\":\"" +county+
				    "\","+
				    "\"V_RECIPIENT_ADDRESS\":\"" +address+
				    "\","+ 
				    "\"V_RECIPIENT_TEL\":\"" +orderBean.getPhone()+
				    "\","+ 
				    "\"V_RECIPIENT_POSTCODE\":\"" +orderBean.getPostcode()+"\","+
				    "\"V_INNER_KIND\":\"物品\","+
				    "\"V_SEND_CHANGE\":\"" +
				    "\","+
				    "\"N_BUSI_TYPE_ID\":\""+buyMode+"\","+
				    "\"N_WEIGHT\":\"" +weight+
				    "\","+
				    "\"N_LENGTH\":\"" +
				    "\","+
				    "\"N_WIDE\":\"" +
				    "\","+
				    "\"N_TALL\":\"" +
				    "\","+
				    "\"N_POSTAGE\":\"" +
				    "\","+
				    "\"N_SECURE_FEE\":\"" +
				    "\","+
				    "\"V_ORDER_NO\":\"" +orderBean.getCode()+
				    "\","+ 
				    "\"N_AGENCY_FUND\":\"" +price+
				    "\","+
				    "\"V_REMARK\":\"" +
				    "\","+
				    "\"V_RECV_ORG_ID\":\"51133501\""+ 
				    "}]}]}";
		        String content = headWihtoutSign + body;
		        	//System.out.println("Invoking callService...");
		        	String head = "{\"SYS_CODE\":\"888888\"," +
		        	"\"USERNAME\":\""+StockServiceImpl.SN_USERNAME+"\"," +
		        	"\"PASSWORD\":\""+StockServiceImpl.SN_PASSWORD+"\"," +
		        	"\"FUNC_CODE\":\"PMS001\"," +
		        	"\"SIGN\":\""+encrypt(content, "MMB", "UTF-8")+"\"," +
		        	"\"CHARSET\":\"utf-8\"}";

		        	//System.out.println("SIGN:"+encrypt(content, "MMB", "UTF-8"));
		        	java.lang.String _callService__return = null;
		        	try{
		        		_callService__return = port.call(head, gzipCompress(body,"UTF-8"));
		        	}catch(Exception e){
		        		//e.printStackTrace();    
		        		//上面的账号还是测试的，所有的接口都需要加入这个输出，用来区分是哪个接口的哪类错误！
		        		Throwable ta = e.getCause();    
		        		if(ta instanceof SocketTimeoutException){     
		        			System.out.println("=========EMS省内回传接口--响应超时=========");    
		        		}else if(ta instanceof HTTPException){ 
		        			System.out.println("============EMS省内回传接口--服务端地址无效404==========");    
		        		}else if(ta instanceof XMLStreamException){ 
		        			System.out.println("========EMS省内回传接口--连接超时===========");    
		        		}else{    
		        			System.out.println("============EMS省内回传接口--其他exception==========");    
		        		}
		        	}
		            //System.out.println("callService.result=" + _callService__return);
		            //System.out.println("---------"+gzipDecompress(_callService__return,"UTF-8"));
		            String strJson = gzipDecompress(_callService__return,"UTF-8");
		            JSONObject jsonBean = JSONObject.fromObject(strJson);
		            JSONObject arryBean = JSONObject.fromObject(jsonBean.get("head"));
		            
		            //System.out.println("callService.result=" + _callService__return);
		            //System.out.println("---------"+gzipDecompress(_callService__return,"UTF-8"));
		    		if(!arryBean.isNullObject()){
		    			if(arryBean.get("ret_code").equals("0")){
		    				return true;
		    			}else{
		    				return false;
		    			}
		    		}
		    		else{
		    			return false;
		    		}
		        } catch (Exception e) {
		        	System.out.println("=========EMS省内回传接口--异常=========");    
		        	return false;
		        }
		}
		/*
		 * gzip压缩
		 * 
		 * @param s
		 * @param charset 编码方式，为空默认为utf-8
		 * @return
		 * @throws IOException
		 */

		public static String gzipCompress(String s,String charset) throws Exception {
			if(charset==null)charset="UTF-8";
			ByteArrayInputStream input = new ByteArrayInputStream(s
					.getBytes(charset));
			ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
			GZIPOutputStream gzout = new GZIPOutputStream(output);

			byte[] buf = new byte[1024];
			int number;

			while ((number = input.read(buf)) != -1) {
				gzout.write(buf, 0, number);
			}

			gzout.close();
			input.close();
			String result = new BASE64Encoder().encode(output.toByteArray());
			output.close();
			return result;
		}
	    
		/**
		 * gzip解压
		 * 
		 * @param data
		 * @param charset 编码方式，为空默认为utf-8
		 * @return
		 * @throws IOException
		 */

		public static String gzipDecompress(String data,String charset) throws Exception {
			if(charset==null)charset="UTF-8";
			ByteArrayOutputStream output = new ByteArrayOutputStream(1024);
			ByteArrayInputStream input = new ByteArrayInputStream(
					new BASE64Decoder().decodeBuffer(data));
			GZIPInputStream gzinpt = new GZIPInputStream(input);
			byte[] buf = new byte[1024];
			int number = 0;
			while ((number = gzinpt.read(buf)) != -1) {
				output.write(buf, 0, number);
			}
			gzinpt.close();
			input.close();
			String result = new String(output.toString(charset));
			output.close();
			return result;
		}
			/**
		    * 对传入的字符串进行MD5加密
		    * @param plainText
		    * @return
		    */

		   public static String MD5(String plainText, String charset) throws Exception {
		     
		         MessageDigest md = MessageDigest.getInstance("MD5");
		         md.update(plainText.getBytes(charset));
		         byte b[] = md.digest();
		         int i;
		         StringBuffer buf = new StringBuffer("");
		         for (int offset = 0; offset < b.length; offset++) {
		            i = b[offset];
		            if (i < 0)
		               i += 256;
		            if (i < 16)
		               buf.append("0");
		            buf.append(Integer.toHexString(i));
		         }
		         return buf.toString();
		   }
		   // 签名程序代码片段
		   public static String encrypt(String content, String keyValue, String charset) throws Exception {
		   	if(keyValue != null) {
		               return base64(MD5(content + keyValue, charset), charset);
		       	} 
		       	return base64(MD5(content, charset), charset);
		   }
		   /**
		    *  base64编码
		    *  
		    * @param str
		    * @return
		    * @throws Exception 
		    */

		   public static String base64(String str, String charset) throws Exception{
				   return (new BASE64Encoder()).encode(str.getBytes(charset));
		   }
	@Override
	public voOrderExtendInfo getOrderExtendInfo(String condition) throws SQLException {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voOrderExtendInfo vo = null;
		StringBuilder strBuilder = new StringBuilder("select * from user_order_extend_info");
		try {
			if(condition != null){
				strBuilder.append(" where ");
				strBuilder.append(condition);
			}
			pst = this.getDbOp().getConn().prepareStatement(strBuilder.toString());

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voOrderExtendInfo();
				vo.setId(rs.getInt("id"));
				vo.setOrderCode(rs.getString("order_code"));
				vo.setOrderPrice(rs.getFloat("order_price"));
				vo.setPayMode(rs.getInt("pay_mode"));
				vo.setPayStatus(rs.getInt("pay_status"));
				vo.setAddId1(rs.getInt("add_id1"));
				vo.setAddId2(rs.getInt("add_id2"));
				vo.setAddId3(rs.getInt("add_id3"));
				vo.setAddId4(rs.getInt("add_id4"));
				vo.setAdd5(rs.getString("add_5"));
			}

		} catch (Exception sqle) {
			sqle.printStackTrace();
		} finally {
			if(rs != null){
				rs.close();
			}
			if(pst != null){
				pst.close();
			}
		}
		return vo;
	}
	public static String toUpperNumber(String downNumber) {
		String[] upperArray = downNumber.split("\\.");// 区分元和角分
		String[] upperNumber = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌",
				"玖" };
		String[] upper = { "元", "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾",
				"佰", "仟" };
		String upperPrice = "";// 大写金额字符串
		if (upperArray.length > 0) {
			int len = upperArray[0].length();// 整数元的位数
			for (int i = 0; i < len; i++) {
				int number = Integer.parseInt(upperArray[0].substring(
						upperArray[0].length() - i - 1, upperArray[0].length()
								- i));// i位上的数字
				upperPrice = upperNumber[number] + upper[i] + upperPrice;
			}
		}
		if (upperArray.length > 1) {// 有角分的情况
			int number1 = Integer.parseInt(upperArray[1].substring(0, 1));// 角
			upperPrice = upperPrice + upperNumber[number1] + "角";
			if (upperArray[1].length() > 1) {
				int number2 = Integer.parseInt(upperArray[1].substring(1, 2));// 分
				upperPrice = upperPrice + upperNumber[number2] + "分";
			}
		}
		upperPrice = upperPrice.replaceAll("零角", "");
		upperPrice = upperPrice.replaceAll("零分", "");
		upperPrice = upperPrice.replaceAll("零拾", "零");
		upperPrice = upperPrice.replaceAll("零佰", "零");
		upperPrice = upperPrice.replaceAll("零仟", "零");
		upperPrice = upperPrice.replaceAll("零+", "零");
		upperPrice = upperPrice.replaceAll("零元", "元");
		upperPrice = upperPrice.replaceAll("零万", "万");
		upperPrice = upperPrice.replaceAll("零亿", "亿");
		upperPrice = upperPrice.replaceAll("亿万", "亿");
		if (upperPrice.endsWith("元")) {
			upperPrice = upperPrice + "整";
		}
		return upperPrice;
	}	
	public static long emsSwInterfaceTest(voOrder orderBean,String packageCode,float weight,String province,String city,String county,String address,int area,int deliver) {
		try {
			long c = 0;
			int orderType = 0;//快递类型
			String fahuoInfo="";//发货人信息
			//如果是增城发货，则发货人地址如下
			if(area==3){
				fahuoInfo="<scontactor>赵艳</scontactor>"+
				"<scustMobile>4008843211</scustMobile>"+
				"<scustTelplus></scustTelplus>"+
		        "<scustPost>510000</scustPost>"+
				"<scustAddr>广州市站南路4号</scustAddr>"+
		        "<scustComp>无锡买卖宝</scustComp>";
			}else if(area==4){//如果是无锡发货，则发货人地址如下
				fahuoInfo="<scontactor>无锡买卖宝</scontactor>"+
				"<scustMobile>4008843211</scustMobile>"+
				"<scustTelplus></scustTelplus>"+
		        "<scustPost>214000</scustPost>"+
				"<scustAddr>广州市站南路4号</scustAddr>"+
		        "<scustComp>无锡买卖宝</scustComp>";
			}
			if("".equals(county)){
				county=city;
			}
			String productCode = "";// 产品代码
			String fee = new String();
			String passWord = new String();
			if (orderBean.getBuyMode() == 0) {
				orderType = 2;// 货到付款
				productCode = "4310101911";//代收货款传4310101911，非代收的传4310101991
				fee+="<fee>"+(int)orderBean.getDprice()+"</fee>" + 
				"<feeUppercase>"+StockServiceImpl.toUpperNumber((int)orderBean.getDprice()+"")+"</feeUppercase>";
			} else {
				orderType = 1;// 标准快递
				productCode = "4310101991";//代收货款传4310101911，非代收的传4310101991
				fee+="<fee></fee><feeUppercase></feeUppercase>";
			}
			//广速省外
			if(deliver==9){
				passWord = "<sysAccount>"+SW_USERNAME+"</sysAccount>"+
			    "<passWord>"+SW_PASSWORD+"</passWord>";
			}
			//江西
			if(deliver==28){
				passWord = "<sysAccount>"+JX_USERNAME+"</sysAccount>"+
			    "<passWord>"+JX_PASSWORD+"</passWord>";
			}
			//无锡
			if(deliver==37){
				passWord = "<sysAccount>"+WX_USERNAME+"</sysAccount>"+
			    "<passWord>"+WX_PASSWORD+"</passWord>";
			}
			//湖南
			if(deliver==31){
				passWord = "<sysAccount>"+HN_USERNAME+"</sysAccount>"+
			    "<passWord>"+HN_PASSWORD+"</passWord>";
			}
			//上海
			if(deliver==29){
				passWord = "<sysAccount>"+SH_USERNAME+"</sysAccount>"+
			    "<passWord>"+SH_PASSWORD+"</passWord>";
			}
			//广西
			if(deliver==25){
				passWord = "<sysAccount>"+GX_USERNAME+"</sysAccount>"+
			    "<passWord>"+GX_PASSWORD+"</passWord>";
			}
			
			java.lang.String xml = 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<XMLInfo>"+passWord+
				"<printKind>2</printKind>"+
				"<printDatas>"+
					"<printData>"+
				        "<bigAccountDataId>"+orderBean.getCode()+"</bigAccountDataId>"+
				        "<billno>"+packageCode+"</billno>"+
				        fahuoInfo+
						"<tcontactor>"+orderBean.getName()+"</tcontactor>"+
						"<tcustMobile>"+orderBean.getPhone()+"</tcustMobile>"+
						"<tcustTelplus></tcustTelplus>"+
						"<tcustPost>"+orderBean.getPostcode()+"</tcustPost>"+
						"<tcustAddr>"+address+"</tcustAddr>"+
				        "<tcustComp></tcustComp>"+
				        "<tcustProvince>"+province+"</tcustProvince>"+
				        "<tcustCity>"+city+"</tcustCity>"+
				        "<tcustCounty>"+county+"</tcustCounty>"+
				        "<weight>"+weight+"</weight>"+
				        "<length>0</length>"+
				        "<insure>0</insure>"+
				        "<insurance></insurance>"+
				        fee+
				        "<businessType>"+orderType+"</businessType>"+
				        "<cargoDesc></cargoDesc>"+
				        "<cargoType></cargoType>"+
				        "<remark></remark>"+
				        "<deliveryclaim></deliveryclaim>"+
				        "<customerDn></customerDn>"+
				        "<productCode>"+ productCode +"</productCode>" + 
				        "<blank1></blank1>"+
				        "<blank2></blank2>"+
				        "<blank3></blank3>"+
				        "<blank4></blank4>"+
				        "<blank5></blank5>"+
			        "</printData>"+
		    	"</printDatas>"+
	    	"</XMLInfo>";
		
			URL wsdlURL = GetPrintDatas.WSDL_LOCATION;
			GetPrintDatas ss = new GetPrintDatas(wsdlURL, EMS_SN_SERVICE_NAME);
	        GetPrintDatasPortType port = ss.getGetPrintDatasHttpSoap12Endpoint();  
	        //设置超时
			Client client = ClientProxy.getClient(port);   
			HTTPConduit http = (HTTPConduit) client.getConduit();   
			HTTPClientPolicy httpClientPolicy =  new  HTTPClientPolicy();   
			httpClientPolicy.setConnectionTimeout(300000);   
			httpClientPolicy.setAllowChunking( false );   
			httpClientPolicy.setReceiveTimeout(4000000);   
			http.setClient(httpClientPolicy); 
			java.lang.String OrderFilterResult = null;
			try{
				long a = System.currentTimeMillis();
				//System.out.println("接口开始时间"+System.currentTimeMillis());
				OrderFilterResult = port.updatePrintEMSDatas(new String(Base64.encode(xml.getBytes())));
				long b = System.currentTimeMillis();
				//System.out.println("接口结束时间"+System.currentTimeMillis());
			    c=b-a;
				//System.out.println("本次执行时间"+c+"毫秒");
				//System.out.println("OrderFilterResult++++"+OrderFilterResult);
			}catch(Exception e){
				//e.printStackTrace();    
			//上面的账号还是测试的，所有的接口都需要加入这个输出，用来区分是哪个接口的哪类错误！
				Throwable ta = e.getCause();    
				if(ta instanceof SocketTimeoutException){     
					System.out.println("=========EMS省外回传接口--响应超时=========");    
					}else if(ta instanceof HTTPException){ 
						System.out.println("============EMS省外回传接口--服务端地址无效404==========");    
						}else if(ta instanceof XMLStreamException){ 
							System.out.println("========EMS省外回传接口--连接超时===========");    
							}else{    
								System.out.println("============EMS省外回传接口--其他exception=========="); 
				}
				return -1;
				
			}
			//System.out.println("加密之后的参数"+new String(Base64.encode(xml.getBytes())));
			//System.out.println(Base64.decode(new String(Base64.encode(xml.getBytes())));
			Document doc = null;
			//System.out.println("解码之后的结果"+new String(Base64.decode(OrderFilterResult.getBytes())));
			doc = DocumentHelper.parseText(new String(Base64.decode(OrderFilterResult.getBytes())));
			Element rootElt = doc.getRootElement(); // 获取根节点
		    // rootElt.getName()拿到根节点的名称
			if ("response".equals(rootElt.getName().toString())) {
				Element isAccept = rootElt.element("result");
				if ("1".equals(isAccept.getData())) {
						return c;
				}
				else if ("0".equals(isAccept.getData())) {
					Element reason = rootElt.element("errorDesc");
					System.out.println("订单号"+orderBean.getCode()+"，包裹单号"+packageCode+"调用EMS省外接口错误：原因是"+reason.getData());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
//	public static void main(String[] args){
//		voOrder orderBean = new voOrder();
//		orderBean.setCode("B13042538614");
//		orderBean.setDprice(499);
//		orderBean.setBuyMode(100);
//		String packageCode ="5100000241144";
//		String province="重庆";
//		String city="重庆市";
//		String county="江北区";
//		String address="复盛镇邮局";
//		int area = 3;
//		//重庆重庆市江北区复盛镇邮局(电话通知)
//		long a = 0;
//		for(int i=1;i<=50;i++){
//			System.out.println("调用第"+i+"次接口");
//			long b = emsSwInterfaceTest(orderBean, packageCode, 1, province, city, county, address, area, 9);
//			if(b!=-1){
//				a=a+b;
//			}
//			//System.out.println(emsSwInterface(orderBean, packageCode, 1, province, city, county, address, area, 9));
//		}
//		System.out.println("平均每次执行时间"+a/50+"毫秒");
//	}
//测试接口时用次函数，并且使用前更改包裹单号
	/**
	 * 生成订单评论码，S单不生成，重复发货使用旧评价码
	 * @param order
	 * @param dbOp
	 * @return
	 */
	public boolean createUserOrderCommentCode(voOrder order,DbOperation dbOp){
		if(!order.getCode().startsWith("S")){//非S单需要生成评价码
			String existSql="select id from user_order_comment_code where order_id="+order.getId();
			ResultSet existRs=dbOp.executeQuery(existSql);
			try {
				if(existRs.next()){//如果订单已有评价码则不再生成
					return true;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String code=String.valueOf(order.getId());
			Random ra=new Random();
			DecimalFormat df1 = new DecimalFormat("000");
			String raCode=df1.format(ra.nextInt(999));
			code+=raCode;
			String sql="insert into user_order_comment_code (order_id,code,phone,user_id) values ("
					+order.getId()+",'"+code+"','"+order.getPhone()+"',"+order.getUserId()+");";
			boolean result=dbOp.executeUpdate(sql);
			return result;
		}else{
			return true;
		}
		
	}

	@Override
	public boolean fixBuyStockinCode(String brefCode, DbOperation dbOp,
			BuyStockinBean bean) {
		if( brefCode == null ) {
			return false;
		}
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			int id = dbOp.getLastInsertId();
			String totalCode = this.getFixedBuyStockinCode(brefCode, id);
			if( totalCode == null ) {
				return false;
			}
			StringBuilder updateBuf = new StringBuilder();
			updateBuf.append("update buy_stockin set code='" + totalCode + "' where id=").append(id);
			if( !service.getDbOp().executeUpdate(updateBuf.toString())) {
				return false;
			}
			bean.setCode(totalCode);
			bean.setId(id);
		} catch(Exception e ) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	public String getFixedBuyStockinCode(String brefCode, int id){
		String totalCode = null;
		
		//将刚添加的装箱单的id的后五位截取，并添到日期code之后
		String newCode = null;
		if(id > 99999){
			String strId = String.valueOf(id);
			newCode = strId.substring(strId.length()- 5, strId.length());
		} else {
			DecimalFormat df2 = new DecimalFormat("00000");
			newCode = df2.format(id);
		}
		totalCode = brefCode + newCode;
		
		return totalCode;
	}
	

	@Override
	public List getStockBatchListWithSomeoneComeFirst(String condition,
			int index, int count, String orderBy) {
		List result = new ArrayList();
		ResultSet rs = null;
		DbOperation dbOp = this.getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		String sql = "select *, left(code,1) as codeType from stock_batch where " + condition;
		if( orderBy != null && !orderBy.equals("")) {
			sql += " order by " + orderBy;
		}
		sql = DbOperation.getPagingQuery(sql, index, count);
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				StockBatchBean sbBean = new StockBatchBean();
				sbBean.setId(rs.getInt("id"));
				sbBean.setCode(rs.getString("code"));
				sbBean.setProductId(rs.getInt("product_id"));
				sbBean.setPrice(rs.getFloat("price"));
				sbBean.setBatchCount(rs.getInt("batch_count"));
				sbBean.setStockArea(rs.getInt("stock_area"));
				sbBean.setStockType(rs.getInt("stock_type"));
				sbBean.setProductStockId(rs.getInt("product_stock_id"));
				sbBean.setCreateDateTime(rs.getString("create_datetime"));
				sbBean.setTax(rs.getFloat("tax"));
				sbBean.setSupplierId(rs.getInt("supplier_id"));
				sbBean.setNotaxPrice(rs.getFloat("notax_price"));
				result.add(sbBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	//亚马逊上传订单信息接口
	public static boolean amazonSubmitFeed(String xml) throws FileNotFoundException {
		String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		s += "<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">";
		s += "<Header>";
		s += "<DocumentVersion>1.01</DocumentVersion>";
		s += "<MerchantIdentifier>A1M6IFOE1QV3LD</MerchantIdentifier>";
		s += "</Header>";
		s += "<MessageType>OrderFulfillment</MessageType>";
		s +=xml;
		s += "</AmazonEnvelope>";
		SubmitFeedResponse response = SubmitFeedSample.submitFulfillInfoToAmazon(s,Constants.WARE_UPLOAD+"ware/fulfillment.xml");
		if( response.isSetSubmitFeedResult() ) {
			SubmitFeedResult re = response.getSubmitFeedResult();
			if(re.getFeedSubmissionInfo().getFeedSubmissionId()!=null){
				System.out.println(re.getFeedSubmissionInfo().getFeedSubmissionId());
				return true;
			}else{
				return false;
			}
		}
		return false;

	}
//	public static void main(String[] args){
//		voOrder orderBean = new voOrder();
//		orderBean.setCode("D122011115759");
//		orderBean.setDprice(67);
//		orderBean.setBuyMode(0);
//		orderBean.setPhone("");
//		String packageCode ="1112233504303";
//		String province="云南省";
//		String city="昆明市";
//		String county="西山区";
//		String address="云南省昆明市西山区阳光花园昊院15大栋一单元102室(电话通知)";
//		orderBean.setAddress(address);
//		int area = 9;
//		//重庆重庆市江北区复盛镇邮局(电话通知)
//		System.out.println(emsSwInterface(orderBean, packageCode, 1, province, city, county, address, area, 28));
//	}
//	public static void main(String args[]) {
//		try {
//			StockServiceImpl.amazonSubmitFeed("");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
}
