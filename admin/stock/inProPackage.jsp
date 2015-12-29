<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.bean.barcode.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="cache.PrinterNameCache"%>
<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
<html>
<head>
<title>省内包裹单</title>
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
String phone="";
String phone1="";
String phone2="";
String name="";
String name1="";
String name2="";
if(order!=null){
	 phone=order.getPhone();
	 String[] phones=phone.split("(　| )");
	 phone1=phones[0];
	 if(phones.length>1){
		 phone2=phones[1];
	 }
}
if(ocBean!=null){
	name=ocBean.getName();
	if(name!=null){
		name=name.trim();
		name=name.replaceAll(" +"," ");
		int nameLength=name.length();
		if(nameLength>6){
			name1=name.substring(0,6);
			name2=name.substring(6,name.length());
		}else{
			name1=name;
		}
	}
	
}
%>
</head>
<body>
<div id="table1" style="display:none;">
<table width="200px" cellspacing="0" cellpadding="0">
	<tr height="20px">
		<td style="vertical-align:top;">序号&nbsp;<span class="bold"><%=ocBean.getBatch() %>-<span class="bold" style="font-size:30px;"><%=ocBean.getSerialNumber() %></span></span></td>
	</tr>
</table>
</div>

<div id="table2" style="display:none;">
<table width="365px" cellspacing="0" cellpadding="0">
	<tr height="30px">
		<td><span class="test">收寄：</span>电商增城</td>
		<td style="text-align:right;"><span class="test">收寄日期</span><%=DateUtil.getNow().substring(0,10) %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=DateUtil.getNow().substring(11,16) %></td>
	</tr>
	<tr height="27px">
		<td><span class="test">寄件人名：</span>赵艳</td>
		<td style="text-align:right;padding-right:25px;"><span class="test">电话：</span>40088-43211</td>
	</tr>
	<tr height="23px">
		<td colspan="2" style="text-align:center;"><span class="test">单位名称：</span>无锡买卖宝</td>
	</tr>
	<tr height="69px">
		<td colspan="2" style="padding-left:60px;"><span class="test">地址：</span>广州市站南路4号</td>
	</tr>
	<tr height="20px">
		<td style="text-align:right;"><span class="test">用户代码：</span>代码S332</td>
		<td style="text-align:right;padding-right:18px;"><span class="test">邮政编码：</span>510407</td>
	</tr>
	<tr height="20px">
		<td colspan="2" style="padding-left:130px;">√</td>
	</tr>
	<tr height="20px">
		<td><span class="test">内件品名</span></td>
		<td><span class="test">数量</span></td>
	</tr>
	<tr height="65px">
		<td style="text-align:center;"><%=orderTypeName %></td>
		<td style="text-align:center;">1件</td>
	</tr>
	<tr height="23px">
		<td colspan="2" style="text-align:right;padding-right:20px;">====不====保====价=====</td>
	</tr>
	<tr height="56px">
		<td style="padding-left:10px;">√</td>
		<td style="text-align:right;vertical-align: bottom;padding-right:10px;"><span class="test">订单号码</span><span class="bold"><%=order.getCode() %></span></td>
	</tr>
	<tr height="23px">
		<td><span class="test">交寄人签名：</span>赵艳</td>
		<td style="text-align:right;"><%=DateUtil.getNow().substring(0,10) %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=DateUtil.getNow().substring(11,16) %></td>
	</tr>
</table>
</div>
<div id="table3" style="display:none;">
<table width="390px" cellspacing="0" cellpadding="0">
	<tr height="24px">
		<td width="42%" style="padding-left:7px;"><span class="test">收件人</span><%=name1 %></td>
		<td style="text-align:right;padding-right:7px;"><span class="test">电话：</span><%=phone1 %></td>
	</tr>
	<tr height="23px">
		<td>&nbsp;<%=name2 %></td>
		<td style="text-align:right;padding-right: 10px;">&nbsp;<%=phone2 %></td>
	</tr>
	<tr height="60px">
		<td colspan="2" width="310px" style="padding-left:80px;"><%=order.getAddress() %></td>
	</tr>
	<tr height="25px">
		<td style="padding-left:30px;"><span class="test">城市：</span>广东省</td>
		<td style="text-align:right;padding-right:9px;"><%=order.getPostcode() %></td>
	</tr>
	<tr height="68px">
		<td style="padding-left:10px;padding-top: 30px;">√</td>
		<td style="font-size:21px;text-align:left;vertical-align: middle;padding-left:10px;"><span class="bold"><%=priceUpper %></span></td>
	</tr>
	<tr height="24px">
		<td>&nbsp;</td>
		<td style="padding-left:12px;">应收货款&nbsp;￥<span class="bold"><%=priceDown %></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;元</td>
	</tr>
	<tr height="62px">
		<td colspan="2" style="vertical-align: top"><span class="test">重量</span>&nbsp;&nbsp;<%=apBean.getWeight()/1000>0?String.valueOf(apBean.getWeight()/1000):"" %></td>
	</tr>
	<tr height="10px">
		<td colspan="2" style="padding-left:20px;"><span class="test">收寄人员签名：</span>仙村</td>
	</tr>
