<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>快速退货</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
function trim( str ) {
	// Immediately return if no trimming is needed
	if( (str.charAt(0) != ' ') && (str.charAt(str.length-1) != ' ') ) { return str; }
	// Trim leading spaces
	while( str.charAt(0)  == ' ' ) {
		str = '' + str.substring(1,str.length);
	}
	// Trim trailing spaces
	while( str.charAt(str.length-1)  == ' ' ) {
		str = '' + str.substring(0,str.length-1);
	}

	return str;
}
var previewOrderStockForm;
var quickCancelStockForm;
var quickCancelStockDialog;
var quickCancelStockDiv;
$(function() {
	previewOrderStockForm = $('#previewOrderStockForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/previewOrderStock.mmx',
		onSubmit : function() {
			return checkSubmit();
		},
		success : function(data) {
			if (data.indexOf("table") >= 0 || data.indexOf("TABLE") >= 0) {
				$("#quickCancelStockDiv").html(data);
				quickCancelStockForm = $('#quickCancelStockForm').form({
					url : '<%=request.getContextPath()%>/SalesReturnController/quickCancelStock.mmx',
					success : function(data) {
						try {
							var d = $.parseJSON(data);
							quickCancelStockDialog.dialog('close');
							$.messager.alert("提示",d.tip,"info",function(){getFocus()});
						} catch (e) {
							$.messager.alert("提示","错误！","info");
						}
					}
				});
				quickCancelStockDialog.dialog('open');
				$("#quickCancel").focus();
			} else {
				$("#orderCode").attr("value", "");
				$("#packageCode").attr("value", "");
				try{
					var d = $.parseJSON(data);
					$.messager.alert("提示",d.tip,"info",function(){getFocus()});
				} catch(e) {
					$.messager.alert("提示","错误！","info");
				}
			}
		}
	});

	
	quickCancelStockDialog = $('#quickCancelStockDialog').show().dialog({
		modal : true,
		title : '预览发货清单',
		closable : false,
		buttons : [ 
		    {
		    	id : 'quickCancel',
				text : '确定',
				handler : function() {
					quickCancelStockForm.submit();
				}
			},
			{
				text : '取消',
				handler : function() {
					quickCancelStockDialog.dialog('close');
				}
			} 
		],
		onClose : function() {
			$("#orderCode").attr("value", "");
			$("#packageCode").attr("value", "");
			getFocus();
		}
	}).dialog('close');
	
	$('#wareArea').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getWareAreaJSON.mmx',  
		valueField : 'areaId',   
		textField : 'areaName',
		panelHeight:'auto',
	    editable:false
	}); 

	$("#packageCode").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
        	$("#enter").click(); 
        	return false;
        }
    });
	$("#orderCode").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
            $("#enter").click(); 
            return false;
        }
    });
});

function calcelSubmit() {
	quickCancelStockDialog.dialog('close');
}
function getScanType1(){
	$("#scanType1").css("display","block");
	$("#scanType2").css("display","none");
	$("#orderCode").focus();
}
function getScanType2(){
	$("#scanType1").css("display","none");
	$("#scanType2").css("display","block");
	$("#packageCode").focus();
}
function getFocus(){
	if($("#scanType1").css("display")=="block"){
		document.getElementsByName("scanType")[0].checked=true;
		getScanType1();
	}else if($("#scanType2").css("display")=="block"){
		document.getElementsByName("scanType")[1].checked=true;
		getScanType2();
	}
}
function checkSubmit(){
	var wareArea = $('#wareArea').combobox('getValue');
	if( wareArea == null || wareArea == "" || wareArea == "-1" ) {
		$.messager.alert("提示","请选择操作的库地区！","info");
		return false;
	}
	if($("#scanType1").css("display")=="block"){
		var orderCode = $("#orderCode").val()
		if(trim(orderCode)==""){
			$.messager.alert("提示","请输入订单编号！","info",function(){getFocus()});
			return false;
		}
	}else if($("#scanType2").css("display")=="block"){
		if(trim($("#packageCode").val())==""){
			$.messager.alert("提示","请输入包裹单号！","info",function(){getFocus()});
			return false;
		}
	}
	return true;
}
</script>
</head>
<body onload="getFocus();">
	<div class="easyui-layout" data-options="fit : true,border : false">
		<div data-options="region:'center',border:false">
		</div>
		<div data-options="region:'north',title:'快速退货——扫描单据',border:false" style="height: 165px;overflow: hidden;" align="center">
			<form id="previewOrderStockForm">
				<table class="tableForm">
					<tr>
						<td colspan="2">
							<input type="radio" name="scanType" value="1" onfocus="getScanType1();" checked="checked"/>扫描订单编号
							<input type="radio" name="scanType" value="2" onfocus="getScanType2();"/>扫描包裹单号
						</td>
					</tr>
					<tr id="scanType1" style="display:block">
						<th>订单编号：</th>
						<td><input id="orderCode"    name="orderCode" size="20" style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;</td>
					</tr>
					<tr id="scanType2" style="display:none">
						<th>包裹单号：</th>
						<td><input id="packageCode"  name="packageCode" size="20" style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;</td>
					</tr>
					<tr id="ware" style="display:block">
						<th>库地区：&nbsp;&nbsp;</th>
						<td><input name='wareArea' id='wareArea' style="width:152px;border:1px solid #ccc"/></td>
					</tr>
				</table>
				<a id="enter" class="easyui-linkbutton" onclick="previewOrderStockForm.submit();" data-options="iconCls:'icon-ok',plain:true" href="javascript:void(0);">确定</a> 
			</form>
		</div>
	</div>
	<div id="quickCancelStockDialog" style="width:700px;height:400px;display: none;overflow: hidden;">
		<div id="quickCancelStockDiv"/>
	</div>
</body>
</html>