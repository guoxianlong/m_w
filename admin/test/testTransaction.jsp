<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<%
	InsertAction insertAction=new InsertAction();
	insertAction.testTransaction(request,response);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>事务测试1</title>
</head>
<body>

</body>
</html>