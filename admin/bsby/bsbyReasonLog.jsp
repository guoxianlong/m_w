<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.util.PageUtil"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="mmb.bsby.model.BsbyReasonLog"%>

<html>
<%
voUser user = (voUser) request.getAttribute("userView");
List bsbyReasonLogs=(List)request.getAttribute("bsbyReasonLogs");
PagingBean paging = (PagingBean) request.getAttribute("paging");

%>
<head>
<title>报损报溢操作日志列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
</head>
<body>

<table>
 
  <% if(bsbyReasonLogs!=null){
    	 for(int i=0;i<bsbyReasonLogs.size();i++){ %>
  <% BsbyReasonLog bsbyReasonLog=(BsbyReasonLog)bsbyReasonLogs.get(i); %>
  <tr>
   	
   		<td><div align="center"><%=StringUtil.convertNull(bsbyReasonLog.getOperType())%><%=bsbyReasonLog.getType() ==1 ?"报溢原因":"报损原因"%></div></td>
   		<td><div align="center"><%=bsbyReasonLog.getReason()%></div></td>
        <td><div align="center"><%=bsbyReasonLog.getOperUserName()%></div></td>
   		<td><div align="center"><%=StringUtil.convertNull(StringUtil.cutString(bsbyReasonLog.getOperDateTime(), 0, 19) ) %></div></td>
  </tr><%}} %>
</table>
<%if (paging!=null){%>
<p align="left"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 5)%></p>
<%} %>
</body>
</html>