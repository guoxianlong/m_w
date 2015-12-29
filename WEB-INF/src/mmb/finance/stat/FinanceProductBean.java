/**
 * 说明：产品相关信息（财务统计使用）,主要存储含票价格等信息
 * 
 * 日期：2012-10-24
 * 
 * @author liuruilan
 *
 */
package mmb.finance.stat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import adultadmin.util.db.DbOperation;

public class FinanceProductBean {
	
	public int id;
	
	/**
	 * 产品id
	 */
	public int productId;
	
	/**
	 * 原库存价
	 */
	public float price;
	
	/**
	 * 不含税库存均价
	 */
	public float notaxPrice;
	
	/**
	 * 含票库存价
	 */
	public float priceHasticket;
	
	/**
	 * 无票库存价
	 */
	public float priceNoticket;
	
	/**
	 * 结存金额
	 */
	public float priceSum;
	
	/**
	 * 含票结存金额
	 */
	public float priceSumHasticket;
	
	/**
	 * 无票结存金额
	 */
	public float priceSumNoticket;
	
	/**
	 * 结存数量
	 */
	public int countSum;
	
	/**
	 * 结存金额
	 */
	public double notaxPriceSum;

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

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getNotaxPrice() {
		return notaxPrice;
	}

	public void setNotaxPrice(float notaxPrice) {
		this.notaxPrice = notaxPrice;
	}

	public float getPriceHasticket() {
		return priceHasticket;
	}

	public void setPriceHasticket(float priceHasticket) {
		this.priceHasticket = priceHasticket;
	}

	public float getPriceNoticket() {
		return priceNoticket;
	}

	public void setPriceNoticket(float priceNoticket) {
		this.priceNoticket = priceNoticket;
	}

	public float getPriceSum() {
		return priceSum;
	}

	public void setPriceSum(float priceSum) {
		this.priceSum = priceSum;
	}

	public float getPriceSumHasticket() {
		return priceSumHasticket;
	}

	public void setPriceSumHasticket(float priceSumHasticket) {
		this.priceSumHasticket = priceSumHasticket;
	}

	public float getPriceSumNoticket() {
		return priceSumNoticket;
	}

	public void setPriceSumNoticket(float priceSumNoticket) {
		this.priceSumNoticket = priceSumNoticket;
	}

	public int getCountSum() {
		return countSum;
	}

	public void setCountSum(int countSum) {
		this.countSum = countSum;
	}

	public double getNotaxPriceSum() {
		return notaxPriceSum;
	}

	public void setNotaxPriceSum(double notaxPriceSum) {
		this.notaxPriceSum = notaxPriceSum;
	}

	/**
	 * 获取某产品的总库存
	 * @param productId 产品id
	 * @param ticket 是否含票，0-含票, 1-无票
	 */
	public static int queryCountIfTicket(DbOperation db, int productId, int ticket){
		int count = 0;
		try {
			String sql = "SELECT sum(batch_count) count FROM stock_batch WHERE product_id = ? AND ticket = ? GROUP BY product_id";
			db.prepareStatement(sql);
			PreparedStatement pstmt = db.getPStmt();
			pstmt.setInt(1, productId);
			pstmt.setInt(2, ticket);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				count = rs.getInt("count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return count;
	}
}
