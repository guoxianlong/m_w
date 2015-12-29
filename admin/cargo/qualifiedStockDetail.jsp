<%@page import="adultadmin.util.DateUtil"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="adultadmin.bean.cargo.CargoOperationCargoBean"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="adultadmin.bean.cargo.CargoOperationBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
List qualifiedStockDetailList = new ArrayList();
List cargoOperationCargoList = new ArrayList();
if(request.getAttribute("qualifiedStockDetailList") != null){
	qualifiedStockDetailList = (List)request.getAttribute("qualifiedStockDetailList");
}
if(request.getAttribute("cargoOperationCargoList") != null){
	cargoOperationCargoList = (List)request.getAttribute("cargoOperationCargoList");
}
List dateList=(List)request.getAttribute("dateList");
List operCountList1=(List)request.getAttribute("operCountList1");
List operCountList2=(List)request.getAttribute("operCountList2");
List operCountList3=(List)request.getAttribute("operCountList3");

String date=StringUtil.convertNull(request.getParameter("date"));
if(date.equals("")){
	date = DateUtil.getNowDateStr();
}
List operList1=(List)request.getAttribute("operList1");
List operList2=(List)request.getAttribute("operList2");
List operList3=(List)request.getAttribute("operList3");
List operList4=(List)request.getAttribute("operList4");
List operCocList1=(List)request.getAttribute("operCocList1");
List operCocList2=(List)request.getAttribute("operCocList2");
List operCocList3=(List)request.getAttribute("operCocList3");
List operCocList4=(List)request.getAttribute("operCocList4");
PagingBean paging=(PagingBean)request.getAttribute("paging");
PagingBean paging1=(PagingBean)request.getAttribute("paging1");
PagingBean paging2=(PagingBean)request.getAttribute("paging2");
PagingBean paging3=(PagingBean)request.getAttribute("paging3");
PagingBean paging4=(PagingBean)request.getAttribute("paging4");
%>	
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<title>合格库作业状态动态明细</title>
</head>
<body>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/highcharts.src.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/themes/dark-blue.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/modules/exporting.src.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<style type="text/css">
.nTab {
	float: left;
	width: 100%;
	margin: 0 auto;
	border-bottom: 1px #000000 solid;
	background: #ffffff;
	background-position: left;
	background-repeat: repeat-y;
	margin-bottom: 1px;
}

.nTab .TabTitle {
	clear: both;
	height: 22px;
	overflow: hidden;
}

.nTab .TabTitle ul {
	border: 0;
	margin: 0;
	padding: 0;
}

.nTab .TabTitle li {
	float: left;
	width: 70px;
	cursor: pointer;
	padding-top: 4px;
	padding-right: 0px;
	padding-left: 0px;
	padding-bottom: 2px;
	list-style-type: none;
	font-size: 12px;
	text-align: center;
	margin: 0;
}

.nTab .TabTitle .active {
	background: #999999;
}

.nTab .TabTitle .normal {
	background: #ffffff;
	border: 1px #000000 solid;
}

.nTab .TabContent {
	width: auto;
	background: #fff;
	margin: 0px auto;
	padding: 10px 0 0 0;
	border:1px #000000 solid;
}

.none {
	display: none;
}
</style>
<script type="text/javascript">
<!--  
	function nTabs(thisObj, Num) {
		if (thisObj.className == "active")
			return;
		var tabObj = thisObj.parentNode.id;
		var tabList = document.getElementById(tabObj)
				.getElementsByTagName("li");
		for (i = 0; i < tabList.length; i++) {
			if (i == Num) {
				thisObj.className = "active";
				document.getElementById(tabObj + "_Content" + i).style.display = "block";
			} else {
				tabList[i].className = "normal";
				document.getElementById(tabObj + "_Content" + i).style.display = "none";
			}
		}
	}
-->
</script>
<script type="text/javascript">

