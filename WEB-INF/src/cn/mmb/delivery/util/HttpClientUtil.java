package cn.mmb.delivery.util;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import adultadmin.util.Constants;

import com.mmb.framework.utils.MD5;

public class HttpClientUtil {
	
	private static Log log = LogFactory.getLog("debug.Log");
	
	public static String getHttpClientInformation(String url,int orderId) throws Exception{
		// 创建默认的httpClient实例.    
		HttpClient httpclient = new DefaultHttpClient(); 
        // 创建httppost    
        HttpPost httppost = new HttpPost(url);  
        // 创建参数队列    
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();  
        JSONObject jsonParam = new JSONObject();
        String security = Constants.config.getProperty("reality_price_key", "");
        jsonParam.put("securityKey", MD5.md5s(orderId+security));
        jsonParam.put("orderId",orderId);
        formparams.add(new BasicNameValuePair("param",jsonParam.toString())); 
        UrlEncodedFormEntity uefEntity;  
        uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");  
        httppost.setEntity(uefEntity);  
        log.info("request：" + httppost.getURI()+"参数："+jsonParam.toString());  
        HttpResponse response = httpclient.execute(httppost);  
        try {  
            HttpEntity entity = response.getEntity();
            String data = EntityUtils.toString(entity, "UTF-8");
            System.out.println("response data:"+data);
            return data;
        }catch(Exception e){
        	e.printStackTrace();
        	log.info(e.toString());  
        	return "";
        }finally { 
        	// 关闭连接,释放资源    
            httppost.releaseConnection();  
        }  
	}
	
	public static void main(String[] args) {
		try {
			String s = HttpClientUtil.getHttpClientInformation("http://192.168.3.69/sale/initSplitHistory/init.mmx", 11979668);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
