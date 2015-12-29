<%@ page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.order.AuditPackageBean,adultadmin.action.vo.voProduct,adultadmin.action.vo.voOrder,
				adultadmin.bean.stat.WarehousingAbnormalBean,mmb.stock.stat.WarehousingAbnormalService "%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>查看异常入库单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<%
List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
List<voProduct> rpList = (ArrayList<voProduct>)request.getSession().getAttribute("rpList");
WarehousingAbnormalBean anormalBean = (WarehousingAbnormalBean)request.getAttribute("anormalBean");
AuditPackageBean apBean = (AuditPackageBean)request.getSession().getAttribute("apBean");
String wareArea = (String)request.getSession().getAttribute("wareArea");
%>
<script type="text/javascript">
$(document).ready(function(){
     $('#return').click(function(){
    	 window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=selectAbnormalList';
     });
	$('#print').click(function(){
		 window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=printWarehousingAbnormal&abnormalId='+ <%=anormalBean.getId()%>
	});
});
</script>
</head>
<body >
<div style="width:  95%"><h1 align="center">查看异常入库单</h1></div>
<%if(wareArea!=null){
	String whAreaSelection = WarehousingAbnormalService.getWeraAreaOptions(request,Integer.parseInt(wareArea),true);%>
	<div align="right" style="width:  95%;color: red;font-size:14px"> 库地区:<%=whAreaSelection %></div><hr width="95%" align="left">
<%}%>
<form action="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=addWarehousingAbnormal" method="post">
<font size="3" color="red">异常入库单号：<%if(anormalBean != null){%><%=anormalBean.getCode() %>&nbsp;&nbsp;
	状态：<%=WarehousingAbnormalBean.statusMap.get(anormalBean.getStatus())%><%} %></font>
<br>
<br>
<% if(vpList!=null &&vpList.size()>0 && apBean != null){%>
<DIV style="font-size:16px;font-weight:bold;left:200px; BORDER-RIGHT: #787878 2px double ; BORDER-TOP: #787878 2px double; BORDER-LEFT: #787878 2px double; BORDER-BOTTOM: #787878 2px double;height: auto ;width: 95%">
<br>
<table>
	<tr>
		<td colspan="2" width="2000px" align="center">
			<table  border="1"  width="97%"  cellspacing="0" >
				<tr style="height: 40px ;font-size:16px;font-weight:bold;" align="center">
					<td width="10%">订单号</td><td width="25%"><%=apBean.getOrderCode() %></td><td width="10%">包裹单号</td><td width="25%"><%=apBean.getPackageCode() %></td><td width="10%">快递公司</td><td width="20%"><%=voOrder.deliverMapAll.get(apBean.getDeliver()+"") %></td></tr>
			</table></td></tr>
	<tr><td>&nbsp;&nbsp;</td><td>&nbsp;&nbsp;</td></tr>
	<tr>
		<td width="50%">
			<table border="1" id="table_hide"  width="92%" align="center"  cellspacing="0" bgcolor="FFFFE0">
				<tr bgcolor="#00ccff" align="center">
					<td>订单中商品</td><td>原名称</td><td>商品名称</td><td>数量</td></tr>
				<% for(int i=0;i<vpList.size();i++){ 
						voProduct vpBean = vpList.get(i);
				%>
				<tr id="tr_<%=i+1%>" align="center">
					<td><%=vpBean.getCode() %></td>
					<td><%=vpBean.getOriname() %></td>
					<td><%=vpBean.getName() %></td>
					<td><%=vpBean.getCount() %></td>
				</tr>
				<%} %>
			</table></td>
		<td  width="50%"><div id="real_td">
			<table border="1"   width="95%" align="center"  cellspacing="0" bgcolor="FFFFE0">
			<tr bgcolor="#00ccff" align="center">
				<td>实际退回商品</td><td>原名称</td><td>商品名称</td><td>数量</td></tr>
			<tr align="center">
			<% if(rpList!= null && rpList.size()>0){for (voProduct bean:rpList) { %>
			<tr align="center">
				<td><%=bean.getCode() %></td>
				<td><%=bean.getOriname() %></td>
				<td><%=bean.getName() %></td>
				<td><%=bean.getCount()%></td>
			<tr align="center">
			<%} }%>
		</table>
			</div></td>
	</tr>
</table>
<br>
</DIV>
<br>
<div align="center" style="width: 980px">
	<input type="button" id="return" value="返回" style="width: 70px;height: 25px ;font-size:14px;font-weight:bold;">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<input type="button" id="print" value="打印" style="width: 70px;height: 25px ;font-size:14px;font-weight:bold;">
</div>
<%}%>
</form>
</body>
</html>
