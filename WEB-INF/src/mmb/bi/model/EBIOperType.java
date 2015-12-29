package mmb.bi.model;

/**
 * 作业环节枚举
 * @author mengqy
 *
 */
public enum EBIOperType {	
	 /**
     * 0 采购入库
     */
	Type0("采购入库", 0),
    /**
     * 1 上架
     */
	Type1("上架", 1),
	/**
     * 2 退货入库
     */
	Type2("退货入库", 2),
	/**
     * 3 订单分拣
     */
	Type3("订单分拣", 3),
	/**
     * 4 订单分播
     */
	Type4("订单分播", 4),
	/**
     * 5 订单复核
     */
	Type5("订单复核", 5),
	/**
     * 6 订单交接
     */
	Type6("订单交接", 6),
	/**
     * 7 异常处理
     */
	Type7("异常处理", 7),
	/**
     * 8 盘点理货
     */
	Type8("盘点理货", 8),	
	/**
	 * 9 封箱
	 */
	Type9("封箱", 9);

	
    private String name;
    private Integer index;

    private EBIOperType(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    public static EBIOperType getEnum(Integer index) {
        for (EBIOperType c : EBIOperType.values()) {
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
