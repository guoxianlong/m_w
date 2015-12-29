<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<script type="text/javascript" charset="UTF-8">
var flag = true;
$(function () {
	setDate();
	$('#area').combobox({
      	url : '${pageContext.request.contextPath}/SendOutOrderStatController/getDeptAreaComboBox.mmx',
      	valueField:'id',
		textField:'text' 
    });
	$('#area').combobox('setValue','-1');
	$.ajax({
		url : '${pageContext.request.contextPath}/SendOutOrderStatController/getProductLines.mmx',
		type : 'post',
		dataType : 'json',
		cache : false,
		success : function(d){
			$('#productLines').append(d.obj);
		}
	});
	loadHicharts();
});
function setDate(){
	var date = new Date()
	var startDate = date.getFullYear() + '-' + (date.getMonth()) + '-' + date.getDate()
	var endDate = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#startDate').datebox('setValue',startDate);
	$('#endDate').datebox('setValue',endDate);
}
function loadHicharts(){
	var area = $('#area').combobox('getValue');
	var chk_value =[];    
	  $('input[name="productLine"]:checked').each(function(){  
		  if($(this).val() != '-1'){
			  chk_value.push($(this).val());  
		  }
	  });    
	var productLines = chk_value.join(',');
	var searchType = $('input:radio[name="searchType"]:checked').val();
	var startDate = $('#startDate').datebox('getValue');
	var endDate = $('#endDate').datebox('getValue');
	if(!checkDate(startDate,endDate)){
		return;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/SendOutOrderStatController/sendOutOrderStat.mmx',
		type : 'post',
		dataType : 'json',
		cache : 'false',
		data : {
			area : area,
			productLines : productLines,
			searchType : searchType,
			startDate : startDate,
			endDate : endDate
		},
		success : function(d){
			$('#container').highcharts({
		        title: {
		            text: '发货订单',
		            x: -20 //center
		        },
		        xAxis: {
		            categories: d.obj.dates
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
		            valueSuffix: '个'
		        },
		        legend: {
		            layout: 'horizontal',
		            align: 'center',
		            borderWidth: 0
		        },
		        series: [{
		            name: '商品',
		            data: d.obj.productCount
		        }, {
		            name: '订单',
		            data: d.obj.orderCount
		        }, {
		            name: 'sku',
		            data: d.obj.skuCount
		        }]
		    });
		}
	});
}
function checkDate(startTime,endTime){
	if(startTime == '' || endTime == ''){
		$.messager.alert('提示', '开始日期和结束日期都不能为空!', 'error');
    	return false;
	}
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
function checkAll() {
	 if(flag){
		 $("[name = productLine]:checkbox").attr("checked", true);
		 flag = false;
	 }else{
		 $("[name = productLine]:checkbox").attr("checked", false);
		 flag = true;
	 }
 };
</script>
</head>
<body>
	<div id="container" style="min-width: 310px; height: 410px; margin: 0 auto"></div>
	<fieldset>
		<legend>筛选</legend>
			<div align="left">
				<div id="productLines"></div>
				<select id="area" style="width:70px;" editable="false"></select>&nbsp;&nbsp;
				<input id="startDate" name="startDate" class="easyui-datebox" required="required" editable="false"/> --
				<input id="endDate" name="endDate" class="easyui-datebox" required="required" editable="false"/>
				<input type="radio" name="searchType" value="day" checked="checked">日  
				<input type="radio" name="searchType" value="week">周  
				<input type="radio" name="searchType" value="month">月 &nbsp;&nbsp;
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="flase" onclick="loadHicharts()">查 询 </a>
			</div>
	</fieldset>
</body>
</html>