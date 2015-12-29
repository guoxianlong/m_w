/*
 * Created on 2005-7-27
 *
 */
package adultadmin.util;

import java.util.Properties;


/**
 * @author zhouj
 *  
 */
public class Constants {
	/**
	 * 是否强制外部登录为HTTPS模式
	 */
	public static boolean OUT_HTTPS = false;
	//by limm: 触屏版图片显示路径
	public static String RESOURCE_TOUCH_IMAGE = "http://localhost:8080/adult-admin-touch/upload/touch/";
	//by hdy: xmemchaced的服务器配置，多个用空格分开。
	public static String MEMCACHED_SERVERS = "192.168.1.203:11211";

	//积分活动显示路径
	public static String RESOURCE_MEMBER_ACTIVE_IMAGE = "http://localhost:8080/adult-member2/upload/member/active/";
	public static String PRODUCT_CONTRAST_IMAGE_URL = "http://localhost/wap/";//"/adult/contrast/img/";
	
	public static Object LOCK = "returnedProduct";//作为商品退货的同步锁
	
	//0：成功，1：包裹单号与订单号不匹配，2：订单号与商品编号不匹配，3：包裹已经入库，不允许重新入库,4:订单号不存在
	//5：包裹号不存在，6：商品条码不存，7：商品不属于订单,8:缺失商品录入成功9：没有输入完整的缺失商品编号10:商品不是缺失商品
	public static String RET_SUC="0";
	public static String RET_PAC_ORDER_UNMATCH="1";
	public static String RET_PRO_ORDER_UNMATCH="2";
	public static String RET_UNENTER="3";
	public static String RET_ORDER_NOTEXIST="4";
	public static String RET_PAC_NOTEXIST="5";
	public static String RET_PRO_NOTEXIST="6";
	public static String RET_PRO_ORDER_BELONG="7";
	public static String RET_SUC_LOSTPRO_ENTER="8";
	public static String RET_UNALL_LOSTPRO_ENTER="9";
	public static final String UNEXP_PRODUCT = "10";
	public static final String UN_INPUTPRODUCT = "11";
	
	public static String NORMAL_STORAGE="0";//正常入库
	public static String EXP_STO_PACORDER="1";//异常入库，包裹订单不匹配
	public static String EXP_STO_PROORDER="2";//异常入库，订单下数量不足

    
	// 新路径配置
	public static String WEB_ROOT = "";
	public static String WEB_PRO_NAME = "wap";
	public static String UPLOAD_ROOT = "/server_root/adult/upload/";
	public static String RESOURCE_PRODUCT_IMAGE = "http://rep.mmb.cn/wap/upload/productImage/";
	public static String RESOURCE_WPRODUCT_IMAGE = "/wap/upload/wproductImage/";
	public static String DOWNLOAD_PATH = "";
	
	//物流员工头像路径
	
	public static String WARE_UPLOAD = "";
	public static String WARE_STAFF_UPLOAD = "";
	public static String STAFF_PHOTO_URL = "";
	//物流声音提醒
	public static String WARE_SOUND_ERROR = "";
	public static String WARE_SOUND_SJYC = "";
	public static String WARE_SOUND_KXP = "";
	
	//快销商品 货位区域
	
	public static String CONSIGNMENT_STOCK_AREA = "U";
	
	//更新索引
	public static String INDEX_SERVER = "192.168.0.252";
    public static int INDEX_SERVER_PORT = 8082;
    public static String INDEX_SERVER_PROTOCOL = "http";
	
	//android客户端上传图片路径地址
	public static String RESOURCE_ANDRIOD_IMAGE = "http://rep.mmb.cn/wap/upload/productImage/";
	//public static String UPLOAD_ANDRIOD_IMAGE = UPLOAD_ROOT + "client/";
	
	//上传图片的地址
	public static String UPLOAD_PRODUCT_IMAGE = UPLOAD_ROOT + "productImage/";
	public static String UPLOAD_WPRODUCT_IMAGE = UPLOAD_ROOT + "wproductImage/";
	public static String UPLOAD_WPOOL_FILE = UPLOAD_ROOT + "wpoolFile/";
	
	public static String UPLOAD_ARTICLE_IMAGE = UPLOAD_ROOT + "articleImage/";

	public static String UPLOAD_PRODUCT_VIDEO = UPLOAD_ROOT + "productVideo/";

	public static String UPLOAD_ORDER_IMAGE = UPLOAD_ROOT + "orderImage/";
	
	public static String CONFIG_PATH = null;
	
	public static String IMPREST_DETAIL_IMAGES_ROOT;//存放备用金明细图片的文件夹
	
	public static Properties config = new Properties();
	public static Properties configCompile = new Properties();
	
	// 旧参数

    public final static String ACTION_SUCCESS_KEY = "success";

    public final static String ACTION_FAILURE_KEY = "failure";

    public final static String SYSTEM_FAILURE_KEY = "systemFailure";

    /**/
      public static String APP_ROOT = WEB_ROOT + "/adult";
      
      public static String RESOURCE_ROOT_URL = WEB_ROOT + "/adult-admin2/rep";
      
