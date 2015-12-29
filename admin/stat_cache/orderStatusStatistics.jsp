<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="java.util.*" %>
<%@ page import="mmb.order.satusStatistics.*" %>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

String startTime="",endTime="",searchTime="",tip="";
if(request.getAttribute("startTime")!=null){
	startTime=request.getAttribute("startTime").toString();
}
if(request.getAttribute("endTime")!=null){
	endTime=request.getAttribute("endTime").toString();
}
if(request.getAttribute("searchTime")!=null){
	searchTime=request.getAttribute("searchTime").toString();
}
if(request.getAttribute("tip")!=null){
	tip=request.getAttribute("tip").toString();
}
List userOrderList=null;
if(request.getAttribute("userOrderList")!=null){
	userOrderList=(List)request.getAttribute("userOrderList");
}
List orderStockList=null;
if(request.getAttribute("orderStockList")!=null){
	orderStockList=(List)request.getAttribute("orderStockList");
}

%>
<html>
<title>小店后台管理 - 即时发货状态订单处理统计</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script type="text/javascript">
function trim(str){
	return str.replace(/(^\s*)|(\s*$)/g,"");
}
function submitForm(){
	var startTime=trim($("#startTime").val());
	var endTime=trim($("#endTime").val());
	if(endTime!=""&&startTime==""){
		alert("请输入开始日期！");
		return;
	}
	if(startTime!=""&&endTime==""){
		alert("请输入结束日期！");
		return;
	}
	
	var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	if(startTime!=""&&!r.test(startTime)){
		alert("开始日期错误，请输入正确的格式！如：2012-12-04");
		return;
	}
	if(endTime!=""&& !r.test(endTime)){
		alert("结束日期错误，请输入正确的格式！如：2012-12-04");
		return;
	}
	if(startTime!=""&&endTime!=""){
		var startTime1 = new Date(Date.parse(startTime.replace(/-/g,"/"))).getTime();     
		var endTime2 = new Date(Date.parse(endTime.replace(/-/g,"/"))).getTime();

		if(startTime1>endTime2){
			alert("开始日期不能大于结束日期！");
			return;
		}
	}

    $("#f1").submit();
}
</script>
<body>
<div style="margin-left:10px;">
<font style="font-weight:bold" size="4">即时发货状态订单处理统计</font>
<form id="f1" name="f1" action="<%=request.getContextPath() %>/admin/orderStatusStatistics.do" method="post" >
统计日期：<input name="startTime" id="startTime" class="Wdate" type="text" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" value="<%=startTime%>" />
至<input name="endTime" id="endTime" class="Wdate" type="text" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'})" value="<%=endTime%>" />(日期区间至多相差14天)&nbsp;&nbsp;
<input type="button" value="查询" onclick="submitForm();" id="bt1"/>
</form>
<br>
说明：表1指订单生成日期&nbsp;|&nbsp;表2指订单申请出库日期，默认查看近3天<br/><br/>
<%if(!startTime.equals("")){ %>
<font color="blue">以下数据查询时间是<%=searchTime %></font><br/>
<%} %>
表1.销售部订单处理状态统计：<br/>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
    <tr bgcolor="#4688D6" align="center">
        <td width="10%" ><font color="#FFFFFF">发货处理状态</font><br/></td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><font color="#FFFFFF"><%=DateUtil.formatDate(new Date(bean.getCreateDateTime()))+(bean.getType()==1 ? "前一周" : "") %></font></td>
    <% } } %>
    </tr>
    <tr bgcolor='#F8F8F8' align="center">
        <td>缺货未处理</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal4Num() %></td>
    <% } } %>    
    </tr>
    <tr bgcolor='#F8F8F8' align="center">
        <td>缺货电话成功</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal6Num() %></td>
    <% } } %>   
    </tr>
     <tr bgcolor='#F8F8F8' align="center">
        <td>缺货电话失败</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal5Num() %></td>
    <% } } %>         
    </tr>
     <tr bgcolor='#F8F8F8' align="center">
        <td>缺货已补货</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal7Num() %></td>
    <% } } %>         
    </tr>
     <tr bgcolor='#F8F8F8' align="center">
        <td>发货空白(仅待发货或已到款订单)</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal3Num() %></td>
    <% } } %>         
    </tr>
     <tr bgcolor='#F8F8F8' align="center">
        <td>发货未处理</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal0Num() %></td>
    <% } } %>         
    </tr>
     <tr bgcolor='#F8F8F8' align="center">
        <td>发货失败</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal1Num() %></td>
    <% } } %>         
    </tr>
     <tr bgcolor='#F8F8F8' align="center">
        <td>发货成功</td>
    <% if(userOrderList!=null){
    	 for(int i=0;i<userOrderList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)userOrderList.get(i);%>
        <td><%=bean.getStockOutDeal2Num() %></td>
    <% } } %>         
    </tr>
</table><br/><br/>
表2.物流部订单处理状态统计：<br/>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
    <tr bgcolor="#4688D6" align="center">
        <td width="10%"><font color="#FFFFFF">订单出库状态</font><br/></td>
    <% if(orderStockList!=null){
    	 for(int i=0;i<orderStockList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)orderStockList.get(i);%>
        <td><font color="#FFFFFF"><%=DateUtil.formatDate(new Date(bean.getCreateDateTime())) + (bean.getType()==1 ? "以前全部" : "")  %></font></td>
    <% } } %>
    </tr>
    <tr bgcolor='#F8F8F8' align="center">
        <td>待出货(打印发货清单以前)</td>
    <% if(orderStockList!=null){
    	 for(int i=0;i<orderStockList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)orderStockList.get(i);%>
        <td ><%=bean.getStatus1Num()%></td>
    <% } } %>           
    </tr>
    <tr bgcolor='#F8F8F8' align="center">
        <td>审核(打印发货清单,复核出库以前)</td>
    <% if(orderStockList!=null){
    	 for(int i=0;i<orderStockList.size();i++){
    		 OrderStatusStatBean bean=(OrderStatusStatBean)orderStockList.get(i);%>
        <td ><%=bean.getStatus5Num()%></td>
    <% } } %>      
    </tr>
</table>    
</div>
</body>
<%if(!tip.equals("")){ %><script>alert('<%=tip%>');</script><%} %>
</html>