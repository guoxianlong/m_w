<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, java.net.*" %><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.bean.*,adultadmin.util.DateUtil" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
//response.setContentType("application/vnd.ms-excel");

ProductStockAction action = new ProductStockAction();
action.stockExchange(request, response);

Map productMap = (Map) request.getAttribute("productMap");
List sepList ;
sepList= (ArrayList) request.getAttribute("sepList");
StockExchangeBean bean = (StockExchangeBean) request.getAttribute("bean");

int i, count,totalcount=0,totalRow=0;
count = sepList.size();
voProduct product = null;
StockExchangeProductBean sep = null;

String now = DateUtil.getNow().substring(0, 10);
String fileName = now;
String stockinCode = request.getParameter("stockinCode");
//response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
<!DOCTYPE link PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>打印调拨单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">

<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0> 
	<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0 pluginspage="install_lodop.exe"></embed>
</object> 
<script type="text/javascript">
//CheckLodop();

function initPrint(num){
	cssStyle = "<style>table{font-size:14px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	LODOP.PRINT_INIT("调拨单打印");
	LODOP.SET_PRINT_PAGESIZE(0,"210mm","148mm","");
	var barImage = null;
	var barcodeTD = null;
	var barcodeHId = null;
	var top=158;
	var barcodeWidth=57.4;
	var barcodeLeft=120;
	var indexT=num;
	var code="<%=bean.getCode()%>";
	for(var i=0;i<indexT;i+=1){
		barImage= document.getElementById("barcodeImage"+i);
		barcodeTD= document.getElementById("barcodeID"+i);
		barcodeTD.removeChild(barImage);
		LODOP.ADD_PRINT_TABLE("2cm","0.5cm","18.85cm","11.27cm",cssStyle+document.getElementById("stockChangerDiv"+i).innerHTML);
		LODOP.ADD_PRINT_BARCODE("21.7mm",barcodeLeft+"mm",barcodeWidth+"mm","13.2mm","128A",code);
		barcodeTD.appendChild(barImage);
		LODOP.NEWPAGE();
	}
	//LODOP.PREVIEWB();
	//LODOP.PRINT_DESIGN();
	LODOP.PRINTB();
}
</script>
</head>
<body>
<%int b=1,c=1;
if(count>10&&count%10!=0) b=(count/10)+1;
if(count%10==0) b=(count/10);
for(int a=0;a<b;a++) {
	//out.print(c+"  "+b+"  "+a+"  "+count);
%>
<%=a>=1?"<br/>":"" %>
<div align="center" id="stockChangerDiv<%=a%>">
	<table cellpadding="0" cellspacing="0" width="690" height="400" border="1" >
			<tr><td colspan="3"  style="border-right: 0;height:60px; text-align:center; vertical-align: middle;"><h2>调 拨 单&nbsp;<%if(bean.getPriorStatus()==1) {%>(紧急)<%} %></h2></td>
				<td height="60" align="right" colspan="3" id="barcodeID<%=a %>" style="border-left: 0;">
				<img id="barcodeImage<%=a%>" src="<%=request.getContextPath() %>/barcodeServlet?msg=<%=bean.getCode() %>&fmt=jpg&type=code39" width="150" height="50"/></td></tr>
				<tr><td colspan="3" align="left">页码：<%=a+1%> &nbsp;源库：<%= ProductStockBean.getAreaName(bean.getStockOutArea()) %>-<%=ProductStockBean.getStockTypeName(bean.getStockOutType())%></td>
				<td colspan="3" align="left">目的库：<%=ProductStockBean.getAreaName(bean.getStockInArea()) %>-<%=ProductStockBean.getStockTypeName(bean.getStockInType())%></td></tr>
				<tr align="left"><td>序</td><td>产品编号</td><td>产品原名称</td><td>源货位</td><td align="center">数量</td><td>目的货位</td></tr>

<%
if(count>10){
	int step = (10*c>count?10*c-(10*c-count):10*c);
	if(c>1){
		i=(c-1)*10;
	}else
		i=0;
	while(i<step){
	totalRow++;
	sep = (StockExchangeProductBean) sepList.get(i);
	product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));%>
	<tr align="left">	
	  <td><%=totalRow%></td> 
	  <td><b><%=product.getCode()%></b></td>
	  <td style="font-size: 10px;" align="left"><%=StringUtil.getString(product.getOriname(),60)%></td>
	  <td>
	    <%if(sep.getSepcOut() != null){ %>
	  	<%=sep.getSepcOut().getCargoProductStock()==null?"":sep.getSepcOut().getCargoProductStock().getCargoInfo().getWholeCode()%>
	  	<%} %>
	  </td>
	  <td align="center"><b><%=sep.getStockOutCount() %></b></td><%totalcount=totalcount+sep.getStockOutCount(); %>
	  <td>
	  	<%if(sep.getSepcIn()!=null){ %>
	  	<%=sep.getSepcIn().getCargoProductStock()==null?"":sep.getSepcIn().getCargoProductStock().getCargoInfo().getWholeCode()%>
	  	<%} %>
	  </td></tr>
	<%i ++;} %>
		<%for(int t=0;t<=9-(count-10*(c-1));t++){ //行数不够的自动补足%>
<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
		<%}}else{
			for(i =0; i<count; i ++){
			totalRow++;
			sep = (StockExchangeProductBean) sepList.get(i);
			product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));%>
	  <tr align="left"><td><%=totalRow%></td>
	  <td><b><%=product.getCode()%></b></td>
	  <td style="font-size: 10px;" align="left"><%=StringUtil.getString(product.getOriname(),90)%></td>
	  <td><%if(sep.getSepcOut() != null){ %><%=sep.getSepcIn().getCargoProductStock()==null?"":sep.getSepcOut().getCargoProductStock().getCargoInfo().getWholeCode()%><%} %></td>
	  <td align="center"><b><%=sep.getStockOutCount() %></b></td><%totalcount=totalcount+sep.getStockOutCount(); %>
	  <td><%if(sep.getSepcIn()!=null){ %><%=sep.getSepcIn().getCargoProductStock()==null?"":sep.getSepcIn().getCargoProductStock().getCargoInfo().getWholeCode()%><%} %></td></tr>
		<%} %>
		<%for(int t=0;t<=9-count;t++){ //行数不够的自动补足%>
		<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	    <%}}%>
	<tr>
	<td>&nbsp;</td>
	<td align="center">合计</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td align="center"><b><%=a==b-1?totalcount+"":"" %></b></td>
	<td>&nbsp;</td>
	</tr>
