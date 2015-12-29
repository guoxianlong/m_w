<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>退货上架单统计</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
var productUpStatisticForm
$(function(){
	$('#productLine').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getProductLine.mmx',  
		valueField:'id',   
		textField:'text',
		editable : false,
		Value:'-1'
	}); 
	
	$('#areaId').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getLimitArea.mmx',   
		valueField:'id',   
		textField:'text',
		editable : false,
		Value:'-1',
		panelHeight: 'auto'
	}); 
	
	productUpStatisticForm = $('#productUpStatisticForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/returnProductUpStatistics.mmx',
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.result == 'success') {
					$("#resultInfo").html(d.tip);
				} else {
					$.messager.alert("提示", d.tip, "info");
				}
			} catch (e) {
				$.messager.alert("提示", "错误！", "info");
			}
		}
	});
	
	var date = new Date()
	var d = date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate()
	$('#time').datebox('setValue',d);
});  
</script>
</head>
<body>
	<div class="easyui-layout" data-options="fit : true,border : false">
		<div data-options="region:'north',title:'退货上架单统计',border:false" style="height: 190px;overflow: hidden;" align="center">
			<form id="productUpStatisticForm">
				<table class="tableForm">
					<tr>
						<th>退货上架单统计：</th>
						<td>地区：</td>
						<td><input name='areaId' id='areaId' style="width:152px;border:1px solid #ccc"/></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<th>退货上架单统计：</th>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
					<tr>
						<td></td>
						<td>制单时间：</td>
						<td><input class="easyui-datebox" name="time" id="time"  style="width:152px;border:1px solid #ccc" data-options="required:true"/>&nbsp;&nbsp;</td>
						<td>产品线：</td>
						<td><input id="productLine" name="productLine"  style="width:152px;border:1px solid #ccc" /></td>
						<td>制单人：</td>
						<td><input id="userId" name="userId"  style="width:150px;border:1px solid #ccc" /></td>
					</tr>
					<tr>
						<td></td>
						<td>产品编号：</td>
						<td><input id="productCode" name="productCode"   style="width:150px;border:1px solid #ccc" /></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>
				</table>
				<a id="enter" class="easyui-linkbutton" onclick="productUpStatisticForm.submit();" data-options="iconCls:'icon-search',plain:true"  href="javascript:void(0);">查询</a>
			</form>
		</div>
		<div data-options="region:'center',border:false" align="center">
			<h3><div id="resultInfo"/></h3>
		</div>
	</div>
</body>
</html>