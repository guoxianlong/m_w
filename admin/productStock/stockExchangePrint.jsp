<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*" %>
<%
ProductStockAction action = new ProductStockAction();
action.stockExchange(request, response);

List productList = (List) request.getAttribute("productList");
List inList = (List) request.getAttribute("inList");
List outList = (List) request.getAttribute("outList");
StockExchangeBean bean = (StockExchangeBean) request.getAttribute("bean");

int i, count;
voProduct product = null;
StockExchangeProductBean sh = null;
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
<tr>
  <td colspan="6">操作名称：<%=bean.getName()%></td>
</tr>
<tr>
  <td>序号</td>
  <td>产品编号</td>
  <td>产品名称</td>
  <td>原名称</td>  
<%
if(bean.getArea() == 0){
%>
  <td>北京出</td>
  <td>广东入</td>
<%
}
else {
%>
  <td>广东出</td>
  <td>北京入</td>
<%
}
%>  
</tr>
<%
count = productList.size();
int stockOutBj = 0;
int stockOutGd = 0;
int stockInBj = 0;
int stockInGd = 0;
StockExchangeProductBean outSh = null;
StockExchangeProductBean inSh = null;
for(i = 0; i < count; i ++){
	product = (voProduct) productList.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><%=product.getCode()%></td>
  <td><%=product.getName()%></td>
  <td><%=product.getOriname()%></td>  
<%
	stockOutBj = 0;
    stockOutGd = 0;
    stockInBj = 0;
    stockInGd = 0;
	outSh = null;
	inSh = null;
	itr = outList.iterator();
    while(itr.hasNext()){
		sh = (StockHistoryBean) itr.next();
		if(sh.getProductId() == product.getId()){
			stockOutBj = sh.getStockBj();
			stockOutGd = sh.getStockGd();
			outSh = sh;
		}
	}
	itr = inList.iterator();
    while(itr.hasNext()){
		sh = (StockHistoryBean) itr.next();
		if(sh.getProductId() == product.getId()){
			stockInBj = sh.getStockBj();
			stockInGd = sh.getStockGd();
			inSh = sh;
		}
	}
%>
<%
if(bean.getArea() == 0){
%>
  <td><%=stockOutBj%></td>  
  <td><%=stockInGd%></td>
<%
}
else {
%>
  <td><%=stockOutGd%></td>
  <td><%=stockInBj%></td>
<%
}
%>
</tr>
<%
}
%>
</table>
<script language="JavaScript">
	exportList();
	window.close();
</script>