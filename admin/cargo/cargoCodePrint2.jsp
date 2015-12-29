<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%
	List cargoList = (ArrayList)request.getAttribute("cargoInfoList");
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
	cssStyle = "<style>table{font-size:21px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){
	<%
	if(cargoList!=null){
		for (int i = 0; i < cargoList.size(); i++) {
				CargoOperationCargoBean cocbTemp = (CargoOperationCargoBean) cargoList.get(i);
				if( cocbTemp.getInCargoWholeCode() != null && cocbTemp.getCargoOperation().getCode() != null ) {
					
	%>			LODOP.SET_PRINTER_INDEX(-1);
				LODOP.SET_PRINT_PAGESIZE(0,"60mm","40mm","");
				//LODOP.SET_PRINT_PAGESIZE(0,"60mm","20mm","");
				//LODOP.ADD_PRINT_BARCODE("0.7cm","2mm","40mm","10mm","128A","<%=cocbTemp.getCargoOperation().getCode()%>");
			    //LODOP.ADD_PRINT_TABLE("0cm","1.40cm","1.2cm","3cm",cssStyle+document.getElementById("tableDiv<%=i%>").innerHTML);
				
			    LODOP.ADD_PRINT_BARCODE("1.5cm","3mm","40mm","15mm","128A","<%=cocbTemp.getCargoOperation().getCode()%>");
			    LODOP.ADD_PRINT_TABLE("0.4cm","1.40cm","1.2cm","3.4cm",cssStyle+document.getElementById("tableDiv<%=i%>").innerHTML);
			    
				//LODOP.SET_PRINTER_INDEX(-1);
				//LODOP.NEWPAGE();
				//LODOP.PREVIEWB();
			    LODOP.PRINTB();
	<%
	}else {
		%>
				alert('打印出错!');
		<%
	}
	}}%>
	
	}
	
	<%
		String tip = (String)request.getAttribute("tip");
		if( tip!= null && !tip.equals("no-alert") && !tip.equals("") ){
	%>
		alert("<%= tip %>");
	<%
		}
	%>
	
	<%
		String url = (String)request.getAttribute("url");
		if( url != null && !url.equals("") ) {
	%>
		window.location="<%= url %>";
	<%
		}
	%>	
}

function initPrint2() {
	<%
		String tip2 = (String)request.getAttribute("tip");
		if( tip2!= null && !tip2.equals("no-alert") ){
	%>
		alert("<%= tip2 %>");
	<%
		}
	%>
	//打印 小条码的设置保存
	//LODOP.SET_PRINT_PAGESIZE(0,"40mm","15mm","");
	//	        LODOP.ADD_PRINT_TABLE("0.15cm","0.01cm","1.2cm","3.4cm",cssStyle+document.getElementById("tableDiv").innerHTML);
	
	LODOP.SET_PRINT_PAGESIZE(0,"100mm","40mm","");
		        LODOP.ADD_PRINT_TABLE("-0.3cm","0.01cm","2cm","12cm",cssStyle+document.getElementById("tableDiv").innerHTML);
				LODOP.ADD_PRINT_BARCODE("17mm","8mm","90mm","20mm","CODE93","");
				//LODOP.SET_PRINTER_INDEX(-1);
				//LODOP.NEWPAGE();
				LODOP.PREVIEWB();
			    LODOP.PRINTB();
	
	<%
		String url2 = (String)request.getAttribute("url");
		if( url2 != null) {
	%>
		window.location = '<%= url2 %>';
	<%
		}
	%>	
	
}
</script>
<title></title>
</head>
<body>

	<%
		if (cargoList != null) {
	%>
	<%
		for (int i = 0; i < cargoList.size(); i++) {
				CargoOperationCargoBean cocb = (CargoOperationCargoBean)cargoList.get(i);
				if( cocb.getInCargoWholeCode() != null ) {
	%>
	<div id="tableDiv<%=i%>" align="center" >

		<table width='330' >
			<tr >
				<td align="left" >
				<%if(cocb.getInCargoWholeCode().length()==14){%>
					<b><%=cocb.getInCargoWholeCode().substring(6,9)%><%=cocb.getInCargoWholeCode().substring(9,11)%><%=cocb.getInCargoWholeCode().substring(11,12)%><%=cocb.getInCargoWholeCode().substring(12)%></b>
				<%} %>
				<%if(cocb.getInCargoWholeCode().length()==12){%>
					<b><%=cocb.getInCargoWholeCode().substring(6,9)%><%=cocb.getInCargoWholeCode().substring(9,11)%><%=cocb.getInCargoWholeCode().substring(11,12)%></b>
				<%} %>
				</td>
			</tr>
			<tr >
				<td align="center"></td>
			</tr>
		</table>
	</div>

	<%
		} else { 
	%>
			<div id="tableDiv<%=i%>" align="center" >

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
