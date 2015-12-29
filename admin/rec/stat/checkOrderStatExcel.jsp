<%@page import="adultadmin.util.StringUtil"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="mmb.rec.checkOrderStat.CheckOrderStatJobBean"%>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
</head>
<%
	List list = (List) request.getAttribute("list");
%>
<%
	response.setContentType("application/vnd.ms-excel;charset=UTF-8");
	String fileName = "复合统计";
	response.setHeader("Content-disposition", "attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%> 
<body>
	<table width="99%" border="0" cellpadding="3" cellspacing="1"bgcolor="#4c6e92" align="center">
	<tr bgcolor="#e8e8e8">
			<td><div align="center">所属仓</div>
			</td>
			<td><div align="center">&nbsp;时间</div>
			</td>
			<td><div align="center">订单数量</div>
			</td>
			<td><div align="center">商品数量</div>
			</td>
			<td><div align="center">SKU数量</div>
			</td>
		</tr>
		<%
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					CheckOrderStatJobBean bean = (CheckOrderStatJobBean) list.get(i);
		%>
		<tr bgcolor="#e8e8e8">
			<td><div align="center"><%if(bean.getArea()==-1){%>全部仓<%}else{%><%=ProductStockBean.areaMap.get(Integer.valueOf(bean.getArea()))%><%}%></div>
			</td>
			<td><div align="center">&nbsp;<%if(bean.getDate().length()==21){%><%=bean.getDate().substring(0, 10)%><%}else{%><%=bean.getDate()%><%}%></div>
			</td>
			<td><div align="center"><%=bean.getOrderCount()%></div>
			</td>
			<td><div align="center"><%=bean.getProductCount()%></div>
			</td>
			<td><div align="center"><%=bean.getSkuCount()%></div>
			</td>
		</tr>
		<%
			}
			}
		%>
	</table>
</body>
</html>