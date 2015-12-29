<%@ page contentType="text/html; charset=utf-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<html>
<head>
<meta http-equiv="Content-type" content="text/html;charset=utf-8">
<% 
	response.setContentType("application/vnd.ms-excel");
	List auditPackageList=(List)request.getAttribute("auditPackageList");
	String fileName = "AuditPackage-"+DateUtil.getNow().replace(":","-").replace(" ","-");
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
</head>
<body>
<table cellpadding="3" border="1">
	<tr>
		<td>序</td>
		<td>订单编号</td>
		<td>分拣时间</td>
		<td>复核出库时间</td>
		<td>核对包裹时间</td>
		<td>包裹单号</td>
		<td>快递公司</td>
	</tr>
	<%if(auditPackageList!=null){ %>
		<%for(int i=0;i<auditPackageList.size();i++){ %>
		<%AuditPackageBean ap=(AuditPackageBean)auditPackageList.get(i); %>
	<tr>
		<td>&nbsp;<%=i+1 %></td>
		<td>&nbsp;<%=ap.getOrderCode() %></td>
		<td>&nbsp;<%=ap.getSortingDatetime().substring(0,19)%></td>
		<td>&nbsp;<%=ap.getCheckDatetime()==null?"":ap.getCheckDatetime().substring(0,19) %></td>
		<td>&nbsp;<%=ap.getAuditPackageDatetime()==null?"":ap.getAuditPackageDatetime().substring(0,19) %></td>
		<td>&nbsp;<%=ap.getPackageCode() %></td>
		<td>&nbsp;<%=ap.getDeliverName() %></td>
	</tr>
		<%} %>
	<%} %>
</table>
</body>
</html>