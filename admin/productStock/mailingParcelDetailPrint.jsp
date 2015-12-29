<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>

<%@page import="java.text.DecimalFormat"%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>邮包装袋明细单</title>
<style type="text/css">
<!--
.STYLE1 {
	font-size: large;
	font-weight: bold;
}
-->
</style>
</head>
<%
DecimalFormat dcmFmt = new DecimalFormat("0.00");
List mbList = (List)request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");
MailingBatchPackageBean mbBean = null;
String code = (String) request.getAttribute("code");
int count = mbList.size();
int pageNum=count%20==0?count/20:((count-count%20)/20+1);
%>
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<script type="text/javascript">
//CheckLodop();
function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:12px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	<%for (int  a = 0; a < pageNum; a++){%>
	if(LODOP.PRINT_INIT("")){ //mmb发货清单打印
		LODOP.ADD_PRINT_TABLE("0.8cm","0.6cm","18.2cm","25cm",cssStyle+document.getElementById("tableDiv<%=a%>").innerHTML);
		LODOP.ADD_PRINT_BARCODE("10mm","140mm","10mm","20mm","CODE93","<%=code%>");	
		LODOP.NEWPAGE();
		LODOP.SET_PRINTER_INDEX(-1);
	    //LODOP.PREVIEWB();
		LODOP.PRINTB();
	}
	<%}%>
}
</script>
<body>
<%
	for (int a = 0; a < pageNum; a++){
%>
<div id="tableDiv<%=a%>" align="center">
 <table  border="1" width='700' height="200" bordercolor="#000000" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="10 height="85"><h2 align="center">邮包装袋明细单</h2></td>
    <td></td>
  </tr>
  <tr>
    <td><div align="center">序号</div></td>
    <td><div align="center">日期</div></td>
    <td><div align="center">订单编号</div></td>
     <td><div align="center">包裹单号</div></td>
     <td><div align="center">订单分类</div></td>
    <td><div align="center">收件人地址</div></td>
    <td><div align="center">重量(kg)</div></td>
    <td><div align="center">订单金额</div></td>
     <td><div align="center">付款方式</div></td>
    <td><div align="center">发货仓</div></td>
  </tr>
   <%if(mbList!=null){
		for (int i = 0; i < 20; i++) {
      			if(i+a*20>=count){%>
      			<tr>
      			  <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      <td><div align="center">&nbsp;</div></td>
      		      </tr>
      			<%continue;}
			mbBean = (MailingBatchPackageBean) mbList.get(i+a*20);
	    %>
	    
	    	
      		
  <tr>
    <td><div align="center"><%=a*20+i+1 %></div></td>
    <td><div align="center"><%=mbBean.getCreateDatetime() %></div></td>
    <td><div align="center"><%=mbBean.getOrderCode() %></div></td>
     <td><div align="center"><%=mbBean.getPackageCode() %></div></td>
     <td><div align="center"><%=mbBean.getOrderType()%></div></td>
    <td><div align="center"><%=mbBean.getAddress() %></div></td>
    <td><div align="center"><%=mbBean.getWeight()/1000%></div></td>
    <td><div align="center"><%=dcmFmt.format(mbBean.getTotalPrice())%></div></td>
    <td><div align="center"><%if(mbBean.getBuyMode()==0){%>货到付款<%} else{%>已到款<%} %></div></td>
    <td><div align="center"><%=mbBean.getMailingBatchBean().getStore() %></div></td>
  </tr>
  <%}} %>
  <tr bordercolor="#FFFFFF">
    <td colspan="3">合计:<%=mbList.size()%>单 </td>
    <td colspan="7">装箱人签字：</td>
  </tr>
</table>
</div>
<%} %>
</body>
<script type="text/javascript">
   initPrint();
   window.close();
</script>
</html>
