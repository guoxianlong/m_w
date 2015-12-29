package mmb.bi.model;

/**
 * 在职人力
 * @author mengqy
 */
public class BIInServiceCountBean {
	/**
	 * 自增id
	 */
    private Integer id;

    /**
     * 库地区id
     */
    private Integer areaId;

    /**
     * 时间
     */
    private String datetime;

    /**
     * 统计时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
    
	/**
     * 仓储部
     */
    private Integer warehouse;

    /**
     * 发货部
     */
    private Integer sendGoods;

    /**
     * 退货部
     */
    private Integer refundGoods;

    /**
     * 收货质检部
     */
    private Integer qualityChecking;

    /**
     * 配送部
     */
    private Integer delivery;

    /**
     * 运营部
     */
    private Integer operation;

    /**
     * 产品部
     */
    private Integer product;

    /**
     * 人事部
     */
    private Integer hr;

    /**
     * 行政部
     */
    private Integer administration;

    /**
     * 总人数
     */
    private Integer total;

    /**
     * 状态
     * 0：未审核
     * 1：已生效
     */
    private EStatus status;

    /**
     * 状态枚举
     */
    public enum EStatus {
        /**
         * 0 未审核
         */
    	Status0("未审核", 0),
        /**
         * 1 已生效
         */
    	Status1("已生效", 1);
    	
        // 成员变量
        private String name;
        private Integer index;

        private EStatus(String name, Integer index) {
            this.name = name;
            this.index = index;
        }
        
        public static EStatus getEnum(Integer index) {
            for (EStatus c : EStatus.values()) {
                if (c.getIndex() == index) {
                    return c;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

    public Integer getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Integer warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getSendGoods() {
        return sendGoods;
    }

    public void setSendGoods(Integer sendGoods) {
        this.sendGoods = sendGoods;
    }

    public Integer getRefundGoods() {
        return refundGoods;
    }

    public void setRefundGoods(Integer refundGoods) {
        this.refundGoods = refundGoods;
    }

    public Integer getQualityChecking() {
        return qualityChecking;
    }

    public void setQualityChecking(Integer qualityChecking) {
        this.qualityChecking = qualityChecking;
    }

    public Integer getDelivery() {
        return delivery;
    }

    public void setDelivery(Integer delivery) {
        this.delivery = delivery;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public Integer getProduct() {
        return product;
    }

    public void setProduct(Integer product) {
        this.product = product;
    }

    public Integer getHr() {
        return hr;
    }

    public void setHr(Integer hr) {
        this.hr = hr;
    }

    public Integer getAdministration() {
        return administration;
    }

    public void setAdministration(Integer administration) {
        this.administration = administration;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getStatus() {
        return status.getIndex();
    }

    public void setStatus(Integer status) {
        this.status = EStatus.getEnum(status);
    }
    
    public void setStatusByEnum(EStatus status) {
        this.status = status;
    }
    
    public String getStatusName() {
    	return this.status.getName();
    }
    
    
}