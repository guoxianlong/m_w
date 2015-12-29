<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="mmb.promotion.PromotionProductCache"%>
<%  try{ 
   PromotionProductCache.init();
	}catch(Exception e){
    e.printStackTrace();
    System.out.println("clearPackageSalesCache清打包和阶梯价活动失败！！");
    } %>
