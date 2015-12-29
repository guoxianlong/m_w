<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="adultadmin.test.InsertAction" %>
<%@ page import="adultadmin.action.vo.voOrder" %>
<%@ page import="adultadmin.action.vo.voProduct" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<%@page import="adultadmin.test.ListUtil"%>
<%@page import="adultadmin.util.StringUtil"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>插入订单</title>
</head>
<body>
<%
	int type = StringUtil.StringToId(request.getParameter("type"));
	ListUtil.sortList(type);
%>
</body>
</html>