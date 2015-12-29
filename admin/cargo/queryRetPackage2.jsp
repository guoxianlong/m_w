<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="mmb.stock.stat.formbean.ReturnedPackageFBean" %>
<%
List list = (ArrayList) request.getAttribute("packageList");
String pageLine = (String) request.getAttribute("pageLine");
String recordNum = (String) request.getAttribute("recordNum");
String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
String packageCode = StringUtil.convertNull(request.getParameter("packageCode"));
String receiveTime = StringUtil.convertNull(request.getParameter("receiveTime"));
String storageStartTime = StringUtil.convertNull(request.getParameter("storageStartTime"));
String storageEndTime = StringUtil.convertNull(request.getParameter("storageEndTime"));

String checkStartTime = StringUtil.convertNull(request.getParameter("checkStartTime"));
String checkEndTime = StringUtil.convertNull(request.getParameter("checkEndTime"));
int deliver = ProductWarePropertyService.toInt(request.getParameter("deliver"));
int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
int cvStatus = ProductWarePropertyService.toInt(request.getParameter("cvStatus"));
int orderStatus = ProductWarePropertyService.toInt(request.getParameter("orderStatus"));
int returnedPackageStatus = ProductWarePropertyService.toInt(request.getParameter("returnedPackageStatus"));
PagingBean paging = (PagingBean)request.getAttribute("paging");
String[] storageStatus = (String[]) request.getAttribute("storageStatus");
Map deliverMap = (HashMap)request.getAttribute("deliverMap");
List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
String wareAreaSelectLableAll = ProductWarePropertyService.getWeraAreaOptionsAll(wareArea);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查询退货包裹</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="javascript" type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
<script>
	function exportPackage(){
		document.getElementById('packageform').action="<%= request.getContextPath()%>/admin/returnedPackageAction.do?method=exportPackage";
		document.getElementById('packageform').submit();
		$("#exportButton").attr('disabled',"true");
	}
	function queryPackage(){
		document.getElementById('packageform').action="<%= request.getContextPath()%>/admin/returnedPackageAction.do?method=queryPackage";
		document.getElementById('packageform').submit();
		$("#searchButton").attr('disabled',"true");
	}
</script>
</head>
<body style="text-align:center;">
<h3>退货包裹列表</h3>
<div style="float:right">共有（<%= recordNum %>）条记录&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </div>
<div align="center" style="margin-left:6%;margin-top:40px;border-style:solid;border-color:#000000;border-width:1px;width:88%;">
		<form action="<%=request.getContextPath()%>/admin/returnedPackageAction.do?method=queryPackage" method="post" style="text-align:left;" id="packageform" >
			<h3>导出退货包裹列表:</h3>
		<table width="90%">
	
		<tr><td align="left">
	<!--  订单状态：
	<select name="orderStatus" id="orderStatus">
			<option value="-1" <%= orderStatus == -1 ? "selected" : ""%>>请选择</option>
			<option value="13" <%= orderStatus == 13 ? "selected" : ""%>>待退回</option>
			<option value="11" <%= orderStatus == 11 ? "selected" : ""%>>已退回</option>
	</select>
	-->
	退货包裹状态：
	<select name="returnedPackageStatus" id="returnedPackageStatus" >
		<option value="-1" <%= returnedPackageStatus == -1 ? "selected" : ""%>>请选择</option>
		<option value="0" <%= returnedPackageStatus == 0 ? "selected" : ""%>>待退回</option>
		<option value="1" <%= returnedPackageStatus == 1 ? "selected" : ""%>>已退回</option>
	</select>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	订单号：
	<input type="text" size="13" name="orderCode" id="orderCode" value="<%= orderCode%>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		
	包裹单号：
		<input type="text" size="13" name="packageCode" id="packageCode" value="<%= packageCode%>" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		理赔单状态：
		<select name="cvStatus" id="cvStatus">
			<option value="-1" <%= cvStatus == -1 ? "selected" : ""%>>请选择</option>
			<option value="0" <%= cvStatus == 0 ? "selected" : ""%>>未处理</option>
			<option value="1" <%= cvStatus == 1 ? "selected" : ""%>>已提交</option>
			<option value="2" <%= cvStatus == 2 ? "selected" : ""%>>审核不通过</option>
			<option value="3" <%= cvStatus == 3 ? "selected" : ""%>>审核通过</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		快递公司：<select name="deliver">
					<%
						if( deliverMap != null ) {
							Iterator itr = deliverMap.keySet().iterator();
							for( ;itr.hasNext(); ) {
								String key = (String)itr.next();
							 	if( key.equals(String.valueOf(deliver)) ){
					%>
								<option value="<%= key %>" selected><%= (String)deliverMap.get(key)%></option>
						<%
								}else {
						%>
								<option value="<%= key%>"><%= (String)deliverMap.get(key)%></option>
					<%
								}
							}
					 	} else {
					 %>
					 	<option value="-1" >请选择物流公司</option>
					 <%
					 	}
					 %>
				</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</td>
	</tr>
	<tr>
	<td>
	
		<!-- hp  -->
		入库日期：
		<input type="text" size="11" name="storageStartTime" id="storageStartTime" value="<%= storageStartTime%>" onclick="WdatePicker();"/>
		-
		<input type="text" size="11" name="storageEndTime" id="storageEndTime" value="<%= storageEndTime%>" onclick="WdatePicker();"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<!-- hp  -->
		复核日期：
		<input type="text" size="11" name="checkStartTime" id="checkStartTime" value="<%= checkStartTime%>" onclick="WdatePicker();"/>
		-
		<input type="text" size="11" name="checkEndTime" id="checkEndTime" value="<%= checkEndTime%>" onclick="WdatePicker();"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		库地区: 
				<%= wareAreaSelectLableAll%>
		</td></tr>
		<tr><td align="right">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<button id="searchButton" onclick="queryPackage();">查询</button>
		<button id="exportButton" onclick="exportPackage();">导出包裹单列表</button>
		</td></tr>
		</table>
		</form>
