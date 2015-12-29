<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
    <%@page import="java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
Long time=(Long)request.getAttribute("time");
List timeList=(List)request.getAttribute("timeList");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>EMS省外接口平均运行时间</title>
</head>
<body>
<%
	if(timeList!=null&&timeList.size()>0){
		for(int i =0;i<timeList.size();i++){ %>
		第<%=i+1%>次 ems省外接口执行时间为<%=timeList.get(i)%>ms<br/>
<%}} %>
<font color='red' size='3'>EMS省外接口平均运行时间为<%=time %>ms</font>
</body>
</html>