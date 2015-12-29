package cn.mmb.hessian;

/**
 * 调用销售系统的接口
 * @author likaige
 * @create 2015年6月3日 下午5:15:42
 */
public interface SaleHessianServlet {

	/** 
	 * @description 根据京东编号查询实际成交价
	 * @param codes 以","分隔的京东编号
	 * @return [{'B01':100}, {'B02':200}]
	 * @returnType String
	 * @create 2015-6-3 下午05:08:08
	 * @author gel
	 */
	String getDpriceByPopCode(String codes);
	
	/**
	 * 一次拆单接口
	 * @param json 数据格式：{"orderId":365432,"userId":125486,"userName":"shenme"}
	 * @return {"success":true,"resultMessage":"","resultCode":null,
	 * "result":"[{"code":MM150902093005,"type":"MMB"},{"code":MJ150902093005,"type":"JD"}]"}
	 * @author likaige
	 * @create 2015年9月16日 上午11:00:16
	 */
	String firstDemolitionOrder(String json);
	
	/**
	 * 下单后订单处理接口
	 * @param json 数据格式：{
		    "success": true,//true下单成功,false下单失败
		    "mmbOrderId": "d125486",//MMB订单号
		    "jdOrderId": "343434",//京东订单号
		    " areaid": "",//发货仓id
		    " resultCode": "",//下单接口的异常code码
		    " resultMessage": "",//下单接口的异常描述
			" userId": 0,
    		" userName": "system"
			}
	 * @return {"resultCode":"x0006","resultMessage":"参数错误","success":false}
	 * @author yaoliang
	 * @create 2015年9月17日 上午11:00:16
	 */
	String updateUserOrderAfterSubmit(String json);

}
