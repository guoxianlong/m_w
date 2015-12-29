/*
 * Created on 2007-1-7
 *
 */
package adultadmin.util;

/**
 * @author zhouj
 *  
 */

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

	/**
	 * @param args
	 */
	
	private  HttpServletRequest request = null;
	private  HttpServletResponse response = null;
	
	
	public CookieUtil(HttpServletRequest request,HttpServletResponse response){
	        this.request = request;
	        this.response = response; 
	}
	
	public static CookieUtil getInstance(HttpServletRequest request,HttpServletResponse response){
		return new CookieUtil(request,response);
	}
	
	public Cookie getCookie(String oldname){
		  Cookie[] cook = request.getCookies();
		  Cookie cookie = null;
		  if(cook!=null){
				for(int i=0;i<cook.length;i++){
					if(oldname.equals(cook[i].getName())){
						cookie = cook[i];
						break;
					}
				}
			}
		  return cookie;
	}
	public String getCookieValue(String name){
		Cookie[] cookie = request.getCookies();
		String cname = null;
		if(cookie!=null){
			for(int i=0;i<cookie.length;i++){
				if(name.equals(cookie[i].getName())){
					cname = cookie[i].getValue();
					break;
				}
			}
		}
		return cname;
		
	}
	
	public void removeCookie(String name){
		Cookie cookie = new Cookie(name,"");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}
	
	/**public void setCookie(String name,String value){
		Cookie cookie = new Cookie(name,value);
		
		response.addCookie(cookie);
	}*/
    public void setCookie(String name,String value,int age){
        
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(age);
         
        response.addCookie(cookie);
    }
    public void setCookieSafe(String name,String value,int age){
        
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        cookie.setHttpOnly(true);
         
        response.addCookie(cookie);
    }
    // 为特定域名设定cookie
	public void setCookieSafe(String name, String domain, String value, int age) {

		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setDomain(domain);
		cookie.setMaxAge(age);
		cookie.setHttpOnly(true);

		response.addCookie(cookie);
	}
	public void setCookie(String name,String value,String domain,int age){
		
		Cookie cookie = new Cookie(name,value);
	    cookie.setDomain(domain);
	    cookie.setMaxAge(age);
	     
	    response.addCookie(cookie);
	}
	public void setCookie(String name,String value,String path,String domain,int age){
		
		Cookie cookie = new Cookie(name,value);
	    cookie.setPath(path);
	    cookie.setMaxAge(age);
//	    if(domain != null)
//	    	cookie.setDomain(domain);
	    response.addCookie(cookie);
	}
   
}
 