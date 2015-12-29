<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
<html>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<head>
<title>售后处理单号打印</title>
<script language="javascript" src="${pageContext.request.contextPath}/admin/barcodeManager/LodopFuncs6.1.js"></script>
<style type="text/css">
<!--
.STYLE19 {
	font-size: x-large;
	font-weight: bold;
}
-->
</style>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
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
function printFun(){
	var count = $('#count').numberbox('getValue');
	if(count == ''){
		alert("打印数量不能为空!");
		return;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/createDetectCode.mmx',
		type : 'post',
		data : {count :count},
		dataType : 'json',
		cache : false,
		success : function(r){
			if(r.success == true){
				for(var d in r.obj){
					initPrint(r.obj[d]);
				}
				alert("打印成功!");
			}else {
				$.messager.show({
					msg : r.msg,
					title : '提示'
				});
			}
			$('#count').numberbox('setValue',0);
		}
	});
}
</script>
	<div align="center">
		<form  id="form">
			<table class="tableForm" >
				<tr>
					<th>打印数量：</th>
					<td><input id="count" name="count" class="easyui-numberbox" required="required" style="width: 155px;" /></td>
				</tr>
				<tr align="center">
					<th></th>
					<td ><a class="easyui-linkbutton" onclick="printFun();" href="javascript:void(0);">打印</a></td>
				</tr>
			</table>
		</form>
	</div>
</body>
</html>
