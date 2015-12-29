<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<html>
<head>
<title>扫描发货清单(兰亭专用)</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function checkCode(){
	var orderCode=document.getElementById("orderCode");
	if(trim(orderCode.value)==""){
		document.getElementById("tip").style.color="red";
		document.getElementById("tip").innerHTML="请扫描或输入发货单编号！";
		document.getElementById("orderCode").focus();
		return false;
	}
	return true;
}
</script>
</head>
<body>
&nbsp;&nbsp;&nbsp;扫描发货清单(兰亭专用)&nbsp;&nbsp;&nbsp;&nbsp;<span id="tip"></span>
<form action="<%=request.getContextPath() %>/admin/scanOrderStock.do" onsubmit="return checkCode();">
出库单编号：<input type="text" id="orderCode" name="orderCode"/>
<input type="hidden" name="orderstock" value="orderStock"/>
<input type="hidden" name="checkType" value="lt"/>
<input type="hidden" name="scanFlag" value="4"/>
<input type="submit" value="确认" />
</form>
<script type="text/javascript">
document.getElementById("orderCode").focus();
</script>
</body>
</html>