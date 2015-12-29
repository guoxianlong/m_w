<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="cache.PrinterNameCache"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

 %>
<html>
<head>
<title>国内包裹单</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<style type="text/css">
body {
	font-size: 14px;
}
</style>
<%
voOrder order=(voOrder)request.getAttribute("order");
OrderStockBean osBean=(OrderStockBean)request.getAttribute("osBean");
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
OrderCustomerBean ocBean=(OrderCustomerBean)request.getAttribute("ocBean");
String orderCode=request.getAttribute("orderCode")==null?"":request.getAttribute("orderCode").toString();
String orderTypeName=request.getAttribute("orderTypeName")==null?"":request.getAttribute("orderTypeName").toString();
String priceUpper=request.getAttribute("priceUpper")==null?"":request.getAttribute("priceUpper").toString();
String priceDown=request.getAttribute("priceDown")==null?"":request.getAttribute("priceDown").toString();
String postCode=request.getAttribute("postCode")==null?"":request.getAttribute("postCode").toString();
String city=request.getAttribute("city")==null?"":request.getAttribute("city").toString();
%>
</head>
<body>
<div  id="table1" style="display:none;">
<table width="300px" cellspacing="0" cellpadding="0" border="0" >
	<tr height="30px">
		<td width="42%"><font size='3'>电商增城</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=ocBean.getOrderDate().substring(0,16) %></td>
	</tr>
	<tr height="20px">
		<td style="text-align: left;padding-left:7mm;"><font size='3'>赵艳&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;40088-43211</font></td>
	</tr>
	<tr height="30px">
		<td style="text-align: left;padding-left:37mm;">无锡买卖宝</td>
	</tr>
	<tr height="40px">
		<td style="text-align: left;padding-left:27mm;vertical-align: bottom;">广州市站南路4号</td>
	</tr>
	<tr height="25px">
		<td style="text-align: left;padding-left:27mm; vertical-align: top;"><%=order.getCode()%></td>
	</tr>
	<tr height="32px">
		<td style="text-align: left;padding-left:3mm;vertical-align:bottom;">A513&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5&nbsp;&nbsp;1&nbsp;&nbsp;0&nbsp;&nbsp;0&nbsp;&nbsp;0&nbsp;&nbsp;0</td>
	</tr>
	<tr height="17px">
		<td style="text-align: left;padding-left:11mm;vertical-align: top;">√</td>
	</tr>
	<tr height="60px">
		<td style="text-align: left;padding-left:20mm;vertical-align: bottom;"><%=order.getProductTypeName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1件</td>
	</tr>
	<tr height="40px">
		<td style="text-align: left;padding-left:8mm;vertical-align: bottom;">======不====保====价======</td>
	</tr>
	<tr height="25px">
		<td style="text-align: left;padding-left:8mm;">赵艳</td>
	</tr>
</table>
</div>
<div id="table2" style="display:none;">
<table width="370px" cellspacing="0" cellpadding="0" border="0">
	<tr height="30px">
		<td style="text-align: left;padding-left:23mm;"><%=ocBean.getName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=order.getPhone() %></td>
	</tr>
	<tr height="80px">
		<td style="text-align: left;padding-left:27mm;"><%=order.getAddress() %></td>
	</tr>
	<tr height="39px">
		<td style="text-align: left;padding-left:74mm;vertical-align: bottom;"><%=order.getPostcode()%></td>
	</tr>
	<tr height="39px">
		<td style="text-align: left;padding-left:15mm; vertical-align: top;"><%=apBean.getWeight()/1000>0?(apBean.getWeight()/1000)+"":""%></td>
	</tr>
	<tr height="20px">
		<td style="text-align: left;padding-left:75mm;vertical-align: bottom;">仙村</td>
	</tr>
	<!--  <tr height="17px">
		<td><%=priceDown%></td>
	</tr>
	<tr height="17px">
		<td><%=priceUpper%></td>
	</tr>
	<tr height="17px">
		<td><%=ocBean.getName()%></td>
	</tr>
	<tr height="17px">
		<td><%=ocBean.getOrderDate().substring(0,19) %></td>
	</tr>-->
