<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<html>
<head>
<title>货位人员操作记录</title>
<%
List cargoLogList=null;
if(request.getAttribute("cargoLogList")!=null){
	cargoLogList=(List)request.getAttribute("cargoLogList");
}
PagingBean paging = (PagingBean) request.getAttribute("paging");
CargoInfoBean ciBean=(CargoInfoBean)request.getAttribute("ciBean");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
</head>
<body>
<form>人员操作记录-货位编号	<%=ciBean.getWholeCode() %>（<%=ciBean.getStoreTypeName() %>）</form><br/>
<table cellpadding="3" cellspacing="1" border=1 >
	<tr>
		<td>序号</td>
		<td>时间</td>
		<td>说明</td>
		<td>操作人员</td>
	</tr>
	<%for(int i=0;i<cargoLogList.size();i++){ %>
	<%CargoInfoLogBean logBean=(CargoInfoLogBean)cargoLogList.get(i);%>
	<tr>
		<td><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %></td>
		<td><%=logBean.getOperDatetime().substring(0,19) %></td>
		<td><%=logBean.getRemark()%></td>
		<td><%=logBean.getOperAdminName() %></td>
	</tr>
	<%} %>
</table>
<%if(paging!=null){ %>
		<p align="left"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
</body>
</html>