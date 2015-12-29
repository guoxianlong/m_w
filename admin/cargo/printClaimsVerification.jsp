<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%
	ClaimsVerificationBean cvBean = (ClaimsVerificationBean) request.getAttribute("claimsVerificationBean");
	List list = cvBean.getClaimsVerificationProductList();
	voOrder vorder = (voOrder) request.getAttribute("userOrder");
%>
<html>
<head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<script>var textname = 'proxytext';</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<style>

	.noborder{
		border:0;
	}		
</style>
<script type="text/javascript">
	function initPrint(){
		var LODOP;
		cssStyle = "<style>table{font-size:12px;}</style>";
		var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
		if(LODOP.PRINT_INIT("")){
		<%if( list!=null && list.size() > 0 ){
		
		%>
					LODOP.SET_PRINT_PAGESIZE(0,"210mm","148.5mm","");
			        LODOP.ADD_PRINT_TABLE("0.5cm","20mm","155mm","8cm",cssStyle+document.getElementById("infoDiv").innerHTML);
			        LODOP.ADD_PRINT_TABLE("2.2cm","20mm","155mm","20cm",cssStyle+document.getElementById("outerDiv").innerHTML);
					//LODOP.SET_PRINTER_INDEX(-1);
					//LODOP.NEWPAGE();
					//LODOP.PREVIEWB();
				    LODOP.PRINTB();
	
		<%
		} else {
		%>
		alert("这个理赔单中没有商品，不予打印！");
		<%
		}
		%>
					
		}
		window.close();
	}
	
</script>
</head>
<body>
<div id="infoDiv">
<table align='center' width='100%' border='1' cellspacing='1px' bgcolor='#000000' cellpadding='1px'>
<tr bgcolor='#FFFFFF'>
<td align="left">
<b>理赔单号：<%= cvBean.getCode()%></b>
</td>
<td align="left">
添加人：<%= cvBean.getCreateUserName()%>
</td>
<td align="left">
添加时间：<%= StringUtil.convertNull(StringUtil.cutString(cvBean.getCreateTime(),19))%>
</td>
</tr>
<tr bgcolor='#FFFFFF'>
<td align="left">
订单号：<%= StringUtil.convertNull(cvBean.getOrderCode())%>
</td>
<td align="left">
包裹单号：<%= StringUtil.convertNull(cvBean.getPackageCode())%>
</td>
<td align="left">
快递公司：<%= StringUtil.convertNull(cvBean.getDeliverCompanyNameDirectly())%>
</td>
</tr>
<tr bgcolor='#FFFFFF'>
<td colspan="3">
报损单号：<%= StringUtil.convertNull(cvBean.getBsbyCodesFixed())%>
</td>
</tr>
</table>
</div>
<div id="outerDiv">
<div align="center">
	<%
		int x = list.size();
		if( x > 0 ){
	%>
			<table align='center' width='100%' border='1' cellspacing='1px' bgcolor='#000000' cellpadding='1px' id='orderInfoTable' >
			<tbody>
			<tr bgcolor='#FFFFFF' >
			<td align='center'>
			原名称
			</td>
			<td align='center'>
			产品线
			</td>
			<td align='center'>
			产品编号
			</td>
			<td align='center'>
			数量
			</td>
			<td align='center'>
			有无实物
			</td>
			<td align='center'>
			理赔方式
			</td>
			</tr>
			<%
				for( int i = 0; i < x; i++ ) {
				ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) list.get(i);
			%>
			<tr bgcolor='#FFFFFF' >
				<td align='center'>
				<%= cvpBean.getProduct().getOriname()%>
				</td>
				<td align='center'>
				<%= cvpBean.getProductLineName()%>
				</td>
				<td align='center'>
				<%= cvpBean.getProduct().getCode()%>
				</td>
				<td align='center'>
				<%= cvpBean.getCount()%>
				</td>
				<td align='center'>
				<%= cvpBean.getExist() == 0 ? "无" : "有"%>
				</td>
				<td align='center'>
				<%= cvpBean.getClaimsTypeName()%>
				</td>
				</tr>
			<%
				}
			%>
			</tbody>
			</table>
	<%
		}
	%>
	
	</div>
	</div>
	<script type="text/javascript">initPrint();</script>
</body>
</html>
