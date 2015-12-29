<%@page import="adultadmin.bean.*"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.cargo.ReturnsReasonBean"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>销售退货原因列表</title>
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
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script type="text/javascript">
</script>
<body >
<table width="50%" border="0" >
	  <tr>
     <td>
     	<h3>销售退货原因设置</h3>
     	<hr/>
    </td>
   </tr>
  <tr>
     <td> <div align="right">
     	<a href="<%=request.getContextPath()%>/admin/cargo/returnsReasonAdd.jsp"><font size="3" color="red">添加</font></a>
      </div>
      </td>
   </tr>
</table>
<table  width="50%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
     <td><div align="center"><span class="STYLE2"><font color="#00000">退货原因条码</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">销售退货原因</font></span></div></td>
    <td colspan="3"><div align="center"><span class="STYLE2"><font color="#00000">操作</font></span></div></td>
  </tr>
  <%if(list != null){
		for (int i = 0; i < list.size(); i++) {
			ReturnsReasonBean bean = (ReturnsReasonBean) list.get(i);
	    %>
  <tr  bgcolor="#FFFFCC">
    <td><div align="center"><%=i+1 %></div></td>
     <td><div align="center"><%=bean.getCode() %></div></td>
    <td><div align="center"><%=bean.getReason() %></div></td>
     <%if(group.isFlag(673)){ %> <td><div align="center"><a href="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonEdit&id=<%= bean.getId() %>">编辑</a></div></td><% } %>
   <td><div align="center"><a href="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonPrint&id=<%= bean.getId() %>">打印原因条码</a></div></td>
   <%if(group.isFlag(674)){ %>  <td><div align="center"><a href="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonDel&id=<%= bean.getId() %>">删除</a></div></td><% } %>
  </tr>
	  <%} 
}%>
</table>

<br>
</body>
</html>