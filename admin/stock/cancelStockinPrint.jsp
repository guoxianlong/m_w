<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, adultadmin.util.StringUtil" %>
<%
StockAction action = new StockAction();
action.buyStockin(request, response);

List productList = (List) request.getAttribute("productList");
List inList = (List) request.getAttribute("inList");
StockOperationBean bean = (StockOperationBean) request.getAttribute("bean");

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
<tr>
  <td colspan="8">操作名称：<%=bean.getName()%></td>
</tr>
<tr>
  <td>序号</td>
  <td>产品编号</td>
  <td>产品名称</td>
  <td>原名称</td>  
<%
if(bean.getArea() == 0){
%>
  <td>北京入库量</td>
<%
}
else {
%>
  <td>广东入库量</td>
<%
}
%> 
  <td>单位</td>
  <td>单价</td>
  <td>总金额</td>
</tr>
<%
count = productList.size();
int stockInBj = 0;
int stockInGd = 0;
StockHistoryBean inSh = null;
for(i = 0; i < count; i ++){
	product = (voProduct) productList.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getCode()%></a></td>
  <td><a href="productStockHistory.jsp?id=<%=product.getId()%>"><%=product.getName()%></a></td>  
  <td><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getOriname()%></a></td>
<%
    stockInBj = 0;
    stockInGd = 0;
	inSh = null;
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
  <td><%=stockInBj%></td>
<%
}
else {
%>
  <td><%=stockInGd%></td>
<%
}
%>
  <td><%=product.getUnit()%></td>
  <td><%=StringUtil.formatFloat(product.getPrice3())%></td>
  <td><%=StringUtil.formatFloat((product.getPrice3() * (stockInBj + stockInGd)))%></td>
</tr>
<%
}
%>
</table>
<script language="JavaScript">
	exportList();
	window.close();
</script>