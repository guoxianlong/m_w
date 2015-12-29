/**
 * 
 */
package adultadmin.framework;

/**
 * @author Bomb
 *
 */
public interface IConstants {
	
	public static final String PROMPT_MESSAGE_KEY="promptMsg";
	
	public static final String ADD_KEY="add";
	
	public static final String MODIFY_KEY="modify";
	
	public static final String SUCCESS_KEY="success";
	
	public static final String RANDOM_KEY="random";
	
	public static final String SUBMIT_KEY="submit";
	
	public static final String FAILURE_KEY="failure";

	public static final String SERVICE_FACTORY_KEY="adultadmin.service.IServiceFactory";
	
	public static final String USER_VIEW_KEY = "userView";
	
	public static final String USER_SERVICE_FACTORY_KEY="net.joycool.web.service.user.IUserServiceFactory";
	
	public static final String USER_LOGIN_KEY="userLogin";
	
	public static final String USER_LOGIN_USERNAME="logusername";
	
	public static final String ORDER_BY_KEY="ASC";
	
	public static final boolean SHOW_INVISIBLE_KEY=false;
	
	public static final int SEARCH_PAGE_NUMBER = 10;	

	public static final int NUM_PERPAGE = 10; 
	
	public static final int AVATAR_UPLOAD_SIZE_LIMIT=61440;

	public static final String GLOBAL_FAILURE_KEY = "GlobalFailure";
	
/**/
	//public static String UPLOAD_ROOT = "E:/workspace/adult/upload/";
	//public static String UPLOAD_ROOT = "D:/newEclipse/workspace/adult/upload/";
	public static String UPLOAD_ROOT = "/server_root/adult/upload/";
	public static String RESOURCE_PRODUCT_IMAGE = "http://rep.mmb.cn/wap/upload/productImage/";
	public static String RESOURCE_WPRODUCT_IMAGE = "/wap/upload/wproductImage/";
	public static String RESOURCE_ARTICLE_IMAGE = "/wap/upload/articleImage/";
	public static String RESOURCE_PRODUCT_VIDEO = "/wap/upload/productVideo/";
	public static float[] PRICE_DELIVER_LINE = {150, 300, 200};	// 免邮费的底线
	public static float[] PRICE_DELIVER_ADD = {10, 20, 15};		// 邮费
	public static String SHOP_NAME = "云雨堂";
/**/
/**	
	public static String UPLOAD_ROOT = "F:/lbj/soft/eclipse/workspace/man/upload/";
	public static String RESOURCE_PRODUCT_IMAGE = "/man/upload/productImage/";
	public static String RESOURCE_ARTICLE_IMAGE = "/man/upload/articleImage/";
	public static float[] PRICE_DELIVER_LINE = {255, 400, 300};	// 免邮费的底线
	public static float[] PRICE_DELIVER_ADD = {10, 20, 15};		// 邮费
	public static String SHOP_NAME = "男人帮";
**/
	
	
	
	
	//上传图片的地址
	public static String UPLOAD_PRODUCT_IMAGE = UPLOAD_ROOT + "productImage/";
	public static String UPLOAD_WPRODUCT_IMAGE = UPLOAD_ROOT + "wproductImage/";
	public static String UPLOAD_WPOOL_FILE = UPLOAD_ROOT + "wpoolFile/";
	
	public static String UPLOAD_ARTICLE_IMAGE = UPLOAD_ROOT + "articleImage/";

	public static String UPLOAD_PRODUCT_VIDEO = UPLOAD_ROOT + "productVideo/";

	public static String UPLOAD_ORDER_IMAGE = UPLOAD_ROOT + "orderImage/";

	/**
	 * MapItem 的类型：分类购买
	 */
	public static int MAP_ITEM_TYPE_CATALOG = 0;
	/**
	 * MapItem 的类型：专题购买
	 */
	public static int MAP_ITEM_TYPE_SUBJECT = 1;
	/**
	 * MapItem 的类型：营销活动
	 */
	public static int MAP_ITEM_TYPE_CAMPAIGN = 2;

	/**
	 * 打折卡 操作类型：增加卡
	 */
	public static int CARD_ACTION_TYPE_ADD = 1;

	/**
	 * 打折卡 操作类型：减少卡
	 */
	public static int CARD_ACTION_TYPE_MINUS = 2;

	/**
	 * 打折卡 操作类型：删除卡
	 */
	public static int CARD_ACTION_TYPE_DEL = 3;
	
	public static String HOUR_LOG_FILE_DIR = "http://211.157.107.134/wap/rep/log/";
}
