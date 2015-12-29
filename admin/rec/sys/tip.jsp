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
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/demo/demo.css">
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/locale/easyui-lang-zh_CN.js"></script>
<title>买卖宝后台</title>
</head>
<body>
<script language="JavaScript">
String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
$.messager.alert('提示','<%= tip %>');
<%if(!url.equals("")){%>
window.location = '<%= url %>';
<%}%>
</script>
</body>
</html>
