<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
function initForm() {
	$("#saveMultiIMEIForm input[id=productCode]").val("");
	$("#saveMultiIMEIForm input[id=imeiCount]").numberbox("setValue", "");
	$("#saveMultiIMEIForm textarea[id=multiIMEICode]").val("");
}

function saveIMEIFun() {
	var isValid = $("#saveMultiIMEIForm").form('validate');
	if (!isValid) {
		return false;
	}
	$.ajax({
        type: "post", //调用方式  post 还是 get
        url: '${pageContext.request.contextPath}/admin/IMEI/saveMultiIMEI.mmx',
        async:false,
        data : {
        	productCode:$("#saveMultiIMEIForm input[id=productCode]").val(),
        	imeiCount:$("#saveMultiIMEIForm input[id=imeiCount]").numberbox("getValue"),
    		multiIMEICode:$("#saveMultiIMEIForm textarea[id=multiIMEICode]").val()
        },
        dataType: "text", //返回的数据的形式
        success: function(result) { 
        	try {
				var r = $.parseJSON(result);
				if (r.success) {
					initForm();
				}
				$.messager.show({
					title : '提示',
					msg : decodeURI(r.msg)
				});
			} catch (e) {
				$.messager.alert('提示', result);
			}
        }
	});
}
</script>
</head>
<body>
		<h3>备用机添加IMEI码</h3>
		<br/>
		<fieldset>
			<form id="saveMultiIMEIForm">
				<table id="table" class="tableForm">
					<tr align="center" >
						<th>商品编号：</th>
						<td align="left">
							<input id="productCode" name="productCode" style="width: 116px;" class="easyui-validatebox" data-options="required:true"/>
						</td>
					</tr>
					<tr align="center">
						<th>数量：</th>
						<td align="left">
							<input id="imeiCount" name="imeiCount" style="width: 116px;" class="easyui-numberbox" data-options="required:true,min:1,max:999999"/>
						</td>
					</tr>
					<tr align="center">
						<th>IMEI码：</th>
						<td align="left" colspan="2">
							<textarea cols="50" rows="4" id="multiIMEICode" name="multiIMEICode" class="easyui-validatebox" data-options="required:true"></textarea>
						</td>
					</tr>
					<tr align="center">
						<td colspan="3" align="center">
							<a class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:true" onclick="saveIMEIFun();" href="javascript:void(0);">保存</a>
						</td>
					</tr>
				</table>
			</form>
		</fieldset>
</body>
</html>