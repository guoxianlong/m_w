<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, adultadmin.bean.*, adultadmin.bean.buy.*" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	Stock2Action stock2Action = new Stock2Action();
	stock2Action.printBuyStock(request,response);
	
	String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
history.back(-1);
</script>
<%
	return;
}

	BuyStockBean stock = (BuyStockBean)request.getAttribute("bean");
	HashMap proxyMap = (HashMap)request.getAttribute("proxyMap");
	BuyStockProductBean bsp = null;
	Double taxPoint = (Double)request.getAttribute("taxPoint");
	double stockPrice = 0;
	
	response.setContentType("application/vnd.ms-excel");
	String now = DateUtil.getNow().substring(0,10);
	String fileName = now+" "+stock.getCode();
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
<table width="100%" cellpadding="3" cellspacing="1" border="1">
    <tr>
    	<td width="100" align="center" colspan="7"><strong>预计到货表</strong></td>
	</tr>
	<tr>
		<td width="100" align="left" colspan="3"><strong>编号：</strong><%=stock.getCode() %></td>
		<td width="100" align="left" colspan="2"><strong>状态：</strong><%=stock.getStatusName() %></td>
		<td width="100" align="left" colspan="2"><strong>生成人/确认人：</strong><%if(stock.getCreatUser()!=null){%><%=stock.getCreatUser().getUsername() %><%}%>/<%if(stock.getAuditingUser()!=null){%><%=stock.getAuditingUser().getUsername()%><%}%></td>
	</tr>
	<tr>
    	<td width="100" align="left" colspan="4"><strong>代理商：</strong><%=stock.getProxyName() %></td>
    	<td width="100" align="left" colspan="3"><strong>地区：</strong><%=stock.getArea()==0?"北京":"广东"%></td>
	</tr>
	<tr>
		<td width="100" align="center"><strong>序号</strong></td>
		<td width="100" align="center"><strong>产品线</strong></td>
		<td width="100" align="center"><strong>产品编号</strong></td>
		<td width="100" align="center"><strong>产品名称</strong></td>
		<td width="100" align="center"><strong>原名称</strong></td>
		<td width="100" align="center"><strong>预计进货量（已入库量）</strong></td>
		<!--  <td width="100" align="center"><strong>预计到货税前价（税后价）</strong></td>
		<td width="120" align="center"><strong>预计到货税前金额（税后金额）</strong></td>-->
		<td width="100" align="center"><strong>进货前库存</strong></td>
	</tr>
	<logic:present name="buyStockProductList" scope="request">
	<logic:iterate name="buyStockProductList" id="item" indexId="index">
	<%bsp = (BuyStockProductBean)item; %>
	<tr>
		<td width="100" align="center"><%=index+1%></td>
		<td width="100" align="center"><%=bsp.getProductLineName()%></td>
		<td width="100" align="center"><%=bsp.getProduct().getCode() %></td>
		<td width="100" align="center"><%=bsp.getProduct().getName() %></td>
		<td width="100" align="center"><%=bsp.getProduct().getOriname() %></td>
		<td width="100" align="center"><bean:write name="item" property="buyCount"/>(<bean:write name="item" property="stockinCount"/>)</td>
		<!--  <td width="100" align="center"><bean:write name="item" property="purchasePrice"/>(<%=df.format(Arith.mul(bsp.getPurchasePrice(),Arith.add(1,taxPoint.doubleValue()))) %>)</td>
		<td width="100" align="center"><%=bsp.getPurchasePrice()*bsp.getBuyCount() %>(<%=df.format(Arith.mul(bsp.getPurchasePrice(),Arith.mul(bsp.getBuyCount(),Arith.add(1,taxPoint.doubleValue())))) %>)<%stockPrice = stockPrice+bsp.getPurchasePrice()*bsp.getBuyCount();%></td>-->
  		<td width="100" align="center">
  			<%if(stock.getArea()==0){%>
  	<%=bsp.getProduct().getStock(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)+bsp.getProduct().getLockCount(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)%>
  	<%}else if(stock.getArea()==1){%>
  	<%=bsp.getProduct().getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED)+bsp.getProduct().getLockCount(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED)+
       bsp.getProduct().getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)+bsp.getProduct().getLockCount(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED)%>
  	<%}%>
  		</td>
  	</tr>
	</logic:iterate>
	</logic:present>
	<tr>
		<td width="100" align="center"><strong></strong></td>
		<td width="100" align="center"><strong>合计</strong></td>
		<td width="100" align="center"><strong></strong></td>
		<td width="100" align="center"><strong></strong></td>
		<td width="100" align="center"><strong></strong></td>
		<td width="100" align="center"><strong></strong></td>
		<!-- <td width="100" align="center"><strong></strong></td>
		<td width="120" align="center"><strong><%=stockPrice %>(<%=df.format(Arith.mul(stockPrice,Arith.add(1,taxPoint.doubleValue())))%>)</strong></td> -->
		<td width="100" align="center"><strong></strong></td>
	</tr>
	<tr><td width="100" align="left" colspan="2"><strong>物流公司：</strong><%=stock.getExpressCompany() %></td>
		<td width="100" align="left" colspan="2"><strong>物流单号：</strong><%=stock.getExpressCode() %></td>
		<td width="100" align="left" colspan="1"><strong>预计运费：</strong><%=stock.getPortage() %></td>
		<td width="100" align="left" colspan="2"><strong>预计到货时间：</strong><%=stock.getExpectArrivalDatetime().substring(0,10) %></td>
	</tr>
</table>