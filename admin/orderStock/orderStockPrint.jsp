<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%
OrderStockAction action = new OrderStockAction();
action.orderStock(request, response);

List outList = (List) request.getAttribute("outList");
OrderStockBean bean = (OrderStockBean) request.getAttribute("bean");

int i, count;
voProduct product = null;
OrderStockProductBean sh = null;
Iterator itr = null;

int stockStatus = StringUtil.StringToId((String) request.getAttribute("stockStatus"));
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
<tr>
  <td colspan="5">操作名称：<%=bean.getName()%></td>
</tr>
<tr>
  <td>序号</td>
  <td>产品编号</td>
  <td>产品名称</td>
  <td>产品原名称</td>
  <td>出库量</td>
</tr>
<%
int stockOutCount = 0;
OrderStockProductBean outSh = null;
itr = outList.iterator();
i = 0;
while(itr.hasNext()){
	i++;
	sh = (OrderStockProductBean) itr.next();
	stockOutCount = sh.getStockoutCount();
	outSh = sh;
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getCode()%></a></td>
  <td><a href="productStockHistory.jsp?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=sh.getProduct().getId()%>"><%=sh.getProduct().getOriname()%></a></td>  
  <td><%=stockOutCount%></td>
 
</tr>
<%
}
%>
</table>
<script language="JavaScript">
	exportList();
	window.close();
</script>