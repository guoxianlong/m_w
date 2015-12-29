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
String packageCode=request.getAttribute("orderCode")==null?"":request.getAttribute("orderCode").toString();//包裹单号
voOrder order=(voOrder)request.getAttribute("order");
OrderCustomerBean ocBean=(OrderCustomerBean)request.getAttribute("ocBean");
MailingBalanceBean mbBean=(MailingBalanceBean)request.getAttribute("mbBean");
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
String orderTypeName=request.getAttribute("orderTypeName")==null?"":request.getAttribute("orderTypeName").toString();
String cityCode=request.getAttribute("cityCode")==null?"":request.getAttribute("cityCode").toString();
%>
<html>
<head>
<title>深圳自建和通路速递</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<style type="text/css">
	table{
		border-collapse:collapse;
	}
</style>
</head>
<body>
<div id="gzsfPackage">
<!-- Save for Web Slices (配送单--标注.jpg) -->
<table id="__01" border=0 cellpadding="0" cellspacing="0" bordercolor="#00000" style="font-family: SimHei;width: 100mm;border-collapse:collapse;border:none;">
	<tr height="60px;">
		<td>
			<table style="width:100%;height: 100%; border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td style="width:33mm;">
						<img src="<%=request.getContextPath()%>/image/logo_sfPackage.jpg" width="120px" height="54px"/>
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="50px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td style="width:63mm">
						<b>寄件方信息：</b><br/>
						广东 广州市越秀区 东风路29号 无锡买卖宝信息技术有限公司 唐祥 40088-43211
					</td>
					<td><b>原寄地<br/><font size="5">&nbsp;&nbsp;020</font></b></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="50px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td style="width:63mm">
						<b>收件方信息：</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;自取<br/>
						<%=order.getAddress() %>
					</td>
					<td><b>目的地<br/><font size="5">&nbsp;&nbsp;<%=cityCode %></font></b></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="50px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td style="width:63mm;line-height: 10px;"><%=orderTypeName %></td>
					<td>业务类型</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="25px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td style="width: 18mm;">件数</td>
					<td style="width: 18mm;">实际重量</td>
					<td style="width: 18mm;">计费重量</td>
					<td style="width: 18mm;">费用</td>
					<td style="width: 18mm;">费用合计</td>
				</tr>
				<tr>
					<td>1</td>
					<td><%=apBean.getWeight()/1000 %>kg</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="45px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td rowspan="2" style="width:46mm">付款方式：寄付<br/>月结账号：0203725062<br/>第三方地区：</td>
					<td style="width:26mm">寄方签名：<br/>唐生</td>
					<td>收件员：<br/>288895</td>
				</tr>
				<tr>
					<td colspan="2">寄件日期：<%=DateUtil.getNow().substring(0,10) %></td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="50px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td rowspan="2" style="width:46mm">附加服务：<br/>代收货款：<%=order.getDprice() %> 卡号：0203715062</td>
					<td style="width:26mm;height: 30px;">收方签名：</td>
					<td>派件员：</td>
				</tr>
				<tr>
					<td colspan="2">派件日期：</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="50px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td rowspan="2" style="width:63mm">
						<b>寄件方信息：</b><br/>
						广东 广州市越秀区 东风路29号 无锡买卖宝信息技术有限公司 唐祥 40088-43211
					</td>
					<td>费用合计：</td>
				</tr>
				<tr>
					<td>付款方式：寄付</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr height="60px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td  style="width:63mm">
						<b>收件方信息：</b><br/>
						<%=order.getAddress() %>
					</td>
				</tr>
				
			</table>
		</td>
	</tr>
	<tr height="55px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td >
					<strong>温馨提示</strong>
					</td>
					<td>
						保留此包裹单底联，代表您已签收并认可我们配送的商品及发货明细单背面规定的内容，如有任何疑问，可通过以下方式联系mmb客服中心<strong>【电话：40088-43211】</strong></td>
	
				</tr>
			</table>
		</td>
	</tr>
	<tr height="50px">
		<td>
			<table style="width:100%;height: 100%;vertical-align: top;border-collapse:collapse;" border="1" cellpadding="0" cellspacing="0" bordercolor="#00000">
				<tr>
					<td style="width:63mm">
					</td>
					<td><b>托寄物信息：</b><br/><%=orderTypeName %><%=order.getCode() %></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<!-- End Save for Web Slices -->
</div>

<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:11px;font-family:宋体;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		LODOP.SET_PRINT_PAGESIZE(0,"105mm","150mm","");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.ADD_PRINT_TABLE("4mm","-2mm","105mm","150mm",cssStyle+document.getElementById("gzsfPackage").innerHTML);
		LODOP.ADD_PRINT_BARCODE("6mm", "37mm","12mm", "10mm", "128A", "<%=packageCode.toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("48mm", "15mm","12mm", "9mm", "128A", "<%=order.getCode().toUpperCase()%>");
		LODOP.ADD_PRINT_BARCODE("137mm", "8mm","12mm", "10mm", "128A", "<%=packageCode.toUpperCase()%>");
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
		window.location="printPackage.do?method=printPackage&checkStatus=6&orderCode2=<%=packageCode%>";
	}
<%}%>
</script>
</body>
</html>