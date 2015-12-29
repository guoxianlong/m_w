<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<head>
<title>批量添加货位间调拨单</title>
<%
if(request.getParameter("data")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.addCargoOperation(request,response);
}
%>

</head>
<body>
批量添加货位间调拨单：
<form method="post">
<textarea rows="20" cols="40" name="data"></textarea>
<input type="submit" value="提交"/>
</form>
<%if(request.getAttribute("errMsg")!=null){%>
	<br/>错误数据：<br/>
	<%=request.getAttribute("errMsg") %>
<%} %>
</body>
</html>