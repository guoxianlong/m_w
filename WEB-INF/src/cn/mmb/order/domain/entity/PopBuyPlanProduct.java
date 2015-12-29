package cn.mmb.order.domain.entity;

/**
 * POP采购计划单商品
 * @author likaige
 * @create 2015年9月16日 上午9:26:26
 */
public class PopBuyPlanProduct {
	private int id;
	private int popBuyPlanId;//POP采购计划单id
	private int popProductId;//POP商品id
	private int myProductId;//我司商品id
	private String name;//POP商品名称
	private int popCategory;//POP商品分类id
	private int count;//商品数量
	private double price;//单价(含税)
	private double tax;//税率
	private double nakedPrice;//不含税价
	private int type;//sku类型（0普通、1附件、2赠品）
	private int oid;//oid为主商品skuid，如果本身是主商品，则oid为0）
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPopBuyPlanId() {
		return popBuyPlanId;
	}
	public void setPopBuyPlanId(int popBuyPlanId) {
		this.popBuyPlanId = popBuyPlanId;
	}
	public int getPopProductId() {
		return popProductId;
	}
	public void setPopProductId(int popProductId) {
		this.popProductId = popProductId;
	}
	public int getMyProductId() {
		return myProductId;
	}
	public void setMyProductId(int myProductId) {
		this.myProductId = myProductId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPopCategory() {
		return popCategory;
	}
	public void setPopCategory(int popCategory) {
		this.popCategory = popCategory;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double tax) {
		this.tax = tax;
	}
	public double getNakedPrice() {
		return nakedPrice;
	}
	public void setNakedPrice(double nakedPrice) {
		this.nakedPrice = nakedPrice;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getOid() {
		return oid;
	}
	public void setOid(int oid) {
		this.oid = oid;
	}
	
	
}
