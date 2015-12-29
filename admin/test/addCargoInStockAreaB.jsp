<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<head>
<title>Insert title here</title>
<%
if(request.getParameter("stockAreaCode")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.addCargoInStockAreaB(request,response);
}
%>
</head>
<body>
批量添加B区货位和G区货位
<form method="post">
<input type="text" name="stockAreaCode"/>
<input type="submit" value="提交"/>
</form>
<%if(request.getAttribute("success")!=null){%>
	<%=request.getAttribute("success") %>
<%} %>
</body>
</html>