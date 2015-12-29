package mmb.stock.aftersale;

public class AfterSaleLogBean {
	/**
	 * 客户寄回包裹签收
	 */
	public static final int TYPE1 = 1;
	/**
	 * 厂商寄回包裹签收
	 */
	public static final int TYPE2 = 2;
	/**
	 * 未妥投包裹签收
	 */
	public static final int TYPE3 = 3;
	/**
	 * 商品检测
	 */
	public static final int TYPE4 = 4;
	/**
	 * 再次检测
	 */
	public static final int TYPE5 = 5;	
	/**
	 * 商品上架
	 */
	public static final int TYPE6 = 6;	
	/**
	 * 添加返厂商品
	 */
	public static final int TYPE7 = 7;
	/**
	 * 寄回用户
	 */
	public static final int TYPE8 = 8;
	/**
	 * 封箱
	 */
	public static final int TYPE9 = 9;
	/**
	 * 售后入库
	 */
	public static final int TYPE10 = 10;
	/**
	 * 新建调拨单
	 */
	public static final int TYPE11 = 11;
	/**
	 * 返厂
	 */
	public static final int TYPE12 = 12;
	/**
	 * 匹配
	 */
	public static final int TYPE13 = 13;
	/**
	 * 无法维修
	 */
	public static final int TYPE14 = 14; 
	/**
	 * 封箱与解封
	 */
	public static final int TYPE15 = 15;
	/**
	 * 报损与报溢
	 */
	public static final int TYPE16 = 16;
	
	/**
	 * 厂家维修更换商品
	 */
	public static final int TYPE17 = 17;
	/**
	 * 匹配失败
	 */
	public static final int TYPE18 = 18;
	/**
	 * 检测合格
	 */
	public static final int TYPE19 = 19;
	/**
	 * 检测不合格
	 */
	public static final int TYPE20 = 20;
	/**
	 * 维修报价
	 */
	public static final int TYPE21 = 21;
	/**
	 * 可以维修
	 */
	public static final int TYPE22 = 22;
	
	/**
	 * 修改匹配商品--增加（operCode为包裹单号）
	 */
	public static final int TYPE23 = 23;
	
	/**
	 * 修改匹配商品--删除（operCode为包裹单号）
	 */
	public static final int TYPE24 = 24;
	
	/**
	 * 售后库报溢(operCode为处理单code)
	 */
	public static final int TYPE25 = 25;
	
	public int id;
	public int type;
	public String content;
	public int count;
	public String createDatetime;
	public int createUserId;
	public String createUserName;
	//售后处理单号
	public String operCode;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	public String getOperCode() {
		return operCode;
	}
	public void setOperCode(String operCode) {
		this.operCode = operCode;
	}
}
