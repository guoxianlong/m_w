/*
 * Created on 2007-11-14
 *
 */
package adultadmin.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import mmb.rec.stat.bean.QualifiedStockVolumeShareBean;
import mmb.rec.stat.bean.StockShareBean;
import mmb.util.LogUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cache.ProductCache;

import adultadmin.action.vo.voUser;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.ProductStockTuneLogBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2009-4-20
 * 
 * 说明：产品库存操作
 * 注意：增删改操作，均有锁操作，使用时请注意
 * 
 */
public class ProductStockServiceImpl extends BaseServiceImpl implements IProductStockService {

	private static byte[] productStockLock = new byte[0];
	public Log stockUpdateLog = LogFactory.getLog("stockUpdate.Log");

	public ProductStockServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ProductStockServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addProductStock(ProductStockBean bean) {
		synchronized(productStockLock){
			return addXXX(bean, "product_stock");
		}
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteProductStock(String condition) {
		synchronized(productStockLock){
			return deleteXXX(condition, "product_stock");
		}
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ProductStockBean getProductStock(String condition) {
		return (ProductStockBean) getXXX(condition, "product_stock",
				"adultadmin.bean.stock.ProductStockBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getProductStockCount(String condition) {
		return getXXXCount(condition, "product_stock", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getProductStockList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_stock",
				"adultadmin.bean.stock.ProductStockBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateProductStock(String set, String condition) {
		synchronized(productStockLock){
			return updateXXX(set, condition, "product_stock");
		}
	}


	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockExchange(StockExchangeBean bean) {
		return addXXX(bean, "stock_exchange");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockExchange(String condition) {
		return deleteXXX(condition, "stock_exchange");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockExchangeBean getStockExchange(String condition) {
		return (StockExchangeBean) getXXX(condition, "stock_exchange",
				"adultadmin.bean.stock.StockExchangeBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockExchangeCount(String condition) {
		return getXXXCount(condition, "stock_exchange", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockExchangeList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_exchange",
				"adultadmin.bean.stock.StockExchangeBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockExchange(String set, String condition) {
		return updateXXX(set, condition, "stock_exchange");
	}


	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockExchangeProduct(StockExchangeProductBean bean) {
		return addXXX(bean, "stock_exchange_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockExchangeProduct(String condition) {
		return deleteXXX(condition, "stock_exchange_product");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockExchangeProductBean getStockExchangeProduct(String condition) {
		return (StockExchangeProductBean) getXXX(condition, "stock_exchange_product",
				"adultadmin.bean.stock.StockExchangeProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockExchangeProductCount(String condition) {
		return getXXXCount(condition, "stock_exchange_product", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockExchangeProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_exchange_product",
				"adultadmin.bean.stock.StockExchangeProductBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockExchangeProduct(String set, String condition) {
		return updateXXX(set, condition, "stock_exchange_product");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockExchangeProductCargo(StockExchangeProductCargoBean bean) {
		return addXXX(bean, "stock_exchange_product_cargo");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockExchangeProductCargo(String condition) {
		return deleteXXX(condition, "stock_exchange_product_cargo");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockExchangeProductCargoBean getStockExchangeProductCargo(String condition) {
		return (StockExchangeProductCargoBean) getXXX(condition, "stock_exchange_product_cargo",
				"adultadmin.bean.stock.StockExchangeProductCargoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockExchangeProductCargoCount(String condition) {
		return getXXXCount(condition, "stock_exchange_product_cargo", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockExchangeProductCargoList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_exchange_product_cargo",
				"adultadmin.bean.stock.StockExchangeProductCargoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockExchangeProductCargo(String set, String condition) {
		return updateXXX(set, condition, "stock_exchange_product_cargo");
	}

	public void checkProductStatus(int productId) {
		DbOperation dbOp = this.getDbOp();
		Connection con=dbOp.getConn();
		DbOperation db = new DbOperation();
		db.init();
		ResultSet rs1=null;
		ResultSet rs2=null;
		String sql = "select sum(stock) from product_stock where product_id="+productId+" and type=0";
		String update = "update product p set status=130 where id = ? and status < 100";	
		try{
			ResultSet rs = dbOp.executeQuery(sql);
			if(rs!=null && rs.next()){
				if(rs.getInt(1)==0){
					dbOp.prepareStatement(update);			
					dbOp.getPStmt().setInt(1, productId);			
					dbOp.getPStmt().executeUpdate();
				}
			}
			dbOp.prepareStatement("update product set status=? where id=?");
			PreparedStatement psProductStatus = dbOp.getPStmt();
			String sql1="select parent_id from  product_package where product_id="+productId;
			String sql2="";
			String sql3="";
			//查看所对应的套装产品
			rs=dbOp.executeQuery(sql1);
			while(rs.next()){
				//查看所有子产品
				sql2="select product_id from product_package where parent_id="+rs.getInt("parent_id");
				rs1=con.prepareStatement(sql2).executeQuery();
				boolean flag=true;//上架状态
				while(rs1.next()){
					//查看每一个子产品的上架状态
					sql3="select status from product where id="+rs1.getInt("product_id");
					rs2=db.executeQuery(sql3);
					if(rs2.next()){
						if(rs2.getInt("status")==130){
							flag=false;
							
						}
					}
					if(!flag){
						break;
					}
				}
				if(!flag){
					psProductStatus.setInt(1, 130);
					psProductStatus.setInt(2, rs.getInt("parent_id"));
					psProductStatus.executeUpdate();
				}
			}
			
			
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);
			db.release();
		}
	}

	public boolean updateProductStockCount(int id, int mcount) {
		boolean result = false;

		String startTime = DateUtil.getNow();
		String log = "update product_stock set stock=(stock + "+mcount+") where id = "+id+" and stock >= "+mcount+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();

		DbOperation dbOp = this.getDbOp();
		String update = "update product_stock set stock=(stock + ?) where id = ? and stock >= ?";
		dbOp.prepareStatement(update);
		try{
			dbOp.getPStmt().setInt(1, mcount);
			dbOp.getPStmt().setInt(2, id);
			dbOp.getPStmt().setInt(3, -mcount);
			if(dbOp.getPStmt().executeUpdate() > 0){
				result = true;
			}
		} catch(Exception e){
			e.printStackTrace();
			stockUpdateLog.error(StringUtil.getExceptionInfo(e));
		} finally {
			this.release(dbOp);

			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新产品库存语句为："+log+",调用者为："+LogUtil.getInvokerName("adultadmin.service.impl.ProductStockServiceImpl"));
		}
		return result;
	}

	public boolean updateProductLockCount(int id, int mcount) {
		boolean result = false;

		String startTime = DateUtil.getNow();
		String log = "update product_stock set lock_count=(lock_count + "+mcount+") where id = "+id+" and lock_count >= "+mcount+"     "+startTime+"   execute :";
		long start = System.currentTimeMillis();

		DbOperation dbOp = this.getDbOp();
		String update = "update product_stock set lock_count=(lock_count + ?) where id = ? and lock_count >= ?";
		dbOp.prepareStatement(update);
		try{
			dbOp.getPStmt().setInt(1, mcount);
			dbOp.getPStmt().setInt(2, id);
			dbOp.getPStmt().setInt(3, -mcount);
			if(dbOp.getPStmt().executeUpdate() > 0){
				result = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);

			long end = System.currentTimeMillis();
			log = log + (end-start)/1000.0 + "s";
			stockUpdateLog.info("更新产品锁定量语句为："+log+",调用者为："+LogUtil.getInvokerName("adultadmin.service.impl.ProductStockServiceImpl"));
		}
		return result;
	}



	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addProductStockTuneLog(ProductStockTuneLogBean bean) {
		return addXXX(bean, "product_stock_tune_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteProductStockTuneLog(String condition) {
		return deleteXXX(condition, "product_stock_tune_log");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ProductStockTuneLogBean getProductStockTuneLog(String condition) {
		return (ProductStockTuneLogBean) getXXX(condition, "product_stock_tune_log",
				"adultadmin.bean.stock.ProductStockTuneLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getProductStockTuneLogCount(String condition) {
		return getXXXCount(condition, "product_stock_tune_log", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getProductStockTuneLogList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_stock_tune_log",
				"adultadmin.bean.stock.ProductStockTuneLogBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateProductStockTuneLog(String set, String condition) {
		return updateXXX(set, condition, "product_stock_tune_log");

	}




	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addStockCard(StockCardBean bean) {
		return addXXX(bean, "stock_card");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteStockCard(String condition) {
		return deleteXXX(condition, "stock_card");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public StockCardBean getStockCard(String condition) {
		return (StockCardBean) getXXX(condition, "stock_card",
				"adultadmin.bean.stock.StockCardBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getStockCardCount(String condition) {
		return getXXXCount(condition, "stock_card", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getStockCardList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "stock_card",
				"adultadmin.bean.stock.StockCardBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateStockCard(String set, String condition) {
		return updateXXX(set, condition, "stock_card");

	}
	
	public boolean addNewProductStock(int productId){
		if (this.getProductStockCount("product_id=" + productId) == 0) {
			ProductStockBean ps = new ProductStockBean();
			ps.setStock(0);
			ps.setLockCount(0);

			// 北库
			ps.setProductId(productId);
			ps.setArea(ProductStockBean.AREA_BJ);
			ps.setType(ProductStockBean.STOCKTYPE_BACK);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_CHECK);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_DEFECTIVE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_QUALIFIED);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			// 维修库 暂时隐藏
			ps.setType(ProductStockBean.STOCKTYPE_REPAIR);
			ps.setStatus(ProductStockBean.STOCKSTATUS_HIDE);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_RETURN);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_SAMPLE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}

			// 广分
			ps.setArea(ProductStockBean.AREA_GF);
			ps.setType(ProductStockBean.STOCKTYPE_BACK);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_CHECK);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_DEFECTIVE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_QUALIFIED);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_REPAIR);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_RETURN);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_SAMPLE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}

			/**
			 * 添加质检库 和 换货库
			 */
			ps.setType(ProductStockBean.STOCKTYPE_QUALITYTESTING);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_NIFFER);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}

			// 广分售后库
			ps.setType(ProductStockBean.STOCKTYPE_AFTER_SALE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}

			// 广速
			ps.setArea(ProductStockBean.AREA_GS);
			ps.setType(ProductStockBean.STOCKTYPE_QUALIFIED);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_RETURN);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			// 残次品库隐藏，暂时不用
			ps.setType(ProductStockBean.STOCKTYPE_DEFECTIVE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_HIDE);
			if(!this.addProductStock(ps)){
				return false;
			}
			/**
			 * 添加质检库 和 换货库
			 */
			ps.setType(ProductStockBean.STOCKTYPE_QUALITYTESTING);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_NIFFER);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			
			//增城
			ps.setArea(ProductStockBean.AREA_ZC);
			ps.setType(ProductStockBean.STOCKTYPE_BACK);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_CHECK);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_DEFECTIVE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_QUALIFIED);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_REPAIR);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_RETURN);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_SAMPLE);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			
			//无锡
			ps.setArea(ProductStockBean.AREA_WX);
			ps.setType(ProductStockBean.STOCKTYPE_QUALIFIED);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_CHECK);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
			ps.setType(ProductStockBean.STOCKTYPE_RETURN);
			ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			if(!this.addProductStock(ps)){
				return false;
			}
		}
		
		return true;
	}
	//添加库存占比表记录
	@Override
	public boolean addStockShare(StockShareBean bean) {
		synchronized(productStockLock){
			return addXXX(bean, "stock_share");
		}
	}
	//增加合格库表记录
	@Override
	public boolean addQualifiedStockVolumeShareBean(
			QualifiedStockVolumeShareBean bean) {
		synchronized(productStockLock){
			return addXXX(bean, "qualified_stock_volume_share");
		}
	}
	
	//新添加的关于退货库调拨单的特殊操作部分代码↓
	/*public ReturnedProductBean getReturnedSaleOutProductBean ( String condition ) {
		return (ReturnedProductBean) getXXX(condition, "returned_saleout_product",
		"mmb.stock.stat.ReturnedProductBean");
	}
	
	public boolean updateReturnedSaleOutProduct(String set, String condition) {
		return updateXXX(set, condition, "returned_saleout_product");
	}
	
//	public boolean updateReturnedProduct(String set, String condition) {
//		return updateXXX(set, condition, "returned_product");
//	}


	public ReturnedProductBean getReturnedProductBean ( String condition ) {
		return (ReturnedProductBean) getXXX(condition, "returned_product",
		"mmb.stock.stat.ReturnedProductBean");
	}*/
	
	/*public boolean updateUnqualifyReturnedProduct(int productId, int type, int count) {
		
		ReturnedProductBean rpb1 = getReturnedProductBean("product_id = " + productId + " and type = " + type );
		if( rpb1 != null && rpb1.getCount() >= count ) {
			int calculateCount = rpb1.getCount() - count;
			return updateReturnedProduct("count = " + calculateCount, "product_id = " + productId + " and type = " + type);
		}  else {
			return false;
		}
	}
	
	public boolean checkUnqualifyReturnedProductNumber( String productCode, int type, int count) {
		boolean result = false;
		ReturnedProductBean rpb1 = getReturnedProductBean("product_code = '" + productCode + "' and type = " + type);
		if( rpb1 != null && rpb1.getCount() >= count ) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}*/
	
	/*public boolean updateUnqualifyReturnedProductBack(int productId, int type, int count) {
		
		ReturnedProductBean rpb1 = getReturnedProductBean("product_id = " + productId + " and type = " + type );
		if( rpb1 != null ) {
			int calculateCount = rpb1.getCount() + count;
			return updateReturnedProduct("count = " + calculateCount, "product_id = " + productId + " and type = " + type);
		} else {
			return false;
		}
	}
	
	public boolean updateReturnedProductCargo(String set, String condition) {
		return updateXXX(set, condition, "returned_product_cargo");
	}
	
	public ReturnedProductCargoBean getReturnedProductCargo(String condition) {
		return (ReturnedProductCargoBean) getXXX(condition, "returned_product_cargo",
		"mmb.stock.stat.ReturnedProductCargoBean");
	}
	
	public List getReturnedProductCargoList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "returned_product_cargo", "mmb.stock.stat.ReturnedProductCargoBean");
	}*/
	
	/*public boolean addUnappraisalNumberOrReturnedProduct(int productId, String productCode, String productName,int count) {
		ReturnedProductBean rpb = getReturnedProductBean("product_id = "
				+ productId + " and type = 0");
		if (rpb == null) {
			ReturnedProductBean rpb0 = new ReturnedProductBean();
			ReturnedProductBean rpb1 = new ReturnedProductBean();
			ReturnedProductBean rpb2 = new ReturnedProductBean();
			rpb0.setProductCode(productCode);
			rpb0.setProductId(productId);
			rpb0.setProductName(productName);
			rpb0.setType(0);
			rpb0.setCount(count);
			rpb1.setProductCode(productCode);
			rpb1.setProductId(productId);
			rpb1.setProductName(productName);
			rpb1.setType(1);
			rpb1.setCount(0);
			rpb2.setProductCode(productCode);
			rpb2.setProductId(productId);
			rpb2.setProductName(productName);
			rpb2.setType(2);
			rpb2.setCount(0);
			boolean result = addReturnedProduct(rpb0)
					&& addReturnedProduct(rpb1) && addReturnedProduct(rpb2);
			return result;
		} else {
			int totalCount = rpb.getCount() + count;
			return updateReturnedProduct("count = " + totalCount, "product_id = "
					+ rpb.getProductId() + " and type = 0");
		}
	}
	
	public boolean addReturnedProduct(ReturnedProductBean bean) {
		return addXXX(bean, "returned_product");
	}*/
	
	/**
	 * 判断非删除入库单商品数量与预计入库单商品数量（入库单商品数量不能多于预计入库单商品数量）
	 * 
	 * @author zy
	 * 
	 * @param dbop
	 * @param stockinId      如果是修改，这个是当前修改的入库单id，如果为-1表示为确认入库和审核通过
	 * @param editCount      如果是修改，这个是当前修改的商品数量或初录复录数量
	 * @param buyStockCount  预计入库单商品数量
	 * @param productId      商品id
	 * @param buyStockId     预计入库单id
	 * @return
	 */
	public boolean checkBuyStockinProductCount(DbOperation dbop, int stockinId, int editCount, int buyStockCount, int productId, int buyStockId) {
		ResultSet rs = null;
		String sql = null;
		int buyStockinCount = editCount;
		boolean result = false;
		try {
			if (stockinId == -1) {
				sql = "select sum(stockin_count) from buy_stockin_product where buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+ buyStockId +
						" and status != "+BuyStockinBean.STATUS8+") and product_id = " + productId;
			} else {
				sql = "select sum(stockin_count) from buy_stockin_product where buy_stockin_id in (select id from buy_stockin where buy_stock_id = "+ buyStockId +
						" and status != "+BuyStockinBean.STATUS8+") and product_id = " + productId + " and buy_stockin_id != " + stockinId;
			}
			rs = dbop.executeQuery(sql);
			if (rs.next()) {
				buyStockinCount += rs.getInt(1);
			}
			if (buyStockinCount <= buyStockCount) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@Override
	public void checkProductStatus(int productId, voUser user) {
		DbOperation dbOp = this.getDbOp();
		Connection con=dbOp.getConn();
		DbOperation db = new DbOperation();
		db.init();
		ResultSet rs1=null;
		ResultSet rs2=null;
		String sql = "select sum(stock) from product_stock where product_id="+productId+" and type=0";
		String update = "update product p set status=130 where id = ? and status < 100";	
		try{
			ResultSet rs = dbOp.executeQuery(sql);
			if(rs!=null && rs.next()){
				if(rs.getInt(1)==0){
					//查询当前产品的状态
					String productSql = "select status from product where id = " + productId;
					ResultSet set = dbOp.executeQuery(productSql);
					int status = 0;
					while(set.next()){
						status = set.getInt("status");
					}
					insertProductAdminHistory(productId, user, dbOp, status);
					
					dbOp.prepareStatement(update);			
					dbOp.getPStmt().setInt(1, productId);			
					dbOp.getPStmt().executeUpdate();
				}
			}
			dbOp.prepareStatement("update product set status=? where id=?");
			PreparedStatement psProductStatus = dbOp.getPStmt();
			String sql1="select parent_id from  product_package where product_id="+productId;
			String sql2="";
			String sql3="";
			//查看所对应的套装产品
			rs=dbOp.executeQuery(sql1);
			while(rs.next()){
				//查看所有子产品
				sql2="select product_id from product_package where parent_id="+rs.getInt("parent_id");
				rs1=con.prepareStatement(sql2).executeQuery();
				boolean flag=true;//上架状态
				int status = 0;
				while(rs1.next()){
					//查看每一个子产品的上架状态
					sql3="select status from product where id="+rs1.getInt("product_id");
					rs2=db.executeQuery(sql3);
					if(rs2.next()){
						status = rs2.getInt("status");
						if(status==130){
							flag=false;
						}
					}
					if(!flag){
						break;
					}
				}
				if(!flag){
					insertProductAdminHistory(rs1.getInt("product_id"), user, dbOp, status);
					
					psProductStatus.setInt(1, 130);
					psProductStatus.setInt(2, rs.getInt("parent_id"));
					psProductStatus.executeUpdate();
				}
			}
			
			
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			this.release(dbOp);
			db.release();
		}
	}
	
	/**
	 * 插入产品操作记录
	 *@author 李宁
	 *@date 2013-5-6 下午2:10:36
	 * @param productId
	 * @param user
	 * @param dbOp
	 * @param status
	 * @throws SQLException
	 */
	private void insertProductAdminHistory(int productId, voUser user, DbOperation dbOp, int status) throws SQLException {
		String insert = "insert into product_admin_history (product_id,admin_id,admin_name,remark,oper_datetime,deleted) values (?,?,?,?,?,?)";
		dbOp.prepareStatement(insert);
		PreparedStatement statment = dbOp.getPStmt();
		statment.setInt(1, productId);
		statment.setInt(2, user.getId());
		statment.setString(3, user.getUsername());
		statment.setString(4, "(" + ProductCache.getProductStatusName(status) + "-缺货)");
		statment.setString(5, DateUtil.getNow());
		statment.setInt(6, 0);
		statment.executeUpdate();
	}
	
	/**
	 * 获取库区域表
	 * @param condition
	 * @return
	 * @author 李宁
	* @date 2014-5-22
	 */
	public StockAreaBean getStockArea(String condition){
		return (StockAreaBean) getXXX(condition, "stock_area", "adultadmin.bean.stock.StockAreaBean");
	}
}
