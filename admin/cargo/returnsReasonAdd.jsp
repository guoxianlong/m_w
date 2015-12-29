<%@page import="adultadmin.bean.*"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.cargo.ReturnsReasonBean"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>添加销售退货原因</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE4 {color: #009933}
-->
</style>
</head>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List list = (List)request.getAttribute("list");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script type="text/javascript">
function check(){
	var reason = $("#reason").val();
	if(reason != ""){
		document.forms[0].submit();
	}else{
		alert("原因不能为空！");
	}
}
</script>
<body >
<form action="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonAdd" method="post">
 <table width="35%" border="0" >
	<tr>
     <td>
     	<h3>添加销售退货原因</h3><hr/>
    </td>
   </tr>
</table>
<table  width="35%" border="1" cellspacing="0"  >
  <tr bgcolor="#00ccff">
    <td><div align="center"><span class="STYLE2"><font color="#00000">销售退货原因</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">操作</font></span></div></td>
  </tr>
  <tr >
    <td><div align="center"><input type="text" value="" name="reason" id="reason"></div></td>
    <td><div align="center"><input type="button" value="添加" onclick="check()"></div></td>
  </tr>
 </table>
</form>
</body>
</html>