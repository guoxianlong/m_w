<%@ include file="taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %><%@ page import="adultadmin.util.*"%><%@ page import="adultadmin.util.db.DbOperation,java.util.Date,adultadmin.action.vo.voUser,adultadmin.util.StringUtil,java.util.List,java.text.SimpleDateFormat,java.sql.ResultSet,java.util.ArrayList,mmb.system.admin.AdminService" %><%@ page import="adultadmin.util.Constants" %><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"><%
voUser user = (voUser)session.getAttribute("userView");
voUser myUser = (voUser)session.getAttribute("myUser");
if(myUser!=null){
DbOperation dbOp = new DbOperation();
dbOp.init(DbOperation.DB);
int uid=dbOp.getInt("select id from authorize where suser_id="+Integer.valueOf(myUser.getId())+" and in_use = 1 and is_delete=0 and now()>start_time and now()<end_time");
dbOp.release();
if(uid==0&&myUser!=null){
	user = myUser;
	myUser = null;
	request.getSession().setAttribute("userView", myUser);
	request.getSession().removeAttribute("myUser");
}
}
%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.10.2.min.js"></script>
<title>无标题文档</title>
</head>
<script>
function toggletree() {
	var f=parent.document.getElementById('contentFrame');
	if(f.cols!='0,*')
		f.cols='0,*';
	else
		f.cols='220,*';
	return false;
}
$(function(){
<%if(myUser!=null){%>
$("#changepassword").click(function(){
	alert("您正在使用授权帐号，不能修改对方密码");
});
<%}%>
});
</script>
<style>
#ind_1 {
	background-color: #F3F3F3;
	margin: 0px;
	padding: 0px;
	width: 100%;
	height: 85px;
	border-bottom-width: 1px;
	border-bottom-style: solid;
	border-bottom-color: #666666;
}
body {
	margin: 0px;
	padding: 0px;
}
#tu_1 {
	float: left;
	margin-left: 8px;
}
#tu_2 {
	float: right;
	text-align:right;
}
#tet {
	font-family: "宋体";
	font-size: 12px;
	margin-top: 60px;
	margin-left: 40px;
	float:left;
}
</style>
<body>
<div id="ind_1">
<div id="tu_1"><img src="images/00002.jpg" /></div>
<div id="tu_2">
<%
String project=Constants.configCompile.getProperty("project");
String branch=Constants.configCompile.getProperty("branch");
String revision=Constants.configCompile.getProperty("revision");
%>
<%if(project!=null){%><%=project%><%}%><br/>
<%if(branch!=null){%><%=branch%><%}%><br/>
<%if(revision!=null){%>r<%=revision%><%}%><br/>
</div>
<div id="tet"><table border="0" cellpadding="0" cellspacing="0">
  <tr>
     <td>登陆用户:
    <%
    	if(myUser!=null){
    %>
    	<%=myUser.getUsername()%>
    	(<%=user.getUsername()%>)
    <%
    	}else{
    %>
    	 <%=user.getUsername()%>
    <%	
    	}
    %>
    <!-- 登录后显示的账户名中，若在使用授权，则帐户名后面加入授权人的帐户名。 -->
    </td>
    <td style="padding:0px 0px 0px 7px">[<a href="logout.jsp"><font color="red">注销</font></a>|<a href="authorize.jsp" target="mainFrame">授权</a>]</td>
    <td style="padding:0px 0px 0px 7px"><%if(myUser!=null) {%><a id="changepassword" href="JavaScript:">修改密码</a>&nbsp;|&nbsp;<%} else{%><a  href="perm/set.jsp" target="mainFrame">修改密码</a>&nbsp;|&nbsp;<%} %> <a href="#" onclick="top.mainFrame.location.reload();return false;">刷新页面</a>
 |  <%if(true){%><a href="#" onclick="return toggletree()">隐藏功能树</a><%}%>
 	</td>
  </tr>
</table>
</div>
</div>
</body>
</html>
