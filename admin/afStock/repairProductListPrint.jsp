<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>打印维修发货清单</title>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
		<div id="repairList" style="padding:8px;">
			<table cellpadding="0" cellspacing="0" rules="rows" width="700" border="1" style="border: 1px solid; border-collapse: collapse; font-size: 12px; ">
				<tr height="60px" bordercolor="#00000">
					<td colspan="7">
						<table width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
							<tr>
								<td class="7" align="center" colspan="7">
									<br><font style="font-size: 28px;"><strong>发&nbsp;货&nbsp;清&nbsp;单&nbsp;</strong></font><br>
								</td>
							</tr>
							<tr>
								<td></td>
								<td style="width: 200px; height: 30px; text-align: center; vertical-align: top;" colspan="2">
									<div align="left"><font size='1'>订单号：${orderCode}&nbsp;&nbsp;</font><br></div>
									<div align="left"><font size='1'>客户姓名：${customerName}</font><br></div>
									<div align="left"><font size='1'>快递公司：${backPackage.deliverName}</font></div>
								</td>
								<td style="width: 200px; height: 30px; text-align: center; vertical-align: top;" colspan="2">
									<div align="left"><font size='1'>售后单号：${afterSaleOrderCode}&nbsp;&nbsp;</font><br></div>
									<div align="left"><font size='1'>发货日期：${fn:substring(backPackage.createDatetime, 0, 10)}</font></div>
									<div align="left"><font size='1'>快递单号：${backPackage.packageCode}</font><br></div>
								</td>
								<td colspan="2"></td>
							</tr>
						</table>
					</td>
					<td><hr></td>
				</tr>
				<tr bordercolor="#000000">
					<td width="51" height="30"><div align="center">序号</div></td>
					<td width="51" height="30"><div align="center">商品名称</div></td>
					<td width="51" height="30"><div align="center">商品编号</div></td>
					<td width="51" height="30"><div align="center">故障描述</div></td>
					<td width="51" height="30"><div align="center">维修费用</div></td>
					<td width="51" height="30"><div align="center">IMEI码</div></td>
					<td width="51" height="30"><div align="center">配件明细</div></td>
				</tr>
				<c:forEach items="${shippingList}" var="shipping" varStatus="status">
					<tr>
						<td height="30"><div align="left">${status.count}</div></td>
						<td height="30"><div align="left">${shipping.productName}</div></td>
						<td height="30"><div align="left">${shipping.productCode}</div></td>
						<td height="30"><div align="left">${shipping.faultDescription}</div></td>
						<td height="30"><div align="left">${shipping.repairCost}</div></td>
						<td height="30"><div align="left">${shipping.imei}</div></td>
						<td height="30"><div align="left">${shipping.fittings}</div></td>
					</tr>
				</c:forEach>
				<tr>
					<td height="30" colspan="7" style="border: 1px ;  table-layout:fixed">备注：本清单中只显示维修费用金额，其他费用例如运费等不包含在内。</td>
				</tr>
				<tr>
					<td colspan="7">
						<c:choose>
							<c:when test="${isDaqOrder==true}">
								<table width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
									<tr>
										<td align="center" >
											<p align="left">
												客服热线：4008864966<br>
												售后邮箱：dqsh@ebinf.com
											</p>
										</td>
										<td  align="center">
											<p align="left">
												退换货事宜：详见大Q手机官网<br>
												手机访问：www.daq.cn
											</p>
										</td>
									</tr>
								</table>
							</c:when>
							<c:otherwise>
								<table width="730px" cellpadding="1" style="border: none; border-collapse: collapse;">
									<tr>
										<td align="center" >
											<p align="left">
												客服热线：400-884-3211<br>
												售后邮箱：mmbsh@mmb.cn
											</p>
										</td>
										<td  align="center">
											<p align="left">
												退换货事宜：详见买卖宝网页<br>
												手机访问：mmb.cn
											</p>
										</td>
									</tr>
								</table>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
			</table>
		</div>
</body>
</html>
<script type="text/javascript">
function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
		LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","20cm","24cm",cssStyle+document.getElementById("repairList").innerHTML);
		//LODOP.SET_PRINTER_INDEX(-1);//设置成默认打印机
		//LODOP.PREVIEWB();//打印预览
		LODOP.PRINTB();
		window.close();
	}else{
		alert("打印控件初始化失败，请重新刷新后再试。");
	}
}

window.onload = initPrint;
</script>
