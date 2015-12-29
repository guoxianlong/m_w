<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="mmb.promotion.*"%>
<%@page import="cache.ProductPreferenceCache"%>
<%	
    try{ 
    ProductPreferenceCache.init();
	PromotionProductCache.init();
	AllPromotionProductCache.init();
    }catch(Exception e){
    e.printStackTrace();
    System.out.println("clearAllPromotionCache清全部促销提示失败！！");
    }
   	%>