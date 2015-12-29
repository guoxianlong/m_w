<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stat.*, adultadmin.bean.stat.*, java.util.*, adultadmin.util.*, adultadmin.util.db.*" %>
<%
String curDate = request.getParameter("date");
StatAction action = new StatAction();
action.orderHourStat(request, response);

Hashtable currentHt = (Hashtable) request.getAttribute("currentHt");
Hashtable prevHt = (Hashtable) request.getAttribute("prevHt");

OrderHourStatBean bean = null;
int count = 10;

DbOperation dbOp = new DbOperation();
dbOp.init();
%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../../header.jsp"%>
<p align="left">
规则说明：<br/>
以当天之前、同时段、非奇点、手机号+-20%之间、最邻近的七个点作为参考。最小值为七个点的最小值，最大值为七个点的最大值。<br/>
特别注意：此规则不影响奇点的设置。<br/>
说明：<br/>
每项三行依次为：订单贡献率/订单数/手机号数。<br/>
订单贡献率单位：千分。<br/>
红色：低于最低值；蓝色：高于最高值；绿色：正常。<br/>
紫色：畸点。点击订单贡献率可将某天某小时设为畸点。<br/>
</p>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='yellow'>
	    <td align=center>小时</td>
<%
String date = null;
int i = 0;
int totalCols = count + 4;
ArrayList refList = null;
OrderHourStatBean bean1 = null;
for(i = count; i >= 1; i --){
	date = DateUtil.formatDate(DateUtil.rollDate(DateUtil.parseDate(curDate, "yyyy-MM-dd"), -i));
%>
		<td align=center><%=date.substring(5, 10)%></td>
<%
}
%>
		<td align=center><%=curDate.substring(5, 10)%></td>
		<td align=center>最小值</td>
		<td align=center>最大值</td>
	</tr>
<%
for(int hour = 0; hour < 24; hour ++){	
%>
	<tr bgcolor='#F8F8F8'>
	   <td align=center><%=hour%></td>
<%
	for(i = count; i >= 1; i --){
	    date = DateUtil.formatDate(DateUtil.rollDate(DateUtil.parseDate(curDate, "yyyy-MM-dd"), -i));
		bean = (OrderHourStatBean) prevHt.get(date + " " + hour);
%>
		<td align=center><%if(bean != null){%><a href="orderHourStatSetFlag.jsp?id=<%=bean.getId()%>&date=<%=curDate%>" title="订单数:<%=bean.getOrderCount()%> 手机号数:<%=bean.getMobileCount()%> 人次数:<%=bean.getSessionCount()%> PV:<%=bean.getPv()%>" onclick="return confirm('确认设置畸点？')"><%if(bean.getFlag() == 1){%><font color="purple"><%}%><%=StringUtil.formatFloat(bean.getRate() * 1000)%><%if(bean.getFlag() == 1){%></font><%}%></a><br/><%=bean.getOrderCount()%>(<%=bean.getTotalOrderCount(dbOp)%>)<br/><%=bean.getMobileCount()%><%}else{%>&nbsp;<%}%></td>
<%
    }
    bean = (OrderHourStatBean) currentHt.get(curDate + " " + hour);
	String color = "green";
	if(bean != null){	
	    if(bean.getRate() < bean.getMinRate()){
		    color = "red";
	    }
	    else if(bean.getRate() > bean.getMaxRate()){
		    color = "blue";
	    }
	}
%>
        <td align=center><%if(bean != null){%><a href="orderHourStatSetFlag.jsp?id=<%=bean.getId()%>&date=<%=curDate%>" title="订单数:<%=bean.getOrderCount()%> 手机号数:<%=bean.getMobileCount()%> 人次数:<%=bean.getSessionCount()%> PV:<%=bean.getPv()%>" onclick="return confirm('确认设置畸点？')"><font color="<%=color%>"><%=StringUtil.formatFloat(bean.getRate() * 1000)%></font></a><br/><%=bean.getOrderCount()%>(<%=bean.getTotalOrderCount(dbOp)%>)<br/><%=bean.getMobileCount()%><%}else{%>&nbsp;<%}%></td>
		<td align=center><%if(bean != null){%><%=StringUtil.formatFloat(bean.getMinRate() * 1000)%><%}else{%>&nbsp;<%}%></td>
		<td align=center><%if(bean != null){%><%=StringUtil.formatFloat(bean.getMaxRate() * 1000)%><%}else{%>&nbsp;<%}%></td>
	</tr>
	<%
	if(bean != null){
	    refList = bean.getRefList(); 
		int j1, count1;
		count1 = refList.size();
%>
    <tr bgcolor='#B5CEF4'>
	<td colspan="<%=(totalCols - count1 - 1)%>">&nbsp;</td>
	<td><font color="black">参考日期：</font></td>
<%
	    for(j1 = count1 - 1; j1 >= 0; j1 --){
	        bean1 = (OrderHourStatBean) refList.get(j1);
	%>
	<td align=center><font color="black"><%if(bean1 != null){%><%=bean1.getLogDate().substring(5, 10)%><br/><a href="orderHourStatSetFlag.jsp?id=<%=bean1.getId()%>&date=<%=curDate%>" title="订单数:<%=bean1.getOrderCount()%> 手机号数:<%=bean1.getMobileCount()%> 人次数:<%=bean1.getSessionCount()%> PV:<%=bean1.getPv()%>" onclick="return confirm('确认设置畸点？')"><%if(bean1.getFlag() == 1){%><font color="purple"><%}%><%=StringUtil.formatFloat(bean1.getRate() * 1000)%><%if(bean1.getFlag() == 1){%></font><%}%></a><br/><%=bean1.getOrderCount()%>(<%=bean1.getTotalOrderCount(dbOp)%>)<br/><%=bean1.getMobileCount()%><%}else{%>&nbsp;<%}%></font></td>
	<%
        }
%>    
	</tr>
<%
	}
	%>
<%
}

dbOp.release();
%>

</table>
</body>
</html>