</div>
<br/>
<div style="margin-top:10px">
	<div>
		<table align="center" width="98%" border="0" cellspacing="1px" bgcolor="#D8D8D5" cellpadding="1px" >
			<tr bgcolor="#484891" >
				<td><font color="#FFFFFF">序号</font></td>
				<td><font color="#FFFFFF">订单编号</font></td>
				<td><font color="#FFFFFF">包裹单号</font></td>
				<td><font color="#FFFFFF">快递公司</font></td>
				<td><font color="#FFFFFF">入库地区</font></td>
				<td><font color="#FFFFFF">操作人</font></td>
				<td><font color="#FFFFFF">退货包裹状态</font></td>
				<td><font color="#FFFFFF">导入时间</font></td>
				<td><font color="#FFFFFF">入库时间</font></td>
				<td><font color="#FFFFFF">理赔单</font></td>
				<td><font color="#FFFFFF">理赔状态</font></td>
				<td><font color="#FFFFFF">退回原因</font></td>
				<td><font color="#FFFFFF">复核时间</font></td>
			</tr>
			<%
				if( list != null && list.size() != 0 ) {
					int x = list.size();
					for( int count = 0; count < x; count++) {
						ReturnedPackageBean rpBean = (ReturnedPackageBean) list.get(count);
			%>
			<tr bgcolor="<%= count%2 == 0 ? "#EEE9D9" : "#FFFFFF"%>" >
				<td><%= count + 1%></td>
				<td><a href="order.do?id=<%= rpBean.getOrderId()%>"><%= rpBean.getOrderCode()%></a></td>
				<td><%= rpBean.getPackageCode()%></td>
				<td><%= StringUtil.convertNull((String)deliverMap.get(String.valueOf(rpBean.getDeliver())))%></td>
				<td><%= StringUtil.convertNull((String)ProductStockBean.areaMap.get(rpBean.getArea()))%></td>
				<td><%= StringUtil.convertNull(rpBean.getOperatorName())%></td>
				<td><%= StringUtil.convertNull(rpBean.getReturnedPackageStatusName())%></td>
				<td><%= StringUtil.convertNull(rpBean.getImportTime()).equals("") ? "" : StringUtil.convertNull(rpBean.getImportTime()).substring(0,19)%></td>
				<td><%= StringUtil.convertNull(rpBean.getStorageTime()).equals("") ? "" : StringUtil.convertNull(rpBean.getStorageTime()).substring(0,19)%></td>
				<td><%= rpBean.getClaimsVerificationBean() == null ? "" : "<a href='claimsVerificationAction.do?method=foreEditClaimsVerification&id=" + rpBean.getClaimsVerificationBean().getId() + "' target='_blank'>" + rpBean.getClaimsVerificationBean().getCode() + "</a>"%></td>
				<td><%= rpBean.getClaimsVerificationBean() == null ? "" : rpBean.getClaimsVerificationBean().getStatusName()%></td>
				<td><%= rpBean.getReturnsReasonBean() == null ? "" : rpBean.getReturnsReasonBean().getReason()%></td>
			    <td><%= StringUtil.convertNull(rpBean.getCheckDatetime()).equals("") ? "" : StringUtil.convertNull(rpBean.getCheckDatetime()).substring(0,19)%></td>
			</tr>
			<%
				}
				} else {
			%>
				<tr Bgcolor="#FFFFFF">
				<td colspan="12">没有退货包裹或无对应当前搜索条件的搜索结果</td>
			</tr>
			<%
				}
			%>
		</table>
	</div>
	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</div>
</body>
</html>