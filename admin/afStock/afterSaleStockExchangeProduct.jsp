<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>选择目的库类型 </title>
</head>
<body>
	<center id="toolbar">
	<table id="conditionTable" class="tableForm">
	 <tr>
		<td><span style="font-size: 12px;">库区：</span></td>
		
		<td><input type="text" class="easyui-validatebox" name="areaType" id="areaType"></td>
	 </tr>
	 <tr>
		<td><span style="font-size: 12px;">库类型：</span></td>
		
		<td><input type="text" class="easyui-validatebox" name="stockType" id="stockType"></td>
	 </tr>
	 <tr>
		<td colspan="2" align="center"><a onclick="exchangeProduct()" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-save'">确定</a></td>
	 </tr>
	</table>
	</center>
</body>
</html>