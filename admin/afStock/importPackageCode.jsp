<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<title>导入包裹单号</title>
<script type="text/javascript">
$(function(){
	$('#deliver').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getDeliver.mmx',
      	valueField:'id',
		textField:'text',
    });
});
function checkFun(){
	var packages = $("#packageCodes").val();
	if(packages == ''){
		$.messager.show({
			msg : '包裹单号不能为空!',
			title : '提示'
		});
		return false;
	}
	return true;
}
function importFun(){
	if(checkFun()){
		var packageArry = $("#packageCodes").val().split("\n");
		$.ajax({
			url : '${pageContext.request.contextPath}/admin/AfStock/importPackageCode.mmx',
			type : 'post',
			dataType:'json',
			data : {
				deliverId : $('#deliver').combobox('getValue'),
				packageCodes : packageArry.join(",")
			},
			success:function(r){
				$("#packageCodes").val("");
				$.messager.show({
					msg : r.msg,
					title : '提示'
				});
			}
		});
	}
}
</script>
</head>
<body>
	<fieldset style="background-color: #dedede;padding:8px;">
		<form id="form" method="post">
			<table align="center">
				<tr>
					<td align="right">快递公司：</td>
					<td><input id="deliver" name="deliverId" editable="false" required="required" style="width: 155px;" /></td>
				</tr>
				<tr>
					<td align="right">包裹单号：</td>
					<td><textarea style="width:500px;" rows="3" id="packageCodes" name="packageCodes" required="required"></textarea></td>
				</tr>
				<tr>
					<td colspan="2" align="center"><a class="easyui-linkbutton" onclick="importFun();" href="javascript:void(0);">导入</a></td>
				</tr>
			</table>
		</form>
	</fieldset>
</body>
</html>
