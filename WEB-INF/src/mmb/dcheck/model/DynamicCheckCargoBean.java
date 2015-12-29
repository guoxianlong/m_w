package mmb.dcheck.model;

public class DynamicCheckCargoBean {
	/**
	 * 自增id
	 */
    private int id;

    /**
     * 盘点计划id
     */
    private int dynamicCheckId;

    /**
     * 货位id
     */
    private int cargoId;

    /**
     * 货位号
     */
    private String cargoWholeCode;

    /**
     * 货位区域id
     */
    private int cargoInfoStockAreaId=-1;

    /**
     * 货位巷道id
     */
    private int cargoInfoPassageId;
    
    /**
     * 商品id
     */
    private int productId;

    /**
     * 产品编号
     */
    private String productCode;

    /**
     * 产品原名称
     */
    private String productName;

    /**
     * 终盘次数
     */
    private int endCheckTimes;

    /**
     * 盘点结果
     */
    private int checkResult=-1;

    /**
     * 状态
     */
    private int status=-1;

    /**
     * 一盘差异数
     */
    private int difference1;

    /**
     * 一盘人id
     */
    private int checkUserId1;

    /**
     * 一盘人用户名
     */
    private String checkUsername1;

    /**
     * 二盘差异数
     */
    private int difference2;

    /**
     * 二盘人id
     */
    private int checkUserId2;

    /**
     * 二盘人用户名
     */
    private String checkUsername2;
    
    /**
     * 三盘差异数
     */
    private int difference3;

    /**
     * 三盘人id
     */
    private int checkUserId3;

    /**
     * 三盘人用户名
     */
    private String checkUsername3;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDynamicCheckId() {
        return dynamicCheckId;
    }

    public void setDynamicCheckId(int dynamicCheckId) {
        this.dynamicCheckId = dynamicCheckId;
    }

    public int getCargoId() {
        return cargoId;
    }

    public void setCargoId(int cargoId) {
        this.cargoId = cargoId;
    }

    public String getCargoWholeCode() {
        return cargoWholeCode;
    }

    public void setCargoWholeCode(String cargoWholeCode) {
        this.cargoWholeCode = cargoWholeCode == null ? null : cargoWholeCode.trim();
    }

    public int getCargoInfoStockAreaId() {
        return cargoInfoStockAreaId;
    }

    public void setCargoInfoStockAreaId(int cargoInfoStockAreaId) {
        this.cargoInfoStockAreaId = cargoInfoStockAreaId;
    }

    public int getCargoInfoPassageId() {
		return cargoInfoPassageId;
	}

	public void setCargoInfoPassageId(int cargoInfoPassageId) {
		this.cargoInfoPassageId = cargoInfoPassageId;
	}

	public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode == null ? null : productCode.trim();
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName == null ? null : productName.trim();
    }

    public int getEndCheckTimes() {
        return endCheckTimes;
    }

    public void setEndCheckTimes(int endCheckTimes) {
        this.endCheckTimes = endCheckTimes;
    }

    public int getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(int checkResult) {
        this.checkResult = checkResult;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDifference1() {
        return difference1;
    }

    public void setDifference1(int difference1) {
        this.difference1 = difference1;
    }

    public int getCheckUserId1() {
        return checkUserId1;
    }

    public void setCheckUserId1(int checkUserId1) {
        this.checkUserId1 = checkUserId1;
    }

    public String getCheckUsername1() {
        return checkUsername1;
    }

    public void setCheckUsername1(String checkUsername1) {
        this.checkUsername1 = checkUsername1 == null ? null : checkUsername1.trim();
    }

    public int getDifference2() {
        return difference2;
    }

    public void setDifference2(int difference2) {
        this.difference2 = difference2;
    }

    public int getCheckUserId2() {
        return checkUserId2;
    }

    public void setCheckUserId2(int checkUserId2) {
        this.checkUserId2 = checkUserId2;
    }

    public String getCheckUsername2() {
        return checkUsername2;
    }

    public void setCheckUsername2(String checkUsername2) {
        this.checkUsername2 = checkUsername2 == null ? null : checkUsername2.trim();
    }

    public int getDifference3() {
        return difference3;
    }

    public void setDifference3(int difference3) {
        this.difference3 = difference3;
    }

    public int getCheckUserId3() {
        return checkUserId3;
    }

    public void setCheckUserId3(int checkUserId3) {
        this.checkUserId3 = checkUserId3;
    }

    public String getCheckUsername3() {
        return checkUsername3;
    }

    public void setCheckUsername3(String checkUsername3) {
        this.checkUsername3 = checkUsername3 == null ? null : checkUsername3.trim();
    }
}