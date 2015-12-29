<%@page import="adultadmin.util.DateUtil,adultadmin.util.StringUtil"%>
<%@page import="adultadmin.bean.order.OrderDealRateBean"%><%@ include file="../taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*" %>
<html>
<head>
<title>成交率查询</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script type="text/javascript">
function check(){
	var startDate = document.getElementById("startDate").value;
	var endDate = document.getElementById("endDate").value;
	
	if(startDate==""&&endDate==""){
		window.alert("请输入查询日期");
		return false;
	}else{
		return true;
	}
}

function daochu(){
	var startDate = document.getElementById("startDate").value;
	var endDate = document.getElementById("endDate").value;
	var startTime = document.getElementById("startTime").value;
	var endTime = document.getElementById("endTime").value;
	var areaNo = document.getElementById("pq").value;
	if(startDate==""&&endDate==""){
		window.alert("请输入查询日期");
		return false;
	}
	
	
	document.location = "orderDealRatePrint.jsp?startDate="+startDate+"&startTime="+startTime+"&endDate="+endDate+"&endTime="+endTime+"&areaNo="+areaNo;
	
}
</script>
<%@include file="../header.jsp"%>
<table width="95%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center>
<tr>
<td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
	<tr bgcolor="#F8F8F8"><td>
<form method=post action="searchdealrate.do" onSubmit="return check();">
<%
String startDate = (String)request.getParameter("startDate");
if(startDate == null)startDate="";
String endDate = (String)request.getParameter("endDate");
if(endDate == null)endDate="";

List dealRateList = (List)request.getAttribute("dealRateList");

Calendar time = Calendar.getInstance();
time.set(Calendar.HOUR_OF_DAY,8);
time.set(Calendar.MINUTE,0);
String startTime = StringUtil.convertNull(request.getParameter("startTime"));
String endTime = StringUtil.convertNull(request.getParameter("endTime"));
String[] isOldUser = request.getParameterValues("isOldUser");
String oldUserStrs=null;
String selectStr="";
if(isOldUser!=null){
	if(isOldUser.length==1)
		oldUserStrs=isOldUser[0];
	for(int i=0;i<isOldUser.length;i++){
		selectStr+=isOldUser[i]+",";
	}
}
%>
开始时间：<input type=text name="startDate" id="startDate" size="10" value="<%=startDate%>" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');">
<select name="startTime" id="startTime">
<%
while(time.get(Calendar.HOUR_OF_DAY)>=0){
	String s=DateUtil.formatDate(time.getTime(),"HH:mm");
	if(s.equals("01:00")){s="00:59";}
 %>
 	<option value="<%=s%>"<%=s.equals(startTime)?"selected='selected'":""%> ><%=s%></option>
<%
	time.add(Calendar.MINUTE,30);
	if(s.equals("00:59"))break;
}
 %>
</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="checkbox" name="isOldUser" value="1">老用户&nbsp;&nbsp;&nbsp;
<input type="checkbox" name="isOldUser" value="2">新用户&nbsp;&nbsp;&nbsp;
<script type="text/javascript">checkboxChecked(document.getElementsByName('isOldUser'),'<%=selectStr%>')</script>
<br>
截止时间：<input type=text name="endDate" id="endDate" size="10" value="<%=endDate%>" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');">
<select name="endTime" id="endTime">
<%
time.set(Calendar.HOUR_OF_DAY,8);
time.set(Calendar.MINUTE,0);
while(time.get(Calendar.HOUR_OF_DAY)>=0){
	String s=DateUtil.formatDate(time.getTime(),"HH:mm");
	if(s.equals("01:00")){s="00:59";}
 %>
 	<option value="<%=s%>" <%=s.equals(endTime)?"selected='selected'":""%>><%=s%></option>
<%
	time.add(Calendar.MINUTE,30);
	if(s.equals("00:59"))break;
}
 %>
</select>
	<input type="hidden" name="pq" id="pq" value="<%=request.getParameter("pq") %>">
<br>
</br><input type=submit value="查询">&nbsp;&nbsp;&nbsp;<input type="button" onclick="return daochu();" value="导出列表">
</form>
</td>
</tr>
</table>
</td>
</tr>
<%if(dealRateList!=null){ %>
<tr>
	<td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
				<tr bgcolor="#4688D6">
					<td align="center"><font color="#FFFFFF">时间</font></td>
					<td align="center"><font color="#FFFFFF">总成交率</font></td>
					<td align="center"><font color="#FFFFFF">成人</font></td>
					<td align="center"><font color="#FFFFFF">手机</font></td>
					<td align="center"><font color="#FFFFFF">数码</font></td>
					<td align="center"><font color="#FFFFFF">电脑</font></td>
					<td align="center"><font color="#FFFFFF">服装</font></td>
					<td align="center"><font color="#FFFFFF">鞋子</font></td>
					<td align="center"><font color="#FFFFFF">护肤品</font></td>
					<td align="center"><font color="#FFFFFF">其他</font></td>
					<td align="center"><font color="#FFFFFF">小家电</font></td>
					<td align="center"><font color="#FFFFFF">饰品</font></td>
					<td align="center"><font color="#FFFFFF">行货手机订单</font></td>
					<td align="center"><font color="#FFFFFF">包</font></td>
<!--					<td align="center"><font color="#FFFFFF">保健品</font></td>-->
					<td align="center"><font color="#FFFFFF">手表</font></td>
<!--					<td align="center"><font color="#FFFFFF">配饰</font></td>-->
					<td align="center"><font color="#FFFFFF">新奇特</font></td>
					<td align="center"><font color="#FFFFFF">内衣</font></td>
				</tr>
				<%						
				//内衣——3,手机——1,数码——2,电脑——5,服装——4,鞋子——6,护肤品——7,  其他——9  小家电-8 行货手机订单-10 饰品-11 包-12
				//保健品 13  手表-14 	配饰-15	新奇特-16		成人-17
					Iterator iter = dealRateList.listIterator();
					while(iter.hasNext()){
						OrderDealRateBean bean = (OrderDealRateBean)iter.next();
				 %>
				 <tr>
				 	<td align="center"><%=bean.getStatisticDatetime().substring(0,16)%></td>
				 	<td align="center"><%=bean.getTotalDealRate() %></td>
				 	<td align="center"><%=bean.getAdultDealRate() %></td>
				 	<td align="center"><%=bean.getPhoneDealRate() %></td>
				 	<td align="center"><%=bean.getDigitalDealRate() %></td>
				 	<td align="center"><%=bean.getComputerDealRate() %></td>
				 	<td align="center"><%=bean.getDressDealRate() %></td>
				 	<td align="center"><%=bean.getShoeDealRate() %></td>
				 	<td align="center"><%=bean.getSkincareDealRate() %></td>
				 	<td align="center"><%=bean.getOtherDealRate() %></td>
				 	<td align="center"><%=bean.getAppliances() %></td>
				 	<td align="center"><%=bean.getAccessories() %></td>
				 	<td align="center"><%=bean.getLicensedPhone() %></td>
				 	<td align="center"><%=bean.getBagDealRate() %></td>
<!--				 	<td align="center"><%=bean.getHealthDealRate() %></td>-->
				 	<td align="center"><%=bean.getWatchDealRate() %></td>
<!--				 	<td align="center"><%=bean.getPeishiDealRate() %></td>-->
				 	<td align="center"><%=bean.getXinqite() %></td>
				 	<td align="center"><%=bean.getUnderWareDealRate() %></td>
				 </tr>
				 <% } %>
			</table>
	</td>
	</tr>
<%} %>
</table>
          <br>
          
          
          <br>   
</body>
</html>