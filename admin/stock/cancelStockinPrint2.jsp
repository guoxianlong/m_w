<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%
StockAction action = new StockAction();
action.cancelStockinPrint2(request, response);

List productList = (List) request.getAttribute("productList");
List inList = (List) request.getAttribute("inList");
StockOperationBean bean = (StockOperationBean) request.getAttribute("bean");
OrderStockBean stockOutBean = (OrderStockBean) request.getAttribute("stockOutBean");

int i, count;
voProduct product = null;
StockHistoryBean sh = null;
Iterator itr = null;

boolean check = false;
if(request.getParameter("check") != null){
	check = true;
}
%>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script   language="JavaScript">   
  function   getTblText(objTbl)   
  {   
  if(!objTbl)   return   "";   
  if(objTbl.tagName   !=   "TABLE")   return   "";   
  var   returnText   =   "";   
  for(var   r=0;   r<objTbl.rows.length;   r++)   
  {   
  for(var   c=0;   c<objTbl.rows[r].cells.length;   c++)   
  {   
  returnText   +=   objTbl.rows[r].cells[c].innerText   +   "\t";   
  }   
  returnText   +=   "\n";   
  }   
  return   returnText;   
  }   
</script>
<script>
function exportList(){
	clipboardData.setData('text',getTblText(listTable));
	alert("列表内容已复制到剪贴板，粘贴到excel文件中即可。");
}
</script>

<p align="center">打印 <input type="button" onclick="javascript:exportList();" value="导出列表"/></p>

<table width="100%" border="1" id="listTable">
<%--
<tr>
  <td colspan="12">操作名称：<%=bean.getName()%></td>
</tr>
<tr>
  <td>序号</td>
  <td>退回日期</td>
  <td>订单号</td>
  <td>订货人</td>
  <td>包裹单号</td>
  <td>价格</td>
  <td>订单生成日期</td>
  <td>发货日期</td>
  <td>退回日期-发货日期</td>
  <td>物品</td>
  <td>数量</td>
  <td>备注</td>  
</tr>
--%>
<%
if(bean != null && stockOutBean != null){
count = productList.size();
int stockInBj = 0;
int stockInGd = 0;
StockHistoryBean inSh = null;
for(i = 0; i < count; i ++){
	product = (voProduct) productList.get(i);
	if(i==0){
%>
<tr>
  <td>1</td>
  <td><%= (bean.getLastOperTime() != null)?bean.getLastOperTime().substring(5, 11):"" %></td>
  <td><%= bean.getOrder().getCode() %></td>
  <td><%= bean.getOrder().getName() %></td>
  <td><%= (StringUtil.isNull(bean.getOrder().getPackageNum()))?"&nbsp;":bean.getOrder().getPackageNum() %></td>
  <td><%= bean.getOrder().getDprice() %></td>
  <%-- 
  <td><%= DateUtil.formatDate(bean.getOrder().getCreateDatetime(), DateUtil.normalTimeFormat).substring(5, 11) %></td>
  --%>
  <td><%= (stockOutBean != null && stockOutBean.getLastOperTime() != null)?stockOutBean.getLastOperTime().substring(5, 11):"" %></td>
  <td><%= (bean.getLastOperTime() != null && stockOutBean.getLastOperTime() != null)?DateUtil.daysBetween(DateUtil.parseDate(bean.getLastOperTime()), DateUtil.parseDate(stockOutBean.getLastOperTime())):0 %></td>
  <td><%=product.getOriname()%></td>
  <td><%=product.getBuyCount()%></td>
  <td><%= (StringUtil.isNull(bean.getRemark()))?"&nbsp;":bean.getRemark() %></td>
</tr>
<%} else { %>

<tr>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  <%-- 
  <td>&nbsp;</td>
  --%>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
  <td><%=product.getOriname()%></td>
  <td><%=product.getBuyCount()%></td>
  <td>&nbsp;</td>
</tr>
<%
}
}
}
%>
</table>
<script language="JavaScript">
	exportList();
	window.close();
</script>