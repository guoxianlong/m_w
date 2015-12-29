/*
 * Created on 2009-9-25
 *
 */
package adultadmin.bean.balance;

public class BalanceComputeBean {

	public int id;

	public int balanceType;

	public String balanceCycle;

	public String balanceDate;

	public float untreadChargePer;

	public float untreadCountPer;

	public float balanceChargePer;

	public float balanceCountPer;

	public float getBalanceChargePer() {
		return balanceChargePer;
	}

	public void setBalanceChargePer(float balanceChargePer) {
		this.balanceChargePer = balanceChargePer;
	}

	public float getBalanceCountPer() {
		return balanceCountPer;
	}

	public void setBalanceCountPer(float balanceCountPer) {
		this.balanceCountPer = balanceCountPer;
	}

	public String getBalanceCycle() {
		return balanceCycle;
	}

	public void setBalanceCycle(String balanceCycle) {
		this.balanceCycle = balanceCycle;
	}

	public String getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(String balanceDate) {
		this.balanceDate = balanceDate;
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

	public float getUntreadChargePer() {
		return untreadChargePer;
	}

	public void setUntreadChargePer(float untreadChargePer) {
		this.untreadChargePer = untreadChargePer;
	}

	public float getUntreadCountPer() {
		return untreadCountPer;
	}

	public void setUntreadCountPer(float untreadCountPer) {
		this.untreadCountPer = untreadCountPer;
	}

}