</table>
</div>
<div id="table4" style="display:none;">
<table width="365px" cellspacing="0" cellpadding="0">
	<tr height="30px">
		<td><span class="test">收寄：</span>电商增城</td>
		<td style="text-align:right;"><span class="test">收寄日期</span><%=DateUtil.getNow().substring(0,10) %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=DateUtil.getNow().substring(11,16) %></td>
	</tr>
	<tr height="27px">
		<td><span class="test">寄件人姓名：</span>赵艳</td>
		<td style="text-align:right;padding-right:25px;"><span class="test">电电话：</span>40088-43211</td>
	</tr>
	<tr height="23px">
		<td colspan="2" style="text-align:center;"><span class="test">单位名称：</span>无锡买卖宝</td>
	</tr>
	<tr height="69px">
		<td colspan="2" style="padding-left:60px;"><span class="test">地址：</span>广州市站南路4号</td>
	</tr>
	<tr height="20px">
		<td style="text-align:right;"><span class="test">用户代码：</span>代码S332</td>
		<td style="text-align:right;padding-right:18px;"><span class="test">邮政编码：</span>510407</td>
	</tr>
	<tr height="28px">
		<td colspan="2" style="font-size:21px;text-align:center"><span class="bold"><%=priceUpper %></span></td>
	</tr>
	<tr height="34px">
		<td colspan="2" style="text-align: center;padding-right: 30px;">应收货款&nbsp;￥<span class="bold"><%=priceDown %></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;元</td>
	</tr>
	<tr height="150px">
		<td style="font-size:21px;text-align:center;padding-left:20px;vertical-align: top;padding-top: 40px;"><%=orderTypeName %></td>
		<td style="font-size:21px;text-align:center;padding-left:20px;vertical-align: top;padding-top: 40px;">1件</td>
	</tr>
	<tr height="20px">
		<td colspan="2" style="text-align:right;vertical-align: baseline;padding-right:10px;"><span class="test">订单号码</span><span class="bold"><%=order.getCode() %></span></td>
	</tr>
	<tr height="23px">
		<td><span class="test">交寄人签名：</span>赵艳</td>
		<td style="text-align:right;"><%=DateUtil.getNow().substring(0,10) %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=DateUtil.getNow().substring(11,16) %></td>
	</tr>
</table>
</div>
<div id="table5" style="display:none;">
<table width="390px" cellspacing="0" cellpadding="0">
	<tr height="24px">
		<td width="44%" style="padding-left:7px;"><span class="test">收件人名</span><%=name1 %></td>
		<td style="text-align:right;padding-right:7px;"><span class="test">电话：</span><%=phone1 %></td>
	</tr>
	<tr height="23px">
		<td>&nbsp;<%=name2 %></td>
		<td style="text-align:right;padding-right: 10px;">&nbsp;<%=phone2 %></td>
	</tr>
	<tr height="60px">
		<td colspan="2" width="310px" style="padding-left:80px;"><%=order.getAddress() %></td>
	</tr>
	<tr height="25px">
		<td style="padding-left:30px;"><span class="test">城市：</span>广东省</td>
		<td style="text-align:right;padding-right:9px;"><%=order.getPostcode() %></td>
	</tr>
	<tr height="28px">
		<td colspan="2" style="text-align:left;vertical-align: middle;padding-right:15px;"><span class="test">重量栏：</span><%=apBean.getWeight()/1000>0?String.valueOf(apBean.getWeight()/1000):"" %></td>
	</tr>
	<tr height="28px">
		<td colspan="2" style="text-align:right;vertical-align: middle;padding-right:15px;">仙村</td>
	</tr>
	<tr height="28px">
		<td colspan="2" style="text-align:right;vertical-align: middle;padding-right:15px;">&nbsp;</td>
	</tr>
	<tr height="160px">
		<td colspan="2" style="padding-left:80px;">应收货款&nbsp;￥<span class="bold"><%=priceDown %></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;元</td>
	</tr>
