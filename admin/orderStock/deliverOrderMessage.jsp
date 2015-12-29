<%@page import="java.util.Iterator"%>
<%@page import="java.util.Map"%>
<%@page import="adultadmin.action.vo.voOrder"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
Map deliverMap = voOrder.deliverMapAll;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查询退货包裹</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="javascript" type="text/javascript" src=<%=request.getContextPath()+"/js/My97DatePicker/WdatePicker.js"%>></script>
<script type="text/javascript">
function check(){
	var startDate = document.getElementById("startDate").value;
	var endDate = document.getElementById("endDate").value;
	if(startDate==""||endDate==""){
		alert("起始日期和结束日期必须填！");
    	return false;
	}
	var reg = new RegExp("-","g");
	var startDay = new Date(startDate.replace(reg,'/'));
	var endDay = new Date(endDate.replace(reg,'/'));
	var nDifTime = endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		alert("起始日期不能大于结束日期！");
    	return false;
	}
}
</script>
</head>
<body>
&nbsp;&nbsp;&nbsp;物流在途数据导出
<form action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=deliverOrderMessage"%> method="post" onsubmit="return check();">
	发货起始日期：<input type="text" id='startDate' name='startDate' onclick="WdatePicker();" readonly="readonly"/>&nbsp;&nbsp;
	发货结束日期：<input type="text" id='endDate' name='endDate' onclick="WdatePicker();" readonly="readonly"/>
	订单支付方式：
	<select name='buyMode'>
		<option value='-1'>全部</option>
		<option value='1'>在线支付</option>
		<option value='0'>货到付款</option>
		<option value='2'>银行汇款</option>
		<option value='3'>售后换货</option>
	</select>&nbsp;&nbsp;
	订单状态：
	<select name='status'>
		<option value='-1'>全部</option>
		<option value='6'>已发货</option>
		<option value='14'>已妥投</option>
		<option value='11'>已退回</option>
	</select>&nbsp;&nbsp;
	发货地区：
	<select name='area'>
		<option value='-1'>全部</option>
		<option value='3'>增城</option>
		<option value='4'>无锡</option>
	</select>
	物流公司：
	<select name='deliver'>
		<%
		if( deliverMap != null ) {
			Iterator itr = deliverMap.keySet().iterator();
			for( ;itr.hasNext(); ) {
				String key = (String)itr.next();%>
				<option value="<%= key%>"><%= (String)deliverMap.get(key)%></option>
			<%
			}
		}%>
	</select>
	<input type='submit' value="导出"/>
</form>
</body>
</html>