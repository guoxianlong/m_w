<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	List list = (ArrayList) request.getAttribute("list");
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	String productCode = (String)request.getAttribute("productCode");
%>
<html>
  <head>
    
    <title>商品物流属性操作日志</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
		

  </head>
  <body>
  <div align="center">
  <h4>人员操作记录--产品编号<%= productCode %>商品物流属性</h4>
  </div>
  	<table align="center" width="80%" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px" >
		<tr bgcolor="#4DFFFF" >
			<td align="left">
			序号
			</td>
			<td align="left">
			时间
			</td>
			<td align="left">
			操作人员
			</td>
			<td align="left">
			操作内容
			</td>
		</tr>
		<%
			if( list != null && list.size() != 0) {
			int x = list.size();
			for( int i = 0; i < x; i++ ) {
			ProductWarePropertyLogBean pwplBean = (ProductWarePropertyLogBean)list.get(i);
		%>
			<tr bgcolor="#FFFFFF" >
				<td align="left">
				<%= i + 1%>
				</td>
				<td align="left">
				<%= pwplBean.getTime().subSequence(0,19)%>
				</td>
				<td align="left">
				<%= pwplBean.getOperName()%>
				</td>
				<td align="left">
				<%= pwplBean.getOperDetail()%>
				</td>
			</tr>
		<%
			}
		 	} else {
		%>
			<tr bgcolor="#FFFFFF" >
				<td align="center" colspan="4">
				还没有日志内容
				</td>
			</tr>
		<%
			}
		%>
	</table>
	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 40)%></p>
	<%} %>
</body>
</html>
