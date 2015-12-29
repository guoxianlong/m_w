package mmb.cargo.model;

import java.util.List;

/**
 * @descripion 退货上架指向param
 * @author 刘仁华
 * @time  2015年2月4日
 */
public class ReturnedProductDirectRequestBean {
	/*
	 * 所属仓库
	 */
	private String storage;
	
	/*
	 * 所属仓库name
	 */
	private String storageName;
	
	/*
	 * 仓库区域
	 */
	private String storageArea;
	
	/*
	 * 仓库区域name
	 */
	private String storageAreaName;
	
	/*
	 * 巷道 
	 */
	private String[] passage;
	
	/*
	 * 巷道 name
	 */
	private String passageName;
	
	/*
	 * 层数
	 */
	private String[] floorNum;
	
	/*
	 * 一级分类
	 */
	private String[] firstCatalog;
	
	/*
	 * 一级分类name
	 */
	private String firstCatalogName;
	
	/*
	 * 二级分类
	 */
	private String[] secondCatalog;
	
	/*
	 * 二级分类name
	 */
	private String secondCatalogName;
	
	/*
	 * 三级分类
	 */
	private String[] thirdCatalog;
	
	/*
	 * 三级分类
	 */
	private String thirdCatalogName;
	
	/*
	 * 默认仓库区域
	 */
	private String defaultStorageArea;
	
	/*
	 * 默认仓库区域name
	 */
	private String defaultStorageAreaName;
	
	/*
	 * 指向状态
	 */
	private String status;
	
	/*
	 * 指向编号
	 */
	private String directCode;
	
	/*
	 * 创建人
	 */
	private String operator;
	
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
	
	/**
	 * ID
	 */
	private Integer directId;
	
	/*
	 * 限制可选仓库
	 */
	private List<String> limitStorage;
	
	public Integer getStartRow() {
		return startRow;
	}

	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
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

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		this.storage = storage;
	}

	public String getStorageArea() {
		return storageArea;
	}

	public void setStorageArea(String storageArea) {
		this.storageArea = storageArea;
	}

	public String[] getPassage() {
		return passage;
	}

	public void setPassage(String[] passage) {
		if(passage!=null){
			this.passage = passage[0].split(",");
		}
	}

	public String[] getFloorNum() {
		return floorNum;
	}

	public void setFloorNum(String[] floorNum) {
		if(floorNum!=null){
			this.floorNum = floorNum[0].split(",");
		}
	}

	public String[] getFirstCatalog() {
		return firstCatalog;
	}

	public void setFirstCatalog(String[] firstCatalog) {
		if(firstCatalog!=null){
			this.firstCatalog = firstCatalog[0].split(",");
		}
	}

	public String[] getSecondCatalog() {
		return secondCatalog;
	}

	public void setSecondCatalog(String[] secondCatalog) {
		if(secondCatalog!=null){
			this.secondCatalog = secondCatalog[0].split(",");
		}
	}

	public String[] getThirdCatalog() {
		return thirdCatalog;
	}

	public void setThirdCatalog(String[] thirdCatalog) {
		if(thirdCatalog!=null){
			this.thirdCatalog = thirdCatalog[0].split(",");
		}
	}

	public String getDefaultStorageArea() {
		return defaultStorageArea;
	}

	public void setDefaultStorageArea(String defaultStorageArea) {
		this.defaultStorageArea = defaultStorageArea;
	}

	public String getStorageName() {
		return storageName;
	}

	public void setStorageName(String storageName) {
		this.storageName = storageName;
	}

	public String getStorageAreaName() {
		return storageAreaName;
	}

	public void setStorageAreaName(String storageAreaName) {
		this.storageAreaName = storageAreaName;
	}

	public String getPassageName() {
		return passageName;
	}

	public void setPassageName(String passageName) {
		this.passageName = passageName;
	}

	public String getFirstCatalogName() {
		return firstCatalogName;
	}

	public void setFirstCatalogName(String firstCatalogName) {
		this.firstCatalogName = firstCatalogName;
	}

	public String getSecondCatalogName() {
		return secondCatalogName;
	}

	public void setSecondCatalogName(String secondCatalogName) {
		this.secondCatalogName = secondCatalogName;
	}

	public String getThirdCatalogName() {
		return thirdCatalogName;
	}

	public void setThirdCatalogName(String thirdCatalogName) {
		this.thirdCatalogName = thirdCatalogName;
	}

	public String getDefaultStorageAreaName() {
		return defaultStorageAreaName;
	}

	public void setDefaultStorageAreaName(String defaultStorageAreaName) {
		this.defaultStorageAreaName = defaultStorageAreaName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDirectCode() {
		return directCode;
	}

	public void setDirectCode(String directCode) {
		this.directCode = directCode;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
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

	public Integer getDirectId() {
		return directId;
	}

	public void setDirectId(Integer directId) {
		this.directId = directId;
	}

	public List<String> getLimitStorage() {
		return limitStorage;
	}

	public void setLimitStorage(List<String> limitStorage) {
		this.limitStorage = limitStorage;
	}
	
}