</table>
</div>
<script type="text/javascript">
var LODOP;
//CheckLodop();
<%--
function initPrint(){
	cssStyle = "<style>table{font-size:16px;}.test{color:white;}.bold{font-weight:bold;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		var count=LODOP.GET_PRINTER_COUNT();
		for(var j=0;j<count;j++){
			var testName=LODOP.GET_PRINTER_NAME(j);
			if(testName=="pingyou"){
				LODOP.SET_PRINTER_INDEXA ("pingyou");
				LODOP.SET_PRINT_PAGESIZE(0,"230mm","127mm","");
				LODOP.ADD_PRINT_TABLE("1.9cm","3cm","10.2cm","9.2cm",cssStyle+document.getElementById("table1").innerHTML);
				LODOP.ADD_PRINT_TABLE("2.7cm","10cm","8.5cm","7.2cm",cssStyle+document.getElementById("table2").innerHTML);
				//LODOP.PREVIEWB();
				LODOP.PRINTB();
				return true;
			}
		}
		window.location="printPackage.do?method=printPackage&checkStatus=10";
		return false;
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
		return false;
	}
}
--%>
function initPrint(){
	cssStyle = "<style>table{font-size:19px;}.test{color:white;}.bold{font-weight:bold;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		var count=LODOP.GET_PRINTER_COUNT();
		for(var j=0;j<count;j++){
			var testName=LODOP.GET_PRINTER_NAME(j);
			if(testName=="pingyou"){
				LODOP.SET_PRINTER_INDEXA ("pingyou");
				LODOP.SET_PRINT_PAGESIZE(0,"230mm","127mm","");
				LODOP.SET_PRINT_STYLE("FontSize","12")
				LODOP.ADD_PRINT_TEXT("22.6mm","33.9mm","26.5mm","5.3mm","赵艳");
				LODOP.ADD_PRINT_TEXT("21.5mm","75.1mm","26.5mm","5.3mm","40088-43211");
				LODOP.ADD_PRINT_TEXT("28.1mm","36.8mm","26.5mm","5.3mm","无锡买卖宝");
				LODOP.ADD_PRINT_TEXT("28.9mm","92.3mm","26.5mm","5.3mm","Y358");
				LODOP.ADD_PRINT_TEXT("34mm","35.2mm","80.7mm","8.2mm","广州市站南路4号");
				LODOP.ADD_PRINT_TEXT("42.2mm","35.7mm","40mm","5.3mm","<%=order.getCode()%>");
				LODOP.ADD_PRINT_TEXT("43.5mm","87.3mm","26.5mm","5.3mm","510000");
				LODOP.ADD_PRINT_TEXT("51.7mm","33.6mm","26.5mm","5.3mm","<%=order.getName()%>");
				LODOP.SET_PRINT_STYLE("FontSize","14")
				LODOP.ADD_PRINT_TEXT("51.4mm","76.7mm","35mm","5.3mm","<%=order.getPhone()%>");
				LODOP.SET_PRINT_STYLE("FontSize","12")
				LODOP.ADD_PRINT_TEXT("63.6mm","31.5mm","84.9mm","14.6mm","<%=order.getAddress()%>");
				LODOP.ADD_PRINT_TEXT("81.1mm","35.7mm","26.5mm","5.3mm","<%=city%>");
				LODOP.SET_PRINT_STYLE("FontSize","14")
				LODOP.ADD_PRINT_TEXT("81.1mm","55.7mm","26.5mm","5.3mm","<%=postCode%>");
				LODOP.SET_PRINT_STYLE("FontSize","12")
				LODOP.ADD_PRINT_TEXT("82.2mm","87.8mm","26.5mm","5.3mm","<%=order.getPostcode()%>");
				LODOP.SET_PRINT_STYLE("FontSize","14")
				LODOP.ADD_PRINT_TEXT("94.1mm","44.7mm","35mm","5.3mm","<%=apBean.getWeight()/1000%>kg");
				LODOP.ADD_PRINT_TEXT("99.4mm","33.9mm","30mm","5.3mm","<%=orderTypeName %>");
				LODOP.SET_PRINT_STYLE("FontSize","12")
				LODOP.ADD_PRINT_TEXT("99.4mm","50.9mm","65.5mm","5.3mm","打单时间:<%=DateUtil.getNow()%>");
				LODOP.ADD_PRINT_TEXT("106.2mm","46.4mm","5.6mm","5.3mm","√");
				LODOP.ADD_PRINT_TEXT("110.9mm","34.5mm","5.8mm","5.3mm","√");
				LODOP.ADD_PRINT_TEXT("110.9mm","50.5mm","70.5mm","5.3mm","========不==保==价========");
				//LODOP.ADD_PRINT_TEXT("46mm","120.6mm","6.4mm","5.3mm","√");
				LODOP.ADD_PRINT_TEXT("107.9mm","124.4mm","13.3mm","5.3mm","仙村");
				LODOP.ADD_PRINT_TEXT("91.5mm","141.8mm","7.1mm","5.3mm","√");
				//LODOP.PREVIEWB();
				LODOP.PRINTB();
				return true;
			}
		}
		window.location="printPackage.do?method=printPackage&checkStatus=10";
		return false;
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
		return false;
	}
}
</script>
<script type="text/javascript">
var b=initPrint();
if(b==true){
	window.location="printPackage.do?method=printPackage&checkStatus=6&orderCode2=<%=orderCode%>";
}
</script>
</body>
</html>