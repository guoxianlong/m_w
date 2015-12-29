package mmb.stock.IMEI;

import java.util.HashMap;

/**
 *IME表 
 */
public class IMEIBean {
	/**
	 * 入库中
	 */
	public static int IMEISTATUS1 = 1;
	/**
	 * 可出库
	 */
	public static int IMEISTATUS2 = 2;
	/**
	 * 已出库
	 */
	public static int IMEISTATUS3 = 3;
	/**
	 * 返厂中\已返厂
	 */
	public static int IMEISTATUS4 = 4;
	/**
	 * 已退货
	 */
	public static int IMEISTATUS5 = 5;

	/**
	 * 维修中
	 */
	public static int IMEISTATUS6 = 6;
	/**
	 * 返还供应商
	 */
	public static int IMEISTATUS7 = 7;
	
	
	public static HashMap IMEIStatusMap = new HashMap();
	static {
		IMEIStatusMap.put(Integer.valueOf(IMEISTATUS1), "入库中");
		IMEIStatusMap.put(Integer.valueOf(IMEISTATUS2), "可出库");
		IMEIStatusMap.put(Integer.valueOf(IMEISTATUS3), "已出库");
		IMEIStatusMap.put(Integer.valueOf(IMEISTATUS4), "返厂中");
		IMEIStatusMap.put(Integer.valueOf(IMEISTATUS5), "已退货");
		IMEIStatusMap.put(Integer.valueOf(IMEISTATUS6), "维修中");
		IMEIStatusMap.put(Integer.valueOf(IMEISTATUS7), "返还供应商");
	}
	
	/**
	 * id
	 */
	public int id;
	/**
	 * IMEI号
	 */
	public String code;
	/**
	 * 状态
	 */
	public int status;
	/**
	 * 商品id
	 */
	public int productId;
	/**
	 * 初次录入时间
	 */
	public String createDatetime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}
