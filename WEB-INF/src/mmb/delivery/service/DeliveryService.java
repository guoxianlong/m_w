package mmb.delivery.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.delivery.domain.DeliverInfo;

/**
 * 处理运单相关业务
 * @author likaige
 * @create 2015年4月28日 下午5:40:22
 */
public interface DeliveryService {
	
	/**
	 * 获取运单号
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月28日 下午5:41:10
	 */
	void getDeliveryCode() throws Exception;
	
	/**
	 * 发送MMB面单信息
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月30日 下午2:10:56
	 */
	void sendWaybill() throws Exception;
	
	/**
	 * 发送POP面单信息
	 * @throws Exception
	 * @author likaige
	 * @create 2015年4月30日 下午2:10:56
	 */
	void sendPopWaybill() throws Exception;

	/**
	 * 处理异常面单数据
	 * @param json JSON格式的运单编号数据，如：{popType:0,deliverCodeList:['P01','P02']}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:100,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月6日 上午11:06:50
	 */
	String processExceptionWaybill(String json) throws Exception;
	
	/**
	 * 初始化大客户运单数据的接口
	 * @param json JSON格式的运单数据，如：{popType:1,popOrderCode:'JD01',time:'2015-01-01 01:01:01'}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年5月15日 上午8:48:34
	 */
	String initPOPDeliverInfoData(String json) throws Exception;
	
	/**
	 * POP配送状态查询
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @create 2015年5月8日 上午11:06:50
	 */
	List<DeliverInfo> getPOPDeliverInfoList(Map<String,Object> paramMap) throws Exception;
	
	/** 
	 * 配送信息接收接口
	 * @param json JSON格式的订单配送数据，如：{deliveryId:'P01',pop:0,trace_api_dtos:[
	 * {ope_remark:"您的订单已分配",ope_time:"2013/06/25 09:16:09",ope_status:0,ope_name:"张三"},{}]}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @create 2015-5-7 上午11:11:23
	 * @author gel
	 */
	void processDeliverInformation(String json) throws Exception;

	/**
	 * 导出POP配送状态查询列表
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @return 
	 * @create 2015年5月14日 上午11:06:50
	 */
	void exportPOPDeliverInfoList(HttpServletRequest request,HttpServletResponse response,Map<String,Object> paramMap) throws Exception;

	/**
	 * 查询运单信息
	 * @param param前台传的参数
	 * @return配送信息列表
	 * @author yaoliang 
	 * @return 
	 * @create 2015年5月14日 上午11:06:50
	 */
	List<DeliverInfo> getPOPDeliverInfo(Map<String,Object> paramMap) throws Exception;
	
	/**
	 * 获取订单号或包裹单号
	 * @author yaoliang 
	 * @create 2015年5月16日  上午8:40:22
	 * @param code 单号
	 * @param scanType 区分订单号和包裹单号
	 */
	String getPOPDeliverOrderCode(String code, int scanType) throws Exception;

	/**
	 * 组装配送信息
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-23
	 */
	List<HashMap<String, String>> getPOPDeliverInfo(List<HashMap<String, String>> listRows,String date);

	/**
	 * 根据POPId获取省份
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-25
	 */
	List<Map<String, Object>> getProvicesByPOPId(int popId);

	/**
	 * 根据省id获取市集合
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-26 上午9:01:10
	 */
	List<Map<String, Object>> getCitysByProvinceId(int provinceId);

	/**
	 * 根据市id获取区集合
	 * @param code
	 * @return
	 * @create yaoliang
	 * @time 2105-05-26 上午9:01:10
	 */
	List<Map<String, Object>> getDistrictsByCityId(int cityId);

	/**
	 * 发送大客户订单出库短信
	 * @param json JSON格式的POP订单号数据，如：{popType:1,orderId:"9527460481"}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年6月1日 上午9:42:04
	 */
	String sendPopShortMessage(String json) throws Exception;

	/**
	 * 修补POP订单信息
	 * @param json JSON格式的订单数据，如：[{orderId:123,orderCode:"B01",popOrderCode:"JD01"},{}]
	 * <br/>orderCode:表示MMB子订单编号；popOrderCode:表示POP子订单编号
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author likaige
	 * @create 2015年6月2日 上午9:50:34
	 */
	//String repairPopOrderInfo(String json) throws Exception;

	/**
	 * 发送大客户订单出库短信
	 * @throws Exception
	 * @author likaige
	 * @create 2015年6月3日 上午10:30:32
	 */
	void sendPopShortMessage() throws Exception;

	/**
	 * 修补POP订单信息
	 * @author likaige
	 * @create 2015年6月6日 下午1:06:18
	 */
	void repairPopOrderInfo() throws Exception;
	
	/**
	 * POP订单监控
	 * @throws Exception
	 * @author likaige
	 * @create 2015年7月28日 下午4:44:14
	 */
	void popOrderMonitor() throws Exception;
	
	/**
	 * 更改订单状态  当当前时间减去订单创建时间>=1小时时将订单状态改为-1即发送失败
	 * @author lml
	 * @create 2015-08-11 10:55:01
	 */
	void updateOrderStatusToFail() throws Exception ;

	/**
	 * 
	 * @description:标记没有采销表数据pop_order_info状态为-3
	 * @throws Exception
	 * @returnType: void
	 * @create:2015年9月18日 下午12:40:25
	 */
	void markNoBuyOrderPopInfo() throws Exception;
}
