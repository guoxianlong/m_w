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
<table width="390px" cellspacing="0" cellpadding="0" >
	<tr height="27px">
		<td colspan="2" style="text-align:right;vertical-align:bottom;padding-right: 45px;">序号&nbsp;&nbsp;<%=ocBean.getBatch() %>-<span class="bold" style="font-size:30px;"><%=ocBean.getSerialNumber() %></span></td>
	</tr>
	<tr height="3px">
		<td width="42%">&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
	<tr height="18px">
		<td style="text-align: left;padding-left:65px;">电商增城</td>
		<td align="right" style="letter-spacing: 2px;vertical-align:top;"><%=ocBean.getOrderDate().substring(0,10) %>&nbsp;&nbsp;&nbsp;&nbsp;<%=DateUtil.getNow().substring(11,16) %></td>
	</tr>
	<tr height="17px">
		<td><span class="test">收寄局：EC9002</span>&nbsp;</td>
		<td><span class="test">销售公司：EC9002</span>&nbsp;</td>
	</tr>
	<tr height="42px">
		<td>&nbsp;</td>
		<td width="120px" style="font-size:20px; padding-left:45px; vertical-align: top;">无锡买卖宝</td>
	</tr>
	<tr height="20px">
		<td>&nbsp;</td>
		<td style="text-align:left;padding-left:58px;">代码Y358</td>
	</tr>
	<tr height="20px">
		<td>&nbsp;</td>
		<td style="padding-left:58px;">40088-43211</td>
	</tr>
	<tr height="36px">
		<td><span class="test">商品名称：</span><%=orderTypeName %></td>
		<td><span class="test">订单号：</span><span class="bold"><%=order.getCode() %></span></td>
	</tr>
	<tr height="33px">
		<td colspan="2" style="text-align:center;vertical-align:bottom;font-size:22px;padding-left:30px;"><span class="bold"><%=priceUpper %></span></td>
	</tr>
	<tr height="24px">
		<td colspan="2" align="center">应收货款￥<span class="bold"><%=priceDown %></span>&nbsp;&nbsp;&nbsp;&nbsp;元</td>
	</tr>
	<tr height="24px">
		<td colspan="2" align="center" style="padding-left:40px;">===不=====保=====价====</td>
	</tr>