function selCondition(id) {
    var index = 0;
    var defaultOption, condition2Option;
    
    index = document.getElementById("condition"+id+"1").value;
    defaultOption = new Option("全部时效状态", "0");
    
    document.getElementById("condition"+id+"2").options.length = 0;
    document.getElementById("condition"+id+"2").options.add(defaultOption);

    if(index==1||index==2){
		document.getElementById("condition"+id+"2").options.add(new Option("作业中","1"));
		document.getElementById("condition"+id+"2").options.add(new Option("超出时效","2"));
		document.getElementById("condition"+id+"2").options.add(new Option("作业失败","5"));
    }else if(index==3){
    	document.getElementById("condition"+id+"2").options.add(new Option("待复核","3"));
		document.getElementById("condition"+id+"2").options.add(new Option("作业成功","4"));
		document.getElementById("condition"+id+"2").options.add(new Option("作业失败","5"));
    }
}

function shaixuan(num,pageIndex){
	var aform=document.getElementById("form"+num);
	var condition1=aform.condition1.value;
	var condition2=aform.condition2.value;
	var date=aform.date.value;
	if(date==""){
		date="0";
	}
	var operType=aform.operType.value;
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=4&condition1="+condition1+"&condition2="+condition2+"&date="+date+"&operType="+operType+"&num="+num+"&pageIndex="+pageIndex,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("myTab1_Content"+num).innerHTML = msg;
			if(operType==-1){
				operType="";
			}
			selCondition(operType);
			selectOption(document.getElementById("condition"+operType+"2"),condition2)
		}
	});
}
function toExcel(num){
	var textarea=document.getElementById("excel"+num);
	var texttable=document.getElementById("table"+num);
	textarea.value=texttable.innerHTML;
}
</script>
<style>
td{
	font-family: 微软雅黑; 
	font-size: 13px; 
	font-weight: normal; 
	font-style: normal; 
	text-decoration: none; 
	color: #333333;
 }
