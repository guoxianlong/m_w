<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="mmb.stock.cargo.CargoOperationTodoBean"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.util.StringUtil"%><html>
<%
List cotList=(List)request.getAttribute("cotList");
int type=StringUtil.StringToId(request.getAttribute("type").toString());
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
		<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
<title>待作业打印</title>
</head>
<body>
<div id="print">
<table id="table0" cellpadding="3" border=1 style="border-collapse: collapse;"
		bordercolor="#D8D8D5" align="center" width="95%">
		<tr>
			<td align="center" style="font-weight:bold;color:#000000;">序号</td>
			<td align="center" style="font-weight:bold;color:#000000;"><%if(type==0){ %>装箱单号<%}else if(type==1||type==2||type==3){ %>商品编号<%} %></td>
			<td align="center" style="font-weight:bold;color:#000000;">状态</td>
			<td align="center" style="font-weight:bold;color:#000000;">源货位</td>
			<td align="center" style="font-weight:bold;color:#000000;"><%if(type==0){ %>装箱数量<%}else if(type==1||type==3){ %>商品数量<%}else if(type==2){ %>可用装箱单<%} %></td>
			<td align="center" style="font-weight:bold;color:#000000;">领取人</td>
		</tr>
		<%	
			for(int i = 0; i < cotList.size(); i++){
					CargoOperationTodoBean cot = (CargoOperationTodoBean)cotList.get(i);	
		%>		
			<tr>
				<td align="center"><%=i+1%></td>
				<td align="center"><%=cot.getProductCode() %></td>
				<td align="center"><%=CargoOperationTodoBean.getStatusName(cot.getStatus()) %></td>
				<td align="center"><%=cot.getCargoCode()%></td>
				<td align="center"><%=cot.getCount()%></td>
				<td align="center"><%=cot.getStaffName()==null?"":cot.getStaffName()%></td>
			</tr>
		<%} %>
</table>
</div>
<script type="text/javascript">
	function printOrder(){
		cssStyle = "<style>table{font-size:14px;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		LODOP.PRINT_INIT("合格库待作业打印");
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","290mm","");
		LODOP.ADD_PRINT_TABLE("1cm","1cm","17cm","27cm",cssStyle+document.getElementById("print").innerHTML);
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
		window.opener.location.reload();
	}
	printOrder();
	window.close();
</script>
</body>
</html>