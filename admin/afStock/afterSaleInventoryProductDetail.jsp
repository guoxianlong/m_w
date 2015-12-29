<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>已盘点商品</title>
</head>
<body>
	<div id="toolbar">
		<table class="tableForm" id="conditionTable">
			<tr>
				<td><span style="font-size: 12px;">售后处理单号：</span><input type="text" class="easyui-validatebox" name="afterSaleDetectCode" id="afterSaleDetectCode"></td>
				<td><span style="font-size: 12px;">商品编号：</span><input type="text" class="easyui-validatebox" name="productCode" id="productCode"></td>
				<td><input type="hidden" id="type" name="type"></td>
				<td><input type="hidden" id="inventoryRecordId" name="inventoryRecordId" ></td>
				<td><a onclick="queryInventoryProductList()" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-search'">查询</a></td>
			</tr>
		</table>
		<div id="operateDiv" style="display:none;">
			<a onclick="modifyRealCargo()" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-edit'">更改为实际货位</a>
		</div>
	</div>
	<table id="afterSaleInventoryProductList"></table>
</body>
</html>
