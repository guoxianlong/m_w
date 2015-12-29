package mmb.stock.aftersale;


public class AfterSaleOperationWsproductRelationalBean {
	public int id;
	
	public int afterSaleOperationId;
	/**after_sale_operation_list表的id**/
	public int afterSaleWsproductRecordId;
	/**after_sale_warehource_product_records表的id**/
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getAfterSaleOperationId() {
		return afterSaleOperationId;
	}
	public void setAfterSaleOperationId(int afterSaleOperationId) {
		this.afterSaleOperationId = afterSaleOperationId;
	}
	public int getAfterSaleWsproductRecordId() {
		return afterSaleWsproductRecordId;
	}
	public void setAfterSaleWsproductRecordId(int afterSaleWsproductRecordId) {
		this.afterSaleWsproductRecordId = afterSaleWsproductRecordId;
	}
	
}
