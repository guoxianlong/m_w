<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>寄回客户包裹列表</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var packageInfoDialog;
$(function(){
	datagrid = $('#backUserPackageDataGrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/getBackUserPackageDataGrid.mmx',
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
	    columns:[[  
			{field:'createDatetime',title:'邮寄时间',width:100,align:'center',
				formatter : function(value, row, index) {
        			return value.substring(0, 19);
				}
			},
			{field:'customerName',title:'姓名',width:60,align:'center'},
			{field:'packageCode',title:'包裹单号',width:80,align:'center'},
			{field:'price',title:'运费金额',width:40,align:'center'},
			{field:'deliverName',title:'快递公司',width:60,align:'center'},
			{field:'userName',title:'发件人',width:60,align:'center'},
			{field:'orderCode',title:'订单号',width:80,align:'center'},
			{field:'afterSaleOrderCode',title:'售后单',width:90,align:'center'},
			{field:'userAddress',title:'收件地址',width:80,align:'center'},
			{field:'afterSaleDetectProductCode',title:'售后处理号',width:80,align:'center'},
			{field:'spareCode',title:'原备用机单号',width:80,align:'center'},
			{field:'productName',title:'产品名称',width:80,align:'center'},
			{field:'parentName',title:'一级分类',width:40,align:'center'},
			{field:'typeName',title:'分类',width:50,align:'center'},
			{field:'weight',title:'重量',width:30,align:'center'},
			{field:'remark',title:'备注',width:30,align:'center'},
			{field:'operation',title:'操作',width:50,align:'center',
				formatter : function(value, row, index) {
        			return '<a class="btn" onclick="printPackage(\'' + index +'\');"href="javascript:void(0);">补打</a> ';
				}},
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
	var d = new Date();
	var str = d.getFullYear()+"-"+(d.getMonth()+1)+"-"+d.getDate();
	window.open('${pageContext.request.contextPath}/admin/AfStock/printAfterSalePackageInfo.mmx?packageCode='+row.packageCode+'&postCode=' + row.customerPostCode);
}

function searchFun() {
	console.info($('#spareCode').val());
	datagrid.datagrid("load", {
		userPhone:$("#tb input[id=userPhone]").val(),
		packageCode:$("#tb input[id=packageCode]").val(),
		afterSaleDetectProductCode:$("#tb input[id=afterSaleDetectProductCode]").val(),
		afterSaleOrderCode:$("#tb input[id=afterSaleOrderCode]").val(),
		orderCode:$("#tb input[id=orderCode]").val(),
		startTime:$("#tb input[id=startTime]").datebox("getValue"),
		areaId : $('#areaId').combobox('getValue'),
		deliverId : $('#deliverId').combobox('getValue'),
		endTime:$("#tb input[id=endTime]").datebox("getValue"),
		spareCode:$('#spareCode').val()
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
			<legend>筛选</legend>
			<table class="tableForm" border="0">
				<tr align="center" >
					<th>客户电话</th>
					<td align="left">
						<input id="userPhone" name="userPhone" style="width: 116px;"/>
					</td>
					<th>包裹单号</th>
					<td align="left">
						<input id="packageCode" name="packageCode" style="width: 116px;"/>
					</td>
					<th>售后单号</th>
					<td align="left">
						<input id="afterSaleOrderCode" name="afterSaleOrderCode" style="width: 116px;"/>
					</td>
					<th>原备用机单号</th>
					<td align="left">
						<input id="spareCode" name="spareCode" style="width: 116px;"/>
					</td>
					<th>邮寄时间</th>
					<td align="left"  colspan="2">
						<input id="startTime" name="startTime" style="width:106px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:106px" class="easyui-datebox"/>
					</td>
				</tr>
				<tr align="center">
					<th>订单号</th>
					<td align="left">
						<input id="orderCode" name="orderCode" style="width: 116px;"/>
					</td>
					<th>售后处理单号</th>
					<td align="left">
						<input id="afterSaleDetectProductCode" name="afterSaleDetectProductCode" style="width: 116px;"/>
					</td>
					<th>售后地区:</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px;"/>
					</td>
					<th>快递公司</th>
					<td align="left">
						<input id="deliverId" name="deliverId" style="width: 121px;"/>
					</td>
					<th>订单号</th>
					<td align="left">
						<input id="orderCode" name="orderCode" style="width: 116px;"/>
					</td>
					<td colspan="">
						<a class="easyui-linkbutton" iconCls="icon-undo" onclick="importPackage();" plain="true" href="javascript:void(0);">导入寄出包裹</a> 
						<a class="easyui-linkbutton" iconCls="icon-redo" onclick="exportPackage();" plain="true" href="javascript:void(0);">导出寄出包裹</a> 
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