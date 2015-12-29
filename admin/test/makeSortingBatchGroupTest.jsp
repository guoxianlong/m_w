<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<%
InsertAction insertAction=new InsertAction();
insertAction.makeSortingBatchGroupTest(request,response);
%>
<head>
<title>Insert title here</title>
</head>
<body>
分拣批次1.4，生成分拣波次测试<br/>
<%if(request.getAttribute("msg")!=null){ %>
<%=request.getAttribute("msg").toString() %>
<%} %>
</body>
</html>