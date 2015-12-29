<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.util.Encoder" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<%@ page import="adultadmin.action.stock.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="mmb.stock.stat.*"%>
<%
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
%>
<%
String orderId=request.getParameter("orderId");//从发货单预览页传来的订单Id
String scanType=StringUtil.convertNull(request.getParameter("scanType"));
if(orderId!=null&&!orderId.equals("")){
	StockAction action = new StockAction();
	action.quickCancelStock(request, response);
}
%>
<html>
<head>
<title>快速退货</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function getScanType1(){
	document.getElementById("scanType1").style.display="inline";
	document.getElementById("scanType2").style.display="none";
	document.getElementById("orderCode").focus();
}
function getScanType2(){
	document.getElementById("scanType1").style.display="none";
	document.getElementById("scanType2").style.display="inline";
	document.getElementById("packageCode").focus();
}
function getFocus(){
	if(document.getElementById("scanType1").style.display=="inline"){
		document.getElementById("orderCode").focus();
	}else if(document.getElementById("scanType2").style.display=="inline"){
		document.getElementById("packageCode").focus();
	}
}
function checkSubmit(){
	var wareArea = document.getElementById("wareArea").value;
	if( wareArea == null || wareArea == "" || wareArea == "-1" ) {
		alert("请选择操作的库地区！");
		return false;
	}
	if(document.getElementById("scanType1").style.display=="inline"){
		if(trim(document.getElementById("orderCode").value)==""){
			alert("请输入订单编号！");
			document.getElementById("orderCode").focus();
			return false;
		}
	}else if(document.getElementById("scanType2").style.display=="inline"){
		if(trim(document.getElementById("packageCode").value)==""){
			alert("请输入包裹单号！");
			document.getElementById("packageCode").focus();
			return false;
		}
	}
	
	return true;
}
</script>
</head>
<body onload="getFocus();">
快速退货——扫描单据
<%if(request.getParameter("tip")!=null){//从发货单预览页传来的错误提示
	String test=request.getParameter("tip");%>
	<script type="text/javascript">alert('<%=request.getParameter("tip")%>');</script>
<%}%>
<%if(request.getAttribute("canceled")!=null&&request.getAttribute("canceled").toString().equals("success")){ %>
<font color="green">退货成功！</font>
<%}else if(request.getAttribute("result")!=null&&request.getAttribute("result").equals("failure")){ %>
<script type="text/javascript">alert("<%=request.getAttribute("tip")%>");</script>
<%} %>
<form action="previewOrderStock.jsp" method="post" onsubmit="return checkSubmit();">
<input type="radio" name="scanType" value="1" onfocus="getScanType1();" checked="checked"/>扫描订单编号&nbsp;&nbsp;&nbsp;
<input type="radio" name="scanType" value="2" onfocus="getScanType2();"/>扫描包裹单号<br/>
<div id="scanType1" style="display:inline">
	订单编号：<input type="text" id="orderCode" name="orderCode" size="20"/>&nbsp;&nbsp;
</div>
<div id="scanType2" style="display:none">
	包裹单号：<input type="text" id="packageCode" name="packageCode" size="20"/>&nbsp;&nbsp;
	
</div>
<div style="margin-top:5px">
			  &nbsp;&nbsp;库地区:
					<%= wareAreaSelectLable%>
</div>
<input type="submit" value="确定" />
</form>
<script type="text/javascript">
<%if(scanType.equals("1")){%>
	document.getElementsByName("scanType")[0].checked=true;
	document.getElementById("scanType1").style.display="inline";
	document.getElementById("scanType2").style.display="none";	
	document.getElementById("orderCode").focus();
<%}else if(scanType.equals("2")){%>
	document.getElementsByName("scanType")[1].checked=true;
	document.getElementById("scanType1").style.display="none";
	document.getElementById("scanType2").style.display="inline"
	document.getElementById("packageCode").focus();
<%}%>
</script>
</body>
</html>