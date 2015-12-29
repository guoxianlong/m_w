<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.buy.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%
Stock2Action action = new Stock2Action();
action.collectBuyStock(request, response);

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
List stockList = (List) request.getAttribute("stockList");
List buyStockProductList = (List) request.getAttribute("buyStockProductList");
BuyStockBean stock = null;
BuyStockProductBean bsp = null;

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
  returnText   +=  objTbl.rows[r].cells[c].innerText.replace(/\r\n/g,';');
  returnText += "\t";   
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
<p align="center">采购进货汇总 <input type="button" onclick="javascript:exportList();" value="导出列表"/></p>
<table width="100%" border="1" id="listTable">
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>产品编号</td>
  <td>计划总量</td>
</tr>
<%
count = buyStockProductList.size();
for(i = 0; i < count; i ++){
	bsp = (BuyStockProductBean) buyStockProductList.get(i);
	if(bsp.getProduct() == null){
		continue;
	}
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="../fproduct.do?id=<%=bsp.getProduct().getId()%>"><%=bsp.getProduct().getName()%></a></td>
  <td><a href="../fproduct.do?id=<%=bsp.getProduct().getId()%>"><%=bsp.getProduct().getOriname()%></a></td>
  <td><a href="../fproduct.do?id=<%=bsp.getProduct().getId()%>"><%=bsp.getProduct().getCode()%></a></td>  
  <td><%=bsp.getPlanCount()%></td>
</tr>
<%
}
%>
</table>

<p align="center"><a href="buyPlanList.jsp">返回采购进货操作记录列表</a></p>