      public static float[] PRICE_DELIVER_LINE = { 150, 300, 300 }; // 免邮费的底线
      
      public static float[] PRICE_DELIVER_ADD = { 10, 20, 15 }; // 邮费
      
      public static String RESOURCE_ARTICLE_IMAGE = "/wap/upload/articleImage/";

      public static String UPLOAD_FORUM_IMAGE = UPLOAD_ROOT + "forumImage/";

      public static String RESOURCE_FORUM_IMAGE = "/wap/upload/forumImage/";

      public static String ORDER_IMAGE = UPLOAD_ROOT + "orderImage/";


    public final static int ONLINE_USER_PER_PAGE = 10;
    public final static int TEMPLATE_USER_ID = 138;
    public final static int MESSAGE_PER_PAGE = 10;
    
    public final static int BLOG_ARTICLE_PER_PAGE = 10;
    public final static int BLOG_IMAGE_PER_PAGE = 10;
    public final static int PGAME_PER_PAGE = 10;
    
    public final static int NEWS_WORD_PER_PAGE = 500;
    public final static int NEWS_PER_PAGE = 10;
    public final static int IMAGE_PER_PAGE = 5;
    public final static int BBS_ARTICLE_PER_PAGE = 10;    
    public final static int GUESTBOOK_ARTICLE_PER_PAGE = 10;
    
    
    public static String RESOURCE_ROOT = null;
    public static String ADMIN_QQ = null;
    public static String CONTEXT_PATH = null;
    public final static String NEWS_RESOURCE_ROOT_URL = RESOURCE_ROOT_URL + "news/";
    public final static String JA_RING_RESOURCE_ROOT_URL = RESOURCE_ROOT_URL + "joycoolAdmin/ring/";
    public static String IMAGE_RESOURCE_ROOT_URL = "../rep/image/";
    public static String EBOOK_RESOURCE_ROOT_URL = "../rep/ebook/";
    public final static String GAME_RESOURCE_ROOT_URL = RESOURCE_ROOT_URL + "game/";
    
    public final static String PGAME_RESOURCE_ROOT_URL = RESOURCE_ROOT_URL + "pgame/";
    
    public static String URL_CONTEXT_PREFIX = "/adult";
    
    public static String EBOOK_FILE_URL = "E:/workspace/adult/rep/ebook/";
   
    public final static String SPECIAL_MARK = "%JCSP%";
    public final static String SPECIAL_MONTH_MARK = "%JCSP%MONTH";
    public final static String SPECIAL_DAY_MARK = "%JCSP%DAY";
    public final static String SPECIAL_ONLINE_USER_COUNT_MARK = "%JCSP%ONLINE_USER_COUNT";
    public final static String SPECIAL_NEW_MESSAGE_COUNT_MARK = "%JCSP%NEW_MESSAGE_COUNT";
    public final static String SPECIAL_ONLINE_USER_NICKNAME_MARK = "%JCSP%ONLINE_USER_NICKNAME";
    public final static String SPECIAL_ONLINE_USER_NAME_MARK = "%JCSP%ONLINE_USER_NAME";
    public final static String SPECIAL_INDEX_SEARCH_MARK = "%JCSP%INDEX_SEARCH";


	public static final String JC_MID = "jc_mid";

	public static final String CACHECAROINO = "cache_cargoInfo";//退货作业单确认，session缓存键值


	/**
	 * 区域代码<br/>
	 * 1. 北京
	 */
	public static int AREA_NO_BEIJING = 1;

	/**
	 * 区域代码<br/>
	 * 2. 广东（除广州以外）<br/>
	 */
	public static int AREA_NO_GUANGDONG = 2;

	/**
	 * 区域代码<br/>
	 * 3. 广州
	 */
	public static int AREA_NO_GUANGZHOU = 3;

	/**
	 * 区域代码<br/>
	 * 0. 其它<br/>
	 */
	public static int AREA_NO_QITA = 0;

	/**
	 * 购买方式<br/>
	 * 0. 货到付款<br/>
	 */
	public static int BUY_TYPE_HUODAOFUKUAN = 0;

	/**
	 * 购买方式<br/>
	 * 1. 邮购<br/>
	 * <br/>邮购方式已取消，现该数字代表钱包支付。<br/>
	 */
	public static int BUY_TYPE_YOUGOU = 1;

	/**
	 * 购买方式<br/>
	 * 2. 上门自取<br/>
	 */
	public static int BUY_TYPE_SHANGMENZIQU = 2;

	/**
	 * 购买方式<br/>
	 * 3. 换货订单<br/>
	 */
	public static int BUY_TYPE_NIFFER =3 ;
	
	/**
	 * 货到付款订单允许最小价格<br/>
	 */
	public static int minPrice = 98;

	/**
	 * 货到付款订单允许最大价格<br/>
	 */
	public static int maxPrice = 2000;

	/**
	 * 运费<br/>
	 * 不同地区，不同送货方式的运费
	 */
	public static int[][] postage = new int[10][5];

	/**
	 * 免运费的订单价格<br/>
	 * 不同地区，不同送货方式下，免运费的订单总价格
	 */
	public static int[][] noPostagePrice = new int[10][5];

