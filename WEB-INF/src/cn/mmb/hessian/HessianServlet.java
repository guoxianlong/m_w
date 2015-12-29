package cn.mmb.hessian;

/**
 * 调用接口系统的接口
 * @author likaige
 * @create 2015年5月5日 下午5:49:33
 */
public interface HessianServlet {

	/**
	 * 调用接口系统的通用接口
	 * @param className 类名称[etmsWaybillcodeGet:获取运单号；etmsWaybillSend:发送面单]
	 * @param params
	 * @return
	 * @author likaige
	 * @create 2015年6月5日 上午9:21:16
	 */
	public String service(String className, String... params);

}
