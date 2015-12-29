<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String text=request.getParameter("excel");
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<title>Insert title here</title>
<%
response.setContentType("application/vnd.ms-excel;charset=gb2312");
response.setHeader("Content-disposition","attachment; filename=\"" + new String("合格库作业动态明细表".getBytes("GB2312"),"ISO8859-1") + ".xls\"");
%>
</head>
<body>
<table border="1">
<%=text %>
</table>
</body>
</html>