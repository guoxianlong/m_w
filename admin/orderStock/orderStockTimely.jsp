<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
int searchType=0;
if(request.getParameter("searchType")!=null){
	searchType=Integer.parseInt(request.getParameter("searchType"));
}
int pageType=0;
if(request.getParameter("pageType")!=null){
	pageType=Integer.parseInt(request.getParameter("pageType"));
}
String date=DateUtil.getNowDateStr();
if(request.getParameter("date")!=null){
	date=request.getParameter("date");
}
String orderType="-1";
if(request.getParameter("orderType")!=null){
	orderType=request.getParameter("orderType");
}
String orderCode="";
if(request.getParameter("orderCode")!=null){
	orderCode=request.getParameter("orderCode");
}
String[][] dateList=(String[][])request.getAttribute("dateList");
%>

<%@page import="adultadmin.util.DateUtil"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.order.OrderStockBean"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="mmb.stock.stat.OrderStockTimelyBean"%><html>
<head>
<title>发货成功率统计</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/dark-blue.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
</head>
<body bgcolor="ffcc00">
<%if(pageType==1){ %>
<span>发货成功率统计</span>
<span><a href="<%=request.getContextPath() %>/admin/orderStockTimely.do?method=orderStockTimely&searchType=1&pageType=2">发货及时率统计</a></span>
<%}else if(pageType==2){ %>
<span><a href="<%=request.getContextPath() %>/admin/orderStockTimely.do?method=orderStockTimely2&searchType=1&pageType=1">发货成功率统计</a></span>
<span>发货及时率统计</span>
<%} %>
<%if(pageType==1){ %>
<div id="chenggonglv">
<form action="<%=request.getContextPath() %>/admin/orderStockTimely.do?method=orderStockTimely2" method="post">
	统计周期：
	<select name="searchType">
		<option value="1" <%if(searchType==1){ %>selected="selected"<%} %>>按天</option>
		<option value="2" <%if(searchType==2){ %>selected="selected"<%} %>>按周</option>
		<option value="3" <%if(searchType==3){ %>selected="selected"<%} %>>按月</option>
	</select>
	<input type="hidden" name="pageType" value="<%=pageType %>" />
	<input type="submit" value="查询" />
</form>

<script type="text/javascript">
var chart;
$(document).ready(function() {
	chart = new Highcharts.Chart({

		chart: {
			renderTo: 'container',
			defaultSeriesType: 'column'
		},

		title: {
			text: '点击柱状图查看当日作业列表'
		},

		xAxis: {
			title: {
				text: '时间'
			},
			categories: ['<%=dateList[0][0].substring(5,10)%>', 
			 			'<%=dateList[1][0].substring(5,10)%>', 
			 			'<%=dateList[2][0].substring(5,10)%>', 
			 			'<%=dateList[3][0].substring(5,10)%>', 
			 			'<%=dateList[4][0].substring(5,10)%>',
			 			'<%=dateList[5][0].substring(5,10)%>',
			 			'<%=dateList[6][0].substring(5,10)%>',
			 			'<%=dateList[7][0].substring(5,10)%>',
			 			'<%=dateList[8][0].substring(5,10)%>',
			 			'<%=dateList[9][0].substring(5,10)%>',
			 			'<%=dateList[10][0].substring(5,10)%>',
			 			'<%=dateList[11][0].substring(5,10)%>',
			 			'<%=dateList[12][0].substring(5,10)%>',
			 			'<%=dateList[13][0].substring(5,10)%>',
			 			'<%=dateList[14][0].substring(5,10)%>',
			 			'<%=dateList[15][0].substring(5,10)%>',
			 			'<%=dateList[16][0].substring(5,10)%>',
			 			'<%=dateList[17][0].substring(5,10)%>',
			 			'<%=dateList[18][0].substring(5,10)%>',
			 			'<%=dateList[19][0].substring(5,10)%>',
			 			'<%=dateList[20][0].substring(5,10)%>',
			 			'<%=dateList[21][0].substring(5,10)%>',
			 			'<%=dateList[22][0].substring(5,10)%>',
			 			'<%=dateList[23][0].substring(5,10)%>',
			 			'<%=dateList[24][0].substring(5,10)%>',
			 			'<%=dateList[25][0].substring(5,10)%>',
			 			'<%=dateList[26][0].substring(5,10)%>',
			 			'<%=dateList[27][0].substring(5,10)%>',
			 			'<%=dateList[28][0].substring(5,10)%>',
			 			'<%=dateList[29][0].substring(5,10)%>',
			 			'<%=dateList[30][0].substring(5,10)%>']
		},

		yAxis: {
			allowDecimals: false,
			min: 0,
			title: {
				text: '作业量'
			}
		},
		tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
					 this.series.name +': '+ this.y +'<br/>'+
					 'Total: '+ this.point.stackTotal;
			}
		},

		plotOptions: {
			series: {
				cursor: 'pointer',
				point: {
					events: {
						click: function() {
							location.href = this.options.url;
						}		
					}
				}
			},
			column: {
				stacking: 'normal'
			}
		},

	    series: [
	     	{
			name: '未发货订单',
			data: [
					{y:<%=StringUtil.StringToId(dateList[0][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[0][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[1][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[1][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[2][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[2][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[3][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[3][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[4][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[4][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[5][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[5][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[6][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[6][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[7][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[7][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[8][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[8][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[9][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[9][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[10][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[10][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[11][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[11][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[12][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[12][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[13][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[13][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[14][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[14][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[15][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[15][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[16][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[16][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[17][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[17][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[18][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[18][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[19][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[19][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[20][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[20][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[21][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[21][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[22][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[22][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[23][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[23][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[24][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[24][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[25][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[25][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[26][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[26][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[27][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[27][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[28][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[28][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[29][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[29][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=StringUtil.StringToId(dateList[30][2])%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[30][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}
					],
			stack: 'male'
			},
	     	{
			name: '实际发货订单',
			data: [
					{y:<%=dateList[0][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[0][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[1][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[1][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[2][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[2][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[3][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[3][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[4][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[4][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[5][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[5][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[6][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[6][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[7][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[7][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[8][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[8][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[9][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[9][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[10][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[10][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[11][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[11][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[12][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[12][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[13][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[13][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[14][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[14][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[15][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[15][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[16][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[16][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[17][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[17][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[18][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[18][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[19][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[19][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[20][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[20][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[21][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[21][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[22][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[22][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[23][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[23][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[24][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[24][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[25][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[25][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[26][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[26][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[27][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[27][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[28][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[28][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[29][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[29][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[30][1]%>,url: 'orderStockTimely.do?method=orderStockTimely2&date=<%=dateList[30][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}
				   ],
			stack: 'male'
			}
			]
	});
	
	
});
	
</script>
<div id="container" style="width: 1000px; height: 400px; margin: 0 auto"></div> 
<div>日订单发货明细</div>
<form id="searchForm" action="<%=request.getContextPath() %>/admin/orderStockTimely.do" method="post">
订单状态：
<select name="orderType">
	<option value="-1" <%if(orderType.equals("-1")){ %>selected="selected"<%} %>>请选择</option>
	<option value="1" <%if(orderType.equals("1")){ %>selected="selected"<%} %>>未发货订单</option>
	<option value="2" <%if(orderType.equals("2")){ %>selected="selected"<%} %>>实际发货订单</option>
</select>&nbsp;&nbsp;
时间：<input type="text" name="date" value="<%=date %>" size="10" onclick="SelectDate(this,'yyyy-MM-dd');">&nbsp;&nbsp;
订单号：<input type="text" name="orderCode" value="<%=orderCode %>" size="15"/>&nbsp;&nbsp;
<input type="hidden" name="pageType" value="<%=pageType %>" />
<input type="hidden" name="searchType" value="<%=searchType %>" />
<input type="hidden" name="search" value="search" />
<input type="hidden" id="searchMethod" name="method" value="orderStockTimely2" />
<input type="submit" onclick="document.getElementById('searchMethod').value='orderStockTimely2';" value="查询"/><br/><br/>
<%if(request.getAttribute("orderStockTimelyList")!=null){%>
	<%List orderStockTimelyList=(List)request.getAttribute("orderStockTimelyList"); %>
	<table border="1">
		<tr>
			<td>订单号</td>
			<td>订单状态</td>
			<td>申请发货次数</td>
			<td>申请发货时间</td>
			<td>申请人</td>
			<td>复核时间</td>
			<td>复核操作人</td>
		</tr>
		<%for(int i=0;i<orderStockTimelyList.size();i++){ %>
			<%OrderStockTimelyBean ostBean=(OrderStockTimelyBean)orderStockTimelyList.get(i); %>
		<tr align="center">
			<td><%=ostBean.getOrderCode() %></td>
			<td><%=ostBean.getStockOutUserId()==0?"待发货":"已复核" %></td>
			<td><%=ostBean.getOrderStockCount() %></td>
			<td><%=ostBean.getFirstOrderStockDatetime()==null?"-":ostBean.getFirstOrderStockDatetime().substring(0,19) %></td>
			<td><%=ostBean.getFirstOrderStockUserName().equals("")?"-":ostBean.getFirstOrderStockUserName() %></td>
			<td><%=ostBean.getStockOutDatetime()==null?"-":ostBean.getStockOutDatetime().substring(0,19) %></td>
			<td><%=ostBean.getStockOutUserName().equals("")?"-":ostBean.getStockOutUserName() %></td>
		</tr>
		<%} %>
	</table>
<input type="button" value="导出明细到excel" onclick="document.getElementById('searchMethod').value='orderStockTimelyExcel';document.getElementById('searchForm').submit();"/>
<%} %>
</form>
</div>
<%}else if(pageType==2){ %>
<div id="jishilv">
<form action="<%=request.getContextPath() %>/admin/orderStockTimely.do?method=orderStockTimely" method="post">
	统计周期：
	<select name="searchType">
		<option value="1" <%if(searchType==1){ %>selected="selected"<%} %>>按天</option>
		<option value="2" <%if(searchType==2){ %>selected="selected"<%} %>>按周</option>
		<option value="3" <%if(searchType==3){ %>selected="selected"<%} %>>按月</option>
	</select>
	<input type="hidden" name="pageType" value="<%=pageType %>" />
	<input type="submit" value="查询" />
</form>
<script type="text/javascript">
var chart;
$(document).ready(function() {
	chart = new Highcharts.Chart({

		chart: {
			renderTo: 'container',
			defaultSeriesType: 'column'
		},

		title: {
			text: '点击柱状图查看当日作业列表'
		},

		xAxis: {
			title: {
				text: '时间'
			},
			categories: ['<%=dateList[0][0].substring(5,10)%>', 
			 			'<%=dateList[1][0].substring(5,10)%>', 
			 			'<%=dateList[2][0].substring(5,10)%>', 
			 			'<%=dateList[3][0].substring(5,10)%>', 
			 			'<%=dateList[4][0].substring(5,10)%>',
			 			'<%=dateList[5][0].substring(5,10)%>',
			 			'<%=dateList[6][0].substring(5,10)%>',
			 			'<%=dateList[7][0].substring(5,10)%>',
			 			'<%=dateList[8][0].substring(5,10)%>',
			 			'<%=dateList[9][0].substring(5,10)%>',
			 			'<%=dateList[10][0].substring(5,10)%>',
			 			'<%=dateList[11][0].substring(5,10)%>',
			 			'<%=dateList[12][0].substring(5,10)%>',
			 			'<%=dateList[13][0].substring(5,10)%>',
			 			'<%=dateList[14][0].substring(5,10)%>',
			 			'<%=dateList[15][0].substring(5,10)%>',
			 			'<%=dateList[16][0].substring(5,10)%>',
			 			'<%=dateList[17][0].substring(5,10)%>',
			 			'<%=dateList[18][0].substring(5,10)%>',
			 			'<%=dateList[19][0].substring(5,10)%>',
			 			'<%=dateList[20][0].substring(5,10)%>',
			 			'<%=dateList[21][0].substring(5,10)%>',
			 			'<%=dateList[22][0].substring(5,10)%>',
			 			'<%=dateList[23][0].substring(5,10)%>',
			 			'<%=dateList[24][0].substring(5,10)%>',
			 			'<%=dateList[25][0].substring(5,10)%>',
			 			'<%=dateList[26][0].substring(5,10)%>',
			 			'<%=dateList[27][0].substring(5,10)%>',
			 			'<%=dateList[28][0].substring(5,10)%>',
			 			'<%=dateList[29][0].substring(5,10)%>',
			 			'<%=dateList[30][0].substring(5,10)%>']
		},

		yAxis: {
			allowDecimals: false,
			min: 0,
			title: {
				text: '作业量'
			}
		},
		tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
					 this.series.name +': '+ this.y +'<br/>'+
					 'Total: '+ this.point.stackTotal;
			}
		},

		plotOptions: {
			series: {
				cursor: 'pointer',
				point: {
					events: {
						click: function() {
							location.href = this.options.url;
						}		
					}
				}
			},
			column: {
				stacking: 'normal'
			}
		},

	    series: [
	     	{
			name: '未发货订单',
			data: [
					{y:<%=dateList[0][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[0][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[1][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[1][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[2][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[2][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[3][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[3][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[4][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[4][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[5][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[5][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[6][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[6][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[7][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[7][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[8][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[8][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[9][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[9][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[10][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[10][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[11][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[11][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[12][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[12][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[13][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[13][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[14][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[14][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[15][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[15][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[16][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[16][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[17][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[17][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[18][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[18][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[19][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[19][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[20][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[20][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[21][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[21][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[22][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[22][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[23][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[23][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[24][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[24][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[25][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[25][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[26][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[26][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[27][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[27][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[28][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[28][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[29][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[29][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[30][1]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[30][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}
				   ],
			stack: 'male'
			}, 
			{
			name: '申请发货多次且已发货订单',
			data: [
					{y:<%=dateList[0][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[0][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[1][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[1][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[2][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[2][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[3][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[3][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[4][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[4][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[5][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[5][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[6][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[6][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[7][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[7][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[8][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[8][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[9][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[9][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[10][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[10][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[11][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[11][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[12][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[12][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[13][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[13][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[14][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[14][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[15][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[15][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[16][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[16][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[17][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[17][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[18][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[18][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[19][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[19][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[20][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[20][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[21][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[21][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[22][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[22][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[23][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[23][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[24][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[24][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[25][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[25][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[26][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[26][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[27][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[27][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[28][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[28][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[29][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[29][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[30][2]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[30][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}
					],
			stack: 'male'
			},
			{
			name: '申请发货一次且已发货订单',
			data: [
					{y:<%=dateList[0][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[0][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[1][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[1][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[2][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[2][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[3][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[3][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[4][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[4][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[5][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[5][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[6][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[6][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[7][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[7][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[8][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[8][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[9][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[9][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[10][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[10][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[11][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[11][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[12][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[12][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[13][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[13][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[14][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[14][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[15][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[15][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[16][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[16][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[17][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[17][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[18][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[18][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[19][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[19][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[20][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[20][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[21][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[21][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[22][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[22][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[23][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[23][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[24][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[24][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[25][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[25][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[26][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[26][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[27][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[27][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[28][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[28][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[29][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[29][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}, 
					{y:<%=dateList[30][3]%>,url: 'orderStockTimely.do?method=orderStockTimely&date=<%=dateList[30][0]%>&searchType=<%=searchType%>&pageType=<%=pageType%>'}
				   ],
			stack: 'male'
			}]
	});
	
	
});
	
</script>
<div id="container" style="width: 1000px; height: 400px; margin: 0 auto"></div> 
<div>日订单发货明细</div>
<form id="searchForm" action="<%=request.getContextPath() %>/admin/orderStockTimely.do" method="post">
订单状态：
<select name="orderType">
	<option value="-1" <%if(orderType.equals("-1")){ %>selected="selected"<%} %>>请选择</option>
	<option value="1" <%if(orderType.equals("1")){ %>selected="selected"<%} %>>未发货</option>
	<option value="2" <%if(orderType.equals("2")){ %>selected="selected"<%} %>>已发货(申请发货多次)</option>
	<option value="3" <%if(orderType.equals("3")){ %>selected="selected"<%} %>>已发货(申请发货一次)</option>
</select>&nbsp;&nbsp;
时间：<input type="text" name="date" value="<%=date %>" onclick="SelectDate(this,'yyyy-MM-dd');" size="10" />&nbsp;&nbsp;
申请出库：<input type="text" name="orderStockCount" value="<%=request.getParameter("orderStockCount")==null?"":request.getParameter("orderStockCount") %>" size="5"/>次及以上&nbsp;&nbsp;
订单号：<input type="text" name="orderCode" value="<%=orderCode %>" size="15"/>
<input type="hidden" name="pageType" value="<%=pageType %>" />
<input type="hidden" name="searchType" value="<%=searchType %>" />
<input type="hidden" name="search" value="search" />
<input type="hidden" id="searchMethod" name="method" value="orderStockTimely" />
<input type="submit" onclick="document.getElementById('searchMethod').value='orderStockTimely';" value="查询"/><br/><br/>
<%if(request.getAttribute("orderStockTimelyList")!=null){%>
	<%List orderStockTimelyList=(List)request.getAttribute("orderStockTimelyList"); %>
	<table border="1">
		<tr>
			<td>订单号</td>
			<td>订单状态</td>
			<td>申请发货次数</td>
			<td>申请发货时间</td>
			<td>申请人</td>
			<td>复核时间</td>
			<td>复核操作人</td>
		</tr>
		<%for(int i=0;i<orderStockTimelyList.size();i++){ %>
			<%OrderStockTimelyBean ostBean=(OrderStockTimelyBean)orderStockTimelyList.get(i); %>
		<tr align="center">
			<td><%=ostBean.getOrderCode() %></td>
			<td><%=ostBean.getStockOutUserId()==0?"待发货":"已复核" %></td>
			<td><%=ostBean.getOrderStockCount() %></td>
			<td><%=ostBean.getFirstOrderStockDatetime()==null?"-":ostBean.getFirstOrderStockDatetime().substring(0,19) %></td>
			<td><%=ostBean.getFirstOrderStockUserName().equals("")?"-":ostBean.getFirstOrderStockUserName() %></td>
			<td><%=ostBean.getStockOutDatetime()==null?"-":ostBean.getStockOutDatetime().substring(0,19) %></td>
			<td><%=ostBean.getStockOutUserName().equals("")?"-":ostBean.getStockOutUserName() %></td>
		</tr>
		<%} %>
	</table>
<input type="button" value="导出明细到excel" onclick="document.getElementById('searchMethod').value='orderStockTimelyExcel';document.getElementById('searchForm').submit();"/>
<%} %>
</form>
</div>
<%} %>
</body>
</html>