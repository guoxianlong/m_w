<%@ page language="java" import="java.util.*,adultadmin.bean.stock.*,adultadmin.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%
	List list = (ArrayList)request.getAttribute("exchangeToRecieveList");
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean flag = true;
	if(!group.isFlag(614)){
		flag = false;
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'returnOrderInfo.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<META NAME="save" CONTENT="history"> 
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<STYLE>
		.saveHistory {behavior:url(#default#savehistory);}
	</STYLE>

	<META content="MSHTML 6.00.2800.1528" name=GENERATOR>
	
	<script type="text/javascript">
	<%if(!flag){%>
	alert("您没有操作该功能的权限！");
	window.location.href='<%=request.getContextPath()%>/login.do';
	<% }%>
		function focusExchangeCode(){
	var exchangeCode=trim(document.getElementById("exchangeCode").value);
	if(exchangeCode=="调拨单条码"){
		document.getElementById("exchangeCode").value="";
		document.getElementById("exchangeCode").style.color="#000000";
	}
}
function blurExchangeCode(){
	var exchangeCode=trim(document.getElementById("exchangeCode").value);
	if(exchangeCode==""){
		document.getElementById("exchangeCode").value="调拨单条码";
		document.getElementById("exchangeCode").style.color="#cccccc";
	}
}
function focusProductCode(){
	var productCode=trim(document.getElementById("productCode").value);
	if(productCode=="商品条码"){
		document.getElementById("productCode").value="";
		document.getElementById("productCode").style.color="#000000";
	}
}
function blurProductCode(){
	var productCode=trim(document.getElementById("productCode").value);
	if(productCode==""){
		document.getElementById("productCode").value="商品条码";
		document.getElementById("productCode").style.color="#cccccc";
	} else {
		document.getElementById("productCode").style.color="#000000";
	}
}
function check(){
	document.getElementById("exchangeCode2").value=document.getElementById("exchangeCode").value;
	var exchangeCode=trim(document.getElementById("exchangeCode2").value);
	var productCode=trim(document.getElementById("productCode").value);
	if(exchangeCode==""||exchangeCode=="调拨单条码"){
		alert("必须输入调拨单条码！");
		return false;
	}
	if(productCode==""||productCode=="商品条码"){
		alert("必须输入商品条码！");
		return false;
	}
	document.getElementById("saveHistoryTA").value=productCode;
	document.getElementById('submitConfirm').disabled='true';
	return true;
}
function submitExchangeCode(){
	document.getElementById("exchangeCode2").value=document.getElementById("exchangeCode").value;
	document.getElementById("productCode").focus();
	return false;
}
function resetForm(){
	document.getElementById("exchangeCode").value="";
	document.getElementById("exchangeCode2").value="";
	document.getElementById("productCode").value="";
	document.getElementById("exchangeCode").focus();
}
function recoverHistory() {
	var sh = trim(document.getElementById("saveHistoryTA").value);
	if(sh != "") {
		document.getElementById("productCode").value=trim(document.getElementById("saveHistoryTA").value);
		document.getElementById("productCode").style.color="#000000";
	}
}
		
	</script>

  </head>
  
  <body onload="document.getElementById('exchangeCode').focus();document.getElementById('productCode').value='商品条码';recoverHistory();document.getElementById('submitConfirm').enabled='true';">
  	<div style="margin-left:15px;margin-top:15px;">
   	<fieldset style="width:500px;">
   		<div style="background-color:#FFFF93;width:360px;height:360px;border-style:solid;border-color:#000000;border-width:1px;">
   		<div style="margin-left:12px;">
   			<br>
   			<h2>售后退货商品入库:</h2>
   			<form action="" method="post" onsubmit="return submitExchangeCode();" >
   			<table border="0" cellspacing="12">
   			<tr>
   				<td>调拨单号：</td>
   				<td><input type="text" id="exchangeCode" name="exchangeCode" style="behavior:url(#default#savehistory);"  size="23" value="调拨单条码" onfocus="focusExchangeCode();" onblur="blurExchangeCode();" /></td>
   			</tr>
   			</form>
   			<form action="returnStorageAction.do?method=returnExchangeCheckIn" method="post" onsubmit="return check();">
   			<input type="hidden" id="exchangeCode2" name="exchangeCode2" />
   			<tr>
   				<td valign="top">产品编号：</td>
   				<td><textarea class="saveHistory" id="productCode" name="productCode" rows="6" value="商品条码" onfocus="focusProductCode();" onblur="blurProductCode();" style="color: #cccccc;"></textarea></td>
   				<input type="hidden" class="saveHistory" name="saveHistoryTA" id="saveHistoryTA" value=""/>
   			</tr>
   			
   			<tr>
   				<td></td>
   				<td><input type="submit" id="submitConfirm" value="确认入库"> <input type="button" value="  取消  " onclick="resetForm();"/></td>
   			</tr>
   			</form>
   			</table>
   			
   			</div>
   		</div>
   		<br>
   		<div style="margin-left:12px;">
   		操作说明：<br>
   		请依次扫描调拨单条码和商品条码，将调拨单中商品入退货库
   		</div>
   	</fieldset>
   	
   	<center><h2>未完成调拨单列表</h2></h2></center>
   	目前未完成调拨单(售后库->退货库)&nbsp; 数量：<%= list.size() %><br>
   	<table  width="82%" border="0" cellspacing="1" cellpadding="0" bgcolor="#000000">
	<tr bgcolor="#00ccff">
		<td align="center">序号</td>
		<td align="center">调拨单号</td>
		<td align="center">源库</td>
		<td align="center">目的库</td>
		<td align="center">出库操作</td>
		<td align="center">出库审核</td>
		<td align="center">状态</td>
		<td align="center">紧急程度</td>
		<td align="center">创建时间</td>
	</tr>
	<% 
	if( list != null ) {
		for( int i = 0 ; i < list.size(); i ++ ) 
		{
			StockExchangeBean seb = (StockExchangeBean)list.get(i);
	%>
	<tr bgcolor="#ffffff">
		<td align="center"><%= i + 1 %></td>
		<td align="center"><%= seb.getCode() %></td>
		<td align="center"><%= ProductStockBean.areaMap.get(seb.getStockOutArea()) %></td>
		<td align="center"><%= ProductStockBean.areaMap.get(seb.getStockInArea()) %></td>
		<td align="center"><%= seb.getStockOutOperName()%></td>
		<td align="center"><%= seb.getAuditingUserName()%></td>
		<td align="center"><%= seb.getStatusName()%></td>
		<td align="center"><%= seb.getPriorStatusName()%></td>
		<td align="center"><%= StringUtil.convertNull(StringUtil.cutString(seb.getCreateDatetime(), 0, 16))%></td>
	</tr>
	<%
		}
	}
	%>
</table>
   	</div>
  </body>
</html>
