/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年8月10日 下午1:55:24 
 * @version V1.0   
 */
package cn.mmb.hessian;

/** 
 * @ClassName: WayBillHessianService 
 * @Description: 处理物流信息相关的hessian接口
 * @author: 叶二鹏
 * @date: 2015年8月10日 下午1:55:24  
 */
public interface WayBillHessianService {
	
	/** 
	 * 配送信息接收接口
	 * @param json JSON格式的订单配送数据，如：{deliveryId:'P01',pop:0,trace_api_dtos:[
	 * {ope_remark:"您的订单已分配",ope_time:"2013/06/25 09:16:09",ope_status:0,ope_name:"张三"},{}]}
	 * @return JSON格式的字符串，如：{code:200,message:'成功'}或{code:-1,message:'错误信息'}
	 * @author 叶二鹏
	 * @date 2015年8月10日 下午2:11:44 
	 */
	String processWayBillInformation(String json);

}
