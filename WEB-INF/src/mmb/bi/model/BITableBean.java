package mmb.bi.model;

/**
 * BI 作业环节 表格bean
 * @author mengqy
 *
 */
public class BITableBean {
	/**
	 * 时间
	 */
	private String datetime;
	/**
	 * 作业量
	 */
	private int operCount;
	/**
	 * 附加值1
	 */
	private int count1;
	/**
	 * 附加值2
	 */
	private int count2;	
	/**
	 * 附加值3
	 */
	private int count3;
	/**
	 * 实际在岗人数
	 */
	private float onGuradCount;
	/**
	 * 在岗总时长
	 */
	private float onGuradTimeLength;
	
	/**
	 * 日在岗人均产能
	 */
	private float onGuradPerCount;
	
	/**
	 * 标准人均产能
	 */
	private float standardCapacity;

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public int getOperCount() {
		return operCount;
	}

	public void setOperCount(int operCount) {
		this.operCount = operCount;
	}

	public int getCount1() {
		return count1;
	}

	public void setCount1(int count1) {
		this.count1 = count1;
	}

	public int getCount2() {
		return count2;
	}

	public void setCount2(int count2) {
		this.count2 = count2;
	}

	public int getCount3() {
		return count3;
	}

	public void setCount3(int count3) {
		this.count3 = count3;
	}

	public float getOnGuradCount() {
		return onGuradCount;
	}

	public void setOnGuradCount(float onGuradCount) {
		this.onGuradCount = onGuradCount;
	}

	public float getOnGuradTimeLength() {
		return onGuradTimeLength;
	}

	public void setOnGuradTimeLength(float onGuradTimeLength) {
		this.onGuradTimeLength = onGuradTimeLength;
	}

	public float getOnGuradPerCount() {
		return onGuradPerCount;
	}

	public void setOnGuradPerCount(float onGuradPerCount) {
		this.onGuradPerCount = onGuradPerCount;
	}

	public float getStandardCapacity() {
		return standardCapacity;
	}

	public void setStandardCapacity(float standardCapacity) {
		this.standardCapacity = standardCapacity;
	}
}
