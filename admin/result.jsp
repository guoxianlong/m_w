<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%
	String tip = StringUtil.convertNull((String)request.getAttribute("tip"));
	String action = StringUtil.convertNull((String)request.getAttribute("action"));
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>买卖宝后台</title>
</head>
<body>
<script language="JavaScript" type="text/javascript">
<%if(tip.length() > 0){%>
alert('<%= tip %>');
<%}%>
<%if(action.equalsIgnoreCase("back")){%>
window.history.back(-1);
<%} else if(action.equalsIgnoreCase("refresh")){%>
window.location.reload();
<%} else if(action.equalsIgnoreCase("parentRefresh&close")){%>
window.opener.location.reload();
window.close();
<%} else if(action.equalsIgnoreCase("parentRefresh")){%>
window.parent.location.reload();
<%} else if(action.equalsIgnoreCase("close")){%>
window.close();
<%}%>
</script>
</body>
</html>
