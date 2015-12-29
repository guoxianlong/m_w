<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*" %>
<%
StockAction action = new StockAction();
action.stockAdminHistory(request, response);

List list = (List) request.getAttribute("list");
StockOperationBean bean = (StockOperationBean) request.getAttribute("bean");

int i, count;
StockAdminHistoryBean log = null;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<p align="center"><%=bean.getName()%></p>
<table width="100%" border="1">
<tr>
  <td>序号</td>
  <td>时间</td>
  <td>说明</td>
  <td>操作人员</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	log = (StockAdminHistoryBean) list.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><%=log.getOperDatetime()%></td>
  <td><font color="red"><%=log.getRemark()%></font></td>
  <td><%=log.getAdminName()%></td>
  <td>
</tr>
<%
}
%>
</table>