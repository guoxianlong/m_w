/*
 * Created on 2009-10-15
 *
 */
package adultadmin.bean.balance;

public class BalanceCycleBean {

	public int id;

	/**
	 * 结算来源
	 */
	public int balanceType;

	/**
	 * 结算周期开始时间
	 */
	public String balanceCycleStart;

	/**
	 * 结算周期结束时间
	 */
	public String balanceCycleEnd;
	
	/**
	 * 结算时间点
	 */
	public int balanceTimepointId;
	
	public BalanceTimepointBean balanceTimepoint;
	
	/**
	 * 第一次结算时间为订单发货周期结束日后的第“N”个结算时间点<br />
	 * 这个值就是“N”
	 */
	public int firstBalanceTimeN;

	/**
	 * 结算时间 设置页面显示用
	 */
	private boolean hasBalanceData = false;

	private boolean hasPackageData = false;
	
	public double lowReckonMoneyScale;
	public double normalReckonMoneyScale;
	public double heightReckonMoneyScale;
	public double returnReckonMoneyScale;
	


	public double getLowReckonMoneyScale() {
		return lowReckonMoneyScale;
	}

	public void setLowReckonMoneyScale(double lowReckonMoneyScale) {
		this.lowReckonMoneyScale = lowReckonMoneyScale;
	}

	

	public double getNormalReckonMoneyScale() {
		return normalReckonMoneyScale;
	}

	public void setNormalReckonMoneyScale(double normalReckonMoneyScale) {
		this.normalReckonMoneyScale = normalReckonMoneyScale;
	}

	public double getHeightReckonMoneyScale() {
		return heightReckonMoneyScale;
	}

	public void setHeightReckonMoneyScale(double heightReckonMoneyScale) {
		this.heightReckonMoneyScale = heightReckonMoneyScale;
	}

	
	public double getReturnReckonMoneyScale() {
		return returnReckonMoneyScale;
	}

	public void setReturnReckonMoneyScale(double returnReckonMoneyScale) {
		this.returnReckonMoneyScale = returnReckonMoneyScale;
	}

	public String getBalanceCycleEnd() {
		return balanceCycleEnd;
	}

	public void setBalanceCycleEnd(String balanceCycleEnd) {
		this.balanceCycleEnd = balanceCycleEnd;
	}

	public String getBalanceCycleStart() {
		return balanceCycleStart;
	}

	public void setBalanceCycleStart(String balanceCycleStart) {
		this.balanceCycleStart = balanceCycleStart;
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

	public int getBalanceTimepointId() {
		return balanceTimepointId;
	}

	public void setBalanceTimepointId(int balanceTimepointId) {
		this.balanceTimepointId = balanceTimepointId;
	}

	

	public BalanceTimepointBean getBalanceTimepoint() {
		return balanceTimepoint;
	}

	public void setBalanceTimepoint(BalanceTimepointBean balanceTimepoint) {
		this.balanceTimepoint = balanceTimepoint;
	}

	public boolean isHasPackageData() {
		return hasPackageData;
	}

	public void setHasPackageData(boolean hasPackageData) {
		this.hasPackageData = hasPackageData;
	}

	public int getFirstBalanceTimeN() {
		return firstBalanceTimeN;
	}

	public void setFirstBalanceTimeN(int firstBalanceTimeN) {
		this.firstBalanceTimeN = firstBalanceTimeN;
	}

}
