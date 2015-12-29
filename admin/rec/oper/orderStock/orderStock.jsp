<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.order.OrderStockBean" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../../inc/easyui-base.jsp"></jsp:include>
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
      	url : '<%=request.getContextPath()%>/OrderStockController/getAreaComboBox.mmx',
      	valueField:'id',
		textField:'text' 
    });
    $('#deliver').combobox({   
			url:'<%=request.getContextPath()%>/OrderStockController/getDeliverComboBox.mmx',  
			valueField : 'id',   
			textField : 'name',
		    editable:false
	});
	$('#datagrid').datagrid({    
		url:"<%= request.getContextPath()%>/OrderStockController/getOrderStockDatagrid.mmx",
		nowrap:false,
		border:false,
		idField:"id",
		fit:true,
		fitColumns:true,
		title:"",
		pageSize :20,
		pageList:[ 20,100, 200, 300, 400 ],
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
		{field:'id',title:'ID', hidden:true, width:20},
		{field:'code',title:'出库单编号', rowspan:2,width:150,align:'center',sortable:true,formatter: function (value,row,index){
			return '<a href=\'<%= request.getContextPath()%>/admin/orderStock/scanOrderStock.jsp?id='+row.id + '&scanback=checkOrderStockList.jsp\' target=\'_blank\'>'+row.code+'</a>';
		}},   
        {field:'orderCode',title:'订单编号',rowspan:2,width:90,align:'center',sortable:true,formatter: function (value,row,index){
			return '<a href=\'<%= request.getContextPath()%>/admin/order.do?id='+row.orderId + '\' target=\'_blank\'>'+row.orderCode+'</a>';
		}},   
        {field:'createDatetime',title:'添加时间',rowspan:2,width:100,align:'center'},
        {field:'statusName',title:'状态',rowspan:2,width:70,align:'center'},
        {field:'stockAreaName',title:'发货地区',rowspan:1,width:70,align:'center'},
        {field:'deliverName',title:'快递公司',rowspan:2,width:70,align:'center',sortable:true},
        {field:'management',title:'操作',rowspan:2,width:40,align:'center',sortable:true,formatter: function (value,row,index){
			return '<a href=\'<%= request.getContextPath()%>/admin/orderStock/deleteOrderStock.jsp?id='+row.id + '&page=0&backType=easyuiOrderStock\'>删除</a>';
		}}
	]]  
	});
});


function searchFun() {
	$('#datagrid').datagrid('load',{
		startTime : $('#tb input[name=startTime]').val(),
		endTime : $('#tb input[name=endTime]').val(),
		area : $('#tb input[name=area]').val(),
		baseCode : $('#tb input[name=baseCode]').val(),
		status : $('#tb input[name=status]').val(),
		deliver : $('#tb input[name=deliver]').val()
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
			<div align="left">
				订单/出库单号：<input name="baseCode">
				出库地区: <input id="area" name="area" editable="false"/>
				快递公司：<input id="deliver" name="deliver" editable="false"/>
				出库单状态：<select name="status" class="easyui-combobox" editable="false">
							<option value="">请选择</option>
							<option value="<%= OrderStockBean.STATUS1 %>">处理中</option>
							<option value="<%= OrderStockBean.STATUS2 %>">待出货</option>
							<option value="<%= OrderStockBean.STATUS6 %>">复核</option>
							<option value="<%= OrderStockBean.STATUS3 %>">已出货</option>
							<%-- <option value="<%= OrderStockBean.STATUS7 %>">实物未退回</option>
							<option value="<%= OrderStockBean.STATUS8 %>">实物已退货</option>--%>
							<option value="<%= OrderStockBean.STATUS9 %>">用户退货</option>
					   </select>
				时间：<input type="text" id="startTime" name="startTime" class="easyui-datebox" editable="false">-<input type="text" id="endTime" name="endTime" class="easyui-datebox" editable="false">
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();">查询 </a>
				<a href="javascript:void(0);" class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();">清空 </a>
			</div>
		</fieldset>
		
	</div>    
    <table id="datagrid" style="height:auto;width:auto;"> 
	<thead>
	<tr>
		
	</tr>
	</thead>
</table>
</body>
</html>