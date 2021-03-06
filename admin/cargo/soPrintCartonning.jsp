<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>打印装箱单</title>
<% 
	String printResult = request.getParameter("result");
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
	ProductStockBean psBean = new ProductStockBean();
 %>
<script type="text/javascript">
function checkButton(){
	var cartonningCode = document.getElementById("cartonningCode").value;
	if(cartonningCode != "" && cartonningCode != "扫描装箱单编号"){
		document.forms[0].submit();
	}else{
		alert("装箱单编号不能为空！");
		document.getElementById("cartonningCode").focus();
	}
}
function checkEnter(){
	if(window.event.keyCode == 13){
		var cartonningCode = document.getElementById("cartonningCode").value;
		if(cartonningCode != "" && cartonningCode != "扫描装箱单编号"){
			document.forms[0].submit();
		}else{
			alert("装箱单编号不能为空！");	
			document.getElementById("cartonningCode").focus();
			return false;
		}
	}
}
function getFocus(){
	<%
		if(area==null){
	%>
			alert("登陆超时,请重新登录!");
			window.location='stockOperation.do?method=logout';
	<%
		}
	%>
	document.getElementById("cartonningCode").value = "";
	document.getElementById("cartonningCode").focus();
}
function prompt(){
	var productCode = document.getElementById("cartonningCode").value;
	if(productCode == ""){
		document.getElementById("cartonningCode").value ="扫描装箱单编号";
	}
}
function clearText(){
	document.getElementById("cartonningCode").value = "";
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()" topmargin="9">
<table width="220" height="220" border="0" cellspacing="0">
<tr height="4">
	<td colspan="2" align="center"><font size="4"style="font-weight:bold">打印装箱单</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr height="4">
	<td colspan="2" align="center"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr height="4">
	<td colspan="2" align="center"><%=DateUtil.getNow() %></td>
</tr>
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=printCartonning" method="post">
<% if(result != null){ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="24" rows="7"  readonly="true"style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
	</tr>
<% }else{ 
		if(printResult != null){%>
		<tr height="28">
			<td colspan="2" align="center"> <textarea cols="26" rows="7"  readonly="true"style="overflow-x:hidden;overflow-y:hidden">装箱单:<%=printResult %>已成功打印！</textarea></td>
		</tr>
	<% }else{ %>
		<tr height="28">
			<td colspan="2" align="center"> <textarea cols="26" rows="7"  readonly="true" style="overflow-x:hidden;overflow-y:hidden">请扫描要打印的装箱单号.</textarea></td>
		</tr>
<%	   }
   } %>
	<tr height="8">
		<td colspan="2" align="center"><input type="text" size="32" id="cartonningCode" name="cartonningCode"onblur="prompt()" onfocus="clearText()" onkeypress="javascript:return checkEnter()"></td>	
	</tr>


	<tr height="8" align="center">
		<td><input type="button" value="打印装箱单"  style="height=26px;width=130px;"onclick="checkButton()"/>
		<input type="button" value="返   回" style="height:26px;width:80px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zhuangxiangguanli'" /></td>
	</tr>
</form>
</table>
</body>
</html>