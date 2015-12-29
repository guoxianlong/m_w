<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/highcharts.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/grid.js"></script>
<script src="${pageContext.request.contextPath}/admin/rec/js/highchart/exporting.src.js"></script>
<script type="text/javascript" charset="UTF-8">
$(function () {
	setDate();
	$('#area').combobox({
		url:'${pageContext.request.contextPath}/stockShareController/getDeptAreaComboBox.mmx',
      	valueField:'id',
		textField:'text' 
    });
	$('#area').combobox('setValue','-1');
	
	$.ajax({
		url : '${pageContext.request.contextPath}/stockShareController/getProductLines.mmx',
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
	var dateStart = date.getFullYear() + '-' + (date.getMonth()) + '-' + date.getDate();
	var dateEnd = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate();
	$('#dateStart').datebox('setValue',dateStart);
	$('#dateEnd').datebox('setValue',dateEnd);
}
function getCheckBoxValue(id) {
    var str="";
    $("[name="+id+"]:checkbox").each(function(){ 
        if($(this).attr("checked")){
            str += $(this).val()+","
        }
    })
    return str.substring(0, str.length-1);
};
function loadHicharts(){
	var area = $('#area').combobox('getValue');
	var searchType = $("input[type='radio'][name='searchType']:checked").val();
	var dateStart = $('#dateStart').datebox('getValue');
	var dateEnd = $('#dateEnd').datebox('getValue');
	var productLines = getCheckBoxValue("productLine");
	$.ajax({
		url : '${pageContext.request.contextPath}/stockShareController/qualifiedStockVolumeShareDetails.mmx',
		type : 'post',
		dataType : 'json',
		cache : 'false',
		data : {
			area : area,
			productLine : productLines,
			searchType : searchType,
			dateStart : dateStart,
			dateEnd : dateEnd
		},
		success : function(dd){
			var key = dd.obj.yAxis;
			var pname = dd.obj.productNameList;
			var sb = "[";
			var name = "";
			var i = 0;
			for (var prop in key) {
				sb = sb + "{name:'"+pname[i++]+"',";
				var data = "data:[";
			    for(var kk in key[prop]){
			    	data = data + key[prop][kk].share+",";
			    }
			    data = data.substring(0,data.length-1);
			    data = data +"]"
			    sb = sb +data+"},";
			}
			sb = sb.substring(0,sb.length-1);
			sb = sb+"]";
			$('#container').highcharts({
		        title: {
		            text: '合格库容积率',
		            x: -20 //center
		        },
		        xAxis: {
		            categories: dd.obj.xAxis
		        },
		        yAxis: {
		            title: {
		                text: '合格库容积率'
		            },
		            plotLines: [{
		                value: 0,
		                width: 1,
		                color: '#808080'
		            }]
		        },
		        tooltip: {
		            valueSuffix: '%'
		        },
		        legend: {
		            layout: 'horizontal',
		            align: 'center',
		            borderWidth: 0
		        },
		        series: eval('('+sb+')')
		    });
		}
	});
}
$(function(){
	$("#query").click(function(){
		if($("input[type='checkbox'][name='productLine']:checked").length<=0){
			$.messager.show({
				title:'提示',
				msg:'请选择至少一个产品线！',
				timeout:3000,
				showType:'slide'
			});
			return false;
		}
		loadHicharts();
	});
});
function checkAllProduct() {
	if ($("#all").attr("checked")){
       	$("[name = productLine]:checkbox").attr("checked", true);
	} else {
		$("[name = productLine]:checkbox").attr("checked", false);
	}
}
</script>
</head>
<body>
	<div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
	<br>
	<fieldset style="border : 1px solid #ff9900;text-align:left;FONT-SIZE: 12px;font-family: Verdana">
	<form method="post" id="form" action="<%=request.getContextPath()%>/stockShareController/stockShareDetails.mmx" >&nbsp;&nbsp;&nbsp;&nbsp;
		<table>
			<tr>
				<td>
					<span>产品线：</span>
				</td>
				<td colspan="3" id="productLines">
					<input type="checkbox" id="all" onclick="checkAllProduct();" name="productLineall"/>全选
				</td>
			</tr>
			<tr>
				<td><input name="area" style="width:80px;" class="easyui-combobox" editable="false" id="area"></td>
				<td><input name="dateStart" id="dateStart" type="text" editable="false" class="easyui-datebox">
				   --<input name="dateEnd" id="dateEnd" type="text" editable="false" class="easyui-datebox"></td>
				<td><input type="radio" name="searchType" value="1" checked="true">日
					<input type="radio" name="searchType" value="2" >周
					<input type="radio" name="searchType" value="3" >月</td>
				<td><a id="query" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a></td>
			</tr>
		</table>
	</form>
</fieldset>
</body>
</html>