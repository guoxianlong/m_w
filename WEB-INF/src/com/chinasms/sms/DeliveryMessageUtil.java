package com.chinasms.sms;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mmb.ware.WareService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;

import com.ebinf.util.ems.EMSUtil;

public class DeliveryMessageUtil {
	
	/**
	 * 
	 * 功能:发货短信内容
	 * <p>作者 李双 Mar 6, 2012 10:26:14 AM
	 * @param order
	 * @return
	 */
	public static String getDeliveryMessage(int orderId){
		WareService service = new WareService();
		IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String content = null;
		try {
			voOrder order = service.getOrder(orderId);
			if(order==null) return "noOrder";
			
			MailingBalanceBean mailingBean = baService.getMailingBalance("order_id="+orderId); //获取关联的订单号
			if(mailingBean==null) return "noMailing";
			
			order.setPackageNum(mailingBean.getPackagenum());
			if(order.getPackageNum().length()==0){
				 return "noMailing";
			}
			content = "您的买卖宝订单%orderCode%于%datetime%从%area%发货,3-5天送达.包裹单号%packageNum%。"+getDeliverTypeName(order.getDeliver())+"。";
			if (order.getPackageNum().length() == 10 && StringUtil.isNumeric(order.getPackageNum())) { // 广宅
				content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过宅急送发货,预计2-4天送达,快递单号%packageNum%,"
						+ "您可拨打4006789000了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址:http://mmb.cn";
			} else if (order.getDeliver() == 13 && order.getPackageNum().length() == 12
					&& StringUtil.isNumeric(order.getPackageNum())) { // 深圳自建
				content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过深圳自建发货,预计1-3天送达,快递单号%packageNum%,"
						+ "您可拨打4008111111了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址:http://mmb.cn";
			} else if (order.getPackageNum().length() == 12 && StringUtil.isNumeric(order.getPackageNum())) { // 顺丰
				content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过顺丰发货,预计1-3天送达,快递单号%packageNum%,"
						+ "您可拨打4008111111了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址:http://mmb.cn";
			} 
//			else if (order.getPackageNum().endsWith("CN")) {
//				content = "您的产品已发出,包裹单%packageNum%,3-5天左右送达.恶劣天气可能会延误,拨11185可查详情";
//			} else if (order.getPackageNum().endsWith("GD")) { // EMS省内
//				content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过EMS发货,预计1-2天送达,快递单号%packageNum%,"
//						+ "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址:http://mmb.cn";
//			} else if (order.getPackageNum().startsWith("6") || order.getPackageNum().startsWith("16")) {
//				content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。拨4006789000查包裹详情";
//			} else if (order.getPackageNum().startsWith("EC")) { // EMS省外
//				content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过EMS发货,预计2-4天送达,快递单号%packageNum%,"
//						+ "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址:http://mmb.cn";
//			}
			else if(order.getDeliver()==9 || order.getDeliver()==11){
				// content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。";
				MailsBean bean = getEailsBean(order.getPackageNum());
				if(bean!=null){
					content = "您好！您订单（%packageNum%）的最新物流详情是："+bean.getVdealdate()+bean.getVdealaddr()+"-"+bean.getVmailstatus()
						+ "。4008869499";
				}else{
					if (order.getPackageNum().endsWith("CN")) {
						content = "您的产品已发出,包裹单%packageNum%,3-5天左右送达.恶劣天气可能会延误,拨11185可查详情";
					} else if (order.getPackageNum().endsWith("GD")) { // EMS省内
						content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过EMS发货,预计1-2天送达,快递单号%packageNum%,"
								+ "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址:http://mmb.cn";
					} else if (order.getPackageNum().startsWith("6") || order.getPackageNum().startsWith("16")) {
						content = "您的订单%orderCode%已发货,3-5天送达.包裹单号%packageNum%。拨4006789000查包裹详情";
					} else if (order.getPackageNum().startsWith("EC")) { // EMS省外
						content = "您订购的%productName%（订单号%orderCode%金额%totalPrice%元）已通过EMS发货,预计2-4天送达,快递单号%packageNum%,"
								+ "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址:http://mmb.cn";
					}else{
						if (order.getAddress().startsWith("广东省")) {// 客户地址是广东省开头
					
							content = "您订购的%productName%（订单号%orderCode%）已通过EMS发货，预计1-2天送达,快递单号%packageNum%,"
								+ "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址http://mmb.cn";
						} else {// 客户地址不是广东省开头
							content = "您订购的%productName%（订单号%orderCode%）已通过EMS发货，预计2-4天送达,快递单号%packageNum%,"
									+ "您可拨打11185了解送达详情.感谢信赖买卖宝,祝您生活愉快!网址http://mmb.cn";
						}
					}
				}
			}
		
			content = content.replace("%name%", order.getName());
			content = content.replace("%orderCode%", order.getCode());
			if (order.getStockOper() != null) {
				content = content.replace("%datetime%", order.getStockOper()
						.getLastOperTime().substring(5, 10));
				if (order.getStockOper().getArea() == 0) {
					content = content.replace("%area%", "北京");
				} else if (order.getStockOper().getArea() == 1) {
					content = content.replace("%area%", "广州");
				}
			} else {
				content = content.replace("%datetime%", DateUtil.getNow());
				content = content.replace("%area%", "北京");
			}
			content = content.replace("%price%", String.valueOf(order
					.getProductPrice()));
			content = content.replace("%packageNum%", order.getPackageNum());
			content = content.replace("%totalPrice%", NumberUtil.price(order.getDprice()));
			if (content.indexOf("%productName%") > 0) {
				String productName = "";
				List orderProductList = service
						.getOrderProducts("a.order_id = " + order.getId()
								+ " order by b.price desc");
				voOrderProduct orderProduct = (voOrderProduct) orderProductList
						.get(0);
				voProduct product = service.getProduct(orderProduct
						.getProductId());

				productName = product.getName();
				if (productName.length() > 13) {
					productName = productName.substring(0, 13);
				}
				if (orderProductList.size() > 1) {
					productName = productName + "等";
				}
				content = content.replace("%productName%", productName);
				if (content.length() > 128) {
					content = content.replace("感谢信赖买卖宝！", "买卖宝");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
		return content;
	}
	
	public static MailsBean getEailsBean(String packageNum){
		try {
			if(packageNum==null || packageNum.equals("")) return null;
			 
			return getEmsJsonToList(EMSUtil.queryMail(packageNum));
		} catch (Exception e) {
			e.printStackTrace();
 			return  null;
		}
	}
	
	public static void main(String args[]){
		System.out.println(getEailsBean("EC540153255CS").getVdealaddr());
	}
	
	public static MailsBean getEmsJsonToList(String json){
 		JSONObject jsonBean = JSONObject.fromObject(json);
		JSONArray arryBean = JSONArray.fromObject(jsonBean.get("mails"));
		if(arryBean==null || arryBean.size()==0){
			return null;
		}
		JSONArray arry =null;
		for(int i=0;i<arryBean.size();i++){
			JSONObject bean = JSONObject.fromObject(arryBean.get(i));
			arry = JSONArray.fromObject(bean.get("step"));
		}
		List list = new ArrayList();
		for(int i=arry.size()-1;i<arry.size();i++){
			JSONObject bean = JSONObject.fromObject(arry.get(i));
			list.add((MailsBean)JSONObject.toBean(bean, MailsBean.class));
		}
		return list.size()>0?(MailsBean)list.get(0):null;
	}
	
	public static Map deliverMapAll = new LinkedHashMap();
	static{
		//deliverMapAll.put(String.valueOf(7), "北速");
		//deliverMapAll.put(String.valueOf(4), "圆通");
		//deliverMapAll.put(String.valueOf(3), "北京宅急送");
		//deliverMapAll.put(String.valueOf(8), "广东省内");
		deliverMapAll.put(String.valueOf(-1), "未选择");
		deliverMapAll.put(String.valueOf(9), "EMS快递");//广速省外
		deliverMapAll.put(String.valueOf(10), "广州宅急送");
		deliverMapAll.put(String.valueOf(11), "EMS快递");//广东省速递局
		deliverMapAll.put(String.valueOf(12), "顺丰快递");
//		deliverMapAll.put(String.valueOf(13), "深圳自建");
		deliverMapAll.put(String.valueOf(14), "通路速递");
		deliverMapAll.put(String.valueOf(15), "赛澳递快递");
		deliverMapAll.put(String.valueOf(16), "如风达快递");
		deliverMapAll.put(String.valueOf(17), "赛澳递快递");
		deliverMapAll.put(String.valueOf(18), "赛澳递快递");
	}
	
	public static String getDeliverTypeName(int deliver){
		String temp=null;
		temp=String.valueOf(deliverMapAll.get(String.valueOf(deliver)));
		if(temp==null||temp.equals("null")){
			temp="";
		}
		return temp;
	}
}
