<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>领用单打印</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
	<div id="receiveFittingDiv">
		<table cellpadding="0" cellspacing="0" width="700"  border="1" style="border: 1px solid; border-collapse: collapse; font-size: 14px; ">
			<tr height="60px" bordercolor="#00000">
				<td colspan="4">
					<table width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
						<tr>
							<td style="border: none;width: 180px; text-align: left; vertical-align: middle;">
								<font style="font-size: 12px;"></font>
							</td>
							<td colspan="4" style="border: none;width: 270px; height: 30px; text-align: center; vertical-align: top;">
								<br/>
								<font style="font-size: 28px;"><strong>配件领用单</strong></font>
								<br>
								 <font style="font-size: 24px;"></font>
							</td>
							<td valign="bottom" style="border: none;width: 180px;text-align: right; vertical-align: middle;">
								<br><br><br><br>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td style="width: 20%;">填表日期：${receiveFitting.createDatetime}</td>
				<td style="width: 20%;">填表人：${receiveFitting.createUserName}</td>
				<td style="width: 30%;">审核人：${receiveFitting.auditUserName}</td>
				<td style="width: 30%;">用途：${receiveFitting.targetName}</td>
			</tr>
			<tr>
				<td style="width: 20%;">领用配件总数量：${totalCount}件</td>
				<td style="width: 20%;">领用配件金额：${totalPrice}元</td>
				<td style="width: 30%;" align="right">配件接收人签字：</td>
				<td style="width: 30%;"></td>
			</tr>
			<tr>
				<th  style="width: 40%;" align="left" colspan="2">配件名称</th>
				<th style="width: 30%;" align="left">配件编号</th>
				<th style="width: 30%;" align="left">数量</th>
			</tr>
			<c:forEach items="${fittingList}" var="fitting">
				<tr>
					<td style="width: 40%;" colspan="2">${fitting.fittingName}</td>
					<td style="width: 30%;">${fitting.fittingCode}</td>
					<td style="width: 30%;">${fitting.count}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</body>
</html>
<script>
	function initPrintSealInventory(){
		var LODOP;
		cssStyle = "<style>table{font-size:13px;border-width: 1px;border-color: #666666;border-collapse: collapse;} table th{border-width: 1px;border-style: solid;} table td{border-width: 1px;border-style: solid;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		if(LODOP.PRINT_INIT("")){
			LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
			LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","18.2cm","12.8cm",cssStyle+document.getElementById("receiveFittingDiv").innerHTML);
			LODOP.ADD_PRINT_BARCODE("10mm","150mm","15mm","13mm","128A","${receiveFitting.code}");
			//LODOP.SET_PRINTER_INDEX(-1);//设置成默认打印机
			LODOP.PREVIEWB();//打印预览
			//LODOP.PRINTB();
			window.close();
		}else{
			alert("打印控件初始化失败，请重新刷新后再试。");
		}
	}

	window.onload = initPrintSealInventory;
</script>
