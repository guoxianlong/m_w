<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.order.OrderStockBean" %>
<!DOCTYPE html>
<html>
<head>
<title>差异量详情</title>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>
<script type="text/javascript" >
var startTimes = '';
var endTimes = '';
function setDate(){
	var date = new Date()
	var startMonth = getTwoL(date.getMonth()+1);
	var startDay = getTwoL(date.getDate());
	endTimes = date.getFullYear() + '-' + startMonth  + '-' + startDay;
	var startDate = getDate(7);
	var endMonth = getTwoL(startDate.getMonth() + 1);
	var endDay = getTwoL(startDate.getDate());
	startTimes = startDate.getFullYear() + '-'+ endMonth +'-'+ endDay;
	$('#endTime').datebox('setValue',endTimes);
	$('#startTime').datebox('setValue',startTimes);
	//$("#datagrid").datagrid('load', {startTime : startTimes, endTime:endTimes});
}
function getTwoL( source ) {
	var target = parseInt(source,10);
	if( target < 10 ) {
		target = '0'+target;
	}
	return target;
}

function getDate(day){
    var zdate=new Date();
    var sdate=zdate.getTime();
    var milis = day*24*60*60*1000;
    var edate=new Date(sdate-milis);
    return edate;
}
var first = 0;
$(document).ready(function(){
	if( first == 0 ) {
		setDate();
		first+=1;
	}
	$('#area').combobox({
      	url : '<%=request.getContextPath()%>/dCheckController/getDeptAreaComboBox.mmx',
      	valueField:'id',
		textField:'text',
		onSelect: function(rec) {
			$('#stockArea').combobox({   
				url:'<%=request.getContextPath()%>/dCheckController/getStockAreaForArea.mmx?areaId='+rec['id'],  
				valueField : 'id',   
				textField : 'text',
			    editable:false
		});
		}
    });
	$('#area2').combobox({
      	url : '<%=request.getContextPath()%>/dCheckController/getDeptAreaComboBox.mmx',
      	valueField:'id',
		textField:'text'
    });
    $('#stockArea').combobox({   
			url:'<%=request.getContextPath()%>/dCheckController/getStockAreaForArea.mmx?areaId=-1',  
			valueField : 'id',   
			textField : 'text',
		    editable:false
	});
	$('#datagrid').datagrid({    
		url:"<%= request.getContextPath()%>/dCheckController/getDCheckCargoDifferenceDatagrid.mmx",
		nowrap:false,
		border:false,
		idField:"id",
		fit:true,
		fitColumns:true,
		title:"",
		pageSize :20,
		pageList:[ 20, 50,100, 200, 300 ],
		toolbar:"#tb", 
		rownumbers:true,
		pagination:true,
		singleSelect:true,
		queryParams: {
			startTime : startTimes, endTime:endTimes
		},
	    rowStyler:function(index,row){    
	        return 'color:black;font-weight:bold'; 
	    },
	    onLoadSuccess: function(data) {
    		if( data['tip'] != null ) {
	    		jQuery.messager.alert("提示", data['tip']);
	    	}
	    },
	    
	    columns:[[
		{field:'areaName',title:'库地区', rowspan:2,width:80,align:'center',sortable:true},   
        {field:'cargoWholeCode',title:'货位号',rowspan:2,width:80,align:'center',sortable:true},   
        {field:'productCode',title:'产品编号',rowspan:2,width:80,align:'center'},
        {field:'productName',title:'商品原名称',rowspan:2,width:170,align:'center'},
        {field:'difference',title:'差异量',rowspan:1,width:70,align:'center'}
	]]  
	});
});


function searchFun() {
	$('#datagrid').datagrid('load',{
		area : $('#tb input[name=area]').val(),
		stockArea : $('#tb input[name=stockArea]').val(),
		cargoCode : $('#tb input[name=cargoCode]').val(),
		productCode : $('#tb input[name=productCode]').val()
	});
}

function clearFun(){
	$('#tb input').val('');
	//$('#datagrid').datagrid('load',{});
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;">
		<fieldset>
		<legend>筛选</legend>
			<div align="left">
				<input id="area" name="area" style="width:152px;border:1px solid #ccc" editable="false"/>
				&nbsp;&nbsp;&nbsp;
				<input id="stockArea" name="stockArea" style="width:152px;border:1px solid #ccc" editable="false"/>
				&nbsp;&nbsp;&nbsp;
				货位号：<input name="cargoCode" />
				&nbsp;&nbsp;&nbsp;
				产品编号：<input name="productCode" />
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();">查询 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();">清空 </a>
			</div>
		</fieldset>
		<br/>
		&nbsp;&nbsp;&nbsp;
		<input id="area2" name="area2" editable="false"/>
		&nbsp;&nbsp;&nbsp;
		<a href="javascript:disposeCargoCheck();" class="easyui-linkbutton" iconCls="icon-reload" >手动平账</a>
		&nbsp;&nbsp;&nbsp;
	    <a href="javascript:generateBsby();" class="easyui-linkbutton" iconCls="icon-add" >生成报损报溢</a>   
		<br/>
		<br/>
	</div>    
    <table id="datagrid" style="height:auto;width:auto;"> 
</table>

<script type="text/javascript">
	function disposeCargoCheck() {
		var area = $("#area2").combobox("getValue");
		$.post("<%= request.getContextPath()%>/dCheckController/disposeCargoCheckManual.mmx?area="+area, {}, function(data) {
			$.messager.alert("提示", data['tip'],'tip', function () {
             	$("#datagrid").datagrid("reload");
			});  
		}, "json");
	}
	
	function generateBsby() {
		var area = $("#area2").combobox("getValue");
		$.post("<%= request.getContextPath()%>/dCheckController/generateBsby.mmx?area="+area, {}, function(data) {
			$.messager.alert("提示", data['tip'],'tip', function () {
				$("#datagrid").datagrid("reload");
			});  
		}, "json");
	}

</script>
</body>
</html>