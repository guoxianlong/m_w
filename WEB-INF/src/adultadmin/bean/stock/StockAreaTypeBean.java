package adultadmin.bean.stock;


/**
 * 
 * 作者：朱爱林
 * 时间：2013-10-25
 * 说明：库类型与库区域关联表
 */
public class StockAreaTypeBean {

	public int id;
	public int areaId;//库地区id --> stock_area
	public int typeId;//库类型id --> stock_type
	public int status;//使用状态  0不使用，1使用
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAreaId() {
		return areaId;
	}
	public void setAreaId(int areaId) {
		this.areaId = areaId;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}
