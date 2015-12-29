<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>售后调拨货位调整(内部使用)</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
function addFun(){
	$('#addform').form('submit', {
		url : '${pageContext.request.contextPath}/admin/AfStock/afterSaleTransferWholeAdjust..mmx',
		success : function(data) {
			var d = $.parseJSON(data);
			if (d) {
				$('#datagrid').datagrid('reload');
				$.messager.alert('提示消息',d.msg,'info');
			}
		}
	});
}
</script>
</head>
<body >
<fieldset style="width: 580px;" >
			<legend>售后调拨货位调整</legend>
			<form id="addform"  method="post">
			<table border="0" >
				<tr>
					<th align="right">售后处理单号：</th>
					<td><textarea name="detectCodes" rows="4" cols="52"  ></textarea>  </td>
				</tr>
				<tr>
					<th align="right">调拨单号： </th>
					<td><textarea  name="exchangeCodes" rows="4" cols="52"></textarea></td>
				</tr>
				<tr>
				<th align="right"> </th>
					<td align="right">
					<a class="easyui-linkbutton" iconCls="icon-save" onclick="addFun();" href="javascript:void(0);">提交</a>
					</td>
				</tr>
			</table>
			</form>
		</fieldset>
</body>
</html>