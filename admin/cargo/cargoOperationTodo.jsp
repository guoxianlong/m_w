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
List refillList=new ArrayList();
List upShelfList=new ArrayList();
List downShelfList=new ArrayList();
List exchangeList=new ArrayList();

if(request.getAttribute("refillList")!=null){
	refillList=(List)request.getAttribute("refillList");
}
if(request.getAttribute("upShelfList")!=null){
	upShelfList=(List)request.getAttribute("upShelfList");
}
if(request.getAttribute("downShelfList")!=null){
	downShelfList=(List)request.getAttribute("downShelfList");
}
if(request.getAttribute("exchangeList")!=null){
	exchangeList=(List)request.getAttribute("exchangeList");
}

int refillCount=0;
int upShelfCount=0;
int downShelfCount=0;
int exchangeCount=0;

if(request.getAttribute("refillCount")!=null){
	refillCount=StringUtil.StringToId(request.getAttribute("refillCount").toString());
}
if(request.getAttribute("upShelfCount")!=null){
	upShelfCount=StringUtil.StringToId(request.getAttribute("upShelfCount").toString());
}
if(request.getAttribute("downShelfCount")!=null){
	downShelfCount=StringUtil.StringToId(request.getAttribute("downShelfCount").toString());
}
if(request.getAttribute("exchangeCount")!=null){
	exchangeCount=StringUtil.StringToId(request.getAttribute("exchangeCount").toString());
}

PagingBean paging0=(PagingBean)request.getAttribute("paging0");
PagingBean paging1=(PagingBean)request.getAttribute("paging1");
PagingBean paging2=(PagingBean)request.getAttribute("paging2");
PagingBean paging3=(PagingBean)request.getAttribute("paging3");

List dateList=(List)request.getAttribute("dateList");
List operCountList1=(List)request.getAttribute("operCountList1");
List operCountList2=(List)request.getAttribute("operCountList2");
List operCountList3=(List)request.getAttribute("operCountList3");
String date=StringUtil.convertNull(request.getParameter("date"));
%>	
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="mmb.stock.cargo.CargoOperationTodoBean"%><html>
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
	width: 170px;
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

function shaixuan(num,pageIndex){//筛选用
	//var aform=document.getElementById("form"+num);
	var status=document.getElementById("status"+num);
	var statusValue=status.options[status.selectedIndex].value;
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=5&pageIndex="+pageIndex+"&num="+num+"&status="+statusValue,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("myTab1_Content"+num).innerHTML = msg;
			}
	});
}
function shaixuan2(num,pageIndex){//分页用
	//var aform=document.getElementById("form"+num);
	var status=document.getElementById("tempStatus"+num).value;
	//var statusValue=status.options[status.selectedIndex].value;
	$.ajax({
		type: "GET",
		url: "qualifiedStock.do?method=selection&selectIndex=5&pageIndex="+pageIndex+"&num="+num+"&status="+status,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("myTab1_Content"+num).innerHTML = msg;
			}
	});
}
function toExcel(num){
	var textarea=document.getElementById("excel"+num);
	var texttable=document.getElementById("table"+num);
	textarea.value=texttable.innerHTML;
}
function checkSubmitTodo(){
	var todoList=document.getElementsByName("todo");
	var canSubmit=true;
	var choose=false;
	for(var i=0;i<todoList.length;i++){
		var todo=todoList[i];
		if(todo.checked==true){
			choose=true;
			if(todo.value.charAt(0)=="*"){
				canSubmit=false;
			}
		}
	}
	if(choose==false){
		alert("至少选择一条作业");
		return false;
	}
	if(canSubmit==false){
		canSubmit=confirm("选项中包含了正在作业的商品\r是否继续分配并打印作业清单");
	}
	return canSubmit;
}
function checkSubmitTodo2(num){
	var todoList=document.getElementsByName("todo"+num);
	var canSubmit=true;
	var choose=false;
	for(var i=0;i<todoList.length;i++){
		var todo=todoList[i];
		if(todo.checked==true){
			choose=true;
			if(todo.value.charAt(0)=="*"){
				canSubmit=false;
			}
		}
	}
	if(choose==false){
		alert("至少选择一条作业");
		return false;
	}
	if(canSubmit==false){
		canSubmit=confirm("选项中包含了正在作业的商品\r是否继续分配并打印作业清单");
	}
	if(canSubmit==true){
		var code=window.prompt("员工号:","");
		code=trim(code);
		var staffCode=document.getElementById("staffCode"+num);
		staffCode.value=code;
		if(code==""){
			alert("员工编号不能为空 ！");
		}
		if(code!=null&&code!=""){
			document.getElementById("form"+num).submit();
		}
	}
	return canSubmit;
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
		},
		{
			name: '待作业',
			data: [
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=0%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'}, 
					{y:<%=refillCount+upShelfCount+downShelfCount+exchangeCount%>,url: 'qualifiedStock.do?method=qualifiedStockDetail&date=<%=dateList.get(0).toString().replace("/","-")%>'} 
					],
			stack: 'male'
		}
		]
	});
	
	
});
	
