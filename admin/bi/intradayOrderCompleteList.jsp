<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<script type="text/javascript" charset="UTF-8">
var flag = '日';
$(function(){
	var yesterday = getEod();
	$("#startTime").datebox("setValue",yesterday);
	$("#endTime").datebox("setValue",yesterday);
	$("#startTime1").val(yesterday);
	$("#endTime1").val(yesterday);
	var productCountWareArea = $('#stockArea').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBIStockArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	$('#completeType').combobox({
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto',
		data: [{
			id:'intraday',
			text: '当日订单完成率'
		},{
			id:'cutOff',
			text: '截单周期订单完成率'
		}]
	});
});

function initIntradayOrderCompleteDatagrid() {
	$('#intradayOrderCompleteDatagrid').datagrid({
	    url:'${pageContext.request.contextPath}/BIController/getIntradayOrderCompleteDatagrid.mmx',
	    queryParams : {
	    	stockArea:$("#tb input[id=stockArea]").combobox("getValue"),
			startYear:$("#tb input[id=startYear]").val(),
			endYear:$("#tb input[id=endYear]").val(),
			startMonth:$("#tb input[id=startMonth]").val(),
			endMonth:$("#tb input[id=endMonth]").val(),
			startTime:$("#tb input[id=startTime]").datebox("getValue"),
			endTime:$("#tb input[id=endTime]").datebox("getValue")
	    },
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    showFooter: true,
	    columns:[[  
			{field:'datex',title:'日期',width:60,align:'center'},
			{field:'outOrderCount',title:'订单当日申请出库量',width:60,align:'center',
				formatter : function(value, row, index) {
					if ( value == null || value == 'undefined') {
						return '';
					} else {
						return '<a href="javascript:void(0);" onclick="toOrderDetail(\'outOrder\',\''+ row.datex+'\')">'+ value + '</a>';
					}
				}
			},
			{field:'deliverOrderCount',title:'订单当日发货量',width:60,align:'center',
				formatter : function(value, row, index) {
					if ( value == null || value == 'undefined') {
						return '';
					} else {
						return '<a href="javascript:void(0);" onclick="toOrderDetail(\'deliverOrder\',\''+ row.datex+'\')">'+ value + '</a>';
					}
				}
			},
			{field:'completePercent',title:'订单当日完成率',width:60,align:'center'}
	    ] ],
		onLoadSuccess : function(data) {
		}
	}); 
}
function initCutOffOrderCompleteDataGrid() {
	$('#intradayOrderCompleteDatagrid').datagrid({
    	url:'${pageContext.request.contextPath}/BIController/getCutOffOrderCompleteDatagrid.mmx',
	    queryParams : {
	    	stockArea:$("#tb input[id=stockArea]").combobox("getValue"),
			startYear:$("#tb input[id=startYear]").val(),
			endYear:$("#tb input[id=endYear]").val(),
			startMonth:$("#tb input[id=startMonth]").val(),
			endMonth:$("#tb input[id=endMonth]").val(),
			startTime:$("#tb input[id=startTime]").datebox("getValue"),
			endTime:$("#tb input[id=endTime]").datebox("getValue")
	    },
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    showFooter: true,
	    columns:[[  
			{field:'datex',title:'日期',width:60,align:'center'},
			{field:'cutOffOutOrderCount',title:'截单周期申请发货订单量',width:60,align:'center',
				formatter : function(value, row, index) {
					if (value == null || value == 'undefined') {
						return '';
					} else {
						return '<a href="javascript:void(0);" onclick="toOrderDetail(\'cutOffOutOrder\',\''+ row.datex+'\')">'+ value + '</a>';
					}
				}
			},
			{field:'realcutOffOutOrderCount',title:'当日实际发货订单量',width:60,align:'center',
				formatter : function(value, row, index) {
					if (value == null || value == 'undefined') {
						return '';
					} else {
						return '<a href="javascript:void(0);" onclick="toOrderDetail(\'realcutOffOutOrder\',\''+ row.datex+'\')">'+ value + '</a>';
					}
				}
			},
			{field:'completePercent',title:'截单周期订单完成率',width:60,align:'center'}
	    ] ],
		onLoadSuccess : function(data) {
		}
	}); 
}

