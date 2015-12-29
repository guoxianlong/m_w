<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ include file="../../taglibs.jsp"%>
<%@ include file="../../jstlTaglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>测试标签库</title>
</head>
<body>
${param.p1 }<br/>
<c:out value="${param.p1}" default="没有参数"></c:out><br/>
</body>
</html>