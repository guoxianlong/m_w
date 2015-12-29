
    /**  
     * 文件名：LogUtil.java  
     *  
     * 版本信息：  
     * 日期：2013-1-31  
     * Copyright 买卖宝 Corporation 2013   
     * 版权所有  
     *  
     */  
    
package mmb.util;


/**  
 * 此类描述的是：  根据被调用者名称获取调用者名称和方法名称,为日志输出服务
 * @author: liubo 
 * @version: 2013-1-31 下午02:53:23   
 */

public class LogUtil {
	
	public static void main(String[] args){
		
	}
	/**
	 * 
     * 此方法描述的是：  根据被调用者名称获取调用者名称和方法名称
     * @calleeClassName:被调用者类全名
     * @author: liubo  
     * @version: 2013-1-31 下午02:55:01
	 */
    public static String getInvokerName(String calleeClassName){
        // 首先得到调用栈
        StackTraceElement stack[] = (new Throwable()).getStackTrace();
        // 然后从栈中向上搜索，直到搜索到我们的calleeClassName类。
        int ix = 0;
        StringBuilder invoker = new StringBuilder();
        while (ix < stack.length){
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (cname.equals(calleeClassName)){
                break;
            }
            ix++;
        }
        // 此时ic位置放置的是calleeClassName类。
        while (ix < stack.length){
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (!cname.equals(calleeClassName)){
                //第一个非calleeClassName类的类就是调用者
                invoker.append(cname);
                invoker.append(".");
                invoker.append(frame.getMethodName());
                break;
            }
            ix++;
        }

        return invoker.toString();
    }
}
