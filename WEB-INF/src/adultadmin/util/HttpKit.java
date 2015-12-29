package adultadmin.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;

public class HttpKit {

    private static final String DEFAULT_CHARSET = "UTF-8"; // 默认字符集
    
    private static final String _GET  = "GET"; // GET
    private static final String _POST = "POST";// POST
    
    /**
     * 初始化http请求参数
     * @param url
     * @param method
     * @param headers
     * @return
     * @throws IOException
     */
    private static HttpURLConnection initHttp (String url, String method, Map<String, String> headers) throws IOException {
        URL _url = new URL(url);
        HttpURLConnection http = (HttpURLConnection) _url.openConnection();
        // 连接超时
        http.setConnectTimeout(25000);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(25000);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        http.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }
    
    /**
     * 
     * @description
     * 功能描述: POST 请求
     * @return       返回类型:
     */
    public static String post(String url, String params, Map<String, String> headers) {
        StringBuffer bufferRes = null;
        try {
            HttpURLConnection http = initHttp(url, _POST, headers);
//            int responseCode = http.getResponseCode();  
//            if (responseCode != 200) {  
//                log.error(" Error===" + responseCode);  
//            } else {  
//                log.info("Post Success!");  
//            }  
//            System.out.println("responsecode+ " + responseCode);
            OutputStream out = http.getOutputStream();
            out.write(params.getBytes(DEFAULT_CHARSET));
            out.flush();
            out.close();

            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null){
                bufferRes.append(valueString);
            }
            read.close();
            in.close();
            if (http != null) {
                http.disconnect();// 关闭连接
            }
            return bufferRes.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * post map 请求
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String post(String url, Map<String, Object> params) throws UnsupportedEncodingException {
        return post(url, map2Url(params), null);
    }
    
    /**
     * post map 请求,headers请求头
     * @param url
     * @param params
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String post(String url, Map<String, Object> params, Map<String, String> headers) throws UnsupportedEncodingException {
        return post(url, map2Url(params), headers);
    }
    
    /**
     * map构造url
     * @description
     * 功能描述:
     * @return       返回类型:
     * @throws UnsupportedEncodingException
     */
    public static String map2Url(Map<String, Object> paramToMap) throws UnsupportedEncodingException {
        if (null == paramToMap || paramToMap.isEmpty()) {
            return null;
        }
        StringBuffer url = new StringBuffer();
        boolean isfist = true;
        for (Entry<String, Object> entry : paramToMap.entrySet()) {
            if (isfist) {
                isfist = false;
            } else {
                url.append("&");
            }
            url.append(entry.getKey()).append("=");
            String value = entry.getValue() + "";
            if (!StringUtil.isEmpty(value)) {
                url.append(URLEncoder.encode(value, DEFAULT_CHARSET));
            }
        }
        return url.toString();
    }
    
}
