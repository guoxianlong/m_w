package mmb.stock.cargo;

public class CargoDeptAreaBean {
	// Id
	// Dept_id 部门id
	// Area 地区
	// Stock_type 库类型
	public int id;
	public int deptId;
	public int area;
	public int stockType;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDeptId() {
		return deptId;
	}
	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	public int getStockType() {
		return stockType;
	}
	public void setStockType(int stockType) {
		this.stockType = stockType;
	}
	
}
