<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="mmb.stock.stat.formbean.ReturnedPackageFBean,adultadmin.bean.order.*" %>
<%
List list = (ArrayList) request.getAttribute("list");
String recordNum = (String)request.getAttribute("recordNum");
String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
String packageCode = StringUtil.convertNull(request.getParameter("packageCode"));
String checkTime = StringUtil.convertNull(request.getParameter("checkTime"));
String userName = StringUtil.convertNull(request.getParameter("userName"));
int pageIndex = ProductWarePropertyService.toInt(request.getParameter("pageIndex"));
int wareArea = ProductWarePropertyService.toInt(request.getParameter("wareArea"));
int resultType = ProductWarePropertyService.toInt(request.getParameter("resultType"));
int status = ProductWarePropertyService.toInt(request.getParameter("status"));
PagingBean paging = (PagingBean)request.getAttribute("paging");
String[] storageStatus = (String[]) request.getAttribute("storageStatus");
List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
String wareAreaSelectLableAll = ProductWarePropertyService.getWeraAreaOptionsAllWithRight(request,wareArea);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查询退货包裹</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/jquery-1.7.1.js" ></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/ui/jquery-ui-1.8.17.custom.js" ></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/jquery/ui/jquery.ui.core.js" ></script>
	<link href="<%=request.getContextPath()%>/jquery/themes/ui-lightness/jquery.ui.all.css" rel="stylesheet" type="text/css" />
<script>
	function queryPackage(){
		document.getElementById('packageform').action="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=getReturnPackageCheckInfo";
		document.getElementById('packageform').submit();
	}
	
	function popADialog(id) 
		{
			//$("#name").attr("value", "go Dialog");
			$("#"+id).dialog(
					   {
					    //hide:true,    //点击关闭是隐藏,如果不加这项,关闭弹窗后再点就会出错.
					    //autoOpen:false,
					    height:450,
					    width:630,
					    modal:true, //蒙层（弹出会影响页面大小）
					    bgiframe:true,
					    draggable: true,
				        resizable: false,
					    title:'异常详情',
					    overlay: {opacity: 0.5, backgroundColor: "#484891" ,overflow:'auto'},
					    buttons:{
					     '关闭':function(){
					      //关闭当前Dialog
					      $(this).dialog("close");
					     }

					    },
					   dialogClass:'alert'
					   }
					);
		}
		function dealReturnPackageCheck(targetId) {
			if( window.confirm("确认开始处理？")) {
				window.location.href="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=dealReturnPackageCheck&pageIndex=<%= pageIndex%>&orderCode=<%= orderCode%>&packageCode=<%= packageCode%>&wareArea=<%= wareArea%>&userName=<%= userName%>&checkTime=<%= checkTime%>&resultType=<%= resultType%>&status=<%= status%>&targetId=" + targetId + "&targetStatus=1";
				return;
			} 
		}
		function auditReturnPackageCheck(targetId, targetStatus) {
			if( window.confirm("是否确认审核？")) {
				window.location.href="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=dealReturnPackageCheck&pageIndex=<%= pageIndex%>&orderCode=<%= orderCode%>&packageCode=<%= packageCode%>&wareArea=<%= wareArea%>&userName=<%= userName%>&checkTime=<%= checkTime%>&resultType=<%= resultType%>&status=<%= status%>&targetId=" + targetId + "&targetStatus=" + targetStatus;
				return;
			}
		}
		function completeReturnPackageCheck(targetId) {
			if( window.confirm("是否确认完成？")) {
				window.location.href="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=dealReturnPackageCheck&pageIndex=<%= pageIndex%>&orderCode=<%= orderCode%>&packageCode=<%= packageCode%>&wareArea=<%= wareArea%>&userName=<%= userName%>&checkTime=<%= checkTime%>&resultType=<%= resultType%>&status=<%= status%>&targetId=" + targetId + "&targetStatus=4";
				return;
			}
		}
