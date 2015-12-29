<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<title>寄回用户打印小面单</title>
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
		<td style="width: 28mm;height: 0mm;border-style: none;"></td>
		<td style="width: 40mm;height: 0mm;border-style: none;"></td>
		<td style="width: 31mm;height: 0mm;border-style: none;"></td>
		<td style="width: 34mm;height: 0mm;border-style: none;"></td>
	</tr>
	<tr>
		<td colspan="5" style="width: 98mm;height: 17.5mm" align="left" >
			<img src="<%=request.getContextPath()%>/image/logo_cartonning.jpg" width="150px" height="54px"/></td>
		<td></td>
	</tr>
	<tr>
		<td colspan="5" style="height: 8mm;" ><strong>客户存联</strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>快递公司</strong>:${backPackage.deliverName} &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<strong>已付款</strong></td>
	</tr>
	<tr>
		<td rowspan="2" style="width: 4mm; height: 28mm" >
			<strong>收件人信息</strong></td>
		<td colspan="4" style="width: 60mm; height: 14mm;padding-right: 10px;border-right-style:none;">
			客户地址:<br/>&nbsp;&nbsp;&nbsp;&nbsp;${backPackage.userAddress}</td>
	</tr>
	<tr>
		<td colspan="3" style="width: 34mm; height: 14mm">
			姓名：${backPackage.customerName}&nbsp;&nbsp;邮编:${backPackage.customerPostCode}<br/><br/>
			电话：${backPackage.userPhone}</td>
			<td colspan="1" style="width: 47mm; height: 15mm;padding: 2mm;">
			重量：${backPackage.weight}kg
			</td>
	</tr>
	<tr>
		<td style="width: 4mm; height: 13mm;" >
			<strong>签收</strong></td>
		<td colspan="3" style="width: 60mm; height: 13mm;padding: 2mm;">
			客户签名:<img src="<%=request.getContextPath()%>/image/as_miandan_qianzitixing.png" border="0" /></td>
		<td colspan="1" style="width: 34mm; height: 13mm;padding: 2mm;">
			时间:${backPackage.printTime}
	</tr>
	<tr>
		<td style="width: 4mm; height: 15mm;" >
			<strong>寄件方</strong></td>
		<td colspan="4" style="width: 98mm; height: 15mm;padding: 2mm;">
			买卖宝售后&nbsp;&nbsp;&nbsp;&nbsp;电话：4008869499<br><br>
			深圳市龙华新区和平工业园昌永路金星大厦8楼A801
		</td>
	</tr>
	<tr style=" height: 0mm;border-style: none;">
		<td  colspan="5" style="width: 98mm; height: 0mm;border-style: none;">------------------------------------------------------------</td>
	</tr>
	<tr>
		<td rowspan="1" style="width: 4mm; height: 2mm" ><strong>收件人</strong></td>
		<td colspan="3" style="width: 60mm; height:2mm;padding-right: 10px;border-right-style: none;">
			姓名：${backPackage.customerName}&nbsp;&nbsp;邮编:${backPackage.customerPostCode}<br/><br/>
			电话：${backPackage.userPhone}</td>
		<td colspan="1" style="width: 34mm; height: 2mm;">
			重量:${backPackage.weight}kg<br><br/>
			时间:${backPackage.printTime}
			</td>
	</tr>
	<tr>
		<td rowspan="1" style="width: 4mm; height: 12mm"><strong>寄件方</strong></td>
		<td colspan="3" style="width: 60mm; height: 12mm;padding-right: 10px;border-right-style: none;">
			买卖宝售后&nbsp;&nbsp;电话：4008869499<br>
			深圳市龙华新区和平工业园昌永路金星大厦8楼A801</td>
		<td colspan="1" style="width: 34mm; height: 12mm"></td>
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
		LODOP.SET_PRINT_PAGESIZE(0,"99mm","120mm","");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.ADD_PRINT_TABLE("2","0mm","99mm","119mm",cssStyle+document.getElementById("szzjPackage").innerHTML);
		LODOP.ADD_PRINT_BARCODE("12mm", "65mm","12mm", "10mm", "128A", "${backPackage.packageCode}");
		LODOP.ADD_PRINT_BARCODE("32mm", "65mm","12mm", "10mm", "128A", "${backPackage.packageCode}");
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
window.close();
</script>
</body>
</html>