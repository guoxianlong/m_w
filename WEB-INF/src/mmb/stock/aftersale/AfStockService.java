package mmb.stock.aftersale;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import mmb.easyui.EasyuiComBoBoxBean;
import mmb.easyui.EasyuiDataGridBean;
import mmb.easyui.EasyuiPageBean;
import mmb.easyui.Json;
import mmb.rec.oper.bean.ProductSellPropertyBean;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.IMEI.IMEIBean;
import mmb.stock.IMEI.IMEILogBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.fitting.model.AfterSaleFittings;
import mmb.stock.fitting.model.FittingBuyStockInBean;
import mmb.util.StockCommon;
import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;
import adultadmin.action.bybs.ByBsAction;
import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.afterSales.AfterSaleOrderBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.impl.ProductStockServiceImpl;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

/**
 * 售后仓内作业
 * 
 */
public class AfStockService extends BaseServiceImpl {
	private static byte[] lock = new byte[0];

	public AfStockService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}
	
	public boolean addAfterSaleBackSupplier(AfterSaleBackSupplier bean) {
		return addXXX(bean, "after_sale_back_supplier");
	}

	public int getAfterSaleBackSupplierCount(String condition) {
		return getXXXCount(condition, "after_sale_back_supplier", "id");
	}

	public AfterSaleBackSupplier getAfterSaleBackSupplier(String condition) {
		return (AfterSaleBackSupplier) getXXX(condition,"after_sale_back_supplier","mmb.stock.aftersale.AfterSaleBackSupplier");
	}

	public boolean updateAfterSaleBackSupplier(String set, String condition) {
		return updateXXX(set, condition, "after_sale_back_supplier");
	}

	public boolean deleteAfterSaleBackSupplier(String condition) {
		return deleteXXX(condition, "after_sale_back_supplier");
	}

	public boolean addAfterSaleBackSupplierProduct(
			AfterSaleBackSupplierProduct bean) {
		return addXXX(bean, "after_sale_back_supplier_product");
	}
	

	public List getAfterSaleBackSupplierProductList(String condition,int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,"after_sale_back_supplier_product","mmb.stock.aftersale.AfterSaleBackSupplierProduct");
	}

	public int getAfterSaleBackSupplierProductCount(String condition) {
		return getXXXCount(condition, "after_sale_back_supplier_product", "id");
	}

	public AfterSaleBackSupplierProduct getAfterSaleBackSupplierProduct(String condition) {
		return (AfterSaleBackSupplierProduct) getXXX(condition,"after_sale_back_supplier_product","mmb.stock.aftersale.AfterSaleBackSupplierProduct");
	}

	public boolean updateAfterSaleBackSupplierProduct(String set,String condition) {
		return updateXXX(set, condition, "after_sale_back_supplier_product");
	}

	public boolean deleteAfterSaleBackSupplierProduct(String condition) {
		return deleteXXX(condition, "after_sale_back_supplier_product");
	}

	public boolean addAfterSaleBackUserPackage(AfterSaleBackUserPackage bean) {
		return addXXX(bean, "after_sale_back_user_package");
	}

	public List getAfterSaleBackUserPackageList(String condition, int index,int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,"after_sale_back_user_package","mmb.stock.aftersale.AfterSaleBackUserPackage");
	}

	public int getAfterSaleBackUserPackageCount(String condition) {
		return getXXXCount(condition, "after_sale_back_user_package", "id");
	}

	public AfterSaleBackUserPackage getAfterSaleBackUserPackage(String condition) {
		return (AfterSaleBackUserPackage) getXXX(condition,"after_sale_back_user_package","mmb.stock.aftersale.AfterSaleBackUserPackage");
	}

	public boolean updateAfterSaleBackUserPackage(String set, String condition) {
		return updateXXX(set, condition, "after_sale_back_user_package");
	}

	public boolean deleteAfterSaleBackUserPackage(String condition) {
		return deleteXXX(condition, "after_sale_back_user_package");
	}

	public boolean addAfterSaleBackUserProduct(AfterSaleBackUserProduct bean) {
		return addXXX(bean, "after_sale_back_user_product");
	}

	public List getAfterSaleBackUserProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_back_user_product",
				"mmb.stock.aftersale.AfterSaleBackUserProduct");
	}

	public int getAfterSaleBackUserProductCount(String condition) {
		return getXXXCount(condition, "after_sale_back_user_product", "id");
	}

	public AfterSaleBackUserProduct getAfterSaleBackUserProduct(String condition) {
		return (AfterSaleBackUserProduct) getXXX(condition,
				"after_sale_back_user_product",
				"mmb.stock.aftersale.AfterSaleBackUserProduct");
	}

	public boolean updateAfterSaleBackUserProduct(String set, String condition) {
		return updateXXX(set, condition, "after_sale_back_user_product");
	}

	public boolean deleteAfterSaleBackUserProduct(String condition) {
		return deleteXXX(condition, "after_sale_back_user_product");
	}

	public boolean addAfterSaleSeal(AfterSaleSeal bean) {
		return addXXX(bean, "after_sale_seal");
	}

	public List getAfterSaleSealList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_seal",
				"mmb.stock.aftersale.AfterSaleSeal");
	}

	public int getAfterSaleSealCount(String condition) {
		return getXXXCount(condition, "after_sale_seal", "id");
	}

	public AfterSaleSeal getAfterSaleSeal(String condition) {
		return (AfterSaleSeal) getXXX(condition, "after_sale_seal",
				"mmb.stock.aftersale.AfterSaleSeal");
	}

	public boolean updateAfterSaleSeal(String set, String condition) {
		return updateXXX(set, condition, "after_sale_seal");
	}

	public boolean deleteAfterSaleSeal(String condition) {
		return deleteXXX(condition, "after_sale_seal");
	}

	public boolean addAfterSaleSealProduct(AfterSaleSealProduct bean) {
		return addXXX(bean, "after_sale_seal_product");
	}

	@SuppressWarnings("rawtypes")
	public List getAfterSaleSealProductList(String condition, int index,int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,"after_sale_seal_product","mmb.stock.aftersale.AfterSaleSealProduct");
	}

	public int getAfterSaleSealProductCount(String condition) {
		return getXXXCount(condition, "after_sale_seal_product", "id");
	}

	public AfterSaleSealProduct getAfterSaleSealProduct(String condition) {
		return (AfterSaleSealProduct) getXXX(condition,
				"after_sale_seal_product",
				"mmb.stock.aftersale.AfterSaleSealProduct");
	}

	public boolean updateAfterSaleSealProduct(String set, String condition) {
		return updateXXX(set, condition, "after_sale_seal_product");
	}

	public boolean deleteAfterSaleSealProduct(String condition) {
		return deleteXXX(condition, "after_sale_seal_product");
	}

	public boolean addAfterSaleDetectProductUpshelf(
			AfterSaleDetectProductUpshelf bean) {
		return addXXX(bean, "after_sale_detect_product_upshelf");
	}

	public List getAfterSaleDetectProductUpshelfList(String condition,
			int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_detect_product_upshelf",
				"mmb.stock.aftersale.AfterSaleDetectProductUpshelf");
	}

	public int getAfterSaleDetectProductUpshelfCount(String condition) {
		return getXXXCount(condition, "after_sale_detect_product_upshelf", "id");
	}

	public AfterSaleDetectProductUpshelf getAfterSaleDetectProductUpshelf(
			String condition) {
		return (AfterSaleDetectProductUpshelf) getXXX(condition,
				"after_sale_detect_product_upshelf",
				"mmb.stock.aftersale.AfterSaleDetectProductUpshelf");
	}

	public boolean updateAfterSaleDetectProductUpshelf(String set,
			String condition) {
		return updateXXX(set, condition, "after_sale_detect_product_upshelf");
	}

	public boolean deleteAfterSaleDetectProductUpshelf(String condition) {
		return deleteXXX(condition, "after_sale_detect_product_upshelf");
	}

	// after_sale_detect_package签收包裹列表
	public boolean addAfterSaleDetectPackage(AfterSaleDetectPackageBean bean) {
		return addXXX(bean, "after_sale_detect_package");
	}

	public List getAfterSaleDetectPackageList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_detect_package",
				"mmb.stock.aftersale.AfterSaleDetectPackageBean");
	}

	public int getAfterSaleDetectPackageCount(String condition) {
		return getXXXCount(condition, "after_sale_detect_package", "id");
	}

	public AfterSaleDetectPackageBean getAfterSaleDetectPackage(String condition) {
		return (AfterSaleDetectPackageBean) getXXX(condition,
				"after_sale_detect_package",
				"mmb.stock.aftersale.AfterSaleDetectPackageBean");
	}

	public boolean updateAfterSaleDetectPackage(String set, String condition) {
		return updateXXX(set, condition, "after_sale_detect_package");
	}

	public boolean deleteAfterSaleDetectPackage(String condition) {
		return deleteXXX(condition, "after_sale_detect_package");
	}

	// after_sale_detect_product签收包裹商品（处理单）
	public boolean addAfterSaleDetectProduct(AfterSaleDetectProductBean bean) {
		return addXXX(bean, "after_sale_detect_product");
	}

	public List getAfterSaleDetectProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_detect_product",
				"mmb.stock.aftersale.AfterSaleDetectProductBean");
	}

	public int getAfterSaleDetectProductCount(String condition) {
		return getXXXCount(condition, "after_sale_detect_product", "id");
	}

	public AfterSaleDetectProductBean getAfterSaleDetectProduct(String condition) {
		return (AfterSaleDetectProductBean) getXXX(condition,
				"after_sale_detect_product",
				"mmb.stock.aftersale.AfterSaleDetectProductBean");
	}

	public boolean updateAfterSaleDetectProduct(String set, String condition) {
		return updateXXX(set, condition, "after_sale_detect_product");
	}

	public boolean deleteAfterSaleDetectProduct(String condition) {
		return deleteXXX(condition, "after_sale_detect_product");
	}

	// after_sale_detect_type检测选项
	public boolean addAfterSaleDetectType(AfterSaleDetectTypeBean bean) {
		return addXXX(bean, "after_sale_detect_type");
	}

	public List getAfterSaleDetectTypeList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_detect_type",
				"mmb.stock.aftersale.AfterSaleDetectTypeBean");
	}

	public int getAfterSaleDetectTypeCount(String condition) {
		return getXXXCount(condition, "after_sale_detect_type", "id");
	}

	public AfterSaleDetectTypeBean getAfterSaleDetectType(String condition) {
		return (AfterSaleDetectTypeBean) getXXX(condition,
				"after_sale_detect_type",
				"mmb.stock.aftersale.AfterSaleDetectTypeBean");
	}

	public boolean updateAfterSaleDetectType(String set, String condition) {
		return updateXXX(set, condition, "after_sale_detect_type");
	}

	public boolean deleteAfterSaleDetectType(String condition) {
		return deleteXXX(condition, "after_sale_detect_type");
	}

	// 检测选项设置内容after_sale_detect_type_detail

	public boolean addAfterSaleDetectTypeDetail(
			AfterSaleDetectTypeDetailBean bean) {
		return addXXX(bean, "after_sale_detect_type_detail");
	}

	public List getAfterSaleDetectTypeDetailList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_detect_type_detail",
				"mmb.stock.aftersale.AfterSaleDetectTypeDetailBean");
	}

	public int getAfterSaleDetectTypeDetailCount(String condition) {
		return getXXXCount(condition, "after_sale_detect_type_detail", "id");
	}

	public AfterSaleDetectTypeDetailBean getAfterSaleDetectTypeDetail(
			String condition) {
		return (AfterSaleDetectTypeDetailBean) getXXX(condition,
				"after_sale_detect_type_detail",
				"mmb.stock.aftersale.AfterSaleDetectTypeDetailBean");
	}

	public boolean updateAfterSaleDetectTypeDetail(String set, String condition) {
		return updateXXX(set, condition, "after_sale_detect_type_detail");
	}

	public boolean deleteAfterSaleDetectTypeDetail(String condition) {
		return deleteXXX(condition, "after_sale_detect_type_detail");
	}

	// 检测信息表after_sale_detect_log

	public boolean addAfterSaleDetectLog(AfterSaleDetectLogBean bean) {
		return addXXX(bean, "after_sale_detect_log");
	}

	public List getAfterSaleDetectLogList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_detect_log",
				"mmb.stock.aftersale.AfterSaleDetectLogBean");
	}

	public int getAfterSaleDetectLogCount(String condition) {
		return getXXXCount(condition, "after_sale_detect_log", "id");
	}

	public AfterSaleDetectLogBean getAfterSaleDetectLog(String condition) {
		return (AfterSaleDetectLogBean) getXXX(condition,
				"after_sale_detect_log",
				"mmb.stock.aftersale.AfterSaleDetectLogBean");
	}

	public boolean updateAfterSaleDetectLog(String set, String condition) {
		return updateXXX(set, condition, "after_sale_detect_log");
	}

	public boolean deleteAfterSaleDetectLog(String condition) {
		return deleteXXX(condition, "after_sale_detect_log");
	}

	public boolean addAfterSaleStockin(AfterSaleStockin bean) {
		return addXXX(bean, "after_sale_stockin");
	}

	public List getAfterSaleStockinList(String condition, int index, int count,
			String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_stockin", "mmb.stock.aftersale.AfterSaleStockin");
	}

	public int getAfterSaleStockinCount(String condition) {
		return getXXXCount(condition, "after_sale_stockin", "id");
	}

	public AfterSaleStockin getAfterSaleStockin(String condition) {
		return (AfterSaleStockin) getXXX(condition, "after_sale_stockin",
				"mmb.stock.aftersale.AfterSaleStockin");
	}

	public boolean updateAfterSaleStockin(String set, String condition) {
		return updateXXX(set, condition, "after_sale_stockin");
	}

	public boolean deleteAfterSaleStockin(String condition) {
		return deleteXXX(condition, "after_sale_stockin");
	}

	public boolean addAfterSaleDetectProductCode(
			AfterSaleDetectProductCodeBean bean) {
		return addXXX(bean, "after_sale_detect_product_code");
	}

	public boolean deleteAfterSaleDetectProductCode(String condition) {
		return deleteXXX(condition, "after_sale_detect_product_code");
	}

	public boolean updateAfterSaleDetectProductCode(String set, String condition) {
		return updateXXX(set, condition, "after_sale_detect_product_code");
	}

	@SuppressWarnings("unchecked")
	public List<AfterSaleDetectProductCodeBean> getAfterSaleDetectProductCodeList(
			String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_detect_product_code",
				"mmb.stock.aftersale.AfterSaleDetectProductCodeBean");
	}

	public AfterSaleDetectProductCodeBean getAfterSaleDetectProductCode(
			String condition) {
		return (AfterSaleDetectProductCodeBean) getXXX(condition,
				"after_sale_detect_product_code",
				"mmb.stock.aftersale.AfterSaleDetectProductCodeBean");
	}
    /**
     * 说明：判断用户是否拥有对应地区售后库权限
     * @param user 登陆用户
     * @param areaId 地区ID
     * @author syuf
     */
	public boolean checkAfterSaleUserGroup(voUser user,int areaId){
		UserGroupBean group = user.getGroup();
		if(areaId == ProductStockBean.AREA_SZ){
			if(!group.isFlag(2063)){
				return false;
			}
		} else if(areaId == ProductStockBean.AREA_GF){
			if(!group.isFlag(2062)){
				return false;
			}
		}else if(areaId == ProductStockBean.AREA_WX){
			if(!group.isFlag(2188)){
				return false;
			}
		}else if(areaId == ProductStockBean.AREA_ZC){
			if(!group.isFlag(3103)){
				return false;
			}			
		}else if(areaId == ProductStockBean.AREA_CD){
			if(!group.isFlag(3102)){
				return false;
			}			
		}
		return true;
	}
	
	/**
	 * @return
	 * @author zy
	 */
	public int getDetectTypeDetailCount(int detectTypeParentId1,int detectTypeParentId2,String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(asdtd.id) "
				+ "FROM after_sale_detect_type_detail asdtd JOIN after_sale_detect_type asdt ON asdtd.after_sale_detect_type_id = asdt.id  and asdt.id!=17 "
				+ "JOIN catalog c ON asdtd.parent_id1 = c.id AND c.parent_id = 0  ";
		if(detectTypeParentId1 > 0){
			query= query + " join after_sale_detect_type_detail asd on asdtd.detect_type_parent_id1 = asd.id ";
		}
		if(detectTypeParentId2 > 0){
			query= query + " join after_sale_detect_type_detail asd2 on asdtd.detect_type_parent_id2 = asd2.id ";
		}
		int count = 0;
		if (null != condition && !condition.equals("")) {
			query= query + " where 1=1 " + condition;
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 
	 * @return
	 * @author syuf
	 */
	public int getDetectPackAgeCountLeft(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "select count(a.id) " 
				+"from (SELECT count(asdp.id) as id "
				+ "from after_sale_detect_package asdp " 
				+ "LEFT JOIN after_sale_detect_product AS asdpr ON asdpr.after_sale_detect_package_id = asdp.id " 
				+ "where  " + condition + " group by asdp.id) as a";
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}
	/**
	 * 
	 * @return
	 * @author hepeng
	 */
	public int getDetectPackAgeCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "select count(a.id) " 
						+"from (SELECT count(asdp.id) as id "
							+ "from after_sale_detect_package asdp " 
							+ "INNER JOIN after_sale_detect_product AS asdpr ON asdpr.after_sale_detect_package_id = asdp.id " 
							+ "where  " + condition + " group by asdp.id) as a";
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 获取待再次检查商品列表总条数
	 * 
	 * @return
	 * @author hepeng
	 */
	public int getDetectProductCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "select count(ap.id) from after_sale_detect_product ap,product p  where ap.status=1 and p.id=ap.product_id ";
		int count = 0;
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 查询待寄回用户商品列表总条数
	 * 
	 * @return
	 * @author hepeng
	 */
	public int getBackUserCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(afbp.id)  from after_sale_back_user_product afbp " +
				"join after_sale_detect_product ap on afbp.after_sale_detect_product_id=ap.id " +
				" join product p on afbp.product_id=p.id " + 
				"left join after_sale_replace_new_product_record asrnpr on asrnpr.after_sale_detect_product_id=ap.id " + 
				"where package_id=0";
		int count = 0;
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 查询售后库上架商品列表总条数
	 * 
	 * @return
	 * @author hepeng
	 */
	public int getSaleUpshelfCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(ap.id) " +
				"from after_sale_detect_product ap,cargo_info cai,after_sale_stockin ats  " +
				"where ap.cargo_whole_code=cai.whole_code  and cai.store_type=2  and ats.after_sale_detect_product_id=ap.id and cai.stock_type in (9) ";
		int count = 0;
		if (null != condition && !condition.equals("")) {
			query += condition;
		}

		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 查询售后库入库列表总条数
	 * 
	 * @return
	 * @author hepeng
	 */
	public int getSaleStockCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(ats.id) from after_sale_stockin ats ,after_sale_detect_product ap,product p  where 1=1 and ats.after_sale_detect_product_id=ap.id and p.id=ats.product_id  and ats.status=0 ";
		int count = 0;
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 获取已检测商品上架任务总条数
	 * 
	 * @return
	 * @author hepeng
	 */
	public int getProductUpShelfCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "select count(ap.product_id) " +
				"FROM after_sale_detect_product AS ap INNER JOIN cargo_info AS cai ON ap.cargo_whole_code = cai.whole_code " +
				" INNER JOIN product AS p ON p.id = ap.product_id " + 
				" LEFT JOIN after_sale_back_supplier_product AS bsp ON ap.id = bsp.after_sale_detect_product_id " +
				" WHERE cai.store_type=2 and cai.stock_type = 10  AND ap.status NOT IN (8, 9, 11, 15, 16, 17) ";
		int count = 0;
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 说明：获取等待返厂商品总记录数
	 * 日期：2014-06-27
	 * @author syuf
	 */
	public int getWaitBackSupplierProductCount(String condition) {
		DbOperation dbop = this.getDbOp();
		int count = 0;
		String query = "SELECT count(absp.id) " +
				"FROM after_sale_back_supplier_product AS absp " +
				"INNER JOIN product AS p ON absp.product_id = p.id " +
				"INNER JOIN after_sale_detect_product AS asdp ON asdp.id = absp.after_sale_detect_product_id where  1=1 " + condition;
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}
	/**
	 * 
	 * @return
	 * @author hepeng
	 */
	public int getSupplierProductCount(String condition, String status) {
		DbOperation dbop = this.getDbOp();
		int count = 0;
		String query = "";
		if (status.equals("0")) {// 等待返厂 3
			query = "SELECT count(absp.id) " +
					"FROM after_sale_back_supplier_product AS absp " +
					"INNER JOIN product AS p ON absp.product_id = p.id " +
					"INNER JOIN after_sale_detect_product AS asdp ON asdp.id = absp.after_sale_detect_product_id where absp.status ="
					+ AfterSaleBackSupplierProduct.STATUS3 + condition;
		} else {
			query = "SELECT count(a.id) from (SELECT count(absp.id) id " +
					"from after_sale_back_supplier_product AS absp, after_sale_detect_product AS ap, product AS p, after_sale_detect_log AS al  " +
					"where absp.product_id=p.id and al.after_sale_detect_type_id=11 " +
					"AND absp.after_sale_detect_product_id = ap.id AND ap.status NOT IN (8, 9, 11, 15, 16, 17)  " +
					"AND al.after_sale_detect_product_id=absp.after_sale_detect_product_id and absp.status =  "+ AfterSaleBackSupplierProduct.STATUS1 + condition
					+ " group by absp.id) as a";

		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * @return 检测选项列表
	 * @author zy
	 */
	public List<AfterSaleDetectTypeDetailBean> getDetectTypeDetailList(int detectTypeParentId1,int detectTypeParentId2,
			String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		StringBuilder query = new StringBuilder("SELECT asdtd.id,asdtd.after_sale_detect_type_id,asdt.name,asdtd.parent_id1,c.name parentId1Name,"
				+ "asdtd.content,asdtd.detect_type_parent_id1,asdtd.detect_type_parent_id2");
		if(detectTypeParentId1>0){
			query.append(",asd.content");
		}
		if(detectTypeParentId2>0){
			query.append(",asd2.content");
		}
		query.append(" FROM after_sale_detect_type_detail asdtd JOIN after_sale_detect_type asdt ON asdtd.after_sale_detect_type_id = asdt.id and asdt.id!=17")
		.append(" JOIN catalog c ON asdtd.parent_id1 = c.id AND c.parent_id = 0 ");
		if(detectTypeParentId1 > 0){
			query.append(" join after_sale_detect_type_detail asd on asdtd.detect_type_parent_id1 = asd.id ");
		}
		if(detectTypeParentId2 > 0){
			query.append(" join after_sale_detect_type_detail asd2 on asdtd.detect_type_parent_id2 = asd2.id ");
		}

		if (null != condition && !condition.equals("")) {
			query.append(" where 1=1 ").append(condition);
		}
		if (orderBy != null) {
			query.append(" order by ").append(orderBy);
		}
		String sql = DbOperation.getPagingQuery(query.toString(), index, count);
		List<AfterSaleDetectTypeDetailBean> list = new ArrayList<AfterSaleDetectTypeDetailBean>();
		AfterSaleDetectTypeDetailBean asdtdBean = null;
		try {
			ResultSet rs = dbop.executeQuery(sql);
			while (rs.next()) {
				asdtdBean = new AfterSaleDetectTypeDetailBean();
				asdtdBean.setId(rs.getInt(1));
				asdtdBean.setAfterSaleDetectTypeId(rs.getInt(2));
				asdtdBean.setAfterSaleDetectTypeName(rs.getString(3));
				asdtdBean.setParentId1(rs.getInt(4));
				asdtdBean.setParentId1Name(rs.getString(5));
				asdtdBean.setContent(rs.getString(6));
				asdtdBean.setDetectTypeParentId1(rs.getInt(7));
				asdtdBean.setDetectTypeParentId2(rs.getInt(8));
				if(detectTypeParentId1 > 0){
					asdtdBean.setDetectTypeParentId1Name(rs.getString(9));
				}
				if(detectTypeParentId2 > 0){
					asdtdBean.setDetectTypeParentId2Name(rs.getString(10));
				}
				list.add(asdtdBean);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * 根据条件获取封箱列表
	 * 
	 * @author 李宁
	 * @date 2014-2-12 下午2:09:34
	 * @param condition
	 * @param groupBy
	 * @param index
	 * @param count
	 * @return
	 */
	public List<AfterSaleSeal> getAfterSaleSealList(String condition,
			String groupBy, int index, int count) {
		DbOperation dbop = this.getDbOp();
		String query = "select ass.id,ass.`code`,ass.create_datetime,ass.user_name,ass.user_id,ass.status,count(assp.after_sale_detect_product_id) amount "
				+ "from after_sale_seal ass left join after_sale_seal_product assp on ass.id=assp.after_sale_seal_id "
				+ "left join after_sale_detect_product asdp on assp.after_sale_detect_product_id = asdp.id "
				+ " where ass.status=0 and assp.status in (1,2)";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (groupBy != null) {
			query += " group by " + groupBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		AfterSaleSeal bean = null;
		List<AfterSaleSeal> list = new ArrayList<AfterSaleSeal>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				bean = new AfterSaleSeal();
				bean.setId(rs.getInt(1));
				bean.setCode(rs.getString(2));
				Date date = DateUtil.parseDate(rs.getString(3));
				bean.setCreateDatetime(DateUtil.formatDate(date, "yyyy年MM月dd日"));
				bean.setUserName(rs.getString(4));
				bean.setUserId(rs.getInt(5));
				bean.setStatus(rs.getInt(6));
				bean.setSealProductCount(rs.getInt(7));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}
	
	/**
	 * 根据条件获取封箱商品数量
	 * 
	 * @author 李宁
	 * @date 2014-2-12 下午2:09:34
	 * @param condition
	 * @param groupBy
	 * @param index
	 * @param count
	 * @return
	 */
	public int getSealCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "select count(ass.id) from after_sale_seal ass left join after_sale_seal_product assp on ass.id=assp.after_sale_seal_id "
				+ "left join after_sale_detect_product asdp on assp.after_sale_detect_product_id = asdp.id "
				+ " where ass.status=0 and assp.status in (1,2)";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int result = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			if(rs!=null && rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return result;
	}

	/**
	 * 根据条件获取封箱产品详细信息列表
	 * 
	 * @author 李宁
	 * @date 2014-2-12 下午2:11:05
	 * @param condition
	 * @return
	 */
	public List<AfterSaleSealProduct> getAfterSaleSealProducts(String query,
			String condition, String groupBy) {
		DbOperation dbop = this.getDbOp();
		if (null != condition && !condition.equals("")) {
			query += " where " + condition;
		}
		if (null != groupBy && !groupBy.equals("")) {
			query += " group by " + groupBy;
		}
		List<AfterSaleSealProduct> list = new ArrayList<AfterSaleSealProduct>();
		AfterSaleSealProduct bean = null;
		try {
			ResultSet rs = dbop.executeQuery(query);
			int columns = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				bean = new AfterSaleSealProduct();
				bean.setProductName(rs.getString(1));
				bean.setProductOriname(rs.getString(2));
				bean.setImei(rs.getString(3));
				bean.setAfterSaleOrderCode(rs.getString(4));
				bean.setAfterSaleOrderStatus(rs.getInt(5));
				bean.setAfterSaleDetectProductCode(rs.getString(6));
				bean.setAfterSaleOrderDetectProductStatus(rs.getInt(7));
				if (columns > 7) {
					bean.setAfterSaleDetectProductId(rs.getInt(8));
					bean.setProductId(rs.getInt(9));
					if (columns > 9) {
						bean.setId(rs.getInt(10));
					}
				}
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * 根据条件获取带解封产品详细信息列表
	 * 
	 * @author 李宁
	 * @date 2014-2-12 下午2:11:05
	 * @param condition
	 * @return
	 */
	public List<Map<String, String>> getAfterSaleSealProductsList(
			String condition, int index, int count) {
		DbOperation dbop = this.getDbOp();
		String query = "select assp.id,p.name,afdp.after_sale_order_code,afdp.`code`,ass.`code`,ass.create_datetime "
				+ "from after_sale_seal_product assp left join after_sale_seal ass "
				+ "on assp.after_sale_seal_id=ass.id "
				+ "left join after_sale_detect_product afdp "
				+ "on assp.after_sale_detect_product_id=afdp.id "
				+ "left join product p on assp.product_id=p.id "
				+ "where assp.status=2";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List list = new ArrayList();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("after_sale_seal_product_id", rs.getInt(1) + "");
				map.put("product_name", rs.getString(2));
				map.put("after_sale_order_code", rs.getString(3));
				map.put("after_sale_detect_product_code", rs.getString(4));
				map.put("after_sale_seal_code", rs.getString(5));
				Date createDate = DateUtil.parseDate(rs.getString(6));
				map.put("seal_date",
						DateUtil.formatDate(createDate, "yyyy年MM月dd日"));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}
	
	/**
	 * 根据条件获取带解封产品数量
	 * 
	 * @author 李宁
	 * @date 2014-2-12 下午2:11:05
	 * @param condition
	 * @return
	 */
	public int getAfterSaleSealProductsCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "select count(assp.id) from after_sale_seal_product assp left join after_sale_seal ass "
				+ "on assp.after_sale_seal_id=ass.id "
				+ "left join after_sale_detect_product afdp "
				+ "on assp.after_sale_detect_product_id=afdp.id "
				+ "left join product p on assp.product_id=p.id "
				+ "where assp.status=2";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int result = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			if(rs!=null && rs.next()) {
				result = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return result;
	}

	/**
	 * @return 以换代修商品 表联合查询返回map集合
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @author syuf
	 */
	public List<Map<String, String>> getAfterSaleChangeRepairProductList(
			String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT asdp.id,p.`name`,p.oriname,asdp.after_sale_order_code,asdp.`code`,asdp.IMEI,asbsp.create_datetime,"
				+ "asbsp.`status`,asbsp.return_datetime "
				+ "FROM product AS p "
				+ "INNER JOIN after_sale_detect_product AS asdp ON asdp.product_id = p.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "INNER JOIN after_sale_back_supplier_product AS asbsp ON asbsp.after_sale_detect_product_id = asdp.id "
				+ "INNER JOIN after_sale_back_supplier AS asbs ON asbs.id = asbsp.supplier_id "
				+ "INNER JOIN after_sale_stockin AS ass ON ass.after_sale_detect_product_id = asdp.id AND ass.product_id = asbsp.product_id AND ass.product_id = asdp.product_id "
				+ "WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", rs.getInt(1) + "");
				map.put("productName", rs.getString(2));
				map.put("productOriName", rs.getString(3));
				map.put("afterSaleCode", rs.getString(4));
				map.put("afterSaleDetectCode", rs.getString(5));
				map.put("imeiCode", rs.getString(6));
				map.put("createDatetime", rs.getString(7));
				map.put("statusName", AfterSaleBackSupplierProduct.statusMap
						.get(rs.getInt(8)));
				map.put("status", rs.getInt(8) + "");
				map.put("returnDatetime", rs.getString(9));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * @return 售后返厂以换代修商品 表联合查询返回map集合
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @author syuf
	 */
	public List<Map<String, String>> getAfterSaleBackSupplierAndProductList(
			String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT asbsp.id,asbsp.product_oriname,p.`name`,p.oriname,asdp.after_sale_order_code,asdp.`code`,asbsp.IMEI,asbsp.send_datetime,"
				+ "asbsp.`status`,asbsp.first_repair,ci.whole_code,ci.stock_type,asdl.content,asbs.`name`,asdp.id,c.name,asdp.create_datetime "
				+ "FROM product AS p "
				+ "INNER JOIN ("
				+ "SELECT t1.*, ("
				+ "SELECT t2.id "
				+ "FROM "
				+ "after_sale_detect_log t2 "
				+ "WHERE t2.after_sale_detect_product_id = t1.id and t2.after_sale_detect_type_id = "
				+ AfterSaleDetectTypeBean.REPORT_STATUS
				+ " ORDER BY t2.create_datetime desc LIMIT 0,1) log_id "
				+ "FROM after_sale_detect_product t1)AS asdp ON asdp.product_id = p.id "
				+ "LEFT JOIN after_sale_detect_log AS asdl ON asdl.after_sale_detect_product_id = asdp.id and asdp.log_id = asdl.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "INNER JOIN after_sale_back_supplier_product AS asbsp ON asbsp.after_sale_detect_product_id = asdp.id "
				+ "INNER JOIN after_sale_back_supplier AS asbs ON asbs.id = asbsp.supplier_id "
				+ "left JOIN after_sale_order AS aso ON aso.id = asdp.after_sale_order_id "
				+ "INNER JOIN catalog AS c ON p.parent_id1 = c.id "
				+ "WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", rs.getInt(1) + "");
				map.put("productModel", rs.getString(2));
				map.put("productName", rs.getString(3));
				map.put("productOriName", rs.getString(4));
				map.put("afterSaleCode", rs.getString(5));
				map.put("afterSaleDetectCode", rs.getString(6));
				map.put("imeiCode", rs.getString(7));
				map.put("sendDatetime", rs.getString(8));
				map.put("statusName", AfterSaleBackSupplierProduct.statusMap.get(rs.getInt(9)));
				map.put("status", rs.getInt(9) + "");
				if (rs.getInt(9) == 1) {
					map.put("repair", "是");
				} else {
					map.put("repair", "否");
				}
				map.put("wholeCode", rs.getString(11));
				if (rs.getInt(12) == CargoInfoBean.STOCKTYPE_CUSTOMER) {
					map.put("stockTypeName", "客户库");
				} else if (rs.getInt(12) == CargoInfoBean.STOCKTYPE_AFTER_SALE) {
					map.put("stockTypeName", "售后库");
				}
				
				map.put("supplierName", rs.getString(14));
				map.put("detectId", rs.getInt(15) + "");
				map.put("catalogName", rs.getString(16));
				map.put("createDateTime", rs.getString(17));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * @return 以换代修商品 表联合查询返回总记录数
	 * @param condition
	 * @author syuf
	 */
	public int getAfterSaleChangeRepairProductCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(asbsp.id) "
				+ "FROM product AS p "
				+ "INNER JOIN after_sale_detect_product AS asdp ON asdp.product_id = p.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "INNER JOIN after_sale_back_supplier_product AS asbsp ON asbsp.after_sale_detect_product_id = asdp.id "
				+ "INNER JOIN after_sale_back_supplier AS asbs ON asbs.id = asbsp.supplier_id "
				+ "INNER JOIN after_sale_stockin AS ass ON ass.after_sale_detect_product_id = asdp.id AND ass.product_id = asbsp.product_id AND ass.product_id = asdp.product_id "
				+ "WHERE  ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * @return 售后返厂以换代修商品 表联合查询返回总记录数
	 * @param condition
	 * @author syuf
	 */
	public int getAfterSaleBackSupplierAndProductCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(asbsp.id) "
				+ "FROM product AS p "
				+ "INNER JOIN ("
				+ "SELECT t1.*, ("
				+ "SELECT t2.id "
				+ "FROM "
				+ "after_sale_detect_log t2 "
				+ "WHERE "
				+ "t2.after_sale_detect_product_id = t1.id and t2.after_sale_detect_type_id = "
				+ AfterSaleDetectTypeBean.REPORT_STATUS
				+ " ORDER BY t2.create_datetime desc LIMIT 0,1) log_id "
				+ "FROM after_sale_detect_product t1)AS asdp ON asdp.product_id = p.id "
				+ "LEFT JOIN after_sale_detect_log AS asdl ON asdl.after_sale_detect_product_id = asdp.id and asdp.log_id = asdl.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "INNER JOIN after_sale_back_supplier_product AS asbsp ON asbsp.after_sale_detect_product_id = asdp.id "
				+ "INNER JOIN after_sale_back_supplier AS asbs ON asbs.id = asbsp.supplier_id "
				+ "left JOIN after_sale_order AS aso ON aso.id = asdp.after_sale_order_id "
				+ "WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * 
	 * @return 查询售后处理日志
	 * @param condition
	 *            orderBy
	 * @author syuf
	 */
	public String getAfterSaleDetectContentByDetect(String condition,
			String orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT asdl.content,asdl.content2,asdl.content3 "
				+ "FROM after_sale_detect_type AS asdt "
				+ "INNER JOIN after_sale_detect_log AS asdl ON asdl.after_sale_detect_type_id = asdt.id "
				+ "WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		String result = null;
		try {
			ResultSet rs = dbop.executeQuery(query);
			StringBuffer sb = new StringBuffer();
			if(rs.next()){
				if(!"".equals(rs.getString("content")) && rs.getString("content")!=null){
					sb.append(rs.getString("content"));
				}
				if(!"".equals(rs.getString("content2")) && rs.getString("content2")!=null){
					sb.append("/");
					sb.append(rs.getString("content2"));
				}
				if(!"".equals(rs.getString("content3")) && rs.getString("content3")!=null){
					sb.append("/");
					sb.append(rs.getString("content3"));
				}
			}
			result = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<AfterSaleBackSupplier> getAfterSaleBackSupplierList(
			String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_back_supplier",
				"mmb.stock.aftersale.AfterSaleBackSupplier");
	}

	/***
	 * 客户寄回包裹列表
	 * @return
	 */
	public List<AfterSaleDetectPackageBean> getDetectPackAgeListLeft(
			String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		
		String query = "SELECT asdp.id ,asdp.package_code,asdp.create_datetime,asdp.create_user_name,asdp.order_code," +
				"asdp.status,asdp.sender_name,asdp.sender_address,asdp.phone,asdp.remark  "
				+ "from after_sale_detect_package asdp " 
				+ "LEFT JOIN after_sale_detect_product AS asdpr ON asdpr.after_sale_detect_package_id = asdp.id" 
				+ " where " + condition + " group by asdp.id";
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleDetectPackageBean> list = new ArrayList<AfterSaleDetectPackageBean>();
		AfterSaleDetectPackageBean asdtdBean = null;
		try {
			
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asdtdBean = new AfterSaleDetectPackageBean();
				asdtdBean.setId(rs.getInt(1));
				asdtdBean.setPackageCode(rs.getString(2));
				asdtdBean.setCreateDatetime(rs.getString(3));
				asdtdBean.setCreateUserName(rs.getString(4));
				asdtdBean.setOrderCode(rs.getString(5));
				asdtdBean.setStatus(rs.getInt(6));
				asdtdBean.setSenderName(rs.getString(7));
				asdtdBean.setSenderAddress(rs.getString(8));
				if(rs.getString(9) != null && rs.getString(9).length() > 10){
					asdtdBean.setPhone(rs.getString(9).substring(0,3) + "****" + rs.getString(9).substring(7,10));
				}
				asdtdBean.setRemark(rs.getString(10));
				list.add(asdtdBean);
			}
			
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}
	/***
	 * 待检测包裹列表 hepeng
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AfterSaleDetectPackageBean> getDetectPackAgeList(
			String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();

		String query = "SELECT asdp.id ,asdp.package_code,asdp.create_datetime,asdp.create_user_name,asdp.order_code,asdp.status  "
				+ "from after_sale_detect_package asdp " 
				+ "INNER JOIN after_sale_detect_product AS asdpr ON asdpr.after_sale_detect_package_id = asdp.id" 
				+ " where " + condition + " group by asdp.id";
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleDetectPackageBean> list = new ArrayList<AfterSaleDetectPackageBean>();
		AfterSaleDetectPackageBean asdtdBean = null;
		try {

			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asdtdBean = new AfterSaleDetectPackageBean();
				asdtdBean.setId(rs.getInt(1));
				asdtdBean.setPackageCode(rs.getString(2));
				asdtdBean.setCreateDatetime(rs.getString(3));
				asdtdBean.setCreateUserName(rs.getString(4));
				asdtdBean.setOrderCode(rs.getString(5));
				asdtdBean.setStatus(rs.getInt(6));
				list.add(asdtdBean);
			}

			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/***
	 * 已检测商品上架任务列表 hepeng
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AfterSaleDetectUpShelfBean> getProductUpShelfList(
			String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ap.product_id,ap.after_sale_order_code,ap.code,ap.remark,ap.status,ap.create_datetime, bsp.status AS bspStatus ");
		sb.append(" FROM after_sale_detect_product AS ap INNER JOIN cargo_info AS cai ON ap.cargo_whole_code = cai.whole_code ");
		sb.append(" INNER JOIN product AS p ON p.id = ap.product_id ");
		sb.append(" LEFT JOIN after_sale_back_supplier_product AS bsp ON ap.id = bsp.after_sale_detect_product_id ");
		sb.append(" WHERE cai.store_type=2 and cai.stock_type = 10 ");

		// 此处理单状态为 过滤 寄回用户 和 寄回用户未妥投
		sb.append(" AND ap.status NOT IN (8, 9, 11, 15, 16, 17) ");

		if (null != condition && !condition.equals("")) {
			sb.append(condition);
		}
		if (orderBy != null) {
			sb.append(" order by " + orderBy);
		}
		String query = DbOperation.getPagingQuery(sb.toString(), index, count);
		List<AfterSaleDetectUpShelfBean> list = new ArrayList<AfterSaleDetectUpShelfBean>();
		AfterSaleDetectUpShelfBean asddsBean = null;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asddsBean = new AfterSaleDetectUpShelfBean();
				// 商品id
				asddsBean.setProductId(rs.getInt(1));
				asddsBean.setAfterSaleOrderCode(rs.getString(2));
				asddsBean.setAfterSaleCode(rs.getString(3));
				asddsBean.setAfterSaleOpinion(rs.getString(4));
				asddsBean.setAfterSaleStatus(AfterSaleStockin.afterstatusMap
						.get(Integer.parseInt(rs.getString(5))));
				asddsBean.setCheckTime(rs.getString(6));
				int bspStatus = -1;
				if (rs.getString("bspStatus") != null)
					bspStatus = StringUtil.toInt(rs.getString("bspStatus"));
				asddsBean
						.setBackSupplierStatus(bspStatus == AfterSaleBackSupplierProduct.STATUS4 ? "已返厂"
								: "");
				list.add(asddsBean);
			}

			rs.close();
			if (list.size() > 0) {
				for (AfterSaleDetectUpShelfBean afterSaleDetectPackageBean : list) {
					// 根据商品id查询对应的商品信息
					voProduct product = getProductById(
							afterSaleDetectPackageBean.getProductId(), dbop);
					if (product != null) {
						afterSaleDetectPackageBean.setProductCode(product
								.getCode());
						afterSaleDetectPackageBean.setShopName(product
								.getName());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/***
	 * 根据包裹id获取售后单号 返回售后单id的字符串 hepeng
	 */
	public String getAftersaleOrderIds(int id, DbOperation dbOp) {
		ResultSet resultSet = null;
		StringBuffer substr = new StringBuffer();
		String resultstr = "";
		try {

			resultSet = dbOp
					.executeQuery("select distinct after_sale_order_code from after_sale_detect_product where after_sale_detect_package_id="
							+ id + "");
			while (resultSet.next()) {

				substr.append(resultSet.getString("after_sale_order_code"))
						.append(",");

			}
			resultSet.close();
			if (substr.length() >= 2) {
				if (substr.indexOf(",") == 0) {
					resultstr = substr.substring(1, substr.length()).toString();
				} else {
					resultstr = substr.toString();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.release(dbOp);
		}

		return resultstr.length() >= 2 ? resultstr.substring(0,
				resultstr.length() - 1).toString() : "";
	}

	/***
	 * 获取等待返厂商品记录集(客户库商品下架使用)
	 * @param condition
	 * @author syuf
	 */
	public List<Map<String,String>> getWaitBackSupplierProductList(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = " SELECT DISTINCT asdp.`code`,p.`name`,ci.whole_code,ci.id " +
						" FROM after_sale_detect_product AS asdp " +
						" INNER JOIN after_sale_back_supplier_product AS asbsp ON asdp.id = asbsp.after_sale_detect_product_id AND asbsp.product_id = asdp.product_id " +
						" INNER JOIN cargo_info AS ci ON asdp.cargo_whole_code = ci.whole_code " +
						" INNER JOIN product AS p ON asdp.product_id = p.id " +
						" WHERE " + condition + 
						" ORDER BY ci.id ";
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				Map<String,String> map = new HashMap<String, String>();
				map.put("wholeCode", rs.getString(3));
				map.put("detectCode", rs.getString(1));
				map.put("productName", rs.getString(2));
				map.put("cargoId", rs.getInt(4) + "");
				list.add(map);
			}
			if(rs != null){
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	/***
	 * 获取等待返厂商品列表
	 * @param condition 查询条件
	 * @param index 分页索引
	 * @param count 索引数量
	 * @param orderBy 排序字符串
	 * @return 返厂商品列表与商品表与处理单表关联记录集
	 * @author syuf
	 */
	public List<AfterSaleBackSupplierProduct> getWaitBackSupplierProductList(String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT absp.id, absp.after_sale_detect_product_id,absp.product_id,absp.return_datetime,p.code productCode " +
				",p.name productName,asdp.cargo_whole_code,asdp.code,asdp.status,asdp.after_sale_order_code,asdp.create_user_name,asdp.create_datetime," +
				"asdp.after_sale_order_id,absp.status back_status " + 
				"FROM after_sale_back_supplier_product AS absp " +
				"INNER JOIN product AS p ON absp.product_id = p.id " +
				"INNER JOIN after_sale_detect_product AS asdp ON asdp.id = absp.after_sale_detect_product_id where 1=1 " + condition;
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleBackSupplierProduct> list = new ArrayList<AfterSaleBackSupplierProduct>();
		AfterSaleBackSupplierProduct asbpBean = null;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asbpBean = new AfterSaleBackSupplierProduct();
				asbpBean.setId(rs.getInt("id"));
				asbpBean.setReturnDatetime(rs.getString("return_datetime"));
				asbpBean.setAfterSaleDetectProductId(rs.getInt("after_sale_detect_product_id"));
				asbpBean.setProductId(rs.getInt("product_id"));
				asbpBean.setProductCode(rs.getString("productCode"));
				asbpBean.setShopName(rs.getString("productName"));
				asbpBean.setCargoWholeCode(rs.getString("cargo_whole_code"));
				asbpBean.setCode(rs.getString("code"));
				asbpBean.setHandlingstatus(AfterSaleStockin.afterstatusMap.get(rs.getInt("status")));
				asbpBean.setAfterSaleOrderCode(rs.getString("after_sale_order_code"));
				asbpBean.setUserName(rs.getString("create_user_name"));
				asbpBean.setCreateDatetime(rs.getString("create_datetime"));
				asbpBean.setAfterSaleOrderId(rs.getInt("after_sale_order_id"));
				asbpBean.setStatusName(AfterSaleBackSupplierProduct.statusMap.get(rs.getInt("back_status")));
				list.add(asbpBean);
			}
			if(rs != null){
				rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}
	/***
	 * 获取厂商寄回商品上架任务列表 hepeng
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AfterSaleBackSupplierProduct> getBackSupplierProductList(String condition, int index, int count, String orderBy,String status) {
		DbOperation dbop = this.getDbOp();
		int statu = 1;
		String query = "";
		if (status.equals("0")) {
			statu = 3;// 等待返厂 3
			query = "SELECT absp.id, absp.after_sale_detect_product_id,absp.product_id,absp.return_datetime,absp.create_datetime,absp.status " +
					"FROM after_sale_back_supplier_product AS absp " +
					"INNER JOIN product AS p ON absp.product_id = p.id " +
					"INNER JOIN after_sale_detect_product AS asdp ON asdp.id = absp.after_sale_detect_product_id where absp.status ="
					+ statu ;
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT absp.id ,absp.after_sale_detect_product_id,absp.product_id,absp.return_datetime ,al.content,absp.create_datetime,absp.status ");
			sb.append(" FROM after_sale_back_supplier_product AS absp, after_sale_detect_product AS ap, product AS p, after_sale_detect_log AS al ");
			sb.append(" where absp.product_id=p.id and al.after_sale_detect_type_id = 11 ");
			// 根据处理单状态过滤 寄回用户 和 寄回用户未妥投
			sb.append(" AND absp.after_sale_detect_product_id = ap.id AND ap.status NOT IN (8, 9, 11, 15, 16, 17) ");
			sb.append(" AND al.after_sale_detect_product_id=absp.after_sale_detect_product_id and absp.status =  ").append(statu).append(" ");
			query = sb.toString();
		}
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (statu != 3) {
			query += " group by absp.id";
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleBackSupplierProduct> list = new ArrayList<AfterSaleBackSupplierProduct>();
		AfterSaleBackSupplierProduct asbpBean = null;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asbpBean = new AfterSaleBackSupplierProduct();
				asbpBean.setId(rs.getInt("id"));
				if (rs.getString("return_datetime") == null) {
					asbpBean.setReturnDatetime("");
				} else {
					asbpBean.setReturnDatetime(rs.getString("return_datetime"));
				}
				int afterSaleDetectProductId = rs.getInt("after_sale_detect_product_id");
				asbpBean.setAfterSaleDetectProductId(afterSaleDetectProductId);
				asbpBean.setProductId(rs.getInt("product_id"));
				if (statu != 3) {
					if (rs.getString("content") == null) {
						asbpBean.setHandling("");
					} else {
						asbpBean.setHandling(rs.getString("content"));
					}
				}
				asbpBean.setCreateDatetime(rs.getString("create_datetime"));
				asbpBean.setStatus(rs.getInt("status"));
				list.add(asbpBean);
			}
			rs.close();
			for (AfterSaleBackSupplierProduct backSupplierProduct : list) {
				// 根据商品id查询对应的商品信息
				voProduct product = getProductById(backSupplierProduct.getProductId(), dbop);
				if (product != null) {
					backSupplierProduct.setProductCode(product.getCode());
					backSupplierProduct.setShopName(product.getName());
				}
				// 根据处理单id获取处理单信息
				AfterSaleDetectProductBean aftersp = getAfterSaleDetectProduct("id=" + backSupplierProduct.getAfterSaleDetectProductId());
				if (aftersp != null) {
					backSupplierProduct.setCargoWholeCode(aftersp.getCargoWholeCode());
					backSupplierProduct.setCode(aftersp.getCode());

					backSupplierProduct.setHandlingstatus(AfterSaleStockin.afterstatusMap.get(aftersp.getStatus()));
					backSupplierProduct.setAfterSaleOrderCode(aftersp.getAfterSaleOrderCode());
					backSupplierProduct.setUserName(aftersp.getCreateUserName());
					backSupplierProduct.setCreateDatetime(aftersp.getCreateDatetime());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/***
	 * 根据售后单号 或者 售后处理单号 获取处理单id hepeng
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public String getAfterSaleIds(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "select id from after_sale_detect_product  where 1=1  ";
		StringBuffer idstr = new StringBuffer();
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				idstr.append(rs.getInt("id")).append(",");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return idstr.length() >= 2 ? idstr.substring(0, idstr.length() - 1).toString() : "0";
	}

	/***
	 * 待再次检测商品列表 hepeng 8
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AfterSaleDetectProductBean> getDetectProductList(String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();

		String query = "select ap.id,ap.code,ap.after_sale_order_id,ap.after_sale_order_code,ap.cargo_whole_code,ap.product_id,ap.create_user_name,ap.create_datetime " +
				"from after_sale_detect_product ap, product p  where ap.status=1 and p.id=ap.product_id  ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleDetectProductBean> list = new ArrayList<AfterSaleDetectProductBean>();
		AfterSaleDetectProductBean afbpBean = null;

		try {

			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				afbpBean = new AfterSaleDetectProductBean();
				afbpBean.setId(rs.getInt("id"));
				afbpBean.setCargoWholeCode(rs.getString("cargo_whole_code"));
				afbpBean.setCode(rs.getString("code"));
				afbpBean.setProductId(rs.getInt("product_id"));
				afbpBean.setAfterSaleOrderId(rs.getInt("after_sale_order_id"));
				afbpBean.setAfterSaleOrderCode(rs.getString("after_sale_order_code"));
				afbpBean.setCreateUserName(rs.getString("create_user_name"));
				afbpBean.setCreateDatetime(rs.getString("create_datetime"));

				list.add(afbpBean);
			}
			rs.close();

			if (list != null && list.size() > 0) {

				for (AfterSaleDetectProductBean afterSaleBackSupplierProduct : list) {
					// 根据商品id查询对应的商品信息
					voProduct product = getProductById(afterSaleBackSupplierProduct.getProductId(), dbop);
					if (product != null) {
						afterSaleBackSupplierProduct.setProductCode(product.getCode());
						afterSaleBackSupplierProduct.setProductName(product.getName());
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/***
	 * 待寄回用户商品列表 hepeng
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AfterSaleBackUserProduct> getBackUserList(String condition,int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		// 查询寄回用户包裹商品 after_sale_back_user_product
		String query = "SELECT afbp.id ,afbp.after_sale_detect_product_id,afbp.product_id,asrnpr.spare_code " +
				"from after_sale_back_user_product afbp " +
				"join after_sale_detect_product ap on afbp.after_sale_detect_product_id=ap.id " +
				"join product p on afbp.product_id=p.id " + 
				"left join after_sale_replace_new_product_record asrnpr on ap.id=asrnpr.after_sale_detect_product_id " + 
				"where package_id=0";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleBackUserProduct> list = new ArrayList<AfterSaleBackUserProduct>();
		AfterSaleBackUserProduct asbpBean = null;

		try {

			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asbpBean = new AfterSaleBackUserProduct();
				asbpBean.setId(rs.getInt("id"));
				int afterSaleDetectProductId = rs.getInt("after_sale_detect_product_id");
				asbpBean.setAfterSaleDetectProductId(afterSaleDetectProductId);
				asbpBean.setProductId(rs.getInt("product_id"));
				asbpBean.setSpareCode(rs.getString("spare_code"));
				list.add(asbpBean);
			}
			rs.close();
			if (list != null && list.size() > 0) {
				for (AfterSaleBackUserProduct backUserProduct : list) {
					// 根据商品id查询对应的商品信息
					voProduct product = getProductById(backUserProduct.getProductId(), dbop);
					if (product != null) {
						backUserProduct.setProductCode(product.getCode());
						backUserProduct.setProductName(product.getName());
					}
					// 根据处理单id获取处理单信息
					AfterSaleDetectProductBean detectProduct = getAfterSaleDetectProduct(" id=" + backUserProduct.getAfterSaleDetectProductId());
					if (detectProduct != null) {
						backUserProduct.setCargoWholeCode(detectProduct.getCargoWholeCode());
						backUserProduct.setAfterSaleDetectProductCode(detectProduct.getCode());
						backUserProduct.setAfterSaleOrderCode(detectProduct.getAfterSaleOrderCode());
						backUserProduct.setUserName(detectProduct.getCreateUserName());
						backUserProduct.setCreateDatetime(detectProduct.getCreateDatetime());
						backUserProduct.setAfterSaleOrderId(detectProduct.getAfterSaleOrderId());
					}					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/***
	 * 售后库入库列表 hepeng
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AfterSaleStockin> getSaleStockList(String condition, int index,
			int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		// 查询售后入库单After_sale_stockin
		String query = "SELECT ats.id ,ats.after_sale_detect_product_id,ats.product_id,ats.out_cargo_whole_code,ats.in_cargo_whole_code,ats.create_datetime,ap.cargo_whole_code " +
				"from after_sale_stockin ats ,after_sale_detect_product ap,product p where 1=1 and ats.after_sale_detect_product_id=ap.id and p.id=ats.product_id and  ats.create_datetime is not  null  and ats.status=0 ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleStockin> list = new ArrayList<AfterSaleStockin>();
		AfterSaleStockin asskBean = null;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asskBean = new AfterSaleStockin();
				asskBean.setId(rs.getInt("id"));
				int afterSaleDetectProductId = rs.getInt("after_sale_detect_product_id");
				asskBean.setAfterSaleDetectProductId(afterSaleDetectProductId);
				asskBean.setProductId(rs.getInt("product_id"));
				if (rs.getString("out_cargo_whole_code") == null) {
					asskBean.setOutCargoWholeCode("");
				} else {
					asskBean.setOutCargoWholeCode(rs.getString("out_cargo_whole_code"));
				}
				if (rs.getString("in_cargo_whole_code") == null) {
					asskBean.setInCargoWholeCode("");
				} else {
					asskBean.setInCargoWholeCode(rs.getString("in_cargo_whole_code"));
				}
				asskBean.setCreateDatetime(rs.getString("create_datetime"));
				asskBean.setDetectCargoWholeCode(rs.getString("cargo_whole_code"));
				list.add(asskBean);
			}
			rs.close();

			if (list != null && list.size() > 0) {

				for (AfterSaleStockin afterSaleStockin : list) {
					// 根据商品id查询对应的商品信息
					voProduct product = getProductById(afterSaleStockin.getProductId(), dbop);
					if (product != null) {
						afterSaleStockin.setProductCode(product.getCode());
						afterSaleStockin.setShopName(product.getName());
					}
					// 根据处理单id获取处理单信息
					AfterSaleDetectProductBean aftersp = getAfterSaleDetectProduct("id=" + afterSaleStockin.getAfterSaleDetectProductId());
					if (aftersp != null) {
						afterSaleStockin.setCode(aftersp.getCode());
						afterSaleStockin.setHandlingstatus(AfterSaleStockin.afterstatusMap.get(aftersp.getStatus()));
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/***
	 * 售后库上架列表 hepeng
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<AfterSaleStockin> getSaleUpshelfList(String condition,
			int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		// 查询售后处理单after_sale_detect_product_upshelf 

		String query = "SELECT ap.id,ap.product_id,ats.out_cargo_whole_code,ats.in_cargo_whole_code  " +
				"from after_sale_detect_product ap,cargo_info cai,after_sale_stockin ats  " +
				"where ap.cargo_whole_code=cai.whole_code  and cai.store_type=2  and ats.after_sale_detect_product_id=ap.id and cai.stock_type in (9)   ";

		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		query += " group by ap.id ";
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleStockin> list = new ArrayList<AfterSaleStockin>();
		AfterSaleStockin asskBean = null;

		try {

			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				asskBean = new AfterSaleStockin();
				asskBean.setId(rs.getInt("ap.id"));
				int afterSaleDetectProductId = rs.getInt("ap.id");
				if (rs.getString("out_cargo_whole_code") == null) {
					asskBean.setOutCargoWholeCode("");
				} else {
					asskBean.setOutCargoWholeCode(rs.getString("out_cargo_whole_code"));
				}
				if (rs.getString("in_cargo_whole_code") == null) {
					asskBean.setInCargoWholeCode(rs.getString("in_cargo_whole_code"));
				} else {
					asskBean.setInCargoWholeCode(rs.getString("in_cargo_whole_code"));
				}

				asskBean.setAfterSaleDetectProductId(afterSaleDetectProductId);
				asskBean.setProductId(rs.getInt("product_id"));
				list.add(asskBean);
			}
			rs.close();

			if (list != null && list.size() > 0) {

				for (AfterSaleStockin afterSaleStockin : list) {
					// 根据商品id查询对应的商品信息
					voProduct product = getProductById(
							afterSaleStockin.getProductId(), dbop);
					if (product != null) {
						afterSaleStockin.setProductCode(product.getCode());
						afterSaleStockin.setShopName(product.getName());
					}
					// 根据处理单id获取处理单信息
					AfterSaleDetectProductBean aftersp = getAfterSaleDetectProduct("id=" + afterSaleStockin.getAfterSaleDetectProductId());
					if (aftersp != null) {
						afterSaleStockin.setCode(aftersp.getCode());
						afterSaleStockin.setHandlingstatus(AfterSaleStockin.afterstatusMap.get(aftersp.getStatus()));
						afterSaleStockin.setCreateDatetime(aftersp.getCreateDatetime());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * hepeng 根据商品id获取商品信息
	 * 
	 * @param id
	 * @return
	 */
	public voProduct getProductById(int id, DbOperation dbOp) {
		voProduct product = null;
		ResultSet resultSet = null;
		try {

			resultSet = dbOp.executeQuery("select id,code,name,oriname,parent_id1 from product where id="
							+ id + "");
			while (resultSet.next()) {
				product = new voProduct();
				product.setId(resultSet.getInt("id"));
				product.setCode(resultSet.getString("code"));
				product.setName(resultSet.getString("name"));
				product.setOriname(resultSet.getString("oriname"));
				product.setParentId1(resultSet.getInt("parent_id1"));
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.release(dbOp);
		}

		return product;
	}

	/**
	 * @return 售后商品 表联合查询返回总记录数
	 * @param condition
	 * @author syuf
	 */
	public int getAfterSaleProductCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(asdp.id) "
				+ "FROM ("
				+ "SELECT t1.*, ("
				+ "SELECT t2.id "
				+ "FROM "
				+ "after_sale_detect_log t2 "
				+ "WHERE "
				+ "t2.after_sale_detect_product_id = t1.id and t2.after_sale_detect_type_id = "
				+ AfterSaleDetectTypeBean.MAIN_PRODUCT_STATUS
				+ " ORDER BY t2.create_datetime desc LIMIT 0,1) log_id "
				+ "FROM after_sale_detect_product t1) AS asdp  "
				+ "LEFT JOIN after_sale_detect_log AS asdl ON asdl.after_sale_detect_product_id = asdp.id and asdp.log_id = asdl.id "
				+ "left JOIN after_sale_order AS aso ON asdp.after_sale_order_id = aso.id "
				+ "INNER JOIN product AS p ON asdp.product_id = p.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "left JOIN after_sale_stockin AS ass ON asdp.id = ass.after_sale_detect_product_id "
				+ " left join product_sell_property psp on asdp.product_id=psp.product_id "
				+ "WHERE  ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * @return 售后商品 表联合查询返回map集合
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @author syuf
	 */
	public List<Map<String, String>> getAfterSaleProductList(String condition,int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT asdp.id,p.oriname,p.`code`,p.`name`,asdp.cargo_whole_code,asdp.`code`,asdp.`status`,"
				+ "aso.after_sale_order_code,asdp.create_datetime,ass.type type,asdl.content,psp.type sellType "
				+ "FROM ("
				+ "SELECT t1.*, ("
				+ "SELECT t2.id "
				+ "FROM "
				+ "after_sale_detect_log t2 "
				+ "WHERE "
				+ "t2.after_sale_detect_product_id = t1.id and t2.after_sale_detect_type_id = "
				+ AfterSaleDetectTypeBean.MAIN_PRODUCT_STATUS
				+ " ORDER BY t2.create_datetime desc LIMIT 0,1) log_id "
				+ "FROM after_sale_detect_product t1) AS asdp  "
				+ "LEFT JOIN after_sale_detect_log AS asdl ON asdl.after_sale_detect_product_id = asdp.id and asdp.log_id = asdl.id "
				+ "left JOIN after_sale_order AS aso ON asdp.after_sale_order_id = aso.id "
				+ "INNER JOIN product AS p ON asdp.product_id = p.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "left JOIN after_sale_stockin AS ass ON asdp.id = ass.after_sale_detect_product_id "
				+ " left join product_sell_property psp on asdp.product_id=psp.product_id "
				+ "WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", rs.getInt(1) + "");
				map.put("productOriName", rs.getString(2));
				map.put("productCode", rs.getString(3));
				map.put("productName", rs.getString(4));
				map.put("wholeCode", rs.getString(5));
				map.put("afterSaleDetectCode", rs.getString(6));
				map.put("statusName",AfterSaleDetectProductBean.statusMap.get(rs.getInt(7)));
				map.put("afterSaleCode", rs.getString(8));
				map.put("createDatetime", rs.getString(9));
				map.put("afterSaleStockinTypeName",AfterSaleStockin.typeMap.get(rs.getInt(10)));
				map.put("mainProductStatus", rs.getString(11));
				map.put("sellTypeName",ProductSellPropertyBean.typeMap.get(rs.getInt(12)));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * @return 售后商品 表联合查询返回总记录数
	 * @param condition
	 * @author zy
	 */
	public int getCustomerAfterSaleProductCount(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT count(asdp.id) "
				+ "FROM after_sale_detect_product asdp "
				+ "INNER JOIN after_sale_order AS aso ON asdp.after_sale_order_id = aso.id "
				+ "INNER JOIN product AS p ON asdp.product_id = p.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "inner join product_sell_property as psp on psp.product_id=p.id "
				+ "WHERE  ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * @return 售后商品 表联合查询返回map集合
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @author zy
	 */
	public List<Map<String, String>> getCustomerAfterSaleProductList(String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT asdp.id,p.oriname,p.`code`,p.`name`,asdp.cargo_whole_code,asdp.`code`,asdp.`status`, "
				+ "aso.after_sale_order_code,asdp.create_datetime "
				+ "FROM after_sale_detect_product asdp  "
				+ "INNER JOIN after_sale_order AS aso ON asdp.after_sale_order_id = aso.id "
				+ "INNER JOIN product AS p ON asdp.product_id = p.id "
				+ "INNER JOIN cargo_info AS ci ON ci.whole_code = asdp.cargo_whole_code "
				+ "inner join product_sell_property as psp on psp.product_id=p.id "
				+ "WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", rs.getInt(1) + "");
				map.put("productOriName", rs.getString(2));
				map.put("productCode", rs.getString(3));
				map.put("productName", rs.getString(4));
				map.put("wholeCode", rs.getString(5));
				map.put("afterSaleDetectCode", rs.getString(6));
				map.put("statusName",AfterSaleDetectProductBean.statusMap.get(rs.getInt(7)));
				map.put("afterSaleCode", rs.getString(8));
				map.put("createDatetime", rs.getString(9));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * @return 售后处理单信息 表联合查询返回list
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @author zhangxiaolei
	 */
	public List<Map<String, String>> getAfterSaleDetectProductData(String condition, int index, int count, String orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = " SELECT b.id,a.id,a.code,e.whole_code,e.stock_type,c.name,c.code,a.IMEI,b.after_sale_order_code,a.status "
				+ " FROM after_sale_detect_product a "
				+ " JOIN after_sale_order b ON a.after_sale_order_id = b.id"
				+ " JOIN cargo_info e ON a.cargo_whole_code = e.whole_code"
				+ " JOIN product c ON a.product_id = c.id" + " WHERE";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List list = new ArrayList();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				AfterSaleDetectProductBean bean = new AfterSaleDetectProductBean();
				bean.setAfterSaleOrderId(rs.getInt("b.id"));// 售后处理单号
				bean.setId(rs.getInt("a.id"));// 售后处理单号
				bean.setCode(rs.getString("a.code"));// 售后处理单号
				bean.setCargoWholeCode(rs.getString("e.whole_code"));// 货位编号
				// bean.setStockType(rs.getInt("e.stock_type"));// 库类型
				bean.setStockTypeName(ProductStockBean.stockTypeMap.get(rs
						.getInt("e.stock_type")));
				bean.setProductName(rs.getString("c.name"));// 商品名称
				bean.setProductCode(rs.getString("c.code"));// 商品编号
				bean.setAfterSaleOrderCode(rs
						.getString("b.after_sale_order_code"));// 售后单号
				bean.setIMEI(rs.getString("a.IMEI"));// IMEI
				// bean.setStatus(rs.getInt("a.status"));// 处理单状态
				bean.setStatusName(AfterSaleDetectProductBean.statusMap.get(rs
						.getInt("a.status")));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	/**
	 * @return 售后处理单信息 表联合查询返回记录数
	 * @param condition
	 * @author zhangxiaolei
	 */
	public int getAfterSaleDetectProductCounts(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = " SELECT count(a.id) "
				+ " FROM after_sale_detect_product a "
				+ " JOIN after_sale_order b ON a.after_sale_order_id = b.id"
				+ " JOIN cargo_info e ON a.cargo_whole_code = e.whole_code"
				+ " JOIN product c ON a.product_id = c.id" + " WHERE";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return count;
	}

	/**
	 * @return 售后处理单详细信息 返回JSON
	 * @author zhangxiaolei
	 */
	public EasyuiDataGridJson getAfterSaleDetectProductDetail(String id) {
		DbOperation dbop = this.getDbOp();
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		String query = " SELECT b.order_id,a.after_sale_order_id,a.after_sale_detect_package_id, f.status,a.id,a.code,d.parent_id1,b.order_code,c.whole_code,c.stock_type,d.name,d.code,a.IMEI,e.create_datetime,e.user_name,"
				+ " b.after_sale_order_code,a.status,asbsp.status,asbsp.id "
				+ " FROM after_sale_detect_product a "
				+ " JOIN after_sale_order b ON a.after_sale_order_id = b.id"
				+ " JOIN cargo_info c ON a.cargo_whole_code = c.whole_code"
				+ " JOIN product d ON a.product_id = d.id"
				+ " LEFT JOIN after_sale_stockin f ON a.id=f.after_sale_detect_product_id"
				+ " LEFT JOIN after_sale_detect_log e ON e.after_sale_detect_product_id =a.id "
				+ " LEFT JOIN after_sale_back_supplier_product AS asbsp ON asbsp.after_sale_detect_product_id = a.id"
				+ " WHERE a.id=" + id;
		try {
			ResultSet rs = dbop.executeQuery(query);
			List list = new ArrayList();
			HashMap map = new HashMap();
			while (rs.next()) {

				map.put("afterSaleDetectProductId", rs.getString("a.id"));// 售后处理单ID
				map.put("afterSaleOrderId",rs.getString("a.after_sale_order_id"));
				map.put("afterSaleDetectPackageId",rs.getString("a.after_sale_detect_package_id"));
				map.put("parent_id1", rs.getString("d.parent_id1"));// 产品线
				map.put("afterSaleDetectProductCode", rs.getString("a.code"));// 售后处理单号
				map.put("afterSaleOrderCode",rs.getString("b.after_sale_order_code"));// 售后单号
				map.put("orderCode", rs.getString("b.order_code") + "");// 订单号
				map.put("orderId", rs.getInt("b.order_id"));// 订单ID
				map.put("detectProductStatus", rs.getInt("a.status"));
				map.put("AfterSaleDetectProductStatusName",AfterSaleDetectProductBean.statusMap.get(rs.getInt("a.status")));// 售后处理单状态
				map.put("productCode", rs.getString("d.code") + "");// 商品编号
				map.put("productName", rs.getString("d.name") + "");// 商品名称
				// map.put("productCode", rs.getString("c.code")+"");//出库状态
				map.put("statusIn", AfterSaleStockin.statusMap.get(rs.getString("f.status")));// 入库状态
				map.put("CargoWholeCode", rs.getString("c.whole_code"));// 货位号
				map.put("afterSaleDetectLogCreateTime",rs.getString("e.create_datetime"));// 检测时间
				map.put("afterSaleDetectLogUserName",rs.getString("e.user_name"));// 检测人
				map.put("backSupplierProductStatus", rs.getInt("asbsp.status")+ "");// 返厂商品状态
				map.put("backSupplierProductId", rs.getInt("asbsp.id") + "");// 返厂商品状态
			}
			list.add(map);
			j.setFooter(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return j;
	}

	/**
	 * @return 售后处理单详细信-多个息检测记录 返回String
	 * @author zhangxiaolei
	 */
	public String getAfterSaleDetectLogDetails(String id) {
		DbOperation dbop = this.getDbOp();
		String queryTime = " SELECT f.create_datetime "
				+ " FROM after_sale_detect_product a "
				+ " JOIN after_sale_detect_log f on f.after_sale_detect_product_id=a.id"
				+ " WHERE a.id=" + id + " group by f.create_datetime";
		StringBuffer tableBuf = new StringBuffer();
		StringBuffer baojiaBuf = new StringBuffer();
		String newStr = null;
		try {
			ResultSet rsTime = dbop.executeQuery(queryTime);
			List timeList = new ArrayList();
			while (rsTime.next()) {
				timeList.add(rsTime.getString("f.create_datetime"));
			}
			if (timeList != null) {
				for (int i = 0; i < timeList.size(); i++) {
					String query = " SELECT g.id,f.content,f.content2,f.content3,"
							+ " a.status,a.IMEI,f.create_datetime,f.user_name "
							+ " FROM after_sale_detect_product a "
							+ " JOIN after_sale_detect_log f on f.after_sale_detect_product_id=a.id"
							+ " JOIN after_sale_detect_type g on f.after_sale_detect_type_id=g.id"
							+ " WHERE a.id=" + id + " and f.create_datetime='"
							+ timeList.get(i) + "'";
					ResultSet rs = dbop.executeQuery(query);
					HashMap map = new HashMap();
					String[] bjx = null;// 报价项
					String[] csbj = null;// 厂商报价
					String[] intactFittings = null;//完好配件
					String[] badFittings = null;//损坏配件
					String createDate = new String();// 检测信息创建时间
					String createUser = new String();// 检测信息创建人
					String IMEI = new String();
					while (rs.next()) {
						createDate = rs.getString("f.create_datetime");
						createUser = rs.getString("f.user_name");
						IMEI = StringUtil.convertNull(rs.getString("a.IMEI"));
						String content = rs.getString("f.content");
						String content2 = rs.getString("f.content2");
						String content3 = rs.getString("f.content3");
						if(null != content && !"".equals(content) && null != content2 && !"".equals(content2)){
							content += "/"+content2;
							if(null != content3 && !"".equals(content3)){
								content += "/"+content3;
							}
						}
						String key = rs.getInt("g.id") + "";
						map.put(key, content);
						if (rs.getInt("g.id") == AfterSaleDetectTypeBean.QUOTE_ITEM) {// 报价项
							bjx = rs.getString("f.content").split("\n");
						}
						if (rs.getInt("g.id") == AfterSaleDetectTypeBean.SUPPLIER_PRICE) {// 厂商报价
							csbj = rs.getString("f.content").split("\n");
						}
						if(rs.getInt("g.id") == AfterSaleDetectTypeBean.INTACT_FITTING){//完好配件
							intactFittings = rs.getString("f.content").split("\n");
						} 
						if(rs.getInt("g.id") == AfterSaleDetectTypeBean.BAD_FITTING){//完好配件
							badFittings = rs.getString("f.content").split("\n");
						} 
					}
					rs.close();
					StringBuffer bjxBuf = new StringBuffer();// 报价项
					StringBuffer csbjBuf = new StringBuffer();// 厂商报价
					StringBuffer intactFittingBuf = new StringBuffer();// 完好配件
					StringBuffer badFittingBuf = new StringBuffer();// 损坏配件
					if (bjx != null) {
						for (int k = 0; k < bjx.length; k++) {
							bjxBuf.append("<font>" + bjx[k] + "</font><br>");
						}
					}
					if (csbj != null) {
						for (int m = 0; m < csbj.length; m++) {
							csbjBuf.append("<font>" + csbj[m] + "</font><br>");
						}
					}
					if (intactFittings != null) {
						for (int m = 0; m < intactFittings.length; m++) {
							intactFittingBuf.append("<font>" + intactFittings[m] + "</font><br>");
						}
					}
					if (badFittings != null) {
						for (int m = 0; m < intactFittings.length; m++) {
							badFittingBuf.append("<font>" + badFittings[m] + "</font><br>");
						}
					}
					
					if (map.get("12") == null) {
						tableBuf.append("<font size=\"4\" color=\"blue\"><strong>检测记录</strong></font>");
						tableBuf.append("<h4 id=\"afterSaleDetectLogCreateTime\" name=\"afterSaleDetectLogCreateTime\">检测时间:"
								+ StringUtil.cutString(createDate, 0, 19)
								+ "</h4>");
						tableBuf.append("<h4 id=\"afterSaleDetectLogUserName\" name=\"afterSaleDetectLogUserName\">检测人:"
								+ createUser + "</h4>");
						tableBuf.append("<table  width=\"99%\" border=\"1\" cellspacing=\"0\" bordercolor=\"#00000\">");
						tableBuf.append("<tr align=\"left\" >");
						tableBuf.append("<td width=\"25%\"><h4>用户描述："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.QUESTION_DESCRIPTION
												+ "")) + "</h4></td>");
						tableBuf.append("<td width=\"25%\"><h4>包装："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.DAMAGED
												+ "")) + "</h4></td>");
						tableBuf.append("<td width=\"25%\"><h4>赠品："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.GIFT_ALL
												+ "")) + "</h4></td>");
						tableBuf.append("<td width=\"25%\"><h4>故障描述："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.FAULT_DESCRIPTION
												+ "")) + "</h4></td>");
						tableBuf.append("</tr>");
						tableBuf.append("<tr align=\"left\" >");
						tableBuf.append("<td width=\"25%\"><h4>申报状态："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.REPORT_STATUS
												+ "")) + "</h4></td>");
						tableBuf.append("<td width=\"25%\"><h4>故障代码："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.FAULT_CODE
												+ "")) + "</h4></td>");
						tableBuf.append("<td width=\"25%\"><h4>IMEI："
								+ IMEI
								+ "</h4></td>");
						tableBuf.append("<td width=\"25%\"><h4>检测异常原因："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.EXCEPTION_REASON
												+ "")) + "</h4></td>");
						tableBuf.append("</tr>");
						tableBuf.append("<tr align=\"left\" >");
						tableBuf.append("<td width=\"25%\" colspan=\"4\"><h4>报价项及报价：<br>"
								+ bjxBuf + "</h4></td>");
						tableBuf.append("</tr>");
						tableBuf.append("<tr align=\"left\" >");
						tableBuf.append("<td width=\"25%\" colspan=\"2\"><h4>完好配件：<br>"
								+ intactFittingBuf + "</h4></td>");
						tableBuf.append("<td width=\"25%\" colspan=\"2\"><h4>损坏配件：<br>"
								+ badFittingBuf + "</h4></td>");
						tableBuf.append("</tr>");
						tableBuf.append("<tr align=\"left\" >");
						tableBuf.append("<td width=\"25%\" colspan=\"4\"><h4>备注："
								+ StringUtil.convertNull((String) map
										.get(AfterSaleDetectTypeBean.REMARK+""))
								+ "</h4></td>");
						tableBuf.append("</tr>");
						tableBuf.append("</table>");
					} else if (map.get("12") != null) {
						baojiaBuf.append("<br/>");
						baojiaBuf
								.append("<font size=\"4\" color=\"blue\"><strong>维修厂家报价</strong></font>");
						baojiaBuf
								.append("<table  width=\"99%\" border=\"1\" cellspacing=\"0\" bordercolor=\"#00000\">");
						baojiaBuf
								.append("<tr><td width=\"25%\"><h4 id=\"time\" name=\"time\">填写时间:"
										+ StringUtil.cutString(createDate, 0,
												19) + "</h4></td></tr>");
						baojiaBuf
								.append("<tr align=\"left\" ><td width=\"25%\"><font><strong>报价项:</strong></font><br><h4 id=\"baojiaxiang\" name=\"baojiaxiang\">"
										+ csbjBuf + "</h4></td></tr>");
						baojiaBuf.append("</table>");
						baojiaBuf.append("<br/>");
					}
				}
			}
			newStr = new String(tableBuf.append(baojiaBuf).toString().getBytes(), "UTF-8");  
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}

		return newStr;
	}

	/**
	 * @return 售后处理单详细信息检测记录 返回JSON
	 * @author zhangxiaolei
	 */
	public EasyuiDataGridJson getAfterSaleDetectLogDetail(String id) {
		DbOperation dbop = this.getDbOp();
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		String query = " SELECT g.id,f.content,"
				+ " a.status,f.create_datetime,f.user_name "
				+ " FROM after_sale_detect_product a "
				+ " JOIN after_sale_detect_log f on f.after_sale_detect_product_id=a.id"
				+ " JOIN after_sale_detect_type g on f.after_sale_detect_type_id=g.id"
				+ " WHERE a.id=" + id;
		try {
			ResultSet rs = dbop.executeQuery(query);
			List list = new ArrayList();
			HashMap map = new HashMap();
			String[] bjx = null;// 报价项
			String[] csbj = null;// 厂商报价
			while (rs.next()) {
				if (rs.getInt("g.id") == AfterSaleDetectTypeBean.QUOTE_ITEM) {// 报价项
					bjx = rs.getString("f.content").split("\n");
				}
				if (rs.getInt("g.id") == AfterSaleDetectTypeBean.SUPPLIER_PRICE) {// 厂商报价
					csbj = rs.getString("f.content").split("\n");
				}
				map.put("time", rs.getString("f.create_datetime"));
				map.put("type" + rs.getInt("g.id"), rs.getString("f.content"));
			}
			StringBuffer sbbjx = new StringBuffer();
			StringBuffer sbcsbj = new StringBuffer();
			if (bjx != null) {
				for (int i = 0; i < bjx.length; i++) {
					sbbjx.append("<font>" + bjx[i] + "</font><br>");
				}
				map.put("bjx", sbbjx);
			}
			if (csbj != null) {
				for (int i = 0; i < bjx.length; i++) {
					sbcsbj.append("<font>" + csbj[i] + "</font><br>");
				}
				map.put("csbj", sbcsbj);
			}
			list.add(map);
			j.setFooter(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return j;
	}

	public boolean addAfterSaleWarehourcePackage(AfterSaleWarehourcePackageListBean b) {
		return addXXX(b, "after_sale_warehource_package_list");
	}

	/**
	 * @return 寄回包裹信息 返回JSON
	 * @author zhangxiaolei
	 */
	public EasyuiDataGridJson getAfterSaleDetectPackageInfo(String id) {
		DbOperation dbop = this.getDbOp();
		EasyuiDataGridJson j = new EasyuiDataGridJson();
		String query = " SELECT c.content,b.*,a.package_id,a.product_id,d.after_sale_order_id"
				+ " FROM after_sale_back_user_product a "
				+ " LEFT JOIN after_sale_back_user_package b on b.id=a.package_id"
				+ " LEFT JOIN sys_dict c on c.id=b.deliver_id"
				+ " LEFT JOIN after_sale_detect_product d ON a.after_sale_detect_product_id = d.id"
				+ " WHERE a.after_sale_detect_product_id=" + id;

		try {
			ResultSet rs = dbop.executeQuery(query);
			List list = new ArrayList();
			HashMap map = new HashMap();
			while (rs.next()) {
				map.put("deliverName", rs.getString("c.content"));// 快递名称
				map.put("packageCode", rs.getString("b.package_code"));// 快递单号
				map.put("freight", rs.getString("b.price"));// 运费金额
				map.put("senderName", rs.getString("b.user_name"));// 包裹发货人
				map.put("createDatetime", rs.getString("b.create_datetime"));// 包裹发货日期
				map.put("remark", rs.getString("b.remark"));// 备注
				map.put("packageId", rs.getInt("package_id"));//寄回包裹id
				map.put("productId", rs.getInt("product_id"));//商品id
				map.put("afterSaleOrderId", rs.getInt("after_sale_order_id"));
			}
			list.add(map);
			j.setFooter(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return j;
	}
	
	/**
	 * @return 发送短信，获取寄回用户包裹相关信息
	 * @author ahc
	 */
	public AfterSaleBackUserPackage getAfterSaleBackUserPackageInfo(String id) {
		DbOperation dbop = this.getDbOp();
		AfterSaleBackUserPackage asup = new AfterSaleBackUserPackage();
		String query = " SELECT c.content,b.*,a.package_id,a.product_id,d.after_sale_order_id,e.order_id"
				+ " FROM after_sale_back_user_product a "
				+ " LEFT JOIN after_sale_back_user_package b on b.id=a.package_id"
				+ " LEFT JOIN sys_dict c on c.id=b.deliver_id"
				+ " LEFT JOIN after_sale_detect_product d ON a.after_sale_detect_product_id = d.id"
				+ " LEFT JOIN after_sale_order e ON d.after_sale_order_id = e.id"
				+ " WHERE a.after_sale_detect_product_id=" + id;
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				asup.setProductId(rs.getInt("product_id"));
				asup.setAfterSaleOrderId(rs.getInt("after_sale_order_id"));
				asup.setPackageCode(rs.getString("b.package_code"));
				asup.setDeliverId(rs.getInt("deliver_id"));
				asup.setUserPhone(rs.getString("user_phone"));
				asup.setOrderId(rs.getInt("order_id"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return asup;
	}

	@SuppressWarnings("unchecked")
	public List<AfterSaleDetectProductOrder> getAfterSaleDetectProductOrderList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,"after_sale_detect_product_order","mmb.stock.aftersale.AfterSaleDetectProductOrder");
	}

	public AfterSaleOperationListBean getAfterSaleOperationList(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = " SELECT id,code "
				+ " FROM after_sale_operation_list WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		AfterSaleOperationListBean bean = null;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				bean = new AfterSaleOperationListBean();
				bean.setId(rs.getInt(1));
				bean.setCODE(rs.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return bean;
	}

	public boolean updateAfterSaleOperationList(String set, String condition) {
		return updateXXX(set, condition, "after_sale_operation_list");
	}

	public AfterSaleOperationWsproductRelationalBean getAfterSaleOperationWsproductRelational(String condition) {
		return (AfterSaleOperationWsproductRelationalBean) getXXX(condition,"after_sale_operation_wsproduct_relational","mmb.stock.aftersale.AfterSaleOperationWsproductRelationalBean");
	}

	public AfterSaleWarehourceProductRecordsBean getAfterSaleWarehourceProductRecord(String condition) {
		return (AfterSaleWarehourceProductRecordsBean) getXXX(condition,"after_sale_warehource_product_records","mmb.stock.aftersale.AfterSaleWarehourceProductRecordsBean");
	}

	public boolean updateAfterSaleWarehourceProductRecords(String set,String condition) {
		return updateXXX(set, condition,"after_sale_warehource_product_records");
	}

	public AfterSaleDetectProductOrder getAfterSaleDetectProductOrder(String condition) {
		return (AfterSaleDetectProductOrder) getXXX(condition,"after_sale_detect_product_order","mmb.stock.aftersale.AfterSaleDetectProductOrder");
	}

	public List<AfterSaleWarehourceProductRecordsBean> getAfterSaleWarehourceProductRecordsAndOperationList(
			String condition, int index, int count, Object orderBy) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT DISTINCT aswpr.id  "
				+ "FROM after_sale_operation_wsproduct_relational "
				+ "INNER JOIN after_sale_operation_list AS asol ON asol.id = after_sale_operation_wsproduct_relational.after_sale_operation_id "
				+ "INNER JOIN after_sale_warehource_product_records AS aswpr ON aswpr.id = after_sale_operation_wsproduct_relational.after_sale_wsproduct_record_id "
				+ "WHERE ";
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<AfterSaleWarehourceProductRecordsBean> list = new ArrayList<AfterSaleWarehourceProductRecordsBean>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				AfterSaleWarehourceProductRecordsBean bean = new AfterSaleWarehourceProductRecordsBean();
				bean.setId(rs.getInt(1));
				list.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return list;
	}

	public boolean addAfterSaleBsbyProduct(AfterSaleBsbyProduct bean) {
		return addXXX(bean, "after_sale_bsby_product");
	}

	public List getAfterSaleBsbyProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_bsby_product",
				"mmb.stock.aftersale.AfterSaleBsbyProduct");
	}

	public int getAfterSaleBsbyProductCount(String condition) {
		return getXXXCount(condition, "after_sale_bsby_product", "id");
	}

	public AfterSaleBsbyProduct getAfterSaleBsbyProduct(String condition) {
		return (AfterSaleBsbyProduct) getXXX(condition,
				"after_sale_bsby_product",
				"mmb.stock.aftersale.AfterSaleBsbyProduct");
	}

	public boolean updateAfterSaleBsbyProduct(String set, String condition) {
		return updateXXX(set, condition, "after_sale_bsby_product");
	}

	public boolean deleteAfterSaleBsbyProduct(String condition) {
		return deleteXXX(condition, "after_sale_bsby_product");
	}

	public boolean addAfterSaleInventory(AfterSaleInventory bean) {
		return addXXX(bean, "after_sale_inventory");
	}

	public List getAfterSaleInventoryList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_inventory",
				"mmb.stock.aftersale.AfterSaleInventory");
	}

	public int getAfterSaleInventoryCount(String condition) {
		return getXXXCount(condition, "after_sale_inventory", "id");
	}

	public AfterSaleInventory getAfterSaleInventory(String condition) {
		return (AfterSaleInventory) getXXX(condition, "after_sale_inventory",
				"mmb.stock.aftersale.AfterSaleInventory");
	}

	public boolean updateAfterSaleInventory(String set, String condition) {
		return updateXXX(set, condition, "after_sale_inventory");
	}

	public boolean deleteAfterSaleInventory(String condition) {
		return deleteXXX(condition, "after_sale_inventory");
	}

	public boolean addAfterSaleInventoryProduct(AfterSaleInventoryProduct bean) {
		return addXXX(bean, "after_sale_inventory_product");
	}

	public List getAfterSaleInventoryProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_inventory_product",
				"mmb.stock.aftersale.AfterSaleInventoryProduct");
	}

	public int getAfterSaleInventoryProductCount(String condition) {
		return getXXXCount(condition, "after_sale_inventory_product", "id");
	}

	public AfterSaleInventoryProduct getAfterSaleInventoryProduct(
			String condition) {
		return (AfterSaleInventoryProduct) getXXX(condition,
				"after_sale_inventory_product",
				"mmb.stock.aftersale.AfterSaleInventoryProduct");
	}

	public boolean updateAfterSaleInventoryProduct(String set, String condition) {
		return updateXXX(set, condition, "after_sale_inventory_product");
	}

	public boolean deleteAfterSaleInventoryProduct(String condition) {
		return deleteXXX(condition, "after_sale_inventory_product");
	}

	public boolean addAfterSaleInventoryRecord(AfterSaleInventoryRecord bean) {
		return addXXX(bean, "after_sale_inventory_record");
	}

	public List getAfterSaleInventoryRecordList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_inventory_record",
				"mmb.stock.aftersale.AfterSaleInventoryRecord");
	}

	public int getAfterSaleInventoryRecordCount(String condition) {
		return getXXXCount(condition, "after_sale_inventory_record", "id");
	}

	public AfterSaleInventoryRecord getAfterSaleInventoryRecord(String condition) {
		return (AfterSaleInventoryRecord) getXXX(condition,
				"after_sale_inventory_record",
				"mmb.stock.aftersale.AfterSaleInventoryRecord");
	}

	public boolean updateAfterSaleInventoryRecord(String set, String condition) {
		return updateXXX(set, condition, "after_sale_inventory_record");
	}

	public boolean deleteAfterSaleInventoryRecord(String condition) {
		return deleteXXX(condition, "after_sale_inventory_record");
	}
	
	/**
	 * 获取盘点商品的详细信息
	 * @param condition
	 * @param index
	 * @param count
	 * @return
	 * @author 李宁
	* @date 2014-4-18
	 */
	public List<Map<String,String>> getAfterSaleInventoryProductList(String condition,int index,int count){
		DbOperation dbop = this.getDbOp();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		String query = "select afip.id,afip.after_sale_detect_product_code,afip.after_sale_detect_product_status,afip.after_sale_order_code," +
				"p.code,p.name,afip.record_whole_code,afip.real_whole_code from after_sale_inventory_product " +
				"afip join product p on afip.product_id=p.id join after_sale_inventory_record asir on afip.after_sale_inventory_record_id = asir.id where 1=1 "; 
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		try {
			ResultSet rs = dbop.executeQuery(query);
			HashMap map = null;
			while (rs.next()) {
				map =new HashMap();
				map.put("afterSaleInventoryProductId",String.valueOf(rs.getInt("afip.id")));
				map.put( "afterSaleDetectCode", rs.getString("afip.after_sale_detect_product_code"));
				int status =  rs.getInt("afip.after_sale_detect_product_status");
				map.put( "afterSaleDetectProductStatus",AfterSaleDetectProductBean.statusMap.get(status));
				map.put( "afterSaleOrderCode", rs.getString("afip.after_sale_order_code"));
				map.put( "productCode", rs.getString("p.code"));
				map.put( "productName", rs.getString("p.name"));
				map.put( "recordWholeCode", rs.getString("afip.record_whole_code"));
				map.put( "realWholeCode", rs.getString("afip.real_whole_code"));
				list.add(map);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return list;
	}
	
	/**
	 * 获取盘点商品数量
	 * @param condition
	 * @param index
	 * @param count
	 * @return
	 * @author zy
	* @date 2014-4-21
	 */
	public int getAfterSaleInventoryProductListCount(String condition){
		DbOperation dbop = this.getDbOp();
		String query = "select count(*) from after_sale_inventory_product " +
				"afip join product p on afip.product_id=p.id join after_sale_inventory_record asir on afip.after_sale_inventory_record_id = asir.id where 1=1 "; 
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		int count = 0;
		try {
			ResultSet rs = dbop.executeQuery(query);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return count;
	}

	/** 新建报损报溢单 商品表信息 
	 * @param dbOp **/
	public List<AfterSaleBsbyProduct> getAfterSaleBsbyProductORProductListInfo(
			int bsbyOperationnoteId, DbOperation dbOp) {
		List<AfterSaleBsbyProduct> list = new ArrayList<AfterSaleBsbyProduct>();
		String sql = "SELECT ci.stock_type stockType, p.id productId, p.`name` productName, p.`code` productCode, " +
				"asbp.after_sale_detect_product_code after_sale_detect_product_code," +
				"asbp.after_sale_order_code after_sale_order_code, " +
				"asbp.after_sale_detect_product_status after_sale_detect_product_status, " +
				"asbp.IMEI IMEI, asbp.whole_code wholeCode FROM after_sale_bsby_product asbp " +
				"LEFT JOIN product p ON asbp.product_id = p.id " +
				"LEFT JOIN cargo_info ci ON asbp.whole_code = ci.whole_code " +
				"where asbp.bsby_operationnote_id = "
				+ bsbyOperationnoteId;

		ResultSet rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				AfterSaleBsbyProduct bean = new AfterSaleBsbyProduct();
				String afterSaleDetectProductCode = rs
						.getString("after_sale_detect_product_code");
				bean.setAfterSaleOrderCode(rs
						.getString("after_sale_order_code"));
				bean.setProductId(rs.getInt("productId"));
				bean.setProductName(rs.getString("productName"));
				bean.setProductCode(rs.getString("productCode"));
				bean.setAfterSaleDetectProductCode(afterSaleDetectProductCode);
				bean.setAfterSaleDetectProductStatusName(AfterSaleDetectProductBean.statusMap.get(rs.getInt("after_sale_detect_product_status")));
				bean.setImei(rs.getString("IMEI"));
				bean.setWholeCode(rs.getString("wholeCode"));
				bean.setStockType(rs.getInt("stockType"));
				list.add(bean);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	/** 新建报损报溢单 添加列表 
	 * @param dbOp **/
	public List<AfterSaleBsbyProduct> getAfterSaleBsbyProductAndProductInfo(
			String afterSaleOrderCodes,int stockType, DbOperation dbOp) {
		List<AfterSaleBsbyProduct> list = new ArrayList<AfterSaleBsbyProduct>();

		String sql = "SELECT ci.stock_type stockType, p.id productId, p.`name` productName, p.`code` productCode, " +
				"asdp.`code` code,asdp.after_sale_order_code after_sale_order_code, asdp.`status` status, " +
				"asdp.IMEI IMEI, asdp.cargo_whole_code cargoWholeCode FROM after_sale_detect_product asdp " +
				"LEFT JOIN cargo_info ci ON asdp.cargo_whole_code = ci.whole_code " +
				"LEFT JOIN product p ON asdp.product_id = p.id WHERE " +
				"ci.stock_type = " + stockType + " AND asdp.`code` IN (" + afterSaleOrderCodes + ");";
		ResultSet rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				AfterSaleBsbyProduct bean = new AfterSaleBsbyProduct();
				String afterSaleDetectProductCode = rs.getString("code");

				bean.setAfterSaleOrderCode(rs
						.getString("after_sale_order_code"));
				bean.setProductId(rs.getInt("productId"));
				bean.setProductName(rs.getString("productName"));
				bean.setProductCode(rs.getString("productCode"));
				bean.setAfterSaleDetectProductCode(afterSaleDetectProductCode);
				bean.setAfterSaleDetectProductStatusName(AfterSaleDetectProductBean.statusMap
						.get(rs.getInt("status")));
				bean.setImei(rs.getString("IMEI"));
				bean.setWholeCode(rs.getString("cargoWholeCode"));
				bean.setStockType(rs.getInt("stockType"));

				list.add(bean);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	
	/**
	 * 查询盘点记录中已返厂的商品数量
	 * @param condition
	 * @return
	 * @author 李宁
	* @date 2014-4-21
	 */
	public int getbackSuppilerProductCount(String condition){
		int count = 0;
		DbOperation dbop = this.getDbOp();
		String sql ="select count(asbsp.id) amount from after_sale_back_supplier_product asbsp join after_sale_detect_product asdp on asdp.id=asbsp.after_sale_detect_product_id " +
							"join cargo_info ci on asdp.cargo_whole_code=ci.whole_code where asbsp.status in(0,4) ";
		if (null != condition && !condition.equals("")) {
			sql += condition;
		}
		ResultSet rs = dbOp.executeQuery(sql);
		try{
			if(rs!=null && rs.next()){
				count = rs.getInt("amount");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return count;
	}
	
	/**
	 * 查询盘点记录中有库存记录缺少实物的商品数量
	 * @param condition
	 * @return
	 * @author 李宁
	* @date 2014-4-21
	 */
	public int getbsCount(int stockType,int recordId,int areaId){
		int count = 0;
		DbOperation dbop = this.getDbOp();
		StringBuilder sql = new StringBuilder();
		sql.append("select count(asdp.code) amount from after_sale_detect_product asdp left join cargo_info ci on asdp.cargo_whole_code=ci.whole_code ")
			.append(" where asdp.status in (0,1,2,3,4,5,6,7,10,12,13,16,17,19) ");
		if(stockType>=0){
			sql.append(" and ci.stock_type=").append(stockType);
		}
		if(areaId>=0){
			sql.append(" and ci.area_id=").append(areaId);
		}
		sql.append(" and asdp.code not in (select after_sale_detect_product_code from after_sale_inventory_product where type!=2 ");
		if(recordId>0){
			sql.append(" and after_sale_inventory_record_id=").append(recordId);
		}
		sql.append(")");
		ResultSet rs = dbOp.executeQuery(sql.toString());
		try{
			if(rs!=null && rs.next()){
				count = rs.getInt("amount");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return count;
	}
	
	/**
	 * 获取盘点记录中库存商品
	 *需要区分库类型， 处理单中状态不是已完成状态的数量
	 * @param condition
	 * @return
	 * @author 李宁
	* @date 2014-4-22
	 */
	public List<AfterSaleDetectProductBean> getStockCountProducts(String condition){
		List<AfterSaleDetectProductBean> list = new ArrayList<AfterSaleDetectProductBean>();
		DbOperation dbop = this.getDbOp();
		String sql ="select asdp.id,asdp.product_id,asdp.after_sale_order_id,asdp.after_sale_order_code,asdp.code,asdp.status,asdp.cargo_whole_code " +
				"from after_sale_detect_product asdp left join cargo_info ci on asdp.cargo_whole_code=ci.whole_code " +
							"where asdp.status in (0,1,2,3,4,5,6,7,10,12,13,16,17,19) ";
		if (null != condition && !condition.equals("")) {
			sql += condition;
		}
		ResultSet rs = dbOp.executeQuery(sql);
		AfterSaleDetectProductBean bean = null;
		try{
			while(rs!=null && rs.next()){
				bean = new AfterSaleDetectProductBean();
				bean.setId(rs.getInt("asdp.id"));
				bean.setProductId(rs.getInt("asdp.product_id"));
				bean.setAfterSaleOrderId(rs.getInt("asdp.after_sale_order_id"));
				bean.setAfterSaleOrderCode(rs.getString("asdp.after_sale_order_code"));
				bean.setCode(rs.getString("asdp.code"));
				bean.setStatus(rs.getInt("asdp.status"));
				bean.setCargoWholeCode(rs.getString("asdp.cargo_whole_code"));
				list.add(bean);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return list;
	}
	
	public boolean addAfterSaleStockExchangeProduct(AfterSaleStockExchangeProduct bean) {
		return addXXX(bean, "after_sale_stock_exchange_product");
	}

	public List getAfterSaleStockExchangeProductList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"after_sale_stock_exchange_product",
				"mmb.stock.aftersale.AfterSaleStockExchangeProduct");
	}

	public int getAfterSaleStockExchangeProductCount(String condition) {
		return getXXXCount(condition, "after_sale_stock_exchange_product", "id");
	}

	public AfterSaleStockExchangeProduct getAfterSaleStockExchangeProduct(String condition) {
		return (AfterSaleStockExchangeProduct) getXXX(condition,
				"after_sale_stock_exchange_product",
				"mmb.stock.aftersale.AfterSaleStockExchangeProduct");
	}

	public boolean updateAfterSaleStockExchangeProduct(String set, String condition) {
		return updateXXX(set, condition, "after_sale_stock_exchange_product");
	}

	public boolean deleteAfterSaleStockExchangeProduct(String condition) {
		return deleteXXX(condition, "after_sale_stock_exchange_product");
	}
	
	public List<Map<String, String>> getAfterSaleStockExchangeProductInfo(
			String afterSaleOrderCodes, DbOperation dbOp) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String sql = "select p.name productName, p.code productCode,asdp.code afterSaleDetectProductCode," +
				"asdp.imei imei,asdp.cargo_whole_code wholeCode," +
				"(select content from after_sale_warehource_record_detail aswrd where aswrd.after_sale_warehource_record_id=asdp.id and aswrd.after_sale_detect_type_id=" + AfterSaleDetectTypeBean.MAIN_PRODUCT_STATUS + ") mainStatus," +
						"psp.type sellType " +
				"from after_sale_detect_product asdp join product p on asdp.product_id=p.id " +
				"left join product_sell_property psp on asdp.product_id=psp.product_id "
				+ " where asdp.code ='" + afterSaleOrderCodes + "'"; 
		ResultSet rs = dbOp.executeQuery(sql);
		Map<String, String> map = null;
		try {
			while (rs.next()) {
				map = new HashMap<String, String>();
				map.put("productName", rs.getString("productName"));
				map.put("productCode", rs.getString("productCode"));
				map.put("afterSaleDetectProductCode", rs.getString("afterSaleDetectProductCode"));
				map.put("imei", rs.getString("imei"));
				map.put("wholeCode", rs.getString("wholeCode"));
				map.put("mainStatus", rs.getString("mainStatus"));
				map.put("sellType", ProductSellPropertyBean.typeMap.get(rs.getInt("sellType")));
				list.add(map);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	public List<Map<String, String>> getAfterSaleStockExchangeProductInfo(
			int stockExchangeId, DbOperation dbOp) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String sql = "select p.name productName, p.code productCode,asdp.code afterSaleDetectProductCode," +
				"asdp.imei imei,asdp.cargo_whole_code wholeCode," +
				"(select content from after_sale_warehource_record_detail aswrd where aswrd.after_sale_warehource_record_id=asdp.id and aswrd.after_sale_detect_type_id=" + AfterSaleDetectTypeBean.MAIN_PRODUCT_STATUS + ") mainStatus," +
						"psp.type sellType " +
				"from after_sale_detect_product asdp join product p on asdp.product_id=p.id " +
				"left join product_sell_property psp on asdp.product_id=psp.product_id " +
				" join after_sale_stock_exchange_product assep on assep.after_sale_detect_product_id=asdp.id"
				+ " where assep.stock_exchange_id =" + stockExchangeId; 
		ResultSet rs = dbOp.executeQuery(sql);
		Map<String, String> map = null;
		try {
			while (rs.next()) {
				map = new HashMap<String, String>();
				map.put("productName", rs.getString("productName"));
				map.put("productCode", rs.getString("productCode"));
				map.put("afterSaleDetectProductCode", rs.getString("afterSaleDetectProductCode"));
				map.put("imei", rs.getString("imei"));
				map.put("wholeCode", rs.getString("wholeCode"));
				map.put("mainStatus", rs.getString("mainStatus"));
				map.put("sellType", ProductSellPropertyBean.typeMap.get(rs.getInt("sellType")));
				list.add(map);
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
	
	/**
	 * 查询售后处理单商品数量（涉及到库地区和库类型）
	 * @param condition
	 * @return
	 * @author 李宁
	* @date 2014-4-28
	 */
	public int getAfterSaleDetectCount(String condition){
		int count = 0;
		DbOperation dbop = this.getDbOp();
		StringBuilder sql = new StringBuilder();
		sql.append("select count(asdp.code) amount from after_sale_detect_product asdp left join cargo_info ci on asdp.cargo_whole_code=ci.whole_code ")
			.append(" where 1=1 ");
		if(condition!=null && condition.trim().length()>0){
			sql.append(condition);
		}
		ResultSet rs = dbOp.executeQuery(sql.toString());
		try{
			if(rs!=null && rs.next()){
				count = rs.getInt("amount");
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return count;
	}
	/**
	 * 导出返厂列表
	 * @param rows
	 * @return
	 * @author syuf
	 * @date 2014-05-08
	 */
	public void excelBackSupplierProduct(HttpServletResponse response,List<Map<String, String>> list) {
		try {
			ExportExcel excel = new ExportExcel();
			List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
			ArrayList<String> header = new ArrayList<String>();
			header.add("商品名称");
			header.add("商品原名称");
			header.add("一级分类");
			header.add("商品型号");
			header.add("售后处理单号 ");
			header.add("IMEI");
			header.add("是否返修");
			header.add("故障代码");
			header.add("故障描述");
			header.add("申报状态");
			header.add("发货日期");
			header.add("状态");
			header.add("维修厂商 ");
			header.add("商品类型");
			header.add("检测日期");
			headers.add(header);
			
			List<Integer> mayMergeColumn = new ArrayList<Integer>();
			excel.setMayMergeColumn(mayMergeColumn);
			
			List<Integer> mayMergeRow = new ArrayList<Integer>();
			excel.setMayMergeRow(mayMergeRow);
			
			List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
			
			if(list != null && list.size() > 0){
				for(Map<String, String> map : list){
					ArrayList<String> printList = new ArrayList<String>();
					printList.add(map.get("productName"));
					printList.add(map.get("productOriName"));
					printList.add(map.get("catalogName"));
					printList.add(map.get("productModel"));
					printList.add(map.get("afterSaleDetectCode"));
					printList.add(map.get("imeiCode"));
					printList.add(map.get("repair"));
					printList.add(map.get("problemCode"));
					printList.add(map.get("problemRmark"));
					printList.add(map.get("declareStatus"));
					printList.add(map.get("sendDatetime"));
					printList.add(map.get("statusName"));
					printList.add(map.get("supplierName"));
					printList.add(map.get("stockTypeName"));
					printList.add(map.get("createDateTime"));
					bodies.add(printList);
				}
			}
			
			excel.setColMergeCount(headers.get(0).size());
			
			List<Integer> row  = new ArrayList<Integer>();
			row.add(bodies.size()-1);
			row.add(bodies.size()-3);
			row.add(bodies.size()-5);
			
			List<Integer> col  = new ArrayList<Integer>();
			
			excel.setRow(row);
			excel.setCol(col);
			
			excel.buildListHeader(headers);
			excel.setHeader(false);
			
			excel.buildListBody(bodies);
			
			String fileName = "product_" + DateUtil.getNow().substring(0, 10);
			response.reset();
			response.addHeader(
					"Content-Disposition",
					"attachment;filename="+ fileName+ ".xlsx");
			response.setContentType("application/msxls");
			response.setCharacterEncoding("utf-8");
			excel.getWorkbook().write(response.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取售后处理单中最后一次检测日志中的各种检测选项的内容
	 * @param detectType
	 * @param detectProductId
	 * @return
	 * @author 李宁
	* @date 2014-5-29
	 */
	public String getAfterSaleDetectLogContent(int detectType,int detectProductId){
		String result = "";
		DbOperation dbop = this.getDbOp();
		StringBuilder sql = new StringBuilder();
		sql.append("select content,content2,content3 from after_sale_detect_log where 1=1 ")
			.append(" and after_sale_detect_type_id=").append(detectType)
			.append(" and after_sale_detect_product_id=").append(detectProductId)
			.append(" order by create_datetime desc limit 1");
		ResultSet rs = dbOp.executeQuery(sql.toString());
		try{
			if(rs!=null && rs.next()){
				result = rs.getString(1);
				String result2 = rs.getString(2);
				String result3 = rs.getString(3);
				if(detectType == AfterSaleDetectTypeBean.FAULT_DESCRIPTION || detectType == AfterSaleDetectTypeBean.QUESTION_DESCRIPTION){//故障描述、问题描述
					if(!"".equals(result) &&  null!= result2 && !"".equals(result2)){
						result += "/"+result2;
						if(null != result3 && !"".equals(result3)){
							result += "/"+result3;
						}
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return result;
	}
	/**
	 * 售后入库修改订单状态
	 * @return
	 * @author zy
	* @date 2014-6-4
	 */
	public String updateOrderAfterSale(int orderId,DbOperation dbOp,voUser user){
		
		WareService wareService = new WareService(dbOp);
		//如果传null的话 默认sms库 
		IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
		voOrder order = wareService.getOrder(orderId);
		if (order == null) {
			return "订单信息不存在！";
		}
		ResultSet rs = null;
		try {
			if (order.getStatus() != 11 ) {
				Map<Integer, Integer> asdpMap = new HashMap<Integer, Integer>();
				StringBuilder sql = new StringBuilder();
				sql.append("select asdp.product_id, count(asdp.id) from after_sale_detect_product asdp, after_sale_order aso,after_sale_stockin ass where asdp.after_sale_order_id=aso.id and asdp.id=ass.after_sale_detect_product_id and ass.status=").append(AfterSaleStockin.STATUS1).append(" and aso.order_id=").append(orderId).append(" group by asdp.product_id");
				rs = dbOp.executeQuery(sql.toString());
				while(rs.next()){
					asdpMap.put(rs.getInt(1), rs.getInt(2));
				}
				sql.setLength(0);
				Map<Integer, Integer> orderMap = new HashMap<Integer, Integer>();
				sql.append("select osp.product_id,osp.stockout_count from order_stock_product osp,order_stock os where osp.order_stock_id=os.id and os.status<> ").append(OrderStockBean.STATUS4).append(" and os.order_id=").append(orderId);
				rs = dbOp.executeQuery(sql.toString());
				while(rs.next()){
					orderMap.put(rs.getInt(1), rs.getInt(2));
				}
				for (int i : orderMap.keySet()) {
					Integer count = asdpMap.get(i);
					if (count == null) {
						return null;
					} else if (count < orderMap.get(i)) {
						return null;
					}
				}
				if(rs != null){
					rs.close();
				}
				//修改订单信息，记录日志
				String query = "update `user_order` set `status`='11' where id="+ order.getId();
				if (!wareService.getDbOp().executeUpdate(query)) {
					return "更新订单状态失败！";
				}
				// 订单状态转换的日志
				StringBuilder logContent = new StringBuilder();
				logContent.append("[售后退换货完成,订单状态变为已退回]");
				if(logContent.length() > 0){
					OrderAdminLogBean orderAdminLog = new OrderAdminLogBean();
					orderAdminLog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
					orderAdminLog.setUserId(user.getId());
					orderAdminLog.setUsername(user.getUsername());
					orderAdminLog.setOrderId(order.getId());
					orderAdminLog.setOrderCode(order.getCode());
					orderAdminLog.setCreateDatetime(DateUtil.getNow());
					orderAdminLog.setContent(logContent.toString());
					if (!logService.addOrderAdminLog(orderAdminLog) ) {
						return "添加订单操作日志失败！";
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "异常";
		} finally {
			logService.releaseAll();
		}
		return null;
	}
	
	/**
	 * 查询处理单商品的配件信息
	 * @param afterSalOrderId
	 * @param orderBy
	 * @return
	 * @author 李宁
	* @date 2014-5-26
	 */
	public String getAfterSaleFitting(int afterSaleProductId,int flag){
		DbOperation dbop = this.getDbOp();
		String query = "select fitting_name,intact_count,damage_count from after_sale_detect_product_fitting where 1=1";
		if(afterSaleProductId>0){
			query += " and after_sale_detect_product_id=" + afterSaleProductId;
		}
		String result = "";
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs!=null && rs.next()) {
				String fittingName = rs.getString(1);
				if(fittingName!=null && fittingName.trim().length()>0){
					int count = rs.getInt(2) + rs.getInt(3);
					if(flag==1){
						result = result + rs.getString(1) + "(" + count + ")" +"\n";
					}else{
						result = result + rs.getString(1) + "\n";
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return result;
	}
	
	/**
	 * 查询检测信息表原因
	 * @author ahc
	 * @date 2014-12-18
	 */
	public String getAfterSaleDetectLog(int afterSaleProductId){
		DbOperation dbop = this.getDbOp();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT content FROM after_sale_detect_log WHERE after_sale_detect_product_id = "+afterSaleProductId+" and after_sale_detect_type_id = 4 ORDER BY id asc LIMIT 1");
		String result = "";
		try {
			ResultSet rs = dbop.executeQuery(sql.toString());
			if (rs!=null && rs.next()) {
				result = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.release(dbop);
		}
		return result;
	}

	public EasyuiDataGridBean getAfterSaleFittingDatagrid(String condition,EasyuiPageBean page) {
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		int total = this.getAfterSaleFittingCount(condition);
		datagrid.setTotal((long)total);
		List<Map<String,String>> list = this.getAfterSaleFittingListLeft(condition, (page.getPage()-1)*page.getRows(), page.getRows(), null);
		for(Map<String,String> map : list){
			List<Map<String,String>> fittingList = this.getAfterSaleFittingListInner("fitting_id=" + map.get("fid"),-1,-1,null);
			String productId = "";
			String productName = "";
			for(Map<String,String> fitting : fittingList){
				if(!"".equals(productId)){
					productId += "," + fitting.get("productId");
					map.put("productId", productId);
				} else {
					productId = fitting.get("productId");
				}
				if(!"".equals(productName)){
					productName += "," + fitting.get("productName");
					map.put("productName", productName);
				} else {
					productName =  fitting.get("productName");
				}
			}
		}
		datagrid.setRows(list);
		return datagrid;
	}
	/**
	 * 售后配件列表
	 * hp
	 */
	public EasyuiDataGridBean getAfterSaleFittingsDatagrid(String condition,EasyuiPageBean page,String status) {
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		int total = this.getAfterSaleFittingsCount(condition,status);
		datagrid.setTotal((long)total);
		List<Map<String,String>> list = this.getAfterSaleFittingsListLeft(condition, (page.getPage()-1)*page.getRows(), page.getRows(), status);
        datagrid.setRows(list);
		return datagrid;
	}
	@SuppressWarnings("unchecked")
	public List<AfterSaleFittingsBean> getAfterSaleFittingList(String condition,int index,int count,String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_fittings", "mmb.stock.aftersale.AfterSaleFittingsBean");
	}

	public int getAfterSaleFittingCount(String condition) {
		int count = 0;
		try {
			String query = "SELECT count(a.id) " +
							"from (SELECT count(f.id) id " +
									"FROM " +
									"product AS f " +
									"LEFT JOIN after_sale_fittings AS asf ON f.id = asf.fitting_id " +
									"LEFT JOIN product AS p ON asf.product_id = p.id " +
									"WHERE " + condition + " GROUP BY f.id) as a";
			ResultSet rs = this.dbOp.executeQuery(query);
			if(rs.next()){
				count = rs.getInt(1);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * hp
	 */
	public int getAfterSaleFittingsCount(String condition,String status) {
		int count = 0;
		try {
			String query ="";
			if(status==null){
			query="select p.code ,p.name,p.code,cps.stock_count,cps.stock_lock_count,cps.cargo_id,cinfo.type,area_id from product p ,after_sale_fittings asf,cargo_product_stock cps,cargo_info cinfo where p.id=asf.fitting_id  and cps.product_id=p.id and cps.cargo_id=cinfo.id  and p.parent_id1="+1536+" and cinfo.type in("+CargoInfoBean.TYPE3+","+CargoInfoBean.TYPE4+") and cinfo.stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING+" GROUP BY code";
			}else{
			query="select p.code ,p.name,p.code,cps.stock_count,cps.stock_lock_count,cps.cargo_id,cinfo.type,area_id from product p ,after_sale_fittings asf,cargo_product_stock cps,cargo_info cinfo where p.id=asf.fitting_id  and cps.product_id=p.id and cps.cargo_id=cinfo.id  and p.parent_id1="+1536+" and cinfo.type in("+CargoInfoBean.TYPE3+","+CargoInfoBean.TYPE4+") and cinfo.stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING+" GROUP BY code";
			}
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs.next()){
				count = count+1;
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	public List<Map<String, String>> getAfterSaleFittingListLeft(String condition,int index,int count,String order) {
		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
		try {
			String query = "SELECT asf.product_id,asf.fitting_id,f.`name`,f.`code`,p.`name`,p.`code`,f.id " +
					"FROM product AS f " +
					"left JOIN after_sale_fittings AS asf ON f.id = asf.fitting_id " +
					"left JOIN product AS p ON asf.product_id = p.id " +
					"where " + condition + " group by f.id ";
			if(!"".equals(StringUtil.checkNull(order))){
				query += order;
			}
			query = DbOperation.getPagingQuery(query, index, count);
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs.next()){
				Map<String,String> map = new HashMap<String, String>();
				map.put("productId", rs.getInt(1) + "");
				map.put("fittingId", rs.getInt(2) + "");
				map.put("fittingName", rs.getString(3));
				map.put("fittingCode", rs.getString(4));
				map.put("productName", rs.getString(5));
				map.put("productCode", rs.getString(6));
				map.put("fid", rs.getString(7));
				maps.add(map);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maps;
	}
	/**
	 * hp
	 * 
	 */
	public List<Map<String, String>> getAfterSaleFittingsListLeft(String condition,int index,int count,String status) {
		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
		try {
			String query ="";
			
			if(status==null){
			query = "select DISTINCT p.id,p.name,p.code,cps.stock_count,cps.stock_lock_count,cps.cargo_id,cinfo.type,area_id,p.parent_id2,p.parent_id3 from product p ,after_sale_fittings asf,cargo_product_stock cps,cargo_info cinfo where p.id=asf.fitting_id  and cps.product_id=p.id and cps.cargo_id=cinfo.id and p.parent_id1="+1536+" and cinfo.stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING+" and cinfo.type in("+CargoInfoBean.TYPE3+","+CargoInfoBean.TYPE4+") ";
			}else{
			query = "select DISTINCT p.id,p.name,p.code,cps.stock_count,cps.stock_lock_count,cps.cargo_id,cinfo.type,area_id,p.parent_id2,p.parent_id3 from product p ,after_sale_fittings asf,cargo_product_stock cps,cargo_info cinfo where p.id=asf.fitting_id  and cps.product_id=p.id and cps.cargo_id=cinfo.id and p.parent_id1="+1536+" and cinfo.stock_type="+ProductStockBean.STOCKTYPE_CUSTOMER_FITTING+" and cinfo.type in("+CargoInfoBean.TYPE3+" ,"+CargoInfoBean.TYPE4+") ";	
			}
			
			if(!"".equals(StringUtil.checkNull(condition))){
				query += condition;
			}
			/**
			 * 得出  售后配件的 总库存
			 * 
			 * 求 不同类型（完好，残次）       不同地区 （深圳  芳村）   的总量
			 * 
			 */
			query = DbOperation.getPagingQuery(query, index, count);
			ResultSet rs = this.dbOp.executeQuery(query);
			//临时保存配件信息
			Map<String, Map<String, String>> codemaps=new HashMap<String, Map<String,String>>();
			while(rs.next()){
				Map<String,String> map = new HashMap<String, String>();
				if( codemaps.containsKey(rs.getString(3)) ) {
			        Map<String, String> tempMap=codemaps.get(rs.getString(3));
			        maps.remove(tempMap);
			        //匹配 相同 配件       相同地区      相同类型    则数量累加
                 
                	                  //固定值 应改成 实体类对应的 常量。
			        	if(rs.getString(8).equals("1")){ //地区相同    1 芳村
			        		 //判断 是完好 还是残次
			        			 if(rs.getInt(7)==CargoInfoBean.TYPE3){
			        				 if(tempMap.get("fwh")!=null && !"".equals(tempMap.get("fwh"))){
			        				 tempMap.put("fwh", (Integer.parseInt(tempMap.get("fwh").toString())+rs.getInt(4)+rs.getInt(5))+"");//芳村完好数量
			        				 }else{
			        					 tempMap.put("fwh", (rs.getInt(4)+rs.getInt(5))+"");//芳村完好数量 
			        				 }
			        			 }else if((rs.getInt(7)==CargoInfoBean.TYPE4)){
			        				 if(tempMap.get("fcc")!=null && !"".equals(tempMap.get("fcc"))){
			        				 tempMap.put("fcc", (Integer.parseInt(tempMap.get("fcc").toString())+rs.getInt(4)+rs.getInt(5))+"");//芳村残次数量
			        				 }else{
			        					 tempMap.put("fcc", (rs.getInt(4)+rs.getInt(5))+"");//芳村残次数量  
			        				 }
			        			 }
			        	}
			        	if(rs.getString(8).equals("7")){ //深圳
			        		 //判断 是完好 还是残次
			        		if(rs.getInt(7)==CargoInfoBean.TYPE3){
		        				 if(tempMap.get("swh")!=null && !"".equals(tempMap.get("swh"))){
		        				 tempMap.put("swh", (Integer.parseInt(tempMap.get("swh").toString())+rs.getInt(4)+rs.getInt(5))+"");//深圳完好数量
		        				 }else {
		        					 tempMap.put("swh", (rs.getInt(4)+rs.getInt(5))+"");//深圳完好数量
								 }
			        		 }else if((rs.getInt(7)==CargoInfoBean.TYPE4)){
		        				 if(tempMap.get("scc")!=null && !"".equals(tempMap.get("scc"))){
		        				 tempMap.put("scc", (Integer.parseInt(tempMap.get("scc").toString())+rs.getInt(4)+rs.getInt(5))+"");//深圳残次数量
		        				 }else {
		        					 tempMap.put("scc", (rs.getInt(4)+rs.getInt(5))+"");//深圳残次数量
								}
		        			 }
			        	}
			        	if("".equals(tempMap.get("fwh")) ||tempMap.get("fwh")==null ){
			        		tempMap.put("fwh", "0");
			        	}
			        	if("".equals(tempMap.get("fcc")) ||tempMap.get("fcc")==null ){
			        		tempMap.put("fcc", "0");
			        	}
			        	if("".equals(tempMap.get("swh")) ||tempMap.get("swh")==null ){
			        		map.put("swh", "0");
			        	}
			        	if("".equals(tempMap.get("scc")) ||tempMap.get("scc")==null ){
			        		tempMap.put("scc", "0");
			        	}
			     tempMap.put("sumcount", (Integer.parseInt(tempMap.get("fwh").toString())+Integer.parseInt(tempMap.get("fcc").toString())+Integer.parseInt(tempMap.get("swh").toString())+Integer.parseInt(tempMap.get("scc").toString()))+""); //总量
			     maps.add(tempMap);
				}else{ 
					map.put("productId", rs.getInt(1) + "");
					map.put("fittingName", rs.getString(2)); //配件名称
					map.put("fittingCode", rs.getString(3));//配件编号
			        map.put("type", rs.getInt(7)+""); //类型
					map.put("areaId", rs.getInt(8)+""); //地区
					int parent2=rs.getInt(9);
					map.put("pname2",getCatalogById(parent2)==null?"未知分类":getCatalogById(parent2).getName());//二级分类名称
					int parent3=rs.getInt(10);
					map.put("pname3", getCatalogById(parent3)==null?"未知分类":getCatalogById(parent3).getName());//三级分类名称
					if(rs.getString(8).equals("1")){ //地区相同    1 芳村
		        		 //判断 是完好 还是残次
		        			 if(rs.getInt(7)== CargoInfoBean.TYPE3){
		        				 map.put("fwh", (rs.getInt(4)+rs.getInt(5))+"");//芳村完好数量
		        			 }else if(rs.getInt(7)== CargoInfoBean.TYPE4){
		        				 map.put("fcc", (rs.getInt(4)+rs.getInt(5))+"");//芳村残次数量
		        			 }
		        	}
		        	if(rs.getString(8).equals("7")){ //深圳
		        		 //判断 是完好 还是残次
	        			 if(rs.getInt(7)== CargoInfoBean.TYPE3){
	        				 map.put("swh", (rs.getInt(4)+rs.getInt(5))+"");//深圳完好数量
	        			 }else if(rs.getInt(7)== CargoInfoBean.TYPE4){
	        				 map.put("scc", (rs.getInt(4)+rs.getInt(5))+"");//深圳残次数量
	        			 }
		        	}
		        	if("".equals(map.get("fwh")) ||map.get("fwh")==null ){
		        		map.put("fwh", "0");
		        	}
		        	if("".equals(map.get("fcc")) ||map.get("fcc")==null ){
		        		map.put("fcc", "0");
		        	}
		        	if("".equals(map.get("swh")) ||map.get("swh")==null ){
		        		map.put("swh", "0");
		        	}
		        	if("".equals(map.get("scc")) ||map.get("scc")==null ){
		        		map.put("scc", "0");
		        	}
		        	map.put("sumcount", (Integer.parseInt(map.get("fwh").toString())+Integer.parseInt(map.get("fcc").toString())+Integer.parseInt(map.get("swh").toString())+Integer.parseInt(map.get("scc").toString()))+""); //总量
				codemaps.put(rs.getString(3), map);
				maps.add(map);
				}
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maps;
	}
 /**
  * 根据id 获取配件信息
  * @param parentId
  * @return 配件信息
  * hp
  */
	public voCatalog getCatalogById(int parentId){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		voCatalog voCatalog=null;
		try {
			String query="SELECT id, parent_id, name from catalog where id="+parentId;
			
			ResultSet rs = dbOp.executeQuery(query);
			if(rs.next()){
				voCatalog=new voCatalog();
				voCatalog.setId(rs.getInt(1));
				voCatalog.setParentId(rs.getInt(2));
				voCatalog.setName(rs.getString(3));
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return voCatalog;
	}
	
	public List<EasyuiComBoBoxBean> loadMatchProduct(String fittingCode) {
		List<EasyuiComBoBoxBean> comboboxList = new ArrayList<EasyuiComBoBoxBean>();
		if(!"".equals(StringUtil.checkNull(fittingCode))){
			List<Map<String,String>> maps = this.getAfterSaleFittingListInner("f.code='" + fittingCode + "'",-1,-1,null);
			for(Map<String,String> map : maps){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(map.get("productId"));
				bean.setText(map.get("productName"));
				bean.setSelected(true);
				comboboxList.add(bean);
			}
		}
		return comboboxList;
	}

	public List<Map<String, String>> getAfterSaleFittingListInner(String condition,int index, int count, String order) {
		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
		try {
			String query = "SELECT asf.product_id,asf.fitting_id,f.`name`,f.`code`,p.`name`,p.`code` " +
					"FROM product AS f " +
					"inner JOIN after_sale_fittings AS asf ON f.id = asf.fitting_id " +
					"inner JOIN product AS p ON asf.product_id = p.id ";
			if(!"".equals(StringUtil.checkNull(condition))){
				query += " WHERE " + condition;
			}
			if(!"".equals(StringUtil.checkNull(order))){
				query += order;
			}
			query = DbOperation.getPagingQuery(query, index, count);
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs.next()){
				Map<String,String> map = new HashMap<String, String>();
				map.put("productId", rs.getInt(1) + "");
				map.put("fittingId", rs.getInt(2) + "");
				map.put("fittingName", rs.getString(3));
				map.put("fittingCode", rs.getString(4));
				map.put("productName", rs.getString(5));
				map.put("productCode", rs.getString(6));
				maps.add(map);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maps;
	}

	private List<Map<String, String>> getProductList(String condition) {
		
		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
		try {
			String query = "SELECT product.id, product.`name` FROM product WHERE " + condition;
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs.next()){
				Map<String,String> map = new HashMap<String, String>();
				map.put("productId", rs.getInt(1) + "");
				map.put("productName", rs.getString(2));
				maps.add(map);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maps;
	}

	public Json importFittingBatch(String fittingInfos) {
		Json j = new Json();
		Map<String,String> map = new HashMap<String, String>();
		if(!"".equals(StringUtil.checkNull(fittingInfos))){
			for(String s : fittingInfos.split("\n")){
				if(s.split("\t").length < 2){
					j.setMsg("导入的数据格式不正确!");
					return j;
				}
				if(!map.containsKey(s.split("\t")[0])){ //去重 只保留第一个
					map.put(s.split("\t")[0].trim(), s.trim());
				}
			}
		}
		WareService service = new WareService(this.dbOp);
		this.dbOp.startTransaction();
		for(Map.Entry<String, String> m : map.entrySet()){
			String value = m.getValue();
			String[] ids = value.split("\t");
			AfterSaleFittingsBean b = null;
			voProduct fitting = service.getProduct(StringUtil.toSql(ids[0]));
			if(fitting == null){
				this.dbOp.rollbackTransaction();
				j.setMsg("不存在编号【" + ids[0] + "】这样的配件!");
				return j;
			}
			for(int i = 1; i < ids.length; i++){
				Set<Integer> set = new HashSet<Integer>();
				List<AfterSaleFittingsBean> list = this.getAfterSaleFittingList("fitting_id=" + fitting.getId(), -1, -1, null);
				for(AfterSaleFittingsBean bean : list){
					set.add(bean.getProductId());
				}
				voProduct product = service.getProduct(StringUtil.toSql(ids[i]));
				if(product == null){
					this.dbOp.rollbackTransaction();
					j.setMsg("不存在编号【" + ids[i] + "】这样的商品!");
					return j;
				}
				if(!set.contains(product.getId())){
					b = new AfterSaleFittingsBean();
					b.setFittingId(fitting.getId());
					b.setProductId(product.getId());
					if(!this.addAfterSaleFittings(b)){
						this.dbOp.rollbackTransaction();
						j.setMsg("导入失败(数据库添加异常)!");
						return j;
					}
				}
			}
		}
		this.dbOp.commitTransaction();
		j.setMsg("导入成功(如果有重复的配件，那么只保留第一个，已关联的商品部会再关联)!");
		return j;
	}
	public boolean addAfterSaleFittings(AfterSaleFittingsBean bean){
		return addXXX(bean, "after_sale_fittings");
	}
	
	public boolean deleteAfterSaleFittings(String condition){
		return deleteXXX(condition, "after_sale_fittings");
	}
	
	public Json editMatchProduct(String ids, String oldIds,String fittingId) {
		Json j = new Json();
		Set<String> set = new HashSet<String>();
		this.dbOp.startTransaction();
		if("".equals(ids)){
			for(String oldId : oldIds.split(",")){
				if(!this.deleteAfterSaleFittings("fitting_id=" + fittingId + " and product_id=" + oldId)){
					this.dbOp.rollbackTransaction();
					j.setMsg("匹配商品更新失败!");
					return j;
				}
			}
		} else {
			for(String id : ids.split(",")){
				set.add(id);
			}
			for(String oldId : oldIds.split(",")){
				if(!set.contains(oldId)){
					if(!this.deleteAfterSaleFittings("fitting_id=" + fittingId + " and product_id=" + oldId)){
						this.dbOp.rollbackTransaction();
						j.setMsg("匹配商品更新失败!");
						return j;
					}
				}
			}
		}
		this.dbOp.commitTransaction();
		j.setSuccess(true);
		return j;
	}

	public List<EasyuiComBoBoxBean> searchMatchProduct(String searchContent,String oldIds) {
		List<EasyuiComBoBoxBean> comboboxList = new ArrayList<EasyuiComBoBoxBean>();
		Set<String> set = new HashSet<String>();
		for(String oldId : StringUtil.checkNull(oldIds).split(",")){
			set.add(oldId);
		}
		if(!"".equals(StringUtil.checkNull(searchContent))){
			List<Map<String,String>> productMaps = this.getProductList(" name like '%" + searchContent + "%'");
			EasyuiComBoBoxBean bean = null;
			for(Map<String,String> productMap : productMaps){
				if(!set.contains(productMap.get("productId"))){//从搜索结果中过滤掉已关联的商品ID
					bean = new EasyuiComBoBoxBean();
					bean.setId(productMap.get("productId"));
					bean.setText(productMap.get("productName"));
					comboboxList.add(bean);
				}
			}
		}
		return comboboxList;
	}

	public Json addMatchProduct(String ids, String fittingId) {
		Json j = new Json();
		this.dbOp.startTransaction();
		AfterSaleFittingsBean bean = null;
		for(String id : ids.split(",")){
			bean = new AfterSaleFittingsBean();
			bean.setFittingId(StringUtil.toInt(fittingId));
			bean.setProductId(StringUtil.toInt(id));
			AfterSaleFittingsBean b = this.getAfterSaleFittingBean("product_id = " + bean.getProductId() + " and fitting_id=" + bean.getFittingId());
			if(b == null){
				if(!this.addAfterSaleFittings(bean)){
					this.dbOp.rollbackTransaction();
					j.setMsg("关联失败!");
					return j;
				}
			}
		}
		this.dbOp.commitTransaction();
		j.setMsg("关联成功!");
		return j;
	}
	
	public AfterSaleFittingsBean getAfterSaleFittingBean(String condition) {
		return (AfterSaleFittingsBean) getXXX(condition, "after_sale_fittings", "mmb.stock.aftersale.getAfterSaleFittingBean");
	}


	/**
	 * 获取相关配件的名称
	 * @param condition
	 * @return
	 * @author 李宁
	* @date 2014-5-26
	 */
	public String getAfterSaleFittings(String condition){
		String fittingNames = "";
		try {
			String query = "SELECT group_concat(f.`name`) " +
					"FROM product AS f " +
					"INNER JOIN after_sale_fittings AS asf ON f.id = asf.fitting_id " +
					"INNER JOIN product AS p ON asf.product_id = p.id ";
			if(!"".equals(StringUtil.checkNull(condition))){
				query += " WHERE " + condition;
			}
			ResultSet rs = this.dbOp.executeQuery(query);
			if(rs!=null && rs.next()){
				fittingNames = rs.getString(1);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fittingNames;
	}
	/**
	 * 历史调拨查询Count
	 * @param condition
	 * @author syuf
	 */
	public int getAfterSaleExchangeCount(String condition) {
		int total = 0;
		try {
			String query = "SELECT count(se.id) " +
					"FROM after_sale_stock_exchange_product AS assep " +
					"INNER JOIN after_sale_detect_product AS asdp ON assep.after_sale_detect_product_id = asdp.id " +
					"INNER JOIN product AS p ON asdp.product_id = p.id " +
					"INNER JOIN stock_exchange AS se ON se.id = assep.stock_exchange_id " +
					"WHERE "+ condition;
			ResultSet rs = this.dbOp.executeQuery(query);
			if(rs.next()){
				total = rs.getInt(1);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return total;
	}
	/**
	 * 历史调拨查询List
	 * @param condition
	 * @param index
	 * @param count
	 * @param order
	 * @author syuf
	 */
	public List<Map<String, String>> getAfterSaleExchangeList(String condition,int index, int count, String order) {
		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
		try {
			String query = "SELECT p.oriname as productOriName,asdp.`code` as afterSaleDetectCode,se.`code` as exchangeCode," +
					"se.create_datetime as createDatetime,se.`status`,se.create_user_name as  createUserName,se.auditing_user_name as auditUserName," +
					"p.code,se.stock_out_type,se.stock_out_area,se.stock_in_type,se.stock_in_area " +
					"FROM after_sale_stock_exchange_product AS assep " +
					"INNER JOIN after_sale_detect_product AS asdp ON assep.after_sale_detect_product_id = asdp.id " +
					"INNER JOIN product AS p ON asdp.product_id = p.id " +
					"INNER JOIN stock_exchange AS se ON se.id = assep.stock_exchange_id " +
					"WHERE "+ condition;
			if(!"".equals(StringUtil.checkNull(order))){
				query += " order by"  + order;
			}
			query = DbOperation.getPagingQuery(query, index, count);
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs.next()){
				Map<String,String> map = new HashMap<String, String>();
				map.put("productOriName", rs.getString(1));
				map.put("afterSaleDetectCode", rs.getString(2));
				map.put("exchangeCode", rs.getString(3));
				map.put("createDatetime", rs.getString(4));
				map.put("status", rs.getInt(5)  + "");
				map.put("createUserName", rs.getString(6));
				map.put("auditUserName", rs.getString(7));
				map.put("productCode", rs.getString(8));
				map.put("sourceExchange", ProductStockBean.getStockTypeName(rs.getInt(9)) + "("  + ProductStockBean.getAreaName(rs.getInt(10)) + ")");
				map.put("targeExchange", ProductStockBean.getStockTypeName(rs.getInt(11)) + "("  + ProductStockBean.getAreaName(rs.getInt(12)) + ")");
				maps.add(map);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maps;
	}
	/**
	 * 根据 id查询 2级和 3级分类
	 * 
	 */
	public List<voCatalog> getCatalogNames(int parentId) {
		List<voCatalog> listcatalogs=null;
		WareService service = new WareService();
		try {
			listcatalogs=service.getCatalogs("parent_id="+parentId+" and hide=0");
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
    		service.releaseAll();
    	}
		return listcatalogs;
	}

	/**
	 * 获取售后、用户货位配件类表
	 * @param condition
	 * @param index
	 * @param count
	 * @param groupBy
	 * @param orderBy
	 * @return
	 * @author lining
	* @date 2014-7-3
	 */
	public List<Map<String, String>> getFittingCargoStockList(String condition, int index, int count) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		StringBuffer sql = new StringBuffer();
		sql.append("select p.id,p.name,p.code,ci.whole_code,sum(cps.stock_count),sum(cps.stock_lock_count),ci.type from product p ")
			.append("join cargo_product_stock cps on p.id=cps.product_id join cargo_info ci on ci.id=cps.cargo_id ")
			.append(" join cargo_info_shelf cis on ci.shelf_id=cis.id where p.parent_id1=").append(AfterSaleFittings.FITTING_PARENT1_ID);
		if(!"".equals(StringUtil.checkNull(condition))){
			sql.append(condition);
		}
		sql.append(" group by p.id,ci.id,ci.type order by ci.id");
		String query = DbOperation.getPagingQuery(sql.toString(), index, count);
		try{
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs!=null && rs.next()){
				Map<String, String> row = new HashMap<String, String>();
				row.put("fittingId", rs.getInt(1)+"");
				row.put("fittingName", rs.getString(2));
				row.put("fittingCode",rs.getString(3));
				row.put("cargoCode",rs.getString(4));
				row.put("stockCount", rs.getInt(5) + "(" + rs.getInt(6) + ")");
				int type = rs.getInt(7);
				int fittingType = FittingBuyStockInBean.FITTING_TYPE1;
				if(type==CargoInfoBean.TYPE4){
					fittingType = FittingBuyStockInBean.FITTING_TYPE2;
				}else if(type==CargoInfoBean.TYPE5){
					fittingType = FittingBuyStockInBean.FITTING_TYPE3;
				}
				String fittingTypeName = FittingBuyStockInBean.fittingTypeMap.get((byte)fittingType);
				row.put("fittingType", fittingType+"");
				row.put("fittingTypeName", fittingTypeName);
				result.add(row);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int getFittingCargoStockListSize(String condition) {
		int count = 0 ;
		StringBuffer sql = new StringBuffer();
		sql.append("select count(p.id) from product p ")
			.append("join cargo_product_stock cps on p.id=cps.product_id join cargo_info ci on ci.id=cps.cargo_id ")
			.append(" join cargo_info_shelf cis on ci.shelf_id=cis.id where 1=1 ");
		if(!"".equals(StringUtil.checkNull(condition))){
			sql.append(condition);
		}
		sql.append(" group by p.id,ci.id order by ci.id");
		try{
			ResultSet rs = this.dbOp.executeQuery(sql.toString());
			if(rs!=null && rs.next()){
				count = rs.getInt(1);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 根据售后处理单商品查找相应的配件信息
	 * @param afterSaleDetectProductId
	 * @return
	 * @author lining
	* @date 2014-7-11
	 */
	public List<Map<String,Integer>> getAfterSaleDetectFitting(int afterSaleDetectProductId){
		List<Map<String,Integer>> result = new ArrayList<Map<String,Integer>>();
		String query = "select fitting_id,intact_count,damage_count from after_sale_detect_product_fitting where 1=1 ";
		if(afterSaleDetectProductId>0){
			query += " and after_sale_detect_product_id=" + afterSaleDetectProductId;
		}
		try{
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs!=null && rs.next()){
				Map<String,Integer> row = new HashMap<String,Integer>();
				row.put("fittingId", rs.getInt(1));
				row.put("intactCount",rs.getInt(2));
				row.put("damageCount", rs.getInt(3));
				result.add(row);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 导入包裹单号
	 * @author syuf
	 */
	public String addAfterSalePackageCode(String deliverId, String packageCodes) {
		if("".equals(deliverId)){
			return "快递公司ID不能为空!";
		}
		if("".equals(packageCodes)){
			return "包裹单号不能为空!";
		}
		this.dbOp.startTransaction();
		for(String s : packageCodes.split(",")){
			AfterSalePackageCodeBean bean = this.getAfterSalePackageCode("deliver_id = " + deliverId + " AND code = '" + s + "'");
			//已经使用过的包裹单号不允许再导入
			AfterSaleBackUserPackage packageBean = this.getAfterSaleBackUserPackage("deliver_id = " + deliverId + " AND package_code = '" + s + "'");
			if(bean == null && packageBean==null){
				bean = new AfterSalePackageCodeBean();
				bean.setCode(s.trim());
				bean.setDeliverId(StringUtil.toInt(deliverId));
				bean.setStatus(1);
				if(!this.addAfterSalePackageCode(bean)){
					this.dbOp.rollbackTransaction();
					return "导入失败,[" + s + "]导入该包裹单号时出错!";
				}
			}else{
				return "该包裹单号已经导入或使用过，不能再次导入!";
			}
		}
		this.dbOp.commitTransaction();
		return null;
	}
	/**
	 * 寄回用户
	 * @return 以","分割 前面是错误信息 后面是包裹单号
	 * @author syuf
	 */
	public String addOriginalOrRepairBackPackage(List<String> codeList, float freight, int deliverId,String phone
			,float weight,String address, String customerName, voUser user, String remark) {
		AfterSalePackageCodeBean packageCodeBean = this.getAfterSalePackageCode("deliver_id=" + deliverId + " AND status = 1");
		if(packageCodeBean == null){
			return "没用可用包裹单号,先导入一些吧!";
		}
		CommonLogic logic = new CommonLogic();
		String result = logic.backUser(codeList, packageCodeBean.getCode(), freight, deliverId, phone, weight, address, customerName, user,remark,1);
		if(result != null){
			return result;
		}
		if(!this.updateAfterSalePackageCode("status=2","id=" + packageCodeBean.getId())){
			return "包裹单状态更新失败!";
		}
		return "#" + packageCodeBean.getCode();
	}

	public boolean updateAfterSalePackageCode(String set, String condition) {
		return updateXXX(set, condition, "after_sale_package_code");
	}

	public AfterSalePackageCodeBean getAfterSalePackageCode(String condition) {
		return (AfterSalePackageCodeBean) getXXX(condition, "after_sale_package_code", "mmb.stock.aftersale.AfterSalePackageCodeBean");
	}

	public boolean addAfterSalePackageCode(AfterSalePackageCodeBean bean) {
		return addXXX(bean, "after_sale_package_code");
	}

	public AfterSaleOrderBean getAfterSaleOrder(String condition) {
		return (AfterSaleOrderBean) getXXX(condition, "after_sale_order", "adultadmin.bean.afterSales.AfterSaleOrderBean");
	}
	
	/**
	 * 说明：写售后日志
	 * @param user 用户
	 * @param content 日志内容
	 * @param count 操作数量
	 * @param type 日志类型
	 * @param dateTime 记录日志时间 如果为null或者空则是当前时间
	 * @return boolean
	 * @author syuf
	 */
	public boolean writeAfterSaleLog(voUser user,String content,int count,int type,String operCode,String dateTime){
		if("".equals(StringUtil.checkNull(dateTime))){
			dateTime = DateUtil.getNow();
		}
		AfterSaleLogBean log = new AfterSaleLogBean();
		log.setContent(content);
		log.setCount(count);
		log.setCreateDatetime(dateTime);
		log.setCreateUserId(user.getId());
		log.setCreateUserName(user.getUsername());
		log.setType(type);
		log.setOperCode(operCode);
		return addAfterSaleLogBean(log);
	}
	
	public boolean addAfterSaleLogBean(AfterSaleLogBean log){
		return addXXX(log, "after_sale_log");
	}

	public boolean updateAfterSaleLog(String set, String condition) {
		return updateXXX(set, condition, "after_sale_log");
	}

	@SuppressWarnings("unchecked")
	public List<AfterSaleLogBean> getAfterSaleLogList(String condition,int index,int count,String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_log", "mmb.stock.aftersale.AfterSaleLogBean");
	}

	public int getAfterSaleLogCount(String condition) {
		return getXXXCount(condition, "after_sale_log", "id");
	}

	public AfterSaleLogBean getAfterSaleLog(String condition) {
		return (AfterSaleLogBean) getXXX(condition, "after_sale_log","mmb.stock.aftersale.AfterSaleLogBean");
	}
	
	public String getStockExchangeInWholeCodeList(String condition) {
		String wholeCode = null;
		String query = "SELECT ci.whole_code " +
				"FROM stock_exchange AS se " +
				"INNER JOIN stock_exchange_product AS sep ON sep.stock_exchange_id = se.id " +
				"INNER JOIN stock_exchange_product_cargo AS sepc ON sepc.stock_exchange_product_id = sep.id AND sepc.type= 1 " +
				"INNER JOIN cargo_info AS ci ON ci.id = sepc.cargo_info_id " +
				"Where " + condition;
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			if(rs != null && rs.next()){
				wholeCode = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return wholeCode;
	}
	
	public AfterSaleBackSupplierProductReplace getAfterSaleBackSupplierProductReplace(String condition) {
		return (AfterSaleBackSupplierProductReplace) getXXX(condition,"after_sale_back_supplier_product_replace","mmb.stock.aftersale.AfterSaleBackSupplierProductReplace");
	}

	
	public List<AfterSaleMatchFailPackageProduct> getMatchFailPackageProducts(String condition,int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,"after_sale_match_fail_package_product","mmb.stock.aftersale.AfterSaleMatchFailPackageProduct");
	}
	
	public boolean addMatchFailPackageProduct(AfterSaleMatchFailPackageProduct bean){
		return addXXX(bean, "after_sale_match_fail_package_product");
	}
	
	public boolean deleteMatchFailPackageProduct(String condition){
		return deleteXXX(condition, "after_sale_match_fail_package_product");
	}


	public boolean updateAfterSaleBackSupplierProductReplace(String set, String condition) {
		return updateXXX(set, condition, "after_sale_back_supplier_product_replace");
	}

	public boolean deleteAfterSaleBackSupplierProductReplace(String condition) {
		return deleteXXX(condition, "after_sale_back_supplier_product_replace");
	}

	public boolean addAfterSaleBackSupplierProductReplace(
			AfterSaleBackSupplierProductReplace bean) {
		return addXXX(bean, "after_sale_back_supplier_product_replace");
	}
	
	public List getAfterSaleBackSupplierProductReplaceList(String condition,int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,"after_sale_back_supplier_product_replace","mmb.stock.aftersale.AfterSaleBackSupplierProductReplace");
	}

	public List<Map<String, String>> getAftersaleBacksupplierProductReplaceMaps(String condition, int index, int count, String orderBy) {
		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
		StringBuffer buff =new StringBuffer();
		try {
			buff.append("SELECT asdp.`code`,asbspr.`code`,asbspr.id,asbspr.old_imei,asbspr.new_imei,op.`code`,op.`name`,");
			buff.append("np.`code`,np.`name`,asbspr.supplier_id,asbspr.supplier_name,asbspr.create_datetime,asbspr.create_user_name,");
			buff.append("asbsp.`status`,asbspr.audit_status,asbspr.old_price,asbspr.new_price,asbspr.audit_datetime,asbspr.audit_user_name,np.price5,asbspr.id,op.id,np.id"
					+ ",asbspr.old_notax_price,asbspr.new_notax_price,fp.notax_price ");
			buff.append("FROM after_sale_back_supplier_product_replace AS asbspr ");
			buff.append("INNER JOIN after_sale_detect_product AS asdp ON asbspr.detect_id = asdp.id ");
			buff.append("INNER JOIN after_sale_back_supplier_product AS asbsp ON asbspr.back_supplier_product_id = asbsp.id AND asbspr.detect_id = asbsp.after_sale_detect_product_id ");
			buff.append("INNER JOIN product AS op ON asbspr.old_product_id = op.id ");
			buff.append("INNER JOIN product AS np ON asbspr.new_product_id = np.id ");
			buff.append("INNER JOIN finance_product AS fp ON asbspr.new_product_id = fp.product_id ");
			buff.append("WHERE ");
			buff.append(condition);
			if(!"".equals(StringUtil.checkNull(orderBy))){
				buff.append(" order by ");
				buff.append(orderBy);
			}
			//System.out.println(buff.toString());
			String query = DbOperation.getPagingQuery(buff.toString(), index, count);
			ResultSet rs = this.dbOp.executeQuery(query);
			while(rs.next()){
				Map<String,String> map = new HashMap<String, String>();
				
				double oldPrice = rs.getDouble(16);
				double newPrice = rs.getDouble(17);
				double nowPrice = rs.getDouble(20);
				
				double oldNotaxPrice = rs.getDouble(24);
				double newNotaxPrice = rs.getDouble(25);
				double nowNotaxPrice = rs.getDouble(26);
				
				if(oldNotaxPrice == 0){
					oldNotaxPrice = new BigDecimal(Double.toString(oldPrice)).divide(new BigDecimal(Double.toString(1.7)), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
				}else{
					oldNotaxPrice = new BigDecimal(Double.toString(rs.getDouble(24))).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				
				if(newNotaxPrice == 0){
					newNotaxPrice = new BigDecimal(Double.toString(newPrice)).divide(new BigDecimal(Double.toString(1.7)), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
				}else{
					newNotaxPrice = new BigDecimal(Double.toString(rs.getDouble(25))).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				
				if(nowNotaxPrice == 0){
					nowNotaxPrice = new BigDecimal(Double.toString(nowPrice)).divide(new BigDecimal(Double.toString(1.7)), 4, BigDecimal.ROUND_HALF_UP).doubleValue();
				}else{
					nowNotaxPrice = new BigDecimal(Double.toString(rs.getDouble(26))).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				
				map.put("detectCode", rs.getString(1));
				map.put("replaceCode", rs.getString(2));
				map.put("replaceId", rs.getInt(3) + "");
				map.put("oldImei", rs.getString(4));
				map.put("newImei", rs.getString(5));
				map.put("oldProductCode", rs.getString(6));
				map.put("oldProductName", rs.getString(7));
				map.put("newProductCode", rs.getString(8));
				map.put("newProductName", rs.getString(9));
				map.put("supplierId", rs.getInt(10) + "");
				map.put("supplierName", rs.getString(11));
				map.put("createDatetime", rs.getString(12));
				map.put("createUserName", rs.getString(13));
				map.put("status", rs.getInt(14) + "");
				map.put("statusName", AfterSaleBackSupplierProduct.statusMap.get(rs.getInt(14)));
				map.put("auditStatus", rs.getInt(15) + "");
				map.put("auditStatusName", AfterSaleBackSupplierProductReplace.auditStatusMap.get(rs.getInt(15)));
				map.put("oldPrice", oldPrice + "("+oldNotaxPrice+")");
				map.put("newPrice", newPrice + "("+newNotaxPrice+")");
				map.put("auditDatetime", rs.getString(18));
				map.put("auditUserName", rs.getString(19));
				map.put("nowPrice", nowPrice + "("+nowNotaxPrice+")");
				map.put("id", rs.getInt(21) + "");
				map.put("oid", rs.getInt(22) + "");
				map.put("nid", rs.getInt(23) + "");
				maps.add(map);
			}
			if(rs != null){
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maps;
	}

	public int getAftersaleBacksupplierProductReplaceTotal(String condition) {
		int count = 0 ;
		StringBuffer buff =new StringBuffer();
		try {
			buff.append("SELECT count(asbspr.id) ");
			buff.append("FROM after_sale_back_supplier_product_replace AS asbspr ");
			buff.append("INNER JOIN after_sale_detect_product AS asdp ON asbspr.detect_id = asdp.id ");
			buff.append("INNER JOIN after_sale_back_supplier_product AS asbsp ON asbspr.back_supplier_product_id = asbsp.id AND asbspr.detect_id = asbsp.after_sale_detect_product_id ");
			buff.append("INNER JOIN product AS op ON asbspr.old_product_id = op.id ");
			buff.append("INNER JOIN product AS np ON asbspr.new_product_id = np.id ");
			buff.append("WHERE ");
			buff.append(condition);
			ResultSet rs = this.dbOp.executeQuery(buff.toString());
			if(rs!=null && rs.next()){
				count = rs.getInt(1);
			}
			if(rs != null){
				rs.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	/**
	 * 审核更换单
	 * @param dbOp 数据源
	 * @param flag 标识是否审核通过 2通过 3不通过
	 * @param id 更换单ID
	 * @author syuf
	 */
	public String auditBackSuppilerReplace(DbOperation dbOp,voUser user, String flag, String id) {
		WareService service = new WareService(dbOp);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ProductStockServiceImpl productStockService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		CargoServiceImpl cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		StockServiceImpl stockService = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE,dbOp);
		StockCommon stockCommon = new StockCommon();
		//校验更换单是否存在
		AfterSaleBackSupplierProductReplace replace = this.getAftersaleBacksupplierProductReplace("id=" + id);
		if(replace == null){
			return "该更换单不存在！";
		}
		if(replace.getAuditStatus() != AfterSaleBackSupplierProductReplace.AUDIT_STATUS1){
			return "更换单状态不是待审核!";
		}
		//校验更换单关联的处理单是否存在
		AfterSaleDetectProductBean detectProduct = this.getAfterSaleDetectProduct("id=" + replace.getDetectId()); 
		if(detectProduct == null){
			return "该更换单关联的售后处理单不存在!";
		}
		//校验处理单关联的货位是否存在
		CargoInfoBean cargoInfo  = cargoService.getCargoInfo("whole_code='" + detectProduct.getCargoWholeCode() + "'");
		if(cargoInfo == null){
			return "处理单关联的货位不存在!";
		}
		//更换单的更新条件拼写
		StringBuffer set = new StringBuffer();
		String auditDatetime = DateUtil.getNow();
		set.append("audit_status =" + flag);
		set.append(",audit_user_id=" + user.getId());
		set.append(",audit_user_name='" + user.getUsername() + "'");
		set.append(",audit_datetime='" + auditDatetime +"' ");
		
		//记录更换单审核人及时间
		replace.setAuditDatetime(auditDatetime);
		replace.setAuditUserId(user.getId());
		replace.setAuditUserName(user.getUsername());
		
		//审核通过
		//获取新旧商品的库存均价
		voProduct oldproduct =  service.getProduct(replace.getOldProductId());
		if(oldproduct == null){
			return "找不到更换单关联的旧商品";
		}
		StringBuffer detectSet = new StringBuffer();
		if("2".equals(flag)){
			set.append(",old_price=" + oldproduct.getPrice5());
			voProduct newproduct =  service.getProduct(replace.getNewProductId());
			if(newproduct == null){
				return "找不到更换单关联的新商品";
			}
			set.append(",new_price=" + newproduct.getPrice5());
			
			//获取不含税价格
			List<FinanceProduct> notaxPrice = this.getNotaxPriceFromFinanceProduct("product_id in("+oldproduct.getId()+","+newproduct.getId()+")");
			double oldNotaxPrice = 0;
			double newNotaxPrice = 0;
			if(null != notaxPrice && notaxPrice.size() > 0){
				for (FinanceProduct notax : notaxPrice) {
					
					if(notax.getProductId() == oldproduct.getId()){
						oldNotaxPrice = notax.getNotaxPrice();
					}
					if(notax.getProductId() == newproduct.getId()){
						newNotaxPrice = notax.getNotaxPrice();
					}
				}
			}
			
			set.append(",old_notax_price=" + oldNotaxPrice);
			set.append(",new_notax_price=" + newNotaxPrice);
			
			
			//旧商品库存及货位存库修改
			ProductStockBean oldStock = productStockService.getProductStock("product_id=" + replace.getOldProductId() + " and lock_count > 0 and area=" + detectProduct.getAreaId() + " and type=" + ProductStockBean.STOCKTYPE_AFTER_SALE);
			if(oldStock == null){
				return "找不到可用的原商品库存!";
			}
			CargoProductStockBean oldCargoStock = cargoService.getCargoProductStock("product_id=" + replace.getOldProductId() + " and cargo_id=" + cargoInfo.getId() + " and stock_lock_count > 0 ");
			if(oldCargoStock == null){
				return "找不到可用原商品货位库存!";
			}
			String condition = "product_id=" + replace.getOldProductId() + " and stock_type=" + ProductStockBean.STOCKTYPE_AFTER_SALE + " and stock_area=" + detectProduct.getAreaId();
			StockBatchBean batch = stockService.getStockBatch(condition);
			if(batch == null){
				return "原商品库存批次不存在!";
			}
			//新商品库存及货位存库修改
			ProductStockBean newStock = productStockService.getProductStock("product_id=" + replace.getNewProductId()  + " and area=" + detectProduct.getAreaId() + " and type=" + ProductStockBean.STOCKTYPE_AFTER_SALE);
			if(newStock == null){
				return "新商品库存信息不存在!";
			}
			CargoProductStockBean newCargoStock =cargoService.getCargoProductStock("product_id=" + replace.getNewProductId() + " and cargo_id=" + cargoInfo.getId());
			synchronized(lock){
			//更新审核通过的更换单
			if(!this.updateAfterSaleBackSupplierProductReplace(set.toString(),"id=" + id)){
				return "更新更换单审核状态失败!";
			}
//			if(!productStockService.updateProductStock("lock_count=" + (oldStock.getLockCount() - 1), "id=" + oldStock.getId())){
//				return "更新原商品库存失败!";
//			}
//			if(!cargoService.updateCargoProductStock("stock_lock_count=" + (oldCargoStock.getStockLockCount() - 1), "id=" + oldCargoStock.getId())){
//				return "更新原商品货位库存失败!";
//			}
//			//更新库存批次及添加库存批次操作日志_出库
//			int batchCount = stockCommon.updateStockBatch(dbOp, user, batch, 1, replace.getCode(), "厂家维修更换商品出库");
//			if(batchCount < 0){
//				return "更新库存批次及添加库存批次操作日志失败_出库";
//			}
//			//财务进销存卡片_出库
//			String result = stockCommon.addFinanceStockCardOut(dbOp, batch, oldStock.getId(), batchCount, StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_OUT, replace.getCode());
//			if(result != null){
//				return result;
//			}
//			//库存进销存卡片_出库
//			result = stockCommon.addStockCardOut(dbOp, 1, replace.getOldProductId(), oldCargoStock.getCargoId(), StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_OUT, replace.getCode(), batch.getStockType(), batch.getStockArea());
//			if(result != null){
//				return result;
//			}
			
//			if(!productStockService.updateProductStock("stock=" + (newStock.getStock() + 1), "id=" + newStock.getId())){
//				return "更新新商品库存失败!";
//			}
			
			if(newCargoStock == null){
				CargoProductStockBean bean = new CargoProductStockBean();
				bean.setCargoId(cargoInfo.getId());
				bean.setProductId(replace.getNewProductId());
				bean.setStockCount(0);
				bean.setStockLockCount(0);
				if(!cargoService.addCargoProductStock(bean)){
					return "添加新商品货位库存失败!";
				}
				newCargoStock =cargoService.getCargoProductStock("product_id=" + replace.getNewProductId() + " and cargo_id=" + cargoInfo.getId());
			}
//			else {
//				if(!cargoService.updateCargoProductStock("stock_count=" + (newCargoStock.getStockCount() + 1), "id=" + newCargoStock.getId())){
//					return "更新新商品货位库存失败!";
//				}
//			}
//			condition = "product_id=" + replace.getNewProductId() + " and stock_type=" + ProductStockBean.STOCKTYPE_AFTER_SALE + " and stock_area=" + detectProduct.getAreaId();
//			StockBatchBean batchIn = stockService.getStockBatch(condition);
//			if(batchIn == null){
//				result = stockCommon.addStockBatch(dbOp, user, replace.getNewProductId(), 1, detectProduct.getAreaId(), ProductStockBean.STOCKTYPE_AFTER_SALE, replace.getCode(), "");
//				if(result != null){
//					return result;
//				}
//			} else {
//				batchCount = stockCommon.updateStockBatch(dbOp, user, batchIn, -1, replace.getCode(), "厂家维修更换商品入库");
//				//更新库存批次及添加库存批次操作日志
//				if(batchCount < 0){
//					return "更新库存批次及添加库存批次操作日志失败_入库";
//				}
//			}
//			batchIn = stockService.getStockBatch(condition);
//			//财务进销存卡片_入库
//			result = stockCommon.addFinanceStockCardIn(dbOp, batchIn, newStock.getId(), 1, StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_IN, replace.getCode());
//			if(result != null){
//				return result;
//			}
//			//库存进销存卡片_入库
//			result = stockCommon.addStockCardIn(dbOp, 1, replace.getNewProductId(), newCargoStock.getCargoId(), StockCardBean.CARDTYPE_SUPPLIER_PRODUCT_REPLACE_IN, replace.getCode(), batch.getStockType(), batch.getStockArea());
//			if(result != null){
//				return result;
//			}
				//更新IMEI
				IMEIBean imeiBean = imeiService.getIMEI("product_id=" +  newproduct.getId() + " and code='" + replace.getNewImei() + "'");
				String result = this.recordNewImei(replace.getOldImei(), replace.getNewImei(), user, imeiService, newproduct.getId(), imeiBean);
				if(result != null){
					return result;
				}
				if(!"".equals(StringUtil.checkNull(replace.getOldImei()))){
					result = this.recordOldImei(replace.getOldImei(), replace.getNewImei(), user, imeiService, detectProduct.getProductId(), false);
					if(result != null){
						return result;
					}
				}
				//更新售后处理单中关联的商品	商品id 	imei 
				detectSet.append("product_id=" + newproduct.getId()).append(",lock_status=" + AfterSaleDetectProductBean.LOCK_STATUS0);
				detectSet.append(",IMEI='" + StringUtil.checkNull(replace.getNewImei()) + "'");
				if(!this.updateAfterSaleDetectProduct(detectSet.toString(), "id=" + detectProduct.getId())){
					return "更新更换单关联的处理单失败!";

				}
				
				//添加售后日志
				String logContent = "厂家维修更换商品更新处理单[" + detectProduct.getCode() + "] 内容[" + set.toString() + "]";
				//System.out.println(logContent);
				if(!this.writeAfterSaleLog(user, logContent, 1, AfterSaleLogBean.TYPE17, detectProduct.getCode(), "")){
					return "添加售后日志失败!";
				}
				
				//更新返厂单 商品id oriName imei
				StringBuffer backSupplierSet = new StringBuffer();
				backSupplierSet.append("product_id=" + newproduct.getId() + ",product_oriname='" + newproduct.getOriname() + "'");
				backSupplierSet.append(",IMEI='" + StringUtil.checkNull(replace.getNewImei()) + "'");
				if(!this.updateAfterSaleBackSupplierProduct(backSupplierSet.toString(), "id=" + replace.getBackSupplierProductId())){
					return "更新返厂单失败!";
				}
				
				//更新售后那边的处理单
				StringBuffer warehourceSet = new StringBuffer();
				warehourceSet.append("product_id=" + newproduct.getId()).append(",IMEI='" + StringUtil.checkNull(replace.getNewImei()) + "'");
				if(!this.updateAfterSaleWarehourceProductRecords(warehourceSet.toString(),"id=" + detectProduct.getId())){
					return "更新售后项目的处理单失败";
				}

			
				//返厂维修更换单审核通过时系统自动初始化生成状态为已完成的报损报溢单 #2536 2015-02-25
				IBsByServiceManagerService bsbyService = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
				String nowTime = DateUtil.getNow();
				
				BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();				
				bsbyOperationnoteBean.setOperator_id(replace.getCreateUserId());//操作人id（添加）
				bsbyOperationnoteBean.setOperator_name(replace.getCreateUserName());//操作人
				bsbyOperationnoteBean.setAdd_time(replace.getCreateDatetime());//添加时间				
				bsbyOperationnoteBean.setEnd_oper_id(replace.getAuditUserId());//完成操作人id(运营)
				bsbyOperationnoteBean.setEnd_oper_name(replace.getAuditUserName());//最后修改人
				bsbyOperationnoteBean.setEnd_time(replace.getAuditDatetime());//完成时间（运营）
				bsbyOperationnoteBean.setFinAuditId(replace.getAuditUserId());//财务审核人id
				bsbyOperationnoteBean.setFinAuditName(replace.getCreateUserName());//财务审核人姓名
				bsbyOperationnoteBean.setFinAuditDatetime(replace.getAuditDatetime());//财务审核时间
				bsbyOperationnoteBean.setFinAuditRemark("");//财务审核意见			
				bsbyOperationnoteBean.setCurrent_type(BsbyOperationnoteBean.audit_end);//状态:已完成
				bsbyOperationnoteBean.setWarehouse_area(detectProduct.getAreaId());//库地区
				bsbyOperationnoteBean.setWarehouse_type(ProductStockBean.STOCKTYPE_AFTER_SALE);//库类型
				bsbyOperationnoteBean.setIf_del(0);//删除状态
				bsbyOperationnoteBean.setRemark("厂家维修更换产品编号（售后专用）");//注释
				
				//报损
				String code = "BS" + nowTime.substring(0, 10).replace("-", "");
				code = ByBsAction.createCode(code);// BS+年月日+4位自动增长数
				bsbyOperationnoteBean.setType(0);//报损
				bsbyOperationnoteBean.setReceipts_number(code);//编码
				String resultStr = addBsbyOperationnoteAndProduct(bsbyService,bsbyOperationnoteBean,detectProduct,oldproduct,oldCargoStock,String.valueOf(oldNotaxPrice),user,false);
				if(resultStr != null && !"".equals(resultStr)){
					return resultStr;
				}
				
				detectProduct.setIMEI(replace.getNewImei());
				detectProduct.setProductId(newproduct.getId());
				
				//报溢
				code = "BY" + nowTime.substring(0, 10).replace("-", "");
				code = ByBsAction.createCode(code);// BY+年月日+4位自动增长数
				bsbyOperationnoteBean.setType(1);//报溢
				bsbyOperationnoteBean.setReceipts_number(code);//编码
				resultStr = addBsbyOperationnoteAndProduct(bsbyService,bsbyOperationnoteBean,detectProduct,newproduct,newCargoStock,String.valueOf(newNotaxPrice),user,false);
				if(resultStr != null && !"".equals(resultStr)){
					return resultStr;
				}
			}
		} else if("3".equals(flag)){
			detectSet.append(" lock_status=" + AfterSaleDetectProductBean.LOCK_STATUS0);
			//更新处理单
			if(!this.updateAfterSaleDetectProduct(detectSet.toString(), "id=" + detectProduct.getId())){
				return "更新更换单关联的处理单失败!";
			}
			//旧商品库存及货位存库修改
			ProductStockBean oldStock = productStockService.getProductStock("product_id=" + replace.getOldProductId() + " and lock_count > 0 and area=" + detectProduct.getAreaId() + " and type=" + ProductStockBean.STOCKTYPE_AFTER_SALE);
			if(oldStock == null){
				return "找不到可用的原商品库存!";
			}
			if(!productStockService.updateProductStock("lock_count=" + (oldStock.getLockCount() - 1) + ",stock=" + (oldStock.getStock() + 1) , "id=" + oldStock.getId())){
				return "更新原商品库存失败!";
			}
			CargoProductStockBean oldCargoStock = cargoService.getCargoProductStock("product_id=" + replace.getOldProductId() + " and cargo_id=" + cargoInfo.getId() + " and stock_lock_count > 0 ");
			if(oldCargoStock == null){
				return "找不到可用原商品货位库存!";
			}
			if(!cargoService.updateCargoProductStock("stock_lock_count=" + (oldCargoStock.getStockLockCount() - 1) + ",stock_count=" + (oldCargoStock.getStockCount() + 1), "id=" + oldCargoStock.getId())){
				return "更新原商品货位库存失败!";
			}
			if(!this.updateAfterSaleBackSupplierProductReplace(set.toString(),"id=" + id)){
				return "更新更换单审核状态失败[审核不通过]!";
			}
		}
		return null;
	}
	
	/**
	 * 说明：返厂维修更换SKU审核-添加报损报溢单（已完成）
	 * @author lihaoxin
	 * @date 2015-02-27
	 * @param newCargoStock 
	 * @param newproduct 
	 * @param afStockService 
	 * @param detectProduct 
	 * @param byOperationnoteBean 
	 * @param bsbyService 
	 * @param notaxPrice 
	 */
	public String addBsbyOperationnoteAndProduct(IBsByServiceManagerService bsbyService, BsbyOperationnoteBean bsbyOperationnoteBean, AfterSaleDetectProductBean detectProduct, voProduct product, CargoProductStockBean cargoStock, String notaxPrice,voUser user,boolean stock){
		List<AfterSaleBsbyProduct> afterSaleBsbyProductList = new ArrayList<AfterSaleBsbyProduct>();
		Map<Integer,BsbyProductBean> bsbyProductMap = new HashMap<Integer, BsbyProductBean>();
		
		int maxid = bsbyService.getNumber("id", "bsby_operationnote", "max", "id > 0");
		bsbyOperationnoteBean.setId(maxid + 1);
		if(null != bsbyOperationnoteBean && bsbyService.addBsbyOperationnoteBean(bsbyOperationnoteBean)){
			String now = DateUtil.getNow();
			int byCount = 1;//报溢商品数量
			// 添加操作日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(bsbyOperationnoteBean.getEnd_oper_id());
			bsbyOperationRecordBean.setOperator_name(bsbyOperationnoteBean.getEnd_oper_name());
			bsbyOperationRecordBean.setTime(now);
			bsbyOperationRecordBean.setInformation("创建新的报溢表"+bsbyOperationnoteBean.getReceipts_number());
			bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
			if(!bsbyService.addBsbyOperationRecord(bsbyOperationRecordBean)){
				return "添加报损报溢日志失败";
			}
			
			//售后报损报溢商品
			AfterSaleBsbyProduct bsbyProduct = new AfterSaleBsbyProduct();
			bsbyProduct.setAfterSaleDetectProductCode(detectProduct.getCode());//处理单编号
			bsbyProduct.setAfterSaleDetectProductStatus(detectProduct.getStatus());//处理单状态
			bsbyProduct.setStatus(bsbyProduct.STATUS2);//已完成
			bsbyProduct.setAfterSaleOrderCode("");
			bsbyProduct.setBsbyOperationnoteId(bsbyOperationnoteBean.getId());//'报损报溢单id',
			bsbyProduct.setImei(detectProduct.getIMEI());//imei
			bsbyProduct.setProductId(detectProduct.getProductId());//商品id
			bsbyProduct.setWholeCode(detectProduct.getCargoWholeCode());//货位号
			if(!addAfterSaleBsbyProduct(bsbyProduct)){
				return "新增售后报溢商品失败";
			}
			afterSaleBsbyProductList.add(bsbyProduct);
			
			int x = ByBsAction.getProductCount(product.getId(), bsbyOperationnoteBean.getWarehouse_area(), bsbyOperationnoteBean.getWarehouse_type());
			if(!stock && bsbyOperationnoteBean.getType() == 0){//因报损 手动审核时 先减库存
				x = x + byCount;
			}
			int result = ByBsAction.updateProductCount(x, bsbyOperationnoteBean.getType(), byCount);
			
			if (result < 0 ) {
				return "您所添加商品的库存不足";
			}
			
	        BsbyProductBean bsbyProductBean = new BsbyProductBean();
	        
			bsbyProductBean.setBsby_count(byCount);//报损报溢数量
			bsbyProductBean.setOperation_id(bsbyOperationnoteBean.getId());//报损报溢单id
			bsbyProductBean.setProduct_code(product.getCode());//商品编号
			bsbyProductBean.setProduct_id(product.getId());//商品id
			bsbyProductBean.setProduct_name(product.getName());//商品名称
			bsbyProductBean.setOriname(product.getOriname());//商品原名称
			bsbyProductBean.setAfter_change(result);//操作后产品数量
			bsbyProductBean.setBefore_change(x);//操作前产品数量
			bsbyProductBean.setPrice(product.getPrice5());//含税金额
			bsbyProductBean.setBsby_price(product.getPrice5());
//			float notaxProductPrice = bsbyService.returnFinanceProductPrice(bsbyProductBean.getProduct_id());//不含税金额
			bsbyProductBean.setNotaxPrice(Float.valueOf(notaxPrice));
			if (!bsbyService.addBsbyProduct(bsbyProductBean)) {
				return "添加报损报溢商品失败";
			}
			bsbyProductMap.put(product.getId(), bsbyProductBean);
			
			BsbyProductCargoBean bsbyCargo = new BsbyProductCargoBean();
			bsbyCargo.setBsbyOperId(bsbyOperationnoteBean.getId());//报损报溢单id
			bsbyCargo.setBsbyProductId(bsbyService.getDbOp().getLastInsertId());//报损报溢商品表id
			bsbyCargo.setCount(byCount);//报损报溢数量
			bsbyCargo.setCargoProductStockId(cargoStock.getId());//新商品货位库存信息 id（产品货位库存id）
			bsbyCargo.setCargoId(cargoStock.getCargoId());//（货位信息id）
			if(!bsbyService.addBsbyProductCargo(bsbyCargo)){
			  return "添加报损报溢商品货位信息失败";
			}
			
			// 添加日志
			bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(bsbyOperationnoteBean.getEnd_oper_id());
			bsbyOperationRecordBean.setOperator_name(bsbyOperationnoteBean.getEnd_oper_name());
			bsbyOperationRecordBean.setTime(now);
			bsbyOperationRecordBean.setInformation("给单据:" + bsbyOperationnoteBean.getReceipts_number()
					+ "添加商品:" + product.getCode() + "数量：" + byCount);
			bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());//报溢单id
			if(!bsbyService.addBsbyOperationRecord(bsbyOperationRecordBean)){
				return "添加报损报溢商品日志失败";
			}
			
			//库存操作
			Json json = updateStock(bsbyOperationnoteBean, dbOp, user, afterSaleBsbyProductList, bsbyProductMap,stock);
			if(!json.isSuccess()){
				return json.getMsg();
			}
			
		}else{
			return "添加报损报溢单失败（返厂维修）";
		}
		return null;
	}
		

	public AfterSaleBackSupplierProductReplace getAftersaleBacksupplierProductReplace(String condition) {
		return (AfterSaleBackSupplierProductReplace) getXXX(condition, "after_sale_back_supplier_product_replace", "mmb.stock.aftersale.AfterSaleBackSupplierProductReplace");
	}
	
	@SuppressWarnings("unchecked")
	public List<FinanceProduct> getNotaxPriceFromFinanceProduct(String condition) {
		return getXXXList(condition, -1, -1, null, "finance_product", "mmb.stock.aftersale.FinanceProduct");
	}

	/**
	 * 记录旧IMEI码的状态变化及日志
	 * @param oriImei
	 * @param newImei
	 * @param user
	 * @param imeiService
	 * @param productId
	 * @param log
	 * @return
	 */
	public String recordOldImei(String oriImei, String newImei,voUser user,IMEIService imeiService, int productId,boolean flag) {
		if(!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS4, "product_id=" + productId + " and code='" + oriImei + "'")){
			return "更新旧imei码状态失败!";
		}
		IMEILogBean log = new IMEILogBean();
		if(flag){
			log.setContent("厂家维修更换商品匹配,同sku更换,IMEI码更换为" + newImei);
		}else{
			log.setContent("厂家维修更换商品匹配,更换sku,IMEI码更换为" + newImei);
		}
		log.setCreateDatetime(DateUtil.getNow());
		log.setIMEI(oriImei);
		log.setOperCode("");
		log.setOperType(IMEILogBean.OPERTYPE10);
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());
		if(!imeiService.addIMEILog(log)){
			return "新增imei日志失败!";
		}
		return null;
	}
	/**
	 * 记录旧IMEI码的状态变化及日志
	 * @param oriImei
	 * @param newImei
	 * @param user
	 * @param imeiService
	 * @param productId
	 * @param log
	 * @return
	 */
	public String recordNewImei(String oriImei, String newImei,voUser user,IMEIService imeiService,int productId, IMEIBean imeiBean) {
		if(imeiBean != null){
			if(!imeiService.updateIMEI("status=" + IMEIBean.IMEISTATUS6, "product_id=" + imeiBean.getProductId() + " and code='" + newImei + "'")){
				return "更新新imei码状态失败!";
			}
		}else{
			IMEIBean bean = new IMEIBean();
			bean.setCode(newImei);
			bean.setStatus(IMEIBean.IMEISTATUS6);
			bean.setProductId(productId);
			bean.setCreateDatetime(DateUtil.getNow());
			if(!imeiService.addIMEI(bean)){
				return "新增imei码失败!";
			}
		}
		
		IMEILogBean log = new IMEILogBean();
		log.setContent("售后维修替换" + oriImei + "入库,入库状态是维修中.");
		log.setIMEI(newImei);
		log.setCreateDatetime(DateUtil.getNow());
		log.setOperCode("");
		log.setOperType(IMEILogBean.OPERTYPE10);
		log.setUserId(user.getId());
		log.setUserName(user.getUsername());
		if(!imeiService.addIMEILog(log)){
			return "新增imei日志失败!";
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	public List<AfterSaleUnqualifiedReason> getAfterSaleUnqualifiedReasonList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_unqualified_reason", "mmb.stock.aftersale.AfterSaleUnqualifiedReason");
	}

	public int getAfterSaleUnqualifiedReasonCount(String condition) {
		return getXXXCount(condition, "after_sale_unqualified_reason", "id");
	}

	public boolean addAfterSaleUnqualifiedReason(AfterSaleUnqualifiedReason reason) {
		return addXXX(reason, "after_sale_unqualified_reason");
	}

	public boolean editAfterSaleUnqualifiedReason(String set, String condition) {
		return updateXXX(set, condition, "after_sale_unqualified_reason");
	}
	
	public boolean delAfterSaleUnqualifiedReason(String condition) {
		return deleteXXX(condition, "after_sale_unqualified_reason");
	}

	public AfterSaleUnqualifiedReason getAfterSaleUnqualifiedReason(String condition) {
		return (AfterSaleUnqualifiedReason) getXXX(condition, "after_sale_unqualified_reason", "mmb.stock.aftersale.AfterSaleUnqualifiedReason");
	}

	public List<Map<String, String>> getUnqualifiedReasonLog(String id) {
		List<Map<String,String>> maps = new ArrayList<Map<String,String>>();
		@SuppressWarnings("unchecked")
		List<AfterSaleBackSupplierProduct> list = this.getAfterSaleBackSupplierProductList("status=" + AfterSaleBackSupplierProduct.STATUS6 + " and after_sale_detect_product_id=" + id, -1, -1, " send_datetime desc ");
		if(list != null){
			for(AfterSaleBackSupplierProduct bean : list){
				Map<String,String> map = new HashMap<String, String>();
				map.put("unqualifiedReasonName", bean.getUnqualifiedReasonName());
				map.put("unqualifiedReasonName2", bean.getUnqualifiedReasonName2());
				map.put("unqualifiedReasonName3", bean.getUnqualifiedReasonName3());
				if(bean.getSendDatetime() != null){
					map.put("sendDatetime", bean.getSendDatetime().substring(0,19));
				}
				map.put("createDatetime", bean.getCreateDatetime());
				map.put("senderName", bean.getSenderName());
				maps.add(map);
			}
		}
		return maps;
	}

	public List<Map<String, String>> getBackUserPackageCodeOfAfterSaleOrder(String condition) {
		List<Map<String, String>> list = new ArrayList<Map<String,String>>();
		String query = "SELECT DISTINCT asdp.`code` " +
				"FROM after_sale_detect_product AS asdp " +
				"INNER JOIN after_sale_order AS aso ON asdp.after_sale_order_id = aso.id " +
				"INNER JOIN after_sale_back_user_product AS asbup ON asbup.after_sale_detect_product_id = asdp.id " +
				"WHERE " +
				"asbup.package_id = 0 and " + condition;
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			while(rs.next()){
				Map<String, String> map = new HashMap<String, String>();
				map.put("detectCode", rs.getString(1));
				list.add(map);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<AfterSaleWareJobQualifiedTime> getAfterSaleWareJobQualifiedTimeList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_ware_job_qualified_time", "mmb.stock.aftersale.AfterSaleWareJobQualifiedTime");
	}

	public int getAfterSaleWareJobQualifiedTimeCount(String condition) {
		return getXXXCount(condition, "after_sale_ware_job_qualified_time", "id");
	}
	
	public boolean addAfterSaleWareJobQualifiedTime(AfterSaleWareJobQualifiedTime bean) {
		return addXXX(bean, "after_sale_ware_job_qualified_time");
	}

	public boolean editAfterSaleWareJobQualifiedTime(String set, String condition) {
		return updateXXX(set, condition, "after_sale_ware_job_qualified_time");
	}
	
	public boolean delAfterSaleWareJobQualifiedTime(String condition) {
		return deleteXXX(condition, "after_sale_ware_job_qualified_time");
	}

	public AfterSaleWareJobQualifiedTime getAfterSaleWareJobQualifiedTime(String condition) {
		return (AfterSaleWareJobQualifiedTime) getXXX(condition, "after_sale_ware_job_qualified_time", "mmb.stock.aftersale.AfterSaleWareJobQualifiedTime");
	}
	
	@SuppressWarnings("unchecked")
	public List<AfterSaleWareJobQualifiedTimeLog> getAfterSaleWareJobQualifiedTimeLogList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_ware_job_qualified_time_log", "mmb.stock.aftersale.AfterSaleWareJobQualifiedTimeLog");
	}

	public boolean addAfterSaleWareJobQualifiedTimeLog(AfterSaleWareJobQualifiedTimeLog bean) {
		return addXXX(bean, "after_sale_ware_job_qualified_time_log");
	}
	
	@SuppressWarnings("unchecked")
	public List<AfterSaleCycleStatBean> getAfterSaleCycleStatBeanList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "after_sale_cycle_stat", "mmb.stock.aftersale.AfterSaleCycleStatBean");
	}

	public int getAfterSaleCycleStatBeanCount(String condition) {
		return getXXXCount(condition, "after_sale_cycle_stat", "id");
	}
	
	public boolean addAfterSaleCycleStatBean(AfterSaleCycleStatBean bean) {
		return addXXX(bean, "after_sale_cycle_stat");
	}

	public boolean editAfterSaleCycleStatBean(String set, String condition) {
		return updateXXX(set, condition, "after_sale_cycle_stat");
	}
	
	public boolean delAfterSaleCycleStatBean(String condition) {
		return deleteXXX(condition, "after_sale_ware_job_qualified_time");
	}

	public AfterSaleCycleStatBean getAfterSaleCycleStatBean(String condition) {
		return (AfterSaleCycleStatBean) getXXX(condition, "after_sale_ware_job_qualified_time", "mmb.stock.aftersale.AfterSaleCycleStatBean");
	}
	
	/**
	 * 根据备用机号获取处理单信息
	 * @param spareCode
	 * @return
	 * 2014-10-30
	 * lining
	 */
	public AfterSaleDetectProductBean getDetectProductBySpareCode(String spareCode){
		AfterSaleDetectProductBean bean = null;
		String query = "SELECT asdp.id,asdp.code,asdp.product_id,asdp.after_sale_order_id,asdp.after_sale_order_code,asdp.IMEI,asdp.status,asdp.lock_status, " +
				"asdp.cargo_whole_code,asdp.area_id,asdp.bs_status,asdp.create_datetime,asdp.create_user_id,asdp.create_user_name FROM after_sale_detect_product asdp " +
				"INNER JOIN after_sale_replace_new_product_record asrnpr  ON asdp.id = asrnpr.after_sale_detect_product_id " +
				"WHERE asrnpr.spare_code='" + spareCode + "'";
		ResultSet rs = this.dbOp.executeQuery(query);
		try {
			if(rs!=null && rs.next()){
				bean = new AfterSaleDetectProductBean();
				bean.setId(rs.getInt(1));
				bean.setCode(rs.getString(2));
				bean.setProductId(rs.getInt(3));
				bean.setAfterSaleOrderId(rs.getInt(4));
				bean.setAfterSaleOrderCode(rs.getString(5));
				bean.setIMEI(rs.getString(6));
				bean.setStatus(rs.getInt(7));
				bean.setLockStatus(rs.getInt(8));
				bean.setCargoWholeCode(rs.getString(9));
				bean.setAreaId(rs.getInt(10));
				bean.setBsStatus(rs.getInt(11));
				bean.setCreateDatetime(rs.getString(12));
				bean.setCreateUserId(rs.getInt(13));
				bean.setCreateUserName(rs.getString(14));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return bean;
	}
	
	/**
	 * 批量删除检测选项内容分类
	 * @param condition
	 * @return
	 * 2015年1月8日
	 * lining
	 */
	public boolean batchDeteleAfterSaleDetectTypeDetail(String condition){
		String sql = "delete from after_sale_detect_type_detail where id in (" + condition + ")";
		boolean result = this.dbOp.executeUpdate(sql);
		release(dbOp);
		return result;
	}
	
	
	/**
	 * 就要根据报损和报溢 变化库存(售后报损报溢)
	 * @date 2015-03-25
	 * @param bean 报损报溢单
	 * @param dbOp 连接
	 * @param user 操作人
	 * @param afterSaleBsbyProductList 报损报溢售后商品
	 * @param bsbyProductMap 报损报溢商品
	 * @param stock 操作库存(true)或库存锁定量(false)
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Json updateStock(BsbyOperationnoteBean bean, DbOperation dbOp, voUser user, List<AfterSaleBsbyProduct> afterSaleBsbyProductList, Map<Integer,BsbyProductBean> bsbyProductMap, boolean stock) {
		Json j = new Json();
		WareService wareService = new WareService(dbOp);
		IBsByServiceManagerService bsbyservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, bsbyservice.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();//财务基础数据
		try {
			int bybs_type = bean.getType();// 单据类型

			boolean existBsbyProduct = true;
			// 得到这个单据中的所有的要修改库存的商品
			if(null == afterSaleBsbyProductList || afterSaleBsbyProductList.size() == 0 ){
				afterSaleBsbyProductList = afStockService.getAfterSaleBsbyProductList("bsby_operationnote_id=" + bean.getId(), -1, -1, null);
				existBsbyProduct = false;
			}else{
				if(null == bsbyProductMap){
					j.setMsg("参数错误，报损报溢商品不存在！");
					return j;
				}
			}
			
			if (null != afterSaleBsbyProductList && afterSaleBsbyProductList.size() != 0) {
				Iterator it = afterSaleBsbyProductList.iterator();
				for (; it.hasNext();) {
					AfterSaleBsbyProduct bsbyProductBean = (AfterSaleBsbyProduct) it.next();
					int productId = bsbyProductBean.getProductId();
					
					voProduct product = wareService.getProduct(productId);// 每一个单据中的产品 依次进行修改库存操作
					product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));// 得到这个产品的所有库存的列表
					
					//报损报溢商品
					if(!bsbyProductMap.containsKey(productId)){
						j.setMsg("参数错误：报损报溢商品错误！");
						return j;
					}
					BsbyProductBean bsbyProduct = bsbyProductMap.get(productId);
					if(!existBsbyProduct){
						bsbyProduct = iBsByservice.getBsbyProductBean("operation_id=" + bean.getId() + " and product_id=" + productId);
					}
					
					if(bsbyProduct.getOperation_id() != bean.getId() || bsbyProductBean.getBsbyOperationnoteId() != bean.getId()){
						j.setMsg("参数错误：报损报溢商品非同一报损报溢单！");
						return j;
					}
					
					// 出库 报损就是出库
					if (bybs_type == BsbyOperationnoteBean.TYPE0) {

						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(DateUtil.getNow());
						bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存" + product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
						bsbyOperationRecordBean.setOperation_id(bean.getId());
						bsbyOperationRecordBean.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean)){
							j.setMsg("添加报损报溢单操作日志失败！");
							return j;
						}

						// 更新指定库的库存

						ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area=" + bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));

						if (ps == null) {
							j.setMsg("没有找到产品库存，操作失败！");
							return j;
						}
						//审核完成，
						if(stock){//减库存量
							if (!psService.updateProductStockCount(ps.getId(), -1)) {
								j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
								return j;
							}
						}else{//清除库存锁定量
							if (!psService.updateProductLockCount(ps.getId(), -1)) {
								j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
								return j;
							}
						}
						
						CargoInfoBean cib = cargoService.getCargoInfo("whole_code='" + bsbyProductBean.getWholeCode() + "'");
						if (cib == null) {
							j.setMsg("没有找到货位！");
							return j;
						}
						
						CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id=" + cib.getId() + " and product_id=" + bsbyProductBean.getProductId());
						if (cpsb == null) {
							j.setMsg("没有找到货位库存信息!");
							return j;
						}
						
						//审核完成
						if(stock){//减货位库存量
							if(!cargoService.updateCargoProductStockCount(cpsb.getId(), -1)){
								j.setMsg("货位库存操作失败，货位冻结库存不足！");
								return j;
							}
						}else{//减货位库存锁定量
							if(!cargoService.updateCargoProductStockLockCount(cpsb.getId(), -1)){
								j.setMsg("货位库存操作失败，货位冻结库存不足！");
								return j;
							}
						}

						BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();

						bsbyOperationRecordBean1.setOperator_id(user.getId());
						bsbyOperationRecordBean1.setOperator_name(user.getUsername());
						bsbyOperationRecordBean1.setTime(DateUtil.getNow());
						bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品" + product.getCode() + "出库1");
						bsbyOperationRecordBean1.setOperation_id(bean.getId());
						bsbyOperationRecordBean1.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1)){
							j.setMsg("添加报损报溢单操作日志失败");
							return j;
						}

						if (bean.getWarehouse_type() != ProductStockBean.STOCKTYPE_CUSTOMER) {
							
							//财务基础数据列表
							BaseProductInfo base = new BaseProductInfo();
							base.setId(productId);
							base.setProductStockId(ps.getId());
							base.setOutCount(1);
							base.setPrice(bsbyProduct.getPrice());
							base.setNotaxPrice(bsbyProduct.getNotaxPrice());
							base.setOutPrice(bsbyProduct.getPrice());
							baseList.add(base);
						}

						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
						CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = "+cpsb.getId());

						// 出库卡片
						StockCardBean sc = new StockCardBean();

						sc.setCardType(StockCardBean.CARDTYPE_LOSE);// 出库就是报损
						sc.setCode(bean.getReceipts_number());

						sc.setCreateDatetime(DateUtil.getNow());
						sc.setStockType(bean.getWarehouse_type());
						sc.setStockArea(bean.getWarehouse_area());
						sc.setProductId(productId);
						sc.setStockId(ps.getId());
						sc.setStockOutCount(1);
						sc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType()) + product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
						sc.setStockAllArea(product.getStock(bean.getWarehouse_area()) + product.getLockCount(bean.getWarehouse_area()));
						sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
						sc.setAllStock(product.getStockAll() + product.getLockCountAll());
						sc.setStockPrice(product.getPrice5());
						sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!psService.addStockCard(sc)){
							j.setMsg("进销存记录添加失败，请重新尝试操作！");
							return j;
						}
						
						//货位出库卡片
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_LOSE);
						csc.setCode(bean.getReceipts_number());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(bean.getWarehouse_type());
						csc.setStockArea(bean.getWarehouse_area());
						csc.setProductId(productId);
						csc.setStockId(cps.getId());
						csc.setStockOutCount(1);
						csc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
						csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
						csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
						if(!cargoService.addCargoStockCard(csc)){
							j.setMsg("货位进销存记录添加失败，请重新尝试操作！");
							return j;
						}
					}
					// 入库 报溢即入库
					else {

						BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();

						bsbyOperationRecordBean.setOperator_id(user.getId());
						bsbyOperationRecordBean.setOperator_name(user.getUsername());
						bsbyOperationRecordBean.setTime(DateUtil.getNow());
						bsbyOperationRecordBean.setInformation("单据:" + bean.getReceipts_number() + "操作前"+product.getCode()+"的库存" + product.getStock(bean.getWarehouse_area(), bean.getWarehouse_type()));
						bsbyOperationRecordBean.setOperation_id(bean.getId());
						bsbyOperationRecordBean.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean)){
							j.setMsg("添加报损单操作日志失败！");
							return j;
						}
						product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));

						ProductStockBean ps = psService.getProductStock("product_id=" + productId + " and area=" + bean.getWarehouse_area() + " and type=" + bean.getWarehouse_type());
						if (ps == null) {
							j.setMsg("没有找到产品库存，操作失败！");
							return j;
						}
						if (!psService.updateProductStockCount(ps.getId(), 1)) {
							j.setMsg("库存操作失败，可能是库存不足，请与管理员联系！");
							return j;
						}
						
						CargoInfoBean cib = cargoService.getCargoInfo("whole_code='" + bsbyProductBean.getWholeCode() + "'");
						if (cib == null) {
							j.setMsg("没有找到货位！");
							return j;
						}
						
						CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id=" + cib.getId() + " and product_id=" + bsbyProductBean.getProductId());
						if (cpsb == null) {
							j.setMsg("没有找到货位库存信息!");
							return j;
						}
						
						//审核完成，增加货位库存量
						if(!cargoService.updateCargoProductStockCount(cpsb.getId(), 1)){
							j.setMsg("货位库存操作失败，货位库存不足！");
							return j;
						}

						BsbyOperationRecordBean bsbyOperationRecordBean1 = new BsbyOperationRecordBean();
						bsbyOperationRecordBean1.setOperator_id(user.getId());
						bsbyOperationRecordBean1.setOperator_name(user.getUsername());
						bsbyOperationRecordBean1.setTime(DateUtil.getNow());
						bsbyOperationRecordBean1.setInformation("修改单据:" + bean.getReceipts_number() + "的商品" + product.getCode() + "入库1");
						bsbyOperationRecordBean1.setOperation_id(bean.getId());
						bsbyOperationRecordBean1.setLog_type(0);
						if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean1)){
							j.setMsg("报损报溢单添加操作日志失败！");
							return j;
						}
						
						// 审核通过，就加 进销存卡片
						product.setPsList(psService.getProductStockList("product_id=" + productId, -1, -1, null));
						CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = " + cpsb.getId());

						// 入库卡片
						StockCardBean sc = new StockCardBean();
						sc.setCardType(StockCardBean.CARDTYPE_GET);
						sc.setCode(bean.getReceipts_number());

						sc.setCreateDatetime(DateUtil.getNow());
						sc.setStockType(bean.getWarehouse_type());
						sc.setStockArea(bean.getWarehouse_area());
						sc.setProductId(productId);
						sc.setStockId(ps.getId());
						sc.setStockInCount(1);
						sc.setStockInPriceSum(product.getPrice5()*1);

						sc.setCurrentStock(product.getStock(bean.getWarehouse_area(), sc.getStockType()) + product.getLockCount(bean.getWarehouse_area(), sc.getStockType()));
						sc.setStockAllArea(product.getStock(bean.getWarehouse_area()) + product.getLockCount(bean.getWarehouse_area()));
						sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
						sc.setAllStock(product.getStockAll() + product.getLockCountAll());
						sc.setStockPrice(product.getPrice5());// 新的库存价格
						sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!psService.addStockCard(sc)){
							j.setMsg("进销存记录添加失败，请重新尝试操作！");
							return j;
						}
						
						//货位入库卡片
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_GET);
						csc.setCode(bean.getReceipts_number());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(bean.getWarehouse_type());
						csc.setStockArea(bean.getWarehouse_area());
						csc.setProductId(productId);
						csc.setStockId(cps.getId());
						csc.setStockInCount(1);
						csc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
						csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
						csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
						if(!cargoService.addCargoStockCard(csc)){
							j.setMsg("货位进销存添加失败，请重新尝试操作！");
							return j;
						}
						
						//售后库报溢审核通过后，需要生成售后处理单的售后入库单
						if(cib.getStockType()==ProductStockBean.STOCKTYPE_AFTER_SALE){
							AfterSaleDetectProductBean detectProductBean = afStockService.getAfterSaleDetectProduct("code='" + bsbyProductBean.getAfterSaleDetectProductCode() + "'");
							if(detectProductBean==null){
								j.setMsg("售后处理单"+bsbyProductBean.getAfterSaleDetectProductCode()+"不存在!");
								return j;
							}
							AfterSaleStockin stockin = new AfterSaleStockin();
							stockin.setAfterSaleDetectProductId(detectProductBean.getId());
							stockin.setProductId(productId);
							stockin.setOutCargoWholeCode("");
							stockin.setInCargoWholeCode(cps.getCargoInfo().getWholeCode());
							stockin.setStatus(AfterSaleStockin.STATUS1);
							stockin.setCreateUserId(user.getId());
							stockin.setCreateUserName(user.getUsername());
							stockin.setCreateDatetime(DateUtil.getNow());
							stockin.setCompleteUserId(user.getId());
							stockin.setCompleteUserName(user.getUsername());
							stockin.setCompleteDatetime(DateUtil.getNow());
							stockin.setType(AfterSaleStockin.TYPE5);
							stockin.setOrderCode("");
							
							if (!afStockService.addAfterSaleStockin(stockin)) {
								j.setMsg("添加售后入库单失败");
								return j;
							}
						}
						if (bean.getWarehouse_type() != ProductStockBean.STOCKTYPE_CUSTOMER) {
							//财务基础数据
							BaseProductInfo base = new BaseProductInfo();
							base.setId(productId);
							base.setInCount(1);//一个处理单就有一个商品
							base.setInPrice(product.getPrice5());
							base.setProductStockId(ps.getId());
							base.setPrice(bsbyProduct.getPrice());
							base.setNotaxPrice(bsbyProduct.getNotaxPrice());
							
							base.setOutCount(1);
							base.setOutPrice(11.0f);
							base.setProductStockOutId(ps.getId());
							baseList.add(base);
						}
					}
					
					int updateStatus = bybs_type == BsbyOperationnoteBean.TYPE0 ? AfterSaleDetectProductBean.LOCK_STATUS2 : AfterSaleDetectProductBean.LOCK_STATUS0;
					String set = "lock_status=" + updateStatus;
					//2014-11-12 李宁 售后库报溢单审核成功需要更新售后处理单状态为报溢已完成
					if(bybs_type==BsbyOperationnoteBean.TYPE1 && bean.getWarehouse_type()==ProductStockBean.STOCKTYPE_AFTER_SALE){
						set += ",status=" + AfterSaleDetectProductBean.STATUS24;
					}
					if (!afStockService.updateAfterSaleDetectProduct(set, "code='" + bsbyProductBean.getAfterSaleDetectProductCode() + "'")) {
						j.setMsg("更新售后处理单状态失败！");
						return j;
					}
				}

				//报损报溢出调用财务接口
				if(baseList.size() > 0){
					if(bybs_type == 0){
						//报损
						FinanceBaseDataService bsBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_LOSE, service.getDbOp().getConn());
						bsBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
					}else{
						//报溢
						FinanceBaseDataService byBaseData = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(StockCardBean.CARDTYPE_GET, service.getDbOp().getConn());
						byBaseData.acquireFinanceBaseData(baseList, bean.getReceipts_number(), user.getId(), bean.getWarehouse_type(), bean.getWarehouse_area());
					}
				}
				
				// 操作完成记录 bsby
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(DateUtil.getNow());
				bsbyOperationRecordBean.setInformation("修改单据:" + bean.getReceipts_number() + "完成更改库存操作");
				bsbyOperationRecordBean.setOperation_id(bean.getId());
				bsbyOperationRecordBean.setLog_type(0);
				if(!bsbyservice.addBsbyOperationRecord(bsbyOperationRecordBean)) {
					j.setMsg("报损报溢单添加操作日志失败！");
					return j;
				}
			}
			j.setSuccess(true);
			return j;
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("报损报溢修改库存操作异常！");
			return j;
		}
	}
}
