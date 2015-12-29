<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*,mmb.stock.stat.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>移动设备功能菜单</title>
<script type="text/javascript">
function selectPage(content){
	var area=document.getElementById("area").value;
	if(area==-1){
		alert("请选择当前仓库！");
	}else {
		document.getElementById("toPage").value=content;
		document.forms[0].submit();
	}
}
function getFocus(){
	document.getElementById("firstButton").focus();
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()">
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation" method="post">
 <table width="220" height="220" border="0" cellspacing="0">
<tr>
	<td colspan="2" align="center"><font size="5" style="font-weight:bold">移动设备功能菜单</font></td>
</tr>
<tr>
	<td colspan="2" align="center"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
</tr>
<tr>
	<td colspan="2" align="center"><%=DateUtil.getNow() %></td>
</tr>
<tr>
	<td colspan="2" align="center" >
	<% String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("area","area", request, -1,true,"-1");
		%>
		<%= wareAreaLable%>
	</td>
</tr>
<tr align="center">
	<td><input type="button" id="firstButton" value="仓内作业" style="height=35px;width=100px;font-weight:bolder"onclick="selectPage('cangneizuoye');"/></td>
	<td><input type="button" value="装箱管理" style="height=35px;width=100px;font-weight:bolder"onclick="selectPage('zhuangxiangguanli');"/></td>
</tr>
<tr align="center">
	<td><input type="button" value="作业交接" style="height=35px;width=100px;font-weight:bolder"onclick="selectPage('zuoyejiaojie');"/></td>
	<td><input type="button" value="货位异常" style="height=35px;width=100px;font-weight:bolder"onclick="selectPage('huoweiyichang');"/></td>
</tr>
<tr align="center">
	<td align="center"><input type="button" value="订单出库" style="height=35px;width=100px;font-weight:bolder"onclick="selectPage('dingdanchuku');"/></td>
	<td align="center"><input type="button" value="商品查询" style="height=35px;width=100px;font-weight:bolder"onclick="selectPage('shangpinchaxun');"/></td>
</tr>
</table>
<input type="hidden" id="toPage" name="toPage" value=""/>
</form>
</body>
</html>