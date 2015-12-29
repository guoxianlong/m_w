<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<script type="text/javascript" src="http://www.jeasyui.com/easyui/datagrid-detailview.js"></script>  
<script type="text/javascript" charset="UTF-8">
$(function(){
	setDate();
	$('#area').combobox({
      	url : '${pageContext.request.contextPath}/SortingAbnormalDisposeController/getDeptAreaComboBox.mmx',
      	valueField:'id',
		textField:'text' 
    });
	$('#area').combobox('setValue',3);
	$('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/SortingAbnormalDisposeController/getSortingAbnormalDatagrid.mmx',
		toolbar : '#tb',
		nowrap : false,
		border : false,
		idField : 'id',
		fit : true,
		fitColumns : false,
		title : '',
		pagination : true,
		pageSize : 20,
		pageList : [10, 20, 30, 40, 50, 60, 70, 80, 90, 100],
		rownumbers : true,
		singleSelect : true,
		view: detailview,    
	    detailFormatter:function(index,row){    
	        return '<div style="padding:2px"><table id="ddv-' + index + '"></table></div>';    
	    },    
	    onExpandRow: function(index,row){    
	        $('#ddv-'+index).datagrid({    
	            url:'${pageContext.request.contextPath}/SortingAbnormalDisposeController/getSortingAbnormalProductDatagrid.mmx?saId='+row.id,    
	            fitColumns:true,    
	            singleSelect:true,    
	            rownumbers:true,    
	            loadMsg:'',    
	            height:'auto',    
	            columns:[[    
	                      {field:'productCode',title:'产品编号',width:150,align:'center'},  
	    		          {field:'cargoWholeCode',title:'货位编号',width:100,align:'center'},  
	    		          {field:'unCount',title:'未处理数',width:70,align:'center',
	    		        	  formatter : function(value, rowData, rowIndex) {
									return rowData.lockCount;
								}},  
	    		          {field:'count',title:'总数',width:70,align:'center'},  
	    		          {field:'lockCount',title:'冻结量',width:70,align:'center'},  
	    		          {field:'statusName',title:'状态',width:70,align:'center'},  
	            ]],    
	            onResize:function(){    
	            	$('#datagrid').datagrid('fixDetailRowHeight',index);    
	            },    
	            onLoadSuccess:function(){    
	                setTimeout(function(){    
	                	$('#datagrid').datagrid('fixDetailRowHeight',index);    
	                },0);    
	            }    
	        });    
	        $('#datagrid').datagrid('fixDetailRowHeight',index);    
	    },
		columns:[[ 
		          {field:'id',title:'ID',width:20,align:'center',checkbox:false,hidden:true},  
		          {field:'code',title:'异常处理单',width:350,align:'center'},  
		          {field:'operCode',title:'作业单',width:300,align:'center'},  
		          {field:'operTypeName',title:'作业单类型',width:200,align:'center'},  
		          {field:'abnormalTypeName',title:'异常类型',width:200,align:'center'},  
		          {field:'statusName',title:'异常处理状态',width:200,align:'center'},  
		      ]]
	});
});
function setDate(){
	var date = new Date()
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#endTime').datebox('setValue',d);
	$('#startTime').datebox('setValue',d);
}
function checksubmit(){
	var startTime = $('#tb input[name=startTime]').val()
	var endTime =  $('#tb input[name=endTime]').val();
	var nDay_ms = 24*60*60*1000;
	var reg = new RegExp("-","g");
	var startDay = new Date(startTime.replace(reg,'/'));
	var endDay = new Date(endTime.replace(reg,'/'));
	var nDifTime = endDay.getTime()- startDay.getTime();
	if(nDifTime < 0){
		$.messager.alert('提示', '起始日期不能大于结束日期!', 'error');
    	return false;
	}
    var nDifDay=Math.floor(nDifTime/nDay_ms);
    if(nDifDay > 31){
    	$.messager.alert('提示', '日期间隔不能大于31天!', 'error');
    	return false;
    }
	return true;
}
function searchFun(){
	if(!checksubmit()){
		return;
	}
	$('#datagrid').datagrid('load',{
		startTime : $('#tb input[name=startTime]').val(),
		endTime : $('#tb input[name=endTime]').val(),
		area : $('#tb input[name=area]').val(),
		type : $('#tb input[name=type]').val(),
		code : $('#tb input[name=code]').val(),
		status : $('#tb input[name=status]').val(),
	});
}
function clearFun(){
	$('#tb input').val('');
	$('#datagrid').datagrid('load',{});
}
</script>
</head>
<body>
	<div id="tb" style="padding:3px;height: auto;">
		<fieldset>
		<legend>筛选</legend>
			<table>
				<tr>
					<td align="right">库地区:</td>
					<td align="left"><input id="area" name="area" editable="false"></td>
					<td align="right">状态：</td>
					<td align="left">
					<select name="status" class="easyui-combobox" editable="false">
						<option value="0">未处理</option>
						<option value="1">处理中</option>
						<option value="2">无异常</option>
						<option value="3">待盘点</option>
						<option value="4">盘点中</option>
						<option value="5">已盘点</option>
					</select></td>
					<td align="right">类型：</td>
					<td align="left">
					<select name="type" class="easyui-combobox" editable="false">
						<option value=""></option>
						<option value="0">撤单</option>
						<option value="1">分拣货位异常</option>
						<option value="2">分拣SKU错误</option>
					</select></td>
				</tr>
				<tr>
					<td align="right">货位号/商品编号:</td>
					<td align="left"><input name="code" id="code"/></td>
					<td align="right">时间：</td>
					<td colspan="3" align="left" >
						<input id="startTime" name="startTime" class="easyui-datebox" editable="false" style="width: 145px">
						-<input id="endTime" name="endTime" class="easyui-datebox" editable="false" style="width: 145px"></td>
					<td>
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun()">查 询 </a>
						<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun()">清 空 </a>
					</td>
					
				</tr>
			</table>
		</fieldset>
	</div>    
    <table id="datagrid"> 
</table>
</body>
</html>