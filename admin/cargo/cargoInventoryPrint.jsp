<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	List list=null;
	if(request.getAttribute("list")!=null){
		list=(List)request.getAttribute("list");
	}
	List productCodeList=null;
	if(request.getAttribute("productCodeList")!=null){
		productCodeList=(List)request.getAttribute("productCodeList");
	}
	List productNameList=null;
	if(request.getAttribute("productNameList")!=null){
		productNameList=(List)request.getAttribute("productNameList");
	}
	List productLineNameList=(List)request.getAttribute("productLineNameList");
	response.setContentType("application/vnd.ms-excel;charset=gb2312");
	String fileName = "CargoInventory-"+DateUtil.getNow().replace(" ","-").replace(":","-");
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
</head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
<body>
<table width="100%" cellpadding="3" cellspacing="1" border="1">
	<tr align="center">
		<td>序号</td>
		<td>货位产品线</td>
		<td>产品编号</td>
		<td>产品原名称</td>
		<td>货位库存量</td>
		<td>货位冻结量</td>
		<td>货位号</td>
		<td>盘点量</td>
		<td>差异</td>
	</tr>
	<%for(int i=0;i<list.size();i++){ %>
		<%CargoProductStockBean cpsBean=(CargoProductStockBean)list.get(i); %>
	<tr align="center">
		<td><%=i+1 %></td>
		<td><%=productLineNameList.get(i) %></td>
		<td><%=productCodeList.get(i).toString() %></td>
		<td><%=productNameList.get(i).toString() %></td>
		<td><%=cpsBean.getStockCount()+cpsBean.getStockLockCount() %></td>
		<td><%=cpsBean.getStockLockCount() %></td>
		<td><%=cpsBean.getCargoInfo().getWholeCode() %></td>
		<td></td>
		<td></td>
	</tr>
	<%} %>
</table>
</body>
</html>