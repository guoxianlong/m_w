<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../../taglibs.jsp"%>
<%@page import="java.sql.*"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");

	UserGroupBean group = user.getGroup();

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部

%>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<body> 
ai<%@include file="../../header.jsp"%>
<%
	String result = (String) request.getAttribute("result");
	if (result != null) {
		String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
</script>
<%
	}
%>
<fieldset>
   <legend>滞销统计</legend>
<form action="<%=request.getContextPath()%>/admin/historical.do" method="post">
统计日期：<input name="startDate" size=14 onclick="SelectDate(this,'yyyy-MM-dd');"/>&nbsp;&nbsp;至&nbsp;&nbsp;<input name="endDate" size=14 onclick="SelectDate(this,'yyyy-MM-dd');"/><br/>
产品编号：<input name="productCodes" size=30/>(产品编号为空表示统计全部，多个编号用逗号隔开)<br/>
  <input type="hidden" name="m" value="stat"/>
  <input type="submit" value="统计">&nbsp;&nbsp;<input type="button" value="清空数据" onclick="javascript:window.location='<%=request.getContextPath()%>/admin/historical.do?m=clear'">
</form>
</fieldset>
<br/>
<br/>
</body>

