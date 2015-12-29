<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="adultadmin.bean.balance.MailingBalanceAuditingBean"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.balance.MailingBalanceBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="java.text.DecimalFormat"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>财务对账单</title>
<%
MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)request.getAttribute("mba");
List packageList=(List)request.getAttribute("packageList");
DecimalFormat dcmFmt = new DecimalFormat("0.00");
int count=packageList.size();
int tableCount=count%20==0?count/20:((count-count%20)/20+1);
%>
</head>
<body>
<%for(int j=0;j<tableCount;j++){ %>
<%float totalPrice=0; %>
<div id="printBalanceCount<%=j %>">
<table bordercolor="#000000" border="1" cellpadding="0" cellspacing="0" width="80%">
	<tr>
		<td colspan="7" style="font-size:large;font-weight: bold;text-align: center">财务对账单</td>
	</tr>
	<tr>
		<td colspan="7">
			<strong>结算批次号：</strong><%=mba.getCode() %>&nbsp;&nbsp;
			<strong>归属物流：</strong><%=MailingBalanceBean.getBalanceTypeMap().get(mba.getBalanceType()+"") %>&nbsp;&nbsp;
			<strong>代收款总额：</strong><%=dcmFmt.format(mba.getShouldPay()) %>&nbsp;&nbsp;
			<strong>付款方式：</strong>
				<% if(mba.getPayType()==0){%>
  					<span id="totalPrice0-<%=mba.getId() %>">未选择</span></font></strong></td>
  				<%}else if(mba.getPayType()==1){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">现金支付</span></font></strong></td>
  				<%}else if(mba.getPayType()==2){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">pos机刷卡</span></font></strong></td>
  				<%} %>
		</td>
	</tr>
	<tr  align="center">
		<td>序号</td>
		<td>申请结算日期</td>
		<td>发货仓</td>
		<td>订单编号</td>
		<td>代收金额</td>
		<td>结算状态</td>
		<td>备注</td>
	</tr>
<%for(int i=j*20;i<20;i++){ %>
	<%if(i<count){ %>
		<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(i); %>
		<%totalPrice+=packageBean.getTotalPrice(); %>
		<tr  align="center">
			<td><%=i+1 %></td>
			<td><%=packageBean.getStockInDatetime().substring(0,10) %></td>
			<td><%=packageBean.getMailingBatchBean()==null?"":packageBean.getMailingBatchBean().getStore() %>&nbsp;</td>
			<td><%=packageBean.getOrderCode() %></td>
			<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
			<td><%=packageBean.getBalanceStatusName(packageBean.getBalanceStatus()) %>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	<%}else{ %>
		<tr  align="center">
			<td><%=i+1 %></td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
		</tr>
	<%} %>
<%} %>
	<tr>
		<td colspan="2">本页小计：<%=dcmFmt.format(totalPrice) %></td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
	</tr>
</table>
</div>
<%} %>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<script type="text/javascript">
//CheckLodop();

	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	<%for(int i=0;i<tableCount;i++){%>
	if(LODOP.PRINT_INIT("")){ 
		        LODOP.SET_PRINT_PAGESIZE(0,"210mm","290mm","");
		        LODOP.ADD_PRINT_TABLE("1cm","1cm","18cm","20cm",cssStyle+document.getElementById("printBalanceCount<%=i%>").innerHTML);
				LODOP.NEWPAGE();
				LODOP.PREVIEWB();
				LODOP.SET_PRINTER_INDEX(-1);
				//LODOP.PRINTB();
	}
	<%}%>
	window.close();
</script>
</body>
</html>