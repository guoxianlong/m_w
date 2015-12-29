package mmb.stock.cargo;

/**
 * 装箱单标准装箱量
 *
 */
public class CartonningStandardCountBean {
	public int id;					//ID
	public int productId;			//产品ID
	public int standard;			//标准装箱量
	public String lastOperDatetime;	//最后修改时间
	public int operId;				//人工修改装箱量的用户Id
	public String operName;			//人工修改装箱量的用户姓名
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getStandard() {
		return standard;
	}
	public void setStandard(int standard) {
		this.standard = standard;
	}
	public String getLastOperDatetime() {
		return lastOperDatetime;
	}
	public void setLastOperDatetime(String lastOperDatetime) {
		this.lastOperDatetime = lastOperDatetime;
	}
	public int getOperId() {
		return operId;
	}
	public void setOperId(int operId) {
		this.operId = operId;
	}
	public String getOperName() {
		return operName;
	}
	public void setOperName(String operName) {
		this.operName = operName;
	}
}
