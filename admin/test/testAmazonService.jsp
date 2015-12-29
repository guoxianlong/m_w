<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<%@page import="java.util.*"%>
<%@page import="java.util.Map.*"%>
<%
	InsertAction insertAction=new InsertAction();
	Map<String,String> map = insertAction.testAmazonService(request, response);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>亚马逊</title>
</head>
<body>
<%
	Set<Entry<String,String>> sets = map.entrySet();
for(Entry<String,String> en : sets ) {
 %>
 	<%= en.getKey() %>----:------<%= en.getValue() %><br/>
 <%
}
%>
</body>
</html>