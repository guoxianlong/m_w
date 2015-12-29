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
	BuyStockinAction buyStockinAction = new BuyStockinAction();
	buyStockinAction.printBuyStockinPrice(request,response);
	
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
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean export = group.isFlag(128);
		
		
	BuyStockinBean stockin = (BuyStockinBean)request.getAttribute("bean");
	BuyOrderBean buyOrder = (BuyOrderBean)request.getAttribute("buyOrder");
	HashMap proxyMap = (HashMap)request.getAttribute("proxyMap");
	BuyStockinProductBean bsip = null;
	
	response.setContentType("application/vnd.ms-excel");
	String now = DateUtil.getNow().substring(0,10);
	String fileName = now+" "+stockin.getCode();
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
<table width="100%" cellpadding="3" cellspacing="1" border="1">
    <tr>
        <%if(export){ %>
    	<td width="100" align="center" colspan="8"><strong>采购入库单</strong></td>
    	<%}else{ %>
    	<td width="100" align="center" colspan="7"><strong>采购入库单</strong></td>
    	<%} %>
	</tr>
	<tr>
		<td width="100" align="left" colspan="3"><strong>入库单编号：</strong><%=stockin.getCode() %></td>
		<td width="100" align="left" colspan="2"><strong>状态：</strong><%=stockin.getStatusName() %></td>
		<%if(export){ %>
		<td width="100" align="left" colspan="2"><strong>生成人/审核人：</strong><%if(stockin.getCreatUser()!=null){%><%=stockin.getCreatUser().getUsername() %><%}%>/<%if(stockin.getAuditingUser()!=null){%><%=stockin.getAuditingUser().getUsername()%><%}%></td>
		<%}else{ %>
		<td width="100" align="left" colspan="2"><strong>生成人/审核人：</strong><%if(stockin.getCreatUser()!=null){%><%=stockin.getCreatUser().getUsername() %><%}%>/<%if(stockin.getAuditingUser()!=null){%><%=stockin.getAuditingUser().getUsername()%><%}%></td>
		<%} %>
		<td width="100" align="left"><strong>来源采购订单：</strong><%=buyOrder.getCode() %></td>
	</tr>
	<tr>
    	<td width="100" align="left" colspan="3"><strong>代理商：</strong><%=stockin.getProxyName() %></td>
    	<td width="100" align="left" colspan="2"><strong>地区：</strong><%=stockin.getStockArea()==0?"北京":"广东"%></td>
    	<%if(export){ %>
    	<td width="100" align="left" colspan="2"><strong>付款人：</strong><%=buyOrder.getPayUser()%></td>
    	<td width="100" align="left" colspan="1"><strong>税点：</strong><%=buyOrder.getTaxPoint()%></td>
    	<%} %>
	</tr>
	<tr>
		<td width="100" align="center"><strong>序号</strong></td>
		<td width="100" align="center"><strong>产品线</strong></td>
		<td width="100" align="center"><strong>产品编号</strong></td>
		<td width="100" align="center"><strong>产品名称</strong></td>
		<td width="100" align="center"><strong>原名称</strong></td>
		<td width="100" align="center"><strong>入库量</strong></td>
		<%if(export){ %>
		<td width="100" align="center"><strong>入库税前价(税后价)</strong></td>
		<td width="100" align="center"><strong>入库税前金额(税后金额)</strong></td>
		<%}else{ %>
		<td width="100" align="center"><strong>入库前库存</strong></td>
		<%} %>
	</tr>
	<%float totalPrice = 0; %>
	<logic:present name="bsipList" scope="request">
	<logic:iterate name="bsipList" id="item" indexId="index">
	<%bsip = (BuyStockinProductBean)item; %>
	<tr>
		<td width="100" align="center"><%=index+1%></td>
		<td width="100" align="center"><%=bsip.getProductLineName()%></td>
		<td width="100" align="center"><%=bsip.getProduct().getCode() %></td>
		<td width="100" align="center"><%=bsip.getProduct().getName() %></td>
		<td width="100" align="center"><%=bsip.getProduct().getOriname() %></td>
		<td width="100" align="center"><bean:write name="item" property="stockInCount"/></td>
		<%if(export){ %>
		<td width="100" align="center"><%=df.format(bsip.getPrice3())%>(<%=df.format(Arith.mul(bsip.getPrice3(),Arith.add(1,stockin.getTaxPoint())))%>)</td>
		<td width="100" align="center"><%=df.format(Arith.mul(bsip.getStockInCount(),bsip.getPrice3())) %>(<%=df.format(Arith.mul(Arith.mul(bsip.getPrice3(),Arith.add(1,stockin.getTaxPoint())),bsip.getStockInCount()))%>)</td>
  		<%totalPrice = totalPrice +  bsip.getStockInCount()*bsip.getPrice3();%>
		<%}else{ %>
  		<td width="100" align="center">
  			<%if(stockin.getStockArea()==ProductStockBean.AREA_BJ){%>
  			<%=bsip.getProduct().getStock(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)+bsip.getProduct().getLockCount(ProductStockBean.AREA_BJ,ProductStockBean.STOCKTYPE_QUALIFIED)%>
  			<%}else if(stockin.getStockArea()==ProductStockBean.AREA_GF){%>
  			<%=bsip.getProduct().getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED)+bsip.getProduct().getLockCount(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED)+
  	   bsip.getProduct().getStock(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED)+bsip.getProduct().getLockCount(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED)%>
  			<%}%>
  		</td>
  		<%} %>
  	</tr>
	</logic:iterate>
	</logic:present>
	<%if(export){ %>
	<tr>
		<td></td>
		<td width="100" align="center"><strong>合计</strong></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td width="100" align="center"><strong><%=df.format(totalPrice)%>(<%=df.format(Arith.mul(totalPrice,Arith.add(1,stockin.getTaxPoint())))%>)</strong></td>
	<%} %>
</table>
<table width="100%" cellpadding="3" cellspacing="1" border="0">
		<tr>
		<td colspan="2"></td>
		<td width="100" align="left" colspan="3">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strong>主管签字：</strong></td>
		<td width="100" align="left" colspan="3"><strong>财务签字：</strong></td>
	</tr>
</table>