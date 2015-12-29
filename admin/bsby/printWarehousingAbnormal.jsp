<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="cache.PrinterNameCache"%>
<%@	page import="adultadmin.bean.stat.WarehousingAbnormalBean"%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
List<voProduct> vpList = (ArrayList<voProduct>)request.getAttribute("vpList");
List<voProduct> rpList = (ArrayList<voProduct>)request.getAttribute("rpList");
System.out.println(rpList.size() + "");
List<WarehousingAbnormalBean> bsbyList = (ArrayList<WarehousingAbnormalBean>)request.getAttribute("bsbyList");
AuditPackageBean apBean = (AuditPackageBean)request.getAttribute("apBean");
WarehousingAbnormalBean abnormalBean = (WarehousingAbnormalBean)request.getAttribute("abnormalBean");
%>
<html>
<head>
<title>深圳自建和通路速递</title>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
</head>
<body>
<div id="warehousingAbnormal">
<table width="98%" >
	<tr>
		<td colspan="2"  align="center">
			<table  border="1"  width="97%"  cellspacing="0" >
				<tr  align="center">
					<td width="10%">异常入库单</td><td width="25%"><%=abnormalBean.getCode()%></td>
					<td width="10%">添加人</td><td width="25%"><%=abnormalBean.getOperatorName()%></td>
				</tr>
				<tr  align="center">
					<td width="10%">添加时间</td><td width="20%"><%=abnormalBean.getCreateTime()%></td>
					<td width="10%">订单号</td><td width="25%"><%=apBean.getOrderCode() %></td>
				</tr>
				<tr  align="center">
					<td width="10%">包裹单号</td><td width="25%"><%=apBean.getPackageCode() %></td>
					<td width="10%">快递公司</td><td width="20%"><%=voOrder.deliverMapAll.get(apBean.getDeliver()+"") %></td>
				</tr>
				<tr  align="center">
					<td width="10%">报损报溢单</td><td width="25%">
					
					<%if(bsbyList != null && bsbyList.size()>0){
						for(WarehousingAbnormalBean bean : bsbyList){%>
							<%=bean.getReceiptsNumber() %>&nbsp;
						<%}
					}else{ %>无<%} %></td>
					<td width="10%">&nbsp;</td><td width="25%">&nbsp;</td>
				</tr>
			</table></td></tr>
	<tr>
		<td width="50%">
			<table border="1"width="94%" align="center"  cellspacing="0" bgcolor="FFFFE0">
				<tr bgcolor="#00ccff" align="center">
					<td>订单中商品</td><td>原名称</td><td>商品名称</td><td>数量</td></tr>
				<% if(vpList!= null && vpList.size()>0){for(voProduct bean:vpList){ %>
				<tr align="center">
					<td><%=bean.getCode() %></td><td><%=bean.getOriname() %></td><td><%=bean.getName() %></td><td><%=bean.getCount() %></td>
				</tr>
				<%}} %>
			</table></td>
		<td  width="50%">
			<table border="1"   width="95%" align="center"  cellspacing="0" bgcolor="FFFFE0">
			<tr bgcolor="#00ccff" align="center">
				<td>实际退回商品</td><td>原名称</td><td>商品名称</td><td>数量</td></tr>
			<tr align="center">
			<% if(rpList!= null && rpList.size()>0){for (voProduct bean:rpList) { %>
			<tr align="center">
				<td><%=bean.getCode() %></td><td><%=bean.getOriname() %></td>
				<td><%=bean.getName() %></td><td><%=bean.getCount()%></td>
			</tr>
				<%} }%>
			</table>
		</td>
	</tr>
</table>
</div>
<br>
<br>
<script type="text/javascript">
var LODOP;
//CheckLodop();
function initPrint(){
	cssStyle = "<style>table{font-size:11px;font-family:Microsoft YaHei;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	if(LODOP.PRINT_INIT("")){ //
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.ADD_PRINT_TABLE("0","-1mm","200mm","140mm",cssStyle+document.getElementById("warehousingAbnormal").innerHTML);
		//LODOP.PREVIEWB();
		LODOP.PRINTB();
		LODOP.NEWPAGE();
		LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
		LODOP.SET_PRINTER_INDEX(-1);
		LODOP.ADD_PRINT_TABLE("0","-1mm","200mm","140mm",cssStyle+document.getElementById("warehousingAbnormal").innerHTML);
		//LODOP.PREVIEWB();
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
window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=selectAbnormalList';
</script>
</body>
</html>