<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
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
</head>
<body>
<div  id="table1">
<table width="390px" border="1" cellspacing="0" cellpadding="0">
	<tr height="27px">
		<td colspan="2" style="font-size: 14px;text-align:right;padding-right: 45px;">订单序号:80</td>
	</tr>
	<tr height="8px">
		<td width="42%"><span class="test">原寄局 Office of Origin</span></td>
		<td><span class="test">收寄日期&nbsp;&nbsp;年Y&nbsp;&nbsp;月M&nbsp;&nbsp;日D&nbsp;&nbsp;时H</span></td>
	</tr>
	<tr height="18px">
		<td style="font-size: 14px;text-align: left;padding-left:65px;">电商芳村</td>
		<td align="right" style="font-size:15px;letter-spacing: 2px;">2011-4-14 13:20</td>
	</tr>
	<tr height="8px">
		<td><span class="test">收寄局：EC9002</span></td>
		<td><span class="test">销售公司：EC9002</span></td>
	</tr>
	<tr height="44px">
		<td>&nbsp;</td>
		<td width="120px" style="padding-left:45px; vertical-align: top;">北京商机无限电子商务有限公司</td>
	</tr>
	<tr height="20px">
		<td>&nbsp;</td>
		<td style="text-align:left;padding-left:63px;">代码Y358</td>
	</tr>
	<tr height="20px">
		<td>&nbsp;</td>
		<td><span class="test">客服电话：&nbsp;</span>4006206966</td>
	</tr>
	<tr height="36px">
		<td><span class="test">商品名称：</span>护肤品</td>
		<td><span class="test">订单号：</span>D11041438192</td>
	</tr>
	<tr height="34px">
		<td colspan="2" align="center">柒拾叁元整</td>
	</tr>
	<tr height="24px">
		<td colspan="2" align="center" style="">应收货款﹩73&nbsp;&nbsp;&nbsp;&nbsp;元</td>
	</tr>
</table>
</div>
<div id="table2">
<table width="320px" border="1" cellspacing="0" cellpadding="0">
	<tr height="28px">
		<td colspan="2"><span class="test">收件人名址</span></td>
	</tr>
	<tr height="23px">
		<td style="text-align:left;padding-left:63px;">付晓东</td>
	</tr>
	<tr height="68px">
		<td width="280px" style="font-size:16px; padding-left:33px; padding-top:10px; vertical-align: top;">河北省承德市围场满足蒙古族自治县大筒子沟70号(电话通知)</td>
	</tr>
	<tr height="31px">
		<td style="padding-left:25px;">电话：13513146543</td>
	</tr>
	<tr height="30px">
		<td style="text-align:right;padding-right:8px;letter-spacing: 8px;">849000</td>
	</tr>
</table>
</div>
<input type="button" onclick="initPrint();" value="打印" />
<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:14px;}.test{color:black;}</style>";
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
		LODOP.ADD_PRINT_TABLE("1.3cm","0.4cm","10.2cm","7.2cm",cssStyle+document.getElementById("table1").innerHTML);
		LODOP.ADD_PRINT_TABLE("2.9cm","10.0cm","8.5cm","5.2cm",cssStyle+document.getElementById("table2").innerHTML);
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