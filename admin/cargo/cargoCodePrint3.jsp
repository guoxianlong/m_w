<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<html>
<head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<script>var textname = 'proxytext';</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
	function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:18px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		if(LODOP.PRINT_INIT("")){
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.SET_PRINT_PAGESIZE(1,"40mm","15mm","");
				LODOP.SET_PRINT_STYLE("FontSize",10);
				LODOP.SET_PRINT_STYLE("Bold",1);
		        LODOP.ADD_PRINT_TEXT("2mm","6mm","30mm","4mm","A1314304s");
		        LODOP.ADD_PRINT_BARCODE("7mm","2mm","36mm","7mm","128A","HWTS150728");
				LODOP.PREVIEWB();
			    //LODOP.PRINTB();
	
		}
	}

</script>
<title></title>
</head>
<body>
	<div id="tableDiv" align="center" >

		<table width='10' >
			<tr >
				<td align="left" >
					<b>a1314304</b>
				</td>
			</tr>
			<tr >
				<td align="center"></td>
			</tr>
		</table>
	</div>
    <script type="text/javascript">initPrint();</script>
</body>
</html>
