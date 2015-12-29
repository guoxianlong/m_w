package mmb.product.imageRepository;

import java.util.HashMap;
import java.util.Map;

public class ImageRepositoryConstant {
	/**
	 * 商品图品查询页每页显示数量
	 */
	public static final int PRODUCT_PICTURE_PAGE_SIZE = 10;
	
	/**
	 * 商品图品查询  查询主相册标识
	 */
	public static final boolean PRODUCT_PICTURE_MAIN_ALBUM = true;
	
	/**
	 * 商品图品type 类别名称map
	 */
	public static final Map PRODUCT_PICTURE_TYPE_MAP = new HashMap(){{
		put("0", "原始图(2000PX)"); 
		put("1", "wap1.0图集"); 
		put("2", "wap2.0图集"); 
		put("3", "wap2.0大图版图集"); 
		put("9", "浏览图(800PX)"); 
		put("10","扩展图");
		}};
	
	/**
	 * 商品图品type 类别0:原图
		9:浏览图
		1:wap1.0
		2: wap2.0
		3:wap 3.0
	 */
	public static final int PRODUCT_PICTURE_TYPE_0 = 0;
	public static final int PRODUCT_PICTURE_TYPE_1 = 1;
	public static final int PRODUCT_PICTURE_TYPE_2 = 2;
	public static final int PRODUCT_PICTURE_TYPE_3 = 3;
	public static final int PRODUCT_PICTURE_TYPE_9 = 9;
	
	/**
	 * 商品图品sub_type 类别名称map
	 */
	public static final Map PRODUCT_PICTURE_SUB_TYPE_MAP = new HashMap(){{
		put("0", "无版本图"); 
		put("1", "列表图"); 
		put("2", "展示图"); 
		put("3", "搜索页图"); 
		put("4", "高清图"); 
		put("5", "高级编辑图"); 
		put("760","pc版的图");
		}};

		/**
		 * 商品图品sub_type 类别0:无版本图
			1:列表图
			2:展示图
			3:搜索页图
			4:高清图
			5:高级编辑图
		 */
		public static final int PRODUCT_PICTURE_SUBTYPE_0 = 0;
		public static final int PRODUCT_PICTURE_SUBTYPE_1 = 1;
		public static final int PRODUCT_PICTURE_SUBTYPE_2 = 2;
		public static final int PRODUCT_PICTURE_SUBTYPE_3 = 3;
		public static final int PRODUCT_PICTURE_SUBTYPE_4 = 4;
		public static final int PRODUCT_PICTURE_SUBTYPE_5 = 5;
}
