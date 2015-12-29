<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>厂家维修更换商品匹配</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
<script type="text/javascript" charset="UTF-8">
	$(function(){
		$("#sameType").click(function(){
			$("#productCode").attr("readonly","true");
		});
		$("#differType").click(function(){
			$("#productCode").removeAttr("readonly");
		});
		
		$("#oriImei").blur(function(){
			var oriImei = $.trim($('#oriImei').val());
			if(oriImei==""){
				$.messager.show({
					title : '提示',
					msg : "请输入原Imei码!"
				});
			}
			$("#detectCode").attr("readonly","true");
			$.ajax({
				url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectProduct.mmx',
				type : 'post',
				data : {imei:oriImei,flag:true},
				dataType : 'json',
				success : function(result){
					$("#detectCode").val(result.obj.code)
					.attr("readonly","true");
				}
			});
		});
		
		$("#detectCode").blur(function(){
			var detectCode = $.trim($('#detectCode').val());
			if(detectCode==""){
				$.messager.show({
					title : '提示',
					msg : "请输入原售后处理单号!"
				});
			}
			$("#oriImei").attr("readonly","true");
			$.ajax({
				url : '${pageContext.request.contextPath}/admin/AfStock/getAfterSaleDetectProduct.mmx',
				type : 'post',
				data : {detectCode:detectCode,flag:false},
				dataType : 'json',
				success : function(result){
					$("#oriImei").val(result.obj.imei)
					.attr("readonly","true");
				}
			});
		});
	});
	
	function replaceProductFun(){
			var type = $.trim($("#form input[name=type]:checked").val());
			var productCode = $.trim($("#productCode").val());
			var oriImei = $.trim($("#oriImei").val());
			var detectCode = $.trim($("#detectCode").val());
			var newImei = $.trim($('#newImei').val());
			$("#form").form("submit",{
				url : '${pageContext.request.contextPath}/admin/AfStock/replaceBackSupplierProduct.mmx',
				onSubmit : function(){
					if(type==""){
						$.messager.show({
							title : '提示',
							msg : "请选择更换类型!"
						});
						return false;
					}
					if(oriImei==''&& detectCode==''){
						$.messager.show({
							title : '提示',
							msg : "请输入原IMEI码或者原处理单号!"
						});
						return false;
					}
					if(type==1){
						if(newImei==''){
							$.messager.show({
								title : '提示',
								msg : "请输入新IMEI码!"
							});
						}
					}
					if(type==2){
						if(productCode==""){
							$.messager.show({
								title : '提示',
								msg : "请输入新产品编号!"
							});
						}
					}
				},
				success : function(result){
					try {
						var r = $.parseJSON(result);
						if(r.success){
							$("#form")[0].reset();
							$("#productCode").removeAttr("readonly");
							$("#oriImei").removeAttr("readonly");
							$("#detectCode").removeAttr("readonly");
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
				<table class="tableForm" align="center">
					<tr>
						<td colspan="2">
							更换类型：<input type="radio" id="sameType" name="type" value="1"/>
							同sku更换<input type="radio" id="differType" name="type" value="2" />更换sku</td>
					</tr>
					<tr>
						<td>原IMEI码：<input type="text" id="oriImei" name="oriImei"/></td>
						<td>原售后处理单号：<input type="text" id="detectCode" name="detectCode"/></td>
					</tr>
					<tr>
						<td>新IMEI码：<input type="text" id="newImei" name="newImei"/></td>
						<td>新产品编号：<input type="text" id="productCode" name="productCode"/></td>
					</tr>
					<tr align="center">
						<td></td>
						<td><a href="javascript:void(0);" id="btn" onclick="replaceProductFun();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'">确认提交</a></td>
					</tr>
				</table>
			</form>
	</fieldset>
	</div>
</body>
</html>
