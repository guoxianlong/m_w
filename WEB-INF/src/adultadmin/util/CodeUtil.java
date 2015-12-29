/*
 * Created on 2006-10-9
 *
 */
package adultadmin.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;



/**
 * @author bomb
 *  
 */
public class CodeUtil {

	/**
	 * 短信订购的开头Z
	 */
	public static String SMS_ORDER_FRONT = "Z";
	private static int stockExchangeNumber = -1;
	private static int buyStockinNumber = -1;
	private static int orderStockNumber = -1;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
	private static DecimalFormat df = new DecimalFormat("0000");
	private static DecimalFormat df2 = new DecimalFormat("000000");

	private static byte[] lock = new byte[0];
	
	public static String getOrderCode()
	{
		synchronized(lock){
			return "A" + getSerialCode();
		}
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2008-6-18
	 * 
	 * 说明：1、 userType: 1
	 * 		带A的订单：销售员为所有分配了客户专员的老用户（购买过商品且有客服专员服务的用户）所下的订单。
	 * 		具体条件如下：管理员所下订单；为老客户且已分配客服专员的用户下订单，在添加新订单时就通过姓名和手机号进行识别；
	 * 		2、 userType: 0
	 * 		带B的订单：销售人员为所有未分配客服专员的老用户（购买过商品且没有客服专员的用户）和新用户所下订单。
	 * 		具体条件如下：管理员所下订单；所有未分配客服专员的用户（包括老用户且在客户回访里未分配列表内的用户  和新用户），在添加新订单时就通过姓名和手机号进行识别
	 * 		3、 userType: 2
	 * 		带T的订单：团购订单，销售人员为团购用户下的订单。
	 * 		4、userType: 3
	 * 		带N的订单，手动设置的订单开头字母
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param userType
	 * @return
	 */
	public static String getOrderCode(int userType)
	{
		synchronized(lock){
			switch (userType) {
			case 0:
				return "B" + getSerialCode();
			case 1:
				return "A" + getSerialCode();
			case 2:
				return "T" + getSerialCode();
			case 3:
				return "MB" + getSerialCode();
			case 4:
				return "QB" + getSerialCode();
			case 5:
				return "PB" + getSerialCode();
			default:
				return "B" + getSerialCode();
			}
//			if(userType == 1){
//				return "A" + getSerialCode();
//			} else if(userType == 0){
//				return "B" + getSerialCode();
//			} else if(userType == 2){
//				return "T" + getSerialCode();
//			} else if(userType == 3){
//				return "N" + getSerialCode();
//			} else {
//				return "B" + getSerialCode();
//			}
		}
	}
	
	public static String getWOrderCode()
	{
		synchronized(lock){
			return "W" + getSerialCode();
		}
	}
	
	public static String getCollectCode()
	{
		synchronized(lock){
			return "C" + getSerialCode();
		}
	}
	
	public static String getSerialCode()
	{
		synchronized(lock){
			Calendar cal = Calendar.getInstance();
			return sdf.format(cal.getTime());
		}
	}
	
	public static String getSOrderCode(){
		synchronized(lock){
			return "S" + getSerialCode();
		}
	}
	
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-4-30
	 * 
	 * 说明：库存调配单编号生成器，number表里面 用了 id=4 的记录
	 * 
	 * 参数及返回值说明：
	 * 
	 * @return
	 */
//	public static String getStockExchangeCode(){
//		synchronized(lock){
//			if(stockExchangeNumber < 0)	// 应用被重新启动，从数据库读入
//			{
//				stockExchangeNumber = getBaseService().getNumber(4);
//			}
//			
//			Calendar cal = Calendar.getInstance();
//	
//			stockExchangeNumber++;
//			if(stockExchangeNumber > 9999)
//				stockExchangeNumber = 1;
//			
//			getBaseService().setNumber(4, stockExchangeNumber);
//			
//			return "DB" + sdf.format(cal.getTime()) + df.format(stockExchangeNumber);
//		}
//	}

	/**
	 * 
	 * 生成短信订单编号
	 * 短信订单生成“S”开头的订单
	 * @return 短信订单编号
	 */
	public static synchronized String getSMSOrderCode()
	{
		Calendar cal = Calendar.getInstance();
		return SMS_ORDER_FRONT + sdf.format(cal.getTime());
	}
}
