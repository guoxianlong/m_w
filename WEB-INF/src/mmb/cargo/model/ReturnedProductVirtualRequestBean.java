package mmb.cargo.model;

import java.util.List;

public class ReturnedProductVirtualRequestBean {
	/*
	 * 所属仓库
	 */
	private String storage;
	
	/*
	 * 退货上架单作业编号
	 */
	private String operCode;
	
	/*
	 * 退货上架单状态
	 */
	private String operStatus;
	
	/*
	 * 源货位号
	 */
	private String originCargo;
	
	/*
	 * 目的货位号
	 */
	private String targetCargo;
	
	/*
	 * 创建时间start
	 */
	private String createDateStart;
	
	/*
	 * 创建时间end
	 */
	private String createDateEnd;
	
	/*
	 * 页数
	 */
	private Integer page;
	/*
	 * 行数
	 */
	private Integer rows;
	
	/*
	 * 开始行
	 */
	private Integer startRow;
	
	/*
	 * 限制可选仓库
	 */
	private List<String> limitStorage;

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getOperCode() {
		return operCode;
	}

	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}

	public String getOperStatus() {
		return operStatus;
	}

	public void setOperStatus(String operStatus) {
		this.operStatus = operStatus;
	}

	public String getOriginCargo() {
		return originCargo;
	}

	public void setOriginCargo(String originCargo) {
		this.originCargo = originCargo;
	}

	public String getTargetCargo() {
		return targetCargo;
	}

	public void setTargetCargo(String targetCargo) {
		this.targetCargo = targetCargo;
	}

	public String getCreateDateStart() {
		return createDateStart;
	}

	public void setCreateDateStart(String createDateStart) {
		this.createDateStart = createDateStart;
	}

	public String getCreateDateEnd() {
		return createDateEnd;
	}

	public void setCreateDateEnd(String createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}

	public List<String> getLimitStorage() {
		return limitStorage;
	}

	public void setLimitStorage(List<String> limitStorage) {
		this.limitStorage = limitStorage;
	};
	
}
