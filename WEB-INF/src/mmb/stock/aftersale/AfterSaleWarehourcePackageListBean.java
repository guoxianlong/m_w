package mmb.stock.aftersale;

import java.util.HashMap;
import java.util.Map;


public class AfterSaleWarehourcePackageListBean {
	
	public int id;
	/**邮寄类型：1 寄出 2寄回**/
	public int postType;
	/**包裹寄出/寄回付费方式，1寄付 2到付**/
	public int payType;
	/**运费金额**/
	public float freight;
	/**快递公司**/
	public int deliverId;
	/**快递单号**/
	public String packageCode;
	/**操作人id**/
	public int createUserId;
	/**操作人姓名**/
	public String createUserName;
	/**操作时间**/
	public String createDatetime;
	
	/**寄出 **/
	public static final int POSTTYPE1 = 1;
	/**寄回**/
	public static final int POSTTYPE2 = 2;
	
	/**寄付**/
	public static final int PAYTYPE1 = 1;
	/**到付**/
	public static final int PAYTYPE2 = 2;
	
	public static Map<Integer,String> postTypeMap = new HashMap<Integer, String>();
	public static Map<Integer,String> payTypeMap = new HashMap<Integer, String>();
	
	static{
		postTypeMap.put(POSTTYPE1, "寄出");
		postTypeMap.put(POSTTYPE2, "寄回");
		
		payTypeMap.put(POSTTYPE2, "寄付");
		payTypeMap.put(POSTTYPE2, "到付");
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPostType() {
		return postType;
	}
	public void setPostType(int postType) {
		this.postType = postType;
	}
	public int getPayType() {
		return payType;
	}
	public void setPayType(int payType) {
		this.payType = payType;
	}
	public float getFreight() {
		return freight;
	}
	public void setFreight(float freight) {
		this.freight = freight;
	}
	public int getDeliverId() {
		return deliverId;
	}
	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}
	public String getPackageCode() {
		return packageCode;
	}
	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
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
	public String getCreateDatetime() {
		return createDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
}
