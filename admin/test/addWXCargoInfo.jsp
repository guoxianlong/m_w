<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<%
if(request.getParameter("stockAreaCode")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.addWXCargoInfo(request,response);
}
%>

<head>
<title>批量添加无锡货位</title>
</head>
<body>
<form method="post">
<input type="text" name="stockAreaCode">
<input type="submit" value="提交"/>
</form>
</body>
</html>