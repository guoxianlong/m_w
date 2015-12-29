<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoOperationCargoBean"%>
<%@page import="adultadmin.bean.cargo.CargoOperationBean"%>
<%@ page import="mmb.stock.stat.ProductWarePropertyService"%>
<html>
<head>
<title>批量完成退货上架单</title>
</head>
<body onload="document.getElementById('code').focus();">
<h3>批量完成退货上架单</h3>
<form action="<%=request.getContextPath() %>/admin/cargoOperation.do?method=allCompleteCargoUpShelf" method="post">
	<input type="text" name="code" id="code"/>
	<input type="submit" value="完成退货上架单"/>
</form>
<%if(request.getAttribute("msg")!=null){ %>
	<font color="red"><%=request.getAttribute("msg") %></font>
<%} %>
<%if(request.getAttribute("errMsg")!=null){ %>
	<font color="red"><%=request.getAttribute("errMsg") %></font>
<%} %>
</body>
</html>