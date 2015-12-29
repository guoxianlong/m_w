<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>备用机检测不合格更换</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="UTF-8">
	
	function cheakInput(){
		var newSpareCode = $.trim($('#newSpareCode').val());
		if(newSpareCode==""){
			$.messager.show({
				title : '提示',
				msg : "新备用机号不能为空!"
			});
			return false;
		}
	}
	
	function cheakInput2(){
		var newImei = $.trim($('#newImei').val());
		if(isNaN(newImei)){
			$.messager.show({
				title : '提示',
				msg : "新备用机IMEI码必须为数字!"
			});
			$('#newImei').val("");
			return false;
		}
	}
	
	function find(){
		var oldSpareCode = $.trim($('#oldSpareCode').val());
		if(oldSpareCode==""){
			$.messager.show({
				title : '提示',
				msg : "请输入原备用机单号!"
			});
			return false;
		}

		$.ajax({
			url : '${pageContext.request.contextPath}/spareManagerController/getSpareStockProduct.mmx',
			type : 'post',
			data : {code:oldSpareCode},
			dataType : 'json',
			success : function(result){
				$("#oldImei").val(result.obj.imei);
			}
		});
	}
	
	function find2(){
		var oldImei = $.trim($('#oldImei').val());
		if(isNaN(oldImei)){
			$.messager.show({
				title : '提示',
				msg : "原备用机IMEI码必须为数字!"
			});
			$('#newImei').val("");
			return false;
		}
		$.ajax({
			url : '${pageContext.request.contextPath}/spareManagerController/getSpareStockProduct.mmx',
			type : 'post',
			data : {oldImei:oldImei},
			dataType : 'json',
			success : function(result){
				$("#oldSpareCode").val(result.obj.code);
			}
		});
	}
	
	function replaceProductFun(){
			var oldSpareCode = $.trim($("#oldSpareCode").val());
			var oldImei = $.trim($("#oldImei").val());
			var newSpareCode = $.trim($("#newSpareCode").val());
			var newImei = $.trim($("#newImei").val());
			$("#form").form("submit",{
				url : '${pageContext.request.contextPath}/spareManagerController/replacement.mmx',
				onSubmit : function(){
					if(newSpareCode==""){
						$.messager.show({
							title : '提示',
							msg : "请输入新备用机号!"
						});
						return false;
					}
				},
				success : function(result){
					try {
						var r = $.parseJSON(result);
						if(r.success){
							$("#oldSpareCode").val("");
							$("#oldImei").val("");
							$("#newSpareCode").val("");
							$("#newImei").val("");
						}
						$.messager.show({
							title : '提示',
							msg : r.msg
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
	<div style="width:900px;">
		<fieldset>
			<form id="form">
				<table id="ttt" class="tableForm" align="center">
					<tr>
					<th>请输入原备用机单号：</th>
					<td><input id="oldSpareCode" name="oldSpareCode" onblur="return find()"  style="width: 155px;" /></td>
				</tr>
				<tr>
					<th>或请输入原IMEI码：</th>
					<td><input id="oldImei" name="oldImei" onblur="return find2()" style="width: 155px;" /></td>
				</tr>
				<tr>
					<th>请输入新备用机单号：</th>
					<td><input id="newSpareCode" name="newSpareCode" onblur="return cheakInput()" style="width: 155px;" /></td>
				</tr>
				<tr>
					<th>请输入新IMEI码：</th>
					<td><input id="newImei" name="newImei" onblur="return cheakInput2()" style="width: 155px;" /></td>
				</tr>
					<tr align="center">
						<td></td>
						<td><a href="javascript:void(0);" id="btn" onclick="replaceProductFun();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'">确认</a></td>
					</tr>
				</table>
			</form>
	</fieldset>
	</div>
</body>
</html>
