package cn.mmb.order.domain.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常码
 * @author likaige
 * @create 2015年9月17日 上午8:43:45
 */
public class ExceptionCode {
	
	/**京东异常码与内部异常码的对应关系*/
	public static Map<String,String> exceptionCodeMap = new HashMap<String, String>();
	/**内部异常码描述*/
	public static Map<String,String> localCodeMsgMap = new HashMap<String, String>();
	
	public static final String CODE_001 = "001"; //系统异常
	public static final String CODE_101 = "101";
	public static final String CODE_103 = "103";
	public static final String CODE_107 = "107"; //
	public static final String CODE_108 = "108"; //保存计划单，更新价格、状态出现异常
	public static final String CODE_109 = "109"; //京东商品不存在
	public static final String CODE_110 = "110"; //MMB订单不存在
	public static final String CODE_111 = "111"; //调用接口initSplitHistory失败
	public static final String CODE_206 = "206"; //更新计划单时出现异常
	public static final String CODE_501 = "501"; //查询计划单成功
	
	static{
		//下单
		localCodeMsgMap.put("101", "下单成功");
		localCodeMsgMap.put("102", "下单成功");
		localCodeMsgMap.put(CODE_103, "库存不足");
		localCodeMsgMap.put("104", "配额不足或者已被锁定");
		localCodeMsgMap.put("105", "下单失败");
		localCodeMsgMap.put("106", "下单失败");
		localCodeMsgMap.put(CODE_107, "提交的JSON数据有误");
		localCodeMsgMap.put(CODE_108, "保存计划单，更新价格、状态出现异常");
		localCodeMsgMap.put(CODE_109, "京东商品不存在");
		localCodeMsgMap.put(CODE_110, "MMB订单不存在");
		localCodeMsgMap.put(CODE_111, "调用接口initSplitHistory失败");
		//确认订单
		localCodeMsgMap.put("201", "确认订单成功");
		localCodeMsgMap.put("202", "确认订单操作失败");
		localCodeMsgMap.put("203", "jdOrderId不存在");
		localCodeMsgMap.put("204", "确认订单成功");
		localCodeMsgMap.put("205", "确认订单操作失败");
		localCodeMsgMap.put(CODE_206, "更新计划单时出现异常");
		//取消订单
		localCodeMsgMap.put("301", "取消订单成功");
		localCodeMsgMap.put("302", "取消订单操作失败");
		localCodeMsgMap.put("303", "jdOrderId不存在");
		localCodeMsgMap.put("304", "取消订单成功");
		localCodeMsgMap.put("305", "不能取消已经生产订单");
		localCodeMsgMap.put("306", "不能取消已确认订单");
		localCodeMsgMap.put("307", "取消订单操作失败");
		//拆单
		localCodeMsgMap.put("401", "拆单失败");
		localCodeMsgMap.put("402", "拆单成功");
		localCodeMsgMap.put("403", "重复拆单");
		localCodeMsgMap.put("404", "订单号不存在");
		localCodeMsgMap.put("405", "订单不是混合单");
		
		//查询计划单
		localCodeMsgMap.put(CODE_501, "成功");
		//下单处理接口
		localCodeMsgMap.put("601", "下单-->未知错误，请联系系统管理员处理");
		localCodeMsgMap.put("602", "参数错误，请联系系统管理员处理");
		localCodeMsgMap.put("603", "系统超时，请稍后再试");
		localCodeMsgMap.put("604", "下单-->操作完成");
		localCodeMsgMap.put("605", "下单-->操作未完成");
				
		//下单
		exceptionCodeMap.put("0001", "101"); //下单成功
		exceptionCodeMap.put("0008", "102"); //下单成功
		exceptionCodeMap.put("3008", "103"); //库存不足
		exceptionCodeMap.put("3016", "104"); //配额不足或者已被锁定
		exceptionCodeMap.put("3057", "105"); //下单失败
		exceptionCodeMap.put("3058", "106"); //下单失败
		//确认订单
		exceptionCodeMap.put("0003", "201"); //确认订单成功
		exceptionCodeMap.put("3101", "202"); //确认订单操作失败
		exceptionCodeMap.put("3102", "203"); //jdOrderId不存在
		exceptionCodeMap.put("3103", "204"); //确认订单成功
		exceptionCodeMap.put("3108", "205"); //确认订单操作失败
		//取消订单
		exceptionCodeMap.put("0002", "301"); //取消订单成功
		exceptionCodeMap.put("3201", "302"); //取消订单操作失败
		exceptionCodeMap.put("3202", "303"); //jdOrderId不存在
		exceptionCodeMap.put("3203", "304"); //取消订单成功
		exceptionCodeMap.put("3204", "305"); //不能取消已经生产订单
		exceptionCodeMap.put("3208", "306"); //不能取消已确认订单
		exceptionCodeMap.put("3212", "307"); //取消订单操作失败
		//拆单
		exceptionCodeMap.put("x0002", "401"); //拆单失败
		exceptionCodeMap.put("x0001", "402"); //拆单成功
		exceptionCodeMap.put("x0003", "403"); //重复拆单
		exceptionCodeMap.put("x0004", "404"); //订单号不存在
		exceptionCodeMap.put("x0005", "405"); //订单不是混合单
		//下单处理接口
		exceptionCodeMap.put("x0000", "601"); //未知错误
		exceptionCodeMap.put("x0006", "602"); //参数异常
		exceptionCodeMap.put("x0007", "603"); //系统超时
		exceptionCodeMap.put("x0010", "604"); //下单后操作完成
		exceptionCodeMap.put("x0011", "605"); //下单后
		
	}

}
