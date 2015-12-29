package mmb.rec.oper.dao;

import java.util.List;
import java.util.Map;

import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.stock.ProductStockBean;

public interface ProductPackageDao {
	
	/**
	 * 添加ProductPackage
	 * @param ProductPackageBean
	 * @return
	 */
	public int addProductPackage(ProductPackageBean productPackageBean);
	/**
	 * 查ProductPackage
	 * @param condition
	 * @return
	 */
	public ProductPackageBean getProductPackage(String condition);
	/**
	 * 查询符合条件的ProductPackage的List
	 * @param paramMap
	 * @return
	 */
	public List<ProductPackageBean> getProductPackageList(Map<String,String> paramMap);
	/**
	 * 查询符合条件的ProductPackage的List
	 * @param paramMap
	 * @return
	 */
	public List<ProductPackageBean> getProductPackageListSlave(Map<String,String> paramMap);

}
