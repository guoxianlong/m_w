<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>退货上架单作业确认完成</title>
<meta HTTP-EQUIV="pragma" CONTENT="no-cache">
<meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
<meta HTTP-EQUIV="expires" CONTENT="0">
<%
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragrma","no-cache");
	response.setDateHeader("Expires",0);
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean flag = true;
	if(!group.isFlag(618)){
		flag = false;
	}
%>
<script type="text/javascript">
<%if(!flag){%>
	alert("您没有操作该功能的权限！");
	window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=logout';
<% }%>
function checkText(){
	var operationCode = document.getElementById("operationCode").value;
	if(operationCode != "条码扫描区" && operationCode!=""){
		return true;
	}else{
		alert("条码不能为空!");
		document.getElementById("operationCode").focus();
		return false;
	}
}
function gosearch(){
    if(window.event.keyCode == 13){
    	var operationCode = document.getElementById("operationCode").value;
		if(operationCode != ""){
			alert("条码不能为空!");
			document.getElementById("operationCode").focus();
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
	document.getElementById("operationCode").value = "";
	document.getElementById("operationCode").focus();
}
function clearText(){
	document.getElementById("operationCode").value="";
}
function prompt(){
	var operationCode = document.getElementById("operationCode").value;
	if(operationCode == ""){
		document.getElementById("operationCode").value ="条码扫描区";
	}
}

function cancel(){
	window.location.href="<%=request.getContextPath()%>/admin/stockOperation.do?method=clearRetShelfSession";
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg" onload="getFocus()" style="overflow:hidden">
<table width="220" height="220" border="0" cellspacing="0">
<tr >
	<td colspan="3" align="center"><font size="4" style="font-weight:bold">退货上架单完成</font><% if("1".equals(area)){ %><font color="red" size="4">芳村</font><% }else if("3".equals(area)){
							 %><font color="red" size="4">增城</font><% }else{ 
							 %><font color="red" size="4">未选</font><% } %></td>
</tr>
<tr ><td colspan="3" align="center">
	<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr >
	<td colspan="3" align="center"><%=DateUtil.getNow() %></td>
</tr>																		
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=pdaConfirmOpShelf" method="post" width="300" height="220" onsubmit="return checkText();">
<% if(result != null){ %>
	<tr >
		<td colspan="3" align="center">
		<textarea cols="26" rows="7" readonly="true" style="overflow-x:hidden;overflow-y:hidden;"><%=result %></textarea></td>
	</tr>
<% }else{ %>
	<tr >
		<td colspan="3" align="center">
		<textarea cols="26" style="height:50px;" rows="7" readonly="true" style="overflow-x:hidden;overflow-y:hidden">条码扫描区</textarea></td>
	</tr>
<% } %>
	<tr>
		<td colspan="3" align="center">					
			<input type="text" id="operationCode" name="operationCode" onblur="prompt()"onfocus="clearText()" size="32"/> 
		</td>
	</tr>
	<tr align="center">
		<td><input type="submit" value="完成" style="height:26px;width:65px;" /></td>
		<td><input type="button" value="取消" style="height:26px;width:65px;" onclick="cancel();"/></td>
		<td><input type="button" value="返回" style="height:26px;width:65px;" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zuoyejiaojie'"/></td>
	</tr>
</form>
</table>
<p style="font-size:12px">
说明：请依次扫描退货上架单条码，货位条码
</p>
</body>
</html>