<%@page import="adultadmin.util.StringUtil"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String operCode=request.getParameter("operCode");
String staffCode=request.getParameter("staffCode");
%>
<html>
<head>
<title>设备扫描</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function checkOperCode(){
	var operCode=trim(document.getElementById("operCode").value);
	if(operCode==""){
		alert("作业单不能为空！");
		return false;
	}
	return true;
}
function checkStaffCode(){
	var staffCode=trim(document.getElementById("staffCode").value);
	if(staffCode==""){
		alert("员工编号不能为空！");
		return false;
	}
	return true;
}
function onLoad(){
	<%if((operCode==null&&staffCode==null)||(operCode!=null&&staffCode!=null)){ %>
	var orderc = document.getElementById("operCode");
	<%}else if(operCode!=null&&staffCode==null){ %>
	var orderc = document.getElementById("staffCode");
	<%} %>
	orderc.value="";
	orderc.focus();
}
</script>
</head>
<body onload="onLoad()">
<%if(request.getAttribute("tip")!=null&&StringUtil.convertNull((String)request.getAttribute("result")).equals("success")){ %>
<script type="text/javascript">
alert('<%=request.getAttribute("tip")%>');
</script>
<%} %>
<%if((operCode==null&&staffCode==null)||(operCode!=null&&staffCode!=null)){ %>
	<form action="qualifiedStock.do?method=cargoOperFac" method="post" onsubmit="return checkOperCode();">
	扫描作业单编号：<br/>
	<input type="text" id="operCode" name="operCode" />
	</form>
<%}else if(operCode!=null&&staffCode==null){ %>
	<form action="qualifiedStock.do?method=cargoOperFac" method="post" onsubmit="return checkStaffCode();">
	扫描员工编号：<br/>
	<input type="text" name="staffCode" id="staffCode"/>
	<input type="hidden" name="operCode" value="<%=operCode %>"/>
	</form>
<%} %>
</body>
</html>