<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
</head>
<body>
<div id="table1">
<table width="110px" border="1" cellspacing="0" cellpadding="0">
	<tr height="20px" style="font-size: 14px;">
		<td>订单序号：187</td>
	</tr>
</table>
</div>

<div  id="table2">
<table width="365px" border="1" cellspacing="0" cellpadding="0">
	<tr height="30px">
		<td><span class="test">收寄：</span>电商芳村</td>
		<td><span class="test">收寄日期</span>2011-4-16&nbsp;12:40</td>
	</tr>
	<tr height="27px">
		<td><span class="test">寄件人名：</span>赵艳</td>
		<td style="padding-left:40px;"><span class="test">电话：</span>6006206966</td>
	</tr>
	<tr height="23px">
		<td colspan="2"><span class="test">单位名称：</span>北京商机无限电子商务有限公司</td>
	</tr>
	<tr height="69px">
		<td colspan="2"><span class="test">地址：</span>广州市站南路4号</td>
	</tr>
	<tr height="20px">
		<td><span class="test">用户代码：</span>代码S332</td>
		<td><span class="test">邮政编码：</span>510407</td>
	</tr>
	<tr height="20px">
		<td colspan="2">对号</td>
	</tr>
	<tr height="20px">
		<td><span class="test">内件品名</span></td>
		<td><span class="test">数量</span></td>
	</tr>
	<tr height="65px">
		<td>衣服</td>
		<td>一件</td>
	</tr>
	<tr height="23px">
		<td colspan="2">保价</td>
	</tr>
	<tr height="56px">
		<td>对号</td>
		<td style="text-align:right;vertical-align: bottom;padding-right:10px;">订单号码D11041543645</td>
	</tr>
	<tr height="23px">
		<td><span class="test">交寄人签名：</span>赵艳</td>
		<td>2011-4-16&nbsp;12:40</td>
	</tr>
</table>
</div>
<div id="table3">
<table width="390px" border="1" cellspacing="0" cellpadding="0">
	<tr height="24px">
		<td width="42%" style="padding-left:7px;"><span class="test">收件人姓名</span>陈锦萍</td>
		<td style="text-align:right;padding-right:7px;"><span class="test">电话：</span>13417052531</td>
	</tr>
	<tr height="23px">
		<td colspan="2"><span class="test">单位名称</span></td>
	</tr>
	<tr height="60px">
		<td colspan="2" width="310px" style="padding-left:80px;">广东省汕头市澄海区莲下镇北湾村繁荣路北六巷(电话通知)</td>
	</tr>
	<tr height="25px">
		<td style="padding-left:30px;"><span class="test">城市：</span>汕头市</td>
		<td style="text-align:right;padding-right:9px;letter-spacing: 7px;">849000</td>
	</tr>
	<tr height="68px">
		<td colspan="2" style="font-size:21px;text-align:right;vertical-align: bottom;padding-right:80px;padding-bottom:10px;">陆拾捌元整</td>
	</tr>
	<tr height="24px">
		<td>&nbsp;</td>
		<td>应收货款&nbsp;￥68&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;元</td>
	</tr>
	<tr height="62px">
		<td colspan="2">重量</td>
	</tr>
	<tr height="10px">
		<td colspan="2" style="padding-left:20px;"><span class="test">收寄人员签名：</span>李德斌</td>
	</tr>
</table>
</div>
<input type="button" onclick="initPrint();" value="打印" />
<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:16px;}.test{color:black;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		var barImage = null;
		var barcodeTD = null;
		var barcodeHId = null;
		var top=90;
		var i=0;
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
		var barcodeWidth=42;
		var barcodeLeft=135;
		LODOP.ADD_PRINT_TABLE("0.8cm","8.4cm","2.9cm","0.6cm",cssStyle+document.getElementById("table1").innerHTML);
		LODOP.ADD_PRINT_TABLE("1.6cm","-0.2cm","9.6cm","10.0cm",cssStyle+document.getElementById("table2").innerHTML);
		LODOP.ADD_PRINT_TABLE("2.5cm","9.4cm","10.2cm","8.5cm",cssStyle+document.getElementById("table3").innerHTML);
		//*****
//		LODOP.SET_PREVIEW_WINDOW(1,1,0,0,00,"打印发货清单.打印");	
		LODOP.PREVIEWB();
		//LODOP.PRINTB();
		//	alert(LODOP.SET_PRINT_MODE("PRINT_START_PAGE",2));
		//	alert(LODOP.SET_PRINT_MODE("PRINT_END_PAGE",1));
		//LODOP.PRINT_DESIGN();	
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
}
</script>
</body>
</html>