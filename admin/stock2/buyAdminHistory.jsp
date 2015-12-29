<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.buy.*,adultadmin.util.*, adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

// 没有权限，无法查看商品采购——人员操作记录
if(!group.isFlag(183)){
	return;
}

Stock2Action action = new Stock2Action();
action.buyAdminHistory(request, response);

List list = (List) request.getAttribute("list");
int logType = StringUtil.toInt((String)request.getAttribute("logType"));

int i, count;
BuyAdminHistoryBean log = null;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<p align="left">
<%if(logType == BuyAdminHistoryBean.LOGTYPE_BUY_PLAN){
	BuyPlanBean bean = (BuyPlanBean) request.getAttribute("bean");
%>
采购计划单&nbsp;<%=bean.getCode()%>&nbsp;操作日志
<%} else if(logType == BuyAdminHistoryBean.LOGTYPE_BUY_STOCK){
	BuyStockBean bean = (BuyStockBean) request.getAttribute("bean");
%>
预计到货表&nbsp;<%=bean.getCode()%>&nbsp;操作日志
<%} else if(logType == BuyAdminHistoryBean.LOGTYPE_BUY_ORDER){
	BuyOrderBean bean = (BuyOrderBean) request.getAttribute("bean");
%>
采购订单&nbsp;<%=bean.getCode()%>&nbsp;操作日志
<%} else if(logType == BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN){
	BuyStockinBean bean = (BuyStockinBean) request.getAttribute("bean");
%>
采购入库单&nbsp;<%=bean.getCode()%>&nbsp;操作日志
<%} %>
</p>
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
	log = (BuyAdminHistoryBean) list.get(i);
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