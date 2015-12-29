package mmb.util;

import java.net.URL;
import java.util.Map;

public class HttpResponse {
	 //返回的状态码 如200
    int responseCode;
    //返回的消息 如OK
    String responseMessage;
    //头信息
    Map headers;
    //内容
    byte[] content;
    
    URL url;
    
    
    /**
     * @return Returns the content.
     */
    public byte[] getContent() {
        return content;
    }
    /**
     * @param content The content to set.
     */
    public void setContent(byte[] content) {
        this.content = content;
    }
    /**
     * @return Returns the headers.
     */
    public Map getHeaders() {
        return headers;
    }
    /**
     * @param headers The headers to set.
     */
    public void setHeaders(Map headers) {
        this.headers = headers;
    }
    /**
     * @return Returns the responseCode.
     */
    public int getResponseCode() {
        return responseCode;
    }
    /**
     * @param responseCode The responseCode to set.
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    /**
     * @return Returns the responseMessage.
     */
    public String getResponseMessage() {
        return responseMessage;
    }
    /**
     * @param responseMessage The responseMessage to set.
     */
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }
	/**
	 * @return Returns the url.
	 */
	public URL getURL() {
		return url;
	}
	/**
	 * @param url The url to set.
	 */
	public void setURL(URL url) {
		this.url = url;
	}
}
