<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.util.*" %>
<html>
<head>
<title>批量打印货位调拨单</title>
<%
	String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
%>


<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">

</head>
<body>
<form action="../cargoOperation.do?method=exchangeCargoListPrint" method="post" onsubmit="return checksubmit();" target="_blank">

	
	<table width="500">
  <tr>
    <td><div align="center"><h2>批量打印货位调拨单</h2></div></td>
  </tr>
  <tr>
  	<td align="center"><font color="red">每次最多打印50个调拨单</font></td>
  </tr>
  <tr>
    <td><div align="center"><textarea name="list" id="list" cols="30" rows="20"></textarea></div></td>
  </tr>
  <tr>
    <td><div align="center">
      <input name="提交" type="submit" value="打印调拨单" >
      <input name="重置" type="button" value="取消" onclick="window.close()">
    </div></td>
  </tr>
</table>
</form>
</body>
</html>