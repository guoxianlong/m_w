package mmb.stock.fitting.model;

/**
 * 功能：采购入库配件商品相关信息
 * @author lining
 *
 */
public class FittingBuyStockInProduct {
    public int id;
    public int buyStockinProductId;
    public int fittingType;
   

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBuyStockinProductId() {
		return buyStockinProductId;
	}

	public void setBuyStockinProductId(int buyStockinProductId) {
		this.buyStockinProductId = buyStockinProductId;
	}

	public int getFittingType() {
		return fittingType;
	}

	public void setFittingType(int fittingType) {
		this.fittingType = fittingType;
	}	

}