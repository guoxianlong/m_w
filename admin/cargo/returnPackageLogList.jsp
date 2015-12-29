<%@page import="adultadmin.bean.*"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="mmb.stock.stat.ReturnPackageLogBean"%>
<%@ page import="adultadmin.util.StringUtil" %>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>退货日志列表</title>
</head>
<%
	voUser user = (voUser) request.getSession()
			.getAttribute("userView");
	String orderCode = (String) request.getParameter("orderCode");
	String packageCode = (String) request.getAttribute("packageCode");
	packageCode = (packageCode==null || "".equals(packageCode)) ? "" : "包裹<font color=\"red\">"+packageCode +" </font>的退货操作日志";
	List list = null;
	if (request.getAttribute("ReturnPackageLogList") != null) {
		list = (List) request.getAttribute("ReturnPackageLogList");
	} else {
		list = new ArrayList();
	}
%>
<link href="<%=request.getContextPath()%>/css/global.css"
	rel="stylesheet" type="text/css">
<script type="text/javascript"
	src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript"
	src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<body onload="document.getElementById('orderCode').focus();">
	<center>
		<h2>退货日志查询</h2>
	</center>
	<form action="../admin/returnStorageAction.do?method=ReturnPackageLog"
		method="post">
		<center>
			<fieldset style="width: 78%; height: 40px;">
				<legend align="left" style="align: left;">查询栏</legend>
				<div style="margin-left: 10px; position: relative;">
					订单号/包裹单号：<input type="text"  id="orderCode" name="orderCode"  size=15
						<%if (null != orderCode && !orderCode.equals("")) {%>
						value="<%=orderCode%>" <%}%> />&nbsp;&nbsp;
					<td><input type="submit" value="查看日志"></td>
				</div>
			</fieldset>
		</center>
		<center>
			<h3><%=packageCode %></h3>
		</center>
		<table width="82%" align="center" border="0" cellspacing="1"
			cellpadding="0" bgcolor="#000000">
			<tr bgcolor="#00ccff">
				<td align="center">序号</td>
				<td width="200" align="center">操作时间</td>
				<td align="center">操作人</td>
				<td width="400" align="center">操作内容</td>
			</tr>
			<%
				for (int i = 0; i < list.size(); i++) {
			%>
			<tr <%if (i % 2 == 0) {%> bgcolor="#eee9d9" <%} else {%>
				bgcolor="#ffffff" <%}%>>
				<%
					ReturnPackageLogBean bean = (ReturnPackageLogBean) list.get(i);
				%>
				<td align="center"><%=i + 1%></td>
				<td align="center"><%=bean.getOperTime().substring(0, 19)%></td>
				<td align="center"><%=bean.getOperName()%></td>
				<td align="center"><%=bean.getRemark()%></td>
				<%
					}
				%>
			</tr>
		</table>
		<br>
	</form>
</body>
</html>