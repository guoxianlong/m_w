package cn.mmb.delivery.infrastructrue.adp;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mmb.framework.support.SpringHandler;

@Service
public class WayBillAdaperFactory {
	
	@Resource(name = "YtWayBillAdaperImpl")
	private WayBillAdaper YtWayBillAdaperImpl;
	
	@Resource(name="jdWayBillAdaperImpl")
	private WayBillAdaper jdWayBillAdaperImpl;
	
	@Resource(name="RfdWayBillAdaperImpl")
	private WayBillAdaper RfdWayBillAdaperImpl;
	
	@Resource(name="YdWayBillAdaperImpl")
	private WayBillAdaper YdWayBillAdaperImpl;
	
	public WayBillAdaper create(String deliverName){
		
		if(deliverName.equals("yt")){
			return YtWayBillAdaperImpl;
		}
		if(deliverName.equals("rfd")){
			return RfdWayBillAdaperImpl;
		}
		if(deliverName.equals("yd")){
			return YdWayBillAdaperImpl;
		}
		else if("jd".equals(deliverName)){
			return jdWayBillAdaperImpl;
		}
		return null;
	}
	
	/** 
	 * @Description: 通过名称获取运单服务类实例
	 * @return T 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月5日 上午11:40:38 
	 */
	public static <T> T getWayBillAByServiceName(String serviceName){
		return SpringHandler.getBean(serviceName);
	}
}
