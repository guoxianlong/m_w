package adultadmin.bean.afterSales;

public class AfterSaleOperatingLogBean {
	public int id;
	public int recordId;
	public String recordCode;
	public String operatorName;
	public String operateTime;
	public String content;
	
	//增加五个字段 2013-08-27 李宁
	//增加售后单的两个状态  
	public int prevStatus;//前一个状态
	public int status;//当前状态
	private String nodeConsume;//节点耗时
	private String consumeType;//耗时类别
	private String processNode;//流程节点
	
	
	public String getProcessNode() {
		return processNode;
	}

	public void setProcessNode(String processNode) {
		this.processNode = processNode;
	}

	public String getConsumeType() {
		return consumeType;
	}

	public void setConsumeType(String consumeType) {
		this.consumeType = consumeType;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the recordId
	 */
	public int getRecordId() {
		return recordId;
	}

	/**
	 * @param recordId the recordId to set
	 */
	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	/**
	 * @return the operatorName
	 */
	public String getOperatorName() {
		return operatorName;
	}

	/**
	 * @param operatorName
	 *            the operatorName to set
	 */
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	/**
	 * @return the operateTime
	 */
	public String getOperateTime() {
		return operateTime;
	}

	/**
	 * @param operateTime
	 *            the operateTime to set
	 */
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the recordCode
	 */
	public String getRecordCode() {
		return recordCode;
	}
	
	/**
	 * @param recordCode
	 *            the recordCode to set
	 */
	public void setRecordCode(String recordCode) {
		this.recordCode = recordCode;
	}

	public int getPrevStatus() {
		return prevStatus;
	}

	public void setPrevStatus(int prevStatus) {
		this.prevStatus = prevStatus;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getNodeConsume() {
		return nodeConsume;
	}

	public void setNodeConsume(String nodeConsume) {
		this.nodeConsume = nodeConsume;
	}

	
}
