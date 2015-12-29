<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.util.DateUtil"%>
<%@page import="java.util.List"%>
<%@page import="mmb.stock.cargo.CartonningInfoBean"%><html>
<head>
<title>扫描条码页面</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script type="text/javascript">
function check(){
	var code=trim(document.getElementById("code").value);
	if(code==""){
		alert("请扫描条码！");
		return false;
	}
	return true;
}
<%if(request.getAttribute("tip")!=null){%>
	alert('<%=request.getAttribute("tip").toString()%>');
<%}%>
</script>
<%
List cartonningList=(List)request.getSession().getAttribute("cartonningList");
%>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden">
<form action="stockOperation.do?method=exchangeOper" method="post" onsubmit="return check();">
<table width="200" height="220" border="0" cellspacing="0">
<tr align="center">
	<td colspan="2"><h2>货位间调拨扫描</h2></td>
</tr>
<tr align="center">
	<td colspan="2"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %></td>
</tr>
<tr align="center">
	<td colspan="2"><%=DateUtil.getNow() %></td>
</tr>
<tr align="center"><td colspan="2">
<%if(cartonningList!=null){ %>
	<%for(int i=0;i<cartonningList.size();i++){ %>
		<%CartonningInfoBean cartonningBean=(CartonningInfoBean)cartonningList.get(i); %>
		<%=cartonningBean.getCode() %><br/>
	<%} %>
<%} %>
</td></tr>
<tr align="center"><td colspan="2"><input type="text" id="code" name="code"/></td></tr>
<tr align="center">
	<td><input type="submit" value="确定"/></td>
	<td><input type="reset" value="取消" onclick="javascript:window.location='stockOperation.do?method=selectOperType'"/></td>
</tr>
</table>
</form>
<script type="text/javascript">
document.getElementById("code").focus();
</script>
</body>
</html>