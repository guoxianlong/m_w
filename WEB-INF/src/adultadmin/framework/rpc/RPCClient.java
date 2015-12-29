/*
 * Created on 2009-4-16
 *
 */
package adultadmin.framework.rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import adultadmin.util.Base64;
import adultadmin.util.MD5;

public class RPCClient {

	public static int CONNECT_TIMEOUT = 5; //5s
	public static int READ_TIMEOUT = 10; //10s
	
	public boolean doRequest(String urlAddress) throws UnsupportedEncodingException {
		boolean result = false;
		StringBuilder sb = new StringBuilder();
		String username = "sjwxAdmin";
		String password = "sjwxAdminPassword4AdultClearCache";
		username = new String(Base64.encode(username.getBytes()));
		password = new String(MD5.encrypt(password.getBytes("UTF-8")));
		try {
			urlAddress += "?u=" + URLEncoder.encode(username, "UTF-8") + "&s=" + URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			URL url = new URL(urlAddress);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT*1000);
			conn.setReadTimeout(READ_TIMEOUT*1000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			for (String line = null; (line = reader.readLine()) != null;)
				sb.append(line + "\n");
			reader.close();
//			System.out.println(sb.toString());
			result = sb.length() < 20;	// 正常情况返回的页面应该是空的
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public boolean doRequest(String urlAddress,boolean isWithParam) throws UnsupportedEncodingException {
		if(!isWithParam){
			return doRequest(urlAddress);
		}

		boolean result = false;
		StringBuilder sb = new StringBuilder();
		String username = "sjwxAdmin";
		String password = "sjwxAdminPassword4AdultClearCache";
		username = new String(Base64.encode(username.getBytes()));
		password = new String(MD5.encrypt(password.getBytes("UTF-8")));
		try {
			urlAddress += "&u=" + URLEncoder.encode(username, "UTF-8") + "&s=" + URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			URL url = new URL(urlAddress);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT*1000);
			conn.setReadTimeout(READ_TIMEOUT*1000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			for (String line = null; (line = reader.readLine()) != null;)
				sb.append(line + "\n");
			reader.close();
			result = sb.length() < 20;
//			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean doRequest(String urlAddress, String data) {
		boolean result = false;
		StringBuilder sb = new StringBuilder();
		String username = "sjwxAdmin";
		String password = "sjwxAdminPassword4AdultClearCache";
		username = new String(Base64.encode(username.getBytes()));
		password = new String(MD5.encrypt(password.getBytes()));
		try {
			if(data == null)
				urlAddress += "?u=" + username + "&s=" + URLEncoder.encode(password, "UTF-8");
			else {
				urlAddress += "?" + data;
				urlAddress += "&u=" + username + "&s=" + URLEncoder.encode(password, "UTF-8");
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			URL url = new URL(urlAddress);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT*1000);
			conn.setReadTimeout(READ_TIMEOUT*1000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			for (String line = null; (line = reader.readLine()) != null;)
				sb.append(line + "\n");
			reader.close();
			result = sb.length() < 20;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String[] shopServers;	// 所有的前台服务器列表，用于清理缓存等操作
	// 清理 全部服务器
	public String doRequestAll(String urlAddress, String data) {
		StringBuilder sb = new StringBuilder(128);
		for(int i = 0;i < shopServers.length;i++) {
			boolean res = doRequest(shopServers[i] + urlAddress, data);
			if(!res) {
				sb.append("\\n");
				sb.append("失败于:");
				sb.append(shopServers[i]);
			}
		}
		return sb.toString();
	}
	public String doRequestAll(String urlAddress) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(128);
		for(int i = 0;i < shopServers.length;i++) {
			boolean res = doRequest(shopServers[i] + urlAddress);
			if(!res) {
				sb.append("\\n");
				sb.append("前台失败于:");
				sb.append(shopServers[i] + urlAddress );
			}
		}
		return sb.toString();
	}
	public String doRequestAll(String urlAddress, boolean isWithParam) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder(128);
		for(int i = 0;i < shopServers.length;i++) {
			boolean res = doRequest(shopServers[i] + urlAddress, isWithParam);
			if(!res) {
				sb.append("\\n");
				sb.append("失败于:");
				sb.append(shopServers[i]);
			}
		}
		return sb.toString();
	}
	public static String[] adminShopServers;	// 所有的前台服务器列表，用于清理缓存等操作
	
	public String doAdminRequestAll(String urlAddress) throws UnsupportedEncodingException {
		if(adminShopServers==null || adminShopServers.length<1) return "没有配置好的服务器需要处理。";
		StringBuilder sb = new StringBuilder(128);
		for(int i = 0;i < adminShopServers.length;i++) {
			boolean res = doRequest(adminShopServers[i] + urlAddress);
			if(!res) {
				sb.append("\\n");
				sb.append("后台失败于:");
				sb.append(adminShopServers[i] + urlAddress );
			}
		}
		return sb.toString();
	}
	
	
	public static void main(String[] args) throws UnsupportedEncodingException{
		String password = "sjwxAdminPassword4AdultClearCache";
		password = new String(MD5.encrypt(password.getBytes("UTF-8")));
		System.out.println(URLEncoder.encode(password, "UTF-8"));
	}
}