</style>
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
			categories: ['<%=dateList.get(0).toString().substring(5,10)%>', 
			 			'<%=dateList.get(1).toString().substring(5,10)%>', 
			 			'<%=dateList.get(2).toString().substring(5,10)%>', 
			 			'<%=dateList.get(3).toString().substring(5,10)%>', 
			 			'<%=dateList.get(4).toString().substring(5,10)%>',
			 			'<%=dateList.get(5).toString().substring(5,10)%>',
			 			'<%=dateList.get(6).toString().substring(5,10)%>',
			 			'<%=dateList.get(7).toString().substring(5,10)%>',
			 			'<%=dateList.get(8).toString().substring(5,10)%>',
			 			'<%=dateList.get(9).toString().substring(5,10)%>',
			 			'<%=dateList.get(10).toString().substring(5,10)%>',
			 			'<%=dateList.get(11).toString().substring(5,10)%>',
			 			'<%=dateList.get(12).toString().substring(5,10)%>',
			 			'<%=dateList.get(13).toString().substring(5,10)%>',
			 			'<%=dateList.get(14).toString().substring(5,10)%>',
			 			'<%=dateList.get(15).toString().substring(5,10)%>',
			 			'<%=dateList.get(16).toString().substring(5,10)%>',
			 			'<%=dateList.get(17).toString().substring(5,10)%>',
			 			'<%=dateList.get(18).toString().substring(5,10)%>',
			 			'<%=dateList.get(19).toString().substring(5,10)%>',
			 			'<%=dateList.get(20).toString().substring(5,10)%>',
			 			'<%=dateList.get(21).toString().substring(5,10)%>',
			 			'<%=dateList.get(22).toString().substring(5,10)%>',
			 			'<%=dateList.get(23).toString().substring(5,10)%>',
			 			'<%=dateList.get(24).toString().substring(5,10)%>',
			 			'<%=dateList.get(25).toString().substring(5,10)%>',
			 			'<%=dateList.get(26).toString().substring(5,10)%>',
			 			'<%=dateList.get(27).toString().substring(5,10)%>',
			 			'<%=dateList.get(28).toString().substring(5,10)%>',
			 			'<%=dateList.get(29).toString().substring(5,10)%>',
			 			'<%="待作业"%>']
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
			name: '未完成',
			data: [
					{y:<%=operCountList1.get(0)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=operCountList1.get(1)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(1).toString().replace("/","-")%>'}, 
					{y:<%=operCountList1.get(2)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(2).toString().replace("/","-")%>'}, 
					{y:<%=operCountList1.get(3)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(3).toString().replace("/","-")%>'}, 
					{y:<%=operCountList1.get(4)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(4).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(5)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(5).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(6)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(6).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(7)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(7).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(8)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(8).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(9)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(9).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(10)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(10).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(11)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(11).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(12)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(12).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(13)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(13).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(14)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(14).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(15)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(15).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(16)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(16).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(17)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(17).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(18)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(18).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(19)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(19).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(20)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(20).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(21)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(21).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(22)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(22).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(23)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(23).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(24)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(24).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(25)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(25).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(26)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(26).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(27)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(27).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(28)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(28).toString().replace("/","-")%>'},
					{y:<%=operCountList1.get(29)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(29).toString().replace("/","-")%>'}
				   ],
			stack: 'male'
			}, 
			{
			name: '已完成',
			data: [
					{y:<%=operCountList2.get(0)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=operCountList2.get(1)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(1).toString().replace("/","-")%>'}, 
					{y:<%=operCountList2.get(2)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(2).toString().replace("/","-")%>'}, 
					{y:<%=operCountList2.get(3)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(3).toString().replace("/","-")%>'}, 
					{y:<%=operCountList2.get(4)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(4).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(5)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(5).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(6)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(6).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(7)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(7).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(8)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(8).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(9)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(9).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(10)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(10).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(11)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(11).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(12)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(12).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(13)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(13).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(14)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(14).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(15)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(15).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(16)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(16).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(17)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(17).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(18)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(18).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(19)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(19).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(20)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(20).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(21)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(21).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(22)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(22).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(23)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(23).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(24)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(24).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(25)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(25).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(26)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(26).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(27)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(27).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(28)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(28).toString().replace("/","-")%>'},
					{y:<%=operCountList2.get(29)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(29).toString().replace("/","-")%>'}
					],
			stack: 'male'
		},
		{
			name: '作业失败',
			data: [
					{y:<%=operCountList3.get(0)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=operCountList3.get(1)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(1).toString().replace("/","-")%>'}, 
					{y:<%=operCountList3.get(2)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(2).toString().replace("/","-")%>'}, 
					{y:<%=operCountList3.get(3)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(3).toString().replace("/","-")%>'}, 
					{y:<%=operCountList3.get(4)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(4).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(5)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(5).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(6)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(6).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(7)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(7).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(8)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(8).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(9)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(9).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(10)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(10).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(11)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(11).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(12)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(12).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(13)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(13).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(14)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(14).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(15)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(15).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(16)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(16).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(17)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(17).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(18)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(18).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(19)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(19).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(20)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(20).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(21)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(21).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(22)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(22).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(23)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(23).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(24)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(24).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(25)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(25).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(26)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(26).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(27)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(27).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(28)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(28).toString().replace("/","-")%>'},
					{y:<%=operCountList3.get(29)%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(29).toString().replace("/","-")%>'}
					],
			stack: 'male'
		}
		]
	});
	
	
});
	
</script>
<div id="container" style="width: 1000px; height: 400px; margin: 0 auto"></div> 
<%--
<form action="qualifiedStock.do?method=qualifiedStockDetail" method="post">
日期：<input type="text" id="date" name="date" onclick="SelectDate(this,'yyyy-MM-dd');" <%if(date!=null){ %>value='<%=date %>'<%} %>/>&nbsp;&nbsp;
<input type="submit" value="查询"/>
</form>
 --%>
<br />
<br />
<div align="center" style="padding-left: 25px;">
<!-- 选项卡开始 -->
<div class="nTab" style="width: 95%">
<!-- 标题开始 -->
<div class="TabTitle">
<ul id="myTab1">
	<li class="active" onclick="nTabs(this,0);">全部</li>
	<li class="normal" onclick="nTabs(this,1);">下架作业</li>
	<li class="normal" onclick="nTabs(this,2);">补货作业</li>
	<li class="normal" onclick="nTabs(this,3);">货位调拨</li>
	<li class="normal" onclick="nTabs(this,4);">上架作业</li>
</ul>
	<form action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=qualifiedStockDetail" method="post">
	<INPUT name="code" type=text  value="作业单编号" 
		style="color:#cccccc;"
		onfocus="if(this.value=='作业单编号'){this.value='';this.style.color='#000000';}" 
		onblur="if(this.value==''){this.value='作业单编号';this.style.color='#cccccc';}">
		<input type="hidden" name="date" value="<%=date%>"/>
	<INPUT name="search4code" type=submit  value="精确查询"  >
	</form>
</div>
<!-- 内容开始 -->
<div class="TabContent">
<div id="myTab1_Content0">
	<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form0" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=qualifiedStockDetail" method="post" onsubmit="return false;">
			<SELECT id="condition1" name="condition1" onchange="selCondition('');">
				<OPTION selected value="0" >全部作业状态</OPTION>
				<OPTION  value="1">生成阶段</OPTION>
				<OPTION  value="2">交接阶段</OPTION>
				<OPTION  value="3">作业完成</OPTION>
			</SELECT>
			<SELECT id="condition2" name="condition2">
				<OPTION selected value="0">全部时效状态</OPTION>
			</SELECT>
			<input type="hidden" name="date" <%if(date!=null){ %>value="<%=date %>"<%} %>/>
			<input type="hidden" name="operType" value="-1"/>
			<INPUT name="search" type="button"  value="筛选" onclick="shaixuan('0',0);"/>
			</form>
			</td>
		</tr>
		</table>

	<table id="table0" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">序号</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业单编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">源仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">目的仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">提交时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态变更时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">时效状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">日志查询</td>
		</tr>
		<%	
			for(int i = 0; i < qualifiedStockDetailList.size(); i++){
					CargoOperationBean cob = (CargoOperationBean)qualifiedStockDetailList.get(i);	
					CargoOperationCargoBean cocb = (CargoOperationCargoBean)cargoOperationCargoList.get(i);
					int id = cob.getId();
					String code = cob.getCode();
					int type = cob.getType();
					String statusName = StringUtil.convertNull(cob.getStatusName());
					if("已出货".equals(statusName)){
						statusName = "<font color = \"red\">" + statusName + "</font>"	;
					}
					if("已入货".equals(statusName)){
						statusName = "<font color = \"blue\">" + statusName + "</font>"	;
					}
					if("待作业".equals(statusName)){
						statusName = "<font color = \"green\">" + statusName + "</font>";
					}
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%></td>
				<td align="center">
				<%
				switch(type){ 
					case 0:
				%>
					<a href="cargoOper.do?method=showEditCargoOperation&operationId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 1:
					%>
					<a href="cargoDownShelf.do?method=showDownShel&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 2:
					%>
					<a href="cargoOperation.do?method=refillCargo&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 3:
					%>
					<a href="cargoOperation.do?method=exchangeCargo&cargoOperId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
				<%
					break;
				} 
				%>
				</td>
				<td align="center"><%=cocb.getOutCargoWholeCode() == null ? "" : cocb.getOutCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cocb.getInCargoWholeCode() == null ? "" : cocb.getInCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cob.getConfirmDatetime() == null ? "" : cob.getConfirmDatetime().substring(0,16)%></td>
				<td align="center"><%=statusName%></td>
				<td align="center"><%=cob.getLastOperateDatetime() == null ? "" : cob.getLastOperateDatetime().substring(0,16)%></td>
				<td align="center"><%if(cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS1 || cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){%><font color="red"><%=cob.getEffectTimeName()%></font><%}else{ %><%=cob.getEffectTimeName()%><%} %></td>
				<td align="center">
				<a href="qualifiedStock.do?method=cargoOperLog&operCode=<%=cob.getCode()%>" target="_blank"><font color="#0000ff">作业日志</font></a>
				</td>
			</tr>
		<%} %>
	</table>
	<form action="cargo/qualifiedStockDetailExcel.jsp" method="post" onsubmit="toExcel('0');">
		<input type="text" id="excel0" name="excel" style="display:none;"/>
		<input type="submit" value="导出本页列表到EXCEL"/>
	</form>
	<%if(paging!=null){ %>
		<center><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
</div>
<div id="myTab1_Content1" class="none">
	<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form1" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=qualifiedStockDetail" method="post">
			<SELECT id="condition11" name="condition1" onchange="selCondition(1);">
				<OPTION selected value="0" >全部作业状态</OPTION>
				<OPTION  value="1">生成阶段</OPTION>
				<OPTION  value="2">交接阶段</OPTION>
				<OPTION  value="3">作业完成</OPTION>
			</SELECT>
			<SELECT id="condition12" name="condition2">
				<OPTION selected value="0">全部时效状态</OPTION>
			</SELECT>
			<input type="hidden" name="date" <%if(date!=null){ %>value="<%=date %>"<%} %>/>
			<input type="hidden" name="operType" value="1"/>
			<INPUT name="search" type="button"  value="筛选" onclick="shaixuan('1',0);"/>
			</form>
			</td>
		</tr>
		</table>

	<table id="table1" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">序号</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业单编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">源仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">目的仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">提交时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态变更时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">时效状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">日志查询</td>
		</tr>
		<%	
			for(int i = 0; i < operList2.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList2.get(i);	
				CargoOperationCargoBean cocb = (CargoOperationCargoBean)operCocList2.get(i);
				int id = cob.getId();
				String code = cob.getCode();
				int type = cob.getType();
				String statusName = StringUtil.convertNull(cob.getStatusName());
				if("已出货".equals(statusName)){
					statusName = "<font color = \"red\">" + statusName + "</font>"	;
				}
				if("已入货".equals(statusName)){
					statusName = "<font color = \"blue\">" + statusName + "</font>"	;
				}
				if("待作业".equals(statusName)){
					statusName = "<font color = \"green\">" + statusName + "</font>";
				}
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%></td>
				<td align="center">
				<%
				switch(type){ 
					case 0:
				%>
					<a href="cargoOper.do?method=showEditCargoOperation&operationId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 1:
					%>
					<a href="cargoDownShelf.do?method=showDownShel&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 2:
					%>
					<a href="cargoOperation.do?method=refillCargo&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 3:
					%>
					<a href="cargoOperation.do?method=exchangeCargo&cargoOperId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
				<%
					break;
				} 
				%>
				</td>
				<td align="center"><%=cocb.getOutCargoWholeCode() == null ? "" : cocb.getOutCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cocb.getInCargoWholeCode() == null ? "" : cocb.getInCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cob.getConfirmDatetime() == null ? "" : cob.getConfirmDatetime().substring(0,16)%></td>
				<td align="center"><%=statusName%></td>
				<td align="center"><%=cob.getLastOperateDatetime() == null ? "" : cob.getLastOperateDatetime().substring(0,16)%></td>
				<td align="center"><%if(cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS1 || cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){%><font color="red"><%=cob.getEffectTimeName()%></font><%}else{ %><%=cob.getEffectTimeName()%><%} %></td>
				<td align="center">
				<a href="qualifiedStock.do?method=cargoOperLog&operCode=<%=cob.getCode()%>" target="_blank"><font color="#0000ff">作业日志</font></a>
				</td>
			</tr>
		<%} %>
	</table>
	<form action="cargo/qualifiedStockDetailExcel.jsp" method="post" onsubmit="toExcel('1');">
		<input type="text" id="excel1" name="excel" style="display:none;"/>
		<input type="submit" value="导出本页列表到EXCEL"/>
	</form>
	<%if(paging2!=null){ %>
		<center><%=PageUtil.fenye(paging2, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
</div>
<div id="myTab1_Content2" class="none">
<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form2" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=qualifiedStockDetail" method="post">
			<SELECT id="condition21" name="condition1" onchange="selCondition(2);">
				<OPTION selected value="0" >全部作业状态</OPTION>
				<OPTION  value="1">生成阶段</OPTION>
				<OPTION  value="2">交接阶段</OPTION>
				<OPTION  value="3">作业完成</OPTION>
			</SELECT>
			<SELECT id="condition22" name="condition2">
				<OPTION selected value="0">全部时效状态</OPTION>
			</SELECT>
			<input type="hidden" name="date" <%if(date!=null){ %>value="<%=date %>"<%} %>/>
			<input type="hidden" name="operType" value="2"/>
			<INPUT name="search" type="button"  value="筛选" onclick="shaixuan('2',0);"/>
			</form>
			</td>
		</tr>
		</table>

	<table id="table2" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">序号</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业单编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">源仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">目的仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">提交时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态变更时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">时效状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">日志查询</td>
		</tr>
		<%	
			for(int i = 0; i < operList3.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList3.get(i);	
				CargoOperationCargoBean cocb = (CargoOperationCargoBean)operCocList3.get(i);
				int id = cob.getId();
				String code = cob.getCode();
				int type = cob.getType();
				String statusName = StringUtil.convertNull(cob.getStatusName());
				if("已出货".equals(statusName)){
					statusName = "<font color = \"red\">" + statusName + "</font>"	;
				}
				if("已入货".equals(statusName)){
					statusName = "<font color = \"blue\">" + statusName + "</font>"	;
				}
				if("待作业".equals(statusName)){
					statusName = "<font color = \"green\">" + statusName + "</font>";
				}
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%></td>
				<td align="center">
				<%
				switch(type){ 
					case 0:
				%>
					<a href="cargoOper.do?method=showEditCargoOperation&operationId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 1:
					%>
					<a href="cargoDownShelf.do?method=showDownShel&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 2:
					%>
					<a href="cargoOperation.do?method=refillCargo&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 3:
					%>
					<a href="cargoOperation.do?method=exchangeCargo&cargoOperId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
				<%
					break;
				} 
				%>
				</td>
				<td align="center"><%=cocb.getOutCargoWholeCode() == null ? "" : cocb.getOutCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cocb.getInCargoWholeCode() == null ? "" : cocb.getInCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cob.getConfirmDatetime() == null ? "" : cob.getConfirmDatetime().substring(0,16)%></td>
				<td align="center"><%=statusName%></td>
				<td align="center"><%=cob.getLastOperateDatetime() == null ? "" : cob.getLastOperateDatetime().substring(0,16)%></td>
				<td align="center"><%if(cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS1 || cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){%><font color="red"><%=cob.getEffectTimeName()%></font><%}else{ %><%=cob.getEffectTimeName()%><%} %></td>
				<td align="center">
				<a href="qualifiedStock.do?method=cargoOperLog&operCode=<%=cob.getCode()%>" target="_blank"><font color="#0000ff">作业日志</font></a>
				</td>
			</tr>
		<%} %>
	</table>
	<form action="cargo/qualifiedStockDetailExcel.jsp" method="post" onsubmit="toExcel('2');">
		<input type="text" id="excel2" name="excel" style="display:none;"/>
		<input type="submit" value="导出本页列表到EXCEL"/>
	</form>
	<%if(paging3!=null){ %>
		<center><%=PageUtil.fenye(paging3, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
</div>
<div id="myTab1_Content3" class="none">
<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form3" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=qualifiedStockDetail" method="post">
			<SELECT id="condition31" name="condition1" onchange="selCondition(3);">
				<OPTION selected value="0" >全部作业状态</OPTION>
				<OPTION  value="1">生成阶段</OPTION>
				<OPTION  value="2">交接阶段</OPTION>
				<OPTION  value="3">作业完成</OPTION>
			</SELECT>
			<SELECT id="condition32" name="condition2">
				<OPTION selected value="0">全部时效状态</OPTION>
			</SELECT>
			<input type="hidden" name="date" <%if(date!=null){ %>value="<%=date %>"<%} %>/>
			<input type="hidden" name="operType" value="3"/>
			<INPUT name="search" type="button"  value="筛选" onclick="shaixuan('3',0);"/>
			</form>
			</td>
		</tr>
		</table>

	<table id="table3" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">序号</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业单编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">源仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">目的仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">提交时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态变更时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">时效状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">日志查询</td>
		</tr>
		<%	
			for(int i = 0; i < operList4.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList4.get(i);	
				CargoOperationCargoBean cocb = (CargoOperationCargoBean)operCocList4.get(i);
				int id = cob.getId();
				String code = cob.getCode();
				int type = cob.getType();
				String statusName = StringUtil.convertNull(cob.getStatusName());
				if("已出货".equals(statusName)){
					statusName = "<font color = \"red\">" + statusName + "</font>"	;
				}
				if("已入货".equals(statusName)){
					statusName = "<font color = \"blue\">" + statusName + "</font>"	;
				}
				if("待作业".equals(statusName)){
					statusName = "<font color = \"green\">" + statusName + "</font>";
				}
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%></td>
				<td align="center">
				<%
				switch(type){ 
					case 0:
				%>
					<a href="cargoOper.do?method=showEditCargoOperation&operationId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 1:
					%>
					<a href="cargoDownShelf.do?method=showDownShel&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 2:
					%>
					<a href="cargoOperation.do?method=refillCargo&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 3:
					%>
					<a href="cargoOperation.do?method=exchangeCargo&cargoOperId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
				<%
					break;
				} 
				%>
				</td>
				<td align="center"><%=cocb.getOutCargoWholeCode() == null ? "" : cocb.getOutCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cocb.getInCargoWholeCode() == null ? "" : cocb.getInCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cob.getConfirmDatetime() == null ? "" : cob.getConfirmDatetime().substring(0,16)%></td>
				<td align="center"><%=statusName%></td>
				<td align="center"><%=cob.getLastOperateDatetime() == null ? "" : cob.getLastOperateDatetime().substring(0,16)%></td>
				<td align="center"><%if(cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS1 || cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){%><font color="red"><%=cob.getEffectTimeName()%></font><%}else{ %><%=cob.getEffectTimeName()%><%} %></td>
				<td align="center">
				<a href="qualifiedStock.do?method=cargoOperLog&operCode=<%=cob.getCode()%>" target="_blank"><font color="#0000ff">作业日志</font></a>
				</td>
			</tr>
		<%} %>
	</table>
	<form action="cargo/qualifiedStockDetailExcel.jsp" method="post" onsubmit="toExcel('3');">
		<input type="text" id="excel3" name="excel" style="display:none;"/>
		<input type="submit" value="导出本页列表到EXCEL"/>
	</form>
	<%if(paging4!=null){ %>
		<center><%=PageUtil.fenye(paging4, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
</div>
<div id="myTab1_Content4" class="none">
<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form4" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=qualifiedStockDetail" method="post">
			<SELECT id="condition01" name="condition1" onchange="selCondition(0);">
				<OPTION selected value="0" >全部作业状态</OPTION>
				<OPTION  value="1">生成阶段</OPTION>
				<OPTION  value="2">交接阶段</OPTION>
				<OPTION  value="3">作业完成</OPTION>
			</SELECT>
			<SELECT id="condition02" name="condition2">
				<OPTION selected value="0">全部时效状态</OPTION>
			</SELECT>
			<input type="hidden" name="date" <%if(date!=null){ %>value="<%=date %>"<%} %>/>
			<input type="hidden" name="operType" value="0"/>
			<INPUT name="search" type="button"  value="筛选" onclick="shaixuan('4',0);"/>
			</form>
			</td>
		</tr>
		</table>

	<table id="table4" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">序号</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业单编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">源仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">目的仓</td>
			<td align="center" style="font-weight:bold;color:#000000;">提交时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">作业状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态变更时间</td>
			<td align="center" style="font-weight:bold;color:#000000;">时效状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">日志查询</td>
		</tr>
		<%	
			for(int i = 0; i < operList1.size(); i++){
				CargoOperationBean cob = (CargoOperationBean)operList1.get(i);	
				CargoOperationCargoBean cocb = (CargoOperationCargoBean)operCocList1.get(i);
				int id = cob.getId();
				String code = cob.getCode();
				int type = cob.getType();
				String statusName = StringUtil.convertNull(cob.getStatusName());
				if("已出货".equals(statusName)){
					statusName = "<font color = \"red\">" + statusName + "</font>"	;
				}
				if("已入货".equals(statusName)){
					statusName = "<font color = \"blue\">" + statusName + "</font>"	;
				}
				if("待作业".equals(statusName)){
					statusName = "<font color = \"green\">" + statusName + "</font>";
				}
		%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%></td>
				<td align="center">
				<%
				switch(type){ 
					case 0:
				%>
					<a href="cargoOper.do?method=showEditCargoOperation&operationId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 1:
					%>
					<a href="cargoDownShelf.do?method=showDownShel&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 2:
					%>
					<a href="cargoOperation.do?method=refillCargo&id=<%=id%>"><font color="#0000ff"><%=code%></font></a>
					<%
					break;
					case 3:
					%>
					<a href="cargoOperation.do?method=exchangeCargo&cargoOperId=<%=id%>"><font color="#0000ff"><%=code%></font></a>
				<%
					break;
				} 
				%>
				</td>
				<td align="center"><%=cocb.getOutCargoWholeCode() == null ? "" : cocb.getOutCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cocb.getInCargoWholeCode() == null ? "" : cocb.getInCargoWholeCode().substring(0,5)%></td>
				<td align="center"><%=cob.getConfirmDatetime() == null ? "" : cob.getConfirmDatetime().substring(0,16)%></td>
				<td align="center"><%=statusName%></td>
				<td align="center"><%=cob.getLastOperateDatetime() == null ? "" : cob.getLastOperateDatetime().substring(0,16)%></td>
				<td align="center"><%if(cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS1 || cob.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){%><font color="red"><%=cob.getEffectTimeName()%></font><%}else{ %><%=cob.getEffectTimeName()%><%} %></td>
				<td align="center">
				<a href="qualifiedStock.do?method=cargoOperLog&operCode=<%=cob.getCode()%>" target="_blank"><font color="#0000ff">作业日志</font></a>
				</td>
			</tr>
		<%} %>
	</table>
	<form action="cargo/qualifiedStockDetailExcel.jsp" method="post" onsubmit="toExcel('4');">
		<input type="text" id="excel4" name="excel" style="display:none;"/>
		<input type="submit" value="导出本页列表到EXCEL"/>
	</form>
	<%if(paging1!=null){ %>
		<center><%=PageUtil.fenye(paging1, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
</div>
</div>
</div>
<!-- 选项卡结束 --></div>
</body>
</html>
