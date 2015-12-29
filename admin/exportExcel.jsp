<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../../taglibs.jsp"%>
<%@ page import="java.sql.*,java.util.*"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.service.*" %>
<%@ page import="adultadmin.bean.*" %>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");

	UserGroupBean group = user.getGroup();

	String content = StringUtil.convertNull(request.getParameter("content"));
	String filename = StringUtil.convertNull(request.getParameter("filename"));

	response.setContentType("application/vnd.ms-excel;charset=gb2312");
	String now = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
	filename = filename + now;
	response.setHeader("Content-disposition","attachment; filename=\"" + new String(filename.getBytes("GBK"), "iso8859-1") + ".xls\"");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
</head>
<body>
<%=content.replaceAll("<[a|A] [^>]*>|<\\/[a|A]>","") %>
</body>