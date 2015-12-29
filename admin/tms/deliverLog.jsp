<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<title>快递公司操作日志</title>
</head>
<body>
	<table width="60%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">操作人</font></td>
			<td><font color="#FFFFFF">操作时间</font></td>
			<td><font color="#FFFFFF">操作描述</font></td>
		</tr>
		<c:forEach items="${logList}" var="log" varStatus="index">
			<tr>
				<td>${index.count}</td>
				<td>${log.userName}</td>
				<td>${log.createDatetime}</td>
				<td>${log.content}</td>
			</tr>
		</c:forEach>
</body>
</html>
