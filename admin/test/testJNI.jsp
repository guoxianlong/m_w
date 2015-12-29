<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="adultadmin.test.HelloWorld"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>测试JNI动态链接库</title>
</head>
<body>
<%
HelloWorld hello = new HelloWorld();
hello.sayHello("pig");
%>
</body>
</html>