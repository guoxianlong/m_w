<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>已盘点商品</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div id="toolbar" class="datagrid-toolbar"  style="height: auto;display: none;">
		<input type="hidden" name="hiddenafterSaleDetectCode" id="hiddenafterSaleDetectCode">
		<input type="hidden" name="hiddenproductCode" id="hiddenproductCode">
		<table class="tableForm" id="conditionTable">
			<tr>
				<td><span style="font-size: 12px;">售后处理编号：</span><input type="text" class="easyui-validatebox" name="afterSaleDetectCode" id="afterSaleDetectCode"></td>
				<td><span style="font-size: 12px;">商品编号：</span><input type="text" class="easyui-validatebox" name="productCode" id="productCode"></td>
				<td><input type="hidden" id="type" name="type"></td>
				<td><input type="hidden" id="inventoryRecordId" name="inventoryRecordId" ></td>
				<td><a onclick="queryInventoryProductList()" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询</a></td>
			</tr>
		</table>
		<a onclick="bsProduct()" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'">报损</a>
		<a onclick="exportProduct()" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-print'">导出列表</a>
	</div>
	<table id="afterSaleInventoryProductList"></table>
</body>
</html>
