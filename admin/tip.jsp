<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%
	String tip = StringUtil.convertNull((String)request.getAttribute("tip"));
	String url = StringUtil.convertNull((String)request.getAttribute("url"));
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>买卖宝后台</title>
</head>
<body>
<script language="JavaScript">
alert('<%= tip %>');
<%if(!url.equals("")){%>
window.location = '<%= url %>';
<%}%>
</script>
</body>
</html>
