<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%> 
<%@ page import="adultadmin.bean.order.AuditPackageBean"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>销售退货原因统计</title>
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
String[][] statistics = (String[][])request.getAttribute("statistics");
int y_length = statistics[0].length-1;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<body bgcolor="#FFFF99">
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
<%if(statistics.length>0){ %>
  <tr bgcolor="#00ccff" >
  	<td rowspan="2"><div align="center"><span class="STYLE2"><font color="#00000"><%= statistics[0][0] %></font></span></div></td>
  	<%		for(int i=1;i<statistics[0].length-1;i++){%>
    <td colspan="2"><div align="center"><span class="STYLE2"><font color="#00000"><%= statistics[0][i] %></font></span></div></td>
  	<%		} %>
  	<td rowspan="2" bordercolor="#FF00FF"><div align="center"><span class="STYLE2"><font color="#00000"><%= statistics[0][y_length] %></font></span></div></td>
  </tr>
 <tr bgcolor="#FF3300">
  	<%		for(int i=1;i<statistics[0].length-1;i++){%>
    <td ><div align="center"><span class="STYLE2"><font color="#00000">退货量</font></span></div></td>
    <td ><div align="center"><span class="STYLE2"><font color="#00000">占比</font></span></div></td>
  	<%		} %>
  </tr >
  <% for(int j=1;j<statistics.length;j++){ %>
 		<tr >
  			<td><div align="center"><font color="#000000"><%= statistics[j][0] %></font></div></td>
  		<%for(int k=1;k<statistics[j].length-1;k++){
  			double number= StringUtil.toDouble(statistics[j][k]) / StringUtil.toDouble(statistics[j][y_length])*100 ;
  			int index = (number+"").indexOf(".");
  			String newNumber = (number+"").substring(0,index+2);
  		%>
    		<td ><div align="center"><%if(statistics[j][k] != null ){%><%=statistics[j][k]  %><% }else{ %>0<% } %></div></td>
    		<td ><div align="center"><font color="red"><%if(statistics[j][k] != null ){%><%= newNumber%>%<% }else{ %>0<% } %></font></div></td>
  		<%}%>
  			<td><div align="center"><font color="#000000"><% if(statistics[j][y_length]!= null){ %><%= statistics[j][y_length] %><% }else{ %>0<% } %></font></div></td>
  	   </tr>
  <%}
}%>
</table>
</form>
</body>
</html>