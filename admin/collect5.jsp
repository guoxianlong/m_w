<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%
List productList = (List) request.getAttribute("productList");
List codeList = null;
Map orderMap = (Map) request.getAttribute("orderMap");
Map productMap = (Map) request.getAttribute("productMap");
voOrderProduct op = null;
voOrderProduct temp = null;

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
  if(r != 0 && c == objTbl.rows[r].cells.length - 1){
  	returnText += ";";
  }
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
<p align="center">缺货订单商品汇总 <input type="button" onclick="javascript:exportList();" value="导出列表"/></p>
<table width="100%" border="1" id="listTable">
<tr>
  <td>序号</td>
  <td>产品名称</td>
  <td>原名称</td>
  <td>产品编号</td>
  <td>产品二级分类</td>
  <td>供货商</td>
  <td>广速出库总量</td>
  <td>广速缺货量</td>
  <td>订单</td>
</tr>
<%
count = productList.size();
for(i = 0; i < count; i ++){
	op = (voOrderProduct) productList.get(i);
	codeList = (List)orderMap.get(Integer.valueOf(op.getProductId()));
	temp = (voOrderProduct) productMap.get(Integer.valueOf(op.getProductId()));
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><a href="./fproduct.do?id=<%=op.getProductId() %>" target="_blank"><%=op.getName() %></a></td>
  <td><a href="./fproduct.do?id=<%=op.getProductId() %>" target="_blank"><%=op.getOriname() %></a></td>
  <td><a href="./fproduct.do?id=<%=op.getProductId() %>" target="_blank"><%=op.getCode() %></a></td>
  <td><%= temp.getParentId2Name() %>&nbsp;</td>
  <td><%= temp.getProxyName() %></td>  
  <td><%= temp.getCount() %></td>
  <td style="color:red;"><%=temp.getCount() - (op.getStock(ProductStockBean.AREA_GS, 0))%></td>
  <td><%
int oCount = 0;
for(int j = 0; j < codeList.size(); j++){
	voOrder order = (voOrder) codeList.get(j);
%><a href="order.do?id=<%= order.getId() %>" target="_blank"><%= order.getCode() %></a><br/><%
}
%></td>
</tr>
<%
}
%>
</table>