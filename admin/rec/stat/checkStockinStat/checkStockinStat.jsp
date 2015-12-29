<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<title>商品入库统计</title>
<script type="text/javascript" charset="UTF-8">
var chart;
$(document).ready(function() {
	$.getJSON("${pageContext.request.contextPath}/CheckStockinStatController/getProductLine.mmx",null,function(response){ 
		  var listHtml='产品线：'; 
		  var SKUlistHtml='产品线：';
		//循环取json中的数据,并呈现在列表中 
		    $.each(response,function(i){ 
			    listHtml += " <input type='checkbox'"; 
			    listHtml += " name=productCountProductLine"; 
			    listHtml += " id=productCountProductLine"+response[i].id; 
			    if (response[i].id == -1) {
			    	listHtml += " onclick='checkAllProduct();' "
			    }
			    listHtml += " value='"+response[i].id+"'/>"; 
			    listHtml += response[i].text + "&nbsp;&nbsp;"; 
			    
			    SKUlistHtml += " <input type='checkbox'"; 
			    SKUlistHtml += " name=SKUCountProductLine"; 
			    SKUlistHtml += " id=SKUCountProductLine"+response[i].id; 
			    if (response[i].id == -1) {
			    	SKUlistHtml += " onclick='checkAllSKU();' "
			    }
			    SKUlistHtml += " value='"+response[i].id+"'/>"; 
			    SKUlistHtml += response[i].text + "&nbsp;&nbsp;"; 
			    if ((i+1)%14 == 0) {
			    	listHtml += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			    	SKUlistHtml += "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
			    }
		    }); 
		    $("#productCountProductLine").html(listHtml); 
		    $("#SKUCountProductLine").html(SKUlistHtml);
		} 
	); 
	
	
	var productCountWareArea = $('#productCountWareArea').combobox({
		url : '${pageContext.request.contextPath}/CheckStockinStatController/getWareArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	
	var SKUCountWareArea = $('#SKUCountWareArea').combobox({
		url : '${pageContext.request.contextPath}/CheckStockinStatController/getWareArea.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	var date = new Date()
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#productCountEndTime').datebox('setValue',d);
	$('#SKUCountEndTime').datebox('setValue',d);
	date.setMonth(date.getMonth()-1);
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#productCountStartTime').datebox('setValue',d);
	$('#SKUCountStartTime').datebox('setValue',d);
	
	$('#SKUCountWareArea').combobox('setValue','-1');
	$('#productCountWareArea').combobox('setValue','-1');
	
	var tab = $('#tt').tabs('getSelected');  
	var title = tab.panel('options').title;
	$('#tt').tabs({  
	    border:false,  
	    onSelect:function(title){  
	        if(title == '商品件数'){
	        	loadProductDatas();
	        }else if(title == 'SKU'){
	        	loadSKUDatas();
	        }
	    }  
	}); 
});

function checkAllProduct() {
	if ($("#productCountProductLine-1").attr("checked")){
       	$("[name = productCountProductLine]:checkbox").attr("checked", true);
	} else {
		$("[name = productCountProductLine]:checkbox").attr("checked", false);
	}
}
function loadProductDatas() {
	if (!$("#productCountForm").form('validate')) {
		return false;
	}
	var productCountProductLine = getCheckBoxValue('productCountProductLine');
	var productCountWareArea = $("#productCountForm [id='productCountWareArea']").combobox("getValue");
	var productCountRadio = $("#productCountForm [name='productCountRadio'][type='radio']:checked").val();
	var productCountStartTime = $("#productCountForm [id='productCountStartTime']").datebox("getValue");
	var productCountEndTime = $("#productCountForm [id='productCountEndTime']").datebox("getValue");
	if (!checkDate(productCountStartTime, productCountEndTime)) {
		return false;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/CheckStockinStatController/getCountDatas.mmx',
		type : 'post',
		data : {
			productLine : productCountProductLine,
			wareArea : productCountWareArea,
			searchType : productCountRadio,
			startTime : productCountStartTime,
			endTime : productCountEndTime,
			type : 'PRODUCT'
		},
		cache: false,
		dataType : "json",
		success: function(result){
			if (result != null) {
				loadProductHighCharts(result);
			} else {
				loadProductHighCharts(new Array(3));
			}
		}
	});
};
//商品件数图表
function loadProductHighCharts(result){
	$('#productCountTable').highcharts({
        title: {
            text: '商品件数',
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
			name: '收货质检',
			data: result[1]
		},{
			name: '上架',
			data: result[2]
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

function checkAllSKU() {
	if ($("#SKUCountProductLine-1").attr("checked")){
       	$("[name = SKUCountProductLine]:checkbox").attr("checked", true);
	} else {
		$("[name = SKUCountProductLine]:checkbox").attr("checked", false);
	}
}
function loadSKUDatas() {
	if (!$("#SKUCountForm").form('validate')) {
		return false;
	}
	var SKUCountProductLine = getCheckBoxValue('SKUCountProductLine');
	var SKUCountWareArea = $("#SKUCountForm [id='SKUCountWareArea']").combobox("getValue");
	var SKUCountRadio = $("#SKUCountForm [name='SKUCountRadio'][type='radio']:checked").val();
	var SKUCountStartTime = $("#SKUCountForm [id='SKUCountStartTime']").datebox("getValue");
	var SKUCountEndTime = $("#SKUCountForm [id='SKUCountEndTime']").datebox("getValue");
	if (!checkDate(SKUCountStartTime, SKUCountEndTime)) {
		return false;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/CheckStockinStatController/getCountDatas.mmx',
		type : 'post',
		data : {
			productLine : SKUCountProductLine,
			wareArea : SKUCountWareArea,
			searchType : SKUCountRadio,
			startTime : SKUCountStartTime,
			endTime : SKUCountEndTime,
			type : 'SKU'
		},
		cache: false,
		dataType : "json",
		success: function(result){
			if (result != null) {
				loadSKUHighCharts(result);
			} else {
				loadSKUHighCharts(new Array(3));
			}
		}
	});
};
//SKU图表
function loadSKUHighCharts(result){
	$('#SKUCountTable').highcharts({
        title: {
            text: 'SKU',
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
			name: '收货质检',
			data: result[1]
		},{
			name: '上架',
			data: result[2]
		}]
	});
}

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
</script>
</head>
<body>
	<div id="tt" class="easyui-tabs" style="height: 850px">    
	    <div title="商品件数" style="padding:10px;" >
		    <div id="productCount" style="padding:3px;height: auto;">
				<fieldset>
				<legend>统计图表</legend>
					<div id="productCountTable" style="min-width:310px; height: 400px; margin: 0 auto"></div>
				</fieldset>
				<form id="productCountForm" method="post">
					<fieldset>
						<legend>筛选</legend>
						<div align="left">
							<div id="productCountProductLine"></div><br><br>
							<input id="productCountWareArea" name="productCountWareArea" />
							<input id="productCountStartTime" name="productCountStartTime" class="easyui-datebox" data-options="required:true"/>
							--
							<input id="productCountEndTime" name="productCountEndTime" class="easyui-datebox" data-options="required:true"/>
							<input type="radio"  name="productCountRadio" value="day" checked="checked" />日
							<input type="radio"  name="productCountRadio" value="week"/>周
							<input type="radio"  name="productCountRadio" value="month"/>月
							<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="loadProductDatas();">查询</a>
						</div>
					</fieldset>
				</form>
			</div>    
	    </div>    
	    <div title="SKU" style="padding:10px;" >    
	        <div id="SKUCount" style="padding:3px;height: auto;">
				<fieldset>
				<legend>统计图表</legend>
					<div id="SKUCountTable" style="min-width:310px; height: 400px; margin: 0 auto"></div>
				</fieldset>
				<form id="SKUCountForm" method="post">
					<fieldset>
						<legend>筛选</legend>
						<div align="left">
							<div id="SKUCountProductLine"></div><br><br>
							<input id="SKUCountWareArea" name="SKUCountWareArea" />
							<input id="SKUCountStartTime" name="SKUCountStartTime" class="easyui-datebox" data-options="required:true"/>
							--
							<input id="SKUCountEndTime" name="SKUCountEndTime" class="easyui-datebox" data-options="required:true"/>
							<input type="radio" name="SKUCountRadio" value="day" checked="checked"/>日
							<input type="radio" name="SKUCountRadio" value="week"/>周
							<input type="radio" name="SKUCountRadio" value="month"/>月
							<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="loadSKUDatas();">查询</a>
						</div>
					</fieldset>
				</form>
			</div>
	    </div>    
    </div> 
</body>
</html>