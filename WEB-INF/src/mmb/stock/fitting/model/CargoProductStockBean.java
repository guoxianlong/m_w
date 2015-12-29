package mmb.stock.fitting.model;

public class CargoProductStockBean {
    private int id;

    private int cargoId;

    private int productId;

    private int stockCount;

    private int stockLockCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getStockCount() {
        return stockCount;
    }

    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }

    public int getStockLockCount() {
        return stockLockCount;
    }

    public void setStockLockCount(int stockLockCount) {
        this.stockLockCount = stockLockCount;
    }
}