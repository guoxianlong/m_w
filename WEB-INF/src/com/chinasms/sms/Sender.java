package com.chinasms.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import adultadmin.util.SmsSender;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class Sender
{
	public static int MAX_LENGTH = 60;
	public static int MAX_LENGTH_PER_MSG = 60;
	public static int MAX_PHONE_COUNT = 100;
	//public static Sender sender = new Sender("ydsj", "123456");
	public static String send(int userId, String dst, String msg) {
		msg += "";

		String [] phones = dst.split("[ \\t\\n\\r,;]");
		List phoneList = new ArrayList();
		for(int i=0; i<phones.length; i++){
			if(!StringUtil.isMobile(phones[i])){
				continue;
			}
			phoneList.add(phones[i]);
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		String tip = "";
		boolean res = false;
		try {
			if(msg.length() > MAX_LENGTH){//如果信息字数大于200，就手工拆解为 67 个字的信息
				StringBuilder result = new StringBuilder();
				int index = 0;
				while(true){
					if(index + MAX_LENGTH_PER_MSG >= msg.length()){
						Iterator iter = phoneList.listIterator();
						while(iter.hasNext()){
							String phone = (String)iter.next();
							
							res = SmsSender.sendSMS(phone, msg.substring(index, msg.length()), 54, userId);
							if(res){
								tip = "提醒短信发送成功";
							} else {
								tip = "提醒短信发送出现故障";
							}
							//result.append(sender.massSend(dst, msg.substring(index, msg.length()), "", ""));
							result.append(tip);
						}
						break;
					} else {

						Iterator iter = phoneList.listIterator();
						while(iter.hasNext()){
							String phone = (String)iter.next();
							
							res = SmsSender.sendSMS(phone, msg.substring(index, index + MAX_LENGTH_PER_MSG), 54, userId);
							if(res){
								tip = "提醒短信发送成功";
							} else {
								tip = "提醒短信发送出现故障";
							}
							//result.append(sender.massSend(dst, msg.substring(index, index + MAX_LENGTH_PER_MSG), "", ""));
							result.append(tip);
						}
					}
					index = index + MAX_LENGTH_PER_MSG;
				}
				return result.toString();
			} else {

				Iterator iter = phoneList.listIterator();
				while(iter.hasNext()){
					String phone = (String)iter.next();

					res = SmsSender.sendSMS(phone, msg, 54, userId);
					if(res){
						tip = "提醒短信发送成功";
					} else {
						tip = "提醒短信发送出现故障";
					}
					//return sender.massSend(dst, msg, "", "");
					return tip;
				}
			}
			
		} catch (Exception e) {
		    tip = "提醒短信发送出现故障";
		} finally {
			dbOp.release();
		}
		return tip;
	}

	public static String multiSend(int userId, String dst, String msg) {
		msg += "";
		String [] phones = dst.split("[ \\t\\n\\r,;]");
		List phoneList = new ArrayList();
		for(int i=0; i<phones.length; i++){
			if(!StringUtil.isMobile(phones[i])){
				continue;
			}
			phoneList.add(phones[i]);
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init();
		String tip = "";
		boolean res = false;
		try {
			if(msg.length() > MAX_LENGTH){
				StringBuilder result = new StringBuilder();
				int index = 0;
				while(true){
					if(index + MAX_LENGTH_PER_MSG >= msg.length()){
						Iterator iter = phoneList.listIterator();
						while(iter.hasNext()){
							String phone = (String)iter.next();
	
							res = SmsSender.sendSMS(phone, msg.substring(index, msg.length()), 54, userId);
							if(res){
								tip = "提醒短信发送成功";
							} else {
								tip = "提醒短信发送出现故障";
							}

							//result.append(sender.massSend(phone, msg.substring(index, msg.length()), "", ""));
							result.append(tip);
						}
						break;
					} else {
						Iterator iter = phoneList.listIterator();
						while(iter.hasNext()){
							String phone = (String)iter.next();
							
							res = SmsSender.sendSMS(phone, msg.substring(index, index + MAX_LENGTH_PER_MSG), 54, userId);
							if(res){
								tip = "提醒短信发送成功";
							} else {
								tip = "提醒短信发送出现故障";
							}

							//result.append(sender.massSend(phone, msg.substring(index, index + MAX_LENGTH_PER_MSG), "", ""));
							result.append(tip);
						}
					}
					index = index + MAX_LENGTH_PER_MSG;
				}
				return result.toString();
	
				//return "短信内容太多";
			} else {
				StringBuilder result = new StringBuilder();
	
				Iterator iter = phoneList.listIterator();
				while(iter.hasNext()){
					String phone = (String)iter.next();
					
					res = SmsSender.sendSMS(phone, msg, 54, userId);
					if(res){
						tip = "提醒短信发送成功";
					} else {
						tip = "提醒短信发送出现故障";
					}
					//result.append(sender.massSend(phone, msg, "", ""));
					result.append(tip);
				}
				return result.toString();
			}
		} catch (Exception e) {
		    tip = "提醒短信发送出现故障";
		} finally {
			dbOp.release();
		}
		return tip;
	}

	public static String send(int userId, String dst, String msg, String time, String subNo) {
		DbOperation dbOp = new DbOperation();
		dbOp.init();
		String tip = "";
		boolean res = false;
		try {
			res = dbOp.executeUpdate("insert into sms2.shortmessage set addtime=now(),mobile='"+dst+"',content='" + msg + "'");
			if(res){
				tip = "提醒短信发送成功";
			} else {
				tip = "提醒短信发送出现故障";
			}
		} catch (Exception e) {
		    tip = "提醒短信发送出现故障";
		} finally {
			dbOp.release();
		}
		//return sender.massSend(dst, msg, time, subNo);
		return tip;
	}
	
    public Sender()
    {
        this("default", "default");
    }

    public Sender(String name, String pwd)
    {
        comName = name;
        comPwd = pwd;
        Server = "http://218.241.67.234:9000";
    }

    public Sender(String name, String pwd, int serverNum)
    {
        comName = name;
        comPwd = pwd;
        if(serverNum == 2)
            Server = "http://218.241.67.234:9000";
        else
            Server = "http://218.241.67.234:9000";
    }

    public String massSend(String dst, String msg, String time, String subNo)
    {
        String sUrl = null;
        try
        {
        	sUrl = Server + "/QxtSms/QxtFirewall?OperID=" + comName + "&OperPass=" + comPwd + "&ContentType=8&SendTime=&ValidTime=&AppendID=&DesMobile=" + dst + "&Content=" + URLEncoder.encode(msg, "GBK");//这里必须GB2312否则发到手机乱码
        }
        catch(UnsupportedEncodingException uee)
        {
            System.out.println(uee.toString());
        }
        return getUrl(sUrl);
    }

    public String readSms()
    {
        String sUrl = null;
        sUrl = Server + "/send/readsms.asp?name=" + comName + "&pwd=" + comPwd;
	try{	
		URLEncoder.encode(sUrl,"GB2312");//linux下编码成GB18030或UTF-8
	}catch(UnsupportedEncodingException uee){
		System.out.println(uee.toString());
	}
        return getUrl(sUrl);
    }

    public String getFee()
    {
        String sUrl = null;
        sUrl = Server + "/send/getfee.asp?name=" + comName + "&pwd=" + comPwd;
        return getUrl(sUrl);
    }

    public String changePwd(String newPwd)
    {
        String sUrl = null;
        sUrl = Server + "/send/cpwd.asp?name=" + comName + "&pwd=" + comPwd + "&newpwd=" + newPwd;
	try{	
		URLEncoder.encode(sUrl,"GB2312");//linux下编码成GB18030或UTF-8
	}catch(UnsupportedEncodingException uee){
		System.out.println(uee.toString());
	}
        return getUrl(sUrl);
    }

    public String getUrl(String urlString)
    {
        StringBuffer sb = new StringBuffer();
        try
        {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "gb2312"));
            for(String line = null; (line = reader.readLine()) != null;)
                sb.append(line + "\n");

            reader.close();
        }
        catch(IOException e)
        {
            System.out.println(e.toString());
        }
        return sb.toString();
    }

    private String comName;
    private String comPwd;
    private String Server;
}
