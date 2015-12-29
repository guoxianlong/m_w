package com.chinasms.sms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import adultadmin.util.SmsSender;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class SenderSMS3{
	
	public static String send(int userId, String dst, String msg) {

		String [] phones = dst.split("[ \\t\\n\\r,;]");
		List phoneList = new ArrayList();
		for(int i=0; i<phones.length; i++){
			if(!StringUtil.isMobile(phones[i])){
				continue;
			}
			phoneList.add(phones[i]);
		}

		String tip = "";
		boolean res = false;
		try {
			//2013-12-18 zhaolin 不切分短信
			Iterator iter = phoneList.listIterator();
			while(iter.hasNext()){
				String phone = (String)iter.next();

				res = SmsSender.sendSMS(phone, msg, 55, userId);
				if(res){
					tip = "提醒短信发送成功";
				} else {
					tip = "提醒短信发送出现故障";
				}
				return tip;
			}
			
//			
//			if(msg.indexOf("受春节期间物流影响,")>0){//春节期间，EMS省外和广宅的包裹单中会有该内容，春节过后取消
//				String content1 = msg.substring(0, msg.indexOf("受春节期间物流影响,")+19);
//				String content2 = msg.substring(msg.indexOf("受春节期间物流影响,")+19, msg.length());
//				
//				StringBuilder result = new StringBuilder();
//				Iterator iter = phoneList.listIterator();
//				while(iter.hasNext()){
//					String phone = (String)iter.next();
//
//					res = SmsSender.sendSMS(phone, content1, 55, userId);
//
//					res = SmsSender.sendSMS(phone, content2, 55, userId);
//
//					if(res){
//						tip = "提醒短信发送成功";
//					} else {
//						tip = "提醒短信发送出现故障";
//					}
//					result.append(tip);
//					
//				}
//				return tip;
//			}else if(msg.indexOf("快递单号") > 0){
//				
//				String content1 = msg.substring(0, msg.indexOf("快递单号")+2);
//				String content2 = msg.substring(msg.indexOf("快递单号")+2, msg.length());
//				
//				StringBuilder result = new StringBuilder();
//				Iterator iter = phoneList.listIterator();
//				while(iter.hasNext()){
//					String phone = (String)iter.next();
//
//					res = SmsSender.sendSMS(phone, content1, 55, userId);
//
//					res = SmsSender.sendSMS(phone, content2, 55, userId);
//
//					if(res){
//						tip = "提醒短信发送成功";
//					} else {
//						tip = "提醒短信发送出现故障";
//					}
//					result.append(tip);
//					
//				}
//				return tip;
//			}else if(msg.length() > MAX_LENGTH){//如果信息字数大于200，就手工拆解为 67 个字的信息
//				StringBuilder result = new StringBuilder();
//				int index = 0;
//				while(true){
//					if(index + MAX_LENGTH_PER_MSG >= msg.length()){
//						Iterator iter = phoneList.listIterator();
//						while(iter.hasNext()){
//							String phone = (String)iter.next();
//
//							res = SmsSender.sendSMS(phone, msg.substring(index, msg.length()), 55, userId);
//							if(res){
//								tip = "提醒短信发送成功";
//							} else {
//								tip = "提醒短信发送出现故障";
//							}
//							//result.append(sender.massSend(dst, msg.substring(index, msg.length()), "", ""));
//							result.append(tip);
//						}
//						break;
//					} else {
//
//						Iterator iter = phoneList.listIterator();
//						while(iter.hasNext()){
//							String phone = (String)iter.next();
//	
//							res = SmsSender.sendSMS(phone, msg.substring(index, index + MAX_LENGTH_PER_MSG), 55, userId);
//							if(res){
//								tip = "提醒短信发送成功";
//							} else {
//								tip = "提醒短信发送出现故障";
//							}
//							//result.append(sender.massSend(dst, msg.substring(index, index + MAX_LENGTH_PER_MSG), "", ""));
//							result.append(tip);
//						}
//					}
//					index = index + MAX_LENGTH_PER_MSG;
//				}
//				return result.toString();
//			} else {
//
//				Iterator iter = phoneList.listIterator();
//				while(iter.hasNext()){
//					String phone = (String)iter.next();
//
//					res = SmsSender.sendSMS(phone, msg, 55, userId);
//					if(res){
//						tip = "提醒短信发送成功";
//					} else {
//						tip = "提醒短信发送出现故障";
//					}
//					//return sender.massSend(dst, msg, "", "");
//					return tip;
//				}
//			}
			
		} catch (Exception e) {
			e.printStackTrace();
		    tip = "提醒短信发送出现故障";
		}
		return tip;
	}
	
	public static boolean sendMore(int userId, String dst, String msg) {

		String [] phones = dst.split("[ \\t\\n\\r,;]");
		List phoneList = new ArrayList();
		for(int i=0; i<phones.length; i++){
			if(!StringUtil.isMobile(phones[i])){
				continue;
			}
			phoneList.add(phones[i]);
		}

		try {
			//2013-12-18 zhaolin 不切分短信
			Iterator iter = phoneList.listIterator();
			while(iter.hasNext()){
				String phone = (String)iter.next();
				if(!SmsSender.sendSMS(phone, msg, 55, userId)){
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 可选择通道号的短信发送方法，与 基本方法一致，只是需要填写通道号
	 * @param userId 用户id
	 * @param dst 电话号码字符串
	 * @param msg 短信内容
	 * @param sub 通道号
	 * @return 信息发送结果提示
	 * 郝亚斌
	 */
	public static String send(int userId, String dst, String msg,int sub) {
		
		String [] phones = dst.split("[ \\t\\n\\r,;]");
		List phoneList = new ArrayList();
		for(int i=0; i<phones.length; i++){
			if(!StringUtil.isMobile(phones[i])){
				continue;
			}
			phoneList.add(phones[i]);
		}
		
		String tip = "";
		boolean res = false;
		try {
			//2013-12-18 zhaolin 不切分短信
			Iterator iter = phoneList.listIterator();
			while(iter.hasNext()){
				String phone = (String)iter.next();
				
				res = SmsSender.sendSMS(phone, msg, sub, userId);
				if(res){
					tip = "提醒短信发送成功";
				} else {
					tip = "提醒短信发送出现故障";
				}
				return tip;
			}
		} catch (Exception e) {
			e.printStackTrace();
			tip = "提醒短信发送出现故障";
		}
		return tip;
	}

	public static String send(int userId, String dst, String msg, String time, String subNo) {
		String tip = "";

		boolean res = SmsSender.sendSMS(dst, msg, 54, userId);
		if(res){
			tip = "提醒短信发送成功";
		} else {
			tip = "提醒短信发送出现故障";
		}
		return tip;
	}
	/**
	 * 
	 *方法描述：向“shortmessage59”表插入预警短信记录
	 *
	 *创建人：敖海晨
	 *
	 * 时间：2014-3-13
	 */
	public static void sendDeliverWarning(int userId, String dst, String msg) {
		boolean res = SmsSender.sendSMS(dst, msg, 59, userId);
		if(!res){
			System.out.println("提醒短信发送出现故障");
		}
	}
}
