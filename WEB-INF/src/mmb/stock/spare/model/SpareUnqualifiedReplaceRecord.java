package mmb.stock.spare.model;

/**
 * 备用机检测不合格更换单
 * @author lining
 */
public class SpareUnqualifiedReplaceRecord {
	private int id;
	private int oriSpareId;
	private String oriSpareCode;
	private int oriSpareStockinId;
	private int replaceSpareId;
	private String replaceSpareCode;
	private String createDatetime;
	private int operateId;
	private String operateUsername;
	
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getOperateId() {
		return operateId;
	}
	public void setOperateId(int operateId) {
		this.operateId = operateId;
	}
	public String getOperateUsername() {
		return operateUsername;
	}
	public void setOperateUsername(String operateUsername) {
		this.operateUsername = operateUsername;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getOriSpareId() {
		return oriSpareId;
	}
	public void setOriSpareId(int oriSpareId) {
		this.oriSpareId = oriSpareId;
	}
	public String getOriSpareCode() {
		return oriSpareCode;
	}
	public void setOriSpareCode(String oriSpareCode) {
		this.oriSpareCode = oriSpareCode;
	}
	public int getOriSpareStockinId() {
		return oriSpareStockinId;
	}
	public void setOriSpareStockinId(int oriSpareStockinId) {
		this.oriSpareStockinId = oriSpareStockinId;
	}
	public int getReplaceSpareId() {
		return replaceSpareId;
	}
	public void setReplaceSpareId(int replaceSpareId) {
		this.replaceSpareId = replaceSpareId;
	}
	public String getReplaceSpareCode() {
		return replaceSpareCode;
	}
	public void setReplaceSpareCode(String replaceSpareCode) {
		this.replaceSpareCode = replaceSpareCode;
	}
	
	
}
