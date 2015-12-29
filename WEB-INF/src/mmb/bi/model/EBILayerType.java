package mmb.bi.model;

public enum EBILayerType {

	/**
	 * 0 整体
	 */
	All("整体", 0),
	/**
	 * 1 物流中心
	 */
	Warehouse("物流中心", 1);

	private String name;
	private Integer index;

	private EBILayerType(String name, Integer index) {
		this.name = name;
		this.index = index;
	}

	public static EBILayerType getEnum(Integer index) {
		for (EBILayerType c : EBILayerType.values()) {
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
