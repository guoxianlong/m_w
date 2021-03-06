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
<title>在库时效分析统计</title>
<script type="text/javascript" charset="UTF-8">
$(function(){
	$('#stockArea').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBIStockArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
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
	var stockArea = $("#productCountForm input[id=stockArea]").combobox('getValue'); 
	var startYear=$("#productCountForm input[id=startYear]").val();
	var startMonth=$("#productCountForm input[id=startMonth]").val();
	var startTime=$("#productCountForm input[id=startTime]").datebox("getValue");
	var endTime=$("#productCountForm input[id=endTime]").datebox("getValue");
	if($.trim(startYear)!=""){
		return true;
	}
	if($.trim(startMonth)!=""){
		return true;
	}
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
	if ($.trim(startYear) != "") {
		return true;
	}
	$.messager.show({
		msg : "请输入时间区间作为查询条件！",
		title : '提示'
	});
	return false;
}

function loadProductDatas(type) {
	if (!checkSubmit()) {
		return false;
	}
	var stockArea = $("#stockArea").combobox("getValue");
	var startYear=$("#productCountForm input[id=startYear]").val();
	var startMonth=$("#productCountForm input[id=startMonth]").val();
	var startTime=$("#productCountForm input[id=startTime]").datebox("getValue");
	var endTime=$("#productCountForm input[id=endTime]").datebox("getValue");
	$.ajax({
		url : '${pageContext.request.contextPath}/BIController/getBiSplitOrderCharts.mmx',
		type : 'post',
		data : {
			stockArea:stockArea,
			startYear : startYear,
			startMonth:startMonth,
			startTime : startTime,
			endTime : endTime,
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
                text: '拆单数据'
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
                text: '订单数量',
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
			name: '拆单量',
			type: 'column',
			data: result[3]
		},{
			name:  '拆单率',
			type: 'spline',
            yAxis: 1,
			data: result[4],
			tooltip: {
                valueSuffix: '%'
            }
		}]
	});
	$('#productCountTable1').highcharts({
        title: {
            text: '',
            x: -20 //center
        },
        xAxis: {
        	title: {
                text: '越仓发货数据'
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
                text: '订单数量',
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
			name: '越仓发货单量',
			type: 'column',
			data: result[1]
		},{
			name:  '越仓发货占比',
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
    <div  style="padding:10px;" >
	    <div id="productCount" style="padding:3px;height: auto;">
			<fieldset>
			<legend>统计图表</legend>
			<form id="productCountForm" method="post">
				<table class="tableForm">
					<tr align="center" >
						<th>仓库：</th>
						<td align="left">
							<input id="stockArea" name="stockArea" style="width: 116px;"/>
						</td>
						<th>年：</th>
						<td align="left"  colspan="3">
							<input id="startYear" name="startYear" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy'})" class="Wdate"/>
						</td>
						<th>年月：</th>
						<td align="left"  colspan="3">
							<input id="startMonth" name="startMonth" style="width:116px" onfocus="WdatePicker({skin:'default',dateFmt:'yyyy-MM'})" class="Wdate"/>
						</td>
						<th>日期：</th>
						<td align="left"  colspan="3">
							<input id="startTime" name="startTime" style="width:116px" class="easyui-datebox"/>
							--
							<input id="endTime" name="endTime" style="width:116px" class="easyui-datebox"/>
						</td>
						<td>
							<mmb:permit value="2130">
							<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="loadProductDatas();">查询</a>
							</mmb:permit>
						</td>
					</tr>
				</table>
			</form>
			</fieldset>
			<table>
			<tr>
			<td><div id="productCountTable" style="min-width:310px; height: 400px; width: 600px; margin: 0 auto"></div></td>
			<td><div id="productCountTable1" style="min-width:310px; height: 400px; width: 600px;margin: 0 auto"></div></td>
			</tr>
			</table>
		</div>    
    </div>    
</body>
</html>