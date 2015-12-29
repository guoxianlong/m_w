/*
 * Created on 2007-6-28
 *
 */
package adultadmin.bean;

import adultadmin.action.vo.voProduct;

/**
 * 作者：张陶
 * 
 * 创建日期：2007-6-28
 * 
 * 说明：
 */
public class ProductPackageBean {

    public int id;

    /**
     * 父产品ID，非空、索引
     */
    public int parentId;

    /**
     * 套装内的产品ID、非空
     */
    public int productId;

    /**
     * 套装内包含该产品的数量、非空
     */
    public int productCount;

    public voProduct product;

    /**
     * @return Returns the product.
     */
    public voProduct getProduct() {
        return product;
    }

    /**
     * @param product
     *            The product to set.
     */
    public void setProduct(voProduct product) {
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }

}
