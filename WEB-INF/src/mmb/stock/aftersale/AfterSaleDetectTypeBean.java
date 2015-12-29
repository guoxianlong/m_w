package mmb.stock.aftersale;

import java.util.HashMap;
import java.util.Map;

//检测选项
public class AfterSaleDetectTypeBean {
	
	/**处理意见**/
	public static Map<Integer,String> handleMap = new HashMap<Integer, String>();
	
	/**问题分类**/
	public static final int QUESTION_DESCRIPTION = 1;
	/**包装**/
	public static final int DAMAGED = 2;
	/**赠品**/
	public static final int GIFT_ALL = 3;
	/**故障描述**/
	public static final int FAULT_DESCRIPTION = 4;
	/**申报状态**/
	public static final int REPORT_STATUS = 5;
	/**报价项**/
	public static final int QUOTE_ITEM = 6;
	/**异常原因**/
	public static final int EXCEPTION_REASON = 7;
	/**故障代码**/
	public static final int FAULT_CODE = 8;
	/**IMEI**/
	public static final int IMEI = 9;
	/**备注**/
	public static final int REMARK = 10;
	/**处理意见**/
	public static final int HANDLE = 11;
	/**厂商报价**/
	public static final int SUPPLIER_PRICE = 12;
	/**主商品状态**/
	public static final int MAIN_PRODUCT_STATUS = 13;
	/**发票**/
	public static final int DEBIT_NOTE = 14;
	
	/**完好配件 **/
	public static final int INTACT_FITTING = 15;
	/**损坏配件 **/
	public static final int BAD_FITTING = 16;
	
	/**不合格原因 **/
	public static final int UNQUALIFIED_REASON = 17;
	
	/**换新机商品id*/
	public static final int CHANGE = 18;
	
	/**换新机报价*/
	public static final int REPLACE_QUOTE = 19;


	
	
	/**非下拉框**/
	public static final int TYPE0 = 0;
	/**下拉框**/
	public static final int TYPE1 = 1;
	
	
	public int id;
	public String name; //检测选项名称
	public int type; //类别，下拉框选择或者文本框输入，包括检测原因
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

}
