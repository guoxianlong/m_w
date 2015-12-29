package cn.mmb.hessian;

/**
 * 处理POP采购计划的Hessian接口
 * @author likaige
 * @create 2015年9月16日 上午9:13:39
 */
public interface PopBuyPlanHessianService {

	/**
	 * 下采购计划单
	 * <br/>调用方：前端和销售后台
	 * @param json JSON格式的字符串，例如：{orderCode:"B001",addr1:201,addr2:202,addr3:203,addr4:204,
	 * productList:[{pop:2,skuId:1234,num:2,bNeedAnnex:true,bNeedGift:false}]}
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	String submitBuyPlan(String json);
	
	/**
	 * 确认采购计划单
	 * <br/>调用方：销售后台
	 * @param json JSON格式的字符串，例如：{"jdOrderId":"911"}
	 * @return {"success":true,"resultMessage":"下单成功！","resultCode":"201"}
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	String confirmBuyPlan(String json);
	
	/**
	 * 取消采购计划单
	 * <br/>调用方：销售后台
	  * @param json JSON格式的字符串，例如：{"jdOrderId":"911"}
	 * @return @return {"success":true,"resultMessage":"下单成功！","resultCode":"301"}
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	String cancleBuyPlan(String json);
	
	/**
	 * 查询采购计划单
	 * <br/>调用方：销售后台
	 * @param json JSON格式的字符串，例如：{"orderCode":"B11"}，orderCode：表示MMB订单号
	 * @return JSON数据：{"success":true,"resultMessage":"成功！","resultCode":"501",
	 * "result":{"mmbOrderId":"B001","sku":[{"skuId":852431,"num":1,"type":0,"oid":0},
	 * {"skuId":852431,"num":1,"type":2,"oid":852431}]}}
	 * @author likaige
	 * @create 2015年9月16日 上午9:14:46
	 */
	String queryBuyPlan(String json);
}
