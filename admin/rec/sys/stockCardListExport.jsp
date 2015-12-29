<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

ProductStockAction action = new ProductStockAction();
action.stockCardListExport(request, response);

String result = (String) request.getAttribute("result");

List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");

int i, count;
StockCardBean bean = null;
voProduct product = (voProduct) request.getAttribute("product");

int stockType = StringUtil.toInt(request.getParameter("stockType"));
int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
String code = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("code")));
String productCode = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productCode")));
String productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
String productOriName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productOriName")));

productName = Encoder.decrypt(productName);//解码为中文
if(productName==null){//解码失败,表示已经为中文,则返回默认
	productName =StringUtil.dealParam(request.getParameter("productName"));//名称
}
if (productName==null) productName="";

productOriName = Encoder.decrypt(productOriName);//解码为中文
if(productOriName==null){//解码失败,表示已经为中文,则返回默认
	productOriName =StringUtil.dealParam(request.getParameter("productOriName"));//名称
}
if (productOriName==null) productOriName="";

String startDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("startDate")));
String endDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("endDate")));

response.setContentType("application/vnd.ms-excel;charset=gb2312");
String now = DateUtil.formatDate(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
String filename =  "进销存卡片"+now;
response.setHeader("Content-disposition","attachment; filename=\"" + new String(filename.getBytes("GBK"), "iso8859-1") + ".xls\"");
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312">
</head>
<body>
<table width="100%" border="1" border="1" bordercolor="#D8D8D5" style="border-collapse:collapse;">
<%if(product != null){ %>
<tr><td colspan="15" style="color:FF0000;">
产品编号：<%= product.getCode() %>&nbsp;&nbsp;
小店名称：<%= StringUtil.toWml(product.getName()) %>&nbsp;&nbsp;
原名称：<%= StringUtil.toWml(product.getOriname()) %>&nbsp;&nbsp;
状态：<%= product.getStatusName() %>
</td></tr>
<%} %>
<tr>
  <td>库类型</td>
  <td>库区域</td>
  <td>单据号</td>
  <td>来源</td>
  <td>时间</td>
  <td>入库数量</td>
<%if(group.isFlag(182)){ %>
  <td>入库金额</td>
<%} %>
  <td>出库数量</td>
<%if(group.isFlag(182)){ %>
  <td>出库金额</td>
<%} %>
  <td>当前结存</td>
  <td>本库区域总结存</td>
  <td>本库类总结存</td>
  <td>全库总结存</td>
<%if(group.isFlag(182)){ %>
  <td>库存单价</td>
  <td>结存总额</td>
<%} %>
</tr>
<%
if(list != null){
count = list.size();
for(i = 0; i < count; i ++){
	bean = (StockCardBean) list.get(i);
%>
<tr style="<%= (i%2==0)?"background-color:EEE9D9;":"" %>" >
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getStockTypeName(bean.getStockType()) %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":ProductStockBean.getAreaName(bean.getStockArea()) %></td>
  <td><%= bean.getCode() %></td>
  <td><%= bean.getCardTypeName() %></td>
  <td><%= StringUtil.cutString(bean.getCreateDatetime(), 19) %></td>
  <td><%= (bean.getStockInCount() > 0)?String.valueOf(bean.getStockInCount()):"-" %></td>
<%if(group.isFlag(182)){ %>
  <td><%= (bean.getStockInPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockInPriceSum()):"-" %></td>
<%} %>
  <td><%= (bean.getStockOutCount() > 0)?String.valueOf(bean.getStockOutCount()):"-" %></td>
<%if(group.isFlag(182)){ %>
  <td><%= (bean.getStockOutPriceSum() > 0)?StringUtil.formatDouble2(bean.getStockOutPriceSum()):"-" %></td>
<%} %>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getCurrentStock() %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getStockAllArea() %></td>
  <td><%= bean.getCardType() == StockCardBean.CARDTYPE_STOCKBATCHPRICE?"-":bean.getStockAllType() %></td>
  <td><%= bean.getAllStock() %></td>
<%if(group.isFlag(182)){ %>
  <td><%= bean.getStockPrice() %></td>
  <td><%= StringUtil.formatDouble2(bean.getAllStockPriceSum()) %></td>
<%} %>
</tr>
<%
}
%>
</table>
<%}%>
</body>