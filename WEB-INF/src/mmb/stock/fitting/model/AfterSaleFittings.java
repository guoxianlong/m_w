package mmb.stock.fitting.model;

public class AfterSaleFittings {
	/**
	 * 商品一级分类id=1536标明此商品为配件
	 */
	public static final int FITTING_PARENT1_ID = 1536;
	
    private Long id;

    private Integer productId;

    private Integer fittingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getFittingId() {
        return fittingId;
    }

    public void setFittingId(Integer fittingId) {
        this.fittingId = fittingId;
    }
}