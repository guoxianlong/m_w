<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>返还厂商备用机包裹列表</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/admin/js/timeAndOther.js"></script>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var packageInfoDialog;
$(function(){
	$("#supplierId").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
		valueField:'id',
		textField:'text',
		delay:500
	});
	
	datagrid = $('#backUserPackageDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/spareManagerController/spareBackSupplierList.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50 ],
	    frozenColumns : [[
	  					{field:'id',width:20,hidden:true}
	  	]],
	    columns:[[ 
				{field:'supplierId',title:'供应商名称',width:60,align:'center'},
				{field:'packageCode',title:'返还快递单号',width:80,align:'center'},
				{field:'deliveryCost',title:'运费金额',width:40,align:'center'},
				{field:'deliveryId',title:'快递公司',width:60,align:'center'},
				{field:'operateUserName',title:'发件人',width:60,align:'center'},
				{field:'ourAddress',title:'收件地址',width:80,align:'center'},
				{field:'areaId',title:'出库地区',width:90,align:'center'},
				{field:'createDatetime',title:'快递时间',width:100,align:'center',
					formatter : function(value, row, index) {
						return value.substring(0, 19);
					}
				},
				{field:'operation',title:'操作',align:'center',
					formatter : function(value, row, index) {
						return '<a class="btn" onclick="see(\'' + index +'\');"href="javascript:void(0);">查看</a> <a class="btn" onclick="printPackage(\'' + index +'\');"href="javascript:void(0);">补打</a> ';
					}
				},
	    ] ],
		onLoadSuccess : function(data) {
			$(".btn").linkbutton();
		}
	});
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#deliverId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getDeliver.mmx',
      	valueField:'id',
		textField:'text',
    });
    packageInfoDialog = $("#packageInfoDialog").show().dialog({
    	width : 900,
		height : 500,
    	modal : true,
		minimizable : true,
		title : '导入数据项',
		buttons : [{
			text : '确定',
			handler : function() {
				$('#packageInfoForm').form('submit', {
					url : '${pageContext.request.contextPath}/admin/AfStock/importPackage.mmx',
					dataType: "text",
					success : function(data) {
						$('#msg').html(data);
					}
				});
			}
		}],
		onClose : function(){
			$('#msg').html('');
			$('#content').val('');
		}
	}).dialog('close');
});

function printPackage(index){
	datagrid.datagrid('selectRow',index);
	var row = datagrid.datagrid('getSelected');
	window.open('${pageContext.request.contextPath}/spareManagerController/print.mmx?id='+row.id);
}

function see(index){
	datagrid.datagrid('selectRow',index);
	var row = datagrid.datagrid('getSelected');
	window.open('${pageContext.request.contextPath}/admin/spare/spareReturnFactorySee.jsp?id='+row.id+'');
}

function searchFun() {
	
	var spareCode = $('#spareCode').val();
	var packageCode = $('#packageCode').val();
	var supplierId =$('#supplierId').combobox('getValue');
	var areaId = $('#areaId').combobox('getValue');
	var startTime =$('#startTime').datebox('getValue');
	var endTime = $('#endTime').datebox('getValue');
	if(spareCode=='' && packageCode=='' && startTime=='' && endTime=='' && supplierId==-1 && areaId==''){
		$.messager.show({
			msg : '请输入查询条件!',
			title : '提示'
		});
		return;
	}
	if(startTime!=''&& endTime!=''){
		var days = getValidateSubDays(endTime, startTime);
		if (days < 0) {
			$.messager.show({
				msg : "生成入库单查询的开始时间不能早于结束时间!",
				title : '提示'
			});
			return false;
		}
	}
	
	datagrid.datagrid("load", {
		spareCode:$("#spareCode").val(),
		packageCode:$("#packageCode").val(),
		supplierId : $('#supplierId').combobox('getValue'),
		startTime:$('#startTime').datebox('getValue'),
		endTime:$("#endTime").datebox("getValue"),
		areaId : $('#areaId').combobox('getValue')
		
	});
}
function exportPackage() {
		var userPhone = $("#tb input[id=userPhone]").val();
		var packageCode = $("#tb input[id=packageCode]").val();
		var afterSaleDetectProductCode = $("#tb input[id=afterSaleDetectProductCode]").val();
		var afterSaleOrderCode = $("#tb input[id=afterSaleOrderCode]").val();
		var orderCode = $("#tb input[id=orderCode]").val();
		var startTime = $("#tb input[id=startTime]").datebox("getValue");
		var areaId = $('#areaId').combobox('getValue');
		var deliverId = $('#deliverId').combobox('getValue');
		var endTime = $("#tb input[id=endTime]").datebox("getValue");
		location.href = "${pageContext.request.contextPath}/admin/AfStock/exportBackUserPackage.mmx?startTime=" + 
		startTime + "&endTime=" + endTime + "&userPhone=" + userPhone + "&packageCode=" + packageCode + "&afterSaleDetectProductCode=" 
			+ afterSaleDetectProductCode + "&afterSaleOrderCode=" + afterSaleOrderCode + "&orderCode=" + orderCode + "&areaId=" + areaId + "&deliverId=" + deliverId;
}
function importPackage(){
	packageInfoDialog.dialog('open');
}
</script>
</head>
<body>
	<table id="backUserPackageDataGrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<fieldset>
			<legend>返还厂商备用机包裹列表</legend>
			<table class="tableForm" border="0">
				<tr align="center" >
					<th>返还快递单号</th>
					<td align="left">
						<input id="packageCode" name="packageCode" style="width: 116px;"/>
					</td>
					<th>供应商名称</th>
					<td align="left">
						<input id="supplierId" name="supplierId" style="width: 116px;"/>
					</td>
					<th>备用机单号</th>
					<td align="left">
						<input id="spareCode" name="spareCode" style="width: 116px;"/>
					</td>
					<th>出库地区</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 116px;"/>
					</td>
					<th>快递时间</th>
					<td align="left"  colspan="2">
						<input id="startTime" name="startTime" style="width:106px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:106px" class="easyui-datebox"/>
					</td>
					<td colspan="">
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-search',plain:true" onclick="searchFun();" href="javascript:void(0);">查询</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<div id="packageInfoDialog" style="overflow-y:auto; overflow-x:auto; display: none;">
		<form id="packageInfoForm" method="post">
			<table align="center">
			<tr>
				<th>导入数据项：</th>
				<td><textarea id="content" name="content" rows="10" cols="80"></textarea></td>
			</tr>
			<tr>
				<td colspan="2"><div id="msg" style="width:300;heigh:100;color:red;"></div></td>
			</tr>
			</table>
		</form>
	</div>
</body>
</html>