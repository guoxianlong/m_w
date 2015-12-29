/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.infc;

import java.util.ArrayList;
import java.util.List;

import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.ProductPresentBean;

/**
 * 作者：张陶
 * 
 * 创建日期：2007-11-21
 * 
 * 说明：
 */
public interface IProductPackageService extends IBaseService {
	public boolean addProductPackage(ProductPackageBean bean);

	public ProductPackageBean getProductPackage(String condition);

	public int getProductPackageCount(String condition);

	public boolean updateProductPackage(String set, String condition);

	public boolean deleteProductPackage(String condition);

	public ArrayList getProductPackageList(String condition, int index,
			int count, String orderBy);


	public boolean addProductPresent(ProductPresentBean bean);

	public ProductPresentBean getProductPresent(String condition);

	public int getProductPresentCount(String condition);

	public boolean updateProductPresent(String set, String condition);

	public boolean deleteProductPresent(String condition);

	public ArrayList getProductPresentList(String condition, int index,
			int count, String orderBy);

	public boolean checkIsContent(int id, String condition);
	
	public List getPresentByParent(int id);
	
	public boolean checkIsHave(int id, String condition);
}
