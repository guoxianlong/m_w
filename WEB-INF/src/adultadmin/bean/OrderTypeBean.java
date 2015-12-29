/*
 * Created on 2008-9-19
 *
 */
package adultadmin.bean;

import java.util.Hashtable;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-9-19
 * 
 * 说明：用于判别一个订单的种类
 */
public class OrderTypeBean {
    public int id;

    public String name;

    public int typeId;

    public String productCatalogs;

    public String productIds;

    public int checkOrder;

    public Hashtable products;

    /**
     * @return Returns the checkOrder.
     */
    public int getCheckOrder() {
        return checkOrder;
    }

    /**
     * @param checkOrder
     *            The checkOrder to set.
     */
    public void setCheckOrder(int checkOrder) {
        this.checkOrder = checkOrder;
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
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the productCatalogs.
     */
    public String getProductCatalogs() {
        return productCatalogs;
    }

    /**
     * @param productCatalogs
     *            The productCatalogs to set.
     */
    public void setProductCatalogs(String productCatalogs) {
        this.productCatalogs = productCatalogs;
    }

    /**
     * @return Returns the productIds.
     */
    public String getProductIds() {
        return productIds;
    }

    /**
     * @param productIds
     *            The productIds to set.
     */
    public void setProductIds(String productIds) {
        this.productIds = productIds;
    }

    /**
     * @return Returns the products.
     */
    public Hashtable getProducts() {
        return products;
    }

    /**
     * @param products
     *            The products to set.
     */
    public void setProducts(Hashtable products) {
        this.products = products;
    }

    /**
     * @return Returns the typeId.
     */
    public int getTypeId() {
        return typeId;
    }

    /**
     * @param typeId
     *            The typeId to set.
     */
    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