function searchFun() {
	if (!checkSubmit()) {
		return false;
	}
	$("#tb input[id=stockArea1]").val($("#tb input[id=stockArea]").combobox("getValue"));
	$("#tb input[id=startYear1]").val($("#tb input[id=startYear]").val());
	$("#tb input[id=endYear1]").val($("#tb input[id=endYear]").val());
	$("#tb input[id=startMonth1]").val($("#tb input[id=startMonth]").val());
	$("#tb input[id=endMonth1]").val($("#tb input[id=endMonth]").val());
	$("#tb input[id=startTime1]").val($("#tb input[id=startTime]").datebox("getValue"));
	$("#tb input[id=endTime1]").val($("#tb input[id=endTime]").datebox("getValue"));
	if ($("#tb input[id=completeType]").combobox("getValue") =='intraday') {
		initIntradayOrderCompleteDatagrid();
		loadProductDatas('intradayComplete');
	} else if ($("#tb input[id=completeType]").combobox("getValue") =='cutOff' ){
		initCutOffOrderCompleteDataGrid();
		loadProductDatas('cutOff');
	}
}

function checkSubmit() {
	if($("#tb input[id=completeType]").combobox("getValue") == '') {
		$.messager.show({
			msg : "请选择订单完成项！",
			title : '提示'
		});
		return false;
	}
	var startYear=$("#tb input[id=startYear]").val();
	var endYear=$("#tb input[id=endYear]").val();
	var startMonth=$("#tb input[id=startMonth]").val();
	var endMonth=$("#tb input[id=endMonth]").val();
	var startTime=$("#tb input[id=startTime]").datebox("getValue");
	var endTime=$("#tb input[id=endTime]").datebox("getValue");
	if ($.trim(startTime) != "" && $.trim(endTime) != "") {
		var days = getValidateSubDays(endTime, startTime);
		if (days < 0) {
			$.messager.show({
				msg : "结束时间必须大于开始时间",
				title : '提示'
			});
			return false;
		}
		if (days>30){
			$.messager.show({
				msg : "日期时间段不得超过31天,请重新填写！",
				title : '提示'
			});
			return false;
		}
		flag='当日';
		return true;
	} 
	if ($.trim(startMonth) != "" && $.trim(endMonth) != "") {
		var days = getValidateSubDays(endMonth + "-01", startMonth + "-01");
		if (days < 0) {
			$.messager.show({
				msg : "结束年月必须大于开始年月",
				title : '提示'
			});
			return false;
		}
		if (days/30 >12) {
			$.messager.show({
				msg : "最多只能查12个月的数据",
				title : '提示'
			});
			return false;
		}
		flag='日均';
		return true;
	}
	if ($.trim(startYear) != "" && $.trim(endYear) != "") {
		var years = endYear - startYear;
		if (years < 0) {
			$.messager.show({
				msg : "结束年必须大于开始年",
				title : '提示'
			});
			return false;
		}
		if (years > 5) {
			$.messager.show({
				msg : "最多只能查5年的数据",
				title : '提示'
			});
			return false;
		}
		flag='月均';
		return true;
	}
	$.messager.show({
		msg : "请输入时间区间作为查询条件！",
		title : '提示'
	});
	return false;
}

function getEod(){  
    var date=new Date();  
    var i_milliseconds=date.getTime();  
    i_milliseconds-=1000*60*60*24;  
    var t_date = new Date();  
    t_date.setTime(i_milliseconds);  
    var i_year = t_date.getFullYear();  
    var i_month = ("0"+(t_date.getMonth()+1)).slice(-2);  
    var i_day = ("0"+t_date.getDate()).slice(-2);  
    return i_year+"-"+i_month+"-"+i_day;  
}  

