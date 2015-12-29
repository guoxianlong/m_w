<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.order.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.util.Encoder" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<%@ page import="adultadmin.action.stock.*" %>
<%@ page import="adultadmin.bean.barcode.*" %>
<%@ page import="adultadmin.util.*" %>
<%

StockAction action = new StockAction();
action.previewOrderStock(request, response);
String reason = (String)request.getAttribute("reason");
String scanType=StringUtil.convertNull(request.getParameter("scanType"));
voOrder order=(voOrder)request.getAttribute("order");
int wareArea = ((Integer)request.getAttribute("wareArea")).intValue();
AuditPackageBean apBean=(AuditPackageBean)request.getAttribute("apBean");
OrderCustomerBean ocBean=(OrderCustomerBean)request.getAttribute("ocBean");
List productList=(List)request.getAttribute("productList");
List ospcList=(List)request.getAttribute("ospcList");
int index=0;//商品序号
int totalCount=0;//商品总数量
int totalPrice=0;//商品总金额
int xiaoji=0;//小计
if(productList!=null){
	for(int i=0;i<productList.size();i++){
		voProduct op=(voProduct)productList.get(i);
    	OrderStockProductCargoBean ospcBean=(OrderStockProductCargoBean)ospcList.get(i);
    	totalCount=totalCount+ospcBean.getCount();
    	totalPrice+=ospcBean.getCount() * op.getPrice();
	}
}
%>
<html>
<head>
<title>预览发货清单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="../../js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function cancelSubmit(){
	document.getElementById("orderId").value="";
	document.forms[0].submit();
}
function getFocus(){
	document.getElementById("submitButton").focus();
}
</script>
</head>
<body onload="getFocus();">
快速退货——预览发货清单，确认退货<br/><br/>
<%
if(request.getAttribute("result")==null){ 
int pageCount=productList.size()%5==0?productList.size()/5:productList.size()/5+1;
%>
订单编号：<%=order.getCode() %>&nbsp;&nbsp;
包裹单号：<%=order.getPackageNum() %>
<%for(int pageNum=0;pageNum<pageCount;pageNum++){ %>
<table cellpadding="0" cellspacing="0" width="670" border="1" style="border: 1px solid;border-collapse:collapse;">
 	<tr>
    	<td align='left' style="font-size:12px;">序号：<strong><%=order.getSerialNumber()%></strong></td>
        <td align="left" colspan="2">订单时间：<%= order.getCreateDatetime().toString().substring(0,16)%></td>
        <td align='left' >客户姓名：<strong style="font-size: 13px;"><%=StringUtil.getString(ocBean.getName(),8)%></strong></td>
        <td align='left' colspan="2">快递公司：<%= order.getDeliverName()%></td>
    </tr>
    <tr>
   		<td align='left'>商品序号</td>
    	<td align='center'>&nbsp;&nbsp;</td>
    	<td align='left'>货号</td>
        <td align='center'>数量</td>
        <td align='center'>单价</td>
        <td align='center'>金额</td>         
    </tr>
    <%for(int i=pageNum*6;i<pageNum*6+6;i++){ %>
    	<%if(i<productList.size()){ %>
    	<%
    	voProduct op=(voProduct)productList.get(i);
    	OrderStockProductCargoBean ospcBean=(OrderStockProductCargoBean)ospcList.get(i);
    	%>
	<tr>
		<td><%= index+1 %></td>
		<td><strong><%=ospcBean.getCargoWholeCode() %></strong></td>
		<td><%= op.getCode()%></td>
		<td align="center"><strong style="font-size: 13px;"><%= ospcBean.getCount()%></strong></td>
		<td style="font-size: 10px; text-align: center;"><%=NumberUtil.price(op.getPrice()) %></td>
		<td style="font-size: 10px; text-align: center;"><%= NumberUtil.price(ospcBean.getCount() * op.getPrice()) %></td>
		<%xiaoji+=ospcBean.getCount(); %>
	</tr>
	<tr>
		<td></td>
		<td align="left"  colspan="5" ><%=op.getOriname()%></td>
	</tr>
	<%index++; %>
	<%}else{ %>
	<tr>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="center">&nbsp;</td>
		<td style="font-size: 10px; text-align: right;">&nbsp;</td>
		<td style="font-size: 10px; text-align: right;">&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td align="left"  colspan="5" >&nbsp;</td>
	</tr>
	<%}} %>
	<tr>
		<td colspan="2" align="left"><%if(xiaoji!=0&&index<productList.size()){ %>小计：<%=xiaoji %>&nbsp;&nbsp;<%} %>商品总数：  <%=totalCount %></td>
		<td>运费：<%=(int)order.getPostage() %>元</td>
		<td colspan="2">付款方式：<%switch(order.getBuyMode()) {case 0:%>货到付款<%break;case 1:%>邮购<%break;case 2:%>上门自取<%break;}%></td>
		<td>总金额：<%=NumberUtil.price(totalPrice) %>元</td>
	</tr>
</table><br/>
<%} %>
<form action="quickCancelStock.jsp" method="post">
<input type="hidden" id="orderId" name="orderId" value="<%=order.getId() %>" />
<input type="hidden" name="reason" value="<%=reason %>"/>
<input type="hidden" name="scanType" value="<%=scanType %>" />
<input type="hidden" name="wareArea" value="<%= wareArea%>" />
<input type="submit" id="submitButton" onblur="this.focus();" value="确定" />
<input type="button" value="取消" onclick="cancelSubmit();"/>
</form>
注：如果确认退货，请单击‘确定’或再次扫描订单编号或包裹单号。反之，请单击‘取消’返回至上一页，继续扫描其他订单。
<%}else{ %>
<form name="toQuickCancelStock" action="quickCancelStock.jsp" method="post">
<input type="hidden" name="tip" value="<%=request.getAttribute("tip") %>"/>
<input type="hidden" name="scanType" value="<%=scanType %>" />
</form>
<script type="text/javascript">document.toQuickCancelStock.submit();</script>
<%} %>
</body>
</html>