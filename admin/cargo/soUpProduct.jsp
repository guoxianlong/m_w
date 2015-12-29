<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean" %>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<html>
<head>
<title>上架作业</title>
<% 
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	int areaId = StringUtil.toInt(area);
	ProductStockBean psBean = new ProductStockBean();
 %>
<script type="text/javascript">
var xmlHttp;  
function createXMLHttpRequest() {    
    if (window.ActiveXObject) {    
        xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");    
    }    
    else if (window.XMLHttpRequest) {    
        xmlHttp = new XMLHttpRequest();    
    }   
}    
function okFunc (){   
    if(xmlHttp.readyState == 4) { 
        if(xmlHttp.status == 200) { 
        	document.getElementById("performance").innerHTML=xmlHttp.responseText;
        }    
    }    
}    
function startAjax(){   
    createXMLHttpRequest();    
    if( !xmlHttp){    
        return alert('create failed');    
    }    
    xmlHttp.open("GET", "<%=request.getContextPath()%>/admin/stockOperation.do?method=ajaxCargoStaffPerformance&selectIndex=9", true); 
    xmlHttp.onreadystatechange = okFunc;    
    xmlHttp.setRequestHeader("Content-Type","application/x-www-form-urlencoded");    
    xmlHttp.send("");    
}    
function check(){
	var cartonningCode = document.getElementById("cartonningCode").value;
	if(cartonningCode == "" || cartonningCode == "扫描装箱单编号"){
		alert("装箱单编号不能为空！");
		document.getElementById("cartonningCode").focus();
	}else{
		document.forms[0].submit();
		setTimeout("window.location='<%=request.getContextPath()%>/admin/cargo/soUpProduct.jsp';",5000);
	}
}
function checkEnter(){
	if(window.event.keyCode == 13){
		var cartonningCode = document.getElementById("cartonningCode").value;
		if(cartonningCode == "" || cartonningCode == "扫描装箱单编号"){
			alert("装箱单编号不能为空！");
		}else{
			//document.forms[0].submit();
		}
	}
}
function getFocus(){
	startAjax();
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
function clearText(){
	document.getElementById("cartonningCode").value="";
}
function prompt(){
	var operationCode = document.getElementById("cartonningCode").value;
	if(operationCode == ""){
		document.getElementById("cartonningCode").value ="扫描装箱单编号";
	}
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()" topmargin="7">
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=upProduct" method="post">
<div id="performance" style="margin-left:-2px;font-size:8px;"></div>
<table width="220" height="220" border="0" cellspacing="0">
<!--  <tr>
	<td colspan="2" align="center"><div id="performance" align="center" style="font-size:8px;"></div></td>
</tr>
-->
<tr align="center">
	<td colspan="2"><font size="4"style="font-weight:bold">上架作业</font>
	<font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td>
</tr>
<tr align="center">
	<td colspan="2"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
	[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
</tr>
<tr align="center">
	<td colspan="2"><font size="2"><%=DateUtil.getNow() %></font></td>
</tr>
<% if(result != null){ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="5"readonly="true" style="overflow-x:hidden;overflow-y:hidden"><%=result %></textarea></td>
	</tr>
<% }else{ %>
	<tr height="28">
		<td colspan="2" align="center"> <textarea cols="26" rows="5"readonly="true"style="overflow-x:hidden;overflow-y:hidden">请扫描装箱单编号!</textarea></td>
	</tr>
<% } %>
	<tr height="8">
		<td colspan="2" align="center"><input type="text" size="32" id="cartonningCode" name="cartonningCode" onblur="prompt()"onfocus="clearText()"onkeypress="javascript:return checkEnter()"></td>	
	</tr>


	<tr height="8" align="center">
		<td><input type="button" value="确 定" style="height:25px;width:100px;"onclick="check()"/></td>
		<td><input type="button" value="返 回" style="height:25px;width:100px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=cangneizuoye'" /></td>
	</tr>
		<td colspan="2" align="center"><input type="button" value="装箱记录管理" style="height:25px;width:200px" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=zhuangxiangguanli'" /></td>
	</tr>
</form>
</table>
</form>
</body>
</html>