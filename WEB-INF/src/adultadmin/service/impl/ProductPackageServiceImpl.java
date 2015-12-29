/*
 * Created on 2007-2-8
 *
 */
package adultadmin.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.ProductPresentBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.util.db.DbOperation;


/**
 * 作者：李北金
 * 
 * 创建日期：2007-2-8
 * 
 * 说明：
 */
public class ProductPackageServiceImpl extends BaseServiceImpl implements IProductPackageService {
	
    public ProductPackageServiceImpl(int useConnType, DbOperation dbOp) {
        this.useConnType = useConnType;
        this.dbOp = dbOp;
    }

//    public ProductPackageServiceImpl() {
//        this.useConnType = CONN_IN_SERVICE;
//    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addProductPackage(ProductPackageBean bean) {
        return addXXX(bean, "product_package");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteProductPackage(String condition) {
        return deleteXXX(condition, "product_package");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ProductPackageBean getProductPackage(String condition) {
        return (ProductPackageBean) getXXX(condition, "product_package", "adultadmin.bean.ProductPackageBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getProductPackageCount(String condition) {
        return getXXXCount(condition, "product_package", "id");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getProductPackageList(String condition, int index, int count,
            String orderBy) {
        return getXXXList(condition, index, count, orderBy, "product_package",
                "adultadmin.bean.ProductPackageBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateProductPackage(String set, String condition) {
        return updateXXX(set, condition, "product_package");
    }



    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean addProductPresent(ProductPresentBean bean) {
        return addXXX(bean, "product_present");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean deleteProductPresent(String condition) {
        return deleteXXX(condition, "product_present");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ProductPresentBean getProductPresent(String condition) {
        return (ProductPresentBean) getXXX(condition, "product_present", "adultadmin.bean.ProductPresentBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public int getProductPresentCount(String condition) {
        return getXXXCount(condition, "product_present", "id");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public ArrayList getProductPresentList(String condition, int index, int count,
            String orderBy) {
        return getXXXList(condition, index, count, orderBy, "product_present",
                "adultadmin.bean.ProductPresentBean");
    }

    /*
     * 请查看父类或接口对应的注释。
     */
    public boolean updateProductPresent(String set, String condition) {
        return updateXXX(set, condition, "product_present");
    }



	
	/**
	 * 检验是否存在键入的赠品code
	 */
	public boolean checkIsContent(int id, String condition){
		if (!getDbOp().init()) {
            return false;
        }
    	Connection conn = this.getDbOp().getConn();
    	Statement stmt = null;
    	ResultSet rs = null;
    	try{
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery("select * from product_present where parent_id="+id+condition);
    		if(!rs.next()){
    			return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
			try{
				if(rs!=null){
					rs.close();
				}
				if(stmt!=null){
					stmt.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 根据产品id 查询赠品
	 */
	public List getPresentByParent(int id){
		if (!getDbOp().init()) {
            return null;
        }
		WareService wareService = new WareService();
    	Connection conn = this.getDbOp().getConn();
    	Statement stmt = null;
    	ResultSet rs = null;
    	List presentList = new ArrayList();
    	try{
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery("select product_id from product_present where parent_id="+id);
    		while(rs.next()){
    			int productId= rs.getInt("product_id");
    			voProduct vo = wareService.getProduct(productId);
    			presentList.add(vo);
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
			try{
				if(rs!=null){
					rs.close();
				}
				if(stmt!=null){
					stmt.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			wareService.releaseAll();
		}
		return presentList;
	}
	
	/**
	 * 校验删除后的商品时候还有赠品
	 */
	public boolean checkIsHave(int id, String condition){
		if (!getDbOp().init()) {
            return false;
        }
    	Connection conn = this.getDbOp().getConn();
    	Statement stmt = null;
    	ResultSet rs = null;
    	StringBuffer insertCondition = new StringBuffer(condition); 
    	try{
    		insertCondition.insert(condition.indexOf("in"), "not ");
    		stmt = conn.createStatement();
    		rs = stmt.executeQuery("select * from product_present where parent_id="+id+insertCondition.toString());
    		if(rs.next()){
    			return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}finally {
			try{
				if(rs!=null){
					rs.close();
				}
				if(stmt!=null){
					stmt.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return true;
	}
	
}
