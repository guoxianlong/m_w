package mmb.stock.fitting.model;

public class FittingStockCard {
	/**
	 * 出入库时间
	 */
	private String datetime;
	
	/**
	 * 配件名称
	 */
	private String fittingName;
	/**
	 * 单据类型
	 */
	private String billType;
	/**
	 * 单据号
	 */
	private String billCode;
	
	/**
	 * 出入库人
	 */
	private String username;
	
	/**
	 * 出入库数量
	 */
	private int count;

	/**
	 * 出入库卡片类型
	 */
	private int cardType;
	
	/**
	 * 配件类型
	 */
	private int fittingType;
	
	public String getFittingTypeName(){
		return FittingBuyStockInBean.fittingTypeMap.get((byte)this.fittingType);
	}
	public int getCardType() {
		return cardType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getFittingName() {
		return fittingName;
	}

	public void setFittingName(String fittingName) {
		this.fittingName = fittingName;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillCode() {
		return billCode;
	}

	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getInOutType() {
		String typeName = "";
		switch (this.cardType) {
		case 20:
			typeName = "检测操作--配件入售后库";
			break;
		case 21:
			typeName = "检测操作--配件入客户库";
			break;		
		case 22:
			typeName = "售后配件采购入库";
			break;
		case 23:
			typeName = "配件领用售后配件入库";
			break;
		case 24:
			typeName = "配件领用客户配件入库";
			break;
		case 25:
			typeName = "配件领用客户配件出库";
			break;
		case 26:
			typeName = "配件领用售后配件出库";
			break;
		case 27:
			typeName = "配件寄回用户";
			break;
		case 28:
			typeName = "配件寄回用户未妥投";
			break;
		case 31:
			typeName = "售后配件维修返还入库";
			break;
		default:
			typeName = "未知类型";
			break;			
		}	
		return typeName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}
