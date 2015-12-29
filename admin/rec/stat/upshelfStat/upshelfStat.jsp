<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<title>上架效率</title>
<script type="text/javascript" charset="UTF-8">
var chart;
$(document).ready(function() {
	$.getJSON("${pageContext.request.contextPath}/CheckStockinStatController/getProductLine.mmx",null,function(response){ 
		  var listHtml='产品线：'; 
		//循环取json中的数据,并呈现在列表中 
		    $.each(response,function(i){ 
			    listHtml += " <input type='checkbox'"; 
			    listHtml += " name=upshelfStatProductLine"; 
			    listHtml += " id=upshelfStatProductLine"+response[i].id; 
			    if (response[i].id == -1) {
			    	listHtml += " onclick='checkAllProduct();' "
			    }
			    listHtml += " value='"+response[i].id+"'/>"; 
			    listHtml += response[i].text + "&nbsp;&nbsp;"; 
			    
			    if ((i+1)%14 == 0) {
			    	listHtml += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			    }
		    }); 
		    $("#upshelfStatProductLine").html(listHtml); 
		} 
	); 
	
	
	var upshelfStatWareArea = $('#upshelfStatWareArea').combobox({
		url : '${pageContext.request.contextPath}/CheckStockinStatController/getWareArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	
	var date = new Date()
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#upshelfStatEndTime').datebox('setValue',d);
	date.setMonth(date.getMonth()-1);
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#upshelfStatStartTime').datebox('setValue',d);
	
	$('#upshelfStatWareArea').combobox('setValue','-1');
	
	loadUpshelfStatDatas();
});

function checkAllProduct() {
	if ($("#upshelfStatProductLine-1").attr("checked")){
       	$("[name = upshelfStatProductLine]:checkbox").attr("checked", true);
	} else {
		$("[name = upshelfStatProductLine]:checkbox").attr("checked", false);
	}
}
function loadUpshelfStatDatas() {
	if (!$("#upshelfStatForm").form('validate')) {
		return false;
	}
	var upshelfStatProductLine = getCheckBoxValue('upshelfStatProductLine');
	var upshelfStatWareArea = $("#upshelfStatForm [id='upshelfStatWareArea']").combobox("getValue");
	var upshelfStatRadio = $("#upshelfStatForm [name='upshelfStatRadio'][type='radio']:checked").val();
	var upshelfStatStartTime = $("#upshelfStatForm [id='upshelfStatStartTime']").datebox("getValue");
	var upshelfStatEndTime = $("#upshelfStatForm [id='upshelfStatEndTime']").datebox("getValue");
	if (!checkDate(upshelfStatStartTime, upshelfStatEndTime)) {
		return false;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/CheckStockinStatController/getUpshelfStat.mmx',
		type : 'post',
		data : {
			productLine : upshelfStatProductLine,
			wareArea : upshelfStatWareArea,
			searchType : upshelfStatRadio,
			startTime : upshelfStatStartTime,
			endTime : upshelfStatEndTime,
			type : 'PRODUCT'
		},
		cache: false,
		dataType : "json",
		success: function(result){
			if (result != null) {
				loadUpshelfStatHighCharts(result);
			} else {
				loadUpshelfStatHighCharts(new Array(4));
			}
		}
	});
};

function checkDate(startTime,endTime){
	var nDay_ms = 24*60*60*1000;
	var reg = new RegExp("-","g");
	var startDay = new Date(startTime.replace(reg,'/'));
	var endDay = new Date(endTime.replace(reg,'/'));
	var nDifTime = endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		$.messager.alert('提示', '起始日期不能大于结束日期!', 'error');
    	return false;
	}
	return true;
}
//上架效率图表
function loadUpshelfStatHighCharts(result){
	$('#upshelfStatTable').highcharts({
        title: {
            text: '上架效率',
            x: -20 //center
        },
        xAxis: {
        	title: {
                text: '时  间'
            },
			categories: result[0],
        },
        yAxis: {
            title: {
                text: '数  量'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
					 this.series.name +': '+ this.y ;
			}
		},
        legend: {
            layout: 'horizontal',
            align: 'center',
            borderWidth: 0
        },
        series: [{
			name: '上架单数',
			data: result[1]
		},{
			name: '商品件数',
			data: result[2]
		},{
			name: 'SKU数',
			data: result[3]
		}]
	});
}
//获取checkbox值
function getCheckBoxValue(id) {
    var str="";
    $("[name="+id+"]:checkbox").each(function(){ 
        if($(this).attr("checked")){
            str += $(this).val()+","
        }
    })
    return str.substring(0, str.length-1);
};

</script>
</head>
<body>
    <div title="上架效率" style="padding:10px;font-size:12px;" >
	    <div id="upshelfStat" style="padding:3px;height: auto;">
			<fieldset>
			<legend>统计图表</legend>
				<div id="upshelfStatTable" style="min-width:310px; height: 400px; margin: 0 auto"></div>
			</fieldset>
			<form id="upshelfStatForm" method="post">
				<fieldset>
					<legend>筛选</legend>
					<div align="left">
						<div id="upshelfStatProductLine"></div><br><br>
						<input id="upshelfStatWareArea" name="upshelfStatWareArea" />
						<input id="upshelfStatStartTime" name="upshelfStatStartTime" class="easyui-datebox" data-options="required:true"/>
						--
						<input id="upshelfStatEndTime" name="upshelfStatEndTime" class="easyui-datebox" data-options="required:true"/>
						<input type="radio"  name="upshelfStatRadio" value="day" checked="checked" />日
						<input type="radio"  name="upshelfStatRadio" value="week"/>周
						<input type="radio"  name="upshelfStatRadio" value="month"/>月
						<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="loadUpshelfStatDatas();">查询</a>
					</div>
				</fieldset>
			</form>
		</div>    
    </div>    
</body>
</html>