<tr>
	<td colspan="3" align="left">出库方签字及日期：</td>
	<td colspan="3" align="left">入库方签字及日期：</td>
</tr>
</table>
</div>
<%--
<div align="center" id="stockChangerDiv<%=a+1%>">
	<table cellpadding="0" cellspacing="0" width="560" height="400" border="1" >
				<tr><td colspan="3"  style="border-right: 0;height:60px; text-align:center; vertical-align: middle;"><h2>调 拨 单</h2></td>
				<td height="60" align="right" colspan="3" id="barcodeID<%=a+1 %>" style="border-left: 0;">
				<img id="barcodeImage<%=a+1%>" src="<%=request.getContextPath() %>/barcodeServlet?msg=<%=bean.getCode() %>&fmt=jpg&type=code39" width="150" height="50"/></td></tr>
				<tr><td colspan="3" align="left">页码：<%=a+1%> &nbsp;源库：<%= ProductStockBean.getAreaName(bean.getStockOutArea()) %>-<%=ProductStockBean.getStockTypeName(bean.getStockOutType())%></td>
				<td colspan="3" align="left">目的库：<%=ProductStockBean.getAreaName(bean.getStockInArea()) %>-<%=ProductStockBean.getStockTypeName(bean.getStockInType())%></td></tr>
				<tr><td>序</td><td>编号</td><td>原名称</td><td>源货位</td><td align="center">数量</td><td>目的货位</td></tr>

<%
if(count>10){
	int step = (10*c>count?10*c-(10*c-count):10*c);
	if(c>1){
		i=(c-1)*10;
	}else
		i=0;
	while(i<step){
	totalRow++;
	sep = (StockExchangeProductBean) sepList.get(i);
	product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));%>
	<tr>	
	  <td><%=totalRow%></td> 
	  <td><%=product.getCode()%></td>
	  <td style="font-size: 10px;"><%=StringUtil.getString(product.getOriname(),60)%></td>
	  <td><b><%=sep.getSepcOut().getCargoProductStock().getCargoInfo().getWholeCode()%></b></td>
	  <td align="center"><b><%=sep.getStockOutCount() %></b></td><%totalcount=totalcount+sep.getStockOutCount(); %>
	  <td><b><%=sep.getSepcIn().getCargoProductStock().getCargoInfo().getWholeCode()%></b></td></tr>
	<%i ++;} %>
		<%for(int t=0;t<=9-(count-10*(c-1));t++){ //行数不够的自动补足%>
<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
		<%}}else{
			for(i =0; i<count; i ++){
			totalRow++;
			sep = (StockExchangeProductBean) sepList.get(i);
			product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));%>
	  <tr><td><%=totalRow%></td>
	  <td><%=product.getCode()%></td>
	  <td style="font-size: 10px;"><%=StringUtil.getString(product.getOriname(),60)%></td>
	  <td><b><%=sep.getSepcOut().getCargoProductStock().getCargoInfo().getWholeCode()%></b></td>
	  <td align="center"><b><%=sep.getStockOutCount() %></b></td><%totalcount=totalcount+sep.getStockOutCount(); %>
	  <td><b><%=sep.getSepcIn().getCargoProductStock().getCargoInfo().getWholeCode()%></b></td></tr>
		<%} %>
		<%for(int t=0;t<=9-count;t++){ //行数不够的自动补足%>
		<tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
	    <%}}%>
	<tr>
	<td>&nbsp;</td>
	<td align="center">合计</td>
	<td>&nbsp;</td>
	<td>&nbsp;</td>
	<td align="center"><b><%=a==b-1?totalcount+"":"" %></b></td>
	<td>&nbsp;</td>
	</tr>
<tr>
	<td colspan="3" align="left">出库方签字及日期：</td>
	<td colspan="3" align="left">入库方签字及日期：</td>
</tr>
</table>
</div> --%>

<%c++;}//序号%>
<br/>
<div align="center">
<script type="text/javascript">
	initPrint(<%=b%>);
	window.close();
</script>
</div>
</body>
</html>