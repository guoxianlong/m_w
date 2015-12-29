package mmb.bi.model;


/**
 * 已使用 ProductStockBean.stockoutAvailableAreaMap 代替
 * 但枚举值 AreaAll需要保留使用
 */
public enum EBIArea {

	/**
	 * -2 迈世
	 */
	AreaAll("迈世", -2),
	/**
	 * 0 北京
	 */
	AreaBJ("北京", 0),
	/**
	 * 3 广迈
	 */
	AreaGZ("广迈", 3),
	/**
	 * 4 无迈
	 */
	AreaWX("无迈", 4),	
	/**
	 * 西安
	 */
	AreaXA("西安", 8),	
	/**
	 * 9 成都
	 */
	AreaCD("成都", 9);

	private String name;
	private Integer index;

	private EBIArea(String name, Integer index) {
		this.name = name;
		this.index = index;
	}

	public static EBIArea getEnum(Integer index) {
		for (EBIArea c : EBIArea.values()) {
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
