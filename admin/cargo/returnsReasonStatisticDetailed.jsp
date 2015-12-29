<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%> 
<%@ page import="adultadmin.bean.cargo.ReturnsReasonBean"%>
<%@ page import="mmb.stock.stat.ReturnedPackageBean"%>
<%@ page import="adultadmin.util.Encoder"%>

<%@page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>销售退货原因统计明细</title>
<style type="text/css">
<!--
.STYLE2 {color: #0099FF; font-weight: bold;
.STYLE3 {color: #00FF00}
.STYLE4 {color: #009933}
-->
</style>
</head>
<%
PagingBean paging = (PagingBean) request.getAttribute("paging");
String url = StringUtil.convertNull((String)request.getAttribute("url"));
List reasonList = (List)request.getAttribute("list");
List rpList = (List)request.getAttribute("rpList");
String orderCode = StringUtil.convertNull((String)request.getAttribute("orderCode"));
String packageCode = StringUtil.convertNull((String)request.getAttribute("packageCode"));
String startTime = StringUtil.convertNull((String)request.getAttribute("startTime"));
String endTime = StringUtil.convertNull((String)request.getAttribute("endTime"));
String reasonId = StringUtil.convertNull((String)request.getAttribute("reasonId"));
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/adult-admin/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript">
function submitForm(flag){
	var orderCode = $("#orderCode").val();
	var packageCode = $("#packageCode").val();
	var startTime = $("#startTime").val();
	var endTime = $("#endTime").val();
	var reasonId = $("#reasonId").val();
	
	var nDay_ms = 24*60*60*1000;
	var reg = new RegExp("-","g");
	var startDay = new Date(startTime.replace(reg,'/'));
	var endDay = new Date(endTime.replace(reg,'/'));
	var nDifTime = endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		alert("起始日期不能大于结束日期！");
    	return false;
	}
	if(flag == "query"){
		window.location='<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatisticDetail&flag=query&orderCode='+ orderCode +'&packageCode='+packageCode+'&startTime='+startTime+'&endTime='+endTime+'&reasonId='+reasonId;
	}else{
		window.location='<%=request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonStatisticDetail&flag=export&orderCode='+ orderCode +'&packageCode='+packageCode+'&startTime='+startTime+'&endTime='+endTime+'&reasonId='+reasonId;
	}
}
</script>
<body bgcolor="#FFFF99">
<form action="" method="post">
<table  width="99%" border="1" cellspacing="0" bgcolor="#FF6633">
	<tr>
		<td>订单编号：<input type="text" id="orderCode" name="ordreCode" value="<%=orderCode  %>"></td>
		<td>入库时间(起)：<input type=text id="startTime"name="startTime"  value="<%=startTime  %>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){this.blur();}});"/></td>
		<td>退货原因：
			<select id="reasonId" name="reasonId">
			<% if(reasonList != null){ %>
					<option value="">全部</option>
				<%for (int i = 0; i < reasonList.size(); i++) {
					ReturnsReasonBean bean = (ReturnsReasonBean) reasonList.get(i);
					if((bean.getId()+"").equals(reasonId)){%>
					<option selected value="<%=bean.getId()  %>"><%=bean.getReason()  %></option>
				<% }else{%>
					<option value="<%=bean.getId()  %>"><%=bean.getReason()  %></option>
				<% } 
				   }
				}%>
			</select></td>
	</tr>
	<tr>
		<td>包裹单号：<input type="text" id="packageCode" name="packageCode" value="<%=packageCode  %>"></td>
		<td>入库时间(末)：<input type=text id="endTime"name="endTime"  value="<%=endTime  %>" onClick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){this.blur();}});"/></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="center"><input type="button" value="查询" onclick="submitForm('query')">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="导出excel" onclick="submitForm('export')"></td>
	</tr>
</table></form>
<br>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
	<tr bgcolor="#00ccff">
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">订单编号</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">包裹单号</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">快递公司</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">入库时间</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">入库状态</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">退货原因</font></span></div></td>
  	</tr >
  	<% if(rpList!=null){ 
  			for(int i=0;i<rpList.size();i++){
  				ReturnedPackageBean rpBean = (ReturnedPackageBean)rpList.get(i);
  	%>
  	<tr>
  		<td align="center"><font color="#000000"><% if(paging!=null){ %><%=paging.getCountPerPage()*(paging.getCurrentPageIndex())+i+1%><% }else{ %>i+1<% } %></font></td>
  		<td align="center"><font color="#000000"><%= rpBean.getOrderCode() %></font></td>
  		<td align="center"><font color="#000000"><% if(!"".equals(rpBean.getPackageCode())){ %><%= rpBean.getPackageCode() %><% }else{ %>未知<% } %></font></td>
  		<td align="center"><font color="#000000"><% if(voOrder.deliverMapAll.get(rpBean.getDeliver() + "")!=null){ %><%=voOrder.deliverGdMap.get(rpBean.getDeliver() + "")%><% }else{ %>未知<% } %></font></td>
  		<td align="center"><font color="#000000"><%= rpBean.getStorageTime().substring(0,19) %></font></td>
  		<td align="center"><%if(rpBean.getStorageStatus()==0){  %><font color="green">正常入库</font><% }if(rpBean.getStorageStatus()==1){ %><font color="red">商品缺失</font><% }if(rpBean.getStorageStatus()==2){ %><font color="#CC3300">订单和包裹不匹配</font><% } %></td>
  		<td align="center"><font color="#000000"><% if(rpBean.getReasonName()!=null){ %><%= rpBean.getReasonName() %><% }else{ %>未知<% } %></font></td>
  	</tr>
  			<% }
  		}		
  	 %>
</table>
<%if (paging!=null){%>
<p align="center"><font color="#000000"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></font></p>
<%} %>
</form>
</body>
</html>