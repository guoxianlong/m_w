<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>货位绑定产品-核实信息</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<%
voProduct product=(voProduct)request.getAttribute("product");
CargoInfoBean ciBean=(CargoInfoBean)request.getAttribute("cargoInfoBean");
List cargoList=(List)request.getAttribute("cargoList");
List stockCountList=(List)request.getAttribute("stockCountList");
String address=(String)request.getAttribute("address");
String productLineName=(String)request.getAttribute("productLineName");
%>
<script type="text/javascript">
function toCargoProduct(){
	document.getElementById('method').value = 'toCargoProduct';
	document.cargoForm.submit();
}
</script>
</head>
<body>
货位绑定产品-核实信息<br/><br/>
请认真核实以下信息，确认无误：<br/><br/>
<b>产品信息：</b><br/>
产品编号：<b><a href="../admin/fproduct.do?id=<%=product.getId() %>" target="_blank"><%=product.getCode() %></a></b>&nbsp;&nbsp;&nbsp;
产品线：<%=product.getProductLineName() %><br/>
产品原名：<%=product.getOriname() %><br/>
当前已绑定货位：<%if(cargoList.size()==0){ %>没有<%} %><br/>
<%if(cargoList.size()!=0){ %>
<table cellpadding="3" cellspacing="1" border=1>
	<tr bgcolor="#4688D6">
		<td><font color="#FFFFFF">货位号</font></td>
		<td><font color="#FFFFFF">货位库存</font></td>
		<td><font color="#FFFFFF">货位类型</font></td>
		<td><font color="#FFFFFF">存放类型</font></td>
		<td><font color="#FFFFFF">库存类型</font></td>
	</tr>
	<%for(int i=0;i<cargoList.size();i++){ %>
	<%CargoInfoBean bean=(CargoInfoBean)cargoList.get(i); %>
	<tr>
		<td><%=bean.getWholeCode() %></td>
		<td><%=stockCountList.get(i).toString() %></td>
		<td><%=bean.getTypeName() %></td>
		<td><%=bean.getStoreTypeName()%></td>
		<td><%=bean.getStockTypeName() %></td>
	</tr>
	<%} %>
</table>
<%} %><br/>
<b>货位信息：</b><br/>
货位号：<b><%=ciBean.getWholeCode() %></b>（<%=address %>）<br/>
货位状态：<%=ciBean.getStatusName() %><br/>
库存类型：<%=ciBean.getStockTypeName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
货位产品线：<%=productLineName %><br/>
存放类型：<%=ciBean.getStoreTypeName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
警戒线：<%=ciBean.getWarnStockCount()%><br/>
货位类型：<%=ciBean.getTypeName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
货位最大容量：<%=ciBean.getMaxStockCount() %><br/>
货位尺寸：长<%=ciBean.getLength() %>cm&nbsp;&nbsp;宽<%=ciBean.getWidth() %>cm&nbsp;&nbsp;高<%=ciBean.getHigh() %>cm<br/>
<form action="../admin/cargoInfo.do" name="cargoForm">
<input type="hidden" id="method" name="method" value="cargoProduct"/>
<input type="hidden" name="cargoCode" value="<%=ciBean.getWholeCode() %>"/>
<input type="hidden" name="productCode" value="<%=product.getCode() %>"/>
<input type="submit" value="确认提交"/>&nbsp;
<input type="button" value="取消返回" onclick="return toCargoProduct();"/>
</form>
</body>
</html>