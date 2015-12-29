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
<title>单均在库处理时效统计（时间段）</title>
<script type="text/javascript" charset="UTF-8">
var flag = '日';
$(document).ready(function() {
	var productCountWareArea = $('#stockArea').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBIStockArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	var yesterday = getEod();
	$("#startTime").datebox("setValue",yesterday);
	$("#endTime").datebox("setValue",yesterday);
	loadProductDatas();
	
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

function loadProductDatas() {
	if (!checkSubmit()) {
		return false;
	}
	var stockArea = $("#stockArea").combobox("getValue");
	var startYear=$("#productCountForm input[id=startYear]").val();
	var endYear=$("#productCountForm input[id=endYear]").val();
	var startMonth=$("#productCountForm input[id=startMonth]").val();
	var endMonth=$("#productCountForm input[id=endMonth]").val();
	var startTime=$("#productCountForm input[id=startTime]").datebox("getValue");
	var endTime=$("#productCountForm input[id=endTime]").datebox("getValue");
	$.ajax({
		url : '${pageContext.request.contextPath}/BIController/getInStockDurationHourChart.mmx',
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
            text: '单均在库处理时效',
            x: -20 //center
        },
        xAxis: {
        	title: {
                text: '时  间'
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
                text: '在库时长',
                style: {
                    color: '#89A54E'
                }
            }
        }],
        tooltip: {
            shared: true
        },
        series: [{
			name:  '单均在库时长',
			type: 'spline',
			data: result[1],
			tooltip: {
                valueSuffix: '小时'
            }
		}]
	});
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
							<mmb:permit value="2121">
							<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="loadProductDatas();">查询</a>
							</mmb:permit>
						</td>
					</tr>
				</table>
			</fieldset>
			</form>
			<fieldset>
			<legend>统计图表</legend>
				<div id="productCountTable" style="min-width:310px; height: 400px; margin: 0 auto"></div>
			</fieldset>
		</div>    
    </div>    
</body>
</html>