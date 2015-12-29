package mmb.stock.IMEI;

import java.util.HashMap;


/**
 *IME日志表 
 */
public class IMEILogBean {
	/**
	 * 采购入库
	 */
	public static int OPERTYPE1 = 1;
	/**
	 * 订单
	 */
	public static int OPERTYPE2 = 2;
	/**
	 * 调拨
	 */
	public static int OPERTYPE3 = 3;
	/**
	 * 销售退货入库
	 */
	public static int OPERTYPE4 = 4;
	/**
	 * 售后
	 */
	public static int OPERTYPE5 = 5;
	/**
	 * 备用机添加IMEI码
	 */
	public static int OPERTYPE6 = 6;
	/**
	 * 订单更换IMEI码
	 */
	public static int OPERTYPE7 = 7;
	/**
	 * 售后直接退货
	 */
	public static int OPERTYPE8 = 8;
	/**
	 * 售后检测判断修改状态
	 */
	public static int OPERTYPE9 = 9;
	
	/**
	 * 厂家维修更换商品
	 */
	public static int OPERTYPE10 = 10;
	
	/**
	 * 备用机入库
	 */
	public static int OPERTYPE11 = 11;
	
	/**
	 * 换新机--更换备用机
	 */
	public static int OPERTYPE12 = 12;
	
	/**
	 * 售后库报溢
	 */
	public static int OPERTYPE14 = 14;
	/**
	 * 检测不合格更换备用机
	 */
	public static int OPERTYPE13 = 13;
	
	/**
	 * 寄回用户
	 */
	public static int OPERTYPE16 = 16;
	
	/**
	 * 备用机返还供应商
	 *operCode- 备用机返厂单的包裹号
	 */
	public static int OPERTYPE15 = 15;
	
	
	public static HashMap operTypeMap = new HashMap();
	static {
		operTypeMap.put(Integer.valueOf(OPERTYPE1), "采购入库");
		operTypeMap.put(Integer.valueOf(OPERTYPE2), "订单");
		operTypeMap.put(Integer.valueOf(OPERTYPE3), "调拨");
		operTypeMap.put(Integer.valueOf(OPERTYPE4), "销售退货入库");
		operTypeMap.put(Integer.valueOf(OPERTYPE5), "售后");
		operTypeMap.put(Integer.valueOf(OPERTYPE6), "备用机添加IMEI码");
		operTypeMap.put(Integer.valueOf(OPERTYPE7), "订单更换IMEI码");
		operTypeMap.put(Integer.valueOf(OPERTYPE8), "售后直接退货");
		operTypeMap.put(Integer.valueOf(OPERTYPE9), "售后检测判断修改状态");
		operTypeMap.put(Integer.valueOf(OPERTYPE10), "厂家维修更换商品");
		operTypeMap.put(Integer.valueOf(OPERTYPE11), "备用机入库");
		operTypeMap.put(Integer.valueOf(OPERTYPE14), "售后库报溢");
		operTypeMap.put(Integer.valueOf(OPERTYPE12), "换新机更换备用机");
		operTypeMap.put(Integer.valueOf(OPERTYPE13), "检测不合格更换备用机");
		operTypeMap.put(Integer.valueOf(OPERTYPE15), "备用机返还供应商");
		operTypeMap.put(Integer.valueOf(OPERTYPE16), "寄回用户");
	}
	/**
	 * id
	 */
	public int id;
	/**
	 * 操作编号
	 */
	public String operCode;
	/**
	 * 操作类型
	 */
	public int operType;
	/**
	 * IMEI码
	 */
	public String IMEI;
	/**
	 * 创建时间
	 */
	public String createDatetime;
	/**
	 * 操作人
	 */
	public int userId;
	/**
	 * 操作人姓名
	 */
	public String userName;
	/**
	 * 操作内容
	 */
	public String content;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getOperCode() {
		return operCode;
	}
	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}
	public int getOperType() {
		return operType;
	}
	public void setOperType(int operType) {
		this.operType = operType;
	}
	public String getIMEI() {
		return IMEI;
	}
	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