function toOrderDetail(type, datex) {
	var stockArea=$("#tb input[id=stockArea1]").val();
	var startYear=$("#tb input[id=startYear1]").val();
	var endYear=$("#tb input[id=endYear1]").val();
	var startMonth=$("#tb input[id=startMonth1]").val();
	var endMonth=$("#tb input[id=endMonth1]").val();
	var startTime=$("#tb input[id=startTime1]").val();
	var endTime=$("#tb input[id=endTime1]").val();
	var datem = replaceAllStr(datex,'年','');
	datem = replaceAllStr(datem,'月','');
	datem = replaceAllStr(datem,'日','');
	if (datem.length == 10) {
		startTime = datem;
		endTime =datem;
	} else if (datem.length == 7) {
		startTime = "";
		endTime ="";
		startMonth = datem;
		endMonth = datem;
	} else if (datem.length == 4) {
		startTime = "";
		endTime ="";
		startMonth = "";
		endMonth = "";
		startYear = datem;
		endYear = datem;
	}
	var params="type=" + type 
			+ "&stockArea=" + stockArea 
			+ "&startYear=" + startYear 
			+ "&endYear=" + endYear 
			+ "&startMonth=" + startMonth 
			+ "&endMonth=" + endMonth 
			+ "&startTime=" + startTime 
			+ "&endTime=" + endTime;
	window.open("${pageContext.request.contextPath}/admin/bi/orderDetail.jsp?" + params,"_blank");
}

//将theStr的所有replaceStrA替换为replaceStrB
function replaceAllStr(theStr, replaceStrA, replaceStrB) 
{ 
   var re=new RegExp(replaceStrA, "g"); 
   var newstart = theStr.replace(re, replaceStrB); 
   return newstart;
} 

function loadProductDatas(type) {
	var stockArea = $("#stockArea").combobox("getValue");
	var startYear=$("#tb input[id=startYear]").val();
	var endYear=$("#tb input[id=endYear]").val();
	var startMonth=$("#tb input[id=startMonth]").val();
	var endMonth=$("#tb input[id=endMonth]").val();
	var startTime=$("#tb input[id=startTime]").datebox("getValue");
	var endTime=$("#tb input[id=endTime]").datebox("getValue");
	$.ajax({
		url : '${pageContext.request.contextPath}/BIController/getOrderComplete.mmx',
		type : 'post',
		data : {
			stockArea:stockArea,
			startYear : startYear,
			endYear : endYear,
			startMonth:startMonth,
			endMonth:endMonth,
			startTime : startTime,
			endTime : endTime,
			type : type
		},
		cache: false,
		dataType : "json",
		success: function(result){
			if (result.success) {
				if (type =='intradayComplete') {
					loadIntradayCharts(result.obj);
				} else if (type=='cutOff') {
					loadCutOffhCharts(result.obj);
				}
			} else {
				$.messager.show({
					msg : result.msg,
					title : '提示'
				});
				return false;
			}
		}
	});
};

function loadIntradayCharts(result){
	$('#tb div[id=cutOffTable]').hide();
	$('#tb div[id=productCountTable]').show();
	$('#tb div[id=productCountTable]').highcharts({
        title: {
            text: '',
            x: -20 //center
        },
        xAxis: {
        	title: {
                text: '日  期'
            } ,
			categories: result[0],
        },
        yAxis: [{ // Primary yAxis
            labels: {
                style: {
                    color: '#89A54E'
                }
            },
            title: {
                text: '订单量',
                style: {
                    color: '#89A54E'
                }
            }
        }, { // Secondary yAxis
            title: {
                text: '完成率',
                style: {
                    color: '#4572A7'
                }
            },
            labels: {
            	 format: '{value}%',
                style: {
                    color: '#4572A7'
                }
            },
            opposite: true
        }],
        tooltip: {
            shared: true
        },
        series: [{
			name: flag +'申请出库订单量',
			type: 'column',
			data: result[1]
		},{
			name: '订单' + flag +'发货量',
			type: 'column',
			data: result[2]
		},{
			name:  flag +'订单完成率',
			type: 'spline',
            yAxis: 1,
			data: result[3],
			tooltip: {
                valueSuffix: '%'
            }
		},{
			name:  '截单单量占比',
			type: 'spline',
            yAxis: 1,
			data: result[4],
			tooltip: {
                valueSuffix: '%'
            }
		}]
	});
}

