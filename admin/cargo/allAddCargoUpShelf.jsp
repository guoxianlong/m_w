<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoOperationCargoBean"%>
<%@page import="adultadmin.bean.cargo.CargoOperationBean"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="mmb.stock.stat.*"%>
<%
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
%>
<html>
<head>
<title>批量添加退货上架单</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
	</object> 
</head>
<body>
<h3>批量添加退货上架单</h3>
<form action="<%=request.getContextPath() %>/admin/cargoOperation.do?method=allAddCargoUpShelf" method="post">
	<%=wareAreaSelectLable %><br></br>
	<textarea name="data" rows="10" cols="40"></textarea>
	<input type="submit" value="批量添加退货上架单"/>
</form>
<%if(request.getAttribute("msg")!=null){ %>
	<font color="red"><%=request.getAttribute("msg") %></font>
<%} %>
<%if(request.getAttribute("errMsg")!=null){ %>
	<font color="red"><%=request.getAttribute("errMsg") %></font>
<%} %>
<%String tCode=""; %>
<%if(request.getAttribute("tCode")!=null){ %>
	<%tCode=request.getAttribute("tCode").toString(); %>
<%} %>
<%List operList=(List)request.getAttribute("operList"); %>
<%if(operList!=null&&operList.size()>0){ %>
<div id='printDiv' style='display: none;'>
	<table border="1" cellpadding="0" cellspacing="0">
		<tr>
			<td colspan="8" align="center"><font size="4">退货上架汇总单</font></td>
		</tr>
		<tr align="center">
			<td>序号</td>
			<td>上架单号</td>
			<td>产品编号</td>
			<td>数量</td>
			<td>目的货位</td>
			<td>制单时间</td>
			<td>制单人</td>
			<td>作业单状态</td>
		</tr>
		<%for(int i=0;i<operList.size();i++){ %>
			<%CargoOperationCargoBean coc=(CargoOperationCargoBean)operList.get(i); %>
			<%CargoOperationBean co=coc.getCargoOperation(); %>
		<tr align="center">
			<td><%=i+1 %></td>
			<td><%=co.getCode() %></td>
			<td><%=coc.getProduct()==null?"":coc.getProduct().getCode() %></td>
			<td><%=coc.getStockCount() %></td>
			<td><%=coc.getInCargoWholeCode() %></td>
			<td><%=co.getCreateDatetime() %></td>
			<td><%=co.getCreateUserName() %></td>
			<td>已提交</td>
		</tr>
		<%} %>
	</table>
	<script type="text/javascript">
	
		cssStyle = "<style>table{font-size:14px;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		LODOP.PRINT_INIT("物流内部上架单打印");
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","290mm","");
		<%if(tCode.length()>0){%>
			LODOP.ADD_PRINT_BARCODE("12mm","10mm","40mm","17mm","128A","<%=tCode%>");
		<%}%>
		LODOP.ADD_PRINT_TABLE("3cm","1.62cm","15.50cm","27.00cm",cssStyle+document.getElementById("printDiv").innerHTML);
		//LODOP.PREVIEWB();
		//LODOP.PRINT_DESIGN();
		LODOP.PRINTB();
	</script>
</div>
<%} %>
</body>
</html>