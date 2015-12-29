<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%> 
<%@ page import="adultadmin.bean.order.AuditPackageBean"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>作业出库明细</title>
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
<body bgcolor="#ffcc00">
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    
    <td ><div align="center"><span class="STYLE2"><font color="#00000">日 期</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">快递公司</font></span></div></td>
    <td><div align="center"><span class="STYLE2"><font color="#00000">订单数</font></span></div></td>
  </tr>
  <%if(list.size()>0){
  		AuditPackageBean apBean = new AuditPackageBean();
  		apBean = (AuditPackageBean) list.get(0);
  %>
  	<tr  bgcolor="#FFFFCC" >
  		<td rowspan="<%=list.size() %>" width="33%"><div align="center"><%=apBean.getCheckDatetime()%></div></td>
    	<td><div align="center"><%=voOrder.deliverMapAll.get(apBean.getDeliver() + "")%></div></td>
    	<td><div align="center"><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=orderDetail&deliver=<%=apBean.getDeliver()%>'><%=apBean.getOrderCount() %></a></div></td>
  </tr>
	<% for (int i = 1; i < list.size(); i++) {
			apBean = (AuditPackageBean) list.get(i);%>
  <tr  bgcolor="#FFFFCC" >
   	<td><div align="center"><%=voOrder.deliverMapAll.get(apBean.getDeliver() + "")%></div></td>
    <td><div align="center"><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=orderDetail&deliver=<%=apBean.getDeliver()%>'><%=apBean.getOrderCount() %></a></div></td>
  </tr>
  	<%}
  }%>
</table>
</form>
</body>
</html>