<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>打印备用机出库清单</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
		
<script type="text/javascript" charset="UTF-8">
	var total = 0;
	$(function(){
		$(".gridtable .allCount").each(function(){
			 total = total + ($(this).text() - 0);			
		})
		$("#total").append(total);
	});
</script>
</head>
<body>
		<div id="repairList" style="padding:8px;">
				<table class="gridtable" style="width: 700px ;table-layout:inherit;">
					<tr>
						<th colspan="6" align="center" style="height: 50px">无锡买卖宝备用机返还厂家清单</th>
					</tr>
					<tr>
						<td colspan="2">经销商：无锡买卖宝信息技术有限公司深圳售后服务中心</td>
						<td colspan="1">供应商：${printDetailedMap.supplierName}</td>
						<td colspan="3">供应商地址：${printDetailedMap.supplierAddress}</td>
					</tr>
					<tr>
						<td colspan="3">填单人：${printDetailedMap.userName}</td>
						<td colspan="3">填单日期：${printDetailedMap.dateTime}</td>
					</tr>
					<tr>
						<th align="left" colspan="6">备用机返还厂家清单明细</th>
					</tr>
					<tr>
						<th >序号</th>
			 			<th>商品编号</th>
			 			<th>商品原名称</th>
			 			<th colspan="2">IMEI码</th>
			 			<th>返还数量</th>
					</tr>
					<c:forEach var="item" items="${productDetailMap}" varStatus="ststus">
						<tr>
							<td align="center">${ststus.count}</td>
							<td align="center">${item.value.productCode}</td>
							<td>${item.value.productName}</td>
							<td colspan="2" style="word-break:break-all">${item.value.imei}</td>
							<td align="center"><div class="allCount">${item.value.count}</div></td>
						</tr>
					</c:forEach>
					<tr>
						<th colspan="6" align="left">SKU种类共计：<c:out value="${fn:length(productDetailMap)}"></c:out> 返还数量共计：<a id="total"></a></th>
					</tr>
					<tr>
						<td colspan="6">备注：${printDetailedMap.remark}</td>
					</tr>
					<tr>
						<td colspan="6">
							敬爱的厂商：您好，备用机返还清单如上所示，请及时查实，如果数量与实物不符， 请在24H内通知我司核实，感谢您的支持！ 
						</td>
					</tr>
					<tr>
						<td colspan="4">
								<span>我司地址：${printDetailedMap.ourAddress}</span><br/>
						</td>
						<td colspan="2">
								<span>邮编:${printDetailedMap.zipCode}</span><br/>
						</td>
					</tr>
					<tr>
						<td colspan="4">
								收件人:&nbsp;&nbsp;${printDetailedMap.receiverName}
						</td>
						<td colspan="2">
								电话: ${printDetailedMap.phone}
						</td>
					</tr>
				</table>
		</div>
</body>
</html>
<script type="text/javascript">
function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:13px;border-width: 1px;border-color: #666666;border-collapse: collapse;} table th{border-width: 1px;border-style: solid;} table td{border-width: 1px;border-style: solid;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","297mm","");
		LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","15cm","18cm",cssStyle+document.getElementById("repairList").innerHTML);
		LODOP.ADD_PRINT_BARCODE("10mm", "150mm","36mm", "10mm", "128A", "${PackageCode}");
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
