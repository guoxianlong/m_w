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
<%
response.setContentType("application/vnd.ms-excel;charset=UTF-8");
String fileName = "邮包装袋明细单";
response.setHeader("Content-disposition","attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%>
<%
	for (int a = 0; a < pageNum; a++){
%>
<div id="tableDiv<%=a%>" align="center">
 <table  border="1" width='700' height="200" bordercolor="#000000" cellpadding="0" cellspacing="0">
  <tr>
    <td colspan="9" height="85"><h2 align="center">邮包装袋明细单</h2></td>
  </tr>
  <tr>
    <td><div align="center">序号</div></td>
    <td><div align="center">日期</div></td>
    <td><div align="center">订单编号</div></td>
     <td><div align="center">包裹单号</div></td>
    <td><div align="center">收件人地址</div></td>
    <td><div align="center">订单邮编</div></td>
    <td><div align="center">重量(kg)</div></td>
    <td><div align="center">订单分类</div></td>
    <td><div align="center">订单金额</div></td>
    <td><div align="center">归属物流</div></td>
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
    <td><div align="center"><%=mbBean.getAddress() %></div></td>
    <td><div align="center"><%=mbBean.getOrderPostCode() %></div></td>
    <td><div align="center"><%=mbBean.getWeight()/1000%></div></td>
    <td><div align="center"><%=mbBean.getOrderType()%></div></td>
    <td><div align="center"><%=dcmFmt.format(mbBean.getTotalPrice())%></div></td>
    <td><div align="center"><%=mbBean.getDeliverName()%></div></td>
    <td><div align="center"><%if(mbBean.getBuyMode()==0){%>货到付款<%} else{%>已到款<%} %></div></td>
    <td><div align="center"><%=mbBean.getMailingBatchBean().getStore() %></div></td>
  </tr>
  <%}} %>
  <tr bordercolor="#FFFFFF">
    <td colspan="3">合计:<%=mbList.size()%>单 </td>
    <td colspan="4">装箱人签字：</td>
  </tr>
</table>
</div>
<%} %>
</body>

</html>