function loadCutOffhCharts(result){
	$('#tb div[id=productCountTable]').hide();
	$('#tb div[id=cutOffTable]').show();
	$('#tb div[id=cutOffTable]').highcharts({
        title: {
            text: '',
            x: -20 //center
        },
        xAxis: {
        	title: {
                text: '日  期'
            } ,
			categories: result[0],
        },
        yAxis: [{ // Primary yAxis
            labels: {
                style: {
                    color: '#89A54E'
                }
            },
            title: {
                text: '订单量',
                style: {
                    color: '#89A54E'
                }
            }
        }, { // Secondary yAxis
            title: {
                text: '截单周期订单完成率',
                style: {
                    color: '#4572A7'
                }
            },
            labels: {
            	 format: '{value}%',
                style: {
                    color: '#4572A7'
                }
            },
            opposite: true
        }],
        tooltip: {
            shared: true
        },
        series: [{
			name:  flag +'截单周期申请出库订单量',
			type: 'column',
			data: result[1]
		},{
			name:  flag +'截单周期实际发货量',
			type: 'column',
			data: result[2]
		},{
			name:  flag +'截单周期订单完成率',
			type: 'spline',
            yAxis: 1,
			data: result[3],
			tooltip: {
                valueSuffix: '%'
            }
		},{
			name:  '截单单量占比',
			type: 'spline',
            yAxis: 1,
			data: result[4],
			tooltip: {
                valueSuffix: '%'
            }
		}]
	});
}
</script>
</head>
<body>
	<table id="intradayOrderCompleteDatagrid"></table>
	<div id="tb"  style="height: auto;">
		<input id="stockArea1" name="stockArea1"  type="hidden" />
		<input id="startYear1" name="startYear1" type="hidden" />
		<input id="endYear1" name="endYear1"  type="hidden" />
		<input id="startMonth1" name="startMonth1"  type="hidden" />
		<input id="endMonth1" name="endMonth1"  type="hidden" />
		<input id="startTime1" name="startTime1"  type="hidden"/>
		<input id="endTime1" name="endTime1"  type="hidden"/>
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th >订单完成项：</th>
					<td align="left" colspan="3">
						<input id="completeType" name="completeType" style="width: 116px;"/>
					</td>
					<th>仓库：</th>
					<td align="left">
						<input id="stockArea" name="stockArea" style="width: 116px;"/>
					</td>
				</tr>
				<tr align="center" >
					<th>年：</th>
					<td align="left"  colspan="3">
						<input id="startYear" name="startYear" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy'})" class="Wdate"/>
						--
						<input id="endYear" name="endYear" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy'})" class="Wdate"/>
					</td>
					<th>年月：</th>
					<td align="left"  colspan="3">
						<input id="startMonth" name="startMonth" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy-MM'})" class="Wdate"/>
						--
						<input id="endMonth" name="endMonth" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy-MM'})" class="Wdate"/>
					</td>
					<th>日期：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
					</td>
				</tr>
			</table>
			<table align="right">
				<tr>
					<td>
						<mmb:permit value="2117">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFun();">查询</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
			<div id="productCountTable" style="min-width:200px; height: 300px; margin: 0 auto;display:none"></div>
			<div id="cutOffTable" style="min-width:200px; height: 300px; margin: 0 auto;display:none"></div>
	</div>
</body>
</html>