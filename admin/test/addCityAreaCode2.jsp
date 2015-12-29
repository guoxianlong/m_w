<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<%
if(request.getParameter("areaCode")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.addCityAreaCode2(request,response);
}
%>

<head>
<title>Insert title here</title>
</head>
<body>
<form method="post">
<textarea rows="20" cols="20" name="areaCode"></textarea>
<input type="submit" value="提交"/>
</form>
<%if(request.getAttribute("count")!=null){%>
	<br/>完成修改：<%=request.getAttribute("count") %>条<br/>
<%}%>
<%if(request.getAttribute("errorMsg")!=null){%>
	<br/>错误数据：<br/>
	<%=request.getAttribute("errorMsg") %>
<%} %>
<%if(request.getAttribute("errorMsg2")!=null){%>
	<br/>地级市：<br/>
	<%=request.getAttribute("errorMsg2") %>
<%} %>
</body>
</html>