</script>
<div id="container" style="width: 1000px; height: 400px; margin: 0 auto"></div> 
<br />
<br />
<div align="center" style="padding-left: 25px;">
<!-- 选项卡开始 -->
<div class="nTab" style="width: 95%">
<!-- 标题开始 -->
<div class="TabTitle">
<ul id="myTab1">
	<li class="active" onclick="nTabs(this,0);">待补货(<%=refillCount %>)</li>
	<li class="normal" onclick="nTabs(this,1);">待上架(<%=upShelfCount %>)</li>
	<li class="normal" onclick="nTabs(this,2);">待下架(<%=downShelfCount %>)</li>
	<li class="normal" onclick="nTabs(this,3);">待货位间调拨(<%=exchangeCount %>)</li>
</ul>
</div>
<!-- 内容开始 -->
<div class="TabContent">
<div id="myTab1_Content0">
	<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=cargoOperationTodo" method="post" onsubmit="return false;">
			<input type="text" name="code" value="输入关键字..." style="color:#cccccc;"
			onfocus="if(this.value=='输入关键字...'){this.value='';this.style.color='#000000';}" 
			onblur="if(this.value==''){this.value='输入关键字...';this.style.color='#cccccc';}"/>
			
			<SELECT id="status0" name="status">
				<OPTION selected value="-1" >全部作业状态</OPTION>
				<OPTION  value="0">未分配</OPTION>
				<OPTION  value="1">已分配</OPTION>
			</SELECT>
			<input type="hidden" id="tempStatus0" name="tempStatus" value="<%=request.getParameter("status")==null?"-1":request.getParameter("status")%>"/>
			<INPUT name="search" type="button"  value="查询" onclick="shaixuan('0',0);"/>
			<input type="button" value="手动更新列表" onclick="window.location.reload();"/>
			</form>
			</td>
		</tr>
	</table> 
	<form id="form0" action="qualifiedStock.do?method=submitCargoOperationTodo" method="post" target="_blank">
	<table id="table0" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">全部</td>
			<td align="center" style="font-weight:bold;color:#000000;">商品编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">源货位</td>
			<td align="center" style="font-weight:bold;color:#000000;">可用装箱单</td>
			<td align="center" style="font-weight:bold;color:#000000;">领取人</td>
		</tr>
		<%	
			for(int i = 0; i < refillList.size(); i++){
				CargoOperationTodoBean cot = (CargoOperationTodoBean)refillList.get(i);	
		%>		
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%><input type="checkbox" name="todo0" value="<%=cot.getStaffId()==0?"":"*" %><%=cot.getCargoProductStockId() %>"/></td>
				<td align="center"><%=cot.getProductCode() %></td>
				<td align="center"><%=CargoOperationTodoBean.getStatusName(cot.getStatus()) %></td>
				<td align="center"><%=cot.getCargoCode()%></td>
				<td align="center"><%=cot.getCount()%></td>
				<td align="center"><%=cot.getStaffName()==null?"":cot.getStaffName()%></td>
			</tr>
		<%} %>
	</table>
	<input type="hidden" id="staffCode0" name="staffCode" />
	<input type="button" value="分配并打印当页已选作业清单" onclick="checkSubmitTodo2(0);"/>
	<input type="hidden" name="type" value="2"/>
	</form>
	<%if(paging0!=null){ %>
		<center><%=PageUtil.fenye(paging0, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %>
	<br/>
	<br/>
</div>

<div id="myTab1_Content1" class="none">
	<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form0" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=cargoOperationTodo" method="post" onsubmit="return false;">
			<input type="text" name="code" value="输入关键字..." style="color:#cccccc;"
			onfocus="if(this.value=='输入关键字...'){this.value='';this.style.color='#000000';}" 
			onblur="if(this.value==''){this.value='输入关键字...';this.style.color='#cccccc';}"/>
			<SELECT id="status1" name="status">
				<OPTION selected value="-1" >全部作业状态</OPTION>
				<OPTION  value="0">未分配</OPTION>
				<OPTION  value="1">已分配</OPTION>
			</SELECT>
			<input type="hidden" id="tempStatus1" name="tempStatus" value="<%=request.getParameter("status")==null?"-1":request.getParameter("status")%>"/>
			<INPUT name="search" type="button"  value="查询" onclick="shaixuan('1',0);"/>
			<input type="button" value="手动更新列表" onclick="window.location.reload();"/>
			</form>
			</td>
		</tr>
	</table> 
	<form id="form1" action="qualifiedStock.do?method=submitCargoOperationTodo" method="post" target="_blank">
	<table id="table0" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">全部</td>
			<td align="center" style="font-weight:bold;color:#000000;">装箱单编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">源货位</td>
			<td align="center" style="font-weight:bold;color:#000000;">装箱数量</td>
			<td align="center" style="font-weight:bold;color:#000000;">领取人</td>
		</tr>
		<%	
			for(int i = 0; i < upShelfList.size(); i++){
					CargoOperationTodoBean cot = (CargoOperationTodoBean)upShelfList.get(i);	
		%>		
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%><input type="checkbox" name="todo1" value="<%=cot.getStaffId()==0?"":"*" %><%=cot.getProductId() %>"/></td>
				<td align="center"><%=cot.getProductCode() %></td>
				<td align="center"><%=CargoOperationTodoBean.getStatusName(cot.getStatus()) %></td>
				<td align="center"><%=cot.getCargoCode()%></td>
				<td align="center"><%=cot.getCount()%></td>
				<td align="center"><%=cot.getStaffName()==null?"":cot.getStaffName()%></td>
			</tr>
		<%} %>
	</table>
	<input type="hidden" id="staffCode1" name="staffCode" value="test"/>
	<input type="button" value="分配并打印当页已选作业清单" onclick="checkSubmitTodo2(1);"/>
	<input type="hidden" name="type" value="0"/>
	</form>
	<%if(paging1!=null){ %>
		<center><%=PageUtil.fenye(paging1, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %> 
	<br/>
	<br/>
</div>

<div id="myTab1_Content2" class="none">
	<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form0" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=cargoOperationTodo" method="post" onsubmit="return false;">
			<input type="text" name="code" value="输入关键字..." style="color:#cccccc;"
			onfocus="if(this.value=='输入关键字...'){this.value='';this.style.color='#000000';}" 
			onblur="if(this.value==''){this.value='输入关键字...';this.style.color='#cccccc';}"/>
			
			<SELECT id="status2" name="status">
				<OPTION selected value="-1" >全部作业状态</OPTION>
				<OPTION  value="0">未分配</OPTION>
				<OPTION  value="1">已分配</OPTION>
			</SELECT>
			<input type="hidden" id="tempStatus2" name="tempStatus" value="<%=request.getParameter("status")==null?"-1":request.getParameter("status")%>"/>
			<INPUT name="search" type="button"  value="查询" onclick="shaixuan('2',0);"/>
			<input type="button" value="手动更新列表" onclick="window.location.reload();"/>
			</form>
			</td>
		</tr>
	</table> 
	<form id="form2" action="qualifiedStock.do?method=submitCargoOperationTodo" method="post" target="_blank">
	<table id="table0" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">全部</td>
			<td align="center" style="font-weight:bold;color:#000000;">商品编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">源货位</td>
			<td align="center" style="font-weight:bold;color:#000000;">商品数量</td>
			<td align="center" style="font-weight:bold;color:#000000;">领取人</td>
		</tr>
		<%	
			for(int i = 0; i < downShelfList.size(); i++){
					CargoOperationTodoBean cot = (CargoOperationTodoBean)downShelfList.get(i);	
		%>		
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%><input type="checkbox" name="todo2" value="<%=cot.getStaffId()==0?"":"*" %><%=cot.getCargoProductStockId() %>"/></td>
				<td align="center"><%=cot.getProductCode() %></td>
				<td align="center"><%=CargoOperationTodoBean.getStatusName(cot.getStatus()) %></td>
				<td align="center"><%=cot.getCargoCode()%></td>
				<td align="center"><%=cot.getCount()%></td>
				<td align="center"><%=cot.getStaffName()==null?"":cot.getStaffName()%></td>
			</tr>
		<%} %>
	</table>
	<input type="hidden" id="staffCode2" name="staffCode" />
	<input type="button" value="分配并打印当页已选作业清单" onclick="checkSubmitTodo2(2);"/>
	<input type="hidden" name="type" value="1"/>
	</form>
	<%if(paging2!=null){ %>
		<center><%=PageUtil.fenye(paging2, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %> 
	<br/>
	<br/>
</div>
<div id="myTab1_Content3" class="none">
	<table cellpadding="3" border=0 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#ffffff">
			<td colspan="9" align="left" height="35px" style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;"> 
			<form id="form0" action="<%=request.getContextPath()%>/admin/qualifiedStock.do?method=cargoOperationTodo" method="post" onsubmit="return false;">
			<input type="text" name="code" value="输入关键字..." style="color:#cccccc;"
			onfocus="if(this.value=='输入关键字...'){this.value='';this.style.color='#000000';}" 
			onblur="if(this.value==''){this.value='输入关键字...';this.style.color='#cccccc';}"/>
			
			<SELECT id="status3" name="status">
				<OPTION selected value="-1" >全部作业状态</OPTION>
				<OPTION  value="0">未分配</OPTION>
				<OPTION  value="1">已分配</OPTION>
			</SELECT>
			<input type="hidden" id="tempStatus3" name="tempStatus" value="<%=request.getParameter("status")==null?"-1":request.getParameter("status")%>"/>
			<INPUT name="search" type="button"  value="查询" onclick="shaixuan('3',0);"/>
			<input style="left: 10cm;" type="button" value="手动更新列表" onclick="window.location.reload();"/>
			</form>
			</td>
		</tr>
	</table> 
	<form id="form3" action="qualifiedStock.do?method=submitCargoOperationTodo" method="post" target="_blank">
	<table id="table0" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr bgcolor="#00ccff" >
			<td align="center" style="font-weight:bold;color:#000000;">全部</td>
			<td align="center" style="font-weight:bold;color:#000000;">商品编号</td>
			<td align="center" style="font-weight:bold;color:#000000;">状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">源货位</td>
			<td align="center" style="font-weight:bold;color:#000000;">商品数量</td>
			<td align="center" style="font-weight:bold;color:#000000;">领取人</td>
		</tr>
		<%	
			for(int i = 0; i < exchangeList.size(); i++){
					CargoOperationTodoBean cot = (CargoOperationTodoBean)exchangeList.get(i);	
		%>		
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td align="center"><%=i+1%><input type="checkbox" name="todo3" value="<%=cot.getStaffId()==0?"":"*" %><%=cot.getCargoProductStockId() %>"/></td>
				<td align="center"><%=cot.getProductCode() %></td>
				<td align="center"><%=CargoOperationTodoBean.getStatusName(cot.getStatus()) %></td>
				<td align="center"><%=cot.getCargoCode()%></td>
				<td align="center"><%=cot.getCount()%></td>
				<td align="center"><%=cot.getStaffName()==null?"":cot.getStaffName()%></td>
			</tr>
		<%} %>
	</table>
	<input type="hidden" id="staffCode3" name="staffCode" />
	<input type="button" value="分配并打印当页已选作业清单" onclick="checkSubmitTodo2(3);"/>
	<input type="hidden" name="type" value="3"/>
	</form>
	<%if(paging3!=null){ %>
		<center><%=PageUtil.fenye(paging3, true, "&nbsp;&nbsp;", "pageIndex", 10)%></center>
	<%} %> 
	<br/>
	<br/>
</div>

</div>
</div>
<!-- 选项卡结束 --></div>
</body>
</html>