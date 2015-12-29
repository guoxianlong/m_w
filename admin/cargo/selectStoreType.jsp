<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.util.DateUtil"%><html>
<head>
<%if(request.getSession().getAttribute("type")!=null&&request.getSession().getAttribute("type").equals("3")){ %>
	<title>选择散件区或整件区调拨</title>
<%}else if(request.getSession().getAttribute("type")!=null&&request.getSession().getAttribute("type").equals("0")){ %>
	<title>选择散件区或整件区上架</title>
<%} %>
<script type="text/javascript">
function returns(){
	 window.location.href="stockOperation.do?method=stockOperation&toPage=cangneizuoye";
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden">
<table width="200" height="220" border="0" cellspacing="0">
<tr align="center">
	<%if(request.getSession().getAttribute("type")!=null&&request.getSession().getAttribute("type").equals("3")){ %>
		<td><h2>移动设备功能菜单</h2></td>
	<%}else if(request.getSession().getAttribute("type")!=null&&request.getSession().getAttribute("type").equals("0")){ %>
		<td><h2>上架作业</h2></td>
	<%} %>
</tr>
<tr align="center">
	<td><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %></td>
</tr>
<tr align="center">
	<td><%=DateUtil.getNow() %></td>
</tr>
<%if(request.getSession().getAttribute("type")!=null&&request.getSession().getAttribute("type").equals("3")){ %>
	<tr align="center"><td><input type="button" value="散件区调拨" onclick="window.location='stockOperation.do?method=exchangeOper&storeType=0'"/></td></tr>
	<tr align="center"><td><input type="button" value="整件区调拨" onclick="window.location='stockOperation.do?method=exchangeOper&storeType=1'"/></td></tr>
	<tr align="center"><td><input type="button" value="混合区调拨" onclick="window.location='stockOperation.do?method=exchangeOper&storeType=4'"/></td></tr>
	<tr align="center"><td><input type="button" name="button" value="返回" onclick="returns();" /></td></tr>
	 
<%}else if(request.getSession().getAttribute("type")!=null&&request.getSession().getAttribute("type").equals("0")){ %>
	<tr align="center"><td><input type="button" value="散件区上架" onclick="window.location='stockOperation.do?method=soUpOper&storeType=0'"/></td></tr>
	<tr align="center"><td><input type="button" value="整件区上架" onclick="window.location='stockOperation.do?method=soUpOper&storeType=1'"/></td></tr>
	<tr align="center"><td><input type="button" name="button" value="返回" onclick="returns();" /></td></tr>
<%} %>
</table>
</body>
</html>