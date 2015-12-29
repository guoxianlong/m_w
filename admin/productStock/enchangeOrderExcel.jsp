<%@page import="adultadmin.util.StringUtil"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage=""%>
<%@ page import="adultadmin.action.supplier.*"%>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="mmb.stock.stat.StatStockAction"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.action.vo.voProduct"%>
<%@ page import="adultadmin.action.vo.voProductLine"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<%
    voUser user = (voUser)session.getAttribute("userView");
    StatStockAction action = new StatStockAction();
 	action.enchangeExcel(request, response);
 	HashMap productmap = (HashMap)request.getAttribute("pList");
 	HashMap countmap = (HashMap)request.getAttribute("cList");
 	String tip=(String)request.getAttribute("tip");
 	String flag=(String)request.getParameter("flag");
 	if(tip!=null && tip.length()>0){
 		%><script>alert('<%= tip %>');</script>
 <%
 		return;
 	}
 %>
<%
String fileName=null;
response.setContentType("application/vnd.ms-excel;charset=UTF-8");
if(flag.equals("fc")){
	 fileName = "需从增城调至芳村的商品汇总表"+DateUtil.getNow().substring(0, 4)+DateUtil.getNow().substring(5, 7)+DateUtil.getNow().substring(8, 10)+DateUtil.getNow().substring(11, 13)+DateUtil.getNow().substring(14, 16)+DateUtil.getNow().substring(17, 19);
}
if(flag.equals("zc")){
	 fileName = "需从芳村调至增城的商品汇总表"+DateUtil.getNow().substring(0, 4)+DateUtil.getNow().substring(5, 7)+DateUtil.getNow().substring(8, 10)+DateUtil.getNow().substring(11, 13)+DateUtil.getNow().substring(14, 16)+DateUtil.getNow().substring(17, 19);
}
	response.setHeader("Content-disposition","attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>

<title>商品调拨量</title>
<style type="text/css">
<!--
.STYLE1 {color: #FF0000}
-->
</style>
</head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">

<body>
<form action="./enchangeOrderExcel.jsp" method="post">
  <table width="99%" border="0" cellpadding="3" cellspacing="1" bordercolor="#000000" bgcolor="#e8e8e8">
    <tr bgcolor="#4688D6">
      
      <td><div align="center"><font color="#FFFFFF">产品编号</font></div></td>
	  <td><div align="center"><font color="#FFFFFF">原名称</font></div></td>
      <td><div align="center"><font color="#FFFFFF">产品名称</font></div></td>
      <td><div align="center"><font color="#FFFFFF">产品一级分类</font></div></td>  
      <td><div align="center"><font color="#FFFFFF"><%if(flag.equals("zc")) {%>需芳村调拨量<%} %><%if(flag.equals("fc")) {%>需增城调拨量<%} %></font></div></td>
    </tr>
     <%if(productmap!=null && countmap!=null) {
      	
      		Iterator iter = productmap.entrySet().iterator(); 
      		Iterator iter1 = countmap.entrySet().iterator(); 
      		while (iter.hasNext()) { 
      			java.util.Map.Entry  entry = (java.util.Map.Entry) iter.next(); 
      			java.util.Map.Entry  entry1 = (java.util.Map.Entry) iter1.next(); 
      			Object key =entry.getKey(); 
      			Object key1 =entry.getKey(); 
      			voProduct p= (voProduct)entry.getValue(); 
      			int count= Integer.parseInt(entry1.getValue().toString()); 
      			
    %>
   <tr bgcolor='#F8F8F8'>
    
      <td bordercolor="#000000"><div align="center"><%=p.getCode()%></div></td>
	  <td bordercolor="#000000"><div align="center"><%=p.getName()%></div></td>
      <td bordercolor="#000000"><div align="center"><%=p.getOriname()%></div></td>
	  <td bordercolor="#000000"><div align="center"><%=p.getParent1().getName()%></div></td>
	  <td bordercolor="#000000"><div align="center"><span class="STYLE1"><%=count%></span></div></td>
    </tr>
    <%}}%>
  </table>
</form>
</body>
</html>