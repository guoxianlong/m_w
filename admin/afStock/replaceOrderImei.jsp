<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
function addFun() {
	if($('#orderCode').val() == ''|| $('#oldImeiCode').val() == '' || $('#newImeiCode').val() == ''){
		return;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/replaceOrderImei.mmx',
		dataType : 'json',
		type : 'post',
		cache : false,
		data : {
			orderCode : $('#orderCode').val(),
			oldImeiCode : $('#oldImeiCode').val(),
			newImeiCode : $('#newImeiCode').val(),
		},
		success : function(d){
			$('#orderCode').val('');
			$('#oldImeiCode').val('');
			$('#newImeiCode').val('');
			$.messager.show({
				msg : d.msg,
				title : '提示'
			});
		}
	});
}
</script>
</head>
<body>
	<div >
		<form id="form" method="post" action="${pageContext.request.contextPath}/admin/AfStock/replaceOrderImei.mmx">
			<table class="tableForm" >
				<tr>
					<th align="center">订单编号:</th> 
					<td><input id="orderCode" name="orderCode" type="text" style="width: 156px;" required="required"/></td>
				</tr>
				<tr>
					<th align="center">旧IMEI码:</th>
					<td><input id="oldImeiCode" name="oldImeiCode" type="text" style="width: 156px;" required="required"/></td>
				</tr>
				<tr>
					<th align="center">新IMEI码:</th>
					<td ><input id="newImeiCode" name="newImeiCode" type="text" required="required" style="width: 156px;"></td>
				</tr>
				<tr>
					<td colspan="3"><a class="easyui-linkbutton" iconCls="icon-search" plain="false" onclick="addFun();" href="javascript:void(0);">提交</a></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>