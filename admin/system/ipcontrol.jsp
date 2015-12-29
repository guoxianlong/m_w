<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %><%@ page import="java.util.*"%><%@ page import="adultadmin.util.*,adultadmin.util.db.*"%>
<% 
String show = "";

if(request.getParameter("dc") != null){
	show = "删除成功!";
}
if(request.getParameter("ac") != null){
	show = "添加成功!";
}
if(request.getParameter("del") != null){
	int d = StringUtil.toInt(request.getParameter("del"));
	if(d>=0 && d < UserControlUtil.allowedIp.size()){
	    DbOperation db = new DbOperation();
	    IP ip = (IP)UserControlUtil.allowedIp.get(d);
	    if (db.executeUpdate("delete from ip_group where `group`='allow'and ip='"+ip.toString()+"'")) {
			UserControlUtil.allowedIp.remove(d);
	    }
	    db.release();
		response.sendRedirect("ipcontrol.jsp?dc=1");return;
	}else{show="请重新确认要删除的IP";}
}

if(request.getParameter("add") != null && request.getParameter("add").length()>2){
	String ip = request.getParameter("add");
	IP aip = new IP(ip);
	if(aip.isValid()) {
        DbOperation db = new DbOperation();
        if (db.executeUpdate("insert into ip_group set `group`='allow',create_time=now(),ip='"+aip.toString()+"'")) {
        	UserControlUtil.allowedIp.add(0,aip);
        }
	    db.release();
		response.sendRedirect("ipcontrol.jsp?ac=1");return;
	}else{show="无效IP";}
}

if (request.getParameter("reload") != null) {UserControlUtil.initAllowedIp();}

List list = UserControlUtil.allowedIp;

 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="../../css/global.css" rel="stylesheet" type="text/css">
<script>
</script>
<title>IP管理</title>
</head>
<body style="margin-left:12px;line-height:150%;font-size:14px;">
<% if (show.length() > 0) {%><span style="color=red"><%=show%></span><%}%>

<table>
<% 
	if (list != null && list.size() > 0) {
	for (int i = 0;i < list.size();i++) {
		IP ip = (IP) list.get(i);
		if (ip == null) continue;
		%><tr><td><%=ip.getIpRange()%></td><td><a href="ipcontrol.jsp?del=<%=i%>">删</a></td></tr><%
	}
	}
 %>
</table>
<form method="post" action="ipcontrol.jsp">
<table>
<tr><td><input type="text" name="add"/></td></tr>
<tr><td><input type="submit" value="添加"></td></tr>
</table>
</form>
<a href="ipcontrol.jsp?reload=1">清缓存</a><br/>
</body>
</html>
