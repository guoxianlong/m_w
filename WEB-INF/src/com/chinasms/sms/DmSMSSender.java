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

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Company: 买卖宝</p>
 *
 * @author 姚兰
 * @version 1.0  Feb 6, 2012
 */

	public class DmSMSSender {
		public static int DM_POWER = 426;
		public static int MAX_PHONE_COUNT = 100;
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
			
			String tip = "";
			boolean res = false;

        	Iterator iter = phoneList.listIterator();
			while(iter.hasNext()){
				String phone = (String)iter.next();
		
				res = SmsSender.sendSMS(phone, msg, 51, userId);
//					res = dbOp.executeUpdate("insert into shortmessage51 set addtime=now(),mobile='"+phone+"',content='" + msg + "'");
				if(res){
					tip = "提醒短信发送成功";
				} else {
					tip = "提醒短信发送出现故障";
				}
			}
		
			return tip;
		}
		
		public static String send(int userId, String dst, String msg, String time, String subNo) {
			String tip = "";
//			boolean res = dbOp.executeUpdate("insert into shortmessage51 set addtime=now(),mobile='"+dst+"',content='" + msg + "'");
			boolean res = SmsSender.sendSMS(dst, msg, 51, userId);
			if(res){
				tip = dst+"提醒短信发送成功";
			} else {
				tip = "提醒短信发送出现故障";
			}

			return tip;
		}
		
	    public DmSMSSender()
	    {
	        this("default", "default");
	    }

	    public DmSMSSender(String name, String pwd)
	    {
	        comName = name;
	        comPwd = pwd;
	        Server = "http://218.241.67.234:9000";
	    }

	    public DmSMSSender(String name, String pwd, int serverNum)
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


