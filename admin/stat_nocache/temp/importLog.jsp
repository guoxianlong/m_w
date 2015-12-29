<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="stat.StatImporter" %>
<%
response.setHeader("Cache-Control","no-cache");
StatImporter.importLog(request.getParameter("hour"));
%>
success.