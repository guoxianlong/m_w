<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.util.StringUtil"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>打印邮包条码</title>
</head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<%
String code = StringUtil.convertNull(request.getParameter("code"));
%>
<script type="text/javascript">
//CheckLodop();
function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ 
		        LODOP.SET_PRINT_PAGESIZE(0,"60mm","30mm","");
				LODOP.ADD_PRINT_BARCODE("10mm","10mm","10mm","20mm","CODE93","<%=code%>");
				LODOP.NEWPAGE();
				LODOP.PREVIEWB();
				LODOP.SET_PRINTER_INDEX(-1);
				//LODOP.PRINTB();
	}
}
</script>
<body>

</body>
<script type="text/javascript">
   initPrint();
   window.close();
</script>
</html>