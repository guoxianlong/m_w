package mmb.finance.stat;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import adultadmin.util.db.DbOperation;

public class FinanceBuyPayBean {
	public static Map typeMap=new HashMap();
	public static Map balanceModeMap=new HashMap();
	static{
		typeMap.put("0","采购入库单" );
		typeMap.put("1", "采购入库单调整作废");
		typeMap.put("2", "采购入库单调整冲抵");
		
		//3,4,5是T单,T单是退货单
		typeMap.put("3", "采购退货单");
		typeMap.put("4", "采购退货单调整作废");
		typeMap.put("5", "采购退货单调整冲抵");
		
		typeMap.put("6", "付款单");
		typeMap.put("7", "付款单调整作废");
		typeMap.put("8", "付款单调整冲抵");
		
		typeMap.put("9", "退款单");
		typeMap.put("10", "退款单调整作废");
		typeMap.put("11", "退款单调整冲抵");
		
		typeMap.put("12", "校准单调整");
		typeMap.put("13", "校准单调整作废");
		typeMap.put("14", "校准单调整冲抵");
		//15,16,17是TH单，TH单是拒收单
		typeMap.put("15", "退换货单");
		typeMap.put("16", "退换货单调整作废");
		typeMap.put("17", "退换货单调整冲抵");
		//配件采购
		typeMap.put("0","配件采购入库单" );
		typeMap.put("1", "配件采购入库单调整作废");
		typeMap.put("2", "配件采购入库单调整冲抵");
		balanceModeMap.put("1", "代销");
		balanceModeMap.put("0", "经销");
	}
	public int id;
	public String billsNumCode;
	public int supplierId;
	public String buyOrderCode;
	public double money;
	public String createDateTime;
	public int type;
	
	public int applyType;
	public int payMode;
	public int paySource;
	public double taxPoint;
	public String tCode;
	
	@Deprecated
	public int ticket;
	public double carriage;
	public int productLineId;
	
	/**
	 * 供应商税率
	 */
	public double tax;
	
	/**
	 * 结算模式[1:代销，2:经销]
	 */
	public int balanceMode;
	
	public int getProductLineId() {
		return productLineId;
	}
	public void setProductLineId(int productLineId) {
		this.productLineId = productLineId;
	}
	public double getCarriage() {
		return carriage;
	}
	public void setCarriage(double carriage) {
		this.carriage = carriage;
	}
	public int getTicket() {
		return ticket;
	}
	public void setTicket(int ticket) {
		this.ticket = ticket;
	}
	public double getTaxPoint() {
		return taxPoint;
	}
	public void setTaxPoint(double taxPoint) {
		this.taxPoint = taxPoint;
	}
	public String gettCode() {
		return tCode;
	}
	public void settCode(String tCode) {
		this.tCode = tCode;
	}
	public int getPaySource() {
		return paySource;
	}
	public void setPaySource(int paySource) {
		this.paySource = paySource;
	}
	public int getPayMode() {
		return payMode;
	}
	public void setPayMode(int payMode) {
		this.payMode = payMode;
	}
	public int getApplyType() {
		return applyType;
	}
	public void setApplyType(int applyType) {
		this.applyType = applyType;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBillsNumCode() {
		return billsNumCode;
	}
	public void setBillsNumCode(String billsNumCode) {
		this.billsNumCode = billsNumCode;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	
	public String getBuyOrderCode() {
		return buyOrderCode;
	}
	public void setBuyOrderCode(String buyOrderCode) {
		this.buyOrderCode = buyOrderCode;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public String getCreateDateTime() {
		return createDateTime;
	}
	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public int getBalanceMode() {
		return balanceMode;
	}
	public void setBalanceMode(int balanceMode) {
		this.balanceMode = balanceMode;
	}
	public static int getProductLine(String buyCode,int supplierId){
		DbOperation db=new DbOperation();
		double maxMoney=0;
		int productId=0;
		int lineId=0;
		try {
			db.init("adult_slave2");
			ResultSet rs=null;
			
			if(!buyCode.contains("D")){
				String sql="select product_line_id from supplier_product_line where supplier_id="+supplierId;
				db.prepareStatement(sql);
				rs=db.getPStmt().executeQuery();
				if(rs.next()){
					lineId=rs.getInt(1);
				}
			}else{
				String sql="select product_line_id from finance_buy_pay where buy_order_code='"+buyCode+"'";
				db.prepareStatement(sql);
				rs=db.getPStmt().executeQuery();
				if(rs.next()){
					lineId=rs.getInt(1);
				}else{
					sql="select buy_stock_product.buy_count*purchase_price,product_id from buy_order " +
							" join buy_stock on buy_stock.buy_order_id=buy_order.id" +
							" join buy_stock_product on buy_stock.id=buy_stock_product.buy_stock_id where buy_order.code='"+buyCode+"' ";
					db.prepareStatement(sql);
					rs=db.getPStmt().executeQuery();
					while(rs.next()){
						if(rs.getDouble(1)>maxMoney){
							maxMoney=rs.getDouble(1);
							productId=rs.getInt(2);
						}
					}
					sql="select finance_product_line_catalog.product_line_id from product join finance_product_line_catalog on product.parent_id1=finance_product_line_catalog.catalog_id or product.parent_id2=finance_product_line_catalog.catalog_id where product.id="+productId;
					db.prepareStatement(sql);
					rs=db.getPStmt().executeQuery();
					if(rs.next()){
						lineId=rs.getInt(1);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.release();
		}
		return lineId;
		
		
	}
	

}
