<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

%>
<html>
<head>
<title>售后处理单号打印</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs6.1.js"></script>
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
<div id="szzjPackage">
</div>
<script type="text/javascript">
var opid=${param.id};
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:12px;font-family:Microsoft YaHei;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //
		LODOP.SET_PRINT_PAGESIZE(1,"40mm","15mm","");
		LODOP.SET_PRINT_STYLE("FontSize",5);
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.SET_PRINT_STYLEA(0,"FontSize",5);
		LODOP.SET_PRINT_STYLEA(0,"Bold",1);
		LODOP.ADD_PRINT_BARCODE("2mm","2mm","36mm","10mm","128A","${param.code}");
		//LODOP.PREVIEWB();//打印预览
		LODOP.PRINTB();
		return true;
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
		return false;
	}
}
</script>
<script type="text/javascript">
   initPrint();
   window.location.href='${pageContext.request.contextPath}/admin/afStock/bsbyAudit.jsp?lookup=1&opid='+opid+'';
</script>
</body>
</html>