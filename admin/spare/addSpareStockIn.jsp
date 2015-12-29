<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>新建备用机入库单</title>
<script type="text/javascript" charset="UTF-8">
var spareItem = 0;
var flag = true;
$(function(){
	$("#backSupplierId").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
		valueField:'id',
		textField:'text',
		delay:500
	});
	
	$("#areaId").combobox({
		url : '${pageContext.request.contextPath}/Combobox/getSpareArea.mmx',
		valueField:'id',
		textField:'text',
		delay:500
	});
});

function addSpareHtml(){
	spareItem=spareItem + 1;
	var count = spareItem+1;
	$("#count").val(count);
	$("#spareCount").html(count);
	var tr = $("#count").parent().parent();
	var addItem = "";
	addItem+=('<tr>');
	addItem+=('<th>备用机号：</th>');
	addItem+=('<td>');
	addItem+=('<input type="text" id="spareCodeAdd'+spareItem+'" name="spareCodeAdd" class="easyui-validatebox" required="required" style="width: 200px;"/>');
	addItem+=('</td>');
	addItem+=('<th>IMEI码：</th>');
	addItem+=('<td>');
	addItem+=('<input type="text" id="imeiCodeAdd'+spareItem+'" name="imeiCodeAdd" class="easyui-validatebox" style="width: 200px;" onblur="validateImei(\'imeiCodeAdd'+spareItem+'\');"/>&nbsp;');
	addItem+=('<a class="addItemClass" onclick="removeSpareHtml('+spareItem+');" href="javascript:void(0);"></a>');
	addItem+=('</td>');
	addItem+=("</tr>");
	tr.before(addItem);
	$(".addItemClass").linkbutton(
		{ 
			plain:true,
			iconCls:'icon-remove'
		}
	);
}

function showCount(){
	var count = $("#count").val();
	$("#spareCount").html(count);
}

function removeSpareHtml(index) {
	$("#spareCodeAdd"+index).parent().parent().remove();
	var count = Number($("#count").val()) - 1;
	$("#count").val(count);
	$("#spareCount").html(count);
	spareItem = count-1;
}

function addSpareStockIn(){
	$("#addSpareForm").form("submit",{
		url : '${pageContext.request.contextPath}/spareManagerController/addSpareStockIn.mmx',
		onSubmit : function(){
			var backSupplierId = $("#backSupplierId").combobox('getValue');
			if(backSupplierId==-1){
				$.messager.show({
					msg : '请选择供应商!',
					title : '提示'
				});
				return false;
			}
			if(!flag){
				$.messager.show({
					msg : "输入的IMEI码必须是数字!",
					title : '提示'
				});
				return false;
			}
			return $("#addSpareForm").form('validate');
		},
		success : function(result){
			var r = $.parseJSON(result);
			if(r!=null){
				$.messager.show({
					msg : r.msg,
					title : '提示'
				});
				if(r.success){
					window.location.href = "${pageContext.request.contextPath}/admin/spare/addSpareStockIn.jsp";
				}
			}
		}
	});
}

function validateImei(id){
	var imei = $.trim($("#"+id).val());
	if(isNaN(imei)){
		$.messager.show({
			msg : "输入的IMEI码必须是数字!",
			title : '提示'
		});
		flag = false;
		return;
	}
	flag = true;
}
</script>
</head>
<body>
<div>
	<span style="font-size:15px;color:#333333;font-weight:bold;">新建备用机</span>
	<hr/>
	<form id="addSpareForm" method="post">
		<table class="tableForm" align="center">
			<tr>
				<th>商品编号：</th>
				<td><input type="text" id="productCode" name="productCode" class="easyui-validatebox" required="required" style="width: 200px;"/></td>
			</tr>
			<tr>
				<th>供应商：</th>
				<td><input type="text" id="backSupplierId" name="backSupplierId" required="required"/></td>
			</tr>
			<tr>
				<th>入库地区：</th>
				<td><input type="text" id="areaId" name="areaId" required="required"/></td>
			</tr>
			<tr>
				<th>备用机号：</th>
				<td><input type="text" id="spareCode" name="spareCode" class="easyui-validatebox" required="required" style="width: 200px;" onblur="showCount();"/></td>
				<th>IMEI码：</th>
				<td>
					<input type="text" id="imeiCode" name="imeiCode" class="easyui-validatebox" data-options="validType:'intNumber'" style="width: 200px;" onblur="validateImei('imeiCode');"/>
					<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addSpareHtml();" href="javascript:void(0);"></a>
				</td>
			</tr>
			<tr>
				<th>数量：</th>
				<th><input type="hidden" id="count" name="count" value="1"/><span id="spareCount"></span></th>
			</tr>
			<tr align="center">
				<td></td>
				<td></td>
				<td>
					<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addSpareStockIn();" href="javascript:void(0);">生成入库单</a>
				</td>
				<td></td>
			</tr>
		</table>
	</form>
</div>
</body>
</html>