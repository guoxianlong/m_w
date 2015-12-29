<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<title>配送状态查询</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript">
$(function(){
	document.getElementById("orderCodes").focus();
	initAllCombobox()
	initDataGrid();
});

function initAllCombobox() {
	$('#wareArea').combobox({
		url : '${pageContext.request.contextPath}/OrderStockController/getAreaComboBox.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
	$('#deliver').combobox({
		url : '${pageContext.request.contextPath}/OrderStockController/getDeliverComboBox.mmx',
		valueField : 'id',
		textField : 'name',
		editable : false
	});
	$('#deliverState').combobox({
		url : '${pageContext.request.contextPath}/Combobox/getDeliverState.mmx',
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
}
function initDataGrid() {
	$('#deliverDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/OrderStockController/getDeliverOrderState.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    rownumbers : true,
	    singleSelect : true,
	    pagination : true,
		pageSize : 20,
		pageList : [ 10, 20, 30, 40, 50 ],
	    columns:[[  
			{field:'wareArea',title:'发货仓',width:60,align:'center'},
			{field:'deliverName',title:'快递公司',width:60,align:'center'},
			{field:'province',title:'省',width:60,align:'center'},
			{field:'city',title:'市',width:60,align:'center'},
			{field:'cityArea',title:'区',width:60,align:'center'},
			{field:'packageCode',title:'包裹单号',width:60,align:'center'},
			{field:'orderCode',title:'订单号',width:60,align:'center'},
			{field:'transitDatetime',title:'发货时间',width:60,align:'center',
				formatter : function(value, row, index) {
					if ( value == null || value == 'undefined') {
						return '';
					} else {
						return value.substr(0,19);
					}
				}
			},
			{field:'deliverStateName',title:'状态',width:60,align:'center'},
			{field:'deliverTime',title:'节点时间',width:60,align:'center',
				formatter : function(value, row, index) {
					if ( value == null || value == 'undefined') {
						return '';
					} else {
						return value.substr(0,19);
					}
				}
			},
			{field:'deliverInfo',title:'配送信息',width:120,align:'center'}
	    ] ],
	    onLoadSuccess: function(data) {
    		if( data['tip'] != null ) {
    			$.messager.show({
    				msg : data['tip'],
    				title : '提示'
    			});
	    	}
	    },
	}); 
}

function checksubmit(){
	var reg=new RegExp("^(\\d)|(\\n)$");
	var scanType = $('input[name="scanType"]:checked').val();
	var orderCodes = document.getElementById("orderCodes");
	var startTime = $('#searchProductForm').find('[id=startDate]').datebox("getValue");
	var endTime = $('#searchProductForm').find('[id=endDate]').datebox("getValue");
	
	var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	if(startTime.length!=0 && endTime.length!=0){
		if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
			$.messager.show({
				msg : "出库日期，请输入正确的格式！如：2011-08-10",
				title : '提示'
			});
			return false;
		}	
	
		if((endTime.length!=0 && endTime.length!=10) || !r.test(endTime)){
			$.messager.show({
				msg : "出库日期，请输入正确的格式！如：2011-08-10",
				title : '提示'
			});
			return false;
		}
		var day = (getDateFromString(endTime)-getDateFromString(startTime))/(1000*60*60*24);
		if(day<0){
			$.messager.show({
				msg : "出库日期，起始日期不能大于截止日期。\n请重新输入",
				title : '提示'
			});
			return false;
		}
		if(day>30){
			$.messager.show({
				msg : "出库日期，两个日期之差不得大于31。\n请重新输入",
				title : '提示'
			});
			return false;
		}
	}
	if(startTime.length!=0&&endTime.length==0 ){
		$.messager.show({
			msg : "出库日期，请输入截止日期",
			title : '提示'
		});
		return false;
	}
    if(startTime.length==0&&endTime.length!=0 ){
    	$.messager.show({
			msg : "出库日期，请输入起始日期",
			title : '提示'
		});
		return false;
	}
	if(orderCodes.value=="" && startTime.length==0 && endTime.length==0){
		$.messager.show({
			msg : "请填写出库日期或订单号或包裹单号！",
			title : '提示'
		});
		return false;
	}
	var str = orderCodes.value.split("\n");
	var numlen = str.length;
	var count=0;
	if(numlen>1000){
		for(var i=0;i<numlen;i+=1){
			if(str[i] && trim(str[i]).length>0)
				count++;
		}
	}
	if(count>1000){
		$.messager.show({
			msg : "一次最多可扫描1000个" + (scanType == 1 ? '包裹单号' : '订单号') +"！",
			title : '提示'
		});
		return false;
	}
	return true;
}

function gridSearch() {
	var scanType = $('input[name="scanType"]:checked').val();
	var orderCodes = document.getElementById("orderCodes").value;
	var startTime = $('#searchProductForm').find('[id=startDate]').datebox("getValue");
	var endTime = $('#searchProductForm').find('[id=endDate]').datebox("getValue");
	var wareArea = $('#searchProductForm [id=wareArea]').combobox("getValue");
	var deliver = $('#searchProductForm [id=deliver]').combobox("getValue");
	var deliverState = $('#searchProductForm [id=deliverState]').combobox("getValue");
	$("#scanType1").val(scanType);
	$("#orderCodes1").val(orderCodes);
	$("#startDate1").val(startTime);
	$("#endDate1").val(endTime);
	$("#wareArea1").val(wareArea);
	$("#deliver1").val(deliver);
	$("#deliverState1").val(deliverState);
	if (!checksubmit()) {
		return false;
	}
	$("#deliverDataGrid").datagrid("load",{
		orderCodes: orderCodes,
		scanType: scanType,
		startDate: startTime,
		endDate: endTime,
		wareArea: wareArea,
		deliver: deliver,
		deliverState: deliverState
	});
}

function exportFun() {
	$("#searchProductForm").submit();
}

function getDateFromString(strDate){
    var arrYmd   =  strDate.split("-");
    var numYear  =  parseInt(arrYmd[0],10);
    var numMonth =  parseInt(arrYmd[1],10)-1;
    var numDay   =  parseInt(arrYmd[2],10);
    var leavetime=new Date(numYear,  numMonth,  numDay);
    return leavetime;

}
</script>
</head>
<body>
		<div id="tb" style="padding:3px;height: auto;">
			<form id="searchProductForm" action="${pageContext.request.contextPath}/OrderStockController/exportDeliverOrderState.mmx" method="post">
				<input name='deliverState1' id='deliverState1' type="hidden"/>
				<textarea name='orderCodes1' id='orderCodes1' style="display:none"></textarea>
				<input name='startDate1' id='startDate1' type="hidden"/>
				<input name='endDate1' id='endDate1' type="hidden"/>
				<input name='wareArea1' id='wareArea1' type="hidden"/>
				<input name='deliver1' id='deliver1' type="hidden"/>
				<input name='scanType1' id='scanType1' type="hidden"/>
				<fieldset>
					<legend>筛选</legend>
					<table class="tableForm">
						<tr>
							<td rowspan="2">
								<textarea cols="50" rows="4" id="orderCodes" name="orderCodes"></textarea>
							</td>
							<th>出库日期：</th>
							<td colspan="6">
								<input name='startDate' id='startDate' style="width:152px" class="easyui-datebox"/>
								——
								<input name='endDate' id='endDate' style="width:152px" class="easyui-datebox"/>
								<font color="red">不能超过31天</font>
							</td>
						</tr>
						<tr>
							<th>发货仓：</th>
							<td>
								<input name='wareArea' id='wareArea' style="width:152px"/>
							</td>
							<th>快递公司：</th>
							<td>
								<input name='deliver' id='deliver' style="width:152px"/>
							</td>
							<th>状态：</th>
							<td>
								<input name='deliverState' id='deliverState' style="width:152px"/>
							</td>
							<td>
								<mmb:permit value="2161">
								<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true" onclick="gridSearch();">查询</a>
								</mmb:permit>
							</td>
						</tr>
						<tr>
							<td>
								<input type="radio" name="scanType" value="1"  checked="checked"/>包裹单号
								<input type="radio" name="scanType" value="2" />订单号
							</td>
						</tr>
					</table>
				</fieldset>
				<mmb:permit value="2161">
				<a href="javascript:void(0);" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true" onclick="exportFun();">导出</a>
				</mmb:permit>
			</form>
		</div>
	<table id="deliverDataGrid"></table>
</body>
</html>