</script>
</head>
<body style="text-align:center;">
<h3>包裹核查表</h3>
<div style="float:right">共有（<%= recordNum %>）条记录&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </div>
<div align="center" style="margin-left:3%;margin-top:40px;width:94%;">
<fieldset>
	<legend>查询栏</legend>
		<form action="<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=getReturnPackageCheckInfo" method="post" style="text-align:left;" id="packageform">
		<table width="90%">
	
		<tr><td align="left">
	
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	库地区: 
				<%= wareAreaSelectLableAll%>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	订单号：
	<input type="text" size="13" name="orderCode" id="orderCode" value="<%= orderCode%>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		
	包裹单号：
		<input type="text" size="13" name="packageCode" id="packageCode" value="<%= packageCode%>" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	核查人：
		<input type="text" size="13" name="userName" id="userName" value="<%= userName%>" />		
	</td>
	</tr>
	<tr>
	<td>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		核查时间：
		<input type="text" size="11" name="checkTime" id="checkTime" value="<%= checkTime%>" onclick="WdatePicker();"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		异常类型：
		<select name="resultType" id="resultType" >
			<option value="-1" <%= resultType == -1 ? "selected" : ""%>>全部   </option>
			<option value="1" <%= resultType == 1 ? "selected" : ""%>>缺失</option>
			<option value="2" <%= resultType == 2 ? "selected" : ""%>>多出</option>
			<option value="3" <%= resultType == 3 ? "selected" : ""%>>错件</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		异常处理状态：
		<select name="status" id="status" >
			<option value="-1" <%= status == -1 ? "selected" : ""%>>全部</option>
			<option value="0" <%= status == 0 ? "selected" : ""%>>未处理</option>
			<option value="1" <%= status == 1 ? "selected" : ""%>>处理中</option>
			<option value="2" <%= status == 2 ? "selected" : ""%>>已审核</option>
			<option value="3" <%= status == 3 ? "selected" : ""%>>审核不通过</option>
			<option value="4" <%= status == 4 ? "selected" : ""%>>已完成</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		</td></tr>
		<tr><td align="right">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<button onclick="queryPackage();">查询</button>
		</td></tr>
		</table>
		</form>
	</fieldset>
