<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.stock.ProductStockBean" %>
<%@page import="adultadmin.util.*"%><html>
<head>
<title>产品查询</title>
<% 
String area = (String)request.getSession().getAttribute("area");
int areaId = StringUtil.toInt(area);
String result=(String)request.getAttribute("result");
if(result==null){
	result="";
}
%>
<script type="text/javascript">
function getFocus(){
	var code=document.getElementById("code");
	if(code.value=="扫描商品或者货位"){
		code.value="";
	}
}
function getBlur(){
	var code=document.getElementById("code");
	if(code.value==""){
		code.value="扫描商品或者货位";
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="document.getElementById('code').focus();">
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=searchProduct" method="post">
 <table width="220" height="220" border="0" cellspacing="0">
<tr align="center">
	<td><font size="4" style="font-weight:bold">货位商品查询</font>
	<font color="red" size="4"><%=ProductStockBean.getAreaName(areaId) %></font></td>
</tr>
<tr align="center">
	<td>
	<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</font></a>]</td>
</tr>
<tr align="center">
	<td><%=DateUtil.getNow() %></td>
</tr>
<tr >
	<td colspan="2" align="center"> <textarea cols="26" rows="7" readonly="readonly" style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
</tr>
<tr align="center">
	<td colspan="2"><input type="text" id="code" name="code" value="扫描商品或者货位" size="32" onfocus="getFocus();" onblur="getBlur();"/></td>
</tr>
<tr align="center">
		<td>
			<input type="submit" value="提 交" style="height:26px;width:100px;" />
			<input type="button" value="返  回" style="height:26px;width:100px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=pdaOperation'"/>
		</td>
</tr>
</table>
</form>
</body>
</html>