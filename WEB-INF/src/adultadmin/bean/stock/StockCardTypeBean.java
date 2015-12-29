package adultadmin.bean.stock;

/**
 * 
 * @author 郝亚斌
 * 进销存卡片类型 stock_card_type 表 的实体类
 */
public class StockCardTypeBean {
	
	public int id;  // stock_card 的 card_type 字段数值
	
	public String name; // 卡片类型名称
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
