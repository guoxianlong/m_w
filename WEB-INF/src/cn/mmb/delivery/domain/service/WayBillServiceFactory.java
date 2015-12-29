package cn.mmb.delivery.domain.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.mmb.framework.support.SpringHandler;

import mmb.stock.stat.DeliverCorpInfoBean;
@Service
public class WayBillServiceFactory {
	/**
	 * 圆通
	 */
	@Resource(name = "YtWayBillServiceImpl")
	private WayBillService YtWayBillServiceImpl;
	/**
	 * 京东
	 */
	@Resource(name="jdWayBillServiceImpl")
	private WayBillService jdWayBillServiceImpl;
	
	/**
	 * 如风达
	 */
	@Resource(name="RfdWayBillServiceImpl")
	private WayBillService RfdWayBillServiceImpl;
	
	/**
	 * 韵达
	 */
	@Resource(name="YdWayBillServiceImpl")
	private WayBillService YdWayBillServiceImpl;
	
	/**
	 * EMS等默认面单
	 */
	@Resource(name="WayBillServiceImpl")
	private WayBillService WayBillServiceImpl;
	
	public WayBillService create(int deliverId){
		if(deliverId==DeliverCorpInfoBean.DELIVER_ID_YT_WX || deliverId==DeliverCorpInfoBean.DELIVER_ID_YT_CD){
			return YtWayBillServiceImpl;
		}else if(deliverId==DeliverCorpInfoBean.DELIVER_ID_RFD_SD){
			return RfdWayBillServiceImpl;
		}else if(deliverId==DeliverCorpInfoBean.DELIVER_ID_YD){
			return YdWayBillServiceImpl;
		}else if(deliverId==DeliverCorpInfoBean.DELIVER_ID_JD_WX || deliverId==DeliverCorpInfoBean.DELIVER_ID_JD_CD){
			return jdWayBillServiceImpl;
		}else{
			return WayBillServiceImpl;
		}
	}
	
	/** 
	 * @Description: 通过名称获取运单服务类实例
	 * @return T 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月5日 上午11:40:38 
	 */
	public static <T> T getWayBillServiceByServiceName(String serviceName){
		return SpringHandler.getBean(serviceName);
	}
}
