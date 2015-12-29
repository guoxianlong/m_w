<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>入库作业统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
var returnPackageCountForm;
$(function() {
	returnPackageCountForm = $('#returnPackageCountForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/getCountResult.mmx',
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.result == 'success') {
					$("#returnPackageInfoDiv").html(d.tip);
				} else {
					$.messager.alert("提示", d.tip, "info");
				}
			} catch (e) {
				$.messager.alert("提示", "错误！", "info");
			}
		}
	});

	$('#wareArea').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getReturnPackageWareAreaJSON.mmx',  
		valueField : 'areaId',   
		textField : 'areaName',
		panelHeight:'auto',
	    editable:false
	}); 
	
	var date = new Date()
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#startTime').datebox('setValue',d);
});

</script>
</head>
<body>
	<div class="easyui-layout" data-options="fit : true,border : false">
		<div data-options="region:'north',title:'入库统计',border:false" style="height: 190px;overflow: hidden;" align="center">
			<form id="returnPackageCountForm">
				<table class="tableForm">
					<tr>
						<th>入库统计：</th>
						<td>库地区：</td>
						<td><input name='wareArea' id='wareArea' style="width:152px;border:1px solid #ccc"/></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<th>入库量统计：</th>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td>入库量时间：</td>
						<td><input class="easyui-datebox" name="startTime" id="startTime"  style="width:152px;border:1px solid #ccc" data-options="required:true"/>&nbsp;&nbsp;</td>
						<td>入库操作人：</td>
						<td><input type="text" id="operationid" name="operationid" style="width:150px;border:1px solid #ccc"/></td>
					</tr>
					<tr>
						<td></td>
						<td>产品编号：</td>
						<td><input type="text" name="productCode" id="productCode" style="width:150px;border:1px solid #ccc"/></td>
						<td><input type="hidden" name="type" id="type" value="returnpackage"/></td>
						<td></td>
					</tr>
				</table>
				<a id="enter" class="easyui-linkbutton" onclick="returnPackageCountForm.submit();" data-options="iconCls:'icon-search',plain:true"  href="javascript:void(0);">查询</a>
			</form>
		</div>
		<div data-options="region:'center',border:false" align="center">
			<h3><div id="returnPackageInfoDiv"/></h3>
		</div>
	</div>
</body>
</html>