<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%
List list = (ArrayList) request.getAttribute("returnPackageList");
String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
String packageCode = StringUtil.convertNull(request.getParameter("packageCode"));
String receiveTime = StringUtil.convertNull(request.getParameter("receiveTime"));
int deliver = ProductWarePropertyService.toInt(request.getParameter("deliver"));
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>查询退货包裹</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script>
	function exportPackage(){
		document.getElementById('packageform').action="<%=request.getContextPath()%>/admin/returnedPackageAction.do?method=exportPackage";
		document.getElementById('packageform').submit();
	}
	function queryPackage(){
		document.getElementById('packageform').action="<%=request.getContextPath()%>/admin/returnedPackageAction.do?method=queryPackage";
		document.getElementById('packageform').submit();
	}
</script>
</head>
<body>
<div align="center">
<h2>退货包裹列表</h2>
<div style="margin-left:79%;">共有（）条记录</div>
<div style="margin-top:5px;border-style:solid;border-color:#000000;border-width:1px;width:90%;">
		<form action="<%=request.getContextPath()%>/admin/returnedPackageAction.do?method=queryPackage" method="post" style="text-align:left;" id="packageform">
			<h3>导出退货包裹列表:</h3>
		<table width="90%">
	
		<tr><td align="left">
	订单状态：
	<select>
			<option>11111</option>
	</select>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	订单号：
	<input type="text" size="13" name="orderCode" id="orderCode" value="<%= orderCode%>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		
	包裹单号：
		<input type="text" size="13" name="packageCode" id="packageCode" value="<%= packageCode%>" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	</tr>
	<tr>
	<td>
	理赔单状态：
		<select>
			<option>11111</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;
	快递公司：
		<select name="deliver" >
			<%
				Iterator itr = voOrder.deliverMapAll.keySet().iterator();
				for( ; itr.hasNext() ; ) {
					String key = (String) itr.next();
			%>
			<option value="<%= key%>" <%= deliver == Integer.parseInt(key) ? "selected" : ""%>>
			<%= voOrder.deliverMapAll.get(""+key)%>
			</option>
			<%
				}
			%>
		</select>
	入库日期：
		<input type="text" name="receiveTime" id="receiveTime" value="<%= receiveTime%>" onclick="WdatePicker();"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		</td></tr>
		<tr><td align="right">
		<button>查询</button><button>导出包裹单列表</button>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</td></tr>
		</table>
		</form>
</div>
</div>
<br/>
<br/>
		<table align="center" width="95%" border="0" cellspacing="1px" bgcolor="#D8D8D5" cellpadding="1px" >
		<tbody>
			<tr Bgcolor="#006030">
				<td align="center"><font color="#FFFFFF">序号</font></td>
				<td align="center"><font color="#FFFFFF">订单编号</font></td>
				<td align="center"><font color="#FFFFFF">包裹单号</font></td>
				<td align="center"><font color="#FFFFFF">快递公司</font></td>
				<td align="center"><font color="#FFFFFF">操作人</font></td>
				<td align="center"><font color="#FFFFFF">订单状态</font></td>
				<td align="center"><font color="#FFFFFF">入库时间</font></td>
				<td align="center"><font color="#FFFFFF">入库状态</font></td>
				<td align="center"><font color="#FFFFFF">异常备注</font></td>
				<td align="center"><font color="#FFFFFF">理赔单</font></td>
				<td align="center"><font color="#FFFFFF">理赔状态</font></td>
				<td align="center"><font color="#FFFFFF">退回原因</font></td>
			</tr>
			<% if( list != null && list.size() > 0) {
				for(int i = 0; i < list.size(); i++ ) {
			
			%>
			<tr bgcolor="<%= i%2 == 0 ? "#EEE9D9" : "#FFFFFF"%>">
				<td align="center">$count</td>
				<td align="center"><a href="order.do?id=$package.orderId">$package.orderCode</a></td>
				<td align="center">$package.packageCode</td>
				<td align="center">$!{deliverMap.get("$package.deliver")}</td>
				<td align="center">$!{package.operatorName}</td>
				<td align="center">订单状态</td>
				<td align="center">$package.storageTime</td>
				#if($package.storageStatus==0)
				<td align="center">正常入库</td>
				#elseif($package.storageStatus==1)
				<td align="center">异常入库（商品缺失）</td>
				#else
				<td align="center">异常入库（订单号与包裹单号不匹配）</td>
				#end
				<td align="center">$!{package.remark}</td>
				<td align="center">理赔单</td>
				<td align="center">理赔状态</td>
				<td align="center">退回原因</td>
			</tr>
			<%
				}
				} else {
			%>
			<tr bgcolor="#FFFFFF" >
				<td align="center" colspan="12">
					没有退货包裹记录或没有符合查询条件的记录
				</td>
			</tr>
			<%}%>
		</table>
</body>
</html>