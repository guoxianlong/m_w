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
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
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
		<td colspan="5" style="width: 100mm;height: 17.5mm" align="center" >
			<img src="<%=request.getContextPath()%>/image/logo_szzjPackage.jpg" width="330px" height="54px"/></td>
	</tr>
	<tr>
		<td colspan="5"><strong>客户存联</strong>&nbsp;&nbsp;&nbsp;&nbsp;快递公司：<%=order.getDeliverName() %></td>
	</tr>
	<tr>
		<td rowspan="3" style="width: 4mm; height: 30mm" align="center">
			<strong>客户信息</strong></td>
		<td rowspan="2" colspan="2" style="width: 48mm; height: 16mm;padding-right: 10px;border-right-style:none;">
			客户地址：<br/><%=order.getAddress() %></td>
		<td colspan="2" rowspan="1" style="width: 47mm; height: 14mm;border-left-style:none;">
			</td>
	</tr>
	<tr>
		<td colspan="2" rowspan="2" style="width: 47mm; height: 13mm">
			应收金额：<%=order.getDprice() %>元<br/>
			包裹重量：<%=apBean.getWeight()/1000 %>kg
			</td>
	</tr>
	<tr>
		<td colspan="2" style="width: 48mm; height: 11mm">
			姓名：<%=ocBean.getName() %>&nbsp;&nbsp;邮编：<%=order.getPostcode() %><br/>
			电话：<%=order.getPhone() %></td>
	</tr>
	<tr>
		<td style="width: 4mm; height: 14mm;font-size: 5;" align="center">
			<strong>温馨提示</strong></td>
		<td colspan="4" style="width: 95mm; height: 14mm;padding: 2mm;">
			保留此包裹单底联，代表您已签收并认可我们配送的商品及发货明细单背面规定的内容，如有任何疑问，可通过以下方式联系mmb客服中心<strong>【电话：40088-43211】</strong></td>
	</tr>
	<tr align="center">
		<td colspan="2" style="width: 27mm; height: 6.5mm">
			<strong>配送员存联</strong></td>
		<td colspan="3" style="width: 73mm; height: 6.5mm">
			□很满意&nbsp;&nbsp;□满意&nbsp;&nbsp;□一般&nbsp;&nbsp;□不满意&nbsp;&nbsp;□很差</td>
	</tr>
	<tr>
		<td colspan="5">快递公司：<%=order.getDeliverName() %></td>
	</tr>
	<%---
	<tr>
		<td colspan="1" rowspan="2" style="width: 4mm; height: 22mm" align="center">
			<strong>温馨提示</strong></td>
		<td colspan="2" rowspan="2" style="width: 72mm; height: 22mm;font-size: 1;">
		1，感谢您订购买卖宝产品，请务必当场验货并清点数量。<br/>2，如您收到包裹时发现包裹箱空箱、箱子破损、专用封箱胶开启或使用非买卖宝封箱条重新封装等，请当场拒收并拨打买卖宝客服电话。<br/>
		3，如需要退货，请按包装箱内发货清单背面流程操作，以便我们为您提供更好地服务。</td>
		<td style="width: 23mm; height: 9mm;text-align: center;">
			订单打印时间<br/><%=DateUtil.getNow() %></td>
	</tr>
	 --%>
	 <tr>
		<td rowspan="2" style="width: 4mm; height: 25mm" align="center">
			<strong>客户信息</strong></td>
		<td rowspan="2" colspan="2" style="width: 50mm; height: 25mm;padding-right: 10px;border-right-style: none;">
			<%=order.getAddress() %><br/>
			------------------------<br/>
			姓名：<%=ocBean.getName() %>&nbsp;电话：<%=order.getPhone() %><br/>
			打印时间：<%=DateUtil.getNowDateStr() %>
			</td>
		<td colspan="2" style="width: 45mm; height: 15mm;border-left-style:none;">
			</td>
	</tr>
	<tr>
		<td colspan="2" style="width: 43mm; height: 10.5mm">
			应收金额:<%=order.getDprice() %>元<br/>
			包裹重量:<%=apBean.getWeight()/1000 %>kg</td>
	</tr>
	<tr>
		<td align="right" colspan="4" style="width: 65mm; height: 15mm">
			<strong>客户签名</strong><br/><br/>
			年&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;时</td>
		<td style="width: 23mm; height: 9mm;text-align: center;">
			订单打印时间<br/><%=DateUtil.getNow() %></td>
	</tr>
	<tr>
		<td colspan="1" style="width: 7.4mm; height: 8mm;font-size: 10px;">
			<strong>送货需求</strong></td>
		<td colspan="4" style="width: 93.5mm; height: 8mm;font-size: 2;">
			□只工作日送货(双休.假日不送)&nbsp;&nbsp;□双休日.假日送货(工作日不送)<br/>
			□工作.双休日与节假日均可送货&nbsp;&nbsp;&nbsp;&nbsp;□客户要求时间
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
		LODOP.SET_PRINT_PAGESIZE(0,"102mm","140mm","");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.ADD_PRINT_TABLE("0","-3mm","99mm","140mm",cssStyle+document.getElementById("szzjPackage").innerHTML);
		LODOP.ADD_PRINT_BARCODE("26mm", "52mm","12mm", "10mm", "128A", "<%=orderCode.toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("81mm", "52mm","12mm", "10mm", "128A", "<%=orderCode.toUpperCase()%>");
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