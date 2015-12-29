<%@ page contentType="text/html;charset=utf-8" %><%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.util.*"%><%
CookieUtil ck = new CookieUtil(request,response);
voUser user = (voUser)session.getAttribute("userView");
if(user!=null) {
	response.sendRedirect(request.getContextPath() + "/admin/default.mmx");
	return;
}
if(request.getParameter("dc")!=null) {		// 删除cookie的操作
	ck.removeCookie("u");
	ck.removeCookie("p");
	ck.removeCookie("ru");
	ck.removeCookie("rp");
	response.sendRedirect("login.mmx");
	return;
}
String username = ck.getCookieValue("u");
String password = ck.getCookieValue("p");
boolean ru=ck.getCookieValue("ru")==null||ck.getCookieValue("ru").equals("1");
boolean rp=ck.getCookieValue("rp")!=null&&ck.getCookieValue("rp").equals("1");
String focus = "username";
if(username==null||!ru)
	username="";
else		// 已经保存了密码则焦点放到密码框
	focus="password";
if(password==null||!rp)
	password="";
else
	focus = "username";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>买卖宝后台登陆</title>
<link rel="shortcut icon" href="<%= request.getContextPath()%>/admin/favicon.ico"> 
</head>
<script type="text/javascript">
if (window!=top){ // 判断当前的window对象是否是top对象
	var parentLocation = top.location;	
	if(parentLocation!=null){
		// 如果不是，将top对象的网址自动导向被嵌入网页的网址，且使用top的主机。
		var url = parentLocation.protocol+"//"+parentLocation.host+"/admin/login.mmx";
		top.location.href=url;
	}
}
</script>

<style>
body {
	font-family: "宋体";
	font-size: 12px;
}
.input1 {
	margin: 0px;
	padding: 0px;
	height: 15px;
	width: 100px;
}
<%if(!rp){%>
#d_1 {
	color: #494949;
	margin-top: 30px;
	margin-left: 12px;
}
#dv {
	margin-top:15%;
	margin-left:auto;
	margin-right:auto;
	float:center;
	width:500px
}

<%}%>

</style>
<%
String errorMsg =(String) request.getAttribute("errorMsg");
if(errorMsg!=null && errorMsg.length()>0){
%>
	<script>
		window.alert("<%=errorMsg%>");
	</script>
<%
}
%>
<body onload="document.f1.<%=focus%>.focus();document.f1.<%=focus%>.select();" style="background-color:rgb(255,255,255);">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<form  method="post" action="login.mmx" name="f1">
<%if(rp){%>

	<table border="1" cellpadding="3" cellspacing="3" style="margin:auto;text-align:center;">
  <tr>
    <td>用户名：</td>
    <td><input name="username" type="text" class="input1" value="<%=username%>"></td>
  </tr>
  <tr>
    <td>密 &nbsp;码：</td>
    <td><input name="password" type="password" class="input1" value="<%=password%>"></td>
  </tr>
  <tr>
    <td colspan=2><input name="ru" type="checkbox" class="input2" value="1" <%if(ru){%>checked<%}%> >记住用户名 
    <input name="rp" type="checkbox" class="input2" value="1" <%if(rp){%>checked<%}%> >简化版本</td>
  </tr>
  <tr>
    <td><input type="submit" value="登 陆" border="0" /></td>
    <td style="text-decoration:underline"><a href="../login.mmx?dc=1"><font color="#000000">删除Cookie</font></a></td>
  </tr>
</table>

<%}else{%>
<table border="0" cellpadding="0" cellspacing="0" id="dv">
  <tr>
    <td style="background:white url(<%=request.getContextPath()%>/admin/rec/images/blue_03.gif) no-repeat top left">
	<table border="0" cellpadding="0" cellspacing="0" id="d_1" width="220px" height="150px">
  <tr>
    <td><table border="0" cellpadding="0" cellspacing="0" style="margin:5px 20px 5px 24px">
  <tr>
    <td width="55">用户名：</td>
    <td><input name="username" type="text" class="input1" value="<%=username%>"></td>
  </tr>
</table>
</td>
  </tr>
  <tr>
    <td><table border="0" cellpadding="0" cellspacing="0" style="margin:5px 20px 5px 24px">
  <tr>
    <td width="55">密 &nbsp;码：</td>
    <td><input name="password" type="password" class="input1" value="<%=password%>"></td>
  </tr>
</table></td>
  </tr>
  <tr>
    <td><div style="margin:5px 5px 5px 15px"><img src="<%=request.getContextPath()%>/admin/rec/images/line.gif" /></div></td>
  </tr>
  <tr>
    <td><table border="0" cellpadding="0" cellspacing="0" style="margin:5px 20px 5px 18px">
  <tr>
    <td width="90"><input name="ru" type="checkbox" class="input2" value="1" <%if(ru){%>checked<%}%> >记住用户名</td>
    <td><input name="rp" type="checkbox" class="input2" value="1" <%if(rp){%>checked<%}%> >简化版本</td>
  </tr>
</table></td>
  </tr>
  <tr>
    <td><table border="0" cellpadding="0" cellspacing="0" style="margin:5px 20px 5px 24px">
  <tr>
    <td width="90"><input type="image" src="<%=request.getContextPath()%>/admin/rec/images/denglu_22.gif" border="0" /></td>
    <td style="text-decoration:underline"><a href="<%=request.getContextPath()%>/admin/login.mmx?dc=1"><font color="#000000">删除Cookie</font></a></td>
  </tr>
</table></td>
  </tr>
</table>
	</td>
    <td width="273" height="213" style="background-image:url(<%=request.getContextPath()%>/admin/rec/images/blue_05.gif)"></td>
  </tr>
</table>
<div id="time" style="margin:auto;text-align:center;font-size: 18px;"></div>
<script language="javascript">
var servertime=new Date();
servertime.setTime(<%=System.currentTimeMillis()%>); 
var timeDifference=new Date()-servertime;
var MyInterval=setInterval("Refresh()",1000);
function Refresh(){
	var a = new Date();
	a.setTime(a.getTime()-timeDifference);
	var elem = document.getElementById("time");
	elem.innerHTML=a.Format("yyyy-MM-dd hh:mm:ss") ;
}
</script>
<%}%>
</form>
</body>
</html>
