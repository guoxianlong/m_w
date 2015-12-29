package mmb.stock.aftersale;

public class AfterSaleFittingsBean {
	public int id;
	public int productId;
	public int fittingId;
	
	public final static int FITTING_PARENT1_ID = 1536;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getFittingId() {
		return fittingId;
	}
	public void setFittingId(int fittingId) {
		this.fittingId = fittingId;
	}
	
}
