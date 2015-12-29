<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="cache.ProductPreferenceCache"%>
<%	
   try{ 
    ProductPreferenceCache.init();
	  }catch(Exception e){
    e.printStackTrace();
    System.out.println("clearAdminProductProductCache清买赠活动失败！！");
    }
%>