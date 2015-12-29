<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%
	CargoInfoBean cargoInfo = (CargoInfoBean)request.getAttribute("cargoInfo");
	int count = StringUtil.parstInt((request.getAttribute("count")).toString());
%>
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
	cssStyle = "<style>table{font-size:16px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		if(LODOP.PRINT_INIT("")){
		<%if(cargoInfo!=null){
		for (int i = 0; i < count; i++) {
					if( cargoInfo.getWholeCode() != null ) {
		%>
					LODOP.SET_PRINT_PAGESIZE(0,"40mm","15mm","");
			        LODOP.ADD_PRINT_TABLE("0.15cm","0.01cm","1.2cm","3.4cm",cssStyle+document.getElementById("tableDiv").innerHTML);
					//LODOP.ADD_PRINT_BARCODE("17mm","8mm","90mm","20mm","CODE93","<%=cargoInfo.getWholeCode()%>");
					//LODOP.SET_PRINTER_INDEX(-1);
					LODOP.NEWPAGE();
					//LODOP.PREVIEWB();
				    LODOP.PRINTB();
	
		<%
		}else {
			%>
					LODOP.SET_PRINT_PAGESIZE(0,"40mm","15mm","");
			 		LODOP.ADD_PRINT_TABLE("0.15cm","0.01cm","1.2cm","3.4cm",cssStyle+document.getElementById("tableDiv").innerHTML);
					//LODOP.SET_PRINTER_INDEX(-1);
					LODOP.NEWPAGE();
					//LODOP.PREVIEWB();
				    LODOP.PRINTB();
				    
			<%
		}
		}}%>
		}
		window.location='<%=request.getContextPath()%>/admin/returnStorageAction.do?method=changeReturnedProductCargoInfo';
	}


</script>
<title></title>
</head>
<body>

	<%
		if (cargoInfo != null) {
	%>
	<%
		for (int i = 0; i < count; i++) {
				if( cargoInfo.getWholeCode() != null ) {
	%>
	<div id="tableDiv" align="center" >

		<table width='130' >
			<tr >
				<td align="center">
				<%if(cargoInfo.getWholeCode().length()==14){%>
					<b><%=cargoInfo.getWholeCode().substring(6,9)%>-<%=cargoInfo.getWholeCode().substring(9,11)%>-<%=cargoInfo.getWholeCode().substring(11,12)%>-<%=cargoInfo.getWholeCode().substring(12)%></b>
				<%} %>
				<%if(cargoInfo.getWholeCode().length()==12){%>
					<b><%=cargoInfo.getWholeCode().substring(6,9)%>-<%=cargoInfo.getWholeCode().substring(9,11)%>-<%=cargoInfo.getWholeCode().substring(11,12)%></b>
				<%} %>
				</td>
				<td align="left" >合格</td>
			</tr>
			<tr >
				<td align="center"></td>
			</tr>
		</table>
	</div>

	<%
		} else { %>
			<div id="tableDiv" align="center" >

		<table width='130'>
			<tr >
				<td align="center">
				合格
				</td>
			</tr>
			<tr >
				<td align="center"></td>
			</tr>
		</table>
	</div>
		<%
		}
		}
		}
	%>
    <script type="text/javascript">initPrint();</script>
</body>
</html>
