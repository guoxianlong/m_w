package mmb.stock.area.model;

public class StockAreaSubTemp {
	
	private String name;//库类型名称
	private Integer status;//库使用情况 1使用, 0未使用
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
}
