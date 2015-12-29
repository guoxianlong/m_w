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
var datagrid;
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
});

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
	$("#productCountTable").show();
	loadInfo();
	loadProductDatas();
}

function loadInfo() {
	$.ajax({
		url:'${pageContext.request.contextPath}/BIController/getInStockAgingDatagrid.mmx',
		data : {
			stockArea:$("#tb input[id=stockArea]").combobox("getValue"),
			startYear:$("#tb input[id=startYear]").val(),
			endYear:$("#tb input[id=endYear]").val(),
			startMonth:$("#tb input[id=startMonth]").val(),
			endMonth:$("#tb input[id=endMonth]").val(),
			startTime:$("#tb input[id=startTime]").datebox("getValue"),
			endTime:$("#tb input[id=endTime]").datebox("getValue")
	    },
		dataType : 'json',
		cache : false,
		success : function(result) {
			try {
				var data = result;
				var columns =new Array();
				columns[0]= new Array();
				columns[0].field='createDate';
				columns[0].title= '日期';
				columns[0].width= document.body.clientWidth * 0.2;
				columns[0].align='center';
				var x= 1;
				for (i in data[0]) {
					 (function(){
						var t=i;
						columns[x]= new Array();
						columns[x].field='inStockAging' + i;
						columns[x].title= data[0][i].type + '小时';
						columns[x].width= document.body.clientWidth * 0.16;
						columns[x].align='center';
						columns[x].formatter = function(value,row, index) {
								return '<a href="javascript:void(0);" onclick="toOrderInStockDetail(\''+t+'\',\'' + row.createDate + '\')">'+ value + '</a>';
						};
					 })();  
					 x++;
				}
 				//处理返回结果，并显示数据表格
			    var options = {
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
						columns : [columns]
			    };
 				datagrid = $("#inStockAgingDatagrid");
			    datagrid.datagrid(options);//根据配置选项，生成datagrid
			    datagrid.datagrid("loadData", data[1]); //载入本地json格式的数据
			    datagrid.datagrid("reloadFooter", data[2]);
			} catch (e) {
			}
		}
	});
}

function orderInStockDetail(value,row,index) {
	return '<a href="javascript:void(0);" onclick="toOrderInStockDetail(\'-1\',\'' + row.createDate + '\')">'+ value + '</a>';
}

//将theStr的所有replaceStrA替换为replaceStrB
function replaceAllStr(theStr, replaceStrA, replaceStrB) 
{ 
   var re=new RegExp(replaceStrA, "g"); 
   var newstart = theStr.replace(re, replaceStrB); 
   return newstart;
} 

function toOrderInStockDetail(type, datex) {
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
	window.open("${pageContext.request.contextPath}/admin/bi/orderInStockDetail.jsp?" + params,"_blank");
}

function checkSubmit() {
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

function loadProductDatas(type) {
	var stockArea = $("#stockArea").combobox("getValue");
	var startYear=$("#tb input[id=startYear]").val();
	var endYear=$("#tb input[id=endYear]").val();
	var startMonth=$("#tb input[id=startMonth]").val();
	var endMonth=$("#tb input[id=endMonth]").val();
	var startTime=$("#tb input[id=startTime]").datebox("getValue");
	var endTime=$("#tb input[id=endTime]").datebox("getValue");
	$.ajax({
		url : '${pageContext.request.contextPath}/BIController/getInStockAgingChart.mmx',
		type : 'post',
		data : {
			stockArea:stockArea,
			startYear : startYear,
			endYear : endYear,
			startMonth:startMonth,
			endMonth:endMonth,
			startTime : startTime,
			endTime : endTime
		},
		cache: false,
		dataType : "json",
		success: function(result){
			if (result.success) {
				loadProductHighCharts(result.obj);
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

function loadProductHighCharts(result){
	$('#productCountTable').highcharts({
        title: {
            text: '',
            x: -20 //center
        },
        xAxis: {
        	title: {
                text: '在库时长'
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
                text: '占比',
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
			name: '订单量',
			type: 'column',
			data: result[1]
		},{
			name:  '占比',
			type: 'spline',
            yAxis: 1,
			data: result[2],
			tooltip: {
                valueSuffix: '%'
            }
		}]
	});
}
</script>
</head>
<body>
	<table id="inStockAgingDatagrid"></table> 
	<div id="tb"  style="height: auto">
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
					<th>仓库：</th>
					<td align="left">
						<input id="stockArea" name="stockArea" style="width: 116px;"/>
					</td>
				</tr>
				<tr>
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
					<td>
						<mmb:permit value="2123">
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFun();">查询</a>
						</mmb:permit>
					</td>
				</tr>
			</table>
		</fieldset>
		<div id="productCountTable" style="min-width:310px; height: 300px; margin: 0 auto;display:none"></div>
	</div>
</body>
</html>