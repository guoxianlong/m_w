package mmb.stock.fitting.model;

/**
 * PDA确认出库(领单)
 * @author mengqy
 */
public class FittingOutBean {
	private String code;
	private int count;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
