package mmb.ware.cargo.model;

public class CargoInfo {
    private Integer id;

    private String code;

    private String wholeCode;

    private Integer storeType;

    private Integer maxStockCount;

    private Integer warnStockCount;

    private Integer spaceLockCount;

    private Integer productLineId;

    private Integer type;

    private Integer length;

    private Integer width;

    private Integer high;

    private Integer floorNum;

    private Integer status;

    private Integer stockType;

    private Integer shelfId;

    private Integer stockAreaId;

    private Integer storageId;

    private Integer areaId;

    private Integer cityId;

    private String remark;

    private Integer passageId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getWholeCode() {
        return wholeCode;
    }

    public void setWholeCode(String wholeCode) {
        this.wholeCode = wholeCode == null ? null : wholeCode.trim();
    }

    public Integer getStoreType() {
        return storeType;
    }

    public void setStoreType(Integer storeType) {
        this.storeType = storeType;
    }

    public Integer getMaxStockCount() {
        return maxStockCount;
    }

    public void setMaxStockCount(Integer maxStockCount) {
        this.maxStockCount = maxStockCount;
    }

    public Integer getWarnStockCount() {
        return warnStockCount;
    }

    public void setWarnStockCount(Integer warnStockCount) {
        this.warnStockCount = warnStockCount;
    }

    public Integer getSpaceLockCount() {
        return spaceLockCount;
    }

    public void setSpaceLockCount(Integer spaceLockCount) {
        this.spaceLockCount = spaceLockCount;
    }

    public Integer getProductLineId() {
        return productLineId;
    }

    public void setProductLineId(Integer productLineId) {
        this.productLineId = productLineId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHigh() {
        return high;
    }

    public void setHigh(Integer high) {
        this.high = high;
    }

    public Integer getFloorNum() {
        return floorNum;
    }

    public void setFloorNum(Integer floorNum) {
        this.floorNum = floorNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStockType() {
        return stockType;
    }

    public void setStockType(Integer stockType) {
        this.stockType = stockType;
    }

    public Integer getShelfId() {
        return shelfId;
    }

    public void setShelfId(Integer shelfId) {
        this.shelfId = shelfId;
    }

    public Integer getStockAreaId() {
        return stockAreaId;
    }

    public void setStockAreaId(Integer stockAreaId) {
        this.stockAreaId = stockAreaId;
    }

    public Integer getStorageId() {
        return storageId;
    }

    public void setStorageId(Integer storageId) {
        this.storageId = storageId;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getPassageId() {
        return passageId;
    }

    public void setPassageId(Integer passageId) {
        this.passageId = passageId;
    }
}