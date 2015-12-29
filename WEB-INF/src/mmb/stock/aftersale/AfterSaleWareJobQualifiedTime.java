package mmb.stock.aftersale;

/**
 * 仓内作业合格时间
 * @author lining
 *
 */
public class AfterSaleWareJobQualifiedTime {
	public int id;
	public String jobName;
	public long qualifiedTime;
	public String remark;
	
	private String qualifiedTimeStr;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public long getQualifiedTime() {
		return qualifiedTime;
	}
	public void setQualifiedTime(long qualifiedTime) {
		this.qualifiedTime = qualifiedTime;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getQualifiedTimeStr() {
		return qualifiedTimeStr;
	}
	public void setQualifiedTimeStr(String qualifiedTimeStr) {
		this.qualifiedTimeStr = qualifiedTimeStr;
	}
	 
}
