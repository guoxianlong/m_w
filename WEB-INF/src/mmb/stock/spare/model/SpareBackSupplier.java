package mmb.stock.spare.model;


public class SpareBackSupplier {
    private int id;

    private int supplierId;

    private String packageCode;

    private Float deliveryCost;

    private int deliveryId;

    private String agency;

    private int operateUserId;

    private String operateUserName;

    private String ourAddress;

    private String ourPost;

    private String receiverName;

    private String contractPhone;

    private int areaId;

    private int count;

    private String createDatetime;

    private String remark;
    
    private String supplierName;//厂商名称
    private String supplierAddress;//厂商地址
    private String createDate;//填单日期

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public Float getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Float deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public int getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(int deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getAgency() {
        return agency;
    }

    public int getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(int operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public String getOurAddress() {
        return ourAddress;
    }

    public void setOurAddress(String ourAddress) {
        this.ourAddress = ourAddress == null ? null : ourAddress.trim();
    }

    public String getOurPost() {
        return ourPost;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName == null ? null : receiverName.trim();
    }

    public String getContractPhone() {
        return contractPhone;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}

	public String getRemark() {
        return remark;
    }

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public void setAgency(String agency) {
		this.agency = agency;
	}

	public void setOperateUserName(String operateUserName) {
		this.operateUserName = operateUserName;
	}

	public void setOurPost(String ourPost) {
		this.ourPost = ourPost;
	}

	public void setContractPhone(String contractPhone) {
		this.contractPhone = contractPhone;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getSupplierAddress() {
		return supplierAddress;
	}

	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}
	
}