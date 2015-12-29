<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>录入退货原因</title>
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
var hasReasonForm;
var returnsReasonForm
$(function() {

	$('#wareArea').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getWareAreaJSON.mmx',  
		valueField : 'areaId',   
		textField : 'areaName',
		panelHeight:'auto',
	    editable:false
	}); 
	
	hasReasonForm = $('#hasReasonForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/getDoReturnedPackageHasReason.mmx',
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				if (d.status == 'failure') {
					$.messager.alert("提示",d.tip,"info",function(){getFocus()});
				} else if (d.status == 'success') {
					if (d.hasReason == '1') {
						$.messager.confirm('询问', "该订单已录入过原因，确认要修改？", 
							function(b) {
								if (b) {
									$("#code1").attr("value", $("#code").val());
									$("#wareArea1").attr("value", $("#wareArea").combobox("getValue"));
									$("#returnsReasonCode1").attr("value", $("#returnsReasonCode").val());
									returnsReasonForm.submit();
								}
								else {
									getFocus();
								}
							}
						);
					} else {
						$("#code1").attr("value", $("#code").val());
						$("#wareArea1").attr("value", $("#wareArea").combobox("getValue"));
						$("#returnsReasonCode1").attr("value", $("#returnsReasonCode").val());
						returnsReasonForm.submit();
					}
				}
			} catch (e) {
				$.messager.alert("提示","错误！","info", function(){getFocus()});
			}
		}
	});
	
	
	returnsReasonForm = $('#returnsReasonForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/returnsReasonInput.mmx',
		success : function(data) {
			try {
				var d = $.parseJSON(data);
				$.messager.alert("提示",d.tip,"info",function(){getFocus()});
			} catch (e) {
				$.messager.alert("提示","错误！","info", function(){getFocus()});
			}
		}
	});


	$("#code").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
        	$("#returnsReasonCode").focus(); 
        	return false;
        }
    });
	$("#returnsReasonCode").keypress(function(e) {
        var key = window.event ? e.keyCode : e.which;
        if (key.toString() == "13") {
            $("#enter").click(); 
            return false;
        }
    });
	
	getFocus();
});

function getFocus(){
	$("#code").attr("value", "");
	$("#returnsReasonCode").attr("value", "");
	$("#code1").attr("value", "");
	$("#wareArea1").attr("value", "");
	$("#returnsReasonCode1").attr("value", "");
	$("#code").focus();
}
</script>
</head>
<body>
	<div class="easyui-layout" data-options="fit : true,border : false">
		<div data-options="region:'center',border:false">
		</div>
		<div data-options="region:'north',title:'录入退货原因',border:false" style="height: 165px;overflow: hidden;" align="center">
			<form id="hasReasonForm">
				<table class="tableForm">
					<tr>
						<th align="right">库地区：</th>
						<td><input name='wareArea' id='wareArea' style="width:152px;border:1px solid #ccc" /></td>
					</tr>
					<tr>
						<th align="right">订单编号（包裹单号）：</th>
						<td><input id="code"  name="code" size="20" style="width:150px;border:1px solid #ccc"  class="easyui-validatebox" data-options="required:true" />&nbsp;&nbsp;</td>
					</tr>
					<tr>
						<th align="right">退货原因条码：</th>
						<td><input id="returnsReasonCode"  name="returnsReasonCode" class="easyui-validatebox" data-options="required:true" style="width:150px;border:1px solid #ccc"/>&nbsp;&nbsp;</td>
					</tr>
				</table>
				<a id="enter" class="easyui-linkbutton" onclick="hasReasonForm.submit();" data-options="iconCls:'icon-ok',plain:true"  href="javascript:void(0);">确定</a> 
			</form>
			<form id="returnsReasonForm" style="display:none">
				<table class="tableForm">
					<tr>
						<td><input type='hidden' name='wareArea1' id='wareArea1' style="width:152px;border:1px solid #ccc" /></td>
					</tr>
					<tr>
						<td><input  type='hidden'  id="code1"  name="code1" size="20" style="width:150px;border:1px solid #ccc" />&nbsp;&nbsp;</td>
					</tr>
					<tr>
						<td><input type='hidden'  id="returnsReasonCode1"  name="returnsReasonCode1" />&nbsp;&nbsp;</td>
					</tr>
				</table>
			</form>
		</div>
	</div>
</body>
</html>