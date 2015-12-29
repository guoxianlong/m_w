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
<title>当日订单完成率统计</title>
<script type="text/javascript" charset="UTF-8">
var flag = '日';
$(document).ready(function() {
	var yesterday = getEod();
	$("#startTime").datebox("setValue",yesterday);
	$("#endTime").datebox("setValue",yesterday);
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

function checkSubmit() {
	if($("#productCountForm input[id=completeType]").combobox("getValue") == '') {
		$.messager.show({
			msg : "请选择订单完成项！",
			title : '提示'
		});
		return false;
	}
	var startYear=$("#productCountForm input[id=startYear]").val();
	var endYear=$("#productCountForm input[id=endYear]").val();
	var startMonth=$("#productCountForm input[id=startMonth]").val();
	var endMonth=$("#productCountForm input[id=endMonth]").val();
	var startTime=$("#productCountForm input[id=startTime]").datebox("getValue");
	var endTime=$("#productCountForm input[id=endTime]").datebox("getValue");
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

function searchFun() {
	if (!checkSubmit()) {
		return false;
	}
	if ($("#productCountForm input[id=completeType]").combobox("getValue") =='intraday') {
		loadProductDatas('intradayComplete');
	} else if ($("#productCountForm input[id=completeType]").combobox("getValue") =='cutOff' ){
		loadProductDatas('cutOff');
	}
}

function loadProductDatas(type) {
	if (!checkSubmit()) {
		return false;
	}
	var startYear=$("#productCountForm input[id=startYear]").val();
	var endYear=$("#productCountForm input[id=endYear]").val();
	var startMonth=$("#productCountForm input[id=startMonth]").val();
	var endMonth=$("#productCountForm input[id=endMonth]").val();
	var startTime=$("#productCountForm input[id=startTime]").datebox("getValue");
	var endTime=$("#productCountForm input[id=endTime]").datebox("getValue");
	$.ajax({
		url : '${pageContext.request.contextPath}/BIController/getOrderCompletePercent.mmx',
		type : 'post',
		data : {
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
					if (result.obj != null) {
						loadIntradayCharts(result.obj);
					} else {
						loadIntradayCharts(new Array(3));
					}
				} else if (type=='cutOff') {
					if (result.obj != null) {
						loadCutOffCharts(result.obj);
					} else {
						loadCutOffCharts(new Array(3));
					}
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
	var options = {
	    title: {
	        text: '',
	        x: -20 //center
	    },
	    xAxis: {
	    	title: {
	            text: '日  期'
	        } ,
			categories: result[1],
	    },
	    yAxis: [ { 
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
	    series: []
	}
	options.seriest = new Array();
	if (result[0] != undefined) {
		for (var i = 0 ; i< result[0].length; i ++) {
			options.series[i] = new Object();
	        options.series[i].name = result[0][i];
	        options.series[i].type = 'spline';
	        options.series[i].data =  result[i + 2]==undefined?'[]':result[i+2] ;
	        options.series[i].tooltip = {valuePrefix:flag+'订单完成率',valueSuffix: '%'};
		}
	}
	$('#cutOffTable').hide();
	$('#intradayTable').show();
	$('#intradayTable').highcharts(options);
}

function loadCutOffCharts(result){
	
	var options = {
        title: {
            text: '',
            x: -20 //center
        },
        xAxis: {
        	title: {
                text: '日  期'
            } ,
			categories: result[1],
        },
        yAxis: [ { 
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
        series: []
    }
    options.seriest = new Array();
	if (result[0] != undefined) {
		for (var i = 0 ; i< result[0].length; i ++) {
			options.series[i] = new Object();
            options.series[i].name = result[0][i];
            options.series[i].type = 'spline';
            options.series[i].data =  result[i + 2]==undefined?'[]':result[i+2] ;
            options.series[i].tooltip = {valuePrefix:flag+'截单周期完成率',valueSuffix: '%'};
		}
	}
	$('#intradayTable').hide();
	$('#cutOffTable').show();
	$('#cutOffTable').highcharts(options);
}
</script>
</head>
<body>
    <div  style="padding:10px;" >
	    <div id="productCount" style="padding:3px;height: auto;">
			<form id="productCountForm" method="post">
				<fieldset>
					<table class="tableForm">
						<tr align="center" >
							<th >订单完成项：</th>
							<td align="left" colspan="3">
								<input id="completeType" name="completeType" style="width: 116px;"/>
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
							<td>
								<mmb:permit value="2118">
								<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="searchFun();">查询</a>
								</mmb:permit>
							</td>
						</tr>
					</table>
				</fieldset>
			</form>
			<div id="intradayTable" style="min-width:310px; height: 400px; margin: 0 auto;display:none"></div>
			<div id="cutOffTable" style="min-width:310px; height: 400px; margin: 0 auto;display:none"></div>
		</div>    
    </div>    
</body>
</html>