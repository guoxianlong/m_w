<%@page import="adultadmin.test.TestAjaxAction"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%
TestAjaxAction action = new TestAjaxAction();
action.test(request,response);
%>
<option><%=s %></option>