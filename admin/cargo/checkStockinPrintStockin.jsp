<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, java.net.*" %><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.bean.*,adultadmin.util.DateUtil" %>
<%@page import="mmb.stock.stat.*,adultadmin.bean.buy.*" %>
<%
	String code = request.getParameter("stockinCode");
	BuyStockinBean bsBean = (BuyStockinBean) session.getAttribute("buyStockinBean_"+code);
	session.setAttribute("buyStockinBean_"+code, null);
%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>打印入库单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">

<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
<script type="text/javascript">
//CheckLodop();

function initPrint(){
	cssStyle = "<style>table{font-size:14px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	LODOP.PRINT_INIT("入库单打印");
	LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
	LODOP.ADD_PRINT_TABLE("2cm","0.5cm","18.85cm","11.27cm",cssStyle+document.getElementById("stockinDiv").innerHTML);
	LODOP.NEWPAGE();
	//LODOP.PREVIEWB();
	//LODOP.PRINT_DESIGN();
	LODOP.PRINTB();
}
</script>
</head>
<body>
<div id="stockinDiv">
<table width="690px" cellspacing="0" cellpadding="4" border="1">
	<tr bgcolor="#FFFFFF" rowspan="2">
		<td colspan="6">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		入库单编号:<%=bsBean.getCode()%>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		状态：<%= bsBean.getStatusName()%> 
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		生成人：<%= bsBean.getCreateUserName()%>
		<br/>
		<div style="height:5px;"></div>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		来源采购订单：<%= bsBean.getBuyOrder().getCode()%> 
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		来源预计单：<%= bsBean.getBuyStock().getCode()%>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<br/>
		</td>
	</tr>
	<tr bgcolor="#FFFFFF">
		<td width="8%">序号</td>
		<td width="12%">产品线</td>
		<td width="13%">产品编号</td>
		<td width="42%">原名称</td>
		<td width="10%">入库量</td>
		<td width="15%">入库前库存</td>
	</tr>
	<% 
		int x = bsBean.getStockinProductList().size();
		for ( int i = 0; i < x; i ++ ) {
			BuyStockinProductBean bsipBean = (BuyStockinProductBean) bsBean.getStockinProductList().get(i);
	 %>
	<tr bgcolor="#FFFFFF">
		<td width="8%"><%= i + 1 %></td>
		<td width="12%"><%= bsipBean.getProduct().getProductLineName()%></td>
		<td width="13%"><%= bsipBean.getProduct().getCode()%></td>
		<td width="42%"><%= bsipBean.getProduct().getOriname()%></td>
		<td width="10%"><%= bsipBean.getStockInCount()%></td>
		<td width="15%"><%= bsipBean.getTotalStockBeforeStockin()%></td>
	</tr>
	<%
		}
	%>
	<tr bgcolor="#FFFFFF">
		<td width="8%">&nbsp;</td>
		<td width="12%">&nbsp;</td>
		<td width="13%">&nbsp;</td>
		<td width="42%">&nbsp;</td>
		<td width="10%">&nbsp;</td>
		<td width="15%">&nbsp;</td>
	</tr>
	<tr>
		<td colspan="6">
		<div style="width:300px;float:left;">质检部签字：
		</div>
		
		<div style="width:338px;float:left;"><b>|</b>物流部签字：
		</div>
		</td>
	</tr>
	
	
	
</table>
</div>
<div align="center">
<script type="text/javascript">
	initPrint();
	window.close();
</script>
</div>
</body>
</html>