	static{
		postage[AREA_NO_BEIJING][BUY_TYPE_HUODAOFUKUAN] = 10;
		postage[AREA_NO_BEIJING][BUY_TYPE_YOUGOU] = 0;
		postage[AREA_NO_BEIJING][BUY_TYPE_SHANGMENZIQU] = 0;
		postage[AREA_NO_GUANGDONG][BUY_TYPE_HUODAOFUKUAN] = 15;
		postage[AREA_NO_GUANGDONG][BUY_TYPE_YOUGOU] = 10;
		postage[AREA_NO_GUANGDONG][BUY_TYPE_SHANGMENZIQU] = 0;
		postage[AREA_NO_GUANGZHOU][BUY_TYPE_HUODAOFUKUAN] = 15;
		postage[AREA_NO_GUANGZHOU][BUY_TYPE_YOUGOU] = 0;
		postage[AREA_NO_GUANGZHOU][BUY_TYPE_SHANGMENZIQU] = 0;
		postage[AREA_NO_QITA][BUY_TYPE_HUODAOFUKUAN] = 25;
		postage[AREA_NO_QITA][BUY_TYPE_YOUGOU] = 10;
		postage[AREA_NO_QITA][BUY_TYPE_SHANGMENZIQU] = 10;

		noPostagePrice[AREA_NO_BEIJING][BUY_TYPE_HUODAOFUKUAN] = 0;
		noPostagePrice[AREA_NO_BEIJING][BUY_TYPE_YOUGOU] = 0;
		noPostagePrice[AREA_NO_BEIJING][BUY_TYPE_SHANGMENZIQU] = 0;
		noPostagePrice[AREA_NO_GUANGDONG][BUY_TYPE_HUODAOFUKUAN] = 0;
		noPostagePrice[AREA_NO_GUANGDONG][BUY_TYPE_YOUGOU] = 98;
		noPostagePrice[AREA_NO_GUANGDONG][BUY_TYPE_SHANGMENZIQU] = 0;
		noPostagePrice[AREA_NO_GUANGZHOU][BUY_TYPE_HUODAOFUKUAN] = 0;
		noPostagePrice[AREA_NO_GUANGZHOU][BUY_TYPE_YOUGOU] = 0;
		noPostagePrice[AREA_NO_GUANGZHOU][BUY_TYPE_SHANGMENZIQU] = 0;
		noPostagePrice[AREA_NO_QITA][BUY_TYPE_HUODAOFUKUAN] = 0;
		noPostagePrice[AREA_NO_QITA][BUY_TYPE_YOUGOU] = 98;
		noPostagePrice[AREA_NO_QITA][BUY_TYPE_SHANGMENZIQU] = 0;
	}
	
	/**
	 * 短信模板地址
	 */
	public static String MESSAGE_PATH="";
	
	/**
	 * 发货短信模板名称
	 */
	public static String SENT_OUT_MESSAGE_NAME="";
	/**
	 * 大q发货短信模板名称
	 */
	public static String SENT_OUT_DAQ_MESSAGE_NAME="";
	/**
	 * 亚马逊发货短信模板名称
	 */
	public static String SENT_OUT_AMAZON_MESSAGE_NAME="";
	/**
	 * 当当发货短信模板名称
	 */
	public static String SENT_OUT_DANGDANG_MESSAGE_NAME="";
	/**
	 * 京东发货短信模板名称
	 */
	public static String SENT_OUT_JD_MESSAGE_NAME="";
	
	/**
	 * 京东（成人）发货短信模板名称
	 */
	public static String SENT_OUT_JD_ADULT_MESSAGE_NAME="";
	
	/**
	 * 安心短信模板名称
	 */
	public static String EASE_MESSAGE_NAME="";
	/**
	 * 大Q 手机安心短信模板
	 */
	public static String EASE_MESSAGE_DAQ_NAME = "";
	/**
	 * 成人用品安心短信模板名称
	 */
	public static String EASE_MESSAGE_ADULT_NAME="";
	
	/**
	 * 安心短信2模板名称
	 */
	public static String EASE_MESSAGE_NAME2="";
	
	/**
	 * 包裹单预警模板名称
	 */
	public static String DELIVER_WARNING_NAME="";
	
	/**
	 * 维修商品寄回短信模板
	 */
	public static String BACK_USER_PRODUCT_MESSAGE_NAME="";
	
	/**
	 * 添加返厂商品短信模板
	 */
	public static String BACK_SUPPLIER_MESSAGE_NAME = "";
	
	/**
	 * 发单量定时短信模板
	 */
	public static String ORDER_STOCK_COUNT_MESSAGE_NAME = "";
	
	/**
	 * 签收包裹模板名称
	 */
	public static String DETECT_PACKAGE_MESSAGE_NAME="";
	
	/**
	 * 添加维修报价或点击可以维修操作模板名称
	 */
	public static String REPAIR_PRODUCT_MESSAGE_NAME="";
	
	/**
	 * 评论有奖模板名称
	 */
	public static String PRIZE_MESSAGE_NAME="";
	
	

}