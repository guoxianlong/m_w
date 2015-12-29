<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.*, adultadmin.util.*" %>
<%
StockAction action = new StockAction();
action.printLogList(request, response);

List list = (List) request.getAttribute("printLogList");
PagingBean paging = (PagingBean) request.getAttribute("paging");
StockOperationBean bean = (StockOperationBean)request.getAttribute("bean");

int i, count;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<p align="center">打印记录</p>
<table width="100%" border="1">
<tr>
  <td>序号</td>
  <td>名称</td>
  <td>打印时间</td>
  <td>打印人</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	PrintLogBean plBean = (PrintLogBean) list.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><%=bean.getName()%></a></td>
  <td><%=plBean.getCreateDatetime().substring(0, 16)%></td>
  <td><%=plBean.getUser().getUsername() %></td>
</tr>
<%
}
%>
</table>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>