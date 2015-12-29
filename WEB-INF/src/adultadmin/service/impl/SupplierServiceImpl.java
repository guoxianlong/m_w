package adultadmin.service.impl;

import java.util.ArrayList;

import adultadmin.action.vo.voProductSupplier;
import adultadmin.bean.supplier.SupplierCityBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.db.DbOperation;

public class SupplierServiceImpl extends BaseServiceImpl implements ISupplierService  {

	public SupplierServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

//	public SupplierServiceImpl() {
//		this.useConnType = CONN_IN_SERVICE;
//	}
	//供应商信息
	public boolean addSupplierStandardInfo(SupplierStandardInfoBean bean) {
		return addXXX(bean, "supplier_standard_info");
	}

	public ArrayList getSupplierStandardInfoList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "supplier_standard_info", "adultadmin.bean.supplier.SupplierStandardInfoBean");
	}

	public int getSupplierStandardInfoCount(String condition) {
		return getXXXCount(condition, "supplier_standard_info", "id");
	}

	public SupplierStandardInfoBean getSupplierStandardInfo(String condition) {
		return (SupplierStandardInfoBean) getXXX(condition, "supplier_standard_info",
				"adultadmin.bean.supplier.SupplierStandardInfoBean");
	}

	public boolean updateSupplierStandardInfo(String set, String condition) {
		return updateXXX(set, condition, "supplier_standard_info");
	}

	public boolean deleteSupplierStandardInfo(String condition) {
		return deleteXXX(condition, "supplier_standard_info");
	}


	// 表product_supplier
	public boolean addProductSupplierInfo(voProductSupplier bean){
		return addXXX(bean, "product_supplier");
	}

	public ArrayList getProductSupplierInfoList(String condition, int index,
			int count, String orderBy){
		return getXXXList(condition, index, count, orderBy, "product_supplier", "adultadmin.action.vo.voProductSupplier");
	}

	public int getProductSupplierInfoCount(String condition){
		return getXXXCount(condition, "product_supplier", "id");
	}

	public voProductSupplier getProductSupplierInfo(String condition){
		return (voProductSupplier) getXXX(condition, "product_supplier",
				"adultadmin.action.vo.voProductSupplier");
	}

	public boolean updateProductSupplierInfo(String set, String condition){
		return updateXXX(set, condition, "product_supplier");
	}

	public boolean deleteProductSupplierInfo(String condition){
		return deleteXXX(condition, "product_supplier");
	}

	public ArrayList getSupplierProductLineList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy,
				"supplier_product_line",
				"adultadmin.bean.supplier.supplierProductLineBean");
	}

	public SupplierCityBean getSupplierCityInfo(String condition) {
		return (SupplierCityBean) getXXX(condition, "province_city",
		"adultadmin.bean.supplier.SupplierCityBean");
	}
}
