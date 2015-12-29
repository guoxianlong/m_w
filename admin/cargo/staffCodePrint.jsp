<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String code=request.getParameter("code");
%>
<html>
<head>
<title>员工条码打印</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
</head>
<body>
<script type="text/javascript">
		cssStyle = "<style>table{font-size:14px;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		LODOP.PRINT_INIT("员工条码打印");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.SET_PRINT_PAGESIZE(0,"60mm","15mm","");
		var code="<%=code%>";
		LODOP.ADD_PRINT_BARCODE("3mm","7.3mm","36mm","10mm","128A",code);
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
		window.close();
</script>
</body>
</html>