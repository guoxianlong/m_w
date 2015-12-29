<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<html>
<head>
<title>批量添加商品物流属性</title>
<%
if(request.getParameter("productWareProperty")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.addProductWareProperty(request,response);
}
%>

</head>
<body>
批量添加商品物流属性：
<form method="post">
<textarea rows="20" cols="40" name="productWareProperty"></textarea>
<input type="submit" value="提交"/>
</form>
<%if(request.getAttribute("errMsg")!=null){%>
	<br/>错误数据：<br/>
	<%=request.getAttribute("errMsg") %>
<%} %>
</body>
</html>