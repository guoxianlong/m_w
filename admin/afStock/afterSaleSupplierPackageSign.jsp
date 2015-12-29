<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="javascript" src="${pageContext.request.contextPath}/admin/barcodeManager/LodopFuncs6.1.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<script type="text/javascript" charset="UTF-8">
var LODOP;
function initPrint(code){
	LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	LODOP.PRINT_INIT("");
	LODOP.SET_PRINT_PAGESIZE(1,"40mm","15mm","");
	LODOP.SET_PRINT_STYLE("FontSize",5);
	LODOP.SET_PRINTER_INDEX(-1);
	LODOP.SET_PRINT_STYLEA(0,"FontSize",5);
	LODOP.SET_PRINT_STYLEA(0,"Bold",1);
	LODOP.ADD_PRINT_BARCODE("2mm","2mm","36mm","10mm","128Auto",code);
	LODOP.PRINTB();
}


$(function(){
	$("input[name='type']").change(function() {
		var selectedvalue = $("input[name='type']:checked").val();
		$("#productCode").val('');
		$("#imeiCode").val('');
		$("#afterSaleCode").val('');		
		if (selectedvalue == 0) {
			$("#productCode").attr("disabled","disabled");			
			$("#imeiCode").removeAttr('disabled');
			$("#imeiCode").focus();
		}else {
			$("#imeiCode").attr("disabled","disabled");			
			$("#productCode").removeAttr('disabled');
			$("#afterSaleCode").focus();
		}
 	});
	
	$("#type0").click();
	
	$('#afterSaleCode').bind('keypress',function(event){
        if(event.keyCode == "13"){
        	getCodeByAfterSaleCode();
        }
    });
	$('#imeiCode').bind('keypress',function(event){
        if(event.keyCode == "13"){
        	getCodeByImeiCode();
        }
    });
	$('#productCode').bind('keypress',function(event){
        if(event.keyCode == "13"){
        	getCodeByProductCode();
        }
    });
});

function getCode(pData){
	$.ajax({
		type : "post",
		url : "<%=request.getContextPath()%>/admin/AfStock/getCodeForBackSupplier.mmx",
		dataType : "json",
		cache : false,
		data : pData,
		error : function(x, s, e) {
			$.messager.show({
				msg : '' + eMsg,
				title : '操作失败'
			});
		},
		success : function(json) {
			if (json != null) {
				if (!json.success) {
					$.messager.alert('提示', '' + json.msg);					
					return;
				}
				$('#afterSaleCode').val(json.obj.afterSaleCode);
				$('#imeiCode').val(json.obj.imeiCode);
				$('#productCode').val(json.obj.productCode);
			}
		}
	});
}

function getCodeByAfterSaleCode(){
	var code = $("#afterSaleCode").val();
	if (code == null || code == '') {
		$.messager.alert('提示', '请扫描售后处理单号');
		return;
	}
	var pData = {
		afterSaleCode : '' + code,
		imeiCode : '',
		productCode : ''			
	};
	getCode(pData);
}

function getCodeByImeiCode(){
	var code = $("#imeiCode").val();
	if (code == null || code == '') {
		$.messager.alert('提示', '请扫描IMEI');
		return;
	}
	var pData = {
		afterSaleCode : '' ,
		imeiCode : '' + code,
		productCode : ''			
	};
	getCode(pData);
}

function getCodeByProductCode(){
	var code = $("#productCode").val();
	if (code == null || code == '') {
		$.messager.alert('提示', '请扫描商品编号');
		return;
	}
	var pData = {
		afterSaleCode : '',
		imeiCode : '',
		productCode : '' + code			
	};
	getCode(pData);
}

function sign() {
	var type = $("input[name='type']:checked").val();
	var imeiCode = $.trim($("#imeiCode").val());
	var afterSaleCode = $.trim($("#afterSaleCode").val());
	var productCode = $.trim($("#productCode").val());
	
	if (type == 0) {
		if (imeiCode == '' && afterSaleCode == ''){
			$.messager.alert('提示', '请扫描IMEI/售后处理单号');	
			return;
		}
		if (afterSaleCode == '') {
			$.messager.alert('提示', '请先按回车查询售后处理单号');
			return;
		}
		productCode = '';
	} else {
		if (productCode == '') {
			$.messager.alert('提示', '请扫描商品编号');
			return;
		}
		if (afterSaleCode == '') {
			$.messager.alert('提示', '请先按回车查询售后处理单号');
			return;
		}
		imeiCode = '';
	}
	
	var pData = {
			type : type,
			afterSaleCode : '' + afterSaleCode,
			imeiCode : '' + imeiCode,
			productCode : '' + productCode
		};
	
	$.ajax({
		type : "post",
		url : "${pageContext.request.contextPath}/admin/AfStock/supplierPackageSign.mmx",
		dataType : "json",
		cache : false,
		data : pData,
		error : function(x, s, e) {
			$.messager.show({
				msg : '' + eMsg,
				title : '操作失败'
			});
		},
		success : function(json) {
			if (json != null) {
				if (!json.success) {
					$.messager.alert('提示', '' + json.msg);					
					return;
				}

				if(json.obj){
					$.messager.alert('提示', '' + json.obj);
				}
				// 打印售后处理单号
				initPrint(afterSaleCode);
				$('#afterSaleCode').val('');
				$('#imeiCode').val('');
				$('#productCode').val('');				
				$.messager.show({
					msg : json.msg,
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
		<form  id="form">
			<table class="tableForm" >
				<tr>
					<th>签收方式：</th>
					<td><input id="type0" name="type" type="radio" value="0" />IMEI/售后处理单&nbsp;&nbsp;<input id="type1" name="type" type="radio" value="1" />商品编号</td>
				</tr>
				<tr>
					<th>IMEI：</th>
					<td><input id="imeiCode" class="easyui-validatebox"/></td>
				</tr>
				<tr>
					<th>处理单号：</th>
					<td><input id="afterSaleCode" class="easyui-validatebox" /></td>
				</tr>
				<tr>
					<th>商品编号：</th>
					<td><input id="productCode" class="easyui-validatebox" /></td>
				</tr>
				<tr align="center">
					<th></th>
					<td ><a class="easyui-linkbutton" onclick="javascript:sign();" href="javascript:void(0);">确认</a></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>