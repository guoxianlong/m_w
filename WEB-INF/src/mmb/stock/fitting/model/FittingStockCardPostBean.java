package mmb.stock.fitting.model;

import adultadmin.bean.stock.StockCardBean;
import adultadmin.util.StringUtil;

public class FittingStockCardPostBean {
	private String fittingName;
	private int parentId2;
	private int parentId3;
	private String code;
	private String productName;
	private String startDate;
	private String endDate;
	private int StockType;
	private int stockInType;//配件入库类型

	public String getFittingName() {
		return fittingName;
	}

	public void setFittingName(String fittingName) {
		this.fittingName = fittingName;
	}

	public int getParentId2() {
		return parentId2;
	}

	public void setParentId2(int parentId2) {
		this.parentId2 = parentId2;
	}

	public int getParentId3() {
		return parentId3;
	}

	public void setParentId3(int parentId3) {
		this.parentId3 = parentId3;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public int getStockType() {
		return StockType;
	}

	public void setStockType(int stockType) {
		StockType = stockType;
	}

	public String buildCondition() {
		StringBuffer sb = new StringBuffer();
		sb.append(" AND sc.stock_type = ").append(this.StockType);

		if (this.parentId2 > 0) {
			sb.append(" AND p.parent_id2 = ").append(this.parentId2);
		}
		if (this.parentId3 > 0) {
			sb.append(" AND p.parent_id3 = ").append(this.parentId3);
		}		
		if (code != null && !code.equals("")) {
			sb.append(" AND sc.code = '").append(StringUtil.dealParam(code)).append("' ");
		}
		if(this.fittingName != null && !this.fittingName.equals("")){
			sb.append(" AND p.name LIKE '%").append(StringUtil.dealParam(this.fittingName)).append("%' ");
		}
		
		if (startDate != null && !startDate.equals("") && endDate != null && !endDate.equals("")) {
			String startDateTemp = startDate + " 00:00:00";
			String endDateTemp = endDate + " 23:59:59";
			sb.append(" AND sc.create_datetime BETWEEN '").append(startDateTemp).append("' AND '").append(endDateTemp).append("' ");
		}
		
		if (this.productName != null && !this.productName.equals("")) {			
			sb.append(" AND p.id IN ( SELECT DISTINCT f.fitting_id ");
			sb.append(" 	FROM after_sale_fittings AS f, product AS p ");
			sb.append(" 	WHERE f.product_id = p.id ");
			sb.append(" 	AND p.name LIKE '%").append(StringUtil.dealParam(this.productName)).append("%' ) ");			
		}
		return sb.toString();
	}
}
