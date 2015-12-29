package mmb.bi.model;

import java.util.Date;

public class BiSplitOrderInfo {
    private Integer id;

    private Date createDate;

    private Integer stockArea;

    private Integer splitOrderCount;

    private Integer baseOrderCount;

    private Integer spanOrderCount;

    private Integer subOrderCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Integer getStockArea() {
        return stockArea;
    }

    public void setStockArea(Integer stockArea) {
        this.stockArea = stockArea;
    }

    public Integer getSplitOrderCount() {
        return splitOrderCount;
    }

    public void setSplitOrderCount(Integer splitOrderCount) {
        this.splitOrderCount = splitOrderCount;
    }

    public Integer getBaseOrderCount() {
        return baseOrderCount;
    }

    public void setBaseOrderCount(Integer baseOrderCount) {
        this.baseOrderCount = baseOrderCount;
    }

    public Integer getSpanOrderCount() {
        return spanOrderCount;
    }

    public void setSpanOrderCount(Integer spanOrderCount) {
        this.spanOrderCount = spanOrderCount;
    }

    public Integer getSubOrderCount() {
        return subOrderCount;
    }

    public void setSubOrderCount(Integer subOrderCount) {
        this.subOrderCount = subOrderCount;
    }
}