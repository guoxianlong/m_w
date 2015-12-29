<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<%
InsertAction insertAction=new InsertAction();
insertAction.testTemplateMarker(request,response);
%>
<head>
<title>测试短信模板</title>
</head>
<body>
<%if(request.getAttribute("content")!=null){ %>
	<%=request.getAttribute("content").toString() %>
<%} %>
</body>
</html>