<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@page import="mmb.stock.stat.*,adultadmin.bean.buy.*,adultadmin.bean.stock.*,java.util.*" %>
<%
	StockExchangeBean seBean = (StockExchangeBean) request.getAttribute("stockExchangeBean");
	BuyStockinBean bsBean = (BuyStockinBean) request.getAttribute("buyStockinBean");
	session.setAttribute("buyStockinBean_" + bsBean.getCode(), bsBean);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>

<script type="text/javascript" >
		//window.open("<%=request.getContextPath()%>/admin/cargo/checkStockinMission/checkStockinPrintExchange.jsp?exchangeId=4699&outUser=haoyabin",'blank_');
	
		<% if( seBean == null ) { %>
			alert("打印调拨单出问题了");
		<%} else {%>
			window.open("<%=request.getContextPath()%>/admin/rec/oper/checkStockinMission/checkStockinPrintExchange.jsp?exchangeId=<%= seBean.getId()%>&outUser=<%= seBean.getStockOutOperName()%>&stockinCode=<%= bsBean.getCode()%>",'_blank');
		<%}%>
		
		window.location="<%=request.getContextPath() %>/admin/rec/oper/checkStockinMission/appraisalStorageResultInput.jsp?redirect=1";
</script>
</head>
<body>

</body>
</html>
