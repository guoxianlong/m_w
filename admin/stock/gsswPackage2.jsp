<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
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
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
String city = (String)request.getAttribute("city");
String orderTypeName = (String)request.getAttribute("orderTypeName");
OrderStockBean osBean = (OrderStockBean)request.getAttribute("osBean");
String color = (String)request.getAttribute("color");
%>
<html>
<head>
<title>广速省外非货到付款</title>
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
		<td colspan="5" style="height: 25mm">
			<img height="30px" src="<%=request.getContextPath()%>/image/ems-mini.png"/><br>
			<font style="margin-left:7mm; font-size:30px; font-weight:normal">标准特快</font>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height:15mm;font-size:12px;">
		<%if(order.getDeliver()==37){ %>
			寄件人：买卖宝<font style="margin-left:30mm;">邮编：214101</font><br>
			地址：买卖宝无锡仓（无锡邮政）<br>
			<br/>计费重量(kg):<%=apBean.getWeight()/1000 %><font style="margin-left:23mm">保价金额：0.00元</font>
		<%}else if(order.getDeliver()==29){ %>
			寄件人：买卖宝<font style="margin-left:30mm;">邮编：214101</font><br>
			地址：买卖宝无锡仓（泰州邮政）<br>
			计费重量(kg):<%=apBean.getWeight()/1000 %><font style="margin-left:23mm">保价金额：0.00元</font>
		<%}else if(order.getDeliver()==43 || order.getDeliver()==44){ %>
			寄件人：买卖宝<font style="margin-left:30mm;">邮编：214101</font><br>
			地址：沈阳苏家屯机场路1010号电子商务收<br>
			计费重量(kg):<%=apBean.getWeight()/1000 %><font style="margin-left:23mm">保价金额：0.00元</font>
		<%}else if(order.getDeliver()==52){ %>
			寄件人：无锡买卖宝<font style="margin-left:30mm;">邮编：610000</font><br>
			地址：买卖宝成都仓<br>
			计费重量(kg):<%=apBean.getWeight()/1000 %><font style="margin-left:23mm">保价金额：0.00元</font>
		<%}else{ %>
			寄件人：赵艳<font style="margin-left:30mm;">邮编：510000</font><br>
			地址：广州市站南路4号<br>
			计费重量(kg):<%=apBean.getWeight()/1000 %><font style="margin-left:23mm">保价金额：0.00元</font>
		<%} %>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 30mm;overflow:hidden;font-size:12px;" valign="top">
			<br>
			<strong>收件人：<%=order.getName() %></strong><font style="margin-left:20mm;"><strong>城市：<%=city %></strong></font><br>
			<strong>电话：<%=order.getPhone() %></strong><font style="margin-left:20mm"><strong>邮编：<%=order.getPostcode() %></strong></font><br>
			<strong>地址：<%=order.getAddress() %></strong><br></br>	<br></br>
			收件人签名：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			建议配送时间：<%=order.getDealDetail() %>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 20mm;font-size:12px;">
			订单号：<%=order.getCode().toUpperCase() %><br/>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="height:10mm; width:105mm; border-top:dashed 1px;overflow:hidden;font-size:12px;">
		<%--
			寄件人：赵艳<font style="margin-left:30mm">计费重量(kg):<%=apBean.getWeight()/1000 %></font><br>
			地址：广州市站南路4号<br> --%>
		<%if(order.getDeliver()==37){ %>
			寄件人：买卖宝<font style="margin-left:30mm;">计费重量(kg):<%=apBean.getWeight()/1000 %></font><br>
			地址：买卖宝无锡仓（无锡邮政）<br>
		<%}else if(order.getDeliver()==29){ %>
			寄件人：买卖宝<font style="margin-left:30mm;">计费重量(kg):<%=apBean.getWeight()/1000 %></font><br>
			地址：买卖宝无锡仓（泰州邮政）<br>
		<%}else if(order.getDeliver()==43 || order.getDeliver()==44){ %>
			寄件人：买卖宝<font style="margin-left:30mm;">计费重量(kg):<%=apBean.getWeight()/1000 %></font><br>
			地址：沈阳苏家屯机场路1010号电子商务收<br>
		<%}else if(order.getDeliver()==52){ %>
			寄件人：无锡买卖宝<font style="margin-left:30mm;">计费重量(kg):<%=apBean.getWeight()/1000 %></font><br>
			地址：买卖宝成都仓<br>
		<%}else{ %>
			寄件人：赵艳<font style="margin-left:30mm;">计费重量(kg):<%=apBean.getWeight()/1000 %></font><br>
			地址：广州市站南路4号<br>
		<%} %>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 30mm;overflow:hidden;font-size:12px;" valign="top">
			<br>
			<strong>收件人：<%=order.getName() %></strong><font style="margin-left:30mm">电话：<%=order.getPhone() %></font><br>
			<strong>地址：<%=order.getAddress() %></strong>
		</td>
	</tr>
	<tr>
		<td colspan="5" style="width: 105mm; height: 18mm;font-size:12px;">
			<font style="margin-left:50mm;size=2">订单号：<%=order.getCode().toUpperCase() %>&nbsp;&nbsp;</font><br/>
		<font style="margin-left:50mm;" size=2><%=orderTypeName %></font>
		<%if(color!=null&&color.equals("red")) {%>
			<font style="margin-left:1mm;" size=4>·红</font>
		<%} %>
		<%if(color!=null&&color.equals("green")) {%>
			<font style="margin-left:1mm;"size=4>·绿</font>
		<%} %>
			<font style="margin-left:5mm;" size="8" ><strong><%=order.getDeliver()%>#</strong></font>
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
		LODOP.ADD_PRINT_TABLE("0","-3mm","105mm","150mm",cssStyle+document.getElementById("szzjPackage").innerHTML);
		LODOP.ADD_PRINT_TEXT("2mm", "45mm","50mm", "10mm", "收寄时间:<%=DateUtil.getNowDateStr() %>");
		LODOP.ADD_PRINT_BARCODE("8mm", "45mm","20mm", "15mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("74mm", "50mm","30mm", "15mm", "128A", "<%=order.getCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("133mm", "3mm","30mm", "15mm", "128A", "<%=apBean.getPackageCode().toUpperCase()%>");
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
	window.location="<%=request.getContextPath()%>/admin/orderStock/scanCheckOrderStock2.jsp";
<%}else{%>
	if(b==true){
		window.location="printPackage.do?method=printPackage&checkStatus=6&orderCode2=<%=orderCode%>";
	}
<%}%>
</script>
</body>
</html>