</table>
</div>
<div id="table6" style="display:none;">
<table width="330px" cellspacing="0" cellpadding="0">
	<tr style="text-align: center;height: 37px;">
		<td>电商增城</td>
		<td><%=DateUtil.getNow().substring(0,16) %></td>
	</tr>
	<tr style="text-align: center;height: 25px;">
		<td>赵艳</td>
		<td>40088-43211</td>
	</tr>
	<tr style="text-align: center;height: 50px;">
		<td><span class="test">地址：</span>广州市站南路4号</td>
		<td><%=order.getCode() %></td>
	</tr>
	<tr style="height: 30px;">
		<td><span class="test" style="text-align: center;">代码S332</span>代码S332</td>
		<td style="text-align: right;padding-right: 20px;letter-spacing: 7px;font-size: 14px;">510407</td>
	</tr>
	<tr style="text-align: center;height: 40px;">
		<td><%=name1 %></td>
		<td><%=phone1 %></td>
	</tr>
	<tr style="height:70px;">
		<td colspan="2" style="padding-left: 10px;"><span class="test">地址：</span><%=order.getAddress() %></td>
	</tr>
	<tr>
		<td style="text-align: right;font-size: 30px;font-weight: bold;"><%=postCode %></td>
		<td style="text-align: right;padding-right: 20px;letter-spacing: 7px;font-size: 14px;height: 20px;"><%=order.getPostcode() %></td>
	</tr>
	<tr>
		<td colspan="2" style="text-align: right;padding-right: 100px;height: 30px;vertical-align: bottom">√</td>
	</tr>
	<tr style="text-align: center;height: 60px;padding-top: 20px;">
		<td><%=orderTypeName %></td>
		<td>1</td>
	</tr>
	<tr>
		<td colspan="2" style="vertical-align:bottom;padding-bottom:15px; padding-left: 30px;height: 65px;">√</td>
	</tr>
	<tr style="text-align: center;height: 17px;">
		<td>&nbsp;</td>
		<td>赵艳</td>
	</tr>
	<tr style="height: 30px;">
		<td>&nbsp;</td>
		<td><%=DateUtil.getNow().substring(0,16) %></td>
	</tr>
</table>
</div>
<div id="table7" style="display:none;">
<table width="390px" cellspacing="0" cellpadding="0">
	<tr style="height: 30px;">
		<td style="width: 50%;text-align: center;"><span class="bold"><%=priceUpper %></span></td>
		<td rowspan="2" style="padding-left: 35px;">&nbsp;</td>
	</tr>
	<tr>
		<td style="vertical-align: top;"><span class="test">小写金额</span><span class="bold"><%=priceDown %></span></td>
	</tr>
	<tr>
		<td colspan="2" style="padding-left: 60px;height: 50px;">√</td>
	</tr>
	<tr>
		<td colspan="2" style="vertical-align:top;padding-left: 60px;padding-top: 20px;height: 50px;"><%=apBean.getWeight()/1000>0?String.valueOf(apBean.getWeight()/1000):"" %></td>
	</tr>
	<tr>
		<td colspan="2" style="text-align:right; padding-right: 80px;">仙村</td>
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
		LODOP.SET_PRINT_PAGESIZE(0,"241mm","152.4173mm","");
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
						LODOP.ADD_PRINT_TABLE("0.8cm","7.4cm","2.9cm","0.6cm",cssStyle+document.getElementById("table1").innerHTML);
						LODOP.ADD_PRINT_TABLE("2.0cm","-0.2cm","9.6cm","10.0cm",cssStyle+document.getElementById("table2").innerHTML);
						LODOP.ADD_PRINT_TABLE("2.9cm","9.4cm","10.2cm","8.5cm",cssStyle+document.getElementById("table3").innerHTML);
					<%}else if(i==1){%>//第二种包裹单
						LODOP.ADD_PRINT_TABLE("0.8cm","7.4cm","2.9cm","0.6cm",cssStyle+document.getElementById("table1").innerHTML);
						LODOP.ADD_PRINT_TABLE("1.9cm","-0.2cm","9.6cm","11.6cm",cssStyle+document.getElementById("table4").innerHTML);
						LODOP.ADD_PRINT_TABLE("2.9cm","9.4cm","10.2cm","10.0cm",cssStyle+document.getElementById("table5").innerHTML);
					<%}else if(i==2){%>//第三种包裹单
						LODOP.ADD_PRINT_TABLE("1.6cm","-0.5cm","9.6cm","16.0cm",cssStyle+document.getElementById("table6").innerHTML);
						LODOP.ADD_PRINT_TABLE("3.5cm","8.4cm","10.2cm","8.5cm",cssStyle+document.getElementById("table7").innerHTML);
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