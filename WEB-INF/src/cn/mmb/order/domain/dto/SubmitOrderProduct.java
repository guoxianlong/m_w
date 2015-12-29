package cn.mmb.order.domain.dto;

public class SubmitOrderProduct{

	/** 2代表是混合单*/
	public static final int IS_MIXED2 = 2;
	/** 1代表是纯京东单*/
	public static final int IS_MIXED1 = 1;
	/** 0代表是纯mmb单*/
	public static final int IS_MIXED0 = 0;
	
	private int pop; //商品渠道 0 MMB ,1 京东
	
	private int skuId; //MMB商品id
	
	private int popSkuId; //POP商品id
	
	private int num;
	
	private boolean bNeedAnnex;
	
	private boolean bNeedGift;

	public int getPop() {
		return pop;
	}

	public void setPop(int pop) {
		this.pop = pop;
	}

	public int getSkuId() {
		return skuId;
	}

	public void setSkuId(int skuId) {
		this.skuId = skuId;
	}

	public int getPopSkuId() {
		return popSkuId;
	}

	public void setPopSkuId(int popSkuId) {
		this.popSkuId = popSkuId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public boolean isbNeedAnnex() {
		return bNeedAnnex;
	}

	public void setbNeedAnnex(boolean bNeedAnnex) {
		this.bNeedAnnex = bNeedAnnex;
	}

	public boolean isbNeedGift() {
		return bNeedGift;
	}

	public void setbNeedGift(boolean bNeedGift) {
		this.bNeedGift = bNeedGift;
	}
}
