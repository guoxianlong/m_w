<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%
	List list = (List) request.getAttribute("list");
	String url = (String ) request.getAttribute("url");
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
					LODOP.SET_PRINT_PAGESIZE(0,"210mm","290mm","");
					LODOP.ADD_PRINT_HTM("0.2cm","8cm","5cm","0.8cm","<style>table{font-size:22px;}</style>不合格品接收明细表");
			        LODOP.ADD_PRINT_TABLE("1.2cm","0cm","20cm","27cm",cssStyle+document.getElementById("outerDiv").innerHTML);
					//LODOP.SET_PRINTER_INDEX(-1);
					//LODOP.NEWPAGE();
					//LODOP.PREVIEWB();
				    LODOP.PRINTB();
	
		<%
		}
		%>
					
		}
		window.history.back(-1);
	}
	
</script>
</head>
<body>
<div id="outerDiv">
<div align="center">
<table align="center" width="95%" bgcolor="#cccccc" style="border-collapse:collapse;" border="1" >
		<tr bgcolor="#FFFFFF" >
			<td align="center">
			序号
			</td>
			<td align="center">
			产品编号
			</td>
			<td align="center">
			原名称
			</td>
			<td align="center">
			数量
			</td>
			<td align="center">
			到货时间
			</td>
			<td align="center">
			调拨时间
			</td>
			<td align="center">
			调拨单号
			</td>
			<td align="center">
			采购入库单号
			</td>
			<td align="center">
			预计到货单号
			</td>
			<td align="center">
			状态
			</td>
		</tr>
		<% if( list != null && list.size() > 0) {
			for(int i = 0; i < list.size(); i++ ) {
			
				CheckStockinUnqualifiedBean csub = (CheckStockinUnqualifiedBean) list.get(i);
		%>
		
		<tr bgcolor="#FFFFFF" >
			<td align="center">
			<span><%= i + 1%></span>
			</td>
			<td align="center">
			<%= csub.getProduct().getCode()%>
			</td>
			<td align="center">
			<%= csub.getProduct().getOriname()%>
			</td>
			<td align="center">
			<%= csub.getCount()%>
			</td>
			<td align="center">
			<%=StringUtil.convertNull(StringUtil.cutString(csub.getStockinDatetime(), 19))%>
			</td>
			<td align="center">
			<%=StringUtil.convertNull(StringUtil.cutString(csub.getExchangeDatetime(), 19))%>
			</td>
			<td align="center">
			<%= csub.getExchangeCode()%>
			</td>
			<td align="center">
			<%= csub.getBuyStorageCode()%>
			</td>
			<td align="center">
			<%= csub.getBuyStockCode()%>
			</td>
			<td align="left">
			<%= csub.getStatusName()%>
			</td>
		</tr>
		
		<%
				}
			} else { 
		%>
		<tr bgcolor="#FFFFFF" >
			<td align="center" colspan="10">
				没有不合格记录或没有符合查询条件的记录
			</td>
		</tr>
		<%
			}
		%>
	</table>
	</div>
	</div>
	<script type="text/javascript">initPrint();</script>
</body>
</html>