</div>
<br/>
<div style="margin-top:10px">
	<div>
		<table align="center" width="98%" border="0" cellspacing="1px" bgcolor="#D8D8D5" cellpadding="1px" >
			<tr bgcolor="#484891" >
				<td><font color="#FFFFFF">序号</font></td>
				<td><font color="#FFFFFF">包裹单号</font></td>
				<td><font color="#FFFFFF">订单号</font></td>
				<td><font color="#FFFFFF">核查人</font></td>
				<td><font color="#FFFFFF">核查时间</font></td>
				<td><font color="#FFFFFF">核查结果</font></td>
				<td><font color="#FFFFFF">异常类型</font></td>
				<td><font color="#FFFFFF">异常详情</font></td>
				<td><font color="#FFFFFF">异常处理状态</font></td>
				<td><font color="#FFFFFF">异常处理</font></td>
				<td><font color="#FFFFFF">查看日志</font></td>
			</tr>
			<%
				if( list != null && list.size() != 0 ) {
					int x = list.size();
					for( int count = 0; count < x; count++) {
						ReturnPackageCheckBean rpcBean = (ReturnPackageCheckBean) list.get(count);
			%>
			<tr bgcolor="<%= count%2 == 0 ? "#EEE9D9" : "#FFFFFF"%>" >
				<td><%= count + 1%></td>
				<td><%= rpcBean.getPackageCode()%></td>
				<td><%= rpcBean.getOrderCode()%></td>
				<td><%= StringUtil.convertNull(rpcBean.getCheckUserName())%></td>
				<td><%= StringUtil.convertNull(rpcBean.getCheckTime()).equals("") ? "" : StringUtil.convertNull(rpcBean.getCheckTime()).substring(0,19)%></td>
				<td><%= rpcBean.getCheckResultName()%></td>
				<td><%= rpcBean.getTypeName()%></td>
				<td>
				<%
					if( rpcBean.getCheckResult() == 0 ) {
				%>
				
				<%
					} else {
					%>
					<a onclick="javascript:popADialog('dialog_<%= count %>');return false;">查看</a>
				<%
					}
				%>
				</td>
				<td>
				<%
					if( rpcBean.getCheckResult() == 0 ) {
				%>
				
				<%
					} else {
					%>
					<%= rpcBean.getStatusName()%>
				<%
					}
				%>
				</td>
				<td>
				<%
					if( rpcBean.getCheckResult() == 0 ) {
				%>
				
				<%
					} else {
					%>
					<%
						if( rpcBean.getStatus() == ReturnPackageCheckBean.STATUS_UNDEAL ) {
					%> 
						<button onclick="dealReturnPackageCheck(<%= rpcBean.getId()%>);">处理</button>
					<%
						} else if (rpcBean.getStatus() == ReturnPackageCheckBean.STATUS_DEALING) { 
					%>
						<button onclick="auditReturnPackageCheck(<%= rpcBean.getId()%>, 2);">审核</button><br/>
						<button onclick="auditReturnPackageCheck(<%= rpcBean.getId()%>, 3);">审核不通过</button>
					<%
						} else if ( rpcBean.getStatus() == ReturnPackageCheckBean.STATUS_AUDIT_SUCCESS ) { 
					%>
						<button onclick="completeReturnPackageCheck(<%= rpcBean.getId()%>);">完成</button>
					<%
						} else if(rpcBean.getStatus() == 3) {
					%>
						
					<%
						} else if(rpcBean.getStatus() == 4) {
					%>
					
					<%
						}
					%>
				<%
					}
				%>
				</td>
				<td><a href="<%= request.getContextPath() %>/admin/returnStorageAction.do?method=ReturnPackageLog&orderCode=<%= rpcBean.getOrderCode()%>" target="_blank">查看日志</a></td>
			</tr>
			<%
				}
				} else {
			%>
				<tr Bgcolor="#FFFFFF">
				<td colspan="11">没有包裹核查信息或无对应当前搜索条件的搜索结果</td>
			</tr>
			<%
				}
			%>
		</table>
	</div>
	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
	
	<%
				if( list != null && list.size() != 0 ) {
					int x = list.size();
					for( int count = 0; count < x; count++) {
						ReturnPackageCheckBean rpcBean = (ReturnPackageCheckBean) list.get(count);
			%>
				
				<div id="dialog_<%= count%>" style="display:none;font-size:4px;">
				<table align='left' width='42%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
				<tr bgcolor='#0080FF' >
				<td align='center'>
				<font color="#FFFFFF">实际扫描商品</font>
				</td>
				<td align='center'>
				<font color="#FFFFFF">数量</font>
				</td>
				</tr>
				<% 
					if( rpcBean.getCheckResult() == ReturnPackageCheckBean.RESULT_ABNORMAL ) {
					int y = rpcBean.getReturnPackageCheckProductList().size();
					if( y != 0 ) {
					for (int i = 0; i < y; i ++ ) {
						ReturnPackageCheckProductBean rpcpBean = rpcBean.getReturnPackageCheckProductList().get(i);
					 %>
					<tr bgcolor="<%= i%2 == 0 ? "#EEE9D9" : "#FFFFFF"%>" >
				<td align="center">
					<%= rpcpBean.getProductCode() %>
				</td>
				<td align="center">
					<%=  rpcpBean.getCount() %>
				</td>
				</tr>
				<%
					}
					}
					}
				%>
				</table>
				
				<table align='right' width='42%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
				<tr bgcolor='#0080FF' >
				<td align='center'>
				<font color="#FFFFFF">出库单内商品</font>
				</td>
				<td align='center'>
				<font color="#FFFFFF">数量</font>
				</td>
				</tr>
				<% 
					if( rpcBean.getCheckResult() == ReturnPackageCheckBean.RESULT_ABNORMAL ) {
					int z = rpcBean.getOrderStockProductList().size();
					if( z != 0 ) {
					for (int i = 0; i < z; i ++ ) {
						OrderStockProductBean ospBean = rpcBean.getOrderStockProductList().get(i);
					 %>
					<tr bgcolor="<%= i%2 == 0 ? "#EEE9D9" : "#FFFFFF"%>" >
				<td align="center">
					<%= ospBean.getProductCode() %>
				</td>
				<td align="center">
					<%=  ospBean.getStockoutCount() %>
				</td>
				</tr>
				<%
					}
					}
					}
				%>
				</table>
				</div>
				
			<%
				}
				}
			%>
</div>
</body>
</html>