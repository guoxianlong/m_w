<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="cache.PrinterNameCache"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.bean.balance.MailingBalanceBean"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
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
OrderStockBean osBean = (OrderStockBean)request.getAttribute("osBean");
String orderTypeName=request.getAttribute("orderTypeName")==null?"":request.getAttribute("orderTypeName").toString();
String addr = request.getAttribute("addr")==null?"":request.getAttribute("addr").toString();
%>
<html>
<head>
<title>深圳自建和通路速递</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
<div id="szzjPackage">
<!-- Save for Web Slices (配送单--标注.jpg) -->
<table id="__01"  border="1" cellpadding="0" cellspacing="0" style="font-family: SimHei;">
	<tr>
		<td style="width: 4mm;height: 0mm;border-style: none;"></td>
		<td style="width: 18mm;height: 0mm;border-style: none;"></td>
		<td style="width: 40mm;height: 0mm;border-style: none;"></td>
		<td style="width: 21mm;height: 0mm;border-style: none;"></td>
		<td style="width: 31mm;height: 0mm;border-style: none;"></td>
	</tr>
	<tr>
		<td colspan="5" style="width: 100mm;height: 18mm;" align="left" >
			<img src="<%=request.getContextPath()%>/image/dQ.jpg" width="110px" height="31px"/>&nbsp;<font size='5'><%if(voOrder.deliverInfoMapAll.get(order.getDeliver()).getIsems()==1){ %>EMS<%} %></font></td>
	</tr>
	<tr>
		<td colspan="5" style="height: 7mm"><strong>配送员存联</strong>&nbsp;&nbsp;&nbsp;&nbsp;快递公司：<%=order.getDeliverName() %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		 <font ><strong><%if(voOrder.deliverInfoMapAll.get(order.getDeliver()).getIsems()==1){ %>省内<%}%><%if(order.getBuyMode()==0){%>代收货款<%}else {%>已付款<%}%></strong></font></td>
	</tr>
	<tr>
		<td rowspan="3" style="width: 4mm; height: 30mm" align="center">
			<strong>收件人信息</strong></td>
		<td rowspan="2" colspan="3" style="width: 47mm; height: 17mm;padding-right: 10px;border-right-style:none;">
			客户地址：<font ><strong><%=addr %></strong></font><br/><%=order.getAddress() %></td>
		<td colspan="1" rowspan="1" style="width: 47mm; height: 17mm;">
			<%if(order.getBuyMode()==0){%>代收金额：<font style="font-size: 14px;font-weight: bold;"><%=order.getDprice() %></font>元<%}else{%>已付款<%} %><br/>
		</td>
	</tr>
	<tr>
		<td colspan="2" rowspan="2" style="width: 47mm; height: 13mm">
			包裹重量：<%=apBean==null?"0.0":apBean.getWeight()/1000 %>kg
			</td>
	</tr>
	<tr>
		<td colspan="3" style="width: 47mm; height: 13mm">
			姓名：<%=order.getName() %>&nbsp;&nbsp;邮编：<%=order.getPostcode() %><br/>
			电话：<%=order.getPhone() %></td>
	</tr>
	<tr>
		<td align="right" colspan="4" style="width: 65mm; height: 16mm">
			<strong>客户签名</strong><br/><br/>
			&nbsp;&nbsp;&nbsp;&nbsp;
		建议配送时间：<%=order.getDealDetail() %></td>
		<td style="width: 23mm; height: 16mm;text-align: center;">
			订单打印时间<br/><%=DateUtil.getNow() %></td>
	</tr>
	<tr>
		<td colspan="1" style="width: 4mm; height: 17mm;font-size: 10px;">
			<strong>寄件方</strong></td>
		<td colspan="3" >
			地址:<font style="font-size: 14px;font-weight: bold;">大Q<%=ProductStockBean.areaMap.get(osBean.getStockArea()) %>仓</font>
			<font style="font-size: 13px;font-weight: bold;">
			<%if(voOrder.deliverInfoMapAll.get(order.getDeliver()).getAddress()!=null){%>(<%=voOrder.deliverInfoMapAll.get(order.getDeliver()).getAddress()%>)<%}%>
			</font><br>
			
			发货人:大Q&nbsp;&nbsp;&nbsp;&nbsp;<%if (!order.isTaobaoOrder()) { %>电话：4008864966<%} %>
			</td>
		<td colspan="1" style="text-align: center;font-size: 60px;font-weight: bold;"><%=order.getDeliver() %></td>
	</tr>
	<tr>
		<td align="left" colspan="4" style="width: 65mm; height: 10mm;">
			订单:</td>
		<td style=" height: 10mm;text-align: left ;">
			快递公司：<%=order.getDeliverName() %><br>
			品&nbsp;&nbsp;&nbsp;类：<%=orderTypeName %>
			</td>
	</tr>
	 <tr>
		<td rowspan="2" style="width: 4mm; height: 17mm" align="center">
		<strong>客户信息</strong></td>
			<td colspan="4" style="height: 9mm;">地址:<%=order.getAddress() %></td>
			
	</tr>
	<tr>
			<td colspan="3" style="height: 8mm;">
			姓名：<%=ocBean.getName() %>&nbsp;电话：<%=order.getPhone() %><br/>
			打印时间：<%=DateUtil.getNowDateStr() %></td>
			<td colspan="1" style="height: 8mm;"><%if(order.getBuyMode()==0){%>代收金额：<%}else {%>订单金额：<%}%> <%=order.getDprice() %>元<br/>
			包裹重量：<%=apBean==null?"0.0":apBean.getWeight()/1000 %>kg</td>
	</tr>
	<%--
	<tr>
		<td colspan="2" style="width: 43mm; height: 10.5mm">
			应收金额:<%=order.getDprice() %>元<br/>
			包裹重量:<%=apBean.getWeight()/1000 %>kg</td>
	</tr>
	 --%>
	 <tr>
		<td style="width: 4mm; height: 14mm;font-size: 10px;" align="center">
			<strong>温馨提示</strong></td>
		<td colspan="4" style="width: 95mm;padding: 1mm;">
		保留此包裹单底联，代表您已签收并认可我们配送的商品及发货明细单背面规定的内容
		 <%if (!order.isTaobaoOrder()) { %>
		，如有任何疑问，可通过以下方式联系大Q商城客服中心
				<%if(order.getDeliver()==42 ){%>
					<strong>【电话：40088-43211】</strong></td>
				<%}else{%>
					<strong>【电话：40088-64966】</strong></td>
				<%}%>
		<%} %>
	</tr>
	<tr>
		<td colspan="5" style="width: 100mm;height: 17mm;" align="left" >
			<%if (order.getDeliver() != 45 &&order.getDeliver() != 50 &&order.getDeliver() != 26 &&order.getDeliver() != 27) {%>
				<img src="<%=request.getContextPath()%>/image/dQ.jpg" width="110px" height="31px"/>&nbsp;<font size='5'><%if(voOrder.deliverInfoMapAll.get(order.getDeliver()).getIsems()==1){ %>EMS<%} %></font>
			<%} %>
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
		LODOP.ADD_PRINT_TABLE("0mm","-2mm","105mm","150mm",cssStyle+document.getElementById("szzjPackage").innerHTML);
		LODOP.ADD_PRINT_BARCODE("3mm", "39mm","12mm", "15mm", "128A", "<%=orderCode.toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("91mm", "9mm","12mm", "10mm", "128A", "<%=order.getCode()%>");
		<%if (order.getDeliver() != 45 &&order.getDeliver() != 50 &&order.getDeliver() != 26 &&order.getDeliver() != 27 ) {%>
			LODOP.ADD_PRINT_BARCODE("132mm", "39mm","48mm", "15mm", "128A", "<%=orderCode.toUpperCase()%>");
		<%} else {%>
			LODOP.ADD_PRINT_BARCODE("132mm", "13mm","80mm", "13mm", "128A", "<%=orderCode.toUpperCase()%>");
		<%}%>
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
<%if(request.getAttribute("forward")!=null&&(request.getAttribute("forward").toString().equals("scanCheckOrderStock2")||request.getAttribute("forward").toString().equals("scanCheckOrderStock3"))){%>
	window.location="<%=request.getContextPath()%>/admin/orderStock/scanCheckOrderStockDQ.jsp";
<%}else{%>
	if(b==true){
		window.location="printPackage.do?method=printPackage&checkStatus=6&orderCode2=<%=orderCode%>";
	}
<%}%>
</script>
</body>
</html>