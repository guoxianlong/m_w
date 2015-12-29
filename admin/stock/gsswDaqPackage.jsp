<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="cache.PrinterNameCache"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.bean.balance.MailingBalanceBean"%>
<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
String orderCode=request.getAttribute("orderCode")==null?"":request.getAttribute("orderCode").toString();
voOrder order=(voOrder)request.getAttribute("order");
OrderCustomerBean ocBean=(OrderCustomerBean)request.getAttribute("ocBean");
MailingBalanceBean mbBean=(MailingBalanceBean)request.getAttribute("mbBean");
OrderStockBean osBean = (OrderStockBean)request.getAttribute("osBean");
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
String city = (String)request.getAttribute("city");
String orderTypeName = (String)request.getAttribute("orderTypeName");
String dprice = (String)request.getAttribute("dprice");
String color = (String)request.getAttribute("color");
%>
<html>
<head>
<title>广速省外货到付款</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
<div id="szzjPackage">
<!-- Save for Web Slices (配送单--标注.jpg) -->
<table id="__01"  border="1" cellpadding="0" cellspacing="0" style="font-family: SimHei; table-layout:fixed;word-break:break-all;">
	<tr>
		<td style="width: 4mm;height: 0mm;border-style: none;"></td>
		<td style="width: 18mm;height: 0mm;border-style: none;"></td>
		<td style="width: 40mm;height: 0mm;border-style: none;"></td>
		<td style="width: 21mm;height: 0mm;border-style: none;"></td>
		<td style="width: 31mm;height: 0mm;border-style: none;"></td>
	</tr>
	<tr>
		<td colspan="5" style="height:25mm" >
		<img height="30px" src="<%=request.getContextPath()%>/image/ems-mini.png"/><br>
		<font style="font-size:10;margin-left:8mm">国内标准快递</font><br>
		<font style="margin-left:7mm; font-size:28px; font-weight:normal">代收货款</font><br>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=DateUtil.getNow() %>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 10mm">
			<font >应收金额（￥<%=order.getDprice() %>）：<%=dprice %></font><br/>
			重量：<%=apBean.getWeight()/1000 %>kg&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;保价金额：￥0
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 10mm; font-size:13px;overflow:hidden; ">
		<%if(order.getDeliver()==37){ %>
			地址：<font style="font-weight: bold;">大Q<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓（无锡邮政）</font><br>
			发货人:大Q&nbsp;&nbsp;&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %>
		<%}else if(order.getDeliver()==29){ %>
			地址：<font style="font-weight: bold;">大Q<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓（泰州邮政）</font><br>
			发货人:大Q&nbsp;&nbsp;&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %>
		<%}else if(order.getDeliver()==43 || order.getDeliver()==44){ %>
			地址：<font style="font-weight: bold;">沈阳苏家屯机场路1010号电子商务收</font><br>
			发货人:大Q&nbsp;&nbsp;&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %>
		<%}else if(order.getDeliver()==52){ %>
			寄件人：无锡买卖宝	<font style="margin-left:30px"><%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %></font><br>
			寄件地址：买卖宝成都仓
		<%}else{ %>
			寄件人：赵艳	<font style="margin-left:30px"><%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %></font><br>
			寄件地址：广州市站南路4号
		<%} %>
		</td>
	</tr>
	<tr>
		<td colspan="4" style="height: 25mm;font-size:13px;overflow:hidden;" valign="top">
			收件人：<%=order.getName() %><font style="margin-left:30px">电话：<%=order.getPhone() %></font><br>
			收件地址：<%=order.getAddress() %>
		</td>
		<td colspan="1" style="height: 25mm;" valign="top">
			城市：<br>
			<font style="font-size:17px;width:22mm;font-weight:bold;overflow:hidden;"><%=city %></font>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 10mm;font-size:13px;">
			证件名：___________________&nbsp;证件号：________________<br>
			签件人：___________________&nbsp;投递时间：&nbsp;&nbsp;&nbsp;&nbsp;年&nbsp;&nbsp;&nbsp;&nbsp;月&nbsp;&nbsp;&nbsp;&nbsp;日&nbsp;&nbsp;&nbsp;&nbsp;时
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 10mm;">
			<font size=2><%=orderTypeName %></font>
		<%if(color!=null&&color.equals("red")) {%>
			<font size=4>·红</font>
		<%} %>
		<%if(color!=null&&color.equals("green")) {%>
			<font size=4>·绿</font>
		<%} %>
		&nbsp;&nbsp;&nbsp;&nbsp;
		建议配送时间：<%=order.getDealDetail() %>
		</td>
	</tr>
	
    <tr>
		<td colspan="5" style="height:10mm; width:105mm; border-top:dashed 1px;">
		<strong style="font-size:20px;margin-left:60mm;">代收货款</strong>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 9mm">
			<font >应收金额（￥<%=order.getDprice() %>）：<%=dprice %></font><br/>
			重量：<%=apBean.getWeight()/1000 %>kg&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;保价金额：￥0
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 9mm;font-size:13px;overflow:hidden;">
			<%if(order.getDeliver()==37){ %>
				地址：<font style="font-weight: bold;">大Q<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓（无锡邮政）</font><br>
				发货人:大Q&nbsp;&nbsp;&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %>
			<%}else if(order.getDeliver()==29){ %>
				地址：<font style="font-weight: bold;">大Q<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓（泰州邮政）</font><br>
				发货人:大Q&nbsp;&nbsp;&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %>
			<%}else if(order.getDeliver()==43 || order.getDeliver()==44){ %>
				地址：<font style="font-weight: bold;">沈阳苏家屯机场路1010号电子商务收 </font><br>
				发货人:大Q&nbsp;&nbsp;&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %>
			<%}else if(order.getDeliver()==52){ %>
				寄件人：无锡买卖宝	<font style="margin-left:30px"><%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %></font><br>
				寄件地址：买卖宝成都仓
			<%}else{ %>
				寄件人：赵艳&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %><br>
				寄件地址：广州市站南路4号
			<%} %>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="height:15mm; width:105mm;font-size:13px;overflow:hidden;" valign="top">
			收件人：<%=order.getName() %><font style="margin-left:30mm">电话：<%=order.getPhone() %></font><br>
			收件地址：<%=order.getAddress() %>
		</td>
	</tr>
	<tr>
		<td colspan="3" style="width: 105mm; height: 12mm;">
			
		</td>
		<td colspan="2" style="width: 105mm; height: 12mm;font-size:13px;">
			<%=orderTypeName %>&nbsp;&nbsp;<font size="8" ><strong><%=order.getDeliver()%>#</strong></font>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 4mm;font-size:10px;">
			收货前请确认包裹是否完好
		</td>
	</tr>
</table>
<!-- End Save for Web Slices -->
</div>

<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:11px;font-family:Microsoft YaHei;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		LODOP.SET_PRINT_PAGESIZE(0,"105mm","150mm","");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.ADD_PRINT_TABLE("0","-3mm","95mm","150mm",cssStyle+document.getElementById("szzjPackage").innerHTML);
		LODOP.ADD_PRINT_BARCODE("5mm", "50mm","30mm", "20mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("92mm", "3mm","30mm", "9mm", "128A", "<%=order.getCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("137mm", "3mm","30mm", "10mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
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
<%if(request.getAttribute("forward")!=null&&request.getAttribute("forward").toString().equals("scanCheckOrderStock2")){%>
	window.location="<%=request.getContextPath()%>/admin/orderStock/scanCheckOrderStockDQ.jsp";
<%}else{%>
	if(b==true){
		window.location="printPackage.do?method=printPackage&checkStatus=6&orderCode2=<%=orderCode%>";
	}
<%}%>
</script>
</body>
</html>