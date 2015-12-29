package mmb.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class Http {
	protected static Pattern removePattern = Pattern.compile("[\r\n]+[ \t]*");
	public static HttpResponse doRequest(String requestURL,
			String requestMethod, String content, String charset,
			HttpServletRequest request) {
		return doRequest(requestURL, requestMethod, content, charset,
				true);
	}

	static String viaKey = "via";
	static String viaValue = "WTP/1.1 BJBJ-PS-WAP4-GW06.bj4.monternet.com (Nokia WAP Gateway 4.0/CD3/4.1.79)";
	static String forwardedKey = "x-forwarded-for";
	static String forwardedValue = "10.18.86.242";
	static String agentKey = "User-Agent";
	static String agentValue = "NokiaN73-1/3.0638.0.0.44_rm132 Series60/3.0 Profile/MIDP-2.0 ";
	static String proxyKey = "jcproxy";

	public static HashMap cookies = new HashMap();
	
	public static HttpResponse doRequest(String requestURL,
			String requestMethod, String content, String charset, boolean redirect) {
		HttpResponse response = null;

		// System.out.println(" ----request---- url : " + requestURL + "?" +
		// content);

		try {
			if (requestMethod.equalsIgnoreCase("GET") && content != null) {
				requestURL += "?" + content;
			}

			URL url = new URL(requestURL);
			String host = url.getHost();
			String cookie = null;
			cookie = (String)cookies.get(host);		// 获取这个域名下的cookie

			response = new HttpResponse();

			try {

				URLConnection urlc = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) urlc;
				
				if(cookie != null)
					httpConnection.addRequestProperty("Cookie", cookie);
				
				httpConnection.setRequestMethod(requestMethod.toUpperCase());
				httpConnection.setInstanceFollowRedirects(redirect);
				httpConnection.setDoInput(true);
				httpConnection.setRequestProperty(viaKey, viaValue);
				httpConnection.setRequestProperty(forwardedKey, forwardedValue);
				// httpConnection.setRequestProperty("x-source-id",
				// "BJGGSN54BNK");
				// httpConnection.setRequestProperty("x-nokia-connection_mode",
				// "TCP");
				// httpConnection.setRequestProperty("x-up-bearer-type",
				// "GPRS");
				// httpConnection.setRequestProperty("x-nokia-gateway-id",
				// "NWG/4.1/Build04");
				httpConnection.setRequestProperty(agentKey, agentValue);

				if (requestMethod.equalsIgnoreCase("POST")) {
					if (content == null) {
						return null;
					}
					if (charset == null) {
						charset = "UTF-8";
					}
					httpConnection.setDoOutput(true);
				}
				// System.out.println("----------start mtlog 5-----------");
				// httpConnection.connect();
				OutputStream out = null;
				if (requestMethod.equalsIgnoreCase("POST")) {
					out = httpConnection.getOutputStream();
					out.write(content.getBytes(charset));
					out.flush();
				}

				InputStream in = httpConnection.getInputStream();
				
				String setc = httpConnection.getHeaderField("Set-Cookie");
	            if (setc != null) {
	                synchronized(cookies) {
	                	cookies.put(host, setc);
	                }
	            }
				

				response.setResponseCode(httpConnection.getResponseCode());
				response
						.setResponseMessage(httpConnection.getResponseMessage());
				response.setHeaders(httpConnection.getHeaderFields());
				response.setURL(httpConnection.getURL());

				// System.out.println("----------start mtlog 6-----------");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int i = in.read();
				while (i != -1) {
					baos.write(i);
					i = in.read();
				}
				in.close();
				baos.close();
				response.setContent(baos.toByteArray());
				if (out != null)
					out.close();
				httpConnection.disconnect();
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			} catch (IOException ex1) { // 连接失败
				ex1.printStackTrace();
				return null;
			}
		} catch (MalformedURLException ex) {
			ex.printStackTrace();
			return null;
		}

		return response;
	}
	public static void main(String [] arg) throws UnsupportedEncodingException
	{
	//	String url = "http://bc.joycool.net/";
		String url = "http://bc.joycool.net/adult/Column.do";
		HttpResponse res = Http.doRequest(url,"get", "columnId=12579", "UTF-8", true);
		String content = new String(res.getContent(), "utf-8");
		System.out.println(content);
		res = Http.doRequest(url,"get", "columnId=12579", "UTF-8", true);
		content = new String(res.getContent(), "utf-8");
		System.out.println(content);
	}

}
