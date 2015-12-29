<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>包裹核查统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
var checkPackageCountForm;
$(function() {
	checkPackageCountForm = $('#checkPackageCountForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/getCountResult.mmx',
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.result == 'success') {
					$("#checkPackageInfoDiv").html(d.tip);
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
		<div data-options="region:'north',title:'包裹核查统计',border:false" style="height: 180px;overflow: hidden;" align="center">
			<form id="checkPackageCountForm">
				<table class="tableForm">
					<tr>
						<th>包裹核查统计：</th>
						<td>库地区：</td>
						<td><input name='wareArea' id='wareArea'  style="width:152px;border:1px solid #ccc"/></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<th>包裹核查统计：</th>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td>核查时间：</td>
						<td><input class="easyui-datebox" name="startTime" id="startTime"  style="width:152px;border:1px solid #ccc" data-options="required:true"/>&nbsp;&nbsp;</td>
						<td>入库操作人：</td>
						<td><input type="text" id="operationid" name="operationid" style="width:150px;border:1px solid #ccc"/></td>
						<td><input type="hidden" name="type" id="type" value="checkpackage"/></td>
					</tr>
				</table>
				<a id="enter" class="easyui-linkbutton" onclick="checkPackageCountForm.submit();" data-options="iconCls:'icon-search',plain:true"  href="javascript:void(0);">查询</a>
			</form>
		</div>
		<div data-options="region:'center',border:false" align="center">
			<h3><div id="checkPackageInfoDiv"/></h3>
		</div>
	</div>
</body>
</html>