package cn.mmb.hessian;

/**
 * 处理运单相关的hessian接口
 * @author likaige
 * @create 2015年5月6日 上午11:06:08
 */
public interface DeliveryHessianService {

	/**
	 * 处理异常面单数据//
	 * @param json JSON格式的运单编号数据，如：{popType:0,deliverCodeList:['P01','P02']}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:100,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月6日 上午11:06:50
	 */
	String processExceptionWaybill(String json);

	/** 
	 * 配送信息接收接口
	 * @param json JSON格式的订单配送数据，如：{deliveryId:'P01',pop:0,trace_api_dtos:[
	 * {ope_remark:"您的订单已分配",ope_time:"2013/06/25 09:16:09",ope_status:0,ope_name:"张三"},{}]}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @create 2015-5-7 上午11:11:23
	 * @author gel
	 */
	String processDeliverInformation(String json);
	
	/**
	 * 初始化大客户运单数据的接口
	 * @param json JSON格式的运单数据，如：{popType:1,popOrderCode:'JD01',time:'2015-01-01 01:01:01'}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月15日 上午8:48:34
	 */
	String initPOPDeliverInfoData(String json);
	
	/**
	 * 发送大客户订单出库短信
	 * @param json JSON格式的POP订单号数据，如：{orderId:"9527460481"}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年6月1日 上午9:42:04
	 */
	String sendPopShortMessage(String json);
	
	/**
	 * 修补POP订单信息
	 * @param json JSON格式的订单数据，如：[{orderId:123,orderCode:"B01",popOrderCode:"JD01"},{}]
	 * <br/>orderCode:表示MMB子订单编号；popOrderCode:表示POP子订单编号
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年6月2日 上午9:50:34
	 */
	//String repairPopOrderInfo(String json);
}
