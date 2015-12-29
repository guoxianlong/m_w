<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%
StockAction action = new StockAction();
action.collectStockOperation(request, response);

String result = (String) request.getAttribute("result");
if("failure".equals(result)){
%>
<script>
alert("参数有误！");
history.back(-1);
</script>
<%
	return;
}
List operList = (List) request.getAttribute("operList");
List productList = (List) request.getAttribute("productList");
List inList = (List) request.getAttribute("inList");
List outList = (List) request.getAttribute("outList");
StockOperationBean oper = null;
StockHistoryBean sh = null;
voProduct product = null;
int i, count;
Iterator itr = null;
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
<p align="center">汇总 <input type="button" onclick="javascript:exportList();" value="导出列表"/></p>
<table width="100%" border="1" id="listTable">
<tr>
  <td colspan="5">操作名称：<%
count = operList.size();
for(i = 0; i < count; i ++){
	oper = (StockOperationBean) operList.get(i);
	if(i > 0){
		out.print(",");
	}
%><%=oper.getName()%><%
}
%>  
  </td>  
</tr>
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>产品原名称</td>
  <td>产品编号</td>
  <td>北京出库总量</td>
  <td>广东出库总量</td>
  <td>北京入库总量</td>
  <td>广东入库总量</td>  
</tr>
<%
count = productList.size();
int stockOutBj = 0;
int stockOutGd = 0;
int stockInBj = 0;
int stockInGd = 0;
StockHistoryBean outSh = null;
StockHistoryBean inSh = null;
for(i = 0; i < count; i ++){
	product = (voProduct) productList.get(i);
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
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getCode()%></a></td>  
  <td><%=stockOutBj%></td>
  <td><%=stockOutGd%></td>
  <td><%=stockInBj%></td>
  <td><%=stockInGd%></td>  
</tr>
<%
}
%>
</table>