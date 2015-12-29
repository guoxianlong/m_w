/*
 * Created on 2007-11-14
 *
 */
package adultadmin.bean.stock;

import adultadmin.action.vo.voProduct;

/**
 * 作者：李北金
 * 
 * 创建日期：2007-11-14
 * 
 * 说明：
 */
public class GroupProductBean {
    public int id;

    public int groupId;

    public int productId;

    public String productCode;

    public int unitCount;

    voProduct product;

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

    /**
     * @return Returns the groupId.
     */
    public int getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     *            The groupId to set.
     */
    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    /**
     * @return Returns the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *            The id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return Returns the productCode.
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param productCode
     *            The productCode to set.
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * @return Returns the productId.
     */
    public int getProductId() {
        return productId;
    }

    /**
     * @param productId
     *            The productId to set.
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * @return Returns the unitCount.
     */
    public int getUnitCount() {
        return unitCount;
    }

    /**
     * @param unitCount
     *            The unitCount to set.
     */
    public void setUnitCount(int unitCount) {
        this.unitCount = unitCount;
    }
}
