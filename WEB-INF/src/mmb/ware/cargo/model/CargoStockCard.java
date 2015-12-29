package mmb.ware.cargo.model;

import java.util.Date;

public class CargoStockCard {
    private Integer id;

    private Integer stockType;

    private Integer stockArea;

    private Integer stockId;

    private String code;

    private Integer cardType;

    private String createDatetime;

    private Integer stockInCount;

    private Double stockInPriceSum;

    private Integer stockOutCount;

    private Double stockOutPriceSum;

    private String cargoWholeCode;

    private Integer cargoStoreType;

    private Integer currentStock;

    private Integer allStock;

    private Float stockPrice;

    private Double allStockPriceSum;

    private Integer productId;

    private Integer currentCargoStock;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStockType() {
        return stockType;
    }

    public void setStockType(Integer stockType) {
        this.stockType = stockType;
    }

    public Integer getStockArea() {
        return stockArea;
    }

    public void setStockArea(Integer stockArea) {
        this.stockArea = stockArea;
    }

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(String createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Integer getStockInCount() {
        return stockInCount;
    }

    public void setStockInCount(Integer stockInCount) {
        this.stockInCount = stockInCount;
    }

    public Double getStockInPriceSum() {
        return stockInPriceSum;
    }

    public void setStockInPriceSum(Double stockInPriceSum) {
        this.stockInPriceSum = stockInPriceSum;
    }

    public Integer getStockOutCount() {
        return stockOutCount;
    }

    public void setStockOutCount(Integer stockOutCount) {
        this.stockOutCount = stockOutCount;
    }

    public Double getStockOutPriceSum() {
        return stockOutPriceSum;
    }

    public void setStockOutPriceSum(Double stockOutPriceSum) {
        this.stockOutPriceSum = stockOutPriceSum;
    }

    public String getCargoWholeCode() {
        return cargoWholeCode;
    }

    public void setCargoWholeCode(String cargoWholeCode) {
        this.cargoWholeCode = cargoWholeCode == null ? null : cargoWholeCode.trim();
    }

    public Integer getCargoStoreType() {
        return cargoStoreType;
    }

    public void setCargoStoreType(Integer cargoStoreType) {
        this.cargoStoreType = cargoStoreType;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getAllStock() {
        return allStock;
    }

    public void setAllStock(Integer allStock) {
        this.allStock = allStock;
    }

    public Float getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Float stockPrice) {
        this.stockPrice = stockPrice;
    }

    public Double getAllStockPriceSum() {
        return allStockPriceSum;
    }

    public void setAllStockPriceSum(Double allStockPriceSum) {
        this.allStockPriceSum = allStockPriceSum;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getCurrentCargoStock() {
        return currentCargoStock;
    }

    public void setCurrentCargoStock(Integer currentCargoStock) {
        this.currentCargoStock = currentCargoStock;
    }
}