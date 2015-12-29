<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var quoteItem = 0;
function initForm() {
	window.location.reload();
}
function getInputValueByName(items){
	var info = '';
	for (var i = 0; i < items.length; i++) {
     // 如果i+1等于选项长度则取值后添加空字符串，否则为逗号
     info = (info + items.get(i).value) + (((i + 1)== items.length) ? '':',');
	}
	return info;
}
function saveIMEIFun() {
	var isValid = $("#userOrderAddImeiForm").form('validate');
	if (!isValid) {
		return false;
	}
	$.ajax({
        type: "post", //调用方式  post 还是 get
        url: '${pageContext.request.contextPath}/admin/IMEI/userOrderAddImei.mmx',
        async:false,
        data : {
        	orderCode:$("#userOrderAddImeiForm input[id=orderCode]").val(),
        	imeiCodes:getInputValueByName($("#userOrderAddImeiForm input[name='imeiCode']"))
        },
        dataType: "text", //返回的数据的形式
        success: function(result) { 
        	try {
				var r = $.parseJSON(result);
				$.messager.show({
					title : '提示',
					msg : decodeURI(r.msg)
				});
			} catch (e) {
				$.messager.show({
					title : '提示',
					msg : result
				});
			}
        }
	});
}

function addQuoteHtml() {
	if ($("#userOrderAddImeiForm input[name='imeiCode']").length >=5) {
		$.messager.show({
			title : '提示',
			msg : '最多一次添加5个IMEI码'
		});
		return false;
	}
	quoteItem += 1;
	var tr = $("#userOrderAddImeiForm a[id=saveButton]").parent().parent();
	var addItem = "";
	addItem+=('<tr align="center">');
	addItem+=('<th>IMEI码：</th>');
	addItem+=('<td align="left">');
	addItem+=('<input id="imeiCode'+quoteItem+'" name="imeiCode" style="width: 116px;"  class="validatebox" data-options="required:true" />');
	addItem+=('&nbsp;<a class="removeItemClass" onclick="removeQuoteHtml('+quoteItem+');" href="javascript:void(0);"></a>');
	addItem+=('</td>');
	addItem+=("</tr>");
	tr.before(addItem);
	$(".removeItemClass").linkbutton(
		{ 
			plain:true,
			iconCls:'icon-remove'
		}
	);
	$(".validatebox").validatebox(
		{ 
			required:true
		}
	);
}

function removeQuoteHtml(index) {
	$("#imeiCode"+index).parent().parent().remove();
}
</script>
</head>
<body>
		<h3>订单IMEI码添加</h3>
		<br/>
		<fieldset>
			<form id="userOrderAddImeiForm">
				<table id="table" class="tableForm">
					<tr align="center" >
						<th>订单编号：</th>
						<td align="left">
							<input id="orderCode" name="orderCode" style="width: 116px;" class="easyui-validatebox" data-options="required:true"/>
						</td>
					</tr>
					<tr align="center">
						<th>IMEI码：</th>
						<td align="left" >
							<input id="imeiCode" name="imeiCode" class="easyui-validatebox" style="width: 116px;" data-options="required:true" />
							<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addQuoteHtml();" href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr align="center">
						<td colspan="2" align="center">
							<mmb:permit value="2203">
								<a class="easyui-linkbutton" id="saveButton" data-options="iconCls:'icon-save',plain:true" onclick="saveIMEIFun();" href="javascript:void(0);">添加</a>
							</mmb:permit>
							<a class="easyui-linkbutton"  data-options="iconCls:'icon-reload',plain:true" onclick="initForm();" href="javascript:void(0);">重置</a>
						</td>
					</tr>
				</table>
			</form>
		</fieldset>
</body>
</html>