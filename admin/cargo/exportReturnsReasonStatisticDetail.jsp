<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="java.util.List"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>
<%@ page import="mmb.stock.stat.ReturnedPackageBean"%>
<%@page import="adultadmin.util.DateUtil"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">
    <title>
     销售退货原因统计
    </title>
    <%
    List rpList = (List)request.getAttribute("rpList");
    response.setContentType("application/vnd.ms-excel;");
    response.setHeader("Content-disposition","attachment; filename=\"" +DateUtil.getNowDateStr().replace("-","") + new String("销售退货原因统计".getBytes("gb2312"),"ISO8859-1")+  ".xls\"");
    %>
  </head>
  <body>
  <table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
	<tr bgcolor="#00ccff">
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">序号</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">订单编号</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">包裹单号</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">快递公司</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">入库时间</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">入库状态</font></span></div></td>
    	<td ><div align="center"><span class="STYLE2"><font color="#00000">退货原因</font></span></div></td>
  	</tr >
  	<% if(rpList!=null){ 
  			for(int i=0;i<rpList.size();i++){
  				ReturnedPackageBean rpBean = (ReturnedPackageBean)rpList.get(i);
  	%>
  	<tr>
  		<td><%= i+1 %></td>
  		<td align="center"><font color="#000000"><%= rpBean.getOrderCode() %></font></td>
  		<td align="center"><font color="#000000"><% if(!"".equals(rpBean.getPackageCode())){ %><%= rpBean.getPackageCode() %><% }else{ %>未知<% } %></font></td>
  		<td align="center"><font color="#000000"><% if(voOrder.deliverMapAll.get(rpBean.getDeliver() + "")!=null){ %><%=voOrder.deliverGdMap.get(rpBean.getDeliver() + "")%><% }else{ %>未知<% } %></font></td>
  		<td align="center"><font color="#000000"><%= rpBean.getStorageTime().substring(0,19) %></font></td>
  		<td align="center"><%if(rpBean.getStorageStatus()==0){  %><font color="green">正常入库</font><% }if(rpBean.getStorageStatus()==1){ %><font color="red">商品缺失</font><% }if(rpBean.getStorageStatus()==2){ %><font color="#CC3300">订单和包裹不匹配</font><% } %></td>
  		<td align="center"><font color="#000000"><% if(rpBean.getReasonName()!=null){ %><%= rpBean.getReasonName() %><% }else{ %>未知<% } %></font></td>
  	</tr>
  			<% }
  		}		
  	 %>
	</table>
</body>
</html>
