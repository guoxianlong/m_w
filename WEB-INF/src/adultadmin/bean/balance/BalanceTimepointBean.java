/*
 * Created on 2009-10-15
 *
 */
package adultadmin.bean.balance;

public class BalanceTimepointBean {

	public int id;

	/**
	 * 结算来源
	 */
	public int balanceType;

	/**
	 * 结算时间点
	 */
	public String balanceTimepoint;

	private boolean hasBalanceData = false;

	public String getBalanceTimepoint() {
		return balanceTimepoint;
	}

	public void setBalanceTimepoint(String balanceTimepoint) {
		this.balanceTimepoint = balanceTimepoint;
	}

	public int getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(int balanceType) {
		this.balanceType = balanceType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isHasBalanceData() {
		return hasBalanceData;
	}

	public void setHasBalanceData(boolean hasBalanceData) {
		this.hasBalanceData = hasBalanceData;
	}

}
