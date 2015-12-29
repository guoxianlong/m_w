package adultadmin.service.infc;

import java.util.ArrayList;

import adultadmin.action.vo.voProductSupplier;
import adultadmin.bean.supplier.SupplierCityBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;

public interface ISupplierService extends IBaseService {

	// 供应商基本信息
	public boolean addSupplierStandardInfo(SupplierStandardInfoBean bean);

	public ArrayList getSupplierStandardInfoList(String condition, int index,
			int count, String orderBy);

	public int getSupplierStandardInfoCount(String condition);

	public SupplierStandardInfoBean getSupplierStandardInfo(String condition);

	public boolean updateSupplierStandardInfo(String set, String condition);

	public boolean deleteSupplierStandardInfo(String condition);

	// 表product_supplier
	public boolean addProductSupplierInfo(voProductSupplier bean);

	public ArrayList getProductSupplierInfoList(String condition, int index,
			int count, String orderBy);

	public int getProductSupplierInfoCount(String condition);

	public voProductSupplier getProductSupplierInfo(String condition);

	public boolean updateProductSupplierInfo(String set, String condition);

	public boolean deleteProductSupplierInfo(String condition);

	public ArrayList getSupplierProductLineList(String condition, int index,
			int count, String orderBy);

	public SupplierCityBean getSupplierCityInfo(String condition);
}
