<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<object
       id="alarmActiveXID" 
       classid="clsid:0104FD36-2FA0-4EAF-8C24-0D16AE2F17E1" >
 </object> 
<head>
<title>添加包裹</title>
<% 
	ProductStockBean psBean = new ProductStockBean();
	MailingBatchBean mbBean = (MailingBatchBean)request.getSession().getAttribute("mbBean");
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
	String parcelCode = (String)request.getAttribute("parcelCode");
	if(parcelCode == null){
		parcelCode = request.getParameter("parcelCode");
	}
	String color=null;
	if(request.getAttribute("color")!=null){
		color=request.getAttribute("color").toString();
	}
 %>
<script type="text/javascript">
function checkButton(){
	var packageCode = document.getElementById("packageCode").value;
	if(packageCode != "" && packageCode != "扫描或手工输入包裹单号"){
		document.forms[0].submit();
	}else{
		alert("包裹单号不能为空！");
		document.getElementById("packageCode").focus();
	}
}
function checkEnter(){
	if(window.event.keyCode == 13){
		var packageCode = document.getElementById("packageCode").value;
		if(packageCode != "" && packageCode != "扫描或手工输入包裹单号"){
			//document.forms[0].submit();
		}else{
			alert("包裹单号不能为空！");	
			document.getElementById("packageCode").focus();
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
	document.getElementById("packageCode").value = "";
	document.getElementById("packageCode").focus();
}
function prompt(){
	var packageCode = document.getElementById("packageCode").value;
	if(packageCode == ""){
		document.getElementById("packageCode").value ="扫描或手工输入包裹单号";
	}
}
function clearText(){
	document.getElementById("packageCode").value = "";
}
</script>
</head>
<body <%if(color==null){ %> background="<%=request.getContextPath() %>/image/soBg.jpg"<%}else{ %> bgcolor="<%=color%>"<%} %> style="overflow:hidden;" onload="getFocus();" >
<table width="220" height="220" border="0" cellspacing="0">
<tr height="4">
	<td colspan="2" align="center"><font size="4"style="font-weight:bold">添加包裹</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr height="4">
	<td colspan="2" align="center"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">[<font color="red" size="2">注销</font>]</a></td>
</tr>
<tr height="4">
	<td colspan="2" align="center"><font size="2"><%=DateUtil.getNow() %></font></td>
</tr>
<tr height="4">
	<td colspan="2" align="center"><font size="2">当前邮包：<%=parcelCode%></font></td>
</tr>
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=addParcel&parcelCode=<%=parcelCode %>" method="post">
<% if(result != null){ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="6" name="" readonly="true"style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
	</tr>
<% }else{ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="6" name="" readonly="true" style="overflow-x:hidden;overflow-y:hidden">扫描或手工输入包裹单号.</textarea></td>
	</tr>
<% } %>
	<tr height="8">
		<td colspan="2" align="center">
		<input type="text" size="32" id="packageCode" name="packageCode"onblur="prompt()" onfocus="clearText()" onkeypress="javascript:return checkEnter()"></td>	
	</tr>


	<tr height="8" align="center">
		<td colspan="2"><input type="button" value="添  加"  style="height:26px;width:130px;"onclick="checkButton()"/>
		<input type="button" value="返   回" style="height:26px;width:80px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=batchParcelList&batchCode=<%=mbBean.getCode()%>';"/></td>
	</tr>
</form>
</table>
<%if(color!=null){ %>
<script type="text/javascript">
try{
    alarmActiveXID.playBeep();
   }catch(ex){
       alert("调用异常:" + ex.description);
   }
</script>
<%} %>
</body>
</html>