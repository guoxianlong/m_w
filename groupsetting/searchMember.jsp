<%@page import="adultadmin.action.autoassignorder.GroupSettingAction"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
	response.setHeader("Cache-Control", "no-cache");
	response.setHeader("Paragma", "no-cache");
	response.setDateHeader("Expires", 0);
	new GroupSettingAction().searchMember(request,response);
	String result = (String)request.getAttribute("result");
%>
<%=result %>