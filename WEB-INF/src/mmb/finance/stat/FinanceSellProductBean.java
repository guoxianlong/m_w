/**
 * 说明：销售商品信息bean
 * 
 * 日期：2012-8-31
 * 
 * 作者：liuruilan
 */
package mmb.finance.stat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class FinanceSellProductBean {
	
	public int id;
	
	/**
	 * 订单id
	 */
	public int orderId;
	
	/**
	 * 调整单号
	 */
	public String adjustCode;
	
	/**
	 * 商品id
	 */
	public int productId;
	
	/**
	 * 购买数量，负数为退货量
	 */
	public int buyCount;
	
	/**
	 * 商品定价
	 */
	public float price;
	
	/**
	 * 商品实际销售价
	 */
	public float dprice;
	
	/**
	 * 成本价格
	 */
	public float price5;
	
	/**
	 * 财务用产品线id
	 */
	public int productLine;
	
	/**
	 * 一级分类
	 */
	public int parentId1;
	
	/**
	 * 二级分类
	 */
	public int parentId2;
	
	/**
	 * 三级分类
	 */
	public int parentId3;
	
	/**
	 * 发生时间
	 */
	public String createDatetime;
	
	/**
	 * 数据类别，详见dataTypeMap 
	 */
	public int dataType;
	
	/**
	 * 是否有票，0-有票，1-无票
	 */
	public int balanceMode;
	
	/**
	 * 供应商id
	 */
	public int supplierId;
	
	/**
	 * 发货信息表id
	 */
	public int financeSellId;
	
	/**
	 * 税前成本价
	 */
	public double notaxPrice;
	
	/**
	 * 供应商税率
	 */
	public double tax;
	
	
	//以下为非数据库映射属性
	//销售出库数量
	private int outCount;
	//销售出库金额
	private Double outMoney;
	//销售退货数量
	private int sInCount;
	//销售退货金额
	private Double sInMoney;
	//售后退货数量
	private int asInCount;
	//售后退货金额
	private Double asInMoney;
	
	public int getOutCount() {
		return outCount;
	}
	public void setOutCount(int outCount) {
		this.outCount = outCount;
	}
	public Double getOutMoney() {
		return outMoney;
	}
	public void setOutMoney(Double outMoney) {
		this.outMoney = outMoney;
	}
	public int getsInCount() {
		return sInCount;
	}
	public void setsInCount(int sInCount) {
		this.sInCount = sInCount;
	}
	public Double getsInMoney() {
		return sInMoney;
	}
	public void setsInMoney(Double sInMoney) {
		this.sInMoney = sInMoney;
	}
	public int getAsInCount() {
		return asInCount;
	}
	public void setAsInCount(int asInCount) {
		this.asInCount = asInCount;
	}
	public Double getAsInMoney() {
		return asInMoney;
	}
	public void setAsInMoney(Double asInMoney) {
		this.asInMoney = asInMoney;
	}

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOrderId() {
		return orderId;
	}
	public String getAdjustCode() {
		return adjustCode;
	}
	public void setAdjustCode(String adjustCode) {
		this.adjustCode = adjustCode;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getBuyCount() {
		return buyCount;
	}
	public void setBuyCount(int buyCount) {
		this.buyCount = buyCount;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getDprice() {
		return dprice;
	}
	public void setDprice(float dprice) {
		this.dprice = dprice;
	}
	public float getPrice5() {
		return price5;
	}
	public void setPrice5(float price5) {
		this.price5 = price5;
	}
	public int getProductLine() {
		return productLine;
	}
	public void setProductLine(int productLine) {
		this.productLine = productLine;
	}
	public int getParentId1() {
		return parentId1;
	}
	public void setParentId1(int parentId1) {
		this.parentId1 = parentId1;
	}
	public int getParentId2() {
		return parentId2;
	}
	public void setParentId2(int parentId2) {
		this.parentId2 = parentId2;
	}
	public int getParentId3() {
		return parentId3;
	}
	public void setParentId3(int parentId3) {
		this.parentId3 = parentId3;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getDataType() {
		return dataType;
	}
	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
	public int getBalanceMode() {
		return balanceMode;
	}
	public void setBalanceMode(int balanceMode) {
		this.balanceMode = balanceMode;
	}
	public int getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}
	public int getFinanceSellId() {
		return financeSellId;
	}
	public void setFinanceSellId(int financeSellId) {
		this.financeSellId = financeSellId;
	}
	public double getNotaxPrice() {
		return notaxPrice;
	}
	public void setNotaxPrice(double notaxPrice) {
		this.notaxPrice = notaxPrice;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}


	public static Map dataTypeMap = new LinkedHashMap();
	static{
		dataTypeMap.put(String.valueOf(0), "商品销售出库");
		dataTypeMap.put(String.valueOf(1), "赠品销售出库");
		dataTypeMap.put(String.valueOf(2), "商品未妥投退回");
		dataTypeMap.put(String.valueOf(3), "赠品未妥投退回");
		dataTypeMap.put(String.valueOf(4), "商品售后退回");
		dataTypeMap.put(String.valueOf(5), "赠品售后退回");
		
		dataTypeMap.put(String.valueOf(6), "商品销售出库废弃");
		dataTypeMap.put(String.valueOf(7), "赠品销售出库废弃");
		dataTypeMap.put(String.valueOf(8), "商品未妥投退回废弃");
		dataTypeMap.put(String.valueOf(9), "赠品未妥投退回废弃");
		dataTypeMap.put(String.valueOf(10), "商品售后退回废弃");
		dataTypeMap.put(String.valueOf(11), "赠品售后退回废弃");
		
		dataTypeMap.put(String.valueOf(12), "商品销售出库冲抵");
		dataTypeMap.put(String.valueOf(13), "赠品销售出库冲抵");
		dataTypeMap.put(String.valueOf(14), "商品未妥投退回冲抵");
		dataTypeMap.put(String.valueOf(15), "赠品未妥投退回冲抵");
		dataTypeMap.put(String.valueOf(16), "商品售后退回冲抵");
		dataTypeMap.put(String.valueOf(17), "赠品售后退回冲抵");
	}
	
	/**
	 * 根据批次号查询当时采购订单的供应商id
	 */
	public static int querySupplier(DbOperation db, String batchCode){
		int supplierId = 0;
		try {
			String sql="";
			
			if( batchCode.startsWith("R")){
				 sql = "SELECT bo.proxy_id FROM buy_order bo JOIN buy_stock bs ON bo.id = bs.buy_order_id "
			   		+ " JOIN buy_stockin bsi ON bs.id = bsi.buy_stock_id WHERE bsi.code = ?";
				
			}
			if( batchCode.startsWith("J")){
				 sql = "SELECT bo.proxy_id FROM buy_order bo JOIN buy_stock bs ON bo.id = bs.buy_order_id "
			   		+ "  WHERE bs.code = ?";
				
			}
			if( batchCode.startsWith("J")||batchCode.startsWith("R")){
				db.prepareStatement(sql);
				PreparedStatement pstmt = db.getPStmt();
				pstmt.setString(1, batchCode);
				ResultSet rs = pstmt.executeQuery();
				if(rs.next()){
					supplierId = rs.getInt("proxy_id");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return supplierId;
	}
	
	/**
	 * 根据批次号查询产品是否带票，0-带票
	 */
	public static int queryTicket(DbOperation db, String code){
		int ticket = -1;
		try {
			String sql = "select ticket from stock_batch where code=? limit 0,1";
			db.prepareStatement(sql);
			PreparedStatement pstmt = db.getPStmt();
			pstmt.setString(1, code);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				ticket = rs.getInt("ticket");
			}
			if(ticket==-1 && code.startsWith("R")){
				sql="select ticket from buy_stockin join buy_stock" +
						" on buy_stockin.buy_stock_id=buy_stock.id " +
						" join buy_order on buy_stock.buy_order_id=buy_order.id where buy_stockin.code='"+StringUtil.toSql(code)+"'";
				db.prepareStatement(sql);
				pstmt=db.getPStmt();
				rs=pstmt.executeQuery();
				if(rs.next()){
					ticket=rs.getInt("ticket");
				}
			}
			if(ticket==-1 && code.startsWith("J")){
				sql="select ticket from buy_stock" +
						" join buy_order on buy_stock.buy_order_id=buy_order.id where buy_stock.code='"+StringUtil.toSql(code)+"'";
				db.prepareStatement(sql);
				pstmt=db.getPStmt();
				rs=pstmt.executeQuery();
				if(rs.next()){
					ticket=rs.getInt("ticket");
				}
			}
			if(ticket == -1){
				ticket = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ticket;
	}
	
}
