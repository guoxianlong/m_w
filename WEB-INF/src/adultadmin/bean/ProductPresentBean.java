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
public class ProductPresentBean {

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
    
    public int type;
    public int promotionId;
    public int typeProductCount1;
    public int typeProductCount2;
   

	public int getTypeProductCount1() {
		return typeProductCount1;
	}

	public void setTypeProductCount1(int typeProductCount1) {
		this.typeProductCount1 = typeProductCount1;
	}

	public int getTypeProductCount2() {
		return typeProductCount2;
	}

	public void setTypeProductCount2(int typeProductCount2) {
		this.typeProductCount2 = typeProductCount2;
	}

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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(int promotionId) {
		this.promotionId = promotionId;
	}

}
