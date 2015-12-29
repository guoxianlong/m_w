<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
$(function(){
	$('#packageCode').bind('keypress',function(event){
        if(event.keyCode == "13"){
        	addFun();
        }
    });
});
function addFun() {
	if($('#packageCode').val() == ''){
		return;
	}
	if(/\s/.test($('#packageCode').val())){
		$('#packageCode').val('');
		alert("包裹单号不合法");
		return;
    }
	$.ajax({
	    url : "${pageContext.request.contextPath}/admin/AfStock/noReceivePackageSign.mmx",
		type : "POST",
		dataType : 'json',
		cache: false,
		data : {packageCode : $('#packageCode').val()},
		success: function(d){
			if (d) {
				$('#packageCode').val('');
				$.messager.show({
					msg : d.msg,
					title : '提示'
				});
			}
		}
	});
	
}
</script>
</head>
<body>
	<div align="center">
		<table class="tableForm" >
			<tr>
				<th>包裹单号：</th>
				<td><input id="packageCode" name="packageCode" class="easyui-validatebox" required="required"/></td>
			</tr>
			<tr align="center">
				<th></th>
				<td ><a class="easyui-linkbutton" onclick="addFun();" href="javascript:void(0);">添加</a></td>
			</tr>
		</table>
	</div>
</body>
</html>