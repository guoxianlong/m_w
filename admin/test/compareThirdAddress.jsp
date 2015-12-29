<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<%@page import="adultadmin.test.InsertAction"%>
<%@page import="java.util.List"%>
<html>
<%
if(request.getParameter("areaCode")!=null){
	InsertAction insertAction=new InsertAction();
	insertAction.compareThirdAddress(request,response);
}
List list =(List)request.getAttribute("list");
List list1 =(List)request.getAttribute("list1");
List list2 =(List)request.getAttribute("list2");
List list3 =(List)request.getAttribute("list3");
%>

<head>
<title>比较三级地址</title>
</head>
<body>
<form method="post">
<textarea rows="20" cols="20" name="areaCode"></textarea>
<input type="submit" value="提交"/>
</form>
<strong>数据库中没有的二级地址</strong><br>
<%if(list!=null){
for(int i=0;i<list.size();i++){%>
<%=list.get(i)%><br>
<%}}%>
<strong>EXCEL中没有的二级地址</strong><br>
<%if(list1!=null){
for(int i=0;i<list1.size();i++){%>
<%=list1.get(i)%><br>
<%}}%>
<strong>数据库中没有的三级地址</strong><br>
<%if(list2!=null){
for(int i=0;i<list2.size();i++){%>
<%=list2.get(i)%><br>
<%}}%>
<strong>EXCEL中没有的三级地址</strong><br>
<%if(list3!=null){
for(int i=0;i<list3.size();i++){%>
<%=list3.get(i)%><br>
<%}}%>
</body>
</html>