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
List mbList = (List)request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");
MailingBatchPackageBean mbBean = null;
DecimalFormat dcmFmt = new DecimalFormat("0.00");
%>

<body>

<table  border="1">
  <tr>
    <td colspan="6"><div align="center" class="STYLE1">邮包装袋明细单</div></td>
  </tr>
  <tr>
    <td><div align="center">序号</div></td>
    <td><div align="center">日期</div></td>
    <td><div align="center">订单编号</div></td>
    <td><div align="center">收件人地址</div></td>
    <td><div align="center">代收金额</div></td>
    <td><div align="center">发货仓</div></td>
  </tr>
   <%if(mbList!=null){
		for (int i = 0; i < mbList.size(); i++) {
			mbBean = (MailingBatchPackageBean) mbList.get(i);
	    %>
  <tr>
    <td><div align="center"><%=i+1%></div></td>
    <td><div align="center"><%=mbBean.getCreateDatetime() %></div></td>
    <td><div align="center"><%=mbBean.getOrderCode() %></div></td>
    <td><div align="center"><%=mbBean.getAddress() %></div></td>
    <td><div align="center"><%=dcmFmt.format(mbBean.getTotalPrice())%></div></td>
    <td><div align="center"><%=mbBean.getMailingBatchBean().getStore() %></div></td>
  </tr>
  <%}} %>
  <tr bordercolor="#FFFFFF">
    <td colspan="3">合计:<%=mbList.size()%>单 </td>
    <td colspan="3">装箱人签字：</td>
  </tr>
</table>

</body>

</html>
