<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
  <%@ page import="adultadmin.bean.barcode.OrderCustomerBean,adultadmin.util.StringUtil" %>  
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>打印客户信息</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">

<%
OrderCustomerBean orderCustomer = (OrderCustomerBean)request.getAttribute("orderCustomer");
String deliverName=request.getAttribute("deliverName").toString();
int printType = StringUtil.StringToId(request.getParameter("printType"));
%>
</head>
<body>
<p align="center"><b>打印客户信息</b></p>
<div id="printDiv" align="center">
<table cellpadding="0" cellspacing="0" width="340" height="140"  align="center" border="1"
style="border: 1px solid;border-collapse:collapse;">
<tr><td align="left">序列号：<b><%=orderCustomer.getBatch()%>-<%=orderCustomer.getSerialNumber() %></b></td>
<td rowspan="3" id="imageTD" height="60" width="200"><img id="imageB" src="<%=request.getContextPath() %>/barcodeServlet?msg=<%=orderCustomer.getOrderCode() %>&fmt=jpg&type=Code39" width="150" height="50" border="0"/></td></tr>
<tr><td align="left">客户姓名：<b><%=orderCustomer.getName() %></b></td></tr>
<tr><td align="left">时间：<b><%=orderCustomer.getOrderDate().substring(0,16) %></b></td></tr>
<tr><td colspan="2" height="60" style="text-align: left;vertical-align: top;padding-top: 6px;">快递公司：<%=deliverName %></td></tr>
</table>
</div>
<form action="barcodeManager/addConsigPrintlog.jsp" method="post" id="consigForm">
<input type="hidden" name="printCode" value="<%=orderCustomer.getOrderCode() %>"/>
<input type="hidden" name="printType" value="<%=printType %>"/>
</form>
<script type="text/javascript">
	var LODOP;	
	function onLoadMe(){
		var cssStyle = "<style>table{font-size:12;}</style>";
		var barcodeWidth=42;
		var barcodeLeft=53.2;
		var tmpcode ="<%=orderCustomer.getOrderCode()%>";
		if(tmpcode.length==12){barcodeWidth+=2.2;barcodeLeft-=2.2;}
		else if(tmpcode.length==13){barcodeWidth+=2.2;barcodeLeft-=2.2;}
		else if(tmpcode.length==14){barcodeWidth+=4.5;barcodeLeft-=4.5;}
		else if(tmpcode.length==15){barcodeWidth+=9;barcodeLeft-=9;}
		LODOP = getLodop(document.getElementById("LODOP"),document.getElementById("LODOP_EM"));
		LODOP.PRINT_INIT("订单客户信息");
		LODOP.SET_PRINT_PAGESIZE(0,"105mm","41mm","");
		//LODOP.SET_PRINT_STYLE("FontSize",9);
		var imageB = document.getElementById("imageB");
		var imageTD = document.getElementById("imageTD");
		imageTD.removeChild(imageB);
		LODOP.ADD_PRINT_TABLE("1mm","1mm","96.3mm","40mm",cssStyle+document.getElementById("printDiv").innerHTML);
		LODOP.ADD_PRINT_BARCODE("25px",barcodeLeft+"mm",barcodeWidth+"mm","13mm","128A",tmpcode);
		imageTD.appendChild(imageB);
		//LODOP.PREVIEWB();
		//LODOP.PRINT();
		if(LODOP.PRINTB()){
			//location.href="barcodeManager/scanCheckCustomerInfo.jsp";
			document.getElementById("consigForm").submit();
		}else{
			alert("打印客户信息错误，请重试。");
		}
	}
	//setTimeout(onLoadMe,500);
	onLoadMe();
</script>
</body>
</html>