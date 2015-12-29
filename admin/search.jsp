<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>搜索</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form method=post action="searchproduct.do">
产品编号：<input type=text name="code" size="20"><br>
产品名称：<input type=text name="name" size="20">（模糊）<br>
<input type=submit value="查询产品">
</form>
</td></tr>
<tr bgcolor="#F8F8F8"><td>
<form method=post action="searchorder.do">
订单编号：<input type=text name="code" size="20"><br>
订单价格：<input type=text name="price" size="20"><br>
用户名字：<input type=text name="name" size="20"><br>
电话：<input type=text name="phone" size="20"><br>
产品名称：<input type=text name="product" size="20"><br>
<input type=submit value="查询订单">
</form>
</td></tr>
</table>
</td></tr></table>
<%@include file="../footer.jsp"%>
</body>
</html>