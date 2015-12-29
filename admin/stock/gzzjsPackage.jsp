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

%>
</head>
<body>
<div  id="table1" style="display:none;">
<table width="390px" cellspacing="0" cellpadding="0" border="0">
	<tr height="30px">
		<td style="text-align: left;padding-left:17mm;"><font size='4'>唐祥</font></td>
	</tr>
	<tr height="30px">
		<td style="text-align: left;padding-left:5mm;"><font size='4'>广州市东风西路&nbsp;29&nbsp;号</font></td>
	</tr>
	<tr height="30px">
		<td style="text-align: left;padding-left:6mm;"><font size='4'>无锡买卖宝 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=order.getCode()%></font></td>
	</tr>
	<tr height="30px">
		<td style="text-align: left;padding-left:6mm;"><font size='4'>40088-43211</font></td>
	</tr>
	<tr height="10px">
		<td>&nbsp;</td>
	</tr>
	<tr height="30px">
		<td style="text-align: left;padding-left:14mm;"><font size='4'><%=ocBean.getName() %></font></td>
	</tr>
	<tr height="30px">
		<td style="text-align: left;padding-left:3mm;"><font size='3'><%=order.getAddress() %></font></td>
	</tr>
	<tr height="30px">
		<td>&nbsp;</td>
	</tr>
	<tr height="30px">
		<td style="text-align: left;padding-left:12mm;"><font size='3'><%=order.getPhone() %></font></td>
	</tr>
	<tr height="40px">
		<td style="text-align: left;padding-left:11mm; vertical-align: bottom;"><font size='3'>1&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=apBean.getWeight()/1000>0?(apBean.getWeight()/1000)+"":""%></font></td>
	</tr>
	<tr height="48px">
		<td style="text-align: left;padding-left:11mm;"><font size='4'><%=order.getProductTypeName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;30x20x10</font></td>
	</tr>
</table>
</div>
<div id="table2" style="display:none;">
<table width="340px" cellspacing="0" cellpadding="0" border="0">
	<tr height="35px">
		<td style="text-align: left;padding-left:9mm;"><font size='3'>5862878</font></td>
	</tr>
	<tr height="15px">
		<td style="text-align: left;padding-left:3.2mm;vertical-align: top;">√</td>
	</tr>
	<tr height="40px">
		<td style="text-align: left;padding-left:7mm;">1</td>
	</tr>
	<tr height="12px">
		<td style="text-align: left;padding-left:17mm;vertical-align: top;">√</td>
	</tr>
	<tr height="25px">
		<td style="text-align: left;padding-left:10mm;vertical-align: bottom;"><font size='4.5'><strong><%=priceDown%></strong></font></td>
	</tr>
	<tr height="20px">
		<td style="text-align: left;padding-left:10mm;vertical-align: middle;"><font size='5'><strong><%=priceUpper%></strong></font></td>
	</tr>
	<tr height="20px">
		<td style="text-align: left;padding-left:10mm;vertical-align: middle;"><font size='5'><strong>可以开箱验货</strong></font></td>
	</tr>
	<tr height="52px">
		<td style="text-align: left;padding-left:12mm;">&nbsp;</td>
	</tr>
	<tr height="23px">
		<td style="text-align: left;padding-left:12mm;"><%=ocBean.getName()%></td>
	</tr>
	<tr height="26px">
		<td style="text-align: left;padding-left:3mm;"><font size='1.5'><%=ocBean.getOrderDate().substring(0,19) %></font></td>
	</tr>
</table>
</div>
<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:16px;}.test{color:white;}.bold{font-weight:bold;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		LODOP.SET_PRINT_PAGESIZE(0,"230mm","139.8mm","");
		LODOP.ADD_PRINT_TABLE("3.2cm","1.4cm","8.7cm","10.2cm",cssStyle+document.getElementById("table1").innerHTML);
		LODOP.ADD_PRINT_TABLE("3.2cm","11.7cm","8.7cm","10.2cm",cssStyle+document.getElementById("table2").innerHTML);
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
		return true;
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