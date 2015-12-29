<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<head>
<title>Insert title here</title>
<%
if(request.getParameter("storeType")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.changeWXCargo(request,response);
}
%>
</head>
<body>
无锡货位存放类型修改
<form method="post">
<input type="text" name="storeType"/>
<input type="submit" value="提交"/>
</form>
<%if(request.getAttribute("success")!=null){%>
	<%=request.getAttribute("success") %>
<%} %>
</body>
</html>