<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>

<%@page import="java.util.List"%><html>
<%
if(request.getParameter("data")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.addDeliverArea(request,response);
}
%>
<head>
<title>Insert title here</title>
</head>
<body>
<form action="" method="post">
<textarea rows="20" cols="40" name="data"></textarea>
<input type="submit" value="提交"/>
</form>
<%if(request.getAttribute("msg")!=null){ %>
	<font color="red"><%=request.getAttribute("msg") %></font>
<%} %>
<%if(request.getAttribute("errMsg")!=null){ %>
	<font color="red"><%=request.getAttribute("errMsg") %></font>
<%} %>
</body>
</html>