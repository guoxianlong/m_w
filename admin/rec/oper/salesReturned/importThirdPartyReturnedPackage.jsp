<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>导入第三方物流退单</title>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
//将theStr的所有replaceStrA替换为replaceStrB
function replaceAllStr(theStr, replaceStrA, replaceStrB) 
{ 
   var re=new RegExp(replaceStrA, "g"); 
   var newstart = theStr.replace(re, replaceStrB); 
   return newstart;
} 
var importThirdPartyReturnedPackageForm;
$(function() {
	$('#wareArea').combobox({   
		url:'<%=request.getContextPath()%>/SalesReturnController/getWareAreaJSON.mmx',  
		valueField : 'areaId',   
		textField : 'areaName',
		panelHeight:'auto',
	    editable:false
	}); 
	importThirdPartyReturnedPackageForm = $('#importThirdPartyReturnedPackageForm').form({
		url : '<%=request.getContextPath()%>/SalesReturnController/importThirdPartyReturnedPackage.mmx',
		success : function(data) {
			try{
				var d = $.parseJSON(data);
				$("#importInfo").attr("value", "");
				$("#totalCount").html(d.totalCount);
				$("#successCount").html(d.successCount);
				$("#failCount").html(d.failCount);
				if (d.result == 'failure') {
					$("#tipArea").attr("value", replaceAllStr(d.errorMsg, "<br>", "\r\n"));
				} else if (d.result == "success"){
					$("#tipArea").attr("value", "");
					$.messager.alert("提示",d.msg,"info");
				} else {
					return;
				}
			} catch(e) {
				$.messager.alert("提示","错误！","info");
			}
		}
	});
	importThirdPartyReturnedPackageForm.submit();
});
</script>
</head>
<body>
	<div class="easyui-layout" data-options="fit : true,border : false">
		<div data-options="region:'center',border:false">
		</div>
		<div data-options="region:'north',title:'导入第三方物流退单',border:false" style="height: 800px;overflow: hidden;" align="center">
		<fieldset style="width:450px;">
   		<div style="background-color:#CCFF80;width:450px;height:500px;border-style:solid;border-width:1px;border-color:#000000;">
			<form id="importThirdPartyReturnedPackageForm">
				<table class="tableForm">
					<tr>
						<td colspan=3>
							<textarea rows="10" cols="50" id="importInfo" name="importInfo"></textarea>
						</td>
					</tr>
					<tr>
						<td style="width:100"></td>
						<td style="width:100">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							<input name='wareArea' id='wareArea' style="width: 80px;"/>
						</td>
						<td style="width:100">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		   					<a id="enter" class="easyui-linkbutton" onclick="importThirdPartyReturnedPackageForm.submit();"  data-options="iconCls:'icon-add',plain:true" href="javascript:void(0);">添加 </a> 
		   				</td>
					</tr>
					<tr>
						<td colspan=3>
							<textarea  rows="10" cols="50" id="tipArea" style="color:red;"></textarea>
						</td>
					</tr>
					<tr>
						<td>
							&nbsp;&nbsp;&nbsp;&nbsp;导入数: 	<font color="red"><span id="totalCount" name="totalCount"></span></font>
						</td>
						<td>
					 		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;成功数：<font color="red"><span id="successCount" name="successCount"></span></font>
					 	</td>
					 	<td> 
					 		&nbsp;&nbsp;&nbsp;&nbsp;失败数：<font color="red"><span id="failCount" name="failCount"></span></font>
					 	</td>
					</tr>
				</table>
			</form>
		</div>
		</fieldset>
		</div>
	</div>
</body>
</html>