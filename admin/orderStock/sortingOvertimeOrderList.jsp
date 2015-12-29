<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%

List orderList =(List) request.getAttribute("orderList");
String tip =(String) request.getAttribute("tip");
String code =(String) request.getAttribute("code");
%>
<html>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<head>
<title>分拣超时订单</title>
</head>
<script type="text/javascript">

</script>
<body>
&nbsp;&nbsp;<font><strong>分拣超时订单</strong></font>
<table  width="99%" border="0" cellpadding="3" cellspacing="1"  bgcolor="black" align="center">
  <tr bgcolor="#00ccff">
		<td><div align="center">员工</div></td>
		<td><div align="center">波次号</div></td>
		<td><div align="center">波次订单数</div></td>
		<td><div align="center">领单时间</div></td>
		<td><div align="center">订单号</div></td>
	</tr>
	<%if(orderList!=null){
		for(int i=0;i<orderList.size();i++){
			SortingBatchGroupBean bean =(SortingBatchGroupBean)orderList.get(i);
		
		%>
	<%if(code.equals(bean.getStaffCode())&&StringUtil.cutString(bean.getReceiveDatetime(),0,10).equals(DateUtil.getNowDateStr())){ %><tr bgcolor="yellow"><%}else{ %>
 <tr bgcolor="#EEE9D9"><%} %>
		<td ><div align="center"><strong><%=bean.getStaffName() %></strong>(<%=bean.getStaffCode() %>)</div></td>
		<td><div align="center"><%=bean.getCode() %></div></td>
		<td><div align="center"><%=bean.getGroupCount() %></div></td>
		<td><div align="center"><%=StringUtil.cutString(bean.getReceiveDatetime(),0,16) %></div></td>
		<td><div align="center"><%=bean.getOrderCode() %></div></td>
	</tr>
	<%}} %>
</table>
</body>
</html>