</table>
</div>
<div id="table2" style="display:none;">
<table width="340px" cellspacing="0" cellpadding="0">
	<tr height="28px">
		<td colspan="2"><span class="test">收件人名址</span></td>
	</tr>
	<tr height="23px">
		<td style="text-align:left;padding-left:63px;"><%=ocBean.getName() %></td>
	</tr>
	<tr height="68px">
		<td width="280px" style="font-size:16px; padding-left:33px; padding-top:10px; vertical-align: top;"><%=order.getAddress() %></td>
	</tr>
	<tr height="31px">
		<td style="padding-left:25px;"><span class="test">电话：&nbsp;</span><%=order.getPhone() %></td>
	</tr>
	<tr height="30px">
		<td style="text-align:right;padding-right:20px;"><span style="font-size: 30px;font-weight: bold;"><%=postCode%></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=order.getPostcode() %></td>
	</tr>
	<tr height="45px">
		<td style="text-align:left;vertical-align: bottom"><span class="test">重量：(kg)</span><%=apBean.getWeight()/1000>0?apBean.getWeight()/1000:"" %></td>
	</tr>
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
		LODOP.SET_PRINT_PAGESIZE(0,"230mm","127mm","");
		<%
		HashMap printerNameCache=PrinterNameCache.printerNameMap;
		ArrayList printerNameList=(ArrayList)printerNameCache.get(order.getBuyMode()+"-"+order.getDeliver());
		for(int i=0;i<printerNameList.size();i++){
		%>
			var count=LODOP.GET_PRINTER_COUNT();
			for(var j=0;j<count;j++){
				var testName=LODOP.GET_PRINTER_NAME(j);
				if(testName=="<%=printerNameList.get(i)%>"){
					LODOP.SET_PRINTER_INDEXA ("<%=printerNameList.get(i)%>");
					<%if(i==0){%>//第一种包裹单
						LODOP.ADD_PRINT_TABLE("1.4cm","1.1cm","10.2cm","9.2cm",cssStyle+document.getElementById("table1").innerHTML);
						LODOP.ADD_PRINT_TABLE("3.4cm","10.0cm","8.5cm","7.2cm",cssStyle+document.getElementById("table2").innerHTML);
					<%}%>
					//LODOP.PREVIEWB();
					LODOP.PRINTB();
					return true;
				}
			}
		<%}%>
		window.location="printPackage.do?method=printPackage&checkStatus=8";
		return false;
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
		return false;
	}
}
--%>
function initPrint(){
	cssStyle = "<style>table{font-size:16px;}.test{color:white;}.bold{font-weight:bold;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		LODOP.SET_PRINT_PAGESIZE(0,"230mm","127mm","");
		<%
		HashMap printerNameCache=PrinterNameCache.printerNameMap;
		ArrayList printerNameList=(ArrayList)printerNameCache.get(order.getBuyMode()+"-"+order.getDeliver());
		for(int i=0;i<printerNameList.size();i++){
		%>
			var count=LODOP.GET_PRINTER_COUNT();
			for(var j=0;j<count;j++){
				var testName=LODOP.GET_PRINTER_NAME(j);
				if(testName=="<%=printerNameList.get(i)%>"){
					LODOP.SET_PRINTER_INDEXA ("<%=printerNameList.get(i)%>");
					<%if(i==0){%>//第一种包裹单
					LODOP.SET_PRINT_STYLE("FontSize","12");
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
					LODOP.ADD_PRINT_TEXT("81.2mm","87.8mm","26.5mm","5.3mm","<%=order.getPostcode()%>");
					LODOP.SET_PRINT_STYLE("FontSize","14")
					LODOP.ADD_PRINT_TEXT("94.1mm","44.7mm","35mm","5.3mm","<%=apBean.getWeight()/1000%>kg");
					LODOP.ADD_PRINT_TEXT("99.4mm","33.9mm","30mm","5.3mm","<%=orderTypeName %>");
					LODOP.SET_PRINT_STYLE("FontSize","12")
					LODOP.ADD_PRINT_TEXT("99.4mm","50.9mm","65.5mm","5.3mm","打单时间:<%=DateUtil.getNow()%>");
					LODOP.ADD_PRINT_TEXT("105.2mm","46.4mm","5.6mm","5.3mm","√");//物品
					LODOP.ADD_PRINT_TEXT("108.9mm","33.5mm","5.8mm","5.3mm","√");//保价
					LODOP.ADD_PRINT_TEXT("110.9mm","50.5mm","70.5mm","5.3mm","========不==保==价========");
					LODOP.ADD_PRINT_TEXT("42mm","118.6mm","6.4mm","5.3mm","√");//代收货款
					LODOP.ADD_PRINT_TEXT("108.9mm","124.4mm","13.3mm","5.3mm","仙村");
					LODOP.ADD_PRINT_TEXT("90.5mm","141.8mm","7.1mm","5.3mm","√");//付款方式
					LODOP.SET_PRINT_STYLE("FontSize","14")
					LODOP.ADD_PRINT_TEXT("48.4mm","120.5mm","150mm","5.3mm","<%=priceUpper %>");
					LODOP.ADD_PRINT_TEXT("42.2mm","142.9mm","20mm","5.3mm","<%=priceDown %>");
					LODOP.SET_PRINT_STYLE("FontSize","24");
					LODOP.ADD_PRINT_TEXT("20mm","167.9mm","40.7mm","5.3mm","代收货款");
					<%}%>
					//LODOP.PREVIEWB();
					LODOP.PRINTB();
					return true;
				}
			}
		<%}%>
		window.location="printPackage.do?method=printPackage&checkStatus=8";
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