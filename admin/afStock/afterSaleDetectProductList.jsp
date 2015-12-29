<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
var status = ${param.status};
$(function(){
	datagrid = $('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectProductList.mmx?status='+status,
		toolbar : '#tb',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    frozenColumns : [[
	    ]],
	    columns:[[  
	        {field:'code',title:'售后处理单号',width:30,align:'center',
	        formatter : function(value, row, index) {
				    return '<a href="javascript:void(0);" class="editbutton" onclick="afterSaleDetectProduct('+row.id+')">'+row.code+'</a>';
			}},
	        {field:'cargoWholeCode',title:'货位编号',width:30,align:'center'},  
	        {field:'stockTypeName',title:'库类型',width:30,align:'center'},  
	        {field:'productName',title:'商品名称',width:30,align:'center'},  
	        {field:'productCode',title:'商品编号',width:30,align:'center'},  
	        {field:'afterSaleOrderCode',title:'售后单号',width:30,align:'center',
	        	formatter : function(value, row, index) {
				    return '<a href="javascript:void(0);" class="editbutton" onclick="afterSaleOrderId('+row.afterSaleOrderId+')">'+row.afterSaleOrderCode+'</a>';
			}},  
	        {field:'IMEI',title:'IMEI',width:30,align:'center'},  
	        {field:'statusName',title:'处理单状态',width:30,align:'center'}
	    ]],
	    onLoadSuccess : function(data) {
			try {
				if (data.footer[0] != 'undefined' && data.footer[0]!=null && data.footer[0] != "") {
					$("#status").html("<font color='blue'>状态:"+data.footer[0]+"</font>");
				} else {
					$("#status").html("");
				}
			} catch(e) {
				$.messager.alert("提示", "错误" ,"info");
			}
		}
	}); 
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
});
function searchFun() {
	datagrid.datagrid('load', {
		AfterSaleOrderCode : $('#tb input[name=AfterSaleOrderCode]').val(),
		AfterSaleDetectProductCode : $('#tb input[name=AfterSaleDetectProductCode]').val(),
		productCode : $('#tb input[name=productCode]').val(),
		productName : $('#tb input[name=productName]').val(),
		IMEI : $('#tb input[name=IMEI]').val(),
		areaId : $('#areaId').combobox('getValue')
	});
}
function clearFun() {
	$('#tb input[name=AfterSaleOrderCode]').val('');
	$('#tb input[name=AfterSaleDetectProductCode]').val('');
	$('#tb input[name=productName]').val('');
	$('#tb input[name=productCode]').val('');
	$('#tb input[name=IMEI]').val('');
	$('#areaId').combobox('setValue','');
	datagrid.datagrid('load', {});
}
function afterSaleDetectProduct(id){ 
	window.location.href ='${pageContext.request.contextPath}/admin/afStock/afterSaleDetectProductInfo.jsp?id='+id;
}
function afterSaleOrderId(id){ 
	window.location.href ='https://sales.ebinf.com/sale/admin/toEdit.mmx?id='+id;

}
</script>
</head>
<body>
	<div id="tb"  style="height: auto;display: none;">
	<h3 id="status" name="status"></h3>
		<fieldset>
			<legend>筛选</legend>
			<table border="0">
				<tr>
					<th>售后单号：</th>
					<td><input id="AfterSaleOrderCode" name="AfterSaleOrderCode" style="width: 116px;"/></td>
					<th>售后处理单号：</th>
					<td><input id="AfterSaleDetectProductCode" name="AfterSaleDetectProductCode" style="width: 116px;"/></td>
					<th >售后地区</th>
					<td align="left">
					<input id="areaId" name="areaId" style="width: 121px" /></td>
				</tr>
				<tr>
					<th>商品名称：</th>
					<td><input id="productName" name="productName" style="width: 116px;"/></td>
					<th>商品编号：</th>
					<td><input id="productCode" name="productCode" style="width: 116px;"/></td>
					<th>IMEI：</th>
					<td><input id="IMEI" name="IMEI" style="width: 116px;"/></td>
					<td>
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-back" plain="true" onclick="clearFun();" href="javascript:void(0);">重置</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
	<table id="datagrid"></table> 
</body>
</html>