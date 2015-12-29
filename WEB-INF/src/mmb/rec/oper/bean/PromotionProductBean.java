package mmb.rec.oper.bean;

import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;

public class PromotionProductBean {
	
	public int id;


	
	public int promotionId;//活动标识
	
	public int promotionRulerId;//关联活动的id
	
	public String promotionName;//活动名称
	
	public int productId;
	public int seq;//排序
	
	private voProduct product;
	
	private float mmbprice; //mmb单价
	private float disprice; // 成交时计算用的单价
	private int count; // 商品个数
	private int type; // 活动类型
	private int point;
	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int ocountunt) {
		this.count = ocountunt;
	}

	public float getDisprice() {
		return disprice;
	}

	public void setDisprice(float disprice) {
		this.disprice = disprice;
	}

	public voProduct getProduct() {
		return product;
	}

	public void setProduct(voProduct product) {
		this.product = product;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(int promotionId) {
		this.promotionId = promotionId;
	}

	public String getPromotionName() {
		return promotionName;
	}

	public void setPromotionName(String promotionName) {
		this.promotionName = promotionName;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String productCode;

	public int getPromotionRulerId() {
		return promotionRulerId;
	}

	public void setPromotionRulerId(int promotionRulerId) {
		this.promotionRulerId = promotionRulerId;
	}

	public float getMmbprice() {
		return mmbprice;
	}

	public void setMmbprice(float mmbprice) {
		this.mmbprice = mmbprice;
	}
	
}
